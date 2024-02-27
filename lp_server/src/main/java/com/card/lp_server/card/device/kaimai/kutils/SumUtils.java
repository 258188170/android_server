package com.card.lp_server.card.device.kaimai.kutils;

public class SumUtils {
    /**
     * 求字节数组的校验和
     * @param b 需要求校验和的字节数组
     * @return 校验和
     */
    public static short sumCheck(byte[] b) {

        int sum = 0;
        for (byte value : b) {
            //&上0xff 转换成无符号整型
            sum = sum + (value & 0xff);
        }
        return (short) (sum & 0xffff);
    }
    /**
     * 将short转为低字节在前，高字节在后的byte数组(小端)
     *
     * @param s short
     * @return byte[]
     */
    public static byte[] shortToByteLittle(short s) {

        byte[] b = new byte[2];
        b[0] = (byte) (s & 0xff);
        b[1] = (byte) (s >> 8 & 0xff);
        return b;
    }
}
