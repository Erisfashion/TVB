package com.whl.quickjs.wrapper;

import java.util.ArrayList;
import java.util.List;

public class JSArray extends JSObject {
    private final List<Object> items = new ArrayList<>();

    public int length() {
        return items.size();
    }

    public Object get(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    public void set(int index, Object val) {
        while (items.size() <= index) {
            items.add(null);
        }
        items.set(index, val);
    }

    public JSObject getJSObject(int index) {
        Object val = get(index);
        return val instanceof JSObject ? (JSObject) val : null;
    }

    public JSArray getJSArray(int index) {
        Object val = get(index);
        return val instanceof JSArray ? (JSArray) val : null;
    }

    public String getString(int index) {
        Object val = get(index);
        return val != null ? String.valueOf(val) : null;
    }

    public Integer getInteger(int index) {
        Object val = get(index);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        return null;
    }

    public int getInt(int index) {
        Integer val = getInteger(index);
        return val != null ? val : 0;
    }

    public Boolean getBoolean(int index) {
        Object val = get(index);
        return val instanceof Boolean ? (Boolean) val : false;
    }

    public JSFunction getJSFunction(int index) {
        Object val = get(index);
        return val instanceof JSFunction ? (JSFunction) val : null;
    }
}
