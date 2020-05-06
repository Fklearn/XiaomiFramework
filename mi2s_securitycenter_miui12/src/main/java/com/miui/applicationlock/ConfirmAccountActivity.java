package com.miui.applicationlock;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.A;
import com.miui.applicationlock.a.h;
import com.miui.applicationlock.c.C0259c;
import com.miui.applicationlock.c.K;
import com.miui.applicationlock.c.o;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import com.xiaomi.accountsdk.account.IXiaomiAccountService;
import miui.cloud.Constants;
import miui.security.SecurityManager;

public class ConfirmAccountActivity extends b.b.c.c.a implements View.OnClickListener {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public ImageView f3146a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public Account f3147b;

    /* renamed from: c  reason: collision with root package name */
    private boolean f3148c;

    /* renamed from: d  reason: collision with root package name */
    private C0259c f3149d;
    private SecurityManager e;
    /* access modifiers changed from: private */
    public boolean f;
    /* access modifiers changed from: private */
    public boolean g;

    private class a implements ServiceConnection {

        /* renamed from: a  reason: collision with root package name */
        private IXiaomiAccountService f3150a;

        private a() {
        }

        /* synthetic */ a(ConfirmAccountActivity confirmAccountActivity, C0297ra raVar) {
            this();
        }

        /* JADX WARNING: Removed duplicated region for block: B:29:0x0089 A[SYNTHETIC, Splitter:B:29:0x0089] */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x0097 A[SYNTHETIC, Splitter:B:34:0x0097] */
        /* JADX WARNING: Removed duplicated region for block: B:42:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onServiceConnected(android.content.ComponentName r10, android.os.IBinder r11) {
            /*
                r9 = this;
                java.lang.String r10 = "ParcelFileDescriptor close exception"
                java.lang.String r0 = "ConfirmAccountActivity"
                com.xiaomi.accountsdk.account.IXiaomiAccountService r11 = com.xiaomi.accountsdk.account.IXiaomiAccountService.Stub.a(r11)
                r9.f3150a = r11
                com.miui.applicationlock.ConfirmAccountActivity r11 = com.miui.applicationlock.ConfirmAccountActivity.this
                android.widget.ImageView r11 = r11.f3146a
                com.miui.applicationlock.ConfirmAccountActivity r1 = com.miui.applicationlock.ConfirmAccountActivity.this
                android.content.res.Resources r1 = r1.getResources()
                r2 = 2131231653(0x7f0803a5, float:1.8079393E38)
                android.graphics.drawable.Drawable r1 = r1.getDrawable(r2)
                r11.setImageDrawable(r1)
                r11 = 0
                com.miui.applicationlock.ConfirmAccountActivity r1 = com.miui.applicationlock.ConfirmAccountActivity.this     // Catch:{ Exception -> 0x0079, all -> 0x0076 }
                android.accounts.Account r1 = r1.f3147b     // Catch:{ Exception -> 0x0079, all -> 0x0076 }
                if (r1 == 0) goto L_0x0063
                com.xiaomi.accountsdk.account.IXiaomiAccountService r1 = r9.f3150a     // Catch:{ Exception -> 0x0079, all -> 0x0076 }
                com.miui.applicationlock.ConfirmAccountActivity r2 = com.miui.applicationlock.ConfirmAccountActivity.this     // Catch:{ Exception -> 0x0079, all -> 0x0076 }
                android.accounts.Account r2 = r2.f3147b     // Catch:{ Exception -> 0x0079, all -> 0x0076 }
                android.os.ParcelFileDescriptor r1 = r1.c(r2)     // Catch:{ Exception -> 0x0079, all -> 0x0076 }
                if (r1 == 0) goto L_0x0064
                java.io.FileDescriptor r2 = r1.getFileDescriptor()     // Catch:{ Exception -> 0x0061 }
                if (r2 == 0) goto L_0x0064
                android.graphics.Bitmap r3 = android.graphics.BitmapFactory.decodeFileDescriptor(r2)     // Catch:{ Exception -> 0x0061 }
                if (r3 == 0) goto L_0x0064
                int r4 = r3.getWidth()     // Catch:{ Exception -> 0x0061 }
                int r5 = r3.getHeight()     // Catch:{ Exception -> 0x0061 }
                int r2 = r3.getWidth()     // Catch:{ Exception -> 0x0061 }
                int r6 = r2 / 2
                r7 = -1
                r8 = 1
                android.graphics.Bitmap r2 = com.miui.applicationlock.c.o.a(r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0061 }
                com.miui.applicationlock.ConfirmAccountActivity r3 = com.miui.applicationlock.ConfirmAccountActivity.this     // Catch:{ Exception -> 0x0061 }
                android.widget.ImageView r3 = r3.f3146a     // Catch:{ Exception -> 0x0061 }
                r3.setImageBitmap(r2)     // Catch:{ Exception -> 0x0061 }
                goto L_0x0064
            L_0x0061:
                r2 = move-exception
                goto L_0x007b
            L_0x0063:
                r1 = r11
            L_0x0064:
                com.miui.applicationlock.ConfirmAccountActivity r2 = com.miui.applicationlock.ConfirmAccountActivity.this
                r2.unbindService(r9)
                r9.f3150a = r11
                if (r1 == 0) goto L_0x008c
                r1.close()     // Catch:{ IOException -> 0x0071 }
                goto L_0x008c
            L_0x0071:
                r11 = move-exception
                android.util.Log.e(r0, r10, r11)
                goto L_0x008c
            L_0x0076:
                r2 = move-exception
                r1 = r11
                goto L_0x008e
            L_0x0079:
                r2 = move-exception
                r1 = r11
            L_0x007b:
                java.lang.String r3 = "Fail getAvatarFd"
                android.util.Log.e(r0, r3, r2)     // Catch:{ all -> 0x008d }
                com.miui.applicationlock.ConfirmAccountActivity r2 = com.miui.applicationlock.ConfirmAccountActivity.this
                r2.unbindService(r9)
                r9.f3150a = r11
                if (r1 == 0) goto L_0x008c
                r1.close()     // Catch:{ IOException -> 0x0071 }
            L_0x008c:
                return
            L_0x008d:
                r2 = move-exception
            L_0x008e:
                com.miui.applicationlock.ConfirmAccountActivity r3 = com.miui.applicationlock.ConfirmAccountActivity.this
                r3.unbindService(r9)
                r9.f3150a = r11
                if (r1 == 0) goto L_0x009f
                r1.close()     // Catch:{ IOException -> 0x009b }
                goto L_0x009f
            L_0x009b:
                r11 = move-exception
                android.util.Log.e(r0, r10, r11)
            L_0x009f:
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.applicationlock.ConfirmAccountActivity.a.onServiceConnected(android.content.ComponentName, android.os.IBinder):void");
        }

        public void onServiceDisconnected(ComponentName componentName) {
        }
    }

    private void a(Activity activity, Bundle bundle, C0259c cVar) {
        AccountManager.get(activity).addAccount(Constants.XIAOMI_ACCOUNT_TYPE, "passportapi", (String[]) null, bundle, activity, new C0297ra(this, cVar, activity), (Handler) null);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccountActivity, miui.app.Activity, android.app.Activity] */
    private void l() {
        if (this.f3148c) {
            h.a(this.f ? "app_binding_result" : "binding_result", "logged_in_binding");
            this.f3149d.a(K.d(getApplicationContext()));
            A.a((Context) this, getResources().getString(R.string.bind_xiaomi_account_success));
            Intent intent = new Intent(this, PrivacyAndAppLockManageActivity.class);
            intent.putExtra("extra_data", "not_home_start");
            setResult(-1, intent);
            finish();
            return;
        }
        if (!o.a(this.e, "com.xiaomi.account")) {
            o.b(this.e, "com.xiaomi.account");
        }
        a(this, new Bundle(), this.f3149d);
    }

    private void m() {
        int i;
        this.f3146a = (ImageView) findViewById(R.id.applock_icon);
        TextView textView = (TextView) findViewById(R.id.applock_account_text);
        Button button = (Button) findViewById(R.id.applock_button_confirm_lock);
        ((TextView) findViewById(R.id.applock_button_confirm_lock_unlock)).setOnClickListener(this);
        button.setOnClickListener(this);
        this.f3147b = K.b(getApplicationContext());
        this.f3149d = C0259c.b(getApplicationContext());
        this.e = (SecurityManager) getSystemService("security");
        AccountManager accountManager = (AccountManager) getSystemService("account");
        this.f3148c = this.f3147b != null;
        if (!this.f3148c || TextUtils.isEmpty(this.f3147b.name)) {
            textView.setText(R.string.applock_not_login_account);
            i = R.string.applock_login_and_add_account;
        } else {
            textView.setText(accountManager.getUserData(this.f3147b, "acc_user_name"));
            i = R.string.applock_add_account;
        }
        button.setText(i);
        n();
        this.f = getIntent().getBooleanExtra("account_dialog_extra_data", false);
    }

    private void n() {
        Intent intent = new Intent("com.xiaomi.account.action.BIND_XIAOMI_ACCOUNT_SERVICE");
        intent.setPackage("com.xiaomi.account");
        bindService(intent, new a(this, (C0297ra) null), 1);
    }

    private void o() {
        String str;
        String str2 = "app_binding_result";
        if (this.f3148c) {
            if (!this.f) {
                str2 = "binding_result";
            }
            str = "logged_in_skip";
        } else {
            if (!this.f) {
                str2 = "binding_result";
            }
            str = this.g ? "not_logged_cancel_login_skip" : "not_logged_skip";
        }
        h.a(str2, str);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccountActivity, miui.app.Activity] */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.applock_button_confirm_lock /*2131296463*/:
                l();
                return;
            case R.id.applock_button_confirm_lock_unlock /*2131296464*/:
                o();
                Intent intent = new Intent(this, PrivacyAndAppLockManageActivity.class);
                intent.putExtra("extra_data", "not_home_start");
                setResult(-1, intent);
                finish();
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.confirm_account_applock);
        if (k.a() >= 10) {
            getActionBar().setExpandState(0);
        }
        m();
    }
}
