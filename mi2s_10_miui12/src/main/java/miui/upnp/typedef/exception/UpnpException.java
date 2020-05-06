package miui.upnp.typedef.exception;

import miui.upnp.typedef.error.UpnpError;

public class UpnpException extends Exception {
    private int errorCode;

    public UpnpException(int errorCode2, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode2;
    }

    public UpnpException(UpnpError error, String detailMessage) {
        super(error.getMessage() + " -> " + detailMessage);
        this.errorCode = error.getCode();
    }

    public UpnpException(UpnpError error) {
        super(error.getMessage());
        this.errorCode = error.getCode();
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public UpnpError toUpnpError() {
        return new UpnpError(this.errorCode, getMessage());
    }
}
