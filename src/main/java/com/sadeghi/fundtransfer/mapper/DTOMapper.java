package com.sadeghi.fundtransfer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

//    List<SampleDto> mapMediaItem(List<MediaItem> source);

}