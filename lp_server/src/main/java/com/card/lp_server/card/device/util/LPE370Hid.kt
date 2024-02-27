package com.card.lp_server.card.device.util

import android.util.Log
import com.card.lp_server.card.HIDCommunicationUtil
import com.card.lp_server.card.HIDCommunicationUtil.Companion.instance
import java.sql.DriverManager
import java.util.LinkedList

/**
 * LPE 370设备 操作
 */
class LPE370Hid private constructor() {
    private val buf = LinkedList<Byte>()

    /**
     * 370写
     *
     * @param data
     * @return
     */
    fun write(data: List<Byte>): Boolean {
        var lenData = data.size
        var pos = 0
        //不为0
        while (lenData != 0) {
            ThreadSleepUtil.sleep(1)
            //拆分成64位
            val lenPacket = if (lenData < REPORT_SIZE) lenData else REPORT_SIZE - REPORT_HEAD_SIZE
            //写入
            val detail = data.subList(pos, pos + lenPacket)
            if (writeReport(detail)) {
                //变值
                pos += lenPacket
                lenData -= lenPacket
            }
        }
        return true
    }

    /**
     * 读数据
     *
     * @param data
     * @param length
     * @return
     */
    fun read(data: MutableList<Byte>, length: Int): Boolean {
        // 清空传入的数据集合，以确保它是空的
        data.clear()
        // 记录开始时间，用于计算等待时间
        val startTime = System.currentTimeMillis()
        var elapsedTime: Long

        // 不断尝试读取数据，直到缓冲区中包含指定长度的数据或等待时间超过 1000 毫秒
        while (buf.size < length) {
            readReport()
            elapsedTime = System.currentTimeMillis() - startTime
            // 如果等待时间超过 1000 毫秒，则返回 false 表示读取失败
            if (elapsedTime >= 1000) {
                return false
            }
            // 使用 ThreadSleepUtil.sleep(1) 等待 1 毫秒，避免过于频繁地尝试读取数据
            ThreadSleepUtil.sleep(1)
        }

        // 从缓冲区中取出指定长度的数据并添加到传入的数据集合中
        for (i in 0 until length) {
            data.add(buf.removeFirst())
        }
        return true
    }

    private fun readReport(): Boolean {
        val data = ByteArray(REPORT_SIZE)
        val result = HIDCommunicationUtil.instance.readFromHID(data)
        if (result) {
            /**
             * 修改usb hid 包实际数据大小： 原首字节改为首两个字节，小端模式。例如数据实际长度为1，字节[0]=0x01,字节[1]=0x00
             */
            val length = ByteUtil.convertFromLittleEndian(data[0], data[1])
            //System.out.println("包实际数据大小 length:" + length);
            if (length <= REPORT_SIZE - REPORT_HEAD_SIZE) {
                for (i in REPORT_HEAD_SIZE until length + REPORT_HEAD_SIZE) {
                    try {
                        buf.addLast(data[i])
                    } catch (ex: IllegalStateException) {
                        DriverManager.println("queue.Full")
                        buf.removeFirst()
                    }
                }
            }
            return true
        }
        return false
    }

    fun readFpsReport(): List<Byte> {
        val detail: MutableList<Byte> = ArrayList()
        val data = ByteArray(REPORT_SIZE)
        val result = HIDCommunicationUtil.instance.readFromHID(data)
        if (result) {
            val length = ByteUtil.convertFromLittleEndian(data[0], data[1])
            if (length <= REPORT_SIZE - REPORT_HEAD_SIZE) {
                for (i in REPORT_HEAD_SIZE until length + REPORT_HEAD_SIZE) {
                    detail.add(data[i])
                }
            }
        }
        return detail
    }

    /**
     * 写支持的子方法，拼接byte
     *
     * @param data
     * @return
     */
    fun writeReport(data: List<Byte>): Boolean {
        return if (data.size >= REPORT_SIZE) {
            false
        } else {
            val dataSize = REPORT_SIZE
            // 创建一个字节数组来存储报告数据
            val packetData = ByteArray(dataSize)
            // 获取数据的大小，转换为小端模式
            val head = ByteUtil.convertToLittleEndian(data.size)
            // 将数据的大小写入报告数据的前两个字节
            // 低位字节
            // 高位字节
            packetData[0] = head[0]
            packetData[1] = head[1]

            // 将数据部分复制到报告数据中 // 减去两个字节的头部
            val dataLength = Math.min(data.size, dataSize - 2)
            for (i in 0 until dataLength) {
                packetData[i + 2] = data[i]
            }
            // 填充剩余的字节（如果有的话）为0
            for (i in dataLength + 2 until dataSize) {
                packetData[i] = 0
            }
            // 写入报告数据
            HIDCommunicationUtil.instance.writeToHID(packetData)
        }
    }

    companion object {
        //    private final int REPORT_SIZE = 64;
        /**
         * 修改usb hid 包大小： 64 改为1024
         */
        private const val REPORT_SIZE = 1024
        private const val REPORT_HEAD_SIZE = 2

        /*private final int REPORT_OVERTIME = 1;
    private final int FIFO_SIZE = 16777216;*/
        private const val TAG = "LPE370Hid"

        val instance by lazy {
            LPE370Hid()
        }
    }
}
