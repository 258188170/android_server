package com.card.lp_server.card.device;


import com.card.lp_server.card.device.model.Command;
import com.card.lp_server.card.device.model.Pair;
import com.card.lp_server.card.device.util.ByteUtil;
import com.card.lp_server.card.device.util.CmdUtil;
import com.card.lp_server.card.device.util.LPE370Hid;
import com.card.lp_server.card.device.util.Talk;
import com.card.lp_server.card.device.util.ThreadSleepUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Fps read write 逻辑处理
 */
public class FpsService {


    private static final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static Pair<Boolean, byte[]> signReadFps(String name, Integer len) throws InterruptedException, ExecutionException {
        Command readCommand = CmdUtil.cmdMap.get("CMD_READ_FPS");
        readCommand.setOffset(len);
        readCommand.setFileName(name);
        readCommand.clearBytes();
        Callable<Pair<Boolean, byte[]>> task = () -> FpsService.readFps(readCommand);
        Future<Pair<Boolean, byte[]>> future = executorService.submit(task);
        return future.get();
    }

    /**
     * 1读 FPS
     *
     * @param command
     * @return
     */
    public static Pair<Boolean, byte[]> readFps(Command command) throws Exception {
        boolean isCheck = runCmd(command);
        if (!isCheck) {
            return new Pair(false, "检查数据执行错误".getBytes());
        }
        ThreadSleepUtil.sleep(1);
        return new Pair(true, runRedFps(command.getOffset()));
    }

    /**
     * 读Fps数据
     *
     * @param length
     * @return
     */
    public static byte[] runRedFps(int length) throws Exception {
        List<Future<List<Byte>>> futures = new ArrayList<>();

        while (length > 0) {
            Callable<List<Byte>> task = () -> LPE370Hid.getInstance().readFpsReport();
            Future<List<Byte>> future = executorService.submit(task);
            futures.add(future);
            length -= future.get().size();
        }

        List<Byte> allData = futures.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return ByteUtil.listToByte(allData);
    }


    /**
     * 检查应答命令
     *
     * @param frame
     * @param cmdDir
     * @param cmd
     * @return
     */
    private static boolean checkCmd(XferFrame frame, String cmdDir, List<Byte> cmd) {
        Talk talk = frame.recvRspFrame();
        if (null == talk) {
            return false;
        }
        //如果是NAK重新发命令
        if (CmdUtil.NAK.equals(talk.getStart())) {
            frame.sendCmdFrame(cmdDir, cmd);
            talk = frame.recvRspFrame();
        }
        if (CmdUtil.ACK.equals(talk.getStart())) {
            return true;
        }
        return false;
    }

    /**
     * 写 Fps
     *
     * @param command
     * @return
     */
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public static Pair<Boolean, byte[]> writeFps(Command command) throws Exception {
        boolean isCheck = runCmd(command);
        if (!isCheck) {
            return new Pair(false, "检查数据执行错误".getBytes());
        }

        long startTime = System.currentTimeMillis();
        boolean isWrite = runWriteDataFps(command.getBytes());
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // 计算每秒传输多少MB
        double seconds = (double) executionTime / 1000;
        double dataSizeMB = 3;
        double transferRateMBPerSecond = dataSizeMB / seconds;

        // 格式化结果，保留两位小数
        String formattedResult = decimalFormat.format(transferRateMBPerSecond);

        return new Pair(isWrite, ("写传输速率：" + formattedResult + "MB/s").getBytes());
    }

    /**
     * 执行命令
     *
     * @param command
     * @return
     */
    private static boolean runCmd(Command command) {
        List<Byte> cmdByte = CmdUtil.cmdToByte(command);
        //1.执行命令
        XferFrame frame = new XferFrame();
        String cmdDir = CmdUtil.MODE_DIR.get(command.getModel());
        frame.sendCmdFrame(cmdDir, cmdByte);
        //检查ACK
        ThreadSleepUtil.sleep(1);
        return checkCmd(frame, cmdDir, cmdByte);
    }


    private static boolean runWriteDataFps(byte[] data) {
        Integer s = 1022;
        Integer l = 1022;
        //2.拆分数据
        List<byte[]> dataList = ByteUtil.splitData(data, s, l);
        //并行写入数据块
        dataList.parallelStream().forEach(detail -> LPE370Hid.getInstance().writeReport(ByteUtil.byteToList(detail)));
        return true;
    }


}
