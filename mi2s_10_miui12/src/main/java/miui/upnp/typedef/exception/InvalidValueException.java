package miui.upnp.typedef.exception;

public class InvalidValueException extends RuntimeException {
    public InvalidValueException(String s) {
        super(s);
    }

    public InvalidValueException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
