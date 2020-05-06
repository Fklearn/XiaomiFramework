package com.miui.cleanmaster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import java.lang.ref.WeakReference;

public class g {

    private static class a implements i {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<Activity> f3747a;

        /* renamed from: b  reason: collision with root package name */
        private Intent f3748b;

        /* renamed from: c  reason: collision with root package name */
        private int f3749c;

        /* renamed from: d  reason: collision with root package name */
        private Bundle f3750d;
        private boolean e;

        public a(Activity activity, Intent intent, int i, Bundle bundle) {
            this.f3747a = new WeakReference<>(activity);
            this.f3748b = intent;
            this.f3749c = i;
            this.f3750d = bundle;
            this.e = true;
        }

        public a(Context context, Intent intent) {
            this.f3748b = intent;
            if (context instanceof Activity) {
                this.f3747a = new WeakReference<>((Activity) context);
            }
            this.e = false;
        }

        public void a(boolean z, int i) {
            Activity activity;
            WeakReference<Activity> weakReference = this.f3747a;
            if (weakReference == null || (activity = (Activity) weakReference.get()) == null || activity.isFinishing() || activity.isDestroyed()) {
                return;
            }
            if (this.e) {
                g.c(activity, this.f3748b, this.f3749c, this.f3750d);
            } else {
                g.c(activity, this.f3748b);
            }
        }
    }

    private static Object a(i iVar) {
        return a() ? new InstallCallbackV28(iVar) : new InstallCallBack(iVar);
    }

    public static boolean a() {
        return Build.VERSION.SDK_INT >= 28;
    }

    public static void b(Activity activity, Intent intent, int i, Bundle bundle) {
        if (activity != null && intent != null) {
            if (f.a(activity)) {
                c(activity, intent, i, bundle);
            } else {
                f.a(activity, a(new a(activity, intent, i, bundle)));
            }
        }
    }

    public static void b(Context context, Intent intent) {
        if (context != null && intent != null) {
            if (f.a(context)) {
                c(context, intent);
            } else {
                f.a(context, a(new a(context, intent)));
            }
        }
    }

    /* access modifiers changed from: private */
    public static void c(Activity activity, Intent intent, int i, Bundle bundle) {
        try {
            activity.startActivityForResult(intent, i, bundle);
        } catch (Exception e) {
            Log.e("CleanMasterLunchUtil", "lunchIntentForResult: " + e.toString());
        }
    }

    /* access modifiers changed from: private */
    public static void c(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("CleanMasterLunchUtil", "lunchIntent: " + e.toString());
        }
    }
}
