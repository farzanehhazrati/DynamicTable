// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PersianCalendarBean.java

package service.classes;


import com.ghasemkiani.util.SimplePersianCalendar;
import entity.classes.PersianCalendarEntity;

import java.util.Date;

public class PersianCalendarBean
{

    public PersianCalendarBean()
    {
    }

    public static String getPersianDate(Date julianDate)
    {
        PersianCalendarEntity spCal = new PersianCalendarEntity();
        String dateSplited[];
        if(julianDate != null && julianDate.getHours() < 5)
        {
            Date date = new Date(julianDate.getTime());
            date.setHours(5);
            dateSplited = spCal.getDateFields(date).toString().split("/");
        } else
        {
            dateSplited = spCal.getDateFields(julianDate).toString().split("/");
        }
        if(dateSplited[1].length() == 1)
            dateSplited[1] = (new StringBuilder()).append('0').append(dateSplited[1]).toString();
        if(dateSplited[2].length() == 1)
            dateSplited[2] = (new StringBuilder()).append('0').append(dateSplited[2]).toString();
        try
        {
            String hours;
            if(julianDate.getHours() < 10)
                hours = (new StringBuilder()).append('0').append(String.valueOf(julianDate.getHours())).toString();
            else
                hours = String.valueOf(julianDate.getHours());
            String minute;
            if(julianDate.getMinutes() < 10)
                minute = (new StringBuilder()).append('0').append(String.valueOf(julianDate.getMinutes())).toString();
            else
                minute = String.valueOf(julianDate.getMinutes());
            String second;
            if(julianDate.getSeconds() < 10)
                second = (new StringBuilder()).append('0').append(String.valueOf(julianDate.getSeconds())).toString();
            else
                second = String.valueOf(julianDate.getSeconds());
            return (new StringBuilder()).append(dateSplited[0]).append("/").append(dateSplited[1]).append("/").append(dateSplited[2]).append(" ").append(hours).append(":").append(minute).append(":").append(second).toString();
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static Date getJulianDate(String persianDate)
    {
        SimplePersianCalendar pCal = new SimplePersianCalendar();
        String dateTime[] = persianDate.split(" ");
        pCal.setDateFields(Integer.parseInt(dateTime[0].substring(0, 4)), Integer.parseInt(dateTime[0].substring(5, 7)) - 1, Integer.parseInt(dateTime[0].substring(8, 10)));
        Date date = pCal.getTime();
        if(dateTime.length > 1)
        {
            date.setHours(Integer.parseInt(dateTime[1].split(":")[0]));
            date.setMinutes(Integer.parseInt(dateTime[1].split(":")[1]));
            date.setSeconds(Integer.parseInt(dateTime[1].split(":")[2]));
        }
        return date;
    }
}
