package android.view;

import android.graphics.Rect;

public abstract class ActionMode
{
  public static final int DEFAULT_HIDE_DURATION = -1;
  public static final int TYPE_FLOATING = 1;
  public static final int TYPE_PRIMARY = 0;
  private Object mTag;
  private boolean mTitleOptionalHint;
  private int mType = 0;
  
  public abstract void finish();
  
  public abstract View getCustomView();
  
  public abstract Menu getMenu();
  
  public abstract MenuInflater getMenuInflater();
  
  public abstract CharSequence getSubtitle();
  
  public Object getTag()
  {
    return this.mTag;
  }
  
  public abstract CharSequence getTitle();
  
  public boolean getTitleOptionalHint()
  {
    return this.mTitleOptionalHint;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public void hide(long paramLong) {}
  
  public abstract void invalidate();
  
  public void invalidateContentRect() {}
  
  public boolean isTitleOptional()
  {
    return false;
  }
  
  public boolean isUiFocusable()
  {
    return true;
  }
  
  public void onWindowFocusChanged(boolean paramBoolean) {}
  
  public abstract void setCustomView(View paramView);
  
  public abstract void setSubtitle(int paramInt);
  
  public abstract void setSubtitle(CharSequence paramCharSequence);
  
  public void setTag(Object paramObject)
  {
    this.mTag = paramObject;
  }
  
  public abstract void setTitle(int paramInt);
  
  public abstract void setTitle(CharSequence paramCharSequence);
  
  public void setTitleOptionalHint(boolean paramBoolean)
  {
    this.mTitleOptionalHint = paramBoolean;
  }
  
  public void setType(int paramInt)
  {
    this.mType = paramInt;
  }
  
  public static abstract interface Callback
  {
    public abstract boolean onActionItemClicked(ActionMode paramActionMode, MenuItem paramMenuItem);
    
    public abstract boolean onCreateActionMode(ActionMode paramActionMode, Menu paramMenu);
    
    public abstract void onDestroyActionMode(ActionMode paramActionMode);
    
    public abstract boolean onPrepareActionMode(ActionMode paramActionMode, Menu paramMenu);
  }
  
  public static abstract class Callback2
    implements ActionMode.Callback
  {
    public void onGetContentRect(ActionMode paramActionMode, View paramView, Rect paramRect)
    {
      if (paramView != null) {
        paramRect.set(0, 0, paramView.getWidth(), paramView.getHeight());
      } else {
        paramRect.set(0, 0, 0, 0);
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ActionMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */