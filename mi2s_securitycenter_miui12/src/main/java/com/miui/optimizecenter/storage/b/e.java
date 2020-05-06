package com.miui.optimizecenter.storage.b;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.miui.optimizecenter.storage.d.c;
import com.miui.optimizecenter.storage.d.d;
import com.miui.securitycenter.R;

public class e {
    public static void a(Context context, String str) {
        new Bundle().putString("android.os.storage.extra.VOLUME_ID", str);
        d b2 = c.a(context).b(str);
        Context applicationContext = context.getApplicationContext();
        if (b2 == null) {
            Log.d("VolumeUnmountedFragment", "VolumeInfo vol is null");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(TextUtils.expandTemplate(context.getResources().getString(R.string.storage_dialog_unmounted), new CharSequence[]{b2.a().a()}));
        builder.setPositiveButton(R.string.storage_menu_mount, new d(b2, applicationContext));
        builder.setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null);
        builder.create().show();
    }
}
