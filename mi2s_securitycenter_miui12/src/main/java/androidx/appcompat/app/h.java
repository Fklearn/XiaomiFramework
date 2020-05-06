package androidx.appcompat.app;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import androidx.appcompat.app.AlertController;

class h extends CursorAdapter {

    /* renamed from: a  reason: collision with root package name */
    private final int f305a;

    /* renamed from: b  reason: collision with root package name */
    private final int f306b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ AlertController.RecycleListView f307c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ AlertController f308d;
    final /* synthetic */ AlertController.a e;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    h(AlertController.a aVar, Context context, Cursor cursor, boolean z, AlertController.RecycleListView recycleListView, AlertController alertController) {
        super(context, cursor, z);
        this.e = aVar;
        this.f307c = recycleListView;
        this.f308d = alertController;
        Cursor cursor2 = getCursor();
        this.f305a = cursor2.getColumnIndexOrThrow(this.e.L);
        this.f306b = cursor2.getColumnIndexOrThrow(this.e.M);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        ((CheckedTextView) view.findViewById(16908308)).setText(cursor.getString(this.f305a));
        AlertController.RecycleListView recycleListView = this.f307c;
        int position = cursor.getPosition();
        boolean z = true;
        if (cursor.getInt(this.f306b) != 1) {
            z = false;
        }
        recycleListView.setItemChecked(position, z);
    }

    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return this.e.f238b.inflate(this.f308d.M, viewGroup, false);
    }
}
