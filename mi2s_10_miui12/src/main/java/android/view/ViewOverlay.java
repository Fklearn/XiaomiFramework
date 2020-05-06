package android.view;

import android.animation.LayoutTransition;
import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RenderNode;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.Iterator;

public class ViewOverlay
{
  OverlayViewGroup mOverlayViewGroup;
  
  ViewOverlay(Context paramContext, View paramView)
  {
    this.mOverlayViewGroup = new OverlayViewGroup(paramContext, paramView);
  }
  
  public void add(Drawable paramDrawable)
  {
    this.mOverlayViewGroup.add(paramDrawable);
  }
  
  public void clear()
  {
    this.mOverlayViewGroup.clear();
  }
  
  @UnsupportedAppUsage
  ViewGroup getOverlayView()
  {
    return this.mOverlayViewGroup;
  }
  
  @UnsupportedAppUsage
  boolean isEmpty()
  {
    return this.mOverlayViewGroup.isEmpty();
  }
  
  public void remove(Drawable paramDrawable)
  {
    this.mOverlayViewGroup.remove(paramDrawable);
  }
  
  static class OverlayViewGroup
    extends ViewGroup
  {
    ArrayList<Drawable> mDrawables = null;
    final View mHostView;
    
    OverlayViewGroup(Context paramContext, View paramView)
    {
      super();
      this.mHostView = paramView;
      this.mAttachInfo = this.mHostView.mAttachInfo;
      this.mRight = paramView.getWidth();
      this.mBottom = paramView.getHeight();
      this.mRenderNode.setLeftTopRightBottom(0, 0, this.mRight, this.mBottom);
    }
    
    public void add(Drawable paramDrawable)
    {
      if (paramDrawable != null)
      {
        if (this.mDrawables == null) {
          this.mDrawables = new ArrayList();
        }
        if (!this.mDrawables.contains(paramDrawable))
        {
          this.mDrawables.add(paramDrawable);
          invalidate(paramDrawable.getBounds());
          paramDrawable.setCallback(this);
        }
        return;
      }
      throw new IllegalArgumentException("drawable must be non-null");
    }
    
    public void add(View paramView)
    {
      if (paramView != null)
      {
        if ((paramView.getParent() instanceof ViewGroup))
        {
          ViewGroup localViewGroup = (ViewGroup)paramView.getParent();
          if ((localViewGroup != this.mHostView) && (localViewGroup.getParent() != null) && (localViewGroup.mAttachInfo != null))
          {
            int[] arrayOfInt1 = new int[2];
            int[] arrayOfInt2 = new int[2];
            localViewGroup.getLocationOnScreen(arrayOfInt1);
            this.mHostView.getLocationOnScreen(arrayOfInt2);
            paramView.offsetLeftAndRight(arrayOfInt1[0] - arrayOfInt2[0]);
            paramView.offsetTopAndBottom(arrayOfInt1[1] - arrayOfInt2[1]);
          }
          localViewGroup.removeView(paramView);
          if (localViewGroup.getLayoutTransition() != null) {
            localViewGroup.getLayoutTransition().cancel(3);
          }
          if (paramView.getParent() != null) {
            paramView.mParent = null;
          }
        }
        super.addView(paramView);
        return;
      }
      throw new IllegalArgumentException("view must be non-null");
    }
    
    public void clear()
    {
      removeAllViews();
      Object localObject = this.mDrawables;
      if (localObject != null)
      {
        localObject = ((ArrayList)localObject).iterator();
        while (((Iterator)localObject).hasNext()) {
          ((Drawable)((Iterator)localObject).next()).setCallback(null);
        }
        this.mDrawables.clear();
      }
    }
    
    protected void dispatchDraw(Canvas paramCanvas)
    {
      paramCanvas.insertReorderBarrier();
      super.dispatchDraw(paramCanvas);
      paramCanvas.insertInorderBarrier();
      ArrayList localArrayList = this.mDrawables;
      int i;
      if (localArrayList == null) {
        i = 0;
      } else {
        i = localArrayList.size();
      }
      for (int j = 0; j < i; j++) {
        ((Drawable)this.mDrawables.get(j)).draw(paramCanvas);
      }
    }
    
    public void invalidate()
    {
      super.invalidate();
      View localView = this.mHostView;
      if (localView != null) {
        localView.invalidate();
      }
    }
    
    public void invalidate(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.invalidate(paramInt1, paramInt2, paramInt3, paramInt4);
      View localView = this.mHostView;
      if (localView != null) {
        localView.invalidate(paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void invalidate(Rect paramRect)
    {
      super.invalidate(paramRect);
      View localView = this.mHostView;
      if (localView != null) {
        localView.invalidate(paramRect);
      }
    }
    
    public void invalidate(boolean paramBoolean)
    {
      super.invalidate(paramBoolean);
      View localView = this.mHostView;
      if (localView != null) {
        localView.invalidate(paramBoolean);
      }
    }
    
    public ViewParent invalidateChildInParent(int[] paramArrayOfInt, Rect paramRect)
    {
      if (this.mHostView != null)
      {
        paramRect.offset(paramArrayOfInt[0], paramArrayOfInt[1]);
        if ((this.mHostView instanceof ViewGroup))
        {
          paramArrayOfInt[0] = 0;
          paramArrayOfInt[1] = 0;
          super.invalidateChildInParent(paramArrayOfInt, paramRect);
          return ((ViewGroup)this.mHostView).invalidateChildInParent(paramArrayOfInt, paramRect);
        }
        invalidate(paramRect);
      }
      return null;
    }
    
    public void invalidateDrawable(Drawable paramDrawable)
    {
      invalidate(paramDrawable.getBounds());
    }
    
    protected void invalidateParentCaches()
    {
      super.invalidateParentCaches();
      View localView = this.mHostView;
      if (localView != null) {
        localView.invalidateParentCaches();
      }
    }
    
    protected void invalidateParentIfNeeded()
    {
      super.invalidateParentIfNeeded();
      View localView = this.mHostView;
      if (localView != null) {
        localView.invalidateParentIfNeeded();
      }
    }
    
    void invalidateViewProperty(boolean paramBoolean1, boolean paramBoolean2)
    {
      super.invalidateViewProperty(paramBoolean1, paramBoolean2);
      View localView = this.mHostView;
      if (localView != null) {
        localView.invalidateViewProperty(paramBoolean1, paramBoolean2);
      }
    }
    
    boolean isEmpty()
    {
      if (getChildCount() == 0)
      {
        ArrayList localArrayList = this.mDrawables;
        if ((localArrayList == null) || (localArrayList.size() == 0)) {
          return true;
        }
      }
      return false;
    }
    
    public void onDescendantInvalidated(View paramView1, View paramView2)
    {
      View localView = this.mHostView;
      if (localView != null) {
        if ((localView instanceof ViewGroup))
        {
          ((ViewGroup)localView).onDescendantInvalidated(localView, paramView2);
          super.onDescendantInvalidated(paramView1, paramView2);
        }
        else
        {
          invalidate();
        }
      }
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
    
    public void remove(Drawable paramDrawable)
    {
      if (paramDrawable != null)
      {
        ArrayList localArrayList = this.mDrawables;
        if (localArrayList != null)
        {
          localArrayList.remove(paramDrawable);
          invalidate(paramDrawable.getBounds());
          paramDrawable.setCallback(null);
        }
        return;
      }
      throw new IllegalArgumentException("drawable must be non-null");
    }
    
    public void remove(View paramView)
    {
      if (paramView != null)
      {
        super.removeView(paramView);
        return;
      }
      throw new IllegalArgumentException("view must be non-null");
    }
    
    protected boolean verifyDrawable(Drawable paramDrawable)
    {
      if (!super.verifyDrawable(paramDrawable))
      {
        ArrayList localArrayList = this.mDrawables;
        if ((localArrayList == null) || (!localArrayList.contains(paramDrawable))) {
          return false;
        }
      }
      boolean bool = true;
      return bool;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ViewOverlay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */