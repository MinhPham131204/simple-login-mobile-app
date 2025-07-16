package com.example.application1.model;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

import android.os.Handler;
import android.os.Looper;

import com.example.application1.presenter.login.LoginPresenter;
import com.example.application1.util.connection.HttpClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.LooperMode;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(RobolectricTestRunner.class)
@LooperMode(PAUSED)
public class LoginInteractorTest {

    @Mock
    HttpClient mockHttpClient;

    @Mock
    LoginPresenter mockPresenter;

    private LoginInteractor loginInteractor;
    private AutoCloseable closeable;

    @Before
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this); // BẮT BUỘC
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper()); // cần giả lập nếu chạy trên JVM
        loginInteractor = new LoginInteractor(executor, handler, mockHttpClient);
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(mockPresenter);
        closeable.close();
    }

    @Test
    public void testLoginSuccess() throws Exception {
        // mock a response after make request successfully
        String fakeResponse = "{\"loginStatus\":\"true\", \"userInfo\":\"John Doe\"}";
        when(mockHttpClient.request(anyString(), anyString())).thenReturn(fakeResponse);

        // call sendLoginRequest method to test
        loginInteractor.sendLoginRequest("test@example.com", "123456", mockPresenter);

        Thread.sleep(500);

        Shadows.shadowOf(Looper.getMainLooper()).idle();

        verify(mockPresenter).onLoginSuccess("John Doe");
    }

    @Test
    public void testLoginFailed() throws Exception {
        String fakeResponse = "{\"loginStatus\":\"false\", \"message\":\"Invalid credentials\"}";
        when(mockHttpClient.request(anyString(), anyString())).thenReturn(fakeResponse);

        loginInteractor.sendLoginRequest("wrong@example.com", "wrongpass", mockPresenter);

        Thread.sleep(500);

        Shadows.shadowOf(Looper.getMainLooper()).idle();

        verify(mockPresenter).onLoginFailed("Invalid credentials");
    }

    @Test
    public void testNetworkError() throws Exception {
        when(mockHttpClient.request(anyString(), anyString())).thenThrow(new IOException("Timeout"));

        loginInteractor.sendLoginRequest("test@example.com", "123456", mockPresenter);

        Thread.sleep(500);

        Shadows.shadowOf(Looper.getMainLooper()).idle();

        verify(mockPresenter).onLoginError("Timeout");
    }

    @Test
    public void testJsonMissingLoginStatus() throws Exception {
        String response = "{\"message\":\"Some error\"}";
        when(mockHttpClient.request(anyString(), anyString())).thenReturn(response);

        loginInteractor.sendLoginRequest("test@example.com", "123456", mockPresenter);

        Thread.sleep(500);

        Shadows.shadowOf(Looper.getMainLooper()).idle();

        // verify onLoginError() is called and exception string pass to this method contain word "loginStatus"
        verify(mockPresenter).onLoginError(Mockito.contains("loginStatus"));
    }

    @Test
    public void testMissingUserInfo() throws Exception {
        String response = "{\"loginStatus\":\"true\"}";
        when(mockHttpClient.request(anyString(), anyString())).thenReturn(response);

        loginInteractor.sendLoginRequest("test@example.com", "123456", mockPresenter);

        Thread.sleep(500);

        Shadows.shadowOf(Looper.getMainLooper()).idle();

        verify(mockPresenter).onLoginError(Mockito.contains("userInfo"));
    }

    @Test
    public void testMissingFailureMessage() throws Exception {
        String response = "{\"loginStatus\":\"false\"}";
        when(mockHttpClient.request(anyString(), anyString())).thenReturn(response);

        loginInteractor.sendLoginRequest("test@example.com", "123456", mockPresenter);

        Thread.sleep(500);

        Shadows.shadowOf(Looper.getMainLooper()).idle();

        verify(mockPresenter).onLoginError(Mockito.contains("message"));
    }
}
