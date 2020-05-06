package com.android.server.uri;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.AppGlobals;
import android.app.GrantedUriPermission;
import android.app.IUriGrantsManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ParceledListSlice;
import android.content.pm.PathPermission;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.server.IoThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.SystemServiceManager;
import com.android.server.job.controllers.JobStatus;
import com.android.server.pm.DumpState;
import com.android.server.uri.UriPermission;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlSerializer;

public class UriGrantsManagerService extends IUriGrantsManager.Stub {
    private static final String ATTR_CREATED_TIME = "createdTime";
    private static final String ATTR_MODE_FLAGS = "modeFlags";
    private static final String ATTR_PREFIX = "prefix";
    private static final String ATTR_SOURCE_PKG = "sourcePkg";
    private static final String ATTR_SOURCE_USER_ID = "sourceUserId";
    private static final String ATTR_TARGET_PKG = "targetPkg";
    private static final String ATTR_TARGET_USER_ID = "targetUserId";
    private static final String ATTR_URI = "uri";
    private static final String ATTR_USER_HANDLE = "userHandle";
    private static final boolean DEBUG = false;
    private static final int MAX_PERSISTED_URI_GRANTS = 128;
    private static final String TAG = "UriGrantsManagerService";
    private static final String TAG_URI_GRANT = "uri-grant";
    private static final String TAG_URI_GRANTS = "uri-grants";
    ActivityManagerInternal mAmInternal;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final AtomicFile mGrantFile;
    /* access modifiers changed from: private */
    public final SparseArray<ArrayMap<GrantUri, UriPermission>> mGrantedUriPermissions;
    private final H mH;
    /* access modifiers changed from: private */
    public final Object mLock;
    PackageManagerInternal mPmInternal;

    private UriGrantsManagerService(Context context) {
        this.mLock = new Object();
        this.mGrantedUriPermissions = new SparseArray<>();
        this.mContext = context;
        this.mH = new H(IoThread.get().getLooper());
        this.mGrantFile = new AtomicFile(new File(SystemServiceManager.ensureSystemDir(), "urigrants.xml"), TAG_URI_GRANTS);
    }

    /* access modifiers changed from: private */
    public void start() {
        LocalServices.addService(UriGrantsManagerInternal.class, new LocalService());
    }

    /* access modifiers changed from: package-private */
    public void onActivityManagerInternalAdded() {
        this.mAmInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
    }

    public static final class Lifecycle extends SystemService {
        private final UriGrantsManagerService mService;

        public Lifecycle(Context context) {
            super(context);
            this.mService = new UriGrantsManagerService(context);
        }

        /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.uri.UriGrantsManagerService, android.os.IBinder] */
        public void onStart() {
            publishBinderService("uri_grants", this.mService);
            this.mService.start();
        }

        public UriGrantsManagerService getService() {
            return this.mService;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    public void grantUriPermissionFromOwner(IBinder token, int fromUid, String targetPkg, Uri uri, int modeFlags, int sourceUserId, int targetUserId) {
        Uri uri2 = uri;
        int targetUserId2 = this.mAmInternal.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), targetUserId, false, 2, "grantUriPermissionFromOwner", (String) null);
        synchronized (this.mLock) {
            try {
                UriPermissionOwner owner = UriPermissionOwner.fromExternalToken(token);
                if (owner != null) {
                    try {
                        if (fromUid != Binder.getCallingUid()) {
                            try {
                                if (Binder.getCallingUid() != Process.myUid()) {
                                    throw new SecurityException("nice try");
                                }
                            } catch (Throwable th) {
                                th = th;
                                IBinder iBinder = token;
                                int i = sourceUserId;
                                throw th;
                            }
                        }
                        if (targetPkg == null) {
                            int i2 = sourceUserId;
                            throw new IllegalArgumentException("null target");
                        } else if (uri2 != null) {
                            try {
                                grantUriPermission(fromUid, targetPkg, new GrantUri(sourceUserId, uri2, false), modeFlags, owner, targetUserId2);
                            } catch (Throwable th2) {
                                th = th2;
                                IBinder iBinder2 = token;
                                throw th;
                            }
                        } else {
                            int i3 = sourceUserId;
                            throw new IllegalArgumentException("null uri");
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        int i4 = fromUid;
                        int i5 = sourceUserId;
                        IBinder iBinder22 = token;
                        throw th;
                    }
                } else {
                    int i6 = fromUid;
                    int i7 = sourceUserId;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Unknown owner: ");
                    sb.append(token);
                    throw new IllegalArgumentException(sb.toString());
                }
            } catch (Throwable th4) {
                th = th4;
                throw th;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    public ParceledListSlice<UriPermission> getUriPermissions(String packageName, boolean incoming, boolean persistedOnly) {
        enforceNotIsolatedCaller("getUriPermissions");
        Preconditions.checkNotNull(packageName, "packageName");
        int callingUid = Binder.getCallingUid();
        try {
            if (AppGlobals.getPackageManager().getPackageUid(packageName, 786432, UserHandle.getUserId(callingUid)) == callingUid) {
                ArrayList<UriPermission> result = Lists.newArrayList();
                synchronized (this.mLock) {
                    if (incoming) {
                        ArrayMap<GrantUri, UriPermission> perms = this.mGrantedUriPermissions.get(callingUid);
                        if (perms == null) {
                            Slog.w(TAG, "No permission grants found for " + packageName);
                        } else {
                            for (int j = 0; j < perms.size(); j++) {
                                UriPermission perm = perms.valueAt(j);
                                if (packageName.equals(perm.targetPkg) && (!persistedOnly || perm.persistedModeFlags != 0)) {
                                    result.add(perm.buildPersistedPublicApiObject());
                                }
                            }
                        }
                    } else {
                        int size = this.mGrantedUriPermissions.size();
                        for (int i = 0; i < size; i++) {
                            ArrayMap<GrantUri, UriPermission> perms2 = this.mGrantedUriPermissions.valueAt(i);
                            for (int j2 = 0; j2 < perms2.size(); j2++) {
                                UriPermission perm2 = perms2.valueAt(j2);
                                if (packageName.equals(perm2.sourcePkg) && (!persistedOnly || perm2.persistedModeFlags != 0)) {
                                    result.add(perm2.buildPersistedPublicApiObject());
                                }
                            }
                        }
                    }
                }
                return new ParceledListSlice<>(result);
            }
            throw new SecurityException("Package " + packageName + " does not belong to calling UID " + callingUid);
        } catch (RemoteException e) {
            throw new SecurityException("Failed to verify package name ownership");
        }
    }

    public ParceledListSlice<GrantedUriPermission> getGrantedUriPermissions(String packageName, int userId) {
        this.mAmInternal.enforceCallingPermission("android.permission.GET_APP_GRANTED_URI_PERMISSIONS", "getGrantedUriPermissions");
        List<GrantedUriPermission> result = new ArrayList<>();
        synchronized (this.mLock) {
            int size = this.mGrantedUriPermissions.size();
            for (int i = 0; i < size; i++) {
                ArrayMap<GrantUri, UriPermission> perms = this.mGrantedUriPermissions.valueAt(i);
                for (int j = 0; j < perms.size(); j++) {
                    UriPermission perm = perms.valueAt(j);
                    if ((packageName == null || packageName.equals(perm.targetPkg)) && perm.targetUserId == userId && perm.persistedModeFlags != 0) {
                        result.add(perm.buildGrantedUriPermission());
                    }
                }
            }
        }
        return new ParceledListSlice<>(result);
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    public void takePersistableUriPermission(Uri uri, int modeFlags, String toPackage, int userId) {
        int uid;
        boolean prefixValid = false;
        if (toPackage != null) {
            this.mAmInternal.enforceCallingPermission("android.permission.FORCE_PERSISTABLE_URI_PERMISSIONS", "takePersistableUriPermission");
            uid = getPmInternal().getPackageUid(toPackage, 0, userId);
        } else {
            enforceNotIsolatedCaller("takePersistableUriPermission");
            uid = Binder.getCallingUid();
        }
        Preconditions.checkFlagsArgument(modeFlags, 3);
        synchronized (this.mLock) {
            boolean persistChanged = false;
            GrantUri grantUri = new GrantUri(userId, uri, false);
            UriPermission exactPerm = findUriPermissionLocked(uid, grantUri);
            UriPermission prefixPerm = findUriPermissionLocked(uid, new GrantUri(userId, uri, true));
            boolean exactValid = exactPerm != null && (exactPerm.persistableModeFlags & modeFlags) == modeFlags;
            if (prefixPerm != null && (prefixPerm.persistableModeFlags & modeFlags) == modeFlags) {
                prefixValid = true;
            }
            if (!exactValid) {
                if (!prefixValid) {
                    throw new SecurityException("No persistable permission grants found for UID " + uid + " and Uri " + grantUri.toSafeString());
                }
            }
            if (exactValid) {
                persistChanged = false | exactPerm.takePersistableModes(modeFlags);
            }
            if (prefixValid) {
                persistChanged |= prefixPerm.takePersistableModes(modeFlags);
            }
            if (persistChanged || maybePrunePersistedUriGrants(uid)) {
                schedulePersistUriGrants();
            }
        }
    }

    public void clearGrantedUriPermissions(String packageName, int userId) {
        this.mAmInternal.enforceCallingPermission("android.permission.CLEAR_APP_GRANTED_URI_PERMISSIONS", "clearGrantedUriPermissions");
        synchronized (this.mLock) {
            removeUriPermissionsForPackage(packageName, userId, true, true);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public void releasePersistableUriPermission(Uri uri, int modeFlags, String toPackage, int userId) {
        int uid;
        if (toPackage != null) {
            this.mAmInternal.enforceCallingPermission("android.permission.FORCE_PERSISTABLE_URI_PERMISSIONS", "releasePersistableUriPermission");
            uid = getPmInternal().getPackageUid(toPackage, 0, userId);
        } else {
            enforceNotIsolatedCaller("releasePersistableUriPermission");
            uid = Binder.getCallingUid();
        }
        Preconditions.checkFlagsArgument(modeFlags, 3);
        synchronized (this.mLock) {
            boolean persistChanged = false;
            UriPermission exactPerm = findUriPermissionLocked(uid, new GrantUri(userId, uri, false));
            UriPermission prefixPerm = findUriPermissionLocked(uid, new GrantUri(userId, uri, true));
            if (exactPerm == null && prefixPerm == null) {
                if (toPackage == null) {
                    throw new SecurityException("No permission grants found for UID " + uid + " and Uri " + uri.toSafeString());
                }
            }
            if (exactPerm != null) {
                persistChanged = false | exactPerm.releasePersistableModes(modeFlags);
                removeUriPermissionIfNeeded(exactPerm);
            }
            if (prefixPerm != null) {
                persistChanged |= prefixPerm.releasePersistableModes(modeFlags);
                removeUriPermissionIfNeeded(prefixPerm);
            }
            if (persistChanged) {
                schedulePersistUriGrants();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void removeUriPermissionsForPackage(String packageName, int userHandle, boolean persistable, boolean targetOnly) {
        if (userHandle == -1 && packageName == null) {
            throw new IllegalArgumentException("Must narrow by either package or user");
        }
        boolean persistChanged = false;
        int N = this.mGrantedUriPermissions.size();
        int i = 0;
        while (i < N) {
            int targetUid = this.mGrantedUriPermissions.keyAt(i);
            ArrayMap<GrantUri, UriPermission> perms = this.mGrantedUriPermissions.valueAt(i);
            if (userHandle == -1 || userHandle == UserHandle.getUserId(targetUid)) {
                Iterator<UriPermission> it = perms.values().iterator();
                while (it.hasNext()) {
                    UriPermission perm = it.next();
                    if ((packageName == null || ((!targetOnly && perm.sourcePkg.equals(packageName)) || perm.targetPkg.equals(packageName))) && (!"downloads".equals(perm.uri.uri.getAuthority()) || persistable)) {
                        persistChanged |= perm.revokeModes(persistable ? -1 : -65, true);
                        if (perm.modeFlags == 0) {
                            it.remove();
                        }
                    }
                }
                if (perms.isEmpty()) {
                    this.mGrantedUriPermissions.remove(targetUid);
                    N--;
                    i--;
                }
            }
            i++;
        }
        if (persistChanged) {
            schedulePersistUriGrants();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean checkAuthorityGrants(int callingUid, ProviderInfo cpi, int userId, boolean checkUser) {
        ArrayMap<GrantUri, UriPermission> perms = this.mGrantedUriPermissions.get(callingUid);
        if (perms == null) {
            return false;
        }
        for (int i = perms.size() - 1; i >= 0; i--) {
            GrantUri grantUri = perms.keyAt(i);
            if ((grantUri.sourceUserId == userId || !checkUser) && matchesProvider(grantUri.uri, cpi)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesProvider(Uri uri, ProviderInfo cpi) {
        String uriAuth = uri.getAuthority();
        String cpiAuth = cpi.authority;
        if (cpiAuth.indexOf(59) == -1) {
            return cpiAuth.equals(uriAuth);
        }
        for (String equals : cpiAuth.split(";")) {
            if (equals.equals(uriAuth)) {
                return true;
            }
        }
        return false;
    }

    private boolean maybePrunePersistedUriGrants(int uid) {
        ArrayMap<GrantUri, UriPermission> perms = this.mGrantedUriPermissions.get(uid);
        if (perms == null || perms.size() < 128) {
            return false;
        }
        ArrayList<UriPermission> persisted = Lists.newArrayList();
        for (UriPermission perm : perms.values()) {
            if (perm.persistedModeFlags != 0) {
                persisted.add(perm);
            }
        }
        int trimCount = persisted.size() - 128;
        if (trimCount <= 0) {
            return false;
        }
        Collections.sort(persisted, new UriPermission.PersistedTimeComparator());
        for (int i = 0; i < trimCount; i++) {
            UriPermission perm2 = persisted.get(i);
            perm2.releasePersistableModes(-1);
            removeUriPermissionIfNeeded(perm2);
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public NeededUriGrants checkGrantUriPermissionFromIntent(int callingUid, String targetPkg, Intent intent, int mode, NeededUriGrants needed, int targetUserId) {
        int contentUserHint;
        int targetUid;
        NeededUriGrants needed2;
        String str = targetPkg;
        int i = mode;
        NeededUriGrants needed3 = needed;
        if (str == null) {
            int i2 = targetUserId;
            throw new NullPointerException(ATTR_TARGET_PKG);
        } else if (intent == null) {
            return null;
        } else {
            Uri data = intent.getData();
            ClipData clip = intent.getClipData();
            if (data == null && clip == null) {
                return null;
            }
            int contentUserHint2 = intent.getContentUserHint();
            if (contentUserHint2 == -2) {
                contentUserHint = UserHandle.getUserId(callingUid);
            } else {
                contentUserHint = contentUserHint2;
            }
            IPackageManager pm = AppGlobals.getPackageManager();
            if (needed3 != null) {
                targetUid = needed3.targetUid;
                int i3 = targetUserId;
            } else {
                try {
                    targetUid = pm.getPackageUid(str, 268435456, targetUserId);
                    if (targetUid < 0) {
                        return null;
                    }
                } catch (RemoteException e) {
                    RemoteException remoteException = e;
                    return null;
                }
            }
            if (data != null) {
                GrantUri grantUri = GrantUri.resolve(contentUserHint, data);
                targetUid = checkGrantUriPermission(callingUid, targetPkg, grantUri, mode, targetUid);
                if (targetUid > 0) {
                    if (needed3 == null) {
                        needed2 = new NeededUriGrants(str, targetUid, i);
                    } else {
                        needed2 = needed3;
                    }
                    needed2.add(grantUri);
                    needed3 = needed2;
                }
            }
            if (clip == null) {
                return needed3;
            }
            int targetUid2 = targetUid;
            NeededUriGrants needed4 = needed3;
            for (int i4 = 0; i4 < clip.getItemCount(); i4++) {
                Uri uri = clip.getItemAt(i4).getUri();
                if (uri != null) {
                    GrantUri grantUri2 = GrantUri.resolve(contentUserHint, uri);
                    Uri uri2 = uri;
                    GrantUri grantUri3 = grantUri2;
                    int targetUid3 = checkGrantUriPermission(callingUid, targetPkg, grantUri2, mode, targetUid2);
                    if (targetUid3 > 0) {
                        if (needed4 == null) {
                            needed4 = new NeededUriGrants(str, targetUid3, i);
                        }
                        needed4.add(grantUri3);
                    }
                    targetUid2 = targetUid3;
                } else {
                    Uri uri3 = uri;
                    Intent clipIntent = clip.getItemAt(i4).getIntent();
                    if (clipIntent != null) {
                        Uri uri4 = uri3;
                        NeededUriGrants newNeeded = checkGrantUriPermissionFromIntent(callingUid, targetPkg, clipIntent, mode, needed4, targetUserId);
                        if (newNeeded != null) {
                            needed4 = newNeeded;
                        }
                    }
                }
            }
            int i5 = targetUid2;
            return needed4;
        }
    }

    /* access modifiers changed from: package-private */
    public void grantUriPermissionFromIntent(int callingUid, String targetPkg, Intent intent, UriPermissionOwner owner, int targetUserId) {
        NeededUriGrants needed = checkGrantUriPermissionFromIntent(callingUid, targetPkg, intent, intent != null ? intent.getFlags() : 0, (NeededUriGrants) null, targetUserId);
        if (needed != null) {
            grantUriPermissionUncheckedFromIntent(needed, owner);
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00c1 A[SYNTHETIC, Splitter:B:33:0x00c1] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00da A[Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0116, XmlPullParserException -> 0x0114 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void readGrantedUriPermissions() {
        /*
            r23 = this;
            r1 = r23
            java.lang.String r2 = "Failed reading Uri grants"
            java.lang.String r3 = "UriGrantsManagerService"
            long r4 = java.lang.System.currentTimeMillis()
            r6 = 0
            android.util.AtomicFile r0 = r1.mGrantFile     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            java.io.FileInputStream r0 = r0.openRead()     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            r6 = r0
            org.xmlpull.v1.XmlPullParser r0 = android.util.Xml.newPullParser()     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            r7 = r0
            java.nio.charset.Charset r0 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            java.lang.String r0 = r0.name()     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            r7.setInput(r6, r0)     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
        L_0x0020:
            int r0 = r7.next()     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            r8 = r0
            r9 = 1
            if (r0 == r9) goto L_0x012f
            java.lang.String r0 = r7.getName()     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            r9 = r0
            r0 = 2
            if (r8 != r0) goto L_0x0121
            java.lang.String r0 = "uri-grant"
            boolean r0 = r0.equals(r9)     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            if (r0 == 0) goto L_0x011a
            java.lang.String r0 = "userHandle"
            r10 = -10000(0xffffffffffffd8f0, float:NaN)
            int r0 = com.android.internal.util.XmlUtils.readIntAttribute(r7, r0, r10)     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            r11 = r0
            if (r11 == r10) goto L_0x004a
            r0 = r11
            r10 = r11
            r12 = r10
            r10 = r0
            goto L_0x005a
        L_0x004a:
            java.lang.String r0 = "sourceUserId"
            int r0 = com.android.internal.util.XmlUtils.readIntAttribute(r7, r0)     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            java.lang.String r10 = "targetUserId"
            int r10 = com.android.internal.util.XmlUtils.readIntAttribute(r7, r10)     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            r12 = r10
            r10 = r0
        L_0x005a:
            java.lang.String r0 = "sourcePkg"
            r13 = 0
            java.lang.String r0 = r7.getAttributeValue(r13, r0)     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            r14 = r0
            java.lang.String r0 = "targetPkg"
            java.lang.String r0 = r7.getAttributeValue(r13, r0)     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            r15 = r0
            java.lang.String r0 = "uri"
            java.lang.String r0 = r7.getAttributeValue(r13, r0)     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            android.net.Uri r0 = android.net.Uri.parse(r0)     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            r13 = r0
            java.lang.String r0 = "prefix"
            boolean r0 = com.android.internal.util.XmlUtils.readBooleanAttribute(r7, r0)     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            r16 = r0
            java.lang.String r0 = "modeFlags"
            int r0 = com.android.internal.util.XmlUtils.readIntAttribute(r7, r0)     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            r17 = r0
            java.lang.String r0 = "createdTime"
            long r18 = com.android.internal.util.XmlUtils.readLongAttribute(r7, r0, r4)     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            r20 = r18
            java.lang.String r0 = r13.getAuthority()     // Catch:{ FileNotFoundException -> 0x014f, IOException -> 0x0142, XmlPullParserException -> 0x013a, all -> 0x0136 }
            r18 = r4
            r4 = 786432(0xc0000, float:1.102026E-39)
            android.content.pm.ProviderInfo r0 = r1.getProviderInfo(r0, r10, r4)     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0116, XmlPullParserException -> 0x0114 }
            r4 = r0
            if (r4 == 0) goto L_0x00e5
            java.lang.String r0 = r4.packageName     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0116, XmlPullParserException -> 0x0114 }
            boolean r0 = r14.equals(r0)     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0116, XmlPullParserException -> 0x0114 }
            if (r0 == 0) goto L_0x00e5
            r5 = -1
            android.content.pm.IPackageManager r0 = android.app.AppGlobals.getPackageManager()     // Catch:{ RemoteException -> 0x00b9 }
            r22 = r5
            r5 = 8192(0x2000, float:1.14794E-41)
            int r0 = r0.getPackageUid(r15, r5, r12)     // Catch:{ RemoteException -> 0x00b7 }
            r5 = r0
            goto L_0x00be
        L_0x00b7:
            r0 = move-exception
            goto L_0x00bc
        L_0x00b9:
            r0 = move-exception
            r22 = r5
        L_0x00bc:
            r5 = r22
        L_0x00be:
            r0 = -1
            if (r5 == r0) goto L_0x00da
            com.android.server.uri.GrantUri r0 = new com.android.server.uri.GrantUri     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0116, XmlPullParserException -> 0x0114 }
            r22 = r7
            r7 = r16
            r0.<init>(r10, r13, r7)     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0116, XmlPullParserException -> 0x0114 }
            com.android.server.uri.UriPermission r0 = r1.findOrCreateUriPermission(r14, r15, r5, r0)     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0116, XmlPullParserException -> 0x0114 }
            r16 = r8
            r1 = r17
            r17 = r7
            r7 = r20
            r0.initPersistedModes(r1, r7)     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0116, XmlPullParserException -> 0x0114 }
            goto L_0x00e4
        L_0x00da:
            r22 = r7
            r1 = r17
            r17 = r16
            r16 = r8
            r7 = r20
        L_0x00e4:
            goto L_0x0127
        L_0x00e5:
            r22 = r7
            r1 = r17
            r17 = r16
            r16 = r8
            r7 = r20
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0116, XmlPullParserException -> 0x0114 }
            r0.<init>()     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0116, XmlPullParserException -> 0x0114 }
            java.lang.String r5 = "Persisted grant for "
            r0.append(r5)     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0116, XmlPullParserException -> 0x0114 }
            r0.append(r13)     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0116, XmlPullParserException -> 0x0114 }
            java.lang.String r5 = " had source "
            r0.append(r5)     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0116, XmlPullParserException -> 0x0114 }
            r0.append(r14)     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0116, XmlPullParserException -> 0x0114 }
            java.lang.String r5 = " but instead found "
            r0.append(r5)     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0116, XmlPullParserException -> 0x0114 }
            r0.append(r4)     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0116, XmlPullParserException -> 0x0114 }
            java.lang.String r0 = r0.toString()     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0116, XmlPullParserException -> 0x0114 }
            android.util.Slog.w(r3, r0)     // Catch:{ FileNotFoundException -> 0x0118, IOException -> 0x0116, XmlPullParserException -> 0x0114 }
            goto L_0x0127
        L_0x0114:
            r0 = move-exception
            goto L_0x013d
        L_0x0116:
            r0 = move-exception
            goto L_0x0145
        L_0x0118:
            r0 = move-exception
            goto L_0x0152
        L_0x011a:
            r18 = r4
            r22 = r7
            r16 = r8
            goto L_0x0127
        L_0x0121:
            r18 = r4
            r22 = r7
            r16 = r8
        L_0x0127:
            r1 = r23
            r4 = r18
            r7 = r22
            goto L_0x0020
        L_0x012f:
            r18 = r4
            r22 = r7
            r16 = r8
            goto L_0x0152
        L_0x0136:
            r0 = move-exception
            r18 = r4
            goto L_0x014b
        L_0x013a:
            r0 = move-exception
            r18 = r4
        L_0x013d:
            android.util.Slog.wtf(r3, r2, r0)     // Catch:{ all -> 0x014a }
            goto L_0x0153
        L_0x0142:
            r0 = move-exception
            r18 = r4
        L_0x0145:
            android.util.Slog.wtf(r3, r2, r0)     // Catch:{ all -> 0x014a }
            goto L_0x0153
        L_0x014a:
            r0 = move-exception
        L_0x014b:
            libcore.io.IoUtils.closeQuietly(r6)
            throw r0
        L_0x014f:
            r0 = move-exception
            r18 = r4
        L_0x0152:
        L_0x0153:
            libcore.io.IoUtils.closeQuietly(r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.uri.UriGrantsManagerService.readGrantedUriPermissions():void");
    }

    private UriPermission findOrCreateUriPermission(String sourcePkg, String targetPkg, int targetUid, GrantUri grantUri) {
        ArrayMap<GrantUri, UriPermission> targetUris = this.mGrantedUriPermissions.get(targetUid);
        if (targetUris == null) {
            targetUris = Maps.newArrayMap();
            this.mGrantedUriPermissions.put(targetUid, targetUris);
        }
        UriPermission perm = targetUris.get(grantUri);
        if (perm != null) {
            return perm;
        }
        UriPermission perm2 = new UriPermission(sourcePkg, targetPkg, targetUid, grantUri);
        targetUris.put(grantUri, perm2);
        return perm2;
    }

    private void grantUriPermissionUnchecked(int targetUid, String targetPkg, GrantUri grantUri, int modeFlags, UriPermissionOwner owner) {
        if (Intent.isAccessUriMode(modeFlags)) {
            ProviderInfo pi = getProviderInfo(grantUri.uri.getAuthority(), grantUri.sourceUserId, 268435456);
            if (pi == null) {
                Slog.w(TAG, "No content provider found for grant: " + grantUri.toSafeString());
                return;
            }
            if ((modeFlags & 128) != 0) {
                grantUri.prefix = true;
            }
            findOrCreateUriPermission(pi.packageName, targetPkg, targetUid, grantUri).grantModes(modeFlags, owner);
        }
    }

    /* access modifiers changed from: package-private */
    public void grantUriPermissionUncheckedFromIntent(NeededUriGrants needed, UriPermissionOwner owner) {
        if (needed != null) {
            for (int i = 0; i < needed.size(); i++) {
                grantUriPermissionUnchecked(needed.targetUid, needed.targetPkg, (GrantUri) needed.get(i), needed.flags, owner);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void grantUriPermission(int callingUid, String targetPkg, GrantUri grantUri, int modeFlags, UriPermissionOwner owner, int targetUserId) {
        if (targetPkg != null) {
            try {
                int targetUid = checkGrantUriPermission(callingUid, targetPkg, grantUri, modeFlags, AppGlobals.getPackageManager().getPackageUid(targetPkg, 268435456, targetUserId));
                if (targetUid >= 0) {
                    grantUriPermissionUnchecked(targetUid, targetPkg, grantUri, modeFlags, owner);
                }
            } catch (RemoteException e) {
            }
        } else {
            throw new NullPointerException(ATTR_TARGET_PKG);
        }
    }

    /* access modifiers changed from: package-private */
    public void revokeUriPermission(String targetPackage, int callingUid, GrantUri grantUri, int modeFlags) {
        String str = targetPackage;
        int i = callingUid;
        GrantUri grantUri2 = grantUri;
        IPackageManager pm = AppGlobals.getPackageManager();
        ProviderInfo pi = getProviderInfo(grantUri2.uri.getAuthority(), grantUri2.sourceUserId, 786432);
        if (pi == null) {
            Slog.w(TAG, "No content provider found for permission revoke: " + grantUri.toSafeString());
        } else if (!checkHoldingPermissions(pm, pi, grantUri, callingUid, modeFlags)) {
            ArrayMap<GrantUri, UriPermission> perms = this.mGrantedUriPermissions.get(i);
            if (perms != null) {
                boolean persistChanged = false;
                for (int i2 = perms.size() - 1; i2 >= 0; i2--) {
                    UriPermission perm = perms.valueAt(i2);
                    if ((str == null || str.equals(perm.targetPkg)) && perm.uri.sourceUserId == grantUri2.sourceUserId && perm.uri.uri.isPathPrefixMatch(grantUri2.uri)) {
                        persistChanged |= perm.revokeModes(modeFlags | 64, false);
                        if (perm.modeFlags == 0) {
                            perms.removeAt(i2);
                        }
                    }
                }
                if (perms.isEmpty()) {
                    this.mGrantedUriPermissions.remove(i);
                }
                if (persistChanged) {
                    schedulePersistUriGrants();
                }
            }
        } else {
            boolean persistChanged2 = false;
            for (int i3 = this.mGrantedUriPermissions.size() - 1; i3 >= 0; i3--) {
                int keyAt = this.mGrantedUriPermissions.keyAt(i3);
                ArrayMap<GrantUri, UriPermission> perms2 = this.mGrantedUriPermissions.valueAt(i3);
                for (int j = perms2.size() - 1; j >= 0; j--) {
                    UriPermission perm2 = perms2.valueAt(j);
                    if ((str == null || str.equals(perm2.targetPkg)) && perm2.uri.sourceUserId == grantUri2.sourceUserId && perm2.uri.uri.isPathPrefixMatch(grantUri2.uri)) {
                        persistChanged2 |= perm2.revokeModes(modeFlags | 64, str == null);
                        if (perm2.modeFlags == 0) {
                            perms2.removeAt(j);
                        }
                    }
                }
                if (perms2.isEmpty()) {
                    this.mGrantedUriPermissions.removeAt(i3);
                }
            }
            if (persistChanged2) {
                schedulePersistUriGrants();
            }
        }
    }

    private boolean checkHoldingPermissions(IPackageManager pm, ProviderInfo pi, GrantUri grantUri, int uid, int modeFlags) {
        if (UserHandle.getUserId(uid) == grantUri.sourceUserId || ActivityManager.checkComponentPermission("android.permission.INTERACT_ACROSS_USERS", uid, -1, true) == 0) {
            return checkHoldingPermissionsInternal(pm, pi, grantUri, uid, modeFlags, true);
        }
        return false;
    }

    private boolean checkHoldingPermissionsInternal(IPackageManager pm, ProviderInfo pi, GrantUri grantUri, int uid, int modeFlags, boolean considerUidPermissions) {
        String ppwperm;
        String pprperm;
        IPackageManager iPackageManager = pm;
        ProviderInfo providerInfo = pi;
        int i = uid;
        if (providerInfo.applicationInfo.uid == i) {
            return true;
        }
        if (!providerInfo.exported) {
            return false;
        }
        boolean readMet = (modeFlags & 1) == 0;
        boolean writeMet = (modeFlags & 2) == 0;
        if (!readMet) {
            try {
                if (providerInfo.readPermission != null && considerUidPermissions && iPackageManager.checkUidPermission(providerInfo.readPermission, i) == 0) {
                    readMet = true;
                }
            } catch (RemoteException e) {
                GrantUri grantUri2 = grantUri;
                return false;
            }
        }
        if (!writeMet && providerInfo.writePermission != null && considerUidPermissions && iPackageManager.checkUidPermission(providerInfo.writePermission, i) == 0) {
            writeMet = true;
        }
        boolean allowDefaultRead = providerInfo.readPermission == null;
        boolean allowDefaultWrite = providerInfo.writePermission == null;
        PathPermission[] pps = providerInfo.pathPermissions;
        if (pps != null) {
            try {
                String path = grantUri.uri.getPath();
                int i2 = pps.length;
                while (i2 > 0 && (!readMet || !writeMet)) {
                    i2--;
                    PathPermission pp = pps[i2];
                    if (pp.match(path)) {
                        if (!readMet && (pprperm = pp.getReadPermission()) != null) {
                            if (!considerUidPermissions || iPackageManager.checkUidPermission(pprperm, i) != 0) {
                                allowDefaultRead = false;
                            } else {
                                readMet = true;
                            }
                        }
                        if (!writeMet && (ppwperm = pp.getWritePermission()) != null) {
                            if (!considerUidPermissions || iPackageManager.checkUidPermission(ppwperm, i) != 0) {
                                allowDefaultWrite = false;
                            } else {
                                writeMet = true;
                            }
                        }
                    }
                }
            } catch (RemoteException e2) {
                return false;
            }
        } else {
            GrantUri grantUri3 = grantUri;
        }
        if (allowDefaultRead) {
            readMet = true;
        }
        if (allowDefaultWrite) {
            writeMet = true;
        }
        if (!readMet || !writeMet) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void removeUriPermissionIfNeeded(UriPermission perm) {
        ArrayMap<GrantUri, UriPermission> perms;
        if (perm.modeFlags == 0 && (perms = this.mGrantedUriPermissions.get(perm.targetUid)) != null) {
            perms.remove(perm.uri);
            if (perms.isEmpty()) {
                this.mGrantedUriPermissions.remove(perm.targetUid);
            }
        }
    }

    private UriPermission findUriPermissionLocked(int targetUid, GrantUri grantUri) {
        ArrayMap<GrantUri, UriPermission> targetUris = this.mGrantedUriPermissions.get(targetUid);
        if (targetUris != null) {
            return targetUris.get(grantUri);
        }
        return null;
    }

    private void schedulePersistUriGrants() {
        if (!this.mH.hasMessages(1)) {
            H h = this.mH;
            h.sendMessageDelayed(h.obtainMessage(1), JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
        }
    }

    /* access modifiers changed from: private */
    public void enforceNotIsolatedCaller(String caller) {
        if (UserHandle.isIsolated(Binder.getCallingUid())) {
            throw new SecurityException("Isolated process not allowed to call " + caller);
        }
    }

    private ProviderInfo getProviderInfo(String authority, int userHandle, int pmFlags) {
        try {
            return AppGlobals.getPackageManager().resolveContentProvider(authority, pmFlags | 2048, userHandle);
        } catch (RemoteException e) {
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x01fc  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0245 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0153  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int checkGrantUriPermission(int r19, java.lang.String r20, com.android.server.uri.GrantUri r21, int r22, int r23) {
        /*
            r18 = this;
            r8 = r18
            r9 = r19
            r10 = r20
            r11 = r21
            r12 = r22
            boolean r0 = android.content.Intent.isAccessUriMode(r22)
            r1 = -1
            if (r0 != 0) goto L_0x0012
            return r1
        L_0x0012:
            android.content.pm.IPackageManager r13 = android.app.AppGlobals.getPackageManager()
            android.net.Uri r0 = r11.uri
            java.lang.String r0 = r0.getScheme()
            java.lang.String r2 = "content"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x0026
            return r1
        L_0x0026:
            int r14 = android.os.UserHandle.getAppId(r19)
            r0 = 1000(0x3e8, float:1.401E-42)
            java.lang.String r2 = "UriGrantsManagerService"
            if (r14 == r0) goto L_0x0032
            if (r14 != 0) goto L_0x006f
        L_0x0032:
            android.net.Uri r0 = r11.uri
            java.lang.String r0 = r0.getAuthority()
            java.lang.String r3 = "com.android.settings.files"
            boolean r0 = r3.equals(r0)
            if (r0 != 0) goto L_0x006f
            boolean r0 = com.android.server.am.ActivityManagerServiceInjector.ignoreSystemUidAppCheck(r22)
            if (r0 != 0) goto L_0x006f
            android.net.Uri r0 = r11.uri
            java.lang.String r0 = r0.getAuthority()
            java.lang.String r3 = "com.android.settings.module_licenses"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0055
            goto L_0x006f
        L_0x0055:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "For security reasons, the system cannot issue a Uri permission grant to "
            r0.append(r3)
            r0.append(r11)
            java.lang.String r3 = "; use startActivityAsCaller() instead"
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            android.util.Slog.w(r2, r0)
            return r1
        L_0x006f:
            android.net.Uri r0 = r11.uri
            java.lang.String r15 = r0.getAuthority()
            int r0 = r11.sourceUserId
            r3 = 268435456(0x10000000, float:2.5243549E-29)
            android.content.pm.ProviderInfo r7 = r8.getProviderInfo(r15, r0, r3)
            if (r7 != 0) goto L_0x009a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "No content provider found for permission check: "
            r0.append(r3)
            android.net.Uri r3 = r11.uri
            java.lang.String r3 = r3.toSafeString()
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            android.util.Slog.w(r2, r0)
            return r1
        L_0x009a:
            r2 = r23
            if (r2 >= 0) goto L_0x00af
            if (r10 == 0) goto L_0x00af
            int r0 = android.os.UserHandle.getUserId(r19)     // Catch:{ RemoteException -> 0x00ad }
            int r0 = r13.getPackageUid(r10, r3, r0)     // Catch:{ RemoteException -> 0x00ad }
            if (r0 >= 0) goto L_0x00ac
            return r1
        L_0x00ac:
            goto L_0x00b0
        L_0x00ad:
            r0 = move-exception
            return r1
        L_0x00af:
            r0 = r2
        L_0x00b0:
            r1 = r12 & 64
            if (r1 != 0) goto L_0x00bd
            boolean r1 = r7.forceUriPermissions
            if (r1 == 0) goto L_0x00b9
            goto L_0x00bd
        L_0x00b9:
            r1 = -1
            r16 = r1
            goto L_0x00c0
        L_0x00bd:
            r1 = r0
            r16 = r1
        L_0x00c0:
            if (r0 < 0) goto L_0x00d2
            r1 = r18
            r2 = r13
            r3 = r7
            r4 = r21
            r5 = r0
            r6 = r22
            boolean r1 = r1.checkHoldingPermissions(r2, r3, r4, r5, r6)
            if (r1 == 0) goto L_0x012b
            return r16
        L_0x00d2:
            boolean r1 = r7.exported
            r2 = r12 & 1
            if (r2 == 0) goto L_0x00dd
            java.lang.String r2 = r7.readPermission
            if (r2 == 0) goto L_0x00dd
            r1 = 0
        L_0x00dd:
            r2 = r12 & 2
            if (r2 == 0) goto L_0x00e6
            java.lang.String r2 = r7.writePermission
            if (r2 == 0) goto L_0x00e6
            r1 = 0
        L_0x00e6:
            android.content.pm.PathPermission[] r2 = r7.pathPermissions
            if (r2 == 0) goto L_0x0128
            android.content.pm.PathPermission[] r2 = r7.pathPermissions
            int r2 = r2.length
            r3 = 0
        L_0x00ee:
            if (r3 >= r2) goto L_0x0128
            android.content.pm.PathPermission[] r4 = r7.pathPermissions
            r4 = r4[r3]
            if (r4 == 0) goto L_0x0125
            android.content.pm.PathPermission[] r4 = r7.pathPermissions
            r4 = r4[r3]
            android.net.Uri r5 = r11.uri
            java.lang.String r5 = r5.getPath()
            boolean r4 = r4.match(r5)
            if (r4 == 0) goto L_0x0125
            r4 = r12 & 1
            if (r4 == 0) goto L_0x0115
            android.content.pm.PathPermission[] r4 = r7.pathPermissions
            r4 = r4[r3]
            java.lang.String r4 = r4.getReadPermission()
            if (r4 == 0) goto L_0x0115
            r1 = 0
        L_0x0115:
            r4 = r12 & 2
            if (r4 == 0) goto L_0x0128
            android.content.pm.PathPermission[] r4 = r7.pathPermissions
            r4 = r4[r3]
            java.lang.String r4 = r4.getWritePermission()
            if (r4 == 0) goto L_0x0128
            r1 = 0
            goto L_0x0128
        L_0x0125:
            int r3 = r3 + 1
            goto L_0x00ee
        L_0x0128:
            if (r1 == 0) goto L_0x012b
            return r16
        L_0x012b:
            if (r0 < 0) goto L_0x014e
            int r1 = android.os.UserHandle.getUserId(r0)
            int r2 = r11.sourceUserId
            if (r1 == r2) goto L_0x014c
            r17 = 0
            r1 = r18
            r2 = r13
            r3 = r7
            r4 = r21
            r5 = r19
            r6 = r22
            r10 = r7
            r7 = r17
            boolean r1 = r1.checkHoldingPermissionsInternal(r2, r3, r4, r5, r6, r7)
            if (r1 == 0) goto L_0x014f
            r1 = 1
            goto L_0x0150
        L_0x014c:
            r10 = r7
            goto L_0x014f
        L_0x014e:
            r10 = r7
        L_0x014f:
            r1 = 0
        L_0x0150:
            r7 = r1
            if (r7 != 0) goto L_0x01df
            boolean r1 = r10.grantUriPermissions
            java.lang.String r2 = "/"
            java.lang.String r3 = "Provider "
            if (r1 == 0) goto L_0x01b3
            android.os.PatternMatcher[] r1 = r10.uriPermissionPatterns
            if (r1 == 0) goto L_0x01df
            android.os.PatternMatcher[] r1 = r10.uriPermissionPatterns
            int r1 = r1.length
            r4 = 0
            r5 = 0
        L_0x0164:
            if (r5 >= r1) goto L_0x0187
            android.os.PatternMatcher[] r6 = r10.uriPermissionPatterns
            r6 = r6[r5]
            if (r6 == 0) goto L_0x0180
            android.os.PatternMatcher[] r6 = r10.uriPermissionPatterns
            r6 = r6[r5]
            r17 = r1
            android.net.Uri r1 = r11.uri
            java.lang.String r1 = r1.getPath()
            boolean r1 = r6.match(r1)
            if (r1 == 0) goto L_0x0182
            r4 = 1
            goto L_0x0189
        L_0x0180:
            r17 = r1
        L_0x0182:
            int r5 = r5 + 1
            r1 = r17
            goto L_0x0164
        L_0x0187:
            r17 = r1
        L_0x0189:
            if (r4 == 0) goto L_0x018c
            goto L_0x01df
        L_0x018c:
            java.lang.SecurityException r1 = new java.lang.SecurityException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r3)
            java.lang.String r3 = r10.packageName
            r5.append(r3)
            r5.append(r2)
            java.lang.String r2 = r10.name
            r5.append(r2)
            java.lang.String r2 = " does not allow granting of permission to path of Uri "
            r5.append(r2)
            r5.append(r11)
            java.lang.String r2 = r5.toString()
            r1.<init>(r2)
            throw r1
        L_0x01b3:
            java.lang.SecurityException r1 = new java.lang.SecurityException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            java.lang.String r3 = r10.packageName
            r4.append(r3)
            r4.append(r2)
            java.lang.String r2 = r10.name
            r4.append(r2)
            java.lang.String r2 = " does not allow granting of Uri permissions (uri "
            r4.append(r2)
            r4.append(r11)
            java.lang.String r2 = ")"
            r4.append(r2)
            java.lang.String r2 = r4.toString()
            r1.<init>(r2)
            throw r1
        L_0x01df:
            boolean r17 = com.android.server.am.ActivityManagerServiceInjector.checkSystemUidHoldingPermissionsLocked(r12, r14)
            if (r17 != 0) goto L_0x0245
            r1 = r18
            r2 = r13
            r3 = r10
            r4 = r21
            r5 = r19
            r6 = r22
            boolean r1 = r1.checkHoldingPermissions(r2, r3, r4, r5, r6)
            if (r1 != 0) goto L_0x0245
            boolean r1 = r8.checkUriPermission(r11, r9, r12)
            if (r1 != 0) goto L_0x0245
            java.lang.String r1 = r10.readPermission
            java.lang.String r2 = "android.permission.MANAGE_DOCUMENTS"
            boolean r1 = r2.equals(r1)
            java.lang.String r2 = " does not have permission to "
            java.lang.String r3 = "UID "
            if (r1 == 0) goto L_0x022a
            java.lang.SecurityException r1 = new java.lang.SecurityException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            r4.append(r9)
            r4.append(r2)
            r4.append(r11)
            java.lang.String r2 = "; you could obtain access using ACTION_OPEN_DOCUMENT or related APIs"
            r4.append(r2)
            java.lang.String r2 = r4.toString()
            r1.<init>(r2)
            throw r1
        L_0x022a:
            java.lang.SecurityException r1 = new java.lang.SecurityException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            r4.append(r9)
            r4.append(r2)
            r4.append(r11)
            java.lang.String r2 = r4.toString()
            r1.<init>(r2)
            throw r1
        L_0x0245:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.uri.UriGrantsManagerService.checkGrantUriPermission(int, java.lang.String, com.android.server.uri.GrantUri, int, int):int");
    }

    /* access modifiers changed from: package-private */
    public int checkGrantUriPermission(int callingUid, String targetPkg, Uri uri, int modeFlags, int userId) {
        return checkGrantUriPermission(callingUid, targetPkg, new GrantUri(userId, uri, false), modeFlags, -1);
    }

    /* access modifiers changed from: package-private */
    public boolean checkUriPermission(GrantUri grantUri, int uid, int modeFlags) {
        int minStrength;
        if ((modeFlags & 64) != 0) {
            minStrength = 3;
        } else {
            minStrength = 1;
        }
        if (uid == 0) {
            return true;
        }
        ArrayMap<GrantUri, UriPermission> perms = this.mGrantedUriPermissions.get(uid);
        if (perms == null) {
            return false;
        }
        UriPermission exactPerm = perms.get(grantUri);
        if (exactPerm != null && exactPerm.getStrength(modeFlags) >= minStrength) {
            return true;
        }
        int N = perms.size();
        for (int i = 0; i < N; i++) {
            UriPermission perm = perms.valueAt(i);
            if (perm.uri.prefix && grantUri.uri.isPathPrefixMatch(perm.uri.uri) && perm.getStrength(modeFlags) >= minStrength) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void writeGrantedUriPermissions() {
        long startTime = SystemClock.uptimeMillis();
        ArrayList<UriPermission.Snapshot> persist = Lists.newArrayList();
        synchronized (this) {
            int size = this.mGrantedUriPermissions.size();
            for (int i = 0; i < size; i++) {
                for (UriPermission perm : this.mGrantedUriPermissions.valueAt(i).values()) {
                    if (perm.persistedModeFlags != 0) {
                        persist.add(perm.snapshot());
                    }
                }
            }
        }
        try {
            FileOutputStream fos = this.mGrantFile.startWrite(startTime);
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(fos, StandardCharsets.UTF_8.name());
            out.startDocument((String) null, true);
            out.startTag((String) null, TAG_URI_GRANTS);
            Iterator<UriPermission.Snapshot> it = persist.iterator();
            while (it.hasNext()) {
                UriPermission.Snapshot perm2 = it.next();
                out.startTag((String) null, TAG_URI_GRANT);
                XmlUtils.writeIntAttribute(out, ATTR_SOURCE_USER_ID, perm2.uri.sourceUserId);
                XmlUtils.writeIntAttribute(out, ATTR_TARGET_USER_ID, perm2.targetUserId);
                out.attribute((String) null, ATTR_SOURCE_PKG, perm2.sourcePkg);
                out.attribute((String) null, ATTR_TARGET_PKG, perm2.targetPkg);
                out.attribute((String) null, ATTR_URI, String.valueOf(perm2.uri.uri));
                XmlUtils.writeBooleanAttribute(out, ATTR_PREFIX, perm2.uri.prefix);
                XmlUtils.writeIntAttribute(out, ATTR_MODE_FLAGS, perm2.persistedModeFlags);
                XmlUtils.writeLongAttribute(out, ATTR_CREATED_TIME, perm2.persistedCreateTime);
                out.endTag((String) null, TAG_URI_GRANT);
            }
            out.endTag((String) null, TAG_URI_GRANTS);
            out.endDocument();
            this.mGrantFile.finishWrite(fos);
        } catch (IOException e) {
            if (0 != 0) {
                this.mGrantFile.failWrite((FileOutputStream) null);
            }
        }
    }

    private PackageManagerInternal getPmInternal() {
        if (this.mPmInternal == null) {
            this.mPmInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        }
        return this.mPmInternal;
    }

    final class H extends Handler {
        static final int PERSIST_URI_GRANTS_MSG = 1;

        public H(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                UriGrantsManagerService.this.writeGrantedUriPermissions();
            }
        }
    }

    final class LocalService implements UriGrantsManagerInternal {
        LocalService() {
        }

        public void removeUriPermissionIfNeeded(UriPermission perm) {
            synchronized (UriGrantsManagerService.this.mLock) {
                UriGrantsManagerService.this.removeUriPermissionIfNeeded(perm);
            }
        }

        public void grantUriPermission(int callingUid, String targetPkg, GrantUri grantUri, int modeFlags, UriPermissionOwner owner, int targetUserId) {
            synchronized (UriGrantsManagerService.this.mLock) {
                UriGrantsManagerService.this.grantUriPermission(callingUid, targetPkg, grantUri, modeFlags, owner, targetUserId);
            }
        }

        public void revokeUriPermission(String targetPackage, int callingUid, GrantUri grantUri, int modeFlags) {
            synchronized (UriGrantsManagerService.this.mLock) {
                UriGrantsManagerService.this.revokeUriPermission(targetPackage, callingUid, grantUri, modeFlags);
            }
        }

        public boolean checkUriPermission(GrantUri grantUri, int uid, int modeFlags) {
            boolean checkUriPermission;
            synchronized (UriGrantsManagerService.this.mLock) {
                checkUriPermission = UriGrantsManagerService.this.checkUriPermission(grantUri, uid, modeFlags);
            }
            return checkUriPermission;
        }

        public int checkGrantUriPermission(int callingUid, String targetPkg, GrantUri uri, int modeFlags, int userId) {
            int checkGrantUriPermission;
            synchronized (UriGrantsManagerService.this.mLock) {
                checkGrantUriPermission = UriGrantsManagerService.this.checkGrantUriPermission(callingUid, targetPkg, uri, modeFlags, userId);
            }
            return checkGrantUriPermission;
        }

        public int checkGrantUriPermission(int callingUid, String targetPkg, Uri uri, int modeFlags, int userId) {
            int checkGrantUriPermission;
            UriGrantsManagerService.this.enforceNotIsolatedCaller("checkGrantUriPermission");
            synchronized (UriGrantsManagerService.this.mLock) {
                checkGrantUriPermission = UriGrantsManagerService.this.checkGrantUriPermission(callingUid, targetPkg, uri, modeFlags, userId);
            }
            return checkGrantUriPermission;
        }

        public NeededUriGrants checkGrantUriPermissionFromIntent(int callingUid, String targetPkg, Intent intent, int mode, NeededUriGrants needed, int targetUserId) {
            NeededUriGrants checkGrantUriPermissionFromIntent;
            synchronized (UriGrantsManagerService.this.mLock) {
                checkGrantUriPermissionFromIntent = UriGrantsManagerService.this.checkGrantUriPermissionFromIntent(callingUid, targetPkg, intent, mode, needed, targetUserId);
            }
            return checkGrantUriPermissionFromIntent;
        }

        public void grantUriPermissionFromIntent(int callingUid, String targetPkg, Intent intent, int targetUserId) {
            synchronized (UriGrantsManagerService.this.mLock) {
                UriGrantsManagerService.this.grantUriPermissionFromIntent(callingUid, targetPkg, intent, (UriPermissionOwner) null, targetUserId);
            }
        }

        public void grantUriPermissionFromIntent(int callingUid, String targetPkg, Intent intent, UriPermissionOwner owner, int targetUserId) {
            synchronized (UriGrantsManagerService.this.mLock) {
                UriGrantsManagerService.this.grantUriPermissionFromIntent(callingUid, targetPkg, intent, owner, targetUserId);
            }
        }

        public void grantUriPermissionUncheckedFromIntent(NeededUriGrants needed, UriPermissionOwner owner) {
            synchronized (UriGrantsManagerService.this.mLock) {
                UriGrantsManagerService.this.grantUriPermissionUncheckedFromIntent(needed, owner);
            }
        }

        public void onSystemReady() {
            synchronized (UriGrantsManagerService.this.mLock) {
                UriGrantsManagerService.this.readGrantedUriPermissions();
            }
        }

        public void onActivityManagerInternalAdded() {
            synchronized (UriGrantsManagerService.this.mLock) {
                UriGrantsManagerService.this.onActivityManagerInternalAdded();
            }
        }

        public IBinder newUriPermissionOwner(String name) {
            Binder externalToken;
            UriGrantsManagerService.this.enforceNotIsolatedCaller("newUriPermissionOwner");
            synchronized (UriGrantsManagerService.this.mLock) {
                externalToken = new UriPermissionOwner(this, name).getExternalToken();
            }
            return externalToken;
        }

        public void removeUriPermissionsForPackage(String packageName, int userHandle, boolean persistable, boolean targetOnly) {
            synchronized (UriGrantsManagerService.this.mLock) {
                UriGrantsManagerService.this.removeUriPermissionsForPackage(packageName, userHandle, persistable, targetOnly);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        public void revokeUriPermissionFromOwner(IBinder token, Uri uri, int mode, int userId) {
            synchronized (UriGrantsManagerService.this.mLock) {
                UriPermissionOwner owner = UriPermissionOwner.fromExternalToken(token);
                if (owner == null) {
                    throw new IllegalArgumentException("Unknown owner: " + token);
                } else if (uri == null) {
                    owner.removeUriPermissions(mode);
                } else {
                    owner.removeUriPermission(new GrantUri(userId, uri, (mode & 128) != 0), mode);
                }
            }
        }

        public boolean checkAuthorityGrants(int callingUid, ProviderInfo cpi, int userId, boolean checkUser) {
            boolean checkAuthorityGrants;
            synchronized (UriGrantsManagerService.this.mLock) {
                checkAuthorityGrants = UriGrantsManagerService.this.checkAuthorityGrants(callingUid, cpi, userId, checkUser);
            }
            return checkAuthorityGrants;
        }

        public void dump(PrintWriter pw, boolean dumpAll, String dumpPackage) {
            synchronized (UriGrantsManagerService.this.mLock) {
                boolean needSep = false;
                boolean printedAnything = false;
                if (UriGrantsManagerService.this.mGrantedUriPermissions.size() > 0) {
                    boolean printed = false;
                    int dumpUid = -2;
                    if (dumpPackage != null) {
                        try {
                            dumpUid = UriGrantsManagerService.this.mContext.getPackageManager().getPackageUidAsUser(dumpPackage, DumpState.DUMP_CHANGES, 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            dumpUid = -1;
                        }
                    }
                    for (int i = 0; i < UriGrantsManagerService.this.mGrantedUriPermissions.size(); i++) {
                        int uid = UriGrantsManagerService.this.mGrantedUriPermissions.keyAt(i);
                        if (dumpUid < -1 || UserHandle.getAppId(uid) == dumpUid) {
                            ArrayMap<GrantUri, UriPermission> perms = (ArrayMap) UriGrantsManagerService.this.mGrantedUriPermissions.valueAt(i);
                            if (!printed) {
                                if (needSep) {
                                    pw.println();
                                }
                                needSep = true;
                                pw.println("  Granted Uri Permissions:");
                                printed = true;
                                printedAnything = true;
                            }
                            pw.print("  * UID ");
                            pw.print(uid);
                            pw.println(" holds:");
                            for (UriPermission perm : perms.values()) {
                                pw.print("    ");
                                pw.println(perm);
                                if (dumpAll) {
                                    perm.dump(pw, "      ");
                                }
                            }
                        }
                    }
                }
                if (!printedAnything) {
                    pw.println("  (nothing)");
                }
            }
        }
    }
}
