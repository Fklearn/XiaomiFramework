package com.miui.maml.data;

import com.miui.maml.data.Expression;
import java.util.HashSet;

public class RootExpression extends Expression {
    public static final String LOG_TAG = "RootExression";
    /* access modifiers changed from: private */
    public boolean mAlwaysEvaluate;
    private double mDoubleValue;
    private Expression mExp;
    private boolean mIsNumInit = false;
    private boolean mIsStrInit = false;
    private String mStringValue;
    private VarVersionVisitor mVarVersionVisitor = null;
    private Variables mVars;
    private HashSet<VarVersion> mVersionSet = new HashSet<>();
    private VarVersion[] mVersions;

    public static class VarVersion {
        int mIndex;
        private boolean mIsNumber;
        int mVersion;

        public VarVersion(int i, int i2, boolean z) {
            this.mIndex = i;
            this.mVersion = i2;
            this.mIsNumber = z;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof VarVersion)) {
                return false;
            }
            VarVersion varVersion = (VarVersion) obj;
            return varVersion.mIsNumber == this.mIsNumber && varVersion.mIndex == this.mIndex;
        }

        public int getVer(Variables variables) {
            return variables.getVer(this.mIndex, this.mIsNumber);
        }

        public int hashCode() {
            return this.mIsNumber ? this.mIndex : (-this.mIndex) - 1;
        }
    }

    private static class VarVersionVisitor extends ExpressionVisitor {
        private RootExpression mRoot;

        public VarVersionVisitor(RootExpression rootExpression) {
            this.mRoot = rootExpression;
        }

        public void visit(Expression expression) {
            if (expression instanceof Expression.VariableExpression) {
                Expression.VariableExpression variableExpression = (Expression.VariableExpression) expression;
                variableExpression.evaluate();
                this.mRoot.addVarVersion(new VarVersion(variableExpression.getIndex(), variableExpression.getVersion(), expression instanceof Expression.NumberVariableExpression));
            } else if (expression instanceof Expression.FunctionExpression) {
                String funName = ((Expression.FunctionExpression) expression).getFunName();
                if ("rand".equals(funName) || "eval".equals(funName) || "preciseeval".equals(funName)) {
                    boolean unused = this.mRoot.mAlwaysEvaluate = true;
                }
            }
        }
    }

    public RootExpression(Variables variables, Expression expression) {
        this.mVars = variables;
        this.mExp = expression;
    }

    public void accept(ExpressionVisitor expressionVisitor) {
    }

    public void addVarVersion(VarVersion varVersion) {
        this.mVersionSet.add(varVersion);
    }

    public double evaluate() {
        boolean z;
        int ver;
        if (!this.mIsNumInit) {
            this.mDoubleValue = this.mExp.evaluate();
            if (this.mVarVersionVisitor == null) {
                this.mVarVersionVisitor = new VarVersionVisitor(this);
                this.mExp.accept(this.mVarVersionVisitor);
                if (this.mVersionSet.size() <= 0) {
                    this.mVersions = null;
                } else {
                    this.mVersions = new VarVersion[this.mVersionSet.size()];
                    this.mVersionSet.toArray(this.mVersions);
                }
            }
            this.mIsNumInit = true;
        } else {
            int i = 0;
            if (this.mAlwaysEvaluate) {
                z = true;
            } else if (this.mVersions != null) {
                z = false;
                while (true) {
                    VarVersion[] varVersionArr = this.mVersions;
                    if (i >= varVersionArr.length) {
                        break;
                    }
                    VarVersion varVersion = varVersionArr[i];
                    if (!(varVersion == null || varVersion.mVersion == (ver = varVersion.getVer(this.mVars)))) {
                        varVersion.mVersion = ver;
                        z = true;
                    }
                    i++;
                }
            } else {
                z = false;
            }
            if (z) {
                this.mDoubleValue = this.mExp.evaluate();
            }
        }
        return this.mDoubleValue;
    }

    public String evaluateStr() {
        boolean z;
        int ver;
        if (!this.mIsStrInit) {
            this.mStringValue = this.mExp.evaluateStr();
            if (this.mVarVersionVisitor == null) {
                this.mVarVersionVisitor = new VarVersionVisitor(this);
                this.mExp.accept(this.mVarVersionVisitor);
                this.mVersions = new VarVersion[this.mVersionSet.size()];
                this.mVersionSet.toArray(this.mVersions);
            }
            this.mIsStrInit = true;
        } else {
            int i = 0;
            if (this.mAlwaysEvaluate) {
                z = true;
            } else if (this.mVersions != null) {
                z = false;
                while (true) {
                    VarVersion[] varVersionArr = this.mVersions;
                    if (i >= varVersionArr.length) {
                        break;
                    }
                    VarVersion varVersion = varVersionArr[i];
                    if (!(varVersion == null || varVersion.mVersion == (ver = varVersion.getVer(this.mVars)))) {
                        varVersion.mVersion = ver;
                        z = true;
                    }
                    i++;
                }
            } else {
                z = false;
            }
            if (z) {
                this.mStringValue = this.mExp.evaluateStr();
            }
        }
        return this.mStringValue;
    }

    public boolean isNull() {
        return this.mExp.isNull();
    }
}
