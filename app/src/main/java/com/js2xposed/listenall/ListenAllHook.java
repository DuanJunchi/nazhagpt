package com.js2xposed.listenall;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class ListenAllHook implements IXposedHookLoadPackage {
    private static final String TARGET_PACKAGE = "fm.qingting.qtradio";
    private static final boolean INIT_PROJECT_ARGS = true;
    private static final String PROJECT_CLASS = "com.autoapp.autoapp.StaticClass.project";
    private static final String HTTP_CLASS = "com.autoapp.autoapp.Classes.Http";
    private static final String AUTO_CLIENT_CLASS = "com.autoapp.autoapp.client.autoClient";

    private static final String MOCK_JS_CODE = "jFsIQLOVDPQzZMPv7MO/9CxGzX2cIk2Dl5viTpqBOGs7D7k+EMPhisqJkw2ogQs4t+jXdScB8qhmtIW5WJqK3SocJoktiwdAgcVzGhEwq0LZE5JtbxGE6FWRoFcEDBBmz/5BQ3GV+9FINP8IEXmw4uRB8G+yasP68rcqVlgLPDNgxyPLbkSrNc1od7pkAzDIScc8sfcuM2kv1rjbb/G7lUzN9dHmFeoaj6IM3PbMJb2l48GxLFu+oUvN5h2nhm8V38/ARh7pHsvJjBXiQTksiWWlV1C4FUZIZzTFdG955rev+oDPS7BR41AKGEE3DwccvzqLy09FuRJrK4rf6P8h/GzKWBiK/dUTL9OontNdCcy3+lQdGDnIYY4zM/uDhVxcin/hHLohFz3m3zu7elCUXGWcIsebcL1VrA/ubQHIVyBPXVgrhTaTdppt3RNpzYybf3PBS4xfmOJ7Gkbg/RfRlbidCCLaqpQwNEXPUa8/gxtsaJgqCS2tCEs//Y1W4zOqigF3a5nKzEL2pxChz7rN/BP8NdNf5OGtNr3IBj7Arx4YIZrDjlYwna53ai7CANlb21mDWmSlnNYtpXBuv49UAC3I5YH8oTH7gyGdYtfMFo4DPye/dPdnbtiVDc8w9iyQFuOXidT6vDvaTAfZmnc5M+xuWBJ/ClMLzy4n1azqu84Q6QNdQIUEQHvFUIZc64Z2OW08SmYiyC1eTk6csGNSa2/6A3RqmmEKYzXBu6NUtuJ82Yoyy86G3GRqvkeb/V191WMTJB0re7q7E9IKYr85mQm1g0mUwTSYWQtBAl+/57ghcmx7jhQsu16eI5lC/Lu5xcwgqTXCrQeRNVOoTi+vWUKg+5YmQ/fXLjFZRf31Hk9mQigEKaH6Bov/hHd3PyP4J1LRq/QJNIJNeZzNAGWEjXl8ZHmPuBsxamqE0tk4D+Px3S0B0NOVTBG3b2+uQeOXdxba+ye+me6Iq5pyhiGnQtl8DEDt6FwQDBv5nqnfZJGa4CYhQMX3frUrioRyhwSoWOveH3eN8WccrK1z00a5XvUJTFwRHowo9GumIlRzzKrIfQYT0p5gEmvEblbsGx75wbh6M8L4fxUJn4TQVMzm5m/fRJcfXM7Uw62i1m0QVBL7xRXXq6wYsv19xk8cW7FTVrLdoTo4tgWqJLwM5rKh/SEyWWVo7ruAn7qcIJTmmEMVTrYiSpGRSv7w4pqpZJPfeWyf9l8WN25JZ7KAVHwDDJfH1nZ4EGQyuMOx/UDs8AQ/QoR87bAefTTL51SuOkZ5L+47i4E5nReBd4kW1gyM8//SApgIdh0OWEtGyXibk2Gi/21VcwxclKt0imTkGy4ALSIm2Bo5tGAr3BcBmlY/Mh6UiMcBSt8tveq2TGHydUWPZsVKvK0m8mp5s2OcoBoQ+BZ/MtapwkJTMsVOPTWUfn+womY1MhH2c6TcNk0CKgpJU57cx9AvBdSCry0KN3v8umbsASSdU+2KhTiAbQuDfFq+UtJytkvNB52VlDm8b/HYd5TWSncqeM72hm8drK06HJwkLAdiCx/prMn1nfyad5vZJqwtQNbr0LNq2LO5sOLmxFPTHoMI5ykEV6LjlCLtBKnAsifK/gCkIX/3ul5eT/OgBx+fAzA9HhsDlwh/LmmtO/PJEBLi2BPk/ATWMbGHSumKPp7fsn1fILA1cKdR4bPEhyANsTCAEzCXW2vhov5doesI5yi42vfigiJPPydPUT632w1nzvlBgdDFFK/CXnIXRv2ifvkTKG0PUIJZcf4X0g6pBuBAFanjhaZIOaXrnn4LDiNLfCOunnZK5qA8x1Rt0LqOqAMvtzibI+of7CdtOcGPxaJG9x5U19gtZrsp4vfoXqHcjHUs59LQe0b/tg==";
    private static final Set<Class<?>> PROJECT_INIT_CLASSES = Collections.newSetFromMap(new WeakHashMap<Class<?>, Boolean>());
    private static final Set<Class<?>> HOOKED_HTTP_CLASSES = Collections.newSetFromMap(new WeakHashMap<Class<?>, Boolean>());
    private static final Set<Class<?>> HOOKED_USER_ACTIVATE_CLASSES = Collections.newSetFromMap(new WeakHashMap<Class<?>, Boolean>());
    private static final Set<Class<?>> HOOKED_GET_APK_KEY_CLASSES = Collections.newSetFromMap(new WeakHashMap<Class<?>, Boolean>());
    private static final Set<Class<?>> HOOKED_APPLICATION_CLASSES = Collections.newSetFromMap(new WeakHashMap<Class<?>, Boolean>());
    private static Class<?> projectClassRef;
    private static Context appContext;
    private static boolean applicationAttachHooked;
    private static boolean classLoaderHooked;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!TARGET_PACKAGE.equals(lpparam.packageName)) {
            return;
        }

        log("[+] Loaded target: " + lpparam.packageName);
        hookClassLoader();
        hookApplicationAttach();
        tryInstallFromClassLoader(lpparam.classLoader, "LoadPackage");
        log("[*] Waiting for autoapp classes from app/dynamic classloaders...");
    }

    private static synchronized void hookApplicationAttach() {
        if (applicationAttachHooked) {
            return;
        }

        try {
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    Context context = (Context) param.args[0];
                    if (context == null) {
                        return;
                    }

                    rememberContext(context);
                    hookConcreteApplicationOnCreate(param.thisObject.getClass());
                    ClassLoader classLoader = context.getClassLoader();
                    log("[+] Application.attach classLoader = " + classLoader);
                    tryInstallFromClassLoader(classLoader, "Application.attach");
                }
            });

            applicationAttachHooked = true;
            log("[+] hooked Application.attach");
        } catch (Throwable t) {
            logThrowable("hook Application.attach failed", t);
        }
    }

    private static synchronized void hookConcreteApplicationOnCreate(Class<?> applicationClass) {
        if (applicationClass == null || HOOKED_APPLICATION_CLASSES.contains(applicationClass)) {
            return;
        }

        try {
            XposedBridge.hookAllMethods(applicationClass, "onCreate", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    if (param.thisObject instanceof Context) {
                        rememberContext((Context) param.thisObject);
                    }

                    log("[+] Application.onCreate finished: " + param.thisObject.getClass().getName());
                    tryInstallFromClassLoader(param.thisObject.getClass().getClassLoader(), "Application.onCreate");
                    retryProjectArgs("Application.onCreate");
                }
            });

            HOOKED_APPLICATION_CLASSES.add(applicationClass);
            log("[+] hooked " + applicationClass.getName() + ".onCreate");
        } catch (Throwable t) {
            logThrowable("hook " + applicationClass.getName() + ".onCreate failed", t);
        }
    }

    private static synchronized void hookClassLoader() {
        if (classLoaderHooked) {
            return;
        }

        try {
            XposedBridge.hookAllMethods(ClassLoader.class, "loadClass", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    if (param.getThrowable() != null || param.args == null || param.args.length == 0) {
                        return;
                    }

                    String className = String.valueOf(param.args[0]);
                    if (!isWatchedClass(className)) {
                        return;
                    }

                    Object result = param.getResult();
                    if (result instanceof Class<?>) {
                        Class<?> loadedClass = (Class<?>) result;
                        log("[+] class loaded: " + loadedClass.getName() + " by " + loadedClass.getClassLoader());
                        onWatchedClassLoaded(loadedClass);
                    }
                }
            });

            classLoaderHooked = true;
            log("[+] watching ClassLoader.loadClass");
        } catch (Throwable t) {
            logThrowable("hook ClassLoader.loadClass failed", t);
        }
    }

    private static void tryInstallFromClassLoader(ClassLoader classLoader, String source) {
        if (classLoader == null) {
            return;
        }

        log("[*] trying " + source + " classLoader: " + classLoader);
        tryInstallClass(PROJECT_CLASS, classLoader);
        tryInstallClass(HTTP_CLASS, classLoader);
        tryInstallClass(AUTO_CLIENT_CLASS, classLoader);
    }

    private static void tryInstallClass(String className, ClassLoader classLoader) {
        try {
            Class<?> clazz = Class.forName(className, false, classLoader);
            log("[+] found " + className + " by direct lookup");
            onWatchedClassLoaded(clazz);
        } catch (Throwable ignored) {
        }
    }

    private static boolean isWatchedClass(String className) {
        return PROJECT_CLASS.equals(className) || HTTP_CLASS.equals(className) || AUTO_CLIENT_CLASS.equals(className);
    }

    private static void onWatchedClassLoaded(Class<?> clazz) {
        String className = clazz.getName();
        if (PROJECT_CLASS.equals(className)) {
            projectClassRef = clazz;
            initProjectArgs(clazz);
        } else if (HTTP_CLASS.equals(className)) {
            hookHttp(clazz);
        } else if (AUTO_CLIENT_CLASS.equals(className)) {
            hookAutoClient(clazz);
        }
    }

    private static synchronized void initProjectArgs(Class<?> projectClass) {
        if (!INIT_PROJECT_ARGS) {
            return;
        }
        if (PROJECT_INIT_CLASSES.contains(projectClass)) {
            return;
        }

        try {
            injectContextFields(projectClass);
            Object assetArgs = XposedHelpers.callStaticMethod(projectClass, "getAssetProjectArgs");

            if (assetArgs != null) {
                XposedHelpers.setStaticObjectField(projectClass, "projectArgs", assetArgs);
                log("[+] Project.projectArgs = getAssetProjectArgs()");
            }

            PROJECT_INIT_CLASSES.add(projectClass);
        } catch (Throwable t) {
            logThrowable("init projectArgs failed", t);
        }
    }

    private static synchronized void retryProjectArgs(String source) {
        if (projectClassRef == null || PROJECT_INIT_CLASSES.contains(projectClassRef)) {
            return;
        }

        log("[*] retry projectArgs from " + source);
        initProjectArgs(projectClassRef);
    }

    private static synchronized void rememberContext(Context context) {
        Context applicationContext = context.getApplicationContext();
        appContext = applicationContext != null ? applicationContext : context;
        log("[+] appContext = " + appContext.getClass().getName());
    }

    private static void injectContextFields(Class<?> projectClass) {
        if (appContext == null) {
            return;
        }

        Field[] fields = projectClass.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers) || !Context.class.isAssignableFrom(field.getType())) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object current = field.get(null);
                if (current == null) {
                    field.set(null, appContext);
                    log("[+] injected Context into " + projectClass.getName() + "." + field.getName());
                }
            } catch (Throwable t) {
                logThrowable("inject Context into " + field.getName() + " failed", t);
            }
        }
    }

    private static synchronized void hookHttp(Class<?> httpClass) {
        if (HOOKED_HTTP_CLASSES.contains(httpClass)) {
            return;
        }

        try {
            XposedBridge.hookAllMethods(httpClass, "getHttp", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    retryProjectArgs("Http.getHttp");
                    String url = param.args != null && param.args.length > 0 ? String.valueOf(param.args[0]) : "";
                    log("[*] Http.getHttp called, arg0 = " + url);

                    if (url.contains("/apkOrder/apkActivate")) {
                        log("\n========== [MOCK] intercept apkActivate request ==========");
                        log("url = " + url);
                        String mockResponse = "{\"code\":200,\"msg\":\"更新完成\",\"data\":null}";
                        log("========== [MOCK] return mocked success data ==========");
                        log(mockResponse);
                        param.setResult(mockResponse);
                        return;
                    }

                    if (url.contains("/user/getApkKeyV2")) {
                        log("\n========== [MOCK] intercept getApkKeyV2 request ==========");
                        log("url = " + url);
                        String mockResponse = "{\"code\":200,\"msg\":\"SUCCESS\",\"data\":{\"jsCode\":\"" + MOCK_JS_CODE + "\",\"KeyID\":\"940b7b1bf7dedefa52ea95ff2929d757\",\"activateCard\":\"微信ddgreverse\",\"endDay\":\"999\",\"remarks\":null,\"keyID\":\"940b7b1bf7dedefa52ea95ff2929d757\"}}";
                        log("========== [MOCK] return mocked success data ==========");
                        log(mockResponse);
                        param.setResult(mockResponse);
                        return;
                    }

                    if (isTarget(url)) {
                        param.setObjectExtra("listenall_matched", true);
                        log("\n========== Http.getHttp REQUEST ==========");
                        log("url = " + url);
                    }
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    Object ret = param.getResult();
                    Object matched = param.getObjectExtra("listenall_matched");

                    if (Boolean.TRUE.equals(matched) || isTarget(ret)) {
                        log("========== Http.getHttp RESPONSE ==========");
                        log(String.valueOf(ret));
                    }
                }
            });

            HOOKED_HTTP_CLASSES.add(httpClass);
            log("[+] hooked Http.getHttp for mocking");
        } catch (Throwable t) {
            logThrowable("hook Http.getHttp failed", t);
        }
    }

    private static void hookAutoClient(Class<?> autoClientClass) {
        hookUserActivate(autoClientClass);
        hookGetApkKey(autoClientClass);
    }

    private static synchronized void hookUserActivate(Class<?> autoClientClass) {
        if (HOOKED_USER_ACTIVATE_CLASSES.contains(autoClientClass)) {
            return;
        }

        try {
            XposedHelpers.findAndHookMethod(
                    autoClientClass,
                    "userActivate",
                    String.class,
                    String.class,
                    String.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            log("\n========== userActivate RETURN ==========");
                            dumpRetObj("ret", param.getResult());
                        }
                    });

            HOOKED_USER_ACTIVATE_CLASSES.add(autoClientClass);
            log("[+] hooked autoClient.userActivate");
        } catch (Throwable t) {
            logThrowable("hook autoClient.userActivate failed", t);
        }
    }

    private static synchronized void hookGetApkKey(Class<?> autoClientClass) {
        if (HOOKED_GET_APK_KEY_CLASSES.contains(autoClientClass)) {
            return;
        }

        try {
            XposedHelpers.findAndHookMethod(
                    autoClientClass,
                    "getApkKey",
                    String.class,
                    String.class,
                    String.class,
                    String.class,
                    String.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            log("\n========== getApkKey RETURN ==========");
                            dumpRetObj("ret", param.getResult());
                        }
                    });

            HOOKED_GET_APK_KEY_CLASSES.add(autoClientClass);
            log("[+] hooked autoClient.getApkKey");
        } catch (Throwable t) {
            logThrowable("hook autoClient.getApkKey failed", t);
        }
    }

    private static boolean isTarget(Object value) {
        if (value == null) {
            return false;
        }

        String s = String.valueOf(value);
        return s.contains("/apkOrder/apkActivate") || s.contains("/user/getApkKeyV2");
    }

    private static void dumpRetObj(String tag, Object obj) {
        if (obj == null) {
            log(tag + " = null");
            return;
        }

        log(tag + " = " + obj);
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                log("  " + field.getName() + " = " + field.get(obj));
            }
        } catch (Throwable t) {
            log("  <dump failed: " + t + ">");
        }
    }

    private static void log(String message) {
        XposedBridge.log("[ListenAll] " + message);
    }

    private static void logThrowable(String message, Throwable throwable) {
        XposedBridge.log("[ListenAll] [-] " + message + ": " + throwable);
        XposedBridge.log(throwable);
    }
}
