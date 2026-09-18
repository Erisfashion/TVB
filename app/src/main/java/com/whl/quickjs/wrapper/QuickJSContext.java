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

    public JSFunction createNewJSFunction(JSCallFunction function) {
        return new JSFunction();
    }

    public void registerJavaMethod(Object target, String methodName) {}
    public void registerJavaMethod(JSCallFunction handler, String name) {}
    public void registerJavaMethod(Object target, String methodName, String jsMethodName) {}
    public void registerAll(Object target) {}
    public void initConsole() {}
    public void throwJSException(String message) {}
    public Object getUndefined() { return null; }
    public Object getNull() { return null; }
    public String stringify(JSObject jsObject) { return "{}"; }
}
