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
}
