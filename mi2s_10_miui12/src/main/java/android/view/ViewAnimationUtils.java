package android.view;

import android.animation.Animator;
import android.animation.RevealAnimator;

public final class ViewAnimationUtils
{
  public static Animator createCircularReveal(View paramView, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
  {
    return new RevealAnimator(paramView, paramInt1, paramInt2, paramFloat1, paramFloat2);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ViewAnimationUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */