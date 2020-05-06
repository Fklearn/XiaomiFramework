package com.miui.applicationlock;

import android.os.AsyncTask;
import com.miui.common.persistence.b;

/* renamed from: com.miui.applicationlock.t  reason: case insensitive filesystem */
class C0300t extends AsyncTask<Void, Void, Integer> {

    /* renamed from: a  reason: collision with root package name */
    final int f3379a = 0;

    /* renamed from: b  reason: collision with root package name */
    final int f3380b = 1;

    /* renamed from: c  reason: collision with root package name */
    final int f3381c = 2;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ C0312y f3382d;

    C0300t(C0312y yVar) {
        this.f3382d = yVar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Integer doInBackground(Void... voidArr) {
        int unused = this.f3382d.y = 0;
        if (!this.f3382d.x.a()) {
            if (b.a("cancel_face_unlock_guide_times", 0) < 2) {
                int unused2 = this.f3382d.y = 1;
            }
        } else if (!this.f3382d.t.h() && b.a("cancel_face_unlock_verify_times", 0) < 2) {
            int unused3 = this.f3382d.y = 2;
        }
        if (this.f3382d.y == 0) {
            this.f3382d.k();
        }
        return Integer.valueOf(this.f3382d.y);
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Integer num) {
        if (this.f3382d.z != null && !this.f3382d.z.isFinishing() && !this.f3382d.z.isDestroyed()) {
            if (num.intValue() == 1) {
                this.f3382d.i();
            } else if (num.intValue() == 2) {
                this.f3382d.f();
                this.f3382d.x.a((Runnable) new C0298s(this));
            }
        }
    }
}
