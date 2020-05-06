package com.miui.securityscan.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.h.e;
import b.b.c.h.j;
import b.b.c.j.f;
import b.b.c.j.z;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.h;
import com.miui.securityscan.M;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import miui.os.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class ModelUpdater {
    /* access modifiers changed from: private */
    public static final String TAG = "ModelUpdater";
    /* access modifiers changed from: private */
    public static ExecutorService mThreadPool = Executors.newFixedThreadPool(1);
    private static ModelUpdater sModelUpdater;
    private AlarmManager mAlarmManager;
    /* access modifiers changed from: private */
    public Context mContext = Application.d();
    /* access modifiers changed from: private */
    public volatile boolean mIsDownloading = false;
    private BroadcastReceiver mScanItemUpdateReceiver = new a(this);

    private class a extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private Context f7761a;

        public a(Context context) {
            this.f7761a = context;
        }

        private int a() {
            String scanItemJSONStr = ModelFactory.getScanItemJSONStr(ModelUpdater.this.mContext);
            if (TextUtils.isEmpty(scanItemJSONStr)) {
                return -1;
            }
            try {
                return new JSONObject(scanItemJSONStr).optInt("version", -1);
            } catch (JSONException e) {
                e.printStackTrace();
                return -1;
            }
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            if (z.a(M.b()) < 3 || !h.i() || !f.b(ModelUpdater.this.mContext)) {
                return null;
            }
            try {
                File a2 = e.a(this.f7761a, a(), new j("securityscan_modelupdate"));
                if (a2 == null || !ModelUpdater.checkFileMd5(a2.getName(), a2)) {
                    return null;
                }
                boolean h = M.h();
                boolean z = false;
                File file = new File(this.f7761a.getFilesDir(), "scanitem.json_v" + (h ? 0 : 1));
                if (file.exists()) {
                    file.delete();
                }
                FileUtils.copyFile(a2, file);
                if (!h) {
                    z = true;
                }
                M.e(z);
                M.c(System.currentTimeMillis());
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e2) {
                e2.printStackTrace();
                return null;
            }
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Void voidR) {
            super.onPostExecute(voidR);
            boolean unused = ModelUpdater.this.mIsDownloading = false;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            super.onPreExecute();
            boolean unused = ModelUpdater.this.mIsDownloading = true;
        }
    }

    private ModelUpdater() {
    }

    /* access modifiers changed from: private */
    public static boolean checkFileMd5(String str, File file) {
        boolean z;
        if (TextUtils.isEmpty(str) || file == null || !file.exists()) {
            return false;
        }
        try {
            String a2 = b.b.c.e.a.a(file);
            if (a2 == null) {
                return false;
            }
            z = a2.equalsIgnoreCase(str);
            try {
                String str2 = TAG;
                Object[] objArr = new Object[3];
                objArr[0] = str;
                int i = 1;
                objArr[1] = a2;
                if (!z) {
                    i = 0;
                }
                objArr[2] = Integer.valueOf(i);
                Log.i(str2, String.format("check file md5: given: %s with caculated: %s, success:%d", objArr));
                return z;
            } catch (RuntimeException e) {
                e = e;
                e.printStackTrace();
                return z;
            }
        } catch (RuntimeException e2) {
            e = e2;
            z = false;
            e.printStackTrace();
            return z;
        }
    }

    public static synchronized ModelUpdater getInstance() {
        ModelUpdater modelUpdater;
        synchronized (ModelUpdater.class) {
            if (sModelUpdater == null) {
                sModelUpdater = new ModelUpdater();
            }
            modelUpdater = sModelUpdater;
        }
        return modelUpdater;
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        this.mContext.unregisterReceiver(this.mScanItemUpdateReceiver);
        super.finalize();
    }

    public void init() {
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        this.mContext.registerReceiver(this.mScanItemUpdateReceiver, new IntentFilter("com.miui.securitycenter.action.ITEM_UPDATE"));
        Calendar instance = Calendar.getInstance();
        instance.set(11, (int) (Math.random() * 23.0d));
        instance.set(12, (int) (Math.random() * 60.0d));
        this.mAlarmManager.setRepeating(1, instance.getTimeInMillis(), 86400000, PendingIntent.getBroadcast(this.mContext, 10004, new Intent("com.miui.securitycenter.action.ITEM_UPDATE"), 0));
    }
}
