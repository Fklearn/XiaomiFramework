package com.miui.optimizecenter.storage.c;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.miui.optimizecenter.storage.d.c;
import com.miui.optimizecenter.storage.d.d;
import com.miui.securitycenter.R;

public class a extends AsyncTask<Void, Void, Exception> {

    /* renamed from: a  reason: collision with root package name */
    private final Context f5722a;

    /* renamed from: b  reason: collision with root package name */
    private final c f5723b = c.a(this.f5722a);

    /* renamed from: c  reason: collision with root package name */
    private final String f5724c;

    /* renamed from: d  reason: collision with root package name */
    private final String f5725d;

    public a(Context context, d dVar) {
        this.f5722a = context.getApplicationContext();
        this.f5724c = dVar.c();
        this.f5725d = dVar.a().a();
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Exception doInBackground(Void... voidArr) {
        try {
            this.f5723b.c(this.f5724c);
            return null;
        } catch (Exception e) {
            return e;
        }
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Exception exc) {
        Context context;
        int i;
        Object[] objArr;
        if (exc == null) {
            context = this.f5722a;
            i = R.string.storage_mount_success;
            objArr = new Object[]{this.f5725d};
        } else {
            Log.e("MountTask", "Failed to mount " + this.f5724c, exc);
            context = this.f5722a;
            i = R.string.storage_mount_failure;
            objArr = new Object[]{this.f5725d};
        }
        Toast.makeText(context, context.getString(i, objArr), 0).show();
    }
}
