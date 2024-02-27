package com.card.lp_server.card.device.util;

import static java.sql.DriverManager.println;

import android.util.Log;

import com.card.lp_server.card.HIDCommunicationUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * LPE 370设备 操作
 */
public class LPE370Hid {

//    private final int REPORT_SIZE = 64;
    /**
     * 修改usb hid 包大小： 64 改为1024
     */
    private static final int REPORT_SIZE = 1024;

    private static final int REPORT_HEAD_SIZE = 2;

    /*private final int REPORT_OVERTIME = 1;
    private final int FIFO_SIZE = 16777216;*/
    private static final String TAG = "LPE370Hid";
    private boolean isOpen = true;

    private LinkedList<Byte> buf = new LinkedList<Byte>();

    private LPE370Hid() {

    }

    // 使用静态内部类创建单例实例
    private static class SingletonHolder {
        private static final LPE370Hid INSTANCE = new LPE370Hid();
    }

    // 提供全局访问点，返回单例实例
    public static LPE370Hid getInstance() {
        return SingletonHolder.INSTANCE;
    }


    /**
     * 关闭
     */
    public void close() {
        if (isOpen) {
            HIDCommunicationUtil.Companion.getInstance().closeUSBConnection();
            isOpen = false;
            Log.d(TAG, "Close LPE370");
        }
    }

    /**
     * 取消连接
     */
    public void disconnect() {
        if (isOpen) {
//            HidUtils.disconnect();
            isOpen = false;
        }
        Log.d(TAG, "Disconnect LPE370");
    }

    /**
     * 370写
     *
     * @param data
     * @return
     */
    public boolean write(List<Byte> data) {
        if (!isOpen) {
            return false;
        }
        int lenData = data.size();
        int pos = 0;
        //不为0
        while (lenData != 0) {
            ThreadSleepUtil.sleep(1);
            //拆分成64位
            int lenPacket = lenData < REPORT_SIZE ? lenData : REPORT_SIZE - REPORT_HEAD_SIZE;
            //写入
            List<Byte> detail = data.subList(pos, pos + lenPacket);
            if (writeReport(detail)) {
                //变值
                pos += lenPacket;
                lenData -= lenPacket;
            }
        }
        return true;
    }

    /**
     * 读数据
     *
     * @param data
     * @param length
     * @return
     */
    public boolean read(List<Byte> data, int length) {
        // 检查连接是否已打开，如果未打开则返回 false
        if (!isOpen) {
            return false;
        }
        // 清空传入的数据集合，以确保它是空的
        data.clear();

        // 记录开始时间，用于计算等待时间
        long startTime = System.currentTimeMillis();
        long elapsedTime;

        // 不断尝试读取数据，直到缓冲区中包含指定长度的数据或等待时间超过 1000 毫秒
        while (buf.size() < length) {
            readReport();
            elapsedTime = System.currentTimeMillis() - startTime;
            // 如果等待时间超过 1000 毫秒，则返回 false 表示读取失败
            if (elapsedTime >= 1000) {
                return false;
            }
            // 使用 ThreadSleepUtil.sleep(1) 等待 1 毫秒，避免过于频繁地尝试读取数据
            ThreadSleepUtil.sleep(1);
        }

        // 从缓冲区中取出指定长度的数据并添加到传入的数据集合中
        for (int i = 0; i < length; i++) {
            data.add(buf.removeFirst());
        }

        return true;
    }


    private boolean readReport() {
        byte[] data = new byte[REPORT_SIZE];

        boolean result = HIDCommunicationUtil.Companion.getInstance().readFromHID(data);
        if (result) {
            /**
             * 修改usb hid 包实际数据大小： 原首字节改为首两个字节，小端模式。例如数据实际长度为1，字节[0]=0x01,字节[1]=0x00
             */
            int length = ByteUtil.convertFromLittleEndian(data[0], data[1]);
            //System.out.println("包实际数据大小 length:" + length);
            if (length <= REPORT_SIZE - REPORT_HEAD_SIZE) {
                for (int i = REPORT_HEAD_SIZE; i < length + REPORT_HEAD_SIZE; i++) {
                    try {
                        buf.addLast(data[i]);
                    } catch (IllegalStateException ex) {
                        println("queue.Full");
                        buf.removeFirst();
                    }
                }
            }
            return true;
        }
        return false;
    }

    public List<Byte> readFpsReport() {
        List<Byte> detail = new ArrayList<>();
        byte[] data = new byte[REPORT_SIZE];
        boolean result = HIDCommunicationUtil.Companion.getInstance().readFromHID(data);
        ;
        if (result) {
            int length = ByteUtil.convertFromLittleEndian(data[0], data[1]);
            if (length <= REPORT_SIZE - REPORT_HEAD_SIZE) {
                for (int i = REPORT_HEAD_SIZE; i < length + REPORT_HEAD_SIZE; i++) {
                    detail.add(data[i]);
                }
            }
        }
        return detail;
    }


    /**
     * 写支持的子方法，拼接byte
     *
     * @param data
     * @return
     */
    public boolean writeReport(List<Byte> data) {
        if (data.size() >= REPORT_SIZE) {
            return false;
        } else {
            int dataSize = REPORT_SIZE;
            // 创建一个字节数组来存储报告数据
            byte[] packetData = new byte[dataSize];
            // 获取数据的大小，转换为小端模式
            byte[] head = ByteUtil.convertToLittleEndian(data.size());
            // 将数据的大小写入报告数据的前两个字节
            // 低位字节
            // 高位字节
            packetData[0] = head[0];
            packetData[1] = head[1];

            // 将数据部分复制到报告数据中 // 减去两个字节的头部
            int dataLength = Math.min(data.size(), dataSize - 2);
            for (int i = 0; i < dataLength; i++) {
                packetData[i + 2] = data.get(i);
            }
            // 填充剩余的字节（如果有的话）为0
            for (int i = dataLength + 2; i < dataSize; i++) {
                packetData[i] = 0;
            }
            // 写入报告数据

            return HIDCommunicationUtil.Companion.getInstance().writeToHID(packetData);
        }
    }


}
