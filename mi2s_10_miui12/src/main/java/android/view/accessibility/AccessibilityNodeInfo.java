package android.view.accessibility;

import android.annotation.UnsupportedAppUsage;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AccessibilityClickableSpan;
import android.text.style.AccessibilityURLSpan;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.LongArray;
import android.util.Pools.SynchronizedPool;
import android.view.View;
import com.android.internal.util.BitUtils;
import com.android.internal.util.CollectionUtils;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class AccessibilityNodeInfo
  implements Parcelable
{
  public static final int ACTION_ACCESSIBILITY_FOCUS = 64;
  public static final String ACTION_ARGUMENT_ACCESSIBLE_CLICKABLE_SPAN = "android.view.accessibility.action.ACTION_ARGUMENT_ACCESSIBLE_CLICKABLE_SPAN";
  public static final String ACTION_ARGUMENT_COLUMN_INT = "android.view.accessibility.action.ARGUMENT_COLUMN_INT";
  public static final String ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN = "ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN";
  public static final String ACTION_ARGUMENT_HTML_ELEMENT_STRING = "ACTION_ARGUMENT_HTML_ELEMENT_STRING";
  public static final String ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT = "ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT";
  public static final String ACTION_ARGUMENT_MOVE_WINDOW_X = "ACTION_ARGUMENT_MOVE_WINDOW_X";
  public static final String ACTION_ARGUMENT_MOVE_WINDOW_Y = "ACTION_ARGUMENT_MOVE_WINDOW_Y";
  public static final String ACTION_ARGUMENT_PROGRESS_VALUE = "android.view.accessibility.action.ARGUMENT_PROGRESS_VALUE";
  public static final String ACTION_ARGUMENT_ROW_INT = "android.view.accessibility.action.ARGUMENT_ROW_INT";
  public static final String ACTION_ARGUMENT_SELECTION_END_INT = "ACTION_ARGUMENT_SELECTION_END_INT";
  public static final String ACTION_ARGUMENT_SELECTION_START_INT = "ACTION_ARGUMENT_SELECTION_START_INT";
  public static final String ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE = "ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE";
  public static final int ACTION_CLEAR_ACCESSIBILITY_FOCUS = 128;
  public static final int ACTION_CLEAR_FOCUS = 2;
  public static final int ACTION_CLEAR_SELECTION = 8;
  public static final int ACTION_CLICK = 16;
  public static final int ACTION_COLLAPSE = 524288;
  public static final int ACTION_COPY = 16384;
  public static final int ACTION_CUT = 65536;
  public static final int ACTION_DISMISS = 1048576;
  public static final int ACTION_EXPAND = 262144;
  public static final int ACTION_FOCUS = 1;
  public static final int ACTION_LONG_CLICK = 32;
  public static final int ACTION_NEXT_AT_MOVEMENT_GRANULARITY = 256;
  public static final int ACTION_NEXT_HTML_ELEMENT = 1024;
  public static final int ACTION_PASTE = 32768;
  public static final int ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY = 512;
  public static final int ACTION_PREVIOUS_HTML_ELEMENT = 2048;
  public static final int ACTION_SCROLL_BACKWARD = 8192;
  public static final int ACTION_SCROLL_FORWARD = 4096;
  public static final int ACTION_SELECT = 4;
  public static final int ACTION_SET_SELECTION = 131072;
  public static final int ACTION_SET_TEXT = 2097152;
  private static final int ACTION_TYPE_MASK = -16777216;
  private static final int BOOLEAN_PROPERTY_ACCESSIBILITY_FOCUSED = 1024;
  private static final int BOOLEAN_PROPERTY_CHECKABLE = 1;
  private static final int BOOLEAN_PROPERTY_CHECKED = 2;
  private static final int BOOLEAN_PROPERTY_CLICKABLE = 32;
  private static final int BOOLEAN_PROPERTY_CONTENT_INVALID = 65536;
  private static final int BOOLEAN_PROPERTY_CONTEXT_CLICKABLE = 131072;
  private static final int BOOLEAN_PROPERTY_DISMISSABLE = 16384;
  private static final int BOOLEAN_PROPERTY_EDITABLE = 4096;
  private static final int BOOLEAN_PROPERTY_ENABLED = 128;
  private static final int BOOLEAN_PROPERTY_FOCUSABLE = 4;
  private static final int BOOLEAN_PROPERTY_FOCUSED = 8;
  private static final int BOOLEAN_PROPERTY_IMPORTANCE = 262144;
  private static final int BOOLEAN_PROPERTY_IS_HEADING = 2097152;
  private static final int BOOLEAN_PROPERTY_IS_SHOWING_HINT = 1048576;
  private static final int BOOLEAN_PROPERTY_IS_TEXT_ENTRY_KEY = 4194304;
  private static final int BOOLEAN_PROPERTY_LONG_CLICKABLE = 64;
  private static final int BOOLEAN_PROPERTY_MULTI_LINE = 32768;
  private static final int BOOLEAN_PROPERTY_OPENS_POPUP = 8192;
  private static final int BOOLEAN_PROPERTY_PASSWORD = 256;
  private static final int BOOLEAN_PROPERTY_SCREEN_READER_FOCUSABLE = 524288;
  private static final int BOOLEAN_PROPERTY_SCROLLABLE = 512;
  private static final int BOOLEAN_PROPERTY_SELECTED = 16;
  private static final int BOOLEAN_PROPERTY_VISIBLE_TO_USER = 2048;
  public static final Parcelable.Creator<AccessibilityNodeInfo> CREATOR = new Parcelable.Creator()
  {
    public AccessibilityNodeInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      AccessibilityNodeInfo localAccessibilityNodeInfo = AccessibilityNodeInfo.obtain();
      localAccessibilityNodeInfo.initFromParcel(paramAnonymousParcel);
      return localAccessibilityNodeInfo;
    }
    
    public AccessibilityNodeInfo[] newArray(int paramAnonymousInt)
    {
      return new AccessibilityNodeInfo[paramAnonymousInt];
    }
  };
  private static final boolean DEBUG = false;
  private static final AccessibilityNodeInfo DEFAULT;
  public static final String EXTRA_DATA_REQUESTED_KEY = "android.view.accessibility.AccessibilityNodeInfo.extra_data_requested";
  public static final String EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_LENGTH = "android.view.accessibility.extra.DATA_TEXT_CHARACTER_LOCATION_ARG_LENGTH";
  public static final String EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_START_INDEX = "android.view.accessibility.extra.DATA_TEXT_CHARACTER_LOCATION_ARG_START_INDEX";
  public static final String EXTRA_DATA_TEXT_CHARACTER_LOCATION_KEY = "android.view.accessibility.extra.DATA_TEXT_CHARACTER_LOCATION_KEY";
  public static final int FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 8;
  public static final int FLAG_PREFETCH_DESCENDANTS = 4;
  public static final int FLAG_PREFETCH_PREDECESSORS = 1;
  public static final int FLAG_PREFETCH_SIBLINGS = 2;
  public static final int FLAG_REPORT_VIEW_IDS = 16;
  public static final int FOCUS_ACCESSIBILITY = 2;
  public static final int FOCUS_INPUT = 1;
  public static final int LAST_LEGACY_STANDARD_ACTION = 2097152;
  private static final int MAX_POOL_SIZE = 50;
  public static final int MOVEMENT_GRANULARITY_CHARACTER = 1;
  public static final int MOVEMENT_GRANULARITY_LINE = 4;
  public static final int MOVEMENT_GRANULARITY_PAGE = 16;
  public static final int MOVEMENT_GRANULARITY_PARAGRAPH = 8;
  public static final int MOVEMENT_GRANULARITY_WORD = 2;
  public static final int ROOT_ITEM_ID = 2147483646;
  public static final long ROOT_NODE_ID;
  private static final String TAG = "AccessibilityNodeInfo";
  public static final int UNDEFINED_CONNECTION_ID = -1;
  public static final int UNDEFINED_ITEM_ID = Integer.MAX_VALUE;
  public static final long UNDEFINED_NODE_ID = makeNodeId(Integer.MAX_VALUE, Integer.MAX_VALUE);
  public static final int UNDEFINED_SELECTION_INDEX = -1;
  private static final long VIRTUAL_DESCENDANT_ID_MASK = -4294967296L;
  private static final int VIRTUAL_DESCENDANT_ID_SHIFT = 32;
  private static AtomicInteger sNumInstancesInUse;
  private static final Pools.SynchronizedPool<AccessibilityNodeInfo> sPool;
  private ArrayList<AccessibilityAction> mActions;
  private int mBooleanProperties;
  private final Rect mBoundsInParent;
  private final Rect mBoundsInScreen;
  @UnsupportedAppUsage
  private LongArray mChildNodeIds;
  private CharSequence mClassName;
  private CollectionInfo mCollectionInfo;
  private CollectionItemInfo mCollectionItemInfo;
  private int mConnectionId;
  private CharSequence mContentDescription;
  private int mDrawingOrderInParent;
  private CharSequence mError;
  private ArrayList<String> mExtraDataKeys;
  private Bundle mExtras;
  private CharSequence mHintText;
  private int mInputType;
  private long mLabelForId;
  private long mLabeledById;
  private int mLiveRegion;
  private int mMaxTextLength;
  private int mMovementGranularities;
  private CharSequence mOriginalText;
  private CharSequence mPackageName;
  private CharSequence mPaneTitle;
  private long mParentNodeId;
  private RangeInfo mRangeInfo;
  @UnsupportedAppUsage
  private boolean mSealed;
  @UnsupportedAppUsage
  private long mSourceNodeId;
  private CharSequence mText;
  private int mTextSelectionEnd;
  private int mTextSelectionStart;
  private CharSequence mTooltipText;
  private TouchDelegateInfo mTouchDelegateInfo;
  private long mTraversalAfter;
  private long mTraversalBefore;
  private String mViewIdResourceName;
  private int mWindowId = -1;
  
  static
  {
    ROOT_NODE_ID = makeNodeId(2147483646, -1);
    sPool = new Pools.SynchronizedPool(50);
    DEFAULT = new AccessibilityNodeInfo();
  }
  
  private AccessibilityNodeInfo()
  {
    long l = UNDEFINED_NODE_ID;
    this.mSourceNodeId = l;
    this.mParentNodeId = l;
    this.mLabelForId = l;
    this.mLabeledById = l;
    this.mTraversalBefore = l;
    this.mTraversalAfter = l;
    this.mBoundsInParent = new Rect();
    this.mBoundsInScreen = new Rect();
    this.mMaxTextLength = -1;
    this.mTextSelectionStart = -1;
    this.mTextSelectionEnd = -1;
    this.mInputType = 0;
    this.mLiveRegion = 0;
    this.mConnectionId = -1;
  }
  
  AccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    long l = UNDEFINED_NODE_ID;
    this.mSourceNodeId = l;
    this.mParentNodeId = l;
    this.mLabelForId = l;
    this.mLabeledById = l;
    this.mTraversalBefore = l;
    this.mTraversalAfter = l;
    this.mBoundsInParent = new Rect();
    this.mBoundsInScreen = new Rect();
    this.mMaxTextLength = -1;
    this.mTextSelectionStart = -1;
    this.mTextSelectionEnd = -1;
    this.mInputType = 0;
    this.mLiveRegion = 0;
    this.mConnectionId = -1;
    init(paramAccessibilityNodeInfo);
  }
  
  private void addActionUnchecked(AccessibilityAction paramAccessibilityAction)
  {
    if (paramAccessibilityAction == null) {
      return;
    }
    if (this.mActions == null) {
      this.mActions = new ArrayList();
    }
    this.mActions.remove(paramAccessibilityAction);
    this.mActions.add(paramAccessibilityAction);
  }
  
  private void addChildInternal(View paramView, int paramInt, boolean paramBoolean)
  {
    enforceNotSealed();
    if (this.mChildNodeIds == null) {
      this.mChildNodeIds = new LongArray();
    }
    int i;
    if (paramView != null) {
      i = paramView.getAccessibilityViewId();
    } else {
      i = Integer.MAX_VALUE;
    }
    long l = makeNodeId(i, paramInt);
    if (l == this.mSourceNodeId)
    {
      Log.e("AccessibilityNodeInfo", "Rejecting attempt to make a View its own child");
      return;
    }
    if ((paramBoolean) && (this.mChildNodeIds.indexOf(l) >= 0)) {
      return;
    }
    this.mChildNodeIds.add(l);
  }
  
  private void addStandardActions(long paramLong)
  {
    while (paramLong > 0L)
    {
      long l = 1L << Long.numberOfTrailingZeros(paramLong);
      paramLong &= l;
      addAction(getActionSingletonBySerializationFlag(l));
    }
  }
  
  private static boolean canPerformRequestOverConnection(int paramInt1, int paramInt2, long paramLong)
  {
    boolean bool;
    if ((paramInt2 != -1) && (getAccessibilityViewId(paramLong) != Integer.MAX_VALUE) && (paramInt1 != -1)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private void clear()
  {
    init(DEFAULT);
  }
  
  private void enforceValidFocusDirection(int paramInt)
  {
    if ((paramInt != 1) && (paramInt != 2) && (paramInt != 17) && (paramInt != 33) && (paramInt != 66) && (paramInt != 130))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unknown direction: ");
      localStringBuilder.append(paramInt);
      throw new IllegalArgumentException(localStringBuilder.toString());
    }
  }
  
  private void enforceValidFocusType(int paramInt)
  {
    if ((paramInt != 1) && (paramInt != 2))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unknown focus type: ");
      localStringBuilder.append(paramInt);
      throw new IllegalArgumentException(localStringBuilder.toString());
    }
  }
  
  @UnsupportedAppUsage
  public static int getAccessibilityViewId(long paramLong)
  {
    return (int)paramLong;
  }
  
  private static AccessibilityAction getActionSingleton(int paramInt)
  {
    int i = AccessibilityAction.sStandardActions.size();
    for (int j = 0; j < i; j++)
    {
      AccessibilityAction localAccessibilityAction = (AccessibilityAction)AccessibilityAction.sStandardActions.valueAt(j);
      if (paramInt == localAccessibilityAction.getId()) {
        return localAccessibilityAction;
      }
    }
    return null;
  }
  
  private static AccessibilityAction getActionSingletonBySerializationFlag(long paramLong)
  {
    int i = AccessibilityAction.sStandardActions.size();
    for (int j = 0; j < i; j++)
    {
      AccessibilityAction localAccessibilityAction = (AccessibilityAction)AccessibilityAction.sStandardActions.valueAt(j);
      if (paramLong == localAccessibilityAction.mSerializationFlag) {
        return localAccessibilityAction;
      }
    }
    return null;
  }
  
  private static String getActionSymbolicName(int paramInt)
  {
    if (paramInt != 1)
    {
      if (paramInt != 2)
      {
        switch (paramInt)
        {
        default: 
          switch (paramInt)
          {
          default: 
            switch (paramInt)
            {
            default: 
              return "ACTION_UNKNOWN";
            case 16908361: 
              return "ACTION_PAGE_RIGHT";
            case 16908360: 
              return "ACTION_PAGE_LEFT";
            case 16908359: 
              return "ACTION_PAGE_DOWN";
            case 16908358: 
              return "ACTION_PAGE_UP";
            case 16908357: 
              return "ACTION_HIDE_TOOLTIP";
            }
            return "ACTION_SHOW_TOOLTIP";
          case 16908349: 
            return "ACTION_SET_PROGRESS";
          case 16908348: 
            return "ACTION_CONTEXT_CLICK";
          case 16908347: 
            return "ACTION_SCROLL_RIGHT";
          case 16908346: 
            return "ACTION_SCROLL_DOWN";
          case 16908345: 
            return "ACTION_SCROLL_LEFT";
          case 16908344: 
            return "ACTION_SCROLL_UP";
          case 16908343: 
            return "ACTION_SCROLL_TO_POSITION";
          }
          return "ACTION_SHOW_ON_SCREEN";
        case 2097152: 
          return "ACTION_SET_TEXT";
        case 1048576: 
          return "ACTION_DISMISS";
        case 524288: 
          return "ACTION_COLLAPSE";
        case 262144: 
          return "ACTION_EXPAND";
        case 131072: 
          return "ACTION_SET_SELECTION";
        case 65536: 
          return "ACTION_CUT";
        case 32768: 
          return "ACTION_PASTE";
        case 16384: 
          return "ACTION_COPY";
        case 8192: 
          return "ACTION_SCROLL_BACKWARD";
        case 4096: 
          return "ACTION_SCROLL_FORWARD";
        case 2048: 
          return "ACTION_PREVIOUS_HTML_ELEMENT";
        case 1024: 
          return "ACTION_NEXT_HTML_ELEMENT";
        case 512: 
          return "ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY";
        case 256: 
          return "ACTION_NEXT_AT_MOVEMENT_GRANULARITY";
        case 128: 
          return "ACTION_CLEAR_ACCESSIBILITY_FOCUS";
        case 64: 
          return "ACTION_ACCESSIBILITY_FOCUS";
        case 32: 
          return "ACTION_LONG_CLICK";
        case 16: 
          return "ACTION_CLICK";
        case 8: 
          return "ACTION_CLEAR_SELECTION";
        }
        return "ACTION_SELECT";
      }
      return "ACTION_CLEAR_FOCUS";
    }
    return "ACTION_FOCUS";
  }
  
  private boolean getBooleanProperty(int paramInt)
  {
    boolean bool;
    if ((this.mBooleanProperties & paramInt) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private static String getMovementGranularitySymbolicName(int paramInt)
  {
    if (paramInt != 1)
    {
      if (paramInt != 2)
      {
        if (paramInt != 4)
        {
          if (paramInt != 8)
          {
            if (paramInt == 16) {
              return "MOVEMENT_GRANULARITY_PAGE";
            }
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("Unknown movement granularity: ");
            localStringBuilder.append(paramInt);
            throw new IllegalArgumentException(localStringBuilder.toString());
          }
          return "MOVEMENT_GRANULARITY_PARAGRAPH";
        }
        return "MOVEMENT_GRANULARITY_LINE";
      }
      return "MOVEMENT_GRANULARITY_WORD";
    }
    return "MOVEMENT_GRANULARITY_CHARACTER";
  }
  
  private static AccessibilityNodeInfo getNodeForAccessibilityId(int paramInt1, int paramInt2, long paramLong)
  {
    if (!canPerformRequestOverConnection(paramInt1, paramInt2, paramLong)) {
      return null;
    }
    return AccessibilityInteractionClient.getInstance().findAccessibilityNodeInfoByAccessibilityId(paramInt1, paramInt2, paramLong, false, 7, null);
  }
  
  @UnsupportedAppUsage
  public static int getVirtualDescendantId(long paramLong)
  {
    return (int)((0xFFFFFFFF00000000 & paramLong) >> 32);
  }
  
  private static String idItemToString(int paramInt)
  {
    if (paramInt != -1)
    {
      switch (paramInt)
      {
      default: 
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("");
        localStringBuilder.append(paramInt);
        return localStringBuilder.toString();
      case 2147483647: 
        return "UNDEFINED";
      }
      return "ROOT";
    }
    return "HOST";
  }
  
  public static String idToString(long paramLong)
  {
    int i = getAccessibilityViewId(paramLong);
    int j = getVirtualDescendantId(paramLong);
    Object localObject;
    if (j == -1)
    {
      localObject = idItemToString(i);
    }
    else
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append(idItemToString(i));
      ((StringBuilder)localObject).append(":");
      ((StringBuilder)localObject).append(idItemToString(j));
      localObject = ((StringBuilder)localObject).toString();
    }
    return (String)localObject;
  }
  
  private void init(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    this.mSealed = paramAccessibilityNodeInfo.mSealed;
    this.mSourceNodeId = paramAccessibilityNodeInfo.mSourceNodeId;
    this.mParentNodeId = paramAccessibilityNodeInfo.mParentNodeId;
    this.mLabelForId = paramAccessibilityNodeInfo.mLabelForId;
    this.mLabeledById = paramAccessibilityNodeInfo.mLabeledById;
    this.mTraversalBefore = paramAccessibilityNodeInfo.mTraversalBefore;
    this.mTraversalAfter = paramAccessibilityNodeInfo.mTraversalAfter;
    this.mWindowId = paramAccessibilityNodeInfo.mWindowId;
    this.mConnectionId = paramAccessibilityNodeInfo.mConnectionId;
    this.mBoundsInParent.set(paramAccessibilityNodeInfo.mBoundsInParent);
    this.mBoundsInScreen.set(paramAccessibilityNodeInfo.mBoundsInScreen);
    this.mPackageName = paramAccessibilityNodeInfo.mPackageName;
    this.mClassName = paramAccessibilityNodeInfo.mClassName;
    this.mOriginalText = paramAccessibilityNodeInfo.mOriginalText;
    this.mText = paramAccessibilityNodeInfo.mText;
    this.mOriginalText = paramAccessibilityNodeInfo.mOriginalText;
    this.mHintText = paramAccessibilityNodeInfo.mHintText;
    this.mError = paramAccessibilityNodeInfo.mError;
    this.mContentDescription = paramAccessibilityNodeInfo.mContentDescription;
    this.mPaneTitle = paramAccessibilityNodeInfo.mPaneTitle;
    this.mTooltipText = paramAccessibilityNodeInfo.mTooltipText;
    this.mViewIdResourceName = paramAccessibilityNodeInfo.mViewIdResourceName;
    Object localObject1 = this.mActions;
    if (localObject1 != null) {
      ((ArrayList)localObject1).clear();
    }
    Object localObject2 = paramAccessibilityNodeInfo.mActions;
    if ((localObject2 != null) && (((ArrayList)localObject2).size() > 0))
    {
      localObject1 = this.mActions;
      if (localObject1 == null) {
        this.mActions = new ArrayList((Collection)localObject2);
      } else {
        ((ArrayList)localObject1).addAll(paramAccessibilityNodeInfo.mActions);
      }
    }
    this.mBooleanProperties = paramAccessibilityNodeInfo.mBooleanProperties;
    this.mMaxTextLength = paramAccessibilityNodeInfo.mMaxTextLength;
    this.mMovementGranularities = paramAccessibilityNodeInfo.mMovementGranularities;
    localObject1 = this.mChildNodeIds;
    if (localObject1 != null) {
      ((LongArray)localObject1).clear();
    }
    localObject2 = paramAccessibilityNodeInfo.mChildNodeIds;
    if ((localObject2 != null) && (((LongArray)localObject2).size() > 0))
    {
      localObject1 = this.mChildNodeIds;
      if (localObject1 == null) {
        this.mChildNodeIds = ((LongArray)localObject2).clone();
      } else {
        ((LongArray)localObject1).addAll((LongArray)localObject2);
      }
    }
    this.mTextSelectionStart = paramAccessibilityNodeInfo.mTextSelectionStart;
    this.mTextSelectionEnd = paramAccessibilityNodeInfo.mTextSelectionEnd;
    this.mInputType = paramAccessibilityNodeInfo.mInputType;
    this.mLiveRegion = paramAccessibilityNodeInfo.mLiveRegion;
    this.mDrawingOrderInParent = paramAccessibilityNodeInfo.mDrawingOrderInParent;
    this.mExtraDataKeys = paramAccessibilityNodeInfo.mExtraDataKeys;
    localObject1 = paramAccessibilityNodeInfo.mExtras;
    localObject2 = null;
    if (localObject1 != null) {
      localObject1 = new Bundle((Bundle)localObject1);
    } else {
      localObject1 = null;
    }
    this.mExtras = ((Bundle)localObject1);
    localObject1 = this.mRangeInfo;
    if (localObject1 != null) {
      ((RangeInfo)localObject1).recycle();
    }
    localObject1 = paramAccessibilityNodeInfo.mRangeInfo;
    if (localObject1 != null) {
      localObject1 = RangeInfo.obtain((RangeInfo)localObject1);
    } else {
      localObject1 = null;
    }
    this.mRangeInfo = ((RangeInfo)localObject1);
    localObject1 = this.mCollectionInfo;
    if (localObject1 != null) {
      ((CollectionInfo)localObject1).recycle();
    }
    localObject1 = paramAccessibilityNodeInfo.mCollectionInfo;
    if (localObject1 != null) {
      localObject1 = CollectionInfo.obtain((CollectionInfo)localObject1);
    } else {
      localObject1 = null;
    }
    this.mCollectionInfo = ((CollectionInfo)localObject1);
    localObject1 = this.mCollectionItemInfo;
    if (localObject1 != null) {
      ((CollectionItemInfo)localObject1).recycle();
    }
    localObject1 = paramAccessibilityNodeInfo.mCollectionItemInfo;
    if (localObject1 != null) {
      localObject1 = CollectionItemInfo.obtain((CollectionItemInfo)localObject1);
    } else {
      localObject1 = null;
    }
    this.mCollectionItemInfo = ((CollectionItemInfo)localObject1);
    paramAccessibilityNodeInfo = paramAccessibilityNodeInfo.mTouchDelegateInfo;
    if (paramAccessibilityNodeInfo != null) {
      paramAccessibilityNodeInfo = new TouchDelegateInfo(paramAccessibilityNodeInfo.mTargetMap, true);
    } else {
      paramAccessibilityNodeInfo = (AccessibilityNodeInfo)localObject2;
    }
    this.mTouchDelegateInfo = paramAccessibilityNodeInfo;
  }
  
  private void initFromParcel(Parcel paramParcel)
  {
    long l1 = paramParcel.readLong();
    int i = 0 + 1;
    boolean bool1;
    if (BitUtils.isBitSet(l1, 0))
    {
      if (paramParcel.readInt() == 1) {
        bool1 = true;
      } else {
        bool1 = false;
      }
    }
    else {
      bool1 = DEFAULT.mSealed;
    }
    int j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      this.mSourceNodeId = paramParcel.readLong();
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      this.mWindowId = paramParcel.readInt();
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      this.mParentNodeId = paramParcel.readLong();
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      this.mLabelForId = paramParcel.readLong();
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      this.mLabeledById = paramParcel.readLong();
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      this.mTraversalBefore = paramParcel.readLong();
    }
    int k = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      this.mTraversalAfter = paramParcel.readLong();
    }
    j = k + 1;
    if (BitUtils.isBitSet(l1, k)) {
      this.mConnectionId = paramParcel.readInt();
    }
    i = j + 1;
    boolean bool2 = BitUtils.isBitSet(l1, j);
    Object localObject1 = null;
    if (bool2)
    {
      k = paramParcel.readInt();
      if (k <= 0)
      {
        this.mChildNodeIds = null;
      }
      else
      {
        this.mChildNodeIds = new LongArray(k);
        for (j = 0; j < k; j++)
        {
          long l2 = paramParcel.readLong();
          this.mChildNodeIds.add(l2);
        }
      }
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i))
    {
      this.mBoundsInParent.top = paramParcel.readInt();
      this.mBoundsInParent.bottom = paramParcel.readInt();
      this.mBoundsInParent.left = paramParcel.readInt();
      this.mBoundsInParent.right = paramParcel.readInt();
    }
    k = j + 1;
    if (BitUtils.isBitSet(l1, j))
    {
      this.mBoundsInScreen.top = paramParcel.readInt();
      this.mBoundsInScreen.bottom = paramParcel.readInt();
      this.mBoundsInScreen.left = paramParcel.readInt();
      this.mBoundsInScreen.right = paramParcel.readInt();
    }
    i = k + 1;
    if (BitUtils.isBitSet(l1, k))
    {
      addStandardActions(paramParcel.readLong());
      k = paramParcel.readInt();
      for (j = 0; j < k; j++) {
        addActionUnchecked(new AccessibilityAction(paramParcel.readInt(), paramParcel.readCharSequence()));
      }
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      this.mMaxTextLength = paramParcel.readInt();
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      this.mMovementGranularities = paramParcel.readInt();
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      this.mBooleanProperties = paramParcel.readInt();
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      this.mPackageName = paramParcel.readCharSequence();
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      this.mClassName = paramParcel.readCharSequence();
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      this.mText = paramParcel.readCharSequence();
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      this.mHintText = paramParcel.readCharSequence();
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      this.mError = paramParcel.readCharSequence();
    }
    k = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      this.mContentDescription = paramParcel.readCharSequence();
    }
    j = k + 1;
    if (BitUtils.isBitSet(l1, k)) {
      this.mPaneTitle = paramParcel.readCharSequence();
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      this.mTooltipText = paramParcel.readCharSequence();
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      this.mViewIdResourceName = paramParcel.readString();
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      this.mTextSelectionStart = paramParcel.readInt();
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      this.mTextSelectionEnd = paramParcel.readInt();
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      this.mInputType = paramParcel.readInt();
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      this.mLiveRegion = paramParcel.readInt();
    }
    k = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      this.mDrawingOrderInParent = paramParcel.readInt();
    }
    i = k + 1;
    if (BitUtils.isBitSet(l1, k)) {
      localObject2 = paramParcel.createStringArrayList();
    } else {
      localObject2 = null;
    }
    this.mExtraDataKeys = ((ArrayList)localObject2);
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      localObject2 = paramParcel.readBundle();
    } else {
      localObject2 = null;
    }
    this.mExtras = ((Bundle)localObject2);
    Object localObject2 = this.mRangeInfo;
    if (localObject2 != null) {
      ((RangeInfo)localObject2).recycle();
    }
    k = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      localObject2 = RangeInfo.obtain(paramParcel.readInt(), paramParcel.readFloat(), paramParcel.readFloat(), paramParcel.readFloat());
    } else {
      localObject2 = null;
    }
    this.mRangeInfo = ((RangeInfo)localObject2);
    localObject2 = this.mCollectionInfo;
    if (localObject2 != null) {
      ((CollectionInfo)localObject2).recycle();
    }
    i = k + 1;
    if (BitUtils.isBitSet(l1, k))
    {
      j = paramParcel.readInt();
      k = paramParcel.readInt();
      if (paramParcel.readInt() == 1) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      localObject2 = CollectionInfo.obtain(j, k, bool2, paramParcel.readInt());
    }
    else
    {
      localObject2 = null;
    }
    this.mCollectionInfo = ((CollectionInfo)localObject2);
    localObject2 = this.mCollectionItemInfo;
    if (localObject2 != null) {
      ((CollectionItemInfo)localObject2).recycle();
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i))
    {
      int m = paramParcel.readInt();
      k = paramParcel.readInt();
      int n = paramParcel.readInt();
      i = paramParcel.readInt();
      if (paramParcel.readInt() == 1) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      boolean bool3;
      if (paramParcel.readInt() == 1) {
        bool3 = true;
      } else {
        bool3 = false;
      }
      localObject2 = CollectionItemInfo.obtain(m, k, n, i, bool2, bool3);
    }
    else
    {
      localObject2 = localObject1;
    }
    this.mCollectionItemInfo = ((CollectionItemInfo)localObject2);
    if (BitUtils.isBitSet(l1, j)) {
      this.mTouchDelegateInfo = ((TouchDelegateInfo)TouchDelegateInfo.CREATOR.createFromParcel(paramParcel));
    }
    this.mSealed = bool1;
  }
  
  private static boolean isDefaultStandardAction(AccessibilityAction paramAccessibilityAction)
  {
    boolean bool;
    if ((paramAccessibilityAction.mSerializationFlag != -1L) && (TextUtils.isEmpty(paramAccessibilityAction.getLabel()))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static long makeNodeId(int paramInt1, int paramInt2)
  {
    return paramInt2 << 32 | paramInt1;
  }
  
  public static AccessibilityNodeInfo obtain()
  {
    AccessibilityNodeInfo localAccessibilityNodeInfo = (AccessibilityNodeInfo)sPool.acquire();
    AtomicInteger localAtomicInteger = sNumInstancesInUse;
    if (localAtomicInteger != null) {
      localAtomicInteger.incrementAndGet();
    }
    if (localAccessibilityNodeInfo == null) {
      localAccessibilityNodeInfo = new AccessibilityNodeInfo();
    }
    return localAccessibilityNodeInfo;
  }
  
  public static AccessibilityNodeInfo obtain(View paramView)
  {
    AccessibilityNodeInfo localAccessibilityNodeInfo = obtain();
    localAccessibilityNodeInfo.setSource(paramView);
    return localAccessibilityNodeInfo;
  }
  
  public static AccessibilityNodeInfo obtain(View paramView, int paramInt)
  {
    AccessibilityNodeInfo localAccessibilityNodeInfo = obtain();
    localAccessibilityNodeInfo.setSource(paramView, paramInt);
    return localAccessibilityNodeInfo;
  }
  
  public static AccessibilityNodeInfo obtain(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    AccessibilityNodeInfo localAccessibilityNodeInfo = obtain();
    localAccessibilityNodeInfo.init(paramAccessibilityNodeInfo);
    return localAccessibilityNodeInfo;
  }
  
  private void setBooleanProperty(int paramInt, boolean paramBoolean)
  {
    enforceNotSealed();
    if (paramBoolean) {
      this.mBooleanProperties |= paramInt;
    } else {
      this.mBooleanProperties &= paramInt;
    }
  }
  
  public static void setNumInstancesInUseCounter(AtomicInteger paramAtomicInteger)
  {
    sNumInstancesInUse = paramAtomicInteger;
  }
  
  @Deprecated
  public void addAction(int paramInt)
  {
    enforceNotSealed();
    if ((0xFF000000 & paramInt) == 0)
    {
      addStandardActions(paramInt);
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Action is not a combination of the standard actions: ");
    localStringBuilder.append(paramInt);
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public void addAction(AccessibilityAction paramAccessibilityAction)
  {
    enforceNotSealed();
    addActionUnchecked(paramAccessibilityAction);
  }
  
  public void addChild(View paramView)
  {
    addChildInternal(paramView, -1, true);
  }
  
  public void addChild(View paramView, int paramInt)
  {
    addChildInternal(paramView, paramInt, true);
  }
  
  public void addChildUnchecked(View paramView)
  {
    addChildInternal(paramView, -1, false);
  }
  
  public boolean canOpenPopup()
  {
    return getBooleanProperty(8192);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  protected void enforceNotSealed()
  {
    if (!isSealed()) {
      return;
    }
    throw new IllegalStateException("Cannot perform this action on a sealed instance.");
  }
  
  protected void enforceSealed()
  {
    if (isSealed()) {
      return;
    }
    throw new IllegalStateException("Cannot perform this action on a not sealed instance.");
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (AccessibilityNodeInfo)paramObject;
    if (this.mSourceNodeId != ((AccessibilityNodeInfo)paramObject).mSourceNodeId) {
      return false;
    }
    return this.mWindowId == ((AccessibilityNodeInfo)paramObject).mWindowId;
  }
  
  public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String paramString)
  {
    enforceSealed();
    if (!canPerformRequestOverConnection(this.mConnectionId, this.mWindowId, this.mSourceNodeId)) {
      return Collections.emptyList();
    }
    return AccessibilityInteractionClient.getInstance().findAccessibilityNodeInfosByText(this.mConnectionId, this.mWindowId, this.mSourceNodeId, paramString);
  }
  
  public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByViewId(String paramString)
  {
    enforceSealed();
    if (!canPerformRequestOverConnection(this.mConnectionId, this.mWindowId, this.mSourceNodeId)) {
      return Collections.emptyList();
    }
    return AccessibilityInteractionClient.getInstance().findAccessibilityNodeInfosByViewId(this.mConnectionId, this.mWindowId, this.mSourceNodeId, paramString);
  }
  
  public AccessibilityNodeInfo findFocus(int paramInt)
  {
    enforceSealed();
    enforceValidFocusType(paramInt);
    if (!canPerformRequestOverConnection(this.mConnectionId, this.mWindowId, this.mSourceNodeId)) {
      return null;
    }
    return AccessibilityInteractionClient.getInstance().findFocus(this.mConnectionId, this.mWindowId, this.mSourceNodeId, paramInt);
  }
  
  public AccessibilityNodeInfo focusSearch(int paramInt)
  {
    enforceSealed();
    enforceValidFocusDirection(paramInt);
    if (!canPerformRequestOverConnection(this.mConnectionId, this.mWindowId, this.mSourceNodeId)) {
      return null;
    }
    return AccessibilityInteractionClient.getInstance().focusSearch(this.mConnectionId, this.mWindowId, this.mSourceNodeId, paramInt);
  }
  
  public List<AccessibilityAction> getActionList()
  {
    return CollectionUtils.emptyIfNull(this.mActions);
  }
  
  @Deprecated
  public int getActions()
  {
    int i = 0;
    ArrayList localArrayList = this.mActions;
    if (localArrayList == null) {
      return 0;
    }
    int j = localArrayList.size();
    int k = 0;
    while (k < j)
    {
      int m = ((AccessibilityAction)this.mActions.get(k)).getId();
      int n = i;
      if (m <= 2097152) {
        n = i | m;
      }
      k++;
      i = n;
    }
    return i;
  }
  
  public List<String> getAvailableExtraData()
  {
    ArrayList localArrayList = this.mExtraDataKeys;
    if (localArrayList != null) {
      return Collections.unmodifiableList(localArrayList);
    }
    return Collections.EMPTY_LIST;
  }
  
  @Deprecated
  public void getBoundsInParent(Rect paramRect)
  {
    paramRect.set(this.mBoundsInParent.left, this.mBoundsInParent.top, this.mBoundsInParent.right, this.mBoundsInParent.bottom);
  }
  
  public Rect getBoundsInScreen()
  {
    return this.mBoundsInScreen;
  }
  
  public void getBoundsInScreen(Rect paramRect)
  {
    paramRect.set(this.mBoundsInScreen.left, this.mBoundsInScreen.top, this.mBoundsInScreen.right, this.mBoundsInScreen.bottom);
  }
  
  public AccessibilityNodeInfo getChild(int paramInt)
  {
    enforceSealed();
    if (this.mChildNodeIds == null) {
      return null;
    }
    if (!canPerformRequestOverConnection(this.mConnectionId, this.mWindowId, this.mSourceNodeId)) {
      return null;
    }
    long l = this.mChildNodeIds.get(paramInt);
    return AccessibilityInteractionClient.getInstance().findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mWindowId, l, false, 4, null);
  }
  
  public int getChildCount()
  {
    LongArray localLongArray = this.mChildNodeIds;
    int i;
    if (localLongArray == null) {
      i = 0;
    } else {
      i = localLongArray.size();
    }
    return i;
  }
  
  public long getChildId(int paramInt)
  {
    LongArray localLongArray = this.mChildNodeIds;
    if (localLongArray != null) {
      return localLongArray.get(paramInt);
    }
    throw new IndexOutOfBoundsException();
  }
  
  public LongArray getChildNodeIds()
  {
    return this.mChildNodeIds;
  }
  
  public CharSequence getClassName()
  {
    return this.mClassName;
  }
  
  public CollectionInfo getCollectionInfo()
  {
    return this.mCollectionInfo;
  }
  
  public CollectionItemInfo getCollectionItemInfo()
  {
    return this.mCollectionItemInfo;
  }
  
  public int getConnectionId()
  {
    return this.mConnectionId;
  }
  
  public CharSequence getContentDescription()
  {
    return this.mContentDescription;
  }
  
  public int getDrawingOrder()
  {
    return this.mDrawingOrderInParent;
  }
  
  public CharSequence getError()
  {
    return this.mError;
  }
  
  public Bundle getExtras()
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    return this.mExtras;
  }
  
  public CharSequence getHintText()
  {
    return this.mHintText;
  }
  
  public int getInputType()
  {
    return this.mInputType;
  }
  
  public AccessibilityNodeInfo getLabelFor()
  {
    enforceSealed();
    return getNodeForAccessibilityId(this.mConnectionId, this.mWindowId, this.mLabelForId);
  }
  
  public AccessibilityNodeInfo getLabeledBy()
  {
    enforceSealed();
    return getNodeForAccessibilityId(this.mConnectionId, this.mWindowId, this.mLabeledById);
  }
  
  public int getLiveRegion()
  {
    return this.mLiveRegion;
  }
  
  public int getMaxTextLength()
  {
    return this.mMaxTextLength;
  }
  
  public int getMovementGranularities()
  {
    return this.mMovementGranularities;
  }
  
  public CharSequence getOriginalText()
  {
    return this.mOriginalText;
  }
  
  public CharSequence getPackageName()
  {
    return this.mPackageName;
  }
  
  public CharSequence getPaneTitle()
  {
    return this.mPaneTitle;
  }
  
  public AccessibilityNodeInfo getParent()
  {
    enforceSealed();
    return getNodeForAccessibilityId(this.mConnectionId, this.mWindowId, this.mParentNodeId);
  }
  
  public long getParentNodeId()
  {
    return this.mParentNodeId;
  }
  
  public RangeInfo getRangeInfo()
  {
    return this.mRangeInfo;
  }
  
  @UnsupportedAppUsage
  public long getSourceNodeId()
  {
    return this.mSourceNodeId;
  }
  
  public CharSequence getText()
  {
    Object localObject1 = this.mText;
    if ((localObject1 instanceof Spanned))
    {
      Object localObject2 = (Spanned)localObject1;
      localObject1 = (AccessibilityClickableSpan[])((Spanned)localObject2).getSpans(0, ((CharSequence)localObject1).length(), AccessibilityClickableSpan.class);
      for (int i = 0; i < localObject1.length; i++) {
        localObject1[i].copyConnectionDataFrom(this);
      }
      localObject2 = (AccessibilityURLSpan[])((Spanned)localObject2).getSpans(0, this.mText.length(), AccessibilityURLSpan.class);
      for (i = 0; i < localObject2.length; i++) {
        localObject2[i].copyConnectionDataFrom(this);
      }
    }
    return this.mText;
  }
  
  public int getTextSelectionEnd()
  {
    return this.mTextSelectionEnd;
  }
  
  public int getTextSelectionStart()
  {
    return this.mTextSelectionStart;
  }
  
  public CharSequence getTooltipText()
  {
    return this.mTooltipText;
  }
  
  public TouchDelegateInfo getTouchDelegateInfo()
  {
    TouchDelegateInfo localTouchDelegateInfo = this.mTouchDelegateInfo;
    if (localTouchDelegateInfo != null)
    {
      localTouchDelegateInfo.setConnectionId(this.mConnectionId);
      this.mTouchDelegateInfo.setWindowId(this.mWindowId);
    }
    return this.mTouchDelegateInfo;
  }
  
  public AccessibilityNodeInfo getTraversalAfter()
  {
    enforceSealed();
    return getNodeForAccessibilityId(this.mConnectionId, this.mWindowId, this.mTraversalAfter);
  }
  
  public AccessibilityNodeInfo getTraversalBefore()
  {
    enforceSealed();
    return getNodeForAccessibilityId(this.mConnectionId, this.mWindowId, this.mTraversalBefore);
  }
  
  public String getViewIdResourceName()
  {
    return this.mViewIdResourceName;
  }
  
  public AccessibilityWindowInfo getWindow()
  {
    enforceSealed();
    if (!canPerformRequestOverConnection(this.mConnectionId, this.mWindowId, this.mSourceNodeId)) {
      return null;
    }
    return AccessibilityInteractionClient.getInstance().getWindow(this.mConnectionId, this.mWindowId);
  }
  
  public int getWindowId()
  {
    return this.mWindowId;
  }
  
  public boolean hasExtras()
  {
    boolean bool;
    if (this.mExtras != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public int hashCode()
  {
    return ((1 * 31 + getAccessibilityViewId(this.mSourceNodeId)) * 31 + getVirtualDescendantId(this.mSourceNodeId)) * 31 + this.mWindowId;
  }
  
  public boolean isAccessibilityFocused()
  {
    return getBooleanProperty(1024);
  }
  
  public boolean isCheckable()
  {
    return getBooleanProperty(1);
  }
  
  public boolean isChecked()
  {
    return getBooleanProperty(2);
  }
  
  public boolean isClickable()
  {
    return getBooleanProperty(32);
  }
  
  public boolean isContentInvalid()
  {
    return getBooleanProperty(65536);
  }
  
  public boolean isContextClickable()
  {
    return getBooleanProperty(131072);
  }
  
  public boolean isDismissable()
  {
    return getBooleanProperty(16384);
  }
  
  public boolean isEditable()
  {
    return getBooleanProperty(4096);
  }
  
  public boolean isEnabled()
  {
    return getBooleanProperty(128);
  }
  
  public boolean isFocusable()
  {
    return getBooleanProperty(4);
  }
  
  public boolean isFocused()
  {
    return getBooleanProperty(8);
  }
  
  public boolean isHeading()
  {
    boolean bool1 = getBooleanProperty(2097152);
    boolean bool2 = true;
    if (bool1) {
      return true;
    }
    CollectionItemInfo localCollectionItemInfo = getCollectionItemInfo();
    if ((localCollectionItemInfo == null) || (!localCollectionItemInfo.mHeading)) {
      bool2 = false;
    }
    return bool2;
  }
  
  public boolean isImportantForAccessibility()
  {
    return getBooleanProperty(262144);
  }
  
  public boolean isLongClickable()
  {
    return getBooleanProperty(64);
  }
  
  public boolean isMultiLine()
  {
    return getBooleanProperty(32768);
  }
  
  public boolean isPassword()
  {
    return getBooleanProperty(256);
  }
  
  public boolean isScreenReaderFocusable()
  {
    return getBooleanProperty(524288);
  }
  
  public boolean isScrollable()
  {
    return getBooleanProperty(512);
  }
  
  @UnsupportedAppUsage
  public boolean isSealed()
  {
    return this.mSealed;
  }
  
  public boolean isSelected()
  {
    return getBooleanProperty(16);
  }
  
  public boolean isShowingHintText()
  {
    return getBooleanProperty(1048576);
  }
  
  public boolean isTextEntryKey()
  {
    return getBooleanProperty(4194304);
  }
  
  public boolean isVisibleToUser()
  {
    return getBooleanProperty(2048);
  }
  
  public boolean performAction(int paramInt)
  {
    enforceSealed();
    if (!canPerformRequestOverConnection(this.mConnectionId, this.mWindowId, this.mSourceNodeId)) {
      return false;
    }
    return AccessibilityInteractionClient.getInstance().performAccessibilityAction(this.mConnectionId, this.mWindowId, this.mSourceNodeId, paramInt, null);
  }
  
  public boolean performAction(int paramInt, Bundle paramBundle)
  {
    enforceSealed();
    if (!canPerformRequestOverConnection(this.mConnectionId, this.mWindowId, this.mSourceNodeId)) {
      return false;
    }
    return AccessibilityInteractionClient.getInstance().performAccessibilityAction(this.mConnectionId, this.mWindowId, this.mSourceNodeId, paramInt, paramBundle);
  }
  
  public void recycle()
  {
    clear();
    sPool.release(this);
    AtomicInteger localAtomicInteger = sNumInstancesInUse;
    if (localAtomicInteger != null) {
      localAtomicInteger.decrementAndGet();
    }
  }
  
  public boolean refresh()
  {
    return refresh(null, true);
  }
  
  @UnsupportedAppUsage
  public boolean refresh(Bundle paramBundle, boolean paramBoolean)
  {
    enforceSealed();
    if (!canPerformRequestOverConnection(this.mConnectionId, this.mWindowId, this.mSourceNodeId)) {
      return false;
    }
    paramBundle = AccessibilityInteractionClient.getInstance().findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mWindowId, this.mSourceNodeId, paramBoolean, 0, paramBundle);
    if (paramBundle == null) {
      return false;
    }
    enforceSealed();
    init(paramBundle);
    paramBundle.recycle();
    return true;
  }
  
  public boolean refreshWithExtraData(String paramString, Bundle paramBundle)
  {
    paramBundle.putString("android.view.accessibility.AccessibilityNodeInfo.extra_data_requested", paramString);
    return refresh(paramBundle, true);
  }
  
  @Deprecated
  public void removeAction(int paramInt)
  {
    enforceNotSealed();
    removeAction(getActionSingleton(paramInt));
  }
  
  public boolean removeAction(AccessibilityAction paramAccessibilityAction)
  {
    enforceNotSealed();
    ArrayList localArrayList = this.mActions;
    if ((localArrayList != null) && (paramAccessibilityAction != null)) {
      return localArrayList.remove(paramAccessibilityAction);
    }
    return false;
  }
  
  public void removeAllActions()
  {
    ArrayList localArrayList = this.mActions;
    if (localArrayList != null) {
      localArrayList.clear();
    }
  }
  
  public boolean removeChild(View paramView)
  {
    return removeChild(paramView, -1);
  }
  
  public boolean removeChild(View paramView, int paramInt)
  {
    enforceNotSealed();
    LongArray localLongArray = this.mChildNodeIds;
    if (localLongArray == null) {
      return false;
    }
    int i;
    if (paramView != null) {
      i = paramView.getAccessibilityViewId();
    } else {
      i = Integer.MAX_VALUE;
    }
    paramInt = localLongArray.indexOf(makeNodeId(i, paramInt));
    if (paramInt < 0) {
      return false;
    }
    localLongArray.remove(paramInt);
    return true;
  }
  
  public void setAccessibilityFocused(boolean paramBoolean)
  {
    setBooleanProperty(1024, paramBoolean);
  }
  
  public void setAvailableExtraData(List<String> paramList)
  {
    enforceNotSealed();
    this.mExtraDataKeys = new ArrayList(paramList);
  }
  
  @Deprecated
  public void setBoundsInParent(Rect paramRect)
  {
    enforceNotSealed();
    this.mBoundsInParent.set(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
  }
  
  public void setBoundsInScreen(Rect paramRect)
  {
    enforceNotSealed();
    this.mBoundsInScreen.set(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
  }
  
  public void setCanOpenPopup(boolean paramBoolean)
  {
    enforceNotSealed();
    setBooleanProperty(8192, paramBoolean);
  }
  
  public void setCheckable(boolean paramBoolean)
  {
    setBooleanProperty(1, paramBoolean);
  }
  
  public void setChecked(boolean paramBoolean)
  {
    setBooleanProperty(2, paramBoolean);
  }
  
  public void setClassName(CharSequence paramCharSequence)
  {
    enforceNotSealed();
    this.mClassName = paramCharSequence;
  }
  
  public void setClickable(boolean paramBoolean)
  {
    setBooleanProperty(32, paramBoolean);
  }
  
  public void setCollectionInfo(CollectionInfo paramCollectionInfo)
  {
    enforceNotSealed();
    this.mCollectionInfo = paramCollectionInfo;
  }
  
  public void setCollectionItemInfo(CollectionItemInfo paramCollectionItemInfo)
  {
    enforceNotSealed();
    this.mCollectionItemInfo = paramCollectionItemInfo;
  }
  
  public void setConnectionId(int paramInt)
  {
    enforceNotSealed();
    this.mConnectionId = paramInt;
  }
  
  public void setContentDescription(CharSequence paramCharSequence)
  {
    enforceNotSealed();
    if (paramCharSequence == null) {
      paramCharSequence = null;
    } else {
      paramCharSequence = paramCharSequence.subSequence(0, paramCharSequence.length());
    }
    this.mContentDescription = paramCharSequence;
  }
  
  public void setContentInvalid(boolean paramBoolean)
  {
    setBooleanProperty(65536, paramBoolean);
  }
  
  public void setContextClickable(boolean paramBoolean)
  {
    setBooleanProperty(131072, paramBoolean);
  }
  
  public void setDismissable(boolean paramBoolean)
  {
    setBooleanProperty(16384, paramBoolean);
  }
  
  public void setDrawingOrder(int paramInt)
  {
    enforceNotSealed();
    this.mDrawingOrderInParent = paramInt;
  }
  
  public void setEditable(boolean paramBoolean)
  {
    setBooleanProperty(4096, paramBoolean);
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    setBooleanProperty(128, paramBoolean);
  }
  
  public void setError(CharSequence paramCharSequence)
  {
    enforceNotSealed();
    if (paramCharSequence == null) {
      paramCharSequence = null;
    } else {
      paramCharSequence = paramCharSequence.subSequence(0, paramCharSequence.length());
    }
    this.mError = paramCharSequence;
  }
  
  public void setFocusable(boolean paramBoolean)
  {
    setBooleanProperty(4, paramBoolean);
  }
  
  public void setFocused(boolean paramBoolean)
  {
    setBooleanProperty(8, paramBoolean);
  }
  
  public void setHeading(boolean paramBoolean)
  {
    setBooleanProperty(2097152, paramBoolean);
  }
  
  public void setHintText(CharSequence paramCharSequence)
  {
    enforceNotSealed();
    if (paramCharSequence == null) {
      paramCharSequence = null;
    } else {
      paramCharSequence = paramCharSequence.subSequence(0, paramCharSequence.length());
    }
    this.mHintText = paramCharSequence;
  }
  
  public void setImportantForAccessibility(boolean paramBoolean)
  {
    setBooleanProperty(262144, paramBoolean);
  }
  
  public void setInputType(int paramInt)
  {
    enforceNotSealed();
    this.mInputType = paramInt;
  }
  
  public void setLabelFor(View paramView)
  {
    setLabelFor(paramView, -1);
  }
  
  public void setLabelFor(View paramView, int paramInt)
  {
    enforceNotSealed();
    int i;
    if (paramView != null) {
      i = paramView.getAccessibilityViewId();
    } else {
      i = Integer.MAX_VALUE;
    }
    this.mLabelForId = makeNodeId(i, paramInt);
  }
  
  public void setLabeledBy(View paramView)
  {
    setLabeledBy(paramView, -1);
  }
  
  public void setLabeledBy(View paramView, int paramInt)
  {
    enforceNotSealed();
    int i;
    if (paramView != null) {
      i = paramView.getAccessibilityViewId();
    } else {
      i = Integer.MAX_VALUE;
    }
    this.mLabeledById = makeNodeId(i, paramInt);
  }
  
  public void setLiveRegion(int paramInt)
  {
    enforceNotSealed();
    this.mLiveRegion = paramInt;
  }
  
  public void setLongClickable(boolean paramBoolean)
  {
    setBooleanProperty(64, paramBoolean);
  }
  
  public void setMaxTextLength(int paramInt)
  {
    enforceNotSealed();
    this.mMaxTextLength = paramInt;
  }
  
  public void setMovementGranularities(int paramInt)
  {
    enforceNotSealed();
    this.mMovementGranularities = paramInt;
  }
  
  public void setMultiLine(boolean paramBoolean)
  {
    setBooleanProperty(32768, paramBoolean);
  }
  
  public void setPackageName(CharSequence paramCharSequence)
  {
    enforceNotSealed();
    this.mPackageName = paramCharSequence;
  }
  
  public void setPaneTitle(CharSequence paramCharSequence)
  {
    enforceNotSealed();
    if (paramCharSequence == null) {
      paramCharSequence = null;
    } else {
      paramCharSequence = paramCharSequence.subSequence(0, paramCharSequence.length());
    }
    this.mPaneTitle = paramCharSequence;
  }
  
  public void setParent(View paramView)
  {
    setParent(paramView, -1);
  }
  
  public void setParent(View paramView, int paramInt)
  {
    enforceNotSealed();
    int i;
    if (paramView != null) {
      i = paramView.getAccessibilityViewId();
    } else {
      i = Integer.MAX_VALUE;
    }
    this.mParentNodeId = makeNodeId(i, paramInt);
  }
  
  public void setPassword(boolean paramBoolean)
  {
    setBooleanProperty(256, paramBoolean);
  }
  
  public void setRangeInfo(RangeInfo paramRangeInfo)
  {
    enforceNotSealed();
    this.mRangeInfo = paramRangeInfo;
  }
  
  public void setScreenReaderFocusable(boolean paramBoolean)
  {
    setBooleanProperty(524288, paramBoolean);
  }
  
  public void setScrollable(boolean paramBoolean)
  {
    setBooleanProperty(512, paramBoolean);
  }
  
  @UnsupportedAppUsage
  public void setSealed(boolean paramBoolean)
  {
    this.mSealed = paramBoolean;
  }
  
  public void setSelected(boolean paramBoolean)
  {
    setBooleanProperty(16, paramBoolean);
  }
  
  public void setShowingHintText(boolean paramBoolean)
  {
    setBooleanProperty(1048576, paramBoolean);
  }
  
  public void setSource(View paramView)
  {
    setSource(paramView, -1);
  }
  
  public void setSource(View paramView, int paramInt)
  {
    enforceNotSealed();
    int i = Integer.MAX_VALUE;
    if (paramView != null) {
      j = paramView.getAccessibilityWindowId();
    } else {
      j = Integer.MAX_VALUE;
    }
    this.mWindowId = j;
    int j = i;
    if (paramView != null) {
      j = paramView.getAccessibilityViewId();
    }
    this.mSourceNodeId = makeNodeId(j, paramInt);
  }
  
  public void setSourceNodeId(long paramLong, int paramInt)
  {
    enforceNotSealed();
    this.mSourceNodeId = paramLong;
    this.mWindowId = paramInt;
  }
  
  public void setText(CharSequence paramCharSequence)
  {
    enforceNotSealed();
    this.mOriginalText = paramCharSequence;
    if ((paramCharSequence instanceof Spanned))
    {
      ClickableSpan[] arrayOfClickableSpan = (ClickableSpan[])((Spanned)paramCharSequence).getSpans(0, paramCharSequence.length(), ClickableSpan.class);
      if (arrayOfClickableSpan.length > 0)
      {
        SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(paramCharSequence);
        for (int i = 0; i < arrayOfClickableSpan.length; i++)
        {
          paramCharSequence = arrayOfClickableSpan[i];
          if (((paramCharSequence instanceof AccessibilityClickableSpan)) || ((paramCharSequence instanceof AccessibilityURLSpan))) {
            break;
          }
          int j = localSpannableStringBuilder.getSpanStart(paramCharSequence);
          int k = localSpannableStringBuilder.getSpanEnd(paramCharSequence);
          int m = localSpannableStringBuilder.getSpanFlags(paramCharSequence);
          localSpannableStringBuilder.removeSpan(paramCharSequence);
          if ((paramCharSequence instanceof URLSpan)) {
            paramCharSequence = new AccessibilityURLSpan((URLSpan)paramCharSequence);
          } else {
            paramCharSequence = new AccessibilityClickableSpan(paramCharSequence.getId());
          }
          localSpannableStringBuilder.setSpan(paramCharSequence, j, k, m);
        }
        this.mText = localSpannableStringBuilder;
        return;
      }
    }
    if (paramCharSequence == null) {
      paramCharSequence = null;
    } else {
      paramCharSequence = paramCharSequence.subSequence(0, paramCharSequence.length());
    }
    this.mText = paramCharSequence;
  }
  
  public void setTextEntryKey(boolean paramBoolean)
  {
    setBooleanProperty(4194304, paramBoolean);
  }
  
  public void setTextSelection(int paramInt1, int paramInt2)
  {
    enforceNotSealed();
    this.mTextSelectionStart = paramInt1;
    this.mTextSelectionEnd = paramInt2;
  }
  
  public void setTooltipText(CharSequence paramCharSequence)
  {
    enforceNotSealed();
    if (paramCharSequence == null) {
      paramCharSequence = null;
    } else {
      paramCharSequence = paramCharSequence.subSequence(0, paramCharSequence.length());
    }
    this.mTooltipText = paramCharSequence;
  }
  
  public void setTouchDelegateInfo(TouchDelegateInfo paramTouchDelegateInfo)
  {
    enforceNotSealed();
    this.mTouchDelegateInfo = paramTouchDelegateInfo;
  }
  
  public void setTraversalAfter(View paramView)
  {
    setTraversalAfter(paramView, -1);
  }
  
  public void setTraversalAfter(View paramView, int paramInt)
  {
    enforceNotSealed();
    int i;
    if (paramView != null) {
      i = paramView.getAccessibilityViewId();
    } else {
      i = Integer.MAX_VALUE;
    }
    this.mTraversalAfter = makeNodeId(i, paramInt);
  }
  
  public void setTraversalBefore(View paramView)
  {
    setTraversalBefore(paramView, -1);
  }
  
  public void setTraversalBefore(View paramView, int paramInt)
  {
    enforceNotSealed();
    int i;
    if (paramView != null) {
      i = paramView.getAccessibilityViewId();
    } else {
      i = Integer.MAX_VALUE;
    }
    this.mTraversalBefore = makeNodeId(i, paramInt);
  }
  
  public void setViewIdResourceName(String paramString)
  {
    enforceNotSealed();
    this.mViewIdResourceName = paramString;
  }
  
  public void setVisibleToUser(boolean paramBoolean)
  {
    setBooleanProperty(2048, paramBoolean);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(super.toString());
    localStringBuilder.append("; boundsInParent: ");
    localStringBuilder.append(this.mBoundsInParent);
    localStringBuilder.append("; boundsInScreen: ");
    localStringBuilder.append(this.mBoundsInScreen);
    localStringBuilder.append("; packageName: ");
    localStringBuilder.append(this.mPackageName);
    localStringBuilder.append("; className: ");
    localStringBuilder.append(this.mClassName);
    localStringBuilder.append("; text: ");
    localStringBuilder.append(this.mText);
    localStringBuilder.append("; error: ");
    localStringBuilder.append(this.mError);
    localStringBuilder.append("; maxTextLength: ");
    localStringBuilder.append(this.mMaxTextLength);
    localStringBuilder.append("; contentDescription: ");
    localStringBuilder.append(this.mContentDescription);
    localStringBuilder.append("; tooltipText: ");
    localStringBuilder.append(this.mTooltipText);
    localStringBuilder.append("; viewIdResName: ");
    localStringBuilder.append(this.mViewIdResourceName);
    localStringBuilder.append("; checkable: ");
    localStringBuilder.append(isCheckable());
    localStringBuilder.append("; checked: ");
    localStringBuilder.append(isChecked());
    localStringBuilder.append("; focusable: ");
    localStringBuilder.append(isFocusable());
    localStringBuilder.append("; focused: ");
    localStringBuilder.append(isFocused());
    localStringBuilder.append("; selected: ");
    localStringBuilder.append(isSelected());
    localStringBuilder.append("; clickable: ");
    localStringBuilder.append(isClickable());
    localStringBuilder.append("; longClickable: ");
    localStringBuilder.append(isLongClickable());
    localStringBuilder.append("; contextClickable: ");
    localStringBuilder.append(isContextClickable());
    localStringBuilder.append("; enabled: ");
    localStringBuilder.append(isEnabled());
    localStringBuilder.append("; password: ");
    localStringBuilder.append(isPassword());
    localStringBuilder.append("; scrollable: ");
    localStringBuilder.append(isScrollable());
    localStringBuilder.append("; importantForAccessibility: ");
    localStringBuilder.append(isImportantForAccessibility());
    localStringBuilder.append("; visible: ");
    localStringBuilder.append(isVisibleToUser());
    localStringBuilder.append("; actions: ");
    localStringBuilder.append(this.mActions);
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    writeToParcelNoRecycle(paramParcel, paramInt);
    recycle();
  }
  
  public void writeToParcelNoRecycle(Parcel paramParcel, int paramInt)
  {
    long l1 = 0L;
    if (isSealed() != DEFAULT.isSealed()) {
      l1 = 0L | BitUtils.bitAt(0);
    }
    int i = 0 + 1;
    long l2 = l1;
    if (this.mSourceNodeId != DEFAULT.mSourceNodeId) {
      l2 = l1 | BitUtils.bitAt(i);
    }
    i++;
    l1 = l2;
    if (this.mWindowId != DEFAULT.mWindowId) {
      l1 = l2 | BitUtils.bitAt(i);
    }
    i++;
    long l3 = l1;
    if (this.mParentNodeId != DEFAULT.mParentNodeId) {
      l3 = l1 | BitUtils.bitAt(i);
    }
    i++;
    l2 = l3;
    if (this.mLabelForId != DEFAULT.mLabelForId) {
      l2 = l3 | BitUtils.bitAt(i);
    }
    i++;
    l1 = l2;
    if (this.mLabeledById != DEFAULT.mLabeledById) {
      l1 = l2 | BitUtils.bitAt(i);
    }
    i++;
    l2 = l1;
    if (this.mTraversalBefore != DEFAULT.mTraversalBefore) {
      l2 = l1 | BitUtils.bitAt(i);
    }
    i++;
    l1 = l2;
    if (this.mTraversalAfter != DEFAULT.mTraversalAfter) {
      l1 = l2 | BitUtils.bitAt(i);
    }
    i++;
    l2 = l1;
    if (this.mConnectionId != DEFAULT.mConnectionId) {
      l2 = l1 | BitUtils.bitAt(i);
    }
    i++;
    l3 = l2;
    if (!LongArray.elementsEqual(this.mChildNodeIds, DEFAULT.mChildNodeIds)) {
      l3 = l2 | BitUtils.bitAt(i);
    }
    i++;
    l1 = l3;
    if (!Objects.equals(this.mBoundsInParent, DEFAULT.mBoundsInParent)) {
      l1 = l3 | BitUtils.bitAt(i);
    }
    i++;
    l2 = l1;
    if (!Objects.equals(this.mBoundsInScreen, DEFAULT.mBoundsInScreen)) {
      l2 = l1 | BitUtils.bitAt(i);
    }
    i++;
    l3 = l2;
    if (!Objects.equals(this.mActions, DEFAULT.mActions)) {
      l3 = l2 | BitUtils.bitAt(i);
    }
    i++;
    l1 = l3;
    if (this.mMaxTextLength != DEFAULT.mMaxTextLength) {
      l1 = l3 | BitUtils.bitAt(i);
    }
    i++;
    l2 = l1;
    if (this.mMovementGranularities != DEFAULT.mMovementGranularities) {
      l2 = l1 | BitUtils.bitAt(i);
    }
    i++;
    l1 = l2;
    if (this.mBooleanProperties != DEFAULT.mBooleanProperties) {
      l1 = l2 | BitUtils.bitAt(i);
    }
    i++;
    l2 = l1;
    if (!Objects.equals(this.mPackageName, DEFAULT.mPackageName)) {
      l2 = l1 | BitUtils.bitAt(i);
    }
    i++;
    l3 = l2;
    if (!Objects.equals(this.mClassName, DEFAULT.mClassName)) {
      l3 = l2 | BitUtils.bitAt(i);
    }
    i++;
    l1 = l3;
    if (!Objects.equals(this.mText, DEFAULT.mText)) {
      l1 = l3 | BitUtils.bitAt(i);
    }
    i++;
    l2 = l1;
    if (!Objects.equals(this.mHintText, DEFAULT.mHintText)) {
      l2 = l1 | BitUtils.bitAt(i);
    }
    i++;
    l1 = l2;
    if (!Objects.equals(this.mError, DEFAULT.mError)) {
      l1 = l2 | BitUtils.bitAt(i);
    }
    i++;
    l3 = l1;
    if (!Objects.equals(this.mContentDescription, DEFAULT.mContentDescription)) {
      l3 = l1 | BitUtils.bitAt(i);
    }
    i++;
    l2 = l3;
    if (!Objects.equals(this.mPaneTitle, DEFAULT.mPaneTitle)) {
      l2 = l3 | BitUtils.bitAt(i);
    }
    i++;
    l1 = l2;
    if (!Objects.equals(this.mTooltipText, DEFAULT.mTooltipText)) {
      l1 = l2 | BitUtils.bitAt(i);
    }
    i++;
    l2 = l1;
    if (!Objects.equals(this.mViewIdResourceName, DEFAULT.mViewIdResourceName)) {
      l2 = l1 | BitUtils.bitAt(i);
    }
    i++;
    l1 = l2;
    if (this.mTextSelectionStart != DEFAULT.mTextSelectionStart) {
      l1 = l2 | BitUtils.bitAt(i);
    }
    i++;
    l2 = l1;
    if (this.mTextSelectionEnd != DEFAULT.mTextSelectionEnd) {
      l2 = l1 | BitUtils.bitAt(i);
    }
    i++;
    l1 = l2;
    if (this.mInputType != DEFAULT.mInputType) {
      l1 = l2 | BitUtils.bitAt(i);
    }
    i++;
    l2 = l1;
    if (this.mLiveRegion != DEFAULT.mLiveRegion) {
      l2 = l1 | BitUtils.bitAt(i);
    }
    i++;
    l3 = l2;
    if (this.mDrawingOrderInParent != DEFAULT.mDrawingOrderInParent) {
      l3 = l2 | BitUtils.bitAt(i);
    }
    i++;
    l1 = l3;
    if (!Objects.equals(this.mExtraDataKeys, DEFAULT.mExtraDataKeys)) {
      l1 = l3 | BitUtils.bitAt(i);
    }
    i++;
    l2 = l1;
    if (!Objects.equals(this.mExtras, DEFAULT.mExtras)) {
      l2 = l1 | BitUtils.bitAt(i);
    }
    i++;
    l1 = l2;
    if (!Objects.equals(this.mRangeInfo, DEFAULT.mRangeInfo)) {
      l1 = l2 | BitUtils.bitAt(i);
    }
    i++;
    l3 = l1;
    if (!Objects.equals(this.mCollectionInfo, DEFAULT.mCollectionInfo)) {
      l3 = l1 | BitUtils.bitAt(i);
    }
    i++;
    l2 = l3;
    if (!Objects.equals(this.mCollectionItemInfo, DEFAULT.mCollectionItemInfo)) {
      l2 = l3 | BitUtils.bitAt(i);
    }
    l1 = l2;
    if (!Objects.equals(this.mTouchDelegateInfo, DEFAULT.mTouchDelegateInfo)) {
      l1 = l2 | BitUtils.bitAt(i + 1);
    }
    paramParcel.writeLong(l1);
    i = 0 + 1;
    if (BitUtils.isBitSet(l1, 0)) {
      paramParcel.writeInt(isSealed());
    }
    int j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      paramParcel.writeLong(this.mSourceNodeId);
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      paramParcel.writeInt(this.mWindowId);
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      paramParcel.writeLong(this.mParentNodeId);
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      paramParcel.writeLong(this.mLabelForId);
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      paramParcel.writeLong(this.mLabeledById);
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      paramParcel.writeLong(this.mTraversalBefore);
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      paramParcel.writeLong(this.mTraversalAfter);
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      paramParcel.writeInt(this.mConnectionId);
    }
    j = i + 1;
    Object localObject;
    if (BitUtils.isBitSet(l1, i))
    {
      localObject = this.mChildNodeIds;
      if (localObject == null)
      {
        paramParcel.writeInt(0);
      }
      else
      {
        k = ((LongArray)localObject).size();
        paramParcel.writeInt(k);
        for (i = 0; i < k; i++) {
          paramParcel.writeLong(((LongArray)localObject).get(i));
        }
      }
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j))
    {
      paramParcel.writeInt(this.mBoundsInParent.top);
      paramParcel.writeInt(this.mBoundsInParent.bottom);
      paramParcel.writeInt(this.mBoundsInParent.left);
      paramParcel.writeInt(this.mBoundsInParent.right);
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i))
    {
      paramParcel.writeInt(this.mBoundsInScreen.top);
      paramParcel.writeInt(this.mBoundsInScreen.bottom);
      paramParcel.writeInt(this.mBoundsInScreen.left);
      paramParcel.writeInt(this.mBoundsInScreen.right);
    }
    int k = j + 1;
    if (BitUtils.isBitSet(l1, j))
    {
      localObject = this.mActions;
      if ((localObject != null) && (!((ArrayList)localObject).isEmpty()))
      {
        int m = this.mActions.size();
        j = 0;
        l2 = 0L;
        for (i = 0; i < m; i++)
        {
          localObject = (AccessibilityAction)this.mActions.get(i);
          if (isDefaultStandardAction((AccessibilityAction)localObject)) {
            l2 |= ((AccessibilityAction)localObject).mSerializationFlag;
          } else {
            j++;
          }
        }
        paramParcel.writeLong(l2);
        paramParcel.writeInt(j);
        for (i = 0; i < m; i++)
        {
          localObject = (AccessibilityAction)this.mActions.get(i);
          if (!isDefaultStandardAction((AccessibilityAction)localObject))
          {
            paramParcel.writeInt(((AccessibilityAction)localObject).getId());
            paramParcel.writeCharSequence(((AccessibilityAction)localObject).getLabel());
          }
        }
      }
      else
      {
        paramParcel.writeLong(0L);
        paramParcel.writeInt(0);
      }
    }
    j = k + 1;
    if (BitUtils.isBitSet(l1, k)) {
      paramParcel.writeInt(this.mMaxTextLength);
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      paramParcel.writeInt(this.mMovementGranularities);
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      paramParcel.writeInt(this.mBooleanProperties);
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      paramParcel.writeCharSequence(this.mPackageName);
    }
    k = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      paramParcel.writeCharSequence(this.mClassName);
    }
    j = k + 1;
    if (BitUtils.isBitSet(l1, k)) {
      paramParcel.writeCharSequence(this.mText);
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      paramParcel.writeCharSequence(this.mHintText);
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      paramParcel.writeCharSequence(this.mError);
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      paramParcel.writeCharSequence(this.mContentDescription);
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      paramParcel.writeCharSequence(this.mPaneTitle);
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      paramParcel.writeCharSequence(this.mTooltipText);
    }
    k = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      paramParcel.writeString(this.mViewIdResourceName);
    }
    j = k + 1;
    if (BitUtils.isBitSet(l1, k)) {
      paramParcel.writeInt(this.mTextSelectionStart);
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      paramParcel.writeInt(this.mTextSelectionEnd);
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      paramParcel.writeInt(this.mInputType);
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      paramParcel.writeInt(this.mLiveRegion);
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      paramParcel.writeInt(this.mDrawingOrderInParent);
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j)) {
      paramParcel.writeStringList(this.mExtraDataKeys);
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i)) {
      paramParcel.writeBundle(this.mExtras);
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j))
    {
      paramParcel.writeInt(this.mRangeInfo.getType());
      paramParcel.writeFloat(this.mRangeInfo.getMin());
      paramParcel.writeFloat(this.mRangeInfo.getMax());
      paramParcel.writeFloat(this.mRangeInfo.getCurrent());
    }
    j = i + 1;
    if (BitUtils.isBitSet(l1, i))
    {
      paramParcel.writeInt(this.mCollectionInfo.getRowCount());
      paramParcel.writeInt(this.mCollectionInfo.getColumnCount());
      paramParcel.writeInt(this.mCollectionInfo.isHierarchical());
      paramParcel.writeInt(this.mCollectionInfo.getSelectionMode());
    }
    i = j + 1;
    if (BitUtils.isBitSet(l1, j))
    {
      paramParcel.writeInt(this.mCollectionItemInfo.getRowIndex());
      paramParcel.writeInt(this.mCollectionItemInfo.getRowSpan());
      paramParcel.writeInt(this.mCollectionItemInfo.getColumnIndex());
      paramParcel.writeInt(this.mCollectionItemInfo.getColumnSpan());
      paramParcel.writeInt(this.mCollectionItemInfo.isHeading());
      paramParcel.writeInt(this.mCollectionItemInfo.isSelected());
    }
    if (BitUtils.isBitSet(l1, i)) {
      this.mTouchDelegateInfo.writeToParcel(paramParcel, paramInt);
    }
  }
  
  public static final class AccessibilityAction
  {
    public static final AccessibilityAction ACTION_ACCESSIBILITY_FOCUS;
    public static final AccessibilityAction ACTION_CLEAR_ACCESSIBILITY_FOCUS;
    public static final AccessibilityAction ACTION_CLEAR_FOCUS;
    public static final AccessibilityAction ACTION_CLEAR_SELECTION;
    public static final AccessibilityAction ACTION_CLICK;
    public static final AccessibilityAction ACTION_COLLAPSE;
    public static final AccessibilityAction ACTION_CONTEXT_CLICK;
    public static final AccessibilityAction ACTION_COPY;
    public static final AccessibilityAction ACTION_CUT;
    public static final AccessibilityAction ACTION_DISMISS;
    public static final AccessibilityAction ACTION_EXPAND;
    public static final AccessibilityAction ACTION_FOCUS;
    public static final AccessibilityAction ACTION_HIDE_TOOLTIP = new AccessibilityAction(16908357);
    public static final AccessibilityAction ACTION_LONG_CLICK;
    public static final AccessibilityAction ACTION_MOVE_WINDOW;
    public static final AccessibilityAction ACTION_NEXT_AT_MOVEMENT_GRANULARITY;
    public static final AccessibilityAction ACTION_NEXT_HTML_ELEMENT;
    public static final AccessibilityAction ACTION_PAGE_DOWN;
    public static final AccessibilityAction ACTION_PAGE_LEFT;
    public static final AccessibilityAction ACTION_PAGE_RIGHT;
    public static final AccessibilityAction ACTION_PAGE_UP;
    public static final AccessibilityAction ACTION_PASTE;
    public static final AccessibilityAction ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY;
    public static final AccessibilityAction ACTION_PREVIOUS_HTML_ELEMENT;
    public static final AccessibilityAction ACTION_SCROLL_BACKWARD;
    public static final AccessibilityAction ACTION_SCROLL_DOWN;
    public static final AccessibilityAction ACTION_SCROLL_FORWARD;
    public static final AccessibilityAction ACTION_SCROLL_LEFT;
    public static final AccessibilityAction ACTION_SCROLL_RIGHT;
    public static final AccessibilityAction ACTION_SCROLL_TO_POSITION;
    public static final AccessibilityAction ACTION_SCROLL_UP;
    public static final AccessibilityAction ACTION_SELECT;
    public static final AccessibilityAction ACTION_SET_PROGRESS;
    public static final AccessibilityAction ACTION_SET_SELECTION;
    public static final AccessibilityAction ACTION_SET_TEXT;
    public static final AccessibilityAction ACTION_SHOW_ON_SCREEN;
    public static final AccessibilityAction ACTION_SHOW_TOOLTIP;
    public static final ArraySet<AccessibilityAction> sStandardActions = new ArraySet();
    private final int mActionId;
    private final CharSequence mLabel;
    public long mSerializationFlag = -1L;
    
    static
    {
      ACTION_FOCUS = new AccessibilityAction(1);
      ACTION_CLEAR_FOCUS = new AccessibilityAction(2);
      ACTION_SELECT = new AccessibilityAction(4);
      ACTION_CLEAR_SELECTION = new AccessibilityAction(8);
      ACTION_CLICK = new AccessibilityAction(16);
      ACTION_LONG_CLICK = new AccessibilityAction(32);
      ACTION_ACCESSIBILITY_FOCUS = new AccessibilityAction(64);
      ACTION_CLEAR_ACCESSIBILITY_FOCUS = new AccessibilityAction(128);
      ACTION_NEXT_AT_MOVEMENT_GRANULARITY = new AccessibilityAction(256);
      ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY = new AccessibilityAction(512);
      ACTION_NEXT_HTML_ELEMENT = new AccessibilityAction(1024);
      ACTION_PREVIOUS_HTML_ELEMENT = new AccessibilityAction(2048);
      ACTION_SCROLL_FORWARD = new AccessibilityAction(4096);
      ACTION_SCROLL_BACKWARD = new AccessibilityAction(8192);
      ACTION_COPY = new AccessibilityAction(16384);
      ACTION_PASTE = new AccessibilityAction(32768);
      ACTION_CUT = new AccessibilityAction(65536);
      ACTION_SET_SELECTION = new AccessibilityAction(131072);
      ACTION_EXPAND = new AccessibilityAction(262144);
      ACTION_COLLAPSE = new AccessibilityAction(524288);
      ACTION_DISMISS = new AccessibilityAction(1048576);
      ACTION_SET_TEXT = new AccessibilityAction(2097152);
      ACTION_SHOW_ON_SCREEN = new AccessibilityAction(16908342);
      ACTION_SCROLL_TO_POSITION = new AccessibilityAction(16908343);
      ACTION_SCROLL_UP = new AccessibilityAction(16908344);
      ACTION_SCROLL_LEFT = new AccessibilityAction(16908345);
      ACTION_SCROLL_DOWN = new AccessibilityAction(16908346);
      ACTION_SCROLL_RIGHT = new AccessibilityAction(16908347);
      ACTION_PAGE_UP = new AccessibilityAction(16908358);
      ACTION_PAGE_DOWN = new AccessibilityAction(16908359);
      ACTION_PAGE_LEFT = new AccessibilityAction(16908360);
      ACTION_PAGE_RIGHT = new AccessibilityAction(16908361);
      ACTION_CONTEXT_CLICK = new AccessibilityAction(16908348);
      ACTION_SET_PROGRESS = new AccessibilityAction(16908349);
      ACTION_MOVE_WINDOW = new AccessibilityAction(16908354);
      ACTION_SHOW_TOOLTIP = new AccessibilityAction(16908356);
    }
    
    private AccessibilityAction(int paramInt)
    {
      this(paramInt, null);
      sStandardActions.add(this);
    }
    
    public AccessibilityAction(int paramInt, CharSequence paramCharSequence)
    {
      if (((0xFF000000 & paramInt) == 0) && (Integer.bitCount(paramInt) != 1)) {
        throw new IllegalArgumentException("Invalid standard action id");
      }
      this.mActionId = paramInt;
      this.mLabel = paramCharSequence;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = false;
      if (paramObject == null) {
        return false;
      }
      if (paramObject == this) {
        return true;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      if (this.mActionId == ((AccessibilityAction)paramObject).mActionId) {
        bool = true;
      }
      return bool;
    }
    
    public int getId()
    {
      return this.mActionId;
    }
    
    public CharSequence getLabel()
    {
      return this.mLabel;
    }
    
    public int hashCode()
    {
      return this.mActionId;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("AccessibilityAction: ");
      localStringBuilder.append(AccessibilityNodeInfo.getActionSymbolicName(this.mActionId));
      localStringBuilder.append(" - ");
      localStringBuilder.append(this.mLabel);
      return localStringBuilder.toString();
    }
  }
  
  public static final class CollectionInfo
  {
    private static final int MAX_POOL_SIZE = 20;
    public static final int SELECTION_MODE_MULTIPLE = 2;
    public static final int SELECTION_MODE_NONE = 0;
    public static final int SELECTION_MODE_SINGLE = 1;
    private static final Pools.SynchronizedPool<CollectionInfo> sPool = new Pools.SynchronizedPool(20);
    private int mColumnCount;
    private boolean mHierarchical;
    private int mRowCount;
    private int mSelectionMode;
    
    private CollectionInfo(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
    {
      this.mRowCount = paramInt1;
      this.mColumnCount = paramInt2;
      this.mHierarchical = paramBoolean;
      this.mSelectionMode = paramInt3;
    }
    
    private void clear()
    {
      this.mRowCount = 0;
      this.mColumnCount = 0;
      this.mHierarchical = false;
      this.mSelectionMode = 0;
    }
    
    public static CollectionInfo obtain(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      return obtain(paramInt1, paramInt2, paramBoolean, 0);
    }
    
    public static CollectionInfo obtain(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
    {
      CollectionInfo localCollectionInfo = (CollectionInfo)sPool.acquire();
      if (localCollectionInfo == null) {
        return new CollectionInfo(paramInt1, paramInt2, paramBoolean, paramInt3);
      }
      localCollectionInfo.mRowCount = paramInt1;
      localCollectionInfo.mColumnCount = paramInt2;
      localCollectionInfo.mHierarchical = paramBoolean;
      localCollectionInfo.mSelectionMode = paramInt3;
      return localCollectionInfo;
    }
    
    public static CollectionInfo obtain(CollectionInfo paramCollectionInfo)
    {
      return obtain(paramCollectionInfo.mRowCount, paramCollectionInfo.mColumnCount, paramCollectionInfo.mHierarchical, paramCollectionInfo.mSelectionMode);
    }
    
    public int getColumnCount()
    {
      return this.mColumnCount;
    }
    
    public int getRowCount()
    {
      return this.mRowCount;
    }
    
    public int getSelectionMode()
    {
      return this.mSelectionMode;
    }
    
    public boolean isHierarchical()
    {
      return this.mHierarchical;
    }
    
    void recycle()
    {
      clear();
      sPool.release(this);
    }
  }
  
  public static final class CollectionItemInfo
  {
    private static final int MAX_POOL_SIZE = 20;
    private static final Pools.SynchronizedPool<CollectionItemInfo> sPool = new Pools.SynchronizedPool(20);
    private int mColumnIndex;
    private int mColumnSpan;
    private boolean mHeading;
    private int mRowIndex;
    private int mRowSpan;
    private boolean mSelected;
    
    private CollectionItemInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2)
    {
      this.mRowIndex = paramInt1;
      this.mRowSpan = paramInt2;
      this.mColumnIndex = paramInt3;
      this.mColumnSpan = paramInt4;
      this.mHeading = paramBoolean1;
      this.mSelected = paramBoolean2;
    }
    
    private void clear()
    {
      this.mColumnIndex = 0;
      this.mColumnSpan = 0;
      this.mRowIndex = 0;
      this.mRowSpan = 0;
      this.mHeading = false;
      this.mSelected = false;
    }
    
    public static CollectionItemInfo obtain(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
    {
      return obtain(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean, false);
    }
    
    public static CollectionItemInfo obtain(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2)
    {
      CollectionItemInfo localCollectionItemInfo = (CollectionItemInfo)sPool.acquire();
      if (localCollectionItemInfo == null) {
        return new CollectionItemInfo(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean1, paramBoolean2);
      }
      localCollectionItemInfo.mRowIndex = paramInt1;
      localCollectionItemInfo.mRowSpan = paramInt2;
      localCollectionItemInfo.mColumnIndex = paramInt3;
      localCollectionItemInfo.mColumnSpan = paramInt4;
      localCollectionItemInfo.mHeading = paramBoolean1;
      localCollectionItemInfo.mSelected = paramBoolean2;
      return localCollectionItemInfo;
    }
    
    public static CollectionItemInfo obtain(CollectionItemInfo paramCollectionItemInfo)
    {
      return obtain(paramCollectionItemInfo.mRowIndex, paramCollectionItemInfo.mRowSpan, paramCollectionItemInfo.mColumnIndex, paramCollectionItemInfo.mColumnSpan, paramCollectionItemInfo.mHeading, paramCollectionItemInfo.mSelected);
    }
    
    public int getColumnIndex()
    {
      return this.mColumnIndex;
    }
    
    public int getColumnSpan()
    {
      return this.mColumnSpan;
    }
    
    public int getRowIndex()
    {
      return this.mRowIndex;
    }
    
    public int getRowSpan()
    {
      return this.mRowSpan;
    }
    
    public boolean isHeading()
    {
      return this.mHeading;
    }
    
    public boolean isSelected()
    {
      return this.mSelected;
    }
    
    void recycle()
    {
      clear();
      sPool.release(this);
    }
  }
  
  public static final class RangeInfo
  {
    private static final int MAX_POOL_SIZE = 10;
    public static final int RANGE_TYPE_FLOAT = 1;
    public static final int RANGE_TYPE_INT = 0;
    public static final int RANGE_TYPE_PERCENT = 2;
    private static final Pools.SynchronizedPool<RangeInfo> sPool = new Pools.SynchronizedPool(10);
    private float mCurrent;
    private float mMax;
    private float mMin;
    private int mType;
    
    private RangeInfo(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3)
    {
      this.mType = paramInt;
      this.mMin = paramFloat1;
      this.mMax = paramFloat2;
      this.mCurrent = paramFloat3;
    }
    
    private void clear()
    {
      this.mType = 0;
      this.mMin = 0.0F;
      this.mMax = 0.0F;
      this.mCurrent = 0.0F;
    }
    
    public static RangeInfo obtain(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3)
    {
      RangeInfo localRangeInfo = (RangeInfo)sPool.acquire();
      if (localRangeInfo == null) {
        return new RangeInfo(paramInt, paramFloat1, paramFloat2, paramFloat3);
      }
      localRangeInfo.mType = paramInt;
      localRangeInfo.mMin = paramFloat1;
      localRangeInfo.mMax = paramFloat2;
      localRangeInfo.mCurrent = paramFloat3;
      return localRangeInfo;
    }
    
    public static RangeInfo obtain(RangeInfo paramRangeInfo)
    {
      return obtain(paramRangeInfo.mType, paramRangeInfo.mMin, paramRangeInfo.mMax, paramRangeInfo.mCurrent);
    }
    
    public float getCurrent()
    {
      return this.mCurrent;
    }
    
    public float getMax()
    {
      return this.mMax;
    }
    
    public float getMin()
    {
      return this.mMin;
    }
    
    public int getType()
    {
      return this.mType;
    }
    
    void recycle()
    {
      clear();
      sPool.release(this);
    }
  }
  
  public static final class TouchDelegateInfo
    implements Parcelable
  {
    public static final Parcelable.Creator<TouchDelegateInfo> CREATOR = new Parcelable.Creator()
    {
      public AccessibilityNodeInfo.TouchDelegateInfo createFromParcel(Parcel paramAnonymousParcel)
      {
        int i = paramAnonymousParcel.readInt();
        if (i == 0) {
          return null;
        }
        ArrayMap localArrayMap = new ArrayMap(i);
        for (int j = 0; j < i; j++) {
          localArrayMap.put((Region)Region.CREATOR.createFromParcel(paramAnonymousParcel), Long.valueOf(paramAnonymousParcel.readLong()));
        }
        return new AccessibilityNodeInfo.TouchDelegateInfo(localArrayMap, false);
      }
      
      public AccessibilityNodeInfo.TouchDelegateInfo[] newArray(int paramAnonymousInt)
      {
        return new AccessibilityNodeInfo.TouchDelegateInfo[paramAnonymousInt];
      }
    };
    private int mConnectionId;
    private ArrayMap<Region, Long> mTargetMap;
    private int mWindowId;
    
    TouchDelegateInfo(ArrayMap<Region, Long> paramArrayMap, boolean paramBoolean)
    {
      boolean bool;
      if ((!paramArrayMap.isEmpty()) && (!paramArrayMap.containsKey(null)) && (!paramArrayMap.containsValue(null))) {
        bool = true;
      } else {
        bool = false;
      }
      Preconditions.checkArgument(bool);
      if (paramBoolean)
      {
        this.mTargetMap = new ArrayMap(paramArrayMap.size());
        this.mTargetMap.putAll(paramArrayMap);
      }
      else
      {
        this.mTargetMap = paramArrayMap;
      }
    }
    
    public TouchDelegateInfo(Map<Region, View> paramMap)
    {
      boolean bool;
      if ((!paramMap.isEmpty()) && (!paramMap.containsKey(null)) && (!paramMap.containsValue(null))) {
        bool = true;
      } else {
        bool = false;
      }
      Preconditions.checkArgument(bool);
      this.mTargetMap = new ArrayMap(paramMap.size());
      Iterator localIterator = paramMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        Region localRegion = (Region)localIterator.next();
        View localView = (View)paramMap.get(localRegion);
        this.mTargetMap.put(localRegion, Long.valueOf(localView.getAccessibilityViewId()));
      }
    }
    
    private void setConnectionId(int paramInt)
    {
      this.mConnectionId = paramInt;
    }
    
    private void setWindowId(int paramInt)
    {
      this.mWindowId = paramInt;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public long getAccessibilityIdForRegion(Region paramRegion)
    {
      return ((Long)this.mTargetMap.get(paramRegion)).longValue();
    }
    
    public Region getRegionAt(int paramInt)
    {
      return (Region)this.mTargetMap.keyAt(paramInt);
    }
    
    public int getRegionCount()
    {
      return this.mTargetMap.size();
    }
    
    public AccessibilityNodeInfo getTargetForRegion(Region paramRegion)
    {
      return AccessibilityNodeInfo.getNodeForAccessibilityId(this.mConnectionId, this.mWindowId, ((Long)this.mTargetMap.get(paramRegion)).longValue());
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mTargetMap.size());
      for (int i = 0; i < this.mTargetMap.size(); i++)
      {
        Region localRegion = (Region)this.mTargetMap.keyAt(i);
        Long localLong = (Long)this.mTargetMap.valueAt(i);
        localRegion.writeToParcel(paramParcel, paramInt);
        paramParcel.writeLong(localLong.longValue());
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/accessibility/AccessibilityNodeInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */