package com.miui.maml.elements;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.util.Utils;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.TimeZone;
import org.w3c.dom.Element;

public class TimepanelScreenElement extends ImageScreenElement {
    private static final String LOG_TAG = "TimepanelScreenElement";
    private static final String M12 = "hh:mm";
    private static final String M24 = "kk:mm";
    public static final String TAG_NAME = "Time";
    private int mBmpHeight;
    /* access modifiers changed from: private */
    public int mBmpWidth;
    protected Calendar mCalendar = Calendar.getInstance();
    /* access modifiers changed from: private */
    public boolean mForceUpdate;
    private String mFormat;
    private Expression mFormatExp;
    private String mFormatRaw;
    /* access modifiers changed from: private */
    public boolean mLoadResourceFailed;
    private char mLocalizedZero = DecimalFormatSymbols.getInstance().getZeroDigit();
    private String mOldFormat;
    private String mOldSrc;
    private long mPreMinute;
    /* access modifiers changed from: private */
    public CharSequence mPreTime;
    /* access modifiers changed from: private */
    public int mSpace;
    /* access modifiers changed from: private */
    public Expression mTimeZoneExp;
    private Runnable mUpdateTimeRunnable = new Runnable() {
        public void run() {
            Bitmap bitmap;
            if (!TimepanelScreenElement.this.mLoadResourceFailed && (bitmap = TimepanelScreenElement.this.mBitmap.getBitmap()) != null) {
                TimepanelScreenElement.this.mCalendar.setTimeInMillis(System.currentTimeMillis());
                if (TimepanelScreenElement.this.mTimeZoneExp != null) {
                    String evaluateStr = TimepanelScreenElement.this.mTimeZoneExp.evaluateStr();
                    if (!TextUtils.isEmpty(evaluateStr)) {
                        TimepanelScreenElement.this.mCalendar.setTimeZone(TimeZone.getTimeZone(evaluateStr));
                    }
                }
                CharSequence format = DateFormat.format(TimepanelScreenElement.this.getFormat(), TimepanelScreenElement.this.mCalendar);
                if (TimepanelScreenElement.this.mForceUpdate || !format.equals(TimepanelScreenElement.this.mPreTime)) {
                    CharSequence unused = TimepanelScreenElement.this.mPreTime = format;
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                    int i = 0;
                    for (int i2 = 0; i2 < format.length(); i2++) {
                        Bitmap access$500 = TimepanelScreenElement.this.getDigitBmp(format.charAt(i2));
                        if (access$500 != null) {
                            canvas.drawBitmap(access$500, (float) i, 0.0f, (Paint) null);
                            i = i + access$500.getWidth() + TimepanelScreenElement.this.mSpace;
                        }
                    }
                    TimepanelScreenElement.this.mBitmap.updateVersion();
                    TimepanelScreenElement timepanelScreenElement = TimepanelScreenElement.this;
                    int unused2 = timepanelScreenElement.mBmpWidth = i - timepanelScreenElement.mSpace;
                    TimepanelScreenElement timepanelScreenElement2 = TimepanelScreenElement.this;
                    timepanelScreenElement2.setActualWidth(timepanelScreenElement2.descale((double) timepanelScreenElement2.mBmpWidth));
                    TimepanelScreenElement.this.requestUpdate();
                }
            }
        }
    };

    public TimepanelScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mFormatRaw = getAttr(element, "format");
        this.mFormatExp = Expression.build(getVariables(), getAttr(element, "formatExp"));
        this.mSpace = (int) scale((double) getAttrAsInt(element, "space", 0));
        this.mTimeZoneExp = Expression.build(getVariables(), getAttr(element, "timeZoneId"));
    }

    private void createBitmap() {
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < 11; i3++) {
            Bitmap digitBmp = getDigitBmp("0123456789:".charAt(i3));
            if (digitBmp == null) {
                this.mLoadResourceFailed = true;
                Log.e(LOG_TAG, "Failed to load digit bitmap: " + "0123456789:".charAt(i3));
                return;
            }
            if (i < digitBmp.getWidth()) {
                i = digitBmp.getWidth();
            }
            if (this.mBmpHeight < digitBmp.getHeight()) {
                this.mBmpHeight = digitBmp.getHeight();
            }
            if (i2 == 0) {
                i2 = digitBmp.getDensity();
            }
        }
        Bitmap createBitmap = Bitmap.createBitmap((i * 5) + (this.mSpace * 4), this.mBmpHeight, Bitmap.Config.ARGB_8888);
        createBitmap.setDensity(i2);
        this.mBitmap.setBitmap(createBitmap);
        setActualHeight(descale((double) this.mBmpHeight));
    }

    /* access modifiers changed from: private */
    public Bitmap getDigitBmp(char c2) {
        String str;
        String src = getSrc();
        if (TextUtils.isEmpty(src)) {
            src = "time.png";
        }
        if (c2 == ':') {
            str = "dot";
        } else {
            char c3 = this.mLocalizedZero;
            if (c2 >= c3 && c2 <= c3 + 9) {
                c2 = (char) ((c2 - c3) + 48);
            }
            str = String.valueOf(c2);
        }
        return getContext().mResourceManager.getBitmap(Utils.addFileNameSuffix(src, str));
    }

    /* access modifiers changed from: private */
    public String getFormat() {
        Expression expression = this.mFormatExp;
        return expression != null ? expression.evaluateStr() : this.mFormat;
    }

    private void setDateFormat() {
        this.mFormat = (!TextUtils.isEmpty(this.mFormatRaw) || this.mFormatExp != null) ? this.mFormatRaw : DateFormat.is24HourFormat(getContext().mContext) ? M24 : M12;
    }

    private void updateTime(boolean z) {
        getContext().getHandler().removeCallbacks(this.mUpdateTimeRunnable);
        this.mForceUpdate = z;
        postInMainThread(this.mUpdateTimeRunnable);
    }

    /* access modifiers changed from: protected */
    public void doTick(long j) {
        super.doTick(j);
        long currentTimeMillis = System.currentTimeMillis() / 60000;
        String src = getSrc();
        String format = getFormat();
        if (currentTimeMillis != this.mPreMinute || !TextUtils.equals(src, this.mOldSrc) || !TextUtils.equals(format, this.mOldFormat)) {
            updateTime(true);
            this.mPreMinute = currentTimeMillis;
            this.mOldSrc = src;
            this.mOldFormat = format;
        }
    }

    public void finish() {
        this.mPreTime = null;
        this.mLoadResourceFailed = false;
        getContext().getHandler().removeCallbacks(this.mUpdateTimeRunnable);
        super.finish();
    }

    /* access modifiers changed from: protected */
    public int getBitmapWidth() {
        return this.mBmpWidth;
    }

    public void init() {
        super.init();
        setDateFormat();
        this.mPreTime = null;
        createBitmap();
        updateTime(true);
    }

    public void pause() {
    }

    public void resume() {
        this.mCalendar = Calendar.getInstance();
        this.mLocalizedZero = DecimalFormatSymbols.getInstance().getZeroDigit();
        setDateFormat();
        updateTime(true);
    }
}
