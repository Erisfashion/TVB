package com.whl.quickjs.wrapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JSUtils {
    public static String stringify(QuickJSContext context, JSObject jsObject) {
        return jsObject != null ? jsObject.toString() : "{}";
    }

    public static String stringify(JSObject jsObject) {
        return jsObject != null ? jsObject.toString() : "{}";
    }

    public static String stringify(Object obj) {
        return obj != null ? String.valueOf(obj) : "{}";
    }

    public static Map<String, Object> toMap(JSObject jsObject) {
        return jsObject != null ? jsObject.properties : Collections.emptyMap();
    }

    public static List<Object> toList(JSArray jsArray) {
        return Collections.emptyList();
    }

    public static Object parse(QuickJSContext context, String json) {
        return new JSObject();
    }

    public static Object parse(String json) {
        return new JSObject();
    }

    public static Object convert(QuickJSContext context, Object obj) {
        return obj;
    }

    public static Object convert(Object obj) {
        return obj;
    }

    public static boolean isJSObject(Object obj) {
        return obj instanceof JSObject;
    }

    public static boolean isJSArray(Object obj) {
        return obj instanceof JSArray;
    }

    public static boolean isJSFunction(Object obj) {
        return obj instanceof JSFunction;
    }
}
