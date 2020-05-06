package com.miui.maml.elements;

import android.text.TextUtils;
import android.util.Log;
import com.miui.luckymoney.config.Constants;
import com.miui.maml.data.Expression;
import com.miui.maml.util.TextFormatter;
import java.util.TimeZone;
import miui.date.Calendar;

public class DateTimeScreenElement extends TextScreenElement {
    public static final String TAG_NAME = "DateTime";
    private DateFormatter mDateFormatter;
    private DateFormatter mDescriptionDateFormatter;

    class DateFormatter {
        private Calendar mCalendar;
        private int mCurDay;
        private String mLunarDate;
        private String mOldFormat;
        private long mPreValue;
        private String mText;
        private TextFormatter mTextFormatter;
        private Expression mTimeZoneExp;
        private Expression mValueExp;

        public DateFormatter(DateTimeScreenElement dateTimeScreenElement, TextFormatter textFormatter, Expression expression) {
            this(textFormatter, expression, (Expression) null);
        }

        public DateFormatter(TextFormatter textFormatter, Expression expression, Expression expression2) {
            this.mCalendar = new Calendar();
            this.mCurDay = -1;
            this.mTextFormatter = textFormatter;
            this.mValueExp = expression;
            this.mTimeZoneExp = expression2;
        }

        public String getText() {
            String format;
            TextFormatter textFormatter = this.mTextFormatter;
            if (textFormatter == null || (format = textFormatter.getFormat()) == null) {
                return "";
            }
            Expression expression = this.mValueExp;
            long evaluate = expression != null ? (long) DateTimeScreenElement.this.evaluate(expression) : System.currentTimeMillis();
            if (TextUtils.equals(this.mOldFormat, format) && Math.abs(evaluate - this.mPreValue) < 200) {
                return this.mText;
            }
            this.mOldFormat = format;
            this.mCalendar.setTimeInMillis(evaluate);
            Expression expression2 = this.mTimeZoneExp;
            if (expression2 != null) {
                String evaluateStr = expression2.evaluateStr();
                if (!TextUtils.isEmpty(evaluateStr)) {
                    this.mCalendar.setTimeZone(TimeZone.getTimeZone(evaluateStr));
                }
            }
            if (format.contains("NNNN")) {
                if (this.mCalendar.get(9) != this.mCurDay) {
                    this.mLunarDate = this.mCalendar.format("N月e");
                    String format2 = this.mCalendar.format(Constants.JSON_KEY_T);
                    if (format2 != null) {
                        this.mLunarDate += " " + format2;
                    }
                    this.mCurDay = this.mCalendar.get(9);
                    Log.i("DateTimeScreenElement", "get lunar date:" + this.mLunarDate);
                }
                format = format.replace("NNNN", this.mLunarDate);
            }
            this.mText = this.mCalendar.format(format);
            this.mPreValue = evaluate;
            return this.mText;
        }

        public void resetCalendar() {
            this.mCalendar = new Calendar();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0075  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public DateTimeScreenElement(org.w3c.dom.Element r11, com.miui.maml.ScreenElementRoot r12) {
        /*
            r10 = this;
            r10.<init>(r11, r12)
            com.miui.maml.data.Variables r12 = r10.getVariables()
            java.lang.String r0 = "value"
            java.lang.String r0 = r11.getAttribute(r0)
            com.miui.maml.data.Expression r12 = com.miui.maml.data.Expression.build(r12, r0)
            com.miui.maml.data.Variables r0 = r10.getVariables()
            java.lang.String r1 = "timeZoneId"
            java.lang.String r1 = r11.getAttribute(r1)
            com.miui.maml.data.Expression r0 = com.miui.maml.data.Expression.build(r0, r1)
            com.miui.maml.elements.DateTimeScreenElement$DateFormatter r1 = new com.miui.maml.elements.DateTimeScreenElement$DateFormatter
            com.miui.maml.util.TextFormatter r2 = r10.mFormatter
            r1.<init>(r2, r12, r0)
            r10.mDateFormatter = r1
            java.lang.String r1 = "contentDescriptionFormat"
            java.lang.String r1 = r11.getAttribute(r1)
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            r2 = 1
            if (r1 != 0) goto L_0x004e
            r10.mHasContentDescription = r2
            com.miui.maml.data.Variables r3 = r10.getVariables()
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            java.lang.String r6 = "contentDescriptionFormat"
            r4 = r11
            com.miui.maml.util.TextFormatter r11 = com.miui.maml.util.TextFormatter.fromElement(r3, r4, r5, r6, r7, r8, r9)
            com.miui.maml.elements.DateTimeScreenElement$DateFormatter r1 = new com.miui.maml.elements.DateTimeScreenElement$DateFormatter
            r1.<init>(r11, r12, r0)
        L_0x004b:
            r10.mDescriptionDateFormatter = r1
            goto L_0x0071
        L_0x004e:
            java.lang.String r1 = "contentDescriptionFormatExp"
            java.lang.String r1 = r11.getAttribute(r1)
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0071
            r10.mHasContentDescription = r2
            com.miui.maml.data.Variables r3 = r10.getVariables()
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            java.lang.String r9 = "contentDescriptionFormatExp"
            r4 = r11
            com.miui.maml.util.TextFormatter r11 = com.miui.maml.util.TextFormatter.fromElement(r3, r4, r5, r6, r7, r8, r9)
            com.miui.maml.elements.DateTimeScreenElement$DateFormatter r1 = new com.miui.maml.elements.DateTimeScreenElement$DateFormatter
            r1.<init>(r11, r12, r0)
            goto L_0x004b
        L_0x0071:
            boolean r11 = r10.mHasContentDescription
            if (r11 == 0) goto L_0x007a
            com.miui.maml.ScreenElementRoot r11 = r10.mRoot
            r11.addAccessibleElements(r10)
        L_0x007a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.DateTimeScreenElement.<init>(org.w3c.dom.Element, com.miui.maml.ScreenElementRoot):void");
    }

    public String getContentDescription() {
        DateFormatter dateFormatter = this.mDescriptionDateFormatter;
        return dateFormatter != null ? dateFormatter.getText() : super.getContentDescription();
    }

    /* access modifiers changed from: protected */
    public String getText() {
        return this.mDateFormatter.getText();
    }

    public void resume() {
        super.resume();
        this.mDateFormatter.resetCalendar();
        DateFormatter dateFormatter = this.mDescriptionDateFormatter;
        if (dateFormatter != null) {
            dateFormatter.resetCalendar();
        }
    }
}
