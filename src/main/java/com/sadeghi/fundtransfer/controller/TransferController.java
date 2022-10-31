package com.sadeghi.fundtransfer.controller;

import com.sadeghi.fundtransfer.dto.TransferRequest;
import com.sadeghi.fundtransfer.dto.TransferResponse;
import com.sadeghi.fundtransfer.service.TransferFacade;
import com.sadeghi.fundtransfer.service.TransferService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
@RestController
public class TransferController {

    final TransferFacade transferFacade;

    @PostMapping("/transfer")
    public TransferResponse transfer(@RequestHeader("X-Request-ID") String requestId, @RequestBody TransferRequest transferRequest) {
        log.info("Transfer API called. Request ID: {}, TransferRequest: {}", requestId, transferRequest);
        TransferResponse transferResponse = transferFacade.transferWithLock(requestId, transferRequest);
        log.info("Transfer API Returned: {}", transferResponse);
        return transferResponse;
//        return DTOMapper.INSTANCE.mapMediaItem(mediaItemService.findAllMediaItemsWithSameWriterAndDirectorWhoIsAlive());
    }

}
