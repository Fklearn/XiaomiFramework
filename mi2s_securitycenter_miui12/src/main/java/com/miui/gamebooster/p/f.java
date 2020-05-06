package com.miui.gamebooster.p;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import com.miui.gamebooster.customview.AddedRelativeLayout;
import com.miui.securitycenter.R;

public class f {

    /* renamed from: a  reason: collision with root package name */
    private static f f4718a;

    /* renamed from: b  reason: collision with root package name */
    private Handler f4719b;

    /* renamed from: c  reason: collision with root package name */
    private Context f4720c;

    /* renamed from: d  reason: collision with root package name */
    private WindowManager.LayoutParams f4721d;
    /* access modifiers changed from: private */
    public WindowManager e;
    /* access modifiers changed from: private */
    public AddedRelativeLayout f;
    private Runnable g = new d(this);

    public interface a {
        void a();
    }

    private f(Context context, Handler handler) {
        this.f4719b = handler;
        this.f4720c = context;
        this.f4721d = new WindowManager.LayoutParams();
        this.e = (WindowManager) context.getSystemService("window");
    }

    public static synchronized f a(Context context, Handler handler) {
        f fVar;
        synchronized (f.class) {
            if (f4718a == null) {
                f4718a = new f(context, handler);
            }
            fVar = f4718a;
        }
        return fVar;
    }

    public void a() {
        this.f4719b.removeCallbacks(this.g);
        AddedRelativeLayout addedRelativeLayout = this.f;
        if (addedRelativeLayout != null && addedRelativeLayout.a()) {
            this.e.removeView(this.f);
            this.f = null;
        }
    }

    public void a(String str, a aVar) {
        a(str, aVar, 2520);
    }

    public void a(String str, a aVar, int i) {
        if (this.f == null) {
            WindowManager.LayoutParams layoutParams = this.f4721d;
            layoutParams.type = 2003;
            layoutParams.format = -3;
            layoutParams.flags = 264;
            layoutParams.gravity = 51;
            layoutParams.width = -2;
            layoutParams.height = -2;
            layoutParams.windowAnimations = R.style.gamebox_toast_view_left;
            layoutParams.x = this.f4720c.getResources().getDimensionPixelOffset(R.dimen.view_dimen_40);
            this.f4721d.y = this.f4720c.getResources().getDimensionPixelOffset(R.dimen.view_dimen_40);
            this.f = (AddedRelativeLayout) LayoutInflater.from(this.f4720c).inflate(R.layout.gb_window_toast, (ViewGroup) null);
            if (!TextUtils.isEmpty(str)) {
                ((TextView) this.f.findViewById(R.id.tv_gb_window_text)).setText(str);
            }
            this.f.setOnClickListener(new e(this, aVar));
            try {
                this.e.addView(this.f, this.f4721d);
            } catch (Exception e2) {
                Log.e("GameToastWindowManager", "add error", e2);
            }
            this.f.setAdded(true);
            this.f4719b.postDelayed(this.g, (long) i);
        }
    }

    public void b() {
        a((String) null, (a) null);
    }
}
