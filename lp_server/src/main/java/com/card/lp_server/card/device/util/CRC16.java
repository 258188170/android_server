package com.card.lp_server.card.device.util;

/**
 * @author ma_zhe
 */
public class CRC16 {

    // CRC16算法参数
    private static final int WIDTH = 16;
    private static final int POLYNOMIAL = 0x1021;
    private static final int INIT_VALUE = 0xFFFF;
    private static final int FINAL_XOR_VALUE = 0xFFFF;
    private static final boolean REVERSE_INPUT = true;
    private static final boolean REVERSE_OUTPUT = true; // 输出值是否翻转
    private static final boolean REVERSE_ORDER = false;

    // CRC H和CRC L的结果
    private byte crcH;
    private byte crcL;

    private static CRC16 instance = new CRC16();

    // 构造函数，初始化CRC H和CRC L
    private CRC16() {
        crcH = (byte) 0xFF;
        crcL = 0x00;
    }

    public static CRC16 getInstance() {
        return instance;
    }


    // 计算CRC16校验码，并更新CRC H和CRC L的值
    public void calculateCRC(byte[] data) {
        int crc = INIT_VALUE;

        for (byte b : data) {
            // 如果输入值翻转，则反转每个字节
            int currentByte = REVERSE_INPUT ? reverseByte(b) : (b & 0xFF);

            // 将当前字节与CRC寄存器高8位异或
            crc ^= (currentByte << (WIDTH - 8));
            for (int i = 0; i < 8; i++) {
                // 判断最高位，若为1则左移一位并与多项式异或
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ POLYNOMIAL;
                } else {
                    crc <<= 1;
                }
            }
        }

        // 异或结果与结果异或值异或
        crc ^= FINAL_XOR_VALUE;

        if (REVERSE_OUTPUT) {
            // 如果输出值翻转，反转整个CRC值
            crc = reverseInt(crc);
        }

        // 将CRC结果分成高位和低位
        crcH = (byte) ((crc >> 8) & 0xFF);
        crcL = (byte) (crc & 0xFF);

        if (REVERSE_ORDER) {
            // 如果需要反转输出字节顺序，反转字节顺序
            crcH = reverseByte(crcH);
            crcL = reverseByte(crcL);
        }
    }

    // 获取CRC H的值
    public byte getCrcH() {
        return crcH;
    }

    // 获取CRC L的值
    public byte getCrcL() {
        return crcL;
    }

    private static int reverseInt(int value) {
        int result = 0;
        for (int i = 0; i < WIDTH; i++) {
            result = (result << 1) | ((value >> i) & 0x01);
        }
        return result;
    }

    private static byte reverseByte(byte value) {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            result = (result << 1) | ((value >> i) & 0x01);
        }
        return (byte) result;
    }

    public static void main(String[] args) {
        CRC16 calculator = CRC16.getInstance();
        byte[] input = {(byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x90};
        // 计算CRC16校验码
        calculator.calculateCRC(input);
        // 获取CRC H和CRC L的值
        byte crcH = calculator.getCrcH();
        byte crcL = calculator.getCrcL();
        // 输出结果
        System.out.println("CRC H: " + String.format("%02X", crcH));
        System.out.println("CRC L: " + String.format("%02X", crcL));
    }
}
