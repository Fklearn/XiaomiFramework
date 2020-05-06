package android.view;

import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.ArraySet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.SparseSetArray;
import com.android.internal.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.function.Supplier;

@VisibleForTesting
public class InsetsAnimationControlImpl
  implements WindowInsetsAnimationController
{
  private final WindowInsetsAnimationListener.InsetsAnimation mAnimation;
  private boolean mCancelled;
  private final SparseArray<InsetsSourceConsumer> mConsumers;
  private final InsetsController mController;
  private Insets mCurrentInsets;
  private boolean mFinished;
  private int mFinishedShownTypes;
  private final Rect mFrame;
  private final Insets mHiddenInsets;
  private final InsetsState mInitialInsetsState;
  private final WindowInsetsAnimationControlListener mListener;
  private Insets mPendingInsets;
  private final Insets mShownInsets;
  private final SparseSetArray<InsetsSourceConsumer> mSideSourceMap = new SparseSetArray();
  private final Rect mTmpFrame = new Rect();
  private final Matrix mTmpMatrix = new Matrix();
  private final Supplier<SyncRtSurfaceTransactionApplier> mTransactionApplierSupplier;
  private final SparseIntArray mTypeSideMap = new SparseIntArray();
  private final int mTypes;
  
  @VisibleForTesting
  public InsetsAnimationControlImpl(SparseArray<InsetsSourceConsumer> paramSparseArray, Rect paramRect, InsetsState paramInsetsState, WindowInsetsAnimationControlListener paramWindowInsetsAnimationControlListener, int paramInt, Supplier<SyncRtSurfaceTransactionApplier> paramSupplier, InsetsController paramInsetsController)
  {
    this.mConsumers = paramSparseArray;
    this.mListener = paramWindowInsetsAnimationControlListener;
    this.mTypes = paramInt;
    this.mTransactionApplierSupplier = paramSupplier;
    this.mController = paramInsetsController;
    this.mInitialInsetsState = new InsetsState(paramInsetsState, true);
    this.mCurrentInsets = getInsetsFromState(this.mInitialInsetsState, paramRect, null);
    this.mHiddenInsets = calculateInsets(this.mInitialInsetsState, paramRect, paramSparseArray, false, null);
    this.mShownInsets = calculateInsets(this.mInitialInsetsState, paramRect, paramSparseArray, true, this.mTypeSideMap);
    this.mFrame = new Rect(paramRect);
    buildTypeSourcesMap(this.mTypeSideMap, this.mSideSourceMap, this.mConsumers);
    paramWindowInsetsAnimationControlListener.onReady(this, paramInt);
    this.mAnimation = new WindowInsetsAnimationListener.InsetsAnimation(this.mTypes, this.mHiddenInsets, this.mShownInsets);
    this.mController.dispatchAnimationStarted(this.mAnimation);
  }
  
  private void addTranslationToMatrix(int paramInt1, int paramInt2, Matrix paramMatrix, Rect paramRect)
  {
    if (paramInt1 != 0)
    {
      if (paramInt1 != 1)
      {
        if (paramInt1 != 2)
        {
          if (paramInt1 == 3)
          {
            paramMatrix.postTranslate(0.0F, paramInt2);
            paramRect.offset(0, paramInt2);
          }
        }
        else
        {
          paramMatrix.postTranslate(paramInt2, 0.0F);
          paramRect.offset(paramInt2, 0);
        }
      }
      else
      {
        paramMatrix.postTranslate(0.0F, -paramInt2);
        paramRect.offset(0, -paramInt2);
      }
    }
    else
    {
      paramMatrix.postTranslate(-paramInt2, 0.0F);
      paramRect.offset(-paramInt2, 0);
    }
  }
  
  private static void buildTypeSourcesMap(SparseIntArray paramSparseIntArray, SparseSetArray<InsetsSourceConsumer> paramSparseSetArray, SparseArray<InsetsSourceConsumer> paramSparseArray)
  {
    for (int i = paramSparseIntArray.size() - 1; i >= 0; i--)
    {
      int j = paramSparseIntArray.keyAt(i);
      paramSparseSetArray.add(paramSparseIntArray.valueAt(i), (InsetsSourceConsumer)paramSparseArray.get(j));
    }
  }
  
  private Insets calculateInsets(InsetsState paramInsetsState, Rect paramRect, SparseArray<InsetsSourceConsumer> paramSparseArray, boolean paramBoolean, SparseIntArray paramSparseIntArray)
  {
    for (int i = paramSparseArray.size() - 1; i >= 0; i--) {
      paramInsetsState.getSource(((InsetsSourceConsumer)paramSparseArray.valueAt(i)).getType()).setVisible(paramBoolean);
    }
    return getInsetsFromState(paramInsetsState, paramRect, paramSparseIntArray);
  }
  
  private Insets getInsetsFromState(InsetsState paramInsetsState, Rect paramRect, SparseIntArray paramSparseIntArray)
  {
    return paramInsetsState.calculateInsets(paramRect, false, false, null, null, null, 16, paramSparseIntArray).getInsets(this.mTypes);
  }
  
  private Insets sanitize(Insets paramInsets)
  {
    return Insets.max(Insets.min(paramInsets, this.mShownInsets), this.mHiddenInsets);
  }
  
  private void updateLeashesForSide(int paramInt1, int paramInt2, int paramInt3, ArrayList<SyncRtSurfaceTransactionApplier.SurfaceParams> paramArrayList, InsetsState paramInsetsState)
  {
    ArraySet localArraySet = this.mSideSourceMap.get(paramInt1);
    for (int i = localArraySet.size() - 1; i >= 0; i--)
    {
      Object localObject1 = (InsetsSourceConsumer)localArraySet.valueAt(i);
      Object localObject2 = this.mInitialInsetsState.getSource(((InsetsSourceConsumer)localObject1).getType());
      InsetsSourceControl localInsetsSourceControl = ((InsetsSourceConsumer)localObject1).getControl();
      localObject1 = ((InsetsSourceConsumer)localObject1).getControl().getLeash();
      this.mTmpMatrix.setTranslate(localInsetsSourceControl.getSurfacePosition().x, localInsetsSourceControl.getSurfacePosition().y);
      this.mTmpFrame.set(((InsetsSource)localObject2).getFrame());
      addTranslationToMatrix(paramInt1, paramInt2, this.mTmpMatrix, this.mTmpFrame);
      paramInsetsState.getSource(((InsetsSource)localObject2).getType()).setFrame(this.mTmpFrame);
      localObject2 = this.mTmpMatrix;
      boolean bool;
      if (paramInt3 != 0) {
        bool = true;
      } else {
        bool = false;
      }
      paramArrayList.add(new SyncRtSurfaceTransactionApplier.SurfaceParams((SurfaceControl)localObject1, 1.0F, (Matrix)localObject2, null, 0, 0.0F, bool));
    }
  }
  
  @VisibleForTesting
  public boolean applyChangeInsets(InsetsState paramInsetsState)
  {
    if (this.mCancelled) {
      return false;
    }
    Insets localInsets = Insets.subtract(this.mShownInsets, this.mPendingInsets);
    ArrayList localArrayList = new ArrayList();
    if (localInsets.left != 0) {
      updateLeashesForSide(0, localInsets.left, this.mPendingInsets.left, localArrayList, paramInsetsState);
    }
    if (localInsets.top != 0) {
      updateLeashesForSide(1, localInsets.top, this.mPendingInsets.top, localArrayList, paramInsetsState);
    }
    if (localInsets.right != 0) {
      updateLeashesForSide(2, localInsets.right, this.mPendingInsets.right, localArrayList, paramInsetsState);
    }
    if (localInsets.bottom != 0) {
      updateLeashesForSide(3, localInsets.bottom, this.mPendingInsets.bottom, localArrayList, paramInsetsState);
    }
    ((SyncRtSurfaceTransactionApplier)this.mTransactionApplierSupplier.get()).scheduleApply((SyncRtSurfaceTransactionApplier.SurfaceParams[])localArrayList.toArray(new SyncRtSurfaceTransactionApplier.SurfaceParams[localArrayList.size()]));
    this.mCurrentInsets = this.mPendingInsets;
    if (this.mFinished) {
      this.mController.notifyFinished(this, this.mFinishedShownTypes);
    }
    return this.mFinished;
  }
  
  public void changeInsets(Insets paramInsets)
  {
    if (!this.mFinished)
    {
      if (!this.mCancelled)
      {
        this.mPendingInsets = sanitize(paramInsets);
        this.mController.scheduleApplyChangeInsets();
        return;
      }
      throw new IllegalStateException("Can't change insets on an animation that is cancelled.");
    }
    throw new IllegalStateException("Can't change insets on an animation that is finished.");
  }
  
  public void finish(int paramInt)
  {
    if (this.mCancelled) {
      return;
    }
    InsetsState localInsetsState = new InsetsState(this.mController.getState());
    for (int i = this.mConsumers.size() - 1; i >= 0; i--)
    {
      InsetsSourceConsumer localInsetsSourceConsumer = (InsetsSourceConsumer)this.mConsumers.valueAt(i);
      boolean bool;
      if ((InsetsState.toPublicType(localInsetsSourceConsumer.getType()) & paramInt) != 0) {
        bool = true;
      } else {
        bool = false;
      }
      localInsetsState.getSource(localInsetsSourceConsumer.getType()).setVisible(bool);
    }
    changeInsets(getInsetsFromState(localInsetsState, this.mFrame, null));
    this.mFinished = true;
    this.mFinishedShownTypes = paramInt;
  }
  
  WindowInsetsAnimationListener.InsetsAnimation getAnimation()
  {
    return this.mAnimation;
  }
  
  public Insets getCurrentInsets()
  {
    return this.mCurrentInsets;
  }
  
  public Insets getHiddenStateInsets()
  {
    return this.mHiddenInsets;
  }
  
  public Insets getShownStateInsets()
  {
    return this.mShownInsets;
  }
  
  public int getTypes()
  {
    return this.mTypes;
  }
  
  @VisibleForTesting
  public void onCancelled()
  {
    if (this.mFinished) {
      return;
    }
    this.mCancelled = true;
    this.mListener.onCancelled();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/InsetsAnimationControlImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */