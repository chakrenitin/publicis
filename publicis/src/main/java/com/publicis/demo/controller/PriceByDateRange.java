package com.publicis.demo.controller;

import com.publicis.demo.dto.ApiResponse;
import com.publicis.demo.dto.ErrorResponse;
import com.publicis.demo.dto.SuccessResponse;
import com.publicis.demo.services.CoinDesk;
import com.publicis.demo.utilities.CurrencyUtil;
import com.publicis.demo.utilities.Utility;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/bpi")
public class PriceByDateRange {

    @Autowired
    private CoinDesk coinDeskService;
    @Autowired
    private CurrencyUtil currencyUtil;

    @GetMapping(value = "/historical" , produces="application/json")
    public ResponseEntity<ApiResponse> getPriceByRange(@RequestParam(name = "startDate", defaultValue = "2000-01-01") String startDate, @RequestParam(name = "endDate", defaultValue = "2100-01-01") String endDate
            , @RequestParam(name = "currency", defaultValue = "USD") String currency) {
        try {

            if(!Utility.isValidDateFormat(startDate,"yyyy-MM-dd") || !Utility.isValidDateFormat(endDate,"yyyy-MM-dd")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("error","Invalid Date Format. Consider using yyyy-MM-dd"));
            }

            if(!currencyUtil.currencyList.contains(currency)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("error","Invalid Currency. Consider using USD, EUR or INR"));
            }

            JSONObject coinDeskResponse = new JSONObject(coinDeskService.getHistoricData());

            if(coinDeskResponse.getString("status").equals("error")){
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(new ErrorResponse("error",coinDeskResponse.getString("message")));
            }

            JSONObject finalRespObj = Utility.filterOutPriceByDateRange(coinDeskResponse.getJSONObject("bpi"), startDate, endDate, currencyUtil.getMultiplicationFactor(currency));

            return ResponseEntity.ok(new SuccessResponse("success",finalRespObj));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("error","Internal Server Error occurred"));
        }
    }

}
