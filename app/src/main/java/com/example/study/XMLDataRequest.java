package com.example.study;

public class XMLDataRequest {

    public XMLDataRequest() {
    }

    public XMLDataRequest(String requestString) {
        this.requestString = requestString;
    }

    private String requestString;
    private String requestType;

    public String getRequestString() {
        return requestString;
    }

    public void setRequestString(String requestString) {
        this.requestString = requestString;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}
