package android.widget;

import android.R.styleable;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.UnsupportedAppUsage;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.UndoManager;
import android.content.UndoOperation;
import android.content.UndoOwner;
import android.content.pm.PackageManager;
import android.content.res.CompatibilityInfo;
import android.content.res.MiuiConfiguration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RenderNode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.ParcelableParcel;
import android.os.SystemClock;
import android.provider.Settings.Global;
import android.text.DynamicLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.ParcelableSpan;
import android.text.Selection;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.text.method.MetaKeyKeyListener;
import android.text.method.MovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.WordIterator;
import android.text.style.EasyEditSpan;
import android.text.style.SuggestionRangeSpan;
import android.text.style.SuggestionSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.MeasureSpec;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.ViewRootImpl;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.InternalInsetsInfo;
import android.view.ViewTreeObserver.OnComputeInternalInsetsListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.ViewTreeObserver.OnTouchModeChangeListener;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.phrase.Phrases.Utils;
import com.android.internal.phrase.QueryPhraseListener;
import com.android.internal.phrase.QueryPhraseTask;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import com.android.internal.view.menu.ActionMenu;
import com.android.internal.widget.EditableInputConnection;
import com.miui.internal.helper.MiuiVersionHelper;
import com.miui.translationservice.provider.TranslationResult;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import miui.os.Build;
import miui.util.HapticFeedbackUtil;
import miui.view.animation.BackEaseOutInterpolator;
import miui.view.animation.CubicEaseOutInterpolator;

