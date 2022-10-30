package com.sadeghi.fundtransfer.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Description of file goes here
 *
 * @author Ali Sadeghi
 * Created at 2022/10/28 - 6:35 PM
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
//todo add indexes
public class Transfer extends BaseEntity<Long> {

    Long fromAccountId;
//    String fromCurrency;
    BigDecimal fromAmount;

    Long toAccountId;
//    String toCurrency;
    BigDecimal toAmount;

    String requestId;

}
