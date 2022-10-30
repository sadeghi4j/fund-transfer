package com.sadeghi.fundtransfer.service;

import com.sadeghi.fundtransfer.dto.TransferRequest;
import com.sadeghi.fundtransfer.dto.TransferResponse;
import com.sadeghi.fundtransfer.entity.Account;
import com.sadeghi.fundtransfer.entity.Transfer;
import com.sadeghi.fundtransfer.exception.BalanceNotSufficientException;
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

@Service
@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferService {

    final AccountService accountService;
    final TransferRepository transferRepository;
    final ExchangeRateClientService exchangeRateClientService;

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
            throw new BalanceNotSufficientException();
        }

        Double exchangeRate = exchangeRateClientService.getExchangeRate(fromAccount.getCurrency(), toAccount.getCurrency());
        if (exchangeRate == null) {
            throw new ExchangeRateCanNotBeRetrievedException();
        }
        return exchangeRate;
    }

    @Transactional
    public TransferResponse transferWithLock(String requestId, TransferRequest request, Double exchangeRate) {
        BigDecimal toAmount = request.getAmount().multiply(BigDecimal.valueOf(exchangeRate));

        Account fromAccount = accountService.findAndLock(request.getFromAccountId());
        Account toAccount = accountService.findAndLock(request.getToAccountId());

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(toAmount));

        accountService.save(fromAccount);
        accountService.save(toAccount);


        Transfer transfer = new Transfer(request.getFromAccountId(), request.getAmount(),
                request.getToAccountId(), toAmount, requestId);
        transferRepository.save(transfer);

        return TransferResponse.builder()
                .fromAccountId(request.getFromAccountId())
                .toAccountId(request.getToAccountId())
                .fromAmount(request.getAmount())
                .toAmount(toAmount)
                .build();
    }

    @Transactional
    public TransferResponse transfer(String requestId, TransferRequest transferRequest, Double exchangeRate) {
        BigDecimal toAmount = transferRequest.getAmount().multiply(BigDecimal.valueOf(exchangeRate));

        accountService.withdraw(transferRequest.getFromAccountId(), transferRequest.getAmount());
        accountService.deposit(transferRequest.getToAccountId(), toAmount);

        Transfer transfer = new Transfer(transferRequest.getFromAccountId(), transferRequest.getAmount(), transferRequest.getToAccountId(), toAmount, requestId);
        transferRepository.save(transfer);

        return TransferResponse.builder()
                .fromAccountId(transferRequest.getFromAccountId())
                .toAccountId(transferRequest.getToAccountId())
                .fromAmount(transferRequest.getAmount())
                .toAmount(toAmount)
                .build();
    }

    public boolean duplicateTransferExists(String requestId) {
        return transferRepository.existsByRequestId(requestId);
    }

}
