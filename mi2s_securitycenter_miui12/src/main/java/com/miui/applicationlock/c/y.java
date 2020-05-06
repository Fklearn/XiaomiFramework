package com.miui.applicationlock.c;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import com.miui.msa.util.MsaUtils;
import com.xiaomi.ad.feedback.IAdFeedbackListener;
import com.xiaomi.ad.feedback.IAdFeedbackService;
import java.util.List;

public class y {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static String f3335a = "DislikeManager";

    /* renamed from: b  reason: collision with root package name */
    private static y f3336b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public IAdFeedbackService f3337c;

    /* renamed from: d  reason: collision with root package name */
    private ServiceConnection f3338d;
    /* access modifiers changed from: private */
    public boolean e = false;

    private y() {
    }

    public static y b() {
        if (f3336b == null) {
            f3336b = new y();
        }
        return f3336b;
    }

    private Intent c(Context context) {
        Intent intent = new Intent("miui.intent.action.ad.FEEDBACK_SERVICE");
        intent.setPackage(MsaUtils.getMsaPackageName(context));
        return intent;
    }

    public void a(Context context, IAdFeedbackListener iAdFeedbackListener, String str, String str2, String str3) {
        Intent c2 = c(context);
        this.f3338d = new w(this, iAdFeedbackListener, str, str2, str3);
        if (!context.bindService(c2, this.f3338d, 1)) {
            this.f3338d = null;
            Log.e(f3335a, "bind service fail");
        }
    }

    public void a(Context context, IAdFeedbackListener iAdFeedbackListener, String str, String str2, List<String> list) {
        this.e = true;
        Intent c2 = c(context);
        this.f3338d = new x(this, iAdFeedbackListener, str, str2, list);
        if (!context.bindService(c2, this.f3338d, 1)) {
            Log.e(f3335a, "bind service fail");
        }
    }

    public boolean a(Context context) {
        Intent c2 = c(context);
        v vVar = new v(this);
        if (!context.bindService(c2, vVar, 1)) {
            return false;
        }
        context.unbindService(vVar);
        return true;
    }

    public void b(Context context) {
        ServiceConnection serviceConnection = this.f3338d;
        if (serviceConnection != null) {
            context.unbindService(serviceConnection);
            this.f3338d = null;
        }
    }
}
