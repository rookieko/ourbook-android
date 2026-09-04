package com.example.ourbook.Chat;

import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;

import java.util.Date;

public class TimeConverter {
    public static DateDetails getNewDate(long oldUnixTime) {
        // Unix 타임스탬프를 밀리초 단위로 변환
        Date date = new Date(oldUnixTime * 1000);

        // 날짜 포맷
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM월 dd일(E)");
        dateFormatter.setTimeZone(TimeZone.getDefault()); // 기기의 현재 시간대로 설정
        String formattedDate = dateFormatter.format(date);

        // 시간 포맷 (오전/오후 포함)
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
        timeFormatter.setTimeZone(TimeZone.getDefault());
        String formattedTime = timeFormatter.format(date);

        // 분 포맷
        SimpleDateFormat minuteFormatter = new SimpleDateFormat("mm");
        minuteFormatter.setTimeZone(TimeZone.getDefault());
        String formattedMinute = minuteFormatter.format(date);

        // 반환 객체 생성
        return new DateDetails(formattedDate, formattedTime, formattedMinute);
    }

    // 날짜 세부 정보를 저장하는 클래스
    static class DateDetails {
        String date, time, minute;

        DateDetails(String date, String time, String minute) {
            this.date = date;
            this.time = time;
            this.minute = minute;
        }
    }
}
