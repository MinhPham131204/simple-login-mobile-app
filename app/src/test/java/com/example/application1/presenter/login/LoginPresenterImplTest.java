package com.example.application1.presenter.login;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import com.example.application1.model.LoginInteractor;
import com.example.application1.view.login.LoginView;

public class LoginPresenterImplTest {

    private LoginView loginView;
    private LoginInteractor loginInteractor;
    private LoginPresenterImpl loginPresenter;

    @Before
    public void setUp() {
        loginView = mock(LoginView.class);
        loginInteractor = mock(LoginInteractor.class);
        loginPresenter = new LoginPresenterImpl(loginView, loginInteractor);
    }

    @Test
    public void testOnLoginSuccess() {
        String username = "john_doe";

        loginPresenter.onLoginSuccess(username);

        verify(loginView).setLoginSuccess(username);
    }

    @Test
    public void testOnLoginFailed() {
        String message = "Invalid info";

        loginPresenter.onLoginFailed(message);

        verify(loginView).setLoginFailed(message);
    }

    @Test
    public void testOnLoginError() {
        String errorMessage = "Network error";

        loginPresenter.onLoginError(errorMessage);

        verify(loginView).setLoginError(errorMessage);
    }

    @Test
    public void testValidateUser() {
        String email = "email@example.com";
        String password = "password123";

        loginPresenter.validateUser(email, password);

        verify(loginInteractor).sendLoginRequest(email, password, loginPresenter);
    }

    @Test
    public void testOnLoginFailed_withEmptyMessage() {
        loginPresenter.onLoginFailed("");
        verify(loginView).setLoginFailed("");
    }

    @Test
    public void testOnLoginError_withNullErrorMessage() {
        loginPresenter.onLoginError(null);
        verify(loginView).setLoginError(null);
    }

    @Test
    public void testValidateUser_withWrongFormatEmail_case1() {
        loginPresenter.validateUser("  @gmail.com", "password");
        verify(loginView).setLoginFailed("Incorrect email format");
    }

    @Test
    public void testValidateUser_withWrongFormatEmail_case2() {
        loginPresenter.validateUser("abc@gmail123.com", "password");
        verify(loginView).setLoginFailed("Incorrect email format");
    }

    @Test
    public void testValidateUser_withEmptyEmail() {
        loginPresenter.validateUser("", "password");
        verify(loginView).setLoginFailed("Please enter your email");
    }

    @Test
    public void testValidateUser_withEmptyPassword() {
        loginPresenter.validateUser("email@example.com", "");
        verify(loginView).setLoginFailed("Please enter your password");
    }
}
