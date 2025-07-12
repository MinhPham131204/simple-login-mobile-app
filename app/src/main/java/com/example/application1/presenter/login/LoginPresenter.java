package com.example.application1.presenter.login;

public interface LoginPresenter {
    public void onLoginSuccess(String username);

    public void onLoginFailed(String message);

    public void onLoginError(String errorMessage);

    public void validateUser(String email, String password);
}