package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnTouchModeChangeListener;
import android.view.Window;
import com.android.internal.R.styleable;
import java.util.ArrayList;
import java.util.List;

public class TabHost
  extends FrameLayout
  implements ViewTreeObserver.OnTouchModeChangeListener
{
  private static final int TABWIDGET_LOCATION_BOTTOM = 3;
  private static final int TABWIDGET_LOCATION_LEFT = 0;
  private static final int TABWIDGET_LOCATION_RIGHT = 2;
  private static final int TABWIDGET_LOCATION_TOP = 1;
  @UnsupportedAppUsage
  protected int mCurrentTab = -1;
  private View mCurrentView = null;
  protected LocalActivityManager mLocalActivityManager = null;
  @UnsupportedAppUsage
  private OnTabChangeListener mOnTabChangeListener;
  private FrameLayout mTabContent;
  private View.OnKeyListener mTabKeyListener;
  private int mTabLayoutId;
  @UnsupportedAppUsage
  private List<TabSpec> mTabSpecs = new ArrayList(2);
  private TabWidget mTabWidget;
  
  public TabHost(Context paramContext)
  {
    super(paramContext);
    initTabHost();
  }
  
  public TabHost(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842883);
  }
  
  public TabHost(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public TabHost(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.TabWidget, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.TabWidget, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    this.mTabLayoutId = localTypedArray.getResourceId(4, 0);
    localTypedArray.recycle();
    if (this.mTabLayoutId == 0) {
      this.mTabLayoutId = 17367330;
    }
    initTabHost();
  }
  
  private int getTabWidgetLocation()
  {
    int i = this.mTabWidget.getOrientation();
    int j = 1;
    if (i != 1)
    {
      if (this.mTabContent.getTop() < this.mTabWidget.getTop()) {
        j = 3;
      }
    }
    else if (this.mTabContent.getLeft() < this.mTabWidget.getLeft()) {
      j = 2;
    } else {
      j = 0;
    }
    return j;
  }
  
  private void initTabHost()
  {
    setFocusableInTouchMode(true);
    setDescendantFocusability(262144);
    this.mCurrentTab = -1;
    this.mCurrentView = null;
  }
  
  private void invokeOnTabChangeListener()
  {
    OnTabChangeListener localOnTabChangeListener = this.mOnTabChangeListener;
    if (localOnTabChangeListener != null) {
      localOnTabChangeListener.onTabChanged(getCurrentTabTag());
    }
  }
  
  public void addTab(TabSpec paramTabSpec)
  {
    if (paramTabSpec.mIndicatorStrategy != null)
    {
      if (paramTabSpec.mContentStrategy != null)
      {
        View localView = paramTabSpec.mIndicatorStrategy.createIndicatorView();
        localView.setOnKeyListener(this.mTabKeyListener);
        if ((paramTabSpec.mIndicatorStrategy instanceof ViewIndicatorStrategy)) {
          this.mTabWidget.setStripEnabled(false);
        }
        this.mTabWidget.addView(localView);
        this.mTabSpecs.add(paramTabSpec);
        if (this.mCurrentTab == -1) {
          setCurrentTab(0);
        }
        return;
      }
      throw new IllegalArgumentException("you must specify a way to create the tab content");
    }
    throw new IllegalArgumentException("you must specify a way to create the tab indicator.");
  }
  
  public void clearAllTabs()
  {
    this.mTabWidget.removeAllViews();
    initTabHost();
    this.mTabContent.removeAllViews();
    this.mTabSpecs.clear();
    requestLayout();
    invalidate();
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    boolean bool = super.dispatchKeyEvent(paramKeyEvent);
    if ((!bool) && (paramKeyEvent.getAction() == 0))
    {
      View localView = this.mCurrentView;
      if ((localView != null) && (localView.isRootNamespace()) && (this.mCurrentView.hasFocus()))
      {
        int i = getTabWidgetLocation();
        int j;
        int k;
        if (i != 0)
        {
          if (i != 2)
          {
            if (i != 3)
            {
              i = 19;
              j = 33;
              k = 2;
            }
            else
            {
              i = 20;
              j = 130;
              k = 4;
            }
          }
          else
          {
            i = 22;
            j = 66;
            k = 3;
          }
        }
        else
        {
          i = 21;
          j = 17;
          k = 1;
        }
        if ((paramKeyEvent.getKeyCode() == i) && (this.mCurrentView.findFocus().focusSearch(j) == null))
        {
          this.mTabWidget.getChildTabViewAt(this.mCurrentTab).requestFocus();
          playSoundEffect(k);
          return true;
        }
      }
    }
    return bool;
  }
  
  public void dispatchWindowFocusChanged(boolean paramBoolean)
  {
    View localView = this.mCurrentView;
    if (localView != null) {
      localView.dispatchWindowFocusChanged(paramBoolean);
    }
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return TabHost.class.getName();
  }
  
  public int getCurrentTab()
  {
    return this.mCurrentTab;
  }
  
  public String getCurrentTabTag()
  {
    int i = this.mCurrentTab;
    if ((i >= 0) && (i < this.mTabSpecs.size())) {
      return ((TabSpec)this.mTabSpecs.get(this.mCurrentTab)).getTag();
    }
    return null;
  }
  
  public View getCurrentTabView()
  {
    int i = this.mCurrentTab;
    if ((i >= 0) && (i < this.mTabSpecs.size())) {
      return this.mTabWidget.getChildTabViewAt(this.mCurrentTab);
    }
    return null;
  }
  
  public View getCurrentView()
  {
    return this.mCurrentView;
  }
  
  public FrameLayout getTabContentView()
  {
    return this.mTabContent;
  }
  
  public TabWidget getTabWidget()
  {
    return this.mTabWidget;
  }
  
  public TabSpec newTabSpec(String paramString)
  {
    if (paramString != null) {
      return new TabSpec(paramString, null);
    }
    throw new IllegalArgumentException("tag must be non-null");
  }
  
  public void onTouchModeChanged(boolean paramBoolean) {}
  
  public void sendAccessibilityEventInternal(int paramInt) {}
  
  public void setCurrentTab(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.mTabSpecs.size()))
    {
      int i = this.mCurrentTab;
      if (paramInt == i) {
        return;
      }
      if (i != -1) {
        ((TabSpec)this.mTabSpecs.get(i)).mContentStrategy.tabClosed();
      }
      this.mCurrentTab = paramInt;
      TabSpec localTabSpec = (TabSpec)this.mTabSpecs.get(paramInt);
      this.mTabWidget.focusCurrentTab(this.mCurrentTab);
      this.mCurrentView = localTabSpec.mContentStrategy.getContentView();
      if (this.mCurrentView.getParent() == null) {
        this.mTabContent.addView(this.mCurrentView, new ViewGroup.LayoutParams(-1, -1));
      }
      if (!this.mTabWidget.hasFocus()) {
        this.mCurrentView.requestFocus();
      }
      invokeOnTabChangeListener();
      return;
    }
  }
  
  public void setCurrentTabByTag(String paramString)
  {
    int i = 0;
    int j = this.mTabSpecs.size();
    while (i < j)
    {
      if (((TabSpec)this.mTabSpecs.get(i)).getTag().equals(paramString))
      {
        setCurrentTab(i);
        break;
      }
      i++;
    }
  }
  
  public void setOnTabChangedListener(OnTabChangeListener paramOnTabChangeListener)
  {
    this.mOnTabChangeListener = paramOnTabChangeListener;
  }
  
  public void setup()
  {
    this.mTabWidget = ((TabWidget)findViewById(16908307));
    if (this.mTabWidget != null)
    {
      this.mTabKeyListener = new View.OnKeyListener()
      {
        public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if (KeyEvent.isModifierKey(paramAnonymousInt)) {
            return false;
          }
          if ((paramAnonymousInt != 61) && (paramAnonymousInt != 62) && (paramAnonymousInt != 66)) {
            switch (paramAnonymousInt)
            {
            default: 
              TabHost.this.mTabContent.requestFocus(2);
              return TabHost.this.mTabContent.dispatchKeyEvent(paramAnonymousKeyEvent);
            }
          }
          return false;
        }
      };
      this.mTabWidget.setTabSelectionListener(new TabWidget.OnTabSelectionChanged()
      {
        public void onTabSelectionChanged(int paramAnonymousInt, boolean paramAnonymousBoolean)
        {
          TabHost.this.setCurrentTab(paramAnonymousInt);
          if (paramAnonymousBoolean) {
            TabHost.this.mTabContent.requestFocus(2);
          }
        }
      });
      this.mTabContent = ((FrameLayout)findViewById(16908305));
      if (this.mTabContent != null) {
        return;
      }
      throw new RuntimeException("Your TabHost must have a FrameLayout whose id attribute is 'android.R.id.tabcontent'");
    }
    throw new RuntimeException("Your TabHost must have a TabWidget whose id attribute is 'android.R.id.tabs'");
  }
  
  public void setup(LocalActivityManager paramLocalActivityManager)
  {
    setup();
    this.mLocalActivityManager = paramLocalActivityManager;
  }
  
  private static abstract interface ContentStrategy
  {
    public abstract View getContentView();
    
    public abstract void tabClosed();
  }
  
  private class FactoryContentStrategy
    implements TabHost.ContentStrategy
  {
    private TabHost.TabContentFactory mFactory;
    private View mTabContent;
    private final CharSequence mTag;
    
    public FactoryContentStrategy(CharSequence paramCharSequence, TabHost.TabContentFactory paramTabContentFactory)
    {
      this.mTag = paramCharSequence;
      this.mFactory = paramTabContentFactory;
    }
    
    public View getContentView()
    {
      if (this.mTabContent == null) {
        this.mTabContent = this.mFactory.createTabContent(this.mTag.toString());
      }
      this.mTabContent.setVisibility(0);
      return this.mTabContent;
    }
    
    public void tabClosed()
    {
      this.mTabContent.setVisibility(8);
    }
  }
  
  private static abstract interface IndicatorStrategy
  {
    public abstract View createIndicatorView();
  }
  
  private class IntentContentStrategy
    implements TabHost.ContentStrategy
  {
    private final Intent mIntent;
    private View mLaunchedView;
    private final String mTag;
    
    private IntentContentStrategy(String paramString, Intent paramIntent)
    {
      this.mTag = paramString;
      this.mIntent = paramIntent;
    }
    
    @UnsupportedAppUsage
    public View getContentView()
    {
      if (TabHost.this.mLocalActivityManager != null)
      {
        Object localObject = TabHost.this.mLocalActivityManager.startActivity(this.mTag, this.mIntent);
        if (localObject != null) {
          localObject = ((Window)localObject).getDecorView();
        } else {
          localObject = null;
        }
        View localView = this.mLaunchedView;
        if ((localView != localObject) && (localView != null) && (localView.getParent() != null)) {
          TabHost.this.mTabContent.removeView(this.mLaunchedView);
        }
        this.mLaunchedView = ((View)localObject);
        localObject = this.mLaunchedView;
        if (localObject != null)
        {
          ((View)localObject).setVisibility(0);
          this.mLaunchedView.setFocusableInTouchMode(true);
          ((ViewGroup)this.mLaunchedView).setDescendantFocusability(262144);
        }
        return this.mLaunchedView;
      }
      throw new IllegalStateException("Did you forget to call 'public void setup(LocalActivityManager activityGroup)'?");
    }
    
    @UnsupportedAppUsage
    public void tabClosed()
    {
      View localView = this.mLaunchedView;
      if (localView != null) {
        localView.setVisibility(8);
      }
    }
  }
  
  private class LabelAndIconIndicatorStrategy
    implements TabHost.IndicatorStrategy
  {
    private final Drawable mIcon;
    private final CharSequence mLabel;
    
    private LabelAndIconIndicatorStrategy(CharSequence paramCharSequence, Drawable paramDrawable)
    {
      this.mLabel = paramCharSequence;
      this.mIcon = paramDrawable;
    }
    
    public View createIndicatorView()
    {
      Context localContext = TabHost.this.getContext();
      View localView = ((LayoutInflater)localContext.getSystemService("layout_inflater")).inflate(TabHost.this.mTabLayoutId, TabHost.this.mTabWidget, false);
      TextView localTextView = (TextView)localView.findViewById(16908310);
      ImageView localImageView = (ImageView)localView.findViewById(16908294);
      int i = localImageView.getVisibility();
      int j = 1;
      int k;
      if (i == 8) {
        k = 1;
      } else {
        k = 0;
      }
      i = j;
      if (k != 0) {
        if (TextUtils.isEmpty(this.mLabel)) {
          i = j;
        } else {
          i = 0;
        }
      }
      localTextView.setText(this.mLabel);
      if (i != 0)
      {
        Drawable localDrawable = this.mIcon;
        if (localDrawable != null)
        {
          localImageView.setImageDrawable(localDrawable);
          localImageView.setVisibility(0);
        }
      }
      if (localContext.getApplicationInfo().targetSdkVersion <= 4)
      {
        localView.setBackgroundResource(17303721);
        localTextView.setTextColor(localContext.getColorStateList(17171015));
      }
      return localView;
    }
  }
  
  private class LabelIndicatorStrategy
    implements TabHost.IndicatorStrategy
  {
    private final CharSequence mLabel;
    
    private LabelIndicatorStrategy(CharSequence paramCharSequence)
    {
      this.mLabel = paramCharSequence;
    }
    
    public View createIndicatorView()
    {
      Context localContext = TabHost.this.getContext();
      View localView = ((LayoutInflater)localContext.getSystemService("layout_inflater")).inflate(TabHost.this.mTabLayoutId, TabHost.this.mTabWidget, false);
      TextView localTextView = (TextView)localView.findViewById(16908310);
      localTextView.setText(this.mLabel);
      if (localContext.getApplicationInfo().targetSdkVersion <= 4)
      {
        localView.setBackgroundResource(17303721);
        localTextView.setTextColor(localContext.getColorStateList(17171015));
      }
      return localView;
    }
  }
  
  public static abstract interface OnTabChangeListener
  {
    public abstract void onTabChanged(String paramString);
  }
  
  public static abstract interface TabContentFactory
  {
    public abstract View createTabContent(String paramString);
  }
  
  public class TabSpec
  {
    @UnsupportedAppUsage
    private TabHost.ContentStrategy mContentStrategy;
    @UnsupportedAppUsage
    private TabHost.IndicatorStrategy mIndicatorStrategy;
    private final String mTag;
    
    private TabSpec(String paramString)
    {
      this.mTag = paramString;
    }
    
    public String getTag()
    {
      return this.mTag;
    }
    
    public TabSpec setContent(int paramInt)
    {
      this.mContentStrategy = new TabHost.ViewIdContentStrategy(TabHost.this, paramInt, null);
      return this;
    }
    
    public TabSpec setContent(Intent paramIntent)
    {
      this.mContentStrategy = new TabHost.IntentContentStrategy(TabHost.this, this.mTag, paramIntent, null);
      return this;
    }
    
    public TabSpec setContent(TabHost.TabContentFactory paramTabContentFactory)
    {
      this.mContentStrategy = new TabHost.FactoryContentStrategy(TabHost.this, this.mTag, paramTabContentFactory);
      return this;
    }
    
    public TabSpec setIndicator(View paramView)
    {
      this.mIndicatorStrategy = new TabHost.ViewIndicatorStrategy(TabHost.this, paramView, null);
      return this;
    }
    
    public TabSpec setIndicator(CharSequence paramCharSequence)
    {
      this.mIndicatorStrategy = new TabHost.LabelIndicatorStrategy(TabHost.this, paramCharSequence, null);
      return this;
    }
    
    public TabSpec setIndicator(CharSequence paramCharSequence, Drawable paramDrawable)
    {
      this.mIndicatorStrategy = new TabHost.LabelAndIconIndicatorStrategy(TabHost.this, paramCharSequence, paramDrawable, null);
      return this;
    }
  }
  
  private class ViewIdContentStrategy
    implements TabHost.ContentStrategy
  {
    private final View mView;
    
    private ViewIdContentStrategy(int paramInt)
    {
      this.mView = TabHost.this.mTabContent.findViewById(paramInt);
      this$1 = this.mView;
      if (TabHost.this != null)
      {
        TabHost.this.setVisibility(8);
        return;
      }
      this$1 = new StringBuilder();
      TabHost.this.append("Could not create tab content because could not find view with id ");
      TabHost.this.append(paramInt);
      throw new RuntimeException(TabHost.this.toString());
    }
    
    public View getContentView()
    {
      this.mView.setVisibility(0);
      return this.mView;
    }
    
    public void tabClosed()
    {
      this.mView.setVisibility(8);
    }
  }
  
  private class ViewIndicatorStrategy
    implements TabHost.IndicatorStrategy
  {
    private final View mView;
    
    private ViewIndicatorStrategy(View paramView)
    {
      this.mView = paramView;
    }
    
    public View createIndicatorView()
    {
      return this.mView;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/TabHost.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */