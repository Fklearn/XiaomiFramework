package com.miui.permcenter.permissions;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
import com.miui.permcenter.n;
import com.miui.permission.PermissionInfo;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.app.Activity;

public class AppPermissionItemActivity extends b.b.c.c.a implements LoaderManager.LoaderCallbacks<List<PermissionInfo>> {

    /* renamed from: a  reason: collision with root package name */
    public static final String f6191a = D.class.getSimpleName();

    /* renamed from: b  reason: collision with root package name */
    private a f6192b;

    /* renamed from: c  reason: collision with root package name */
    private long f6193c;

    /* renamed from: d  reason: collision with root package name */
    private ArrayList<PermissionInfo> f6194d;
    private ArrayList<PermissionInfo> e;

    private static class a extends b.b.c.i.a<List<PermissionInfo>> {

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<AppPermissionItemActivity> f6195b;

        /* JADX WARNING: type inference failed for: r2v0, types: [com.miui.permcenter.permissions.AppPermissionItemActivity, android.content.Context, java.lang.Object] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public a(com.miui.permcenter.permissions.AppPermissionItemActivity r2) {
            /*
                r1 = this;
                r1.<init>(r2)
                java.lang.ref.WeakReference r0 = new java.lang.ref.WeakReference
                r0.<init>(r2)
                r1.f6195b = r0
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.permcenter.permissions.AppPermissionItemActivity.a.<init>(com.miui.permcenter.permissions.AppPermissionItemActivity):void");
        }

        public List<PermissionInfo> loadInBackground() {
            Activity activity = (AppPermissionItemActivity) this.f6195b.get();
            if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                return null;
            }
            List<PermissionInfo> allPermissions = PermissionManager.getInstance(activity).getAllPermissions(1);
            for (PermissionInfo id : allPermissions) {
                int i = (id.getId() > PermissionManager.PERM_ID_BACKGROUND_LOCATION ? 1 : (id.getId() == PermissionManager.PERM_ID_BACKGROUND_LOCATION ? 0 : -1));
            }
            return allPermissions;
        }
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [com.miui.permcenter.permissions.AppPermissionItemActivity, android.content.Context, miui.app.Activity] */
    /* renamed from: a */
    public void onLoadFinished(Loader<List<PermissionInfo>> loader, List<PermissionInfo> list) {
        Intent intent;
        int i;
        ArrayList<PermissionInfo> arrayList;
        if (list != null && list.size() > 0) {
            this.f6194d = new ArrayList<>();
            this.e = new ArrayList<>();
            Iterator<PermissionInfo> it = list.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                PermissionInfo next = it.next();
                if (this.f6193c == next.getId()) {
                    Intent intent2 = new Intent(this, PermissionAppsEditorActivity.class);
                    intent2.putExtra(":miui:starting_window_label", next.getName());
                    intent2.putExtra("extra_permission_id", next.getId());
                    intent2.putExtra("extra_permission_name", next.getName());
                    intent2.putExtra("extra_permission_flags", next.getFlags());
                    startActivity(intent2);
                    break;
                }
                if (n.a(Long.valueOf(next.getId()))) {
                    arrayList = this.f6194d;
                } else if (n.b(Long.valueOf(next.getId()))) {
                    arrayList = this.e;
                }
                arrayList.add(next);
            }
            long j = this.f6193c;
            if (j == -1) {
                intent = new Intent(this, SecondPermissionAppsActivity.class);
                intent.putExtra(":miui:starting_window_label", getString(R.string.SMS_and_MMS));
                intent.putParcelableArrayListExtra("extra_permission_list", this.e);
                i = 1;
            } else if (j == -2) {
                intent = new Intent(this, SecondPermissionAppsActivity.class);
                intent.putExtra(":miui:starting_window_label", getString(R.string.call_and_contact));
                intent.putParcelableArrayListExtra("extra_permission_list", this.f6194d);
                i = 2;
            }
            intent.putExtra("extra_group_type", i);
            startActivity(intent);
        }
        finish();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            this.f6193c = Long.parseLong(getIntent().getStringExtra("permissionID"));
        } catch (NumberFormatException e2) {
            e2.printStackTrace();
            finish();
        }
        if (this.f6193c == 0) {
            finish();
        }
        Loader loader = getLoaderManager().getLoader(200);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(200, (Bundle) null, this);
        if (Build.VERSION.SDK_INT >= 24 && bundle != null && loader != null) {
            loaderManager.restartLoader(200, (Bundle) null, this);
        }
    }

    public Loader<List<PermissionInfo>> onCreateLoader(int i, Bundle bundle) {
        this.f6192b = new a(this);
        return this.f6192b;
    }

    public void onLoaderReset(Loader<List<PermissionInfo>> loader) {
    }
}
