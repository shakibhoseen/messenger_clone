package com.example.chatapp.Model;

public class LastMsg {

    private String id;
    private String message;
    private boolean isseen;

    public LastMsg() {
    }

    public LastMsg(String id, String message, boolean isseen) {
        this.id = id;
        this.message = message;
        this.isseen = isseen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
}
