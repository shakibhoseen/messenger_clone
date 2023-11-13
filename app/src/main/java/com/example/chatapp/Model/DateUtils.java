package com.example.chatapp.Model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String dateFromLong(long time){

        DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy 'at' hh:mm aaa", Locale.US);
        return format.format(new Date(time));
    }

    public static String WeekFromLong(long time){

        DateFormat format = new SimpleDateFormat("EEE 'at' hh:mm aaa", Locale.US);
        return format.format(new Date(time));
    }

    public static String feedback(long times){
        DateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm", Locale.US);
        return format.format(new Date(times));
    }

    public static String todayTime(long time){
        DateFormat format = new SimpleDateFormat("hh:mm aaa", Locale.US);
        return format.format(new Date(time));
    }

    public static long transform(String dateString) {

        // SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy HH:mm");
        long l = 0;
        //  String dateString = "22-03-2017 11:18:32";
        try {
            //formatting the dateString to convert it into a Date
            Date date = sdf.parse(dateString);
            System.out.println("Given Time in milliseconds : " + date.getTime());

            Calendar calendar = Calendar.getInstance();
            //Setting the Calendar date and time to the given date and time
            calendar.setTime(date);
            l = calendar.getTimeInMillis();
            System.out.println("Given Time in milliseconds : " + calendar.getTimeInMillis());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return l;
    }



    public static String dayIsYesterday(long time){

        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(time);

        Calendar now  = Calendar.getInstance();

        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)){
            DateFormat format = new SimpleDateFormat("hh:mm aaa", Locale.US);
            return format.format(new Date(time));
        }else {    // ( now.get(Calendar.DATE) - smsTime.get(Calendar.DATE)==1)  check for yesterday
            DateFormat format = new SimpleDateFormat("'Yesterday at' hh:mm aaa", Locale.US);
            return format.format(new Date(time));
        }




    }

}
