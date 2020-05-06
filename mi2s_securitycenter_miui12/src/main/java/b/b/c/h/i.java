package b.b.c.h;

import android.text.TextUtils;
import com.xiaomi.stat.MiStat;
import com.xiaomi.stat.NetAvailableEvent;
import miui.os.Build;

public class i {

    /* renamed from: a  reason: collision with root package name */
    public static final boolean f1737a = Build.IS_INTERNATIONAL_BUILD;

    public static void a(j jVar, int i, int i2) {
        if (!f1737a) {
            MiStat.trackNetAvaliable(new NetAvailableEvent.Builder().flag(!TextUtils.isEmpty(jVar.a()) ? jVar.a() : "terminaldot_unset_flag").resultType(i != 200 ? i != 408 ? 1 : 2 : 0).responseCode(i).retryCount(i2).build());
        }
    }
}
