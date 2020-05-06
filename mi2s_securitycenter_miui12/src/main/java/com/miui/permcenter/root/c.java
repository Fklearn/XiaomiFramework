package com.miui.permcenter.root;

import android.os.AsyncTask;
import com.miui.permission.PermissionManager;

class c extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f6498a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ RootManagementActivity f6499b;

    c(RootManagementActivity rootManagementActivity, String str) {
        this.f6499b = rootManagementActivity;
        this.f6498a = str;
    }

    /* JADX WARNING: type inference failed for: r5v1, types: [android.content.Context, com.miui.permcenter.root.RootManagementActivity] */
    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        PermissionManager.getInstance(this.f6499b).setApplicationPermission(512, 1, this.f6498a);
        return null;
    }
}
