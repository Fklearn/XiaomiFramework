package com.miui.maml.elements;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.text.TextUtils;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.data.Variables;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.maml.folme.AnimatedTarget;
import com.miui.maml.folme.PropertyWrapper;
import com.miui.maml.shader.ShadersElement;
import com.miui.maml.util.ColorParser;
import com.miui.maml.util.Utils;
import org.w3c.dom.Element;

public abstract class GeometryScreenElement extends AnimatedScreenElement {
    public static final AnimatedProperty.AnimatedColorProperty FILL_COLOR = new AnimatedProperty.AnimatedColorProperty(PROPERTY_NAME_FILL_COLOR) {
        public int getIntValue(AnimatedScreenElement animatedScreenElement) {
            if (animatedScreenElement instanceof GeometryScreenElement) {
                return (int) ((long) ((GeometryScreenElement) animatedScreenElement).mFillColorProperty.getValue());
            }
            return 0;
        }

        public void setIntValue(AnimatedScreenElement animatedScreenElement, int i) {
            if (animatedScreenElement instanceof GeometryScreenElement) {
                ((GeometryScreenElement) animatedScreenElement).mFillColorProperty.setValue((double) i);
            }
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof GeometryScreenElement) {
                ((GeometryScreenElement) animatedScreenElement).mFillColorProperty.setVelocity((double) f);
            }
        }
    };
    private static final String LOG_TAG = "GeometryScreenElement";
    private static final String PROPERTY_NAME_FILL_COLOR = "fillColor";
    private static final String PROPERTY_NAME_STROKE_COLOR = "strokeColor";
    private static final String PROPERTY_NAME_STROKE_WEIGHT = "strokeWeight";
    public static final AnimatedProperty.AnimatedColorProperty STROKE_COLOR = new AnimatedProperty.AnimatedColorProperty(PROPERTY_NAME_STROKE_COLOR) {
        public int getIntValue(AnimatedScreenElement animatedScreenElement) {
            if (animatedScreenElement instanceof GeometryScreenElement) {
                return (int) ((long) ((GeometryScreenElement) animatedScreenElement).mStrokeColorProperty.getValue());
            }
            return 0;
        }

        public void setIntValue(AnimatedScreenElement animatedScreenElement, int i) {
            if (animatedScreenElement instanceof GeometryScreenElement) {
                ((GeometryScreenElement) animatedScreenElement).mStrokeColorProperty.setValue((double) i);
            }
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof GeometryScreenElement) {
                ((GeometryScreenElement) animatedScreenElement).mStrokeColorProperty.setVelocity((double) f);
            }
        }
    };
    public static final AnimatedProperty STROKE_WEIGHT = new AnimatedProperty(PROPERTY_NAME_STROKE_WEIGHT) {
        public float getValue(AnimatedScreenElement animatedScreenElement) {
            if (animatedScreenElement instanceof GeometryScreenElement) {
                return (float) ((GeometryScreenElement) animatedScreenElement).mStrokeWeightProperty.getValue();
            }
            return 0.0f;
        }

        public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof GeometryScreenElement) {
                ((GeometryScreenElement) animatedScreenElement).mStrokeWeightProperty.setValue((double) f);
            }
        }

        public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
            if (animatedScreenElement instanceof GeometryScreenElement) {
                ((GeometryScreenElement) animatedScreenElement).mStrokeWeightProperty.setVelocity((double) f);
            }
        }
    };
    private int mFillColor;
    protected ColorParser mFillColorParser;
    /* access modifiers changed from: private */
    public PropertyWrapper mFillColorProperty;
    protected ShadersElement mFillShadersElement;
    protected Paint mPaint = new Paint();
    private final DrawMode mStrokeAlign;
    private int mStrokeColor;
    protected ColorParser mStrokeColorParser;
    /* access modifiers changed from: private */
    public PropertyWrapper mStrokeColorProperty;
    protected ShadersElement mStrokeShadersElement;
    /* access modifiers changed from: private */
    public PropertyWrapper mStrokeWeightProperty;
    protected float mWeight = scale(1.0d);
    protected Expression mXfermodeNumExp;

    protected enum DrawMode {
        STROKE_CENTER,
        STROKE_OUTER,
        STROKE_INNER,
        FILL;

        public static DrawMode getStrokeAlign(String str) {
            return "inner".equalsIgnoreCase(str) ? STROKE_INNER : TtmlNode.CENTER.equalsIgnoreCase(str) ? STROKE_CENTER : STROKE_OUTER;
        }
    }

    static {
        AnimatedProperty.sPropertyNameMap.put(PROPERTY_NAME_FILL_COLOR, FILL_COLOR);
        AnimatedTarget.sPropertyMap.put(1004, FILL_COLOR);
        AnimatedTarget.sPropertyTypeMap.put(FILL_COLOR, 1004);
        AnimatedProperty.sPropertyNameMap.put(PROPERTY_NAME_STROKE_COLOR, STROKE_COLOR);
        AnimatedTarget.sPropertyMap.put(1005, STROKE_COLOR);
        AnimatedTarget.sPropertyTypeMap.put(STROKE_COLOR, 1005);
        AnimatedProperty.sPropertyNameMap.put(PROPERTY_NAME_STROKE_WEIGHT, STROKE_WEIGHT);
        AnimatedTarget.sPropertyMap.put(1012, STROKE_WEIGHT);
        AnimatedTarget.sPropertyTypeMap.put(STROKE_WEIGHT, 1012);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GeometryScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        Element element2 = element;
        String attr = getAttr(element2, PROPERTY_NAME_STROKE_COLOR);
        Variables variables = getVariables();
        if (!TextUtils.isEmpty(attr)) {
            this.mStrokeColorParser = new ColorParser(variables, attr);
        }
        String attr2 = getAttr(element2, PROPERTY_NAME_FILL_COLOR);
        if (!TextUtils.isEmpty(attr2)) {
            this.mFillColorParser = new ColorParser(variables, attr2);
        }
        Expression build = Expression.build(variables, getAttr(element2, "weight"));
        this.mPaint.setStrokeCap(getCap(getAttr(element2, "cap")));
        float[] resolveDashIntervals = resolveDashIntervals(element);
        if (resolveDashIntervals != null) {
            this.mPaint.setPathEffect(new DashPathEffect(resolveDashIntervals, 0.0f));
        }
        this.mStrokeAlign = DrawMode.getStrokeAlign(getAttr(element2, "strokeAlign"));
        this.mXfermodeNumExp = Expression.build(variables, getAttr(element2, "xfermodeNum"));
        if (this.mXfermodeNumExp == null) {
            this.mPaint.setXfermode(new PorterDuffXfermode(Utils.getPorterDuffMode(getAttr(element2, "xfermode"))));
        }
        this.mPaint.setAntiAlias(true);
        ColorParser colorParser = this.mStrokeColorParser;
        int i = 0;
        this.mStrokeColor = colorParser != null ? colorParser.getColor() : 0;
        ColorParser colorParser2 = this.mFillColorParser;
        this.mFillColor = colorParser2 != null ? colorParser2.getColor() : i;
        this.mFillColorProperty = new PropertyWrapper(this.mName + ".fillColor", getVariables(), (Expression) null, isInFolmeMode(), (double) this.mFillColor);
        this.mStrokeColorProperty = new PropertyWrapper(this.mName + ".strokeColor", getVariables(), (Expression) null, isInFolmeMode(), (double) this.mStrokeColor);
        this.mStrokeWeightProperty = new PropertyWrapper(this.mName + ".strokeWeight", getVariables(), build, isInFolmeMode(), 0.0d);
        loadShadersElement(element, screenElementRoot);
    }

    private final Paint.Cap getCap(String str) {
        Paint.Cap cap = Paint.Cap.BUTT;
        return TextUtils.isEmpty(str) ? cap : str.equalsIgnoreCase("round") ? Paint.Cap.ROUND : str.equalsIgnoreCase("square") ? Paint.Cap.SQUARE : cap;
    }

    private void loadShadersElement(Element element, ScreenElementRoot screenElementRoot) {
        Element child = Utils.getChild(element, ShadersElement.STROKE_TAG_NAME);
        if (child != null) {
            this.mStrokeShadersElement = new ShadersElement(child, screenElementRoot);
        }
        Element child2 = Utils.getChild(element, ShadersElement.FILL_TAG_NAME);
        if (child2 != null) {
            this.mFillShadersElement = new ShadersElement(child2, screenElementRoot);
        }
    }

    private float[] resolveDashIntervals(Element element) {
        String attr = getAttr(element, "dash");
        if (TextUtils.isEmpty(attr)) {
            return null;
        }
        String[] split = attr.split(",");
        if (split.length < 2 || split.length % 2 != 0) {
            return null;
        }
        float[] fArr = new float[split.length];
        int i = 0;
        while (i < split.length) {
            try {
                fArr[i] = Float.parseFloat(split[i]);
                i++;
            } catch (NumberFormatException unused) {
                return null;
            }
        }
        return fArr;
    }

    /* access modifiers changed from: protected */
    public void doRender(Canvas canvas) {
        int i;
        Paint paint;
        int i2;
        Paint paint2;
        if (!(this.mFillShadersElement == null && this.mFillColor == 0)) {
            this.mPaint.setStyle(Paint.Style.FILL);
            ShadersElement shadersElement = this.mFillShadersElement;
            if (shadersElement != null) {
                this.mPaint.setShader(shadersElement.getShader());
                paint2 = this.mPaint;
                i2 = this.mAlpha;
            } else {
                this.mPaint.setShader((Shader) null);
                this.mPaint.setColor(this.mFillColor);
                paint2 = this.mPaint;
                i2 = Utils.mixAlpha(paint2.getAlpha(), this.mAlpha);
            }
            paint2.setAlpha(i2);
            onDraw(canvas, DrawMode.FILL);
        }
        if (this.mWeight <= 0.0f) {
            return;
        }
        if (this.mStrokeShadersElement != null || this.mStrokeColor != 0) {
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setStrokeWidth(this.mWeight);
            ShadersElement shadersElement2 = this.mStrokeShadersElement;
            if (shadersElement2 != null) {
                this.mPaint.setShader(shadersElement2.getShader());
                paint = this.mPaint;
                i = this.mAlpha;
            } else {
                this.mPaint.setShader((Shader) null);
                this.mPaint.setColor(this.mStrokeColor);
                paint = this.mPaint;
                i = Utils.mixAlpha(paint.getAlpha(), this.mAlpha);
            }
            paint.setAlpha(i);
            onDraw(canvas, this.mStrokeAlign);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x003c  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0043  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0056  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x006d  */
    /* JADX WARNING: Removed duplicated region for block: B:26:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void doTick(long r2) {
        /*
            r1 = this;
            super.doTick(r2)
            boolean r2 = r1.isVisible()
            if (r2 != 0) goto L_0x000a
            return
        L_0x000a:
            boolean r2 = r1.isInFolmeMode()
            if (r2 == 0) goto L_0x0025
            com.miui.maml.folme.PropertyWrapper r2 = r1.mStrokeColorProperty
            double r2 = r2.getValue()
            long r2 = (long) r2
            int r2 = (int) r2
            r1.mStrokeColor = r2
            com.miui.maml.folme.PropertyWrapper r2 = r1.mFillColorProperty
            double r2 = r2.getValue()
            long r2 = (long) r2
            int r2 = (int) r2
        L_0x0022:
            r1.mFillColor = r2
            goto L_0x0038
        L_0x0025:
            com.miui.maml.util.ColorParser r2 = r1.mStrokeColorParser
            if (r2 == 0) goto L_0x002f
            int r2 = r2.getColor()
            r1.mStrokeColor = r2
        L_0x002f:
            com.miui.maml.util.ColorParser r2 = r1.mFillColorParser
            if (r2 == 0) goto L_0x0038
            int r2 = r2.getColor()
            goto L_0x0022
        L_0x0038:
            com.miui.maml.shader.ShadersElement r2 = r1.mStrokeShadersElement
            if (r2 == 0) goto L_0x003f
            r2.updateShader()
        L_0x003f:
            com.miui.maml.shader.ShadersElement r2 = r1.mFillShadersElement
            if (r2 == 0) goto L_0x0046
            r2.updateShader()
        L_0x0046:
            com.miui.maml.folme.PropertyWrapper r2 = r1.mStrokeWeightProperty
            double r2 = r2.getValue()
            float r2 = r1.scale(r2)
            r1.mWeight = r2
            com.miui.maml.data.Expression r2 = r1.mXfermodeNumExp
            if (r2 == 0) goto L_0x0069
            double r2 = r2.evaluate()
            int r2 = (int) r2
            android.graphics.PorterDuff$Mode r2 = com.miui.maml.util.Utils.getPorterDuffMode((int) r2)
            android.graphics.Paint r3 = r1.mPaint
            android.graphics.PorterDuffXfermode r0 = new android.graphics.PorterDuffXfermode
            r0.<init>(r2)
            r3.setXfermode(r0)
        L_0x0069:
            boolean r2 = r1.mTintChanged
            if (r2 == 0) goto L_0x0074
            android.graphics.Paint r2 = r1.mPaint
            android.graphics.PorterDuffColorFilter r3 = r1.mTintFilter
            r2.setColorFilter(r3)
        L_0x0074:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.GeometryScreenElement.doTick(long):void");
    }

    /* access modifiers changed from: protected */
    public void initProperties() {
        super.initProperties();
        this.mFillColorProperty.init();
        this.mStrokeColorProperty.init();
        this.mStrokeWeightProperty.init();
    }

    /* access modifiers changed from: protected */
    public abstract void onDraw(Canvas canvas, DrawMode drawMode);

    public void setColorFilter(ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
        Paint paint = this.mPaint;
        if (paint != null) {
            paint.setColorFilter(colorFilter);
        }
    }
}
