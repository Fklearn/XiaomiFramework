package android.widget;

import android.view.MenuItem;
import com.android.internal.view.menu.MenuBuilder;

public abstract interface MenuItemHoverListener
{
  public abstract void onItemHoverEnter(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem);
  
  public abstract void onItemHoverExit(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/MenuItemHoverListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */