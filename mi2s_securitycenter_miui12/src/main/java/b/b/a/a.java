package b.b.a;

import android.net.Uri;
import android.provider.BaseColumns;
import com.miui.activityutil.o;
import com.miui.securitycenter.Application;
import java.io.File;

public class a {

    /* renamed from: a  reason: collision with root package name */
    public static boolean f1308a = new File(Application.d().getCacheDir(), "debug.log").exists();

    /* renamed from: b.b.a.a$a  reason: collision with other inner class name */
    public static final class C0020a implements BaseColumns {

        /* renamed from: a  reason: collision with root package name */
        public static final Uri f1309a = Uri.parse("content://antispam/report_sms");
    }

    public static final class b implements BaseColumns {

        /* renamed from: a  reason: collision with root package name */
        public static final Uri f1310a = Uri.parse("content://antispam/report_sms_pending");
    }

    public static final class c {

        /* renamed from: a  reason: collision with root package name */
        public static final int f1311a = Integer.valueOf(o.f2310b).intValue();

        /* renamed from: b  reason: collision with root package name */
        public static final int f1312b = Integer.valueOf("2").intValue();

        /* renamed from: c  reason: collision with root package name */
        public static final int f1313c = Integer.valueOf(o.f2312d).intValue();

        /* renamed from: d  reason: collision with root package name */
        public static final int f1314d = Integer.valueOf(o.e).intValue();
        public static final int e = Integer.valueOf(o.f).intValue();
        public static final int f = Integer.valueOf(o.g).intValue();
        public static final int g = Integer.valueOf(o.h).intValue();
    }
}
