package com.spapp.shadipedia.FireBase;

public class DateTime {

    private String Day;
    private String Date;
    private String Time;

    public DateTime(String day, String date, String time) {
        Day = day;
        Date = date;
        Time = time;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
