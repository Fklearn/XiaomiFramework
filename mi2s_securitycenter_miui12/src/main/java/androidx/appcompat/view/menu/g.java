package androidx.appcompat.view.menu;

import android.os.SystemClock;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.CascadingMenuPopup;
import androidx.appcompat.widget.V;

class g implements V {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CascadingMenuPopup f377a;

    g(CascadingMenuPopup cascadingMenuPopup) {
        this.f377a = cascadingMenuPopup;
    }

    public void a(@NonNull j jVar, @NonNull MenuItem menuItem) {
        CascadingMenuPopup.a aVar = null;
        this.f377a.h.removeCallbacksAndMessages((Object) null);
        int size = this.f377a.j.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                i = -1;
                break;
            } else if (jVar == this.f377a.j.get(i).f351b) {
                break;
            } else {
                i++;
            }
        }
        if (i != -1) {
            int i2 = i + 1;
            if (i2 < this.f377a.j.size()) {
                aVar = this.f377a.j.get(i2);
            }
            this.f377a.h.postAtTime(new f(this, aVar, menuItem, jVar), jVar, SystemClock.uptimeMillis() + 200);
        }
    }

    public void b(@NonNull j jVar, @NonNull MenuItem menuItem) {
        this.f377a.h.removeCallbacksAndMessages(jVar);
    }
}
