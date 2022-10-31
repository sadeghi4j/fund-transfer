package com.sadeghi.fundtransfer.service;

import com.sadeghi.fundtransfer.dto.TransferRequest;
import com.sadeghi.fundtransfer.dto.TransferResponse;
import com.sadeghi.fundtransfer.entity.Transfer;
import com.sadeghi.fundtransfer.mapper.DTOMapper;
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
        Transfer transfer = transferService.transfer(requestId, transferRequest, exchangeRate);
        return DTOMapper.INSTANCE.mapTransfer(transfer);
    }

    public TransferResponse transferWithLock(String requestId, TransferRequest transferRequest) {
        Double exchangeRate = transferService.validateTransfer(requestId, transferRequest);
        Transfer transfer = transferService.transferWithLock(requestId, transferRequest, exchangeRate);
        return DTOMapper.INSTANCE.mapTransfer(transfer);
    }
}
