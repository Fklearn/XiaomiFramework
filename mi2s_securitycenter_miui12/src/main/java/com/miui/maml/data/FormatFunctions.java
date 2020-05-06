package com.miui.maml.data;

import com.miui.maml.data.Expression;
import java.util.IllegalFormatException;

public class FormatFunctions extends Expression.FunctionImpl {
    private final Fun mFun;

    /* renamed from: com.miui.maml.data.FormatFunctions$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$FormatFunctions$Fun = new int[Fun.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|8) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        static {
            /*
                com.miui.maml.data.FormatFunctions$Fun[] r0 = com.miui.maml.data.FormatFunctions.Fun.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$data$FormatFunctions$Fun = r0
                int[] r0 = $SwitchMap$com$miui$maml$data$FormatFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.data.FormatFunctions$Fun r1 = com.miui.maml.data.FormatFunctions.Fun.FORMAT_DATE     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$maml$data$FormatFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.data.FormatFunctions$Fun r1 = com.miui.maml.data.FormatFunctions.Fun.FORMAT_FLOAT     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$maml$data$FormatFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.maml.data.FormatFunctions$Fun r1 = com.miui.maml.data.FormatFunctions.Fun.FORMAT_INT     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.FormatFunctions.AnonymousClass1.<clinit>():void");
        }
    }

    private enum Fun {
        INVALID,
        FORMAT_DATE,
        FORMAT_FLOAT,
        FORMAT_INT
    }

    private FormatFunctions(Fun fun, int i) {
        super(i);
        this.mFun = fun;
    }

    public static void load() {
        Expression.FunctionExpression.registerFunction("formatDate", new FormatFunctions(Fun.FORMAT_DATE, 2));
        Expression.FunctionExpression.registerFunction("formatFloat", new FormatFunctions(Fun.FORMAT_FLOAT, 2));
        Expression.FunctionExpression.registerFunction("formatInt", new FormatFunctions(Fun.FORMAT_INT, 2));
    }

    public double evaluate(Expression[] expressionArr, Variables variables) {
        return 0.0d;
    }

    public String evaluateStr(Expression[] expressionArr, Variables variables) {
        String evaluateStr = expressionArr[0].evaluateStr();
        if (evaluateStr == null) {
            return null;
        }
        int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$FormatFunctions$Fun[this.mFun.ordinal()];
        if (i == 1) {
            return DateTimeVariableUpdater.formatDate(evaluateStr, (long) expressionArr[1].evaluate());
        }
        if (i != 2) {
            if (i == 3) {
                try {
                    return String.format(evaluateStr, new Object[]{Integer.valueOf((int) expressionArr[1].evaluate())});
                } catch (IllegalFormatException unused) {
                }
            }
            return null;
        }
        return String.format(evaluateStr, new Object[]{Double.valueOf(expressionArr[1].evaluate())});
    }

    public void reset() {
    }
}
