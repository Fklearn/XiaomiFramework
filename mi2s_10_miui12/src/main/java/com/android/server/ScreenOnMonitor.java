package com.android.server;

import android.app.ActivityThread;
import android.os.AnrMonitor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.os.SomeArgs;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import miui.os.Build;

public class ScreenOnMonitor {
    private static final long AVG_MAX_COUNT = 50;
    public static final int BLOCK_SCREEN_ON_BEGIN = 2;
    public static final int BLOCK_SCREEN_ON_END = 3;
    public static final Date DATE = new Date();
    public static final int FINGERPRINT_AUTHENTICATED = 5;
    public static final int FINGERPRINT_DOWN = 4;
    private static final long INTERVAL_REPORT_TIME = 14400000;
    public static final int KEYGUARD_DRAWN = 6;
    public static final int KEYGUARD_EXIT_ANIM = 8;
    public static final int KEYGUARD_GOING_AWAY = 7;
    private static final int MSG_RECORD_TIME = 3;
    private static final int MSG_REPORT = 5;
    private static final int MSG_SCREEN_ON_TIMEOUT = 4;
    private static final int MSG_START_MONITOR = 1;
    private static final int MSG_STOP_MONITOR = 2;
    private static String PROPERTY_SCREEN_ON_UPLOAD = "persist.sys.screenon";
    private static final long REPORT_DELAY = 2000;
    private static final long SCREEN_ON_TIMEOUT = 1000;
    public static final int SET_DISPLAY_STATE_BEGIN = 0;
    public static final int SET_DISPLAY_STATE_END = 1;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final List<String> STABLE_SUPPORT_DEVICE = new ArrayList<String>() {
        {
            add("wayne");
            add("dipper");
            add("sirius");
        }
    };
    private static final String TAG = "ScreenOnMonitor";
    private static final int TYPE_WAKE_SOURCE_DEFAULT = -1;
    private static final int TYPE_WAKE_SOURCE_DP_CENTER = 2;
    private static final int TYPE_WAKE_SOURCE_KEYGUARD_FINGER_PASS = 4;
    private static final int TYPE_WAKE_SOURCE_KEYGUARD_NOTIFICATION = 3;
    private static final int TYPE_WAKE_SOURCE_LID = 5;
    private static final int TYPE_WAKE_SOURCE_POWER = 1;
    private static final int TYPE_WAKE_SOURCE_TOTAL = 0;
    private static volatile ScreenOnMonitor sInstance;
    private int[] mAvgCount = {0, 0, 0, 0, 0, 0};
    private long mBlockScreenOnBegin;
    private long mBlockScreenOnEnd;
    private int mDisplayBrightness = -1;
    private int mDisplayState = 0;
    private long mFingerDown;
    private long mFingerSuccess;
    private ScreenOnMonitorHandler mHandler;
    private long mKeyExitAnim;
    private long mKeyGoingAway;
    private long mKeyguardDrawn;
    private long mLastReportTime;
    private boolean[] mNeedRecord = {true, true, true, true, true, true};
    private long mSetDisplayStateBegin;
    private long mSetDisplayStateEnd;
    private long mStartTime;
    private long mStopTime = -1;
    private long mTimeStamp;
    private String mTimeoutSummary;
    private long[] mTotalBlockScreenOnTime = {0, 0, 0, 0, 0, 0};
    private long mTotalFingerAllTime = 0;
    private int mTotalFingerAvgCount = 0;
    private long mTotalFingerAway2Exit = 0;
    private long mTotalFingerAway2On = 0;
    private long mTotalFingerBlock2KeyDrawn = 0;
    private long mTotalFingerBlockScreenOnTime = 0;
    private long mTotalFingerDisplayOnTime = 0;
    private long mTotalFingerDown2SuccessTime = 0;
    private long mTotalFingerExit2Draw = 0;
    private long mTotalFingerSuccess2WakeTime = 0;
    private long mTotalFingerWake2Away = 0;
    private long mTotalFingerWake2BlockTime = 0;
    private long[] mTotalScreenOnTime = {0, 0, 0, 0, 0, 0};
    private long[] mTotalSetDisplayTime = {0, 0, 0, 0, 0, 0};
    private StringBuilder mTypeNeedRecordSb;
    private String mUploadVersion;
    private PowerManager.WakeLock mWakeLock;
    private String mWakeSource;

    public static ScreenOnMonitor getInstance() {
        if (sInstance == null) {
            synchronized (ScreenOnMonitor.class) {
                if (sInstance == null) {
                    sInstance = new ScreenOnMonitor();
                }
            }
        }
        return sInstance;
    }

    private ScreenOnMonitor() {
        if (!isDisableUpload()) {
            String propScreenOnUpload = SystemProperties.get(PROPERTY_SCREEN_ON_UPLOAD);
            if (!TextUtils.isEmpty(propScreenOnUpload)) {
                String[] props = propScreenOnUpload.split(",");
                if (props.length == 2) {
                    String needRecord = props[0];
                    if (!TextUtils.isEmpty(needRecord) && needRecord.length() == this.mNeedRecord.length) {
                        this.mTypeNeedRecordSb = new StringBuilder(needRecord);
                        int i = 0;
                        while (true) {
                            boolean[] zArr = this.mNeedRecord;
                            if (i >= zArr.length) {
                                break;
                            }
                            zArr[i] = needRecord.charAt(i) == '0';
                            i++;
                        }
                    } else {
                        this.mTypeNeedRecordSb = new StringBuilder("000000");
                    }
                    this.mUploadVersion = props[1];
                }
            }
            if (TextUtils.isEmpty(this.mUploadVersion)) {
                this.mUploadVersion = "0.0.0";
                this.mTypeNeedRecordSb = new StringBuilder("000000");
            }
            this.mHandler = new ScreenOnMonitorHandler(AnrMonitor.getWorkHandler().getLooper());
            this.mWakeLock = ((PowerManager) ActivityThread.currentApplication().getSystemService("power")).newWakeLock(1, TAG);
        }
    }

    public void startMonitor(String wakeSource) {
        if (!isDisableUpload() && this.mDisplayState != 2 && this.mStartTime == 0) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = Long.valueOf(SystemClock.elapsedRealtime());
            args.arg2 = wakeSource;
            args.arg3 = Long.valueOf(System.currentTimeMillis());
            this.mHandler.obtainMessage(1, args).sendToTarget();
        }
    }

    public void stopMonitor(int brightness, int state) {
        if (!isDisableUpload()) {
            if (this.mDisplayState == 2 && state != 2) {
                this.mHandler.obtainMessage(2, false).sendToTarget();
            } else if (this.mDisplayBrightness == 0 && brightness != 0) {
                this.mStopTime = SystemClock.elapsedRealtime();
                this.mHandler.obtainMessage(2, true).sendToTarget();
            }
            this.mDisplayBrightness = brightness;
            this.mDisplayState = state;
        }
    }

    public void recordTime(int type) {
        if (!isDisableUpload()) {
            ScreenOnMonitorHandler screenOnMonitorHandler = this.mHandler;
            screenOnMonitorHandler.sendMessage(screenOnMonitorHandler.obtainMessage(3, type, -1, Long.valueOf(SystemClock.elapsedRealtime())));
        }
    }

    class ScreenOnMonitorHandler extends Handler {
        public ScreenOnMonitorHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                ScreenOnMonitor.this.handleStartMonitor((SomeArgs) msg.obj);
            } else if (i == 2) {
                ScreenOnMonitor.this.handleStopMonitor(((Boolean) msg.obj).booleanValue());
            } else if (i == 3) {
                ScreenOnMonitor.this.handleRecordTime(msg.arg1, ((Long) msg.obj).longValue());
            } else if (i == 4) {
                ScreenOnMonitor.this.handleScreenOnTimeout();
            } else if (i == 5) {
                ScreenOnMonitor.this.handleReport(false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleStartMonitor(SomeArgs args) {
        try {
            if (this.mStartTime == 0) {
                this.mStartTime = ((Long) args.arg1).longValue();
                this.mWakeSource = (String) args.arg2;
                this.mTimeStamp = ((Long) args.arg3).longValue();
                this.mStopTime = 0;
                this.mBlockScreenOnBegin = 0;
                this.mKeyguardDrawn = 0;
                this.mBlockScreenOnEnd = 0;
                this.mSetDisplayStateBegin = 0;
                this.mSetDisplayStateEnd = 0;
                if (!this.mWakeLock.isHeld()) {
                    this.mWakeLock.acquire();
                }
                this.mHandler.sendEmptyMessageDelayed(4, 1000);
                args.recycle();
            }
        } finally {
            args.recycle();
        }
    }

    /* access modifiers changed from: private */
    public void handleStopMonitor(boolean report) {
        if (this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
        }
        if (this.mStartTime != 0 && this.mBlockScreenOnBegin != 0 && this.mSetDisplayStateBegin != 0) {
            this.mHandler.removeMessages(4);
            this.mHandler.removeMessages(5);
            if (report) {
                handleReport(true);
            }
            this.mStartTime = 0;
            this.mBlockScreenOnBegin = 0;
            this.mSetDisplayStateBegin = 0;
        }
    }

    /* access modifiers changed from: private */
    public void handleRecordTime(int type, long time) {
        if (type == 4) {
            this.mFingerDown = time;
        } else if (type == 5) {
            this.mFingerSuccess = time;
        } else if (this.mStartTime != 0) {
            if (type != 0) {
                if (type != 1) {
                    if (type != 2) {
                        if (type != 3) {
                            if (type != 6) {
                                if (type != 7) {
                                    if (type == 8 && this.mKeyExitAnim == 0 && this.mKeyGoingAway > 0) {
                                        this.mKeyExitAnim = time;
                                    }
                                } else if (this.mKeyGoingAway == 0) {
                                    this.mKeyGoingAway = time;
                                }
                            } else if (this.mKeyguardDrawn == 0 && this.mBlockScreenOnBegin > 0) {
                                this.mKeyguardDrawn = time;
                            }
                        } else if (this.mBlockScreenOnEnd == 0 && this.mBlockScreenOnBegin > 0) {
                            this.mBlockScreenOnEnd = time;
                        }
                    } else if (this.mBlockScreenOnBegin == 0) {
                        this.mBlockScreenOnBegin = time;
                    }
                } else if (this.mSetDisplayStateEnd == 0 && this.mSetDisplayStateBegin > 0) {
                    this.mSetDisplayStateEnd = time;
                }
            } else if (this.mSetDisplayStateBegin == 0) {
                this.mSetDisplayStateBegin = time;
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleScreenOnTimeout() {
        this.mTimeoutSummary = getTimeoutSummary();
        Slog.e(TAG, this.mTimeoutSummary);
        this.mHandler.sendEmptyMessageDelayed(5, REPORT_DELAY);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x045e A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x045f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleReport(boolean r70) {
        /*
            r69 = this;
            r1 = r69
            r2 = r70
            long r3 = android.os.SystemClock.elapsedRealtime()
            long r5 = r1.mKeyguardDrawn
            r7 = 0
            int r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r0 <= 0) goto L_0x0011
            goto L_0x0012
        L_0x0011:
            r5 = r3
        L_0x0012:
            r1.mKeyguardDrawn = r5
            long r5 = r1.mKeyguardDrawn
            long r9 = r1.mBlockScreenOnBegin
            long r5 = r5 - r9
            java.lang.String r9 = r69.getScreenOnDetail()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r9)
            java.lang.String r10 = " block2keyDrawn="
            r0.append(r10)
            r0.append(r5)
            java.lang.String r10 = " hasOn:"
            r0.append(r10)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r10 = "ScreenOnMonitor"
            android.util.Slog.i(r10, r0)
            java.lang.String r0 = r1.mTimeoutSummary
            if (r0 == 0) goto L_0x007e
            long r11 = r1.mLastReportTime
            int r0 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r0 == 0) goto L_0x0051
            long r11 = r3 - r11
            r13 = 14400000(0xdbba00, double:7.1145453E-317)
            int r0 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x007b
        L_0x0051:
            r1.mLastReportTime = r3
            miui.mqsas.sdk.event.ScreenOnEvent r0 = new miui.mqsas.sdk.event.ScreenOnEvent
            r0.<init>()
            java.lang.String r11 = r1.mTimeoutSummary
            r0.setTimeoutSummary(r11)
            r0.setmTimeOutDetail(r9)
            java.lang.String r11 = r1.mWakeSource
            r0.setWakeSource(r11)
            long r11 = r1.mTimeStamp
            java.lang.String r11 = r1.toCalendarTime(r11)
            r0.setTimeStamp(r11)
            java.lang.String r11 = "lt_screen_on"
            r0.setScreenOnType(r11)
            miui.mqsas.sdk.MQSEventManagerDelegate r11 = miui.mqsas.sdk.MQSEventManagerDelegate.getInstance()
            r11.reportScreenOnEvent(r0)
        L_0x007b:
            r0 = 0
            r1.mTimeoutSummary = r0
        L_0x007e:
            if (r2 == 0) goto L_0x053a
            long r11 = r1.mStopTime
            long r13 = r1.mStartTime
            long r11 = r11 - r13
            r13 = 1000(0x3e8, double:4.94E-321)
            int r0 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r0 >= 0) goto L_0x053a
            java.lang.String r0 = android.os.Build.VERSION.INCREMENTAL
            java.lang.String r11 = r1.mUploadVersion
            boolean r0 = r0.equals(r11)
            if (r0 != 0) goto L_0x0532
            long r11 = r1.mStopTime
            long r13 = r1.mStartTime
            long r11 = r11 - r13
            long r13 = r1.mSetDisplayStateEnd
            long r7 = r1.mSetDisplayStateBegin
            long r13 = r13 - r7
            long r7 = r1.mBlockScreenOnEnd
            r19 = r3
            long r2 = r1.mBlockScreenOnBegin
            long r7 = r7 - r2
            boolean[] r0 = r1.mNeedRecord
            r2 = 0
            boolean r0 = r0[r2]
            if (r0 == 0) goto L_0x00ca
            int[] r0 = r1.mAvgCount
            r3 = r0[r2]
            int r3 = r3 + 1
            r0[r2] = r3
            long[] r0 = r1.mTotalScreenOnTime
            r3 = r0[r2]
            long r3 = r3 + r11
            r0[r2] = r3
            long[] r0 = r1.mTotalSetDisplayTime
            r3 = r0[r2]
            long r3 = r3 + r13
            r0[r2] = r3
            long[] r0 = r1.mTotalBlockScreenOnTime
            r3 = r0[r2]
            long r3 = r3 + r7
            r0[r2] = r3
        L_0x00ca:
            int[] r0 = r1.mAvgCount
            r0 = r0[r2]
            long r3 = (long) r0
            r21 = 50
            int r0 = (r3 > r21 ? 1 : (r3 == r21 ? 0 : -1))
            java.lang.String r4 = ","
            if (r0 != 0) goto L_0x0155
            miui.mqsas.sdk.event.ScreenOnEvent r0 = new miui.mqsas.sdk.event.ScreenOnEvent
            r0.<init>()
            long[] r15 = r1.mTotalScreenOnTime
            r15 = r15[r2]
            int[] r3 = r1.mAvgCount
            r3 = r3[r2]
            long r2 = (long) r3
            long r2 = r15 / r2
            r0.setTotalTime(r2)
            long[] r2 = r1.mTotalSetDisplayTime
            r3 = 0
            r15 = r2[r3]
            int[] r2 = r1.mAvgCount
            r2 = r2[r3]
            r25 = r4
            long r3 = (long) r2
            long r2 = r15 / r3
            r0.setSetDisplayTime(r2)
            long[] r2 = r1.mTotalBlockScreenOnTime
            r3 = 0
            r15 = r2[r3]
            int[] r2 = r1.mAvgCount
            r2 = r2[r3]
            long r2 = (long) r2
            long r2 = r15 / r2
            r0.setBlockScreenTime(r2)
            java.lang.String r2 = "avg_screen_on"
            r0.setScreenOnType(r2)
            miui.mqsas.sdk.MQSEventManagerDelegate r2 = miui.mqsas.sdk.MQSEventManagerDelegate.getInstance()
            r2.reportScreenOnEvent(r0)
            long[] r2 = r1.mTotalScreenOnTime
            r3 = 0
            r15 = 0
            r2[r3] = r15
            long[] r2 = r1.mTotalSetDisplayTime
            r2[r3] = r15
            long[] r2 = r1.mTotalBlockScreenOnTime
            r2[r3] = r15
            int[] r2 = r1.mAvgCount
            r2[r3] = r3
            boolean[] r2 = r1.mNeedRecord
            r2[r3] = r3
            java.lang.StringBuilder r2 = r1.mTypeNeedRecordSb
            r4 = 49
            r2.setCharAt(r3, r4)
            java.lang.String r2 = PROPERTY_SCREEN_ON_UPLOAD
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.StringBuilder r4 = r1.mTypeNeedRecordSb
            java.lang.String r4 = r4.toString()
            r3.append(r4)
            r4 = r25
            r3.append(r4)
            java.lang.String r15 = r1.mUploadVersion
            r3.append(r15)
            java.lang.String r3 = r3.toString()
            android.os.SystemProperties.set(r2, r3)
        L_0x0155:
            java.lang.String r0 = r1.mWakeSource
            int r2 = r1.getWakeupSrcIndex(r0)
            r0 = 4
            if (r2 != r0) goto L_0x043e
            r15 = r11
            long r11 = r1.mFingerSuccess
            r17 = 0
            int r0 = (r11 > r17 ? 1 : (r11 == r17 ? 0 : -1))
            if (r0 == 0) goto L_0x042e
            r25 = r11
            long r11 = r1.mFingerDown
            int r0 = (r11 > r17 ? 1 : (r11 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x0170
            goto L_0x0172
        L_0x0170:
            r11 = r25
        L_0x0172:
            r1.mFingerDown = r11
            long r11 = r1.mKeyGoingAway
            int r0 = (r11 > r17 ? 1 : (r11 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x017b
            goto L_0x017d
        L_0x017b:
            r11 = r19
        L_0x017d:
            r1.mKeyGoingAway = r11
            long r11 = r1.mKeyExitAnim
            int r0 = (r11 > r17 ? 1 : (r11 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x0186
            goto L_0x0188
        L_0x0186:
            r11 = r19
        L_0x0188:
            r1.mKeyExitAnim = r11
            long r11 = r1.mFingerSuccess
            r25 = r2
            long r2 = r1.mFingerDown
            r26 = r5
            long r5 = r11 - r2
            r28 = r9
            r0 = r10
            long r9 = r1.mStartTime
            long r11 = r9 - r11
            r29 = r13
            long r13 = r1.mBlockScreenOnBegin
            long r13 = r13 - r9
            r31 = r7
            long r7 = r1.mStopTime
            long r7 = r7 - r2
            long r2 = r1.mKeyGoingAway
            r33 = r7
            long r7 = r2 - r9
            r35 = r7
            long r7 = r1.mKeyExitAnim
            r37 = r13
            long r13 = r7 - r2
            r39 = r13
            long r13 = r1.mKeyguardDrawn
            r41 = r11
            long r11 = r13 - r7
            r43 = r11
            long r11 = r1.mBlockScreenOnEnd
            long r13 = r11 - r13
            r45 = r13
            long r13 = r1.mSetDisplayStateBegin
            long r13 = r13 - r9
            long r9 = r1.mSetDisplayStateEnd
            r47 = r13
            long r13 = r7 - r9
            long r11 = r11 - r7
            long r7 = r9 - r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "fingerDown2suc2wake="
            r2.append(r3)
            r2.append(r5)
            r2.append(r4)
            r9 = r41
            r2.append(r9)
            java.lang.String r3 = "; wake2block2un="
            r2.append(r3)
            r9 = r37
            r2.append(r9)
            r2.append(r4)
            r9 = r31
            r2.append(r9)
            java.lang.String r3 = "; wake2away2exit2drawn2un="
            r2.append(r3)
            r9 = r35
            r2.append(r9)
            r2.append(r4)
            r9 = r39
            r2.append(r9)
            r2.append(r4)
            r9 = r43
            r2.append(r9)
            r2.append(r4)
            r9 = r45
            r2.append(r9)
            java.lang.String r3 = "; wake2disp2on2exit2un="
            r2.append(r3)
            r9 = r47
            r2.append(r9)
            r2.append(r4)
            r9 = r29
            r2.append(r9)
            r2.append(r4)
            r2.append(r13)
            r2.append(r4)
            r2.append(r11)
            java.lang.String r3 = "; away2on:"
            r2.append(r3)
            r2.append(r7)
            java.lang.String r3 = " all="
            r2.append(r3)
            r29 = r11
            r11 = r33
            r2.append(r11)
            java.lang.String r2 = r2.toString()
            android.util.Slog.d(r0, r2)
            r17 = 0
            int r0 = (r5 > r17 ? 1 : (r5 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x025a
            r23 = r5
            goto L_0x025c
        L_0x025a:
            r23 = 1000(0x3e8, double:4.94E-321)
        L_0x025c:
            r2 = r23
            int r0 = (r7 > r17 ? 1 : (r7 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x0264
            r5 = r7
            goto L_0x0266
        L_0x0264:
            r5 = r17
        L_0x0266:
            int r0 = (r41 > r17 ? 1 : (r41 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x02ba
            int r0 = (r31 > r17 ? 1 : (r31 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x02ba
            int r0 = (r35 > r17 ? 1 : (r35 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x02ba
            int r0 = (r39 > r17 ? 1 : (r39 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x02ba
            int r0 = r1.mTotalFingerAvgCount
            int r0 = r0 + 1
            r1.mTotalFingerAvgCount = r0
            long r7 = r1.mTotalFingerDown2SuccessTime
            long r7 = r7 + r2
            r1.mTotalFingerDown2SuccessTime = r7
            long r7 = r1.mTotalFingerSuccess2WakeTime
            long r7 = r7 + r41
            r1.mTotalFingerSuccess2WakeTime = r7
            long r7 = r1.mTotalFingerWake2BlockTime
            long r7 = r7 + r37
            r1.mTotalFingerWake2BlockTime = r7
            long r7 = r1.mTotalFingerBlockScreenOnTime
            long r7 = r7 + r31
            r1.mTotalFingerBlockScreenOnTime = r7
            long r7 = r1.mTotalFingerDisplayOnTime
            long r7 = r7 + r9
            r1.mTotalFingerDisplayOnTime = r7
            long r7 = r1.mTotalFingerAllTime
            long r7 = r7 + r11
            r1.mTotalFingerAllTime = r7
            long r7 = r1.mTotalFingerBlock2KeyDrawn
            long r7 = r7 + r26
            r1.mTotalFingerBlock2KeyDrawn = r7
            long r7 = r1.mTotalFingerWake2Away
            long r7 = r7 + r35
            r1.mTotalFingerWake2Away = r7
            long r7 = r1.mTotalFingerAway2Exit
            long r7 = r7 + r39
            r1.mTotalFingerAway2Exit = r7
            long r7 = r1.mTotalFingerExit2Draw
            long r7 = r7 + r43
            r1.mTotalFingerExit2Draw = r7
            long r7 = r1.mTotalFingerAway2On
            long r7 = r7 + r5
            r1.mTotalFingerAway2On = r7
        L_0x02ba:
            int r0 = r1.mTotalFingerAvgCount
            long r7 = (long) r0
            int r0 = (r7 > r21 ? 1 : (r7 == r21 ? 0 : -1))
            if (r0 != 0) goto L_0x041e
            org.json.JSONObject r0 = new org.json.JSONObject
            r0.<init>()
            r7 = r0
            r23 = r2
            long r2 = r1.mTotalFingerDown2SuccessTime     // Catch:{ JSONException -> 0x040c }
            int r0 = r1.mTotalFingerAvgCount     // Catch:{ JSONException -> 0x040c }
            r33 = r5
            long r5 = (long) r0
            long r2 = r2 / r5
            long r5 = r1.mTotalFingerSuccess2WakeTime     // Catch:{ JSONException -> 0x03ff }
            int r0 = r1.mTotalFingerAvgCount     // Catch:{ JSONException -> 0x03ff }
            r49 = r11
            long r11 = (long) r0
            long r5 = r5 / r11
            long r11 = r1.mTotalFingerWake2BlockTime     // Catch:{ JSONException -> 0x03f4 }
            int r0 = r1.mTotalFingerAvgCount     // Catch:{ JSONException -> 0x03f4 }
            r51 = r13
            long r13 = (long) r0
            long r11 = r11 / r13
            long r13 = r1.mTotalFingerBlockScreenOnTime     // Catch:{ JSONException -> 0x03eb }
            int r0 = r1.mTotalFingerAvgCount     // Catch:{ JSONException -> 0x03eb }
            r53 = r9
            long r8 = (long) r0
            long r13 = r13 / r8
            r8 = r13
            long r13 = r1.mTotalFingerDisplayOnTime     // Catch:{ JSONException -> 0x03e4 }
            int r0 = r1.mTotalFingerAvgCount     // Catch:{ JSONException -> 0x03e4 }
            r55 = r8
            long r8 = (long) r0     // Catch:{ JSONException -> 0x03e4 }
            long r13 = r13 / r8
            r8 = r13
            long r13 = r1.mTotalFingerAllTime     // Catch:{ JSONException -> 0x03e4 }
            int r0 = r1.mTotalFingerAvgCount     // Catch:{ JSONException -> 0x03e4 }
            r57 = r8
            long r8 = (long) r0     // Catch:{ JSONException -> 0x03e4 }
            long r13 = r13 / r8
            r8 = r13
            long r13 = r1.mTotalFingerBlock2KeyDrawn     // Catch:{ JSONException -> 0x03e4 }
            int r0 = r1.mTotalFingerAvgCount     // Catch:{ JSONException -> 0x03e4 }
            r59 = r8
            long r8 = (long) r0     // Catch:{ JSONException -> 0x03e4 }
            long r13 = r13 / r8
            r8 = r13
            long r13 = r1.mTotalFingerWake2Away     // Catch:{ JSONException -> 0x03e4 }
            int r0 = r1.mTotalFingerAvgCount     // Catch:{ JSONException -> 0x03e4 }
            r61 = r8
            long r8 = (long) r0     // Catch:{ JSONException -> 0x03e4 }
            long r13 = r13 / r8
            r8 = r13
            long r13 = r1.mTotalFingerAway2Exit     // Catch:{ JSONException -> 0x03e4 }
            int r0 = r1.mTotalFingerAvgCount     // Catch:{ JSONException -> 0x03e4 }
            r63 = r11
            long r10 = (long) r0     // Catch:{ JSONException -> 0x03e4 }
            long r13 = r13 / r10
            r10 = r13
            long r12 = r1.mTotalFingerExit2Draw     // Catch:{ JSONException -> 0x03e4 }
            int r0 = r1.mTotalFingerAvgCount     // Catch:{ JSONException -> 0x03e4 }
            r65 = r15
            long r14 = (long) r0
            long r12 = r12 / r14
            long r14 = r1.mTotalFingerAway2On     // Catch:{ JSONException -> 0x03df }
            int r0 = r1.mTotalFingerAvgCount     // Catch:{ JSONException -> 0x03df }
            long r0 = (long) r0
            long r14 = r14 / r0
            r0 = r14
            java.lang.String r14 = "authenticatedTime"
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x03d9 }
            r15.<init>()     // Catch:{ JSONException -> 0x03d9 }
            r16 = r4
            java.lang.String r4 = "##w2a:"
            r15.append(r4)     // Catch:{ JSONException -> 0x03d7 }
            r15.append(r8)     // Catch:{ JSONException -> 0x03d7 }
            java.lang.String r4 = "##a2e:"
            r15.append(r4)     // Catch:{ JSONException -> 0x03d7 }
            r15.append(r10)     // Catch:{ JSONException -> 0x03d7 }
            java.lang.String r4 = "##e2d"
            r15.append(r4)     // Catch:{ JSONException -> 0x03d7 }
            r15.append(r12)     // Catch:{ JSONException -> 0x03d7 }
            java.lang.String r4 = r15.toString()     // Catch:{ JSONException -> 0x03d7 }
            r7.put(r14, r4)     // Catch:{ JSONException -> 0x03d7 }
            java.lang.String r4 = "wakeupTime"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x03d7 }
            r14.<init>()     // Catch:{ JSONException -> 0x03d7 }
            java.lang.String r15 = "authen:"
            r14.append(r15)     // Catch:{ JSONException -> 0x03d7 }
            r14.append(r2)     // Catch:{ JSONException -> 0x03d7 }
            java.lang.String r15 = "##suc2wake:"
            r14.append(r15)     // Catch:{ JSONException -> 0x03d7 }
            r14.append(r5)     // Catch:{ JSONException -> 0x03d7 }
            java.lang.String r15 = "##w2b:"
            r14.append(r15)     // Catch:{ JSONException -> 0x03d7 }
            r67 = r2
            r2 = r63
            r14.append(r2)     // Catch:{ JSONException -> 0x03d7 }
            java.lang.String r15 = "##blockScr:"
            r14.append(r15)     // Catch:{ JSONException -> 0x03d7 }
            r63 = r2
            r2 = r55
            r14.append(r2)     // Catch:{ JSONException -> 0x03d7 }
            java.lang.String r14 = r14.toString()     // Catch:{ JSONException -> 0x03d7 }
            r7.put(r4, r14)     // Catch:{ JSONException -> 0x03d7 }
            java.lang.String r4 = "ext"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x03d7 }
            r14.<init>()     // Catch:{ JSONException -> 0x03d7 }
            java.lang.String r15 = "all:"
            r14.append(r15)     // Catch:{ JSONException -> 0x03d7 }
            r55 = r2
            r2 = r59
            r14.append(r2)     // Catch:{ JSONException -> 0x03d7 }
            java.lang.String r15 = "##setDisp:"
            r14.append(r15)     // Catch:{ JSONException -> 0x03d7 }
            r59 = r2
            r2 = r57
            r14.append(r2)     // Catch:{ JSONException -> 0x03d7 }
            java.lang.String r15 = "##keyDrawn:"
            r14.append(r15)     // Catch:{ JSONException -> 0x03d7 }
            r57 = r2
            r2 = r61
            r14.append(r2)     // Catch:{ JSONException -> 0x03d7 }
            java.lang.String r15 = "##a2e"
            r14.append(r15)     // Catch:{ JSONException -> 0x03d7 }
            r14.append(r0)     // Catch:{ JSONException -> 0x03d7 }
            java.lang.String r14 = r14.toString()     // Catch:{ JSONException -> 0x03d7 }
            r7.put(r4, r14)     // Catch:{ JSONException -> 0x03d7 }
            miui.mqsas.sdk.MQSEventManagerDelegate r4 = miui.mqsas.sdk.MQSEventManagerDelegate.getInstance()     // Catch:{ JSONException -> 0x03d7 }
            java.lang.String r14 = "fingerprintScreenOn"
            java.lang.String r15 = r7.toString()     // Catch:{ JSONException -> 0x03d7 }
            r61 = r0
            r1 = 0
            r4.reportEvent(r14, r15, r1)     // Catch:{ JSONException -> 0x03d7 }
            r4 = r69
            r4.mTotalFingerAvgCount = r1     // Catch:{ JSONException -> 0x03d5 }
            goto L_0x044d
        L_0x03d5:
            r0 = move-exception
            goto L_0x041a
        L_0x03d7:
            r0 = move-exception
            goto L_0x03dc
        L_0x03d9:
            r0 = move-exception
            r16 = r4
        L_0x03dc:
            r4 = r69
            goto L_0x041a
        L_0x03df:
            r0 = move-exception
            r16 = r4
            r4 = r1
            goto L_0x041a
        L_0x03e4:
            r0 = move-exception
            r65 = r15
            r16 = r4
            r4 = r1
            goto L_0x041a
        L_0x03eb:
            r0 = move-exception
            r53 = r9
            r65 = r15
            r16 = r4
            r4 = r1
            goto L_0x041a
        L_0x03f4:
            r0 = move-exception
            r53 = r9
            r51 = r13
            r65 = r15
            r16 = r4
            r4 = r1
            goto L_0x041a
        L_0x03ff:
            r0 = move-exception
            r53 = r9
            r49 = r11
            r51 = r13
            r65 = r15
            r16 = r4
            r4 = r1
            goto L_0x041a
        L_0x040c:
            r0 = move-exception
            r33 = r5
            r53 = r9
            r49 = r11
            r51 = r13
            r65 = r15
            r16 = r4
            r4 = r1
        L_0x041a:
            r0.printStackTrace()
            goto L_0x044d
        L_0x041e:
            r23 = r2
            r33 = r5
            r53 = r9
            r49 = r11
            r51 = r13
            r65 = r15
            r16 = r4
            r4 = r1
            goto L_0x044d
        L_0x042e:
            r25 = r2
            r26 = r5
            r31 = r7
            r28 = r9
            r53 = r13
            r65 = r15
            r16 = r4
            r4 = r1
            goto L_0x044d
        L_0x043e:
            r25 = r2
            r16 = r4
            r26 = r5
            r31 = r7
            r28 = r9
            r65 = r11
            r53 = r13
            r4 = r1
        L_0x044d:
            r1 = 0
            r4.mFingerDown = r1
            r4.mFingerSuccess = r1
            r4.mKeyGoingAway = r1
            r4.mKeyExitAnim = r1
            r4.mKeyguardDrawn = r1
            r0 = -1
            r1 = r25
            if (r1 != r0) goto L_0x045f
            return
        L_0x045f:
            boolean[] r0 = r4.mNeedRecord
            boolean r0 = r0[r1]
            if (r0 == 0) goto L_0x0485
            int[] r0 = r4.mAvgCount
            r2 = r0[r1]
            int r2 = r2 + 1
            r0[r1] = r2
            long[] r0 = r4.mTotalScreenOnTime
            r2 = r0[r1]
            long r2 = r2 + r65
            r0[r1] = r2
            long[] r0 = r4.mTotalSetDisplayTime
            r2 = r0[r1]
            long r2 = r2 + r53
            r0[r1] = r2
            long[] r0 = r4.mTotalBlockScreenOnTime
            r2 = r0[r1]
            long r2 = r2 + r31
            r0[r1] = r2
        L_0x0485:
            int[] r0 = r4.mAvgCount
            r0 = r0[r1]
            long r2 = (long) r0
            int r0 = (r2 > r21 ? 1 : (r2 == r21 ? 0 : -1))
            if (r0 != 0) goto L_0x0541
            miui.mqsas.sdk.event.ScreenOnEvent r0 = new miui.mqsas.sdk.event.ScreenOnEvent
            r0.<init>()
            long[] r2 = r4.mTotalScreenOnTime
            r2 = r2[r1]
            int[] r5 = r4.mAvgCount
            r5 = r5[r1]
            long r5 = (long) r5
            long r2 = r2 / r5
            r0.setTotalTime(r2)
            long[] r2 = r4.mTotalSetDisplayTime
            r2 = r2[r1]
            int[] r5 = r4.mAvgCount
            r5 = r5[r1]
            long r5 = (long) r5
            long r2 = r2 / r5
            r0.setSetDisplayTime(r2)
            long[] r2 = r4.mTotalBlockScreenOnTime
            r2 = r2[r1]
            int[] r5 = r4.mAvgCount
            r5 = r5[r1]
            long r5 = (long) r5
            long r2 = r2 / r5
            r0.setBlockScreenTime(r2)
            java.lang.String[] r2 = miui.mqsas.sdk.event.ScreenOnEvent.TYPE_SCREEN_ON
            r2 = r2[r1]
            r0.setScreenOnType(r2)
            java.lang.String r2 = r4.mWakeSource
            r0.setWakeSource(r2)
            miui.mqsas.sdk.MQSEventManagerDelegate r2 = miui.mqsas.sdk.MQSEventManagerDelegate.getInstance()
            r2.reportScreenOnEvent(r0)
            long[] r2 = r4.mTotalScreenOnTime
            r5 = 0
            r2[r1] = r5
            long[] r2 = r4.mTotalSetDisplayTime
            r2[r1] = r5
            long[] r2 = r4.mTotalBlockScreenOnTime
            r2[r1] = r5
            int[] r2 = r4.mAvgCount
            r3 = 0
            r2[r1] = r3
            boolean[] r2 = r4.mNeedRecord
            r2[r1] = r3
            java.lang.StringBuilder r2 = r4.mTypeNeedRecordSb
            r3 = 49
            r2.setCharAt(r1, r3)
            java.lang.String r2 = PROPERTY_SCREEN_ON_UPLOAD
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.StringBuilder r5 = r4.mTypeNeedRecordSb
            java.lang.String r5 = r5.toString()
            r3.append(r5)
            r5 = r16
            r3.append(r5)
            java.lang.String r6 = r4.mUploadVersion
            r3.append(r6)
            java.lang.String r3 = r3.toString()
            android.os.SystemProperties.set(r2, r3)
            boolean r2 = r69.needRecordScreenOn()
            if (r2 != 0) goto L_0x0541
            java.lang.String r2 = PROPERTY_SCREEN_ON_UPLOAD
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.StringBuilder r6 = r4.mTypeNeedRecordSb
            java.lang.String r6 = r6.toString()
            r3.append(r6)
            r3.append(r5)
            java.lang.String r5 = android.os.Build.VERSION.INCREMENTAL
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            android.os.SystemProperties.set(r2, r3)
            goto L_0x0541
        L_0x0532:
            r19 = r3
            r26 = r5
            r28 = r9
            r4 = r1
            goto L_0x0541
        L_0x053a:
            r19 = r3
            r26 = r5
            r28 = r9
            r4 = r1
        L_0x0541:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ScreenOnMonitor.handleReport(boolean):void");
    }

    private boolean needRecordScreenOn() {
        for (boolean b : this.mNeedRecord) {
            if (b) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getWakeupSrcIndex(java.lang.String r8) {
        /*
            r7 = this;
            int r0 = r8.hashCode()
            r1 = 5
            r2 = 4
            r3 = 3
            r4 = 2
            r5 = 1
            r6 = -1
            switch(r0) {
                case -952838356: goto L_0x004d;
                case -392237989: goto L_0x0043;
                case -190005216: goto L_0x0038;
                case -135250500: goto L_0x002e;
                case 692591870: goto L_0x0024;
                case 693992349: goto L_0x0019;
                case 1315079558: goto L_0x000e;
                default: goto L_0x000d;
            }
        L_0x000d:
            goto L_0x0058
        L_0x000e:
            java.lang.String r0 = "keyguard_screenon_notification"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x000d
            r0 = r4
            goto L_0x0059
        L_0x0019:
            java.lang.String r0 = "lid switch open"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x000d
            r0 = 6
            goto L_0x0059
        L_0x0024:
            java.lang.String r0 = "android.policy:LID"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x000d
            r0 = r1
            goto L_0x0059
        L_0x002e:
            java.lang.String r0 = "android.policy:POWER"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x000d
            r0 = 0
            goto L_0x0059
        L_0x0038:
            java.lang.String r0 = "miui.policy:FINGERPRINT_DPAD_CENTER"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x000d
            r0 = r5
            goto L_0x0059
        L_0x0043:
            java.lang.String r0 = "android.policy:FINGERPRINT"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x000d
            r0 = r2
            goto L_0x0059
        L_0x004d:
            java.lang.String r0 = "keyguard_screenon_finger_pass"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x000d
            r0 = r3
            goto L_0x0059
        L_0x0058:
            r0 = r6
        L_0x0059:
            switch(r0) {
                case 0: goto L_0x0061;
                case 1: goto L_0x0060;
                case 2: goto L_0x005f;
                case 3: goto L_0x005e;
                case 4: goto L_0x005e;
                case 5: goto L_0x005d;
                case 6: goto L_0x005d;
                default: goto L_0x005c;
            }
        L_0x005c:
            return r6
        L_0x005d:
            return r1
        L_0x005e:
            return r2
        L_0x005f:
            return r3
        L_0x0060:
            return r4
        L_0x0061:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ScreenOnMonitor.getWakeupSrcIndex(java.lang.String):int");
    }

    private String getTimeoutSummary() {
        if (this.mBlockScreenOnEnd != 0 && this.mSetDisplayStateEnd == 0) {
            return "Abnormal in setting display state";
        }
        if (this.mBlockScreenOnEnd == 0 && this.mSetDisplayStateEnd != 0) {
            return "Abnormal in blocking screen on";
        }
        if (this.mBlockScreenOnEnd != 0 && this.mSetDisplayStateEnd != 0) {
            return "Abnormal in setting brightness";
        }
        if (this.mBlockScreenOnBegin == 0) {
            return "Abnormal before setting screen state";
        }
        if (this.mSetDisplayStateBegin == 0) {
            return "Abnormal before setting display state";
        }
        return "Abnormal in setting display state and blocking screen on";
    }

    private String getScreenOnDetail() {
        String totalTime;
        String setDisplayStateTime;
        String blockScreenOnTime;
        long currentTime = SystemClock.elapsedRealtime();
        if (this.mStopTime > 0) {
            totalTime = (this.mStopTime - this.mStartTime) + "ms";
        } else {
            totalTime = (currentTime - this.mStartTime) + "+ms";
        }
        if (this.mSetDisplayStateEnd > 0) {
            setDisplayStateTime = (this.mSetDisplayStateEnd - this.mSetDisplayStateBegin) + "ms";
        } else if (this.mSetDisplayStateBegin > 0) {
            setDisplayStateTime = (currentTime - this.mSetDisplayStateBegin) + "+ms";
        } else {
            setDisplayStateTime = totalTime;
        }
        if (this.mBlockScreenOnEnd > 0) {
            blockScreenOnTime = (this.mBlockScreenOnEnd - this.mBlockScreenOnBegin) + "ms";
        } else if (this.mBlockScreenOnBegin > 0) {
            blockScreenOnTime = (currentTime - this.mBlockScreenOnBegin) + "+ms";
        } else {
            blockScreenOnTime = totalTime;
        }
        return "total=" + totalTime + " setDisp=" + setDisplayStateTime + " blockScreen=" + blockScreenOnTime;
    }

    private String toCalendarTime(long now) {
        DATE.setTime(now);
        return SIMPLE_DATE_FORMAT.format(DATE);
    }

    private boolean isDisableUpload() {
        return Build.IS_STABLE_VERSION && !STABLE_SUPPORT_DEVICE.contains(Build.DEVICE);
    }
}
