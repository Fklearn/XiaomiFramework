package b.b.a.d.b;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import b.b.a.a.a;
import b.b.a.d.a.l;
import b.b.a.d.a.o;
import b.b.a.e.g;
import b.b.a.e.n;
import b.b.a.e.q;
import b.b.c.j.d;
import com.miui.antispam.ui.activity.MainActivity;
import com.miui.maml.data.VariableNames;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.cloud.CloudPushConstants;

public class y extends c {
    public static final String[] t = {"blocked_threads._id", "address", CloudPushConstants.XML_NAME, VariableNames.VAR_DATE, "message_count", "unread_count", "snippet", "snippet_cs"};
    private MenuItem u;
    private MenuItem v;
    private int w = 0;
    private int x = 0;
    private AtomicBoolean y = new AtomicBoolean(false);
    private boolean z = false;

    /* access modifiers changed from: private */
    public void a(long[] jArr) {
        if (jArr != null) {
            if (jArr.length > 15) {
                d();
            }
            g.a(this.l, jArr);
        }
    }

    private void f() {
        int i;
        TextView textView;
        this.e.setVisibility(0);
        this.i.setVisibility(8);
        this.f1378a.setVisibility(8);
        if (!q.b()) {
            textView = this.f;
            i = R.string.antispam_xpace_text;
        } else {
            textView = this.f;
            i = R.string.antispam_mms_text;
        }
        textView.setText(i);
        this.h.setContentDescription(this.f1380c.getString(i));
        MenuItem menuItem = this.u;
        if (menuItem != null) {
            menuItem.setVisible(false);
        }
        MenuItem menuItem2 = this.v;
        if (menuItem2 != null) {
            menuItem2.setVisible(false);
        }
    }

    private void g() {
        Bundle bundle = new Bundle();
        bundle.putInt("first_load_count", 100);
        if (!this.z) {
            getLoaderManager().initLoader(1, bundle, this);
            getLoaderManager().initLoader(2, (Bundle) null, this);
            getLoaderManager().initLoader(3, (Bundle) null, this);
            getLoaderManager().initLoader(4, (Bundle) null, this);
            this.z = true;
            return;
        }
        getLoaderManager().restartLoader(1, bundle, this);
        getLoaderManager().restartLoader(2, (Bundle) null, this);
        getLoaderManager().restartLoader(3, (Bundle) null, this);
        getLoaderManager().restartLoader(4, (Bundle) null, this);
    }

    private void h() {
        MenuItem menuItem = this.u;
        boolean z2 = true;
        if (menuItem != null) {
            menuItem.setVisible(this.w > 0);
            if (g.d()) {
                this.u.setEnabled(false);
            } else {
                this.u.setEnabled(this.x > 0);
            }
        }
        MenuItem menuItem2 = this.v;
        if (menuItem2 != null) {
            menuItem2.setVisible(this.w > 0);
            if (g.d()) {
                this.v.setEnabled(false);
                return;
            }
            MenuItem menuItem3 = this.v;
            if (this.w <= 0) {
                z2 = false;
            }
            menuItem3.setEnabled(z2);
        }
    }

    public l a(Context context) {
        return new o(context);
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            int id = loader.getId();
            if (id != 1) {
                if (id == 2) {
                    this.f1381d.b(false);
                    this.f1381d.setData(b(cursor));
                    this.y.set(true);
                } else if (id == 3) {
                    if (cursor.moveToFirst()) {
                        this.w = cursor.getInt(0);
                        this.x = cursor.getInt(1);
                    }
                    this.j.setVisibility(8);
                    if (this.w > 0) {
                        this.h.onPause();
                        this.e.setVisibility(8);
                        this.f1379b.setVisibility(0);
                    } else {
                        this.h.onResume();
                        this.e.setVisibility(0);
                        this.f1379b.setVisibility(8);
                        this.f.setText(R.string.bl_no_block_sms);
                        this.h.setContentDescription(this.f1380c.getString(R.string.bl_no_block_sms));
                    }
                    h();
                } else if (id == 4) {
                    a(cursor);
                }
            } else if (!this.y.get()) {
                this.f1381d.b(false);
                this.f1381d.setData(b(cursor));
            }
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity] */
    public void a(ActionMode actionMode, boolean z2) {
        AlertDialog alertDialog = this.n;
        if (alertDialog == null || !alertDialog.isShowing()) {
            this.n = new AlertDialog.Builder(this.f1380c).setTitle(z2 ? R.string.delete_all_sms : R.string.sms_delete_title).setMessage(z2 ? R.string.sms_delete_all_hint : R.string.sms_delete_hint).setPositiveButton(R.string.button_text_delete, new x(this, actionMode, z2)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
        }
    }

    public String b() {
        return this.f1380c.b() ? "maml/antispam/dark/sms" : "maml/antispam/light/sms";
    }

    public List<Object> b(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if (cursor.moveToFirst()) {
            do {
                int i = cursor.getInt(cursor.getColumnIndex("_id"));
                String string = cursor.getString(cursor.getColumnIndex("address"));
                int i2 = cursor.getInt(cursor.getColumnIndex("message_count"));
                int i3 = cursor.getInt(cursor.getColumnIndex("unread_count"));
                String string2 = cursor.getString(cursor.getColumnIndex("snippet"));
                int i4 = cursor.getInt(cursor.getColumnIndex("snippet_cs"));
                long j = cursor.getLong(cursor.getColumnIndex(VariableNames.VAR_DATE));
                int i5 = cursor.getInt(cursor.getColumnIndex("reason"));
                int columnIndex = cursor.getColumnIndex("data1");
                arrayList.add(new o.a(i, string, i2, i3, string2, i4, j, i5, columnIndex != -1 ? cursor.getString(columnIndex) : ""));
            } while (cursor.moveToNext());
        }
        return arrayList;
    }

    public String c() {
        return "reason";
    }

    public void e() {
        getLoaderManager().restartLoader(2, (Bundle) null, this);
        getLoaderManager().restartLoader(3, (Bundle) null, this);
    }

    /* JADX WARNING: type inference failed for: r3v1, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity] */
    /* JADX WARNING: type inference failed for: r3v2, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity] */
    /* JADX WARNING: type inference failed for: r3v3, types: [android.content.Context] */
    /* JADX WARNING: type inference failed for: r3v4, types: [android.content.Context] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.content.Loader<android.database.Cursor> onCreateLoader(int r10, android.os.Bundle r11) {
        /*
            r9 = this;
            int r0 = r9.r
            r1 = 0
            r2 = -1
            if (r0 != r2) goto L_0x0008
            r6 = r1
            goto L_0x000b
        L_0x0008:
            java.lang.String r0 = "reason = ?"
            r6 = r0
        L_0x000b:
            r0 = 2
            r2 = 0
            r3 = 1
            if (r10 != r3) goto L_0x0046
            android.content.CursorLoader r10 = new android.content.CursorLoader
            com.miui.antispam.ui.activity.MainActivity r1 = r9.f1380c
            android.net.Uri r4 = b.b.a.e.c.a.f1419c
            java.lang.String[] r5 = t
            java.lang.String r7 = "first_load_count"
            if (r6 != 0) goto L_0x0029
            java.lang.String[] r0 = new java.lang.String[r3]
            int r11 = r11.getInt(r7)
            java.lang.String r11 = java.lang.String.valueOf(r11)
            r0[r2] = r11
            goto L_0x003d
        L_0x0029:
            java.lang.String[] r0 = new java.lang.String[r0]
            int r8 = r9.r
            java.lang.String r8 = java.lang.String.valueOf(r8)
            r0[r2] = r8
            int r11 = r11.getInt(r7)
            java.lang.String r11 = java.lang.String.valueOf(r11)
            r0[r3] = r11
        L_0x003d:
            r7 = r0
            java.lang.String r8 = "date DESC"
            r2 = r10
            r3 = r1
            r2.<init>(r3, r4, r5, r6, r7, r8)
            return r10
        L_0x0046:
            if (r10 != r0) goto L_0x0066
            android.content.CursorLoader r10 = new android.content.CursorLoader
            com.miui.antispam.ui.activity.MainActivity r11 = r9.f1380c
            android.net.Uri r4 = b.b.a.e.c.a.f1419c
            java.lang.String[] r5 = t
            if (r6 != 0) goto L_0x0053
            goto L_0x005d
        L_0x0053:
            java.lang.String[] r1 = new java.lang.String[r3]
            int r0 = r9.r
            java.lang.String r0 = java.lang.String.valueOf(r0)
            r1[r2] = r0
        L_0x005d:
            r7 = r1
            java.lang.String r8 = "date DESC"
            r2 = r10
            r3 = r11
            r2.<init>(r3, r4, r5, r6, r7, r8)
            return r10
        L_0x0066:
            r11 = 3
            if (r10 != r11) goto L_0x007f
            android.content.CursorLoader r10 = new android.content.CursorLoader
            com.miui.antispam.ui.activity.MainActivity r3 = r9.f1380c
            android.net.Uri r4 = miui.provider.ExtraTelephony.MmsSms.BLOCKED_CONVERSATION_CONTENT_URI
            java.lang.String r11 = "sum(message_count)"
            java.lang.String r0 = "sum(unread_count)"
            java.lang.String[] r5 = new java.lang.String[]{r11, r0}
            r6 = 0
            r7 = 0
            r8 = 0
            r2 = r10
            r2.<init>(r3, r4, r5, r6, r7, r8)
            return r10
        L_0x007f:
            r11 = 4
            if (r10 != r11) goto L_0x0093
            android.content.CursorLoader r10 = new android.content.CursorLoader
            com.miui.antispam.ui.activity.MainActivity r3 = r9.f1380c
            android.net.Uri r4 = b.b.a.e.c.a.f1419c
            java.lang.String[] r5 = t
            r6 = 0
            r7 = 0
            java.lang.String r8 = "date DESC"
            r2 = r10
            r2.<init>(r3, r4, r5, r6, r7, r8)
            return r10
        L_0x0093:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.a.d.b.y.onCreateLoader(int, android.os.Bundle):android.content.Loader");
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MainActivity mainActivity = this.f1380c;
        if (mainActivity != null) {
            mainActivity.getMenuInflater().inflate(R.menu.report_menu, menu);
            this.u = menu.findItem(R.id.read_menu);
            this.v = menu.findItem(R.id.delete_all_menu);
            h();
        }
    }

    /* JADX WARNING: type inference failed for: r2v3, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity] */
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        if (!q.b() || !n.c((Context) this.f1380c)) {
            f();
        } else {
            g();
        }
        if (g.c()) {
            d();
        }
        return onCreateView;
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem == this.u) {
            d.a(new w(this, this.f1380c.getApplicationContext()));
            this.f1381d.b(true);
            a.a("sms_all_read");
        } else if (menuItem == this.v) {
            a.c();
            a((ActionMode) null, true);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /* JADX WARNING: type inference failed for: r0v1, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity] */
    public void onResume() {
        super.onResume();
        a.a();
        if (!q.b() || !n.c((Context) this.f1380c)) {
            f();
        } else {
            g();
        }
    }
}
