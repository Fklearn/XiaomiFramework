package android.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.graphics.Insets;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.util.Property;
import android.util.SparseArray;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;

public class InsetsController
  implements WindowInsetsController
{
  private static final int ANIMATION_DURATION_HIDE_MS = 340;
  private static final int ANIMATION_DURATION_SHOW_MS = 275;
  private static final int DIRECTION_HIDE = 2;
  private static final int DIRECTION_NONE = 0;
  private static final int DIRECTION_SHOW = 1;
  private static final Interpolator INTERPOLATOR = new PathInterpolator(0.4F, 0.0F, 0.2F, 1.0F);
  private static TypeEvaluator<Insets> sEvaluator = _..Lambda.InsetsController.Cj7UJrCkdHvJAZ_cYKrXuTMsjz8.INSTANCE;
  private final String TAG = "InsetsControllerImpl";
  private final Runnable mAnimCallback;
  private boolean mAnimCallbackScheduled;
  private final ArrayList<InsetsAnimationControlImpl> mAnimationControls = new ArrayList();
  @AnimationDirection
  private int mAnimationDirection;
  private final Rect mFrame = new Rect();
  private WindowInsets mLastInsets;
  private final Rect mLastLegacyContentInsets = new Rect();
  private int mLastLegacySoftInputMode;
  private final Rect mLastLegacyStableInsets = new Rect();
  private int mPendingTypesToShow;
  private final SparseArray<InsetsSourceConsumer> mSourceConsumers = new SparseArray();
  private final InsetsState mState = new InsetsState();
  private final SparseArray<InsetsSourceControl> mTmpControlArray = new SparseArray();
  private final ArrayList<InsetsAnimationControlImpl> mTmpFinishedControls = new ArrayList();
  private final InsetsState mTmpState = new InsetsState();
  private final ViewRootImpl mViewRoot;
  
  public InsetsController(ViewRootImpl paramViewRootImpl)
  {
    this.mViewRoot = paramViewRootImpl;
    this.mAnimCallback = new _..Lambda.InsetsController.HI9QZ2HvGm6iykc_WONz2KPG61Q(this);
  }
  
  private void applyAnimation(final int paramInt, final boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramInt == 0) {
      return;
    }
    controlAnimationUnchecked(paramInt, new WindowInsetsAnimationControlListener()
    {
      private ObjectAnimator mAnimator;
      private WindowInsetsAnimationController mController;
      
      private void onAnimationFinish()
      {
        Object localObject = InsetsController.this;
        int i = 0;
        InsetsController.access$502((InsetsController)localObject, 0);
        localObject = this.mController;
        if (paramBoolean1) {
          i = paramInt;
        }
        ((WindowInsetsAnimationController)localObject).finish(i);
      }
      
      public void onCancelled()
      {
        this.mAnimator.cancel();
      }
      
      public void onReady(WindowInsetsAnimationController paramAnonymousWindowInsetsAnimationController, int paramAnonymousInt)
      {
        this.mController = paramAnonymousWindowInsetsAnimationController;
        if (paramBoolean1) {
          InsetsController.this.showDirectly(paramAnonymousInt);
        } else {
          InsetsController.this.hideDirectly(paramAnonymousInt);
        }
        InsetsController.InsetsProperty localInsetsProperty = new InsetsController.InsetsProperty();
        TypeEvaluator localTypeEvaluator = InsetsController.sEvaluator;
        Insets localInsets1;
        if (paramBoolean1) {
          localInsets1 = paramAnonymousWindowInsetsAnimationController.getHiddenStateInsets();
        } else {
          localInsets1 = paramAnonymousWindowInsetsAnimationController.getShownStateInsets();
        }
        Insets localInsets2;
        if (paramBoolean1) {
          localInsets2 = paramAnonymousWindowInsetsAnimationController.getShownStateInsets();
        } else {
          localInsets2 = paramAnonymousWindowInsetsAnimationController.getHiddenStateInsets();
        }
        this.mAnimator = ObjectAnimator.ofObject(paramAnonymousWindowInsetsAnimationController, localInsetsProperty, localTypeEvaluator, new Insets[] { localInsets1, localInsets2 });
        paramAnonymousWindowInsetsAnimationController = this.mAnimator;
        long l;
        if (paramBoolean1) {
          l = 275L;
        } else {
          l = 340L;
        }
        paramAnonymousWindowInsetsAnimationController.setDuration(l);
        this.mAnimator.setInterpolator(InsetsController.INTERPOLATOR);
        this.mAnimator.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymous2Animator)
          {
            InsetsController.1.this.onAnimationFinish();
          }
        });
        this.mAnimator.start();
      }
    }, this.mState.getDisplayFrame(), paramBoolean2);
  }
  
  private void applyLocalVisibilityOverride()
  {
    for (int i = this.mSourceConsumers.size() - 1; i >= 0; i--) {
      ((InsetsSourceConsumer)this.mSourceConsumers.valueAt(i)).applyLocalVisibilityOverride();
    }
  }
  
  private void cancelAnimation(InsetsAnimationControlImpl paramInsetsAnimationControlImpl)
  {
    paramInsetsAnimationControlImpl.onCancelled();
    this.mAnimationControls.remove(paramInsetsAnimationControlImpl);
  }
  
  private void cancelExistingControllers(int paramInt)
  {
    for (int i = this.mAnimationControls.size() - 1; i >= 0; i--)
    {
      InsetsAnimationControlImpl localInsetsAnimationControlImpl = (InsetsAnimationControlImpl)this.mAnimationControls.get(i);
      if ((localInsetsAnimationControlImpl.getTypes() & paramInt) != 0) {
        cancelAnimation(localInsetsAnimationControlImpl);
      }
    }
  }
  
  private Pair<Integer, Boolean> collectConsumers(boolean paramBoolean, ArraySet<Integer> paramArraySet, SparseArray<InsetsSourceConsumer> paramSparseArray)
  {
    int i = 0;
    boolean bool1 = true;
    int j = paramArraySet.size() - 1;
    while (j >= 0)
    {
      InsetsSourceConsumer localInsetsSourceConsumer = getSourceConsumer(((Integer)paramArraySet.valueAt(j)).intValue());
      int k = i;
      boolean bool2 = bool1;
      if (localInsetsSourceConsumer.getControl() != null)
      {
        if (!localInsetsSourceConsumer.isVisible())
        {
          k = localInsetsSourceConsumer.requestShow(paramBoolean);
          if (k != 0)
          {
            if (k != 1)
            {
              if (k != 2)
              {
                k = i;
                bool2 = bool1;
              }
              else
              {
                int m = this.mPendingTypesToShow;
                k = i;
                bool2 = bool1;
                if (m != 0)
                {
                  this.mPendingTypesToShow = (m & InsetsState.toPublicType(10));
                  k = i;
                  bool2 = bool1;
                }
              }
            }
            else
            {
              bool2 = false;
              k = i;
            }
          }
          else
          {
            k = i | InsetsState.toPublicType(localInsetsSourceConsumer.getType());
            bool2 = bool1;
          }
          bool1 = bool2;
        }
        else
        {
          localInsetsSourceConsumer.notifyHidden();
          k = i | InsetsState.toPublicType(localInsetsSourceConsumer.getType());
        }
        paramSparseArray.put(localInsetsSourceConsumer.getType(), localInsetsSourceConsumer);
        bool2 = bool1;
      }
      j--;
      i = k;
      bool1 = bool2;
    }
    return new Pair(Integer.valueOf(i), Boolean.valueOf(bool1));
  }
  
  private int collectPendingConsumers(int paramInt, SparseArray<InsetsSourceConsumer> paramSparseArray)
  {
    int i = this.mPendingTypesToShow;
    int j = paramInt;
    if (i != 0)
    {
      j = paramInt | i;
      Object localObject = this.mState;
      localObject = InsetsState.toInternalType(i);
      for (paramInt = ((ArraySet)localObject).size() - 1; paramInt >= 0; paramInt--)
      {
        InsetsSourceConsumer localInsetsSourceConsumer = getSourceConsumer(((Integer)((ArraySet)localObject).valueAt(paramInt)).intValue());
        paramSparseArray.put(localInsetsSourceConsumer.getType(), localInsetsSourceConsumer);
      }
      this.mPendingTypesToShow = 0;
    }
    return j;
  }
  
  private void controlAnimationUnchecked(int paramInt, WindowInsetsAnimationControlListener paramWindowInsetsAnimationControlListener, Rect paramRect, boolean paramBoolean)
  {
    if (paramInt == 0) {
      return;
    }
    cancelExistingControllers(paramInt);
    Object localObject1 = this.mState;
    Object localObject2 = InsetsState.toInternalType(paramInt);
    localObject1 = new SparseArray();
    localObject2 = collectConsumers(paramBoolean, (ArraySet)localObject2, (SparseArray)localObject1);
    paramInt = ((Integer)((Pair)localObject2).first).intValue();
    if (!((Boolean)((Pair)localObject2).second).booleanValue())
    {
      this.mPendingTypesToShow = paramInt;
      return;
    }
    paramInt = collectPendingConsumers(paramInt, (SparseArray)localObject1);
    if (paramInt == 0)
    {
      paramWindowInsetsAnimationControlListener.onCancelled();
      return;
    }
    paramWindowInsetsAnimationControlListener = new InsetsAnimationControlImpl((SparseArray)localObject1, paramRect, this.mState, paramWindowInsetsAnimationControlListener, paramInt, new _..Lambda.InsetsController.n9dGLDW5oKSxT73i9ZlnIPWSzms(this), this);
    this.mAnimationControls.add(paramWindowInsetsAnimationControlListener);
  }
  
  private void controlWindowInsetsAnimation(int paramInt, WindowInsetsAnimationControlListener paramWindowInsetsAnimationControlListener, boolean paramBoolean)
  {
    if (!this.mState.getDisplayFrame().equals(this.mFrame))
    {
      paramWindowInsetsAnimationControlListener.onCancelled();
      return;
    }
    controlAnimationUnchecked(paramInt, paramWindowInsetsAnimationControlListener, this.mFrame, paramBoolean);
  }
  
  private InsetsSourceConsumer createConsumerOfType(int paramInt)
  {
    if (paramInt == 10) {
      return new ImeInsetsSourceConsumer(this.mState, _..Lambda.9vBfnQOmNnsc9WU80IIatZHQGKc.INSTANCE, this);
    }
    return new InsetsSourceConsumer(paramInt, this.mState, _..Lambda.9vBfnQOmNnsc9WU80IIatZHQGKc.INSTANCE, this);
  }
  
  private void hideDirectly(int paramInt)
  {
    ArraySet localArraySet = InsetsState.toInternalType(paramInt);
    for (paramInt = localArraySet.size() - 1; paramInt >= 0; paramInt--) {
      getSourceConsumer(((Integer)localArraySet.valueAt(paramInt)).intValue()).hide();
    }
  }
  
  private void sendStateToWindowManager()
  {
    InsetsState localInsetsState = new InsetsState();
    for (int i = this.mSourceConsumers.size() - 1; i >= 0; i--)
    {
      InsetsSourceConsumer localInsetsSourceConsumer = (InsetsSourceConsumer)this.mSourceConsumers.valueAt(i);
      if (localInsetsSourceConsumer.getControl() != null) {
        localInsetsState.addSource(this.mState.getSource(localInsetsSourceConsumer.getType()));
      }
    }
    try
    {
      this.mViewRoot.mWindowSession.insetsModified(this.mViewRoot.mWindow, localInsetsState);
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("InsetsControllerImpl", "Failed to call insetsModified", localRemoteException);
    }
  }
  
  private void show(int paramInt, boolean paramBoolean)
  {
    int i = 0;
    ArraySet localArraySet = InsetsState.toInternalType(paramInt);
    paramInt = localArraySet.size() - 1;
    while (paramInt >= 0)
    {
      InsetsSourceConsumer localInsetsSourceConsumer = getSourceConsumer(((Integer)localArraySet.valueAt(paramInt)).intValue());
      if (this.mAnimationDirection == 2)
      {
        cancelExistingAnimation();
      }
      else if (localInsetsSourceConsumer.isVisible())
      {
        int j = this.mAnimationDirection;
        k = i;
        if (j == 0) {
          break label99;
        }
        if (j == 2)
        {
          k = i;
          break label99;
        }
      }
      int k = i | InsetsState.toPublicType(localInsetsSourceConsumer.getType());
      label99:
      paramInt--;
      i = k;
    }
    applyAnimation(i, true, paramBoolean);
  }
  
  private void showDirectly(int paramInt)
  {
    ArraySet localArraySet = InsetsState.toInternalType(paramInt);
    for (paramInt = localArraySet.size() - 1; paramInt >= 0; paramInt--) {
      getSourceConsumer(((Integer)localArraySet.valueAt(paramInt)).intValue()).show();
    }
  }
  
  @VisibleForTesting
  public void applyImeVisibility(boolean paramBoolean)
  {
    if (paramBoolean) {
      show(2, true);
    } else {
      hide(2);
    }
  }
  
  @VisibleForTesting
  public WindowInsets calculateInsets(boolean paramBoolean1, boolean paramBoolean2, DisplayCutout paramDisplayCutout, Rect paramRect1, Rect paramRect2, int paramInt)
  {
    this.mLastLegacyContentInsets.set(paramRect1);
    this.mLastLegacyStableInsets.set(paramRect2);
    this.mLastLegacySoftInputMode = paramInt;
    this.mLastInsets = this.mState.calculateInsets(this.mFrame, paramBoolean1, paramBoolean2, paramDisplayCutout, paramRect1, paramRect2, paramInt, null);
    return this.mLastInsets;
  }
  
  @VisibleForTesting
  public void cancelExistingAnimation()
  {
    cancelExistingControllers(WindowInsets.Type.all());
  }
  
  public void controlWindowInsetsAnimation(int paramInt, WindowInsetsAnimationControlListener paramWindowInsetsAnimationControlListener)
  {
    controlWindowInsetsAnimation(paramInt, paramWindowInsetsAnimationControlListener, false);
  }
  
  @VisibleForTesting
  public void dispatchAnimationFinished(WindowInsetsAnimationListener.InsetsAnimation paramInsetsAnimation)
  {
    this.mViewRoot.mView.dispatchWindowInsetsAnimationFinished(paramInsetsAnimation);
  }
  
  @VisibleForTesting
  public void dispatchAnimationStarted(WindowInsetsAnimationListener.InsetsAnimation paramInsetsAnimation)
  {
    this.mViewRoot.mView.dispatchWindowInsetsAnimationStarted(paramInsetsAnimation);
  }
  
  void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println(paramString);
    paramPrintWriter.println("InsetsController:");
    InsetsState localInsetsState = this.mState;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append("  ");
    localInsetsState.dump(localStringBuilder.toString(), paramPrintWriter);
  }
  
  @VisibleForTesting
  public InsetsSourceConsumer getSourceConsumer(int paramInt)
  {
    InsetsSourceConsumer localInsetsSourceConsumer = (InsetsSourceConsumer)this.mSourceConsumers.get(paramInt);
    if (localInsetsSourceConsumer != null) {
      return localInsetsSourceConsumer;
    }
    localInsetsSourceConsumer = createConsumerOfType(paramInt);
    this.mSourceConsumers.put(paramInt, localInsetsSourceConsumer);
    return localInsetsSourceConsumer;
  }
  
  public InsetsState getState()
  {
    return this.mState;
  }
  
  ViewRootImpl getViewRoot()
  {
    return this.mViewRoot;
  }
  
  public void hide(int paramInt)
  {
    int i = 0;
    ArraySet localArraySet = InsetsState.toInternalType(paramInt);
    paramInt = localArraySet.size() - 1;
    while (paramInt >= 0)
    {
      InsetsSourceConsumer localInsetsSourceConsumer = getSourceConsumer(((Integer)localArraySet.valueAt(paramInt)).intValue());
      if (this.mAnimationDirection == 1)
      {
        cancelExistingAnimation();
      }
      else if (!localInsetsSourceConsumer.isVisible())
      {
        int j = this.mAnimationDirection;
        k = i;
        if (j == 0) {
          break label96;
        }
        if (j == 2)
        {
          k = i;
          break label96;
        }
      }
      int k = i | InsetsState.toPublicType(localInsetsSourceConsumer.getType());
      label96:
      paramInt--;
      i = k;
    }
    applyAnimation(i, false, false);
  }
  
  void notifyControlRevoked(InsetsSourceConsumer paramInsetsSourceConsumer)
  {
    for (int i = this.mAnimationControls.size() - 1; i >= 0; i--)
    {
      InsetsAnimationControlImpl localInsetsAnimationControlImpl = (InsetsAnimationControlImpl)this.mAnimationControls.get(i);
      if ((localInsetsAnimationControlImpl.getTypes() & InsetsState.toPublicType(paramInsetsSourceConsumer.getType())) != 0) {
        cancelAnimation(localInsetsAnimationControlImpl);
      }
    }
  }
  
  @VisibleForTesting
  public void notifyFinished(InsetsAnimationControlImpl paramInsetsAnimationControlImpl, int paramInt)
  {
    this.mAnimationControls.remove(paramInsetsAnimationControlImpl);
    hideDirectly(paramInsetsAnimationControlImpl.getTypes() & paramInt);
    showDirectly(paramInsetsAnimationControlImpl.getTypes() & paramInt);
  }
  
  @VisibleForTesting
  public void notifyVisibilityChanged()
  {
    this.mViewRoot.notifyInsetsChanged();
    sendStateToWindowManager();
  }
  
  public void onControlsChanged(InsetsSourceControl[] paramArrayOfInsetsSourceControl)
  {
    if (paramArrayOfInsetsSourceControl != null)
    {
      int i = paramArrayOfInsetsSourceControl.length;
      for (j = 0; j < i; j++)
      {
        InsetsSourceControl localInsetsSourceControl = paramArrayOfInsetsSourceControl[j];
        if (localInsetsSourceControl != null) {
          this.mTmpControlArray.put(localInsetsSourceControl.getType(), localInsetsSourceControl);
        }
      }
    }
    for (int j = this.mSourceConsumers.size() - 1; j >= 0; j--)
    {
      paramArrayOfInsetsSourceControl = (InsetsSourceConsumer)this.mSourceConsumers.valueAt(j);
      paramArrayOfInsetsSourceControl.setControl((InsetsSourceControl)this.mTmpControlArray.get(paramArrayOfInsetsSourceControl.getType()));
    }
    for (j = this.mTmpControlArray.size() - 1; j >= 0; j--)
    {
      paramArrayOfInsetsSourceControl = (InsetsSourceControl)this.mTmpControlArray.valueAt(j);
      getSourceConsumer(paramArrayOfInsetsSourceControl.getType()).setControl(paramArrayOfInsetsSourceControl);
    }
    this.mTmpControlArray.clear();
  }
  
  @VisibleForTesting
  public void onFrameChanged(Rect paramRect)
  {
    if (this.mFrame.equals(paramRect)) {
      return;
    }
    this.mViewRoot.notifyInsetsChanged();
    this.mFrame.set(paramRect);
  }
  
  boolean onStateChanged(InsetsState paramInsetsState)
  {
    if (this.mState.equals(paramInsetsState)) {
      return false;
    }
    this.mState.set(paramInsetsState);
    this.mTmpState.set(paramInsetsState, true);
    applyLocalVisibilityOverride();
    this.mViewRoot.notifyInsetsChanged();
    if (!this.mState.equals(this.mTmpState)) {
      sendStateToWindowManager();
    }
    return true;
  }
  
  public void onWindowFocusGained()
  {
    getSourceConsumer(10).onWindowFocusGained();
  }
  
  public void onWindowFocusLost()
  {
    getSourceConsumer(10).onWindowFocusLost();
  }
  
  @VisibleForTesting
  public void scheduleApplyChangeInsets()
  {
    if (!this.mAnimCallbackScheduled)
    {
      this.mViewRoot.mChoreographer.postCallback(2, this.mAnimCallback, null);
      this.mAnimCallbackScheduled = true;
    }
  }
  
  public void show(int paramInt)
  {
    show(paramInt, false);
  }
  
  private static @interface AnimationDirection {}
  
  private static class InsetsProperty
    extends Property<WindowInsetsAnimationController, Insets>
  {
    InsetsProperty()
    {
      super("Insets");
    }
    
    public Insets get(WindowInsetsAnimationController paramWindowInsetsAnimationController)
    {
      return paramWindowInsetsAnimationController.getCurrentInsets();
    }
    
    public void set(WindowInsetsAnimationController paramWindowInsetsAnimationController, Insets paramInsets)
    {
      paramWindowInsetsAnimationController.changeInsets(paramInsets);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/InsetsController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */