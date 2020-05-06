package b.b.j;

import android.app.Activity;
import android.view.View;
import android.widget.AbsListView;
import com.miui.securityscan.MainActivity;
import com.miui.securityscan.a.G;

class d implements AbsListView.OnScrollListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f1816a;

    d(h hVar) {
        this.f1816a = hVar;
    }

    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        if (this.f1816a.o >= 0 && !this.f1816a.l) {
            h hVar = this.f1816a;
            hVar.a(i <= hVar.o, false);
        }
        Activity activity = this.f1816a.getActivity();
        if (this.f1816a.a(activity) && ((MainActivity) activity).l() != 0) {
            this.f1816a.f1823d.setDefaultStatShow(true);
        }
        if (i > this.f1816a.n && this.f1816a.m) {
            G.h();
            boolean unused = this.f1816a.m = false;
        }
        int unused2 = this.f1816a.n = i;
        View childAt = this.f1816a.f1820a.getChildAt(this.f1816a.f1820a.getChildCount() - 1);
        if (this.f1816a.p == 0) {
            h hVar2 = this.f1816a;
            int unused3 = hVar2.p = hVar2.f1820a.getHeight();
        }
        if (i2 + i == i3 && childAt != null && childAt.getBottom() == this.f1816a.p) {
            this.f1816a.f1821b.setCanRefresh(true);
        } else {
            this.f1816a.f1821b.setCanRefresh(false);
        }
    }

    public void onScrollStateChanged(AbsListView absListView, int i) {
        Activity activity = this.f1816a.getActivity();
        if (this.f1816a.a(activity) && i == 0) {
            MainActivity mainActivity = (MainActivity) activity;
            boolean m = mainActivity.m();
            if (this.f1816a.f1820a.getLastVisiblePosition() == this.f1816a.f1820a.getCount() - 1 && !m) {
                mainActivity.a(true, true);
            }
        }
    }
}
