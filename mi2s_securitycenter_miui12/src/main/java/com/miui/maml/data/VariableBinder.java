package com.miui.maml.data;

import android.text.TextUtils;
import com.miui.maml.CommandTrigger;
import com.miui.maml.ScreenContext;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.ContentProviderBinder;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import miui.cloud.CloudPushConstants;
import org.w3c.dom.Element;

public abstract class VariableBinder {
    private String mDependency;
    protected boolean mFinished;
    protected String mName;
    protected boolean mPaused;
    protected boolean mQueryAtStart = true;
    private ContentProviderBinder.QueryCompleteListener mQueryCompletedListener;
    protected ScreenElementRoot mRoot;
    protected CommandTrigger mTrigger;
    protected ArrayList<Variable> mVariables = new ArrayList<>();

    public static class TypedValue {
        public static final int BITMAP = 7;
        public static final int DOUBLE = 6;
        public static final int FLOAT = 5;
        public static final int INT = 3;
        public static final int LONG = 4;
        public static final int NUM_ARR = 8;
        public static final int STRING = 2;
        public static final int STR_ARR = 9;
        public static final int TYPE_BASE = 1000;
        public String mName;
        public int mType;
        public String mTypeStr;

        public TypedValue(String str, String str2) {
            initInner(str, str2);
        }

        public TypedValue(Element element) {
            if (element != null) {
                initInner(element.getAttribute(CloudPushConstants.XML_NAME), element.getAttribute("type"));
                return;
            }
            throw new NullPointerException("node is null");
        }

        private void initInner(String str, String str2) {
            this.mName = str;
            this.mTypeStr = str2;
            this.mType = parseType(this.mTypeStr);
        }

        public boolean isArray() {
            int i = this.mType;
            return i == 8 || i == 9;
        }

        public boolean isNumber() {
            int i = this.mType;
            return i >= 3 && i <= 6;
        }

        /* access modifiers changed from: protected */
        public int parseType(String str) {
            if ("string".equalsIgnoreCase(str)) {
                return 2;
            }
            if ("double".equalsIgnoreCase(str) || "number".equalsIgnoreCase(str)) {
                return 6;
            }
            if ("float".equalsIgnoreCase(str)) {
                return 5;
            }
            if ("int".equalsIgnoreCase(str) || "integer".equalsIgnoreCase(str)) {
                return 3;
            }
            if ("long".equalsIgnoreCase(str)) {
                return 4;
            }
            if ("bitmap".equalsIgnoreCase(str)) {
                return 7;
            }
            if ("number[]".equalsIgnoreCase(str)) {
                return 8;
            }
            return "string[]".equalsIgnoreCase(str) ? 9 : 6;
        }
    }

    public static class Variable extends TypedValue {
        public static final String TAG_NAME = "Variable";
        private Expression mArrayIndex;
        protected double mDefNumberValue;
        protected String mDefStringValue;
        protected IndexedVariable mVar;

        public Variable(String str, String str2, Variables variables) {
            super(str, str2);
            this.mVar = new IndexedVariable(this.mName, variables, isNumber());
        }

        public Variable(Element element, Variables variables) {
            super(element);
            this.mArrayIndex = Expression.build(variables, element.getAttribute("arrIndex"));
            this.mVar = new IndexedVariable(this.mName, variables, isNumber() && this.mArrayIndex == null);
            this.mDefStringValue = element.getAttribute("default");
            if (isNumber()) {
                try {
                    this.mDefNumberValue = Double.parseDouble(this.mDefStringValue);
                } catch (NumberFormatException unused) {
                    this.mDefStringValue = null;
                    this.mDefNumberValue = 0.0d;
                }
            }
        }

        public double getNumber() {
            if (isNumber()) {
                Expression expression = this.mArrayIndex;
                return expression != null ? this.mVar.getArrDouble((int) expression.evaluate()) : this.mVar.getDouble();
            }
            Expression expression2 = this.mArrayIndex;
            return expression2 != null ? Utils.stringToDouble(this.mVar.getArrString((int) expression2.evaluate()), 0.0d) : Utils.stringToDouble(this.mVar.getString(), 0.0d);
        }

        public void set(double d2) {
            Expression expression = this.mArrayIndex;
            if (expression != null) {
                this.mVar.setArr((int) expression.evaluate(), d2);
            } else {
                this.mVar.set(d2);
            }
        }

        public void set(Object obj) {
            if (isNumber()) {
                double d2 = 0.0d;
                if (obj instanceof String) {
                    try {
                        d2 = Utils.parseDouble((String) obj);
                    } catch (NumberFormatException unused) {
                    }
                } else if (obj instanceof Number) {
                    d2 = ((Number) obj).doubleValue();
                }
                Expression expression = this.mArrayIndex;
                if (expression != null) {
                    this.mVar.setArr((int) expression.evaluate(), d2);
                } else {
                    this.mVar.set(d2);
                }
            } else {
                if (obj instanceof Number) {
                    obj = Utils.numberToString((Number) obj);
                }
                Expression expression2 = this.mArrayIndex;
                if (expression2 != null) {
                    this.mVar.setArr((int) expression2.evaluate(), obj);
                } else {
                    this.mVar.set(obj);
                }
            }
        }
    }

    public VariableBinder(Element element, ScreenElementRoot screenElementRoot) {
        this.mRoot = screenElementRoot;
        if (element != null) {
            this.mName = element.getAttribute(CloudPushConstants.XML_NAME);
            this.mDependency = element.getAttribute("dependency");
            this.mQueryAtStart = !"false".equalsIgnoreCase(element.getAttribute("queryAtStart"));
            this.mTrigger = CommandTrigger.fromParentElement(element, this.mRoot);
        }
    }

    public final void accept(VariableBinderVisitor variableBinderVisitor) {
        variableBinderVisitor.visit(this);
    }

    /* access modifiers changed from: protected */
    public void addVariable(Variable variable) {
        this.mVariables.add(variable);
    }

    public void finish() {
        CommandTrigger commandTrigger = this.mTrigger;
        if (commandTrigger != null) {
            commandTrigger.finish();
        }
        this.mFinished = true;
    }

    /* access modifiers changed from: protected */
    public ScreenContext getContext() {
        return this.mRoot.getContext();
    }

    public String getDependency() {
        return this.mDependency;
    }

    public String getName() {
        return this.mName;
    }

    public Variables getVariables() {
        return this.mRoot.getVariables();
    }

    public void init() {
        this.mFinished = false;
        this.mPaused = false;
        CommandTrigger commandTrigger = this.mTrigger;
        if (commandTrigger != null) {
            commandTrigger.init();
        }
        if (TextUtils.isEmpty(getDependency()) && this.mQueryAtStart) {
            startQuery();
        }
    }

    /* access modifiers changed from: protected */
    public void loadVariables(Element element) {
        Utils.traverseXmlElementChildren(element, Variable.TAG_NAME, new Utils.XmlTraverseListener() {
            public void onChild(Element element) {
                Variable onLoadVariable = VariableBinder.this.onLoadVariable(element);
                if (onLoadVariable != null) {
                    VariableBinder.this.mVariables.add(onLoadVariable);
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public Variable onLoadVariable(Element element) {
        return null;
    }

    /* access modifiers changed from: protected */
    public final void onUpdateComplete() {
        CommandTrigger commandTrigger = this.mTrigger;
        if (commandTrigger != null) {
            commandTrigger.perform();
        }
        if (this.mQueryCompletedListener != null && !TextUtils.isEmpty(this.mName)) {
            this.mQueryCompletedListener.onQueryCompleted(this.mName);
        }
        this.mRoot.requestUpdate();
    }

    public void pause() {
        CommandTrigger commandTrigger = this.mTrigger;
        if (commandTrigger != null) {
            commandTrigger.pause();
        }
        this.mPaused = true;
    }

    public void refresh() {
    }

    public void resume() {
        CommandTrigger commandTrigger = this.mTrigger;
        if (commandTrigger != null) {
            commandTrigger.resume();
        }
        this.mPaused = false;
    }

    public void setQueryCompleteListener(ContentProviderBinder.QueryCompleteListener queryCompleteListener) {
        this.mQueryCompletedListener = queryCompleteListener;
    }

    public void startQuery() {
    }

    public void tick() {
    }
}
