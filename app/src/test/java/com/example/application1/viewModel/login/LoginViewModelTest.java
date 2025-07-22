package com.example.application1.viewModel.login;

import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.example.application1.model.LoginInteractor;
import com.example.application1.util.handler.LoginResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginViewModelTest {

    // tạo executor để chạy các tác vụ LiveData một cách đồng bộ
    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private LoginViewModel viewModel;
    private LoginInteractor mockInteractor;

    @Before
    public void setUp() {
        mockInteractor = mock(LoginInteractor.class);
        viewModel = new LoginViewModel(mockInteractor);
    }

    @Test
    public void testEmptyEmail() {
        viewModel.getEmail().setValue("");
        viewModel.getPassword().setValue("password");

        Observer<String> messageObserver = mock(Observer.class);
        viewModel.getMessage().observeForever(messageObserver);

        viewModel.validateUser();

        verify(messageObserver).onChanged("Please enter your email");
    }

    @Test
    public void testInvalidEmail() {
        viewModel.getEmail().setValue("abc123");
        viewModel.getPassword().setValue("password");

        Observer<String> messageObserver = mock(Observer.class);
        viewModel.getMessage().observeForever(messageObserver);

        viewModel.validateUser();

        verify(messageObserver).onChanged("Incorrect email format");
    }

    @Test
    public void testEmptyPassword() {
        viewModel.getEmail().setValue("user@example.com");
        viewModel.getPassword().setValue("");

        Observer<String> messageObserver = mock(Observer.class);
        viewModel.getMessage().observeForever(messageObserver);

        viewModel.validateUser();

        verify(messageObserver).onChanged("Please enter your password");
    }

    @Test
    public void testLoginSuccess() {
        // Setup
        String email = "user@example.com";
        String password = "123456";
        String userInfo = "John Doe";

        viewModel.getEmail().setValue(email);
        viewModel.getPassword().setValue(password);

        // Chặn gọi sendLoginRequest → gọi callback ngay lập tức
        doAnswer(invocation -> {
            LoginResponse callback = invocation.getArgument(2);
            callback.onSuccess(userInfo);
            return null;
        }).when(mockInteractor).sendLoginRequest(eq(email), eq(password), any());

        Observer<String> successObserver = mock(Observer.class);
        viewModel.getLoginSuccess().observeForever(successObserver);

        viewModel.validateUser();

        verify(successObserver).onChanged(userInfo);
    }

    @Test
    public void testLoginFailure() {
        String email = "user@example.com";
        String password = "wrongpass";

        viewModel.getEmail().setValue(email);
        viewModel.getPassword().setValue(password);

        doAnswer(invocation -> {
            LoginResponse callback = invocation.getArgument(2);
            callback.onFailure("Invalid credentials");
            return null;
        }).when(mockInteractor).sendLoginRequest(eq(email), eq(password), any());

        Observer<String> messageObserver = mock(Observer.class);
        viewModel.getMessage().observeForever(messageObserver);

        viewModel.validateUser();

        verify(messageObserver).onChanged("Login failed: Invalid credentials");
    }

    @Test
    public void testLoginError() {
        String email = "user@example.com";
        String password = "123456";

        viewModel.getEmail().setValue(email);
        viewModel.getPassword().setValue(password);

        doAnswer(invocation -> {
            LoginResponse callback = invocation.getArgument(2);
            callback.onError("Network error");
            return null;
        }).when(mockInteractor).sendLoginRequest(eq(email), eq(password), any());

        Observer<String> messageObserver = mock(Observer.class);
        viewModel.getMessage().observeForever(messageObserver);

        viewModel.validateUser();

        verify(messageObserver).onChanged("Error: Network error");
    }
}