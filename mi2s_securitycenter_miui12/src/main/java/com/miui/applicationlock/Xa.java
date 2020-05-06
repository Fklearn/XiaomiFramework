package com.miui.applicationlock;

import android.os.AsyncTask;

class Xa extends AsyncTask<Void, Void, Integer> {

    /* renamed from: a  reason: collision with root package name */
    final int f3232a = 1;

    /* renamed from: b  reason: collision with root package name */
    final int f3233b = 2;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ bb f3234c;

    Xa(bb bbVar) {
        this.f3234c = bbVar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Integer doInBackground(Void... voidArr) {
        return Integer.valueOf(this.f3234c.B.a() ? 2 : 1);
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Integer num) {
        if (!this.f3234c.getActivity().isFinishing() && !this.f3234c.getActivity().isDestroyed()) {
            if (num.intValue() == 1) {
                this.f3234c.i();
            } else if (num.intValue() == 2) {
                this.f3234c.g();
                this.f3234c.B.a((Runnable) new Wa(this));
            }
        }
    }
}
