package com.miui.antispam.ui.activity;

import android.os.Bundle;
import b.b.a.d.a.e;
import com.miui.securitycenter.R;

public class BlackListActivity extends z {
    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.antispam.ui.activity.BlackListActivity] */
    public e e() {
        return new e(this, true);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.q = true;
        this.e.setAdapter(this.f);
        this.j.setText(R.string.st_message_SMS_AntiSpam);
        this.k.setText(R.string.st_message_phone_AntiSpam);
        getLoaderManager().initLoader(0, (Bundle) null, this);
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.content.Loader<android.database.Cursor> onCreateLoader(int r8, android.os.Bundle r9) {
        /*
            r7 = this;
            android.content.CursorLoader r8 = new android.content.CursorLoader
            android.net.Uri r2 = miui.provider.ExtraTelephony.Phonelist.CONTENT_URI
            r9 = 3
            java.lang.String[] r5 = new java.lang.String[r9]
            r9 = 0
            java.lang.String r0 = "1"
            r5[r9] = r0
            int r9 = r7.p
            java.lang.String r9 = java.lang.String.valueOf(r9)
            r0 = 1
            r5[r0] = r9
            java.lang.String r9 = java.lang.String.valueOf(r0)
            r0 = 2
            r5[r0] = r9
            r3 = 0
            java.lang.String r4 = "type = ? AND sim_id = ? AND sync_dirty <> ? "
            r6 = 0
            r0 = r8
            r1 = r7
            r0.<init>(r1, r2, r3, r4, r5, r6)
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.ui.activity.BlackListActivity.onCreateLoader(int, android.os.Bundle):android.content.Loader");
    }
}
