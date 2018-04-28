package com.allen.customergraphview.exception;

/**
 * 节点图异常类
 *
 * @author Renjy
 */
public class GraphException extends Exception {
    private String message;

    public GraphException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
