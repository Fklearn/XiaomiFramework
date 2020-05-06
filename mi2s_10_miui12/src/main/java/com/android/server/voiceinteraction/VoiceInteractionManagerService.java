package com.android.server.voiceinteraction;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.AppGlobals;
import android.app.role.OnRoleHoldersChangedListener;
import android.app.role.RoleManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.ShortcutServiceInternal;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.soundtrigger.IRecognitionStatusCallback;
import android.hardware.soundtrigger.SoundTrigger;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallback;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.service.voice.IVoiceInteractionService;
import android.service.voice.IVoiceInteractionSession;
import android.service.voice.VoiceInteractionManagerInternal;
import android.service.voice.VoiceInteractionServiceInfo;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import com.android.internal.app.IVoiceActionCheckCallback;
import com.android.internal.app.IVoiceInteractionManagerService;
import com.android.internal.app.IVoiceInteractionSessionListener;
import com.android.internal.app.IVoiceInteractionSessionShowCallback;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.Preconditions;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.UiThread;
import com.android.server.soundtrigger.SoundTriggerInternal;
import com.android.server.voiceinteraction.VoiceInteractionManagerService;
import com.android.server.wm.ActivityTaskManagerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

public class VoiceInteractionManagerService extends SystemService {
    static final boolean DEBUG = false;
    static final String TAG = "VoiceInteractionManagerService";
    final ActivityManagerInternal mAmInternal;
    final ActivityTaskManagerInternal mAtmInternal;
    final Context mContext;
    final DatabaseHelper mDbHelper;
    final ArraySet<Integer> mLoadedKeyphraseIds = new ArraySet<>();
    final ContentResolver mResolver;
    /* access modifiers changed from: private */
    public final VoiceInteractionManagerServiceStub mServiceStub;
    ShortcutServiceInternal mShortcutServiceInternal;
    SoundTriggerInternal mSoundTriggerInternal;
    final UserManager mUserManager;
    /* access modifiers changed from: private */
    public final RemoteCallbackList<IVoiceInteractionSessionListener> mVoiceInteractionSessionListeners = new RemoteCallbackList<>();

