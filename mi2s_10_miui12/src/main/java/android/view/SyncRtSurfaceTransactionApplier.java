package android.view;

import android.graphics.Matrix;
import android.graphics.Rect;
import com.android.internal.annotations.VisibleForTesting;
import java.util.function.Consumer;

public class SyncRtSurfaceTransactionApplier
{
  private final Surface mTargetSurface;
  private final ViewRootImpl mTargetViewRootImpl;
  private final float[] mTmpFloat9 = new float[9];
  
  public SyncRtSurfaceTransactionApplier(View paramView)
  {
    Object localObject = null;
    if (paramView != null) {
      paramView = paramView.getViewRootImpl();
    } else {
      paramView = null;
    }
    this.mTargetViewRootImpl = paramView;
    ViewRootImpl localViewRootImpl = this.mTargetViewRootImpl;
    paramView = (View)localObject;
    if (localViewRootImpl != null) {
      paramView = localViewRootImpl.mSurface;
    }
    this.mTargetSurface = paramView;
  }
  
  public static void applyParams(SurfaceControl.Transaction paramTransaction, SurfaceParams paramSurfaceParams, float[] paramArrayOfFloat)
  {
    paramTransaction.setMatrix(paramSurfaceParams.surface, paramSurfaceParams.matrix, paramArrayOfFloat);
    paramTransaction.setWindowCrop(paramSurfaceParams.surface, paramSurfaceParams.windowCrop);
    paramTransaction.setAlpha(paramSurfaceParams.surface, paramSurfaceParams.alpha);
    paramTransaction.setLayer(paramSurfaceParams.surface, paramSurfaceParams.layer);
    paramTransaction.setCornerRadius(paramSurfaceParams.surface, paramSurfaceParams.cornerRadius);
    if (paramSurfaceParams.visible) {
      paramTransaction.show(paramSurfaceParams.surface);
    } else {
      paramTransaction.hide(paramSurfaceParams.surface);
    }
  }
  
  public static void create(View paramView, final Consumer<SyncRtSurfaceTransactionApplier> paramConsumer)
  {
    if (paramView == null) {
      paramConsumer.accept(null);
    } else if (paramView.getViewRootImpl() != null) {
      paramConsumer.accept(new SyncRtSurfaceTransactionApplier(paramView));
    } else {
      paramView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener()
      {
        public void onViewAttachedToWindow(View paramAnonymousView)
        {
          SyncRtSurfaceTransactionApplier.this.removeOnAttachStateChangeListener(this);
          paramConsumer.accept(new SyncRtSurfaceTransactionApplier(SyncRtSurfaceTransactionApplier.this));
        }
        
        public void onViewDetachedFromWindow(View paramAnonymousView) {}
      });
    }
  }
  
  public void scheduleApply(SurfaceParams... paramVarArgs)
  {
    ViewRootImpl localViewRootImpl = this.mTargetViewRootImpl;
    if (localViewRootImpl == null) {
      return;
    }
    localViewRootImpl.registerRtFrameCallback(new _..Lambda.SyncRtSurfaceTransactionApplier.ttntIVYYZl7t890CcQHVoB3U1nQ(this, paramVarArgs));
    this.mTargetViewRootImpl.getView().invalidate();
  }
  
  public static class SurfaceParams
  {
    @VisibleForTesting
    public final float alpha;
    @VisibleForTesting
    final float cornerRadius;
    @VisibleForTesting
    public final int layer;
    @VisibleForTesting
    public final Matrix matrix;
    @VisibleForTesting
    public final SurfaceControl surface;
    public final boolean visible;
    @VisibleForTesting
    public final Rect windowCrop;
    
    public SurfaceParams(SurfaceControl paramSurfaceControl, float paramFloat1, Matrix paramMatrix, Rect paramRect, int paramInt, float paramFloat2, boolean paramBoolean)
    {
      this.surface = paramSurfaceControl;
      this.alpha = paramFloat1;
      this.matrix = new Matrix(paramMatrix);
      this.windowCrop = new Rect(paramRect);
      this.layer = paramInt;
      this.cornerRadius = paramFloat2;
      this.visible = paramBoolean;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/SyncRtSurfaceTransactionApplier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */