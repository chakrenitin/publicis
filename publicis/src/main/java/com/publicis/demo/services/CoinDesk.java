package com.publicis.demo.services;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CoinDesk {

    @Value("${coinDesk.baseurl}")
    private String coinDeskBaseUrl;

    @Autowired
    private CacheManager cacheManager;

    public String getHistoricData() throws UnirestException {

        System.out.println("\nprint new build 4 \n");
        JSONObject response = new JSONObject();
        try {
            String cacheName = "historicData";
            String cacheKey = "historicData";

            String cachedValue = getCachedValue(cacheName, cacheKey);
            if (cachedValue != null) { // checking the data in cache
                return cachedValue;
            }

            HttpResponse<JsonNode> resp = Unirest.get(coinDeskBaseUrl + "/v1/bpi/historical/close.json")
                    .asJson();

            JSONObject respObj = resp.getBody().getObject();
            response.put("status", "success");
            response.put("bpi",respObj.getJSONObject("bpi"));

            cacheValue(cacheName, cacheKey, response.toString()); // caching the success response from coinDesk, to reuse it when coinDesk is not accessible or using it in offline mode
        }catch (Exception e){
            response.put("status", "error");
            response.put("message", "Failed to fetch the data, please try again in some time");
        }
        return response.toString();
    }

    private String getCachedValue(String cacheName, String cacheKey) {
        return Objects.requireNonNull(cacheManager.getCache(cacheName)).get(cacheKey, String.class);
    }

    private void cacheValue(String cacheName, String cacheKey, String value) {
        Objects.requireNonNull(cacheManager.getCache(cacheName)).put(cacheKey, value);
    }
}
