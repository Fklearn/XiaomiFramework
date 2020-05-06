package com.miui.appmanager.c;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.miui.applicationlock.c.y;
import com.miui.appmanager.AppManagerMainActivity;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.R;
import com.xiaomi.ad.feedback.IAdFeedbackListener;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class i extends k {

    /* renamed from: c  reason: collision with root package name */
    private List<k> f3640c = new ArrayList();

    /* renamed from: d  reason: collision with root package name */
    private String f3641d;
    private int e = 0;
    private int f = 0;
    private int g = 0;

    public static class a extends l {

        /* renamed from: a  reason: collision with root package name */
        private TextView f3642a;

        /* renamed from: b  reason: collision with root package name */
        private View f3643b;

        public a(View view) {
            super(view);
            this.f3642a = (TextView) view.findViewById(R.id.tv_title);
            this.f3643b = view.findViewById(R.id.close);
        }

        public void a(View view, k kVar, int i) {
            super.a(view, kVar, i);
            i iVar = (i) kVar;
            TextView textView = this.f3642a;
            if (textView != null) {
                textView.setText(iVar.c());
            }
            View view2 = this.f3643b;
            if (view2 != null) {
                view2.setVisibility(iVar.d() ? 0 : 4);
                this.f3643b.setOnClickListener(new h(this, iVar));
            }
        }
    }

    public i(JSONObject jSONObject) {
        super(R.layout.app_manager_card_layout_title);
        this.f3641d = jSONObject.optString("appName");
        if (TextUtils.isEmpty(this.f3641d)) {
            this.f3641d = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
        }
    }

    /* access modifiers changed from: private */
    public void a(AppManagerMainActivity appManagerMainActivity) {
        new Handler(Looper.getMainLooper()).post(new g(this, appManagerMainActivity));
    }

    private void a(AppManagerMainActivity appManagerMainActivity, View view) {
        View inflate = appManagerMainActivity.getLayoutInflater().inflate(R.layout.result_unlike_pop_window, (ViewGroup) null);
        Resources resources = appManagerMainActivity.getResources();
        int i = resources.getDisplayMetrics().widthPixels;
        PopupWindow popupWindow = new PopupWindow(inflate, -2, -2);
        inflate.measure(View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(10, Integer.MIN_VALUE));
        inflate.setOnClickListener(new e(this, popupWindow, appManagerMainActivity));
        int measuredWidth = inflate.getMeasuredWidth();
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        popupWindow.showAtLocation(view, 0, iArr[0] - measuredWidth, iArr[1] - resources.getDimensionPixelOffset(R.dimen.result_popwindow_offset));
    }

    private void b(AppManagerMainActivity appManagerMainActivity, View view) {
        y b2 = y.b();
        f fVar = new f(this, appManagerMainActivity);
        if (b2.a(appManagerMainActivity.getApplicationContext())) {
            b2.a(appManagerMainActivity.getApplicationContext(), (IAdFeedbackListener) fVar, "com.miui.securitycenter", "com.miui.securitycenter_appmanager", ((c) b().get(0)).c());
        }
    }

    public void a(k kVar) {
        c cVar = (c) kVar;
        if (cVar.l()) {
            this.g++;
        } else if (cVar.d() > 0) {
            this.e++;
        } else {
            this.f++;
        }
        this.f3640c.add(kVar);
    }

    public List<k> b() {
        return this.f3640c;
    }

    public String c() {
        return this.f3641d;
    }

    public boolean d() {
        return (this.e > 1 && this.f == 0) || this.g > 1;
    }

    public void onClick(View view) {
        if (view.getId() != R.id.close) {
            return;
        }
        if (this.g > 1) {
            a((AppManagerMainActivity) view.getContext(), view);
        } else {
            b((AppManagerMainActivity) view.getContext(), view);
        }
    }
}
