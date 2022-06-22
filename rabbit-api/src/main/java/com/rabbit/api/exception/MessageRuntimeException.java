package com.rabbit.api.exception;

/**
 * @author ym.y
 * @description
 * @date 23:32 2022/3/25
 */
public class MessageRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 3349459531521265951L;

    public MessageRuntimeException() {
        super();
    }

    public MessageRuntimeException(String message) {
        super(message);
    }

    public MessageRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public MessageRuntimeException(Throwable throwable) {
        super(throwable);
    }
}
