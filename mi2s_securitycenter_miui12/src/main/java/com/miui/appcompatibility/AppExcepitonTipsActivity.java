package com.miui.appcompatibility;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.miui.gamebooster.m.C0375f;
import com.miui.permcenter.compact.MiuiSettingsCompat;
import java.util.List;
import miui.R;
import miui.app.AlertActivity;
import org.json.JSONObject;

public class AppExcepitonTipsActivity extends AlertActivity implements DialogInterface.OnCancelListener {

    /* renamed from: a  reason: collision with root package name */
    private View f3062a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public TextView f3063b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public String f3064c = "该应用";
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public String f3065d;
    /* access modifiers changed from: private */
    public String e;
    /* access modifiers changed from: private */
    public String f;
    /* access modifiers changed from: private */
    public String g = "com.miui.appcompatibility.LaunchDialog.appstore";

    /* access modifiers changed from: private */
    public boolean a() {
        if (C0375f.a()) {
            return false;
        }
        List<Object> cloudDataList = MiuiSettingsCompat.getCloudDataList(getContentResolver(), "app_compatibility");
        if (cloudDataList == null || cloudDataList.size() == 0) {
            Log.d("AppExcepitonTipsActivity", "dataList=null");
            return false;
        }
        try {
            for (Object obj : cloudDataList) {
                String obj2 = obj.toString();
                if (!TextUtils.isEmpty(obj2)) {
                    JSONObject jSONObject = new JSONObject(obj2);
                    if (jSONObject.has("lauch")) {
                        String optString = jSONObject.optString("launch");
                        if (!TextUtils.isEmpty(optString)) {
                            this.e = optString;
                        }
                    }
                    if (!jSONObject.has("store")) {
                        return true;
                    }
                    String optString2 = jSONObject.optString("store");
                    if (TextUtils.isEmpty(optString2)) {
                        return true;
                    }
                    this.f = optString2;
                    return true;
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x003a  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x004d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void b() {
        /*
            r9 = this;
            boolean r0 = com.miui.appcompatibility.n.a()
            if (r0 != 0) goto L_0x000e
            r0 = -1
            r9.setResult(r0)
            r9.finish()
            return
        L_0x000e:
            r0 = 2131755316(0x7f100134, float:1.9141508E38)
            java.lang.String r0 = r9.getString(r0)
            r9.e = r0
            r0 = 2131755317(0x7f100135, float:1.914151E38)
            java.lang.String r0 = r9.getString(r0)
            r9.f = r0
            java.lang.String r0 = r9.f
            r9.f3065d = r0
            r0 = 0
            java.lang.Class<miui.app.AlertActivity> r1 = miui.app.AlertActivity.class
            java.lang.String r2 = "mAlertParams"
            java.lang.Object r1 = b.b.o.g.e.a((java.lang.Object) r9, (java.lang.Class<?>) r1, (java.lang.String) r2)     // Catch:{ NoSuchFieldException -> 0x0033, IllegalAccessException -> 0x002e }
            goto L_0x0038
        L_0x002e:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x0037
        L_0x0033:
            r1 = move-exception
            r1.printStackTrace()
        L_0x0037:
            r1 = r0
        L_0x0038:
            if (r1 != 0) goto L_0x003d
            r9.finish()
        L_0x003d:
            java.lang.Class r2 = r1.getClass()
            java.lang.Class r2 = r2.getSuperclass()
            android.content.Intent r3 = r9.getIntent()
            java.lang.String r4 = "AppExcepitonTipsActivity"
            if (r3 == 0) goto L_0x0099
            java.lang.String r5 = "app_name"
            java.lang.String r5 = r3.getStringExtra(r5)
            boolean r6 = android.text.TextUtils.isEmpty(r5)
            if (r6 != 0) goto L_0x0095
            r9.f3064c = r5
            java.lang.String r3 = r3.getAction()
            r9.g = r3
            java.lang.String r3 = r9.g
            if (r3 != 0) goto L_0x0069
            r9.finish()
            return
        L_0x0069:
            java.lang.String r5 = "com.miui.appcompatibility.LaunchDialog.launcher"
            boolean r3 = r3.equals(r5)
            java.lang.String r5 = "关闭"
            java.lang.String r6 = "mPositiveButtonText"
            java.lang.String r7 = "mNegativeButtonText"
            if (r3 == 0) goto L_0x0084
            java.lang.String r3 = r9.e
            r9.f3065d = r3
            java.lang.String r3 = "继续运行"
        L_0x007d:
            com.miui.permcenter.compact.ReflectUtilHelper.setObjectField(r4, r1, r2, r7, r3)
            com.miui.permcenter.compact.ReflectUtilHelper.setObjectField(r4, r1, r2, r6, r5)
            goto L_0x0099
        L_0x0084:
            java.lang.String r3 = r9.g
            java.lang.String r8 = "com.miui.appcompatibility.LaunchDialog.appstore"
            boolean r3 = r3.equals(r8)
            if (r3 == 0) goto L_0x0099
            java.lang.String r3 = r9.f
            r9.f3065d = r3
            java.lang.String r3 = "仍然安装"
            goto L_0x007d
        L_0x0095:
            r9.finish()
            return
        L_0x0099:
            java.lang.String r3 = "mTitle"
            java.lang.String r5 = "应用异常提示"
            com.miui.permcenter.compact.ReflectUtilHelper.setObjectField(r4, r1, r2, r3, r5)
            android.view.LayoutInflater r3 = r9.getLayoutInflater()
            r5 = 2131492893(0x7f0c001d, float:1.860925E38)
            android.view.View r0 = r3.inflate(r5, r0)
            r9.f3062a = r0
            android.view.View r0 = r9.f3062a
            r3 = 2131297360(0x7f090450, float:1.8212663E38)
            android.view.View r0 = r0.findViewById(r3)
            android.widget.TextView r0 = (android.widget.TextView) r0
            r9.f3063b = r0
            android.widget.TextView r0 = r9.f3063b
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = r9.f3064c
            r3.append(r5)
            java.lang.String r5 = r9.f3065d
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            r0.setText(r3)
            android.view.View r0 = r9.f3062a
            java.lang.String r3 = "mView"
            com.miui.permcenter.compact.ReflectUtilHelper.setObjectField(r4, r1, r2, r3, r0)
            com.miui.appcompatibility.e r0 = new com.miui.appcompatibility.e
            r0.<init>(r9)
            java.lang.String r3 = "mNegativeButtonListener"
            com.miui.permcenter.compact.ReflectUtilHelper.setObjectField(r4, r1, r2, r3, r0)
            com.miui.appcompatibility.f r0 = new com.miui.appcompatibility.f
            r0.<init>(r9)
            java.lang.String r3 = "mPositiveButtonListener"
            com.miui.permcenter.compact.ReflectUtilHelper.setObjectField(r4, r1, r2, r3, r0)
            com.miui.appcompatibility.g r0 = new com.miui.appcompatibility.g
            r0.<init>(r9)
            java.util.concurrent.Executor r1 = android.os.AsyncTask.THREAD_POOL_EXECUTOR
            r2 = 0
            java.lang.Void[] r2 = new java.lang.Void[r2]
            r0.executeOnExecutor(r1, r2)
            r9.setupAlert()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.appcompatibility.AppExcepitonTipsActivity.b():void");
    }

    public void onBackPressed() {
        AppExcepitonTipsActivity.super.onBackPressed();
        a.a("module_click", "back");
    }

    public void onCancel(DialogInterface dialogInterface) {
        setResult(0);
        finish();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        AppExcepitonTipsActivity.super.onCreate(bundle);
        setTheme(R.style.Theme_Light_Dialog_Alert);
        b();
        a.a("module_show", this.f3064c);
    }
}
