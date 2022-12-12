package com.sadeghi.fundtransfer.repository;

import com.sadeghi.fundtransfer.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    boolean existsByRequestId(String requestIid);

    int countAllBy();

}
