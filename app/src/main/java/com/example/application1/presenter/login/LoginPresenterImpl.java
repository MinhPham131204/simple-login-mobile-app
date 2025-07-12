package com.example.application1.presenter.login;

import com.example.application1.model.LoginInteractor;
import com.example.application1.view.login.LoginView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginPresenterImpl implements LoginPresenter {
    private LoginView loginView;
    private LoginInteractor loginInteractor;

    public LoginPresenterImpl(LoginView loginView) {
        this.loginView = loginView;
        this.loginInteractor = new LoginInteractor();
    }

    @Override
    public void validateUser(String email, String password) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            loginInteractor.sendLoginRequest(email, password, this);
        });
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