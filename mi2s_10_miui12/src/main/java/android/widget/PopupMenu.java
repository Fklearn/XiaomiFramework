package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnTouchListener;
import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.view.menu.MenuBuilder.Callback;
import com.android.internal.view.menu.MenuPopup;
import com.android.internal.view.menu.MenuPopupHelper;
import com.android.internal.view.menu.ShowableListMenu;

public class PopupMenu
{
  private final View mAnchor;
  @UnsupportedAppUsage
  private final Context mContext;
  private View.OnTouchListener mDragListener;
  private final MenuBuilder mMenu;
  private OnMenuItemClickListener mMenuItemClickListener;
  private OnDismissListener mOnDismissListener;
  @UnsupportedAppUsage
  private final MenuPopupHelper mPopup;
  
  public PopupMenu(Context paramContext, View paramView)
  {
    this(paramContext, paramView, 0);
  }
  
  public PopupMenu(Context paramContext, View paramView, int paramInt)
  {
    this(paramContext, paramView, paramInt, 16843520, 0);
  }
  
  public PopupMenu(Context paramContext, View paramView, int paramInt1, int paramInt2, int paramInt3)
  {
    this.mContext = paramContext;
    this.mAnchor = paramView;
    this.mMenu = new MenuBuilder(paramContext);
    this.mMenu.setCallback(new MenuBuilder.Callback()
    {
      public boolean onMenuItemSelected(MenuBuilder paramAnonymousMenuBuilder, MenuItem paramAnonymousMenuItem)
      {
        if (PopupMenu.this.mMenuItemClickListener != null) {
          return PopupMenu.this.mMenuItemClickListener.onMenuItemClick(paramAnonymousMenuItem);
        }
        return false;
      }
      
      public void onMenuModeChange(MenuBuilder paramAnonymousMenuBuilder) {}
    });
    this.mPopup = new MenuPopupHelper(paramContext, this.mMenu, paramView, false, paramInt2, paramInt3);
    this.mPopup.setGravity(paramInt1);
    this.mPopup.setOnDismissListener(new PopupWindow.OnDismissListener()
    {
      public void onDismiss()
      {
        if (PopupMenu.this.mOnDismissListener != null) {
          PopupMenu.this.mOnDismissListener.onDismiss(PopupMenu.this);
        }
      }
    });
  }
  
  public void dismiss()
  {
    this.mPopup.dismiss();
  }
  
  public View.OnTouchListener getDragToOpenListener()
  {
    if (this.mDragListener == null) {
      this.mDragListener = new ForwardingListener(this.mAnchor)
      {
        public ShowableListMenu getPopup()
        {
          return PopupMenu.this.mPopup.getPopup();
        }
        
        protected boolean onForwardingStarted()
        {
          PopupMenu.this.show();
          return true;
        }
        
        protected boolean onForwardingStopped()
        {
          PopupMenu.this.dismiss();
          return true;
        }
      };
    }
    return this.mDragListener;
  }
  
  public int getGravity()
  {
    return this.mPopup.getGravity();
  }
  
  public Menu getMenu()
  {
    return this.mMenu;
  }
  
  public MenuInflater getMenuInflater()
  {
    return new MenuInflater(this.mContext);
  }
  
  public ListView getMenuListView()
  {
    if (!this.mPopup.isShowing()) {
      return null;
    }
    return this.mPopup.getPopup().getListView();
  }
  
  public void inflate(int paramInt)
  {
    getMenuInflater().inflate(paramInt, this.mMenu);
  }
  
  public void setForceShowIcon(boolean paramBoolean)
  {
    this.mPopup.setForceShowIcon(paramBoolean);
  }
  
  public void setGravity(int paramInt)
  {
    this.mPopup.setGravity(paramInt);
  }
  
  public void setOnDismissListener(OnDismissListener paramOnDismissListener)
  {
    this.mOnDismissListener = paramOnDismissListener;
  }
  
  public void setOnMenuItemClickListener(OnMenuItemClickListener paramOnMenuItemClickListener)
  {
    this.mMenuItemClickListener = paramOnMenuItemClickListener;
  }
  
  public void show()
  {
    this.mPopup.show();
  }
  
  public static abstract interface OnDismissListener
  {
    public abstract void onDismiss(PopupMenu paramPopupMenu);
  }
  
  public static abstract interface OnMenuItemClickListener
  {
    public abstract boolean onMenuItemClick(MenuItem paramMenuItem);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/PopupMenu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */