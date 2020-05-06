package com.miui.maml.util;

import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;

@Deprecated
public class IndexedStringVariable extends IndexedVariable {
    public IndexedStringVariable(String str, Variables variables) {
        super(str, variables, false);
    }
}
