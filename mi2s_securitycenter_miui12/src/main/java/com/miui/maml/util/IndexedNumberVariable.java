package com.miui.maml.util;

import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;

@Deprecated
public class IndexedNumberVariable extends IndexedVariable {
    public IndexedNumberVariable(String str, Variables variables) {
        super(str, variables, true);
    }
}
