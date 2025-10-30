package com.example.sereincandle;

import java.security.cert.CertificateException;
import javax.net.ssl.*;
import okhttp3.OkHttpClient;

public class UnsafeOkHttpClient {

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Trust manager bỏ qua tất cả certificate
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[]{}; }
                    }
            };

            // SSL context dùng trust manager trên
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true); // bỏ qua hostname check

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}