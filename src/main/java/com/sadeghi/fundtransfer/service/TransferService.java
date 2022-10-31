package com.sadeghi.fundtransfer.service;

import com.sadeghi.fundtransfer.dto.TransferRequest;
import com.sadeghi.fundtransfer.dto.TransferResponse;
import com.sadeghi.fundtransfer.entity.Account;
import com.sadeghi.fundtransfer.entity.Transfer;
import com.sadeghi.fundtransfer.exception.InsufficientBalanceException;
import com.sadeghi.fundtransfer.exception.DuplicateRequestException;
import com.sadeghi.fundtransfer.exception.ExchangeRateCanNotBeRetrievedException;
import com.sadeghi.fundtransfer.repository.TransferRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // Transactional annotation should not be used here, because here we are doing some validation
    // , and external service call, so it is not needed to grab transaction and waste it
    // @Transactional()
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

    @Transactional
    public Transfer transferWithLock(String requestId, TransferRequest request, Double exchangeRate) {
        BigDecimal toAmount = request.getAmount().multiply(BigDecimal.valueOf(exchangeRate));

        Account fromAccount = accountService.findAndLock(request.getFromAccountId());
        Account toAccount = accountService.findAndLock(request.getToAccountId());

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(toAmount));

        accountService.save(fromAccount);
        accountService.save(toAccount);

        Transfer transfer = Transfer.builder()
                .fromAccountId(request.getFromAccountId())
                .fromAmount(fromAccount.getBalance())
                .toAccountId(request.getToAccountId())
                .toAmount(toAccount.getBalance())
                .requestId(requestId)
                .build();
        return transferRepository.save(transfer);
    }

    @Transactional
    public Transfer transfer(String requestId, TransferRequest request, Double exchangeRate) {
        BigDecimal toAmount = request.getAmount().multiply(BigDecimal.valueOf(exchangeRate));

        accountService.withdraw(request.getFromAccountId(), request.getAmount());
        accountService.deposit(request.getToAccountId(), toAmount);

        return createAndSaveTransfer(requestId, request, toAmount);
    }

    private Transfer createAndSaveTransfer(String requestId, TransferRequest request, BigDecimal toAmount) {
        Transfer transfer = Transfer.builder()
                .fromAccountId(request.getFromAccountId())
                .fromAmount(request.getAmount())
                .toAccountId(request.getToAccountId())
                .toAmount(toAmount)
                .requestId(requestId)
                .build();
        transfer = transferRepository.save(transfer);
        return transfer;
    }

    public boolean duplicateTransferExists(String requestId) {
        return transferRepository.existsByRequestId(requestId);
    }

    public List<Transfer> findAll() {
        return transferRepository.findAll();
    }

}
