package android.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.style.AccessibilityClickableSpan;
import android.text.style.ClickableSpan;
import android.util.LongSparseArray;
import android.util.Slog;
import android.view.accessibility.AccessibilityInteractionClient;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeIdManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.accessibility.AccessibilityRequestPreparer;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.internal.os.SomeArgs;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

@VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
public final class AccessibilityInteractionController
{
  private static final boolean CONSIDER_REQUEST_PREPARERS = false;
  private static final boolean ENFORCE_NODE_TREE_CONSISTENT = false;
  private static final boolean IGNORE_REQUEST_PREPARERS = true;
  private static final String LOG_TAG = "AccessibilityInteractionController";
  private static final long REQUEST_PREPARER_TIMEOUT_MS = 500L;
  private final AccessibilityManager mA11yManager;
  @GuardedBy({"mLock"})
  private int mActiveRequestPreparerId;
  private AddNodeInfosForViewId mAddNodeInfosForViewId;
  private final PrivateHandler mHandler;
  private final Object mLock = new Object();
  @GuardedBy({"mLock"})
  private List<MessageHolder> mMessagesWaitingForRequestPreparer;
  private final long mMyLooperThreadId;
  private final int mMyProcessId;
  @GuardedBy({"mLock"})
  private int mNumActiveRequestPreparers;
  private final AccessibilityNodePrefetcher mPrefetcher;
  private final ArrayList<AccessibilityNodeInfo> mTempAccessibilityNodeInfoList = new ArrayList();
  private final ArrayList<View> mTempArrayList = new ArrayList();
  private final Point mTempPoint = new Point();
  private final Rect mTempRect = new Rect();
  private final Rect mTempRect1 = new Rect();
  private final Rect mTempRect2 = new Rect();
  private final ViewRootImpl mViewRootImpl;
  
  public AccessibilityInteractionController(ViewRootImpl paramViewRootImpl)
  {
    Looper localLooper = paramViewRootImpl.mHandler.getLooper();
    this.mMyLooperThreadId = localLooper.getThread().getId();
    this.mMyProcessId = Process.myPid();
    this.mHandler = new PrivateHandler(localLooper);
    this.mViewRootImpl = paramViewRootImpl;
    this.mPrefetcher = new AccessibilityNodePrefetcher(null);
    this.mA11yManager = ((AccessibilityManager)this.mViewRootImpl.mContext.getSystemService(AccessibilityManager.class));
  }
  
  private void adjustIsVisibleToUserIfNeeded(AccessibilityNodeInfo paramAccessibilityNodeInfo, Region paramRegion)
  {
    if ((paramRegion != null) && (paramAccessibilityNodeInfo != null))
    {
      Rect localRect = this.mTempRect;
      paramAccessibilityNodeInfo.getBoundsInScreen(localRect);
      if ((paramRegion.quickReject(localRect)) && (!shouldBypassAdjustIsVisible())) {
        paramAccessibilityNodeInfo.setVisibleToUser(false);
      }
      return;
    }
  }
  
  private void adjustIsVisibleToUserIfNeeded(List<AccessibilityNodeInfo> paramList, Region paramRegion)
  {
    if ((paramRegion != null) && (paramList != null))
    {
      int i = paramList.size();
      for (int j = 0; j < i; j++) {
        adjustIsVisibleToUserIfNeeded((AccessibilityNodeInfo)paramList.get(j), paramRegion);
      }
      return;
    }
  }
  
  private void applyAppScaleAndMagnificationSpecIfNeeded(AccessibilityNodeInfo paramAccessibilityNodeInfo, MagnificationSpec paramMagnificationSpec)
  {
    if (paramAccessibilityNodeInfo == null) {
      return;
    }
    float f = this.mViewRootImpl.mAttachInfo.mApplicationScale;
    if (!shouldApplyAppScaleAndMagnificationSpec(f, paramMagnificationSpec)) {
      return;
    }
    Object localObject1 = this.mTempRect;
    Rect localRect = this.mTempRect1;
    paramAccessibilityNodeInfo.getBoundsInParent((Rect)localObject1);
    paramAccessibilityNodeInfo.getBoundsInScreen(localRect);
    if (f != 1.0F)
    {
      ((Rect)localObject1).scale(f);
      localRect.scale(f);
    }
    if (paramMagnificationSpec != null)
    {
      ((Rect)localObject1).scale(paramMagnificationSpec.scale);
      localRect.scale(paramMagnificationSpec.scale);
      localRect.offset((int)paramMagnificationSpec.offsetX, (int)paramMagnificationSpec.offsetY);
    }
    paramAccessibilityNodeInfo.setBoundsInParent((Rect)localObject1);
    paramAccessibilityNodeInfo.setBoundsInScreen(localRect);
    int i;
    Object localObject2;
    if (paramAccessibilityNodeInfo.hasExtras())
    {
      localObject1 = paramAccessibilityNodeInfo.getExtras();
      localObject1 = ((Bundle)localObject1).getParcelableArray("android.view.accessibility.extra.DATA_TEXT_CHARACTER_LOCATION_KEY");
      if (localObject1 != null) {
        for (i = 0; i < localObject1.length; i++)
        {
          localObject2 = (RectF)localObject1[i];
          ((RectF)localObject2).scale(f);
          if (paramMagnificationSpec != null)
          {
            ((RectF)localObject2).scale(paramMagnificationSpec.scale);
            ((RectF)localObject2).offset(paramMagnificationSpec.offsetX, paramMagnificationSpec.offsetY);
          }
        }
      }
    }
    if (paramMagnificationSpec != null)
    {
      localObject2 = this.mViewRootImpl.mAttachInfo;
      if (((View.AttachInfo)localObject2).mDisplay == null) {
        return;
      }
      f = ((View.AttachInfo)localObject2).mApplicationScale * paramMagnificationSpec.scale;
      localObject1 = this.mTempRect1;
      ((Rect)localObject1).left = ((int)(((View.AttachInfo)localObject2).mWindowLeft * f + paramMagnificationSpec.offsetX));
      ((Rect)localObject1).top = ((int)(((View.AttachInfo)localObject2).mWindowTop * f + paramMagnificationSpec.offsetY));
      ((Rect)localObject1).right = ((int)(((Rect)localObject1).left + this.mViewRootImpl.mWidth * f));
      ((Rect)localObject1).bottom = ((int)(((Rect)localObject1).top + this.mViewRootImpl.mHeight * f));
      ((View.AttachInfo)localObject2).mDisplay.getRealSize(this.mTempPoint);
      int j = this.mTempPoint.x;
      i = this.mTempPoint.y;
      paramMagnificationSpec = this.mTempRect2;
      paramMagnificationSpec.set(0, 0, j, i);
      if (!((Rect)localObject1).intersect(paramMagnificationSpec)) {
        paramMagnificationSpec.setEmpty();
      }
      if (!((Rect)localObject1).intersects(localRect.left, localRect.top, localRect.right, localRect.bottom)) {
        paramAccessibilityNodeInfo.setVisibleToUser(false);
      }
    }
  }
  
  private void applyAppScaleAndMagnificationSpecIfNeeded(List<AccessibilityNodeInfo> paramList, MagnificationSpec paramMagnificationSpec)
  {
    if (paramList == null) {
      return;
    }
    if (shouldApplyAppScaleAndMagnificationSpec(this.mViewRootImpl.mAttachInfo.mApplicationScale, paramMagnificationSpec))
    {
      int i = paramList.size();
      for (int j = 0; j < i; j++) {
        applyAppScaleAndMagnificationSpecIfNeeded((AccessibilityNodeInfo)paramList.get(j), paramMagnificationSpec);
      }
    }
  }
  
