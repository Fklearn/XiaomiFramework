package com.miui.maml.data;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import com.miui.maml.NotifierManager;
import miui.date.Calendar;

public class DateTimeVariableUpdater extends NotifierVariableUpdater {
    private static final String LOG_TAG = "DateTimeVariableUpdater";
    private static final int TIME_DAY = 86400000;
    private static final int TIME_HOUR = 3600000;
    private static final int TIME_MINUTE = 60000;
    private static final int TIME_SECOND = 1000;
    public static final String USE_TAG = "DateTime";
    private static final int[] fields = {22, 21, 20, 18, 9};
    private static Calendar sCalendar;
    private IndexedVariable mAmPm;
    protected Calendar mCalendar;
    private long mCurrentTime;
    private IndexedVariable mDate;
    private IndexedVariable mDateLunar;
    private IndexedVariable mDayOfWeek;
    private volatile boolean mFinished;
    private IndexedVariable mHour12;
    private IndexedVariable mHour24;
    private long mLastUpdatedTime;
    private final Object mLock;
    private IndexedVariable mMinute;
    private IndexedVariable mMonth;
    private IndexedVariable mMonth1;
    private IndexedVariable mMonthLunar;
    private IndexedVariable mMonthLunarLeap;
    private IndexedVariable mNextAlarm;
    private long mNextUpdateTime;
    private IndexedVariable mSecond;
    private IndexedVariable mTime;
    private long mTimeAccuracy;
    private int mTimeAccuracyField;
    private int mTimeFormat;
    private IndexedVariable mTimeFormatVar;
    private IndexedVariable mTimeSys;
    private final Runnable mTimeUpdater;
    private IndexedVariable mYear;
    private IndexedVariable mYearLunar;
    private IndexedVariable mYearLunar1864;

    /* renamed from: com.miui.maml.data.DateTimeVariableUpdater$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$DateTimeVariableUpdater$Accuracy = new int[Accuracy.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(10:0|1|2|3|4|5|6|7|8|10) */
        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        static {
            /*
                com.miui.maml.data.DateTimeVariableUpdater$Accuracy[] r0 = com.miui.maml.data.DateTimeVariableUpdater.Accuracy.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$data$DateTimeVariableUpdater$Accuracy = r0
                int[] r0 = $SwitchMap$com$miui$maml$data$DateTimeVariableUpdater$Accuracy     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.data.DateTimeVariableUpdater$Accuracy r1 = com.miui.maml.data.DateTimeVariableUpdater.Accuracy.Day     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$maml$data$DateTimeVariableUpdater$Accuracy     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.data.DateTimeVariableUpdater$Accuracy r1 = com.miui.maml.data.DateTimeVariableUpdater.Accuracy.Hour     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$maml$data$DateTimeVariableUpdater$Accuracy     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.maml.data.DateTimeVariableUpdater$Accuracy r1 = com.miui.maml.data.DateTimeVariableUpdater.Accuracy.Minute     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                int[] r0 = $SwitchMap$com$miui$maml$data$DateTimeVariableUpdater$Accuracy     // Catch:{ NoSuchFieldError -> 0x0035 }
                com.miui.maml.data.DateTimeVariableUpdater$Accuracy r1 = com.miui.maml.data.DateTimeVariableUpdater.Accuracy.Second     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.DateTimeVariableUpdater.AnonymousClass2.<clinit>():void");
        }
    }

    public enum Accuracy {
        Day,
        Hour,
        Minute,
        Second
    }

    public DateTimeVariableUpdater(VariableUpdaterManager variableUpdaterManager) {
        this(variableUpdaterManager, Accuracy.Minute);
    }

    public DateTimeVariableUpdater(VariableUpdaterManager variableUpdaterManager, Accuracy accuracy) {
        super(variableUpdaterManager, NotifierManager.TYPE_TIME_CHANGED);
        this.mCalendar = new Calendar();
        this.mTimeFormat = -1;
        this.mLock = new Object();
        this.mTimeUpdater = new Runnable() {
            public void run() {
                DateTimeVariableUpdater.this.checkUpdateTime();
            }
        };
        initInner(accuracy);
    }

    public DateTimeVariableUpdater(VariableUpdaterManager variableUpdaterManager, String str) {
        super(variableUpdaterManager, NotifierManager.TYPE_TIME_CHANGED);
        this.mCalendar = new Calendar();
        this.mTimeFormat = -1;
        this.mLock = new Object();
        this.mTimeUpdater = new Runnable() {
            public void run() {
                DateTimeVariableUpdater.this.checkUpdateTime();
            }
        };
        Accuracy accuracy = null;
        if (!TextUtils.isEmpty(str)) {
            for (Accuracy accuracy2 : Accuracy.values()) {
                if (accuracy2.name().equals(str)) {
                    accuracy = accuracy2;
                }
            }
        }
        if (accuracy == null) {
            accuracy = Accuracy.Minute;
            Log.w(LOG_TAG, "invalid accuracy tag:" + str);
        }
        initInner(accuracy);
    }

    /* access modifiers changed from: private */
    public void checkUpdateTime() {
        if (!this.mFinished) {
            synchronized (this.mLock) {
                if (!this.mFinished) {
                    getContext().getHandler().removeCallbacks(this.mTimeUpdater);
                    long currentTimeMillis = System.currentTimeMillis();
                    this.mCalendar.setTimeInMillis(currentTimeMillis);
                    int[] iArr = fields;
                    int length = iArr.length;
                    int i = 0;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= length) {
                            break;
                        }
                        int i3 = iArr[i2];
                        if (i3 == this.mTimeAccuracyField) {
                            break;
                        }
                        this.mCalendar.set(i3, 0);
                        i2++;
                    }
                    if (DateFormat.is24HourFormat(getContext().mContext)) {
                        i = 1;
                    }
                    long timeInMillis = this.mCalendar.getTimeInMillis();
                    if (!(this.mCurrentTime == timeInMillis && this.mTimeFormat == i)) {
                        this.mCurrentTime = timeInMillis;
                        this.mNextUpdateTime = this.mCurrentTime + this.mTimeAccuracy;
                        this.mTimeFormat = i;
                        this.mTimeFormatVar.set((double) this.mTimeFormat);
                        getRoot().requestUpdate();
                    }
                    getContext().getHandler().postDelayed(this.mTimeUpdater, this.mNextUpdateTime - currentTimeMillis);
                }
            }
        }
    }

    public static String formatDate(CharSequence charSequence, long j) {
        if (sCalendar == null) {
            sCalendar = new Calendar();
        }
        sCalendar.setTimeInMillis(j);
        return sCalendar.format(charSequence);
    }

    private void initInner(Accuracy accuracy) {
        int i;
        Log.i(LOG_TAG, "init with accuracy:" + accuracy.name());
        int i2 = AnonymousClass2.$SwitchMap$com$miui$maml$data$DateTimeVariableUpdater$Accuracy[accuracy.ordinal()];
        if (i2 == 1) {
            this.mTimeAccuracy = 86400000;
            i = 9;
        } else if (i2 == 2) {
            this.mTimeAccuracy = 3600000;
            i = 18;
        } else if (i2 == 3 || i2 != 4) {
            this.mTimeAccuracy = 60000;
            this.mTimeAccuracyField = 20;
            Variables variables = getContext().mVariables;
            this.mYear = new IndexedVariable(VariableNames.VAR_YEAR, variables, true);
            this.mMonth = new IndexedVariable(VariableNames.VAR_MONTH, variables, true);
            this.mMonth1 = new IndexedVariable("month1", variables, true);
            this.mDate = new IndexedVariable(VariableNames.VAR_DATE, variables, true);
            this.mYearLunar = new IndexedVariable(VariableNames.VAR_YEAR_LUNAR, variables, true);
            this.mYearLunar1864 = new IndexedVariable(VariableNames.VAR_YEAR_LUNAR1864, variables, true);
            this.mMonthLunar = new IndexedVariable(VariableNames.VAR_MONTH_LUNAR, variables, true);
            this.mMonthLunarLeap = new IndexedVariable(VariableNames.VAR_MONTH_LUNAR_LEAP, variables, true);
            this.mDateLunar = new IndexedVariable(VariableNames.VAR_DATE_LUNAR, variables, true);
            this.mDayOfWeek = new IndexedVariable(VariableNames.VAR_DAY_OF_WEEK, variables, true);
            this.mAmPm = new IndexedVariable(VariableNames.VAR_AMPM, variables, true);
            this.mHour12 = new IndexedVariable(VariableNames.VAR_HOUR12, variables, true);
            this.mHour24 = new IndexedVariable(VariableNames.VAR_HOUR24, variables, true);
            this.mMinute = new IndexedVariable(VariableNames.VAR_MINUTE, variables, true);
            this.mSecond = new IndexedVariable(VariableNames.VAR_SECOND, variables, true);
            this.mTime = new IndexedVariable("time", variables, true);
            this.mTimeSys = new IndexedVariable(VariableNames.VAR_TIME_SYS, variables, true);
            this.mTimeSys.set((double) System.currentTimeMillis());
            this.mNextAlarm = new IndexedVariable(VariableNames.VAR_NEXT_ALARM_TIME, variables, false);
            this.mTimeFormatVar = new IndexedVariable(VariableNames.VAR_TIME_FORMAT, variables, true);
        } else {
            this.mTimeAccuracy = 1000;
            i = 21;
        }
        this.mTimeAccuracyField = i;
        Variables variables2 = getContext().mVariables;
        this.mYear = new IndexedVariable(VariableNames.VAR_YEAR, variables2, true);
        this.mMonth = new IndexedVariable(VariableNames.VAR_MONTH, variables2, true);
        this.mMonth1 = new IndexedVariable("month1", variables2, true);
        this.mDate = new IndexedVariable(VariableNames.VAR_DATE, variables2, true);
        this.mYearLunar = new IndexedVariable(VariableNames.VAR_YEAR_LUNAR, variables2, true);
        this.mYearLunar1864 = new IndexedVariable(VariableNames.VAR_YEAR_LUNAR1864, variables2, true);
        this.mMonthLunar = new IndexedVariable(VariableNames.VAR_MONTH_LUNAR, variables2, true);
        this.mMonthLunarLeap = new IndexedVariable(VariableNames.VAR_MONTH_LUNAR_LEAP, variables2, true);
        this.mDateLunar = new IndexedVariable(VariableNames.VAR_DATE_LUNAR, variables2, true);
        this.mDayOfWeek = new IndexedVariable(VariableNames.VAR_DAY_OF_WEEK, variables2, true);
        this.mAmPm = new IndexedVariable(VariableNames.VAR_AMPM, variables2, true);
        this.mHour12 = new IndexedVariable(VariableNames.VAR_HOUR12, variables2, true);
        this.mHour24 = new IndexedVariable(VariableNames.VAR_HOUR24, variables2, true);
        this.mMinute = new IndexedVariable(VariableNames.VAR_MINUTE, variables2, true);
        this.mSecond = new IndexedVariable(VariableNames.VAR_SECOND, variables2, true);
        this.mTime = new IndexedVariable("time", variables2, true);
        this.mTimeSys = new IndexedVariable(VariableNames.VAR_TIME_SYS, variables2, true);
        this.mTimeSys.set((double) System.currentTimeMillis());
        this.mNextAlarm = new IndexedVariable(VariableNames.VAR_NEXT_ALARM_TIME, variables2, false);
        this.mTimeFormatVar = new IndexedVariable(VariableNames.VAR_TIME_FORMAT, variables2, true);
    }

    private void refreshAlarm() {
        this.mNextAlarm.set((Object) Settings.System.getString(getContext().mContext.getContentResolver(), "next_alarm_formatted"));
    }

    private void updateTime() {
        long currentTimeMillis = System.currentTimeMillis();
        this.mTimeSys.set((double) currentTimeMillis);
        long j = currentTimeMillis / 1000;
        if (j != this.mLastUpdatedTime) {
            this.mCalendar.setTimeInMillis(currentTimeMillis);
            int i = this.mCalendar.get(1);
            int i2 = this.mCalendar.get(5);
            int i3 = this.mCalendar.get(9);
            this.mAmPm.set((double) this.mCalendar.get(17));
            this.mHour24.set((double) this.mCalendar.get(18));
            int i4 = this.mCalendar.get(18) % 12;
            this.mHour12.set(i4 == 0 ? 12.0d : (double) i4);
            this.mMinute.set((double) this.mCalendar.get(20));
            this.mYear.set((double) i);
            this.mMonth.set((double) i2);
            this.mMonth1.set((double) (i2 + 1));
            this.mDate.set((double) i3);
            this.mDayOfWeek.set((double) this.mCalendar.get(14));
            this.mSecond.set((double) this.mCalendar.get(21));
            this.mYearLunar.set((double) this.mCalendar.get(2));
            this.mMonthLunar.set((double) this.mCalendar.get(6));
            this.mDateLunar.set((double) this.mCalendar.get(10));
            this.mYearLunar1864.set((double) this.mCalendar.get(4));
            this.mMonthLunarLeap.set((double) this.mCalendar.get(8));
            this.mLastUpdatedTime = j;
        }
    }

    public void finish() {
        synchronized (this.mLock) {
            super.finish();
            this.mFinished = true;
            this.mLastUpdatedTime = 0;
            sCalendar = null;
            getContext().getHandler().removeCallbacks(this.mTimeUpdater);
        }
    }

    public void init() {
        super.init();
        refreshAlarm();
        updateTime();
        checkUpdateTime();
    }

    public void onNotify(Context context, Intent intent, Object obj) {
        resetCalendar();
        checkUpdateTime();
    }

    public void pause() {
        super.pause();
        getContext().getHandler().removeCallbacks(this.mTimeUpdater);
    }

    /* access modifiers changed from: protected */
    public void resetCalendar() {
        this.mCalendar = new Calendar();
        if (sCalendar != null) {
            sCalendar = new Calendar();
        }
    }

    public void resume() {
        super.resume();
        refreshAlarm();
        resetCalendar();
        checkUpdateTime();
    }

    public void tick(long j) {
        super.tick(j);
        this.mTime.set((double) j);
        updateTime();
    }
}
