package com.sadeghi.fundtransfer;

import com.sadeghi.fundtransfer.dto.TransferRequest;
import com.sadeghi.fundtransfer.entity.Account;
import com.sadeghi.fundtransfer.entity.Transfer;
import com.sadeghi.fundtransfer.service.AccountService;
import com.sadeghi.fundtransfer.service.TransferFacade;
import com.sadeghi.fundtransfer.service.TransferService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
class FundTransferApplicationTests extends BaseTestClass {

    @Autowired
    private TransferFacade transferFacade;
    @Autowired
    private TransferService transferService;
    @Autowired
    private AccountService accountService;

    @Test
    void simpleTransfer() {
        transferFacade.transferWithLock(UUID.randomUUID().toString(), new TransferRequest(1L, 2L, new BigDecimal(1)));
        Account fromAccount = accountService.findById(1L);
        assertEquals(0, fromAccount.getBalance().compareTo(new BigDecimal(999)));

        List<Transfer> transferList = transferService.findAll();
        assertEquals(1, transferList.size());
    }

    @Test
    void concurrentTransfer() {
        StopWatch watch = new StopWatch();
        watch.start();
        IntStream.range(1, 1000).parallel().forEach(cnt -> {
//            log.info("cnt = " + cnt);
            transferFacade.transferWithLock(UUID.randomUUID().toString(), new TransferRequest(1L, 2L, new BigDecimal(1)));
        });
        watch.stop();
        System.out.println("watch.getTotalTimeMillis() = " + watch.getTotalTimeMillis());
        Account fromAccount = accountService.findById(1L);
        assertEquals(0, fromAccount.getBalance().compareTo(new BigDecimal(1)));

        List<Transfer> transferList = transferService.findAll();
        assertEquals(999, transferList.size());
    }

}
