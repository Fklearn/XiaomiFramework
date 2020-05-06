package androidx.core.view.a;

import android.os.Build;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import java.util.ArrayList;
import java.util.List;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private final Object f813a;

    @RequiresApi(16)
    static class a extends AccessibilityNodeProvider {

        /* renamed from: a  reason: collision with root package name */
        final d f814a;

        a(d dVar) {
            this.f814a = dVar;
        }

        public AccessibilityNodeInfo createAccessibilityNodeInfo(int i) {
            c a2 = this.f814a.a(i);
            if (a2 == null) {
                return null;
            }
            return a2.w();
        }

        public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String str, int i) {
            List<c> a2 = this.f814a.a(str, i);
            if (a2 == null) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            int size = a2.size();
            for (int i2 = 0; i2 < size; i2++) {
                arrayList.add(a2.get(i2).w());
            }
            return arrayList;
        }

        public boolean performAction(int i, int i2, Bundle bundle) {
            return this.f814a.a(i, i2, bundle);
        }
    }

    @RequiresApi(19)
    static class b extends a {
        b(d dVar) {
            super(dVar);
        }

        public AccessibilityNodeInfo findFocus(int i) {
            c b2 = this.f814a.b(i);
            if (b2 == null) {
                return null;
            }
            return b2.w();
        }
    }

    public d() {
        int i = Build.VERSION.SDK_INT;
        this.f813a = i >= 19 ? new b(this) : i >= 16 ? new a(this) : null;
    }

    public d(Object obj) {
        this.f813a = obj;
    }

    @Nullable
    public c a(int i) {
        return null;
    }

    public Object a() {
        return this.f813a;
    }

    @Nullable
    public List<c> a(String str, int i) {
        return null;
    }

    public boolean a(int i, int i2, Bundle bundle) {
        return false;
    }

    @Nullable
    public c b(int i) {
        return null;
    }
}
