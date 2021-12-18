package com.example.m2000example.callback;

public interface QueryResponse {

    void onSuccess       (String response);
    void onFail     (Throwable throwable);

}
