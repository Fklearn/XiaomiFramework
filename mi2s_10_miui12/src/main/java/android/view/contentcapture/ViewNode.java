package android.view.contentcapture;

import android.annotation.SystemApi;
import android.app.assist.AssistStructure.ViewNode;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.LocaleList;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStructure;
import android.view.ViewStructure.HtmlInfo;
import android.view.ViewStructure.HtmlInfo.Builder;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import com.android.internal.util.Preconditions;

@SystemApi
public final class ViewNode
  extends AssistStructure.ViewNode
{
  private static final long FLAGS_ACCESSIBILITY_FOCUSED = 131072L;
  private static final long FLAGS_ACTIVATED = 2097152L;
  private static final long FLAGS_ASSIST_BLOCKED = 1024L;
  private static final long FLAGS_CHECKABLE = 262144L;
  private static final long FLAGS_CHECKED = 524288L;
  private static final long FLAGS_CLICKABLE = 4096L;
  private static final long FLAGS_CONTEXT_CLICKABLE = 16384L;
  private static final long FLAGS_DISABLED = 2048L;
  private static final long FLAGS_FOCUSABLE = 32768L;
  private static final long FLAGS_FOCUSED = 65536L;
  private static final long FLAGS_HAS_AUTOFILL_HINTS = 8589934592L;
  private static final long FLAGS_HAS_AUTOFILL_ID = 32L;
  private static final long FLAGS_HAS_AUTOFILL_OPTIONS = 17179869184L;
  private static final long FLAGS_HAS_AUTOFILL_PARENT_ID = 64L;
  private static final long FLAGS_HAS_AUTOFILL_TYPE = 2147483648L;
  private static final long FLAGS_HAS_AUTOFILL_VALUE = 4294967296L;
  private static final long FLAGS_HAS_CLASSNAME = 16L;
  private static final long FLAGS_HAS_COMPLEX_TEXT = 2L;
  private static final long FLAGS_HAS_CONTENT_DESCRIPTION = 8388608L;
  private static final long FLAGS_HAS_EXTRAS = 16777216L;
  private static final long FLAGS_HAS_ID = 128L;
  private static final long FLAGS_HAS_INPUT_TYPE = 67108864L;
  private static final long FLAGS_HAS_LARGE_COORDS = 256L;
  private static final long FLAGS_HAS_LOCALE_LIST = 33554432L;
  private static final long FLAGS_HAS_MAX_TEXT_EMS = 268435456L;
  private static final long FLAGS_HAS_MAX_TEXT_LENGTH = 536870912L;
  private static final long FLAGS_HAS_MIN_TEXT_EMS = 134217728L;
  private static final long FLAGS_HAS_SCROLL = 512L;
  private static final long FLAGS_HAS_TEXT = 1L;
  private static final long FLAGS_HAS_TEXT_ID_ENTRY = 1073741824L;
  private static final long FLAGS_LONG_CLICKABLE = 8192L;
  private static final long FLAGS_OPAQUE = 4194304L;
  private static final long FLAGS_SELECTED = 1048576L;
  private static final long FLAGS_VISIBILITY_MASK = 12L;
  private static final String TAG = ViewNode.class.getSimpleName();
  private String[] mAutofillHints;
  private AutofillId mAutofillId;
  private CharSequence[] mAutofillOptions;
  private int mAutofillType;
  private AutofillValue mAutofillValue;
  private String mClassName;
  private CharSequence mContentDescription;
  private Bundle mExtras;
  private long mFlags;
  private int mHeight;
  private int mId = -1;
  private String mIdEntry;
  private String mIdPackage;
  private String mIdType;
  private int mInputType;
  private LocaleList mLocaleList;
  private int mMaxEms = -1;
  private int mMaxLength = -1;
  private int mMinEms = -1;
  private AutofillId mParentAutofillId;
  private int mScrollX;
  private int mScrollY;
  private ViewNodeText mText;
  private String mTextIdEntry;
  private int mWidth;
  private int mX;
  private int mY;
  
  public ViewNode()
  {
    this.mAutofillType = 0;
  }
  
  private ViewNode(long paramLong, Parcel paramParcel)
  {
    boolean bool = false;
    this.mAutofillType = 0;
    this.mFlags = paramLong;
    if ((0x20 & paramLong) != 0L) {
      this.mAutofillId = ((AutofillId)paramParcel.readParcelable(null));
    }
    if ((0x40 & paramLong) != 0L) {
      this.mParentAutofillId = ((AutofillId)paramParcel.readParcelable(null));
    }
    if ((1L & paramLong) != 0L)
    {
      if ((0x2 & paramLong) == 0L) {
        bool = true;
      }
      this.mText = new ViewNodeText(paramParcel, bool);
    }
    if ((0x10 & paramLong) != 0L) {
      this.mClassName = paramParcel.readString();
    }
    if ((0x80 & paramLong) != 0L)
    {
      this.mId = paramParcel.readInt();
      if (this.mId != -1)
      {
        this.mIdEntry = paramParcel.readString();
        if (this.mIdEntry != null)
        {
          this.mIdType = paramParcel.readString();
          this.mIdPackage = paramParcel.readString();
        }
      }
    }
    if ((0x100 & paramLong) != 0L)
    {
      this.mX = paramParcel.readInt();
      this.mY = paramParcel.readInt();
      this.mWidth = paramParcel.readInt();
      this.mHeight = paramParcel.readInt();
    }
    else
    {
      int i = paramParcel.readInt();
      this.mX = (i & 0x7FFF);
      this.mY = (i >> 16 & 0x7FFF);
      i = paramParcel.readInt();
      this.mWidth = (i & 0x7FFF);
      this.mHeight = (i >> 16 & 0x7FFF);
    }
    if ((0x200 & paramLong) != 0L)
    {
      this.mScrollX = paramParcel.readInt();
      this.mScrollY = paramParcel.readInt();
    }
    if ((0x800000 & paramLong) != 0L) {
      this.mContentDescription = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    }
    if ((0x1000000 & paramLong) != 0L) {
      this.mExtras = paramParcel.readBundle();
    }
    if ((0x2000000 & paramLong) != 0L) {
      this.mLocaleList = ((LocaleList)paramParcel.readParcelable(null));
    }
    if ((0x4000000 & paramLong) != 0L) {
      this.mInputType = paramParcel.readInt();
    }
    if ((0x8000000 & paramLong) != 0L) {
      this.mMinEms = paramParcel.readInt();
    }
    if ((0x10000000 & paramLong) != 0L) {
      this.mMaxEms = paramParcel.readInt();
    }
    if ((0x20000000 & paramLong) != 0L) {
      this.mMaxLength = paramParcel.readInt();
    }
    if ((0x40000000 & paramLong) != 0L) {
      this.mTextIdEntry = paramParcel.readString();
    }
    if ((0x80000000 & paramLong) != 0L) {
      this.mAutofillType = paramParcel.readInt();
    }
    if ((0x200000000 & paramLong) != 0L) {
      this.mAutofillHints = paramParcel.readStringArray();
    }
    if ((0x100000000 & paramLong) != 0L) {
      this.mAutofillValue = ((AutofillValue)paramParcel.readParcelable(null));
    }
    if ((0x400000000 & paramLong) != 0L) {
      this.mAutofillOptions = paramParcel.readCharSequenceArray();
    }
  }
  
  public static ViewNode readFromParcel(Parcel paramParcel)
  {
    long l = paramParcel.readLong();
    if (l == 0L) {
      paramParcel = null;
    } else {
      paramParcel = new ViewNode(l, paramParcel);
    }
    return paramParcel;
  }
  
  private void writeSelfToParcel(Parcel paramParcel, int paramInt)
  {
    long l1 = this.mFlags;
    long l2 = l1;
    if (this.mAutofillId != null) {
      l2 = l1 | 0x20;
    }
    l1 = l2;
    if (this.mParentAutofillId != null) {
      l1 = l2 | 0x40;
    }
    ViewNodeText localViewNodeText = this.mText;
    l2 = l1;
    if (localViewNodeText != null)
    {
      l1 |= 1L;
      l2 = l1;
      if (!localViewNodeText.isSimple()) {
        l2 = l1 | 0x2;
      }
    }
    l1 = l2;
    if (this.mClassName != null) {
      l1 = l2 | 0x10;
    }
    long l3 = l1;
    if (this.mId != -1) {
      l3 = l1 | 0x80;
    }
    if (((this.mX & 0x8000) == 0) && ((this.mY & 0x8000) == 0))
    {
      int i;
      if ((this.mWidth & 0x8000) != 0) {
        i = 1;
      } else {
        i = 0;
      }
      int j;
      if ((this.mHeight & 0x8000) != 0) {
        j = 1;
      } else {
        j = 0;
      }
      l2 = l3;
      if ((i | j) == 0) {}
    }
    else
    {
      l2 = l3 | 0x100;
    }
    if (this.mScrollX == 0)
    {
      l3 = l2;
      if (this.mScrollY == 0) {}
    }
    else
    {
      l3 = l2 | 0x200;
    }
    l1 = l3;
    if (this.mContentDescription != null) {
      l1 = l3 | 0x800000;
    }
    l2 = l1;
    if (this.mExtras != null) {
      l2 = l1 | 0x1000000;
    }
    l1 = l2;
    if (this.mLocaleList != null) {
      l1 = l2 | 0x2000000;
    }
    l2 = l1;
    if (this.mInputType != 0) {
      l2 = l1 | 0x4000000;
    }
    l1 = l2;
    if (this.mMinEms > -1) {
      l1 = l2 | 0x8000000;
    }
    l2 = l1;
    if (this.mMaxEms > -1) {
      l2 = l1 | 0x10000000;
    }
    l3 = l2;
    if (this.mMaxLength > -1) {
      l3 = l2 | 0x20000000;
    }
    l1 = l3;
    if (this.mTextIdEntry != null) {
      l1 = l3 | 0x40000000;
    }
    l2 = l1;
    if (this.mAutofillValue != null) {
      l2 = l1 | 0x100000000;
    }
    l1 = l2;
    if (this.mAutofillType != 0) {
      l1 = l2 | 0x80000000;
    }
    l2 = l1;
    if (this.mAutofillHints != null) {
      l2 = l1 | 0x200000000;
    }
    l1 = l2;
    if (this.mAutofillOptions != null) {
      l1 = l2 | 0x400000000;
    }
    paramParcel.writeLong(l1);
    if ((l1 & 0x20) != 0L) {
      paramParcel.writeParcelable(this.mAutofillId, paramInt);
    }
    if ((l1 & 0x40) != 0L) {
      paramParcel.writeParcelable(this.mParentAutofillId, paramInt);
    }
    if ((l1 & 1L) != 0L)
    {
      localViewNodeText = this.mText;
      boolean bool;
      if ((l1 & 0x2) == 0L) {
        bool = true;
      } else {
        bool = false;
      }
      localViewNodeText.writeToParcel(paramParcel, bool);
    }
    if ((0x10 & l1) != 0L) {
      paramParcel.writeString(this.mClassName);
    }
    if ((l1 & 0x80) != 0L)
    {
      paramParcel.writeInt(this.mId);
      if (this.mId != -1)
      {
        paramParcel.writeString(this.mIdEntry);
        if (this.mIdEntry != null)
        {
          paramParcel.writeString(this.mIdType);
          paramParcel.writeString(this.mIdPackage);
        }
      }
    }
    if ((l1 & 0x100) != 0L)
    {
      paramParcel.writeInt(this.mX);
      paramParcel.writeInt(this.mY);
      paramParcel.writeInt(this.mWidth);
      paramParcel.writeInt(this.mHeight);
    }
    else
    {
      paramParcel.writeInt(this.mY << 16 | this.mX);
      paramParcel.writeInt(this.mHeight << 16 | this.mWidth);
    }
    if ((l1 & 0x200) != 0L)
    {
      paramParcel.writeInt(this.mScrollX);
      paramParcel.writeInt(this.mScrollY);
    }
    if ((l1 & 0x800000) != 0L) {
      TextUtils.writeToParcel(this.mContentDescription, paramParcel, 0);
    }
    if ((l1 & 0x1000000) != 0L) {
      paramParcel.writeBundle(this.mExtras);
    }
    if ((0x2000000 & l1) != 0L) {
      paramParcel.writeParcelable(this.mLocaleList, 0);
    }
    if ((0x4000000 & l1) != 0L) {
      paramParcel.writeInt(this.mInputType);
    }
    if ((0x8000000 & l1) != 0L) {
      paramParcel.writeInt(this.mMinEms);
    }
    if ((0x10000000 & l1) != 0L) {
      paramParcel.writeInt(this.mMaxEms);
    }
    if ((0x20000000 & l1) != 0L) {
      paramParcel.writeInt(this.mMaxLength);
    }
    if ((0x40000000 & l1) != 0L) {
      paramParcel.writeString(this.mTextIdEntry);
    }
    if ((0x80000000 & l1) != 0L) {
      paramParcel.writeInt(this.mAutofillType);
    }
    if ((0x200000000 & l1) != 0L) {
      paramParcel.writeStringArray(this.mAutofillHints);
    }
    if ((0x100000000 & l1) != 0L) {
      paramParcel.writeParcelable(this.mAutofillValue, 0);
    }
    if ((0x400000000 & l1) != 0L) {
      paramParcel.writeCharSequenceArray(this.mAutofillOptions);
    }
  }
  
  public static void writeToParcel(Parcel paramParcel, ViewNode paramViewNode, int paramInt)
  {
    if (paramViewNode == null) {
      paramParcel.writeLong(0L);
    } else {
      paramViewNode.writeSelfToParcel(paramParcel, paramInt);
    }
  }
  
  public String[] getAutofillHints()
  {
    return this.mAutofillHints;
  }
  
  public AutofillId getAutofillId()
  {
    return this.mAutofillId;
  }
  
  public CharSequence[] getAutofillOptions()
  {
    return this.mAutofillOptions;
  }
  
  public int getAutofillType()
  {
    return this.mAutofillType;
  }
  
  public AutofillValue getAutofillValue()
  {
    return this.mAutofillValue;
  }
  
  public String getClassName()
  {
    return this.mClassName;
  }
  
  public CharSequence getContentDescription()
  {
    return this.mContentDescription;
  }
  
  public Bundle getExtras()
  {
    return this.mExtras;
  }
  
  public int getHeight()
  {
    return this.mHeight;
  }
  
  public String getHint()
  {
    Object localObject = this.mText;
    if (localObject != null) {
      localObject = ((ViewNodeText)localObject).mHint;
    } else {
      localObject = null;
    }
    return (String)localObject;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public String getIdEntry()
  {
    return this.mIdEntry;
  }
  
  public String getIdPackage()
  {
    return this.mIdPackage;
  }
  
  public String getIdType()
  {
    return this.mIdType;
  }
  
  public int getInputType()
  {
    return this.mInputType;
  }
  
  public int getLeft()
  {
    return this.mX;
  }
  
  public LocaleList getLocaleList()
  {
    return this.mLocaleList;
  }
  
  public int getMaxTextEms()
  {
    return this.mMaxEms;
  }
  
  public int getMaxTextLength()
  {
    return this.mMaxLength;
  }
  
  public int getMinTextEms()
  {
    return this.mMinEms;
  }
  
  public AutofillId getParentAutofillId()
  {
    return this.mParentAutofillId;
  }
  
  public int getScrollX()
  {
    return this.mScrollX;
  }
  
  public int getScrollY()
  {
    return this.mScrollY;
  }
  
  public CharSequence getText()
  {
    Object localObject = this.mText;
    if (localObject != null) {
      localObject = ((ViewNodeText)localObject).mText;
    } else {
      localObject = null;
    }
    return (CharSequence)localObject;
  }
  
  public int getTextBackgroundColor()
  {
    ViewNodeText localViewNodeText = this.mText;
    int i;
    if (localViewNodeText != null) {
      i = localViewNodeText.mTextBackgroundColor;
    } else {
      i = 1;
    }
    return i;
  }
  
  public int getTextColor()
  {
    ViewNodeText localViewNodeText = this.mText;
    int i;
    if (localViewNodeText != null) {
      i = localViewNodeText.mTextColor;
    } else {
      i = 1;
    }
    return i;
  }
  
  public String getTextIdEntry()
  {
    return this.mTextIdEntry;
  }
  
  public int[] getTextLineBaselines()
  {
    Object localObject = this.mText;
    if (localObject != null) {
      localObject = ((ViewNodeText)localObject).mLineBaselines;
    } else {
      localObject = null;
    }
    return (int[])localObject;
  }
  
  public int[] getTextLineCharOffsets()
  {
    Object localObject = this.mText;
    if (localObject != null) {
      localObject = ((ViewNodeText)localObject).mLineCharOffsets;
    } else {
      localObject = null;
    }
    return (int[])localObject;
  }
  
  public int getTextSelectionEnd()
  {
    ViewNodeText localViewNodeText = this.mText;
    int i;
    if (localViewNodeText != null) {
      i = localViewNodeText.mTextSelectionEnd;
    } else {
      i = -1;
    }
    return i;
  }
  
  public int getTextSelectionStart()
  {
    ViewNodeText localViewNodeText = this.mText;
    int i;
    if (localViewNodeText != null) {
      i = localViewNodeText.mTextSelectionStart;
    } else {
      i = -1;
    }
    return i;
  }
  
  public float getTextSize()
  {
    ViewNodeText localViewNodeText = this.mText;
    float f;
    if (localViewNodeText != null) {
      f = localViewNodeText.mTextSize;
    } else {
      f = 0.0F;
    }
    return f;
  }
  
  public int getTextStyle()
  {
    ViewNodeText localViewNodeText = this.mText;
    int i;
    if (localViewNodeText != null) {
      i = localViewNodeText.mTextStyle;
    } else {
      i = 0;
    }
    return i;
  }
  
  public int getTop()
  {
    return this.mY;
  }
  
  public int getVisibility()
  {
    return (int)(this.mFlags & 0xC);
  }
  
  public int getWidth()
  {
    return this.mWidth;
  }
  
  public boolean isAccessibilityFocused()
  {
    boolean bool;
    if ((this.mFlags & 0x20000) != 0L) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isActivated()
  {
    boolean bool;
    if ((this.mFlags & 0x200000) != 0L) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isAssistBlocked()
  {
    boolean bool;
    if ((this.mFlags & 0x400) != 0L) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isCheckable()
  {
    boolean bool;
    if ((this.mFlags & 0x40000) != 0L) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isChecked()
  {
    boolean bool;
    if ((this.mFlags & 0x80000) != 0L) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isClickable()
  {
    boolean bool;
    if ((this.mFlags & 0x1000) != 0L) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isContextClickable()
  {
    boolean bool;
    if ((this.mFlags & 0x4000) != 0L) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isEnabled()
  {
    boolean bool;
    if ((this.mFlags & 0x800) == 0L) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isFocusable()
  {
    boolean bool;
    if ((this.mFlags & 0x8000) != 0L) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isFocused()
  {
    boolean bool;
    if ((this.mFlags & 0x10000) != 0L) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isLongClickable()
  {
    boolean bool;
    if ((this.mFlags & 0x2000) != 0L) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isOpaque()
  {
    boolean bool;
    if ((this.mFlags & 0x400000) != 0L) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isSelected()
  {
    boolean bool;
    if ((this.mFlags & 0x100000) != 0L) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  static final class ViewNodeText
  {
    String mHint;
    int[] mLineBaselines;
    int[] mLineCharOffsets;
    CharSequence mText;
    int mTextBackgroundColor = 1;
    int mTextColor = 1;
    int mTextSelectionEnd;
    int mTextSelectionStart;
    float mTextSize;
    int mTextStyle;
    
    ViewNodeText() {}
    
    ViewNodeText(Parcel paramParcel, boolean paramBoolean)
    {
      this.mText = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      this.mTextSize = paramParcel.readFloat();
      this.mTextStyle = paramParcel.readInt();
      this.mTextColor = paramParcel.readInt();
      if (!paramBoolean)
      {
        this.mTextBackgroundColor = paramParcel.readInt();
        this.mTextSelectionStart = paramParcel.readInt();
        this.mTextSelectionEnd = paramParcel.readInt();
        this.mLineCharOffsets = paramParcel.createIntArray();
        this.mLineBaselines = paramParcel.createIntArray();
        this.mHint = paramParcel.readString();
      }
    }
    
    boolean isSimple()
    {
      int i = this.mTextBackgroundColor;
      boolean bool = true;
      if ((i != 1) || (this.mTextSelectionStart != 0) || (this.mTextSelectionEnd != 0) || (this.mLineCharOffsets != null) || (this.mLineBaselines != null) || (this.mHint != null)) {
        bool = false;
      }
      return bool;
    }
    
    void writeToParcel(Parcel paramParcel, boolean paramBoolean)
    {
      TextUtils.writeToParcel(this.mText, paramParcel, 0);
      paramParcel.writeFloat(this.mTextSize);
      paramParcel.writeInt(this.mTextStyle);
      paramParcel.writeInt(this.mTextColor);
      if (!paramBoolean)
      {
        paramParcel.writeInt(this.mTextBackgroundColor);
        paramParcel.writeInt(this.mTextSelectionStart);
        paramParcel.writeInt(this.mTextSelectionEnd);
        paramParcel.writeIntArray(this.mLineCharOffsets);
        paramParcel.writeIntArray(this.mLineBaselines);
        paramParcel.writeString(this.mHint);
      }
    }
  }
  
  public static final class ViewStructureImpl
    extends ViewStructure
  {
    final ViewNode mNode = new ViewNode();
    
    public ViewStructureImpl(View paramView)
    {
      ViewNode.access$002(this.mNode, ((View)Preconditions.checkNotNull(paramView)).getAutofillId());
      paramView = paramView.getParent();
      if ((paramView instanceof View)) {
        ViewNode.access$102(this.mNode, ((View)paramView).getAutofillId());
      }
    }
    
    public ViewStructureImpl(AutofillId paramAutofillId, long paramLong, int paramInt)
    {
      ViewNode.access$102(this.mNode, (AutofillId)Preconditions.checkNotNull(paramAutofillId));
      ViewNode.access$002(this.mNode, new AutofillId(paramAutofillId, paramLong, paramInt));
    }
    
    private ViewNode.ViewNodeText getNodeText()
    {
      if (this.mNode.mText != null) {
        return this.mNode.mText;
      }
      ViewNode.access$2702(this.mNode, new ViewNode.ViewNodeText());
      return this.mNode.mText;
    }
    
    public int addChildCount(int paramInt)
    {
      Log.w(ViewNode.TAG, "addChildCount() is not supported");
      return 0;
    }
    
    public void asyncCommit()
    {
      Log.w(ViewNode.TAG, "asyncCommit() is not supported");
    }
    
    public ViewStructure asyncNewChild(int paramInt)
    {
      Log.w(ViewNode.TAG, "asyncNewChild() is not supported");
      return null;
    }
    
    public AutofillId getAutofillId()
    {
      return this.mNode.mAutofillId;
    }
    
    public int getChildCount()
    {
      Log.w(ViewNode.TAG, "getChildCount() is not supported");
      return 0;
    }
    
    public Bundle getExtras()
    {
      if (this.mNode.mExtras != null) {
        return this.mNode.mExtras;
      }
      ViewNode.access$1702(this.mNode, new Bundle());
      return this.mNode.mExtras;
    }
    
    public CharSequence getHint()
    {
      return this.mNode.getHint();
    }
    
    public ViewNode getNode()
    {
      return this.mNode;
    }
    
    public Rect getTempRect()
    {
      Log.w(ViewNode.TAG, "getTempRect() is not supported");
      return null;
    }
    
    public CharSequence getText()
    {
      return this.mNode.getText();
    }
    
    public int getTextSelectionEnd()
    {
      return this.mNode.getTextSelectionEnd();
    }
    
    public int getTextSelectionStart()
    {
      return this.mNode.getTextSelectionStart();
    }
    
    public boolean hasExtras()
    {
      boolean bool;
      if (this.mNode.mExtras != null) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public ViewStructure newChild(int paramInt)
    {
      Log.w(ViewNode.TAG, "newChild() is not supported");
      return null;
    }
    
    public ViewStructure.HtmlInfo.Builder newHtmlInfoBuilder(String paramString)
    {
      Log.w(ViewNode.TAG, "newHtmlInfoBuilder() is not supported");
      return null;
    }
    
    public void setAccessibilityFocused(boolean paramBoolean)
    {
      ViewNode localViewNode = this.mNode;
      long l1 = localViewNode.mFlags;
      long l2;
      if (paramBoolean) {
        l2 = 131072L;
      } else {
        l2 = 0L;
      }
      ViewNode.access$1302(localViewNode, l1 & 0xFFFFFFFFFFFDFFFF | l2);
    }
    
    public void setActivated(boolean paramBoolean)
    {
      ViewNode localViewNode = this.mNode;
      long l1 = localViewNode.mFlags;
      long l2;
      if (paramBoolean) {
        l2 = 2097152L;
      } else {
        l2 = 0L;
      }
      ViewNode.access$1302(localViewNode, l1 & 0xFFFFFFFFFFDFFFFF | l2);
    }
    
    public void setAlpha(float paramFloat)
    {
      Log.w(ViewNode.TAG, "setAlpha() is not supported");
    }
    
    public void setAssistBlocked(boolean paramBoolean)
    {
      ViewNode localViewNode = this.mNode;
      long l1 = localViewNode.mFlags;
      long l2;
      if (paramBoolean) {
        l2 = 1024L;
      } else {
        l2 = 0L;
      }
      ViewNode.access$1302(localViewNode, l1 & 0xFFFFFFFFFFFFFBFF | l2);
    }
    
    public void setAutofillHints(String[] paramArrayOfString)
    {
      ViewNode.access$1902(this.mNode, paramArrayOfString);
    }
    
    public void setAutofillId(AutofillId paramAutofillId)
    {
      ViewNode.access$002(this.mNode, (AutofillId)Preconditions.checkNotNull(paramAutofillId));
    }
    
    public void setAutofillId(AutofillId paramAutofillId, int paramInt)
    {
      ViewNode.access$102(this.mNode, (AutofillId)Preconditions.checkNotNull(paramAutofillId));
      ViewNode.access$002(this.mNode, new AutofillId(paramAutofillId, paramInt));
    }
    
    public void setAutofillOptions(CharSequence[] paramArrayOfCharSequence)
    {
      ViewNode.access$2102(this.mNode, paramArrayOfCharSequence);
    }
    
    public void setAutofillType(int paramInt)
    {
      ViewNode.access$1802(this.mNode, paramInt);
    }
    
    public void setAutofillValue(AutofillValue paramAutofillValue)
    {
      ViewNode.access$2002(this.mNode, paramAutofillValue);
    }
    
    public void setCheckable(boolean paramBoolean)
    {
      ViewNode localViewNode = this.mNode;
      long l1 = localViewNode.mFlags;
      long l2;
      if (paramBoolean) {
        l2 = 262144L;
      } else {
        l2 = 0L;
      }
      ViewNode.access$1302(localViewNode, l1 & 0xFFFFFFFFFFFBFFFF | l2);
    }
    
    public void setChecked(boolean paramBoolean)
    {
      ViewNode localViewNode = this.mNode;
      long l1 = localViewNode.mFlags;
      long l2;
      if (paramBoolean) {
        l2 = 524288L;
      } else {
        l2 = 0L;
      }
      ViewNode.access$1302(localViewNode, l1 & 0xFFFFFFFFFFF7FFFF | l2);
    }
    
    public void setChildCount(int paramInt)
    {
      Log.w(ViewNode.TAG, "setChildCount() is not supported");
    }
    
    public void setClassName(String paramString)
    {
      ViewNode.access$1402(this.mNode, paramString);
    }
    
    public void setClickable(boolean paramBoolean)
    {
      ViewNode localViewNode = this.mNode;
      long l1 = localViewNode.mFlags;
      long l2;
      if (paramBoolean) {
        l2 = 4096L;
      } else {
        l2 = 0L;
      }
      ViewNode.access$1302(localViewNode, l1 & 0xFFFFFFFFFFFFEFFF | l2);
    }
    
    public void setContentDescription(CharSequence paramCharSequence)
    {
      ViewNode.access$1502(this.mNode, paramCharSequence);
    }
    
    public void setContextClickable(boolean paramBoolean)
    {
      ViewNode localViewNode = this.mNode;
      long l1 = localViewNode.mFlags;
      long l2;
      if (paramBoolean) {
        l2 = 16384L;
      } else {
        l2 = 0L;
      }
      ViewNode.access$1302(localViewNode, l1 & 0xFFFFFFFFFFFFBFFF | l2);
    }
    
    public void setDataIsSensitive(boolean paramBoolean)
    {
      Log.w(ViewNode.TAG, "setDataIsSensitive() is not supported");
    }
    
    public void setDimens(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    {
      ViewNode.access$602(this.mNode, paramInt1);
      ViewNode.access$702(this.mNode, paramInt2);
      ViewNode.access$802(this.mNode, paramInt3);
      ViewNode.access$902(this.mNode, paramInt4);
      ViewNode.access$1002(this.mNode, paramInt5);
      ViewNode.access$1102(this.mNode, paramInt6);
    }
    
    public void setElevation(float paramFloat)
    {
      Log.w(ViewNode.TAG, "setElevation() is not supported");
    }
    
    public void setEnabled(boolean paramBoolean)
    {
      ViewNode localViewNode = this.mNode;
      long l1 = localViewNode.mFlags;
      long l2;
      if (paramBoolean) {
        l2 = 0L;
      } else {
        l2 = 2048L;
      }
      ViewNode.access$1302(localViewNode, l1 & 0xFFFFFFFFFFFFF7FF | l2);
    }
    
    public void setFocusable(boolean paramBoolean)
    {
      ViewNode localViewNode = this.mNode;
      long l1 = localViewNode.mFlags;
      long l2;
      if (paramBoolean) {
        l2 = 32768L;
      } else {
        l2 = 0L;
      }
      ViewNode.access$1302(localViewNode, l1 & 0xFFFFFFFFFFFF7FFF | l2);
    }
    
    public void setFocused(boolean paramBoolean)
    {
      ViewNode localViewNode = this.mNode;
      long l1 = localViewNode.mFlags;
      long l2;
      if (paramBoolean) {
        l2 = 65536L;
      } else {
        l2 = 0L;
      }
      ViewNode.access$1302(localViewNode, l1 & 0xFFFFFFFFFFFEFFFF | l2);
    }
    
    public void setHint(CharSequence paramCharSequence)
    {
      ViewNode.ViewNodeText localViewNodeText = getNodeText();
      if (paramCharSequence != null) {
        paramCharSequence = paramCharSequence.toString();
      } else {
        paramCharSequence = null;
      }
      localViewNodeText.mHint = paramCharSequence;
    }
    
    public void setHtmlInfo(ViewStructure.HtmlInfo paramHtmlInfo)
    {
      Log.w(ViewNode.TAG, "setHtmlInfo() is not supported");
    }
    
    public void setId(int paramInt, String paramString1, String paramString2, String paramString3)
    {
      ViewNode.access$202(this.mNode, paramInt);
      ViewNode.access$302(this.mNode, paramString1);
      ViewNode.access$402(this.mNode, paramString2);
      ViewNode.access$502(this.mNode, paramString3);
    }
    
    public void setInputType(int paramInt)
    {
      ViewNode.access$2202(this.mNode, paramInt);
    }
    
    public void setLocaleList(LocaleList paramLocaleList)
    {
      ViewNode.access$2602(this.mNode, paramLocaleList);
    }
    
    public void setLongClickable(boolean paramBoolean)
    {
      ViewNode localViewNode = this.mNode;
      long l1 = localViewNode.mFlags;
      long l2;
      if (paramBoolean) {
        l2 = 8192L;
      } else {
        l2 = 0L;
      }
      ViewNode.access$1302(localViewNode, l1 & 0xFFFFFFFFFFFFDFFF | l2);
    }
    
    public void setMaxTextEms(int paramInt)
    {
      ViewNode.access$2402(this.mNode, paramInt);
    }
    
    public void setMaxTextLength(int paramInt)
    {
      ViewNode.access$2502(this.mNode, paramInt);
    }
    
    public void setMinTextEms(int paramInt)
    {
      ViewNode.access$2302(this.mNode, paramInt);
    }
    
    public void setOpaque(boolean paramBoolean)
    {
      ViewNode localViewNode = this.mNode;
      long l1 = localViewNode.mFlags;
      long l2;
      if (paramBoolean) {
        l2 = 4194304L;
      } else {
        l2 = 0L;
      }
      ViewNode.access$1302(localViewNode, l1 & 0xFFFFFFFFFFBFFFFF | l2);
    }
    
    public void setSelected(boolean paramBoolean)
    {
      ViewNode localViewNode = this.mNode;
      long l1 = localViewNode.mFlags;
      long l2;
      if (paramBoolean) {
        l2 = 1048576L;
      } else {
        l2 = 0L;
      }
      ViewNode.access$1302(localViewNode, l1 & 0xFFFFFFFFFFEFFFFF | l2);
    }
    
    public void setText(CharSequence paramCharSequence)
    {
      ViewNode.ViewNodeText localViewNodeText = getNodeText();
      localViewNodeText.mText = TextUtils.trimNoCopySpans(paramCharSequence);
      localViewNodeText.mTextSelectionEnd = -1;
      localViewNodeText.mTextSelectionStart = -1;
    }
    
    public void setText(CharSequence paramCharSequence, int paramInt1, int paramInt2)
    {
      ViewNode.ViewNodeText localViewNodeText = getNodeText();
      localViewNodeText.mText = TextUtils.trimNoCopySpans(paramCharSequence);
      localViewNodeText.mTextSelectionStart = paramInt1;
      localViewNodeText.mTextSelectionEnd = paramInt2;
    }
    
    public void setTextIdEntry(String paramString)
    {
      ViewNode.access$1602(this.mNode, (String)Preconditions.checkNotNull(paramString));
    }
    
    public void setTextLines(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    {
      ViewNode.ViewNodeText localViewNodeText = getNodeText();
      localViewNodeText.mLineCharOffsets = paramArrayOfInt1;
      localViewNodeText.mLineBaselines = paramArrayOfInt2;
    }
    
    public void setTextStyle(float paramFloat, int paramInt1, int paramInt2, int paramInt3)
    {
      ViewNode.ViewNodeText localViewNodeText = getNodeText();
      localViewNodeText.mTextColor = paramInt1;
      localViewNodeText.mTextBackgroundColor = paramInt2;
      localViewNodeText.mTextSize = paramFloat;
      localViewNodeText.mTextStyle = paramInt3;
    }
    
    public void setTransformation(Matrix paramMatrix)
    {
      Log.w(ViewNode.TAG, "setTransformation() is not supported");
    }
    
    public void setVisibility(int paramInt)
    {
      ViewNode localViewNode = this.mNode;
      ViewNode.access$1302(localViewNode, localViewNode.mFlags & 0xFFFFFFFFFFFFFFF3 | paramInt & 0xC);
    }
    
    public void setWebDomain(String paramString)
    {
      Log.w(ViewNode.TAG, "setWebDomain() is not supported");
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/contentcapture/ViewNode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */