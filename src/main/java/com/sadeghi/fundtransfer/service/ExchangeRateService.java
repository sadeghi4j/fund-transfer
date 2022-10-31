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

    final ExchangeRateClientService exchangeRateClientService;

    public Double getExchangeRate(String from, String to) {
        return exchangeRateClientService.getExchangeRate(from, to);
    }
}
