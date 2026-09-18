package com.github.tvbox.osc.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;
import com.github.tvbox.osc.bean.VodInfo;
import com.github.tvbox.osc.callback.EmptyCallback;
import com.github.tvbox.osc.callback.LoadingCallback;
import com.kingja.loadsir.core.LoadSir;
import com.lzy.okgo.OkGo;
import com.orhanobut.hawk.Hawk;
import com.p2p.P2PClass;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class App extends Application {
    private static App instance;
    private String dashData = "";
    private VodInfo vodInfo;

    public static String burl = "";
    private static P2PClass p2p;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // 关键配置：解决 Android 4.2 解析 VectorDrawable 导致的 InflateException 闪退
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // 崩溃自捕获：将异常写入存储卡根目录或应用私有目录，方便随时取样
        initCrashHandler();

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

    private void initCrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Log.e("TVBoxCrash", "FATAL EXCEPTION", ex);
                try {
                    File crashFile = new File(getExternalFilesDir(null), "tvbox_crash.log");
                    if (!crashFile.exists()) {
                        crashFile = new File(Environment.getExternalStorageDirectory(), "tvbox_crash.log");
                    }
                    PrintWriter pw = new PrintWriter(new FileWriter(crashFile, false));
                    ex.printStackTrace(pw);
                    pw.flush();
                    pw.close();
                } catch (Throwable ignored) {}
                System.exit(1);
            }
        });
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

    public static P2PClass getp2p() {
        if (p2p == null) {
            try {
                String path = "";
                if (instance != null) {
                    File cache = instance.getExternalCacheDir();
                    if (cache == null) cache = instance.getCacheDir();
                    if (cache != null) path = cache.getAbsolutePath();
                }
                p2p = new P2PClass(path);
            } catch (Throwable t) {
                p2p = new P2PClass("");
            }
        }
        return p2p;
    }

    public static void setp2p(P2PClass p) {
        p2p = p;
    }

    public VodInfo getVodInfo() {
        return vodInfo;
    }

    public void setVodInfo(VodInfo vodInfo) {
        this.vodInfo = vodInfo;
    }

    public String getDashData() {
        return dashData == null ? "" : dashData;
    }

    public void setDashData(String dashData) {
        this.dashData = dashData;
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
