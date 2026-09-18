package com.whl.quickjs.wrapper;

public class UriUtil {
    public static String resolve(String base, String rel) {
        try {
            return com.google.android.exoplayer2.util.UriUtil.resolve(base, rel);
        } catch (Throwable t) {
            return rel != null ? rel : "";
        }
    }

    public static String resolve(String rel) {
        return rel != null ? rel : "";
    }

    public static String format(String uri) {
        return uri != null ? uri : "";
    }
}
