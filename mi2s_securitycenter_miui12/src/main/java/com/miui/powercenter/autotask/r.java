package com.miui.powercenter.autotask;

import android.app.FragmentManager;
import android.content.Context;
import miui.os.AsyncTaskWithProgress;

class r extends AsyncTaskWithProgress<Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Long[] f6764a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Context f6765b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    r(FragmentManager fragmentManager, Long[] lArr, Context context) {
        super(fragmentManager);
        this.f6764a = lArr;
        this.f6765b = context;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        int i = 0;
        while (true) {
            Long[] lArr = this.f6764a;
            if (i >= lArr.length) {
                return null;
            }
            C0489s.d(this.f6765b, lArr[i].longValue());
            publishProgress(new Integer[]{Integer.valueOf((i * 100) / this.f6764a.length)});
            i++;
        }
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Void voidR) {
        r.super.onPostExecute(voidR);
        C0489s.b(this.f6765b, this.f6764a);
    }
}
