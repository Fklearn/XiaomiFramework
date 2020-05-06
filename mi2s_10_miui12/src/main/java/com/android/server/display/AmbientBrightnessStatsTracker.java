package com.android.server.display;

import android.hardware.display.AmbientBrightnessDayStats;
import android.os.SystemClock;
import android.os.UserManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FastXmlSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import org.xmlpull.v1.XmlSerializer;

public class AmbientBrightnessStatsTracker {
    @VisibleForTesting
    static final float[] BUCKET_BOUNDARIES_FOR_NEW_STATS = {0.0f, 0.1f, 0.3f, 1.0f, 3.0f, 10.0f, 30.0f, 100.0f, 300.0f, 1000.0f, 3000.0f, 10000.0f};
    private static final boolean DEBUG = false;
    @VisibleForTesting
    static final int MAX_DAYS_TO_TRACK = 7;
    private static final String TAG = "AmbientBrightnessStatsTracker";
    private final AmbientBrightnessStats mAmbientBrightnessStats;
    private float mCurrentAmbientBrightness;
    private int mCurrentUserId;
    /* access modifiers changed from: private */
    public final Injector mInjector;
    private final Timer mTimer;
    /* access modifiers changed from: private */
    public final UserManager mUserManager;

    @VisibleForTesting
    interface Clock {
        long elapsedTimeMillis();
    }

    public AmbientBrightnessStatsTracker(UserManager userManager, Injector injector) {
        this.mUserManager = userManager;
        if (injector != null) {
            this.mInjector = injector;
        } else {
            this.mInjector = new Injector();
        }
        this.mAmbientBrightnessStats = new AmbientBrightnessStats();
        this.mTimer = new Timer(new Clock() {
            public final long elapsedTimeMillis() {
                return AmbientBrightnessStatsTracker.this.lambda$new$0$AmbientBrightnessStatsTracker();
            }
        });
        this.mCurrentAmbientBrightness = -1.0f;
    }

    public /* synthetic */ long lambda$new$0$AmbientBrightnessStatsTracker() {
        return this.mInjector.elapsedRealtimeMillis();
    }

    public synchronized void start() {
        this.mTimer.reset();
        this.mTimer.start();
    }

    public synchronized void stop() {
        if (this.mTimer.isRunning()) {
            this.mAmbientBrightnessStats.log(this.mCurrentUserId, this.mInjector.getLocalDate(), this.mCurrentAmbientBrightness, this.mTimer.totalDurationSec());
        }
        this.mTimer.reset();
        this.mCurrentAmbientBrightness = -1.0f;
    }

    public synchronized void add(int userId, float newAmbientBrightness) {
        if (this.mTimer.isRunning()) {
            if (userId == this.mCurrentUserId) {
                this.mAmbientBrightnessStats.log(this.mCurrentUserId, this.mInjector.getLocalDate(), this.mCurrentAmbientBrightness, this.mTimer.totalDurationSec());
            } else {
                this.mCurrentUserId = userId;
            }
            this.mTimer.reset();
            this.mTimer.start();
            this.mCurrentAmbientBrightness = newAmbientBrightness;
        }
    }

    public synchronized void writeStats(OutputStream stream) throws IOException {
        this.mAmbientBrightnessStats.writeToXML(stream);
    }

    public synchronized void readStats(InputStream stream) throws IOException {
        this.mAmbientBrightnessStats.readFromXML(stream);
    }

    public synchronized ArrayList<AmbientBrightnessDayStats> getUserStats(int userId) {
        return this.mAmbientBrightnessStats.getUserStats(userId);
    }

    public synchronized void dump(PrintWriter pw) {
        pw.println("AmbientBrightnessStats:");
        pw.print(this.mAmbientBrightnessStats);
    }

    class AmbientBrightnessStats {
        private static final String ATTR_BUCKET_BOUNDARIES = "bucket-boundaries";
        private static final String ATTR_BUCKET_STATS = "bucket-stats";
        private static final String ATTR_LOCAL_DATE = "local-date";
        private static final String ATTR_USER = "user";
        private static final String TAG_AMBIENT_BRIGHTNESS_DAY_STATS = "ambient-brightness-day-stats";
        private static final String TAG_AMBIENT_BRIGHTNESS_STATS = "ambient-brightness-stats";
        private Map<Integer, Deque<AmbientBrightnessDayStats>> mStats = new HashMap();

        public AmbientBrightnessStats() {
        }

        public void log(int userId, LocalDate localDate, float ambientBrightness, float durationSec) {
            getOrCreateDayStats(getOrCreateUserStats(this.mStats, userId), localDate).log(ambientBrightness, durationSec);
        }

        public ArrayList<AmbientBrightnessDayStats> getUserStats(int userId) {
            if (this.mStats.containsKey(Integer.valueOf(userId))) {
                return new ArrayList<>(this.mStats.get(Integer.valueOf(userId)));
            }
            return null;
        }

        public void writeToXML(OutputStream stream) throws IOException {
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(stream, StandardCharsets.UTF_8.name());
            out.startDocument((String) null, true);
            out.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            LocalDate cutOffDate = AmbientBrightnessStatsTracker.this.mInjector.getLocalDate().minusDays(7);
            out.startTag((String) null, TAG_AMBIENT_BRIGHTNESS_STATS);
            for (Map.Entry<Integer, Deque<AmbientBrightnessDayStats>> entry : this.mStats.entrySet()) {
                for (AmbientBrightnessDayStats userDayStats : entry.getValue()) {
                    int userSerialNumber = AmbientBrightnessStatsTracker.this.mInjector.getUserSerialNumber(AmbientBrightnessStatsTracker.this.mUserManager, entry.getKey().intValue());
                    if (userSerialNumber != -1 && userDayStats.getLocalDate().isAfter(cutOffDate)) {
                        out.startTag((String) null, TAG_AMBIENT_BRIGHTNESS_DAY_STATS);
                        out.attribute((String) null, ATTR_USER, Integer.toString(userSerialNumber));
                        out.attribute((String) null, ATTR_LOCAL_DATE, userDayStats.getLocalDate().toString());
                        StringBuilder bucketBoundariesValues = new StringBuilder();
                        StringBuilder timeSpentValues = new StringBuilder();
                        for (int i = 0; i < userDayStats.getBucketBoundaries().length; i++) {
                            if (i > 0) {
                                bucketBoundariesValues.append(",");
                                timeSpentValues.append(",");
                            }
                            bucketBoundariesValues.append(userDayStats.getBucketBoundaries()[i]);
                            timeSpentValues.append(userDayStats.getStats()[i]);
                        }
                        out.attribute((String) null, ATTR_BUCKET_BOUNDARIES, bucketBoundariesValues.toString());
                        out.attribute((String) null, ATTR_BUCKET_STATS, timeSpentValues.toString());
                        out.endTag((String) null, TAG_AMBIENT_BRIGHTNESS_DAY_STATS);
                    }
                }
            }
            out.endTag((String) null, TAG_AMBIENT_BRIGHTNESS_STATS);
            out.endDocument();
            stream.flush();
        }

        /* Debug info: failed to restart local var, previous not found, register: 18 */
        /* JADX WARNING: Code restructure failed: missing block: B:48:0x010e, code lost:
            r1.mStats = r2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:49:0x0111, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void readFromXML(java.io.InputStream r19) throws java.io.IOException {
            /*
                r18 = this;
                r1 = r18
                java.lang.String r0 = ","
                java.util.HashMap r2 = new java.util.HashMap     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012d }
                r2.<init>()     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012d }
                org.xmlpull.v1.XmlPullParser r3 = android.util.Xml.newPullParser()     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012d }
                java.nio.charset.Charset r4 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012d }
                java.lang.String r4 = r4.name()     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012d }
                r5 = r19
                r3.setInput(r5, r4)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
            L_0x0018:
                int r4 = r3.next()     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                r6 = r4
                r7 = 1
                if (r4 == r7) goto L_0x0024
                r4 = 2
                if (r6 == r4) goto L_0x0024
                goto L_0x0018
            L_0x0024:
                java.lang.String r4 = r3.getName()     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                java.lang.String r8 = "ambient-brightness-stats"
                boolean r8 = r8.equals(r4)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                if (r8 == 0) goto L_0x0112
                com.android.server.display.AmbientBrightnessStatsTracker r8 = com.android.server.display.AmbientBrightnessStatsTracker.this     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                com.android.server.display.AmbientBrightnessStatsTracker$Injector r8 = r8.mInjector     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                java.time.LocalDate r8 = r8.getLocalDate()     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                r9 = 7
                java.time.LocalDate r8 = r8.minusDays(r9)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                r3.next()     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                int r9 = r3.getDepth()     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
            L_0x0047:
                int r10 = r3.next()     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                r6 = r10
                if (r10 == r7) goto L_0x010c
                r10 = 3
                if (r6 != r10) goto L_0x005c
                int r11 = r3.getDepth()     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                if (r11 <= r9) goto L_0x0058
                goto L_0x005c
            L_0x0058:
                r17 = r3
                goto L_0x010e
            L_0x005c:
                if (r6 == r10) goto L_0x0101
                r10 = 4
                if (r6 != r10) goto L_0x0067
                r16 = r0
                r17 = r3
                goto L_0x0105
            L_0x0067:
                java.lang.String r10 = r3.getName()     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                r4 = r10
                java.lang.String r10 = "ambient-brightness-day-stats"
                boolean r10 = r10.equals(r4)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                if (r10 == 0) goto L_0x00fc
                java.lang.String r10 = "user"
                r11 = 0
                java.lang.String r10 = r3.getAttributeValue(r11, r10)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                java.lang.String r12 = "local-date"
                java.lang.String r12 = r3.getAttributeValue(r11, r12)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                java.time.LocalDate r12 = java.time.LocalDate.parse(r12)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                java.lang.String r13 = "bucket-boundaries"
                java.lang.String r13 = r3.getAttributeValue(r11, r13)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                java.lang.String[] r13 = r13.split(r0)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                java.lang.String r14 = "bucket-stats"
                java.lang.String r11 = r3.getAttributeValue(r11, r14)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                java.lang.String[] r11 = r11.split(r0)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                int r14 = r13.length     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                int r15 = r11.length     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                if (r14 != r15) goto L_0x00f2
                int r14 = r13.length     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                if (r14 < r7) goto L_0x00f2
                int r14 = r13.length     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                float[] r14 = new float[r14]     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                int r15 = r11.length     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                float[] r15 = new float[r15]     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                r16 = 0
                r7 = r16
            L_0x00ac:
                r16 = r0
                int r0 = r13.length     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                if (r7 >= r0) goto L_0x00c6
                r0 = r13[r7]     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                float r0 = java.lang.Float.parseFloat(r0)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                r14[r7] = r0     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                r0 = r11[r7]     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                float r0 = java.lang.Float.parseFloat(r0)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                r15[r7] = r0     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                int r7 = r7 + 1
                r0 = r16
                goto L_0x00ac
            L_0x00c6:
                com.android.server.display.AmbientBrightnessStatsTracker r0 = com.android.server.display.AmbientBrightnessStatsTracker.this     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                com.android.server.display.AmbientBrightnessStatsTracker$Injector r0 = r0.mInjector     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                com.android.server.display.AmbientBrightnessStatsTracker r7 = com.android.server.display.AmbientBrightnessStatsTracker.this     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                android.os.UserManager r7 = r7.mUserManager     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                r17 = r3
                int r3 = java.lang.Integer.parseInt(r10)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                int r0 = r0.getUserId(r7, r3)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                r3 = -1
                if (r0 == r3) goto L_0x00f1
                boolean r3 = r12.isAfter(r8)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                if (r3 == 0) goto L_0x00f1
                java.util.Deque r3 = r1.getOrCreateUserStats(r2, r0)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                android.hardware.display.AmbientBrightnessDayStats r7 = new android.hardware.display.AmbientBrightnessDayStats     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                r7.<init>(r12, r14, r15)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                r3.offer(r7)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
            L_0x00f1:
                goto L_0x0105
            L_0x00f2:
                r17 = r3
                java.io.IOException r0 = new java.io.IOException     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                java.lang.String r3 = "Invalid brightness stats string."
                r0.<init>(r3)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                throw r0     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
            L_0x00fc:
                r16 = r0
                r17 = r3
                goto L_0x0105
            L_0x0101:
                r16 = r0
                r17 = r3
            L_0x0105:
                r0 = r16
                r3 = r17
                r7 = 1
                goto L_0x0047
            L_0x010c:
                r17 = r3
            L_0x010e:
                r1.mStats = r2     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                return
            L_0x0112:
                r17 = r3
                org.xmlpull.v1.XmlPullParserException r0 = new org.xmlpull.v1.XmlPullParserException     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                r3.<init>()     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                java.lang.String r7 = "Ambient brightness stats not found in tracker file "
                r3.append(r7)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                r3.append(r4)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                java.lang.String r3 = r3.toString()     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                r0.<init>(r3)     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
                throw r0     // Catch:{ IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException -> 0x012b }
            L_0x012b:
                r0 = move-exception
                goto L_0x0130
            L_0x012d:
                r0 = move-exception
                r5 = r19
            L_0x0130:
                java.io.IOException r2 = new java.io.IOException
                java.lang.String r3 = "Failed to parse brightness stats file."
                r2.<init>(r3, r0)
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.AmbientBrightnessStatsTracker.AmbientBrightnessStats.readFromXML(java.io.InputStream):void");
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<Integer, Deque<AmbientBrightnessDayStats>> entry : this.mStats.entrySet()) {
                for (AmbientBrightnessDayStats dayStats : entry.getValue()) {
                    builder.append("  ");
                    builder.append(entry.getKey());
                    builder.append(" ");
                    builder.append(dayStats);
                    builder.append("\n");
                }
            }
            return builder.toString();
        }

        private Deque<AmbientBrightnessDayStats> getOrCreateUserStats(Map<Integer, Deque<AmbientBrightnessDayStats>> stats, int userId) {
            if (!stats.containsKey(Integer.valueOf(userId))) {
                stats.put(Integer.valueOf(userId), new ArrayDeque());
            }
            return stats.get(Integer.valueOf(userId));
        }

        private AmbientBrightnessDayStats getOrCreateDayStats(Deque<AmbientBrightnessDayStats> userStats, LocalDate localDate) {
            AmbientBrightnessDayStats lastBrightnessStats = userStats.peekLast();
            if (lastBrightnessStats != null && lastBrightnessStats.getLocalDate().equals(localDate)) {
                return lastBrightnessStats;
            }
            AmbientBrightnessDayStats dayStats = new AmbientBrightnessDayStats(localDate, AmbientBrightnessStatsTracker.BUCKET_BOUNDARIES_FOR_NEW_STATS);
            if (userStats.size() == 7) {
                userStats.poll();
            }
            userStats.offer(dayStats);
            return dayStats;
        }
    }

    @VisibleForTesting
    static class Timer {
        private final Clock clock;
        private long startTimeMillis;
        private boolean started;

        public Timer(Clock clock2) {
            this.clock = clock2;
        }

        public void reset() {
            this.started = false;
        }

        public void start() {
            if (!this.started) {
                this.startTimeMillis = this.clock.elapsedTimeMillis();
                this.started = true;
            }
        }

        public boolean isRunning() {
            return this.started;
        }

        public float totalDurationSec() {
            if (this.started) {
                return (float) (((double) (this.clock.elapsedTimeMillis() - this.startTimeMillis)) / 1000.0d);
            }
            return 0.0f;
        }
    }

    @VisibleForTesting
    static class Injector {
        Injector() {
        }

        public long elapsedRealtimeMillis() {
            return SystemClock.elapsedRealtime();
        }

        public int getUserSerialNumber(UserManager userManager, int userId) {
            return userManager.getUserSerialNumber(userId);
        }

        public int getUserId(UserManager userManager, int userSerialNumber) {
            return userManager.getUserHandle(userSerialNumber);
        }

        public LocalDate getLocalDate() {
            return LocalDate.now();
        }
    }
}
