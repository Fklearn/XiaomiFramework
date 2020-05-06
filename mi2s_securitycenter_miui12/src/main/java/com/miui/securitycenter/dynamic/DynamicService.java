package com.miui.securitycenter.dynamic;

import android.content.Context;

public class DynamicService {
    private Context mContext;
    private boolean mDestroyed;

    /* access modifiers changed from: package-private */
    public final void attach(Context context) {
        this.mContext = context;
    }

    public final Context getContext() {
        return this.mContext;
    }

    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    /* access modifiers changed from: protected */
    public void onCreate() {
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
    }

    /* access modifiers changed from: protected */
    public void performCreate() {
        onCreate();
    }

    /* access modifiers changed from: protected */
    public void performDestroy() {
        this.mDestroyed = true;
        onDestroy();
    }
}
