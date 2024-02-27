package com.card.lp_server.card.device.util;

/**
 * 对话 返回ACK 或者NAK 状态
 *
 * @author gaowei
 */
public class Talk {
    private String start;
    private Integer length;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "Talk{" +
                "start='" + start + '\'' +
                ", length=" + length +
                '}';
    }
}
