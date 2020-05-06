package b.b.n;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import b.b.p.f;
import com.miui.common.persistence.b;
import com.miui.systemAdSolution.common.AdInfo;
import java.io.File;
import java.util.List;

public class h implements f.c {

    /* renamed from: a  reason: collision with root package name */
    private String f1865a;

    /* renamed from: b  reason: collision with root package name */
    private Context f1866b;

    public h(Context context, String str) {
        this.f1866b = context;
        this.f1865a = str;
    }

    public boolean a(List<AdInfo> list) {
        String str;
        String str2;
        Context context;
        if (!TextUtils.isEmpty(this.f1865a)) {
            boolean a2 = b.a(this.f1865a, false);
            if (a2) {
                context = this.f1866b;
                str2 = this.f1865a;
                str = "skin_resource_B";
            } else {
                context = this.f1866b;
                str2 = this.f1865a;
                str = "skin_resource_A";
            }
            String a3 = l.a(context, str2, str);
            File file = new File(a3);
            if (file.exists()) {
                l.a(file);
            }
            if (list == null || list.size() <= 0) {
                b.b(this.f1865a, !a2);
                Log.d("SecAdInfoListener", "onGetAllAdInfo is A: " + a2);
            } else {
                l.a(this.f1866b, list, a3, this.f1865a);
                return true;
            }
        }
        return false;
    }
}
