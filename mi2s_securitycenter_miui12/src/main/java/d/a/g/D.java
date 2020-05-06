package d.a.g;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import com.google.android.exoplayer2.offline.DownloadService;
import d.a.m;

public class D {

    /* renamed from: a  reason: collision with root package name */
    public static final b f8758a = new b();

    /* renamed from: b  reason: collision with root package name */
    public static final a f8759b = new a();

    public static class a extends B implements C0576c<View> {
        private a() {
            super("background");
        }

        /* renamed from: a */
        public void setValue(View view, float f) {
        }

        /* renamed from: a */
        public void setIntValue(View view, int i) {
            view.setBackgroundColor(i);
        }

        /* renamed from: b */
        public int getIntValue(View view) {
            Drawable background = view.getBackground();
            if (background instanceof ColorDrawable) {
                return ((ColorDrawable) background).getColor();
            }
            return 0;
        }

        /* renamed from: c */
        public float getValue(View view) {
            return 0.0f;
        }
    }

    public static class b extends B implements C0576c<View> {
        private b() {
            super(DownloadService.KEY_FOREGROUND);
        }

        /* renamed from: a */
        public void setValue(View view, float f) {
        }

        /* renamed from: a */
        public void setIntValue(View view, int i) {
            Drawable foreground;
            view.setTag(m.miuix_animation_tag_foreground_color, Integer.valueOf(i));
            if (Build.VERSION.SDK_INT >= 23 && (foreground = view.getForeground()) != null) {
                foreground.invalidateSelf();
            }
        }

        /* renamed from: b */
        public int getIntValue(View view) {
            Object tag = view.getTag(m.miuix_animation_tag_foreground_color);
            if (tag instanceof Integer) {
                return ((Integer) tag).intValue();
            }
            return 0;
        }

        /* renamed from: c */
        public float getValue(View view) {
            return 0.0f;
        }
    }
}
