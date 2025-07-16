package com.example.application1.presenter.login;

import com.example.application1.model.LoginInteractor;
import com.example.application1.view.login.LoginView;

public class LoginPresenterImpl implements LoginPresenter {
    private LoginView loginView;
    private LoginInteractor loginInteractor;

    public LoginPresenterImpl(LoginView loginView) {
        this.loginView = loginView;
        this.loginInteractor = new LoginInteractor();
    }

    public LoginPresenterImpl(LoginView loginView, LoginInteractor loginInteractor) {
        this.loginView = loginView;
        this.loginInteractor = loginInteractor;
    }

    @Override
    public void validateUser(String email, String password) {
        if (email.isEmpty()) {
            loginView.setLoginFailed("Please enter your email");
        }
        else if(!email.matches("[a-zA-Z0-9]+@[a-z]+[.]com")) {
            loginView.setLoginFailed("Incorrect email format");
        }
        else if (password.isEmpty()) {
            loginView.setLoginFailed("Please enter your password");
        }
        else loginInteractor.sendLoginRequest(email, password, this);
    }

    @Override
    public void onLoginSuccess(String username) {
        loginView.setLoginSuccess(username);
    }

    @Override
    public void onLoginFailed(String message) {
        loginView.setLoginFailed(message);
    }

    @Override
    public void onLoginError(String errorMessage) {
        loginView.setLoginError(errorMessage);
    }
}