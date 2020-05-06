package com.miui.antispam.ui.activity;

import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import miui.provider.ExtraTelephony;

/* renamed from: com.miui.antispam.ui.activity.v  reason: case insensitive filesystem */
class C0227v extends AsyncTask<Void, Integer, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f2616a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ long f2617b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ z f2618c;

    C0227v(z zVar, int i, long j) {
        this.f2618c = zVar;
        this.f2616a = i;
        this.f2617b = j;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        int i = this.f2616a;
        if (i == 3 || i == 2) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("sync_dirty", 1);
            this.f2618c.getContentResolver().update(Uri.withAppendedPath(ExtraTelephony.Phonelist.CONTENT_URI, String.valueOf(this.f2617b)), contentValues, (String) null, (String[]) null);
        } else {
            this.f2618c.getContentResolver().delete(Uri.withAppendedPath(ExtraTelephony.Phonelist.CONTENT_URI, String.valueOf(this.f2617b)), (String) null, (String[]) null);
        }
        return null;
    }
}
