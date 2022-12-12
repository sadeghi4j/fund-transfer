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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select acc from Account acc where acc.id = ?1")
//    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    Account findAndLock(Long id);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select acc from Account acc where acc.id = ?1")
    Account findAndLockOptimistic(Long id);

    @Modifying
    @Query("update Account acc set acc.balance = acc.balance - ?2 where acc.id = ?1")
    int withdraw(Long id, BigDecimal amount);


    @Modifying
    @Query("update Account acc set acc.balance = acc.balance + ?2 where acc.id = ?1")
    int deposit(Long id, BigDecimal amount);
}
