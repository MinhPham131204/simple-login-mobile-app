package com.example.application1.viewModel.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application1.model.LoginInteractor;
import com.example.application1.util.handler.LoginResponse;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<String> email = new MutableLiveData<>("");
    private final MutableLiveData<String> password = new MutableLiveData<>("");
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<String> loginSuccess = new MutableLiveData<>();

    private final LoginInteractor loginInteractor;

    public LoginViewModel() {
        this.loginInteractor = new LoginInteractor();
    }

    public LoginViewModel(LoginInteractor loginInteractor) {
        this.loginInteractor = loginInteractor;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    // return LiveData type to hide update data from another class
    public LiveData<String> getMessage() {
        return message;
    }

    public LiveData<String> getLoginSuccess() {
        return loginSuccess;
    }

    public void validateUser() {
        String emailValue = email.getValue();
        String passwordValue = password.getValue();

        if (emailValue == null || emailValue.isEmpty()) {
            message.setValue("Please enter your email");
        } else if (!emailValue.matches("[a-zA-Z0-9._%+-]+@[a-z]+\\.[a-z]{2,4}")) {
            message.setValue("Incorrect email format");
        } else if (passwordValue == null || passwordValue.isEmpty()) {
            message.setValue("Please enter your password");
        } else {
            loginInteractor.sendLoginRequest(emailValue, passwordValue, new LoginResponse() {
                @Override
                public void onSuccess(String userInfo) {
                    loginSuccess.setValue(userInfo); // thông báo thành công
                }

                @Override
                public void onFailure(String msg) {
                    message.setValue("Login failed: " + msg);
                }

                @Override
                public void onError(String errorMessage) {
                    message.setValue("Error: " + errorMessage);
                }
            });
        }
    }
}

