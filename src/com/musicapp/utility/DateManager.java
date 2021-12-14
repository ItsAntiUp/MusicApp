package com.musicapp.utility;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateManager {
    private static final String UNDEFINED_TIME = "00:00:00";

    public static Date getLastModifiedDate(File f){
        /*Trying to extract the last modified date from file, and assign it to the release date.*/
        try{
            Path filePath = Paths.get(f.getPath());
            BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            return df.parse(df.format(attr.lastModifiedTime().toMillis()));
        }
        catch(Exception e){
            /* Error getting the last modified time (catching literally any exception).*/
            return new Date();
        }
    }

    public static Calendar getTimeFromDouble(double input){
        Calendar calendar = Calendar.getInstance();

        int hour = (int)(input/ 3600);
        int minute = (int)((input - hour * 3600) / 60);
        int second = (int)(input - hour * 3600 - minute * 60);

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        return calendar;
    }

    public static String getDateString(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String getTimeString(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        try {
            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            return df.format(calendar.getTime());
        }
        catch(Exception e){
            return UNDEFINED_TIME;
        }
    }
}
