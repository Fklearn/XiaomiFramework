package com.miui.maml.elements;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.ContextThemeWrapper;
import com.miui.gamebooster.globalgame.view.RoundedDrawable;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import com.miui.maml.elements.ScreenElement;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.maml.folme.AnimatedPropertyType;
import com.miui.maml.folme.AnimatedTarget;
import com.miui.maml.folme.PropertyWrapper;
import com.miui.maml.util.ColorParser;
import com.miui.maml.util.HideSdkDependencyUtils;
import com.miui.maml.util.ReflectionHelper;
import com.miui.maml.util.TextFormatter;
import com.miui.maml.util.Utils;
import d.a.g.C0575b;
import miui.R;
import org.w3c.dom.Element;

public class TextScreenElement extends AnimatedScreenElement {
    private static final String CRLF = "\n";
    private static final int DEFAULT_SIZE = 18;
    private static final String LOG_TAG = "TextScreenElement";
    private static final int MARQUEE_FRAMERATE = 45;
    private static final int PADDING = 50;
    private static final String PROPERTY_NAME_TEXT_COLOR = "textColor";
    private static final String PROPERTY_NAME_TEXT_SHADOW_COLOR = "textShadowColor";
    private static final String PROPERTY_NAME_TEXT_SIZE = "textSize";
    private static final String RAW_CRLF = "\\n";
    public static final String TAG_NAME = "Text";
    public static final AnimatedProperty.AnimatedColorProperty TEXT_COLOR = new AnimatedProperty.AnimatedColorProperty(PROPERTY_NAME_TEXT_COLOR) {
        public int getIntValue(AnimatedScreenElement animatedScreenElement) {
            return animatedScreenElement instanceof TextScreenElement ? (int) ((long) ((TextScreenElement) animatedScreenElement).mTextColorProperty.getValue()) : RoundedDrawable.DEFAULT_BORDER_COLOR;
        }

        public void setIntValue(AnimatedScreenElement animatedScreenElement, int i) {
            if (animatedScreenElement instanceof TextScreenElement) {
                ((TextScreenElement) animatedScreenElement).mTextColorProperty.setValue((double) i);
            }
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof TextScreenElement) {
                ((TextScreenElement) animatedScreenElement).mTextColorProperty.setVelocity((double) f);
            }
        }
    };
    public static final String TEXT_HEIGHT = "text_height";
    public static final AnimatedProperty.AnimatedColorProperty TEXT_SHADOW_COLOR = new AnimatedProperty.AnimatedColorProperty(PROPERTY_NAME_TEXT_SHADOW_COLOR) {
        public int getIntValue(AnimatedScreenElement animatedScreenElement) {
            return animatedScreenElement instanceof TextScreenElement ? (int) ((long) ((TextScreenElement) animatedScreenElement).mTextShadowColorProperty.getValue()) : RoundedDrawable.DEFAULT_BORDER_COLOR;
        }

        public void setIntValue(AnimatedScreenElement animatedScreenElement, int i) {
            if (animatedScreenElement instanceof TextScreenElement) {
                ((TextScreenElement) animatedScreenElement).mTextShadowColorProperty.setValue((double) i);
            }
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof TextScreenElement) {
                ((TextScreenElement) animatedScreenElement).mTextShadowColorProperty.setVelocity((double) f);
            }
        }
    };
    public static final AnimatedProperty TEXT_SIZE = new AnimatedProperty(PROPERTY_NAME_TEXT_SIZE) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            if (animatedScreenElement instanceof TextScreenElement) {
                return (float) ((TextScreenElement) animatedScreenElement).mTextSizeProperty.getValue();
            }
            return 18.0f;
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof TextScreenElement) {
                ((TextScreenElement) animatedScreenElement).mTextSizeProperty.setValue((double) f);
            }
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof TextScreenElement) {
                ((TextScreenElement) animatedScreenElement).mTextSizeProperty.setVelocity((double) f);
            }
        }
    };
    public static final String TEXT_WIDTH = "text_width";
    private static final Object mLock = new Object();
    private ColorParser mColorParser;
    private boolean mFontScaleEnabled;
    protected TextFormatter mFormatter;
    private float mLayoutWidth;
    private int mMarqueeGap;
    private float mMarqueePos = Float.MAX_VALUE;
    private int mMarqueeSpeed;
    private boolean mMultiLine;
    private TextPaint mPaint = new TextPaint();
    private long mPreviousTime;
    private String mSetText;
    private ColorParser mShadowColorParser;
    private float mShadowDx;
    private float mShadowDy;
    private float mShadowRadius;
    private boolean mShouldMarquee;
    private float mSpacingAdd;
    private float mSpacingMult;
    private String mText;
    /* access modifiers changed from: private */
    public PropertyWrapper mTextColorProperty;
    private float mTextHeight;
    private IndexedVariable mTextHeightVar;
    private StaticLayout mTextLayout;
    /* access modifiers changed from: private */
    public PropertyWrapper mTextShadowColorProperty;
    private float mTextSize = scale(18.0d);
    /* access modifiers changed from: private */
    public PropertyWrapper mTextSizeProperty;
    private float mTextWidth;
    private IndexedVariable mTextWidthVar;

    /* renamed from: com.miui.maml.elements.TextScreenElement$4  reason: invalid class name */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$elements$ScreenElement$Align = new int[ScreenElement.Align.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|8) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        static {
            /*
                com.miui.maml.elements.ScreenElement$Align[] r0 = com.miui.maml.elements.ScreenElement.Align.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$elements$ScreenElement$Align = r0
                int[] r0 = $SwitchMap$com$miui$maml$elements$ScreenElement$Align     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.elements.ScreenElement$Align r1 = com.miui.maml.elements.ScreenElement.Align.LEFT     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$maml$elements$ScreenElement$Align     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.elements.ScreenElement$Align r1 = com.miui.maml.elements.ScreenElement.Align.CENTER     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$maml$elements$ScreenElement$Align     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.maml.elements.ScreenElement$Align r1 = com.miui.maml.elements.ScreenElement.Align.RIGHT     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.TextScreenElement.AnonymousClass4.<clinit>():void");
        }
    }

    static {
        AnimatedProperty.sPropertyNameMap.put(PROPERTY_NAME_TEXT_COLOR, TEXT_COLOR);
        AnimatedTarget.sPropertyMap.put(1003, TEXT_COLOR);
        AnimatedTarget.sPropertyTypeMap.put(TEXT_COLOR, 1003);
        AnimatedProperty.sPropertyNameMap.put(PROPERTY_NAME_TEXT_SIZE, TEXT_SIZE);
        AnimatedTarget.sPropertyMap.put(1002, TEXT_SIZE);
        AnimatedTarget.sPropertyTypeMap.put(TEXT_SIZE, 1002);
        AnimatedProperty.sPropertyNameMap.put(PROPERTY_NAME_TEXT_SHADOW_COLOR, TEXT_SHADOW_COLOR);
        ArrayMap<Integer, C0575b> arrayMap = AnimatedTarget.sPropertyMap;
        Integer valueOf = Integer.valueOf(AnimatedPropertyType.TEXT_SHADOW_COLOR);
        arrayMap.put(valueOf, TEXT_SHADOW_COLOR);
        AnimatedTarget.sPropertyTypeMap.put(TEXT_SHADOW_COLOR, valueOf);
    }

    public TextScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        load(element);
    }

    private Layout.Alignment getAlignment() {
        String str;
        StringBuilder sb;
        Layout.Alignment alignment;
        Layout.Alignment alignment2 = Layout.Alignment.ALIGN_NORMAL;
        int i = AnonymousClass4.$SwitchMap$com$miui$maml$elements$ScreenElement$Align[this.mAlign.ordinal()];
        if (i == 1) {
            try {
                alignment = (Layout.Alignment) ReflectionHelper.getFieldValue(alignment2.getClass(), alignment2, "ALIGN_LEFT");
            } catch (Exception e) {
                e = e;
                sb = new StringBuilder();
                str = "Invoke | getAlignment_ALIGN_LEFT occur EXCEPTION: ";
                sb.append(str);
                sb.append(e.getMessage());
                Log.e(LOG_TAG, sb.toString());
                return alignment2;
            }
        } else if (i == 2) {
            return Layout.Alignment.ALIGN_CENTER;
        } else {
            if (i != 3) {
                return alignment2;
            }
            try {
                alignment = (Layout.Alignment) ReflectionHelper.getFieldValue(alignment2.getClass(), alignment2, "ALIGN_RIGHT");
            } catch (Exception e2) {
                e = e2;
                sb = new StringBuilder();
                str = "Invoke | getAlignment_ALIGN_RIGHT occur EXCEPTION: ";
            }
        }
        return alignment;
    }

    private void load(Element element) {
        Element element2 = element;
        if (element2 != null) {
            Variables variables = getVariables();
            if (this.mHasName) {
                this.mTextWidthVar = new IndexedVariable(this.mName + "." + TEXT_WIDTH, variables, true);
                this.mTextHeightVar = new IndexedVariable(this.mName + "." + TEXT_HEIGHT, variables, true);
            }
            this.mFormatter = TextFormatter.fromElement(variables, element2, this.mStyle);
            this.mColorParser = ColorParser.fromElement(variables, element2, this.mStyle);
            this.mMarqueeSpeed = getAttrAsInt(element2, "marqueeSpeed", 0);
            this.mSpacingMult = getAttrAsFloat(element2, "spacingMult", 1.0f);
            this.mSpacingAdd = getAttrAsFloat(element2, "spacingAdd", 0.0f);
            this.mMarqueeGap = getAttrAsInt(element2, "marqueeGap", 2);
            this.mMultiLine = Boolean.parseBoolean(getAttr(element2, "multiLine"));
            this.mFontScaleEnabled = Boolean.parseBoolean(getAttr(element2, "enableFontScale"));
            Expression build = Expression.build(variables, getAttr(element2, "size"));
            String attr = getAttr(element2, TtmlNode.ATTR_TTS_FONT_FAMILY);
            String attr2 = getAttr(element2, "fontPath");
            if (!TextUtils.isEmpty(attr)) {
                this.mPaint.setTypeface(Typeface.create(attr, parseFontStyle(getAttr(element2, TtmlNode.ATTR_TTS_FONT_STYLE))));
            } else if (!TextUtils.isEmpty(attr2)) {
                Typeface typeface = null;
                try {
                    typeface = Typeface.createFromAsset(getContext().mContext.getAssets(), attr2);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "create typeface from asset fail :" + e);
                }
                if (typeface != null) {
                    this.mPaint.setTypeface(typeface);
                }
            } else {
                this.mPaint.setFakeBoldText(Boolean.parseBoolean(getAttr(element2, TtmlNode.BOLD)));
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext().mContext, R.style.Theme_Light);
                TextPaint textPaint = this.mPaint;
                textPaint.setTypeface(HideSdkDependencyUtils.TypefaceUtils_replaceTypeface(contextThemeWrapper, textPaint.getTypeface()));
            }
            this.mPaint.setColor(getColor());
            this.mPaint.setTextSize(scale(18.0d));
            this.mPaint.setAntiAlias(true);
            this.mShadowRadius = getAttrAsFloat(element2, "shadowRadius", 0.0f);
            this.mShadowDx = getAttrAsFloat(element2, "shadowDx", 0.0f);
            this.mShadowDy = getAttrAsFloat(element2, "shadowDy", 0.0f);
            this.mShadowColorParser = ColorParser.fromElement(variables, element2, "shadowColor", this.mStyle);
            this.mPaint.setShadowLayer(this.mShadowRadius, this.mShadowDx, this.mShadowDy, getShadowColor());
            this.mTextSizeProperty = new PropertyWrapper(this.mName + ".textColor", getVariables(), build, isInFolmeMode(), 18.0d);
            this.mTextColorProperty = new PropertyWrapper(this.mName + ".textSize", getVariables(), (Expression) null, isInFolmeMode(), (double) this.mColorParser.getColor());
            this.mTextShadowColorProperty = new PropertyWrapper(this.mName + ".textShadowColor", getVariables(), (Expression) null, isInFolmeMode(), (double) this.mShadowColorParser.getColor());
        }
    }

    private static int parseFontStyle(String str) {
        if (!TextUtils.isEmpty(str) && !"normal".equals(str)) {
            if (TtmlNode.BOLD.equals(str)) {
                return 1;
            }
            if (TtmlNode.ITALIC.equals(str)) {
                return 2;
            }
            if ("bold_italic".equals(str)) {
                return 3;
            }
        }
        return 0;
    }

    private void updateTextSize() {
        this.mTextSize = scale(this.mTextSizeProperty.getValue());
        if (this.mFontScaleEnabled) {
            this.mTextSize *= this.mRoot.getFontScale();
        }
        this.mPaint.setTextSize(this.mTextSize);
    }

    private void updateTextWidth() {
        this.mTextWidth = 0.0f;
        if (!TextUtils.isEmpty(this.mText)) {
            if (this.mMultiLine) {
                String[] split = this.mText.split(CRLF);
                for (String measureText : split) {
                    float measureText2 = this.mPaint.measureText(measureText);
                    if (measureText2 > this.mTextWidth) {
                        this.mTextWidth = measureText2;
                    }
                }
            } else {
                this.mTextWidth = this.mPaint.measureText(this.mText);
            }
        }
        if (this.mHasName) {
            this.mTextWidthVar.set(descale((double) this.mTextWidth));
        }
    }

    /* access modifiers changed from: protected */
    public void doRender(Canvas canvas) {
        float f;
        float f2;
        float f3;
        float f4;
        if (!TextUtils.isEmpty(this.mText)) {
            this.mPaint.setColor(getColor());
            TextPaint textPaint = this.mPaint;
            textPaint.setAlpha(Utils.mixAlpha(textPaint.getAlpha(), getAlpha()));
            this.mPaint.setShadowLayer(this.mShadowRadius, this.mShadowDx, this.mShadowDy, getShadowColor());
            float width = getWidth();
            boolean z = width >= 0.0f;
            if (width < 0.0f || width > this.mTextWidth) {
                width = this.mTextWidth;
            }
            float height = getHeight();
            float textSize = this.mPaint.getTextSize();
            if (height < 0.0f) {
                height = this.mTextHeight;
            }
            float left = getLeft(0.0f, width);
            float top = getTop(0.0f, height);
            canvas.save();
            float f5 = this.mShadowRadius;
            if (f5 != 0.0f) {
                f4 = Math.min(0.0f, this.mShadowDx - f5);
                f3 = Math.max(0.0f, this.mShadowDx + this.mShadowRadius);
                f2 = Math.min(0.0f, this.mShadowDy - this.mShadowRadius);
                f = Math.max(0.0f, this.mShadowDy + this.mShadowRadius);
            } else {
                f4 = 0.0f;
                f3 = 0.0f;
                f2 = 0.0f;
                f = 0.0f;
            }
            canvas.translate(left, top);
            if (z) {
                f4 = 0.0f;
            }
            if (z) {
                f3 = 0.0f;
            }
            canvas.clipRect(f4, f2, f3 + width, height + f);
            StaticLayout staticLayout = this.mTextLayout;
            if (staticLayout != null) {
                if (staticLayout.getLineCount() != 1 || !this.mShouldMarquee) {
                    this.mTextLayout.draw(canvas);
                } else {
                    int lineStart = this.mTextLayout.getLineStart(0);
                    int lineEnd = this.mTextLayout.getLineEnd(0);
                    int lineTop = this.mTextLayout.getLineTop(0);
                    float lineLeft = this.mTextLayout.getLineLeft(0);
                    float f6 = ((float) lineTop) + textSize;
                    canvas.drawText(this.mText, lineStart, lineEnd, lineLeft + this.mMarqueePos, f6, this.mPaint);
                    float f7 = this.mMarqueePos;
                    if (f7 != 0.0f) {
                        float f8 = f7 + this.mTextWidth + (this.mTextSize * ((float) this.mMarqueeGap));
                        if (f8 < width) {
                            canvas.drawText(this.mText, lineLeft + f8, f6, this.mPaint);
                        }
                    }
                }
            }
            canvas.restore();
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00f7, code lost:
        if (r1.mShouldMarquee == false) goto L_0x00fc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00f9, code lost:
        r0 = 45.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00fc, code lost:
        r0 = 0.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00fd, code lost:
        requestFramerate(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0100, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00ad  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00bd  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void doTick(long r19) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            java.lang.Object r4 = mLock
            monitor-enter(r4)
            super.doTick(r19)     // Catch:{ all -> 0x0101 }
            boolean r0 = r18.isVisible()     // Catch:{ all -> 0x0101 }
            if (r0 != 0) goto L_0x0012
            monitor-exit(r4)     // Catch:{ all -> 0x0101 }
            return
        L_0x0012:
            r0 = 0
            r1.mShouldMarquee = r0     // Catch:{ all -> 0x0101 }
            java.lang.String r5 = r1.mText     // Catch:{ all -> 0x0101 }
            java.lang.String r6 = r18.getText()     // Catch:{ all -> 0x0101 }
            r1.mText = r6     // Catch:{ all -> 0x0101 }
            java.lang.String r6 = r1.mText     // Catch:{ all -> 0x0101 }
            boolean r6 = android.text.TextUtils.isEmpty(r6)     // Catch:{ all -> 0x0101 }
            if (r6 == 0) goto L_0x002d
            r0 = 0
            r1.mTextLayout = r0     // Catch:{ all -> 0x0101 }
            r18.updateTextWidth()     // Catch:{ all -> 0x0101 }
            monitor-exit(r4)     // Catch:{ all -> 0x0101 }
            return
        L_0x002d:
            float r6 = r1.mTextSize     // Catch:{ all -> 0x0101 }
            r18.updateTextSize()     // Catch:{ all -> 0x0101 }
            java.lang.String r7 = r1.mText     // Catch:{ all -> 0x0101 }
            boolean r5 = android.text.TextUtils.equals(r5, r7)     // Catch:{ all -> 0x0101 }
            r7 = 1
            if (r5 == 0) goto L_0x0044
            float r5 = r1.mTextSize     // Catch:{ all -> 0x0101 }
            int r5 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
            if (r5 == 0) goto L_0x0042
            goto L_0x0044
        L_0x0042:
            r5 = r0
            goto L_0x0045
        L_0x0044:
            r5 = r7
        L_0x0045:
            if (r5 == 0) goto L_0x004a
            r18.updateTextWidth()     // Catch:{ all -> 0x0101 }
        L_0x004a:
            float r6 = r18.getWidth()     // Catch:{ all -> 0x0101 }
            boolean r8 = r1.mMultiLine     // Catch:{ all -> 0x0101 }
            if (r8 != 0) goto L_0x005d
            int r8 = r1.mMarqueeSpeed     // Catch:{ all -> 0x0101 }
            if (r8 <= 0) goto L_0x005d
            float r8 = r1.mTextWidth     // Catch:{ all -> 0x0101 }
            int r8 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r8 <= 0) goto L_0x005d
            r0 = r7
        L_0x005d:
            boolean r8 = r1.mMultiLine     // Catch:{ all -> 0x0101 }
            if (r8 == 0) goto L_0x0067
            float r8 = r1.mTextWidth     // Catch:{ all -> 0x0101 }
            int r8 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r8 <= 0) goto L_0x0069
        L_0x0067:
            float r6 = r1.mTextWidth     // Catch:{ all -> 0x0101 }
        L_0x0069:
            android.text.StaticLayout r8 = r1.mTextLayout     // Catch:{ all -> 0x0101 }
            r9 = 2139095039(0x7f7fffff, float:3.4028235E38)
            if (r8 == 0) goto L_0x0078
            if (r5 != 0) goto L_0x0078
            float r5 = r1.mLayoutWidth     // Catch:{ all -> 0x0101 }
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 == 0) goto L_0x00bb
        L_0x0078:
            r1.mLayoutWidth = r6     // Catch:{ all -> 0x0101 }
            android.text.StaticLayout r5 = new android.text.StaticLayout     // Catch:{ all -> 0x0101 }
            java.lang.String r11 = r1.mText     // Catch:{ all -> 0x0101 }
            android.text.TextPaint r12 = r1.mPaint     // Catch:{ all -> 0x0101 }
            float r6 = r1.mLayoutWidth     // Catch:{ all -> 0x0101 }
            double r13 = (double) r6     // Catch:{ all -> 0x0101 }
            double r13 = java.lang.Math.ceil(r13)     // Catch:{ all -> 0x0101 }
            int r13 = (int) r13     // Catch:{ all -> 0x0101 }
            android.text.Layout$Alignment r14 = r18.getAlignment()     // Catch:{ all -> 0x0101 }
            float r15 = r1.mSpacingMult     // Catch:{ all -> 0x0101 }
            float r6 = r1.mSpacingAdd     // Catch:{ all -> 0x0101 }
            r17 = 0
            r10 = r5
            r16 = r6
            r10.<init>(r11, r12, r13, r14, r15, r16, r17)     // Catch:{ all -> 0x0101 }
            r1.mTextLayout = r5     // Catch:{ all -> 0x0101 }
            android.text.StaticLayout r5 = r1.mTextLayout     // Catch:{ all -> 0x0101 }
            android.text.StaticLayout r6 = r1.mTextLayout     // Catch:{ all -> 0x0101 }
            int r6 = r6.getLineCount()     // Catch:{ all -> 0x0101 }
            int r5 = r5.getLineTop(r6)     // Catch:{ all -> 0x0101 }
            float r5 = (float) r5     // Catch:{ all -> 0x0101 }
            r1.mTextHeight = r5     // Catch:{ all -> 0x0101 }
            boolean r5 = r1.mHasName     // Catch:{ all -> 0x0101 }
            if (r5 == 0) goto L_0x00b9
            com.miui.maml.data.IndexedVariable r5 = r1.mTextHeightVar     // Catch:{ all -> 0x0101 }
            float r6 = r1.mTextHeight     // Catch:{ all -> 0x0101 }
            double r10 = (double) r6     // Catch:{ all -> 0x0101 }
            double r10 = r1.descale(r10)     // Catch:{ all -> 0x0101 }
            r5.set((double) r10)     // Catch:{ all -> 0x0101 }
        L_0x00b9:
            r1.mMarqueePos = r9     // Catch:{ all -> 0x0101 }
        L_0x00bb:
            if (r0 == 0) goto L_0x00f4
            float r0 = r1.mMarqueePos     // Catch:{ all -> 0x0101 }
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 != 0) goto L_0x00c8
            r0 = 1112014848(0x42480000, float:50.0)
            r1.mMarqueePos = r0     // Catch:{ all -> 0x0101 }
            goto L_0x00f0
        L_0x00c8:
            float r0 = r1.mMarqueePos     // Catch:{ all -> 0x0101 }
            int r5 = r1.mMarqueeSpeed     // Catch:{ all -> 0x0101 }
            long r5 = (long) r5     // Catch:{ all -> 0x0101 }
            long r8 = r1.mPreviousTime     // Catch:{ all -> 0x0101 }
            long r8 = r2 - r8
            long r5 = r5 * r8
            float r5 = (float) r5     // Catch:{ all -> 0x0101 }
            r6 = 1148846080(0x447a0000, float:1000.0)
            float r5 = r5 / r6
            float r0 = r0 - r5
            r1.mMarqueePos = r0     // Catch:{ all -> 0x0101 }
            float r0 = r1.mMarqueePos     // Catch:{ all -> 0x0101 }
            float r5 = r1.mTextWidth     // Catch:{ all -> 0x0101 }
            float r5 = -r5
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 >= 0) goto L_0x00f0
            float r0 = r1.mMarqueePos     // Catch:{ all -> 0x0101 }
            float r5 = r1.mTextWidth     // Catch:{ all -> 0x0101 }
            float r6 = r1.mTextSize     // Catch:{ all -> 0x0101 }
            int r8 = r1.mMarqueeGap     // Catch:{ all -> 0x0101 }
            float r8 = (float) r8     // Catch:{ all -> 0x0101 }
            float r6 = r6 * r8
            float r5 = r5 + r6
            float r0 = r0 + r5
            r1.mMarqueePos = r0     // Catch:{ all -> 0x0101 }
        L_0x00f0:
            r1.mPreviousTime = r2     // Catch:{ all -> 0x0101 }
            r1.mShouldMarquee = r7     // Catch:{ all -> 0x0101 }
        L_0x00f4:
            monitor-exit(r4)     // Catch:{ all -> 0x0101 }
            boolean r0 = r1.mShouldMarquee
            if (r0 == 0) goto L_0x00fc
            r0 = 1110704128(0x42340000, float:45.0)
            goto L_0x00fd
        L_0x00fc:
            r0 = 0
        L_0x00fd:
            r1.requestFramerate(r0)
            return
        L_0x0101:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0101 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.TextScreenElement.doTick(long):void");
    }

    public void finish() {
        super.finish();
        this.mText = null;
        this.mSetText = null;
        this.mMarqueePos = Float.MAX_VALUE;
    }

    /* access modifiers changed from: protected */
    public int getColor() {
        return isInFolmeMode() ? (int) ((long) this.mTextColorProperty.getValue()) : this.mColorParser.getColor();
    }

    /* access modifiers changed from: protected */
    public String getFormat() {
        return this.mFormatter.getFormat();
    }

    public float getHeight() {
        float height = super.getHeight();
        return height <= 0.0f ? this.mTextHeight : height;
    }

    /* access modifiers changed from: protected */
    public int getShadowColor() {
        return isInFolmeMode() ? (int) ((long) this.mTextShadowColorProperty.getValue()) : this.mShadowColorParser.getColor();
    }

    /* access modifiers changed from: protected */
    public String getText() {
        String str = this.mSetText;
        if (str != null) {
            return str;
        }
        String text = this.mFormatter.getText();
        if (text == null) {
            return text;
        }
        String replace = text.replace(RAW_CRLF, CRLF);
        return !this.mMultiLine ? replace.replace(CRLF, " ") : replace;
    }

    public float getWidth() {
        float width = super.getWidth();
        return width <= 0.0f ? this.mTextWidth : width;
    }

    public void init() {
        super.init();
    }

    /* access modifiers changed from: protected */
    public void initProperties() {
        super.initProperties();
        this.mTextSizeProperty.init();
        this.mTextColorProperty.init();
        this.mTextShadowColorProperty.init();
    }

    /* access modifiers changed from: protected */
    public void onVisibilityChange(boolean z) {
        super.onVisibilityChange(z);
        requestFramerate((!this.mShouldMarquee || !z) ? 0.0f : 45.0f);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
        TextPaint textPaint = this.mPaint;
        if (textPaint != null) {
            textPaint.setColorFilter(colorFilter);
        }
    }

    public void setParams(Object... objArr) {
        this.mFormatter.setParams(objArr);
    }

    public void setText(String str) {
        this.mSetText = str;
    }
}
