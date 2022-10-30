package com.sadeghi.fundtransfer.exception;

/**
 * Description of file goes here
 *
 * @author Ali Sadeghi
 * Created at 2022/10/28 - 6:42 PM
 */
public class DuplicateRequestException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Duplicate Request";
    }

}
