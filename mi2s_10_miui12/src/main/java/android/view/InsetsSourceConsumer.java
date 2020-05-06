package android.view;

import com.android.internal.annotations.VisibleForTesting;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.function.Supplier;

public class InsetsSourceConsumer
{
  protected final InsetsController mController;
  private InsetsSourceControl mSourceControl;
  private final InsetsState mState;
  private final Supplier<SurfaceControl.Transaction> mTransactionSupplier;
  private final int mType;
  protected boolean mVisible;
  
  public InsetsSourceConsumer(int paramInt, InsetsState paramInsetsState, Supplier<SurfaceControl.Transaction> paramSupplier, InsetsController paramInsetsController)
  {
    this.mType = paramInt;
    this.mState = paramInsetsState;
    this.mTransactionSupplier = paramSupplier;
    this.mController = paramInsetsController;
    this.mVisible = InsetsState.getDefaultVisibility(paramInt);
  }
  
  private void applyHiddenToControl()
  {
    if (this.mSourceControl == null) {
      return;
    }
    SurfaceControl.Transaction localTransaction = (SurfaceControl.Transaction)this.mTransactionSupplier.get();
    if (this.mVisible) {
      localTransaction.show(this.mSourceControl.getLeash());
    } else {
      localTransaction.hide(this.mSourceControl.getLeash());
    }
    localTransaction.apply();
  }
  
  private void setVisible(boolean paramBoolean)
  {
    if (this.mVisible == paramBoolean) {
      return;
    }
    this.mVisible = paramBoolean;
    applyHiddenToControl();
    applyLocalVisibilityOverride();
    this.mController.notifyVisibilityChanged();
  }
  
  boolean applyLocalVisibilityOverride()
  {
    if (this.mSourceControl == null) {
      return false;
    }
    if (this.mState.getSource(this.mType).isVisible() == this.mVisible) {
      return false;
    }
    this.mState.getSource(this.mType).setVisible(this.mVisible);
    return true;
  }
  
  @VisibleForTesting
  public InsetsSourceControl getControl()
  {
    return this.mSourceControl;
  }
  
  int getType()
  {
    return this.mType;
  }
  
  @VisibleForTesting
  public void hide()
  {
    setVisible(false);
  }
  
  @VisibleForTesting
  public boolean isVisible()
  {
    return this.mVisible;
  }
  
  void notifyHidden() {}
  
  public void onWindowFocusGained() {}
  
  public void onWindowFocusLost() {}
  
  int requestShow(boolean paramBoolean)
  {
    return 0;
  }
  
  public void setControl(InsetsSourceControl paramInsetsSourceControl)
  {
    if (this.mSourceControl == paramInsetsSourceControl) {
      return;
    }
    this.mSourceControl = paramInsetsSourceControl;
    applyHiddenToControl();
    if (applyLocalVisibilityOverride()) {
      this.mController.notifyVisibilityChanged();
    }
    if (this.mSourceControl == null) {
      this.mController.notifyControlRevoked(this);
    }
  }
  
  @VisibleForTesting
  public void show()
  {
    setVisible(true);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  static @interface ShowResult
  {
    public static final int SHOW_DELAYED = 1;
    public static final int SHOW_FAILED = 2;
    public static final int SHOW_IMMEDIATELY = 0;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/InsetsSourceConsumer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */