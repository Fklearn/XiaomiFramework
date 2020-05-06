package com.android.server.role;

import android.os.Environment;
import android.os.Handler;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.CollectionUtils;
import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.internal.util.function.pooled.PooledLambda;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class RoleUserState {
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_PACKAGES_HASH = "packagesHash";
    private static final String ATTRIBUTE_VERSION = "version";
    private static final String LOG_TAG = RoleUserState.class.getSimpleName();
    private static final String ROLES_FILE_NAME = "roles.xml";
    private static final String TAG_HOLDER = "holder";
    private static final String TAG_ROLE = "role";
    private static final String TAG_ROLES = "roles";
    public static final int VERSION_UNDEFINED = -1;
    private static final long WRITE_DELAY_MILLIS = 200;
    private final Callback mCallback;
    @GuardedBy({"mLock"})
    private boolean mDestroyed;
    private final Object mLock = new Object();
    @GuardedBy({"mLock"})
    private String mPackagesHash;
    @GuardedBy({"mLock"})
    private ArrayMap<String, ArraySet<String>> mRoles = new ArrayMap<>();
    private final int mUserId;
    @GuardedBy({"mLock"})
    private int mVersion = -1;
    private final Handler mWriteHandler = new Handler(BackgroundThread.getHandler().getLooper());
    @GuardedBy({"mLock"})
    private boolean mWriteScheduled;

    public interface Callback {
        void onRoleHoldersChanged(String str, int i, String str2, String str3);
    }

    public RoleUserState(int userId, Callback callback) {
        this.mUserId = userId;
        this.mCallback = callback;
        readFile();
    }

    public int getVersion() {
        int i;
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            i = this.mVersion;
        }
        return i;
    }

    public void setVersion(int version) {
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            if (this.mVersion != version) {
                this.mVersion = version;
                scheduleWriteFileLocked();
            }
        }
    }

    public String getPackagesHash() {
        String str;
        synchronized (this.mLock) {
            str = this.mPackagesHash;
        }
        return str;
    }

    public void setPackagesHash(String packagesHash) {
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            if (!Objects.equals(this.mPackagesHash, packagesHash)) {
                this.mPackagesHash = packagesHash;
                scheduleWriteFileLocked();
            }
        }
    }

    public boolean isRoleAvailable(String roleName) {
        boolean containsKey;
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            containsKey = this.mRoles.containsKey(roleName);
        }
        return containsKey;
    }

    public ArraySet<String> getRoleHolders(String roleName) {
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            ArraySet<String> packageNames = this.mRoles.get(roleName);
            if (packageNames == null) {
                return null;
            }
            ArraySet<String> arraySet = new ArraySet<>(packageNames);
            return arraySet;
        }
    }

    public boolean addRoleName(String roleName) {
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            if (this.mRoles.containsKey(roleName)) {
                return false;
            }
            this.mRoles.put(roleName, new ArraySet());
            String str = LOG_TAG;
            Slog.i(str, "Added new role: " + roleName);
            scheduleWriteFileLocked();
            return true;
        }
    }

    public void setRoleNames(List<String> roleNames) {
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            boolean changed = false;
            for (int i = this.mRoles.size() - 1; i >= 0; i--) {
                String roleName = this.mRoles.keyAt(i);
                if (!roleNames.contains(roleName)) {
                    ArraySet<String> packageNames = this.mRoles.valueAt(i);
                    if (!packageNames.isEmpty()) {
                        Slog.e(LOG_TAG, "Holders of a removed role should have been cleaned up, role: " + roleName + ", holders: " + packageNames);
                    }
                    this.mRoles.removeAt(i);
                    changed = true;
                }
            }
            int i2 = roleNames.size();
            for (int i3 = 0; i3 < i2; i3++) {
                changed |= addRoleName(roleNames.get(i3));
            }
            if (changed) {
                scheduleWriteFileLocked();
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003b, code lost:
        if (r2 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x003d, code lost:
        r5.mCallback.onRoleHoldersChanged(r6, r5.mUserId, (java.lang.String) null, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean addRoleHolder(java.lang.String r6, java.lang.String r7) {
        /*
            r5 = this;
            java.lang.Object r0 = r5.mLock
            monitor-enter(r0)
            r5.throwIfDestroyedLocked()     // Catch:{ all -> 0x0047 }
            android.util.ArrayMap<java.lang.String, android.util.ArraySet<java.lang.String>> r1 = r5.mRoles     // Catch:{ all -> 0x0047 }
            java.lang.Object r1 = r1.get(r6)     // Catch:{ all -> 0x0047 }
            android.util.ArraySet r1 = (android.util.ArraySet) r1     // Catch:{ all -> 0x0047 }
            if (r1 != 0) goto L_0x0031
            java.lang.String r2 = LOG_TAG     // Catch:{ all -> 0x0047 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0047 }
            r3.<init>()     // Catch:{ all -> 0x0047 }
            java.lang.String r4 = "Cannot add role holder for unknown role, role: "
            r3.append(r4)     // Catch:{ all -> 0x0047 }
            r3.append(r6)     // Catch:{ all -> 0x0047 }
            java.lang.String r4 = ", package: "
            r3.append(r4)     // Catch:{ all -> 0x0047 }
            r3.append(r7)     // Catch:{ all -> 0x0047 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0047 }
            android.util.Slog.e(r2, r3)     // Catch:{ all -> 0x0047 }
            r2 = 0
            monitor-exit(r0)     // Catch:{ all -> 0x0047 }
            return r2
        L_0x0031:
            boolean r2 = r1.add(r7)     // Catch:{ all -> 0x0047 }
            if (r2 == 0) goto L_0x003a
            r5.scheduleWriteFileLocked()     // Catch:{ all -> 0x0047 }
        L_0x003a:
            monitor-exit(r0)     // Catch:{ all -> 0x0047 }
            if (r2 == 0) goto L_0x0045
            com.android.server.role.RoleUserState$Callback r0 = r5.mCallback
            int r1 = r5.mUserId
            r3 = 0
            r0.onRoleHoldersChanged(r6, r1, r3, r7)
        L_0x0045:
            r0 = 1
            return r0
        L_0x0047:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0047 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.role.RoleUserState.addRoleHolder(java.lang.String, java.lang.String):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003b, code lost:
        if (r2 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x003d, code lost:
        r5.mCallback.onRoleHoldersChanged(r6, r5.mUserId, r7, (java.lang.String) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean removeRoleHolder(java.lang.String r6, java.lang.String r7) {
        /*
            r5 = this;
            java.lang.Object r0 = r5.mLock
            monitor-enter(r0)
            r5.throwIfDestroyedLocked()     // Catch:{ all -> 0x0047 }
            android.util.ArrayMap<java.lang.String, android.util.ArraySet<java.lang.String>> r1 = r5.mRoles     // Catch:{ all -> 0x0047 }
            java.lang.Object r1 = r1.get(r6)     // Catch:{ all -> 0x0047 }
            android.util.ArraySet r1 = (android.util.ArraySet) r1     // Catch:{ all -> 0x0047 }
            if (r1 != 0) goto L_0x0031
            java.lang.String r2 = LOG_TAG     // Catch:{ all -> 0x0047 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0047 }
            r3.<init>()     // Catch:{ all -> 0x0047 }
            java.lang.String r4 = "Cannot remove role holder for unknown role, role: "
            r3.append(r4)     // Catch:{ all -> 0x0047 }
            r3.append(r6)     // Catch:{ all -> 0x0047 }
            java.lang.String r4 = ", package: "
            r3.append(r4)     // Catch:{ all -> 0x0047 }
            r3.append(r7)     // Catch:{ all -> 0x0047 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0047 }
            android.util.Slog.e(r2, r3)     // Catch:{ all -> 0x0047 }
            r2 = 0
            monitor-exit(r0)     // Catch:{ all -> 0x0047 }
            return r2
        L_0x0031:
            boolean r2 = r1.remove(r7)     // Catch:{ all -> 0x0047 }
            if (r2 == 0) goto L_0x003a
            r5.scheduleWriteFileLocked()     // Catch:{ all -> 0x0047 }
        L_0x003a:
            monitor-exit(r0)     // Catch:{ all -> 0x0047 }
            if (r2 == 0) goto L_0x0045
            com.android.server.role.RoleUserState$Callback r0 = r5.mCallback
            int r1 = r5.mUserId
            r3 = 0
            r0.onRoleHoldersChanged(r6, r1, r7, r3)
        L_0x0045:
            r0 = 1
            return r0
        L_0x0047:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0047 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.role.RoleUserState.removeRoleHolder(java.lang.String, java.lang.String):boolean");
    }

    public List<String> getHeldRoles(String packageName) {
        ArrayList<String> result = new ArrayList<>();
        int size = this.mRoles.size();
        for (int i = 0; i < size; i++) {
            if (this.mRoles.valueAt(i).contains(packageName)) {
                result.add(this.mRoles.keyAt(i));
            }
        }
        return result;
    }

    @GuardedBy({"mLock"})
    private void scheduleWriteFileLocked() {
        throwIfDestroyedLocked();
        if (!this.mWriteScheduled) {
            this.mWriteHandler.sendMessageDelayed(PooledLambda.obtainMessage($$Lambda$RoleUserState$e8W_Zaq_FyocW_DX1qcbN0ld0co.INSTANCE, this), WRITE_DELAY_MILLIS);
            this.mWriteScheduled = true;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r4 = r0.startWrite();
        r5 = android.util.Xml.newSerializer();
        r5.setOutput(r4, java.nio.charset.StandardCharsets.UTF_8.name());
        r5.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        r5.startDocument((java.lang.String) null, true);
        serializeRoles(r5, r1, r2, r3);
        r5.endDocument();
        r0.finishWrite(r4);
        android.util.Slog.i(LOG_TAG, "Wrote roles.xml successfully");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0068, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x006a, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        android.util.Slog.wtf(LOG_TAG, "Failed to write roles.xml, restoring backup", r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0072, code lost:
        if (r4 != null) goto L_0x0074;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0074, code lost:
        r0.failWrite(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0078, code lost:
        libcore.io.IoUtils.closeQuietly(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x007c, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x007d, code lost:
        libcore.io.IoUtils.closeQuietly(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0080, code lost:
        throw r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0015, code lost:
        r4 = getFile(r8.mUserId);
        r0 = new android.util.AtomicFile(r4, "roles-" + r8.mUserId);
        r4 = null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writeFile() {
        /*
            r8 = this;
            java.lang.Object r0 = r8.mLock
            monitor-enter(r0)
            boolean r1 = r8.mDestroyed     // Catch:{ all -> 0x0081 }
            if (r1 == 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0081 }
            return
        L_0x0009:
            r1 = 0
            r8.mWriteScheduled = r1     // Catch:{ all -> 0x0081 }
            int r1 = r8.mVersion     // Catch:{ all -> 0x0081 }
            java.lang.String r2 = r8.mPackagesHash     // Catch:{ all -> 0x0081 }
            android.util.ArrayMap r3 = r8.snapshotRolesLocked()     // Catch:{ all -> 0x0081 }
            monitor-exit(r0)     // Catch:{ all -> 0x0081 }
            android.util.AtomicFile r0 = new android.util.AtomicFile
            int r4 = r8.mUserId
            java.io.File r4 = getFile(r4)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "roles-"
            r5.append(r6)
            int r6 = r8.mUserId
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r0.<init>(r4, r5)
            r4 = 0
            java.io.FileOutputStream r5 = r0.startWrite()     // Catch:{ IOException | IllegalArgumentException | IllegalStateException -> 0x006a }
            r4 = r5
            org.xmlpull.v1.XmlSerializer r5 = android.util.Xml.newSerializer()     // Catch:{ IOException | IllegalArgumentException | IllegalStateException -> 0x006a }
            java.nio.charset.Charset r6 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException | IllegalArgumentException | IllegalStateException -> 0x006a }
            java.lang.String r6 = r6.name()     // Catch:{ IOException | IllegalArgumentException | IllegalStateException -> 0x006a }
            r5.setOutput(r4, r6)     // Catch:{ IOException | IllegalArgumentException | IllegalStateException -> 0x006a }
            java.lang.String r6 = "http://xmlpull.org/v1/doc/features.html#indent-output"
            r7 = 1
            r5.setFeature(r6, r7)     // Catch:{ IOException | IllegalArgumentException | IllegalStateException -> 0x006a }
            r6 = 0
            java.lang.Boolean r7 = java.lang.Boolean.valueOf(r7)     // Catch:{ IOException | IllegalArgumentException | IllegalStateException -> 0x006a }
            r5.startDocument(r6, r7)     // Catch:{ IOException | IllegalArgumentException | IllegalStateException -> 0x006a }
            r8.serializeRoles(r5, r1, r2, r3)     // Catch:{ IOException | IllegalArgumentException | IllegalStateException -> 0x006a }
            r5.endDocument()     // Catch:{ IOException | IllegalArgumentException | IllegalStateException -> 0x006a }
            r0.finishWrite(r4)     // Catch:{ IOException | IllegalArgumentException | IllegalStateException -> 0x006a }
            java.lang.String r6 = LOG_TAG     // Catch:{ IOException | IllegalArgumentException | IllegalStateException -> 0x006a }
            java.lang.String r7 = "Wrote roles.xml successfully"
            android.util.Slog.i(r6, r7)     // Catch:{ IOException | IllegalArgumentException | IllegalStateException -> 0x006a }
            goto L_0x0078
        L_0x0068:
            r5 = move-exception
            goto L_0x007d
        L_0x006a:
            r5 = move-exception
            java.lang.String r6 = LOG_TAG     // Catch:{ all -> 0x0068 }
            java.lang.String r7 = "Failed to write roles.xml, restoring backup"
            android.util.Slog.wtf(r6, r7, r5)     // Catch:{ all -> 0x0068 }
            if (r4 == 0) goto L_0x0077
            r0.failWrite(r4)     // Catch:{ all -> 0x0068 }
        L_0x0077:
        L_0x0078:
            libcore.io.IoUtils.closeQuietly(r4)
            return
        L_0x007d:
            libcore.io.IoUtils.closeQuietly(r4)
            throw r5
        L_0x0081:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0081 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.role.RoleUserState.writeFile():void");
    }

    private void serializeRoles(XmlSerializer serializer, int version, String packagesHash, ArrayMap<String, ArraySet<String>> roles) throws IOException {
        serializer.startTag((String) null, TAG_ROLES);
        serializer.attribute((String) null, ATTRIBUTE_VERSION, Integer.toString(version));
        if (packagesHash != null) {
            serializer.attribute((String) null, ATTRIBUTE_PACKAGES_HASH, packagesHash);
        }
        int size = roles.size();
        for (int i = 0; i < size; i++) {
            serializer.startTag((String) null, TAG_ROLE);
            serializer.attribute((String) null, "name", roles.keyAt(i));
            serializeRoleHolders(serializer, roles.valueAt(i));
            serializer.endTag((String) null, TAG_ROLE);
        }
        serializer.endTag((String) null, TAG_ROLES);
    }

    private void serializeRoleHolders(XmlSerializer serializer, ArraySet<String> roleHolders) throws IOException {
        int size = roleHolders.size();
        for (int i = 0; i < size; i++) {
            serializer.startTag((String) null, TAG_HOLDER);
            serializer.attribute((String) null, "name", roleHolders.valueAt(i));
            serializer.endTag((String) null, TAG_HOLDER);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002d, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002e, code lost:
        if (r2 != null) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0038, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void readFile() {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mLock
            monitor-enter(r0)
            int r1 = r6.mUserId     // Catch:{ all -> 0x005d }
            java.io.File r1 = getFile(r1)     // Catch:{ all -> 0x005d }
            android.util.AtomicFile r2 = new android.util.AtomicFile     // Catch:{ FileNotFoundException -> 0x0051, IOException | XmlPullParserException -> 0x0039 }
            r2.<init>(r1)     // Catch:{ FileNotFoundException -> 0x0051, IOException | XmlPullParserException -> 0x0039 }
            java.io.FileInputStream r2 = r2.openRead()     // Catch:{ FileNotFoundException -> 0x0051, IOException | XmlPullParserException -> 0x0039 }
            org.xmlpull.v1.XmlPullParser r3 = android.util.Xml.newPullParser()     // Catch:{ all -> 0x002b }
            r4 = 0
            r3.setInput(r2, r4)     // Catch:{ all -> 0x002b }
            r6.parseXmlLocked(r3)     // Catch:{ all -> 0x002b }
            java.lang.String r4 = LOG_TAG     // Catch:{ all -> 0x002b }
            java.lang.String r5 = "Read roles.xml successfully"
            android.util.Slog.i(r4, r5)     // Catch:{ all -> 0x002b }
            if (r2 == 0) goto L_0x002a
            r2.close()     // Catch:{ FileNotFoundException -> 0x0051, IOException | XmlPullParserException -> 0x0039 }
        L_0x002a:
            goto L_0x005b
        L_0x002b:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x002d }
        L_0x002d:
            r4 = move-exception
            if (r2 == 0) goto L_0x0038
            r2.close()     // Catch:{ all -> 0x0034 }
            goto L_0x0038
        L_0x0034:
            r5 = move-exception
            r3.addSuppressed(r5)     // Catch:{ FileNotFoundException -> 0x0051, IOException | XmlPullParserException -> 0x0039 }
        L_0x0038:
            throw r4     // Catch:{ FileNotFoundException -> 0x0051, IOException | XmlPullParserException -> 0x0039 }
        L_0x0039:
            r2 = move-exception
            java.lang.IllegalStateException r3 = new java.lang.IllegalStateException     // Catch:{ all -> 0x005d }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x005d }
            r4.<init>()     // Catch:{ all -> 0x005d }
            java.lang.String r5 = "Failed to parse roles.xml: "
            r4.append(r5)     // Catch:{ all -> 0x005d }
            r4.append(r1)     // Catch:{ all -> 0x005d }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x005d }
            r3.<init>(r4, r2)     // Catch:{ all -> 0x005d }
            throw r3     // Catch:{ all -> 0x005d }
        L_0x0051:
            r2 = move-exception
            java.lang.String r3 = LOG_TAG     // Catch:{ all -> 0x005d }
            java.lang.String r4 = "roles.xml not found"
            android.util.Slog.i(r3, r4)     // Catch:{ all -> 0x005d }
        L_0x005b:
            monitor-exit(r0)     // Catch:{ all -> 0x005d }
            return
        L_0x005d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x005d }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.role.RoleUserState.readFile():void");
    }

    private void parseXmlLocked(XmlPullParser parser) throws IOException, XmlPullParserException {
        int innerDepth = parser.getDepth() + 1;
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                break;
            }
            int depth = parser.getDepth();
            int depth2 = depth;
            if (depth < innerDepth && type == 3) {
                break;
            } else if (depth2 <= innerDepth && type == 2 && parser.getName().equals(TAG_ROLES)) {
                parseRolesLocked(parser);
                return;
            }
        }
        Slog.w(LOG_TAG, "Missing <roles> in roles.xml");
    }

    private void parseRolesLocked(XmlPullParser parser) throws IOException, XmlPullParserException {
        this.mVersion = Integer.parseInt(parser.getAttributeValue((String) null, ATTRIBUTE_VERSION));
        this.mPackagesHash = parser.getAttributeValue((String) null, ATTRIBUTE_PACKAGES_HASH);
        this.mRoles.clear();
        int innerDepth = parser.getDepth() + 1;
        while (true) {
            int next = parser.next();
            int type = next;
            if (next != 1) {
                int depth = parser.getDepth();
                int depth2 = depth;
                if (depth < innerDepth && type == 3) {
                    return;
                }
                if (depth2 <= innerDepth && type == 2 && parser.getName().equals(TAG_ROLE)) {
                    this.mRoles.put(parser.getAttributeValue((String) null, "name"), parseRoleHoldersLocked(parser));
                }
            } else {
                return;
            }
        }
    }

    private ArraySet<String> parseRoleHoldersLocked(XmlPullParser parser) throws IOException, XmlPullParserException {
        ArraySet<String> roleHolders = new ArraySet<>();
        int innerDepth = parser.getDepth() + 1;
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                break;
            }
            int depth = parser.getDepth();
            int depth2 = depth;
            if (depth < innerDepth && type == 3) {
                break;
            } else if (depth2 <= innerDepth && type == 2 && parser.getName().equals(TAG_HOLDER)) {
                roleHolders.add(parser.getAttributeValue((String) null, "name"));
            }
        }
        return roleHolders;
    }

    public void dump(DualDumpOutputStream dumpOutputStream, String fieldName, long fieldId) {
        int version;
        String packagesHash;
        ArrayMap<String, ArraySet<String>> roles;
        DualDumpOutputStream dualDumpOutputStream = dumpOutputStream;
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            version = this.mVersion;
            packagesHash = this.mPackagesHash;
            roles = snapshotRolesLocked();
        }
        long fieldToken = dumpOutputStream.start(fieldName, fieldId);
        dualDumpOutputStream.write("user_id", 1120986464257L, this.mUserId);
        dualDumpOutputStream.write(ATTRIBUTE_VERSION, 1120986464258L, version);
        dualDumpOutputStream.write("packages_hash", 1138166333443L, packagesHash);
        int rolesSize = roles.size();
        int rolesIndex = 0;
        while (rolesIndex < rolesSize) {
            ArraySet<String> roleHolders = roles.valueAt(rolesIndex);
            long rolesToken = dualDumpOutputStream.start(TAG_ROLES, 2246267895812L);
            dualDumpOutputStream.write("name", 1138166333441L, roles.keyAt(rolesIndex));
            int roleHoldersSize = roleHolders.size();
            int roleHoldersIndex = 0;
            while (roleHoldersIndex < roleHoldersSize) {
                dualDumpOutputStream.write("holders", 2237677961218L, roleHolders.valueAt(roleHoldersIndex));
                roleHoldersIndex++;
                version = version;
                rolesSize = rolesSize;
            }
            int i = rolesSize;
            dualDumpOutputStream.end(rolesToken);
            rolesIndex++;
        }
        dualDumpOutputStream.end(fieldToken);
    }

    public ArrayMap<String, ArraySet<String>> getRolesAndHolders() {
        ArrayMap<String, ArraySet<String>> snapshotRolesLocked;
        synchronized (this.mLock) {
            snapshotRolesLocked = snapshotRolesLocked();
        }
        return snapshotRolesLocked;
    }

    @GuardedBy({"mLock"})
    private ArrayMap<String, ArraySet<String>> snapshotRolesLocked() {
        ArrayMap<String, ArraySet<String>> roles = new ArrayMap<>();
        int size = CollectionUtils.size(this.mRoles);
        for (int i = 0; i < size; i++) {
            roles.put(this.mRoles.keyAt(i), new ArraySet(this.mRoles.valueAt(i)));
        }
        return roles;
    }

    public void destroy() {
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            this.mWriteHandler.removeCallbacksAndMessages((Object) null);
            getFile(this.mUserId).delete();
            this.mDestroyed = true;
        }
    }

    @GuardedBy({"mLock"})
    private void throwIfDestroyedLocked() {
        if (this.mDestroyed) {
            throw new IllegalStateException("This RoleUserState has already been destroyed");
        }
    }

    private static File getFile(int userId) {
        return new File(Environment.getUserSystemDirectory(userId), ROLES_FILE_NAME);
    }
}
