package android.view;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class ViewTreeObserver
{
  private static boolean sIllegalOnDrawModificationIsFatal;
  private boolean mAlive;
  private CopyOnWriteArray<Consumer<List<Rect>>> mGestureExclusionListeners;
  private boolean mInDispatchOnDraw;
  @UnsupportedAppUsage
  private CopyOnWriteArray<OnComputeInternalInsetsListener> mOnComputeInternalInsetsListeners;
  private ArrayList<OnDrawListener> mOnDrawListeners;
  private CopyOnWriteArrayList<OnEnterAnimationCompleteListener> mOnEnterAnimationCompleteListeners;
  private ArrayList<Runnable> mOnFrameCommitListeners;
  private CopyOnWriteArrayList<OnGlobalFocusChangeListener> mOnGlobalFocusListeners;
  @UnsupportedAppUsage
  private CopyOnWriteArray<OnGlobalLayoutListener> mOnGlobalLayoutListeners;
  private CopyOnWriteArray<OnPreDrawListener> mOnPreDrawListeners;
  @UnsupportedAppUsage
  private CopyOnWriteArray<OnScrollChangedListener> mOnScrollChangedListeners;
  @UnsupportedAppUsage
  private CopyOnWriteArrayList<OnTouchModeChangeListener> mOnTouchModeChangeListeners;
  private CopyOnWriteArrayList<OnWindowAttachListener> mOnWindowAttachListeners;
  private CopyOnWriteArrayList<OnWindowFocusChangeListener> mOnWindowFocusListeners;
  private CopyOnWriteArray<OnWindowShownListener> mOnWindowShownListeners;
  private boolean mWindowShown;
  
  ViewTreeObserver(Context paramContext)
  {
    boolean bool = true;
    this.mAlive = true;
    if (paramContext.getApplicationInfo().targetSdkVersion < 26) {
      bool = false;
    }
    sIllegalOnDrawModificationIsFatal = bool;
  }
  
  private void checkIsAlive()
  {
    if (this.mAlive) {
      return;
    }
    throw new IllegalStateException("This ViewTreeObserver is not alive, call getViewTreeObserver() again");
  }
  
  private void kill()
  {
    this.mAlive = false;
  }
  
  @UnsupportedAppUsage
  public void addOnComputeInternalInsetsListener(OnComputeInternalInsetsListener paramOnComputeInternalInsetsListener)
  {
    checkIsAlive();
    if (this.mOnComputeInternalInsetsListeners == null) {
      this.mOnComputeInternalInsetsListeners = new CopyOnWriteArray();
    }
    this.mOnComputeInternalInsetsListeners.add(paramOnComputeInternalInsetsListener);
  }
  
  public void addOnDrawListener(OnDrawListener paramOnDrawListener)
  {
    checkIsAlive();
    if (this.mOnDrawListeners == null) {
      this.mOnDrawListeners = new ArrayList();
    }
    if (this.mInDispatchOnDraw)
    {
      IllegalStateException localIllegalStateException = new IllegalStateException("Cannot call addOnDrawListener inside of onDraw");
      if (!sIllegalOnDrawModificationIsFatal) {
        Log.e("ViewTreeObserver", localIllegalStateException.getMessage(), localIllegalStateException);
      } else {
        throw localIllegalStateException;
      }
    }
    this.mOnDrawListeners.add(paramOnDrawListener);
  }
  
  public void addOnEnterAnimationCompleteListener(OnEnterAnimationCompleteListener paramOnEnterAnimationCompleteListener)
  {
    checkIsAlive();
    if (this.mOnEnterAnimationCompleteListeners == null) {
      this.mOnEnterAnimationCompleteListeners = new CopyOnWriteArrayList();
    }
    this.mOnEnterAnimationCompleteListeners.add(paramOnEnterAnimationCompleteListener);
  }
  
  public void addOnGlobalFocusChangeListener(OnGlobalFocusChangeListener paramOnGlobalFocusChangeListener)
  {
    checkIsAlive();
    if (this.mOnGlobalFocusListeners == null) {
      this.mOnGlobalFocusListeners = new CopyOnWriteArrayList();
    }
    this.mOnGlobalFocusListeners.add(paramOnGlobalFocusChangeListener);
  }
  
  public void addOnGlobalLayoutListener(OnGlobalLayoutListener paramOnGlobalLayoutListener)
  {
    checkIsAlive();
    if (this.mOnGlobalLayoutListeners == null) {
      this.mOnGlobalLayoutListeners = new CopyOnWriteArray();
    }
    this.mOnGlobalLayoutListeners.add(paramOnGlobalLayoutListener);
  }
  
  public void addOnPreDrawListener(OnPreDrawListener paramOnPreDrawListener)
  {
    checkIsAlive();
    if (this.mOnPreDrawListeners == null) {
      this.mOnPreDrawListeners = new CopyOnWriteArray();
    }
    this.mOnPreDrawListeners.add(paramOnPreDrawListener);
  }
  
  public void addOnScrollChangedListener(OnScrollChangedListener paramOnScrollChangedListener)
  {
    checkIsAlive();
    if (this.mOnScrollChangedListeners == null) {
      this.mOnScrollChangedListeners = new CopyOnWriteArray();
    }
    this.mOnScrollChangedListeners.add(paramOnScrollChangedListener);
  }
  
  public void addOnSystemGestureExclusionRectsChangedListener(Consumer<List<Rect>> paramConsumer)
  {
    checkIsAlive();
    if (this.mGestureExclusionListeners == null) {
      this.mGestureExclusionListeners = new CopyOnWriteArray();
    }
    this.mGestureExclusionListeners.add(paramConsumer);
  }
  
  public void addOnTouchModeChangeListener(OnTouchModeChangeListener paramOnTouchModeChangeListener)
  {
    checkIsAlive();
    if (this.mOnTouchModeChangeListeners == null) {
      this.mOnTouchModeChangeListeners = new CopyOnWriteArrayList();
    }
    this.mOnTouchModeChangeListeners.add(paramOnTouchModeChangeListener);
  }
  
  public void addOnWindowAttachListener(OnWindowAttachListener paramOnWindowAttachListener)
  {
    checkIsAlive();
    if (this.mOnWindowAttachListeners == null) {
      this.mOnWindowAttachListeners = new CopyOnWriteArrayList();
    }
    this.mOnWindowAttachListeners.add(paramOnWindowAttachListener);
  }
  
  public void addOnWindowFocusChangeListener(OnWindowFocusChangeListener paramOnWindowFocusChangeListener)
  {
    checkIsAlive();
    if (this.mOnWindowFocusListeners == null) {
      this.mOnWindowFocusListeners = new CopyOnWriteArrayList();
    }
    this.mOnWindowFocusListeners.add(paramOnWindowFocusChangeListener);
  }
  
  public void addOnWindowShownListener(OnWindowShownListener paramOnWindowShownListener)
  {
    checkIsAlive();
    if (this.mOnWindowShownListeners == null) {
      this.mOnWindowShownListeners = new CopyOnWriteArray();
    }
    this.mOnWindowShownListeners.add(paramOnWindowShownListener);
    if (this.mWindowShown) {
      paramOnWindowShownListener.onWindowShown();
    }
  }
  
  ArrayList<Runnable> captureFrameCommitCallbacks()
  {
    ArrayList localArrayList = this.mOnFrameCommitListeners;
    this.mOnFrameCommitListeners = null;
    return localArrayList;
  }
  
  @UnsupportedAppUsage
  final void dispatchOnComputeInternalInsets(InternalInsetsInfo paramInternalInsetsInfo)
  {
    CopyOnWriteArray localCopyOnWriteArray = this.mOnComputeInternalInsetsListeners;
    if ((localCopyOnWriteArray != null) && (localCopyOnWriteArray.size() > 0))
    {
      ViewTreeObserver.CopyOnWriteArray.Access localAccess = localCopyOnWriteArray.start();
      try
      {
        int i = localAccess.size();
        for (int j = 0; j < i; j++) {
          ((OnComputeInternalInsetsListener)localAccess.get(j)).onComputeInternalInsets(paramInternalInsetsInfo);
        }
      }
      finally
      {
        localCopyOnWriteArray.end();
      }
    }
  }
  
  public final void dispatchOnDraw()
  {
    if (this.mOnDrawListeners != null)
    {
      this.mInDispatchOnDraw = true;
      ArrayList localArrayList = this.mOnDrawListeners;
      int i = localArrayList.size();
      for (int j = 0; j < i; j++) {
        ((OnDrawListener)localArrayList.get(j)).onDraw();
      }
      this.mInDispatchOnDraw = false;
    }
  }
  
  public final void dispatchOnEnterAnimationComplete()
  {
    Object localObject = this.mOnEnterAnimationCompleteListeners;
    if ((localObject != null) && (!((CopyOnWriteArrayList)localObject).isEmpty()))
    {
      localObject = ((CopyOnWriteArrayList)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        ((OnEnterAnimationCompleteListener)((Iterator)localObject).next()).onEnterAnimationComplete();
      }
    }
  }
  
  @UnsupportedAppUsage
  final void dispatchOnGlobalFocusChange(View paramView1, View paramView2)
  {
    Object localObject = this.mOnGlobalFocusListeners;
    if ((localObject != null) && (((CopyOnWriteArrayList)localObject).size() > 0))
    {
      localObject = ((CopyOnWriteArrayList)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        ((OnGlobalFocusChangeListener)((Iterator)localObject).next()).onGlobalFocusChanged(paramView1, paramView2);
      }
    }
  }
  
  public final void dispatchOnGlobalLayout()
  {
    CopyOnWriteArray localCopyOnWriteArray = this.mOnGlobalLayoutListeners;
    if ((localCopyOnWriteArray != null) && (localCopyOnWriteArray.size() > 0))
    {
      ViewTreeObserver.CopyOnWriteArray.Access localAccess = localCopyOnWriteArray.start();
      try
      {
        int i = localAccess.size();
        for (int j = 0; j < i; j++) {
          ((OnGlobalLayoutListener)localAccess.get(j)).onGlobalLayout();
        }
      }
      finally
      {
        localCopyOnWriteArray.end();
      }
    }
  }
  
  public final boolean dispatchOnPreDraw()
  {
    boolean bool1 = false;
    boolean bool2 = false;
    CopyOnWriteArray localCopyOnWriteArray = this.mOnPreDrawListeners;
    boolean bool3 = bool1;
    if (localCopyOnWriteArray != null)
    {
      bool3 = bool1;
      if (localCopyOnWriteArray.size() > 0)
      {
        ViewTreeObserver.CopyOnWriteArray.Access localAccess = localCopyOnWriteArray.start();
        try
        {
          int i = localAccess.size();
          int j = 0;
          bool3 = bool2;
          while (j < i)
          {
            bool2 = ((OnPreDrawListener)localAccess.get(j)).onPreDraw();
            bool3 |= bool2 ^ true;
            j++;
          }
        }
        finally
        {
          localCopyOnWriteArray.end();
        }
      }
    }
    return bool3;
  }
  
  @UnsupportedAppUsage
  final void dispatchOnScrollChanged()
  {
    CopyOnWriteArray localCopyOnWriteArray = this.mOnScrollChangedListeners;
    if ((localCopyOnWriteArray != null) && (localCopyOnWriteArray.size() > 0))
    {
      ViewTreeObserver.CopyOnWriteArray.Access localAccess = localCopyOnWriteArray.start();
      try
      {
        int i = localAccess.size();
        for (int j = 0; j < i; j++) {
          ((OnScrollChangedListener)localAccess.get(j)).onScrollChanged();
        }
      }
      finally
      {
        localCopyOnWriteArray.end();
      }
    }
  }
  
  void dispatchOnSystemGestureExclusionRectsChanged(List<Rect> paramList)
  {
    CopyOnWriteArray localCopyOnWriteArray = this.mGestureExclusionListeners;
    if ((localCopyOnWriteArray != null) && (localCopyOnWriteArray.size() > 0))
    {
      ViewTreeObserver.CopyOnWriteArray.Access localAccess = localCopyOnWriteArray.start();
      try
      {
        int i = localAccess.size();
        for (int j = 0; j < i; j++) {
          ((Consumer)localAccess.get(j)).accept(paramList);
        }
      }
      finally
      {
        localCopyOnWriteArray.end();
      }
    }
  }
  
  @UnsupportedAppUsage
  final void dispatchOnTouchModeChanged(boolean paramBoolean)
  {
    Object localObject = this.mOnTouchModeChangeListeners;
    if ((localObject != null) && (((CopyOnWriteArrayList)localObject).size() > 0))
    {
      localObject = ((CopyOnWriteArrayList)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        ((OnTouchModeChangeListener)((Iterator)localObject).next()).onTouchModeChanged(paramBoolean);
      }
    }
  }
  
  final void dispatchOnWindowAttachedChange(boolean paramBoolean)
  {
    Object localObject = this.mOnWindowAttachListeners;
    if ((localObject != null) && (((CopyOnWriteArrayList)localObject).size() > 0))
    {
      localObject = ((CopyOnWriteArrayList)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        OnWindowAttachListener localOnWindowAttachListener = (OnWindowAttachListener)((Iterator)localObject).next();
        if (paramBoolean) {
          localOnWindowAttachListener.onWindowAttached();
        } else {
          localOnWindowAttachListener.onWindowDetached();
        }
      }
    }
  }
  
  final void dispatchOnWindowFocusChange(boolean paramBoolean)
  {
    Object localObject = this.mOnWindowFocusListeners;
    if ((localObject != null) && (((CopyOnWriteArrayList)localObject).size() > 0))
    {
      localObject = ((CopyOnWriteArrayList)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        ((OnWindowFocusChangeListener)((Iterator)localObject).next()).onWindowFocusChanged(paramBoolean);
      }
    }
  }
  
  public final void dispatchOnWindowShown()
  {
    this.mWindowShown = true;
    CopyOnWriteArray localCopyOnWriteArray = this.mOnWindowShownListeners;
    if ((localCopyOnWriteArray != null) && (localCopyOnWriteArray.size() > 0))
    {
      ViewTreeObserver.CopyOnWriteArray.Access localAccess = localCopyOnWriteArray.start();
      try
      {
        int i = localAccess.size();
        for (int j = 0; j < i; j++) {
          ((OnWindowShownListener)localAccess.get(j)).onWindowShown();
        }
      }
      finally
      {
        localCopyOnWriteArray.end();
      }
    }
  }
  
  @UnsupportedAppUsage
  final boolean hasComputeInternalInsetsListeners()
  {
    CopyOnWriteArray localCopyOnWriteArray = this.mOnComputeInternalInsetsListeners;
    boolean bool;
    if ((localCopyOnWriteArray != null) && (localCopyOnWriteArray.size() > 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  final boolean hasOnPreDrawListeners()
  {
    CopyOnWriteArray localCopyOnWriteArray = this.mOnPreDrawListeners;
    boolean bool;
    if ((localCopyOnWriteArray != null) && (localCopyOnWriteArray.size() > 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isAlive()
  {
    return this.mAlive;
  }
  
  void merge(ViewTreeObserver paramViewTreeObserver)
  {
    Object localObject1 = paramViewTreeObserver.mOnWindowAttachListeners;
    if (localObject1 != null)
    {
      localObject2 = this.mOnWindowAttachListeners;
      if (localObject2 != null) {
        ((CopyOnWriteArrayList)localObject2).addAll((Collection)localObject1);
      } else {
        this.mOnWindowAttachListeners = ((CopyOnWriteArrayList)localObject1);
      }
    }
    Object localObject2 = paramViewTreeObserver.mOnWindowFocusListeners;
    if (localObject2 != null)
    {
      localObject1 = this.mOnWindowFocusListeners;
      if (localObject1 != null) {
        ((CopyOnWriteArrayList)localObject1).addAll((Collection)localObject2);
      } else {
        this.mOnWindowFocusListeners = ((CopyOnWriteArrayList)localObject2);
      }
    }
    localObject2 = paramViewTreeObserver.mOnGlobalFocusListeners;
    if (localObject2 != null)
    {
      localObject1 = this.mOnGlobalFocusListeners;
      if (localObject1 != null) {
        ((CopyOnWriteArrayList)localObject1).addAll((Collection)localObject2);
      } else {
        this.mOnGlobalFocusListeners = ((CopyOnWriteArrayList)localObject2);
      }
    }
    localObject2 = paramViewTreeObserver.mOnGlobalLayoutListeners;
    if (localObject2 != null)
    {
      localObject1 = this.mOnGlobalLayoutListeners;
      if (localObject1 != null) {
        ((CopyOnWriteArray)localObject1).addAll((CopyOnWriteArray)localObject2);
      } else {
        this.mOnGlobalLayoutListeners = ((CopyOnWriteArray)localObject2);
      }
    }
    localObject1 = paramViewTreeObserver.mOnPreDrawListeners;
    if (localObject1 != null)
    {
      localObject2 = this.mOnPreDrawListeners;
      if (localObject2 != null) {
        ((CopyOnWriteArray)localObject2).addAll((CopyOnWriteArray)localObject1);
      } else {
        this.mOnPreDrawListeners = ((CopyOnWriteArray)localObject1);
      }
    }
    localObject2 = paramViewTreeObserver.mOnDrawListeners;
    if (localObject2 != null)
    {
      localObject1 = this.mOnDrawListeners;
      if (localObject1 != null) {
        ((ArrayList)localObject1).addAll((Collection)localObject2);
      } else {
        this.mOnDrawListeners = ((ArrayList)localObject2);
      }
    }
    if (paramViewTreeObserver.mOnFrameCommitListeners != null)
    {
      localObject1 = this.mOnFrameCommitListeners;
      if (localObject1 != null) {
        ((ArrayList)localObject1).addAll(paramViewTreeObserver.captureFrameCommitCallbacks());
      } else {
        this.mOnFrameCommitListeners = paramViewTreeObserver.captureFrameCommitCallbacks();
      }
    }
    localObject2 = paramViewTreeObserver.mOnTouchModeChangeListeners;
    if (localObject2 != null)
    {
      localObject1 = this.mOnTouchModeChangeListeners;
      if (localObject1 != null) {
        ((CopyOnWriteArrayList)localObject1).addAll((Collection)localObject2);
      } else {
        this.mOnTouchModeChangeListeners = ((CopyOnWriteArrayList)localObject2);
      }
    }
    localObject2 = paramViewTreeObserver.mOnComputeInternalInsetsListeners;
    if (localObject2 != null)
    {
      localObject1 = this.mOnComputeInternalInsetsListeners;
      if (localObject1 != null) {
        ((CopyOnWriteArray)localObject1).addAll((CopyOnWriteArray)localObject2);
      } else {
        this.mOnComputeInternalInsetsListeners = ((CopyOnWriteArray)localObject2);
      }
    }
    localObject2 = paramViewTreeObserver.mOnScrollChangedListeners;
    if (localObject2 != null)
    {
      localObject1 = this.mOnScrollChangedListeners;
      if (localObject1 != null) {
        ((CopyOnWriteArray)localObject1).addAll((CopyOnWriteArray)localObject2);
      } else {
        this.mOnScrollChangedListeners = ((CopyOnWriteArray)localObject2);
      }
    }
    localObject2 = paramViewTreeObserver.mOnWindowShownListeners;
    if (localObject2 != null)
    {
      localObject1 = this.mOnWindowShownListeners;
      if (localObject1 != null) {
        ((CopyOnWriteArray)localObject1).addAll((CopyOnWriteArray)localObject2);
      } else {
        this.mOnWindowShownListeners = ((CopyOnWriteArray)localObject2);
      }
    }
    localObject1 = paramViewTreeObserver.mGestureExclusionListeners;
    if (localObject1 != null)
    {
      localObject2 = this.mGestureExclusionListeners;
      if (localObject2 != null) {
        ((CopyOnWriteArray)localObject2).addAll((CopyOnWriteArray)localObject1);
      } else {
        this.mGestureExclusionListeners = ((CopyOnWriteArray)localObject1);
      }
    }
    paramViewTreeObserver.kill();
  }
  
  public void registerFrameCommitCallback(Runnable paramRunnable)
  {
    checkIsAlive();
    if (this.mOnFrameCommitListeners == null) {
      this.mOnFrameCommitListeners = new ArrayList();
    }
    this.mOnFrameCommitListeners.add(paramRunnable);
  }
  
  @Deprecated
  public void removeGlobalOnLayoutListener(OnGlobalLayoutListener paramOnGlobalLayoutListener)
  {
    removeOnGlobalLayoutListener(paramOnGlobalLayoutListener);
  }
  
  @UnsupportedAppUsage
  public void removeOnComputeInternalInsetsListener(OnComputeInternalInsetsListener paramOnComputeInternalInsetsListener)
  {
    checkIsAlive();
    CopyOnWriteArray localCopyOnWriteArray = this.mOnComputeInternalInsetsListeners;
    if (localCopyOnWriteArray == null) {
      return;
    }
    localCopyOnWriteArray.remove(paramOnComputeInternalInsetsListener);
  }
  
  public void removeOnDrawListener(OnDrawListener paramOnDrawListener)
  {
    checkIsAlive();
    if (this.mOnDrawListeners == null) {
      return;
    }
    if (this.mInDispatchOnDraw)
    {
      IllegalStateException localIllegalStateException = new IllegalStateException("Cannot call removeOnDrawListener inside of onDraw");
      if (!sIllegalOnDrawModificationIsFatal) {
        Log.e("ViewTreeObserver", localIllegalStateException.getMessage(), localIllegalStateException);
      } else {
        throw localIllegalStateException;
      }
    }
    this.mOnDrawListeners.remove(paramOnDrawListener);
  }
  
  public void removeOnEnterAnimationCompleteListener(OnEnterAnimationCompleteListener paramOnEnterAnimationCompleteListener)
  {
    checkIsAlive();
    CopyOnWriteArrayList localCopyOnWriteArrayList = this.mOnEnterAnimationCompleteListeners;
    if (localCopyOnWriteArrayList == null) {
      return;
    }
    localCopyOnWriteArrayList.remove(paramOnEnterAnimationCompleteListener);
  }
  
  public void removeOnGlobalFocusChangeListener(OnGlobalFocusChangeListener paramOnGlobalFocusChangeListener)
  {
    checkIsAlive();
    CopyOnWriteArrayList localCopyOnWriteArrayList = this.mOnGlobalFocusListeners;
    if (localCopyOnWriteArrayList == null) {
      return;
    }
    localCopyOnWriteArrayList.remove(paramOnGlobalFocusChangeListener);
  }
  
  public void removeOnGlobalLayoutListener(OnGlobalLayoutListener paramOnGlobalLayoutListener)
  {
    checkIsAlive();
    CopyOnWriteArray localCopyOnWriteArray = this.mOnGlobalLayoutListeners;
    if (localCopyOnWriteArray == null) {
      return;
    }
    localCopyOnWriteArray.remove(paramOnGlobalLayoutListener);
  }
  
  public void removeOnPreDrawListener(OnPreDrawListener paramOnPreDrawListener)
  {
    checkIsAlive();
    CopyOnWriteArray localCopyOnWriteArray = this.mOnPreDrawListeners;
    if (localCopyOnWriteArray == null) {
      return;
    }
    localCopyOnWriteArray.remove(paramOnPreDrawListener);
  }
  
  public void removeOnScrollChangedListener(OnScrollChangedListener paramOnScrollChangedListener)
  {
    checkIsAlive();
    CopyOnWriteArray localCopyOnWriteArray = this.mOnScrollChangedListeners;
    if (localCopyOnWriteArray == null) {
      return;
    }
    localCopyOnWriteArray.remove(paramOnScrollChangedListener);
  }
  
  public void removeOnSystemGestureExclusionRectsChangedListener(Consumer<List<Rect>> paramConsumer)
  {
    checkIsAlive();
    CopyOnWriteArray localCopyOnWriteArray = this.mGestureExclusionListeners;
    if (localCopyOnWriteArray == null) {
      return;
    }
    localCopyOnWriteArray.remove(paramConsumer);
  }
  
  public void removeOnTouchModeChangeListener(OnTouchModeChangeListener paramOnTouchModeChangeListener)
  {
    checkIsAlive();
    CopyOnWriteArrayList localCopyOnWriteArrayList = this.mOnTouchModeChangeListeners;
    if (localCopyOnWriteArrayList == null) {
      return;
    }
    localCopyOnWriteArrayList.remove(paramOnTouchModeChangeListener);
  }
  
  public void removeOnWindowAttachListener(OnWindowAttachListener paramOnWindowAttachListener)
  {
    checkIsAlive();
    CopyOnWriteArrayList localCopyOnWriteArrayList = this.mOnWindowAttachListeners;
    if (localCopyOnWriteArrayList == null) {
      return;
    }
    localCopyOnWriteArrayList.remove(paramOnWindowAttachListener);
  }
  
  public void removeOnWindowFocusChangeListener(OnWindowFocusChangeListener paramOnWindowFocusChangeListener)
  {
    checkIsAlive();
    CopyOnWriteArrayList localCopyOnWriteArrayList = this.mOnWindowFocusListeners;
    if (localCopyOnWriteArrayList == null) {
      return;
    }
    localCopyOnWriteArrayList.remove(paramOnWindowFocusChangeListener);
  }
  
  public void removeOnWindowShownListener(OnWindowShownListener paramOnWindowShownListener)
  {
    checkIsAlive();
    CopyOnWriteArray localCopyOnWriteArray = this.mOnWindowShownListeners;
    if (localCopyOnWriteArray == null) {
      return;
    }
    localCopyOnWriteArray.remove(paramOnWindowShownListener);
  }
  
  public boolean unregisterFrameCommitCallback(Runnable paramRunnable)
  {
    checkIsAlive();
    ArrayList localArrayList = this.mOnFrameCommitListeners;
    if (localArrayList == null) {
      return false;
    }
    return localArrayList.remove(paramRunnable);
  }
  
  static class CopyOnWriteArray<T>
  {
    private final Access<T> mAccess = new Access();
    private ArrayList<T> mData = new ArrayList();
    private ArrayList<T> mDataCopy;
    private boolean mStart;
    
    private ArrayList<T> getArray()
    {
      if (this.mStart)
      {
        if (this.mDataCopy == null) {
          this.mDataCopy = new ArrayList(this.mData);
        }
        return this.mDataCopy;
      }
      return this.mData;
    }
    
    void add(T paramT)
    {
      getArray().add(paramT);
    }
    
    void addAll(CopyOnWriteArray<T> paramCopyOnWriteArray)
    {
      getArray().addAll(paramCopyOnWriteArray.mData);
    }
    
    void clear()
    {
      getArray().clear();
    }
    
    void end()
    {
      if (this.mStart)
      {
        this.mStart = false;
        ArrayList localArrayList = this.mDataCopy;
        if (localArrayList != null)
        {
          this.mData = localArrayList;
          this.mAccess.mData.clear();
          Access.access$102(this.mAccess, 0);
        }
        this.mDataCopy = null;
        return;
      }
      throw new IllegalStateException("Iteration not started");
    }
    
    void remove(T paramT)
    {
      getArray().remove(paramT);
    }
    
    int size()
    {
      return getArray().size();
    }
    
    Access<T> start()
    {
      if (!this.mStart)
      {
        this.mStart = true;
        this.mDataCopy = null;
        Access.access$002(this.mAccess, this.mData);
        Access.access$102(this.mAccess, this.mData.size());
        return this.mAccess;
      }
      throw new IllegalStateException("Iteration already started");
    }
    
    static class Access<T>
    {
      private ArrayList<T> mData;
      private int mSize;
      
      T get(int paramInt)
      {
        return (T)this.mData.get(paramInt);
      }
      
      int size()
      {
        return this.mSize;
      }
    }
  }
  
  public static final class InternalInsetsInfo
  {
    public static final int TOUCHABLE_INSETS_CONTENT = 1;
    public static final int TOUCHABLE_INSETS_FRAME = 0;
    @UnsupportedAppUsage
    public static final int TOUCHABLE_INSETS_REGION = 3;
    public static final int TOUCHABLE_INSETS_VISIBLE = 2;
    @UnsupportedAppUsage
    public final Rect contentInsets = new Rect();
    @UnsupportedAppUsage
    int mTouchableInsets;
    @UnsupportedAppUsage
    public final Region touchableRegion = new Region();
    @UnsupportedAppUsage
    public final Rect visibleInsets = new Rect();
    
    public boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (this == paramObject) {
        return true;
      }
      if ((paramObject != null) && (getClass() == paramObject.getClass()))
      {
        paramObject = (InternalInsetsInfo)paramObject;
        if ((this.mTouchableInsets != ((InternalInsetsInfo)paramObject).mTouchableInsets) || (!this.contentInsets.equals(((InternalInsetsInfo)paramObject).contentInsets)) || (!this.visibleInsets.equals(((InternalInsetsInfo)paramObject).visibleInsets)) || (!this.touchableRegion.equals(((InternalInsetsInfo)paramObject).touchableRegion))) {
          bool = false;
        }
        return bool;
      }
      return false;
    }
    
    public int hashCode()
    {
      return ((this.contentInsets.hashCode() * 31 + this.visibleInsets.hashCode()) * 31 + this.touchableRegion.hashCode()) * 31 + this.mTouchableInsets;
    }
    
    boolean isEmpty()
    {
      boolean bool;
      if ((this.contentInsets.isEmpty()) && (this.visibleInsets.isEmpty()) && (this.touchableRegion.isEmpty()) && (this.mTouchableInsets == 0)) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    void reset()
    {
      this.contentInsets.setEmpty();
      this.visibleInsets.setEmpty();
      this.touchableRegion.setEmpty();
      this.mTouchableInsets = 0;
    }
    
    @UnsupportedAppUsage
    void set(InternalInsetsInfo paramInternalInsetsInfo)
    {
      this.contentInsets.set(paramInternalInsetsInfo.contentInsets);
      this.visibleInsets.set(paramInternalInsetsInfo.visibleInsets);
      this.touchableRegion.set(paramInternalInsetsInfo.touchableRegion);
      this.mTouchableInsets = paramInternalInsetsInfo.mTouchableInsets;
    }
    
    @UnsupportedAppUsage
    public void setTouchableInsets(int paramInt)
    {
      this.mTouchableInsets = paramInt;
    }
  }
  
  public static abstract interface OnComputeInternalInsetsListener
  {
    public abstract void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo paramInternalInsetsInfo);
  }
  
  public static abstract interface OnDrawListener
  {
    public abstract void onDraw();
  }
  
  public static abstract interface OnEnterAnimationCompleteListener
  {
    public abstract void onEnterAnimationComplete();
  }
  
  public static abstract interface OnGlobalFocusChangeListener
  {
    public abstract void onGlobalFocusChanged(View paramView1, View paramView2);
  }
  
  public static abstract interface OnGlobalLayoutListener
  {
    public abstract void onGlobalLayout();
  }
  
  public static abstract interface OnPreDrawListener
  {
    public abstract boolean onPreDraw();
  }
  
  public static abstract interface OnScrollChangedListener
  {
    public abstract void onScrollChanged();
  }
  
  public static abstract interface OnTouchModeChangeListener
  {
    public abstract void onTouchModeChanged(boolean paramBoolean);
  }
  
  public static abstract interface OnWindowAttachListener
  {
    public abstract void onWindowAttached();
    
    public abstract void onWindowDetached();
  }
  
  public static abstract interface OnWindowFocusChangeListener
  {
    public abstract void onWindowFocusChanged(boolean paramBoolean);
  }
  
  public static abstract interface OnWindowShownListener
  {
    public abstract void onWindowShown();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ViewTreeObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */