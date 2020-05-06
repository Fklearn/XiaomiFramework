package com.android.server.search;

import android.app.AppGlobals;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.server.LocalServices;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Searchables {
    public static String ENHANCED_GOOGLE_SEARCH_COMPONENT_NAME = "com.google.android.providers.enhancedgooglesearch/.Launcher";
    private static final Comparator<ResolveInfo> GLOBAL_SEARCH_RANKER = new Comparator<ResolveInfo>() {
        public int compare(ResolveInfo lhs, ResolveInfo rhs) {
            if (lhs == rhs) {
                return 0;
            }
            boolean lhsSystem = Searchables.isSystemApp(lhs);
            boolean rhsSystem = Searchables.isSystemApp(rhs);
            if (lhsSystem && !rhsSystem) {
                return -1;
            }
            if (!rhsSystem || lhsSystem) {
                return rhs.priority - lhs.priority;
            }
            return 1;
        }
    };
    public static String GOOGLE_SEARCH_COMPONENT_NAME = "com.android.googlesearch/.GoogleSearch";
    private static final String LOG_TAG = "Searchables";
    private static final String MD_LABEL_DEFAULT_SEARCHABLE = "android.app.default_searchable";
    private static final String MD_SEARCHABLE_SYSTEM_SEARCH = "*";
    private Context mContext;
    private ComponentName mCurrentGlobalSearchActivity = null;
    private List<ResolveInfo> mGlobalSearchActivities;
    private final IPackageManager mPm;
    private ArrayList<SearchableInfo> mSearchablesInGlobalSearchList = null;
    private ArrayList<SearchableInfo> mSearchablesList = null;
    private HashMap<ComponentName, SearchableInfo> mSearchablesMap = null;
    private int mUserId;
    private ComponentName mWebSearchActivity = null;

    public Searchables(Context context, int userId) {
        this.mContext = context;
        this.mUserId = userId;
        this.mPm = AppGlobals.getPackageManager();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0036, code lost:
        r2 = r11.mPm.getActivityInfo(r12, 128, r11.mUserId);
        r3 = null;
        r4 = r2.metaData;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003b, code lost:
        if (r4 == null) goto L_0x0043;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003d, code lost:
        r3 = r4.getString(MD_LABEL_DEFAULT_SEARCHABLE);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0043, code lost:
        if (r3 != null) goto L_0x0051;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0045, code lost:
        r4 = r2.applicationInfo.metaData;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0049, code lost:
        if (r4 == null) goto L_0x0051;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x004b, code lost:
        r3 = r4.getString(MD_LABEL_DEFAULT_SEARCHABLE);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0051, code lost:
        if (r3 == null) goto L_0x00b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0059, code lost:
        if (r3.equals(MD_SEARCHABLE_SYSTEM_SEARCH) == false) goto L_0x005c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x005b, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x005c, code lost:
        r5 = r12.getPackageName();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0067, code lost:
        if (r3.charAt(0) != '.') goto L_0x007e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0069, code lost:
        r6 = new android.content.ComponentName(r5, r5 + r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x007e, code lost:
        r6 = new android.content.ComponentName(r5, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0083, code lost:
        monitor-enter(r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        r0 = r11.mSearchablesMap.get(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x008d, code lost:
        if (r0 == null) goto L_0x0094;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x008f, code lost:
        r11.mSearchablesMap.put(r12, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0094, code lost:
        monitor-exit(r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0095, code lost:
        if (r0 == null) goto L_0x00b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00af, code lost:
        if (((android.content.pm.PackageManagerInternal) com.android.server.LocalServices.getService(android.content.pm.PackageManagerInternal.class)).canAccessComponent(android.os.Binder.getCallingUid(), r0.getSearchActivity(), android.os.UserHandle.getCallingUserId()) == false) goto L_0x00b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00b1, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00b2, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00b6, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00b7, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00b8, code lost:
        android.util.Log.e(LOG_TAG, "Error getting activity info " + r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00ce, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.app.SearchableInfo getSearchableInfo(android.content.ComponentName r12) {
        /*
            r11 = this;
            monitor-enter(r11)
            java.util.HashMap<android.content.ComponentName, android.app.SearchableInfo> r0 = r11.mSearchablesMap     // Catch:{ all -> 0x00cf }
            java.lang.Object r0 = r0.get(r12)     // Catch:{ all -> 0x00cf }
            android.app.SearchableInfo r0 = (android.app.SearchableInfo) r0     // Catch:{ all -> 0x00cf }
            r1 = 0
            if (r0 == 0) goto L_0x002a
            java.lang.Class<android.content.pm.PackageManagerInternal> r2 = android.content.pm.PackageManagerInternal.class
            java.lang.Object r2 = com.android.server.LocalServices.getService(r2)     // Catch:{ all -> 0x00cf }
            android.content.pm.PackageManagerInternal r2 = (android.content.pm.PackageManagerInternal) r2     // Catch:{ all -> 0x00cf }
            int r3 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x00cf }
            android.content.ComponentName r4 = r0.getSearchActivity()     // Catch:{ all -> 0x00cf }
            int r5 = android.os.UserHandle.getCallingUserId()     // Catch:{ all -> 0x00cf }
            boolean r3 = r2.canAccessComponent(r3, r4, r5)     // Catch:{ all -> 0x00cf }
            if (r3 == 0) goto L_0x0028
            monitor-exit(r11)     // Catch:{ all -> 0x00cf }
            return r0
        L_0x0028:
            monitor-exit(r11)     // Catch:{ all -> 0x00cf }
            return r1
        L_0x002a:
            monitor-exit(r11)     // Catch:{ all -> 0x00cf }
            r2 = 0
            android.content.pm.IPackageManager r3 = r11.mPm     // Catch:{ RemoteException -> 0x00b7 }
            r4 = 128(0x80, float:1.794E-43)
            int r5 = r11.mUserId     // Catch:{ RemoteException -> 0x00b7 }
            android.content.pm.ActivityInfo r3 = r3.getActivityInfo(r12, r4, r5)     // Catch:{ RemoteException -> 0x00b7 }
            r2 = r3
            r3 = 0
            android.os.Bundle r4 = r2.metaData
            if (r4 == 0) goto L_0x0043
            java.lang.String r5 = "android.app.default_searchable"
            java.lang.String r3 = r4.getString(r5)
        L_0x0043:
            if (r3 != 0) goto L_0x0051
            android.content.pm.ApplicationInfo r5 = r2.applicationInfo
            android.os.Bundle r4 = r5.metaData
            if (r4 == 0) goto L_0x0051
            java.lang.String r5 = "android.app.default_searchable"
            java.lang.String r3 = r4.getString(r5)
        L_0x0051:
            if (r3 == 0) goto L_0x00b6
            java.lang.String r5 = "*"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x005c
            return r1
        L_0x005c:
            java.lang.String r5 = r12.getPackageName()
            r6 = 0
            char r6 = r3.charAt(r6)
            r7 = 46
            if (r6 != r7) goto L_0x007e
            android.content.ComponentName r6 = new android.content.ComponentName
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r5)
            r7.append(r3)
            java.lang.String r7 = r7.toString()
            r6.<init>(r5, r7)
            goto L_0x0083
        L_0x007e:
            android.content.ComponentName r6 = new android.content.ComponentName
            r6.<init>(r5, r3)
        L_0x0083:
            monitor-enter(r11)
            java.util.HashMap<android.content.ComponentName, android.app.SearchableInfo> r7 = r11.mSearchablesMap     // Catch:{ all -> 0x00b3 }
            java.lang.Object r7 = r7.get(r6)     // Catch:{ all -> 0x00b3 }
            android.app.SearchableInfo r7 = (android.app.SearchableInfo) r7     // Catch:{ all -> 0x00b3 }
            r0 = r7
            if (r0 == 0) goto L_0x0094
            java.util.HashMap<android.content.ComponentName, android.app.SearchableInfo> r7 = r11.mSearchablesMap     // Catch:{ all -> 0x00b3 }
            r7.put(r12, r0)     // Catch:{ all -> 0x00b3 }
        L_0x0094:
            monitor-exit(r11)     // Catch:{ all -> 0x00b3 }
            if (r0 == 0) goto L_0x00b6
            java.lang.Class<android.content.pm.PackageManagerInternal> r7 = android.content.pm.PackageManagerInternal.class
            java.lang.Object r7 = com.android.server.LocalServices.getService(r7)
            android.content.pm.PackageManagerInternal r7 = (android.content.pm.PackageManagerInternal) r7
            int r8 = android.os.Binder.getCallingUid()
            android.content.ComponentName r9 = r0.getSearchActivity()
            int r10 = android.os.UserHandle.getCallingUserId()
            boolean r8 = r7.canAccessComponent(r8, r9, r10)
            if (r8 == 0) goto L_0x00b2
            return r0
        L_0x00b2:
            return r1
        L_0x00b3:
            r1 = move-exception
            monitor-exit(r11)     // Catch:{ all -> 0x00b3 }
            throw r1
        L_0x00b6:
            return r1
        L_0x00b7:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Error getting activity info "
            r4.append(r5)
            r4.append(r3)
            java.lang.String r4 = r4.toString()
            java.lang.String r5 = "Searchables"
            android.util.Log.e(r5, r4)
            return r1
        L_0x00cf:
            r0 = move-exception
            monitor-exit(r11)     // Catch:{ all -> 0x00cf }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.search.Searchables.getSearchableInfo(android.content.ComponentName):android.app.SearchableInfo");
    }

    /* Debug info: failed to restart local var, previous not found, register: 19 */
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
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    public void updateSearchableList() {
        /*
            r19 = this;
            r1 = r19
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r2 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r3 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r4 = r0
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r5 = "android.intent.action.SEARCH"
            r0.<init>(r5)
            r5 = r0
            long r6 = android.os.Binder.clearCallingIdentity()
            r0 = 268435584(0x10000080, float:2.5243934E-29)
            java.util.List r8 = r1.queryIntentActivities(r5, r0)     // Catch:{ all -> 0x00d4 }
            android.content.Intent r9 = new android.content.Intent     // Catch:{ all -> 0x00d4 }
            java.lang.String r10 = "android.intent.action.WEB_SEARCH"
            r9.<init>(r10)     // Catch:{ all -> 0x00d4 }
            java.util.List r0 = r1.queryIntentActivities(r9, r0)     // Catch:{ all -> 0x00d4 }
            r10 = r0
            if (r8 != 0) goto L_0x003e
            if (r10 == 0) goto L_0x0038
            goto L_0x003e
        L_0x0038:
            r17 = r5
            r18 = r8
            goto L_0x00ad
        L_0x003e:
            r0 = 0
            if (r8 != 0) goto L_0x0043
            r11 = r0
            goto L_0x0047
        L_0x0043:
            int r11 = r8.size()     // Catch:{ all -> 0x00d4 }
        L_0x0047:
            if (r10 != 0) goto L_0x004a
            goto L_0x004e
        L_0x004a:
            int r0 = r10.size()     // Catch:{ all -> 0x00d4 }
        L_0x004e:
            int r12 = r11 + r0
            r13 = 0
        L_0x0051:
            if (r13 >= r12) goto L_0x00a7
            if (r13 >= r11) goto L_0x0061
            java.lang.Object r14 = r8.get(r13)     // Catch:{ all -> 0x005c }
            android.content.pm.ResolveInfo r14 = (android.content.pm.ResolveInfo) r14     // Catch:{ all -> 0x005c }
            goto L_0x0069
        L_0x005c:
            r0 = move-exception
            r17 = r5
            goto L_0x00d7
        L_0x0061:
            int r14 = r13 - r11
            java.lang.Object r14 = r10.get(r14)     // Catch:{ all -> 0x00d4 }
            android.content.pm.ResolveInfo r14 = (android.content.pm.ResolveInfo) r14     // Catch:{ all -> 0x00d4 }
        L_0x0069:
            android.content.pm.ActivityInfo r15 = r14.activityInfo     // Catch:{ all -> 0x00d4 }
            r16 = r0
            android.content.ComponentName r0 = new android.content.ComponentName     // Catch:{ all -> 0x00d4 }
            r17 = r5
            java.lang.String r5 = r15.packageName     // Catch:{ all -> 0x00d2 }
            r18 = r8
            java.lang.String r8 = r15.name     // Catch:{ all -> 0x00d2 }
            r0.<init>(r5, r8)     // Catch:{ all -> 0x00d2 }
            java.lang.Object r0 = r2.get(r0)     // Catch:{ all -> 0x00d2 }
            if (r0 != 0) goto L_0x009e
            android.content.Context r0 = r1.mContext     // Catch:{ all -> 0x00d2 }
            int r5 = r1.mUserId     // Catch:{ all -> 0x00d2 }
            android.app.SearchableInfo r0 = android.app.SearchableInfo.getActivityMetaData(r0, r15, r5)     // Catch:{ all -> 0x00d2 }
            if (r0 == 0) goto L_0x009e
            r3.add(r0)     // Catch:{ all -> 0x00d2 }
            android.content.ComponentName r5 = r0.getSearchActivity()     // Catch:{ all -> 0x00d2 }
            r2.put(r5, r0)     // Catch:{ all -> 0x00d2 }
            boolean r5 = r0.shouldIncludeInGlobalSearch()     // Catch:{ all -> 0x00d2 }
            if (r5 == 0) goto L_0x009e
            r4.add(r0)     // Catch:{ all -> 0x00d2 }
        L_0x009e:
            int r13 = r13 + 1
            r0 = r16
            r5 = r17
            r8 = r18
            goto L_0x0051
        L_0x00a7:
            r16 = r0
            r17 = r5
            r18 = r8
        L_0x00ad:
            java.util.List r0 = r19.findGlobalSearchActivities()     // Catch:{ all -> 0x00d2 }
            r5 = r0
            android.content.ComponentName r0 = r1.findGlobalSearchActivity(r5)     // Catch:{ all -> 0x00d2 }
            r8 = r0
            android.content.ComponentName r0 = r1.findWebSearchActivity(r8)     // Catch:{ all -> 0x00d2 }
            r11 = r0
            monitor-enter(r19)     // Catch:{ all -> 0x00d2 }
            r1.mSearchablesMap = r2     // Catch:{ all -> 0x00cf }
            r1.mSearchablesList = r3     // Catch:{ all -> 0x00cf }
            r1.mSearchablesInGlobalSearchList = r4     // Catch:{ all -> 0x00cf }
            r1.mGlobalSearchActivities = r5     // Catch:{ all -> 0x00cf }
            r1.mCurrentGlobalSearchActivity = r8     // Catch:{ all -> 0x00cf }
            r1.mWebSearchActivity = r11     // Catch:{ all -> 0x00cf }
            monitor-exit(r19)     // Catch:{ all -> 0x00cf }
            android.os.Binder.restoreCallingIdentity(r6)
            return
        L_0x00cf:
            r0 = move-exception
            monitor-exit(r19)     // Catch:{ all -> 0x00cf }
            throw r0     // Catch:{ all -> 0x00d2 }
        L_0x00d2:
            r0 = move-exception
            goto L_0x00d7
        L_0x00d4:
            r0 = move-exception
            r17 = r5
        L_0x00d7:
            android.os.Binder.restoreCallingIdentity(r6)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.search.Searchables.updateSearchableList():void");
    }

    private List<ResolveInfo> findGlobalSearchActivities() {
        List<ResolveInfo> activities = queryIntentActivities(new Intent("android.search.action.GLOBAL_SEARCH"), 268500992);
        if (activities != null && !activities.isEmpty()) {
            Collections.sort(activities, GLOBAL_SEARCH_RANKER);
        }
        return activities;
    }

    private ComponentName findGlobalSearchActivity(List<ResolveInfo> installed) {
        ComponentName globalSearchComponent;
        String searchProviderSetting = getGlobalSearchProviderSetting();
        if (TextUtils.isEmpty(searchProviderSetting) || (globalSearchComponent = ComponentName.unflattenFromString(searchProviderSetting)) == null || !isInstalled(globalSearchComponent)) {
            return getDefaultGlobalSearchProvider(installed);
        }
        return globalSearchComponent;
    }

    private boolean isInstalled(ComponentName globalSearch) {
        Intent intent = new Intent("android.search.action.GLOBAL_SEARCH");
        intent.setComponent(globalSearch);
        List<ResolveInfo> activities = queryIntentActivities(intent, 65536);
        if (activities == null || activities.isEmpty()) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public static final boolean isSystemApp(ResolveInfo res) {
        return (res.activityInfo.applicationInfo.flags & 1) != 0;
    }

    private ComponentName getDefaultGlobalSearchProvider(List<ResolveInfo> providerList) {
        if (providerList == null || providerList.isEmpty()) {
            Log.w(LOG_TAG, "No global search activity found");
            return null;
        }
        ActivityInfo ai = providerList.get(0).activityInfo;
        return new ComponentName(ai.packageName, ai.name);
    }

    private String getGlobalSearchProviderSetting() {
        return Settings.Secure.getString(this.mContext.getContentResolver(), "search_global_search_activity");
    }

    private ComponentName findWebSearchActivity(ComponentName globalSearchActivity) {
        if (globalSearchActivity == null) {
            return null;
        }
        Intent intent = new Intent("android.intent.action.WEB_SEARCH");
        intent.setPackage(globalSearchActivity.getPackageName());
        List<ResolveInfo> activities = queryIntentActivities(intent, 65536);
        if (activities == null || activities.isEmpty()) {
            Log.w(LOG_TAG, "No web search activity found");
            return null;
        }
        ActivityInfo ai = activities.get(0).activityInfo;
        return new ComponentName(ai.packageName, ai.name);
    }

    private List<ResolveInfo> queryIntentActivities(Intent intent, int flags) {
        try {
            return this.mPm.queryIntentActivities(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 8388608 | flags, this.mUserId).getList();
        } catch (RemoteException e) {
            return null;
        }
    }

    public synchronized ArrayList<SearchableInfo> getSearchablesList() {
        return createFilterdSearchableInfoList(this.mSearchablesList);
    }

    public synchronized ArrayList<SearchableInfo> getSearchablesInGlobalSearchList() {
        return createFilterdSearchableInfoList(this.mSearchablesInGlobalSearchList);
    }

    public synchronized ArrayList<ResolveInfo> getGlobalSearchActivities() {
        return createFilterdResolveInfoList(this.mGlobalSearchActivities);
    }

    private ArrayList<SearchableInfo> createFilterdSearchableInfoList(List<SearchableInfo> list) {
        if (list == null) {
            return null;
        }
        ArrayList<SearchableInfo> resultList = new ArrayList<>(list.size());
        PackageManagerInternal pm = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        int callingUid = Binder.getCallingUid();
        int callingUserId = UserHandle.getCallingUserId();
        for (SearchableInfo info : list) {
            if (pm.canAccessComponent(callingUid, info.getSearchActivity(), callingUserId)) {
                resultList.add(info);
            }
        }
        return resultList;
    }

    private ArrayList<ResolveInfo> createFilterdResolveInfoList(List<ResolveInfo> list) {
        if (list == null) {
            return null;
        }
        ArrayList<ResolveInfo> resultList = new ArrayList<>(list.size());
        PackageManagerInternal pm = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        int callingUid = Binder.getCallingUid();
        int callingUserId = UserHandle.getCallingUserId();
        for (ResolveInfo info : list) {
            if (pm.canAccessComponent(callingUid, info.activityInfo.getComponentName(), callingUserId)) {
                resultList.add(info);
            }
        }
        return resultList;
    }

    public synchronized ComponentName getGlobalSearchActivity() {
        PackageManagerInternal pm = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        int callingUid = Binder.getCallingUid();
        int callingUserId = UserHandle.getCallingUserId();
        if (this.mCurrentGlobalSearchActivity == null || !pm.canAccessComponent(callingUid, this.mCurrentGlobalSearchActivity, callingUserId)) {
            return null;
        }
        return this.mCurrentGlobalSearchActivity;
    }

    public synchronized ComponentName getWebSearchActivity() {
        PackageManagerInternal pm = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        int callingUid = Binder.getCallingUid();
        int callingUserId = UserHandle.getCallingUserId();
        if (this.mWebSearchActivity == null || !pm.canAccessComponent(callingUid, this.mWebSearchActivity, callingUserId)) {
            return null;
        }
        return this.mWebSearchActivity;
    }

    /* access modifiers changed from: package-private */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("Searchable authorities:");
        synchronized (this) {
            if (this.mSearchablesList != null) {
                Iterator<SearchableInfo> it = this.mSearchablesList.iterator();
                while (it.hasNext()) {
                    pw.print("  ");
                    pw.println(it.next().getSuggestAuthority());
                }
            }
        }
    }
}
