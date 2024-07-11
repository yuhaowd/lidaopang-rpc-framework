package com.zshs.rpcframeworksimple.exception;

public class RpcException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private int errorCode;

    public RpcException() {
        super();
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public RpcException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "RpcException{" +
                "errorCode=" + errorCode +
                ", message=" + getMessage() +
                '}';
    }
}
