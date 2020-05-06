package com.miui.maml.elements;

import android.graphics.Canvas;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import com.miui.maml.elements.ListScreenElement;
import com.miui.maml.util.Utils;
import com.xiaomi.stat.MiStat;
import java.util.ArrayList;
import java.util.HashSet;
import miui.cloud.CloudPushConstants;
import org.w3c.dom.Element;

public class VariableArrayElement extends ScreenElement {
    public static final String TAG_NAME = "VarArray";
    /* access modifiers changed from: private */
    public ArrayList<Item> mArray = new ArrayList<>();
    Object[] mData;
    private int mItemCount;
    private IndexedVariable mItemCountVar;
    Type mType = Type.DOUBLE;
    HashSet<VarObserver> mVarObserver = new HashSet<>();
    /* access modifiers changed from: private */
    public ArrayList<Var> mVars = new ArrayList<>();

    private class Item {
        public Expression mExpression;
        public Object mValue;

        public Item(Variables variables, Element element) {
            if (element != null) {
                this.mExpression = Expression.build(variables, element.getAttribute("expression"));
                String attribute = element.getAttribute(MiStat.Param.VALUE);
                if (VariableArrayElement.this.mType == Type.DOUBLE) {
                    try {
                        this.mValue = Double.valueOf(Double.parseDouble(attribute));
                    } catch (NumberFormatException unused) {
                    }
                } else {
                    this.mValue = attribute;
                }
            }
        }

        public Item(Object obj) {
            this.mValue = obj;
            this.mExpression = null;
        }

        public Double evaluate() {
            Expression expression = this.mExpression;
            if (expression == null) {
                Object obj = this.mValue;
                if (obj instanceof Number) {
                    return Double.valueOf(((Number) obj).doubleValue());
                }
                return null;
            } else if (expression.isNull()) {
                return null;
            } else {
                return Double.valueOf(this.mExpression.evaluate());
            }
        }

        public String evaluateStr() {
            Expression expression = this.mExpression;
            if (expression != null) {
                return expression.evaluateStr();
            }
            Object obj = this.mValue;
            if (obj instanceof String) {
                return (String) obj;
            }
            return null;
        }

        public boolean isExpression() {
            return this.mExpression != null;
        }
    }

    public enum Type {
        DOUBLE,
        STRING
    }

    private class Var {
        private boolean mConst;
        private boolean mCurrentItemIsExpression;
        private int mIndex = -1;
        private Expression mIndexExpression;
        private String mName;
        private IndexedVariable mVar;

        public Var(Variables variables, Element element) {
            if (element != null) {
                this.mName = element.getAttribute(CloudPushConstants.XML_NAME);
                this.mIndexExpression = Expression.build(variables, element.getAttribute("index"));
                this.mConst = Boolean.parseBoolean(element.getAttribute("const"));
                this.mVar = new IndexedVariable(this.mName, VariableArrayElement.this.getVariables(), VariableArrayElement.this.mType != Type.STRING);
            }
        }

        private void update() {
            IndexedVariable indexedVariable;
            Object evaluate;
            Expression expression = this.mIndexExpression;
            if (expression != null) {
                int evaluate2 = (int) expression.evaluate();
                if (evaluate2 < 0 || evaluate2 >= VariableArrayElement.this.mArray.size()) {
                    Type type = VariableArrayElement.this.mType;
                    if (type == Type.STRING) {
                        this.mVar.set((Object) null);
                    } else if (type == Type.DOUBLE) {
                        this.mVar.set(0.0d);
                    }
                } else if (this.mIndex != evaluate2 || this.mCurrentItemIsExpression) {
                    Item item = (Item) VariableArrayElement.this.mArray.get(evaluate2);
                    if (this.mIndex != evaluate2) {
                        this.mIndex = evaluate2;
                        this.mCurrentItemIsExpression = item.isExpression();
                    }
                    Type type2 = VariableArrayElement.this.mType;
                    if (type2 == Type.STRING) {
                        indexedVariable = this.mVar;
                        evaluate = item.evaluateStr();
                    } else if (type2 == Type.DOUBLE) {
                        indexedVariable = this.mVar;
                        evaluate = item.evaluate();
                    } else {
                        return;
                    }
                    indexedVariable.set(evaluate);
                }
            }
        }

        public void init() {
            this.mIndex = -1;
            update();
        }

        public void tick() {
            if (!this.mConst) {
                update();
            }
        }
    }

    public interface VarObserver {
        void onDataChange(Object[] objArr);
    }

    public VariableArrayElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        if (element != null) {
            this.mType = "string".equalsIgnoreCase(element.getAttribute("type")) ? Type.STRING : Type.DOUBLE;
            final Variables variables = getVariables();
            Utils.traverseXmlElementChildren(Utils.getChild(element, "Vars"), VariableElement.TAG_NAME, new Utils.XmlTraverseListener() {
                public void onChild(Element element) {
                    VariableArrayElement.this.mVars.add(new Var(variables, element));
                }
            });
            Utils.traverseXmlElementChildren(Utils.getChild(element, "Items"), ListScreenElement.ListItemElement.TAG_NAME, new Utils.XmlTraverseListener() {
                public void onChild(Element element) {
                    VariableArrayElement.this.mArray.add(new Item(variables, element));
                }
            });
            if (this.mHasName) {
                this.mItemCountVar = new IndexedVariable(this.mName + ".count", variables, true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void doRender(Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public void doTick(long j) {
        int size = this.mVars.size();
        for (int i = 0; i < size; i++) {
            this.mVars.get(i).tick();
        }
    }

    public int getItemSize() {
        return this.mItemCount;
    }

    public void init() {
        super.init();
        int size = this.mVars.size();
        for (int i = 0; i < size; i++) {
            this.mVars.get(i).init();
        }
        this.mItemCount = this.mArray.size();
        IndexedVariable indexedVariable = this.mItemCountVar;
        if (indexedVariable != null) {
            indexedVariable.set((double) this.mItemCount);
        }
        if (this.mData == null) {
            this.mData = new Object[this.mItemCount];
            for (int i2 = 0; i2 < this.mItemCount; i2++) {
                this.mData[i2] = this.mArray.get(i2).mValue;
            }
        }
    }

    public void registerVarObserver(VarObserver varObserver, boolean z) {
        if (varObserver != null) {
            if (z) {
                this.mVarObserver.add(varObserver);
                varObserver.onDataChange(this.mData);
                return;
            }
            this.mVarObserver.remove(varObserver);
        }
    }
}
