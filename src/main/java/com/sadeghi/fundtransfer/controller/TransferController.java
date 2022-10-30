package com.sadeghi.fundtransfer.controller;

import com.sadeghi.fundtransfer.dto.TransferRequest;
import com.sadeghi.fundtransfer.dto.TransferResponse;
import com.sadeghi.fundtransfer.service.TransferFacade;
import com.sadeghi.fundtransfer.service.TransferService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
public class TransferController {

    final TransferFacade transferFacade;

    @PostMapping("/transfer")
    public TransferResponse transfer(@RequestHeader("X-Request-ID") String requestId, @RequestBody TransferRequest transferRequest) {
        return transferFacade.transferWithLock(requestId, transferRequest);
//        return DTOMapper.INSTANCE.mapMediaItem(mediaItemService.findAllMediaItemsWithSameWriterAndDirectorWhoIsAlive());
    }

}
