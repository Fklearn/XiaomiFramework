package com.android.server.display;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ParceledListSlice;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.AmbientBrightnessDayStats;
import android.hardware.display.BrightnessChangeEvent;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManagerInternal;
import android.hardware.display.DisplayedContentSample;
import android.hardware.display.DisplayedContentSamplingAttributes;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserManager;
import android.provider.Settings;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.RingBuffer;
import com.android.server.LocalServices;
import com.android.server.display.AmbientBrightnessStatsTracker;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class BrightnessTracker {
    private static final String AMBIENT_BRIGHTNESS_STATS_FILE = "ambient_brightness_stats.xml";
    private static final String ATTR_BATTERY_LEVEL = "batteryLevel";
    private static final String ATTR_COLOR_SAMPLE_DURATION = "colorSampleDuration";
    private static final String ATTR_COLOR_TEMPERATURE = "colorTemperature";
    private static final String ATTR_COLOR_VALUE_BUCKETS = "colorValueBuckets";
    private static final String ATTR_DEFAULT_CONFIG = "defaultConfig";
    private static final String ATTR_LAST_NITS = "lastNits";
    private static final String ATTR_LUX = "lux";
    private static final String ATTR_LUX_TIMESTAMPS = "luxTimestamps";
    private static final String ATTR_NIGHT_MODE = "nightMode";
    private static final String ATTR_NITS = "nits";
    private static final String ATTR_PACKAGE_NAME = "packageName";
    private static final String ATTR_POWER_SAVE = "powerSaveFactor";
    private static final String ATTR_TIMESTAMP = "timestamp";
    private static final String ATTR_USER = "user";
    private static final String ATTR_USER_POINT = "userPoint";
    private static final int COLOR_SAMPLE_COMPONENT_MASK = 4;
    private static final long COLOR_SAMPLE_DURATION = TimeUnit.SECONDS.toSeconds(10);
    static final boolean DEBUG = false;
    private static final String EVENTS_FILE = "brightness_events.xml";
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
    private static final long LUX_EVENT_HORIZON = TimeUnit.SECONDS.toNanos(10);
    private static final int MAX_EVENTS = 100;
    private static final long MAX_EVENT_AGE = TimeUnit.DAYS.toMillis(30);
    private static final int MSG_BACKGROUND_START = 0;
    private static final int MSG_BRIGHTNESS_CHANGED = 1;
    private static final int MSG_START_SENSOR_LISTENER = 3;
    private static final int MSG_STOP_SENSOR_LISTENER = 2;
    static final String TAG = "BrightnessTracker";
    private static final String TAG_EVENT = "event";
    private static final String TAG_EVENTS = "events";
    private AmbientBrightnessStatsTracker mAmbientBrightnessStatsTracker;
    /* access modifiers changed from: private */
    public final Handler mBgHandler;
    private BroadcastReceiver mBroadcastReceiver;
    private boolean mColorSamplingEnabled;
    /* access modifiers changed from: private */
    public final ContentResolver mContentResolver;
    private final Context mContext;
    private int mCurrentUserId = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
    private final Object mDataCollectionLock = new Object();
    private DisplayListener mDisplayListener;
    @GuardedBy({"mEventsLock"})
    private RingBuffer<BrightnessChangeEvent> mEvents = new RingBuffer<>(BrightnessChangeEvent.class, 100);
    @GuardedBy({"mEventsLock"})
    private boolean mEventsDirty;
    private final Object mEventsLock = new Object();
    private float mFrameRate;
    /* access modifiers changed from: private */
    public final Injector mInjector;
    @GuardedBy({"mDataCollectionLock"})
    private float mLastBatteryLevel = Float.NaN;
    @GuardedBy({"mDataCollectionLock"})
    private float mLastBrightness = -1.0f;
    @GuardedBy({"mDataCollectionLock"})
    private Deque<LightData> mLastSensorReadings = new ArrayDeque();
    private int mNoFramesToSample;
    private SensorListener mSensorListener;
    private boolean mSensorRegistered;
    private SettingsObserver mSettingsObserver;
    @GuardedBy({"mDataCollectionLock"})
    private boolean mStarted;
    private final UserManager mUserManager;
    private volatile boolean mWriteBrightnessTrackerStateScheduled;

    /* access modifiers changed from: package-private */
    public void adjustSensorListenerWithProximity(boolean positive) {
        this.mBgHandler.obtainMessage(positive ? 2 : 3).sendToTarget();
    }

    public BrightnessTracker(Context context, Injector injector) {
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        if (injector != null) {
            this.mInjector = injector;
        } else {
            this.mInjector = new Injector();
        }
        this.mBgHandler = new TrackerHandler(this.mInjector.getBackgroundHandler().getLooper());
        this.mUserManager = (UserManager) this.mContext.getSystemService(UserManager.class);
    }

    public void start(float initialBrightness) {
        this.mCurrentUserId = ActivityManager.getCurrentUser();
        this.mBgHandler.obtainMessage(0, Float.valueOf(initialBrightness)).sendToTarget();
    }

    /* access modifiers changed from: private */
    public void backgroundStart(float initialBrightness) {
        readEvents();
        readAmbientBrightnessStats();
        this.mSensorListener = new SensorListener();
        this.mSettingsObserver = new SettingsObserver(this.mBgHandler);
        this.mInjector.registerBrightnessModeObserver(this.mContentResolver, this.mSettingsObserver);
        startSensorListener();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_SHUTDOWN");
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        this.mBroadcastReceiver = new Receiver();
        this.mInjector.registerReceiver(this.mContext, this.mBroadcastReceiver, intentFilter);
        this.mInjector.scheduleIdleJob(this.mContext);
        synchronized (this.mDataCollectionLock) {
            this.mLastBrightness = initialBrightness;
            this.mStarted = true;
        }
        enableColorSampling();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void stop() {
        this.mBgHandler.removeMessages(0);
        stopSensorListener();
        this.mInjector.unregisterSensorListener(this.mContext, this.mSensorListener);
        this.mInjector.unregisterBrightnessModeObserver(this.mContext, this.mSettingsObserver);
        this.mInjector.unregisterReceiver(this.mContext, this.mBroadcastReceiver);
        this.mInjector.cancelIdleJob(this.mContext);
        synchronized (this.mDataCollectionLock) {
            this.mStarted = false;
        }
        disableColorSampling();
    }

    public void onSwitchUser(int newUserId) {
        this.mCurrentUserId = newUserId;
    }

    public ParceledListSlice<BrightnessChangeEvent> getEvents(int userId, boolean includePackage) {
        BrightnessChangeEvent[] events;
        synchronized (this.mEventsLock) {
            events = (BrightnessChangeEvent[]) this.mEvents.toArray();
        }
        int[] profiles = this.mInjector.getProfileIds(this.mUserManager, userId);
        Map<Integer, Boolean> toRedact = new HashMap<>();
        int i = 0;
        while (true) {
            boolean redact = true;
            if (i >= profiles.length) {
                break;
            }
            int profileId = profiles[i];
            if (includePackage && profileId == userId) {
                redact = false;
            }
            toRedact.put(Integer.valueOf(profiles[i]), Boolean.valueOf(redact));
            i++;
        }
        ArrayList<BrightnessChangeEvent> out = new ArrayList<>(events.length);
        for (int i2 = 0; i2 < events.length; i2++) {
            Boolean redact2 = toRedact.get(Integer.valueOf(events[i2].userId));
            if (redact2 != null) {
                if (!redact2.booleanValue()) {
                    out.add(events[i2]);
                } else {
                    out.add(new BrightnessChangeEvent(events[i2], true));
                }
            }
        }
        return new ParceledListSlice<>(out);
    }

    public void persistBrightnessTrackerState() {
        scheduleWriteBrightnessTrackerState();
    }

    public void notifyBrightnessChanged(float brightness, boolean userInitiated, float powerBrightnessFactor, boolean isUserSetBrightness, boolean isDefaultBrightnessConfig) {
        this.mBgHandler.obtainMessage(1, userInitiated, 0, new BrightnessChangeValues(brightness, powerBrightnessFactor, isUserSetBrightness, isDefaultBrightnessConfig, this.mInjector.currentTimeMillis())).sendToTarget();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        r0 = r1.mInjector.getFocusedStack();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0096, code lost:
        if (r0 == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x009a, code lost:
        if (r0.topActivity == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x009c, code lost:
        r4.setUserId(r0.userId);
        r4.setPackageName(r0.topActivity.getPackageName());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00aa, code lost:
        r4.setNightMode(r1.mInjector.isNightDisplayActivated(r1.mContext));
        r4.setColorTemperature(r1.mInjector.getNightDisplayColorTemperature(r1.mContext));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00c3, code lost:
        if (r1.mColorSamplingEnabled == false) goto L_0x00f0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00c5, code lost:
        r0 = r1.mInjector.sampleColor(r1.mNoFramesToSample);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00cd, code lost:
        if (r0 == null) goto L_0x00f0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00d5, code lost:
        if (r0.getSampleComponent(android.hardware.display.DisplayedContentSample.ColorComponent.CHANNEL2) == null) goto L_0x00f0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00d7, code lost:
        r4.setColorValues(r0.getSampleComponent(android.hardware.display.DisplayedContentSample.ColorComponent.CHANNEL2), (long) java.lang.Math.round((((float) r0.getNumFrames()) / r1.mFrameRate) * 1000.0f));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00f0, code lost:
        r2 = r4.build();
        r3 = r1.mEventsLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00f6, code lost:
        monitor-enter(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
        r1.mEventsDirty = true;
        r1.mEvents.append(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00ff, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0100, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0106, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleBrightnessChanged(float r21, boolean r22, float r23, boolean r24, boolean r25, long r26) {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            java.lang.Object r3 = r1.mDataCollectionLock
            monitor-enter(r3)
            boolean r0 = r1.mStarted     // Catch:{ all -> 0x0109 }
            if (r0 != 0) goto L_0x000d
            monitor-exit(r3)     // Catch:{ all -> 0x0109 }
            return
        L_0x000d:
            float r0 = r1.mLastBrightness     // Catch:{ all -> 0x0109 }
            r1.mLastBrightness = r2     // Catch:{ all -> 0x0109 }
            if (r22 != 0) goto L_0x0015
            monitor-exit(r3)     // Catch:{ all -> 0x0109 }
            return
        L_0x0015:
            android.hardware.display.BrightnessChangeEvent$Builder r4 = new android.hardware.display.BrightnessChangeEvent$Builder     // Catch:{ all -> 0x0109 }
            r4.<init>()     // Catch:{ all -> 0x0109 }
            r4.setBrightness(r2)     // Catch:{ all -> 0x0109 }
            r5 = r26
            r4.setTimeStamp(r5)     // Catch:{ all -> 0x0109 }
            r7 = r23
            r4.setPowerBrightnessFactor(r7)     // Catch:{ all -> 0x0109 }
            r8 = r24
            r4.setUserBrightnessPoint(r8)     // Catch:{ all -> 0x0107 }
            r9 = r25
            r4.setIsDefaultBrightnessConfig(r9)     // Catch:{ all -> 0x0110 }
            java.util.Deque<com.android.server.display.BrightnessTracker$LightData> r10 = r1.mLastSensorReadings     // Catch:{ all -> 0x0110 }
            int r10 = r10.size()     // Catch:{ all -> 0x0110 }
            if (r10 != 0) goto L_0x003b
            monitor-exit(r3)     // Catch:{ all -> 0x0110 }
            return
        L_0x003b:
            float[] r11 = new float[r10]     // Catch:{ all -> 0x0110 }
            long[] r12 = new long[r10]     // Catch:{ all -> 0x0110 }
            r13 = 0
            com.android.server.display.BrightnessTracker$Injector r14 = r1.mInjector     // Catch:{ all -> 0x0110 }
            long r14 = r14.currentTimeMillis()     // Catch:{ all -> 0x0110 }
            com.android.server.display.BrightnessTracker$Injector r2 = r1.mInjector     // Catch:{ all -> 0x0110 }
            long r16 = r2.elapsedRealtimeNanos()     // Catch:{ all -> 0x0110 }
            java.util.Deque<com.android.server.display.BrightnessTracker$LightData> r2 = r1.mLastSensorReadings     // Catch:{ all -> 0x0110 }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x0110 }
        L_0x0052:
            boolean r18 = r2.hasNext()     // Catch:{ all -> 0x0110 }
            if (r18 == 0) goto L_0x0080
            java.lang.Object r18 = r2.next()     // Catch:{ all -> 0x0110 }
            com.android.server.display.BrightnessTracker$LightData r18 = (com.android.server.display.BrightnessTracker.LightData) r18     // Catch:{ all -> 0x0110 }
            r19 = r18
            r18 = r2
            r2 = r19
            float r5 = r2.lux     // Catch:{ all -> 0x0110 }
            r11[r13] = r5     // Catch:{ all -> 0x0110 }
            java.util.concurrent.TimeUnit r5 = java.util.concurrent.TimeUnit.NANOSECONDS     // Catch:{ all -> 0x0110 }
            long r6 = r2.timestamp     // Catch:{ all -> 0x0110 }
            long r6 = r16 - r6
            long r5 = r5.toMillis(r6)     // Catch:{ all -> 0x0110 }
            long r5 = r14 - r5
            r12[r13] = r5     // Catch:{ all -> 0x0110 }
            int r13 = r13 + 1
            r7 = r23
            r5 = r26
            r2 = r18
            goto L_0x0052
        L_0x0080:
            r4.setLuxValues(r11)     // Catch:{ all -> 0x0110 }
            r4.setLuxTimestamps(r12)     // Catch:{ all -> 0x0110 }
            float r2 = r1.mLastBatteryLevel     // Catch:{ all -> 0x0110 }
            r4.setBatteryLevel(r2)     // Catch:{ all -> 0x0110 }
            r4.setLastBrightness(r0)     // Catch:{ all -> 0x0110 }
            monitor-exit(r3)     // Catch:{ all -> 0x0110 }
            com.android.server.display.BrightnessTracker$Injector r0 = r1.mInjector     // Catch:{ RemoteException -> 0x0105 }
            android.app.ActivityManager$StackInfo r0 = r0.getFocusedStack()     // Catch:{ RemoteException -> 0x0105 }
            if (r0 == 0) goto L_0x0104
            android.content.ComponentName r2 = r0.topActivity     // Catch:{ RemoteException -> 0x0105 }
            if (r2 == 0) goto L_0x0104
            int r2 = r0.userId     // Catch:{ RemoteException -> 0x0105 }
            r4.setUserId(r2)     // Catch:{ RemoteException -> 0x0105 }
            android.content.ComponentName r2 = r0.topActivity     // Catch:{ RemoteException -> 0x0105 }
            java.lang.String r2 = r2.getPackageName()     // Catch:{ RemoteException -> 0x0105 }
            r4.setPackageName(r2)     // Catch:{ RemoteException -> 0x0105 }
            com.android.server.display.BrightnessTracker$Injector r0 = r1.mInjector
            android.content.Context r2 = r1.mContext
            boolean r0 = r0.isNightDisplayActivated(r2)
            r4.setNightMode(r0)
            com.android.server.display.BrightnessTracker$Injector r0 = r1.mInjector
            android.content.Context r2 = r1.mContext
            int r0 = r0.getNightDisplayColorTemperature(r2)
            r4.setColorTemperature(r0)
            boolean r0 = r1.mColorSamplingEnabled
            if (r0 == 0) goto L_0x00f0
            com.android.server.display.BrightnessTracker$Injector r0 = r1.mInjector
            int r2 = r1.mNoFramesToSample
            android.hardware.display.DisplayedContentSample r0 = r0.sampleColor(r2)
            if (r0 == 0) goto L_0x00f0
            android.hardware.display.DisplayedContentSample$ColorComponent r2 = android.hardware.display.DisplayedContentSample.ColorComponent.CHANNEL2
            long[] r2 = r0.getSampleComponent(r2)
            if (r2 == 0) goto L_0x00f0
            long r2 = r0.getNumFrames()
            float r2 = (float) r2
            float r3 = r1.mFrameRate
            float r2 = r2 / r3
            r3 = 1148846080(0x447a0000, float:1000.0)
            float r2 = r2 * r3
            android.hardware.display.DisplayedContentSample$ColorComponent r3 = android.hardware.display.DisplayedContentSample.ColorComponent.CHANNEL2
            long[] r3 = r0.getSampleComponent(r3)
            int r5 = java.lang.Math.round(r2)
            long r5 = (long) r5
            r4.setColorValues(r3, r5)
        L_0x00f0:
            android.hardware.display.BrightnessChangeEvent r2 = r4.build()
            java.lang.Object r3 = r1.mEventsLock
            monitor-enter(r3)
            r0 = 1
            r1.mEventsDirty = r0     // Catch:{ all -> 0x0101 }
            com.android.internal.util.RingBuffer<android.hardware.display.BrightnessChangeEvent> r0 = r1.mEvents     // Catch:{ all -> 0x0101 }
            r0.append(r2)     // Catch:{ all -> 0x0101 }
            monitor-exit(r3)     // Catch:{ all -> 0x0101 }
            return
        L_0x0101:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0101 }
            throw r0
        L_0x0104:
            return
        L_0x0105:
            r0 = move-exception
            return
        L_0x0107:
            r0 = move-exception
            goto L_0x010c
        L_0x0109:
            r0 = move-exception
            r8 = r24
        L_0x010c:
            r9 = r25
        L_0x010e:
            monitor-exit(r3)     // Catch:{ all -> 0x0110 }
            throw r0
        L_0x0110:
            r0 = move-exception
            goto L_0x010e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.BrightnessTracker.handleBrightnessChanged(float, boolean, float, boolean, boolean, long):void");
    }

    /* access modifiers changed from: private */
    public void startSensorListener() {
        if (this.mAmbientBrightnessStatsTracker != null && !this.mSensorRegistered && this.mInjector.isInteractive(this.mContext) && this.mInjector.isBrightnessModeAutomatic(this.mContentResolver)) {
            this.mAmbientBrightnessStatsTracker.start();
            this.mSensorRegistered = true;
            Injector injector = this.mInjector;
            injector.registerSensorListener(this.mContext, this.mSensorListener, injector.getBackgroundHandler());
        }
    }

    /* access modifiers changed from: private */
    public void stopSensorListener() {
        AmbientBrightnessStatsTracker ambientBrightnessStatsTracker = this.mAmbientBrightnessStatsTracker;
        if (ambientBrightnessStatsTracker != null && this.mSensorRegistered) {
            ambientBrightnessStatsTracker.stop();
            this.mInjector.unregisterSensorListener(this.mContext, this.mSensorListener);
            this.mSensorRegistered = false;
        }
    }

    /* access modifiers changed from: private */
    public void scheduleWriteBrightnessTrackerState() {
        if (!this.mWriteBrightnessTrackerStateScheduled) {
            this.mBgHandler.post(new Runnable() {
                public final void run() {
                    BrightnessTracker.this.lambda$scheduleWriteBrightnessTrackerState$0$BrightnessTracker();
                }
            });
            this.mWriteBrightnessTrackerStateScheduled = true;
        }
    }

    public /* synthetic */ void lambda$scheduleWriteBrightnessTrackerState$0$BrightnessTracker() {
        this.mWriteBrightnessTrackerStateScheduled = false;
        writeEvents();
        writeAmbientBrightnessStats();
    }

    private void writeEvents() {
        synchronized (this.mEventsLock) {
            if (this.mEventsDirty) {
                AtomicFile writeTo = this.mInjector.getFile(EVENTS_FILE);
                if (writeTo != null) {
                    if (this.mEvents.isEmpty()) {
                        if (writeTo.exists()) {
                            writeTo.delete();
                        }
                        this.mEventsDirty = false;
                    } else {
                        FileOutputStream output = null;
                        try {
                            output = writeTo.startWrite();
                            writeEventsLocked(output);
                            writeTo.finishWrite(output);
                            this.mEventsDirty = false;
                        } catch (IOException e) {
                            writeTo.failWrite(output);
                            Slog.e(TAG, "Failed to write change mEvents.", e);
                        }
                    }
                }
            }
        }
    }

    private void writeAmbientBrightnessStats() {
        AtomicFile writeTo = this.mInjector.getFile(AMBIENT_BRIGHTNESS_STATS_FILE);
        if (writeTo != null) {
            FileOutputStream output = null;
            try {
                output = writeTo.startWrite();
                this.mAmbientBrightnessStatsTracker.writeStats(output);
                writeTo.finishWrite(output);
            } catch (IOException e) {
                writeTo.failWrite(output);
                Slog.e(TAG, "Failed to write ambient brightness stats.", e);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* JADX INFO: finally extract failed */
    private void readEvents() {
        synchronized (this.mEventsLock) {
            this.mEventsDirty = true;
            this.mEvents.clear();
            AtomicFile readFrom = this.mInjector.getFile(EVENTS_FILE);
            if (readFrom != null && readFrom.exists()) {
                FileInputStream input = null;
                try {
                    input = readFrom.openRead();
                    readEventsLocked(input);
                    IoUtils.closeQuietly(input);
                } catch (IOException e) {
                    try {
                        readFrom.delete();
                        Slog.e(TAG, "Failed to read change mEvents.", e);
                        IoUtils.closeQuietly(input);
                    } catch (Throwable th) {
                        IoUtils.closeQuietly(input);
                        throw th;
                    }
                }
            }
        }
    }

    private void readAmbientBrightnessStats() {
        this.mAmbientBrightnessStatsTracker = new AmbientBrightnessStatsTracker(this.mUserManager, (AmbientBrightnessStatsTracker.Injector) null);
        AtomicFile readFrom = this.mInjector.getFile(AMBIENT_BRIGHTNESS_STATS_FILE);
        if (readFrom != null && readFrom.exists()) {
            FileInputStream input = null;
            try {
                input = readFrom.openRead();
                this.mAmbientBrightnessStatsTracker.readStats(input);
            } catch (IOException e) {
                readFrom.delete();
                Slog.e(TAG, "Failed to read ambient brightness stats.", e);
            } catch (Throwable th) {
                IoUtils.closeQuietly(input);
                throw th;
            }
            IoUtils.closeQuietly(input);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mEventsLock"})
    @VisibleForTesting
    public void writeEventsLocked(OutputStream stream) throws IOException {
        String str;
        XmlSerializer out = new FastXmlSerializer();
        out.setOutput(stream, StandardCharsets.UTF_8.name());
        String str2 = null;
        out.startDocument((String) null, true);
        out.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        out.startTag((String) null, TAG_EVENTS);
        BrightnessChangeEvent[] toWrite = (BrightnessChangeEvent[]) this.mEvents.toArray();
        this.mEvents.clear();
        long timeCutOff = this.mInjector.currentTimeMillis() - MAX_EVENT_AGE;
        int i = 0;
        while (i < toWrite.length) {
            int userSerialNo = this.mInjector.getUserSerialNumber(this.mUserManager, toWrite[i].userId);
            if (userSerialNo != -1 && toWrite[i].timeStamp > timeCutOff) {
                this.mEvents.append(toWrite[i]);
                out.startTag(str2, TAG_EVENT);
                out.attribute(str2, ATTR_NITS, Float.toString(toWrite[i].brightness));
                out.attribute(str2, "timestamp", Long.toString(toWrite[i].timeStamp));
                out.attribute(str2, ATTR_PACKAGE_NAME, toWrite[i].packageName);
                out.attribute(str2, ATTR_USER, Integer.toString(userSerialNo));
                out.attribute(str2, ATTR_BATTERY_LEVEL, Float.toString(toWrite[i].batteryLevel));
                out.attribute(str2, ATTR_NIGHT_MODE, Boolean.toString(toWrite[i].nightMode));
                out.attribute(str2, ATTR_COLOR_TEMPERATURE, Integer.toString(toWrite[i].colorTemperature));
                out.attribute(str2, ATTR_LAST_NITS, Float.toString(toWrite[i].lastBrightness));
                out.attribute(str2, ATTR_DEFAULT_CONFIG, Boolean.toString(toWrite[i].isDefaultBrightnessConfig));
                out.attribute(str2, ATTR_POWER_SAVE, Float.toString(toWrite[i].powerBrightnessFactor));
                out.attribute(str2, ATTR_USER_POINT, Boolean.toString(toWrite[i].isUserSetBrightness));
                StringBuilder luxValues = new StringBuilder();
                StringBuilder luxTimestamps = new StringBuilder();
                for (int j = 0; j < toWrite[i].luxValues.length; j++) {
                    if (j > 0) {
                        luxValues.append(',');
                        luxTimestamps.append(',');
                    }
                    luxValues.append(Float.toString(toWrite[i].luxValues[j]));
                    luxTimestamps.append(Long.toString(toWrite[i].luxTimestamps[j]));
                }
                out.attribute(str2, ATTR_LUX, luxValues.toString());
                out.attribute(str2, ATTR_LUX_TIMESTAMPS, luxTimestamps.toString());
                if (toWrite[i].colorValueBuckets == null || toWrite[i].colorValueBuckets.length <= 0) {
                    str = str2;
                } else {
                    out.attribute(str2, ATTR_COLOR_SAMPLE_DURATION, Long.toString(toWrite[i].colorSampleDuration));
                    StringBuilder buckets = new StringBuilder();
                    for (int j2 = 0; j2 < toWrite[i].colorValueBuckets.length; j2++) {
                        if (j2 > 0) {
                            buckets.append(',');
                        }
                        buckets.append(Long.toString(toWrite[i].colorValueBuckets[j2]));
                    }
                    str = null;
                    out.attribute((String) null, ATTR_COLOR_VALUE_BUCKETS, buckets.toString());
                }
                out.endTag(str, TAG_EVENT);
            }
            i++;
            str2 = null;
        }
        out.endTag((String) null, TAG_EVENTS);
        out.endDocument();
        stream.flush();
    }

    /* Debug info: failed to restart local var, previous not found, register: 36 */
    /* access modifiers changed from: package-private */
    @GuardedBy({"mEventsLock"})
    @VisibleForTesting
    public void readEventsLocked(InputStream stream) throws IOException {
        int i;
        XmlPullParser parser;
        int outerDepth;
        int type;
        String str;
        XmlPullParser parser2;
        int outerDepth2;
        int type2;
        String tag;
        String str2;
        String str3 = ",";
        try {
            XmlPullParser parser3 = Xml.newPullParser();
            parser3.setInput(stream, StandardCharsets.UTF_8.name());
            while (true) {
                int next = parser3.next();
                int type3 = next;
                i = 1;
                if (next == 1 || type3 == 2) {
                    String tag2 = parser3.getName();
                }
            }
            String tag22 = parser3.getName();
            if (TAG_EVENTS.equals(tag22)) {
                long timeCutOff = this.mInjector.currentTimeMillis() - MAX_EVENT_AGE;
                parser3.next();
                int outerDepth3 = parser3.getDepth();
                while (true) {
                    int next2 = parser3.next();
                    int type4 = next2;
                    if (next2 == i) {
                        int i2 = type4;
                        int i3 = outerDepth3;
                        return;
                    } else if (type4 != 3 || parser3.getDepth() > outerDepth3) {
                        if (type4 == 3) {
                            str = str3;
                            parser = parser3;
                            type = type4;
                            outerDepth = outerDepth3;
                        } else if (type4 == 4) {
                            str = str3;
                            parser = parser3;
                            type = type4;
                            outerDepth = outerDepth3;
                        } else {
                            String tag3 = parser3.getName();
                            if (TAG_EVENT.equals(tag3)) {
                                BrightnessChangeEvent.Builder builder = new BrightnessChangeEvent.Builder();
                                String brightness = parser3.getAttributeValue((String) null, ATTR_NITS);
                                builder.setBrightness(Float.parseFloat(brightness));
                                builder.setTimeStamp(Long.parseLong(parser3.getAttributeValue((String) null, "timestamp")));
                                builder.setPackageName(parser3.getAttributeValue((String) null, ATTR_PACKAGE_NAME));
                                builder.setUserId(this.mInjector.getUserId(this.mUserManager, Integer.parseInt(parser3.getAttributeValue((String) null, ATTR_USER))));
                                String batteryLevel = parser3.getAttributeValue((String) null, ATTR_BATTERY_LEVEL);
                                builder.setBatteryLevel(Float.parseFloat(batteryLevel));
                                builder.setNightMode(Boolean.parseBoolean(parser3.getAttributeValue((String) null, ATTR_NIGHT_MODE)));
                                tag = tag3;
                                String colorTemperature = parser3.getAttributeValue((String) null, ATTR_COLOR_TEMPERATURE);
                                builder.setColorTemperature(Integer.parseInt(colorTemperature));
                                String str4 = colorTemperature;
                                String lastBrightness = parser3.getAttributeValue((String) null, ATTR_LAST_NITS);
                                builder.setLastBrightness(Float.parseFloat(lastBrightness));
                                String str5 = lastBrightness;
                                String luxValue = parser3.getAttributeValue((String) null, ATTR_LUX);
                                String luxTimestamp = parser3.getAttributeValue((String) null, ATTR_LUX_TIMESTAMPS);
                                String[] luxValuesStrings = luxValue.split(str3);
                                String str6 = luxTimestamp;
                                type2 = type4;
                                String[] luxTimestampsStrings = luxTimestamp.split(str3);
                                String str7 = batteryLevel;
                                if (luxValuesStrings.length != luxTimestampsStrings.length) {
                                    str2 = str3;
                                    parser2 = parser3;
                                    outerDepth2 = outerDepth3;
                                } else {
                                    float[] luxValues = new float[luxValuesStrings.length];
                                    long[] luxTimestamps = new long[luxValuesStrings.length];
                                    outerDepth2 = outerDepth3;
                                    int i4 = 0;
                                    while (true) {
                                        String brightness2 = brightness;
                                        if (i4 >= luxValues.length) {
                                            break;
                                        }
                                        luxValues[i4] = Float.parseFloat(luxValuesStrings[i4]);
                                        luxTimestamps[i4] = Long.parseLong(luxTimestampsStrings[i4]);
                                        i4++;
                                        brightness = brightness2;
                                    }
                                    builder.setLuxValues(luxValues);
                                    builder.setLuxTimestamps(luxTimestamps);
                                    String defaultConfig = parser3.getAttributeValue((String) null, ATTR_DEFAULT_CONFIG);
                                    if (defaultConfig != null) {
                                        builder.setIsDefaultBrightnessConfig(Boolean.parseBoolean(defaultConfig));
                                    }
                                    float[] fArr = luxValues;
                                    String powerSave = parser3.getAttributeValue((String) null, ATTR_POWER_SAVE);
                                    if (powerSave != null) {
                                        builder.setPowerBrightnessFactor(Float.parseFloat(powerSave));
                                    } else {
                                        builder.setPowerBrightnessFactor(1.0f);
                                    }
                                    String str8 = powerSave;
                                    String userPoint = parser3.getAttributeValue((String) null, ATTR_USER_POINT);
                                    if (userPoint != null) {
                                        builder.setUserBrightnessPoint(Boolean.parseBoolean(userPoint));
                                    }
                                    String str9 = userPoint;
                                    String colorSampleDurationString = parser3.getAttributeValue((String) null, ATTR_COLOR_SAMPLE_DURATION);
                                    String[] strArr = luxValuesStrings;
                                    String colorValueBucketsString = parser3.getAttributeValue((String) null, ATTR_COLOR_VALUE_BUCKETS);
                                    if (colorSampleDurationString == null || colorValueBucketsString == null) {
                                        str2 = str3;
                                        parser2 = parser3;
                                        String str10 = colorValueBucketsString;
                                    } else {
                                        long colorSampleDuration = Long.parseLong(colorSampleDurationString);
                                        String[] buckets = colorValueBucketsString.split(str3);
                                        str2 = str3;
                                        long[] bucketValues = new long[buckets.length];
                                        parser2 = parser3;
                                        int i5 = 0;
                                        while (true) {
                                            String colorValueBucketsString2 = colorValueBucketsString;
                                            if (i5 >= bucketValues.length) {
                                                break;
                                            }
                                            bucketValues[i5] = Long.parseLong(buckets[i5]);
                                            i5++;
                                            colorValueBucketsString = colorValueBucketsString2;
                                        }
                                        builder.setColorValues(bucketValues, colorSampleDuration);
                                    }
                                    BrightnessChangeEvent event = builder.build();
                                    if (event.userId != -1 && event.timeStamp > timeCutOff && event.luxValues.length > 0) {
                                        this.mEvents.append(event);
                                    }
                                }
                            } else {
                                str2 = str3;
                                parser2 = parser3;
                                tag = tag3;
                                type2 = type4;
                                outerDepth2 = outerDepth3;
                            }
                            InputStream inputStream = stream;
                            str3 = str2;
                            String str11 = tag;
                            int i6 = type2;
                            outerDepth3 = outerDepth2;
                            parser3 = parser2;
                            i = 1;
                        }
                        InputStream inputStream2 = stream;
                        str3 = str;
                        int i7 = type;
                        outerDepth3 = outerDepth;
                        parser3 = parser;
                        i = 1;
                    } else {
                        return;
                    }
                }
            } else {
                XmlPullParser xmlPullParser = parser3;
                throw new XmlPullParserException("Events not found in brightness tracker file " + tag22);
            }
        } catch (IOException | NullPointerException | NumberFormatException | XmlPullParserException e) {
            this.mEvents = new RingBuffer<>(BrightnessChangeEvent.class, 100);
            Slog.e(TAG, "Failed to parse brightness event", e);
            throw new IOException("failed to parse file", e);
        }
    }

    public void dump(PrintWriter pw) {
        pw.println("BrightnessTracker state:");
        synchronized (this.mDataCollectionLock) {
            pw.println("  mStarted=" + this.mStarted);
            pw.println("  mLastBatteryLevel=" + this.mLastBatteryLevel);
            pw.println("  mLastBrightness=" + this.mLastBrightness);
            pw.println("  mLastSensorReadings.size=" + this.mLastSensorReadings.size());
            if (!this.mLastSensorReadings.isEmpty()) {
                pw.println("  mLastSensorReadings time span " + this.mLastSensorReadings.peekFirst().timestamp + "->" + this.mLastSensorReadings.peekLast().timestamp);
            }
        }
        synchronized (this.mEventsLock) {
            pw.println("  mEventsDirty=" + this.mEventsDirty);
            pw.println("  mEvents.size=" + this.mEvents.size());
            BrightnessChangeEvent[] events = (BrightnessChangeEvent[]) this.mEvents.toArray();
            for (int i = 0; i < events.length; i++) {
                pw.print("    " + FORMAT.format(new Date(events[i].timeStamp)));
                pw.print(", userId=" + events[i].userId);
                pw.print(", " + events[i].lastBrightness + "->" + events[i].brightness);
                StringBuilder sb = new StringBuilder();
                sb.append(", isUserSetBrightness=");
                sb.append(events[i].isUserSetBrightness);
                pw.print(sb.toString());
                pw.print(", powerBrightnessFactor=" + events[i].powerBrightnessFactor);
                pw.print(", isDefaultBrightnessConfig=" + events[i].isDefaultBrightnessConfig);
                pw.print(" {");
                for (int j = 0; j < events[i].luxValues.length; j++) {
                    if (j != 0) {
                        pw.print(", ");
                    }
                    pw.print("(" + events[i].luxValues[j] + "," + events[i].luxTimestamps[j] + ")");
                }
                pw.println("}");
            }
        }
        pw.println("  mWriteBrightnessTrackerStateScheduled=" + this.mWriteBrightnessTrackerStateScheduled);
        this.mBgHandler.runWithScissors(new Runnable(pw) {
            private final /* synthetic */ PrintWriter f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                BrightnessTracker.this.lambda$dump$1$BrightnessTracker(this.f$1);
            }
        }, 1000);
        if (this.mAmbientBrightnessStatsTracker != null) {
            pw.println();
            this.mAmbientBrightnessStatsTracker.dump(pw);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: dumpLocal */
    public void lambda$dump$1$BrightnessTracker(PrintWriter pw) {
        pw.println("  mSensorRegistered=" + this.mSensorRegistered);
        pw.println("  mColorSamplingEnabled=" + this.mColorSamplingEnabled);
        pw.println("  mNoFramesToSample=" + this.mNoFramesToSample);
        pw.println("  mFrameRate=" + this.mFrameRate);
    }

    /* access modifiers changed from: private */
    public void enableColorSampling() {
        if (this.mInjector.isBrightnessModeAutomatic(this.mContentResolver) && this.mInjector.isInteractive(this.mContext) && !this.mColorSamplingEnabled) {
            this.mFrameRate = this.mInjector.getFrameRate(this.mContext);
            float f = this.mFrameRate;
            if (f <= 0.0f) {
                Slog.wtf(TAG, "Default display has a zero or negative framerate.");
                return;
            }
            this.mNoFramesToSample = (int) (f * ((float) COLOR_SAMPLE_DURATION));
            DisplayedContentSamplingAttributes attributes = this.mInjector.getSamplingAttributes();
            if (!(attributes == null || attributes.getPixelFormat() != 55 || (attributes.getComponentMask() & 4) == 0)) {
                this.mColorSamplingEnabled = this.mInjector.enableColorSampling(true, this.mNoFramesToSample);
            }
            if (this.mColorSamplingEnabled && this.mDisplayListener == null) {
                this.mDisplayListener = new DisplayListener();
                this.mInjector.registerDisplayListener(this.mContext, this.mDisplayListener, this.mBgHandler);
            }
        }
    }

    /* access modifiers changed from: private */
    public void disableColorSampling() {
        if (this.mColorSamplingEnabled) {
            this.mInjector.enableColorSampling(false, 0);
            this.mColorSamplingEnabled = false;
            DisplayListener displayListener = this.mDisplayListener;
            if (displayListener != null) {
                this.mInjector.unRegisterDisplayListener(this.mContext, displayListener);
                this.mDisplayListener = null;
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateColorSampling() {
        if (this.mColorSamplingEnabled && this.mInjector.getFrameRate(this.mContext) != this.mFrameRate) {
            disableColorSampling();
            enableColorSampling();
        }
    }

    public ParceledListSlice<AmbientBrightnessDayStats> getAmbientBrightnessStats(int userId) {
        ArrayList<AmbientBrightnessDayStats> stats;
        AmbientBrightnessStatsTracker ambientBrightnessStatsTracker = this.mAmbientBrightnessStatsTracker;
        if (ambientBrightnessStatsTracker == null || (stats = ambientBrightnessStatsTracker.getUserStats(userId)) == null) {
            return ParceledListSlice.emptyList();
        }
        return new ParceledListSlice<>(stats);
    }

    private static class LightData {
        public float lux;
        public long timestamp;

        private LightData() {
        }
    }

    /* access modifiers changed from: private */
    public void recordSensorEvent(SensorEvent event) {
        long horizon = this.mInjector.elapsedRealtimeNanos() - LUX_EVENT_HORIZON;
        synchronized (this.mDataCollectionLock) {
            if (this.mLastSensorReadings.isEmpty() || event.timestamp >= this.mLastSensorReadings.getLast().timestamp) {
                LightData data = null;
                while (!this.mLastSensorReadings.isEmpty() && this.mLastSensorReadings.getFirst().timestamp < horizon) {
                    data = this.mLastSensorReadings.removeFirst();
                }
                if (data != null) {
                    this.mLastSensorReadings.addFirst(data);
                }
                LightData data2 = new LightData();
                data2.timestamp = event.timestamp;
                data2.lux = event.values[0];
                this.mLastSensorReadings.addLast(data2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void recordAmbientBrightnessStats(SensorEvent event) {
        this.mAmbientBrightnessStatsTracker.add(this.mCurrentUserId, event.values[0]);
    }

    /* access modifiers changed from: private */
    public void batteryLevelChanged(int level, int scale) {
        synchronized (this.mDataCollectionLock) {
            this.mLastBatteryLevel = ((float) level) / ((float) scale);
        }
    }

    private final class SensorListener implements SensorEventListener {
        private SensorListener() {
        }

        public void onSensorChanged(SensorEvent event) {
            BrightnessTracker.this.recordSensorEvent(event);
            BrightnessTracker.this.recordAmbientBrightnessStats(event);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    private final class DisplayListener implements DisplayManager.DisplayListener {
        private DisplayListener() {
        }

        public void onDisplayAdded(int displayId) {
        }

        public void onDisplayRemoved(int displayId) {
        }

        public void onDisplayChanged(int displayId) {
            if (displayId == 0) {
                BrightnessTracker.this.updateColorSampling();
            }
        }
    }

    private final class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange, Uri uri) {
            if (BrightnessTracker.this.mInjector.isBrightnessModeAutomatic(BrightnessTracker.this.mContentResolver)) {
                BrightnessTracker.this.mBgHandler.obtainMessage(3).sendToTarget();
            } else {
                BrightnessTracker.this.mBgHandler.obtainMessage(2).sendToTarget();
            }
        }
    }

    private final class Receiver extends BroadcastReceiver {
        private Receiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.ACTION_SHUTDOWN".equals(action)) {
                BrightnessTracker.this.stop();
                BrightnessTracker.this.scheduleWriteBrightnessTrackerState();
            } else if ("android.intent.action.BATTERY_CHANGED".equals(action)) {
                int level = intent.getIntExtra("level", -1);
                int scale = intent.getIntExtra("scale", 0);
                if (level != -1 && scale != 0) {
                    BrightnessTracker.this.batteryLevelChanged(level, scale);
                }
            } else if ("android.intent.action.SCREEN_OFF".equals(action)) {
                BrightnessTracker.this.mBgHandler.obtainMessage(2).sendToTarget();
            } else if ("android.intent.action.SCREEN_ON".equals(action)) {
                BrightnessTracker.this.mBgHandler.obtainMessage(3).sendToTarget();
            }
        }
    }

    private final class TrackerHandler extends Handler {
        public TrackerHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i != 0) {
                boolean userInitiatedChange = true;
                if (i == 1) {
                    BrightnessChangeValues values = (BrightnessChangeValues) msg.obj;
                    if (msg.arg1 != 1) {
                        userInitiatedChange = false;
                    }
                    BrightnessTracker.this.handleBrightnessChanged(values.brightness, userInitiatedChange, values.powerBrightnessFactor, values.isUserSetBrightness, values.isDefaultBrightnessConfig, values.timestamp);
                } else if (i == 2) {
                    BrightnessTracker.this.stopSensorListener();
                    BrightnessTracker.this.disableColorSampling();
                } else if (i == 3) {
                    BrightnessTracker.this.startSensorListener();
                    BrightnessTracker.this.enableColorSampling();
                }
            } else {
                BrightnessTracker.this.backgroundStart(((Float) msg.obj).floatValue());
            }
        }
    }

    private static class BrightnessChangeValues {
        final float brightness;
        final boolean isDefaultBrightnessConfig;
        final boolean isUserSetBrightness;
        final float powerBrightnessFactor;
        final long timestamp;

        BrightnessChangeValues(float brightness2, float powerBrightnessFactor2, boolean isUserSetBrightness2, boolean isDefaultBrightnessConfig2, long timestamp2) {
            this.brightness = brightness2;
            this.powerBrightnessFactor = powerBrightnessFactor2;
            this.isUserSetBrightness = isUserSetBrightness2;
            this.isDefaultBrightnessConfig = isDefaultBrightnessConfig2;
            this.timestamp = timestamp2;
        }
    }

    @VisibleForTesting
    static class Injector {
        Injector() {
        }

        public void registerSensorListener(Context context, SensorEventListener sensorListener, Handler handler) {
            SensorManager sensorManager = (SensorManager) context.getSystemService(SensorManager.class);
            sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(5), 3, handler);
        }

        public void unregisterSensorListener(Context context, SensorEventListener sensorListener) {
            ((SensorManager) context.getSystemService(SensorManager.class)).unregisterListener(sensorListener);
        }

        public void registerBrightnessModeObserver(ContentResolver resolver, ContentObserver settingsObserver) {
            resolver.registerContentObserver(Settings.System.getUriFor("screen_brightness_mode"), false, settingsObserver, -1);
        }

        public void unregisterBrightnessModeObserver(Context context, ContentObserver settingsObserver) {
            context.getContentResolver().unregisterContentObserver(settingsObserver);
        }

        public void registerReceiver(Context context, BroadcastReceiver receiver, IntentFilter filter) {
            context.registerReceiver(receiver, filter);
        }

        public void unregisterReceiver(Context context, BroadcastReceiver receiver) {
            context.unregisterReceiver(receiver);
        }

        public Handler getBackgroundHandler() {
            return BackgroundThread.getHandler();
        }

        public boolean isBrightnessModeAutomatic(ContentResolver resolver) {
            return Settings.System.getIntForUser(resolver, "screen_brightness_mode", 0, -2) == 1;
        }

        public int getSecureIntForUser(ContentResolver resolver, String setting, int defaultValue, int userId) {
            return Settings.Secure.getIntForUser(resolver, setting, defaultValue, userId);
        }

        public AtomicFile getFile(String filename) {
            return new AtomicFile(new File(Environment.getDataSystemDeDirectory(), filename));
        }

        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }

        public long elapsedRealtimeNanos() {
            return SystemClock.elapsedRealtimeNanos();
        }

        public int getUserSerialNumber(UserManager userManager, int userId) {
            return userManager.getUserSerialNumber(userId);
        }

        public int getUserId(UserManager userManager, int userSerialNumber) {
            return userManager.getUserHandle(userSerialNumber);
        }

        public int[] getProfileIds(UserManager userManager, int userId) {
            if (userManager != null) {
                return userManager.getProfileIds(userId, false);
            }
            return new int[]{userId};
        }

        public ActivityManager.StackInfo getFocusedStack() throws RemoteException {
            return ActivityTaskManager.getService().getFocusedStackInfo();
        }

        public void scheduleIdleJob(Context context) {
            BrightnessIdleJob.scheduleJob(context);
        }

        public void cancelIdleJob(Context context) {
            BrightnessIdleJob.cancelJob(context);
        }

        public boolean isInteractive(Context context) {
            return ((PowerManager) context.getSystemService(PowerManager.class)).isInteractive();
        }

        public int getNightDisplayColorTemperature(Context context) {
            return ((ColorDisplayManager) context.getSystemService(ColorDisplayManager.class)).getNightDisplayColorTemperature();
        }

        public boolean isNightDisplayActivated(Context context) {
            return ((ColorDisplayManager) context.getSystemService(ColorDisplayManager.class)).isNightDisplayActivated();
        }

        public DisplayedContentSample sampleColor(int noFramesToSample) {
            return ((DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class)).getDisplayedContentSample(0, (long) noFramesToSample, 0);
        }

        public float getFrameRate(Context context) {
            return ((DisplayManager) context.getSystemService(DisplayManager.class)).getDisplay(0).getRefreshRate();
        }

        public DisplayedContentSamplingAttributes getSamplingAttributes() {
            return ((DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class)).getDisplayedContentSamplingAttributes(0);
        }

        public boolean enableColorSampling(boolean enable, int noFrames) {
            return ((DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class)).setDisplayedContentSamplingEnabled(0, enable, 4, noFrames);
        }

        public void registerDisplayListener(Context context, DisplayManager.DisplayListener listener, Handler handler) {
            ((DisplayManager) context.getSystemService(DisplayManager.class)).registerDisplayListener(listener, handler);
        }

        public void unRegisterDisplayListener(Context context, DisplayManager.DisplayListener listener) {
            ((DisplayManager) context.getSystemService(DisplayManager.class)).unregisterDisplayListener(listener);
        }
    }
}
