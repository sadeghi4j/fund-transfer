package com.sadeghi.fundtransfer.service;

import com.sadeghi.fundtransfer.dto.TransferRequest;
import com.sadeghi.fundtransfer.dto.TransferResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Description of file goes here
 *
 * @author Ali Sadeghi
 * Created at 2022/10/28 - 11:04 PM
 */

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class TransferFacade {

    final TransferService transferService;

    public TransferResponse transfer(String requestId, TransferRequest transferRequest) {
        Double exchangeRate = transferService.validateTransfer(requestId, transferRequest);
        return transferService.transfer(requestId, transferRequest, exchangeRate);
    }

    public TransferResponse transferWithLock(String requestId, TransferRequest transferRequest) {
        Double exchangeRate = transferService.validateTransfer(requestId, transferRequest);
        return transferService.transferWithLock(requestId, transferRequest, exchangeRate);
    }
}
