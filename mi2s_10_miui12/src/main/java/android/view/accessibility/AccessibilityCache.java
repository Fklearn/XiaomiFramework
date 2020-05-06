package android.view.accessibility;

import android.os.Build;
import android.util.ArraySet;
import android.util.Log;
import android.util.LongArray;
import android.util.LongSparseArray;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.List;

public class AccessibilityCache
{
  public static final int CACHE_CRITICAL_EVENTS_MASK = 4307005;
  private static final boolean CHECK_INTEGRITY = Build.IS_ENG;
  private static final boolean DEBUG = false;
  private static final String LOG_TAG = "AccessibilityCache";
  private long mAccessibilityFocus = 2147483647L;
  private final AccessibilityNodeRefresher mAccessibilityNodeRefresher;
  private long mInputFocus = 2147483647L;
  private boolean mIsAllWindowsCached;
  private final Object mLock = new Object();
  private final SparseArray<LongSparseArray<AccessibilityNodeInfo>> mNodeCache = new SparseArray();
  private final SparseArray<AccessibilityWindowInfo> mTempWindowArray = new SparseArray();
  private final SparseArray<AccessibilityWindowInfo> mWindowCache = new SparseArray();
  
  public AccessibilityCache(AccessibilityNodeRefresher paramAccessibilityNodeRefresher)
  {
    this.mAccessibilityNodeRefresher = paramAccessibilityNodeRefresher;
  }
  
  private void clearNodesForWindowLocked(int paramInt)
  {
    if ((LongSparseArray)this.mNodeCache.get(paramInt) == null) {
      return;
    }
    this.mNodeCache.remove(paramInt);
  }
  
  private void clearSubTreeLocked(int paramInt, long paramLong)
  {
    LongSparseArray localLongSparseArray = (LongSparseArray)this.mNodeCache.get(paramInt);
    if (localLongSparseArray != null) {
      clearSubTreeRecursiveLocked(localLongSparseArray, paramLong);
    }
  }
  
  private boolean clearSubTreeRecursiveLocked(LongSparseArray<AccessibilityNodeInfo> paramLongSparseArray, long paramLong)
  {
    AccessibilityNodeInfo localAccessibilityNodeInfo = (AccessibilityNodeInfo)paramLongSparseArray.get(paramLong);
    if (localAccessibilityNodeInfo == null)
    {
      clear();
      return true;
    }
    paramLongSparseArray.remove(paramLong);
    int i = localAccessibilityNodeInfo.getChildCount();
    for (int j = 0; j < i; j++) {
      if (clearSubTreeRecursiveLocked(paramLongSparseArray, localAccessibilityNodeInfo.getChildId(j))) {
        return true;
      }
    }
    return false;
  }
  
  private void clearWindowCache()
  {
    this.mWindowCache.clear();
    this.mIsAllWindowsCached = false;
  }
  
  private void refreshCachedNodeLocked(int paramInt, long paramLong)
  {
    Object localObject = (LongSparseArray)this.mNodeCache.get(paramInt);
    if (localObject == null) {
      return;
    }
    localObject = (AccessibilityNodeInfo)((LongSparseArray)localObject).get(paramLong);
    if (localObject == null) {
      return;
    }
    if (this.mAccessibilityNodeRefresher.refreshNode((AccessibilityNodeInfo)localObject, true)) {
      return;
    }
    clearSubTreeLocked(paramInt, paramLong);
  }
  
  public void add(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    synchronized (this.mLock)
    {
      int i = paramAccessibilityNodeInfo.getWindowId();
      Object localObject2 = (LongSparseArray)this.mNodeCache.get(i);
      Object localObject3 = localObject2;
      if (localObject2 == null)
      {
        localObject3 = new android/util/LongSparseArray;
        ((LongSparseArray)localObject3).<init>();
        this.mNodeCache.put(i, localObject3);
      }
      long l1 = paramAccessibilityNodeInfo.getSourceNodeId();
      AccessibilityNodeInfo localAccessibilityNodeInfo = (AccessibilityNodeInfo)((LongSparseArray)localObject3).get(l1);
      if (localAccessibilityNodeInfo != null)
      {
        localObject2 = paramAccessibilityNodeInfo.getChildNodeIds();
        int j = localAccessibilityNodeInfo.getChildCount();
        for (int k = 0; k < j; k++)
        {
          l2 = localAccessibilityNodeInfo.getChildId(k);
          if ((localObject2 == null) || (((LongArray)localObject2).indexOf(l2) < 0)) {
            clearSubTreeLocked(i, l2);
          }
          if (((LongSparseArray)localObject3).get(l1) == null)
          {
            clearNodesForWindowLocked(i);
            return;
          }
        }
        long l2 = localAccessibilityNodeInfo.getParentNodeId();
        if (paramAccessibilityNodeInfo.getParentNodeId() != l2) {
          clearSubTreeLocked(i, l2);
        }
      }
      localObject2 = new android/view/accessibility/AccessibilityNodeInfo;
      ((AccessibilityNodeInfo)localObject2).<init>(paramAccessibilityNodeInfo);
      ((LongSparseArray)localObject3).put(l1, localObject2);
      if (((AccessibilityNodeInfo)localObject2).isAccessibilityFocused())
      {
        if ((this.mAccessibilityFocus != 2147483647L) && (this.mAccessibilityFocus != l1)) {
          refreshCachedNodeLocked(i, this.mAccessibilityFocus);
        }
        this.mAccessibilityFocus = l1;
      }
      else if (this.mAccessibilityFocus == l1)
      {
        this.mAccessibilityFocus = 2147483647L;
      }
      if (((AccessibilityNodeInfo)localObject2).isFocused()) {
        this.mInputFocus = l1;
      }
      return;
    }
  }
  
  public void addWindow(AccessibilityWindowInfo paramAccessibilityWindowInfo)
  {
    synchronized (this.mLock)
    {
      int i = paramAccessibilityWindowInfo.getId();
      SparseArray localSparseArray = this.mWindowCache;
      AccessibilityWindowInfo localAccessibilityWindowInfo = new android/view/accessibility/AccessibilityWindowInfo;
      localAccessibilityWindowInfo.<init>(paramAccessibilityWindowInfo);
      localSparseArray.put(i, localAccessibilityWindowInfo);
      return;
    }
  }
  
  public void checkIntegrity()
  {
    Object localObject1 = this;
    synchronized (((AccessibilityCache)localObject1).mLock)
    {
      if ((((AccessibilityCache)localObject1).mWindowCache.size() <= 0) && (((AccessibilityCache)localObject1).mNodeCache.size() == 0)) {
        return;
      }
      Object localObject3 = null;
      Object localObject4 = null;
      int i = ((AccessibilityCache)localObject1).mWindowCache.size();
      int j = 0;
      while (j < i)
      {
        localObject6 = (AccessibilityWindowInfo)((AccessibilityCache)localObject1).mWindowCache.valueAt(j);
        localObject7 = localObject4;
        if (((AccessibilityWindowInfo)localObject6).isActive()) {
          if (localObject4 != null)
          {
            localObject7 = new java/lang/StringBuilder;
            ((StringBuilder)localObject7).<init>();
            ((StringBuilder)localObject7).append("Duplicate active window:");
            ((StringBuilder)localObject7).append(localObject6);
            Log.e("AccessibilityCache", ((StringBuilder)localObject7).toString());
            localObject7 = localObject4;
          }
          else
          {
            localObject7 = localObject6;
          }
        }
        localObject4 = localObject3;
        if (((AccessibilityWindowInfo)localObject6).isFocused()) {
          if (localObject3 != null)
          {
            localObject4 = new java/lang/StringBuilder;
            ((StringBuilder)localObject4).<init>();
            ((StringBuilder)localObject4).append("Duplicate focused window:");
            ((StringBuilder)localObject4).append(localObject6);
            Log.e("AccessibilityCache", ((StringBuilder)localObject4).toString());
            localObject4 = localObject3;
          }
          else
          {
            localObject4 = localObject6;
          }
        }
        j++;
        localObject3 = localObject4;
        localObject4 = localObject7;
      }
      Object localObject7 = null;
      Object localObject6 = null;
      int k = ((AccessibilityCache)localObject1).mNodeCache.size();
      int m = 0;
      j = i;
      for (;;)
      {
        localObject1 = this;
        if (m >= k) {
          break;
        }
        LongSparseArray localLongSparseArray = (LongSparseArray)((AccessibilityCache)localObject1).mNodeCache.valueAt(m);
        if (localLongSparseArray.size() > 0)
        {
          ArraySet localArraySet = new android/util/ArraySet;
          localArraySet.<init>();
          int n = ((AccessibilityCache)localObject1).mNodeCache.keyAt(m);
          int i1 = localLongSparseArray.size();
          for (i = 0; i < i1; i++)
          {
            AccessibilityNodeInfo localAccessibilityNodeInfo1 = (AccessibilityNodeInfo)localLongSparseArray.valueAt(i);
            if (!localArraySet.add(localAccessibilityNodeInfo1))
            {
              localObject1 = new java/lang/StringBuilder;
              ((StringBuilder)localObject1).<init>();
              ((StringBuilder)localObject1).append("Duplicate node: ");
              ((StringBuilder)localObject1).append(localAccessibilityNodeInfo1);
              ((StringBuilder)localObject1).append(" in window:");
              ((StringBuilder)localObject1).append(n);
              Log.e("AccessibilityCache", ((StringBuilder)localObject1).toString());
            }
            else
            {
              localObject1 = localObject7;
              if (localAccessibilityNodeInfo1.isAccessibilityFocused()) {
                if (localObject7 != null)
                {
                  localObject1 = new java/lang/StringBuilder;
                  ((StringBuilder)localObject1).<init>();
                  ((StringBuilder)localObject1).append("Duplicate accessibility focus:");
                  ((StringBuilder)localObject1).append(localAccessibilityNodeInfo1);
                  ((StringBuilder)localObject1).append(" in window:");
                  ((StringBuilder)localObject1).append(n);
                  Log.e("AccessibilityCache", ((StringBuilder)localObject1).toString());
                  localObject1 = localObject7;
                }
                else
                {
                  localObject1 = localAccessibilityNodeInfo1;
                }
              }
              localObject7 = localObject6;
              if (localAccessibilityNodeInfo1.isFocused()) {
                if (localObject6 != null)
                {
                  localObject7 = new java/lang/StringBuilder;
                  ((StringBuilder)localObject7).<init>();
                  ((StringBuilder)localObject7).append("Duplicate input focus: ");
                  ((StringBuilder)localObject7).append(localAccessibilityNodeInfo1);
                  ((StringBuilder)localObject7).append(" in window:");
                  ((StringBuilder)localObject7).append(n);
                  Log.e("AccessibilityCache", ((StringBuilder)localObject7).toString());
                  localObject7 = localObject6;
                }
                else
                {
                  localObject7 = localAccessibilityNodeInfo1;
                }
              }
              AccessibilityNodeInfo localAccessibilityNodeInfo2 = (AccessibilityNodeInfo)localLongSparseArray.get(localAccessibilityNodeInfo1.getParentNodeId());
              if (localAccessibilityNodeInfo2 != null)
              {
                int i2 = localAccessibilityNodeInfo2.getChildCount();
                i3 = 0;
                for (i4 = 0; i4 < i2; i4++) {
                  if ((AccessibilityNodeInfo)localLongSparseArray.get(localAccessibilityNodeInfo2.getChildId(i4)) == localAccessibilityNodeInfo1)
                  {
                    i4 = 1;
                    break label603;
                  }
                }
                i4 = i3;
                label603:
                localObject6 = localObject4;
                i3 = j;
                j = i3;
                localObject4 = localObject6;
                if (i4 == 0)
                {
                  localObject4 = new java/lang/StringBuilder;
                  ((StringBuilder)localObject4).<init>();
                  ((StringBuilder)localObject4).append("Invalid parent-child relation between parent: ");
                  ((StringBuilder)localObject4).append(localAccessibilityNodeInfo2);
                  ((StringBuilder)localObject4).append(" and child: ");
                  ((StringBuilder)localObject4).append(localAccessibilityNodeInfo1);
                  Log.e("AccessibilityCache", ((StringBuilder)localObject4).toString());
                  j = i3;
                  localObject4 = localObject6;
                }
              }
              int i3 = localAccessibilityNodeInfo1.getChildCount();
              for (int i4 = 0; i4 < i3; i4++)
              {
                localObject6 = (AccessibilityNodeInfo)localLongSparseArray.get(localAccessibilityNodeInfo1.getChildId(i4));
                if (localObject6 != null) {
                  if ((AccessibilityNodeInfo)localLongSparseArray.get(((AccessibilityNodeInfo)localObject6).getParentNodeId()) != localAccessibilityNodeInfo1)
                  {
                    localObject6 = new java/lang/StringBuilder;
                    ((StringBuilder)localObject6).<init>();
                    ((StringBuilder)localObject6).append("Invalid child-parent relation between child: ");
                    ((StringBuilder)localObject6).append(localAccessibilityNodeInfo1);
                    ((StringBuilder)localObject6).append(" and parent: ");
                    ((StringBuilder)localObject6).append(localAccessibilityNodeInfo2);
                    Log.e("AccessibilityCache", ((StringBuilder)localObject6).toString());
                  }
                  else {}
                }
              }
              localObject6 = localObject7;
              localObject7 = localObject1;
            }
          }
        }
        m++;
      }
      return;
    }
  }
  
