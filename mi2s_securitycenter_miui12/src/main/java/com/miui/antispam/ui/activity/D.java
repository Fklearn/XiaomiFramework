package com.miui.antispam.ui.activity;

import android.content.DialogInterface;
import android.net.Uri;
import miui.provider.ExtraTelephony;

class D implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ KeywordListActivity f2527a;

    D(KeywordListActivity keywordListActivity) {
        this.f2527a = keywordListActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f2527a.q.remove(this.f2527a.l);
        this.f2527a.getContentResolver().delete(Uri.withAppendedPath(ExtraTelephony.Keyword.CONTENT_URI, String.valueOf(this.f2527a.k)), (String) null, (String[]) null);
    }
}
