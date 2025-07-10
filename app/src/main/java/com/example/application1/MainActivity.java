package com.example.application1;

import static android.widget.Toast.LENGTH_SHORT;

import static java.lang.Boolean.parseBoolean;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.application1.security.CustomTrustManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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


public class MainActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private String resultText, errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                boolean success = sendLoginRequest(email, password);
                handler.post(() -> {
                    if (success) {
                        Intent intent = new Intent(this, HomeActivity.class);

                        // ensure only 1 activity in the back stack
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, errorText != null ? errorText : "Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
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
            InputStream hashedKeyInput = getResources().openRawResource(R.raw.pubkey_hash);

            BufferedReader reader = new BufferedReader(new InputStreamReader(hashedKeyInput));
            String pinnedPublicKeyHash = reader.readLine().trim();
            reader.close();

            // Tạo CustomTrustManager
            CustomTrustManager pinningTm = new CustomTrustManager(defaultTm, pinnedPublicKeyHash);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{pinningTm}, null);

            return sslContext.getSocketFactory();

        } catch (Exception e) {
            throw new RuntimeException("Không thể khởi tạo SSL", e);
        }
    }

    private boolean sendLoginRequest(String email, String password) {
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
            Log.d("DEBUG", "Response Code: " + responseCode);

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

            if (responseCode >= 200 && responseCode < 300) {
                JSONObject resp = new JSONObject(responseBody);
                String loginStatus = resp.getString("loginStatus");

                if (parseBoolean(loginStatus)) {
                    resultText = resp.getString("userInfo");
                    return true;
                } else {
                    errorText = resp.getString("message");
                    return false;
                }
            }
            else {
                try {
                    JSONObject errorJson = new JSONObject(responseBody);
                    if (errorJson.has("message")) {
                        errorText = "Message: " + errorJson.getString("message");
                    } else {
                        errorText = "Login failed. Please check your credentials.";
                    }
                } catch (JSONException e) {
                    errorText = "Unexpected error: " + responseBody;
                }
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorText = "Server error: " + e.getMessage();
            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}