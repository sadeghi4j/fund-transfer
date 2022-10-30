package com.sadeghi.fundtransfer.repository;

import com.sadeghi.fundtransfer.entity.Account;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    Account findAndLock(Long id);

    @Modifying
    @Query("update Account acc set acc.balance = acc.balance - ?2 where acc.id = ?1")
    int withdraw(Long id, BigDecimal amount);


    @Modifying
    @Query("update Account acc set acc.balance = acc.balance + ?2 where acc.id = ?1")
    int deposit(Long id, BigDecimal amount);
}
