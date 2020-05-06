package a.e.a;

import a.e.a.b;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import androidx.annotation.RestrictTo;

public abstract class a extends BaseAdapter implements Filterable, b.a {
    @RestrictTo({RestrictTo.a.f223b})

    /* renamed from: a  reason: collision with root package name */
    protected boolean f137a;
    @RestrictTo({RestrictTo.a.f223b})

    /* renamed from: b  reason: collision with root package name */
    protected boolean f138b;
    @RestrictTo({RestrictTo.a.f223b})

    /* renamed from: c  reason: collision with root package name */
    protected Cursor f139c;
    @RestrictTo({RestrictTo.a.f223b})

    /* renamed from: d  reason: collision with root package name */
    protected Context f140d;
    @RestrictTo({RestrictTo.a.f223b})
    protected int e;
    @RestrictTo({RestrictTo.a.f223b})
    protected C0005a f;
    @RestrictTo({RestrictTo.a.f223b})
    protected DataSetObserver g;
    @RestrictTo({RestrictTo.a.f223b})
    protected b h;

    /* renamed from: a.e.a.a$a  reason: collision with other inner class name */
    private class C0005a extends ContentObserver {
        C0005a() {
            super(new Handler());
        }

        public boolean deliverSelfNotifications() {
            return true;
        }

        public void onChange(boolean z) {
            a.this.a();
        }
    }

    private class b extends DataSetObserver {
        b() {
        }

        public void onChanged() {
            a aVar = a.this;
            aVar.f137a = true;
            aVar.notifyDataSetChanged();
        }

        public void onInvalidated() {
            a aVar = a.this;
            aVar.f137a = false;
            aVar.notifyDataSetInvalidated();
        }
    }

    public a(Context context, Cursor cursor, boolean z) {
        a(context, cursor, z ? 1 : 2);
    }

    public Cursor a(Cursor cursor) {
        Cursor cursor2 = this.f139c;
        if (cursor == cursor2) {
            return null;
        }
        if (cursor2 != null) {
            C0005a aVar = this.f;
            if (aVar != null) {
                cursor2.unregisterContentObserver(aVar);
            }
            DataSetObserver dataSetObserver = this.g;
            if (dataSetObserver != null) {
                cursor2.unregisterDataSetObserver(dataSetObserver);
            }
        }
        this.f139c = cursor;
        if (cursor != null) {
            C0005a aVar2 = this.f;
            if (aVar2 != null) {
                cursor.registerContentObserver(aVar2);
            }
            DataSetObserver dataSetObserver2 = this.g;
            if (dataSetObserver2 != null) {
                cursor.registerDataSetObserver(dataSetObserver2);
            }
            this.e = cursor.getColumnIndexOrThrow("_id");
            this.f137a = true;
            notifyDataSetChanged();
        } else {
            this.e = -1;
            this.f137a = false;
            notifyDataSetInvalidated();
        }
        return cursor2;
    }

    public abstract View a(Context context, Cursor cursor, ViewGroup viewGroup);

    /* access modifiers changed from: protected */
    public void a() {
        Cursor cursor;
        if (this.f138b && (cursor = this.f139c) != null && !cursor.isClosed()) {
            this.f137a = this.f139c.requery();
        }
    }

    /* access modifiers changed from: package-private */
    public void a(Context context, Cursor cursor, int i) {
        b bVar;
        boolean z = false;
        if ((i & 1) == 1) {
            i |= 2;
            this.f138b = true;
        } else {
            this.f138b = false;
        }
        if (cursor != null) {
            z = true;
        }
        this.f139c = cursor;
        this.f137a = z;
        this.f140d = context;
        this.e = z ? cursor.getColumnIndexOrThrow("_id") : -1;
        if ((i & 2) == 2) {
            this.f = new C0005a();
            bVar = new b();
        } else {
            bVar = null;
            this.f = null;
        }
        this.g = bVar;
        if (z) {
            C0005a aVar = this.f;
            if (aVar != null) {
                cursor.registerContentObserver(aVar);
            }
            DataSetObserver dataSetObserver = this.g;
            if (dataSetObserver != null) {
                cursor.registerDataSetObserver(dataSetObserver);
            }
        }
    }

    public abstract void a(View view, Context context, Cursor cursor);

    public abstract View b(Context context, Cursor cursor, ViewGroup viewGroup);

    public void changeCursor(Cursor cursor) {
        Cursor a2 = a(cursor);
        if (a2 != null) {
            a2.close();
        }
    }

    public abstract CharSequence convertToString(Cursor cursor);

    public int getCount() {
        Cursor cursor;
        if (!this.f137a || (cursor = this.f139c) == null) {
            return 0;
        }
        return cursor.getCount();
    }

    public Cursor getCursor() {
        return this.f139c;
    }

    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        if (!this.f137a) {
            return null;
        }
        this.f139c.moveToPosition(i);
        if (view == null) {
            view = a(this.f140d, this.f139c, viewGroup);
        }
        a(view, this.f140d, this.f139c);
        return view;
    }

    public Filter getFilter() {
        if (this.h == null) {
            this.h = new b(this);
        }
        return this.h;
    }

    public Object getItem(int i) {
        Cursor cursor;
        if (!this.f137a || (cursor = this.f139c) == null) {
            return null;
        }
        cursor.moveToPosition(i);
        return this.f139c;
    }

    public long getItemId(int i) {
        Cursor cursor;
        if (!this.f137a || (cursor = this.f139c) == null || !cursor.moveToPosition(i)) {
            return 0;
        }
        return this.f139c.getLong(this.e);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (!this.f137a) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        } else if (this.f139c.moveToPosition(i)) {
            if (view == null) {
                view = b(this.f140d, this.f139c, viewGroup);
            }
            a(view, this.f140d, this.f139c);
            return view;
        } else {
            throw new IllegalStateException("couldn't move cursor to position " + i);
        }
    }
}
