package com.sadeghi.fundtransfer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sadeghi.fundtransfer.dto.TransferRequest;
import com.sadeghi.fundtransfer.entity.Account;
import com.sadeghi.fundtransfer.exception.AccountNotFoundException;
import com.sadeghi.fundtransfer.exception.ExchangeRateCanNotBeRetrievedException;
import com.sadeghi.fundtransfer.exception.InsufficientBalanceException;
import com.sadeghi.fundtransfer.repository.AccountRepository;
import com.sadeghi.fundtransfer.repository.TransferRepository;
import com.sadeghi.fundtransfer.service.AccountService;
import com.sadeghi.fundtransfer.service.ExchangeRateService;
import com.sadeghi.fundtransfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log4j2
class FundTransferApplicationTests extends BaseTestClass {

    private static final BigDecimal INITIAL_BALANCE = new BigDecimal("1000.000");
    private static final Double EXCHANGE_RATE = 0.9940;
    private static final String EUR = "EUR";
    private static final String USD = "USD";

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransferRepository transferRepository;
    @Autowired
    private TransferService transferService;
    @Autowired
    private AccountService accountService;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    public void initializeDatabase() {
        accountRepository.save(Account.builder().balance(INITIAL_BALANCE).currency(EUR).build());
        accountRepository.save(Account.builder().balance(INITIAL_BALANCE).currency(USD).build());
    }

    @BeforeEach
    public void beforeEach() {
        transferRepository.deleteAll();
        resetBalance();
        when(exchangeRateService.getExchangeRate(EUR, USD)).thenReturn(EXCHANGE_RATE);
    }

    @Test
    void simpleTransfer() {
        transferService.transfer(UUID.randomUUID().toString(), new TransferRequest(1L, 2L, new BigDecimal(1)));
        checkAccountBalance(1L, new BigDecimal(999));
        checkAccountBalance(2L, new BigDecimal("1000.994"));
        checkTransferListSize(1);
    }

    @Test
    void IncorrectSourceAccount() {
        assertThrows(AccountNotFoundException.class, () ->
                transferService.transfer(UUID.randomUUID().toString(), new TransferRequest(0L, 2L, new BigDecimal(1))));
        checkTransferListSize(0);
    }

    @Test
    void IncorrectDestinationAccount() {
        assertThrows(AccountNotFoundException.class, () ->
                transferService.transfer(UUID.randomUUID().toString(), new TransferRequest(1L, 0L, new BigDecimal(1))));
        checkTransferListSize(0);
    }

    @Test
    void insufficientBalanceException() {
        assertThrows(InsufficientBalanceException.class, () ->
                transferService.transfer(UUID.randomUUID().toString(), new TransferRequest(1L, 2L, new BigDecimal(1001))));
        checkTransferListSize(0);
    }

    @Test
    void exchangeRateCanNotBeRetrieved() {
        accountRepository.save(Account.builder().balance(INITIAL_BALANCE).currency(EUR).build());
        accountRepository.save(Account.builder().balance(INITIAL_BALANCE).currency("US").build());
        assertThrows(ExchangeRateCanNotBeRetrievedException.class, () ->
                transferService.transfer(UUID.randomUUID().toString(), new TransferRequest(3L, 4L, new BigDecimal(1))));
        checkTransferListSize(0);
    }

    //    @Disabled
    @Test
    void concurrentTransfer() {
        StopWatch watch = new StopWatch();
        watch.start();
        int count = 1_000;
        IntStream.range(1, count + 1).parallel().forEach(cnt -> {
            String requestId = UUID.randomUUID().toString();
            transferService.transfer(requestId, new TransferRequest(1L, 2L, new BigDecimal(1)));
        });
        watch.stop();
        log.info("watch.getTotalTimeMillis() = " + watch.getTotalTimeMillis());

        checkAccountBalance(1L, new BigDecimal(0));
        double total = INITIAL_BALANCE.doubleValue() + (EXCHANGE_RATE * count);
        checkAccountBalance(2L, new BigDecimal(total));
        checkTransferListSize(count);
    }

    private void checkAccountBalance(long accountId, BigDecimal desiredBalance) {
        Account fromAccount = accountService.findById(accountId);
        assertEquals(0, fromAccount.getBalance().compareTo(desiredBalance));
    }

    private void checkTransferListSize(int expected) {
        int countAll = transferService.countAll();
        assertEquals(expected, countAll);
    }

    private void resetBalance() {
        Optional<Account> optionalAccount = accountRepository.findById(1L);
        optionalAccount.ifPresent(account -> {
            account.setBalance(INITIAL_BALANCE);
            accountRepository.save(account);
        });

        optionalAccount = accountRepository.findById(2L);
        optionalAccount.ifPresent(account -> {
            account.setBalance(INITIAL_BALANCE);
            accountRepository.save(account);
        });
    }

    @Test
    public void mockMVCTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/transfer")
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .content(asJsonString(new TransferRequest(1L, 2L, new BigDecimal(100))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromAmount").value(900))
                .andExpect(jsonPath("$.toAmount").value(1099.4));
    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
