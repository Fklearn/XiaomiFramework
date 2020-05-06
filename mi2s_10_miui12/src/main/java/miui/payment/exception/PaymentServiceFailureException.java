package miui.payment.exception;

import android.os.Bundle;

public class PaymentServiceFailureException extends Exception {
    private static final long serialVersionUID = 1;
    private int mErrorCode;
    private Bundle mErrorResult;

    public PaymentServiceFailureException(int error, String message) {
        this(error, message, (Bundle) null);
    }

    public PaymentServiceFailureException(int error, String message, Bundle result) {
        super(message);
        this.mErrorCode = error;
        if (result == null) {
            this.mErrorResult = new Bundle();
        } else {
            this.mErrorResult = result;
        }
    }

    public int getError() {
        return this.mErrorCode;
    }

    public Bundle getErrorResult() {
        return this.mErrorResult;
    }
}
