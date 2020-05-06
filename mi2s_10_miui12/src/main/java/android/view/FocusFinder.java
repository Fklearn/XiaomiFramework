package android.view;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.util.ArrayMap;
import android.util.ArraySet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class FocusFinder
{
  private static final ThreadLocal<FocusFinder> tlFocusFinder = new ThreadLocal()
  {
    protected FocusFinder initialValue()
    {
      return new FocusFinder(null);
    }
  };
  final Rect mBestCandidateRect = new Rect();
  private final FocusSorter mFocusSorter = new FocusSorter();
  final Rect mFocusedRect = new Rect();
  final Rect mOtherRect = new Rect();
  private final ArrayList<View> mTempList = new ArrayList();
  private final UserSpecifiedFocusComparator mUserSpecifiedClusterComparator = new UserSpecifiedFocusComparator(_..Lambda.FocusFinder.P8rLvOJhymJH5ALAgUjGaM5gxKA.INSTANCE);
  private final UserSpecifiedFocusComparator mUserSpecifiedFocusComparator = new UserSpecifiedFocusComparator(_..Lambda.FocusFinder.Pgx6IETuqCkrhJYdiBes48tolG4.INSTANCE);
  
  private View findNextFocus(ViewGroup paramViewGroup, View paramView, Rect paramRect, int paramInt)
  {
    ArrayList localArrayList = null;
    ViewGroup localViewGroup = getEffectiveRoot(paramViewGroup, paramView);
    paramViewGroup = localArrayList;
    if (paramView != null) {
      paramViewGroup = findNextUserSpecifiedFocus(localViewGroup, paramView, paramInt);
    }
    if (paramViewGroup != null) {
      return paramViewGroup;
    }
    localArrayList = this.mTempList;
    try
    {
      localArrayList.clear();
      localViewGroup.addFocusables(localArrayList, paramInt);
      if (!localArrayList.isEmpty()) {
        paramViewGroup = findNextFocus(localViewGroup, paramView, paramRect, paramInt, localArrayList);
      }
      return paramViewGroup;
    }
    finally
    {
      localArrayList.clear();
    }
  }
  
  private View findNextFocus(ViewGroup paramViewGroup, View paramView, Rect paramRect, int paramInt, ArrayList<View> paramArrayList)
  {
    if (paramView != null)
    {
      if (paramRect == null) {
        paramRect = this.mFocusedRect;
      }
      paramView.getFocusedRect(paramRect);
      paramViewGroup.offsetDescendantRectToMyCoords(paramView, paramRect);
    }
    else if (paramRect == null)
    {
      paramRect = this.mFocusedRect;
      if (paramInt != 1)
      {
        if (paramInt != 2)
        {
          if ((paramInt != 17) && (paramInt != 33))
          {
            if ((paramInt == 66) || (paramInt == 130)) {
              setFocusTopLeft(paramViewGroup, paramRect);
            }
          }
          else {
            setFocusBottomRight(paramViewGroup, paramRect);
          }
        }
        else if (paramViewGroup.isLayoutRtl()) {
          setFocusBottomRight(paramViewGroup, paramRect);
        } else {
          setFocusTopLeft(paramViewGroup, paramRect);
        }
      }
      else if (paramViewGroup.isLayoutRtl()) {
        setFocusTopLeft(paramViewGroup, paramRect);
      } else {
        setFocusBottomRight(paramViewGroup, paramRect);
      }
    }
    if ((paramInt != 1) && (paramInt != 2))
    {
      if ((paramInt != 17) && (paramInt != 33) && (paramInt != 66) && (paramInt != 130))
      {
        paramViewGroup = new StringBuilder();
        paramViewGroup.append("Unknown direction: ");
        paramViewGroup.append(paramInt);
        throw new IllegalArgumentException(paramViewGroup.toString());
      }
      return findNextFocusInAbsoluteDirection(paramArrayList, paramViewGroup, paramView, paramRect, paramInt);
    }
    return findNextFocusInRelativeDirection(paramArrayList, paramViewGroup, paramView, paramRect, paramInt);
  }
  
  private View findNextFocusInRelativeDirection(ArrayList<View> paramArrayList, ViewGroup paramViewGroup, View paramView, Rect paramRect, int paramInt)
  {
    try
    {
      this.mUserSpecifiedFocusComparator.setFocusables(paramArrayList, paramViewGroup);
      Collections.sort(paramArrayList, this.mUserSpecifiedFocusComparator);
      this.mUserSpecifiedFocusComparator.recycle();
      int i = paramArrayList.size();
      if (paramInt != 1)
      {
        if (paramInt != 2) {
          return (View)paramArrayList.get(i - 1);
        }
        return getNextFocusable(paramView, paramArrayList, i);
      }
      return getPreviousFocusable(paramView, paramArrayList, i);
    }
    finally
    {
      this.mUserSpecifiedFocusComparator.recycle();
    }
  }
  
  private View findNextKeyboardNavigationCluster(View paramView1, View paramView2, List<View> paramList, int paramInt)
  {
    try
    {
      this.mUserSpecifiedClusterComparator.setFocusables(paramList, paramView1);
      Collections.sort(paramList, this.mUserSpecifiedClusterComparator);
      this.mUserSpecifiedClusterComparator.recycle();
      int i = paramList.size();
      if (paramInt != 1)
      {
        if (paramInt != 2)
        {
          if ((paramInt == 17) || (paramInt == 33)) {
            break label119;
          }
          if ((paramInt != 66) && (paramInt != 130))
          {
            paramView1 = new StringBuilder();
            paramView1.append("Unknown direction: ");
            paramView1.append(paramInt);
            throw new IllegalArgumentException(paramView1.toString());
          }
        }
        return getNextKeyboardNavigationCluster(paramView1, paramView2, paramList, i);
      }
      label119:
      return getPreviousKeyboardNavigationCluster(paramView1, paramView2, paramList, i);
    }
    finally
    {
      this.mUserSpecifiedClusterComparator.recycle();
    }
  }
  
  private View findNextUserSpecifiedFocus(ViewGroup paramViewGroup, View paramView, int paramInt)
  {
    Object localObject = paramView.findUserSetNextFocus(paramViewGroup, paramInt);
    paramView = (View)localObject;
    int i = 1;
    View localView1;
    View localView2;
    do
    {
      int j;
      int k;
      do
      {
        if (localObject == null) {
          break;
        }
        if ((((View)localObject).isFocusable()) && (((View)localObject).getVisibility() == 0) && ((!((View)localObject).isInTouchMode()) || (((View)localObject).isFocusableInTouchMode()))) {
          return (View)localObject;
        }
        localView1 = ((View)localObject).findUserSetNextFocus(paramViewGroup, paramInt);
        if (i == 0) {
          j = 1;
        } else {
          j = 0;
        }
        k = j;
        localObject = localView1;
        i = k;
      } while (j == 0);
      localView2 = paramView.findUserSetNextFocus(paramViewGroup, paramInt);
      localObject = localView1;
      paramView = localView2;
      i = k;
    } while (localView2 != localView1);
    return null;
  }
  
  private View findNextUserSpecifiedKeyboardNavigationCluster(View paramView1, View paramView2, int paramInt)
  {
    paramView1 = paramView2.findUserSetNextKeyboardNavigationCluster(paramView1, paramInt);
    if ((paramView1 != null) && (paramView1.hasFocusable())) {
      return paramView1;
    }
    return null;
  }
  
  private ViewGroup getEffectiveRoot(ViewGroup paramViewGroup, View paramView)
  {
    if ((paramView != null) && (paramView != paramViewGroup))
    {
      Object localObject1 = null;
      Object localObject2 = paramView.getParent();
      Object localObject3;
      do
      {
        if (localObject2 == paramViewGroup)
        {
          if (localObject1 != null) {
            paramViewGroup = (ViewGroup)localObject1;
          }
          return paramViewGroup;
        }
        localObject3 = (ViewGroup)localObject2;
        Object localObject4 = localObject1;
        if (((ViewGroup)localObject3).getTouchscreenBlocksFocus())
        {
          localObject4 = localObject1;
          if (paramView.getContext().getPackageManager().hasSystemFeature("android.hardware.touchscreen"))
          {
            localObject4 = localObject1;
            if (((ViewGroup)localObject3).isKeyboardNavigationCluster()) {
              localObject4 = localObject3;
            }
          }
        }
        localObject3 = ((ViewParent)localObject2).getParent();
        localObject1 = localObject4;
        localObject2 = localObject3;
      } while ((localObject3 instanceof ViewGroup));
      return paramViewGroup;
    }
    return paramViewGroup;
  }
  
  public static FocusFinder getInstance()
  {
    return (FocusFinder)tlFocusFinder.get();
  }
  
  private static View getNextFocusable(View paramView, ArrayList<View> paramArrayList, int paramInt)
  {
    if (paramView != null)
    {
      int i = paramArrayList.lastIndexOf(paramView);
      if ((i >= 0) && (i + 1 < paramInt)) {
        return (View)paramArrayList.get(i + 1);
      }
    }
    if (!paramArrayList.isEmpty()) {
      return (View)paramArrayList.get(0);
    }
    return null;
  }
  
  private static View getNextKeyboardNavigationCluster(View paramView1, View paramView2, List<View> paramList, int paramInt)
  {
    if (paramView2 == null) {
      return (View)paramList.get(0);
    }
    int i = paramList.lastIndexOf(paramView2);
    if ((i >= 0) && (i + 1 < paramInt)) {
      return (View)paramList.get(i + 1);
    }
    return paramView1;
  }
  
  private static View getPreviousFocusable(View paramView, ArrayList<View> paramArrayList, int paramInt)
  {
    if (paramView != null)
    {
      int i = paramArrayList.indexOf(paramView);
      if (i > 0) {
        return (View)paramArrayList.get(i - 1);
      }
    }
    if (!paramArrayList.isEmpty()) {
      return (View)paramArrayList.get(paramInt - 1);
    }
    return null;
  }
  
  private static View getPreviousKeyboardNavigationCluster(View paramView1, View paramView2, List<View> paramList, int paramInt)
  {
    if (paramView2 == null) {
      return (View)paramList.get(paramInt - 1);
    }
    paramInt = paramList.indexOf(paramView2);
    if (paramInt > 0) {
      return (View)paramList.get(paramInt - 1);
    }
    return paramView1;
  }
  
  private boolean isTouchCandidate(int paramInt1, int paramInt2, Rect paramRect, int paramInt3)
  {
    boolean bool1 = true;
    boolean bool2 = true;
    boolean bool3 = true;
    boolean bool4 = true;
    if (paramInt3 != 17)
    {
      if (paramInt3 != 33)
      {
        if (paramInt3 != 66)
        {
          if (paramInt3 == 130)
          {
            if ((paramRect.top < paramInt2) || (paramRect.left > paramInt1) || (paramInt1 > paramRect.right)) {
              bool4 = false;
            }
            return bool4;
          }
          throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
        }
        if ((paramRect.left >= paramInt1) && (paramRect.top <= paramInt2) && (paramInt2 <= paramRect.bottom)) {
          bool4 = bool1;
        } else {
          bool4 = false;
        }
        return bool4;
      }
      if ((paramRect.top <= paramInt2) && (paramRect.left <= paramInt1) && (paramInt1 <= paramRect.right)) {
        bool4 = bool2;
      } else {
        bool4 = false;
      }
      return bool4;
    }
    if ((paramRect.left <= paramInt1) && (paramRect.top <= paramInt2) && (paramInt2 <= paramRect.bottom)) {
      bool4 = bool3;
    } else {
      bool4 = false;
    }
    return bool4;
  }
  
  private static final boolean isValidId(int paramInt)
  {
    boolean bool;
    if ((paramInt != 0) && (paramInt != -1)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  static int majorAxisDistance(int paramInt, Rect paramRect1, Rect paramRect2)
  {
    return Math.max(0, majorAxisDistanceRaw(paramInt, paramRect1, paramRect2));
  }
  
  static int majorAxisDistanceRaw(int paramInt, Rect paramRect1, Rect paramRect2)
  {
    if (paramInt != 17)
    {
      if (paramInt != 33)
      {
        if (paramInt != 66)
        {
          if (paramInt == 130) {
            return paramRect2.top - paramRect1.bottom;
          }
          throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
        }
        return paramRect2.left - paramRect1.right;
      }
      return paramRect1.top - paramRect2.bottom;
    }
    return paramRect1.left - paramRect2.right;
  }
  
  static int majorAxisDistanceToFarEdge(int paramInt, Rect paramRect1, Rect paramRect2)
  {
    return Math.max(1, majorAxisDistanceToFarEdgeRaw(paramInt, paramRect1, paramRect2));
  }
  
  static int majorAxisDistanceToFarEdgeRaw(int paramInt, Rect paramRect1, Rect paramRect2)
  {
    if (paramInt != 17)
    {
      if (paramInt != 33)
      {
        if (paramInt != 66)
        {
          if (paramInt == 130) {
            return paramRect2.bottom - paramRect1.bottom;
          }
          throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
        }
        return paramRect2.right - paramRect1.right;
      }
      return paramRect1.top - paramRect2.top;
    }
    return paramRect1.left - paramRect2.left;
  }
  
  static int minorAxisDistance(int paramInt, Rect paramRect1, Rect paramRect2)
  {
    if (paramInt != 17)
    {
      if (paramInt != 33)
      {
        if (paramInt == 66) {
          break label66;
        }
        if (paramInt != 130) {
          throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
        }
      }
      return Math.abs(paramRect1.left + paramRect1.width() / 2 - (paramRect2.left + paramRect2.width() / 2));
    }
    label66:
    return Math.abs(paramRect1.top + paramRect1.height() / 2 - (paramRect2.top + paramRect2.height() / 2));
  }
  
  private void setFocusBottomRight(ViewGroup paramViewGroup, Rect paramRect)
  {
    int i = paramViewGroup.getScrollY() + paramViewGroup.getHeight();
    int j = paramViewGroup.getScrollX() + paramViewGroup.getWidth();
    paramRect.set(j, i, j, i);
  }
  
  private void setFocusTopLeft(ViewGroup paramViewGroup, Rect paramRect)
  {
    int i = paramViewGroup.getScrollY();
    int j = paramViewGroup.getScrollX();
    paramRect.set(j, i, j, i);
  }
  
  public static void sort(View[] paramArrayOfView, int paramInt1, int paramInt2, ViewGroup paramViewGroup, boolean paramBoolean)
  {
    getInstance().mFocusSorter.sort(paramArrayOfView, paramInt1, paramInt2, paramViewGroup, paramBoolean);
  }
  
  boolean beamBeats(int paramInt, Rect paramRect1, Rect paramRect2, Rect paramRect3)
  {
    boolean bool1 = beamsOverlap(paramInt, paramRect1, paramRect2);
    boolean bool2 = beamsOverlap(paramInt, paramRect1, paramRect3);
    boolean bool3 = false;
    if ((!bool2) && (bool1))
    {
      if (!isToDirectionOf(paramInt, paramRect1, paramRect3)) {
        return true;
      }
      if ((paramInt != 17) && (paramInt != 66))
      {
        if (majorAxisDistance(paramInt, paramRect1, paramRect2) < majorAxisDistanceToFarEdge(paramInt, paramRect1, paramRect3)) {
          bool3 = true;
        }
        return bool3;
      }
      return true;
    }
    return false;
  }
  
  boolean beamsOverlap(int paramInt, Rect paramRect1, Rect paramRect2)
  {
    boolean bool1 = true;
    boolean bool2 = true;
    if (paramInt != 17)
    {
      if (paramInt != 33)
      {
        if (paramInt == 66) {
          break label76;
        }
        if (paramInt != 130) {
          throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
        }
      }
      if ((paramRect2.right <= paramRect1.left) || (paramRect2.left >= paramRect1.right)) {
        bool2 = false;
      }
      return bool2;
    }
    label76:
    if ((paramRect2.bottom > paramRect1.top) && (paramRect2.top < paramRect1.bottom)) {
      bool2 = bool1;
    } else {
      bool2 = false;
    }
    return bool2;
  }
  
  public View findNearestTouchable(ViewGroup paramViewGroup, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt)
  {
    ArrayList localArrayList = paramViewGroup.getTouchables();
    int i = Integer.MAX_VALUE;
    Object localObject1 = null;
    int j = localArrayList.size();
    int k = ViewConfiguration.get(paramViewGroup.mContext).getScaledEdgeSlop();
    Rect localRect1 = new Rect();
    Rect localRect2 = this.mOtherRect;
    int m = 0;
    while (m < j)
    {
      View localView = (View)localArrayList.get(m);
      localView.getDrawingRect(localRect2);
      paramViewGroup.offsetRectBetweenParentAndChild(localView, localRect2, true, true);
      int n;
      Object localObject2;
      if (!isTouchCandidate(paramInt1, paramInt2, localRect2, paramInt3))
      {
        n = i;
        localObject2 = localObject1;
      }
      else
      {
        int i1 = Integer.MAX_VALUE;
        if (paramInt3 != 17)
        {
          if (paramInt3 != 33)
          {
            if (paramInt3 != 66)
            {
              if (paramInt3 == 130) {
                i1 = localRect2.top;
              }
            }
            else {
              i1 = localRect2.left;
            }
          }
          else {
            i1 = paramInt2 - localRect2.bottom + 1;
          }
        }
        else {
          i1 = paramInt1 - localRect2.right + 1;
        }
        n = i;
        localObject2 = localObject1;
        if (i1 < k) {
          if ((localObject1 != null) && (!localRect1.contains(localRect2)))
          {
            n = i;
            localObject2 = localObject1;
            if (!localRect2.contains(localRect1))
            {
              n = i;
              localObject2 = localObject1;
              if (i1 >= i) {}
            }
          }
          else
          {
            n = i1;
            localObject2 = localView;
            localRect1.set(localRect2);
            if (paramInt3 != 17)
            {
              if (paramInt3 != 33)
              {
                if (paramInt3 != 66)
                {
                  if (paramInt3 == 130) {
                    paramArrayOfInt[1] = i1;
                  }
                }
                else {
                  paramArrayOfInt[0] = i1;
                }
              }
              else {
                paramArrayOfInt[1] = (-i1);
              }
            }
            else {
              paramArrayOfInt[0] = (-i1);
            }
          }
        }
      }
      m++;
      i = n;
      localObject1 = localObject2;
    }
    return (View)localObject1;
  }
  
  public final View findNextFocus(ViewGroup paramViewGroup, View paramView, int paramInt)
  {
    return findNextFocus(paramViewGroup, paramView, null, paramInt);
  }
  
  public View findNextFocusFromRect(ViewGroup paramViewGroup, Rect paramRect, int paramInt)
  {
    this.mFocusedRect.set(paramRect);
    return findNextFocus(paramViewGroup, null, this.mFocusedRect, paramInt);
  }
  
  View findNextFocusInAbsoluteDirection(ArrayList<View> paramArrayList, ViewGroup paramViewGroup, View paramView, Rect paramRect, int paramInt)
  {
    this.mBestCandidateRect.set(paramRect);
    if (paramInt != 17)
    {
      if (paramInt != 33)
      {
        if (paramInt != 66)
        {
          if (paramInt == 130) {
            this.mBestCandidateRect.offset(0, -(paramRect.height() + 1));
          }
        }
        else {
          this.mBestCandidateRect.offset(-(paramRect.width() + 1), 0);
        }
      }
      else {
        this.mBestCandidateRect.offset(0, paramRect.height() + 1);
      }
    }
    else {
      this.mBestCandidateRect.offset(paramRect.width() + 1, 0);
    }
    Object localObject1 = null;
    int i = paramArrayList.size();
    int j = 0;
    while (j < i)
    {
      View localView = (View)paramArrayList.get(j);
      Object localObject2 = localObject1;
      if (localView != paramView) {
        if (localView == paramViewGroup)
        {
          localObject2 = localObject1;
        }
        else
        {
          localView.getFocusedRect(this.mOtherRect);
          paramViewGroup.offsetDescendantRectToMyCoords(localView, this.mOtherRect);
          localObject2 = localObject1;
          if (isBetterCandidate(paramInt, paramRect, this.mOtherRect, this.mBestCandidateRect))
          {
            this.mBestCandidateRect.set(this.mOtherRect);
            localObject2 = localView;
          }
        }
      }
      j++;
      localObject1 = localObject2;
    }
    return (View)localObject1;
  }
  
  public View findNextKeyboardNavigationCluster(View paramView1, View paramView2, int paramInt)
  {
    Object localObject1 = null;
    if (paramView2 != null)
    {
      localObject2 = findNextUserSpecifiedKeyboardNavigationCluster(paramView1, paramView2, paramInt);
      localObject1 = localObject2;
      if (localObject2 != null) {
        return (View)localObject2;
      }
    }
    Object localObject2 = this.mTempList;
    try
    {
      ((ArrayList)localObject2).clear();
      paramView1.addKeyboardNavigationClusters((Collection)localObject2, paramInt);
      if (!((ArrayList)localObject2).isEmpty()) {
        localObject1 = findNextKeyboardNavigationCluster(paramView1, paramView2, (List)localObject2, paramInt);
      }
      return (View)localObject1;
    }
    finally
    {
      ((ArrayList)localObject2).clear();
    }
  }
  
  long getWeightedDistanceFor(long paramLong1, long paramLong2)
  {
    return 13L * paramLong1 * paramLong1 + paramLong2 * paramLong2;
  }
  
  boolean isBetterCandidate(int paramInt, Rect paramRect1, Rect paramRect2, Rect paramRect3)
  {
    boolean bool1 = isCandidate(paramRect1, paramRect2, paramInt);
    boolean bool2 = false;
    if (!bool1) {
      return false;
    }
    if (!isCandidate(paramRect1, paramRect3, paramInt)) {
      return true;
    }
    if (beamBeats(paramInt, paramRect1, paramRect2, paramRect3)) {
      return true;
    }
    if (beamBeats(paramInt, paramRect1, paramRect3, paramRect2)) {
      return false;
    }
    if (getWeightedDistanceFor(majorAxisDistance(paramInt, paramRect1, paramRect2), minorAxisDistance(paramInt, paramRect1, paramRect2)) < getWeightedDistanceFor(majorAxisDistance(paramInt, paramRect1, paramRect3), minorAxisDistance(paramInt, paramRect1, paramRect3))) {
      bool2 = true;
    }
    return bool2;
  }
  
  boolean isCandidate(Rect paramRect1, Rect paramRect2, int paramInt)
  {
    boolean bool1 = true;
    boolean bool2 = true;
    boolean bool3 = true;
    boolean bool4 = true;
    if (paramInt != 17)
    {
      if (paramInt != 33)
      {
        if (paramInt != 66)
        {
          if (paramInt == 130)
          {
            if (((paramRect1.top >= paramRect2.top) && (paramRect1.bottom > paramRect2.top)) || (paramRect1.bottom >= paramRect2.bottom)) {
              bool4 = false;
            }
            return bool4;
          }
          throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
        }
        if (((paramRect1.left < paramRect2.left) || (paramRect1.right <= paramRect2.left)) && (paramRect1.right < paramRect2.right)) {
          bool4 = bool1;
        } else {
          bool4 = false;
        }
        return bool4;
      }
      if (((paramRect1.bottom > paramRect2.bottom) || (paramRect1.top >= paramRect2.bottom)) && (paramRect1.top > paramRect2.top)) {
        bool4 = bool2;
      } else {
        bool4 = false;
      }
      return bool4;
    }
    if (((paramRect1.right > paramRect2.right) || (paramRect1.left >= paramRect2.right)) && (paramRect1.left > paramRect2.left)) {
      bool4 = bool3;
    } else {
      bool4 = false;
    }
    return bool4;
  }
  
  boolean isToDirectionOf(int paramInt, Rect paramRect1, Rect paramRect2)
  {
    boolean bool1 = true;
    boolean bool2 = true;
    boolean bool3 = true;
    boolean bool4 = true;
    if (paramInt != 17)
    {
      if (paramInt != 33)
      {
        if (paramInt != 66)
        {
          if (paramInt == 130)
          {
            if (paramRect1.bottom > paramRect2.top) {
              bool4 = false;
            }
            return bool4;
          }
          throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
        }
        if (paramRect1.right <= paramRect2.left) {
          bool4 = bool1;
        } else {
          bool4 = false;
        }
        return bool4;
      }
      if (paramRect1.top >= paramRect2.bottom) {
        bool4 = bool2;
      } else {
        bool4 = false;
      }
      return bool4;
    }
    if (paramRect1.left >= paramRect2.right) {
      bool4 = bool3;
    } else {
      bool4 = false;
    }
    return bool4;
  }
  
  static final class FocusSorter
  {
    private int mLastPoolRect;
    private HashMap<View, Rect> mRectByView = null;
    private ArrayList<Rect> mRectPool = new ArrayList();
    private int mRtlMult;
    private Comparator<View> mSidesComparator = new _..Lambda.FocusFinder.FocusSorter.h0f2ZYL6peSaaEeCCkAoYs_YZvU(this);
    private Comparator<View> mTopsComparator = new _..Lambda.FocusFinder.FocusSorter.kW7K1t9q7Y62V38r_7g6xRzqqq8(this);
    
    public void sort(View[] paramArrayOfView, int paramInt1, int paramInt2, ViewGroup paramViewGroup, boolean paramBoolean)
    {
      int i = paramInt2 - paramInt1;
      if (i < 2) {
        return;
      }
      if (this.mRectByView == null) {
        this.mRectByView = new HashMap();
      }
      if (paramBoolean) {
        j = -1;
      } else {
        j = 1;
      }
      this.mRtlMult = j;
      for (int j = this.mRectPool.size(); j < i; j++) {
        this.mRectPool.add(new Rect());
      }
      for (j = paramInt1; j < paramInt2; j++)
      {
        Object localObject = this.mRectPool;
        k = this.mLastPoolRect;
        this.mLastPoolRect = (k + 1);
        localObject = (Rect)((ArrayList)localObject).get(k);
        paramArrayOfView[j].getDrawingRect((Rect)localObject);
        paramViewGroup.offsetDescendantRectToMyCoords(paramArrayOfView[j], (Rect)localObject);
        this.mRectByView.put(paramArrayOfView[j], localObject);
      }
      Arrays.sort(paramArrayOfView, paramInt1, i, this.mTopsComparator);
      int k = ((Rect)this.mRectByView.get(paramArrayOfView[paramInt1])).bottom;
      j = paramInt1;
      paramInt1++;
      while (paramInt1 < paramInt2)
      {
        paramViewGroup = (Rect)this.mRectByView.get(paramArrayOfView[paramInt1]);
        if (paramViewGroup.top >= k)
        {
          if (paramInt1 - j > 1) {
            Arrays.sort(paramArrayOfView, j, paramInt1, this.mSidesComparator);
          }
          j = paramViewGroup.bottom;
          i = paramInt1;
        }
        else
        {
          k = Math.max(k, paramViewGroup.bottom);
          i = j;
          j = k;
        }
        paramInt1++;
        k = j;
        j = i;
      }
      if (paramInt1 - j > 1) {
        Arrays.sort(paramArrayOfView, j, paramInt1, this.mSidesComparator);
      }
      this.mLastPoolRect = 0;
      this.mRectByView.clear();
    }
  }
  
  private static final class UserSpecifiedFocusComparator
    implements Comparator<View>
  {
    private final ArrayMap<View, View> mHeadsOfChains = new ArrayMap();
    private final ArraySet<View> mIsConnectedTo = new ArraySet();
    private final ArrayMap<View, View> mNextFoci = new ArrayMap();
    private final NextFocusGetter mNextFocusGetter;
    private final ArrayMap<View, Integer> mOriginalOrdinal = new ArrayMap();
    private View mRoot;
    
    UserSpecifiedFocusComparator(NextFocusGetter paramNextFocusGetter)
    {
      this.mNextFocusGetter = paramNextFocusGetter;
    }
    
    private void setHeadOfChain(View paramView)
    {
      View localView1 = paramView;
      while (localView1 != null)
      {
        View localView2 = (View)this.mHeadsOfChains.get(localView1);
        View localView3 = paramView;
        if (localView2 != null)
        {
          if (localView2 == paramView) {
            return;
          }
          localView3 = localView2;
          localView1 = paramView;
        }
        this.mHeadsOfChains.put(localView1, localView3);
        localView1 = (View)this.mNextFoci.get(localView1);
        paramView = localView3;
      }
    }
    
    public int compare(View paramView1, View paramView2)
    {
      if (paramView1 == paramView2) {
        return 0;
      }
      View localView1 = (View)this.mHeadsOfChains.get(paramView1);
      View localView2 = (View)this.mHeadsOfChains.get(paramView2);
      int i = 1;
      if ((localView1 == localView2) && (localView1 != null))
      {
        if (paramView1 == localView1) {
          return -1;
        }
        if (paramView2 == localView1) {
          return 1;
        }
        if (this.mNextFoci.get(paramView1) != null) {
          return -1;
        }
        return 1;
      }
      int j = 0;
      if (localView1 != null)
      {
        paramView1 = localView1;
        j = 1;
      }
      if (localView2 != null)
      {
        paramView2 = localView2;
        j = 1;
      }
      if (j != 0)
      {
        j = i;
        if (((Integer)this.mOriginalOrdinal.get(paramView1)).intValue() < ((Integer)this.mOriginalOrdinal.get(paramView2)).intValue()) {
          j = -1;
        }
        return j;
      }
      return 0;
    }
    
    public void recycle()
    {
      this.mRoot = null;
      this.mHeadsOfChains.clear();
      this.mIsConnectedTo.clear();
      this.mOriginalOrdinal.clear();
      this.mNextFoci.clear();
    }
    
    public void setFocusables(List<View> paramList, View paramView)
    {
      this.mRoot = paramView;
      for (int i = 0; i < paramList.size(); i++) {
        this.mOriginalOrdinal.put((View)paramList.get(i), Integer.valueOf(i));
      }
      for (i = paramList.size() - 1; i >= 0; i--)
      {
        View localView = (View)paramList.get(i);
        paramView = this.mNextFocusGetter.get(this.mRoot, localView);
        if ((paramView != null) && (this.mOriginalOrdinal.containsKey(paramView)))
        {
          this.mNextFoci.put(localView, paramView);
          this.mIsConnectedTo.add(paramView);
        }
      }
      for (i = paramList.size() - 1; i >= 0; i--)
      {
        paramView = (View)paramList.get(i);
        if (((View)this.mNextFoci.get(paramView) != null) && (!this.mIsConnectedTo.contains(paramView))) {
          setHeadOfChain(paramView);
        }
      }
    }
    
    public static abstract interface NextFocusGetter
    {
      public abstract View get(View paramView1, View paramView2);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/FocusFinder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */