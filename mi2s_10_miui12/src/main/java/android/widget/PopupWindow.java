package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.IBinder;
import android.transition.Transition;
import android.transition.Transition.EpicenterCallback;
import android.transition.Transition.TransitionListener;
import android.transition.TransitionInflater;
import android.transition.TransitionListenerAdapter;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.KeyboardShortcutGroup;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerGlobal;
import com.android.internal.R.styleable;
import com.miui.internal.variable.api.Overridable;
import com.miui.internal.variable.api.v29.Android_Widget_PopupWindow.Extension;
import com.miui.internal.variable.api.v29.Android_Widget_PopupWindow.Interface;
import java.lang.ref.WeakReference;
import java.util.List;

public class PopupWindow
{
  private static final int[] ABOVE_ANCHOR_STATE_SET = { 16842922 };
  private static final int ANIMATION_STYLE_DEFAULT = -1;
  private static final int DEFAULT_ANCHORED_GRAVITY = 8388659;
  public static final int INPUT_METHOD_FROM_FOCUSABLE = 0;
  public static final int INPUT_METHOD_NEEDED = 1;
  public static final int INPUT_METHOD_NOT_NEEDED = 2;
  @UnsupportedAppUsage
  private boolean mAboveAnchor;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private Drawable mAboveAnchorBackgroundDrawable;
  private boolean mAllowScrollingAnchorParent = true;
  @UnsupportedAppUsage
  private WeakReference<View> mAnchor;
  private WeakReference<View> mAnchorRoot;
  private int mAnchorXoff;
  private int mAnchorYoff;
  private int mAnchoredGravity;
  @UnsupportedAppUsage
  private int mAnimationStyle = -1;
  private boolean mAttachedInDecor = true;
  private boolean mAttachedInDecorSet = false;
  private Drawable mBackground;
  @UnsupportedAppUsage
  private View mBackgroundView;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private Drawable mBelowAnchorBackgroundDrawable;
  private boolean mClipToScreen;
  private boolean mClippingEnabled = true;
  @UnsupportedAppUsage
  private View mContentView;
  @UnsupportedAppUsage
  private Context mContext;
  @UnsupportedAppUsage
  private PopupDecorView mDecorView;
  private float mElevation;
  private Transition mEnterTransition;
  private Rect mEpicenterBounds;
  private Transition mExitTransition;
  private boolean mFocusable;
  private int mGravity = 0;
  private int mHeight = -2;
  @UnsupportedAppUsage
  private int mHeightMode;
  private boolean mIgnoreCheekPress = false;
  private int mInputMethodMode = 0;
  private boolean mIsAnchorRootAttached;
  @UnsupportedAppUsage
  private boolean mIsDropdown;
  @UnsupportedAppUsage
  private boolean mIsShowing;
  private boolean mIsTransitioningToDismiss;
  @UnsupportedAppUsage
  private int mLastHeight;
  @UnsupportedAppUsage
  private int mLastWidth;
  @UnsupportedAppUsage
  private boolean mLayoutInScreen;
  private boolean mLayoutInsetDecor = false;
  @UnsupportedAppUsage
  private boolean mNotTouchModal;
  private final View.OnAttachStateChangeListener mOnAnchorDetachedListener = new View.OnAttachStateChangeListener()
  {
    public void onViewAttachedToWindow(View paramAnonymousView)
    {
      PopupWindow.this.alignToAnchor();
    }
    
    public void onViewDetachedFromWindow(View paramAnonymousView) {}
  };
  private final View.OnAttachStateChangeListener mOnAnchorRootDetachedListener = new View.OnAttachStateChangeListener()
  {
    public void onViewAttachedToWindow(View paramAnonymousView) {}
    
    public void onViewDetachedFromWindow(View paramAnonymousView)
    {
      PopupWindow.access$102(PopupWindow.this, false);
    }
  };
  @UnsupportedAppUsage
  private OnDismissListener mOnDismissListener;
  private final View.OnLayoutChangeListener mOnLayoutChangeListener = new _..Lambda.PopupWindow.8Gc2stI5cSJZbuKX7X4Qr_vU2nI(this);
  @UnsupportedAppUsage(maxTargetSdk=28)
  private final ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener = new _..Lambda.PopupWindow.nV1HS3Nc6Ck5JRIbIHe3mkyHWzc(this);
  private boolean mOutsideTouchable = false;
  @UnsupportedAppUsage(maxTargetSdk=28)
  private boolean mOverlapAnchor;
  private WeakReference<View> mParentRootView;
  private boolean mPopupViewInitialLayoutDirectionInherited;
  private int mSoftInputMode = 1;
  private int mSplitTouchEnabled = -1;
  private final Rect mTempRect = new Rect();
  private final int[] mTmpAppLocation = new int[2];
  private final int[] mTmpDrawingLocation = new int[2];
  private final int[] mTmpScreenLocation = new int[2];
  @UnsupportedAppUsage
  private View.OnTouchListener mTouchInterceptor;
  private boolean mTouchable = true;
  private int mWidth = -2;
  @UnsupportedAppUsage
  private int mWidthMode;
  @UnsupportedAppUsage
  private int mWindowLayoutType = 1000;
  @UnsupportedAppUsage
  private WindowManager mWindowManager;
  
  static
  {
    Android_Widget_PopupWindow.Extension.get().bindOriginal(new Android_Widget_PopupWindow.Interface()
    {
      public void invokePopup(PopupWindow paramAnonymousPopupWindow, WindowManager.LayoutParams paramAnonymousLayoutParams)
      {
        paramAnonymousPopupWindow.originalInvokePopup(paramAnonymousLayoutParams);
      }
    });
  }
  
  public PopupWindow()
  {
    this(null, 0, 0);
  }
  
  public PopupWindow(int paramInt1, int paramInt2)
  {
    this(null, paramInt1, paramInt2);
  }
  
  public PopupWindow(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public PopupWindow(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842870);
  }
  
  public PopupWindow(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public PopupWindow(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    this.mContext = paramContext;
    this.mWindowManager = ((WindowManager)paramContext.getSystemService("window"));
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.PopupWindow, paramInt1, paramInt2);
    Drawable localDrawable = paramAttributeSet.getDrawable(0);
    this.mElevation = paramAttributeSet.getDimension(3, 0.0F);
    this.mOverlapAnchor = paramAttributeSet.getBoolean(2, false);
    if (paramAttributeSet.hasValueOrEmpty(1))
    {
      paramInt1 = paramAttributeSet.getResourceId(1, 0);
      if (paramInt1 == 16974594) {
        this.mAnimationStyle = -1;
      } else {
        this.mAnimationStyle = paramInt1;
      }
    }
    else
    {
      this.mAnimationStyle = -1;
    }
    Transition localTransition = getTransition(paramAttributeSet.getResourceId(4, 0));
    if (paramAttributeSet.hasValueOrEmpty(5)) {
      paramContext = getTransition(paramAttributeSet.getResourceId(5, 0));
    } else if (localTransition == null) {
      paramContext = null;
    } else {
      paramContext = localTransition.clone();
    }
    paramAttributeSet.recycle();
    setEnterTransition(localTransition);
    setExitTransition(paramContext);
    setBackgroundDrawable(localDrawable);
  }
  
  public PopupWindow(View paramView)
  {
    this(paramView, 0, 0);
  }
  
  public PopupWindow(View paramView, int paramInt1, int paramInt2)
  {
    this(paramView, paramInt1, paramInt2, false);
  }
  
  public PopupWindow(View paramView, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramView != null)
    {
      this.mContext = paramView.getContext();
      this.mWindowManager = ((WindowManager)this.mContext.getSystemService("window"));
    }
    setContentView(paramView);
    setWidth(paramInt1);
    setHeight(paramInt2);
    setFocusable(paramBoolean);
  }
  
  private void alignToAnchor()
  {
    Object localObject = this.mAnchor;
    if (localObject != null) {
      localObject = (View)((WeakReference)localObject).get();
    } else {
      localObject = null;
    }
    if ((localObject != null) && (((View)localObject).isAttachedToWindow()) && (hasDecorView()))
    {
      WindowManager.LayoutParams localLayoutParams = getDecorViewLayoutParams();
      updateAboveAnchor(findDropDownPosition((View)localObject, localLayoutParams, this.mAnchorXoff, this.mAnchorYoff, localLayoutParams.width, localLayoutParams.height, this.mAnchoredGravity, false));
      update(localLayoutParams.x, localLayoutParams.y, -1, -1, true);
    }
  }
  
  @UnsupportedAppUsage
  private int computeAnimationResource()
  {
    int i = this.mAnimationStyle;
    if (i == -1)
    {
      if (this.mIsDropdown)
      {
        if (this.mAboveAnchor) {
          i = 16974582;
        } else {
          i = 16974581;
        }
        return i;
      }
      return 0;
    }
    return i;
  }
  
  private int computeFlags(int paramInt)
  {
    paramInt &= 0xFF797DE7;
    int i = paramInt;
    if (this.mIgnoreCheekPress) {
      i = paramInt | 0x8000;
    }
    if (!this.mFocusable)
    {
      i |= 0x8;
      paramInt = i;
      if (this.mInputMethodMode == 1) {
        paramInt = i | 0x20000;
      }
    }
    else
    {
      paramInt = i;
      if (this.mInputMethodMode == 2) {
        paramInt = i | 0x20000;
      }
    }
    i = paramInt;
    if (!this.mTouchable) {
      i = paramInt | 0x10;
    }
    int j = i;
    if (this.mOutsideTouchable) {
      j = i | 0x40000;
    }
    if (this.mClippingEnabled)
    {
      paramInt = j;
      if (!this.mClipToScreen) {}
    }
    else
    {
      paramInt = j | 0x200;
    }
    i = paramInt;
    if (isSplitTouchEnabled()) {
      i = paramInt | 0x800000;
    }
    paramInt = i;
    if (this.mLayoutInScreen) {
      paramInt = i | 0x100;
    }
    i = paramInt;
    if (this.mLayoutInsetDecor) {
      i = paramInt | 0x10000;
    }
    paramInt = i;
    if (this.mNotTouchModal) {
      paramInt = i | 0x20;
    }
    i = paramInt;
    if (this.mAttachedInDecor) {
      i = paramInt | 0x40000000;
    }
    return i;
  }
  
  private int computeGravity()
  {
    int i = this.mGravity;
    int j = i;
    if (i == 0) {
      j = 8388659;
    }
    i = j;
    if (this.mIsDropdown) {
      if (!this.mClipToScreen)
      {
        i = j;
        if (!this.mClippingEnabled) {}
      }
      else
      {
        i = j | 0x10000000;
      }
    }
    return i;
  }
  
  private PopupBackgroundView createBackgroundView(View paramView)
  {
    Object localObject = this.mContentView.getLayoutParams();
    int i;
    if ((localObject != null) && (((ViewGroup.LayoutParams)localObject).height == -2)) {
      i = -2;
    } else {
      i = -1;
    }
    localObject = new PopupBackgroundView(this.mContext);
    ((PopupBackgroundView)localObject).addView(paramView, new FrameLayout.LayoutParams(-1, i));
    return (PopupBackgroundView)localObject;
  }
  
  private PopupDecorView createDecorView(View paramView)
  {
    Object localObject = this.mContentView.getLayoutParams();
    int i;
    if ((localObject != null) && (((ViewGroup.LayoutParams)localObject).height == -2)) {
      i = -2;
    } else {
      i = -1;
    }
    localObject = new PopupDecorView(this.mContext);
    ((PopupDecorView)localObject).addView(paramView, -1, i);
    ((PopupDecorView)localObject).setClipChildren(false);
    ((PopupDecorView)localObject).setClipToPadding(false);
    return (PopupDecorView)localObject;
  }
  
  private void dismissImmediate(View paramView1, ViewGroup paramViewGroup, View paramView2)
  {
    if (paramView1.getParent() != null) {
      this.mWindowManager.removeViewImmediate(paramView1);
    }
    if (paramViewGroup != null) {
      paramViewGroup.removeView(paramView2);
    }
    this.mDecorView = null;
    this.mBackgroundView = null;
    this.mIsTransitioningToDismiss = false;
  }
  
  private View getAppRootView(View paramView)
  {
    View localView = WindowManagerGlobal.getInstance().getWindowView(paramView.getApplicationWindowToken());
    if (localView != null) {
      return localView;
    }
    return paramView.getRootView();
  }
  
  private Transition getTransition(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 17760256))
    {
      Transition localTransition = TransitionInflater.from(this.mContext).inflateTransition(paramInt);
      if (localTransition != null)
      {
        if (((localTransition instanceof TransitionSet)) && (((TransitionSet)localTransition).getTransitionCount() == 0)) {
          paramInt = 1;
        } else {
          paramInt = 0;
        }
        if (paramInt == 0) {
          return localTransition;
        }
      }
    }
    return null;
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28)
  private void invokePopup(WindowManager.LayoutParams paramLayoutParams)
  {
    if (Android_Widget_PopupWindow.Extension.get().getExtension() != null) {
      ((Android_Widget_PopupWindow.Interface)Android_Widget_PopupWindow.Extension.get().getExtension().asInterface()).invokePopup(this, paramLayoutParams);
    } else {
      originalInvokePopup(paramLayoutParams);
    }
  }
  
  private boolean positionInDisplayHorizontal(WindowManager.LayoutParams paramLayoutParams, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
  {
    boolean bool1 = true;
    paramInt2 = paramInt3 - paramInt2;
    paramLayoutParams.x += paramInt2;
    paramInt3 = paramLayoutParams.x + paramInt1;
    if (paramInt3 > paramInt5) {
      paramLayoutParams.x -= paramInt3 - paramInt5;
    }
    boolean bool2 = bool1;
    if (paramLayoutParams.x < paramInt4)
    {
      paramLayoutParams.x = paramInt4;
      paramInt3 = paramInt5 - paramInt4;
      if ((paramBoolean) && (paramInt1 > paramInt3))
      {
        paramLayoutParams.width = paramInt3;
        bool2 = bool1;
      }
      else
      {
        bool2 = false;
      }
    }
    paramLayoutParams.x -= paramInt2;
    return bool2;
  }
  
  private boolean positionInDisplayVertical(WindowManager.LayoutParams paramLayoutParams, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
  {
    boolean bool1 = true;
    paramInt2 = paramInt3 - paramInt2;
    paramLayoutParams.y += paramInt2;
    paramLayoutParams.height = paramInt1;
    paramInt3 = paramLayoutParams.y + paramInt1;
    if (paramInt3 > paramInt5) {
      paramLayoutParams.y -= paramInt3 - paramInt5;
    }
    boolean bool2 = bool1;
    if (paramLayoutParams.y < paramInt4)
    {
      paramLayoutParams.y = paramInt4;
      paramInt3 = paramInt5 - paramInt4;
      if ((paramBoolean) && (paramInt1 > paramInt3))
      {
        paramLayoutParams.height = paramInt3;
        bool2 = bool1;
      }
      else
      {
        bool2 = false;
      }
    }
    paramLayoutParams.y -= paramInt2;
    return bool2;
  }
  
  @UnsupportedAppUsage
  private void preparePopup(WindowManager.LayoutParams paramLayoutParams)
  {
    if ((this.mContentView != null) && (this.mContext != null) && (this.mWindowManager != null))
    {
      if (paramLayoutParams.accessibilityTitle == null) {
        paramLayoutParams.accessibilityTitle = this.mContext.getString(17040963);
      }
      PopupDecorView localPopupDecorView = this.mDecorView;
      if (localPopupDecorView != null) {
        localPopupDecorView.cancelTransitions();
      }
      if (this.mBackground != null)
      {
        this.mBackgroundView = createBackgroundView(this.mContentView);
        this.mBackgroundView.setBackground(this.mBackground);
      }
      else
      {
        this.mBackgroundView = this.mContentView;
      }
      this.mDecorView = createDecorView(this.mBackgroundView);
      localPopupDecorView = this.mDecorView;
      boolean bool = true;
      localPopupDecorView.setIsRootNamespace(true);
      this.mBackgroundView.setElevation(this.mElevation);
      paramLayoutParams.setSurfaceInsets(this.mBackgroundView, true, true);
      if (this.mContentView.getRawLayoutDirection() != 2) {
        bool = false;
      }
      this.mPopupViewInitialLayoutDirectionInherited = bool;
      return;
    }
    throw new IllegalStateException("You must specify a valid content view by calling setContentView() before attempting to show the popup.");
  }
  
  private void setLayoutDirectionFromAnchor()
  {
    Object localObject = this.mAnchor;
    if (localObject != null)
    {
      localObject = (View)((WeakReference)localObject).get();
      if ((localObject != null) && (this.mPopupViewInitialLayoutDirectionInherited)) {
        this.mDecorView.setLayoutDirection(((View)localObject).getLayoutDirection());
      }
    }
  }
  
  private boolean tryFitHorizontal(WindowManager.LayoutParams paramLayoutParams, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean)
  {
    paramInt1 = paramLayoutParams.x + (paramInt5 - paramInt4);
    if ((paramInt1 >= 0) && (paramInt2 <= paramInt7 - paramInt1)) {
      return true;
    }
    return positionInDisplayHorizontal(paramLayoutParams, paramInt2, paramInt4, paramInt5, paramInt6, paramInt7, paramBoolean);
  }
  
  private boolean tryFitVertical(WindowManager.LayoutParams paramLayoutParams, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean)
  {
    int i = paramLayoutParams.y + (paramInt5 - paramInt4);
    if ((i >= 0) && (paramInt2 <= paramInt7 - i)) {
      return true;
    }
    if (paramInt2 <= i - paramInt3 - paramInt6)
    {
      if (this.mOverlapAnchor) {
        paramInt1 += paramInt3;
      }
      paramLayoutParams.y = (paramInt4 - paramInt2 + paramInt1);
      return true;
    }
    return positionInDisplayVertical(paramLayoutParams, paramInt2, paramInt4, paramInt5, paramInt6, paramInt7, paramBoolean);
  }
  
  private void update(View paramView, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((isShowing()) && (hasContentView()))
    {
      Object localObject = this.mAnchor;
      int i = this.mAnchoredGravity;
      if ((paramBoolean) && ((this.mAnchorXoff != paramInt1) || (this.mAnchorYoff != paramInt2))) {
        j = 1;
      } else {
        j = 0;
      }
      if ((localObject != null) && (((WeakReference)localObject).get() == paramView) && ((j == 0) || (this.mIsDropdown)))
      {
        if (j != 0)
        {
          this.mAnchorXoff = paramInt1;
          this.mAnchorYoff = paramInt2;
        }
      }
      else {
        attachToAnchor(paramView, paramInt1, paramInt2, i);
      }
      localObject = getDecorViewLayoutParams();
      int j = ((WindowManager.LayoutParams)localObject).gravity;
      int k = ((WindowManager.LayoutParams)localObject).width;
      int m = ((WindowManager.LayoutParams)localObject).height;
      int n = ((WindowManager.LayoutParams)localObject).x;
      int i1 = ((WindowManager.LayoutParams)localObject).y;
      if (paramInt3 < 0) {
        paramInt1 = this.mWidth;
      } else {
        paramInt1 = paramInt3;
      }
      if (paramInt4 < 0) {
        paramInt2 = this.mHeight;
      } else {
        paramInt2 = paramInt4;
      }
      updateAboveAnchor(findDropDownPosition(paramView, (WindowManager.LayoutParams)localObject, this.mAnchorXoff, this.mAnchorYoff, paramInt1, paramInt2, i, this.mAllowScrollingAnchorParent));
      if ((j == ((WindowManager.LayoutParams)localObject).gravity) && (n == ((WindowManager.LayoutParams)localObject).x) && (i1 == ((WindowManager.LayoutParams)localObject).y) && (k == ((WindowManager.LayoutParams)localObject).width) && (m == ((WindowManager.LayoutParams)localObject).height)) {
        paramBoolean = false;
      } else {
        paramBoolean = true;
      }
      if (paramInt1 >= 0) {
        paramInt1 = ((WindowManager.LayoutParams)localObject).width;
      }
      if (paramInt2 >= 0) {
        paramInt2 = ((WindowManager.LayoutParams)localObject).height;
      }
      update(((WindowManager.LayoutParams)localObject).x, ((WindowManager.LayoutParams)localObject).y, paramInt1, paramInt2, paramBoolean);
      return;
    }
  }
  
  protected void attachToAnchor(View paramView, int paramInt1, int paramInt2, int paramInt3)
  {
    detachFromAnchor();
    Object localObject = paramView.getViewTreeObserver();
    if (localObject != null) {
      ((ViewTreeObserver)localObject).addOnScrollChangedListener(this.mOnScrollChangedListener);
    }
    paramView.addOnAttachStateChangeListener(this.mOnAnchorDetachedListener);
    localObject = paramView.getRootView();
    ((View)localObject).addOnAttachStateChangeListener(this.mOnAnchorRootDetachedListener);
    ((View)localObject).addOnLayoutChangeListener(this.mOnLayoutChangeListener);
    this.mAnchor = new WeakReference(paramView);
    this.mAnchorRoot = new WeakReference(localObject);
    this.mIsAnchorRootAttached = ((View)localObject).isAttachedToWindow();
    this.mParentRootView = this.mAnchorRoot;
    this.mAnchorXoff = paramInt1;
    this.mAnchorYoff = paramInt2;
    this.mAnchoredGravity = paramInt3;
  }
  
  @UnsupportedAppUsage
  protected final WindowManager.LayoutParams createPopupLayoutParams(IBinder paramIBinder)
  {
    WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
    localLayoutParams.gravity = computeGravity();
    localLayoutParams.flags = computeFlags(localLayoutParams.flags);
    localLayoutParams.type = this.mWindowLayoutType;
    localLayoutParams.token = paramIBinder;
    localLayoutParams.softInputMode = this.mSoftInputMode;
    localLayoutParams.windowAnimations = computeAnimationResource();
    paramIBinder = this.mBackground;
    if (paramIBinder != null) {
      localLayoutParams.format = paramIBinder.getOpacity();
    } else {
      localLayoutParams.format = -3;
    }
    int i = this.mHeightMode;
    if (i < 0)
    {
      this.mLastHeight = i;
      localLayoutParams.height = i;
    }
    else
    {
      i = this.mHeight;
      this.mLastHeight = i;
      localLayoutParams.height = i;
    }
    i = this.mWidthMode;
    if (i < 0)
    {
      this.mLastWidth = i;
      localLayoutParams.width = i;
    }
    else
    {
      i = this.mWidth;
      this.mLastWidth = i;
      localLayoutParams.width = i;
    }
    localLayoutParams.privateFlags = 98304;
    paramIBinder = new StringBuilder();
    paramIBinder.append("PopupWindow:");
    paramIBinder.append(Integer.toHexString(hashCode()));
    localLayoutParams.setTitle(paramIBinder.toString());
    return localLayoutParams;
  }
  
  protected void detachFromAnchor()
  {
    Object localObject = getAnchor();
    if (localObject != null)
    {
      ((View)localObject).getViewTreeObserver().removeOnScrollChangedListener(this.mOnScrollChangedListener);
      ((View)localObject).removeOnAttachStateChangeListener(this.mOnAnchorDetachedListener);
    }
    localObject = this.mAnchorRoot;
    if (localObject != null) {
      localObject = (View)((WeakReference)localObject).get();
    } else {
      localObject = null;
    }
    if (localObject != null)
    {
      ((View)localObject).removeOnAttachStateChangeListener(this.mOnAnchorRootDetachedListener);
      ((View)localObject).removeOnLayoutChangeListener(this.mOnLayoutChangeListener);
    }
    this.mAnchor = null;
    this.mAnchorRoot = null;
    this.mIsAnchorRootAttached = false;
  }
  
  public void dismiss()
  {
    if ((isShowing()) && (!isTransitioningToDismiss()))
    {
      final PopupDecorView localPopupDecorView = this.mDecorView;
      final View localView = this.mContentView;
      final Object localObject1 = localView.getParent();
      if ((localObject1 instanceof ViewGroup)) {
        localObject1 = (ViewGroup)localObject1;
      } else {
        localObject1 = null;
      }
      localPopupDecorView.cancelTransitions();
      this.mIsShowing = false;
      this.mIsTransitioningToDismiss = true;
      Transition localTransition = this.mExitTransition;
      if ((localTransition != null) && (localPopupDecorView.isLaidOut()) && ((this.mIsAnchorRootAttached) || (this.mAnchorRoot == null)))
      {
        Object localObject2 = (WindowManager.LayoutParams)localPopupDecorView.getLayoutParams();
        ((WindowManager.LayoutParams)localObject2).flags |= 0x10;
        ((WindowManager.LayoutParams)localObject2).flags |= 0x8;
        ((WindowManager.LayoutParams)localObject2).flags &= 0xFFFDFFFF;
        this.mWindowManager.updateViewLayout(localPopupDecorView, (ViewGroup.LayoutParams)localObject2);
        localObject2 = this.mAnchorRoot;
        if (localObject2 != null) {
          localObject2 = (View)((WeakReference)localObject2).get();
        } else {
          localObject2 = null;
        }
        localPopupDecorView.startExitTransition(localTransition, (View)localObject2, getTransitionEpicenter(), new TransitionListenerAdapter()
        {
          public void onTransitionEnd(Transition paramAnonymousTransition)
          {
            PopupWindow.this.dismissImmediate(localPopupDecorView, localObject1, localView);
          }
        });
      }
      else
      {
        dismissImmediate(localPopupDecorView, (ViewGroup)localObject1, localView);
      }
      detachFromAnchor();
      localObject1 = this.mOnDismissListener;
      if (localObject1 != null) {
        ((OnDismissListener)localObject1).onDismiss();
      }
      return;
    }
  }
  
  protected boolean findDropDownPosition(View paramView, WindowManager.LayoutParams paramLayoutParams, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
  {
    int i = paramView.getHeight();
    int j = paramView.getWidth();
    if (this.mOverlapAnchor) {
      paramInt2 -= i;
    }
    int[] arrayOfInt1 = this.mTmpAppLocation;
    Object localObject = getAppRootView(paramView);
    ((View)localObject).getLocationOnScreen(arrayOfInt1);
    int[] arrayOfInt2 = this.mTmpScreenLocation;
    paramView.getLocationOnScreen(arrayOfInt2);
    int[] arrayOfInt3 = this.mTmpDrawingLocation;
    boolean bool1 = false;
    arrayOfInt2[0] -= arrayOfInt1[0];
    arrayOfInt2[1] -= arrayOfInt1[1];
    paramLayoutParams.x = (arrayOfInt3[0] + paramInt1);
    paramLayoutParams.y = (arrayOfInt3[1] + i + paramInt2);
    Rect localRect = new Rect();
    ((View)localObject).getWindowVisibleDisplayFrame(localRect);
    if (paramInt3 == -1) {
      paramInt3 = localRect.right - localRect.left;
    }
    if (paramInt4 == -1) {
      paramInt4 = localRect.bottom - localRect.top;
    }
    paramLayoutParams.gravity = computeGravity();
    paramLayoutParams.width = paramInt3;
    paramLayoutParams.height = paramInt4;
    paramInt5 = Gravity.getAbsoluteGravity(paramInt5, paramView.getLayoutDirection()) & 0x7;
    if (paramInt5 == 5) {
      paramLayoutParams.x -= paramInt3 - j;
    }
    boolean bool2 = tryFitVertical(paramLayoutParams, paramInt2, paramInt4, i, arrayOfInt3[1], arrayOfInt2[1], localRect.top, localRect.bottom, false);
    boolean bool3 = tryFitHorizontal(paramLayoutParams, paramInt1, paramInt3, j, arrayOfInt3[0], arrayOfInt2[0], localRect.left, localRect.right, false);
    if ((bool2) && (bool3))
    {
      paramView = paramLayoutParams;
    }
    else
    {
      int k = paramView.getScrollX();
      int m = paramView.getScrollY();
      localObject = new Rect(k, m, k + paramInt3 + paramInt1, m + paramInt4 + i + paramInt2);
      if (paramBoolean) {
        if (paramView.requestRectangleOnScreen((Rect)localObject, true))
        {
          paramView.getLocationOnScreen(arrayOfInt2);
          arrayOfInt2[0] -= arrayOfInt1[0];
          arrayOfInt2[1] -= arrayOfInt1[1];
          k = arrayOfInt3[0];
          paramView = paramLayoutParams;
          paramView.x = (k + paramInt1);
          paramView.y = (arrayOfInt3[1] + i + paramInt2);
          if (paramInt5 == 5) {
            paramView.x -= paramInt3 - j;
          }
        }
        else {}
      }
      m = arrayOfInt3[1];
      paramInt5 = arrayOfInt2[1];
      k = localRect.top;
      int n = localRect.bottom;
      paramBoolean = this.mClipToScreen;
      paramView = paramLayoutParams;
      tryFitVertical(paramLayoutParams, paramInt2, paramInt4, i, m, paramInt5, k, n, paramBoolean);
      tryFitHorizontal(paramLayoutParams, paramInt1, paramInt3, j, arrayOfInt3[0], arrayOfInt2[0], localRect.left, localRect.right, this.mClipToScreen);
    }
    paramBoolean = bool1;
    if (paramView.y < arrayOfInt3[1]) {
      paramBoolean = true;
    }
    return paramBoolean;
  }
  
  protected final boolean getAllowScrollingAnchorParent()
  {
    return this.mAllowScrollingAnchorParent;
  }
  
  protected View getAnchor()
  {
    Object localObject = this.mAnchor;
    if (localObject != null) {
      localObject = (View)((WeakReference)localObject).get();
    } else {
      localObject = null;
    }
    return (View)localObject;
  }
  
  public int getAnimationStyle()
  {
    return this.mAnimationStyle;
  }
  
  public Drawable getBackground()
  {
    return this.mBackground;
  }
  
  public View getContentView()
  {
    return this.mContentView;
  }
  
  protected WindowManager.LayoutParams getDecorViewLayoutParams()
  {
    return (WindowManager.LayoutParams)this.mDecorView.getLayoutParams();
  }
  
  public float getElevation()
  {
    return this.mElevation;
  }
  
  public Transition getEnterTransition()
  {
    return this.mEnterTransition;
  }
  
  public Rect getEpicenterBounds()
  {
    Rect localRect = this.mEpicenterBounds;
    if (localRect != null) {
      localRect = new Rect(localRect);
    } else {
      localRect = null;
    }
    return localRect;
  }
  
  public Transition getExitTransition()
  {
    return this.mExitTransition;
  }
  
  public int getHeight()
  {
    return this.mHeight;
  }
  
  public int getInputMethodMode()
  {
    return this.mInputMethodMode;
  }
  
  public int getMaxAvailableHeight(View paramView)
  {
    return getMaxAvailableHeight(paramView, 0);
  }
  
  public int getMaxAvailableHeight(View paramView, int paramInt)
  {
    return getMaxAvailableHeight(paramView, paramInt, false);
  }
  
  public int getMaxAvailableHeight(View paramView, int paramInt, boolean paramBoolean)
  {
    Object localObject1 = new Rect();
    getAppRootView(paramView).getWindowVisibleDisplayFrame((Rect)localObject1);
    if (paramBoolean)
    {
      localObject2 = new Rect();
      paramView.getWindowDisplayFrame((Rect)localObject2);
      ((Rect)localObject2).top = ((Rect)localObject1).top;
      ((Rect)localObject2).right = ((Rect)localObject1).right;
      ((Rect)localObject2).left = ((Rect)localObject1).left;
      localObject1 = localObject2;
    }
    Object localObject2 = this.mTmpDrawingLocation;
    paramView.getLocationOnScreen((int[])localObject2);
    int i = ((Rect)localObject1).bottom;
    if (this.mOverlapAnchor) {
      i = i - localObject2[1] - paramInt;
    } else {
      i = i - (localObject2[1] + paramView.getHeight()) - paramInt;
    }
    i = Math.max(i, localObject2[1] - ((Rect)localObject1).top + paramInt);
    paramView = this.mBackground;
    paramInt = i;
    if (paramView != null)
    {
      paramView.getPadding(this.mTempRect);
      paramInt = i - (this.mTempRect.top + this.mTempRect.bottom);
    }
    return paramInt;
  }
  
  protected final OnDismissListener getOnDismissListener()
  {
    return this.mOnDismissListener;
  }
  
  public boolean getOverlapAnchor()
  {
    return this.mOverlapAnchor;
  }
  
  public int getSoftInputMode()
  {
    return this.mSoftInputMode;
  }
  
  protected final Rect getTransitionEpicenter()
  {
    Object localObject1 = this.mAnchor;
    if (localObject1 != null) {
      localObject1 = (View)((WeakReference)localObject1).get();
    } else {
      localObject1 = null;
    }
    Object localObject2 = this.mDecorView;
    if ((localObject1 != null) && (localObject2 != null))
    {
      int[] arrayOfInt = ((View)localObject1).getLocationOnScreen();
      localObject2 = this.mDecorView.getLocationOnScreen();
      localObject1 = new Rect(0, 0, ((View)localObject1).getWidth(), ((View)localObject1).getHeight());
      ((Rect)localObject1).offset(arrayOfInt[0] - localObject2[0], arrayOfInt[1] - localObject2[1]);
      if (this.mEpicenterBounds != null)
      {
        int i = ((Rect)localObject1).left;
        int j = ((Rect)localObject1).top;
        ((Rect)localObject1).set(this.mEpicenterBounds);
        ((Rect)localObject1).offset(i, j);
      }
      return (Rect)localObject1;
    }
    return null;
  }
  
  public int getWidth()
  {
    return this.mWidth;
  }
  
  public int getWindowLayoutType()
  {
    return this.mWindowLayoutType;
  }
  
  protected boolean hasContentView()
  {
    boolean bool;
    if (this.mContentView != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  protected boolean hasDecorView()
  {
    boolean bool;
    if (this.mDecorView != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isAboveAnchor()
  {
    return this.mAboveAnchor;
  }
  
  public boolean isAttachedInDecor()
  {
    return this.mAttachedInDecor;
  }
  
  @Deprecated
  public boolean isClipToScreenEnabled()
  {
    return this.mClipToScreen;
  }
  
  public boolean isClippedToScreen()
  {
    return this.mClipToScreen;
  }
  
  public boolean isClippingEnabled()
  {
    return this.mClippingEnabled;
  }
  
  public boolean isFocusable()
  {
    return this.mFocusable;
  }
  
  public boolean isLaidOutInScreen()
  {
    return this.mLayoutInScreen;
  }
  
  @Deprecated
  public boolean isLayoutInScreenEnabled()
  {
    return this.mLayoutInScreen;
  }
  
  protected final boolean isLayoutInsetDecor()
  {
    return this.mLayoutInsetDecor;
  }
  
  public boolean isOutsideTouchable()
  {
    return this.mOutsideTouchable;
  }
  
  public boolean isShowing()
  {
    return this.mIsShowing;
  }
  
  public boolean isSplitTouchEnabled()
  {
    int i = this.mSplitTouchEnabled;
    boolean bool1 = false;
    boolean bool2 = false;
    if (i < 0)
    {
      Context localContext = this.mContext;
      if (localContext != null)
      {
        if (localContext.getApplicationInfo().targetSdkVersion >= 11) {
          bool2 = true;
        }
        return bool2;
      }
    }
    bool2 = bool1;
    if (this.mSplitTouchEnabled == 1) {
      bool2 = true;
    }
    return bool2;
  }
  
  public boolean isTouchModal()
  {
    return this.mNotTouchModal ^ true;
  }
  
  public boolean isTouchable()
  {
    return this.mTouchable;
  }
  
  protected final boolean isTransitioningToDismiss()
  {
    return this.mIsTransitioningToDismiss;
  }
  
  void originalInvokePopup(WindowManager.LayoutParams paramLayoutParams)
  {
    Object localObject = this.mContext;
    if (localObject != null) {
      paramLayoutParams.packageName = ((Context)localObject).getPackageName();
    }
    localObject = this.mDecorView;
    ((PopupDecorView)localObject).setFitsSystemWindows(this.mLayoutInsetDecor);
    setLayoutDirectionFromAnchor();
    this.mWindowManager.addView((View)localObject, paramLayoutParams);
    paramLayoutParams = this.mEnterTransition;
    if (paramLayoutParams != null) {
      ((PopupDecorView)localObject).requestEnterTransition(paramLayoutParams);
    }
  }
  
  @UnsupportedAppUsage
  void setAllowScrollingAnchorParent(boolean paramBoolean)
  {
    this.mAllowScrollingAnchorParent = paramBoolean;
  }
  
  public void setAnimationStyle(int paramInt)
  {
    this.mAnimationStyle = paramInt;
  }
  
  public void setAttachedInDecor(boolean paramBoolean)
  {
    this.mAttachedInDecor = paramBoolean;
    this.mAttachedInDecorSet = true;
  }
  
  public void setBackgroundDrawable(Drawable paramDrawable)
  {
    this.mBackground = paramDrawable;
    paramDrawable = this.mBackground;
    if ((paramDrawable instanceof StateListDrawable))
    {
      paramDrawable = (StateListDrawable)paramDrawable;
      int i = paramDrawable.findStateDrawableIndex(ABOVE_ANCHOR_STATE_SET);
      int j = paramDrawable.getStateCount();
      int k = -1;
      int n;
      for (int m = 0;; m++)
      {
        n = k;
        if (m >= j) {
          break;
        }
        if (m != i)
        {
          n = m;
          break;
        }
      }
      if ((i != -1) && (n != -1))
      {
        this.mAboveAnchorBackgroundDrawable = paramDrawable.getStateDrawable(i);
        this.mBelowAnchorBackgroundDrawable = paramDrawable.getStateDrawable(n);
      }
      else
      {
        this.mBelowAnchorBackgroundDrawable = null;
        this.mAboveAnchorBackgroundDrawable = null;
      }
    }
  }
  
  @Deprecated
  public void setClipToScreenEnabled(boolean paramBoolean)
  {
    this.mClipToScreen = paramBoolean;
  }
  
  public void setClippingEnabled(boolean paramBoolean)
  {
    this.mClippingEnabled = paramBoolean;
  }
  
  public void setContentView(View paramView)
  {
    if (isShowing()) {
      return;
    }
    this.mContentView = paramView;
    if (this.mContext == null)
    {
      paramView = this.mContentView;
      if (paramView != null) {
        this.mContext = paramView.getContext();
      }
    }
    if ((this.mWindowManager == null) && (this.mContentView != null)) {
      this.mWindowManager = ((WindowManager)this.mContext.getSystemService("window"));
    }
    paramView = this.mContext;
    if ((paramView != null) && (!this.mAttachedInDecorSet))
    {
      boolean bool;
      if (paramView.getApplicationInfo().targetSdkVersion >= 22) {
        bool = true;
      } else {
        bool = false;
      }
      setAttachedInDecor(bool);
    }
  }
  
  protected final void setDropDown(boolean paramBoolean)
  {
    this.mIsDropdown = paramBoolean;
  }
  
  public void setElevation(float paramFloat)
  {
    this.mElevation = paramFloat;
  }
  
  public void setEnterTransition(Transition paramTransition)
  {
    this.mEnterTransition = paramTransition;
  }
  
  public void setEpicenterBounds(Rect paramRect)
  {
    if (paramRect != null) {
      paramRect = new Rect(paramRect);
    } else {
      paramRect = null;
    }
    this.mEpicenterBounds = paramRect;
  }
  
  public void setExitTransition(Transition paramTransition)
  {
    this.mExitTransition = paramTransition;
  }
  
  public void setFocusable(boolean paramBoolean)
  {
    this.mFocusable = paramBoolean;
  }
  
  public void setHeight(int paramInt)
  {
    this.mHeight = paramInt;
  }
  
  public void setIgnoreCheekPress()
  {
    this.mIgnoreCheekPress = true;
  }
  
  public void setInputMethodMode(int paramInt)
  {
    this.mInputMethodMode = paramInt;
  }
  
  public void setIsClippedToScreen(boolean paramBoolean)
  {
    this.mClipToScreen = paramBoolean;
  }
  
  public void setIsLaidOutInScreen(boolean paramBoolean)
  {
    this.mLayoutInScreen = paramBoolean;
  }
  
  @Deprecated
  public void setLayoutInScreenEnabled(boolean paramBoolean)
  {
    this.mLayoutInScreen = paramBoolean;
  }
  
  @UnsupportedAppUsage
  public void setLayoutInsetDecor(boolean paramBoolean)
  {
    this.mLayoutInsetDecor = paramBoolean;
  }
  
  public void setOnDismissListener(OnDismissListener paramOnDismissListener)
  {
    this.mOnDismissListener = paramOnDismissListener;
  }
  
  public void setOutsideTouchable(boolean paramBoolean)
  {
    this.mOutsideTouchable = paramBoolean;
  }
  
  public void setOverlapAnchor(boolean paramBoolean)
  {
    this.mOverlapAnchor = paramBoolean;
  }
  
  protected final void setShowing(boolean paramBoolean)
  {
    this.mIsShowing = paramBoolean;
  }
  
  public void setSoftInputMode(int paramInt)
  {
    this.mSoftInputMode = paramInt;
  }
  
  public void setSplitTouchEnabled(boolean paramBoolean)
  {
    this.mSplitTouchEnabled = paramBoolean;
  }
  
  public void setTouchInterceptor(View.OnTouchListener paramOnTouchListener)
  {
    this.mTouchInterceptor = paramOnTouchListener;
  }
  
  public void setTouchModal(boolean paramBoolean)
  {
    this.mNotTouchModal = (paramBoolean ^ true);
  }
  
  public void setTouchable(boolean paramBoolean)
  {
    this.mTouchable = paramBoolean;
  }
  
  protected final void setTransitioningToDismiss(boolean paramBoolean)
  {
    this.mIsTransitioningToDismiss = paramBoolean;
  }
  
  public void setWidth(int paramInt)
  {
    this.mWidth = paramInt;
  }
  
  @Deprecated
  public void setWindowLayoutMode(int paramInt1, int paramInt2)
  {
    this.mWidthMode = paramInt1;
    this.mHeightMode = paramInt2;
  }
  
  public void setWindowLayoutType(int paramInt)
  {
    this.mWindowLayoutType = paramInt;
  }
  
  public void showAsDropDown(View paramView)
  {
    showAsDropDown(paramView, 0, 0);
  }
  
  public void showAsDropDown(View paramView, int paramInt1, int paramInt2)
  {
    showAsDropDown(paramView, paramInt1, paramInt2, 8388659);
  }
  
  public void showAsDropDown(View paramView, int paramInt1, int paramInt2, int paramInt3)
  {
    if ((!isShowing()) && (hasContentView()))
    {
      TransitionManager.endTransitions(this.mDecorView);
      attachToAnchor(paramView, paramInt1, paramInt2, paramInt3);
      this.mIsShowing = true;
      this.mIsDropdown = true;
      WindowManager.LayoutParams localLayoutParams = createPopupLayoutParams(paramView.getApplicationWindowToken());
      preparePopup(localLayoutParams);
      updateAboveAnchor(findDropDownPosition(paramView, localLayoutParams, paramInt1, paramInt2, localLayoutParams.width, localLayoutParams.height, paramInt3, this.mAllowScrollingAnchorParent));
      localLayoutParams.accessibilityIdOfAnchor = paramView.getAccessibilityViewId();
      invokePopup(localLayoutParams);
      return;
    }
  }
  
  @UnsupportedAppUsage
  public void showAtLocation(IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3)
  {
    if ((!isShowing()) && (this.mContentView != null))
    {
      TransitionManager.endTransitions(this.mDecorView);
      detachFromAnchor();
      this.mIsShowing = true;
      this.mIsDropdown = false;
      this.mGravity = paramInt1;
      paramIBinder = createPopupLayoutParams(paramIBinder);
      preparePopup(paramIBinder);
      paramIBinder.x = paramInt2;
      paramIBinder.y = paramInt3;
      invokePopup(paramIBinder);
      return;
    }
  }
  
  public void showAtLocation(View paramView, int paramInt1, int paramInt2, int paramInt3)
  {
    this.mParentRootView = new WeakReference(paramView.getRootView());
    showAtLocation(paramView.getWindowToken(), paramInt1, paramInt2, paramInt3);
  }
  
  public void update()
  {
    if ((isShowing()) && (hasContentView()))
    {
      WindowManager.LayoutParams localLayoutParams = getDecorViewLayoutParams();
      int i = 0;
      int j = computeAnimationResource();
      if (j != localLayoutParams.windowAnimations)
      {
        localLayoutParams.windowAnimations = j;
        i = 1;
      }
      j = computeFlags(localLayoutParams.flags);
      if (j != localLayoutParams.flags)
      {
        localLayoutParams.flags = j;
        i = 1;
      }
      j = computeGravity();
      if (j != localLayoutParams.gravity)
      {
        localLayoutParams.gravity = j;
        i = 1;
      }
      if (i != 0)
      {
        Object localObject = this.mAnchor;
        if (localObject != null) {
          localObject = (View)((WeakReference)localObject).get();
        } else {
          localObject = null;
        }
        update((View)localObject, localLayoutParams);
      }
      return;
    }
  }
  
  public void update(int paramInt1, int paramInt2)
  {
    WindowManager.LayoutParams localLayoutParams = getDecorViewLayoutParams();
    update(localLayoutParams.x, localLayoutParams.y, paramInt1, paramInt2, false);
  }
  
  public void update(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    update(paramInt1, paramInt2, paramInt3, paramInt4, false);
  }
  
  public void update(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    if (paramInt3 >= 0)
    {
      this.mLastWidth = paramInt3;
      setWidth(paramInt3);
    }
    if (paramInt4 >= 0)
    {
      this.mLastHeight = paramInt4;
      setHeight(paramInt4);
    }
    if ((isShowing()) && (hasContentView()))
    {
      WindowManager.LayoutParams localLayoutParams = getDecorViewLayoutParams();
      boolean bool = paramBoolean;
      int i = this.mWidthMode;
      if (i >= 0) {
        i = this.mLastWidth;
      }
      paramBoolean = bool;
      if (paramInt3 != -1)
      {
        paramBoolean = bool;
        if (localLayoutParams.width != i)
        {
          this.mLastWidth = i;
          localLayoutParams.width = i;
          paramBoolean = true;
        }
      }
      paramInt3 = this.mHeightMode;
      if (paramInt3 >= 0) {
        paramInt3 = this.mLastHeight;
      }
      bool = paramBoolean;
      if (paramInt4 != -1)
      {
        bool = paramBoolean;
        if (localLayoutParams.height != paramInt3)
        {
          this.mLastHeight = paramInt3;
          localLayoutParams.height = paramInt3;
          bool = true;
        }
      }
      if (localLayoutParams.x != paramInt1)
      {
        localLayoutParams.x = paramInt1;
        bool = true;
      }
      paramBoolean = bool;
      if (localLayoutParams.y != paramInt2)
      {
        localLayoutParams.y = paramInt2;
        paramBoolean = true;
      }
      paramInt1 = computeAnimationResource();
      if (paramInt1 != localLayoutParams.windowAnimations)
      {
        localLayoutParams.windowAnimations = paramInt1;
        paramBoolean = true;
      }
      paramInt1 = computeFlags(localLayoutParams.flags);
      if (paramInt1 != localLayoutParams.flags)
      {
        localLayoutParams.flags = paramInt1;
        paramBoolean = true;
      }
      paramInt1 = computeGravity();
      if (paramInt1 != localLayoutParams.gravity)
      {
        localLayoutParams.gravity = paramInt1;
        paramBoolean = true;
      }
      Object localObject1 = null;
      paramInt2 = -1;
      WeakReference localWeakReference = this.mAnchor;
      Object localObject2 = localObject1;
      paramInt1 = paramInt2;
      if (localWeakReference != null)
      {
        localObject2 = localObject1;
        paramInt1 = paramInt2;
        if (localWeakReference.get() != null)
        {
          localObject2 = (View)this.mAnchor.get();
          paramInt1 = ((View)localObject2).getAccessibilityViewId();
        }
      }
      if (paramInt1 != localLayoutParams.accessibilityIdOfAnchor)
      {
        localLayoutParams.accessibilityIdOfAnchor = paramInt1;
        paramBoolean = true;
      }
      if (paramBoolean) {
        update((View)localObject2, localLayoutParams);
      }
      return;
    }
  }
  
  public void update(View paramView, int paramInt1, int paramInt2)
  {
    update(paramView, false, 0, 0, paramInt1, paramInt2);
  }
  
  public void update(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    update(paramView, true, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  protected void update(View paramView, WindowManager.LayoutParams paramLayoutParams)
  {
    setLayoutDirectionFromAnchor();
    this.mWindowManager.updateViewLayout(this.mDecorView, paramLayoutParams);
  }
  
  @UnsupportedAppUsage
  protected final void updateAboveAnchor(boolean paramBoolean)
  {
    if (paramBoolean != this.mAboveAnchor)
    {
      this.mAboveAnchor = paramBoolean;
      if (this.mBackground != null)
      {
        View localView = this.mBackgroundView;
        if (localView != null)
        {
          Drawable localDrawable = this.mAboveAnchorBackgroundDrawable;
          if (localDrawable != null)
          {
            if (this.mAboveAnchor) {
              localView.setBackground(localDrawable);
            } else {
              localView.setBackground(this.mBelowAnchorBackgroundDrawable);
            }
          }
          else {
            localView.refreshDrawableState();
          }
        }
      }
    }
  }
  
  public static abstract interface OnDismissListener
  {
    public abstract void onDismiss();
  }
  
  private class PopupBackgroundView
    extends FrameLayout
  {
    public PopupBackgroundView(Context paramContext)
    {
      super();
    }
    
    protected int[] onCreateDrawableState(int paramInt)
    {
      if (PopupWindow.this.mAboveAnchor)
      {
        int[] arrayOfInt = super.onCreateDrawableState(paramInt + 1);
        View.mergeDrawableStates(arrayOfInt, PopupWindow.ABOVE_ANCHOR_STATE_SET);
        return arrayOfInt;
      }
      return super.onCreateDrawableState(paramInt);
    }
  }
  
  private class PopupDecorView
    extends FrameLayout
  {
    private Runnable mCleanupAfterExit;
    private final View.OnAttachStateChangeListener mOnAnchorRootDetachedListener = new View.OnAttachStateChangeListener()
    {
      public void onViewAttachedToWindow(View paramAnonymousView) {}
      
      public void onViewDetachedFromWindow(View paramAnonymousView)
      {
        paramAnonymousView.removeOnAttachStateChangeListener(this);
        if (PopupWindow.PopupDecorView.this.isAttachedToWindow()) {
          TransitionManager.endTransitions(PopupWindow.PopupDecorView.this);
        }
      }
    };
    
    public PopupDecorView(Context paramContext)
    {
      super();
    }
    
    private void startEnterTransition(Transition paramTransition)
    {
      int i = getChildCount();
      for (int j = 0; j < i; j++)
      {
        View localView = getChildAt(j);
        paramTransition.addTarget(localView);
        localView.setTransitionVisibility(4);
      }
      TransitionManager.beginDelayedTransition(this, paramTransition);
      for (j = 0; j < i; j++) {
        getChildAt(j).setTransitionVisibility(0);
      }
    }
    
    public void cancelTransitions()
    {
      TransitionManager.endTransitions(this);
      Runnable localRunnable = this.mCleanupAfterExit;
      if (localRunnable != null) {
        localRunnable.run();
      }
    }
    
    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
    {
      if (paramKeyEvent.getKeyCode() == 4)
      {
        if (getKeyDispatcherState() == null) {
          return super.dispatchKeyEvent(paramKeyEvent);
        }
        KeyEvent.DispatcherState localDispatcherState;
        if ((paramKeyEvent.getAction() == 0) && (paramKeyEvent.getRepeatCount() == 0))
        {
          localDispatcherState = getKeyDispatcherState();
          if (localDispatcherState != null) {
            localDispatcherState.startTracking(paramKeyEvent, this);
          }
          return true;
        }
        if (paramKeyEvent.getAction() == 1)
        {
          localDispatcherState = getKeyDispatcherState();
          if ((localDispatcherState != null) && (localDispatcherState.isTracking(paramKeyEvent)) && (!paramKeyEvent.isCanceled()))
          {
            PopupWindow.this.dismiss();
            return true;
          }
        }
        return super.dispatchKeyEvent(paramKeyEvent);
      }
      return super.dispatchKeyEvent(paramKeyEvent);
    }
    
    public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
    {
      if ((PopupWindow.this.mTouchInterceptor != null) && (PopupWindow.this.mTouchInterceptor.onTouch(this, paramMotionEvent))) {
        return true;
      }
      return super.dispatchTouchEvent(paramMotionEvent);
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      int i = (int)paramMotionEvent.getX();
      int j = (int)paramMotionEvent.getY();
      if ((paramMotionEvent.getAction() == 0) && ((i < 0) || (i >= getWidth()) || (j < 0) || (j >= getHeight())))
      {
        PopupWindow.this.dismiss();
        return true;
      }
      if (paramMotionEvent.getAction() == 4)
      {
        PopupWindow.this.dismiss();
        return true;
      }
      return super.onTouchEvent(paramMotionEvent);
    }
    
    public void requestEnterTransition(Transition paramTransition)
    {
      ViewTreeObserver localViewTreeObserver = getViewTreeObserver();
      if ((localViewTreeObserver != null) && (paramTransition != null)) {
        localViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
          public void onGlobalLayout()
          {
            final Object localObject = PopupWindow.PopupDecorView.this.getViewTreeObserver();
            if (localObject != null) {
              ((ViewTreeObserver)localObject).removeOnGlobalLayoutListener(this);
            }
            localObject = PopupWindow.this.getTransitionEpicenter();
            this.val$enterTransition.setEpicenterCallback(new Transition.EpicenterCallback()
            {
              public Rect onGetEpicenter(Transition paramAnonymous2Transition)
              {
                return localObject;
              }
            });
            PopupWindow.PopupDecorView.this.startEnterTransition(this.val$enterTransition);
          }
        });
      }
    }
    
    public void requestKeyboardShortcuts(List<KeyboardShortcutGroup> paramList, int paramInt)
    {
      if (PopupWindow.this.mParentRootView != null)
      {
        View localView = (View)PopupWindow.this.mParentRootView.get();
        if (localView != null) {
          localView.requestKeyboardShortcuts(paramList, paramInt);
        }
      }
    }
    
    public void startExitTransition(Transition paramTransition, View paramView, final Rect paramRect, Transition.TransitionListener paramTransitionListener)
    {
      if (paramTransition == null) {
        return;
      }
      if (paramView != null) {
        paramView.addOnAttachStateChangeListener(this.mOnAnchorRootDetachedListener);
      }
      this.mCleanupAfterExit = new _..Lambda.PopupWindow.PopupDecorView.T99WKEnQefOCXbbKvW95WY38p_I(this, paramTransitionListener, paramTransition, paramView);
      paramTransition = paramTransition.clone();
      paramTransition.addListener(new TransitionListenerAdapter()
      {
        public void onTransitionEnd(Transition paramAnonymousTransition)
        {
          paramAnonymousTransition.removeListener(this);
          if (PopupWindow.PopupDecorView.this.mCleanupAfterExit != null) {
            PopupWindow.PopupDecorView.this.mCleanupAfterExit.run();
          }
        }
      });
      paramTransition.setEpicenterCallback(new Transition.EpicenterCallback()
      {
        public Rect onGetEpicenter(Transition paramAnonymousTransition)
        {
          return paramRect;
        }
      });
      int i = getChildCount();
      for (int j = 0; j < i; j++) {
        paramTransition.addTarget(getChildAt(j));
      }
      TransitionManager.beginDelayedTransition(this, paramTransition);
      for (j = 0; j < i; j++) {
        getChildAt(j).setVisibility(4);
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/PopupWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */