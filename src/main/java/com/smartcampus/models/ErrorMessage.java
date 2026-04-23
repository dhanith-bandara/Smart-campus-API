package com.smartcampus.models;

public class ErrorMessage {
    private String error;
    private int status;
    private String documentation;

    public ErrorMessage() {}

    public ErrorMessage(String error, int status, String documentation) {
        this.error = error;
        this.status = status;
        this.documentation = documentation;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }
}
