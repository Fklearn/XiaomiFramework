package com.miui.optimizemanage.d;

import android.animation.ValueAnimator;
import android.view.animation.PathInterpolator;
import java.util.ArrayList;
import java.util.List;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static ValueAnimator f5927a;

    /* renamed from: b  reason: collision with root package name */
    private static ValueAnimator f5928b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public static List<a> f5929c = new ArrayList();
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public static List<b> f5930d = new ArrayList();

    public interface a {
        void a(float f);
    }

    public interface b {
        void a(float f);
    }

    public static void a(float f, float f2) {
        ValueAnimator valueAnimator = f5927a;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            f5927a.cancel();
        }
        PathInterpolator pathInterpolator = new PathInterpolator(0.6f, 0.35f, 0.19f, 1.0f);
        f5927a = ValueAnimator.ofFloat(new float[]{f, f2});
        f5927a.setDuration(400);
        f5927a.setInterpolator(pathInterpolator);
        f5929c.clear();
        f5927a.addUpdateListener(new b());
        f5927a.start();
    }

    public static void a(a aVar) {
        if (aVar != null && !f5929c.contains(aVar)) {
            f5929c.add(aVar);
        }
    }

    public static void a(b bVar) {
        if (bVar != null && !f5930d.contains(bVar)) {
            f5930d.add(bVar);
        }
    }

    public static void c() {
        f5929c.clear();
        ValueAnimator valueAnimator = f5927a;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    public static void d() {
        f5930d.clear();
        ValueAnimator valueAnimator = f5928b;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    public static void e() {
        ValueAnimator valueAnimator = f5928b;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            f5928b.cancel();
        }
        PathInterpolator pathInterpolator = new PathInterpolator(0.6f, 0.35f, 0.19f, 1.0f);
        f5928b = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        f5928b.setDuration(400);
        f5928b.setInterpolator(pathInterpolator);
        f5928b.addUpdateListener(new a());
        f5930d.clear();
        f5928b.start();
    }
}
