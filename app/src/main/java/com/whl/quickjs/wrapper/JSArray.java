package com.whl.quickjs.wrapper;

import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;

public class JSArray extends JSObject {
    private final List<Object> items = new ArrayList<>();

    public int length() {
        return items.size();
    }

    public int size() {
        return items.size();
    }

    public void push(int val) {
        items.add(val);
    }

    public void push(Object val) {
        items.add(val);
    }

    public void push(Object... args) {
        if (args != null) {
            for (Object arg : args) {
                items.add(arg);
            }
        }
    }

    public void add(Object val) {
        items.add(val);
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

    public JSONArray toJsonArray() {
        JSONArray jsonArray = new JSONArray();
        for (Object item : items) {
            if (item instanceof JSObject) {
                jsonArray.put(((JSObject) item).toJsonObject());
            } else if (item instanceof JSArray) {
                jsonArray.put(((JSArray) item).toJsonArray());
            } else {
                jsonArray.put(item);
            }
        }
        return jsonArray;
    }
}
