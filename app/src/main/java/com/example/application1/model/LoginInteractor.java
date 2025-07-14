package com.example.application1.model;
import static java.lang.Boolean.parseBoolean;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.application1.presenter.login.LoginPresenter;
import com.example.application1.security.CustomTrustManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyStore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class LoginInteractor {
    private final ExecutorService executor;

    public LoginInteractor() {
        executor = Executors.newSingleThreadExecutor();
    }

    private SSLSocketFactory getSSLSocketFactory() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            // Tạo TrustManager từ KeyStore
            X509TrustManager defaultTm = null;
            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    defaultTm = (X509TrustManager) tm;
                    break;
                }
            }
            if (defaultTm == null) {
                throw new IllegalStateException("Không tìm thấy X509TrustManager mặc định");
            }

            // Đọc dữ liệu trong file pubkey_hash.txt chứa public key đã được băm (bằng sha256) và lưu trữ dưới dạng Base64
//            FileInputStream hashedKeyInput = new FileInputStream("pubkey_hash.txt");
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(hashedKeyInput));
//            String pinnedPublicKeyHash = reader.readLine().trim();
//            reader.close();
//
//            hashedKeyInput.close();

            String pinnedPublicKeyHash = "UYMDTDBVvHngAyAeSLdKpe941pLJhs7yyTbkJQ/VLFs=";

            // Tạo CustomTrustManager
            CustomTrustManager pinningTm = new CustomTrustManager(defaultTm, pinnedPublicKeyHash);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{pinningTm}, null);

            return sslContext.getSocketFactory();

        } catch (Exception e) {
            throw new RuntimeException("Không thể khởi tạo SSL", e);
        }
    }

    public void sendLoginRequest(String email, String password, LoginPresenter presenter) {
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            HttpsURLConnection conn = null;
            try {
                URL url = new URL("https://192.168.1.18/login");
                conn = (HttpsURLConnection) url.openConnection();

                conn.setSSLSocketFactory(getSSLSocketFactory());

                // Thiết lập phương thức và headers
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                // Tạo JSON body
                JSONObject json = new JSONObject();
                json.put("email", email);
                json.put("password", password);
                byte[] postData = json.toString().getBytes();

                // Gửi dữ liệu
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData);
                    os.flush();
                }

                int responseCode = conn.getResponseCode();

                // Đọc phản hồi
                InputStream inputStream = (responseCode >= 200 && responseCode < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                StringBuilder response = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                String responseBody = response.toString();

                Log.d("DEBUG", "response code: " + responseCode);

                if (responseCode >= 200 && responseCode < 300) {
                    JSONObject resp = new JSONObject(responseBody);
                    String loginStatus = resp.getString("loginStatus");

                    handler.post(() -> {
                        if (parseBoolean(loginStatus)) {
                            try {
                                presenter.onLoginSuccess(resp.getString("userInfo"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            try {
                                presenter.onLoginFailed(resp.getString("message"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }

            } catch (Exception e) {
                handler.post(() -> presenter.onLoginError(e.getMessage()));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                    executor.shutdown();
                }
            }
        });
    }
}