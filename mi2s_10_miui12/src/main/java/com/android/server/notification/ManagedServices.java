package com.android.server.notification;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IntArray;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.XmlUtils;
import com.android.internal.util.function.TriPredicate;
import com.android.server.job.controllers.JobStatus;
import com.android.server.notification.NotificationManagerService;
import com.android.server.slice.SliceClientPermissions;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public abstract class ManagedServices {
    static final int APPROVAL_BY_COMPONENT = 1;
    static final int APPROVAL_BY_PACKAGE = 0;
    static final String ATT_APPROVED_LIST = "approved";
    static final String ATT_IS_PRIMARY = "primary";
    static final String ATT_USER_ID = "user";
    static final String ATT_VERSION = "version";
    static final int DB_VERSION = 1;
    protected static final String ENABLED_SERVICES_SEPARATOR = ":";
    private static final int ON_BINDING_DIED_REBIND_DELAY_MS = 10000;
    static final String TAG_MANAGED_SERVICES = "service_listing";
    protected final boolean DEBUG = Log.isLoggable(this.TAG, 3);
    protected final String TAG = getClass().getSimpleName();
    protected int mApprovalLevel;
    private ArrayMap<Integer, ArrayMap<Boolean, ArraySet<String>>> mApproved = new ArrayMap<>();
    private final Config mConfig;
    protected final Context mContext;
    /* access modifiers changed from: private */
    public ArrayMap<Integer, ArraySet<ComponentName>> mEnabledServicesForCurrentProfiles = new ArrayMap<>();
    private ArraySet<String> mEnabledServicesPackageNames = new ArraySet<>();
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler(Looper.getMainLooper());
    protected final Object mMutex;
    private final IPackageManager mPm;
    /* access modifiers changed from: private */
    public final ArrayList<ManagedServiceInfo> mServices = new ArrayList<>();
    /* access modifiers changed from: private */
    public final ArrayList<Pair<ComponentName, Integer>> mServicesBound = new ArrayList<>();
    /* access modifiers changed from: private */
    public final ArraySet<Pair<ComponentName, Integer>> mServicesRebinding = new ArraySet<>();
    private ArraySet<ComponentName> mSnoozingForCurrentProfiles = new ArraySet<>();
    protected final UserManager mUm;
    private boolean mUseXml;
    /* access modifiers changed from: private */
    public final UserProfiles mUserProfiles;

    public static class Config {
        public String bindPermission;
        public String caption;
        public int clientLabel;
        public String secondarySettingName;
        public String secureSettingName;
        public String serviceInterface;
        public String settingsAction;
        public String xmlTag;
    }

    /* access modifiers changed from: protected */
    public abstract IInterface asInterface(IBinder iBinder);

    /* access modifiers changed from: protected */
    public abstract boolean checkType(IInterface iInterface);

    /* access modifiers changed from: protected */
    public abstract Config getConfig();

    /* access modifiers changed from: protected */
    public abstract String getRequiredPermission();

    /* access modifiers changed from: protected */
    public abstract void onServiceAdded(ManagedServiceInfo managedServiceInfo);

    public ManagedServices(Context context, Object mutex, UserProfiles userProfiles, IPackageManager pm) {
        this.mContext = context;
        this.mMutex = mutex;
        this.mUserProfiles = userProfiles;
        this.mPm = pm;
        this.mConfig = getConfig();
        this.mApprovalLevel = 1;
        this.mUm = (UserManager) this.mContext.getSystemService(ATT_USER_ID);
    }

    /* access modifiers changed from: private */
    public String getCaption() {
        return this.mConfig.caption;
    }

    /* access modifiers changed from: protected */
    public List<ManagedServiceInfo> getServices() {
        List<ManagedServiceInfo> services2;
        synchronized (this.mMutex) {
            services2 = new ArrayList<>(this.mServices);
        }
        return services2;
    }

    /* access modifiers changed from: protected */
    public int getBindFlags() {
        return 83886081;
    }

    /* access modifiers changed from: protected */
    public void onServiceRemovedLocked(ManagedServiceInfo removed) {
    }

    /* access modifiers changed from: private */
    public ManagedServiceInfo newServiceInfo(IInterface service, ComponentName component, int userId, boolean isSystem, ServiceConnection connection, int targetSdkVersion) {
        return new ManagedServiceInfo(service, component, userId, isSystem, connection, targetSdkVersion);
    }

    public void onBootPhaseAppsCanStart() {
    }

    public void dump(PrintWriter pw, NotificationManagerService.DumpFilter filter) {
        pw.println("    Allowed " + getCaption() + "s:");
        int N = this.mApproved.size();
        for (int i = 0; i < N; i++) {
            int userId = this.mApproved.keyAt(i).intValue();
            ArrayMap<Boolean, ArraySet<String>> approvedByType = this.mApproved.valueAt(i);
            if (approvedByType != null) {
                int M = approvedByType.size();
                for (int j = 0; j < M; j++) {
                    boolean isPrimary = approvedByType.keyAt(j).booleanValue();
                    ArraySet<String> approved = approvedByType.valueAt(j);
                    if (approvedByType.size() > 0) {
                        pw.println("      " + String.join(ENABLED_SERVICES_SEPARATOR, approved) + " (user: " + userId + " isPrimary: " + isPrimary + ")");
                    }
                }
            }
        }
        for (Integer item : this.mEnabledServicesForCurrentProfiles.keySet()) {
            pw.println("(user " + item + "):   All " + getCaption() + "s (" + this.mEnabledServicesForCurrentProfiles.get(item).size() + ") enabled for current profiles:");
            Iterator<ComponentName> iterator = this.mEnabledServicesForCurrentProfiles.get(item).iterator();
            while (iterator.hasNext()) {
                ComponentName cmpt = iterator.next();
                if (filter == null || filter.matches(cmpt)) {
                    pw.println("      " + cmpt);
                }
            }
        }
        pw.println("    Live " + getCaption() + "s (" + this.mServices.size() + "):");
        synchronized (this.mMutex) {
            Iterator<ManagedServiceInfo> it = this.mServices.iterator();
            while (it.hasNext()) {
                ManagedServiceInfo info = it.next();
                if (filter == null || filter.matches(info.component)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("      ");
                    sb.append(info.component);
                    sb.append(" (user ");
                    sb.append(info.userid);
                    sb.append("): ");
                    sb.append(info.service);
                    sb.append(info.isSystem ? " SYSTEM" : "");
                    sb.append(info.isGuest(this) ? " GUEST" : "");
                    pw.println(sb.toString());
                }
            }
        }
        pw.println("    Snoozed " + getCaption() + "s (" + this.mSnoozingForCurrentProfiles.size() + "):");
        Iterator<ComponentName> it2 = this.mSnoozingForCurrentProfiles.iterator();
        while (it2.hasNext()) {
            pw.println("      " + it2.next().flattenToShortString());
        }
    }

    public void dump(ProtoOutputStream proto, NotificationManagerService.DumpFilter filter) {
        int M;
        ArrayMap<Boolean, ArraySet<String>> approvedByType;
        ProtoOutputStream protoOutputStream = proto;
        NotificationManagerService.DumpFilter dumpFilter = filter;
        protoOutputStream.write(1138166333441L, getCaption());
        int N = this.mApproved.size();
        for (int i = 0; i < N; i++) {
            int userId = this.mApproved.keyAt(i).intValue();
            ArrayMap<Boolean, ArraySet<String>> approvedByType2 = this.mApproved.valueAt(i);
            if (approvedByType2 != null) {
                int M2 = approvedByType2.size();
                int j = 0;
                while (j < M2) {
                    boolean isPrimary = approvedByType2.keyAt(j).booleanValue();
                    ArraySet<String> approved = approvedByType2.valueAt(j);
                    if (approvedByType2.size() > 0) {
                        long sToken = protoOutputStream.start(2246267895810L);
                        Iterator<String> it = approved.iterator();
                        while (it.hasNext()) {
                            protoOutputStream.write(2237677961217L, it.next());
                            approvedByType2 = approvedByType2;
                            M2 = M2;
                        }
                        approvedByType = approvedByType2;
                        M = M2;
                        protoOutputStream.write(1120986464258L, userId);
                        protoOutputStream.write(1133871366147L, isPrimary);
                        protoOutputStream.end(sToken);
                    } else {
                        approvedByType = approvedByType2;
                        M = M2;
                    }
                    j++;
                    approvedByType2 = approvedByType;
                    M2 = M;
                }
                int i2 = M2;
            }
        }
        for (Integer item : this.mEnabledServicesForCurrentProfiles.keySet()) {
            Iterator<ComponentName> iterator = this.mEnabledServicesForCurrentProfiles.get(item).iterator();
            while (iterator.hasNext()) {
                ComponentName cmpt = iterator.next();
                if (dumpFilter == null || dumpFilter.matches(cmpt)) {
                    cmpt.writeToProto(protoOutputStream, 2246267895811L);
                }
            }
        }
        synchronized (this.mMutex) {
            Iterator<ManagedServiceInfo> it2 = this.mServices.iterator();
            while (it2.hasNext()) {
                ManagedServiceInfo info = it2.next();
                if (dumpFilter == null || dumpFilter.matches(info.component)) {
                    info.writeToProto(protoOutputStream, 2246267895812L, this);
                }
            }
        }
        Iterator<ComponentName> it3 = this.mSnoozingForCurrentProfiles.iterator();
        while (it3.hasNext()) {
            it3.next().writeToProto(protoOutputStream, 2246267895813L);
        }
    }

    /* access modifiers changed from: protected */
    public void onSettingRestored(String element, String value, int backupSdkInt, int userId) {
        if (!this.mUseXml) {
            Slog.d(this.TAG, "Restored managed service setting: " + element);
            if (this.mConfig.secureSettingName.equals(element) || (this.mConfig.secondarySettingName != null && this.mConfig.secondarySettingName.equals(element))) {
                if (backupSdkInt < 26) {
                    String currentSetting = getApproved(userId, this.mConfig.secureSettingName.equals(element));
                    if (!TextUtils.isEmpty(currentSetting)) {
                        if (!TextUtils.isEmpty(value)) {
                            value = value + ENABLED_SERVICES_SEPARATOR + currentSetting;
                        } else {
                            value = currentSetting;
                        }
                    }
                }
                Settings.Secure.putStringForUser(this.mContext.getContentResolver(), element, value, userId);
                loadAllowedComponentsFromSettings();
                rebindServices(false, userId);
            }
        }
    }

    public void writeXml(XmlSerializer out, boolean forBackup, int userId) throws IOException {
        ArrayMap<Boolean, ArraySet<String>> approvedByType;
        XmlSerializer xmlSerializer = out;
        int i = userId;
        xmlSerializer.startTag((String) null, getConfig().xmlTag);
        xmlSerializer.attribute((String) null, ATT_VERSION, String.valueOf(1));
        if (forBackup) {
            trimApprovedListsAccordingToInstalledServices(i);
        }
        int N = this.mApproved.size();
        for (int i2 = 0; i2 < N; i2++) {
            int approvedUserId = this.mApproved.keyAt(i2).intValue();
            if ((!forBackup || approvedUserId == i) && (approvedByType = this.mApproved.valueAt(i2)) != null) {
                int M = approvedByType.size();
                for (int j = 0; j < M; j++) {
                    boolean isPrimary = approvedByType.keyAt(j).booleanValue();
                    Set<String> approved = approvedByType.valueAt(j);
                    if (approved != null) {
                        String allowedItems = String.join(ENABLED_SERVICES_SEPARATOR, approved);
                        xmlSerializer.startTag((String) null, TAG_MANAGED_SERVICES);
                        xmlSerializer.attribute((String) null, ATT_APPROVED_LIST, allowedItems);
                        xmlSerializer.attribute((String) null, ATT_USER_ID, Integer.toString(approvedUserId));
                        xmlSerializer.attribute((String) null, ATT_IS_PRIMARY, Boolean.toString(isPrimary));
                        writeExtraAttributes(xmlSerializer, approvedUserId);
                        xmlSerializer.endTag((String) null, TAG_MANAGED_SERVICES);
                        if (!forBackup && isPrimary) {
                            Settings.Secure.putStringForUser(this.mContext.getContentResolver(), getConfig().secureSettingName, allowedItems, approvedUserId);
                        }
                    }
                }
            }
        }
        writeExtraXmlTags(out);
        xmlSerializer.endTag((String) null, getConfig().xmlTag);
    }

    /* access modifiers changed from: protected */
    public void writeExtraAttributes(XmlSerializer out, int userId) throws IOException {
    }

    /* access modifiers changed from: protected */
    public void writeExtraXmlTags(XmlSerializer out) throws IOException {
    }

    /* access modifiers changed from: protected */
    public void readExtraTag(String tag, XmlPullParser parser) throws IOException {
    }

    /* access modifiers changed from: protected */
    public void migrateToXml() {
        loadAllowedComponentsFromSettings();
    }

    public void readXml(XmlPullParser parser, TriPredicate<String, Integer, String> allowedManagedServicePackages, boolean forRestore, int userId) throws XmlPullParserException, IOException {
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                break;
            }
            String tag = parser.getName();
            if (type == 3 && getConfig().xmlTag.equals(tag)) {
                break;
            } else if (type == 2) {
                if (TAG_MANAGED_SERVICES.equals(tag)) {
                    String str = this.TAG;
                    Slog.i(str, "Read " + this.mConfig.caption + " permissions from xml");
                    String approved = XmlUtils.readStringAttribute(parser, ATT_APPROVED_LIST);
                    int resolvedUserId = forRestore ? userId : XmlUtils.readIntAttribute(parser, ATT_USER_ID, 0);
                    boolean isPrimary = XmlUtils.readBooleanAttribute(parser, ATT_IS_PRIMARY, true);
                    readExtraAttributes(tag, parser, resolvedUserId);
                    if (allowedManagedServicePackages == null || allowedManagedServicePackages.test(getPackageName(approved), Integer.valueOf(resolvedUserId), getRequiredPermission())) {
                        if (this.mUm.getUserInfo(resolvedUserId) != null) {
                            addApprovedList(approved, resolvedUserId, isPrimary);
                        }
                        this.mUseXml = true;
                    }
                } else {
                    readExtraTag(tag, parser);
                }
            }
        }
        rebindServices(false, -1);
    }

    /* access modifiers changed from: protected */
    public void readExtraAttributes(String tag, XmlPullParser parser, int userId) throws IOException {
    }

    private void loadAllowedComponentsFromSettings() {
        for (UserInfo user : this.mUm.getUsers()) {
            ContentResolver cr = this.mContext.getContentResolver();
            addApprovedList(Settings.Secure.getStringForUser(cr, getConfig().secureSettingName, user.id), user.id, true);
            if (!TextUtils.isEmpty(getConfig().secondarySettingName)) {
                addApprovedList(Settings.Secure.getStringForUser(cr, getConfig().secondarySettingName, user.id), user.id, false);
            }
        }
        Slog.d(this.TAG, "Done loading approved values from settings");
    }

    /* access modifiers changed from: protected */
    public void addApprovedList(String approved, int userId, boolean isPrimary) {
        if (TextUtils.isEmpty(approved)) {
            approved = "";
        }
        ArrayMap<Boolean, ArraySet<String>> approvedByType = this.mApproved.get(Integer.valueOf(userId));
        if (approvedByType == null) {
            approvedByType = new ArrayMap<>();
            this.mApproved.put(Integer.valueOf(userId), approvedByType);
        }
        ArraySet<String> approvedList = approvedByType.get(Boolean.valueOf(isPrimary));
        if (approvedList == null) {
            approvedList = new ArraySet<>();
            approvedByType.put(Boolean.valueOf(isPrimary), approvedList);
        }
        for (String pkgOrComponent : approved.split(ENABLED_SERVICES_SEPARATOR)) {
            String approvedItem = getApprovedValue(pkgOrComponent);
            if (approvedItem != null) {
                approvedList.add(approvedItem);
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean isComponentEnabledForPackage(String pkg) {
        return this.mEnabledServicesPackageNames.contains(pkg);
    }

    /* access modifiers changed from: protected */
    public void setPackageOrComponentEnabled(String pkgOrComponent, int userId, boolean isPrimary, boolean enabled) {
        String str = this.TAG;
        StringBuilder sb = new StringBuilder();
        sb.append(enabled ? " Allowing " : "Disallowing ");
        sb.append(this.mConfig.caption);
        sb.append(" ");
        sb.append(pkgOrComponent);
        Slog.i(str, sb.toString());
        ArrayMap<Boolean, ArraySet<String>> allowedByType = this.mApproved.get(Integer.valueOf(userId));
        if (allowedByType == null) {
            allowedByType = new ArrayMap<>();
            this.mApproved.put(Integer.valueOf(userId), allowedByType);
        }
        ArraySet<String> approved = allowedByType.get(Boolean.valueOf(isPrimary));
        if (approved == null) {
            approved = new ArraySet<>();
            allowedByType.put(Boolean.valueOf(isPrimary), approved);
        }
        String approvedItem = getApprovedValue(pkgOrComponent);
        if (approvedItem != null) {
            if (enabled) {
                approved.add(approvedItem);
            } else {
                approved.remove(approvedItem);
            }
        }
        rebindServices(false, userId);
    }

    private String getApprovedValue(String pkgOrComponent) {
        if (this.mApprovalLevel != 1) {
            return getPackageName(pkgOrComponent);
        }
        if (ComponentName.unflattenFromString(pkgOrComponent) != null) {
            return pkgOrComponent;
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public String getApproved(int userId, boolean primary) {
        return String.join(ENABLED_SERVICES_SEPARATOR, (ArraySet) ((ArrayMap) this.mApproved.getOrDefault(Integer.valueOf(userId), new ArrayMap())).getOrDefault(Boolean.valueOf(primary), new ArraySet()));
    }

    /* access modifiers changed from: protected */
    public List<ComponentName> getAllowedComponents(int userId) {
        List<ComponentName> allowedComponents = new ArrayList<>();
        ArrayMap<Boolean, ArraySet<String>> allowedByType = (ArrayMap) this.mApproved.getOrDefault(Integer.valueOf(userId), new ArrayMap());
        for (int i = 0; i < allowedByType.size(); i++) {
            ArraySet<String> allowed = allowedByType.valueAt(i);
            for (int j = 0; j < allowed.size(); j++) {
                ComponentName cn = ComponentName.unflattenFromString(allowed.valueAt(j));
                if (cn != null) {
                    allowedComponents.add(cn);
                }
            }
        }
        return allowedComponents;
    }

    /* access modifiers changed from: protected */
    public List<String> getAllowedPackages(int userId) {
        List<String> allowedPackages = new ArrayList<>();
        ArrayMap<Boolean, ArraySet<String>> allowedByType = (ArrayMap) this.mApproved.getOrDefault(Integer.valueOf(userId), new ArrayMap());
        for (int i = 0; i < allowedByType.size(); i++) {
            ArraySet<String> allowed = allowedByType.valueAt(i);
            for (int j = 0; j < allowed.size(); j++) {
                String pkgName = getPackageName(allowed.valueAt(j));
                if (!TextUtils.isEmpty(pkgName)) {
                    allowedPackages.add(pkgName);
                }
            }
        }
        return allowedPackages;
    }

    /* access modifiers changed from: protected */
    public boolean isPackageOrComponentAllowed(String pkgOrComponent, int userId) {
        ArrayMap<Boolean, ArraySet<String>> allowedByType = (ArrayMap) this.mApproved.getOrDefault(Integer.valueOf(userId), new ArrayMap());
        for (int i = 0; i < allowedByType.size(); i++) {
            if (allowedByType.valueAt(i).contains(pkgOrComponent)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isPackageAllowed(String pkg, int userId) {
        if (pkg == null) {
            return false;
        }
        ArrayMap<Boolean, ArraySet<String>> allowedByType = (ArrayMap) this.mApproved.getOrDefault(Integer.valueOf(userId), new ArrayMap());
        for (int i = 0; i < allowedByType.size(); i++) {
            Iterator<String> it = allowedByType.valueAt(i).iterator();
            while (it.hasNext()) {
                String allowedEntry = it.next();
                ComponentName component = ComponentName.unflattenFromString(allowedEntry);
                if (component != null) {
                    if (pkg.equals(component.getPackageName())) {
                        return true;
                    }
                } else if (pkg.equals(allowedEntry)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onPackagesChanged(boolean removingPackage, String[] pkgList, int[] uidList) {
        if (this.DEBUG) {
            String str = this.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onPackagesChanged removingPackage=");
            sb.append(removingPackage);
            sb.append(" pkgList=");
            sb.append(pkgList == null ? null : Arrays.asList(pkgList));
            sb.append(" mEnabledServicesPackageNames=");
            sb.append(this.mEnabledServicesPackageNames);
            Slog.d(str, sb.toString());
        }
        if (pkgList != null && pkgList.length > 0) {
            boolean anyServicesInvolved = false;
            if (removingPackage && uidList != null) {
                int size = Math.min(pkgList.length, uidList.length);
                for (int i = 0; i < size; i++) {
                    anyServicesInvolved = removeUninstalledItemsFromApprovedLists(UserHandle.getUserId(uidList[i]), pkgList[i]);
                }
            }
            boolean anyServicesInvolved2 = anyServicesInvolved;
            for (String pkgName : pkgList) {
                if (this.mEnabledServicesPackageNames.contains(pkgName)) {
                    anyServicesInvolved2 = true;
                }
                if (uidList != null && uidList.length > 0) {
                    boolean anyServicesInvolved3 = anyServicesInvolved2;
                    for (int uid : uidList) {
                        if (isPackageAllowed(pkgName, UserHandle.getUserId(uid))) {
                            anyServicesInvolved3 = true;
                        }
                    }
                    anyServicesInvolved2 = anyServicesInvolved3;
                }
            }
            if (anyServicesInvolved2) {
                rebindServices(false, -1);
            }
        }
    }

    public void onUserRemoved(int user) {
        String str = this.TAG;
        Slog.i(str, "Removing approved services for removed user " + user);
        this.mApproved.remove(Integer.valueOf(user));
        rebindServices(true, user);
    }

    public void onUserSwitched(int user) {
        if (this.DEBUG) {
            String str = this.TAG;
            Slog.d(str, "onUserSwitched u=" + user);
        }
        rebindServices(true, user);
    }

    public void onUserUnlocked(int user) {
        if (this.DEBUG) {
            String str = this.TAG;
            Slog.d(str, "onUserUnlocked u=" + user);
        }
        rebindServices(false, user);
    }

    private ManagedServiceInfo getServiceFromTokenLocked(IInterface service) {
        if (service == null) {
            return null;
        }
        IBinder token = service.asBinder();
        int N = this.mServices.size();
        for (int i = 0; i < N; i++) {
            ManagedServiceInfo info = this.mServices.get(i);
            if (info.service.asBinder() == token) {
                return info;
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isServiceTokenValidLocked(IInterface service) {
        if (service == null || getServiceFromTokenLocked(service) == null) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public ManagedServiceInfo checkServiceTokenLocked(IInterface service) {
        checkNotNull(service);
        ManagedServiceInfo info = getServiceFromTokenLocked(service);
        if (info != null) {
            return info;
        }
        throw new SecurityException("Disallowed call from unknown " + getCaption() + ": " + service + " " + service.getClass());
    }

    public boolean isSameUser(IInterface service, int userId) {
        checkNotNull(service);
        synchronized (this.mMutex) {
            ManagedServiceInfo info = getServiceFromTokenLocked(service);
            if (info == null) {
                return false;
            }
            boolean isSameUser = info.isSameUser(userId);
            return isSameUser;
        }
    }

    public void unregisterService(IInterface service, int userid) {
        checkNotNull(service);
        unregisterServiceImpl(service, userid);
    }

    public void registerService(IInterface service, ComponentName component, int userid) {
        checkNotNull(service);
        ManagedServiceInfo info = registerServiceImpl(service, component, userid);
        if (info != null) {
            onServiceAdded(info);
        }
    }

    /* access modifiers changed from: protected */
    public void registerGuestService(ManagedServiceInfo guest) {
        checkNotNull(guest.service);
        if (!checkType(guest.service)) {
            throw new IllegalArgumentException();
        } else if (registerServiceImpl(guest) != null) {
            onServiceAdded(guest);
        }
    }

    /* access modifiers changed from: protected */
    public void setComponentState(ComponentName component, boolean enabled) {
        if ((!this.mSnoozingForCurrentProfiles.contains(component)) != enabled) {
            if (enabled) {
                this.mSnoozingForCurrentProfiles.remove(component);
            } else {
                this.mSnoozingForCurrentProfiles.add(component);
            }
            String str = this.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append(enabled ? "Enabling " : "Disabling ");
            sb.append("component ");
            sb.append(component.flattenToShortString());
            Slog.d(str, sb.toString());
            synchronized (this.mMutex) {
                IntArray userIds = this.mUserProfiles.getCurrentProfileIds();
                for (int i = 0; i < userIds.size(); i++) {
                    int userId = userIds.get(i);
                    if (enabled) {
                        if (!isPackageOrComponentAllowed(component.flattenToString(), userId)) {
                            if (!isPackageOrComponentAllowed(component.getPackageName(), userId)) {
                                String str2 = this.TAG;
                                Slog.d(str2, component + " no longer has permission to be bound");
                            }
                        }
                        registerServiceLocked(component, userId);
                    } else {
                        unregisterServiceLocked(component, userId);
                    }
                }
            }
        }
    }

    private ArraySet<ComponentName> loadComponentNamesFromValues(ArraySet<String> approved, int userId) {
        if (approved == null || approved.size() == 0) {
            return new ArraySet<>();
        }
        ArraySet<ComponentName> result = new ArraySet<>(approved.size());
        for (int i = 0; i < approved.size(); i++) {
            String packageOrComponent = approved.valueAt(i);
            if (!TextUtils.isEmpty(packageOrComponent)) {
                ComponentName component = ComponentName.unflattenFromString(packageOrComponent);
                if (component != null) {
                    result.add(component);
                } else {
                    result.addAll(queryPackageForServices(packageOrComponent, userId));
                }
            }
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public Set<ComponentName> queryPackageForServices(String packageName, int userId) {
        return queryPackageForServices(packageName, 0, userId);
    }

    /* access modifiers changed from: protected */
    public Set<ComponentName> queryPackageForServices(String packageName, int extraFlags, int userId) {
        Set<ComponentName> installed = new ArraySet<>();
        PackageManager pm = this.mContext.getPackageManager();
        Intent queryIntent = new Intent(this.mConfig.serviceInterface);
        if (!TextUtils.isEmpty(packageName)) {
            queryIntent.setPackage(packageName);
        }
        List<ResolveInfo> installedServices = pm.queryIntentServicesAsUser(queryIntent, extraFlags | 132, userId);
        if (this.DEBUG) {
            String str = this.TAG;
            Slog.v(str, this.mConfig.serviceInterface + " services: " + installedServices);
        }
        if (installedServices != null) {
            int count = installedServices.size();
            for (int i = 0; i < count; i++) {
                ServiceInfo info = installedServices.get(i).serviceInfo;
                ComponentName component = new ComponentName(info.packageName, info.name);
                if (!this.mConfig.bindPermission.equals(info.permission)) {
                    String str2 = this.TAG;
                    Slog.w(str2, "Skipping " + getCaption() + " service " + info.packageName + SliceClientPermissions.SliceAuthority.DELIMITER + info.name + ": it does not require the permission " + this.mConfig.bindPermission);
                } else {
                    installed.add(component);
                }
            }
        }
        return installed;
    }

    /* access modifiers changed from: protected */
    public Set<String> getAllowedPackages() {
        Set<String> allowedPackages = new ArraySet<>();
        for (int k = 0; k < this.mApproved.size(); k++) {
            ArrayMap<Boolean, ArraySet<String>> allowedByType = this.mApproved.valueAt(k);
            for (int i = 0; i < allowedByType.size(); i++) {
                ArraySet<String> allowed = allowedByType.valueAt(i);
                for (int j = 0; j < allowed.size(); j++) {
                    String pkgName = getPackageName(allowed.valueAt(j));
                    if (!TextUtils.isEmpty(pkgName)) {
                        allowedPackages.add(pkgName);
                    }
                }
            }
        }
        return allowedPackages;
    }

    private void trimApprovedListsAccordingToInstalledServices(int userId) {
        ArrayMap<Boolean, ArraySet<String>> approvedByType = this.mApproved.get(Integer.valueOf(userId));
        if (approvedByType != null) {
            for (int i = 0; i < approvedByType.size(); i++) {
                ArraySet<String> approved = approvedByType.valueAt(i);
                for (int j = approved.size() - 1; j >= 0; j--) {
                    String approvedPackageOrComponent = approved.valueAt(j);
                    if (!isValidEntry(approvedPackageOrComponent, userId)) {
                        approved.removeAt(j);
                        Slog.v(this.TAG, "Removing " + approvedPackageOrComponent + " from approved list; no matching services found");
                    } else if (this.DEBUG) {
                        Slog.v(this.TAG, "Keeping " + approvedPackageOrComponent + " on approved list; matching services found");
                    }
                }
            }
        }
    }

    private boolean removeUninstalledItemsFromApprovedLists(int uninstalledUserId, String pkg) {
        ArrayMap<Boolean, ArraySet<String>> approvedByType = this.mApproved.get(Integer.valueOf(uninstalledUserId));
        if (approvedByType != null) {
            int M = approvedByType.size();
            for (int j = 0; j < M; j++) {
                ArraySet<String> approved = approvedByType.valueAt(j);
                for (int k = approved.size() - 1; k >= 0; k--) {
                    String packageOrComponent = approved.valueAt(k);
                    if (TextUtils.equals(pkg, getPackageName(packageOrComponent))) {
                        approved.removeAt(k);
                        if (this.DEBUG) {
                            Slog.v(this.TAG, "Removing " + packageOrComponent + " from approved list; uninstalled");
                        }
                    }
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public String getPackageName(String packageOrComponent) {
        ComponentName component = ComponentName.unflattenFromString(packageOrComponent);
        if (component != null) {
            return component.getPackageName();
        }
        return packageOrComponent;
    }

    /* access modifiers changed from: protected */
    public boolean isValidEntry(String packageOrComponent, int userId) {
        return hasMatchingServices(packageOrComponent, userId);
    }

    private boolean hasMatchingServices(String packageOrComponent, int userId) {
        if (TextUtils.isEmpty(packageOrComponent) || queryPackageForServices(getPackageName(packageOrComponent), userId).size() <= 0) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public SparseArray<ArraySet<ComponentName>> getAllowedComponents(IntArray userIds) {
        int nUserIds = userIds.size();
        SparseArray<ArraySet<ComponentName>> componentsByUser = new SparseArray<>();
        for (int i = 0; i < nUserIds; i++) {
            int userId = userIds.get(i);
            ArrayMap<Boolean, ArraySet<String>> approvedLists = this.mApproved.get(Integer.valueOf(userId));
            if (approvedLists != null) {
                int N = approvedLists.size();
                for (int j = 0; j < N; j++) {
                    ArraySet<ComponentName> approvedByUser = componentsByUser.get(userId);
                    if (approvedByUser == null) {
                        approvedByUser = new ArraySet<>();
                        componentsByUser.put(userId, approvedByUser);
                    }
                    approvedByUser.addAll(loadComponentNamesFromValues(approvedLists.valueAt(j), userId));
                }
            }
        }
        return componentsByUser;
    }

    /* access modifiers changed from: protected */
    @GuardedBy({"mMutex"})
    public void populateComponentsToBind(SparseArray<Set<ComponentName>> componentsToBind, IntArray activeUsers, SparseArray<ArraySet<ComponentName>> approvedComponentsByUser) {
        int[] activeUsersArray = this.mUserProfiles.getCurrentProfileIds().toArray();
        for (Integer item : this.mEnabledServicesForCurrentProfiles.keySet()) {
            if (!ArrayUtils.contains(activeUsersArray, item.intValue())) {
                this.mEnabledServicesForCurrentProfiles.put(Integer.valueOf(item.intValue()), new ArraySet());
            }
        }
        this.mEnabledServicesPackageNames.clear();
        int nUserIds = activeUsers.size();
        for (int i = 0; i < nUserIds; i++) {
            int userId = activeUsers.get(i);
            ArraySet<ComponentName> userComponents = approvedComponentsByUser.get(userId);
            if (userComponents == null) {
                this.mEnabledServicesForCurrentProfiles.put(Integer.valueOf(userId), new ArraySet());
                componentsToBind.put(userId, new ArraySet());
            } else {
                Set<ComponentName> add = new HashSet<>(userComponents);
                add.removeAll(this.mSnoozingForCurrentProfiles);
                componentsToBind.put(userId, add);
                ArraySet<ComponentName> componentNameCurrentUserId = new ArraySet<>();
                this.mEnabledServicesForCurrentProfiles.put(Integer.valueOf(userId), componentNameCurrentUserId);
                for (int j = 0; j < userComponents.size(); j++) {
                    ComponentName component = userComponents.valueAt(j);
                    componentNameCurrentUserId.add(component);
                    this.mEnabledServicesPackageNames.add(component.getPackageName());
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    @GuardedBy({"mMutex"})
    public Set<ManagedServiceInfo> getRemovableConnectedServices() {
        Set<ManagedServiceInfo> removableBoundServices = new ArraySet<>();
        Iterator<ManagedServiceInfo> it = this.mServices.iterator();
        while (it.hasNext()) {
            ManagedServiceInfo service = it.next();
            if (!service.isSystem && !service.isGuest(this)) {
                removableBoundServices.add(service);
            }
        }
        return removableBoundServices;
    }

    /* access modifiers changed from: protected */
    public void populateComponentsToUnbind(boolean forceRebind, Set<ManagedServiceInfo> removableBoundServices, SparseArray<Set<ComponentName>> allowedComponentsToBind, SparseArray<Set<ComponentName>> componentsToUnbind) {
        for (ManagedServiceInfo info : removableBoundServices) {
            Set<ComponentName> allowedComponents = allowedComponentsToBind.get(info.userid);
            if (allowedComponents != null && (forceRebind || !allowedComponents.contains(info.component))) {
                Set<ComponentName> toUnbind = componentsToUnbind.get(info.userid, new ArraySet());
                toUnbind.add(info.component);
                componentsToUnbind.put(info.userid, toUnbind);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void rebindServices(boolean forceRebind, int userToRebind) {
        if (this.DEBUG) {
            String str = this.TAG;
            Slog.d(str, "rebindServices " + forceRebind + " " + userToRebind);
        }
        IntArray userIds = this.mUserProfiles.getCurrentProfileIds();
        if (userToRebind != -1) {
            userIds = new IntArray(1);
            userIds.add(userToRebind);
        }
        SparseArray<Set<ComponentName>> componentsToBind = new SparseArray<>();
        SparseArray<Set<ComponentName>> componentsToUnbind = new SparseArray<>();
        synchronized (this.mMutex) {
            SparseArray<ArraySet<ComponentName>> approvedComponentsByUser = getAllowedComponents(userIds);
            Set<ManagedServiceInfo> removableBoundServices = getRemovableConnectedServices();
            populateComponentsToBind(componentsToBind, userIds, approvedComponentsByUser);
            populateComponentsToUnbind(forceRebind, removableBoundServices, componentsToBind, componentsToUnbind);
        }
        unbindFromServices(componentsToUnbind);
        bindToServices(componentsToBind);
    }

    /* access modifiers changed from: protected */
    public void unbindFromServices(SparseArray<Set<ComponentName>> componentsToUnbind) {
        for (int i = 0; i < componentsToUnbind.size(); i++) {
            int userId = componentsToUnbind.keyAt(i);
            for (ComponentName cn : componentsToUnbind.get(userId)) {
                String str = this.TAG;
                Slog.v(str, "disabling " + getCaption() + " for user " + userId + ": " + cn);
                unregisterService(cn, userId);
            }
        }
    }

    private void bindToServices(SparseArray<Set<ComponentName>> componentsToBind) {
        for (int i = 0; i < componentsToBind.size(); i++) {
            int userId = componentsToBind.keyAt(i);
            for (ComponentName component : componentsToBind.get(userId)) {
                try {
                    ServiceInfo info = this.mPm.getServiceInfo(component, 786432, userId);
                    if (info == null) {
                        String str = this.TAG;
                        Slog.w(str, "Not binding " + getCaption() + " service " + component + ": service not found");
                    } else if (!this.mConfig.bindPermission.equals(info.permission)) {
                        String str2 = this.TAG;
                        Slog.w(str2, "Not binding " + getCaption() + " service " + component + ": it does not require the permission " + this.mConfig.bindPermission);
                    } else {
                        String str3 = this.TAG;
                        Slog.v(str3, "enabling " + getCaption() + " for " + userId + ": " + component);
                        registerService(component, userId);
                    }
                } catch (RemoteException e) {
                    e.rethrowFromSystemServer();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void registerService(ComponentName name, int userid) {
        synchronized (this.mMutex) {
            registerServiceLocked(name, userid);
        }
    }

    public void registerSystemService(ComponentName name, int userid) {
        synchronized (this.mMutex) {
            registerServiceLocked(name, userid, true);
        }
    }

    private void registerServiceLocked(ComponentName name, int userid) {
        registerServiceLocked(name, userid, false);
    }

    private void registerServiceLocked(ComponentName name, int userid, boolean isSystem) {
        ApplicationInfo appInfo;
        ComponentName componentName = name;
        int i = userid;
        if (this.DEBUG) {
            Slog.v(this.TAG, "registerService: " + componentName + " u=" + i);
        }
        Pair<ComponentName, Integer> servicesBindingTag = Pair.create(componentName, Integer.valueOf(userid));
        if (this.mServicesBound.contains(servicesBindingTag)) {
            Slog.v(this.TAG, "Not registering " + componentName + " is already bound");
            return;
        }
        this.mServicesBound.add(servicesBindingTag);
        for (int i2 = this.mServices.size() - 1; i2 >= 0; i2--) {
            ManagedServiceInfo info = this.mServices.get(i2);
            if (componentName.equals(info.component) && info.userid == i) {
                Slog.v(this.TAG, "    disconnecting old " + getCaption() + ": " + info.service);
                removeServiceLocked(i2);
                if (info.connection != null) {
                    unbindService(info.connection, info.component, info.userid);
                }
            }
        }
        Intent intent = new Intent(this.mConfig.serviceInterface);
        intent.setComponent(componentName);
        intent.putExtra("android.intent.extra.client_label", this.mConfig.clientLabel);
        intent.putExtra("android.intent.extra.client_intent", PendingIntent.getActivity(this.mContext, 0, new Intent(this.mConfig.settingsAction), 0));
        try {
            appInfo = this.mContext.getPackageManager().getApplicationInfo(name.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            appInfo = null;
        }
        final int targetSdkVersion = appInfo != null ? appInfo.targetSdkVersion : 1;
        try {
            if (!ManagedServicesInjector.canBindService(this.mContext, intent, i)) {
                try {
                    if (SystemProperties.getBoolean("persist.sys.miui_optimization", true)) {
                        this.mServicesBound.remove(servicesBindingTag);
                        Slog.w(this.TAG, "AutStart Unable to bind " + getCaption() + " service: " + intent);
                        return;
                    }
                } catch (SecurityException e2) {
                    ex = e2;
                    ApplicationInfo applicationInfo = appInfo;
                    this.mServicesBound.remove(servicesBindingTag);
                    Slog.e(this.TAG, "Unable to bind " + getCaption() + " service: " + intent, ex);
                }
            }
            Slog.v(this.TAG, "binding: " + intent);
            final int i3 = userid;
            final Pair<ComponentName, Integer> pair = servicesBindingTag;
            ApplicationInfo applicationInfo2 = appInfo;
            final boolean z = isSystem;
            try {
                AnonymousClass1 r1 = new ServiceConnection() {
                    IInterface mService;

                    public void onServiceConnected(ComponentName name, IBinder binder) {
                        String str = ManagedServices.this.TAG;
                        Slog.v(str, i3 + " " + ManagedServices.this.getCaption() + " service connected: " + name);
                        boolean added = false;
                        ManagedServiceInfo info = null;
                        synchronized (ManagedServices.this.mMutex) {
                            ManagedServices.this.mServicesRebinding.remove(pair);
                            try {
                                this.mService = ManagedServices.this.asInterface(binder);
                                info = ManagedServices.this.newServiceInfo(this.mService, name, i3, z, this, targetSdkVersion);
                                binder.linkToDeath(info, 0);
                                added = ManagedServices.this.mServices.add(info);
                            } catch (RemoteException e) {
                                Slog.e(ManagedServices.this.TAG, "Failed to linkToDeath, already dead", e);
                            }
                        }
                        if (added) {
                            ManagedServices.this.onServiceAdded(info);
                        }
                    }

                    public void onServiceDisconnected(ComponentName name) {
                        String str = ManagedServices.this.TAG;
                        Slog.v(str, i3 + " " + ManagedServices.this.getCaption() + " connection lost: " + name);
                        ManagedServices.this.mServicesBound.remove(pair);
                    }

                    public void onBindingDied(final ComponentName name) {
                        String str = ManagedServices.this.TAG;
                        Slog.w(str, i3 + " " + ManagedServices.this.getCaption() + " binding died: " + name);
                        synchronized (ManagedServices.this.mMutex) {
                            ManagedServices.this.unbindService(this, name, i3);
                            if (!ManagedServices.this.mServicesRebinding.contains(pair)) {
                                ManagedServices.this.mServicesRebinding.add(pair);
                                ManagedServices.this.mHandler.postDelayed(new Runnable() {
                                    public void run() {
                                        ManagedServices.this.registerService(name, i3);
                                    }
                                }, JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
                            } else {
                                String str2 = ManagedServices.this.TAG;
                                Slog.v(str2, ManagedServices.this.getCaption() + " not rebinding in user " + i3 + " as a previous rebind attempt was made: " + name);
                            }
                        }
                    }

                    public void onNullBinding(ComponentName name) {
                        String str = ManagedServices.this.TAG;
                        Slog.v(str, "onNullBinding() called with: name = [" + name + "]");
                        ManagedServices.this.mServicesBound.remove(pair);
                    }
                };
                if (!this.mContext.bindServiceAsUser(intent, r1, getBindFlags(), new UserHandle(i))) {
                    this.mServicesBound.remove(servicesBindingTag);
                    Slog.w(this.TAG, "Unable to bind " + getCaption() + " service: " + intent + " in user " + i);
                }
            } catch (SecurityException e3) {
                ex = e3;
                this.mServicesBound.remove(servicesBindingTag);
                Slog.e(this.TAG, "Unable to bind " + getCaption() + " service: " + intent, ex);
            }
        } catch (SecurityException e4) {
            ex = e4;
            ApplicationInfo applicationInfo3 = appInfo;
            this.mServicesBound.remove(servicesBindingTag);
            Slog.e(this.TAG, "Unable to bind " + getCaption() + " service: " + intent, ex);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isBound(ComponentName cn, int userId) {
        return this.mServicesBound.contains(Pair.create(cn, Integer.valueOf(userId)));
    }

    private void unregisterService(ComponentName name, int userid) {
        synchronized (this.mMutex) {
            unregisterServiceLocked(name, userid);
        }
    }

    private void unregisterServiceLocked(ComponentName name, int userid) {
        for (int i = this.mServices.size() - 1; i >= 0; i--) {
            ManagedServiceInfo info = this.mServices.get(i);
            if (name.equals(info.component) && info.userid == userid) {
                removeServiceLocked(i);
                if (info.connection != null) {
                    unbindService(info.connection, info.component, info.userid);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public ManagedServiceInfo removeServiceImpl(IInterface service, int userid) {
        if (this.DEBUG) {
            Slog.d(this.TAG, "removeServiceImpl service=" + service + " u=" + userid);
        }
        ManagedServiceInfo serviceInfo = null;
        synchronized (this.mMutex) {
            for (int i = this.mServices.size() - 1; i >= 0; i--) {
                ManagedServiceInfo info = this.mServices.get(i);
                if (info.service.asBinder() == service.asBinder() && info.userid == userid) {
                    Slog.d(this.TAG, "Removing active service " + info.component);
                    serviceInfo = removeServiceLocked(i);
                }
            }
        }
        return serviceInfo;
    }

    private ManagedServiceInfo removeServiceLocked(int i) {
        ManagedServiceInfo info = this.mServices.remove(i);
        onServiceRemovedLocked(info);
        return info;
    }

    private void checkNotNull(IInterface service) {
        if (service == null) {
            throw new IllegalArgumentException(getCaption() + " must not be null");
        }
    }

    private ManagedServiceInfo registerServiceImpl(IInterface service, ComponentName component, int userid) {
        return registerServiceImpl(newServiceInfo(service, component, userid, true, (ServiceConnection) null, 21));
    }

    private ManagedServiceInfo registerServiceImpl(ManagedServiceInfo info) {
        synchronized (this.mMutex) {
            try {
                info.service.asBinder().linkToDeath(info, 0);
                this.mServices.add(info);
            } catch (RemoteException e) {
                return null;
            } catch (Throwable th) {
                throw th;
            }
        }
        return info;
    }

    private void unregisterServiceImpl(IInterface service, int userid) {
        ManagedServiceInfo info = removeServiceImpl(service, userid);
        if (info != null && info.connection != null && !info.isGuest(this)) {
            unbindService(info.connection, info.component, info.userid);
        }
    }

    /* access modifiers changed from: private */
    public void unbindService(ServiceConnection connection, ComponentName component, int userId) {
        try {
            this.mContext.unbindService(connection);
        } catch (IllegalArgumentException e) {
            String str = this.TAG;
            Slog.e(str, getCaption() + " " + component + " could not be unbound", e);
        }
        synchronized (this.mMutex) {
            this.mServicesBound.remove(Pair.create(component, Integer.valueOf(userId)));
        }
    }

    public class ManagedServiceInfo implements IBinder.DeathRecipient {
        public ComponentName component;
        public ServiceConnection connection;
        public boolean isSystem;
        public IInterface service;
        public int targetSdkVersion;
        public int userid;

        public ManagedServiceInfo(IInterface service2, ComponentName component2, int userid2, boolean isSystem2, ServiceConnection connection2, int targetSdkVersion2) {
            this.service = service2;
            this.component = component2;
            this.userid = userid2;
            this.isSystem = isSystem2;
            this.connection = connection2;
            this.targetSdkVersion = targetSdkVersion2;
        }

        public boolean isGuest(ManagedServices host) {
            return ManagedServices.this != host;
        }

        public ManagedServices getOwner() {
            return ManagedServices.this;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("ManagedServiceInfo[");
            sb.append("component=");
            sb.append(this.component);
            sb.append(",userid=");
            sb.append(this.userid);
            sb.append(",isSystem=");
            sb.append(this.isSystem);
            sb.append(",targetSdkVersion=");
            sb.append(this.targetSdkVersion);
            sb.append(",connection=");
            sb.append(this.connection == null ? null : "<connection>");
            sb.append(",service=");
            sb.append(this.service);
            sb.append(']');
            return sb.toString();
        }

        public void writeToProto(ProtoOutputStream proto, long fieldId, ManagedServices host) {
            long token = proto.start(fieldId);
            this.component.writeToProto(proto, 1146756268033L);
            proto.write(1120986464258L, this.userid);
            proto.write(1138166333443L, this.service.getClass().getName());
            proto.write(1133871366148L, this.isSystem);
            proto.write(1133871366149L, isGuest(host));
            proto.end(token);
        }

        public boolean isSameUser(int userId) {
            if (isEnabledForCurrentProfiles() && this.userid == userId) {
                return true;
            }
            return false;
        }

        public boolean enabledAndUserMatches(int nid) {
            if (!isEnabledForCurrentProfiles()) {
                return false;
            }
            int i = this.userid;
            if (i == -1 || this.isSystem || nid == -1 || nid == i) {
                return true;
            }
            if (!supportsProfiles() || !ManagedServices.this.mUserProfiles.isCurrentProfile(nid) || !isPermittedForProfile(nid)) {
                return false;
            }
            return true;
        }

        public boolean supportsProfiles() {
            return this.targetSdkVersion >= 21;
        }

        public void binderDied() {
            if (ManagedServices.this.DEBUG) {
                Slog.d(ManagedServices.this.TAG, "binderDied");
            }
            ManagedServiceInfo unused = ManagedServices.this.removeServiceImpl(this.service, this.userid);
        }

        public boolean isEnabledForCurrentProfiles() {
            if (this.isSystem) {
                return true;
            }
            if (this.connection == null) {
                return false;
            }
            return ((ArraySet) ManagedServices.this.mEnabledServicesForCurrentProfiles.get(Integer.valueOf(this.userid))).contains(this.component);
        }

        public boolean isPermittedForProfile(int userId) {
            if (!ManagedServices.this.mUserProfiles.isManagedProfile(userId)) {
                return true;
            }
            DevicePolicyManager dpm = (DevicePolicyManager) ManagedServices.this.mContext.getSystemService("device_policy");
            long identity = Binder.clearCallingIdentity();
            try {
                return dpm.isNotificationListenerServicePermitted(this.component.getPackageName(), userId);
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ManagedServiceInfo that = (ManagedServiceInfo) o;
            if (this.userid != that.userid || this.isSystem != that.isSystem || this.targetSdkVersion != that.targetSdkVersion || !Objects.equals(this.service, that.service) || !Objects.equals(this.component, that.component) || !Objects.equals(this.connection, that.connection)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.service, this.component, Integer.valueOf(this.userid), Boolean.valueOf(this.isSystem), this.connection, Integer.valueOf(this.targetSdkVersion)});
        }
    }

    public boolean isComponentEnabledForCurrentProfiles(ComponentName component) {
        for (Integer item : this.mEnabledServicesForCurrentProfiles.keySet()) {
            if (this.mEnabledServicesForCurrentProfiles.get(item).contains(component)) {
                return true;
            }
        }
        return false;
    }

    public static class UserProfiles {
        private final SparseArray<UserInfo> mCurrentProfiles = new SparseArray<>();

        public void updateCache(Context context) {
            UserManager userManager = (UserManager) context.getSystemService(ManagedServices.ATT_USER_ID);
            if (userManager != null) {
                List<UserInfo> profiles = userManager.getProfiles(ActivityManager.getCurrentUser());
                synchronized (this.mCurrentProfiles) {
                    this.mCurrentProfiles.clear();
                    for (UserInfo user : profiles) {
                        this.mCurrentProfiles.put(user.id, user);
                    }
                }
            }
        }

        public IntArray getCurrentProfileIds() {
            IntArray users;
            synchronized (this.mCurrentProfiles) {
                users = new IntArray(this.mCurrentProfiles.size());
                int N = this.mCurrentProfiles.size();
                for (int i = 0; i < N; i++) {
                    users.add(this.mCurrentProfiles.keyAt(i));
                }
            }
            return users;
        }

        public boolean isCurrentProfile(int userId) {
            boolean z;
            synchronized (this.mCurrentProfiles) {
                z = this.mCurrentProfiles.get(userId) != null;
            }
            return z;
        }

        public boolean isManagedProfile(int userId) {
            boolean z;
            synchronized (this.mCurrentProfiles) {
                UserInfo user = this.mCurrentProfiles.get(userId);
                z = user != null && user.isManagedProfile();
            }
            return z;
        }
    }
}
