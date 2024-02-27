package com.card.lp_server.card.device.model;

public enum CountEnum {
    TEMP("69"),
    HUMI("90"),
    DOOR("75"),
    ACCE("130");

    private final String value;

    CountEnum() {
        this.value = ""; // 默认值，如果没有提供关联值
    }

    CountEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}