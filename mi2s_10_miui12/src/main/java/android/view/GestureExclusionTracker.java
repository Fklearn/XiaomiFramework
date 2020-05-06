package android.view;

import android.graphics.Rect;
import com.android.internal.util.Preconditions;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class GestureExclusionTracker
{
  private List<Rect> mGestureExclusionRects = Collections.emptyList();
  private List<GestureExclusionViewInfo> mGestureExclusionViewInfos = new ArrayList();
  private boolean mGestureExclusionViewsChanged = false;
  private List<Rect> mRootGestureExclusionRects = Collections.emptyList();
  private boolean mRootGestureExclusionRectsChanged = false;
  
  public List<Rect> computeChangedRects()
  {
    boolean bool1 = this.mRootGestureExclusionRectsChanged;
    Iterator localIterator = this.mGestureExclusionViewInfos.iterator();
    ArrayList localArrayList = new ArrayList(this.mRootGestureExclusionRects);
    label109:
    while (localIterator.hasNext())
    {
      GestureExclusionViewInfo localGestureExclusionViewInfo = (GestureExclusionViewInfo)localIterator.next();
      int i = localGestureExclusionViewInfo.update();
      boolean bool2;
      if (i != 0)
      {
        bool2 = bool1;
        if (i != 1)
        {
          if (i != 2) {
            break label109;
          }
          this.mGestureExclusionViewsChanged = true;
          localIterator.remove();
          break label109;
        }
      }
      else
      {
        bool2 = true;
      }
      localArrayList.addAll(localGestureExclusionViewInfo.mExclusionRects);
      bool1 = bool2;
    }
    if ((bool1) || (this.mGestureExclusionViewsChanged))
    {
      this.mGestureExclusionViewsChanged = false;
      this.mRootGestureExclusionRectsChanged = false;
      if (!this.mGestureExclusionRects.equals(localArrayList))
      {
        this.mGestureExclusionRects = localArrayList;
        return localArrayList;
      }
    }
    return null;
  }
  
  public List<Rect> getRootSystemGestureExclusionRects()
  {
    return this.mRootGestureExclusionRects;
  }
  
  public void setRootSystemGestureExclusionRects(List<Rect> paramList)
  {
    Preconditions.checkNotNull(paramList, "rects must not be null");
    this.mRootGestureExclusionRects = paramList;
    this.mRootGestureExclusionRectsChanged = true;
  }
  
  public void updateRectsForView(View paramView)
  {
    int i = 0;
    Iterator localIterator = this.mGestureExclusionViewInfos.iterator();
    int j;
    for (;;)
    {
      j = i;
      if (!localIterator.hasNext()) {
        break;
      }
      GestureExclusionViewInfo localGestureExclusionViewInfo = (GestureExclusionViewInfo)localIterator.next();
      View localView = localGestureExclusionViewInfo.getView();
      if ((localView != null) && (localView.isAttachedToWindow()))
      {
        if (localView == paramView)
        {
          j = 1;
          localGestureExclusionViewInfo.mDirty = true;
          break;
        }
      }
      else
      {
        this.mGestureExclusionViewsChanged = true;
        localIterator.remove();
      }
    }
    if ((j == 0) && (paramView.isAttachedToWindow()))
    {
      this.mGestureExclusionViewInfos.add(new GestureExclusionViewInfo(paramView));
      this.mGestureExclusionViewsChanged = true;
    }
  }
  
  private static class GestureExclusionViewInfo
  {
    public static final int CHANGED = 0;
    public static final int GONE = 2;
    public static final int UNCHANGED = 1;
    boolean mDirty = true;
    List<Rect> mExclusionRects = Collections.emptyList();
    private final WeakReference<View> mView;
    
    GestureExclusionViewInfo(View paramView)
    {
      this.mView = new WeakReference(paramView);
    }
    
    public View getView()
    {
      return (View)this.mView.get();
    }
    
    public int update()
    {
      View localView = getView();
      if ((localView != null) && (localView.isAttachedToWindow()))
      {
        List localList = localView.getSystemGestureExclusionRects();
        ArrayList localArrayList = new ArrayList(localList.size());
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          Rect localRect = new Rect((Rect)localIterator.next());
          ViewParent localViewParent = localView.getParent();
          if ((localViewParent != null) && (localViewParent.getChildVisibleRect(localView, localRect, null))) {
            localArrayList.add(localRect);
          }
        }
        if (this.mExclusionRects.equals(localList)) {
          return 1;
        }
        this.mExclusionRects = localArrayList;
        return 0;
      }
      return 2;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/GestureExclusionTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */