package com.android.server;

import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.ISystemUpdateManager;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.AtomicFile;
import android.util.Slog;
import com.android.internal.util.FastXmlSerializer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class SystemUpdateManagerService extends ISystemUpdateManager.Stub {
    private static final String INFO_FILE = "system-update-info.xml";
    private static final int INFO_FILE_VERSION = 0;
    private static final String KEY_BOOT_COUNT = "boot-count";
    private static final String KEY_INFO_BUNDLE = "info-bundle";
    private static final String KEY_UID = "uid";
    private static final String KEY_VERSION = "version";
    private static final String TAG = "SystemUpdateManagerService";
    private static final String TAG_INFO = "info";
    private static final int UID_UNKNOWN = -1;
    private final Context mContext;
    private final AtomicFile mFile;
    private int mLastStatus = 0;
    private int mLastUid = -1;
    private final Object mLock = new Object();

    public SystemUpdateManagerService(Context context) {
        this.mContext = context;
        this.mFile = new AtomicFile(new File(Environment.getDataSystemDirectory(), INFO_FILE));
        synchronized (this.mLock) {
            loadSystemUpdateInfoLocked();
        }
    }

    public void updateSystemUpdateInfo(PersistableBundle infoBundle) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.RECOVERY", TAG);
        int status = infoBundle.getInt("status", 0);
        if (status == 0) {
            Slog.w(TAG, "Invalid status info. Ignored");
            return;
        }
        int uid = Binder.getCallingUid();
        int i = this.mLastUid;
        if (i == -1 || i == uid || status != 1) {
            synchronized (this.mLock) {
                saveSystemUpdateInfoLocked(infoBundle, uid);
            }
            return;
        }
        Slog.i(TAG, "Inactive updater reporting IDLE status. Ignored");
    }

    public Bundle retrieveSystemUpdateInfo() {
        Bundle loadSystemUpdateInfoLocked;
        if (this.mContext.checkCallingOrSelfPermission("android.permission.READ_SYSTEM_UPDATE_INFO") == -1 && this.mContext.checkCallingOrSelfPermission("android.permission.RECOVERY") == -1) {
            throw new SecurityException("Can't read system update info. Requiring READ_SYSTEM_UPDATE_INFO or RECOVERY permission.");
        }
        synchronized (this.mLock) {
            loadSystemUpdateInfoLocked = loadSystemUpdateInfoLocked();
        }
        return loadSystemUpdateInfoLocked;
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0023, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0024, code lost:
        if (r2 != null) goto L_0x0026;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002e, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.os.Bundle loadSystemUpdateInfoLocked() {
        /*
            r8 = this;
            java.lang.String r0 = "SystemUpdateManagerService"
            r1 = 0
            android.util.AtomicFile r2 = r8.mFile     // Catch:{ FileNotFoundException -> 0x003d, XmlPullParserException -> 0x0036, IOException -> 0x002f }
            java.io.FileInputStream r2 = r2.openRead()     // Catch:{ FileNotFoundException -> 0x003d, XmlPullParserException -> 0x0036, IOException -> 0x002f }
            org.xmlpull.v1.XmlPullParser r3 = android.util.Xml.newPullParser()     // Catch:{ all -> 0x0021 }
            java.nio.charset.Charset r4 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ all -> 0x0021 }
            java.lang.String r4 = r4.name()     // Catch:{ all -> 0x0021 }
            r3.setInput(r2, r4)     // Catch:{ all -> 0x0021 }
            android.os.PersistableBundle r4 = r8.readInfoFileLocked(r3)     // Catch:{ all -> 0x0021 }
            r1 = r4
            if (r2 == 0) goto L_0x0058
            r2.close()     // Catch:{ FileNotFoundException -> 0x003d, XmlPullParserException -> 0x0036, IOException -> 0x002f }
            goto L_0x0058
        L_0x0021:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0023 }
        L_0x0023:
            r4 = move-exception
            if (r2 == 0) goto L_0x002e
            r2.close()     // Catch:{ all -> 0x002a }
            goto L_0x002e
        L_0x002a:
            r5 = move-exception
            r3.addSuppressed(r5)     // Catch:{ FileNotFoundException -> 0x003d, XmlPullParserException -> 0x0036, IOException -> 0x002f }
        L_0x002e:
            throw r4     // Catch:{ FileNotFoundException -> 0x003d, XmlPullParserException -> 0x0036, IOException -> 0x002f }
        L_0x002f:
            r2 = move-exception
            java.lang.String r3 = "Failed to read the info file:"
            android.util.Slog.e(r0, r3, r2)
            goto L_0x0059
        L_0x0036:
            r2 = move-exception
            java.lang.String r3 = "Failed to parse the info file:"
            android.util.Slog.e(r0, r3, r2)
            goto L_0x0058
        L_0x003d:
            r2 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "No existing info file "
            r3.append(r4)
            android.util.AtomicFile r4 = r8.mFile
            java.io.File r4 = r4.getBaseFile()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            android.util.Slog.i(r0, r3)
        L_0x0058:
        L_0x0059:
            if (r1 != 0) goto L_0x0060
            android.os.Bundle r0 = r8.removeInfoFileAndGetDefaultInfoBundleLocked()
            return r0
        L_0x0060:
            r2 = -1
            java.lang.String r3 = "version"
            int r3 = r1.getInt(r3, r2)
            if (r3 != r2) goto L_0x0074
            java.lang.String r2 = "Invalid info file (invalid version). Ignored"
            android.util.Slog.w(r0, r2)
            android.os.Bundle r0 = r8.removeInfoFileAndGetDefaultInfoBundleLocked()
            return r0
        L_0x0074:
            java.lang.String r4 = "uid"
            int r4 = r1.getInt(r4, r2)
            if (r4 != r2) goto L_0x0087
            java.lang.String r2 = "Invalid info file (invalid UID). Ignored"
            android.util.Slog.w(r0, r2)
            android.os.Bundle r0 = r8.removeInfoFileAndGetDefaultInfoBundleLocked()
            return r0
        L_0x0087:
            java.lang.String r5 = "boot-count"
            int r5 = r1.getInt(r5, r2)
            if (r5 == r2) goto L_0x00c7
            int r2 = r8.getBootCount()
            if (r5 == r2) goto L_0x0096
            goto L_0x00c7
        L_0x0096:
            java.lang.String r2 = "info-bundle"
            android.os.PersistableBundle r2 = r1.getPersistableBundle(r2)
            if (r2 != 0) goto L_0x00a9
            java.lang.String r6 = "Invalid info file (missing info). Ignored"
            android.util.Slog.w(r0, r6)
            android.os.Bundle r0 = r8.removeInfoFileAndGetDefaultInfoBundleLocked()
            return r0
        L_0x00a9:
            r6 = 0
            java.lang.String r7 = "status"
            int r6 = r2.getInt(r7, r6)
            if (r6 != 0) goto L_0x00bd
            java.lang.String r7 = "Invalid info file (invalid status). Ignored"
            android.util.Slog.w(r0, r7)
            android.os.Bundle r0 = r8.removeInfoFileAndGetDefaultInfoBundleLocked()
            return r0
        L_0x00bd:
            r8.mLastStatus = r6
            r8.mLastUid = r4
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>(r2)
            return r0
        L_0x00c7:
            java.lang.String r2 = "Outdated info file. Ignored"
            android.util.Slog.w(r0, r2)
            android.os.Bundle r0 = r8.removeInfoFileAndGetDefaultInfoBundleLocked()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.SystemUpdateManagerService.loadSystemUpdateInfoLocked():android.os.Bundle");
    }

    private void saveSystemUpdateInfoLocked(PersistableBundle infoBundle, int uid) {
        PersistableBundle outBundle = new PersistableBundle();
        outBundle.putPersistableBundle(KEY_INFO_BUNDLE, infoBundle);
        outBundle.putInt(KEY_VERSION, 0);
        outBundle.putInt("uid", uid);
        outBundle.putInt(KEY_BOOT_COUNT, getBootCount());
        if (writeInfoFileLocked(outBundle)) {
            this.mLastUid = uid;
            this.mLastStatus = infoBundle.getInt("status");
        }
    }

    private PersistableBundle readInfoFileLocked(XmlPullParser parser) throws XmlPullParserException, IOException {
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                return null;
            }
            if (type == 2 && TAG_INFO.equals(parser.getName())) {
                return PersistableBundle.restoreFromXml(parser);
            }
        }
    }

    private boolean writeInfoFileLocked(PersistableBundle outBundle) {
        FileOutputStream fos = null;
        try {
            fos = this.mFile.startWrite();
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(fos, StandardCharsets.UTF_8.name());
            out.startDocument((String) null, true);
            out.startTag((String) null, TAG_INFO);
            outBundle.saveToXml(out);
            out.endTag((String) null, TAG_INFO);
            out.endDocument();
            this.mFile.finishWrite(fos);
            return true;
        } catch (IOException | XmlPullParserException e) {
            Slog.e(TAG, "Failed to save the info file:", e);
            if (fos == null) {
                return false;
            }
            this.mFile.failWrite(fos);
            return false;
        }
    }

    private Bundle removeInfoFileAndGetDefaultInfoBundleLocked() {
        if (this.mFile.exists()) {
            Slog.i(TAG, "Removing info file");
            this.mFile.delete();
        }
        this.mLastStatus = 0;
        this.mLastUid = -1;
        Bundle infoBundle = new Bundle();
        infoBundle.putInt("status", 0);
        return infoBundle;
    }

    private int getBootCount() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "boot_count", 0);
    }
}
