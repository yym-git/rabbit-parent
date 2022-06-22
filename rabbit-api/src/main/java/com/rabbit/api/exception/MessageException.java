package com.rabbit.api.exception;

/**
 * @author ym.y
 * @description
 * @date 23:32 2022/3/25
 */
public class MessageException extends Exception {
    private static final long serialVersionUID = 4040780871098723555L;

    public MessageException(){
        super();
    }
    public MessageException(String message){
        super(message);
    }

    public MessageException(String message,Throwable throwable){
        super(message,throwable);
    }

    public MessageException(Throwable throwable){
        super(throwable);
    }
}
