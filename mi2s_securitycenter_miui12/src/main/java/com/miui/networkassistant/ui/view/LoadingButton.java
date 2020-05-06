package com.miui.networkassistant.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

@SuppressLint({"AppCompatCustomView"})
public class LoadingButton extends TextView {
    /* access modifiers changed from: private */
    public static final int BACKGROUND_COLOR = Color.parseColor("#4CFFFFFF");
    private static final int BACKGROUND_COLOR_B = Color.parseColor("#2CFFFFFF");
    private static final int BACKGROUND_COLOR_P = Color.parseColor("#2CFFFFFF");
    private static final int LOADING_COLOR = Color.parseColor("#8CFFFFFF");
    private static final int POINT_A = 0;
    private static final int POINT_B = 100;
    private static final int POINT_C = 150;
    private static final int POINT_D = 200;
    private static final int POINT_E = 250;
    private static final int POINT_F = 300;
    private static final float SCALE_AX = 0.3659f;
    private static final float SCALE_AY = 0.4588f;
    private static final float SCALE_BX = 0.5041f;
    private static final float SCALE_BY = 0.6006f;
    private static final float SCALE_CX = 0.7553f;
    private static final float SCALE_CY = 0.3388f;
    private static final int TICK_WIDTH = 120;

    /* renamed from: d  reason: collision with root package name */
    private int f5660d = 6;
    private Bitmap dstBitmap;
    private boolean enable = true;
    /* access modifiers changed from: private */
    public Paint mBaseBgPaint;
    private int mHeight;
    /* access modifiers changed from: private */
    public int mProgress = 0;
    private Bitmap mTickBitmap;
    private int mTickOffsetX;
    private Paint mTickPaint;
    private int mWidth;
    private int r = (this.f5660d / 2);
    private Bitmap srcBitmap;
    /* access modifiers changed from: private */
    public String textTmp;
    private Bitmap xfermodeBitmap;

    public LoadingButton(Context context) {
        super(context);
        init();
    }

    public LoadingButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public LoadingButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public LoadingButton(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    private void createDstBitmap() {
        this.dstBitmap = Bitmap.createBitmap(this.mWidth, this.mHeight, Bitmap.Config.ARGB_8888);
        new Canvas(this.dstBitmap).drawRoundRect(0.0f, 0.0f, (float) this.mWidth, (float) this.mHeight, 109.0f, 109.0f, this.mBaseBgPaint);
    }

    private void createSrcBitmap() {
        this.srcBitmap = Bitmap.createBitmap(this.mWidth, this.mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.srcBitmap);
        Paint paint = new Paint();
        paint.setColor(LOADING_COLOR);
        canvas.drawRect(0.0f, 0.0f, ((float) (this.mWidth * this.mProgress)) / 100.0f, (float) this.mHeight, paint);
    }

    private void createXfermodeBitmap() {
        Bitmap createBitmap = Bitmap.createBitmap(this.mWidth, this.mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawBitmap(this.dstBitmap, 0.0f, 0.0f, (Paint) null);
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(this.srcBitmap, 0.0f, 0.0f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawBitmap(this.dstBitmap, 0.0f, 0.0f, paint);
        this.xfermodeBitmap = createBitmap;
    }

    private void init() {
        setGravity(17);
        this.mBaseBgPaint = new Paint();
        this.mBaseBgPaint.setStyle(Paint.Style.FILL);
        this.mBaseBgPaint.setColor(BACKGROUND_COLOR);
        this.mBaseBgPaint.setAntiAlias(true);
        this.mTickPaint = new Paint();
        this.mTickPaint.setColor(-1);
        this.mTickPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mTickPaint.setStrokeWidth(4.0f);
        this.mTickPaint.setAntiAlias(true);
    }

    private void initBackgroundView() {
        createDstBitmap();
    }

    private boolean isCancelEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        return x < 0.0f || x > ((float) this.mWidth) || y < 0.0f || y > ((float) this.mHeight);
    }

    /* access modifiers changed from: private */
    public void setColor(int i) {
        if (this.mProgress == 0 && this.mWidth != 0) {
            this.mBaseBgPaint.setColor(i);
            initBackgroundView();
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Bitmap bitmap;
        int width;
        Paint paint;
        int i;
        super.onDraw(canvas);
        int i2 = this.mProgress;
        if (i2 <= 0 || i2 > 100) {
            if (this.mProgress > 100) {
                this.mBaseBgPaint.setColor(BACKGROUND_COLOR);
                int i3 = this.mProgress;
                if (i3 <= POINT_C || i3 > POINT_D) {
                    int i4 = this.mProgress;
                    if (i4 <= POINT_D || i4 > POINT_E) {
                        int i5 = this.mProgress;
                        if (i5 > POINT_E && i5 <= POINT_F) {
                            paint = this.mBaseBgPaint;
                            i = (int) (114.0f - (((float) ((i5 - POINT_E) * 38)) / 50.0f));
                        }
                        createDstBitmap();
                    } else {
                        this.mBaseBgPaint.setAlpha(114);
                        createDstBitmap();
                    }
                } else {
                    paint = this.mBaseBgPaint;
                    i = (int) ((((float) ((i3 - POINT_C) * 38)) / 50.0f) + 76.0f);
                }
                paint.setAlpha(i);
                createDstBitmap();
            }
            bitmap = this.dstBitmap;
        } else {
            if (i2 >= 95) {
                this.mProgress = 100;
            }
            createSrcBitmap();
            createXfermodeBitmap();
            bitmap = this.xfermodeBitmap;
        }
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
        int i6 = this.mProgress;
        if (i6 > POINT_C && i6 <= POINT_D && (width = (int) (((float) (this.mTickBitmap.getWidth() * (this.mProgress - POINT_C))) / 50.0f)) > 0) {
            Bitmap bitmap2 = this.mTickBitmap;
            canvas.drawBitmap(Bitmap.createBitmap(bitmap2, 0, 0, width, bitmap2.getHeight()), (float) this.mTickOffsetX, 0.0f, (Paint) null);
        }
        int i7 = this.mProgress;
        if (i7 > POINT_D && i7 <= POINT_E) {
            canvas.drawBitmap(this.mTickBitmap, (float) this.mTickOffsetX, 0.0f, (Paint) null);
        }
        int i8 = this.mProgress;
        if (i8 > POINT_E && i8 <= POINT_F) {
            this.mTickPaint.setAlpha((int) ((1.0f - (((float) (i8 - POINT_E)) / 50.0f)) * 255.0f));
            canvas.drawBitmap(this.mTickBitmap, (float) this.mTickOffsetX, 0.0f, this.mTickPaint);
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mWidth = getWidth();
        this.mHeight = getHeight();
        initBackgroundView();
        this.mTickBitmap = Bitmap.createBitmap(120, this.mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.mTickBitmap);
        Path path = new Path();
        this.mTickOffsetX = (this.mWidth / 2) - 60;
        float f = (float) ((this.mHeight / 2) - 60);
        float f2 = 55.056f + f;
        path.moveTo(43.908f, ((float) this.f5660d) + f2);
        float f3 = 72.072f + f;
        path.lineTo(60.492004f, ((float) this.f5660d) + f3);
        int i5 = this.r;
        int i6 = this.f5660d;
        path.quadTo(((float) i5) + 60.492004f, ((float) i5) + f3 + ((float) i6), ((float) i6) + 60.492004f, ((float) i6) + f3);
        int i7 = this.f5660d;
        float f4 = f + 40.656002f;
        path.lineTo(((float) i7) + 90.636f, ((float) i7) + f4);
        int i8 = this.f5660d;
        path.quadTo(((float) i8) + 90.636f, ((float) this.r) + f4, 90.636f, f4 + ((float) i8));
        path.lineTo(((float) this.f5660d) + 60.492004f, f3);
        int i9 = this.r;
        path.quadTo(((float) i9) + 60.492004f, ((float) i9) + f3, 60.492004f, f3);
        int i10 = this.f5660d;
        path.lineTo(((float) i10) + 43.908f, ((float) i10) + f2);
        int i11 = this.r;
        path.quadTo(((float) i11) + 43.908f, ((float) i11) + f2, 43.908f, f2 + ((float) this.f5660d));
        canvas.drawPath(path, this.mTickPaint);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action != 0) {
            if (action != 1) {
                if (action == 3) {
                    setColor(BACKGROUND_COLOR);
                    return false;
                }
            } else if (isCancelEvent(motionEvent)) {
                motionEvent.setAction(3);
                return onTouchEvent(motionEvent);
            } else {
                performClick();
            }
        } else if (!this.enable) {
            return false;
        } else {
            setColor(BACKGROUND_COLOR_P);
        }
        return true;
    }

    public boolean performClick() {
        return super.performClick();
    }

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.enable = z;
        setColor(!this.enable ? BACKGROUND_COLOR_P : BACKGROUND_COLOR);
    }

    public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
        if (!TextUtils.equals(this.textTmp, charSequence.toString())) {
            int i = this.mProgress;
            if (i <= 0 || i >= POINT_C) {
                super.setText(charSequence, bufferType);
            } else {
                this.textTmp = charSequence.toString();
            }
        }
    }

    public void startAnim() {
        if (this.mProgress == 0) {
            setColor(BACKGROUND_COLOR_B);
            ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{0, POINT_F});
            ofInt.setDuration(2500);
            ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int unused = LoadingButton.this.mProgress = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                    if (LoadingButton.this.mProgress > 100 && LoadingButton.this.mProgress <= LoadingButton.POINT_C) {
                        LoadingButton loadingButton = LoadingButton.this;
                        loadingButton.setTextColor(loadingButton.getTextColors().withAlpha((int) ((1.0f - (((float) (LoadingButton.this.mProgress - 100)) / 50.0f)) * 255.0f)));
                    }
                    if (LoadingButton.this.mProgress > LoadingButton.POINT_E && LoadingButton.this.mProgress <= LoadingButton.POINT_F) {
                        if (!TextUtils.isEmpty(LoadingButton.this.textTmp)) {
                            LoadingButton loadingButton2 = LoadingButton.this;
                            LoadingButton.super.setText(loadingButton2.textTmp, TextView.BufferType.NORMAL);
                            String unused2 = LoadingButton.this.textTmp = null;
                        }
                        LoadingButton loadingButton3 = LoadingButton.this;
                        loadingButton3.setTextColor(loadingButton3.getTextColors().withAlpha((int) ((((float) (LoadingButton.this.mProgress - LoadingButton.POINT_E)) / 50.0f) * 255.0f)));
                    }
                    LoadingButton.this.invalidate();
                }
            });
            ofInt.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    LoadingButton loadingButton = LoadingButton.this;
                    loadingButton.setTextColor(loadingButton.getTextColors().withAlpha(255));
                    LoadingButton.this.mBaseBgPaint.setAlpha(255);
                    int unused = LoadingButton.this.mProgress = 0;
                    LoadingButton.this.setColor(LoadingButton.BACKGROUND_COLOR);
                    LoadingButton.this.invalidate();
                }
            });
            ofInt.start();
        }
    }
}
