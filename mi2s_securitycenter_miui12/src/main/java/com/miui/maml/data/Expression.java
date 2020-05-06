package com.miui.maml.data;

import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.util.Utils;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public abstract class Expression {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "Expression";

    /* renamed from: com.miui.maml.data.Expression$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$Expression$Ope = new int[Ope.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType = new int[Tokenizer.TokenType.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(58:0|(2:1|2)|3|(2:5|6)|7|(2:9|10)|11|(2:13|14)|15|(2:17|18)|19|(2:21|22)|23|(2:25|26)|27|(2:29|30)|31|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|61|62|63|64|65|66|67|68|69|70|71|72|74) */
        /* JADX WARNING: Can't wrap try/catch for region: R(59:0|(2:1|2)|3|(2:5|6)|7|(2:9|10)|11|(2:13|14)|15|17|18|19|(2:21|22)|23|(2:25|26)|27|(2:29|30)|31|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|61|62|63|64|65|66|67|68|69|70|71|72|74) */
        /* JADX WARNING: Can't wrap try/catch for region: R(60:0|(2:1|2)|3|(2:5|6)|7|(2:9|10)|11|13|14|15|17|18|19|(2:21|22)|23|(2:25|26)|27|(2:29|30)|31|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|61|62|63|64|65|66|67|68|69|70|71|72|74) */
        /* JADX WARNING: Can't wrap try/catch for region: R(61:0|(2:1|2)|3|(2:5|6)|7|9|10|11|13|14|15|17|18|19|(2:21|22)|23|(2:25|26)|27|(2:29|30)|31|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|61|62|63|64|65|66|67|68|69|70|71|72|74) */
        /* JADX WARNING: Can't wrap try/catch for region: R(63:0|(2:1|2)|3|5|6|7|9|10|11|13|14|15|17|18|19|(2:21|22)|23|(2:25|26)|27|29|30|31|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|61|62|63|64|65|66|67|68|69|70|71|72|74) */
        /* JADX WARNING: Can't wrap try/catch for region: R(65:0|1|2|3|5|6|7|9|10|11|13|14|15|17|18|19|(2:21|22)|23|25|26|27|29|30|31|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|61|62|63|64|65|66|67|68|69|70|71|72|74) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:35:0x0075 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:37:0x007f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:39:0x0089 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:41:0x0093 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:43:0x009d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:45:0x00a7 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:47:0x00b1 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:49:0x00bb */
        /* JADX WARNING: Missing exception handler attribute for start block: B:51:0x00c7 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:53:0x00d3 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:55:0x00df */
        /* JADX WARNING: Missing exception handler attribute for start block: B:57:0x00eb */
        /* JADX WARNING: Missing exception handler attribute for start block: B:59:0x00f7 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:61:0x0103 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:63:0x010f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:65:0x011b */
        /* JADX WARNING: Missing exception handler attribute for start block: B:67:0x0127 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:69:0x0133 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:71:0x013f */
        static {
            /*
                com.miui.maml.data.Expression$Tokenizer$TokenType[] r0 = com.miui.maml.data.Expression.Tokenizer.TokenType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType = r0
                r0 = 1
                int[] r1 = $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.data.Expression$Tokenizer$TokenType r2 = com.miui.maml.data.Expression.Tokenizer.TokenType.VAR_NUM     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r2 = r2.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r1[r2] = r0     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                r1 = 2
                int[] r2 = $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.data.Expression$Tokenizer$TokenType r3 = com.miui.maml.data.Expression.Tokenizer.TokenType.VAR_STR     // Catch:{ NoSuchFieldError -> 0x001f }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2[r3] = r1     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                r2 = 3
                int[] r3 = $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.maml.data.Expression$Tokenizer$TokenType r4 = com.miui.maml.data.Expression.Tokenizer.TokenType.NUM     // Catch:{ NoSuchFieldError -> 0x002a }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r3[r4] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                r3 = 4
                int[] r4 = $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType     // Catch:{ NoSuchFieldError -> 0x0035 }
                com.miui.maml.data.Expression$Tokenizer$TokenType r5 = com.miui.maml.data.Expression.Tokenizer.TokenType.STR     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r4[r5] = r3     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                r4 = 5
                int[] r5 = $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType     // Catch:{ NoSuchFieldError -> 0x0040 }
                com.miui.maml.data.Expression$Tokenizer$TokenType r6 = com.miui.maml.data.Expression.Tokenizer.TokenType.BRACKET_ROUND     // Catch:{ NoSuchFieldError -> 0x0040 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0040 }
                r5[r6] = r4     // Catch:{ NoSuchFieldError -> 0x0040 }
            L_0x0040:
                r5 = 6
                int[] r6 = $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType     // Catch:{ NoSuchFieldError -> 0x004b }
                com.miui.maml.data.Expression$Tokenizer$TokenType r7 = com.miui.maml.data.Expression.Tokenizer.TokenType.BRACKET_SQUARE     // Catch:{ NoSuchFieldError -> 0x004b }
                int r7 = r7.ordinal()     // Catch:{ NoSuchFieldError -> 0x004b }
                r6[r7] = r5     // Catch:{ NoSuchFieldError -> 0x004b }
            L_0x004b:
                r6 = 7
                int[] r7 = $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType     // Catch:{ NoSuchFieldError -> 0x0056 }
                com.miui.maml.data.Expression$Tokenizer$TokenType r8 = com.miui.maml.data.Expression.Tokenizer.TokenType.OPE     // Catch:{ NoSuchFieldError -> 0x0056 }
                int r8 = r8.ordinal()     // Catch:{ NoSuchFieldError -> 0x0056 }
                r7[r8] = r6     // Catch:{ NoSuchFieldError -> 0x0056 }
            L_0x0056:
                r7 = 8
                int[] r8 = $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType     // Catch:{ NoSuchFieldError -> 0x0062 }
                com.miui.maml.data.Expression$Tokenizer$TokenType r9 = com.miui.maml.data.Expression.Tokenizer.TokenType.FUN     // Catch:{ NoSuchFieldError -> 0x0062 }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x0062 }
                r8[r9] = r7     // Catch:{ NoSuchFieldError -> 0x0062 }
            L_0x0062:
                com.miui.maml.data.Expression$Ope[] r8 = com.miui.maml.data.Expression.Ope.values()
                int r8 = r8.length
                int[] r8 = new int[r8]
                $SwitchMap$com$miui$maml$data$Expression$Ope = r8
                int[] r8 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x0075 }
                com.miui.maml.data.Expression$Ope r9 = com.miui.maml.data.Expression.Ope.MIN     // Catch:{ NoSuchFieldError -> 0x0075 }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x0075 }
                r8[r9] = r0     // Catch:{ NoSuchFieldError -> 0x0075 }
            L_0x0075:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x007f }
                com.miui.maml.data.Expression$Ope r8 = com.miui.maml.data.Expression.Ope.NOT     // Catch:{ NoSuchFieldError -> 0x007f }
                int r8 = r8.ordinal()     // Catch:{ NoSuchFieldError -> 0x007f }
                r0[r8] = r1     // Catch:{ NoSuchFieldError -> 0x007f }
            L_0x007f:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x0089 }
                com.miui.maml.data.Expression$Ope r1 = com.miui.maml.data.Expression.Ope.BIT_NOT     // Catch:{ NoSuchFieldError -> 0x0089 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0089 }
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0089 }
            L_0x0089:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x0093 }
                com.miui.maml.data.Expression$Ope r1 = com.miui.maml.data.Expression.Ope.ADD     // Catch:{ NoSuchFieldError -> 0x0093 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0093 }
                r0[r1] = r3     // Catch:{ NoSuchFieldError -> 0x0093 }
            L_0x0093:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x009d }
                com.miui.maml.data.Expression$Ope r1 = com.miui.maml.data.Expression.Ope.MUL     // Catch:{ NoSuchFieldError -> 0x009d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x009d }
                r0[r1] = r4     // Catch:{ NoSuchFieldError -> 0x009d }
            L_0x009d:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x00a7 }
                com.miui.maml.data.Expression$Ope r1 = com.miui.maml.data.Expression.Ope.DIV     // Catch:{ NoSuchFieldError -> 0x00a7 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00a7 }
                r0[r1] = r5     // Catch:{ NoSuchFieldError -> 0x00a7 }
            L_0x00a7:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x00b1 }
                com.miui.maml.data.Expression$Ope r1 = com.miui.maml.data.Expression.Ope.MOD     // Catch:{ NoSuchFieldError -> 0x00b1 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00b1 }
                r0[r1] = r6     // Catch:{ NoSuchFieldError -> 0x00b1 }
            L_0x00b1:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x00bb }
                com.miui.maml.data.Expression$Ope r1 = com.miui.maml.data.Expression.Ope.BIT_AND     // Catch:{ NoSuchFieldError -> 0x00bb }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00bb }
                r0[r1] = r7     // Catch:{ NoSuchFieldError -> 0x00bb }
            L_0x00bb:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x00c7 }
                com.miui.maml.data.Expression$Ope r1 = com.miui.maml.data.Expression.Ope.BIT_OR     // Catch:{ NoSuchFieldError -> 0x00c7 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00c7 }
                r2 = 9
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x00c7 }
            L_0x00c7:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x00d3 }
                com.miui.maml.data.Expression$Ope r1 = com.miui.maml.data.Expression.Ope.BIT_XOR     // Catch:{ NoSuchFieldError -> 0x00d3 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00d3 }
                r2 = 10
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x00d3 }
            L_0x00d3:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x00df }
                com.miui.maml.data.Expression$Ope r1 = com.miui.maml.data.Expression.Ope.BIT_LSHIFT     // Catch:{ NoSuchFieldError -> 0x00df }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00df }
                r2 = 11
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x00df }
            L_0x00df:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x00eb }
                com.miui.maml.data.Expression$Ope r1 = com.miui.maml.data.Expression.Ope.BIT_RSHIFT     // Catch:{ NoSuchFieldError -> 0x00eb }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00eb }
                r2 = 12
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x00eb }
            L_0x00eb:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x00f7 }
                com.miui.maml.data.Expression$Ope r1 = com.miui.maml.data.Expression.Ope.EQ     // Catch:{ NoSuchFieldError -> 0x00f7 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00f7 }
                r2 = 13
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x00f7 }
            L_0x00f7:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x0103 }
                com.miui.maml.data.Expression$Ope r1 = com.miui.maml.data.Expression.Ope.NEQ     // Catch:{ NoSuchFieldError -> 0x0103 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0103 }
                r2 = 14
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0103 }
            L_0x0103:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x010f }
                com.miui.maml.data.Expression$Ope r1 = com.miui.maml.data.Expression.Ope.AND     // Catch:{ NoSuchFieldError -> 0x010f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x010f }
                r2 = 15
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x010f }
            L_0x010f:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x011b }
                com.miui.maml.data.Expression$Ope r1 = com.miui.maml.data.Expression.Ope.OR     // Catch:{ NoSuchFieldError -> 0x011b }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x011b }
                r2 = 16
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x011b }
            L_0x011b:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x0127 }
                com.miui.maml.data.Expression$Ope r1 = com.miui.maml.data.Expression.Ope.GT     // Catch:{ NoSuchFieldError -> 0x0127 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0127 }
                r2 = 17
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0127 }
            L_0x0127:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x0133 }
                com.miui.maml.data.Expression$Ope r1 = com.miui.maml.data.Expression.Ope.GE     // Catch:{ NoSuchFieldError -> 0x0133 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0133 }
                r2 = 18
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0133 }
            L_0x0133:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x013f }
                com.miui.maml.data.Expression$Ope r1 = com.miui.maml.data.Expression.Ope.LT     // Catch:{ NoSuchFieldError -> 0x013f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x013f }
                r2 = 19
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x013f }
            L_0x013f:
                int[] r0 = $SwitchMap$com$miui$maml$data$Expression$Ope     // Catch:{ NoSuchFieldError -> 0x014b }
                com.miui.maml.data.Expression$Ope r1 = com.miui.maml.data.Expression.Ope.LE     // Catch:{ NoSuchFieldError -> 0x014b }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x014b }
                r2 = 20
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x014b }
            L_0x014b:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.Expression.AnonymousClass1.<clinit>():void");
        }
    }

    static abstract class ArrayVariableExpression extends VariableExpression {
        protected Expression mIndexExp;

        public ArrayVariableExpression(Variables variables, String str, Expression expression) {
            super(variables, str, false);
            this.mIndexExp = expression;
        }

        public void accept(ExpressionVisitor expressionVisitor) {
            expressionVisitor.visit(this);
            this.mIndexExp.accept(expressionVisitor);
        }
    }

    static class BinaryExpression extends Expression {
        private Expression mExp1;
        private Expression mExp2;
        private Ope mOpe;

        public BinaryExpression(Expression expression, Expression expression2, Ope ope) {
            Ope ope2 = Ope.INVALID;
            this.mOpe = ope2;
            this.mExp1 = expression;
            this.mExp2 = expression2;
            this.mOpe = ope;
            if (this.mOpe == ope2) {
                Log.e(Expression.LOG_TAG, "BinaryExpression: invalid operator:" + ope);
            }
        }

        public void accept(ExpressionVisitor expressionVisitor) {
            expressionVisitor.visit(this);
            this.mExp1.accept(expressionVisitor);
            this.mExp2.accept(expressionVisitor);
        }

        public double evaluate() {
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$Expression$Ope[this.mOpe.ordinal()];
            if (i == 1) {
                return this.mExp1.evaluate() - this.mExp2.evaluate();
            }
            switch (i) {
                case 4:
                    return this.mExp1.evaluate() + this.mExp2.evaluate();
                case 5:
                    return this.mExp1.evaluate() * this.mExp2.evaluate();
                case 6:
                    return this.mExp1.evaluate() / this.mExp2.evaluate();
                case 7:
                    return this.mExp1.evaluate() % this.mExp2.evaluate();
                case 8:
                    return (double) (((long) this.mExp1.evaluate()) & ((long) this.mExp2.evaluate()));
                case 9:
                    return (double) (((long) this.mExp1.evaluate()) | ((long) this.mExp2.evaluate()));
                case 10:
                    return (double) (((long) this.mExp1.evaluate()) ^ ((long) this.mExp2.evaluate()));
                case 11:
                    return (double) (((long) this.mExp1.evaluate()) << ((int) ((long) this.mExp2.evaluate())));
                case 12:
                    return (double) (((long) this.mExp1.evaluate()) >> ((int) ((long) this.mExp2.evaluate())));
                case 13:
                    return this.mExp1.evaluate() == this.mExp2.evaluate() ? 1.0d : 0.0d;
                case 14:
                    return this.mExp1.evaluate() != this.mExp2.evaluate() ? 1.0d : 0.0d;
                case 15:
                    return (this.mExp1.evaluate() <= 0.0d || this.mExp2.evaluate() <= 0.0d) ? 0.0d : 1.0d;
                case 16:
                    return (this.mExp1.evaluate() > 0.0d || this.mExp2.evaluate() > 0.0d) ? 1.0d : 0.0d;
                case 17:
                    return this.mExp1.evaluate() > this.mExp2.evaluate() ? 1.0d : 0.0d;
                case 18:
                    return this.mExp1.evaluate() >= this.mExp2.evaluate() ? 1.0d : 0.0d;
                case 19:
                    return this.mExp1.evaluate() < this.mExp2.evaluate() ? 1.0d : 0.0d;
                case 20:
                    return this.mExp1.evaluate() <= this.mExp2.evaluate() ? 1.0d : 0.0d;
                default:
                    Log.e(Expression.LOG_TAG, "fail to evalute BinaryExpression, invalid operator");
                    return 0.0d;
            }
        }

        public String evaluateStr() {
            String evaluateStr = this.mExp1.evaluateStr();
            String evaluateStr2 = this.mExp2.evaluateStr();
            if (AnonymousClass1.$SwitchMap$com$miui$maml$data$Expression$Ope[this.mOpe.ordinal()] != 4) {
                Log.e(Expression.LOG_TAG, "fail to evalute string BinaryExpression, invalid operator");
                return null;
            } else if (evaluateStr == null && evaluateStr2 == null) {
                return null;
            } else {
                if (evaluateStr == null) {
                    return evaluateStr2;
                }
                if (evaluateStr2 == null) {
                    return evaluateStr;
                }
                return evaluateStr + evaluateStr2;
            }
        }

        public boolean isNull() {
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$Expression$Ope[this.mOpe.ordinal()];
            if (i == 1 || i == 4) {
                return this.mExp1.isNull() && this.mExp2.isNull();
            }
            if (!(i == 5 || i == 6 || i == 7)) {
                switch (i) {
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                        break;
                    default:
                        return true;
                }
            }
            return this.mExp1.isNull() || this.mExp2.isNull();
        }

        public BigDecimal preciseEvaluate() {
            if (this.mOpe != Ope.INVALID) {
                BigDecimal preciseEvaluate = this.mExp1.preciseEvaluate();
                BigDecimal preciseEvaluate2 = this.mExp2.preciseEvaluate();
                if (!(preciseEvaluate == null || preciseEvaluate2 == null)) {
                    int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$Expression$Ope[this.mOpe.ordinal()];
                    if (i == 1) {
                        return preciseEvaluate.subtract(preciseEvaluate2);
                    }
                    if (i == 4) {
                        return preciseEvaluate.add(preciseEvaluate2);
                    }
                    if (i == 5) {
                        return preciseEvaluate.multiply(preciseEvaluate2);
                    }
                    if (i == 6) {
                        try {
                            return preciseEvaluate.divide(preciseEvaluate2, MathContext.DECIMAL128);
                        } catch (Exception unused) {
                            return null;
                        }
                    } else if (i == 7) {
                        try {
                            return preciseEvaluate.remainder(preciseEvaluate2);
                        } catch (Exception unused2) {
                            return null;
                        }
                    }
                }
            }
            Log.e(Expression.LOG_TAG, "fail to PRECISE evalute BinaryExpression, invalid operator");
            return null;
        }
    }

    public static class FunctionExpression extends Expression {
        protected static HashMap<String, FunctionImpl> sFunMap = new HashMap<>();
        private FunctionImpl mFun;
        private String mFunName;
        private Expression[] mParaExps;
        private Variables mVariables;

        static {
            FunctionsLoader.load();
        }

        public FunctionExpression(Variables variables, Expression[] expressionArr, String str) {
            this.mVariables = variables;
            this.mParaExps = expressionArr;
            this.mFunName = str;
            parseFunction(str);
        }

        private void parseFunction(String str) {
            FunctionImpl functionImpl = sFunMap.get(str);
            boolean z = true;
            Utils.asserts(functionImpl != null, "invalid function:" + str);
            this.mFun = functionImpl;
            if (this.mParaExps.length < functionImpl.params) {
                z = false;
            }
            Utils.asserts(z, "parameters count not matching for function: " + str);
        }

        public static void registerFunction(String str, FunctionImpl functionImpl) {
            if (sFunMap.put(str, functionImpl) != null) {
                Log.w(Expression.LOG_TAG, "duplicated function name registation: " + str);
            }
        }

        public static void removeFunction(String str, FunctionImpl functionImpl) {
            sFunMap.remove(str);
        }

        public static void resetFunctions() {
            for (Map.Entry<String, FunctionImpl> value : sFunMap.entrySet()) {
                ((FunctionImpl) value.getValue()).reset();
            }
        }

        public void accept(ExpressionVisitor expressionVisitor) {
            expressionVisitor.visit(this);
            int i = 0;
            while (true) {
                Expression[] expressionArr = this.mParaExps;
                if (i < expressionArr.length) {
                    expressionArr[i].accept(expressionVisitor);
                    i++;
                } else {
                    return;
                }
            }
        }

        public double evaluate() {
            return this.mFun.evaluate(this.mParaExps, this.mVariables);
        }

        public String evaluateStr() {
            return this.mFun.evaluateStr(this.mParaExps, this.mVariables);
        }

        public String getFunName() {
            return this.mFunName;
        }
    }

    public static abstract class FunctionImpl {
        public int params;

        public FunctionImpl(int i) {
            this.params = i;
        }

        public abstract double evaluate(Expression[] expressionArr, Variables variables);

        public abstract String evaluateStr(Expression[] expressionArr, Variables variables);

        public abstract void reset();
    }

    static class NumberArrayVariableExpression extends ArrayVariableExpression {
        public NumberArrayVariableExpression(Variables variables, String str, Expression expression) {
            super(variables, str, expression);
        }

        public double evaluate() {
            return this.mIndexedVar.getArrDouble((int) this.mIndexExp.evaluate());
        }

        public String evaluateStr() {
            return Utils.doubleToString(evaluate());
        }

        public boolean isNull() {
            return this.mIndexedVar.isNull((int) this.mIndexExp.evaluate());
        }
    }

    public static class NumberExpression extends Expression {
        private String mString;
        private double mValue;

        public NumberExpression(double d2) {
            this.mValue = d2;
        }

        public NumberExpression(String str) {
            if (str == null) {
                Log.e(Expression.LOG_TAG, "invalid NumberExpression: null");
                return;
            }
            try {
                if (str.length() <= 2 || str.indexOf("0x") != 0) {
                    this.mValue = Double.parseDouble(str);
                } else {
                    this.mValue = (double) Long.parseLong(str.substring(2), 16);
                }
            } catch (NumberFormatException unused) {
                Log.e(Expression.LOG_TAG, "invalid NumberExpression:" + str);
            }
        }

        public double evaluate() {
            return this.mValue;
        }

        public String evaluateStr() {
            if (this.mString == null) {
                this.mString = Utils.doubleToString(this.mValue);
            }
            return this.mString;
        }

        public void setValue(double d2) {
            this.mValue = d2;
        }
    }

    static class NumberVariableExpression extends VariableExpression {
        public NumberVariableExpression(Variables variables, String str) {
            super(variables, str, true);
        }

        public double evaluate() {
            return this.mIndexedVar.getDouble();
        }

        public String evaluateStr() {
            return Utils.doubleToString(evaluate());
        }

        public boolean isNull() {
            return this.mIndexedVar.isNull();
        }
    }

    private enum Ope {
        ADD,
        MIN,
        MUL,
        DIV,
        MOD,
        BIT_AND,
        BIT_OR,
        BIT_XOR,
        BIT_NOT,
        BIT_LSHIFT,
        BIT_RSHIFT,
        NOT,
        EQ,
        NEQ,
        AND,
        OR,
        GT,
        GE,
        LT,
        LE,
        INVALID
    }

    private static class OpeInfo {
        /* access modifiers changed from: private */
        public static final int OPE_SIZE = mOpes.length;
        private static final int[] mOpePar = {2, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2};
        private static final int[] mOpePriority = {4, 4, 3, 3, 3, 8, 9, 10, 2, 5, 5, 2, 7, 7, 11, 12, 6, 6, 6, 6};
        /* access modifiers changed from: private */
        public static final String[] mOpes = {"+", "-", "*", "/", "%", "&", "|", "^", "~", "{{", "}}", "!", "==", "!=", "**", "||", "}", "}=", "{", "{="};
        public int participants;
        public int priority;
        public String str;
        public boolean unary;

        public static class Parser {
            private int[] mFlags = new int[OpeInfo.OPE_SIZE];
            private int mMatch;
            private int mStep;

            public boolean accept(char c2, boolean z) {
                if (z) {
                    for (int i = 0; i < OpeInfo.OPE_SIZE; i++) {
                        this.mFlags[i] = 0;
                    }
                    this.mStep = 0;
                    this.mMatch = -1;
                }
                boolean z2 = false;
                for (int i2 = 0; i2 < OpeInfo.OPE_SIZE; i2++) {
                    if (this.mFlags[i2] != -1) {
                        String str = OpeInfo.mOpes[i2];
                        int length = str.length();
                        int i3 = this.mStep;
                        if (length <= i3 || str.charAt(i3) != c2) {
                            this.mFlags[i2] = -1;
                        } else {
                            boolean z3 = this.mStep == str.length() - 1;
                            this.mFlags[i2] = 0;
                            if (z3) {
                                this.mMatch = i2;
                            }
                            z2 = true;
                        }
                    }
                }
                if (z2) {
                    this.mStep++;
                }
                return z2;
            }

            public Ope getMatch() {
                return this.mMatch == -1 ? Ope.INVALID : Ope.values()[this.mMatch];
            }
        }

        private OpeInfo() {
        }

        public static OpeInfo getOpeInfo(int i) {
            OpeInfo opeInfo = new OpeInfo();
            opeInfo.priority = mOpePriority[i];
            opeInfo.participants = mOpePar[i];
            opeInfo.str = mOpes[i];
            return opeInfo;
        }
    }

    static class StringArrayVariableExpression extends ArrayVariableExpression {
        public StringArrayVariableExpression(Variables variables, String str, Expression expression) {
            super(variables, str, expression);
        }

        public double evaluate() {
            String evaluateStr = evaluateStr();
            if (evaluateStr == null) {
                return 0.0d;
            }
            try {
                return Double.parseDouble(evaluateStr);
            } catch (NumberFormatException unused) {
                return 0.0d;
            }
        }

        public String evaluateStr() {
            return this.mIndexedVar.getArrString((int) this.mIndexExp.evaluate());
        }

        public boolean isNull() {
            return this.mIndexedVar.isNull((int) this.mIndexExp.evaluate());
        }
    }

    static class StringExpression extends Expression {
        private String mValue;

        public StringExpression(String str) {
            this.mValue = str;
        }

        public double evaluate() {
            try {
                return Double.parseDouble(this.mValue);
            } catch (NumberFormatException unused) {
                return 0.0d;
            }
        }

        public String evaluateStr() {
            return this.mValue;
        }
    }

    static class StringVariableExpression extends VariableExpression {
        public StringVariableExpression(Variables variables, String str) {
            super(variables, str, false);
        }

        public double evaluate() {
            String evaluateStr = evaluateStr();
            if (evaluateStr == null) {
                return 0.0d;
            }
            try {
                return Double.parseDouble(evaluateStr);
            } catch (NumberFormatException unused) {
                return 0.0d;
            }
        }

        public String evaluateStr() {
            return this.mIndexedVar.getString();
        }

        public boolean isNull() {
            return this.mIndexedVar.isNull();
        }
    }

    private static class Tokenizer {
        private static final int BRACKET_MODE_NONE = 0;
        private static final int BRACKET_MODE_ROUND = 1;
        private static final int BRACKET_MODE_SQUARE = 2;
        private OpeInfo.Parser mOpeParser = new OpeInfo.Parser();
        private int mPos;
        private String mString;

        public static class Token {
            public OpeInfo info;
            public Ope op = Ope.INVALID;
            public String token;
            public TokenType type = TokenType.INVALID;

            public Token(TokenType tokenType, String str) {
                this.type = tokenType;
                this.token = str;
            }

            public Token(TokenType tokenType, String str, Ope ope) {
                this.type = tokenType;
                this.token = str;
                this.op = ope;
                this.info = OpeInfo.getOpeInfo(this.op.ordinal());
            }
        }

        public enum TokenType {
            INVALID,
            VAR_NUM,
            VAR_STR,
            NUM,
            STR,
            OPE,
            FUN,
            BRACKET_ROUND,
            BRACKET_SQUARE
        }

        public Tokenizer(String str) {
            this.mString = str;
            reset();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:33:0x008d, code lost:
            if (r0.mString.charAt(r1) == 'x') goto L_0x009f;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.miui.maml.data.Expression.Tokenizer.Token getToken() {
            /*
                r16 = this;
                r0 = r16
                com.miui.maml.data.Expression$Tokenizer$TokenType r1 = com.miui.maml.data.Expression.Tokenizer.TokenType.INVALID
                java.lang.String r2 = r0.mString
                int r2 = r2.length()
                int r3 = r0.mPos
                r4 = -1
                r5 = 0
                r8 = r1
                r9 = r4
                r1 = r5
                r4 = r1
                r6 = r4
                r7 = r6
            L_0x0014:
                java.lang.String r10 = "Expression"
                if (r3 >= r2) goto L_0x0196
                java.lang.String r12 = r0.mString
                char r12 = r12.charAt(r3)
                r13 = 91
                r14 = 40
                r15 = 1
                if (r4 != 0) goto L_0x015d
                r11 = 35
                if (r12 == r11) goto L_0x0118
                r11 = 64
                if (r12 != r11) goto L_0x002f
                goto L_0x0118
            L_0x002f:
                r10 = 39
                if (r12 != r10) goto L_0x0067
                int r3 = r3 + r15
                r1 = r3
                r4 = r5
            L_0x0036:
                if (r1 >= r2) goto L_0x004d
                java.lang.String r6 = r0.mString
                char r6 = r6.charAt(r1)
                if (r4 != 0) goto L_0x0043
                if (r6 != r10) goto L_0x0043
                goto L_0x004d
            L_0x0043:
                r4 = 92
                if (r6 != r4) goto L_0x0049
                r4 = r15
                goto L_0x004a
            L_0x0049:
                r4 = r5
            L_0x004a:
                int r1 = r1 + 1
                goto L_0x0036
            L_0x004d:
                int r2 = r1 + 1
                r0.mPos = r2
                com.miui.maml.data.Expression$Tokenizer$Token r2 = new com.miui.maml.data.Expression$Tokenizer$Token
                com.miui.maml.data.Expression$Tokenizer$TokenType r4 = com.miui.maml.data.Expression.Tokenizer.TokenType.STR
                java.lang.String r5 = r0.mString
                java.lang.String r1 = r5.substring(r3, r1)
                java.lang.String r3 = "\\'"
                java.lang.String r5 = "'"
                java.lang.String r1 = r1.replace(r3, r5)
                r2.<init>(r4, r1)
                return r2
            L_0x0067:
                if (r12 != r14) goto L_0x006c
                r4 = r15
                goto L_0x015d
            L_0x006c:
                if (r12 != r13) goto L_0x0071
                r4 = 2
                goto L_0x015d
            L_0x0071:
                boolean r10 = com.miui.maml.data.Expression.isDigitCharStart(r12)
                if (r10 == 0) goto L_0x00b2
                int r1 = r3 + 1
                java.lang.String r4 = r0.mString
                char r4 = r4.charAt(r3)
                r5 = 48
                if (r4 != r5) goto L_0x0090
                if (r1 >= r2) goto L_0x0090
                java.lang.String r4 = r0.mString
                char r4 = r4.charAt(r1)
                r5 = 120(0x78, float:1.68E-43)
                if (r4 != r5) goto L_0x0090
                goto L_0x009f
            L_0x0090:
                if (r1 >= r2) goto L_0x00a2
                java.lang.String r4 = r0.mString
                char r4 = r4.charAt(r1)
                boolean r4 = com.miui.maml.data.Expression.isDigitCharRest(r4)
                if (r4 != 0) goto L_0x009f
                goto L_0x00a2
            L_0x009f:
                int r1 = r1 + 1
                goto L_0x0090
            L_0x00a2:
                r0.mPos = r1
                com.miui.maml.data.Expression$Tokenizer$Token r2 = new com.miui.maml.data.Expression$Tokenizer$Token
                com.miui.maml.data.Expression$Tokenizer$TokenType r4 = com.miui.maml.data.Expression.Tokenizer.TokenType.NUM
                java.lang.String r5 = r0.mString
                java.lang.String r1 = r5.substring(r3, r1)
                r2.<init>(r4, r1)
                return r2
            L_0x00b2:
                boolean r10 = com.miui.maml.data.Expression.isFunctionCharStart(r12)
                if (r10 == 0) goto L_0x00dc
                int r1 = r3 + 1
            L_0x00ba:
                if (r1 >= r2) goto L_0x00cc
                java.lang.String r4 = r0.mString
                char r4 = r4.charAt(r1)
                boolean r4 = com.miui.maml.data.Expression.isFunctionCharRest(r4)
                if (r4 != 0) goto L_0x00c9
                goto L_0x00cc
            L_0x00c9:
                int r1 = r1 + 1
                goto L_0x00ba
            L_0x00cc:
                r0.mPos = r1
                com.miui.maml.data.Expression$Tokenizer$Token r2 = new com.miui.maml.data.Expression$Tokenizer$Token
                com.miui.maml.data.Expression$Tokenizer$TokenType r4 = com.miui.maml.data.Expression.Tokenizer.TokenType.FUN
                java.lang.String r5 = r0.mString
                java.lang.String r1 = r5.substring(r3, r1)
                r2.<init>(r4, r1)
                return r2
            L_0x00dc:
                com.miui.maml.data.Expression$OpeInfo$Parser r10 = r0.mOpeParser
                boolean r10 = r10.accept(r12, r15)
                if (r10 == 0) goto L_0x015d
                int r10 = r3 + 1
            L_0x00e6:
                if (r10 >= r2) goto L_0x00fc
                com.miui.maml.data.Expression$OpeInfo$Parser r11 = r0.mOpeParser
                java.lang.String r13 = r0.mString
                char r13 = r13.charAt(r10)
                boolean r11 = r11.accept(r13, r5)
                if (r11 != 0) goto L_0x00f7
                goto L_0x00fc
            L_0x00f7:
                int r10 = r10 + 1
                r13 = 91
                goto L_0x00e6
            L_0x00fc:
                com.miui.maml.data.Expression$OpeInfo$Parser r11 = r0.mOpeParser
                com.miui.maml.data.Expression$Ope r11 = r11.getMatch()
                com.miui.maml.data.Expression$Ope r13 = com.miui.maml.data.Expression.Ope.INVALID
                if (r11 == r13) goto L_0x015d
                r0.mPos = r10
                com.miui.maml.data.Expression$Tokenizer$Token r1 = new com.miui.maml.data.Expression$Tokenizer$Token
                com.miui.maml.data.Expression$Tokenizer$TokenType r2 = com.miui.maml.data.Expression.Tokenizer.TokenType.OPE
                java.lang.String r4 = r0.mString
                int r5 = r0.mPos
                java.lang.String r3 = r4.substring(r3, r5)
                r1.<init>(r2, r3, r11)
                return r1
            L_0x0118:
                int r3 = r3 + r15
                r1 = r3
            L_0x011a:
                if (r1 >= r2) goto L_0x012c
                java.lang.String r4 = r0.mString
                char r4 = r4.charAt(r1)
                boolean r4 = com.miui.maml.data.Expression.isVariableChar(r4)
                if (r4 != 0) goto L_0x0129
                goto L_0x012c
            L_0x0129:
                int r1 = r1 + 1
                goto L_0x011a
            L_0x012c:
                if (r1 != r3) goto L_0x0146
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "invalid variable name:"
                r1.append(r2)
                java.lang.String r2 = r0.mString
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                android.util.Log.e(r10, r1)
                r1 = 0
                return r1
            L_0x0146:
                r0.mPos = r1
                com.miui.maml.data.Expression$Tokenizer$Token r2 = new com.miui.maml.data.Expression$Tokenizer$Token
                r4 = 35
                if (r12 != r4) goto L_0x0151
                com.miui.maml.data.Expression$Tokenizer$TokenType r4 = com.miui.maml.data.Expression.Tokenizer.TokenType.VAR_NUM
                goto L_0x0153
            L_0x0151:
                com.miui.maml.data.Expression$Tokenizer$TokenType r4 = com.miui.maml.data.Expression.Tokenizer.TokenType.VAR_STR
            L_0x0153:
                java.lang.String r5 = r0.mString
                java.lang.String r1 = r5.substring(r3, r1)
                r2.<init>(r4, r1)
                return r2
            L_0x015d:
                if (r4 == 0) goto L_0x0192
                if (r1 != 0) goto L_0x0177
                if (r4 == r15) goto L_0x016f
                r9 = 2
                if (r4 == r9) goto L_0x0167
                goto L_0x0175
            L_0x0167:
                r6 = 93
                com.miui.maml.data.Expression$Tokenizer$TokenType r8 = com.miui.maml.data.Expression.Tokenizer.TokenType.BRACKET_SQUARE
                r7 = r6
                r6 = 91
                goto L_0x0175
            L_0x016f:
                r6 = 41
                com.miui.maml.data.Expression$Tokenizer$TokenType r8 = com.miui.maml.data.Expression.Tokenizer.TokenType.BRACKET_ROUND
                r7 = r6
                r6 = r14
            L_0x0175:
                int r9 = r3 + 1
            L_0x0177:
                if (r12 != r6) goto L_0x017c
                int r1 = r1 + 1
                goto L_0x0192
            L_0x017c:
                if (r12 != r7) goto L_0x0192
                int r1 = r1 + -1
                if (r1 != 0) goto L_0x0192
                int r1 = r3 + 1
                r0.mPos = r1
                com.miui.maml.data.Expression$Tokenizer$Token r1 = new com.miui.maml.data.Expression$Tokenizer$Token
                java.lang.String r2 = r0.mString
                java.lang.String r2 = r2.substring(r9, r3)
                r1.<init>(r8, r2)
                return r1
            L_0x0192:
                int r3 = r3 + 1
                goto L_0x0014
            L_0x0196:
                if (r1 == 0) goto L_0x01ae
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "mismatched bracket:"
                r1.append(r2)
                java.lang.String r2 = r0.mString
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                android.util.Log.e(r10, r1)
            L_0x01ae:
                r1 = 0
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.Expression.Tokenizer.getToken():com.miui.maml.data.Expression$Tokenizer$Token");
        }

        public void reset() {
            this.mPos = 0;
        }
    }

    static class UnaryExpression extends Expression {
        private Expression mExp;
        private Ope mOpe;

        public UnaryExpression(Expression expression, Ope ope) {
            Ope ope2 = Ope.INVALID;
            this.mOpe = ope2;
            this.mExp = expression;
            this.mOpe = ope;
            if (this.mOpe == ope2) {
                Log.e(Expression.LOG_TAG, "UnaryExpression: invalid operator:" + ope);
            }
        }

        public void accept(ExpressionVisitor expressionVisitor) {
            expressionVisitor.visit(this);
            this.mExp.accept(expressionVisitor);
        }

        public double evaluate() {
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$Expression$Ope[this.mOpe.ordinal()];
            if (i == 1) {
                return 0.0d - this.mExp.evaluate();
            }
            if (i == 2) {
                return this.mExp.evaluate() <= 0.0d ? 1.0d : 0.0d;
            }
            if (i == 3) {
                return (double) (~((int) this.mExp.evaluate()));
            }
            Log.e(Expression.LOG_TAG, "fail to evalute UnaryExpression, invalid operator");
            return this.mExp.evaluate();
        }

        public String evaluateStr() {
            return Utils.doubleToString(evaluate());
        }

        public boolean isNull() {
            return this.mExp.isNull();
        }
    }

    static abstract class VariableExpression extends Expression {
        protected IndexedVariable mIndexedVar;
        protected String mName;

        public VariableExpression(Variables variables, String str, boolean z) {
            this.mName = str;
            this.mIndexedVar = new IndexedVariable(this.mName, variables, z);
        }

        public int getIndex() {
            return this.mIndexedVar.getIndex();
        }

        public String getName() {
            return this.mName;
        }

        public int getVersion() {
            return this.mIndexedVar.getVersion();
        }
    }

    public static Expression build(Variables variables, String str) {
        Expression buildInner = buildInner(variables, str);
        if (buildInner == null) {
            return null;
        }
        return new RootExpression(variables, buildInner);
    }

    private static Expression buildBracket(Variables variables, Tokenizer.Token token, Stack<Tokenizer.Token> stack) {
        StringBuilder sb;
        String str;
        Expression[] buildMultipleInner = buildMultipleInner(variables, token.token);
        if (!checkParams(buildMultipleInner)) {
            sb = new StringBuilder();
            str = "invalid expressions: ";
        } else {
            try {
                if (!stack.isEmpty() && stack.peek().type == Tokenizer.TokenType.FUN) {
                    return new FunctionExpression(variables, buildMultipleInner, stack.pop().token);
                }
                if (buildMultipleInner.length == 1) {
                    return buildMultipleInner[0];
                }
                sb = new StringBuilder();
                str = "fail to buid: multiple expressions in brackets, but seems no function presents:";
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(LOG_TAG, e.toString());
            }
        }
        sb.append(str);
        sb.append(token.token);
        Log.e(LOG_TAG, sb.toString());
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0178, code lost:
        if (r2.empty() != false) goto L_0x019f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0182, code lost:
        if (((com.miui.maml.data.Expression.Tokenizer.Token) r2.peek()).info == null) goto L_0x019f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x018e, code lost:
        if (((com.miui.maml.data.Expression.Tokenizer.Token) r2.peek()).info.unary == false) goto L_0x019f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0190, code lost:
        r4 = new com.miui.maml.data.Expression.UnaryExpression(r4, ((com.miui.maml.data.Expression.Tokenizer.Token) r2.pop()).op);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x019f, code lost:
        r3.push(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0034, code lost:
        r2.push(r5);
     */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0112 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.miui.maml.data.Expression buildInner(com.miui.maml.data.Variables r10, java.lang.String r11) {
        /*
            java.lang.String r0 = r11.trim()
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            r1 = 0
            if (r0 == 0) goto L_0x000c
            return r1
        L_0x000c:
            com.miui.maml.data.Expression$Tokenizer r0 = new com.miui.maml.data.Expression$Tokenizer
            r0.<init>(r11)
            java.util.Stack r2 = new java.util.Stack
            r2.<init>()
            java.util.Stack r3 = new java.util.Stack
            r3.<init>()
            r4 = r1
        L_0x001c:
            com.miui.maml.data.Expression$Tokenizer$Token r5 = r0.getToken()
            java.lang.String r6 = "Expression"
            r7 = 1
            if (r5 == 0) goto L_0x01a5
            int[] r8 = com.miui.maml.data.Expression.AnonymousClass1.$SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType
            com.miui.maml.data.Expression$Tokenizer$TokenType r9 = r5.type
            int r9 = r9.ordinal()
            r8 = r8[r9]
            switch(r8) {
                case 1: goto L_0x00a8;
                case 2: goto L_0x00a8;
                case 3: goto L_0x00a8;
                case 4: goto L_0x00a8;
                case 5: goto L_0x00a8;
                case 6: goto L_0x00a8;
                case 7: goto L_0x0039;
                case 8: goto L_0x0034;
                default: goto L_0x0032;
            }
        L_0x0032:
            goto L_0x01a2
        L_0x0034:
            r2.push(r5)
            goto L_0x01a2
        L_0x0039:
            com.miui.maml.data.Expression$OpeInfo r8 = r5.info
            int r8 = r8.participants
            if (r8 != r7) goto L_0x004c
            if (r4 == 0) goto L_0x0047
            com.miui.maml.data.Expression$Tokenizer$TokenType r4 = r4.type
            com.miui.maml.data.Expression$Tokenizer$TokenType r8 = com.miui.maml.data.Expression.Tokenizer.TokenType.OPE
            if (r4 != r8) goto L_0x004c
        L_0x0047:
            com.miui.maml.data.Expression$OpeInfo r4 = r5.info
            r4.unary = r7
            goto L_0x0034
        L_0x004c:
            int r4 = r2.size()
            if (r4 <= 0) goto L_0x0034
            java.lang.Object r4 = r2.peek()
            com.miui.maml.data.Expression$Tokenizer$Token r4 = (com.miui.maml.data.Expression.Tokenizer.Token) r4
            com.miui.maml.data.Expression$Tokenizer$TokenType r4 = r4.type
            com.miui.maml.data.Expression$Tokenizer$TokenType r7 = com.miui.maml.data.Expression.Tokenizer.TokenType.OPE
            if (r4 != r7) goto L_0x0034
            java.lang.Object r4 = r2.peek()
            com.miui.maml.data.Expression$Tokenizer$Token r4 = (com.miui.maml.data.Expression.Tokenizer.Token) r4
            com.miui.maml.data.Expression$OpeInfo r4 = r4.info
            int r4 = r4.priority
            com.miui.maml.data.Expression$OpeInfo r7 = r5.info
            int r7 = r7.priority
            int r4 = r4 - r7
            if (r4 > 0) goto L_0x0034
            int r4 = r3.size()
            r7 = 2
            if (r4 >= r7) goto L_0x008b
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r0 = "fail to buid: invalid operator position:"
        L_0x007d:
            r10.append(r0)
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            android.util.Log.e(r6, r10)
            return r1
        L_0x008b:
            java.lang.Object r4 = r3.pop()
            com.miui.maml.data.Expression r4 = (com.miui.maml.data.Expression) r4
            java.lang.Object r7 = r3.pop()
            com.miui.maml.data.Expression r7 = (com.miui.maml.data.Expression) r7
            com.miui.maml.data.Expression$BinaryExpression r8 = new com.miui.maml.data.Expression$BinaryExpression
            java.lang.Object r9 = r2.pop()
            com.miui.maml.data.Expression$Tokenizer$Token r9 = (com.miui.maml.data.Expression.Tokenizer.Token) r9
            com.miui.maml.data.Expression$Ope r9 = r9.op
            r8.<init>(r7, r4, r9)
            r3.push(r8)
            goto L_0x004c
        L_0x00a8:
            int[] r4 = com.miui.maml.data.Expression.AnonymousClass1.$SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType
            com.miui.maml.data.Expression$Tokenizer$TokenType r8 = r5.type
            int r8 = r8.ordinal()
            r4 = r4[r8]
            switch(r4) {
                case 1: goto L_0x016d;
                case 2: goto L_0x0165;
                case 3: goto L_0x0122;
                case 4: goto L_0x011a;
                case 5: goto L_0x0113;
                case 6: goto L_0x00b8;
                default: goto L_0x00b5;
            }
        L_0x00b5:
            r4 = r1
            goto L_0x0174
        L_0x00b8:
            int r4 = r3.size()
            if (r4 >= r7) goto L_0x00c6
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r0 = "fail to buid: no array name before []:"
            goto L_0x007d
        L_0x00c6:
            java.lang.Object r4 = r3.pop()
            com.miui.maml.data.Expression r4 = (com.miui.maml.data.Expression) r4
            boolean r7 = r4 instanceof com.miui.maml.data.Expression.VariableExpression
            if (r7 == 0) goto L_0x00fb
            java.lang.String r7 = r5.token
            com.miui.maml.data.Expression r7 = buildInner(r10, r7)
            if (r7 != 0) goto L_0x00e0
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r0 = "fail to buid: no index expression in []:"
            goto L_0x007d
        L_0x00e0:
            r6 = r4
            com.miui.maml.data.Expression$VariableExpression r6 = (com.miui.maml.data.Expression.VariableExpression) r6
            java.lang.String r6 = r6.getName()
            boolean r8 = r4 instanceof com.miui.maml.data.Expression.NumberVariableExpression
            if (r8 == 0) goto L_0x00f1
            com.miui.maml.data.Expression$NumberArrayVariableExpression r4 = new com.miui.maml.data.Expression$NumberArrayVariableExpression
            r4.<init>(r10, r6, r7)
            goto L_0x0110
        L_0x00f1:
            boolean r4 = r4 instanceof com.miui.maml.data.Expression.StringVariableExpression
            if (r4 == 0) goto L_0x010f
            com.miui.maml.data.Expression$StringArrayVariableExpression r4 = new com.miui.maml.data.Expression$StringArrayVariableExpression
            r4.<init>(r10, r6, r7)
            goto L_0x0110
        L_0x00fb:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r7 = "fail to buid: the expression before [] is not a variable:"
            r4.append(r7)
            r4.append(r11)
            java.lang.String r4 = r4.toString()
            android.util.Log.e(r6, r4)
        L_0x010f:
            r4 = r1
        L_0x0110:
            if (r4 != 0) goto L_0x0174
            return r1
        L_0x0113:
            com.miui.maml.data.Expression r4 = buildBracket(r10, r5, r2)
            if (r4 != 0) goto L_0x0174
            return r1
        L_0x011a:
            com.miui.maml.data.Expression$StringExpression r4 = new com.miui.maml.data.Expression$StringExpression
            java.lang.String r6 = r5.token
            r4.<init>(r6)
            goto L_0x0174
        L_0x0122:
            boolean r4 = r2.empty()
            if (r4 != 0) goto L_0x0141
            java.lang.Object r4 = r2.peek()
            com.miui.maml.data.Expression$Tokenizer$Token r4 = (com.miui.maml.data.Expression.Tokenizer.Token) r4
            com.miui.maml.data.Expression$Ope r4 = r4.op
            com.miui.maml.data.Expression$Ope r6 = com.miui.maml.data.Expression.Ope.MIN
            if (r4 != r6) goto L_0x0141
            java.lang.Object r4 = r2.peek()
            com.miui.maml.data.Expression$Tokenizer$Token r4 = (com.miui.maml.data.Expression.Tokenizer.Token) r4
            com.miui.maml.data.Expression$OpeInfo r4 = r4.info
            boolean r4 = r4.unary
            if (r4 == 0) goto L_0x0141
            goto L_0x0142
        L_0x0141:
            r7 = 0
        L_0x0142:
            com.miui.maml.data.Expression$NumberExpression r4 = new com.miui.maml.data.Expression$NumberExpression
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            if (r7 == 0) goto L_0x014e
            java.lang.String r8 = "-"
            goto L_0x0150
        L_0x014e:
            java.lang.String r8 = ""
        L_0x0150:
            r6.append(r8)
            java.lang.String r8 = r5.token
            r6.append(r8)
            java.lang.String r6 = r6.toString()
            r4.<init>((java.lang.String) r6)
            if (r7 == 0) goto L_0x0174
            r2.pop()
            goto L_0x0174
        L_0x0165:
            com.miui.maml.data.Expression$StringVariableExpression r4 = new com.miui.maml.data.Expression$StringVariableExpression
            java.lang.String r6 = r5.token
            r4.<init>(r10, r6)
            goto L_0x0174
        L_0x016d:
            com.miui.maml.data.Expression$NumberVariableExpression r4 = new com.miui.maml.data.Expression$NumberVariableExpression
            java.lang.String r6 = r5.token
            r4.<init>(r10, r6)
        L_0x0174:
            boolean r6 = r2.empty()
            if (r6 != 0) goto L_0x019f
            java.lang.Object r6 = r2.peek()
            com.miui.maml.data.Expression$Tokenizer$Token r6 = (com.miui.maml.data.Expression.Tokenizer.Token) r6
            com.miui.maml.data.Expression$OpeInfo r6 = r6.info
            if (r6 == 0) goto L_0x019f
            java.lang.Object r6 = r2.peek()
            com.miui.maml.data.Expression$Tokenizer$Token r6 = (com.miui.maml.data.Expression.Tokenizer.Token) r6
            com.miui.maml.data.Expression$OpeInfo r6 = r6.info
            boolean r6 = r6.unary
            if (r6 == 0) goto L_0x019f
            com.miui.maml.data.Expression$UnaryExpression r6 = new com.miui.maml.data.Expression$UnaryExpression
            java.lang.Object r7 = r2.pop()
            com.miui.maml.data.Expression$Tokenizer$Token r7 = (com.miui.maml.data.Expression.Tokenizer.Token) r7
            com.miui.maml.data.Expression$Ope r7 = r7.op
            r6.<init>(r4, r7)
            r4 = r6
            goto L_0x0174
        L_0x019f:
            r3.push(r4)
        L_0x01a2:
            r4 = r5
            goto L_0x001c
        L_0x01a5:
            int r10 = r3.size()
            int r0 = r2.size()
            int r0 = r0 + r7
            if (r10 == r0) goto L_0x01b9
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r0 = "fail to buid: invalid expression:"
            goto L_0x007d
        L_0x01b9:
            java.lang.Object r10 = r3.pop()
            com.miui.maml.data.Expression r10 = (com.miui.maml.data.Expression) r10
        L_0x01bf:
            int r11 = r2.size()
            if (r11 <= 0) goto L_0x01da
            java.lang.Object r11 = r3.pop()
            com.miui.maml.data.Expression r11 = (com.miui.maml.data.Expression) r11
            com.miui.maml.data.Expression$BinaryExpression r0 = new com.miui.maml.data.Expression$BinaryExpression
            java.lang.Object r1 = r2.pop()
            com.miui.maml.data.Expression$Tokenizer$Token r1 = (com.miui.maml.data.Expression.Tokenizer.Token) r1
            com.miui.maml.data.Expression$Ope r1 = r1.op
            r0.<init>(r11, r10, r1)
            r10 = r0
            goto L_0x01bf
        L_0x01da:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.Expression.buildInner(com.miui.maml.data.Variables, java.lang.String):com.miui.maml.data.Expression");
    }

    public static Expression[] buildMultiple(Variables variables, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        Expression[] buildMultipleInner = buildMultipleInner(variables, str);
        Expression[] expressionArr = new Expression[buildMultipleInner.length];
        for (int i = 0; i < buildMultipleInner.length; i++) {
            Expression expression = buildMultipleInner[i];
            if (expression == null || (expression instanceof NumberExpression) || (expression instanceof StringExpression)) {
                expressionArr[i] = expression;
            } else {
                expressionArr[i] = new RootExpression(variables, expression);
            }
        }
        return expressionArr;
    }

    private static Expression[] buildMultipleInner(Variables variables, String str) {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        boolean z = false;
        int i2 = 0;
        for (int i3 = 0; i3 < str.length(); i3++) {
            char charAt = str.charAt(i3);
            if (!z) {
                if (charAt == ',' && i2 == 0) {
                    arrayList.add(buildInner(variables, str.substring(i, i3)));
                    i = i3 + 1;
                } else if (charAt == '(') {
                    i2++;
                } else if (charAt == ')') {
                    i2--;
                }
            }
            if (charAt == '\'') {
                z = !z;
            }
        }
        if (i < str.length()) {
            arrayList.add(buildInner(variables, str.substring(i)));
        }
        return (Expression[]) arrayList.toArray(new Expression[arrayList.size()]);
    }

    private static boolean checkParams(Expression[] expressionArr) {
        for (Expression expression : expressionArr) {
            if (expression == null) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public static boolean isDigitCharRest(char c2) {
        return (c2 >= '0' && c2 <= '9') || (c2 >= 'a' && c2 <= 'f') || ((c2 >= 'A' && c2 <= 'F') || c2 == '.');
    }

    /* access modifiers changed from: private */
    public static boolean isDigitCharStart(char c2) {
        return (c2 >= '0' && c2 <= '9') || c2 == '.';
    }

    /* access modifiers changed from: private */
    public static boolean isFunctionCharRest(char c2) {
        return isFunctionCharStart(c2) || c2 == '_' || (c2 >= '0' && c2 <= '9');
    }

    /* access modifiers changed from: private */
    public static boolean isFunctionCharStart(char c2) {
        return (c2 >= 'a' && c2 <= 'z') || (c2 >= 'A' && c2 <= 'Z');
    }

    /* access modifiers changed from: private */
    public static boolean isVariableChar(char c2) {
        return (c2 >= 'a' && c2 <= 'z') || (c2 >= 'A' && c2 <= 'Z') || c2 == '_' || c2 == '.' || (c2 >= '0' && c2 <= '9');
    }

    public void accept(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }

    public abstract double evaluate();

    public String evaluateStr() {
        return null;
    }

    public boolean isNull() {
        return false;
    }

    public BigDecimal preciseEvaluate() {
        try {
            return BigDecimal.valueOf(evaluate());
        } catch (NumberFormatException unused) {
            return null;
        }
    }
}
