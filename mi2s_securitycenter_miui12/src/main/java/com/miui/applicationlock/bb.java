package com.miui.applicationlock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import b.b.c.j.B;
import b.b.c.j.g;
import com.miui.applicationlock.SettingLockActivity;
import com.miui.applicationlock.a.h;
import com.miui.applicationlock.a.i;
import com.miui.applicationlock.c.C;
import com.miui.applicationlock.c.C0259c;
import com.miui.applicationlock.c.E;
import com.miui.applicationlock.c.K;
import com.miui.applicationlock.c.o;
import com.miui.applicationlock.c.q;
import com.miui.applicationlock.c.z;
import com.miui.applicationlock.widget.C0309b;
import com.miui.appmanager.AppManageUtils;
import com.miui.maml.elements.AdvancedSlider;
import com.miui.privacyapps.ui.PrivacyAppsOperationTutorialActivity;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import miui.app.AlertDialog;
import miui.security.SecurityManager;
import miuix.preference.DropDownPreference;
import miuix.preference.TextPreference;
import miuix.preference.s;

public class bb extends s implements Preference.b, Preference.c, SettingLockActivity.a {
    private b.b.k.b.a A;
    /* access modifiers changed from: private */
    public C B;
    /* access modifiers changed from: private */
    public AlertDialog C;
    /* access modifiers changed from: private */
    public int D;
    private boolean E;
    private AlertDialog F;
    /* access modifiers changed from: private */
    public AlertDialog G;
    /* access modifiers changed from: private */
    public TextView H;
    /* access modifiers changed from: private */
    public Context I;
    /* access modifiers changed from: private */
    public final z J = new b(this, (Sa) null);
    private DialogInterface.OnDismissListener K = new Ta(this);
    private DialogInterface.OnClickListener L = new Ua(this);
    private DialogInterface.OnClickListener M = new Va(this);
    /* access modifiers changed from: private */
    public AlertDialog N;

    /* renamed from: a  reason: collision with root package name */
    private CheckBoxPreference f3263a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public CheckBoxPreference f3264b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public CheckBoxPreference f3265c;

    /* renamed from: d  reason: collision with root package name */
    private Preference f3266d;
    /* access modifiers changed from: private */
    public AlertDialog e;
    /* access modifiers changed from: private */
    public TextView f;
    private View g;
    /* access modifiers changed from: private */
    public E h;
    /* access modifiers changed from: private */
    public int i = 0;
    public boolean j = true;
    /* access modifiers changed from: private */
    public CheckBoxPreference k;
    /* access modifiers changed from: private */
    public C0259c l;
    /* access modifiers changed from: private */
    public TextPreference m;
    private CheckBoxPreference n;
    /* access modifiers changed from: private */
    public CheckBoxPreference o;
    /* access modifiers changed from: private */
    public CheckBoxPreference p;
    private CheckBoxPreference q;
    private DropDownPreference r;
    /* access modifiers changed from: private */
    public TextPreference s;
    private PreferenceCategory t;
    private PreferenceCategory u;
    private PreferenceCategory v;
    private CheckBoxPreference w;
    private Preference x;
    private boolean y;
    /* access modifiers changed from: private */
    public SecurityManager z;

    private static class a implements q {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<bb> f3267a;

        private a(bb bbVar) {
            this.f3267a = new WeakReference<>(bbVar);
        }

        /* synthetic */ a(bb bbVar, Sa sa) {
            this(bbVar);
        }

        public void a() {
            bb bbVar = (bb) this.f3267a.get();
            if (bbVar != null) {
                if (bb.v(bbVar) < 5) {
                    bbVar.f.setText(bbVar.getResources().getString(R.string.fingerprint_verify_try_agin));
                    bbVar.e.show();
                    o.j(bbVar.I);
                    return;
                }
                int unused = bbVar.i = 0;
                bbVar.e.dismiss();
                Toast.makeText(bbVar.I, bbVar.getResources().getString(R.string.fingerprint_verify_failed), 1).show();
                Settings.Secure.putInt(bbVar.I.getContentResolver(), i.f3250a, 1);
                bbVar.h.a();
            }
        }

