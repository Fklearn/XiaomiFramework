package com.miui.optimizecenter.storage.c;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.miui.optimizecenter.storage.d.a;
import com.miui.optimizecenter.storage.d.c;
import com.miui.optimizecenter.storage.d.d;
import com.miui.securitycenter.R;

public class b extends AsyncTask<Void, Void, Exception> {

    /* renamed from: a  reason: collision with root package name */
    private final Context f5726a;

    /* renamed from: b  reason: collision with root package name */
    private final c f5727b = c.a(this.f5726a);

    /* renamed from: c  reason: collision with root package name */
    private final String f5728c;

    /* renamed from: d  reason: collision with root package name */
    private final String f5729d;

    public b(Context context, d dVar) {
        this.f5726a = context.getApplicationContext();
        String str = null;
        if (dVar != null) {
            this.f5728c = dVar.c();
            a a2 = dVar.a();
            if (a2 != null) {
                str = a2.a();
            }
        } else {
            this.f5728c = null;
        }
        this.f5729d = str;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Exception doInBackground(Void... voidArr) {
        try {
            this.f5727b.d(this.f5728c);
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
            context = this.f5726a;
            i = R.string.storage_unmount_success;
            objArr = new Object[]{this.f5729d};
        } else {
            Log.e("UnmountTask", "Failed to unmount " + this.f5728c, exc);
            context = this.f5726a;
            i = R.string.storage_unmount_failure;
            objArr = new Object[]{this.f5729d};
        }
        Toast.makeText(context, context.getString(i, objArr), 0).show();
    }
}
