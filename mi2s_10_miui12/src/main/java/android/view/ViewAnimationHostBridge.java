package android.view;

import android.graphics.RenderNode;
import android.graphics.RenderNode.AnimationHost;

public class ViewAnimationHostBridge
  implements RenderNode.AnimationHost
{
  private final View mView;
  
  public ViewAnimationHostBridge(View paramView)
  {
    this.mView = paramView;
  }
  
  public boolean isAttached()
  {
    boolean bool;
    if (this.mView.mAttachInfo != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void registerAnimatingRenderNode(RenderNode paramRenderNode)
  {
    this.mView.mAttachInfo.mViewRootImpl.registerAnimatingRenderNode(paramRenderNode);
  }
  
  public void registerVectorDrawableAnimator(NativeVectorDrawableAnimator paramNativeVectorDrawableAnimator)
  {
    this.mView.mAttachInfo.mViewRootImpl.registerVectorDrawableAnimator(paramNativeVectorDrawableAnimator);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ViewAnimationHostBridge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */