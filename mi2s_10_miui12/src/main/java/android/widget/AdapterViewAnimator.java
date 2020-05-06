package android.widget;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.FilterComparison;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import com.android.internal.R.styleable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public abstract class AdapterViewAnimator
  extends AdapterView<Adapter>
  implements RemoteViewsAdapter.RemoteAdapterConnectionCallback, Advanceable
{
  private static final int DEFAULT_ANIMATION_DURATION = 200;
  private static final String TAG = "RemoteViewAnimator";
  static final int TOUCH_MODE_DOWN_IN_CURRENT_VIEW = 1;
  static final int TOUCH_MODE_HANDLED = 2;
  static final int TOUCH_MODE_NONE = 0;
  int mActiveOffset = 0;
  Adapter mAdapter;
  boolean mAnimateFirstTime = true;
  int mCurrentWindowEnd = -1;
  int mCurrentWindowStart = 0;
  int mCurrentWindowStartUnbounded = 0;
  AdapterView<Adapter>.AdapterDataSetObserver mDataSetObserver;
  boolean mDeferNotifyDataSetChanged = false;
  boolean mFirstTime = true;
  ObjectAnimator mInAnimation;
  boolean mLoopViews = true;
  int mMaxNumActiveViews = 1;
  ObjectAnimator mOutAnimation;
  private Runnable mPendingCheckForTap;
  ArrayList<Integer> mPreviousViews;
  int mReferenceChildHeight = -1;
  int mReferenceChildWidth = -1;
  RemoteViewsAdapter mRemoteViewsAdapter;
  private int mRestoreWhichChild = -1;
  private int mTouchMode = 0;
  HashMap<Integer, ViewAndMetaData> mViewsMap = new HashMap();
  int mWhichChild = 0;
  
  public AdapterViewAnimator(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AdapterViewAnimator(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public AdapterViewAnimator(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public AdapterViewAnimator(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.AdapterViewAnimator, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.AdapterViewAnimator, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    paramInt1 = localTypedArray.getResourceId(0, 0);
    if (paramInt1 > 0) {
      setInAnimation(paramContext, paramInt1);
    } else {
      setInAnimation(getDefaultInAnimation());
    }
    paramInt1 = localTypedArray.getResourceId(1, 0);
    if (paramInt1 > 0) {
      setOutAnimation(paramContext, paramInt1);
    } else {
      setOutAnimation(getDefaultOutAnimation());
    }
    setAnimateFirstView(localTypedArray.getBoolean(2, true));
    this.mLoopViews = localTypedArray.getBoolean(3, false);
    localTypedArray.recycle();
    initViewAnimator();
  }
  
  private void addChild(View paramView)
  {
    addViewInLayout(paramView, -1, createOrReuseLayoutParams(paramView));
    if ((this.mReferenceChildWidth == -1) || (this.mReferenceChildHeight == -1))
    {
      int i = View.MeasureSpec.makeMeasureSpec(0, 0);
      paramView.measure(i, i);
      this.mReferenceChildWidth = paramView.getMeasuredWidth();
      this.mReferenceChildHeight = paramView.getMeasuredHeight();
    }
  }
  
  private ViewAndMetaData getMetaDataForChild(View paramView)
  {
    Iterator localIterator = this.mViewsMap.values().iterator();
    while (localIterator.hasNext())
    {
      ViewAndMetaData localViewAndMetaData = (ViewAndMetaData)localIterator.next();
      if (localViewAndMetaData.view == paramView) {
        return localViewAndMetaData;
      }
    }
    return null;
  }
  
  private void initViewAnimator()
  {
    this.mPreviousViews = new ArrayList();
  }
  
  private void measureChildren()
  {
    int i = getChildCount();
    int j = getMeasuredWidth();
    int k = this.mPaddingLeft;
    int m = this.mPaddingRight;
    int n = getMeasuredHeight();
    int i1 = this.mPaddingTop;
    int i2 = this.mPaddingBottom;
    for (int i3 = 0; i3 < i; i3++) {
      getChildAt(i3).measure(View.MeasureSpec.makeMeasureSpec(j - k - m, 1073741824), View.MeasureSpec.makeMeasureSpec(n - i1 - i2, 1073741824));
    }
  }
  
  private void setDisplayedChild(int paramInt, boolean paramBoolean)
  {
    if (this.mAdapter != null)
    {
      this.mWhichChild = paramInt;
      int i = getWindowSize();
      int j = 0;
      if (paramInt >= i)
      {
        if (this.mLoopViews) {
          paramInt = 0;
        } else {
          paramInt = getWindowSize() - 1;
        }
        this.mWhichChild = paramInt;
      }
      else if (paramInt < 0)
      {
        if (this.mLoopViews) {
          paramInt = getWindowSize() - 1;
        } else {
          paramInt = 0;
        }
        this.mWhichChild = paramInt;
      }
      paramInt = j;
      if (getFocusedChild() != null) {
        paramInt = 1;
      }
      showOnly(this.mWhichChild, paramBoolean);
      if (paramInt != 0) {
        requestFocus(2);
      }
    }
  }
  
  public void advance()
  {
    showNext();
  }
  
  void applyTransformForChildAtIndex(View paramView, int paramInt) {}
  
  void cancelHandleClick()
  {
    View localView = getCurrentView();
    if (localView != null) {
      hideTapFeedback(localView);
    }
    this.mTouchMode = 0;
  }
  
  void checkForAndHandleDataChanged()
  {
    if (this.mDataChanged) {
      post(new Runnable()
      {
        public void run()
        {
          AdapterViewAnimator.this.handleDataChanged();
          AdapterViewAnimator localAdapterViewAnimator;
          if (AdapterViewAnimator.this.mWhichChild >= AdapterViewAnimator.this.getWindowSize())
          {
            localAdapterViewAnimator = AdapterViewAnimator.this;
            localAdapterViewAnimator.mWhichChild = 0;
            localAdapterViewAnimator.showOnly(localAdapterViewAnimator.mWhichChild, false);
          }
          else if (AdapterViewAnimator.this.mOldItemCount != AdapterViewAnimator.this.getCount())
          {
            localAdapterViewAnimator = AdapterViewAnimator.this;
            localAdapterViewAnimator.showOnly(localAdapterViewAnimator.mWhichChild, false);
          }
          AdapterViewAnimator.this.refreshChildren();
          AdapterViewAnimator.this.requestLayout();
        }
      });
    }
    this.mDataChanged = false;
  }
  
  void configureViewAnimator(int paramInt1, int paramInt2)
  {
    this.mMaxNumActiveViews = paramInt1;
    this.mActiveOffset = paramInt2;
    this.mPreviousViews.clear();
    this.mViewsMap.clear();
    removeAllViewsInLayout();
    this.mCurrentWindowStart = 0;
    this.mCurrentWindowEnd = -1;
  }
  
  ViewGroup.LayoutParams createOrReuseLayoutParams(View paramView)
  {
    paramView = paramView.getLayoutParams();
    if (paramView != null) {
      return paramView;
    }
    return new ViewGroup.LayoutParams(0, 0);
  }
  
  public void deferNotifyDataSetChanged()
  {
    this.mDeferNotifyDataSetChanged = true;
  }
  
  public void fyiWillBeAdvancedByHostKThx() {}
  
  public CharSequence getAccessibilityClassName()
  {
    return AdapterViewAnimator.class.getName();
  }
  
  public Adapter getAdapter()
  {
    return this.mAdapter;
  }
  
  public int getBaseline()
  {
    int i;
    if (getCurrentView() != null) {
      i = getCurrentView().getBaseline();
    } else {
      i = super.getBaseline();
    }
    return i;
  }
  
  public View getCurrentView()
  {
    return getViewAtRelativeIndex(this.mActiveOffset);
  }
  
  ObjectAnimator getDefaultInAnimation()
  {
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(null, "alpha", new float[] { 0.0F, 1.0F });
    localObjectAnimator.setDuration(200L);
    return localObjectAnimator;
  }
  
  ObjectAnimator getDefaultOutAnimation()
  {
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(null, "alpha", new float[] { 1.0F, 0.0F });
    localObjectAnimator.setDuration(200L);
    return localObjectAnimator;
  }
  
  public int getDisplayedChild()
  {
    return this.mWhichChild;
  }
  
  FrameLayout getFrameForChild()
  {
    return new FrameLayout(this.mContext);
  }
  
  public ObjectAnimator getInAnimation()
  {
    return this.mInAnimation;
  }
  
  int getNumActiveViews()
  {
    if (this.mAdapter != null) {
      return Math.min(getCount() + 1, this.mMaxNumActiveViews);
    }
    return this.mMaxNumActiveViews;
  }
  
  public ObjectAnimator getOutAnimation()
  {
    return this.mOutAnimation;
  }
  
  public View getSelectedView()
  {
    return getViewAtRelativeIndex(this.mActiveOffset);
  }
  
  View getViewAtRelativeIndex(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt <= getNumActiveViews() - 1) && (this.mAdapter != null))
    {
      paramInt = modulo(this.mCurrentWindowStartUnbounded + paramInt, getWindowSize());
      if (this.mViewsMap.get(Integer.valueOf(paramInt)) != null) {
        return ((ViewAndMetaData)this.mViewsMap.get(Integer.valueOf(paramInt))).view;
      }
    }
    return null;
  }
  
  int getWindowSize()
  {
    if (this.mAdapter != null)
    {
      int i = getCount();
      if ((i <= getNumActiveViews()) && (this.mLoopViews)) {
        return this.mMaxNumActiveViews * i;
      }
      return i;
    }
    return 0;
  }
  
  void hideTapFeedback(View paramView)
  {
    paramView.setPressed(false);
  }
  
  int modulo(int paramInt1, int paramInt2)
  {
    if (paramInt2 > 0) {
      return (paramInt1 % paramInt2 + paramInt2) % paramInt2;
    }
    return 0;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    checkForAndHandleDataChanged();
    paramInt2 = getChildCount();
    for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++)
    {
      View localView = getChildAt(paramInt1);
      int i = this.mPaddingLeft;
      paramInt4 = localView.getMeasuredWidth();
      int j = this.mPaddingTop;
      paramInt3 = localView.getMeasuredHeight();
      localView.layout(this.mPaddingLeft, this.mPaddingTop, i + paramInt4, j + paramInt3);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getSize(paramInt1);
    int j = View.MeasureSpec.getSize(paramInt2);
    int k = View.MeasureSpec.getMode(paramInt1);
    int m = View.MeasureSpec.getMode(paramInt2);
    paramInt1 = this.mReferenceChildWidth;
    paramInt2 = 0;
    int n;
    if ((paramInt1 != -1) && (this.mReferenceChildHeight != -1)) {
      n = 1;
    } else {
      n = 0;
    }
    if (m == 0)
    {
      if (n != 0) {
        paramInt1 = this.mReferenceChildHeight + this.mPaddingTop + this.mPaddingBottom;
      } else {
        paramInt1 = 0;
      }
    }
    else
    {
      paramInt1 = j;
      if (m == Integer.MIN_VALUE)
      {
        paramInt1 = j;
        if (n != 0)
        {
          paramInt1 = this.mReferenceChildHeight + this.mPaddingTop + this.mPaddingBottom;
          if (paramInt1 > j) {
            paramInt1 = j | 0x1000000;
          }
        }
      }
    }
    if (k == 0)
    {
      if (n != 0)
      {
        n = this.mReferenceChildWidth;
        paramInt2 = this.mPaddingLeft;
        paramInt2 = this.mPaddingRight + (n + paramInt2);
      }
    }
    else
    {
      paramInt2 = i;
      if (m == Integer.MIN_VALUE)
      {
        paramInt2 = i;
        if (n != 0)
        {
          paramInt2 = this.mReferenceChildWidth + this.mPaddingLeft + this.mPaddingRight;
          if (paramInt2 > i) {
            paramInt2 = i | 0x1000000;
          }
        }
      }
    }
    setMeasuredDimension(paramInt2, paramInt1);
    measureChildren();
  }
  
  public boolean onRemoteAdapterConnected()
  {
    RemoteViewsAdapter localRemoteViewsAdapter = this.mRemoteViewsAdapter;
    if (localRemoteViewsAdapter != this.mAdapter)
    {
      setAdapter(localRemoteViewsAdapter);
      if (this.mDeferNotifyDataSetChanged)
      {
        this.mRemoteViewsAdapter.notifyDataSetChanged();
        this.mDeferNotifyDataSetChanged = false;
      }
      int i = this.mRestoreWhichChild;
      if (i > -1)
      {
        setDisplayedChild(i, false);
        this.mRestoreWhichChild = -1;
      }
      return false;
    }
    if (localRemoteViewsAdapter != null)
    {
      localRemoteViewsAdapter.superNotifyDataSetChanged();
      return true;
    }
    return false;
  }
  
  public void onRemoteAdapterDisconnected() {}
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    this.mWhichChild = paramParcelable.whichChild;
    if ((this.mRemoteViewsAdapter != null) && (this.mAdapter == null)) {
      this.mRestoreWhichChild = this.mWhichChild;
    } else {
      setDisplayedChild(this.mWhichChild, false);
    }
  }
  
  public Parcelable onSaveInstanceState()
  {
    Parcelable localParcelable = super.onSaveInstanceState();
    RemoteViewsAdapter localRemoteViewsAdapter = this.mRemoteViewsAdapter;
    if (localRemoteViewsAdapter != null) {
      localRemoteViewsAdapter.saveRemoteViewsCache();
    }
    return new SavedState(localParcelable, this.mWhichChild);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction();
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3;
    final Object localObject;
    if (i != 0)
    {
      if (i != 1)
      {
        if (i != 2)
        {
          if (i != 3)
          {
            if (i != 6) {
              bool3 = bool1;
            } else {
              bool3 = bool1;
            }
          }
          else
          {
            paramMotionEvent = getCurrentView();
            if (paramMotionEvent != null) {
              hideTapFeedback(paramMotionEvent);
            }
            this.mTouchMode = 0;
            bool3 = bool1;
          }
        }
        else {
          bool3 = bool1;
        }
      }
      else
      {
        bool3 = bool2;
        if (this.mTouchMode == 1)
        {
          final View localView = getCurrentView();
          localObject = getMetaDataForChild(localView);
          bool3 = bool2;
          if (localView != null)
          {
            bool3 = bool2;
            if (isTransformedTouchPointInView(paramMotionEvent.getX(), paramMotionEvent.getY(), localView, null))
            {
              paramMotionEvent = getHandler();
              if (paramMotionEvent != null) {
                paramMotionEvent.removeCallbacks(this.mPendingCheckForTap);
              }
              showTapFeedback(localView);
              postDelayed(new Runnable()
              {
                public void run()
                {
                  AdapterViewAnimator.this.hideTapFeedback(localView);
                  AdapterViewAnimator.this.post(new Runnable()
                  {
                    public void run()
                    {
                      if (AdapterViewAnimator.1.this.val$viewData != null) {
                        AdapterViewAnimator.this.performItemClick(AdapterViewAnimator.1.this.val$v, AdapterViewAnimator.1.this.val$viewData.adapterPosition, AdapterViewAnimator.1.this.val$viewData.itemId);
                      } else {
                        AdapterViewAnimator.this.performItemClick(AdapterViewAnimator.1.this.val$v, 0, 0L);
                      }
                    }
                  });
                }
              }, ViewConfiguration.getPressedStateDuration());
              bool3 = true;
            }
          }
        }
        this.mTouchMode = 0;
      }
    }
    else
    {
      localObject = getCurrentView();
      bool3 = bool1;
      if (localObject != null)
      {
        bool3 = bool1;
        if (isTransformedTouchPointInView(paramMotionEvent.getX(), paramMotionEvent.getY(), (View)localObject, null))
        {
          if (this.mPendingCheckForTap == null) {
            this.mPendingCheckForTap = new CheckForTap();
          }
          this.mTouchMode = 1;
          postDelayed(this.mPendingCheckForTap, ViewConfiguration.getTapTimeout());
          bool3 = bool1;
        }
      }
    }
    return bool3;
  }
  
  void refreshChildren()
  {
    if (this.mAdapter == null) {
      return;
    }
    for (int i = this.mCurrentWindowStart; i <= this.mCurrentWindowEnd; i++)
    {
      int j = modulo(i, getWindowSize());
      int k = getCount();
      View localView = this.mAdapter.getView(modulo(i, k), null, this);
      if (localView.getImportantForAccessibility() == 0) {
        localView.setImportantForAccessibility(1);
      }
      if (this.mViewsMap.containsKey(Integer.valueOf(j)))
      {
        FrameLayout localFrameLayout = (FrameLayout)((ViewAndMetaData)this.mViewsMap.get(Integer.valueOf(j))).view;
        localFrameLayout.removeAllViewsInLayout();
        localFrameLayout.addView(localView);
      }
    }
  }
  
  public void setAdapter(Adapter paramAdapter)
  {
    Adapter localAdapter = this.mAdapter;
    if (localAdapter != null)
    {
      AdapterView.AdapterDataSetObserver localAdapterDataSetObserver = this.mDataSetObserver;
      if (localAdapterDataSetObserver != null) {
        localAdapter.unregisterDataSetObserver(localAdapterDataSetObserver);
      }
    }
    this.mAdapter = paramAdapter;
    checkFocus();
    if (this.mAdapter != null)
    {
      this.mDataSetObserver = new AdapterView.AdapterDataSetObserver(this);
      this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
      this.mItemCount = this.mAdapter.getCount();
    }
    setFocusable(true);
    this.mWhichChild = 0;
    showOnly(this.mWhichChild, false);
  }
  
  public void setAnimateFirstView(boolean paramBoolean)
  {
    this.mAnimateFirstTime = paramBoolean;
  }
  
  @RemotableViewMethod
  public void setDisplayedChild(int paramInt)
  {
    setDisplayedChild(paramInt, true);
  }
  
  public void setInAnimation(ObjectAnimator paramObjectAnimator)
  {
    this.mInAnimation = paramObjectAnimator;
  }
  
  public void setInAnimation(Context paramContext, int paramInt)
  {
    setInAnimation((ObjectAnimator)AnimatorInflater.loadAnimator(paramContext, paramInt));
  }
  
  public void setOutAnimation(ObjectAnimator paramObjectAnimator)
  {
    this.mOutAnimation = paramObjectAnimator;
  }
  
  public void setOutAnimation(Context paramContext, int paramInt)
  {
    setOutAnimation((ObjectAnimator)AnimatorInflater.loadAnimator(paramContext, paramInt));
  }
  
  @RemotableViewMethod(asyncImpl="setRemoteViewsAdapterAsync")
  public void setRemoteViewsAdapter(Intent paramIntent)
  {
    setRemoteViewsAdapter(paramIntent, false);
  }
  
  public void setRemoteViewsAdapter(Intent paramIntent, boolean paramBoolean)
  {
    if ((this.mRemoteViewsAdapter != null) && (new Intent.FilterComparison(paramIntent).equals(new Intent.FilterComparison(this.mRemoteViewsAdapter.getRemoteViewsServiceIntent())))) {
      return;
    }
    this.mDeferNotifyDataSetChanged = false;
    this.mRemoteViewsAdapter = new RemoteViewsAdapter(getContext(), paramIntent, this, paramBoolean);
    if (this.mRemoteViewsAdapter.isDataReady()) {
      setAdapter(this.mRemoteViewsAdapter);
    }
  }
  
  public Runnable setRemoteViewsAdapterAsync(Intent paramIntent)
  {
    return new RemoteViewsAdapter.AsyncRemoteAdapterAction(this, paramIntent);
  }
  
  public void setRemoteViewsOnClickHandler(RemoteViews.OnClickHandler paramOnClickHandler)
  {
    RemoteViewsAdapter localRemoteViewsAdapter = this.mRemoteViewsAdapter;
    if (localRemoteViewsAdapter != null) {
      localRemoteViewsAdapter.setRemoteViewsOnClickHandler(paramOnClickHandler);
    }
  }
  
  public void setSelection(int paramInt)
  {
    setDisplayedChild(paramInt);
  }
  
  public void showNext()
  {
    setDisplayedChild(this.mWhichChild + 1);
  }
  
  void showOnly(int paramInt, boolean paramBoolean)
  {
    if (this.mAdapter == null) {
      return;
    }
    int i = getCount();
    if (i == 0) {
      return;
    }
    Object localObject1;
    for (int j = 0; j < this.mPreviousViews.size(); j++)
    {
      localObject1 = ((ViewAndMetaData)this.mViewsMap.get(this.mPreviousViews.get(j))).view;
      this.mViewsMap.remove(this.mPreviousViews.get(j));
      ((View)localObject1).clearAnimation();
      if ((localObject1 instanceof ViewGroup)) {
        ((ViewGroup)localObject1).removeAllViewsInLayout();
      }
      applyTransformForChildAtIndex((View)localObject1, -1);
      removeViewInLayout((View)localObject1);
    }
    this.mPreviousViews.clear();
    int k = paramInt - this.mActiveOffset;
    int m = getNumActiveViews() + k - 1;
    paramInt = Math.max(0, k);
    j = Math.min(i - 1, m);
    if (this.mLoopViews)
    {
      paramInt = k;
      j = m;
    }
    int n = modulo(paramInt, getWindowSize());
    int i1 = modulo(j, getWindowSize());
    if (n > i1) {
      i2 = 1;
    } else {
      i2 = 0;
    }
    Iterator localIterator = this.mViewsMap.keySet().iterator();
    Object localObject2;
    while (localIterator.hasNext())
    {
      localObject1 = (Integer)localIterator.next();
      i3 = 0;
      if ((i2 == 0) && ((((Integer)localObject1).intValue() < n) || (((Integer)localObject1).intValue() > i1)))
      {
        i4 = 1;
      }
      else
      {
        i4 = i3;
        if (i2 != 0)
        {
          i4 = i3;
          if (((Integer)localObject1).intValue() > i1)
          {
            i4 = i3;
            if (((Integer)localObject1).intValue() < n) {
              i4 = 1;
            }
          }
        }
      }
      if (i4 != 0)
      {
        localObject2 = ((ViewAndMetaData)this.mViewsMap.get(localObject1)).view;
        i4 = ((ViewAndMetaData)this.mViewsMap.get(localObject1)).relativeIndex;
        this.mPreviousViews.add(localObject1);
        transformViewForTransition(i4, -1, (View)localObject2, paramBoolean);
      }
    }
    if ((paramInt == this.mCurrentWindowStart) && (j == this.mCurrentWindowEnd) && (k == this.mCurrentWindowStartUnbounded)) {
      break label799;
    }
    int i3 = paramInt;
    int i4 = i;
    int i2 = n;
    i = i1;
    while (i3 <= j)
    {
      int i5 = modulo(i3, getWindowSize());
      if (this.mViewsMap.containsKey(Integer.valueOf(i5))) {
        i1 = ((ViewAndMetaData)this.mViewsMap.get(Integer.valueOf(i5))).relativeIndex;
      } else {
        i1 = -1;
      }
      int i6 = i3 - k;
      if ((this.mViewsMap.containsKey(Integer.valueOf(i5))) && (!this.mPreviousViews.contains(Integer.valueOf(i5)))) {
        n = 1;
      } else {
        n = 0;
      }
      if (n != 0)
      {
        localObject1 = ((ViewAndMetaData)this.mViewsMap.get(Integer.valueOf(i5))).view;
        ((ViewAndMetaData)this.mViewsMap.get(Integer.valueOf(i5))).relativeIndex = i6;
        applyTransformForChildAtIndex((View)localObject1, i6);
        transformViewForTransition(i1, i6, (View)localObject1, paramBoolean);
      }
      else
      {
        i1 = modulo(i3, i4);
        localObject1 = this.mAdapter.getView(i1, null, this);
        long l = this.mAdapter.getItemId(i1);
        localObject2 = getFrameForChild();
        if (localObject1 != null) {
          ((FrameLayout)localObject2).addView((View)localObject1);
        }
        this.mViewsMap.put(Integer.valueOf(i5), new ViewAndMetaData((View)localObject2, i6, i1, l));
        addChild((View)localObject2);
        applyTransformForChildAtIndex((View)localObject2, i6);
        transformViewForTransition(-1, i6, (View)localObject2, paramBoolean);
      }
      ((ViewAndMetaData)this.mViewsMap.get(Integer.valueOf(i5))).view.bringToFront();
      i3++;
    }
    this.mCurrentWindowStart = paramInt;
    this.mCurrentWindowEnd = j;
    this.mCurrentWindowStartUnbounded = k;
    if (this.mRemoteViewsAdapter != null)
    {
      paramInt = modulo(this.mCurrentWindowStart, i4);
      j = modulo(this.mCurrentWindowEnd, i4);
      this.mRemoteViewsAdapter.setVisibleRangeHint(paramInt, j);
    }
    label799:
    requestLayout();
    invalidate();
  }
  
  public void showPrevious()
  {
    setDisplayedChild(this.mWhichChild - 1);
  }
  
  void showTapFeedback(View paramView)
  {
    paramView.setPressed(true);
  }
  
  void transformViewForTransition(int paramInt1, int paramInt2, View paramView, boolean paramBoolean)
  {
    if (paramInt1 == -1)
    {
      this.mInAnimation.setTarget(paramView);
      this.mInAnimation.start();
    }
    else if (paramInt2 == -1)
    {
      this.mOutAnimation.setTarget(paramView);
      this.mOutAnimation.start();
    }
  }
  
  final class CheckForTap
    implements Runnable
  {
    CheckForTap() {}
    
    public void run()
    {
      if (AdapterViewAnimator.this.mTouchMode == 1)
      {
        View localView = AdapterViewAnimator.this.getCurrentView();
        AdapterViewAnimator.this.showTapFeedback(localView);
      }
    }
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public AdapterViewAnimator.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new AdapterViewAnimator.SavedState(paramAnonymousParcel, null);
      }
      
      public AdapterViewAnimator.SavedState[] newArray(int paramAnonymousInt)
      {
        return new AdapterViewAnimator.SavedState[paramAnonymousInt];
      }
    };
    int whichChild;
    
    private SavedState(Parcel paramParcel)
    {
      super();
      this.whichChild = paramParcel.readInt();
    }
    
    SavedState(Parcelable paramParcelable, int paramInt)
    {
      super();
      this.whichChild = paramInt;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("AdapterViewAnimator.SavedState{ whichChild = ");
      localStringBuilder.append(this.whichChild);
      localStringBuilder.append(" }");
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.whichChild);
    }
  }
  
  class ViewAndMetaData
  {
    int adapterPosition;
    long itemId;
    int relativeIndex;
    View view;
    
    ViewAndMetaData(View paramView, int paramInt1, int paramInt2, long paramLong)
    {
      this.view = paramView;
      this.relativeIndex = paramInt1;
      this.adapterPosition = paramInt2;
      this.itemId = paramLong;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/AdapterViewAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */