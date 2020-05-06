package miui.cloud.exception;

public class CloudServiceFailureException extends Exception {
    private int errorCode;

    public CloudServiceFailureException() {
    }

    public CloudServiceFailureException(String message) {
        super(message);
    }

    public CloudServiceFailureException(Throwable cause) {
        super(cause);
    }

    public CloudServiceFailureException(Throwable cause, int errorCode2) {
        super(cause);
        this.errorCode = errorCode2;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}
