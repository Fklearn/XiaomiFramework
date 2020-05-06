package b.c.a.b.d;

import com.google.android.exoplayer2.util.MimeTypes;
import com.xiaomi.stat.MiStat;
import java.io.InputStream;
import java.util.Locale;

public interface d {

    public enum a {
        HTTP("http"),
        HTTPS("https"),
        FILE("file"),
        CONTENT(MiStat.Param.CONTENT),
        ASSETS("assets"),
        DRAWABLE("drawable"),
        PKG_ICON("pkg_icon"),
        APK_ICON("apk_icon"),
        FILE_ICON("file_icon"),
        PKG_ICON_XSPACE("pkg_icon_xspace"),
        PCK_ICON_MANAGED_PROFILE("pkg_icon_managed_profile"),
        VIDEO_FILE(MimeTypes.BASE_TYPE_VIDEO),
        UNKNOWN("");
        
        private String o;
        private String p;

        private a(String str) {
            this.o = str;
            this.p = str + "://";
        }

        public static a b(String str) {
            if (str != null) {
                for (a aVar : values()) {
                    if (aVar.d(str)) {
                        return aVar;
                    }
                }
            }
            return UNKNOWN;
        }

        private boolean d(String str) {
            return str.toLowerCase(Locale.US).startsWith(this.p);
        }

        public String a(String str) {
            if (d(str)) {
                return str.substring(this.p.length());
            }
            throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", new Object[]{str, this.o}));
        }

        public String c(String str) {
            return this.p + str;
        }
    }

    InputStream a(String str, Object obj);
}
