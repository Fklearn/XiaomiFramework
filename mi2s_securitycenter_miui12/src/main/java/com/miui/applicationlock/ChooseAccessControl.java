package com.miui.applicationlock;

import android.app.AppOpsManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import b.b.o.g.e;
import com.miui.applicationlock.a.h;
import com.miui.applicationlock.c.C0259c;
import com.miui.applicationlock.c.o;
import com.miui.applicationlock.c.p;
import com.miui.applicationlock.widget.C0308a;
import com.miui.applicationlock.widget.LinearLayoutWithDefaultTouchRecepient;
import com.miui.applicationlock.widget.LockPatternView;
import com.miui.applicationlock.widget.PasswordUnlockMediator;
import com.miui.applicationlock.widget.PercentLayout;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import java.util.Locale;
import miui.app.AlertDialog;
import miui.security.SecurityManager;

public class ChooseAccessControl extends b.b.c.c.a implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    public static int f3115a = 4;

    /* renamed from: b  reason: collision with root package name */
    protected TextView f3116b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public TextView f3117c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public TextView f3118d;
    /* access modifiers changed from: private */
    public TextView e;
    private boolean f;
    private String g;
    private C0259c h;
    private AppOpsManager i;
    private IBinder j;
    /* access modifiers changed from: private */
    public C0308a k;
    /* access modifiers changed from: private */
    public String l;
    /* access modifiers changed from: private */
    public Editable m;
    /* access modifiers changed from: private */
    public String n;
    /* access modifiers changed from: private */
    public AccessibilityManager o;
    /* access modifiers changed from: private */
    public PasswordUnlockMediator p;
    private LinearLayoutWithDefaultTouchRecepient q;
    /* access modifiers changed from: private */
    public TextView r;
    /* access modifiers changed from: private */
    public c s = c.Introduction;
    private DialogInterface.OnClickListener t = new I(this);
    private p u = new J(this);

    enum a {
        Cancel(R.string.cancel, true),
        CancelDisable(R.string.cancel, false),
        Retry(R.string.lockpattern_retry_button_text, true),
        RetryNumeric(R.string.numeric_retry_button_text, true),
        RetryMixed(R.string.mixed_retry_button_text, true),
        RetryDisabled(R.string.lockpattern_retry_button_text, false),
        Gone(-1, false);
        
        final int i;
        final boolean j;

        private a(int i2, boolean z) {
            this.i = i2;
            this.j = z;
        }
    }

    enum b {
        Continue(R.string.lockpattern_continue_button_text, true),
        ContinueDisabled(R.string.lockpattern_continue_button_text, false),
        Confirm(R.string.lockpattern_confirm_button_text, true),
        ConfirmDisabled(R.string.lockpattern_confirm_button_text, false),
        Ok(17039370, true),
        Gone(-1, false);
        
        /* access modifiers changed from: private */
        public int h;
        /* access modifiers changed from: private */
        public boolean i;

        private b(int i2, boolean z) {
            this.h = i2;
            this.i = z;
        }

        public void a(int i2) {
            this.h = i2;
        }
    }

    protected enum c {
        Introduction(R.string.lockpattern_recording_intro_header, a.Gone, b.Gone, -1, true),
        ChoiceTooShort(R.plurals.lockpattern_recording_incorrect_too_short, a.Gone, b.Gone, -1, true),
        FirstChoiceValid(R.string.lockpattern_pattern_entered_header, a.Gone, b.Gone, -1, false),
        NeedToConfirm(R.string.lockpattern_need_to_confirm, a.Retry, b.ConfirmDisabled, -1, true),
        ConfirmWrong(R.string.lockpattern_need_to_unlock_wrong, a.Retry, b.ConfirmDisabled, -1, true),
        ChoiceConfirmed(R.string.lockpattern_pattern_confirmed_header, a.Retry, b.Confirm, -1, false);
        
        int h;
        a i;
        b j;
        final int k;
        final boolean l;

        private c(int i2, a aVar, b bVar, int i3, boolean z) {
            this.h = i2;
            this.i = aVar;
            this.j = bVar;
            this.k = i3;
            this.l = z;
        }

        public void a(int i2) {
            this.h = i2;
        }

        public void a(a aVar) {
            this.i = aVar;
        }

        public void a(b bVar) {
            this.j = bVar;
        }
    }

    private void a(int i2, a aVar, b bVar, int i3, int i4, int i5, int i6, a aVar2, a aVar3, int i7, a aVar4) {
        c.Introduction.a(i2);
        c.Introduction.a(aVar);
        c.Introduction.a(bVar);
        c.ChoiceTooShort.a(i4);
        c.FirstChoiceValid.a(i5);
        c.NeedToConfirm.a(i6);
        c.NeedToConfirm.a(aVar2);
        c.ConfirmWrong.a(aVar3);
        c.ChoiceConfirmed.a(i7);
        c.ChoiceConfirmed.a(aVar4);
    }

    private void a(int i2, boolean z) {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                e.a((Object) this.i, "setUserRestriction", (Class<?>[]) new Class[]{Integer.TYPE, Boolean.TYPE, IBinder.class}, Integer.valueOf(i2), Boolean.valueOf(z), this.j);
            } catch (Exception e2) {
                Log.e("ChooseAccessControl", "restrictOpsWindow error", e2);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0027  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x008c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void b(java.lang.String r14) {
        /*
            r13 = this;
            int r0 = r14.hashCode()
            r1 = -2000413939(0xffffffff88c41b0d, float:-1.18026805E-33)
            r2 = 1
            if (r0 == r1) goto L_0x001a
            r1 = 103910395(0x6318bfb, float:3.339284E-35)
            if (r0 == r1) goto L_0x0010
            goto L_0x0024
        L_0x0010:
            java.lang.String r0 = "mixed"
            boolean r14 = r14.equals(r0)
            if (r14 == 0) goto L_0x0024
            r14 = r2
            goto L_0x0025
        L_0x001a:
            java.lang.String r0 = "numeric"
            boolean r14 = r14.equals(r0)
            if (r14 == 0) goto L_0x0024
            r14 = 0
            goto L_0x0025
        L_0x0024:
            r14 = -1
        L_0x0025:
            if (r14 == 0) goto L_0x008c
            if (r14 == r2) goto L_0x005e
            android.widget.TextView r14 = r13.e
            r0 = 2131757813(0x7f100af5, float:1.9146572E38)
            r14.setText(r0)
            r2 = 2131756688(0x7f100690, float:1.914429E38)
            com.miui.applicationlock.ChooseAccessControl$a r3 = com.miui.applicationlock.ChooseAccessControl.a.Gone
            com.miui.applicationlock.ChooseAccessControl$b r4 = com.miui.applicationlock.ChooseAccessControl.b.Gone
            r5 = 2131756690(0x7f100692, float:1.9144295E38)
            r6 = 2131623995(0x7f0e003b, float:1.8875157E38)
            r7 = 2131756686(0x7f10068e, float:1.9144287E38)
            r8 = 2131756683(0x7f10068b, float:1.914428E38)
            com.miui.applicationlock.ChooseAccessControl$a r12 = com.miui.applicationlock.ChooseAccessControl.a.Retry
            r11 = 2131756685(0x7f10068d, float:1.9144284E38)
            r1 = r13
            r9 = r12
            r10 = r12
        L_0x004c:
            r1.a(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
        L_0x004f:
            android.view.accessibility.AccessibilityManager r14 = r13.o
            android.content.res.Resources r1 = r13.getResources()
            java.lang.String r0 = r1.getString(r0)
            com.miui.applicationlock.c.o.a((android.view.accessibility.AccessibilityManager) r14, (java.lang.String) r0)
            goto L_0x00c6
        L_0x005e:
            android.widget.TextView r14 = r13.e
            r0 = 2131757811(0x7f100af3, float:1.9146568E38)
            r14.setText(r0)
            r2 = 2131756905(0x7f100769, float:1.914473E38)
            com.miui.applicationlock.ChooseAccessControl$a r3 = com.miui.applicationlock.ChooseAccessControl.a.RetryNumeric
            com.miui.applicationlock.ChooseAccessControl$b r4 = com.miui.applicationlock.ChooseAccessControl.b.ContinueDisabled
            r5 = 2131756907(0x7f10076b, float:1.9144735E38)
            r6 = 2131624003(0x7f0e0043, float:1.8875173E38)
            r7 = 2131756904(0x7f100768, float:1.9144729E38)
            r8 = 2131756901(0x7f100765, float:1.9144723E38)
            com.miui.applicationlock.ChooseAccessControl$a r10 = com.miui.applicationlock.ChooseAccessControl.a.RetryMixed
            boolean r14 = r13.f
            if (r14 == 0) goto L_0x0083
            r14 = 2131756902(0x7f100766, float:1.9144725E38)
            goto L_0x0086
        L_0x0083:
            r14 = 2131756903(0x7f100767, float:1.9144727E38)
        L_0x0086:
            r11 = r14
            com.miui.applicationlock.ChooseAccessControl$a r12 = com.miui.applicationlock.ChooseAccessControl.a.RetryMixed
            r1 = r13
            r9 = r10
            goto L_0x004c
        L_0x008c:
            android.widget.TextView r14 = r13.e
            r0 = 2131757812(0x7f100af4, float:1.914657E38)
            r14.setText(r0)
            r2 = 2131757016(0x7f1007d8, float:1.9144956E38)
            com.miui.applicationlock.ChooseAccessControl$a r3 = com.miui.applicationlock.ChooseAccessControl.a.RetryNumeric
            com.miui.applicationlock.ChooseAccessControl$b r4 = com.miui.applicationlock.ChooseAccessControl.b.ContinueDisabled
            r5 = 2131757018(0x7f1007da, float:1.914496E38)
            r6 = 2131624014(0x7f0e004e, float:1.8875196E38)
            r7 = 2131757015(0x7f1007d7, float:1.9144954E38)
            r8 = 2131757012(0x7f1007d4, float:1.9144948E38)
            com.miui.applicationlock.ChooseAccessControl$a r10 = com.miui.applicationlock.ChooseAccessControl.a.RetryNumeric
            boolean r14 = r13.f
            if (r14 == 0) goto L_0x00b1
            r14 = 2131757013(0x7f1007d5, float:1.914495E38)
            goto L_0x00b4
        L_0x00b1:
            r14 = 2131757014(0x7f1007d6, float:1.9144952E38)
        L_0x00b4:
            r11 = r14
            com.miui.applicationlock.ChooseAccessControl$a r12 = com.miui.applicationlock.ChooseAccessControl.a.RetryNumeric
            r1 = r13
            r9 = r10
            r1.a(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            android.view.Window r14 = r13.getWindow()
            r1 = 16
            r14.setSoftInputMode(r1)
            goto L_0x004f
        L_0x00c6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.applicationlock.ChooseAccessControl.b(java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public void c(String str) {
        PercentLayout.a aVar;
        int i2;
        Resources resources;
        TypedValue typedValue;
        if ("pattern".equals(this.n)) {
            aVar = (PercentLayout.a) this.f3116b.getLayoutParams();
            typedValue = new TypedValue();
            resources = getResources();
            i2 = R.dimen.pattern_header_marginTop_percent;
        } else {
            aVar = (PercentLayout.a) this.f3116b.getLayoutParams();
            typedValue = new TypedValue();
            resources = getResources();
            i2 = R.dimen.password_number_header_marginTop_percent;
        }
        resources.getValue(i2, typedValue, true);
        aVar.a(typedValue.getFloat());
        this.f3116b.requestLayout();
    }

    public static void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService("input_method");
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /* access modifiers changed from: private */
    public void n() {
        c cVar;
        c cVar2 = this.s;
        if (cVar2 == c.Introduction) {
            cVar = c.NeedToConfirm;
        } else if ((cVar2 != c.NeedToConfirm && cVar2 != c.ChoiceConfirmed) || this.m == null || TextUtils.isEmpty(this.l)) {
            return;
        } else {
            if (this.l.equals(this.m.toString())) {
                m();
                return;
            }
            Editable editable = this.m;
            Selection.setSelection(editable, 0, editable.length());
            cVar = c.ConfirmWrong;
        }
        a(cVar);
    }

    /* access modifiers changed from: private */
    public void o() {
        b(this.n);
        this.p.a(this.n);
        this.k = this.p.getUnlockView();
        this.k.setApplockUnlockCallback(this.u);
        this.k.setLightMode(true);
        this.f3117c = (TextView) this.k.findViewById(R.id.footerLeftButton);
        this.f3118d = (TextView) this.k.findViewById(R.id.footerRightButton);
        this.f3117c.setOnClickListener(this);
        this.f3118d.setOnClickListener(this);
        this.q.setDefaultTouchRecepient(this.k);
        l();
        if ("mixed".equals(this.n)) {
            getWindow().addFlags(131072);
        } else {
            getWindow().clearFlags(131072);
        }
    }

    private void p() {
        this.k.a();
    }

    private void q() {
        ((SecurityManager) getSystemService("security")).setAccessControlPassword(this.n, this.l);
        C0259c.b(getApplicationContext()).a(true);
        o.a(0, getApplicationContext());
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [com.miui.applicationlock.ChooseAccessControl, android.content.Context, miui.app.Activity] */
    /* access modifiers changed from: private */
    public void r() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.applock_set_password_title)).setNegativeButton(R.string.cancel, new G(this)).setSingleChoiceItems(R.array.applock_password_types, "pattern".equals(this.n) ? 0 : "numeric".equals(this.n) ? 1 : 2, this.t);
        builder.create().show();
    }

    public static void showKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService("input_method");
        if (inputMethodManager != null) {
            view.requestFocus();
            inputMethodManager.showSoftInput(view, 0);
        }
    }

    /* access modifiers changed from: protected */
    public void a(c cVar) {
        this.s = cVar;
        if (cVar == c.ChoiceTooShort) {
            String quantityString = getResources().getQuantityString(cVar.h, 4, new Object[]{4});
            this.f3116b.setText(quantityString);
            o.a(this.o, quantityString);
        } else {
            if (cVar != c.Introduction || !"mixed".equals(this.n)) {
                this.f3116b.setText(cVar.h);
            } else {
                this.f3116b.setText(getResources().getString(cVar.h, new Object[]{String.format(Locale.getDefault(), "%d", new Object[]{4}), String.format(Locale.getDefault(), "%d", new Object[]{11})}));
            }
            o.a(this.o, getResources().getString(cVar.h));
        }
        if (cVar.i == a.Gone) {
            this.f3117c.setVisibility(8);
        } else {
            this.f3117c.setVisibility(0);
            this.f3117c.setText(cVar.i.i);
            this.f3117c.setEnabled(cVar.i.j);
        }
        if (cVar.j == b.Gone) {
            this.f3118d.setVisibility(8);
        } else {
            this.f3118d.setVisibility(0);
            this.f3118d.setText(cVar.j.h);
            o.a(this.o, getResources().getString(this.f ? R.string.lockpattern_tutorial_continue_label_confirm : R.string.lockpattern_tutorial_continue_label_next));
            this.f3118d.setEnabled(cVar.j.i);
        }
        if (cVar.l) {
            this.k.g();
        } else {
            this.k.b();
        }
        this.k.setDisplayMode(LockPatternView.b.Correct);
        switch (L.f3187a[this.s.ordinal()]) {
            case 1:
                break;
            case 2:
            case 5:
                this.k.setDisplayMode(LockPatternView.b.Wrong);
                p();
                return;
            case 3:
                this.k.postDelayed(new K(this), 500);
                break;
            case 4:
                this.k.d();
                this.k.g();
                return;
            case 6:
                this.k.b();
                return;
            default:
                return;
        }
        this.k.d();
    }

    /* access modifiers changed from: protected */
    public void l() {
        a(c.Introduction);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [com.miui.applicationlock.ChooseAccessControl, android.content.Context, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void m() {
        if (TextUtils.isEmpty(this.l)) {
            l();
            Log.w("ChooseAccessControl", "password is null");
        } else if (this.f) {
            q();
            setResult(-1);
            finish();
        } else {
            startActivityForResult(new Intent(this, ConfirmAccountActivity.class), 1022120);
        }
    }

    public void onActivityResult(int i2, int i3, Intent intent) {
        ChooseAccessControl.super.onActivityResult(i2, i3, intent);
        if (i2 != 55) {
            if (i2 != 56) {
                if (i2 == 1022120) {
                    if (i3 == -1) {
                        if (intent != null) {
                            if (this.h.j() || !TextUtils.isEmpty(this.g)) {
                                setResult(-1, intent);
                            } else {
                                startActivity(intent);
                                setResult(-1);
                            }
                        }
                        if (TextUtils.isEmpty(this.l)) {
                            Log.w("ChooseAccessControl", "password is null");
                            return;
                        }
                        q();
                        ChooseAccessControl.super.finish();
                        return;
                    }
                } else {
                    return;
                }
            } else if (i3 == -1) {
                setResult(-1);
            } else {
                return;
            }
            finish();
            return;
        }
        if (i3 != -1) {
            setResult(0);
            finish();
        }
        a(c.Introduction);
    }

    public void onBackPressed() {
        finish();
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [com.miui.applicationlock.ChooseAccessControl, android.content.Context, miui.app.Activity] */
    public void onClick(View view) {
        StringBuilder sb;
        b bVar;
        c cVar;
        if (view == this.f3117c) {
            a aVar = this.s.i;
            if (aVar == a.Retry) {
                this.l = null;
                this.k.d();
                cVar = c.Introduction;
            } else if (aVar == a.RetryNumeric || aVar == a.RetryMixed) {
                if (!this.f) {
                    Intent intent = new Intent(this, ChooseLockTypeActivity.class);
                    intent.putExtra("cancel_setting_password", true);
                    setResult(0, intent);
                }
                finish();
                return;
            } else {
                Log.d("ChooseAccessControl", "left footer button pressed , but stage of " + this.s + " doesn't make sense");
                return;
            }
        } else if (view != this.f3118d) {
            return;
        } else {
            if ("pattern".equals(this.n)) {
                c cVar2 = this.s;
                b bVar2 = cVar2.j;
                if (bVar2 == b.Continue) {
                    if (cVar2 != c.FirstChoiceValid) {
                        sb = new StringBuilder();
                        sb.append("expected ui stage ");
                        sb.append(c.ChoiceConfirmed);
                        sb.append(" when button is ");
                        bVar = b.Continue;
                    } else {
                        cVar = c.NeedToConfirm;
                    }
                } else if (bVar2 != b.Confirm) {
                    return;
                } else {
                    if (cVar2 != c.ChoiceConfirmed) {
                        sb = new StringBuilder();
                        sb.append("expected ui stage ");
                        sb.append(c.ChoiceConfirmed);
                        sb.append(" when button is ");
                        bVar = b.Confirm;
                    } else {
                        m();
                        return;
                    }
                }
                sb.append(bVar);
                Log.d("ChooseAccessControl", sb.toString());
                return;
            }
            n();
            return;
        }
        a(cVar);
    }

    public void onCreate(Bundle bundle) {
        b bVar;
        int i2;
        super.onCreate(bundle);
        this.i = (AppOpsManager) getSystemService("appops");
        this.o = (AccessibilityManager) getSystemService("accessibility");
        this.j = new Binder();
        setContentView(R.layout.choose_applock_pattern);
        if (k.a() >= 10) {
            getActionBar().setExpandState(0);
        }
        this.h = C0259c.b(getApplicationContext());
        this.e = (TextView) findViewById(R.id.privacy_password_setting);
        this.n = getIntent().getStringExtra("passwordType");
        if (this.n == null) {
            this.n = "pattern";
        }
        this.g = getIntent().getStringExtra("external_app_name");
        String stringExtra = getIntent().getStringExtra("extra_data");
        getIntent().getBooleanExtra("forgot_password_reset", false);
        boolean booleanExtra = getIntent().getBooleanExtra("setting_password_reset", false);
        if (stringExtra == null || !stringExtra.equals("ModifyPassword")) {
            bVar = b.Confirm;
            i2 = R.string.lockpattern_tutorial_continue_label;
        } else {
            getActionBar().setTitle(R.string.modifypassword);
            this.f = true;
            bVar = b.Confirm;
            i2 = R.string.lockpattern_confirm_button_text;
        }
        bVar.a(i2);
        b.ConfirmDisabled.a(i2);
        if ("pattern".equals(this.n) && !booleanExtra) {
            this.r = (TextView) findViewById(R.id.footerText);
            this.r.setVisibility(0);
            this.r.setOnClickListener(new F(this));
        }
        this.p = (PasswordUnlockMediator) findViewById(R.id.passwordMediator);
        this.f3116b = (TextView) findViewById(R.id.headerText);
        this.q = (LinearLayoutWithDefaultTouchRecepient) findViewById(R.id.topLayout);
        c(this.n);
        o();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            h.k("set_back");
            finish();
        }
        return ChooseAccessControl.super.onOptionsItemSelected(menuItem);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        a(24, false);
        a(45, false);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        getWindow().addFlags(8192);
        a(24, true);
        a(45, true);
        b(this.n);
    }
}