  public void clear()
  {
    synchronized (this.mLock)
    {
      clearWindowCache();
      for (int i = this.mNodeCache.size() - 1; i >= 0; i--) {
        clearNodesForWindowLocked(this.mNodeCache.keyAt(i));
      }
      this.mAccessibilityFocus = 2147483647L;
      this.mInputFocus = 2147483647L;
      return;
    }
  }
  
  public AccessibilityNodeInfo getNode(int paramInt, long paramLong)
  {
    synchronized (this.mLock)
    {
      Object localObject2 = (LongSparseArray)this.mNodeCache.get(paramInt);
      if (localObject2 == null) {
        return null;
      }
      AccessibilityNodeInfo localAccessibilityNodeInfo = (AccessibilityNodeInfo)((LongSparseArray)localObject2).get(paramLong);
      localObject2 = localAccessibilityNodeInfo;
      if (localAccessibilityNodeInfo != null)
      {
        localObject2 = new android/view/accessibility/AccessibilityNodeInfo;
        ((AccessibilityNodeInfo)localObject2).<init>(localAccessibilityNodeInfo);
      }
      return (AccessibilityNodeInfo)localObject2;
    }
  }
  
  public AccessibilityWindowInfo getWindow(int paramInt)
  {
    synchronized (this.mLock)
    {
      AccessibilityWindowInfo localAccessibilityWindowInfo1 = (AccessibilityWindowInfo)this.mWindowCache.get(paramInt);
      if (localAccessibilityWindowInfo1 != null)
      {
        AccessibilityWindowInfo localAccessibilityWindowInfo2 = new android/view/accessibility/AccessibilityWindowInfo;
        localAccessibilityWindowInfo2.<init>(localAccessibilityWindowInfo1);
        return localAccessibilityWindowInfo2;
      }
      return null;
    }
  }
  
