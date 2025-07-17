package com.example.application1.util.connection;

import com.example.application1.R;
import com.example.application1.util.security.CustomTrustManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class RealHttpClient implements HttpClient{
    private static RealHttpClient instance;

    private RealHttpClient() {}

    public static RealHttpClient getInstance() {
        if(instance == null) {
            instance = new RealHttpClient();
        }
        return instance;
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
    public String request(String urlString, String jsonBody) {
        try {
            URL url = new URL(urlString);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            conn.setSSLSocketFactory(getSSLSocketFactory());

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
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

            return response.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
