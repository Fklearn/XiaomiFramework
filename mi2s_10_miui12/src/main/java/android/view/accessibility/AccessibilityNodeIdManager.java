package android.view.accessibility;

import android.view.View;

public final class AccessibilityNodeIdManager
{
  private static AccessibilityNodeIdManager sIdManager;
  private WeakSparseArray<View> mIdsToViews = new WeakSparseArray();
  
  public static AccessibilityNodeIdManager getInstance()
  {
    try
    {
      if (sIdManager == null)
      {
        localAccessibilityNodeIdManager = new android/view/accessibility/AccessibilityNodeIdManager;
        localAccessibilityNodeIdManager.<init>();
        sIdManager = localAccessibilityNodeIdManager;
      }
      AccessibilityNodeIdManager localAccessibilityNodeIdManager = sIdManager;
      return localAccessibilityNodeIdManager;
    }
    finally {}
  }
  
  public View findView(int paramInt)
  {
    synchronized (this.mIdsToViews)
    {
      View localView = (View)this.mIdsToViews.get(paramInt);
      if ((localView == null) || (!localView.includeForAccessibility())) {
        localView = null;
      }
      return localView;
    }
  }
  
  public void registerViewWithId(View paramView, int paramInt)
  {
    synchronized (this.mIdsToViews)
    {
      this.mIdsToViews.append(paramInt, paramView);
      return;
    }
  }
  
  public void unregisterViewWithId(int paramInt)
  {
    synchronized (this.mIdsToViews)
    {
      this.mIdsToViews.remove(paramInt);
      return;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/accessibility/AccessibilityNodeIdManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */