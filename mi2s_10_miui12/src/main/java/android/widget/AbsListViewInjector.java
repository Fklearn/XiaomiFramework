package android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.Log;
import android.util.TypedValue;
import java.lang.reflect.Field;
import java.util.Objects;
import miui.os.Environment;

public class AbsListViewInjector
{
  private static final int MINIMUM_VELOCITY_IN_DP = 180;
  private static final String TAG = "AbsListViewInjector";
  private static final Field mMinimumVelocity;
  
  static
  {
    Field localField2;
    try
    {
      Field localField1 = AbsListView.class.getDeclaredField("mMinimumVelocity");
      localField1.setAccessible(true);
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      Log.w("AbsListViewInjector", "reflect mMinimumVelocity, skip");
      localField2 = null;
    }
    mMinimumVelocity = localField2;
  }
  
  static void doAnimationFrame(AbsListView paramAbsListView, AbsListView.OverFlingRunnable paramOverFlingRunnable)
  {
    if (paramAbsListView.mTouchMode == 6)
    {
      OverScroller localOverScroller = paramOverFlingRunnable.mScroller;
      if (localOverScroller.computeScrollOffset())
      {
        int i = paramAbsListView.getScrollY();
        int j = localOverScroller.getCurrY();
        int k = j - i;
        boolean bool;
        if (localOverScroller.getFinalY() == 0) {
          bool = true;
        } else {
          bool = false;
        }
        OverScrollLogger.debug("overfling scrollY: %d, currY: %d, springBack: %b", new Object[] { Integer.valueOf(i), Integer.valueOf(j), Boolean.valueOf(bool) });
        int m;
        if (i == 0)
        {
          m = k;
          if (!bool) {}
        }
        else
        {
          if ((!bool) && (Integer.signum(j) * i < 0)) {
            m = -i;
          } else {
            m = k;
          }
          paramAbsListView.overScrollBy(0, m, 0, i, 0, 0, 0, paramAbsListView.mOverflingDistance, false);
        }
        if ((!bool) && (paramAbsListView.getScrollY() == 0))
        {
          OverScrollLogger.debug("scrollY fully consumed, do normal fling");
          k = Integer.signum(m);
          m = (int)localOverScroller.getCurrVelocity();
          localOverScroller.abortAnimation();
          paramOverFlingRunnable.start(k * m);
        }
        else
        {
          paramAbsListView.invalidate();
          paramAbsListView.postOnAnimation(paramOverFlingRunnable);
        }
      }
      else
      {
        OverScrollLogger.debug("overfling finish.");
        paramOverFlingRunnable.endFling();
      }
    }
    else
    {
      paramOverFlingRunnable.superDoAnimationFrame();
    }
  }
  
  private static boolean isSpringOverscrollEnabled(AbsListView paramAbsListView)
  {
    boolean bool;
    if ((paramAbsListView.getOverScrollMode() != 2) && (paramAbsListView.mUsingMiuiTheme)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean needFinishActionMode(AbsListView paramAbsListView)
  {
    boolean bool;
    if ((!Environment.isUsingMiui(paramAbsListView.getContext())) && (paramAbsListView.getCheckedItemCount() == 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  static void onInit(AbsListView paramAbsListView)
  {
    paramAbsListView.mDefaultOverscrollDistance = paramAbsListView.mOverscrollDistance;
    paramAbsListView.mDefaultOverflingDistance = paramAbsListView.mOverflingDistance;
    setMinimumVelocity(paramAbsListView, (int)TypedValue.applyDimension(1, 180.0F, paramAbsListView.getResources().getDisplayMetrics()));
    paramAbsListView.mUsingMiuiTheme = Environment.isUsingMiui(paramAbsListView.getContext());
    if (isSpringOverscrollEnabled(paramAbsListView)) {
      setupSpring(paramAbsListView);
    }
  }
  
  static void onLayout(AbsListView paramAbsListView, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (isSpringOverscrollEnabled(paramAbsListView))
    {
      paramAbsListView.mOverflingDistance = (paramInt4 - paramInt2);
      paramAbsListView.mOverscrollDistance = (paramInt4 - paramInt2);
    }
  }
  
  static boolean overScrollBy(AbsListView paramAbsListView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, boolean paramBoolean)
  {
    if ((paramBoolean) && (isSpringOverscrollEnabled(paramAbsListView)))
    {
      int i = Integer.signum(paramInt4);
      if (Math.abs(paramInt4) >= paramInt8) {
        paramInt4 = i * paramInt8;
      }
      float f = overScrollWeight(paramInt4, paramInt8);
      paramInt2 = (int)(paramInt2 * f);
    }
    return paramAbsListView.superOverScrollBy(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramBoolean);
  }
  
  private static float overScrollWeight(int paramInt1, int paramInt2)
  {
    return (float)-Math.pow(Math.abs(paramInt1 / paramInt2) - 1.0F, 3.0D) / 1.5F;
  }
  
  private static void setMinimumVelocity(AbsListView paramAbsListView, int paramInt)
  {
    Field localField = mMinimumVelocity;
    if (localField != null) {
      try
      {
        localField.set(paramAbsListView, Integer.valueOf(paramInt));
      }
      catch (IllegalAccessException paramAbsListView)
      {
        Log.w("AbsListViewInjector", "set mMinimumVelocity failed");
      }
    } else {
      Log.w("AbsListViewInjector", "no mMinimumVelocity field, skipping");
    }
  }
  
  private static void setupSpring(AbsListView paramAbsListView)
  {
    Objects.requireNonNull(paramAbsListView);
    paramAbsListView.mFlingRunnable = new AbsListView.OverFlingRunnable(paramAbsListView);
    paramAbsListView.mEdgeGlowTop = new DummyEdgeEffect(paramAbsListView.getContext());
    paramAbsListView.mEdgeGlowBottom = new DummyEdgeEffect(paramAbsListView.getContext());
    if ((paramAbsListView.isLaidOut()) && (!paramAbsListView.isLayoutRequested()))
    {
      paramAbsListView.mOverflingDistance = paramAbsListView.getHeight();
      paramAbsListView.mOverscrollDistance = paramAbsListView.getHeight();
    }
  }
  
  static void startOverfling(AbsListView paramAbsListView, AbsListView.OverFlingRunnable paramOverFlingRunnable, int paramInt)
  {
    paramOverFlingRunnable.mScroller.setInterpolator(null);
    int i = paramAbsListView.getScrollY();
    if (i == 0)
    {
      OverScrollLogger.debug("startOverfling: unknown direction, start normal fling with velocity %d", new Object[] { Integer.valueOf(paramInt) });
      paramOverFlingRunnable.start(paramInt);
      return;
    }
    if (Integer.signum(paramInt) * i > 0)
    {
      OverScrollLogger.debug("startOverfling: fling to boundary with velocity %d", new Object[] { Integer.valueOf(paramInt) });
      float f = overScrollWeight(i, paramAbsListView.mOverflingDistance);
      paramOverFlingRunnable.mScroller.fling(0, i, 0, (int)(paramInt * f), 0, 0, 0, 0, 0, paramAbsListView.mOverflingDistance);
    }
    else
    {
      OverScrollLogger.debug("startOverfling: fling to content with velocity %d, current scrollY %d", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(paramAbsListView.getScrollY()) });
      int j;
      int k;
      if (i > 0)
      {
        j = Integer.MIN_VALUE;
        k = 0;
      }
      else
      {
        j = 0;
        k = Integer.MAX_VALUE;
      }
      paramOverFlingRunnable.mScroller.fling(0, i, 0, paramInt, 0, 0, j, k, 0, paramAbsListView.mOverflingDistance);
    }
    paramAbsListView.mTouchMode = 6;
    paramAbsListView.invalidate();
    paramAbsListView.postOnAnimation(paramOverFlingRunnable);
  }
  
  static class DummyEdgeEffect
    extends EdgeEffect
  {
    DummyEdgeEffect(Context paramContext)
    {
      super();
    }
    
    public boolean draw(Canvas paramCanvas)
    {
      return false;
    }
    
    public void onAbsorb(int paramInt) {}
    
    public void onPull(float paramFloat) {}
    
    public void onPull(float paramFloat1, float paramFloat2) {}
    
    public void onRelease() {}
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/AbsListViewInjector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */