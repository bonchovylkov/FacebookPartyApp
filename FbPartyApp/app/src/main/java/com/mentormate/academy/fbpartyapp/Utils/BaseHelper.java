package com.mentormate.academy.fbpartyapp.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Student11 on 2/19/2015.
 */
public class BaseHelper {
    public  static String getSubstringByCount(String text,int lenght){
        String result ="";
        if (text.length()>lenght){
            result = text.substring(0,lenght) + "...";
        }
        else{
            result = text;
        }


        return  result;
    }

    public  static String getDateInFormat(String format,Date date){
        if(format.equals("")){
            format = "dd-mm-yyyy";
        }
        SimpleDateFormat formatter5=new SimpleDateFormat(format);
        String formattedDate = formatter5.format(date);
        return  formattedDate;
    }

    public  static String getDateInFormat(String format,String dateStr){
        try {
            Date date = Constants.formatter.parse(dateStr);
            return Constants.formatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";


        // String startTime = "2013-12-21T18:30:00+0100";
        //this is the facebook date format




        /*DateFormat formatter5=Constants.formatter;
        String formattedDate = formatter5.format(dateStr);
        return  formattedDate;*/
    }


}
