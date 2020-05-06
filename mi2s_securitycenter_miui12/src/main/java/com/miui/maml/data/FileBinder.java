package com.miui.maml.data;

import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.VariableBinder;
import com.miui.maml.util.FilenameExtFilter;
import com.miui.maml.util.TextFormatter;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.Element;

public class FileBinder extends VariableBinder {
    private static final boolean DBG = false;
    private static final String LOG_TAG = "FileBinder";
    public static final String TAG_NAME = "FileBinder";
    private IndexedVariable mCountVar;
    protected TextFormatter mDirFormatter;
    private String[] mFiles;
    private String[] mFilters;
    private ArrayList<Variable> mVariables = new ArrayList<>();

    private static class Variable extends VariableBinder.Variable {
        public Expression mIndex;

        public Variable(Element element, Variables variables) {
            super(element, variables);
            this.mIndex = Expression.build(variables, element.getAttribute("index"));
            if (this.mIndex == null) {
                Log.e(VariableBinder.Variable.TAG_NAME, "fail to load file index expression");
            }
        }

        /* access modifiers changed from: protected */
        public int parseType(String str) {
            return 2;
        }
    }

    public FileBinder(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        load(element);
    }

    private void load(Element element) {
        if (element == null) {
            Log.e("FileBinder", "FileBinder node is null");
            return;
        }
        String trim = element.getAttribute("filter").trim();
        this.mFilters = TextUtils.isEmpty(trim) ? null : trim.split(",");
        this.mDirFormatter = new TextFormatter(getVariables(), element.getAttribute("dir"), Expression.build(getVariables(), element.getAttribute("dirExp")));
        if (!TextUtils.isEmpty(this.mName)) {
            this.mCountVar = new IndexedVariable(this.mName + ".count", getContext().mVariables, true);
        }
        loadVariables(element);
    }

    private void updateVariables() {
        String[] strArr = this.mFiles;
        int length = strArr == null ? 0 : strArr.length;
        Iterator<Variable> it = this.mVariables.iterator();
        while (it.hasNext()) {
            Variable next = it.next();
            Expression expression = next.mIndex;
            if (expression != null) {
                next.set((Object) length == 0 ? null : this.mFiles[((int) expression.evaluate()) % length]);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void addVariable(Variable variable) {
        this.mVariables.add(variable);
    }

    public void init() {
        super.init();
        refresh();
    }

    /* access modifiers changed from: protected */
    public Variable onLoadVariable(Element element) {
        return new Variable(element, getVariables());
    }

    public void refresh() {
        super.refresh();
        File file = new File(this.mDirFormatter.getText());
        String[] strArr = this.mFilters;
        this.mFiles = strArr == null ? file.list() : file.list(new FilenameExtFilter(strArr));
        String[] strArr2 = this.mFiles;
        int length = strArr2 == null ? 0 : strArr2.length;
        IndexedVariable indexedVariable = this.mCountVar;
        if (indexedVariable != null) {
            indexedVariable.set((double) length);
        }
        Log.i("FileBinder", "file count: " + length);
        updateVariables();
    }

    public void tick() {
        super.tick();
        updateVariables();
    }
}
