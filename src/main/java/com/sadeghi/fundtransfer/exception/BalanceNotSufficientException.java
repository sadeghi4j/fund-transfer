package com.sadeghi.fundtransfer.exception;

/**
 * Description of file goes here
 *
 * @author Ali Sadeghi
 * Created at 2022/10/28 - 6:32 PM
 */
public class BalanceNotSufficientException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Balance Not Sufficient";
    }

}
