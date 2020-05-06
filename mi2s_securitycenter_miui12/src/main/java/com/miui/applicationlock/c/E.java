package com.miui.applicationlock.c;

import android.content.Context;
import android.hardware.biometrics.BiometricManager;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.util.Log;
import b.b.o.g.e;
import java.util.ArrayList;
import java.util.List;

public class E {

    /* renamed from: a  reason: collision with root package name */
    private static E f3277a;

    /* renamed from: b  reason: collision with root package name */
    private FingerprintManager f3278b = null;

    /* renamed from: c  reason: collision with root package name */
    private BiometricManager f3279c = null;

    /* renamed from: d  reason: collision with root package name */
    private CancellationSignal f3280d = null;
    /* access modifiers changed from: private */
    public q e;

    private E(Context context) {
        this.f3278b = (FingerprintManager) context.getApplicationContext().getSystemService("fingerprint");
        this.f3279c = (BiometricManager) context.getApplicationContext().getSystemService("biometric");
    }

    /* access modifiers changed from: private */
    public int a(Fingerprint fingerprint) {
        return Build.VERSION.SDK_INT > 28 ? fingerprint.getBiometricId() : fingerprint.getFingerId();
    }

    public static synchronized E a(Context context) {
        E e2;
        synchronized (E.class) {
            if (f3277a == null) {
                f3277a = new E(context);
            }
            e2 = f3277a;
        }
        return e2;
    }

    public void a() {
        CancellationSignal cancellationSignal = this.f3280d;
        if (cancellationSignal != null && !cancellationSignal.isCanceled()) {
            this.f3280d.cancel();
            this.f3280d = null;
        }
    }

    public void a(q qVar, int i) {
        if (this.f3278b != null) {
            this.f3280d = new CancellationSignal();
            this.e = qVar;
            this.f3278b.authenticate((FingerprintManager.CryptoObject) null, this.f3280d, i, new D(this), (Handler) null);
        }
    }

    public void a(byte[] bArr) {
        Object obj;
        Class cls;
        String str;
        Class[] clsArr;
        Object[] objArr;
        if (Build.VERSION.SDK_INT > 28) {
            if (this.f3279c == null) {
                return;
            }
        } else if (this.f3278b == null) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT > 28) {
                obj = this.f3279c;
                cls = Void.TYPE;
                str = "resetLockout";
                clsArr = new Class[]{byte[].class};
                objArr = new Object[]{bArr};
            } else {
                obj = this.f3278b;
                cls = Void.TYPE;
                str = "resetTimeout";
                clsArr = new Class[]{byte[].class};
                objArr = new Object[]{bArr};
            }
            e.a(obj, cls, str, (Class<?>[]) clsArr, objArr);
        } catch (Exception e2) {
            Log.e("FingerprintHelperImpl", "resetTimeout exception: ", e2);
        }
    }

    public List<Integer> b() {
        FingerprintManager fingerprintManager = this.f3278b;
        ArrayList arrayList = null;
        if (fingerprintManager == null) {
            return null;
        }
        try {
            List<Fingerprint> list = (List) e.a((Object) fingerprintManager, List.class, "getEnrolledFingerprints", (Class<?>[]) null, new Object[0]);
            if (list == null || list.isEmpty()) {
                return null;
            }
            ArrayList arrayList2 = new ArrayList();
            try {
                for (Fingerprint fingerprint : list) {
                    if (fingerprint != null) {
                        arrayList2.add(Integer.valueOf(a(fingerprint)));
                    }
                }
                return arrayList2;
            } catch (Exception e2) {
                e = e2;
                arrayList = arrayList2;
                Log.e("FingerprintHelperImpl", "getEnrolledFingerprints exception: ", e);
                return arrayList;
            }
        } catch (Exception e3) {
            e = e3;
            Log.e("FingerprintHelperImpl", "getEnrolledFingerprints exception: ", e);
            return arrayList;
        }
    }

    public boolean c() {
        FingerprintManager fingerprintManager = this.f3278b;
        if (fingerprintManager == null) {
            return false;
        }
        try {
            return fingerprintManager.hasEnrolledFingerprints();
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public boolean d() {
        FingerprintManager fingerprintManager = this.f3278b;
        if (fingerprintManager == null) {
            return false;
        }
        return fingerprintManager.isHardwareDetected();
    }
}
