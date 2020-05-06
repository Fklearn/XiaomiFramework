package com.miui.maml.util;

import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.data.VariableBinder;

public class Variable {
    private String mObjectName;
    private String mPropertyName;

    public Variable(String str) {
        int indexOf = str.indexOf(46);
        if (indexOf == -1) {
            this.mObjectName = null;
            this.mPropertyName = str;
        } else {
            this.mObjectName = str.substring(0, indexOf);
            this.mPropertyName = str.substring(indexOf + 1);
        }
        if (TextUtils.isEmpty(this.mPropertyName)) {
            Log.e(VariableBinder.Variable.TAG_NAME, "invalid variable name:" + str);
        }
    }

    public String getObjName() {
        return this.mObjectName;
    }

    public String getPropertyName() {
        return this.mPropertyName;
    }
}
