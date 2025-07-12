package com.example.application1.view.login;

public interface LoginView {
    public void setLoginSuccess(String username);

    public void setLoginFailed(String message);

    public void setLoginError(String errorMessage);
}