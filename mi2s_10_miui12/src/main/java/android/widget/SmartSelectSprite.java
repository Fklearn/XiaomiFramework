package android.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.Op;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

final class SmartSelectSprite
{
  private static final int CORNER_DURATION = 50;
  private static final int EXPAND_DURATION = 300;
  static final Comparator<RectF> RECTANGLE_COMPARATOR = Comparator.comparingDouble(_..Lambda.SmartSelectSprite.c8eqlh2kO_X0luLU2BexwK921WA.INSTANCE).thenComparingDouble(_..Lambda.SmartSelectSprite.mdkXIT1_UNlJQMaziE_E815aIKE.INSTANCE);
  private Animator mActiveAnimator = null;
  private final Interpolator mCornerInterpolator;
  private Drawable mExistingDrawable = null;
  private RectangleList mExistingRectangleList = null;
  private final Interpolator mExpandInterpolator;
  private final int mFillColor;
  private final Runnable mInvalidator;
  
  SmartSelectSprite(Context paramContext, int paramInt, Runnable paramRunnable)
  {
    this.mExpandInterpolator = AnimationUtils.loadInterpolator(paramContext, 17563661);
    this.mCornerInterpolator = AnimationUtils.loadInterpolator(paramContext, 17563663);
    this.mFillColor = paramInt;
    this.mInvalidator = ((Runnable)Preconditions.checkNotNull(paramRunnable));
  }
  
  private static boolean contains(RectF paramRectF, PointF paramPointF)
  {
    float f1 = paramPointF.x;
    float f2 = paramPointF.y;
    boolean bool;
    if ((f1 >= paramRectF.left) && (f1 <= paramRectF.right) && (f2 >= paramRectF.top) && (f2 <= paramRectF.bottom)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private Animator createAnimator(RectangleList paramRectangleList, float paramFloat1, float paramFloat2, List<Animator> paramList, ValueAnimator.AnimatorUpdateListener paramAnimatorUpdateListener, Runnable paramRunnable)
  {
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(paramRectangleList, "rightBoundary", new float[] { paramFloat2, paramRectangleList.getTotalWidth() });
    paramRectangleList = ObjectAnimator.ofFloat(paramRectangleList, "leftBoundary", new float[] { paramFloat1, 0.0F });
    localObjectAnimator.setDuration(300L);
    paramRectangleList.setDuration(300L);
    localObjectAnimator.addUpdateListener(paramAnimatorUpdateListener);
    paramRectangleList.addUpdateListener(paramAnimatorUpdateListener);
    localObjectAnimator.setInterpolator(this.mExpandInterpolator);
    paramRectangleList.setInterpolator(this.mExpandInterpolator);
    paramAnimatorUpdateListener = new AnimatorSet();
    paramAnimatorUpdateListener.playTogether(paramList);
    paramList = new AnimatorSet();
    paramList.playTogether(new Animator[] { paramRectangleList, localObjectAnimator });
    paramRectangleList = new AnimatorSet();
    paramRectangleList.playSequentially(new Animator[] { paramList, paramAnimatorUpdateListener });
    setUpAnimatorListener(paramRectangleList, paramRunnable);
    return paramRectangleList;
  }
  
  private ObjectAnimator createCornerAnimator(RoundedRectangleShape paramRoundedRectangleShape, ValueAnimator.AnimatorUpdateListener paramAnimatorUpdateListener)
  {
    paramRoundedRectangleShape = ObjectAnimator.ofFloat(paramRoundedRectangleShape, "roundRatio", new float[] { paramRoundedRectangleShape.getRoundRatio(), 0.0F });
    paramRoundedRectangleShape.setDuration(50L);
    paramRoundedRectangleShape.addUpdateListener(paramAnimatorUpdateListener);
    paramRoundedRectangleShape.setInterpolator(this.mCornerInterpolator);
    return paramRoundedRectangleShape;
  }
  
  private static int[] generateDirections(RectangleWithTextSelectionLayout paramRectangleWithTextSelectionLayout, List<RectangleWithTextSelectionLayout> paramList)
  {
    int[] arrayOfInt = new int[paramList.size()];
    int i = paramList.indexOf(paramRectangleWithTextSelectionLayout);
    for (int j = 0; j < i - 1; j++) {
      arrayOfInt[j] = -1;
    }
    if (paramList.size() == 1) {
      arrayOfInt[i] = 0;
    } else if (i == 0) {
      arrayOfInt[i] = -1;
    } else if (i == paramList.size() - 1) {
      arrayOfInt[i] = 1;
    } else {
      arrayOfInt[i] = 0;
    }
    for (j = i + 1; j < arrayOfInt.length; j++) {
      arrayOfInt[j] = 1;
    }
    return arrayOfInt;
  }
  
  private void removeExistingDrawables()
  {
    this.mExistingDrawable = null;
    this.mExistingRectangleList = null;
    this.mInvalidator.run();
  }
  
  private void setUpAnimatorListener(Animator paramAnimator, final Runnable paramRunnable)
  {
    paramAnimator.addListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator) {}
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        SmartSelectSprite.this.mExistingRectangleList.setDisplayType(1);
        SmartSelectSprite.this.mInvalidator.run();
        paramRunnable.run();
      }
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator) {}
    });
  }
  
  public void cancelAnimation()
  {
    Animator localAnimator = this.mActiveAnimator;
    if (localAnimator != null)
    {
      localAnimator.cancel();
      this.mActiveAnimator = null;
      removeExistingDrawables();
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    Drawable localDrawable = this.mExistingDrawable;
    if (localDrawable != null) {
      localDrawable.draw(paramCanvas);
    }
  }
  
  public boolean isAnimationActive()
  {
    Animator localAnimator = this.mActiveAnimator;
    boolean bool;
    if ((localAnimator != null) && (localAnimator.isRunning())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void startAnimation(PointF paramPointF, List<RectangleWithTextSelectionLayout> paramList, Runnable paramRunnable)
  {
    cancelAnimation();
    _..Lambda.SmartSelectSprite.2pck5xTffRWoiD4l_tkO_IIf5iM local2pck5xTffRWoiD4l_tkO_IIf5iM = new _..Lambda.SmartSelectSprite.2pck5xTffRWoiD4l_tkO_IIf5iM(this);
    int i = paramList.size();
    ArrayList localArrayList1 = new ArrayList(i);
    ArrayList localArrayList2 = new ArrayList(i);
    int j = 0;
    Object localObject1 = paramList.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (RectangleWithTextSelectionLayout)((Iterator)localObject1).next();
      RectF localRectF = ((RectangleWithTextSelectionLayout)localObject2).getRectangle();
      if (contains(localRectF, paramPointF)) {
        break label114;
      }
      j = (int)(j + localRectF.width());
    }
    Object localObject2 = null;
    label114:
    if (localObject2 != null)
    {
      int k = (int)(j + (paramPointF.x - ((RectangleWithTextSelectionLayout)localObject2).getRectangle().left));
      paramPointF = generateDirections((RectangleWithTextSelectionLayout)localObject2, paramList);
      for (j = 0; j < i; j++)
      {
        localObject2 = (RectangleWithTextSelectionLayout)paramList.get(j);
        localObject1 = ((RectangleWithTextSelectionLayout)localObject2).getRectangle();
        int m = paramPointF[j];
        boolean bool;
        if (((RectangleWithTextSelectionLayout)localObject2).getTextSelectionLayout() == 0) {
          bool = true;
        } else {
          bool = false;
        }
        localObject2 = new RoundedRectangleShape((RectF)localObject1, m, bool, null);
        localArrayList2.add(createCornerAnimator((RoundedRectangleShape)localObject2, local2pck5xTffRWoiD4l_tkO_IIf5iM));
        localArrayList1.add(localObject2);
      }
      paramPointF = new RectangleList(localArrayList1, null);
      paramList = new ShapeDrawable(paramPointF);
      localObject2 = paramList.getPaint();
      ((Paint)localObject2).setColor(this.mFillColor);
      ((Paint)localObject2).setStyle(Paint.Style.FILL);
      this.mExistingRectangleList = paramPointF;
      this.mExistingDrawable = paramList;
      this.mActiveAnimator = createAnimator(paramPointF, k, k, localArrayList2, local2pck5xTffRWoiD4l_tkO_IIf5iM, paramRunnable);
      this.mActiveAnimator.start();
      return;
    }
    throw new IllegalArgumentException("Center point is not inside any of the rectangles!");
  }
  
  private static final class RectangleList
    extends Shape
  {
    private static final String PROPERTY_LEFT_BOUNDARY = "leftBoundary";
    private static final String PROPERTY_RIGHT_BOUNDARY = "rightBoundary";
    private int mDisplayType = 0;
    private final Path mOutlinePolygonPath;
    private final List<SmartSelectSprite.RoundedRectangleShape> mRectangles;
    private final List<SmartSelectSprite.RoundedRectangleShape> mReversedRectangles;
    
    private RectangleList(List<SmartSelectSprite.RoundedRectangleShape> paramList)
    {
      this.mRectangles = new ArrayList(paramList);
      this.mReversedRectangles = new ArrayList(paramList);
      Collections.reverse(this.mReversedRectangles);
      this.mOutlinePolygonPath = generateOutlinePolygonPath(paramList);
    }
    
    private void drawPolygon(Canvas paramCanvas, Paint paramPaint)
    {
      paramCanvas.drawPath(this.mOutlinePolygonPath, paramPaint);
    }
    
    private void drawRectangles(Canvas paramCanvas, Paint paramPaint)
    {
      Iterator localIterator = this.mRectangles.iterator();
      while (localIterator.hasNext()) {
        ((SmartSelectSprite.RoundedRectangleShape)localIterator.next()).draw(paramCanvas, paramPaint);
      }
    }
    
    private static Path generateOutlinePolygonPath(List<SmartSelectSprite.RoundedRectangleShape> paramList)
    {
      Path localPath1 = new Path();
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        paramList = (SmartSelectSprite.RoundedRectangleShape)localIterator.next();
        Path localPath2 = new Path();
        localPath2.addRect(SmartSelectSprite.RoundedRectangleShape.access$300(paramList), Path.Direction.CW);
        localPath1.op(localPath2, Path.Op.UNION);
      }
      return localPath1;
    }
    
    private int getTotalWidth()
    {
      int i = 0;
      Iterator localIterator = this.mRectangles.iterator();
      while (localIterator.hasNext())
      {
        SmartSelectSprite.RoundedRectangleShape localRoundedRectangleShape = (SmartSelectSprite.RoundedRectangleShape)localIterator.next();
        i = (int)(i + SmartSelectSprite.RoundedRectangleShape.access$000(localRoundedRectangleShape));
      }
      return i;
    }
    
    private void setLeftBoundary(float paramFloat)
    {
      float f1 = getTotalWidth();
      Iterator localIterator = this.mReversedRectangles.iterator();
      while (localIterator.hasNext())
      {
        SmartSelectSprite.RoundedRectangleShape localRoundedRectangleShape = (SmartSelectSprite.RoundedRectangleShape)localIterator.next();
        float f2 = f1 - SmartSelectSprite.RoundedRectangleShape.access$000(localRoundedRectangleShape);
        if (paramFloat < f2) {
          SmartSelectSprite.RoundedRectangleShape.access$100(localRoundedRectangleShape, 0.0F);
        } else if (paramFloat > f1) {
          SmartSelectSprite.RoundedRectangleShape.access$100(localRoundedRectangleShape, SmartSelectSprite.RoundedRectangleShape.access$000(localRoundedRectangleShape));
        } else {
          SmartSelectSprite.RoundedRectangleShape.access$100(localRoundedRectangleShape, SmartSelectSprite.RoundedRectangleShape.access$000(localRoundedRectangleShape) - f1 + paramFloat);
        }
        f1 = f2;
      }
    }
    
    private void setRightBoundary(float paramFloat)
    {
      float f1 = 0.0F;
      Iterator localIterator = this.mRectangles.iterator();
      while (localIterator.hasNext())
      {
        SmartSelectSprite.RoundedRectangleShape localRoundedRectangleShape = (SmartSelectSprite.RoundedRectangleShape)localIterator.next();
        float f2 = SmartSelectSprite.RoundedRectangleShape.access$000(localRoundedRectangleShape) + f1;
        if (f2 < paramFloat) {
          SmartSelectSprite.RoundedRectangleShape.access$200(localRoundedRectangleShape, SmartSelectSprite.RoundedRectangleShape.access$000(localRoundedRectangleShape));
        } else if (f1 > paramFloat) {
          SmartSelectSprite.RoundedRectangleShape.access$200(localRoundedRectangleShape, 0.0F);
        } else {
          SmartSelectSprite.RoundedRectangleShape.access$200(localRoundedRectangleShape, paramFloat - f1);
        }
        f1 = f2;
      }
    }
    
    public void draw(Canvas paramCanvas, Paint paramPaint)
    {
      if (this.mDisplayType == 1) {
        drawPolygon(paramCanvas, paramPaint);
      } else {
        drawRectangles(paramCanvas, paramPaint);
      }
    }
    
    void setDisplayType(int paramInt)
    {
      this.mDisplayType = paramInt;
    }
    
    @Retention(RetentionPolicy.SOURCE)
    private static @interface DisplayType
    {
      public static final int POLYGON = 1;
      public static final int RECTANGLES = 0;
    }
  }
  
  static final class RectangleWithTextSelectionLayout
  {
    private final RectF mRectangle;
    private final int mTextSelectionLayout;
    
    RectangleWithTextSelectionLayout(RectF paramRectF, int paramInt)
    {
      this.mRectangle = ((RectF)Preconditions.checkNotNull(paramRectF));
      this.mTextSelectionLayout = paramInt;
    }
    
    public RectF getRectangle()
    {
      return this.mRectangle;
    }
    
    public int getTextSelectionLayout()
    {
      return this.mTextSelectionLayout;
    }
  }
  
  private static final class RoundedRectangleShape
    extends Shape
  {
    private static final String PROPERTY_ROUND_RATIO = "roundRatio";
    private final RectF mBoundingRectangle;
    private final float mBoundingWidth;
    private final Path mClipPath = new Path();
    private final RectF mDrawRect = new RectF();
    private final int mExpansionDirection;
    private final boolean mInverted;
    private float mLeftBoundary = 0.0F;
    private float mRightBoundary = 0.0F;
    private float mRoundRatio = 1.0F;
    
    private RoundedRectangleShape(RectF paramRectF, int paramInt, boolean paramBoolean)
    {
      this.mBoundingRectangle = new RectF(paramRectF);
      this.mBoundingWidth = paramRectF.width();
      boolean bool;
      if ((paramBoolean) && (paramInt != 0)) {
        bool = true;
      } else {
        bool = false;
      }
      this.mInverted = bool;
      if (paramBoolean) {
        this.mExpansionDirection = invert(paramInt);
      } else {
        this.mExpansionDirection = paramInt;
      }
      if (paramRectF.height() > paramRectF.width()) {
        setRoundRatio(0.0F);
      } else {
        setRoundRatio(1.0F);
      }
    }
    
    private float getAdjustedCornerRadius()
    {
      return getCornerRadius() * this.mRoundRatio;
    }
    
    private float getBoundingWidth()
    {
      return (int)(this.mBoundingRectangle.width() + getCornerRadius());
    }
    
    private float getCornerRadius()
    {
      return Math.min(this.mBoundingRectangle.width(), this.mBoundingRectangle.height());
    }
    
    private static int invert(int paramInt)
    {
      return paramInt * -1;
    }
    
    private void setEndBoundary(float paramFloat)
    {
      if (this.mInverted) {
        this.mLeftBoundary = (this.mBoundingWidth - paramFloat);
      } else {
        this.mRightBoundary = paramFloat;
      }
    }
    
    private void setStartBoundary(float paramFloat)
    {
      if (this.mInverted) {
        this.mRightBoundary = (this.mBoundingWidth - paramFloat);
      } else {
        this.mLeftBoundary = paramFloat;
      }
    }
    
    public void draw(Canvas paramCanvas, Paint paramPaint)
    {
      if (this.mLeftBoundary == this.mRightBoundary) {
        return;
      }
      float f1 = getCornerRadius();
      float f2 = getAdjustedCornerRadius();
      this.mDrawRect.set(this.mBoundingRectangle);
      this.mDrawRect.left = (this.mBoundingRectangle.left + this.mLeftBoundary - f1 / 2.0F);
      this.mDrawRect.right = (this.mBoundingRectangle.left + this.mRightBoundary + f1 / 2.0F);
      paramCanvas.save();
      this.mClipPath.reset();
      this.mClipPath.addRoundRect(this.mDrawRect, f2, f2, Path.Direction.CW);
      paramCanvas.clipPath(this.mClipPath);
      paramCanvas.drawRect(this.mBoundingRectangle, paramPaint);
      paramCanvas.restore();
    }
    
    float getRoundRatio()
    {
      return this.mRoundRatio;
    }
    
    void setRoundRatio(float paramFloat)
    {
      this.mRoundRatio = paramFloat;
    }
    
    @Retention(RetentionPolicy.SOURCE)
    private static @interface ExpansionDirection
    {
      public static final int CENTER = 0;
      public static final int LEFT = -1;
      public static final int RIGHT = 1;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/SmartSelectSprite.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */