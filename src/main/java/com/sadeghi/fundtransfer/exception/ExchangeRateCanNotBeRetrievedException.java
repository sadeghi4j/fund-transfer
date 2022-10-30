package com.sadeghi.fundtransfer.exception;

/**
 * Description of file goes here
 *
 * @author Ali Sadeghi
 * Created at 2022/10/28 - 9:50 PM
 */
public class ExchangeRateCanNotBeRetrievedException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Exchange Rate Can Not Be Retrieved";
    }
}
