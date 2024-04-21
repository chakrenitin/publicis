package com.publicis.demo.utilities;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utility {

    public static boolean isValidDateFormat(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static JSONObject filterOutPriceByDateRange(JSONObject bpi, String startDate, String endDate, double multiFactor) throws ParseException {

        Date startDateObj = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                .parse(startDate);

        Date endDateObj = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                .parse(endDate);

        Iterator<String> keys = bpi.keys();

        Double max = Double.MIN_VALUE;
        Double min = Double.MAX_VALUE;

        String maxDate = "";
        String minDate = "";
        JSONObject finalRespObj = new JSONObject();

        while(keys.hasNext()) {
            String curDateStr = keys.next();
            Date curDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    .parse(curDateStr);

            if(curDate.compareTo(startDateObj) >=0 && curDate.compareTo(endDateObj) <=0){
                Double curValue =  bpi.getDouble(curDateStr);
                curValue = curValue * multiFactor;
                if(curValue > max){  // Updating the value of max and min
                    max = curValue;
                    maxDate = curDateStr;
                } else if(curValue < min){
                    min = curValue;
                    minDate = curDateStr;
                }
                finalRespObj.put(curDateStr,String.valueOf(curValue));
            }
        }

        String finalMaxValue = finalRespObj.getString(maxDate); // Marking the Highest and Lowest Bitcoin price in the data stream
        finalMaxValue += " " + "(high)";

        String finalMinValue = finalRespObj.getString(minDate);
        finalMinValue += " " + "(low)";

        finalRespObj.put(maxDate, finalMaxValue);
        finalRespObj.put(minDate, finalMinValue);

        return  finalRespObj;
    }
}
