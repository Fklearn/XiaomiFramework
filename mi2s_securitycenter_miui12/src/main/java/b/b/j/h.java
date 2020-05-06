package b.b.j;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import com.miui.appmanager.B;
import com.miui.common.card.CardViewAdapter;
import com.miui.common.card.models.BaseCardModel;
import com.miui.common.card.models.BottomPlaceCardModel;
import com.miui.common.customview.OverScrollLayout;
import com.miui.phonemanage.view.PhoneManageListView;
import com.miui.securitycenter.R;
import com.miui.securityscan.MainActivity;
import com.miui.securityscan.cards.b;
import com.miui.securityscan.cards.c;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;

public class h extends Fragment {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public PhoneManageListView f1820a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public OverScrollLayout f1821b;

    /* renamed from: c  reason: collision with root package name */
    private ViewStub f1822c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public CardViewAdapter f1823d;
    private b.b.j.c.a e;
    /* access modifiers changed from: private */
    public List<BaseCardModel> f;
    /* access modifiers changed from: private */
    public b g = new b(this);
    private Object h = new Object();
    /* access modifiers changed from: private */
    public Object i = new Object();
    /* access modifiers changed from: private */
    public boolean j;
    /* access modifiers changed from: private */
    public boolean k;
    /* access modifiers changed from: private */
    public boolean l;
    /* access modifiers changed from: private */
    public boolean m = true;
    /* access modifiers changed from: private */
    public int n = 0;
    /* access modifiers changed from: private */
    public int o = -1;
    /* access modifiers changed from: private */
    public int p;
    /* access modifiers changed from: private */
    public float q;
    /* access modifiers changed from: private */
    public float r;
    private a s;

    private static class a extends ContentObserver {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<h> f1824a;

        public a(h hVar, Handler handler) {
            super(handler);
            this.f1824a = new WeakReference<>(hVar);
        }

        public void onChange(boolean z, Uri uri) {
            h hVar = (h) this.f1824a.get();
            if (hVar != null) {
                hVar.b();
            }
        }
    }

    private void a(View view) {
        this.f1822c = (ViewStub) view.findViewById(R.id.main_refresh_item);
        this.f1822c.setOnInflateListener(new g(this));
        this.f1822c.inflate();
    }

    /* access modifiers changed from: private */
    public boolean a(Activity activity) {
        return activity != null && !activity.isDestroyed();
    }

    private void c() {
        new c(this).start();
    }

    public void a() {
        synchronized (this.h) {
            List<BaseCardModel> list = this.f;
            if (!(list == null || this.f1820a == null || this.f1823d == null)) {
                b(list);
            }
        }
    }

    public void a(BaseCardModel baseCardModel, List<BaseCardModel> list) {
        c.a((List<BaseCardModel>) this.f1823d.getModelList(), baseCardModel);
        if (list != null) {
            this.f1823d.getModelList().removeAll(list);
        }
        this.f1823d.notifyDataSetChanged(false);
    }

    public void a(List<BaseCardModel> list) {
        Activity activity = getActivity();
        if (this.f1823d != null && a(activity)) {
            synchronized (this.h) {
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(list);
                ArrayList<BaseCardModel> c2 = c.c(arrayList);
                if (c2.isEmpty() && this.f == null) {
                    c2 = b.a((Context) activity);
                }
                b((List<BaseCardModel>) c2);
            }
        }
    }

    public void a(boolean z) {
        this.m = z;
    }

    public void a(boolean z, boolean z2) {
        CardViewAdapter cardViewAdapter;
        Activity activity = getActivity();
        if (a(activity)) {
            if ((((MainActivity) activity).l() == 1 || z2) && (cardViewAdapter = this.f1823d) != null && cardViewAdapter.isCanAutoScroll() != z) {
                if (!z) {
                    this.f1823d.resetViewPager();
                }
                this.f1823d.setCanAutoScroll(z);
                for (BaseCardModel next : this.f1823d.getModelList()) {
                    if (next instanceof b.b.j.b.b) {
                        next.setCanAutoScroll(z);
                        if (z) {
                            next.startAutoScroll();
                        } else {
                            next.stopAutoScroll();
                        }
                    }
                }
            }
        }
    }

    public void b() {
        this.f1823d.notifyDataSetChanged(false);
    }

    public void b(List<BaseCardModel> list) {
        for (BaseCardModel next : this.f1823d.getModelList()) {
            if (next instanceof b.b.j.b.b) {
                next.stopAutoScroll();
            }
        }
        int i2 = -1;
        int i3 = 0;
        while (true) {
            if (i3 >= list.size()) {
                break;
            } else if (list.get(i3) instanceof b.b.j.b.b) {
                i2 = i3;
                break;
            } else {
                i3++;
            }
        }
        List<BaseCardModel> a2 = b.a(getContext(), list);
        if (a2 != null && !a2.isEmpty()) {
            list.addAll(i2 + 1, a2);
        }
        if (!Build.IS_INTERNATIONAL_BUILD) {
            list.add(new BottomPlaceCardModel());
        }
        this.f1823d.clear();
        this.f1823d.addAll(list);
        this.f1823d.notifyDataSetChanged(false);
    }

    public void b(boolean z) {
        CardViewAdapter cardViewAdapter = this.f1823d;
        if (cardViewAdapter != null) {
            cardViewAdapter.setDefaultStatShow(z);
            if (z) {
                this.f1823d.notifyDataSetChanged();
            }
        }
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        synchronized (this.i) {
            this.k = true;
            if (this.j) {
                a();
            }
        }
        this.e = new b.b.j.c.a(this);
        this.e.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        b.b.j.d.b.b(true);
        b.b.j.d.b.a(true);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.m_securityscan_phone_manage_fragment, (ViewGroup) null);
        this.f1820a = (PhoneManageListView) inflate.findViewById(R.id.list_view);
        this.f1821b = (OverScrollLayout) inflate.findViewById(R.id.scroll_layout);
        a(inflate);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.superpower_action_bar_height);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.f1820a.getLayoutParams();
        layoutParams.setMargins(0, dimensionPixelSize, 0, 0);
        this.f1820a.setLayoutParams(layoutParams);
        this.f1823d = new CardViewAdapter(getActivity(), (Handler) null, 3);
        this.f1823d.setDefaultStatShow(false);
        this.f1820a.setAdapter(this.f1823d);
        this.f1820a.setOnScrollListener(new d(this));
        this.f1820a.setOnTouchListener(new e(this));
        this.f1820a.getViewTreeObserver().addOnGlobalLayoutListener(new f(this));
        c();
        this.s = new a(this, new Handler(Looper.getMainLooper()));
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().registerContentObserver(B.f3568a, false, this.s);
        }
        return inflate;
    }

    public void onDestroy() {
        super.onDestroy();
        this.e.cancel(true);
        CardViewAdapter cardViewAdapter = this.f1823d;
        if (cardViewAdapter != null) {
            cardViewAdapter.onDestroy();
        }
        this.g.removeCallbacksAndMessages((Object) null);
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().unregisterContentObserver(this.s);
        }
    }

    public void onPause() {
        super.onPause();
        this.l = true;
        a(false, false);
    }

    public void onResume() {
        super.onResume();
        this.l = false;
        a(true, false);
    }
}
