package com.sadeghi.fundtransfer;

import com.sadeghi.fundtransfer.dto.TransferRequest;
import com.sadeghi.fundtransfer.entity.Account;
import com.sadeghi.fundtransfer.entity.Transfer;
import com.sadeghi.fundtransfer.exception.AccountNotFoundException;
import com.sadeghi.fundtransfer.exception.InsufficientBalanceException;
import com.sadeghi.fundtransfer.exception.ExchangeRateCanNotBeRetrievedException;
import com.sadeghi.fundtransfer.repository.AccountRepository;
import com.sadeghi.fundtransfer.repository.TransferRepository;
import com.sadeghi.fundtransfer.service.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
class FundTransferApplicationTests extends BaseTestClass {

    private static final BigDecimal INITIAL_BALANCE = new BigDecimal(1000);

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransferRepository transferRepository;
    @Autowired
    private TransferFacade transferFacade;
    @Autowired
    private TransferService transferService;
    @Autowired
    private AccountService accountService;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @BeforeAll
    public void initializeDatabase() {
        accountRepository.save(Account.builder().balance(INITIAL_BALANCE).currency("EUR").build());
        accountRepository.save(Account.builder().balance(INITIAL_BALANCE).currency("USD").build());
        when(exchangeRateService.getExchangeRate("EUR", "USD")).thenReturn(Double.valueOf("0.9940"));
    }

    @BeforeEach
    public void beforeEach() {
        transferRepository.deleteAll();
        resetBalance();
    }

    @Test
    void simpleTransfer() {
        transferFacade.transferWithLock(UUID.randomUUID().toString(), new TransferRequest(1L, 2L, new BigDecimal(1)));
        Account fromAccount = accountService.findById(1L);
        assertEquals(0, fromAccount.getBalance().compareTo(new BigDecimal(999)));

        List<Transfer> transferList = transferService.findAll();
        assertEquals(1, transferList.size());
    }

    @Test
    void IncorrectSourceAccount() {
        assertThrows(AccountNotFoundException.class, () ->
                transferFacade.transferWithLock(UUID.randomUUID().toString(), new TransferRequest(0L, 2L, new BigDecimal(1))));
    }

    @Test
    void IncorrectDestinationAccount() {
        assertThrows(AccountNotFoundException.class, () ->
                transferFacade.transferWithLock(UUID.randomUUID().toString(), new TransferRequest(1L, 0L, new BigDecimal(1))));
    }

    @Test
    void insufficientBalanceException() {
        assertThrows(InsufficientBalanceException.class, () ->
                transferFacade.transferWithLock(UUID.randomUUID().toString(), new TransferRequest(1L, 2L, new BigDecimal(1001))));
    }

    @Test
    void exchangeRateCanNotBeRetrieved() {
        accountRepository.save(Account.builder().balance(INITIAL_BALANCE).currency("EU").build());
        accountRepository.save(Account.builder().balance(INITIAL_BALANCE).currency("US").build());
        assertThrows(ExchangeRateCanNotBeRetrievedException.class, () ->
                transferFacade.transferWithLock(UUID.randomUUID().toString(), new TransferRequest(3L, 4L, new BigDecimal(1000))));
    }

//    @Disabled
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

    private void resetBalance() {
        Optional<Account> optionalAccount = accountRepository.findById(1L);
        optionalAccount.ifPresent(account -> {
            account.setBalance(new BigDecimal(1000));
            accountRepository.save(account);
        });

        optionalAccount = accountRepository.findById(2L);
        optionalAccount.ifPresent(account -> {
            account.setBalance(new BigDecimal(1000));
            accountRepository.save(account);
        });
    }

}
