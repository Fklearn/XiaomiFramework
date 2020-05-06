package com.miui.maml.elements;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.data.VariableNames;
import com.miui.maml.data.Variables;
import com.miui.maml.util.ColorParser;
import com.miui.maml.util.Utils;
import org.w3c.dom.Element;

public class PaintScreenElement extends AnimatedScreenElement {
    private static float DEFAULT_WEIGHT = 1.0f;
    public static final String TAG_NAME = "Paint";
    private Bitmap mCachedBitmap;
    private Canvas mCachedCanvas;
    private Paint mCachedPaint;
    private int mColor;
    private ColorParser mColorParser;
    private Paint mPaint;
    private Path mPath = new Path();
    private boolean mPendingMouseUp;
    private float mWeight;
    private Expression mWeightExp;
    private Xfermode mXfermode;

    public PaintScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        load(element, screenElementRoot);
        DEFAULT_WEIGHT = scale((double) DEFAULT_WEIGHT);
        this.mWeight = DEFAULT_WEIGHT;
        this.mPaint = new Paint();
        this.mPaint.setXfermode(this.mXfermode);
        this.mPaint.setAntiAlias(true);
        this.mCachedPaint = new Paint();
        this.mCachedPaint.setStyle(Paint.Style.STROKE);
        this.mCachedPaint.setStrokeWidth(DEFAULT_WEIGHT);
        this.mCachedPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mCachedPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mCachedPaint.setAntiAlias(true);
        this.mTouchable = true;
    }

    private void load(Element element, ScreenElementRoot screenElementRoot) {
        if (element != null) {
            Variables variables = getVariables();
            this.mWeightExp = Expression.build(variables, element.getAttribute("weight"));
            this.mColorParser = ColorParser.fromElement(variables, element);
            this.mXfermode = new PorterDuffXfermode(Utils.getPorterDuffMode(element.getAttribute("xfermode")));
        }
    }

    /* access modifiers changed from: protected */
    public void doRender(Canvas canvas) {
        float width = getWidth();
        float height = getHeight();
        float left = getLeft(0.0f, width);
        float top = getTop(0.0f, height);
        float absoluteLeft = getAbsoluteLeft();
        float absoluteTop = getAbsoluteTop();
        if (this.mPendingMouseUp) {
            this.mCachedCanvas.save();
            this.mCachedCanvas.translate(-absoluteLeft, -absoluteTop);
            this.mCachedCanvas.drawPath(this.mPath, this.mCachedPaint);
            this.mCachedCanvas.restore();
            this.mPath.reset();
            this.mPendingMouseUp = false;
        }
        canvas.drawBitmap(this.mCachedBitmap, left, top, this.mPaint);
        if (this.mPressed) {
            float f = this.mWeight;
            if (f > 0.0f && this.mAlpha > 0) {
                this.mCachedPaint.setStrokeWidth(f);
                this.mCachedPaint.setColor(this.mColor);
                Paint paint = this.mCachedPaint;
                paint.setAlpha(Utils.mixAlpha(paint.getAlpha(), this.mAlpha));
                canvas.save();
                canvas.translate((-absoluteLeft) + left, (-absoluteTop) + top);
                Xfermode xfermode = this.mCachedPaint.getXfermode();
                this.mCachedPaint.setXfermode(this.mXfermode);
                canvas.drawPath(this.mPath, this.mCachedPaint);
                this.mCachedPaint.setXfermode(xfermode);
                canvas.restore();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void doTick(long j) {
        super.doTick(j);
        if (isVisible()) {
            Expression expression = this.mWeightExp;
            if (expression != null) {
                this.mWeight = scale(expression.evaluate());
            }
            this.mColor = this.mColorParser.getColor();
        }
    }

    public void finish() {
        super.finish();
        this.mCachedBitmap.recycle();
        this.mCachedBitmap = null;
        this.mCachedCanvas = null;
    }

    public void init() {
        super.init();
        float width = getWidth();
        if (width < 0.0f) {
            width = scale(Utils.getVariableNumber(VariableNames.SCREEN_WIDTH, getVariables()));
        }
        float height = getHeight();
        if (height < 0.0f) {
            height = scale(Utils.getVariableNumber(VariableNames.SCREEN_HEIGHT, getVariables()));
        }
        this.mCachedBitmap = Bitmap.createBitmap((int) Math.ceil((double) width), (int) Math.ceil((double) height), Bitmap.Config.ARGB_8888);
        this.mCachedBitmap.setDensity(this.mRoot.getTargetDensity());
        this.mCachedCanvas = new Canvas(this.mCachedBitmap);
    }

    /* access modifiers changed from: protected */
    public void onActionCancel() {
        this.mPendingMouseUp = true;
    }

    /* access modifiers changed from: protected */
    public void onActionDown(float f, float f2) {
        super.onActionDown(f, f2);
        this.mPath.reset();
        this.mPath.moveTo(f, f2);
    }

    /* access modifiers changed from: protected */
    public void onActionMove(float f, float f2) {
        super.onActionMove(f, f2);
        this.mPath.lineTo(f, f2);
    }

    /* access modifiers changed from: protected */
    public void onActionUp() {
        super.onActionUp();
        this.mPendingMouseUp = true;
    }

    public void reset(long j) {
        super.reset(j);
        this.mCachedCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        this.mPressed = false;
    }

    public void setColorFilter(ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
        Paint paint = this.mPaint;
        if (paint != null) {
            paint.setColorFilter(colorFilter);
        }
    }
}
