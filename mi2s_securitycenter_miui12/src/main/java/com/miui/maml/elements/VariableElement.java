package com.miui.maml.elements;

import android.graphics.Canvas;
import android.util.Log;
import com.miui.maml.CommandTrigger;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.animation.VariableAnimation;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.VariableType;
import com.miui.maml.data.Variables;
import com.miui.maml.util.Utils;
import org.w3c.dom.Element;

public class VariableElement extends ScreenElement {
    private static final String LOG_TAG = "VariableElement";
    private static final String OLD_VALUE = "old_value";
    public static final String TAG_NAME = "Var";
    private VariableAnimation mAnimation;
    private int mArraySize;
    private Expression[] mArrayValues;
    private boolean mConst;
    private Expression mExpression;
    private Expression mIndexExpression;
    private boolean mInited;
    private double mOldValue;
    private IndexedVariable mOldVar;
    private double mThreshold;
    private CommandTrigger mTrigger;
    private VariableType mType;
    private IndexedVariable mVar;

    /* renamed from: com.miui.maml.elements.VariableElement$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$VariableType = new int[VariableType.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|8) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        static {
            /*
                com.miui.maml.data.VariableType[] r0 = com.miui.maml.data.VariableType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$data$VariableType = r0
                int[] r0 = $SwitchMap$com$miui$maml$data$VariableType     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.data.VariableType r1 = com.miui.maml.data.VariableType.STR     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$maml$data$VariableType     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.data.VariableType r1 = com.miui.maml.data.VariableType.STR_ARR     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$maml$data$VariableType     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.maml.data.VariableType r1 = com.miui.maml.data.VariableType.NUM     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.VariableElement.AnonymousClass1.<clinit>():void");
        }
    }

    public VariableElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        StringBuilder sb;
        String str;
        if (element != null) {
            this.mExpression = Expression.build(getVariables(), element.getAttribute("expression"));
            this.mIndexExpression = Expression.build(getVariables(), element.getAttribute("index"));
            this.mThreshold = (double) Math.abs(Utils.getAttrAsFloat(element, "threshold", 1.0f));
            this.mType = VariableType.parseType(element.getAttribute("type"));
            this.mConst = Boolean.parseBoolean(element.getAttribute("const"));
            this.mArraySize = Utils.getAttrAsInt(element, "size", 0);
            Variables variables = getVariables();
            this.mVar = new IndexedVariable(this.mName, variables, this.mType.isNumber());
            this.mOldVar = new IndexedVariable(this.mName + "." + OLD_VALUE, variables, this.mType.isNumber());
            this.mTrigger = CommandTrigger.fromParentElement(element, screenElementRoot);
            if (this.mType.isArray()) {
                this.mArrayValues = Expression.buildMultiple(variables, element.getAttribute("values"));
                Expression[] expressionArr = this.mArrayValues;
                if (expressionArr != null) {
                    this.mArraySize = expressionArr.length;
                }
                int i = this.mArraySize;
                if (i <= 0) {
                    sb = new StringBuilder();
                    str = "array size is 0:";
                } else if (!variables.createArray(this.mName, i, this.mType.mTypeClass)) {
                    sb = new StringBuilder();
                    str = "fail to create array:";
                } else {
                    return;
                }
                sb.append(str);
                sb.append(this.mName);
                Log.e(LOG_TAG, sb.toString());
            }
        }
    }

    private double getDouble(boolean z, int i) {
        VariableAnimation variableAnimation = this.mAnimation;
        if (variableAnimation != null) {
            return variableAnimation.getValue();
        }
        Expression expression = this.mExpression;
        return expression != null ? expression.evaluate() : z ? this.mVar.getArrDouble(i) : this.mVar.getDouble();
    }

    private void onValueChange(double d2) {
        if (!this.mInited) {
            this.mOldValue = d2;
        }
        if (this.mTrigger != null && Math.abs(d2 - this.mOldValue) >= this.mThreshold) {
            this.mOldVar.set(this.mOldValue);
            this.mOldValue = d2;
            this.mTrigger.perform();
        }
    }

    private void update() {
        CommandTrigger commandTrigger;
        Expression expression;
        int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$VariableType[this.mType.ordinal()];
        if (i != 1) {
            int i2 = 0;
            if (i != 2) {
                if (i == 3) {
                    double d2 = getDouble(false, 0);
                    this.mVar.set(d2);
                    onValueChange(d2);
                    return;
                } else if (this.mType.isNumberArray()) {
                    Expression expression2 = this.mIndexExpression;
                    if (expression2 != null) {
                        int evaluate = (int) expression2.evaluate();
                        double d3 = getDouble(true, evaluate);
                        this.mVar.setArr(evaluate, d3);
                        onValueChange(d3);
                        return;
                    }
                    Expression[] expressionArr = this.mArrayValues;
                    if (expressionArr != null) {
                        int length = expressionArr.length;
                        while (i2 < length) {
                            Expression expression3 = this.mArrayValues[i2];
                            this.mVar.setArr(i2, expression3 == null ? 0.0d : expression3.evaluate());
                            i2++;
                        }
                        return;
                    }
                    return;
                } else {
                    return;
                }
            } else if (this.mIndexExpression == null || (expression = this.mExpression) == null) {
                Expression[] expressionArr2 = this.mArrayValues;
                if (expressionArr2 != null) {
                    int length2 = expressionArr2.length;
                    while (i2 < length2) {
                        Expression expression4 = this.mArrayValues[i2];
                        this.mVar.setArr(i2, (Object) expression4 == null ? null : expression4.evaluateStr());
                        i2++;
                    }
                    return;
                }
                return;
            } else {
                String evaluateStr = expression.evaluateStr();
                int evaluate2 = (int) this.mIndexExpression.evaluate();
                String arrString = this.mVar.getArrString(evaluate2);
                if (!Utils.equals(evaluateStr, arrString)) {
                    this.mOldVar.set((Object) arrString);
                    this.mVar.setArr(evaluate2, (Object) evaluateStr);
                    commandTrigger = this.mTrigger;
                    if (commandTrigger == null) {
                        return;
                    }
                } else {
                    return;
                }
            }
        } else {
            Expression expression5 = this.mExpression;
            if (expression5 != null) {
                String evaluateStr2 = expression5.evaluateStr();
                String string = this.mVar.getString();
                if (!Utils.equals(evaluateStr2, string)) {
                    this.mOldVar.set((Object) string);
                    this.mVar.set((Object) evaluateStr2);
                    commandTrigger = this.mTrigger;
                    if (commandTrigger == null) {
                        return;
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        commandTrigger.perform();
    }

    /* access modifiers changed from: protected */
    public void doRender(Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public void doTick(long j) {
        if (!this.mConst) {
            super.doTick(j);
            update();
        }
    }

    public void finish() {
        super.finish();
        CommandTrigger commandTrigger = this.mTrigger;
        if (commandTrigger != null) {
            commandTrigger.finish();
        }
        this.mInited = false;
    }

    public void init() {
        super.init();
        CommandTrigger commandTrigger = this.mTrigger;
        if (commandTrigger != null) {
            commandTrigger.init();
        }
        update();
        this.mInited = true;
    }

    /* access modifiers changed from: protected */
    public BaseAnimation onCreateAnimation(String str, Element element) {
        if (!VariableAnimation.TAG_NAME.equals(str)) {
            return super.onCreateAnimation(str, element);
        }
        VariableAnimation variableAnimation = new VariableAnimation(element, this);
        this.mAnimation = variableAnimation;
        return variableAnimation;
    }

    /* access modifiers changed from: protected */
    public void onSetAnimBefore() {
        this.mAnimation = null;
    }

    /* access modifiers changed from: protected */
    public void onSetAnimEnable(BaseAnimation baseAnimation) {
        if (baseAnimation instanceof VariableAnimation) {
            this.mAnimation = (VariableAnimation) baseAnimation;
        }
    }

    public void pause() {
        super.pause();
        CommandTrigger commandTrigger = this.mTrigger;
        if (commandTrigger != null) {
            commandTrigger.pause();
        }
    }

    /* access modifiers changed from: protected */
    public void pauseAnim(long j) {
        super.pauseAnim(j);
        update();
    }

    /* access modifiers changed from: protected */
    public void playAnim(long j, long j2, long j3, boolean z, boolean z2) {
        super.playAnim(j, j2, j3, z, z2);
        update();
    }

    public void reset(long j) {
        super.reset(j);
        update();
    }

    public void resume() {
        super.resume();
        CommandTrigger commandTrigger = this.mTrigger;
        if (commandTrigger != null) {
            commandTrigger.resume();
        }
    }

    /* access modifiers changed from: protected */
    public void resumeAnim(long j) {
        super.resumeAnim(j);
        update();
    }
}
