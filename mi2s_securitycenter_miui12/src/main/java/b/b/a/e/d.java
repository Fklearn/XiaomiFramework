package b.b.a.e;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import miui.provider.ExtraTelephony;

class d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ long[] f1425a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ContentResolver f1426b;

    d(long[] jArr, ContentResolver contentResolver) {
        this.f1425a = jArr;
        this.f1426b = contentResolver;
    }

    public void run() {
        ArrayList arrayList = new ArrayList();
        for (long j : this.f1425a) {
            arrayList.add(ContentProviderOperation.newDelete(ExtraTelephony.MmsSms.BLOCKED_CONVERSATION_CONTENT_URI).withSelection("thread_id = " + j, (String[]) null).build());
        }
        try {
            String authority = ExtraTelephony.MmsSms.BLOCKED_CONVERSATION_CONTENT_URI.getAuthority();
            if (TextUtils.isEmpty(authority)) {
                authority = "mms-sms";
            }
            this.f1426b.applyBatch(authority, arrayList);
        } catch (Exception e) {
            Log.e("AntiSpamLogOperator", "delete sms log failed, " + e);
        }
        g.f1432a.set(false);
    }
}
