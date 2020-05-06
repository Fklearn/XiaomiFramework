package miui.keyguard.clock;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.system.internal.R;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import miui.date.Calendar;
import miui.date.DateUtils;
import miui.keyguard.clock.KeyguardClockController;
import miui.telephony.phonenumber.Prefix;

public class MiuiDualClock extends RelativeLayout implements KeyguardClockController.IClockView {
    private static final String TAG = "MiuiDualClock";
    private static final String WEATHER_URI = "content://weather/realtimeLocalWeatherData/4/1";
    private boolean m24HourFormat;
    private boolean mAttached;
    /* access modifiers changed from: private */
    public boolean mAutoTimeZone;
    ContentObserver mAutoTimeZoneObserver;
    private Calendar mCalendar;
    /* access modifiers changed from: private */
    public Context mContext;
    private String mCountry;
    private boolean mDateTooLong;
    private String mLanguage;
    /* access modifiers changed from: private */
    public TextView mLocalCity;
    /* access modifiers changed from: private */
    public OnLocalCityChangeListener mLocalCityChangeListener;
    private TextView mLocalDate;
    private TextView mLocalTime;
    private String mLocalTimeZone;
    private Calendar mResidentCalendar;
    private TextView mResidentCity;
    private TextView mResidentDate;
    private LinearLayout mResidentLayout;
    private TextView mResidentTime;
    private String mResidentTimeZone;
    protected float mScaleRatio;

    public interface OnLocalCityChangeListener {
        void onLocalCityChanged(String str);
    }

    public MiuiDualClock(Context context) {
        this(context, (AttributeSet) null);
    }

    public MiuiDualClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLanguage = Prefix.EMPTY;
        this.mCountry = Locale.getDefault().getCountry();
        boolean z = true;
        this.mAutoTimeZone = true;
        this.mDateTooLong = false;
        this.mScaleRatio = 1.0f;
        this.mAutoTimeZoneObserver = new ContentObserver(new Handler()) {
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                MiuiDualClock miuiDualClock = MiuiDualClock.this;
                boolean z = false;
                if (Settings.Global.getInt(miuiDualClock.mContext.getContentResolver(), "auto_time_zone", 0) > 0) {
                    z = true;
                }
                boolean unused = miuiDualClock.mAutoTimeZone = z;
                MiuiDualClock.this.updateLocalCity();
            }
        };
        this.mContext = context;
        this.mAutoTimeZone = Settings.Global.getInt(this.mContext.getContentResolver(), "auto_time_zone", 0) <= 0 ? false : z;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mLocalCity = (TextView) findViewById(R.id.local_city_name);
        this.mLocalTime = (TextView) findViewById(R.id.local_time);
        this.mLocalTime.setAccessibilityDelegate(new MiuiClockAccessibilityDelegate(this.mContext));
        this.mLocalDate = (TextView) findViewById(R.id.local_date);
        this.mResidentCity = (TextView) findViewById(R.id.resident_city_name);
        this.mResidentTime = (TextView) findViewById(R.id.resident_time);
        this.mResidentTime.setAccessibilityDelegate(new MiuiClockAccessibilityDelegate(this.mContext));
        this.mResidentDate = (TextView) findViewById(R.id.resident_date);
        this.mResidentLayout = (LinearLayout) findViewById(R.id.resident_time_layout);
        this.mLanguage = this.mContext.getResources().getConfiguration().locale.getLanguage();
        TimeZone tz = TimeZone.getDefault();
        this.mLocalTimeZone = tz.getID();
        updateLocalCity();
        this.mCalendar = new Calendar();
        if (TextUtils.isEmpty(this.mResidentTimeZone)) {
            this.mResidentTimeZone = tz.getID();
        }
        updateResidentCityName();
        this.mResidentCalendar = new Calendar(TimeZone.getTimeZone(this.mResidentTimeZone));
        updateHourFormat();
        updateTime();
        updateDateLines();
    }

    /* access modifiers changed from: protected */
    public void updateViewsTextSize() {
        Resources resources = this.mContext.getResources();
        int cityTextSize = (int) (this.mScaleRatio * ((float) resources.getDimensionPixelSize(R.dimen.miui_dual_clock_city_text_size)));
        this.mLocalCity.setTextSize(0, (float) cityTextSize);
        this.mResidentCity.setTextSize(0, (float) cityTextSize);
        this.mLocalDate.setTextSize(0, (float) cityTextSize);
        this.mResidentDate.setTextSize(0, (float) cityTextSize);
        int timeTextSize = (int) (this.mScaleRatio * ((float) resources.getDimensionPixelSize(R.dimen.miui_dual_clock_time_text_size)));
        this.mLocalTime.setTextSize(0, (float) timeTextSize);
        this.mResidentTime.setTextSize(0, (float) timeTextSize);
    }

    /* access modifiers changed from: protected */
    public void updateViewsLayoutParams() {
        Resources resources = this.mContext.getResources();
        FrameLayout.LayoutParams clockLayoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        clockLayoutParams.topMargin = (int) (this.mScaleRatio * ((float) resources.getDimensionPixelSize(R.dimen.miui_dual_clock_margin_top)));
        setLayoutParams(clockLayoutParams);
        int timeTopMargin = (int) (this.mScaleRatio * ((float) resources.getDimensionPixelSize(R.dimen.miui_dual_clock_time_margin_top)));
        int dateTopMargin = (int) (this.mScaleRatio * ((float) resources.getDimensionPixelSize(R.dimen.miui_dual_clock_date_margin_top)));
        LinearLayout.LayoutParams localTimeInfoLayoutParams = (LinearLayout.LayoutParams) this.mLocalTime.getLayoutParams();
        localTimeInfoLayoutParams.topMargin = timeTopMargin;
        this.mLocalTime.setLayoutParams(localTimeInfoLayoutParams);
        LinearLayout.LayoutParams localDateInfoLayoutParams = (LinearLayout.LayoutParams) this.mLocalDate.getLayoutParams();
        localDateInfoLayoutParams.topMargin = dateTopMargin;
        this.mLocalDate.setLayoutParams(localDateInfoLayoutParams);
        LinearLayout.LayoutParams residentTimeInfoLayoutParams = (LinearLayout.LayoutParams) this.mResidentTime.getLayoutParams();
        residentTimeInfoLayoutParams.topMargin = timeTopMargin;
        this.mResidentTime.setLayoutParams(residentTimeInfoLayoutParams);
        LinearLayout.LayoutParams residentDateInfoLayoutParams = (LinearLayout.LayoutParams) this.mResidentDate.getLayoutParams();
        residentDateInfoLayoutParams.topMargin = dateTopMargin;
        this.mResidentDate.setLayoutParams(residentDateInfoLayoutParams);
        RelativeLayout.LayoutParams residentLayoutParams = (RelativeLayout.LayoutParams) this.mResidentLayout.getLayoutParams();
        residentLayoutParams.setMarginStart((int) (this.mScaleRatio * ((float) resources.getDimensionPixelSize(R.dimen.miui_resident_time_margin_start))));
        this.mResidentLayout.setLayoutParams(residentLayoutParams);
    }

    public void updateHourFormat() {
        this.m24HourFormat = DateFormat.is24HourFormat(this.mContext);
    }

    public void setIs24HourFormat(boolean is24HourFormat) {
        this.m24HourFormat = is24HourFormat;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.mAttached) {
            this.mAttached = true;
            this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("auto_time_zone"), false, this.mAutoTimeZoneObserver);
            this.mAutoTimeZoneObserver.onChange(false);
            updateViewsLayoutParams();
            updateViewsTextSize();
        }
    }

    public void setTextColorDark(boolean textDark) {
        int color = textDark ? getContext().getResources().getColor(R.color.miui_common_time_dark_text_color) : -1;
        this.mLocalCity.setTextColor(color);
        this.mLocalTime.setTextColor(color);
        this.mLocalDate.setTextColor(color);
        this.mResidentCity.setTextColor(color);
        this.mResidentTime.setTextColor(color);
        this.mResidentDate.setTextColor(color);
    }

    public int getClockHeight() {
        return getHeight();
    }

    public float getClockVisibleHeight() {
        return (float) getHeight();
    }

    public void setClockAlpha(float alpha) {
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mAttached) {
            this.mAttached = false;
            this.mContext.getContentResolver().unregisterContentObserver(this.mAutoTimeZoneObserver);
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        String language = newConfig.locale.getLanguage();
        String country = newConfig.locale.getCountry();
        if ((language != null && !language.equals(this.mLanguage)) || (country != null && !country.equals(this.mCountry))) {
            updateResidentCityName();
            updateLocalCity();
            this.mDateTooLong = false;
            updateTime();
            updateDateLines();
            this.mLanguage = language;
            this.mCountry = country;
        }
    }

    private void updateDateLines() {
        boolean dateTooLong = ((float) (((int) this.mLocalDate.getPaint().measureText(this.mLocalDate.getText().toString())) + ((int) this.mResidentDate.getPaint().measureText(this.mResidentDate.getText().toString())))) > getResources().getDimension(R.dimen.miui_dual_clock_max_width) * 2.0f;
        if (dateTooLong != this.mDateTooLong) {
            this.mDateTooLong = dateTooLong;
            updateTime();
        }
    }

    public void updateTime() {
        updateTime(this.mCalendar, this.mLocalTime, this.mLocalDate);
        updateTime(this.mResidentCalendar, this.mResidentTime, this.mResidentDate);
    }

    public void updateTime(Calendar calendar, TextView timeView, TextView dateView) {
        int dateResId;
        calendar.setTimeInMillis(System.currentTimeMillis());
        timeView.setText(DateUtils.formatDateTime(System.currentTimeMillis(), (this.m24HourFormat ? 32 : 16) | 12 | 64, calendar.getTimeZone()));
        if (this.mDateTooLong) {
            dateResId = this.m24HourFormat ? R.string.miui_lock_screen_date_two_lines : R.string.miui_lock_screen_date_two_lines_12;
        } else {
            dateResId = this.m24HourFormat != 0 ? R.string.miui_lock_screen_date : R.string.miui_lock_screen_date_12;
        }
        dateView.setText(calendar.format(this.mContext.getString(dateResId)));
    }

    public void updateTimeZone(String timeZone) {
        if (!TextUtils.isEmpty(timeZone)) {
            this.mLocalTimeZone = timeZone;
            Log.i(TAG, "update local timeZone:" + this.mLocalTimeZone);
            this.mCalendar = new Calendar(TimeZone.getTimeZone(this.mLocalTimeZone));
            updateTime();
            updateLocalCity();
        }
    }

    public void updateResidentTimeZone(String residentTimeZone) {
        if (!TextUtils.isEmpty(residentTimeZone)) {
            this.mResidentTimeZone = residentTimeZone;
            Log.i(TAG, "update resident timeZone:" + this.mResidentTimeZone);
            this.mResidentCalendar = new Calendar(TimeZone.getTimeZone(this.mResidentTimeZone));
            updateTime();
            updateResidentCityName();
        }
    }

    private void updateResidentCityName() {
        this.mResidentCity.setText(getNamebyZone(this.mResidentTimeZone));
    }

    private String getNamebyZone(String zone) {
        String str = zone;
        try {
            Class clz = Class.forName("android.icu.text.TimeZoneNames");
            Method getInstance = clz.getDeclaredMethod("getInstance", new Class[]{Locale.class});
            getInstance.setAccessible(true);
            Object timeZoneNames = getInstance.invoke(clz, new Object[]{Locale.getDefault()});
            if (str == null || !str.equals("Asia/Shanghai")) {
                Method getExemplarLocationName = clz.getDeclaredMethod("getExemplarLocationName", new Class[]{String.class});
                getExemplarLocationName.setAccessible(true);
                return (String) getExemplarLocationName.invoke(timeZoneNames, new Object[]{str});
            }
            Class nameType = Class.forName("android.icu.text.TimeZoneNames$NameType");
            Object longStandard = new Object();
            Object longStandard2 = longStandard;
            for (Object constant : nameType.getEnumConstants()) {
                if (constant.toString().equalsIgnoreCase("LONG_STANDARD")) {
                    longStandard2 = constant;
                }
            }
            Method getDisplayName = clz.getDeclaredMethod("getDisplayName", new Class[]{String.class, nameType, Long.TYPE});
            getDisplayName.setAccessible(true);
            return (String) getDisplayName.invoke(timeZoneNames, new Object[]{"Asia/Shanghai", longStandard2, Long.valueOf(new Date().getTime())});
        } catch (Exception e) {
            e.printStackTrace();
            return this.mContext.getString(R.string.miui_clock_city_name_second);
        }
    }

    /* access modifiers changed from: private */
    public void updateLocalCity() {
        if (this.mAutoTimeZone) {
            new AsyncTask<Void, Void, String>() {
                /* access modifiers changed from: protected */
                /* JADX WARNING: Code restructure failed: missing block: B:15:0x0055, code lost:
                    if (r2 == null) goto L_0x0058;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:16:0x0058, code lost:
                    return r1;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:8:0x0046, code lost:
                    if (r2 != null) goto L_0x0048;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:9:0x0048, code lost:
                    r2.close();
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public java.lang.String doInBackground(java.lang.Void... r11) {
                    /*
                        r10 = this;
                        java.lang.String r0 = "MiuiDualClock"
                        java.lang.String r1 = ""
                        r2 = 0
                        miui.keyguard.clock.MiuiDualClock r3 = miui.keyguard.clock.MiuiDualClock.this     // Catch:{ Exception -> 0x004e }
                        android.content.Context r3 = r3.mContext     // Catch:{ Exception -> 0x004e }
                        android.content.ContentResolver r4 = r3.getContentResolver()     // Catch:{ Exception -> 0x004e }
                        java.lang.String r3 = "content://weather/realtimeLocalWeatherData/4/1"
                        android.net.Uri r5 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x004e }
                        r6 = 0
                        r7 = 0
                        r8 = 0
                        r9 = 0
                        android.database.Cursor r3 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x004e }
                        r2 = r3
                        if (r2 == 0) goto L_0x0032
                    L_0x0020:
                        boolean r3 = r2.moveToNext()     // Catch:{ Exception -> 0x004e }
                        if (r3 == 0) goto L_0x0032
                        java.lang.String r3 = "city_name"
                        int r3 = r2.getColumnIndex(r3)     // Catch:{ Exception -> 0x004e }
                        java.lang.String r3 = r2.getString(r3)     // Catch:{ Exception -> 0x004e }
                        r1 = r3
                        goto L_0x0020
                    L_0x0032:
                        java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x004e }
                        r3.<init>()     // Catch:{ Exception -> 0x004e }
                        java.lang.String r4 = "update local city name, city="
                        r3.append(r4)     // Catch:{ Exception -> 0x004e }
                        r3.append(r1)     // Catch:{ Exception -> 0x004e }
                        java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x004e }
                        android.util.Log.i(r0, r3)     // Catch:{ Exception -> 0x004e }
                        if (r2 == 0) goto L_0x0058
                    L_0x0048:
                        r2.close()
                        goto L_0x0058
                    L_0x004c:
                        r0 = move-exception
                        goto L_0x0059
                    L_0x004e:
                        r3 = move-exception
                        java.lang.String r4 = "get city exception"
                        android.util.Log.e(r0, r4, r3)     // Catch:{ all -> 0x004c }
                        if (r2 == 0) goto L_0x0058
                        goto L_0x0048
                    L_0x0058:
                        return r1
                    L_0x0059:
                        if (r2 == 0) goto L_0x005e
                        r2.close()
                    L_0x005e:
                        throw r0
                    */
                    throw new UnsupportedOperationException("Method not decompiled: miui.keyguard.clock.MiuiDualClock.AnonymousClass2.doInBackground(java.lang.Void[]):java.lang.String");
                }

                /* access modifiers changed from: protected */
                public void onPostExecute(String city) {
                    String city2 = TextUtils.isEmpty(city) ? MiuiDualClock.this.mContext.getString(R.string.miui_clock_city_name_local) : city;
                    MiuiDualClock.this.mLocalCity.setText(city2);
                    if (MiuiDualClock.this.mLocalCityChangeListener != null) {
                        MiuiDualClock.this.mLocalCityChangeListener.onLocalCityChanged(city2);
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            return;
        }
        String city = getNamebyZone(this.mLocalTimeZone);
        this.mLocalCity.setText(city);
        OnLocalCityChangeListener onLocalCityChangeListener = this.mLocalCityChangeListener;
        if (onLocalCityChangeListener != null) {
            onLocalCityChangeListener.onLocalCityChanged(city);
        }
    }

    public float getTopMargin() {
        return (float) this.mContext.getResources().getDimensionPixelSize(R.dimen.miui_dual_clock_margin_top);
    }

    public void setShowLunarCalendar(boolean showLunarCalendar) {
    }

    public void setScaleRatio(float ratio) {
        this.mScaleRatio = ratio;
        updateViewsTextSize();
        updateViewsLayoutParams();
    }

    public void updateViewTopMargin(boolean hasTopMargin) {
        FrameLayout.LayoutParams clockLayoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        if (hasTopMargin) {
            clockLayoutParams.topMargin = (int) (this.mScaleRatio * ((float) this.mContext.getResources().getDimensionPixelSize(R.dimen.miui_dual_clock_margin_top)));
        } else {
            clockLayoutParams.topMargin = 0;
        }
        setLayoutParams(clockLayoutParams);
    }

    public void setOwnerInfo(String ownerInfo) {
    }

    public void setOnLocalCityChangeListener(OnLocalCityChangeListener localCityChangeListener) {
        this.mLocalCityChangeListener = localCityChangeListener;
    }
}
