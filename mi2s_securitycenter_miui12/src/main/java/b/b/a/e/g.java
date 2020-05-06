package b.b.a.e;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.provider.CallLog;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import b.b.a.d.a.l;
import b.b.c.j.d;
import com.miui.antispam.ui.view.RecyclerViewExt;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

public class g {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static AtomicBoolean f1432a = new AtomicBoolean(false);

    /* renamed from: b  reason: collision with root package name */
    private static AtomicBoolean f1433b = new AtomicBoolean(false);

    public static void a(ContentResolver contentResolver, l lVar) {
        if (!f1433b.get() && contentResolver != null && lVar != null) {
            f1433b.set(true);
            d.a(new f(lVar, contentResolver));
        }
    }

    public static void a(ContentResolver contentResolver, RecyclerViewExt.c cVar, SparseBooleanArray sparseBooleanArray) {
        if (!f1433b.get() && contentResolver != null && cVar != null && sparseBooleanArray.size() != 0) {
            f1433b.set(true);
            d.a(new e(sparseBooleanArray, cVar, contentResolver));
        }
    }

    public static void a(ContentResolver contentResolver, long[] jArr) {
        if (!f1432a.get() && contentResolver != null && jArr.length != 0) {
            f1432a.set(true);
            d.a(new d(jArr, contentResolver));
        }
    }

    /* access modifiers changed from: private */
    public static void b(ContentResolver contentResolver, ArrayList<String> arrayList) {
        if (arrayList.size() > 0) {
            ArrayList arrayList2 = new ArrayList();
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                String next = it.next();
                if (next != null) {
                    String str = TextUtils.isEmpty(next) ? "number" : "normalized_number";
                    ContentProviderOperation.Builder newDelete = ContentProviderOperation.newDelete(CallLog.Calls.CONTENT_URI);
                    arrayList2.add(newDelete.withSelection("firewalltype <> 0 AND " + str + " = ?", new String[]{next}).build());
                }
            }
            try {
                String authority = CallLog.Calls.CONTENT_URI.getAuthority();
                if (TextUtils.isEmpty(authority)) {
                    authority = "call_log";
                }
                contentResolver.applyBatch(authority, arrayList2);
            } catch (Exception e) {
                Log.e("AntiSpamLogOperator", "delete call log failed, " + e);
            }
        }
        f1433b.set(false);
    }

    public static boolean b() {
        return f1433b.get();
    }

    public static boolean c() {
        return d() || b();
    }

    public static boolean d() {
        return f1432a.get();
    }
}
