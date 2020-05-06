package android.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.transition.Transition;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import com.android.internal.view.menu.ListMenuItemView;
import com.android.internal.view.menu.MenuAdapter;
import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.view.menu.MenuItemImpl;

public class MenuPopupWindow
  extends ListPopupWindow
  implements MenuItemHoverListener
{
  private MenuItemHoverListener mHoverListener;
  
  public MenuPopupWindow(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  DropDownListView createDropDownListView(Context paramContext, boolean paramBoolean)
  {
    paramContext = new MenuDropDownListView(paramContext, paramBoolean);
    paramContext.setHoverListener(this);
    return paramContext;
  }
  
  public void onItemHoverEnter(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem)
  {
    MenuItemHoverListener localMenuItemHoverListener = this.mHoverListener;
    if (localMenuItemHoverListener != null) {
      localMenuItemHoverListener.onItemHoverEnter(paramMenuBuilder, paramMenuItem);
    }
  }
  
  public void onItemHoverExit(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem)
  {
    MenuItemHoverListener localMenuItemHoverListener = this.mHoverListener;
    if (localMenuItemHoverListener != null) {
      localMenuItemHoverListener.onItemHoverExit(paramMenuBuilder, paramMenuItem);
    }
  }
  
  public void setEnterTransition(Transition paramTransition)
  {
    this.mPopup.setEnterTransition(paramTransition);
  }
  
  public void setExitTransition(Transition paramTransition)
  {
    this.mPopup.setExitTransition(paramTransition);
  }
  
  public void setHoverListener(MenuItemHoverListener paramMenuItemHoverListener)
  {
    this.mHoverListener = paramMenuItemHoverListener;
  }
  
  public void setTouchModal(boolean paramBoolean)
  {
    this.mPopup.setTouchModal(paramBoolean);
  }
  
  public static class MenuDropDownListView
    extends DropDownListView
  {
    final int mAdvanceKey;
    private MenuItemHoverListener mHoverListener;
    private MenuItem mHoveredMenuItem;
    final int mRetreatKey;
    
    public MenuDropDownListView(Context paramContext, boolean paramBoolean)
    {
      super(paramBoolean);
      if (paramContext.getResources().getConfiguration().getLayoutDirection() == 1)
      {
        this.mAdvanceKey = 21;
        this.mRetreatKey = 22;
      }
      else
      {
        this.mAdvanceKey = 22;
        this.mRetreatKey = 21;
      }
    }
    
    public void clearSelection()
    {
      setSelectedPositionInt(-1);
      setNextSelectedPositionInt(-1);
    }
    
    public boolean onHoverEvent(MotionEvent paramMotionEvent)
    {
      if (this.mHoverListener != null)
      {
        Object localObject1 = getAdapter();
        int i;
        if ((localObject1 instanceof HeaderViewListAdapter))
        {
          localObject1 = (HeaderViewListAdapter)localObject1;
          i = ((HeaderViewListAdapter)localObject1).getHeadersCount();
          localObject1 = (MenuAdapter)((HeaderViewListAdapter)localObject1).getWrappedAdapter();
        }
        else
        {
          i = 0;
          localObject1 = (MenuAdapter)localObject1;
        }
        MenuItem localMenuItem = null;
        Object localObject2 = localMenuItem;
        if (paramMotionEvent.getAction() != 10)
        {
          int j = pointToPosition((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
          localObject2 = localMenuItem;
          if (j != -1)
          {
            i = j - i;
            localObject2 = localMenuItem;
            if (i >= 0)
            {
              localObject2 = localMenuItem;
              if (i < ((MenuAdapter)localObject1).getCount()) {
                localObject2 = ((MenuAdapter)localObject1).getItem(i);
              }
            }
          }
        }
        localMenuItem = this.mHoveredMenuItem;
        if (localMenuItem != localObject2)
        {
          localObject1 = ((MenuAdapter)localObject1).getAdapterMenu();
          if (localMenuItem != null) {
            this.mHoverListener.onItemHoverExit((MenuBuilder)localObject1, localMenuItem);
          }
          this.mHoveredMenuItem = ((MenuItem)localObject2);
          if (localObject2 != null) {
            this.mHoverListener.onItemHoverEnter((MenuBuilder)localObject1, (MenuItem)localObject2);
          }
        }
      }
      return super.onHoverEvent(paramMotionEvent);
    }
    
    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
    {
      ListMenuItemView localListMenuItemView = (ListMenuItemView)getSelectedView();
      if ((localListMenuItemView != null) && (paramInt == this.mAdvanceKey))
      {
        if ((localListMenuItemView.isEnabled()) && (localListMenuItemView.getItemData().hasSubMenu())) {
          performItemClick(localListMenuItemView, getSelectedItemPosition(), getSelectedItemId());
        }
        return true;
      }
      if ((localListMenuItemView != null) && (paramInt == this.mRetreatKey))
      {
        setSelectedPositionInt(-1);
        setNextSelectedPositionInt(-1);
        ((MenuAdapter)getAdapter()).getAdapterMenu().close(false);
        return true;
      }
      return super.onKeyDown(paramInt, paramKeyEvent);
    }
    
    public void setHoverListener(MenuItemHoverListener paramMenuItemHoverListener)
    {
      this.mHoverListener = paramMenuItemHoverListener;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/MenuPopupWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */