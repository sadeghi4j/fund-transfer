package com.sadeghi.fundtransfer.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Version;
import java.math.BigDecimal;

/**
 * Account Entity
 *
 * @author Ali Sadeghi
 * Created at 2022/10/28 - 5:52 PM
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account extends BaseEntity<Long> {

    String currency;
    @Column(precision = 19, scale = 4)
    BigDecimal balance;

//    @Version
//    int version;

}
