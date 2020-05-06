package android.view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.android.internal.R.styleable;
import com.android.internal.view.menu.MenuItemImpl;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class MenuInflater
{
  private static final Class<?>[] ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE = ACTION_VIEW_CONSTRUCTOR_SIGNATURE;
  private static final Class<?>[] ACTION_VIEW_CONSTRUCTOR_SIGNATURE = { Context.class };
  private static final String LOG_TAG = "MenuInflater";
  private static final int NO_ID = 0;
  private static final String XML_GROUP = "group";
  private static final String XML_ITEM = "item";
  private static final String XML_MENU = "menu";
  private final Object[] mActionProviderConstructorArguments;
  private final Object[] mActionViewConstructorArguments;
  private Context mContext;
  private Object mRealOwner;
  
  public MenuInflater(Context paramContext)
  {
    this.mContext = paramContext;
    this.mActionViewConstructorArguments = new Object[] { paramContext };
    this.mActionProviderConstructorArguments = this.mActionViewConstructorArguments;
  }
  
  public MenuInflater(Context paramContext, Object paramObject)
  {
    this.mContext = paramContext;
    this.mRealOwner = paramObject;
    this.mActionViewConstructorArguments = new Object[] { paramContext };
    this.mActionProviderConstructorArguments = this.mActionViewConstructorArguments;
  }
  
  private Object findRealOwner(Object paramObject)
  {
    if ((paramObject instanceof Activity)) {
      return paramObject;
    }
    if ((paramObject instanceof ContextWrapper)) {
      return findRealOwner(((ContextWrapper)paramObject).getBaseContext());
    }
    return paramObject;
  }
  
  private Object getRealOwner()
  {
    if (this.mRealOwner == null) {
      this.mRealOwner = findRealOwner(this.mContext);
    }
    return this.mRealOwner;
  }
  
  private void parseMenu(XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Menu paramMenu)
    throws XmlPullParserException, IOException
  {
    MenuState localMenuState = new MenuState(paramMenu);
    int i = paramXmlPullParser.getEventType();
    int j = 0;
    Object localObject = null;
    do
    {
      if (i == 2)
      {
        paramMenu = paramXmlPullParser.getName();
        if (paramMenu.equals("menu"))
        {
          i = paramXmlPullParser.next();
          break;
        }
        paramXmlPullParser = new StringBuilder();
        paramXmlPullParser.append("Expecting menu, got ");
        paramXmlPullParser.append(paramMenu);
        throw new RuntimeException(paramXmlPullParser.toString());
      }
      k = paramXmlPullParser.next();
      i = k;
    } while (k != 1);
    i = k;
    int k = 0;
    int m = i;
    while (k == 0) {
      if (m != 1)
      {
        int n;
        if (m != 2)
        {
          if (m != 3)
          {
            i = j;
            paramMenu = (Menu)localObject;
            n = k;
          }
          else
          {
            String str = paramXmlPullParser.getName();
            if ((j != 0) && (str.equals(localObject)))
            {
              i = 0;
              paramMenu = null;
              n = k;
            }
            else if (str.equals("group"))
            {
              localMenuState.resetGroup();
              i = j;
              paramMenu = (Menu)localObject;
              n = k;
            }
            else if (str.equals("item"))
            {
              i = j;
              paramMenu = (Menu)localObject;
              n = k;
              if (!localMenuState.hasAddedItem()) {
                if ((localMenuState.itemActionProvider != null) && (localMenuState.itemActionProvider.hasSubMenu()))
                {
                  registerMenu(localMenuState.addSubMenuItem(), paramAttributeSet);
                  i = j;
                  paramMenu = (Menu)localObject;
                  n = k;
                }
                else
                {
                  registerMenu(localMenuState.addItem(), paramAttributeSet);
                  i = j;
                  paramMenu = (Menu)localObject;
                  n = k;
                }
              }
            }
            else
            {
              i = j;
              paramMenu = (Menu)localObject;
              n = k;
              if (str.equals("menu"))
              {
                n = 1;
                i = j;
                paramMenu = (Menu)localObject;
              }
            }
          }
        }
        else if (j != 0)
        {
          i = j;
          paramMenu = (Menu)localObject;
          n = k;
        }
        else
        {
          paramMenu = paramXmlPullParser.getName();
          if (paramMenu.equals("group"))
          {
            localMenuState.readGroup(paramAttributeSet);
            i = j;
            paramMenu = (Menu)localObject;
            n = k;
          }
          else if (paramMenu.equals("item"))
          {
            localMenuState.readItem(paramAttributeSet);
            i = j;
            paramMenu = (Menu)localObject;
            n = k;
          }
          else if (paramMenu.equals("menu"))
          {
            paramMenu = localMenuState.addSubMenuItem();
            registerMenu(paramMenu, paramAttributeSet);
            parseMenu(paramXmlPullParser, paramAttributeSet, paramMenu);
            i = j;
            paramMenu = (Menu)localObject;
            n = k;
          }
          else
          {
            i = 1;
            n = k;
          }
        }
        m = paramXmlPullParser.next();
        j = i;
        localObject = paramMenu;
        k = n;
      }
      else
      {
        throw new RuntimeException("Unexpected end of document");
      }
    }
  }
  
  private void registerMenu(MenuItem paramMenuItem, AttributeSet paramAttributeSet) {}
  
  private void registerMenu(SubMenu paramSubMenu, AttributeSet paramAttributeSet) {}
  
  Context getContext()
  {
    return this.mContext;
  }
  
  /* Error */
  public void inflate(int paramInt, Menu paramMenu)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aconst_null
    //   6: astore 5
    //   8: aload_0
    //   9: getfield 54	android/view/MenuInflater:mContext	Landroid/content/Context;
    //   12: invokevirtual 182	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   15: iload_1
    //   16: invokevirtual 188	android/content/res/Resources:getLayout	(I)Landroid/content/res/XmlResourceParser;
    //   19: astore 6
    //   21: aload 6
    //   23: astore 5
    //   25: aload 6
    //   27: astore_3
    //   28: aload 6
    //   30: astore 4
    //   32: aload_0
    //   33: aload 6
    //   35: aload 6
    //   37: invokestatic 194	android/util/Xml:asAttributeSet	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/AttributeSet;
    //   40: aload_2
    //   41: invokespecial 172	android/view/MenuInflater:parseMenu	(Lorg/xmlpull/v1/XmlPullParser;Landroid/util/AttributeSet;Landroid/view/Menu;)V
    //   44: aload 6
    //   46: ifnull +10 -> 56
    //   49: aload 6
    //   51: invokeinterface 199 1 0
    //   56: return
    //   57: astore_2
    //   58: goto +54 -> 112
    //   61: astore 4
    //   63: aload_3
    //   64: astore 5
    //   66: new 201	android/view/InflateException
    //   69: astore_2
    //   70: aload_3
    //   71: astore 5
    //   73: aload_2
    //   74: ldc -53
    //   76: aload 4
    //   78: invokespecial 206	android/view/InflateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   81: aload_3
    //   82: astore 5
    //   84: aload_2
    //   85: athrow
    //   86: astore_3
    //   87: aload 4
    //   89: astore 5
    //   91: new 201	android/view/InflateException
    //   94: astore_2
    //   95: aload 4
    //   97: astore 5
    //   99: aload_2
    //   100: ldc -53
    //   102: aload_3
    //   103: invokespecial 206	android/view/InflateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   106: aload 4
    //   108: astore 5
    //   110: aload_2
    //   111: athrow
    //   112: aload 5
    //   114: ifnull +10 -> 124
    //   117: aload 5
    //   119: invokeinterface 199 1 0
    //   124: aload_2
    //   125: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	126	0	this	MenuInflater
    //   0	126	1	paramInt	int
    //   0	126	2	paramMenu	Menu
    //   1	81	3	localObject1	Object
    //   86	17	3	localXmlPullParserException	XmlPullParserException
    //   3	28	4	localObject2	Object
    //   61	46	4	localIOException	IOException
    //   6	112	5	localObject3	Object
    //   19	31	6	localXmlResourceParser	android.content.res.XmlResourceParser
    // Exception table:
    //   from	to	target	type
    //   8	21	57	finally
    //   32	44	57	finally
    //   66	70	57	finally
    //   73	81	57	finally
    //   84	86	57	finally
    //   91	95	57	finally
    //   99	106	57	finally
    //   110	112	57	finally
    //   8	21	61	java/io/IOException
    //   32	44	61	java/io/IOException
    //   8	21	86	org/xmlpull/v1/XmlPullParserException
    //   32	44	86	org/xmlpull/v1/XmlPullParserException
  }
  
  private static class InflatedOnMenuItemClickListener
    implements MenuItem.OnMenuItemClickListener
  {
    private static final Class<?>[] PARAM_TYPES = { MenuItem.class };
    private Method mMethod;
    private Object mRealOwner;
    
    public InflatedOnMenuItemClickListener(Object paramObject, String paramString)
    {
      this.mRealOwner = paramObject;
      Class localClass = paramObject.getClass();
      try
      {
        this.mMethod = localClass.getMethod(paramString, PARAM_TYPES);
        return;
      }
      catch (Exception paramObject)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Couldn't resolve menu item onClick handler ");
        localStringBuilder.append(paramString);
        localStringBuilder.append(" in class ");
        localStringBuilder.append(localClass.getName());
        paramString = new InflateException(localStringBuilder.toString());
        paramString.initCause((Throwable)paramObject);
        throw paramString;
      }
    }
    
    public boolean onMenuItemClick(MenuItem paramMenuItem)
    {
      try
      {
        if (this.mMethod.getReturnType() == Boolean.TYPE) {
          return ((Boolean)this.mMethod.invoke(this.mRealOwner, new Object[] { paramMenuItem })).booleanValue();
        }
        this.mMethod.invoke(this.mRealOwner, new Object[] { paramMenuItem });
        return true;
      }
      catch (Exception paramMenuItem)
      {
        throw new RuntimeException(paramMenuItem);
      }
    }
  }
  
  private class MenuState
  {
    private static final int defaultGroupId = 0;
    private static final int defaultItemCategory = 0;
    private static final int defaultItemCheckable = 0;
    private static final boolean defaultItemChecked = false;
    private static final boolean defaultItemEnabled = true;
    private static final int defaultItemId = 0;
    private static final int defaultItemOrder = 0;
    private static final boolean defaultItemVisible = true;
    private int groupCategory;
    private int groupCheckable;
    private boolean groupEnabled;
    private int groupId;
    private int groupOrder;
    private boolean groupVisible;
    private ActionProvider itemActionProvider;
    private String itemActionProviderClassName;
    private String itemActionViewClassName;
    private int itemActionViewLayout;
    private boolean itemAdded;
    private int itemAlphabeticModifiers;
    private char itemAlphabeticShortcut;
    private int itemCategoryOrder;
    private int itemCheckable;
    private boolean itemChecked;
    private CharSequence itemContentDescription;
    private boolean itemEnabled;
    private int itemIconResId;
    private ColorStateList itemIconTintList = null;
    private int itemId;
    private String itemListenerMethodName;
    private int itemNumericModifiers;
    private char itemNumericShortcut;
    private int itemShowAsAction;
    private CharSequence itemTitle;
    private CharSequence itemTitleCondensed;
    private CharSequence itemTooltipText;
    private boolean itemVisible;
    private BlendMode mItemIconBlendMode = null;
    private Menu menu;
    
    public MenuState(Menu paramMenu)
    {
      this.menu = paramMenu;
      resetGroup();
    }
    
    private char getShortcut(String paramString)
    {
      if (paramString == null) {
        return '\000';
      }
      return paramString.charAt(0);
    }
    
    private <T> T newInstance(String paramString, Class<?>[] paramArrayOfClass, Object[] paramArrayOfObject)
    {
      try
      {
        paramArrayOfClass = MenuInflater.this.mContext.getClassLoader().loadClass(paramString).getConstructor(paramArrayOfClass);
        paramArrayOfClass.setAccessible(true);
        paramArrayOfClass = paramArrayOfClass.newInstance(paramArrayOfObject);
        return paramArrayOfClass;
      }
      catch (Exception paramArrayOfObject)
      {
        paramArrayOfClass = new StringBuilder();
        paramArrayOfClass.append("Cannot instantiate class: ");
        paramArrayOfClass.append(paramString);
        Log.w("MenuInflater", paramArrayOfClass.toString(), paramArrayOfObject);
      }
      return null;
    }
    
    private void setItem(MenuItem paramMenuItem)
    {
      Object localObject = paramMenuItem.setChecked(this.itemChecked).setVisible(this.itemVisible).setEnabled(this.itemEnabled);
      boolean bool;
      if (this.itemCheckable >= 1) {
        bool = true;
      } else {
        bool = false;
      }
      ((MenuItem)localObject).setCheckable(bool).setTitleCondensed(this.itemTitleCondensed).setIcon(this.itemIconResId).setAlphabeticShortcut(this.itemAlphabeticShortcut, this.itemAlphabeticModifiers).setNumericShortcut(this.itemNumericShortcut, this.itemNumericModifiers);
      int i = this.itemShowAsAction;
      if (i >= 0) {
        paramMenuItem.setShowAsAction(i);
      }
      localObject = this.mItemIconBlendMode;
      if (localObject != null) {
        paramMenuItem.setIconTintBlendMode((BlendMode)localObject);
      }
      localObject = this.itemIconTintList;
      if (localObject != null) {
        paramMenuItem.setIconTintList((ColorStateList)localObject);
      }
      if (this.itemListenerMethodName != null) {
        if (!MenuInflater.this.mContext.isRestricted()) {
          paramMenuItem.setOnMenuItemClickListener(new MenuInflater.InflatedOnMenuItemClickListener(MenuInflater.this.getRealOwner(), this.itemListenerMethodName));
        } else {
          throw new IllegalStateException("The android:onClick attribute cannot be used within a restricted context");
        }
      }
      if ((paramMenuItem instanceof MenuItemImpl))
      {
        localObject = (MenuItemImpl)paramMenuItem;
        if (this.itemCheckable >= 2) {
          ((MenuItemImpl)localObject).setExclusiveCheckable(true);
        }
      }
      i = 0;
      localObject = this.itemActionViewClassName;
      if (localObject != null)
      {
        paramMenuItem.setActionView((View)newInstance((String)localObject, MenuInflater.ACTION_VIEW_CONSTRUCTOR_SIGNATURE, MenuInflater.this.mActionViewConstructorArguments));
        i = 1;
      }
      int j = this.itemActionViewLayout;
      if (j > 0) {
        if (i == 0) {
          paramMenuItem.setActionView(j);
        } else {
          Log.w("MenuInflater", "Ignoring attribute 'itemActionViewLayout'. Action view already specified.");
        }
      }
      localObject = this.itemActionProvider;
      if (localObject != null) {
        paramMenuItem.setActionProvider((ActionProvider)localObject);
      }
      paramMenuItem.setContentDescription(this.itemContentDescription);
      paramMenuItem.setTooltipText(this.itemTooltipText);
    }
    
    public MenuItem addItem()
    {
      this.itemAdded = true;
      MenuItem localMenuItem = this.menu.add(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle);
      setItem(localMenuItem);
      return localMenuItem;
    }
    
    public SubMenu addSubMenuItem()
    {
      this.itemAdded = true;
      SubMenu localSubMenu = this.menu.addSubMenu(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle);
      setItem(localSubMenu.getItem());
      return localSubMenu;
    }
    
    public boolean hasAddedItem()
    {
      return this.itemAdded;
    }
    
    public void readGroup(AttributeSet paramAttributeSet)
    {
      paramAttributeSet = MenuInflater.this.mContext.obtainStyledAttributes(paramAttributeSet, R.styleable.MenuGroup);
      this.groupId = paramAttributeSet.getResourceId(1, 0);
      this.groupCategory = paramAttributeSet.getInt(3, 0);
      this.groupOrder = paramAttributeSet.getInt(4, 0);
      this.groupCheckable = paramAttributeSet.getInt(5, 0);
      this.groupVisible = paramAttributeSet.getBoolean(2, true);
      this.groupEnabled = paramAttributeSet.getBoolean(0, true);
      paramAttributeSet.recycle();
    }
    
    public void readItem(AttributeSet paramAttributeSet)
    {
      paramAttributeSet = MenuInflater.this.mContext.obtainStyledAttributes(paramAttributeSet, R.styleable.MenuItem);
      this.itemId = paramAttributeSet.getResourceId(2, 0);
      this.itemCategoryOrder = (0xFFFF0000 & paramAttributeSet.getInt(5, this.groupCategory) | 0xFFFF & paramAttributeSet.getInt(6, this.groupOrder));
      this.itemTitle = paramAttributeSet.getText(7);
      this.itemTitleCondensed = paramAttributeSet.getText(8);
      this.itemIconResId = paramAttributeSet.getResourceId(0, 0);
      if (paramAttributeSet.hasValue(22)) {
        this.mItemIconBlendMode = Drawable.parseBlendMode(paramAttributeSet.getInt(22, -1), this.mItemIconBlendMode);
      } else {
        this.mItemIconBlendMode = null;
      }
      if (paramAttributeSet.hasValue(21)) {
        this.itemIconTintList = paramAttributeSet.getColorStateList(21);
      } else {
        this.itemIconTintList = null;
      }
      this.itemAlphabeticShortcut = getShortcut(paramAttributeSet.getString(9));
      this.itemAlphabeticModifiers = paramAttributeSet.getInt(19, 4096);
      this.itemNumericShortcut = getShortcut(paramAttributeSet.getString(10));
      this.itemNumericModifiers = paramAttributeSet.getInt(20, 4096);
      if (paramAttributeSet.hasValue(11)) {
        this.itemCheckable = paramAttributeSet.getBoolean(11, false);
      } else {
        this.itemCheckable = this.groupCheckable;
      }
      this.itemChecked = paramAttributeSet.getBoolean(3, false);
      this.itemVisible = paramAttributeSet.getBoolean(4, this.groupVisible);
      boolean bool = this.groupEnabled;
      int i = 1;
      this.itemEnabled = paramAttributeSet.getBoolean(1, bool);
      this.itemShowAsAction = paramAttributeSet.getInt(14, -1);
      this.itemListenerMethodName = paramAttributeSet.getString(12);
      this.itemActionViewLayout = paramAttributeSet.getResourceId(15, 0);
      this.itemActionViewClassName = paramAttributeSet.getString(16);
      this.itemActionProviderClassName = paramAttributeSet.getString(17);
      if (this.itemActionProviderClassName == null) {
        i = 0;
      }
      if ((i != 0) && (this.itemActionViewLayout == 0) && (this.itemActionViewClassName == null))
      {
        this.itemActionProvider = ((ActionProvider)newInstance(this.itemActionProviderClassName, MenuInflater.ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE, MenuInflater.this.mActionProviderConstructorArguments));
      }
      else
      {
        if (i != 0) {
          Log.w("MenuInflater", "Ignoring attribute 'actionProviderClass'. Action view already specified.");
        }
        this.itemActionProvider = null;
      }
      this.itemContentDescription = paramAttributeSet.getText(13);
      this.itemTooltipText = paramAttributeSet.getText(18);
      paramAttributeSet.recycle();
      this.itemAdded = false;
    }
    
    public void resetGroup()
    {
      this.groupId = 0;
      this.groupCategory = 0;
      this.groupOrder = 0;
      this.groupCheckable = 0;
      this.groupVisible = true;
      this.groupEnabled = true;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/MenuInflater.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */