package com.android.server.usb;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.hardware.usb.AccessoryFilter;
import android.hardware.usb.DeviceFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.AtomicFile;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.Immutable;
import com.android.internal.app.IntentForwarderActivity;
import com.android.internal.content.PackageMonitor;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.XmlUtils;
import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.server.pm.CloudControlPreinstallService;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.Settings;
import com.android.server.slice.SliceClientPermissions;
import com.android.server.usb.MtpNotificationManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class UsbProfileGroupSettingsManager {
    private static final boolean DEBUG = false;
    private static final String TAG = UsbProfileGroupSettingsManager.class.getSimpleName();
    private static final File sSingleUserSettingsFile = new File("/data/system/usb_device_manager.xml");
    @GuardedBy({"mLock"})
    private final HashMap<AccessoryFilter, UserPackage> mAccessoryPreferenceMap = new HashMap<>();
    private final Context mContext;
    @GuardedBy({"mLock"})
    private final HashMap<DeviceFilter, UserPackage> mDevicePreferenceMap = new HashMap<>();
    private final boolean mDisablePermissionDialogs;
    @GuardedBy({"mLock"})
    private boolean mIsWriteSettingsScheduled;
    private final Object mLock = new Object();
    private final MtpNotificationManager mMtpNotificationManager;
    private final PackageManager mPackageManager;
    MyPackageMonitor mPackageMonitor = new MyPackageMonitor();
    /* access modifiers changed from: private */
    public final UserHandle mParentUser;
    private final AtomicFile mSettingsFile;
    private final UsbSettingsManager mSettingsManager;
    private final UsbHandlerManager mUsbHandlerManager;
    /* access modifiers changed from: private */
    public final UserManager mUserManager;

    @Immutable
    private static class UserPackage {
        final String packageName;
        final UserHandle user;

        private UserPackage(String packageName2, UserHandle user2) {
            this.packageName = packageName2;
            this.user = user2;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof UserPackage)) {
                return false;
            }
            UserPackage other = (UserPackage) obj;
            if (!this.user.equals(other.user) || !this.packageName.equals(other.packageName)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (this.user.hashCode() * 31) + this.packageName.hashCode();
        }

        public String toString() {
            return this.user.getIdentifier() + SliceClientPermissions.SliceAuthority.DELIMITER + this.packageName;
        }

        public void dump(DualDumpOutputStream dump, String idName, long id) {
            long token = dump.start(idName, id);
            dump.write("user_id", 1120986464257L, this.user.getIdentifier());
            dump.write("package_name", 1138166333442L, this.packageName);
            dump.end(token);
        }
    }

    private class MyPackageMonitor extends PackageMonitor {
        private MyPackageMonitor() {
        }

        public void onPackageAdded(String packageName, int uid) {
            if (UsbProfileGroupSettingsManager.this.mUserManager.isSameProfileGroup(UsbProfileGroupSettingsManager.this.mParentUser.getIdentifier(), UserHandle.getUserId(uid))) {
                UsbProfileGroupSettingsManager.this.handlePackageAdded(new UserPackage(packageName, UserHandle.getUserHandleForUid(uid)));
            }
        }

        public void onPackageRemoved(String packageName, int uid) {
            if (UsbProfileGroupSettingsManager.this.mUserManager.isSameProfileGroup(UsbProfileGroupSettingsManager.this.mParentUser.getIdentifier(), UserHandle.getUserId(uid))) {
                UsbProfileGroupSettingsManager.this.clearDefaults(packageName, UserHandle.getUserHandleForUid(uid));
            }
        }
    }

    UsbProfileGroupSettingsManager(Context context, UserHandle user, UsbSettingsManager settingsManager, UsbHandlerManager usbResolveActivityManager) {
        try {
            Context parentUserContext = context.createPackageContextAsUser(PackageManagerService.PLATFORM_PACKAGE_NAME, 0, user);
            this.mContext = context;
            this.mPackageManager = context.getPackageManager();
            this.mSettingsManager = settingsManager;
            this.mUserManager = (UserManager) context.getSystemService("user");
            this.mParentUser = user;
            this.mSettingsFile = new AtomicFile(new File(Environment.getUserSystemDirectory(user.getIdentifier()), "usb_device_manager.xml"), "usb-state");
            this.mDisablePermissionDialogs = context.getResources().getBoolean(17891410);
            synchronized (this.mLock) {
                if (UserHandle.SYSTEM.equals(user)) {
                    upgradeSingleUserLocked();
                }
                readSettingsLocked();
            }
            this.mPackageMonitor.register(context, (Looper) null, UserHandle.ALL, true);
            this.mMtpNotificationManager = new MtpNotificationManager(parentUserContext, new MtpNotificationManager.OnOpenInAppListener() {
                public final void onOpenInApp(UsbDevice usbDevice) {
                    UsbProfileGroupSettingsManager.this.lambda$new$0$UsbProfileGroupSettingsManager(usbDevice);
                }
            });
            this.mUsbHandlerManager = usbResolveActivityManager;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Missing android package");
        }
    }

    public /* synthetic */ void lambda$new$0$UsbProfileGroupSettingsManager(UsbDevice device) {
        resolveActivity(createDeviceAttachedIntent(device), device, false);
    }

    /* access modifiers changed from: package-private */
    public void removeAllDefaultsForUser(UserHandle userToRemove) {
        synchronized (this.mLock) {
            boolean needToPersist = false;
            Iterator<Map.Entry<DeviceFilter, UserPackage>> devicePreferenceIt = this.mDevicePreferenceMap.entrySet().iterator();
            while (devicePreferenceIt.hasNext()) {
                if (devicePreferenceIt.next().getValue().user.equals(userToRemove)) {
                    devicePreferenceIt.remove();
                    needToPersist = true;
                }
            }
            Iterator<Map.Entry<AccessoryFilter, UserPackage>> accessoryPreferenceIt = this.mAccessoryPreferenceMap.entrySet().iterator();
            while (accessoryPreferenceIt.hasNext()) {
                if (accessoryPreferenceIt.next().getValue().user.equals(userToRemove)) {
                    accessoryPreferenceIt.remove();
                    needToPersist = true;
                }
            }
            if (needToPersist) {
                scheduleWriteSettingsLocked();
            }
        }
    }

    private void readPreference(XmlPullParser parser) throws XmlPullParserException, IOException {
        String packageName = null;
        UserHandle user = this.mParentUser;
        int count = parser.getAttributeCount();
        for (int i = 0; i < count; i++) {
            if (Settings.ATTR_PACKAGE.equals(parser.getAttributeName(i))) {
                packageName = parser.getAttributeValue(i);
            }
            if ("user".equals(parser.getAttributeName(i))) {
                user = this.mUserManager.getUserForSerialNumber((long) Integer.parseInt(parser.getAttributeValue(i)));
            }
        }
        XmlUtils.nextElement(parser);
        if ("usb-device".equals(parser.getName())) {
            DeviceFilter filter = DeviceFilter.read(parser);
            if (user != null) {
                this.mDevicePreferenceMap.put(filter, new UserPackage(packageName, user));
            }
        } else if ("usb-accessory".equals(parser.getName())) {
            AccessoryFilter filter2 = AccessoryFilter.read(parser);
            if (user != null) {
                this.mAccessoryPreferenceMap.put(filter2, new UserPackage(packageName, user));
            }
        }
        XmlUtils.nextElement(parser);
    }

    @GuardedBy({"mLock"})
    private void upgradeSingleUserLocked() {
        if (sSingleUserSettingsFile.exists()) {
            this.mDevicePreferenceMap.clear();
            this.mAccessoryPreferenceMap.clear();
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(sSingleUserSettingsFile);
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(fis, StandardCharsets.UTF_8.name());
                XmlUtils.nextElement(parser);
                while (parser.getEventType() != 1) {
                    if ("preference".equals(parser.getName())) {
                        readPreference(parser);
                    } else {
                        XmlUtils.nextElement(parser);
                    }
                }
            } catch (IOException | XmlPullParserException e) {
                Log.wtf(TAG, "Failed to read single-user settings", e);
            } catch (Throwable th) {
                IoUtils.closeQuietly((AutoCloseable) null);
                throw th;
            }
            IoUtils.closeQuietly(fis);
            scheduleWriteSettingsLocked();
            sSingleUserSettingsFile.delete();
        }
    }

    @GuardedBy({"mLock"})
    private void readSettingsLocked() {
        this.mDevicePreferenceMap.clear();
        this.mAccessoryPreferenceMap.clear();
        FileInputStream stream = null;
        try {
            stream = this.mSettingsFile.openRead();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(stream, StandardCharsets.UTF_8.name());
            XmlUtils.nextElement(parser);
            while (parser.getEventType() != 1) {
                if ("preference".equals(parser.getName())) {
                    readPreference(parser);
                } else {
                    XmlUtils.nextElement(parser);
                }
            }
        } catch (FileNotFoundException e) {
        } catch (Exception e2) {
            Slog.e(TAG, "error reading settings file, deleting to start fresh", e2);
            this.mSettingsFile.delete();
        } catch (Throwable th) {
            IoUtils.closeQuietly((AutoCloseable) null);
            throw th;
        }
        IoUtils.closeQuietly(stream);
    }

    @GuardedBy({"mLock"})
    private void scheduleWriteSettingsLocked() {
        if (!this.mIsWriteSettingsScheduled) {
            this.mIsWriteSettingsScheduled = true;
            AsyncTask.execute(new Runnable() {
                public final void run() {
                    UsbProfileGroupSettingsManager.this.lambda$scheduleWriteSettingsLocked$1$UsbProfileGroupSettingsManager();
                }
            });
        }
    }

    public /* synthetic */ void lambda$scheduleWriteSettingsLocked$1$UsbProfileGroupSettingsManager() {
        synchronized (this.mLock) {
            try {
                FileOutputStream fos = this.mSettingsFile.startWrite();
                FastXmlSerializer serializer = new FastXmlSerializer();
                serializer.setOutput(fos, StandardCharsets.UTF_8.name());
                serializer.startDocument((String) null, true);
                serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                serializer.startTag((String) null, "settings");
                for (DeviceFilter filter : this.mDevicePreferenceMap.keySet()) {
                    serializer.startTag((String) null, "preference");
                    serializer.attribute((String) null, Settings.ATTR_PACKAGE, this.mDevicePreferenceMap.get(filter).packageName);
                    serializer.attribute((String) null, "user", String.valueOf(getSerial(this.mDevicePreferenceMap.get(filter).user)));
                    filter.write(serializer);
                    serializer.endTag((String) null, "preference");
                }
                for (AccessoryFilter filter2 : this.mAccessoryPreferenceMap.keySet()) {
                    serializer.startTag((String) null, "preference");
                    serializer.attribute((String) null, Settings.ATTR_PACKAGE, this.mAccessoryPreferenceMap.get(filter2).packageName);
                    serializer.attribute((String) null, "user", String.valueOf(getSerial(this.mAccessoryPreferenceMap.get(filter2).user)));
                    filter2.write(serializer);
                    serializer.endTag((String) null, "preference");
                }
                serializer.endTag((String) null, "settings");
                serializer.endDocument();
                this.mSettingsFile.finishWrite(fos);
            } catch (IOException e) {
                Slog.e(TAG, "Failed to write settings", e);
                if (0 != 0) {
                    this.mSettingsFile.failWrite((FileOutputStream) null);
                }
            }
            this.mIsWriteSettingsScheduled = false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0078, code lost:
        if (r2 == null) goto L_0x007b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.util.ArrayList<android.hardware.usb.DeviceFilter> getDeviceFilters(android.content.pm.PackageManager r7, android.content.pm.ResolveInfo r8) {
        /*
            r0 = 0
            android.content.pm.ActivityInfo r1 = r8.activityInfo
            r2 = 0
            java.lang.String r3 = "android.hardware.usb.action.USB_DEVICE_ATTACHED"
            android.content.res.XmlResourceParser r3 = r1.loadXmlMetaData(r7, r3)     // Catch:{ Exception -> 0x005c }
            r2 = r3
            if (r2 != 0) goto L_0x002b
            java.lang.String r3 = TAG     // Catch:{ Exception -> 0x005c }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x005c }
            r4.<init>()     // Catch:{ Exception -> 0x005c }
            java.lang.String r5 = "no meta-data for "
            r4.append(r5)     // Catch:{ Exception -> 0x005c }
            r4.append(r8)     // Catch:{ Exception -> 0x005c }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x005c }
            android.util.Slog.w(r3, r4)     // Catch:{ Exception -> 0x005c }
            r3 = 0
            if (r2 == 0) goto L_0x002a
            r2.close()
        L_0x002a:
            return r3
        L_0x002b:
            com.android.internal.util.XmlUtils.nextElement(r2)     // Catch:{ Exception -> 0x005c }
        L_0x002e:
            int r3 = r2.getEventType()     // Catch:{ Exception -> 0x005c }
            r4 = 1
            if (r3 == r4) goto L_0x0055
            java.lang.String r3 = r2.getName()     // Catch:{ Exception -> 0x005c }
            java.lang.String r5 = "usb-device"
            boolean r5 = r5.equals(r3)     // Catch:{ Exception -> 0x005c }
            if (r5 == 0) goto L_0x0051
            if (r0 != 0) goto L_0x004a
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x005c }
            r5.<init>(r4)     // Catch:{ Exception -> 0x005c }
            r0 = r5
        L_0x004a:
            android.hardware.usb.DeviceFilter r4 = android.hardware.usb.DeviceFilter.read(r2)     // Catch:{ Exception -> 0x005c }
            r0.add(r4)     // Catch:{ Exception -> 0x005c }
        L_0x0051:
            com.android.internal.util.XmlUtils.nextElement(r2)     // Catch:{ Exception -> 0x005c }
            goto L_0x002e
        L_0x0055:
        L_0x0056:
            r2.close()
            goto L_0x007b
        L_0x005a:
            r3 = move-exception
            goto L_0x007c
        L_0x005c:
            r3 = move-exception
            java.lang.String r4 = TAG     // Catch:{ all -> 0x005a }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x005a }
            r5.<init>()     // Catch:{ all -> 0x005a }
            java.lang.String r6 = "Unable to load component info "
            r5.append(r6)     // Catch:{ all -> 0x005a }
            java.lang.String r6 = r8.toString()     // Catch:{ all -> 0x005a }
            r5.append(r6)     // Catch:{ all -> 0x005a }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x005a }
            android.util.Slog.w(r4, r5, r3)     // Catch:{ all -> 0x005a }
            if (r2 == 0) goto L_0x007b
            goto L_0x0056
        L_0x007b:
            return r0
        L_0x007c:
            if (r2 == 0) goto L_0x0081
            r2.close()
        L_0x0081:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbProfileGroupSettingsManager.getDeviceFilters(android.content.pm.PackageManager, android.content.pm.ResolveInfo):java.util.ArrayList");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0078, code lost:
        if (r2 == null) goto L_0x007b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.util.ArrayList<android.hardware.usb.AccessoryFilter> getAccessoryFilters(android.content.pm.PackageManager r7, android.content.pm.ResolveInfo r8) {
        /*
            r0 = 0
            android.content.pm.ActivityInfo r1 = r8.activityInfo
            r2 = 0
            java.lang.String r3 = "android.hardware.usb.action.USB_ACCESSORY_ATTACHED"
            android.content.res.XmlResourceParser r3 = r1.loadXmlMetaData(r7, r3)     // Catch:{ Exception -> 0x005c }
            r2 = r3
            if (r2 != 0) goto L_0x002b
            java.lang.String r3 = TAG     // Catch:{ Exception -> 0x005c }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x005c }
            r4.<init>()     // Catch:{ Exception -> 0x005c }
            java.lang.String r5 = "no meta-data for "
            r4.append(r5)     // Catch:{ Exception -> 0x005c }
            r4.append(r8)     // Catch:{ Exception -> 0x005c }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x005c }
            android.util.Slog.w(r3, r4)     // Catch:{ Exception -> 0x005c }
            r3 = 0
            if (r2 == 0) goto L_0x002a
            r2.close()
        L_0x002a:
            return r3
        L_0x002b:
            com.android.internal.util.XmlUtils.nextElement(r2)     // Catch:{ Exception -> 0x005c }
        L_0x002e:
            int r3 = r2.getEventType()     // Catch:{ Exception -> 0x005c }
            r4 = 1
            if (r3 == r4) goto L_0x0055
            java.lang.String r3 = r2.getName()     // Catch:{ Exception -> 0x005c }
            java.lang.String r5 = "usb-accessory"
            boolean r5 = r5.equals(r3)     // Catch:{ Exception -> 0x005c }
            if (r5 == 0) goto L_0x0051
            if (r0 != 0) goto L_0x004a
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x005c }
            r5.<init>(r4)     // Catch:{ Exception -> 0x005c }
            r0 = r5
        L_0x004a:
            android.hardware.usb.AccessoryFilter r4 = android.hardware.usb.AccessoryFilter.read(r2)     // Catch:{ Exception -> 0x005c }
            r0.add(r4)     // Catch:{ Exception -> 0x005c }
        L_0x0051:
            com.android.internal.util.XmlUtils.nextElement(r2)     // Catch:{ Exception -> 0x005c }
            goto L_0x002e
        L_0x0055:
        L_0x0056:
            r2.close()
            goto L_0x007b
        L_0x005a:
            r3 = move-exception
            goto L_0x007c
        L_0x005c:
            r3 = move-exception
            java.lang.String r4 = TAG     // Catch:{ all -> 0x005a }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x005a }
            r5.<init>()     // Catch:{ all -> 0x005a }
            java.lang.String r6 = "Unable to load component info "
            r5.append(r6)     // Catch:{ all -> 0x005a }
            java.lang.String r6 = r8.toString()     // Catch:{ all -> 0x005a }
            r5.append(r6)     // Catch:{ all -> 0x005a }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x005a }
            android.util.Slog.w(r4, r5, r3)     // Catch:{ all -> 0x005a }
            if (r2 == 0) goto L_0x007b
            goto L_0x0056
        L_0x007b:
            return r0
        L_0x007c:
            if (r2 == 0) goto L_0x0081
            r2.close()
        L_0x0081:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbProfileGroupSettingsManager.getAccessoryFilters(android.content.pm.PackageManager, android.content.pm.ResolveInfo):java.util.ArrayList");
    }

    private boolean packageMatchesLocked(ResolveInfo info, UsbDevice device, UsbAccessory accessory) {
        ArrayList<AccessoryFilter> accessoryFilters;
        ArrayList<DeviceFilter> deviceFilters;
        if (isForwardMatch(info)) {
            return true;
        }
        if (!(device == null || (deviceFilters = getDeviceFilters(this.mPackageManager, info)) == null)) {
            int numDeviceFilters = deviceFilters.size();
            for (int i = 0; i < numDeviceFilters; i++) {
                if (deviceFilters.get(i).matches(device)) {
                    return true;
                }
            }
        }
        if (accessory == null || (accessoryFilters = getAccessoryFilters(this.mPackageManager, info)) == null) {
            return false;
        }
        int numAccessoryFilters = accessoryFilters.size();
        for (int i2 = 0; i2 < numAccessoryFilters; i2++) {
            if (accessoryFilters.get(i2).matches(accessory)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<ResolveInfo> queryIntentActivitiesForAllProfiles(Intent intent) {
        List<UserInfo> profiles = this.mUserManager.getEnabledProfiles(this.mParentUser.getIdentifier());
        ArrayList<ResolveInfo> resolveInfos = new ArrayList<>();
        int numProfiles = profiles.size();
        for (int i = 0; i < numProfiles; i++) {
            resolveInfos.addAll(this.mSettingsManager.getSettingsForUser(profiles.get(i).id).queryIntentActivities(intent));
        }
        return resolveInfos;
    }

    private boolean isForwardMatch(ResolveInfo match) {
        return match.getComponentInfo().name.equals(IntentForwarderActivity.FORWARD_INTENT_TO_MANAGED_PROFILE);
    }

    private ArrayList<ResolveInfo> preferHighPriority(ArrayList<ResolveInfo> matches) {
        SparseArray<ArrayList<ResolveInfo>> highestPriorityMatchesByUserId = new SparseArray<>();
        SparseIntArray highestPriorityByUserId = new SparseIntArray();
        ArrayList<ResolveInfo> forwardMatches = new ArrayList<>();
        int numMatches = matches.size();
        for (int matchNum = 0; matchNum < numMatches; matchNum++) {
            ResolveInfo match = matches.get(matchNum);
            if (isForwardMatch(match)) {
                forwardMatches.add(match);
            } else {
                if (highestPriorityByUserId.indexOfKey(match.targetUserId) < 0) {
                    highestPriorityByUserId.put(match.targetUserId, Integer.MIN_VALUE);
                    highestPriorityMatchesByUserId.put(match.targetUserId, new ArrayList());
                }
                int highestPriority = highestPriorityByUserId.get(match.targetUserId);
                ArrayList<ResolveInfo> highestPriorityMatches = highestPriorityMatchesByUserId.get(match.targetUserId);
                if (match.priority == highestPriority) {
                    highestPriorityMatches.add(match);
                } else if (match.priority > highestPriority) {
                    highestPriorityByUserId.put(match.targetUserId, match.priority);
                    highestPriorityMatches.clear();
                    highestPriorityMatches.add(match);
                }
            }
        }
        ArrayList<ResolveInfo> combinedMatches = new ArrayList<>(forwardMatches);
        int numMatchArrays = highestPriorityMatchesByUserId.size();
        for (int matchArrayNum = 0; matchArrayNum < numMatchArrays; matchArrayNum++) {
            combinedMatches.addAll(highestPriorityMatchesByUserId.valueAt(matchArrayNum));
        }
        return combinedMatches;
    }

    private ArrayList<ResolveInfo> removeForwardIntentIfNotNeeded(ArrayList<ResolveInfo> rawMatches) {
        int numRawMatches = rawMatches.size();
        int numParentActivityMatches = 0;
        int numNonParentActivityMatches = 0;
        for (int i = 0; i < numRawMatches; i++) {
            ResolveInfo rawMatch = rawMatches.get(i);
            if (!isForwardMatch(rawMatch)) {
                if (UserHandle.getUserHandleForUid(rawMatch.activityInfo.applicationInfo.uid).equals(this.mParentUser)) {
                    numParentActivityMatches++;
                } else {
                    numNonParentActivityMatches++;
                }
            }
        }
        if (numParentActivityMatches != 0 && numNonParentActivityMatches != 0) {
            return rawMatches;
        }
        ArrayList<ResolveInfo> matches = new ArrayList<>(numParentActivityMatches + numNonParentActivityMatches);
        for (int i2 = 0; i2 < numRawMatches; i2++) {
            ResolveInfo rawMatch2 = rawMatches.get(i2);
            if (!isForwardMatch(rawMatch2)) {
                matches.add(rawMatch2);
            }
        }
        return matches;
    }

    private ArrayList<ResolveInfo> getDeviceMatchesLocked(UsbDevice device, Intent intent) {
        ArrayList<ResolveInfo> matches = new ArrayList<>();
        List<ResolveInfo> resolveInfos = queryIntentActivitiesForAllProfiles(intent);
        int count = resolveInfos.size();
        for (int i = 0; i < count; i++) {
            ResolveInfo resolveInfo = resolveInfos.get(i);
            if (packageMatchesLocked(resolveInfo, device, (UsbAccessory) null)) {
                matches.add(resolveInfo);
            }
        }
        return removeForwardIntentIfNotNeeded(preferHighPriority(matches));
    }

    private ArrayList<ResolveInfo> getAccessoryMatchesLocked(UsbAccessory accessory, Intent intent) {
        ArrayList<ResolveInfo> matches = new ArrayList<>();
        List<ResolveInfo> resolveInfos = queryIntentActivitiesForAllProfiles(intent);
        int count = resolveInfos.size();
        for (int i = 0; i < count; i++) {
            ResolveInfo resolveInfo = resolveInfos.get(i);
            if (packageMatchesLocked(resolveInfo, (UsbDevice) null, accessory)) {
                matches.add(resolveInfo);
            }
        }
        return removeForwardIntentIfNotNeeded(preferHighPriority(matches));
    }

    public void deviceAttached(UsbDevice device) {
        Intent intent = createDeviceAttachedIntent(device);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        resolveActivity(intent, device, true);
    }

    private void resolveActivity(Intent intent, UsbDevice device, boolean showMtpNotification) {
        ArrayList<ResolveInfo> matches;
        ActivityInfo defaultActivity;
        synchronized (this.mLock) {
            matches = getDeviceMatchesLocked(device, intent);
            defaultActivity = getDefaultActivityLocked(matches, this.mDevicePreferenceMap.get(new DeviceFilter(device)));
        }
        if (!showMtpNotification || !MtpNotificationManager.shouldShowNotification(this.mPackageManager, device) || defaultActivity != null) {
            resolveActivity(intent, matches, defaultActivity, device, (UsbAccessory) null);
        } else {
            this.mMtpNotificationManager.showNotification(device);
        }
    }

    public void deviceAttachedForFixedHandler(UsbDevice device, ComponentName component) {
        Intent intent = createDeviceAttachedIntent(device);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.of(ActivityManager.getCurrentUser()));
        try {
            ApplicationInfo appInfo = this.mPackageManager.getApplicationInfoAsUser(component.getPackageName(), 0, this.mParentUser.getIdentifier());
            this.mSettingsManager.getSettingsForUser(UserHandle.getUserId(appInfo.uid)).grantDevicePermission(device, appInfo.uid);
            Intent activityIntent = new Intent(intent);
            activityIntent.setComponent(component);
            try {
                this.mContext.startActivityAsUser(activityIntent, this.mParentUser);
            } catch (ActivityNotFoundException e) {
                String str = TAG;
                Slog.e(str, "unable to start activity " + activityIntent);
            }
        } catch (PackageManager.NameNotFoundException e2) {
            String str2 = TAG;
            Slog.e(str2, "Default USB handling package (" + component.getPackageName() + ") not found  for user " + this.mParentUser);
        }
    }

    /* access modifiers changed from: package-private */
    public void usbDeviceRemoved(UsbDevice device) {
        this.mMtpNotificationManager.hideNotification(device.getDeviceId());
    }

    public void accessoryAttached(UsbAccessory accessory) {
        ArrayList<ResolveInfo> matches;
        ActivityInfo defaultActivity;
        Intent intent = new Intent("android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
        intent.putExtra("accessory", accessory);
        intent.addFlags(285212672);
        synchronized (this.mLock) {
            matches = getAccessoryMatchesLocked(accessory, intent);
            defaultActivity = getDefaultActivityLocked(matches, this.mAccessoryPreferenceMap.get(new AccessoryFilter(accessory)));
        }
        resolveActivity(intent, matches, defaultActivity, (UsbDevice) null, accessory);
    }

    private void resolveActivity(Intent intent, ArrayList<ResolveInfo> matches, ActivityInfo defaultActivity, UsbDevice device, UsbAccessory accessory) {
        if (matches.size() == 0) {
            if (accessory != null) {
                this.mUsbHandlerManager.showUsbAccessoryUriActivity(accessory, this.mParentUser);
            }
        } else if (defaultActivity != null) {
            UsbUserSettingsManager defaultRIUserSettings = this.mSettingsManager.getSettingsForUser(UserHandle.getUserId(defaultActivity.applicationInfo.uid));
            if (device != null) {
                defaultRIUserSettings.grantDevicePermission(device, defaultActivity.applicationInfo.uid);
            } else if (accessory != null) {
                defaultRIUserSettings.grantAccessoryPermission(accessory, defaultActivity.applicationInfo.uid);
            }
            try {
                intent.setComponent(new ComponentName(defaultActivity.packageName, defaultActivity.name));
                this.mContext.startActivityAsUser(intent, UserHandle.getUserHandleForUid(defaultActivity.applicationInfo.uid));
            } catch (ActivityNotFoundException e) {
                Slog.e(TAG, "startActivity failed", e);
            }
        } else if (matches.size() == 1) {
            this.mUsbHandlerManager.confirmUsbHandler(matches.get(0), device, accessory);
        } else {
            this.mUsbHandlerManager.selectUsbHandler(matches, this.mParentUser, intent);
        }
    }

    private ActivityInfo getDefaultActivityLocked(ArrayList<ResolveInfo> matches, UserPackage userPackage) {
        ActivityInfo activityInfo;
        if (userPackage != null) {
            Iterator<ResolveInfo> it = matches.iterator();
            while (it.hasNext()) {
                ResolveInfo info = it.next();
                if (info.activityInfo != null && userPackage.equals(new UserPackage(info.activityInfo.packageName, UserHandle.getUserHandleForUid(info.activityInfo.applicationInfo.uid)))) {
                    return info.activityInfo;
                }
            }
        }
        if (matches.size() == 1 && (activityInfo = matches.get(0).activityInfo) != null) {
            if (this.mDisablePermissionDialogs) {
                return activityInfo;
            }
            if (activityInfo.applicationInfo == null || (1 & activityInfo.applicationInfo.flags) == 0) {
                return null;
            }
            return activityInfo;
        }
        return null;
    }

    @GuardedBy({"mLock"})
    private boolean clearCompatibleMatchesLocked(UserPackage userPackage, DeviceFilter filter) {
        ArrayList<DeviceFilter> keysToRemove = new ArrayList<>();
        for (DeviceFilter device : this.mDevicePreferenceMap.keySet()) {
            if (filter.contains(device) && !this.mDevicePreferenceMap.get(device).equals(userPackage)) {
                keysToRemove.add(device);
            }
        }
        if (!keysToRemove.isEmpty()) {
            Iterator<DeviceFilter> it = keysToRemove.iterator();
            while (it.hasNext()) {
                this.mDevicePreferenceMap.remove(it.next());
            }
        }
        return !keysToRemove.isEmpty();
    }

    @GuardedBy({"mLock"})
    private boolean clearCompatibleMatchesLocked(UserPackage userPackage, AccessoryFilter filter) {
        ArrayList<AccessoryFilter> keysToRemove = new ArrayList<>();
        for (AccessoryFilter accessory : this.mAccessoryPreferenceMap.keySet()) {
            if (filter.contains(accessory) && !this.mAccessoryPreferenceMap.get(accessory).equals(userPackage)) {
                keysToRemove.add(accessory);
            }
        }
        if (!keysToRemove.isEmpty()) {
            Iterator<AccessoryFilter> it = keysToRemove.iterator();
            while (it.hasNext()) {
                this.mAccessoryPreferenceMap.remove(it.next());
            }
        }
        return !keysToRemove.isEmpty();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0070, code lost:
        if (r0 == null) goto L_0x0073;
     */
    @com.android.internal.annotations.GuardedBy({"mLock"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean handlePackageAddedLocked(com.android.server.usb.UsbProfileGroupSettingsManager.UserPackage r7, android.content.pm.ActivityInfo r8, java.lang.String r9) {
        /*
            r6 = this;
            r0 = 0
            r1 = 0
            android.content.pm.PackageManager r2 = r6.mPackageManager     // Catch:{ Exception -> 0x0054 }
            android.content.res.XmlResourceParser r2 = r8.loadXmlMetaData(r2, r9)     // Catch:{ Exception -> 0x0054 }
            r0 = r2
            if (r0 != 0) goto L_0x0012
            r2 = 0
            if (r0 == 0) goto L_0x0011
            r0.close()
        L_0x0011:
            return r2
        L_0x0012:
            com.android.internal.util.XmlUtils.nextElement(r0)     // Catch:{ Exception -> 0x0054 }
        L_0x0015:
            int r2 = r0.getEventType()     // Catch:{ Exception -> 0x0054 }
            r3 = 1
            if (r2 == r3) goto L_0x004d
            java.lang.String r2 = r0.getName()     // Catch:{ Exception -> 0x0054 }
            java.lang.String r3 = "usb-device"
            boolean r3 = r3.equals(r2)     // Catch:{ Exception -> 0x0054 }
            if (r3 == 0) goto L_0x0035
            android.hardware.usb.DeviceFilter r3 = android.hardware.usb.DeviceFilter.read(r0)     // Catch:{ Exception -> 0x0054 }
            boolean r4 = r6.clearCompatibleMatchesLocked((com.android.server.usb.UsbProfileGroupSettingsManager.UserPackage) r7, (android.hardware.usb.DeviceFilter) r3)     // Catch:{ Exception -> 0x0054 }
            if (r4 == 0) goto L_0x0034
            r1 = 1
        L_0x0034:
            goto L_0x0049
        L_0x0035:
            java.lang.String r3 = "usb-accessory"
            boolean r3 = r3.equals(r2)     // Catch:{ Exception -> 0x0054 }
            if (r3 == 0) goto L_0x0049
            android.hardware.usb.AccessoryFilter r3 = android.hardware.usb.AccessoryFilter.read(r0)     // Catch:{ Exception -> 0x0054 }
            boolean r4 = r6.clearCompatibleMatchesLocked((com.android.server.usb.UsbProfileGroupSettingsManager.UserPackage) r7, (android.hardware.usb.AccessoryFilter) r3)     // Catch:{ Exception -> 0x0054 }
            if (r4 == 0) goto L_0x0049
            r1 = 1
        L_0x0049:
            com.android.internal.util.XmlUtils.nextElement(r0)     // Catch:{ Exception -> 0x0054 }
            goto L_0x0015
        L_0x004d:
        L_0x004e:
            r0.close()
            goto L_0x0073
        L_0x0052:
            r2 = move-exception
            goto L_0x0074
        L_0x0054:
            r2 = move-exception
            java.lang.String r3 = TAG     // Catch:{ all -> 0x0052 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0052 }
            r4.<init>()     // Catch:{ all -> 0x0052 }
            java.lang.String r5 = "Unable to load component info "
            r4.append(r5)     // Catch:{ all -> 0x0052 }
            java.lang.String r5 = r8.toString()     // Catch:{ all -> 0x0052 }
            r4.append(r5)     // Catch:{ all -> 0x0052 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0052 }
            android.util.Slog.w(r3, r4, r2)     // Catch:{ all -> 0x0052 }
            if (r0 == 0) goto L_0x0073
            goto L_0x004e
        L_0x0073:
            return r1
        L_0x0074:
            if (r0 == 0) goto L_0x0079
            r0.close()
        L_0x0079:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbProfileGroupSettingsManager.handlePackageAddedLocked(com.android.server.usb.UsbProfileGroupSettingsManager$UserPackage, android.content.pm.ActivityInfo, java.lang.String):boolean");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x003e, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handlePackageAdded(com.android.server.usb.UsbProfileGroupSettingsManager.UserPackage r8) {
        /*
            r7 = this;
            java.lang.Object r0 = r7.mLock
            monitor-enter(r0)
            r1 = 0
            android.content.pm.PackageManager r2 = r7.mPackageManager     // Catch:{ NameNotFoundException -> 0x0041 }
            java.lang.String r3 = r8.packageName     // Catch:{ NameNotFoundException -> 0x0041 }
            r4 = 129(0x81, float:1.81E-43)
            android.os.UserHandle r5 = r8.user     // Catch:{ NameNotFoundException -> 0x0041 }
            int r5 = r5.getIdentifier()     // Catch:{ NameNotFoundException -> 0x0041 }
            android.content.pm.PackageInfo r2 = r2.getPackageInfoAsUser(r3, r4, r5)     // Catch:{ NameNotFoundException -> 0x0041 }
            android.content.pm.ActivityInfo[] r3 = r2.activities     // Catch:{ all -> 0x003f }
            if (r3 != 0) goto L_0x001b
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            return
        L_0x001b:
            r4 = 0
        L_0x001c:
            int r5 = r3.length     // Catch:{ all -> 0x003f }
            if (r4 >= r5) goto L_0x0038
            r5 = r3[r4]     // Catch:{ all -> 0x003f }
            java.lang.String r6 = "android.hardware.usb.action.USB_DEVICE_ATTACHED"
            boolean r5 = r7.handlePackageAddedLocked(r8, r5, r6)     // Catch:{ all -> 0x003f }
            if (r5 == 0) goto L_0x002a
            r1 = 1
        L_0x002a:
            r5 = r3[r4]     // Catch:{ all -> 0x003f }
            java.lang.String r6 = "android.hardware.usb.action.USB_ACCESSORY_ATTACHED"
            boolean r5 = r7.handlePackageAddedLocked(r8, r5, r6)     // Catch:{ all -> 0x003f }
            if (r5 == 0) goto L_0x0035
            r1 = 1
        L_0x0035:
            int r4 = r4 + 1
            goto L_0x001c
        L_0x0038:
            if (r1 == 0) goto L_0x003d
            r7.scheduleWriteSettingsLocked()     // Catch:{ all -> 0x003f }
        L_0x003d:
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            return
        L_0x003f:
            r1 = move-exception
            goto L_0x005a
        L_0x0041:
            r2 = move-exception
            java.lang.String r3 = TAG     // Catch:{ all -> 0x003f }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x003f }
            r4.<init>()     // Catch:{ all -> 0x003f }
            java.lang.String r5 = "handlePackageUpdate could not find package "
            r4.append(r5)     // Catch:{ all -> 0x003f }
            r4.append(r8)     // Catch:{ all -> 0x003f }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x003f }
            android.util.Slog.e(r3, r4, r2)     // Catch:{ all -> 0x003f }
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            return
        L_0x005a:
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbProfileGroupSettingsManager.handlePackageAdded(com.android.server.usb.UsbProfileGroupSettingsManager$UserPackage):void");
    }

    private int getSerial(UserHandle user) {
        return this.mUserManager.getUserSerialNumber(user.getIdentifier());
    }

    /* access modifiers changed from: package-private */
    public void setDevicePackage(UsbDevice device, String packageName, UserHandle user) {
        DeviceFilter filter = new DeviceFilter(device);
        synchronized (this.mLock) {
            boolean changed = true;
            if (packageName != null) {
                UserPackage userPackage = new UserPackage(packageName, user);
                changed = true ^ userPackage.equals(this.mDevicePreferenceMap.get(filter));
                if (changed) {
                    this.mDevicePreferenceMap.put(filter, userPackage);
                }
            } else if (this.mDevicePreferenceMap.remove(filter) == null) {
                changed = false;
            }
            if (changed) {
                scheduleWriteSettingsLocked();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setAccessoryPackage(UsbAccessory accessory, String packageName, UserHandle user) {
        AccessoryFilter filter = new AccessoryFilter(accessory);
        synchronized (this.mLock) {
            boolean changed = true;
            if (packageName != null) {
                UserPackage userPackage = new UserPackage(packageName, user);
                changed = true ^ userPackage.equals(this.mAccessoryPreferenceMap.get(filter));
                if (changed) {
                    this.mAccessoryPreferenceMap.put(filter, userPackage);
                }
            } else if (this.mAccessoryPreferenceMap.remove(filter) == null) {
                changed = false;
            }
            if (changed) {
                scheduleWriteSettingsLocked();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasDefaults(String packageName, UserHandle user) {
        UserPackage userPackage = new UserPackage(packageName, user);
        synchronized (this.mLock) {
            if (this.mDevicePreferenceMap.values().contains(userPackage)) {
                return true;
            }
            boolean contains = this.mAccessoryPreferenceMap.values().contains(userPackage);
            return contains;
        }
    }

    /* access modifiers changed from: package-private */
    public void clearDefaults(String packageName, UserHandle user) {
        UserPackage userPackage = new UserPackage(packageName, user);
        synchronized (this.mLock) {
            if (clearPackageDefaultsLocked(userPackage)) {
                scheduleWriteSettingsLocked();
            }
        }
    }

    private boolean clearPackageDefaultsLocked(UserPackage userPackage) {
        boolean cleared = false;
        synchronized (this.mLock) {
            if (this.mDevicePreferenceMap.containsValue(userPackage)) {
                DeviceFilter[] keys = (DeviceFilter[]) this.mDevicePreferenceMap.keySet().toArray(new DeviceFilter[0]);
                for (DeviceFilter key : keys) {
                    if (userPackage.equals(this.mDevicePreferenceMap.get(key))) {
                        this.mDevicePreferenceMap.remove(key);
                        cleared = true;
                    }
                }
            }
            if (this.mAccessoryPreferenceMap.containsValue(userPackage)) {
                AccessoryFilter[] keys2 = (AccessoryFilter[]) this.mAccessoryPreferenceMap.keySet().toArray(new AccessoryFilter[0]);
                for (AccessoryFilter key2 : keys2) {
                    if (userPackage.equals(this.mAccessoryPreferenceMap.get(key2))) {
                        this.mAccessoryPreferenceMap.remove(key2);
                        cleared = true;
                    }
                }
            }
        }
        return cleared;
    }

    public void dump(DualDumpOutputStream dump, String idName, long id) {
        DualDumpOutputStream dualDumpOutputStream = dump;
        long token = dump.start(idName, id);
        synchronized (this.mLock) {
            dualDumpOutputStream.write("parent_user_id", 1120986464257L, this.mParentUser.getIdentifier());
            for (DeviceFilter filter : this.mDevicePreferenceMap.keySet()) {
                long devicePrefToken = dualDumpOutputStream.start("device_preferences", 2246267895810L);
                filter.dump(dualDumpOutputStream, "filter", 1146756268033L);
                this.mDevicePreferenceMap.get(filter).dump(dualDumpOutputStream, "user_package", 1146756268034L);
                dualDumpOutputStream.end(devicePrefToken);
            }
            for (AccessoryFilter filter2 : this.mAccessoryPreferenceMap.keySet()) {
                long accessoryPrefToken = dualDumpOutputStream.start("accessory_preferences", 2246267895811L);
                filter2.dump(dualDumpOutputStream, "filter", 1146756268033L);
                this.mAccessoryPreferenceMap.get(filter2).dump(dualDumpOutputStream, "user_package", 1146756268034L);
                dualDumpOutputStream.end(accessoryPrefToken);
            }
        }
        dualDumpOutputStream.end(token);
    }

    private static Intent createDeviceAttachedIntent(UsbDevice device) {
        Intent intent = new Intent("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        intent.putExtra(CloudControlPreinstallService.ConnectEntity.DEVICE, device);
        intent.addFlags(285212672);
        return intent;
    }
}
