package com.sadeghi.fundtransfer.service;

import com.sadeghi.fundtransfer.dto.TransferRequest;
import com.sadeghi.fundtransfer.dto.TransferResponse;
import com.sadeghi.fundtransfer.entity.Account;
import com.sadeghi.fundtransfer.entity.Transfer;
import com.sadeghi.fundtransfer.exception.DuplicateRequestException;
import com.sadeghi.fundtransfer.exception.ExchangeRateCanNotBeRetrievedException;
import com.sadeghi.fundtransfer.exception.InsufficientBalanceException;
import com.sadeghi.fundtransfer.mapper.DTOMapper;
import com.sadeghi.fundtransfer.repository.TransferRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferService {

    final AccountService accountService;
    final TransferRepository transferRepository;
    final ExchangeRateService exchangeRateService;
    final PlatformTransactionManager platformTransactionManager;

    TransactionTemplate transactionTemplate;

    @PostConstruct
    public void init() {
        transactionTemplate = new TransactionTemplate(platformTransactionManager);
    }

    // Transactional annotation should not be used here, because here we are doing some validation
    // , and external service call, so it is not needed to grab transaction and waste it
    // @Transactional()
    public TransferResponse transfer(String requestId, TransferRequest transferRequest) {
        Double exchangeRate = validateTransfer(requestId, transferRequest);
        Transfer transfer = transfer(requestId, transferRequest, exchangeRate);
        return DTOMapper.INSTANCE.mapTransfer(transfer);
    }

    public Double validateTransfer(String requestId, TransferRequest transferRequest) {

        // This makes service Idempotent, preventing the same request processed multiple times.
        if (duplicateTransferExists(requestId)) {
            throw new DuplicateRequestException();
        }

        Account fromAccount = accountService.findById(transferRequest.getFromAccountId());
        Account toAccount = accountService.findById(transferRequest.getToAccountId());

        if (fromAccount.getBalance().compareTo(transferRequest.getAmount()) < 0) {
            throw new InsufficientBalanceException();
        }

        Double exchangeRate = exchangeRateService.getExchangeRate(fromAccount.getCurrency(), toAccount.getCurrency());
        if (exchangeRate == null || exchangeRate.equals(0D)) {
            log.error("Exchange Rate Can Not Be Retrieved Exception from: {} to: {}, requestId: {}",
                    fromAccount.getCurrency(), toAccount.getCurrency(), requestId);
            throw new ExchangeRateCanNotBeRetrievedException();
        }
        return exchangeRate;
    }

    /**
     * As far as @{@link Transactional} does not work when calling within the same bean, I used {@link TransactionTemplate}
     * to manage transaction programmatically.
     * Otherwise, you should declare another [Facade] bean and call the validateTransfer and transferSafe there in order to use @{@link Transactional}
     * @param requestId
     * @param request
     * @param exchangeRate
     * @return
     */
    private Transfer transfer(String requestId, TransferRequest request, Double exchangeRate) {
        return transactionTemplate.execute(status -> {

            BigDecimal toAmount = request.getAmount().multiply(BigDecimal.valueOf(exchangeRate));

            Account fromAccount = accountService.findAndLock(request.getFromAccountId());
            accountService.checkBalance(fromAccount, request.getAmount());

            Account toAccount = accountService.findAndLock(request.getToAccountId());

            fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
            toAccount.setBalance(toAccount.getBalance().add(toAmount));

            accountService.save(fromAccount);
            accountService.save(toAccount);

            Transfer innerTransfer = Transfer.builder()
                    .fromAccountId(request.getFromAccountId())
                    .fromAmount(fromAccount.getBalance())
                    .toAccountId(request.getToAccountId())
                    .toAmount(toAccount.getBalance())
                    .requestId(requestId)
                    .build();
            return transferRepository.save(innerTransfer);

        });
    }

    public boolean duplicateTransferExists(String requestId) {
        return transferRepository.existsByRequestId(requestId);
    }

    public List<Transfer> findAll() {
        return transferRepository.findAll();
    }

    public int countAll() {
        return transferRepository.countAllBy();
    }

}
