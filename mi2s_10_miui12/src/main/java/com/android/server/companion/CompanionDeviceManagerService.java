package com.android.server.companion;

import android.app.PendingIntent;
import android.companion.AssociationRequest;
import android.companion.ICompanionDeviceDiscoveryService;
import android.companion.ICompanionDeviceDiscoveryServiceCallback;
import android.companion.ICompanionDeviceManager;
import android.companion.IFindDeviceCallback;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.NetworkPolicyManager;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.IDeviceIdleController;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ShellCommand;
import android.os.UserHandle;
import android.provider.Settings;
import android.provider.SettingsStringUtil;
import android.text.BidiFormatter;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.ExceptionUtils;
import android.util.Log;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.app.IAppOpsService;
import com.android.internal.content.PackageMonitor;
import com.android.internal.notification.NotificationAccessConfirmationActivityContract;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.CollectionUtils;
import com.android.internal.util.FunctionalUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.companion.CompanionDeviceManagerService;
import com.android.server.wm.ActivityTaskManagerInternal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class CompanionDeviceManagerService extends SystemService implements IBinder.DeathRecipient {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "CompanionDeviceManagerService";
    /* access modifiers changed from: private */
    public static final ComponentName SERVICE_TO_BIND_TO = ComponentName.createRelative("com.android.companiondevicemanager", ".DeviceDiscoveryService");
    private static final String XML_ATTR_DEVICE = "device";
    private static final String XML_ATTR_PACKAGE = "package";
    private static final String XML_FILE_NAME = "companion_device_manager_associations.xml";
    private static final String XML_TAG_ASSOCIATION = "association";
    private static final String XML_TAG_ASSOCIATIONS = "associations";
    /* access modifiers changed from: private */
    public IAppOpsService mAppOpsManager = IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
    /* access modifiers changed from: private */
    public String mCallingPackage;
    /* access modifiers changed from: private */
    public IFindDeviceCallback mFindDeviceCallback;
    private IDeviceIdleController mIdleController = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));
    private final CompanionDeviceManagerImpl mImpl = new CompanionDeviceManagerImpl();
    private final Object mLock = new Object();
    /* access modifiers changed from: private */
    public AssociationRequest mRequest;
    private ServiceConnection mServiceConnection;
    private final ConcurrentMap<Integer, AtomicFile> mUidToStorage = new ConcurrentHashMap();

    public CompanionDeviceManagerService(Context context) {
        super(context);
        registerPackageMonitor();
    }

    private void registerPackageMonitor() {
        new PackageMonitor() {
            public void onPackageRemoved(String packageName, int uid) {
                CompanionDeviceManagerService.this.updateAssociations(new Function(packageName) {
                    private final /* synthetic */ String f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final Object apply(Object obj) {
                        return CollectionUtils.filter((Set) obj, new Predicate(this.f$0) {
                            private final /* synthetic */ String f$0;

                            {
                                this.f$0 = r1;
                            }

                            public final boolean test(Object obj) {
                                return CompanionDeviceManagerService.AnonymousClass1.lambda$onPackageRemoved$0(this.f$0, (CompanionDeviceManagerService.Association) obj);
                            }
                        });
                    }
                }, getChangingUserId());
            }

            static /* synthetic */ boolean lambda$onPackageRemoved$0(String packageName, Association a) {
                return !Objects.equals(a.companionAppPackage, packageName);
            }

            public void onPackageModified(String packageName) {
                int userId = getChangingUserId();
                if (!ArrayUtils.isEmpty(CompanionDeviceManagerService.this.readAllAssociations(userId, packageName))) {
                    CompanionDeviceManagerService.this.updateSpecialAccessPermissionForAssociatedPackage(packageName, userId);
                }
            }
        }.register(getContext(), FgThread.get().getLooper(), UserHandle.ALL, true);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.companion.CompanionDeviceManagerService$CompanionDeviceManagerImpl, android.os.IBinder] */
    public void onStart() {
        publishBinderService("companiondevice", this.mImpl);
    }

    public void onUnlockUser(int userHandle) {
        Set<Association> associations = readAllAssociations(userHandle);
        if (associations != null && !associations.isEmpty()) {
            Set<String> companionAppPackages = new HashSet<>();
            for (Association association : associations) {
                companionAppPackages.add(association.companionAppPackage);
            }
            ActivityTaskManagerInternal atmInternal = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
            if (atmInternal != null) {
                atmInternal.setCompanionAppPackages(userHandle, companionAppPackages);
            }
        }
    }

    public void binderDied() {
        Handler.getMain().post(new Runnable() {
            public final void run() {
                CompanionDeviceManagerService.this.cleanup();
            }
        });
    }

    /* access modifiers changed from: private */
    public void cleanup() {
        synchronized (this.mLock) {
            this.mServiceConnection = unbind(this.mServiceConnection);
            this.mFindDeviceCallback = unlinkToDeath(this.mFindDeviceCallback, this, 0);
            this.mRequest = null;
            this.mCallingPackage = null;
        }
    }

    private static <T extends IInterface> T unlinkToDeath(T iinterface, IBinder.DeathRecipient deathRecipient, int flags) {
        if (iinterface == null) {
            return null;
        }
        iinterface.asBinder().unlinkToDeath(deathRecipient, flags);
        return null;
    }

    private ServiceConnection unbind(ServiceConnection conn) {
        if (conn == null) {
            return null;
        }
        getContext().unbindService(conn);
        return null;
    }

    class CompanionDeviceManagerImpl extends ICompanionDeviceManager.Stub {
        CompanionDeviceManagerImpl() {
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            try {
                return CompanionDeviceManagerService.super.onTransact(code, data, reply, flags);
            } catch (Throwable e) {
                Slog.e(CompanionDeviceManagerService.LOG_TAG, "Error during IPC", e);
                throw ExceptionUtils.propagate(e, RemoteException.class);
            }
        }

        public void associate(AssociationRequest request, IFindDeviceCallback callback, String callingPackage) throws RemoteException {
            Preconditions.checkNotNull(request, "Request cannot be null");
            Preconditions.checkNotNull(callback, "Callback cannot be null");
            checkCallerIsSystemOr(callingPackage);
            int userId = CompanionDeviceManagerService.getCallingUserId();
            checkUsesFeature(callingPackage, userId);
            long callingIdentity = Binder.clearCallingIdentity();
            try {
                CompanionDeviceManagerService.this.getContext().bindServiceAsUser(new Intent().setComponent(CompanionDeviceManagerService.SERVICE_TO_BIND_TO), CompanionDeviceManagerService.this.createServiceConnection(request, callback, callingPackage), 1, UserHandle.of(userId));
            } finally {
                Binder.restoreCallingIdentity(callingIdentity);
            }
        }

        public void stopScan(AssociationRequest request, IFindDeviceCallback callback, String callingPackage) {
            if (Objects.equals(request, CompanionDeviceManagerService.this.mRequest) && Objects.equals(callback, CompanionDeviceManagerService.this.mFindDeviceCallback) && Objects.equals(callingPackage, CompanionDeviceManagerService.this.mCallingPackage)) {
                CompanionDeviceManagerService.this.cleanup();
            }
        }

        public List<String> getAssociations(String callingPackage, int userId) throws RemoteException {
            checkCallerIsSystemOr(callingPackage, userId);
            checkUsesFeature(callingPackage, CompanionDeviceManagerService.getCallingUserId());
            return new ArrayList(CollectionUtils.map(CompanionDeviceManagerService.this.readAllAssociations(userId, callingPackage), $$Lambda$CompanionDeviceManagerService$CompanionDeviceManagerImpl$bdv3Vfadbb8b9nrSgkARO4oYOXU.INSTANCE));
        }

        public void disassociate(String deviceMacAddress, String callingPackage) throws RemoteException {
            Preconditions.checkNotNull(deviceMacAddress);
            checkCallerIsSystemOr(callingPackage);
            checkUsesFeature(callingPackage, CompanionDeviceManagerService.getCallingUserId());
            CompanionDeviceManagerService.this.removeAssociation(CompanionDeviceManagerService.getCallingUserId(), callingPackage, deviceMacAddress);
        }

        private void checkCallerIsSystemOr(String pkg) throws RemoteException {
            checkCallerIsSystemOr(pkg, CompanionDeviceManagerService.getCallingUserId());
        }

        private void checkCallerIsSystemOr(String pkg, int userId) throws RemoteException {
            if (!CompanionDeviceManagerService.isCallerSystem()) {
                Preconditions.checkArgument(CompanionDeviceManagerService.getCallingUserId() == userId, "Must be called by either same user or system");
                CompanionDeviceManagerService.this.mAppOpsManager.checkPackage(Binder.getCallingUid(), pkg);
            }
        }

        public PendingIntent requestNotificationAccess(ComponentName component) throws RemoteException {
            String callingPackage = component.getPackageName();
            checkCanCallNotificationApi(callingPackage);
            int userId = CompanionDeviceManagerService.getCallingUserId();
            String packageTitle = BidiFormatter.getInstance().unicodeWrap(CompanionDeviceManagerService.this.getPackageInfo(callingPackage, userId).applicationInfo.loadSafeLabel(CompanionDeviceManagerService.this.getContext().getPackageManager(), 500.0f, 5).toString());
            long identity = Binder.clearCallingIdentity();
            try {
                return PendingIntent.getActivity(CompanionDeviceManagerService.this.getContext(), 0, NotificationAccessConfirmationActivityContract.launcherIntent(userId, component, packageTitle), 1409286144);
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        public boolean hasNotificationAccess(ComponentName component) throws RemoteException {
            checkCanCallNotificationApi(component.getPackageName());
            return new SettingsStringUtil.ComponentNameSet(Settings.Secure.getString(CompanionDeviceManagerService.this.getContext().getContentResolver(), "enabled_notification_listeners")).contains(component);
        }

        private void checkCanCallNotificationApi(String callingPackage) throws RemoteException {
            checkCallerIsSystemOr(callingPackage);
            int userId = CompanionDeviceManagerService.getCallingUserId();
            Preconditions.checkState(!ArrayUtils.isEmpty(CompanionDeviceManagerService.this.readAllAssociations(userId, callingPackage)), "App must have an association before calling this API");
            checkUsesFeature(callingPackage, userId);
        }

        private void checkUsesFeature(String pkg, int userId) {
            if (!CompanionDeviceManagerService.isCallerSystem()) {
                FeatureInfo[] reqFeatures = CompanionDeviceManagerService.this.getPackageInfo(pkg, userId).reqFeatures;
                int numFeatures = ArrayUtils.size(reqFeatures);
                int i = 0;
                while (i < numFeatures) {
                    if (!"android.software.companion_device_setup".equals(reqFeatures[i].name)) {
                        i++;
                    } else {
                        return;
                    }
                }
                throw new IllegalStateException("Must declare uses-feature " + "android.software.companion_device_setup" + " in manifest to use this API");
            }
        }

        /* JADX WARNING: type inference failed for: r1v1, types: [android.os.Binder] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) throws android.os.RemoteException {
            /*
                r8 = this;
                com.android.server.companion.CompanionDeviceManagerService$ShellCmd r0 = new com.android.server.companion.CompanionDeviceManagerService$ShellCmd
                com.android.server.companion.CompanionDeviceManagerService r1 = com.android.server.companion.CompanionDeviceManagerService.this
                r2 = 0
                r0.<init>()
                r1 = r8
                r2 = r9
                r3 = r10
                r4 = r11
                r5 = r12
                r6 = r13
                r7 = r14
                r0.exec(r1, r2, r3, r4, r5, r6, r7)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.companion.CompanionDeviceManagerService.CompanionDeviceManagerImpl.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
        }
    }

    /* access modifiers changed from: private */
    public static int getCallingUserId() {
        return UserHandle.getUserId(Binder.getCallingUid());
    }

    /* access modifiers changed from: private */
    public static boolean isCallerSystem() {
        return Binder.getCallingUid() == 1000;
    }

    /* access modifiers changed from: private */
    public ServiceConnection createServiceConnection(final AssociationRequest request, final IFindDeviceCallback findDeviceCallback, final String callingPackage) {
        this.mServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder service) {
                IFindDeviceCallback unused = CompanionDeviceManagerService.this.mFindDeviceCallback = findDeviceCallback;
                AssociationRequest unused2 = CompanionDeviceManagerService.this.mRequest = request;
                String unused3 = CompanionDeviceManagerService.this.mCallingPackage = callingPackage;
                try {
                    CompanionDeviceManagerService.this.mFindDeviceCallback.asBinder().linkToDeath(CompanionDeviceManagerService.this, 0);
                    try {
                        ICompanionDeviceDiscoveryService.Stub.asInterface(service).startDiscovery(request, callingPackage, findDeviceCallback, CompanionDeviceManagerService.this.getServiceCallback());
                    } catch (RemoteException e) {
                        Log.e(CompanionDeviceManagerService.LOG_TAG, "Error while initiating device discovery", e);
                    }
                } catch (RemoteException e2) {
                    CompanionDeviceManagerService.this.cleanup();
                }
            }

            public void onServiceDisconnected(ComponentName name) {
            }
        };
        return this.mServiceConnection;
    }

    /* access modifiers changed from: private */
    public ICompanionDeviceDiscoveryServiceCallback.Stub getServiceCallback() {
        return new ICompanionDeviceDiscoveryServiceCallback.Stub() {
            public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
                try {
                    return CompanionDeviceManagerService.super.onTransact(code, data, reply, flags);
                } catch (Throwable e) {
                    Slog.e(CompanionDeviceManagerService.LOG_TAG, "Error during IPC", e);
                    throw ExceptionUtils.propagate(e, RemoteException.class);
                }
            }

            public void onDeviceSelected(String packageName, int userId, String deviceAddress) {
                CompanionDeviceManagerService.this.addAssociation(userId, packageName, deviceAddress);
                CompanionDeviceManagerService.this.cleanup();
            }

            public void onDeviceSelectionCancel() {
                CompanionDeviceManagerService.this.cleanup();
            }
        };
    }

    /* access modifiers changed from: package-private */
    public void addAssociation(int userId, String packageName, String deviceAddress) {
        updateSpecialAccessPermissionForAssociatedPackage(packageName, userId);
        recordAssociation(packageName, deviceAddress);
    }

    public /* synthetic */ Set lambda$removeAssociation$0$CompanionDeviceManagerService(int userId, String deviceMacAddress, String pkg, Set associations) {
        return CollectionUtils.remove(associations, new Association(userId, deviceMacAddress, pkg));
    }

    /* access modifiers changed from: package-private */
    public void removeAssociation(int userId, String pkg, String deviceMacAddress) {
        updateAssociations(new Function(userId, deviceMacAddress, pkg) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ String f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final Object apply(Object obj) {
                return CompanionDeviceManagerService.this.lambda$removeAssociation$0$CompanionDeviceManagerService(this.f$1, this.f$2, this.f$3, (Set) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    public void updateSpecialAccessPermissionForAssociatedPackage(String packageName, int userId) {
        PackageInfo packageInfo = getPackageInfo(packageName, userId);
        if (packageInfo != null) {
            Binder.withCleanCallingIdentity(PooledLambda.obtainRunnable($$Lambda$CompanionDeviceManagerService$wnUkAY8uXyjMGM59bNpzLLMJ1I.INSTANCE, this, packageInfo).recycleOnUse());
        }
    }

    /* access modifiers changed from: private */
    public void updateSpecialAccessPermissionAsSystem(PackageInfo packageInfo) {
        try {
            if (containsEither(packageInfo.requestedPermissions, "android.permission.RUN_IN_BACKGROUND", "android.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND")) {
                this.mIdleController.addPowerSaveWhitelistApp(packageInfo.packageName);
            } else {
                this.mIdleController.removePowerSaveWhitelistApp(packageInfo.packageName);
            }
        } catch (RemoteException e) {
        }
        NetworkPolicyManager networkPolicyManager = NetworkPolicyManager.from(getContext());
        if (containsEither(packageInfo.requestedPermissions, "android.permission.USE_DATA_IN_BACKGROUND", "android.permission.REQUEST_COMPANION_USE_DATA_IN_BACKGROUND")) {
            networkPolicyManager.addUidPolicy(packageInfo.applicationInfo.uid, 4);
        } else {
            networkPolicyManager.removeUidPolicy(packageInfo.applicationInfo.uid, 4);
        }
    }

    private static <T> boolean containsEither(T[] array, T a, T b) {
        return ArrayUtils.contains(array, a) || ArrayUtils.contains(array, b);
    }

    /* access modifiers changed from: private */
    public PackageInfo getPackageInfo(String packageName, int userId) {
        return (PackageInfo) Binder.withCleanCallingIdentity(PooledLambda.obtainSupplier($$Lambda$CompanionDeviceManagerService$0VKz9ecFqvfFXzRrfazPf5wW2s.INSTANCE, getContext(), packageName, Integer.valueOf(userId)).recycleOnUse());
    }

    static /* synthetic */ PackageInfo lambda$getPackageInfo$1(Context context, String pkg, Integer id) {
        try {
            return context.getPackageManager().getPackageInfoAsUser(pkg, 20480, id.intValue());
        } catch (PackageManager.NameNotFoundException e) {
            Slog.e(LOG_TAG, "Failed to get PackageInfo for package " + pkg, e);
            return null;
        }
    }

    private void recordAssociation(String priviledgedPackage, String deviceAddress) {
        updateAssociations(new Function(getCallingUserId(), deviceAddress, priviledgedPackage) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ String f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final Object apply(Object obj) {
                return CompanionDeviceManagerService.this.lambda$recordAssociation$2$CompanionDeviceManagerService(this.f$1, this.f$2, this.f$3, (Set) obj);
            }
        });
    }

    public /* synthetic */ Set lambda$recordAssociation$2$CompanionDeviceManagerService(int userId, String deviceAddress, String priviledgedPackage, Set associations) {
        return CollectionUtils.add(associations, new Association(userId, deviceAddress, priviledgedPackage));
    }

    private void updateAssociations(Function<Set<Association>, Set<Association>> update) {
        updateAssociations(update, getCallingUserId());
    }

    /* access modifiers changed from: private */
    public void updateAssociations(Function<Set<Association>, Set<Association>> update, int userId) {
        AtomicFile file = getStorageFileForUser(userId);
        synchronized (file) {
            Set<Association> associations = readAllAssociations(userId);
            Set<Association> old = CollectionUtils.copyOf(associations);
            Set<Association> associations2 = update.apply(associations);
            if (CollectionUtils.size(old) != CollectionUtils.size(associations2)) {
                Set<Association> finalAssociations = associations2;
                Set<String> companionAppPackages = new HashSet<>();
                for (Association association : finalAssociations) {
                    companionAppPackages.add(association.companionAppPackage);
                }
                file.write(new Consumer(finalAssociations) {
                    private final /* synthetic */ Set f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void accept(Object obj) {
                        CompanionDeviceManagerService.lambda$updateAssociations$4(this.f$0, (FileOutputStream) obj);
                    }
                });
                ((ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class)).setCompanionAppPackages(userId, companionAppPackages);
            }
        }
    }

    static /* synthetic */ void lambda$updateAssociations$4(Set finalAssociations, FileOutputStream out) {
        XmlSerializer xml = Xml.newSerializer();
        try {
            xml.setOutput(out, StandardCharsets.UTF_8.name());
            xml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            xml.startDocument((String) null, true);
            xml.startTag((String) null, XML_TAG_ASSOCIATIONS);
            CollectionUtils.forEach(finalAssociations, new FunctionalUtils.ThrowingConsumer(xml) {
                private final /* synthetic */ XmlSerializer f$0;

                {
                    this.f$0 = r1;
                }

                public final void acceptOrThrow(Object obj) {
                    this.f$0.startTag((String) null, CompanionDeviceManagerService.XML_TAG_ASSOCIATION).attribute((String) null, "package", ((CompanionDeviceManagerService.Association) obj).companionAppPackage).attribute((String) null, "device", ((CompanionDeviceManagerService.Association) obj).deviceAddress).endTag((String) null, CompanionDeviceManagerService.XML_TAG_ASSOCIATION);
                }
            });
            xml.endTag((String) null, XML_TAG_ASSOCIATIONS);
            xml.endDocument();
        } catch (Exception e) {
            Slog.e(LOG_TAG, "Error while writing associations file", e);
            throw ExceptionUtils.propagate(e);
        }
    }

    private AtomicFile getStorageFileForUser(int uid) {
        return this.mUidToStorage.computeIfAbsent(Integer.valueOf(uid), $$Lambda$CompanionDeviceManagerService$bh5xRJq9CRJoXvmerYRNjK1xEQ.INSTANCE);
    }

    static /* synthetic */ AtomicFile lambda$getStorageFileForUser$5(Integer u) {
        return new AtomicFile(new File(Environment.getUserSystemDirectory(u.intValue()), XML_FILE_NAME));
    }

    /* access modifiers changed from: private */
    public Set<Association> readAllAssociations(int userId) {
        return readAllAssociations(userId, (String) null);
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    /* access modifiers changed from: private */
    public Set<Association> readAllAssociations(int userId, String packageFilter) {
        Throwable th;
        String str = packageFilter;
        AtomicFile file = getStorageFileForUser(userId);
        if (!file.getBaseFile().exists()) {
            return null;
        }
        ArraySet<Association> result = null;
        XmlPullParser parser = Xml.newPullParser();
        synchronized (file) {
            try {
                FileInputStream in = file.openRead();
                try {
                    parser.setInput(in, StandardCharsets.UTF_8.name());
                    while (true) {
                        int next = parser.next();
                        int type = next;
                        if (next == 1) {
                            break;
                        } else if (type == 2 || XML_TAG_ASSOCIATIONS.equals(parser.getName())) {
                            String appPackage = parser.getAttributeValue((String) null, "package");
                            String deviceAddress = parser.getAttributeValue((String) null, "device");
                            if (appPackage != null) {
                                if (deviceAddress != null) {
                                    if (str == null || str.equals(appPackage)) {
                                        result = ArrayUtils.add(result, new Association(userId, deviceAddress, appPackage));
                                    }
                                }
                            }
                        }
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (Throwable th2) {
                    Throwable th3 = th2;
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException | XmlPullParserException e) {
                            e = e;
                            Slog.e(LOG_TAG, "Error while reading associations file", e);
                            return null;
                        } catch (Throwable th4) {
                            e = th4;
                            throw e;
                        }
                    }
                    throw th3;
                }
                try {
                    return result;
                } catch (Throwable th5) {
                    e = th5;
                    throw e;
                }
            } catch (IOException | XmlPullParserException e2) {
                e = e2;
                Slog.e(LOG_TAG, "Error while reading associations file", e);
                return null;
            }
        }
    }

    private class Association {
        public final String companionAppPackage;
        public final String deviceAddress;
        public final int uid;

        private Association(int uid2, String deviceAddress2, String companionAppPackage2) {
            this.uid = uid2;
            this.deviceAddress = (String) Preconditions.checkNotNull(deviceAddress2);
            this.companionAppPackage = (String) Preconditions.checkNotNull(companionAppPackage2);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Association that = (Association) o;
            if (this.uid == that.uid && this.deviceAddress.equals(that.deviceAddress)) {
                return this.companionAppPackage.equals(that.companionAppPackage);
            }
            return false;
        }

        public int hashCode() {
            return (((this.uid * 31) + this.deviceAddress.hashCode()) * 31) + this.companionAppPackage.hashCode();
        }
    }

    private class ShellCmd extends ShellCommand {
        public static final String USAGE = "help\nlist USER_ID\nassociate USER_ID PACKAGE MAC_ADDRESS\ndisassociate USER_ID PACKAGE MAC_ADDRESS";

        private ShellCmd() {
        }

        /* JADX WARNING: Removed duplicated region for block: B:17:0x0039  */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x0066  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int onCommand(java.lang.String r6) {
            /*
                r5 = this;
                int r0 = r6.hashCode()
                r1 = 3322014(0x32b09e, float:4.655133E-39)
                r2 = 0
                r3 = 2
                r4 = 1
                if (r0 == r1) goto L_0x002b
                r1 = 784321104(0x2ebfca50, float:8.721612E-11)
                if (r0 == r1) goto L_0x0021
                r1 = 1586499358(0x5e900f1e, float:5.1902743E18)
                if (r0 == r1) goto L_0x0017
            L_0x0016:
                goto L_0x0036
            L_0x0017:
                java.lang.String r0 = "associate"
                boolean r0 = r6.equals(r0)
                if (r0 == 0) goto L_0x0016
                r0 = r4
                goto L_0x0037
            L_0x0021:
                java.lang.String r0 = "disassociate"
                boolean r0 = r6.equals(r0)
                if (r0 == 0) goto L_0x0016
                r0 = r3
                goto L_0x0037
            L_0x002b:
                java.lang.String r0 = "list"
                boolean r0 = r6.equals(r0)
                if (r0 == 0) goto L_0x0016
                r0 = r2
                goto L_0x0037
            L_0x0036:
                r0 = -1
            L_0x0037:
                if (r0 == 0) goto L_0x0066
                if (r0 == r4) goto L_0x0054
                if (r0 == r3) goto L_0x0042
                int r0 = r5.handleDefaultCommands(r6)
                return r0
            L_0x0042:
                com.android.server.companion.CompanionDeviceManagerService r0 = com.android.server.companion.CompanionDeviceManagerService.this
                int r1 = r5.getNextArgInt()
                java.lang.String r3 = r5.getNextArgRequired()
                java.lang.String r4 = r5.getNextArgRequired()
                r0.removeAssociation(r1, r3, r4)
                goto L_0x0079
            L_0x0054:
                com.android.server.companion.CompanionDeviceManagerService r0 = com.android.server.companion.CompanionDeviceManagerService.this
                int r1 = r5.getNextArgInt()
                java.lang.String r3 = r5.getNextArgRequired()
                java.lang.String r4 = r5.getNextArgRequired()
                r0.addAssociation(r1, r3, r4)
                goto L_0x0079
            L_0x0066:
                com.android.server.companion.CompanionDeviceManagerService r0 = com.android.server.companion.CompanionDeviceManagerService.this
                int r1 = r5.getNextArgInt()
                java.util.Set r0 = r0.readAllAssociations(r1)
                com.android.server.companion.-$$Lambda$CompanionDeviceManagerService$ShellCmd$spuk4wZBlDmxSJgcFgRkfptYY8g r1 = new com.android.server.companion.-$$Lambda$CompanionDeviceManagerService$ShellCmd$spuk4wZBlDmxSJgcFgRkfptYY8g
                r1.<init>()
                com.android.internal.util.CollectionUtils.forEach(r0, r1)
            L_0x0079:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.companion.CompanionDeviceManagerService.ShellCmd.onCommand(java.lang.String):int");
        }

        public /* synthetic */ void lambda$onCommand$0$CompanionDeviceManagerService$ShellCmd(Association a) throws Exception {
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.println(a.companionAppPackage + " " + a.deviceAddress);
        }

        private int getNextArgInt() {
            return Integer.parseInt(getNextArgRequired());
        }

        public void onHelp() {
            getOutPrintWriter().println(USAGE);
        }
    }
}
