package com.miui.maml.data;

import android.util.Log;
import com.miui.maml.data.Expression;
import com.miui.maml.util.Utils;
import java.util.regex.PatternSyntaxException;

public class StringFunctions extends Expression.FunctionImpl {
    private static final String LOG_TAG = "Expression";
    private final Fun mFun;

    /* renamed from: com.miui.maml.data.StringFunctions$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$StringFunctions$Fun = new int[Fun.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(26:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|(3:25|26|28)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(28:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|28) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0040 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x004b */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0056 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0062 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x006e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x007a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:23:0x0086 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:25:0x0092 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0035 */
        static {
            /*
                com.miui.maml.data.StringFunctions$Fun[] r0 = com.miui.maml.data.StringFunctions.Fun.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$data$StringFunctions$Fun = r0
                int[] r0 = $SwitchMap$com$miui$maml$data$StringFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.data.StringFunctions$Fun r1 = com.miui.maml.data.StringFunctions.Fun.STR_CONTAINS     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$maml$data$StringFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.data.StringFunctions$Fun r1 = com.miui.maml.data.StringFunctions.Fun.STR_STARTWITH     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$maml$data$StringFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.maml.data.StringFunctions$Fun r1 = com.miui.maml.data.StringFunctions.Fun.STR_ENDSWITH     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                int[] r0 = $SwitchMap$com$miui$maml$data$StringFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0035 }
                com.miui.maml.data.StringFunctions$Fun r1 = com.miui.maml.data.StringFunctions.Fun.STR_MATCHES     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                int[] r0 = $SwitchMap$com$miui$maml$data$StringFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0040 }
                com.miui.maml.data.StringFunctions$Fun r1 = com.miui.maml.data.StringFunctions.Fun.STR_INDEXOF     // Catch:{ NoSuchFieldError -> 0x0040 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0040 }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0040 }
            L_0x0040:
                int[] r0 = $SwitchMap$com$miui$maml$data$StringFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x004b }
                com.miui.maml.data.StringFunctions$Fun r1 = com.miui.maml.data.StringFunctions.Fun.STR_LASTINDEXOF     // Catch:{ NoSuchFieldError -> 0x004b }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x004b }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x004b }
            L_0x004b:
                int[] r0 = $SwitchMap$com$miui$maml$data$StringFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0056 }
                com.miui.maml.data.StringFunctions$Fun r1 = com.miui.maml.data.StringFunctions.Fun.STR_ISEMPTY     // Catch:{ NoSuchFieldError -> 0x0056 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0056 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0056 }
            L_0x0056:
                int[] r0 = $SwitchMap$com$miui$maml$data$StringFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0062 }
                com.miui.maml.data.StringFunctions$Fun r1 = com.miui.maml.data.StringFunctions.Fun.STR_REPLACE     // Catch:{ NoSuchFieldError -> 0x0062 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0062 }
                r2 = 8
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0062 }
            L_0x0062:
                int[] r0 = $SwitchMap$com$miui$maml$data$StringFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x006e }
                com.miui.maml.data.StringFunctions$Fun r1 = com.miui.maml.data.StringFunctions.Fun.STR_REPLACEALL     // Catch:{ NoSuchFieldError -> 0x006e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x006e }
                r2 = 9
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x006e }
            L_0x006e:
                int[] r0 = $SwitchMap$com$miui$maml$data$StringFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x007a }
                com.miui.maml.data.StringFunctions$Fun r1 = com.miui.maml.data.StringFunctions.Fun.STR_REPLACEFIRST     // Catch:{ NoSuchFieldError -> 0x007a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x007a }
                r2 = 10
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x007a }
            L_0x007a:
                int[] r0 = $SwitchMap$com$miui$maml$data$StringFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0086 }
                com.miui.maml.data.StringFunctions$Fun r1 = com.miui.maml.data.StringFunctions.Fun.STR_TOLOWER     // Catch:{ NoSuchFieldError -> 0x0086 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0086 }
                r2 = 11
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0086 }
            L_0x0086:
                int[] r0 = $SwitchMap$com$miui$maml$data$StringFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0092 }
                com.miui.maml.data.StringFunctions$Fun r1 = com.miui.maml.data.StringFunctions.Fun.STR_TOUPPER     // Catch:{ NoSuchFieldError -> 0x0092 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0092 }
                r2 = 12
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0092 }
            L_0x0092:
                int[] r0 = $SwitchMap$com$miui$maml$data$StringFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x009e }
                com.miui.maml.data.StringFunctions$Fun r1 = com.miui.maml.data.StringFunctions.Fun.STR_TRIM     // Catch:{ NoSuchFieldError -> 0x009e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x009e }
                r2 = 13
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x009e }
            L_0x009e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.StringFunctions.AnonymousClass1.<clinit>():void");
        }
    }

    private enum Fun {
        INVALID,
        STR_TOLOWER,
        STR_TOUPPER,
        STR_TRIM,
        STR_REPLACE,
        STR_REPLACEALL,
        STR_REPLACEFIRST,
        STR_CONTAINS,
        STR_STARTWITH,
        STR_ENDSWITH,
        STR_ISEMPTY,
        STR_MATCHES,
        STR_INDEXOF,
        STR_LASTINDEXOF
    }

    private StringFunctions(Fun fun, int i) {
        super(i);
        this.mFun = fun;
    }

    public static void load() {
        Expression.FunctionExpression.registerFunction("strToLowerCase", new StringFunctions(Fun.STR_TOLOWER, 1));
        Expression.FunctionExpression.registerFunction("strToUpperCase", new StringFunctions(Fun.STR_TOUPPER, 1));
        Expression.FunctionExpression.registerFunction("strTrim", new StringFunctions(Fun.STR_TRIM, 1));
        Expression.FunctionExpression.registerFunction("strReplace", new StringFunctions(Fun.STR_REPLACE, 3));
        Expression.FunctionExpression.registerFunction("strReplaceAll", new StringFunctions(Fun.STR_REPLACEALL, 3));
        Expression.FunctionExpression.registerFunction("strReplaceFirst", new StringFunctions(Fun.STR_REPLACEFIRST, 3));
        Expression.FunctionExpression.registerFunction("strContains", new StringFunctions(Fun.STR_CONTAINS, 2));
        Expression.FunctionExpression.registerFunction("strStartsWith", new StringFunctions(Fun.STR_STARTWITH, 2));
        Expression.FunctionExpression.registerFunction("strEndsWith", new StringFunctions(Fun.STR_ENDSWITH, 2));
        Expression.FunctionExpression.registerFunction("strIsEmpty", new StringFunctions(Fun.STR_ISEMPTY, 1));
        Expression.FunctionExpression.registerFunction("strMatches", new StringFunctions(Fun.STR_MATCHES, 2));
        Expression.FunctionExpression.registerFunction("strIndexOf", new StringFunctions(Fun.STR_INDEXOF, 2));
        Expression.FunctionExpression.registerFunction("strLastIndexOf", new StringFunctions(Fun.STR_LASTINDEXOF, 2));
    }

    public double evaluate(Expression[] expressionArr, Variables variables) {
        switch (AnonymousClass1.$SwitchMap$com$miui$maml$data$StringFunctions$Fun[this.mFun.ordinal()]) {
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
                return Utils.stringToDouble(evaluateStr(expressionArr, variables), 0.0d);
            default:
                String evaluateStr = expressionArr[0].evaluateStr();
                if (AnonymousClass1.$SwitchMap$com$miui$maml$data$StringFunctions$Fun[this.mFun.ordinal()] == 7) {
                    return (evaluateStr == null || evaluateStr.isEmpty()) ? 1.0d : 0.0d;
                }
                String evaluateStr2 = expressionArr[1].evaluateStr();
                switch (AnonymousClass1.$SwitchMap$com$miui$maml$data$StringFunctions$Fun[this.mFun.ordinal()]) {
                    case 1:
                        return (evaluateStr == null || evaluateStr2 == null || !evaluateStr.contains(evaluateStr2)) ? 0.0d : 1.0d;
                    case 2:
                        return (evaluateStr == null || evaluateStr2 == null || !evaluateStr.startsWith(evaluateStr2)) ? 0.0d : 1.0d;
                    case 3:
                        return (evaluateStr == null || evaluateStr2 == null || !evaluateStr.endsWith(evaluateStr2)) ? 0.0d : 1.0d;
                    case 4:
                        if (evaluateStr == null || evaluateStr2 == null) {
                            return 0.0d;
                        }
                        try {
                            return evaluateStr.matches(evaluateStr2) ? 1.0d : 0.0d;
                        } catch (PatternSyntaxException unused) {
                            Log.w(LOG_TAG, "invaid pattern of matches: " + evaluateStr2);
                            return 0.0d;
                        }
                    case 5:
                        if (evaluateStr == null || evaluateStr2 == null) {
                            return -1.0d;
                        }
                        return (double) evaluateStr.indexOf(evaluateStr2);
                    case 6:
                        if (evaluateStr == null || evaluateStr2 == null) {
                            return -1.0d;
                        }
                        return (double) evaluateStr.lastIndexOf(evaluateStr2);
                    default:
                        Log.e(LOG_TAG, "fail to evalute FunctionExpression, invalid function: " + this.mFun.toString());
                        return 0.0d;
                }
        }
    }

    public String evaluateStr(Expression[] expressionArr, Variables variables) {
        StringBuilder sb;
        String str;
        switch (AnonymousClass1.$SwitchMap$com$miui$maml$data$StringFunctions$Fun[this.mFun.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                return Utils.doubleToString(evaluate(expressionArr, variables));
            default:
                String evaluateStr = expressionArr[0].evaluateStr();
                if (evaluateStr == null) {
                    return null;
                }
                switch (AnonymousClass1.$SwitchMap$com$miui$maml$data$StringFunctions$Fun[this.mFun.ordinal()]) {
                    case 11:
                        return evaluateStr.toLowerCase();
                    case 12:
                        return evaluateStr.toUpperCase();
                    case 13:
                        return evaluateStr.trim();
                    default:
                        String evaluateStr2 = expressionArr[1].evaluateStr();
                        String evaluateStr3 = expressionArr[2].evaluateStr();
                        if (evaluateStr2 == null || evaluateStr3 == null) {
                            return evaluateStr;
                        }
                        switch (AnonymousClass1.$SwitchMap$com$miui$maml$data$StringFunctions$Fun[this.mFun.ordinal()]) {
                            case 8:
                                return evaluateStr.replace(evaluateStr2, evaluateStr3);
                            case 9:
                                try {
                                    return evaluateStr.replaceAll(evaluateStr2, evaluateStr3);
                                } catch (PatternSyntaxException unused) {
                                    sb = new StringBuilder();
                                    str = "invaid pattern of replaceAll: ";
                                    break;
                                }
                            case 10:
                                try {
                                    return evaluateStr.replaceFirst(evaluateStr2, evaluateStr3);
                                } catch (PatternSyntaxException unused2) {
                                    sb = new StringBuilder();
                                    str = "invaid pattern of replaceFirst:";
                                    break;
                                }
                            default:
                                Log.e(LOG_TAG, "fail to evaluteStr FunctionExpression, invalid function: " + this.mFun.toString());
                                return null;
                        }
                        sb.append(str);
                        sb.append(evaluateStr2);
                        Log.w(LOG_TAG, sb.toString());
                        return evaluateStr;
                }
        }
    }

    public void reset() {
    }
}
