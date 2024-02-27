package com.card.lp_server.card.device;

import android.os.Build;

import com.card.lp_server.card.HIDCommunicationUtil;
import com.card.lp_server.card.device.util.ByteUtil;
import com.card.lp_server.card.device.util.CRC16;
import com.card.lp_server.card.device.util.CmdUtil;
import com.card.lp_server.card.device.util.LPE370Hid;
import com.card.lp_server.card.device.util.Talk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class XferFrame {

    // 同步帧

    private static final String TAG = "XferFrame";
    private static final int MAX_RETRY_COUNT = 5;

    private String type = null;
    private Integer num = null;
    private Object dir = null;
    private String cmdDir = null;
    private Integer size = null;
    private String crc = null;
    private final List<Byte> data = new ArrayList<Byte>();
    CRC16 crc16 = CRC16.getInstance();

    /**
     * 重置
     */
    private void reset() {
        type = null;
        num = null;
        dir = null;
        cmdDir = null;
        size = null;
        data.clear();
    }

    /**
     * 清空
     */
    private void clear() {
        type = null;
        num = null;
        dir = null;
        size = null;
        data.clear();
    }

    private boolean setType(Object type) {
        if (type instanceof Integer && CmdUtil.VALUE_TYPE.containsKey(type)) {
            this.type = CmdUtil.VALUE_TYPE.get(type);
            return true;
        } else if (type instanceof String && CmdUtil.TYPE_VALUE.containsKey(type)) {
            this.type = (String) type;
            return true;
        } else {
            this.type = null;
            return false;
        }
    }

    //read num
    private boolean setReadNum(int index, byte num, byte chk) {
        if (num == (~chk)) {
            this.num = (index + 1);
            return true;
        } else {
            this.num = null;
            return false;
        }
    }

    //write num
    private boolean setWriteNum(int index, byte num, byte chk) {
        if (num == (~chk)) {
            this.num = index;
            return true;
        } else {
            this.num = null;
            return false;
        }
    }


    public boolean setDir(Object dir) {
        if (dir instanceof Integer && CmdUtil.VALUE_DIR.containsKey(dir)) {
            this.dir = CmdUtil.VALUE_DIR.get(dir);
            return true;
        } else if (dir instanceof String && CmdUtil.DIR_VALUE.containsKey(dir)) {
            this.dir = dir;
            return true;
        } else {
            this.dir = null;
            return false;
        }
    }


    /**
     * 发送应答命令执行
     *
     * @param dir
     * @param cmd
     */
    public boolean sendCmdFrame(String dir, List<Byte> cmd) {
        reset();
        // 数据帧:序号
        num = 0;
        // 数据帧:方向
        if (!setDir(dir)) {
            return false;
        }
        cmdDir = dir;
        // 数据帧:长度,命令列表最后一个
        if (cmd.size() > CmdUtil.TYPE_DSIZE.get("SOH")) {
            return false;
        } else {
            setType("SOH");
            size = CmdUtil.TYPE_FSIZE.get("SOH");
        }

        //添加数据
        cmd.addAll(Collections.nCopies(CmdUtil.TYPE_DSIZE.get(type) - cmd.size(), (byte) 0));
        //System.out.println("cmd size:"+cmd.size());
        //1.长度标识
        this.data.add(Objects.requireNonNull(CmdUtil.TYPE_VALUE.get(type)).byteValue());
        //2.num 序号
        this.data.add(num.byteValue());
        //3~num 反序号
        this.data.add((byte) (~num & 0xff));
        //4.方向标识 命令标识 file -> DATA_CTL
        this.data.add(CmdUtil.DIR_VALUE.get("DATA_CTL").byteValue());
        //5.结束标识
        //System.out.println("dir:"+dir);
        byte endTag = "DATA_NONE".equals(dir) ? Objects.requireNonNull(CmdUtil.END_VALUE.get("EOT")).byteValue() : CmdUtil.END_VALUE.get("CTN").byteValue();
        //System.out.println("endTag:" + endTag);
        this.data.add(endTag);
        //6.CRC H
        byte[] cmdByte = ByteUtil.listToByte(cmd);
        //System.out.print("1.cmd 命令:");
        //ByteUtil.printByteArrayAsHex(cmdByte);
        crc16.calculateCRC(cmdByte);
        //System.out.println("2.cmd CRC 字节[0]: " + String.format("%02X", crc16.getCrcH()) + "  CRC 字节[1]: " + String.format("%02X", crc16.getCrcL()));
        this.data.add(crc16.getCrcH());
        //7 CRC l
        this.data.add(crc16.getCrcL());
        //this.data.add((byte) 0xFF);
        //7 CRC l
        //this.data.add((byte) 00);
        //8 ETX
        this.data.add(Objects.requireNonNull(CmdUtil.END_VALUE.get("ETX")).byteValue());

        this.data.addAll(cmd);
        //64
        //System.out.println("命令:" + data.size());
        //System.out.print("命令:");
        //ByteUtil.printByteArrayAsHex(ByteUtil.listToByte(data));
        LPE370Hid.getInstance().write(data);
        return true;
    }


    /**
     * 发送数据执行
     *
     * @param num
     * @param dir
     * @param data
     * @return /**
     */
    public boolean sendDataFrame(int num, String dir, byte[] data) {

        clear();
        // 数据帧:序号
        byte bNum = ((byte) (num & 0xff));
        byte cnkNum = ((byte) (~((num) & 0xff)));

        //System.out.println("index:" + num + "   bNum:" + bNum + "  cnkNum:" + cnkNum);
        if (!setWriteNum(num, bNum, cnkNum)) {
            return false;
        }
        // 数据帧:方向
        // 数据帧:类型,长度
        if (data.length > CmdUtil.TYPE_DSIZE.get("STX")) {
            return false;
        }

        if (data.length > CmdUtil.TYPE_DSIZE.get("SOH")) {
            setType("STX");
        } else {
            setType("SOH");
        }

        size = CmdUtil.TYPE_FSIZE.get(type);

        // 打包数据帧
        this.data.clear();

        //1.长度标识
        this.data.add(CmdUtil.TYPE_VALUE.get(type).byteValue());
        //2.num 序号
        this.data.add(bNum);
        //3.~num 反序号
        this.data.add(cnkNum);
        //4.方向标识 写入文件到存储
        this.data.add(CmdUtil.DIR_VALUE.get("DATA_IN").byteValue());
        //5.结束标识
        this.data.add("DATA_NONE".equals(dir) ? CmdUtil.DIR_VALUE.get("DATA_NONE").byteValue() : 0x00);
        //crc-------------------------------------------------
        crc16.calculateCRC(data);
        //6.CRC H
        this.data.add(crc16.getCrcH());
        //7 CRC L
        this.data.add(crc16.getCrcL());
        //8. tail
        this.data.add(CmdUtil.END_VALUE.get("ETX").byteValue());
        //数据
        List<Byte> byteList = new ArrayList<>();
        for (byte b : data) {
            byteList.add(b);
        }
        this.data.addAll(byteList);
        this.data.addAll(Collections.nCopies(CmdUtil.TYPE_DSIZE.get(type) - data.length, (byte) 0));
        //System.out.println("写数据:" + bytesToHexString(this.data));
        LPE370Hid.getInstance().write(this.data);
        return true;
    }


    /**
     * 读取数据执行
     *
     * @param num
     * @return
     */
    public byte[] readDataFrame(int num) {
        clear();
        // 获取初始的数据长度（SOH类型的数据帧大小）
        int dataLen = CmdUtil.TYPE_FSIZE.get("SOH");
        int left = 0;
        // 尝试多次读取数据帧，最多 MAX_RETRY_COUNT 次
        for (int i = 0; i < MAX_RETRY_COUNT; i++) {
            List<Byte> data = new ArrayList<Byte>();
            // 尝试从设备读取数据
            //if (!LPE370Hid.getInstance().read(data, dataLen)) {
            if (!checkReadData(data, dataLen)) {
                //System.out.println("i:" + i);
                continue;
            } else {
                //System.out.print("data 是有数据的:");
                //ByteUtil.printByteArrayAsHex(ByteUtil.listToByte(data));
                // 第一次读取数据时，设置帧格式信息，剔除头部数据
                if (size == null) {
                    // 解析数据帧的各个字段
                    if (!setType(data.get(0).intValue())) {
                        return null;
                    }

                    if (!setReadNum(num, data.get(1), data.get(2))) {
                        return null;
                    }

                    if (this.num != (num + 1)) {
                        return null;
                    }

                    if (!setDir(data.get(3).intValue())) {
                        return null;
                    }

                    // 判断是否为结束标识
                    if (Objects.requireNonNull(CmdUtil.END_VALUE.get("EOT")).byteValue() == data.get(4)) {
                        setDir(0x24);
                        head[4] = 0x24;
                    } else {
                        head[4] = 0x00;
                    }
                    setCrc(ByteUtil.bytesToHex(data.get(5), data.get(6)));
                    if (CmdUtil.END_VALUE.get("ETX").byteValue() != data.get(7)) {
                        return null;
                    }
                    // 获取数据帧的长度（根据类型动态变化）
                    size = CmdUtil.TYPE_FSIZE.get(type);
                    left = size - dataLen;
                    //1 num cnk 方向
                    head[0] = data.get(0);
                    head[1] = data.get(1);
                    head[2] = data.get(2);
                    head[3] = data.get(3);
                }

                this.data.addAll(data);
                // 处理 left 不等于 0 的情况
                if (left != 0) {
                    dataLen = left;
                    left = 0;
                } else {
                    // 构造并返回数据帧
                    byte[] frameData = new byte[this.data.size() - CmdUtil.HEAD_SIZE];
                    for (int j = 0; j < frameData.length; j++) {
                        frameData[j] = this.data.get(j + CmdUtil.HEAD_SIZE);
                    }
                    return frameData;
                }
            }
        }
        return null;
    }

    /**
     * @param data
     * @param dataLen
     * @return
     */
    private boolean checkReadData(List<Byte> data, int dataLen) {
        // 使用CompletableFuture来实现超时
        CompletableFuture<Boolean> future = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            future = CompletableFuture.supplyAsync(() -> {
                LPE370Hid.getInstance().read(data, dataLen);
                return true; // 读取成功时返回true
            });
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return future.get(1, TimeUnit.SECONDS);
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // 处理异常，例如记录日志
            //e.printStackTrace();
            return false;
        }
        return false;
    }


    /**
     * 发送应答帧 10次以后就退出 命令 1.命令 2.没有数据 2.带数据 DATA_IN DATA_OUT  写数据帧 接收来说 我给你应答   最后一帧 24   没有收到应答针  只有一个数据帧
     * 06 01 0F 00 1.
     *
     * @param rsp
     * @return
     */
    public boolean sendRspFrame(String rsp, byte[] head) {
        Integer rspValue = CmdUtil.RSP_VALUE.get(rsp);
        //ACK  长包 短包 收的序号 01 第二是序号的反码 下一位 class of 下一位 两位CRC ACK状态 00 00  是5位  CRC NAK ACK 00 00
        //我给他发送数据 你给我ACK会有超时 如果是ACK超时你就得重新读这个ACK 6 7如果 以10次循环
        //System.out.print("ack send start:");
        //ByteUtil.printByteArrayAsHex(head);
        byte[] rspFrame = new byte[]{rspValue.byteValue(), head[1], head[2], head[3], head[4], 0x00, 0x00, 0x03};
        boolean isSuccess = LPE370Hid.getInstance().write(ByteUtil.byteToList(rspFrame));
        //System.out.print("ack send end:"+isSuccess);
        return isSuccess;
    }

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    // 最大重试次数
    private final int maxRetries = 1;
    // 超时时间1秒
    private final int timeoutInSeconds = 1;

    public Talk recvRspFrame() {
        for (int retry = 0; retry < maxRetries; retry++) {
            data.clear();
            Future<Boolean> future = executorService.submit(() -> LPE370Hid.getInstance().read(data, 8));
            try {
                Boolean result = future.get(timeoutInSeconds, TimeUnit.SECONDS);
//                ByteUtil.printByteArrayAsHex(ByteUtil.listToByte(data));
                if (result != null && result) {
                    Talk talk = new Talk();
                    // 获取状态值 CRC位置里面填的是00 如果是负的就是超时 回到00  发下一珍 必须在一秒 5MB 发 connect
                    // 3MB 读 切分  2.read 3.时间 6 开始  累加  500KB 3MB
                    //System.out.println(data.get(0).intValue());
                    if (CmdUtil.RSP_VALUE.containsValue(data.get(0).intValue())) {
                        String rspString = CmdUtil.VALUE_RSP.get(data.get(0).intValue());
                        talk.setStart(rspString);
                    }

                    return talk;
                }
            } catch (TimeoutException e) {
                //取消任务
                future.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public String getType() {
        return type;
    }

    public Integer getNum() {
        return num;
    }

    public Object getDir() {
        return dir;
    }


    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    byte[] head = new byte[5];

    public byte[] getHead() {
        return head;
    }
}
