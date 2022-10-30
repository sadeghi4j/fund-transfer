package com.sadeghi.fundtransfer.controller;

import com.sadeghi.fundtransfer.service.ExchangeRateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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

    final ExchangeRateService exchangeRateService;

    @GetMapping("/exchange-rate/{from}/{to}")
    public Double getExchangeRate(@PathVariable String from, @PathVariable String to) {
        return exchangeRateService.getExchangeRate(from, to);
    }

}
