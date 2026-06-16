package com.ahuralearn.common.utils;

public class TimeUtils {

    public static String formatTime(Integer seconds) {
        if (seconds == null)
            return "00:00";

        // shift the seconds to h/m/s
        int hour = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int second = seconds % 60;

        // format
        if (hour > 0) // over at least 1 hour
            return String.format("%02d:%02d:%02d", hour, minutes, second);
        // within 1 hour
        return String.format("%02d:%02d", minutes, second);
    }
}
