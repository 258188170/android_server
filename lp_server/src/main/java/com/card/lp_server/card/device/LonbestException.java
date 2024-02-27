package com.card.lp_server.card.device;

/**
 * 自定义Lonbest异常
 */
public class LonbestException extends RuntimeException {
    public LonbestException() {
    }

    public LonbestException(String message) {
        super(message);
    }

    public LonbestException(String message, Throwable cause) {
        super(message, cause);
    }
}
