package android.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.annotations.VisibleForTesting;
import java.util.Arrays;
import java.util.function.Supplier;

public final class ImeInsetsSourceConsumer
  extends InsetsSourceConsumer
{
  private EditorInfo mFocusedEditor;
  private boolean mHasWindowFocus;
  private EditorInfo mPreRenderedEditor;
  private boolean mShowOnNextImeRender;
  
  public ImeInsetsSourceConsumer(InsetsState paramInsetsState, Supplier<SurfaceControl.Transaction> paramSupplier, InsetsController paramInsetsController)
  {
    super(10, paramInsetsState, paramSupplier, paramInsetsController);
  }
  
  @VisibleForTesting
  public static boolean areEditorsSimilar(EditorInfo paramEditorInfo1, EditorInfo paramEditorInfo2)
  {
    boolean bool1;
    if ((paramEditorInfo1.imeOptions == paramEditorInfo2.imeOptions) && (paramEditorInfo1.inputType == paramEditorInfo2.inputType) && (TextUtils.equals(paramEditorInfo1.packageName, paramEditorInfo2.packageName))) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    boolean bool2;
    if (paramEditorInfo1.privateImeOptions != null) {
      bool2 = paramEditorInfo1.privateImeOptions.equals(paramEditorInfo2.privateImeOptions);
    } else {
      bool2 = true;
    }
    if (!(bool1 & bool2)) {
      return false;
    }
    if (((paramEditorInfo1.extras == null) && (paramEditorInfo2.extras == null)) || (paramEditorInfo1.extras == paramEditorInfo2.extras)) {
      return true;
    }
    if (((paramEditorInfo1.extras == null) && (paramEditorInfo2.extras != null)) || ((paramEditorInfo1.extras == null) && (paramEditorInfo2.extras != null))) {
      return false;
    }
    if ((paramEditorInfo1.extras.hashCode() != paramEditorInfo2.extras.hashCode()) && (!paramEditorInfo1.extras.equals(paramEditorInfo1)))
    {
      if (paramEditorInfo1.extras.size() != paramEditorInfo2.extras.size()) {
        return false;
      }
      if (paramEditorInfo1.extras.toString().equals(paramEditorInfo2.extras.toString())) {
        return true;
      }
      Parcel localParcel = Parcel.obtain();
      paramEditorInfo1.extras.writeToParcel(localParcel, 0);
      localParcel.setDataPosition(0);
      paramEditorInfo1 = Parcel.obtain();
      paramEditorInfo2.extras.writeToParcel(paramEditorInfo1, 0);
      paramEditorInfo1.setDataPosition(0);
      return Arrays.equals(localParcel.createByteArray(), paramEditorInfo1.createByteArray());
    }
    return true;
  }
  
  private InputMethodManager getImm()
  {
    return (InputMethodManager)this.mController.getViewRoot().mContext.getSystemService(InputMethodManager.class);
  }
  
  private boolean isDummyOrEmptyEditor(EditorInfo paramEditorInfo)
  {
    boolean bool;
    if ((paramEditorInfo != null) && ((paramEditorInfo.fieldId > 0) || (paramEditorInfo.inputType > 0))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private boolean isServedEditorRendered()
  {
    EditorInfo localEditorInfo = this.mFocusedEditor;
    if ((localEditorInfo != null) && (this.mPreRenderedEditor != null) && (!isDummyOrEmptyEditor(localEditorInfo)) && (!isDummyOrEmptyEditor(this.mPreRenderedEditor))) {
      return areEditorsSimilar(this.mFocusedEditor, this.mPreRenderedEditor);
    }
    return false;
  }
  
  public void applyImeVisibility(boolean paramBoolean)
  {
    if (!this.mHasWindowFocus) {
      return;
    }
    this.mController.applyImeVisibility(paramBoolean);
  }
  
  void notifyHidden()
  {
    getImm().notifyImeHidden();
  }
  
  public void onPreRendered(EditorInfo paramEditorInfo)
  {
    this.mPreRenderedEditor = paramEditorInfo;
    if (this.mShowOnNextImeRender)
    {
      this.mShowOnNextImeRender = false;
      if (isServedEditorRendered()) {
        applyImeVisibility(true);
      }
    }
  }
  
  public void onServedEditorChanged(EditorInfo paramEditorInfo)
  {
    if (isDummyOrEmptyEditor(paramEditorInfo)) {
      this.mShowOnNextImeRender = false;
    }
    this.mFocusedEditor = paramEditorInfo;
  }
  
  public void onWindowFocusGained()
  {
    this.mHasWindowFocus = true;
    getImm().registerImeConsumer(this);
  }
  
  public void onWindowFocusLost()
  {
    this.mHasWindowFocus = false;
    getImm().unregisterImeConsumer(this);
  }
  
  int requestShow(boolean paramBoolean)
  {
    if (paramBoolean) {
      return 0;
    }
    int i;
    if (getImm().requestImeShow(null)) {
      i = 1;
    } else {
      i = 2;
    }
    return i;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ImeInsetsSourceConsumer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */