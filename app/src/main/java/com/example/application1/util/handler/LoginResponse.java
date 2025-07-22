package com.example.application1.util.handler;

public interface LoginResponse {
    void onSuccess(String userInfo);
    void onFailure(String message);
    void onError(String errorMessage);
}