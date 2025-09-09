package com.omgservers.tenants.errors;

public class ResourceConflict extends RuntimeException {
    public ResourceConflict() {
        super();
    }

    public ResourceConflict(String message) {
        super(message);
    }

    public ResourceConflict(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceConflict(Throwable cause) {
        super(cause);
    }

    protected ResourceConflict(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