    public VoiceInteractionManagerService(Context context) {
        super(context);
        this.mContext = context;
        this.mResolver = context.getContentResolver();
        this.mDbHelper = new DatabaseHelper(context);
        this.mServiceStub = new VoiceInteractionManagerServiceStub();
        this.mAmInternal = (ActivityManagerInternal) Preconditions.checkNotNull((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class));
        this.mAtmInternal = (ActivityTaskManagerInternal) Preconditions.checkNotNull((ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class));
        this.mUserManager = (UserManager) Preconditions.checkNotNull((UserManager) context.getSystemService(UserManager.class));
        ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).setVoiceInteractionPackagesProvider(new PackageManagerInternal.PackagesProvider() {
            public String[] getPackages(int userId) {
                VoiceInteractionManagerService.this.mServiceStub.initForUser(userId);
                ComponentName interactor = VoiceInteractionManagerService.this.mServiceStub.getCurInteractor(userId);
                if (interactor == null) {
                    return null;
                }
                return new String[]{interactor.getPackageName()};
            }
        });
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub, android.os.IBinder] */
    public void onStart() {
        publishBinderService("voiceinteraction", this.mServiceStub);
        publishLocalService(VoiceInteractionManagerInternal.class, new LocalService());
    }

    public void onBootPhase(int phase) {
        if (500 == phase) {
            this.mShortcutServiceInternal = (ShortcutServiceInternal) Preconditions.checkNotNull((ShortcutServiceInternal) LocalServices.getService(ShortcutServiceInternal.class));
            this.mSoundTriggerInternal = (SoundTriggerInternal) LocalServices.getService(SoundTriggerInternal.class);
        } else if (phase == 600) {
            this.mServiceStub.systemRunning(isSafeMode());
        }
    }

    public void onStartUser(int userHandle) {
        this.mServiceStub.initForUser(userHandle);
    }

    public void onUnlockUser(int userHandle) {
        this.mServiceStub.initForUser(userHandle);
        this.mServiceStub.switchImplementationIfNeeded(false);
    }

    public void onSwitchUser(int userHandle) {
        this.mServiceStub.switchUser(userHandle);
    }

    class LocalService extends VoiceInteractionManagerInternal {
        LocalService() {
        }

        public void startLocalVoiceInteraction(IBinder callingActivity, Bundle options) {
            VoiceInteractionManagerService.this.mServiceStub.startLocalVoiceInteraction(callingActivity, options);
        }

        public boolean supportsLocalVoiceInteraction() {
            return VoiceInteractionManagerService.this.mServiceStub.supportsLocalVoiceInteraction();
        }

        public void stopLocalVoiceInteraction(IBinder callingActivity) {
            VoiceInteractionManagerService.this.mServiceStub.stopLocalVoiceInteraction(callingActivity);
        }
    }

    class VoiceInteractionManagerServiceStub extends IVoiceInteractionManagerService.Stub {
        /* access modifiers changed from: private */
        public int mCurUser;
        private boolean mCurUserUnlocked;
        private final boolean mEnableService;
        VoiceInteractionManagerServiceImpl mImpl;
        PackageMonitor mPackageMonitor = new PackageMonitor() {
            public boolean onHandleForceStop(Intent intent, String[] packages, int uid, boolean doit) {
                boolean hitRec;
                boolean hitRec2;
                String[] strArr = packages;
                int userHandle = UserHandle.getUserId(uid);
                ComponentName curInteractor = VoiceInteractionManagerServiceStub.this.getCurInteractor(userHandle);
                ComponentName curRecognizer = VoiceInteractionManagerServiceStub.this.getCurRecognizer(userHandle);
                int length = strArr.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        hitRec = false;
                        hitRec2 = false;
                        break;
                    }
                    String pkg = strArr[i];
                    if (curInteractor == null || !pkg.equals(curInteractor.getPackageName())) {
                        if (curRecognizer != null && pkg.equals(curRecognizer.getPackageName())) {
                            hitRec = true;
                            hitRec2 = false;
                            break;
                        }
                        i++;
                    } else {
                        hitRec = false;
                        hitRec2 = true;
                        break;
                    }
                }
                if (hitRec2 && doit) {
                    synchronized (VoiceInteractionManagerServiceStub.this) {
                        Slog.i(VoiceInteractionManagerService.TAG, "Force stopping current voice interactor: " + VoiceInteractionManagerServiceStub.this.getCurInteractor(userHandle));
                        VoiceInteractionManagerServiceStub.this.unloadAllKeyphraseModels();
                        if (VoiceInteractionManagerServiceStub.this.mImpl != null) {
                            VoiceInteractionManagerServiceStub.this.mImpl.shutdownLocked();
                            VoiceInteractionManagerServiceStub.this.setImplLocked((VoiceInteractionManagerServiceImpl) null);
                        }
                        VoiceInteractionManagerServiceStub.this.setCurInteractor((ComponentName) null, userHandle);
                        VoiceInteractionManagerServiceStub.this.setCurRecognizer((ComponentName) null, userHandle);
                        VoiceInteractionManagerServiceStub.this.resetCurAssistant(userHandle);
                        VoiceInteractionManagerServiceStub.this.initForUser(userHandle);
                        VoiceInteractionManagerServiceStub.this.switchImplementationIfNeededLocked(true);
                        Context context = VoiceInteractionManagerService.this.getContext();
                        ((RoleManager) context.getSystemService(RoleManager.class)).clearRoleHoldersAsUser("android.app.role.ASSISTANT", 0, UserHandle.of(userHandle), context.getMainExecutor(), $$Lambda$VoiceInteractionManagerService$VoiceInteractionManagerServiceStub$2$_YjGqp96fW1i83gthgQe_rVHY5s.INSTANCE);
                    }
                } else if (hitRec && doit) {
                    synchronized (VoiceInteractionManagerServiceStub.this) {
                        Slog.i(VoiceInteractionManagerService.TAG, "Force stopping current voice recognizer: " + VoiceInteractionManagerServiceStub.this.getCurRecognizer(userHandle));
                        VoiceInteractionManagerServiceStub.this.initSimpleRecognizer((VoiceInteractionServiceInfo) null, userHandle);
                    }
                }
                if (hitRec2 || hitRec) {
                    return true;
                }
                return false;
            }

            static /* synthetic */ void lambda$onHandleForceStop$0(Boolean successful) {
                if (!successful.booleanValue()) {
                    Slog.e(VoiceInteractionManagerService.TAG, "Failed to clear default assistant for force stop");
                }
            }

            public void onHandleUserStop(Intent intent, int userHandle) {
            }

            public void onPackageModified(String pkgName) {
                if (VoiceInteractionManagerServiceStub.this.mCurUser == getChangingUserId() && isPackageAppearing(pkgName) == 0) {
                    VoiceInteractionManagerServiceStub voiceInteractionManagerServiceStub = VoiceInteractionManagerServiceStub.this;
                    ComponentName curInteractor = voiceInteractionManagerServiceStub.getCurInteractor(voiceInteractionManagerServiceStub.mCurUser);
                    if (curInteractor == null) {
                        VoiceInteractionManagerServiceStub voiceInteractionManagerServiceStub2 = VoiceInteractionManagerServiceStub.this;
                        VoiceInteractionServiceInfo availInteractorInfo = voiceInteractionManagerServiceStub2.findAvailInteractor(voiceInteractionManagerServiceStub2.mCurUser, pkgName);
                        if (availInteractorInfo != null) {
                            ComponentName availInteractor = new ComponentName(availInteractorInfo.getServiceInfo().packageName, availInteractorInfo.getServiceInfo().name);
                            VoiceInteractionManagerServiceStub voiceInteractionManagerServiceStub3 = VoiceInteractionManagerServiceStub.this;
                            voiceInteractionManagerServiceStub3.setCurInteractor(availInteractor, voiceInteractionManagerServiceStub3.mCurUser);
                            VoiceInteractionManagerServiceStub voiceInteractionManagerServiceStub4 = VoiceInteractionManagerServiceStub.this;
                            if (voiceInteractionManagerServiceStub4.getCurRecognizer(voiceInteractionManagerServiceStub4.mCurUser) == null && availInteractorInfo.getRecognitionService() != null) {
                                VoiceInteractionManagerServiceStub.this.setCurRecognizer(new ComponentName(availInteractorInfo.getServiceInfo().packageName, availInteractorInfo.getRecognitionService()), VoiceInteractionManagerServiceStub.this.mCurUser);
                            }
                        }
                    } else if (didSomePackagesChange()) {
                        if (pkgName.equals(curInteractor.getPackageName())) {
                            VoiceInteractionManagerServiceStub.this.switchImplementationIfNeeded(true);
                        }
                    } else if (isComponentModified(curInteractor.getClassName())) {
                        VoiceInteractionManagerServiceStub.this.switchImplementationIfNeeded(true);
                    }
                }
            }

            /* JADX WARNING: Code restructure failed: missing block: B:11:0x0031, code lost:
                return;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:27:0x0080, code lost:
                return;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:44:0x00d7, code lost:
                return;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onSomePackagesChanged() {
                /*
                    r9 = this;
                    int r0 = r9.getChangingUserId()
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r1 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this
                    monitor-enter(r1)
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r2 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    android.content.ComponentName r2 = r2.getCurInteractor(r0)     // Catch:{ all -> 0x00d8 }
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r3 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    android.content.ComponentName r3 = r3.getCurRecognizer(r0)     // Catch:{ all -> 0x00d8 }
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r4 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    android.content.ComponentName r4 = r4.getCurAssistant(r0)     // Catch:{ all -> 0x00d8 }
                    r5 = 0
                    if (r3 != 0) goto L_0x0032
                    boolean r6 = r9.anyPackagesAppearing()     // Catch:{ all -> 0x00d8 }
                    if (r6 == 0) goto L_0x0030
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r6 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    android.content.ComponentName r5 = r6.findAvailRecognizer(r5, r0)     // Catch:{ all -> 0x00d8 }
                    r3 = r5
                    if (r3 == 0) goto L_0x0030
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r5 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    r5.setCurRecognizer(r3, r0)     // Catch:{ all -> 0x00d8 }
                L_0x0030:
                    monitor-exit(r1)     // Catch:{ all -> 0x00d8 }
                    return
                L_0x0032:
                    r6 = 3
                    if (r2 == 0) goto L_0x0081
                    java.lang.String r7 = r2.getPackageName()     // Catch:{ all -> 0x00d8 }
                    int r7 = r9.isPackageDisappearing(r7)     // Catch:{ all -> 0x00d8 }
                    if (r7 != r6) goto L_0x0055
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r6 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    r6.setCurInteractor(r5, r0)     // Catch:{ all -> 0x00d8 }
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r6 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    r6.setCurRecognizer(r5, r0)     // Catch:{ all -> 0x00d8 }
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r5 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    r5.resetCurAssistant(r0)     // Catch:{ all -> 0x00d8 }
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r5 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    r5.initForUser(r0)     // Catch:{ all -> 0x00d8 }
                    monitor-exit(r1)     // Catch:{ all -> 0x00d8 }
                    return
                L_0x0055:
                    java.lang.String r5 = r2.getPackageName()     // Catch:{ all -> 0x00d8 }
                    int r5 = r9.isPackageAppearing(r5)     // Catch:{ all -> 0x00d8 }
                    if (r5 == 0) goto L_0x007f
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r6 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    com.android.server.voiceinteraction.VoiceInteractionManagerServiceImpl r6 = r6.mImpl     // Catch:{ all -> 0x00d8 }
                    if (r6 == 0) goto L_0x007f
                    java.lang.String r6 = r2.getPackageName()     // Catch:{ all -> 0x00d8 }
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r7 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    com.android.server.voiceinteraction.VoiceInteractionManagerServiceImpl r7 = r7.mImpl     // Catch:{ all -> 0x00d8 }
                    android.content.ComponentName r7 = r7.mComponent     // Catch:{ all -> 0x00d8 }
                    java.lang.String r7 = r7.getPackageName()     // Catch:{ all -> 0x00d8 }
                    boolean r6 = r6.equals(r7)     // Catch:{ all -> 0x00d8 }
                    if (r6 == 0) goto L_0x007f
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r6 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    r7 = 1
                    r6.switchImplementationIfNeededLocked(r7)     // Catch:{ all -> 0x00d8 }
                L_0x007f:
                    monitor-exit(r1)     // Catch:{ all -> 0x00d8 }
                    return
                L_0x0081:
                    if (r4 == 0) goto L_0x00a3
                    java.lang.String r7 = r4.getPackageName()     // Catch:{ all -> 0x00d8 }
                    int r7 = r9.isPackageDisappearing(r7)     // Catch:{ all -> 0x00d8 }
                    if (r7 != r6) goto L_0x00a3
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r6 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    r6.setCurInteractor(r5, r0)     // Catch:{ all -> 0x00d8 }
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r6 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    r6.setCurRecognizer(r5, r0)     // Catch:{ all -> 0x00d8 }
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r5 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    r5.resetCurAssistant(r0)     // Catch:{ all -> 0x00d8 }
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r5 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    r5.initForUser(r0)     // Catch:{ all -> 0x00d8 }
                    monitor-exit(r1)     // Catch:{ all -> 0x00d8 }
                    return
                L_0x00a3:
                    java.lang.String r7 = r3.getPackageName()     // Catch:{ all -> 0x00d8 }
                    int r7 = r9.isPackageDisappearing(r7)     // Catch:{ all -> 0x00d8 }
                    if (r7 == r6) goto L_0x00cb
                    r6 = 2
                    if (r7 != r6) goto L_0x00b1
                    goto L_0x00cb
                L_0x00b1:
                    java.lang.String r5 = r3.getPackageName()     // Catch:{ all -> 0x00d8 }
                    boolean r5 = r9.isPackageModified(r5)     // Catch:{ all -> 0x00d8 }
                    if (r5 == 0) goto L_0x00d6
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r5 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r6 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    java.lang.String r8 = r3.getPackageName()     // Catch:{ all -> 0x00d8 }
                    android.content.ComponentName r6 = r6.findAvailRecognizer(r8, r0)     // Catch:{ all -> 0x00d8 }
                    r5.setCurRecognizer(r6, r0)     // Catch:{ all -> 0x00d8 }
                    goto L_0x00d6
                L_0x00cb:
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r6 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    com.android.server.voiceinteraction.VoiceInteractionManagerService$VoiceInteractionManagerServiceStub r8 = com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this     // Catch:{ all -> 0x00d8 }
                    android.content.ComponentName r5 = r8.findAvailRecognizer(r5, r0)     // Catch:{ all -> 0x00d8 }
                    r6.setCurRecognizer(r5, r0)     // Catch:{ all -> 0x00d8 }
                L_0x00d6:
                    monitor-exit(r1)     // Catch:{ all -> 0x00d8 }
                    return
                L_0x00d8:
                    r2 = move-exception
                    monitor-exit(r1)     // Catch:{ all -> 0x00d8 }
                    throw r2
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.AnonymousClass2.onSomePackagesChanged():void");
            }
        };
        private boolean mSafeMode;

        VoiceInteractionManagerServiceStub() {
            this.mEnableService = shouldEnableService(VoiceInteractionManagerService.this.mContext);
            new RoleObserver(VoiceInteractionManagerService.this.mContext.getMainExecutor());
        }

        /* access modifiers changed from: package-private */
        public void startLocalVoiceInteraction(final IBinder token, Bundle options) {
            if (this.mImpl != null) {
                long caller = Binder.clearCallingIdentity();
                try {
                    this.mImpl.showSessionLocked(options, 16, new IVoiceInteractionSessionShowCallback.Stub() {
                        public void onFailed() {
                        }

                        public void onShown() {
                            VoiceInteractionManagerService.this.mAtmInternal.onLocalVoiceInteractionStarted(token, VoiceInteractionManagerServiceStub.this.mImpl.mActiveSession.mSession, VoiceInteractionManagerServiceStub.this.mImpl.mActiveSession.mInteractor);
                        }
                    }, token);
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        public void stopLocalVoiceInteraction(IBinder callingActivity) {
            if (this.mImpl != null) {
                long caller = Binder.clearCallingIdentity();
                try {
                    this.mImpl.finishLocked(callingActivity, true);
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        public boolean supportsLocalVoiceInteraction() {
            VoiceInteractionManagerServiceImpl voiceInteractionManagerServiceImpl = this.mImpl;
            if (voiceInteractionManagerServiceImpl == null) {
                return false;
            }
            return voiceInteractionManagerServiceImpl.supportsLocalVoiceInteraction();
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            try {
                return VoiceInteractionManagerService.super.onTransact(code, data, reply, flags);
            } catch (RuntimeException e) {
                if (!(e instanceof SecurityException)) {
                    Slog.wtf(VoiceInteractionManagerService.TAG, "VoiceInteractionManagerService Crash", e);
                }
                throw e;
            }
        }

        public void initForUser(int userHandle) {
            String curInteractorStr = Settings.Secure.getStringForUser(VoiceInteractionManagerService.this.mContext.getContentResolver(), "voice_interaction_service", userHandle);
            ComponentName curRecognizer = getCurRecognizer(userHandle);
            VoiceInteractionServiceInfo curInteractorInfo = null;
            if (curInteractorStr == null && curRecognizer != null && this.mEnableService && (curInteractorInfo = findAvailInteractor(userHandle, curRecognizer.getPackageName())) != null) {
                curRecognizer = null;
            }
            String forceInteractorPackage = getForceVoiceInteractionServicePackage(VoiceInteractionManagerService.this.mContext.getResources());
            if (!(forceInteractorPackage == null || (curInteractorInfo = findAvailInteractor(userHandle, forceInteractorPackage)) == null)) {
                curRecognizer = null;
            }
            if (!this.mEnableService && curInteractorStr != null && !TextUtils.isEmpty(curInteractorStr)) {
                setCurInteractor((ComponentName) null, userHandle);
                curInteractorStr = "";
            }
            if (curRecognizer != null) {
                IPackageManager pm = AppGlobals.getPackageManager();
                ServiceInfo interactorInfo = null;
                ServiceInfo recognizerInfo = null;
                ComponentName curInteractor = !TextUtils.isEmpty(curInteractorStr) ? ComponentName.unflattenFromString(curInteractorStr) : null;
                try {
                    recognizerInfo = pm.getServiceInfo(curRecognizer, 786432, userHandle);
                    if (curInteractor != null) {
                        interactorInfo = pm.getServiceInfo(curInteractor, 786432, userHandle);
                    }
                } catch (RemoteException e) {
                }
                if (recognizerInfo != null && (curInteractor == null || interactorInfo != null)) {
                    return;
                }
            }
            if (curInteractorInfo == null && this.mEnableService) {
                curInteractorInfo = findAvailInteractor(userHandle, (String) null);
            }
            if (curInteractorInfo != null) {
                setCurInteractor(new ComponentName(curInteractorInfo.getServiceInfo().packageName, curInteractorInfo.getServiceInfo().name), userHandle);
                if (curInteractorInfo.getRecognitionService() != null) {
                    setCurRecognizer(new ComponentName(curInteractorInfo.getServiceInfo().packageName, curInteractorInfo.getRecognitionService()), userHandle);
                    return;
                }
            }
            initSimpleRecognizer(curInteractorInfo, userHandle);
        }

        public void initSimpleRecognizer(VoiceInteractionServiceInfo curInteractorInfo, int userHandle) {
            ComponentName curRecognizer = findAvailRecognizer((String) null, userHandle);
            if (curRecognizer != null) {
                if (curInteractorInfo == null) {
                    setCurInteractor((ComponentName) null, userHandle);
                }
                setCurRecognizer(curRecognizer, userHandle);
            }
        }

        private boolean shouldEnableService(Context context) {
            if (getForceVoiceInteractionServicePackage(context.getResources()) != null) {
                return true;
            }
            return context.getPackageManager().hasSystemFeature("android.software.voice_recognizers");
        }

        private String getForceVoiceInteractionServicePackage(Resources res) {
            String interactorPackage = res.getString(17039756);
            if (TextUtils.isEmpty(interactorPackage)) {
                return null;
            }
            return interactorPackage;
        }

        public void systemRunning(boolean safeMode) {
            this.mSafeMode = safeMode;
            this.mPackageMonitor.register(VoiceInteractionManagerService.this.mContext, BackgroundThread.getHandler().getLooper(), UserHandle.ALL, true);
            new SettingsObserver(UiThread.getHandler());
            synchronized (this) {
                this.mCurUser = ActivityManager.getCurrentUser();
                switchImplementationIfNeededLocked(false);
            }
        }

        public void switchUser(int userHandle) {
            FgThread.getHandler().post(new Runnable(userHandle) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.this.lambda$switchUser$0$VoiceInteractionManagerService$VoiceInteractionManagerServiceStub(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$switchUser$0$VoiceInteractionManagerService$VoiceInteractionManagerServiceStub(int userHandle) {
            synchronized (this) {
                this.mCurUser = userHandle;
                this.mCurUserUnlocked = false;
                switchImplementationIfNeededLocked(false);
            }
        }

        /* access modifiers changed from: package-private */
        public void switchImplementationIfNeeded(boolean force) {
            synchronized (this) {
                switchImplementationIfNeededLocked(force);
            }
        }

        /* access modifiers changed from: package-private */
        public void switchImplementationIfNeededLocked(boolean force) {
            VoiceInteractionManagerServiceImpl voiceInteractionManagerServiceImpl;
            if (!this.mSafeMode) {
                String curService = Settings.Secure.getStringForUser(VoiceInteractionManagerService.this.mResolver, "voice_interaction_service", this.mCurUser);
                ComponentName serviceComponent = null;
                ServiceInfo serviceInfo = null;
                boolean hasComponent = false;
                if (curService != null && !curService.isEmpty()) {
                    try {
                        serviceComponent = ComponentName.unflattenFromString(curService);
                        serviceInfo = AppGlobals.getPackageManager().getServiceInfo(serviceComponent, 0, this.mCurUser);
                    } catch (RemoteException | RuntimeException e) {
                        Slog.wtf(VoiceInteractionManagerService.TAG, "Bad voice interaction service name " + curService, e);
                        serviceComponent = null;
                        serviceInfo = null;
                    }
                }
                if (!(serviceComponent == null || serviceInfo == null)) {
                    hasComponent = true;
                }
                if (VoiceInteractionManagerService.this.mUserManager.isUserUnlockingOrUnlocked(this.mCurUser)) {
                    if (hasComponent) {
                        VoiceInteractionManagerService.this.mShortcutServiceInternal.setShortcutHostPackage(VoiceInteractionManagerService.TAG, serviceComponent.getPackageName(), this.mCurUser);
                        VoiceInteractionManagerService.this.mAtmInternal.setAllowAppSwitches(VoiceInteractionManagerService.TAG, serviceInfo.applicationInfo.uid, this.mCurUser);
                    } else {
                        VoiceInteractionManagerService.this.mShortcutServiceInternal.setShortcutHostPackage(VoiceInteractionManagerService.TAG, (String) null, this.mCurUser);
                        VoiceInteractionManagerService.this.mAtmInternal.setAllowAppSwitches(VoiceInteractionManagerService.TAG, -1, this.mCurUser);
                    }
                }
                if (force || (voiceInteractionManagerServiceImpl = this.mImpl) == null || voiceInteractionManagerServiceImpl.mUser != this.mCurUser || !this.mImpl.mComponent.equals(serviceComponent)) {
                    unloadAllKeyphraseModels();
                    VoiceInteractionManagerServiceImpl voiceInteractionManagerServiceImpl2 = this.mImpl;
                    if (voiceInteractionManagerServiceImpl2 != null) {
                        voiceInteractionManagerServiceImpl2.shutdownLocked();
                    }
                    if (hasComponent) {
                        setImplLocked(new VoiceInteractionManagerServiceImpl(VoiceInteractionManagerService.this.mContext, UiThread.getHandler(), this, this.mCurUser, serviceComponent));
                        this.mImpl.startLocked();
                        return;
                    }
                    setImplLocked((VoiceInteractionManagerServiceImpl) null);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public VoiceInteractionServiceInfo findAvailInteractor(int userHandle, String packageName) {
            List<ResolveInfo> available = VoiceInteractionManagerService.this.mContext.getPackageManager().queryIntentServicesAsUser(new Intent("android.service.voice.VoiceInteractionService"), 269221888, userHandle);
            int numAvailable = available.size();
            if (numAvailable == 0) {
                Slog.w(VoiceInteractionManagerService.TAG, "no available voice interaction services found for user " + userHandle);
                return null;
            }
            VoiceInteractionServiceInfo foundInfo = null;
            for (int i = 0; i < numAvailable; i++) {
                ServiceInfo cur = available.get(i).serviceInfo;
                if ((cur.applicationInfo.flags & 1) != 0) {
                    ComponentName comp = new ComponentName(cur.packageName, cur.name);
                    try {
                        VoiceInteractionServiceInfo info = new VoiceInteractionServiceInfo(VoiceInteractionManagerService.this.mContext.getPackageManager(), comp, userHandle);
                        if (info.getParseError() != null) {
                            Slog.w(VoiceInteractionManagerService.TAG, "Bad interaction service " + comp + ": " + info.getParseError());
                        } else if (packageName == null || info.getServiceInfo().packageName.equals(packageName)) {
                            if (foundInfo == null) {
                                foundInfo = info;
                            } else {
                                Slog.w(VoiceInteractionManagerService.TAG, "More than one voice interaction service, picking first " + new ComponentName(foundInfo.getServiceInfo().packageName, foundInfo.getServiceInfo().name) + " over " + new ComponentName(cur.packageName, cur.name));
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        Slog.w(VoiceInteractionManagerService.TAG, "Failure looking up interaction service " + comp);
                    }
                }
            }
            return foundInfo;
        }

        /* access modifiers changed from: package-private */
        public ComponentName getCurInteractor(int userHandle) {
            String curInteractor = Settings.Secure.getStringForUser(VoiceInteractionManagerService.this.mContext.getContentResolver(), "voice_interaction_service", userHandle);
            if (TextUtils.isEmpty(curInteractor)) {
                return null;
            }
            return ComponentName.unflattenFromString(curInteractor);
        }

        /* access modifiers changed from: package-private */
        public void setCurInteractor(ComponentName comp, int userHandle) {
            Settings.Secure.putStringForUser(VoiceInteractionManagerService.this.mContext.getContentResolver(), "voice_interaction_service", comp != null ? comp.flattenToShortString() : "", userHandle);
        }

        /* access modifiers changed from: package-private */
        public ComponentName findAvailRecognizer(String prefPackage, int userHandle) {
            List<ResolveInfo> available = VoiceInteractionManagerService.this.mContext.getPackageManager().queryIntentServicesAsUser(new Intent("android.speech.RecognitionService"), 786432, userHandle);
            int numAvailable = available.size();
            if (numAvailable == 0) {
                Slog.w(VoiceInteractionManagerService.TAG, "no available voice recognition services found for user " + userHandle);
                return null;
            }
            if (prefPackage != null) {
                for (int i = 0; i < numAvailable; i++) {
                    ServiceInfo serviceInfo = available.get(i).serviceInfo;
                    if (prefPackage.equals(serviceInfo.packageName)) {
                        return new ComponentName(serviceInfo.packageName, serviceInfo.name);
                    }
                }
            }
            if (numAvailable > 1) {
                Slog.w(VoiceInteractionManagerService.TAG, "more than one voice recognition service found, picking first");
            }
            ServiceInfo serviceInfo2 = available.get(0).serviceInfo;
            return new ComponentName(serviceInfo2.packageName, serviceInfo2.name);
        }

        /* access modifiers changed from: package-private */
        public ComponentName getCurRecognizer(int userHandle) {
            String curRecognizer = Settings.Secure.getStringForUser(VoiceInteractionManagerService.this.mContext.getContentResolver(), "voice_recognition_service", userHandle);
            if (TextUtils.isEmpty(curRecognizer)) {
                return null;
            }
            return ComponentName.unflattenFromString(curRecognizer);
        }

        /* access modifiers changed from: package-private */
        public void setCurRecognizer(ComponentName comp, int userHandle) {
            Settings.Secure.putStringForUser(VoiceInteractionManagerService.this.mContext.getContentResolver(), "voice_recognition_service", comp != null ? comp.flattenToShortString() : "", userHandle);
        }

        /* access modifiers changed from: package-private */
        public ComponentName getCurAssistant(int userHandle) {
            String curAssistant = Settings.Secure.getStringForUser(VoiceInteractionManagerService.this.mContext.getContentResolver(), "assistant", userHandle);
            if (TextUtils.isEmpty(curAssistant)) {
                return null;
            }
            return ComponentName.unflattenFromString(curAssistant);
        }

        /* access modifiers changed from: package-private */
        public void resetCurAssistant(int userHandle) {
            Settings.Secure.putStringForUser(VoiceInteractionManagerService.this.mContext.getContentResolver(), "assistant", (String) null, userHandle);
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public void showSession(IVoiceInteractionService service, Bundle args, int flags) {
            synchronized (this) {
                enforceIsCurrentVoiceInteractionService(service);
                long caller = Binder.clearCallingIdentity();
                try {
                    this.mImpl.showSessionLocked(args, flags, (IVoiceInteractionSessionShowCallback) null, (IBinder) null);
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public boolean deliverNewSession(IBinder token, IVoiceInteractionSession session, IVoiceInteractor interactor) {
            boolean deliverNewSessionLocked;
            synchronized (this) {
                if (this.mImpl != null) {
                    long caller = Binder.clearCallingIdentity();
                    try {
                        deliverNewSessionLocked = this.mImpl.deliverNewSessionLocked(token, session, interactor);
                    } finally {
                        Binder.restoreCallingIdentity(caller);
                    }
                } else {
                    throw new SecurityException("deliverNewSession without running voice interaction service");
                }
            }
            return deliverNewSessionLocked;
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public boolean showSessionFromSession(IBinder token, Bundle sessionArgs, int flags) {
            synchronized (this) {
                if (this.mImpl == null) {
                    Slog.w(VoiceInteractionManagerService.TAG, "showSessionFromSession without running voice interaction service");
                    return false;
                }
                long caller = Binder.clearCallingIdentity();
                try {
                    boolean showSessionLocked = this.mImpl.showSessionLocked(sessionArgs, flags, (IVoiceInteractionSessionShowCallback) null, (IBinder) null);
                    return showSessionLocked;
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public boolean hideSessionFromSession(IBinder token) {
            synchronized (this) {
                if (this.mImpl == null) {
                    Slog.w(VoiceInteractionManagerService.TAG, "hideSessionFromSession without running voice interaction service");
                    return false;
                }
                long caller = Binder.clearCallingIdentity();
                try {
                    boolean hideSessionLocked = this.mImpl.hideSessionLocked();
                    return hideSessionLocked;
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        public int startVoiceActivity(IBinder token, Intent intent, String resolvedType) {
            synchronized (this) {
                if (this.mImpl == null) {
                    Slog.w(VoiceInteractionManagerService.TAG, "startVoiceActivity without running voice interaction service");
                    return -96;
                }
                int callingPid = Binder.getCallingPid();
                int callingUid = Binder.getCallingUid();
                long caller = Binder.clearCallingIdentity();
                try {
                    int startVoiceActivityLocked = this.mImpl.startVoiceActivityLocked(callingPid, callingUid, token, intent, resolvedType);
                    return startVoiceActivityLocked;
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        public int startAssistantActivity(IBinder token, Intent intent, String resolvedType) {
            synchronized (this) {
                if (this.mImpl == null) {
                    Slog.w(VoiceInteractionManagerService.TAG, "startAssistantActivity without running voice interaction service");
                    return -96;
                }
                int callingPid = Binder.getCallingPid();
                int callingUid = Binder.getCallingUid();
                long caller = Binder.clearCallingIdentity();
                try {
                    int startAssistantActivityLocked = this.mImpl.startAssistantActivityLocked(callingPid, callingUid, token, intent, resolvedType);
                    return startAssistantActivityLocked;
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void requestDirectActions(IBinder token, int taskId, IBinder assistToken, RemoteCallback cancellationCallback, RemoteCallback resultCallback) {
            synchronized (this) {
                if (this.mImpl == null) {
                    Slog.w(VoiceInteractionManagerService.TAG, "requestDirectActions without running voice interaction service");
                    resultCallback.sendResult((Bundle) null);
                    return;
                }
                long caller = Binder.clearCallingIdentity();
                try {
                    this.mImpl.requestDirectActionsLocked(token, taskId, assistToken, cancellationCallback, resultCallback);
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 13 */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0034, code lost:
            r0 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0035, code lost:
            android.os.Binder.restoreCallingIdentity(r11);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0038, code lost:
            throw r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x003e, code lost:
            r0 = th;
         */
        /* JADX WARNING: Exception block dominator not found, dom blocks: [B:7:0x0011, B:13:0x001d] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void performDirectAction(android.os.IBinder r14, java.lang.String r15, android.os.Bundle r16, int r17, android.os.IBinder r18, android.os.RemoteCallback r19, android.os.RemoteCallback r20) {
            /*
                r13 = this;
                r1 = r13
                monitor-enter(r13)
                com.android.server.voiceinteraction.VoiceInteractionManagerServiceImpl r0 = r1.mImpl     // Catch:{ all -> 0x0039 }
                if (r0 != 0) goto L_0x0016
                java.lang.String r0 = "VoiceInteractionManagerService"
                java.lang.String r2 = "performDirectAction without running voice interaction service"
                android.util.Slog.w(r0, r2)     // Catch:{ all -> 0x0039 }
                r0 = 0
                r10 = r20
                r10.sendResult(r0)     // Catch:{ all -> 0x003e }
                monitor-exit(r13)     // Catch:{ all -> 0x003e }
                return
            L_0x0016:
                r10 = r20
                long r2 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x003e }
                r11 = r2
                com.android.server.voiceinteraction.VoiceInteractionManagerServiceImpl r2 = r1.mImpl     // Catch:{ all -> 0x0034 }
                r3 = r14
                r4 = r15
                r5 = r16
                r6 = r17
                r7 = r18
                r8 = r19
                r9 = r20
                r2.performDirectActionLocked(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x0034 }
                android.os.Binder.restoreCallingIdentity(r11)     // Catch:{ all -> 0x003e }
                monitor-exit(r13)     // Catch:{ all -> 0x003e }
                return
            L_0x0034:
                r0 = move-exception
                android.os.Binder.restoreCallingIdentity(r11)     // Catch:{ all -> 0x003e }
                throw r0     // Catch:{ all -> 0x003e }
            L_0x0039:
                r0 = move-exception
                r10 = r20
            L_0x003c:
                monitor-exit(r13)     // Catch:{ all -> 0x003e }
                throw r0
            L_0x003e:
                r0 = move-exception
                goto L_0x003c
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.voiceinteraction.VoiceInteractionManagerService.VoiceInteractionManagerServiceStub.performDirectAction(android.os.IBinder, java.lang.String, android.os.Bundle, int, android.os.IBinder, android.os.RemoteCallback, android.os.RemoteCallback):void");
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public void setKeepAwake(IBinder token, boolean keepAwake) {
            synchronized (this) {
                if (this.mImpl == null) {
                    Slog.w(VoiceInteractionManagerService.TAG, "setKeepAwake without running voice interaction service");
                    return;
                }
                long caller = Binder.clearCallingIdentity();
                try {
                    this.mImpl.setKeepAwakeLocked(token, keepAwake);
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public void closeSystemDialogs(IBinder token) {
            synchronized (this) {
                if (this.mImpl == null) {
                    Slog.w(VoiceInteractionManagerService.TAG, "closeSystemDialogs without running voice interaction service");
                    return;
                }
                long caller = Binder.clearCallingIdentity();
                try {
                    this.mImpl.closeSystemDialogsLocked(token);
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public void finish(IBinder token) {
            synchronized (this) {
                if (this.mImpl == null) {
                    Slog.w(VoiceInteractionManagerService.TAG, "finish without running voice interaction service");
                    return;
                }
                long caller = Binder.clearCallingIdentity();
                try {
                    this.mImpl.finishLocked(token, false);
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public void setDisabledShowContext(int flags) {
            synchronized (this) {
                if (this.mImpl == null) {
                    Slog.w(VoiceInteractionManagerService.TAG, "setDisabledShowContext without running voice interaction service");
                    return;
                }
                int callingUid = Binder.getCallingUid();
                long caller = Binder.clearCallingIdentity();
                try {
                    this.mImpl.setDisabledShowContextLocked(callingUid, flags);
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public int getDisabledShowContext() {
            synchronized (this) {
                if (this.mImpl == null) {
                    Slog.w(VoiceInteractionManagerService.TAG, "getDisabledShowContext without running voice interaction service");
                    return 0;
                }
                int callingUid = Binder.getCallingUid();
                long caller = Binder.clearCallingIdentity();
                try {
                    int disabledShowContextLocked = this.mImpl.getDisabledShowContextLocked(callingUid);
                    return disabledShowContextLocked;
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public int getUserDisabledShowContext() {
            synchronized (this) {
                if (this.mImpl == null) {
                    Slog.w(VoiceInteractionManagerService.TAG, "getUserDisabledShowContext without running voice interaction service");
                    return 0;
                }
                int callingUid = Binder.getCallingUid();
                long caller = Binder.clearCallingIdentity();
                try {
                    int userDisabledShowContextLocked = this.mImpl.getUserDisabledShowContextLocked(callingUid);
                    return userDisabledShowContextLocked;
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        public SoundTrigger.KeyphraseSoundModel getKeyphraseSoundModel(int keyphraseId, String bcp47Locale) {
            enforceCallingPermission("android.permission.MANAGE_VOICE_KEYPHRASES");
            if (bcp47Locale != null) {
                int callingUid = UserHandle.getCallingUserId();
                long caller = Binder.clearCallingIdentity();
                try {
                    return VoiceInteractionManagerService.this.mDbHelper.getKeyphraseSoundModel(keyphraseId, callingUid, bcp47Locale);
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            } else {
                throw new IllegalArgumentException("Illegal argument(s) in getKeyphraseSoundModel");
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public int updateKeyphraseSoundModel(SoundTrigger.KeyphraseSoundModel model) {
            enforceCallingPermission("android.permission.MANAGE_VOICE_KEYPHRASES");
            if (model != null) {
                long caller = Binder.clearCallingIdentity();
                try {
                    if (VoiceInteractionManagerService.this.mDbHelper.updateKeyphraseSoundModel(model)) {
                        synchronized (this) {
                            if (!(this.mImpl == null || this.mImpl.mService == null)) {
                                this.mImpl.notifySoundModelsChangedLocked();
                            }
                        }
                        Binder.restoreCallingIdentity(caller);
                        return 0;
                    }
                    Binder.restoreCallingIdentity(caller);
                    return Integer.MIN_VALUE;
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(caller);
                    throw th;
                }
            } else {
                throw new IllegalArgumentException("Model must not be null");
            }
        }

        public int deleteKeyphraseSoundModel(int keyphraseId, String bcp47Locale) {
            enforceCallingPermission("android.permission.MANAGE_VOICE_KEYPHRASES");
            if (bcp47Locale != null) {
                int callingUid = UserHandle.getCallingUserId();
                long caller = Binder.clearCallingIdentity();
                try {
                    int unloadStatus = VoiceInteractionManagerService.this.mSoundTriggerInternal.unloadKeyphraseModel(keyphraseId);
                    if (unloadStatus != 0) {
                        Slog.w(VoiceInteractionManagerService.TAG, "Unable to unload keyphrase sound model:" + unloadStatus);
                    }
                    boolean deleted = VoiceInteractionManagerService.this.mDbHelper.deleteKeyphraseSoundModel(keyphraseId, callingUid, bcp47Locale);
                    int i = deleted ? 0 : Integer.MIN_VALUE;
                    if (deleted) {
                        synchronized (this) {
                            if (!(this.mImpl == null || this.mImpl.mService == null)) {
                                this.mImpl.notifySoundModelsChangedLocked();
                            }
                            VoiceInteractionManagerService.this.mLoadedKeyphraseIds.remove(Integer.valueOf(keyphraseId));
                        }
                    }
                    Binder.restoreCallingIdentity(caller);
                    return i;
                } catch (Throwable th) {
                    if (0 != 0) {
                        synchronized (this) {
                            if (!(this.mImpl == null || this.mImpl.mService == null)) {
                                this.mImpl.notifySoundModelsChangedLocked();
                            }
                            VoiceInteractionManagerService.this.mLoadedKeyphraseIds.remove(Integer.valueOf(keyphraseId));
                        }
                    }
                    Binder.restoreCallingIdentity(caller);
                    throw th;
                }
            } else {
                throw new IllegalArgumentException("Illegal argument(s) in deleteKeyphraseSoundModel");
            }
        }

        public boolean isEnrolledForKeyphrase(IVoiceInteractionService service, int keyphraseId, String bcp47Locale) {
            synchronized (this) {
                enforceIsCurrentVoiceInteractionService(service);
            }
            if (bcp47Locale != null) {
                int callingUid = UserHandle.getCallingUserId();
                long caller = Binder.clearCallingIdentity();
                try {
                    return VoiceInteractionManagerService.this.mDbHelper.getKeyphraseSoundModel(keyphraseId, callingUid, bcp47Locale) != null;
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            } else {
                throw new IllegalArgumentException("Illegal argument(s) in isEnrolledForKeyphrase");
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public SoundTrigger.ModuleProperties getDspModuleProperties(IVoiceInteractionService service) {
            SoundTrigger.ModuleProperties moduleProperties;
            synchronized (this) {
                enforceIsCurrentVoiceInteractionService(service);
                long caller = Binder.clearCallingIdentity();
                try {
                    moduleProperties = VoiceInteractionManagerService.this.mSoundTriggerInternal.getModuleProperties();
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            }
            return moduleProperties;
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        public int startRecognition(IVoiceInteractionService service, int keyphraseId, String bcp47Locale, IRecognitionStatusCallback callback, SoundTrigger.RecognitionConfig recognitionConfig) {
            synchronized (this) {
                enforceIsCurrentVoiceInteractionService(service);
                if (callback == null || recognitionConfig == null || bcp47Locale == null) {
                    throw new IllegalArgumentException("Illegal argument(s) in startRecognition");
                }
            }
            int callingUid = UserHandle.getCallingUserId();
            long caller = Binder.clearCallingIdentity();
            try {
                SoundTrigger.KeyphraseSoundModel soundModel = VoiceInteractionManagerService.this.mDbHelper.getKeyphraseSoundModel(keyphraseId, callingUid, bcp47Locale);
                if (!(soundModel == null || soundModel.uuid == null)) {
                    if (soundModel.keyphrases != null) {
                        synchronized (this) {
                            VoiceInteractionManagerService.this.mLoadedKeyphraseIds.add(Integer.valueOf(keyphraseId));
                        }
                        int startRecognition = VoiceInteractionManagerService.this.mSoundTriggerInternal.startRecognition(keyphraseId, soundModel, callback, recognitionConfig);
                        Binder.restoreCallingIdentity(caller);
                        return startRecognition;
                    }
                }
                Slog.w(VoiceInteractionManagerService.TAG, "No matching sound model found in startRecognition");
                Binder.restoreCallingIdentity(caller);
                return Integer.MIN_VALUE;
            } catch (Throwable soundModel2) {
                Binder.restoreCallingIdentity(caller);
                throw soundModel2;
            }
        }

        public int stopRecognition(IVoiceInteractionService service, int keyphraseId, IRecognitionStatusCallback callback) {
            synchronized (this) {
                enforceIsCurrentVoiceInteractionService(service);
            }
            long caller = Binder.clearCallingIdentity();
            try {
                return VoiceInteractionManagerService.this.mSoundTriggerInternal.stopRecognition(keyphraseId, callback);
            } finally {
                Binder.restoreCallingIdentity(caller);
            }
        }

        /* access modifiers changed from: private */
        public synchronized void unloadAllKeyphraseModels() {
            int i = 0;
            while (i < VoiceInteractionManagerService.this.mLoadedKeyphraseIds.size()) {
                long caller = Binder.clearCallingIdentity();
                try {
                    int status = VoiceInteractionManagerService.this.mSoundTriggerInternal.unloadKeyphraseModel(VoiceInteractionManagerService.this.mLoadedKeyphraseIds.valueAt(i).intValue());
                    if (status != 0) {
                        try {
                            Slog.w(VoiceInteractionManagerService.TAG, "Failed to unload keyphrase " + VoiceInteractionManagerService.this.mLoadedKeyphraseIds.valueAt(i) + ":" + status);
                        } catch (Throwable th) {
                            th = th;
                        }
                    }
                    Binder.restoreCallingIdentity(caller);
                    i++;
                } catch (Throwable th2) {
                    th = th2;
                    Binder.restoreCallingIdentity(caller);
                    throw th;
                }
            }
            VoiceInteractionManagerService.this.mLoadedKeyphraseIds.clear();
        }

        public ComponentName getActiveServiceComponentName() {
            ComponentName componentName;
            enforceCallingPermission("android.permission.ACCESS_VOICE_INTERACTION_SERVICE");
            synchronized (this) {
                componentName = this.mImpl != null ? this.mImpl.mComponent : null;
            }
            return componentName;
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public boolean showSessionForActiveService(Bundle args, int sourceFlags, IVoiceInteractionSessionShowCallback showCallback, IBinder activityToken) {
            enforceCallingPermission("android.permission.ACCESS_VOICE_INTERACTION_SERVICE");
            synchronized (this) {
                if (this.mImpl == null) {
                    Slog.w(VoiceInteractionManagerService.TAG, "showSessionForActiveService without running voice interactionservice");
                    return false;
                }
                long caller = Binder.clearCallingIdentity();
                try {
                    boolean showSessionLocked = this.mImpl.showSessionLocked(args, sourceFlags | 1 | 2, showCallback, activityToken);
                    return showSessionLocked;
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        public void hideCurrentSession() throws RemoteException {
            enforceCallingPermission("android.permission.ACCESS_VOICE_INTERACTION_SERVICE");
            synchronized (this) {
                if (this.mImpl != null) {
                    long caller = Binder.clearCallingIdentity();
                    try {
                        if (!(this.mImpl.mActiveSession == null || this.mImpl.mActiveSession.mSession == null)) {
                            this.mImpl.mActiveSession.mSession.closeSystemDialogs();
                        }
                    } catch (RemoteException e) {
                        Log.w(VoiceInteractionManagerService.TAG, "Failed to call closeSystemDialogs", e);
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(caller);
                        throw th;
                    }
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public void launchVoiceAssistFromKeyguard() {
            enforceCallingPermission("android.permission.ACCESS_VOICE_INTERACTION_SERVICE");
            synchronized (this) {
                if (this.mImpl == null) {
                    Slog.w(VoiceInteractionManagerService.TAG, "launchVoiceAssistFromKeyguard without running voice interactionservice");
                    return;
                }
                long caller = Binder.clearCallingIdentity();
                try {
                    this.mImpl.launchVoiceAssistFromKeyguard();
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        public boolean isSessionRunning() {
            boolean z;
            enforceCallingPermission("android.permission.ACCESS_VOICE_INTERACTION_SERVICE");
            synchronized (this) {
                z = (this.mImpl == null || this.mImpl.mActiveSession == null) ? false : true;
            }
            return z;
        }

        public boolean activeServiceSupportsAssist() {
            boolean z;
            enforceCallingPermission("android.permission.ACCESS_VOICE_INTERACTION_SERVICE");
            synchronized (this) {
                z = (this.mImpl == null || this.mImpl.mInfo == null || !this.mImpl.mInfo.getSupportsAssist()) ? false : true;
            }
            return z;
        }

        public boolean activeServiceSupportsLaunchFromKeyguard() throws RemoteException {
            boolean z;
            enforceCallingPermission("android.permission.ACCESS_VOICE_INTERACTION_SERVICE");
            synchronized (this) {
                z = (this.mImpl == null || this.mImpl.mInfo == null || !this.mImpl.mInfo.getSupportsLaunchFromKeyguard()) ? false : true;
            }
            return z;
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        public void onLockscreenShown() {
            enforceCallingPermission("android.permission.ACCESS_VOICE_INTERACTION_SERVICE");
            synchronized (this) {
                if (this.mImpl != null) {
                    long caller = Binder.clearCallingIdentity();
                    try {
                        if (!(this.mImpl.mActiveSession == null || this.mImpl.mActiveSession.mSession == null)) {
                            this.mImpl.mActiveSession.mSession.onLockscreenShown();
                        }
                    } catch (RemoteException e) {
                        Log.w(VoiceInteractionManagerService.TAG, "Failed to call onLockscreenShown", e);
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(caller);
                        throw th;
                    }
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        public void registerVoiceInteractionSessionListener(IVoiceInteractionSessionListener listener) {
            enforceCallingPermission("android.permission.ACCESS_VOICE_INTERACTION_SERVICE");
            synchronized (this) {
                VoiceInteractionManagerService.this.mVoiceInteractionSessionListeners.register(listener);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public void getActiveServiceSupportedActions(List<String> voiceActions, IVoiceActionCheckCallback callback) {
            enforceCallingPermission("android.permission.ACCESS_VOICE_INTERACTION_SERVICE");
            synchronized (this) {
                if (this.mImpl == null) {
                    try {
                        callback.onComplete((List) null);
                    } catch (RemoteException e) {
                    }
                    return;
                }
                long caller = Binder.clearCallingIdentity();
                try {
                    this.mImpl.getActiveServiceSupportedActions(voiceActions, callback);
                } finally {
                    Binder.restoreCallingIdentity(caller);
                }
            }
        }

        public void onSessionShown() {
            synchronized (this) {
                int size = VoiceInteractionManagerService.this.mVoiceInteractionSessionListeners.beginBroadcast();
                for (int i = 0; i < size; i++) {
                    try {
                        VoiceInteractionManagerService.this.mVoiceInteractionSessionListeners.getBroadcastItem(i).onVoiceSessionShown();
                    } catch (RemoteException e) {
                        Slog.e(VoiceInteractionManagerService.TAG, "Error delivering voice interaction open event.", e);
                    }
                }
                VoiceInteractionManagerService.this.mVoiceInteractionSessionListeners.finishBroadcast();
            }
        }

        public void onSessionHidden() {
            synchronized (this) {
                int size = VoiceInteractionManagerService.this.mVoiceInteractionSessionListeners.beginBroadcast();
                for (int i = 0; i < size; i++) {
                    try {
                        VoiceInteractionManagerService.this.mVoiceInteractionSessionListeners.getBroadcastItem(i).onVoiceSessionHidden();
                    } catch (RemoteException e) {
                        Slog.e(VoiceInteractionManagerService.TAG, "Error delivering voice interaction closed event.", e);
                    }
                }
                VoiceInteractionManagerService.this.mVoiceInteractionSessionListeners.finishBroadcast();
            }
        }

        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            if (DumpUtils.checkDumpPermission(VoiceInteractionManagerService.this.mContext, VoiceInteractionManagerService.TAG, pw)) {
                synchronized (this) {
                    pw.println("VOICE INTERACTION MANAGER (dumpsys voiceinteraction)");
                    pw.println("  mEnableService: " + this.mEnableService);
                    if (this.mImpl == null) {
                        pw.println("  (No active implementation)");
                        return;
                    }
                    this.mImpl.dumpLocked(fd, pw, args);
                    VoiceInteractionManagerService.this.mSoundTriggerInternal.dump(fd, pw, args);
                }
            }
        }

        public void setUiHints(IVoiceInteractionService service, Bundle hints) {
            synchronized (this) {
                enforceIsCurrentVoiceInteractionService(service);
                int size = VoiceInteractionManagerService.this.mVoiceInteractionSessionListeners.beginBroadcast();
                for (int i = 0; i < size; i++) {
                    try {
                        VoiceInteractionManagerService.this.mVoiceInteractionSessionListeners.getBroadcastItem(i).onSetUiHints(hints);
                    } catch (RemoteException e) {
                        Slog.e(VoiceInteractionManagerService.TAG, "Error delivering UI hints.", e);
                    }
                }
                VoiceInteractionManagerService.this.mVoiceInteractionSessionListeners.finishBroadcast();
            }
        }

        private void enforceCallingPermission(String permission) {
            if (VoiceInteractionManagerService.this.mContext.checkCallingOrSelfPermission(permission) != 0) {
                throw new SecurityException("Caller does not hold the permission " + permission);
            }
        }

        private void enforceIsCurrentVoiceInteractionService(IVoiceInteractionService service) {
            VoiceInteractionManagerServiceImpl voiceInteractionManagerServiceImpl = this.mImpl;
            if (voiceInteractionManagerServiceImpl == null || voiceInteractionManagerServiceImpl.mService == null || service.asBinder() != this.mImpl.mService.asBinder()) {
                throw new SecurityException("Caller is not the current voice interaction service");
            }
        }

        /* access modifiers changed from: private */
        public void setImplLocked(VoiceInteractionManagerServiceImpl impl) {
            this.mImpl = impl;
            VoiceInteractionManagerService.this.mAtmInternal.notifyActiveVoiceInteractionServiceChanged(getActiveServiceComponentName());
        }

        class RoleObserver implements OnRoleHoldersChangedListener {
            private PackageManager mPm = VoiceInteractionManagerService.this.mContext.getPackageManager();
            private RoleManager mRm = ((RoleManager) VoiceInteractionManagerService.this.mContext.getSystemService(RoleManager.class));

            RoleObserver(Executor executor) {
                this.mRm.addOnRoleHoldersChangedListenerAsUser(executor, this, UserHandle.ALL);
                if (this.mRm.isRoleAvailable("android.app.role.ASSISTANT")) {
                    onRoleHoldersChanged("android.app.role.ASSISTANT", UserHandle.of(((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).getCurrentUserId()));
                }
            }

            private String getDefaultRecognizer(UserHandle user) {
                ResolveInfo resolveInfo = this.mPm.resolveServiceAsUser(new Intent("android.speech.RecognitionService"), 128, user.getIdentifier());
                if (resolveInfo != null && resolveInfo.serviceInfo != null) {
                    return new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name).flattenToShortString();
                }
                Log.w(VoiceInteractionManagerService.TAG, "Unable to resolve default voice recognition service.");
                return "";
            }

            public void onRoleHoldersChanged(String roleName, UserHandle user) {
                if (roleName.equals("android.app.role.ASSISTANT")) {
                    List<String> roleHolders = this.mRm.getRoleHoldersAsUser(roleName, user);
                    int userId = user.getIdentifier();
                    if (roleHolders.isEmpty()) {
                        Settings.Secure.putStringForUser(VoiceInteractionManagerService.this.getContext().getContentResolver(), "assistant", "", userId);
                        Settings.Secure.putStringForUser(VoiceInteractionManagerService.this.getContext().getContentResolver(), "voice_interaction_service", "", userId);
                        Settings.Secure.putStringForUser(VoiceInteractionManagerService.this.getContext().getContentResolver(), "voice_recognition_service", getDefaultRecognizer(user), userId);
                        return;
                    }
                    String pkg = roleHolders.get(0);
                    for (ResolveInfo resolveInfo : this.mPm.queryIntentServicesAsUser(new Intent("android.service.voice.VoiceInteractionService").setPackage(pkg), 786560, userId)) {
                        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                        VoiceInteractionServiceInfo voiceInteractionServiceInfo = new VoiceInteractionServiceInfo(this.mPm, serviceInfo);
                        if (voiceInteractionServiceInfo.getSupportsAssist()) {
                            String serviceComponentName = serviceInfo.getComponentName().flattenToShortString();
                            String serviceRecognizerName = new ComponentName(pkg, voiceInteractionServiceInfo.getRecognitionService()).flattenToShortString();
                            Settings.Secure.putStringForUser(VoiceInteractionManagerService.this.getContext().getContentResolver(), "assistant", serviceComponentName, userId);
                            Settings.Secure.putStringForUser(VoiceInteractionManagerService.this.getContext().getContentResolver(), "voice_interaction_service", serviceComponentName, userId);
                            Settings.Secure.putStringForUser(VoiceInteractionManagerService.this.getContext().getContentResolver(), "voice_recognition_service", serviceRecognizerName, userId);
                            return;
                        }
                    }
                    Iterator<ResolveInfo> it = this.mPm.queryIntentActivitiesAsUser(new Intent("android.intent.action.ASSIST").setPackage(pkg), 851968, userId).iterator();
                    if (it.hasNext()) {
                        Settings.Secure.putStringForUser(VoiceInteractionManagerService.this.getContext().getContentResolver(), "assistant", it.next().activityInfo.getComponentName().flattenToShortString(), userId);
                        Settings.Secure.putStringForUser(VoiceInteractionManagerService.this.getContext().getContentResolver(), "voice_interaction_service", "", userId);
                        Settings.Secure.putStringForUser(VoiceInteractionManagerService.this.getContext().getContentResolver(), "voice_recognition_service", getDefaultRecognizer(user), userId);
                    }
                }
            }
        }

        class SettingsObserver extends ContentObserver {
            SettingsObserver(Handler handler) {
                super(handler);
                VoiceInteractionManagerService.this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("voice_interaction_service"), false, this, -1);
            }

            public void onChange(boolean selfChange) {
                synchronized (VoiceInteractionManagerServiceStub.this) {
                    VoiceInteractionManagerServiceStub.this.switchImplementationIfNeededLocked(false);
                }
            }
        }
    }
}
