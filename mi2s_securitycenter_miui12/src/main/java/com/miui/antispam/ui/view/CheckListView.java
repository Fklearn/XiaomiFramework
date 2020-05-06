package com.miui.antispam.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;
import b.b.a.d.a.b;

public class CheckListView extends ExpandableListView implements ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener {

    /* renamed from: a  reason: collision with root package name */
    private b f2634a;

    public CheckListView(Context context) {
        super(context);
        setOnChildClickListener(this);
        setOnGroupClickListener(this);
    }

    public CheckListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnChildClickListener(this);
        setOnGroupClickListener(this);
    }

    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long j) {
        b bVar = this.f2634a;
        if (bVar == null) {
            return false;
        }
        bVar.a(i, i2);
        return false;
    }

    public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long j) {
        b bVar = this.f2634a;
        if (bVar == null) {
            return false;
        }
        bVar.a(i);
        return false;
    }

    public void setAdapter(b bVar) {
        this.f2634a = bVar;
        super.setAdapter(bVar);
    }
}
