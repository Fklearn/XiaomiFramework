package b.b.a.d.b;

import android.app.AlertDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import b.b.a.d.a.h;
import b.b.a.e.c;
import b.b.a.e.g;
import b.b.a.e.q;
import com.miui.antispam.ui.activity.MainActivity;
import com.miui.maml.data.VariableNames;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miui.provider.ExtraContacts;

public class l extends c {
    private MenuItem t;

    /* access modifiers changed from: private */
    public void a(boolean z) {
        if (z) {
            d();
            g.a(this.l, this.f1381d);
            return;
        }
        SparseBooleanArray g = this.f1381d.g();
        if (g.size() > 15) {
            d();
        }
        g.a(this.l, this.f1381d, g);
    }

    private void f() {
        MenuItem menuItem;
        if (this.t != null) {
            boolean z = false;
            if (!q.b()) {
                this.t.setVisible(false);
                return;
            }
            this.t.setVisible(this.f1381d.getItemCount() > 0);
            if (g.b()) {
                menuItem = this.t;
            } else {
                menuItem = this.t;
                if (this.f1381d.getItemCount() > 0) {
                    z = true;
                }
            }
            menuItem.setEnabled(z);
        }
    }

    public b.b.a.d.a.l a(Context context) {
        return new h(context);
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            if (loader.getId() == 1) {
                this.j.setVisibility(8);
                if (cursor.getCount() > 0) {
                    this.h.onPause();
                    this.e.setVisibility(8);
                    this.f1379b.setVisibility(0);
                } else {
                    this.h.onResume();
                    this.e.setVisibility(0);
                    this.f1379b.setVisibility(8);
                    this.f.setText(R.string.bl_no_block_call);
                    this.h.setContentDescription(this.f1380c.getString(R.string.bl_no_block_call));
                }
                if (c.f.equals(this.m)) {
                    this.i.setVisibility(0);
                }
                this.f1381d.setData(b(cursor));
                f();
                return;
            }
            a(cursor);
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity] */
    public void a(ActionMode actionMode, boolean z) {
        AlertDialog alertDialog = this.n;
        if (alertDialog == null || !alertDialog.isShowing()) {
            this.n = new AlertDialog.Builder(this.f1380c).setTitle(z ? R.string.delete_all_sms : R.string.call_delete_title).setMessage(z ? R.string.call_delete_all_hint : R.string.call_delete_hint).setPositiveButton(R.string.button_text_delete, new k(this, actionMode, z)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
        }
    }

    public String b() {
        return this.f1380c.b() ? "maml/antispam/dark/call" : "maml/antispam/light/call";
    }

    public List<Object> b(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if (cursor.moveToFirst()) {
            do {
                arrayList.add(new h.a(cursor.getString(cursor.getColumnIndex("number")), cursor.getString(cursor.getColumnIndex("normalized_number")), cursor.getInt(cursor.getColumnIndex("presentation")), cursor.getInt(cursor.getColumnIndex("unRead")), cursor.getInt(cursor.getColumnIndex("total")), cursor.getLong(cursor.getColumnIndex(VariableNames.VAR_DATE)), cursor.getInt(cursor.getColumnIndex("firewalltype"))));
            } while (cursor.moveToNext());
        }
        return arrayList;
    }

    public String c() {
        return "firewalltype";
    }

    public void e() {
        getLoaderManager().restartLoader(1, (Bundle) null, this);
    }

    /* JADX WARNING: type inference failed for: r10v0, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity] */
    /* JADX WARNING: type inference failed for: r3v3, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity] */
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String str;
        if (i == 1) {
            if (this.r == -1) {
                str = "firewalltype >= 3";
            } else {
                str = "firewalltype = " + this.r;
            }
            return new CursorLoader(this.f1380c, ExtraContacts.Calls.CONTENT_CONVERSATION_URI, new String[]{"*", "count() as total", "sum(case when is_read = 0 then 1 else 0 end) as unRead"}, str, (String[]) null, "date DESC");
        }
        return new CursorLoader(this.f1380c, ExtraContacts.Calls.CONTENT_CONVERSATION_URI, new String[]{"firewalltype"}, "firewalltype >= ? ", new String[]{String.valueOf(3)}, "date DESC");
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MainActivity mainActivity = this.f1380c;
        if (mainActivity != null) {
            mainActivity.getMenuInflater().inflate(R.menu.edit_menu, menu);
            this.t = menu.findItem(R.id.delete_all_menu);
            f();
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        if (!q.b()) {
            this.e.setVisibility(0);
            this.i.setVisibility(8);
            this.f1378a.setVisibility(8);
            this.f.setText(R.string.antispam_xpace_text);
            this.h.setContentDescription(this.f1380c.getString(R.string.antispam_xpace_text));
        } else {
            getLoaderManager().initLoader(1, (Bundle) null, this);
            getLoaderManager().initLoader(4, (Bundle) null, this);
        }
        if (g.c()) {
            d();
        }
        return onCreateView;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem == this.t) {
            a((ActionMode) null, true);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity] */
    public void onPause() {
        super.onPause();
        new j(this, this.f1380c).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }
}
