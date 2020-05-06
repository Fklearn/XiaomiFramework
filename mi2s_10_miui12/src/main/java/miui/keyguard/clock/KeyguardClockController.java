package miui.keyguard.clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.miui.system.internal.R;
import java.util.TimeZone;
import miui.os.Build;

public class KeyguardClockController {
    private static final String AUTO_DUAL_CLOCK = "auto_dual_clock";
    public static final int HIDE_LUNAR_CALENDAR = 0;
    public static final int KEYGUARD_CLOCK_CENTER_HORIZONTAL = 1;
    public static final int KEYGUARD_CLOCK_CENTER_VERTICAL = 3;
    public static final int KEYGUARD_CLOCK_DEFAULT = 0;
    public static final int KEYGUARD_CLOCK_DUAL = 101;
    public static final int KEYGUARD_CLOCK_LEFT_TOP = 2;
    public static final int KEYGUARD_CLOCK_LEFT_TOP_LARGE = 4;
    private static final String KEY_SETTINGS_SYSTEM_SHOW_LUNAR_CALENDAR = "show_lunar_calendar";
    private static final String RESIDENT_TIMEZONE = "resident_timezone";
    public static final String SELECTED_KEYGUARD_CLOCK_POSITION = "selected_keyguard_clock_position";
    public static final int SHOW_LUNAR_CALENDAR = 1;
    public static final int UNDEFINED = -1;
    private boolean mAutoDualClock;
    private boolean mAutoUpdateTime;
    private int mClockStyle;
    /* access modifiers changed from: private */
    public IClockView mClockView;
    private ViewGroup mContainer;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public String mCurrentTimezone = TimeZone.getDefault().getID();
    /* access modifiers changed from: private */
    public boolean mDualClockOpen;
    ContentObserver mDualClockOpenObserver;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private boolean mHasTopMargin;
    private int mLastClockPosition;
    private String mOwnerString;
    /* access modifiers changed from: private */
    public String mResidentTimezone;
    ContentObserver mResidentTimezoneObserver;
    private float mScaleRatio;
    private int mSelectedClockPosition;
    private boolean mShowDualClock;
    private int mShowLunarCalendar;
    private boolean mTextDark;
    private final BroadcastReceiver mTimezoneChangeReceiver;
    private final BroadcastReceiver mUpdateTimeReceiver;
    /* access modifiers changed from: private */
    public Runnable mUpdateTimeRunnable;

    public interface IClockView {
        int getClockHeight();

        float getClockVisibleHeight();

        float getTopMargin();

        void setClockAlpha(float f);

        void setOwnerInfo(String str);

        void setScaleRatio(float f);

        void setShowLunarCalendar(boolean z);

        void setTextColorDark(boolean z);

        void updateResidentTimeZone(String str);

        void updateTime();

        void updateTimeZone(String str);

        void updateViewTopMargin(boolean z);
    }

    public KeyguardClockController(Context context, ViewGroup container) {
        String str;
        boolean z = true;
        this.mAutoDualClock = true;
        this.mDualClockOpen = false;
        this.mShowDualClock = false;
        this.mHasTopMargin = true;
        this.mOwnerString = null;
        this.mClockStyle = 0;
        this.mSelectedClockPosition = 0;
        this.mLastClockPosition = 0;
        this.mShowLunarCalendar = -1;
        this.mScaleRatio = 1.0f;
        this.mTextDark = false;
        this.mHandler = new Handler();
        this.mAutoUpdateTime = true;
        this.mUpdateTimeRunnable = new Runnable() {
            public void run() {
                if (KeyguardClockController.this.mClockView != null) {
                    KeyguardClockController.this.mClockView.updateTime();
                }
            }
        };
        this.mTimezoneChangeReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                KeyguardClockController.this.mHandler.post(new Runnable() {
                    public void run() {
                        String unused = KeyguardClockController.this.mCurrentTimezone = TimeZone.getDefault().getID();
                        KeyguardClockController.this.updateDualClock();
                    }
                });
            }
        };
        this.mUpdateTimeReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                KeyguardClockController.this.mHandler.post(KeyguardClockController.this.mUpdateTimeRunnable);
            }
        };
        this.mDualClockOpenObserver = new ContentObserver(new Handler()) {
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                KeyguardClockController keyguardClockController = KeyguardClockController.this;
                boolean z = false;
                if (Settings.System.getInt(keyguardClockController.mContext.getContentResolver(), KeyguardClockController.AUTO_DUAL_CLOCK, 0) != 0) {
                    z = true;
                }
                boolean unused = keyguardClockController.mDualClockOpen = z;
                KeyguardClockController.this.updateDualClock();
            }
        };
        this.mResidentTimezoneObserver = new ContentObserver(new Handler()) {
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                KeyguardClockController keyguardClockController = KeyguardClockController.this;
                String unused = keyguardClockController.mResidentTimezone = Settings.System.getString(keyguardClockController.mContext.getContentResolver(), KeyguardClockController.RESIDENT_TIMEZONE);
                KeyguardClockController.this.updateDualClock();
            }
        };
        this.mContext = context;
        this.mContainer = container;
        this.mSelectedClockPosition = Settings.System.getInt(context.getContentResolver(), SELECTED_KEYGUARD_CLOCK_POSITION, 0);
        this.mDualClockOpen = Settings.System.getInt(context.getContentResolver(), AUTO_DUAL_CLOCK, 0) != 0;
        this.mResidentTimezone = Settings.System.getString(context.getContentResolver(), RESIDENT_TIMEZONE);
        this.mShowDualClock = (!this.mDualClockOpen || (str = this.mResidentTimezone) == null || str.equals(this.mCurrentTimezone)) ? false : z;
        addClockView();
        updateKeyguardClock();
    }

    private int getClockPosition() {
        if (this.mShowDualClock) {
            return 101;
        }
        int i = this.mClockStyle;
        if (i != 0) {
            return i;
        }
        int i2 = this.mSelectedClockPosition;
        if (i2 != 0) {
            return i2;
        }
        return getDefaultClockPosition();
    }

    public int getDefaultClockPosition() {
        if ("davinci".equals(Build.DEVICE) || "davinciin".equals(Build.DEVICE) || "raphael".equals(Build.DEVICE) || "raphaelin".equals(Build.DEVICE) || "chiron".equals(Build.DEVICE) || "polaris".equals(Build.DEVICE)) {
            return 3;
        }
        return 0;
    }

    public void setClockStyle(int clockStyle) {
        if (this.mClockStyle != clockStyle) {
            this.mClockStyle = clockStyle;
            updateKeyguardClock();
        }
    }

    public void setShowLunarCalendar(int showLunarCalendar) {
        if (this.mShowLunarCalendar != showLunarCalendar) {
            this.mShowLunarCalendar = showLunarCalendar;
            IClockView iClockView = this.mClockView;
            if (iClockView != null) {
                iClockView.setShowLunarCalendar(getShowLunarCalendar());
            }
        }
    }

    private boolean getShowLunarCalendar() {
        int i = this.mShowLunarCalendar;
        if (i != -1) {
            return i == 1;
        }
        if (Settings.System.getInt(this.mContext.getContentResolver(), KEY_SETTINGS_SYSTEM_SHOW_LUNAR_CALENDAR, 0) == 1) {
            return true;
        }
        return false;
    }

    public void setScaleRatio(float ratio) {
        if (this.mScaleRatio != ratio) {
            this.mScaleRatio = ratio;
            IClockView iClockView = this.mClockView;
            if (iClockView != null) {
                iClockView.setScaleRatio(ratio);
            }
        }
    }

    public void setTextColorDark(boolean textDark) {
        if (this.mTextDark != textDark) {
            this.mTextDark = textDark;
            IClockView iClockView = this.mClockView;
            if (iClockView != null) {
                iClockView.setTextColorDark(textDark);
            }
        }
    }

    public void setHasTopMargin(boolean hasTopMargin) {
        if (this.mHasTopMargin != hasTopMargin) {
            this.mHasTopMargin = hasTopMargin;
            IClockView iClockView = this.mClockView;
            if (iClockView != null) {
                iClockView.updateViewTopMargin(hasTopMargin);
            }
        }
    }

    public void setOwnerInfo(String ownerInfo) {
        if (this.mOwnerString != ownerInfo) {
            this.mOwnerString = ownerInfo;
            IClockView iClockView = this.mClockView;
            if (iClockView != null) {
                iClockView.setOwnerInfo(ownerInfo);
            }
        }
    }

    public void setAutoUpdateTime(boolean autoUpdateTime) {
        this.mAutoUpdateTime = autoUpdateTime;
    }

    public void updateKeyguardClock() {
        if (getClockPosition() != this.mLastClockPosition) {
            this.mLastClockPosition = getClockPosition();
            this.mContainer.removeAllViews();
            addClockView();
        }
    }

    public void updateTime() {
        IClockView iClockView = this.mClockView;
        if (iClockView != null) {
            iClockView.updateTime();
        }
    }

    public void onAddToWindow() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.TIMEZONE_CHANGED");
        this.mContext.registerReceiver(this.mTimezoneChangeReceiver, filter);
        if (this.mAutoUpdateTime) {
            IntentFilter updateTimeFilter = new IntentFilter();
            updateTimeFilter.addAction("android.intent.action.TIME_TICK");
            updateTimeFilter.addAction("android.intent.action.TIME_SET");
            this.mContext.registerReceiver(this.mUpdateTimeReceiver, updateTimeFilter);
        }
        registerDualClockObserver();
    }

    public void onRemoveFromWindow() {
        this.mContext.unregisterReceiver(this.mTimezoneChangeReceiver);
        if (this.mAutoUpdateTime) {
            this.mContext.unregisterReceiver(this.mUpdateTimeReceiver);
        }
        unregisterDualClockObserver();
    }

    private void registerDualClockObserver() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(AUTO_DUAL_CLOCK), false, this.mDualClockOpenObserver);
        this.mDualClockOpenObserver.onChange(false);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(RESIDENT_TIMEZONE), false, this.mResidentTimezoneObserver);
        this.mResidentTimezoneObserver.onChange(false);
    }

    private void unregisterDualClockObserver() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mDualClockOpenObserver);
        this.mContext.getContentResolver().unregisterContentObserver(this.mResidentTimezoneObserver);
    }

    public void setAutoDualClock(boolean autoDualClock) {
        this.mAutoDualClock = autoDualClock;
        updateDualClock();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0008, code lost:
        r0 = r2.mResidentTimezone;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateDualClock() {
        /*
            r2 = this;
            boolean r0 = r2.mAutoDualClock
            if (r0 == 0) goto L_0x0016
            boolean r0 = r2.mDualClockOpen
            if (r0 == 0) goto L_0x0016
            java.lang.String r0 = r2.mResidentTimezone
            if (r0 == 0) goto L_0x0016
            java.lang.String r1 = r2.mCurrentTimezone
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L_0x0016
            r0 = 1
            goto L_0x0017
        L_0x0016:
            r0 = 0
        L_0x0017:
            r2.mShowDualClock = r0
            r2.updateKeyguardClock()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.keyguard.clock.KeyguardClockController.updateDualClock():void");
    }

    private void addClockView() {
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        View view = null;
        int clockPosition = getClockPosition();
        if (clockPosition == 0 || clockPosition == 1) {
            view = inflater.inflate(R.layout.miui_center_horizontal_clock, this.mContainer, false);
        } else if (clockPosition == 2) {
            view = inflater.inflate(R.layout.miui_left_top_clock, this.mContainer, false);
        } else if (clockPosition == 3) {
            view = inflater.inflate(R.layout.miui_vertical_clock, this.mContainer, false);
        } else if (clockPosition == 4) {
            view = inflater.inflate(R.layout.miui_left_top_large_clock, this.mContainer, false);
        } else if (clockPosition == 101) {
            view = inflater.inflate(R.layout.miui_dual_clock, this.mContainer, false);
        }
        this.mContainer.addView(view);
        this.mClockView = (IClockView) view;
        IClockView iClockView = this.mClockView;
        if (iClockView != null) {
            iClockView.updateResidentTimeZone(this.mResidentTimezone);
            this.mClockView.updateTimeZone(this.mCurrentTimezone);
            this.mClockView.setShowLunarCalendar(getShowLunarCalendar());
            this.mClockView.setScaleRatio(this.mScaleRatio);
            this.mClockView.setTextColorDark(this.mTextDark);
            this.mClockView.updateViewTopMargin(this.mHasTopMargin);
            this.mClockView.setOwnerInfo(this.mOwnerString);
        }
    }

    public int getClockHeight() {
        return this.mClockView.getClockHeight();
    }

    public float getClockVisibleHeight() {
        return this.mClockView.getClockVisibleHeight();
    }

    public float getTopMargin() {
        return this.mClockView.getTopMargin();
    }

    public void setClockAlpha(float alpha) {
        this.mClockView.setClockAlpha(alpha);
    }
}
