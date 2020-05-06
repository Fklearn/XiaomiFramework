package com.android.server.tv;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.Rect;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.media.PlaybackParams;
import android.media.tv.DvbDeviceInfo;
import android.media.tv.ITvInputClient;
import android.media.tv.ITvInputHardware;
import android.media.tv.ITvInputHardwareCallback;
import android.media.tv.ITvInputManager;
import android.media.tv.ITvInputManagerCallback;
import android.media.tv.ITvInputService;
import android.media.tv.ITvInputServiceCallback;
import android.media.tv.ITvInputSession;
import android.media.tv.ITvInputSessionCallback;
import android.media.tv.TvContentRating;
import android.media.tv.TvContentRatingSystemInfo;
import android.media.tv.TvContract;
import android.media.tv.TvInputHardwareInfo;
import android.media.tv.TvInputInfo;
import android.media.tv.TvStreamConfig;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.view.InputChannel;
import android.view.Surface;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.IoThread;
import com.android.server.SystemService;
import com.android.server.UiModeManagerService;
import com.android.server.pm.PackageManagerService;
import com.android.server.slice.SliceClientPermissions;
import com.android.server.tv.TvInputHardwareManager;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TvInputManagerService extends SystemService {
    private static final boolean DEBUG = false;
    private static final String DVB_DIRECTORY = "/dev/dvb";
    private static final String TAG = "TvInputManagerService";
    /* access modifiers changed from: private */
    public static final Pattern sAdapterDirPattern = Pattern.compile("^adapter([0-9]+)$");
    /* access modifiers changed from: private */
    public static final Pattern sFrontEndDevicePattern = Pattern.compile("^dvb([0-9]+)\\.frontend([0-9]+)$");
    /* access modifiers changed from: private */
    public static final Pattern sFrontEndInAdapterDirPattern = Pattern.compile("^frontend([0-9]+)$");
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentUserId = 0;
    /* access modifiers changed from: private */
    public IBinder.DeathRecipient mDeathRecipient;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    /* access modifiers changed from: private */
    public final TvInputHardwareManager mTvInputHardwareManager;
    /* access modifiers changed from: private */
    public final SparseArray<UserState> mUserStates = new SparseArray<>();
    /* access modifiers changed from: private */
    public final WatchLogHandler mWatchLogHandler;

    public TvInputManagerService(Context context) {
        super(context);
        this.mContext = context;
        this.mWatchLogHandler = new WatchLogHandler(this.mContext.getContentResolver(), IoThread.get().getLooper());
        this.mTvInputHardwareManager = new TvInputHardwareManager(context, new HardwareListener());
        synchronized (this.mLock) {
            getOrCreateUserStateLocked(this.mCurrentUserId);
        }
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.tv.TvInputManagerService$BinderService, android.os.IBinder] */
    public void onStart() {
        publishBinderService("tv_input", new BinderService());
    }

    public void onBootPhase(int phase) {
        if (phase == 500) {
            registerBroadcastReceivers();
        } else if (phase == 600) {
            synchronized (this.mLock) {
                buildTvInputListLocked(this.mCurrentUserId, (String[]) null);
                buildTvContentRatingSystemListLocked(this.mCurrentUserId);
            }
        }
        this.mTvInputHardwareManager.onBootPhase(phase);
    }

    public void onUnlockUser(int userHandle) {
        synchronized (this.mLock) {
            if (this.mCurrentUserId == userHandle) {
                buildTvInputListLocked(this.mCurrentUserId, (String[]) null);
                buildTvContentRatingSystemListLocked(this.mCurrentUserId);
            }
        }
    }

    private void registerBroadcastReceivers() {
        new PackageMonitor() {
            private void buildTvInputList(String[] packages) {
                synchronized (TvInputManagerService.this.mLock) {
                    if (TvInputManagerService.this.mCurrentUserId == getChangingUserId()) {
                        TvInputManagerService.this.buildTvInputListLocked(TvInputManagerService.this.mCurrentUserId, packages);
                        TvInputManagerService.this.buildTvContentRatingSystemListLocked(TvInputManagerService.this.mCurrentUserId);
                    }
                }
            }

            public void onPackageUpdateFinished(String packageName, int uid) {
                buildTvInputList(new String[]{packageName});
            }

            public void onPackagesAvailable(String[] packages) {
                if (isReplacing()) {
                    buildTvInputList(packages);
                }
            }

            public void onPackagesUnavailable(String[] packages) {
                if (isReplacing()) {
                    buildTvInputList(packages);
                }
            }

            public void onSomePackagesChanged() {
                if (!isReplacing()) {
                    buildTvInputList((String[]) null);
                }
            }

            public boolean onPackageChanged(String packageName, int uid, String[] components) {
                return true;
            }
        }.register(this.mContext, (Looper) null, UserHandle.ALL, true);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.intent.action.USER_REMOVED");
        this.mContext.registerReceiverAsUser(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("android.intent.action.USER_SWITCHED".equals(action)) {
                    TvInputManagerService.this.switchUser(intent.getIntExtra("android.intent.extra.user_handle", 0));
                } else if ("android.intent.action.USER_REMOVED".equals(action)) {
                    TvInputManagerService.this.removeUser(intent.getIntExtra("android.intent.extra.user_handle", 0));
                }
            }
        }, UserHandle.ALL, intentFilter, (String) null, (Handler) null);
    }

    /* access modifiers changed from: private */
    public static boolean hasHardwarePermission(PackageManager pm, ComponentName component) {
        return pm.checkPermission("android.permission.TV_INPUT_HARDWARE", component.getPackageName()) == 0;
    }

    /* access modifiers changed from: private */
    public void buildTvInputListLocked(int userId, String[] updatedPackages) {
        UserState userState = getOrCreateUserStateLocked(userId);
        userState.packageSet.clear();
        PackageManager pm = this.mContext.getPackageManager();
        List<ResolveInfo> services2 = pm.queryIntentServicesAsUser(new Intent("android.media.tv.TvInputService"), 132, userId);
        List<TvInputInfo> inputList = new ArrayList<>();
        for (ResolveInfo ri : services2) {
            ServiceInfo si = ri.serviceInfo;
            if (!"android.permission.BIND_TV_INPUT".equals(si.permission)) {
                Slog.w(TAG, "Skipping TV input " + si.name + ": it does not require the permission " + "android.permission.BIND_TV_INPUT");
            } else {
                ComponentName component = new ComponentName(si.packageName, si.name);
                if (hasHardwarePermission(pm, component)) {
                    ServiceState serviceState = (ServiceState) userState.serviceStateMap.get(component);
                    if (serviceState == null) {
                        userState.serviceStateMap.put(component, new ServiceState(component, userId));
                        updateServiceConnectionLocked(component, userId);
                    } else {
                        inputList.addAll(serviceState.hardwareInputMap.values());
                    }
                } else {
                    try {
                        inputList.add(new TvInputInfo.Builder(this.mContext, ri).build());
                    } catch (Exception e) {
                        Slog.e(TAG, "failed to load TV input " + si.name, e);
                    }
                }
                userState.packageSet.add(si.packageName);
            }
        }
        Map<String, TvInputState> inputMap = new HashMap<>();
        for (TvInputInfo info : inputList) {
            TvInputState inputState = (TvInputState) userState.inputMap.get(info.getId());
            if (inputState == null) {
                inputState = new TvInputState();
            }
            TvInputInfo unused = inputState.info = info;
            inputMap.put(info.getId(), inputState);
        }
        for (String inputId : inputMap.keySet()) {
            if (!userState.inputMap.containsKey(inputId)) {
                notifyInputAddedLocked(userState, inputId);
            } else if (updatedPackages != null) {
                ComponentName component2 = inputMap.get(inputId).info.getComponent();
                int length = updatedPackages.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    if (component2.getPackageName().equals(updatedPackages[i])) {
                        updateServiceConnectionLocked(component2, userId);
                        notifyInputUpdatedLocked(userState, inputId);
                        break;
                    }
                    i++;
                }
            }
        }
        for (String inputId2 : userState.inputMap.keySet()) {
            if (!inputMap.containsKey(inputId2)) {
                ServiceState serviceState2 = (ServiceState) userState.serviceStateMap.get(((TvInputState) userState.inputMap.get(inputId2)).info.getComponent());
                if (serviceState2 != null) {
                    abortPendingCreateSessionRequestsLocked(serviceState2, inputId2, userId);
                }
                notifyInputRemovedLocked(userState, inputId2);
            }
        }
        userState.inputMap.clear();
        Map unused2 = userState.inputMap = inputMap;
    }

    /* access modifiers changed from: private */
    public void buildTvContentRatingSystemListLocked(int userId) {
        UserState userState = getOrCreateUserStateLocked(userId);
        userState.contentRatingSystemList.clear();
        for (ResolveInfo resolveInfo : this.mContext.getPackageManager().queryBroadcastReceivers(new Intent("android.media.tv.action.QUERY_CONTENT_RATING_SYSTEMS"), 128)) {
            ActivityInfo receiver = resolveInfo.activityInfo;
            Bundle metaData = receiver.metaData;
            if (metaData != null) {
                int xmlResId = metaData.getInt("android.media.tv.metadata.CONTENT_RATING_SYSTEMS");
                if (xmlResId == 0) {
                    Slog.w(TAG, "Missing meta-data 'android.media.tv.metadata.CONTENT_RATING_SYSTEMS' on receiver " + receiver.packageName + SliceClientPermissions.SliceAuthority.DELIMITER + receiver.name);
                } else {
                    userState.contentRatingSystemList.add(TvContentRatingSystemInfo.createTvContentRatingSystemInfo(xmlResId, receiver.applicationInfo));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void switchUser(int userId) {
        synchronized (this.mLock) {
            if (this.mCurrentUserId != userId) {
                UserState userState = this.mUserStates.get(this.mCurrentUserId);
                List<SessionState> sessionStatesToRelease = new ArrayList<>();
                for (SessionState sessionState : userState.sessionStateMap.values()) {
                    if (sessionState.session != null && !sessionState.isRecordingSession) {
                        sessionStatesToRelease.add(sessionState);
                    }
                }
                for (SessionState sessionState2 : sessionStatesToRelease) {
                    try {
                        sessionState2.session.release();
                    } catch (RemoteException e) {
                        Slog.e(TAG, "error in release", e);
                    }
                    clearSessionAndNotifyClientLocked(sessionState2);
                }
                Iterator<ComponentName> it = userState.serviceStateMap.keySet().iterator();
                while (it.hasNext()) {
                    ServiceState serviceState = (ServiceState) userState.serviceStateMap.get(it.next());
                    if (serviceState != null && serviceState.sessionTokens.isEmpty()) {
                        if (serviceState.callback != null) {
                            try {
                                serviceState.service.unregisterCallback(serviceState.callback);
                            } catch (RemoteException e2) {
                                Slog.e(TAG, "error in unregisterCallback", e2);
                            }
                        }
                        this.mContext.unbindService(serviceState.connection);
                        it.remove();
                    }
                }
                this.mCurrentUserId = userId;
                getOrCreateUserStateLocked(userId);
                buildTvInputListLocked(userId, (String[]) null);
                buildTvContentRatingSystemListLocked(userId);
                this.mWatchLogHandler.obtainMessage(3, getContentResolverForUser(userId)).sendToTarget();
            }
        }
    }

    /* access modifiers changed from: private */
    public void clearSessionAndNotifyClientLocked(SessionState state) {
        if (state.client != null) {
            try {
                state.client.onSessionReleased(state.seq);
            } catch (RemoteException e) {
                Slog.e(TAG, "error in onSessionReleased", e);
            }
        }
        for (SessionState sessionState : getOrCreateUserStateLocked(state.userId).sessionStateMap.values()) {
            if (state.sessionToken == sessionState.hardwareSessionToken) {
                releaseSessionLocked(sessionState.sessionToken, 1000, state.userId);
                try {
                    sessionState.client.onSessionReleased(sessionState.seq);
                } catch (RemoteException e2) {
                    Slog.e(TAG, "error in onSessionReleased", e2);
                }
            }
        }
        removeSessionStateLocked(state.sessionToken, state.userId);
    }

    /* access modifiers changed from: private */
    public void removeUser(int userId) {
        synchronized (this.mLock) {
            UserState userState = this.mUserStates.get(userId);
            if (userState != null) {
                for (SessionState state : userState.sessionStateMap.values()) {
                    if (state.session != null) {
                        try {
                            state.session.release();
                        } catch (RemoteException e) {
                            Slog.e(TAG, "error in release", e);
                        }
                    }
                }
                userState.sessionStateMap.clear();
                for (ServiceState serviceState : userState.serviceStateMap.values()) {
                    if (serviceState.service != null) {
                        if (serviceState.callback != null) {
                            try {
                                serviceState.service.unregisterCallback(serviceState.callback);
                            } catch (RemoteException e2) {
                                Slog.e(TAG, "error in unregisterCallback", e2);
                            }
                        }
                        this.mContext.unbindService(serviceState.connection);
                    }
                }
                userState.serviceStateMap.clear();
                userState.inputMap.clear();
                userState.packageSet.clear();
                userState.contentRatingSystemList.clear();
                userState.clientStateMap.clear();
                userState.callbackSet.clear();
                IBinder unused = userState.mainSessionToken = null;
                this.mUserStates.remove(userId);
            }
        }
    }

    private ContentResolver getContentResolverForUser(int userId) {
        Context context;
        UserHandle user = new UserHandle(userId);
        try {
            context = this.mContext.createPackageContextAsUser(PackageManagerService.PLATFORM_PACKAGE_NAME, 0, user);
        } catch (PackageManager.NameNotFoundException e) {
            Slog.e(TAG, "failed to create package context as user " + user);
            context = this.mContext;
        }
        return context.getContentResolver();
    }

    /* access modifiers changed from: private */
    public UserState getOrCreateUserStateLocked(int userId) {
        UserState userState = this.mUserStates.get(userId);
        if (userState != null) {
            return userState;
        }
        UserState userState2 = new UserState(this.mContext, userId);
        this.mUserStates.put(userId, userState2);
        return userState2;
    }

    /* access modifiers changed from: private */
    public ServiceState getServiceStateLocked(ComponentName component, int userId) {
        ServiceState serviceState = (ServiceState) getOrCreateUserStateLocked(userId).serviceStateMap.get(component);
        if (serviceState != null) {
            return serviceState;
        }
        throw new IllegalStateException("Service state not found for " + component + " (userId=" + userId + ")");
    }

    /* access modifiers changed from: private */
    public SessionState getSessionStateLocked(IBinder sessionToken, int callingUid, int userId) {
        SessionState sessionState = (SessionState) getOrCreateUserStateLocked(userId).sessionStateMap.get(sessionToken);
        if (sessionState == null) {
            throw new SessionNotFoundException("Session state not found for token " + sessionToken);
        } else if (callingUid == 1000 || callingUid == sessionState.callingUid) {
            return sessionState;
        } else {
            throw new SecurityException("Illegal access to the session with token " + sessionToken + " from uid " + callingUid);
        }
    }

    /* access modifiers changed from: private */
    public ITvInputSession getSessionLocked(IBinder sessionToken, int callingUid, int userId) {
        return getSessionLocked(getSessionStateLocked(sessionToken, callingUid, userId));
    }

    /* access modifiers changed from: private */
    public ITvInputSession getSessionLocked(SessionState sessionState) {
        ITvInputSession session = sessionState.session;
        if (session != null) {
            return session;
        }
        throw new IllegalStateException("Session not yet created for token " + sessionState.sessionToken);
    }

    /* access modifiers changed from: private */
    public int resolveCallingUserId(int callingPid, int callingUid, int requestedUserId, String methodName) {
        return ActivityManager.handleIncomingUser(callingPid, callingUid, requestedUserId, false, false, methodName, (String) null);
    }

    /* access modifiers changed from: private */
    public void updateServiceConnectionLocked(ComponentName component, int userId) {
        boolean shouldBind;
        UserState userState = getOrCreateUserStateLocked(userId);
        ServiceState serviceState = (ServiceState) userState.serviceStateMap.get(component);
        if (serviceState != null) {
            boolean z = false;
            if (serviceState.reconnecting) {
                if (serviceState.sessionTokens.isEmpty()) {
                    boolean unused = serviceState.reconnecting = false;
                } else {
                    return;
                }
            }
            if (userId == this.mCurrentUserId) {
                if (!serviceState.sessionTokens.isEmpty() || serviceState.isHardware) {
                    z = true;
                }
                shouldBind = z;
            } else {
                shouldBind = !serviceState.sessionTokens.isEmpty();
            }
            if (serviceState.service != null || !shouldBind) {
                if (serviceState.service != null && !shouldBind) {
                    this.mContext.unbindService(serviceState.connection);
                    userState.serviceStateMap.remove(component);
                }
            } else if (!serviceState.bound) {
                boolean unused2 = serviceState.bound = this.mContext.bindServiceAsUser(new Intent("android.media.tv.TvInputService").setComponent(component), serviceState.connection, 33554433, new UserHandle(userId));
            }
        }
    }

    /* access modifiers changed from: private */
    public void abortPendingCreateSessionRequestsLocked(ServiceState serviceState, String inputId, int userId) {
        UserState userState = getOrCreateUserStateLocked(userId);
        List<SessionState> sessionsToAbort = new ArrayList<>();
        for (IBinder sessionToken : serviceState.sessionTokens) {
            SessionState sessionState = (SessionState) userState.sessionStateMap.get(sessionToken);
            if (sessionState.session == null && (inputId == null || sessionState.inputId.equals(inputId))) {
                sessionsToAbort.add(sessionState);
            }
        }
        for (SessionState sessionState2 : sessionsToAbort) {
            removeSessionStateLocked(sessionState2.sessionToken, sessionState2.userId);
            sendSessionTokenToClientLocked(sessionState2.client, sessionState2.inputId, (IBinder) null, (InputChannel) null, sessionState2.seq);
        }
        updateServiceConnectionLocked(serviceState.component, userId);
    }

    /* access modifiers changed from: private */
    public boolean createSessionInternalLocked(ITvInputService service, IBinder sessionToken, int userId) {
        ITvInputService iTvInputService = service;
        SessionState sessionState = (SessionState) getOrCreateUserStateLocked(userId).sessionStateMap.get(sessionToken);
        RemoteException[] channels = InputChannel.openInputChannelPair(sessionToken.toString());
        SessionCallback sessionCallback = new SessionCallback(sessionState, channels);
        boolean created = true;
        try {
            if (sessionState.isRecordingSession) {
                iTvInputService.createRecordingSession(sessionCallback, sessionState.inputId);
            } else {
                iTvInputService.createSession(channels[1], sessionCallback, sessionState.inputId);
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "error in createSession", e);
            sendSessionTokenToClientLocked(sessionState.client, sessionState.inputId, (IBinder) null, (InputChannel) null, sessionState.seq);
            created = false;
        }
        channels[1].dispose();
        return created;
    }

    /* access modifiers changed from: private */
    public void sendSessionTokenToClientLocked(ITvInputClient client, String inputId, IBinder sessionToken, InputChannel channel, int seq) {
        try {
            client.onSessionCreated(inputId, sessionToken, channel, seq);
        } catch (RemoteException e) {
            Slog.e(TAG, "error in onSessionCreated", e);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003e, code lost:
        if (0 == 0) goto L_0x0041;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0041, code lost:
        removeSessionStateLocked(r6, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0044, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x002d, code lost:
        if (r0 != null) goto L_0x002f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x002f, code lost:
        com.android.server.tv.TvInputManagerService.SessionState.access$1702(r0, (android.media.tv.ITvInputSession) null);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void releaseSessionLocked(android.os.IBinder r6, int r7, int r8) {
        /*
            r5 = this;
            r0 = 0
            r1 = 0
            com.android.server.tv.TvInputManagerService$SessionState r2 = r5.getSessionStateLocked(r6, r7, r8)     // Catch:{ RemoteException | SessionNotFoundException -> 0x0035 }
            r0 = r2
            android.media.tv.ITvInputSession r2 = r0.session     // Catch:{ RemoteException | SessionNotFoundException -> 0x0035 }
            if (r2 == 0) goto L_0x002d
            com.android.server.tv.TvInputManagerService$UserState r2 = r5.getOrCreateUserStateLocked(r8)     // Catch:{ RemoteException | SessionNotFoundException -> 0x0035 }
            android.os.IBinder r3 = r2.mainSessionToken     // Catch:{ RemoteException | SessionNotFoundException -> 0x0035 }
            r4 = 0
            if (r6 != r3) goto L_0x001b
            r5.setMainLocked(r6, r4, r7, r8)     // Catch:{ RemoteException | SessionNotFoundException -> 0x0035 }
        L_0x001b:
            android.media.tv.ITvInputSession r3 = r0.session     // Catch:{ RemoteException | SessionNotFoundException -> 0x0035 }
            android.os.IBinder r3 = r3.asBinder()     // Catch:{ RemoteException | SessionNotFoundException -> 0x0035 }
            r3.unlinkToDeath(r0, r4)     // Catch:{ RemoteException | SessionNotFoundException -> 0x0035 }
            android.media.tv.ITvInputSession r3 = r0.session     // Catch:{ RemoteException | SessionNotFoundException -> 0x0035 }
            r3.release()     // Catch:{ RemoteException | SessionNotFoundException -> 0x0035 }
        L_0x002d:
            if (r0 == 0) goto L_0x0041
        L_0x002f:
            android.media.tv.ITvInputSession unused = r0.session = r1
            goto L_0x0041
        L_0x0033:
            r2 = move-exception
            goto L_0x0045
        L_0x0035:
            r2 = move-exception
            java.lang.String r3 = "TvInputManagerService"
            java.lang.String r4 = "error in releaseSession"
            android.util.Slog.e(r3, r4, r2)     // Catch:{ all -> 0x0033 }
            if (r0 == 0) goto L_0x0041
            goto L_0x002f
        L_0x0041:
            r5.removeSessionStateLocked(r6, r8)
            return
        L_0x0045:
            if (r0 == 0) goto L_0x004a
            android.media.tv.ITvInputSession unused = r0.session = r1
        L_0x004a:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.releaseSessionLocked(android.os.IBinder, int, int):void");
    }

    /* access modifiers changed from: private */
    public void removeSessionStateLocked(IBinder sessionToken, int userId) {
        UserState userState = getOrCreateUserStateLocked(userId);
        if (sessionToken == userState.mainSessionToken) {
            IBinder unused = userState.mainSessionToken = null;
        }
        SessionState sessionState = (SessionState) userState.sessionStateMap.remove(sessionToken);
        if (sessionState != null) {
            ClientState clientState = (ClientState) userState.clientStateMap.get(sessionState.client.asBinder());
            if (clientState != null) {
                clientState.sessionTokens.remove(sessionToken);
                if (clientState.isEmpty()) {
                    userState.clientStateMap.remove(sessionState.client.asBinder());
                    sessionState.client.asBinder().unlinkToDeath(clientState, 0);
                }
            }
            ServiceState serviceState = (ServiceState) userState.serviceStateMap.get(sessionState.componentName);
            if (serviceState != null) {
                serviceState.sessionTokens.remove(sessionToken);
            }
            updateServiceConnectionLocked(sessionState.componentName, userId);
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = sessionToken;
            args.arg2 = Long.valueOf(System.currentTimeMillis());
            this.mWatchLogHandler.obtainMessage(2, args).sendToTarget();
        }
    }

    /* access modifiers changed from: private */
    public void setMainLocked(IBinder sessionToken, boolean isMain, int callingUid, int userId) {
        try {
            SessionState sessionState = getSessionStateLocked(sessionToken, callingUid, userId);
            if (sessionState.hardwareSessionToken != null) {
                sessionState = getSessionStateLocked(sessionState.hardwareSessionToken, 1000, userId);
            }
            if (getServiceStateLocked(sessionState.componentName, userId).isHardware) {
                getSessionLocked(sessionState).setMain(isMain);
            }
        } catch (RemoteException | SessionNotFoundException e) {
            Slog.e(TAG, "error in setMain", e);
        }
    }

    private void notifyInputAddedLocked(UserState userState, String inputId) {
        for (ITvInputManagerCallback callback : userState.callbackSet) {
            try {
                callback.onInputAdded(inputId);
            } catch (RemoteException e) {
                Slog.e(TAG, "failed to report added input to callback", e);
            }
        }
    }

    private void notifyInputRemovedLocked(UserState userState, String inputId) {
        for (ITvInputManagerCallback callback : userState.callbackSet) {
            try {
                callback.onInputRemoved(inputId);
            } catch (RemoteException e) {
                Slog.e(TAG, "failed to report removed input to callback", e);
            }
        }
    }

    private void notifyInputUpdatedLocked(UserState userState, String inputId) {
        for (ITvInputManagerCallback callback : userState.callbackSet) {
            try {
                callback.onInputUpdated(inputId);
            } catch (RemoteException e) {
                Slog.e(TAG, "failed to report updated input to callback", e);
            }
        }
    }

    /* access modifiers changed from: private */
    public void notifyInputStateChangedLocked(UserState userState, String inputId, int state, ITvInputManagerCallback targetCallback) {
        if (targetCallback == null) {
            for (ITvInputManagerCallback callback : userState.callbackSet) {
                try {
                    callback.onInputStateChanged(inputId, state);
                } catch (RemoteException e) {
                    Slog.e(TAG, "failed to report state change to callback", e);
                }
            }
            return;
        }
        try {
            targetCallback.onInputStateChanged(inputId, state);
        } catch (RemoteException e2) {
            Slog.e(TAG, "failed to report state change to callback", e2);
        }
    }

    /* access modifiers changed from: private */
    public void updateTvInputInfoLocked(UserState userState, TvInputInfo inputInfo) {
        String inputId = inputInfo.getId();
        TvInputState inputState = (TvInputState) userState.inputMap.get(inputId);
        if (inputState == null) {
            Slog.e(TAG, "failed to set input info - unknown input id " + inputId);
            return;
        }
        TvInputInfo unused = inputState.info = inputInfo;
        for (ITvInputManagerCallback callback : userState.callbackSet) {
            try {
                callback.onTvInputInfoUpdated(inputInfo);
            } catch (RemoteException e) {
                Slog.e(TAG, "failed to report updated input info to callback", e);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setStateLocked(String inputId, int state, int userId) {
        UserState userState = getOrCreateUserStateLocked(userId);
        TvInputState inputState = (TvInputState) userState.inputMap.get(inputId);
        ServiceState serviceState = (ServiceState) userState.serviceStateMap.get(inputState.info.getComponent());
        int oldState = inputState.state;
        int unused = inputState.state = state;
        if ((serviceState == null || serviceState.service != null || (serviceState.sessionTokens.isEmpty() && !serviceState.isHardware)) && oldState != state) {
            notifyInputStateChangedLocked(userState, inputId, state, (ITvInputManagerCallback) null);
        }
    }

    private final class BinderService extends ITvInputManager.Stub {
        private BinderService() {
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        public List<TvInputInfo> getTvInputList(int userId) {
            List<TvInputInfo> inputList;
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), userId, "getTvInputList");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    UserState userState = TvInputManagerService.this.getOrCreateUserStateLocked(resolvedUserId);
                    inputList = new ArrayList<>();
                    for (TvInputState state : userState.inputMap.values()) {
                        inputList.add(state.info);
                    }
                }
                Binder.restoreCallingIdentity(identity);
                return inputList;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        public TvInputInfo getTvInputInfo(String inputId, int userId) {
            TvInputInfo access$1400;
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), userId, "getTvInputInfo");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    TvInputState state = (TvInputState) TvInputManagerService.this.getOrCreateUserStateLocked(resolvedUserId).inputMap.get(inputId);
                    access$1400 = state == null ? null : state.info;
                }
                Binder.restoreCallingIdentity(identity);
                return access$1400;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void updateTvInputInfo(TvInputInfo inputInfo, int userId) {
            String inputInfoPackageName = inputInfo.getServiceInfo().packageName;
            String callingPackageName = getCallingPackageName();
            if (TextUtils.equals(inputInfoPackageName, callingPackageName) || TvInputManagerService.this.mContext.checkCallingPermission("android.permission.WRITE_SECURE_SETTINGS") == 0) {
                int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), userId, "updateTvInputInfo");
                long identity = Binder.clearCallingIdentity();
                try {
                    synchronized (TvInputManagerService.this.mLock) {
                        TvInputManagerService.this.updateTvInputInfoLocked(TvInputManagerService.this.getOrCreateUserStateLocked(resolvedUserId), inputInfo);
                    }
                    Binder.restoreCallingIdentity(identity);
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(identity);
                    throw th;
                }
            } else {
                throw new IllegalArgumentException("calling package " + callingPackageName + " is not allowed to change TvInputInfo for " + inputInfoPackageName);
            }
        }

        private String getCallingPackageName() {
            String[] packages = TvInputManagerService.this.mContext.getPackageManager().getPackagesForUid(Binder.getCallingUid());
            if (packages == null || packages.length <= 0) {
                return UiModeManagerService.Shell.NIGHT_MODE_STR_UNKNOWN;
            }
            return packages[0];
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        public int getTvInputState(String inputId, int userId) {
            int access$4000;
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), userId, "getTvInputState");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    TvInputState state = (TvInputState) TvInputManagerService.this.getOrCreateUserStateLocked(resolvedUserId).inputMap.get(inputId);
                    access$4000 = state == null ? 0 : state.state;
                }
                Binder.restoreCallingIdentity(identity);
                return access$4000;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        public List<TvContentRatingSystemInfo> getTvContentRatingSystemList(int userId) {
            List<TvContentRatingSystemInfo> access$1500;
            if (TvInputManagerService.this.mContext.checkCallingPermission("android.permission.READ_CONTENT_RATING_SYSTEMS") == 0) {
                int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), userId, "getTvContentRatingSystemList");
                long identity = Binder.clearCallingIdentity();
                try {
                    synchronized (TvInputManagerService.this.mLock) {
                        access$1500 = TvInputManagerService.this.getOrCreateUserStateLocked(resolvedUserId).contentRatingSystemList;
                    }
                    Binder.restoreCallingIdentity(identity);
                    return access$1500;
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(identity);
                    throw th;
                }
            } else {
                throw new SecurityException("The caller does not have permission to read content rating systems");
            }
        }

        public void sendTvInputNotifyIntent(Intent intent, int userId) {
            if (TvInputManagerService.this.mContext.checkCallingPermission("android.permission.NOTIFY_TV_INPUTS") != 0) {
                throw new SecurityException("The caller: " + getCallingPackageName() + " doesn't have permission: " + "android.permission.NOTIFY_TV_INPUTS");
            } else if (!TextUtils.isEmpty(intent.getPackage())) {
                String action = intent.getAction();
                char c = 65535;
                int hashCode = action.hashCode();
                if (hashCode != -160295064) {
                    if (hashCode != 1568780589) {
                        if (hashCode == 2011523553 && action.equals("android.media.tv.action.PREVIEW_PROGRAM_ADDED_TO_WATCH_NEXT")) {
                            c = 2;
                        }
                    } else if (action.equals("android.media.tv.action.PREVIEW_PROGRAM_BROWSABLE_DISABLED")) {
                        c = 0;
                    }
                } else if (action.equals("android.media.tv.action.WATCH_NEXT_PROGRAM_BROWSABLE_DISABLED")) {
                    c = 1;
                }
                if (c != 0) {
                    if (c != 1) {
                        if (c != 2) {
                            throw new IllegalArgumentException("Invalid TV input notifying action: " + intent.getAction());
                        } else if (intent.getLongExtra("android.media.tv.extra.PREVIEW_PROGRAM_ID", -1) < 0) {
                            throw new IllegalArgumentException("Invalid preview program ID.");
                        } else if (intent.getLongExtra("android.media.tv.extra.WATCH_NEXT_PROGRAM_ID", -1) < 0) {
                            throw new IllegalArgumentException("Invalid watch next program ID.");
                        }
                    } else if (intent.getLongExtra("android.media.tv.extra.WATCH_NEXT_PROGRAM_ID", -1) < 0) {
                        throw new IllegalArgumentException("Invalid watch next program ID.");
                    }
                } else if (intent.getLongExtra("android.media.tv.extra.PREVIEW_PROGRAM_ID", -1) < 0) {
                    throw new IllegalArgumentException("Invalid preview program ID.");
                }
                int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), userId, "sendTvInputNotifyIntent");
                long identity = Binder.clearCallingIdentity();
                try {
                    TvInputManagerService.this.getContext().sendBroadcastAsUser(intent, new UserHandle(resolvedUserId));
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            } else {
                throw new IllegalArgumentException("Must specify package name to notify.");
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void registerCallback(final ITvInputManagerCallback callback, int userId) {
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), userId, "registerCallback");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    final UserState userState = TvInputManagerService.this.getOrCreateUserStateLocked(resolvedUserId);
                    userState.callbackSet.add(callback);
                    IBinder.DeathRecipient unused = TvInputManagerService.this.mDeathRecipient = new IBinder.DeathRecipient() {
                        public void binderDied() {
                            synchronized (TvInputManagerService.this.mLock) {
                                if (userState.callbackSet != null) {
                                    userState.callbackSet.remove(callback);
                                }
                            }
                        }
                    };
                    try {
                        callback.asBinder().linkToDeath(TvInputManagerService.this.mDeathRecipient, 0);
                    } catch (RemoteException e) {
                        Slog.e(TvInputManagerService.TAG, "client process has already died", e);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void unregisterCallback(ITvInputManagerCallback callback, int userId) {
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), userId, "unregisterCallback");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    TvInputManagerService.this.getOrCreateUserStateLocked(resolvedUserId).callbackSet.remove(callback);
                    callback.asBinder().unlinkToDeath(TvInputManagerService.this.mDeathRecipient, 0);
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        public boolean isParentalControlsEnabled(int userId) {
            boolean isParentalControlsEnabled;
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), userId, "isParentalControlsEnabled");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    isParentalControlsEnabled = TvInputManagerService.this.getOrCreateUserStateLocked(resolvedUserId).persistentDataStore.isParentalControlsEnabled();
                }
                Binder.restoreCallingIdentity(identity);
                return isParentalControlsEnabled;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        public void setParentalControlsEnabled(boolean enabled, int userId) {
            ensureParentalControlsPermission();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), userId, "setParentalControlsEnabled");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    TvInputManagerService.this.getOrCreateUserStateLocked(resolvedUserId).persistentDataStore.setParentalControlsEnabled(enabled);
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        public boolean isRatingBlocked(String rating, int userId) {
            boolean isRatingBlocked;
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), userId, "isRatingBlocked");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    isRatingBlocked = TvInputManagerService.this.getOrCreateUserStateLocked(resolvedUserId).persistentDataStore.isRatingBlocked(TvContentRating.unflattenFromString(rating));
                }
                Binder.restoreCallingIdentity(identity);
                return isRatingBlocked;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 11 */
        public List<String> getBlockedRatings(int userId) {
            List<String> ratings;
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), userId, "getBlockedRatings");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    UserState userState = TvInputManagerService.this.getOrCreateUserStateLocked(resolvedUserId);
                    ratings = new ArrayList<>();
                    for (TvContentRating rating : userState.persistentDataStore.getBlockedRatings()) {
                        ratings.add(rating.flattenToString());
                    }
                }
                Binder.restoreCallingIdentity(identity);
                return ratings;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        public void addBlockedRating(String rating, int userId) {
            ensureParentalControlsPermission();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), userId, "addBlockedRating");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    TvInputManagerService.this.getOrCreateUserStateLocked(resolvedUserId).persistentDataStore.addBlockedRating(TvContentRating.unflattenFromString(rating));
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        public void removeBlockedRating(String rating, int userId) {
            ensureParentalControlsPermission();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), userId, "removeBlockedRating");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    TvInputManagerService.this.getOrCreateUserStateLocked(resolvedUserId).persistentDataStore.removeBlockedRating(TvContentRating.unflattenFromString(rating));
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        private void ensureParentalControlsPermission() {
            if (TvInputManagerService.this.mContext.checkCallingPermission("android.permission.MODIFY_PARENTAL_CONTROLS") != 0) {
                throw new SecurityException("The caller does not have parental controls permission");
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 27 */
        /* JADX WARNING: Code restructure failed: missing block: B:51:0x011e, code lost:
            android.os.Binder.restoreCallingIdentity(r21);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:52:0x0122, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void createSession(android.media.tv.ITvInputClient r28, java.lang.String r29, boolean r30, int r31, int r32) {
            /*
                r27 = this;
                r1 = r27
                r8 = r29
                r9 = r32
                int r15 = android.os.Binder.getCallingUid()
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this
                int r2 = android.os.Binder.getCallingPid()
                java.lang.String r3 = "createSession"
                int r14 = r0.resolveCallingUserId(r2, r15, r9, r3)
                long r21 = android.os.Binder.clearCallingIdentity()
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x012c }
                java.lang.Object r23 = r0.mLock     // Catch:{ all -> 0x012c }
                monitor-enter(r23)     // Catch:{ all -> 0x012c }
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x0123 }
                int r0 = r0.mCurrentUserId     // Catch:{ all -> 0x0123 }
                if (r9 == r0) goto L_0x0042
                if (r30 != 0) goto L_0x0042
                com.android.server.tv.TvInputManagerService r2 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x003d }
                r5 = 0
                r6 = 0
                r3 = r28
                r4 = r29
                r7 = r31
                r2.sendSessionTokenToClientLocked(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x003d }
                monitor-exit(r23)     // Catch:{ all -> 0x003d }
                android.os.Binder.restoreCallingIdentity(r21)
                return
            L_0x003d:
                r0 = move-exception
                r5 = r14
                r4 = r15
                goto L_0x0126
            L_0x0042:
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x0123 }
                com.android.server.tv.TvInputManagerService$UserState r0 = r0.getOrCreateUserStateLocked(r14)     // Catch:{ all -> 0x0123 }
                java.util.Map r2 = r0.inputMap     // Catch:{ all -> 0x0123 }
                java.lang.Object r2 = r2.get(r8)     // Catch:{ all -> 0x0123 }
                com.android.server.tv.TvInputManagerService$TvInputState r2 = (com.android.server.tv.TvInputManagerService.TvInputState) r2     // Catch:{ all -> 0x0123 }
                r24 = r2
                if (r24 != 0) goto L_0x007e
                java.lang.String r2 = "TvInputManagerService"
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x003d }
                r3.<init>()     // Catch:{ all -> 0x003d }
                java.lang.String r4 = "Failed to find input state for inputId="
                r3.append(r4)     // Catch:{ all -> 0x003d }
                r3.append(r8)     // Catch:{ all -> 0x003d }
                java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x003d }
                android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x003d }
                com.android.server.tv.TvInputManagerService r2 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x003d }
                r5 = 0
                r6 = 0
                r3 = r28
                r4 = r29
                r7 = r31
                r2.sendSessionTokenToClientLocked(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x003d }
                monitor-exit(r23)     // Catch:{ all -> 0x003d }
                android.os.Binder.restoreCallingIdentity(r21)
                return
            L_0x007e:
                android.media.tv.TvInputInfo r2 = r24.info     // Catch:{ all -> 0x0123 }
                r25 = r2
                java.util.Map r2 = r0.serviceStateMap     // Catch:{ all -> 0x0123 }
                android.content.ComponentName r3 = r25.getComponent()     // Catch:{ all -> 0x0123 }
                java.lang.Object r2 = r2.get(r3)     // Catch:{ all -> 0x0123 }
                com.android.server.tv.TvInputManagerService$ServiceState r2 = (com.android.server.tv.TvInputManagerService.ServiceState) r2     // Catch:{ all -> 0x0123 }
                if (r2 != 0) goto L_0x00af
                com.android.server.tv.TvInputManagerService$ServiceState r3 = new com.android.server.tv.TvInputManagerService$ServiceState     // Catch:{ all -> 0x003d }
                com.android.server.tv.TvInputManagerService r4 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x003d }
                android.content.ComponentName r5 = r25.getComponent()     // Catch:{ all -> 0x003d }
                r6 = 0
                r3.<init>(r5, r14)     // Catch:{ all -> 0x003d }
                r2 = r3
                java.util.Map r3 = r0.serviceStateMap     // Catch:{ all -> 0x003d }
                android.content.ComponentName r4 = r25.getComponent()     // Catch:{ all -> 0x003d }
                r3.put(r4, r2)     // Catch:{ all -> 0x003d }
                r26 = r2
                goto L_0x00b1
            L_0x00af:
                r26 = r2
            L_0x00b1:
                boolean r2 = r26.reconnecting     // Catch:{ all -> 0x0123 }
                if (r2 == 0) goto L_0x00c9
                com.android.server.tv.TvInputManagerService r2 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x003d }
                r5 = 0
                r6 = 0
                r3 = r28
                r4 = r29
                r7 = r31
                r2.sendSessionTokenToClientLocked(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x003d }
                monitor-exit(r23)     // Catch:{ all -> 0x003d }
                android.os.Binder.restoreCallingIdentity(r21)
                return
            L_0x00c9:
                android.os.Binder r2 = new android.os.Binder     // Catch:{ all -> 0x0123 }
                r2.<init>()     // Catch:{ all -> 0x0123 }
                com.android.server.tv.TvInputManagerService$SessionState r3 = new com.android.server.tv.TvInputManagerService$SessionState     // Catch:{ all -> 0x0123 }
                com.android.server.tv.TvInputManagerService r11 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x0123 }
                java.lang.String r13 = r25.getId()     // Catch:{ all -> 0x0123 }
                android.content.ComponentName r4 = r25.getComponent()     // Catch:{ all -> 0x0123 }
                r20 = 0
                r10 = r3
                r12 = r2
                r5 = r14
                r14 = r4
                r4 = r15
                r15 = r30
                r16 = r28
                r17 = r31
                r18 = r4
                r19 = r5
                r10.<init>(r12, r13, r14, r15, r16, r17, r18, r19)     // Catch:{ all -> 0x012a }
                java.util.Map r6 = r0.sessionStateMap     // Catch:{ all -> 0x012a }
                r6.put(r2, r3)     // Catch:{ all -> 0x012a }
                java.util.List r6 = r26.sessionTokens     // Catch:{ all -> 0x012a }
                r6.add(r2)     // Catch:{ all -> 0x012a }
                android.media.tv.ITvInputService r6 = r26.service     // Catch:{ all -> 0x012a }
                if (r6 == 0) goto L_0x0114
                com.android.server.tv.TvInputManagerService r6 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x012a }
                android.media.tv.ITvInputService r7 = r26.service     // Catch:{ all -> 0x012a }
                boolean r6 = r6.createSessionInternalLocked(r7, r2, r5)     // Catch:{ all -> 0x012a }
                if (r6 != 0) goto L_0x011d
                com.android.server.tv.TvInputManagerService r6 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x012a }
                r6.removeSessionStateLocked(r2, r5)     // Catch:{ all -> 0x012a }
                goto L_0x011d
            L_0x0114:
                com.android.server.tv.TvInputManagerService r6 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x012a }
                android.content.ComponentName r7 = r25.getComponent()     // Catch:{ all -> 0x012a }
                r6.updateServiceConnectionLocked(r7, r5)     // Catch:{ all -> 0x012a }
            L_0x011d:
                monitor-exit(r23)     // Catch:{ all -> 0x012a }
                android.os.Binder.restoreCallingIdentity(r21)
                return
            L_0x0123:
                r0 = move-exception
                r5 = r14
                r4 = r15
            L_0x0126:
                monitor-exit(r23)     // Catch:{ all -> 0x012a }
                throw r0     // Catch:{ all -> 0x0128 }
            L_0x0128:
                r0 = move-exception
                goto L_0x012f
            L_0x012a:
                r0 = move-exception
                goto L_0x0126
            L_0x012c:
                r0 = move-exception
                r5 = r14
                r4 = r15
            L_0x012f:
                android.os.Binder.restoreCallingIdentity(r21)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.BinderService.createSession(android.media.tv.ITvInputClient, java.lang.String, boolean, int, int):void");
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        public void releaseSession(IBinder sessionToken, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "releaseSession");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    TvInputManagerService.this.releaseSessionLocked(sessionToken, callingUid, resolvedUserId);
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 10 */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0055, code lost:
            android.os.Binder.restoreCallingIdentity(r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0059, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void setMainSession(android.os.IBinder r11, int r12) {
            /*
                r10 = this;
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this
                android.content.Context r0 = r0.mContext
                java.lang.String r1 = "android.permission.CHANGE_HDMI_CEC_ACTIVE_SOURCE"
                int r0 = r0.checkCallingPermission(r1)
                if (r0 != 0) goto L_0x0062
                int r0 = android.os.Binder.getCallingUid()
                com.android.server.tv.TvInputManagerService r1 = com.android.server.tv.TvInputManagerService.this
                int r2 = android.os.Binder.getCallingPid()
                java.lang.String r3 = "setMainSession"
                int r1 = r1.resolveCallingUserId(r2, r0, r12, r3)
                long r2 = android.os.Binder.clearCallingIdentity()
                com.android.server.tv.TvInputManagerService r4 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x005d }
                java.lang.Object r4 = r4.mLock     // Catch:{ all -> 0x005d }
                monitor-enter(r4)     // Catch:{ all -> 0x005d }
                com.android.server.tv.TvInputManagerService r5 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x005a }
                com.android.server.tv.TvInputManagerService$UserState r5 = r5.getOrCreateUserStateLocked(r1)     // Catch:{ all -> 0x005a }
                android.os.IBinder r6 = r5.mainSessionToken     // Catch:{ all -> 0x005a }
                if (r6 != r11) goto L_0x003b
                monitor-exit(r4)     // Catch:{ all -> 0x005a }
                android.os.Binder.restoreCallingIdentity(r2)
                return
            L_0x003b:
                android.os.IBinder r6 = r5.mainSessionToken     // Catch:{ all -> 0x005a }
                android.os.IBinder unused = r5.mainSessionToken = r11     // Catch:{ all -> 0x005a }
                if (r11 == 0) goto L_0x004a
                com.android.server.tv.TvInputManagerService r7 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x005a }
                r8 = 1
                r7.setMainLocked(r11, r8, r0, r12)     // Catch:{ all -> 0x005a }
            L_0x004a:
                if (r6 == 0) goto L_0x0054
                com.android.server.tv.TvInputManagerService r7 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x005a }
                r8 = 0
                r9 = 1000(0x3e8, float:1.401E-42)
                r7.setMainLocked(r6, r8, r9, r12)     // Catch:{ all -> 0x005a }
            L_0x0054:
                monitor-exit(r4)     // Catch:{ all -> 0x005a }
                android.os.Binder.restoreCallingIdentity(r2)
                return
            L_0x005a:
                r5 = move-exception
                monitor-exit(r4)     // Catch:{ all -> 0x005a }
                throw r5     // Catch:{ all -> 0x005d }
            L_0x005d:
                r4 = move-exception
                android.os.Binder.restoreCallingIdentity(r2)
                throw r4
            L_0x0062:
                java.lang.SecurityException r0 = new java.lang.SecurityException
                java.lang.String r1 = "The caller does not have CHANGE_HDMI_CEC_ACTIVE_SOURCE permission"
                r0.<init>(r1)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.BinderService.setMainSession(android.os.IBinder, int):void");
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        public void setSurface(IBinder sessionToken, Surface surface, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "setSurface");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        SessionState sessionState = TvInputManagerService.this.getSessionStateLocked(sessionToken, callingUid, resolvedUserId);
                        if (sessionState.hardwareSessionToken == null) {
                            TvInputManagerService.this.getSessionLocked(sessionState).setSurface(surface);
                        } else {
                            TvInputManagerService.this.getSessionLocked(sessionState.hardwareSessionToken, 1000, resolvedUserId).setSurface(surface);
                        }
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in setSurface", e);
                    }
                }
                if (surface != null) {
                    surface.release();
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                if (surface != null) {
                    surface.release();
                }
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        public void dispatchSurfaceChanged(IBinder sessionToken, int format, int width, int height, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "dispatchSurfaceChanged");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        SessionState sessionState = TvInputManagerService.this.getSessionStateLocked(sessionToken, callingUid, resolvedUserId);
                        TvInputManagerService.this.getSessionLocked(sessionState).dispatchSurfaceChanged(format, width, height);
                        if (sessionState.hardwareSessionToken != null) {
                            TvInputManagerService.this.getSessionLocked(sessionState.hardwareSessionToken, 1000, resolvedUserId).dispatchSurfaceChanged(format, width, height);
                        }
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in dispatchSurfaceChanged", e);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 11 */
        public void setVolume(IBinder sessionToken, float volume, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "setVolume");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        SessionState sessionState = TvInputManagerService.this.getSessionStateLocked(sessionToken, callingUid, resolvedUserId);
                        TvInputManagerService.this.getSessionLocked(sessionState).setVolume(volume);
                        if (sessionState.hardwareSessionToken != null) {
                            ITvInputSession access$5600 = TvInputManagerService.this.getSessionLocked(sessionState.hardwareSessionToken, 1000, resolvedUserId);
                            float f = 0.0f;
                            if (volume > 0.0f) {
                                f = 1.0f;
                            }
                            access$5600.setVolume(f);
                        }
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in setVolume", e);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 10 */
        public void tune(IBinder sessionToken, Uri channelUri, Bundle params, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "tune");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        TvInputManagerService.this.getSessionLocked(sessionToken, callingUid, resolvedUserId).tune(channelUri, params);
                        if (TvContract.isChannelUriForPassthroughInput(channelUri)) {
                            Binder.restoreCallingIdentity(identity);
                            return;
                        }
                        SessionState sessionState = (SessionState) TvInputManagerService.this.getOrCreateUserStateLocked(resolvedUserId).sessionStateMap.get(sessionToken);
                        if (sessionState.isRecordingSession) {
                            Binder.restoreCallingIdentity(identity);
                            return;
                        }
                        SomeArgs args = SomeArgs.obtain();
                        args.arg1 = sessionState.componentName.getPackageName();
                        args.arg2 = Long.valueOf(System.currentTimeMillis());
                        args.arg3 = Long.valueOf(ContentUris.parseId(channelUri));
                        args.arg4 = params;
                        args.arg5 = sessionToken;
                        TvInputManagerService.this.mWatchLogHandler.obtainMessage(1, args).sendToTarget();
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in tune", e);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void unblockContent(IBinder sessionToken, String unblockedRating, int userId) {
            ensureParentalControlsPermission();
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "unblockContent");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        TvInputManagerService.this.getSessionLocked(sessionToken, callingUid, resolvedUserId).unblockContent(unblockedRating);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in unblockContent", e);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void setCaptionEnabled(IBinder sessionToken, boolean enabled, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "setCaptionEnabled");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        TvInputManagerService.this.getSessionLocked(sessionToken, callingUid, resolvedUserId).setCaptionEnabled(enabled);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in setCaptionEnabled", e);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void selectTrack(IBinder sessionToken, int type, String trackId, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "selectTrack");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        TvInputManagerService.this.getSessionLocked(sessionToken, callingUid, resolvedUserId).selectTrack(type, trackId);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in selectTrack", e);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void sendAppPrivateCommand(IBinder sessionToken, String command, Bundle data, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "sendAppPrivateCommand");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        TvInputManagerService.this.getSessionLocked(sessionToken, callingUid, resolvedUserId).appPrivateCommand(command, data);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in appPrivateCommand", e);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void createOverlayView(IBinder sessionToken, IBinder windowToken, Rect frame, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "createOverlayView");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        TvInputManagerService.this.getSessionLocked(sessionToken, callingUid, resolvedUserId).createOverlayView(windowToken, frame);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in createOverlayView", e);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void relayoutOverlayView(IBinder sessionToken, Rect frame, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "relayoutOverlayView");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        TvInputManagerService.this.getSessionLocked(sessionToken, callingUid, resolvedUserId).relayoutOverlayView(frame);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in relayoutOverlayView", e);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void removeOverlayView(IBinder sessionToken, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "removeOverlayView");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        TvInputManagerService.this.getSessionLocked(sessionToken, callingUid, resolvedUserId).removeOverlayView();
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in removeOverlayView", e);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void timeShiftPlay(IBinder sessionToken, Uri recordedProgramUri, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "timeShiftPlay");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        TvInputManagerService.this.getSessionLocked(sessionToken, callingUid, resolvedUserId).timeShiftPlay(recordedProgramUri);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in timeShiftPlay", e);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void timeShiftPause(IBinder sessionToken, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "timeShiftPause");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        TvInputManagerService.this.getSessionLocked(sessionToken, callingUid, resolvedUserId).timeShiftPause();
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in timeShiftPause", e);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void timeShiftResume(IBinder sessionToken, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "timeShiftResume");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        TvInputManagerService.this.getSessionLocked(sessionToken, callingUid, resolvedUserId).timeShiftResume();
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in timeShiftResume", e);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void timeShiftSeekTo(IBinder sessionToken, long timeMs, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "timeShiftSeekTo");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        TvInputManagerService.this.getSessionLocked(sessionToken, callingUid, resolvedUserId).timeShiftSeekTo(timeMs);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in timeShiftSeekTo", e);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void timeShiftSetPlaybackParams(IBinder sessionToken, PlaybackParams params, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "timeShiftSetPlaybackParams");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        TvInputManagerService.this.getSessionLocked(sessionToken, callingUid, resolvedUserId).timeShiftSetPlaybackParams(params);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in timeShiftSetPlaybackParams", e);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void timeShiftEnablePositionTracking(IBinder sessionToken, boolean enable, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "timeShiftEnablePositionTracking");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        TvInputManagerService.this.getSessionLocked(sessionToken, callingUid, resolvedUserId).timeShiftEnablePositionTracking(enable);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in timeShiftEnablePositionTracking", e);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void startRecording(IBinder sessionToken, Uri programUri, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "startRecording");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        TvInputManagerService.this.getSessionLocked(sessionToken, callingUid, resolvedUserId).startRecording(programUri);
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in startRecording", e);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void stopRecording(IBinder sessionToken, int userId) {
            int callingUid = Binder.getCallingUid();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "stopRecording");
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (TvInputManagerService.this.mLock) {
                    try {
                        TvInputManagerService.this.getSessionLocked(sessionToken, callingUid, resolvedUserId).stopRecording();
                    } catch (RemoteException | SessionNotFoundException e) {
                        Slog.e(TvInputManagerService.TAG, "error in stopRecording", e);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        public List<TvInputHardwareInfo> getHardwareList() throws RemoteException {
            if (TvInputManagerService.this.mContext.checkCallingPermission("android.permission.TV_INPUT_HARDWARE") != 0) {
                return null;
            }
            long identity = Binder.clearCallingIdentity();
            try {
                return TvInputManagerService.this.mTvInputHardwareManager.getHardwareList();
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        public ITvInputHardware acquireTvInputHardware(int deviceId, ITvInputHardwareCallback callback, TvInputInfo info, int userId) throws RemoteException {
            if (TvInputManagerService.this.mContext.checkCallingPermission("android.permission.TV_INPUT_HARDWARE") != 0) {
                return null;
            }
            long identity = Binder.clearCallingIdentity();
            int callingUid = Binder.getCallingUid();
            try {
                return TvInputManagerService.this.mTvInputHardwareManager.acquireHardware(deviceId, callback, info, callingUid, TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "acquireTvInputHardware"));
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        public void releaseTvInputHardware(int deviceId, ITvInputHardware hardware, int userId) throws RemoteException {
            if (TvInputManagerService.this.mContext.checkCallingPermission("android.permission.TV_INPUT_HARDWARE") == 0) {
                long identity = Binder.clearCallingIdentity();
                int callingUid = Binder.getCallingUid();
                try {
                    TvInputManagerService.this.mTvInputHardwareManager.releaseHardware(deviceId, hardware, callingUid, TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "releaseTvInputHardware"));
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }
        }

        public List<DvbDeviceInfo> getDvbDeviceList() throws RemoteException {
            int i;
            List<T> list;
            File dvbDirectory;
            File devDirectory;
            if (TvInputManagerService.this.mContext.checkCallingPermission("android.permission.DVB_DEVICE") == 0) {
                long identity = Binder.clearCallingIdentity();
                try {
                    ArrayList<DvbDeviceInfo> deviceInfosFromPattern1 = new ArrayList<>();
                    File devDirectory2 = new File("/dev");
                    String[] list2 = devDirectory2.list();
                    int length = list2.length;
                    boolean dvbDirectoryFound = false;
                    int i2 = 0;
                    while (true) {
                        i = 1;
                        if (i2 >= length) {
                            break;
                        }
                        String fileName = list2[i2];
                        Matcher matcher = TvInputManagerService.sFrontEndDevicePattern.matcher(fileName);
                        if (matcher.find()) {
                            deviceInfosFromPattern1.add(new DvbDeviceInfo(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2))));
                        }
                        if (TextUtils.equals("dvb", fileName)) {
                            dvbDirectoryFound = true;
                        }
                        i2++;
                    }
                    if (!dvbDirectoryFound) {
                        return Collections.unmodifiableList(deviceInfosFromPattern1);
                    }
                    File dvbDirectory2 = new File(TvInputManagerService.DVB_DIRECTORY);
                    ArrayList<DvbDeviceInfo> deviceInfosFromPattern2 = new ArrayList<>();
                    String[] list3 = dvbDirectory2.list();
                    int length2 = list3.length;
                    int i3 = 0;
                    while (i3 < length2) {
                        String fileNameInDvb = list3[i3];
                        Matcher adapterMatcher = TvInputManagerService.sAdapterDirPattern.matcher(fileNameInDvb);
                        if (adapterMatcher.find()) {
                            int adapterId = Integer.parseInt(adapterMatcher.group(i));
                            File adapterDirectory = new File("/dev/dvb/" + fileNameInDvb);
                            String[] list4 = adapterDirectory.list();
                            int length3 = list4.length;
                            File file = adapterDirectory;
                            int i4 = 0;
                            while (i4 < length3) {
                                File devDirectory3 = devDirectory2;
                                File dvbDirectory3 = dvbDirectory2;
                                String fileNameInAdapter = list4[i4];
                                Matcher frontendMatcher = TvInputManagerService.sFrontEndInAdapterDirPattern.matcher(fileNameInAdapter);
                                if (frontendMatcher.find()) {
                                    String str = fileNameInAdapter;
                                    Matcher matcher2 = frontendMatcher;
                                    deviceInfosFromPattern2.add(new DvbDeviceInfo(adapterId, Integer.parseInt(frontendMatcher.group(1))));
                                } else {
                                    String str2 = fileNameInAdapter;
                                }
                                i4++;
                                devDirectory2 = devDirectory3;
                                dvbDirectory2 = dvbDirectory3;
                            }
                            devDirectory = devDirectory2;
                            dvbDirectory = dvbDirectory2;
                        } else {
                            devDirectory = devDirectory2;
                            dvbDirectory = dvbDirectory2;
                        }
                        i3++;
                        devDirectory2 = devDirectory;
                        dvbDirectory2 = dvbDirectory;
                        i = 1;
                    }
                    File file2 = dvbDirectory2;
                    if (deviceInfosFromPattern2.isEmpty()) {
                        list = Collections.unmodifiableList(deviceInfosFromPattern1);
                    } else {
                        list = Collections.unmodifiableList(deviceInfosFromPattern2);
                    }
                    Binder.restoreCallingIdentity(identity);
                    return list;
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            } else {
                throw new SecurityException("Requires DVB_DEVICE permission");
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 20 */
        public ParcelFileDescriptor openDvbDevice(DvbDeviceInfo info, int device) throws RemoteException {
            String deviceFileName;
            int i;
            String[] strArr;
            File devDirectory;
            int i2 = device;
            if (TvInputManagerService.this.mContext.checkCallingPermission("android.permission.DVB_DEVICE") == 0) {
                File devDirectory2 = new File("/dev");
                String[] list = devDirectory2.list();
                int length = list.length;
                boolean dvbDeviceFound = false;
                int i3 = 0;
                while (true) {
                    if (i3 >= length) {
                        break;
                    }
                    if (TextUtils.equals("dvb", list[i3])) {
                        String[] list2 = new File(TvInputManagerService.DVB_DIRECTORY).list();
                        int length2 = list2.length;
                        boolean dvbDeviceFound2 = dvbDeviceFound;
                        int i4 = 0;
                        while (true) {
                            if (i4 >= length2) {
                                devDirectory = devDirectory2;
                                strArr = list;
                                dvbDeviceFound = dvbDeviceFound2;
                                break;
                            }
                            String fileNameInDvb = list2[i4];
                            if (TvInputManagerService.sAdapterDirPattern.matcher(fileNameInDvb).find()) {
                                File adapterDirectory = new File("/dev/dvb/" + fileNameInDvb);
                                String[] list3 = adapterDirectory.list();
                                int length3 = list3.length;
                                File file = adapterDirectory;
                                int i5 = 0;
                                while (true) {
                                    if (i5 >= length3) {
                                        devDirectory = devDirectory2;
                                        strArr = list;
                                        break;
                                    }
                                    devDirectory = devDirectory2;
                                    strArr = list;
                                    if (TvInputManagerService.sFrontEndInAdapterDirPattern.matcher(list3[i5]).find()) {
                                        dvbDeviceFound2 = true;
                                        break;
                                    }
                                    i5++;
                                    devDirectory2 = devDirectory;
                                    list = strArr;
                                }
                            } else {
                                devDirectory = devDirectory2;
                                strArr = list;
                            }
                            if (dvbDeviceFound2) {
                                dvbDeviceFound = dvbDeviceFound2;
                                break;
                            }
                            i4++;
                            devDirectory2 = devDirectory;
                            list = strArr;
                        }
                    } else {
                        devDirectory = devDirectory2;
                        strArr = list;
                    }
                    if (dvbDeviceFound) {
                        break;
                    }
                    i3++;
                    devDirectory2 = devDirectory;
                    list = strArr;
                }
                long identity = Binder.clearCallingIdentity();
                if (i2 == 0) {
                    deviceFileName = String.format(dvbDeviceFound ? "/dev/dvb/adapter%d/demux%d" : "/dev/dvb%d.demux%d", new Object[]{Integer.valueOf(info.getAdapterId()), Integer.valueOf(info.getDeviceId())});
                } else if (i2 == 1) {
                    deviceFileName = String.format(dvbDeviceFound ? "/dev/dvb/adapter%d/dvr%d" : "/dev/dvb%d.dvr%d", new Object[]{Integer.valueOf(info.getAdapterId()), Integer.valueOf(info.getDeviceId())});
                } else if (i2 == 2) {
                    try {
                        deviceFileName = String.format(dvbDeviceFound ? "/dev/dvb/adapter%d/frontend%d" : "/dev/dvb%d.frontend%d", new Object[]{Integer.valueOf(info.getAdapterId()), Integer.valueOf(info.getDeviceId())});
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(identity);
                        throw th;
                    }
                } else {
                    throw new IllegalArgumentException("Invalid DVB device: " + i2);
                }
                try {
                    File file2 = new File(deviceFileName);
                    if (2 == i2) {
                        i = 805306368;
                    } else {
                        i = 268435456;
                    }
                    ParcelFileDescriptor open = ParcelFileDescriptor.open(file2, i);
                    Binder.restoreCallingIdentity(identity);
                    return open;
                } catch (FileNotFoundException e) {
                    Binder.restoreCallingIdentity(identity);
                    return null;
                }
            } else {
                throw new SecurityException("Requires DVB_DEVICE permission");
            }
        }

        public List<TvStreamConfig> getAvailableTvStreamConfigList(String inputId, int userId) throws RemoteException {
            ensureCaptureTvInputPermission();
            long identity = Binder.clearCallingIdentity();
            int callingUid = Binder.getCallingUid();
            try {
                return TvInputManagerService.this.mTvInputHardwareManager.getAvailableTvStreamConfigList(inputId, callingUid, TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), callingUid, userId, "getAvailableTvStreamConfigList"));
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 11 */
        /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
            r2 = com.android.server.tv.TvInputManagerService.access$5800(r11.this$0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x0092, code lost:
            if (r10 == null) goto L_0x0096;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x0094, code lost:
            r3 = r10;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0096, code lost:
            r3 = r12;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x0097, code lost:
            r2 = r2.captureFrame(r3, r13, r14, r8, r9);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x009f, code lost:
            android.os.Binder.restoreCallingIdentity(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x00a2, code lost:
            return r2;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean captureFrame(java.lang.String r12, android.view.Surface r13, android.media.tv.TvStreamConfig r14, int r15) throws android.os.RemoteException {
            /*
                r11 = this;
                r11.ensureCaptureTvInputPermission()
                long r0 = android.os.Binder.clearCallingIdentity()
                int r8 = android.os.Binder.getCallingUid()
                com.android.server.tv.TvInputManagerService r2 = com.android.server.tv.TvInputManagerService.this
                int r3 = android.os.Binder.getCallingPid()
                java.lang.String r4 = "captureFrame"
                int r9 = r2.resolveCallingUserId(r3, r8, r15, r4)
                r2 = 0
                com.android.server.tv.TvInputManagerService r3 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x00a9 }
                java.lang.Object r3 = r3.mLock     // Catch:{ all -> 0x00a9 }
                monitor-enter(r3)     // Catch:{ all -> 0x00a9 }
                com.android.server.tv.TvInputManagerService r4 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x00a6 }
                com.android.server.tv.TvInputManagerService$UserState r4 = r4.getOrCreateUserStateLocked(r9)     // Catch:{ all -> 0x00a6 }
                java.util.Map r5 = r4.inputMap     // Catch:{ all -> 0x00a6 }
                java.lang.Object r5 = r5.get(r12)     // Catch:{ all -> 0x00a6 }
                if (r5 != 0) goto L_0x004c
                java.lang.String r5 = "TvInputManagerService"
                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a6 }
                r6.<init>()     // Catch:{ all -> 0x00a6 }
                java.lang.String r7 = "input not found for "
                r6.append(r7)     // Catch:{ all -> 0x00a6 }
                r6.append(r12)     // Catch:{ all -> 0x00a6 }
                java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00a6 }
                android.util.Slog.e(r5, r6)     // Catch:{ all -> 0x00a6 }
                r5 = 0
                monitor-exit(r3)     // Catch:{ all -> 0x00a6 }
                android.os.Binder.restoreCallingIdentity(r0)
                return r5
            L_0x004c:
                java.util.Map r5 = r4.sessionStateMap     // Catch:{ all -> 0x00a6 }
                java.util.Collection r5 = r5.values()     // Catch:{ all -> 0x00a6 }
                java.util.Iterator r5 = r5.iterator()     // Catch:{ all -> 0x00a6 }
            L_0x0058:
                boolean r6 = r5.hasNext()     // Catch:{ all -> 0x00a6 }
                if (r6 == 0) goto L_0x008a
                java.lang.Object r6 = r5.next()     // Catch:{ all -> 0x00a6 }
                com.android.server.tv.TvInputManagerService$SessionState r6 = (com.android.server.tv.TvInputManagerService.SessionState) r6     // Catch:{ all -> 0x00a6 }
                java.lang.String r7 = r6.inputId     // Catch:{ all -> 0x00a6 }
                boolean r7 = r7.equals(r12)     // Catch:{ all -> 0x00a6 }
                if (r7 == 0) goto L_0x0089
                android.os.IBinder r7 = r6.hardwareSessionToken     // Catch:{ all -> 0x00a6 }
                if (r7 == 0) goto L_0x0089
                java.util.Map r5 = r4.sessionStateMap     // Catch:{ all -> 0x00a6 }
                android.os.IBinder r7 = r6.hardwareSessionToken     // Catch:{ all -> 0x00a6 }
                java.lang.Object r5 = r5.get(r7)     // Catch:{ all -> 0x00a6 }
                com.android.server.tv.TvInputManagerService$SessionState r5 = (com.android.server.tv.TvInputManagerService.SessionState) r5     // Catch:{ all -> 0x00a6 }
                java.lang.String r5 = r5.inputId     // Catch:{ all -> 0x00a6 }
                r2 = r5
                r10 = r2
                goto L_0x008b
            L_0x0089:
                goto L_0x0058
            L_0x008a:
                r10 = r2
            L_0x008b:
                monitor-exit(r3)     // Catch:{ all -> 0x00a3 }
                com.android.server.tv.TvInputManagerService r2 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x00a9 }
                com.android.server.tv.TvInputHardwareManager r2 = r2.mTvInputHardwareManager     // Catch:{ all -> 0x00a9 }
                if (r10 == 0) goto L_0x0096
                r3 = r10
                goto L_0x0097
            L_0x0096:
                r3 = r12
            L_0x0097:
                r4 = r13
                r5 = r14
                r6 = r8
                r7 = r9
                boolean r2 = r2.captureFrame(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x00a9 }
                android.os.Binder.restoreCallingIdentity(r0)
                return r2
            L_0x00a3:
                r4 = move-exception
                r2 = r10
                goto L_0x00a7
            L_0x00a6:
                r4 = move-exception
            L_0x00a7:
                monitor-exit(r3)     // Catch:{ all -> 0x00a6 }
                throw r4     // Catch:{ all -> 0x00a9 }
            L_0x00a9:
                r2 = move-exception
                android.os.Binder.restoreCallingIdentity(r0)
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.BinderService.captureFrame(java.lang.String, android.view.Surface, android.media.tv.TvStreamConfig, int):boolean");
        }

        /* Debug info: failed to restart local var, previous not found, register: 10 */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0062, code lost:
            android.os.Binder.restoreCallingIdentity(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0065, code lost:
            return true;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean isSingleSessionActive(int r11) throws android.os.RemoteException {
            /*
                r10 = this;
                r10.ensureCaptureTvInputPermission()
                long r0 = android.os.Binder.clearCallingIdentity()
                int r2 = android.os.Binder.getCallingUid()
                com.android.server.tv.TvInputManagerService r3 = com.android.server.tv.TvInputManagerService.this
                int r4 = android.os.Binder.getCallingPid()
                java.lang.String r5 = "isSingleSessionActive"
                int r3 = r3.resolveCallingUserId(r4, r2, r11, r5)
                com.android.server.tv.TvInputManagerService r4 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x006e }
                java.lang.Object r4 = r4.mLock     // Catch:{ all -> 0x006e }
                monitor-enter(r4)     // Catch:{ all -> 0x006e }
                com.android.server.tv.TvInputManagerService r5 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x006b }
                com.android.server.tv.TvInputManagerService$UserState r5 = r5.getOrCreateUserStateLocked(r3)     // Catch:{ all -> 0x006b }
                java.util.Map r6 = r5.sessionStateMap     // Catch:{ all -> 0x006b }
                int r6 = r6.size()     // Catch:{ all -> 0x006b }
                r7 = 1
                if (r6 != r7) goto L_0x0035
                monitor-exit(r4)     // Catch:{ all -> 0x006b }
                android.os.Binder.restoreCallingIdentity(r0)
                return r7
            L_0x0035:
                java.util.Map r6 = r5.sessionStateMap     // Catch:{ all -> 0x006b }
                int r6 = r6.size()     // Catch:{ all -> 0x006b }
                r8 = 0
                r9 = 2
                if (r6 != r9) goto L_0x0066
                java.util.Map r6 = r5.sessionStateMap     // Catch:{ all -> 0x006b }
                java.util.Collection r6 = r6.values()     // Catch:{ all -> 0x006b }
                com.android.server.tv.TvInputManagerService$SessionState[] r9 = new com.android.server.tv.TvInputManagerService.SessionState[r9]     // Catch:{ all -> 0x006b }
                java.lang.Object[] r6 = r6.toArray(r9)     // Catch:{ all -> 0x006b }
                com.android.server.tv.TvInputManagerService$SessionState[] r6 = (com.android.server.tv.TvInputManagerService.SessionState[]) r6     // Catch:{ all -> 0x006b }
                r9 = r6[r8]     // Catch:{ all -> 0x006b }
                android.os.IBinder r9 = r9.hardwareSessionToken     // Catch:{ all -> 0x006b }
                if (r9 != 0) goto L_0x0061
                r9 = r6[r7]     // Catch:{ all -> 0x006b }
                android.os.IBinder r9 = r9.hardwareSessionToken     // Catch:{ all -> 0x006b }
                if (r9 == 0) goto L_0x0066
            L_0x0061:
                monitor-exit(r4)     // Catch:{ all -> 0x006b }
                android.os.Binder.restoreCallingIdentity(r0)
                return r7
            L_0x0066:
                monitor-exit(r4)     // Catch:{ all -> 0x006b }
                android.os.Binder.restoreCallingIdentity(r0)
                return r8
            L_0x006b:
                r5 = move-exception
                monitor-exit(r4)     // Catch:{ all -> 0x006b }
                throw r5     // Catch:{ all -> 0x006e }
            L_0x006e:
                r4 = move-exception
                android.os.Binder.restoreCallingIdentity(r0)
                throw r4
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.BinderService.isSingleSessionActive(int):boolean");
        }

        private void ensureCaptureTvInputPermission() {
            if (TvInputManagerService.this.mContext.checkCallingPermission("android.permission.CAPTURE_TV_INPUT") != 0) {
                throw new SecurityException("Requires CAPTURE_TV_INPUT permission");
            }
        }

        public void requestChannelBrowsable(Uri channelUri, int userId) throws RemoteException {
            String callingPackageName = getCallingPackageName();
            long identity = Binder.clearCallingIdentity();
            int resolvedUserId = TvInputManagerService.this.resolveCallingUserId(Binder.getCallingPid(), Binder.getCallingUid(), userId, "requestChannelBrowsable");
            try {
                Intent intent = new Intent("android.media.tv.action.CHANNEL_BROWSABLE_REQUESTED");
                List<ResolveInfo> list = TvInputManagerService.this.getContext().getPackageManager().queryBroadcastReceivers(intent, 0);
                if (list != null) {
                    for (ResolveInfo info : list) {
                        String receiverPackageName = info.activityInfo.packageName;
                        intent.putExtra("android.media.tv.extra.CHANNEL_ID", ContentUris.parseId(channelUri));
                        intent.putExtra("android.media.tv.extra.PACKAGE_NAME", callingPackageName);
                        intent.setPackage(receiverPackageName);
                        TvInputManagerService.this.getContext().sendBroadcastAsUser(intent, new UserHandle(resolvedUserId));
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        /* access modifiers changed from: protected */
        public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
            IndentingPrintWriter pw = new IndentingPrintWriter(writer, "  ");
            if (DumpUtils.checkDumpPermission(TvInputManagerService.this.mContext, TvInputManagerService.TAG, pw)) {
                synchronized (TvInputManagerService.this.mLock) {
                    pw.println("User Ids (Current user: " + TvInputManagerService.this.mCurrentUserId + "):");
                    pw.increaseIndent();
                    for (int i = 0; i < TvInputManagerService.this.mUserStates.size(); i++) {
                        pw.println(Integer.valueOf(TvInputManagerService.this.mUserStates.keyAt(i)));
                    }
                    pw.decreaseIndent();
                    for (int i2 = 0; i2 < TvInputManagerService.this.mUserStates.size(); i2++) {
                        int userId = TvInputManagerService.this.mUserStates.keyAt(i2);
                        UserState userState = TvInputManagerService.this.getOrCreateUserStateLocked(userId);
                        pw.println("UserState (" + userId + "):");
                        pw.increaseIndent();
                        pw.println("inputMap: inputId -> TvInputState");
                        pw.increaseIndent();
                        for (Map.Entry<String, TvInputState> entry : userState.inputMap.entrySet()) {
                            pw.println(entry.getKey() + ": " + entry.getValue());
                        }
                        pw.decreaseIndent();
                        pw.println("packageSet:");
                        pw.increaseIndent();
                        for (String packageName : userState.packageSet) {
                            pw.println(packageName);
                        }
                        pw.decreaseIndent();
                        pw.println("clientStateMap: ITvInputClient -> ClientState");
                        pw.increaseIndent();
                        for (Map.Entry<IBinder, ClientState> entry2 : userState.clientStateMap.entrySet()) {
                            ClientState client = entry2.getValue();
                            pw.println(entry2.getKey() + ": " + client);
                            pw.increaseIndent();
                            pw.println("sessionTokens:");
                            pw.increaseIndent();
                            for (IBinder token : client.sessionTokens) {
                                pw.println("" + token);
                            }
                            pw.decreaseIndent();
                            pw.println("clientTokens: " + client.clientToken);
                            pw.println("userId: " + client.userId);
                            pw.decreaseIndent();
                        }
                        pw.decreaseIndent();
                        pw.println("serviceStateMap: ComponentName -> ServiceState");
                        pw.increaseIndent();
                        for (Map.Entry<ComponentName, ServiceState> entry3 : userState.serviceStateMap.entrySet()) {
                            ServiceState service = entry3.getValue();
                            pw.println(entry3.getKey() + ": " + service);
                            pw.increaseIndent();
                            pw.println("sessionTokens:");
                            pw.increaseIndent();
                            for (IBinder token2 : service.sessionTokens) {
                                pw.println("" + token2);
                            }
                            pw.decreaseIndent();
                            pw.println("service: " + service.service);
                            pw.println("callback: " + service.callback);
                            pw.println("bound: " + service.bound);
                            pw.println("reconnecting: " + service.reconnecting);
                            pw.decreaseIndent();
                        }
                        pw.decreaseIndent();
                        pw.println("sessionStateMap: ITvInputSession -> SessionState");
                        pw.increaseIndent();
                        for (Map.Entry<IBinder, SessionState> entry4 : userState.sessionStateMap.entrySet()) {
                            SessionState session = entry4.getValue();
                            pw.println(entry4.getKey() + ": " + session);
                            pw.increaseIndent();
                            pw.println("inputId: " + session.inputId);
                            pw.println("client: " + session.client);
                            pw.println("seq: " + session.seq);
                            pw.println("callingUid: " + session.callingUid);
                            pw.println("userId: " + session.userId);
                            pw.println("sessionToken: " + session.sessionToken);
                            pw.println("session: " + session.session);
                            pw.println("logUri: " + session.logUri);
                            pw.println("hardwareSessionToken: " + session.hardwareSessionToken);
                            pw.decreaseIndent();
                        }
                        pw.decreaseIndent();
                        pw.println("callbackSet:");
                        pw.increaseIndent();
                        for (ITvInputManagerCallback callback : userState.callbackSet) {
                            pw.println(callback.toString());
                        }
                        pw.decreaseIndent();
                        pw.println("mainSessionToken: " + userState.mainSessionToken);
                        pw.decreaseIndent();
                    }
                }
                TvInputManagerService.this.mTvInputHardwareManager.dump(fd, writer, args);
            }
        }
    }

    private static final class UserState {
        /* access modifiers changed from: private */
        public final Set<ITvInputManagerCallback> callbackSet;
        /* access modifiers changed from: private */
        public final Map<IBinder, ClientState> clientStateMap;
        /* access modifiers changed from: private */
        public final List<TvContentRatingSystemInfo> contentRatingSystemList;
        /* access modifiers changed from: private */
        public Map<String, TvInputState> inputMap;
        /* access modifiers changed from: private */
        public IBinder mainSessionToken;
        /* access modifiers changed from: private */
        public final Set<String> packageSet;
        /* access modifiers changed from: private */
        public final PersistentDataStore persistentDataStore;
        /* access modifiers changed from: private */
        public final Map<ComponentName, ServiceState> serviceStateMap;
        /* access modifiers changed from: private */
        public final Map<IBinder, SessionState> sessionStateMap;

        private UserState(Context context, int userId) {
            this.inputMap = new HashMap();
            this.packageSet = new HashSet();
            this.contentRatingSystemList = new ArrayList();
            this.clientStateMap = new HashMap();
            this.serviceStateMap = new HashMap();
            this.sessionStateMap = new HashMap();
            this.callbackSet = new HashSet();
            this.mainSessionToken = null;
            this.persistentDataStore = new PersistentDataStore(context, userId);
        }
    }

    private final class ClientState implements IBinder.DeathRecipient {
        /* access modifiers changed from: private */
        public IBinder clientToken;
        /* access modifiers changed from: private */
        public final List<IBinder> sessionTokens = new ArrayList();
        /* access modifiers changed from: private */
        public final int userId;

        ClientState(IBinder clientToken2, int userId2) {
            this.clientToken = clientToken2;
            this.userId = userId2;
        }

        public boolean isEmpty() {
            return this.sessionTokens.isEmpty();
        }

        public void binderDied() {
            synchronized (TvInputManagerService.this.mLock) {
                ClientState clientState = (ClientState) TvInputManagerService.this.getOrCreateUserStateLocked(this.userId).clientStateMap.get(this.clientToken);
                if (clientState != null) {
                    while (clientState.sessionTokens.size() > 0) {
                        TvInputManagerService.this.releaseSessionLocked(clientState.sessionTokens.get(0), 1000, this.userId);
                    }
                }
                this.clientToken = null;
            }
        }
    }

    private final class ServiceState {
        /* access modifiers changed from: private */
        public boolean bound;
        /* access modifiers changed from: private */
        public ServiceCallback callback;
        /* access modifiers changed from: private */
        public final ComponentName component;
        /* access modifiers changed from: private */
        public final ServiceConnection connection;
        /* access modifiers changed from: private */
        public final Map<String, TvInputInfo> hardwareInputMap;
        /* access modifiers changed from: private */
        public final boolean isHardware;
        /* access modifiers changed from: private */
        public boolean reconnecting;
        /* access modifiers changed from: private */
        public ITvInputService service;
        /* access modifiers changed from: private */
        public final List<IBinder> sessionTokens;

        private ServiceState(ComponentName component2, int userId) {
            this.sessionTokens = new ArrayList();
            this.hardwareInputMap = new HashMap();
            this.component = component2;
            this.connection = new InputServiceConnection(component2, userId);
            this.isHardware = TvInputManagerService.hasHardwarePermission(TvInputManagerService.this.mContext.getPackageManager(), component2);
        }
    }

    private static final class TvInputState {
        /* access modifiers changed from: private */
        public TvInputInfo info;
        /* access modifiers changed from: private */
        public int state;

        private TvInputState() {
            this.state = 0;
        }

        public String toString() {
            return "info: " + this.info + "; state: " + this.state;
        }
    }

    private final class SessionState implements IBinder.DeathRecipient {
        /* access modifiers changed from: private */
        public final int callingUid;
        /* access modifiers changed from: private */
        public final ITvInputClient client;
        /* access modifiers changed from: private */
        public final ComponentName componentName;
        /* access modifiers changed from: private */
        public IBinder hardwareSessionToken;
        /* access modifiers changed from: private */
        public final String inputId;
        /* access modifiers changed from: private */
        public final boolean isRecordingSession;
        /* access modifiers changed from: private */
        public Uri logUri;
        /* access modifiers changed from: private */
        public final int seq;
        /* access modifiers changed from: private */
        public ITvInputSession session;
        /* access modifiers changed from: private */
        public final IBinder sessionToken;
        /* access modifiers changed from: private */
        public final int userId;

        private SessionState(IBinder sessionToken2, String inputId2, ComponentName componentName2, boolean isRecordingSession2, ITvInputClient client2, int seq2, int callingUid2, int userId2) {
            this.sessionToken = sessionToken2;
            this.inputId = inputId2;
            this.componentName = componentName2;
            this.isRecordingSession = isRecordingSession2;
            this.client = client2;
            this.seq = seq2;
            this.callingUid = callingUid2;
            this.userId = userId2;
        }

        public void binderDied() {
            synchronized (TvInputManagerService.this.mLock) {
                this.session = null;
                TvInputManagerService.this.clearSessionAndNotifyClientLocked(this);
            }
        }
    }

    private final class InputServiceConnection implements ServiceConnection {
        private final ComponentName mComponent;
        private final int mUserId;

        private InputServiceConnection(ComponentName component, int userId) {
            this.mComponent = component;
            this.mUserId = userId;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:64:0x0149, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onServiceConnected(android.content.ComponentName r11, android.os.IBinder r12) {
            /*
                r10 = this;
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.tv.TvInputManagerService r1 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x014a }
                android.util.SparseArray r1 = r1.mUserStates     // Catch:{ all -> 0x014a }
                int r2 = r10.mUserId     // Catch:{ all -> 0x014a }
                java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x014a }
                com.android.server.tv.TvInputManagerService$UserState r1 = (com.android.server.tv.TvInputManagerService.UserState) r1     // Catch:{ all -> 0x014a }
                if (r1 != 0) goto L_0x0022
                com.android.server.tv.TvInputManagerService r2 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x014a }
                android.content.Context r2 = r2.mContext     // Catch:{ all -> 0x014a }
                r2.unbindService(r10)     // Catch:{ all -> 0x014a }
                monitor-exit(r0)     // Catch:{ all -> 0x014a }
                return
            L_0x0022:
                java.util.Map r2 = r1.serviceStateMap     // Catch:{ all -> 0x014a }
                android.content.ComponentName r3 = r10.mComponent     // Catch:{ all -> 0x014a }
                java.lang.Object r2 = r2.get(r3)     // Catch:{ all -> 0x014a }
                com.android.server.tv.TvInputManagerService$ServiceState r2 = (com.android.server.tv.TvInputManagerService.ServiceState) r2     // Catch:{ all -> 0x014a }
                android.media.tv.ITvInputService r3 = android.media.tv.ITvInputService.Stub.asInterface(r12)     // Catch:{ all -> 0x014a }
                android.media.tv.ITvInputService unused = r2.service = r3     // Catch:{ all -> 0x014a }
                boolean r3 = r2.isHardware     // Catch:{ all -> 0x014a }
                if (r3 == 0) goto L_0x0063
                com.android.server.tv.TvInputManagerService$ServiceCallback r3 = r2.callback     // Catch:{ all -> 0x014a }
                if (r3 != 0) goto L_0x0063
                com.android.server.tv.TvInputManagerService$ServiceCallback r3 = new com.android.server.tv.TvInputManagerService$ServiceCallback     // Catch:{ all -> 0x014a }
                com.android.server.tv.TvInputManagerService r4 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x014a }
                android.content.ComponentName r5 = r10.mComponent     // Catch:{ all -> 0x014a }
                int r6 = r10.mUserId     // Catch:{ all -> 0x014a }
                r3.<init>(r5, r6)     // Catch:{ all -> 0x014a }
                com.android.server.tv.TvInputManagerService.ServiceCallback unused = r2.callback = r3     // Catch:{ all -> 0x014a }
                android.media.tv.ITvInputService r3 = r2.service     // Catch:{ RemoteException -> 0x005b }
                com.android.server.tv.TvInputManagerService$ServiceCallback r4 = r2.callback     // Catch:{ RemoteException -> 0x005b }
                r3.registerCallback(r4)     // Catch:{ RemoteException -> 0x005b }
                goto L_0x0063
            L_0x005b:
                r3 = move-exception
                java.lang.String r4 = "TvInputManagerService"
                java.lang.String r5 = "error in registerCallback"
                android.util.Slog.e(r4, r5, r3)     // Catch:{ all -> 0x014a }
            L_0x0063:
                java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ all -> 0x014a }
                r3.<init>()     // Catch:{ all -> 0x014a }
                java.util.List r4 = r2.sessionTokens     // Catch:{ all -> 0x014a }
                java.util.Iterator r4 = r4.iterator()     // Catch:{ all -> 0x014a }
            L_0x0070:
                boolean r5 = r4.hasNext()     // Catch:{ all -> 0x014a }
                if (r5 == 0) goto L_0x008e
                java.lang.Object r5 = r4.next()     // Catch:{ all -> 0x014a }
                android.os.IBinder r5 = (android.os.IBinder) r5     // Catch:{ all -> 0x014a }
                com.android.server.tv.TvInputManagerService r6 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x014a }
                android.media.tv.ITvInputService r7 = r2.service     // Catch:{ all -> 0x014a }
                int r8 = r10.mUserId     // Catch:{ all -> 0x014a }
                boolean r6 = r6.createSessionInternalLocked(r7, r5, r8)     // Catch:{ all -> 0x014a }
                if (r6 != 0) goto L_0x008d
                r3.add(r5)     // Catch:{ all -> 0x014a }
            L_0x008d:
                goto L_0x0070
            L_0x008e:
                java.util.Iterator r4 = r3.iterator()     // Catch:{ all -> 0x014a }
            L_0x0092:
                boolean r5 = r4.hasNext()     // Catch:{ all -> 0x014a }
                if (r5 == 0) goto L_0x00a6
                java.lang.Object r5 = r4.next()     // Catch:{ all -> 0x014a }
                android.os.IBinder r5 = (android.os.IBinder) r5     // Catch:{ all -> 0x014a }
                com.android.server.tv.TvInputManagerService r6 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x014a }
                int r7 = r10.mUserId     // Catch:{ all -> 0x014a }
                r6.removeSessionStateLocked(r5, r7)     // Catch:{ all -> 0x014a }
                goto L_0x0092
            L_0x00a6:
                java.util.Map r4 = r1.inputMap     // Catch:{ all -> 0x014a }
                java.util.Collection r4 = r4.values()     // Catch:{ all -> 0x014a }
                java.util.Iterator r4 = r4.iterator()     // Catch:{ all -> 0x014a }
            L_0x00b2:
                boolean r5 = r4.hasNext()     // Catch:{ all -> 0x014a }
                if (r5 == 0) goto L_0x00e5
                java.lang.Object r5 = r4.next()     // Catch:{ all -> 0x014a }
                com.android.server.tv.TvInputManagerService$TvInputState r5 = (com.android.server.tv.TvInputManagerService.TvInputState) r5     // Catch:{ all -> 0x014a }
                android.media.tv.TvInputInfo r6 = r5.info     // Catch:{ all -> 0x014a }
                android.content.ComponentName r6 = r6.getComponent()     // Catch:{ all -> 0x014a }
                boolean r6 = r6.equals(r11)     // Catch:{ all -> 0x014a }
                if (r6 == 0) goto L_0x00e4
                int r6 = r5.state     // Catch:{ all -> 0x014a }
                if (r6 == 0) goto L_0x00e4
                com.android.server.tv.TvInputManagerService r6 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x014a }
                android.media.tv.TvInputInfo r7 = r5.info     // Catch:{ all -> 0x014a }
                java.lang.String r7 = r7.getId()     // Catch:{ all -> 0x014a }
                int r8 = r5.state     // Catch:{ all -> 0x014a }
                r9 = 0
                r6.notifyInputStateChangedLocked(r1, r7, r8, r9)     // Catch:{ all -> 0x014a }
            L_0x00e4:
                goto L_0x00b2
            L_0x00e5:
                boolean r4 = r2.isHardware     // Catch:{ all -> 0x014a }
                if (r4 == 0) goto L_0x0148
                java.util.Map r4 = r2.hardwareInputMap     // Catch:{ all -> 0x014a }
                r4.clear()     // Catch:{ all -> 0x014a }
                com.android.server.tv.TvInputManagerService r4 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x014a }
                com.android.server.tv.TvInputHardwareManager r4 = r4.mTvInputHardwareManager     // Catch:{ all -> 0x014a }
                java.util.List r4 = r4.getHardwareList()     // Catch:{ all -> 0x014a }
                java.util.Iterator r4 = r4.iterator()     // Catch:{ all -> 0x014a }
            L_0x0100:
                boolean r5 = r4.hasNext()     // Catch:{ all -> 0x014a }
                if (r5 == 0) goto L_0x011d
                java.lang.Object r5 = r4.next()     // Catch:{ all -> 0x014a }
                android.media.tv.TvInputHardwareInfo r5 = (android.media.tv.TvInputHardwareInfo) r5     // Catch:{ all -> 0x014a }
                android.media.tv.ITvInputService r6 = r2.service     // Catch:{ RemoteException -> 0x0114 }
                r6.notifyHardwareAdded(r5)     // Catch:{ RemoteException -> 0x0114 }
                goto L_0x011c
            L_0x0114:
                r6 = move-exception
                java.lang.String r7 = "TvInputManagerService"
                java.lang.String r8 = "error in notifyHardwareAdded"
                android.util.Slog.e(r7, r8, r6)     // Catch:{ all -> 0x014a }
            L_0x011c:
                goto L_0x0100
            L_0x011d:
                com.android.server.tv.TvInputManagerService r4 = com.android.server.tv.TvInputManagerService.this     // Catch:{ all -> 0x014a }
                com.android.server.tv.TvInputHardwareManager r4 = r4.mTvInputHardwareManager     // Catch:{ all -> 0x014a }
                java.util.List r4 = r4.getHdmiDeviceList()     // Catch:{ all -> 0x014a }
                java.util.Iterator r4 = r4.iterator()     // Catch:{ all -> 0x014a }
            L_0x012b:
                boolean r5 = r4.hasNext()     // Catch:{ all -> 0x014a }
                if (r5 == 0) goto L_0x0148
                java.lang.Object r5 = r4.next()     // Catch:{ all -> 0x014a }
                android.hardware.hdmi.HdmiDeviceInfo r5 = (android.hardware.hdmi.HdmiDeviceInfo) r5     // Catch:{ all -> 0x014a }
                android.media.tv.ITvInputService r6 = r2.service     // Catch:{ RemoteException -> 0x013f }
                r6.notifyHdmiDeviceAdded(r5)     // Catch:{ RemoteException -> 0x013f }
                goto L_0x0147
            L_0x013f:
                r6 = move-exception
                java.lang.String r7 = "TvInputManagerService"
                java.lang.String r8 = "error in notifyHdmiDeviceAdded"
                android.util.Slog.e(r7, r8, r6)     // Catch:{ all -> 0x014a }
            L_0x0147:
                goto L_0x012b
            L_0x0148:
                monitor-exit(r0)     // Catch:{ all -> 0x014a }
                return
            L_0x014a:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x014a }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.InputServiceConnection.onServiceConnected(android.content.ComponentName, android.os.IBinder):void");
        }

        public void onServiceDisconnected(ComponentName component) {
            if (this.mComponent.equals(component)) {
                synchronized (TvInputManagerService.this.mLock) {
                    ServiceState serviceState = (ServiceState) TvInputManagerService.this.getOrCreateUserStateLocked(this.mUserId).serviceStateMap.get(this.mComponent);
                    if (serviceState != null) {
                        boolean unused = serviceState.reconnecting = true;
                        boolean unused2 = serviceState.bound = false;
                        ITvInputService unused3 = serviceState.service = null;
                        ServiceCallback unused4 = serviceState.callback = null;
                        TvInputManagerService.this.abortPendingCreateSessionRequestsLocked(serviceState, (String) null, this.mUserId);
                    }
                }
                return;
            }
            throw new IllegalArgumentException("Mismatched ComponentName: " + this.mComponent + " (expected), " + component + " (actual).");
        }
    }

    private final class ServiceCallback extends ITvInputServiceCallback.Stub {
        private final ComponentName mComponent;
        private final int mUserId;

        ServiceCallback(ComponentName component, int userId) {
            this.mComponent = component;
            this.mUserId = userId;
        }

        private void ensureHardwarePermission() {
            if (TvInputManagerService.this.mContext.checkCallingPermission("android.permission.TV_INPUT_HARDWARE") != 0) {
                throw new SecurityException("The caller does not have hardware permission");
            }
        }

        private void ensureValidInput(TvInputInfo inputInfo) {
            if (inputInfo.getId() == null || !this.mComponent.equals(inputInfo.getComponent())) {
                throw new IllegalArgumentException("Invalid TvInputInfo");
            }
        }

        private void addHardwareInputLocked(TvInputInfo inputInfo) {
            TvInputManagerService.this.getServiceStateLocked(this.mComponent, this.mUserId).hardwareInputMap.put(inputInfo.getId(), inputInfo);
            TvInputManagerService.this.buildTvInputListLocked(this.mUserId, (String[]) null);
        }

        public void addHardwareInput(int deviceId, TvInputInfo inputInfo) {
            ensureHardwarePermission();
            ensureValidInput(inputInfo);
            synchronized (TvInputManagerService.this.mLock) {
                TvInputManagerService.this.mTvInputHardwareManager.addHardwareInput(deviceId, inputInfo);
                addHardwareInputLocked(inputInfo);
            }
        }

        public void addHdmiInput(int id, TvInputInfo inputInfo) {
            ensureHardwarePermission();
            ensureValidInput(inputInfo);
            synchronized (TvInputManagerService.this.mLock) {
                TvInputManagerService.this.mTvInputHardwareManager.addHdmiInput(id, inputInfo);
                addHardwareInputLocked(inputInfo);
            }
        }

        public void removeHardwareInput(String inputId) {
            ensureHardwarePermission();
            synchronized (TvInputManagerService.this.mLock) {
                if (TvInputManagerService.this.getServiceStateLocked(this.mComponent, this.mUserId).hardwareInputMap.remove(inputId) != null) {
                    TvInputManagerService.this.buildTvInputListLocked(this.mUserId, (String[]) null);
                    TvInputManagerService.this.mTvInputHardwareManager.removeHardwareInput(inputId);
                } else {
                    Slog.e(TvInputManagerService.TAG, "failed to remove input " + inputId);
                }
            }
        }
    }

    private final class SessionCallback extends ITvInputSessionCallback.Stub {
        private final InputChannel[] mChannels;
        private final SessionState mSessionState;

        SessionCallback(SessionState sessionState, InputChannel[] channels) {
            this.mSessionState = sessionState;
            this.mChannels = channels;
        }

        public void onSessionCreated(ITvInputSession session, IBinder hardwareSessionToken) {
            synchronized (TvInputManagerService.this.mLock) {
                ITvInputSession unused = this.mSessionState.session = session;
                IBinder unused2 = this.mSessionState.hardwareSessionToken = hardwareSessionToken;
                if (session == null || !addSessionTokenToClientStateLocked(session)) {
                    TvInputManagerService.this.removeSessionStateLocked(this.mSessionState.sessionToken, this.mSessionState.userId);
                    TvInputManagerService.this.sendSessionTokenToClientLocked(this.mSessionState.client, this.mSessionState.inputId, (IBinder) null, (InputChannel) null, this.mSessionState.seq);
                } else {
                    TvInputManagerService.this.sendSessionTokenToClientLocked(this.mSessionState.client, this.mSessionState.inputId, this.mSessionState.sessionToken, this.mChannels[0], this.mSessionState.seq);
                }
                this.mChannels[0].dispose();
            }
        }

        private boolean addSessionTokenToClientStateLocked(ITvInputSession session) {
            try {
                session.asBinder().linkToDeath(this.mSessionState, 0);
                IBinder clientToken = this.mSessionState.client.asBinder();
                UserState userState = TvInputManagerService.this.getOrCreateUserStateLocked(this.mSessionState.userId);
                ClientState clientState = (ClientState) userState.clientStateMap.get(clientToken);
                if (clientState == null) {
                    clientState = new ClientState(clientToken, this.mSessionState.userId);
                    try {
                        clientToken.linkToDeath(clientState, 0);
                        userState.clientStateMap.put(clientToken, clientState);
                    } catch (RemoteException e) {
                        Slog.e(TvInputManagerService.TAG, "client process has already died", e);
                        return false;
                    }
                }
                clientState.sessionTokens.add(this.mSessionState.sessionToken);
                return true;
            } catch (RemoteException e2) {
                Slog.e(TvInputManagerService.TAG, "session process has already died", e2);
                return false;
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onChannelRetuned(android.net.Uri r5) {
            /*
                r4 = this;
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputSession r1 = r1.session     // Catch:{ all -> 0x0034 }
                if (r1 == 0) goto L_0x0032
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ all -> 0x0034 }
                if (r1 != 0) goto L_0x0018
                goto L_0x0032
            L_0x0018:
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ RemoteException -> 0x0028 }
                com.android.server.tv.TvInputManagerService$SessionState r2 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                int r2 = r2.seq     // Catch:{ RemoteException -> 0x0028 }
                r1.onChannelRetuned(r5, r2)     // Catch:{ RemoteException -> 0x0028 }
                goto L_0x0030
            L_0x0028:
                r1 = move-exception
                java.lang.String r2 = "TvInputManagerService"
                java.lang.String r3 = "error in onChannelRetuned"
                android.util.Slog.e(r2, r3, r1)     // Catch:{ all -> 0x0034 }
            L_0x0030:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0032:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0034:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.SessionCallback.onChannelRetuned(android.net.Uri):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onTracksChanged(java.util.List<android.media.tv.TvTrackInfo> r5) {
            /*
                r4 = this;
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputSession r1 = r1.session     // Catch:{ all -> 0x0034 }
                if (r1 == 0) goto L_0x0032
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ all -> 0x0034 }
                if (r1 != 0) goto L_0x0018
                goto L_0x0032
            L_0x0018:
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ RemoteException -> 0x0028 }
                com.android.server.tv.TvInputManagerService$SessionState r2 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                int r2 = r2.seq     // Catch:{ RemoteException -> 0x0028 }
                r1.onTracksChanged(r5, r2)     // Catch:{ RemoteException -> 0x0028 }
                goto L_0x0030
            L_0x0028:
                r1 = move-exception
                java.lang.String r2 = "TvInputManagerService"
                java.lang.String r3 = "error in onTracksChanged"
                android.util.Slog.e(r2, r3, r1)     // Catch:{ all -> 0x0034 }
            L_0x0030:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0032:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0034:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.SessionCallback.onTracksChanged(java.util.List):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onTrackSelected(int r5, java.lang.String r6) {
            /*
                r4 = this;
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputSession r1 = r1.session     // Catch:{ all -> 0x0034 }
                if (r1 == 0) goto L_0x0032
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ all -> 0x0034 }
                if (r1 != 0) goto L_0x0018
                goto L_0x0032
            L_0x0018:
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ RemoteException -> 0x0028 }
                com.android.server.tv.TvInputManagerService$SessionState r2 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                int r2 = r2.seq     // Catch:{ RemoteException -> 0x0028 }
                r1.onTrackSelected(r5, r6, r2)     // Catch:{ RemoteException -> 0x0028 }
                goto L_0x0030
            L_0x0028:
                r1 = move-exception
                java.lang.String r2 = "TvInputManagerService"
                java.lang.String r3 = "error in onTrackSelected"
                android.util.Slog.e(r2, r3, r1)     // Catch:{ all -> 0x0034 }
            L_0x0030:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0032:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0034:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.SessionCallback.onTrackSelected(int, java.lang.String):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onVideoAvailable() {
            /*
                r4 = this;
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputSession r1 = r1.session     // Catch:{ all -> 0x0034 }
                if (r1 == 0) goto L_0x0032
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ all -> 0x0034 }
                if (r1 != 0) goto L_0x0018
                goto L_0x0032
            L_0x0018:
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ RemoteException -> 0x0028 }
                com.android.server.tv.TvInputManagerService$SessionState r2 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                int r2 = r2.seq     // Catch:{ RemoteException -> 0x0028 }
                r1.onVideoAvailable(r2)     // Catch:{ RemoteException -> 0x0028 }
                goto L_0x0030
            L_0x0028:
                r1 = move-exception
                java.lang.String r2 = "TvInputManagerService"
                java.lang.String r3 = "error in onVideoAvailable"
                android.util.Slog.e(r2, r3, r1)     // Catch:{ all -> 0x0034 }
            L_0x0030:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0032:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0034:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.SessionCallback.onVideoAvailable():void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onVideoUnavailable(int r5) {
            /*
                r4 = this;
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputSession r1 = r1.session     // Catch:{ all -> 0x0034 }
                if (r1 == 0) goto L_0x0032
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ all -> 0x0034 }
                if (r1 != 0) goto L_0x0018
                goto L_0x0032
            L_0x0018:
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ RemoteException -> 0x0028 }
                com.android.server.tv.TvInputManagerService$SessionState r2 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                int r2 = r2.seq     // Catch:{ RemoteException -> 0x0028 }
                r1.onVideoUnavailable(r5, r2)     // Catch:{ RemoteException -> 0x0028 }
                goto L_0x0030
            L_0x0028:
                r1 = move-exception
                java.lang.String r2 = "TvInputManagerService"
                java.lang.String r3 = "error in onVideoUnavailable"
                android.util.Slog.e(r2, r3, r1)     // Catch:{ all -> 0x0034 }
            L_0x0030:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0032:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0034:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.SessionCallback.onVideoUnavailable(int):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onContentAllowed() {
            /*
                r4 = this;
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputSession r1 = r1.session     // Catch:{ all -> 0x0034 }
                if (r1 == 0) goto L_0x0032
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ all -> 0x0034 }
                if (r1 != 0) goto L_0x0018
                goto L_0x0032
            L_0x0018:
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ RemoteException -> 0x0028 }
                com.android.server.tv.TvInputManagerService$SessionState r2 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                int r2 = r2.seq     // Catch:{ RemoteException -> 0x0028 }
                r1.onContentAllowed(r2)     // Catch:{ RemoteException -> 0x0028 }
                goto L_0x0030
            L_0x0028:
                r1 = move-exception
                java.lang.String r2 = "TvInputManagerService"
                java.lang.String r3 = "error in onContentAllowed"
                android.util.Slog.e(r2, r3, r1)     // Catch:{ all -> 0x0034 }
            L_0x0030:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0032:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0034:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.SessionCallback.onContentAllowed():void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onContentBlocked(java.lang.String r5) {
            /*
                r4 = this;
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputSession r1 = r1.session     // Catch:{ all -> 0x0034 }
                if (r1 == 0) goto L_0x0032
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ all -> 0x0034 }
                if (r1 != 0) goto L_0x0018
                goto L_0x0032
            L_0x0018:
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ RemoteException -> 0x0028 }
                com.android.server.tv.TvInputManagerService$SessionState r2 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                int r2 = r2.seq     // Catch:{ RemoteException -> 0x0028 }
                r1.onContentBlocked(r5, r2)     // Catch:{ RemoteException -> 0x0028 }
                goto L_0x0030
            L_0x0028:
                r1 = move-exception
                java.lang.String r2 = "TvInputManagerService"
                java.lang.String r3 = "error in onContentBlocked"
                android.util.Slog.e(r2, r3, r1)     // Catch:{ all -> 0x0034 }
            L_0x0030:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0032:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0034:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.SessionCallback.onContentBlocked(java.lang.String):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0037, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onLayoutSurface(int r9, int r10, int r11, int r12) {
            /*
                r8 = this;
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.tv.TvInputManagerService$SessionState r1 = r8.mSessionState     // Catch:{ all -> 0x0038 }
                android.media.tv.ITvInputSession r1 = r1.session     // Catch:{ all -> 0x0038 }
                if (r1 == 0) goto L_0x0036
                com.android.server.tv.TvInputManagerService$SessionState r1 = r8.mSessionState     // Catch:{ all -> 0x0038 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ all -> 0x0038 }
                if (r1 != 0) goto L_0x0018
                goto L_0x0036
            L_0x0018:
                com.android.server.tv.TvInputManagerService$SessionState r1 = r8.mSessionState     // Catch:{ RemoteException -> 0x002c }
                android.media.tv.ITvInputClient r2 = r1.client     // Catch:{ RemoteException -> 0x002c }
                com.android.server.tv.TvInputManagerService$SessionState r1 = r8.mSessionState     // Catch:{ RemoteException -> 0x002c }
                int r7 = r1.seq     // Catch:{ RemoteException -> 0x002c }
                r3 = r9
                r4 = r10
                r5 = r11
                r6 = r12
                r2.onLayoutSurface(r3, r4, r5, r6, r7)     // Catch:{ RemoteException -> 0x002c }
                goto L_0x0034
            L_0x002c:
                r1 = move-exception
                java.lang.String r2 = "TvInputManagerService"
                java.lang.String r3 = "error in onLayoutSurface"
                android.util.Slog.e(r2, r3, r1)     // Catch:{ all -> 0x0038 }
            L_0x0034:
                monitor-exit(r0)     // Catch:{ all -> 0x0038 }
                return
            L_0x0036:
                monitor-exit(r0)     // Catch:{ all -> 0x0038 }
                return
            L_0x0038:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0038 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.SessionCallback.onLayoutSurface(int, int, int, int):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onSessionEvent(java.lang.String r5, android.os.Bundle r6) {
            /*
                r4 = this;
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputSession r1 = r1.session     // Catch:{ all -> 0x0034 }
                if (r1 == 0) goto L_0x0032
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ all -> 0x0034 }
                if (r1 != 0) goto L_0x0018
                goto L_0x0032
            L_0x0018:
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ RemoteException -> 0x0028 }
                com.android.server.tv.TvInputManagerService$SessionState r2 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                int r2 = r2.seq     // Catch:{ RemoteException -> 0x0028 }
                r1.onSessionEvent(r5, r6, r2)     // Catch:{ RemoteException -> 0x0028 }
                goto L_0x0030
            L_0x0028:
                r1 = move-exception
                java.lang.String r2 = "TvInputManagerService"
                java.lang.String r3 = "error in onSessionEvent"
                android.util.Slog.e(r2, r3, r1)     // Catch:{ all -> 0x0034 }
            L_0x0030:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0032:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0034:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.SessionCallback.onSessionEvent(java.lang.String, android.os.Bundle):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onTimeShiftStatusChanged(int r5) {
            /*
                r4 = this;
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputSession r1 = r1.session     // Catch:{ all -> 0x0034 }
                if (r1 == 0) goto L_0x0032
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ all -> 0x0034 }
                if (r1 != 0) goto L_0x0018
                goto L_0x0032
            L_0x0018:
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ RemoteException -> 0x0028 }
                com.android.server.tv.TvInputManagerService$SessionState r2 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                int r2 = r2.seq     // Catch:{ RemoteException -> 0x0028 }
                r1.onTimeShiftStatusChanged(r5, r2)     // Catch:{ RemoteException -> 0x0028 }
                goto L_0x0030
            L_0x0028:
                r1 = move-exception
                java.lang.String r2 = "TvInputManagerService"
                java.lang.String r3 = "error in onTimeShiftStatusChanged"
                android.util.Slog.e(r2, r3, r1)     // Catch:{ all -> 0x0034 }
            L_0x0030:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0032:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0034:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.SessionCallback.onTimeShiftStatusChanged(int):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onTimeShiftStartPositionChanged(long r5) {
            /*
                r4 = this;
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputSession r1 = r1.session     // Catch:{ all -> 0x0034 }
                if (r1 == 0) goto L_0x0032
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ all -> 0x0034 }
                if (r1 != 0) goto L_0x0018
                goto L_0x0032
            L_0x0018:
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ RemoteException -> 0x0028 }
                com.android.server.tv.TvInputManagerService$SessionState r2 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                int r2 = r2.seq     // Catch:{ RemoteException -> 0x0028 }
                r1.onTimeShiftStartPositionChanged(r5, r2)     // Catch:{ RemoteException -> 0x0028 }
                goto L_0x0030
            L_0x0028:
                r1 = move-exception
                java.lang.String r2 = "TvInputManagerService"
                java.lang.String r3 = "error in onTimeShiftStartPositionChanged"
                android.util.Slog.e(r2, r3, r1)     // Catch:{ all -> 0x0034 }
            L_0x0030:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0032:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0034:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.SessionCallback.onTimeShiftStartPositionChanged(long):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onTimeShiftCurrentPositionChanged(long r5) {
            /*
                r4 = this;
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputSession r1 = r1.session     // Catch:{ all -> 0x0034 }
                if (r1 == 0) goto L_0x0032
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ all -> 0x0034 }
                if (r1 != 0) goto L_0x0018
                goto L_0x0032
            L_0x0018:
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ RemoteException -> 0x0028 }
                com.android.server.tv.TvInputManagerService$SessionState r2 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                int r2 = r2.seq     // Catch:{ RemoteException -> 0x0028 }
                r1.onTimeShiftCurrentPositionChanged(r5, r2)     // Catch:{ RemoteException -> 0x0028 }
                goto L_0x0030
            L_0x0028:
                r1 = move-exception
                java.lang.String r2 = "TvInputManagerService"
                java.lang.String r3 = "error in onTimeShiftCurrentPositionChanged"
                android.util.Slog.e(r2, r3, r1)     // Catch:{ all -> 0x0034 }
            L_0x0030:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0032:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0034:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.SessionCallback.onTimeShiftCurrentPositionChanged(long):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onTuned(android.net.Uri r5) {
            /*
                r4 = this;
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputSession r1 = r1.session     // Catch:{ all -> 0x0034 }
                if (r1 == 0) goto L_0x0032
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ all -> 0x0034 }
                if (r1 != 0) goto L_0x0018
                goto L_0x0032
            L_0x0018:
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ RemoteException -> 0x0028 }
                com.android.server.tv.TvInputManagerService$SessionState r2 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                int r2 = r2.seq     // Catch:{ RemoteException -> 0x0028 }
                r1.onTuned(r2, r5)     // Catch:{ RemoteException -> 0x0028 }
                goto L_0x0030
            L_0x0028:
                r1 = move-exception
                java.lang.String r2 = "TvInputManagerService"
                java.lang.String r3 = "error in onTuned"
                android.util.Slog.e(r2, r3, r1)     // Catch:{ all -> 0x0034 }
            L_0x0030:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0032:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0034:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.SessionCallback.onTuned(android.net.Uri):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onRecordingStopped(android.net.Uri r5) {
            /*
                r4 = this;
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputSession r1 = r1.session     // Catch:{ all -> 0x0034 }
                if (r1 == 0) goto L_0x0032
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ all -> 0x0034 }
                if (r1 != 0) goto L_0x0018
                goto L_0x0032
            L_0x0018:
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ RemoteException -> 0x0028 }
                com.android.server.tv.TvInputManagerService$SessionState r2 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                int r2 = r2.seq     // Catch:{ RemoteException -> 0x0028 }
                r1.onRecordingStopped(r5, r2)     // Catch:{ RemoteException -> 0x0028 }
                goto L_0x0030
            L_0x0028:
                r1 = move-exception
                java.lang.String r2 = "TvInputManagerService"
                java.lang.String r3 = "error in onRecordingStopped"
                android.util.Slog.e(r2, r3, r1)     // Catch:{ all -> 0x0034 }
            L_0x0030:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0032:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0034:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.SessionCallback.onRecordingStopped(android.net.Uri):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onError(int r5) {
            /*
                r4 = this;
                com.android.server.tv.TvInputManagerService r0 = com.android.server.tv.TvInputManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputSession r1 = r1.session     // Catch:{ all -> 0x0034 }
                if (r1 == 0) goto L_0x0032
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ all -> 0x0034 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ all -> 0x0034 }
                if (r1 != 0) goto L_0x0018
                goto L_0x0032
            L_0x0018:
                com.android.server.tv.TvInputManagerService$SessionState r1 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                android.media.tv.ITvInputClient r1 = r1.client     // Catch:{ RemoteException -> 0x0028 }
                com.android.server.tv.TvInputManagerService$SessionState r2 = r4.mSessionState     // Catch:{ RemoteException -> 0x0028 }
                int r2 = r2.seq     // Catch:{ RemoteException -> 0x0028 }
                r1.onError(r5, r2)     // Catch:{ RemoteException -> 0x0028 }
                goto L_0x0030
            L_0x0028:
                r1 = move-exception
                java.lang.String r2 = "TvInputManagerService"
                java.lang.String r3 = "error in onError"
                android.util.Slog.e(r2, r3, r1)     // Catch:{ all -> 0x0034 }
            L_0x0030:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0032:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return
            L_0x0034:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputManagerService.SessionCallback.onError(int):void");
        }
    }

    private static final class WatchLogHandler extends Handler {
        static final int MSG_LOG_WATCH_END = 2;
        static final int MSG_LOG_WATCH_START = 1;
        static final int MSG_SWITCH_CONTENT_RESOLVER = 3;
        private ContentResolver mContentResolver;

        WatchLogHandler(ContentResolver contentResolver, Looper looper) {
            super(looper);
            this.mContentResolver = contentResolver;
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                SomeArgs args = (SomeArgs) msg.obj;
                long watchStartTime = ((Long) args.arg2).longValue();
                long channelId = ((Long) args.arg3).longValue();
                Bundle tuneParams = (Bundle) args.arg4;
                IBinder sessionToken = (IBinder) args.arg5;
                ContentValues values = new ContentValues();
                values.put("package_name", (String) args.arg1);
                values.put("watch_start_time_utc_millis", Long.valueOf(watchStartTime));
                values.put("channel_id", Long.valueOf(channelId));
                if (tuneParams != null) {
                    values.put("tune_params", encodeTuneParams(tuneParams));
                }
                values.put("session_token", sessionToken.toString());
                this.mContentResolver.insert(TvContract.WatchedPrograms.CONTENT_URI, values);
                args.recycle();
            } else if (i == 2) {
                SomeArgs args2 = (SomeArgs) msg.obj;
                long watchEndTime = ((Long) args2.arg2).longValue();
                ContentValues values2 = new ContentValues();
                values2.put("watch_end_time_utc_millis", Long.valueOf(watchEndTime));
                values2.put("session_token", ((IBinder) args2.arg1).toString());
                this.mContentResolver.insert(TvContract.WatchedPrograms.CONTENT_URI, values2);
                args2.recycle();
            } else if (i != 3) {
                Slog.w(TvInputManagerService.TAG, "unhandled message code: " + msg.what);
            } else {
                this.mContentResolver = (ContentResolver) msg.obj;
            }
        }

        private String encodeTuneParams(Bundle tuneParams) {
            StringBuilder builder = new StringBuilder();
            Iterator<String> it = tuneParams.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Object value = tuneParams.get(key);
                if (value != null) {
                    builder.append(replaceEscapeCharacters(key));
                    builder.append("=");
                    builder.append(replaceEscapeCharacters(value.toString()));
                    if (it.hasNext()) {
                        builder.append(", ");
                    }
                }
            }
            return builder.toString();
        }

        private String replaceEscapeCharacters(String src) {
            StringBuilder builder = new StringBuilder();
            for (char ch : src.toCharArray()) {
                if ("%=,".indexOf(ch) >= 0) {
                    builder.append('%');
                }
                builder.append(ch);
            }
            return builder.toString();
        }
    }

    private final class HardwareListener implements TvInputHardwareManager.Listener {
        private HardwareListener() {
        }

        public void onStateChanged(String inputId, int state) {
            synchronized (TvInputManagerService.this.mLock) {
                TvInputManagerService.this.setStateLocked(inputId, state, TvInputManagerService.this.mCurrentUserId);
            }
        }

        public void onHardwareDeviceAdded(TvInputHardwareInfo info) {
            synchronized (TvInputManagerService.this.mLock) {
                for (ServiceState serviceState : TvInputManagerService.this.getOrCreateUserStateLocked(TvInputManagerService.this.mCurrentUserId).serviceStateMap.values()) {
                    if (serviceState.isHardware && serviceState.service != null) {
                        try {
                            serviceState.service.notifyHardwareAdded(info);
                        } catch (RemoteException e) {
                            Slog.e(TvInputManagerService.TAG, "error in notifyHardwareAdded", e);
                        }
                    }
                }
            }
        }

        public void onHardwareDeviceRemoved(TvInputHardwareInfo info) {
            synchronized (TvInputManagerService.this.mLock) {
                for (ServiceState serviceState : TvInputManagerService.this.getOrCreateUserStateLocked(TvInputManagerService.this.mCurrentUserId).serviceStateMap.values()) {
                    if (serviceState.isHardware && serviceState.service != null) {
                        try {
                            serviceState.service.notifyHardwareRemoved(info);
                        } catch (RemoteException e) {
                            Slog.e(TvInputManagerService.TAG, "error in notifyHardwareRemoved", e);
                        }
                    }
                }
            }
        }

        public void onHdmiDeviceAdded(HdmiDeviceInfo deviceInfo) {
            synchronized (TvInputManagerService.this.mLock) {
                for (ServiceState serviceState : TvInputManagerService.this.getOrCreateUserStateLocked(TvInputManagerService.this.mCurrentUserId).serviceStateMap.values()) {
                    if (serviceState.isHardware && serviceState.service != null) {
                        try {
                            serviceState.service.notifyHdmiDeviceAdded(deviceInfo);
                        } catch (RemoteException e) {
                            Slog.e(TvInputManagerService.TAG, "error in notifyHdmiDeviceAdded", e);
                        }
                    }
                }
            }
        }

        public void onHdmiDeviceRemoved(HdmiDeviceInfo deviceInfo) {
            synchronized (TvInputManagerService.this.mLock) {
                for (ServiceState serviceState : TvInputManagerService.this.getOrCreateUserStateLocked(TvInputManagerService.this.mCurrentUserId).serviceStateMap.values()) {
                    if (serviceState.isHardware && serviceState.service != null) {
                        try {
                            serviceState.service.notifyHdmiDeviceRemoved(deviceInfo);
                        } catch (RemoteException e) {
                            Slog.e(TvInputManagerService.TAG, "error in notifyHdmiDeviceRemoved", e);
                        }
                    }
                }
            }
        }

        public void onHdmiDeviceUpdated(String inputId, HdmiDeviceInfo deviceInfo) {
            Integer state;
            synchronized (TvInputManagerService.this.mLock) {
                int devicePowerStatus = deviceInfo.getDevicePowerStatus();
                if (devicePowerStatus == 0) {
                    state = 0;
                } else if (devicePowerStatus == 1 || devicePowerStatus == 2 || devicePowerStatus == 3) {
                    state = 1;
                } else {
                    state = null;
                }
                if (state != null) {
                    TvInputManagerService.this.setStateLocked(inputId, state.intValue(), TvInputManagerService.this.mCurrentUserId);
                }
            }
        }
    }

    private static class SessionNotFoundException extends IllegalArgumentException {
        public SessionNotFoundException(String name) {
            super(name);
        }
    }
}
