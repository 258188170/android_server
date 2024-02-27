package com.card.lp_server.card.device.util;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * byte数组操作
 *
 * @author gaowei
 */
public class ByteUtil {


    public static int arrayToInt(byte[] bytes) {
        if (null == bytes) {
            return 0;
        }
        int res = 0;
        for (int i = 0; i < bytes.length; i++) {
            res += (bytes[i] & 0xff) << i * 8;
        }
        return res;
    }

    public static List<Byte> byteToList(byte[] data, int startIndex) {
        List<Byte> byteList = new ArrayList<Byte>();
        for (int i = startIndex; i < data.length; i++) {
            byteList.add(data[i]);
        }
        return byteList;
    }

    public static byte[] intToByteArray(int value) {
        byte[] byteArray = new byte[4];
        byteArray[0] = (byte) ((value >> 24) & 0xFF);
        byteArray[1] = (byte) ((value >> 16) & 0xFF);
        byteArray[2] = (byte) ((value >> 8) & 0xFF);
        byteArray[3] = (byte) (value & 0xFF);
        return byteArray;
    }


    public static String convertToString(List<Byte> byteList) {
        byte[] byteArray = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            byteArray[i] = byteList.get(i);
        }
        return new String(byteArray);
    }

    public static byte[] readFileBytes(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return null;
        }

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputStream.read(buffer);
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String convertToStr(List<byte[]> byteList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte[] byteArray : byteList) {
            String str = new String(byteArray, StandardCharsets.UTF_8); // 使用UTF-8编码将字节数组转换为字符串
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }


    public static String byteArrayToHexStringWithSpaces(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = String.format("%02X", b);
            hexString.append(hex);
            hexString.append(" "); // 添加空格分隔符
        }
        // 去除末尾多余的空格
        if (hexString.length() > 0) {
            hexString.setLength(hexString.length() - 1);
        }
        return hexString.toString();
    }

    public static byte[] listToByte(List<Byte> list) {
        if (list == null || list.size() < 0) {
            return null;
        }
        byte[] bytes = new byte[list.size()];
        int i = 0;
        Iterator<Byte> iterator = list.iterator();
        while (iterator.hasNext()) {
            bytes[i] = iterator.next();
            i++;
        }
        return bytes;
    }

    public static List<Byte> byteToList(byte[] data) {
        List<Byte> byteList = new ArrayList<Byte>();
        if (isNonEmpty(data)) {
            for (byte b : data) {
                byteList.add(b);
            }
        }
        return byteList;
    }

    private static final String TAG = "ByteUtil";

    public static void printByteArrayAsHex(byte[] byteArray) {
        for (byte b : byteArray) {
            String hex = String.format("%02X", b);
            Log.d(TAG, "printByteArrayAsHex: " + hex + " ");
        }
        System.out.println();
    }

    public static boolean isNonEmpty(byte[] data) {
        // 数据为空
        if (data == null) {
            return false;
        }
        for (byte b : data) {
            if (b != 0) {
                // 数据中至少有一个字节不为0
                return true;
            }
        }
        // 数据不为空但所有字节都为0
        return false;
    }

    public static int byteToInt(byte data) {
        return data & 0xFF;
    }


    public static List<byte[]> splitByteArray(byte[] byteArray, int chunkSize) {
        List<byte[]> chunks = new ArrayList<byte[]>();
        int length = byteArray.length;
        int index = 0;

        while (index < length) {
            int remaining = length - index;
            int chunkLength = Math.min(chunkSize, remaining);
            byte[] chunk = new byte[chunkLength];
            System.arraycopy(byteArray, index, chunk, 0, chunkLength);
            chunks.add(chunk);
            index += chunkSize;
        }

        return chunks;
    }

    public static String byteArrayToString(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(b).append(" ");
        }
        return sb.toString().trim();
    }

    public static byte[] bmpImgToByte(File file) {
        byte[] bmpBuf = null;
        if (file.exists()) {
            try {
                FileInputStream in = new FileInputStream(file);
                if (file.length() == 17342)//280*480
                {
                    in.skip(54 + 8);
                    bmpBuf = new byte[480 * 35];
                    for (int i = 479; i >= 0; i--) {
                        in.read(bmpBuf, i * 35, 35);
                        in.skip(1);
                    }
                    //Arrays.fill(bmpBuf,(byte)255);
                }
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                //close
            }
        }
        return bmpBuf;

    }


    /*public static void main(String[] args) {
        short s = 111;
        byte[] length = splitShort(s);
        short mergeV = mergeBytes(length[0], length[1]);
        System.out.println(mergeV);

    }*/

    // 步骤1：将short拆分成两个byte并返回一个byte数组
    public static byte[] splitShort(short value) {
        byte[] result = new byte[2];
        result[0] = (byte) (value >> 8); // 高位字节
        result[1] = (byte) value;        // 低位字节
        return result;
    }

    public static short mergeBytes(byte highByte, byte lowByte) {
        short mergedValue = (short) ((highByte << 8) | (lowByte & 0xFF));
        return mergedValue;
    }


    public static byte[] convertFileToByteArray() {
        File file = new File(System.getProperty("user.dir") + "/src/test.jpg");
        BufferedInputStream bis = null;
        byte[] bytesArray = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            bytesArray = new byte[(int) file.length()];
            bis.read(bytesArray);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bytesArray;
    }

    public static void main_b(String[] args) {
        // 将整数转换为字节
        /*int myInt = 12345;
        byte[] intBytes = setDataLength(myInt);

        // 将字节转换回整数
        int convertedInt = getDataLength(intBytes[0], intBytes[1]);

        System.out.println(convertedInt);*/

        int l = 9;
        int s = 4;
        byte[] inputData = new byte[6];

        // 填充示例数据
        for (int i = 0; i < inputData.length; i++) {
            inputData[i] = (byte) (i + 1);
        }

        // 使用 splitData 方法拆分数据
        List<byte[]> dataList = splitData(inputData, s, l);

        // 打印拆分后的数据块
        for (byte[] chunk : dataList) {
            System.out.println(Arrays.toString(chunk));
        }

    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 重新标记中断状态
        }
    }

    public static int byteArrayToInt(byte[] byteArray) {
        if (byteArray.length != 4) {
            return -1;
        }
        int result = ((byteArray[3] & 0xFF) << 24) | ((byteArray[2] & 0xFF) << 16) | ((byteArray[1] & 0xFF) << 8) | (byteArray[0] & 0xFF);
        return result;
    }


    public static List<byte[]> splitData(byte[] data, int s, int l) {
        List<byte[]> dataList = new ArrayList<>();
        int index = 0;
        int length = data.length;
        while (index < length) {
            int chunkSize;
            if (length - index >= l) {
                // 如果剩余数据长度大于等于l，按照l拆分
                chunkSize = l;
            } else {
                chunkSize = Math.min(s, length - index); // 否则按照s拆分
            }
            byte[] chunk = Arrays.copyOfRange(data, index, index + chunkSize);
            // 如果chunk的长度不足s，用0进行补齐
            if (chunkSize < s) {
                byte[] paddedChunk = new byte[s];
                System.arraycopy(chunk, 0, paddedChunk, 0, chunkSize);
                Arrays.fill(paddedChunk, chunkSize, s, (byte) 0);
                dataList.add(paddedChunk);
            } else {
                dataList.add(chunk);
            }
            index += chunkSize;
        }
        return dataList;
    }

    public static List<byte[]> splitTitleData(byte[] data, int s, int l) {
        List<byte[]> dataList = new ArrayList<>();
        int index = 0;
        int length = data.length;
        while (index < length) {
            int chunkSize;
            if (length - index > l) {
                // 如果剩余数据长度大于l，按照l拆分
                chunkSize = l;
            } else {
                chunkSize = Math.min(s, length - index); // 否则按照s拆分
            }
            byte[] chunk = Arrays.copyOfRange(data, index, index + chunkSize);
            dataList.add(chunk);
            index += chunkSize;
        }
        return dataList;
    }


    /**
     * @param hByte
     * @param lByte
     * @return
     */
    public static Integer getDataLength(Byte hByte, Byte lByte) {
        // 确保输入的字节不为null
        if (hByte == null || lByte == null) {
            return 0;
        }
        // 将高位字节和底位字节转换为整数
        int highByteValue = hByte & 0xFF;
        // 使用0xFF掩码确保字节值是正数
        int lowByteValue = lByte & 0xFF;
        // 将高位字节左移8位并与底位字节进行按位或操作以得到整数
        int result = (highByteValue << 8) | lowByteValue;
        return result;
    }

    public static byte[] setDataLength(int value) {
        // 创建一个包含两个字节的字节数组
        byte[] bytes = new byte[2];

        // 高位字节是整数右移8位后的结果
        bytes[0] = (byte) (value >> 8);

        // 低位字节是整数的低8位
        bytes[1] = (byte) value;

        return bytes;
    }

    public static void main(String[] args) {
        //System.out.println(Arrays.asList(parseLength(120, 50)));
        byte[] head = convertToLittleEndian(1022);
        System.out.printf("字节[0]=0x%02X, 字节[1]=0x%02X\n", head[0] & 0xFF, head[1] & 0xFF);
        int size = convertFromLittleEndian(head[0], head[1]);
        System.out.println("size:" + size);
    }


    public static byte[] concatenateByteArrays(List<byte[]> byteArrayList) {
        if (byteArrayList == null || byteArrayList.isEmpty()) {
            throw new IllegalArgumentException("byteArrayList不能为空");
        }
        int totalLength = 0;
        // 计算总字节数
        for (byte[] byteArray : byteArrayList) {
            totalLength += byteArray.length;
        }
        // 创建新的字节数组并拼接
        byte[] result = new byte[totalLength];
        int currentIndex = 0;
        for (byte[] byteArray : byteArrayList) {
            System.arraycopy(byteArray, 0, result, currentIndex, byteArray.length);
            currentIndex += byteArray.length;
        }
        return result;
    }

    public static List<Integer> parseLength(int value, int parseSize) {
        List<Integer> resultList = new ArrayList<>();

        if (parseSize <= 0) {
            throw new IllegalArgumentException("parseSize必须大于0");
        }

        while (value > 0) {
            int part = Math.min(value, parseSize);
            resultList.add(part);
            value -= part;
        }

        return resultList;
    }

    /**
     * 将一个int无符号整数拆分成小端模式占两个字节
     */
    public static byte[] convertToLittleEndian(int value) {
        if (value < 0 || value > 65535) {
            throw new IllegalArgumentException("输入的整数必须在0到65535之间");
        }
        byte[] result = new byte[2];
        result[0] = (byte) (value & 0xFF);
        result[1] = (byte) ((value >> 8) & 0xFF);
        return result;
    }
    public static byte[] intToTwoByteArray(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort((short) value);
        return buffer.array();
    }
    /**
     * 小端模式占两个字节 TO Integer
     *
     * @param head1
     * @return
     */
    public static int convertFromLittleEndian(byte head1, byte head2) {
        int result = (head1 & 0xFF) | ((head2 & 0xFF) << 8);
        return result;
    }

    public static List<Integer> splitIntoChunks(int length, int chunkSize) {
        List<Integer> resultList = new ArrayList<>();

        while (length > 0) {
            int chunk = Math.min(length, chunkSize);
            resultList.add(chunk);
            length -= chunk;
        }

        return resultList;
    }

    public static String bytesToHex(byte aByte, byte aByte1) {
        // 使用Java的格式化功能将字节转换为16进制字符串
        String hexAByte = String.format("%02X", aByte);
        String hexAByte1 = String.format("%02X", aByte1);
        // 将两个16进制字符串连接起来
        String hexString = hexAByte + hexAByte1;

        return hexString;
    }
}
