package android.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;

public abstract interface MenuItem
{
  public static final int SHOW_AS_ACTION_ALWAYS = 2;
  public static final int SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW = 8;
  public static final int SHOW_AS_ACTION_IF_ROOM = 1;
  public static final int SHOW_AS_ACTION_NEVER = 0;
  public static final int SHOW_AS_ACTION_WITH_TEXT = 4;
  
  public abstract boolean collapseActionView();
  
  public abstract boolean expandActionView();
  
  public abstract ActionProvider getActionProvider();
  
  public abstract View getActionView();
  
  public int getAlphabeticModifiers()
  {
    return 4096;
  }
  
  public abstract char getAlphabeticShortcut();
  
  public CharSequence getContentDescription()
  {
    return null;
  }
  
  public abstract int getGroupId();
  
  public abstract Drawable getIcon();
  
  public BlendMode getIconTintBlendMode()
  {
    PorterDuff.Mode localMode = getIconTintMode();
    if (localMode != null) {
      return BlendMode.fromValue(localMode.nativeInt);
    }
    return null;
  }
  
  public ColorStateList getIconTintList()
  {
    return null;
  }
  
  public PorterDuff.Mode getIconTintMode()
  {
    return null;
  }
  
  public abstract Intent getIntent();
  
  public abstract int getItemId();
  
  public abstract ContextMenu.ContextMenuInfo getMenuInfo();
  
  public int getNumericModifiers()
  {
    return 4096;
  }
  
  public abstract char getNumericShortcut();
  
  public abstract int getOrder();
  
  public abstract SubMenu getSubMenu();
  
  public abstract CharSequence getTitle();
  
  public abstract CharSequence getTitleCondensed();
  
  public CharSequence getTooltipText()
  {
    return null;
  }
  
  public abstract boolean hasSubMenu();
  
  public abstract boolean isActionViewExpanded();
  
  public abstract boolean isCheckable();
  
  public abstract boolean isChecked();
  
  public abstract boolean isEnabled();
  
  public abstract boolean isVisible();
  
  public boolean requiresActionButton()
  {
    return false;
  }
  
  public boolean requiresOverflow()
  {
    return true;
  }
  
  public abstract MenuItem setActionProvider(ActionProvider paramActionProvider);
  
  public abstract MenuItem setActionView(int paramInt);
  
  public abstract MenuItem setActionView(View paramView);
  
  public abstract MenuItem setAlphabeticShortcut(char paramChar);
  
  public MenuItem setAlphabeticShortcut(char paramChar, int paramInt)
  {
    if ((0x1100F & paramInt) == 4096) {
      return setAlphabeticShortcut(paramChar);
    }
    return this;
  }
  
  public abstract MenuItem setCheckable(boolean paramBoolean);
  
  public abstract MenuItem setChecked(boolean paramBoolean);
  
  public MenuItem setContentDescription(CharSequence paramCharSequence)
  {
    return this;
  }
  
  public abstract MenuItem setEnabled(boolean paramBoolean);
  
  public abstract MenuItem setIcon(int paramInt);
  
  public abstract MenuItem setIcon(Drawable paramDrawable);
  
  public MenuItem setIconTintBlendMode(BlendMode paramBlendMode)
  {
    paramBlendMode = BlendMode.blendModeToPorterDuffMode(paramBlendMode);
    if (paramBlendMode != null) {
      return setIconTintMode(paramBlendMode);
    }
    return this;
  }
  
  public MenuItem setIconTintList(ColorStateList paramColorStateList)
  {
    return this;
  }
  
  public MenuItem setIconTintMode(PorterDuff.Mode paramMode)
  {
    return this;
  }
  
  public abstract MenuItem setIntent(Intent paramIntent);
  
  public abstract MenuItem setNumericShortcut(char paramChar);
  
  public MenuItem setNumericShortcut(char paramChar, int paramInt)
  {
    if ((0x1100F & paramInt) == 4096) {
      return setNumericShortcut(paramChar);
    }
    return this;
  }
  
  public abstract MenuItem setOnActionExpandListener(OnActionExpandListener paramOnActionExpandListener);
  
  public abstract MenuItem setOnMenuItemClickListener(OnMenuItemClickListener paramOnMenuItemClickListener);
  
  public abstract MenuItem setShortcut(char paramChar1, char paramChar2);
  
  public MenuItem setShortcut(char paramChar1, char paramChar2, int paramInt1, int paramInt2)
  {
    if (((paramInt2 & 0x1100F) == 4096) && ((0x1100F & paramInt1) == 4096)) {
      return setShortcut(paramChar1, paramChar2);
    }
    return this;
  }
  
  public abstract void setShowAsAction(int paramInt);
  
  public abstract MenuItem setShowAsActionFlags(int paramInt);
  
  public abstract MenuItem setTitle(int paramInt);
  
  public abstract MenuItem setTitle(CharSequence paramCharSequence);
  
  public abstract MenuItem setTitleCondensed(CharSequence paramCharSequence);
  
  public MenuItem setTooltipText(CharSequence paramCharSequence)
  {
    return this;
  }
  
  public abstract MenuItem setVisible(boolean paramBoolean);
  
  public static abstract interface OnActionExpandListener
  {
    public abstract boolean onMenuItemActionCollapse(MenuItem paramMenuItem);
    
    public abstract boolean onMenuItemActionExpand(MenuItem paramMenuItem);
  }
  
  public static abstract interface OnMenuItemClickListener
  {
    public abstract boolean onMenuItemClick(MenuItem paramMenuItem);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/MenuItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */