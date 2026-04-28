package com.js2xposed.listenall;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!TARGET_PACKAGE.equals(lpparam.packageName)) {
            return;
        }

        log("[+] Loaded target: " + lpparam.packageName);
        initProjectArgs(lpparam.classLoader);
        hookHttp(lpparam.classLoader);
        hookAutoClient(lpparam.classLoader);
        log("[*] Mock ready. Now click login/activate with any card code.");
    }

    private static void initProjectArgs(ClassLoader classLoader) {
        if (!INIT_PROJECT_ARGS) {
            return;
        }

        try {
            Class<?> projectClass = XposedHelpers.findClass(PROJECT_CLASS, classLoader);
            Object assetArgs = XposedHelpers.callStaticMethod(projectClass, "getAssetProjectArgs");

            if (assetArgs != null) {
                XposedHelpers.setStaticObjectField(projectClass, "projectArgs", assetArgs);
                log("[+] Project.projectArgs = getAssetProjectArgs()");
            }
        } catch (Throwable t) {
            log("[-] init projectArgs failed: " + t);
        }
    }

    private static void hookHttp(ClassLoader classLoader) {
        try {
            Class<?> httpClass = XposedHelpers.findClass(HTTP_CLASS, classLoader);
            XposedBridge.hookAllMethods(httpClass, "getHttp", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    String url = param.args != null && param.args.length > 0 ? String.valueOf(param.args[0]) : "";

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

            log("[+] hooked Http.getHttp for mocking");
        } catch (Throwable t) {
            log("[-] hook Http.getHttp failed: " + t);
        }
    }

    private static void hookAutoClient(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod(
                    AUTO_CLIENT_CLASS,
                    classLoader,
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
        } catch (Throwable ignored) {
        }

        try {
            XposedHelpers.findAndHookMethod(
                    AUTO_CLIENT_CLASS,
                    classLoader,
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
        } catch (Throwable ignored) {
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
}
