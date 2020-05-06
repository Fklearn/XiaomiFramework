package b.b.a.d.b;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.recyclerview.widget.LinearLayoutManager;
import b.b.a.d.a.k;
import b.b.a.e.n;
import com.miui.antispam.ui.activity.AddAntiSpamActivity;
import com.miui.antispam.ui.view.RecyclerViewExt;
import com.miui.maml.data.VariableNames;
import com.miui.securitycenter.R;
import miui.app.ActionBar;
import miui.app.Activity;
import miuix.recyclerview.widget.RecyclerView;

public class i extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /* renamed from: a  reason: collision with root package name */
    private RecyclerView f1392a;

    /* renamed from: b  reason: collision with root package name */
    private Button f1393b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Activity f1394c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public ActionBar f1395d;
    private k e;
    /* access modifiers changed from: private */
    public String f;
    /* access modifiers changed from: private */
    public int g;

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0053, code lost:
        r0 = r10.f1394c.getString(com.miui.securitycenter.R.string.mark_harass);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005c, code lost:
        if (r11 == null) goto L_0x0061;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r11.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0061, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        r0 = r10.f1394c.getString(com.miui.securitycenter.R.string.mark_sell);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x006b, code lost:
        if (r11 == null) goto L_0x0070;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        r11.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0070, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        r0 = r10.f1394c.getString(com.miui.securitycenter.R.string.mark_agent);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x007a, code lost:
        if (r11 == null) goto L_0x007f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        r11.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x007f, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:?, code lost:
        r0 = r10.f1394c.getString(com.miui.securitycenter.R.string.mark_fraud);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0089, code lost:
        if (r11 == null) goto L_0x0092;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:?, code lost:
        r11.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x008f, code lost:
        r11 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0090, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0092, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String a(java.lang.String r11) {
        /*
            r10 = this;
            java.lang.String r0 = "firewalltype"
            java.lang.String r1 = ""
            miui.app.Activity r2 = r10.f1394c     // Catch:{ Exception -> 0x00af }
            android.content.ContentResolver r3 = r2.getContentResolver()     // Catch:{ Exception -> 0x00af }
            android.net.Uri r4 = miui.provider.ExtraContacts.Calls.CONTENT_CONVERSATION_URI     // Catch:{ Exception -> 0x00af }
            java.lang.String[] r5 = new java.lang.String[]{r0}     // Catch:{ Exception -> 0x00af }
            java.lang.String r6 = "number = ? AND firewalltype >= ? AND firewalltype <= ?"
            r2 = 3
            java.lang.String[] r7 = new java.lang.String[r2]     // Catch:{ Exception -> 0x00af }
            r2 = 0
            r7[r2] = r11     // Catch:{ Exception -> 0x00af }
            r11 = 1
            r2 = 8
            java.lang.String r8 = java.lang.String.valueOf(r2)     // Catch:{ Exception -> 0x00af }
            r7[r11] = r8     // Catch:{ Exception -> 0x00af }
            r11 = 2
            r9 = 14
            java.lang.String r8 = java.lang.String.valueOf(r9)     // Catch:{ Exception -> 0x00af }
            r7[r11] = r8     // Catch:{ Exception -> 0x00af }
            java.lang.String r8 = "date DESC"
            android.database.Cursor r11 = r3.query(r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x00af }
            r3 = 0
            if (r11 == 0) goto L_0x00a9
        L_0x0033:
            boolean r4 = r11.moveToNext()     // Catch:{ Throwable -> 0x0095 }
            if (r4 == 0) goto L_0x00a9
            int r4 = r11.getColumnIndex(r0)     // Catch:{ Throwable -> 0x0095 }
            r5 = -1
            if (r4 != r5) goto L_0x0042
            goto L_0x00a9
        L_0x0042:
            int r4 = r11.getInt(r4)     // Catch:{ Throwable -> 0x0095 }
            if (r4 == r2) goto L_0x0080
            r5 = 10
            if (r4 == r5) goto L_0x0071
            r5 = 12
            if (r4 == r5) goto L_0x0062
            if (r4 == r9) goto L_0x0053
            goto L_0x0033
        L_0x0053:
            miui.app.Activity r0 = r10.f1394c     // Catch:{ Throwable -> 0x0095 }
            r2 = 2131756789(0x7f1006f5, float:1.9144495E38)
            java.lang.String r0 = r0.getString(r2)     // Catch:{ Throwable -> 0x0095 }
            if (r11 == 0) goto L_0x0061
            r11.close()     // Catch:{ Exception -> 0x008f }
        L_0x0061:
            return r0
        L_0x0062:
            miui.app.Activity r0 = r10.f1394c     // Catch:{ Throwable -> 0x0095 }
            r2 = 2131756803(0x7f100703, float:1.9144524E38)
            java.lang.String r0 = r0.getString(r2)     // Catch:{ Throwable -> 0x0095 }
            if (r11 == 0) goto L_0x0070
            r11.close()     // Catch:{ Exception -> 0x008f }
        L_0x0070:
            return r0
        L_0x0071:
            miui.app.Activity r0 = r10.f1394c     // Catch:{ Throwable -> 0x0095 }
            r2 = 2131756781(0x7f1006ed, float:1.914448E38)
            java.lang.String r0 = r0.getString(r2)     // Catch:{ Throwable -> 0x0095 }
            if (r11 == 0) goto L_0x007f
            r11.close()     // Catch:{ Exception -> 0x008f }
        L_0x007f:
            return r0
        L_0x0080:
            miui.app.Activity r0 = r10.f1394c     // Catch:{ Throwable -> 0x0095 }
            r2 = 2131756785(0x7f1006f1, float:1.9144487E38)
            java.lang.String r0 = r0.getString(r2)     // Catch:{ Throwable -> 0x0095 }
            if (r11 == 0) goto L_0x0092
            r11.close()     // Catch:{ Exception -> 0x008f }
            goto L_0x0092
        L_0x008f:
            r11 = move-exception
            r1 = r0
            goto L_0x00b0
        L_0x0092:
            return r0
        L_0x0093:
            r0 = move-exception
            goto L_0x0098
        L_0x0095:
            r0 = move-exception
            r3 = r0
            throw r3     // Catch:{ all -> 0x0093 }
        L_0x0098:
            if (r11 == 0) goto L_0x00a8
            if (r3 == 0) goto L_0x00a5
            r11.close()     // Catch:{ Throwable -> 0x00a0 }
            goto L_0x00a8
        L_0x00a0:
            r11 = move-exception
            r3.addSuppressed(r11)     // Catch:{ Exception -> 0x00af }
            goto L_0x00a8
        L_0x00a5:
            r11.close()     // Catch:{ Exception -> 0x00af }
        L_0x00a8:
            throw r0     // Catch:{ Exception -> 0x00af }
        L_0x00a9:
            if (r11 == 0) goto L_0x00c6
            r11.close()     // Catch:{ Exception -> 0x00af }
            goto L_0x00c6
        L_0x00af:
            r11 = move-exception
        L_0x00b0:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "getMarkTypeTag failed. "
            r0.append(r2)
            r0.append(r11)
            java.lang.String r11 = r0.toString()
            java.lang.String r0 = "CallLogFragment"
            android.util.Log.e(r0, r11)
        L_0x00c6:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.a.d.b.i.a(java.lang.String):java.lang.String");
    }

    private void a() {
        new h(this, this.f1394c).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null});
    }

    /* access modifiers changed from: private */
    public void a(Context context, String str) {
        Intent intent = new Intent("miui.intent.action.ADD_FIREWALL");
        intent.setType("vnd.android.cursor.item/firewall-blacklist");
        intent.putExtra(AddAntiSpamActivity.f2508d, 1);
        intent.putExtra(AddAntiSpamActivity.e, 2);
        intent.putExtra("numbers", new String[]{str});
        startActivity(intent);
        this.f1394c.finish();
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            this.e.a(cursor);
        }
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            n.a((Context) this.f1394c, this.f, -1);
        } else if (itemId == 2) {
            n.h(this.f1394c, this.f);
        }
        return true;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.f = getArguments().getString("number");
        this.g = getArguments().getInt("number_presentation", 1);
        setHasOptionsMenu(true);
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        Cursor cursor = (Cursor) this.e.a(((RecyclerViewExt.e) contextMenuInfo).f2646a);
        contextMenu.setHeaderTitle(n.a((Context) this.f1394c, cursor.getLong(cursor.getColumnIndex(VariableNames.VAR_DATE)), false));
        contextMenu.add(0, 1, 0, R.string.menu_call);
        contextMenu.add(0, 2, 0, R.string.menu_sms);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (this.g != 1) {
            this.f1393b.setVisibility(8);
            return new CursorLoader(this.f1394c, CallLog.Calls.CONTENT_URI, (String[]) null, "presentation <> ? AND firewalltype >= ? ", new String[]{String.valueOf(1), String.valueOf(3)}, "date DESC");
        }
        return new CursorLoader(this.f1394c, CallLog.Calls.CONTENT_URI, (String[]) null, " PHONE_NUMBERS_EQUAL(number, ?, 0) AND firewalltype >= ? ", new String[]{this.f, String.valueOf(3)}, "date DESC");
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        this.f1394c = getActivity();
        this.f1395d = this.f1394c.getActionBar();
        View inflate = layoutInflater.inflate(R.layout.fw_calllog_fragment, (ViewGroup) null);
        this.f1393b = (Button) inflate.findViewById(R.id.no_block);
        this.f1393b.setOnClickListener(new g(this));
        this.e = new k(this.f1394c);
        this.f1392a = (RecyclerView) inflate.findViewById(16908298);
        this.f1392a.setLayoutManager(new LinearLayoutManager(this.f1394c));
        this.f1392a.setAdapter(this.e);
        registerForContextMenu(this.f1392a);
        getLoaderManager().initLoader(0, (Bundle) null, this);
        return inflate;
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        this.e.a((Cursor) null);
    }

    public void onResume() {
        super.onResume();
        a();
    }
}
