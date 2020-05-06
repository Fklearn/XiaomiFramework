package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.R.styleable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

public class AutoCompleteTextView
  extends EditText
  implements Filter.FilterListener
{
  static final boolean DEBUG = false;
  static final int EXPAND_MAX = 3;
  static final String TAG = "AutoCompleteTextView";
  private ListAdapter mAdapter;
  private MyWatcher mAutoCompleteTextWatcher;
  private boolean mBlockCompletion;
  private int mDropDownAnchorId;
  private boolean mDropDownDismissedOnCompletion = true;
  private Filter mFilter;
  private int mHintResource;
  private CharSequence mHintText;
  @UnsupportedAppUsage
  private TextView mHintView;
  private AdapterView.OnItemClickListener mItemClickListener;
  private AdapterView.OnItemSelectedListener mItemSelectedListener;
  private int mLastKeyCode = 0;
  @UnsupportedAppUsage
  private PopupDataSetObserver mObserver;
  @UnsupportedAppUsage
  private final PassThroughClickListener mPassThroughClickListener;
  @UnsupportedAppUsage
  private final ListPopupWindow mPopup;
  private boolean mPopupCanBeUpdated = true;
  private final Context mPopupContext;
  private int mThreshold;
  private Validator mValidator = null;
  
  public AutoCompleteTextView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AutoCompleteTextView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842859);
  }
  
  public AutoCompleteTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public AutoCompleteTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    this(paramContext, paramAttributeSet, paramInt1, paramInt2, null);
  }
  
  public AutoCompleteTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2, Resources.Theme paramTheme)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.AutoCompleteTextView, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.AutoCompleteTextView, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    if (paramTheme != null)
    {
      this.mPopupContext = new ContextThemeWrapper(paramContext, paramTheme);
    }
    else
    {
      i = localTypedArray.getResourceId(8, 0);
      if (i != 0) {
        this.mPopupContext = new ContextThemeWrapper(paramContext, i);
      } else {
        this.mPopupContext = paramContext;
      }
    }
    paramTheme = this.mPopupContext;
    if (paramTheme != paramContext)
    {
      paramTheme = paramTheme.obtainStyledAttributes(paramAttributeSet, R.styleable.AutoCompleteTextView, paramInt1, paramInt2);
      saveAttributeDataForStyleable(paramContext, R.styleable.AutoCompleteTextView, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
      paramContext = paramTheme;
    }
    else
    {
      paramContext = localTypedArray;
    }
    paramTheme = paramContext.getDrawable(3);
    int i = paramContext.getLayoutDimension(5, -2);
    int j = paramContext.getLayoutDimension(7, -2);
    int k = paramContext.getResourceId(1, 17367314);
    CharSequence localCharSequence = paramContext.getText(0);
    if (paramContext != localTypedArray) {
      paramContext.recycle();
    }
    this.mPopup = new ListPopupWindow(this.mPopupContext, paramAttributeSet, paramInt1, paramInt2);
    this.mPopup.setSoftInputMode(16);
    this.mPopup.setPromptPosition(1);
    this.mPopup.setListSelector(paramTheme);
    this.mPopup.setOnItemClickListener(new DropDownItemClickListener(null));
    this.mPopup.setWidth(i);
    this.mPopup.setHeight(j);
    this.mHintResource = k;
    setCompletionHint(localCharSequence);
    this.mDropDownAnchorId = localTypedArray.getResourceId(6, -1);
    this.mThreshold = localTypedArray.getInt(2, 2);
    localTypedArray.recycle();
    paramInt1 = getInputType();
    if ((paramInt1 & 0xF) == 1) {
      setRawInputType(paramInt1 | 0x10000);
    }
    setFocusable(true);
    this.mAutoCompleteTextWatcher = new MyWatcher(null);
    addTextChangedListener(this.mAutoCompleteTextWatcher);
    this.mPassThroughClickListener = new PassThroughClickListener(null);
    super.setOnClickListener(this.mPassThroughClickListener);
  }
  
  private void buildImeCompletions()
  {
    ListAdapter localListAdapter = this.mAdapter;
    if (localListAdapter != null)
    {
      InputMethodManager localInputMethodManager = (InputMethodManager)getContext().getSystemService(InputMethodManager.class);
      if (localInputMethodManager != null)
      {
        int i = Math.min(localListAdapter.getCount(), 20);
        CompletionInfo[] arrayOfCompletionInfo = new CompletionInfo[i];
        int j = 0;
        int k = 0;
        while (k < i)
        {
          int m = j;
          if (localListAdapter.isEnabled(k))
          {
            localObject = localListAdapter.getItem(k);
            arrayOfCompletionInfo[j] = new CompletionInfo(localListAdapter.getItemId(k), j, convertSelectionToString(localObject));
            m = j + 1;
          }
          k++;
          j = m;
        }
        Object localObject = arrayOfCompletionInfo;
        if (j != i)
        {
          localObject = new CompletionInfo[j];
          System.arraycopy(arrayOfCompletionInfo, 0, localObject, 0, j);
        }
        localInputMethodManager.displayCompletions(this, (CompletionInfo[])localObject);
      }
    }
  }
  
  private void onClickImpl()
  {
    if (isPopupShowing()) {
      ensureImeVisible(true);
    }
  }
  
  private void performCompletion(View paramView, int paramInt, long paramLong)
  {
    if (isPopupShowing())
    {
      Object localObject;
      if (paramInt < 0) {
        localObject = this.mPopup.getSelectedItem();
      } else {
        localObject = this.mAdapter.getItem(paramInt);
      }
      if (localObject == null)
      {
        Log.w("AutoCompleteTextView", "performCompletion: no selected item");
        return;
      }
      this.mBlockCompletion = true;
      replaceText(convertSelectionToString(localObject));
      this.mBlockCompletion = false;
      if (this.mItemClickListener != null)
      {
        localObject = this.mPopup;
        int i;
        if (paramView != null)
        {
          i = paramInt;
          if (paramInt >= 0) {}
        }
        else
        {
          paramView = ((ListPopupWindow)localObject).getSelectedView();
          i = ((ListPopupWindow)localObject).getSelectedItemPosition();
          paramLong = ((ListPopupWindow)localObject).getSelectedItemId();
        }
        this.mItemClickListener.onItemClick(((ListPopupWindow)localObject).getListView(), paramView, i, paramLong);
      }
    }
    if ((this.mDropDownDismissedOnCompletion) && (!this.mPopup.isDropDownAlwaysVisible())) {
      dismissDropDown();
    }
  }
  
  private void updateDropDownForFilter(int paramInt)
  {
    if (getWindowVisibility() == 8) {
      return;
    }
    boolean bool1 = this.mPopup.isDropDownAlwaysVisible();
    boolean bool2 = enoughToFilter();
    if (((paramInt > 0) || (bool1)) && (bool2))
    {
      if ((hasFocus()) && (hasWindowFocus()) && (this.mPopupCanBeUpdated)) {
        showDropDown();
      }
    }
    else if ((!bool1) && (isPopupShowing()))
    {
      dismissDropDown();
      this.mPopupCanBeUpdated = true;
    }
  }
  
  public void clearListSelection()
  {
    this.mPopup.clearListSelection();
  }
  
  protected CharSequence convertSelectionToString(Object paramObject)
  {
    return this.mFilter.convertResultToString(paramObject);
  }
  
  public void dismissDropDown()
  {
    InputMethodManager localInputMethodManager = (InputMethodManager)getContext().getSystemService(InputMethodManager.class);
    if (localInputMethodManager != null) {
      localInputMethodManager.displayCompletions(this, null);
    }
    this.mPopup.dismiss();
    this.mPopupCanBeUpdated = false;
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28)
  void doAfterTextChanged()
  {
    this.mAutoCompleteTextWatcher.afterTextChanged(null);
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28)
  void doBeforeTextChanged()
  {
    this.mAutoCompleteTextWatcher.beforeTextChanged(null, 0, 0, 0);
  }
  
  public boolean enoughToFilter()
  {
    boolean bool;
    if (getText().length() >= this.mThreshold) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123768913L)
  public void ensureImeVisible(boolean paramBoolean)
  {
    ListPopupWindow localListPopupWindow = this.mPopup;
    int i;
    if (paramBoolean) {
      i = 1;
    } else {
      i = 2;
    }
    localListPopupWindow.setInputMethodMode(i);
    if ((this.mPopup.isDropDownAlwaysVisible()) || ((this.mFilter != null) && (enoughToFilter()))) {
      showDropDown();
    }
  }
  
  public ListAdapter getAdapter()
  {
    return this.mAdapter;
  }
  
  public CharSequence getCompletionHint()
  {
    return this.mHintText;
  }
  
  public int getDropDownAnchor()
  {
    return this.mDropDownAnchorId;
  }
  
  public int getDropDownAnimationStyle()
  {
    return this.mPopup.getAnimationStyle();
  }
  
  public Drawable getDropDownBackground()
  {
    return this.mPopup.getBackground();
  }
  
  public int getDropDownHeight()
  {
    return this.mPopup.getHeight();
  }
  
  public int getDropDownHorizontalOffset()
  {
    return this.mPopup.getHorizontalOffset();
  }
  
  public int getDropDownVerticalOffset()
  {
    return this.mPopup.getVerticalOffset();
  }
  
  public int getDropDownWidth()
  {
    return this.mPopup.getWidth();
  }
  
  protected Filter getFilter()
  {
    return this.mFilter;
  }
  
  public int getInputMethodMode()
  {
    return this.mPopup.getInputMethodMode();
  }
  
  @Deprecated
  public AdapterView.OnItemClickListener getItemClickListener()
  {
    return this.mItemClickListener;
  }
  
  @Deprecated
  public AdapterView.OnItemSelectedListener getItemSelectedListener()
  {
    return this.mItemSelectedListener;
  }
  
  public int getListSelection()
  {
    return this.mPopup.getSelectedItemPosition();
  }
  
  public AdapterView.OnItemClickListener getOnItemClickListener()
  {
    return this.mItemClickListener;
  }
  
  public AdapterView.OnItemSelectedListener getOnItemSelectedListener()
  {
    return this.mItemSelectedListener;
  }
  
  public int getThreshold()
  {
    return this.mThreshold;
  }
  
  public Validator getValidator()
  {
    return this.mValidator;
  }
  
  public boolean isDropDownAlwaysVisible()
  {
    return this.mPopup.isDropDownAlwaysVisible();
  }
  
  public boolean isDropDownDismissedOnCompletion()
  {
    return this.mDropDownDismissedOnCompletion;
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28)
  public boolean isInputMethodNotNeeded()
  {
    boolean bool;
    if (this.mPopup.getInputMethodMode() == 2) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isPerformingCompletion()
  {
    return this.mBlockCompletion;
  }
  
  public boolean isPopupShowing()
  {
    return this.mPopup.isShowing();
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
  }
  
  public void onCommitCompletion(CompletionInfo paramCompletionInfo)
  {
    if (isPopupShowing()) {
      this.mPopup.performItemClick(paramCompletionInfo.getPosition());
    }
  }
  
  protected void onDetachedFromWindow()
  {
    dismissDropDown();
    super.onDetachedFromWindow();
  }
  
  protected void onDisplayHint(int paramInt)
  {
    super.onDisplayHint(paramInt);
    if ((paramInt == 4) && (!this.mPopup.isDropDownAlwaysVisible())) {
      dismissDropDown();
    }
  }
  
  public void onFilterComplete(int paramInt)
  {
    updateDropDownForFilter(paramInt);
  }
  
  protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
  {
    super.onFocusChanged(paramBoolean, paramInt, paramRect);
    if (isTemporarilyDetached()) {
      return;
    }
    if (!paramBoolean) {
      performValidation();
    }
    if ((!paramBoolean) && (!this.mPopup.isDropDownAlwaysVisible())) {
      dismissDropDown();
    }
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (this.mPopup.onKeyDown(paramInt, paramKeyEvent)) {
      return true;
    }
    if ((!isPopupShowing()) && (paramInt == 20) && (paramKeyEvent.hasNoModifiers())) {
      performValidation();
    }
    if ((isPopupShowing()) && (paramInt == 61) && (paramKeyEvent.hasNoModifiers())) {
      return true;
    }
    this.mLastKeyCode = paramInt;
    boolean bool = super.onKeyDown(paramInt, paramKeyEvent);
    this.mLastKeyCode = 0;
    if ((bool) && (isPopupShowing())) {
      clearListSelection();
    }
    return bool;
  }
  
  public boolean onKeyPreIme(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramInt == 4) && (isPopupShowing()) && (!this.mPopup.isDropDownAlwaysVisible()))
    {
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
        if (localDispatcherState != null) {
          localDispatcherState.handleUpEvent(paramKeyEvent);
        }
        if ((paramKeyEvent.isTracking()) && (!paramKeyEvent.isCanceled()))
        {
          dismissDropDown();
          return true;
        }
      }
    }
    return super.onKeyPreIme(paramInt, paramKeyEvent);
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((this.mPopup.onKeyUp(paramInt, paramKeyEvent)) && ((paramInt == 23) || (paramInt == 61) || (paramInt == 66)))
    {
      if (paramKeyEvent.hasNoModifiers()) {
        performCompletion();
      }
      return true;
    }
    if ((isPopupShowing()) && (paramInt == 61) && (paramKeyEvent.hasNoModifiers()))
    {
      performCompletion();
      return true;
    }
    return super.onKeyUp(paramInt, paramKeyEvent);
  }
  
  public void onWindowFocusChanged(boolean paramBoolean)
  {
    super.onWindowFocusChanged(paramBoolean);
    if ((!paramBoolean) && (!this.mPopup.isDropDownAlwaysVisible())) {
      dismissDropDown();
    }
  }
  
  public void performCompletion()
  {
    performCompletion(null, -1, -1L);
  }
  
  protected void performFiltering(CharSequence paramCharSequence, int paramInt)
  {
    this.mFilter.filter(paramCharSequence, this);
  }
  
  public void performValidation()
  {
    if (this.mValidator == null) {
      return;
    }
    Editable localEditable = getText();
    if ((!TextUtils.isEmpty(localEditable)) && (!this.mValidator.isValid(localEditable))) {
      setText(this.mValidator.fixText(localEditable));
    }
  }
  
  public final void refreshAutoCompleteResults()
  {
    if (enoughToFilter())
    {
      if (this.mFilter != null)
      {
        this.mPopupCanBeUpdated = true;
        performFiltering(getText(), this.mLastKeyCode);
      }
    }
    else
    {
      if (!this.mPopup.isDropDownAlwaysVisible()) {
        dismissDropDown();
      }
      Filter localFilter = this.mFilter;
      if (localFilter != null) {
        localFilter.filter(null);
      }
    }
  }
  
  protected void replaceText(CharSequence paramCharSequence)
  {
    clearComposingText();
    setText(paramCharSequence);
    paramCharSequence = getText();
    Selection.setSelection(paramCharSequence, paramCharSequence.length());
  }
  
  public <T extends ListAdapter,  extends Filterable> void setAdapter(T paramT)
  {
    Object localObject = this.mObserver;
    if (localObject == null)
    {
      this.mObserver = new PopupDataSetObserver(this, null);
    }
    else
    {
      ListAdapter localListAdapter = this.mAdapter;
      if (localListAdapter != null) {
        localListAdapter.unregisterDataSetObserver((DataSetObserver)localObject);
      }
    }
    this.mAdapter = paramT;
    localObject = this.mAdapter;
    if (localObject != null)
    {
      this.mFilter = ((Filterable)localObject).getFilter();
      paramT.registerDataSetObserver(this.mObserver);
    }
    else
    {
      this.mFilter = null;
    }
    this.mPopup.setAdapter(this.mAdapter);
  }
  
  public void setCompletionHint(CharSequence paramCharSequence)
  {
    this.mHintText = paramCharSequence;
    if (paramCharSequence != null)
    {
      TextView localTextView = this.mHintView;
      if (localTextView == null)
      {
        paramCharSequence = (TextView)LayoutInflater.from(this.mPopupContext).inflate(this.mHintResource, null).findViewById(16908308);
        paramCharSequence.setText(this.mHintText);
        this.mHintView = paramCharSequence;
        this.mPopup.setPromptView(paramCharSequence);
      }
      else
      {
        localTextView.setText(paramCharSequence);
      }
    }
    else
    {
      this.mPopup.setPromptView(null);
      this.mHintView = null;
    }
  }
  
  @UnsupportedAppUsage
  public void setDropDownAlwaysVisible(boolean paramBoolean)
  {
    this.mPopup.setDropDownAlwaysVisible(paramBoolean);
  }
  
  public void setDropDownAnchor(int paramInt)
  {
    this.mDropDownAnchorId = paramInt;
    this.mPopup.setAnchorView(null);
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  public void setDropDownAnimationStyle(int paramInt)
  {
    this.mPopup.setAnimationStyle(paramInt);
  }
  
  public void setDropDownBackgroundDrawable(Drawable paramDrawable)
  {
    this.mPopup.setBackgroundDrawable(paramDrawable);
  }
  
  public void setDropDownBackgroundResource(int paramInt)
  {
    this.mPopup.setBackgroundDrawable(getContext().getDrawable(paramInt));
  }
  
  @UnsupportedAppUsage
  public void setDropDownDismissedOnCompletion(boolean paramBoolean)
  {
    this.mDropDownDismissedOnCompletion = paramBoolean;
  }
  
  public void setDropDownHeight(int paramInt)
  {
    this.mPopup.setHeight(paramInt);
  }
  
  public void setDropDownHorizontalOffset(int paramInt)
  {
    this.mPopup.setHorizontalOffset(paramInt);
  }
  
  public void setDropDownVerticalOffset(int paramInt)
  {
    this.mPopup.setVerticalOffset(paramInt);
  }
  
  public void setDropDownWidth(int paramInt)
  {
    this.mPopup.setWidth(paramInt);
  }
  
  @UnsupportedAppUsage
  public void setForceIgnoreOutsideTouch(boolean paramBoolean)
  {
    this.mPopup.setForceIgnoreOutsideTouch(paramBoolean);
  }
  
  protected boolean setFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool = super.setFrame(paramInt1, paramInt2, paramInt3, paramInt4);
    if (isPopupShowing()) {
      showDropDown();
    }
    return bool;
  }
  
  public void setInputMethodMode(int paramInt)
  {
    this.mPopup.setInputMethodMode(paramInt);
  }
  
  public void setListSelection(int paramInt)
  {
    this.mPopup.setSelection(paramInt);
  }
  
  public void setOnClickListener(View.OnClickListener paramOnClickListener)
  {
    PassThroughClickListener.access$302(this.mPassThroughClickListener, paramOnClickListener);
  }
  
  public void setOnDismissListener(final OnDismissListener paramOnDismissListener)
  {
    PopupWindow.OnDismissListener local1 = null;
    if (paramOnDismissListener != null) {
      local1 = new PopupWindow.OnDismissListener()
      {
        public void onDismiss()
        {
          paramOnDismissListener.onDismiss();
        }
      };
    }
    this.mPopup.setOnDismissListener(local1);
  }
  
  public void setOnItemClickListener(AdapterView.OnItemClickListener paramOnItemClickListener)
  {
    this.mItemClickListener = paramOnItemClickListener;
  }
  
  public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener paramOnItemSelectedListener)
  {
    this.mItemSelectedListener = paramOnItemSelectedListener;
  }
  
  public void setText(CharSequence paramCharSequence, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      setText(paramCharSequence);
    }
    else
    {
      this.mBlockCompletion = true;
      setText(paramCharSequence);
      this.mBlockCompletion = false;
    }
  }
  
  public void setThreshold(int paramInt)
  {
    int i = paramInt;
    if (paramInt <= 0) {
      i = 1;
    }
    this.mThreshold = i;
  }
  
  public void setValidator(Validator paramValidator)
  {
    this.mValidator = paramValidator;
  }
  
  public void showDropDown()
  {
    buildImeCompletions();
    if (this.mPopup.getAnchorView() == null) {
      if (this.mDropDownAnchorId != -1) {
        this.mPopup.setAnchorView(getRootView().findViewById(this.mDropDownAnchorId));
      } else {
        this.mPopup.setAnchorView(this);
      }
    }
    if (!isPopupShowing())
    {
      this.mPopup.setInputMethodMode(1);
      this.mPopup.setListItemExpandMax(3);
    }
    this.mPopup.show();
    this.mPopup.getListView().setOverScrollMode(0);
  }
  
  @UnsupportedAppUsage
  public void showDropDownAfterLayout()
  {
    this.mPopup.postShow();
  }
  
  private class DropDownItemClickListener
    implements AdapterView.OnItemClickListener
  {
    private DropDownItemClickListener() {}
    
    public void onItemClick(AdapterView paramAdapterView, View paramView, int paramInt, long paramLong)
    {
      AutoCompleteTextView.this.performCompletion(paramView, paramInt, paramLong);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface InputMethodMode {}
  
  private class MyWatcher
    implements TextWatcher
  {
    private boolean mOpenBefore;
    
    private MyWatcher() {}
    
    public void afterTextChanged(Editable paramEditable)
    {
      if (AutoCompleteTextView.this.mBlockCompletion) {
        return;
      }
      if ((this.mOpenBefore) && (!AutoCompleteTextView.this.isPopupShowing())) {
        return;
      }
      AutoCompleteTextView.this.refreshAutoCompleteResults();
    }
    
    public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
    {
      if (AutoCompleteTextView.this.mBlockCompletion) {
        return;
      }
      this.mOpenBefore = AutoCompleteTextView.this.isPopupShowing();
    }
    
    public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
  }
  
  public static abstract interface OnDismissListener
  {
    public abstract void onDismiss();
  }
  
  private class PassThroughClickListener
    implements View.OnClickListener
  {
    private View.OnClickListener mWrapped;
    
    private PassThroughClickListener() {}
    
    public void onClick(View paramView)
    {
      AutoCompleteTextView.this.onClickImpl();
      View.OnClickListener localOnClickListener = this.mWrapped;
      if (localOnClickListener != null) {
        localOnClickListener.onClick(paramView);
      }
    }
  }
  
  private static class PopupDataSetObserver
    extends DataSetObserver
  {
    private final WeakReference<AutoCompleteTextView> mViewReference;
    private final Runnable updateRunnable = new Runnable()
    {
      public void run()
      {
        AutoCompleteTextView localAutoCompleteTextView = (AutoCompleteTextView)AutoCompleteTextView.PopupDataSetObserver.this.mViewReference.get();
        if (localAutoCompleteTextView == null) {
          return;
        }
        ListAdapter localListAdapter = localAutoCompleteTextView.mAdapter;
        if (localListAdapter == null) {
          return;
        }
        localAutoCompleteTextView.updateDropDownForFilter(localListAdapter.getCount());
      }
    };
    
    private PopupDataSetObserver(AutoCompleteTextView paramAutoCompleteTextView)
    {
      this.mViewReference = new WeakReference(paramAutoCompleteTextView);
    }
    
    public void onChanged()
    {
      AutoCompleteTextView localAutoCompleteTextView = (AutoCompleteTextView)this.mViewReference.get();
      if ((localAutoCompleteTextView != null) && (localAutoCompleteTextView.mAdapter != null)) {
        localAutoCompleteTextView.post(this.updateRunnable);
      }
    }
  }
  
  public static abstract interface Validator
  {
    public abstract CharSequence fixText(CharSequence paramCharSequence);
    
    public abstract boolean isValid(CharSequence paramCharSequence);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/AutoCompleteTextView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */