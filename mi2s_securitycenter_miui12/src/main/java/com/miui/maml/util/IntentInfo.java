package com.miui.maml.util;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import com.miui.maml.data.Expression;
import com.miui.maml.data.Variables;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.Element;

public class IntentInfo {
    private static final String LOG_TAG = "TaskVariable";
    private Expression mClassNameExp;
    /* access modifiers changed from: private */
    public ArrayList<Extra> mExtraList = new ArrayList<>();
    private Expression mPackageNameExp;
    private Task mTask;
    private String mUri;
    private Expression mUriExp;
    /* access modifiers changed from: private */
    public Variables mVariables;

    /* renamed from: com.miui.maml.util.IntentInfo$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$util$IntentInfo$Type = new int[Type.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(12:0|1|2|3|4|5|6|7|8|9|10|(3:11|12|14)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0040 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0035 */
        static {
            /*
                com.miui.maml.util.IntentInfo$Type[] r0 = com.miui.maml.util.IntentInfo.Type.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$util$IntentInfo$Type = r0
                int[] r0 = $SwitchMap$com$miui$maml$util$IntentInfo$Type     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.util.IntentInfo$Type r1 = com.miui.maml.util.IntentInfo.Type.STRING     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$maml$util$IntentInfo$Type     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.util.IntentInfo$Type r1 = com.miui.maml.util.IntentInfo.Type.INT     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$maml$util$IntentInfo$Type     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.maml.util.IntentInfo$Type r1 = com.miui.maml.util.IntentInfo.Type.LONG     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                int[] r0 = $SwitchMap$com$miui$maml$util$IntentInfo$Type     // Catch:{ NoSuchFieldError -> 0x0035 }
                com.miui.maml.util.IntentInfo$Type r1 = com.miui.maml.util.IntentInfo.Type.FLOAT     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                int[] r0 = $SwitchMap$com$miui$maml$util$IntentInfo$Type     // Catch:{ NoSuchFieldError -> 0x0040 }
                com.miui.maml.util.IntentInfo$Type r1 = com.miui.maml.util.IntentInfo.Type.DOUBLE     // Catch:{ NoSuchFieldError -> 0x0040 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0040 }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0040 }
            L_0x0040:
                int[] r0 = $SwitchMap$com$miui$maml$util$IntentInfo$Type     // Catch:{ NoSuchFieldError -> 0x004b }
                com.miui.maml.util.IntentInfo$Type r1 = com.miui.maml.util.IntentInfo.Type.BOOLEAN     // Catch:{ NoSuchFieldError -> 0x004b }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x004b }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x004b }
            L_0x004b:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.util.IntentInfo.AnonymousClass2.<clinit>():void");
        }
    }

    private class Extra {
        public static final String TAG_NAME = "Extra";
        private Expression mCondition;
        private Expression mExpression;
        private String mName;
        protected Type mType = Type.DOUBLE;

        public Extra(Element element) {
            load(element);
        }

        /* JADX WARNING: Removed duplicated region for block: B:27:0x007b  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void load(org.w3c.dom.Element r4) {
            /*
                r3 = this;
                java.lang.String r0 = "TaskVariable"
                if (r4 != 0) goto L_0x000a
                java.lang.String r4 = "node is null"
                android.util.Log.e(r0, r4)
                return
            L_0x000a:
                java.lang.String r1 = "name"
                java.lang.String r1 = r4.getAttribute(r1)
                r3.mName = r1
                java.lang.String r1 = "type"
                java.lang.String r1 = r4.getAttribute(r1)
                java.lang.String r2 = "string"
                boolean r2 = r2.equalsIgnoreCase(r1)
                if (r2 == 0) goto L_0x0025
                com.miui.maml.util.IntentInfo$Type r1 = com.miui.maml.util.IntentInfo.Type.STRING
            L_0x0022:
                r3.mType = r1
                goto L_0x0065
            L_0x0025:
                java.lang.String r2 = "int"
                boolean r2 = r2.equalsIgnoreCase(r1)
                if (r2 != 0) goto L_0x0062
                java.lang.String r2 = "integer"
                boolean r2 = r2.equalsIgnoreCase(r1)
                if (r2 == 0) goto L_0x0036
                goto L_0x0062
            L_0x0036:
                java.lang.String r2 = "long"
                boolean r2 = r2.equalsIgnoreCase(r1)
                if (r2 == 0) goto L_0x0041
                com.miui.maml.util.IntentInfo$Type r1 = com.miui.maml.util.IntentInfo.Type.LONG
                goto L_0x0022
            L_0x0041:
                java.lang.String r2 = "float"
                boolean r2 = r2.equalsIgnoreCase(r1)
                if (r2 == 0) goto L_0x004c
                com.miui.maml.util.IntentInfo$Type r1 = com.miui.maml.util.IntentInfo.Type.FLOAT
                goto L_0x0022
            L_0x004c:
                java.lang.String r2 = "double"
                boolean r2 = r2.equalsIgnoreCase(r1)
                if (r2 == 0) goto L_0x0057
                com.miui.maml.util.IntentInfo$Type r1 = com.miui.maml.util.IntentInfo.Type.DOUBLE
                goto L_0x0022
            L_0x0057:
                java.lang.String r2 = "boolean"
                boolean r1 = r2.equalsIgnoreCase(r1)
                if (r1 == 0) goto L_0x0065
                com.miui.maml.util.IntentInfo$Type r1 = com.miui.maml.util.IntentInfo.Type.BOOLEAN
                goto L_0x0022
            L_0x0062:
                com.miui.maml.util.IntentInfo$Type r1 = com.miui.maml.util.IntentInfo.Type.INT
                goto L_0x0022
            L_0x0065:
                com.miui.maml.util.IntentInfo r1 = com.miui.maml.util.IntentInfo.this
                com.miui.maml.data.Variables r1 = r1.mVariables
                java.lang.String r2 = "expression"
                java.lang.String r2 = r4.getAttribute(r2)
                com.miui.maml.data.Expression r1 = com.miui.maml.data.Expression.build(r1, r2)
                r3.mExpression = r1
                com.miui.maml.data.Expression r1 = r3.mExpression
                if (r1 != 0) goto L_0x0080
                java.lang.String r1 = "invalid expression in IntentCommand"
                android.util.Log.e(r0, r1)
            L_0x0080:
                com.miui.maml.util.IntentInfo r0 = com.miui.maml.util.IntentInfo.this
                com.miui.maml.data.Variables r0 = r0.mVariables
                java.lang.String r1 = "condition"
                java.lang.String r4 = r4.getAttribute(r1)
                com.miui.maml.data.Expression r4 = com.miui.maml.data.Expression.build(r0, r4)
                r3.mCondition = r4
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.util.IntentInfo.Extra.load(org.w3c.dom.Element):void");
        }

        public double getDouble() {
            Expression expression = this.mExpression;
            if (expression == null) {
                return 0.0d;
            }
            return expression.evaluate();
        }

        public String getName() {
            return this.mName;
        }

        public String getString() {
            Expression expression = this.mExpression;
            if (expression == null) {
                return null;
            }
            return expression.evaluateStr();
        }

        public boolean isConditionTrue() {
            Expression expression = this.mCondition;
            return expression == null || expression.evaluate() > 0.0d;
        }
    }

    private enum Type {
        STRING,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        BOOLEAN
    }

    public IntentInfo(Element element, Variables variables) {
        if (element != null) {
            this.mTask = Task.load(element);
            this.mVariables = variables;
            this.mPackageNameExp = Expression.build(variables, element.getAttribute("packageExp"));
            this.mClassNameExp = Expression.build(variables, element.getAttribute("classExp"));
            this.mUri = element.getAttribute("uri");
            this.mUriExp = Expression.build(variables, element.getAttribute("uriExp"));
            loadExtras(element);
        }
    }

    private void loadExtras(Element element) {
        Utils.traverseXmlElementChildren(element, Extra.TAG_NAME, new Utils.XmlTraverseListener() {
            public void onChild(Element element) {
                IntentInfo.this.mExtraList.add(new Extra(element));
            }
        });
    }

    public String getAction() {
        Task task = this.mTask;
        if (task != null) {
            return task.action;
        }
        return null;
    }

    public String getId() {
        Task task = this.mTask;
        if (task != null) {
            return task.id;
        }
        return null;
    }

    public void set(Task task) {
        this.mTask = task;
    }

    public void update(Intent intent) {
        Task task = this.mTask;
        String str = null;
        String str2 = task != null ? task.action : null;
        if (!TextUtils.isEmpty(str2)) {
            intent.setAction(str2);
        }
        Task task2 = this.mTask;
        String str3 = task2 != null ? task2.type : null;
        if (!TextUtils.isEmpty(str3)) {
            intent.setType(str3);
        }
        Task task3 = this.mTask;
        String str4 = task3 != null ? task3.category : null;
        if (!TextUtils.isEmpty(str4)) {
            intent.addCategory(str4);
        }
        Task task4 = this.mTask;
        String str5 = task4 != null ? task4.packageName : null;
        Expression expression = this.mPackageNameExp;
        if (expression != null) {
            str5 = expression.evaluateStr();
        }
        Task task5 = this.mTask;
        if (task5 != null) {
            str = task5.className;
        }
        Expression expression2 = this.mClassNameExp;
        if (expression2 != null) {
            str = expression2.evaluateStr();
        }
        if (!TextUtils.isEmpty(str5)) {
            if (!TextUtils.isEmpty(str)) {
                intent.setClassName(str5, str);
            } else {
                intent.setPackage(str5);
            }
        }
        CustomUtils.replaceCameraIntentInfoOnF3M(str5, str, intent);
        String str6 = this.mUri;
        Expression expression3 = this.mUriExp;
        if (expression3 != null) {
            str6 = expression3.evaluateStr();
        }
        if (!TextUtils.isEmpty(str6)) {
            intent.setData(Uri.parse(str6));
        }
        ArrayList<Extra> arrayList = this.mExtraList;
        if (arrayList != null) {
            Iterator<Extra> it = arrayList.iterator();
            while (it.hasNext()) {
                Extra next = it.next();
                if (next.isConditionTrue()) {
                    switch (AnonymousClass2.$SwitchMap$com$miui$maml$util$IntentInfo$Type[next.mType.ordinal()]) {
                        case 1:
                            intent.putExtra(next.getName(), next.getString());
                            break;
                        case 2:
                            intent.putExtra(next.getName(), (int) next.getDouble());
                            break;
                        case 3:
                            intent.putExtra(next.getName(), (long) next.getDouble());
                            break;
                        case 4:
                            intent.putExtra(next.getName(), (float) next.getDouble());
                            break;
                        case 5:
                            intent.putExtra(next.getName(), next.getDouble());
                            break;
                        case 6:
                            intent.putExtra(next.getName(), next.getDouble() > 0.0d);
                            break;
                    }
                } else {
                    intent.removeExtra(next.getName());
                }
            }
        }
    }
}
