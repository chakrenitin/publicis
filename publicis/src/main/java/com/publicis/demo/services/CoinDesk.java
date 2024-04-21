package com.publicis.demo.services;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CoinDesk {

    @Value("${coinDesk.baseurl}")
    private String coinDeskBaseUrl;


    @Cacheable(cacheNames = "historicData", key = "'historicData'")
    public String getHistoricData() throws UnirestException {
        try {

            HttpResponse<JsonNode> resp = Unirest.get(coinDeskBaseUrl + "/v1/bpi/historical/close.json")
                    .asJson();

            JSONObject respObj = resp.getBody().getObject();

            return respObj.toString();
        }catch (Exception e){
            return "";
        }
    }
}
