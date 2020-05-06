package miui.view;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;

public class MiuiKeyBoardView
  extends FrameLayout
  implements View.OnClickListener, View.OnTouchListener
{
  private static final float FUNC_KEY_RATIO = 1.6F;
  private static final float HORIZONTAL_MARGIN_RATIO = 0.2F;
  private static final float OK_KEY_RATIO = 2.8F;
  private static final int PREVIEW_ANIMATION_DURATION = 100;
  private static final long SHOW_PREVIEW_DURATION = 300L;
  private static final float[][] SIZE_GROUP;
  private static final float SPACE_KEY_RATIO = 5.8F;
  private static final String SPACE_STR = " ";
  private static final float VERTICAL_MARGIN_RATIO = 0.17F;
  private ArrayList<KeyButton> mAllKeys = new ArrayList();
  private View mBtnCapsLock;
  private View mBtnLetterDelete;
  private View mBtnLetterOK;
  private View mBtnLetterSpace;
  private View mBtnSymbolDelete;
  private View mBtnSymbolOK;
  private View mBtnSymbolSpace;
  private View mBtnToLetterBoard;
  private View mBtnToSymbolBoard;
  private Runnable mConfirmHide = new Runnable()
  {
    public void run()
    {
      MiuiKeyBoardView.this.showPreviewAnim(false);
    }
  };
  private Context mContext;
  private boolean mIsShowingPreview = false;
  private boolean mIsUpperMode = false;
  private ArrayList<OnKeyboardActionListener> mKeyboardListeners;
  private FrameLayout mLetterBoard;
  private int mPopupViewHeight;
  private int mPopupViewWidth;
  private int mPopupViewX;
  private int mPopupViewY;
  private TextView mPreviewText;
  private final Runnable mSendDeleteActionRunnable = new Runnable()
  {
    public void run()
    {
      MiuiKeyBoardView.this.onKeyBoardDelete();
      MiuiKeyBoardView.this.postDelayed(this, 50L);
    }
  };
  private ValueAnimator mShowPreviewAnimator = new ValueAnimator();
  private long mShowPreviewLastTime = 0L;
  private Animation mShrinkToBottonAnimation = null;
  private Animation mStretchFromBottonAnimation = null;
  private FrameLayout mSymbolBoard;
  
  static
  {
    float[] arrayOfFloat1 = { 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F };
    float[] arrayOfFloat2 = { 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F };
    float[] arrayOfFloat3 = { 2.8F, 5.8F, 2.8F };
    SIZE_GROUP = new float[][] { arrayOfFloat1, arrayOfFloat2, { 1.6F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.6F }, arrayOfFloat3 };
  }
  
  public MiuiKeyBoardView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public MiuiKeyBoardView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, -1);
  }
  
  public MiuiKeyBoardView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mContext = paramContext;
    View.inflate(this.mContext, 285933607, this);
    View.inflate(this.mContext, 285933625, this);
    View.inflate(this.mContext, 285933604, this);
    setFocusableInTouchMode(true);
  }
  
  private void calcAndStartShowPreviewAnim(View paramView)
  {
    if ((paramView instanceof KeyButton))
    {
      this.mPreviewText.setText(((KeyButton)paramView).getText());
      this.mPreviewText.setTypeface(Typeface.DEFAULT_BOLD);
      this.mPopupViewWidth = ((int)(paramView.getWidth() * 1.7D));
      this.mPopupViewHeight = ((int)(paramView.getHeight() * 1.4D));
      this.mPreviewText.setWidth(this.mPopupViewWidth);
      this.mPreviewText.setHeight(this.mPopupViewHeight);
      float[] arrayOfFloat = new float[2];
      getChildCoordRelativeToKeyboard(paramView, arrayOfFloat, false, true);
      this.mPopupViewX = ((int)(arrayOfFloat[0] + (paramView.getWidth() - this.mPopupViewWidth) / 2));
      this.mPopupViewY = ((int)(arrayOfFloat[1] - this.mPopupViewHeight - paramView.getHeight() * 0.17F));
      showPreviewAnim(true);
      this.mPreviewText.setVisibility(0);
    }
  }
  
  private float getChildCoordRelativeToKeyboard(View paramView, float[] paramArrayOfFloat, boolean paramBoolean1, boolean paramBoolean2)
  {
    paramArrayOfFloat[1] = 0.0F;
    paramArrayOfFloat[0] = 0.0F;
    if (paramBoolean1) {
      paramView.getMatrix().mapPoints(paramArrayOfFloat);
    }
    float f1 = 1.0F * paramView.getScaleX();
    paramArrayOfFloat[0] += paramView.getLeft();
    paramArrayOfFloat[1] += paramView.getTop();
    Object localObject = paramView.getParent();
    while (((localObject instanceof View)) && (localObject != this))
    {
      localObject = (View)localObject;
      float f2 = f1;
      if (paramBoolean1)
      {
        ((View)localObject).getMatrix().mapPoints(paramArrayOfFloat);
        f2 = f1 * ((View)localObject).getScaleX();
      }
      paramArrayOfFloat[0] += ((View)localObject).getLeft() - ((View)localObject).getScrollX();
      paramArrayOfFloat[1] += ((View)localObject).getTop() - ((View)localObject).getScrollY();
      localObject = ((View)localObject).getParent();
      f1 = f2;
    }
    if (paramBoolean2)
    {
      paramArrayOfFloat[0] -= paramView.getWidth() * (1.0F - f1) / 2.0F;
      paramArrayOfFloat[1] -= paramView.getHeight() * (1.0F - f1) / 2.0F;
    }
    return f1;
  }
  
  private void onKeyBoardDelete()
  {
    Iterator localIterator = this.mKeyboardListeners.iterator();
    while (localIterator.hasNext()) {
      ((OnKeyboardActionListener)localIterator.next()).onKeyBoardDelete();
    }
  }
  
  private void onKeyBoardOK()
  {
    Iterator localIterator = this.mKeyboardListeners.iterator();
    while (localIterator.hasNext()) {
      ((OnKeyboardActionListener)localIterator.next()).onKeyBoardOK();
    }
  }
  
  private void onText(CharSequence paramCharSequence)
  {
    Iterator localIterator = this.mKeyboardListeners.iterator();
    while (localIterator.hasNext()) {
      ((OnKeyboardActionListener)localIterator.next()).onText(paramCharSequence);
    }
  }
  
  private void setOnTouchAndClickListenerForKey(ViewGroup paramViewGroup)
  {
    int i = paramViewGroup.getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = paramViewGroup.getChildAt(j);
      if ((localView instanceof KeyButton))
      {
        localView.setOnClickListener(this);
        localView.setOnTouchListener(this);
        this.mAllKeys.add((KeyButton)localView);
      }
      else if ((localView instanceof ViewGroup))
      {
        setOnTouchAndClickListenerForKey((ViewGroup)localView);
      }
    }
  }
  
  private void shiftLetterBoard()
  {
    Iterator localIterator = this.mAllKeys.iterator();
    while (localIterator.hasNext())
    {
      KeyButton localKeyButton = (KeyButton)localIterator.next();
      if ((localKeyButton.getTag() instanceof String))
      {
        String str = (String)localKeyButton.getTag();
        if ((str.length() == 1) && (Character.isLowerCase(str.toCharArray()[0])))
        {
          if (this.mIsUpperMode) {
            str = str.toLowerCase();
          } else {
            str = str.toUpperCase();
          }
          localKeyButton.setText(str);
        }
      }
    }
    this.mIsUpperMode ^= true;
    if (this.mIsUpperMode) {
      this.mBtnCapsLock.setBackgroundResource(285671682);
    } else {
      this.mBtnCapsLock.setBackgroundResource(285671681);
    }
  }
  
  private void showLetterBoard(boolean paramBoolean)
  {
    FrameLayout localFrameLayout = this.mLetterBoard;
    int i = 0;
    if (paramBoolean) {
      j = 0;
    } else {
      j = 4;
    }
    localFrameLayout.setVisibility(j);
    localFrameLayout = this.mSymbolBoard;
    int j = i;
    if (paramBoolean) {
      j = 4;
    }
    localFrameLayout.setVisibility(j);
  }
  
  private void showPreviewAnim(boolean paramBoolean)
  {
    this.mShowPreviewAnimator.cancel();
    removeCallbacks(this.mConfirmHide);
    this.mShowPreviewAnimator.removeAllListeners();
    this.mShowPreviewAnimator.removeAllUpdateListeners();
    if (paramBoolean) {
      this.mShowPreviewAnimator.setFloatValues(new float[] { 0.0F, 1.0F });
    } else {
      this.mShowPreviewAnimator.setFloatValues(new float[] { 1.0F, 0.0F });
    }
    this.mShowPreviewAnimator.setDuration(100L);
    this.mPreviewText.setVisibility(0);
    this.mPreviewText.setPivotX(this.mPopupViewWidth * 0.5F);
    this.mPreviewText.setPivotY(this.mPopupViewHeight);
    this.mShowPreviewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        float f = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        MiuiKeyBoardView.this.mPreviewText.setAlpha(f);
      }
    });
    this.mShowPreviewAnimator.start();
    this.mIsShowingPreview = paramBoolean;
    if (paramBoolean) {
      this.mShowPreviewLastTime = System.currentTimeMillis();
    }
  }
  
  public void addKeyboardListener(OnKeyboardActionListener paramOnKeyboardActionListener)
  {
    Iterator localIterator = this.mKeyboardListeners.iterator();
    while (localIterator.hasNext()) {
      if (paramOnKeyboardActionListener.equals((OnKeyboardActionListener)localIterator.next())) {
        return;
      }
    }
    this.mKeyboardListeners.add(paramOnKeyboardActionListener);
  }
  
  public void hide()
  {
    startAnimation(this.mShrinkToBottonAnimation);
  }
  
  void keyboardOnLayout(ViewGroup paramViewGroup, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    int i = SIZE_GROUP.length;
    int j = 0;
    int k = this.mPaddingTop;
    for (int m = 0; m < i; m++)
    {
      float[] arrayOfFloat = SIZE_GROUP[m];
      float f1 = 0.0F;
      for (int n = 0; n < arrayOfFloat.length; n++) {
        f1 += arrayOfFloat[n] * paramInt2;
      }
      float f2 = (arrayOfFloat.length - 1) * paramInt3;
      n = (int)((paramInt1 - (f1 + f2)) / 2.0F);
      for (int i1 = 0;; i1++)
      {
        int i2 = paramInt2;
        if (i1 >= arrayOfFloat.length) {
          break;
        }
        KeyButton localKeyButton = (KeyButton)paramViewGroup.getChildAt(j);
        int i3 = n;
        int i4 = i3;
        if ("!".equals(localKeyButton.getText())) {
          i4 = (int)(i3 + i2 * (arrayOfFloat[i1] - 1.0F));
        }
        localKeyButton.layout(i4, k, (int)(n + i2 * arrayOfFloat[i1]), k + paramInt4);
        n = (int)(n + (i2 * arrayOfFloat[i1] + paramInt3));
        j++;
      }
      k += paramInt5 + paramInt4;
    }
  }
  
  protected void onAttachedToWindow()
  {
    if (getParent() != null) {
      ((ViewGroup)getParent()).setClipChildren(false);
    }
    super.onAttachedToWindow();
  }
  
  public void onClick(View paramView)
  {
    if (!isEnabled()) {
      return;
    }
    if (paramView == this.mBtnCapsLock) {
      shiftLetterBoard();
    } else if (paramView == this.mBtnToSymbolBoard) {
      showLetterBoard(false);
    } else if (paramView == this.mBtnToLetterBoard) {
      showLetterBoard(true);
    } else if ((paramView != this.mBtnLetterDelete) && (paramView != this.mBtnSymbolDelete))
    {
      if ((paramView != this.mBtnSymbolOK) && (paramView != this.mBtnLetterOK))
      {
        if ((paramView != this.mBtnSymbolSpace) && (paramView != this.mBtnLetterSpace)) {
          onText(((KeyButton)paramView).getText());
        } else {
          onText(" ");
        }
      }
      else {
        onKeyBoardOK();
      }
    }
    else {
      onKeyBoardDelete();
    }
  }
  
  protected void onFinishInflate()
  {
    Resources localResources = this.mContext.getResources();
    this.mPaddingTop = localResources.getDimensionPixelSize(285606024);
    this.mPaddingLeft = localResources.getDimensionPixelSize(285606023);
    this.mStretchFromBottonAnimation = AnimationUtils.loadAnimation(getContext(), 285278242);
    this.mShrinkToBottonAnimation = AnimationUtils.loadAnimation(getContext(), 285278241);
    this.mKeyboardListeners = new ArrayList();
    setClipChildren(false);
    setClipToPadding(false);
    this.mPreviewText = ((TextView)findViewById(285802639));
    this.mLetterBoard = ((FrameLayout)findViewById(285802610));
    this.mLetterBoard.setVisibility(0);
    this.mBtnCapsLock = findViewById(285802537);
    this.mBtnLetterDelete = findViewById(285802539);
    this.mBtnToSymbolBoard = findViewById(285802543);
    this.mBtnLetterSpace = findViewById(285802541);
    this.mBtnLetterOK = findViewById(285802540);
    this.mSymbolBoard = ((FrameLayout)findViewById(285802613));
    this.mSymbolBoard.setVisibility(4);
    this.mBtnSymbolDelete = findViewById(285802544);
    this.mBtnToLetterBoard = findViewById(285802542);
    this.mBtnSymbolSpace = findViewById(285802546);
    this.mBtnSymbolOK = findViewById(285802545);
    setOnTouchAndClickListenerForKey(this.mLetterBoard);
    setOnTouchAndClickListenerForKey(this.mSymbolBoard);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt1 = paramInt3 - paramInt1;
    int i = (int)((paramInt1 - this.mPaddingLeft * 2) / SIZE_GROUP[0].length * 1 / 1.2F);
    paramInt3 = (int)(i * 0.2F);
    int j = (int)((paramInt4 - paramInt2 - this.mPaddingTop * 2) / SIZE_GROUP.length * 1 / 1.17F);
    int k = (int)(j * 0.2F);
    this.mLetterBoard.layout(0, 0, paramInt1, paramInt4 - paramInt2);
    this.mSymbolBoard.layout(0, 0, paramInt1, paramInt4 - paramInt2);
    keyboardOnLayout(this.mLetterBoard, paramInt1, i, paramInt3, j, k);
    keyboardOnLayout(this.mSymbolBoard, paramInt1, i, paramInt3, j, k);
    TextView localTextView = this.mPreviewText;
    paramInt2 = this.mPopupViewX;
    paramInt1 = this.mPopupViewY;
    localTextView.layout(paramInt2, paramInt1, this.mPopupViewWidth + paramInt2, this.mPopupViewHeight + paramInt1);
  }
  
  public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
  {
    if (isEnabled())
    {
      int i = paramMotionEvent.getAction();
      if (i != 0)
      {
        if ((i == 1) || (i == 3))
        {
          long l1 = 300L - (System.currentTimeMillis() - this.mShowPreviewLastTime);
          if (this.mIsShowingPreview)
          {
            paramMotionEvent = this.mConfirmHide;
            long l2 = 0L;
            if (l1 > 0L) {
              l2 = l1;
            }
            postDelayed(paramMotionEvent, l2);
          }
          if ((paramView == this.mBtnLetterDelete) || (paramView == this.mBtnSymbolDelete)) {
            removeCallbacks(this.mSendDeleteActionRunnable);
          }
        }
      }
      else
      {
        if (((paramView.getTag() instanceof String)) && (((String)paramView.getTag()).length() == 1)) {
          calcAndStartShowPreviewAnim(paramView);
        }
        if ((paramView == this.mBtnLetterDelete) || (paramView == this.mBtnSymbolDelete)) {
          postDelayed(this.mSendDeleteActionRunnable, 500L);
        }
      }
    }
    return false;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    super.onTouchEvent(paramMotionEvent);
    return true;
  }
  
  public void removeKeyboardListener(OnKeyboardActionListener paramOnKeyboardActionListener)
  {
    this.mKeyboardListeners.remove(paramOnKeyboardActionListener);
  }
  
  public void show()
  {
    this.mLetterBoard.setVisibility(4);
    this.mSymbolBoard.setVisibility(0);
    if (this.mIsUpperMode) {
      shiftLetterBoard();
    }
    startAnimation(this.mStretchFromBottonAnimation);
  }
  
  public static class KeyButton
    extends TextView
  {
    public KeyButton(Context paramContext)
    {
      super();
    }
    
    public KeyButton(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public KeyButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
      super(paramAttributeSet, paramInt);
    }
    
    public void layout(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      measure(View.MeasureSpec.makeMeasureSpec(paramInt3 - paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt4 - paramInt2, 1073741824));
      super.layout(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    protected void onFinishInflate()
    {
      if ((getTag() instanceof String)) {
        setText((String)getTag());
      }
      super.onFinishInflate();
    }
  }
  
  public static abstract interface OnKeyboardActionListener
  {
    public abstract void onKeyBoardDelete();
    
    public abstract void onKeyBoardOK();
    
    public abstract void onText(CharSequence paramCharSequence);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/view/MiuiKeyBoardView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */