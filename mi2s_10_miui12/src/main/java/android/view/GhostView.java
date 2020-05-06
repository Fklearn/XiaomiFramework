package android.view;

import android.annotation.UnsupportedAppUsage;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RecordingCanvas;
import android.graphics.RenderNode;
import android.widget.FrameLayout;
import java.util.ArrayList;

public class GhostView
  extends View
{
  private boolean mBeingMoved;
  private int mReferences;
  private final View mView;
  
  private GhostView(View paramView)
  {
    super(paramView.getContext());
    this.mView = paramView;
    paramView = this.mView;
    paramView.mGhostView = this;
    paramView = (ViewGroup)paramView.getParent();
    this.mView.setTransitionVisibility(4);
    paramView.invalidate();
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28)
  public static GhostView addGhost(View paramView, ViewGroup paramViewGroup)
  {
    return addGhost(paramView, paramViewGroup, null);
  }
  
  @UnsupportedAppUsage
  public static GhostView addGhost(View paramView, ViewGroup paramViewGroup, Matrix paramMatrix)
  {
    if ((paramView.getParent() instanceof ViewGroup))
    {
      ViewGroupOverlay localViewGroupOverlay = paramViewGroup.getOverlay();
      ViewOverlay.OverlayViewGroup localOverlayViewGroup = localViewGroupOverlay.mOverlayViewGroup;
      GhostView localGhostView = paramView.mGhostView;
      int i = 0;
      Object localObject = localGhostView;
      int j = i;
      if (localGhostView != null)
      {
        View localView = (View)localGhostView.getParent();
        ViewGroup localViewGroup = (ViewGroup)localView.getParent();
        localObject = localGhostView;
        j = i;
        if (localViewGroup != localOverlayViewGroup)
        {
          j = localGhostView.mReferences;
          localViewGroup.removeView(localView);
          localObject = null;
        }
      }
      if (localObject == null)
      {
        localObject = paramMatrix;
        if (paramMatrix == null)
        {
          localObject = new Matrix();
          calculateMatrix(paramView, paramViewGroup, (Matrix)localObject);
        }
        paramMatrix = new GhostView(paramView);
        paramMatrix.setMatrix((Matrix)localObject);
        paramView = new FrameLayout(paramView.getContext());
        paramView.setClipChildren(false);
        copySize(paramViewGroup, paramView);
        copySize(paramViewGroup, paramMatrix);
        paramView.addView(paramMatrix);
        paramViewGroup = new ArrayList();
        i = moveGhostViewsToTop(localViewGroupOverlay.mOverlayViewGroup, paramViewGroup);
        insertIntoOverlay(localViewGroupOverlay.mOverlayViewGroup, paramView, paramMatrix, paramViewGroup, i);
        paramMatrix.mReferences = j;
        paramView = paramMatrix;
      }
      for (;;)
      {
        break;
        paramView = (View)localObject;
        if (paramMatrix != null)
        {
          ((GhostView)localObject).setMatrix(paramMatrix);
          paramView = (View)localObject;
        }
      }
      paramView.mReferences += 1;
      return paramView;
    }
    throw new IllegalArgumentException("Ghosted views must be parented by a ViewGroup");
  }
  
  public static void calculateMatrix(View paramView, ViewGroup paramViewGroup, Matrix paramMatrix)
  {
    paramView = (ViewGroup)paramView.getParent();
    paramMatrix.reset();
    paramView.transformMatrixToGlobal(paramMatrix);
    paramMatrix.preTranslate(-paramView.getScrollX(), -paramView.getScrollY());
    paramViewGroup.transformMatrixToLocal(paramMatrix);
  }
  
  private static void copySize(View paramView1, View paramView2)
  {
    paramView2.setLeft(0);
    paramView2.setTop(0);
    paramView2.setRight(paramView1.getWidth());
    paramView2.setBottom(paramView1.getHeight());
  }
  
  public static GhostView getGhost(View paramView)
  {
    return paramView.mGhostView;
  }
  
  private static int getInsertIndex(ViewGroup paramViewGroup, ArrayList<View> paramArrayList1, ArrayList<View> paramArrayList2, int paramInt)
  {
    int i = paramInt;
    paramInt = paramViewGroup.getChildCount() - 1;
    while (i <= paramInt)
    {
      int j = (i + paramInt) / 2;
      getParents(((GhostView)((ViewGroup)paramViewGroup.getChildAt(j)).getChildAt(0)).mView, paramArrayList2);
      if (isOnTop(paramArrayList1, paramArrayList2)) {
        i = j + 1;
      } else {
        paramInt = j - 1;
      }
      paramArrayList2.clear();
    }
    return i;
  }
  
  private static void getParents(View paramView, ArrayList<View> paramArrayList)
  {
    ViewParent localViewParent = paramView.getParent();
    if ((localViewParent != null) && ((localViewParent instanceof ViewGroup))) {
      getParents((View)localViewParent, paramArrayList);
    }
    paramArrayList.add(paramView);
  }
  
  private static void insertIntoOverlay(ViewGroup paramViewGroup1, ViewGroup paramViewGroup2, GhostView paramGhostView, ArrayList<View> paramArrayList, int paramInt)
  {
    if (paramInt == -1)
    {
      paramViewGroup1.addView(paramViewGroup2);
    }
    else
    {
      ArrayList localArrayList = new ArrayList();
      getParents(paramGhostView.mView, localArrayList);
      paramInt = getInsertIndex(paramViewGroup1, localArrayList, paramArrayList, paramInt);
      if ((paramInt >= 0) && (paramInt < paramViewGroup1.getChildCount())) {
        paramViewGroup1.addView(paramViewGroup2, paramInt);
      } else {
        paramViewGroup1.addView(paramViewGroup2);
      }
    }
  }
  
  private static boolean isGhostWrapper(View paramView)
  {
    if ((paramView instanceof FrameLayout))
    {
      paramView = (FrameLayout)paramView;
      if (paramView.getChildCount() == 1) {
        return paramView.getChildAt(0) instanceof GhostView;
      }
    }
    return false;
  }
  
  private static boolean isOnTop(View paramView1, View paramView2)
  {
    ViewGroup localViewGroup = (ViewGroup)paramView1.getParent();
    int i = localViewGroup.getChildCount();
    ArrayList localArrayList = localViewGroup.buildOrderedChildList();
    int j;
    if ((localArrayList == null) && (localViewGroup.isChildrenDrawingOrderEnabled())) {
      j = 1;
    } else {
      j = 0;
    }
    boolean bool1 = true;
    boolean bool2;
    for (int k = 0;; k++)
    {
      bool2 = bool1;
      if (k >= i) {
        break;
      }
      int m;
      if (j != 0) {
        m = localViewGroup.getChildDrawingOrder(i, k);
      } else {
        m = k;
      }
      View localView;
      if (localArrayList == null) {
        localView = localViewGroup.getChildAt(m);
      } else {
        localView = (View)localArrayList.get(m);
      }
      if (localView == paramView1)
      {
        bool2 = false;
        break;
      }
      if (localView == paramView2)
      {
        bool2 = true;
        break;
      }
    }
    if (localArrayList != null) {
      localArrayList.clear();
    }
    return bool2;
  }
  
  private static boolean isOnTop(ArrayList<View> paramArrayList1, ArrayList<View> paramArrayList2)
  {
    if ((!paramArrayList1.isEmpty()) && (!paramArrayList2.isEmpty()))
    {
      boolean bool = false;
      if (paramArrayList1.get(0) == paramArrayList2.get(0))
      {
        int i = Math.min(paramArrayList1.size(), paramArrayList2.size());
        for (int j = 1; j < i; j++)
        {
          View localView1 = (View)paramArrayList1.get(j);
          View localView2 = (View)paramArrayList2.get(j);
          if (localView1 != localView2) {
            return isOnTop(localView1, localView2);
          }
        }
        if (paramArrayList2.size() == i) {
          bool = true;
        }
        return bool;
      }
    }
    return true;
  }
  
  private static int moveGhostViewsToTop(ViewGroup paramViewGroup, ArrayList<View> paramArrayList)
  {
    int i = paramViewGroup.getChildCount();
    if (i == 0) {
      return -1;
    }
    int j;
    if (isGhostWrapper(paramViewGroup.getChildAt(i - 1)))
    {
      j = i - 1;
      i -= 2;
      while ((i >= 0) && (isGhostWrapper(paramViewGroup.getChildAt(i))))
      {
        j = i;
        i--;
      }
      return j;
    }
    i -= 2;
    while (i >= 0)
    {
      Object localObject = paramViewGroup.getChildAt(i);
      if (isGhostWrapper((View)localObject))
      {
        paramArrayList.add(localObject);
        localObject = (GhostView)((ViewGroup)localObject).getChildAt(0);
        ((GhostView)localObject).mBeingMoved = true;
        paramViewGroup.removeViewAt(i);
        ((GhostView)localObject).mBeingMoved = false;
      }
      i--;
    }
    if (paramArrayList.isEmpty())
    {
      i = -1;
    }
    else
    {
      j = paramViewGroup.getChildCount();
      for (i = paramArrayList.size() - 1; i >= 0; i--) {
        paramViewGroup.addView((View)paramArrayList.get(i));
      }
      paramArrayList.clear();
      i = j;
    }
    return i;
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28)
  public static void removeGhost(View paramView)
  {
    paramView = paramView.mGhostView;
    if (paramView != null)
    {
      paramView.mReferences -= 1;
      if (paramView.mReferences == 0)
      {
        paramView = (ViewGroup)paramView.getParent();
        ((ViewGroup)paramView.getParent()).removeView(paramView);
      }
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (!this.mBeingMoved)
    {
      this.mView.setTransitionVisibility(0);
      Object localObject = this.mView;
      ((View)localObject).mGhostView = null;
      localObject = (ViewGroup)((View)localObject).getParent();
      if (localObject != null) {
        ((ViewGroup)localObject).invalidate();
      }
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if ((paramCanvas instanceof RecordingCanvas))
    {
      paramCanvas = (RecordingCanvas)paramCanvas;
      Object localObject = this.mView;
      ((View)localObject).mRecreateDisplayList = true;
      localObject = ((View)localObject).updateDisplayListIfDirty();
      if (((RenderNode)localObject).hasDisplayList())
      {
        paramCanvas.insertReorderBarrier();
        paramCanvas.drawRenderNode((RenderNode)localObject);
        paramCanvas.insertInorderBarrier();
      }
    }
  }
  
  public void setMatrix(Matrix paramMatrix)
  {
    this.mRenderNode.setAnimationMatrix(paramMatrix);
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    if (this.mView.mGhostView == this)
    {
      if (paramInt == 0) {
        paramInt = 4;
      } else {
        paramInt = 0;
      }
      this.mView.setTransitionVisibility(paramInt);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/GhostView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */