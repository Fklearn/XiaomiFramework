package com.android.server.policy;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.Surface;
import java.io.PrintWriter;

public abstract class WindowOrientationListener {
    private static final int DEFAULT_BATCH_LATENCY = 100000;
    /* access modifiers changed from: private */
    public static final boolean LOG = SystemProperties.getBoolean("debug.orientation.log", false);
    private static final String TAG = "WindowOrientationListener";
    private static final boolean USE_GRAVITY_SENSOR = false;
    /* access modifiers changed from: private */
    public int mCurrentRotation;
    private boolean mEnabled;
    /* access modifiers changed from: private */
    public Handler mHandler;
    /* access modifiers changed from: private */
    public final Object mLock;
    private OrientationJudge mOrientationJudge;
    private int mRate;
    private Sensor mSensor;
    private SensorManager mSensorManager;
    private String mSensorType;

    public abstract void onProposedRotationChanged(int i);

    public WindowOrientationListener(Context context, Handler handler) {
        this(context, handler, 2);
    }

    private WindowOrientationListener(Context context, Handler handler, int rate) {
        this.mCurrentRotation = -1;
        this.mLock = new Object();
        this.mHandler = handler;
        this.mSensorManager = (SensorManager) context.getSystemService("sensor");
        this.mRate = rate;
        Sensor wakeUpDeviceOrientationSensor = null;
        Sensor nonWakeUpDeviceOrientationSensor = null;
        for (Sensor s : this.mSensorManager.getSensorList(27)) {
            if (s.isWakeUpSensor()) {
                wakeUpDeviceOrientationSensor = s;
            } else {
                nonWakeUpDeviceOrientationSensor = s;
            }
        }
        if (wakeUpDeviceOrientationSensor != null) {
            this.mSensor = wakeUpDeviceOrientationSensor;
        } else {
            this.mSensor = nonWakeUpDeviceOrientationSensor;
        }
        if (this.mSensor != null) {
            this.mOrientationJudge = new OrientationSensorJudge();
        }
        if (this.mOrientationJudge == null) {
            this.mSensor = this.mSensorManager.getDefaultSensor(1);
            if (this.mSensor != null) {
                this.mOrientationJudge = new AccelSensorJudge(context);
            }
        }
    }

    public void enable() {
        enable(true);
    }

    public void enable(boolean clearCurrentRotation) {
        synchronized (this.mLock) {
            if (this.mSensor == null) {
                Slog.w(TAG, "Cannot detect sensors. Not enabled");
            } else if (!this.mEnabled) {
                if (LOG) {
                    Slog.d(TAG, "WindowOrientationListener enabled clearCurrentRotation=" + clearCurrentRotation);
                }
                this.mOrientationJudge.resetLocked(clearCurrentRotation);
                if (this.mSensor.getType() == 1) {
                    this.mSensorManager.registerListener(this.mOrientationJudge, this.mSensor, this.mRate, DEFAULT_BATCH_LATENCY, this.mHandler);
                } else {
                    this.mSensorManager.registerListener(this.mOrientationJudge, this.mSensor, this.mRate, this.mHandler);
                }
                this.mEnabled = true;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void disable() {
        /*
            r3 = this;
            java.lang.Object r0 = r3.mLock
            monitor-enter(r0)
            android.hardware.Sensor r1 = r3.mSensor     // Catch:{ all -> 0x002c }
            if (r1 != 0) goto L_0x0010
            java.lang.String r1 = "WindowOrientationListener"
            java.lang.String r2 = "Cannot detect sensors. Invalid disable"
            android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x002c }
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            return
        L_0x0010:
            boolean r1 = r3.mEnabled     // Catch:{ all -> 0x002c }
            r2 = 1
            if (r1 != r2) goto L_0x002a
            boolean r1 = LOG     // Catch:{ all -> 0x002c }
            if (r1 == 0) goto L_0x0020
            java.lang.String r1 = "WindowOrientationListener"
            java.lang.String r2 = "WindowOrientationListener disabled"
            android.util.Slog.d(r1, r2)     // Catch:{ all -> 0x002c }
        L_0x0020:
            android.hardware.SensorManager r1 = r3.mSensorManager     // Catch:{ all -> 0x002c }
            com.android.server.policy.WindowOrientationListener$OrientationJudge r2 = r3.mOrientationJudge     // Catch:{ all -> 0x002c }
            r1.unregisterListener(r2)     // Catch:{ all -> 0x002c }
            r1 = 0
            r3.mEnabled = r1     // Catch:{ all -> 0x002c }
        L_0x002a:
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            return
        L_0x002c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.policy.WindowOrientationListener.disable():void");
    }

    public void onTouchStart() {
        synchronized (this.mLock) {
            if (this.mOrientationJudge != null) {
                this.mOrientationJudge.onTouchStartLocked();
            }
        }
    }

    public void onTouchEnd() {
        long whenElapsedNanos = SystemClock.elapsedRealtimeNanos();
        synchronized (this.mLock) {
            if (this.mOrientationJudge != null) {
                this.mOrientationJudge.onTouchEndLocked(whenElapsedNanos);
            }
        }
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public void setCurrentRotation(int rotation) {
        synchronized (this.mLock) {
            this.mCurrentRotation = rotation;
        }
    }

    public int getProposedRotation() {
        synchronized (this.mLock) {
            if (!this.mEnabled) {
                return -1;
            }
            int proposedRotationLocked = this.mOrientationJudge.getProposedRotationLocked();
            return proposedRotationLocked;
        }
    }

    public boolean canDetectOrientation() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mSensor != null;
        }
        return z;
    }

    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        synchronized (this.mLock) {
            proto.write(1133871366145L, this.mEnabled);
            proto.write(1159641169922L, this.mCurrentRotation);
        }
        proto.end(token);
    }

    public void dump(PrintWriter pw, String prefix) {
        synchronized (this.mLock) {
            pw.println(prefix + TAG);
            String prefix2 = prefix + "  ";
            pw.println(prefix2 + "mEnabled=" + this.mEnabled);
            pw.println(prefix2 + "mCurrentRotation=" + Surface.rotationToString(this.mCurrentRotation));
            pw.println(prefix2 + "mSensorType=" + this.mSensorType);
            pw.println(prefix2 + "mSensor=" + this.mSensor);
            pw.println(prefix2 + "mRate=" + this.mRate);
            if (this.mOrientationJudge != null) {
                this.mOrientationJudge.dumpLocked(pw, prefix2);
            }
        }
    }

    abstract class OrientationJudge implements SensorEventListener {
        protected static final float MILLIS_PER_NANO = 1.0E-6f;
        protected static final long NANOS_PER_MS = 1000000;
        protected static final long PROPOSAL_MIN_TIME_SINCE_TOUCH_END_NANOS = 500000000;

        public abstract void dumpLocked(PrintWriter printWriter, String str);

        public abstract int getProposedRotationLocked();

        public abstract void onAccuracyChanged(Sensor sensor, int i);

        public abstract void onSensorChanged(SensorEvent sensorEvent);

        public abstract void onTouchEndLocked(long j);

        public abstract void onTouchStartLocked();

        public abstract void resetLocked(boolean z);

        OrientationJudge() {
        }
    }

    final class AccelSensorJudge extends OrientationJudge {
        private static final float ACCELERATION_TOLERANCE = 4.0f;
        private static final int ACCELEROMETER_DATA_X = 0;
        private static final int ACCELEROMETER_DATA_Y = 1;
        private static final int ACCELEROMETER_DATA_Z = 2;
        private static final int ADJACENT_ORIENTATION_ANGLE_GAP = 45;
        private static final float FILTER_TIME_CONSTANT_MS = 200.0f;
        private static final float FLAT_ANGLE = 80.0f;
        private static final long FLAT_TIME_NANOS = 1000000000;
        private static final float MAX_ACCELERATION_MAGNITUDE = 13.80665f;
        private static final long MAX_FILTER_DELTA_TIME_NANOS = 1000000000;
        private static final int MAX_TILT = 80;
        private static final float MIN_ACCELERATION_MAGNITUDE = 5.80665f;
        private static final float NEAR_ZERO_MAGNITUDE = 1.0f;
        private static final long PROPOSAL_MIN_TIME_SINCE_ACCELERATION_ENDED_NANOS = 500000000;
        private static final long PROPOSAL_MIN_TIME_SINCE_FLAT_ENDED_NANOS = 500000000;
        private static final long PROPOSAL_MIN_TIME_SINCE_SWING_ENDED_NANOS = 300000000;
        private static final long PROPOSAL_SETTLE_TIME_NANOS = 40000000;
        private static final float RADIANS_TO_DEGREES = 57.29578f;
        private static final float SWING_AWAY_ANGLE_DELTA = 20.0f;
        private static final long SWING_TIME_NANOS = 300000000;
        private static final int TILT_HISTORY_SIZE = 200;
        private static final int TILT_OVERHEAD_ENTER = -40;
        private static final int TILT_OVERHEAD_EXIT = -15;
        private boolean mAccelerating;
        private long mAccelerationTimestampNanos;
        private boolean mFlat;
        private long mFlatTimestampNanos;
        private long mLastFilteredTimestampNanos;
        private float mLastFilteredX;
        private float mLastFilteredY;
        private float mLastFilteredZ;
        private boolean mOverhead;
        private int mPredictedRotation;
        private long mPredictedRotationTimestampNanos;
        private int mProposedRotation;
        private long mSwingTimestampNanos;
        private boolean mSwinging;
        private float[] mTiltHistory = new float[200];
        private int mTiltHistoryIndex;
        private long[] mTiltHistoryTimestampNanos = new long[200];
        private final int[][] mTiltToleranceConfig = {new int[]{-25, 70}, new int[]{-25, 65}, new int[]{-25, 60}, new int[]{-25, 65}};
        private long mTouchEndedTimestampNanos = Long.MIN_VALUE;
        private boolean mTouched;

        public AccelSensorJudge(Context context) {
            super();
            int[] tiltTolerance = context.getResources().getIntArray(17235992);
            if (tiltTolerance.length == 8) {
                for (int i = 0; i < 4; i++) {
                    int min = tiltTolerance[i * 2];
                    int max = tiltTolerance[(i * 2) + 1];
                    if (min < -90 || min > max || max > 90) {
                        Slog.wtf(WindowOrientationListener.TAG, "config_autoRotationTiltTolerance contains invalid range: min=" + min + ", max=" + max);
                    } else {
                        int[][] iArr = this.mTiltToleranceConfig;
                        iArr[i][0] = min;
                        iArr[i][1] = max;
                    }
                }
                return;
            }
            Slog.wtf(WindowOrientationListener.TAG, "config_autoRotationTiltTolerance should have exactly 8 elements");
        }

        public int getProposedRotationLocked() {
            return this.mProposedRotation;
        }

        public void dumpLocked(PrintWriter pw, String prefix) {
            pw.println(prefix + "AccelSensorJudge");
            String prefix2 = prefix + "  ";
            pw.println(prefix2 + "mProposedRotation=" + this.mProposedRotation);
            pw.println(prefix2 + "mPredictedRotation=" + this.mPredictedRotation);
            pw.println(prefix2 + "mLastFilteredX=" + this.mLastFilteredX);
            pw.println(prefix2 + "mLastFilteredY=" + this.mLastFilteredY);
            pw.println(prefix2 + "mLastFilteredZ=" + this.mLastFilteredZ);
            long delta = SystemClock.elapsedRealtimeNanos() - this.mLastFilteredTimestampNanos;
            pw.println(prefix2 + "mLastFilteredTimestampNanos=" + this.mLastFilteredTimestampNanos + " (" + (((float) delta) * 1.0E-6f) + "ms ago)");
            StringBuilder sb = new StringBuilder();
            sb.append(prefix2);
            sb.append("mTiltHistory={last: ");
            sb.append(getLastTiltLocked());
            sb.append("}");
            pw.println(sb.toString());
            pw.println(prefix2 + "mFlat=" + this.mFlat);
            pw.println(prefix2 + "mSwinging=" + this.mSwinging);
            pw.println(prefix2 + "mAccelerating=" + this.mAccelerating);
            pw.println(prefix2 + "mOverhead=" + this.mOverhead);
            pw.println(prefix2 + "mTouched=" + this.mTouched);
            StringBuilder sb2 = new StringBuilder();
            sb2.append(prefix2);
            sb2.append("mTiltToleranceConfig=[");
            pw.print(sb2.toString());
            for (int i = 0; i < 4; i++) {
                if (i != 0) {
                    pw.print(", ");
                }
                pw.print("[");
                pw.print(this.mTiltToleranceConfig[i][0]);
                pw.print(", ");
                pw.print(this.mTiltToleranceConfig[i][1]);
                pw.print("]");
            }
            pw.println("]");
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        /* JADX WARNING: Removed duplicated region for block: B:28:0x010b  */
        /* JADX WARNING: Removed duplicated region for block: B:82:0x028e  */
        /* JADX WARNING: Removed duplicated region for block: B:91:0x02b5  */
        /* JADX WARNING: Removed duplicated region for block: B:92:0x0372  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onSensorChanged(android.hardware.SensorEvent r26) {
            /*
                r25 = this;
                r1 = r25
                r2 = r26
                com.android.server.policy.WindowOrientationListener r0 = com.android.server.policy.WindowOrientationListener.this
                java.lang.Object r3 = r0.mLock
                monitor-enter(r3)
                float[] r0 = r2.values     // Catch:{ all -> 0x03a2 }
                r4 = 0
                r0 = r0[r4]     // Catch:{ all -> 0x03a2 }
                float[] r5 = r2.values     // Catch:{ all -> 0x03a2 }
                r6 = 1
                r5 = r5[r6]     // Catch:{ all -> 0x03a2 }
                float[] r7 = r2.values     // Catch:{ all -> 0x03a2 }
                r8 = 2
                r7 = r7[r8]     // Catch:{ all -> 0x03a2 }
                boolean r8 = com.android.server.policy.WindowOrientationListener.LOG     // Catch:{ all -> 0x03a2 }
                if (r8 == 0) goto L_0x005b
                java.lang.String r8 = "WindowOrientationListener"
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x03a2 }
                r9.<init>()     // Catch:{ all -> 0x03a2 }
                java.lang.String r10 = "Raw acceleration vector: x="
                r9.append(r10)     // Catch:{ all -> 0x03a2 }
                r9.append(r0)     // Catch:{ all -> 0x03a2 }
                java.lang.String r10 = ", y="
                r9.append(r10)     // Catch:{ all -> 0x03a2 }
                r9.append(r5)     // Catch:{ all -> 0x03a2 }
                java.lang.String r10 = ", z="
                r9.append(r10)     // Catch:{ all -> 0x03a2 }
                r9.append(r7)     // Catch:{ all -> 0x03a2 }
                java.lang.String r10 = ", magnitude="
                r9.append(r10)     // Catch:{ all -> 0x03a2 }
                float r10 = r0 * r0
                float r11 = r5 * r5
                float r10 = r10 + r11
                float r11 = r7 * r7
                float r10 = r10 + r11
                double r10 = (double) r10     // Catch:{ all -> 0x03a2 }
                double r10 = java.lang.Math.sqrt(r10)     // Catch:{ all -> 0x03a2 }
                r9.append(r10)     // Catch:{ all -> 0x03a2 }
                java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x03a2 }
                android.util.Slog.v(r8, r9)     // Catch:{ all -> 0x03a2 }
            L_0x005b:
                long r8 = r2.timestamp     // Catch:{ all -> 0x03a2 }
                long r10 = r1.mLastFilteredTimestampNanos     // Catch:{ all -> 0x03a2 }
                long r12 = r8 - r10
                float r12 = (float) r12     // Catch:{ all -> 0x03a2 }
                r13 = 897988541(0x358637bd, float:1.0E-6)
                float r12 = r12 * r13
                int r14 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
                if (r14 < 0) goto L_0x00ec
                r14 = 1000000000(0x3b9aca00, double:4.94065646E-315)
                long r14 = r14 + r10
                int r14 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1))
                if (r14 > 0) goto L_0x00ec
                r14 = 0
                int r15 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
                if (r15 != 0) goto L_0x0080
                int r15 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
                if (r15 != 0) goto L_0x0080
                int r14 = (r7 > r14 ? 1 : (r7 == r14 ? 0 : -1))
                if (r14 != 0) goto L_0x0080
                goto L_0x00ec
            L_0x0080:
                r14 = 1128792064(0x43480000, float:200.0)
                float r14 = r14 + r12
                float r14 = r12 / r14
                float r15 = r1.mLastFilteredX     // Catch:{ all -> 0x03a2 }
                float r15 = r0 - r15
                float r15 = r15 * r14
                float r13 = r1.mLastFilteredX     // Catch:{ all -> 0x03a2 }
                float r0 = r15 + r13
                float r13 = r1.mLastFilteredY     // Catch:{ all -> 0x03a2 }
                float r13 = r5 - r13
                float r13 = r13 * r14
                float r15 = r1.mLastFilteredY     // Catch:{ all -> 0x03a2 }
                float r5 = r13 + r15
                float r13 = r1.mLastFilteredZ     // Catch:{ all -> 0x03a2 }
                float r13 = r7 - r13
                float r13 = r13 * r14
                float r15 = r1.mLastFilteredZ     // Catch:{ all -> 0x03a2 }
                float r7 = r13 + r15
                boolean r13 = com.android.server.policy.WindowOrientationListener.LOG     // Catch:{ all -> 0x03a2 }
                if (r13 == 0) goto L_0x00e6
                java.lang.String r13 = "WindowOrientationListener"
                java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ all -> 0x03a2 }
                r15.<init>()     // Catch:{ all -> 0x03a2 }
                java.lang.String r4 = "Filtered acceleration vector: x="
                r15.append(r4)     // Catch:{ all -> 0x03a2 }
                r15.append(r0)     // Catch:{ all -> 0x03a2 }
                java.lang.String r4 = ", y="
                r15.append(r4)     // Catch:{ all -> 0x03a2 }
                r15.append(r5)     // Catch:{ all -> 0x03a2 }
                java.lang.String r4 = ", z="
                r15.append(r4)     // Catch:{ all -> 0x03a2 }
                r15.append(r7)     // Catch:{ all -> 0x03a2 }
                java.lang.String r4 = ", magnitude="
                r15.append(r4)     // Catch:{ all -> 0x03a2 }
                float r4 = r0 * r0
                float r18 = r5 * r5
                float r4 = r4 + r18
                float r18 = r7 * r7
                float r4 = r4 + r18
                r19 = r7
                double r6 = (double) r4     // Catch:{ all -> 0x03a2 }
                double r6 = java.lang.Math.sqrt(r6)     // Catch:{ all -> 0x03a2 }
                r15.append(r6)     // Catch:{ all -> 0x03a2 }
                java.lang.String r4 = r15.toString()     // Catch:{ all -> 0x03a2 }
                android.util.Slog.v(r13, r4)     // Catch:{ all -> 0x03a2 }
                goto L_0x00e8
            L_0x00e6:
                r19 = r7
            L_0x00e8:
                r4 = 0
                r7 = r19
                goto L_0x00fe
            L_0x00ec:
                boolean r4 = com.android.server.policy.WindowOrientationListener.LOG     // Catch:{ all -> 0x03a2 }
                if (r4 == 0) goto L_0x00f9
                java.lang.String r4 = "WindowOrientationListener"
                java.lang.String r6 = "Resetting orientation listener."
                android.util.Slog.v(r4, r6)     // Catch:{ all -> 0x03a2 }
            L_0x00f9:
                r4 = 1
                r1.resetLocked(r4)     // Catch:{ all -> 0x03a2 }
                r4 = 1
            L_0x00fe:
                r1.mLastFilteredTimestampNanos = r8     // Catch:{ all -> 0x03a2 }
                r1.mLastFilteredX = r0     // Catch:{ all -> 0x03a2 }
                r1.mLastFilteredY = r5     // Catch:{ all -> 0x03a2 }
                r1.mLastFilteredZ = r7     // Catch:{ all -> 0x03a2 }
                r6 = 0
                r13 = 0
                r14 = 0
                if (r4 != 0) goto L_0x028e
                float r15 = r0 * r0
                float r19 = r5 * r5
                float r15 = r15 + r19
                float r19 = r7 * r7
                float r15 = r15 + r19
                r19 = r10
                double r10 = (double) r15     // Catch:{ all -> 0x03a2 }
                double r10 = java.lang.Math.sqrt(r10)     // Catch:{ all -> 0x03a2 }
                float r10 = (float) r10     // Catch:{ all -> 0x03a2 }
                r11 = 1065353216(0x3f800000, float:1.0)
                int r11 = (r10 > r11 ? 1 : (r10 == r11 ? 0 : -1))
                if (r11 >= 0) goto L_0x013b
                boolean r11 = com.android.server.policy.WindowOrientationListener.LOG     // Catch:{ all -> 0x03a2 }
                if (r11 == 0) goto L_0x0130
                java.lang.String r11 = "WindowOrientationListener"
                java.lang.String r15 = "Ignoring sensor data, magnitude too close to zero."
                android.util.Slog.v(r11, r15)     // Catch:{ all -> 0x03a2 }
            L_0x0130:
                r25.clearPredictedRotationLocked()     // Catch:{ all -> 0x03a2 }
                r18 = r0
                r22 = r4
                r0 = r5
                r15 = r7
                goto L_0x0296
            L_0x013b:
                boolean r11 = r1.isAcceleratingLocked(r10)     // Catch:{ all -> 0x03a2 }
                if (r11 == 0) goto L_0x0144
                r6 = 1
                r1.mAccelerationTimestampNanos = r8     // Catch:{ all -> 0x03a2 }
            L_0x0144:
                float r11 = r7 / r10
                r21 = r6
                r15 = r7
                double r6 = (double) r11     // Catch:{ all -> 0x03a2 }
                double r6 = java.lang.Math.asin(r6)     // Catch:{ all -> 0x03a2 }
                r22 = 4633260481505656832(0x404ca5dc20000000, double:57.295780181884766)
                double r6 = r6 * r22
                long r6 = java.lang.Math.round(r6)     // Catch:{ all -> 0x03a2 }
                int r6 = (int) r6     // Catch:{ all -> 0x03a2 }
                float r7 = (float) r6     // Catch:{ all -> 0x03a2 }
                r1.addTiltHistoryEntryLocked(r8, r7)     // Catch:{ all -> 0x03a2 }
                boolean r7 = r1.isFlatLocked(r8)     // Catch:{ all -> 0x03a2 }
                if (r7 == 0) goto L_0x0167
                r13 = 1
                r1.mFlatTimestampNanos = r8     // Catch:{ all -> 0x03a2 }
            L_0x0167:
                float r7 = (float) r6     // Catch:{ all -> 0x03a2 }
                boolean r7 = r1.isSwingingLocked(r8, r7)     // Catch:{ all -> 0x03a2 }
                if (r7 == 0) goto L_0x0171
                r14 = 1
                r1.mSwingTimestampNanos = r8     // Catch:{ all -> 0x03a2 }
            L_0x0171:
                r7 = -40
                if (r6 > r7) goto L_0x0179
                r7 = 1
                r1.mOverhead = r7     // Catch:{ all -> 0x03a2 }
                goto L_0x0180
            L_0x0179:
                r7 = -15
                if (r6 < r7) goto L_0x0180
                r7 = 0
                r1.mOverhead = r7     // Catch:{ all -> 0x03a2 }
            L_0x0180:
                boolean r7 = r1.mOverhead     // Catch:{ all -> 0x03a2 }
                if (r7 == 0) goto L_0x01ad
                boolean r7 = com.android.server.policy.WindowOrientationListener.LOG     // Catch:{ all -> 0x03a2 }
                if (r7 == 0) goto L_0x01a0
                java.lang.String r7 = "WindowOrientationListener"
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x03a2 }
                r11.<init>()     // Catch:{ all -> 0x03a2 }
                java.lang.String r2 = "Ignoring sensor data, device is overhead: tiltAngle="
                r11.append(r2)     // Catch:{ all -> 0x03a2 }
                r11.append(r6)     // Catch:{ all -> 0x03a2 }
                java.lang.String r2 = r11.toString()     // Catch:{ all -> 0x03a2 }
                android.util.Slog.v(r7, r2)     // Catch:{ all -> 0x03a2 }
            L_0x01a0:
                r25.clearPredictedRotationLocked()     // Catch:{ all -> 0x03a2 }
                r18 = r0
                r22 = r4
                r0 = r5
                r2 = r13
                r17 = r14
                goto L_0x0289
            L_0x01ad:
                int r2 = java.lang.Math.abs(r6)     // Catch:{ all -> 0x03a2 }
                r7 = 80
                if (r2 <= r7) goto L_0x01de
                boolean r2 = com.android.server.policy.WindowOrientationListener.LOG     // Catch:{ all -> 0x03a2 }
                if (r2 == 0) goto L_0x01d1
                java.lang.String r2 = "WindowOrientationListener"
                java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x03a2 }
                r7.<init>()     // Catch:{ all -> 0x03a2 }
                java.lang.String r11 = "Ignoring sensor data, tilt angle too high: tiltAngle="
                r7.append(r11)     // Catch:{ all -> 0x03a2 }
                r7.append(r6)     // Catch:{ all -> 0x03a2 }
                java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x03a2 }
                android.util.Slog.v(r2, r7)     // Catch:{ all -> 0x03a2 }
            L_0x01d1:
                r25.clearPredictedRotationLocked()     // Catch:{ all -> 0x03a2 }
                r18 = r0
                r22 = r4
                r0 = r5
                r2 = r13
                r17 = r14
                goto L_0x0289
            L_0x01de:
                float r2 = -r0
                r7 = r10
                double r10 = (double) r2     // Catch:{ all -> 0x03a2 }
                r2 = r13
                r17 = r14
                double r13 = (double) r5     // Catch:{ all -> 0x03a2 }
                double r10 = java.lang.Math.atan2(r10, r13)     // Catch:{ all -> 0x03a2 }
                double r10 = -r10
                double r10 = r10 * r22
                long r10 = java.lang.Math.round(r10)     // Catch:{ all -> 0x03a2 }
                int r10 = (int) r10     // Catch:{ all -> 0x03a2 }
                if (r10 >= 0) goto L_0x01f5
                int r10 = r10 + 360
            L_0x01f5:
                int r11 = r10 + 45
                int r11 = r11 / 90
                r13 = 4
                if (r11 != r13) goto L_0x01fd
                r11 = 0
            L_0x01fd:
                boolean r13 = r1.isTiltAngleAcceptableLocked(r11, r6)     // Catch:{ all -> 0x03a2 }
                if (r13 == 0) goto L_0x025d
                boolean r13 = r1.isOrientationAngleAcceptableLocked(r11, r10)     // Catch:{ all -> 0x03a2 }
                if (r13 == 0) goto L_0x0257
                r1.updatePredictedRotationLocked(r8, r11)     // Catch:{ all -> 0x03a2 }
                boolean r13 = com.android.server.policy.WindowOrientationListener.LOG     // Catch:{ all -> 0x03a2 }
                if (r13 == 0) goto L_0x0251
                java.lang.String r13 = "WindowOrientationListener"
                java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x03a2 }
                r14.<init>()     // Catch:{ all -> 0x03a2 }
                r18 = r0
                java.lang.String r0 = "Predicted: tiltAngle="
                r14.append(r0)     // Catch:{ all -> 0x03a2 }
                r14.append(r6)     // Catch:{ all -> 0x03a2 }
                java.lang.String r0 = ", orientationAngle="
                r14.append(r0)     // Catch:{ all -> 0x03a2 }
                r14.append(r10)     // Catch:{ all -> 0x03a2 }
                java.lang.String r0 = ", predictedRotation="
                r14.append(r0)     // Catch:{ all -> 0x03a2 }
                int r0 = r1.mPredictedRotation     // Catch:{ all -> 0x03a2 }
                r14.append(r0)     // Catch:{ all -> 0x03a2 }
                java.lang.String r0 = ", predictedRotationAgeMS="
                r14.append(r0)     // Catch:{ all -> 0x03a2 }
                r22 = r4
                r0 = r5
                long r4 = r1.mPredictedRotationTimestampNanos     // Catch:{ all -> 0x03a2 }
                long r4 = r8 - r4
                float r4 = (float) r4     // Catch:{ all -> 0x03a2 }
                r5 = 897988541(0x358637bd, float:1.0E-6)
                float r4 = r4 * r5
                r14.append(r4)     // Catch:{ all -> 0x03a2 }
                java.lang.String r4 = r14.toString()     // Catch:{ all -> 0x03a2 }
                android.util.Slog.v(r13, r4)     // Catch:{ all -> 0x03a2 }
                goto L_0x0289
            L_0x0251:
                r18 = r0
                r22 = r4
                r0 = r5
                goto L_0x0289
            L_0x0257:
                r18 = r0
                r22 = r4
                r0 = r5
                goto L_0x0262
            L_0x025d:
                r18 = r0
                r22 = r4
                r0 = r5
            L_0x0262:
                boolean r4 = com.android.server.policy.WindowOrientationListener.LOG     // Catch:{ all -> 0x03a2 }
                if (r4 == 0) goto L_0x0286
                java.lang.String r4 = "WindowOrientationListener"
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x03a2 }
                r5.<init>()     // Catch:{ all -> 0x03a2 }
                java.lang.String r13 = "Ignoring sensor data, no predicted rotation: tiltAngle="
                r5.append(r13)     // Catch:{ all -> 0x03a2 }
                r5.append(r6)     // Catch:{ all -> 0x03a2 }
                java.lang.String r13 = ", orientationAngle="
                r5.append(r13)     // Catch:{ all -> 0x03a2 }
                r5.append(r10)     // Catch:{ all -> 0x03a2 }
                java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x03a2 }
                android.util.Slog.v(r4, r5)     // Catch:{ all -> 0x03a2 }
            L_0x0286:
                r25.clearPredictedRotationLocked()     // Catch:{ all -> 0x03a2 }
            L_0x0289:
                r14 = r17
                r6 = r21
                goto L_0x0297
            L_0x028e:
                r18 = r0
                r22 = r4
                r0 = r5
                r15 = r7
                r19 = r10
            L_0x0296:
                r2 = r13
            L_0x0297:
                r1.mFlat = r2     // Catch:{ all -> 0x03a2 }
                r1.mSwinging = r14     // Catch:{ all -> 0x03a2 }
                r1.mAccelerating = r6     // Catch:{ all -> 0x03a2 }
                int r4 = r1.mProposedRotation     // Catch:{ all -> 0x03a2 }
                int r5 = r1.mPredictedRotation     // Catch:{ all -> 0x03a2 }
                if (r5 < 0) goto L_0x02a9
                boolean r5 = r1.isPredictedRotationAcceptableLocked(r8)     // Catch:{ all -> 0x03a2 }
                if (r5 == 0) goto L_0x02ad
            L_0x02a9:
                int r5 = r1.mPredictedRotation     // Catch:{ all -> 0x03a2 }
                r1.mProposedRotation = r5     // Catch:{ all -> 0x03a2 }
            L_0x02ad:
                int r5 = r1.mProposedRotation     // Catch:{ all -> 0x03a2 }
                boolean r7 = com.android.server.policy.WindowOrientationListener.LOG     // Catch:{ all -> 0x03a2 }
                if (r7 == 0) goto L_0x0372
                java.lang.String r7 = "WindowOrientationListener"
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x03a2 }
                r10.<init>()     // Catch:{ all -> 0x03a2 }
                java.lang.String r11 = "Result: currentRotation="
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                com.android.server.policy.WindowOrientationListener r11 = com.android.server.policy.WindowOrientationListener.this     // Catch:{ all -> 0x03a2 }
                int r11 = r11.mCurrentRotation     // Catch:{ all -> 0x03a2 }
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                java.lang.String r11 = ", proposedRotation="
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                r10.append(r5)     // Catch:{ all -> 0x03a2 }
                java.lang.String r11 = ", predictedRotation="
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                int r11 = r1.mPredictedRotation     // Catch:{ all -> 0x03a2 }
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                java.lang.String r11 = ", timeDeltaMS="
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                r10.append(r12)     // Catch:{ all -> 0x03a2 }
                java.lang.String r11 = ", isAccelerating="
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                r10.append(r6)     // Catch:{ all -> 0x03a2 }
                java.lang.String r11 = ", isFlat="
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                r10.append(r2)     // Catch:{ all -> 0x03a2 }
                java.lang.String r11 = ", isSwinging="
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                r10.append(r14)     // Catch:{ all -> 0x03a2 }
                java.lang.String r11 = ", isOverhead="
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                boolean r11 = r1.mOverhead     // Catch:{ all -> 0x03a2 }
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                java.lang.String r11 = ", isTouched="
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                boolean r11 = r1.mTouched     // Catch:{ all -> 0x03a2 }
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                java.lang.String r11 = ", timeUntilSettledMS="
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                r13 = r12
                long r11 = r1.mPredictedRotationTimestampNanos     // Catch:{ all -> 0x03a2 }
                r16 = 40000000(0x2625a00, double:1.9762626E-316)
                long r11 = r11 + r16
                float r11 = r1.remainingMS(r8, r11)     // Catch:{ all -> 0x03a2 }
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                java.lang.String r11 = ", timeUntilAccelerationDelayExpiredMS="
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                long r11 = r1.mAccelerationTimestampNanos     // Catch:{ all -> 0x03a2 }
                r16 = 500000000(0x1dcd6500, double:2.47032823E-315)
                long r11 = r11 + r16
                float r11 = r1.remainingMS(r8, r11)     // Catch:{ all -> 0x03a2 }
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                java.lang.String r11 = ", timeUntilFlatDelayExpiredMS="
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                long r11 = r1.mFlatTimestampNanos     // Catch:{ all -> 0x03a2 }
                long r11 = r11 + r16
                float r11 = r1.remainingMS(r8, r11)     // Catch:{ all -> 0x03a2 }
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                java.lang.String r11 = ", timeUntilSwingDelayExpiredMS="
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                long r11 = r1.mSwingTimestampNanos     // Catch:{ all -> 0x03a2 }
                r23 = 300000000(0x11e1a300, double:1.482196938E-315)
                long r11 = r11 + r23
                float r11 = r1.remainingMS(r8, r11)     // Catch:{ all -> 0x03a2 }
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                java.lang.String r11 = ", timeUntilTouchDelayExpiredMS="
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                long r11 = r1.mTouchEndedTimestampNanos     // Catch:{ all -> 0x03a2 }
                long r11 = r11 + r16
                float r11 = r1.remainingMS(r8, r11)     // Catch:{ all -> 0x03a2 }
                r10.append(r11)     // Catch:{ all -> 0x03a2 }
                java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x03a2 }
                android.util.Slog.v(r7, r10)     // Catch:{ all -> 0x03a2 }
                goto L_0x0373
            L_0x0372:
                r13 = r12
            L_0x0373:
                monitor-exit(r3)     // Catch:{ all -> 0x03a2 }
                if (r5 == r4) goto L_0x03a1
                if (r5 < 0) goto L_0x03a1
                boolean r0 = com.android.server.policy.WindowOrientationListener.LOG
                if (r0 == 0) goto L_0x039c
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r2 = "Proposed rotation changed!  proposedRotation="
                r0.append(r2)
                r0.append(r5)
                java.lang.String r2 = ", oldProposedRotation="
                r0.append(r2)
                r0.append(r4)
                java.lang.String r0 = r0.toString()
                java.lang.String r2 = "WindowOrientationListener"
                android.util.Slog.v(r2, r0)
            L_0x039c:
                com.android.server.policy.WindowOrientationListener r0 = com.android.server.policy.WindowOrientationListener.this
                r0.onProposedRotationChanged(r5)
            L_0x03a1:
                return
            L_0x03a2:
                r0 = move-exception
                monitor-exit(r3)     // Catch:{ all -> 0x03a2 }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.policy.WindowOrientationListener.AccelSensorJudge.onSensorChanged(android.hardware.SensorEvent):void");
        }

        public void onTouchStartLocked() {
            this.mTouched = true;
        }

        public void onTouchEndLocked(long whenElapsedNanos) {
            this.mTouched = false;
            this.mTouchEndedTimestampNanos = whenElapsedNanos;
        }

        public void resetLocked(boolean clearCurrentRotation) {
            this.mLastFilteredTimestampNanos = Long.MIN_VALUE;
            if (clearCurrentRotation) {
                this.mProposedRotation = -1;
            }
            this.mFlatTimestampNanos = Long.MIN_VALUE;
            this.mFlat = false;
            this.mSwingTimestampNanos = Long.MIN_VALUE;
            this.mSwinging = false;
            this.mAccelerationTimestampNanos = Long.MIN_VALUE;
            this.mAccelerating = false;
            this.mOverhead = false;
            clearPredictedRotationLocked();
            clearTiltHistoryLocked();
        }

        private boolean isTiltAngleAcceptableLocked(int rotation, int tiltAngle) {
            int[][] iArr = this.mTiltToleranceConfig;
            return tiltAngle >= iArr[rotation][0] && tiltAngle <= iArr[rotation][1];
        }

        private boolean isOrientationAngleAcceptableLocked(int rotation, int orientationAngle) {
            int currentRotation = WindowOrientationListener.this.mCurrentRotation;
            if (currentRotation < 0) {
                return true;
            }
            if (rotation == currentRotation || rotation == (currentRotation + 1) % 4) {
                int lowerBound = ((rotation * 90) - 45) + 22;
                if (rotation == 0) {
                    if (orientationAngle >= 315 && orientationAngle < lowerBound + 360) {
                        return false;
                    }
                } else if (orientationAngle < lowerBound) {
                    return false;
                }
            }
            if (rotation != currentRotation && rotation != (currentRotation + 3) % 4) {
                return true;
            }
            int upperBound = ((rotation * 90) + 45) - 22;
            if (rotation == 0) {
                if (orientationAngle > 45 || orientationAngle <= upperBound) {
                    return true;
                }
                return false;
            } else if (orientationAngle > upperBound) {
                return false;
            } else {
                return true;
            }
        }

        private boolean isPredictedRotationAcceptableLocked(long now) {
            if (now >= this.mPredictedRotationTimestampNanos + PROPOSAL_SETTLE_TIME_NANOS && now >= this.mFlatTimestampNanos + 500000000 && now >= this.mSwingTimestampNanos + 300000000 && now >= this.mAccelerationTimestampNanos + 500000000 && !this.mTouched && now >= this.mTouchEndedTimestampNanos + 500000000) {
                return true;
            }
            return false;
        }

        private void clearPredictedRotationLocked() {
            this.mPredictedRotation = -1;
            this.mPredictedRotationTimestampNanos = Long.MIN_VALUE;
        }

        private void updatePredictedRotationLocked(long now, int rotation) {
            if (this.mPredictedRotation != rotation) {
                this.mPredictedRotation = rotation;
                this.mPredictedRotationTimestampNanos = now;
            }
        }

        private boolean isAcceleratingLocked(float magnitude) {
            return magnitude < MIN_ACCELERATION_MAGNITUDE || magnitude > MAX_ACCELERATION_MAGNITUDE;
        }

        private void clearTiltHistoryLocked() {
            this.mTiltHistoryTimestampNanos[0] = Long.MIN_VALUE;
            this.mTiltHistoryIndex = 1;
        }

        private void addTiltHistoryEntryLocked(long now, float tilt) {
            float[] fArr = this.mTiltHistory;
            int i = this.mTiltHistoryIndex;
            fArr[i] = tilt;
            long[] jArr = this.mTiltHistoryTimestampNanos;
            jArr[i] = now;
            this.mTiltHistoryIndex = (i + 1) % 200;
            jArr[this.mTiltHistoryIndex] = Long.MIN_VALUE;
        }

        private boolean isFlatLocked(long now) {
            int i = this.mTiltHistoryIndex;
            do {
                int nextTiltHistoryIndexLocked = nextTiltHistoryIndexLocked(i);
                i = nextTiltHistoryIndexLocked;
                if (nextTiltHistoryIndexLocked < 0 || this.mTiltHistory[i] < FLAT_ANGLE) {
                    return false;
                }
            } while (this.mTiltHistoryTimestampNanos[i] + 1000000000 > now);
            return true;
        }

        private boolean isSwingingLocked(long now, float tilt) {
            int i = this.mTiltHistoryIndex;
            do {
                int nextTiltHistoryIndexLocked = nextTiltHistoryIndexLocked(i);
                i = nextTiltHistoryIndexLocked;
                if (nextTiltHistoryIndexLocked < 0 || this.mTiltHistoryTimestampNanos[i] + 300000000 < now) {
                    return false;
                }
            } while (this.mTiltHistory[i] + SWING_AWAY_ANGLE_DELTA > tilt);
            return true;
        }

        private int nextTiltHistoryIndexLocked(int index) {
            int index2 = (index == 0 ? 200 : index) - 1;
            if (this.mTiltHistoryTimestampNanos[index2] != Long.MIN_VALUE) {
                return index2;
            }
            return -1;
        }

        private float getLastTiltLocked() {
            int index = nextTiltHistoryIndexLocked(this.mTiltHistoryIndex);
            if (index >= 0) {
                return this.mTiltHistory[index];
            }
            return Float.NaN;
        }

        private float remainingMS(long now, long until) {
            if (now >= until) {
                return 0.0f;
            }
            return ((float) (until - now)) * 1.0E-6f;
        }
    }

    final class OrientationSensorJudge extends OrientationJudge {
        private int mDesiredRotation = -1;
        private int mProposedRotation = -1;
        /* access modifiers changed from: private */
        public boolean mRotationEvaluationScheduled;
        private Runnable mRotationEvaluator = new Runnable() {
            public void run() {
                int newRotation;
                synchronized (WindowOrientationListener.this.mLock) {
                    boolean unused = OrientationSensorJudge.this.mRotationEvaluationScheduled = false;
                    newRotation = OrientationSensorJudge.this.evaluateRotationChangeLocked();
                }
                if (newRotation >= 0) {
                    WindowOrientationListener.this.onProposedRotationChanged(newRotation);
                }
            }
        };
        private long mTouchEndedTimestampNanos = Long.MIN_VALUE;
        private boolean mTouching;

        OrientationSensorJudge() {
            super();
        }

        public int getProposedRotationLocked() {
            return this.mProposedRotation;
        }

        public void onTouchStartLocked() {
            this.mTouching = true;
        }

        public void onTouchEndLocked(long whenElapsedNanos) {
            this.mTouching = false;
            this.mTouchEndedTimestampNanos = whenElapsedNanos;
            if (this.mDesiredRotation != this.mProposedRotation) {
                scheduleRotationEvaluationIfNecessaryLocked(SystemClock.elapsedRealtimeNanos());
            }
        }

        public void onSensorChanged(SensorEvent event) {
            int newRotation;
            synchronized (WindowOrientationListener.this.mLock) {
                this.mDesiredRotation = (int) event.values[0];
                newRotation = evaluateRotationChangeLocked();
            }
            if (newRotation >= 0) {
                WindowOrientationListener.this.onProposedRotationChanged(newRotation);
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void dumpLocked(PrintWriter pw, String prefix) {
            pw.println(prefix + "OrientationSensorJudge");
            String prefix2 = prefix + "  ";
            pw.println(prefix2 + "mDesiredRotation=" + Surface.rotationToString(this.mDesiredRotation));
            pw.println(prefix2 + "mProposedRotation=" + Surface.rotationToString(this.mProposedRotation));
            pw.println(prefix2 + "mTouching=" + this.mTouching);
            pw.println(prefix2 + "mTouchEndedTimestampNanos=" + this.mTouchEndedTimestampNanos);
        }

        public void resetLocked(boolean clearCurrentRotation) {
            if (clearCurrentRotation) {
                this.mProposedRotation = -1;
                this.mDesiredRotation = -1;
            }
            this.mTouching = false;
            this.mTouchEndedTimestampNanos = Long.MIN_VALUE;
            unscheduleRotationEvaluationLocked();
        }

        public int evaluateRotationChangeLocked() {
            unscheduleRotationEvaluationLocked();
            if (this.mDesiredRotation == this.mProposedRotation) {
                return -1;
            }
            long now = SystemClock.elapsedRealtimeNanos();
            if (isDesiredRotationAcceptableLocked(now)) {
                this.mProposedRotation = this.mDesiredRotation;
                return this.mProposedRotation;
            }
            scheduleRotationEvaluationIfNecessaryLocked(now);
            return -1;
        }

        private boolean isDesiredRotationAcceptableLocked(long now) {
            if (!this.mTouching && now >= this.mTouchEndedTimestampNanos + 500000000) {
                return true;
            }
            return false;
        }

        private void scheduleRotationEvaluationIfNecessaryLocked(long now) {
            if (this.mRotationEvaluationScheduled || this.mDesiredRotation == this.mProposedRotation) {
                if (WindowOrientationListener.LOG) {
                    Slog.d(WindowOrientationListener.TAG, "scheduleRotationEvaluationLocked: ignoring, an evaluation is already scheduled or is unnecessary.");
                }
            } else if (!this.mTouching) {
                long timeOfNextPossibleRotationNanos = this.mTouchEndedTimestampNanos + 500000000;
                if (now < timeOfNextPossibleRotationNanos) {
                    WindowOrientationListener.this.mHandler.postDelayed(this.mRotationEvaluator, (long) Math.ceil((double) (((float) (timeOfNextPossibleRotationNanos - now)) * 1.0E-6f)));
                    this.mRotationEvaluationScheduled = true;
                } else if (WindowOrientationListener.LOG) {
                    Slog.d(WindowOrientationListener.TAG, "scheduleRotationEvaluationLocked: ignoring, already past the next possible time of rotation.");
                }
            } else if (WindowOrientationListener.LOG) {
                Slog.d(WindowOrientationListener.TAG, "scheduleRotationEvaluationLocked: ignoring, user is still touching the screen.");
            }
        }

        private void unscheduleRotationEvaluationLocked() {
            if (this.mRotationEvaluationScheduled) {
                WindowOrientationListener.this.mHandler.removeCallbacks(this.mRotationEvaluator);
                this.mRotationEvaluationScheduled = false;
            }
        }
    }
}
