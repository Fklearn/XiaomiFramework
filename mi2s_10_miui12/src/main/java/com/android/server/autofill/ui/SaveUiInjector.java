package com.android.server.autofill.ui;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.miui.R;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import com.android.server.slice.SliceClientPermissions;
import miui.app.AlertDialog;

@TargetApi(11)
final class SaveUiInjector {
    private static final String AUTOFILL_ACTIVITY = "com.miui.contentcatcher.autofill.activitys.AutofillSettingActivity";
    private static final String AUTOFILL_PACKAGE = "com.miui.contentcatcher";
    private static final String AUTO_CANCEL = "auto_cancel";
    private static final String AUTO_SAVE = "auto_save";
    private static String MIUI_VERSION_NAME = SystemProperties.get("ro.miui.ui.version.name", "");
    private static final String NEVER_SHOW_SAVE_UI = "never_show_save_ui";
    private static final String SAVEUI_ACTION = "intent.action.saveui";
    private static final String SERVICE_SP_NAME = "multi_process";
    private static final String TAG = "AutofillSaveUi";
    private static AlertDialog mDialog;

    private SaveUiInjector() {
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0164  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0178  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.app.Dialog showDialog(android.content.Context r21, com.android.server.autofill.ui.OverlayControl r22, android.content.DialogInterface.OnClickListener r23, android.content.DialogInterface.OnClickListener r24, android.content.DialogInterface.OnDismissListener r25) {
        /*
            r1 = r21
            r2 = r23
            r3 = r24
            java.lang.String r4 = "AutofillSaveUi"
            android.view.LayoutInflater r0 = android.view.LayoutInflater.from(r21)
            r5 = 0
            r6 = 285933584(0x110b0010, float:1.0965186E-28)
            android.view.View r6 = r0.inflate(r6, r5)
            android.content.res.Resources r0 = r21.getResources()
            r7 = 286130261(0x110e0055, float:1.1201927E-28)
            java.lang.String r7 = r0.getString(r7)
            android.content.res.Resources r0 = r21.getResources()
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]
            r10 = 0
            r9[r10] = r7
            r11 = 286130259(0x110e0053, float:1.1201925E-28)
            java.lang.String r9 = r0.getString(r11, r9)
            int r11 = r9.indexOf(r7)
            int r0 = r7.length()
            int r12 = r11 + r0
            android.text.SpannableString r0 = new android.text.SpannableString
            r0.<init>(r9)
            r13 = r0
            android.text.style.UnderlineSpan r0 = new android.text.style.UnderlineSpan
            r0.<init>()
            r14 = 33
            r13.setSpan(r0, r11, r12, r14)
            com.android.server.autofill.ui.SaveUiInjector$1 r0 = new com.android.server.autofill.ui.SaveUiInjector$1
            r0.<init>(r2)
            r13.setSpan(r0, r11, r12, r14)
            android.content.res.Resources r0 = r21.getResources()
            r15 = 285540358(0x11050006, float:1.0491857E-28)
            int r15 = r0.getColor(r15)
            android.text.style.ForegroundColorSpan r0 = new android.text.style.ForegroundColorSpan
            r0.<init>(r15)
            r13.setSpan(r0, r11, r12, r14)
            r0 = 285802530(0x11090022, float:1.0807435E-28)
            android.view.View r0 = r6.findViewById(r0)
            r14 = r0
            android.widget.TextView r14 = (android.widget.TextView) r14
            r14.setText(r13)
            android.text.method.MovementMethod r0 = android.text.method.LinkMovementMethod.getInstance()
            r14.setMovementMethod(r0)
            miui.app.AlertDialog$Builder r0 = new miui.app.AlertDialog$Builder
            int r5 = miui.R.style.Theme_DayNight_Dialog_Alert
            r0.<init>(r1, r5)
            miui.app.AlertDialog$Builder r0 = r0.setView(r6)
            r5 = 286130260(0x110e0054, float:1.1201926E-28)
            miui.app.AlertDialog$Builder r0 = r0.setTitle(r5)
            android.content.res.Resources r5 = r21.getResources()
            r8 = 286130451(0x110e0113, float:1.1202156E-28)
            java.lang.String r5 = r5.getString(r8)
            miui.app.AlertDialog$Builder r0 = r0.setCheckBox(r10, r5)
            com.android.server.autofill.ui.-$$Lambda$SaveUiInjector$bzUWDoNjzqx2fb5sKPN3lCavQ6Q r5 = new com.android.server.autofill.ui.-$$Lambda$SaveUiInjector$bzUWDoNjzqx2fb5sKPN3lCavQ6Q
            r5.<init>(r3)
            r8 = 286130258(0x110e0052, float:1.1201924E-28)
            miui.app.AlertDialog$Builder r0 = r0.setPositiveButton(r8, r5)
            com.android.server.autofill.ui.-$$Lambda$SaveUiInjector$ZF_imrMlz0JgFl42oIS6dPyHUxE r5 = new com.android.server.autofill.ui.-$$Lambda$SaveUiInjector$ZF_imrMlz0JgFl42oIS6dPyHUxE
            r5.<init>(r2)
            r8 = 17039360(0x1040000, float:2.424457E-38)
            miui.app.AlertDialog$Builder r0 = r0.setNegativeButton(r8, r5)
            miui.app.AlertDialog r5 = r0.create()
            com.android.server.autofill.ui.-$$Lambda$SaveUiInjector$1JYPltIy-i0zYdBHeGDanVW4pDg r0 = new com.android.server.autofill.ui.-$$Lambda$SaveUiInjector$1JYPltIy-i0zYdBHeGDanVW4pDg
            r8 = r25
            r0.<init>(r8)
            r5.setOnDismissListener(r0)
            android.view.Window r10 = r5.getWindow()
            r0 = 2038(0x7f6, float:2.856E-42)
            r10.setType(r0)
            r0 = 393248(0x60020, float:5.51058E-40)
            r10.addFlags(r0)
            r0 = 16
            r10.addPrivateFlags(r0)
            r0 = 32
            r10.setSoftInputMode(r0)
            r0 = 1
            r10.setCloseOnTouchOutside(r0)
            r16 = 0
            r17 = 0
            r18 = 0
            java.lang.String r0 = "com.miui.contentcatcher"
            r19 = r6
            r6 = 3
            android.content.Context r0 = r1.createPackageContext(r0, r6)     // Catch:{ NameNotFoundException -> 0x0116 }
            java.lang.String r6 = "multi_process"
            r20 = r7
            r7 = 4
            android.content.SharedPreferences r6 = r0.getSharedPreferences(r6, r7)     // Catch:{ NameNotFoundException -> 0x0114 }
            java.lang.String r7 = "never_show_save_ui"
            r8 = 0
            boolean r7 = r6.getBoolean(r7, r8)     // Catch:{ NameNotFoundException -> 0x0114 }
            r16 = r7
            java.lang.String r7 = "auto_save"
            boolean r7 = r6.getBoolean(r7, r8)     // Catch:{ NameNotFoundException -> 0x0114 }
            r17 = r7
            java.lang.String r7 = "auto_cancel"
            boolean r7 = r6.getBoolean(r7, r8)     // Catch:{ NameNotFoundException -> 0x0114 }
            r18 = r7
            r0 = r16
            r6 = r17
            goto L_0x013c
        L_0x0114:
            r0 = move-exception
            goto L_0x011f
        L_0x0116:
            r0 = move-exception
            r20 = r7
            goto L_0x011f
        L_0x011a:
            r0 = move-exception
            r19 = r6
            r20 = r7
        L_0x011f:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "SaveUi  e="
            r6.append(r7)
            r6.append(r0)
            java.lang.String r6 = r6.toString()
            android.util.Log.d(r4, r6)
            r0.printStackTrace()
            r0 = r16
            r6 = r17
            r7 = r18
        L_0x013c:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r16 = r9
            java.lang.String r9 = "SaveUi  neverShow="
            r8.append(r9)
            r8.append(r0)
            java.lang.String r9 = ",  isAutoSave="
            r8.append(r9)
            r8.append(r6)
            java.lang.String r9 = ",  isAutoCancel="
            r8.append(r9)
            r8.append(r7)
            java.lang.String r8 = r8.toString()
            android.util.Log.d(r4, r8)
            if (r0 != 0) goto L_0x0178
            mDialog = r5
            r5.show()
            android.content.Intent r4 = new android.content.Intent
            java.lang.String r8 = "intent.action.saveui"
            r4.<init>(r8)
            r1.sendBroadcast(r4)
            r22.hideOverlays()
            goto L_0x0187
        L_0x0178:
            if (r6 == 0) goto L_0x0180
            r4 = 0
            r8 = 0
            r3.onClick(r4, r8)
            goto L_0x0187
        L_0x0180:
            r4 = 0
            r8 = 0
            if (r7 == 0) goto L_0x0187
            r2.onClick(r4, r8)
        L_0x0187:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.ui.SaveUiInjector.showDialog(android.content.Context, com.android.server.autofill.ui.OverlayControl, android.content.DialogInterface$OnClickListener, android.content.DialogInterface$OnClickListener, android.content.DialogInterface$OnDismissListener):android.app.Dialog");
    }

    static /* synthetic */ void lambda$showDialog$0(DialogInterface.OnClickListener okListener, DialogInterface v, int w) {
        Log.d(TAG, "showDialog  save");
        okListener.onClick((DialogInterface) null, 0);
        autoSave();
        mDialog = null;
    }

    static /* synthetic */ void lambda$showDialog$1(DialogInterface.OnClickListener cancelListener, DialogInterface v, int w) {
        Log.d(TAG, "showDialog  cancel");
        cancelListener.onClick((DialogInterface) null, 0);
        autoCancel();
        mDialog = null;
    }

    static /* synthetic */ void lambda$showDialog$2(DialogInterface.OnDismissListener onDismissListener, DialogInterface v) {
        Log.d(TAG, "showDialog  dismiss");
        onDismissListener.onDismiss((DialogInterface) null);
        mDialog = null;
    }

    public static void changeBackground(View decor, WindowManager.LayoutParams params) {
        if (decor != null && params != null) {
            String autofillService = Settings.Secure.getStringForUser(decor.getContext().getContentResolver(), "autofill_service", UserHandle.myUserId());
            if (!TextUtils.isEmpty(autofillService) && TextUtils.equals(autofillService.split(SliceClientPermissions.SliceAuthority.DELIMITER)[0], AUTOFILL_PACKAGE)) {
                decor.setBackgroundResource(R.drawable.text_select_bg);
                if (TextUtils.equals(MIUI_VERSION_NAME, "V11") || TextUtils.equals(MIUI_VERSION_NAME, "V12")) {
                    params.x -= 40;
                    params.y -= 80;
                    params.width += 80;
                    params.height += 160;
                    return;
                }
                params.x -= 60;
                params.y -= 60;
                params.width += 120;
                params.height += 120;
            }
        }
    }

    public static void autoSave() {
        AlertDialog alertDialog = mDialog;
        if (alertDialog != null && alertDialog.isChecked()) {
            Log.d(TAG, "autoSave  checked=true");
            try {
                SharedPreferences sharedPreferences = mDialog.getContext().createPackageContext(AUTOFILL_PACKAGE, 3).getSharedPreferences(SERVICE_SP_NAME, 4);
                sharedPreferences.edit().putBoolean(NEVER_SHOW_SAVE_UI, true).apply();
                sharedPreferences.edit().putBoolean(AUTO_SAVE, true).apply();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void autoCancel() {
        AlertDialog alertDialog = mDialog;
        if (alertDialog != null && alertDialog.isChecked()) {
            Log.d(TAG, "autoCancel  checked=true");
            try {
                SharedPreferences sharedPreferences = mDialog.getContext().createPackageContext(AUTOFILL_PACKAGE, 3).getSharedPreferences(SERVICE_SP_NAME, 4);
                sharedPreferences.edit().putBoolean(NEVER_SHOW_SAVE_UI, true).apply();
                sharedPreferences.edit().putBoolean(AUTO_CANCEL, true).apply();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
