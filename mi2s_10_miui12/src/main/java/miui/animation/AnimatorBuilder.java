package miui.animation;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import miui.animation.ViewPropertyAnimator;

public class AnimatorBuilder {
    protected Animator mAnimator;

    public static AnimatorBuilder of(Animator animator) {
        return new AnimatorBuilder(animator);
    }

    public static ViewPropertyAnimator.Builder of(ViewPropertyAnimator animator) {
        return new ViewPropertyAnimator.Builder(animator);
    }

    public AnimatorBuilder(Animator animator) {
        this.mAnimator = animator;
    }

    public AnimatorBuilder setStartDelay(long startDelay) {
        this.mAnimator.setStartDelay(startDelay);
        return this;
    }

    public AnimatorBuilder setDuration(long duration) {
        this.mAnimator.setDuration(duration);
        return this;
    }

    public AnimatorBuilder setInterpolator(TimeInterpolator value) {
        this.mAnimator.setInterpolator(value);
        return this;
    }

    public AnimatorBuilder addListener(Animator.AnimatorListener listener) {
        this.mAnimator.addListener(listener);
        return this;
    }

    public Animator animator() {
        return this.mAnimator;
    }

    public Animator start() {
        this.mAnimator.start();
        return this.mAnimator;
    }
}
