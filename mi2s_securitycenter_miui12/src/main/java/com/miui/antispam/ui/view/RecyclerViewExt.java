package com.miui.antispam.ui.view;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import miui.R;
import miui.app.Activity;
import miuix.recyclerview.widget.RecyclerView;

public class RecyclerViewExt extends RecyclerView {
    private e Ta = new e();

    public static abstract class a<VH extends RecyclerView.u> extends RecyclerView.a<VH> implements Filterable, b.a {

        /* renamed from: a  reason: collision with root package name */
        protected Context f2635a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public boolean f2636b;

        /* renamed from: c  reason: collision with root package name */
        private Cursor f2637c;

        /* renamed from: d  reason: collision with root package name */
        private int f2638d;
        private a<VH>.a e;
        private DataSetObserver f;
        private b g;
        private FilterQueryProvider h;

        /* renamed from: com.miui.antispam.ui.view.RecyclerViewExt$a$a  reason: collision with other inner class name */
        private class C0038a extends ContentObserver {
            private C0038a() {
                super(new Handler());
            }

            public boolean deliverSelfNotifications() {
                return true;
            }

            public void onChange(boolean z) {
                a.this.b();
            }
        }

        private class b extends DataSetObserver {
            private b() {
            }

            public void onChanged() {
                boolean unused = a.this.f2636b = true;
                a.this.notifyDataSetChanged();
            }

            public void onInvalidated() {
                boolean unused = a.this.f2636b = false;
                a.this.notifyDataSetChanged();
            }
        }

        public a(Context context, Cursor cursor, int i) {
            a(context, cursor, i);
        }

        private void a(Context context, Cursor cursor, int i) {
            boolean z = cursor != null;
            this.f2637c = cursor;
            this.f2636b = z;
            this.f2635a = context;
            this.f2638d = z ? cursor.getColumnIndexOrThrow("_id") : -1;
            if ((i & 2) == 2) {
                this.e = new C0038a();
                this.f = new b();
            } else {
                this.e = null;
                this.f = null;
            }
            if (z) {
                a<VH>.a aVar = this.e;
                if (aVar != null) {
                    cursor.registerContentObserver(aVar);
                }
                DataSetObserver dataSetObserver = this.f;
                if (dataSetObserver != null) {
                    cursor.registerDataSetObserver(dataSetObserver);
                }
            }
            setHasStableIds(true);
        }

        public Cursor a(Cursor cursor) {
            boolean z;
            Cursor cursor2 = this.f2637c;
            if (cursor == cursor2) {
                return null;
            }
            if (cursor2 != null) {
                a<VH>.a aVar = this.e;
                if (aVar != null) {
                    cursor2.unregisterContentObserver(aVar);
                }
                DataSetObserver dataSetObserver = this.f;
                if (dataSetObserver != null) {
                    cursor2.unregisterDataSetObserver(dataSetObserver);
                }
            }
            this.f2637c = cursor;
            if (cursor != null) {
                a<VH>.a aVar2 = this.e;
                if (aVar2 != null) {
                    cursor.registerContentObserver(aVar2);
                }
                DataSetObserver dataSetObserver2 = this.f;
                if (dataSetObserver2 != null) {
                    cursor.registerDataSetObserver(dataSetObserver2);
                }
                this.f2638d = cursor.getColumnIndexOrThrow("_id");
                z = true;
            } else {
                this.f2638d = -1;
                z = false;
            }
            this.f2636b = z;
            notifyDataSetChanged();
            return cursor2;
        }

        public Object a(int i) {
            Cursor cursor;
            if (!this.f2636b || (cursor = this.f2637c) == null) {
                return null;
            }
            cursor.moveToPosition(i);
            return this.f2637c;
        }

        public abstract void a(VH vh, Cursor cursor, int i);

        /* access modifiers changed from: protected */
        public abstract void b();

        public void changeCursor(Cursor cursor) {
            Cursor a2 = a(cursor);
            if (a2 != null) {
                a2.close();
            }
        }

        public CharSequence convertToString(Cursor cursor) {
            return cursor == null ? "" : cursor.toString();
        }

        public Cursor getCursor() {
            return this.f2637c;
        }

        public Filter getFilter() {
            if (this.g == null) {
                this.g = new b(this);
            }
            return this.g;
        }

        public int getItemCount() {
            Cursor cursor;
            if (!this.f2636b || (cursor = this.f2637c) == null) {
                return 0;
            }
            return cursor.getCount();
        }

        public long getItemId(int i) {
            Cursor cursor;
            if (!this.f2636b || (cursor = this.f2637c) == null || !cursor.moveToPosition(i)) {
                return 0;
            }
            return this.f2637c.getLong(this.f2638d);
        }

        public void onBindViewHolder(@NonNull VH vh, int i) {
            if (!this.f2636b) {
                throw new IllegalStateException("this should only be called when the cursor is valid");
            } else if (this.f2637c.moveToPosition(i)) {
                a(vh, this.f2637c, i);
            } else {
                throw new IllegalStateException("couldn't move cursor to position " + i);
            }
        }

        public Cursor runQueryOnBackgroundThread(CharSequence charSequence) {
            FilterQueryProvider filterQueryProvider = this.h;
            return filterQueryProvider != null ? filterQueryProvider.runQuery(charSequence) : this.f2637c;
        }
    }

    private static class b extends Filter {

        /* renamed from: a  reason: collision with root package name */
        a f2641a;

        interface a {
            void changeCursor(Cursor cursor);

            CharSequence convertToString(Cursor cursor);

            Cursor getCursor();

            Cursor runQueryOnBackgroundThread(CharSequence charSequence);
        }

        b(a aVar) {
            this.f2641a = aVar;
        }

        public CharSequence convertResultToString(Object obj) {
            return this.f2641a.convertToString((Cursor) obj);
        }

        /* access modifiers changed from: protected */
        public Filter.FilterResults performFiltering(CharSequence charSequence) {
            Cursor runQueryOnBackgroundThread = this.f2641a.runQueryOnBackgroundThread(charSequence);
            Filter.FilterResults filterResults = new Filter.FilterResults();
            if (runQueryOnBackgroundThread != null) {
                filterResults.count = runQueryOnBackgroundThread.getCount();
            } else {
                filterResults.count = 0;
                runQueryOnBackgroundThread = null;
            }
            filterResults.values = runQueryOnBackgroundThread;
            return filterResults;
        }

        /* access modifiers changed from: protected */
        public void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
            Cursor cursor = this.f2641a.getCursor();
            Object obj = filterResults.values;
            if (obj != null && obj != cursor) {
                this.f2641a.changeCursor((Cursor) obj);
            }
        }
    }

    public static abstract class c<VH extends RecyclerView.u> extends RecyclerView.a<VH> {

        /* renamed from: a  reason: collision with root package name */
        private Activity f2642a;

        /* renamed from: b  reason: collision with root package name */
        private d f2643b;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public ActionMode f2644c;

        /* renamed from: d  reason: collision with root package name */
        private SparseBooleanArray f2645d = new SparseBooleanArray();
        public boolean e = false;

        private void b(boolean z) {
            this.e = z;
            notifyDataSetChanged();
        }

        public abstract Object a(int i);

        public void a(int i, boolean z, boolean z2) {
            if (z) {
                this.f2645d.put(i, true);
            } else {
                this.f2645d.delete(i);
            }
            this.f2643b.a(this.f2644c, i, z);
            if (z2) {
                notifyDataSetChanged();
            }
        }

        public void a(Context context, ActionMode actionMode) {
            String str;
            int f = f();
            if (f == 0) {
                str = context.getString(R.string.action_mode_title_empty);
                actionMode.getMenu().findItem(com.miui.securitycenter.R.id.edit_mode_delete).setEnabled(false);
            } else {
                str = context.getResources().getQuantityString(R.plurals.items_selected, f, new Object[]{Integer.valueOf(f)});
                actionMode.getMenu().findItem(com.miui.securitycenter.R.id.edit_mode_delete).setEnabled(true);
            }
            actionMode.setTitle(str);
        }

        public void a(Activity activity, d dVar) {
            this.f2642a = activity;
            this.f2643b = dVar;
        }

        public void a(boolean z) {
            if (z) {
                int itemCount = getItemCount();
                for (int i = 0; i < itemCount; i++) {
                    this.f2645d.put(i, z);
                }
            } else {
                this.f2645d.clear();
            }
            notifyDataSetChanged();
        }

        public void b() {
            this.e = true;
            b(true);
        }

        public boolean b(int i) {
            return this.f2645d.get(i);
        }

        public void c() {
            this.e = false;
            this.f2645d.clear();
            b(false);
        }

        public Activity d() {
            return this.f2642a;
        }

        public ActionMode.Callback e() {
            return this.f2643b;
        }

        public int f() {
            return this.f2645d.size();
        }

        public SparseBooleanArray g() {
            return this.f2645d.clone();
        }

        public boolean h() {
            return f() == getItemCount();
        }

        public void onBindViewHolder(@androidx.annotation.NonNull VH vh, int i) {
            vh.itemView.setLongClickable(true);
            vh.itemView.setOnLongClickListener(new b(this, i));
        }
    }

    public interface d extends ActionMode.Callback {
        void a(ActionMode actionMode, int i, boolean z);
    }

    public static class e implements ContextMenu.ContextMenuInfo {

        /* renamed from: a  reason: collision with root package name */
        public int f2646a = -1;

        public void a(int i) {
            this.f2646a = i;
        }
    }

    public RecyclerViewExt(@androidx.annotation.NonNull Context context) {
        super(context);
    }

    public RecyclerViewExt(@androidx.annotation.NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public RecyclerViewExt(@androidx.annotation.NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private void n(View view) {
        RecyclerView.g layoutManager = getLayoutManager();
        if (layoutManager != null) {
            this.Ta.a(layoutManager.l(view));
        }
    }

    /* access modifiers changed from: protected */
    public ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return this.Ta;
    }

    public boolean showContextMenuForChild(View view) {
        n(view);
        return super.showContextMenuForChild(view);
    }

    public boolean showContextMenuForChild(View view, float f, float f2) {
        n(view);
        return super.showContextMenuForChild(view, f, f2);
    }
}
