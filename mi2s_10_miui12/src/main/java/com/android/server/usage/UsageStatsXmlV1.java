package com.android.server.usage;

import android.app.usage.ConfigurationStats;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.content.res.Configuration;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.util.XmlUtils;
import com.android.server.am.AssistDataRequester;
import com.android.server.pm.Settings;
import com.android.server.usage.IntervalStats;
import java.io.IOException;
import java.net.ProtocolException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

final class UsageStatsXmlV1 {
    private static final String ACTIVE_ATTR = "active";
    private static final String APP_LAUNCH_COUNT_ATTR = "appLaunchCount";
    private static final String CATEGORY_TAG = "category";
    private static final String CHOOSER_COUNT_TAG = "chosen_action";
    private static final String CLASS_ATTR = "class";
    private static final String CONFIGURATIONS_TAG = "configurations";
    private static final String CONFIG_TAG = "config";
    private static final String COUNT = "count";
    private static final String COUNT_ATTR = "count";
    private static final String END_TIME_ATTR = "endTime";
    private static final String EVENT_LOG_TAG = "event-log";
    private static final String EVENT_TAG = "event";
    private static final String FLAGS_ATTR = "flags";
    private static final String INSTANCE_ID_ATTR = "instanceId";
    private static final String INTERACTIVE_TAG = "interactive";
    private static final String KEYGUARD_HIDDEN_TAG = "keyguard-hidden";
    private static final String KEYGUARD_SHOWN_TAG = "keyguard-shown";
    private static final String LAST_EVENT_ATTR = "lastEvent";
    private static final String LAST_TIME_ACTIVE_ATTR = "lastTimeActive";
    private static final String LAST_TIME_SERVICE_USED_ATTR = "lastTimeServiceUsed";
    private static final String LAST_TIME_VISIBLE_ATTR = "lastTimeVisible";
    private static final String MAJOR_VERSION_ATTR = "majorVersion";
    private static final String MINOR_VERSION_ATTR = "minorVersion";
    private static final String NAME = "name";
    private static final String NON_INTERACTIVE_TAG = "non-interactive";
    private static final String NOTIFICATION_CHANNEL_ATTR = "notificationChannel";
    private static final String PACKAGES_TAG = "packages";
    private static final String PACKAGE_ATTR = "package";
    private static final String PACKAGE_TAG = "package";
    private static final String SHORTCUT_ID_ATTR = "shortcutId";
    private static final String STANDBY_BUCKET_ATTR = "standbyBucket";
    private static final String TAG = "UsageStatsXmlV1";
    private static final String TIME_ATTR = "time";
    private static final String TOTAL_TIME_ACTIVE_ATTR = "timeActive";
    private static final String TOTAL_TIME_SERVICE_USED_ATTR = "timeServiceUsed";
    private static final String TOTAL_TIME_VISIBLE_ATTR = "timeVisible";
    private static final String TYPE_ATTR = "type";

    private static void loadUsageStats(XmlPullParser parser, IntervalStats statsOut) throws XmlPullParserException, IOException {
        String pkg = parser.getAttributeValue((String) null, Settings.ATTR_PACKAGE);
        if (pkg != null) {
            UsageStats stats = statsOut.getOrCreateUsageStats(pkg);
            stats.mLastTimeUsed = statsOut.beginTime + XmlUtils.readLongAttribute(parser, LAST_TIME_ACTIVE_ATTR);
            if (stats.mLastTimeUsed < statsOut.beginTime) {
                stats.mLastTimeUsed = 0;
            }
            try {
                stats.mLastTimeVisible = statsOut.beginTime + XmlUtils.readLongAttribute(parser, LAST_TIME_VISIBLE_ATTR);
            } catch (IOException e) {
                Log.i(TAG, "Failed to parse mLastTimeVisible");
            }
            try {
                stats.mLastTimeForegroundServiceUsed = statsOut.beginTime + XmlUtils.readLongAttribute(parser, LAST_TIME_SERVICE_USED_ATTR);
            } catch (IOException e2) {
                Log.i(TAG, "Failed to parse mLastTimeForegroundServiceUsed");
            }
            stats.mTotalTimeInForeground = XmlUtils.readLongAttribute(parser, TOTAL_TIME_ACTIVE_ATTR);
            try {
                stats.mTotalTimeVisible = XmlUtils.readLongAttribute(parser, TOTAL_TIME_VISIBLE_ATTR);
            } catch (IOException e3) {
                Log.i(TAG, "Failed to parse mTotalTimeVisible");
            }
            try {
                stats.mTotalTimeForegroundServiceUsed = XmlUtils.readLongAttribute(parser, TOTAL_TIME_SERVICE_USED_ATTR);
            } catch (IOException e4) {
                Log.i(TAG, "Failed to parse mTotalTimeForegroundServiceUsed");
            }
            stats.mLastEvent = XmlUtils.readIntAttribute(parser, LAST_EVENT_ATTR);
            stats.mAppLaunchCount = XmlUtils.readIntAttribute(parser, APP_LAUNCH_COUNT_ATTR, 0);
            while (true) {
                int next = parser.next();
                int eventCode = next;
                if (next != 1) {
                    String tag = parser.getName();
                    if (eventCode == 3 && tag.equals(Settings.ATTR_PACKAGE)) {
                        return;
                    }
                    if (eventCode == 2 && tag.equals(CHOOSER_COUNT_TAG)) {
                        loadChooserCounts(parser, stats, XmlUtils.readStringAttribute(parser, "name"));
                    }
                } else {
                    return;
                }
            }
        } else {
            throw new ProtocolException("no package attribute present");
        }
    }

    private static void loadCountAndTime(XmlPullParser parser, IntervalStats.EventTracker tracker) throws IOException, XmlPullParserException {
        tracker.count = XmlUtils.readIntAttribute(parser, AssistDataRequester.KEY_RECEIVER_EXTRA_COUNT, 0);
        tracker.duration = XmlUtils.readLongAttribute(parser, "time", 0);
        XmlUtils.skipCurrentTag(parser);
    }

    private static void loadChooserCounts(XmlPullParser parser, UsageStats usageStats, String action) throws XmlPullParserException, IOException {
        if (action != null) {
            if (usageStats.mChooserCounts == null) {
                usageStats.mChooserCounts = new ArrayMap();
            }
            if (!usageStats.mChooserCounts.containsKey(action)) {
                usageStats.mChooserCounts.put(action, new ArrayMap<>());
            }
            while (true) {
                int next = parser.next();
                int eventCode = next;
                if (next != 1) {
                    String tag = parser.getName();
                    if (eventCode == 3 && tag.equals(CHOOSER_COUNT_TAG)) {
                        return;
                    }
                    if (eventCode == 2 && tag.equals(CATEGORY_TAG)) {
                        ((ArrayMap) usageStats.mChooserCounts.get(action)).put(XmlUtils.readStringAttribute(parser, "name"), Integer.valueOf(XmlUtils.readIntAttribute(parser, AssistDataRequester.KEY_RECEIVER_EXTRA_COUNT)));
                    }
                } else {
                    return;
                }
            }
        }
    }

    private static void loadConfigStats(XmlPullParser parser, IntervalStats statsOut) throws XmlPullParserException, IOException {
        Configuration config = new Configuration();
        Configuration.readXmlAttrs(parser, config);
        ConfigurationStats configStats = statsOut.getOrCreateConfigurationStats(config);
        configStats.mLastTimeActive = statsOut.beginTime + XmlUtils.readLongAttribute(parser, LAST_TIME_ACTIVE_ATTR);
        configStats.mTotalTimeActive = XmlUtils.readLongAttribute(parser, TOTAL_TIME_ACTIVE_ATTR);
        configStats.mActivationCount = XmlUtils.readIntAttribute(parser, AssistDataRequester.KEY_RECEIVER_EXTRA_COUNT);
        if (XmlUtils.readBooleanAttribute(parser, ACTIVE_ATTR)) {
            statsOut.activeConfiguration = configStats.mConfiguration;
        }
    }

