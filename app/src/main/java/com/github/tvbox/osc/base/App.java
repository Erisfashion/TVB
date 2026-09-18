package com.github.tvbox.osc.base;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import com.github.tvbox.osc.util.HawkConfig;
import com.orhanobut.hawk.Hawk;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class App extends Application {
    private static App instance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // 关键：Dalvik 虚拟机在 Android 4.2 必须首先初始化 MultiDex
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Hawk.init(this).build();
        initLegacySSL();
        initDefaultConfigs();
    }

    public static App getInstance() {
        return instance;
    }

    /**
     * 解决 Android 4.2.2 默认不支持 TLS 1.2 以及老旧根证书过期的 HTTPS 握手失败问题
     */
    private void initLegacySSL() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置适合 x86 老平板的默认硬件/解码参数
     */
    private void initDefaultConfigs() {
        // 默认播放器: 1 为 IJK 播放器 (0:系统默认, 1:IJK, 2:Exo)
        if (!Hawk.contains(HawkConfig.PLAY_TYPE)) {
            Hawk.put(HawkConfig.PLAY_TYPE, 1);
        }
        // 默认解码格式: 软解码 (x86 CPU 软解能力强于 4.2 老系统失效的硬解码)
        if (!Hawk.contains(HawkConfig.IJK_CODEC)) {
            Hawk.put(HawkConfig.IJK_CODEC, "软解码");
        }
        // 默认主页推荐
        if (!Hawk.contains(HawkConfig.HOME_REC)) {
            Hawk.put(HawkConfig.HOME_REC, 1);
        }
    }
}
