package com.example.ourbook.DataTool;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeAgo {
//    public static void main(String[] args) {
//        // 예제 시간 설정
//        String timeStr = "2024-02-16T00:47:26";
//
//        String timeAgoStr = timeAgo(givenTime);
//        System.out.println(timeAgoStr);
//    }

    public static String timeAgo(String timeStr) {
        LocalDateTime givenTime = LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(givenTime, now);

        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;
        long months = days / 30;
        long years = days / 365;

        if (years >= 1) {
            return years + "년 전";
        } else if (months >= 1) {
            return months + "개월 전";
        } else if (weeks >= 1) {
            return weeks + "주 전";
        } else if (days >= 1) {
            return days + "일 전";
        } else if (hours >= 1) {
            return hours + "시간 전";
        } else if (minutes >= 1) {
            return minutes + "분 전";
        } else {
            return "방금 전";
        }
    }
}
