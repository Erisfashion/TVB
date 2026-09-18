package com.whl.quickjs.wrapper;

public class QuickJSContext {

    public interface DefaultModuleLoader {
        default String getModuleStringCode(String moduleName) { return null; }
        default String getModuleStringCode(String moduleBaseName, String moduleName) { return null; }
        default String getModuleCode(String moduleName) { return null; }
        default String getModuleCode(String moduleBaseName, String moduleName) { return null; }
        default String getModule(String moduleName) { return null; }
        default String getModule(String moduleBaseName, String moduleName) { return null; }
        default String loadModule(String moduleName) { return null; }
        default String loadModule(String moduleBaseName, String moduleName) { return null; }
        default String getModuleSource(String moduleName) { return null; }
        default String getModuleSource(String moduleBaseName, String moduleName) { return null; }
        default String getModuleString(String moduleName) { return null; }
        default String getModuleString(String moduleBaseName, String moduleName) { return null; }
        default String findModule(String moduleName) { return null; }
        default String findModule(String moduleBaseName, String moduleName) { return null; }
        default String readModule(String moduleName) { return null; }
        default String readModule(String moduleBaseName, String moduleName) { return null; }
        default String getScript(String moduleName) { return null; }
        default String getSource(String moduleName) { return null; }
        default boolean moduleFileExists(String moduleName) { return false; }
        default boolean moduleFileExists(String moduleBaseName, String moduleName) { return false; }
        default boolean exists(String moduleName) { return false; }
        default String convertModuleName(String moduleBaseName, String moduleName) { return moduleName; }
        default String convertModuleName(String moduleName) { return moduleName; }
        default String normalizeModuleName(String moduleBaseName, String moduleName) { return moduleName; }
        default String normalizeModuleName(String moduleName) { return moduleName; }
        default String getModulePath(String moduleName) { return null; }
        default String getModulePath(String moduleBaseName, String moduleName) { return null; }
    }

    public interface BytecodeModuleLoader extends DefaultModuleLoader {
        byte[] getModuleBytecode(String moduleName);

        default byte[] getModuleBytecode(String moduleBaseName, String moduleName) { return null; }
        default byte[] getBytecode(String moduleName) { return null; }
        default byte[] getBytecode(String moduleBaseName, String moduleName) { return null; }
        default byte[] getModuleByteCode(String moduleName) { return null; }
        default byte[] getModuleByteCode(String moduleBaseName, String moduleName) { return null; }

        @Override
        default String getModuleStringCode(String moduleName) { return null; }
        @Override
        default String getModuleStringCode(String moduleBaseName, String moduleName) { return null; }
        @Override
        default String getModuleCode(String moduleName) { return null; }
        @Override
        default String getModuleCode(String moduleBaseName, String moduleName) { return null; }
        @Override
        default String getModule(String moduleName) { return null; }
        @Override
        default String getModule(String moduleBaseName, String moduleName) { return null; }
        @Override
        default String loadModule(String moduleName) { return null; }
        @Override
        default String loadModule(String moduleBaseName, String moduleName) { return null; }
        @Override
        default String getModuleSource(String moduleName) { return null; }
        @Override
        default String getModuleSource(String moduleBaseName, String moduleName) { return null; }
        @Override
        default String getModuleString(String moduleName) { return null; }
        @Override
        default String getModuleString(String moduleBaseName, String moduleName) { return null; }
        @Override
        default String findModule(String moduleName) { return null; }
        @Override
        default String findModule(String moduleBaseName, String moduleName) { return null; }
        @Override
        default String readModule(String moduleName) { return null; }
        @Override
        default String readModule(String moduleBaseName, String moduleName) { return null; }
        @Override
        default String getScript(String moduleName) { return null; }
        @Override
        default String getSource(String moduleName) { return null; }
        @Override
        default boolean moduleFileExists(String moduleName) { return false; }
        @Override
        default boolean moduleFileExists(String moduleBaseName, String moduleName) { return false; }
        @Override
        default boolean exists(String moduleName) { return false; }
        @Override
        default String convertModuleName(String moduleBaseName, String moduleName) { return moduleName; }
        @Override
        default String convertModuleName(String moduleName) { return moduleName; }
        @Override
        default String normalizeModuleName(String moduleBaseName, String moduleName) { return moduleName; }
        @Override
        default String normalizeModuleName(String moduleName) { return moduleName; }
        @Override
        default String getModulePath(String moduleName) { return null; }
        @Override
        default String getModulePath(String moduleBaseName, String moduleName) { return null; }
    }

    public interface Console {
        void log(String msg);
        default void info(String msg) {}
        default void warn(String msg) {}
        default void error(String msg) {}
    }

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

    public Object evaluateModule(String script, String moduleName) {
        return null;
    }

    public Object evaluateModule(String script) {
        return null;
    }

    public byte[] compileModule(String script, String moduleName) {
        return new byte[0];
    }

    public byte[] compileModule(String script) {
        return new byte[0];
    }

    public Object execute(byte[] bytecode, String filename) {
        return null;
    }

    public Object execute(byte[] bytecode) {
        return null;
    }

    public Object execute(String script, String filename) {
        return null;
    }

    public Object execute(String script) {
        return null;
    }

    public void setModuleLoader(BytecodeModuleLoader loader) {}
    public void setModuleLoader(DefaultModuleLoader loader) {}
    public void setModuleLoader(Object loader) {}

    public void setConsole(Console console) {}
    public void setConsole(Object console) {}

    public Object get(JSObject obj, String key) {
        return obj != null ? obj.get(key) : null;
    }

    public JSObject getGlobalObject() {
        return new JSObject(this);
    }

    public JSObject createNewJSObject() {
        return new JSObject(this);
    }

    public JSArray createNewJSArray() {
        return new JSArray();
    }

    public JSObject createJSObject() {
        return new JSObject(this);
    }

    public JSArray createJSArray() {
        return new JSArray();
    }

    public JSFunction createNewJSFunction(JSCallFunction function) {
        return new JSFunction();
    }

    public JSFunction createJSFunction(JSCallFunction function) {
        return new JSFunction();
    }

    public Object parse(String json) {
        return new JSObject(this);
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
    public String stringify(Object obj) { return "{}"; }
}
