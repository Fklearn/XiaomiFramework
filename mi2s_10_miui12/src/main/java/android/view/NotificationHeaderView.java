package android.view;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.RemoteViews.RemoteView;
import com.android.internal.widget.CachingIconView;
import java.util.ArrayList;

@RemoteViews.RemoteView
public class NotificationHeaderView
  extends ViewGroup
{
  public static final int NO_COLOR = 1;
  private boolean mAcceptAllTouches;
  private View mAppName;
  private View mAppOps;
  private View.OnClickListener mAppOpsListener;
  private View mAudiblyAlertedIcon;
  private Drawable mBackground;
  private View mCameraIcon;
  private final int mChildMinWidth;
  private final int mContentEndMargin;
  private boolean mEntireHeaderClickable;
  private ImageView mExpandButton;
  private View.OnClickListener mExpandClickListener;
  private boolean mExpandOnlyOnButton;
  private boolean mExpanded;
  private final int mGravity;
  private View mHeaderText;
  private int mHeaderTextMarginEnd;
  private CachingIconView mIcon;
  private int mIconColor;
  private View mMicIcon;
  private int mOriginalNotificationColor;
  private View mOverlayIcon;
  private View mProfileBadge;
  ViewOutlineProvider mProvider = new ViewOutlineProvider()
  {
    public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
    {
      if (NotificationHeaderView.this.mBackground != null)
      {
        paramAnonymousOutline.setRect(0, 0, NotificationHeaderView.this.getWidth(), NotificationHeaderView.this.getHeight());
        paramAnonymousOutline.setAlpha(1.0F);
      }
    }
  };
  private View mSecondaryHeaderText;
  private boolean mShowExpandButtonAtEnd;
  private boolean mShowWorkBadgeAtEnd;
  private int mTotalWidth;
  private HeaderTouchListener mTouchListener = new HeaderTouchListener();
  
  public NotificationHeaderView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  @UnsupportedAppUsage
  public NotificationHeaderView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public NotificationHeaderView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public NotificationHeaderView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    Resources localResources = getResources();
    this.mChildMinWidth = localResources.getDimensionPixelSize(17105353);
    this.mContentEndMargin = localResources.getDimensionPixelSize(17105330);
    this.mEntireHeaderClickable = localResources.getBoolean(17891492);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, new int[] { 16842927 }, paramInt1, paramInt2);
    this.mGravity = paramContext.getInt(0, 0);
    paramContext.recycle();
  }
  
  private View getFirstChildNotGone()
  {
    for (int i = 0; i < getChildCount(); i++)
    {
      View localView = getChildAt(i);
      if (localView.getVisibility() != 8) {
        return localView;
      }
    }
    return this;
  }
  
  private int shrinkViewForOverflow(int paramInt1, int paramInt2, View paramView, int paramInt3)
  {
    int i = paramView.getMeasuredWidth();
    int j = paramInt2;
    if (paramInt2 > 0)
    {
      j = paramInt2;
      if (paramView.getVisibility() != 8)
      {
        j = paramInt2;
        if (i > paramInt3)
        {
          paramInt3 = Math.max(paramInt3, i - paramInt2);
          paramView.measure(View.MeasureSpec.makeMeasureSpec(paramInt3, Integer.MIN_VALUE), paramInt1);
          j = paramInt2 - (i - paramInt3);
        }
      }
    }
    return j;
  }
  
  private void updateExpandButton()
  {
    int i;
    int j;
    if (this.mExpanded)
    {
      i = 17302356;
      j = 17039966;
    }
    else
    {
      i = 17302416;
      j = 17039965;
    }
    this.mExpandButton.setImageDrawable(getContext().getDrawable(i));
    this.mExpandButton.setColorFilter(this.mOriginalNotificationColor);
    this.mExpandButton.setContentDescription(this.mContext.getText(j));
  }
  
  private void updateTouchListener()
  {
    if ((this.mExpandClickListener == null) && (this.mAppOpsListener == null))
    {
      setOnTouchListener(null);
      return;
    }
    setOnTouchListener(this.mTouchListener);
    this.mTouchListener.bindTouchRects();
  }
  
  protected void drawableStateChanged()
  {
    Drawable localDrawable = this.mBackground;
    if ((localDrawable != null) && (localDrawable.isStateful())) {
      this.mBackground.setState(getDrawableState());
    }
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new ViewGroup.MarginLayoutParams(getContext(), paramAttributeSet);
  }
  
  public ImageView getExpandButton()
  {
    return this.mExpandButton;
  }
  
  public int getHeaderTextMarginEnd()
  {
    return this.mHeaderTextMarginEnd;
  }
  
  public CachingIconView getIcon()
  {
    return this.mIcon;
  }
  
  public int getOriginalIconColor()
  {
    return this.mIconColor;
  }
  
  public int getOriginalNotificationColor()
  {
    return this.mOriginalNotificationColor;
  }
  
  public View getWorkProfileIcon()
  {
    return this.mProfileBadge;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public boolean isInTouchRect(float paramFloat1, float paramFloat2)
  {
    if (this.mExpandClickListener == null) {
      return false;
    }
    return this.mTouchListener.isInside(paramFloat1, paramFloat2);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    Drawable localDrawable = this.mBackground;
    if (localDrawable != null)
    {
      localDrawable.setBounds(0, 0, getWidth(), getHeight());
      this.mBackground.draw(paramCanvas);
    }
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mAppName = findViewById(16908733);
    this.mHeaderText = findViewById(16908988);
    this.mSecondaryHeaderText = findViewById(16908990);
    this.mExpandButton = ((ImageView)findViewById(16908909));
    this.mIcon = ((CachingIconView)findViewById(16908294));
    this.mProfileBadge = findViewById(16909292);
    this.mCameraIcon = findViewById(16908801);
    this.mMicIcon = findViewById(16909130);
    this.mOverlayIcon = findViewById(16909237);
    this.mAppOps = findViewById(16908734);
    this.mAudiblyAlertedIcon = findViewById(16908714);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt4 = getPaddingStart();
    paramInt3 = getMeasuredWidth();
    if ((this.mGravity & 0x1) != 0) {
      paramInt2 = 1;
    } else {
      paramInt2 = 0;
    }
    paramInt1 = paramInt4;
    if (paramInt2 != 0) {
      paramInt1 = paramInt4 + (getMeasuredWidth() / 2 - this.mTotalWidth / 2);
    }
    int i = getChildCount();
    int j = getMeasuredHeight();
    int k = getPaddingTop();
    int m = getPaddingBottom();
    int n = 0;
    paramInt2 = paramInt3;
    while (n < i)
    {
      View localView = getChildAt(n);
      if (localView.getVisibility() != 8)
      {
        int i1 = localView.getMeasuredHeight();
        ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)localView.getLayoutParams();
        int i2 = (int)(getPaddingTop() + (j - k - m - i1) / 2.0F);
        if (((localView != this.mExpandButton) || (!this.mShowExpandButtonAtEnd)) && (localView != this.mProfileBadge) && (localView != this.mAppOps))
        {
          paramInt3 = paramInt1 + localMarginLayoutParams.getMarginStart();
          paramInt1 = localView.getMeasuredWidth() + paramInt3;
          paramInt4 = paramInt1;
          paramInt1 += localMarginLayoutParams.getMarginEnd();
        }
        else
        {
          if (paramInt2 == getMeasuredWidth()) {
            paramInt4 = paramInt2 - this.mContentEndMargin;
          } else {
            paramInt4 = paramInt2 - localMarginLayoutParams.getMarginEnd();
          }
          paramInt3 = paramInt4 - localView.getMeasuredWidth();
          paramInt2 = paramInt3 - localMarginLayoutParams.getMarginStart();
        }
        int i3 = paramInt3;
        int i4 = paramInt4;
        if (getLayoutDirection() == 1)
        {
          i3 = getWidth() - paramInt4;
          i4 = getWidth() - paramInt3;
        }
        localView.layout(i3, i2, i4, i2 + i1);
      }
      n++;
    }
    updateTouchListener();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getSize(paramInt1);
    int j = View.MeasureSpec.getSize(paramInt2);
    int k = View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE);
    int m = View.MeasureSpec.makeMeasureSpec(j, Integer.MIN_VALUE);
    int n = getPaddingStart();
    paramInt2 = getPaddingEnd();
    for (paramInt1 = 0; paramInt1 < getChildCount(); paramInt1++)
    {
      View localView = getChildAt(paramInt1);
      if (localView.getVisibility() != 8)
      {
        ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)localView.getLayoutParams();
        localView.measure(getChildMeasureSpec(k, localMarginLayoutParams.leftMargin + localMarginLayoutParams.rightMargin, localMarginLayoutParams.width), getChildMeasureSpec(m, localMarginLayoutParams.topMargin + localMarginLayoutParams.bottomMargin, localMarginLayoutParams.height));
        if (((localView != this.mExpandButton) || (!this.mShowExpandButtonAtEnd)) && (localView != this.mProfileBadge) && (localView != this.mAppOps)) {
          n += localMarginLayoutParams.leftMargin + localMarginLayoutParams.rightMargin + localView.getMeasuredWidth();
        } else {
          paramInt2 += localMarginLayoutParams.leftMargin + localMarginLayoutParams.rightMargin + localView.getMeasuredWidth();
        }
      }
    }
    paramInt1 = Math.max(this.mHeaderTextMarginEnd, paramInt2);
    if (n > i - paramInt1) {
      shrinkViewForOverflow(m, shrinkViewForOverflow(m, shrinkViewForOverflow(m, n - i + paramInt1, this.mAppName, this.mChildMinWidth), this.mHeaderText, 0), this.mSecondaryHeaderText, 0);
    }
    this.mTotalWidth = Math.min(n + getPaddingEnd(), i);
    setMeasuredDimension(i, j);
  }
  
  @RemotableViewMethod
  public void setAcceptAllTouches(boolean paramBoolean)
  {
    if ((!this.mEntireHeaderClickable) && (!paramBoolean)) {
      paramBoolean = false;
    } else {
      paramBoolean = true;
    }
    this.mAcceptAllTouches = paramBoolean;
  }
  
  public void setAppOpsOnClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mAppOpsListener = paramOnClickListener;
    this.mAppOps.setOnClickListener(this.mAppOpsListener);
    this.mCameraIcon.setOnClickListener(this.mAppOpsListener);
    this.mMicIcon.setOnClickListener(this.mAppOpsListener);
    this.mOverlayIcon.setOnClickListener(this.mAppOpsListener);
    updateTouchListener();
  }
  
  @RemotableViewMethod
  public void setExpandOnlyOnButton(boolean paramBoolean)
  {
    this.mExpandOnlyOnButton = paramBoolean;
  }
  
  @RemotableViewMethod
  public void setExpanded(boolean paramBoolean)
  {
    this.mExpanded = paramBoolean;
    updateExpandButton();
  }
  
  public void setHeaderBackgroundDrawable(Drawable paramDrawable)
  {
    if (paramDrawable != null)
    {
      setWillNotDraw(false);
      this.mBackground = paramDrawable;
      this.mBackground.setCallback(this);
      setOutlineProvider(this.mProvider);
    }
    else
    {
      setWillNotDraw(true);
      this.mBackground = null;
      setOutlineProvider(null);
    }
    invalidate();
  }
  
  @RemotableViewMethod
  public void setHeaderTextMarginEnd(int paramInt)
  {
    if (this.mHeaderTextMarginEnd != paramInt)
    {
      this.mHeaderTextMarginEnd = paramInt;
      requestLayout();
    }
  }
  
  public void setOnClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mExpandClickListener = paramOnClickListener;
    this.mExpandButton.setOnClickListener(this.mExpandClickListener);
    updateTouchListener();
  }
  
  @RemotableViewMethod
  public void setOriginalIconColor(int paramInt)
  {
    this.mIconColor = paramInt;
  }
  
  @RemotableViewMethod
  public void setOriginalNotificationColor(int paramInt)
  {
    this.mOriginalNotificationColor = paramInt;
  }
  
  public void setRecentlyAudiblyAlerted(boolean paramBoolean)
  {
    View localView = this.mAudiblyAlertedIcon;
    int i;
    if (paramBoolean) {
      i = 0;
    } else {
      i = 8;
    }
    localView.setVisibility(i);
  }
  
  public void setShowExpandButtonAtEnd(boolean paramBoolean)
  {
    if (paramBoolean != this.mShowExpandButtonAtEnd)
    {
      setClipToPadding(paramBoolean ^ true);
      this.mShowExpandButtonAtEnd = paramBoolean;
    }
  }
  
  public void setShowWorkBadgeAtEnd(boolean paramBoolean)
  {
    if (paramBoolean != this.mShowWorkBadgeAtEnd)
    {
      setClipToPadding(paramBoolean ^ true);
      this.mShowWorkBadgeAtEnd = paramBoolean;
    }
  }
  
  public void showAppOpsIcons(ArraySet<Integer> paramArraySet)
  {
    View localView = this.mOverlayIcon;
    if ((localView != null) && (this.mCameraIcon != null) && (this.mMicIcon != null) && (paramArraySet != null))
    {
      boolean bool = paramArraySet.contains(Integer.valueOf(24));
      int i = 0;
      int j;
      if (bool) {
        j = 0;
      } else {
        j = 8;
      }
      localView.setVisibility(j);
      localView = this.mCameraIcon;
      if (paramArraySet.contains(Integer.valueOf(26))) {
        j = 0;
      } else {
        j = 8;
      }
      localView.setVisibility(j);
      localView = this.mMicIcon;
      if (paramArraySet.contains(Integer.valueOf(27))) {
        j = i;
      } else {
        j = 8;
      }
      localView.setVisibility(j);
      return;
    }
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    boolean bool;
    if ((!super.verifyDrawable(paramDrawable)) && (paramDrawable != this.mBackground)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public class HeaderTouchListener
    implements View.OnTouchListener
  {
    private Rect mAppOpsRect;
    private float mDownX;
    private float mDownY;
    private Rect mExpandButtonRect;
    private final ArrayList<Rect> mTouchRects = new ArrayList();
    private int mTouchSlop;
    private boolean mTrackGesture;
    
    public HeaderTouchListener() {}
    
    private Rect addRectAroundView(View paramView)
    {
      paramView = getRectAroundView(paramView);
      this.mTouchRects.add(paramView);
      return paramView;
    }
    
    private void addWidthRect()
    {
      Rect localRect = new Rect();
      localRect.top = 0;
      localRect.bottom = ((int)(NotificationHeaderView.this.getResources().getDisplayMetrics().density * 32.0F));
      localRect.left = 0;
      localRect.right = NotificationHeaderView.this.getWidth();
      this.mTouchRects.add(localRect);
    }
    
    private Rect getRectAroundView(View paramView)
    {
      float f1 = NotificationHeaderView.this.getResources().getDisplayMetrics().density * 48.0F;
      float f2 = Math.max(f1, paramView.getWidth());
      f1 = Math.max(f1, paramView.getHeight());
      Rect localRect = new Rect();
      if (paramView.getVisibility() == 8)
      {
        paramView = NotificationHeaderView.this.getFirstChildNotGone();
        localRect.left = ((int)(paramView.getLeft() - f2 / 2.0F));
      }
      else
      {
        localRect.left = ((int)((paramView.getLeft() + paramView.getRight()) / 2.0F - f2 / 2.0F));
      }
      localRect.top = ((int)((paramView.getTop() + paramView.getBottom()) / 2.0F - f1 / 2.0F));
      localRect.bottom = ((int)(localRect.top + f1));
      localRect.right = ((int)(localRect.left + f2));
      return localRect;
    }
    
    private boolean isInside(float paramFloat1, float paramFloat2)
    {
      if (NotificationHeaderView.this.mAcceptAllTouches) {
        return true;
      }
      if (NotificationHeaderView.this.mExpandOnlyOnButton) {
        return this.mExpandButtonRect.contains((int)paramFloat1, (int)paramFloat2);
      }
      for (int i = 0; i < this.mTouchRects.size(); i++) {
        if (((Rect)this.mTouchRects.get(i)).contains((int)paramFloat1, (int)paramFloat2)) {
          return true;
        }
      }
      return false;
    }
    
    public void bindTouchRects()
    {
      this.mTouchRects.clear();
      addRectAroundView(NotificationHeaderView.this.mIcon);
      this.mExpandButtonRect = addRectAroundView(NotificationHeaderView.this.mExpandButton);
      this.mAppOpsRect = addRectAroundView(NotificationHeaderView.this.mAppOps);
      addWidthRect();
      this.mTouchSlop = ViewConfiguration.get(NotificationHeaderView.this.getContext()).getScaledTouchSlop();
    }
    
    public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
    {
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      int i = paramMotionEvent.getActionMasked() & 0xFF;
      if (i != 0)
      {
        if (i != 1)
        {
          if ((i == 2) && (this.mTrackGesture) && ((Math.abs(this.mDownX - f1) > this.mTouchSlop) || (Math.abs(this.mDownY - f2) > this.mTouchSlop))) {
            this.mTrackGesture = false;
          }
        }
        else if (this.mTrackGesture)
        {
          if ((NotificationHeaderView.this.mAppOps.isVisibleToUser()) && ((this.mAppOpsRect.contains((int)f1, (int)f2)) || (this.mAppOpsRect.contains((int)this.mDownX, (int)this.mDownY))))
          {
            NotificationHeaderView.this.mAppOps.performClick();
            return true;
          }
          NotificationHeaderView.this.mExpandButton.performClick();
        }
      }
      else
      {
        this.mTrackGesture = false;
        if (isInside(f1, f2))
        {
          this.mDownX = f1;
          this.mDownY = f2;
          this.mTrackGesture = true;
          return true;
        }
      }
      return this.mTrackGesture;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/NotificationHeaderView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */