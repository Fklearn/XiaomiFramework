package android.widget;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.os.LocaleList;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.textclassifier.SelectionEvent;
import android.view.textclassifier.SelectionSessionLogger;
import android.view.textclassifier.TextClassification;
import android.view.textclassifier.TextClassification.Request;
import android.view.textclassifier.TextClassification.Request.Builder;
import android.view.textclassifier.TextClassificationConstants;
import android.view.textclassifier.TextClassificationManager;
import android.view.textclassifier.TextClassifier;
import android.view.textclassifier.TextSelection;
import android.view.textclassifier.TextSelection.Request;
import android.view.textclassifier.TextSelection.Request.Builder;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.internal.util.Preconditions;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
public final class SelectionActionModeHelper
{
  private static final String LOG_TAG = "SelectActionModeHelper";
  private final Editor mEditor;
  private final SelectionTracker mSelectionTracker;
  private final SmartSelectSprite mSmartSelectSprite;
  private TextClassification mTextClassification;
  private AsyncTask mTextClassificationAsyncTask;
  private final TextClassificationHelper mTextClassificationHelper;
  private final TextView mTextView;
  
  SelectionActionModeHelper(Editor paramEditor)
  {
    this.mEditor = ((Editor)Preconditions.checkNotNull(paramEditor));
    this.mTextView = this.mEditor.getTextView();
    Context localContext = this.mTextView.getContext();
    Object localObject = this.mTextView;
    Objects.requireNonNull(localObject);
    this.mTextClassificationHelper = new TextClassificationHelper(localContext, new _..Lambda.yIdmBO6ZxaY03PGN08RySVVQXuE((TextView)localObject), getText(this.mTextView), 0, 1, this.mTextView.getTextLocales());
    this.mSelectionTracker = new SelectionTracker(this.mTextView);
    if (getTextClassificationSettings().isSmartSelectionAnimationEnabled())
    {
      localObject = this.mTextView.getContext();
      int i = paramEditor.getTextView().mHighlightColor;
      paramEditor = this.mTextView;
      Objects.requireNonNull(paramEditor);
      this.mSmartSelectSprite = new SmartSelectSprite((Context)localObject, i, new _..Lambda.IfzAW5fP9thoftErKAjo9SLZufw(paramEditor));
    }
    else
    {
      this.mSmartSelectSprite = null;
    }
  }
  
  private void cancelAsyncTask()
  {
    AsyncTask localAsyncTask = this.mTextClassificationAsyncTask;
    if (localAsyncTask != null)
    {
      localAsyncTask.cancel(true);
      this.mTextClassificationAsyncTask = null;
    }
    this.mTextClassification = null;
  }
  
  private void cancelSmartSelectAnimation()
  {
    SmartSelectSprite localSmartSelectSprite = this.mSmartSelectSprite;
    if (localSmartSelectSprite != null) {
      localSmartSelectSprite.cancelAnimation();
    }
  }
  
  private List<SmartSelectSprite.RectangleWithTextSelectionLayout> convertSelectionToRectangles(Layout paramLayout, int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = new ArrayList();
    paramLayout.getSelection(paramInt1, paramInt2, new _..Lambda.SelectionActionModeHelper.cMbIRcH_yFkksR3CQmROa0_hmgM(localArrayList));
    localArrayList.sort(Comparator.comparing(_..Lambda.ChL7kntlZCrPaPVdRfaSzGdk1JU.INSTANCE, SmartSelectSprite.RECTANGLE_COMPARATOR));
    return localArrayList;
  }
  
  private static int getActionType(int paramInt)
  {
    if (paramInt != 16908337)
    {
      if (paramInt != 16908341) {
        if (paramInt == 16908353) {}
      }
      switch (paramInt)
      {
      default: 
        return 108;
      case 16908321: 
        return 101;
      case 16908320: 
        return 103;
      case 16908319: 
        return 200;
        return 105;
        return 104;
      }
    }
    return 102;
  }
  
  private static CharSequence getText(TextView paramTextView)
  {
    paramTextView = paramTextView.getText();
    if (paramTextView != null) {
      return paramTextView;
    }
    return "";
  }
  
  private TextClassificationConstants getTextClassificationSettings()
  {
    return TextClassificationManager.getSettings(this.mTextView.getContext());
  }
  
  private void invalidateActionMode(SelectionResult paramSelectionResult)
  {
    cancelSmartSelectAnimation();
    if (paramSelectionResult != null) {
      paramSelectionResult = paramSelectionResult.mClassification;
    } else {
      paramSelectionResult = null;
    }
    this.mTextClassification = paramSelectionResult;
    paramSelectionResult = this.mEditor.getTextActionMode();
    if (paramSelectionResult != null) {
      paramSelectionResult.invalidate();
    }
    this.mSelectionTracker.onSelectionUpdated(this.mTextView.getSelectionStart(), this.mTextView.getSelectionEnd(), this.mTextClassification);
    this.mTextClassificationAsyncTask = null;
  }
  
  @VisibleForTesting
  public static <T> void mergeRectangleIntoList(List<T> paramList, RectF paramRectF, Function<T, RectF> paramFunction, Function<RectF, T> paramFunction1)
  {
    if (paramRectF.isEmpty()) {
      return;
    }
    int i = paramList.size();
    for (int j = 0; j < i; j++)
    {
      RectF localRectF = (RectF)paramFunction.apply(paramList.get(j));
      if (localRectF.contains(paramRectF)) {
        return;
      }
      if (paramRectF.contains(localRectF))
      {
        localRectF.setEmpty();
      }
      else
      {
        float f1 = paramRectF.left;
        float f2 = localRectF.right;
        int k = 0;
        int m;
        if ((f1 != f2) && (paramRectF.right != localRectF.left)) {
          m = 0;
        } else {
          m = 1;
        }
        if ((paramRectF.top == localRectF.top) && (paramRectF.bottom == localRectF.bottom) && ((RectF.intersects(paramRectF, localRectF)) || (m != 0))) {
          m = 1;
        } else {
          m = k;
        }
        if (m != 0)
        {
          paramRectF.union(localRectF);
          localRectF.setEmpty();
        }
      }
    }
    for (j = i - 1; j >= 0; j--) {
      if (((RectF)paramFunction.apply(paramList.get(j))).isEmpty()) {
        paramList.remove(j);
      }
    }
    paramList.add(paramFunction1.apply(paramRectF));
  }
  
  @VisibleForTesting
  public static <T> PointF movePointInsideNearestRectangle(PointF paramPointF, List<T> paramList, Function<T, RectF> paramFunction)
  {
    float f1 = -1.0F;
    float f2 = -1.0F;
    double d1 = Double.MAX_VALUE;
    int i = paramList.size();
    int j = 0;
    while (j < i)
    {
      RectF localRectF = (RectF)paramFunction.apply(paramList.get(j));
      float f3 = localRectF.centerY();
      float f4;
      if (paramPointF.x > localRectF.right) {
        f4 = localRectF.right;
      } else if (paramPointF.x < localRectF.left) {
        f4 = localRectF.left;
      } else {
        f4 = paramPointF.x;
      }
      double d2 = Math.pow(paramPointF.x - f4, 2.0D) + Math.pow(paramPointF.y - f3, 2.0D);
      double d3 = d1;
      if (d2 < d1)
      {
        f2 = f3;
        d3 = d2;
        f1 = f4;
      }
      j++;
      d1 = d3;
    }
    return new PointF(f1, f2);
  }
  
  private void resetTextClassificationHelper()
  {
    resetTextClassificationHelper(-1, -1);
  }
  
  private void resetTextClassificationHelper(int paramInt1, int paramInt2)
  {
    int i;
    if (paramInt1 >= 0)
    {
      i = paramInt1;
      paramInt1 = paramInt2;
      if (paramInt2 >= 0) {}
    }
    else
    {
      i = this.mTextView.getSelectionStart();
      paramInt1 = this.mTextView.getSelectionEnd();
    }
    TextClassificationHelper localTextClassificationHelper = this.mTextClassificationHelper;
    TextView localTextView = this.mTextView;
    Objects.requireNonNull(localTextView);
    localTextClassificationHelper.init(new _..Lambda.yIdmBO6ZxaY03PGN08RySVVQXuE(localTextView), getText(this.mTextView), i, paramInt1, this.mTextView.getTextLocales());
  }
  
  private boolean skipTextClassification()
  {
    boolean bool1 = this.mTextView.usesNoOpTextClassifier();
    int i = this.mTextView.getSelectionEnd();
    int j = this.mTextView.getSelectionStart();
    boolean bool2 = true;
    if (i == j) {
      j = 1;
    } else {
      j = 0;
    }
    if ((!this.mTextView.hasPasswordTransformationMethod()) && (!TextView.isPasswordInputType(this.mTextView.getInputType()))) {
      i = 0;
    } else {
      i = 1;
    }
    boolean bool3 = bool2;
    if (!bool1)
    {
      bool3 = bool2;
      if (j == 0) {
        if (i != 0) {
          bool3 = bool2;
        } else {
          bool3 = false;
        }
      }
    }
    return bool3;
  }
  
  private void startActionMode(@Editor.TextActionMode int paramInt, SelectionResult paramSelectionResult)
  {
    Object localObject = getText(this.mTextView);
    if ((paramSelectionResult != null) && ((localObject instanceof Spannable)) && ((this.mTextView.isTextSelectable()) || (this.mTextView.isTextEditable())))
    {
      if (!getTextClassificationSettings().isModelDarkLaunchEnabled())
      {
        Selection.setSelection((Spannable)localObject, paramSelectionResult.mStart, paramSelectionResult.mEnd);
        this.mTextView.invalidate();
      }
      this.mTextClassification = paramSelectionResult.mClassification;
    }
    else if ((paramSelectionResult != null) && (paramInt == 2))
    {
      this.mTextClassification = paramSelectionResult.mClassification;
    }
    else
    {
      this.mTextClassification = null;
    }
    if (this.mEditor.startActionModeInternal(paramInt))
    {
      localObject = this.mEditor.getSelectionController();
      if ((localObject != null) && ((this.mTextView.isTextSelectable()) || (this.mTextView.isTextEditable()))) {
        ((Editor.SelectionModifierCursorController)localObject).show();
      }
      if (paramSelectionResult != null) {
        if (paramInt != 0)
        {
          if (paramInt == 2) {
            this.mSelectionTracker.onLinkSelected(paramSelectionResult);
          }
        }
        else {
          this.mSelectionTracker.onSmartSelection(paramSelectionResult);
        }
      }
    }
    this.mEditor.setRestartActionModeOnNextRefresh(false);
    this.mTextClassificationAsyncTask = null;
  }
  
  private void startLinkActionMode(SelectionResult paramSelectionResult)
  {
    startActionMode(2, paramSelectionResult);
  }
  
  private void startSelectionActionMode(SelectionResult paramSelectionResult)
  {
    startActionMode(0, paramSelectionResult);
  }
  
  private void startSelectionActionModeWithSmartSelectAnimation(SelectionResult paramSelectionResult)
  {
    Object localObject = this.mTextView.getLayout();
    _..Lambda.SelectionActionModeHelper.xdBRwQcbRdz8duQr0RBo4YKAnOA localxdBRwQcbRdz8duQr0RBo4YKAnOA = new _..Lambda.SelectionActionModeHelper.xdBRwQcbRdz8duQr0RBo4YKAnOA(this, paramSelectionResult);
    int i;
    if ((paramSelectionResult != null) && ((this.mTextView.getSelectionStart() != paramSelectionResult.mStart) || (this.mTextView.getSelectionEnd() != paramSelectionResult.mEnd))) {
      i = 1;
    } else {
      i = 0;
    }
    if (i == 0)
    {
      localxdBRwQcbRdz8duQr0RBo4YKAnOA.run();
      return;
    }
    localObject = convertSelectionToRectangles((Layout)localObject, paramSelectionResult.mStart, paramSelectionResult.mEnd);
    paramSelectionResult = movePointInsideNearestRectangle(new PointF(this.mEditor.getLastUpPositionX(), this.mEditor.getLastUpPositionY()), (List)localObject, _..Lambda.ChL7kntlZCrPaPVdRfaSzGdk1JU.INSTANCE);
    this.mSmartSelectSprite.startAnimation(paramSelectionResult, (List)localObject, localxdBRwQcbRdz8duQr0RBo4YKAnOA);
  }
  
  public TextClassification getTextClassification()
  {
    return this.mTextClassification;
  }
  
  public void invalidateActionModeAsync()
  {
    cancelAsyncTask();
    if (skipTextClassification())
    {
      invalidateActionMode(null);
    }
    else
    {
      resetTextClassificationHelper();
      TextView localTextView = this.mTextView;
      int i = this.mTextClassificationHelper.getTimeoutDuration();
      TextClassificationHelper localTextClassificationHelper = this.mTextClassificationHelper;
      Objects.requireNonNull(localTextClassificationHelper);
      _..Lambda.aOGBsMC_jnvTDjezYLRtz35nAPI localaOGBsMC_jnvTDjezYLRtz35nAPI = new _..Lambda.aOGBsMC_jnvTDjezYLRtz35nAPI(localTextClassificationHelper);
      _..Lambda.SelectionActionModeHelper.Lwzg10CkEpNBaAXBpjnWEpIlTzQ localLwzg10CkEpNBaAXBpjnWEpIlTzQ = new _..Lambda.SelectionActionModeHelper.Lwzg10CkEpNBaAXBpjnWEpIlTzQ(this);
      localTextClassificationHelper = this.mTextClassificationHelper;
      Objects.requireNonNull(localTextClassificationHelper);
      this.mTextClassificationAsyncTask = new TextClassificationAsyncTask(localTextView, i, localaOGBsMC_jnvTDjezYLRtz35nAPI, localLwzg10CkEpNBaAXBpjnWEpIlTzQ, new _..Lambda.etfJkiCJnT2dqM2O4M2TCm9i_oA(localTextClassificationHelper)).execute(new Void[0]);
    }
  }
  
  public boolean isDrawingHighlight()
  {
    SmartSelectSprite localSmartSelectSprite = this.mSmartSelectSprite;
    boolean bool;
    if ((localSmartSelectSprite != null) && (localSmartSelectSprite.isAnimationActive())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void onDestroyActionMode()
  {
    cancelSmartSelectAnimation();
    this.mSelectionTracker.onSelectionDestroyed();
    cancelAsyncTask();
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    if (isDrawingHighlight())
    {
      SmartSelectSprite localSmartSelectSprite = this.mSmartSelectSprite;
      if (localSmartSelectSprite != null) {
        localSmartSelectSprite.draw(paramCanvas);
      }
    }
  }
  
  public void onSelectionAction(int paramInt)
  {
    this.mSelectionTracker.onSelectionAction(this.mTextView.getSelectionStart(), this.mTextView.getSelectionEnd(), getActionType(paramInt), this.mTextClassification);
  }
  
  public void onSelectionDrag()
  {
    this.mSelectionTracker.onSelectionAction(this.mTextView.getSelectionStart(), this.mTextView.getSelectionEnd(), 106, this.mTextClassification);
  }
  
  public void onTextChanged(int paramInt1, int paramInt2)
  {
    this.mSelectionTracker.onTextChanged(paramInt1, paramInt2, this.mTextClassification);
  }
  
  public boolean resetSelection(int paramInt)
  {
    if (this.mSelectionTracker.resetSelection(paramInt, this.mEditor))
    {
      invalidateActionModeAsync();
      return true;
    }
    return false;
  }
  
  public void startLinkActionModeAsync(int paramInt1, int paramInt2)
  {
    this.mSelectionTracker.onOriginalSelection(getText(this.mTextView), paramInt1, paramInt2, true);
    cancelAsyncTask();
    if (skipTextClassification())
    {
      startLinkActionMode(null);
    }
    else
    {
      resetTextClassificationHelper(paramInt1, paramInt2);
      TextView localTextView = this.mTextView;
      paramInt1 = this.mTextClassificationHelper.getTimeoutDuration();
      TextClassificationHelper localTextClassificationHelper = this.mTextClassificationHelper;
      Objects.requireNonNull(localTextClassificationHelper);
      _..Lambda.aOGBsMC_jnvTDjezYLRtz35nAPI localaOGBsMC_jnvTDjezYLRtz35nAPI = new _..Lambda.aOGBsMC_jnvTDjezYLRtz35nAPI(localTextClassificationHelper);
      _..Lambda.SelectionActionModeHelper.WnFw1_gP20c3ltvTN6OPqQ5XUns localWnFw1_gP20c3ltvTN6OPqQ5XUns = new _..Lambda.SelectionActionModeHelper.WnFw1_gP20c3ltvTN6OPqQ5XUns(this);
      localTextClassificationHelper = this.mTextClassificationHelper;
      Objects.requireNonNull(localTextClassificationHelper);
      this.mTextClassificationAsyncTask = new TextClassificationAsyncTask(localTextView, paramInt1, localaOGBsMC_jnvTDjezYLRtz35nAPI, localWnFw1_gP20c3ltvTN6OPqQ5XUns, new _..Lambda.etfJkiCJnT2dqM2O4M2TCm9i_oA(localTextClassificationHelper)).execute(new Void[0]);
    }
  }
  
  public void startSelectionActionModeAsync(boolean paramBoolean)
  {
    boolean bool = getTextClassificationSettings().isSmartSelectionEnabled();
    this.mSelectionTracker.onOriginalSelection(getText(this.mTextView), this.mTextView.getSelectionStart(), this.mTextView.getSelectionEnd(), false);
    cancelAsyncTask();
    if (skipTextClassification())
    {
      startSelectionActionMode(null);
    }
    else
    {
      resetTextClassificationHelper();
      TextView localTextView = this.mTextView;
      int i = this.mTextClassificationHelper.getTimeoutDuration();
      Object localObject1;
      if ((paramBoolean & bool))
      {
        localObject1 = this.mTextClassificationHelper;
        Objects.requireNonNull(localObject1);
        localObject1 = new _..Lambda.E_XesXLNXm7BCuVAnjZcIGfnQJQ((TextClassificationHelper)localObject1);
      }
      else
      {
        localObject1 = this.mTextClassificationHelper;
        Objects.requireNonNull(localObject1);
        localObject1 = new _..Lambda.aOGBsMC_jnvTDjezYLRtz35nAPI((TextClassificationHelper)localObject1);
      }
      Object localObject2;
      if (this.mSmartSelectSprite != null) {
        localObject2 = new _..Lambda.SelectionActionModeHelper.l1f1_V5lw6noQxI_3u11qF753Iw(this);
      } else {
        localObject2 = new _..Lambda.SelectionActionModeHelper.CcJ0IF8nDFsmkuaqvOxFqYGazzY(this);
      }
      TextClassificationHelper localTextClassificationHelper = this.mTextClassificationHelper;
      Objects.requireNonNull(localTextClassificationHelper);
      this.mTextClassificationAsyncTask = new TextClassificationAsyncTask(localTextView, i, (Supplier)localObject1, (Consumer)localObject2, new _..Lambda.etfJkiCJnT2dqM2O4M2TCm9i_oA(localTextClassificationHelper)).execute(new Void[0]);
    }
  }
  
  private static final class SelectionMetricsLogger
  {
    private static final String LOG_TAG = "SelectionMetricsLogger";
    private static final Pattern PATTERN_WHITESPACE = Pattern.compile("\\s+");
    private TextClassifier mClassificationSession;
    private final boolean mEditTextLogger;
    private int mStartIndex;
    private String mText;
    private final BreakIterator mTokenIterator;
    
    SelectionMetricsLogger(TextView paramTextView)
    {
      Preconditions.checkNotNull(paramTextView);
      this.mEditTextLogger = paramTextView.isTextEditable();
      this.mTokenIterator = SelectionSessionLogger.getTokenIterator(paramTextView.getTextLocale());
    }
    
    private int countWordsBackward(int paramInt)
    {
      boolean bool;
      if (paramInt >= this.mStartIndex) {
        bool = true;
      } else {
        bool = false;
      }
      Preconditions.checkArgument(bool);
      int k;
      for (int i = 0; paramInt > this.mStartIndex; i = k)
      {
        int j = this.mTokenIterator.preceding(paramInt);
        k = i;
        if (!isWhitespace(j, paramInt)) {
          k = i + 1;
        }
        paramInt = j;
      }
      return i;
    }
    
    private int countWordsForward(int paramInt)
    {
      boolean bool;
      if (paramInt <= this.mStartIndex) {
        bool = true;
      } else {
        bool = false;
      }
      Preconditions.checkArgument(bool);
      int i = 0;
      int j = paramInt;
      for (paramInt = i; j < this.mStartIndex; paramInt = i)
      {
        int k = this.mTokenIterator.following(j);
        i = paramInt;
        if (!isWhitespace(j, k)) {
          i = paramInt + 1;
        }
        j = k;
      }
      return paramInt;
    }
    
    private static String getWidetType(TextView paramTextView)
    {
      if (paramTextView.isTextEditable()) {
        return "edittext";
      }
      if (paramTextView.isTextSelectable()) {
        return "textview";
      }
      return "nosel-textview";
    }
    
    private int[] getWordDelta(int paramInt1, int paramInt2)
    {
      int[] arrayOfInt = new int[2];
      int i = this.mStartIndex;
      if (paramInt1 == i)
      {
        arrayOfInt[0] = 0;
      }
      else if (paramInt1 < i)
      {
        arrayOfInt[0] = (-countWordsForward(paramInt1));
      }
      else
      {
        arrayOfInt[0] = countWordsBackward(paramInt1);
        if ((!this.mTokenIterator.isBoundary(paramInt1)) && (!isWhitespace(this.mTokenIterator.preceding(paramInt1), this.mTokenIterator.following(paramInt1)))) {
          arrayOfInt[0] -= 1;
        }
      }
      paramInt1 = this.mStartIndex;
      if (paramInt2 == paramInt1) {
        arrayOfInt[1] = 0;
      } else if (paramInt2 < paramInt1) {
        arrayOfInt[1] = (-countWordsForward(paramInt2));
      } else {
        arrayOfInt[1] = countWordsBackward(paramInt2);
      }
      return arrayOfInt;
    }
    
    private boolean hasActiveClassificationSession()
    {
      TextClassifier localTextClassifier = this.mClassificationSession;
      boolean bool;
      if ((localTextClassifier != null) && (!localTextClassifier.isDestroyed())) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    private boolean isWhitespace(int paramInt1, int paramInt2)
    {
      return PATTERN_WHITESPACE.matcher(this.mText.substring(paramInt1, paramInt2)).matches();
    }
    
    public void endTextClassificationSession()
    {
      if (hasActiveClassificationSession()) {
        this.mClassificationSession.destroy();
      }
    }
    
    public boolean isEditTextLogger()
    {
      return this.mEditTextLogger;
    }
    
    public void logSelectionAction(int paramInt1, int paramInt2, int paramInt3, TextClassification paramTextClassification)
    {
      try
      {
        if (hasActiveClassificationSession())
        {
          Preconditions.checkArgumentInRange(paramInt1, 0, this.mText.length(), "start");
          Preconditions.checkArgumentInRange(paramInt2, paramInt1, this.mText.length(), "end");
          int[] arrayOfInt = getWordDelta(paramInt1, paramInt2);
          if (paramTextClassification != null) {
            this.mClassificationSession.onSelectionEvent(SelectionEvent.createSelectionActionEvent(arrayOfInt[0], arrayOfInt[1], paramInt3, paramTextClassification));
          } else {
            this.mClassificationSession.onSelectionEvent(SelectionEvent.createSelectionActionEvent(arrayOfInt[0], arrayOfInt[1], paramInt3));
          }
          if (SelectionEvent.isTerminal(paramInt3)) {
            endTextClassificationSession();
          }
        }
      }
      catch (Exception localException)
      {
        paramTextClassification = new StringBuilder();
        paramTextClassification.append("");
        paramTextClassification.append(localException.getMessage());
        Log.e("SelectionMetricsLogger", paramTextClassification.toString(), localException);
      }
    }
    
    public void logSelectionModified(int paramInt1, int paramInt2, TextClassification paramTextClassification, TextSelection paramTextSelection)
    {
      try
      {
        if (hasActiveClassificationSession())
        {
          Preconditions.checkArgumentInRange(paramInt1, 0, this.mText.length(), "start");
          Preconditions.checkArgumentInRange(paramInt2, paramInt1, this.mText.length(), "end");
          int[] arrayOfInt = getWordDelta(paramInt1, paramInt2);
          if (paramTextSelection != null) {
            this.mClassificationSession.onSelectionEvent(SelectionEvent.createSelectionModifiedEvent(arrayOfInt[0], arrayOfInt[1], paramTextSelection));
          } else if (paramTextClassification != null) {
            this.mClassificationSession.onSelectionEvent(SelectionEvent.createSelectionModifiedEvent(arrayOfInt[0], arrayOfInt[1], paramTextClassification));
          } else {
            this.mClassificationSession.onSelectionEvent(SelectionEvent.createSelectionModifiedEvent(arrayOfInt[0], arrayOfInt[1]));
          }
        }
      }
      catch (Exception paramTextSelection)
      {
        paramTextClassification = new StringBuilder();
        paramTextClassification.append("");
        paramTextClassification.append(paramTextSelection.getMessage());
        Log.e("SelectionMetricsLogger", paramTextClassification.toString(), paramTextSelection);
      }
    }
    
    public void logSelectionStarted(TextClassifier paramTextClassifier, CharSequence paramCharSequence, int paramInt1, int paramInt2)
    {
      try
      {
        Preconditions.checkNotNull(paramCharSequence);
        Preconditions.checkArgumentInRange(paramInt1, 0, paramCharSequence.length(), "index");
        if ((this.mText == null) || (!this.mText.contentEquals(paramCharSequence))) {
          this.mText = paramCharSequence.toString();
        }
        this.mTokenIterator.setText(this.mText);
        this.mStartIndex = paramInt1;
        this.mClassificationSession = paramTextClassifier;
        if (hasActiveClassificationSession()) {
          this.mClassificationSession.onSelectionEvent(SelectionEvent.createSelectionStartedEvent(paramInt2, 0));
        }
      }
      catch (Exception paramCharSequence)
      {
        paramTextClassifier = new StringBuilder();
        paramTextClassifier.append("");
        paramTextClassifier.append(paramCharSequence.getMessage());
        Log.e("SelectionMetricsLogger", paramTextClassifier.toString(), paramCharSequence);
      }
    }
  }
  
  private static final class SelectionResult
  {
    private final TextClassification mClassification;
    private final int mEnd;
    private final TextSelection mSelection;
    private final int mStart;
    
    SelectionResult(int paramInt1, int paramInt2, TextClassification paramTextClassification, TextSelection paramTextSelection)
    {
      this.mStart = paramInt1;
      this.mEnd = paramInt2;
      this.mClassification = paramTextClassification;
      this.mSelection = paramTextSelection;
    }
  }
  
  private static final class SelectionTracker
  {
    private boolean mAllowReset;
    private final LogAbandonRunnable mDelayedLogAbandon = new LogAbandonRunnable(null);
    private SelectionActionModeHelper.SelectionMetricsLogger mLogger;
    private int mOriginalEnd;
    private int mOriginalStart;
    private int mSelectionEnd;
    private int mSelectionStart;
    private final TextView mTextView;
    
    SelectionTracker(TextView paramTextView)
    {
      this.mTextView = ((TextView)Preconditions.checkNotNull(paramTextView));
      this.mLogger = new SelectionActionModeHelper.SelectionMetricsLogger(paramTextView);
    }
    
    private boolean isSelectionStarted()
    {
      int i = this.mSelectionStart;
      if (i >= 0)
      {
        int j = this.mSelectionEnd;
        if ((j >= 0) && (i != j)) {
          return true;
        }
      }
      boolean bool = false;
      return bool;
    }
    
    private void maybeInvalidateLogger()
    {
      if (this.mLogger.isEditTextLogger() != this.mTextView.isTextEditable()) {
        this.mLogger = new SelectionActionModeHelper.SelectionMetricsLogger(this.mTextView);
      }
    }
    
    private void onClassifiedSelection(SelectionActionModeHelper.SelectionResult paramSelectionResult)
    {
      if (isSelectionStarted())
      {
        this.mSelectionStart = paramSelectionResult.mStart;
        this.mSelectionEnd = paramSelectionResult.mEnd;
        boolean bool;
        if ((this.mSelectionStart == this.mOriginalStart) && (this.mSelectionEnd == this.mOriginalEnd)) {
          bool = false;
        } else {
          bool = true;
        }
        this.mAllowReset = bool;
      }
    }
    
    public void onLinkSelected(SelectionActionModeHelper.SelectionResult paramSelectionResult)
    {
      onClassifiedSelection(paramSelectionResult);
    }
    
    public void onOriginalSelection(CharSequence paramCharSequence, int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this.mDelayedLogAbandon.flush();
      this.mSelectionStart = paramInt1;
      this.mOriginalStart = paramInt1;
      this.mSelectionEnd = paramInt2;
      this.mOriginalEnd = paramInt2;
      this.mAllowReset = false;
      maybeInvalidateLogger();
      SelectionActionModeHelper.SelectionMetricsLogger localSelectionMetricsLogger = this.mLogger;
      TextClassifier localTextClassifier = this.mTextView.getTextClassificationSession();
      if (paramBoolean) {
        paramInt2 = 2;
      } else {
        paramInt2 = 1;
      }
      localSelectionMetricsLogger.logSelectionStarted(localTextClassifier, paramCharSequence, paramInt1, paramInt2);
    }
    
    public void onSelectionAction(int paramInt1, int paramInt2, int paramInt3, TextClassification paramTextClassification)
    {
      if (isSelectionStarted())
      {
        this.mAllowReset = false;
        this.mLogger.logSelectionAction(paramInt1, paramInt2, paramInt3, paramTextClassification);
      }
    }
    
    public void onSelectionDestroyed()
    {
      this.mAllowReset = false;
      this.mDelayedLogAbandon.schedule(100);
    }
    
    public void onSelectionUpdated(int paramInt1, int paramInt2, TextClassification paramTextClassification)
    {
      if (isSelectionStarted())
      {
        this.mSelectionStart = paramInt1;
        this.mSelectionEnd = paramInt2;
        this.mAllowReset = false;
        this.mLogger.logSelectionModified(paramInt1, paramInt2, paramTextClassification, null);
      }
    }
    
    public void onSmartSelection(SelectionActionModeHelper.SelectionResult paramSelectionResult)
    {
      onClassifiedSelection(paramSelectionResult);
      this.mLogger.logSelectionModified(paramSelectionResult.mStart, paramSelectionResult.mEnd, paramSelectionResult.mClassification, paramSelectionResult.mSelection);
    }
    
    public void onTextChanged(int paramInt1, int paramInt2, TextClassification paramTextClassification)
    {
      if ((isSelectionStarted()) && (paramInt1 == this.mSelectionStart) && (paramInt2 == this.mSelectionEnd)) {
        onSelectionAction(paramInt1, paramInt2, 100, paramTextClassification);
      }
    }
    
    public boolean resetSelection(int paramInt, Editor paramEditor)
    {
      TextView localTextView = paramEditor.getTextView();
      if ((isSelectionStarted()) && (this.mAllowReset) && (paramInt >= this.mSelectionStart) && (paramInt <= this.mSelectionEnd) && ((SelectionActionModeHelper.getText(localTextView) instanceof Spannable)))
      {
        this.mAllowReset = false;
        boolean bool = paramEditor.selectCurrentWord();
        if (bool)
        {
          this.mSelectionStart = paramEditor.getTextView().getSelectionStart();
          this.mSelectionEnd = paramEditor.getTextView().getSelectionEnd();
          this.mLogger.logSelectionAction(localTextView.getSelectionStart(), localTextView.getSelectionEnd(), 201, null);
        }
        return bool;
      }
      return false;
    }
    
    private final class LogAbandonRunnable
      implements Runnable
    {
      private boolean mIsPending;
      
      private LogAbandonRunnable() {}
      
      void flush()
      {
        SelectionActionModeHelper.SelectionTracker.this.mTextView.removeCallbacks(this);
        run();
      }
      
      public void run()
      {
        if (this.mIsPending)
        {
          SelectionActionModeHelper.SelectionTracker.this.mLogger.logSelectionAction(SelectionActionModeHelper.SelectionTracker.this.mSelectionStart, SelectionActionModeHelper.SelectionTracker.this.mSelectionEnd, 107, null);
          SelectionActionModeHelper.SelectionTracker localSelectionTracker = SelectionActionModeHelper.SelectionTracker.this;
          SelectionActionModeHelper.SelectionTracker.access$702(localSelectionTracker, SelectionActionModeHelper.SelectionTracker.access$802(localSelectionTracker, -1));
          SelectionActionModeHelper.SelectionTracker.this.mLogger.endTextClassificationSession();
          this.mIsPending = false;
        }
      }
      
      void schedule(int paramInt)
      {
        if (this.mIsPending)
        {
          Log.e("SelectActionModeHelper", "Force flushing abandon due to new scheduling request");
          flush();
        }
        this.mIsPending = true;
        SelectionActionModeHelper.SelectionTracker.this.mTextView.postDelayed(this, paramInt);
      }
    }
  }
  
  private static final class TextClassificationAsyncTask
    extends AsyncTask<Void, Void, SelectionActionModeHelper.SelectionResult>
  {
    private final String mOriginalText;
    private final Consumer<SelectionActionModeHelper.SelectionResult> mSelectionResultCallback;
    private final Supplier<SelectionActionModeHelper.SelectionResult> mSelectionResultSupplier;
    private final TextView mTextView;
    private final int mTimeOutDuration;
    private final Supplier<SelectionActionModeHelper.SelectionResult> mTimeOutResultSupplier;
    
    TextClassificationAsyncTask(TextView paramTextView, int paramInt, Supplier<SelectionActionModeHelper.SelectionResult> paramSupplier1, Consumer<SelectionActionModeHelper.SelectionResult> paramConsumer, Supplier<SelectionActionModeHelper.SelectionResult> paramSupplier2)
    {
      super();
      this.mTextView = ((TextView)Preconditions.checkNotNull(paramTextView));
      this.mTimeOutDuration = paramInt;
      this.mSelectionResultSupplier = ((Supplier)Preconditions.checkNotNull(paramSupplier1));
      this.mSelectionResultCallback = ((Consumer)Preconditions.checkNotNull(paramConsumer));
      this.mTimeOutResultSupplier = ((Supplier)Preconditions.checkNotNull(paramSupplier2));
      this.mOriginalText = SelectionActionModeHelper.getText(this.mTextView).toString();
    }
    
    private void onTimeOut()
    {
      if (getStatus() == AsyncTask.Status.RUNNING) {
        onPostExecute((SelectionActionModeHelper.SelectionResult)this.mTimeOutResultSupplier.get());
      }
      cancel(true);
    }
    
    protected SelectionActionModeHelper.SelectionResult doInBackground(Void... paramVarArgs)
    {
      paramVarArgs = new _..Lambda.SelectionActionModeHelper.TextClassificationAsyncTask.D5tkmK_caFBtl9ux2L0aUfUee4E(this);
      this.mTextView.postDelayed(paramVarArgs, this.mTimeOutDuration);
      SelectionActionModeHelper.SelectionResult localSelectionResult = (SelectionActionModeHelper.SelectionResult)this.mSelectionResultSupplier.get();
      this.mTextView.removeCallbacks(paramVarArgs);
      return localSelectionResult;
    }
    
    protected void onPostExecute(SelectionActionModeHelper.SelectionResult paramSelectionResult)
    {
      if (!TextUtils.equals(this.mOriginalText, SelectionActionModeHelper.getText(this.mTextView))) {
        paramSelectionResult = null;
      }
      this.mSelectionResultCallback.accept(paramSelectionResult);
    }
  }
  
  private static final class TextClassificationHelper
  {
    private static final int TRIM_DELTA = 120;
    private final Context mContext;
    private LocaleList mDefaultLocales;
    private boolean mHot;
    private LocaleList mLastClassificationLocales;
    private SelectionActionModeHelper.SelectionResult mLastClassificationResult;
    private int mLastClassificationSelectionEnd;
    private int mLastClassificationSelectionStart;
    private CharSequence mLastClassificationText;
    private int mRelativeEnd;
    private int mRelativeStart;
    private int mSelectionEnd;
    private int mSelectionStart;
    private String mText;
    private Supplier<TextClassifier> mTextClassifier;
    private int mTrimStart;
    private CharSequence mTrimmedText;
    
    TextClassificationHelper(Context paramContext, Supplier<TextClassifier> paramSupplier, CharSequence paramCharSequence, int paramInt1, int paramInt2, LocaleList paramLocaleList)
    {
      init(paramSupplier, paramCharSequence, paramInt1, paramInt2, paramLocaleList);
      this.mContext = ((Context)Preconditions.checkNotNull(paramContext));
    }
    
    private boolean isDarkLaunchEnabled()
    {
      return TextClassificationManager.getSettings(this.mContext).isModelDarkLaunchEnabled();
    }
    
    private SelectionActionModeHelper.SelectionResult performClassification(TextSelection paramTextSelection)
    {
      if ((!Objects.equals(this.mText, this.mLastClassificationText)) || (this.mSelectionStart != this.mLastClassificationSelectionStart) || (this.mSelectionEnd != this.mLastClassificationSelectionEnd) || (!Objects.equals(this.mDefaultLocales, this.mLastClassificationLocales)))
      {
        this.mLastClassificationText = this.mText;
        this.mLastClassificationSelectionStart = this.mSelectionStart;
        this.mLastClassificationSelectionEnd = this.mSelectionEnd;
        this.mLastClassificationLocales = this.mDefaultLocales;
        trimText();
        Object localObject;
        if (this.mContext.getApplicationInfo().targetSdkVersion >= 28)
        {
          localObject = new TextClassification.Request.Builder(this.mTrimmedText, this.mRelativeStart, this.mRelativeEnd).setDefaultLocales(this.mDefaultLocales).build();
          localObject = ((TextClassifier)this.mTextClassifier.get()).classifyText((TextClassification.Request)localObject);
        }
        else
        {
          localObject = ((TextClassifier)this.mTextClassifier.get()).classifyText(this.mTrimmedText, this.mRelativeStart, this.mRelativeEnd, this.mDefaultLocales);
        }
        this.mLastClassificationResult = new SelectionActionModeHelper.SelectionResult(this.mSelectionStart, this.mSelectionEnd, (TextClassification)localObject, paramTextSelection);
      }
      return this.mLastClassificationResult;
    }
    
    private void trimText()
    {
      this.mTrimStart = Math.max(0, this.mSelectionStart - 120);
      int i = Math.min(this.mText.length(), this.mSelectionEnd + 120);
      this.mTrimmedText = this.mText.subSequence(this.mTrimStart, i);
      i = this.mSelectionStart;
      int j = this.mTrimStart;
      this.mRelativeStart = (i - j);
      this.mRelativeEnd = (this.mSelectionEnd - j);
    }
    
    public SelectionActionModeHelper.SelectionResult classifyText()
    {
      this.mHot = true;
      return performClassification(null);
    }
    
    public SelectionActionModeHelper.SelectionResult getOriginalSelection()
    {
      return new SelectionActionModeHelper.SelectionResult(this.mSelectionStart, this.mSelectionEnd, null, null);
    }
    
    public int getTimeoutDuration()
    {
      if (this.mHot) {
        return 200;
      }
      return 500;
    }
    
    public void init(Supplier<TextClassifier> paramSupplier, CharSequence paramCharSequence, int paramInt1, int paramInt2, LocaleList paramLocaleList)
    {
      this.mTextClassifier = ((Supplier)Preconditions.checkNotNull(paramSupplier));
      this.mText = ((CharSequence)Preconditions.checkNotNull(paramCharSequence)).toString();
      this.mLastClassificationText = null;
      boolean bool;
      if (paramInt2 > paramInt1) {
        bool = true;
      } else {
        bool = false;
      }
      Preconditions.checkArgument(bool);
      this.mSelectionStart = paramInt1;
      this.mSelectionEnd = paramInt2;
      this.mDefaultLocales = paramLocaleList;
    }
    
    public SelectionActionModeHelper.SelectionResult suggestSelection()
    {
      this.mHot = true;
      trimText();
      Object localObject;
      if (this.mContext.getApplicationInfo().targetSdkVersion >= 28)
      {
        localObject = new TextSelection.Request.Builder(this.mTrimmedText, this.mRelativeStart, this.mRelativeEnd).setDefaultLocales(this.mDefaultLocales).setDarkLaunchAllowed(true).build();
        localObject = ((TextClassifier)this.mTextClassifier.get()).suggestSelection((TextSelection.Request)localObject);
      }
      else
      {
        localObject = ((TextClassifier)this.mTextClassifier.get()).suggestSelection(this.mTrimmedText, this.mRelativeStart, this.mRelativeEnd, this.mDefaultLocales);
      }
      if (!isDarkLaunchEnabled())
      {
        this.mSelectionStart = Math.max(0, ((TextSelection)localObject).getSelectionStartIndex() + this.mTrimStart);
        this.mSelectionEnd = Math.min(this.mText.length(), ((TextSelection)localObject).getSelectionEndIndex() + this.mTrimStart);
      }
      return performClassification((TextSelection)localObject);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/SelectionActionModeHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */