package com.example.application1.model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

import android.os.Handler;
import android.os.Looper;

import com.example.application1.util.connection.HttpClient;
import com.example.application1.util.handler.LoginResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.LooperMode;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(RobolectricTestRunner.class)
@LooperMode(PAUSED)
public class LoginInteractorTest {

    private HttpClient mockHttpClient;
    private LoginInteractor loginInteractor;

    private LoginResponse loginResponse;

    @Before
    public void setUp() {
        mockHttpClient = mock(HttpClient.class);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        loginInteractor = new LoginInteractor(executor, handler, mockHttpClient);
        loginResponse = mock(LoginResponse.class);
    }

    @Test
    public void testLoginSuccess() throws Exception {
        // mock a response after make request successfully
        String fakeResponse = "{\"loginStatus\":\"true\", \"userInfo\":\"John Doe\"}";
        when(mockHttpClient.request(anyString(), anyString())).thenReturn(fakeResponse);

        // call sendLoginRequest method to test
        loginInteractor.sendLoginRequest("test@example.com", "123456", loginResponse);

        Thread.sleep(500);

        Shadows.shadowOf(Looper.getMainLooper()).idle();

        verify(loginResponse).onSuccess("John Doe");
        verify(loginResponse, never()).onFailure(any());
        verify(loginResponse, never()).onError(any());
    }

    @Test
    public void testLoginFailed() throws Exception {
        String fakeResponse = "{\"loginStatus\":\"false\", \"message\":\"Invalid credentials\"}";
        when(mockHttpClient.request(anyString(), anyString())).thenReturn(fakeResponse);

        loginInteractor.sendLoginRequest("wrong@example.com", "wrongpass", loginResponse);

        Thread.sleep(500);

        Shadows.shadowOf(Looper.getMainLooper()).idle();

        verify(loginResponse).onFailure("Invalid credentials");
        verify(loginResponse, never()).onSuccess(any());
        verify(loginResponse, never()).onError(any());
    }

    @Test
    public void testNetworkError() throws Exception {
        when(mockHttpClient.request(anyString(), anyString())).thenThrow(new IOException("Timeout"));

        loginInteractor.sendLoginRequest("test@example.com", "123456", loginResponse);

        Thread.sleep(500);

        Shadows.shadowOf(Looper.getMainLooper()).idle();

        verify(loginResponse).onError("Timeout");
        verify(loginResponse, never()).onSuccess(any());
        verify(loginResponse, never()).onFailure(any());
    }

    @Test
    public void testJsonMissingLoginStatus() throws Exception {
        String response = "{\"message\":\"Some error\"}";
        when(mockHttpClient.request(anyString(), anyString())).thenReturn(response);

        loginInteractor.sendLoginRequest("test@example.com", "123456", loginResponse);

        Thread.sleep(500);

        Shadows.shadowOf(Looper.getMainLooper()).idle();

        // verify onLoginError() is called and exception string pass to this method contain word "loginStatus"
        verify(loginResponse).onError(Mockito.contains("loginStatus"));
        verify(loginResponse, never()).onSuccess(any());
        verify(loginResponse, never()).onFailure(any());
    }

    @Test
    public void testMissingUserInfo() throws Exception {
        String response = "{\"loginStatus\":\"true\"}";
        when(mockHttpClient.request(anyString(), anyString())).thenReturn(response);

        loginInteractor.sendLoginRequest("test@example.com", "123456", loginResponse);

        Thread.sleep(500);

        Shadows.shadowOf(Looper.getMainLooper()).idle();

        verify(loginResponse).onError(Mockito.contains("userInfo"));
        verify(loginResponse, never()).onSuccess(any());
        verify(loginResponse, never()).onFailure(any());
    }

    @Test
    public void testMissingFailureMessage() throws Exception {
        String response = "{\"loginStatus\":\"false\"}";
        when(mockHttpClient.request(anyString(), anyString())).thenReturn(response);

        loginInteractor.sendLoginRequest("test@example.com", "123456", loginResponse);

        Thread.sleep(500);

        Shadows.shadowOf(Looper.getMainLooper()).idle();

        verify(loginResponse).onError(Mockito.contains("message"));
        verify(loginResponse, never()).onSuccess(any());
        verify(loginResponse, never()).onFailure(any());
    }
}
