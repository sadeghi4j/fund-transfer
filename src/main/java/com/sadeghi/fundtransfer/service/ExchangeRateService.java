package com.sadeghi.fundtransfer.service;

import com.sadeghi.fundtransfer.dto.ExchangeRateRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExchangeRateService {

    Map<String, Double> rates;

    @PostConstruct
    public void init() {
        rates = new HashMap<>();
        rates.put("EUR/USD", 0.9940);
        rates.put("EUR/GBP", 0.8583);
        rates.put("EUR/CAD", 1.3547);
    }

    public Double getExchangeRate(String from, String to) {
        return rates.get(from + "/" + to);
    }
}
