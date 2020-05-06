package miui.keyguard.clock;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.system.internal.R;
import java.util.Locale;
import java.util.TimeZone;
import miui.date.Calendar;
import miui.keyguard.clock.KeyguardClockController;

public class MiuiBaseClock extends LinearLayout implements KeyguardClockController.IClockView {
    protected boolean m24HourFormat;
    protected Calendar mCalendar;
    private int mCalendarDayOfWeek;
    protected Context mContext;
    protected TextView mCurrentDate;
    protected int mDensityDpi;
    protected float mFontScale;
    protected boolean mFontScaleChanged;
    protected boolean mHasTopMargin;
    protected String mLanguage;
    protected TextView mLunarCalendarInfo;
    protected TextView mOwnerInfo;
    protected Resources mResources;
    protected float mScaleRatio;
    private boolean mShowLunarCalendar;
    protected boolean mTextDark;
    protected int mUserId;

    public MiuiBaseClock(Context context) {
        this(context, (AttributeSet) null);
    }

    public MiuiBaseClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = null;
        this.mResources = null;
        this.mTextDark = false;
        this.mHasTopMargin = true;
        this.mScaleRatio = 1.0f;
        this.mContext = context;
        this.mResources = this.mContext.getResources();
        updateHourFormat();
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mCurrentDate = (TextView) findViewById(R.id.current_date);
        this.mLunarCalendarInfo = (TextView) findViewById(R.id.unlock_screen_lunar_calendar_info);
        this.mOwnerInfo = (TextView) findViewById(R.id.unlock_screen_owner_info);
        this.mCalendar = new Calendar();
        updateLunarCalendarInfo();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateViewsLayoutParams();
        updateViewsTextSize();
    }

    public void updateHourFormat() {
        this.m24HourFormat = DateFormat.is24HourFormat(this.mContext);
    }

    public void setIs24HourFormat(boolean is24HourFormat) {
        this.m24HourFormat = is24HourFormat;
    }

    public View getLunarCalendarView() {
        return this.mLunarCalendarInfo;
    }

    public void updateLunarCalendarInfo() {
        if (!Locale.CHINESE.getLanguage().equals(Locale.getDefault().getLanguage()) || !this.mShowLunarCalendar) {
            this.mLunarCalendarInfo.setVisibility(8);
            return;
        }
        Calendar calendar = new Calendar();
        this.mLunarCalendarInfo.setVisibility(0);
        this.mLunarCalendarInfo.setText(calendar.format("YY年 N月e"));
    }

    public float getTopMargin() {
        return 0.0f;
    }

    public void setShowLunarCalendar(boolean showLunarCalendar) {
        this.mShowLunarCalendar = showLunarCalendar;
        updateLunarCalendarInfo();
    }

    public void setScaleRatio(float ratio) {
        this.mScaleRatio = ratio;
        updateViewsTextSize();
        try {
            updateViewsLayoutParams();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateViewTopMargin(boolean hasTopMargin) {
        this.mHasTopMargin = hasTopMargin;
        try {
            updateViewsLayoutParams();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOwnerInfo(String ownerInfo) {
        if (TextUtils.isEmpty(ownerInfo)) {
            this.mOwnerInfo.setVisibility(8);
            return;
        }
        this.mOwnerInfo.setVisibility(0);
        this.mOwnerInfo.setText(ownerInfo);
    }

    public void setTextColorDark(boolean textDark) {
        int ownerInfoColor;
        if (textDark) {
            ownerInfoColor = getContext().getResources().getColor(R.color.miui_owner_info_dark_text_color);
        } else {
            ownerInfoColor = getContext().getResources().getColor(R.color.miui_owner_info_light_text_color);
        }
        this.mOwnerInfo.setTextColor(ownerInfoColor);
    }

    /* access modifiers changed from: protected */
    public void setInfoDarkMode(int color) {
        this.mCurrentDate.setTextColor(color);
        this.mLunarCalendarInfo.setTextColor(color);
    }

    public int getClockHeight() {
        if (getHeight() > 0) {
            return getHeight();
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        float fontScale = newConfig.fontScale;
        if (this.mFontScale != fontScale) {
            this.mFontScaleChanged = true;
            updateViewsTextSize();
            this.mFontScale = fontScale;
        }
        int densityDpi = newConfig.densityDpi;
        if (this.mDensityDpi != densityDpi) {
            this.mFontScaleChanged = true;
            updateViewsTextSize();
            updateViewsLayoutParams();
            this.mDensityDpi = densityDpi;
        }
        String language = newConfig.locale.getLanguage();
        if (!TextUtils.isEmpty(language) && !language.equals(this.mLanguage)) {
            this.mLanguage = language;
        }
    }

    public void updateTimeZone(String timeZone) {
        if (!TextUtils.isEmpty(timeZone)) {
            this.mCalendar = new Calendar(TimeZone.getTimeZone(timeZone));
            updateTime();
        }
    }

    public void updateResidentTimeZone(String residentTimezone) {
    }

    public void updateTime() {
        int dateResId;
        this.mCalendar.setTimeInMillis(System.currentTimeMillis());
        if (this.m24HourFormat) {
            dateResId = R.string.miui_lock_screen_date;
        } else {
            dateResId = R.string.miui_lock_screen_date_12;
        }
        this.mCurrentDate.setText(this.mCalendar.format(this.mContext.getString(dateResId)));
        int day = this.mCalendar.get(14);
        if (day != this.mCalendarDayOfWeek) {
            updateLunarCalendarInfo();
            this.mCalendarDayOfWeek = day;
        }
    }

    /* access modifiers changed from: protected */
    public void updateViewsTextSize() {
        Resources resources = this.mContext.getResources();
        int size = (int) (this.mScaleRatio * ((float) resources.getDimensionPixelSize(R.dimen.miui_clock_date_text_size)));
        this.mCurrentDate.setTextSize(0, (float) size);
        this.mLunarCalendarInfo.setTextSize(0, (float) size);
        this.mOwnerInfo.setTextSize(0, (float) ((int) (this.mScaleRatio * ((float) resources.getDimensionPixelSize(R.dimen.miui_clock_date_text_size)))));
    }

    /* access modifiers changed from: protected */
    public void updateViewsLayoutParams() {
    }

    public void setClockAlpha(float alpha) {
        setAlpha(alpha);
    }

    public float getClockVisibleHeight() {
        if (getHeight() > 0) {
            return (float) getHeight();
        }
        return 0.0f;
    }
}
