package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.app.PendingIntent;
import android.app.SearchableInfo;
import android.app.SearchableInfo.ActionKeyInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.CollapsibleActionView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewConfiguration;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.R.styleable;
import java.util.WeakHashMap;

public class SearchView
  extends LinearLayout
  implements CollapsibleActionView
{
  private static final boolean DBG = false;
  private static final String IME_OPTION_NO_MICROPHONE = "nm";
  private static final String LOG_TAG = "SearchView";
  private Bundle mAppSearchData;
  @UnsupportedAppUsage
  private boolean mClearingFocus;
  @UnsupportedAppUsage
  private final ImageView mCloseButton;
  private final ImageView mCollapsedIcon;
  @UnsupportedAppUsage
  private int mCollapsedImeOptions;
  private final CharSequence mDefaultQueryHint;
  private final View mDropDownAnchor;
  @UnsupportedAppUsage
  private boolean mExpandedInActionView;
  private final ImageView mGoButton;
  @UnsupportedAppUsage
  private boolean mIconified;
  @UnsupportedAppUsage
  private boolean mIconifiedByDefault;
  private int mMaxWidth;
  private CharSequence mOldQueryText;
  @UnsupportedAppUsage
  private final View.OnClickListener mOnClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (paramAnonymousView == SearchView.this.mSearchButton) {
        SearchView.this.onSearchClicked();
      } else if (paramAnonymousView == SearchView.this.mCloseButton) {
        SearchView.this.onCloseClicked();
      } else if (paramAnonymousView == SearchView.this.mGoButton) {
        SearchView.this.onSubmitQuery();
      } else if (paramAnonymousView == SearchView.this.mVoiceButton) {
        SearchView.this.onVoiceClicked();
      } else if (paramAnonymousView == SearchView.this.mSearchSrcTextView) {
        SearchView.this.forceSuggestionQuery();
      }
    }
  };
  private OnCloseListener mOnCloseListener;
  private final TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener()
  {
    public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
    {
      SearchView.this.onSubmitQuery();
      return true;
    }
  };
  @UnsupportedAppUsage
  private final AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener()
  {
    public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      SearchView.this.onItemClicked(paramAnonymousInt, 0, null);
    }
  };
  private final AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener()
  {
    public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      SearchView.this.onItemSelected(paramAnonymousInt);
    }
    
    public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView) {}
  };
  @UnsupportedAppUsage
  private OnQueryTextListener mOnQueryChangeListener;
  private View.OnFocusChangeListener mOnQueryTextFocusChangeListener;
  private View.OnClickListener mOnSearchClickListener;
  private OnSuggestionListener mOnSuggestionListener;
  private final WeakHashMap<String, Drawable.ConstantState> mOutsideDrawablesCache = new WeakHashMap();
  private CharSequence mQueryHint;
  private boolean mQueryRefinement;
  private Runnable mReleaseCursorRunnable = new Runnable()
  {
    public void run()
    {
      if ((SearchView.this.mSuggestionsAdapter != null) && ((SearchView.this.mSuggestionsAdapter instanceof SuggestionsAdapter))) {
        SearchView.this.mSuggestionsAdapter.changeCursor(null);
      }
    }
  };
  @UnsupportedAppUsage
  private final ImageView mSearchButton;
  @UnsupportedAppUsage
  private final View mSearchEditFrame;
  @UnsupportedAppUsage
  private final Drawable mSearchHintIcon;
  @UnsupportedAppUsage
  private final View mSearchPlate;
  @UnsupportedAppUsage
  private final SearchAutoComplete mSearchSrcTextView;
  private Rect mSearchSrcTextViewBounds = new Rect();
  private Rect mSearchSrtTextViewBoundsExpanded = new Rect();
  private SearchableInfo mSearchable;
  @UnsupportedAppUsage
  private final View mSubmitArea;
  private boolean mSubmitButtonEnabled;
  private final int mSuggestionCommitIconResId;
  private final int mSuggestionRowLayout;
  @UnsupportedAppUsage
  private CursorAdapter mSuggestionsAdapter;
  private int[] mTemp = new int[2];
  private int[] mTemp2 = new int[2];
  View.OnKeyListener mTextKeyListener = new View.OnKeyListener()
  {
    public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
    {
      if (SearchView.this.mSearchable == null) {
        return false;
      }
      if ((SearchView.this.mSearchSrcTextView.isPopupShowing()) && (SearchView.this.mSearchSrcTextView.getListSelection() != -1)) {
        return SearchView.this.onSuggestionsKey(paramAnonymousView, paramAnonymousInt, paramAnonymousKeyEvent);
      }
      if ((!SearchView.SearchAutoComplete.access$1700(SearchView.this.mSearchSrcTextView)) && (paramAnonymousKeyEvent.hasNoModifiers()))
      {
        if ((paramAnonymousKeyEvent.getAction() == 1) && (paramAnonymousInt == 66))
        {
          paramAnonymousView.cancelLongPress();
          paramAnonymousView = SearchView.this;
          paramAnonymousView.launchQuerySearch(0, null, paramAnonymousView.mSearchSrcTextView.getText().toString());
          return true;
        }
        if (paramAnonymousKeyEvent.getAction() == 0)
        {
          paramAnonymousView = SearchView.this.mSearchable.findActionKey(paramAnonymousInt);
          if ((paramAnonymousView != null) && (paramAnonymousView.getQueryActionMsg() != null))
          {
            SearchView.this.launchQuerySearch(paramAnonymousInt, paramAnonymousView.getQueryActionMsg(), SearchView.this.mSearchSrcTextView.getText().toString());
            return true;
          }
        }
      }
      return false;
    }
  };
  private TextWatcher mTextWatcher = new TextWatcher()
  {
    public void afterTextChanged(Editable paramAnonymousEditable) {}
    
    public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
    
    public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
    {
      SearchView.this.onTextChanged(paramAnonymousCharSequence);
    }
  };
  private UpdatableTouchDelegate mTouchDelegate;
  private Runnable mUpdateDrawableStateRunnable = new Runnable()
  {
    public void run()
    {
      SearchView.this.updateFocusedState();
    }
  };
  @UnsupportedAppUsage
  private CharSequence mUserQuery;
  private final Intent mVoiceAppSearchIntent;
  @UnsupportedAppUsage
  private final ImageView mVoiceButton;
  @UnsupportedAppUsage
  private boolean mVoiceButtonEnabled;
  private final Intent mVoiceWebSearchIntent;
  
  public SearchView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SearchView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16843904);
  }
  
  public SearchView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public SearchView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SearchView, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.SearchView, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    ((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(localTypedArray.getResourceId(0, 17367302), this, true);
    this.mSearchSrcTextView = ((SearchAutoComplete)findViewById(16909362));
    this.mSearchSrcTextView.setSearchView(this);
    this.mSearchEditFrame = findViewById(16909358);
    this.mSearchPlate = findViewById(16909361);
    this.mSubmitArea = findViewById(16909453);
    this.mSearchButton = ((ImageView)findViewById(16909356));
    this.mGoButton = ((ImageView)findViewById(16909359));
    this.mCloseButton = ((ImageView)findViewById(16909357));
    this.mVoiceButton = ((ImageView)findViewById(16909364));
    this.mCollapsedIcon = ((ImageView)findViewById(16909360));
    this.mSearchPlate.setBackground(localTypedArray.getDrawable(12));
    this.mSubmitArea.setBackground(localTypedArray.getDrawable(13));
    this.mSearchButton.setImageDrawable(localTypedArray.getDrawable(8));
    this.mGoButton.setImageDrawable(localTypedArray.getDrawable(7));
    this.mCloseButton.setImageDrawable(localTypedArray.getDrawable(6));
    this.mVoiceButton.setImageDrawable(localTypedArray.getDrawable(9));
    this.mCollapsedIcon.setImageDrawable(localTypedArray.getDrawable(8));
    if (localTypedArray.hasValueOrEmpty(14)) {
      this.mSearchHintIcon = localTypedArray.getDrawable(14);
    } else {
      this.mSearchHintIcon = localTypedArray.getDrawable(8);
    }
    this.mSuggestionRowLayout = localTypedArray.getResourceId(11, 17367301);
    this.mSuggestionCommitIconResId = localTypedArray.getResourceId(10, 0);
    this.mSearchButton.setOnClickListener(this.mOnClickListener);
    this.mCloseButton.setOnClickListener(this.mOnClickListener);
    this.mGoButton.setOnClickListener(this.mOnClickListener);
    this.mVoiceButton.setOnClickListener(this.mOnClickListener);
    this.mSearchSrcTextView.setOnClickListener(this.mOnClickListener);
    this.mSearchSrcTextView.addTextChangedListener(this.mTextWatcher);
    this.mSearchSrcTextView.setOnEditorActionListener(this.mOnEditorActionListener);
    this.mSearchSrcTextView.setOnItemClickListener(this.mOnItemClickListener);
    this.mSearchSrcTextView.setOnItemSelectedListener(this.mOnItemSelectedListener);
    this.mSearchSrcTextView.setOnKeyListener(this.mTextKeyListener);
    this.mSearchSrcTextView.setOnFocusChangeListener(new View.OnFocusChangeListener()
    {
      public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
      {
        if (SearchView.this.mOnQueryTextFocusChangeListener != null) {
          SearchView.this.mOnQueryTextFocusChangeListener.onFocusChange(SearchView.this, paramAnonymousBoolean);
        }
      }
    });
    setIconifiedByDefault(localTypedArray.getBoolean(4, true));
    paramInt1 = localTypedArray.getDimensionPixelSize(1, -1);
    if (paramInt1 != -1) {
      setMaxWidth(paramInt1);
    }
    this.mDefaultQueryHint = localTypedArray.getText(15);
    this.mQueryHint = localTypedArray.getText(5);
    paramInt1 = localTypedArray.getInt(3, -1);
    if (paramInt1 != -1) {
      setImeOptions(paramInt1);
    }
    paramInt1 = localTypedArray.getInt(2, -1);
    if (paramInt1 != -1) {
      setInputType(paramInt1);
    }
    if (getFocusable() == 16) {
      setFocusable(1);
    }
    localTypedArray.recycle();
    this.mVoiceWebSearchIntent = new Intent("android.speech.action.WEB_SEARCH");
    this.mVoiceWebSearchIntent.addFlags(268435456);
    this.mVoiceWebSearchIntent.putExtra("android.speech.extra.LANGUAGE_MODEL", "web_search");
    this.mVoiceAppSearchIntent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
    this.mVoiceAppSearchIntent.addFlags(268435456);
    this.mDropDownAnchor = findViewById(this.mSearchSrcTextView.getDropDownAnchor());
    paramContext = this.mDropDownAnchor;
    if (paramContext != null) {
      paramContext.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
      {
        public void onLayoutChange(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6, int paramAnonymousInt7, int paramAnonymousInt8)
        {
          SearchView.this.adjustDropDownSizeAndPosition();
        }
      });
    }
    updateViewsVisibility(this.mIconifiedByDefault);
    updateQueryHint();
  }
  
  private void adjustDropDownSizeAndPosition()
  {
    if (this.mDropDownAnchor.getWidth() > 1)
    {
      Resources localResources = getContext().getResources();
      int i = this.mSearchPlate.getPaddingLeft();
      Rect localRect = new Rect();
      boolean bool = isLayoutRtl();
      int j;
      if (this.mIconifiedByDefault) {
        j = localResources.getDimensionPixelSize(17105145) + localResources.getDimensionPixelSize(17105146);
      } else {
        j = 0;
      }
      this.mSearchSrcTextView.getDropDownBackground().getPadding(localRect);
      if (bool) {
        k = -localRect.left;
      } else {
        k = i - (localRect.left + j);
      }
      this.mSearchSrcTextView.setDropDownHorizontalOffset(k);
      int m = this.mDropDownAnchor.getWidth();
      int n = localRect.left;
      int k = localRect.right;
      this.mSearchSrcTextView.setDropDownWidth(m + n + k + j - i);
    }
  }
  
  private Intent createIntent(String paramString1, Uri paramUri, String paramString2, String paramString3, int paramInt, String paramString4)
  {
    paramString1 = new Intent(paramString1);
    paramString1.addFlags(268435456);
    if (paramUri != null) {
      paramString1.setData(paramUri);
    }
    paramString1.putExtra("user_query", this.mUserQuery);
    if (paramString3 != null) {
      paramString1.putExtra("query", paramString3);
    }
    if (paramString2 != null) {
      paramString1.putExtra("intent_extra_data_key", paramString2);
    }
    paramUri = this.mAppSearchData;
    if (paramUri != null) {
      paramString1.putExtra("app_data", paramUri);
    }
    if (paramInt != 0)
    {
      paramString1.putExtra("action_key", paramInt);
      paramString1.putExtra("action_msg", paramString4);
    }
    paramString1.setComponent(this.mSearchable.getSearchActivity());
    return paramString1;
  }
  
  private Intent createIntentFromSuggestion(Cursor paramCursor, int paramInt, String paramString)
  {
    try
    {
      Object localObject1 = SuggestionsAdapter.getColumnString(paramCursor, "suggest_intent_action");
      Object localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = this.mSearchable.getSuggestIntentAction();
      }
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = "android.intent.action.SEARCH";
      }
      String str = SuggestionsAdapter.getColumnString(paramCursor, "suggest_intent_data");
      localObject2 = str;
      if (str == null) {
        localObject2 = this.mSearchable.getSuggestIntentData();
      }
      if (localObject2 != null)
      {
        str = SuggestionsAdapter.getColumnString(paramCursor, "suggest_intent_data_id");
        if (str != null)
        {
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localStringBuilder.append((String)localObject2);
          localStringBuilder.append("/");
          localStringBuilder.append(Uri.encode(str));
          localObject2 = localStringBuilder.toString();
        }
      }
      if (localObject2 == null) {
        localObject2 = null;
      } else {
        localObject2 = Uri.parse((String)localObject2);
      }
      str = SuggestionsAdapter.getColumnString(paramCursor, "suggest_intent_query");
      paramString = createIntent((String)localObject1, (Uri)localObject2, SuggestionsAdapter.getColumnString(paramCursor, "suggest_intent_extra_data"), str, paramInt, paramString);
      return paramString;
    }
    catch (RuntimeException paramString)
    {
      try
      {
        paramInt = paramCursor.getPosition();
      }
      catch (RuntimeException paramCursor)
      {
        paramInt = -1;
      }
      paramCursor = new StringBuilder();
      paramCursor.append("Search suggestions cursor at row ");
      paramCursor.append(paramInt);
      paramCursor.append(" returned exception.");
      Log.w("SearchView", paramCursor.toString(), paramString);
    }
    return null;
  }
  
  private Intent createVoiceAppSearchIntent(Intent paramIntent, SearchableInfo paramSearchableInfo)
  {
    ComponentName localComponentName = paramSearchableInfo.getSearchActivity();
    Object localObject = new Intent("android.intent.action.SEARCH");
    ((Intent)localObject).setComponent(localComponentName);
    PendingIntent localPendingIntent = PendingIntent.getActivity(getContext(), 0, (Intent)localObject, 1073741824);
    Bundle localBundle = new Bundle();
    localObject = this.mAppSearchData;
    if (localObject != null) {
      localBundle.putParcelable("app_data", (Parcelable)localObject);
    }
    Intent localIntent = new Intent(paramIntent);
    paramIntent = "free_form";
    localObject = null;
    String str = null;
    int i = 1;
    Resources localResources = getResources();
    if (paramSearchableInfo.getVoiceLanguageModeId() != 0) {
      paramIntent = localResources.getString(paramSearchableInfo.getVoiceLanguageModeId());
    }
    if (paramSearchableInfo.getVoicePromptTextId() != 0) {
      localObject = localResources.getString(paramSearchableInfo.getVoicePromptTextId());
    }
    if (paramSearchableInfo.getVoiceLanguageId() != 0) {
      str = localResources.getString(paramSearchableInfo.getVoiceLanguageId());
    }
    if (paramSearchableInfo.getVoiceMaxResults() != 0) {
      i = paramSearchableInfo.getVoiceMaxResults();
    }
    localIntent.putExtra("android.speech.extra.LANGUAGE_MODEL", paramIntent);
    localIntent.putExtra("android.speech.extra.PROMPT", (String)localObject);
    localIntent.putExtra("android.speech.extra.LANGUAGE", str);
    localIntent.putExtra("android.speech.extra.MAX_RESULTS", i);
    if (localComponentName == null) {
      paramIntent = null;
    } else {
      paramIntent = localComponentName.flattenToShortString();
    }
    localIntent.putExtra("calling_package", paramIntent);
    localIntent.putExtra("android.speech.extra.RESULTS_PENDINGINTENT", localPendingIntent);
    localIntent.putExtra("android.speech.extra.RESULTS_PENDINGINTENT_BUNDLE", localBundle);
    return localIntent;
  }
  
  private Intent createVoiceWebSearchIntent(Intent paramIntent, SearchableInfo paramSearchableInfo)
  {
    Intent localIntent = new Intent(paramIntent);
    paramIntent = paramSearchableInfo.getSearchActivity();
    if (paramIntent == null) {
      paramIntent = null;
    } else {
      paramIntent = paramIntent.flattenToShortString();
    }
    localIntent.putExtra("calling_package", paramIntent);
    return localIntent;
  }
  
  private void dismissSuggestions()
  {
    this.mSearchSrcTextView.dismissDropDown();
  }
  
  private void forceSuggestionQuery()
  {
    this.mSearchSrcTextView.doBeforeTextChanged();
    this.mSearchSrcTextView.doAfterTextChanged();
  }
  
  private static String getActionKeyMessage(Cursor paramCursor, SearchableInfo.ActionKeyInfo paramActionKeyInfo)
  {
    String str1 = null;
    String str2 = paramActionKeyInfo.getSuggestActionMsgColumn();
    if (str2 != null) {
      str1 = SuggestionsAdapter.getColumnString(paramCursor, str2);
    }
    paramCursor = str1;
    if (str1 == null) {
      paramCursor = paramActionKeyInfo.getSuggestActionMsg();
    }
    return paramCursor;
  }
  
  private void getChildBoundsWithinSearchView(View paramView, Rect paramRect)
  {
    paramView.getLocationInWindow(this.mTemp);
    getLocationInWindow(this.mTemp2);
    int[] arrayOfInt1 = this.mTemp;
    int i = arrayOfInt1[1];
    int[] arrayOfInt2 = this.mTemp2;
    i -= arrayOfInt2[1];
    int j = arrayOfInt1[0] - arrayOfInt2[0];
    paramRect.set(j, i, paramView.getWidth() + j, paramView.getHeight() + i);
  }
  
  private CharSequence getDecoratedHint(CharSequence paramCharSequence)
  {
    if ((this.mIconifiedByDefault) && (this.mSearchHintIcon != null))
    {
      int i = (int)(this.mSearchSrcTextView.getTextSize() * 1.25D);
      this.mSearchHintIcon.setBounds(0, 0, i, i);
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder("   ");
      localSpannableStringBuilder.setSpan(new ImageSpan(this.mSearchHintIcon), 1, 2, 33);
      localSpannableStringBuilder.append(paramCharSequence);
      return localSpannableStringBuilder;
    }
    return paramCharSequence;
  }
  
  private int getPreferredHeight()
  {
    return getContext().getResources().getDimensionPixelSize(17105452);
  }
  
  private int getPreferredWidth()
  {
    return getContext().getResources().getDimensionPixelSize(17105453);
  }
  
  private boolean hasVoiceSearch()
  {
    Object localObject = this.mSearchable;
    boolean bool = false;
    if ((localObject != null) && (((SearchableInfo)localObject).getVoiceSearchEnabled()))
    {
      localObject = null;
      if (this.mSearchable.getVoiceSearchLaunchWebSearch()) {
        localObject = this.mVoiceWebSearchIntent;
      } else if (this.mSearchable.getVoiceSearchLaunchRecognizer()) {
        localObject = this.mVoiceAppSearchIntent;
      }
      if (localObject != null)
      {
        if (getContext().getPackageManager().resolveActivity((Intent)localObject, 65536) != null) {
          bool = true;
        }
        return bool;
      }
    }
    return false;
  }
  
  static boolean isLandscapeMode(Context paramContext)
  {
    boolean bool;
    if (paramContext.getResources().getConfiguration().orientation == 2) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private boolean isSubmitAreaEnabled()
  {
    boolean bool;
    if (((this.mSubmitButtonEnabled) || (this.mVoiceButtonEnabled)) && (!isIconified())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private void launchIntent(Intent paramIntent)
  {
    if (paramIntent == null) {
      return;
    }
    try
    {
      getContext().startActivity(paramIntent);
    }
    catch (RuntimeException localRuntimeException)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Failed launch activity: ");
      localStringBuilder.append(paramIntent);
      Log.e("SearchView", localStringBuilder.toString(), localRuntimeException);
    }
  }
  
  private void launchQuerySearch(int paramInt, String paramString1, String paramString2)
  {
    paramString1 = createIntent("android.intent.action.SEARCH", null, null, paramString2, paramInt, paramString1);
    getContext().startActivity(paramString1);
  }
  
  private boolean launchSuggestion(int paramInt1, int paramInt2, String paramString)
  {
    Cursor localCursor = this.mSuggestionsAdapter.getCursor();
    if ((localCursor != null) && (localCursor.moveToPosition(paramInt1)))
    {
      launchIntent(createIntentFromSuggestion(localCursor, paramInt2, paramString));
      return true;
    }
    return false;
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private void onCloseClicked()
  {
    if (TextUtils.isEmpty(this.mSearchSrcTextView.getText()))
    {
      if (this.mIconifiedByDefault)
      {
        OnCloseListener localOnCloseListener = this.mOnCloseListener;
        if ((localOnCloseListener == null) || (!localOnCloseListener.onClose()))
        {
          clearFocus();
          updateViewsVisibility(true);
        }
      }
    }
    else
    {
      this.mSearchSrcTextView.setText("");
      this.mSearchSrcTextView.requestFocus();
      this.mSearchSrcTextView.setImeVisibility(true);
    }
  }
  
  private boolean onItemClicked(int paramInt1, int paramInt2, String paramString)
  {
    paramString = this.mOnSuggestionListener;
    if ((paramString != null) && (paramString.onSuggestionClick(paramInt1))) {
      return false;
    }
    launchSuggestion(paramInt1, 0, null);
    this.mSearchSrcTextView.setImeVisibility(false);
    dismissSuggestions();
    return true;
  }
  
  private boolean onItemSelected(int paramInt)
  {
    OnSuggestionListener localOnSuggestionListener = this.mOnSuggestionListener;
    if ((localOnSuggestionListener != null) && (localOnSuggestionListener.onSuggestionSelect(paramInt))) {
      return false;
    }
    rewriteQueryFromSuggestion(paramInt);
    return true;
  }
  
  private void onSearchClicked()
  {
    updateViewsVisibility(false);
    this.mSearchSrcTextView.requestFocus();
    this.mSearchSrcTextView.setImeVisibility(true);
    View.OnClickListener localOnClickListener = this.mOnSearchClickListener;
    if (localOnClickListener != null) {
      localOnClickListener.onClick(this);
    }
  }
  
  private void onSubmitQuery()
  {
    Editable localEditable = this.mSearchSrcTextView.getText();
    if ((localEditable != null) && (TextUtils.getTrimmedLength(localEditable) > 0))
    {
      OnQueryTextListener localOnQueryTextListener = this.mOnQueryChangeListener;
      if ((localOnQueryTextListener == null) || (!localOnQueryTextListener.onQueryTextSubmit(localEditable.toString())))
      {
        if (this.mSearchable != null) {
          launchQuerySearch(0, null, localEditable.toString());
        }
        this.mSearchSrcTextView.setImeVisibility(false);
        dismissSuggestions();
      }
    }
  }
  
  private boolean onSuggestionsKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    if (this.mSearchable == null) {
      return false;
    }
    if (this.mSuggestionsAdapter == null) {
      return false;
    }
    if ((paramKeyEvent.getAction() == 0) && (paramKeyEvent.hasNoModifiers())) {
      if ((paramInt != 66) && (paramInt != 84) && (paramInt != 61))
      {
        if ((paramInt != 21) && (paramInt != 22))
        {
          if ((paramInt == 19) && (this.mSearchSrcTextView.getListSelection() == 0)) {
            return false;
          }
          paramView = this.mSearchable.findActionKey(paramInt);
          if ((paramView != null) && ((paramView.getSuggestActionMsg() != null) || (paramView.getSuggestActionMsgColumn() != null)))
          {
            int i = this.mSearchSrcTextView.getListSelection();
            if (i != -1)
            {
              paramKeyEvent = this.mSuggestionsAdapter.getCursor();
              if (paramKeyEvent.moveToPosition(i))
              {
                paramView = getActionKeyMessage(paramKeyEvent, paramView);
                if ((paramView != null) && (paramView.length() > 0)) {
                  return onItemClicked(i, paramInt, paramView);
                }
              }
            }
          }
        }
        else
        {
          if (paramInt == 21) {
            paramInt = 0;
          } else {
            paramInt = this.mSearchSrcTextView.length();
          }
          this.mSearchSrcTextView.setSelection(paramInt);
          this.mSearchSrcTextView.setListSelection(0);
          this.mSearchSrcTextView.clearListSelection();
          this.mSearchSrcTextView.ensureImeVisible(true);
          return true;
        }
      }
      else {
        return onItemClicked(this.mSearchSrcTextView.getListSelection(), 0, null);
      }
    }
    return false;
  }
  
  private void onTextChanged(CharSequence paramCharSequence)
  {
    Editable localEditable = this.mSearchSrcTextView.getText();
    this.mUserQuery = localEditable;
    boolean bool1 = TextUtils.isEmpty(localEditable);
    boolean bool2 = true;
    bool1 ^= true;
    updateSubmitButton(bool1);
    if (bool1) {
      bool2 = false;
    }
    updateVoiceButton(bool2);
    updateCloseButton();
    updateSubmitArea();
    if ((this.mOnQueryChangeListener != null) && (!TextUtils.equals(paramCharSequence, this.mOldQueryText))) {
      this.mOnQueryChangeListener.onQueryTextChange(paramCharSequence.toString());
    }
    this.mOldQueryText = paramCharSequence.toString();
  }
  
  private void onVoiceClicked()
  {
    if (this.mSearchable == null) {
      return;
    }
    Object localObject = this.mSearchable;
    try
    {
      if (((SearchableInfo)localObject).getVoiceSearchLaunchWebSearch())
      {
        localObject = createVoiceWebSearchIntent(this.mVoiceWebSearchIntent, (SearchableInfo)localObject);
        getContext().startActivity((Intent)localObject);
      }
      else if (((SearchableInfo)localObject).getVoiceSearchLaunchRecognizer())
      {
        localObject = createVoiceAppSearchIntent(this.mVoiceAppSearchIntent, (SearchableInfo)localObject);
        getContext().startActivity((Intent)localObject);
      }
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.w("SearchView", "Could not find voice search activity");
    }
  }
  
  private void postUpdateFocusedState()
  {
    post(this.mUpdateDrawableStateRunnable);
  }
  
  private void rewriteQueryFromSuggestion(int paramInt)
  {
    Editable localEditable = this.mSearchSrcTextView.getText();
    Object localObject = this.mSuggestionsAdapter.getCursor();
    if (localObject == null) {
      return;
    }
    if (((Cursor)localObject).moveToPosition(paramInt))
    {
      localObject = this.mSuggestionsAdapter.convertToString((Cursor)localObject);
      if (localObject != null) {
        setQuery((CharSequence)localObject);
      } else {
        setQuery(localEditable);
      }
    }
    else
    {
      setQuery(localEditable);
    }
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private void setQuery(CharSequence paramCharSequence)
  {
    this.mSearchSrcTextView.setText(paramCharSequence, true);
    SearchAutoComplete localSearchAutoComplete = this.mSearchSrcTextView;
    int i;
    if (TextUtils.isEmpty(paramCharSequence)) {
      i = 0;
    } else {
      i = paramCharSequence.length();
    }
    localSearchAutoComplete.setSelection(i);
  }
  
  private void updateCloseButton()
  {
    boolean bool = TextUtils.isEmpty(this.mSearchSrcTextView.getText());
    int i = 1;
    int j = bool ^ true;
    int k = 0;
    int m = i;
    if (j == 0) {
      if ((this.mIconifiedByDefault) && (!this.mExpandedInActionView)) {
        m = i;
      } else {
        m = 0;
      }
    }
    Object localObject = this.mCloseButton;
    if (m != 0) {
      m = k;
    } else {
      m = 8;
    }
    ((ImageView)localObject).setVisibility(m);
    Drawable localDrawable = this.mCloseButton.getDrawable();
    if (localDrawable != null)
    {
      if (j != 0) {
        localObject = ENABLED_STATE_SET;
      } else {
        localObject = EMPTY_STATE_SET;
      }
      localDrawable.setState((int[])localObject);
    }
  }
  
  private void updateFocusedState()
  {
    int[] arrayOfInt;
    if (this.mSearchSrcTextView.hasFocus()) {
      arrayOfInt = FOCUSED_STATE_SET;
    } else {
      arrayOfInt = EMPTY_STATE_SET;
    }
    Drawable localDrawable = this.mSearchPlate.getBackground();
    if (localDrawable != null) {
      localDrawable.setState(arrayOfInt);
    }
    localDrawable = this.mSubmitArea.getBackground();
    if (localDrawable != null) {
      localDrawable.setState(arrayOfInt);
    }
    invalidate();
  }
  
  private void updateQueryHint()
  {
    Object localObject = getQueryHint();
    SearchAutoComplete localSearchAutoComplete = this.mSearchSrcTextView;
    if (localObject == null) {
      localObject = "";
    }
    localSearchAutoComplete.setHint(getDecoratedHint((CharSequence)localObject));
  }
  
  private void updateSearchAutoComplete()
  {
    this.mSearchSrcTextView.setDropDownAnimationStyle(0);
    this.mSearchSrcTextView.setThreshold(this.mSearchable.getSuggestThreshold());
    this.mSearchSrcTextView.setImeOptions(this.mSearchable.getImeOptions());
    int i = this.mSearchable.getInputType();
    int j = 1;
    int k = i;
    if ((i & 0xF) == 1)
    {
      i &= 0xFFFEFFFF;
      k = i;
      if (this.mSearchable.getSuggestAuthority() != null) {
        k = i | 0x10000 | 0x80000;
      }
    }
    this.mSearchSrcTextView.setInputType(k);
    Object localObject = this.mSuggestionsAdapter;
    if (localObject != null) {
      ((CursorAdapter)localObject).changeCursor(null);
    }
    if (this.mSearchable.getSuggestAuthority() != null)
    {
      this.mSuggestionsAdapter = new SuggestionsAdapter(getContext(), this, this.mSearchable, this.mOutsideDrawablesCache);
      this.mSearchSrcTextView.setAdapter(this.mSuggestionsAdapter);
      localObject = (SuggestionsAdapter)this.mSuggestionsAdapter;
      if (this.mQueryRefinement) {
        k = 2;
      } else {
        k = j;
      }
      ((SuggestionsAdapter)localObject).setQueryRefinement(k);
    }
  }
  
  @UnsupportedAppUsage
  private void updateSubmitArea()
  {
    int i = 8;
    int j = i;
    if (isSubmitAreaEnabled()) {
      if (this.mGoButton.getVisibility() != 0)
      {
        j = i;
        if (this.mVoiceButton.getVisibility() != 0) {}
      }
      else
      {
        j = 0;
      }
    }
    this.mSubmitArea.setVisibility(j);
  }
  
  @UnsupportedAppUsage
  private void updateSubmitButton(boolean paramBoolean)
  {
    int i = 8;
    int j = i;
    if (this.mSubmitButtonEnabled)
    {
      j = i;
      if (isSubmitAreaEnabled())
      {
        j = i;
        if (hasFocus()) {
          if (!paramBoolean)
          {
            j = i;
            if (this.mVoiceButtonEnabled) {}
          }
          else
          {
            j = 0;
          }
        }
      }
    }
    this.mGoButton.setVisibility(j);
  }
  
  @UnsupportedAppUsage
  private void updateViewsVisibility(boolean paramBoolean)
  {
    this.mIconified = paramBoolean;
    int i = 8;
    boolean bool1 = false;
    int j;
    if (paramBoolean) {
      j = 0;
    } else {
      j = 8;
    }
    boolean bool2 = TextUtils.isEmpty(this.mSearchSrcTextView.getText()) ^ true;
    this.mSearchButton.setVisibility(j);
    updateSubmitButton(bool2);
    View localView = this.mSearchEditFrame;
    if (paramBoolean) {
      j = i;
    } else {
      j = 0;
    }
    localView.setVisibility(j);
    if ((this.mCollapsedIcon.getDrawable() != null) && (!this.mIconifiedByDefault)) {
      j = 0;
    } else {
      j = 8;
    }
    this.mCollapsedIcon.setVisibility(j);
    updateCloseButton();
    paramBoolean = bool1;
    if (!bool2) {
      paramBoolean = true;
    }
    updateVoiceButton(paramBoolean);
    updateSubmitArea();
  }
  
  private void updateVoiceButton(boolean paramBoolean)
  {
    int i = 8;
    int j = i;
    if (this.mVoiceButtonEnabled)
    {
      j = i;
      if (!isIconified())
      {
        j = i;
        if (paramBoolean)
        {
          j = 0;
          this.mGoButton.setVisibility(8);
        }
      }
    }
    this.mVoiceButton.setVisibility(j);
  }
  
  public void clearFocus()
  {
    this.mClearingFocus = true;
    super.clearFocus();
    this.mSearchSrcTextView.clearFocus();
    this.mSearchSrcTextView.setImeVisibility(false);
    this.mClearingFocus = false;
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return SearchView.class.getName();
  }
  
  public int getImeOptions()
  {
    return this.mSearchSrcTextView.getImeOptions();
  }
  
  public int getInputType()
  {
    return this.mSearchSrcTextView.getInputType();
  }
  
  public int getMaxWidth()
  {
    return this.mMaxWidth;
  }
  
  public CharSequence getQuery()
  {
    return this.mSearchSrcTextView.getText();
  }
  
  public CharSequence getQueryHint()
  {
    Object localObject;
    if (this.mQueryHint != null)
    {
      localObject = this.mQueryHint;
    }
    else
    {
      localObject = this.mSearchable;
      if ((localObject != null) && (((SearchableInfo)localObject).getHintId() != 0)) {
        localObject = getContext().getText(this.mSearchable.getHintId());
      } else {
        localObject = this.mDefaultQueryHint;
      }
    }
    return (CharSequence)localObject;
  }
  
  int getSuggestionCommitIconResId()
  {
    return this.mSuggestionCommitIconResId;
  }
  
  int getSuggestionRowLayout()
  {
    return this.mSuggestionRowLayout;
  }
  
  public CursorAdapter getSuggestionsAdapter()
  {
    return this.mSuggestionsAdapter;
  }
  
  @Deprecated
  public boolean isIconfiedByDefault()
  {
    return this.mIconifiedByDefault;
  }
  
  public boolean isIconified()
  {
    return this.mIconified;
  }
  
  public boolean isIconifiedByDefault()
  {
    return this.mIconifiedByDefault;
  }
  
  public boolean isQueryRefinementEnabled()
  {
    return this.mQueryRefinement;
  }
  
  public boolean isSubmitButtonEnabled()
  {
    return this.mSubmitButtonEnabled;
  }
  
  public void onActionViewCollapsed()
  {
    setQuery("", false);
    clearFocus();
    updateViewsVisibility(true);
    this.mSearchSrcTextView.setImeOptions(this.mCollapsedImeOptions);
    this.mExpandedInActionView = false;
  }
  
  public void onActionViewExpanded()
  {
    if (this.mExpandedInActionView) {
      return;
    }
    this.mExpandedInActionView = true;
    this.mCollapsedImeOptions = this.mSearchSrcTextView.getImeOptions();
    this.mSearchSrcTextView.setImeOptions(this.mCollapsedImeOptions | 0x2000000);
    this.mSearchSrcTextView.setText("");
    setIconified(false);
  }
  
  protected void onDetachedFromWindow()
  {
    removeCallbacks(this.mUpdateDrawableStateRunnable);
    post(this.mReleaseCursorRunnable);
    super.onDetachedFromWindow();
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    Object localObject = this.mSearchable;
    if (localObject == null) {
      return false;
    }
    localObject = ((SearchableInfo)localObject).findActionKey(paramInt);
    if ((localObject != null) && (((SearchableInfo.ActionKeyInfo)localObject).getQueryActionMsg() != null))
    {
      launchQuerySearch(paramInt, ((SearchableInfo.ActionKeyInfo)localObject).getQueryActionMsg(), this.mSearchSrcTextView.getText().toString());
      return true;
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramBoolean)
    {
      getChildBoundsWithinSearchView(this.mSearchSrcTextView, this.mSearchSrcTextViewBounds);
      this.mSearchSrtTextViewBoundsExpanded.set(this.mSearchSrcTextViewBounds.left, 0, this.mSearchSrcTextViewBounds.right, paramInt4 - paramInt2);
      UpdatableTouchDelegate localUpdatableTouchDelegate = this.mTouchDelegate;
      if (localUpdatableTouchDelegate == null)
      {
        this.mTouchDelegate = new UpdatableTouchDelegate(this.mSearchSrtTextViewBoundsExpanded, this.mSearchSrcTextViewBounds, this.mSearchSrcTextView);
        setTouchDelegate(this.mTouchDelegate);
      }
      else
      {
        localUpdatableTouchDelegate.setBounds(this.mSearchSrtTextViewBoundsExpanded, this.mSearchSrcTextViewBounds);
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (isIconified())
    {
      super.onMeasure(paramInt1, paramInt2);
      return;
    }
    int i = View.MeasureSpec.getMode(paramInt1);
    int j = View.MeasureSpec.getSize(paramInt1);
    if (i != Integer.MIN_VALUE)
    {
      if (i != 0)
      {
        if (i != 1073741824)
        {
          paramInt1 = j;
        }
        else
        {
          i = this.mMaxWidth;
          paramInt1 = j;
          if (i > 0) {
            paramInt1 = Math.min(i, j);
          }
        }
      }
      else
      {
        paramInt1 = this.mMaxWidth;
        if (paramInt1 <= 0) {
          paramInt1 = getPreferredWidth();
        }
      }
    }
    else
    {
      paramInt1 = this.mMaxWidth;
      if (paramInt1 > 0) {
        paramInt1 = Math.min(paramInt1, j);
      } else {
        paramInt1 = Math.min(getPreferredWidth(), j);
      }
    }
    j = View.MeasureSpec.getMode(paramInt2);
    paramInt2 = View.MeasureSpec.getSize(paramInt2);
    if (j != Integer.MIN_VALUE)
    {
      if (j == 0) {
        paramInt2 = getPreferredHeight();
      }
    }
    else {
      paramInt2 = Math.min(getPreferredHeight(), paramInt2);
    }
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824));
  }
  
  void onQueryRefine(CharSequence paramCharSequence)
  {
    setQuery(paramCharSequence);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    updateViewsVisibility(paramParcelable.isIconified);
    requestLayout();
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.isIconified = isIconified();
    return localSavedState;
  }
  
  void onTextFocusChanged()
  {
    updateViewsVisibility(isIconified());
    postUpdateFocusedState();
    if (this.mSearchSrcTextView.hasFocus()) {
      forceSuggestionQuery();
    }
  }
  
  public void onWindowFocusChanged(boolean paramBoolean)
  {
    super.onWindowFocusChanged(paramBoolean);
    postUpdateFocusedState();
  }
  
  public boolean requestFocus(int paramInt, Rect paramRect)
  {
    if (this.mClearingFocus) {
      return false;
    }
    if (!isFocusable()) {
      return false;
    }
    if (!isIconified())
    {
      boolean bool = this.mSearchSrcTextView.requestFocus(paramInt, paramRect);
      if (bool) {
        updateViewsVisibility(false);
      }
      return bool;
    }
    return super.requestFocus(paramInt, paramRect);
  }
  
  public void setAppSearchData(Bundle paramBundle)
  {
    this.mAppSearchData = paramBundle;
  }
  
  public void setIconified(boolean paramBoolean)
  {
    if (paramBoolean) {
      onCloseClicked();
    } else {
      onSearchClicked();
    }
  }
  
  public void setIconifiedByDefault(boolean paramBoolean)
  {
    if (this.mIconifiedByDefault == paramBoolean) {
      return;
    }
    this.mIconifiedByDefault = paramBoolean;
    updateViewsVisibility(paramBoolean);
    updateQueryHint();
  }
  
  public void setImeOptions(int paramInt)
  {
    this.mSearchSrcTextView.setImeOptions(paramInt);
  }
  
  public void setInputType(int paramInt)
  {
    this.mSearchSrcTextView.setInputType(paramInt);
  }
  
  public void setMaxWidth(int paramInt)
  {
    this.mMaxWidth = paramInt;
    requestLayout();
  }
  
  public void setOnCloseListener(OnCloseListener paramOnCloseListener)
  {
    this.mOnCloseListener = paramOnCloseListener;
  }
  
  public void setOnQueryTextFocusChangeListener(View.OnFocusChangeListener paramOnFocusChangeListener)
  {
    this.mOnQueryTextFocusChangeListener = paramOnFocusChangeListener;
  }
  
  public void setOnQueryTextListener(OnQueryTextListener paramOnQueryTextListener)
  {
    this.mOnQueryChangeListener = paramOnQueryTextListener;
  }
  
  public void setOnSearchClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mOnSearchClickListener = paramOnClickListener;
  }
  
  public void setOnSuggestionListener(OnSuggestionListener paramOnSuggestionListener)
  {
    this.mOnSuggestionListener = paramOnSuggestionListener;
  }
  
  public void setQuery(CharSequence paramCharSequence, boolean paramBoolean)
  {
    this.mSearchSrcTextView.setText(paramCharSequence);
    if (paramCharSequence != null)
    {
      SearchAutoComplete localSearchAutoComplete = this.mSearchSrcTextView;
      localSearchAutoComplete.setSelection(localSearchAutoComplete.length());
      this.mUserQuery = paramCharSequence;
    }
    if ((paramBoolean) && (!TextUtils.isEmpty(paramCharSequence))) {
      onSubmitQuery();
    }
  }
  
  public void setQueryHint(CharSequence paramCharSequence)
  {
    this.mQueryHint = paramCharSequence;
    updateQueryHint();
  }
  
  public void setQueryRefinementEnabled(boolean paramBoolean)
  {
    this.mQueryRefinement = paramBoolean;
    Object localObject = this.mSuggestionsAdapter;
    if ((localObject instanceof SuggestionsAdapter))
    {
      localObject = (SuggestionsAdapter)localObject;
      int i;
      if (paramBoolean) {
        i = 2;
      } else {
        i = 1;
      }
      ((SuggestionsAdapter)localObject).setQueryRefinement(i);
    }
  }
  
  public void setSearchableInfo(SearchableInfo paramSearchableInfo)
  {
    this.mSearchable = paramSearchableInfo;
    if (this.mSearchable != null)
    {
      updateSearchAutoComplete();
      updateQueryHint();
    }
    this.mVoiceButtonEnabled = hasVoiceSearch();
    if (this.mVoiceButtonEnabled) {
      this.mSearchSrcTextView.setPrivateImeOptions("nm");
    }
    updateViewsVisibility(isIconified());
  }
  
  public void setSubmitButtonEnabled(boolean paramBoolean)
  {
    this.mSubmitButtonEnabled = paramBoolean;
    updateViewsVisibility(isIconified());
  }
  
  public void setSuggestionsAdapter(CursorAdapter paramCursorAdapter)
  {
    this.mSuggestionsAdapter = paramCursorAdapter;
    this.mSearchSrcTextView.setAdapter(this.mSuggestionsAdapter);
  }
  
  public static abstract interface OnCloseListener
  {
    public abstract boolean onClose();
  }
  
  public static abstract interface OnQueryTextListener
  {
    public abstract boolean onQueryTextChange(String paramString);
    
    public abstract boolean onQueryTextSubmit(String paramString);
  }
  
  public static abstract interface OnSuggestionListener
  {
    public abstract boolean onSuggestionClick(int paramInt);
    
    public abstract boolean onSuggestionSelect(int paramInt);
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public SearchView.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new SearchView.SavedState(paramAnonymousParcel);
      }
      
      public SearchView.SavedState[] newArray(int paramAnonymousInt)
      {
        return new SearchView.SavedState[paramAnonymousInt];
      }
    };
    boolean isIconified;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.isIconified = ((Boolean)paramParcel.readValue(null)).booleanValue();
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("SearchView.SavedState{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(" isIconified=");
      localStringBuilder.append(this.isIconified);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeValue(Boolean.valueOf(this.isIconified));
    }
  }
  
  public static class SearchAutoComplete
    extends AutoCompleteTextView
  {
    private boolean mHasPendingShowSoftInputRequest;
    final Runnable mRunShowSoftInputIfNecessary = new _..Lambda.SearchView.SearchAutoComplete.qdPU54FiW6QTzCbsg7P4cSs3cJ8(this);
    private SearchView mSearchView;
    private int mThreshold = getThreshold();
    
    public SearchAutoComplete(Context paramContext)
    {
      super();
    }
    
    @UnsupportedAppUsage
    public SearchAutoComplete(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public SearchAutoComplete(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
      super(paramAttributeSet, paramInt);
    }
    
    public SearchAutoComplete(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
    {
      super(paramAttributeSet, paramInt1, paramInt2);
    }
    
    private int getSearchViewTextMinWidthDp()
    {
      Configuration localConfiguration = getResources().getConfiguration();
      int i = localConfiguration.screenWidthDp;
      int j = localConfiguration.screenHeightDp;
      int k = localConfiguration.orientation;
      if ((i >= 960) && (j >= 720) && (k == 2)) {
        return 256;
      }
      if ((i < 600) && ((i < 640) || (j < 480))) {
        return 160;
      }
      return 192;
    }
    
    private boolean isEmpty()
    {
      boolean bool;
      if (TextUtils.getTrimmedLength(getText()) == 0) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    private void setImeVisibility(boolean paramBoolean)
    {
      InputMethodManager localInputMethodManager = (InputMethodManager)getContext().getSystemService(InputMethodManager.class);
      if (!paramBoolean)
      {
        this.mHasPendingShowSoftInputRequest = false;
        removeCallbacks(this.mRunShowSoftInputIfNecessary);
        localInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
        return;
      }
      if (localInputMethodManager.isActive(this))
      {
        this.mHasPendingShowSoftInputRequest = false;
        removeCallbacks(this.mRunShowSoftInputIfNecessary);
        localInputMethodManager.showSoftInput(this, 0);
        return;
      }
      this.mHasPendingShowSoftInputRequest = true;
    }
    
    private void showSoftInputIfNecessary()
    {
      if (this.mHasPendingShowSoftInputRequest)
      {
        ((InputMethodManager)getContext().getSystemService(InputMethodManager.class)).showSoftInput(this, 0);
        this.mHasPendingShowSoftInputRequest = false;
      }
    }
    
    public boolean checkInputConnectionProxy(View paramView)
    {
      boolean bool;
      if (paramView == this.mSearchView) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public boolean enoughToFilter()
    {
      boolean bool;
      if ((this.mThreshold > 0) && (!super.enoughToFilter())) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    public InputConnection onCreateInputConnection(EditorInfo paramEditorInfo)
    {
      paramEditorInfo = super.onCreateInputConnection(paramEditorInfo);
      if (this.mHasPendingShowSoftInputRequest)
      {
        removeCallbacks(this.mRunShowSoftInputIfNecessary);
        post(this.mRunShowSoftInputIfNecessary);
      }
      return paramEditorInfo;
    }
    
    protected void onFinishInflate()
    {
      super.onFinishInflate();
      DisplayMetrics localDisplayMetrics = getResources().getDisplayMetrics();
      setMinWidth((int)TypedValue.applyDimension(1, getSearchViewTextMinWidthDp(), localDisplayMetrics));
    }
    
    protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
    {
      super.onFocusChanged(paramBoolean, paramInt, paramRect);
      this.mSearchView.onTextFocusChanged();
    }
    
    public boolean onKeyPreIme(int paramInt, KeyEvent paramKeyEvent)
    {
      boolean bool = super.onKeyPreIme(paramInt, paramKeyEvent);
      if ((bool) && (paramInt == 4) && (paramKeyEvent.getAction() == 1)) {
        setImeVisibility(false);
      }
      return bool;
    }
    
    public void onWindowFocusChanged(boolean paramBoolean)
    {
      super.onWindowFocusChanged(paramBoolean);
      if ((paramBoolean) && (this.mSearchView.hasFocus()) && (getVisibility() == 0))
      {
        this.mHasPendingShowSoftInputRequest = true;
        if (SearchView.isLandscapeMode(getContext())) {
          ensureImeVisible(true);
        }
      }
    }
    
    public void performCompletion() {}
    
    protected void replaceText(CharSequence paramCharSequence) {}
    
    void setSearchView(SearchView paramSearchView)
    {
      this.mSearchView = paramSearchView;
    }
    
    public void setThreshold(int paramInt)
    {
      super.setThreshold(paramInt);
      this.mThreshold = paramInt;
    }
  }
  
  private static class UpdatableTouchDelegate
    extends TouchDelegate
  {
    private final Rect mActualBounds;
    private boolean mDelegateTargeted;
    private final View mDelegateView;
    private final int mSlop;
    private final Rect mSlopBounds;
    private final Rect mTargetBounds;
    
    public UpdatableTouchDelegate(Rect paramRect1, Rect paramRect2, View paramView)
    {
      super(paramView);
      this.mSlop = ViewConfiguration.get(paramView.getContext()).getScaledTouchSlop();
      this.mTargetBounds = new Rect();
      this.mSlopBounds = new Rect();
      this.mActualBounds = new Rect();
      setBounds(paramRect1, paramRect2);
      this.mDelegateView = paramView;
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      int i = (int)paramMotionEvent.getX();
      int j = (int)paramMotionEvent.getY();
      boolean bool1 = false;
      int k = 1;
      boolean bool2 = false;
      int m = paramMotionEvent.getAction();
      if (m != 0)
      {
        if ((m != 1) && (m != 2))
        {
          if (m != 3)
          {
            m = k;
          }
          else
          {
            bool1 = this.mDelegateTargeted;
            this.mDelegateTargeted = false;
            m = k;
          }
        }
        else
        {
          boolean bool3 = this.mDelegateTargeted;
          bool1 = bool3;
          m = k;
          if (bool3)
          {
            bool1 = bool3;
            m = k;
            if (!this.mSlopBounds.contains(i, j))
            {
              m = 0;
              bool1 = bool3;
            }
          }
        }
      }
      else
      {
        m = k;
        if (this.mTargetBounds.contains(i, j))
        {
          this.mDelegateTargeted = true;
          bool1 = true;
          m = k;
        }
      }
      if (bool1)
      {
        if ((m != 0) && (!this.mActualBounds.contains(i, j))) {
          paramMotionEvent.setLocation(this.mDelegateView.getWidth() / 2, this.mDelegateView.getHeight() / 2);
        } else {
          paramMotionEvent.setLocation(i - this.mActualBounds.left, j - this.mActualBounds.top);
        }
        bool2 = this.mDelegateView.dispatchTouchEvent(paramMotionEvent);
      }
      return bool2;
    }
    
    public void setBounds(Rect paramRect1, Rect paramRect2)
    {
      this.mTargetBounds.set(paramRect1);
      this.mSlopBounds.set(paramRect1);
      paramRect1 = this.mSlopBounds;
      int i = this.mSlop;
      paramRect1.inset(-i, -i);
      this.mActualBounds.set(paramRect2);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/SearchView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */