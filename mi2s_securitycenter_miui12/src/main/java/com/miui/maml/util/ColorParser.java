package com.miui.maml.util;

import android.graphics.Color;
import android.util.Log;
import com.miui.maml.StylesManager;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import org.w3c.dom.Element;

public class ColorParser {
    private static final int DEFAULT_COLOR = -16777216;
    private static final String LOG_TAG = "ColorParser";
    private int mColor = -16777216;
    private String mColorExpression;
    private String mCurColorString;
    private IndexedVariable mIndexedColorVar;
    private Expression[] mRGBExpression;
    private ExpressionType mType;

    /* renamed from: com.miui.maml.util.ColorParser$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$util$ColorParser$ExpressionType = new int[ExpressionType.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|8) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        static {
            /*
                com.miui.maml.util.ColorParser$ExpressionType[] r0 = com.miui.maml.util.ColorParser.ExpressionType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$util$ColorParser$ExpressionType = r0
                int[] r0 = $SwitchMap$com$miui$maml$util$ColorParser$ExpressionType     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.util.ColorParser$ExpressionType r1 = com.miui.maml.util.ColorParser.ExpressionType.CONST     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$maml$util$ColorParser$ExpressionType     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.util.ColorParser$ExpressionType r1 = com.miui.maml.util.ColorParser.ExpressionType.VARIABLE     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$maml$util$ColorParser$ExpressionType     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.maml.util.ColorParser$ExpressionType r1 = com.miui.maml.util.ColorParser.ExpressionType.ARGB     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.util.ColorParser.AnonymousClass1.<clinit>():void");
        }
    }

    private enum ExpressionType {
        CONST,
        VARIABLE,
        ARGB,
        INVALID
    }

    public ColorParser(Variables variables, String str) {
        this.mColorExpression = str.trim();
        if (this.mColorExpression.startsWith("#")) {
            this.mType = ExpressionType.CONST;
            try {
                this.mColor = Color.parseColor(this.mColorExpression);
            } catch (IllegalArgumentException unused) {
                this.mColor = -16777216;
            }
        } else if (this.mColorExpression.startsWith("@")) {
            this.mType = ExpressionType.VARIABLE;
            this.mIndexedColorVar = new IndexedVariable(this.mColorExpression.substring(1), variables, false);
        } else if (!this.mColorExpression.startsWith("argb(") || !this.mColorExpression.endsWith(")")) {
            this.mType = ExpressionType.INVALID;
        } else {
            this.mType = ExpressionType.ARGB;
            String str2 = this.mColorExpression;
            this.mRGBExpression = Expression.buildMultiple(variables, str2.substring(5, str2.length() - 1));
            Expression[] expressionArr = this.mRGBExpression;
            if (expressionArr != null && expressionArr.length != 4) {
                Log.e(LOG_TAG, "bad expression format");
                throw new IllegalArgumentException("bad expression format.");
            }
        }
    }

    public static ColorParser fromElement(Variables variables, Element element) {
        return new ColorParser(variables, element.getAttribute(TtmlNode.ATTR_TTS_COLOR));
    }

    public static ColorParser fromElement(Variables variables, Element element, StylesManager.Style style) {
        return new ColorParser(variables, StyleHelper.getAttr(element, TtmlNode.ATTR_TTS_COLOR, style));
    }

    public static ColorParser fromElement(Variables variables, Element element, String str, StylesManager.Style style) {
        return new ColorParser(variables, StyleHelper.getAttr(element, str, style));
    }

    public int getColor() {
        int i = AnonymousClass1.$SwitchMap$com$miui$maml$util$ColorParser$ExpressionType[this.mType.ordinal()];
        if (i != 1) {
            int i2 = -16777216;
            if (i == 2) {
                String string = this.mIndexedColorVar.getString();
                if (!Utils.equals(string, this.mCurColorString)) {
                    if (string != null) {
                        i2 = Color.parseColor(string);
                    }
                    this.mColor = i2;
                    this.mCurColorString = string;
                }
            } else if (i != 3) {
                this.mColor = -16777216;
            } else {
                this.mColor = Color.argb((int) this.mRGBExpression[0].evaluate(), (int) this.mRGBExpression[1].evaluate(), (int) this.mRGBExpression[2].evaluate(), (int) this.mRGBExpression[3].evaluate());
            }
        }
        return this.mColor;
    }
}