  public List<AccessibilityWindowInfo> getWindows()
  {
    synchronized (this.mLock)
    {
      if (!this.mIsAllWindowsCached) {
        return null;
      }
      int i = this.mWindowCache.size();
      if (i > 0)
      {
        SparseArray localSparseArray = this.mTempWindowArray;
        localSparseArray.clear();
        AccessibilityWindowInfo localAccessibilityWindowInfo1;
        for (int j = 0; j < i; j++)
        {
          localAccessibilityWindowInfo1 = (AccessibilityWindowInfo)this.mWindowCache.valueAt(j);
          localSparseArray.put(localAccessibilityWindowInfo1.getLayer(), localAccessibilityWindowInfo1);
        }
        j = localSparseArray.size();
        ArrayList localArrayList = new java/util/ArrayList;
        localArrayList.<init>(j);
        j--;
        while (j >= 0)
        {
          AccessibilityWindowInfo localAccessibilityWindowInfo2 = (AccessibilityWindowInfo)localSparseArray.valueAt(j);
          localAccessibilityWindowInfo1 = new android/view/accessibility/AccessibilityWindowInfo;
          localAccessibilityWindowInfo1.<init>(localAccessibilityWindowInfo2);
          localArrayList.add(localAccessibilityWindowInfo1);
          localSparseArray.removeAt(j);
          j--;
        }
        return localArrayList;
      }
      return null;
    }
  }
  
  public void onAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    synchronized (this.mLock)
    {
      switch (paramAccessibilityEvent.getEventType())
      {
      default: 
        break;
      case 65536: 
        if (this.mAccessibilityFocus == paramAccessibilityEvent.getSourceNodeId())
        {
          refreshCachedNodeLocked(paramAccessibilityEvent.getWindowId(), this.mAccessibilityFocus);
          this.mAccessibilityFocus = 2147483647L;
        }
        break;
      case 32768: 
        if (this.mAccessibilityFocus != 2147483647L) {
          refreshCachedNodeLocked(paramAccessibilityEvent.getWindowId(), this.mAccessibilityFocus);
        }
        this.mAccessibilityFocus = paramAccessibilityEvent.getSourceNodeId();
        refreshCachedNodeLocked(paramAccessibilityEvent.getWindowId(), this.mAccessibilityFocus);
        break;
      case 4096: 
        clearSubTreeLocked(paramAccessibilityEvent.getWindowId(), paramAccessibilityEvent.getSourceNodeId());
        break;
      case 2048: 
        synchronized (this.mLock)
        {
          int i = paramAccessibilityEvent.getWindowId();
          long l = paramAccessibilityEvent.getSourceNodeId();
          if ((paramAccessibilityEvent.getContentChangeTypes() & 0x1) != 0) {
            clearSubTreeLocked(i, l);
          } else {
            refreshCachedNodeLocked(i, l);
          }
        }
      case 32: 
      case 4194304: 
        clear();
        break;
      case 8: 
        if (this.mInputFocus != 2147483647L) {
          refreshCachedNodeLocked(paramAccessibilityEvent.getWindowId(), this.mInputFocus);
        }
        this.mInputFocus = paramAccessibilityEvent.getSourceNodeId();
        refreshCachedNodeLocked(paramAccessibilityEvent.getWindowId(), this.mInputFocus);
        break;
      case 1: 
      case 4: 
      case 16: 
      case 8192: 
        refreshCachedNodeLocked(paramAccessibilityEvent.getWindowId(), paramAccessibilityEvent.getSourceNodeId());
      }
      if (CHECK_INTEGRITY) {
        checkIntegrity();
      }
      return;
    }
  }
  
  public void setWindows(List<AccessibilityWindowInfo> paramList)
  {
    synchronized (this.mLock)
    {
      clearWindowCache();
      if (paramList == null) {
        return;
      }
      int i = paramList.size();
      for (int j = 0; j < i; j++) {
        addWindow((AccessibilityWindowInfo)paramList.get(j));
      }
      this.mIsAllWindowsCached = true;
      return;
    }
  }
  
  public static class AccessibilityNodeRefresher
  {
    public boolean refreshNode(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean)
    {
      return paramAccessibilityNodeInfo.refresh(null, paramBoolean);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/accessibility/AccessibilityCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */