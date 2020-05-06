package com.android.server.slice;

import android.content.ContentProvider;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.XmlUtils;
import com.android.server.slice.DirtyTracker;
import com.android.server.slice.SliceProviderPermissions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class SlicePermissionManager implements DirtyTracker {
    static final int DB_VERSION = 2;
    private static final long PERMISSION_CACHE_PERIOD = 300000;
    private static final String SLICE_DIR = "slice";
    private static final String TAG = "SlicePermissionManager";
    private static final String TAG_LIST = "slice-access-list";
    private static final long WRITE_GRACE_PERIOD = 500;
    private final String ATT_VERSION;
    /* access modifiers changed from: private */
    public final ArrayMap<PkgUser, SliceClientPermissions> mCachedClients;
    /* access modifiers changed from: private */
    public final ArrayMap<PkgUser, SliceProviderPermissions> mCachedProviders;
    private final Context mContext;
    /* access modifiers changed from: private */
    public final ArraySet<DirtyTracker.Persistable> mDirty;
    private final Handler mHandler;
    private final File mSliceDir;

    @VisibleForTesting
    SlicePermissionManager(Context context, Looper looper, File sliceDir) {
        this.ATT_VERSION = "version";
        this.mCachedProviders = new ArrayMap<>();
        this.mCachedClients = new ArrayMap<>();
        this.mDirty = new ArraySet<>();
        this.mContext = context;
        this.mHandler = new H(looper);
        this.mSliceDir = sliceDir;
    }

    public SlicePermissionManager(Context context, Looper looper) {
        this(context, looper, new File(Environment.getDataDirectory(), "system/slice"));
    }

    public void grantFullAccess(String pkg, int userId) {
        getClient(new PkgUser(pkg, userId)).setHasFullAccess(true);
    }

    public void grantSliceAccess(String pkg, int userId, String providerPkg, int providerUser, Uri uri) {
        PkgUser pkgUser = new PkgUser(pkg, userId);
        PkgUser providerPkgUser = new PkgUser(providerPkg, providerUser);
        getClient(pkgUser).grantUri(uri, providerPkgUser);
        getProvider(providerPkgUser).getOrCreateAuthority(ContentProvider.getUriWithoutUserId(uri).getAuthority()).addPkg(pkgUser);
    }

    public void revokeSliceAccess(String pkg, int userId, String providerPkg, int providerUser, Uri uri) {
        PkgUser pkgUser = new PkgUser(pkg, userId);
        getClient(pkgUser).revokeUri(uri, new PkgUser(providerPkg, providerUser));
    }

    public void removePkg(String pkg, int userId) {
        PkgUser pkgUser = new PkgUser(pkg, userId);
        for (SliceProviderPermissions.SliceAuthority authority : getProvider(pkgUser).getAuthorities()) {
            for (PkgUser p : authority.getPkgs()) {
                getClient(p).removeAuthority(authority.getAuthority(), userId);
            }
        }
        getClient(pkgUser).clear();
        this.mHandler.obtainMessage(3, pkgUser);
    }

    public String[] getAllPackagesGranted(String pkg) {
        ArraySet<String> ret = new ArraySet<>();
        for (SliceProviderPermissions.SliceAuthority authority : getProvider(new PkgUser(pkg, 0)).getAuthorities()) {
            for (PkgUser pkgUser : authority.getPkgs()) {
                ret.add(pkgUser.mPkg);
            }
        }
        return (String[]) ret.toArray(new String[ret.size()]);
    }

    public boolean hasFullAccess(String pkg, int userId) {
        return getClient(new PkgUser(pkg, userId)).hasFullAccess();
    }

    public boolean hasPermission(String pkg, int userId, Uri uri) {
        SliceClientPermissions client = getClient(new PkgUser(pkg, userId));
        return client.hasFullAccess() || client.hasPermission(ContentProvider.getUriWithoutUserId(uri), ContentProvider.getUserIdFromUri(uri, userId));
    }

    public void onPersistableDirty(DirtyTracker.Persistable obj) {
        this.mHandler.removeMessages(2);
        this.mHandler.obtainMessage(1, obj).sendToTarget();
        this.mHandler.sendEmptyMessageDelayed(2, 500);
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00a6, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00a7, code lost:
        if (r7 != null) goto L_0x00a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
        $closeResource(r1, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00ac, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writeBackup(org.xmlpull.v1.XmlSerializer r13) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
        /*
            r12 = this;
            monitor-enter(r12)
            java.lang.String r0 = "slice-access-list"
            r1 = 0
            r13.startTag(r1, r0)     // Catch:{ all -> 0x00b6 }
            java.lang.String r0 = "version"
            r2 = 2
            java.lang.String r3 = java.lang.String.valueOf(r2)     // Catch:{ all -> 0x00b6 }
            r13.attribute(r1, r0, r3)     // Catch:{ all -> 0x00b6 }
            com.android.server.slice.-$$Lambda$SlicePermissionManager$y3Tun5dTftw8s8sky62syeWR34U r0 = com.android.server.slice.$$Lambda$SlicePermissionManager$y3Tun5dTftw8s8sky62syeWR34U.INSTANCE     // Catch:{ all -> 0x00b6 }
            android.os.Handler r3 = r12.mHandler     // Catch:{ all -> 0x00b6 }
            boolean r3 = r3.hasMessages(r2)     // Catch:{ all -> 0x00b6 }
            if (r3 == 0) goto L_0x0025
            android.os.Handler r3 = r12.mHandler     // Catch:{ all -> 0x00b6 }
            r3.removeMessages(r2)     // Catch:{ all -> 0x00b6 }
            r12.handlePersist()     // Catch:{ all -> 0x00b6 }
        L_0x0025:
            java.io.File r3 = new java.io.File     // Catch:{ all -> 0x00b6 }
            java.io.File r4 = r12.mSliceDir     // Catch:{ all -> 0x00b6 }
            java.lang.String r4 = r4.getAbsolutePath()     // Catch:{ all -> 0x00b6 }
            r3.<init>(r4)     // Catch:{ all -> 0x00b6 }
            java.lang.String[] r3 = r3.list()     // Catch:{ all -> 0x00b6 }
            int r4 = r3.length     // Catch:{ all -> 0x00b6 }
            r5 = 0
        L_0x0036:
            if (r5 >= r4) goto L_0x00ad
            r6 = r3[r5]     // Catch:{ all -> 0x00b6 }
            com.android.server.slice.SlicePermissionManager$ParserHolder r7 = r12.getParser(r6)     // Catch:{ all -> 0x00b6 }
            r8 = r1
        L_0x003f:
            org.xmlpull.v1.XmlPullParser r9 = r7.parser     // Catch:{ all -> 0x00a4 }
            int r9 = r9.getEventType()     // Catch:{ all -> 0x00a4 }
            r10 = 1
            if (r9 == r10) goto L_0x0080
            org.xmlpull.v1.XmlPullParser r9 = r7.parser     // Catch:{ all -> 0x00a4 }
            int r9 = r9.getEventType()     // Catch:{ all -> 0x00a4 }
            if (r9 != r2) goto L_0x0078
            java.lang.String r9 = "client"
            org.xmlpull.v1.XmlPullParser r10 = r7.parser     // Catch:{ all -> 0x00a4 }
            java.lang.String r10 = r10.getName()     // Catch:{ all -> 0x00a4 }
            boolean r9 = r9.equals(r10)     // Catch:{ all -> 0x00a4 }
            if (r9 == 0) goto L_0x006e
            org.xmlpull.v1.XmlPullParser r9 = r7.parser     // Catch:{ all -> 0x00a4 }
            com.android.server.slice.SliceClientPermissions r9 = com.android.server.slice.SliceClientPermissions.createFrom(r9, r0)     // Catch:{ all -> 0x00a4 }
            r8 = r9
            goto L_0x0080
        L_0x006e:
            org.xmlpull.v1.XmlPullParser r9 = r7.parser     // Catch:{ all -> 0x00a4 }
            com.android.server.slice.SliceProviderPermissions r9 = com.android.server.slice.SliceProviderPermissions.createFrom(r9, r0)     // Catch:{ all -> 0x00a4 }
            r8 = r9
            goto L_0x0080
        L_0x0078:
            org.xmlpull.v1.XmlPullParser r9 = r7.parser     // Catch:{ all -> 0x00a4 }
            r9.next()     // Catch:{ all -> 0x00a4 }
            goto L_0x003f
        L_0x0080:
            if (r8 == 0) goto L_0x0086
            r8.writeTo(r13)     // Catch:{ all -> 0x00a4 }
            goto L_0x009c
        L_0x0086:
            java.lang.String r9 = "SlicePermissionManager"
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a4 }
            r10.<init>()     // Catch:{ all -> 0x00a4 }
            java.lang.String r11 = "Invalid or empty slice permissions file: "
            r10.append(r11)     // Catch:{ all -> 0x00a4 }
            r10.append(r6)     // Catch:{ all -> 0x00a4 }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x00a4 }
            android.util.Slog.w(r9, r10)     // Catch:{ all -> 0x00a4 }
        L_0x009c:
            if (r7 == 0) goto L_0x00a1
            $closeResource(r1, r7)     // Catch:{ all -> 0x00b6 }
        L_0x00a1:
            int r5 = r5 + 1
            goto L_0x0036
        L_0x00a4:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x00a6 }
        L_0x00a6:
            r2 = move-exception
            if (r7 == 0) goto L_0x00ac
            $closeResource(r1, r7)     // Catch:{ all -> 0x00b6 }
        L_0x00ac:
            throw r2     // Catch:{ all -> 0x00b6 }
        L_0x00ad:
            java.lang.String r2 = "slice-access-list"
            r13.endTag(r1, r2)     // Catch:{ all -> 0x00b6 }
            monitor-exit(r12)     // Catch:{ all -> 0x00b6 }
            return
        L_0x00b6:
            r0 = move-exception
            monitor-exit(r12)     // Catch:{ all -> 0x00b6 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.slice.SlicePermissionManager.writeBackup(org.xmlpull.v1.XmlSerializer):void");
    }

    static /* synthetic */ void lambda$writeBackup$0(DirtyTracker.Persistable obj) {
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

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    public void readRestore(XmlPullParser parser) throws IOException, XmlPullParserException {
        synchronized (this) {
            while (true) {
                if ((parser.getEventType() != 2 || !TAG_LIST.equals(parser.getName())) && parser.getEventType() != 1) {
                    parser.next();
                }
            }
            if (XmlUtils.readIntAttribute(parser, "version", 0) >= 2) {
                while (parser.getEventType() != 1) {
                    if (parser.getEventType() != 2) {
                        parser.next();
                    } else if ("client".equals(parser.getName())) {
                        SliceClientPermissions client = SliceClientPermissions.createFrom(parser, this);
                        synchronized (this.mCachedClients) {
                            this.mCachedClients.put(client.getPkg(), client);
                        }
                        onPersistableDirty(client);
                        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(4, client.getPkg()), 300000);
                    } else if ("provider".equals(parser.getName())) {
                        SliceProviderPermissions provider = SliceProviderPermissions.createFrom(parser, this);
                        synchronized (this.mCachedProviders) {
                            this.mCachedProviders.put(provider.getPkg(), provider);
                        }
                        onPersistableDirty(provider);
                        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(5, provider.getPkg()), 300000);
                    } else {
                        parser.next();
                    }
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:1043)
        	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:975)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
        */
    private com.android.server.slice.SliceClientPermissions getClient(com.android.server.slice.SlicePermissionManager.PkgUser r8) {
        /*
            r7 = this;
            android.util.ArrayMap<com.android.server.slice.SlicePermissionManager$PkgUser, com.android.server.slice.SliceClientPermissions> r0 = r7.mCachedClients
            monitor-enter(r0)
            android.util.ArrayMap<com.android.server.slice.SlicePermissionManager$PkgUser, com.android.server.slice.SliceClientPermissions> r1 = r7.mCachedClients     // Catch:{ all -> 0x0073 }
            java.lang.Object r1 = r1.get(r8)     // Catch:{ all -> 0x0073 }
            com.android.server.slice.SliceClientPermissions r1 = (com.android.server.slice.SliceClientPermissions) r1     // Catch:{ all -> 0x0073 }
            monitor-exit(r0)     // Catch:{ all -> 0x0073 }
            if (r1 != 0) goto L_0x0071
            java.lang.String r0 = com.android.server.slice.SliceClientPermissions.getFileName(r8)     // Catch:{ FileNotFoundException -> 0x005d, IOException -> 0x0054, XmlPullParserException -> 0x004b }
            com.android.server.slice.SlicePermissionManager$ParserHolder r0 = r7.getParser(r0)     // Catch:{ FileNotFoundException -> 0x005d, IOException -> 0x0054, XmlPullParserException -> 0x004b }
            r2 = 0
            org.xmlpull.v1.XmlPullParser r3 = r0.parser     // Catch:{ all -> 0x0042 }
            com.android.server.slice.SliceClientPermissions r3 = com.android.server.slice.SliceClientPermissions.createFrom(r3, r7)     // Catch:{ all -> 0x0042 }
            r1 = r3
            android.util.ArrayMap<com.android.server.slice.SlicePermissionManager$PkgUser, com.android.server.slice.SliceClientPermissions> r3 = r7.mCachedClients     // Catch:{ all -> 0x0042 }
            monitor-enter(r3)     // Catch:{ all -> 0x0042 }
            android.util.ArrayMap<com.android.server.slice.SlicePermissionManager$PkgUser, com.android.server.slice.SliceClientPermissions> r4 = r7.mCachedClients     // Catch:{ all -> 0x003f }
            r4.put(r8, r1)     // Catch:{ all -> 0x003f }
            monitor-exit(r3)     // Catch:{ all -> 0x003f }
            android.os.Handler r3 = r7.mHandler     // Catch:{ all -> 0x0042 }
            android.os.Handler r4 = r7.mHandler     // Catch:{ all -> 0x0042 }
            r5 = 4
            android.os.Message r4 = r4.obtainMessage(r5, r8)     // Catch:{ all -> 0x0042 }
            r5 = 300000(0x493e0, double:1.482197E-318)
            r3.sendMessageDelayed(r4, r5)     // Catch:{ all -> 0x0042 }
            if (r0 == 0) goto L_0x003e
            $closeResource(r2, r0)     // Catch:{ FileNotFoundException -> 0x005d, IOException -> 0x0054, XmlPullParserException -> 0x004b }
        L_0x003e:
            return r1
        L_0x003f:
            r2 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x003f }
            throw r2     // Catch:{ all -> 0x0042 }
        L_0x0042:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0044 }
        L_0x0044:
            r3 = move-exception
            if (r0 == 0) goto L_0x004a
            $closeResource(r2, r0)     // Catch:{ FileNotFoundException -> 0x005d, IOException -> 0x0054, XmlPullParserException -> 0x004b }
        L_0x004a:
            throw r3     // Catch:{ FileNotFoundException -> 0x005d, IOException -> 0x0054, XmlPullParserException -> 0x004b }
        L_0x004b:
            r0 = move-exception
            java.lang.String r2 = "SlicePermissionManager"
            java.lang.String r3 = "Can't read client"
            android.util.Log.e(r2, r3, r0)
            goto L_0x005f
        L_0x0054:
            r0 = move-exception
            java.lang.String r2 = "SlicePermissionManager"
            java.lang.String r3 = "Can't read client"
            android.util.Log.e(r2, r3, r0)
            goto L_0x005e
        L_0x005d:
            r0 = move-exception
        L_0x005e:
        L_0x005f:
            com.android.server.slice.SliceClientPermissions r0 = new com.android.server.slice.SliceClientPermissions
            r0.<init>(r8, r7)
            android.util.ArrayMap<com.android.server.slice.SlicePermissionManager$PkgUser, com.android.server.slice.SliceClientPermissions> r2 = r7.mCachedClients
            monitor-enter(r2)
            android.util.ArrayMap<com.android.server.slice.SlicePermissionManager$PkgUser, com.android.server.slice.SliceClientPermissions> r1 = r7.mCachedClients     // Catch:{ all -> 0x006e }
            r1.put(r8, r0)     // Catch:{ all -> 0x006e }
            monitor-exit(r2)     // Catch:{ all -> 0x006e }
            goto L_0x0072
        L_0x006e:
            r1 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x006e }
            throw r1
        L_0x0071:
            r0 = r1
        L_0x0072:
            return r0
        L_0x0073:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0073 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.slice.SlicePermissionManager.getClient(com.android.server.slice.SlicePermissionManager$PkgUser):com.android.server.slice.SliceClientPermissions");
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:1043)
        	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:975)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
        */
    private com.android.server.slice.SliceProviderPermissions getProvider(com.android.server.slice.SlicePermissionManager.PkgUser r8) {
        /*
            r7 = this;
            android.util.ArrayMap<com.android.server.slice.SlicePermissionManager$PkgUser, com.android.server.slice.SliceProviderPermissions> r0 = r7.mCachedProviders
            monitor-enter(r0)
            android.util.ArrayMap<com.android.server.slice.SlicePermissionManager$PkgUser, com.android.server.slice.SliceProviderPermissions> r1 = r7.mCachedProviders     // Catch:{ all -> 0x0073 }
            java.lang.Object r1 = r1.get(r8)     // Catch:{ all -> 0x0073 }
            com.android.server.slice.SliceProviderPermissions r1 = (com.android.server.slice.SliceProviderPermissions) r1     // Catch:{ all -> 0x0073 }
            monitor-exit(r0)     // Catch:{ all -> 0x0073 }
            if (r1 != 0) goto L_0x0071
            java.lang.String r0 = com.android.server.slice.SliceProviderPermissions.getFileName(r8)     // Catch:{ FileNotFoundException -> 0x005d, IOException -> 0x0054, XmlPullParserException -> 0x004b }
            com.android.server.slice.SlicePermissionManager$ParserHolder r0 = r7.getParser(r0)     // Catch:{ FileNotFoundException -> 0x005d, IOException -> 0x0054, XmlPullParserException -> 0x004b }
            r2 = 0
            org.xmlpull.v1.XmlPullParser r3 = r0.parser     // Catch:{ all -> 0x0042 }
            com.android.server.slice.SliceProviderPermissions r3 = com.android.server.slice.SliceProviderPermissions.createFrom(r3, r7)     // Catch:{ all -> 0x0042 }
            r1 = r3
            android.util.ArrayMap<com.android.server.slice.SlicePermissionManager$PkgUser, com.android.server.slice.SliceProviderPermissions> r3 = r7.mCachedProviders     // Catch:{ all -> 0x0042 }
            monitor-enter(r3)     // Catch:{ all -> 0x0042 }
            android.util.ArrayMap<com.android.server.slice.SlicePermissionManager$PkgUser, com.android.server.slice.SliceProviderPermissions> r4 = r7.mCachedProviders     // Catch:{ all -> 0x003f }
            r4.put(r8, r1)     // Catch:{ all -> 0x003f }
            monitor-exit(r3)     // Catch:{ all -> 0x003f }
            android.os.Handler r3 = r7.mHandler     // Catch:{ all -> 0x0042 }
            android.os.Handler r4 = r7.mHandler     // Catch:{ all -> 0x0042 }
            r5 = 5
            android.os.Message r4 = r4.obtainMessage(r5, r8)     // Catch:{ all -> 0x0042 }
            r5 = 300000(0x493e0, double:1.482197E-318)
            r3.sendMessageDelayed(r4, r5)     // Catch:{ all -> 0x0042 }
            if (r0 == 0) goto L_0x003e
            $closeResource(r2, r0)     // Catch:{ FileNotFoundException -> 0x005d, IOException -> 0x0054, XmlPullParserException -> 0x004b }
        L_0x003e:
            return r1
        L_0x003f:
            r2 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x003f }
            throw r2     // Catch:{ all -> 0x0042 }
        L_0x0042:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0044 }
        L_0x0044:
            r3 = move-exception
            if (r0 == 0) goto L_0x004a
            $closeResource(r2, r0)     // Catch:{ FileNotFoundException -> 0x005d, IOException -> 0x0054, XmlPullParserException -> 0x004b }
        L_0x004a:
            throw r3     // Catch:{ FileNotFoundException -> 0x005d, IOException -> 0x0054, XmlPullParserException -> 0x004b }
        L_0x004b:
            r0 = move-exception
            java.lang.String r2 = "SlicePermissionManager"
            java.lang.String r3 = "Can't read provider"
            android.util.Log.e(r2, r3, r0)
            goto L_0x005f
        L_0x0054:
            r0 = move-exception
            java.lang.String r2 = "SlicePermissionManager"
            java.lang.String r3 = "Can't read provider"
            android.util.Log.e(r2, r3, r0)
            goto L_0x005e
        L_0x005d:
            r0 = move-exception
        L_0x005e:
        L_0x005f:
            com.android.server.slice.SliceProviderPermissions r0 = new com.android.server.slice.SliceProviderPermissions
            r0.<init>(r8, r7)
            android.util.ArrayMap<com.android.server.slice.SlicePermissionManager$PkgUser, com.android.server.slice.SliceProviderPermissions> r2 = r7.mCachedProviders
            monitor-enter(r2)
            android.util.ArrayMap<com.android.server.slice.SlicePermissionManager$PkgUser, com.android.server.slice.SliceProviderPermissions> r1 = r7.mCachedProviders     // Catch:{ all -> 0x006e }
            r1.put(r8, r0)     // Catch:{ all -> 0x006e }
            monitor-exit(r2)     // Catch:{ all -> 0x006e }
            goto L_0x0072
        L_0x006e:
            r1 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x006e }
            throw r1
        L_0x0071:
            r0 = r1
        L_0x0072:
            return r0
        L_0x0073:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0073 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.slice.SlicePermissionManager.getProvider(com.android.server.slice.SlicePermissionManager$PkgUser):com.android.server.slice.SliceProviderPermissions");
    }

    private ParserHolder getParser(String fileName) throws FileNotFoundException, XmlPullParserException {
        AtomicFile file = getFile(fileName);
        ParserHolder holder = new ParserHolder();
        InputStream unused = holder.input = file.openRead();
        XmlPullParser unused2 = holder.parser = XmlPullParserFactory.newInstance().newPullParser();
        holder.parser.setInput(holder.input, Xml.Encoding.UTF_8.name());
        return holder;
    }

    private AtomicFile getFile(String fileName) {
        if (!this.mSliceDir.exists()) {
            this.mSliceDir.mkdir();
        }
        return new AtomicFile(new File(this.mSliceDir, fileName));
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void handlePersist() {
        synchronized (this) {
            Iterator<DirtyTracker.Persistable> it = this.mDirty.iterator();
            while (it.hasNext()) {
                DirtyTracker.Persistable persistable = it.next();
                AtomicFile file = getFile(persistable.getFileName());
                try {
                    FileOutputStream stream = file.startWrite();
                    try {
                        XmlSerializer out = XmlPullParserFactory.newInstance().newSerializer();
                        out.setOutput(stream, Xml.Encoding.UTF_8.name());
                        persistable.writeTo(out);
                        out.flush();
                        file.finishWrite(stream);
                    } catch (IOException | RuntimeException | XmlPullParserException e) {
                        Slog.w(TAG, "Failed to save access file, restoring backup", e);
                        file.failWrite(stream);
                    }
                } catch (IOException e2) {
                    Slog.w(TAG, "Failed to save access file", e2);
                    return;
                }
            }
            this.mDirty.clear();
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void addDirtyImmediate(DirtyTracker.Persistable obj) {
        this.mDirty.add(obj);
    }

    /* access modifiers changed from: private */
    public void handleRemove(PkgUser pkgUser) {
        getFile(SliceClientPermissions.getFileName(pkgUser)).delete();
        getFile(SliceProviderPermissions.getFileName(pkgUser)).delete();
        this.mDirty.remove(this.mCachedClients.remove(pkgUser));
        this.mDirty.remove(this.mCachedProviders.remove(pkgUser));
    }

    private final class H extends Handler {
        private static final int MSG_ADD_DIRTY = 1;
        private static final int MSG_CLEAR_CLIENT = 4;
        private static final int MSG_CLEAR_PROVIDER = 5;
        private static final int MSG_PERSIST = 2;
        private static final int MSG_REMOVE = 3;

        public H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                SlicePermissionManager.this.mDirty.add((DirtyTracker.Persistable) msg.obj);
            } else if (i == 2) {
                SlicePermissionManager.this.handlePersist();
            } else if (i == 3) {
                SlicePermissionManager.this.handleRemove((PkgUser) msg.obj);
            } else if (i == 4) {
                synchronized (SlicePermissionManager.this.mCachedClients) {
                    SlicePermissionManager.this.mCachedClients.remove(msg.obj);
                }
            } else if (i == 5) {
                synchronized (SlicePermissionManager.this.mCachedProviders) {
                    SlicePermissionManager.this.mCachedProviders.remove(msg.obj);
                }
            }
        }
    }

    public static class PkgUser {
        private static final String FORMAT = "%s@%d";
        private static final String SEPARATOR = "@";
        /* access modifiers changed from: private */
        public final String mPkg;
        private final int mUserId;

        public PkgUser(String pkg, int userId) {
            this.mPkg = pkg;
            this.mUserId = userId;
        }

        public PkgUser(String pkgUserStr) throws IllegalArgumentException {
            try {
                String[] vals = pkgUserStr.split(SEPARATOR, 2);
                this.mPkg = vals[0];
                this.mUserId = Integer.parseInt(vals[1]);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        public String getPkg() {
            return this.mPkg;
        }

        public int getUserId() {
            return this.mUserId;
        }

        public int hashCode() {
            return this.mPkg.hashCode() + this.mUserId;
        }

        public boolean equals(Object obj) {
            if (!getClass().equals(obj != null ? obj.getClass() : null)) {
                return false;
            }
            PkgUser other = (PkgUser) obj;
            if (!Objects.equals(other.mPkg, this.mPkg) || other.mUserId != this.mUserId) {
                return false;
            }
            return true;
        }

        public String toString() {
            return String.format(FORMAT, new Object[]{this.mPkg, Integer.valueOf(this.mUserId)});
        }
    }

    private class ParserHolder implements AutoCloseable {
        /* access modifiers changed from: private */
        public InputStream input;
        /* access modifiers changed from: private */
        public XmlPullParser parser;

        private ParserHolder() {
        }

        public void close() throws IOException {
            this.input.close();
        }
    }
}