    private static void loadEvent(XmlPullParser parser, IntervalStats statsOut) throws XmlPullParserException, IOException {
        String packageName = XmlUtils.readStringAttribute(parser, Settings.ATTR_PACKAGE);
        if (packageName != null) {
            UsageEvents.Event event = statsOut.buildEvent(packageName, XmlUtils.readStringAttribute(parser, CLASS_ATTR));
            event.mFlags = XmlUtils.readIntAttribute(parser, FLAGS_ATTR, 0);
            event.mTimeStamp = statsOut.beginTime + XmlUtils.readLongAttribute(parser, "time");
            event.mEventType = XmlUtils.readIntAttribute(parser, "type");
            try {
                event.mInstanceId = XmlUtils.readIntAttribute(parser, INSTANCE_ID_ATTR);
            } catch (IOException e) {
                Log.e(TAG, "Failed to parse mInstanceId", e);
            }
            int i = event.mEventType;
            if (i != 5) {
                String str = null;
                if (i == 8) {
                    String id = XmlUtils.readStringAttribute(parser, SHORTCUT_ID_ATTR);
                    if (id != null) {
                        str = id.intern();
                    }
                    event.mShortcutId = str;
                } else if (i == 11) {
                    event.mBucketAndReason = XmlUtils.readIntAttribute(parser, STANDBY_BUCKET_ATTR, 0);
                } else if (i == 12) {
                    String channelId = XmlUtils.readStringAttribute(parser, NOTIFICATION_CHANNEL_ATTR);
                    if (channelId != null) {
                        str = channelId.intern();
                    }
                    event.mNotificationChannelId = str;
                }
            } else {
                event.mConfiguration = new Configuration();
                Configuration.readXmlAttrs(parser, event.mConfiguration);
            }
            statsOut.addEvent(event);
            return;
        }
        throw new ProtocolException("no package attribute present");
    }

    private static void writeUsageStats(XmlSerializer xml, IntervalStats stats, UsageStats usageStats) throws IOException {
        xml.startTag((String) null, Settings.ATTR_PACKAGE);
        XmlUtils.writeLongAttribute(xml, LAST_TIME_ACTIVE_ATTR, usageStats.mLastTimeUsed - stats.beginTime);
        XmlUtils.writeLongAttribute(xml, LAST_TIME_VISIBLE_ATTR, usageStats.mLastTimeVisible - stats.beginTime);
        XmlUtils.writeLongAttribute(xml, LAST_TIME_SERVICE_USED_ATTR, usageStats.mLastTimeForegroundServiceUsed - stats.beginTime);
        XmlUtils.writeStringAttribute(xml, Settings.ATTR_PACKAGE, usageStats.mPackageName);
        XmlUtils.writeLongAttribute(xml, TOTAL_TIME_ACTIVE_ATTR, usageStats.mTotalTimeInForeground);
        XmlUtils.writeLongAttribute(xml, TOTAL_TIME_VISIBLE_ATTR, usageStats.mTotalTimeVisible);
        XmlUtils.writeLongAttribute(xml, TOTAL_TIME_SERVICE_USED_ATTR, usageStats.mTotalTimeForegroundServiceUsed);
        XmlUtils.writeIntAttribute(xml, LAST_EVENT_ATTR, usageStats.mLastEvent);
        if (usageStats.mAppLaunchCount > 0) {
            XmlUtils.writeIntAttribute(xml, APP_LAUNCH_COUNT_ATTR, usageStats.mAppLaunchCount);
        }
        writeChooserCounts(xml, usageStats);
        xml.endTag((String) null, Settings.ATTR_PACKAGE);
    }

    private static void writeCountAndTime(XmlSerializer xml, String tag, int count, long time) throws IOException {
        xml.startTag((String) null, tag);
        XmlUtils.writeIntAttribute(xml, AssistDataRequester.KEY_RECEIVER_EXTRA_COUNT, count);
        XmlUtils.writeLongAttribute(xml, "time", time);
        xml.endTag((String) null, tag);
    }

    private static void writeChooserCounts(XmlSerializer xml, UsageStats usageStats) throws IOException {
        if (usageStats != null && usageStats.mChooserCounts != null && !usageStats.mChooserCounts.keySet().isEmpty()) {
            int chooserCountSize = usageStats.mChooserCounts.size();
            for (int i = 0; i < chooserCountSize; i++) {
                String action = (String) usageStats.mChooserCounts.keyAt(i);
                ArrayMap<String, Integer> counts = (ArrayMap) usageStats.mChooserCounts.valueAt(i);
                if (!(action == null || counts == null || counts.isEmpty())) {
                    xml.startTag((String) null, CHOOSER_COUNT_TAG);
                    XmlUtils.writeStringAttribute(xml, "name", action);
                    writeCountsForAction(xml, counts);
                    xml.endTag((String) null, CHOOSER_COUNT_TAG);
                }
            }
        }
    }

    private static void writeCountsForAction(XmlSerializer xml, ArrayMap<String, Integer> counts) throws IOException {
        int countsSize = counts.size();
        for (int i = 0; i < countsSize; i++) {
            String key = counts.keyAt(i);
            int count = counts.valueAt(i).intValue();
            if (count > 0) {
                xml.startTag((String) null, CATEGORY_TAG);
                XmlUtils.writeStringAttribute(xml, "name", key);
                XmlUtils.writeIntAttribute(xml, AssistDataRequester.KEY_RECEIVER_EXTRA_COUNT, count);
                xml.endTag((String) null, CATEGORY_TAG);
            }
        }
    }

    private static void writeConfigStats(XmlSerializer xml, IntervalStats stats, ConfigurationStats configStats, boolean isActive) throws IOException {
        xml.startTag((String) null, CONFIG_TAG);
        XmlUtils.writeLongAttribute(xml, LAST_TIME_ACTIVE_ATTR, configStats.mLastTimeActive - stats.beginTime);
        XmlUtils.writeLongAttribute(xml, TOTAL_TIME_ACTIVE_ATTR, configStats.mTotalTimeActive);
        XmlUtils.writeIntAttribute(xml, AssistDataRequester.KEY_RECEIVER_EXTRA_COUNT, configStats.mActivationCount);
        if (isActive) {
            XmlUtils.writeBooleanAttribute(xml, ACTIVE_ATTR, true);
        }
        Configuration.writeXmlAttrs(xml, configStats.mConfiguration);
        xml.endTag((String) null, CONFIG_TAG);
    }

    private static void writeEvent(XmlSerializer xml, IntervalStats stats, UsageEvents.Event event) throws IOException {
        xml.startTag((String) null, EVENT_TAG);
        XmlUtils.writeLongAttribute(xml, "time", event.mTimeStamp - stats.beginTime);
        XmlUtils.writeStringAttribute(xml, Settings.ATTR_PACKAGE, event.mPackage);
        if (event.mClass != null) {
            XmlUtils.writeStringAttribute(xml, CLASS_ATTR, event.mClass);
        }
        XmlUtils.writeIntAttribute(xml, FLAGS_ATTR, event.mFlags);
        XmlUtils.writeIntAttribute(xml, "type", event.mEventType);
        XmlUtils.writeIntAttribute(xml, INSTANCE_ID_ATTR, event.mInstanceId);
        int i = event.mEventType;
        if (i != 5) {
            if (i != 8) {
                if (i != 11) {
                    if (i == 12 && event.mNotificationChannelId != null) {
                        XmlUtils.writeStringAttribute(xml, NOTIFICATION_CHANNEL_ATTR, event.mNotificationChannelId);
                    }
                } else if (event.mBucketAndReason != 0) {
                    XmlUtils.writeIntAttribute(xml, STANDBY_BUCKET_ATTR, event.mBucketAndReason);
                }
            } else if (event.mShortcutId != null) {
                XmlUtils.writeStringAttribute(xml, SHORTCUT_ID_ATTR, event.mShortcutId);
            }
        } else if (event.mConfiguration != null) {
            Configuration.writeXmlAttrs(xml, event.mConfiguration);
        }
        xml.endTag((String) null, EVENT_TAG);
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0097, code lost:
        if (r5.equals(NON_INTERACTIVE_TAG) != false) goto L_0x00b0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void read(org.xmlpull.v1.XmlPullParser r8, com.android.server.usage.IntervalStats r9) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            java.lang.String r0 = "UsageStatsXmlV1"
            android.util.ArrayMap<java.lang.String, android.app.usage.UsageStats> r1 = r9.packageStats
            r1.clear()
            android.util.ArrayMap<android.content.res.Configuration, android.app.usage.ConfigurationStats> r1 = r9.configurations
            r1.clear()
            r1 = 0
            r9.activeConfiguration = r1
            android.app.usage.EventList r1 = r9.events
            r1.clear()
            long r1 = r9.beginTime
            java.lang.String r3 = "endTime"
            long r3 = com.android.internal.util.XmlUtils.readLongAttribute(r8, r3)
            long r1 = r1 + r3
            r9.endTime = r1
            java.lang.String r1 = "majorVersion"
            int r1 = com.android.internal.util.XmlUtils.readIntAttribute(r8, r1)     // Catch:{ IOException -> 0x0029 }
            r9.majorVersion = r1     // Catch:{ IOException -> 0x0029 }
            goto L_0x002f
        L_0x0029:
            r1 = move-exception
            java.lang.String r2 = "Failed to parse majorVersion"
            android.util.Log.e(r0, r2, r1)
        L_0x002f:
            java.lang.String r1 = "minorVersion"
            int r1 = com.android.internal.util.XmlUtils.readIntAttribute(r8, r1)     // Catch:{ IOException -> 0x0039 }
            r9.minorVersion = r1     // Catch:{ IOException -> 0x0039 }
            goto L_0x003f
        L_0x0039:
            r1 = move-exception
            java.lang.String r2 = "Failed to parse minorVersion"
            android.util.Log.e(r0, r2, r1)
        L_0x003f:
            int r0 = r8.getDepth()
        L_0x0043:
            int r1 = r8.next()
            r2 = r1
            r3 = 1
            if (r1 == r3) goto L_0x00da
            r1 = 3
            if (r2 != r1) goto L_0x0054
            int r4 = r8.getDepth()
            if (r4 <= r0) goto L_0x00da
        L_0x0054:
            r4 = 2
            if (r2 == r4) goto L_0x0058
            goto L_0x0043
        L_0x0058:
            java.lang.String r5 = r8.getName()
            r6 = -1
            int r7 = r5.hashCode()
            switch(r7) {
                case -1354792126: goto L_0x00a5;
                case -1169351247: goto L_0x009a;
                case -807157790: goto L_0x0090;
                case -807062458: goto L_0x0085;
                case 96891546: goto L_0x007b;
                case 526608426: goto L_0x0070;
                case 1844104930: goto L_0x0065;
                default: goto L_0x0064;
            }
        L_0x0064:
            goto L_0x00af
        L_0x0065:
            java.lang.String r1 = "interactive"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0064
            r3 = 0
            goto L_0x00b0
        L_0x0070:
            java.lang.String r1 = "keyguard-shown"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0064
            r3 = r4
            goto L_0x00b0
        L_0x007b:
            java.lang.String r1 = "event"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0064
            r3 = 6
            goto L_0x00b0
        L_0x0085:
            java.lang.String r1 = "package"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0064
            r3 = 4
            goto L_0x00b0
        L_0x0090:
            java.lang.String r1 = "non-interactive"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0064
            goto L_0x00b0
        L_0x009a:
            java.lang.String r3 = "keyguard-hidden"
            boolean r3 = r5.equals(r3)
            if (r3 == 0) goto L_0x0064
            r3 = r1
            goto L_0x00b0
        L_0x00a5:
            java.lang.String r1 = "config"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0064
            r3 = 5
            goto L_0x00b0
        L_0x00af:
            r3 = r6
        L_0x00b0:
            switch(r3) {
                case 0: goto L_0x00d2;
                case 1: goto L_0x00cc;
                case 2: goto L_0x00c6;
                case 3: goto L_0x00c0;
                case 4: goto L_0x00bc;
                case 5: goto L_0x00b8;
                case 6: goto L_0x00b4;
                default: goto L_0x00b3;
            }
        L_0x00b3:
            goto L_0x00d8
        L_0x00b4:
            loadEvent(r8, r9)
            goto L_0x00d8
        L_0x00b8:
            loadConfigStats(r8, r9)
            goto L_0x00d8
        L_0x00bc:
            loadUsageStats(r8, r9)
            goto L_0x00d8
        L_0x00c0:
            com.android.server.usage.IntervalStats$EventTracker r1 = r9.keyguardHiddenTracker
            loadCountAndTime(r8, r1)
            goto L_0x00d8
        L_0x00c6:
            com.android.server.usage.IntervalStats$EventTracker r1 = r9.keyguardShownTracker
            loadCountAndTime(r8, r1)
            goto L_0x00d8
        L_0x00cc:
            com.android.server.usage.IntervalStats$EventTracker r1 = r9.nonInteractiveTracker
            loadCountAndTime(r8, r1)
            goto L_0x00d8
        L_0x00d2:
            com.android.server.usage.IntervalStats$EventTracker r1 = r9.interactiveTracker
            loadCountAndTime(r8, r1)
        L_0x00d8:
            goto L_0x0043
        L_0x00da:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usage.UsageStatsXmlV1.read(org.xmlpull.v1.XmlPullParser, com.android.server.usage.IntervalStats):void");
    }

    public static void write(XmlSerializer xml, IntervalStats stats) throws IOException {
        XmlUtils.writeLongAttribute(xml, END_TIME_ATTR, stats.endTime - stats.beginTime);
        XmlUtils.writeIntAttribute(xml, MAJOR_VERSION_ATTR, stats.majorVersion);
        XmlUtils.writeIntAttribute(xml, MINOR_VERSION_ATTR, stats.minorVersion);
        writeCountAndTime(xml, INTERACTIVE_TAG, stats.interactiveTracker.count, stats.interactiveTracker.duration);
        writeCountAndTime(xml, NON_INTERACTIVE_TAG, stats.nonInteractiveTracker.count, stats.nonInteractiveTracker.duration);
        writeCountAndTime(xml, KEYGUARD_SHOWN_TAG, stats.keyguardShownTracker.count, stats.keyguardShownTracker.duration);
        writeCountAndTime(xml, KEYGUARD_HIDDEN_TAG, stats.keyguardHiddenTracker.count, stats.keyguardHiddenTracker.duration);
        xml.startTag((String) null, PACKAGES_TAG);
        int statsCount = stats.packageStats.size();
        for (int i = 0; i < statsCount; i++) {
            writeUsageStats(xml, stats, stats.packageStats.valueAt(i));
        }
        xml.endTag((String) null, PACKAGES_TAG);
        xml.startTag((String) null, CONFIGURATIONS_TAG);
        int configCount = stats.configurations.size();
        for (int i2 = 0; i2 < configCount; i2++) {
            writeConfigStats(xml, stats, stats.configurations.valueAt(i2), stats.activeConfiguration.equals(stats.configurations.keyAt(i2)));
        }
        xml.endTag((String) null, CONFIGURATIONS_TAG);
        xml.startTag((String) null, EVENT_LOG_TAG);
        int eventCount = stats.events.size();
        for (int i3 = 0; i3 < eventCount; i3++) {
            writeEvent(xml, stats, stats.events.get(i3));
        }
        xml.endTag((String) null, EVENT_LOG_TAG);
    }

    private UsageStatsXmlV1() {
    }
}
