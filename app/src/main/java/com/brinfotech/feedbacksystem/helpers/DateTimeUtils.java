package com.brinfotech.feedbacksystem.helpers;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {


    public static String getCurrentDate(Context context) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static String getCurrentTime(Context context) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("h:mm aa");
        String formattedDate = df.format(c.getTime());

        if (formattedDate.contains("p.m.")) {
            formattedDate = formattedDate.replace("p.m.", "pm");
        } else {
            formattedDate = formattedDate.replace("a.m.", "am");
        }

        return formattedDate;
    }


    public static String getDesiredDateTime(String originalString, String actualPattern, String requiredPattern) {
        Date date;
        try {
            date = new SimpleDateFormat(actualPattern, Locale.US).parse(originalString);
            return new SimpleDateFormat(requiredPattern, Locale.US).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

    private static String getNthNumberOfDate(int number) {
        if (number > 3 && number < 21){
            return "th";
        }
        switch (number % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static long getCurrentTimeInMilis() {
        return System.currentTimeMillis();
    }

    public static String getMediumFormattedDate(int year, int month, int day) {

        int actualMonth = month + 1;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, day, 0, 0, 0);
        Date chosenDate = cal.getTime();

        DateFormat df_medium_us = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);

        return df_medium_us.format(chosenDate);
    }

    public static String getLongFormattedDate() {

        Calendar c = Calendar.getInstance();
        Date date = c.getTime();


        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(date.getYear(), date.getMonth(), date.getDay(), 0, 0, 0);
        Date chosenDate = cal.getTime();

        DateFormat df_medium_us = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);

        return df_medium_us.format(chosenDate);
    }

    public static String getFormattedTime(int hour, int minute) {


        Calendar datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hour);
        datetime.set(Calendar.MINUTE, minute);


        String myFormat = "hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        return sdf.format(datetime.getTime());

    }

}
