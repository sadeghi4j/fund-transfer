package com.sadeghi.fundtransfer.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Description of file goes here
 *
 * @author Ali Sadeghi
 * Created at 2022/10/28 - 7:35 PM
 */
@FeignClient(name = "ExchangeRateClientService", url = "http://localhost:8080")
public interface ExchangeRateClientService {

    // todo Retry can be used here
    @GetMapping("/exchange-rate/{from}/{to}")
    Double getExchangeRate(@PathVariable String from, @PathVariable String to);

}
