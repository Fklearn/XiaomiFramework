package b.b.i.b;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import b.b.c.j.A;

public class g {
    public static void a(View view) {
        if (view != null) {
            try {
                view.setBackground((Drawable) null);
            } catch (Exception unused) {
            }
        }
    }

    public static void a(View view, int i) {
        if (view != null) {
            view.setVisibility(i);
        }
    }

    public static void a(View view, View.OnClickListener onClickListener) {
        if (view != null) {
            view.setOnClickListener(onClickListener);
            view.setClickable(true);
        }
    }

    public static void a(View view, boolean z) {
        if (view != null) {
            view.setEnabled(z);
        }
    }

    public static void a(TextView textView, int i) {
        if (textView != null) {
            textView.setText(i);
        }
    }

    public static void a(TextView textView, String str) {
        if (textView != null) {
            textView.setText(str);
        }
    }

    public static boolean a() {
        return A.a();
    }

    public static void b(View view) {
        if (view != null) {
            view.setOnClickListener((View.OnClickListener) null);
            view.setClickable(false);
        }
    }

    public static void b(View view, int i) {
        if (view != null) {
            view.setVisibility(i);
        }
    }

    public static boolean b() {
        return c.b() && d.b();
    }
}