  private void clearAccessibilityFocusUiThread()
  {
    if ((this.mViewRootImpl.mView != null) && (this.mViewRootImpl.mAttachInfo != null)) {
      try
      {
        this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = 8;
        Object localObject1 = this.mViewRootImpl.mView;
        if ((localObject1 != null) && (isShown((View)localObject1)))
        {
          View localView = this.mViewRootImpl.mAccessibilityFocusedHost;
          if ((localView != null) && (ViewRootImpl.isViewDescendantOf(localView, (View)localObject1)))
          {
            AccessibilityNodeProvider localAccessibilityNodeProvider = localView.getAccessibilityNodeProvider();
            localObject1 = this.mViewRootImpl.mAccessibilityFocusedVirtualView;
            if ((localAccessibilityNodeProvider != null) && (localObject1 != null)) {
              localAccessibilityNodeProvider.performAction(AccessibilityNodeInfo.getVirtualDescendantId(((AccessibilityNodeInfo)localObject1).getSourceNodeId()), AccessibilityNodeInfo.AccessibilityAction.ACTION_CLEAR_ACCESSIBILITY_FOCUS.getId(), null);
            } else {
              localView.performAccessibilityAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLEAR_ACCESSIBILITY_FOCUS.getId(), null);
            }
          }
          else
          {
            return;
          }
        }
        return;
      }
      finally
      {
        this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = 0;
      }
    }
  }
  
  /* Error */
  private void findAccessibilityNodeInfoByAccessibilityIdUiThread(Message paramMessage)
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 424	android/os/Message:arg1	I
    //   4: istore_2
    //   5: aload_1
    //   6: getfield 427	android/os/Message:obj	Ljava/lang/Object;
    //   9: checkcast 429	com/android/internal/os/SomeArgs
    //   12: astore_3
    //   13: aload_3
    //   14: getfield 432	com/android/internal/os/SomeArgs:argi1	I
    //   17: istore 4
    //   19: aload_3
    //   20: getfield 435	com/android/internal/os/SomeArgs:argi2	I
    //   23: istore 5
    //   25: aload_3
    //   26: getfield 438	com/android/internal/os/SomeArgs:argi3	I
    //   29: istore 6
    //   31: aload_3
    //   32: getfield 440	com/android/internal/os/SomeArgs:arg1	Ljava/lang/Object;
    //   35: checkcast 442	android/view/accessibility/IAccessibilityInteractionConnectionCallback
    //   38: astore 7
    //   40: aload_3
    //   41: getfield 445	com/android/internal/os/SomeArgs:arg2	Ljava/lang/Object;
    //   44: checkcast 273	android/view/MagnificationSpec
    //   47: astore 8
    //   49: aload_3
    //   50: getfield 448	com/android/internal/os/SomeArgs:arg3	Ljava/lang/Object;
    //   53: checkcast 222	android/graphics/Region
    //   56: astore 9
    //   58: aload_3
    //   59: getfield 451	com/android/internal/os/SomeArgs:arg4	Ljava/lang/Object;
    //   62: checkcast 302	android/os/Bundle
    //   65: astore_1
    //   66: aload_3
    //   67: invokevirtual 454	com/android/internal/os/SomeArgs:recycle	()V
    //   70: aload_0
    //   71: getfield 79	android/view/AccessibilityInteractionController:mTempAccessibilityNodeInfoList	Ljava/util/ArrayList;
    //   74: astore_3
    //   75: aload_3
    //   76: invokeinterface 457 1 0
    //   81: aload_0
    //   82: getfield 137	android/view/AccessibilityInteractionController:mViewRootImpl	Landroid/view/ViewRootImpl;
    //   85: getfield 374	android/view/ViewRootImpl:mView	Landroid/view/View;
    //   88: ifnull +88 -> 176
    //   91: aload_0
    //   92: getfield 137	android/view/AccessibilityInteractionController:mViewRootImpl	Landroid/view/ViewRootImpl;
    //   95: getfield 254	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   98: ifnonnull +6 -> 104
    //   101: goto +75 -> 176
    //   104: aload_0
    //   105: getfield 137	android/view/AccessibilityInteractionController:mViewRootImpl	Landroid/view/ViewRootImpl;
    //   108: getfield 254	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   111: iload_2
    //   112: putfield 377	android/view/View$AttachInfo:mAccessibilityFetchFlags	I
    //   115: aload_0
    //   116: iload 4
    //   118: invokespecial 461	android/view/AccessibilityInteractionController:findViewByAccessibilityId	(I)Landroid/view/View;
    //   121: astore 10
    //   123: aload 10
    //   125: ifnull +37 -> 162
    //   128: aload_0
    //   129: aload 10
    //   131: invokespecial 188	android/view/AccessibilityInteractionController:isShown	(Landroid/view/View;)Z
    //   134: ifeq +28 -> 162
    //   137: aload_0
    //   138: getfield 142	android/view/AccessibilityInteractionController:mPrefetcher	Landroid/view/AccessibilityInteractionController$AccessibilityNodePrefetcher;
    //   141: astore 11
    //   143: aload 11
    //   145: aload 10
    //   147: iload 5
    //   149: iload_2
    //   150: aload_3
    //   151: aload_1
    //   152: invokevirtual 465	android/view/AccessibilityInteractionController$AccessibilityNodePrefetcher:prefetchAccessibilityNodeInfos	(Landroid/view/View;IILjava/util/List;Landroid/os/Bundle;)V
    //   155: goto +7 -> 162
    //   158: astore_1
    //   159: goto +32 -> 191
    //   162: aload_0
    //   163: aload_3
    //   164: aload 7
    //   166: iload 6
    //   168: aload 8
    //   170: aload 9
    //   172: invokespecial 469	android/view/AccessibilityInteractionController:updateInfosForViewportAndReturnFindNodeResult	(Ljava/util/List;Landroid/view/accessibility/IAccessibilityInteractionConnectionCallback;ILandroid/view/MagnificationSpec;Landroid/graphics/Region;)V
    //   175: return
    //   176: aload_0
    //   177: aload_3
    //   178: aload 7
    //   180: iload 6
    //   182: aload 8
    //   184: aload 9
    //   186: invokespecial 469	android/view/AccessibilityInteractionController:updateInfosForViewportAndReturnFindNodeResult	(Ljava/util/List;Landroid/view/accessibility/IAccessibilityInteractionConnectionCallback;ILandroid/view/MagnificationSpec;Landroid/graphics/Region;)V
    //   189: return
    //   190: astore_1
    //   191: aload_0
    //   192: aload_3
    //   193: aload 7
    //   195: iload 6
    //   197: aload 8
    //   199: aload 9
    //   201: invokespecial 469	android/view/AccessibilityInteractionController:updateInfosForViewportAndReturnFindNodeResult	(Ljava/util/List;Landroid/view/accessibility/IAccessibilityInteractionConnectionCallback;ILandroid/view/MagnificationSpec;Landroid/graphics/Region;)V
    //   204: aload_1
    //   205: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	206	0	this	AccessibilityInteractionController
    //   0	206	1	paramMessage	Message
    //   4	146	2	i	int
    //   12	181	3	localObject	Object
    //   17	100	4	j	int
    //   23	125	5	k	int
    //   29	167	6	m	int
    //   38	156	7	localIAccessibilityInteractionConnectionCallback	IAccessibilityInteractionConnectionCallback
    //   47	151	8	localMagnificationSpec	MagnificationSpec
    //   56	144	9	localRegion	Region
    //   121	25	10	localView	View
    //   141	3	11	localAccessibilityNodePrefetcher	AccessibilityNodePrefetcher
    // Exception table:
    //   from	to	target	type
    //   143	155	158	finally
    //   81	101	190	finally
    //   104	123	190	finally
    //   128	143	190	finally
  }
  
  private void findAccessibilityNodeInfosByTextUiThread(Message paramMessage)
  {
    int i = paramMessage.arg1;
    paramMessage = (SomeArgs)paramMessage.obj;
    String str = (String)paramMessage.arg1;
    IAccessibilityInteractionConnectionCallback localIAccessibilityInteractionConnectionCallback = (IAccessibilityInteractionConnectionCallback)paramMessage.arg2;
    MagnificationSpec localMagnificationSpec = (MagnificationSpec)paramMessage.arg3;
    int j = paramMessage.argi1;
    int k = paramMessage.argi2;
    int m = paramMessage.argi3;
    Region localRegion = (Region)paramMessage.arg4;
    paramMessage.recycle();
    Object localObject1 = null;
    paramMessage = null;
    try
    {
      if ((this.mViewRootImpl.mView != null) && (this.mViewRootImpl.mAttachInfo != null))
      {
        this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = i;
        Object localObject4 = findViewByAccessibilityId(j);
        if (localObject4 != null) {
          try
          {
            if (isShown((View)localObject4))
            {
              Object localObject5 = ((View)localObject4).getAccessibilityNodeProvider();
              if (localObject5 != null)
              {
                paramMessage = ((AccessibilityNodeProvider)localObject5).findAccessibilityNodeInfosByText(str, k);
              }
              else if (k == -1)
              {
                localObject5 = this.mTempArrayList;
                ((ArrayList)localObject5).clear();
                ((View)localObject4).findViewsWithText((ArrayList)localObject5, str, 7);
                if (!((ArrayList)localObject5).isEmpty())
                {
                  paramMessage = this.mTempAccessibilityNodeInfoList;
                  try
                  {
                    paramMessage.clear();
                    i = ((ArrayList)localObject5).size();
                    k = 0;
                    localObject1 = localObject5;
                    if (k < i)
                    {
                      View localView = (View)((ArrayList)localObject1).get(k);
                      if (isShown(localView))
                      {
                        localObject5 = localView.getAccessibilityNodeProvider();
                        if (localObject5 != null)
                        {
                          localObject5 = ((AccessibilityNodeProvider)localObject5).findAccessibilityNodeInfosByText(str, -1);
                          if (localObject5 != null) {
                            paramMessage.addAll((Collection)localObject5);
                          }
                        }
                        else
                        {
                          paramMessage.add(localView.createAccessibilityNodeInfo());
                        }
                      }
                      k++;
                    }
                  }
                  finally
                  {
                    localObject4 = paramMessage;
                    paramMessage = (Message)localObject2;
                    localObject3 = localObject4;
                  }
                }
              }
            }
          }
          finally
          {
            break label357;
          }
        }
        updateInfosForViewportAndReturnFindNodeResult(paramMessage, localIAccessibilityInteractionConnectionCallback, m, localMagnificationSpec, localRegion);
        return;
      }
      updateInfosForViewportAndReturnFindNodeResult(null, localIAccessibilityInteractionConnectionCallback, m, localMagnificationSpec, localRegion);
      return;
    }
    finally
    {
      Object localObject3;
      label357:
      updateInfosForViewportAndReturnFindNodeResult((List)localObject3, localIAccessibilityInteractionConnectionCallback, m, localMagnificationSpec, localRegion);
    }
  }
  
  private void findAccessibilityNodeInfosByViewIdUiThread(Message paramMessage)
  {
    int i = paramMessage.arg1;
    int j = paramMessage.arg2;
    paramMessage = (SomeArgs)paramMessage.obj;
    int k = paramMessage.argi1;
    IAccessibilityInteractionConnectionCallback localIAccessibilityInteractionConnectionCallback = (IAccessibilityInteractionConnectionCallback)paramMessage.arg1;
    MagnificationSpec localMagnificationSpec = (MagnificationSpec)paramMessage.arg2;
    Object localObject = (String)paramMessage.arg3;
    Region localRegion = (Region)paramMessage.arg4;
    paramMessage.recycle();
    ArrayList localArrayList = this.mTempAccessibilityNodeInfoList;
    localArrayList.clear();
    try
    {
      if ((this.mViewRootImpl.mView != null) && (this.mViewRootImpl.mAttachInfo != null))
      {
        this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = i;
        paramMessage = findViewByAccessibilityId(j);
        if (paramMessage != null)
        {
          j = paramMessage.getContext().getResources().getIdentifier((String)localObject, null, null);
          if (j <= 0) {
            return;
          }
          try
          {
            if (this.mAddNodeInfosForViewId == null)
            {
              localObject = new android/view/AccessibilityInteractionController$AddNodeInfosForViewId;
              ((AddNodeInfosForViewId)localObject).<init>(this, null);
              this.mAddNodeInfosForViewId = ((AddNodeInfosForViewId)localObject);
            }
            this.mAddNodeInfosForViewId.init(j, localArrayList);
            paramMessage.findViewByPredicate(this.mAddNodeInfosForViewId);
            this.mAddNodeInfosForViewId.reset();
          }
          finally
          {
            break label244;
          }
        }
        return;
      }
      return;
    }
    finally
    {
      label244:
      updateInfosForViewportAndReturnFindNodeResult(localArrayList, localIAccessibilityInteractionConnectionCallback, k, localMagnificationSpec, localRegion);
    }
  }
  
  private void findFocusUiThread(Message paramMessage)
  {
    int i = paramMessage.arg1;
    int j = paramMessage.arg2;
    paramMessage = (SomeArgs)paramMessage.obj;
    int k = paramMessage.argi1;
    int m = paramMessage.argi2;
    int n = paramMessage.argi3;
    IAccessibilityInteractionConnectionCallback localIAccessibilityInteractionConnectionCallback = (IAccessibilityInteractionConnectionCallback)paramMessage.arg1;
    MagnificationSpec localMagnificationSpec = (MagnificationSpec)paramMessage.arg2;
    Region localRegion = (Region)paramMessage.arg3;
    paramMessage.recycle();
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = null;
    View localView1 = null;
    Object localObject4 = localObject3;
    try
    {
      if (this.mViewRootImpl.mView != null)
      {
        localObject4 = localObject3;
        if (this.mViewRootImpl.mAttachInfo != null)
        {
          localObject4 = localObject3;
          this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = i;
          localObject4 = localObject3;
          View localView2 = findViewByAccessibilityId(m);
          paramMessage = (Message)localObject2;
          if (localView2 != null)
          {
            paramMessage = (Message)localObject2;
            localObject4 = localObject3;
            if (isShown(localView2)) {
              if (j != 1)
              {
                if (j == 2)
                {
                  localObject4 = localObject3;
                  localObject1 = this.mViewRootImpl.mAccessibilityFocusedHost;
                  paramMessage = (Message)localObject2;
                  if (localObject1 != null)
                  {
                    localObject4 = localObject3;
                    if (!ViewRootImpl.isViewDescendantOf((View)localObject1, localView2))
                    {
                      paramMessage = (Message)localObject2;
                    }
                    else
                    {
                      localObject4 = localObject3;
                      if (!isShown((View)localObject1))
                      {
                        paramMessage = (Message)localObject2;
                      }
                      else
                      {
                        localObject4 = localObject3;
                        if (((View)localObject1).getAccessibilityNodeProvider() != null)
                        {
                          paramMessage = localView1;
                          localObject4 = localObject3;
                          if (this.mViewRootImpl.mAccessibilityFocusedVirtualView != null)
                          {
                            localObject4 = localObject3;
                            paramMessage = AccessibilityNodeInfo.obtain(this.mViewRootImpl.mAccessibilityFocusedVirtualView);
                          }
                        }
                        else
                        {
                          paramMessage = localView1;
                          if (n == -1)
                          {
                            localObject4 = localObject3;
                            paramMessage = ((View)localObject1).createAccessibilityNodeInfo();
                          }
                        }
                      }
                    }
                  }
                }
                else
                {
                  localObject4 = localObject3;
                  paramMessage = new java/lang/IllegalArgumentException;
                  localObject4 = localObject3;
                  localObject1 = new java/lang/StringBuilder;
                  localObject4 = localObject3;
                  ((StringBuilder)localObject1).<init>();
                  localObject4 = localObject3;
                  ((StringBuilder)localObject1).append("Unknown focus type: ");
                  localObject4 = localObject3;
                  ((StringBuilder)localObject1).append(j);
                  localObject4 = localObject3;
                  paramMessage.<init>(((StringBuilder)localObject1).toString());
                  localObject4 = localObject3;
                  throw paramMessage;
                }
              }
              else
              {
                localObject4 = localObject3;
                localView1 = localView2.findFocus();
                localObject4 = localObject3;
                if (!isShown(localView1))
                {
                  paramMessage = (Message)localObject2;
                }
                else
                {
                  localObject4 = localObject3;
                  paramMessage = localView1.getAccessibilityNodeProvider();
                  localObject4 = localObject1;
                  if (paramMessage != null)
                  {
                    localObject4 = localObject3;
                    paramMessage = paramMessage.findFocus(j);
                    localObject4 = paramMessage;
                  }
                  paramMessage = (Message)localObject4;
                  if (localObject4 == null) {
                    paramMessage = localView1.createAccessibilityNodeInfo();
                  }
                }
              }
            }
          }
          updateInfoForViewportAndReturnFindNodeResult(paramMessage, localIAccessibilityInteractionConnectionCallback, k, localMagnificationSpec, localRegion);
          return;
        }
      }
      updateInfoForViewportAndReturnFindNodeResult(null, localIAccessibilityInteractionConnectionCallback, k, localMagnificationSpec, localRegion);
      return;
    }
    finally
    {
      updateInfoForViewportAndReturnFindNodeResult((AccessibilityNodeInfo)localObject4, localIAccessibilityInteractionConnectionCallback, k, localMagnificationSpec, localRegion);
    }
  }
  
  private View findViewByAccessibilityId(int paramInt)
  {
    if (paramInt == 2147483646) {
      return this.mViewRootImpl.mView;
    }
    return AccessibilityNodeIdManager.getInstance().findView(paramInt);
  }
  
  private void focusSearchUiThread(Message paramMessage)
  {
    int i = paramMessage.arg1;
    int j = paramMessage.arg2;
    paramMessage = (SomeArgs)paramMessage.obj;
    int k = paramMessage.argi2;
    int m = paramMessage.argi3;
    IAccessibilityInteractionConnectionCallback localIAccessibilityInteractionConnectionCallback = (IAccessibilityInteractionConnectionCallback)paramMessage.arg1;
    MagnificationSpec localMagnificationSpec = (MagnificationSpec)paramMessage.arg2;
    Region localRegion = (Region)paramMessage.arg3;
    paramMessage.recycle();
    Object localObject = null;
    try
    {
      if ((this.mViewRootImpl.mView != null) && (this.mViewRootImpl.mAttachInfo != null))
      {
        this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = i;
        View localView = findViewByAccessibilityId(j);
        paramMessage = (Message)localObject;
        if (localView != null)
        {
          paramMessage = (Message)localObject;
          if (isShown(localView))
          {
            localView = localView.focusSearch(k);
            paramMessage = (Message)localObject;
            if (localView != null) {
              paramMessage = localView.createAccessibilityNodeInfo();
            }
          }
        }
        updateInfoForViewportAndReturnFindNodeResult(paramMessage, localIAccessibilityInteractionConnectionCallback, m, localMagnificationSpec, localRegion);
        return;
      }
      return;
    }
    finally
    {
      updateInfoForViewportAndReturnFindNodeResult(null, localIAccessibilityInteractionConnectionCallback, m, localMagnificationSpec, localRegion);
    }
  }
  
  private boolean handleClickableSpanActionUiThread(View paramView, int paramInt, Bundle paramBundle)
  {
    Parcelable localParcelable = paramBundle.getParcelable("android.view.accessibility.action.ACTION_ARGUMENT_ACCESSIBLE_CLICKABLE_SPAN");
    if (!(localParcelable instanceof AccessibilityClickableSpan)) {
      return false;
    }
    paramBundle = null;
    AccessibilityNodeProvider localAccessibilityNodeProvider = paramView.getAccessibilityNodeProvider();
    if (localAccessibilityNodeProvider != null) {
      paramBundle = localAccessibilityNodeProvider.createAccessibilityNodeInfo(paramInt);
    } else if (paramInt == -1) {
      paramBundle = paramView.createAccessibilityNodeInfo();
    }
    if (paramBundle == null) {
      return false;
    }
    paramBundle = ((AccessibilityClickableSpan)localParcelable).findClickableSpan(paramBundle.getOriginalText());
    if (paramBundle != null)
    {
      paramBundle.onClick(paramView);
      return true;
    }
    return false;
  }
  
  private boolean holdOffMessageIfNeeded(Message paramMessage, int paramInt, long paramLong)
  {
    synchronized (this.mLock)
    {
      if (this.mNumActiveRequestPreparers != 0)
      {
        queueMessageToHandleOncePrepared(paramMessage, paramInt, paramLong);
        return true;
      }
      if (paramMessage.what != 2) {
        return false;
      }
      SomeArgs localSomeArgs1 = (SomeArgs)paramMessage.obj;
      Bundle localBundle = (Bundle)localSomeArgs1.arg4;
      if (localBundle == null) {
        return false;
      }
      int i = localSomeArgs1.argi1;
      List localList = this.mA11yManager.getRequestPreparersForAccessibilityId(i);
      if (localList == null) {
        return false;
      }
      String str = localBundle.getString("android.view.accessibility.AccessibilityNodeInfo.extra_data_requested");
      if (str == null) {
        return false;
      }
      this.mNumActiveRequestPreparers = localList.size();
      for (i = 0; i < localList.size(); i++)
      {
        Message localMessage1 = this.mHandler.obtainMessage(7);
        SomeArgs localSomeArgs2 = SomeArgs.obtain();
        if (localSomeArgs1.argi2 == Integer.MAX_VALUE) {
          j = -1;
        } else {
          j = localSomeArgs1.argi2;
        }
        localSomeArgs2.argi1 = j;
        localSomeArgs2.arg1 = localList.get(i);
        localSomeArgs2.arg2 = str;
        localSomeArgs2.arg3 = localBundle;
        Message localMessage2 = this.mHandler.obtainMessage(8);
        int j = this.mActiveRequestPreparerId + 1;
        this.mActiveRequestPreparerId = j;
        localMessage2.arg1 = j;
        localSomeArgs2.arg4 = localMessage2;
        localMessage1.obj = localSomeArgs2;
        scheduleMessage(localMessage1, paramInt, paramLong, true);
        this.mHandler.obtainMessage(9);
        this.mHandler.sendEmptyMessageDelayed(9, 500L);
      }
      queueMessageToHandleOncePrepared(paramMessage, paramInt, paramLong);
      return true;
    }
  }
  
  private boolean isShown(View paramView)
  {
    boolean bool;
    if ((paramView != null) && (paramView.getWindowVisibility() == 0) && (paramView.isShown())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private void notifyOutsideTouchUiThread()
  {
    if ((this.mViewRootImpl.mView != null) && (this.mViewRootImpl.mAttachInfo != null) && (!this.mViewRootImpl.mStopped) && (!this.mViewRootImpl.mPausedForTransition))
    {
      Object localObject = this.mViewRootImpl.mView;
      if ((localObject != null) && (isShown((View)localObject)))
      {
        long l = SystemClock.uptimeMillis();
        localObject = MotionEvent.obtain(l, l, 4, 0.0F, 0.0F, 0);
        ((MotionEvent)localObject).setSource(4098);
        this.mViewRootImpl.dispatchInputEvent((InputEvent)localObject);
      }
      return;
    }
  }
  
  private void performAccessibilityActionUiThread(Message paramMessage)
  {
    int i = paramMessage.arg1;
    int j = paramMessage.arg2;
    Object localObject1 = (SomeArgs)paramMessage.obj;
    int k = ((SomeArgs)localObject1).argi1;
    int m = ((SomeArgs)localObject1).argi2;
    int n = ((SomeArgs)localObject1).argi3;
    paramMessage = (IAccessibilityInteractionConnectionCallback)((SomeArgs)localObject1).arg1;
    Bundle localBundle = (Bundle)((SomeArgs)localObject1).arg2;
    ((SomeArgs)localObject1).recycle();
    boolean bool1 = false;
    try
    {
      if ((this.mViewRootImpl.mView != null) && (this.mViewRootImpl.mAttachInfo != null) && (!this.mViewRootImpl.mStopped) && (!this.mViewRootImpl.mPausedForTransition))
      {
        this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = i;
        localObject1 = findViewByAccessibilityId(j);
        boolean bool2 = bool1;
        if (localObject1 != null)
        {
          bool2 = bool1;
          if (isShown((View)localObject1)) {
            if (m == 16908660)
            {
              bool2 = handleClickableSpanActionUiThread((View)localObject1, k, localBundle);
            }
            else
            {
              AccessibilityNodeProvider localAccessibilityNodeProvider = ((View)localObject1).getAccessibilityNodeProvider();
              if (localAccessibilityNodeProvider != null)
              {
                bool2 = localAccessibilityNodeProvider.performAction(k, m, localBundle);
              }
              else
              {
                bool2 = bool1;
                if (k == -1) {
                  bool2 = ((View)localObject1).performAccessibilityAction(m, localBundle);
                }
              }
            }
          }
        }
        try
        {
          this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = 0;
          paramMessage.setPerformAccessibilityActionResult(bool2, n);
        }
        catch (RemoteException paramMessage) {}
        return;
      }
      return;
    }
    finally
    {
      try
      {
        this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = 0;
        paramMessage.setPerformAccessibilityActionResult(false, n);
      }
      catch (RemoteException paramMessage) {}
    }
  }
  
  private void prepareForExtraDataRequestUiThread(Message paramMessage)
  {
    paramMessage = (SomeArgs)paramMessage.obj;
    int i = paramMessage.argi1;
    ((AccessibilityRequestPreparer)paramMessage.arg1).onPrepareExtraData(i, (String)paramMessage.arg2, (Bundle)paramMessage.arg3, (Message)paramMessage.arg4);
  }
  
  private void queueMessageToHandleOncePrepared(Message paramMessage, int paramInt, long paramLong)
  {
    if (this.mMessagesWaitingForRequestPreparer == null) {
      this.mMessagesWaitingForRequestPreparer = new ArrayList(1);
    }
    paramMessage = new MessageHolder(paramMessage, paramInt, paramLong);
    this.mMessagesWaitingForRequestPreparer.add(paramMessage);
  }
  
  private void recycleMagnificationSpecAndRegionIfNeeded(MagnificationSpec paramMagnificationSpec, Region paramRegion)
  {
    if (Process.myPid() != Binder.getCallingPid())
    {
      if (paramMagnificationSpec != null) {
        paramMagnificationSpec.recycle();
      }
    }
    else if (paramRegion != null) {
      paramRegion.recycle();
    }
  }
  
  private void requestPreparerDoneUiThread(Message paramMessage)
  {
    synchronized (this.mLock)
    {
      if (paramMessage.arg1 != this.mActiveRequestPreparerId)
      {
        Slog.e("AccessibilityInteractionController", "Surprising AccessibilityRequestPreparer callback (likely late)");
        return;
      }
      this.mNumActiveRequestPreparers -= 1;
      if (this.mNumActiveRequestPreparers <= 0)
      {
        this.mHandler.removeMessages(9);
        scheduleAllMessagesWaitingForRequestPreparerLocked();
      }
      return;
    }
  }
  
  private void requestPreparerTimeoutUiThread()
  {
    synchronized (this.mLock)
    {
      Slog.e("AccessibilityInteractionController", "AccessibilityRequestPreparer timed out");
      scheduleAllMessagesWaitingForRequestPreparerLocked();
      return;
    }
  }
  
  @GuardedBy({"mLock"})
  private void scheduleAllMessagesWaitingForRequestPreparerLocked()
  {
    int i = this.mMessagesWaitingForRequestPreparer.size();
    for (int j = 0;; j++)
    {
      boolean bool = false;
      if (j >= i) {
        break;
      }
      MessageHolder localMessageHolder = (MessageHolder)this.mMessagesWaitingForRequestPreparer.get(j);
      Message localMessage = localMessageHolder.mMessage;
      int k = localMessageHolder.mInterrogatingPid;
      long l = localMessageHolder.mInterrogatingTid;
      if (j == 0) {
        bool = true;
      }
      scheduleMessage(localMessage, k, l, bool);
    }
    this.mMessagesWaitingForRequestPreparer.clear();
    this.mNumActiveRequestPreparers = 0;
    this.mActiveRequestPreparerId = -1;
  }
  
  private void scheduleMessage(Message paramMessage, int paramInt, long paramLong, boolean paramBoolean)
  {
    if ((paramBoolean) || (!holdOffMessageIfNeeded(paramMessage, paramInt, paramLong))) {
      if ((paramInt == this.mMyProcessId) && (paramLong == this.mMyLooperThreadId) && (this.mHandler.hasAccessibilityCallback(paramMessage))) {
        AccessibilityInteractionClient.getInstanceForThread(paramLong).setSameThreadMessage(paramMessage);
      } else if ((!this.mHandler.hasAccessibilityCallback(paramMessage)) && (Thread.currentThread().getId() == this.mMyLooperThreadId)) {
        this.mHandler.handleMessage(paramMessage);
      } else {
        this.mHandler.sendMessage(paramMessage);
      }
    }
  }
  
  private boolean shouldApplyAppScaleAndMagnificationSpec(float paramFloat, MagnificationSpec paramMagnificationSpec)
  {
    boolean bool;
    if ((paramFloat == 1.0F) && ((paramMagnificationSpec == null) || (paramMagnificationSpec.isNop()))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private boolean shouldBypassAdjustIsVisible()
  {
    return this.mViewRootImpl.mOrigWindowType == 2011;
  }
  
  /* Error */
  private void updateInfoForViewportAndReturnFindNodeResult(AccessibilityNodeInfo paramAccessibilityNodeInfo, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt, MagnificationSpec paramMagnificationSpec, Region paramRegion)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 137	android/view/AccessibilityInteractionController:mViewRootImpl	Landroid/view/ViewRootImpl;
    //   4: getfield 254	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   7: iconst_0
    //   8: putfield 377	android/view/View$AttachInfo:mAccessibilityFetchFlags	I
    //   11: aload_0
    //   12: aload_1
    //   13: aload 4
    //   15: invokespecial 369	android/view/AccessibilityInteractionController:applyAppScaleAndMagnificationSpecIfNeeded	(Landroid/view/accessibility/AccessibilityNodeInfo;Landroid/view/MagnificationSpec;)V
    //   18: aload_0
    //   19: aload_1
    //   20: aload 5
    //   22: invokespecial 246	android/view/AccessibilityInteractionController:adjustIsVisibleToUserIfNeeded	(Landroid/view/accessibility/AccessibilityNodeInfo;Landroid/graphics/Region;)V
    //   25: aload_2
    //   26: aload_1
    //   27: iload_3
    //   28: invokeinterface 761 3 0
    //   33: goto +15 -> 48
    //   36: astore_1
    //   37: aload_0
    //   38: aload 4
    //   40: aload 5
    //   42: invokespecial 763	android/view/AccessibilityInteractionController:recycleMagnificationSpecAndRegionIfNeeded	(Landroid/view/MagnificationSpec;Landroid/graphics/Region;)V
    //   45: aload_1
    //   46: athrow
    //   47: astore_1
    //   48: aload_0
    //   49: aload 4
    //   51: aload 5
    //   53: invokespecial 763	android/view/AccessibilityInteractionController:recycleMagnificationSpecAndRegionIfNeeded	(Landroid/view/MagnificationSpec;Landroid/graphics/Region;)V
    //   56: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	57	0	this	AccessibilityInteractionController
    //   0	57	1	paramAccessibilityNodeInfo	AccessibilityNodeInfo
    //   0	57	2	paramIAccessibilityInteractionConnectionCallback	IAccessibilityInteractionConnectionCallback
    //   0	57	3	paramInt	int
    //   0	57	4	paramMagnificationSpec	MagnificationSpec
    //   0	57	5	paramRegion	Region
    // Exception table:
    //   from	to	target	type
    //   0	33	36	finally
    //   0	33	47	android/os/RemoteException
  }
  
  /* Error */
  private void updateInfosForViewportAndReturnFindNodeResult(List<AccessibilityNodeInfo> paramList, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt, MagnificationSpec paramMagnificationSpec, Region paramRegion)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 137	android/view/AccessibilityInteractionController:mViewRootImpl	Landroid/view/ViewRootImpl;
    //   4: getfield 254	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   7: iconst_0
    //   8: putfield 377	android/view/View$AttachInfo:mAccessibilityFetchFlags	I
    //   11: aload_0
    //   12: aload_1
    //   13: aload 4
    //   15: invokespecial 765	android/view/AccessibilityInteractionController:applyAppScaleAndMagnificationSpecIfNeeded	(Ljava/util/List;Landroid/view/MagnificationSpec;)V
    //   18: aload_0
    //   19: aload_1
    //   20: aload 5
    //   22: invokespecial 767	android/view/AccessibilityInteractionController:adjustIsVisibleToUserIfNeeded	(Ljava/util/List;Landroid/graphics/Region;)V
    //   25: aload_2
    //   26: aload_1
    //   27: iload_3
    //   28: invokeinterface 771 3 0
    //   33: aload_1
    //   34: ifnull +24 -> 58
    //   37: aload_1
    //   38: invokeinterface 457 1 0
    //   43: goto +15 -> 58
    //   46: astore_1
    //   47: aload_0
    //   48: aload 4
    //   50: aload 5
    //   52: invokespecial 763	android/view/AccessibilityInteractionController:recycleMagnificationSpecAndRegionIfNeeded	(Landroid/view/MagnificationSpec;Landroid/graphics/Region;)V
    //   55: aload_1
    //   56: athrow
    //   57: astore_1
    //   58: aload_0
    //   59: aload 4
    //   61: aload 5
    //   63: invokespecial 763	android/view/AccessibilityInteractionController:recycleMagnificationSpecAndRegionIfNeeded	(Landroid/view/MagnificationSpec;Landroid/graphics/Region;)V
    //   66: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	67	0	this	AccessibilityInteractionController
    //   0	67	1	paramList	List<AccessibilityNodeInfo>
    //   0	67	2	paramIAccessibilityInteractionConnectionCallback	IAccessibilityInteractionConnectionCallback
    //   0	67	3	paramInt	int
    //   0	67	4	paramMagnificationSpec	MagnificationSpec
    //   0	67	5	paramRegion	Region
    // Exception table:
    //   from	to	target	type
    //   0	33	46	finally
    //   37	43	46	finally
    //   0	33	57	android/os/RemoteException
    //   37	43	57	android/os/RemoteException
  }
  
  public void clearAccessibilityFocusClientThread()
  {
    Message localMessage = this.mHandler.obtainMessage();
    localMessage.what = 101;
    scheduleMessage(localMessage, 0, 0L, false);
  }
  
  public void findAccessibilityNodeInfoByAccessibilityIdClientThread(long paramLong1, Region paramRegion, int paramInt1, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt2, int paramInt3, long paramLong2, MagnificationSpec paramMagnificationSpec, Bundle paramBundle)
  {
    Message localMessage = this.mHandler.obtainMessage();
    localMessage.what = 2;
    localMessage.arg1 = paramInt2;
    SomeArgs localSomeArgs = SomeArgs.obtain();
    localSomeArgs.argi1 = AccessibilityNodeInfo.getAccessibilityViewId(paramLong1);
    localSomeArgs.argi2 = AccessibilityNodeInfo.getVirtualDescendantId(paramLong1);
    localSomeArgs.argi3 = paramInt1;
    localSomeArgs.arg1 = paramIAccessibilityInteractionConnectionCallback;
    localSomeArgs.arg2 = paramMagnificationSpec;
    localSomeArgs.arg3 = paramRegion;
    localSomeArgs.arg4 = paramBundle;
    localMessage.obj = localSomeArgs;
    scheduleMessage(localMessage, paramInt3, paramLong2, false);
  }
  
  public void findAccessibilityNodeInfosByTextClientThread(long paramLong1, String paramString, Region paramRegion, int paramInt1, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt2, int paramInt3, long paramLong2, MagnificationSpec paramMagnificationSpec)
  {
    Message localMessage = this.mHandler.obtainMessage();
    localMessage.what = 4;
    localMessage.arg1 = paramInt2;
    SomeArgs localSomeArgs = SomeArgs.obtain();
    localSomeArgs.arg1 = paramString;
    localSomeArgs.arg2 = paramIAccessibilityInteractionConnectionCallback;
    localSomeArgs.arg3 = paramMagnificationSpec;
    localSomeArgs.argi1 = AccessibilityNodeInfo.getAccessibilityViewId(paramLong1);
    localSomeArgs.argi2 = AccessibilityNodeInfo.getVirtualDescendantId(paramLong1);
    localSomeArgs.argi3 = paramInt1;
    localSomeArgs.arg4 = paramRegion;
    localMessage.obj = localSomeArgs;
    scheduleMessage(localMessage, paramInt3, paramLong2, false);
  }
  
  public void findAccessibilityNodeInfosByViewIdClientThread(long paramLong1, String paramString, Region paramRegion, int paramInt1, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt2, int paramInt3, long paramLong2, MagnificationSpec paramMagnificationSpec)
  {
    Message localMessage = this.mHandler.obtainMessage();
    localMessage.what = 3;
    localMessage.arg1 = paramInt2;
    localMessage.arg2 = AccessibilityNodeInfo.getAccessibilityViewId(paramLong1);
    SomeArgs localSomeArgs = SomeArgs.obtain();
    localSomeArgs.argi1 = paramInt1;
    localSomeArgs.arg1 = paramIAccessibilityInteractionConnectionCallback;
    localSomeArgs.arg2 = paramMagnificationSpec;
    localSomeArgs.arg3 = paramString;
    localSomeArgs.arg4 = paramRegion;
    localMessage.obj = localSomeArgs;
    scheduleMessage(localMessage, paramInt3, paramLong2, false);
  }
  
  public void findFocusClientThread(long paramLong1, int paramInt1, Region paramRegion, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt3, int paramInt4, long paramLong2, MagnificationSpec paramMagnificationSpec)
  {
    Message localMessage = this.mHandler.obtainMessage();
    localMessage.what = 5;
    localMessage.arg1 = paramInt3;
    localMessage.arg2 = paramInt1;
    SomeArgs localSomeArgs = SomeArgs.obtain();
    localSomeArgs.argi1 = paramInt2;
    localSomeArgs.argi2 = AccessibilityNodeInfo.getAccessibilityViewId(paramLong1);
    localSomeArgs.argi3 = AccessibilityNodeInfo.getVirtualDescendantId(paramLong1);
    localSomeArgs.arg1 = paramIAccessibilityInteractionConnectionCallback;
    localSomeArgs.arg2 = paramMagnificationSpec;
    localSomeArgs.arg3 = paramRegion;
    localMessage.obj = localSomeArgs;
    scheduleMessage(localMessage, paramInt4, paramLong2, false);
  }
  
  public void focusSearchClientThread(long paramLong1, int paramInt1, Region paramRegion, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt3, int paramInt4, long paramLong2, MagnificationSpec paramMagnificationSpec)
  {
    Message localMessage = this.mHandler.obtainMessage();
    localMessage.what = 6;
    localMessage.arg1 = paramInt3;
    localMessage.arg2 = AccessibilityNodeInfo.getAccessibilityViewId(paramLong1);
    SomeArgs localSomeArgs = SomeArgs.obtain();
    localSomeArgs.argi2 = paramInt1;
    localSomeArgs.argi3 = paramInt2;
    localSomeArgs.arg1 = paramIAccessibilityInteractionConnectionCallback;
    localSomeArgs.arg2 = paramMagnificationSpec;
    localSomeArgs.arg3 = paramRegion;
    localMessage.obj = localSomeArgs;
    scheduleMessage(localMessage, paramInt4, paramLong2, false);
  }
  
  public void notifyOutsideTouchClientThread()
  {
    Message localMessage = this.mHandler.obtainMessage();
    localMessage.what = 102;
    scheduleMessage(localMessage, 0, 0L, false);
  }
  
  public void performAccessibilityActionClientThread(long paramLong1, int paramInt1, Bundle paramBundle, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt3, int paramInt4, long paramLong2)
  {
    Message localMessage = this.mHandler.obtainMessage();
    localMessage.what = 1;
    localMessage.arg1 = paramInt3;
    localMessage.arg2 = AccessibilityNodeInfo.getAccessibilityViewId(paramLong1);
    SomeArgs localSomeArgs = SomeArgs.obtain();
    localSomeArgs.argi1 = AccessibilityNodeInfo.getVirtualDescendantId(paramLong1);
    localSomeArgs.argi2 = paramInt1;
    localSomeArgs.argi3 = paramInt2;
    localSomeArgs.arg1 = paramIAccessibilityInteractionConnectionCallback;
    localSomeArgs.arg2 = paramBundle;
    localMessage.obj = localSomeArgs;
    scheduleMessage(localMessage, paramInt4, paramLong2, false);
  }
  
  private class AccessibilityNodePrefetcher
  {
    private static final int MAX_ACCESSIBILITY_NODE_INFO_BATCH_SIZE = 50;
    private final ArrayList<View> mTempViewList = new ArrayList();
    
    private AccessibilityNodePrefetcher() {}
    
    private void enforceNodeTreeConsistent(List<AccessibilityNodeInfo> paramList)
    {
      LongSparseArray localLongSparseArray = new LongSparseArray();
      int i = paramList.size();
      for (int j = 0; j < i; j++)
      {
        localObject1 = (AccessibilityNodeInfo)paramList.get(j);
        localLongSparseArray.put(((AccessibilityNodeInfo)localObject1).getSourceNodeId(), localObject1);
      }
      Object localObject1 = (AccessibilityNodeInfo)localLongSparseArray.valueAt(0);
      for (paramList = (List<AccessibilityNodeInfo>)localObject1; paramList != null; paramList = (AccessibilityNodeInfo)localLongSparseArray.get(paramList.getParentNodeId())) {
        localObject1 = paramList;
      }
      Object localObject2 = null;
      paramList = null;
      HashSet localHashSet = new HashSet();
      LinkedList localLinkedList = new LinkedList();
      localLinkedList.add(localObject1);
      localObject1 = paramList;
      while (!localLinkedList.isEmpty())
      {
        paramList = (AccessibilityNodeInfo)localLinkedList.poll();
        if (localHashSet.add(paramList))
        {
          Object localObject3 = localObject2;
          if (paramList.isAccessibilityFocused()) {
            if (localObject2 == null)
            {
              localObject3 = paramList;
            }
            else
            {
              localObject1 = new StringBuilder();
              ((StringBuilder)localObject1).append("Duplicate accessibility focus:");
              ((StringBuilder)localObject1).append(paramList);
              ((StringBuilder)localObject1).append(" in window:");
              ((StringBuilder)localObject1).append(AccessibilityInteractionController.this.mViewRootImpl.mAttachInfo.mAccessibilityWindowId);
              throw new IllegalStateException(((StringBuilder)localObject1).toString());
            }
          }
          Object localObject4 = localObject1;
          if (paramList.isFocused()) {
            if (localObject1 == null)
            {
              localObject4 = paramList;
            }
            else
            {
              localObject1 = new StringBuilder();
              ((StringBuilder)localObject1).append("Duplicate input focus: ");
              ((StringBuilder)localObject1).append(paramList);
              ((StringBuilder)localObject1).append(" in window:");
              ((StringBuilder)localObject1).append(AccessibilityInteractionController.this.mViewRootImpl.mAttachInfo.mAccessibilityWindowId);
              throw new IllegalStateException(((StringBuilder)localObject1).toString());
            }
          }
          i = paramList.getChildCount();
          for (j = 0; j < i; j++)
          {
            localObject1 = (AccessibilityNodeInfo)localLongSparseArray.get(paramList.getChildId(j));
            if (localObject1 != null) {
              localLinkedList.add(localObject1);
            }
          }
          localObject2 = localObject3;
          localObject1 = localObject4;
        }
        else
        {
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append("Duplicate node: ");
          ((StringBuilder)localObject1).append(paramList);
          ((StringBuilder)localObject1).append(" in window:");
          ((StringBuilder)localObject1).append(AccessibilityInteractionController.this.mViewRootImpl.mAttachInfo.mAccessibilityWindowId);
          throw new IllegalStateException(((StringBuilder)localObject1).toString());
        }
      }
      j = localLongSparseArray.size() - 1;
      while (j >= 0)
      {
        paramList = (AccessibilityNodeInfo)localLongSparseArray.valueAt(j);
        if (localHashSet.contains(paramList))
        {
          j--;
        }
        else
        {
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append("Disconnected node: ");
          ((StringBuilder)localObject1).append(paramList);
          throw new IllegalStateException(((StringBuilder)localObject1).toString());
        }
      }
    }
    
    private void prefetchDescendantsOfRealNode(View paramView, List<AccessibilityNodeInfo> paramList)
    {
      if (!(paramView instanceof ViewGroup)) {
        return;
      }
      Object localObject1 = new HashMap();
      Object localObject2 = this.mTempViewList;
      ((ArrayList)localObject2).clear();
      try
      {
        paramView.addChildrenForAccessibility((ArrayList)localObject2);
        int i = ((ArrayList)localObject2).size();
        for (int j = 0; j < i; j++)
        {
          int k = paramList.size();
          if (k >= 50) {
            return;
          }
          paramView = (View)((ArrayList)localObject2).get(j);
          if (AccessibilityInteractionController.this.isShown(paramView))
          {
            Object localObject3 = paramView.getAccessibilityNodeProvider();
            if (localObject3 == null)
            {
              localObject3 = paramView.createAccessibilityNodeInfo();
              if (localObject3 != null)
              {
                paramList.add(localObject3);
                ((HashMap)localObject1).put(paramView, null);
              }
            }
            else
            {
              localObject3 = ((AccessibilityNodeProvider)localObject3).createAccessibilityNodeInfo(-1);
              if (localObject3 != null)
              {
                paramList.add(localObject3);
                ((HashMap)localObject1).put(paramView, localObject3);
              }
            }
          }
        }
        ((ArrayList)localObject2).clear();
        if (paramList.size() < 50)
        {
          localObject2 = ((HashMap)localObject1).entrySet().iterator();
          while (((Iterator)localObject2).hasNext())
          {
            localObject1 = (Map.Entry)((Iterator)localObject2).next();
            paramView = (View)((Map.Entry)localObject1).getKey();
            localObject1 = (AccessibilityNodeInfo)((Map.Entry)localObject1).getValue();
            if (localObject1 == null) {
              prefetchDescendantsOfRealNode(paramView, paramList);
            } else {
              prefetchDescendantsOfVirtualNode((AccessibilityNodeInfo)localObject1, paramView.getAccessibilityNodeProvider(), paramList);
            }
          }
        }
        return;
      }
      finally
      {
        ((ArrayList)localObject2).clear();
      }
    }
    
    private void prefetchDescendantsOfVirtualNode(AccessibilityNodeInfo paramAccessibilityNodeInfo, AccessibilityNodeProvider paramAccessibilityNodeProvider, List<AccessibilityNodeInfo> paramList)
    {
      int i = paramList.size();
      int j = paramAccessibilityNodeInfo.getChildCount();
      for (int k = 0; k < j; k++)
      {
        if (paramList.size() >= 50) {
          return;
        }
        long l = paramAccessibilityNodeInfo.getChildId(k);
        AccessibilityNodeInfo localAccessibilityNodeInfo = paramAccessibilityNodeProvider.createAccessibilityNodeInfo(AccessibilityNodeInfo.getVirtualDescendantId(l));
        if (localAccessibilityNodeInfo != null) {
          paramList.add(localAccessibilityNodeInfo);
        }
      }
      if (paramList.size() < 50)
      {
        j = paramList.size();
        for (k = 0; k < j - i; k++) {
          prefetchDescendantsOfVirtualNode((AccessibilityNodeInfo)paramList.get(i + k), paramAccessibilityNodeProvider, paramList);
        }
      }
    }
    
    private void prefetchPredecessorsOfRealNode(View paramView, List<AccessibilityNodeInfo> paramList)
    {
      for (paramView = paramView.getParentForAccessibility(); ((paramView instanceof View)) && (paramList.size() < 50); paramView = paramView.getParentForAccessibility())
      {
        AccessibilityNodeInfo localAccessibilityNodeInfo = ((View)paramView).createAccessibilityNodeInfo();
        if (localAccessibilityNodeInfo != null) {
          paramList.add(localAccessibilityNodeInfo);
        }
      }
    }
    
    private void prefetchPredecessorsOfVirtualNode(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView, AccessibilityNodeProvider paramAccessibilityNodeProvider, List<AccessibilityNodeInfo> paramList)
    {
      int i = paramList.size();
      long l = paramAccessibilityNodeInfo.getParentNodeId();
      for (int j = AccessibilityNodeInfo.getAccessibilityViewId(l); j != Integer.MAX_VALUE; j = AccessibilityNodeInfo.getAccessibilityViewId(l))
      {
        if (paramList.size() >= 50) {
          return;
        }
        int k = AccessibilityNodeInfo.getVirtualDescendantId(l);
        if ((k == -1) && (j != paramView.getAccessibilityViewId()))
        {
          prefetchPredecessorsOfRealNode(paramView, paramList);
          return;
        }
        paramAccessibilityNodeInfo = paramAccessibilityNodeProvider.createAccessibilityNodeInfo(k);
        if (paramAccessibilityNodeInfo == null)
        {
          for (j = paramList.size() - 1; j >= i; j--) {
            paramList.remove(j);
          }
          return;
        }
        paramList.add(paramAccessibilityNodeInfo);
        l = paramAccessibilityNodeInfo.getParentNodeId();
      }
    }
    
    private void prefetchSiblingsOfRealNode(View paramView, List<AccessibilityNodeInfo> paramList)
    {
      Object localObject = paramView.getParentForAccessibility();
      if ((localObject instanceof ViewGroup))
      {
        localObject = (ViewGroup)localObject;
        ArrayList localArrayList = this.mTempViewList;
        localArrayList.clear();
        try
        {
          ((ViewGroup)localObject).addChildrenForAccessibility(localArrayList);
          int i = localArrayList.size();
          for (int j = 0; j < i; j++)
          {
            int k = paramList.size();
            if (k >= 50) {
              return;
            }
            localObject = (View)localArrayList.get(j);
            if ((((View)localObject).getAccessibilityViewId() != paramView.getAccessibilityViewId()) && (AccessibilityInteractionController.this.isShown((View)localObject)))
            {
              AccessibilityNodeProvider localAccessibilityNodeProvider = ((View)localObject).getAccessibilityNodeProvider();
              if (localAccessibilityNodeProvider == null) {
                localObject = ((View)localObject).createAccessibilityNodeInfo();
              } else {
                localObject = localAccessibilityNodeProvider.createAccessibilityNodeInfo(-1);
              }
              if (localObject != null) {
                paramList.add(localObject);
              }
            }
          }
        }
        finally
        {
          localArrayList.clear();
        }
      }
    }
    
    private void prefetchSiblingsOfVirtualNode(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView, AccessibilityNodeProvider paramAccessibilityNodeProvider, List<AccessibilityNodeInfo> paramList)
    {
      long l = paramAccessibilityNodeInfo.getParentNodeId();
      int i = AccessibilityNodeInfo.getAccessibilityViewId(l);
      int j = AccessibilityNodeInfo.getVirtualDescendantId(l);
      if ((j == -1) && (i != paramView.getAccessibilityViewId()))
      {
        prefetchSiblingsOfRealNode(paramView, paramList);
      }
      else
      {
        paramView = paramAccessibilityNodeProvider.createAccessibilityNodeInfo(j);
        if (paramView != null)
        {
          i = paramView.getChildCount();
          for (j = 0; j < i; j++)
          {
            if (paramList.size() >= 50) {
              return;
            }
            l = paramView.getChildId(j);
            if (l != paramAccessibilityNodeInfo.getSourceNodeId())
            {
              AccessibilityNodeInfo localAccessibilityNodeInfo = paramAccessibilityNodeProvider.createAccessibilityNodeInfo(AccessibilityNodeInfo.getVirtualDescendantId(l));
              if (localAccessibilityNodeInfo != null) {
                paramList.add(localAccessibilityNodeInfo);
              }
            }
          }
        }
      }
    }
    
    public void prefetchAccessibilityNodeInfos(View paramView, int paramInt1, int paramInt2, List<AccessibilityNodeInfo> paramList, Bundle paramBundle)
    {
      Object localObject = paramView.getAccessibilityNodeProvider();
      String str;
      if (paramBundle == null) {
        str = null;
      } else {
        str = paramBundle.getString("android.view.accessibility.AccessibilityNodeInfo.extra_data_requested");
      }
      if (localObject == null)
      {
        localObject = paramView.createAccessibilityNodeInfo();
        if (localObject != null)
        {
          if (str != null) {
            paramView.addExtraDataToAccessibilityNodeInfo((AccessibilityNodeInfo)localObject, str, paramBundle);
          }
          paramList.add(localObject);
          if ((paramInt2 & 0x1) != 0) {
            prefetchPredecessorsOfRealNode(paramView, paramList);
          }
          if ((paramInt2 & 0x2) != 0) {
            prefetchSiblingsOfRealNode(paramView, paramList);
          }
          if ((paramInt2 & 0x4) != 0) {
            prefetchDescendantsOfRealNode(paramView, paramList);
          }
        }
      }
      else
      {
        AccessibilityNodeInfo localAccessibilityNodeInfo = ((AccessibilityNodeProvider)localObject).createAccessibilityNodeInfo(paramInt1);
        if (localAccessibilityNodeInfo != null)
        {
          if (str != null) {
            ((AccessibilityNodeProvider)localObject).addExtraDataToAccessibilityNodeInfo(paramInt1, localAccessibilityNodeInfo, str, paramBundle);
          }
          paramList.add(localAccessibilityNodeInfo);
          if ((paramInt2 & 0x1) != 0) {
            prefetchPredecessorsOfVirtualNode(localAccessibilityNodeInfo, paramView, (AccessibilityNodeProvider)localObject, paramList);
          }
          if ((paramInt2 & 0x2) != 0) {
            prefetchSiblingsOfVirtualNode(localAccessibilityNodeInfo, paramView, (AccessibilityNodeProvider)localObject, paramList);
          }
          if ((paramInt2 & 0x4) != 0) {
            prefetchDescendantsOfVirtualNode(localAccessibilityNodeInfo, (AccessibilityNodeProvider)localObject, paramList);
          }
        }
      }
    }
  }
  
  private final class AddNodeInfosForViewId
    implements Predicate<View>
  {
    private List<AccessibilityNodeInfo> mInfos;
    private int mViewId = -1;
    
    private AddNodeInfosForViewId() {}
    
    public void init(int paramInt, List<AccessibilityNodeInfo> paramList)
    {
      this.mViewId = paramInt;
      this.mInfos = paramList;
    }
    
    public void reset()
    {
      this.mViewId = -1;
      this.mInfos = null;
    }
    
    public boolean test(View paramView)
    {
      if ((paramView.getId() == this.mViewId) && (AccessibilityInteractionController.this.isShown(paramView))) {
        this.mInfos.add(paramView.createAccessibilityNodeInfo());
      }
      return false;
    }
  }
  
  private static final class MessageHolder
  {
    final int mInterrogatingPid;
    final long mInterrogatingTid;
    final Message mMessage;
    
    MessageHolder(Message paramMessage, int paramInt, long paramLong)
    {
      this.mMessage = paramMessage;
      this.mInterrogatingPid = paramInt;
      this.mInterrogatingTid = paramLong;
    }
  }
  
  private class PrivateHandler
    extends Handler
  {
    private static final int FIRST_NO_ACCESSIBILITY_CALLBACK_MSG = 100;
    private static final int MSG_APP_PREPARATION_FINISHED = 8;
    private static final int MSG_APP_PREPARATION_TIMEOUT = 9;
    private static final int MSG_CLEAR_ACCESSIBILITY_FOCUS = 101;
    private static final int MSG_FIND_ACCESSIBILITY_NODE_INFOS_BY_VIEW_ID = 3;
    private static final int MSG_FIND_ACCESSIBILITY_NODE_INFO_BY_ACCESSIBILITY_ID = 2;
    private static final int MSG_FIND_ACCESSIBILITY_NODE_INFO_BY_TEXT = 4;
    private static final int MSG_FIND_FOCUS = 5;
    private static final int MSG_FOCUS_SEARCH = 6;
    private static final int MSG_NOTIFY_OUTSIDE_TOUCH = 102;
    private static final int MSG_PERFORM_ACCESSIBILITY_ACTION = 1;
    private static final int MSG_PREPARE_FOR_EXTRA_DATA_REQUEST = 7;
    
    public PrivateHandler(Looper paramLooper)
    {
      super();
    }
    
    public String getMessageName(Message paramMessage)
    {
      int i = paramMessage.what;
      if (i != 101)
      {
        if (i != 102)
        {
          switch (i)
          {
          default: 
            paramMessage = new StringBuilder();
            paramMessage.append("Unknown message type: ");
            paramMessage.append(i);
            throw new IllegalArgumentException(paramMessage.toString());
          case 9: 
            return "MSG_APP_PREPARATION_TIMEOUT";
          case 8: 
            return "MSG_APP_PREPARATION_FINISHED";
          case 7: 
            return "MSG_PREPARE_FOR_EXTRA_DATA_REQUEST";
          case 6: 
            return "MSG_FOCUS_SEARCH";
          case 5: 
            return "MSG_FIND_FOCUS";
          case 4: 
            return "MSG_FIND_ACCESSIBILITY_NODE_INFO_BY_TEXT";
          case 3: 
            return "MSG_FIND_ACCESSIBILITY_NODE_INFOS_BY_VIEW_ID";
          case 2: 
            return "MSG_FIND_ACCESSIBILITY_NODE_INFO_BY_ACCESSIBILITY_ID";
          }
          return "MSG_PERFORM_ACCESSIBILITY_ACTION";
        }
        return "MSG_NOTIFY_OUTSIDE_TOUCH";
      }
      return "MSG_CLEAR_ACCESSIBILITY_FOCUS";
    }
    
    public void handleMessage(Message paramMessage)
    {
      int i = paramMessage.what;
      if (i != 101)
      {
        if (i != 102) {
          switch (i)
          {
          default: 
            paramMessage = new StringBuilder();
            paramMessage.append("Unknown message type: ");
            paramMessage.append(i);
            throw new IllegalArgumentException(paramMessage.toString());
          case 9: 
            AccessibilityInteractionController.this.requestPreparerTimeoutUiThread();
            break;
          case 8: 
            AccessibilityInteractionController.this.requestPreparerDoneUiThread(paramMessage);
            break;
          case 7: 
            AccessibilityInteractionController.this.prepareForExtraDataRequestUiThread(paramMessage);
            break;
          case 6: 
            AccessibilityInteractionController.this.focusSearchUiThread(paramMessage);
            break;
          case 5: 
            AccessibilityInteractionController.this.findFocusUiThread(paramMessage);
            break;
          case 4: 
            AccessibilityInteractionController.this.findAccessibilityNodeInfosByTextUiThread(paramMessage);
            break;
          case 3: 
            AccessibilityInteractionController.this.findAccessibilityNodeInfosByViewIdUiThread(paramMessage);
            break;
          case 2: 
            AccessibilityInteractionController.this.findAccessibilityNodeInfoByAccessibilityIdUiThread(paramMessage);
            break;
          case 1: 
            AccessibilityInteractionController.this.performAccessibilityActionUiThread(paramMessage);
            break;
          }
        } else {
          AccessibilityInteractionController.this.notifyOutsideTouchUiThread();
        }
      }
      else {
        AccessibilityInteractionController.this.clearAccessibilityFocusUiThread();
      }
    }
    
    boolean hasAccessibilityCallback(Message paramMessage)
    {
      boolean bool;
      if (paramMessage.what < 100) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/AccessibilityInteractionController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */