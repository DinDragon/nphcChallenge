package com.nphc.service.Employee.Util;

public class MessageBuilder {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageBuilder(String message) {
        this.message = message;
    }
}
