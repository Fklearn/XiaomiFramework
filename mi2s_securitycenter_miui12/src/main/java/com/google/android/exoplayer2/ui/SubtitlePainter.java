package com.google.android.exoplayer2.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.google.android.exoplayer2.text.CaptionStyleCompat;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.util.Util;
import com.miui.gamebooster.globalgame.view.RoundedDrawable;

final class SubtitlePainter {
    private static final float INNER_PADDING_RATIO = 0.125f;
    private static final String TAG = "SubtitlePainter";
    private boolean applyEmbeddedFontSizes;
    private boolean applyEmbeddedStyles;
    private int backgroundColor;
    private Rect bitmapRect;
    private float bottomPaddingFraction;
    private final float cornerRadius;
    private Bitmap cueBitmap;
    private float cueBitmapHeight;
    private float cueLine;
    private int cueLineAnchor;
    private int cueLineType;
    private float cuePosition;
    private int cuePositionAnchor;
    private float cueSize;
    private CharSequence cueText;
    private Layout.Alignment cueTextAlignment;
    private int edgeColor;
    private int edgeType;
    private int foregroundColor;
    private final RectF lineBounds = new RectF();
    private final float outlineWidth;
    private final Paint paint;
    private int parentBottom;
    private int parentLeft;
    private int parentRight;
    private int parentTop;
    private final float shadowOffset;
    private final float shadowRadius;
    private final float spacingAdd;
    private final float spacingMult;
    private StaticLayout textLayout;
    private int textLeft;
    private int textPaddingX;
    private final TextPaint textPaint;
    private float textSizePx;
    private int textTop;
    private int windowColor;

    public SubtitlePainter(Context context) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes((AttributeSet) null, new int[]{16843287, 16843288}, 0, 0);
        this.spacingAdd = (float) obtainStyledAttributes.getDimensionPixelSize(0, 0);
        this.spacingMult = obtainStyledAttributes.getFloat(1, 1.0f);
        obtainStyledAttributes.recycle();
        float round = (float) Math.round((((float) context.getResources().getDisplayMetrics().densityDpi) * 2.0f) / 160.0f);
        this.cornerRadius = round;
        this.outlineWidth = round;
        this.shadowRadius = round;
        this.shadowOffset = round;
        this.textPaint = new TextPaint();
        this.textPaint.setAntiAlias(true);
        this.textPaint.setSubpixelText(true);
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Paint.Style.FILL);
    }

    private static boolean areCharSequencesEqual(CharSequence charSequence, CharSequence charSequence2) {
        return charSequence == charSequence2 || (charSequence != null && charSequence.equals(charSequence2));
    }

    private void drawBitmapLayout(Canvas canvas) {
        canvas.drawBitmap(this.cueBitmap, (Rect) null, this.bitmapRect, (Paint) null);
    }

    private void drawLayout(Canvas canvas, boolean z) {
        if (z) {
            drawTextLayout(canvas);
        } else {
            drawBitmapLayout(canvas);
        }
    }

    private void drawTextLayout(Canvas canvas) {
        StaticLayout staticLayout = this.textLayout;
        if (staticLayout != null) {
            int save = canvas.save();
            canvas.translate((float) this.textLeft, (float) this.textTop);
            if (Color.alpha(this.windowColor) > 0) {
                this.paint.setColor(this.windowColor);
                canvas.drawRect((float) (-this.textPaddingX), 0.0f, (float) (staticLayout.getWidth() + this.textPaddingX), (float) staticLayout.getHeight(), this.paint);
            }
            if (Color.alpha(this.backgroundColor) > 0) {
                this.paint.setColor(this.backgroundColor);
                int lineCount = staticLayout.getLineCount();
                float lineTop = (float) staticLayout.getLineTop(0);
                int i = 0;
                while (i < lineCount) {
                    float lineLeft = staticLayout.getLineLeft(i);
                    float lineRight = staticLayout.getLineRight(i);
                    RectF rectF = this.lineBounds;
                    int i2 = this.textPaddingX;
                    rectF.left = lineLeft - ((float) i2);
                    rectF.right = ((float) i2) + lineRight;
                    rectF.top = lineTop;
                    rectF.bottom = (float) staticLayout.getLineBottom(i);
                    RectF rectF2 = this.lineBounds;
                    float f = rectF2.bottom;
                    if (lineRight - lineLeft > 0.0f) {
                        float f2 = this.cornerRadius;
                        canvas.drawRoundRect(rectF2, f2, f2, this.paint);
                    }
                    i++;
                    lineTop = f;
                }
            }
            int i3 = this.edgeType;
            boolean z = true;
            if (i3 == 1) {
                this.textPaint.setStrokeJoin(Paint.Join.ROUND);
                this.textPaint.setStrokeWidth(this.outlineWidth);
                this.textPaint.setColor(this.edgeColor);
                this.textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                staticLayout.draw(canvas);
            } else if (i3 == 2) {
                TextPaint textPaint2 = this.textPaint;
                float f3 = this.shadowRadius;
                float f4 = this.shadowOffset;
                textPaint2.setShadowLayer(f3, f4, f4, this.edgeColor);
            } else if (i3 == 3 || i3 == 4) {
                if (this.edgeType != 3) {
                    z = false;
                }
                int i4 = -1;
                int i5 = z ? -1 : this.edgeColor;
                if (z) {
                    i4 = this.edgeColor;
                }
                float f5 = this.shadowRadius / 2.0f;
                this.textPaint.setColor(this.foregroundColor);
                this.textPaint.setStyle(Paint.Style.FILL);
                float f6 = -f5;
                this.textPaint.setShadowLayer(this.shadowRadius, f6, f6, i5);
                staticLayout.draw(canvas);
                this.textPaint.setShadowLayer(this.shadowRadius, f5, f5, i4);
            }
            this.textPaint.setColor(this.foregroundColor);
            this.textPaint.setStyle(Paint.Style.FILL);
            staticLayout.draw(canvas);
            this.textPaint.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
            canvas.restoreToCount(save);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0054  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setupBitmapLayout() {
        /*
            r7 = this;
            int r0 = r7.parentRight
            int r1 = r7.parentLeft
            int r0 = r0 - r1
            int r2 = r7.parentBottom
            int r3 = r7.parentTop
            int r2 = r2 - r3
            float r1 = (float) r1
            float r0 = (float) r0
            float r4 = r7.cuePosition
            float r4 = r4 * r0
            float r1 = r1 + r4
            float r3 = (float) r3
            float r2 = (float) r2
            float r4 = r7.cueLine
            float r4 = r4 * r2
            float r3 = r3 + r4
            float r4 = r7.cueSize
            float r0 = r0 * r4
            int r0 = java.lang.Math.round(r0)
            float r4 = r7.cueBitmapHeight
            r5 = 1
            int r5 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r5 == 0) goto L_0x0025
            goto L_0x0035
        L_0x0025:
            float r2 = (float) r0
            android.graphics.Bitmap r4 = r7.cueBitmap
            int r4 = r4.getHeight()
            float r4 = (float) r4
            android.graphics.Bitmap r5 = r7.cueBitmap
            int r5 = r5.getWidth()
            float r5 = (float) r5
            float r4 = r4 / r5
        L_0x0035:
            float r2 = r2 * r4
            int r2 = java.lang.Math.round(r2)
            int r4 = r7.cueLineAnchor
            r5 = 1
            r6 = 2
            if (r4 != r6) goto L_0x0043
            float r4 = (float) r0
        L_0x0041:
            float r1 = r1 - r4
            goto L_0x0049
        L_0x0043:
            if (r4 != r5) goto L_0x0049
            int r4 = r0 / 2
            float r4 = (float) r4
            goto L_0x0041
        L_0x0049:
            int r1 = java.lang.Math.round(r1)
            int r4 = r7.cuePositionAnchor
            if (r4 != r6) goto L_0x0054
            float r4 = (float) r2
        L_0x0052:
            float r3 = r3 - r4
            goto L_0x005a
        L_0x0054:
            if (r4 != r5) goto L_0x005a
            int r4 = r2 / 2
            float r4 = (float) r4
            goto L_0x0052
        L_0x005a:
            int r3 = java.lang.Math.round(r3)
            android.graphics.Rect r4 = new android.graphics.Rect
            int r0 = r0 + r1
            int r2 = r2 + r3
            r4.<init>(r1, r3, r0, r2)
            r7.bitmapRect = r4
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ui.SubtitlePainter.setupBitmapLayout():void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:58:0x014e  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0150  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x015c  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x015f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setupTextLayout() {
        /*
            r25 = this;
            r0 = r25
            int r1 = r0.parentRight
            int r2 = r0.parentLeft
            int r1 = r1 - r2
            int r2 = r0.parentBottom
            int r3 = r0.parentTop
            int r2 = r2 - r3
            android.text.TextPaint r3 = r0.textPaint
            float r4 = r0.textSizePx
            r3.setTextSize(r4)
            float r3 = r0.textSizePx
            r4 = 1040187392(0x3e000000, float:0.125)
            float r3 = r3 * r4
            r4 = 1056964608(0x3f000000, float:0.5)
            float r3 = r3 + r4
            int r3 = (int) r3
            int r4 = r3 * 2
            int r5 = r1 - r4
            float r6 = r0.cueSize
            r7 = 1
            int r8 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r8 == 0) goto L_0x002a
            float r5 = (float) r5
            float r5 = r5 * r6
            int r5 = (int) r5
        L_0x002a:
            java.lang.String r6 = "SubtitlePainter"
            if (r5 > 0) goto L_0x0034
            java.lang.String r1 = "Skipped drawing subtitle cue (insufficient space)"
            android.util.Log.w(r6, r1)
            return
        L_0x0034:
            boolean r8 = r0.applyEmbeddedFontSizes
            r15 = 0
            if (r8 == 0) goto L_0x0040
            boolean r8 = r0.applyEmbeddedStyles
            if (r8 == 0) goto L_0x0040
            java.lang.CharSequence r8 = r0.cueText
            goto L_0x004a
        L_0x0040:
            boolean r8 = r0.applyEmbeddedStyles
            if (r8 != 0) goto L_0x004d
            java.lang.CharSequence r8 = r0.cueText
            java.lang.String r8 = r8.toString()
        L_0x004a:
            r17 = r8
            goto L_0x0080
        L_0x004d:
            android.text.SpannableStringBuilder r8 = new android.text.SpannableStringBuilder
            java.lang.CharSequence r9 = r0.cueText
            r8.<init>(r9)
            int r9 = r8.length()
            java.lang.Class<android.text.style.AbsoluteSizeSpan> r10 = android.text.style.AbsoluteSizeSpan.class
            java.lang.Object[] r10 = r8.getSpans(r15, r9, r10)
            android.text.style.AbsoluteSizeSpan[] r10 = (android.text.style.AbsoluteSizeSpan[]) r10
            java.lang.Class<android.text.style.RelativeSizeSpan> r11 = android.text.style.RelativeSizeSpan.class
            java.lang.Object[] r9 = r8.getSpans(r15, r9, r11)
            android.text.style.RelativeSizeSpan[] r9 = (android.text.style.RelativeSizeSpan[]) r9
            int r11 = r10.length
            r12 = r15
        L_0x006a:
            if (r12 >= r11) goto L_0x0074
            r13 = r10[r12]
            r8.removeSpan(r13)
            int r12 = r12 + 1
            goto L_0x006a
        L_0x0074:
            int r10 = r9.length
            r11 = r15
        L_0x0076:
            if (r11 >= r10) goto L_0x004a
            r12 = r9[r11]
            r8.removeSpan(r12)
            int r11 = r11 + 1
            goto L_0x0076
        L_0x0080:
            android.text.Layout$Alignment r8 = r0.cueTextAlignment
            if (r8 != 0) goto L_0x0086
            android.text.Layout$Alignment r8 = android.text.Layout.Alignment.ALIGN_CENTER
        L_0x0086:
            r20 = r8
            android.text.StaticLayout r14 = new android.text.StaticLayout
            android.text.TextPaint r10 = r0.textPaint
            float r13 = r0.spacingMult
            float r12 = r0.spacingAdd
            r16 = 1
            r8 = r14
            r9 = r17
            r11 = r5
            r18 = r12
            r12 = r20
            r7 = r14
            r14 = r18
            r24 = r3
            r3 = r15
            r15 = r16
            r8.<init>(r9, r10, r11, r12, r13, r14, r15)
            r0.textLayout = r7
            android.text.StaticLayout r7 = r0.textLayout
            int r7 = r7.getHeight()
            android.text.StaticLayout r8 = r0.textLayout
            int r8 = r8.getLineCount()
            r9 = r3
            r10 = r9
        L_0x00b5:
            if (r9 >= r8) goto L_0x00ca
            android.text.StaticLayout r11 = r0.textLayout
            float r11 = r11.getLineWidth(r9)
            double r11 = (double) r11
            double r11 = java.lang.Math.ceil(r11)
            int r11 = (int) r11
            int r10 = java.lang.Math.max(r11, r10)
            int r9 = r9 + 1
            goto L_0x00b5
        L_0x00ca:
            float r8 = r0.cueSize
            r9 = 1
            int r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r8 == 0) goto L_0x00d4
            if (r10 >= r5) goto L_0x00d4
            goto L_0x00d5
        L_0x00d4:
            r5 = r10
        L_0x00d5:
            int r5 = r5 + r4
            float r4 = r0.cuePosition
            int r8 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            r9 = 1
            r10 = 2
            if (r8 == 0) goto L_0x0101
            float r1 = (float) r1
            float r1 = r1 * r4
            int r1 = java.lang.Math.round(r1)
            int r4 = r0.parentLeft
            int r1 = r1 + r4
            int r4 = r0.cuePositionAnchor
            if (r4 != r10) goto L_0x00ed
            int r1 = r1 - r5
            goto L_0x00f3
        L_0x00ed:
            if (r4 != r9) goto L_0x00f3
            int r1 = r1 * 2
            int r1 = r1 - r5
            int r1 = r1 / r10
        L_0x00f3:
            int r4 = r0.parentLeft
            int r1 = java.lang.Math.max(r1, r4)
            int r5 = r5 + r1
            int r4 = r0.parentRight
            int r4 = java.lang.Math.min(r5, r4)
            goto L_0x0105
        L_0x0101:
            int r1 = r1 - r5
            int r1 = r1 / r10
            int r4 = r1 + r5
        L_0x0105:
            int r4 = r4 - r1
            if (r4 > 0) goto L_0x010e
            java.lang.String r1 = "Skipped drawing subtitle cue (invalid horizontal positioning)"
            android.util.Log.w(r6, r1)
            return
        L_0x010e:
            float r5 = r0.cueLine
            r6 = 1
            int r6 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r6 == 0) goto L_0x0165
            int r6 = r0.cueLineType
            if (r6 != 0) goto L_0x0123
            float r2 = (float) r2
            float r2 = r2 * r5
            int r2 = java.lang.Math.round(r2)
        L_0x011f:
            int r3 = r0.parentTop
        L_0x0121:
            int r2 = r2 + r3
            goto L_0x014a
        L_0x0123:
            android.text.StaticLayout r2 = r0.textLayout
            int r2 = r2.getLineBottom(r3)
            android.text.StaticLayout r5 = r0.textLayout
            int r3 = r5.getLineTop(r3)
            int r2 = r2 - r3
            float r3 = r0.cueLine
            r5 = 0
            int r5 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r5 < 0) goto L_0x013e
            float r2 = (float) r2
            float r3 = r3 * r2
            int r2 = java.lang.Math.round(r3)
            goto L_0x011f
        L_0x013e:
            r5 = 1065353216(0x3f800000, float:1.0)
            float r3 = r3 + r5
            float r2 = (float) r2
            float r3 = r3 * r2
            int r2 = java.lang.Math.round(r3)
            int r3 = r0.parentBottom
            goto L_0x0121
        L_0x014a:
            int r3 = r0.cueLineAnchor
            if (r3 != r10) goto L_0x0150
            int r2 = r2 - r7
            goto L_0x0156
        L_0x0150:
            if (r3 != r9) goto L_0x0156
            int r2 = r2 * 2
            int r2 = r2 - r7
            int r2 = r2 / r10
        L_0x0156:
            int r3 = r2 + r7
            int r5 = r0.parentBottom
            if (r3 <= r5) goto L_0x015f
            int r2 = r5 - r7
            goto L_0x016f
        L_0x015f:
            int r3 = r0.parentTop
            if (r2 >= r3) goto L_0x016f
            r2 = r3
            goto L_0x016f
        L_0x0165:
            int r3 = r0.parentBottom
            int r3 = r3 - r7
            float r2 = (float) r2
            float r5 = r0.bottomPaddingFraction
            float r2 = r2 * r5
            int r2 = (int) r2
            int r2 = r3 - r2
        L_0x016f:
            android.text.StaticLayout r3 = new android.text.StaticLayout
            android.text.TextPaint r5 = r0.textPaint
            float r6 = r0.spacingMult
            float r7 = r0.spacingAdd
            r23 = 1
            r16 = r3
            r18 = r5
            r19 = r4
            r21 = r6
            r22 = r7
            r16.<init>(r17, r18, r19, r20, r21, r22, r23)
            r0.textLayout = r3
            r0.textLeft = r1
            r0.textTop = r2
            r1 = r24
            r0.textPaddingX = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ui.SubtitlePainter.setupTextLayout():void");
    }

    public void draw(Cue cue, boolean z, boolean z2, CaptionStyleCompat captionStyleCompat, float f, float f2, Canvas canvas, int i, int i2, int i3, int i4) {
        boolean z3 = cue.bitmap == null;
        int i5 = RoundedDrawable.DEFAULT_BORDER_COLOR;
        if (z3) {
            if (!TextUtils.isEmpty(cue.text)) {
                i5 = (!cue.windowColorSet || !z) ? captionStyleCompat.windowColor : cue.windowColor;
            } else {
                return;
            }
        }
        if (areCharSequencesEqual(this.cueText, cue.text) && Util.areEqual(this.cueTextAlignment, cue.textAlignment) && this.cueBitmap == cue.bitmap && this.cueLine == cue.line && this.cueLineType == cue.lineType && Util.areEqual(Integer.valueOf(this.cueLineAnchor), Integer.valueOf(cue.lineAnchor)) && this.cuePosition == cue.position && Util.areEqual(Integer.valueOf(this.cuePositionAnchor), Integer.valueOf(cue.positionAnchor)) && this.cueSize == cue.size && this.cueBitmapHeight == cue.bitmapHeight && this.applyEmbeddedStyles == z && this.applyEmbeddedFontSizes == z2 && this.foregroundColor == captionStyleCompat.foregroundColor && this.backgroundColor == captionStyleCompat.backgroundColor && this.windowColor == i5 && this.edgeType == captionStyleCompat.edgeType && this.edgeColor == captionStyleCompat.edgeColor && Util.areEqual(this.textPaint.getTypeface(), captionStyleCompat.typeface) && this.textSizePx == f && this.bottomPaddingFraction == f2 && this.parentLeft == i && this.parentTop == i2 && this.parentRight == i3 && this.parentBottom == i4) {
            drawLayout(canvas, z3);
            return;
        }
        this.cueText = cue.text;
        this.cueTextAlignment = cue.textAlignment;
        this.cueBitmap = cue.bitmap;
        this.cueLine = cue.line;
        this.cueLineType = cue.lineType;
        this.cueLineAnchor = cue.lineAnchor;
        this.cuePosition = cue.position;
        this.cuePositionAnchor = cue.positionAnchor;
        this.cueSize = cue.size;
        this.cueBitmapHeight = cue.bitmapHeight;
        this.applyEmbeddedStyles = z;
        this.applyEmbeddedFontSizes = z2;
        this.foregroundColor = captionStyleCompat.foregroundColor;
        this.backgroundColor = captionStyleCompat.backgroundColor;
        this.windowColor = i5;
        this.edgeType = captionStyleCompat.edgeType;
        this.edgeColor = captionStyleCompat.edgeColor;
        this.textPaint.setTypeface(captionStyleCompat.typeface);
        this.textSizePx = f;
        this.bottomPaddingFraction = f2;
        this.parentLeft = i;
        this.parentTop = i2;
        this.parentRight = i3;
        this.parentBottom = i4;
        if (z3) {
            setupTextLayout();
        } else {
            setupBitmapLayout();
        }
        drawLayout(canvas, z3);
    }
}
