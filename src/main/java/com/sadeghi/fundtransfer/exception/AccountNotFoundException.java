package com.sadeghi.fundtransfer.exception;

/**
 * Description of file goes here
 *
 * @author Ali Sadeghi
 * Created at 2022/10/28 - 6:28 PM
 */
public class AccountNotFoundException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Account Not Found";
    }

}
