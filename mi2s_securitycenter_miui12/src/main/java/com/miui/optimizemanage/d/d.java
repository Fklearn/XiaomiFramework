package com.miui.optimizemanage.d;

import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import b.b.o.g.e;
import com.miui.optimizemanage.settings.c;

class d extends AsyncTask<Void, Void, Integer> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f5931a;

    d(Context context) {
        this.f5931a = context;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Integer doInBackground(Void... voidArr) {
        int i = 3;
        try {
            i = ((Integer) e.a(Class.forName("android.provider.MiuiSettings$SettingsCloudData"), Integer.TYPE, "getCloudDataInt", (Class<?>[]) new Class[]{ContentResolver.class, String.class, String.class, Integer.TYPE}, this.f5931a.getContentResolver(), "app_compatibility", "omAnimationTime", 3)).intValue();
        } catch (Exception e) {
            Log.e("Utils", "getCloudDataInt error", e);
        }
        return Integer.valueOf(i);
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Integer num) {
        super.onPostExecute(num);
        c.a(num.intValue());
        c.a(System.currentTimeMillis());
    }
}