public class Editor
{
  static final int BLINK = 500;
  private static final boolean DEBUG_UNDO = false;
  private static int DRAG_SHADOW_MAX_TEXT_LENGTH = 20;
  static final int EXTRACT_NOTHING = -2;
  static final int EXTRACT_UNKNOWN = -1;
  private static final boolean FLAG_USE_MAGNIFIER = true;
  public static final int HANDLE_TYPE_SELECTION_END = 1;
  public static final int HANDLE_TYPE_SELECTION_START = 0;
  private static final float LINE_SLOP_MULTIPLIER_FOR_HANDLEVIEWS = 0.5F;
  private static final int MENU_ITEM_ORDER_COPY = 4;
  private static final int MENU_ITEM_ORDER_CUT = 3;
  private static final int MENU_ITEM_ORDER_PASTE = 5;
  private static final int MENU_ITEM_ORDER_PASTE_AS_PLAIN_TEXT = 6;
  private static final int MENU_ITEM_ORDER_PROCESS_TEXT_INTENT_ACTIONS_START = 10;
  private static final int MENU_ITEM_ORDER_REDO = 2;
  private static final int MENU_ITEM_ORDER_REPLACE = 9;
  private static final int MENU_ITEM_ORDER_SELECT_ALL = 8;
  private static final int MENU_ITEM_ORDER_SHARE = 7;
  private static final int MENU_ITEM_ORDER_UNDO = 1;
  private static final String MOCK_CALLBACK_NAME = "Mock for Callback";
  private static final String TAG = "Editor";
  private static final int TAP_STATE_DOUBLE_TAP = 2;
  private static final int TAP_STATE_FIRST_TAP = 1;
  private static final int TAP_STATE_INITIAL = 0;
  private static final int TAP_STATE_TRIPLE_CLICK = 3;
  private static final float[] TEMP_POSITION = new float[2];
  private static final String UNDO_OWNER_TAG = "Editor";
  private static final int UNSET_LINE = -1;
  private static final int UNSET_X_VALUE = -1;
  private static final ActionMode mMockActionMode = new ActionMode()
  {
    public void finish() {}
    
    public View getCustomView()
    {
      return null;
    }
    
    public Menu getMenu()
    {
      return null;
    }
    
    public MenuInflater getMenuInflater()
    {
      return null;
    }
    
    public CharSequence getSubtitle()
    {
      return null;
    }
    
    public CharSequence getTitle()
    {
      return null;
    }
    
    public void invalidate() {}
    
    public void setCustomView(View paramAnonymousView) {}
    
    public void setSubtitle(int paramAnonymousInt) {}
    
    public void setSubtitle(CharSequence paramAnonymousCharSequence) {}
    
    public void setTitle(int paramAnonymousInt) {}
    
    public void setTitle(CharSequence paramAnonymousCharSequence) {}
  };
  private static DisplayMetrics sTmpDisplayMetrics;
  private boolean mAllowToStartActionMode = true;
  boolean mAllowUndo = true;
  private Blink mBlink;
  private float mContextMenuAnchorX;
  private float mContextMenuAnchorY;
  private CorrectionHighlighter mCorrectionHighlighter;
  @UnsupportedAppUsage
  boolean mCreatedWithASelection;
  boolean mCursorVisible = true;
  ActionMode.Callback mCustomInsertionActionModeCallback;
  ActionMode.Callback mCustomSelectionActionModeCallback;
  private boolean mCustomSelectionActionModeCallbackDestroyed = false;
  boolean mDiscardNextActionUp;
  Drawable mDrawableForCursor = null;
  private Runnable mEmailPopupShower;
  private EmailAddPopupWindow mEmailPopupWindow;
  CharSequence mError;
  private ErrorPopup mErrorPopup;
  boolean mErrorWasChanged;
  boolean mFirstTouchUp = true;
  boolean mFrozenWithFocus;
  boolean mIgnoreActionUpEvent;
  boolean mInBatchEditControllers;
  InputContentType mInputContentType;
  InputMethodState mInputMethodState;
  int mInputType = 0;
  private Runnable mInsertionActionModeRunnable;
  @UnsupportedAppUsage
  private boolean mInsertionControllerEnabled;
  private InsertionPointCursorController mInsertionPointCursorController;
  boolean mIsBeingLongClicked;
  boolean mIsInsertionActionModeStartPending = false;
  KeyListener mKeyListener;
  private int mLastButtonState;
  private float mLastDownPositionX;
  private float mLastDownPositionY;
  private long mLastTouchUpTime = 0L;
  private float mLastUpPositionX;
  private float mLastUpPositionY;
  private final MagnifierMotionAnimator mMagnifierAnimator;
  private PositionListener mPositionListener;
  private boolean mPreserveSelection;
  final ProcessTextIntentActionsHandler mProcessTextIntentActionsHandler;
  private boolean mRenderCursorRegardlessTiming;
  private boolean mRestartActionModeOnNextRefresh;
  boolean mSelectAllOnFocus;
  @UnsupportedAppUsage(maxTargetSdk=28)
  private Drawable mSelectHandleCenter;
  @UnsupportedAppUsage(maxTargetSdk=28)
  private Drawable mSelectHandleLeft;
  @UnsupportedAppUsage(maxTargetSdk=28)
  private Drawable mSelectHandleRight;
  private SelectionActionModeHelper mSelectionActionModeHelper;
  @UnsupportedAppUsage
  private boolean mSelectionControllerEnabled;
  SelectionModifierCursorController mSelectionModifierCursorController;
  boolean mSelectionMoved;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123769485L)
  private long mShowCursor;
  private boolean mShowErrorAfterAttach;
  @UnsupportedAppUsage
  boolean mShowSoftInputOnFocus = true;
  private Runnable mShowSuggestionRunnable;
  protected WeakReference<PinnedPopupWindow> mShownWindow;
  private SpanController mSpanController;
  SpellChecker mSpellChecker;
  SuggestionRangeSpan mSuggestionRangeSpan;
  private SuggestionsPopupWindow mSuggestionsPopupWindow;
  private int mTapState = 0;
  private Rect mTempRect;
  private ActionMode mTextActionMode;
  boolean mTextIsSelectable;
  private TextRenderNode[] mTextRenderNodes;
  private TextView mTextView;
  boolean mTouchFocusSelected;
  final UndoInputFilter mUndoInputFilter = new UndoInputFilter(this);
  private final UndoManager mUndoManager = new UndoManager();
  private UndoOwner mUndoOwner = this.mUndoManager.getOwner("Editor", this);
  private boolean mUpdateWordIteratorText;
  private WordIterator mWordIterator;
  private WordIterator mWordIteratorWithText;
  
  Editor(TextView paramTextView)
  {
    this.mTextView = paramTextView;
    paramTextView = this.mTextView;
    paramTextView.setFilters(paramTextView.getFilters());
    this.mProcessTextIntentActionsHandler = new ProcessTextIntentActionsHandler(this, null);
    this.mMagnifierAnimator = new MagnifierMotionAnimator(Magnifier.createBuilderWithOldMagnifierDefaults(this.mTextView).build(), null);
  }
  
  private boolean canSelectText()
  {
    boolean bool;
    if ((hasSelectionController()) && (this.mTextView.getText().length() != 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private void chooseSize(PopupWindow paramPopupWindow, CharSequence paramCharSequence, TextView paramTextView)
  {
    int i = paramTextView.getPaddingLeft();
    int j = paramTextView.getPaddingRight();
    int k = paramTextView.getPaddingTop();
    int m = paramTextView.getPaddingBottom();
    int n = this.mTextView.getResources().getDimensionPixelSize(17105503);
    paramCharSequence = new StaticLayout(paramCharSequence, paramTextView.getPaint(), n, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
    float f = 0.0F;
    for (n = 0; n < paramCharSequence.getLineCount(); n++) {
      f = Math.max(f, paramCharSequence.getLineWidth(n));
    }
    paramPopupWindow.setWidth((int)Math.ceil(f) + (i + j));
    paramPopupWindow.setHeight(paramCharSequence.getHeight() + (k + m));
  }
  
  private int clampHorizontalPosition(Drawable paramDrawable, float paramFloat)
  {
    float f = Math.max(0.5F, paramFloat - 0.5F);
    if (this.mTempRect == null) {
      this.mTempRect = new Rect();
    }
    int i = 0;
    if (paramDrawable != null)
    {
      paramDrawable.getPadding(this.mTempRect);
      i = paramDrawable.getIntrinsicWidth();
    }
    else
    {
      this.mTempRect.setEmpty();
    }
    int j = this.mTextView.getScrollX();
    paramFloat = f - j;
    int k = this.mTextView.getWidth() - this.mTextView.getCompoundPaddingLeft() - this.mTextView.getCompoundPaddingRight();
    if (paramFloat >= k - 1.0F) {
      i = k + j - (i - this.mTempRect.right);
    } else if ((Math.abs(paramFloat) > 1.0F) && ((!TextUtils.isEmpty(this.mTextView.getText())) || (1048576 - j > k + 1.0F) || (f > 1.0F))) {
      i = (int)f - this.mTempRect.left;
    } else {
      i = j - this.mTempRect.left;
    }
    return i;
  }
  
  private void discardTextDisplayLists()
  {
    if (this.mTextRenderNodes != null) {
      for (int i = 0;; i++)
      {
        Object localObject = this.mTextRenderNodes;
        if (i >= localObject.length) {
          break;
        }
        if (localObject[i] != null) {
          localObject = localObject[i].renderNode;
        } else {
          localObject = null;
        }
        if ((localObject != null) && (((RenderNode)localObject).hasDisplayList())) {
          ((RenderNode)localObject).discardDisplayList();
        }
      }
    }
  }
  
  private void downgradeEasyCorrectionSpans()
  {
    Object localObject = this.mTextView.getText();
    if ((localObject instanceof Spannable))
    {
      localObject = (Spannable)localObject;
      localObject = (SuggestionSpan[])((Spannable)localObject).getSpans(0, ((Spannable)localObject).length(), SuggestionSpan.class);
      for (int i = 0; i < localObject.length; i++)
      {
        int j = localObject[i].getFlags();
        if (((j & 0x1) != 0) && ((j & 0x2) == 0)) {
          localObject[i].setFlags(j & 0xFFFFFFFE);
        }
      }
    }
  }
  
  private void drawCursor(Canvas paramCanvas, int paramInt)
  {
    int i;
    if (paramInt != 0) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0) {
      paramCanvas.translate(0.0F, paramInt);
    }
    Drawable localDrawable = this.mDrawableForCursor;
    if (localDrawable != null) {
      localDrawable.draw(paramCanvas);
    }
    if (i != 0) {
      paramCanvas.translate(0.0F, -paramInt);
    }
  }
  
  private void drawHardwareAccelerated(Canvas paramCanvas, Layout paramLayout, Path paramPath, Paint paramPaint, int paramInt)
  {
    long l = paramLayout.getLineRangeForDraw(paramCanvas);
    int i = TextUtils.unpackRangeStartFromLong(l);
    int j = TextUtils.unpackRangeEndFromLong(l);
    if (j < 0) {
      return;
    }
    paramLayout.drawBackground(paramCanvas, paramPath, paramPaint, paramInt, i, j);
    if ((paramLayout instanceof DynamicLayout))
    {
      if (this.mTextRenderNodes == null) {
        this.mTextRenderNodes = ((TextRenderNode[])ArrayUtils.emptyArray(TextRenderNode.class));
      }
      paramPath = (DynamicLayout)paramLayout;
      int[] arrayOfInt = paramPath.getBlockEndLines();
      paramPaint = paramPath.getBlockIndices();
      int k = paramPath.getNumberOfBlocks();
      int m = paramPath.getIndexFirstChangedBlock();
      int n = 0;
      int i1 = -1;
      paramInt = 0;
      while (n < k)
      {
        int i2 = arrayOfInt[n];
        int i3 = paramPaint[n];
        int i4;
        if (i3 == -1) {
          i4 = 1;
        } else {
          i4 = 0;
        }
        if (i4 != 0)
        {
          i4 = getAvailableDisplayListIndex(paramPaint, k, paramInt);
          paramPaint[n] = i4;
          localObject1 = this.mTextRenderNodes;
          if (localObject1[i4] != null) {
            localObject1[i4].isDirty = true;
          }
          paramInt = i4 + 1;
        }
        else
        {
          i4 = i3;
        }
        Object localObject2 = this.mTextRenderNodes;
        if (localObject2[i4] == null)
        {
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append("Text ");
          ((StringBuilder)localObject1).append(i4);
          localObject2[i4] = new TextRenderNode(((StringBuilder)localObject1).toString());
        }
        boolean bool = this.mTextRenderNodes[i4].needsRecord();
        Object localObject1 = this.mTextRenderNodes[i4].renderNode;
        if ((n < m) && (!bool)) {
          break label531;
        }
        int i5 = i1 + 1;
        int i6 = paramLayout.getLineTop(i5);
        int i7 = paramLayout.getLineBottom(i2);
        i3 = this.mTextView.getWidth();
        float f1;
        float f2;
        if (this.mTextView.getHorizontallyScrolling())
        {
          f1 = Float.MAX_VALUE;
          i1 = i5;
          f2 = Float.MIN_VALUE;
          while (i1 <= i2)
          {
            f1 = Math.min(f1, paramLayout.getLineLeft(i1));
            f2 = Math.max(f2, paramLayout.getLineRight(i1));
            i1++;
          }
          i1 = (int)f1;
          i3 = (int)(0.5F + f2);
        }
        else
        {
          i1 = 0;
        }
        if (bool)
        {
          localObject2 = ((RenderNode)localObject1).start(i3 - i1, i7 - i6);
          f1 = -i1;
          f2 = -i6;
        }
        label531:
        try
        {
          ((RecordingCanvas)localObject2).translate(f1, f2);
          paramLayout.drawText((Canvas)localObject2, i5, i2);
          this.mTextRenderNodes[i4].isDirty = false;
          ((RenderNode)localObject1).end((RecordingCanvas)localObject2);
          ((RenderNode)localObject1).setClipToBounds(false);
        }
        finally
        {
          ((RenderNode)localObject1).end((RecordingCanvas)localObject2);
          ((RenderNode)localObject1).setClipToBounds(false);
        }
        i1 = i2;
        n++;
      }
      paramPath.setIndexFirstChangedBlock(k);
    }
    else
    {
      paramLayout.drawText(paramCanvas, i, j);
    }
  }
  
  private boolean extractTextInternal(ExtractedTextRequest paramExtractedTextRequest, int paramInt1, int paramInt2, int paramInt3, ExtractedText paramExtractedText)
  {
    if ((paramExtractedTextRequest != null) && (paramExtractedText != null))
    {
      CharSequence localCharSequence = this.mTextView.getText();
      if (localCharSequence == null) {
        return false;
      }
      if (paramInt1 != -2)
      {
        int i = localCharSequence.length();
        if (paramInt1 < 0)
        {
          paramExtractedText.partialEndOffset = -1;
          paramExtractedText.partialStartOffset = -1;
          paramInt3 = 0;
          paramInt2 = i;
        }
        else
        {
          paramInt2 += paramInt3;
          int j = paramInt1;
          int k = paramInt2;
          if ((localCharSequence instanceof Spanned))
          {
            Spanned localSpanned = (Spanned)localCharSequence;
            Object[] arrayOfObject = localSpanned.getSpans(paramInt1, paramInt2, ParcelableSpan.class);
            int m = arrayOfObject.length;
            for (;;)
            {
              j = paramInt1;
              k = paramInt2;
              if (m <= 0) {
                break;
              }
              m--;
              j = localSpanned.getSpanStart(arrayOfObject[m]);
              k = paramInt1;
              if (j < paramInt1) {
                k = j;
              }
              paramInt1 = localSpanned.getSpanEnd(arrayOfObject[m]);
              j = paramInt2;
              if (paramInt1 > paramInt2) {
                j = paramInt1;
              }
              paramInt1 = k;
              paramInt2 = j;
            }
          }
          paramExtractedText.partialStartOffset = j;
          paramExtractedText.partialEndOffset = (k - paramInt3);
          if (j > i)
          {
            paramInt1 = i;
          }
          else
          {
            paramInt1 = j;
            if (j < 0) {
              paramInt1 = 0;
            }
          }
          if (k > i)
          {
            paramInt2 = i;
            paramInt3 = paramInt1;
          }
          else
          {
            paramInt3 = paramInt1;
            paramInt2 = k;
            if (k < 0)
            {
              paramInt2 = 0;
              paramInt3 = paramInt1;
            }
          }
        }
        if ((paramExtractedTextRequest.flags & 0x1) != 0) {
          paramExtractedText.text = localCharSequence.subSequence(paramInt3, paramInt2);
        } else {
          paramExtractedText.text = TextUtils.substring(localCharSequence, paramInt3, paramInt2);
        }
      }
      else
      {
        paramExtractedText.partialStartOffset = 0;
        paramExtractedText.partialEndOffset = 0;
        paramExtractedText.text = "";
      }
      paramExtractedText.flags = 0;
      if (MetaKeyKeyListener.getMetaState(localCharSequence, 2048) != 0) {
        paramExtractedText.flags |= 0x2;
      }
      if (this.mTextView.isSingleLine()) {
        paramExtractedText.flags |= 0x1;
      }
      paramExtractedText.startOffset = 0;
      paramExtractedText.selectionStart = this.mTextView.getSelectionStart();
      paramExtractedText.selectionEnd = this.mTextView.getSelectionEnd();
      return true;
    }
    return false;
  }
  
  private Layout getActiveLayout()
  {
    Layout localLayout1 = this.mTextView.getLayout();
    Layout localLayout2 = this.mTextView.getHintLayout();
    Layout localLayout3 = localLayout1;
    if (TextUtils.isEmpty(localLayout1.getText()))
    {
      localLayout3 = localLayout1;
      if (localLayout2 != null)
      {
        localLayout3 = localLayout1;
        if (!TextUtils.isEmpty(localLayout2.getText())) {
          localLayout3 = localLayout2;
        }
      }
    }
    return localLayout3;
  }
  
  private int getAvailableDisplayListIndex(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int i = this.mTextRenderNodes.length;
    while (paramInt2 < i)
    {
      int j = 0;
      int m;
      for (int k = 0;; k++)
      {
        m = j;
        if (k >= paramInt1) {
          break;
        }
        if (paramArrayOfInt[k] == paramInt2)
        {
          m = 1;
          break;
        }
      }
      if (m != 0) {
        paramInt2++;
      } else {
        return paramInt2;
      }
    }
    this.mTextRenderNodes = ((TextRenderNode[])GrowingArrayUtils.append(this.mTextRenderNodes, i, null));
    return i;
  }
  
  private long getCharClusterRange(int paramInt)
  {
    if (paramInt < this.mTextView.getText().length())
    {
      paramInt = getNextCursorOffset(paramInt, true);
      return TextUtils.packRangeInLong(getNextCursorOffset(paramInt, false), paramInt);
    }
    if (paramInt - 1 >= 0)
    {
      paramInt = getNextCursorOffset(paramInt, false);
      return TextUtils.packRangeInLong(paramInt, getNextCursorOffset(paramInt, true));
    }
    return TextUtils.packRangeInLong(paramInt, paramInt);
  }
  
  private long getCharRange(int paramInt)
  {
    int i = this.mTextView.getText().length();
    if ((paramInt + 1 < i) && (Character.isSurrogatePair(this.mTextView.getText().charAt(paramInt), this.mTextView.getText().charAt(paramInt + 1)))) {
      return TextUtils.packRangeInLong(paramInt, paramInt + 2);
    }
    if (paramInt < i) {
      return TextUtils.packRangeInLong(paramInt, paramInt + 1);
    }
    if (paramInt - 2 >= 0)
    {
      char c = this.mTextView.getText().charAt(paramInt - 1);
      if (Character.isSurrogatePair(this.mTextView.getText().charAt(paramInt - 2), c)) {
        return TextUtils.packRangeInLong(paramInt - 2, paramInt);
      }
    }
    if (paramInt - 1 >= 0) {
      return TextUtils.packRangeInLong(paramInt - 1, paramInt);
    }
    return TextUtils.packRangeInLong(paramInt, paramInt);
  }
  
  private int getCurrentLineAdjustedForSlop(Layout paramLayout, int paramInt, float paramFloat)
  {
    int i = this.mTextView.getLineAtCoordinate(paramFloat);
    if ((paramLayout != null) && (paramInt <= paramLayout.getLineCount()) && (paramLayout.getLineCount() > 0) && (paramInt >= 0))
    {
      if (Math.abs(i - paramInt) >= 2) {
        return i;
      }
      float f1 = this.mTextView.viewportToContentVerticalOffset();
      i = paramLayout.getLineCount();
      float f2 = this.mTextView.getLineHeight() * 0.5F;
      float f3 = paramLayout.getLineTop(0);
      f3 = Math.max(paramLayout.getLineTop(paramInt) + f1 - f2, f3 + f1 + f2);
      float f4 = paramLayout.getLineBottom(i - 1);
      f2 = Math.min(paramLayout.getLineBottom(paramInt) + f1 + f2, f4 + f1 - f2);
      if (paramFloat <= f3) {
        paramInt = Math.max(paramInt - 1, 0);
      } else if (paramFloat >= f2) {
        paramInt = Math.min(paramInt + 1, i - 1);
      }
      return paramInt;
    }
    return i;
  }
  
  private static float getDescendantViewScale(View paramView)
  {
    float f = 1.0F * paramView.getScaleX();
    for (paramView = paramView.getParent(); (paramView instanceof View); paramView = paramView.getParent())
    {
      paramView = (View)paramView;
      if (paramView.getId() == 16908290) {
        break;
      }
      f *= paramView.getScaleX();
    }
    return f;
  }
  
  private int getDisplayHeightPixels()
  {
    if (Settings.Global.getInt(this.mTextView.getContext().getContentResolver(), "force_fsg_nav_bar", 0) == 1)
    {
      if (sTmpDisplayMetrics == null) {
        sTmpDisplayMetrics = new DisplayMetrics();
      }
      this.mTextView.getContext().getDisplay().getRealMetrics(sTmpDisplayMetrics);
      return sTmpDisplayMetrics.heightPixels;
    }
    return this.mTextView.getResources().getDisplayMetrics().heightPixels;
  }
  
  private int getErrorX()
  {
    float f = this.mTextView.getResources().getDisplayMetrics().density;
    TextView.Drawables localDrawables = this.mTextView.mDrawables;
    int i = this.mTextView.getLayoutDirection();
    int j = 0;
    int k = 0;
    if (i != 1)
    {
      if (localDrawables != null) {
        k = localDrawables.mDrawableSizeRight;
      }
      j = -k / 2;
      k = (int)(25.0F * f + 0.5F);
      k = this.mTextView.getWidth() - this.mErrorPopup.getWidth() - this.mTextView.getPaddingRight() + (j + k);
    }
    else
    {
      k = j;
      if (localDrawables != null) {
        k = localDrawables.mDrawableSizeLeft;
      }
      k /= 2;
      j = (int)(25.0F * f + 0.5F);
      k = this.mTextView.getPaddingLeft() + (k - j);
    }
    return k;
  }
  
  private int getErrorY()
  {
    int i = this.mTextView.getCompoundPaddingTop();
    int j = this.mTextView.getBottom();
    int k = this.mTextView.getTop();
    int m = this.mTextView.getCompoundPaddingBottom();
    TextView.Drawables localDrawables = this.mTextView.mDrawables;
    int n = this.mTextView.getLayoutDirection();
    int i1 = 0;
    int i2 = 0;
    if (n != 1)
    {
      if (localDrawables != null) {
        i2 = localDrawables.mDrawableHeightRight;
      }
    }
    else
    {
      i2 = i1;
      if (localDrawables != null) {
        i2 = localDrawables.mDrawableHeightLeft;
      }
    }
    i1 = (j - k - m - i - i2) / 2;
    float f = this.mTextView.getResources().getDisplayMetrics().density;
    return i1 + i + i2 - this.mTextView.getHeight() - (int)(2.0F * f + 0.5F);
  }
  
  private InputMethodManager getInputMethodManager()
  {
    return (InputMethodManager)this.mTextView.getContext().getSystemService(InputMethodManager.class);
  }
  
  private int getLastTapPosition()
  {
    SelectionModifierCursorController localSelectionModifierCursorController = this.mSelectionModifierCursorController;
    if (localSelectionModifierCursorController != null)
    {
      int i = localSelectionModifierCursorController.getMinTouchOffset();
      if (i >= 0)
      {
        int j = i;
        if (i > this.mTextView.getText().length()) {
          j = this.mTextView.getText().length();
        }
        return j;
      }
    }
    return -1;
  }
  
  private long getLastTouchOffsets()
  {
    SelectionModifierCursorController localSelectionModifierCursorController = getSelectionController();
    return TextUtils.packRangeInLong(localSelectionModifierCursorController.getMinTouchOffset(), localSelectionModifierCursorController.getMaxTouchOffset());
  }
  
  private int getNextCursorOffset(int paramInt, boolean paramBoolean)
  {
    Layout localLayout = this.mTextView.getLayout();
    if (localLayout == null) {
      return paramInt;
    }
    if (paramBoolean == localLayout.isRtlCharAt(paramInt)) {
      paramInt = localLayout.getOffsetToLeftOf(paramInt);
    } else {
      paramInt = localLayout.getOffsetToRightOf(paramInt);
    }
    return paramInt;
  }
  
  private long getParagraphsRange(int paramInt1, int paramInt2)
  {
    Layout localLayout = this.mTextView.getLayout();
    if (localLayout == null) {
      return TextUtils.packRangeInLong(-1, -1);
    }
    CharSequence localCharSequence = this.mTextView.getText();
    for (paramInt1 = localLayout.getLineForOffset(paramInt1); (paramInt1 > 0) && (localCharSequence.charAt(localLayout.getLineEnd(paramInt1 - 1) - 1) != '\n'); paramInt1--) {}
    for (paramInt2 = localLayout.getLineForOffset(paramInt2); (paramInt2 < localLayout.getLineCount() - 1) && (localCharSequence.charAt(localLayout.getLineEnd(paramInt2) - 1) != '\n'); paramInt2++) {}
    return TextUtils.packRangeInLong(localLayout.getLineStart(paramInt1), localLayout.getLineEnd(paramInt2));
  }
  
  private PositionListener getPositionListener()
  {
    if (this.mPositionListener == null) {
      this.mPositionListener = new PositionListener(null);
    }
    return this.mPositionListener;
  }
  
  private float getPrimaryHorizontal(Layout paramLayout1, Layout paramLayout2, int paramInt, boolean paramBoolean)
  {
    if ((TextUtils.isEmpty(paramLayout1.getText())) && (paramLayout2 != null) && (!TextUtils.isEmpty(paramLayout2.getText()))) {
      return paramLayout2.getPrimaryHorizontal(paramInt, paramBoolean);
    }
    return paramLayout1.getPrimaryHorizontal(paramInt, paramBoolean);
  }
  
  private SelectionActionModeHelper getSelectionActionModeHelper()
  {
    if (this.mSelectionActionModeHelper == null) {
      this.mSelectionActionModeHelper = new SelectionActionModeHelper(this);
    }
    return this.mSelectionActionModeHelper;
  }
  
  private View.DragShadowBuilder getTextThumbnailBuilder(int paramInt1, int paramInt2)
  {
    TextView localTextView = (TextView)View.inflate(this.mTextView.getContext(), 17367332, null);
    if (localTextView != null)
    {
      int i = DRAG_SHADOW_MAX_TEXT_LENGTH;
      int j = paramInt2;
      if (paramInt2 - paramInt1 > i) {
        j = TextUtils.unpackRangeEndFromLong(getCharClusterRange(i + paramInt1));
      }
      localTextView.setText(this.mTextView.getTransformedText(paramInt1, j));
      localTextView.setTextColor(this.mTextView.getTextColors());
      localTextView.setTextAppearance(16);
      localTextView.setGravity(17);
      localTextView.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
      paramInt1 = View.MeasureSpec.makeMeasureSpec(0, 0);
      localTextView.measure(paramInt1, paramInt1);
      localTextView.layout(0, 0, localTextView.getMeasuredWidth(), localTextView.getMeasuredHeight());
      localTextView.invalidate();
      return new View.DragShadowBuilder(localTextView);
    }
    throw new IllegalArgumentException("Unable to inflate text drag thumbnail");
  }
  
  private int getWordEnd(int paramInt)
  {
    int i = getWordIteratorWithText().nextBoundary(paramInt);
    if (getWordIteratorWithText().isAfterPunctuation(i)) {
      i = getWordIteratorWithText().getPunctuationEnd(paramInt);
    } else {
      i = getWordIteratorWithText().getNextWordEndOnTwoWordBoundary(paramInt);
    }
    if (i == -1) {
      return paramInt;
    }
    return i;
  }
  
  private WordIterator getWordIteratorWithText()
  {
    if (this.mWordIteratorWithText == null)
    {
      this.mWordIteratorWithText = new WordIterator(this.mTextView.getTextServicesLocale());
      this.mUpdateWordIteratorText = true;
    }
    if (this.mUpdateWordIteratorText)
    {
      CharSequence localCharSequence = this.mTextView.getText();
      this.mWordIteratorWithText.setCharSequence(localCharSequence, 0, localCharSequence.length());
      this.mUpdateWordIteratorText = false;
    }
    return this.mWordIteratorWithText;
  }
  
  private int getWordStart(int paramInt)
  {
    int i = getWordIteratorWithText().prevBoundary(paramInt);
    if (getWordIteratorWithText().isOnPunctuation(i)) {
      i = getWordIteratorWithText().getPunctuationBeginning(paramInt);
    } else {
      i = getWordIteratorWithText().getPrevWordBeginningOnTwoWordsBoundary(paramInt);
    }
    if (i == -1) {
      return paramInt;
    }
    return i;
  }
  
  private void handleEmailPopup(int paramInt)
  {
    if (!this.mTextView.isTextEditable())
    {
      hideEmailPopupWindow();
      return;
    }
    Object localObject = this.mTextView.getText();
    if (!TextUtils.isEmpty((CharSequence)localObject))
    {
      localObject = TextPatternUtil.findEmailAtPos(((CharSequence)localObject).toString(), paramInt);
      if (localObject != null)
      {
        showEmailPopupWindow((TextPatternUtil.EmailInfo)localObject);
        return;
      }
    }
    hideEmailPopupWindow();
  }
  
  private boolean hasPasswordTransformationMethod()
  {
    return this.mTextView.getTransformationMethod() instanceof PasswordTransformationMethod;
  }
  
  private void hideCursorControllers()
  {
    SuggestionsPopupWindow localSuggestionsPopupWindow = this.mSuggestionsPopupWindow;
    if ((localSuggestionsPopupWindow != null) && (!localSuggestionsPopupWindow.isShowingUp())) {
      this.mSuggestionsPopupWindow.hide();
    }
    hideInsertionPointCursorController();
    stopSelectionActionMode();
  }
  
  private void hideError()
  {
    ErrorPopup localErrorPopup = this.mErrorPopup;
    if ((localErrorPopup != null) && (localErrorPopup.isShowing())) {
      this.mErrorPopup.dismiss();
    }
    this.mShowErrorAfterAttach = false;
  }
  
  private void hideSpanControllers()
  {
    SpanController localSpanController = this.mSpanController;
    if (localSpanController != null) {
      localSpanController.hide();
    }
  }
  
  private static boolean isBigFontMode()
  {
    int i = MiuiConfiguration.getScaleMode();
    return (i == 11) || (i == 15);
  }
  
  private boolean isCursorInsideEasyCorrectionSpan()
  {
    SuggestionSpan[] arrayOfSuggestionSpan = (SuggestionSpan[])((Spannable)this.mTextView.getText()).getSpans(this.mTextView.getSelectionStart(), this.mTextView.getSelectionEnd(), SuggestionSpan.class);
    for (int i = 0; i < arrayOfSuggestionSpan.length; i++) {
      if ((arrayOfSuggestionSpan[i].getFlags() & 0x1) != 0) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isOffsetVisible(int paramInt)
  {
    Object localObject = this.mTextView.getLayout();
    if (localObject == null) {
      return false;
    }
    int i = ((Layout)localObject).getLineForOffset(paramInt);
    int j = ((Layout)localObject).getLineBottom(i);
    ((Layout)localObject).getLineTop(i);
    paramInt = (int)((Layout)localObject).getPrimaryHorizontal(paramInt);
    localObject = this.mTextView;
    return ((TextView)localObject).isPositionVisible(((TextView)localObject).viewportToContentHorizontalOffset() + paramInt, this.mTextView.viewportToContentVerticalOffset() + j);
  }
  
  private boolean isPositionOnText(float paramFloat1, float paramFloat2)
  {
    Layout localLayout = this.mTextView.getLayout();
    if (localLayout == null) {
      return false;
    }
    int i = this.mTextView.getLineAtCoordinate(paramFloat2);
    paramFloat1 = this.mTextView.convertToLocalHorizontalCoordinate(paramFloat1);
    if (paramFloat1 < localLayout.getLineLeft(i)) {
      return false;
    }
    return paramFloat1 <= localLayout.getLineRight(i);
  }
  
  private boolean isPositionVisible(int paramInt1, int paramInt2)
  {
    synchronized (TEMP_POSITION)
    {
      float[] arrayOfFloat2 = TEMP_POSITION;
      arrayOfFloat2[0] = paramInt1;
      arrayOfFloat2[1] = paramInt2;
      Object localObject1 = this.mTextView;
      while (localObject1 != null)
      {
        if (localObject1 != this.mTextView)
        {
          arrayOfFloat2[0] -= ((View)localObject1).getScrollX();
          arrayOfFloat2[1] -= ((View)localObject1).getScrollY();
        }
        if ((arrayOfFloat2[0] >= 0.0F) && (arrayOfFloat2[1] >= 0.0F) && (arrayOfFloat2[0] <= ((View)localObject1).getWidth()) && (arrayOfFloat2[1] <= ((View)localObject1).getHeight()))
        {
          if (!((View)localObject1).getMatrix().isIdentity()) {
            ((View)localObject1).getMatrix().mapPoints(arrayOfFloat2);
          }
          arrayOfFloat2[0] += ((View)localObject1).getLeft();
          arrayOfFloat2[1] += ((View)localObject1).getTop();
          localObject1 = ((View)localObject1).getParent();
          if ((localObject1 instanceof View)) {
            localObject1 = (View)localObject1;
          } else {
            localObject1 = null;
          }
        }
        else
        {
          return false;
        }
      }
      return true;
    }
  }
  
  private static boolean isValidRange(CharSequence paramCharSequence, int paramInt1, int paramInt2)
  {
    boolean bool;
    if ((paramInt1 >= 0) && (paramInt1 <= paramInt2) && (paramInt2 <= paramCharSequence.length())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private boolean needsToSelectAllToSelectWordOrParagraph()
  {
    if (this.mTextView.hasPasswordTransformationMethod()) {
      return true;
    }
    int i = this.mTextView.getInputType();
    int j = i & 0xF;
    i &= 0xFF0;
    return (j == 2) || (j == 3) || (j == 4) || (i == 16) || (i == 32) || (i == 208) || (i == 176);
  }
  
  private void resumeBlink()
  {
    Blink localBlink = this.mBlink;
    if (localBlink != null)
    {
      localBlink.uncancel();
      makeBlink();
    }
  }
  
  private boolean selectCurrentParagraph()
  {
    if (!this.mTextView.canSelectText()) {
      return false;
    }
    if (needsToSelectAllToSelectWordOrParagraph()) {
      return this.mTextView.selectAllText();
    }
    long l = getLastTouchOffsets();
    l = getParagraphsRange(TextUtils.unpackRangeStartFromLong(l), TextUtils.unpackRangeEndFromLong(l));
    int i = TextUtils.unpackRangeStartFromLong(l);
    int j = TextUtils.unpackRangeEndFromLong(l);
    if (i < j)
    {
      Selection.setSelection((Spannable)this.mTextView.getText(), i, j);
      return true;
    }
    return false;
  }
  
  private boolean selectCurrentWordAndStartDrag()
  {
    Runnable localRunnable = this.mInsertionActionModeRunnable;
    if (localRunnable != null) {
      this.mTextView.removeCallbacks(localRunnable);
    }
    if (extractedTextModeWillBeStarted()) {
      return false;
    }
    if (!checkField()) {
      return false;
    }
    if ((!this.mTextView.hasSelection()) && (!selectCurrentWord())) {
      return false;
    }
    stopTextActionModeWithPreservingSelection();
    getSelectionController().enterDrag(2);
    return true;
  }
  
  private void sendUpdateSelection()
  {
    Object localObject = this.mInputMethodState;
    if ((localObject != null) && (((InputMethodState)localObject).mBatchEditNesting <= 0))
    {
      InputMethodManager localInputMethodManager = getInputMethodManager();
      if (localInputMethodManager != null)
      {
        int i = this.mTextView.getSelectionStart();
        int j = this.mTextView.getSelectionEnd();
        int k;
        int m;
        if ((this.mTextView.getText() instanceof Spannable))
        {
          localObject = (Spannable)this.mTextView.getText();
          k = EditableInputConnection.getComposingSpanStart((Spannable)localObject);
          m = EditableInputConnection.getComposingSpanEnd((Spannable)localObject);
        }
        else
        {
          k = -1;
          m = -1;
        }
        localInputMethodManager.updateSelection(this.mTextView, i, j, k, m);
      }
    }
  }
  
  private void setErrorIcon(Drawable paramDrawable)
  {
    TextView.Drawables localDrawables1 = this.mTextView.mDrawables;
    TextView.Drawables localDrawables2 = localDrawables1;
    if (localDrawables1 == null)
    {
      TextView localTextView = this.mTextView;
      localDrawables1 = new TextView.Drawables(localTextView.getContext());
      localDrawables2 = localDrawables1;
      localTextView.mDrawables = localDrawables1;
    }
    localDrawables2.setErrorDrawable(paramDrawable, this.mTextView);
    this.mTextView.resetResolvedDrawables();
    this.mTextView.invalidate();
    this.mTextView.requestLayout();
  }
  
  private void setSelectionTranslation(int paramInt1, int paramInt2)
  {
    if (!hasSelectionController()) {
      return;
    }
    float[] arrayOfFloat = new float[2];
    float[] tmp13_12 = arrayOfFloat;
    tmp13_12[0] = 0.0F;
    float[] tmp17_13 = tmp13_12;
    tmp17_13[1] = 0.0F;
    tmp17_13;
    Layout localLayout = this.mTextView.getLayout();
    if ((localLayout != null) && (paramInt2 > paramInt1))
    {
      float f1 = localLayout.getPrimaryHorizontal(paramInt1);
      float f2 = localLayout.getPrimaryHorizontal(paramInt2);
      int i = Selection.getSelectionStart(this.mTextView.getText());
      if ((i >= paramInt1) && (i <= paramInt2))
      {
        float f3 = localLayout.getPrimaryHorizontal(i);
        if (i == paramInt1)
        {
          arrayOfFloat[1] = (f3 - f2);
        }
        else if (i == paramInt2)
        {
          arrayOfFloat[0] = (f3 - f1);
        }
        else
        {
          arrayOfFloat[0] = (f3 - f1);
          arrayOfFloat[1] = (f3 - f2);
        }
      }
      else
      {
        arrayOfFloat[0] = ((f2 - f1) / 2.0F);
        arrayOfFloat[1] = ((f1 - f2) / 2.0F);
      }
    }
    getSelectionController().setTranslationCache(arrayOfFloat);
  }
  
  private boolean shouldBlink()
  {
    boolean bool1 = isCursorVisible();
    boolean bool2 = false;
    if ((bool1) && (this.mTextView.isFocused()))
    {
      int i = this.mTextView.getSelectionStart();
      if (i < 0) {
        return false;
      }
      int j = this.mTextView.getSelectionEnd();
      if (j < 0) {
        return false;
      }
      if (i == j) {
        bool2 = true;
      }
      return bool2;
    }
    return false;
  }
  
  private boolean shouldFilterOutTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!paramMotionEvent.isFromSource(8194)) {
      return false;
    }
    int i;
    if (((this.mLastButtonState ^ paramMotionEvent.getButtonState()) & 0x1) != 0) {
      i = 1;
    } else {
      i = 0;
    }
    int j = paramMotionEvent.getActionMasked();
    if (((j == 0) || (j == 1)) && (i == 0)) {
      return true;
    }
    return (j == 2) && (!paramMotionEvent.isButtonPressed(1));
  }
  
  private boolean shouldOfferToShowSuggestions()
  {
    Object localObject = this.mTextView.getText();
    if (!(localObject instanceof Spannable)) {
      return false;
    }
    Spannable localSpannable = (Spannable)localObject;
    int i = this.mTextView.getSelectionStart();
    int j = this.mTextView.getSelectionEnd();
    localObject = (SuggestionSpan[])localSpannable.getSpans(i, j, SuggestionSpan.class);
    if (localObject.length == 0) {
      return false;
    }
    if (i == j)
    {
      for (j = 0; j < localObject.length; j++) {
        if (localObject[j].getSuggestions().length > 0) {
          return true;
        }
      }
      return false;
    }
    int k = this.mTextView.getText().length();
    int m = 0;
    int n = this.mTextView.getText().length();
    int i1 = 0;
    j = 0;
    int i2 = 0;
    while (i2 < localObject.length)
    {
      int i3 = localSpannable.getSpanStart(localObject[i2]);
      int i4 = localSpannable.getSpanEnd(localObject[i2]);
      k = Math.min(k, i3);
      m = Math.max(m, i4);
      int i5 = n;
      int i6 = i1;
      int i7 = j;
      if (i >= i3) {
        if (i > i4)
        {
          i5 = n;
          i6 = i1;
          i7 = j;
        }
        else
        {
          if ((j == 0) && (localObject[i2].getSuggestions().length <= 0)) {
            j = 0;
          } else {
            j = 1;
          }
          i5 = Math.min(n, i3);
          i6 = Math.max(i1, i4);
          i7 = j;
        }
      }
      i2++;
      n = i5;
      i1 = i6;
      j = i7;
    }
    if (j == 0) {
      return false;
    }
    if (n >= i1) {
      return false;
    }
    return (k >= n) && (m <= i1);
  }
  
  private void showError()
  {
    if (this.mTextView.getWindowToken() == null)
    {
      this.mShowErrorAfterAttach = true;
      return;
    }
    if (this.mErrorPopup == null)
    {
      localObject = (TextView)LayoutInflater.from(this.mTextView.getContext()).inflate(17367343, null);
      float f = this.mTextView.getResources().getDisplayMetrics().density;
      this.mErrorPopup = new ErrorPopup((TextView)localObject, (int)(200.0F * f + 0.5F), (int)(50.0F * f + 0.5F));
      this.mErrorPopup.setFocusable(false);
      this.mErrorPopup.setInputMethodMode(1);
    }
    Object localObject = (TextView)this.mErrorPopup.getContentView();
    chooseSize(this.mErrorPopup, this.mError, (TextView)localObject);
    ((TextView)localObject).setText(this.mError);
    this.mErrorPopup.showAsDropDown(this.mTextView, getErrorX(), getErrorY());
    localObject = this.mErrorPopup;
    ((ErrorPopup)localObject).fixDirection(((ErrorPopup)localObject).isAboveAnchor());
  }
  
  private void startActivityFromContext(Context paramContext, Intent paramIntent)
  {
    if ((paramContext instanceof Activity))
    {
      paramContext.startActivity(paramIntent);
    }
    else
    {
      paramIntent.addFlags(268435456);
      paramContext.startActivity(paramIntent);
    }
  }
  
  private void startDragAndDrop()
  {
    if (this.mTextView.isInExtractedMode()) {
      return;
    }
    int i = this.mTextView.getSelectionStart();
    int j = this.mTextView.getSelectionEnd();
    ClipData localClipData = ClipData.newPlainText(null, this.mTextView.getTransformedText(i, j));
    DragLocalState localDragLocalState = new DragLocalState(this.mTextView, i, j);
    this.mTextView.startDragAndDrop(localClipData, getTextThumbnailBuilder(i, j), localDragLocalState, 256);
    stopTextActionMode();
    if (hasSelectionController()) {
      getSelectionController().resetTouchOffsets();
    }
  }
  
  private void stopTextActionModeWithPreservingSelection()
  {
    if (this.mTextActionMode != null) {
      this.mRestartActionModeOnNextRefresh = true;
    }
    this.mPreserveSelection = true;
    stopTextActionMode();
    this.mPreserveSelection = false;
  }
  
  private void suspendBlink()
  {
    Blink localBlink = this.mBlink;
    if (localBlink != null) {
      localBlink.cancel();
    }
  }
  
  private boolean touchPositionIsInSelection()
  {
    int i = this.mTextView.getSelectionStart();
    int j = this.mTextView.getSelectionEnd();
    boolean bool1 = false;
    if (i == j) {
      return false;
    }
    int k = i;
    int m = j;
    if (i > j)
    {
      k = j;
      m = i;
      Selection.setSelection((Spannable)this.mTextView.getText(), k, m);
    }
    SelectionModifierCursorController localSelectionModifierCursorController = getSelectionController();
    i = localSelectionModifierCursorController.getMinTouchOffset();
    j = localSelectionModifierCursorController.getMaxTouchOffset();
    boolean bool2 = bool1;
    if (i >= k)
    {
      bool2 = bool1;
      if (j < m) {
        bool2 = true;
      }
    }
    return bool2;
  }
  
  private void updateCursorPosition(int paramInt1, int paramInt2, float paramFloat)
  {
    loadCursorDrawable();
    int i = clampHorizontalPosition(this.mDrawableForCursor, paramFloat);
    int j = this.mDrawableForCursor.getIntrinsicWidth();
    this.mDrawableForCursor.setBounds(i, paramInt1 - this.mTempRect.top, i + j, this.mTempRect.bottom + paramInt2);
  }
  
  private void updateFloatingToolbarVisibility(MotionEvent paramMotionEvent) {}
  
  private void updateSpellCheckSpans(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.mTextView.removeAdjacentSuggestionSpans(paramInt1);
    this.mTextView.removeAdjacentSuggestionSpans(paramInt2);
    if ((this.mTextView.isTextEditable()) && (this.mTextView.isSuggestionsEnabled()) && (!this.mTextView.isInExtractedMode()))
    {
      if ((this.mSpellChecker == null) && (paramBoolean)) {
        this.mSpellChecker = new SpellChecker(this.mTextView);
      }
      SpellChecker localSpellChecker = this.mSpellChecker;
      if (localSpellChecker != null) {
        localSpellChecker.spellCheck(paramInt1, paramInt2);
      }
    }
  }
  
  private void updateTapState(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getActionMasked();
    if (i == 0)
    {
      boolean bool = paramMotionEvent.isFromSource(8194);
      int j = this.mTapState;
      if (((j == 1) || ((j == 2) && (bool))) && (SystemClock.uptimeMillis() - this.mLastTouchUpTime <= ViewConfiguration.getDoubleTapTimeout()))
      {
        if (this.mTapState == 1) {
          this.mTapState = 2;
        } else {
          this.mTapState = 3;
        }
      }
      else {
        this.mTapState = 1;
      }
    }
    if (i == 1) {
      this.mLastTouchUpTime = SystemClock.uptimeMillis();
    }
  }
  
  public void addSpanWatchers(Spannable paramSpannable)
  {
    int i = paramSpannable.length();
    KeyListener localKeyListener = this.mKeyListener;
    if (localKeyListener != null) {
      paramSpannable.setSpan(localKeyListener, 0, i, 18);
    }
    if (this.mSpanController == null) {
      this.mSpanController = new SpanController();
    }
    paramSpannable.setSpan(this.mSpanController, 0, i, 18);
  }
  
  void adjustInputType(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    int i = this.mInputType;
    if ((i & 0xF) == 1)
    {
      if ((paramBoolean1) || (paramBoolean2)) {
        this.mInputType = (this.mInputType & 0xF00F | 0x80);
      }
      if (paramBoolean3) {
        this.mInputType = (this.mInputType & 0xF00F | 0xE0);
      }
    }
    else if (((i & 0xF) == 2) && (paramBoolean4))
    {
      this.mInputType = (i & 0xF00F | 0x10);
    }
  }
  
  boolean areSuggestionsShown()
  {
    SuggestionsPopupWindow localSuggestionsPopupWindow = this.mSuggestionsPopupWindow;
    boolean bool;
    if ((localSuggestionsPopupWindow != null) && (localSuggestionsPopupWindow.isShowing())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void beginBatchEdit()
  {
    this.mInBatchEditControllers = true;
    InputMethodState localInputMethodState = this.mInputMethodState;
    if (localInputMethodState != null)
    {
      int i = localInputMethodState.mBatchEditNesting + 1;
      localInputMethodState.mBatchEditNesting = i;
      if (i == 1)
      {
        localInputMethodState.mCursorChanged = false;
        localInputMethodState.mChangedDelta = 0;
        if (localInputMethodState.mContentChanged)
        {
          localInputMethodState.mChangedStart = 0;
          localInputMethodState.mChangedEnd = this.mTextView.getText().length();
        }
        else
        {
          localInputMethodState.mChangedStart = -1;
          localInputMethodState.mChangedEnd = -1;
          localInputMethodState.mContentChanged = false;
        }
        this.mUndoInputFilter.beginBatchEdit();
        this.mTextView.onBeginBatchEdit();
      }
    }
  }
  
  boolean canRedo()
  {
    boolean bool = true;
    UndoOwner localUndoOwner = this.mUndoOwner;
    if (this.mAllowUndo)
    {
      if (this.mUndoManager.countRedos(new UndoOwner[] { localUndoOwner }) > 0) {}
    }
    else {
      bool = false;
    }
    return bool;
  }
  
  boolean canUndo()
  {
    boolean bool = true;
    UndoOwner localUndoOwner = this.mUndoOwner;
    if (this.mAllowUndo)
    {
      if (this.mUndoManager.countUndos(new UndoOwner[] { localUndoOwner }) > 0) {}
    }
    else {
      bool = false;
    }
    return bool;
  }
  
  boolean checkField()
  {
    if ((this.mTextView.canSelectText()) && (this.mTextView.requestFocus())) {
      return true;
    }
    Log.w("TextView", "TextView does not support text selection. Selection cancelled.");
    return false;
  }
  
  boolean checkFieldAndSelectCurrentWord()
  {
    if ((this.mTextView.canSelectText()) && (this.mTextView.requestFocus()))
    {
      if (!this.mTextView.hasSelection()) {
        return selectCurrentWord();
      }
      return true;
    }
    Log.w("TextView", "TextView does not support text selection. Selection cancelled.");
    return false;
  }
  
  void createInputContentTypeIfNeeded()
  {
    if (this.mInputContentType == null) {
      this.mInputContentType = new InputContentType();
    }
  }
  
  void createInputMethodStateIfNeeded()
  {
    if (this.mInputMethodState == null) {
      this.mInputMethodState = new InputMethodState();
    }
  }
  
  public void endBatchEdit()
  {
    this.mInBatchEditControllers = false;
    InputMethodState localInputMethodState = this.mInputMethodState;
    if (localInputMethodState != null)
    {
      int i = localInputMethodState.mBatchEditNesting - 1;
      localInputMethodState.mBatchEditNesting = i;
      if (i == 0) {
        finishBatchEdit(localInputMethodState);
      }
    }
  }
  
  void ensureEndedBatchEdit()
  {
    InputMethodState localInputMethodState = this.mInputMethodState;
    if ((localInputMethodState != null) && (localInputMethodState.mBatchEditNesting != 0))
    {
      localInputMethodState.mBatchEditNesting = 0;
      finishBatchEdit(localInputMethodState);
    }
  }
  
  boolean extractText(ExtractedTextRequest paramExtractedTextRequest, ExtractedText paramExtractedText)
  {
    return extractTextInternal(paramExtractedTextRequest, -1, -1, -1, paramExtractedText);
  }
  
  boolean extractedTextModeWillBeStarted()
  {
    boolean bool1 = this.mTextView.isInExtractedMode();
    boolean bool2 = false;
    if (!bool1)
    {
      InputMethodManager localInputMethodManager = getInputMethodManager();
      bool1 = bool2;
      if (localInputMethodManager != null)
      {
        bool1 = bool2;
        if (localInputMethodManager.isFullscreenMode()) {
          bool1 = true;
        }
      }
      return bool1;
    }
    return false;
  }
  
  void finishBatchEdit(InputMethodState paramInputMethodState)
  {
    this.mTextView.onEndBatchEdit();
    this.mUndoInputFilter.endBatchEdit();
    if ((!paramInputMethodState.mContentChanged) && (!paramInputMethodState.mSelectionModeChanged))
    {
      if (paramInputMethodState.mCursorChanged) {
        this.mTextView.invalidateCursor();
      }
    }
    else
    {
      this.mTextView.updateAfterEdit();
      reportExtractedText();
    }
    sendUpdateSelection();
  }
  
  void forgetUndoRedo()
  {
    UndoOwner[] arrayOfUndoOwner = new UndoOwner[1];
    arrayOfUndoOwner[0] = this.mUndoOwner;
    this.mUndoManager.forgetUndos(arrayOfUndoOwner, -1);
    this.mUndoManager.forgetRedos(arrayOfUndoOwner, -1);
  }
  
  @VisibleForTesting
  public Drawable getCursorDrawable()
  {
    return this.mDrawableForCursor;
  }
  
  EmailAddPopupWindow getEmailPopupWindow()
  {
    if (this.mEmailPopupWindow == null)
    {
      SelectionModifierCursorController localSelectionModifierCursorController = getSelectionController();
      localSelectionModifierCursorController.initDrawables();
      localSelectionModifierCursorController.initHandleView();
      this.mEmailPopupWindow = new EmailAddPopupWindow(localSelectionModifierCursorController.mStartHandle);
    }
    return this.mEmailPopupWindow;
  }
  
  InsertionPointCursorController getInsertionController()
  {
    if (!this.mInsertionControllerEnabled) {
      return null;
    }
    if (this.mInsertionPointCursorController == null)
    {
      this.mInsertionPointCursorController = new InsertionPointCursorController(null);
      this.mTextView.getViewTreeObserver().addOnTouchModeChangeListener(this.mInsertionPointCursorController);
    }
    return this.mInsertionPointCursorController;
  }
  
  float getLastUpPositionX()
  {
    return this.mLastUpPositionX;
  }
  
  float getLastUpPositionY()
  {
    return this.mLastUpPositionY;
  }
  
  SelectionModifierCursorController getSelectionController()
  {
    if (!this.mSelectionControllerEnabled) {
      return null;
    }
    if (this.mSelectionModifierCursorController == null)
    {
      this.mSelectionModifierCursorController = new SelectionModifierCursorController();
      this.mTextView.getViewTreeObserver().addOnTouchModeChangeListener(this.mSelectionModifierCursorController);
    }
    return this.mSelectionModifierCursorController;
  }
  
  ActionMode getTextActionMode()
  {
    return this.mTextActionMode;
  }
  
  TextView getTextView()
  {
    return this.mTextView;
  }
  
  public WordIterator getWordIterator()
  {
    if (this.mWordIterator == null) {
      this.mWordIterator = new WordIterator(this.mTextView.getTextServicesLocale());
    }
    return this.mWordIterator;
  }
  
  boolean hasInsertionController()
  {
    return this.mInsertionControllerEnabled;
  }
  
  boolean hasSelectionController()
  {
    return this.mSelectionControllerEnabled;
  }
  
  void hideCursorAndSpanControllers()
  {
    hideCursorControllers();
    hideSpanControllers();
  }
  
  protected void hideEmailPopupWindow()
  {
    Object localObject = this.mEmailPopupShower;
    if (localObject != null) {
      this.mTextView.removeCallbacks((Runnable)localObject);
    }
    localObject = this.mEmailPopupWindow;
    if (localObject != null) {
      ((EmailAddPopupWindow)localObject).hide();
    }
  }
  
  void hideFloatingToolbar(int paramInt) {}
  
  void hideInsertionPointCursorController()
  {
    InsertionPointCursorController localInsertionPointCursorController = this.mInsertionPointCursorController;
    if (localInsertionPointCursorController != null) {
      localInsertionPointCursorController.hide();
    }
  }
  
  void invalidateActionModeAsync()
  {
    getSelectionActionModeHelper().invalidateActionModeAsync();
  }
  
  void invalidateHandlesAndActionMode()
  {
    Object localObject = this.mSelectionModifierCursorController;
    if (localObject != null) {
      ((SelectionModifierCursorController)localObject).invalidateHandles();
    }
    localObject = this.mInsertionPointCursorController;
    if (localObject != null) {
      ((InsertionPointCursorController)localObject).invalidateHandle();
    }
    localObject = this.mTextActionMode;
    if (localObject != null) {
      ((ActionMode)localObject).invalidate();
    }
  }
  
  @UnsupportedAppUsage
  void invalidateTextDisplayList()
  {
    if (this.mTextRenderNodes != null) {
      for (int i = 0;; i++)
      {
        TextRenderNode[] arrayOfTextRenderNode = this.mTextRenderNodes;
        if (i >= arrayOfTextRenderNode.length) {
          break;
        }
        if (arrayOfTextRenderNode[i] != null) {
          arrayOfTextRenderNode[i].isDirty = true;
        }
      }
    }
  }
  
  void invalidateTextDisplayList(Layout paramLayout, int paramInt1, int paramInt2)
  {
    if ((this.mTextRenderNodes != null) && ((paramLayout instanceof DynamicLayout)))
    {
      int i = paramLayout.getLineForOffset(paramInt1);
      int j = paramLayout.getLineForOffset(paramInt2);
      DynamicLayout localDynamicLayout = (DynamicLayout)paramLayout;
      int[] arrayOfInt = localDynamicLayout.getBlockEndLines();
      paramLayout = localDynamicLayout.getBlockIndices();
      int k = localDynamicLayout.getNumberOfBlocks();
      for (paramInt1 = 0;; paramInt1++)
      {
        paramInt2 = paramInt1;
        if (paramInt1 >= k) {
          break;
        }
        if (arrayOfInt[paramInt1] >= i)
        {
          paramInt2 = paramInt1;
          break;
        }
      }
      while (paramInt2 < k)
      {
        paramInt1 = paramLayout[paramInt2];
        if (paramInt1 != -1) {
          this.mTextRenderNodes[paramInt1].isDirty = true;
        }
        if (arrayOfInt[paramInt2] >= j) {
          break;
        }
        paramInt2++;
      }
    }
  }
  
  boolean isCursorVisible()
  {
    boolean bool;
    if ((this.mCursorVisible) && (this.mTextView.isTextEditable())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isInSwipeSelectionMode()
  {
    if (getSelectionController() != null) {
      return getSelectionController().mInSwipeSelectionMode;
    }
    return false;
  }
  
  boolean isPasswordInputType()
  {
    int i = this.mInputType & 0xFFF;
    boolean bool;
    if ((i != 129) && (i != 225) && (i != 18)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  void loadCursorDrawable()
  {
    if (this.mDrawableForCursor == null) {
      this.mDrawableForCursor = this.mTextView.getTextCursorDrawable();
    }
  }
  
  void loadHandleDrawables(boolean paramBoolean)
  {
    if ((this.mSelectHandleCenter == null) || (paramBoolean))
    {
      this.mSelectHandleCenter = this.mTextView.getTextSelectHandle();
      if (hasInsertionController()) {
        getInsertionController().reloadHandleDrawable();
      }
    }
    if ((this.mSelectHandleLeft == null) || (this.mSelectHandleRight == null) || (paramBoolean))
    {
      this.mSelectHandleLeft = this.mTextView.getTextSelectHandleLeft();
      this.mSelectHandleRight = this.mTextView.getTextSelectHandleRight();
      if (hasSelectionController()) {
        getSelectionController().reloadHandleDrawables();
      }
    }
  }
  
  void makeBlink()
  {
    if (shouldBlink())
    {
      this.mShowCursor = SystemClock.uptimeMillis();
      if (this.mBlink == null) {
        this.mBlink = new Blink(null);
      }
      this.mTextView.removeCallbacks(this.mBlink);
      this.mTextView.postDelayed(this.mBlink, 500L);
    }
    else
    {
      Blink localBlink = this.mBlink;
      if (localBlink != null) {
        this.mTextView.removeCallbacks(localBlink);
      }
    }
  }
  
  void onAttachedToWindow()
  {
    if (this.mShowErrorAfterAttach)
    {
      showError();
      this.mShowErrorAfterAttach = false;
    }
    ViewTreeObserver localViewTreeObserver = this.mTextView.getViewTreeObserver();
    Object localObject = this.mInsertionPointCursorController;
    if (localObject != null) {
      localViewTreeObserver.addOnTouchModeChangeListener((ViewTreeObserver.OnTouchModeChangeListener)localObject);
    }
    localObject = this.mSelectionModifierCursorController;
    if (localObject != null)
    {
      ((SelectionModifierCursorController)localObject).resetTouchOffsets();
      localViewTreeObserver.addOnTouchModeChangeListener(this.mSelectionModifierCursorController);
    }
    updateSpellCheckSpans(0, this.mTextView.getText().length(), true);
    if ((this.mTextView.hasTransientState()) && (this.mTextView.getSelectionStart() != this.mTextView.getSelectionEnd()))
    {
      this.mTextView.setHasTransientState(false);
      startSelectionActionMode();
    }
  }
  
  public void onCommitCorrection(CorrectionInfo paramCorrectionInfo)
  {
    CorrectionHighlighter localCorrectionHighlighter = this.mCorrectionHighlighter;
    if (localCorrectionHighlighter == null) {
      this.mCorrectionHighlighter = new CorrectionHighlighter();
    } else {
      localCorrectionHighlighter.invalidate(false);
    }
    this.mCorrectionHighlighter.highlight(paramCorrectionInfo);
    this.mUndoInputFilter.freezeLastEdit();
  }
  
  void onCreateContextMenu(ContextMenu paramContextMenu) {}
  
  void onDetachedFromWindow()
  {
    if (this.mError != null) {
      hideError();
    }
    Object localObject = this.mBlink;
    if (localObject != null) {
      this.mTextView.removeCallbacks((Runnable)localObject);
    }
    localObject = this.mInsertionPointCursorController;
    if (localObject != null) {
      ((InsertionPointCursorController)localObject).onDetached();
    }
    localObject = this.mSelectionModifierCursorController;
    if (localObject != null) {
      ((SelectionModifierCursorController)localObject).onDetached();
    }
    localObject = this.mShowSuggestionRunnable;
    if (localObject != null) {
      this.mTextView.removeCallbacks((Runnable)localObject);
    }
    discardTextDisplayLists();
    localObject = this.mSpellChecker;
    if (localObject != null)
    {
      ((SpellChecker)localObject).closeSession();
      this.mSpellChecker = null;
    }
    hideCursorAndSpanControllers();
    stopTextActionModeWithPreservingSelection();
  }
  
  void onDraw(Canvas paramCanvas, Layout paramLayout, Path paramPath, Paint paramPaint, int paramInt)
  {
    int i = this.mTextView.getSelectionStart();
    int j = this.mTextView.getSelectionEnd();
    InputMethodState localInputMethodState = this.mInputMethodState;
    if ((localInputMethodState != null) && (localInputMethodState.mBatchEditNesting == 0))
    {
      localObject = getInputMethodManager();
      if (localObject != null)
      {
        if ((((InputMethodManager)localObject).isActive(this.mTextView)) && ((localInputMethodState.mContentChanged) || (localInputMethodState.mSelectionModeChanged))) {
          reportExtractedText();
        }
        if ((((InputMethodManager)localObject).isWatchingCursor(this.mTextView)) && (paramPath != null))
        {
          paramPath.computeBounds(localInputMethodState.mTmpRectF, true);
          float[] arrayOfFloat = localInputMethodState.mTmpOffset;
          localInputMethodState.mTmpOffset[1] = 0.0F;
          arrayOfFloat[0] = 0.0F;
          paramCanvas.getMatrix().mapPoints(localInputMethodState.mTmpOffset);
          localInputMethodState.mTmpRectF.offset(localInputMethodState.mTmpOffset[0], localInputMethodState.mTmpOffset[1]);
          localInputMethodState.mTmpRectF.offset(0.0F, paramInt);
          localInputMethodState.mCursorRectInWindow.set((int)(localInputMethodState.mTmpRectF.left + 0.5D), (int)(localInputMethodState.mTmpRectF.top + 0.5D), (int)(localInputMethodState.mTmpRectF.right + 0.5D), (int)(localInputMethodState.mTmpRectF.bottom + 0.5D));
          ((InputMethodManager)localObject).updateCursor(this.mTextView, localInputMethodState.mCursorRectInWindow.left, localInputMethodState.mCursorRectInWindow.top, localInputMethodState.mCursorRectInWindow.right, localInputMethodState.mCursorRectInWindow.bottom);
        }
      }
    }
    Object localObject = this.mCorrectionHighlighter;
    if (localObject != null) {
      ((CorrectionHighlighter)localObject).draw(paramCanvas, paramInt);
    }
    if ((paramPath != null) && (i == j) && (this.mDrawableForCursor != null))
    {
      drawCursor(paramCanvas, paramInt);
      paramPath = null;
    }
    if ((this.mTextView.canHaveDisplayList()) && (paramCanvas.isHardwareAccelerated())) {
      drawHardwareAccelerated(paramCanvas, paramLayout, paramPath, paramPaint, paramInt);
    } else {
      paramLayout.draw(paramCanvas, paramPath, paramPaint, paramInt);
    }
  }
  
  /* Error */
  void onDrop(android.view.DragEvent paramDragEvent)
  {
    // Byte code:
    //   0: new 1960	android/text/SpannableStringBuilder
    //   3: dup
    //   4: invokespecial 1961	android/text/SpannableStringBuilder:<init>	()V
    //   7: astore_2
    //   8: aload_1
    //   9: invokestatic 1967	android/view/DragAndDropPermissions:obtain	(Landroid/view/DragEvent;)Landroid/view/DragAndDropPermissions;
    //   12: astore_3
    //   13: aload_3
    //   14: ifnull +8 -> 22
    //   17: aload_3
    //   18: invokevirtual 1970	android/view/DragAndDropPermissions:takeTransient	()Z
    //   21: pop
    //   22: aload_1
    //   23: invokevirtual 1976	android/view/DragEvent:getClipData	()Landroid/content/ClipData;
    //   26: astore 4
    //   28: aload 4
    //   30: invokevirtual 1979	android/content/ClipData:getItemCount	()I
    //   33: istore 5
    //   35: iconst_0
    //   36: istore 6
    //   38: iload 6
    //   40: iload 5
    //   42: if_icmpge +31 -> 73
    //   45: aload_2
    //   46: aload 4
    //   48: iload 6
    //   50: invokevirtual 1983	android/content/ClipData:getItemAt	(I)Landroid/content/ClipData$Item;
    //   53: aload_0
    //   54: getfield 392	android/widget/Editor:mTextView	Landroid/widget/TextView;
    //   57: invokevirtual 1032	android/widget/TextView:getContext	()Landroid/content/Context;
    //   60: invokevirtual 1989	android/content/ClipData$Item:coerceToStyledText	(Landroid/content/Context;)Ljava/lang/CharSequence;
    //   63: invokevirtual 1992	android/text/SpannableStringBuilder:append	(Ljava/lang/CharSequence;)Landroid/text/SpannableStringBuilder;
    //   66: pop
    //   67: iinc 6 1
    //   70: goto -32 -> 38
    //   73: aload_3
    //   74: ifnull +7 -> 81
    //   77: aload_3
    //   78: invokevirtual 1995	android/view/DragAndDropPermissions:release	()V
    //   81: aload_0
    //   82: getfield 392	android/widget/Editor:mTextView	Landroid/widget/TextView;
    //   85: invokevirtual 1996	android/widget/TextView:beginBatchEdit	()V
    //   88: aload_0
    //   89: getfield 368	android/widget/Editor:mUndoInputFilter	Landroid/widget/Editor$UndoInputFilter;
    //   92: invokevirtual 1877	android/widget/Editor$UndoInputFilter:freezeLastEdit	()V
    //   95: aload_0
    //   96: getfield 392	android/widget/Editor:mTextView	Landroid/widget/TextView;
    //   99: aload_1
    //   100: invokevirtual 1999	android/view/DragEvent:getX	()F
    //   103: aload_1
    //   104: invokevirtual 2002	android/view/DragEvent:getY	()F
    //   107: invokevirtual 2006	android/widget/TextView:getOffsetForPosition	(FF)I
    //   110: istore 7
    //   112: aload_1
    //   113: invokevirtual 2010	android/view/DragEvent:getLocalState	()Ljava/lang/Object;
    //   116: astore_3
    //   117: aconst_null
    //   118: astore_1
    //   119: aload_3
    //   120: instanceof 46
    //   123: ifeq +8 -> 131
    //   126: aload_3
    //   127: checkcast 46	android/widget/Editor$DragLocalState
    //   130: astore_1
    //   131: aload_1
    //   132: ifnull +20 -> 152
    //   135: aload_1
    //   136: getfield 2013	android/widget/Editor$DragLocalState:sourceTextView	Landroid/widget/TextView;
    //   139: aload_0
    //   140: getfield 392	android/widget/Editor:mTextView	Landroid/widget/TextView;
    //   143: if_acmpne +9 -> 152
    //   146: iconst_1
    //   147: istore 6
    //   149: goto +6 -> 155
    //   152: iconst_0
    //   153: istore 6
    //   155: iload 6
    //   157: ifeq +40 -> 197
    //   160: iload 7
    //   162: aload_1
    //   163: getfield 2015	android/widget/Editor$DragLocalState:start	I
    //   166: if_icmplt +31 -> 197
    //   169: aload_1
    //   170: getfield 2017	android/widget/Editor$DragLocalState:end	I
    //   173: istore 5
    //   175: iload 7
    //   177: iload 5
    //   179: if_icmpge +18 -> 197
    //   182: aload_0
    //   183: getfield 392	android/widget/Editor:mTextView	Landroid/widget/TextView;
    //   186: invokevirtual 2018	android/widget/TextView:endBatchEdit	()V
    //   189: aload_0
    //   190: getfield 368	android/widget/Editor:mUndoInputFilter	Landroid/widget/Editor$UndoInputFilter;
    //   193: invokevirtual 1877	android/widget/Editor$UndoInputFilter:freezeLastEdit	()V
    //   196: return
    //   197: aload_0
    //   198: getfield 392	android/widget/Editor:mTextView	Landroid/widget/TextView;
    //   201: invokevirtual 618	android/widget/TextView:getText	()Ljava/lang/CharSequence;
    //   204: invokeinterface 623 1 0
    //   209: istore 8
    //   211: aload_0
    //   212: getfield 392	android/widget/Editor:mTextView	Landroid/widget/TextView;
    //   215: invokevirtual 618	android/widget/TextView:getText	()Ljava/lang/CharSequence;
    //   218: checkcast 757	android/text/Spannable
    //   221: iload 7
    //   223: invokestatic 2021	android/text/Selection:setSelection	(Landroid/text/Spannable;I)V
    //   226: aload_0
    //   227: getfield 392	android/widget/Editor:mTextView	Landroid/widget/TextView;
    //   230: iload 7
    //   232: iload 7
    //   234: aload_2
    //   235: invokevirtual 2025	android/widget/TextView:replaceText_internal	(IILjava/lang/CharSequence;)V
    //   238: iload 6
    //   240: ifeq +172 -> 412
    //   243: aload_1
    //   244: getfield 2015	android/widget/Editor$DragLocalState:start	I
    //   247: istore 9
    //   249: aload_1
    //   250: getfield 2017	android/widget/Editor$DragLocalState:end	I
    //   253: istore 10
    //   255: iload 9
    //   257: istore 6
    //   259: iload 10
    //   261: istore 5
    //   263: iload 7
    //   265: iload 9
    //   267: if_icmpgt +34 -> 301
    //   270: aload_0
    //   271: getfield 392	android/widget/Editor:mTextView	Landroid/widget/TextView;
    //   274: invokevirtual 618	android/widget/TextView:getText	()Ljava/lang/CharSequence;
    //   277: invokeinterface 623 1 0
    //   282: iload 8
    //   284: isub
    //   285: istore 5
    //   287: iload 9
    //   289: iload 5
    //   291: iadd
    //   292: istore 6
    //   294: iload 10
    //   296: iload 5
    //   298: iadd
    //   299: istore 5
    //   301: aload_0
    //   302: getfield 392	android/widget/Editor:mTextView	Landroid/widget/TextView;
    //   305: iload 6
    //   307: iload 5
    //   309: invokevirtual 2028	android/widget/TextView:deleteText_internal	(II)V
    //   312: iconst_0
    //   313: iload 6
    //   315: iconst_1
    //   316: isub
    //   317: invokestatic 1012	java/lang/Math:max	(II)I
    //   320: istore 5
    //   322: aload_0
    //   323: getfield 392	android/widget/Editor:mTextView	Landroid/widget/TextView;
    //   326: invokevirtual 618	android/widget/TextView:getText	()Ljava/lang/CharSequence;
    //   329: invokeinterface 623 1 0
    //   334: iload 6
    //   336: iconst_1
    //   337: iadd
    //   338: invokestatic 1014	java/lang/Math:min	(II)I
    //   341: istore 6
    //   343: iload 6
    //   345: iload 5
    //   347: iconst_1
    //   348: iadd
    //   349: if_icmple +60 -> 409
    //   352: aload_0
    //   353: getfield 392	android/widget/Editor:mTextView	Landroid/widget/TextView;
    //   356: iload 5
    //   358: iload 6
    //   360: invokevirtual 1177	android/widget/TextView:getTransformedText	(II)Ljava/lang/CharSequence;
    //   363: astore_1
    //   364: aload_1
    //   365: iconst_0
    //   366: invokeinterface 991 2 0
    //   371: invokestatic 2032	java/lang/Character:isSpaceChar	(C)Z
    //   374: ifeq +32 -> 406
    //   377: aload_1
    //   378: iconst_1
    //   379: invokeinterface 991 2 0
    //   384: invokestatic 2032	java/lang/Character:isSpaceChar	(C)Z
    //   387: ifeq +19 -> 406
    //   390: aload_0
    //   391: getfield 392	android/widget/Editor:mTextView	Landroid/widget/TextView;
    //   394: iload 5
    //   396: iload 5
    //   398: iconst_1
    //   399: iadd
    //   400: invokevirtual 2028	android/widget/TextView:deleteText_internal	(II)V
    //   403: goto +9 -> 412
    //   406: goto +6 -> 412
    //   409: goto +3 -> 412
    //   412: aload_0
    //   413: getfield 392	android/widget/Editor:mTextView	Landroid/widget/TextView;
    //   416: invokevirtual 2018	android/widget/TextView:endBatchEdit	()V
    //   419: aload_0
    //   420: getfield 368	android/widget/Editor:mUndoInputFilter	Landroid/widget/Editor$UndoInputFilter;
    //   423: invokevirtual 1877	android/widget/Editor$UndoInputFilter:freezeLastEdit	()V
    //   426: return
    //   427: astore_1
    //   428: aload_0
    //   429: getfield 392	android/widget/Editor:mTextView	Landroid/widget/TextView;
    //   432: invokevirtual 2018	android/widget/TextView:endBatchEdit	()V
    //   435: aload_0
    //   436: getfield 368	android/widget/Editor:mUndoInputFilter	Landroid/widget/Editor$UndoInputFilter;
    //   439: invokevirtual 1877	android/widget/Editor$UndoInputFilter:freezeLastEdit	()V
    //   442: aload_1
    //   443: athrow
    //   444: astore_1
    //   445: aload_3
    //   446: ifnull +7 -> 453
    //   449: aload_3
    //   450: invokevirtual 1995	android/view/DragAndDropPermissions:release	()V
    //   453: aload_1
    //   454: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	455	0	this	Editor
    //   0	455	1	paramDragEvent	android.view.DragEvent
    //   7	228	2	localSpannableStringBuilder	SpannableStringBuilder
    //   12	438	3	localObject	Object
    //   26	21	4	localClipData	ClipData
    //   33	367	5	i	int
    //   36	323	6	j	int
    //   110	158	7	k	int
    //   209	76	8	m	int
    //   247	45	9	n	int
    //   253	46	10	i1	int
    // Exception table:
    //   from	to	target	type
    //   95	117	427	finally
    //   119	131	427	finally
    //   135	146	427	finally
    //   160	175	427	finally
    //   197	238	427	finally
    //   243	255	427	finally
    //   270	287	427	finally
    //   301	343	427	finally
    //   352	403	427	finally
    //   22	35	444	finally
    //   45	67	444	finally
  }
  
  void onFocusChanged(boolean paramBoolean, int paramInt)
  {
    this.mShowCursor = SystemClock.uptimeMillis();
    ensureEndedBatchEdit();
    Object localObject;
    if (paramBoolean)
    {
      int i = this.mTextView.getSelectionStart();
      int j = this.mTextView.getSelectionEnd();
      int k;
      if ((this.mSelectAllOnFocus) && (i == 0) && (j == this.mTextView.getText().length())) {
        k = 1;
      } else {
        k = 0;
      }
      if ((this.mFrozenWithFocus) && (this.mTextView.hasSelection()) && (k == 0)) {
        paramBoolean = true;
      } else {
        paramBoolean = false;
      }
      this.mCreatedWithASelection = paramBoolean;
      if ((!this.mFrozenWithFocus) || (i < 0) || (j < 0))
      {
        k = getLastTapPosition();
        if (k >= 0) {
          Selection.setSelection((Spannable)this.mTextView.getText(), k);
        }
        MovementMethod localMovementMethod = this.mTextView.getMovementMethod();
        if (localMovementMethod != null)
        {
          localObject = this.mTextView;
          localMovementMethod.onTakeFocus((TextView)localObject, (Spannable)((TextView)localObject).getText(), paramInt);
        }
        if (((this.mTextView.isInExtractedMode()) || (this.mSelectionMoved)) && (i >= 0) && (j >= 0)) {
          Selection.setSelection((Spannable)this.mTextView.getText(), i, j);
        }
        if (this.mSelectAllOnFocus) {
          this.mTextView.selectAllText();
        }
        this.mTouchFocusSelected = true;
      }
      this.mFrozenWithFocus = false;
      this.mSelectionMoved = false;
      if (this.mError != null) {
        showError();
      }
      makeBlink();
    }
    else
    {
      if (this.mError != null) {
        hideError();
      }
      this.mTextView.onEndBatchEdit();
      if (this.mTextView.isInExtractedMode())
      {
        hideCursorAndSpanControllers();
        stopTextActionModeWithPreservingSelection();
      }
      else
      {
        hideCursorAndSpanControllers();
        if (this.mTextView.isTemporarilyDetached()) {
          stopTextActionModeWithPreservingSelection();
        } else {
          stopTextActionMode();
        }
        downgradeEasyCorrectionSpans();
      }
      localObject = this.mSelectionModifierCursorController;
      if (localObject != null) {
        ((SelectionModifierCursorController)localObject).resetTouchOffsets();
      }
      this.mFirstTouchUp = true;
    }
  }
  
  void onLocaleChanged()
  {
    this.mWordIterator = null;
  }
  
  protected void onPopupWindowDismiss(PinnedPopupWindow paramPinnedPopupWindow)
  {
    WeakReference localWeakReference = this.mShownWindow;
    if ((localWeakReference != null) && ((PinnedPopupWindow)localWeakReference.get() == paramPinnedPopupWindow)) {
      this.mShownWindow = null;
    }
  }
  
  protected void onPopupWindowShown(PinnedPopupWindow paramPinnedPopupWindow)
  {
    Object localObject = this.mShownWindow;
    if (localObject != null)
    {
      localObject = (PinnedPopupWindow)((WeakReference)localObject).get();
      if (localObject == paramPinnedPopupWindow) {
        return;
      }
      if (localObject != null) {
        ((PinnedPopupWindow)localObject).hide();
      }
    }
    this.mShownWindow = new WeakReference(paramPinnedPopupWindow);
  }
  
  void onScreenStateChanged(int paramInt)
  {
    if (paramInt != 0)
    {
      if (paramInt == 1) {
        resumeBlink();
      }
    }
    else {
      suspendBlink();
    }
  }
  
  void onScrollChanged()
  {
    PositionListener localPositionListener = this.mPositionListener;
    if (localPositionListener != null) {
      localPositionListener.onScrollChanged();
    }
  }
  
  final void onTextOperationUserChanged()
  {
    SpellChecker localSpellChecker = this.mSpellChecker;
    if (localSpellChecker != null) {
      localSpellChecker.resetSession();
    }
  }
  
  void onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool = shouldFilterOutTouchEvent(paramMotionEvent);
    this.mLastButtonState = paramMotionEvent.getButtonState();
    if (bool)
    {
      if (paramMotionEvent.getActionMasked() == 1) {
        this.mDiscardNextActionUp = true;
      }
      return;
    }
    updateTapState(paramMotionEvent);
    updateFloatingToolbarVisibility(paramMotionEvent);
    if (hasSelectionController()) {
      getSelectionController().onTouchEvent(paramMotionEvent);
    }
    Runnable localRunnable = this.mShowSuggestionRunnable;
    if (localRunnable != null)
    {
      this.mTextView.removeCallbacks(localRunnable);
      this.mShowSuggestionRunnable = null;
    }
    if (paramMotionEvent.getActionMasked() == 1)
    {
      this.mLastUpPositionX = paramMotionEvent.getX();
      this.mLastUpPositionY = paramMotionEvent.getY();
    }
    if (paramMotionEvent.getActionMasked() == 0)
    {
      this.mLastDownPositionX = paramMotionEvent.getX();
      this.mLastDownPositionY = paramMotionEvent.getY();
      this.mTouchFocusSelected = false;
      this.mIgnoreActionUpEvent = false;
    }
  }
  
  void onTouchUpEvent(MotionEvent paramMotionEvent)
  {
    if (getSelectionActionModeHelper().resetSelection(getTextView().getOffsetForPosition(paramMotionEvent.getX(), paramMotionEvent.getY()))) {
      return;
    }
    boolean bool = hasInsertionController();
    int i = 1;
    if (((!bool) || (getInsertionController().getHandle().isPopshowing())) || ((!this.mSelectAllOnFocus) || (!this.mTextView.didTouchFocusSelect()))) {
      i = 0;
    }
    hideCursorAndSpanControllers();
    stopTextActionMode();
    CharSequence localCharSequence = this.mTextView.getText();
    if ((i == 0) && (localCharSequence.length() > 0))
    {
      i = this.mTextView.getOffsetForPosition(paramMotionEvent.getX(), paramMotionEvent.getY());
      Selection.setSelection((Spannable)localCharSequence, i);
      paramMotionEvent = this.mSpellChecker;
      if (paramMotionEvent != null) {
        paramMotionEvent.onSelectionChanged();
      }
      if (!extractedTextModeWillBeStarted()) {
        if (isCursorInsideEasyCorrectionSpan())
        {
          this.mShowSuggestionRunnable = new Runnable()
          {
            public void run()
            {
              Editor.this.showSuggestions();
            }
          };
          this.mTextView.postDelayed(this.mShowSuggestionRunnable, ViewConfiguration.getDoubleTapTimeout());
        }
        else if (hasInsertionController())
        {
          getInsertionController().show();
        }
      }
    }
  }
  
  void onWindowFocusChanged(boolean paramBoolean)
  {
    Object localObject;
    if (paramBoolean)
    {
      localObject = this.mBlink;
      if (localObject != null)
      {
        ((Blink)localObject).uncancel();
        makeBlink();
      }
    }
    else
    {
      localObject = this.mBlink;
      if (localObject != null) {
        ((Blink)localObject).cancel();
      }
      localObject = this.mInputContentType;
      if (localObject != null) {
        ((InputContentType)localObject).enterDown = false;
      }
      hideCursorAndSpanControllers();
      stopTextActionModeWithPreservingSelection();
      localObject = this.mSuggestionsPopupWindow;
      if (localObject != null) {
        ((SuggestionsPopupWindow)localObject).onParentLostFocus();
      }
      ensureEndedBatchEdit();
    }
  }
  
  public boolean performLongClick(boolean paramBoolean)
  {
    boolean bool = paramBoolean;
    if (!paramBoolean)
    {
      bool = paramBoolean;
      if (!isPositionOnText(this.mLastDownPositionX, this.mLastDownPositionY))
      {
        bool = paramBoolean;
        if (this.mInsertionControllerEnabled)
        {
          int i = this.mTextView.getOffsetForPosition(this.mLastDownPositionX, this.mLastDownPositionY);
          Selection.setSelection((Spannable)this.mTextView.getText(), i);
          getInsertionController().showWithActionPopup();
          this.mIsInsertionActionModeStartPending = true;
          bool = true;
          MetricsLogger.action(this.mTextView.getContext(), 629, 0);
        }
      }
    }
    paramBoolean = bool;
    if (!bool)
    {
      paramBoolean = bool;
      if (this.mTextView.hasSelection())
      {
        if (touchPositionIsInSelection())
        {
          startDragAndDrop();
          MetricsLogger.action(this.mTextView.getContext(), 629, 2);
        }
        else
        {
          getSelectionController().hide();
          selectCurrentWord();
          getSelectionController().show();
          MetricsLogger.action(this.mTextView.getContext(), 629, 1);
        }
        paramBoolean = true;
      }
    }
    bool = paramBoolean;
    if (!paramBoolean)
    {
      paramBoolean = selectCurrentWordAndStartDrag();
      bool = paramBoolean;
      if (paramBoolean)
      {
        MetricsLogger.action(this.mTextView.getContext(), 629, 1);
        bool = paramBoolean;
      }
    }
    return bool;
  }
  
  void prepareCursorControllers()
  {
    int i = 0;
    Object localObject = this.mTextView.getRootView().getLayoutParams();
    boolean bool1 = localObject instanceof WindowManager.LayoutParams;
    boolean bool2 = true;
    if (bool1)
    {
      localObject = (WindowManager.LayoutParams)localObject;
      if ((((WindowManager.LayoutParams)localObject).type >= 1000) && (((WindowManager.LayoutParams)localObject).type <= 1999)) {
        i = 0;
      } else {
        i = 1;
      }
    }
    if ((i != 0) && (this.mTextView.getLayout() != null)) {
      i = 1;
    } else {
      i = 0;
    }
    if ((i != 0) && (isCursorVisible())) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    this.mInsertionControllerEnabled = bool1;
    if ((i != 0) && (this.mTextView.textCanBeSelected())) {
      bool1 = bool2;
    } else {
      bool1 = false;
    }
    this.mSelectionControllerEnabled = bool1;
    if (!this.mInsertionControllerEnabled)
    {
      hideInsertionPointCursorController();
      localObject = this.mInsertionPointCursorController;
      if (localObject != null)
      {
        ((InsertionPointCursorController)localObject).onDetached();
        this.mInsertionPointCursorController = null;
      }
    }
    if (!this.mSelectionControllerEnabled)
    {
      stopSelectionActionMode();
      localObject = this.mSelectionModifierCursorController;
      if (localObject != null)
      {
        ((SelectionModifierCursorController)localObject).onDetached();
        this.mSelectionModifierCursorController = null;
      }
    }
  }
  
  void redo()
  {
    if (!this.mAllowUndo) {
      return;
    }
    UndoOwner localUndoOwner = this.mUndoOwner;
    this.mUndoManager.redo(new UndoOwner[] { localUndoOwner }, 1);
  }
  
  void refreshTextActionMode()
  {
    if (extractedTextModeWillBeStarted())
    {
      this.mRestartActionModeOnNextRefresh = false;
      return;
    }
    boolean bool = this.mTextView.hasSelection();
    SelectionModifierCursorController localSelectionModifierCursorController = getSelectionController();
    Object localObject = getInsertionController();
    if (((localSelectionModifierCursorController != null) && (localSelectionModifierCursorController.isCursorBeingModified())) || ((localObject != null) && (((InsertionPointCursorController)localObject).isCursorBeingModified())))
    {
      this.mRestartActionModeOnNextRefresh = false;
      return;
    }
    if (bool)
    {
      hideInsertionPointCursorController();
      if (this.mTextActionMode == null)
      {
        if (this.mRestartActionModeOnNextRefresh) {
          startSelectionActionModeAsync(false);
        }
      }
      else if ((localSelectionModifierCursorController != null) && (localSelectionModifierCursorController.isActive()))
      {
        this.mTextActionMode.invalidateContentRect();
      }
      else
      {
        stopTextActionModeWithPreservingSelection();
        startSelectionActionModeAsync(false);
      }
    }
    else if ((localObject != null) && (((InsertionPointCursorController)localObject).isActive()))
    {
      localObject = this.mTextActionMode;
      if (localObject != null) {
        ((ActionMode)localObject).invalidateContentRect();
      }
    }
    else
    {
      stopTextActionMode();
    }
    this.mRestartActionModeOnNextRefresh = false;
  }
  
  void replace()
  {
    int i = (this.mTextView.getSelectionStart() + this.mTextView.getSelectionEnd()) / 2;
    stopSelectionActionMode();
    Selection.setSelection((Spannable)this.mTextView.getText(), i);
    showSuggestions();
  }
  
  boolean reportExtractedText()
  {
    InputMethodState localInputMethodState = this.mInputMethodState;
    if (localInputMethodState != null)
    {
      boolean bool = localInputMethodState.mContentChanged;
      if ((bool) || (localInputMethodState.mSelectionModeChanged))
      {
        localInputMethodState.mContentChanged = false;
        localInputMethodState.mSelectionModeChanged = false;
        ExtractedTextRequest localExtractedTextRequest = localInputMethodState.mExtractedTextRequest;
        if (localExtractedTextRequest != null)
        {
          InputMethodManager localInputMethodManager = getInputMethodManager();
          if (localInputMethodManager != null)
          {
            if ((localInputMethodState.mChangedStart < 0) && (!bool)) {
              localInputMethodState.mChangedStart = -2;
            }
            if (extractTextInternal(localExtractedTextRequest, localInputMethodState.mChangedStart, localInputMethodState.mChangedEnd, localInputMethodState.mChangedDelta, localInputMethodState.mExtractedText))
            {
              localInputMethodManager.updateExtractedText(this.mTextView, localExtractedTextRequest.token, localInputMethodState.mExtractedText);
              localInputMethodState.mChangedStart = -1;
              localInputMethodState.mChangedEnd = -1;
              localInputMethodState.mChangedDelta = 0;
              localInputMethodState.mContentChanged = false;
              return true;
            }
          }
        }
      }
    }
    return false;
  }
  
  void restoreInstanceState(ParcelableParcel paramParcelableParcel)
  {
    Parcel localParcel = paramParcelableParcel.getParcel();
    this.mUndoManager.restoreInstanceState(localParcel, paramParcelableParcel.getClassLoader());
    this.mUndoInputFilter.restoreInstanceState(localParcel);
    this.mUndoOwner = this.mUndoManager.getOwner("Editor", this);
  }
  
  ParcelableParcel saveInstanceState()
  {
    ParcelableParcel localParcelableParcel = new ParcelableParcel(getClass().getClassLoader());
    Parcel localParcel = localParcelableParcel.getParcel();
    this.mUndoManager.saveInstanceState(localParcel);
    this.mUndoInputFilter.saveInstanceState(localParcel);
    return localParcelableParcel;
  }
  
  boolean selectCurrentWord()
  {
    boolean bool1 = canSelectText();
    boolean bool2 = false;
    if (!bool1) {
      return false;
    }
    if (needsToSelectAllToSelectWordOrParagraph()) {
      return this.mTextView.selectAllText();
    }
    long l = getLastTouchOffsets();
    int i = TextUtils.unpackRangeStartFromLong(l);
    int j = TextUtils.unpackRangeEndFromLong(l);
    if ((i >= 0) && (i <= this.mTextView.getText().length()))
    {
      if ((j >= 0) && (j <= this.mTextView.getText().length()))
      {
        Object localObject = (URLSpan[])((Spanned)this.mTextView.getText()).getSpans(i, j, URLSpan.class);
        int k;
        if (localObject.length >= 1)
        {
          localObject = localObject[0];
          j = ((Spanned)this.mTextView.getText()).getSpanStart(localObject);
          k = ((Spanned)this.mTextView.getText()).getSpanEnd(localObject);
        }
        else
        {
          localObject = getWordIterator();
          ((WordIterator)localObject).setCharSequence(this.mTextView.getText(), i, j);
          int m = ((WordIterator)localObject).getBeginning(i);
          int n = ((WordIterator)localObject).getEnd(j);
          if ((m != -1) && (n != -1))
          {
            j = m;
            k = n;
            if (m != n) {}
          }
          else
          {
            l = getCharClusterRange(i);
            j = TextUtils.unpackRangeStartFromLong(l);
            k = TextUtils.unpackRangeEndFromLong(l);
          }
        }
        setSelectionTranslation(j, k);
        Selection.setSelection((Spannable)this.mTextView.getText(), j, k);
        if (k > j) {
          bool2 = true;
        }
        return bool2;
      }
      return false;
    }
    return false;
  }
  
  void sendOnTextChanged(int paramInt1, int paramInt2, int paramInt3)
  {
    getSelectionActionModeHelper().onTextChanged(paramInt1, paramInt1 + paramInt2);
    updateSpellCheckSpans(paramInt1, paramInt1 + paramInt3, false);
    this.mUpdateWordIteratorText = true;
    hideCursorControllers();
    SelectionModifierCursorController localSelectionModifierCursorController = this.mSelectionModifierCursorController;
    if (localSelectionModifierCursorController != null) {
      localSelectionModifierCursorController.resetTouchOffsets();
    }
    stopTextActionMode();
  }
  
  void setContextMenuAnchor(float paramFloat1, float paramFloat2)
  {
    this.mContextMenuAnchorX = paramFloat1;
    this.mContextMenuAnchorY = paramFloat2;
  }
  
  public void setError(CharSequence paramCharSequence, Drawable paramDrawable)
  {
    this.mError = TextUtils.stringOrSpannedString(paramCharSequence);
    this.mErrorWasChanged = true;
    if (this.mError == null)
    {
      setErrorIcon(null);
      paramCharSequence = this.mErrorPopup;
      if (paramCharSequence != null)
      {
        if (paramCharSequence.isShowing()) {
          this.mErrorPopup.dismiss();
        }
        this.mErrorPopup = null;
      }
    }
    else
    {
      setErrorIcon(paramDrawable);
      if (this.mTextView.isFocused()) {
        showError();
      }
    }
  }
  
  void setFrame()
  {
    Object localObject = this.mErrorPopup;
    if (localObject != null)
    {
      localObject = (TextView)((ErrorPopup)localObject).getContentView();
      chooseSize(this.mErrorPopup, this.mError, (TextView)localObject);
      this.mErrorPopup.update(this.mTextView, getErrorX(), getErrorY(), this.mErrorPopup.getWidth(), this.mErrorPopup.getHeight());
    }
  }
  
  void setRestartActionModeOnNextRefresh(boolean paramBoolean)
  {
    this.mRestartActionModeOnNextRefresh = paramBoolean;
  }
  
  boolean shouldRenderCursor()
  {
    boolean bool1 = isCursorVisible();
    boolean bool2 = false;
    if (!bool1) {
      return false;
    }
    if (this.mRenderCursorRegardlessTiming) {
      return true;
    }
    if ((SystemClock.uptimeMillis() - this.mShowCursor) % 1000L < 500L) {
      bool2 = true;
    }
    return bool2;
  }
  
  void showEmailPopupWindow(TextPatternUtil.EmailInfo paramEmailInfo)
  {
    this.mEmailPopupWindow = getEmailPopupWindow();
    this.mEmailPopupWindow.setEmail(paramEmailInfo);
    if (this.mEmailPopupShower == null) {
      this.mEmailPopupShower = new Runnable()
      {
        public void run()
        {
          Editor.this.mEmailPopupWindow.show();
        }
      };
    }
    this.mTextView.removeCallbacks(this.mEmailPopupShower);
    this.mTextView.post(this.mEmailPopupShower);
  }
  
  void showSuggestions()
  {
    if (this.mSuggestionsPopupWindow == null) {
      this.mSuggestionsPopupWindow = new SuggestionsPopupWindow();
    }
    hideCursorAndSpanControllers();
    this.mSuggestionsPopupWindow.show();
  }
  
  boolean startActionModeInternal(@TextActionMode int paramInt)
  {
    return startSelectionActionModeInternal();
  }
  
  void startInsertionActionMode() {}
  
  void startLinkActionModeAsync(int paramInt1, int paramInt2) {}
  
  boolean startSelectionActionMode()
  {
    boolean bool = startSelectionActionModeInternal();
    if (bool) {
      getSelectionController().show();
    } else if (getInsertionController() != null) {
      getInsertionController().show();
    }
    return bool;
  }
  
  void startSelectionActionModeAsync(boolean paramBoolean)
  {
    getSelectionActionModeHelper().startSelectionActionModeAsync(paramBoolean);
  }
  
  boolean startSelectionActionModeInternal()
  {
    Object localObject = this.mTextActionMode;
    if (localObject != null)
    {
      ((ActionMode)localObject).invalidate();
      return false;
    }
    if (!checkFieldAndSelectCurrentWord()) {
      return false;
    }
    if (!extractedTextModeWillBeStarted())
    {
      localObject = this.mCustomSelectionActionModeCallback;
      if ((localObject != null) && (localObject.toString().contains("Mock for Callback")))
      {
        this.mAllowToStartActionMode = this.mCustomSelectionActionModeCallback.onCreateActionMode(mMockActionMode, new ActionMenu(this.mTextView.getContext()));
        if (!this.mAllowToStartActionMode)
        {
          Selection.setSelection((Spannable)this.mTextView.getText(), this.mTextView.getSelectionEnd());
          return false;
        }
      }
    }
    if ((!this.mTextView.isTextSelectable()) && (this.mShowSoftInputOnFocus))
    {
      localObject = getInputMethodManager();
      if (localObject != null) {
        ((InputMethodManager)localObject).showSoftInput(this.mTextView, 0, null);
      }
    }
    return true;
  }
  
  protected void stopSelectionActionMode()
  {
    Object localObject = this.mSelectionModifierCursorController;
    if (localObject != null) {
      ((SelectionModifierCursorController)localObject).hide();
    }
    if (!this.mCustomSelectionActionModeCallbackDestroyed)
    {
      localObject = this.mCustomSelectionActionModeCallback;
      if ((localObject != null) && (this.mAllowToStartActionMode))
      {
        ((ActionMode.Callback)localObject).onDestroyActionMode(mMockActionMode);
        this.mCustomSelectionActionModeCallbackDestroyed = true;
      }
    }
  }
  
  protected void stopTextActionMode()
  {
    ActionMode localActionMode = this.mTextActionMode;
    if (localActionMode != null) {
      localActionMode.finish();
    }
    stopSelectionActionMode();
  }
  
  void undo()
  {
    if (!this.mAllowUndo) {
      return;
    }
    UndoOwner localUndoOwner = this.mUndoOwner;
    this.mUndoManager.undo(new UndoOwner[] { localUndoOwner }, 1);
  }
  
  void updateCursorPosition()
  {
    loadCursorDrawable();
    if (this.mDrawableForCursor == null) {
      return;
    }
    Layout localLayout = getActiveLayout();
    int i = this.mTextView.getSelectionStart();
    int j = localLayout.getLineForOffset(i);
    updateCursorPosition(localLayout.getLineTop(j), localLayout.getLineBottomWithoutSpacing(j), localLayout.getPrimaryHorizontal(i, localLayout.shouldClampCursor(j)));
    handleEmailPopup(i);
  }
  
  private abstract class ActionPinnedPopupWindow
    extends Editor.PinnedPopupWindow
    implements Editor.Fader
  {
    protected static final int LONG_ACTION_MENU_COUNT = 6;
    protected boolean mAboveHandle;
    protected AnimatorSet mAnimationFadeIn;
    protected AnimatorSet mAnimationFadeOut;
    protected AnimatorListenerAdapter mAnimationFadeOutListener;
    private Editor.HandleView mHandleView;
    protected LayoutInflater mInflater;
    private final ViewTreeObserver.OnComputeInternalInsetsListener mInsetsComputer = new ViewTreeObserver.OnComputeInternalInsetsListener()
    {
      public void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo paramAnonymousInternalInsetsInfo)
      {
        paramAnonymousInternalInsetsInfo.contentInsets.setEmpty();
        paramAnonymousInternalInsetsInfo.visibleInsets.setEmpty();
        paramAnonymousInternalInsetsInfo.touchableRegion.set(Editor.ActionPinnedPopupWindow.this.mTouchableRegion);
        paramAnonymousInternalInsetsInfo.setTouchableInsets(3);
      }
    };
    protected TouchPanelLayout mMainPanel;
    private Runnable mShower = new Runnable()
    {
      public void run()
      {
        if ((Editor.this.isOffsetVisible(Editor.this.mTextView.getSelectionStart())) || (Editor.this.isOffsetVisible(Editor.this.mTextView.getSelectionEnd())) || (Editor.ActionPinnedPopupWindow.this.isMiddleOffsetInSelection()))
        {
          Editor.ActionPinnedPopupWindow.this.show();
          if ((Editor.ActionPinnedPopupWindow.this.mHandleView instanceof Editor.InsertionHandleView)) {
            Editor.InsertionHandleView.access$2200((Editor.InsertionHandleView)Editor.ActionPinnedPopupWindow.this.mHandleView);
          }
        }
      }
    };
    private int mSpaceOffScreen;
    private final Region mTouchableRegion = new Region();
    protected List<View> mVisibleChildren = new ArrayList();
    
    public ActionPinnedPopupWindow(Editor.HandleView paramHandleView)
    {
      super();
      this.mHandleView = paramHandleView;
      this.mSpaceOffScreen = Editor.this.mTextView.getResources().getDimensionPixelSize(285606085);
      createAnimations();
      ((Editor.AnimatePopupWindow)this.mPopupWindow).setFader(this);
      this.mInflater = ((LayoutInflater)Editor.this.mTextView.getContext().getSystemService("layout_inflater"));
    }
    
    private boolean isMainPanelContent()
    {
      TouchPanelLayout localTouchPanelLayout = this.mMainPanel;
      boolean bool1 = false;
      boolean bool2 = bool1;
      if (localTouchPanelLayout != null)
      {
        bool2 = bool1;
        if (this.mContentView.getChildAt(0) == this.mMainPanel) {
          bool2 = true;
        }
      }
      return bool2;
    }
    
    private boolean isMiddleOffsetInSelection()
    {
      int i = Editor.this.mTextView.getOffsetForPosition(Editor.this.mTextView.getWidth() / 2, Editor.this.mTextView.getHeight() / 2);
      boolean bool;
      if ((i <= Editor.this.mTextView.getSelectionEnd()) && (i >= Editor.this.mTextView.getSelectionStart())) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    private void setTouchableSurfaceInsetsComputer()
    {
      ViewTreeObserver localViewTreeObserver = this.mPopupWindow.getContentView().getRootView().getViewTreeObserver();
      localViewTreeObserver.removeOnComputeInternalInsetsListener(this.mInsetsComputer);
      localViewTreeObserver.addOnComputeInternalInsetsListener(this.mInsetsComputer);
    }
    
    public void cancelAnimations()
    {
      this.mAnimationFadeIn.cancel();
      this.mContentView.setScaleX(1.0F);
      this.mContentView.setScaleY(1.0F);
    }
    
    protected int clipVertically(int paramInt)
    {
      Layout localLayout = Editor.this.mTextView.getLayout();
      int i = Editor.this.mTextView.getSelectionStart();
      int j = Editor.this.mTextView.getSelectionEnd();
      int k = localLayout.getLineForOffset(i);
      int m = localLayout.getLineForOffset(j);
      int n = localLayout.getLineTop(m) - localLayout.getLineBottom(k);
      int i1 = Editor.this.mTextView.getResources().getDrawable(285671928).getIntrinsicHeight();
      if ((paramInt < 0) && (Editor.this.isOffsetVisible(i)))
      {
        if (n > this.mContentView.getMeasuredHeight()) {
          paramInt += localLayout.getLineBottom(k) - localLayout.getLineTop(k);
        } else {
          paramInt += localLayout.getLineBottom(m) - localLayout.getLineTop(k);
        }
        i = paramInt + this.mContentView.getMeasuredHeight() + i1 / 2;
        this.mAboveHandle = false;
      }
      else if ((paramInt < 0) && (Editor.this.mTextView.isSingleLine()))
      {
        i = localLayout.getLineBottom(k) + Editor.this.mTextView.viewportToContentVerticalOffset() + i1 / 2;
        this.mAboveHandle = false;
      }
      else
      {
        i = paramInt;
        if (this.mContentView.getMeasuredHeight() + paramInt > Editor.this.getDisplayHeightPixels())
        {
          i = paramInt;
          if (Editor.this.isOffsetVisible(j))
          {
            if (n > this.mContentView.getMeasuredHeight()) {
              paramInt -= localLayout.getLineBottom(m) - localLayout.getLineTop(m);
            } else {
              paramInt -= localLayout.getLineBottom(m) - localLayout.getLineTop(k);
            }
            i = paramInt - this.mContentView.getMeasuredHeight() - i1 / 2;
          }
        }
      }
      return i;
    }
    
    protected void computeLocalPosition()
    {
      int i = getSelectionStart();
      int j = getSelectionEnd();
      Layout localLayout = Editor.this.mTextView.getLayout();
      this.mAboveHandle = true;
      measureContent();
      if (Editor.this.isOffsetVisible(i))
      {
        this.mPositionY = (localLayout.getLineTop(localLayout.getLineForOffset(i)) - this.mContentView.getMeasuredHeight());
        this.mPositionY += Editor.this.mTextView.viewportToContentVerticalOffset();
      }
      else if (Editor.this.isOffsetVisible(j))
      {
        this.mPositionY = localLayout.getLineBottom(localLayout.getLineForOffset(j));
        this.mPositionY += Editor.this.mTextView.viewportToContentVerticalOffset();
        if (this.mHandleView.getVisibility() == 0)
        {
          k = Editor.this.mTextView.getResources().getDrawable(285671928).getIntrinsicHeight();
          this.mPositionY += k / 2;
        }
        this.mAboveHandle = false;
      }
      else if (Editor.this.mTextView.isSingleLine())
      {
        this.mPositionY = (localLayout.getLineTop(localLayout.getLineForOffset(getTextOffset())) - this.mContentView.getMeasuredHeight());
        this.mPositionY += Editor.this.mTextView.viewportToContentVerticalOffset();
      }
      else
      {
        if (Editor.this.mTempRect == null) {
          Editor.access$2402(Editor.this, new Rect());
        }
        Editor.this.mTextView.getLocalVisibleRect(Editor.this.mTempRect);
        this.mPositionY = ((Editor.this.mTempRect.bottom - Editor.this.mTempRect.top) / 2 + Editor.this.mTempRect.top - this.mContentView.getMeasuredHeight() / 2);
      }
      int k = this.mContentView.getMeasuredWidth();
      float f = this.mHandleView.mHorizontalScale;
      this.mPositionX = ((int)(((localLayout.getPrimaryHorizontal(i) + localLayout.getPrimaryHorizontal(j) - k) / 2.0F + Editor.this.mTextView.viewportToContentHorizontalOffset()) * f));
    }
    
    protected abstract void createAnimations();
    
    public void dismiss()
    {
      super.dismiss();
      setZeroTouchableSurface();
    }
    
    public void fadeOut()
    {
      this.mPopupWindow.dismiss();
    }
    
    protected int getSelectionEnd()
    {
      return Editor.this.mTextView.getSelectionEnd();
    }
    
    protected int getSelectionStart()
    {
      return Editor.this.mTextView.getSelectionStart();
    }
    
    protected int getTextOffset()
    {
      return (Editor.this.mTextView.getSelectionStart() + Editor.this.mTextView.getSelectionEnd()) / 2;
    }
    
    protected int getVerticalLocalPosition(int paramInt)
    {
      return 0;
    }
    
    public void hide()
    {
      Editor.this.mTextView.removeCallbacks(this.mShower);
      super.hide();
    }
    
    protected void measureContent()
    {
      DisplayMetrics localDisplayMetrics = Editor.this.mTextView.getResources().getDisplayMetrics();
      int i = localDisplayMetrics.widthPixels;
      int j = this.mSpaceOffScreen;
      if (!Build.IS_TABLET)
      {
        int k = this.mVisibleChildren.size();
        if (Build.IS_INTERNATIONAL_BUILD) {
          m = 5;
        } else {
          m = 6;
        }
        if (k >= m)
        {
          m = 1073741824;
          break label80;
        }
      }
      int m = Integer.MIN_VALUE;
      label80:
      this.mContentView.measure(View.MeasureSpec.makeMeasureSpec(i + j * 2, m), View.MeasureSpec.makeMeasureSpec(localDisplayMetrics.heightPixels, Integer.MIN_VALUE));
    }
    
    protected void setContentAreaAsTouchableSurface(boolean paramBoolean)
    {
      View localView = this.mContentView.findViewById(285802689);
      int i = localView.getPaddingLeft();
      int j = localView.getPaddingTop();
      int k = localView.getPaddingRight();
      int m = localView.getPaddingBottom();
      if ((paramBoolean) || (!this.mPopupWindow.isShowing()))
      {
        DisplayMetrics localDisplayMetrics = Editor.this.mTextView.getResources().getDisplayMetrics();
        int n = localDisplayMetrics.widthPixels;
        i1 = this.mSpaceOffScreen;
        int i2 = this.mVisibleChildren.size();
        if (Build.IS_INTERNATIONAL_BUILD) {
          i3 = 5;
        } else {
          i3 = 6;
        }
        if (i2 < i3) {
          i3 = Integer.MIN_VALUE;
        } else {
          i3 = 1073741824;
        }
        localView.measure(View.MeasureSpec.makeMeasureSpec(n + i1 * 2, i3), View.MeasureSpec.makeMeasureSpec(localDisplayMetrics.heightPixels, Integer.MIN_VALUE));
      }
      int i3 = localView.getMeasuredWidth();
      int i1 = localView.getMeasuredHeight();
      this.mTouchableRegion.set(localView.getLeft() + i, localView.getTop() + j, localView.getLeft() + i3 - k, localView.getTop() + i1 - m);
    }
    
    protected void setMainPanelAsContent()
    {
      if (this.mMainPanel.getParent() != null)
      {
        setContentAreaAsTouchableSurface(false);
        return;
      }
      this.mContentView.removeAllViews();
      this.mContentView.addView(this.mMainPanel, new ViewGroup.LayoutParams(-1, -1));
      setContentAreaAsTouchableSurface(false);
    }
    
    protected void setSubPanelAsContent(View paramView, Drawable paramDrawable)
    {
      dismiss();
      Object localObject = this.mHandleView;
      if ((localObject instanceof Editor.InsertionHandleView)) {
        Editor.InsertionHandleView.access$2600((Editor.InsertionHandleView)localObject);
      }
      localObject = new LinearLayout(Editor.this.mTextView.getContext());
      if (paramDrawable != null)
      {
        ((LinearLayout)localObject).setBackground(paramDrawable);
        ((LinearLayout)localObject).setId(285802689);
      }
      ((LinearLayout)localObject).addView(paramView);
      this.mContentView.removeAllViews();
      this.mContentView.addView((View)localObject, new ViewGroup.LayoutParams(-1, -1));
      this.mVisibleChildren.clear();
      computeLocalPosition();
      paramView = Editor.this.getPositionListener();
      updatePosition(paramView.getPositionX(), paramView.getPositionY());
      setContentAreaAsTouchableSurface(false);
    }
    
    public void setY(int paramInt) {}
    
    protected void setZeroTouchableSurface()
    {
      this.mTouchableRegion.setEmpty();
    }
    
    public void show()
    {
      setMainPanelAsContent();
      super.show();
    }
    
    void show(int paramInt)
    {
      Editor.this.mTextView.removeCallbacks(this.mShower);
      Editor.this.mTextView.postDelayed(this.mShower, paramInt);
    }
    
    protected void updatePosition(int paramInt1, int paramInt2)
    {
      int i = this.mPositionX;
      int j = clipVertically(this.mPositionY + paramInt2);
      paramInt2 = this.mContentView.getMeasuredWidth();
      this.mPopupWindow.setWidth(paramInt2);
      paramInt1 = Math.min(Editor.this.mTextView.getResources().getDisplayMetrics().widthPixels + this.mSpaceOffScreen - paramInt2, i + paramInt1);
      paramInt1 = Math.max(-this.mSpaceOffScreen, paramInt1);
      if (isShowing())
      {
        this.mPopupWindow.update(paramInt1, j, paramInt2, -1);
      }
      else
      {
        this.mPopupWindow.showAtLocation(Editor.this.mTextView, 0, paramInt1, j);
        setTouchableSurfaceInsetsComputer();
      }
    }
    
    public void updatePosition(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
    {
      if ((paramBoolean2) || (paramBoolean1))
      {
        paramInt2 = 0;
        paramInt1 = paramInt2;
        if (this.mHandleView.isShowing())
        {
          Object localObject = this.mHandleView;
          paramInt1 = paramInt2;
          if ((localObject instanceof Editor.InsertionHandleView))
          {
            localObject = (Editor.InsertionHandleView)localObject;
            Editor.InsertionHandleView.access$2600((Editor.InsertionHandleView)localObject);
            paramInt1 = 1;
            Editor.InsertionHandleView.access$2702((Editor.InsertionHandleView)localObject, true);
          }
        }
        if (isMainPanelContent())
        {
          hide();
          show(500);
        }
        else if (paramInt1 != 0)
        {
          hide();
        }
        else
        {
          Editor.this.stopSelectionActionMode();
        }
      }
    }
  }
  
  private class ActionPopupWindow
    extends Editor.ActionPinnedPopupWindow
    implements View.OnClickListener
  {
    private final int POPUP_TEXT_LAYOUT = 285933627;
    private TextView mAutoFillTextView;
    private TextView mCopyTextView;
    private TextView mCutTextView;
    private boolean mFeatureTel;
    private TextView mPasteTextView;
    private TextView mPhraseTextView;
    private ArrayList<String> mPhrases;
    private QueryPhraseTask mQueryPhraseTask;
    private TextView mReplaceTextView;
    private ImageView mSearchImageView;
    private TextView mSelectAllTextView;
    private TextView mSelectTextView;
    private ImageView mShareImageView;
    private ImageView mTelImageView;
    private int mTextActionPadding;
    private Handler mTranslationHandler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        int i = paramAnonymousMessage.what;
        if (i != 0)
        {
          if (i != 1) {
            Log.e("Editor", "Unrecognised message received.");
          } else {
            Editor.ActionPopupWindow.this.mTranslationPresenter.updatePanel(null);
          }
        }
        else
        {
          paramAnonymousMessage = (TranslationResult)paramAnonymousMessage.obj;
          Editor.ActionPopupWindow.this.mTranslationPresenter.updatePanel(paramAnonymousMessage);
        }
        Editor.ActionPopupWindow.this.setContentAreaAsTouchableSurface(true);
      }
    };
    private ImageView mTranslationImageView;
    private TranslationManager mTranslationManager;
    private View mTranslationPanel;
    private TranslationPresenter mTranslationPresenter;
    private ImageView mWebImageView;
    
    public ActionPopupWindow(Editor.HandleView paramHandleView)
    {
      super(paramHandleView);
      paramHandleView = Editor.this.mTextView.getResources();
      int i;
      if (Editor.access$3800()) {
        i = 285606087;
      } else {
        i = 285606086;
      }
      this.mTextActionPadding = paramHandleView.getDimensionPixelSize(i);
      paramHandleView = new Intent("android.intent.action.DIAL");
      if ((!Build.IS_TABLET) && (paramHandleView.resolveActivity(Editor.this.mTextView.getContext().getPackageManager()) != null)) {
        this.mFeatureTel = true;
      }
    }
    
    private int getMaxPhraseListHeight()
    {
      Resources localResources = Editor.this.mTextView.getResources();
      int i = localResources.getDrawable(285671783, Editor.this.mTextView.getContext().getTheme()).getIntrinsicHeight();
      int j = localResources.getDimensionPixelSize(285606038);
      int k = localResources.getDimensionPixelSize(285606038);
      int m = localResources.getDimensionPixelSize(285606039);
      if (this.mPhrases.size() != 3) {
        if (this.mPhrases.size() == 4) {
          m = k + m / 2;
        } else {
          m = k + k / 2;
        }
      }
      return i + j + k + m;
    }
    
    private ImageView newImageView()
    {
      LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-2, -2, 1.0F);
      ImageView localImageView = new ImageView(Editor.this.mTextView.getContext());
      localImageView.setLayoutParams(localLayoutParams);
      localImageView.setBackgroundResource(285671903);
      localImageView.setScaleType(ImageView.ScaleType.CENTER);
      localImageView.setOnClickListener(this);
      return localImageView;
    }
    
    private TextView newTextView()
    {
      LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-2, -2, 1.0F);
      TextView localTextView = (TextView)((LayoutInflater)Editor.this.mTextView.getContext().getSystemService("layout_inflater")).inflate(285933627, null);
      localTextView.setLayoutParams(localLayoutParams);
      localTextView.setSingleLine();
      localTextView.setGravity(17);
      localTextView.setOnClickListener(this);
      return localTextView;
    }
    
    private void setMainPanelChildPadding(boolean paramBoolean)
    {
      int i = this.mMainPanel.getChildCount();
      this.mVisibleChildren.clear();
      Object localObject;
      for (int j = 0; j < i; j++)
      {
        localObject = this.mMainPanel.getChildAt(j);
        if (((View)localObject).getVisibility() == 0) {
          this.mVisibleChildren.add(localObject);
        }
      }
      int k = this.mVisibleChildren.size();
      if (!Build.IS_TABLET)
      {
        if (Build.IS_INTERNATIONAL_BUILD) {
          j = 5;
        } else {
          j = 6;
        }
        if (k >= j)
        {
          j = 0;
          break label203;
        }
      }
      if ((!Build.IS_TABLET) && (Locale.getDefault().getLanguage().equals(Locale.US.getLanguage())) && (paramBoolean) && (k >= 5)) {
        j = (int)(this.mTextActionPadding * 0.4D);
      } else if ((!Build.IS_TABLET) && (Locale.getDefault().getLanguage().equals(Locale.US.getLanguage())) && (paramBoolean) && (k >= 4)) {
        j = (int)(this.mTextActionPadding * 0.6D);
      } else {
        j = this.mTextActionPadding;
      }
      label203:
      if (k == 1)
      {
        localObject = (View)this.mVisibleChildren.get(0);
        ((View)localObject).getBackground().setLevel(3);
        ((View)localObject).setPadding(j, ((View)localObject).getPaddingTop(), j, ((View)localObject).getPaddingBottom());
      }
      else
      {
        for (i = 0; i < k; i++)
        {
          View localView = (View)this.mVisibleChildren.get(i);
          localObject = localView.getBackground();
          if (i == 0) {
            ((Drawable)localObject).setLevel(0);
          } else if (i == k - 1) {
            ((Drawable)localObject).setLevel(2);
          } else {
            ((Drawable)localObject).setLevel(1);
          }
          localView.setPadding(j, localView.getPaddingTop(), j, localView.getPaddingBottom());
        }
      }
    }
    
    private void startAddPhraseActivity()
    {
      Intent localIntent = Phrases.Utils.getAddPhraseIntent();
      localIntent.addFlags(268435456);
      Editor.this.mTextView.getContext().startActivity(localIntent);
    }
    
    protected void createAnimations()
    {
      this.mAnimationFadeIn = new AnimatorSet();
      ObjectAnimator localObjectAnimator1 = ObjectAnimator.ofFloat(this.mContentView, View.ALPHA, new float[] { 0.0F, 1.0F });
      ObjectAnimator localObjectAnimator2 = ObjectAnimator.ofFloat(this.mContentView, View.SCALE_X, new float[] { 0.6F, 1.0F });
      ObjectAnimator localObjectAnimator3 = ObjectAnimator.ofFloat(this.mContentView, View.SCALE_Y, new float[] { 0.6F, 1.0F });
      this.mAnimationFadeIn.setInterpolator(new CubicEaseOutInterpolator());
      this.mAnimationFadeIn.setDuration(150L);
      this.mAnimationFadeIn.playTogether(new Animator[] { localObjectAnimator1, localObjectAnimator2, localObjectAnimator3 });
      this.mAnimationFadeOut = new AnimatorSet();
      localObjectAnimator1 = ObjectAnimator.ofFloat(this.mContentView, View.ALPHA, new float[] { 1.0F, 0.0F });
      localObjectAnimator3 = ObjectAnimator.ofFloat(this.mContentView, View.SCALE_X, new float[] { 1.0F, 0.6F });
      localObjectAnimator2 = ObjectAnimator.ofFloat(this.mContentView, View.SCALE_Y, new float[] { 1.0F, 0.6F });
      this.mAnimationFadeOut.setInterpolator(new CubicEaseOutInterpolator());
      this.mAnimationFadeOut.setDuration(100L);
      this.mAnimationFadeOut.playTogether(new Animator[] { localObjectAnimator1, localObjectAnimator3, localObjectAnimator2 });
      this.mAnimationFadeOutListener = new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          ((Editor.AnimatePopupWindow)Editor.ActionPopupWindow.this.mPopupWindow).dismiss(false);
        }
      };
    }
    
    protected void createPopupWindow()
    {
      Editor localEditor = Editor.this;
      this.mPopupWindow = new Editor.AnimatePopupWindow(localEditor, localEditor.mTextView.getContext(), null);
      this.mPopupWindow.setClippingEnabled(false);
    }
    
    public void dismiss()
    {
      ((Editor.AnimatePopupWindow)this.mPopupWindow).dismiss(true);
      setZeroTouchableSurface();
    }
    
    public void fadeIn(int paramInt1, int paramInt2)
    {
      this.mContentView.setPivotX(this.mContentView.getMeasuredWidth() / 2);
      ViewGroup localViewGroup = this.mContentView;
      float f;
      if (this.mAboveHandle) {
        f = this.mContentView.getMeasuredHeight();
      } else {
        f = 0.0F;
      }
      localViewGroup.setPivotY(f);
      this.mAnimationFadeIn.start();
    }
    
    protected void initContentView()
    {
      this.mMainPanel = new TouchPanelLayout(Editor.this.mTextView.getContext());
      if (this.mPopupElevation > 0) {
        this.mMainPanel.setElevation(this.mPopupElevation);
      }
      this.mMainPanel.setOrientation(0);
      this.mMainPanel.setBackgroundResource(285671900);
      this.mMainPanel.setId(285802689);
      this.mContentView = new FrameLayout(Editor.this.mTextView.getContext());
      this.mContentView.addView(this.mMainPanel, new ViewGroup.LayoutParams(-1, -1));
      this.mSearchImageView = newImageView();
      this.mMainPanel.addView(this.mSearchImageView);
      this.mSearchImageView.setImageResource(285671878);
      this.mSearchImageView.setContentDescription(Editor.this.mTextView.getResources().getString(286130280));
      this.mWebImageView = newImageView();
      this.mMainPanel.addView(this.mWebImageView);
      this.mWebImageView.setImageResource(285671893);
      this.mWebImageView.setContentDescription(Editor.this.mTextView.getResources().getString(286130283));
      this.mTelImageView = newImageView();
      this.mMainPanel.addView(this.mTelImageView);
      this.mTelImageView.setImageResource(285671888);
      this.mTelImageView.setContentDescription(Editor.this.mTextView.getResources().getString(286130279));
      this.mSelectTextView = newTextView();
      this.mMainPanel.addView(this.mSelectTextView);
      this.mSelectTextView.setText(286130462);
      this.mSelectAllTextView = newTextView();
      this.mMainPanel.addView(this.mSelectAllTextView);
      this.mSelectAllTextView.setText(286130463);
      this.mCutTextView = newTextView();
      this.mMainPanel.addView(this.mCutTextView);
      this.mCutTextView.setText(286130286);
      this.mCopyTextView = newTextView();
      this.mMainPanel.addView(this.mCopyTextView);
      this.mCopyTextView.setText(286130284);
      this.mPasteTextView = newTextView();
      this.mMainPanel.addView(this.mPasteTextView);
      this.mPasteTextView.setText(286130396);
      this.mReplaceTextView = newTextView();
      this.mMainPanel.addView(this.mReplaceTextView);
      this.mReplaceTextView.setText(286130431);
      this.mAutoFillTextView = newTextView();
      this.mMainPanel.addView(this.mAutoFillTextView);
      this.mAutoFillTextView.setText(286130256);
      this.mAutoFillTextView.setId(16908965);
      this.mTranslationImageView = newImageView();
      this.mMainPanel.addView(this.mTranslationImageView);
      this.mTranslationImageView.setImageResource(285671873);
      this.mTranslationImageView.setContentDescription(Editor.this.mTextView.getResources().getString(286130282));
      this.mShareImageView = newImageView();
      this.mMainPanel.addView(this.mShareImageView);
      this.mShareImageView.setImageResource(285671883);
      this.mShareImageView.setContentDescription(Editor.this.mTextView.getResources().getString(286130281));
      this.mPhraseTextView = newTextView();
      this.mMainPanel.addView(this.mPhraseTextView);
    }
    
    public void onClick(View paramView)
    {
      final int i = 0;
      final int j = Editor.this.mTextView.getText().length();
      int k;
      if (Editor.this.mTextView.isFocused())
      {
        k = Editor.this.mTextView.getSelectionStart();
        j = Editor.this.mTextView.getSelectionEnd();
        i = Math.max(0, Math.min(k, j));
        j = Math.max(0, Math.max(k, j));
      }
      if (paramView == this.mSelectTextView)
      {
        if (Editor.this.hasSelectionController())
        {
          Editor.this.getSelectionController().setMinTouchOffset(i);
          Editor.this.getSelectionController().setMaxTouchOffset(j);
        }
        Editor.this.startSelectionActionMode();
      }
      else
      {
        if (paramView == this.mSelectAllTextView)
        {
          Editor.this.mTextView.onTextContextMenuItem(16908319);
          Editor.this.startSelectionActionMode();
          i = 16908319;
          break label1455;
        }
        if ((paramView == this.mPasteTextView) && (Editor.this.mTextView.canPaste()))
        {
          Editor.this.mTextView.onTextContextMenuItem(16908322);
          hide();
          i = 16908322;
          break label1455;
        }
        if (paramView == this.mReplaceTextView)
        {
          Editor.this.replace();
          i = 16908340;
          break label1455;
        }
        if (paramView == this.mCopyTextView)
        {
          Editor.this.mTextView.onTextContextMenuItem(16908321);
          Selection.setSelection((Spannable)Editor.this.mTextView.getText(), Editor.this.mTextView.getSelectionEnd(), Editor.this.mTextView.getSelectionEnd());
          i = 16908321;
          break label1455;
        }
        if (paramView == this.mCutTextView)
        {
          Editor.this.mTextView.onTextContextMenuItem(16908320);
          i = 16908320;
          break label1455;
        }
        if (paramView == this.mShareImageView)
        {
          localObject1 = new Intent("android.intent.action.SEND");
          ((Intent)localObject1).setType("text/plain");
          ((Intent)localObject1).putExtra("android.intent.extra.TEXT", Editor.this.mTextView.getText().subSequence(i, j).toString());
          paramView = Editor.this.mTextView.getContext();
          Editor.this.startActivityFromContext(paramView, Intent.createChooser((Intent)localObject1, paramView.getString(17041103)));
          Selection.setSelection((Spannable)Editor.this.mTextView.getText(), (i + j) / 2);
          i = 16908341;
          break label1455;
        }
        if (paramView == this.mSearchImageView)
        {
          if (Build.IS_INTERNATIONAL_BUILD)
          {
            paramView = new Intent("android.intent.action.WEB_SEARCH");
            paramView.putExtra("query", Editor.this.mTextView.getText().subSequence(i, j).toString());
            localObject1 = Editor.this;
            ((Editor)localObject1).startActivityFromContext(((Editor)localObject1).mTextView.getContext(), paramView);
          }
          else
          {
            paramView = new Intent("android.intent.action.SEARCH");
            localObject1 = new StringBuilder();
            ((StringBuilder)localObject1).append("qsb://query?words=");
            ((StringBuilder)localObject1).append(Editor.this.mTextView.getText().subSequence(i, j).toString());
            ((StringBuilder)localObject1).append("&ref=miuiEditor&web_search=true");
            paramView.setData(Uri.parse(((StringBuilder)localObject1).toString()));
            localObject1 = Editor.this;
            ((Editor)localObject1).startActivityFromContext(((Editor)localObject1).mTextView.getContext(), paramView);
          }
          Selection.setSelection((Spannable)Editor.this.mTextView.getText(), (i + j) / 2);
          i = 16908341;
          break label1455;
        }
        Object localObject1 = this.mTelImageView;
        if (paramView == localObject1)
        {
          paramView = (LinkSpec)((ImageView)localObject1).getTag();
          if (paramView != null)
          {
            localObject1 = new Intent("android.intent.action.DIAL", Uri.parse(paramView.url));
            paramView = Editor.this;
            paramView.startActivityFromContext(paramView.mTextView.getContext(), (Intent)localObject1);
            if (i != j) {
              Selection.setSelection((Spannable)Editor.this.mTextView.getText(), (i + j) / 2);
            }
          }
        }
        else
        {
          localObject1 = this.mWebImageView;
          if (paramView == localObject1)
          {
            paramView = (LinkSpec)((ImageView)localObject1).getTag();
            if (paramView != null)
            {
              paramView = new Intent("android.intent.action.VIEW", Uri.parse(paramView.url));
              localObject1 = Editor.this;
              ((Editor)localObject1).startActivityFromContext(((Editor)localObject1).mTextView.getContext(), paramView);
              if (i != j) {
                Selection.setSelection((Spannable)Editor.this.mTextView.getText(), (i + j) / 2);
              }
            }
          }
          else
          {
            Object localObject2;
            if (paramView == this.mPhraseTextView)
            {
              paramView = this.mPhrases;
              if ((paramView != null) && (paramView.size() != 0))
              {
                paramView = ((LayoutInflater)Editor.this.mTextView.getContext().getSystemService("layout_inflater")).inflate(285933610, null);
                localObject1 = (ImageButton)paramView.findViewById(285802582);
                localObject2 = (ImageButton)paramView.findViewById(285802502);
                if (Editor.this.isPasswordInputType())
                {
                  ((ImageButton)localObject1).setVisibility(8);
                  ((ImageButton)localObject2).setVisibility(8);
                }
                else
                {
                  ((ImageButton)localObject1).setVisibility(0);
                  ((ImageButton)localObject1).setOnClickListener(new View.OnClickListener()
                  {
                    public void onClick(View paramAnonymousView)
                    {
                      paramAnonymousView = Phrases.Utils.getPhraseEditIntent();
                      paramAnonymousView.addFlags(268435456);
                      Editor.this.mTextView.getContext().startActivity(paramAnonymousView);
                    }
                  });
                  ((ImageButton)localObject2).setVisibility(0);
                  ((ImageButton)localObject2).setOnClickListener(new View.OnClickListener()
                  {
                    public void onClick(View paramAnonymousView)
                    {
                      Editor.ActionPopupWindow.this.startAddPhraseActivity();
                    }
                  });
                }
                localObject1 = (ListView)paramView.findViewById(16908298);
                ((ListView)localObject1).setOverScrollMode(2);
                ((ListView)localObject1).setAdapter(new Editor.PhraseAdapter(Editor.this, this.mPhrases));
                ((ListView)localObject1).setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                  public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
                  {
                    paramAnonymousAdapterView = (String)paramAnonymousAdapterView.getAdapter().getItem(paramAnonymousInt);
                    paramAnonymousView = Editor.this.mTextView.getText();
                    Selection.setSelection((Spannable)paramAnonymousView, j);
                    ((Editable)paramAnonymousView).replace(i, j, paramAnonymousAdapterView);
                  }
                });
                j = Editor.this.mTextView.getResources().getDimensionPixelSize(285606045);
                i = -2;
                if (this.mPhrases.size() > 2) {
                  i = getMaxPhraseListHeight();
                }
                paramView.setLayoutParams(new ViewGroup.LayoutParams(j, i));
                setSubPanelAsContent(paramView, Editor.this.mTextView.getResources().getDrawable(285671900, Editor.this.mTextView.getContext().getTheme()));
              }
              else
              {
                startAddPhraseActivity();
              }
            }
            else if (paramView == this.mTranslationImageView)
            {
              paramView = this.mTranslationPanel;
              if (paramView == null) {
                this.mTranslationPanel = ((LayoutInflater)Editor.this.mTextView.getContext().getSystemService("layout_inflater")).inflate(285933628, null);
              } else if (paramView.getParent() != null) {
                ((ViewGroup)this.mTranslationPanel.getParent()).removeView(this.mTranslationPanel);
              }
              if (this.mTranslationPresenter == null) {
                this.mTranslationPresenter = new TranslationPresenter(Editor.this.mTextView.getContext(), this.mTranslationPanel);
              }
              paramView = Editor.this.mTextView.getResources().getDisplayMetrics();
              localObject1 = Editor.this.mTextView.getResources().getDrawable(285671900, Editor.this.mTextView.getContext().getTheme());
              localObject2 = new Rect();
              ((Drawable)localObject1).getPadding((Rect)localObject2);
              int m = paramView.widthPixels;
              int n = ((Rect)localObject2).left;
              int i1 = ((Rect)localObject2).right;
              int i2 = Editor.this.mTextView.getResources().getDimensionPixelSize(285606088);
              k = ((Rect)localObject2).top;
              int i3 = ((Rect)localObject2).bottom;
              this.mTranslationPanel.setLayoutParams(new ViewGroup.LayoutParams(m - n - i1, i2 + k + i3));
              setSubPanelAsContent(this.mTranslationPanel, null);
              this.mTranslationPresenter.setAboveHandle(this.mAboveHandle);
              this.mTranslationPresenter.setInProgress();
              this.mTranslationManager.translate(null, null, Editor.this.mTextView.getTransformedText(i, j).toString());
            }
            else if (paramView == this.mAutoFillTextView)
            {
              Editor.this.mTextView.onTextContextMenuItem(16908355);
            }
          }
        }
      }
      i = -1;
      label1455:
      Editor.this.getSelectionActionModeHelper().onSelectionAction(i);
    }
    
    public void show()
    {
      Object localObject1 = Editor.this.mTextView.getText();
      int i = Editor.this.mTextView.getSelectionStart();
      int j = Editor.this.mTextView.getSelectionEnd();
      final boolean bool1 = Editor.this.isPasswordInputType();
      boolean bool2 = WindowLayoutParamsUtil.isInSystemWindow(Editor.this.mTextView);
      int k;
      if ((((CharSequence)localObject1).length() > 0) && (j - i > 0) && (!bool1) && (!bool2)) {
        k = 1;
      } else {
        k = 0;
      }
      int m = 0;
      this.mTelImageView.setTag(null);
      int n = m;
      Object localObject2;
      if (this.mFeatureTel)
      {
        n = m;
        if (((CharSequence)localObject1).length() > 0)
        {
          localObject2 = Linkify.getLinks((CharSequence)localObject1, i, j, 4);
          n = m;
          if (localObject2 != null)
          {
            n = m;
            if (((ArrayList)localObject2).size() == 1)
            {
              this.mTelImageView.setTag(((ArrayList)localObject2).get(0));
              n = 1;
            }
          }
        }
      }
      m = 0;
      this.mWebImageView.setTag(null);
      int i1 = m;
      if (n == 0)
      {
        i1 = m;
        if (((CharSequence)localObject1).length() > 0)
        {
          localObject2 = Linkify.getLinks((CharSequence)localObject1, i, j, 1);
          i1 = m;
          if (localObject2 != null)
          {
            i1 = m;
            if (((ArrayList)localObject2).size() > 0)
            {
              this.mWebImageView.setTag(((ArrayList)localObject2).get(0));
              i1 = 1;
            }
          }
        }
      }
      boolean bool3 = Editor.this.mTextView.canCopy();
      boolean bool4 = Editor.this.mTextView.canCut();
      final boolean bool5 = Editor.this.mTextView.canPaste();
      int i2;
      if ((Editor.this.mTextView.isSuggestionsEnabled()) && (Editor.this.shouldOfferToShowSuggestions()) && ((!Editor.this.mTextView.isInExtractedMode()) || (!Editor.this.mTextView.hasSelection()))) {
        i2 = 1;
      } else {
        i2 = 0;
      }
      int i3;
      if ((((CharSequence)localObject1).length() > 0) && (!Editor.this.mTextView.hasSelection())) {
        i3 = 1;
      } else {
        i3 = 0;
      }
      if ((((CharSequence)localObject1).length() > 0) && ((i != 0) || (j != ((CharSequence)localObject1).length()))) {
        j = 1;
      } else {
        j = 0;
      }
      if ((((CharSequence)localObject1).length() > 0) && (Editor.this.mTextView.hasSelection()) && (!bool1) && (!bool2)) {
        i = 1;
      } else {
        i = 0;
      }
      if ((!Editor.this.mTextView.hasSelection()) && (!Phrases.Utils.isAddPhraseActivity(Editor.this.mTextView.getContext())) && (!Editor.this.mTextView.isInExtractedMode()) && (!bool2)) {
        m = 1;
      } else {
        m = 0;
      }
      if (this.mTranslationManager == null) {
        this.mTranslationManager = new TranslationManager(Editor.this.mTextView.getContext(), this.mTranslationHandler);
      }
      int i4 = m;
      int i5;
      if ((((CharSequence)localObject1).length() > 0) && (Editor.this.mTextView.hasSelection()) && (this.mTranslationManager.isAvailable()) && (!bool1) && (!bool2)) {
        i5 = 1;
      } else {
        i5 = 0;
      }
      if ((Editor.this.mTextView.canRequestAutofill()) && ((Editor.this.mTextView.getSelectedText() == null) || (Editor.this.mTextView.getSelectedText().isEmpty()))) {
        m = 1;
      } else {
        m = 0;
      }
      localObject1 = this.mSearchImageView;
      if (k != 0) {
        k = 0;
      } else {
        k = 8;
      }
      ((ImageView)localObject1).setVisibility(k);
      localObject1 = this.mTelImageView;
      if (n != 0) {
        n = 0;
      } else {
        n = 8;
      }
      ((ImageView)localObject1).setVisibility(n);
      localObject1 = this.mWebImageView;
      if (i1 != 0) {
        i1 = 0;
      } else {
        i1 = 8;
      }
      ((ImageView)localObject1).setVisibility(i1);
      localObject1 = this.mCopyTextView;
      if (bool3) {
        i1 = 0;
      } else {
        i1 = 8;
      }
      ((TextView)localObject1).setVisibility(i1);
      localObject1 = this.mCutTextView;
      if (bool4) {
        i1 = 0;
      } else {
        i1 = 8;
      }
      ((TextView)localObject1).setVisibility(i1);
      localObject1 = this.mPasteTextView;
      if (bool5) {
        i1 = 0;
      } else {
        i1 = 8;
      }
      ((TextView)localObject1).setVisibility(i1);
      localObject1 = this.mReplaceTextView;
      if (i2 != 0) {
        i1 = 0;
      } else {
        i1 = 8;
      }
      ((TextView)localObject1).setVisibility(i1);
      localObject1 = this.mSelectTextView;
      if (i3 != 0) {
        i1 = 0;
      } else {
        i1 = 8;
      }
      ((TextView)localObject1).setVisibility(i1);
      localObject1 = this.mSelectAllTextView;
      if (j != 0) {
        i1 = 0;
      } else {
        i1 = 8;
      }
      ((TextView)localObject1).setVisibility(i1);
      localObject1 = this.mTranslationImageView;
      if (i5 != 0) {
        i5 = 0;
      } else {
        i5 = 8;
      }
      ((ImageView)localObject1).setVisibility(i5);
      localObject1 = this.mShareImageView;
      if (i != 0) {
        i = 0;
      } else {
        i = 8;
      }
      ((ImageView)localObject1).setVisibility(i);
      localObject1 = this.mPhraseTextView;
      if (i4 != 0) {
        i = 0;
      } else {
        i = 8;
      }
      ((TextView)localObject1).setVisibility(i);
      localObject1 = this.mAutoFillTextView;
      if (m != 0) {
        m = 0;
      } else {
        m = 8;
      }
      ((TextView)localObject1).setVisibility(m);
      if ((!bool3) && (!bool4) && (!bool5) && (i2 == 0) && (i3 == 0) && (j == 0)) {
        bool5 = true;
      } else {
        bool5 = false;
      }
      if (i4 != 0)
      {
        localObject1 = new QueryPhraseListener()
        {
          public void onPostExecute(ArrayList<String> paramAnonymousArrayList)
          {
            Editor.ActionPopupWindow.access$4102(Editor.ActionPopupWindow.this, paramAnonymousArrayList);
            boolean bool = true;
            if (Editor.ActionPopupWindow.this.mPhrases.size() == 0)
            {
              if (bool1)
              {
                if (bool5) {
                  return;
                }
                bool = false;
              }
              else
              {
                Editor.ActionPopupWindow.this.mPhraseTextView.setText(286130177);
              }
            }
            else {
              Editor.ActionPopupWindow.this.mPhraseTextView.setText(286130317);
            }
            paramAnonymousArrayList = Editor.ActionPopupWindow.this.mPhraseTextView;
            int i;
            if (bool) {
              i = 0;
            } else {
              i = 8;
            }
            paramAnonymousArrayList.setVisibility(i);
            Editor.ActionPopupWindow.this.setMainPanelChildPadding(bool);
            Editor.ActionPopupWindow.this.show();
          }
        };
        localObject2 = this.mQueryPhraseTask;
        if (localObject2 != null) {
          if (((QueryPhraseTask)localObject2).getStatus() != AsyncTask.Status.FINISHED) {
            return;
          }
        }
        this.mQueryPhraseTask = new QueryPhraseTask(Editor.this.mTextView.getContext(), (QueryPhraseListener)localObject1);
        this.mQueryPhraseTask.execute(new Void[0]);
      }
      else
      {
        if (bool5) {
          return;
        }
        this.mPhraseTextView.setVisibility(8);
        setMainPanelChildPadding(false);
        super.show();
      }
    }
    
    protected void updatePosition(int paramInt1, int paramInt2)
    {
      Editor.AnimatePopupWindow localAnimatePopupWindow = (Editor.AnimatePopupWindow)this.mPopupWindow;
      if (localAnimatePopupWindow.isDismissing()) {
        localAnimatePopupWindow.dismiss(false);
      }
      super.updatePosition(paramInt1, paramInt2);
    }
  }
  
  private class AnimatePopupWindow
    extends PopupWindow
  {
    private boolean mDismissing = false;
    private Editor.Fader mFader;
    
    private AnimatePopupWindow(Context paramContext)
    {
      super(null, 0);
      if (this.mFader == null) {
        setAnimationStyle(286195715);
      }
    }
    
    public AnimatePopupWindow(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
      super(paramAttributeSet, paramInt);
    }
    
    public void dismiss(boolean paramBoolean)
    {
      if ((paramBoolean) && (this.mDismissing)) {
        return;
      }
      Editor.Fader localFader = this.mFader;
      if (localFader != null) {
        localFader.cancelAnimations();
      }
      if (paramBoolean)
      {
        localFader = this.mFader;
        if (localFader != null)
        {
          this.mDismissing = true;
          localFader.fadeOut();
          return;
        }
      }
      this.mDismissing = false;
      dismiss();
    }
    
    public boolean isDismissing()
    {
      return this.mDismissing;
    }
    
    public void setFader(Editor.Fader paramFader)
    {
      this.mFader = paramFader;
    }
    
    public void showAtLocation(View paramView, int paramInt1, int paramInt2, int paramInt3)
    {
      this.mDismissing = false;
      Editor.Fader localFader = this.mFader;
      if (localFader != null) {
        localFader.cancelAnimations();
      }
      super.showAtLocation(paramView, paramInt1, paramInt2, paramInt3);
      paramView = this.mFader;
      if (paramView != null) {
        paramView.fadeIn(paramInt2, paramInt3);
      }
    }
    
    public void update(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.mDismissing = false;
      super.update(paramInt1, paramInt2, paramInt3, paramInt4);
      Editor.Fader localFader = this.mFader;
      if (localFader != null) {
        localFader.setY(paramInt2);
      }
    }
  }
  
  private class Blink
    implements Runnable
  {
    private boolean mCancelled;
    
    private Blink() {}
    
    void cancel()
    {
      if (!this.mCancelled)
      {
        Editor.this.mTextView.removeCallbacks(this);
        this.mCancelled = true;
      }
    }
    
    public void run()
    {
      if (this.mCancelled) {
        return;
      }
      Editor.this.mTextView.removeCallbacks(this);
      if (Editor.this.shouldBlink())
      {
        if (Editor.this.mTextView.getLayout() != null) {
          Editor.this.mTextView.invalidateCursorPath();
        }
        Editor.this.mTextView.postDelayed(this, 500L);
      }
    }
    
    void uncancel()
    {
      this.mCancelled = false;
    }
  }
  
  private class CorrectionHighlighter
  {
    private static final int FADE_OUT_DURATION = 400;
    private int mEnd;
    private long mFadingStartTime;
    private final Paint mPaint = new Paint(1);
    private final Path mPath = new Path();
    private int mStart;
    private RectF mTempRectF;
    
    public CorrectionHighlighter()
    {
      this.mPaint.setCompatibilityScaling(Editor.this.mTextView.getResources().getCompatibilityInfo().applicationScale);
      this.mPaint.setStyle(Paint.Style.FILL);
    }
    
    private void invalidate(boolean paramBoolean)
    {
      if (Editor.this.mTextView.getLayout() == null) {
        return;
      }
      if (this.mTempRectF == null) {
        this.mTempRectF = new RectF();
      }
      this.mPath.computeBounds(this.mTempRectF, false);
      int i = Editor.this.mTextView.getCompoundPaddingLeft();
      int j = Editor.this.mTextView.getExtendedPaddingTop() + Editor.this.mTextView.getVerticalOffset(true);
      if (paramBoolean) {
        Editor.this.mTextView.postInvalidateOnAnimation((int)this.mTempRectF.left + i, (int)this.mTempRectF.top + j, (int)this.mTempRectF.right + i, (int)this.mTempRectF.bottom + j);
      } else {
        Editor.this.mTextView.postInvalidate((int)this.mTempRectF.left, (int)this.mTempRectF.top, (int)this.mTempRectF.right, (int)this.mTempRectF.bottom);
      }
    }
    
    private void stopAnimation()
    {
      Editor.access$8202(Editor.this, null);
    }
    
    private boolean updatePaint()
    {
      long l = SystemClock.uptimeMillis() - this.mFadingStartTime;
      if (l > 400L) {
        return false;
      }
      float f = (float)l / 400.0F;
      int i = Color.alpha(Editor.this.mTextView.mHighlightColor);
      int j = Editor.this.mTextView.mHighlightColor;
      i = (int)(i * (1.0F - f));
      this.mPaint.setColor((j & 0xFFFFFF) + (i << 24));
      return true;
    }
    
    private boolean updatePath()
    {
      Layout localLayout = Editor.this.mTextView.getLayout();
      if (localLayout == null) {
        return false;
      }
      int i = Editor.this.mTextView.getText().length();
      int j = Math.min(i, this.mStart);
      i = Math.min(i, this.mEnd);
      this.mPath.reset();
      localLayout.getSelectionPath(j, i, this.mPath);
      return true;
    }
    
    public void draw(Canvas paramCanvas, int paramInt)
    {
      if ((updatePath()) && (updatePaint()))
      {
        if (paramInt != 0) {
          paramCanvas.translate(0.0F, paramInt);
        }
        paramCanvas.drawPath(this.mPath, this.mPaint);
        if (paramInt != 0) {
          paramCanvas.translate(0.0F, -paramInt);
        }
        invalidate(true);
      }
      else
      {
        stopAnimation();
        invalidate(false);
      }
    }
    
    public void highlight(CorrectionInfo paramCorrectionInfo)
    {
      this.mStart = paramCorrectionInfo.getOffset();
      this.mEnd = (this.mStart + paramCorrectionInfo.getNewText().length());
      this.mFadingStartTime = SystemClock.uptimeMillis();
      if ((this.mStart < 0) || (this.mEnd < 0)) {
        stopAnimation();
      }
    }
  }
  
  private static abstract interface CursorController
    extends ViewTreeObserver.OnTouchModeChangeListener
  {
    public abstract void hide();
    
    public abstract boolean isActive();
    
    public abstract boolean isCursorBeingModified();
    
    public abstract void onDetached();
    
    public abstract void show();
  }
  
  private static class DragLocalState
  {
    public int end;
    public TextView sourceTextView;
    public int start;
    
    public DragLocalState(TextView paramTextView, int paramInt1, int paramInt2)
    {
      this.sourceTextView = paramTextView;
      this.start = paramInt1;
      this.end = paramInt2;
    }
  }
  
  private static abstract interface EasyEditDeleteListener
  {
    public abstract void onDeleteClick(EasyEditSpan paramEasyEditSpan);
  }
  
  private class EasyEditPopupWindow
    extends Editor.PinnedPopupWindow
    implements View.OnClickListener
  {
    private static final int POPUP_TEXT_LAYOUT = 17367333;
    private TextView mDeleteTextView;
    private EasyEditSpan mEasyEditSpan;
    private Editor.EasyEditDeleteListener mOnDeleteListener;
    
    private EasyEditPopupWindow()
    {
      super();
    }
    
    private void setOnDeleteListener(Editor.EasyEditDeleteListener paramEasyEditDeleteListener)
    {
      this.mOnDeleteListener = paramEasyEditDeleteListener;
    }
    
    protected int clipVertically(int paramInt)
    {
      return paramInt;
    }
    
    protected void createPopupWindow()
    {
      this.mPopupWindow = new PopupWindow(Editor.this.mTextView.getContext(), null, 16843464);
      this.mPopupWindow.setInputMethodMode(2);
      this.mPopupWindow.setClippingEnabled(true);
    }
    
    protected int getTextOffset()
    {
      return ((Editable)Editor.this.mTextView.getText()).getSpanEnd(this.mEasyEditSpan);
    }
    
    protected int getVerticalLocalPosition(int paramInt)
    {
      return Editor.this.mTextView.getLayout().getLineBottom(paramInt);
    }
    
    public void hide()
    {
      EasyEditSpan localEasyEditSpan = this.mEasyEditSpan;
      if (localEasyEditSpan != null) {
        localEasyEditSpan.setDeleteEnabled(false);
      }
      this.mOnDeleteListener = null;
      super.hide();
    }
    
    protected void initContentView()
    {
      Object localObject = new LinearLayout(Editor.this.mTextView.getContext());
      ((LinearLayout)localObject).setOrientation(0);
      this.mContentView = ((ViewGroup)localObject);
      this.mContentView.setBackgroundResource(17303744);
      localObject = (LayoutInflater)Editor.this.mTextView.getContext().getSystemService("layout_inflater");
      ViewGroup.LayoutParams localLayoutParams = new ViewGroup.LayoutParams(-2, -2);
      this.mDeleteTextView = ((TextView)((LayoutInflater)localObject).inflate(17367333, null));
      this.mDeleteTextView.setLayoutParams(localLayoutParams);
      this.mDeleteTextView.setText(17039884);
      this.mDeleteTextView.setOnClickListener(this);
      this.mContentView.addView(this.mDeleteTextView);
    }
    
    public void onClick(View paramView)
    {
      if (paramView == this.mDeleteTextView)
      {
        paramView = this.mEasyEditSpan;
        if ((paramView != null) && (paramView.isDeleteEnabled()))
        {
          paramView = this.mOnDeleteListener;
          if (paramView != null) {
            paramView.onDeleteClick(this.mEasyEditSpan);
          }
        }
      }
    }
    
    public void setEasyEditSpan(EasyEditSpan paramEasyEditSpan)
    {
      this.mEasyEditSpan = paramEasyEditSpan;
    }
  }
  
  public static class EditOperation
    extends UndoOperation<Editor>
  {
    public static final Parcelable.ClassLoaderCreator<EditOperation> CREATOR = new Parcelable.ClassLoaderCreator()
    {
      public Editor.EditOperation createFromParcel(Parcel paramAnonymousParcel)
      {
        return new Editor.EditOperation(paramAnonymousParcel, null);
      }
      
      public Editor.EditOperation createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new Editor.EditOperation(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public Editor.EditOperation[] newArray(int paramAnonymousInt)
      {
        return new Editor.EditOperation[paramAnonymousInt];
      }
    };
    private static final int TYPE_DELETE = 1;
    private static final int TYPE_INSERT = 0;
    private static final int TYPE_REPLACE = 2;
    private boolean mFrozen;
    private boolean mIsComposition;
    private int mNewCursorPos;
    private String mNewText;
    private int mOldCursorPos;
    private String mOldText;
    private int mStart;
    private int mType;
    
    public EditOperation(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      this.mType = paramParcel.readInt();
      this.mOldText = paramParcel.readString();
      this.mNewText = paramParcel.readString();
      this.mStart = paramParcel.readInt();
      this.mOldCursorPos = paramParcel.readInt();
      this.mNewCursorPos = paramParcel.readInt();
      int i = paramParcel.readInt();
      boolean bool1 = false;
      if (i == 1) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      this.mFrozen = bool2;
      boolean bool2 = bool1;
      if (paramParcel.readInt() == 1) {
        bool2 = true;
      }
      this.mIsComposition = bool2;
    }
    
    public EditOperation(Editor paramEditor, String paramString1, int paramInt, String paramString2, boolean paramBoolean)
    {
      super();
      this.mOldText = paramString1;
      this.mNewText = paramString2;
      if ((this.mNewText.length() > 0) && (this.mOldText.length() == 0)) {
        this.mType = 0;
      } else if ((this.mNewText.length() == 0) && (this.mOldText.length() > 0)) {
        this.mType = 1;
      } else {
        this.mType = 2;
      }
      this.mStart = paramInt;
      this.mOldCursorPos = paramEditor.mTextView.getSelectionStart();
      this.mNewCursorPos = (this.mNewText.length() + paramInt);
      this.mIsComposition = paramBoolean;
    }
    
    private int getNewTextEnd()
    {
      return this.mStart + this.mNewText.length();
    }
    
    private int getOldTextEnd()
    {
      return this.mStart + this.mOldText.length();
    }
    
    private String getTypeString()
    {
      int i = this.mType;
      if (i != 0)
      {
        if (i != 1)
        {
          if (i != 2) {
            return "";
          }
          return "replace";
        }
        return "delete";
      }
      return "insert";
    }
    
    private boolean mergeDeleteWith(EditOperation paramEditOperation)
    {
      if (paramEditOperation.mType != 1) {
        return false;
      }
      if (this.mStart != paramEditOperation.getOldTextEnd()) {
        return false;
      }
      this.mStart = paramEditOperation.mStart;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramEditOperation.mOldText);
      localStringBuilder.append(this.mOldText);
      this.mOldText = localStringBuilder.toString();
      this.mNewCursorPos = paramEditOperation.mNewCursorPos;
      this.mIsComposition = paramEditOperation.mIsComposition;
      return true;
    }
    
    private boolean mergeInsertWith(EditOperation paramEditOperation)
    {
      int i = paramEditOperation.mType;
      StringBuilder localStringBuilder;
      if (i == 0)
      {
        if (getNewTextEnd() != paramEditOperation.mStart) {
          return false;
        }
        localStringBuilder = new StringBuilder();
        localStringBuilder.append(this.mNewText);
        localStringBuilder.append(paramEditOperation.mNewText);
        this.mNewText = localStringBuilder.toString();
        this.mNewCursorPos = paramEditOperation.mNewCursorPos;
        this.mFrozen = paramEditOperation.mFrozen;
        this.mIsComposition = paramEditOperation.mIsComposition;
        return true;
      }
      if ((this.mIsComposition) && (i == 2) && (this.mStart <= paramEditOperation.mStart) && (getNewTextEnd() >= paramEditOperation.getOldTextEnd()))
      {
        localStringBuilder = new StringBuilder();
        localStringBuilder.append(this.mNewText.substring(0, paramEditOperation.mStart - this.mStart));
        localStringBuilder.append(paramEditOperation.mNewText);
        localStringBuilder.append(this.mNewText.substring(paramEditOperation.getOldTextEnd() - this.mStart, this.mNewText.length()));
        this.mNewText = localStringBuilder.toString();
        this.mNewCursorPos = paramEditOperation.mNewCursorPos;
        this.mIsComposition = paramEditOperation.mIsComposition;
        return true;
      }
      return false;
    }
    
    private boolean mergeReplaceWith(EditOperation paramEditOperation)
    {
      StringBuilder localStringBuilder;
      if ((paramEditOperation.mType == 0) && (getNewTextEnd() == paramEditOperation.mStart))
      {
        localStringBuilder = new StringBuilder();
        localStringBuilder.append(this.mNewText);
        localStringBuilder.append(paramEditOperation.mNewText);
        this.mNewText = localStringBuilder.toString();
        this.mNewCursorPos = paramEditOperation.mNewCursorPos;
        return true;
      }
      if (!this.mIsComposition) {
        return false;
      }
      if ((paramEditOperation.mType == 1) && (this.mStart <= paramEditOperation.mStart) && (getNewTextEnd() >= paramEditOperation.getOldTextEnd()))
      {
        localStringBuilder = new StringBuilder();
        localStringBuilder.append(this.mNewText.substring(0, paramEditOperation.mStart - this.mStart));
        localStringBuilder.append(this.mNewText.substring(paramEditOperation.getOldTextEnd() - this.mStart, this.mNewText.length()));
        this.mNewText = localStringBuilder.toString();
        if (this.mNewText.isEmpty()) {
          this.mType = 1;
        }
        this.mNewCursorPos = paramEditOperation.mNewCursorPos;
        this.mIsComposition = paramEditOperation.mIsComposition;
        return true;
      }
      if ((paramEditOperation.mType == 2) && (this.mStart == paramEditOperation.mStart) && (TextUtils.equals(this.mNewText, paramEditOperation.mOldText)))
      {
        this.mNewText = paramEditOperation.mNewText;
        this.mNewCursorPos = paramEditOperation.mNewCursorPos;
        this.mIsComposition = paramEditOperation.mIsComposition;
        return true;
      }
      return false;
    }
    
    private boolean mergeWith(EditOperation paramEditOperation)
    {
      if (this.mFrozen) {
        return false;
      }
      int i = this.mType;
      if (i != 0)
      {
        if (i != 1)
        {
          if (i != 2) {
            return false;
          }
          return mergeReplaceWith(paramEditOperation);
        }
        return mergeDeleteWith(paramEditOperation);
      }
      return mergeInsertWith(paramEditOperation);
    }
    
    private static void modifyText(Editable paramEditable, int paramInt1, int paramInt2, CharSequence paramCharSequence, int paramInt3, int paramInt4)
    {
      if ((Editor.isValidRange(paramEditable, paramInt1, paramInt2)) && (paramInt3 <= paramEditable.length() - (paramInt2 - paramInt1)))
      {
        if (paramInt1 != paramInt2) {
          paramEditable.delete(paramInt1, paramInt2);
        }
        if (paramCharSequence.length() != 0) {
          paramEditable.insert(paramInt3, paramCharSequence);
        }
      }
      if ((paramInt4 >= 0) && (paramInt4 <= paramEditable.length())) {
        Selection.setSelection(paramEditable, paramInt4);
      }
    }
    
    public void commit() {}
    
    public void forceMergeWith(EditOperation paramEditOperation)
    {
      if (mergeWith(paramEditOperation)) {
        return;
      }
      Object localObject = (Editable)((Editor)getOwnerData()).mTextView.getText();
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(localObject.toString());
      modifyText(localSpannableStringBuilder, this.mStart, getNewTextEnd(), this.mOldText, this.mStart, this.mOldCursorPos);
      localObject = new SpannableStringBuilder(localObject.toString());
      modifyText((Editable)localObject, paramEditOperation.mStart, paramEditOperation.getOldTextEnd(), paramEditOperation.mNewText, paramEditOperation.mStart, paramEditOperation.mNewCursorPos);
      this.mType = 2;
      this.mNewText = localObject.toString();
      this.mOldText = localSpannableStringBuilder.toString();
      this.mStart = 0;
      this.mNewCursorPos = paramEditOperation.mNewCursorPos;
      this.mIsComposition = paramEditOperation.mIsComposition;
    }
    
    public void redo()
    {
      modifyText((Editable)((Editor)getOwnerData()).mTextView.getText(), this.mStart, getOldTextEnd(), this.mNewText, this.mStart, this.mNewCursorPos);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[mType=");
      localStringBuilder.append(getTypeString());
      localStringBuilder.append(", mOldText=");
      localStringBuilder.append(this.mOldText);
      localStringBuilder.append(", mNewText=");
      localStringBuilder.append(this.mNewText);
      localStringBuilder.append(", mStart=");
      localStringBuilder.append(this.mStart);
      localStringBuilder.append(", mOldCursorPos=");
      localStringBuilder.append(this.mOldCursorPos);
      localStringBuilder.append(", mNewCursorPos=");
      localStringBuilder.append(this.mNewCursorPos);
      localStringBuilder.append(", mFrozen=");
      localStringBuilder.append(this.mFrozen);
      localStringBuilder.append(", mIsComposition=");
      localStringBuilder.append(this.mIsComposition);
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
    
    public void undo()
    {
      modifyText((Editable)((Editor)getOwnerData()).mTextView.getText(), this.mStart, getNewTextEnd(), this.mOldText, this.mStart, this.mOldCursorPos);
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mType);
      paramParcel.writeString(this.mOldText);
      paramParcel.writeString(this.mNewText);
      paramParcel.writeInt(this.mStart);
      paramParcel.writeInt(this.mOldCursorPos);
      paramParcel.writeInt(this.mNewCursorPos);
      paramParcel.writeInt(this.mFrozen);
      paramParcel.writeInt(this.mIsComposition);
    }
  }
  
  private class EmailAddPopupWindow
    extends Editor.ActionPinnedPopupWindow
    implements View.OnClickListener
  {
    private final int POPUP_TEXT_LAYOUT = 285933627;
    private TextPatternUtil.EmailInfo mEmail;
    private TextView mEmailTextView;
    private boolean mPosChanged;
    private int mTextActionPadding;
    
    public EmailAddPopupWindow(Editor.HandleView paramHandleView)
    {
      super(paramHandleView);
      this$1 = Editor.this.mTextView.getResources();
      int i;
      if (Editor.access$3800()) {
        i = 285606087;
      } else {
        i = 285606086;
      }
      this.mTextActionPadding = Editor.this.getDimensionPixelSize(i);
    }
    
    private TextView newTextView()
    {
      LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-2, -2, 1.0F);
      TextView localTextView = (TextView)((LayoutInflater)Editor.this.mTextView.getContext().getSystemService("layout_inflater")).inflate(285933627, null);
      localTextView.setLayoutParams(localLayoutParams);
      localTextView.setSingleLine();
      localTextView.setGravity(17);
      localTextView.setOnClickListener(this);
      return localTextView;
    }
    
    protected void createAnimations()
    {
      this.mAnimationFadeIn = new AnimatorSet();
      ObjectAnimator localObjectAnimator1 = ObjectAnimator.ofFloat(this.mContentView, View.ALPHA, new float[] { 0.0F, 1.0F });
      ObjectAnimator localObjectAnimator2 = ObjectAnimator.ofFloat(this.mContentView, View.SCALE_X, new float[] { 0.6F, 1.0F });
      ObjectAnimator localObjectAnimator3 = ObjectAnimator.ofFloat(this.mContentView, View.SCALE_Y, new float[] { 0.6F, 1.0F });
      this.mAnimationFadeIn.setInterpolator(new CubicEaseOutInterpolator());
      this.mAnimationFadeIn.setDuration(150L);
      this.mAnimationFadeIn.playTogether(new Animator[] { localObjectAnimator1, localObjectAnimator2, localObjectAnimator3 });
      this.mAnimationFadeOut = new AnimatorSet();
      localObjectAnimator3 = ObjectAnimator.ofFloat(this.mContentView, View.ALPHA, new float[] { 1.0F, 0.0F });
      localObjectAnimator1 = ObjectAnimator.ofFloat(this.mContentView, View.SCALE_X, new float[] { 1.0F, 0.6F });
      localObjectAnimator2 = ObjectAnimator.ofFloat(this.mContentView, View.SCALE_Y, new float[] { 1.0F, 0.6F });
      this.mAnimationFadeOut.setInterpolator(new CubicEaseOutInterpolator());
      this.mAnimationFadeOut.setDuration(100L);
      this.mAnimationFadeOut.playTogether(new Animator[] { localObjectAnimator3, localObjectAnimator1, localObjectAnimator2 });
      this.mAnimationFadeOutListener = new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          ((Editor.AnimatePopupWindow)Editor.EmailAddPopupWindow.this.mPopupWindow).dismiss(false);
        }
      };
    }
    
    protected void createPopupWindow()
    {
      Editor localEditor = Editor.this;
      this.mPopupWindow = new Editor.AnimatePopupWindow(localEditor, localEditor.mTextView.getContext(), null);
      this.mPopupWindow.setClippingEnabled(false);
    }
    
    public void dismiss()
    {
      ((Editor.AnimatePopupWindow)this.mPopupWindow).dismiss(true);
      setZeroTouchableSurface();
      this.mEmail = null;
    }
    
    public void fadeIn(int paramInt1, int paramInt2)
    {
      this.mContentView.setPivotX(this.mContentView.getMeasuredWidth() / 2);
      ViewGroup localViewGroup = this.mContentView;
      float f;
      if (this.mAboveHandle) {
        f = this.mContentView.getMeasuredHeight();
      } else {
        f = 0.0F;
      }
      localViewGroup.setPivotY(f);
      this.mAnimationFadeIn.start();
    }
    
    protected int getSelectionEnd()
    {
      if (this.mEmail == null) {
        return 0;
      }
      Layout localLayout = Editor.this.mTextView.getLayout();
      if (localLayout != null)
      {
        int i = localLayout.getLineForOffset(this.mEmail.cursorPos);
        if (localLayout.getLineForOffset(this.mEmail.start + this.mEmail.email.length()) > i) {
          return localLayout.getLineEnd(i) - 1;
        }
      }
      return this.mEmail.start + this.mEmail.email.length();
    }
    
    protected int getSelectionStart()
    {
      if (this.mEmail == null) {
        return 0;
      }
      Layout localLayout = Editor.this.mTextView.getLayout();
      if (localLayout != null)
      {
        int i = localLayout.getLineForOffset(this.mEmail.cursorPos);
        if (localLayout.getLineForOffset(this.mEmail.start) < i) {
          return localLayout.getLineStart(i);
        }
      }
      return this.mEmail.start;
    }
    
    protected void initContentView()
    {
      this.mMainPanel = new TouchPanelLayout(Editor.this.mTextView.getContext());
      this.mMainPanel.setOrientation(0);
      this.mMainPanel.setBackgroundResource(285671900);
      this.mMainPanel.setId(285802689);
      this.mContentView = new FrameLayout(Editor.this.mTextView.getContext());
      this.mContentView.addView(this.mMainPanel, new ViewGroup.LayoutParams(-1, -1));
      this.mEmailTextView = newTextView();
      this.mEmailTextView.setText(286130316);
      this.mMainPanel.addView(this.mEmailTextView);
    }
    
    public void onClick(View paramView)
    {
      if (this.mEmail != null)
      {
        Phrases.Utils.startAddPhraseActivity(Editor.this.mTextView.getContext(), this.mEmail.email);
        hide();
      }
    }
    
    public void setEmail(TextPatternUtil.EmailInfo paramEmailInfo)
    {
      TextPatternUtil.EmailInfo localEmailInfo = this.mEmail;
      boolean bool;
      if ((localEmailInfo != null) && (localEmailInfo.start == paramEmailInfo.start) && (this.mEmail.email.length() == paramEmailInfo.email.length()) && (this.mEmail.cursorPos == paramEmailInfo.cursorPos)) {
        bool = false;
      } else {
        bool = true;
      }
      this.mPosChanged = bool;
      this.mEmail = paramEmailInfo;
    }
    
    public void show()
    {
      if (WindowLayoutParamsUtil.isInSystemWindow(Editor.this.mTextView)) {
        return;
      }
      int i;
      if ((!Phrases.Utils.isAddPhraseActivity(Editor.this.mTextView.getContext())) && (!Editor.this.isPasswordInputType()) && (!Editor.this.mTextView.isInExtractedMode())) {
        i = 1;
      } else {
        i = 0;
      }
      if (i == 0) {
        return;
      }
      if ((this.mPosChanged) && (this.mEmail != null))
      {
        this.mEmailTextView.getBackground().setLevel(3);
        TextView localTextView = this.mEmailTextView;
        localTextView.setPadding(this.mTextActionPadding, localTextView.getPaddingTop(), this.mTextActionPadding, this.mEmailTextView.getPaddingBottom());
        super.show();
      }
    }
    
    protected void updatePosition(int paramInt1, int paramInt2)
    {
      Editor.AnimatePopupWindow localAnimatePopupWindow = (Editor.AnimatePopupWindow)this.mPopupWindow;
      if (localAnimatePopupWindow.isDismissing()) {
        localAnimatePopupWindow.dismiss(false);
      }
      super.updatePosition(paramInt1, paramInt2);
    }
  }
  
  private static class ErrorPopup
    extends PopupWindow
  {
    private boolean mAbove = false;
    private int mPopupInlineErrorAboveBackgroundId = 0;
    private int mPopupInlineErrorBackgroundId = 0;
    private final TextView mView;
    
    ErrorPopup(TextView paramTextView, int paramInt1, int paramInt2)
    {
      super(paramInt1, paramInt2);
      this.mView = paramTextView;
      this.mPopupInlineErrorBackgroundId = getResourceId(this.mPopupInlineErrorBackgroundId, 297);
      this.mView.setBackgroundResource(this.mPopupInlineErrorBackgroundId);
    }
    
    private int getResourceId(int paramInt1, int paramInt2)
    {
      int i = paramInt1;
      if (paramInt1 == 0)
      {
        TypedArray localTypedArray = this.mView.getContext().obtainStyledAttributes(R.styleable.Theme);
        i = localTypedArray.getResourceId(paramInt2, 0);
        localTypedArray.recycle();
      }
      return i;
    }
    
    void fixDirection(boolean paramBoolean)
    {
      this.mAbove = paramBoolean;
      if (paramBoolean) {
        this.mPopupInlineErrorAboveBackgroundId = getResourceId(this.mPopupInlineErrorAboveBackgroundId, 296);
      } else {
        this.mPopupInlineErrorBackgroundId = getResourceId(this.mPopupInlineErrorBackgroundId, 297);
      }
      TextView localTextView = this.mView;
      int i;
      if (paramBoolean) {
        i = this.mPopupInlineErrorAboveBackgroundId;
      } else {
        i = this.mPopupInlineErrorBackgroundId;
      }
      localTextView.setBackgroundResource(i);
    }
    
    public void update(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
    {
      super.update(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
      paramBoolean = isAboveAnchor();
      if (paramBoolean != this.mAbove) {
        fixDirection(paramBoolean);
      }
    }
  }
  
  private static abstract interface Fader
  {
    public abstract void cancelAnimations();
    
    public abstract void fadeIn(int paramInt1, int paramInt2);
    
    public abstract void fadeOut();
    
    public abstract void setY(int paramInt);
  }
  
  public static @interface HandleType {}
  
  @VisibleForTesting
  public abstract class HandleView
    extends View
    implements Editor.TextViewPositionListener
  {
    private static final int HISTORY_SIZE = 5;
    private static final int TOUCH_UP_FILTER_DELAY_AFTER = 150;
    private static final int TOUCH_UP_FILTER_DELAY_BEFORE = 350;
    private Runnable mActionPopupShower;
    protected Editor.ActionPopupWindow mActionPopupWindow;
    protected AnimatorSet mAnimationFadeIn;
    protected AnimatorSet mAnimationFadeOut;
    protected AnimatorListenerAdapter mAnimationFadeOutListener;
    protected final PopupWindow mContainer = new Editor.AnimatePopupWindow(Editor.this, Editor.this.mTextView.getContext(), null);
    private float mCurrentDragInitialTouchRawX = -1.0F;
    protected Drawable mDrawable;
    protected Drawable mDrawableLtr;
    protected Drawable mDrawableRtl;
    private int mHandleExtension;
    protected int mHorizontalGravity;
    protected float mHorizontalScale = 1.0F;
    protected int mHotspotX;
    private float mIdealVerticalOffset;
    private boolean mIsDragging;
    private int mLastParentX;
    private int mLastParentY;
    private int mLastWindowY;
    private int mMinSize;
    private int mNumberPreviousOffsets = 0;
    private boolean mPositionHasChanged = true;
    private int mPositionX;
    private int mPositionY;
    protected int mPrevLine = -1;
    protected int mPreviousLineTouched = -1;
    protected int mPreviousOffset = -1;
    private int mPreviousOffsetIndex = 0;
    private final int[] mPreviousOffsets = new int[5];
    private final long[] mPreviousOffsetsTimes = new long[5];
    private float mTextViewScaleX;
    private float mTextViewScaleY;
    private float mTouchOffsetY;
    private float mTouchToWindowOffsetX;
    private float mTouchToWindowOffsetY;
    
    private HandleView(Drawable paramDrawable1, Drawable paramDrawable2)
    {
      super();
      this.mContainer.setSplitTouchEnabled(true);
      this.mContainer.setClippingEnabled(false);
      this.mContainer.setWindowLayoutType(1002);
      this.mContainer.setContentView(this);
      this.mContainer.setOnDismissListener(new PopupWindow.OnDismissListener()
      {
        public void onDismiss()
        {
          Editor.HandleView.this.onDetached();
        }
      });
      setDrawables(paramDrawable1, paramDrawable2);
      this.mMinSize = Editor.this.mTextView.getContext().getResources().getDimensionPixelSize(285606095);
      updateDrawable(false);
    }
    
    private void addPositionToTouchUpFilter(int paramInt)
    {
      this.mPreviousOffsetIndex = ((this.mPreviousOffsetIndex + 1) % 5);
      int[] arrayOfInt = this.mPreviousOffsets;
      int i = this.mPreviousOffsetIndex;
      arrayOfInt[i] = paramInt;
      this.mPreviousOffsetsTimes[i] = SystemClock.uptimeMillis();
      this.mNumberPreviousOffsets += 1;
    }
    
    private boolean checkForTransforms()
    {
      if (Editor.MagnifierMotionAnimator.access$5200(Editor.this.mMagnifierAnimator)) {
        return true;
      }
      if ((Editor.this.mTextView.getRotation() == 0.0F) && (Editor.this.mTextView.getRotationX() == 0.0F) && (Editor.this.mTextView.getRotationY() == 0.0F))
      {
        this.mTextViewScaleX = Editor.this.mTextView.getScaleX();
        this.mTextViewScaleY = Editor.this.mTextView.getScaleY();
        for (ViewParent localViewParent = Editor.this.mTextView.getParent(); localViewParent != null; localViewParent = localViewParent.getParent()) {
          if ((localViewParent instanceof View))
          {
            View localView = (View)localViewParent;
            if ((localView.getRotation() == 0.0F) && (localView.getRotationX() == 0.0F) && (localView.getRotationY() == 0.0F))
            {
              this.mTextViewScaleX *= localView.getScaleX();
              this.mTextViewScaleY *= localView.getScaleY();
            }
            else
            {
              return false;
            }
          }
        }
        return true;
      }
      return false;
    }
    
    private int clipVertically(int paramInt)
    {
      int i = paramInt;
      if (paramInt >= Editor.this.getDisplayHeightPixels())
      {
        i = getCurrentCursorOffset();
        Layout localLayout = Editor.this.mTextView.getLayout();
        i = localLayout.getLineForOffset(i);
        i = paramInt - (localLayout.getLineBottom(i) - localLayout.getLineTop(i)) - getMeasuredHeight();
      }
      return i;
    }
    
    private void filterOnTouchUp()
    {
      long l = SystemClock.uptimeMillis();
      int i = 0;
      int j = this.mPreviousOffsetIndex;
      int k = Math.min(this.mNumberPreviousOffsets, 5);
      while ((i < k) && (l - this.mPreviousOffsetsTimes[j] < 150L))
      {
        i++;
        j = (this.mPreviousOffsetIndex - i + 5) % 5;
      }
      if ((i > 0) && (i < k) && (l - this.mPreviousOffsetsTimes[j] > 350L)) {
        positionAtCursorOffset(this.mPreviousOffsets[j], false);
      }
    }
    
    private int getHandleExtension(int paramInt)
    {
      return Math.min(paramInt, Editor.this.mTextView.getLineHeight() * 2);
    }
    
    private HandleView getOtherSelectionHandle()
    {
      Object localObject = Editor.this.getSelectionController();
      if ((localObject != null) && (((Editor.SelectionModifierCursorController)localObject).isActive()))
      {
        if (Editor.SelectionModifierCursorController.access$500((Editor.SelectionModifierCursorController)localObject) != this) {
          localObject = Editor.SelectionModifierCursorController.access$500((Editor.SelectionModifierCursorController)localObject);
        } else {
          localObject = Editor.SelectionModifierCursorController.access$5300((Editor.SelectionModifierCursorController)localObject);
        }
        return (HandleView)localObject;
      }
      return null;
    }
    
    private boolean handleOverlapsMagnifier(HandleView paramHandleView, Rect paramRect)
    {
      paramHandleView = paramHandleView.mContainer;
      if (!paramHandleView.hasDecorView()) {
        return false;
      }
      return Rect.intersects(new Rect(paramHandleView.getDecorViewLayoutParams().x, paramHandleView.getDecorViewLayoutParams().y, paramHandleView.getDecorViewLayoutParams().x + paramHandleView.getContentView().getWidth(), paramHandleView.getDecorViewLayoutParams().y + paramHandleView.getContentView().getHeight()), paramRect);
    }
    
    private boolean isVisible()
    {
      if (this.mIsDragging) {
        return true;
      }
      if (Editor.this.mTextView.isInBatchEditMode()) {
        return false;
      }
      return Editor.this.isPositionVisible(this.mPositionX + this.mHotspotX + getHorizontalOffset(), this.mPositionY);
    }
    
    private boolean obtainMagnifierShowCoordinates(MotionEvent paramMotionEvent, PointF paramPointF)
    {
      int i = getMagnifierHandleTrigger();
      int k;
      if (i != 0)
      {
        if (i != 1)
        {
          if (i != 2)
          {
            j = -1;
            k = -1;
          }
          else
          {
            j = Editor.this.mTextView.getSelectionEnd();
            k = Editor.this.mTextView.getSelectionStart();
          }
        }
        else
        {
          j = Editor.this.mTextView.getSelectionStart();
          k = Editor.this.mTextView.getSelectionEnd();
        }
      }
      else
      {
        j = Editor.this.mTextView.getSelectionStart();
        k = -1;
      }
      if (j == -1) {
        return false;
      }
      if (paramMotionEvent.getActionMasked() == 0) {
        this.mCurrentDragInitialTouchRawX = paramMotionEvent.getRawX();
      } else if (paramMotionEvent.getActionMasked() == 1) {
        this.mCurrentDragInitialTouchRawX = -1.0F;
      }
      Object localObject = Editor.this.mTextView.getLayout();
      int m = ((Layout)localObject).getLineForOffset(j);
      int n;
      if ((k != -1) && (m == ((Layout)localObject).getLineForOffset(k))) {
        n = 1;
      } else {
        n = 0;
      }
      int i1;
      if (n != 0)
      {
        if (j < k) {
          i1 = 1;
        } else {
          i1 = 0;
        }
        if (getHorizontal(Editor.this.mTextView.getLayout(), j) < getHorizontal(Editor.this.mTextView.getLayout(), k)) {
          j = 1;
        } else {
          j = 0;
        }
        if (i1 != j)
        {
          j = 1;
          break label267;
        }
      }
      int j = 0;
      label267:
      localObject = new int[2];
      Editor.this.mTextView.getLocationOnScreen((int[])localObject);
      float f1 = paramMotionEvent.getRawX() - localObject[0];
      float f2 = Editor.this.mTextView.getTotalPaddingLeft() - Editor.this.mTextView.getScrollX();
      float f3 = Editor.this.mTextView.getTotalPaddingLeft() - Editor.this.mTextView.getScrollX();
      if (n != 0)
      {
        if (i == 2) {
          i1 = 1;
        } else {
          i1 = 0;
        }
        if ((i1 ^ j) != 0)
        {
          f2 += getHorizontal(Editor.this.mTextView.getLayout(), k);
          break label415;
        }
      }
      f2 += Editor.this.mTextView.getLayout().getLineLeft(m);
      label415:
      if (n != 0)
      {
        if (i == 1) {
          n = 1;
        } else {
          n = 0;
        }
        if ((n ^ j) != 0)
        {
          f3 += getHorizontal(Editor.this.mTextView.getLayout(), k);
          break label486;
        }
      }
      f3 += Editor.this.mTextView.getLayout().getLineRight(m);
      label486:
      float f4 = this.mTextViewScaleX;
      float f5 = f2 * f4;
      f3 *= f4;
      f2 = Math.round(Editor.MagnifierMotionAnimator.access$5100(Editor.this.mMagnifierAnimator).getWidth() / Editor.MagnifierMotionAnimator.access$5100(Editor.this.mMagnifierAnimator).getZoom());
      if ((f1 >= f5 - f2 / 2.0F) && (f1 <= f3 + f2 / 2.0F))
      {
        if (this.mTextViewScaleX == 1.0F)
        {
          f2 = f1;
        }
        else
        {
          f2 = paramMotionEvent.getRawX();
          f1 = this.mCurrentDragInitialTouchRawX;
          f2 = (f2 - f1) * this.mTextViewScaleX + f1 - localObject[0];
        }
        paramPointF.x = Math.max(f5, Math.min(f3, f2));
        paramPointF.y = (((Editor.this.mTextView.getLayout().getLineTop(m) + Editor.this.mTextView.getLayout().getLineBottom(m)) / 2.0F + Editor.this.mTextView.getTotalPaddingTop() - Editor.this.mTextView.getScrollY()) * this.mTextViewScaleY);
        return true;
      }
      return false;
    }
    
    private void setVisible(boolean paramBoolean)
    {
      View localView = this.mContainer.getContentView();
      int i;
      if (paramBoolean) {
        i = 0;
      } else {
        i = 4;
      }
      localView.setVisibility(i);
    }
    
    private void startTouchUpFilter(int paramInt)
    {
      this.mNumberPreviousOffsets = 0;
      addPositionToTouchUpFilter(paramInt);
    }
    
    private boolean tooLargeTextForMagnifier()
    {
      float f1 = Math.round(Editor.MagnifierMotionAnimator.access$5100(Editor.this.mMagnifierAnimator).getHeight() / Editor.MagnifierMotionAnimator.access$5100(Editor.this.mMagnifierAnimator).getZoom());
      Paint.FontMetrics localFontMetrics = Editor.this.mTextView.getPaint().getFontMetrics();
      float f2 = localFontMetrics.descent;
      float f3 = localFontMetrics.ascent;
      boolean bool;
      if (this.mTextViewScaleY * (f2 - f3) > f1) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    private void updateHandlesVisibility()
    {
      Object localObject = Editor.MagnifierMotionAnimator.access$5100(Editor.this.mMagnifierAnimator).getPosition();
      if (localObject == null) {
        return;
      }
      Rect localRect = Editor.this.mTextView.getViewRootImpl().mWindowAttributes.surfaceInsets;
      ((Point)localObject).offset(-localRect.left, -localRect.top);
      localRect = new Rect(((Point)localObject).x, ((Point)localObject).y, ((Point)localObject).x + Editor.MagnifierMotionAnimator.access$5100(Editor.this.mMagnifierAnimator).getWidth(), ((Point)localObject).y + Editor.MagnifierMotionAnimator.access$5100(Editor.this.mMagnifierAnimator).getHeight());
      setVisible(handleOverlapsMagnifier(this, localRect) ^ true);
      localObject = getOtherSelectionHandle();
      if (localObject != null) {
        ((HandleView)localObject).setVisible(handleOverlapsMagnifier((HandleView)localObject, localRect) ^ true);
      }
    }
    
    protected void dismiss()
    {
      this.mIsDragging = false;
      ((Editor.AnimatePopupWindow)this.mContainer).dismiss();
      onDetached();
    }
    
    protected final void dismissMagnifier()
    {
      if (Editor.this.mMagnifierAnimator != null)
      {
        Editor.MagnifierMotionAnimator.access$5700(Editor.this.mMagnifierAnimator);
        Editor.access$5402(Editor.this, false);
        Editor.this.resumeBlink();
        setVisible(true);
        HandleView localHandleView = getOtherSelectionHandle();
        if (localHandleView != null) {
          localHandleView.setVisible(true);
        }
      }
    }
    
    protected Editor.ActionPopupWindow getActionPopupWindow()
    {
      if (this.mActionPopupWindow == null) {
        this.mActionPopupWindow = new Editor.ActionPopupWindow(Editor.this, this);
      }
      return this.mActionPopupWindow;
    }
    
    public abstract int getCurrentCursorOffset();
    
    int getCursorHorizontalPosition(Layout paramLayout, int paramInt)
    {
      return (int)(getHorizontal(paramLayout, paramInt) - 0.5F);
    }
    
    protected int getCursorOffset()
    {
      return 0;
    }
    
    @VisibleForTesting
    public float getHorizontal(Layout paramLayout, int paramInt)
    {
      return paramLayout.getPrimaryHorizontal(paramInt);
    }
    
    protected abstract int getHorizontalGravity(boolean paramBoolean);
    
    protected int getHorizontalOffset()
    {
      int i = getPreferredWidth();
      int j = this.mDrawable.getIntrinsicWidth();
      int k = this.mHorizontalGravity;
      if (k != 3)
      {
        if (k != 5) {
          j = (i - j) / 2;
        } else {
          j = i - j;
        }
      }
      else {
        j = 0;
      }
      return j;
    }
    
    protected abstract int getHotspotX(Drawable paramDrawable, boolean paramBoolean);
    
    public float getIdealVerticalOffset()
    {
      return this.mIdealVerticalOffset;
    }
    
    protected abstract int getMagnifierHandleTrigger();
    
    int getPreferredHeight()
    {
      return Math.max(this.mDrawable.getIntrinsicHeight(), this.mMinSize);
    }
    
    int getPreferredWidth()
    {
      return Math.max(this.mDrawable.getIntrinsicWidth(), this.mMinSize);
    }
    
    public void hide()
    {
      dismiss();
      Editor.this.getPositionListener().removeSubscriber(this);
    }
    
    protected void hideActionPopupWindow()
    {
      if (this.mActionPopupShower != null) {
        Editor.this.mTextView.removeCallbacks(this.mActionPopupShower);
      }
      Editor.ActionPopupWindow localActionPopupWindow = this.mActionPopupWindow;
      if (localActionPopupWindow != null) {
        localActionPopupWindow.hide();
      }
    }
    
    public boolean isDragging()
    {
      return this.mIsDragging;
    }
    
    public boolean isPopshowing()
    {
      Editor.ActionPopupWindow localActionPopupWindow = this.mActionPopupWindow;
      boolean bool;
      if ((localActionPopupWindow != null) && (localActionPopupWindow.isShowing())) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public boolean isShowing()
    {
      return this.mContainer.isShowing();
    }
    
    public boolean offsetHasBeenChanged()
    {
      int i = this.mNumberPreviousOffsets;
      boolean bool = true;
      if (i <= 1) {
        bool = false;
      }
      return bool;
    }
    
    public void onAttached() {}
    
    public void onDetached()
    {
      hideActionPopupWindow();
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      int i = this.mDrawable.getIntrinsicWidth();
      int j = this.mDrawable.getIntrinsicHeight();
      int k = this.mHandleExtension;
      int m = getHorizontalOffset();
      this.mDrawable.setBounds(m, 0, m + i, j + k - 1);
      this.mDrawable.draw(paramCanvas);
    }
    
    void onHandleMoved()
    {
      hideActionPopupWindow();
      if (HapticFeedbackUtil.isSupportLinearMotorVibrate()) {
        performHapticFeedback(268435462);
      }
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      Layout localLayout = Editor.this.mTextView.getLayout();
      paramInt1 = 0;
      if (localLayout != null)
      {
        paramInt1 = localLayout.getLineForOffset(getCurrentCursorOffset());
        paramInt1 = localLayout.getLineBottom(paramInt1) - localLayout.getLineTop(paramInt1);
      }
      this.mHandleExtension = getHandleExtension(paramInt1);
      paramInt1 = getPreferredHeight();
      paramInt2 = this.mHandleExtension;
      setMeasuredDimension(getPreferredWidth(), paramInt2 + paramInt1);
      paramInt2 = this.mHandleExtension;
      this.mTouchOffsetY = (paramInt2 * 0.5F);
      this.mIdealVerticalOffset = (paramInt1 * 0.5F + paramInt2);
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      int i = paramMotionEvent.getActionMasked();
      if (i != 0)
      {
        if (i != 1)
        {
          if (i != 2)
          {
            if (i != 3) {
              break label292;
            }
          }
          else
          {
            float f1 = paramMotionEvent.getRawX();
            float f2 = paramMotionEvent.getRawY();
            float f3 = this.mTouchToWindowOffsetY;
            i = this.mLastParentY;
            float f4 = i;
            int j = this.mLastWindowY;
            f3 = f3 - f4 - j;
            f4 = f2 - this.mPositionY - i - j;
            float f5 = this.mIdealVerticalOffset;
            if (f3 < f5) {
              f4 = Math.max(Math.min(f4, f5), f3);
            } else {
              f4 = Math.min(Math.max(f4, f5), f3);
            }
            this.mTouchToWindowOffsetY = (this.mLastParentY + f4 + this.mLastWindowY);
            updatePosition(f1 - this.mTouchToWindowOffsetX + this.mHotspotX + getHorizontalOffset(), f2 - this.mTouchToWindowOffsetY + this.mTouchOffsetY);
            break label292;
          }
        }
        else {
          filterOnTouchUp();
        }
        this.mIsDragging = false;
      }
      else
      {
        startTouchUpFilter(getCurrentCursorOffset());
        this.mTouchToWindowOffsetX = (paramMotionEvent.getRawX() - this.mPositionX);
        this.mTouchToWindowOffsetY = (paramMotionEvent.getRawY() - this.mPositionY);
        this.mLastParentX = Editor.this.getPositionListener().getPositionX();
        this.mLastParentY = Editor.this.getPositionListener().getPositionY();
        this.mLastWindowY = Editor.this.mTextView.getRootView().getLocationOnScreen()[1];
        this.mIsDragging = true;
        this.mPreviousLineTouched = -1;
      }
      label292:
      return true;
    }
    
    protected void positionAtCursorOffset(int paramInt, boolean paramBoolean)
    {
      Layout localLayout = Editor.this.mTextView.getLayout();
      if (localLayout == null)
      {
        Editor.this.prepareCursorControllers();
        return;
      }
      int i;
      if (paramInt != this.mPreviousOffset) {
        i = 1;
      } else {
        i = 0;
      }
      if ((i != 0) || (paramBoolean))
      {
        if (i != 0)
        {
          updateSelection(paramInt);
          addPositionToTouchUpFilter(paramInt);
        }
        int j = localLayout.getLineForOffset(paramInt);
        this.mPositionX = ((int)(localLayout.getPrimaryHorizontal(paramInt) * this.mHorizontalScale - this.mHotspotX - getHorizontalOffset() + getCursorOffset()));
        i = getHandleExtension(localLayout.getLineBottom(j) - localLayout.getLineTop(j));
        this.mPositionY = (localLayout.getLineBottom(j) - i);
        if (i != this.mHandleExtension) {
          onMeasure(0, 0);
        }
        this.mPositionX = ((int)(this.mPositionX + Editor.this.mTextView.viewportToContentHorizontalOffset() * this.mHorizontalScale));
        this.mPositionY += Editor.this.mTextView.viewportToContentVerticalOffset();
        this.mPreviousOffset = paramInt;
        this.mPositionHasChanged = true;
      }
    }
    
    void setDrawables(Drawable paramDrawable1, Drawable paramDrawable2)
    {
      this.mDrawableLtr = paramDrawable1;
      this.mDrawableRtl = paramDrawable2;
      updateDrawable(true);
    }
    
    public void show()
    {
      Editor.AnimatePopupWindow localAnimatePopupWindow = (Editor.AnimatePopupWindow)this.mContainer;
      if (localAnimatePopupWindow.isDismissing()) {
        localAnimatePopupWindow.dismiss(false);
      }
      if (isShowing()) {
        return;
      }
      Editor.this.getPositionListener().addSubscriber(this, true);
      this.mPreviousOffset = -1;
      this.mHorizontalScale = Editor.getDescendantViewScale(Editor.this.mTextView);
      positionAtCursorOffset(getCurrentCursorOffset(), false);
    }
    
    void showActionPopupWindow(int paramInt)
    {
      this.mActionPopupWindow = getActionPopupWindow();
      if (this.mActionPopupShower == null) {
        this.mActionPopupShower = new Runnable()
        {
          public void run()
          {
            Editor.HandleView.this.mActionPopupWindow.show();
          }
        };
      }
      Editor.this.mTextView.removeCallbacks(this.mActionPopupShower);
      Editor.this.mTextView.postDelayed(this.mActionPopupShower, paramInt);
    }
    
    protected void updateDrawable(boolean paramBoolean)
    {
      if (!paramBoolean) {
        return;
      }
      Layout localLayout = Editor.this.mTextView.getLayout();
      if (localLayout == null) {
        return;
      }
      int i = getCurrentCursorOffset();
      paramBoolean = Editor.this.mTextView.getLayout().isRtlCharAt(i);
      Drawable localDrawable1 = this.mDrawable;
      Drawable localDrawable2;
      if (paramBoolean) {
        localDrawable2 = this.mDrawableRtl;
      } else {
        localDrawable2 = this.mDrawableLtr;
      }
      this.mDrawable = localDrawable2;
      this.mHotspotX = getHotspotX(this.mDrawable, paramBoolean);
      this.mHorizontalGravity = getHorizontalGravity(paramBoolean);
      if ((localDrawable1 != this.mDrawable) && (isShowing()))
      {
        this.mPositionX = (getCursorHorizontalPosition(localLayout, i) - this.mHotspotX - getHorizontalOffset() + getCursorOffset());
        this.mPositionX += Editor.this.mTextView.viewportToContentHorizontalOffset();
        this.mPositionHasChanged = true;
        updatePosition(this.mLastParentX, this.mLastParentY, false, false);
        postInvalidate();
      }
    }
    
    protected final void updateMagnifier(MotionEvent paramMotionEvent)
    {
      if (Editor.this.mMagnifierAnimator == null) {
        return;
      }
      PointF localPointF = new PointF();
      int i;
      if ((checkForTransforms()) && (!tooLargeTextForMagnifier()) && (obtainMagnifierShowCoordinates(paramMotionEvent, localPointF))) {
        i = 1;
      } else {
        i = 0;
      }
      if (i != 0)
      {
        Editor.access$5402(Editor.this, true);
        Editor.this.mTextView.invalidateCursorPath();
        Editor.this.suspendBlink();
        Editor.MagnifierMotionAnimator.access$5600(Editor.this.mMagnifierAnimator, localPointF.x, localPointF.y);
        updateHandlesVisibility();
      }
      else
      {
        dismissMagnifier();
      }
    }
    
    public abstract void updatePosition(float paramFloat1, float paramFloat2);
    
    public void updatePosition(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
    {
      positionAtCursorOffset(getCurrentCursorOffset(), paramBoolean2);
      if ((paramBoolean1) || (this.mPositionHasChanged))
      {
        if (this.mIsDragging)
        {
          if ((paramInt1 != this.mLastParentX) || (paramInt2 != this.mLastParentY))
          {
            this.mTouchToWindowOffsetX += paramInt1 - this.mLastParentX;
            this.mTouchToWindowOffsetY += paramInt2 - this.mLastParentY;
            this.mLastParentX = paramInt1;
            this.mLastParentY = paramInt2;
          }
          onHandleMoved();
        }
        if (isVisible())
        {
          paramInt1 = this.mPositionX + paramInt1;
          paramInt2 = clipVertically(this.mPositionY + paramInt2);
          if (isShowing())
          {
            this.mContainer.update(paramInt1, paramInt2, -1, -1);
          }
          else
          {
            onAttached();
            this.mContainer.showAtLocation(Editor.this.mTextView, 0, paramInt1, paramInt2);
          }
        }
        else if (isShowing())
        {
          dismiss();
        }
        this.mPositionHasChanged = false;
      }
    }
    
    protected abstract void updateSelection(int paramInt);
  }
  
  static class InputContentType
  {
    boolean enterDown;
    Bundle extras;
    int imeActionId;
    CharSequence imeActionLabel;
    LocaleList imeHintLocales;
    int imeOptions = 0;
    TextView.OnEditorActionListener onEditorActionListener;
    @UnsupportedAppUsage
    String privateImeOptions;
  }
  
  static class InputMethodState
  {
    int mBatchEditNesting;
    int mChangedDelta;
    int mChangedEnd;
    int mChangedStart;
    boolean mContentChanged;
    boolean mCursorChanged;
    Rect mCursorRectInWindow = new Rect();
    final ExtractedText mExtractedText = new ExtractedText();
    ExtractedTextRequest mExtractedTextRequest;
    boolean mSelectionModeChanged;
    float[] mTmpOffset = new float[2];
    RectF mTmpRectF = new RectF();
  }
  
  private class InsertionHandleView
    extends Editor.HandleView
    implements Editor.Fader
  {
    private static final int DELAY_BEFORE_HANDLE_FADES_OUT = 3000;
    private static final int RECENT_CUT_COPY_DURATION = 15000;
    private float mDownPositionX;
    private float mDownPositionY;
    private Runnable mHider;
    private boolean mReShowPopup;
    
    public InsertionHandleView(Drawable paramDrawable)
    {
      super(paramDrawable, paramDrawable, null);
      createAnimations();
      ((Editor.AnimatePopupWindow)this.mContainer).setFader(this);
    }
    
    private void createAnimations()
    {
      this.mAnimationFadeIn = new AnimatorSet();
      ObjectAnimator localObjectAnimator1 = ObjectAnimator.ofFloat(this, View.SCALE_X, new float[] { 0.0F, 1.0F });
      ObjectAnimator localObjectAnimator2 = ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[] { 0.0F, 1.0F });
      this.mAnimationFadeIn.setInterpolator(new BackEaseOutInterpolator());
      this.mAnimationFadeIn.setDuration(300L);
      this.mAnimationFadeIn.playTogether(new Animator[] { localObjectAnimator1, localObjectAnimator2 });
      this.mAnimationFadeOut = new AnimatorSet();
      localObjectAnimator1 = ObjectAnimator.ofFloat(this, View.SCALE_X, new float[] { 1.0F, 0.0F });
      localObjectAnimator2 = ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[] { 1.0F, 0.0F });
      this.mAnimationFadeOut.setInterpolator(new CubicEaseOutInterpolator());
      this.mAnimationFadeOut.setDuration(150L);
      this.mAnimationFadeOut.playTogether(new Animator[] { localObjectAnimator1, localObjectAnimator2 });
      this.mAnimationFadeOutListener = new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          ((Editor.AnimatePopupWindow)Editor.InsertionHandleView.this.mContainer).dismiss(false);
        }
      };
    }
    
    private void hideAfterDelay()
    {
      if (this.mHider == null) {
        this.mHider = new Runnable()
        {
          public void run()
          {
            Editor.InsertionHandleView.this.hide();
          }
        };
      } else {
        removeHiderCallback();
      }
      Editor.this.mTextView.postDelayed(this.mHider, 3000L);
    }
    
    private void removeHiderCallback()
    {
      if (this.mHider != null) {
        Editor.this.mTextView.removeCallbacks(this.mHider);
      }
    }
    
    private void setVisible(boolean paramBoolean)
    {
      View localView = this.mContainer.getContentView();
      int i;
      if (paramBoolean) {
        i = 0;
      } else {
        i = 4;
      }
      localView.setVisibility(i);
    }
    
    public void cancelAnimations()
    {
      this.mAnimationFadeIn.cancel();
      this.mAnimationFadeOut.removeAllListeners();
      this.mAnimationFadeOut.cancel();
      setScaleX(1.0F);
      setScaleY(1.0F);
    }
    
    public void fadeIn(int paramInt1, int paramInt2)
    {
      Layout localLayout = Editor.this.mTextView.getLayout();
      if (localLayout != null)
      {
        paramInt1 = localLayout.getLineForOffset(getCurrentCursorOffset());
        setPivotY(localLayout.getLineBottom(paramInt1) - localLayout.getLineTop(paramInt1));
      }
      this.mAnimationFadeIn.start();
    }
    
    public void fadeOut()
    {
      this.mAnimationFadeOut.removeAllListeners();
      this.mAnimationFadeOut.addListener(this.mAnimationFadeOutListener);
      this.mAnimationFadeOut.start();
    }
    
    public int getCurrentCursorOffset()
    {
      return Editor.this.mTextView.getSelectionStart();
    }
    
    protected int getCursorOffset()
    {
      int i = super.getCursorOffset();
      int j = i;
      if (Editor.this.mDrawableForCursor != null)
      {
        if (Editor.this.mTempRect == null) {
          Editor.access$2402(Editor.this, new Rect());
        }
        Editor.this.mDrawableForCursor.getPadding(Editor.this.mTempRect);
        j = i + (Editor.this.mDrawableForCursor.getIntrinsicWidth() - Editor.this.mTempRect.left - Editor.this.mTempRect.right) / 2;
      }
      return j;
    }
    
    protected int getHorizontalGravity(boolean paramBoolean)
    {
      return 1;
    }
    
    protected int getHotspotX(Drawable paramDrawable, boolean paramBoolean)
    {
      return paramDrawable.getIntrinsicWidth() / 2;
    }
    
    protected int getMagnifierHandleTrigger()
    {
      return 0;
    }
    
    public void hide()
    {
      this.mReShowPopup = false;
      super.hide();
    }
    
    public void onAttached()
    {
      if (this.mReShowPopup)
      {
        showActionPopupWindow(0);
        this.mReShowPopup = false;
      }
      super.onAttached();
      hideAfterDelay();
    }
    
    public void onDetached()
    {
      super.onDetached();
      removeHiderCallback();
    }
    
    void onHandleMoved()
    {
      if (isPopshowing()) {
        this.mReShowPopup = true;
      }
      super.onHandleMoved();
      removeHiderCallback();
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(paramInt1, paramInt2);
      setPivotX(getMeasuredWidth() / 2);
      setPivotY(getMeasuredHeight() - getPreferredHeight());
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      boolean bool = super.onTouchEvent(paramMotionEvent);
      int i = paramMotionEvent.getActionMasked();
      if (i != 0)
      {
        if (i != 1)
        {
          if (i == 3) {
            hideAfterDelay();
          }
        }
        else
        {
          if (!offsetHasBeenChanged())
          {
            float f1 = this.mDownPositionX - paramMotionEvent.getRawX();
            float f2 = this.mDownPositionY - paramMotionEvent.getRawY();
            i = ViewConfiguration.get(Editor.this.mTextView.getContext()).getScaledTouchSlop();
            if (f1 * f1 + f2 * f2 < i * i) {
              if ((this.mActionPopupWindow != null) && (this.mActionPopupWindow.isShowing()))
              {
                this.mReShowPopup = false;
                hideActionPopupWindow();
              }
              else
              {
                showWithActionPopup();
              }
            }
          }
          else
          {
            if (this.mReShowPopup)
            {
              showActionPopupWindow(0);
              this.mReShowPopup = false;
            }
            if (Editor.this.hasSelectionController())
            {
              Editor.this.getSelectionController().setMinTouchOffset(getCurrentCursorOffset());
              Editor.this.getSelectionController().setMaxTouchOffset(getCurrentCursorOffset());
            }
          }
          hideAfterDelay();
        }
      }
      else
      {
        this.mDownPositionX = paramMotionEvent.getRawX();
        this.mDownPositionY = paramMotionEvent.getRawY();
      }
      return bool;
    }
    
    public void setY(int paramInt) {}
    
    public void show()
    {
      super.show();
      long l1 = SystemClock.uptimeMillis();
      long l2 = TextView.sLastCutCopyOrTextChangedTime;
      if ((Editor.this.mInsertionActionModeRunnable != null) && ((Editor.this.mTapState == 2) || (Editor.this.mTapState == 3) || (Editor.this.isCursorInsideEasyCorrectionSpan()))) {
        Editor.this.mTextView.removeCallbacks(Editor.this.mInsertionActionModeRunnable);
      }
      if ((Editor.this.mTapState != 2) && (Editor.this.mTapState != 3) && (!Editor.this.isCursorInsideEasyCorrectionSpan()) && (l1 - l2 < 15000L) && (Editor.this.mTextActionMode == null))
      {
        if (Editor.this.mInsertionActionModeRunnable == null) {
          Editor.access$6002(Editor.this, new Runnable()
          {
            public void run()
            {
              Editor.this.startInsertionActionMode();
            }
          });
        }
        Editor.this.mTextView.postDelayed(Editor.this.mInsertionActionModeRunnable, ViewConfiguration.getDoubleTapTimeout() + 1);
      }
      hideAfterDelay();
    }
    
    public void showWithActionPopup()
    {
      show();
      showActionPopupWindow(0);
    }
    
    public void updatePosition(float paramFloat1, float paramFloat2)
    {
      float f = paramFloat1;
      if (this.mHorizontalScale != 1.0F) {
        f = paramFloat1 / this.mHorizontalScale;
      }
      Layout localLayout = Editor.this.mTextView.getLayout();
      int j;
      if (localLayout != null)
      {
        if (this.mPreviousLineTouched == -1) {
          this.mPreviousLineTouched = Editor.this.mTextView.getLineAtCoordinate(paramFloat2);
        }
        int i = Editor.this.getCurrentLineAdjustedForSlop(localLayout, this.mPreviousLineTouched, paramFloat2);
        j = Editor.this.mTextView.getOffsetAtCoordinate(i, f);
        this.mPreviousLineTouched = i;
      }
      else
      {
        j = Editor.this.mTextView.getOffsetForPosition(f, paramFloat2);
      }
      positionAtCursorOffset(j, false);
    }
    
    public void updateSelection(int paramInt)
    {
      int i = paramInt;
      if (paramInt < 0) {
        i = 0;
      }
      Selection.setSelection((Spannable)Editor.this.mTextView.getText(), i);
    }
  }
  
  private class InsertionPointCursorController
    implements Editor.CursorController
  {
    private Editor.InsertionHandleView mHandle;
    
    private InsertionPointCursorController() {}
    
    private Editor.InsertionHandleView getHandle()
    {
      Editor localEditor;
      if (Editor.this.mSelectHandleCenter == null)
      {
        localEditor = Editor.this;
        Editor.access$6702(localEditor, localEditor.mTextView.getResources().getDrawable(285671931));
      }
      if (this.mHandle == null)
      {
        localEditor = Editor.this;
        this.mHandle = new Editor.InsertionHandleView(localEditor, localEditor.mSelectHandleCenter);
      }
      return this.mHandle;
    }
    
    private void reloadHandleDrawable()
    {
      Editor.InsertionHandleView localInsertionHandleView = this.mHandle;
      if (localInsertionHandleView == null) {
        return;
      }
      localInsertionHandleView.setDrawables(Editor.this.mSelectHandleCenter, Editor.this.mSelectHandleCenter);
    }
    
    public void hide()
    {
      Editor.InsertionHandleView localInsertionHandleView = this.mHandle;
      if (localInsertionHandleView != null) {
        localInsertionHandleView.hide();
      }
    }
    
    public void invalidateHandle()
    {
      Editor.InsertionHandleView localInsertionHandleView = this.mHandle;
      if (localInsertionHandleView != null) {
        localInsertionHandleView.invalidate();
      }
    }
    
    public boolean isActive()
    {
      Editor.InsertionHandleView localInsertionHandleView = this.mHandle;
      boolean bool;
      if ((localInsertionHandleView != null) && (localInsertionHandleView.isShowing())) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public boolean isCursorBeingModified()
    {
      Editor.InsertionHandleView localInsertionHandleView = this.mHandle;
      boolean bool;
      if ((localInsertionHandleView != null) && (localInsertionHandleView.isDragging())) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public void onDetached()
    {
      Editor.this.mTextView.getViewTreeObserver().removeOnTouchModeChangeListener(this);
      Editor.InsertionHandleView localInsertionHandleView = this.mHandle;
      if (localInsertionHandleView != null) {
        localInsertionHandleView.onDetached();
      }
    }
    
    public void onTouchModeChanged(boolean paramBoolean)
    {
      if (!paramBoolean) {
        hide();
      }
    }
    
    public void show()
    {
      getHandle().show();
      getHandle().setVisibility(0);
    }
    
    public void showWithActionPopup()
    {
      getHandle().showWithActionPopup();
      if (Editor.this.mTextView.getText().length() == 0) {
        getHandle().setVisibility(8);
      } else {
        getHandle().setVisibility(0);
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface MagnifierHandleTrigger
  {
    public static final int INSERTION = 0;
    public static final int SELECTION_END = 2;
    public static final int SELECTION_START = 1;
  }
  
  private static class MagnifierMotionAnimator
  {
    private static final long DURATION = 100L;
    private float mAnimationCurrentX;
    private float mAnimationCurrentY;
    private float mAnimationStartX;
    private float mAnimationStartY;
    private final ValueAnimator mAnimator;
    private float mLastX;
    private float mLastY;
    private final Magnifier mMagnifier;
    private boolean mMagnifierIsShowing;
    
    private MagnifierMotionAnimator(Magnifier paramMagnifier)
    {
      this.mMagnifier = paramMagnifier;
      this.mAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
      this.mAnimator.setDuration(100L);
      this.mAnimator.setInterpolator(new LinearInterpolator());
      this.mAnimator.addUpdateListener(new _..Lambda.Editor.MagnifierMotionAnimator.E_RaelOMgCHAzvKgSSZE_hDYeIg(this));
    }
    
    private void dismiss()
    {
      this.mMagnifier.dismiss();
      this.mAnimator.cancel();
      this.mMagnifierIsShowing = false;
    }
    
    private void show(float paramFloat1, float paramFloat2)
    {
      int i;
      if ((this.mMagnifierIsShowing) && (paramFloat2 != this.mLastY)) {
        i = 1;
      } else {
        i = 0;
      }
      if (i != 0)
      {
        if (this.mAnimator.isRunning())
        {
          this.mAnimator.cancel();
          this.mAnimationStartX = this.mAnimationCurrentX;
          this.mAnimationStartY = this.mAnimationCurrentY;
        }
        else
        {
          this.mAnimationStartX = this.mLastX;
          this.mAnimationStartY = this.mLastY;
        }
        this.mAnimator.start();
      }
      else if (!this.mAnimator.isRunning())
      {
        this.mMagnifier.show(paramFloat1, paramFloat2);
      }
      this.mLastX = paramFloat1;
      this.mLastY = paramFloat2;
      this.mMagnifierIsShowing = true;
    }
    
    private void update()
    {
      this.mMagnifier.update();
    }
  }
  
  private class PhraseAdapter
    extends BaseAdapter
  {
    private LayoutInflater mInflater = null;
    private int mPaddingHorizontal;
    private int mPaddingVerticalLarge;
    private int mPaddingVerticalSmall;
    private ArrayList<String> mPhraseList = null;
    
    public PhraseAdapter()
    {
      ArrayList localArrayList;
      this.mPhraseList = localArrayList;
      this.mPaddingHorizontal = Editor.this.mTextView.getContext().getResources().getDimensionPixelSize(285606041);
      this.mPaddingVerticalSmall = Editor.this.mTextView.getContext().getResources().getDimensionPixelSize(285606042);
      this.mPaddingVerticalLarge = Editor.this.mTextView.getContext().getResources().getDimensionPixelSize(285606043);
    }
    
    public int getCount()
    {
      ArrayList localArrayList = this.mPhraseList;
      int i;
      if (localArrayList == null) {
        i = 0;
      } else {
        i = localArrayList.size();
      }
      return i;
    }
    
    public Object getItem(int paramInt)
    {
      ArrayList localArrayList = this.mPhraseList;
      if ((localArrayList != null) && (paramInt < localArrayList.size())) {
        return this.mPhraseList.get(paramInt);
      }
      return null;
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null) {
        paramView = (TextView)this.mInflater.inflate(285933611, paramViewGroup, false);
      } else {
        paramView = (TextView)paramView;
      }
      paramView.getPaddingTop();
      paramView.getPaddingBottom();
      paramView.setText((String)getItem(paramInt));
      int i = this.mPaddingVerticalSmall;
      if (getCount() == 1)
      {
        paramView.getBackground().setLevel(3);
        paramInt = this.mPaddingVerticalLarge;
      }
      else if (paramInt == 0)
      {
        paramView.getBackground().setLevel(0);
        paramInt = this.mPaddingVerticalSmall;
      }
      else if (paramInt == getCount() - 1)
      {
        paramView.getBackground().setLevel(2);
        paramInt = this.mPaddingVerticalLarge;
      }
      else
      {
        paramView.getBackground().setLevel(1);
        paramInt = this.mPaddingVerticalSmall;
      }
      int j = this.mPaddingHorizontal;
      paramView.setPaddingRelative(j, i, j, paramInt);
      return paramView;
    }
  }
  
  private abstract class PinnedPopupWindow
    implements Editor.TextViewPositionListener
  {
    protected ViewGroup mContentView;
    protected int mPopupElevation = MiuiVersionHelper.getPopupElevation(Editor.this.mTextView.getResources());
    protected PopupWindow mPopupWindow;
    int mPositionX;
    int mPositionY;
    
    public PinnedPopupWindow()
    {
      createPopupWindow();
      this.mPopupWindow.setWindowLayoutType(1002);
      this.mPopupWindow.setWidth(-2);
      this.mPopupWindow.setHeight(-2);
      initContentView();
      this$1 = new ViewGroup.LayoutParams(-2, -2);
      this.mContentView.setLayoutParams(Editor.this);
      this.mPopupWindow.setContentView(this.mContentView);
    }
    
    protected abstract int clipVertically(int paramInt);
    
    protected void computeLocalPosition()
    {
      measureContent();
      int i = this.mContentView.getMeasuredWidth();
      int j = getTextOffset();
      Layout localLayout = Editor.this.mTextView.getLayout();
      if (localLayout != null)
      {
        this.mPositionX = ((int)(localLayout.getPrimaryHorizontal(j) - i / 2.0F));
        this.mPositionX += Editor.this.mTextView.viewportToContentHorizontalOffset();
        this.mPositionY = getVerticalLocalPosition(localLayout.getLineForOffset(j));
        this.mPositionY += Editor.this.mTextView.viewportToContentVerticalOffset();
      }
      else
      {
        this.mPositionX = 0;
        this.mPositionY = Editor.this.mTextView.getCompoundPaddingTop();
      }
    }
    
    protected abstract void createPopupWindow();
    
    public void dismiss()
    {
      this.mPopupWindow.dismiss();
      Editor.this.onPopupWindowDismiss(this);
    }
    
    protected abstract int getTextOffset();
    
    protected abstract int getVerticalLocalPosition(int paramInt);
    
    public void hide()
    {
      dismiss();
      Editor.this.getPositionListener().removeSubscriber(this);
    }
    
    protected abstract void initContentView();
    
    public boolean isShowing()
    {
      return this.mPopupWindow.isShowing();
    }
    
    protected void measureContent()
    {
      DisplayMetrics localDisplayMetrics = Editor.this.mTextView.getResources().getDisplayMetrics();
      this.mContentView.measure(View.MeasureSpec.makeMeasureSpec(localDisplayMetrics.widthPixels, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(localDisplayMetrics.heightPixels, Integer.MIN_VALUE));
    }
    
    public void show()
    {
      Editor.this.onPopupWindowShown(this);
      Editor.this.getPositionListener().addSubscriber(this, false);
      computeLocalPosition();
      int i = this.mPopupElevation;
      if (i > 0)
      {
        this.mContentView.setElevation(i);
        this.mPopupWindow.setElevation(this.mContentView.getElevation());
        this.mPopupWindow.setContentView(this.mContentView);
      }
      Editor.PositionListener localPositionListener = Editor.this.getPositionListener();
      updatePosition(localPositionListener.getPositionX(), localPositionListener.getPositionY());
      if (HapticFeedbackUtil.isSupportLinearMotorVibrate()) {
        this.mPopupWindow.getContentView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener()
        {
          public void onViewAttachedToWindow(View paramAnonymousView)
          {
            paramAnonymousView.performHapticFeedback(268435464);
          }
          
          public void onViewDetachedFromWindow(View paramAnonymousView)
          {
            paramAnonymousView.removeOnAttachStateChangeListener(this);
          }
        });
      }
    }
    
    protected void updatePosition(int paramInt1, int paramInt2)
    {
      int i = this.mPositionX;
      paramInt2 = clipVertically(this.mPositionY + paramInt2);
      DisplayMetrics localDisplayMetrics = Editor.this.mTextView.getResources().getDisplayMetrics();
      int j = this.mContentView.getMeasuredWidth();
      paramInt1 = Math.max(0, Math.min(localDisplayMetrics.widthPixels - j, i + paramInt1));
      if (isShowing()) {
        this.mPopupWindow.update(paramInt1, paramInt2, -1, -1);
      } else {
        this.mPopupWindow.showAtLocation(Editor.this.mTextView, 0, paramInt1, paramInt2);
      }
    }
    
    public void updatePosition(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
    {
      if ((isShowing()) && (Editor.this.isOffsetVisible(getTextOffset())))
      {
        if (paramBoolean2) {
          computeLocalPosition();
        }
        updatePosition(paramInt1, paramInt2);
      }
      else
      {
        hide();
      }
    }
  }
  
  private class PositionListener
    implements ViewTreeObserver.OnPreDrawListener
  {
    private final int MAXIMUM_NUMBER_OF_LISTENERS = 6;
    private boolean[] mCanMove = new boolean[6];
    private int mNumberOfListeners;
    private boolean mPositionHasChanged = true;
    private Editor.TextViewPositionListener[] mPositionListeners = new Editor.TextViewPositionListener[6];
    private int mPositionX;
    private int mPositionY;
    private boolean mScrollHasChanged;
    final int[] mTempCoords = new int[2];
    
    private PositionListener() {}
    
    private void updatePosition()
    {
      Editor.this.mTextView.getLocationInWindow(this.mTempCoords);
      int[] arrayOfInt = this.mTempCoords;
      boolean bool;
      if ((arrayOfInt[0] == this.mPositionX) && (arrayOfInt[1] == this.mPositionY)) {
        bool = false;
      } else {
        bool = true;
      }
      this.mPositionHasChanged = bool;
      arrayOfInt = this.mTempCoords;
      this.mPositionX = arrayOfInt[0];
      this.mPositionY = arrayOfInt[1];
    }
    
    public void addSubscriber(Editor.TextViewPositionListener paramTextViewPositionListener, boolean paramBoolean)
    {
      if (this.mNumberOfListeners == 0)
      {
        updatePosition();
        Editor.this.mTextView.getViewTreeObserver().addOnPreDrawListener(this);
      }
      int i = -1;
      int j = 0;
      while (j < 6)
      {
        Editor.TextViewPositionListener localTextViewPositionListener = this.mPositionListeners[j];
        if (localTextViewPositionListener == paramTextViewPositionListener) {
          return;
        }
        int k = i;
        if (i < 0)
        {
          k = i;
          if (localTextViewPositionListener == null) {
            k = j;
          }
        }
        j++;
        i = k;
      }
      this.mPositionListeners[i] = paramTextViewPositionListener;
      this.mCanMove[i] = paramBoolean;
      this.mNumberOfListeners += 1;
    }
    
    public int getPositionX()
    {
      return this.mPositionX;
    }
    
    public int getPositionY()
    {
      return this.mPositionY;
    }
    
    public boolean onPreDraw()
    {
      updatePosition();
      for (int i = 0; i < 6; i++) {
        if ((this.mPositionHasChanged) || (this.mScrollHasChanged) || (this.mCanMove[i] != 0))
        {
          Editor.TextViewPositionListener localTextViewPositionListener = this.mPositionListeners[i];
          if (localTextViewPositionListener != null) {
            localTextViewPositionListener.updatePosition(this.mPositionX, this.mPositionY, this.mPositionHasChanged, this.mScrollHasChanged);
          }
        }
      }
      this.mScrollHasChanged = false;
      return true;
    }
    
    public void onScrollChanged()
    {
      this.mScrollHasChanged = true;
    }
    
    public void removeSubscriber(Editor.TextViewPositionListener paramTextViewPositionListener)
    {
      for (int i = 0; i < 6; i++)
      {
        Editor.TextViewPositionListener[] arrayOfTextViewPositionListener = this.mPositionListeners;
        if (arrayOfTextViewPositionListener[i] == paramTextViewPositionListener)
        {
          arrayOfTextViewPositionListener[i] = null;
          this.mNumberOfListeners -= 1;
          break;
        }
      }
      if (this.mNumberOfListeners == 0) {
        Editor.this.mTextView.getViewTreeObserver().removeOnPreDrawListener(this);
      }
    }
  }
  
  static final class ProcessTextIntentActionsHandler
  {
    private ProcessTextIntentActionsHandler(Editor paramEditor) {}
    
    public void initializeAccessibilityActions() {}
    
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo) {}
    
    public boolean performAccessibilityAction(int paramInt)
    {
      return false;
    }
  }
  
  private class SelectionEndHandleView
    extends Editor.SelectionHandleView
  {
    public SelectionEndHandleView(Drawable paramDrawable1, Drawable paramDrawable2)
    {
      super(paramDrawable1, paramDrawable2);
    }
    
    private void positionAndAdjustForCrossingHandles(int paramInt)
    {
      int i = Editor.this.mTextView.getSelectionStart();
      int j = paramInt;
      if (paramInt <= i) {
        j = Math.min(i + 1, Editor.this.mTextView.getText().length());
      }
      positionAtCursorOffset(j, false);
    }
    
    public int getCurrentCursorOffset()
    {
      return Editor.this.mTextView.getSelectionEnd();
    }
    
    protected int getHorizontalGravity(boolean paramBoolean)
    {
      int i;
      if (paramBoolean) {
        i = 5;
      } else {
        i = 3;
      }
      return i;
    }
    
    protected int getHotspotX(Drawable paramDrawable, boolean paramBoolean)
    {
      if (paramBoolean) {
        return paramDrawable.getIntrinsicWidth() * 3 / 4;
      }
      return paramDrawable.getIntrinsicWidth() / 4;
    }
    
    public void updatePosition(float paramFloat1, float paramFloat2)
    {
      Layout localLayout = Editor.this.mTextView.getLayout();
      if (localLayout == null)
      {
        positionAndAdjustForCrossingHandles(Editor.this.mTextView.getOffsetForPosition(paramFloat1, paramFloat2));
        return;
      }
      if (this.mPreviousLineTouched == -1) {
        this.mPreviousLineTouched = Editor.this.mTextView.getLineAtCoordinate(paramFloat2);
      }
      int i = Editor.this.mTextView.getSelectionStart();
      int j = Editor.this.getCurrentLineAdjustedForSlop(localLayout, this.mPreviousLineTouched, paramFloat2);
      int k = Editor.this.mTextView.getOffsetAtCoordinate(j, paramFloat1);
      int m = k;
      if (k <= i)
      {
        j = localLayout.getLineForOffset(i);
        m = Editor.this.mTextView.getOffsetAtCoordinate(j, paramFloat1);
      }
      this.mPreviousLineTouched = j;
      positionAndAdjustForCrossingHandles(m);
    }
    
    public void updateSelection(int paramInt)
    {
      Selection.setSelection((Spannable)Editor.this.mTextView.getText(), Editor.this.mTextView.getSelectionStart(), paramInt);
      updateDrawable(false);
    }
  }
  
  @VisibleForTesting
  public abstract class SelectionHandleView
    extends Editor.HandleView
    implements Editor.Fader
  {
    @Editor.HandleType
    private final int mHandleType = 0;
    private float mTranslation;
    private int mY = -1;
    
    public SelectionHandleView(Drawable paramDrawable1, Drawable paramDrawable2)
    {
      super(paramDrawable1, paramDrawable2, null);
      setPivotX(this.mHotspotX + getHorizontalOffset());
      ((Editor.AnimatePopupWindow)this.mContainer).setFader(this);
    }
    
    private float getHorizontal(Layout paramLayout, int paramInt, boolean paramBoolean)
    {
      int i = paramLayout.getLineForOffset(paramInt);
      boolean bool1 = false;
      int j;
      if (paramBoolean) {
        j = paramInt;
      } else {
        j = Math.max(paramInt - 1, 0);
      }
      boolean bool2 = paramLayout.isRtlCharAt(j);
      paramBoolean = bool1;
      if (paramLayout.getParagraphDirection(i) == -1) {
        paramBoolean = true;
      }
      float f;
      if (bool2 == paramBoolean) {
        f = paramLayout.getPrimaryHorizontal(paramInt);
      } else {
        f = paramLayout.getSecondaryHorizontal(paramInt);
      }
      return f;
    }
    
    private boolean isStartHandle()
    {
      return true;
    }
    
    public void cancelAnimations()
    {
      if (this.mAnimationFadeIn != null) {
        this.mAnimationFadeIn.cancel();
      }
      setScaleX(1.0F);
      setTranslationX(0.0F);
    }
    
    public void fadeIn(int paramInt1, final int paramInt2)
    {
      float f1 = paramInt1;
      float f2 = this.mTranslation;
      float f3 = this.mHorizontalScale;
      this.mY = -1;
      this.mAnimationFadeIn = new AnimatorSet();
      this.mAnimationFadeIn.setInterpolator(new CubicEaseOutInterpolator());
      this.mAnimationFadeIn.setDuration(300L);
      ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(this, View.SCALE_X, new float[] { 0.0F, 1.0F });
      if (this.mTranslation != 0.0F)
      {
        ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { f1 + f2 * f3, paramInt1 });
        localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
          public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
          {
            float f = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
            if (Editor.SelectionHandleView.this.mY == -1) {
              Editor.SelectionHandleView.access$6602(Editor.SelectionHandleView.this, paramInt2);
            }
            Editor.SelectionHandleView.this.mContainer.update((int)f, Editor.SelectionHandleView.this.mY, -1, -1);
          }
        });
        this.mAnimationFadeIn.playTogether(new Animator[] { localObjectAnimator, localValueAnimator });
        this.mTranslation = 0.0F;
      }
      else
      {
        this.mAnimationFadeIn.playTogether(new Animator[] { localObjectAnimator });
      }
      this.mAnimationFadeIn.start();
    }
    
    public void fadeOut()
    {
      ((Editor.AnimatePopupWindow)this.mContainer).dismiss();
    }
    
    public Editor.ActionPopupWindow getActionPopupWindow()
    {
      if (this.mActionPopupWindow == null) {
        this.mActionPopupWindow = new Editor.SelectionPopupWindow(Editor.this, this);
      }
      return this.mActionPopupWindow;
    }
    
    public float getHorizontal(Layout paramLayout, int paramInt)
    {
      return getHorizontal(paramLayout, paramInt, isStartHandle());
    }
    
    protected int getMagnifierHandleTrigger()
    {
      int i;
      if (isStartHandle()) {
        i = 1;
      } else {
        i = 2;
      }
      return i;
    }
    
    public void onAttached()
    {
      showActionPopupWindow(0);
    }
    
    public void onDetached()
    {
      if ((Editor.this.hasSelectionController()) && (!Editor.SelectionModifierCursorController.access$6500(Editor.this.getSelectionController()))) {
        hideActionPopupWindow();
      }
      Editor.this.hideEmailPopupWindow();
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      boolean bool = super.onTouchEvent(paramMotionEvent);
      if (paramMotionEvent.getActionMasked() == 1) {
        showActionPopupWindow(300);
      }
      return bool;
    }
    
    public void setActionPopupWindow(Editor.ActionPopupWindow paramActionPopupWindow)
    {
      this.mActionPopupWindow = paramActionPopupWindow;
    }
    
    public void setTranslation(float paramFloat)
    {
      this.mTranslation = paramFloat;
    }
    
    public void setY(int paramInt)
    {
      this.mY = paramInt;
    }
  }
  
  class SelectionModifierCursorController
    implements Editor.CursorController
  {
    private static final int DELAY_BEFORE_REPLACE_ACTION = 200;
    private static final int DRAG_ACCELERATOR_MODE_CHARACTER = 1;
    private static final int DRAG_ACCELERATOR_MODE_INACTIVE = 0;
    private static final int DRAG_ACCELERATOR_MODE_PARAGRAPH = 3;
    private static final int DRAG_ACCELERATOR_MODE_WORD = 2;
    private boolean mDoubleTabed;
    private float mDownPositionX;
    private float mDownPositionY;
    private int mDragAcceleratorMode = 0;
    private Editor.SelectionHandleView mEndHandle;
    private boolean mGestureStayedInTapRegion;
    private boolean mHaventMovedEnoughToStartDrag;
    private boolean mInSwipeSelectionMode;
    private int mLineSelectionIsOn = -1;
    private int mMaxTouchOffset;
    private int mMinTouchOffset;
    private long mPreviousTapUpTime = 0L;
    private Editor.SelectionHandleView mStartHandle;
    private int mStartOffset = -1;
    private int mSwipeSelectionStart;
    private boolean mSwitchedLines = false;
    private boolean mTextSelectionModeEnable;
    private float[] mTranslationCache = { 0.0F, 0.0F };
    
    SelectionModifierCursorController()
    {
      resetTouchOffsets();
    }
    
    private void initDrawables()
    {
      Editor localEditor;
      if (Editor.this.mSelectHandleLeft == null)
      {
        localEditor = Editor.this;
        Editor.access$6802(localEditor, localEditor.mTextView.getContext().getResources().getDrawable(285671928));
      }
      if (Editor.this.mSelectHandleRight == null)
      {
        localEditor = Editor.this;
        Editor.access$6902(localEditor, localEditor.mTextView.getContext().getResources().getDrawable(285671934));
      }
    }
    
    private void initHandleView()
    {
      Editor localEditor;
      if (this.mStartHandle == null)
      {
        localEditor = Editor.this;
        this.mStartHandle = new Editor.SelectionStartHandleView(localEditor, localEditor.mSelectHandleLeft, Editor.this.mSelectHandleRight);
      }
      if (this.mEndHandle == null)
      {
        localEditor = Editor.this;
        this.mEndHandle = new Editor.SelectionEndHandleView(localEditor, localEditor.mSelectHandleRight, Editor.this.mSelectHandleLeft);
      }
    }
    
    private void initHandles()
    {
      initHandleView();
      this.mStartHandle.setTranslation(this.mTranslationCache[0]);
      this.mEndHandle.setTranslation(this.mTranslationCache[1]);
      this.mStartHandle.show();
      this.mEndHandle.show();
      this.mStartHandle.showActionPopupWindow(200);
      this.mEndHandle.setActionPopupWindow(this.mStartHandle.getActionPopupWindow());
      Editor.this.hideInsertionPointCursorController();
    }
    
    private void reloadHandleDrawables()
    {
      Editor.SelectionHandleView localSelectionHandleView = this.mStartHandle;
      if (localSelectionHandleView == null) {
        return;
      }
      localSelectionHandleView.setDrawables(Editor.this.mSelectHandleLeft, Editor.this.mSelectHandleRight);
      this.mEndHandle.setDrawables(Editor.this.mSelectHandleRight, Editor.this.mSelectHandleLeft);
    }
    
    private void requestDisallowInterceptTouchEvent(boolean paramBoolean)
    {
      ViewParent localViewParent = Editor.this.mTextView.getParent();
      if (localViewParent != null) {
        localViewParent.requestDisallowInterceptTouchEvent(paramBoolean);
      }
    }
    
    private void resetDragAcceleratorState()
    {
      this.mStartOffset = -1;
      this.mDragAcceleratorMode = 0;
      this.mSwitchedLines = false;
      int i = Editor.this.mTextView.getSelectionStart();
      int j = Editor.this.mTextView.getSelectionEnd();
      if (i > j) {
        Selection.setSelection((Spannable)Editor.this.mTextView.getText(), j, i);
      }
    }
    
    private boolean selectCurrentParagraphAndStartDrag()
    {
      if (Editor.this.mInsertionActionModeRunnable != null) {
        Editor.this.mTextView.removeCallbacks(Editor.this.mInsertionActionModeRunnable);
      }
      Editor.this.stopTextActionMode();
      if (!Editor.this.selectCurrentParagraph()) {
        return false;
      }
      enterDrag(3);
      return true;
    }
    
    private void updateCharacterBasedSelection(MotionEvent paramMotionEvent)
    {
      int i = Editor.this.mTextView.getOffsetForPosition(paramMotionEvent.getX(), paramMotionEvent.getY());
      Selection.setSelection((Spannable)Editor.this.mTextView.getText(), this.mStartOffset, i);
    }
    
    private void updateMinAndMaxOffsets(MotionEvent paramMotionEvent)
    {
      int i = paramMotionEvent.getPointerCount();
      for (int j = 0; j < i; j++)
      {
        int k = Editor.this.mTextView.getOffsetForPosition(paramMotionEvent.getX(j), paramMotionEvent.getY(j));
        if (k < this.mMinTouchOffset) {
          this.mMinTouchOffset = k;
        }
        if (k > this.mMaxTouchOffset) {
          this.mMaxTouchOffset = k;
        }
      }
    }
    
    private void updateParagraphBasedSelection(MotionEvent paramMotionEvent)
    {
      int i = Editor.this.mTextView.getOffsetForPosition(paramMotionEvent.getX(), paramMotionEvent.getY());
      int j = Math.min(i, this.mStartOffset);
      i = Math.max(i, this.mStartOffset);
      long l = Editor.this.getParagraphsRange(j, i);
      i = TextUtils.unpackRangeStartFromLong(l);
      j = TextUtils.unpackRangeEndFromLong(l);
      Selection.setSelection((Spannable)Editor.this.mTextView.getText(), i, j);
    }
    
    private void updateSelection(MotionEvent paramMotionEvent)
    {
      if (Editor.this.mTextView.getLayout() != null)
      {
        int i = this.mDragAcceleratorMode;
        if (i != 1)
        {
          if (i != 2)
          {
            if (i == 3) {
              updateParagraphBasedSelection(paramMotionEvent);
            }
          }
          else {
            updateWordBasedSelection(paramMotionEvent);
          }
        }
        else {
          updateCharacterBasedSelection(paramMotionEvent);
        }
      }
    }
    
    private void updateWordBasedSelection(MotionEvent paramMotionEvent)
    {
      if (this.mHaventMovedEnoughToStartDrag) {
        return;
      }
      boolean bool = paramMotionEvent.isFromSource(8194);
      ViewConfiguration localViewConfiguration = ViewConfiguration.get(Editor.this.mTextView.getContext());
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      int i;
      if (bool)
      {
        i = Editor.this.mTextView.getLineAtCoordinate(f2);
      }
      else
      {
        float f3 = f2;
        if (this.mSwitchedLines)
        {
          j = localViewConfiguration.getScaledTouchSlop();
          paramMotionEvent = this.mStartHandle;
          if (paramMotionEvent != null) {
            f3 = paramMotionEvent.getIdealVerticalOffset();
          } else {
            f3 = j;
          }
          f3 = f2 - f3;
        }
        paramMotionEvent = Editor.this;
        i = paramMotionEvent.getCurrentLineAdjustedForSlop(paramMotionEvent.mTextView.getLayout(), this.mLineSelectionIsOn, f3);
        if ((!this.mSwitchedLines) && (i != this.mLineSelectionIsOn))
        {
          this.mSwitchedLines = true;
          return;
        }
      }
      int j = Editor.this.mTextView.getOffsetAtCoordinate(i, f1);
      int k;
      if (this.mStartOffset < j)
      {
        j = Editor.this.getWordEnd(j);
        k = Editor.this.getWordStart(this.mStartOffset);
      }
      else
      {
        int m = Editor.this.getWordStart(j);
        int n = Editor.this.getWordEnd(this.mStartOffset);
        j = m;
        k = n;
        if (n == m)
        {
          j = Editor.this.getNextCursorOffset(m, false);
          k = n;
        }
      }
      this.mLineSelectionIsOn = i;
      Selection.setSelection((Spannable)Editor.this.mTextView.getText(), k, j);
    }
    
    public void enterDrag(int paramInt)
    {
      show();
      this.mDragAcceleratorMode = paramInt;
      this.mStartOffset = Editor.this.mTextView.getOffsetForPosition(Editor.this.mLastDownPositionX, Editor.this.mLastDownPositionY);
      this.mLineSelectionIsOn = Editor.this.mTextView.getLineAtCoordinate(Editor.this.mLastDownPositionY);
      hide();
      Editor.this.mTextView.getParent().requestDisallowInterceptTouchEvent(true);
      Editor.this.mTextView.cancelLongPress();
    }
    
    public int getMaxTouchOffset()
    {
      return this.mMaxTouchOffset;
    }
    
    public int getMinTouchOffset()
    {
      return this.mMinTouchOffset;
    }
    
    public void hide()
    {
      this.mTextSelectionModeEnable = false;
      Editor.SelectionHandleView localSelectionHandleView = this.mStartHandle;
      if (localSelectionHandleView != null) {
        localSelectionHandleView.hide();
      }
      localSelectionHandleView = this.mEndHandle;
      if (localSelectionHandleView != null) {
        localSelectionHandleView.hide();
      }
    }
    
    public void invalidateHandles()
    {
      Editor.SelectionHandleView localSelectionHandleView = this.mStartHandle;
      if (localSelectionHandleView != null) {
        localSelectionHandleView.invalidate();
      }
      localSelectionHandleView = this.mEndHandle;
      if (localSelectionHandleView != null) {
        localSelectionHandleView.invalidate();
      }
    }
    
    public boolean isActive()
    {
      Editor.SelectionHandleView localSelectionHandleView = this.mStartHandle;
      boolean bool;
      if ((localSelectionHandleView != null) && (localSelectionHandleView.isShowing())) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public boolean isCursorBeingModified()
    {
      if ((!isDragAcceleratorActive()) && (!isSelectionStartDragged()))
      {
        Editor.SelectionHandleView localSelectionHandleView = this.mEndHandle;
        if ((localSelectionHandleView == null) || (!localSelectionHandleView.isDragging())) {
          return false;
        }
      }
      boolean bool = true;
      return bool;
    }
    
    public boolean isDragAcceleratorActive()
    {
      boolean bool;
      if (this.mDragAcceleratorMode != 0) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public boolean isSelectionStartDragged()
    {
      Editor.SelectionHandleView localSelectionHandleView = this.mStartHandle;
      boolean bool;
      if ((localSelectionHandleView != null) && (localSelectionHandleView.isDragging())) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public void onDetached()
    {
      Editor.this.mTextView.getViewTreeObserver().removeOnTouchModeChangeListener(this);
      Editor.SelectionHandleView localSelectionHandleView = this.mStartHandle;
      if (localSelectionHandleView != null) {
        localSelectionHandleView.hideActionPopupWindow();
      }
      localSelectionHandleView = this.mEndHandle;
      if (localSelectionHandleView != null) {
        localSelectionHandleView.hideActionPopupWindow();
      }
    }
    
    public void onTouchEvent(MotionEvent paramMotionEvent)
    {
      boolean bool1 = paramMotionEvent.isFromSource(8194);
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      int i = paramMotionEvent.getActionMasked();
      int j = 0;
      float f3;
      float f4;
      if (i != 0)
      {
        Object localObject;
        if (i != 1)
        {
          if (i != 2)
          {
            if (i != 3)
            {
              if ((i == 5) || (i == 6))
              {
                if (!Editor.this.mTextView.getContext().getPackageManager().hasSystemFeature("android.hardware.touchscreen.multitouch.distinct")) {
                  return;
                }
                updateMinAndMaxOffsets(paramMotionEvent);
                return;
              }
            }
            else
            {
              paramMotionEvent = this.mEndHandle;
              if (paramMotionEvent == null) {
                return;
              }
              paramMotionEvent.dismissMagnifier();
              return;
            }
          }
          else
          {
            if ((this.mGestureStayedInTapRegion) || (this.mHaventMovedEnoughToStartDrag))
            {
              f3 = f1 - this.mDownPositionX;
              f4 = f2 - this.mDownPositionY;
              f4 = f3 * f3 + f4 * f4;
              localObject = ViewConfiguration.get(Editor.this.mTextView.getContext());
              i = ((ViewConfiguration)localObject).getScaledDoubleTapTouchSlop();
              j = ((ViewConfiguration)localObject).getScaledTouchSlop();
              boolean bool2;
              if (this.mGestureStayedInTapRegion)
              {
                if (f4 <= i * i) {
                  bool2 = true;
                } else {
                  bool2 = false;
                }
                this.mGestureStayedInTapRegion = bool2;
              }
              if (this.mHaventMovedEnoughToStartDrag)
              {
                if (f4 <= j * j) {
                  bool2 = true;
                } else {
                  bool2 = false;
                }
                this.mHaventMovedEnoughToStartDrag = bool2;
              }
            }
            if ((bool1) && (!isDragAcceleratorActive()))
            {
              j = Editor.this.mTextView.getOffsetForPosition(f1, f2);
              if ((Editor.this.mTextView.hasSelection()) && ((!this.mHaventMovedEnoughToStartDrag) || (this.mStartOffset != j)) && (j >= Editor.this.mTextView.getSelectionStart()) && (j <= Editor.this.mTextView.getSelectionEnd()))
              {
                Editor.this.startDragAndDrop();
                return;
              }
              if (this.mStartOffset != j)
              {
                Editor.this.stopTextActionMode();
                enterDrag(1);
                Editor.this.mDiscardNextActionUp = true;
                this.mHaventMovedEnoughToStartDrag = false;
              }
            }
            localObject = this.mStartHandle;
            if ((localObject != null) && (((Editor.SelectionHandleView)localObject).isShowing())) {
              return;
            }
            updateSelection(paramMotionEvent);
            if (!Editor.this.mTextView.hasSelection()) {
              return;
            }
            localObject = this.mEndHandle;
            if (localObject == null) {
              return;
            }
            ((Editor.SelectionHandleView)localObject).updateMagnifier(paramMotionEvent);
            return;
          }
        }
        else
        {
          this.mPreviousTapUpTime = SystemClock.uptimeMillis();
          localObject = this.mEndHandle;
          if (localObject != null) {
            ((Editor.SelectionHandleView)localObject).dismissMagnifier();
          }
          if (!isDragAcceleratorActive()) {
            return;
          }
          updateSelection(paramMotionEvent);
          requestDisallowInterceptTouchEvent(false);
          resetDragAcceleratorState();
          if ((Editor.this.mTextView.hasSelection()) || (Editor.this.mTapState == 2)) {
            Editor.this.startSelectionActionModeAsync(this.mHaventMovedEnoughToStartDrag);
          }
        }
        requestDisallowInterceptTouchEvent(false);
      }
      else
      {
        this.mInSwipeSelectionMode = false;
        if (Editor.this.extractedTextModeWillBeStarted())
        {
          hide();
        }
        else
        {
          i = Editor.this.mTextView.getOffsetForPosition(f1, f2);
          this.mMaxTouchOffset = i;
          this.mMinTouchOffset = i;
          if ((this.mGestureStayedInTapRegion) && ((Editor.this.mTapState == 2) || (Editor.this.mTapState == 3)))
          {
            f4 = f1 - this.mDownPositionX;
            f3 = f2 - this.mDownPositionY;
            i = ViewConfiguration.get(Editor.this.mTextView.getContext()).getScaledDoubleTapSlop();
            if (f4 * f4 + f3 * f3 < i * i) {
              j = 1;
            }
            if ((j != 0) && ((bool1) || (Editor.this.isPositionOnText(f1, f2))))
            {
              if (Editor.this.mTapState == 2) {
                Editor.this.selectCurrentWordAndStartDrag();
              } else if (Editor.this.mTapState == 3) {
                selectCurrentParagraphAndStartDrag();
              }
              this.mSwipeSelectionStart = Editor.this.mTextView.getOffsetForPosition(f1, f2);
              this.mInSwipeSelectionMode = true;
              this.mDoubleTabed = true;
              Editor.this.mDiscardNextActionUp = true;
            }
          }
          this.mDownPositionX = f1;
          this.mDownPositionY = f2;
          this.mGestureStayedInTapRegion = true;
          this.mHaventMovedEnoughToStartDrag = true;
        }
      }
    }
    
    public void onTouchModeChanged(boolean paramBoolean)
    {
      if (!paramBoolean) {
        hide();
      }
    }
    
    public void resetTouchOffsets()
    {
      this.mMaxTouchOffset = -1;
      this.mMinTouchOffset = -1;
      resetDragAcceleratorState();
    }
    
    public void setMaxTouchOffset(int paramInt)
    {
      this.mMaxTouchOffset = paramInt;
    }
    
    public void setMinTouchOffset(int paramInt)
    {
      this.mMinTouchOffset = paramInt;
    }
    
    public void setTranslationCache(float[] paramArrayOfFloat)
    {
      this.mTranslationCache = paramArrayOfFloat;
    }
    
    public void show()
    {
      if (Editor.this.mTextView.isInBatchEditMode()) {
        return;
      }
      initDrawables();
      initHandles();
      this.mTextSelectionModeEnable = true;
    }
  }
  
  private class SelectionPopupWindow
    extends Editor.ActionPopupWindow
  {
    private LayoutAnimationController mLayoutAnimationController;
    
    public SelectionPopupWindow(Editor.HandleView paramHandleView)
    {
      super(paramHandleView);
    }
    
    protected void createAnimations()
    {
      this.mAnimationFadeIn = new AnimatorSet();
      ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(this.mContentView, View.SCALE_X, new float[] { 0.5F, 1.0F });
      this.mAnimationFadeIn.setInterpolator(new CubicEaseOutInterpolator());
      this.mAnimationFadeIn.setDuration(200L);
      this.mAnimationFadeIn.playTogether(new Animator[] { localObjectAnimator });
      this.mLayoutAnimationController = new LayoutAnimationController(AnimationUtils.loadAnimation(Editor.this.mTextView.getContext(), 285278244));
      this.mLayoutAnimationController.setInterpolator(new CubicEaseOutInterpolator());
      this.mLayoutAnimationController.setDelay(0.05F);
    }
    
    public void fadeIn(int paramInt1, int paramInt2)
    {
      this.mContentView.setPivotX(this.mContentView.getMeasuredWidth() / 2);
      this.mContentView.setPivotY(this.mContentView.getMeasuredHeight() / 2);
      this.mAnimationFadeIn.start();
      this.mMainPanel.setLayoutAnimation(this.mLayoutAnimationController);
    }
  }
  
  private class SelectionStartHandleView
    extends Editor.SelectionHandleView
  {
    public SelectionStartHandleView(Drawable paramDrawable1, Drawable paramDrawable2)
    {
      super(paramDrawable1, paramDrawable2);
    }
    
    private void positionAndAdjustForCrossingHandles(int paramInt)
    {
      int i = Editor.this.mTextView.getSelectionEnd();
      int j = paramInt;
      if (paramInt >= i) {
        j = Math.max(0, i - 1);
      }
      positionAtCursorOffset(j, false);
    }
    
    public int getCurrentCursorOffset()
    {
      return Editor.this.mTextView.getSelectionStart();
    }
    
    protected int getHorizontalGravity(boolean paramBoolean)
    {
      int i;
      if (paramBoolean) {
        i = 3;
      } else {
        i = 5;
      }
      return i;
    }
    
    protected int getHotspotX(Drawable paramDrawable, boolean paramBoolean)
    {
      if (paramBoolean) {
        return paramDrawable.getIntrinsicWidth() / 4;
      }
      return paramDrawable.getIntrinsicWidth() * 3 / 4;
    }
    
    public void updatePosition(float paramFloat1, float paramFloat2)
    {
      Layout localLayout = Editor.this.mTextView.getLayout();
      if (localLayout == null)
      {
        positionAndAdjustForCrossingHandles(Editor.this.mTextView.getOffsetForPosition(paramFloat1, paramFloat2));
        return;
      }
      if (this.mPreviousLineTouched == -1) {
        this.mPreviousLineTouched = Editor.this.mTextView.getLineAtCoordinate(paramFloat2);
      }
      int i = Editor.this.mTextView.getSelectionEnd();
      int j = Editor.this.getCurrentLineAdjustedForSlop(localLayout, this.mPreviousLineTouched, paramFloat2);
      int k = Editor.this.mTextView.getOffsetAtCoordinate(j, paramFloat1);
      int m = k;
      if (k >= i)
      {
        j = localLayout.getLineForOffset(i);
        m = Editor.this.mTextView.getOffsetAtCoordinate(j, paramFloat1);
      }
      this.mPreviousLineTouched = j;
      positionAndAdjustForCrossingHandles(m);
    }
    
    public void updateSelection(int paramInt)
    {
      int i = paramInt;
      if (paramInt < 0) {
        i = 0;
      }
      Selection.setSelection((Spannable)Editor.this.mTextView.getText(), i, Editor.this.mTextView.getSelectionEnd());
      updateDrawable(false);
    }
  }
  
  class SpanController
    implements SpanWatcher
  {
    private static final int DISPLAY_TIMEOUT_MS = 3000;
    private Runnable mHidePopup;
    private Editor.EasyEditPopupWindow mPopupWindow;
    
    SpanController() {}
    
    private boolean isNonIntermediateSelectionSpan(Spannable paramSpannable, Object paramObject)
    {
      boolean bool;
      if (((Selection.SELECTION_START == paramObject) || (Selection.SELECTION_END == paramObject)) && ((paramSpannable.getSpanFlags(paramObject) & 0x200) == 0)) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    private void sendEasySpanNotification(int paramInt, EasyEditSpan paramEasyEditSpan)
    {
      try
      {
        paramEasyEditSpan = paramEasyEditSpan.getPendingIntent();
        if (paramEasyEditSpan != null)
        {
          Intent localIntent = new android/content/Intent;
          localIntent.<init>();
          localIntent.putExtra("android.text.style.EXTRA_TEXT_CHANGED_TYPE", paramInt);
          paramEasyEditSpan.send(Editor.this.mTextView.getContext(), 0, localIntent);
        }
      }
      catch (PendingIntent.CanceledException paramEasyEditSpan)
      {
        Log.w("Editor", "PendingIntent for notification cannot be sent", paramEasyEditSpan);
      }
    }
    
    public void hide()
    {
      Editor.EasyEditPopupWindow localEasyEditPopupWindow = this.mPopupWindow;
      if (localEasyEditPopupWindow != null)
      {
        localEasyEditPopupWindow.hide();
        Editor.this.mTextView.removeCallbacks(this.mHidePopup);
      }
    }
    
    public void onSpanAdded(Spannable paramSpannable, Object paramObject, int paramInt1, int paramInt2)
    {
      if (isNonIntermediateSelectionSpan(paramSpannable, paramObject))
      {
        Editor.this.sendUpdateSelection();
      }
      else if ((paramObject instanceof EasyEditSpan))
      {
        if (this.mPopupWindow == null)
        {
          this.mPopupWindow = new Editor.EasyEditPopupWindow(Editor.this, null);
          this.mHidePopup = new Runnable()
          {
            public void run()
            {
              Editor.SpanController.this.hide();
            }
          };
        }
        if (this.mPopupWindow.mEasyEditSpan != null) {
          this.mPopupWindow.mEasyEditSpan.setDeleteEnabled(false);
        }
        this.mPopupWindow.setEasyEditSpan((EasyEditSpan)paramObject);
        this.mPopupWindow.setOnDeleteListener(new Editor.EasyEditDeleteListener()
        {
          public void onDeleteClick(EasyEditSpan paramAnonymousEasyEditSpan)
          {
            Editable localEditable = (Editable)Editor.this.mTextView.getText();
            int i = localEditable.getSpanStart(paramAnonymousEasyEditSpan);
            int j = localEditable.getSpanEnd(paramAnonymousEasyEditSpan);
            if ((i >= 0) && (j >= 0))
            {
              Editor.SpanController.this.sendEasySpanNotification(1, paramAnonymousEasyEditSpan);
              Editor.this.mTextView.deleteText_internal(i, j);
            }
            localEditable.removeSpan(paramAnonymousEasyEditSpan);
          }
        });
        if (Editor.this.mTextView.getWindowVisibility() != 0) {
          return;
        }
        if (Editor.this.mTextView.getLayout() == null) {
          return;
        }
        if (Editor.this.extractedTextModeWillBeStarted()) {
          return;
        }
        this.mPopupWindow.show();
        Editor.this.mTextView.removeCallbacks(this.mHidePopup);
        Editor.this.mTextView.postDelayed(this.mHidePopup, 3000L);
      }
    }
    
    public void onSpanChanged(Spannable paramSpannable, Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (isNonIntermediateSelectionSpan(paramSpannable, paramObject))
      {
        Editor.this.sendUpdateSelection();
      }
      else if ((this.mPopupWindow != null) && ((paramObject instanceof EasyEditSpan)))
      {
        paramObject = (EasyEditSpan)paramObject;
        sendEasySpanNotification(2, (EasyEditSpan)paramObject);
        paramSpannable.removeSpan(paramObject);
      }
    }
    
    public void onSpanRemoved(Spannable paramSpannable, Object paramObject, int paramInt1, int paramInt2)
    {
      if (isNonIntermediateSelectionSpan(paramSpannable, paramObject))
      {
        Editor.this.sendUpdateSelection();
      }
      else
      {
        paramSpannable = this.mPopupWindow;
        if ((paramSpannable != null) && (paramObject == paramSpannable.mEasyEditSpan)) {
          hide();
        }
      }
    }
  }
  
  private class SuggestionInfo
  {
    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(Editor.this.mTextView.getContext(), 16974104);
    int suggestionEnd;
    int suggestionIndex;
    SuggestionSpan suggestionSpan;
    int suggestionStart;
    SpannableStringBuilder text = new SpannableStringBuilder();
    
    private SuggestionInfo() {}
  }
  
  private final class SuggestionsPopupWindow
    extends Editor.PinnedPopupWindow
    implements AdapterView.OnItemClickListener
  {
    private static final int ADD_TO_DICTIONARY = -1;
    private static final int DELETE_TEXT = -2;
    private static final int MAX_NUMBER_SUGGESTIONS = 5;
    private boolean mCursorWasVisibleBeforeSuggestions = Editor.this.mCursorVisible;
    private boolean mIsShowingUp = false;
    private int mNumberOfSuggestions;
    private final HashMap<SuggestionSpan, Integer> mSpansLengths = new HashMap();
    private Editor.SuggestionInfo[] mSuggestionInfos;
    private final Comparator<SuggestionSpan> mSuggestionSpanComparator = new SuggestionSpanComparator(null);
    private SuggestionAdapter mSuggestionsAdapter;
    
    public SuggestionsPopupWindow()
    {
      super();
    }
    
    private SuggestionSpan[] getSuggestionSpans()
    {
      int i = Editor.this.mTextView.getSelectionStart();
      Spannable localSpannable = (Spannable)Editor.this.mTextView.getText();
      SuggestionSpan[] arrayOfSuggestionSpan = (SuggestionSpan[])localSpannable.getSpans(i, i, SuggestionSpan.class);
      this.mSpansLengths.clear();
      int j = arrayOfSuggestionSpan.length;
      for (i = 0; i < j; i++)
      {
        SuggestionSpan localSuggestionSpan = arrayOfSuggestionSpan[i];
        int k = localSpannable.getSpanStart(localSuggestionSpan);
        int m = localSpannable.getSpanEnd(localSuggestionSpan);
        this.mSpansLengths.put(localSuggestionSpan, Integer.valueOf(m - k));
      }
      Arrays.sort(arrayOfSuggestionSpan, this.mSuggestionSpanComparator);
      return arrayOfSuggestionSpan;
    }
    
    private void highlightTextDifferences(Editor.SuggestionInfo paramSuggestionInfo, int paramInt1, int paramInt2)
    {
      Object localObject = (Spannable)Editor.this.mTextView.getText();
      int i = ((Spannable)localObject).getSpanStart(paramSuggestionInfo.suggestionSpan);
      int j = ((Spannable)localObject).getSpanEnd(paramSuggestionInfo.suggestionSpan);
      paramSuggestionInfo.suggestionStart = (i - paramInt1);
      paramSuggestionInfo.suggestionEnd = (paramSuggestionInfo.suggestionStart + paramSuggestionInfo.text.length());
      paramSuggestionInfo.text.setSpan(paramSuggestionInfo.highlightSpan, 0, paramSuggestionInfo.text.length(), 33);
      localObject = localObject.toString();
      paramSuggestionInfo.text.insert(0, ((String)localObject).substring(paramInt1, i));
      paramSuggestionInfo.text.append(((String)localObject).substring(j, paramInt2));
    }
    
    private boolean updateSuggestions()
    {
      Spannable localSpannable = (Spannable)Editor.this.mTextView.getText();
      Object localObject1 = getSuggestionSpans();
      int i = localObject1.length;
      if (i == 0) {
        return false;
      }
      this.mNumberOfSuggestions = 0;
      int j = Editor.this.mTextView.getText().length();
      int k = 0;
      Object localObject2 = null;
      int m = 0;
      int i5;
      for (int n = 0; n < i; n = i5)
      {
        Object localObject3 = localObject1[n];
        int i1 = localSpannable.getSpanStart(localObject3);
        int i2 = localSpannable.getSpanEnd(localObject3);
        int i3 = Math.min(i1, j);
        j = Math.max(i2, k);
        if ((((SuggestionSpan)localObject3).getFlags() & 0x2) != 0) {
          localObject2 = localObject3;
        }
        if (n == 0) {
          m = ((SuggestionSpan)localObject3).getUnderlineColor();
        }
        String[] arrayOfString = ((SuggestionSpan)localObject3).getSuggestions();
        int i4 = arrayOfString.length;
        i5 = 0;
        k = i3;
        while (i5 < i4)
        {
          String str = arrayOfString[i5];
          int i6 = 0;
          Object localObject4;
          for (i3 = 0; i3 < this.mNumberOfSuggestions; i3++) {
            if (this.mSuggestionInfos[i3].text.toString().equals(str))
            {
              localObject4 = this.mSuggestionInfos[i3].suggestionSpan;
              int i7 = localSpannable.getSpanStart(localObject4);
              int i8 = localSpannable.getSpanEnd(localObject4);
              if ((i1 == i7) && (i2 == i8))
              {
                i3 = 1;
                break label269;
              }
            }
          }
          i3 = i6;
          label269:
          if (i3 == 0)
          {
            localObject4 = this.mSuggestionInfos[this.mNumberOfSuggestions];
            ((Editor.SuggestionInfo)localObject4).suggestionSpan = ((SuggestionSpan)localObject3);
            ((Editor.SuggestionInfo)localObject4).suggestionIndex = i5;
            ((Editor.SuggestionInfo)localObject4).text.replace(0, ((Editor.SuggestionInfo)localObject4).text.length(), str);
            this.mNumberOfSuggestions += 1;
            if (this.mNumberOfSuggestions == 5)
            {
              n = i;
              break;
            }
          }
          i5++;
        }
        i5 = n + 1;
        n = j;
        j = k;
        k = n;
      }
      for (n = 0; n < this.mNumberOfSuggestions; n++) {
        highlightTextDifferences(this.mSuggestionInfos[n], j, k);
      }
      if (localObject2 != null)
      {
        i5 = localSpannable.getSpanStart(localObject2);
        n = localSpannable.getSpanEnd(localObject2);
        if ((i5 >= 0) && (n > i5))
        {
          localObject1 = this.mSuggestionInfos[this.mNumberOfSuggestions];
          ((Editor.SuggestionInfo)localObject1).suggestionSpan = ((SuggestionSpan)localObject2);
          ((Editor.SuggestionInfo)localObject1).suggestionIndex = -1;
          ((Editor.SuggestionInfo)localObject1).text.replace(0, ((Editor.SuggestionInfo)localObject1).text.length(), Editor.this.mTextView.getContext().getString(17039475));
          ((Editor.SuggestionInfo)localObject1).text.setSpan(((Editor.SuggestionInfo)localObject1).highlightSpan, 0, 0, 33);
          this.mNumberOfSuggestions += 1;
        }
      }
      localObject2 = this.mSuggestionInfos[this.mNumberOfSuggestions];
      ((Editor.SuggestionInfo)localObject2).suggestionSpan = null;
      ((Editor.SuggestionInfo)localObject2).suggestionIndex = -2;
      ((Editor.SuggestionInfo)localObject2).text.replace(0, ((Editor.SuggestionInfo)localObject2).text.length(), Editor.this.mTextView.getContext().getString(17039885));
      ((Editor.SuggestionInfo)localObject2).text.setSpan(((Editor.SuggestionInfo)localObject2).highlightSpan, 0, 0, 33);
      this.mNumberOfSuggestions += 1;
      if (Editor.this.mSuggestionRangeSpan == null) {
        Editor.this.mSuggestionRangeSpan = new SuggestionRangeSpan();
      }
      if (m == 0)
      {
        Editor.this.mSuggestionRangeSpan.setBackgroundColor(Editor.this.mTextView.mHighlightColor);
      }
      else
      {
        n = (int)(Color.alpha(m) * 0.4F);
        Editor.this.mSuggestionRangeSpan.setBackgroundColor((0xFFFFFF & m) + (n << 24));
      }
      localSpannable.setSpan(Editor.this.mSuggestionRangeSpan, j, k, 33);
      this.mSuggestionsAdapter.notifyDataSetChanged();
      return true;
    }
    
    protected int clipVertically(int paramInt)
    {
      int i = this.mContentView.getMeasuredHeight();
      return Math.min(paramInt, Editor.this.mTextView.getResources().getDisplayMetrics().heightPixels - i);
    }
    
    protected void createPopupWindow()
    {
      this.mPopupWindow = new CustomPopupWindow(Editor.this.mTextView.getContext(), 16843635);
      this.mPopupWindow.setInputMethodMode(2);
      this.mPopupWindow.setFocusable(true);
      this.mPopupWindow.setClippingEnabled(false);
    }
    
    protected int getTextOffset()
    {
      return Editor.this.mTextView.getSelectionStart();
    }
    
    protected int getVerticalLocalPosition(int paramInt)
    {
      return Editor.this.mTextView.getLayout().getLineBottom(paramInt);
    }
    
    public void hide()
    {
      super.hide();
    }
    
    protected void initContentView()
    {
      Object localObject = new ListView(Editor.this.mTextView.getContext());
      this.mSuggestionsAdapter = new SuggestionAdapter(null);
      ((ListView)localObject).setAdapter(this.mSuggestionsAdapter);
      ((ListView)localObject).setOnItemClickListener(this);
      this.mContentView = ((ViewGroup)localObject);
      this.mSuggestionInfos = new Editor.SuggestionInfo[7];
      for (int i = 0;; i++)
      {
        localObject = this.mSuggestionInfos;
        if (i >= localObject.length) {
          break;
        }
        localObject[i] = new Editor.SuggestionInfo(Editor.this, null);
      }
    }
    
    public boolean isShowingUp()
    {
      return this.mIsShowingUp;
    }
    
    protected void measureContent()
    {
      Object localObject = Editor.this.mTextView.getResources().getDisplayMetrics();
      int i = View.MeasureSpec.makeMeasureSpec(((DisplayMetrics)localObject).widthPixels, Integer.MIN_VALUE);
      int j = View.MeasureSpec.makeMeasureSpec(((DisplayMetrics)localObject).heightPixels, Integer.MIN_VALUE);
      int k = 0;
      localObject = null;
      for (int m = 0; m < this.mNumberOfSuggestions; m++)
      {
        localObject = this.mSuggestionsAdapter.getView(m, (View)localObject, this.mContentView);
        ((View)localObject).getLayoutParams().width = -2;
        ((View)localObject).measure(i, j);
        k = Math.max(k, ((View)localObject).getMeasuredWidth());
      }
      this.mContentView.measure(View.MeasureSpec.makeMeasureSpec(k, 1073741824), j);
      localObject = this.mPopupWindow.getBackground();
      m = k;
      if (localObject != null)
      {
        if (Editor.this.mTempRect == null) {
          Editor.access$2402(Editor.this, new Rect());
        }
        ((Drawable)localObject).getPadding(Editor.this.mTempRect);
        m = k + (Editor.this.mTempRect.left + Editor.this.mTempRect.right);
      }
      this.mPopupWindow.setWidth(m);
    }
    
    public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    {
      Object localObject1 = (Editable)Editor.this.mTextView.getText();
      paramView = this.mSuggestionInfos[paramInt];
      int i;
      if (paramView.suggestionIndex == -2)
      {
        i = ((Editable)localObject1).getSpanStart(Editor.this.mSuggestionRangeSpan);
        j = ((Editable)localObject1).getSpanEnd(Editor.this.mSuggestionRangeSpan);
        if ((i >= 0) && (j > i))
        {
          paramInt = j;
          if (j < ((Editable)localObject1).length())
          {
            paramInt = j;
            if (Character.isSpaceChar(((Editable)localObject1).charAt(j))) {
              if (i != 0)
              {
                paramInt = j;
                if (!Character.isSpaceChar(((Editable)localObject1).charAt(i - 1))) {}
              }
              else
              {
                paramInt = j + 1;
              }
            }
          }
          Editor.this.mTextView.deleteText_internal(i, paramInt);
        }
        hide();
        return;
      }
      int j = ((Editable)localObject1).getSpanStart(paramView.suggestionSpan);
      int k = ((Editable)localObject1).getSpanEnd(paramView.suggestionSpan);
      if ((j >= 0) && (k > j))
      {
        paramAdapterView = localObject1.toString().substring(j, k);
        Object localObject2;
        if (paramView.suggestionIndex == -1)
        {
          localObject2 = new Intent("com.android.settings.USER_DICTIONARY_INSERT");
          ((Intent)localObject2).putExtra("word", paramAdapterView);
          ((Intent)localObject2).putExtra("locale", Editor.this.mTextView.getTextServicesLocale().toString());
          ((Intent)localObject2).setFlags(((Intent)localObject2).getFlags() | 0x10000000);
          Editor.this.mTextView.getContext().startActivity((Intent)localObject2);
          ((Editable)localObject1).removeSpan(paramView.suggestionSpan);
          Selection.setSelection((Spannable)localObject1, k);
          Editor.this.updateSpellCheckSpans(j, k, false);
        }
        else
        {
          SuggestionSpan[] arrayOfSuggestionSpan = (SuggestionSpan[])((Editable)localObject1).getSpans(j, k, SuggestionSpan.class);
          int m = arrayOfSuggestionSpan.length;
          int[] arrayOfInt1 = new int[m];
          localObject2 = new int[m];
          int[] arrayOfInt2 = new int[m];
          for (paramInt = 0; paramInt < m; paramInt++)
          {
            localObject3 = arrayOfSuggestionSpan[paramInt];
            arrayOfInt1[paramInt] = ((Editable)localObject1).getSpanStart(localObject3);
            localObject2[paramInt] = ((Editable)localObject1).getSpanEnd(localObject3);
            arrayOfInt2[paramInt] = ((Editable)localObject1).getSpanFlags(localObject3);
            i = ((SuggestionSpan)localObject3).getFlags();
            if ((i & 0x2) > 0) {
              ((SuggestionSpan)localObject3).setFlags(i & 0xFFFFFFFD & 0xFFFFFFFE);
            }
          }
          paramInt = paramView.suggestionStart;
          i = paramView.suggestionEnd;
          Object localObject3 = paramView.text.subSequence(paramInt, i).toString();
          Editor.this.mTextView.replaceText_internal(j, k, (CharSequence)localObject3);
          paramView.suggestionSpan.notifySelection(Editor.this.mTextView.getContext(), paramAdapterView, paramView.suggestionIndex);
          localObject1 = paramView.suggestionSpan.getSuggestions();
          localObject1[paramView.suggestionIndex] = paramAdapterView;
          int n = ((String)localObject3).length() - (k - j);
          for (i = 0; i < m; i++) {
            if ((arrayOfInt1[i] <= j) && (localObject2[i] >= k)) {
              Editor.this.mTextView.setSpan_internal(arrayOfSuggestionSpan[i], arrayOfInt1[i], localObject2[i] + n, arrayOfInt2[i]);
            }
          }
          paramInt = k + n;
          Editor.this.mTextView.setCursorPosition_internal(paramInt, paramInt);
        }
        hide();
        return;
      }
      hide();
    }
    
    public void onParentLostFocus()
    {
      this.mIsShowingUp = false;
    }
    
    public void show()
    {
      if (!(Editor.this.mTextView.getText() instanceof Editable)) {
        return;
      }
      if (updateSuggestions())
      {
        this.mCursorWasVisibleBeforeSuggestions = Editor.this.mCursorVisible;
        Editor.this.mTextView.setCursorVisible(false);
        this.mIsShowingUp = true;
        super.show();
      }
    }
    
    private class CustomPopupWindow
      extends Editor.AnimatePopupWindow
    {
      public CustomPopupWindow(Context paramContext, int paramInt)
      {
        super(paramContext, null, paramInt);
      }
      
      public void dismiss()
      {
        super.dismiss();
        Editor.this.getPositionListener().removeSubscriber(Editor.SuggestionsPopupWindow.this);
        ((Spannable)Editor.this.mTextView.getText()).removeSpan(Editor.this.mSuggestionRangeSpan);
        Editor.this.mTextView.setCursorVisible(Editor.SuggestionsPopupWindow.this.mCursorWasVisibleBeforeSuggestions);
        if (Editor.this.hasInsertionController()) {
          Editor.this.getInsertionController().show();
        }
      }
    }
    
    private class SuggestionAdapter
      extends BaseAdapter
    {
      private LayoutInflater mInflater = (LayoutInflater)Editor.this.mTextView.getContext().getSystemService("layout_inflater");
      
      private SuggestionAdapter() {}
      
      public int getCount()
      {
        return Editor.SuggestionsPopupWindow.this.mNumberOfSuggestions;
      }
      
      public Object getItem(int paramInt)
      {
        return Editor.SuggestionsPopupWindow.this.mSuggestionInfos[paramInt];
      }
      
      public long getItemId(int paramInt)
      {
        return paramInt;
      }
      
      public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
      {
        TextView localTextView = (TextView)paramView;
        paramView = localTextView;
        if (localTextView == null) {
          paramView = (TextView)this.mInflater.inflate(Editor.this.mTextView.mTextEditSuggestionItemLayout, paramViewGroup, false);
        }
        paramViewGroup = Editor.SuggestionsPopupWindow.this.mSuggestionInfos[paramInt];
        paramView.setText(paramViewGroup.text);
        if ((paramViewGroup.suggestionIndex != -1) && (paramViewGroup.suggestionIndex != -2)) {
          paramView.setBackgroundColor(-1);
        } else {
          paramView.setBackgroundColor(0);
        }
        return paramView;
      }
    }
    
    private class SuggestionSpanComparator
      implements Comparator<SuggestionSpan>
    {
      private SuggestionSpanComparator() {}
      
      public int compare(SuggestionSpan paramSuggestionSpan1, SuggestionSpan paramSuggestionSpan2)
      {
        int i = paramSuggestionSpan1.getFlags();
        int j = paramSuggestionSpan2.getFlags();
        if (i != j)
        {
          int k = 0;
          int m;
          if ((i & 0x1) != 0) {
            m = 1;
          } else {
            m = 0;
          }
          int n;
          if ((j & 0x1) != 0) {
            n = 1;
          } else {
            n = 0;
          }
          if ((i & 0x2) != 0) {
            i = 1;
          } else {
            i = 0;
          }
          if ((j & 0x2) != 0) {
            k = 1;
          }
          if ((m != 0) && (i == 0)) {
            return -1;
          }
          if ((n != 0) && (k == 0)) {
            return 1;
          }
          if (i != 0) {
            return -1;
          }
          if (k != 0) {
            return 1;
          }
        }
        return ((Integer)Editor.SuggestionsPopupWindow.this.mSpansLengths.get(paramSuggestionSpan1)).intValue() - ((Integer)Editor.SuggestionsPopupWindow.this.mSpansLengths.get(paramSuggestionSpan2)).intValue();
      }
    }
  }
  
  static @interface TextActionMode
  {
    public static final int INSERTION = 1;
    public static final int SELECTION = 0;
    public static final int TEXT_LINK = 2;
  }
  
  private static class TextRenderNode
  {
    boolean isDirty = true;
    RenderNode renderNode;
    
    public TextRenderNode(String paramString)
    {
      this.renderNode = RenderNode.create(paramString, null);
    }
    
    boolean needsRecord()
    {
      boolean bool;
      if ((!this.isDirty) && (this.renderNode.hasDisplayList())) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
  }
  
  private static abstract interface TextViewPositionListener
  {
    public abstract void updatePosition(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2);
  }
  
  public static class UndoInputFilter
    implements InputFilter
  {
    private static final int MERGE_EDIT_MODE_FORCE_MERGE = 0;
    private static final int MERGE_EDIT_MODE_NEVER_MERGE = 1;
    private static final int MERGE_EDIT_MODE_NORMAL = 2;
    private final Editor mEditor;
    private boolean mExpanding;
    private boolean mHasComposition;
    private boolean mIsUserEdit;
    private boolean mPreviousOperationWasInSameBatchEdit;
    
    public UndoInputFilter(Editor paramEditor)
    {
      this.mEditor = paramEditor;
    }
    
    private boolean canUndoEdit(CharSequence paramCharSequence, int paramInt1, int paramInt2, Spanned paramSpanned, int paramInt3, int paramInt4)
    {
      if (!this.mEditor.mAllowUndo) {
        return false;
      }
      if (this.mEditor.mUndoManager.isInUndo()) {
        return false;
      }
      if ((Editor.isValidRange(paramCharSequence, paramInt1, paramInt2)) && (Editor.isValidRange(paramSpanned, paramInt3, paramInt4))) {
        return (paramInt1 != paramInt2) || (paramInt3 != paramInt4);
      }
      return false;
    }
    
    private Editor.EditOperation getLastEdit()
    {
      return (Editor.EditOperation)this.mEditor.mUndoManager.getLastOperation(Editor.EditOperation.class, this.mEditor.mUndoOwner, 1);
    }
    
    private void handleEdit(CharSequence paramCharSequence, int paramInt1, int paramInt2, Spanned paramSpanned, int paramInt3, int paramInt4, boolean paramBoolean)
    {
      int i;
      if ((!isInTextWatcher()) && (!this.mPreviousOperationWasInSameBatchEdit))
      {
        if (paramBoolean) {
          i = 1;
        } else {
          i = 2;
        }
      }
      else {
        i = 0;
      }
      paramCharSequence = TextUtils.substring(paramCharSequence, paramInt1, paramInt2);
      paramSpanned = TextUtils.substring(paramSpanned, paramInt3, paramInt4);
      paramCharSequence = new Editor.EditOperation(this.mEditor, paramSpanned, paramInt3, paramCharSequence, this.mHasComposition);
      if ((this.mHasComposition) && (TextUtils.equals(paramCharSequence.mNewText, paramCharSequence.mOldText))) {
        return;
      }
      recordEdit(paramCharSequence, i);
    }
    
    private static boolean isComposition(CharSequence paramCharSequence)
    {
      boolean bool1 = paramCharSequence instanceof Spannable;
      boolean bool2 = false;
      if (!bool1) {
        return false;
      }
      paramCharSequence = (Spannable)paramCharSequence;
      if (EditableInputConnection.getComposingSpanStart(paramCharSequence) < EditableInputConnection.getComposingSpanEnd(paramCharSequence)) {
        bool2 = true;
      }
      return bool2;
    }
    
    private boolean isInTextWatcher()
    {
      CharSequence localCharSequence = this.mEditor.mTextView.getText();
      boolean bool;
      if (((localCharSequence instanceof SpannableStringBuilder)) && (((SpannableStringBuilder)localCharSequence).getTextWatcherDepth() > 0)) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    private void recordEdit(Editor.EditOperation paramEditOperation, int paramInt)
    {
      UndoManager localUndoManager = this.mEditor.mUndoManager;
      localUndoManager.beginUpdate("Edit text");
      Editor.EditOperation localEditOperation = getLastEdit();
      if (localEditOperation == null)
      {
        localUndoManager.addOperation(paramEditOperation, 0);
      }
      else if (paramInt == 0)
      {
        localEditOperation.forceMergeWith(paramEditOperation);
      }
      else if (!this.mIsUserEdit)
      {
        localUndoManager.commitState(this.mEditor.mUndoOwner);
        localUndoManager.addOperation(paramEditOperation, 0);
      }
      else if ((paramInt != 2) || (!localEditOperation.mergeWith(paramEditOperation)))
      {
        localUndoManager.commitState(this.mEditor.mUndoOwner);
        localUndoManager.addOperation(paramEditOperation, 0);
      }
      this.mPreviousOperationWasInSameBatchEdit = this.mIsUserEdit;
      localUndoManager.endUpdate();
    }
    
    public void beginBatchEdit()
    {
      this.mIsUserEdit = true;
    }
    
    public void endBatchEdit()
    {
      this.mIsUserEdit = false;
      this.mPreviousOperationWasInSameBatchEdit = false;
    }
    
    public CharSequence filter(CharSequence paramCharSequence, int paramInt1, int paramInt2, Spanned paramSpanned, int paramInt3, int paramInt4)
    {
      if (!canUndoEdit(paramCharSequence, paramInt1, paramInt2, paramSpanned, paramInt3, paramInt4)) {
        return null;
      }
      boolean bool1 = this.mHasComposition;
      this.mHasComposition = isComposition(paramCharSequence);
      boolean bool2 = this.mExpanding;
      if (paramInt2 - paramInt1 != paramInt4 - paramInt3)
      {
        if (paramInt2 - paramInt1 > paramInt4 - paramInt3) {
          bool3 = true;
        } else {
          bool3 = false;
        }
        this.mExpanding = bool3;
        if ((bool1) && (this.mExpanding != bool2))
        {
          bool3 = true;
          break label98;
        }
      }
      boolean bool3 = false;
      label98:
      handleEdit(paramCharSequence, paramInt1, paramInt2, paramSpanned, paramInt3, paramInt4, bool3);
      return null;
    }
    
    void freezeLastEdit()
    {
      this.mEditor.mUndoManager.beginUpdate("Edit text");
      Editor.EditOperation localEditOperation = getLastEdit();
      if (localEditOperation != null) {
        Editor.EditOperation.access$8402(localEditOperation, true);
      }
      this.mEditor.mUndoManager.endUpdate();
    }
    
    public void restoreInstanceState(Parcel paramParcel)
    {
      int i = paramParcel.readInt();
      boolean bool1 = true;
      boolean bool2;
      if (i != 0) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      this.mIsUserEdit = bool2;
      if (paramParcel.readInt() != 0) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      this.mHasComposition = bool2;
      if (paramParcel.readInt() != 0) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      this.mExpanding = bool2;
      if (paramParcel.readInt() != 0) {
        bool2 = bool1;
      } else {
        bool2 = false;
      }
      this.mPreviousOperationWasInSameBatchEdit = bool2;
    }
    
    public void saveInstanceState(Parcel paramParcel)
    {
      paramParcel.writeInt(this.mIsUserEdit);
      paramParcel.writeInt(this.mHasComposition);
      paramParcel.writeInt(this.mExpanding);
      paramParcel.writeInt(this.mPreviousOperationWasInSameBatchEdit);
    }
    
    @Retention(RetentionPolicy.SOURCE)
    private static @interface MergeMode {}
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/Editor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */