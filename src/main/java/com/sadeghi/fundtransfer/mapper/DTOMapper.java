package com.sadeghi.fundtransfer.mapper;

import com.sadeghi.fundtransfer.dto.TransferResponse;
import com.sadeghi.fundtransfer.entity.Transfer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    TransferResponse mapTransfer(Transfer transfer);

}