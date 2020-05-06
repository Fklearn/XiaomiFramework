package com.miui.maml.data;

import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.data.Expression;
import com.miui.maml.util.Utils;
import com.xiaomi.stat.d;
import java.math.BigDecimal;
import miui.util.HashUtils;

public class BaseFunctions extends Expression.FunctionImpl {
    private static final String LOG_TAG = "Expression";
    private Fun fun;
    private Expression mEvalExp;
    private String mEvalExpStr;

    /* renamed from: com.miui.maml.data.BaseFunctions$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$BaseFunctions$Fun = new int[Fun.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(78:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|61|62|63|64|65|66|67|68|69|70|71|72|73|74|75|76|(3:77|78|80)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(80:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|61|62|63|64|65|66|67|68|69|70|71|72|73|74|75|76|77|78|80) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0040 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x004b */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0056 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0062 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x006e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x007a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:23:0x0086 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:25:0x0092 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:27:0x009e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:29:0x00aa */
        /* JADX WARNING: Missing exception handler attribute for start block: B:31:0x00b6 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:33:0x00c2 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:35:0x00ce */
        /* JADX WARNING: Missing exception handler attribute for start block: B:37:0x00da */
        /* JADX WARNING: Missing exception handler attribute for start block: B:39:0x00e6 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:41:0x00f2 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:43:0x00fe */
        /* JADX WARNING: Missing exception handler attribute for start block: B:45:0x010a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:47:0x0116 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:49:0x0122 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:51:0x012e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:53:0x013a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:55:0x0146 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:57:0x0152 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:59:0x015e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:61:0x016a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:63:0x0176 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:65:0x0182 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:67:0x018e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:69:0x019a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:71:0x01a6 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:73:0x01b2 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:75:0x01be */
        /* JADX WARNING: Missing exception handler attribute for start block: B:77:0x01ca */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0035 */
        static {
            /*
                com.miui.maml.data.BaseFunctions$Fun[] r0 = com.miui.maml.data.BaseFunctions.Fun.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun = r0
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.RAND     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.SIN     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.COS     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0035 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.TAN     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0040 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.ASIN     // Catch:{ NoSuchFieldError -> 0x0040 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0040 }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0040 }
            L_0x0040:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x004b }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.ACOS     // Catch:{ NoSuchFieldError -> 0x004b }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x004b }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x004b }
            L_0x004b:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0056 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.ATAN     // Catch:{ NoSuchFieldError -> 0x0056 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0056 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0056 }
            L_0x0056:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0062 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.SINH     // Catch:{ NoSuchFieldError -> 0x0062 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0062 }
                r2 = 8
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0062 }
            L_0x0062:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x006e }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.COSH     // Catch:{ NoSuchFieldError -> 0x006e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x006e }
                r2 = 9
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x006e }
            L_0x006e:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x007a }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.SQRT     // Catch:{ NoSuchFieldError -> 0x007a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x007a }
                r2 = 10
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x007a }
            L_0x007a:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0086 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.ABS     // Catch:{ NoSuchFieldError -> 0x0086 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0086 }
                r2 = 11
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0086 }
            L_0x0086:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0092 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.LEN     // Catch:{ NoSuchFieldError -> 0x0092 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0092 }
                r2 = 12
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0092 }
            L_0x0092:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x009e }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.EVAL     // Catch:{ NoSuchFieldError -> 0x009e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x009e }
                r2 = 13
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x009e }
            L_0x009e:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x00aa }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.PRECISE_EVAL     // Catch:{ NoSuchFieldError -> 0x00aa }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00aa }
                r2 = 14
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x00aa }
            L_0x00aa:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x00b6 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.ROUND     // Catch:{ NoSuchFieldError -> 0x00b6 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00b6 }
                r2 = 15
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x00b6 }
            L_0x00b6:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x00c2 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.INT     // Catch:{ NoSuchFieldError -> 0x00c2 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00c2 }
                r2 = 16
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x00c2 }
            L_0x00c2:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x00ce }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.NUM     // Catch:{ NoSuchFieldError -> 0x00ce }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00ce }
                r2 = 17
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x00ce }
            L_0x00ce:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x00da }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.ISNULL     // Catch:{ NoSuchFieldError -> 0x00da }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00da }
                r2 = 18
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x00da }
            L_0x00da:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x00e6 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.NOT     // Catch:{ NoSuchFieldError -> 0x00e6 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00e6 }
                r2 = 19
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x00e6 }
            L_0x00e6:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x00f2 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.MIN     // Catch:{ NoSuchFieldError -> 0x00f2 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00f2 }
                r2 = 20
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x00f2 }
            L_0x00f2:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x00fe }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.MAX     // Catch:{ NoSuchFieldError -> 0x00fe }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00fe }
                r2 = 21
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x00fe }
            L_0x00fe:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x010a }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.POW     // Catch:{ NoSuchFieldError -> 0x010a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x010a }
                r2 = 22
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x010a }
            L_0x010a:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0116 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.LOG     // Catch:{ NoSuchFieldError -> 0x0116 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0116 }
                r2 = 23
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0116 }
            L_0x0116:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0122 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.LOG10     // Catch:{ NoSuchFieldError -> 0x0122 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0122 }
                r2 = 24
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0122 }
            L_0x0122:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x012e }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.DIGIT     // Catch:{ NoSuchFieldError -> 0x012e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x012e }
                r2 = 25
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x012e }
            L_0x012e:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x013a }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.EQ     // Catch:{ NoSuchFieldError -> 0x013a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x013a }
                r2 = 26
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x013a }
            L_0x013a:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0146 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.NE     // Catch:{ NoSuchFieldError -> 0x0146 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0146 }
                r2 = 27
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0146 }
            L_0x0146:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0152 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.GE     // Catch:{ NoSuchFieldError -> 0x0152 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0152 }
                r2 = 28
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0152 }
            L_0x0152:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x015e }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.GT     // Catch:{ NoSuchFieldError -> 0x015e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x015e }
                r2 = 29
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x015e }
            L_0x015e:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x016a }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.LE     // Catch:{ NoSuchFieldError -> 0x016a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x016a }
                r2 = 30
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x016a }
            L_0x016a:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0176 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.LT     // Catch:{ NoSuchFieldError -> 0x0176 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0176 }
                r2 = 31
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0176 }
            L_0x0176:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x0182 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.IFELSE     // Catch:{ NoSuchFieldError -> 0x0182 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0182 }
                r2 = 32
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0182 }
            L_0x0182:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x018e }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.AND     // Catch:{ NoSuchFieldError -> 0x018e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x018e }
                r2 = 33
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x018e }
            L_0x018e:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x019a }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.OR     // Catch:{ NoSuchFieldError -> 0x019a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x019a }
                r2 = 34
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x019a }
            L_0x019a:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x01a6 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.EQS     // Catch:{ NoSuchFieldError -> 0x01a6 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x01a6 }
                r2 = 35
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x01a6 }
            L_0x01a6:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x01b2 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.SUBSTR     // Catch:{ NoSuchFieldError -> 0x01b2 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x01b2 }
                r2 = 36
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x01b2 }
            L_0x01b2:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x01be }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.FLOOR     // Catch:{ NoSuchFieldError -> 0x01be }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x01be }
                r2 = 37
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x01be }
            L_0x01be:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x01ca }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.CEIL     // Catch:{ NoSuchFieldError -> 0x01ca }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x01ca }
                r2 = 38
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x01ca }
            L_0x01ca:
                int[] r0 = $SwitchMap$com$miui$maml$data$BaseFunctions$Fun     // Catch:{ NoSuchFieldError -> 0x01d6 }
                com.miui.maml.data.BaseFunctions$Fun r1 = com.miui.maml.data.BaseFunctions.Fun.HASH     // Catch:{ NoSuchFieldError -> 0x01d6 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x01d6 }
                r2 = 39
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x01d6 }
            L_0x01d6:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.BaseFunctions.AnonymousClass1.<clinit>():void");
        }
    }

    private enum Fun {
        INVALID,
        RAND,
        SIN,
        COS,
        TAN,
        ASIN,
        ACOS,
        ATAN,
        SINH,
        COSH,
        SQRT,
        ABS,
        LEN,
        EVAL,
        PRECISE_EVAL,
        ROUND,
        INT,
        NUM,
        MIN,
        MAX,
        POW,
        LOG,
        LOG10,
        DIGIT,
        EQ,
        NE,
        GE,
        GT,
        LE,
        LT,
        ISNULL,
        NOT,
        IFELSE,
        AND,
        OR,
        EQS,
        SUBSTR,
        HASH,
        FLOOR,
        CEIL
    }

    private static class NullObjFunction extends Expression.FunctionImpl {
        private String mObjName;
        private int mVarIndex = -1;

        public NullObjFunction() {
            super(1);
        }

        public double evaluate(Expression[] expressionArr, Variables variables) {
            String evaluateStr = expressionArr[0].evaluateStr();
            if (evaluateStr != this.mObjName) {
                this.mObjName = evaluateStr;
                if (TextUtils.isEmpty(this.mObjName) || !variables.existsObj(this.mObjName)) {
                    this.mVarIndex = -1;
                } else {
                    this.mVarIndex = variables.registerVariable(this.mObjName);
                }
            }
            int i = this.mVarIndex;
            return (i == -1 || variables.get(i) == null) ? 1.0d : 0.0d;
        }

        public String evaluateStr(Expression[] expressionArr, Variables variables) {
            return null;
        }

        public void reset() {
            this.mObjName = null;
        }
    }

    private BaseFunctions(Fun fun2, int i) {
        super(i);
        this.fun = fun2;
    }

    private int digit(int i, int i2) {
        if (i2 <= 0) {
            return -1;
        }
        int i3 = 0;
        if (i == 0 && i2 == 1) {
            return 0;
        }
        while (i > 0 && i3 < i2 - 1) {
            i /= 10;
            i3++;
        }
        if (i > 0) {
            return i % 10;
        }
        return -1;
    }

    public static void load() {
        Expression.FunctionExpression.registerFunction("rand", new BaseFunctions(Fun.RAND, 0));
        Expression.FunctionExpression.registerFunction("sin", new BaseFunctions(Fun.SIN, 1));
        Expression.FunctionExpression.registerFunction("cos", new BaseFunctions(Fun.COS, 1));
        Expression.FunctionExpression.registerFunction("tan", new BaseFunctions(Fun.TAN, 1));
        Expression.FunctionExpression.registerFunction("asin", new BaseFunctions(Fun.ASIN, 1));
        Expression.FunctionExpression.registerFunction("acos", new BaseFunctions(Fun.ACOS, 1));
        Expression.FunctionExpression.registerFunction("atan", new BaseFunctions(Fun.ATAN, 1));
        Expression.FunctionExpression.registerFunction("sinh", new BaseFunctions(Fun.SINH, 1));
        Expression.FunctionExpression.registerFunction("cosh", new BaseFunctions(Fun.COSH, 1));
        Expression.FunctionExpression.registerFunction("sqrt", new BaseFunctions(Fun.SQRT, 1));
        Expression.FunctionExpression.registerFunction("abs", new BaseFunctions(Fun.ABS, 1));
        Expression.FunctionExpression.registerFunction("len", new BaseFunctions(Fun.LEN, 1));
        Expression.FunctionExpression.registerFunction("eval", new BaseFunctions(Fun.EVAL, 1));
        Expression.FunctionExpression.registerFunction("preciseeval", new BaseFunctions(Fun.PRECISE_EVAL, 2));
        Expression.FunctionExpression.registerFunction("round", new BaseFunctions(Fun.ROUND, 1));
        Expression.FunctionExpression.registerFunction("int", new BaseFunctions(Fun.INT, 1));
        Expression.FunctionExpression.registerFunction("num", new BaseFunctions(Fun.NUM, 1));
        Expression.FunctionExpression.registerFunction("isnull", new BaseFunctions(Fun.ISNULL, 1));
        Expression.FunctionExpression.registerFunction("not", new BaseFunctions(Fun.NOT, 1));
        Expression.FunctionExpression.registerFunction("min", new BaseFunctions(Fun.MIN, 2));
        Expression.FunctionExpression.registerFunction("max", new BaseFunctions(Fun.MAX, 2));
        Expression.FunctionExpression.registerFunction("pow", new BaseFunctions(Fun.POW, 2));
        Expression.FunctionExpression.registerFunction("log", new BaseFunctions(Fun.LOG, 1));
        Expression.FunctionExpression.registerFunction("log10", new BaseFunctions(Fun.LOG10, 1));
        Expression.FunctionExpression.registerFunction("digit", new BaseFunctions(Fun.DIGIT, 2));
        Expression.FunctionExpression.registerFunction("eq", new BaseFunctions(Fun.EQ, 2));
        Expression.FunctionExpression.registerFunction("ne", new BaseFunctions(Fun.NE, 2));
        Expression.FunctionExpression.registerFunction("ge", new BaseFunctions(Fun.GE, 2));
        Expression.FunctionExpression.registerFunction("gt", new BaseFunctions(Fun.GT, 2));
        Expression.FunctionExpression.registerFunction("le", new BaseFunctions(Fun.LE, 2));
        Expression.FunctionExpression.registerFunction(d.T, new BaseFunctions(Fun.LT, 2));
        Expression.FunctionExpression.registerFunction("ifelse", new BaseFunctions(Fun.IFELSE, 3));
        Expression.FunctionExpression.registerFunction("and", new BaseFunctions(Fun.AND, 2));
        Expression.FunctionExpression.registerFunction("or", new BaseFunctions(Fun.OR, 2));
        Expression.FunctionExpression.registerFunction("eqs", new BaseFunctions(Fun.EQS, 2));
        Expression.FunctionExpression.registerFunction("substr", new BaseFunctions(Fun.SUBSTR, 2));
        Expression.FunctionExpression.registerFunction("hash", new BaseFunctions(Fun.HASH, 2));
        Expression.FunctionExpression.registerFunction("nullobj", new NullObjFunction());
        Expression.FunctionExpression.registerFunction("floor", new BaseFunctions(Fun.FLOOR, 1));
        Expression.FunctionExpression.registerFunction("ceil", new BaseFunctions(Fun.CEIL, 1));
    }

    public double evaluate(Expression[] expressionArr, Variables variables) {
        StringBuilder sb;
        String str;
        if (AnonymousClass1.$SwitchMap$com$miui$maml$data$BaseFunctions$Fun[this.fun.ordinal()] == 1) {
            return Math.random();
        }
        int i = 0;
        double evaluate = expressionArr[0].evaluate();
        switch (AnonymousClass1.$SwitchMap$com$miui$maml$data$BaseFunctions$Fun[this.fun.ordinal()]) {
            case 2:
                return Math.sin(evaluate);
            case 3:
                return Math.cos(evaluate);
            case 4:
                return Math.tan(evaluate);
            case 5:
                return Math.asin(evaluate);
            case 6:
                return Math.acos(evaluate);
            case 7:
                return Math.atan(evaluate);
            case 8:
                return Math.sinh(evaluate);
            case 9:
                return Math.cosh(evaluate);
            case 10:
                return Math.sqrt(evaluate);
            case 11:
                return Math.abs(evaluate);
            case 12:
                String evaluateStr = expressionArr[0].evaluateStr();
                if (evaluateStr == null) {
                    return 0.0d;
                }
                return (double) evaluateStr.length();
            case 13:
                String evaluateStr2 = expressionArr[0].evaluateStr();
                if (evaluateStr2 == null) {
                    return 0.0d;
                }
                if (!evaluateStr2.equals(this.mEvalExpStr)) {
                    this.mEvalExpStr = evaluateStr2;
                    this.mEvalExp = Expression.build(variables, this.mEvalExpStr);
                }
                Expression expression = this.mEvalExp;
                if (expression == null) {
                    return 0.0d;
                }
                return expression.evaluate();
            case 14:
                String evaluateStr3 = expressionArr[0].evaluateStr();
                if (evaluateStr3 == null) {
                    return 0.0d;
                }
                if (!evaluateStr3.equals(this.mEvalExpStr)) {
                    this.mEvalExpStr = evaluateStr3;
                    this.mEvalExp = Expression.build(variables, this.mEvalExpStr);
                }
                Expression expression2 = this.mEvalExp;
                BigDecimal preciseEvaluate = expression2 != null ? expression2.preciseEvaluate() : null;
                if (preciseEvaluate == null) {
                    return Double.NaN;
                }
                int scale = preciseEvaluate.scale();
                int evaluate2 = (int) expressionArr[1].evaluate();
                if (evaluate2 > 0 && scale > evaluate2) {
                    preciseEvaluate = preciseEvaluate.setScale(evaluate2, 4);
                }
                return preciseEvaluate.doubleValue();
            case 15:
                return (double) Math.round(evaluate);
            case 16:
                return (double) ((int) ((long) evaluate));
            case 17:
                return evaluate;
            case 18:
                return expressionArr[0].isNull() ? 1.0d : 0.0d;
            case 19:
                return evaluate > 0.0d ? 0.0d : 1.0d;
            case 20:
                return Math.min(evaluate, expressionArr[1].evaluate());
            case 21:
                return Math.max(evaluate, expressionArr[1].evaluate());
            case 22:
                return Math.pow(evaluate, expressionArr[1].evaluate());
            case 23:
                return Math.log(evaluate);
            case 24:
                return Math.log10(evaluate);
            case 25:
                return (double) digit((int) evaluate, (int) expressionArr[1].evaluate());
            case 26:
                return evaluate == expressionArr[1].evaluate() ? 1.0d : 0.0d;
            case 27:
                return evaluate != expressionArr[1].evaluate() ? 1.0d : 0.0d;
            case 28:
                return evaluate >= expressionArr[1].evaluate() ? 1.0d : 0.0d;
            case 29:
                return evaluate > expressionArr[1].evaluate() ? 1.0d : 0.0d;
            case 30:
                return evaluate <= expressionArr[1].evaluate() ? 1.0d : 0.0d;
            case 31:
                return evaluate < expressionArr[1].evaluate() ? 1.0d : 0.0d;
            case 32:
                int length = expressionArr.length;
                if (length % 2 != 1) {
                    sb = new StringBuilder();
                    str = "function parameter number should be 2*n+1: ";
                    break;
                } else {
                    while (true) {
                        int i2 = length - 1;
                        if (i >= i2 / 2) {
                            return expressionArr[i2].evaluate();
                        }
                        int i3 = i * 2;
                        if (expressionArr[i3].evaluate() > 0.0d) {
                            return expressionArr[i3 + 1].evaluate();
                        }
                        i++;
                    }
                }
            case 33:
                int length2 = expressionArr.length;
                while (i < length2) {
                    if (expressionArr[i].evaluate() <= 0.0d) {
                        return 0.0d;
                    }
                    i++;
                }
                return 1.0d;
            case 34:
                int length3 = expressionArr.length;
                while (i < length3) {
                    if (expressionArr[i].evaluate() > 0.0d) {
                        return 1.0d;
                    }
                    i++;
                }
                return 0.0d;
            case 35:
                return TextUtils.equals(expressionArr[0].evaluateStr(), expressionArr[1].evaluateStr()) ? 1.0d : 0.0d;
            case 36:
                return Utils.stringToDouble(evaluateStr(expressionArr, variables), 0.0d);
            case 37:
                return Math.floor(evaluate);
            case 38:
                return Math.ceil(evaluate);
            default:
                sb = new StringBuilder();
                str = "fail to evalute FunctionExpression, invalid function: ";
                break;
        }
        sb.append(str);
        sb.append(this.fun.toString());
        Log.e(LOG_TAG, sb.toString());
        return 0.0d;
    }

    public String evaluateStr(Expression[] expressionArr, Variables variables) {
        int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$BaseFunctions$Fun[this.fun.ordinal()];
        int i2 = 0;
        if (i == 13) {
            String evaluateStr = expressionArr[0].evaluateStr();
            if (evaluateStr == null) {
                return null;
            }
            if (!evaluateStr.equals(this.mEvalExpStr)) {
                this.mEvalExpStr = evaluateStr;
                this.mEvalExp = Expression.build(variables, this.mEvalExpStr);
            }
            Expression expression = this.mEvalExp;
            if (expression == null) {
                return null;
            }
            return expression.evaluateStr();
        } else if (i == 32) {
            int length = expressionArr.length;
            if (length % 2 != 1) {
                Log.e(LOG_TAG, "function parameter number should be 2*n+1: " + this.fun.toString());
                return null;
            }
            while (true) {
                int i3 = length - 1;
                if (i2 >= i3 / 2) {
                    return expressionArr[i3].evaluateStr();
                }
                int i4 = i2 * 2;
                if (expressionArr[i4].evaluate() > 0.0d) {
                    return expressionArr[i4 + 1].evaluateStr();
                }
                i2++;
            }
        } else if (i == 36) {
            String evaluateStr2 = expressionArr[0].evaluateStr();
            if (evaluateStr2 == null) {
                return null;
            }
            int length2 = expressionArr.length;
            int evaluate = (int) expressionArr[1].evaluate();
            if (length2 < 3) {
                return evaluateStr2.substring(evaluate);
            }
            try {
                int evaluate2 = (int) expressionArr[2].evaluate();
                int length3 = evaluateStr2.length();
                if (evaluate2 > length3) {
                    evaluate2 = length3;
                }
                return evaluateStr2.substring(evaluate, evaluate2 + evaluate);
            } catch (IndexOutOfBoundsException unused) {
                return null;
            }
        } else if (i != 39) {
            return Utils.doubleToString(evaluate(expressionArr, variables));
        } else {
            String evaluateStr3 = expressionArr[0].evaluateStr();
            String evaluateStr4 = expressionArr[1].evaluateStr();
            if (evaluateStr3 == null || evaluateStr4 == null) {
                return null;
            }
            return HashUtils.getHash(evaluateStr3, evaluateStr4);
        }
    }

    public void reset() {
        this.mEvalExpStr = null;
        this.mEvalExp = null;
    }
}
