package com.card.lp_server.card.device.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
    public static String getCurrentYear() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return dateFormat.format(new Date());
    }

    public static String getCurrentMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        return dateFormat.format(new Date());
    }

    public static String getCurrentDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        return dateFormat.format(new Date());
    }

    public static String getCurrentHour() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
        return dateFormat.format(new Date());
    }

    public static String getCurrentMinute() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm");
        return dateFormat.format(new Date());
    }

    public static String getCurrentSecond() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ss");
        return dateFormat.format(new Date());
    }
}
