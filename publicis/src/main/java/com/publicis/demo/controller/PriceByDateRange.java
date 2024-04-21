package com.publicis.demo.controller;

import com.mashape.unirest.http.JsonNode;
import com.publicis.demo.services.CoinDesk;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

@RestController
@RequestMapping("api/v1/bpi")
public class PriceByDateRange {

    @Autowired
    private CoinDesk coinDeskService;
    @GetMapping("/historical")
    public ResponseEntity<String> getPriceByRange(HttpServletRequest request, HttpServletResponse response) throws UnirestException {
        try {

            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");

            Date startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    .parse(startDateStr);
            Date endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    .parse(endDateStr);

            JSONObject respObj = new JSONObject(coinDeskService.getHistoricData());

           JSONObject bpi = respObj.getJSONObject("bpi");

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
                if(curDate.compareTo(startDate) >=0 && curDate.compareTo(endDate) <=0){
                    Double curValue =  bpi.getDouble(curDateStr);
                    if(curValue > max){
                        max = curValue;
                        maxDate = curDateStr;
                    } else if(curValue < min){
                        min = curValue;
                        minDate = curDateStr;
                    }
                    finalRespObj.put(curDateStr,String.valueOf(curValue));
                }
            }

            String finalMaxValue = finalRespObj.getString(maxDate);
            finalMaxValue += " " + "(high)";

            String finalMinValue = finalRespObj.getString(minDate);
            finalMinValue += " " + "(low)";

            finalRespObj.put(maxDate, finalMaxValue);
            finalRespObj.put(minDate, finalMinValue);



            return ResponseEntity.ok(finalRespObj.toString());
        }catch (Exception e){
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

}
