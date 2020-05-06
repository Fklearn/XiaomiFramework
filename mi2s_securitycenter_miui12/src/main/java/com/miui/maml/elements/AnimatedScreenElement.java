package com.miui.maml.elements;

import a.c.d;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.animation.AlphaAnimation;
import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.animation.PositionAnimation;
import com.miui.maml.animation.RotationAnimation;
import com.miui.maml.animation.ScaleAnimation;
import com.miui.maml.animation.SizeAnimation;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.maml.folme.AnimatedTarget;
import com.miui.maml.folme.FolmeConfigValue;
import com.miui.maml.folme.IAnimatedProperty;
import com.miui.maml.folme.PropertyWrapper;
import com.miui.maml.util.ColorParser;
import com.miui.maml.util.Utils;
import d.a.a.a;
import d.a.b;
import d.a.e.k;
import d.a.e.l;
import d.a.g.C0574a;
import d.a.g.C0575b;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import org.w3c.dom.Element;

public abstract class AnimatedScreenElement extends ScreenElement {
    public static final String LOG_TAG = "AnimatedScreenElement";
    private static final int sPaintColor = -4982518;
    private IndexedVariable mActualXVar;
    private IndexedVariable mActualYVar;
    protected int mAlpha;
    public PropertyWrapper mAlphaProperty;
    private AlphaAnimation mAlphas;
    private AnimatedTarget mAnimTarget;
    /* access modifiers changed from: private */
    public d<FunctionElement> mBeginCallback = new d<>();
    private Camera mCamera;
    /* access modifiers changed from: private */
    public d<FunctionElement> mCompleteCallback = new d<>();
    protected String mContentDescription;
    protected Expression mContentDescriptionExp;
    private boolean mFolmeMode;
    protected boolean mHasContentDescription;
    public PropertyWrapper mHeightProperty;
    protected boolean mInterceptTouch;
    private boolean mIsHaptic;
    private k mListener = new k() {
        public void onBegin(Object obj) {
            Iterator it = AnimatedScreenElement.this.mBeginCallback.iterator();
            while (it.hasNext()) {
                ((FunctionElement) it.next()).perform();
            }
        }

        public void onComplete(Object obj) {
            AnimatedScreenElement.this.mToProperties.clear();
            Iterator it = AnimatedScreenElement.this.mCompleteCallback.iterator();
            while (it.hasNext()) {
                ((FunctionElement) it.next()).perform();
            }
        }

        public void onUpdate(Object obj, Collection<l> collection) {
            for (l next : collection) {
                C0575b bVar = next.f8730a;
                if (bVar instanceof IAnimatedProperty) {
                    ((IAnimatedProperty) bVar).setVelocityValue(AnimatedScreenElement.this, next.f8731b);
                }
                if (next.f8732c) {
                    AnimatedScreenElement.this.mToProperties.remove(bVar);
                }
            }
            Iterator it = AnimatedScreenElement.this.mUpdateCallback.iterator();
            while (it.hasNext()) {
                ((FunctionElement) it.next()).perform();
            }
        }
    };
    private float mMarginBottom;
    private float mMarginLeft;
    private float mMarginRight;
    private float mMarginTop;
    private Matrix mMatrix = new Matrix();
    private Paint mPaint = new Paint();
    public PropertyWrapper mPivotXProperty;
    public PropertyWrapper mPivotYProperty;
    public PropertyWrapper mPivotZProperty;
    private PositionAnimation mPositions;
    protected boolean mPressed;
    public PropertyWrapper mRotationProperty;
    public PropertyWrapper mRotationXProperty;
    public PropertyWrapper mRotationYProperty;
    public PropertyWrapper mRotationZProperty;
    private RotationAnimation mRotations;
    private Expression mScaleExpression;
    public PropertyWrapper mScaleXProperty;
    public PropertyWrapper mScaleYProperty;
    private ScaleAnimation mScales;
    private SizeAnimation mSizes;
    private FunctionElement mTickListener;
    protected boolean mTintChanged = true;
    protected int mTintColor;
    protected ColorParser mTintColorParser;
    public PropertyWrapper mTintColorProperty;
    protected PorterDuffColorFilter mTintFilter;
    protected PorterDuff.Mode mTintMode = PorterDuff.Mode.SRC_IN;
    protected Expression mTintModeExp;
    /* access modifiers changed from: private */
    public CopyOnWriteArraySet<C0575b> mToProperties = new CopyOnWriteArraySet<>();
    protected boolean mTouchable;
    /* access modifiers changed from: private */
    public d<FunctionElement> mUpdateCallback = new d<>();
    private int mVirtualViewId = Integer.MIN_VALUE;
    public PropertyWrapper mWidthProperty;
    public PropertyWrapper mXProperty;
    public PropertyWrapper mYProperty;

    public AnimatedScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        load(element);
        if (this.mHasContentDescription) {
            this.mRoot.addAccessibleElements(this);
        }
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(1.0f);
        this.mPaint.setColor(sPaintColor);
    }

    private Expression createExp(Variables variables, Element element, String str, String str2) {
        Expression build = Expression.build(variables, getAttr(element, str));
        return (build != null || TextUtils.isEmpty(str2)) ? build : Expression.build(variables, getAttr(element, str2));
    }

    /* access modifiers changed from: private */
    public void folmeFromToImpl(String str, String str2, String str3) {
        a[] aVarArr;
        ScreenElement findElement = getRoot().findElement(str);
        ScreenElement findElement2 = getRoot().findElement(str2);
        ScreenElement findElement3 = getRoot().findElement(str3);
        if (findElement == null || findElement2 == null || !(findElement instanceof FolmeStateScreenElement) || !(findElement2 instanceof FolmeStateScreenElement)) {
            Log.w(LOG_TAG, "folmeFromTo: wrong state name " + str + " " + str2);
            return;
        }
        if (findElement3 != null) {
            try {
                aVarArr = createAnimConfig((FolmeConfigScreenElement) findElement3);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else {
            aVarArr = new a[0];
        }
        d.a.b.a createAnimState = createAnimState(AnimatedTarget.STATE_TAG_FROM, (FolmeStateScreenElement) findElement);
        d.a.b.a createAnimState2 = createAnimState(AnimatedTarget.STATE_TAG_TO, (FolmeStateScreenElement) findElement2);
        setupToProperties((FolmeStateScreenElement) findElement2);
        b.a((d.a.d) getAnimTarget()).state().a(createAnimState, createAnimState2, aVarArr);
    }

    /* access modifiers changed from: private */
    public void folmeSetToImpl(String str) {
        ScreenElement findElement = getRoot().findElement(str);
        if (findElement == null || !(findElement instanceof FolmeStateScreenElement)) {
            Log.w(LOG_TAG, "folmeSetTo: wrong state name " + str);
            return;
        }
        try {
            b.a((d.a.d) getAnimTarget()).state().setTo((Object) createAnimState(AnimatedTarget.STATE_TAG_SET_TO, (FolmeStateScreenElement) findElement));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void folmeToImpl(String str, String str2) {
        a[] aVarArr;
        ScreenElement findElement = getRoot().findElement(str);
        ScreenElement findElement2 = getRoot().findElement(str2);
        if (findElement == null || !(findElement instanceof FolmeStateScreenElement)) {
            Log.w(LOG_TAG, "folmeTo: wrong state name " + str);
            return;
        }
        if (findElement2 != null) {
            try {
                aVarArr = createAnimConfig((FolmeConfigScreenElement) findElement2);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else {
            aVarArr = new a[0];
        }
        d.a.b.a createAnimState = createAnimState(AnimatedTarget.STATE_TAG_TO, (FolmeStateScreenElement) findElement);
        setupToProperties((FolmeStateScreenElement) findElement);
        b.a((d.a.d) getAnimTarget()).state().a(createAnimState, aVarArr);
    }

    private void handleCancel() {
        if (this.mTouchable && this.mPressed) {
            this.mPressed = false;
            performAction("cancel");
            onActionCancel();
        }
    }

    private boolean isInMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    private void load(Element element) {
        Expression expression;
        Expression expression2;
        Expression expression3;
        Expression expression4;
        Expression expression5;
        Expression expression6;
        Expression expression7;
        Expression expression8;
        Expression expression9;
        Expression expression10;
        Expression expression11;
        Expression expression12;
        Expression expression13;
        Expression expression14;
        Expression expression15;
        boolean z;
        Element element2 = element;
        Variables variables = getVariables();
        if (element2 != null) {
            this.mScaleExpression = createExp(variables, element2, "scale", (String) null);
            Expression createExp = createExp(variables, element2, AnimatedProperty.PROPERTY_NAME_X, TtmlNode.LEFT);
            Expression createExp2 = createExp(variables, element2, AnimatedProperty.PROPERTY_NAME_Y, "top");
            Expression createExp3 = createExp(variables, element2, AnimatedProperty.PROPERTY_NAME_W, "width");
            Expression createExp4 = createExp(variables, element2, AnimatedProperty.PROPERTY_NAME_H, "height");
            Expression createExp5 = createExp(variables, element2, "angle", AnimatedProperty.PROPERTY_NAME_ROTATION);
            Expression createExp6 = createExp(variables, element2, "centerX", AnimatedProperty.PROPERTY_NAME_PIVOT_X);
            Expression createExp7 = createExp(variables, element2, "centerY", AnimatedProperty.PROPERTY_NAME_PIVOT_Y);
            Expression createExp8 = createExp(variables, element2, AnimatedProperty.PROPERTY_NAME_ALPHA, (String) null);
            Expression createExp9 = createExp(variables, element2, AnimatedProperty.PROPERTY_NAME_SCALE_X, (String) null);
            Expression createExp10 = createExp(variables, element2, AnimatedProperty.PROPERTY_NAME_SCALE_Y, (String) null);
            expression9 = createExp(variables, element2, "angleX", AnimatedProperty.PROPERTY_NAME_ROTATION_X);
            expression8 = createExp(variables, element2, "angleY", AnimatedProperty.PROPERTY_NAME_ROTATION_Y);
            expression6 = createExp10;
            expression5 = createExp(variables, element2, "angleZ", AnimatedProperty.PROPERTY_NAME_ROTATION_Z);
            Expression createExp11 = createExp(variables, element2, "centerZ", AnimatedProperty.PROPERTY_NAME_PIVOT_Z);
            if (this.mHasName) {
                StringBuilder sb = new StringBuilder();
                expression15 = createExp;
                sb.append(this.mName);
                sb.append(".");
                expression = createExp2;
                sb.append(ScreenElement.ACTUAL_X);
                this.mActualXVar = new IndexedVariable(sb.toString(), variables, true);
                z = true;
                this.mActualYVar = new IndexedVariable(this.mName + "." + ScreenElement.ACTUAL_Y, variables, true);
            } else {
                expression15 = createExp;
                expression = createExp2;
                z = true;
            }
            this.mTouchable = Boolean.parseBoolean(getAttr(element2, "touchable"));
            this.mInterceptTouch = Boolean.parseBoolean(getAttr(element2, "interceptTouch"));
            this.mIsHaptic = Boolean.parseBoolean(getAttr(element2, "haptic"));
            this.mContentDescription = getAttr(element2, "contentDescription");
            this.mContentDescriptionExp = Expression.build(variables, getAttr(element2, "contentDescriptionExp"));
            int i = 0;
            if (TextUtils.isEmpty(this.mContentDescription) && this.mContentDescriptionExp == null) {
                z = false;
            }
            this.mHasContentDescription = z;
            this.mMarginLeft = Utils.getAttrAsFloat(element2, "marginLeft", 0.0f);
            this.mMarginRight = Utils.getAttrAsFloat(element2, "marginRight", 0.0f);
            this.mMarginTop = Utils.getAttrAsFloat(element2, "marginTop", 0.0f);
            this.mMarginBottom = Utils.getAttrAsFloat(element2, "marginBottom", 0.0f);
            String attr = getAttr(element2, "tint");
            if (!TextUtils.isEmpty(attr)) {
                this.mTintColorParser = new ColorParser(variables, attr);
            }
            this.mTintModeExp = Expression.build(variables, getAttr(element2, "tintmode"));
            ColorParser colorParser = this.mTintColorParser;
            if (colorParser != null) {
                i = colorParser.getColor();
            }
            this.mTintColor = i;
            this.mFolmeMode = Boolean.parseBoolean(getAttr(element2, "folmeMode"));
            expression3 = createExp7;
            expression7 = createExp9;
            expression14 = expression15;
            expression12 = createExp4;
            expression10 = createExp8;
            expression2 = createExp11;
            expression11 = createExp5;
            expression4 = createExp6;
            expression13 = createExp3;
        } else {
            expression14 = null;
            expression13 = null;
            expression12 = null;
            expression11 = null;
            expression10 = null;
            expression9 = null;
            expression8 = null;
            expression7 = null;
            expression6 = null;
            expression5 = null;
            expression4 = null;
            expression3 = null;
            expression2 = null;
            expression = null;
        }
        Variables variables2 = variables;
        Expression expression16 = expression7;
        PropertyWrapper propertyWrapper = r1;
        PropertyWrapper propertyWrapper2 = new PropertyWrapper(this.mName + ".x", variables2, expression14, isInFolmeMode(), 0.0d);
        this.mXProperty = propertyWrapper;
        this.mYProperty = new PropertyWrapper(this.mName + ".y", variables2, expression, isInFolmeMode(), 0.0d);
        this.mWidthProperty = new PropertyWrapper(this.mName + ".w", variables2, expression13, isInFolmeMode(), -1.0d);
        this.mHeightProperty = new PropertyWrapper(this.mName + ".h", variables2, expression12, isInFolmeMode(), -1.0d);
        this.mRotationProperty = new PropertyWrapper(this.mName + ".rotation", variables2, expression11, isInFolmeMode(), 0.0d);
        this.mAlphaProperty = new PropertyWrapper(this.mName + ".alpha", variables2, expression10, isInFolmeMode(), 255.0d);
        this.mRotationXProperty = new PropertyWrapper(this.mName + ".rotationX", variables2, expression9, isInFolmeMode(), 0.0d);
        this.mRotationYProperty = new PropertyWrapper(this.mName + ".rotationY", variables2, expression8, isInFolmeMode(), 0.0d);
        this.mRotationZProperty = new PropertyWrapper(this.mName + ".rotationZ", variables2, expression5, isInFolmeMode(), 0.0d);
        this.mScaleXProperty = new PropertyWrapper(this.mName + ".scaleX", variables2, expression16, isInFolmeMode(), 1.0d);
        this.mScaleYProperty = new PropertyWrapper(this.mName + ".scaleY", variables2, expression6, isInFolmeMode(), 1.0d);
        this.mTintColorProperty = new PropertyWrapper(this.mName + ".tintColor", variables2, (Expression) null, isInFolmeMode(), (double) this.mTintColor);
        this.mPivotXProperty = new PropertyWrapper(this.mName + ".pivotX", variables2, expression4, isInFolmeMode(), 0.0d);
        this.mPivotYProperty = new PropertyWrapper(this.mName + ".pivotY", variables2, expression3, isInFolmeMode(), 0.0d);
        this.mPivotZProperty = new PropertyWrapper(this.mName + ".pivotZ", variables2, expression2, isInFolmeMode(), 0.0d);
    }

    private void setupCallbacks(d<FunctionElement> dVar, d<String> dVar2) {
        if (dVar2 != null) {
            Iterator<String> it = dVar2.iterator();
            while (it.hasNext()) {
                ScreenElement findElement = getRoot().findElement(it.next());
                if (findElement instanceof FunctionElement) {
                    dVar.add((FunctionElement) findElement);
                }
            }
        }
    }

    private void setupToProperties(FolmeStateScreenElement folmeStateScreenElement) {
        for (Map.Entry<String, Expression> key : folmeStateScreenElement.getAttrs().entrySet()) {
            C0575b propertyByName = AnimatedProperty.getPropertyByName((String) key.getKey());
            if (propertyByName != null) {
                this.mToProperties.add(propertyByName);
            }
        }
    }

    /* access modifiers changed from: protected */
    public a[] createAnimConfig(FolmeConfigScreenElement folmeConfigScreenElement) {
        ArrayList arrayList = new ArrayList();
        ArrayList<FolmeConfigValue> config = folmeConfigScreenElement.getConfig();
        this.mUpdateCallback.clear();
        this.mBeginCallback.clear();
        this.mCompleteCallback.clear();
        Iterator<FolmeConfigValue> it = config.iterator();
        while (it.hasNext()) {
            FolmeConfigValue next = it.next();
            a aVar = null;
            if (next.relatedProperty != null) {
                ArrayList arrayList2 = new ArrayList();
                Iterator<String> it2 = next.relatedProperty.iterator();
                while (it2.hasNext()) {
                    C0575b propertyByName = AnimatedProperty.getPropertyByName(it2.next());
                    if (propertyByName != null) {
                        arrayList2.add(propertyByName);
                    }
                }
                aVar = new a((C0575b[]) arrayList2.toArray(new AnimatedProperty[arrayList2.size()]));
            }
            if (aVar == null) {
                aVar = new a();
            }
            setupCallbacks(this.mBeginCallback, next.onBeginCallback);
            setupCallbacks(this.mUpdateCallback, next.onUpdateCallback);
            setupCallbacks(this.mCompleteCallback, next.onCompleteCallback);
            aVar.a(next.ease);
            aVar.a(next.delay);
            aVar.a(this.mListener);
            arrayList.add(aVar);
        }
        return (a[]) arrayList.toArray(new a[arrayList.size()]);
    }

    /* access modifiers changed from: protected */
    public d.a.b.a createAnimState(String str, FolmeStateScreenElement folmeStateScreenElement) {
        d.a.b.a aVar = new d.a.b.a(str);
        for (Map.Entry next : folmeStateScreenElement.getAttrs().entrySet()) {
            C0575b propertyByName = AnimatedProperty.getPropertyByName((String) next.getKey());
            if (propertyByName != null) {
                if (propertyByName instanceof C0574a) {
                    aVar.a(propertyByName, (int) ((long) ((Expression) next.getValue()).evaluate()), 2);
                } else {
                    aVar.a(propertyByName, (float) ((Expression) next.getValue()).evaluate(), 2);
                }
            }
        }
        return aVar;
    }

    /* access modifiers changed from: protected */
    public void doRenderWithTranslation(Canvas canvas) {
        int save = canvas.save();
        this.mMatrix.reset();
        float rotationX = getRotationX();
        float rotationY = getRotationY();
        float rotationZ = getRotationZ();
        if (!(rotationX == 0.0f && rotationY == 0.0f && rotationZ == 0.0f)) {
            if (this.mCamera == null) {
                this.mCamera = new Camera();
            }
            this.mCamera.save();
            this.mCamera.rotate(rotationX, rotationY, rotationZ);
            float pivotZ = getPivotZ();
            if (pivotZ != 0.0f) {
                this.mCamera.translate(0.0f, 0.0f, pivotZ);
            }
            this.mCamera.getMatrix(this.mMatrix);
            this.mCamera.restore();
        }
        float rotation = getRotation();
        if (rotation != 0.0f) {
            this.mMatrix.preRotate(rotation);
        }
        float scaleX = getScaleX();
        float scaleY = getScaleY();
        if (!(scaleX == 1.0f && scaleY == 1.0f)) {
            this.mMatrix.preScale(scaleX, scaleY);
        }
        float x = getX();
        float y = getY();
        float pivotX = getPivotX() - (x - getLeft());
        float pivotY = getPivotY() - (y - getTop());
        this.mMatrix.preTranslate(-pivotX, -pivotY);
        this.mMatrix.postTranslate(pivotX + x, pivotY + y);
        canvas.concat(this.mMatrix);
        doRender(canvas);
        if (this.mRoot.mShowDebugLayout) {
            float width = getWidth();
            float height = getHeight();
            if (width > 0.0f && height > 0.0f) {
                float left = getLeft(0.0f, width);
                float top = getTop(0.0f, height);
                canvas.drawRect(left, top, left + width, top + height, this.mPaint);
            }
        }
        canvas.restoreToCount(save);
    }

    /* access modifiers changed from: protected */
    public void doTick(long j) {
        super.doTick(j);
        if (this.mHasName) {
            this.mActualXVar.set(descale((double) getX()));
            this.mActualYVar.set(descale((double) getY()));
        }
        this.mAlpha = evaluateAlpha();
        int i = this.mAlpha;
        if (i < 0) {
            i = 0;
        }
        this.mAlpha = i;
        this.mTintChanged = false;
        int tintColor = getTintColor();
        if (tintColor != this.mTintColor) {
            this.mTintChanged = true;
            this.mTintColor = tintColor;
        }
        if (this.mTintColor != 0) {
            PorterDuff.Mode mode = this.mTintMode;
            Expression expression = this.mTintModeExp;
            if (expression != null) {
                mode = Utils.getPorterDuffMode((int) expression.evaluate(), this.mTintMode);
            }
            if (this.mTintMode != mode) {
                this.mTintMode = mode;
                this.mTintChanged = true;
            }
            if (this.mTintFilter == null || this.mTintChanged) {
                this.mTintFilter = new PorterDuffColorFilter(this.mTintColor, mode);
            }
        } else {
            this.mTintFilter = null;
        }
        FunctionElement functionElement = this.mTickListener;
        if (functionElement != null) {
            functionElement.perform();
        }
    }

    /* access modifiers changed from: protected */
    public int evaluateAlpha() {
        int value = (int) ((long) this.mAlphaProperty.getValue());
        if (!isInFolmeMode()) {
            AlphaAnimation alphaAnimation = this.mAlphas;
            value = Utils.mixAlpha(value, alphaAnimation != null ? alphaAnimation.getAlpha() : 255);
        }
        ElementGroup elementGroup = this.mParent;
        return (elementGroup == null || (elementGroup instanceof LayerScreenElement)) ? value : (!(elementGroup instanceof ElementGroup) || !elementGroup.isLayered()) ? Utils.mixAlpha(value, this.mParent.getAlpha()) : value;
    }

    public void finish() {
        super.finish();
        getContext().getHandler().removeCallbacksAndMessages(this);
        try {
            if (this.mAnimTarget != null) {
                b.a((T[]) new AnimatedScreenElement[]{this});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void folmeCancel(Expression[] expressionArr) {
        if (expressionArr != null) {
            try {
                d dVar = new d();
                for (Expression evaluateStr : expressionArr) {
                    C0575b propertyByName = AnimatedProperty.getPropertyByName(evaluateStr.evaluateStr());
                    if (propertyByName != null) {
                        dVar.add(propertyByName);
                        this.mToProperties.remove(propertyByName);
                    }
                }
                b.a((d.a.d) getAnimTarget()).state().a((C0575b[]) dVar.toArray(new C0575b[dVar.size()]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            b.a((d.a.d) getAnimTarget()).state().cancel();
            this.mToProperties.clear();
        }
    }

    public void folmeFromTo(final String str, final String str2, final String str3) {
        if (isInMainThread()) {
            folmeFromToImpl(str, str2, str3);
        } else {
            getContext().getHandler().postAtTime(new Runnable() {
                public void run() {
                    AnimatedScreenElement.this.folmeFromToImpl(str, str2, str3);
                }
            }, this, 0);
        }
    }

    public void folmeSetTo(final String str) {
        if (isInMainThread()) {
            folmeSetToImpl(str);
        } else {
            getContext().getHandler().postAtTime(new Runnable() {
                public void run() {
                    AnimatedScreenElement.this.folmeSetToImpl(str);
                }
            }, this, 0);
        }
    }

    public void folmeTo(final String str, final String str2) {
        if (isInMainThread()) {
            folmeToImpl(str, str2);
        } else {
            getContext().getHandler().postAtTime(new Runnable() {
                public void run() {
                    AnimatedScreenElement.this.folmeToImpl(str, str2);
                }
            }, this, 0);
        }
    }

    public float getAbsoluteLeft() {
        float left = getLeft();
        ElementGroup elementGroup = this.mParent;
        return left + (elementGroup == null ? 0.0f : elementGroup.getParentLeft());
    }

    public float getAbsoluteTop() {
        float top = getTop();
        ElementGroup elementGroup = this.mParent;
        return top + (elementGroup == null ? 0.0f : elementGroup.getParentTop());
    }

    public int getAlpha() {
        return this.mAlpha;
    }

    /* access modifiers changed from: protected */
    public AnimatedTarget getAnimTarget() {
        if (this.mAnimTarget == null) {
            this.mAnimTarget = (AnimatedTarget) b.a(this, AnimatedTarget.sCreator);
        }
        return this.mAnimTarget;
    }

    public String getContentDescription() {
        Expression expression = this.mContentDescriptionExp;
        if (expression == null) {
            return this.mContentDescription;
        }
        String evaluateStr = expression.evaluateStr();
        if (evaluateStr != null) {
            return evaluateStr;
        }
        Log.e(LOG_TAG, "element.getContentDescription() == null " + this.mName);
        return "";
    }

    public float getHeight() {
        return scale((double) getHeightRaw());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000d, code lost:
        r1 = r2.mSizes;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public float getHeightRaw() {
        /*
            r2 = this;
            com.miui.maml.folme.PropertyWrapper r0 = r2.mHeightProperty
            double r0 = r0.getValue()
            float r0 = (float) r0
            boolean r1 = r2.isInFolmeMode()
            if (r1 != 0) goto L_0x0016
            com.miui.maml.animation.SizeAnimation r1 = r2.mSizes
            if (r1 == 0) goto L_0x0016
            double r0 = r1.getHeight()
            float r0 = (float) r0
        L_0x0016:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.AnimatedScreenElement.getHeightRaw():float");
    }

    /* access modifiers changed from: protected */
    public float getLeft() {
        return getLeft(getX(), getWidth());
    }

    public final float getMarginBottom() {
        return scale((double) this.mMarginBottom);
    }

    public final float getMarginLeft() {
        return scale((double) this.mMarginLeft);
    }

    public final float getMarginRight() {
        return scale((double) this.mMarginRight);
    }

    public final float getMarginTop() {
        return scale((double) this.mMarginTop);
    }

    /* access modifiers changed from: protected */
    public Matrix getMatrix() {
        return this.mMatrix;
    }

    /* access modifiers changed from: protected */
    public float getPivotX() {
        return scale(this.mPivotXProperty.getValue());
    }

    /* access modifiers changed from: protected */
    public float getPivotY() {
        return scale(this.mPivotYProperty.getValue());
    }

    /* access modifiers changed from: protected */
    public float getPivotZ() {
        return scale(this.mPivotZProperty.getValue());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000d, code lost:
        r1 = r2.mRotations;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public float getRotation() {
        /*
            r2 = this;
            com.miui.maml.folme.PropertyWrapper r0 = r2.mRotationProperty
            double r0 = r0.getValue()
            float r0 = (float) r0
            boolean r1 = r2.isInFolmeMode()
            if (r1 != 0) goto L_0x0016
            com.miui.maml.animation.RotationAnimation r1 = r2.mRotations
            if (r1 == 0) goto L_0x0016
            float r1 = r1.getAngle()
            float r0 = r0 + r1
        L_0x0016:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.AnimatedScreenElement.getRotation():float");
    }

    public float getRotationX() {
        return (float) this.mRotationXProperty.getValue();
    }

    public float getRotationY() {
        return (float) this.mRotationYProperty.getValue();
    }

    public float getRotationZ() {
        return (float) this.mRotationZProperty.getValue();
    }

    public float getScaleX() {
        float value = (float) this.mScaleXProperty.getValue();
        if (isInFolmeMode()) {
            return value;
        }
        Expression expression = this.mScaleExpression;
        if (expression != null) {
            value = (float) expression.evaluate();
        }
        ScaleAnimation scaleAnimation = this.mScales;
        return scaleAnimation != null ? (float) (((double) value) * scaleAnimation.getScaleX()) : value;
    }

    public float getScaleY() {
        float value = (float) this.mScaleYProperty.getValue();
        if (isInFolmeMode()) {
            return value;
        }
        Expression expression = this.mScaleExpression;
        if (expression != null) {
            value = (float) expression.evaluate();
        }
        ScaleAnimation scaleAnimation = this.mScales;
        return scaleAnimation != null ? (float) (((double) value) * scaleAnimation.getScaleY()) : value;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000e, code lost:
        r1 = r2.mTintColorParser;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getTintColor() {
        /*
            r2 = this;
            com.miui.maml.folme.PropertyWrapper r0 = r2.mTintColorProperty
            double r0 = r0.getValue()
            long r0 = (long) r0
            int r0 = (int) r0
            boolean r1 = r2.isInFolmeMode()
            if (r1 != 0) goto L_0x0016
            com.miui.maml.util.ColorParser r1 = r2.mTintColorParser
            if (r1 == 0) goto L_0x0016
            int r0 = r1.getColor()
        L_0x0016:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.AnimatedScreenElement.getTintColor():int");
    }

    /* access modifiers changed from: protected */
    public float getTop() {
        return getTop(getY(), getHeight());
    }

    public float getWidth() {
        return scale((double) getWidthRaw());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000d, code lost:
        r1 = r2.mSizes;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public float getWidthRaw() {
        /*
            r2 = this;
            com.miui.maml.folme.PropertyWrapper r0 = r2.mWidthProperty
            double r0 = r0.getValue()
            float r0 = (float) r0
            boolean r1 = r2.isInFolmeMode()
            if (r1 != 0) goto L_0x0016
            com.miui.maml.animation.SizeAnimation r1 = r2.mSizes
            if (r1 == 0) goto L_0x0016
            double r0 = r1.getWidth()
            float r0 = (float) r0
        L_0x0016:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.AnimatedScreenElement.getWidthRaw():float");
    }

    /* access modifiers changed from: protected */
    public float getX() {
        PositionAnimation positionAnimation;
        float value = (float) this.mXProperty.getValue();
        if (!isInFolmeMode() && (positionAnimation = this.mPositions) != null) {
            value = (float) (((double) value) + positionAnimation.getX());
        }
        return scale((double) value);
    }

    /* access modifiers changed from: protected */
    public float getY() {
        PositionAnimation positionAnimation;
        float value = (float) this.mYProperty.getValue();
        if (!isInFolmeMode() && (positionAnimation = this.mPositions) != null) {
            value = (float) (((double) value) + positionAnimation.getY());
        }
        return scale((double) value);
    }

    public void init() {
        super.init();
        if (isInFolmeMode()) {
            initProperties();
        }
    }

    /* access modifiers changed from: protected */
    public void initProperties() {
        this.mXProperty.init();
        this.mYProperty.init();
        this.mWidthProperty.init();
        this.mHeightProperty.init();
        this.mRotationProperty.init();
        this.mAlphaProperty.init();
        this.mRotationXProperty.init();
        this.mRotationYProperty.init();
        this.mRotationZProperty.init();
        this.mScaleXProperty.init();
        this.mScaleYProperty.init();
        this.mTintColorProperty.init();
        this.mPivotXProperty.init();
        this.mPivotYProperty.init();
        this.mPivotZProperty.init();
    }

    /* access modifiers changed from: protected */
    public boolean isInFolmeMode() {
        return this.mFolmeMode && this.mHasName;
    }

    /* access modifiers changed from: protected */
    public void onActionCancel() {
    }

    /* access modifiers changed from: protected */
    public void onActionDown(float f, float f2) {
        this.mRoot.onUIInteractive(this, "down");
    }

    /* access modifiers changed from: protected */
    public void onActionMove(float f, float f2) {
        this.mRoot.onUIInteractive(this, "move");
    }

    /* access modifiers changed from: protected */
    public void onActionUp() {
        this.mRoot.onUIInteractive(this, "up");
    }

    /* access modifiers changed from: protected */
    public BaseAnimation onCreateAnimation(String str, Element element) {
        if (AlphaAnimation.TAG_NAME.equals(str)) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(element, this);
            this.mAlphas = alphaAnimation;
            return alphaAnimation;
        } else if (PositionAnimation.TAG_NAME.equals(str)) {
            PositionAnimation positionAnimation = new PositionAnimation(element, this);
            this.mPositions = positionAnimation;
            return positionAnimation;
        } else if (RotationAnimation.TAG_NAME.equals(str)) {
            RotationAnimation rotationAnimation = new RotationAnimation(element, this);
            this.mRotations = rotationAnimation;
            return rotationAnimation;
        } else if (SizeAnimation.TAG_NAME.equals(str)) {
            SizeAnimation sizeAnimation = new SizeAnimation(element, this);
            this.mSizes = sizeAnimation;
            return sizeAnimation;
        } else if (!ScaleAnimation.TAG_NAME.equals(str)) {
            return super.onCreateAnimation(str, element);
        } else {
            ScaleAnimation scaleAnimation = new ScaleAnimation(element, this);
            this.mScales = scaleAnimation;
            return scaleAnimation;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0042, code lost:
        if (r7.mRoot.getHoverElement() != r7) goto L_0x002f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x002d, code lost:
        if (touched(r2, r3) == false) goto L_0x0045;
     */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:25:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onHover(android.view.MotionEvent r8) {
        /*
            r7 = this;
            boolean r0 = r7.isVisible()
            r1 = 0
            if (r0 == 0) goto L_0x0051
            boolean r0 = r7.mHasContentDescription
            if (r0 != 0) goto L_0x000c
            goto L_0x0051
        L_0x000c:
            java.lang.String r0 = r7.getContentDescription()
            float r2 = r8.getX()
            float r3 = r8.getY()
            boolean r4 = super.onHover(r8)
            int r8 = r8.getActionMasked()
            r5 = 7
            r6 = 1
            if (r8 == r5) goto L_0x0036
            r5 = 9
            if (r8 == r5) goto L_0x0029
            goto L_0x0045
        L_0x0029:
            boolean r8 = r7.touched(r2, r3)
            if (r8 == 0) goto L_0x0045
        L_0x002f:
            com.miui.maml.ScreenElementRoot r8 = r7.mRoot
            r8.onHoverChange(r7, r0)
        L_0x0034:
            r4 = r6
            goto L_0x0045
        L_0x0036:
            boolean r8 = r7.touched(r2, r3)
            if (r8 == 0) goto L_0x0045
            com.miui.maml.ScreenElementRoot r8 = r7.mRoot
            com.miui.maml.elements.AnimatedScreenElement r8 = r8.getHoverElement()
            if (r8 == r7) goto L_0x0034
            goto L_0x002f
        L_0x0045:
            if (r4 == 0) goto L_0x004a
            r7.requestUpdate()
        L_0x004a:
            if (r4 == 0) goto L_0x0051
            boolean r8 = r7.mInterceptTouch
            if (r8 == 0) goto L_0x0051
            r1 = r6
        L_0x0051:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.AnimatedScreenElement.onHover(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: protected */
    public void onSetAnimBefore() {
        this.mAlphas = null;
        this.mPositions = null;
        this.mRotations = null;
        this.mSizes = null;
        this.mScales = null;
    }

    /* access modifiers changed from: protected */
    public void onSetAnimEnable(BaseAnimation baseAnimation) {
        if (baseAnimation instanceof AlphaAnimation) {
            this.mAlphas = (AlphaAnimation) baseAnimation;
        } else if (baseAnimation instanceof PositionAnimation) {
            this.mPositions = (PositionAnimation) baseAnimation;
        } else if (baseAnimation instanceof RotationAnimation) {
            this.mRotations = (RotationAnimation) baseAnimation;
        } else if (baseAnimation instanceof SizeAnimation) {
            this.mSizes = (SizeAnimation) baseAnimation;
        } else if (baseAnimation instanceof ScaleAnimation) {
            this.mScales = (ScaleAnimation) baseAnimation;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x0081  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x008a  */
    /* JADX WARNING: Removed duplicated region for block: B:40:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouch(android.view.MotionEvent r7) {
        /*
            r6 = this;
            boolean r0 = r6.isVisible()
            r1 = 0
            if (r0 == 0) goto L_0x008b
            boolean r0 = r6.mTouchable
            if (r0 != 0) goto L_0x000d
            goto L_0x008b
        L_0x000d:
            float r0 = r7.getX()
            float r2 = r7.getY()
            boolean r3 = super.onTouch(r7)
            int r7 = r7.getActionMasked()
            r4 = 1
            if (r7 == 0) goto L_0x0065
            if (r7 == r4) goto L_0x003e
            r5 = 2
            if (r7 == r5) goto L_0x002d
            r0 = 3
            if (r7 == r0) goto L_0x0029
            goto L_0x007f
        L_0x0029:
            r6.handleCancel()
            goto L_0x007f
        L_0x002d:
            boolean r7 = r6.mPressed
            if (r7 == 0) goto L_0x007f
            boolean r3 = r6.touched(r0, r2)
            java.lang.String r7 = "move"
            r6.performAction(r7)
            r6.onActionMove(r0, r2)
            goto L_0x007f
        L_0x003e:
            boolean r7 = r6.mPressed
            if (r7 == 0) goto L_0x007f
            r6.mPressed = r1
            boolean r7 = r6.touched(r0, r2)
            if (r7 == 0) goto L_0x005c
            boolean r7 = r6.mIsHaptic
            if (r7 == 0) goto L_0x0053
            com.miui.maml.ScreenElementRoot r7 = r6.mRoot
            r7.haptic(r4)
        L_0x0053:
            java.lang.String r7 = "up"
            r6.performAction(r7)
            r6.onActionUp()
            goto L_0x007e
        L_0x005c:
            java.lang.String r7 = "cancel"
            r6.performAction(r7)
            r6.onActionCancel()
            goto L_0x007e
        L_0x0065:
            boolean r7 = r6.touched(r0, r2)
            if (r7 == 0) goto L_0x007f
            r6.mPressed = r4
            boolean r7 = r6.mIsHaptic
            if (r7 == 0) goto L_0x0076
            com.miui.maml.ScreenElementRoot r7 = r6.mRoot
            r7.haptic(r4)
        L_0x0076:
            java.lang.String r7 = "down"
            r6.performAction(r7)
            r6.onActionDown(r0, r2)
        L_0x007e:
            r3 = r4
        L_0x007f:
            if (r3 == 0) goto L_0x0084
            r6.requestUpdate()
        L_0x0084:
            if (r3 == 0) goto L_0x008b
            boolean r7 = r6.mInterceptTouch
            if (r7 == 0) goto L_0x008b
            r1 = r4
        L_0x008b:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.AnimatedScreenElement.onTouch(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: protected */
    public void onVisibilityChange(boolean z) {
        super.onVisibilityChange(z);
        if (!z) {
            handleCancel();
            if (this.mVirtualViewId != Integer.MIN_VALUE && getRoot().getMamlAccessHelper() != null && getRoot().getMamlAccessHelper().getFocusedVirtualView() == this.mVirtualViewId) {
                getRoot().getMamlAccessHelper().performAccessibilityAction(this.mVirtualViewId, 128);
            }
        }
    }

    public void pause() {
        super.pause();
        handleCancel();
        try {
            if (this.mAnimTarget != null && this.mToProperties.size() > 0) {
                b.a((d.a.d) this.mAnimTarget).state().a(this.mToProperties.toArray(new C0575b[this.mToProperties.size()]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void render(Canvas canvas) {
        updateVisibility();
        if (isVisible()) {
            doRenderWithTranslation(canvas);
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        Paint paint = this.mPaint;
        if (paint != null) {
            paint.setColorFilter(colorFilter);
        }
    }

    public void setHeight(double d2) {
        this.mHeightProperty.setValue(descale(d2));
    }

    public void setOnTickListener(FunctionElement functionElement) {
        this.mTickListener = functionElement;
    }

    public void setVirtualViewId(int i) {
        this.mVirtualViewId = i;
    }

    public void setWidth(double d2) {
        this.mWidthProperty.setValue(descale(d2));
    }

    public void setX(double d2) {
        this.mXProperty.setValue(descale(d2));
    }

    public void setY(double d2) {
        this.mYProperty.setValue(descale(d2));
    }

    public boolean touched(float f, float f2) {
        return touched(f, f2, true);
    }

    public boolean touched(float f, float f2, boolean z) {
        if (z) {
            ElementGroup elementGroup = this.mParent;
            float f3 = 0.0f;
            float parentLeft = elementGroup == null ? 0.0f : elementGroup.getParentLeft();
            ElementGroup elementGroup2 = this.mParent;
            if (elementGroup2 != null) {
                f3 = elementGroup2.getParentTop();
            }
            f -= parentLeft;
            f2 -= f3;
        }
        float left = getLeft();
        float top = getTop();
        return f >= left && f <= left + getWidth() && f2 >= top && f2 <= top + getHeight();
    }

    public void unsetOnTickListener() {
        this.mTickListener = null;
    }
}
