package com.example.application1.security;

import android.util.Base64;

import javax.net.ssl.*;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class CustomTrustManager implements X509TrustManager {
    private final String hashedPublicKey;

    private final X509TrustManager defaultTrustManager;

    public CustomTrustManager(X509TrustManager defaultTrustManager, String pinnedHash) {
        this.defaultTrustManager = defaultTrustManager;
        this.hashedPublicKey = pinnedHash;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        // empty
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        // Lấy public key từ chứng chỉ đầu tiên (gốc server)
        X509Certificate cert = chain[0];

        byte[] pubKeyEncoded = cert.getPublicKey().getEncoded();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] sha256 = md.digest(pubKeyEncoded);
            String base64Sha256 = Base64.encodeToString(sha256, Base64.NO_WRAP);

            if (!hashedPublicKey.equals(base64Sha256)) {
                throw new CertificateException("Public key pinning failure!");
            }

        } catch (Exception e) {
            throw new CertificateException("Failed to verify public key pinning", e);
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return defaultTrustManager.getAcceptedIssuers();
    }
}
