package com.miui.gamebooster.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DimenRes;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static final String f5265a = "c";

    /* renamed from: b  reason: collision with root package name */
    private Handler f5266b = new Handler(Looper.myLooper());

    /* renamed from: c  reason: collision with root package name */
    private View f5267c;

    /* renamed from: d  reason: collision with root package name */
    private View f5268d;
    private boolean e;
    private boolean f;
    private PopupWindow g;
    private Context h;
    private int i;
    private b j;
    private String k;
    private Runnable l = new a(this);

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        private c f5269a;

        /* renamed from: b  reason: collision with root package name */
        private View f5270b;

        /* renamed from: c  reason: collision with root package name */
        private View f5271c;

        /* renamed from: d  reason: collision with root package name */
        private boolean f5272d;
        private boolean e;
        private int f;
        private b g;
        private String h;

        public a a(int i) {
            this.f = i;
            return this;
        }

        public a a(View view) {
            this.f5271c = view;
            return this;
        }

        public a a(View view, String str) {
            this.f5270b = view;
            this.h = str;
            return this;
        }

        public a a(b bVar) {
            this.g = bVar;
            return this;
        }

        public a a(boolean z, boolean z2) {
            this.f5272d = z;
            this.e = z2;
            return this;
        }

        public void a() {
            this.f5269a = new c(this.f5270b, this.h);
            this.f5269a.a(this.f5271c);
            this.f5269a.a(this.f5272d, this.e);
            this.f5269a.b(this.f);
            this.f5269a.a(this.g);
            this.f5269a.e();
        }
    }

    public interface b {
        void onShow();
    }

    public c(View view, String str) {
        this.f5267c = view;
        this.k = str;
        c();
    }

    private int a(@DimenRes int i2) {
        Context context = this.h;
        if (context != null) {
            return context.getResources().getDimensionPixelOffset(i2);
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public void a() {
        PopupWindow popupWindow = this.g;
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public void a(View view) {
        this.f5268d = view;
    }

    /* access modifiers changed from: private */
    public void a(b bVar) {
        this.j = bVar;
    }

    /* access modifiers changed from: private */
    public void a(boolean z, boolean z2) {
        this.e = z;
        this.f = z2;
    }

    private int b() {
        int i2;
        float f2;
        Paint paint = new Paint();
        paint.setTextSize((float) this.h.getResources().getDimensionPixelOffset(R.dimen.gb_active_view_pop_textsize));
        if (this.k.contains("\n")) {
            String[] split = this.k.split("\n");
            i2 = 0;
            if (split.length > 0) {
                f2 = Math.max(paint.measureText(split[0] + "\n"), paint.measureText(split[1]));
            }
            return Math.max(i2 + this.h.getResources().getDimensionPixelOffset(R.dimen.gb_active_bubble_rightslide_left) + this.h.getResources().getDimensionPixelOffset(R.dimen.gb_active_bubble_rightslide_right) + this.h.getResources().getDimensionPixelOffset(R.dimen.gb_active_view_pop_offeset), this.h.getResources().getDrawable(R.drawable.game_toast_bg_shuping_right).getIntrinsicWidth() + this.h.getResources().getDimensionPixelOffset(R.dimen.gb_active_view_pop_offeset));
        }
        f2 = paint.measureText(this.k);
        i2 = (int) f2;
        return Math.max(i2 + this.h.getResources().getDimensionPixelOffset(R.dimen.gb_active_bubble_rightslide_left) + this.h.getResources().getDimensionPixelOffset(R.dimen.gb_active_bubble_rightslide_right) + this.h.getResources().getDimensionPixelOffset(R.dimen.gb_active_view_pop_offeset), this.h.getResources().getDrawable(R.drawable.game_toast_bg_shuping_right).getIntrinsicWidth() + this.h.getResources().getDimensionPixelOffset(R.dimen.gb_active_view_pop_offeset));
    }

    /* access modifiers changed from: private */
    public void b(int i2) {
        this.i = i2;
    }

    private void c() {
        this.h = Application.d().getApplicationContext();
        View view = this.f5267c;
        if (view == null) {
            Log.e(f5265a, "error init view");
            return;
        }
        if (view instanceof TextView) {
            ((TextView) view).setText(this.k);
        }
        this.g = new PopupWindow(this.f5267c, -2, -2);
        this.g.setOutsideTouchable(true);
        this.g.setBackgroundDrawable(new BitmapDrawable());
    }

    /* access modifiers changed from: private */
    public void d() {
        try {
            int width = this.f5268d.getWidth();
            int height = this.f5268d.getHeight();
            if (this.e) {
                this.g.showAsDropDown(this.f5268d, width / 2, this.h.getResources().getDimensionPixelOffset(R.dimen.gb_active_view_pop_offeset), 0);
                this.f5267c.setBackgroundResource(R.drawable.game_toast_bg_hengping);
                this.f5267c.setPadding(a((int) R.dimen.gb_active_bubble_hor_left_right), a((int) R.dimen.gb_active_bubble_hor_top), a((int) R.dimen.gb_active_bubble_hor_left_right), a((int) R.dimen.gb_active_bubble_hor_bottom));
            } else {
                if (!this.f) {
                    this.g.showAsDropDown(this.f5268d, -b(), (-height) / 2, 0);
                    this.f5267c.setPadding(a((int) R.dimen.gb_active_bubble_rightslide_left), a((int) R.dimen.gb_active_bubble_slide_top_bottom), a((int) R.dimen.gb_active_bubble_rightslide_right), a((int) R.dimen.gb_active_bubble_slide_top_bottom));
                } else {
                    this.f5267c.setPadding(a((int) R.dimen.gb_active_bubble_leftslide_left), a((int) R.dimen.gb_active_bubble_slide_top_bottom), a((int) R.dimen.gb_active_bubble_leftslide_right), a((int) R.dimen.gb_active_bubble_slide_top_bottom));
                    int dimensionPixelOffset = width + this.h.getResources().getDimensionPixelOffset(R.dimen.gb_active_view_pop_offeset);
                    PopupWindow popupWindow = this.g;
                    View view = this.f5268d;
                    if (!this.f) {
                        dimensionPixelOffset = -dimensionPixelOffset;
                    }
                    popupWindow.showAsDropDown(view, dimensionPixelOffset, (-height) / 2, 0);
                }
                this.f5267c.setBackgroundResource(this.f ? R.drawable.game_toast_bg_shuping_left : R.drawable.game_toast_bg_shuping_right);
            }
            if (this.j != null) {
                this.j.onShow();
            }
            this.f5266b.postDelayed(this.l, (long) this.i);
        } catch (Exception e2) {
            Log.e(f5265a, "show pop erro", e2);
        }
    }

    /* access modifiers changed from: private */
    public void e() {
        this.f5266b.postDelayed(new b(this), 50);
    }
}
