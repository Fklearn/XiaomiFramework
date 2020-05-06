package com.miui.antispam.ui.activity;

import android.content.DialogInterface;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import com.miui.antispam.ui.activity.KeywordListActivity;

class J implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ActionMode f2539a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ long[] f2540b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ SparseBooleanArray f2541c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ KeywordListActivity.c f2542d;

    J(KeywordListActivity.c cVar, ActionMode actionMode, long[] jArr, SparseBooleanArray sparseBooleanArray) {
        this.f2542d = cVar;
        this.f2539a = actionMode;
        this.f2540b = jArr;
        this.f2541c = sparseBooleanArray;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f2539a.finish();
        this.f2542d.a(this.f2540b, this.f2541c);
    }
}
