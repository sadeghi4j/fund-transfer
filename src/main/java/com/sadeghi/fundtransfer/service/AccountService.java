package com.sadeghi.fundtransfer.service;

import com.sadeghi.fundtransfer.entity.Account;
import com.sadeghi.fundtransfer.exception.AccountNotFoundException;
import com.sadeghi.fundtransfer.exception.BalanceNotSufficientException;
import com.sadeghi.fundtransfer.repository.AccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountService {

    final AccountRepository accountRepository;

    public Account findById(Long id) {
        return accountRepository.findById(id).orElseThrow(AccountNotFoundException::new);
    }

    public void save(Account account) {
        accountRepository.save(account);
    }


    public void withdrawWithLock(Long id, BigDecimal amount) {
        Account account = accountRepository.findAndLock(id);
        BigDecimal subtract = account.getBalance().subtract(amount);
        account.setBalance(subtract);
        accountRepository.save(account);
    }

    public void withdraw(Long id, BigDecimal amount) {
        BigDecimal balance = findById(id).getBalance();
        // The balance is checked here again just because there is a time
        // between transfer method starts until the execution gets here
        // and during this time another request may withdraw from this account
        if (balance.compareTo(amount) < 0) {
            throw new BalanceNotSufficientException();
        } else {
            accountRepository.withdraw(id, amount);
        }
    }

    public void deposit(Long id, BigDecimal amount) {
        // findById is called just because there is a possibility that any developer calls this method directly
        findById(id);
        accountRepository.deposit(id, amount);

    }
}
