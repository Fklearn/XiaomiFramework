package com.miui.optimizecenter.storage.b;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import com.miui.optimizecenter.storage.d.a;
import com.miui.securitycenter.R;

public class c {
    public static void a(Activity activity, String str) {
        a(activity, str, (String) null, false);
    }

    private static void a(Activity activity, String str, String str2, boolean z) {
        a a2 = com.miui.optimizecenter.storage.d.c.a((Context) activity).a(str);
        if (a2 != null) {
            Resources resources = activity.getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            String b2 = Build.VERSION.SDK_INT > 24 ? a2.b() : a2.a();
            builder.setTitle(TextUtils.expandTemplate(resources.getString(R.string.storage_wizard_format_confirm_v2_title), new CharSequence[]{b2}));
            builder.setMessage(TextUtils.expandTemplate(resources.getString(R.string.storage_wizard_format_confirm_v2_body), new CharSequence[]{a2.a(), b2, b2}));
            builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
            builder.setPositiveButton(TextUtils.expandTemplate(resources.getString(R.string.storage_wizard_format_confirm_v2_action), new CharSequence[]{b2}), new a(str, str2, z, activity));
            builder.create().show();
        }
    }

    static /* synthetic */ void a(String str, String str2, boolean z, Activity activity, DialogInterface dialogInterface, int i) {
        try {
            Intent parseUri = Intent.parseUri("#Intent;component=com.android.settings/.deviceinfo.StorageWizardFormatProgress;end", 0);
            parseUri.putExtra("android.os.storage.extra.DISK_ID", str);
            parseUri.putExtra("format_forget_uuid", str2);
            parseUri.putExtra("format_private", z);
            activity.startActivity(parseUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
