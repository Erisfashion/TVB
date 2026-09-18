package com.whl.quickjs.wrapper;

import java.util.HashMap;
import java.util.Map;

public class JSObject {
    protected final Map<String, Object> properties = new HashMap<>();
    private QuickJSContext context;

    public JSObject() {}

    public JSObject(QuickJSContext context) {
        this.context = context;
    }

    public QuickJSContext getContext() {
        if (context == null) {
            context = QuickJSContext.create();
        }
        return context;
    }

    public void bind(Object target) {}

    public Object get(String key) {
        return properties.get(key);
    }

    public String getString(String key) {
        Object val = get(key);
        return val == null ? null : String.valueOf(val);
    }

    public Integer getInteger(String key) {
        Object val = get(key);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        return null;
    }

    public int getInt(String key) {
        Integer val = getInteger(key);
        return val == null ? 0 : val;
    }

    public long getLong(String key) {
        Object val = get(key);
        if (val instanceof Number) {
            return ((Number) val).longValue();
        }
        return 0L;
    }

    public double getDouble(String key) {
        Object val = get(key);
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        return 0.0;
    }

    public Boolean getBoolean(String key) {
        Object val = get(key);
        return val instanceof Boolean ? (Boolean) val : false;
    }

    public JSObject getJSObject(String key) {
        Object val = get(key);
        return val instanceof JSObject ? (JSObject) val : null;
    }

    public JSArray getJSArray(String key) {
        Object val = get(key);
        return val instanceof JSArray ? (JSArray) val : null;
    }

    public JSFunction getJSFunction(String key) {
        Object val = get(key);
        return val instanceof JSFunction ? (JSFunction) val : null;
    }

    public void set(String key, Object val) {
        properties.put(key, val);
    }

    public void set(String key, JSCallFunction val) {
        properties.put(key, val);
    }

    public void put(String key, Object val) {
        properties.put(key, val);
    }

    public void registerJavaMethod(Object target, String methodName) {}
    public void registerJavaMethod(JSCallFunction handler, String name) {}
    public void registerAll(Object target) {}
}
