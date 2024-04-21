package com.publicis.demo.utilities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CurrencyUtil {

    public List<String> currencyList = Arrays.asList("USD", "EUR", "INR");

    @Value("${currency.conversion.factor.USD}")
    private double usdConversionFactor;

    @Value("${currency.conversion.factor.EUR}")
    private double eurConversionFactor;

    @Value("${currency.conversion.factor.INR}")
    private double inrConversionFactor;

    public double getMultiplicationFactor(String currency){
        double multiFactor = 1.0;
        if(currency.equals("EUR"))
            multiFactor = eurConversionFactor;
        else if(currency.equals("INR"))
            multiFactor = inrConversionFactor;

        return multiFactor;
    }
}
