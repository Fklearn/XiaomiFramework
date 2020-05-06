package com.miui.antispam.ui.activity;

import android.content.ContentProviderOperation;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.SparseBooleanArray;
import com.miui.antispam.ui.activity.KeywordListActivity;
import miui.provider.ExtraTelephony;

class K extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SparseBooleanArray f2543a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ long[] f2544b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ KeywordListActivity.c f2545c;

    K(KeywordListActivity.c cVar, SparseBooleanArray sparseBooleanArray, long[] jArr) {
        this.f2545c = cVar;
        this.f2543a = sparseBooleanArray;
        this.f2544b = jArr;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        int i = 0;
        for (int i2 = 0; i2 < this.f2543a.size(); i2++) {
            if (this.f2543a.valueAt(i2)) {
                KeywordListActivity.this.q.remove(((KeywordListActivity.b.a) KeywordListActivity.this.f.a(this.f2543a.keyAt(i2))).f2549b);
            }
        }
        while (true) {
            long[] jArr = this.f2544b;
            if (i >= jArr.length) {
                break;
            }
            KeywordListActivity.this.i.add(ContentProviderOperation.newDelete(Uri.withAppendedPath(ExtraTelephony.Keyword.CONTENT_URI, String.valueOf(jArr[i]))).build());
            if (KeywordListActivity.this.i.size() > 100) {
                KeywordListActivity.this.i.execute();
            }
            i++;
        }
        if (KeywordListActivity.this.i.size() <= 0) {
            return null;
        }
        KeywordListActivity.this.i.execute();
        return null;
    }
}
