package b.b.c.a;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import java.util.ArrayList;

public class c implements Animation.AnimationListener {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public ArrayList<a> f1597a = new ArrayList<>();
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public int f1598b;

    /* renamed from: c  reason: collision with root package name */
    private Handler f1599c;

    /* renamed from: d  reason: collision with root package name */
    private final Runnable f1600d = new b(this);

    public static class a {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public View f1601a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public Animation f1602b;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public Animation.AnimationListener f1603c;

        public a(View view, Animation animation, Animation.AnimationListener animationListener) {
            this.f1601a = view;
            this.f1602b = animation;
            this.f1603c = animationListener;
        }
    }

    public c(Context context) {
        this.f1599c = new Handler(context.getMainLooper());
    }

    public c a(View view, Animation animation, Animation.AnimationListener animationListener) {
        this.f1597a.add(new a(view, animation, animationListener));
        return this;
    }

    public void a() {
        if (this.f1597a.size() != 0) {
            this.f1598b = 0;
            this.f1599c.post(this.f1600d);
        }
    }

    public void onAnimationEnd(Animation animation) {
        if (this.f1598b < this.f1597a.size()) {
            a aVar = this.f1597a.get(this.f1598b);
            View a2 = aVar.f1601a;
            Animation.AnimationListener c2 = aVar.f1603c;
            if (c2 != null) {
                c2.onAnimationEnd(animation);
            }
            this.f1598b++;
            if (this.f1598b < this.f1597a.size()) {
                this.f1599c.post(this.f1600d);
            }
            a2.setEnabled(true);
        }
    }

    public void onAnimationRepeat(Animation animation) {
        Animation.AnimationListener c2;
        if (this.f1598b < this.f1597a.size() && (c2 = this.f1597a.get(this.f1598b).f1603c) != null) {
            c2.onAnimationRepeat(animation);
        }
    }

    public void onAnimationStart(Animation animation) {
        if (this.f1598b < this.f1597a.size()) {
            a aVar = this.f1597a.get(this.f1598b);
            View a2 = aVar.f1601a;
            Animation.AnimationListener c2 = aVar.f1603c;
            a2.setEnabled(false);
            if (c2 != null) {
                c2.onAnimationStart(animation);
            }
        }
    }
}
