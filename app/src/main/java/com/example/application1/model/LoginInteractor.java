package com.example.application1.model;

import android.os.Handler;
import android.os.Looper;

import com.example.application1.presenter.login.LoginPresenter;
import com.example.application1.util.connection.HttpClient;
import com.example.application1.util.connection.RealHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginInteractor {
    private final ExecutorService executor;
    private final Handler handler;
    private final HttpClient httpClient;

    public LoginInteractor() {
        handler = new Handler(Looper.getMainLooper());
        executor = Executors.newSingleThreadExecutor();
        httpClient = RealHttpClient.getInstance();
    }

    public LoginInteractor(ExecutorService executor, Handler handler, HttpClient httpClient) {
        this.executor = executor;
        this.handler = handler;
        this.httpClient = httpClient;
    }

    public void sendLoginRequest(String email, String password, LoginPresenter presenter) {
        executor.execute(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("email", email);
                json.put("password", password);
                String requestBody = json.toString();

                String responseBody = httpClient.request("https://192.168.1.18/login", requestBody);
                JSONObject resp = new JSONObject(responseBody);
                boolean loginStatus = Boolean.parseBoolean(resp.getString("loginStatus"));

                handler.post(() -> {
                    try {
                        if (loginStatus) {
                            presenter.onLoginSuccess(resp.getString("userInfo"));
                        } else {
                            presenter.onLoginFailed(resp.getString("message"));
                        }
                    } catch (JSONException e) {
                        // exception string: No value for userInfo (message)
                        presenter.onLoginError(e.getMessage());
                    }
                });
            } catch (Exception e) {
                handler.post(() -> presenter.onLoginError(e.getMessage()));
            }
        });
    }
}