package com.card.lp_server.card.device.model;

import java.util.Arrays;

/**
 * 执行命令封装对象
 *
 * @author mazhe
 */
public class Command {

    /**
     * 模式 READ WRITE NONE
     */
    private String model;
    /**
     * 命令名称
     */
    private String cmdName;
    /**
     * 文件名称
     */
    private String fileName;


    private String year;
    private String month;
    private String day;
    private String hour;
    private String min;
    private String second;
    /**
     * 偏移量
     */
    private Integer offset;
    /**
     * read 长度
     */
    private Integer length;
    /**
     * read :输出数据源 write :输入数据源
     */
    public byte[] bytes;

    /**
     * 命令执行结果
     */
    private boolean cmdStates = false;

    public boolean isCmdStates() {
        return cmdStates;
    }

    public void setCmdStates(boolean cmdStates) {
        this.cmdStates = cmdStates;
    }

    public byte[] getBytes() {
        return bytes;
    }


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }


    /**
     * 设置返回数据合并追加
     */
    public void setBytes(byte[] bytes) {
        if (this.bytes == null) {
            this.bytes = bytes;
        } else {
            int length1 = this.bytes.length;
            int length2 = bytes.length;
            byte[] result = new byte[length1 + length2];
            System.arraycopy(this.bytes, 0, result, 0, length1);
            System.arraycopy(bytes, 0, result, length1, length2);
            this.bytes = result;
        }

    }

    public void clearBytes() {
        bytes = null;
    }

    public Command(String model, String cmdName, String fileName, Integer offset, Integer length) {
        this.model = model;
        this.cmdName = cmdName;
        this.fileName = fileName;
        this.offset = offset;
        this.length = length;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCmdName() {
        return cmdName;
    }

    public void setCmdName(String cmdName) {
        this.cmdName = cmdName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "Command{" +
                "model='" + model + '\'' +
                ", cmdName='" + cmdName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", year='" + year + '\'' +
                ", month='" + month + '\'' +
                ", day='" + day + '\'' +
                ", hour='" + hour + '\'' +
                ", min='" + min + '\'' +
                ", second='" + second + '\'' +
                ", offset=" + offset +
                ", length=" + length +
                ", bytes=" + Arrays.toString(bytes) +
                ", cmdStates=" + cmdStates +
                '}';
    }
}
