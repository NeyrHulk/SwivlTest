package com.test.swivl.http;

public class ServerResponse {
    private String mInputStream;
    private String mErrorStream;


    public String getInputStream() {
        return mInputStream;
    }

    public void setInputStream(String inputStream) {
        this.mInputStream = inputStream;
    }

    public String getErrorStream() {
        return mErrorStream;
    }

    public void setErrorStream(String errorStream) {
        this.mErrorStream = errorStream;
    }
}
