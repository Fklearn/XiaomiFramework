package android.view.accessibility;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

public abstract class AccessibilityRequestPreparer
{
  public static final int REQUEST_TYPE_EXTRA_DATA = 1;
  private final int mAccessibilityViewId;
  private final int mRequestTypes;
  private final WeakReference<View> mViewRef;
  
  public AccessibilityRequestPreparer(View paramView, int paramInt)
  {
    if (paramView.isAttachedToWindow())
    {
      this.mViewRef = new WeakReference(paramView);
      this.mAccessibilityViewId = paramView.getAccessibilityViewId();
      this.mRequestTypes = paramInt;
      paramView.addOnAttachStateChangeListener(new ViewAttachStateListener(null));
      return;
    }
    throw new IllegalStateException("View must be attached to a window");
  }
  
  int getAccessibilityViewId()
  {
    return this.mAccessibilityViewId;
  }
  
  public View getView()
  {
    return (View)this.mViewRef.get();
  }
  
  public abstract void onPrepareExtraData(int paramInt, String paramString, Bundle paramBundle, Message paramMessage);
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface RequestTypes {}
  
  private class ViewAttachStateListener
    implements View.OnAttachStateChangeListener
  {
    private ViewAttachStateListener() {}
    
    public void onViewAttachedToWindow(View paramView) {}
    
    public void onViewDetachedFromWindow(View paramView)
    {
      Context localContext = paramView.getContext();
      if (localContext != null) {
        ((AccessibilityManager)localContext.getSystemService(AccessibilityManager.class)).removeAccessibilityRequestPreparer(AccessibilityRequestPreparer.this);
      }
      paramView.removeOnAttachStateChangeListener(this);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/accessibility/AccessibilityRequestPreparer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */