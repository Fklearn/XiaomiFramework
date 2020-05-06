package com.android.server.inputmethod;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Slog;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodInfo;
import com.android.server.inputmethod.InputMethodSubtypeSwitchingController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.os.Build;

public class MiuiSecurityInputMethodHelper {
    public static final boolean DEBUG = true;
    public static final String MIUI_SEC_INPUT_METHOD_APP_PKG_NAME = "com.miui.securityinputmethod";
    public static final int NUMBER_PASSWORD = 18;
    public static final boolean SUPPORT_SEC_INPUT_METHOD;
    public static final String TAG = "MiuiSecurityInputMethodHelper";
    public static final int TEXT_MASK = 4095;
    public static final int TEXT_PASSWORD = 129;
    public static final int TEXT_VISIBLE_PASSWORD = 145;
    public static final int TEXT_WEB_PASSWORD = 225;
    public static final int WEB_EDIT_TEXT = 160;
    /* access modifiers changed from: private */
    public boolean mSecEnabled;
    /* access modifiers changed from: private */
    public InputMethodManagerService mService;
    private SettingsObserver mSettingsObserver;

    static {
        boolean z = false;
        if (SystemProperties.getInt("ro.miui.has_security_keyboard", 0) == 1 && !Build.IS_GLOBAL_BUILD) {
            z = true;
        }
        SUPPORT_SEC_INPUT_METHOD = z;
    }

    public MiuiSecurityInputMethodHelper(InputMethodManagerService service) {
        this.mService = service;
    }

    /* access modifiers changed from: package-private */
    public void onSystemRunningLocked() {
        this.mSettingsObserver = new SettingsObserver(this.mService.mHandler);
        this.mSettingsObserver.registerContentObserverLocked(this.mService.mSettings.getCurrentUserId());
        boolean z = true;
        if (Settings.Secure.getIntForUser(this.mService.mContext.getContentResolver(), "enable_miui_security_ime", 1, this.mService.mSettings.getCurrentUserId()) == 0) {
            z = false;
        }
        this.mSecEnabled = z;
    }

    /* access modifiers changed from: package-private */
    public void onSwitchUserLocked(int newUserId) {
        this.mSettingsObserver.registerContentObserverLocked(newUserId);
        boolean z = true;
        if (Settings.Secure.getIntForUser(this.mService.mContext.getContentResolver(), "enable_miui_security_ime", 1, this.mService.mSettings.getCurrentUserId()) == 0) {
            z = false;
        }
        this.mSecEnabled = z;
    }

    class SettingsObserver extends ContentObserver {
        boolean mRegistered = false;
        int mUserId;

        SettingsObserver(Handler handler) {
            super(handler);
        }

        /* access modifiers changed from: package-private */
        public void registerContentObserverLocked(int userId) {
            if (MiuiSecurityInputMethodHelper.SUPPORT_SEC_INPUT_METHOD) {
                if (!this.mRegistered || this.mUserId != userId) {
                    ContentResolver resolver = MiuiSecurityInputMethodHelper.this.mService.mContext.getContentResolver();
                    if (this.mRegistered) {
                        resolver.unregisterContentObserver(this);
                        this.mRegistered = false;
                    }
                    if (this.mUserId != userId) {
                        this.mUserId = userId;
                    }
                    resolver.registerContentObserver(Settings.Secure.getUriFor("enable_miui_security_ime"), false, this, userId);
                    this.mRegistered = true;
                }
            }
        }

        public void onChange(boolean selfChange, Uri uri) {
            Uri secIMEUri = Settings.Secure.getUriFor("enable_miui_security_ime");
            synchronized (MiuiSecurityInputMethodHelper.this.mService.mMethodMap) {
                if (secIMEUri.equals(uri)) {
                    MiuiSecurityInputMethodHelper miuiSecurityInputMethodHelper = MiuiSecurityInputMethodHelper.this;
                    boolean z = true;
                    if (Settings.Secure.getIntForUser(MiuiSecurityInputMethodHelper.this.mService.mContext.getContentResolver(), "enable_miui_security_ime", 1, MiuiSecurityInputMethodHelper.this.mService.mSettings.getCurrentUserId()) == 0) {
                        z = false;
                    }
                    boolean unused = miuiSecurityInputMethodHelper.mSecEnabled = z;
                    MiuiSecurityInputMethodHelper.this.updateFromSettingsLocked();
                    Slog.d(MiuiSecurityInputMethodHelper.TAG, "enable status change: " + MiuiSecurityInputMethodHelper.this.mSecEnabled);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateFromSettingsLocked() {
        if (this.mService.mCurMethodId != null && !this.mSecEnabled && isSecMethodLocked(this.mService.mCurMethodId)) {
            this.mService.clearCurMethodLocked();
            this.mService.unbindCurrentClientLocked(2);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean mayChangeInputMethodLocked(EditorInfo attribute) {
        EditorInfo editorInfo = attribute;
        if (!SUPPORT_SEC_INPUT_METHOD) {
            return false;
        }
        if (this.mService.mCurMethodId == null) {
            Slog.w(TAG, "input_service has no current_method_id");
            return false;
        } else if (editorInfo == null) {
            Slog.w(TAG, "editor_info is null, we cannot judge");
            return false;
        } else if (this.mService.mMethodMap.get(this.mService.mCurMethodId) == null) {
            Slog.w(TAG, "fail to find current_method_info in the map");
            return false;
        } else {
            boolean switchToSecInput = isPasswdInputType(editorInfo.inputType) && !isSecMethodLocked(this.mService.mCurMethodId) && this.mSecEnabled && !TextUtils.isEmpty(getSecMethodIdLocked()) && !isEditorInDefaultImeApp(attribute);
            boolean switchFromSecInput = isSecMethodLocked(this.mService.mCurMethodId) && (!this.mSecEnabled || !isPasswdInputType(editorInfo.inputType) || isEditorInDefaultImeApp(attribute));
            if (switchToSecInput) {
                String secInputMethodId = getSecMethodIdLocked();
                if (TextUtils.isEmpty(secInputMethodId)) {
                    Slog.w(TAG, "fail to find secure_input_method in input_method_list");
                    return false;
                }
                InputMethodManagerService inputMethodManagerService = this.mService;
                inputMethodManagerService.mCurMethodId = secInputMethodId;
                inputMethodManagerService.clearCurMethodLocked();
                this.mService.unbindCurrentClientLocked(2);
                this.mService.unbindCurrentMethodLocked();
                InputMethodManagerService inputMethodManagerService2 = this.mService;
                inputMethodManagerService2.setInputMethodLocked(inputMethodManagerService2.mCurMethodId, -1);
                return true;
            } else if (!switchFromSecInput) {
                return false;
            } else {
                String selectedInputMethod = this.mService.mSettings.getSelectedInputMethod();
                if (TextUtils.isEmpty(selectedInputMethod)) {
                    Slog.w(TAG, "something is weired, maybe the input method app are uninstalled");
                    InputMethodInfo imi = InputMethodUtils.getMostApplicableDefaultIME(this.mService.mSettings.getEnabledInputMethodListLocked());
                    if (imi == null || TextUtils.equals(imi.getPackageName(), MIUI_SEC_INPUT_METHOD_APP_PKG_NAME)) {
                        Slog.w(TAG, "fail to find a most applicable default ime");
                        List<InputMethodInfo> imiList = this.mService.mSettings.getEnabledInputMethodListLocked();
                        if (imiList != null && imiList.size() != 0) {
                            Iterator<InputMethodInfo> it = imiList.iterator();
                            while (true) {
                                if (!it.hasNext()) {
                                    break;
                                }
                                InputMethodInfo inputMethodInfo = it.next();
                                if (!TextUtils.equals(inputMethodInfo.getPackageName(), MIUI_SEC_INPUT_METHOD_APP_PKG_NAME)) {
                                    selectedInputMethod = inputMethodInfo.getId();
                                    break;
                                }
                            }
                        } else {
                            Slog.w(TAG, "there is no enabled method list");
                            return false;
                        }
                    }
                }
                if (TextUtils.isEmpty(selectedInputMethod)) {
                    Slog.w(TAG, "finally, we still fail to find default input method");
                    return false;
                } else if (TextUtils.equals(this.mService.mCurMethodId, selectedInputMethod)) {
                    Slog.w(TAG, "It looks like there is only miui_sec_input_method in the system");
                    return false;
                } else {
                    InputMethodManagerService inputMethodManagerService3 = this.mService;
                    inputMethodManagerService3.mCurMethodId = selectedInputMethod;
                    inputMethodManagerService3.clearCurMethodLocked();
                    this.mService.unbindCurrentClientLocked(2);
                    this.mService.unbindCurrentMethodLocked();
                    InputMethodManagerService inputMethodManagerService4 = this.mService;
                    inputMethodManagerService4.setInputMethodLocked(inputMethodManagerService4.mCurMethodId, -1);
                    return true;
                }
            }
        }
    }

    private boolean isSecMethodLocked(String methodId) {
        InputMethodInfo imi = this.mService.mMethodMap.get(methodId);
        return imi != null && TextUtils.equals(imi.getPackageName(), MIUI_SEC_INPUT_METHOD_APP_PKG_NAME);
    }

    private String getSecMethodIdLocked() {
        for (Map.Entry<String, InputMethodInfo> entry : this.mService.mMethodMap.entrySet()) {
            if (isSecMethodLocked(entry.getKey())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public boolean shouldHideImeSwitcherLocked() {
        return (SUPPORT_SEC_INPUT_METHOD && isSecMethodLocked(this.mService.mCurMethodId)) || this.mService.mCurMethod == null;
    }

    /* access modifiers changed from: package-private */
    public void removeSecMethod(List<InputMethodSubtypeSwitchingController.ImeSubtypeListItem> imList) {
        if (SUPPORT_SEC_INPUT_METHOD && imList != null && imList.size() > 0) {
            for (InputMethodSubtypeSwitchingController.ImeSubtypeListItem imeSubtypeListItem : imList) {
                if (TextUtils.equals(imeSubtypeListItem.mImi.getPackageName(), MIUI_SEC_INPUT_METHOD_APP_PKG_NAME)) {
                    imList.remove(imeSubtypeListItem);
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public ArrayList<InputMethodInfo> filterSecMethodLocked(ArrayList<InputMethodInfo> methodInfos) {
        if (methodInfos != null && SUPPORT_SEC_INPUT_METHOD) {
            Iterator<InputMethodInfo> it = methodInfos.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                InputMethodInfo methodInfo = it.next();
                if (isSecMethodLocked(methodInfo.getId())) {
                    methodInfos.remove(methodInfo);
                    break;
                }
            }
        }
        return methodInfos;
    }

    private static boolean isPasswdInputType(int inputType) {
        if ((inputType & 160) == 160) {
            if ((inputType & TEXT_MASK) == 225) {
                return true;
            }
            return false;
        } else if ((inputType & TEXT_MASK) == 129 || (inputType & TEXT_MASK) == 145 || (inputType & TEXT_MASK) == 18) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isEditorInDefaultImeApp(EditorInfo editor) {
        String pkg = editor.packageName;
        String defaultIme = this.mService.mSettings.getSelectedInputMethod();
        if (!TextUtils.isEmpty(defaultIme)) {
            ComponentName cn = ComponentName.unflattenFromString(defaultIme);
            return cn != null && TextUtils.equals(pkg, cn.getPackageName());
        }
    }
}