        public void a(int i) {
            bb bbVar = (bb) this.f3267a.get();
            if (bbVar != null) {
                o.a(i, bbVar.D);
                Settings.Secure.putInt(bbVar.I.getContentResolver(), i.f3250a, 2);
                Toast.makeText(bbVar.I, bbVar.getResources().getString(R.string.fingerprint_verify_succeed), 1).show();
                bbVar.e.setOnDismissListener((DialogInterface.OnDismissListener) null);
                bbVar.e.dismiss();
                int unused = bbVar.i = 0;
                bbVar.h.a();
            }
        }
    }

    private static class b implements z {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<bb> f3268a;

        private b(bb bbVar) {
            this.f3268a = new WeakReference<>(bbVar);
        }

        /* synthetic */ b(bb bbVar, Sa sa) {
            this(bbVar);
        }

        public void a() {
            Log.d("SettingLockActivity", " restartFaceUnlock ");
        }

        public void a(String str) {
            bb bbVar = (bb) this.f3268a.get();
            if (bbVar != null) {
                Log.d("SettingLockActivity", " onFaceHelp ");
                if (bbVar.H != null) {
                    bbVar.H.setText(str);
                }
            }
        }

        public void a(boolean z) {
            bb bbVar = (bb) this.f3268a.get();
            if (bbVar != null) {
                Log.d("SettingLockActivity", " onFaceAuthFailed ");
                if (bbVar.H != null) {
                    bbVar.H.setText(R.string.face_unlock_verity_dialog_title_failed);
                }
                if (bbVar.f3265c != null) {
                    bbVar.f3265c.setChecked(false);
                }
                bbVar.G.dismiss();
            }
        }

        public void b() {
            bb bbVar = (bb) this.f3268a.get();
            if (bbVar != null) {
                Log.d("SettingLockActivity", " onFaceAuthenticated ");
                if (bbVar.H != null) {
                    bbVar.H.setText(R.string.face_unlock_verity_dialog_title_succeed);
                }
                bbVar.G.dismiss();
                bbVar.l.c(true);
            }
        }

        public void c() {
            Log.d("SettingLockActivity", " onFaceLocked ");
        }

