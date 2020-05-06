package android.view;

import android.graphics.drawable.Drawable;

public abstract interface ContextMenu
  extends Menu
{
  public abstract void clearHeader();
  
  public abstract ContextMenu setHeaderIcon(int paramInt);
  
  public abstract ContextMenu setHeaderIcon(Drawable paramDrawable);
  
  public abstract ContextMenu setHeaderTitle(int paramInt);
  
  public abstract ContextMenu setHeaderTitle(CharSequence paramCharSequence);
  
  public abstract ContextMenu setHeaderView(View paramView);
  
  public static abstract interface ContextMenuInfo {}
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ContextMenu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */