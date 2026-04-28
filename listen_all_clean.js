function main() {
    if (typeof Java === "undefined") {
        console.log("[-] Java is undefined");
        return;
    }

    Java.perform(function () {
        console.log("[+] Java bridge ready");

        const INIT_PROJECT_ARGS = true;

        const TARGETS = [
            "/apkOrder/apkActivate",
            "/user/getApkKeyV2"
        ];

        function isTarget(s) {
            if (s === null || s === undefined) return false;
            s = String(s);
            for (let i = 0; i < TARGETS.length; i++) {
                if (s.indexOf(TARGETS[i]) >= 0) return true;
            }
            return false;
        }

        function dumpRetObj(tag, obj) {
            if (obj === null || obj === undefined) {
                console.log(tag + " = null");
                return;
            }
            console.log(tag + " = " + obj);
            try {
                const fields = obj.getClass().getDeclaredFields();
                for (let i = 0; i < fields.length; i++) {
                    const f = fields[i];
                    f.setAccessible(true);
                    console.log("  " + f.getName() + " = " + f.get(obj));
                }
            } catch (e) {
                console.log("  <dump failed: " + e + ">");
            }
        }

        if (INIT_PROJECT_ARGS) {
            try {
                const Project = Java.use("com.autoapp.autoapp.StaticClass.project");
                const assetArgs = Project.getAssetProjectArgs();

                if (assetArgs !== null) {
                    Project.projectArgs.value = assetArgs;
                    console.log("[+] Project.projectArgs = getAssetProjectArgs()");
                }
            } catch (e) {
                console.log("[-] init projectArgs failed: " + e);
            }
        }

        // 1. 高层业务 HTTP：进行断网 Mock 劫持
        try {
            const Http = Java.use("com.autoapp.autoapp.Classes.Http");

            Http.getHttp.overloads.forEach(function (ov) {
                ov.implementation = function () {
                    let url = "";
                    try {
                        url = String(arguments[0]);
                    } catch (e) {}

                    // ================= 劫持逻辑开始 =================
                    // 1. 劫持激活/登录接口
                    if (url.indexOf("/apkOrder/apkActivate") >= 0) {
                        console.log("\n========== [MOCK] 拦截 apkActivate 请求 ==========");
                        console.log("url = " + url);
                        
                        let mockResponse = '{"code":200,"msg":"更新完成","data":null}';
                        console.log("========== [MOCK] 返回伪造成功数据 ==========");
                        console.log(mockResponse);
                        
                        // 直接返回伪造的字符串，不执行实际的网络请求
                        return Java.use("java.lang.String").$new(mockResponse);
                    }

                    // 2. 劫持获取密钥接口
                    if (url.indexOf("/user/getApkKeyV2") >= 0) {
                        console.log("\n========== [MOCK] 拦截 getApkKeyV2 请求 ==========");
                        console.log("url = " + url);
                        
                        // 使用你日志里的真实 jsCode
                        let mockJsCode = "jFsIQLOVDPQzZMPv7MO/9CxGzX2cIk2Dl5viTpqBOGs7D7k+EMPhisqJkw2ogQs4t+jXdScB8qhmtIW5WJqK3SocJoktiwdAgcVzGhEwq0LZE5JtbxGE6FWRoFcEDBBmz/5BQ3GV+9FINP8IEXmw4uRB8G+yasP68rcqVlgLPDNgxyPLbkSrNc1od7pkAzDIScc8sfcuM2kv1rjbb/G7lUzN9dHmFeoaj6IM3PbMJb2l48GxLFu+oUvN5h2nhm8V38/ARh7pHsvJjBXiQTksiWWlV1C4FUZIZzTFdG955rev+oDPS7BR41AKGEE3DwccvzqLy09FuRJrK4rf6P8h/GzKWBiK/dUTL9OontNdCcy3+lQdGDnIYY4zM/uDhVxcin/hHLohFz3m3zu7elCUXGWcIsebcL1VrA/ubQHIVyBPXVgrhTaTdppt3RNpzYybf3PBS4xfmOJ7Gkbg/RfRlbidCCLaqpQwNEXPUa8/gxtsaJgqCS2tCEs//Y1W4zOqigF3a5nKzEL2pxChz7rN/BP8NdNf5OGtNr3IBj7Arx4YIZrDjlYwna53ai7CANlb21mDWmSlnNYtpXBuv49UAC3I5YH8oTH7gyGdYtfMFo4DPye/dPdnbtiVDc8w9iyQFuOXidT6vDvaTAfZmnc5M+xuWBJ/ClMLzy4n1azqu84Q6QNdQIUEQHvFUIZc64Z2OW08SmYiyC1eTk6csGNSa2/6A3RqmmEKYzXBu6NUtuJ82Yoyy86G3GRqvkeb/V191WMTJB0re7q7E9IKYr85mQm1g0mUwTSYWQtBAl+/57ghcmx7jhQsu16eI5lC/Lu5xcwgqTXCrQeRNVOoTi+vWUKg+5YmQ/fXLjFZRf31Hk9mQigEKaH6Bov/hHd3PyP4J1LRq/QJNIJNeZzNAGWEjXl8ZHmPuBsxamqE0tk4D+Px3S0B0NOVTBG3b2+uQeOXdxba+ye+me6Iq5pyhiGnQtl8DEDt6FwQDBv5nqnfZJGa4CYhQMX3frUrioRyhwSoWOveH3eN8WccrK1z00a5XvUJTFwRHowo9GumIlRzzKrIfQYT0p5gEmvEblbsGx75wbh6M8L4fxUJn4TQVMzm5m/fRJcfXM7Uw62i1m0QVBL7xRXXq6wYsv19xk8cW7FTVrLdoTo4tgWqJLwM5rKh/SEyWWVo7ruAn7qcIJTmmEMVTrYiSpGRSv7w4pqpZJPfeWyf9l8WN25JZ7KAVHwDDJfH1nZ4EGQyuMOx/UDs8AQ/QoR87bAefTTL51SuOkZ5L+47i4E5nReBd4kW1gyM8//SApgIdh0OWEtGyXibk2Gi/21VcwxclKt0imTkGy4ALSIm2Bo5tGAr3BcBmlY/Mh6UiMcBSt8tveq2TGHydUWPZsVKvK0m8mp5s2OcoBoQ+BZ/MtapwkJTMsVOPTWUfn+womY1MhH2c6TcNk0CKgpJU57cx9AvBdSCry0KN3v8umbsASSdU+2KhTiAbQuDfFq+UtJytkvNB52VlDm8b/HYd5TWSncqeM72hm8drK06HJwkLAdiCx/prMn1nfyad5vZJqwtQNbr0LNq2LO5sOLmxFPTHoMI5ykEV6LjlCLtBKnAsifK/gCkIX/3ul5eT/OgBx+fAzA9HhsDlwh/LmmtO/PJEBLi2BPk/ATWMbGHSumKPp7fsn1fILA1cKdR4bPEhyANsTCAEzCXW2vhov5doesI5yi42vfigiJPPydPUT632w1nzvlBgdDFFK/CXnIXRv2ifvkTKG0PUIJZcf4X0g6pBuBAFanjhaZIOaXrnn4LDiNLfCOunnZK5qA8x1Rt0LqOqAMvtzibI+of7CdtOcGPxaJG9x5U19gtZrsp4vfoXqHcjHUs59LQe0b/tg==";
                        
                        // 拼装你想要的伪造结果
                        let mockResponse = `{"code":200,"msg":"SUCCESS","data":{"jsCode":"${mockJsCode}","KeyID":"940b7b1bf7dedefa52ea95ff2929d757","activateCard":"微信ddgreverse","endDay":"999","remarks":null,"keyID":"940b7b1bf7dedefa52ea95ff2929d757"}}`;
                        
                        console.log("========== [MOCK] 返回伪造成功数据 ==========");
                        console.log(mockResponse);
                        return Java.use("java.lang.String").$new(mockResponse);
                    }
                    // ================= 劫持逻辑结束 =================

                    // 对于其他的请求（如 /system/myapk/getApkInfo 等），放行让它正常走网络
                    const matched = isTarget(url);
                    if (matched) {
                        console.log("\n========== Http.getHttp REQUEST ==========");
                        console.log("url = " + url);
                    }

                    const ret = ov.apply(this, arguments);

                    if (matched || isTarget(ret)) {
                        console.log("========== Http.getHttp RESPONSE ==========");
                        console.log(ret);
                    }

                    return ret;
                };
            });

            console.log("[+] hooked Http.getHttp for Mocking");
        } catch (e) {
            console.log("[-] hook Http.getHttp failed: " + e);
        }

        // 2. 业务方法监听保持不变
        try {
            const AutoClient = Java.use("com.autoapp.autoapp.client.autoClient");
            const userActivate = AutoClient.userActivate.overload("java.lang.String", "java.lang.String", "java.lang.String");
            userActivate.implementation = function (imei, keyId, cardCode) {
                const ret = userActivate.call(this, imei, keyId, cardCode);
                console.log("\n========== userActivate RETURN ==========");
                dumpRetObj("ret", ret);
                return ret;
            };
        } catch (e) {}

        try {
            const AutoClient = Java.use("com.autoapp.autoapp.client.autoClient");
            const getApkKey = AutoClient.getApkKey.overload("java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String");
            getApkKey.implementation = function (imei, devkey, pkg, username, pass) {
                const ret = getApkKey.call(this, imei, devkey, pkg, username, pass);
                console.log("\n========== getApkKey RETURN ==========");
                dumpRetObj("ret", ret);
                return ret;
            };
        } catch (e) {}

        console.log("[*] Mock ready. Now click login/activate with any card code.");
    });
}

setImmediate(main);