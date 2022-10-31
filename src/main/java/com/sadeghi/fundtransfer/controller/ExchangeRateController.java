package com.sadeghi.fundtransfer.controller;

import com.sadeghi.fundtransfer.service.ExchangeRateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * A Controller class for simulating exchange rate external API
 *
 * @author Ali Sadeghi
 * Created at 2022/10/28 - 6:55 PM
 */

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
public class ExchangeRateController {

    Map<String, Double> rates;

    @PostConstruct
    public void init() {
        rates = new HashMap<>();
        rates.put("EUR/USD", 0.9940);
        rates.put("EUR/GBP", 0.8583);
        rates.put("EUR/CAD", 1.3547);
    }

    @GetMapping("/exchange-rate/{from}/{to}")
    public Double getExchangeRate(@PathVariable String from, @PathVariable String to) {
        return rates.get(from + "/" + to);
    }

}
