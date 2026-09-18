package com.whl.quickjs.android;

public class QuickJSLoader {
    public static void init() {
        // 彻底移除 System.loadLibrary("quickjs-android") 避免 x86 设备抛出 UnsatisfiedLinkError
    }

    public static boolean isSupported() {
        return true;
    }
}
