package com.sadeghi.fundtransfer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Description of file goes here
 *
 * @author Ali Sadeghi
 * Created at 2022/10/28 - 6:14 PM
 */

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferResponse {

    Long fromAccountId;
    BigDecimal fromAmount;
    Long toAccountId;
    BigDecimal toAmount;
    String requestId;
    LocalDateTime creationTime;

}
