package com.miui.antispam.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import b.b.a.d.a.e;
import b.b.a.e.i;
import b.b.a.e.n;
import b.b.a.e.o;
import com.miui.antispam.ui.view.RecyclerViewExt;
import com.miui.maml.elements.AdvancedSlider;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import miui.app.Activity;
import miui.cloud.common.XSimChangeNotification;
import miui.os.Build;
import miui.view.EditActionMode;

public abstract class z extends r implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewExt.d, i.a {

    /* renamed from: d  reason: collision with root package name */
    private static final String[] f2627d = {"data1"};
    protected RecyclerViewExt e;
    protected e f;
    protected View g;
    protected TextView h;
    protected ImageView i;
    protected CheckBox j;
    protected CheckBox k;
    protected View l;
    protected Dialog m;
    protected AlertDialog n;
    protected a o;
    protected int p;
    protected boolean q;
    private Comparator<o.a> r = new C0225t(this);

    protected class a implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        public List<String> f2628a;

        protected a() {
        }

        public void a(List<String> list) {
            this.f2628a = list;
        }

        /* JADX WARNING: type inference failed for: r3v0, types: [com.miui.antispam.ui.activity.z, android.content.Context] */
        public void onClick(DialogInterface dialogInterface, int i) {
            int i2;
            int i3;
            if (z.this.j.isChecked() && z.this.k.isChecked()) {
                i2 = 0;
            } else if (z.this.j.isChecked()) {
                i2 = 1;
            } else if (z.this.k.isChecked()) {
                i2 = 2;
            } else {
                return;
            }
            if (!z.this.f2612c) {
                i3 = i2;
            } else if (i2 != 1) {
                i3 = 2;
            } else {
                return;
            }
            z zVar = z.this;
            n.a(z.this, (String[]) this.f2628a.toArray(new String[0]), i3, (Integer[]) null, zVar.p, zVar.q ^ true ? 1 : 0);
        }
    }

    /* JADX WARNING: type inference failed for: r5v2, types: [android.content.Context] */
    /* JADX WARNING: type inference failed for: r5v3, types: [android.content.Context] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void g() {
        /*
            r11 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            b.b.a.d.a.e r3 = r11.f
            android.util.SparseBooleanArray r3 = r3.g()
            r4 = 0
            r5 = r4
        L_0x0017:
            int r6 = r3.size()
            if (r5 >= r6) goto L_0x0055
            boolean r6 = r3.valueAt(r5)
            if (r6 == 0) goto L_0x0052
            b.b.a.d.a.e r6 = r11.f
            int r7 = r3.keyAt(r5)
            java.lang.Object r6 = r6.a(r7)
            b.b.a.d.a.e$a r6 = (b.b.a.d.a.e.a) r6
            java.lang.String r7 = r6.f1339c
            java.lang.String r8 = "***"
            int r7 = r7.indexOf(r8)
            if (r7 != 0) goto L_0x004d
            java.lang.String r7 = r6.e
            r1.add(r7)
            java.lang.String r6 = r6.f1339c
            r7 = 3
            java.lang.String r6 = r6.substring(r7)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r2.add(r6)
            goto L_0x0052
        L_0x004d:
            java.lang.String r6 = r6.f1339c
            r0.add(r6)
        L_0x0052:
            int r5 = r5 + 1
            goto L_0x0017
        L_0x0055:
            boolean r3 = r11.f2612c
            if (r3 == 0) goto L_0x005c
            r3 = 2
            r7 = r3
            goto L_0x005d
        L_0x005c:
            r7 = r4
        L_0x005d:
            boolean r3 = r0.isEmpty()
            if (r3 != 0) goto L_0x0074
            java.lang.String[] r3 = new java.lang.String[r4]
            java.lang.Object[] r0 = r0.toArray(r3)
            r6 = r0
            java.lang.String[] r6 = (java.lang.String[]) r6
            r8 = 0
            int r9 = r11.p
            r10 = 1
            r5 = r11
            b.b.a.e.n.a(r5, r6, r7, r8, r9, r10)
        L_0x0074:
            boolean r0 = r1.isEmpty()
            if (r0 != 0) goto L_0x0094
            java.lang.String[] r0 = new java.lang.String[r4]
            java.lang.Object[] r0 = r1.toArray(r0)
            r6 = r0
            java.lang.String[] r6 = (java.lang.String[]) r6
            r7 = 0
            java.lang.Integer[] r0 = new java.lang.Integer[r4]
            java.lang.Object[] r0 = r2.toArray(r0)
            r8 = r0
            java.lang.Integer[] r8 = (java.lang.Integer[]) r8
            int r9 = r11.p
            r10 = 1
            r5 = r11
            b.b.a.e.n.a(r5, r6, r7, r8, r9, r10)
        L_0x0094:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.ui.activity.z.g():void");
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [com.miui.antispam.ui.activity.z, android.content.Context] */
    /* access modifiers changed from: protected */
    public Dialog a(List<String> list) {
        if (this.m == null) {
            this.o = new a();
            this.m = new AlertDialog.Builder(this).setTitle(this.q ? R.string.dlg_black_antispam_hint : R.string.dlg_white_antispam_hint).setMessage(this.q ? R.string.dlg_black_antispam_message : R.string.dlg_white_antispam_message).setView(this.l).setPositiveButton(17039370, this.o).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
        }
        this.o.a(list);
        return this.m;
    }

    public List<Object> a(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if (cursor.moveToFirst()) {
            do {
                long j2 = cursor.getLong(cursor.getColumnIndex("_id"));
                int i2 = cursor.getInt(cursor.getColumnIndex("sync_dirty"));
                String string = cursor.getString(cursor.getColumnIndex("number"));
                if (!TextUtils.isEmpty(string) && string.length() > 64) {
                    string = string.substring(0, 64);
                }
                arrayList.add(new e.a(j2, i2, string, cursor.getInt(cursor.getColumnIndex(AdvancedSlider.STATE)), cursor.getString(cursor.getColumnIndex("notes")), cursor.getInt(cursor.getColumnIndex(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID))));
            } while (cursor.moveToNext());
        }
        return arrayList;
    }

    public void a() {
        e eVar = this.f;
        if (eVar != null) {
            eVar.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: protected */
    public void a(int i2, long j2, String str) {
        new C0227v(this, i2, j2).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null});
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                this.g.setVisibility(8);
                this.e.setVisibility(0);
            } else {
                this.g.setVisibility(0);
                this.e.setVisibility(8);
                this.i.setImageResource(R.drawable.no_blacklist);
                this.h.setText(this.q ? R.string.bl_no_blacklist : R.string.wl_no_whitelist);
            }
            this.f.setData(a((Cursor) new o(cursor, "number", this.r)));
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [com.miui.antispam.ui.activity.z, android.content.Context, com.miui.antispam.ui.activity.r] */
    public void a(ActionMode actionMode, int i2, boolean z) {
        int i3;
        EditActionMode editActionMode;
        this.f.a((Context) this, actionMode);
        if (!r.f2610a) {
            if (this.f.f() == this.f.getItemCount()) {
                editActionMode = (EditActionMode) actionMode;
                i3 = b() ? miui.R.drawable.action_mode_title_button_deselect_all_dark : miui.R.drawable.action_mode_title_button_deselect_all_light;
            } else {
                editActionMode = (EditActionMode) actionMode;
                i3 = b() ? miui.R.drawable.action_mode_title_button_select_all_dark : miui.R.drawable.action_mode_title_button_select_all_light;
            }
            editActionMode.setButton(16908314, (CharSequence) null, i3);
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [com.miui.antispam.ui.activity.z, android.content.Context] */
    /* access modifiers changed from: protected */
    public void a(ActionMode actionMode, boolean z) {
        AlertDialog alertDialog = this.n;
        if (alertDialog == null || !alertDialog.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            boolean z2 = this.q;
            int i2 = R.string.dlg_move_to_white;
            AlertDialog.Builder message = builder.setTitle(z2 ? z ? R.string.dlg_move_to_white : R.string.dlg_remove_blacklist_title : R.string.dlg_remove_whitelist_title).setMessage(this.q ? z ? R.string.dlg_add_white : R.string.dlg_remove_blacklist : R.string.dlg_remove_whitelist);
            if (!z) {
                i2 = R.string.dlg_remove_ok;
            }
            message.setPositiveButton(i2, new x(this, actionMode, z)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [com.miui.antispam.ui.activity.z, android.content.Context, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void a(e.a aVar) {
        Intent intent = new Intent(this, AddPhoneListActivity.class);
        intent.putExtra("is_black", this.q);
        intent.putExtra("id_edit_blacklist", aVar.f1337a);
        intent.putExtra("number_edit_blacklist", aVar.f1339c);
        intent.putExtra("state_edit_blacklist", aVar.f1340d);
        intent.putExtra("sync_edit_blacklist", aVar.f1338b);
        intent.putExtra("note_edit_blacklist", aVar.e);
        intent.putExtra(AddAntiSpamActivity.g, aVar.f);
        startActivity(intent);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [com.miui.antispam.ui.activity.z, android.content.Context, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void c() {
        int i2;
        Resources resources;
        if (!Build.IS_INTERNATIONAL_BUILD) {
            resources = getResources();
            i2 = this.q ? R.array.st_antispam_choose_methods_black : R.array.st_antispam_choose_methods_white;
        } else {
            resources = getResources();
            i2 = this.q ? R.array.st_antispam_choose_methods_black_international : R.array.st_antispam_choose_methods_white_international;
        }
        new AlertDialog.Builder(this).setTitle(R.string.st_antispam_bw_choose_modes).setItems(resources.getTextArray(i2), new C0228w(this)).create().show();
    }

    /* access modifiers changed from: protected */
    public void d() {
        new y(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null});
    }

    public abstract e e();

    /* access modifiers changed from: protected */
    public void f() {
        int i2;
        Intent intent;
        if (this.f2612c) {
            intent = new Intent("android.intent.action.PICK");
            intent.setData(ContactsContract.Contacts.CONTENT_URI);
            intent.setPackage("com.google.android.contacts");
            intent.addCategory(Constants.System.CATEGORY_DEFALUT);
            i2 = 1007;
        } else {
            intent = new Intent("com.android.contacts.action.GET_MULTIPLE_PHONES");
            intent.addCategory(Constants.System.CATEGORY_DEFALUT);
            intent.setType("vnd.android.cursor.dir/phone_v2");
            intent.putExtra("android.intent.extra.include_unknown_numbers", true);
            intent.putExtra("android.intent.extra.initial_picker_tab", 1);
            intent.putExtra("com.android.contacts.extra.MAX_COUNT", 500);
            i2 = 1006;
        }
        startActivityForResult(intent, i2);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [com.miui.antispam.ui.activity.z, android.content.Context, com.miui.antispam.ui.activity.r] */
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        boolean z = false;
        switch (menuItem.getItemId()) {
            case 16908313:
                actionMode.finish();
                break;
            case 16908314:
                e eVar = this.f;
                if (!eVar.h()) {
                    z = true;
                }
                eVar.a(z);
                this.f.a((Context) this, actionMode);
                if (!r.f2610a) {
                    ((EditActionMode) actionMode).setButton(16908314, (CharSequence) null, !this.f.h() ? b() ? miui.R.drawable.action_mode_title_button_select_all_dark : miui.R.drawable.action_mode_title_button_select_all_light : b() ? miui.R.drawable.action_mode_title_button_deselect_all_dark : miui.R.drawable.action_mode_title_button_deselect_all_light);
                    break;
                }
                break;
            case R.id.edit_mode_delete /*2131296738*/:
                a(actionMode, false);
                break;
            case R.id.edit_mode_white /*2131296739*/:
                a(actionMode, true);
                break;
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0084, code lost:
        if (r1 != null) goto L_0x0086;
     */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0095  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00a5  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00aa  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0163  */
    /* JADX WARNING: Removed duplicated region for block: B:88:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityResult(int r11, int r12, android.content.Intent r13) {
        /*
            r10 = this;
            if (r13 != 0) goto L_0x0003
            return
        L_0x0003:
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            r0 = 1007(0x3ef, float:1.411E-42)
            r1 = 0
            if (r11 != r0) goto L_0x00ae
            android.net.Uri r3 = r13.getData()
            if (r3 != 0) goto L_0x0014
            return
        L_0x0014:
            android.content.ContentResolver r2 = r10.getContentResolver()     // Catch:{ Exception -> 0x008e, all -> 0x008b }
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            android.database.Cursor r11 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x008e, all -> 0x008b }
            if (r11 == 0) goto L_0x007f
            boolean r13 = r11.moveToFirst()     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            if (r13 == 0) goto L_0x007f
            java.lang.String r13 = "has_phone_number"
            int r13 = r11.getColumnIndex(r13)     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            java.lang.String r13 = r11.getString(r13)     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            java.lang.String r0 = "_id"
            int r0 = r11.getColumnIndex(r0)     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            java.lang.String r0 = r11.getString(r0)     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            java.lang.String r2 = "1"
            boolean r13 = r13.equals(r2)     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            if (r13 == 0) goto L_0x007f
            android.content.ContentResolver r2 = r10.getContentResolver()     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            android.net.Uri r3 = android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            r4 = 0
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            r13.<init>()     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            java.lang.String r5 = "contact_id = "
            r13.append(r5)     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            r13.append(r0)     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            java.lang.String r5 = r13.toString()     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            r6 = 0
            r7 = 0
            android.database.Cursor r1 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            if (r1 == 0) goto L_0x007f
            boolean r13 = r1.moveToFirst()     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            if (r13 == 0) goto L_0x007f
            java.lang.String r13 = "data1"
            int r13 = r1.getColumnIndex(r13)     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            java.lang.String r13 = r1.getString(r13)     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            r12.add(r13)     // Catch:{ Exception -> 0x007a, all -> 0x0078 }
            goto L_0x007f
        L_0x0078:
            r12 = move-exception
            goto L_0x00a3
        L_0x007a:
            r13 = move-exception
            r9 = r1
            r1 = r11
            r11 = r9
            goto L_0x0090
        L_0x007f:
            if (r11 == 0) goto L_0x0084
            r11.close()
        L_0x0084:
            if (r1 == 0) goto L_0x0152
        L_0x0086:
            r1.close()
            goto L_0x0152
        L_0x008b:
            r12 = move-exception
            r11 = r1
            goto L_0x00a3
        L_0x008e:
            r13 = move-exception
            r11 = r1
        L_0x0090:
            r13.printStackTrace()     // Catch:{ all -> 0x009f }
            if (r1 == 0) goto L_0x0098
            r1.close()
        L_0x0098:
            if (r11 == 0) goto L_0x0152
            r11.close()
            goto L_0x0152
        L_0x009f:
            r12 = move-exception
            r9 = r1
            r1 = r11
            r11 = r9
        L_0x00a3:
            if (r11 == 0) goto L_0x00a8
            r11.close()
        L_0x00a8:
            if (r1 == 0) goto L_0x00ad
            r1.close()
        L_0x00ad:
            throw r12
        L_0x00ae:
            r0 = 1006(0x3ee, float:1.41E-42)
            if (r11 != r0) goto L_0x016a
            java.lang.String r11 = "com.android.contacts.extra.PHONE_URIS"
            android.os.Parcelable[] r11 = r13.getParcelableArrayExtra(r11)
            if (r11 == 0) goto L_0x016a
            int r13 = r11.length
            if (r13 != 0) goto L_0x00bf
            goto L_0x016a
        L_0x00bf:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            int r0 = r11.length
            r2 = 0
            r3 = r2
        L_0x00c7:
            if (r3 >= r0) goto L_0x0106
            r4 = r11[r3]
            android.net.Uri r4 = (android.net.Uri) r4
            java.lang.String r5 = r4.getScheme()
            java.lang.String r6 = "content"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x00ec
            int r5 = r13.length()
            if (r5 <= 0) goto L_0x00e4
            r5 = 44
            r13.append(r5)
        L_0x00e4:
            java.lang.String r4 = r4.getLastPathSegment()
            r13.append(r4)
            goto L_0x0103
        L_0x00ec:
            java.lang.String r5 = r4.getScheme()
            java.lang.String r6 = "tel"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x0103
            java.lang.String r4 = r4.getSchemeSpecificPart()
            java.lang.String r4 = r4.trim()
            r12.add(r4)
        L_0x0103:
            int r3 = r3 + 1
            goto L_0x00c7
        L_0x0106:
            int r11 = r13.length()
            if (r11 <= 0) goto L_0x0134
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r0 = "_id IN ("
            r11.append(r0)
            java.lang.String r13 = r13.toString()
            r11.append(r13)
            java.lang.String r13 = ")"
            r11.append(r13)
            java.lang.String r6 = r11.toString()
            android.content.ContentResolver r3 = r10.getContentResolver()
            android.net.Uri r4 = android.provider.ContactsContract.Data.CONTENT_URI
            java.lang.String[] r5 = f2627d
            r7 = 0
            r8 = 0
            android.database.Cursor r1 = r3.query(r4, r5, r6, r7, r8)
        L_0x0134:
            if (r1 != 0) goto L_0x013d
            int r11 = r12.size()
            if (r11 != 0) goto L_0x013d
            return
        L_0x013d:
            if (r1 == 0) goto L_0x0152
        L_0x013f:
            boolean r11 = r1.moveToNext()     // Catch:{ all -> 0x014d }
            if (r11 == 0) goto L_0x0086
            java.lang.String r11 = r1.getString(r2)     // Catch:{ all -> 0x014d }
            r12.add(r11)     // Catch:{ all -> 0x014d }
            goto L_0x013f
        L_0x014d:
            r11 = move-exception
            r1.close()
            throw r11
        L_0x0152:
            android.widget.CheckBox r11 = r10.j
            r13 = 1
            r11.setChecked(r13)
            android.widget.CheckBox r11 = r10.k
            r11.setChecked(r13)
            int r11 = r12.size()
            if (r11 <= 0) goto L_0x016a
            android.app.Dialog r11 = r10.a((java.util.List<java.lang.String>) r12)
            r11.show()
        L_0x016a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.ui.activity.z.onActivityResult(int, int, android.content.Intent):void");
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [com.miui.antispam.ui.activity.z, android.content.Context, miui.app.Activity] */
    public boolean onContextItemSelected(MenuItem menuItem) {
        e.a aVar = (e.a) this.f.a(((RecyclerViewExt.e) menuItem.getMenuInfo()).f2646a);
        boolean z = aVar.f1339c.indexOf("***") == 0;
        switch (menuItem.getItemId()) {
            case 3:
                n.b(this, aVar.f1339c, 1);
                break;
            case 4:
                Bundle call = getContentResolver().call(Uri.parse("content://antispam"), "getBlockKeyword", aVar.f1339c, (Bundle) null);
                n.a((Context) this, aVar.f1339c, call != null ? call.getString("blockKeyword") : "");
                break;
            case 5:
                n.a((Context) this, aVar.f1339c, this.p - 1);
                break;
            case 6:
                n.h(this, aVar.f1339c);
                break;
            case 7:
                n.g(this, aVar.f1339c);
                break;
            case 8:
                new AlertDialog.Builder(this).setTitle(this.q ? R.string.dlg_remove_blacklist_title : R.string.dlg_remove_whitelist_title).setMessage(z ? this.q ? R.string.dlg_remove_address_blacklist : R.string.dlg_remove_address_whitelist : this.q ? R.string.dlg_remove_blacklist : R.string.dlg_remove_whitelist).setPositiveButton(R.string.dlg_remove_ok, new C0226u(this, aVar)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
                break;
            case 10:
                a(aVar);
                break;
        }
        return true;
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [com.miui.antispam.ui.activity.z, b.b.a.e.i$a, android.content.Context, com.miui.antispam.ui.view.RecyclerViewExt$d, com.miui.antispam.ui.activity.r, miui.app.Activity] */
    public void onCreate(Bundle bundle) {
        if (r.f2610a) {
            setTheme(2131821040);
        }
        super.onCreate(bundle);
        setContentView(R.layout.fw_black_list_fragment);
        k.a((Activity) this);
        this.p = getIntent().getIntExtra("key_sim_id", 1);
        this.g = findViewById(16908292);
        this.i = (ImageView) findViewById(R.id.emptyImage);
        this.h = (TextView) findViewById(R.id.emptyText);
        this.e = (RecyclerViewExt) findViewById(16908298);
        this.e.setLayoutManager(new LinearLayoutManager(this));
        this.f = e();
        this.e.setAdapter(this.f);
        this.f.a((Activity) this, (RecyclerViewExt.d) this);
        this.l = LayoutInflater.from(this).inflate(R.layout.sp_choose_mode, (ViewGroup) null);
        this.j = (CheckBox) this.l.findViewById(R.id.SMSpass);
        this.k = (CheckBox) this.l.findViewById(R.id.Phonepass);
        if (this.f2612c) {
            this.j.setVisibility(8);
        }
        registerForContextMenu(this.e);
        i.a((Context) this).a((i.a) this);
    }

    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        this.f.b();
        getMenuInflater().inflate(R.menu.list_view_edit_mode_menu, menu);
        if (!this.q) {
            menu.findItem(R.id.edit_mode_white).setVisible(false);
        }
        if (r.f2610a) {
            return true;
        }
        EditActionMode editActionMode = (EditActionMode) actionMode;
        editActionMode.setButton(16908313, (CharSequence) null, b() ? miui.R.drawable.action_mode_title_button_cancel_dark : miui.R.drawable.action_mode_title_button_cancel_light);
        editActionMode.setButton(16908314, (CharSequence) null, b() ? miui.R.drawable.action_mode_title_button_select_all_dark : miui.R.drawable.action_mode_title_button_select_all_light);
        return true;
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [com.miui.antispam.ui.activity.z, android.content.Context, com.miui.antispam.ui.activity.r, miui.app.Activity] */
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        e.a aVar = (e.a) this.f.a(((RecyclerViewExt.e) contextMenuInfo).f2646a);
        contextMenu.setHeaderTitle(aVar.f1339c.indexOf("***") == 0 ? aVar.e : aVar.f1339c);
        if (this.q) {
            int a2 = n.a((Context) this, aVar.f1339c);
            int b2 = !this.f2612c ? n.b((Context) this, aVar.f1339c) : 0;
            if (a2 > 0) {
                contextMenu.add(0, 3, 0, getString(R.string.menu_bl_call_log));
            }
            if (b2 > 0) {
                contextMenu.add(0, 4, 0, R.string.menu_bl_sms_log);
            }
        }
        if (!aVar.f1339c.contains("*")) {
            contextMenu.add(0, 5, 0, getString(R.string.menu_call));
            contextMenu.add(0, 6, 0, R.string.menu_sms);
        }
        contextMenu.add(0, 10, 0, R.string.menu_edit);
        contextMenu.add(0, 8, 0, this.q ? R.string.menu_bl_remove : R.string.menu_wh_remove);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 100, 0, R.string.menu_add).setIcon(b() ? miui.R.drawable.action_button_new_dark : miui.R.drawable.action_button_new_light).setShowAsAction(2);
        return true;
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [com.miui.antispam.ui.activity.z, b.b.a.e.i$a, android.content.Context, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onDestroy() {
        z.super.onDestroy();
        i.a((Context) this).b((i.a) this);
    }

    public void onDestroyActionMode(ActionMode actionMode) {
        this.f.c();
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        this.f.setData((List<Object>) null);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 100) {
            return z.super.onOptionsItemSelected(menuItem);
        }
        c();
        return true;
    }

    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }
}
