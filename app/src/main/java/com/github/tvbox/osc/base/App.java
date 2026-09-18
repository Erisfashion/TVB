package com.github.tvbox.osc.base;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import com.github.tvbox.osc.callback.EmptyCallback;
import com.github.tvbox.osc.callback.LoadingCallback;
import com.kingja.loadsir.core.LoadSir;
import com.lzy.okgo.OkGo;
import com.orhanobut.hawk.Hawk;
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
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        try {
            Hawk.init(this).build();
        } catch (Throwable ignored) {}
        initLegacySSL();
        initDefaultConfigs();

        try {
            LoadSir.beginBuilder()
                    .addCallback(new EmptyCallback())
                    .addCallback(new LoadingCallback())
                    .setDefaultCallback(LoadingCallback.class)
                    .commit();
        } catch (Throwable ignored) {}

        try {
            OkGo.getInstance().init(this);
        } catch (Throwable ignored) {}
    }

    public static App getInstance() {
        return instance;
    }

    public static App get() {
        return instance;
    }

    public static Context getContext() {
        return instance == null ? null : instance.getApplicationContext();
    }

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
        } catch (Throwable ignored) {}
    }

    private void initDefaultConfigs() {
        try {
            if (!Hawk.contains("play_type")) {
                Hawk.put("play_type", 1);
            }
            if (!Hawk.contains("ijk_codec")) {
                Hawk.put("ijk_codec", "软解码");
            }
            if (!Hawk.contains("home_rec")) {
                Hawk.put("home_rec", 1);
            }
        } catch (Throwable ignored) {}
    }
}
