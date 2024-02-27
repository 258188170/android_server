package com.card.lp_server.card.device;

import android.util.Log;


import com.card.lp_server.card.device.model.XferCommand;
import com.card.lp_server.card.device.util.ByteUtil;
import com.card.lp_server.card.device.util.CRC16;
import com.card.lp_server.card.device.util.CmdUtil;
import com.card.lp_server.card.device.util.Talk;
import com.card.lp_server.card.device.util.ThreadSleepUtil;

import java.util.List;

public class LPE370Xfer {
    private static final String TAG = "LPE370Xfer";
    private XferCommand cmd = new XferCommand();
    private String cmdDir = null;
    private int num = 0;

    CRC16 crc16 = CRC16.getInstance();


    /**
     * 执行应答命令
     *
     * @param frame
     * @param cmd
     * @return
     */
    private boolean sendCmdPhase(XferFrame frame, XferCommand cmd) {
        //1.发送应答命令
        boolean isCmd = frame.sendCmdFrame(cmdDir, cmd.getCmd());
        ByteUtil.sleep(10);
        //2.检查命令
        System.out.println("check start:---------------------------");
        return checkCmd(frame, isCmd);

    }

    /**
     * 检查应答命令
     *
     * @param frame
     * @param isCmd
     * @return
     */
    private boolean checkCmd(XferFrame frame, boolean isCmd) {
        if (isCmd) {
            Talk talk = frame.recvRspFrame();
            if (null == talk) {
                return false;
            }
            //如果是NAK重新发命令
            if (CmdUtil.NAK.equals(talk.getStart())) {
                frame.sendCmdFrame(cmdDir, cmd.getCmd());
                talk = frame.recvRspFrame();
            }
            if (CmdUtil.ACK.equals(talk.getStart())) {
                num = (frame.getNum() != null) ? frame.getNum() : 0;
                cmd.getCommand().setCmdStates(true);
                return true;
            }
        }
        return isCmd;
    }

    /**
     * 执行写入命令
     *
     * @param frame
     * @param cmd
     * @return /**
     */
    private boolean writeDataPhase(XferFrame frame, XferCommand cmd) {
        //1.准备发送数据
        byte[] data = cmd.getCommand().getBytes();
        Integer s = CmdUtil.DATA_SIZE.get("S");
        Integer l = CmdUtil.DATA_SIZE.get("L");
        //2.拆分数据
        this.num = 1;
        List<byte[]> dataList = ByteUtil.splitData(data, s, l);
        //3.写入数据
        boolean isWrite = loopWrite(frame, dataList);
        //4.检查写入
        return isWrite;

    }

    private boolean loopWrite(XferFrame frame, List<byte[]> dataList) {
        for (int i = this.num - 1; i < dataList.size(); i++) {
            byte[] detail = dataList.get(i);
            frame.sendDataFrame(num, (i == dataList.size() - 1) ? "DATA_NONE" : "DATA_IN", detail);
            //容错
            if (checkSuccess(frame)) {
                this.num += 1;
            } else {
                ThreadSleepUtil.sleep(100);
                return false;
            }
        }
        return true;
    }


    //如果是NAK重新发命令 就是超时他在处理
    private boolean checkSuccess(XferFrame frame) {
        Talk talk = frame.recvRspFrame();
        if (null == talk) {
            return false;
        }
        if (CmdUtil.NAK.equals(talk.getStart())) {
            return false;
        }
        if (CmdUtil.ACK.equals(talk.getStart())) {
            return true;
        }
        return false;
    }


    /**
     * 执行读的命令
     *
     * @param frame
     * @param cmd
     * @return
     */
    private boolean readDataPhase(XferFrame frame, XferCommand cmd) {
        this.num = 0;
        boolean isRead = loopRead(frame, cmd, 0);
        return isRead;
    }


    /**
     * 循环从存储读数据
     *
     * @param frame
     * @param cmd
     * @return
     */
    private static Byte MAX_ERROR_RED_SIZE = 2;

    private boolean loopRead(XferFrame frame, XferCommand cmd, int nakCount) {
        // 设置最大允许的错误次数
        //这个是一直在循环呢
        isWhile:
        while (nakCount < MAX_ERROR_RED_SIZE) {
            // 从数据帧中获取数据
            byte[] data = frame.readDataFrame(this.num);
            byte[] head = frame.head;
            // 检查获取到的数据是否非空 4个0也是哈
            if (ByteUtil.isNonEmpty(data) && checkCrc(data, frame)) {
                //如果获取到有效数据，则将数据设置到命令对象中 这个重新实现哈
                cmd.setData(ByteUtil.byteToList(data));
                //allData.add(data);
                // 发送 ACK 以表示已成功接收数据
                frame.sendRspFrame(CmdUtil.ACK, head);
                // 如果数据读取完毕，则结束循环
                if (CmdUtil.DATA_NONE.equals(frame.getDir())) {
                    //cmd.getCommand().clearBytes();
                    //cmd.getCommand().bytes=ByteUtil.convertListToByteArray(allData);
                    return true;
                }
                // 否则继续递归调用 loopRead（更新 this.num）
                this.num = frame.getNum() != null ? frame.getNum() : 0;
            } else {
                //如果超时就会来这里呢 无论是否有数据我返回ack是必须要的
                //System.out.println("---------------来了没有:" );
                //ByteUtil.printByteArrayAsHex(head);
                frame.sendRspFrame(CmdUtil.ACK, head);
                if (CmdUtil.DATA_NONE.equals(frame.getDir())) {
                    return true;
                }
                // 未成功获取数据，发送 NAK 并增加错误计数 head其实也是没有的哈
                //byte[] head = new byte[]{0x01, 0x00, (byte) 0xFF, 0x11, 0x24};
                //0x15  0x01 0x00 0xFF 0x11 0x24 0x00 0x00

                //break isWhile;
                nakCount++;
            }
        }
        // 达到最大错误次数后，返回 false 表示失败
        return false;
    }

    private boolean checkCrc(byte[] data, XferFrame frame) {
        crc16.calculateCRC(data);
        String createCrc = ByteUtil.bytesToHex(crc16.getCrcH(), crc16.getCrcL());
        return createCrc.equals(frame.crc);
    }


    /**
     * 执行命令集入口
     */
    private boolean xferThread() {
        XferFrame frame = new XferFrame();
        //命令调用
        if (!sendCmdPhase(frame, cmd)) {
            Log.d(TAG, "xferThread: " + cmd.getCommand().getCmdName() + "指令正在执行中，请耐心等待");
            return false;
        }
        //System.out.println("-------------命令结束-------------------------");
        //读数据调用
        if (CmdUtil.READ.equals(cmd.getModel())) {
            System.out.println("读开始--------------------------------------");
            if (!readDataPhase(frame, cmd)) {
                Log.d(TAG, "xferThread: " + "执行读数据出错，请检查读取长度");
                return false;
            }
        }
        ThreadSleepUtil.sleep(4);
        //写数据调用
        if (CmdUtil.WRITE.equals(cmd.getModel())) {
            System.out.println("写开始--------------------------------------");
            if (!writeDataPhase(frame, cmd)) {
                //超时写入会执行
                Log.d(TAG, "xferThread: " + "Write operation timed out");
                return false;
            }
        }

        return true;
    }

    /**
     * 执行命令调用
     *
     * @param cmd
     */
    public boolean run(XferCommand cmd) {
        this.cmd = cmd;
        this.cmdDir = CmdUtil.MODE_DIR.get(this.cmd.getModel());
        return xferThread();
    }


}