        public void d() {
            bb bbVar = (bb) this.f3268a.get();
            if (bbVar != null) {
                Log.d("SettingLockActivity", " onFaceStart ");
                if (bbVar.H != null) {
                    bbVar.H.setText(R.string.face_unlock_face_start_title);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public String a(int i2) {
        Resources resources = getResources();
        String[] stringArray = resources.getStringArray(R.array.lock_mode_value);
        HashMap hashMap = new HashMap();
        String[] stringArray2 = resources.getStringArray(R.array.lock_mode);
        for (int i3 = 0; i3 < stringArray.length; i3++) {
            hashMap.put(stringArray[i3], stringArray2[i3]);
        }
        return (String) hashMap.get(String.valueOf(i2));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0085  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(boolean r17) {
        /*
            r16 = this;
            r1 = r16
            java.lang.String r0 = "asInterface"
            java.lang.String r2 = "getService"
            java.lang.String r3 = "SettingLockActivity"
            android.content.Context r4 = r1.I
            java.lang.String r5 = "user"
            java.lang.Object r4 = r4.getSystemService(r5)
            android.os.UserManager r4 = (android.os.UserManager) r4
            java.util.List r4 = r4.getUserProfiles()
            java.lang.String r5 = "android.os.ServiceManager"
            r6 = 0
            r7 = 1
            r8 = 0
            java.lang.Class r5 = java.lang.Class.forName(r5)     // Catch:{ Exception -> 0x0074 }
            java.lang.Class[] r9 = new java.lang.Class[r7]     // Catch:{ Exception -> 0x0074 }
            java.lang.Class<java.lang.String> r10 = java.lang.String.class
            r9[r8] = r10     // Catch:{ Exception -> 0x0074 }
            java.lang.Object[] r10 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0074 }
            java.lang.String r11 = "notification"
            r10[r8] = r11     // Catch:{ Exception -> 0x0074 }
            java.lang.Object r5 = b.b.o.g.e.a((java.lang.Class<?>) r5, (java.lang.String) r2, (java.lang.Class<?>[]) r9, (java.lang.Object[]) r10)     // Catch:{ Exception -> 0x0074 }
            android.os.IBinder r5 = (android.os.IBinder) r5     // Catch:{ Exception -> 0x0074 }
            java.lang.String r9 = "android.app.INotificationManager$Stub"
            java.lang.Class r9 = java.lang.Class.forName(r9)     // Catch:{ Exception -> 0x0074 }
            java.lang.Class[] r10 = new java.lang.Class[r7]     // Catch:{ Exception -> 0x0074 }
            java.lang.Class<android.os.IBinder> r11 = android.os.IBinder.class
            r10[r8] = r11     // Catch:{ Exception -> 0x0074 }
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0074 }
            r11[r8] = r5     // Catch:{ Exception -> 0x0074 }
            java.lang.Object r5 = b.b.o.g.e.a((java.lang.Class<?>) r9, (java.lang.String) r0, (java.lang.Class<?>[]) r10, (java.lang.Object[]) r11)     // Catch:{ Exception -> 0x0074 }
            java.lang.String r9 = "android.os.ServiceManager"
            java.lang.Class r9 = java.lang.Class.forName(r9)     // Catch:{ Exception -> 0x0072 }
            java.lang.Class[] r10 = new java.lang.Class[r7]     // Catch:{ Exception -> 0x0072 }
            java.lang.Class<java.lang.String> r11 = java.lang.String.class
            r10[r8] = r11     // Catch:{ Exception -> 0x0072 }
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0072 }
            java.lang.String r12 = "package"
            r11[r8] = r12     // Catch:{ Exception -> 0x0072 }
            java.lang.Object r2 = b.b.o.g.e.a((java.lang.Class<?>) r9, (java.lang.String) r2, (java.lang.Class<?>[]) r10, (java.lang.Object[]) r11)     // Catch:{ Exception -> 0x0072 }
            android.os.IBinder r2 = (android.os.IBinder) r2     // Catch:{ Exception -> 0x0072 }
            java.lang.String r9 = "android.content.pm.IPackageManager$Stub"
            java.lang.Class r9 = java.lang.Class.forName(r9)     // Catch:{ Exception -> 0x0072 }
            java.lang.Class[] r10 = new java.lang.Class[r7]     // Catch:{ Exception -> 0x0072 }
            java.lang.Class<android.os.IBinder> r11 = android.os.IBinder.class
            r10[r8] = r11     // Catch:{ Exception -> 0x0072 }
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0072 }
            r11[r8] = r2     // Catch:{ Exception -> 0x0072 }
            java.lang.Object r6 = b.b.o.g.e.a((java.lang.Class<?>) r9, (java.lang.String) r0, (java.lang.Class<?>[]) r10, (java.lang.Object[]) r11)     // Catch:{ Exception -> 0x0072 }
            goto L_0x007b
        L_0x0072:
            r0 = move-exception
            goto L_0x0076
        L_0x0074:
            r0 = move-exception
            r5 = r6
        L_0x0076:
            java.lang.String r2 = "reflect error while getService"
            android.util.Log.e(r3, r2, r0)
        L_0x007b:
            java.util.Iterator r2 = r4.iterator()
        L_0x007f:
            boolean r0 = r2.hasNext()
            if (r0 == 0) goto L_0x00ee
            java.lang.Object r0 = r2.next()
            android.os.UserHandle r0 = (android.os.UserHandle) r0
            int r0 = r0.getIdentifier()
            miui.security.SecurityManager r4 = r1.z     // Catch:{ Exception -> 0x00e7 }
            java.lang.String r9 = "getAllPrivacyApps"
            java.lang.Class[] r10 = new java.lang.Class[r7]     // Catch:{ Exception -> 0x00e7 }
            java.lang.Class r11 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x00e7 }
            r10[r8] = r11     // Catch:{ Exception -> 0x00e7 }
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x00e7 }
            java.lang.Integer r12 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x00e7 }
            r11[r8] = r12     // Catch:{ Exception -> 0x00e7 }
            java.lang.Object r4 = b.b.o.g.e.a((java.lang.Object) r4, (java.lang.String) r9, (java.lang.Class<?>[]) r10, (java.lang.Object[]) r11)     // Catch:{ Exception -> 0x00e7 }
            java.util.List r4 = (java.util.List) r4     // Catch:{ Exception -> 0x00e7 }
            java.util.Iterator r4 = r4.iterator()     // Catch:{ Exception -> 0x00e7 }
        L_0x00ab:
            boolean r9 = r4.hasNext()     // Catch:{ Exception -> 0x00e7 }
            if (r9 == 0) goto L_0x007f
            java.lang.Object r9 = r4.next()     // Catch:{ Exception -> 0x00e7 }
            java.lang.String r9 = (java.lang.String) r9     // Catch:{ Exception -> 0x00e7 }
            if (r6 == 0) goto L_0x00ab
            if (r5 == 0) goto L_0x00ab
            android.content.pm.ApplicationInfo r10 = com.miui.appmanager.AppManageUtils.a((java.lang.Object) r6, (java.lang.String) r9, (int) r8, (int) r0)     // Catch:{ Exception -> 0x00e7 }
            java.lang.String r11 = "setNotificationsEnabledForPackage"
            r12 = 3
            java.lang.Class[] r13 = new java.lang.Class[r12]     // Catch:{ Exception -> 0x00e7 }
            java.lang.Class<java.lang.String> r14 = java.lang.String.class
            r13[r8] = r14     // Catch:{ Exception -> 0x00e7 }
            java.lang.Class r14 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x00e7 }
            r13[r7] = r14     // Catch:{ Exception -> 0x00e7 }
            java.lang.Class r14 = java.lang.Boolean.TYPE     // Catch:{ Exception -> 0x00e7 }
            r15 = 2
            r13[r15] = r14     // Catch:{ Exception -> 0x00e7 }
            java.lang.Object[] r12 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x00e7 }
            r12[r8] = r9     // Catch:{ Exception -> 0x00e7 }
            int r9 = r10.uid     // Catch:{ Exception -> 0x00e7 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x00e7 }
            r12[r7] = r9     // Catch:{ Exception -> 0x00e7 }
            java.lang.Boolean r9 = java.lang.Boolean.valueOf(r17)     // Catch:{ Exception -> 0x00e7 }
            r12[r15] = r9     // Catch:{ Exception -> 0x00e7 }
            b.b.o.g.e.a((java.lang.Object) r5, (java.lang.String) r11, (java.lang.Class<?>[]) r13, (java.lang.Object[]) r12)     // Catch:{ Exception -> 0x00e7 }
            goto L_0x00ab
        L_0x00e7:
            r0 = move-exception
            java.lang.String r4 = "reflect error while setNotificationEnable error"
            android.util.Log.e(r3, r4, r0)
            goto L_0x007f
        L_0x00ee:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.applicationlock.bb.a(boolean):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0006, code lost:
        if (r3 != 2) goto L_0x001e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void b(int r3) {
        /*
            r2 = this;
            r0 = 1
            if (r3 == 0) goto L_0x000d
            if (r3 == r0) goto L_0x0009
            r0 = 2
            if (r3 == r0) goto L_0x000d
            goto L_0x001e
        L_0x0009:
            miuix.preference.DropDownPreference r3 = r2.r
            r0 = 0
            goto L_0x000f
        L_0x000d:
            miuix.preference.DropDownPreference r3 = r2.r
        L_0x000f:
            java.lang.String r1 = r2.a((int) r0)
            r3.b((java.lang.String) r1)
            com.miui.applicationlock.c.c r3 = r2.l
            r3.a((int) r0)
            r2.c((int) r0)
        L_0x001e:
            r3 = 4
            com.miui.applicationlock.c.o.c((int) r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.applicationlock.bb.b(int):void");
    }

    /* access modifiers changed from: private */
    public void c(int i2) {
        if (i2 != 1) {
            this.v.d(this.n);
        } else {
            this.v.a((Preference) this.n);
        }
        this.n.setChecked(this.l.g());
    }

    private void f() {
        AlertDialog create = new AlertDialog.Builder(this.I).setTitle(getResources().getString(R.string.applock_close_dialog_title)).setMessage(getResources().getString(R.string.applock_close_dialog_summary)).setNegativeButton(getResources().getString(R.string.lockpattern_tutorial_cancel_label), new Oa(this)).setPositiveButton(getResources().getString(R.string.lockpattern_confirm_button_text), new Na(this)).create();
        create.setOnDismissListener(new Pa(this));
        create.show();
    }

    /* access modifiers changed from: private */
    public void g() {
        this.G = new AlertDialog.Builder(this.I).create();
        View inflate = getActivity().getLayoutInflater().inflate(R.layout.guide_face_unlock_dialog, (ViewGroup) null);
        this.H = (TextView) inflate.findViewById(R.id.confirm_face_unlock_view_msg);
        this.H.setText(R.string.face_unlock_verity_dialog_summary);
        this.G.setTitle(R.string.applock_face_unlock_title);
        this.G.setView(inflate);
        this.G.setButton(-2, getResources().getString(R.string.cancel), this.M);
        this.G.show();
        this.G.setOnDismissListener(new Za(this));
    }

    private void h() {
        this.e = new C0309b(this.I, R.style.Fod_Dialog_Fullscreen, this.h);
        Animation loadAnimation = AnimationUtils.loadAnimation(this.I, R.anim.fod_finger_appear);
        View inflate = getLayoutInflater().inflate(R.layout.applock_fod_fingerprint_window, (ViewGroup) null);
        this.f = (TextView) inflate.findViewById(R.id.confirm_fingerprint_view_msg);
        inflate.setAnimation(loadAnimation);
        this.e.show();
        this.e.setContentView(inflate);
        ((TextView) inflate.findViewById(R.id.cancel_finger_authenticate)).setOnClickListener(new Ra(this));
    }

    /* access modifiers changed from: private */
    public void i() {
        this.F = new AlertDialog.Builder(this.I).setTitle(R.string.applock_face_unlock_title).setNegativeButton(getResources().getString(R.string.cancal_to_setting_fingerprint), (DialogInterface.OnClickListener) null).setPositiveButton(getResources().getString(R.string.face_unlock_guide_confirm), new Ya(this)).setView(getActivity().getLayoutInflater().inflate(R.layout.guide_face_unlock_dialog, (ViewGroup) null)).create();
        this.F.show();
    }

    private void j() {
        this.B = C.a(this.I.getApplicationContext());
        new Xa(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void k() {
        new Qa(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private boolean l() {
        return AppManageUtils.a(this.I, B.j());
    }

    /* access modifiers changed from: private */
    public void m() {
        new ab(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void n() {
        int a2 = this.l.a();
        int i2 = 2;
        if (a2 == 0) {
            i2 = 1;
        } else if (a2 == 1) {
            i2 = 0;
        } else if (a2 != 2) {
            i2 = -1;
        }
        String[] stringArray = getResources().getStringArray(R.array.lock_mode);
        View inflate = getLayoutInflater().inflate(R.layout.confirm_lockmode_dialog, (ViewGroup) null);
        ListView listView = (ListView) inflate.findViewById(R.id.settings_lock_mode_listview);
        listView.setAdapter(new ArrayAdapter(this.I, R.layout.lock_mode_dialog_item, stringArray));
        listView.setChoiceMode(1);
        listView.setItemChecked(i2, true);
        listView.setOnItemClickListener(new Ja(this, new Ha(this)));
        this.N = new AlertDialog.Builder(this.I).setTitle(R.string.lock_mode_title).setView(inflate).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).show();
    }

    static /* synthetic */ int v(bb bbVar) {
        int i2 = bbVar.i + 1;
        bbVar.i = i2;
        return i2;
    }

    public void a() {
        if (this.y) {
            this.j = true;
            this.y = false;
        }
        this.f3263a.setChecked(o.i(this.I));
    }

    /* access modifiers changed from: protected */
    public void b() {
        if (!K.c(this.I)) {
            if (!o.a(this.z, "com.xiaomi.account")) {
                o.b(this.z, "com.xiaomi.account");
            }
            K.a(getActivity(), new Bundle(), this.l);
            this.y = true;
        } else if (this.l.b() != null) {
            this.l.a((String) null);
            this.p.setChecked(false);
        } else {
            d();
        }
    }

    public void c() {
        E e2 = this.h;
        if (e2 != null) {
            e2.a();
        }
    }

    /* access modifiers changed from: protected */
    public void d() {
        this.C = new AlertDialog.Builder(this.I).setTitle(getResources().getString(R.string.confirm_bind_xiaomi_account_dialog_title)).setMessage(getResources().getString(R.string.bind_xiaomi_account_dialog_summery, new Object[]{K.d(this.I)})).setNegativeButton(getResources().getString(R.string.bind_xiaomi_account_cancel), new La(this)).setPositiveButton(getResources().getString(R.string.bind_xiaomi_account_confirm), new Ka(this)).create();
        this.C.setOnDismissListener(new Ma(this));
        this.C.show();
    }

    public void e() {
        this.B.a((Runnable) new _a(this));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x005d, code lost:
        if (r6 == -1) goto L_0x005f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityResult(int r5, int r6, android.content.Intent r7) {
        /*
            r4 = this;
            super.onActivityResult(r5, r6, r7)
            r0 = 3
            r1 = -1
            r2 = 0
            r3 = 1
            if (r5 == r0) goto L_0x005d
            r0 = 30
            if (r5 == r0) goto L_0x0039
            r0 = 33
            if (r5 == r0) goto L_0x0024
            r6 = 34
            if (r5 == r6) goto L_0x0016
            goto L_0x006b
        L_0x0016:
            com.miui.applicationlock.c.C r5 = r4.B
            boolean r5 = r5.a()
            if (r5 == 0) goto L_0x005f
            com.miui.applicationlock.c.c r5 = r4.l
            r5.c((boolean) r3)
            goto L_0x005f
        L_0x0024:
            r4.j = r3
            if (r6 != r1) goto L_0x002e
            miui.security.SecurityManager r5 = r4.z
            com.miui.applicationlock.c.o.c((miui.security.SecurityManager) r5)
            goto L_0x006b
        L_0x002e:
            if (r7 == 0) goto L_0x006b
            java.lang.String r5 = "cancel_back_to_home"
            boolean r5 = r7.getBooleanExtra(r5, r2)
            if (r5 == 0) goto L_0x006b
            goto L_0x0062
        L_0x0039:
            if (r6 != r1) goto L_0x0050
            android.content.Context r5 = r4.I
            int r6 = r4.D
            boolean r5 = com.miui.applicationlock.c.o.d(r5, r6)
            if (r5 == 0) goto L_0x005a
            androidx.preference.CheckBoxPreference r5 = r4.f3264b
            r5.setChecked(r3)
            com.miui.applicationlock.c.c r5 = r4.l
            r5.d(r3)
            goto L_0x005a
        L_0x0050:
            androidx.preference.CheckBoxPreference r5 = r4.f3264b
            r5.setChecked(r2)
            com.miui.applicationlock.c.c r5 = r4.l
            r5.d(r2)
        L_0x005a:
            r4.E = r2
            goto L_0x005f
        L_0x005d:
            if (r6 != r1) goto L_0x0062
        L_0x005f:
            r4.j = r3
            goto L_0x006b
        L_0x0062:
            r4.j = r2
            android.app.Activity r5 = r4.getActivity()
            r5.finish()
        L_0x006b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.applicationlock.bb.onActivityResult(int, int, android.content.Intent):void");
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferencesFromResource(R.xml.applock_settings, str);
        this.I = getContext();
        String stringExtra = getActivity().getIntent().getStringExtra("extra_data");
        if ((bundle == null || !bundle.containsKey(AdvancedSlider.STATE)) && stringExtra.equals("ChooseAppToLock")) {
            this.j = true;
        } else {
            this.j = false;
        }
        this.D = g.a(this.I.getApplicationContext());
        this.z = (SecurityManager) this.I.getSystemService("security");
        this.l = C0259c.b(this.I.getApplicationContext());
        this.B = C.a(this.I.getApplicationContext());
        this.h = E.a(this.I);
        this.A = new b.b.k.b.a(this.I);
        int a2 = this.l.a();
        this.f3263a = (CheckBoxPreference) findPreference("show_pattern");
        if (!this.l.d()) {
            this.j = true;
        }
        this.f3263a.setChecked(o.i(this.I));
        this.f3263a.setOnPreferenceChangeListener(this);
        this.f3264b = (CheckBoxPreference) findPreference("fingerprint_lock");
        this.f3264b.setOnPreferenceChangeListener(this);
        this.f3265c = (CheckBoxPreference) findPreference("face_unlock");
        this.f3265c.setOnPreferenceChangeListener(this);
        this.f3266d = findPreference("modify_password");
        this.f3266d.setOnPreferenceClickListener(this);
        this.r = (DropDownPreference) findPreference("lock_mode");
        this.k = (CheckBoxPreference) findPreference("ac_enable");
        this.k.setChecked(this.l.e());
        this.k.setOnPreferenceChangeListener(this);
        this.m = (TextPreference) findPreference("notification_mask");
        this.m.setOnPreferenceClickListener(this);
        this.n = (CheckBoxPreference) findPreference("convenient_mode");
        this.n.setOnPreferenceChangeListener(this);
        this.o = (CheckBoxPreference) findPreference("lock_all_apps");
        this.o.setOnPreferenceChangeListener(this);
        this.p = (CheckBoxPreference) findPreference("bind_xiaomi_account");
        this.p.setOnPreferenceChangeListener(this);
        this.q = (CheckBoxPreference) findPreference("receive_recommendation");
        this.q.setOnPreferenceChangeListener(this);
        String a3 = a(a2);
        this.f3264b.setDependency("ac_enable");
        this.f3265c.setDependency("ac_enable");
        this.f3266d.setDependency("ac_enable");
        this.f3263a.setDependency("ac_enable");
        this.m.setDependency("ac_enable");
        this.n.setDependency("ac_enable");
        this.o.setDependency("ac_enable");
        this.p.setDependency("ac_enable");
        this.q.setDependency("ac_enable");
        this.t = (PreferenceCategory) findPreference("privacy_apps_category");
        this.u = (PreferenceCategory) findPreference("pwd_settings_category");
        this.v = (PreferenceCategory) findPreference("base_function_settings");
        this.w = (CheckBoxPreference) findPreference("privacy_apps_shield_message");
        this.x = findPreference("privacy_apps_tutorial");
        c(a2);
        this.o.setChecked(this.l.f());
        if (k.a() < 10) {
            this.v.d(this.r);
            this.s = (TextPreference) findPreference("preference_key_lock_mode_old");
            this.s.setVisible(true);
            this.s.setTitle((int) R.string.om_settings_memory_clean_lock_screen);
            this.s.setOnPreferenceClickListener(this);
        } else {
            this.r.setOnPreferenceChangeListener(this);
            if (a3 != null) {
                this.r.b(a3);
            }
            this.r.setDependency("ac_enable");
        }
        if (l()) {
            this.w.setOnPreferenceChangeListener(this);
            this.w.setChecked(this.A.e());
            this.x.setOnPreferenceClickListener(this);
            return;
        }
        getPreferenceScreen().d(this.t);
    }

    public void onDestroy() {
        this.h.a();
        super.onDestroy();
    }

    public void onPause() {
        super.onPause();
        this.h.a();
        AlertDialog alertDialog = this.e;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        AlertDialog alertDialog2 = this.C;
        if (alertDialog2 != null) {
            alertDialog2.dismiss();
        }
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        Context context;
        String str;
        String key = preference.getKey();
        if ("show_pattern".equals(key)) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            this.f3263a.setChecked(booleanValue);
            o.c(this.I, booleanValue);
        } else if ("fingerprint_lock".equals(key)) {
            if (!((Boolean) obj).booleanValue()) {
                Settings.Secure.putInt(this.I.getContentResolver(), i.f3250a, 1);
                o.a(this.I);
                o.c(false);
            } else if (!this.h.d()) {
                this.f3264b.setEnabled(false);
            } else if (!TransitionHelper.a(this.I) || !this.h.c()) {
                if (TransitionHelper.a(this.I) || !this.h.c()) {
                    context = this.I;
                    str = "com.android.settings.NewFingerprintInternalActivity";
                } else {
                    context = this.I;
                    str = "com.android.settings.MiuiSecurityChooseUnlock";
                }
                startActivityForResult(o.a(context, "com.android.settings", str), 30);
                this.E = true;
            } else {
                if (b.b.c.j.i.d()) {
                    h();
                } else {
                    this.e = new AlertDialog.Builder(this.I).create();
                    this.g = getActivity().getLayoutInflater().inflate(R.layout.confirm_fingerprint_dialog, (ViewGroup) null);
                    this.f = (TextView) this.g.findViewById(R.id.confirm_fingerprint_view_title);
                    this.f.setText(getResources().getString(R.string.fingerprint_identify_msg));
                    this.e.setView(this.g);
                    this.e.setButton(-2, getResources().getString(R.string.cancel), this.L);
                    this.e.show();
                }
                this.e.setOnDismissListener(this.K);
                this.h.a((q) new a(this, (Sa) null), 1);
            }
        } else if ("ac_enable".equals(key)) {
            if (!((Boolean) obj).booleanValue()) {
                f();
            }
        } else if ("convenient_mode".equals(key)) {
            this.l.b(((Boolean) obj).booleanValue());
        } else if ("lock_all_apps".equals(key)) {
            new Sa(this).execute(new Void[0]);
        } else if ("bind_xiaomi_account".equals(key)) {
            b();
        } else if ("privacy_apps_shield_message".equals(key)) {
            boolean booleanValue2 = ((Boolean) obj).booleanValue();
            this.A.d(booleanValue2);
            this.w.setChecked(booleanValue2);
            a(!booleanValue2);
        } else if ("receive_recommendation".equals(key)) {
            o.g(((Boolean) obj).booleanValue());
        } else if ("face_unlock".equals(key)) {
            if (((Boolean) obj).booleanValue()) {
                j();
            } else {
                this.l.c(false);
                o.b(false);
            }
        } else if ("lock_mode".equals(key)) {
            String str2 = (String) obj;
            String[] stringArray = this.I.getResources().getStringArray(R.array.lock_mode);
            if (stringArray[0].equals(str2)) {
                b(0);
            } else if (stringArray[1].equals(str2)) {
                b(1);
            } else {
                b(2);
            }
        }
        return true;
    }

    public boolean onPreferenceClick(Preference preference) {
        Intent intent;
        String key = preference.getKey();
        if ("modify_password".equals(key)) {
            intent = new Intent(this.I, ChooseLockTypeActivity.class);
            intent.putExtra("extra_data", "ModifyPassword");
            intent.putExtra("setting_password_reset", true);
        } else if ("notification_mask".equals(key)) {
            intent = new Intent(this.I, MaskNotificationActivity.class);
            intent.putExtra("extra_data", "applock_setting_mask_notification");
            intent.putExtra("enter_way", "mask_notification_security_center");
        } else {
            if ("privacy_apps_tutorial".equals(key)) {
                startActivityForResult(new Intent(this.I, PrivacyAppsOperationTutorialActivity.class), 3);
                h.m();
            } else if ("preference_key_lock_mode_old".equals(key)) {
                n();
            }
            return true;
        }
        startActivityForResult(intent, 33);
        return true;
    }

    public void onResume() {
        super.onResume();
        if (!this.l.d()) {
            getActivity().finish();
        }
        this.k.setChecked(this.l.e());
        k();
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(AdvancedSlider.STATE, this.j);
    }

    public void onStart() {
        super.onStart();
        boolean z2 = true;
        if (!this.l.d() || this.j || this.E) {
            this.j = true;
        } else {
            Intent intent = new Intent(this.I, ConfirmAccessControl.class);
            intent.putExtra("extra_data", "HappyCodingMain");
            startActivityForResult(intent, 3);
        }
        boolean c2 = K.c(this.I);
        String b2 = this.l.b();
        if (!c2 || !TextUtils.equals(K.a(this.I), b2)) {
            this.l.a((String) null);
        }
        CheckBoxPreference checkBoxPreference = this.p;
        if (!c2 || b2 == null) {
            z2 = false;
        }
        checkBoxPreference.setChecked(z2);
        this.q.setChecked(o.u());
        if ("pattern".equals(this.z.getAccessControlPasswordType())) {
            this.u.b((Preference) this.f3263a);
        } else {
            this.u.d(this.f3263a);
        }
    }

    public void onStop() {
        if (this.j) {
            this.j = false;
        }
        super.onStop();
    }

    public void onWindowFocusChanged(boolean z2) {
        boolean z3 = true;
        if (this.h.d()) {
            boolean z4 = this.h.c() && TransitionHelper.a(this.I) && this.l.i();
            this.f3264b.setChecked(z4);
            this.l.d(z4);
        } else {
            this.u.d(this.f3264b);
        }
        if (this.B.c()) {
            if (!this.B.a() || !this.l.h()) {
                z3 = false;
            }
            this.f3265c.setChecked(z3);
            this.l.c(z3);
            return;
        }
        this.u.d(this.f3265c);
    }
}
