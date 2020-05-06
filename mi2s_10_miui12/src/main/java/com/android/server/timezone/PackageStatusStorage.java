package com.android.server.timezone;

import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.FastXmlSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

final class PackageStatusStorage {
    private static final String ATTRIBUTE_CHECK_STATUS = "checkStatus";
    private static final String ATTRIBUTE_DATA_APP_VERSION = "dataAppPackageVersion";
    private static final String ATTRIBUTE_OPTIMISTIC_LOCK_ID = "optimisticLockId";
    private static final String ATTRIBUTE_UPDATE_APP_VERSION = "updateAppPackageVersion";
    private static final String LOG_TAG = "timezone.PackageStatusStorage";
    private static final String TAG_PACKAGE_STATUS = "PackageStatus";
    private static final long UNKNOWN_PACKAGE_VERSION = -1;
    private final AtomicFile mPackageStatusFile;

    PackageStatusStorage(File storageDir) {
        this.mPackageStatusFile = new AtomicFile(new File(storageDir, "package-status.xml"), "timezone-status");
    }

    /* access modifiers changed from: package-private */
    public void initialize() throws IOException {
        if (!this.mPackageStatusFile.getBaseFile().exists()) {
            insertInitialPackageStatus();
        }
    }

    /* access modifiers changed from: package-private */
    public void deleteFileForTests() {
        synchronized (this) {
            this.mPackageStatusFile.delete();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: package-private */
    public PackageStatus getPackageStatus() {
        PackageStatus packageStatusLocked;
        synchronized (this) {
            try {
                packageStatusLocked = getPackageStatusLocked();
            } catch (ParseException e2) {
                throw new IllegalStateException("Recovery from bad file failed", e2);
            } catch (ParseException e) {
                Slog.e(LOG_TAG, "Package status invalid, resetting and retrying", e);
                recoverFromBadData(e);
                return getPackageStatusLocked();
            } catch (Throwable th) {
                throw th;
            }
        }
        return packageStatusLocked;
    }

    /* Debug info: failed to restart local var, previous not found, register: 13 */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x003f, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0040, code lost:
        if (r0 != null) goto L_0x0042;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        $closeResource(r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0045, code lost:
        throw r2;
     */
    @com.android.internal.annotations.GuardedBy({"this"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.android.server.timezone.PackageStatus getPackageStatusLocked() throws java.text.ParseException {
        /*
            r13 = this;
            android.util.AtomicFile r0 = r13.mPackageStatusFile     // Catch:{ IOException -> 0x0046 }
            java.io.FileInputStream r0 = r0.openRead()     // Catch:{ IOException -> 0x0046 }
            org.xmlpull.v1.XmlPullParser r1 = parseToPackageStatusTag(r0)     // Catch:{ all -> 0x003d }
            java.lang.String r2 = "checkStatus"
            java.lang.Integer r2 = getNullableIntAttribute(r1, r2)     // Catch:{ all -> 0x003d }
            r3 = 0
            if (r2 != 0) goto L_0x001a
            if (r0 == 0) goto L_0x0019
            $closeResource(r3, r0)     // Catch:{ IOException -> 0x0046 }
        L_0x0019:
            return r3
        L_0x001a:
            java.lang.String r4 = "updateAppPackageVersion"
            int r4 = getIntAttribute(r1, r4)     // Catch:{ all -> 0x003d }
            java.lang.String r5 = "dataAppPackageVersion"
            int r5 = getIntAttribute(r1, r5)     // Catch:{ all -> 0x003d }
            com.android.server.timezone.PackageStatus r6 = new com.android.server.timezone.PackageStatus     // Catch:{ all -> 0x003d }
            int r7 = r2.intValue()     // Catch:{ all -> 0x003d }
            com.android.server.timezone.PackageVersions r8 = new com.android.server.timezone.PackageVersions     // Catch:{ all -> 0x003d }
            long r9 = (long) r4     // Catch:{ all -> 0x003d }
            long r11 = (long) r5     // Catch:{ all -> 0x003d }
            r8.<init>(r9, r11)     // Catch:{ all -> 0x003d }
            r6.<init>(r7, r8)     // Catch:{ all -> 0x003d }
            if (r0 == 0) goto L_0x003c
            $closeResource(r3, r0)     // Catch:{ IOException -> 0x0046 }
        L_0x003c:
            return r6
        L_0x003d:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x003f }
        L_0x003f:
            r2 = move-exception
            if (r0 == 0) goto L_0x0045
            $closeResource(r1, r0)     // Catch:{ IOException -> 0x0046 }
        L_0x0045:
            throw r2     // Catch:{ IOException -> 0x0046 }
        L_0x0046:
            r0 = move-exception
            java.text.ParseException r1 = new java.text.ParseException
            r2 = 0
            java.lang.String r3 = "Error reading package status"
            r1.<init>(r3, r2)
            r1.initCause(r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.timezone.PackageStatusStorage.getPackageStatusLocked():com.android.server.timezone.PackageStatus");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        } else {
            x1.close();
        }
    }

    @GuardedBy({"this"})
    private int recoverFromBadData(Exception cause) {
        this.mPackageStatusFile.delete();
        try {
            return insertInitialPackageStatus();
        } catch (IOException e) {
            IllegalStateException fatal = new IllegalStateException(e);
            fatal.addSuppressed(cause);
            throw fatal;
        }
    }

    private int insertInitialPackageStatus() throws IOException {
        int initialOptimisticLockId = (int) System.currentTimeMillis();
        writePackageStatusLocked((Integer) null, initialOptimisticLockId, (PackageVersions) null);
        return initialOptimisticLockId;
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    /* access modifiers changed from: package-private */
    public CheckToken generateCheckToken(PackageVersions currentInstalledVersions) {
        int optimisticLockId;
        CheckToken checkToken;
        if (currentInstalledVersions != null) {
            synchronized (this) {
                try {
                    optimisticLockId = getCurrentOptimisticLockId();
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                } catch (ParseException e2) {
                    Slog.w(LOG_TAG, "Unable to find optimistic lock ID from package status");
                    optimisticLockId = recoverFromBadData(e2);
                }
                int newOptimisticLockId = optimisticLockId + 1;
                if (writePackageStatusWithOptimisticLockCheck(optimisticLockId, newOptimisticLockId, 1, currentInstalledVersions)) {
                    checkToken = new CheckToken(newOptimisticLockId, currentInstalledVersions);
                } else {
                    throw new IllegalStateException("Unable to update status to CHECK_STARTED. synchronization failure?");
                }
            }
            return checkToken;
        }
        throw new NullPointerException("currentInstalledVersions == null");
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    /* access modifiers changed from: package-private */
    public void resetCheckState() {
        int optimisticLockId;
        synchronized (this) {
            try {
                optimisticLockId = getCurrentOptimisticLockId();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            } catch (ParseException e2) {
                Slog.w(LOG_TAG, "resetCheckState: Unable to find optimistic lock ID from package status");
                optimisticLockId = recoverFromBadData(e2);
            }
            int newOptimisticLockId = optimisticLockId + 1;
            if (!writePackageStatusWithOptimisticLockCheck(optimisticLockId, newOptimisticLockId, (Integer) null, (PackageVersions) null)) {
                throw new IllegalStateException("resetCheckState: Unable to reset package status, newOptimisticLockId=" + newOptimisticLockId);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    /* access modifiers changed from: package-private */
    public boolean markChecked(CheckToken checkToken, boolean succeeded) {
        boolean writePackageStatusWithOptimisticLockCheck;
        synchronized (this) {
            int optimisticLockId = checkToken.mOptimisticLockId;
            try {
                writePackageStatusWithOptimisticLockCheck = writePackageStatusWithOptimisticLockCheck(optimisticLockId, optimisticLockId + 1, Integer.valueOf(succeeded ? 2 : 3), checkToken.mPackageVersions);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return writePackageStatusWithOptimisticLockCheck;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001a, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001b, code lost:
        if (r0 != null) goto L_0x001d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        $closeResource(r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0020, code lost:
        throw r2;
     */
    @com.android.internal.annotations.GuardedBy({"this"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getCurrentOptimisticLockId() throws java.text.ParseException {
        /*
            r4 = this;
            android.util.AtomicFile r0 = r4.mPackageStatusFile     // Catch:{ IOException -> 0x0021 }
            java.io.FileInputStream r0 = r0.openRead()     // Catch:{ IOException -> 0x0021 }
            r1 = 0
            org.xmlpull.v1.XmlPullParser r2 = parseToPackageStatusTag(r0)     // Catch:{ all -> 0x0018 }
            java.lang.String r3 = "optimisticLockId"
            int r3 = getIntAttribute(r2, r3)     // Catch:{ all -> 0x0018 }
            if (r0 == 0) goto L_0x0017
            $closeResource(r1, r0)     // Catch:{ IOException -> 0x0021 }
        L_0x0017:
            return r3
        L_0x0018:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x001a }
        L_0x001a:
            r2 = move-exception
            if (r0 == 0) goto L_0x0020
            $closeResource(r1, r0)     // Catch:{ IOException -> 0x0021 }
        L_0x0020:
            throw r2     // Catch:{ IOException -> 0x0021 }
        L_0x0021:
            r0 = move-exception
            java.text.ParseException r1 = new java.text.ParseException
            r2 = 0
            java.lang.String r3 = "Unable to read file"
            r1.<init>(r3, r2)
            r1.initCause(r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.timezone.PackageStatusStorage.getCurrentOptimisticLockId():int");
    }

    private static XmlPullParser parseToPackageStatusTag(FileInputStream fis) throws ParseException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, StandardCharsets.UTF_8.name());
            while (true) {
                int next = parser.next();
                int type = next;
                if (next != 1) {
                    String tag = parser.getName();
                    if (type == 2 && TAG_PACKAGE_STATUS.equals(tag)) {
                        return parser;
                    }
                } else {
                    throw new ParseException("Unable to find PackageStatus tag", 0);
                }
            }
        } catch (XmlPullParserException e) {
            throw new IllegalStateException("Unable to configure parser", e);
        } catch (IOException e2) {
            ParseException e22 = new ParseException("Error reading XML", 0);
            e2.initCause(e2);
            throw e22;
        }
    }

    @GuardedBy({"this"})
    private boolean writePackageStatusWithOptimisticLockCheck(int optimisticLockId, int newOptimisticLockId, Integer status, PackageVersions packageVersions) throws IOException {
        try {
            if (getCurrentOptimisticLockId() != optimisticLockId) {
                return false;
            }
            writePackageStatusLocked(status, newOptimisticLockId, packageVersions);
            return true;
        } catch (ParseException e) {
            recoverFromBadData(e);
            return false;
        }
    }

    @GuardedBy({"this"})
    private void writePackageStatusLocked(Integer status, int optimisticLockId, PackageVersions packageVersions) throws IOException {
        boolean z = false;
        boolean z2 = status == null;
        if (packageVersions == null) {
            z = true;
        }
        if (z2 == z) {
            try {
                FileOutputStream fos = this.mPackageStatusFile.startWrite();
                XmlSerializer serializer = new FastXmlSerializer();
                serializer.setOutput(fos, StandardCharsets.UTF_8.name());
                serializer.startDocument((String) null, true);
                serializer.startTag((String) null, TAG_PACKAGE_STATUS);
                serializer.attribute((String) null, ATTRIBUTE_CHECK_STATUS, status == null ? "" : Integer.toString(status.intValue()));
                serializer.attribute((String) null, ATTRIBUTE_OPTIMISTIC_LOCK_ID, Integer.toString(optimisticLockId));
                long dataAppVersion = -1;
                serializer.attribute((String) null, ATTRIBUTE_UPDATE_APP_VERSION, Long.toString(status == null ? -1 : packageVersions.mUpdateAppVersion));
                if (status != null) {
                    dataAppVersion = packageVersions.mDataAppVersion;
                }
                serializer.attribute((String) null, ATTRIBUTE_DATA_APP_VERSION, Long.toString(dataAppVersion));
                serializer.endTag((String) null, TAG_PACKAGE_STATUS);
                serializer.endDocument();
                serializer.flush();
                this.mPackageStatusFile.finishWrite(fos);
            } catch (IOException e) {
                if (0 != 0) {
                    this.mPackageStatusFile.failWrite((FileOutputStream) null);
                }
                throw e;
            }
        } else {
            throw new IllegalArgumentException("Provide both status and packageVersions, or neither.");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 2 */
    public void forceCheckStateForTests(int checkStatus, PackageVersions packageVersions) throws IOException {
        synchronized (this) {
            try {
                writePackageStatusLocked(Integer.valueOf(checkStatus), (int) System.currentTimeMillis(), packageVersions);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private static Integer getNullableIntAttribute(XmlPullParser parser, String attributeName) throws ParseException {
        String attributeValue = parser.getAttributeValue((String) null, attributeName);
        if (attributeValue != null) {
            try {
                if (attributeValue.isEmpty()) {
                    return null;
                }
                return Integer.valueOf(Integer.parseInt(attributeValue));
            } catch (NumberFormatException e) {
                throw new ParseException("Bad integer for attributeName=" + attributeName + ": " + attributeValue, 0);
            }
        } else {
            throw new ParseException("Attribute " + attributeName + " missing", 0);
        }
    }

    private static int getIntAttribute(XmlPullParser parser, String attributeName) throws ParseException {
        Integer value = getNullableIntAttribute(parser, attributeName);
        if (value != null) {
            return value.intValue();
        }
        throw new ParseException("Missing attribute " + attributeName, 0);
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("Package status: " + getPackageStatus());
    }
}
