package com.whl.quickjs.wrapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JSUtils<T> {

    public JSObject toObj(QuickJSContext ctx, Map<String, T> map) {
        JSObject obj = ctx != null ? ctx.createJSObject() : new JSObject();
        if (map != null) {
            for (Map.Entry<String, T> entry : map.entrySet()) {
                obj.set(entry.getKey(), entry.getValue());
            }
        }
        return obj;
    }

    public JSArray toArray(QuickJSContext ctx, Collection<T> collection) {
        JSArray array = ctx != null ? ctx.createJSArray() : new JSArray();
        if (collection != null) {
            int i = 0;
            for (T item : collection) {
                array.set(i++, item);
            }
        }
        return array;
    }

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
        return context != null ? context.parse(json) : new JSObject();
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
