package b.b.o.f.a;

import android.os.Build;
import com.miui.applicationlock.widget.LockPatternView;
import java.util.List;

public abstract class e {
    public static e a() {
        return Build.VERSION.SDK_INT >= 23 ? g.a() : f.a();
    }

    public abstract String a(List<LockPatternView.a> list);
}
