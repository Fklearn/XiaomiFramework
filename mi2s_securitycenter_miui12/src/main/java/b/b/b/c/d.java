package b.b.b.c;

import android.animation.Animator;

class d implements Animator.AnimatorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e f1490a;

    d(e eVar) {
        this.f1490a = eVar;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        if (this.f1490a.f1493c != null) {
            this.f1490a.f1493c.setVisibility(8);
        }
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }
}
