package com.whl.quickjs.wrapper;

public class QuickJSContext {
    public static QuickJSContext create() {
        return new QuickJSContext();
    }

    public void destroy() {}

    public Object evaluate(String script) {
        return null;
    }

    public Object evaluate(String script, String filename) {
        return null;
    }

    public JSObject getGlobalObject() {
        return new JSObject();
    }

    public JSObject createNewJSObject() {
        return new JSObject();
    }

    public JSArray createNewJSArray() {
        return new JSArray();
    }
}
