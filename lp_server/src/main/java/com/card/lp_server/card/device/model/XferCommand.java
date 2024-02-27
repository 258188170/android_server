package com.card.lp_server.card.device.model;

import android.util.Log;


import com.card.lp_server.card.device.util.ByteUtil;
import com.card.lp_server.card.device.util.CmdUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 命令对象处理、写读数据拆分
 *
 * @author ma_zhe
 */
public class XferCommand {
    private static final String TAG = "XferCommand";
    private Command command;
    private int currentLen;
    private int currentPos;


    /**
     * 将命令对象设置进来进行预处理
     */
    public void setCmd(Command command) {
        if (command == null) {
            throw new AssertionError();
        }
        this.command = command;

        if (!(command.getCmdName() != null && CmdUtil.MODE.containsKey(command.getModel()))) {
            throw new AssertionError();
        }

        String model = command.getModel();
        //根据读 写 来赋予初始值和偏移量
        if (CmdUtil.WRITE.equals(model)) {
            if (command.getFileName() != null && command.getLength() >= 0) {
                currentPos = 0;
                currentLen = 0;
            }
        } else if (CmdUtil.READ.equals(model)) {
            if (command.getFileName() != null) {
                currentPos = command.getOffset();
                currentLen = 0;
            }
        } else {
            currentPos = 0;
            currentLen = 0;
        }
    }

    /**
     * 将命令对象转成byte集合
     */
    public List<Byte> getCmd() {
        List<Byte> cmd = new ArrayList<Byte>();
        if (command.getCmdName() != null) {
            byte[] byteArray = command.getCmdName().getBytes();
            for (byte b : byteArray) {
                cmd.add(b);
            }
            cmd.add((byte) 0);
        }
        if (command.getFileName() != null) {
            byte[] byteArray = command.getFileName().getBytes();
            for (byte b : byteArray) {
                cmd.add(b);
            }
            cmd.add((byte) 0);
        }

        if (command.getYear() != null) {
            byte[] byteArray = command.getYear().getBytes();
            for (byte b : byteArray) {
                cmd.add(b);
            }
            cmd.add((byte) 0);
        }
        if (command.getMonth() != null) {
            byte[] byteArray = command.getMonth().getBytes();
            for (byte b : byteArray) {
                cmd.add(b);
            }
            cmd.add((byte) 0);
        }
        if (command.getDay() != null) {
            byte[] byteArray = command.getDay().getBytes();
            for (byte b : byteArray) {
                cmd.add(b);
            }
            cmd.add((byte) 0);
        }
        if (command.getHour() != null) {
            byte[] byteArray = command.getHour().getBytes();
            for (byte b : byteArray) {
                cmd.add(b);
            }
            cmd.add((byte) 0);
        }
        if (command.getMin() != null) {
            byte[] byteArray = command.getMin().getBytes();
            for (byte b : byteArray) {
                cmd.add(b);
            }
            cmd.add((byte) 0);
        }
        if (command.getSecond() != null) {
            byte[] byteArray = command.getSecond().getBytes();
            for (byte b : byteArray) {
                cmd.add(b);
            }
            cmd.add((byte) 0);
        }
        //``````````````````````````````````````````````````````````````````````

        if (command.getOffset() != null) {
            byte[] byteArray = String.valueOf((int)command.getOffset()).getBytes();
            for (byte b : byteArray) {
                cmd.add(b);
            }
            cmd.add((byte) 0);
        }
        if (command.getLength() != null) {
            byte[] byteArray = String.valueOf((int)command.getLength()).getBytes();
            for (byte b : byteArray) {
                cmd.add(b);
            }
            cmd.add((byte) 0);
        }
        return cmd;
    }

    /**
     * 获取拆分后的需要写入设备的byte数组
     *
     */
    public Pair<byte[], Boolean> getData() {
        //1.判断不是write不处理
        if (!CmdUtil.WRITE.equals(command.getModel())) {
            throw new AssertionError();
        }
        //2.小于0判断 第一次默认是0
        int dataLen = command.getLength() - currentLen;
        if (dataLen < 0) {
            throw new AssertionError();
        }

        //3.如果长度大于 长的定长
        int maxDataLen = CmdUtil.DATA_SIZE.get("L");
        int minDataLen = CmdUtil.DATA_SIZE.get("S");

        if (dataLen > maxDataLen) {
            dataLen = maxDataLen;
        } else if (dataLen > minDataLen) {
            dataLen = minDataLen;
        }

        //计算结束位置= 当前长度+ 上一次的长度
        int endPos = dataLen + currentLen;

        //赋值 currentPos 默认是从0开始  endLen
        byte[] data = Arrays.copyOfRange(command.getBytes(), currentPos, endPos);
        //LoggerUtil.info(logger, "拆分后的da长度:" + data.length);

        //切分成功
        if (data.length == dataLen) {
            //计算新的起始位置 + 切分长度
            currentPos = currentPos + dataLen;
            //计算新的当前位置 + 切分长度 为了
            currentLen = currentLen + dataLen;
        }

        if (command.getLength() == currentLen) {
            //LoggerUtil.info(logger, "getData: 数据传输已结束");
            return new Pair<byte[], Boolean>(data, true);
        } else {
            //LoggerUtil.info(logger, "getData: 数据传输未结束");
            return new Pair<byte[], Boolean>(data, false);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------

    /**
     * 获取拆分后的需要读的byte集合
     *
     * @param data
     */
    public void setData(List<Byte> data) {
        if (!CmdUtil.READ.equals(command.getModel())) {
            throw new AssertionError();
        }
        //getLength 获取是文件最长度-上一次文件长度
        int dataLen = command.getLength() - currentLen;
        if (dataLen <= 0) {
            Log.d(TAG, "setData: set_data error, data_len is " + dataLen);
            return;
        }

        if (data.size() > dataLen) {
            data = data.subList(0, dataLen);
        }

        appendData(data);
    }

    /**
     * 设置要读的byte数组以及改变 读的前后指针
     *
     */
    private void appendData(List<Byte> data) {
        try {
            int dataLen = data.size();
            command.setBytes(ByteUtil.listToByte(data));
            currentPos += dataLen;
            //累加currentLen 长度
            currentLen += dataLen;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getModel() {
        return command.getModel();
    }

    public Command getCommand() {
        return command;
    }
}
