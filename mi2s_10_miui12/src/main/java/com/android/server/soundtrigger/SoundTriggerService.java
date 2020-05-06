package com.android.server.soundtrigger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.hardware.broadcastradio.V2_0.IdentifierType;
import android.hardware.soundtrigger.IRecognitionStatusCallback;
import android.hardware.soundtrigger.SoundTrigger;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.soundtrigger.ISoundTriggerDetectionService;
import android.media.soundtrigger.ISoundTriggerDetectionServiceClient;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.ISoundTriggerService;
import com.android.internal.util.Preconditions;
import com.android.server.SystemService;
import com.android.server.soundtrigger.SoundTriggerLogger;
import com.android.server.soundtrigger.SoundTriggerService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SoundTriggerService extends SystemService {
    private static final boolean DEBUG = true;
    private static final String TAG = "SoundTriggerService";
    /* access modifiers changed from: private */
    public static final SoundTriggerLogger sEventLogger = new SoundTriggerLogger(200, "SoundTrigger activity");
    /* access modifiers changed from: private */
    public final TreeMap<UUID, IRecognitionStatusCallback> mCallbacks;
    /* access modifiers changed from: private */
    public Object mCallbacksLock;
    final Context mContext;
    /* access modifiers changed from: private */
    public SoundTriggerDbHelper mDbHelper;
    /* access modifiers changed from: private */
    public final TreeMap<UUID, SoundTrigger.SoundModel> mLoadedModels;
    private final LocalSoundTriggerService mLocalSoundTriggerService;
    /* access modifiers changed from: private */
    public Object mLock;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public final ArrayMap<String, NumOps> mNumOpsPerPackage = new ArrayMap<>();
    private final SoundTriggerServiceStub mServiceStub;
    /* access modifiers changed from: private */
    public SoundTriggerHelper mSoundTriggerHelper;

    public SoundTriggerService(Context context) {
        super(context);
        this.mContext = context;
        this.mServiceStub = new SoundTriggerServiceStub();
        this.mLocalSoundTriggerService = new LocalSoundTriggerService(context);
        this.mLoadedModels = new TreeMap<>();
        this.mCallbacksLock = new Object();
        this.mCallbacks = new TreeMap<>();
        this.mLock = new Object();
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.soundtrigger.SoundTriggerService$SoundTriggerServiceStub, android.os.IBinder] */
    public void onStart() {
        publishBinderService("soundtrigger", this.mServiceStub);
        publishLocalService(SoundTriggerInternal.class, this.mLocalSoundTriggerService);
    }

    public void onBootPhase(int phase) {
        if (500 == phase) {
            initSoundTriggerHelper();
            this.mLocalSoundTriggerService.setSoundTriggerHelper(this.mSoundTriggerHelper);
        } else if (600 == phase) {
            this.mDbHelper = new SoundTriggerDbHelper(this.mContext);
        }
    }

    public void onStartUser(int userHandle) {
    }

    public void onSwitchUser(int userHandle) {
    }

    private synchronized void initSoundTriggerHelper() {
        if (this.mSoundTriggerHelper == null) {
            this.mSoundTriggerHelper = new SoundTriggerHelper(this.mContext);
        }
    }

    /* access modifiers changed from: private */
    public synchronized boolean isInitialized() {
        if (this.mSoundTriggerHelper != null) {
            return true;
        }
        Slog.e(TAG, "SoundTriggerHelper not initialized.");
        return false;
    }

    class SoundTriggerServiceStub extends ISoundTriggerService.Stub {
        SoundTriggerServiceStub() {
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            try {
                return SoundTriggerService.super.onTransact(code, data, reply, flags);
            } catch (RuntimeException e) {
                if (!(e instanceof SecurityException)) {
                    Slog.wtf(SoundTriggerService.TAG, "SoundTriggerService Crash", e);
                }
                throw e;
            }
        }

        public int startRecognition(ParcelUuid parcelUuid, IRecognitionStatusCallback callback, SoundTrigger.RecognitionConfig config) {
            SoundTriggerService.this.enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
            if (!SoundTriggerService.this.isInitialized()) {
                return Integer.MIN_VALUE;
            }
            Slog.i(SoundTriggerService.TAG, "startRecognition(): Uuid : " + parcelUuid);
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent("startRecognition(): Uuid : " + parcelUuid));
            SoundTrigger.GenericSoundModel model = getSoundModel(parcelUuid);
            if (model != null) {
                return SoundTriggerService.this.mSoundTriggerHelper.startGenericRecognition(parcelUuid.getUuid(), model, callback, config);
            }
            Slog.e(SoundTriggerService.TAG, "Null model in database for id: " + parcelUuid);
            SoundTriggerLogger access$2002 = SoundTriggerService.sEventLogger;
            access$2002.log(new SoundTriggerLogger.StringEvent("startRecognition(): Null model in database for id: " + parcelUuid));
            return Integer.MIN_VALUE;
        }

        public int stopRecognition(ParcelUuid parcelUuid, IRecognitionStatusCallback callback) {
            SoundTriggerService.this.enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
            Slog.i(SoundTriggerService.TAG, "stopRecognition(): Uuid : " + parcelUuid);
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent("stopRecognition(): Uuid : " + parcelUuid));
            if (!SoundTriggerService.this.isInitialized()) {
                return Integer.MIN_VALUE;
            }
            return SoundTriggerService.this.mSoundTriggerHelper.stopGenericRecognition(parcelUuid.getUuid(), callback);
        }

        public SoundTrigger.GenericSoundModel getSoundModel(ParcelUuid soundModelId) {
            SoundTriggerService.this.enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
            Slog.i(SoundTriggerService.TAG, "getSoundModel(): id = " + soundModelId);
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent("getSoundModel(): id = " + soundModelId));
            return SoundTriggerService.this.mDbHelper.getGenericSoundModel(soundModelId.getUuid());
        }

        public void updateSoundModel(SoundTrigger.GenericSoundModel soundModel) {
            SoundTriggerService.this.enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
            Slog.i(SoundTriggerService.TAG, "updateSoundModel(): model = " + soundModel);
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent("updateSoundModel(): model = " + soundModel));
            SoundTriggerService.this.mDbHelper.updateGenericSoundModel(soundModel);
        }

        public void deleteSoundModel(ParcelUuid soundModelId) {
            SoundTriggerService.this.enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
            Slog.i(SoundTriggerService.TAG, "deleteSoundModel(): id = " + soundModelId);
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent("deleteSoundModel(): id = " + soundModelId));
            SoundTriggerService.this.mSoundTriggerHelper.unloadGenericSoundModel(soundModelId.getUuid());
            SoundTriggerService.this.mDbHelper.deleteGenericSoundModel(soundModelId.getUuid());
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        public int loadGenericSoundModel(SoundTrigger.GenericSoundModel soundModel) {
            SoundTriggerService.this.enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
            if (!SoundTriggerService.this.isInitialized()) {
                return Integer.MIN_VALUE;
            }
            if (soundModel == null || soundModel.uuid == null) {
                Slog.e(SoundTriggerService.TAG, "Invalid sound model");
                SoundTriggerService.sEventLogger.log(new SoundTriggerLogger.StringEvent("loadGenericSoundModel(): Invalid sound model"));
                return Integer.MIN_VALUE;
            }
            Slog.i(SoundTriggerService.TAG, "loadGenericSoundModel(): id = " + soundModel.uuid);
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent("loadGenericSoundModel(): id = " + soundModel.uuid));
            synchronized (SoundTriggerService.this.mLock) {
                SoundTrigger.SoundModel oldModel = (SoundTrigger.SoundModel) SoundTriggerService.this.mLoadedModels.get(soundModel.uuid);
                if (oldModel != null && !oldModel.equals(soundModel)) {
                    SoundTriggerService.this.mSoundTriggerHelper.unloadGenericSoundModel(soundModel.uuid);
                    synchronized (SoundTriggerService.this.mCallbacksLock) {
                        SoundTriggerService.this.mCallbacks.remove(soundModel.uuid);
                    }
                }
                SoundTriggerService.this.mLoadedModels.put(soundModel.uuid, soundModel);
            }
            return 0;
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        public int loadKeyphraseSoundModel(SoundTrigger.KeyphraseSoundModel soundModel) {
            SoundTriggerService.this.enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
            if (!SoundTriggerService.this.isInitialized()) {
                return Integer.MIN_VALUE;
            }
            if (soundModel == null || soundModel.uuid == null) {
                Slog.e(SoundTriggerService.TAG, "Invalid sound model");
                SoundTriggerService.sEventLogger.log(new SoundTriggerLogger.StringEvent("loadKeyphraseSoundModel(): Invalid sound model"));
                return Integer.MIN_VALUE;
            } else if (soundModel.keyphrases == null || soundModel.keyphrases.length != 1) {
                Slog.e(SoundTriggerService.TAG, "Only one keyphrase per model is currently supported.");
                SoundTriggerService.sEventLogger.log(new SoundTriggerLogger.StringEvent("loadKeyphraseSoundModel(): Only one keyphrase per model is currently supported."));
                return Integer.MIN_VALUE;
            } else {
                Slog.i(SoundTriggerService.TAG, "loadKeyphraseSoundModel(): id = " + soundModel.uuid);
                SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
                access$200.log(new SoundTriggerLogger.StringEvent("loadKeyphraseSoundModel(): id = " + soundModel.uuid));
                synchronized (SoundTriggerService.this.mLock) {
                    SoundTrigger.SoundModel oldModel = (SoundTrigger.SoundModel) SoundTriggerService.this.mLoadedModels.get(soundModel.uuid);
                    if (oldModel != null && !oldModel.equals(soundModel)) {
                        SoundTriggerService.this.mSoundTriggerHelper.unloadKeyphraseSoundModel(soundModel.keyphrases[0].id);
                        synchronized (SoundTriggerService.this.mCallbacksLock) {
                            SoundTriggerService.this.mCallbacks.remove(soundModel.uuid);
                        }
                    }
                    SoundTriggerService.this.mLoadedModels.put(soundModel.uuid, soundModel);
                }
                return 0;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 11 */
        public int startRecognitionForService(ParcelUuid soundModelId, Bundle params, ComponentName detectionService, SoundTrigger.RecognitionConfig config) {
            IRecognitionStatusCallback existingCallback;
            Preconditions.checkNotNull(soundModelId);
            Preconditions.checkNotNull(detectionService);
            Preconditions.checkNotNull(config);
            SoundTriggerService.this.enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
            if (!SoundTriggerService.this.isInitialized()) {
                return Integer.MIN_VALUE;
            }
            Slog.i(SoundTriggerService.TAG, "startRecognition(): id = " + soundModelId);
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent("startRecognitionForService(): id = " + soundModelId));
            IRecognitionStatusCallback existingCallback2 = new RemoteSoundTriggerDetectionService(soundModelId.getUuid(), params, detectionService, Binder.getCallingUserHandle(), config);
            synchronized (SoundTriggerService.this.mLock) {
                SoundTrigger.SoundModel soundModel = (SoundTrigger.SoundModel) SoundTriggerService.this.mLoadedModels.get(soundModelId.getUuid());
                if (soundModel == null) {
                    Slog.e(SoundTriggerService.TAG, soundModelId + " is not loaded");
                    SoundTriggerLogger access$2002 = SoundTriggerService.sEventLogger;
                    access$2002.log(new SoundTriggerLogger.StringEvent("startRecognitionForService():" + soundModelId + " is not loaded"));
                    return Integer.MIN_VALUE;
                }
                synchronized (SoundTriggerService.this.mCallbacksLock) {
                    existingCallback = (IRecognitionStatusCallback) SoundTriggerService.this.mCallbacks.get(soundModelId.getUuid());
                }
                if (existingCallback != null) {
                    Slog.e(SoundTriggerService.TAG, soundModelId + " is already running");
                    SoundTriggerLogger access$2003 = SoundTriggerService.sEventLogger;
                    access$2003.log(new SoundTriggerLogger.StringEvent("startRecognitionForService():" + soundModelId + " is already running"));
                    return Integer.MIN_VALUE;
                } else if (soundModel.type != 1) {
                    Slog.e(SoundTriggerService.TAG, "Unknown model type");
                    SoundTriggerService.sEventLogger.log(new SoundTriggerLogger.StringEvent("startRecognitionForService(): Unknown model type"));
                    return Integer.MIN_VALUE;
                } else {
                    int ret = SoundTriggerService.this.mSoundTriggerHelper.startGenericRecognition(soundModel.uuid, (SoundTrigger.GenericSoundModel) soundModel, existingCallback2, config);
                    if (ret != 0) {
                        Slog.e(SoundTriggerService.TAG, "Failed to start model: " + ret);
                        SoundTriggerService.sEventLogger.log(new SoundTriggerLogger.StringEvent("startRecognitionForService(): Failed to start model:"));
                        return ret;
                    }
                    synchronized (SoundTriggerService.this.mCallbacksLock) {
                        SoundTriggerService.this.mCallbacks.put(soundModelId.getUuid(), existingCallback2);
                    }
                    return 0;
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public int stopRecognitionForService(ParcelUuid soundModelId) {
            IRecognitionStatusCallback callback;
            SoundTriggerService.this.enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
            if (!SoundTriggerService.this.isInitialized()) {
                return Integer.MIN_VALUE;
            }
            Slog.i(SoundTriggerService.TAG, "stopRecognition(): id = " + soundModelId);
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent("stopRecognitionForService(): id = " + soundModelId));
            synchronized (SoundTriggerService.this.mLock) {
                SoundTrigger.SoundModel soundModel = (SoundTrigger.SoundModel) SoundTriggerService.this.mLoadedModels.get(soundModelId.getUuid());
                if (soundModel == null) {
                    Slog.e(SoundTriggerService.TAG, soundModelId + " is not loaded");
                    SoundTriggerLogger access$2002 = SoundTriggerService.sEventLogger;
                    access$2002.log(new SoundTriggerLogger.StringEvent("stopRecognitionForService(): " + soundModelId + " is not loaded"));
                    return Integer.MIN_VALUE;
                }
                synchronized (SoundTriggerService.this.mCallbacksLock) {
                    callback = (IRecognitionStatusCallback) SoundTriggerService.this.mCallbacks.get(soundModelId.getUuid());
                }
                if (callback == null) {
                    Slog.e(SoundTriggerService.TAG, soundModelId + " is not running");
                    SoundTriggerLogger access$2003 = SoundTriggerService.sEventLogger;
                    access$2003.log(new SoundTriggerLogger.StringEvent("stopRecognitionForService(): " + soundModelId + " is not running"));
                    return Integer.MIN_VALUE;
                } else if (soundModel.type != 1) {
                    Slog.e(SoundTriggerService.TAG, "Unknown model type");
                    SoundTriggerService.sEventLogger.log(new SoundTriggerLogger.StringEvent("stopRecognitionForService(): Unknown model type"));
                    return Integer.MIN_VALUE;
                } else {
                    int ret = SoundTriggerService.this.mSoundTriggerHelper.stopGenericRecognition(soundModel.uuid, callback);
                    if (ret != 0) {
                        Slog.e(SoundTriggerService.TAG, "Failed to stop model: " + ret);
                        SoundTriggerLogger access$2004 = SoundTriggerService.sEventLogger;
                        access$2004.log(new SoundTriggerLogger.StringEvent("stopRecognitionForService(): Failed to stop model: " + ret));
                        return ret;
                    }
                    synchronized (SoundTriggerService.this.mCallbacksLock) {
                        SoundTriggerService.this.mCallbacks.remove(soundModelId.getUuid());
                    }
                    return 0;
                }
            }
        }

        public int unloadSoundModel(ParcelUuid soundModelId) {
            int ret;
            SoundTriggerService.this.enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
            if (!SoundTriggerService.this.isInitialized()) {
                return Integer.MIN_VALUE;
            }
            Slog.i(SoundTriggerService.TAG, "unloadSoundModel(): id = " + soundModelId);
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent("unloadSoundModel(): id = " + soundModelId));
            synchronized (SoundTriggerService.this.mLock) {
                SoundTrigger.SoundModel soundModel = (SoundTrigger.SoundModel) SoundTriggerService.this.mLoadedModels.get(soundModelId.getUuid());
                if (soundModel == null) {
                    Slog.e(SoundTriggerService.TAG, soundModelId + " is not loaded");
                    SoundTriggerLogger access$2002 = SoundTriggerService.sEventLogger;
                    access$2002.log(new SoundTriggerLogger.StringEvent("unloadSoundModel(): " + soundModelId + " is not loaded"));
                    return Integer.MIN_VALUE;
                }
                int i = soundModel.type;
                if (i == 0) {
                    ret = SoundTriggerService.this.mSoundTriggerHelper.unloadKeyphraseSoundModel(((SoundTrigger.KeyphraseSoundModel) soundModel).keyphrases[0].id);
                } else if (i != 1) {
                    Slog.e(SoundTriggerService.TAG, "Unknown model type");
                    SoundTriggerService.sEventLogger.log(new SoundTriggerLogger.StringEvent("unloadSoundModel(): Unknown model type"));
                    return Integer.MIN_VALUE;
                } else {
                    ret = SoundTriggerService.this.mSoundTriggerHelper.unloadGenericSoundModel(soundModel.uuid);
                }
                if (ret != 0) {
                    Slog.e(SoundTriggerService.TAG, "Failed to unload model");
                    SoundTriggerService.sEventLogger.log(new SoundTriggerLogger.StringEvent("unloadSoundModel(): Failed to unload model"));
                    return ret;
                }
                SoundTriggerService.this.mLoadedModels.remove(soundModelId.getUuid());
                return 0;
            }
        }

        public boolean isRecognitionActive(ParcelUuid parcelUuid) {
            SoundTriggerService.this.enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
            if (!SoundTriggerService.this.isInitialized()) {
                return false;
            }
            synchronized (SoundTriggerService.this.mCallbacksLock) {
                if (((IRecognitionStatusCallback) SoundTriggerService.this.mCallbacks.get(parcelUuid.getUuid())) == null) {
                    return false;
                }
                return SoundTriggerService.this.mSoundTriggerHelper.isRecognitionRequested(parcelUuid.getUuid());
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x00e4, code lost:
            return r0;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int getModelState(android.os.ParcelUuid r8) {
            /*
                r7 = this;
                com.android.server.soundtrigger.SoundTriggerService r0 = com.android.server.soundtrigger.SoundTriggerService.this
                java.lang.String r1 = "android.permission.MANAGE_SOUND_TRIGGER"
                r0.enforceCallingPermission(r1)
                r0 = -2147483648(0xffffffff80000000, float:-0.0)
                com.android.server.soundtrigger.SoundTriggerService r1 = com.android.server.soundtrigger.SoundTriggerService.this
                boolean r1 = r1.isInitialized()
                if (r1 != 0) goto L_0x0012
                return r0
            L_0x0012:
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "getModelState(): id = "
                r1.append(r2)
                r1.append(r8)
                java.lang.String r1 = r1.toString()
                java.lang.String r2 = "SoundTriggerService"
                android.util.Slog.i(r2, r1)
                com.android.server.soundtrigger.SoundTriggerLogger r1 = com.android.server.soundtrigger.SoundTriggerService.sEventLogger
                com.android.server.soundtrigger.SoundTriggerLogger$StringEvent r2 = new com.android.server.soundtrigger.SoundTriggerLogger$StringEvent
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "getModelState(): id = "
                r3.append(r4)
                r3.append(r8)
                java.lang.String r3 = r3.toString()
                r2.<init>(r3)
                r1.log(r2)
                com.android.server.soundtrigger.SoundTriggerService r1 = com.android.server.soundtrigger.SoundTriggerService.this
                java.lang.Object r1 = r1.mLock
                monitor-enter(r1)
                com.android.server.soundtrigger.SoundTriggerService r2 = com.android.server.soundtrigger.SoundTriggerService.this     // Catch:{ all -> 0x00e5 }
                java.util.TreeMap r2 = r2.mLoadedModels     // Catch:{ all -> 0x00e5 }
                java.util.UUID r3 = r8.getUuid()     // Catch:{ all -> 0x00e5 }
                java.lang.Object r2 = r2.get(r3)     // Catch:{ all -> 0x00e5 }
                android.hardware.soundtrigger.SoundTrigger$SoundModel r2 = (android.hardware.soundtrigger.SoundTrigger.SoundModel) r2     // Catch:{ all -> 0x00e5 }
                if (r2 != 0) goto L_0x0098
                java.lang.String r3 = "SoundTriggerService"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00e5 }
                r4.<init>()     // Catch:{ all -> 0x00e5 }
                r4.append(r8)     // Catch:{ all -> 0x00e5 }
                java.lang.String r5 = " is not loaded"
                r4.append(r5)     // Catch:{ all -> 0x00e5 }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00e5 }
                android.util.Slog.e(r3, r4)     // Catch:{ all -> 0x00e5 }
                com.android.server.soundtrigger.SoundTriggerLogger r3 = com.android.server.soundtrigger.SoundTriggerService.sEventLogger     // Catch:{ all -> 0x00e5 }
                com.android.server.soundtrigger.SoundTriggerLogger$StringEvent r4 = new com.android.server.soundtrigger.SoundTriggerLogger$StringEvent     // Catch:{ all -> 0x00e5 }
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00e5 }
                r5.<init>()     // Catch:{ all -> 0x00e5 }
                java.lang.String r6 = "getModelState(): "
                r5.append(r6)     // Catch:{ all -> 0x00e5 }
                r5.append(r8)     // Catch:{ all -> 0x00e5 }
                java.lang.String r6 = " is not loaded"
                r5.append(r6)     // Catch:{ all -> 0x00e5 }
                java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00e5 }
                r4.<init>(r5)     // Catch:{ all -> 0x00e5 }
                r3.log(r4)     // Catch:{ all -> 0x00e5 }
                monitor-exit(r1)     // Catch:{ all -> 0x00e5 }
                return r0
            L_0x0098:
                int r3 = r2.type     // Catch:{ all -> 0x00e5 }
                r4 = 1
                if (r3 == r4) goto L_0x00d5
                java.lang.String r3 = "SoundTriggerService"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00e5 }
                r4.<init>()     // Catch:{ all -> 0x00e5 }
                java.lang.String r5 = "Unsupported model type, "
                r4.append(r5)     // Catch:{ all -> 0x00e5 }
                int r5 = r2.type     // Catch:{ all -> 0x00e5 }
                r4.append(r5)     // Catch:{ all -> 0x00e5 }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00e5 }
                android.util.Slog.e(r3, r4)     // Catch:{ all -> 0x00e5 }
                com.android.server.soundtrigger.SoundTriggerLogger r3 = com.android.server.soundtrigger.SoundTriggerService.sEventLogger     // Catch:{ all -> 0x00e5 }
                com.android.server.soundtrigger.SoundTriggerLogger$StringEvent r4 = new com.android.server.soundtrigger.SoundTriggerLogger$StringEvent     // Catch:{ all -> 0x00e5 }
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00e5 }
                r5.<init>()     // Catch:{ all -> 0x00e5 }
                java.lang.String r6 = "getModelState(): Unsupported model type, "
                r5.append(r6)     // Catch:{ all -> 0x00e5 }
                int r6 = r2.type     // Catch:{ all -> 0x00e5 }
                r5.append(r6)     // Catch:{ all -> 0x00e5 }
                java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00e5 }
                r4.<init>(r5)     // Catch:{ all -> 0x00e5 }
                r3.log(r4)     // Catch:{ all -> 0x00e5 }
                goto L_0x00e3
            L_0x00d5:
                com.android.server.soundtrigger.SoundTriggerService r3 = com.android.server.soundtrigger.SoundTriggerService.this     // Catch:{ all -> 0x00e5 }
                com.android.server.soundtrigger.SoundTriggerHelper r3 = r3.mSoundTriggerHelper     // Catch:{ all -> 0x00e5 }
                java.util.UUID r4 = r2.uuid     // Catch:{ all -> 0x00e5 }
                int r3 = r3.getGenericModelState(r4)     // Catch:{ all -> 0x00e5 }
                r0 = r3
            L_0x00e3:
                monitor-exit(r1)     // Catch:{ all -> 0x00e5 }
                return r0
            L_0x00e5:
                r2 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x00e5 }
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.soundtrigger.SoundTriggerService.SoundTriggerServiceStub.getModelState(android.os.ParcelUuid):int");
        }
    }

    private static class NumOps {
        @GuardedBy({"mLock"})
        private long mLastOpsHourSinceBoot;
        private final Object mLock;
        @GuardedBy({"mLock"})
        private int[] mNumOps;

        private NumOps() {
            this.mLock = new Object();
            this.mNumOps = new int[24];
        }

        /* access modifiers changed from: package-private */
        public void clearOldOps(long currentTime) {
            synchronized (this.mLock) {
                long numHoursSinceBoot = TimeUnit.HOURS.convert(currentTime, TimeUnit.NANOSECONDS);
                if (this.mLastOpsHourSinceBoot != 0) {
                    for (long hour = this.mLastOpsHourSinceBoot + 1; hour <= numHoursSinceBoot; hour++) {
                        this.mNumOps[(int) (hour % 24)] = 0;
                    }
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void addOp(long currentTime) {
            synchronized (this.mLock) {
                long numHoursSinceBoot = TimeUnit.HOURS.convert(currentTime, TimeUnit.NANOSECONDS);
                int[] iArr = this.mNumOps;
                int i = (int) (numHoursSinceBoot % 24);
                iArr[i] = iArr[i] + 1;
                this.mLastOpsHourSinceBoot = numHoursSinceBoot;
            }
        }

        /* access modifiers changed from: package-private */
        public int getOpsAdded() {
            int totalOperationsInLastDay;
            synchronized (this.mLock) {
                totalOperationsInLastDay = 0;
                for (int i = 0; i < 24; i++) {
                    totalOperationsInLastDay += this.mNumOps[i];
                }
            }
            return totalOperationsInLastDay;
        }
    }

    private static class Operation {
        private final Runnable mDropOp;
        private final ExecuteOp mExecuteOp;
        private final Runnable mSetupOp;

        private interface ExecuteOp {
            void run(int i, ISoundTriggerDetectionService iSoundTriggerDetectionService) throws RemoteException;
        }

        private Operation(Runnable setupOp, ExecuteOp executeOp, Runnable cancelOp) {
            this.mSetupOp = setupOp;
            this.mExecuteOp = executeOp;
            this.mDropOp = cancelOp;
        }

        private void setup() {
            Runnable runnable = this.mSetupOp;
            if (runnable != null) {
                runnable.run();
            }
        }

        /* access modifiers changed from: package-private */
        public void run(int opId, ISoundTriggerDetectionService service) throws RemoteException {
            setup();
            this.mExecuteOp.run(opId, service);
        }

        /* access modifiers changed from: package-private */
        public void drop() {
            setup();
            Runnable runnable = this.mDropOp;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    private class RemoteSoundTriggerDetectionService extends IRecognitionStatusCallback.Stub implements ServiceConnection {
        private static final int MSG_STOP_ALL_PENDING_OPERATIONS = 1;
        private final ISoundTriggerDetectionServiceClient mClient;
        /* access modifiers changed from: private */
        @GuardedBy({"mRemoteServiceLock"})
        public boolean mDestroyOnceRunningOpsDone;
        private final Handler mHandler;
        @GuardedBy({"mRemoteServiceLock"})
        private boolean mIsBound;
        @GuardedBy({"mRemoteServiceLock"})
        private boolean mIsDestroyed;
        private final NumOps mNumOps;
        @GuardedBy({"mRemoteServiceLock"})
        private int mNumTotalOpsPerformed;
        private final Bundle mParams;
        /* access modifiers changed from: private */
        @GuardedBy({"mRemoteServiceLock"})
        public final ArrayList<Operation> mPendingOps = new ArrayList<>();
        private final ParcelUuid mPuuid;
        private final SoundTrigger.RecognitionConfig mRecognitionConfig;
        /* access modifiers changed from: private */
        public final Object mRemoteServiceLock = new Object();
        private final PowerManager.WakeLock mRemoteServiceWakeLock;
        /* access modifiers changed from: private */
        @GuardedBy({"mRemoteServiceLock"})
        public final ArraySet<Integer> mRunningOpIds = new ArraySet<>();
        @GuardedBy({"mRemoteServiceLock"})
        private ISoundTriggerDetectionService mService;
        private final ComponentName mServiceName;
        private final UserHandle mUser;

        public RemoteSoundTriggerDetectionService(UUID modelUuid, Bundle params, ComponentName serviceName, UserHandle user, SoundTrigger.RecognitionConfig config) {
            this.mPuuid = new ParcelUuid(modelUuid);
            this.mParams = params;
            this.mServiceName = serviceName;
            this.mUser = user;
            this.mRecognitionConfig = config;
            this.mHandler = new Handler(Looper.getMainLooper());
            this.mRemoteServiceWakeLock = ((PowerManager) SoundTriggerService.this.mContext.getSystemService("power")).newWakeLock(1, "RemoteSoundTriggerDetectionService " + this.mServiceName.getPackageName() + ":" + this.mServiceName.getClassName());
            synchronized (SoundTriggerService.this.mLock) {
                NumOps numOps = (NumOps) SoundTriggerService.this.mNumOpsPerPackage.get(this.mServiceName.getPackageName());
                if (numOps == null) {
                    numOps = new NumOps();
                    SoundTriggerService.this.mNumOpsPerPackage.put(this.mServiceName.getPackageName(), numOps);
                }
                this.mNumOps = numOps;
            }
            this.mClient = new ISoundTriggerDetectionServiceClient.Stub(SoundTriggerService.this) {
                /* Debug info: failed to restart local var, previous not found, register: 5 */
                public void onOpFinished(int opId) {
                    long token = Binder.clearCallingIdentity();
                    try {
                        synchronized (RemoteSoundTriggerDetectionService.this.mRemoteServiceLock) {
                            RemoteSoundTriggerDetectionService.this.mRunningOpIds.remove(Integer.valueOf(opId));
                            if (RemoteSoundTriggerDetectionService.this.mRunningOpIds.isEmpty() && RemoteSoundTriggerDetectionService.this.mPendingOps.isEmpty()) {
                                if (RemoteSoundTriggerDetectionService.this.mDestroyOnceRunningOpsDone) {
                                    RemoteSoundTriggerDetectionService.this.destroy();
                                } else {
                                    RemoteSoundTriggerDetectionService.this.disconnectLocked();
                                }
                            }
                        }
                        Binder.restoreCallingIdentity(token);
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(token);
                        throw th;
                    }
                }
            };
        }

        public boolean pingBinder() {
            return !this.mIsDestroyed && !this.mDestroyOnceRunningOpsDone;
        }

        /* access modifiers changed from: private */
        @GuardedBy({"mRemoteServiceLock"})
        public void disconnectLocked() {
            ISoundTriggerDetectionService iSoundTriggerDetectionService = this.mService;
            if (iSoundTriggerDetectionService != null) {
                try {
                    iSoundTriggerDetectionService.removeClient(this.mPuuid);
                } catch (Exception e) {
                    Slog.e(SoundTriggerService.TAG, this.mPuuid + ": Cannot remove client", e);
                    SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
                    access$200.log(new SoundTriggerLogger.StringEvent(this.mPuuid + ": Cannot remove client"));
                }
                this.mService = null;
            }
            if (this.mIsBound) {
                SoundTriggerService.this.mContext.unbindService(this);
                this.mIsBound = false;
                synchronized (SoundTriggerService.this.mCallbacksLock) {
                    this.mRemoteServiceWakeLock.release();
                }
            }
        }

        /* access modifiers changed from: private */
        public void destroy() {
            Slog.v(SoundTriggerService.TAG, this.mPuuid + ": destroy");
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent(this.mPuuid + ": destroy"));
            synchronized (this.mRemoteServiceLock) {
                disconnectLocked();
                this.mIsDestroyed = true;
            }
            if (!this.mDestroyOnceRunningOpsDone) {
                synchronized (SoundTriggerService.this.mCallbacksLock) {
                    SoundTriggerService.this.mCallbacks.remove(this.mPuuid.getUuid());
                }
            }
        }

        /* access modifiers changed from: private */
        public void stopAllPendingOperations() {
            synchronized (this.mRemoteServiceLock) {
                if (!this.mIsDestroyed) {
                    if (this.mService != null) {
                        int numOps = this.mRunningOpIds.size();
                        for (int i = 0; i < numOps; i++) {
                            try {
                                this.mService.onStopOperation(this.mPuuid, this.mRunningOpIds.valueAt(i).intValue());
                            } catch (Exception e) {
                                Slog.e(SoundTriggerService.TAG, this.mPuuid + ": Could not stop operation " + this.mRunningOpIds.valueAt(i), e);
                                SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
                                access$200.log(new SoundTriggerLogger.StringEvent(this.mPuuid + ": Could not stop operation " + this.mRunningOpIds.valueAt(i)));
                            }
                        }
                        this.mRunningOpIds.clear();
                    }
                    disconnectLocked();
                }
            }
        }

        private void bind() {
            long token = Binder.clearCallingIdentity();
            try {
                Intent i = new Intent();
                i.setComponent(this.mServiceName);
                ResolveInfo ri = SoundTriggerService.this.mContext.getPackageManager().resolveServiceAsUser(i, 268435588, this.mUser.getIdentifier());
                if (ri == null) {
                    Slog.w(SoundTriggerService.TAG, this.mPuuid + ": " + this.mServiceName + " not found");
                    SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
                    access$200.log(new SoundTriggerLogger.StringEvent(this.mPuuid + ": " + this.mServiceName + " not found"));
                } else if (!"android.permission.BIND_SOUND_TRIGGER_DETECTION_SERVICE".equals(ri.serviceInfo.permission)) {
                    Slog.w(SoundTriggerService.TAG, this.mPuuid + ": " + this.mServiceName + " does not require " + "android.permission.BIND_SOUND_TRIGGER_DETECTION_SERVICE");
                    SoundTriggerLogger access$2002 = SoundTriggerService.sEventLogger;
                    access$2002.log(new SoundTriggerLogger.StringEvent(this.mPuuid + ": " + this.mServiceName + " does not require " + "android.permission.BIND_SOUND_TRIGGER_DETECTION_SERVICE"));
                    Binder.restoreCallingIdentity(token);
                } else {
                    this.mIsBound = SoundTriggerService.this.mContext.bindServiceAsUser(i, this, 67108865, this.mUser);
                    if (this.mIsBound) {
                        this.mRemoteServiceWakeLock.acquire();
                    } else {
                        Slog.w(SoundTriggerService.TAG, this.mPuuid + ": Could not bind to " + this.mServiceName);
                        SoundTriggerLogger access$2003 = SoundTriggerService.sEventLogger;
                        access$2003.log(new SoundTriggerLogger.StringEvent(this.mPuuid + ": Could not bind to " + this.mServiceName));
                    }
                    Binder.restoreCallingIdentity(token);
                }
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:33:0x0128, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void runOrAddOperation(com.android.server.soundtrigger.SoundTriggerService.Operation r13) {
            /*
                r12 = this;
                java.lang.Object r0 = r12.mRemoteServiceLock
                monitor-enter(r0)
                boolean r1 = r12.mIsDestroyed     // Catch:{ all -> 0x0165 }
                if (r1 != 0) goto L_0x0129
                boolean r1 = r12.mDestroyOnceRunningOpsDone     // Catch:{ all -> 0x0165 }
                if (r1 == 0) goto L_0x000d
                goto L_0x0129
            L_0x000d:
                android.media.soundtrigger.ISoundTriggerDetectionService r1 = r12.mService     // Catch:{ all -> 0x0165 }
                if (r1 != 0) goto L_0x001f
                java.util.ArrayList<com.android.server.soundtrigger.SoundTriggerService$Operation> r1 = r12.mPendingOps     // Catch:{ all -> 0x0165 }
                r1.add(r13)     // Catch:{ all -> 0x0165 }
                boolean r1 = r12.mIsBound     // Catch:{ all -> 0x0165 }
                if (r1 != 0) goto L_0x0127
                r12.bind()     // Catch:{ all -> 0x0165 }
                goto L_0x0127
            L_0x001f:
                long r1 = java.lang.System.nanoTime()     // Catch:{ all -> 0x0165 }
                com.android.server.soundtrigger.SoundTriggerService$NumOps r3 = r12.mNumOps     // Catch:{ all -> 0x0165 }
                r3.clearOldOps(r1)     // Catch:{ all -> 0x0165 }
                com.android.server.soundtrigger.SoundTriggerService r3 = com.android.server.soundtrigger.SoundTriggerService.this     // Catch:{ all -> 0x0165 }
                android.content.Context r3 = r3.mContext     // Catch:{ all -> 0x0165 }
                android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ all -> 0x0165 }
                java.lang.String r4 = "max_sound_trigger_detection_service_ops_per_day"
                r5 = 2147483647(0x7fffffff, float:NaN)
                int r3 = android.provider.Settings.Global.getInt(r3, r4, r5)     // Catch:{ all -> 0x0165 }
                com.android.server.soundtrigger.SoundTriggerService$NumOps r4 = r12.mNumOps     // Catch:{ all -> 0x0165 }
                int r4 = r4.getOpsAdded()     // Catch:{ all -> 0x0165 }
                com.android.server.soundtrigger.SoundTriggerService$NumOps r5 = r12.mNumOps     // Catch:{ all -> 0x0165 }
                r5.addOp(r1)     // Catch:{ all -> 0x0165 }
                int r5 = r12.mNumTotalOpsPerformed     // Catch:{ all -> 0x0165 }
            L_0x0047:
                int r6 = r12.mNumTotalOpsPerformed     // Catch:{ all -> 0x0165 }
                r7 = 1
                int r6 = r6 + r7
                r12.mNumTotalOpsPerformed = r6     // Catch:{ all -> 0x0165 }
                android.util.ArraySet<java.lang.Integer> r6 = r12.mRunningOpIds     // Catch:{ all -> 0x0165 }
                java.lang.Integer r8 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x0165 }
                boolean r6 = r6.contains(r8)     // Catch:{ all -> 0x0165 }
                if (r6 != 0) goto L_0x0047
                java.lang.String r6 = "SoundTriggerService"
                java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00a5 }
                r8.<init>()     // Catch:{ Exception -> 0x00a5 }
                android.os.ParcelUuid r9 = r12.mPuuid     // Catch:{ Exception -> 0x00a5 }
                r8.append(r9)     // Catch:{ Exception -> 0x00a5 }
                java.lang.String r9 = ": runOp "
                r8.append(r9)     // Catch:{ Exception -> 0x00a5 }
                r8.append(r5)     // Catch:{ Exception -> 0x00a5 }
                java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x00a5 }
                android.util.Slog.v(r6, r8)     // Catch:{ Exception -> 0x00a5 }
                com.android.server.soundtrigger.SoundTriggerLogger r6 = com.android.server.soundtrigger.SoundTriggerService.sEventLogger     // Catch:{ Exception -> 0x00a5 }
                com.android.server.soundtrigger.SoundTriggerLogger$StringEvent r8 = new com.android.server.soundtrigger.SoundTriggerLogger$StringEvent     // Catch:{ Exception -> 0x00a5 }
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00a5 }
                r9.<init>()     // Catch:{ Exception -> 0x00a5 }
                android.os.ParcelUuid r10 = r12.mPuuid     // Catch:{ Exception -> 0x00a5 }
                r9.append(r10)     // Catch:{ Exception -> 0x00a5 }
                java.lang.String r10 = ": runOp "
                r9.append(r10)     // Catch:{ Exception -> 0x00a5 }
                r9.append(r5)     // Catch:{ Exception -> 0x00a5 }
                java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x00a5 }
                r8.<init>(r9)     // Catch:{ Exception -> 0x00a5 }
                r6.log(r8)     // Catch:{ Exception -> 0x00a5 }
                android.media.soundtrigger.ISoundTriggerDetectionService r6 = r12.mService     // Catch:{ Exception -> 0x00a5 }
                r13.run(r5, r6)     // Catch:{ Exception -> 0x00a5 }
                android.util.ArraySet<java.lang.Integer> r6 = r12.mRunningOpIds     // Catch:{ Exception -> 0x00a5 }
                java.lang.Integer r8 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x00a5 }
                r6.add(r8)     // Catch:{ Exception -> 0x00a5 }
                goto L_0x00e3
            L_0x00a5:
                r6 = move-exception
                java.lang.String r8 = "SoundTriggerService"
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x0165 }
                r9.<init>()     // Catch:{ all -> 0x0165 }
                android.os.ParcelUuid r10 = r12.mPuuid     // Catch:{ all -> 0x0165 }
                r9.append(r10)     // Catch:{ all -> 0x0165 }
                java.lang.String r10 = ": Could not run operation "
                r9.append(r10)     // Catch:{ all -> 0x0165 }
                r9.append(r5)     // Catch:{ all -> 0x0165 }
                java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x0165 }
                android.util.Slog.e(r8, r9, r6)     // Catch:{ all -> 0x0165 }
                com.android.server.soundtrigger.SoundTriggerLogger r8 = com.android.server.soundtrigger.SoundTriggerService.sEventLogger     // Catch:{ all -> 0x0165 }
                com.android.server.soundtrigger.SoundTriggerLogger$StringEvent r9 = new com.android.server.soundtrigger.SoundTriggerLogger$StringEvent     // Catch:{ all -> 0x0165 }
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x0165 }
                r10.<init>()     // Catch:{ all -> 0x0165 }
                android.os.ParcelUuid r11 = r12.mPuuid     // Catch:{ all -> 0x0165 }
                r10.append(r11)     // Catch:{ all -> 0x0165 }
                java.lang.String r11 = ": Could not run operation "
                r10.append(r11)     // Catch:{ all -> 0x0165 }
                r10.append(r5)     // Catch:{ all -> 0x0165 }
                java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x0165 }
                r9.<init>(r10)     // Catch:{ all -> 0x0165 }
                r8.log(r9)     // Catch:{ all -> 0x0165 }
            L_0x00e3:
                java.util.ArrayList<com.android.server.soundtrigger.SoundTriggerService$Operation> r5 = r12.mPendingOps     // Catch:{ all -> 0x0165 }
                boolean r5 = r5.isEmpty()     // Catch:{ all -> 0x0165 }
                if (r5 == 0) goto L_0x00ff
                android.util.ArraySet<java.lang.Integer> r5 = r12.mRunningOpIds     // Catch:{ all -> 0x0165 }
                boolean r5 = r5.isEmpty()     // Catch:{ all -> 0x0165 }
                if (r5 == 0) goto L_0x00ff
                boolean r5 = r12.mDestroyOnceRunningOpsDone     // Catch:{ all -> 0x0165 }
                if (r5 == 0) goto L_0x00fb
                r12.destroy()     // Catch:{ all -> 0x0165 }
                goto L_0x0127
            L_0x00fb:
                r12.disconnectLocked()     // Catch:{ all -> 0x0165 }
                goto L_0x0127
            L_0x00ff:
                android.os.Handler r5 = r12.mHandler     // Catch:{ all -> 0x0165 }
                r5.removeMessages(r7)     // Catch:{ all -> 0x0165 }
                android.os.Handler r5 = r12.mHandler     // Catch:{ all -> 0x0165 }
                com.android.server.soundtrigger.-$$Lambda$SoundTriggerService$RemoteSoundTriggerDetectionService$wfDlqQ7aPvu9qZCZ24jJu4tfUMY r6 = com.android.server.soundtrigger.$$Lambda$SoundTriggerService$RemoteSoundTriggerDetectionService$wfDlqQ7aPvu9qZCZ24jJu4tfUMY.INSTANCE     // Catch:{ all -> 0x0165 }
                android.os.Message r6 = com.android.internal.util.function.pooled.PooledLambda.obtainMessage(r6, r12)     // Catch:{ all -> 0x0165 }
                android.os.Message r6 = r6.setWhat(r7)     // Catch:{ all -> 0x0165 }
                com.android.server.soundtrigger.SoundTriggerService r7 = com.android.server.soundtrigger.SoundTriggerService.this     // Catch:{ all -> 0x0165 }
                android.content.Context r7 = r7.mContext     // Catch:{ all -> 0x0165 }
                android.content.ContentResolver r7 = r7.getContentResolver()     // Catch:{ all -> 0x0165 }
                java.lang.String r8 = "sound_trigger_detection_service_op_timeout"
                r9 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
                long r7 = android.provider.Settings.Global.getLong(r7, r8, r9)     // Catch:{ all -> 0x0165 }
                r5.sendMessageDelayed(r6, r7)     // Catch:{ all -> 0x0165 }
            L_0x0127:
                monitor-exit(r0)     // Catch:{ all -> 0x0165 }
                return
            L_0x0129:
                java.lang.String r1 = "SoundTriggerService"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0165 }
                r2.<init>()     // Catch:{ all -> 0x0165 }
                android.os.ParcelUuid r3 = r12.mPuuid     // Catch:{ all -> 0x0165 }
                r2.append(r3)     // Catch:{ all -> 0x0165 }
                java.lang.String r3 = ": Dropped operation as already destroyed or marked for destruction"
                r2.append(r3)     // Catch:{ all -> 0x0165 }
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0165 }
                android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x0165 }
                com.android.server.soundtrigger.SoundTriggerLogger r1 = com.android.server.soundtrigger.SoundTriggerService.sEventLogger     // Catch:{ all -> 0x0165 }
                com.android.server.soundtrigger.SoundTriggerLogger$StringEvent r2 = new com.android.server.soundtrigger.SoundTriggerLogger$StringEvent     // Catch:{ all -> 0x0165 }
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0165 }
                r3.<init>()     // Catch:{ all -> 0x0165 }
                android.os.ParcelUuid r4 = r12.mPuuid     // Catch:{ all -> 0x0165 }
                r3.append(r4)     // Catch:{ all -> 0x0165 }
                java.lang.String r4 = ":Dropped operation as already destroyed or marked for destruction"
                r3.append(r4)     // Catch:{ all -> 0x0165 }
                java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0165 }
                r2.<init>(r3)     // Catch:{ all -> 0x0165 }
                r1.log(r2)     // Catch:{ all -> 0x0165 }
                r13.drop()     // Catch:{ all -> 0x0165 }
                monitor-exit(r0)     // Catch:{ all -> 0x0165 }
                return
            L_0x0165:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0165 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.soundtrigger.SoundTriggerService.RemoteSoundTriggerDetectionService.runOrAddOperation(com.android.server.soundtrigger.SoundTriggerService$Operation):void");
        }

        public void onKeyphraseDetected(SoundTrigger.KeyphraseRecognitionEvent event) {
            Slog.w(SoundTriggerService.TAG, this.mPuuid + "->" + this.mServiceName + ": IGNORED onKeyphraseDetected(" + event + ")");
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent(this.mPuuid + "->" + this.mServiceName + ": IGNORED onKeyphraseDetected(" + event + ")"));
        }

        private AudioRecord createAudioRecordForEvent(SoundTrigger.GenericRecognitionEvent event) {
            int i;
            int i2;
            AudioAttributes.Builder attributesBuilder = new AudioAttributes.Builder();
            attributesBuilder.setInternalCapturePreset(IdentifierType.VENDOR_END);
            AudioAttributes attributes = attributesBuilder.build();
            AudioFormat originalFormat = event.getCaptureFormat();
            AudioFormat captureFormat = new AudioFormat.Builder().setChannelMask(originalFormat.getChannelMask()).setEncoding(originalFormat.getEncoding()).setSampleRate(originalFormat.getSampleRate()).build();
            if (captureFormat.getSampleRate() == 0) {
                i = 192000;
            } else {
                i = captureFormat.getSampleRate();
            }
            if (captureFormat.getChannelCount() == 2) {
                i2 = 12;
            } else {
                i2 = 16;
            }
            int bufferSize = AudioRecord.getMinBufferSize(i, i2, captureFormat.getEncoding());
            SoundTriggerService.sEventLogger.log(new SoundTriggerLogger.StringEvent("createAudioRecordForEvent"));
            return new AudioRecord(attributes, captureFormat, bufferSize, event.getCaptureSession());
        }

        public void onGenericSoundTriggerDetected(SoundTrigger.GenericRecognitionEvent event) {
            Slog.v(SoundTriggerService.TAG, this.mPuuid + ": Generic sound trigger event: " + event);
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent(this.mPuuid + ": Generic sound trigger event: " + event));
            runOrAddOperation(new Operation(new Runnable() {
                public final void run() {
                    SoundTriggerService.RemoteSoundTriggerDetectionService.this.lambda$onGenericSoundTriggerDetected$0$SoundTriggerService$RemoteSoundTriggerDetectionService();
                }
            }, new Operation.ExecuteOp(event) {
                private final /* synthetic */ SoundTrigger.GenericRecognitionEvent f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(int i, ISoundTriggerDetectionService iSoundTriggerDetectionService) {
                    SoundTriggerService.RemoteSoundTriggerDetectionService.this.lambda$onGenericSoundTriggerDetected$1$SoundTriggerService$RemoteSoundTriggerDetectionService(this.f$1, i, iSoundTriggerDetectionService);
                }
            }, new Runnable(event) {
                private final /* synthetic */ SoundTrigger.GenericRecognitionEvent f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SoundTriggerService.RemoteSoundTriggerDetectionService.this.lambda$onGenericSoundTriggerDetected$2$SoundTriggerService$RemoteSoundTriggerDetectionService(this.f$1);
                }
            }));
        }

        public /* synthetic */ void lambda$onGenericSoundTriggerDetected$0$SoundTriggerService$RemoteSoundTriggerDetectionService() {
            if (!this.mRecognitionConfig.allowMultipleTriggers) {
                synchronized (SoundTriggerService.this.mCallbacksLock) {
                    SoundTriggerService.this.mCallbacks.remove(this.mPuuid.getUuid());
                }
                this.mDestroyOnceRunningOpsDone = true;
            }
        }

        public /* synthetic */ void lambda$onGenericSoundTriggerDetected$1$SoundTriggerService$RemoteSoundTriggerDetectionService(SoundTrigger.GenericRecognitionEvent event, int opId, ISoundTriggerDetectionService service) throws RemoteException {
            service.onGenericRecognitionEvent(this.mPuuid, opId, event);
        }

        public /* synthetic */ void lambda$onGenericSoundTriggerDetected$2$SoundTriggerService$RemoteSoundTriggerDetectionService(SoundTrigger.GenericRecognitionEvent event) {
            if (event.isCaptureAvailable()) {
                AudioRecord capturedData = createAudioRecordForEvent(event);
                capturedData.startRecording();
                capturedData.release();
            }
        }

        public void onError(int status) {
            Slog.v(SoundTriggerService.TAG, this.mPuuid + ": onError: " + status);
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent(this.mPuuid + ": onError: " + status));
            runOrAddOperation(new Operation(new Runnable() {
                public final void run() {
                    SoundTriggerService.RemoteSoundTriggerDetectionService.this.lambda$onError$3$SoundTriggerService$RemoteSoundTriggerDetectionService();
                }
            }, new Operation.ExecuteOp(status) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(int i, ISoundTriggerDetectionService iSoundTriggerDetectionService) {
                    SoundTriggerService.RemoteSoundTriggerDetectionService.this.lambda$onError$4$SoundTriggerService$RemoteSoundTriggerDetectionService(this.f$1, i, iSoundTriggerDetectionService);
                }
            }, (Runnable) null));
        }

        public /* synthetic */ void lambda$onError$3$SoundTriggerService$RemoteSoundTriggerDetectionService() {
            synchronized (SoundTriggerService.this.mCallbacksLock) {
                SoundTriggerService.this.mCallbacks.remove(this.mPuuid.getUuid());
            }
            this.mDestroyOnceRunningOpsDone = true;
        }

        public /* synthetic */ void lambda$onError$4$SoundTriggerService$RemoteSoundTriggerDetectionService(int status, int opId, ISoundTriggerDetectionService service) throws RemoteException {
            service.onError(this.mPuuid, opId, status);
        }

        public void onRecognitionPaused() {
            Slog.i(SoundTriggerService.TAG, this.mPuuid + "->" + this.mServiceName + ": IGNORED onRecognitionPaused");
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent(this.mPuuid + "->" + this.mServiceName + ": IGNORED onRecognitionPaused"));
        }

        public void onRecognitionResumed() {
            Slog.i(SoundTriggerService.TAG, this.mPuuid + "->" + this.mServiceName + ": IGNORED onRecognitionResumed");
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent(this.mPuuid + "->" + this.mServiceName + ": IGNORED onRecognitionResumed"));
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Slog.v(SoundTriggerService.TAG, this.mPuuid + ": onServiceConnected(" + service + ")");
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent(this.mPuuid + ": onServiceConnected(" + service + ")"));
            synchronized (this.mRemoteServiceLock) {
                this.mService = ISoundTriggerDetectionService.Stub.asInterface(service);
                try {
                    this.mService.setClient(this.mPuuid, this.mParams, this.mClient);
                    while (!this.mPendingOps.isEmpty()) {
                        runOrAddOperation(this.mPendingOps.remove(0));
                    }
                } catch (Exception e) {
                    Slog.e(SoundTriggerService.TAG, this.mPuuid + ": Could not init " + this.mServiceName, e);
                }
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            Slog.v(SoundTriggerService.TAG, this.mPuuid + ": onServiceDisconnected");
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent(this.mPuuid + ": onServiceDisconnected"));
            synchronized (this.mRemoteServiceLock) {
                this.mService = null;
            }
        }

        public void onBindingDied(ComponentName name) {
            Slog.v(SoundTriggerService.TAG, this.mPuuid + ": onBindingDied");
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent(this.mPuuid + ": onBindingDied"));
            synchronized (this.mRemoteServiceLock) {
                destroy();
            }
        }

        public void onNullBinding(ComponentName name) {
            Slog.w(SoundTriggerService.TAG, name + " for model " + this.mPuuid + " returned a null binding");
            SoundTriggerLogger access$200 = SoundTriggerService.sEventLogger;
            access$200.log(new SoundTriggerLogger.StringEvent(name + " for model " + this.mPuuid + " returned a null binding"));
            synchronized (this.mRemoteServiceLock) {
                disconnectLocked();
            }
        }
    }

    public final class LocalSoundTriggerService extends SoundTriggerInternal {
        private final Context mContext;
        private SoundTriggerHelper mSoundTriggerHelper;

        LocalSoundTriggerService(Context context) {
            this.mContext = context;
        }

        /* access modifiers changed from: package-private */
        public synchronized void setSoundTriggerHelper(SoundTriggerHelper helper) {
            this.mSoundTriggerHelper = helper;
        }

        public int startRecognition(int keyphraseId, SoundTrigger.KeyphraseSoundModel soundModel, IRecognitionStatusCallback listener, SoundTrigger.RecognitionConfig recognitionConfig) {
            if (!isInitialized()) {
                return Integer.MIN_VALUE;
            }
            return this.mSoundTriggerHelper.startKeyphraseRecognition(keyphraseId, soundModel, listener, recognitionConfig);
        }

        public synchronized int stopRecognition(int keyphraseId, IRecognitionStatusCallback listener) {
            if (!isInitialized()) {
                return Integer.MIN_VALUE;
            }
            return this.mSoundTriggerHelper.stopKeyphraseRecognition(keyphraseId, listener);
        }

        public SoundTrigger.ModuleProperties getModuleProperties() {
            if (!isInitialized()) {
                return null;
            }
            return this.mSoundTriggerHelper.getModuleProperties();
        }

        public int unloadKeyphraseModel(int keyphraseId) {
            if (!isInitialized()) {
                return Integer.MIN_VALUE;
            }
            return this.mSoundTriggerHelper.unloadKeyphraseSoundModel(keyphraseId);
        }

        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            if (isInitialized()) {
                this.mSoundTriggerHelper.dump(fd, pw, args);
                SoundTriggerService.sEventLogger.dump(pw);
            }
        }

        private synchronized boolean isInitialized() {
            if (this.mSoundTriggerHelper != null) {
                return true;
            }
            Slog.e(SoundTriggerService.TAG, "SoundTriggerHelper not initialized.");
            SoundTriggerService.sEventLogger.log(new SoundTriggerLogger.StringEvent("SoundTriggerHelper not initialized."));
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void enforceCallingPermission(String permission) {
        if (this.mContext.checkCallingOrSelfPermission(permission) != 0) {
            throw new SecurityException("Caller does not hold the permission " + permission);
        }
    }
}
