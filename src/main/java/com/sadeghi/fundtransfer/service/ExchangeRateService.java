package com.sadeghi.fundtransfer.service;

import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExchangeRateService {

    final ExchangeRateClientService exchangeRateClientService;
    final RetryRegistry retryRegistry;

    @PostConstruct
    public void init() {
        retryRegistry.retry("getExchangeRate")
                .getEventPublisher().onRetry(event -> log.error("getExchangeRate failed. event: {}", event));
    }

    @Retry(name = "getExchangeRate")
    public Double getExchangeRate(String from, String to) {
        return exchangeRateClientService.getExchangeRate(from, to);
    }

}
