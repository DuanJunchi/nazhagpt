# ListenAll LSPosed

LSPosed module converted from `listen_all_clean.js`.

## Target

- Scope package: `fm.qingting.qtradio`
- Hook classes kept as tested in Frida:
  - `com.autoapp.autoapp.StaticClass.project`
  - `com.autoapp.autoapp.Classes.Http`
  - `com.autoapp.autoapp.client.autoClient`

## Build on GitHub

Push this repository to GitHub, then run the `Build APK` workflow from the Actions tab. The built APK is uploaded as the `listenall-lsposed-apk` artifact.

After installing the APK, enable the module in LSPosed, keep the scope on `fm.qingting.qtradio`, then force stop and reopen the target app.
