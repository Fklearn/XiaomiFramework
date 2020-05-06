package com.miui.maml.data;

public class IndexedVariable {
    protected int mIndex;
    private boolean mIsNumber;
    protected Variables mVars;

    public IndexedVariable(String str, Variables variables, boolean z) {
        this.mIsNumber = z;
        this.mIndex = this.mIsNumber ? variables.registerDoubleVariable(str) : variables.registerVariable(str);
        this.mVars = variables;
    }

    public final Object get() {
        return this.mVars.get(this.mIndex);
    }

    public final Object getArr(int i) {
        return this.mVars.getArr(this.mIndex, i);
    }

    public final double getArrDouble(int i) {
        return this.mVars.getArrDouble(this.mIndex, i);
    }

    public final String getArrString(int i) {
        return this.mVars.getArrString(this.mIndex, i);
    }

    public final double getDouble() {
        return this.mVars.getDouble(this.mIndex);
    }

    public final int getIndex() {
        return this.mIndex;
    }

    public final String getString() {
        return this.mVars.getString(this.mIndex);
    }

    public final Variables getVariables() {
        return this.mVars;
    }

    public final int getVersion() {
        return this.mVars.getVer(this.mIndex, this.mIsNumber);
    }

    public final boolean isNull() {
        if (this.mIsNumber) {
            if (!this.mVars.existsDouble(this.mIndex)) {
                return true;
            }
        } else if (this.mVars.get(this.mIndex) == null) {
            return true;
        }
        return false;
    }

    public final boolean isNull(int i) {
        if (this.mIsNumber) {
            if (!this.mVars.existsArrItem(this.mIndex, i)) {
                return true;
            }
        } else if (this.mVars.getArr(this.mIndex, i) == null) {
            return true;
        }
        return false;
    }

    public final void set(double d2) {
        this.mVars.put(this.mIndex, d2);
    }

    public final boolean set(Object obj) {
        if (this.mIsNumber) {
            return this.mVars.putDouble(this.mIndex, obj);
        }
        this.mVars.put(this.mIndex, obj);
        return true;
    }

    public final boolean setArr(int i, double d2) {
        return this.mVars.putArr(this.mIndex, i, d2);
    }

    public final boolean setArr(int i, Object obj) {
        return this.mIsNumber ? this.mVars.putArrDouble(this.mIndex, i, obj) : this.mVars.putArr(this.mIndex, i, obj);
    }
}
