package com.miui.powercenter.autotask;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.A;
import b.b.c.j.e;
import com.miui.antispam.ui.view.RecyclerViewExt;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import miui.animation.Folme;
import miui.animation.base.AnimConfig;
import miui.app.Activity;
import miui.view.EditActionMode;
import miui.widget.SlidingButton;

public class AutoTaskManageActivity extends b.b.c.c.a implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewExt.d {

    /* renamed from: a  reason: collision with root package name */
    public static final boolean f6680a = (e.b() == 7);

    /* renamed from: b  reason: collision with root package name */
    private RecyclerViewExt f6681b;

    /* renamed from: c  reason: collision with root package name */
    private a f6682c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public FloatActionButtonView f6683d;
    /* access modifiers changed from: private */
    public HashSet<Long> e = new HashSet<>();
    private View.OnClickListener f = new C0490t(this);
    private BroadcastReceiver g = new C0491u(this);

    private class a extends RecyclerViewExt.c<b> {
        private Context f;
        protected List<AutoTask> g;

        private a(Context context) {
            this.g = new ArrayList();
            this.f = context;
        }

        /* synthetic */ a(AutoTaskManageActivity autoTaskManageActivity, Context context, C0490t tVar) {
            this(context);
        }

        private String a(Context context, AutoTask autoTask) {
            return ea.b(context, autoTask);
        }

        /* access modifiers changed from: private */
        public void setData(List<AutoTask> list) {
            this.g.clear();
            if (list != null) {
                this.g.addAll(list);
            }
            notifyDataSetChanged();
        }

        public Object a(int i) {
            return this.g.get(i);
        }

        /* JADX WARNING: Removed duplicated region for block: B:14:0x0061  */
        /* JADX WARNING: Removed duplicated region for block: B:15:0x0067  */
        /* JADX WARNING: Removed duplicated region for block: B:18:0x0085  */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x0087  */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x0092  */
        /* renamed from: a */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(@androidx.annotation.NonNull com.miui.powercenter.autotask.AutoTaskManageActivity.b r6, int r7) {
            /*
                r5 = this;
                super.onBindViewHolder(r6, r7)
                java.util.List<com.miui.powercenter.autotask.AutoTask> r0 = r5.g
                java.lang.Object r0 = r0.get(r7)
                com.miui.powercenter.autotask.AutoTask r0 = (com.miui.powercenter.autotask.AutoTask) r0
                android.widget.TextView r1 = r6.f6685b
                android.content.Context r2 = r5.f
                java.lang.String r2 = com.miui.powercenter.autotask.ea.a((android.content.Context) r2, (com.miui.powercenter.autotask.AutoTask) r0)
                r1.setText(r2)
                java.lang.String r1 = com.miui.powercenter.autotask.ea.a((com.miui.powercenter.autotask.AutoTask) r0)
                java.lang.String r2 = "battery_level_up"
                boolean r2 = r2.equals(r1)
                if (r2 != 0) goto L_0x0047
                java.lang.String r2 = "battery_level_down"
                boolean r2 = r2.equals(r1)
                if (r2 == 0) goto L_0x002b
                goto L_0x0047
            L_0x002b:
                java.lang.String r2 = "hour_minute"
                boolean r2 = r2.equals(r1)
                if (r2 == 0) goto L_0x0039
                android.widget.ImageView r1 = r6.f6684a
                r2 = 2131232051(0x7f080533, float:1.80802E38)
                goto L_0x004c
            L_0x0039:
                java.lang.String r2 = "hour_minute_duration"
                boolean r1 = r2.equals(r1)
                if (r1 == 0) goto L_0x004f
                android.widget.ImageView r1 = r6.f6684a
                r2 = 2131232507(0x7f0806fb, float:1.8081125E38)
                goto L_0x004c
            L_0x0047:
                android.widget.ImageView r1 = r6.f6684a
                r2 = 2131231008(0x7f080120, float:1.8078085E38)
            L_0x004c:
                r1.setImageResource(r2)
            L_0x004f:
                android.widget.TextView r1 = r6.f6685b
                android.content.Context r1 = r1.getContext()
                java.lang.String r1 = r5.a((android.content.Context) r1, (com.miui.powercenter.autotask.AutoTask) r0)
                boolean r2 = android.text.TextUtils.isEmpty(r1)
                r3 = 8
                if (r2 != 0) goto L_0x0067
                android.widget.TextView r2 = r6.f6686c
                r2.setText(r1)
                goto L_0x006c
            L_0x0067:
                android.widget.TextView r1 = r6.f6686c
                r1.setVisibility(r3)
            L_0x006c:
                android.widget.CheckBox r1 = r6.e
                boolean r2 = r5.b((int) r7)
                r1.setChecked(r2)
                miui.widget.SlidingButton r1 = r6.f6687d
                boolean r2 = r0.getEnabled()
                r1.setChecked(r2)
                android.widget.CheckBox r1 = r6.e
                boolean r2 = r5.e
                r4 = 0
                if (r2 == 0) goto L_0x0087
                r2 = r4
                goto L_0x0088
            L_0x0087:
                r2 = r3
            L_0x0088:
                r1.setVisibility(r2)
                miui.widget.SlidingButton r1 = r6.f6687d
                boolean r2 = r5.e
                if (r2 == 0) goto L_0x0092
                goto L_0x0093
            L_0x0092:
                r3 = r4
            L_0x0093:
                r1.setVisibility(r3)
                miui.widget.SlidingButton r1 = r6.f6687d
                com.miui.powercenter.autotask.w r2 = new com.miui.powercenter.autotask.w
                r2.<init>(r5, r0)
                r1.setOnCheckedChangeListener(r2)
                android.view.View r0 = r6.itemView
                com.miui.powercenter.autotask.x r1 = new com.miui.powercenter.autotask.x
                r1.<init>(r5, r7, r6)
                r0.setOnClickListener(r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.autotask.AutoTaskManageActivity.a.onBindViewHolder(com.miui.powercenter.autotask.AutoTaskManageActivity$b, int):void");
        }

        public int getItemCount() {
            return this.g.size();
        }

        public long getItemId(int i) {
            return this.g.get(i).getId();
        }

        @NonNull
        public b onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new b(LayoutInflater.from(this.f).inflate(R.layout.pc_auto_task_list_item, viewGroup, false), (C0490t) null);
        }
    }

    private static class b extends RecyclerView.u {

        /* renamed from: a  reason: collision with root package name */
        ImageView f6684a;

        /* renamed from: b  reason: collision with root package name */
        TextView f6685b;

        /* renamed from: c  reason: collision with root package name */
        TextView f6686c;

        /* renamed from: d  reason: collision with root package name */
        SlidingButton f6687d;
        CheckBox e;

        private b(@NonNull View view) {
            super(view);
            this.f6684a = (ImageView) view.findViewById(R.id.icon);
            this.f6685b = (TextView) view.findViewById(R.id.title);
            this.f6686c = (TextView) view.findViewById(R.id.summary);
            this.f6687d = view.findViewById(R.id.slide);
            this.e = (CheckBox) view.findViewById(16908289);
        }

        /* synthetic */ b(View view, C0490t tVar) {
            this(view);
        }
    }

    private List<AutoTask> a(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if (cursor.moveToFirst()) {
            do {
                arrayList.add(new AutoTask(cursor));
            } while (cursor.moveToNext());
        }
        return arrayList;
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [com.miui.powercenter.autotask.AutoTaskManageActivity, android.content.Context, miui.app.Activity] */
    /* access modifiers changed from: private */
    public void a(int i) {
        Intent intent = new Intent(this, AutoTaskEditActivity.class);
        if (i >= 0) {
            AutoTask autoTask = (AutoTask) this.f6682c.a(i);
            Bundle bundle = new Bundle();
            bundle.putParcelable("task", autoTask);
            intent.putExtra("bundle", bundle);
            if (autoTask.getId() == 1) {
                com.miui.powercenter.a.b.j();
            } else if (autoTask.getId() == 2) {
                com.miui.powercenter.a.b.k();
            } else {
                com.miui.powercenter.a.b.l();
            }
        } else {
            com.miui.powercenter.a.b.m();
        }
        startActivityForResult(intent, 1);
    }

    private void a(boolean z) {
        setTranslucentStatus(z ? 2 : 1);
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        this.f6682c.setData(a(cursor));
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [com.miui.powercenter.autotask.AutoTaskManageActivity, b.b.c.c.a, android.content.Context] */
    public void a(ActionMode actionMode, int i, boolean z) {
        int i2;
        EditActionMode editActionMode;
        long id = ((AutoTask) this.f6682c.a(i)).getId();
        if (z) {
            this.e.add(Long.valueOf(id));
        } else {
            this.e.remove(Long.valueOf(id));
        }
        this.f6682c.a((Context) this, actionMode);
        if (!f6680a) {
            if (this.f6682c.f() == this.f6682c.getItemCount()) {
                editActionMode = (EditActionMode) actionMode;
                i2 = isDarkModeEnable() ? miui.R.drawable.action_mode_title_button_deselect_all_dark : miui.R.drawable.action_mode_title_button_deselect_all_light;
            } else {
                editActionMode = (EditActionMode) actionMode;
                i2 = isDarkModeEnable() ? miui.R.drawable.action_mode_title_button_select_all_dark : miui.R.drawable.action_mode_title_button_select_all_light;
            }
            editActionMode.setButton(16908314, (CharSequence) null, i2);
        }
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [com.miui.powercenter.autotask.AutoTaskManageActivity, b.b.c.c.a, android.content.Context, miui.app.Activity] */
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 16908313:
                actionMode.finish();
                break;
            case 16908314:
                a aVar = this.f6682c;
                aVar.a(!aVar.h());
                this.f6682c.a((Context) this, actionMode);
                if (this.f6682c.h()) {
                    int itemCount = this.f6682c.getItemCount();
                    for (int i = 0; i < itemCount; i++) {
                        this.e.add(Long.valueOf(this.f6682c.getItemId(i)));
                    }
                } else {
                    this.e.clear();
                }
                if (!f6680a) {
                    ((EditActionMode) actionMode).setButton(16908314, (CharSequence) null, !this.f6682c.h() ? isDarkModeEnable() ? miui.R.drawable.action_mode_title_button_select_all_dark : miui.R.drawable.action_mode_title_button_select_all_light : isDarkModeEnable() ? miui.R.drawable.action_mode_title_button_deselect_all_dark : miui.R.drawable.action_mode_title_button_deselect_all_light);
                    break;
                }
                break;
            case R.id.edit_mode_delete /*2131296738*/:
                if (this.e.size() > 0) {
                    C0473b.a(this, getResources().getString(R.string.auto_task_manage_delete_prompt), getResources().getString(R.string.delete), new C0492v(this, actionMode));
                    return true;
                }
                break;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        AutoTaskManageActivity.super.onActivityResult(i, i2, intent);
        if (i == 1 && i2 == -1) {
            getLoaderManager().restartLoader(300, (Bundle) null, this);
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [com.miui.powercenter.autotask.AutoTaskManageActivity, b.b.c.c.a, android.content.Context, com.miui.antispam.ui.view.RecyclerViewExt$d, android.app.LoaderManager$LoaderCallbacks, miui.app.Activity, android.view.View$OnCreateContextMenuListener] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.pc_activity_autotask_manage);
        this.f6681b = (RecyclerViewExt) findViewById(R.id.list);
        this.f6682c = new a(this, this, (C0490t) null);
        this.f6682c.a((Activity) this, (RecyclerViewExt.d) this);
        this.f6681b.setAdapter(this.f6682c);
        this.f6681b.setLayoutManager(new LinearLayoutManager(this));
        this.f6681b.setOnCreateContextMenuListener(this);
        this.f6681b.setVerticalScrollBarEnabled(true);
        this.f6683d = (FloatActionButtonView) findViewById(R.id.action_btn);
        this.f6683d.setOnClickListener(this.f);
        if (A.a()) {
            try {
                Folme.useAt(new View[]{this.f6683d}).touch().handleTouchOf(this.f6683d, new AnimConfig[0]);
            } catch (Throwable unused) {
            }
        }
        getLoaderManager().initLoader(300, (Bundle) null, this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.miui.powercenter.action.TASK_DELETE");
        LocalBroadcastManager.getInstance(this).registerReceiver(this.g, intentFilter);
        com.miui.powercenter.a.a.c();
    }

    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        getMenuInflater().inflate(R.menu.delete_item, menu);
        this.f6682c.b();
        this.f6683d.setVisibility(8);
        this.e.clear();
        a(false);
        if (f6680a) {
            return true;
        }
        EditActionMode editActionMode = (EditActionMode) actionMode;
        editActionMode.setButton(16908313, (CharSequence) null, isDarkModeEnable() ? miui.R.drawable.action_mode_title_button_cancel_dark : miui.R.drawable.action_mode_title_button_cancel_light);
        editActionMode.setButton(16908314, (CharSequence) null, isDarkModeEnable() ? miui.R.drawable.action_mode_title_button_select_all_dark : miui.R.drawable.action_mode_title_button_select_all_light);
        return true;
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.content.Loader<android.database.Cursor> onCreateLoader(int r8, android.os.Bundle r9) {
        /*
            r7 = this;
            android.content.CursorLoader r8 = new android.content.CursorLoader
            android.net.Uri r2 = com.miui.powercenter.autotask.AutoTask.CONTENT_URI
            java.lang.String[] r3 = com.miui.powercenter.autotask.AutoTask.QUERY_COLUMNS
            r4 = 0
            r5 = 0
            r6 = 0
            r0 = r8
            r1 = r7
            r0.<init>(r1, r2, r3, r4, r5, r6)
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.autotask.AutoTaskManageActivity.onCreateLoader(int, android.os.Bundle):android.content.Loader");
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [com.miui.powercenter.autotask.AutoTaskManageActivity, android.content.Context, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onDestroy() {
        AutoTaskManageActivity.super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.g);
    }

    public void onDestroyActionMode(ActionMode actionMode) {
        this.f6682c.c();
        this.f6683d.setVisibility(0);
        a(false);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        this.f6682c.setData((List<AutoTask>) null);
    }

    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }
}
