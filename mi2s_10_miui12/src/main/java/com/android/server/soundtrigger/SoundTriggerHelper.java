package com.android.server.soundtrigger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.soundtrigger.IRecognitionStatusCallback;
import android.hardware.soundtrigger.SoundTrigger;
import android.hardware.soundtrigger.SoundTriggerModule;
import android.os.Binder;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.PowerManager;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Slog;
import com.android.internal.logging.MetricsLogger;
import com.android.server.am.ProcessPolicy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class SoundTriggerHelper implements SoundTrigger.StatusListener {
    static final boolean DBG = false;
    private static final int INVALID_VALUE = Integer.MIN_VALUE;
    public static final int STATUS_ERROR = Integer.MIN_VALUE;
    public static final int STATUS_OK = 0;
    static final String TAG = "SoundTriggerHelper";
    private boolean mCallActive = false;
    private final Context mContext;
    private boolean mIsPowerSaveMode = false;
    private HashMap<Integer, UUID> mKeyphraseUuidMap;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private final HashMap<UUID, ModelData> mModelDataMap;
    private SoundTriggerModule mModule;
    final SoundTrigger.ModuleProperties mModuleProperties;
    private final PhoneStateListener mPhoneStateListener;
    /* access modifiers changed from: private */
    public final PowerManager mPowerManager;
    private PowerSaveModeListener mPowerSaveModeListener;
    private boolean mRecognitionRunning = false;
    private boolean mServiceDisabled = false;
    private final TelephonyManager mTelephonyManager;

    SoundTriggerHelper(Context context) {
        ArrayList<SoundTrigger.ModuleProperties> modules = new ArrayList<>();
        int status = SoundTrigger.listModules(modules);
        this.mContext = context;
        this.mTelephonyManager = (TelephonyManager) context.getSystemService("phone");
        this.mPowerManager = (PowerManager) context.getSystemService("power");
        this.mModelDataMap = new HashMap<>();
        this.mKeyphraseUuidMap = new HashMap<>();
        this.mPhoneStateListener = new MyCallStateListener();
        if (status != 0 || modules.size() == 0) {
            Slog.w(TAG, "listModules status=" + status + ", # of modules=" + modules.size());
            this.mModuleProperties = null;
            this.mModule = null;
            return;
        }
        this.mModuleProperties = modules.get(0);
    }

    /* access modifiers changed from: package-private */
    public int startGenericRecognition(UUID modelId, SoundTrigger.GenericSoundModel soundModel, IRecognitionStatusCallback callback, SoundTrigger.RecognitionConfig recognitionConfig) {
        MetricsLogger.count(this.mContext, "sth_start_recognition", 1);
        if (modelId == null || soundModel == null || callback == null || recognitionConfig == null) {
            Slog.w(TAG, "Passed in bad data to startGenericRecognition().");
            return Integer.MIN_VALUE;
        }
        synchronized (this.mLock) {
            ModelData modelData = getOrCreateGenericModelDataLocked(modelId);
            if (modelData == null) {
                Slog.w(TAG, "Irrecoverable error occurred, check UUID / sound model data.");
                return Integer.MIN_VALUE;
            }
            int startRecognition = startRecognition(soundModel, modelData, callback, recognitionConfig, Integer.MIN_VALUE);
            return startRecognition;
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x005c, code lost:
        return Integer.MIN_VALUE;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int startKeyphraseRecognition(int r9, android.hardware.soundtrigger.SoundTrigger.KeyphraseSoundModel r10, android.hardware.soundtrigger.IRecognitionStatusCallback r11, android.hardware.soundtrigger.SoundTrigger.RecognitionConfig r12) {
        /*
            r8 = this;
            java.lang.Object r0 = r8.mLock
            monitor-enter(r0)
            android.content.Context r1 = r8.mContext     // Catch:{ all -> 0x005d }
            java.lang.String r2 = "sth_start_recognition"
            r3 = 1
            com.android.internal.logging.MetricsLogger.count(r1, r2, r3)     // Catch:{ all -> 0x005d }
            r1 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r10 == 0) goto L_0x005b
            if (r11 == 0) goto L_0x005b
            if (r12 != 0) goto L_0x0015
            goto L_0x005b
        L_0x0015:
            com.android.server.soundtrigger.SoundTriggerHelper$ModelData r2 = r8.getKeyphraseModelDataLocked(r9)     // Catch:{ all -> 0x005d }
            if (r2 == 0) goto L_0x002a
            boolean r3 = r2.isKeyphraseModel()     // Catch:{ all -> 0x005d }
            if (r3 != 0) goto L_0x002a
            java.lang.String r3 = "SoundTriggerHelper"
            java.lang.String r4 = "Generic model with same UUID exists."
            android.util.Slog.e(r3, r4)     // Catch:{ all -> 0x005d }
            monitor-exit(r0)     // Catch:{ all -> 0x005d }
            return r1
        L_0x002a:
            if (r2 == 0) goto L_0x0044
            java.util.UUID r1 = r2.getModelId()     // Catch:{ all -> 0x005d }
            java.util.UUID r3 = r10.uuid     // Catch:{ all -> 0x005d }
            boolean r1 = r1.equals(r3)     // Catch:{ all -> 0x005d }
            if (r1 != 0) goto L_0x0044
            int r1 = r8.cleanUpExistingKeyphraseModelLocked(r2)     // Catch:{ all -> 0x005d }
            if (r1 == 0) goto L_0x0040
            monitor-exit(r0)     // Catch:{ all -> 0x005d }
            return r1
        L_0x0040:
            r8.removeKeyphraseModelLocked(r9)     // Catch:{ all -> 0x005d }
            r2 = 0
        L_0x0044:
            if (r2 != 0) goto L_0x004e
            java.util.UUID r1 = r10.uuid     // Catch:{ all -> 0x005d }
            com.android.server.soundtrigger.SoundTriggerHelper$ModelData r1 = r8.createKeyphraseModelDataLocked(r1, r9)     // Catch:{ all -> 0x005d }
            r2 = r1
            goto L_0x004f
        L_0x004e:
            r1 = r2
        L_0x004f:
            r2 = r8
            r3 = r10
            r4 = r1
            r5 = r11
            r6 = r12
            r7 = r9
            int r2 = r2.startRecognition(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x005d }
            monitor-exit(r0)     // Catch:{ all -> 0x005d }
            return r2
        L_0x005b:
            monitor-exit(r0)     // Catch:{ all -> 0x005d }
            return r1
        L_0x005d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x005d }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.soundtrigger.SoundTriggerHelper.startKeyphraseRecognition(int, android.hardware.soundtrigger.SoundTrigger$KeyphraseSoundModel, android.hardware.soundtrigger.IRecognitionStatusCallback, android.hardware.soundtrigger.SoundTrigger$RecognitionConfig):int");
    }

    private int cleanUpExistingKeyphraseModelLocked(ModelData modelData) {
        int status = tryStopAndUnloadLocked(modelData, true, true);
        if (status != 0) {
            Slog.w(TAG, "Unable to stop or unload previous model: " + modelData.toString());
        }
        return status;
    }

    /* access modifiers changed from: package-private */
    public int startRecognition(SoundTrigger.SoundModel soundModel, ModelData modelData, IRecognitionStatusCallback callback, SoundTrigger.RecognitionConfig recognitionConfig, int keyphraseId) {
        int status;
        synchronized (this.mLock) {
            if (this.mModuleProperties == null) {
                Slog.w(TAG, "Attempting startRecognition without the capability");
                return Integer.MIN_VALUE;
            }
            if (this.mModule == null) {
                this.mModule = SoundTrigger.attachModule(this.mModuleProperties.id, this, (Handler) null);
                if (this.mModule == null) {
                    Slog.w(TAG, "startRecognition cannot attach to sound trigger module");
                    return Integer.MIN_VALUE;
                }
            }
            if (!this.mRecognitionRunning) {
                initializeTelephonyAndPowerStateListeners();
            }
            if (modelData.getSoundModel() != null) {
                boolean stopModel = false;
                boolean unloadModel = false;
                if (modelData.getSoundModel().equals(soundModel) && modelData.isModelStarted()) {
                    stopModel = true;
                    unloadModel = false;
                } else if (!modelData.getSoundModel().equals(soundModel)) {
                    stopModel = modelData.isModelStarted();
                    unloadModel = modelData.isModelLoaded();
                }
                if ((stopModel || unloadModel) && (status = tryStopAndUnloadLocked(modelData, stopModel, unloadModel)) != 0) {
                    Slog.w(TAG, "Unable to stop or unload previous model: " + modelData.toString());
                    return status;
                }
            }
            IRecognitionStatusCallback oldCallback = modelData.getCallback();
            if (!(oldCallback == null || oldCallback.asBinder() == callback.asBinder())) {
                Slog.w(TAG, "Canceling previous recognition for model id: " + modelData.getModelId());
                try {
                    oldCallback.onError(Integer.MIN_VALUE);
                } catch (RemoteException e) {
                    Slog.w(TAG, "RemoteException in onDetectionStopped", e);
                }
                modelData.clearCallback();
            }
            if (!modelData.isModelLoaded()) {
                stopAndUnloadDeadModelsLocked();
                int[] handle = {Integer.MIN_VALUE};
                int status2 = this.mModule.loadSoundModel(soundModel, handle);
                if (status2 != 0) {
                    Slog.w(TAG, "loadSoundModel call failed with " + status2);
                    return status2;
                } else if (handle[0] == Integer.MIN_VALUE) {
                    Slog.w(TAG, "loadSoundModel call returned invalid sound model handle");
                    return Integer.MIN_VALUE;
                } else {
                    modelData.setHandle(handle[0]);
                    modelData.setLoaded();
                    Slog.d(TAG, "Sound model loaded with handle:" + handle[0]);
                }
            }
            modelData.setCallback(callback);
            modelData.setRequested(true);
            modelData.setRecognitionConfig(recognitionConfig);
            modelData.setSoundModel(soundModel);
            int startRecognitionLocked = startRecognitionLocked(modelData, false);
            return startRecognitionLocked;
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0042, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int stopGenericRecognition(java.util.UUID r7, android.hardware.soundtrigger.IRecognitionStatusCallback r8) {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mLock
            monitor-enter(r0)
            android.content.Context r1 = r6.mContext     // Catch:{ all -> 0x0073 }
            java.lang.String r2 = "sth_stop_recognition"
            r3 = 1
            com.android.internal.logging.MetricsLogger.count(r1, r2, r3)     // Catch:{ all -> 0x0073 }
            r1 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r8 == 0) goto L_0x005b
            if (r7 != 0) goto L_0x0013
            goto L_0x005b
        L_0x0013:
            java.util.HashMap<java.util.UUID, com.android.server.soundtrigger.SoundTriggerHelper$ModelData> r2 = r6.mModelDataMap     // Catch:{ all -> 0x0073 }
            java.lang.Object r2 = r2.get(r7)     // Catch:{ all -> 0x0073 }
            com.android.server.soundtrigger.SoundTriggerHelper$ModelData r2 = (com.android.server.soundtrigger.SoundTriggerHelper.ModelData) r2     // Catch:{ all -> 0x0073 }
            if (r2 == 0) goto L_0x0043
            boolean r3 = r2.isGenericModel()     // Catch:{ all -> 0x0073 }
            if (r3 != 0) goto L_0x0024
            goto L_0x0043
        L_0x0024:
            int r1 = r6.stopRecognition(r2, r8)     // Catch:{ all -> 0x0073 }
            if (r1 == 0) goto L_0x0041
            java.lang.String r3 = "SoundTriggerHelper"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0073 }
            r4.<init>()     // Catch:{ all -> 0x0073 }
            java.lang.String r5 = "stopGenericRecognition failed: "
            r4.append(r5)     // Catch:{ all -> 0x0073 }
            r4.append(r1)     // Catch:{ all -> 0x0073 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0073 }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x0073 }
        L_0x0041:
            monitor-exit(r0)     // Catch:{ all -> 0x0073 }
            return r1
        L_0x0043:
            java.lang.String r3 = "SoundTriggerHelper"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0073 }
            r4.<init>()     // Catch:{ all -> 0x0073 }
            java.lang.String r5 = "Attempting stopRecognition on invalid model with id:"
            r4.append(r5)     // Catch:{ all -> 0x0073 }
            r4.append(r7)     // Catch:{ all -> 0x0073 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0073 }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x0073 }
            monitor-exit(r0)     // Catch:{ all -> 0x0073 }
            return r1
        L_0x005b:
            java.lang.String r2 = "SoundTriggerHelper"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0073 }
            r3.<init>()     // Catch:{ all -> 0x0073 }
            java.lang.String r4 = "Null callbackreceived for stopGenericRecognition() for modelid:"
            r3.append(r4)     // Catch:{ all -> 0x0073 }
            r3.append(r7)     // Catch:{ all -> 0x0073 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0073 }
            android.util.Slog.e(r2, r3)     // Catch:{ all -> 0x0073 }
            monitor-exit(r0)     // Catch:{ all -> 0x0073 }
            return r1
        L_0x0073:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0073 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.soundtrigger.SoundTriggerHelper.stopGenericRecognition(java.util.UUID, android.hardware.soundtrigger.IRecognitionStatusCallback):int");
    }

    /* access modifiers changed from: package-private */
    public int stopKeyphraseRecognition(int keyphraseId, IRecognitionStatusCallback callback) {
        synchronized (this.mLock) {
            MetricsLogger.count(this.mContext, "sth_stop_recognition", 1);
            if (callback == null) {
                Slog.e(TAG, "Null callback received for stopKeyphraseRecognition() for keyphraseId:" + keyphraseId);
                return Integer.MIN_VALUE;
            }
            ModelData modelData = getKeyphraseModelDataLocked(keyphraseId);
            if (modelData != null) {
                if (modelData.isKeyphraseModel()) {
                    int status = stopRecognition(modelData, callback);
                    if (status != 0) {
                        return status;
                    }
                    return status;
                }
            }
            Slog.e(TAG, "No model exists for given keyphrase Id " + keyphraseId);
            return Integer.MIN_VALUE;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:32:0x005c, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int stopRecognition(com.android.server.soundtrigger.SoundTriggerHelper.ModelData r6, android.hardware.soundtrigger.IRecognitionStatusCallback r7) {
        /*
            r5 = this;
            java.lang.Object r0 = r5.mLock
            monitor-enter(r0)
            r1 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r7 != 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x006f }
            return r1
        L_0x0009:
            android.hardware.soundtrigger.SoundTrigger$ModuleProperties r2 = r5.mModuleProperties     // Catch:{ all -> 0x006f }
            if (r2 == 0) goto L_0x0066
            android.hardware.soundtrigger.SoundTriggerModule r2 = r5.mModule     // Catch:{ all -> 0x006f }
            if (r2 != 0) goto L_0x0012
            goto L_0x0066
        L_0x0012:
            android.hardware.soundtrigger.IRecognitionStatusCallback r2 = r6.getCallback()     // Catch:{ all -> 0x006f }
            if (r2 == 0) goto L_0x005d
            boolean r3 = r6.isRequested()     // Catch:{ all -> 0x006f }
            if (r3 != 0) goto L_0x0025
            boolean r3 = r6.isModelStarted()     // Catch:{ all -> 0x006f }
            if (r3 != 0) goto L_0x0025
            goto L_0x005d
        L_0x0025:
            android.os.IBinder r3 = r2.asBinder()     // Catch:{ all -> 0x006f }
            android.os.IBinder r4 = r7.asBinder()     // Catch:{ all -> 0x006f }
            if (r3 == r4) goto L_0x0038
            java.lang.String r3 = "SoundTriggerHelper"
            java.lang.String r4 = "Attempting stopRecognition for another recognition"
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x006f }
            monitor-exit(r0)     // Catch:{ all -> 0x006f }
            return r1
        L_0x0038:
            r1 = 0
            r6.setRequested(r1)     // Catch:{ all -> 0x006f }
            boolean r3 = r5.isRecognitionAllowed()     // Catch:{ all -> 0x006f }
            int r1 = r5.updateRecognitionLocked(r6, r3, r1)     // Catch:{ all -> 0x006f }
            if (r1 == 0) goto L_0x0048
            monitor-exit(r0)     // Catch:{ all -> 0x006f }
            return r1
        L_0x0048:
            r6.setLoaded()     // Catch:{ all -> 0x006f }
            r6.clearCallback()     // Catch:{ all -> 0x006f }
            r3 = 0
            r6.setRecognitionConfig(r3)     // Catch:{ all -> 0x006f }
            boolean r3 = r5.computeRecognitionRunningLocked()     // Catch:{ all -> 0x006f }
            if (r3 != 0) goto L_0x005b
            r5.internalClearGlobalStateLocked()     // Catch:{ all -> 0x006f }
        L_0x005b:
            monitor-exit(r0)     // Catch:{ all -> 0x006f }
            return r1
        L_0x005d:
            java.lang.String r3 = "SoundTriggerHelper"
            java.lang.String r4 = "Attempting stopRecognition without a successful startRecognition"
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x006f }
            monitor-exit(r0)     // Catch:{ all -> 0x006f }
            return r1
        L_0x0066:
            java.lang.String r2 = "SoundTriggerHelper"
            java.lang.String r3 = "Attempting stopRecognition without the capability"
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x006f }
            monitor-exit(r0)     // Catch:{ all -> 0x006f }
            return r1
        L_0x006f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x006f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.soundtrigger.SoundTriggerHelper.stopRecognition(com.android.server.soundtrigger.SoundTriggerHelper$ModelData, android.hardware.soundtrigger.IRecognitionStatusCallback):int");
    }

    private int tryStopAndUnloadLocked(ModelData modelData, boolean stopModel, boolean unloadModel) {
        int status = 0;
        if (modelData.isModelNotLoaded()) {
            return 0;
        }
        if (!stopModel || !modelData.isModelStarted() || (status = stopRecognitionLocked(modelData, false)) == 0) {
            if (unloadModel && modelData.isModelLoaded()) {
                Slog.d(TAG, "Unloading previously loaded stale model.");
                status = this.mModule.unloadSoundModel(modelData.getHandle());
                MetricsLogger.count(this.mContext, "sth_unloading_stale_model", 1);
                if (status != 0) {
                    Slog.w(TAG, "unloadSoundModel call failed with " + status);
                } else {
                    modelData.clearState();
                }
            }
            return status;
        }
        Slog.w(TAG, "stopRecognition failed: " + status);
        return status;
    }

    public SoundTrigger.ModuleProperties getModuleProperties() {
        return this.mModuleProperties;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0073, code lost:
        return Integer.MIN_VALUE;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int unloadKeyphraseSoundModel(int r7) {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mLock
            monitor-enter(r0)
            android.content.Context r1 = r6.mContext     // Catch:{ all -> 0x0074 }
            java.lang.String r2 = "sth_unload_keyphrase_sound_model"
            r3 = 1
            com.android.internal.logging.MetricsLogger.count(r1, r2, r3)     // Catch:{ all -> 0x0074 }
            com.android.server.soundtrigger.SoundTriggerHelper$ModelData r1 = r6.getKeyphraseModelDataLocked(r7)     // Catch:{ all -> 0x0074 }
            android.hardware.soundtrigger.SoundTriggerModule r2 = r6.mModule     // Catch:{ all -> 0x0074 }
            r3 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r2 == 0) goto L_0x0072
            if (r1 == 0) goto L_0x0072
            int r2 = r1.getHandle()     // Catch:{ all -> 0x0074 }
            if (r2 == r3) goto L_0x0072
            boolean r2 = r1.isKeyphraseModel()     // Catch:{ all -> 0x0074 }
            if (r2 != 0) goto L_0x0025
            goto L_0x0072
        L_0x0025:
            r2 = 0
            r1.setRequested(r2)     // Catch:{ all -> 0x0074 }
            boolean r3 = r6.isRecognitionAllowed()     // Catch:{ all -> 0x0074 }
            int r2 = r6.updateRecognitionLocked(r1, r3, r2)     // Catch:{ all -> 0x0074 }
            if (r2 == 0) goto L_0x0049
            java.lang.String r3 = "SoundTriggerHelper"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0074 }
            r4.<init>()     // Catch:{ all -> 0x0074 }
            java.lang.String r5 = "Stop recognition failed for keyphrase ID:"
            r4.append(r5)     // Catch:{ all -> 0x0074 }
            r4.append(r2)     // Catch:{ all -> 0x0074 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0074 }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x0074 }
        L_0x0049:
            android.hardware.soundtrigger.SoundTriggerModule r3 = r6.mModule     // Catch:{ all -> 0x0074 }
            int r4 = r1.getHandle()     // Catch:{ all -> 0x0074 }
            int r3 = r3.unloadSoundModel(r4)     // Catch:{ all -> 0x0074 }
            r2 = r3
            if (r2 == 0) goto L_0x006d
            java.lang.String r3 = "SoundTriggerHelper"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0074 }
            r4.<init>()     // Catch:{ all -> 0x0074 }
            java.lang.String r5 = "unloadKeyphraseSoundModel call failed with "
            r4.append(r5)     // Catch:{ all -> 0x0074 }
            r4.append(r2)     // Catch:{ all -> 0x0074 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0074 }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x0074 }
        L_0x006d:
            r6.removeKeyphraseModelLocked(r7)     // Catch:{ all -> 0x0074 }
            monitor-exit(r0)     // Catch:{ all -> 0x0074 }
            return r2
        L_0x0072:
            monitor-exit(r0)     // Catch:{ all -> 0x0074 }
            return r3
        L_0x0074:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0074 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.soundtrigger.SoundTriggerHelper.unloadKeyphraseSoundModel(int):int");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00b4, code lost:
        return Integer.MIN_VALUE;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int unloadGenericSoundModel(java.util.UUID r7) {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mLock
            monitor-enter(r0)
            android.content.Context r1 = r6.mContext     // Catch:{ all -> 0x00b5 }
            java.lang.String r2 = "sth_unload_generic_sound_model"
            r3 = 1
            com.android.internal.logging.MetricsLogger.count(r1, r2, r3)     // Catch:{ all -> 0x00b5 }
            r1 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r7 == 0) goto L_0x00b3
            android.hardware.soundtrigger.SoundTriggerModule r2 = r6.mModule     // Catch:{ all -> 0x00b5 }
            if (r2 != 0) goto L_0x0016
            goto L_0x00b3
        L_0x0016:
            java.util.HashMap<java.util.UUID, com.android.server.soundtrigger.SoundTriggerHelper$ModelData> r2 = r6.mModelDataMap     // Catch:{ all -> 0x00b5 }
            java.lang.Object r2 = r2.get(r7)     // Catch:{ all -> 0x00b5 }
            com.android.server.soundtrigger.SoundTriggerHelper$ModelData r2 = (com.android.server.soundtrigger.SoundTriggerHelper.ModelData) r2     // Catch:{ all -> 0x00b5 }
            if (r2 == 0) goto L_0x009b
            boolean r3 = r2.isGenericModel()     // Catch:{ all -> 0x00b5 }
            if (r3 != 0) goto L_0x0027
            goto L_0x009b
        L_0x0027:
            boolean r1 = r2.isModelLoaded()     // Catch:{ all -> 0x00b5 }
            r3 = 0
            if (r1 != 0) goto L_0x0046
            java.lang.String r1 = "SoundTriggerHelper"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b5 }
            r4.<init>()     // Catch:{ all -> 0x00b5 }
            java.lang.String r5 = "Unload: Given generic model is not loaded:"
            r4.append(r5)     // Catch:{ all -> 0x00b5 }
            r4.append(r7)     // Catch:{ all -> 0x00b5 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00b5 }
            android.util.Slog.i(r1, r4)     // Catch:{ all -> 0x00b5 }
            monitor-exit(r0)     // Catch:{ all -> 0x00b5 }
            return r3
        L_0x0046:
            boolean r1 = r2.isModelStarted()     // Catch:{ all -> 0x00b5 }
            if (r1 == 0) goto L_0x0069
            int r1 = r6.stopRecognitionLocked(r2, r3)     // Catch:{ all -> 0x00b5 }
            if (r1 == 0) goto L_0x0069
            java.lang.String r3 = "SoundTriggerHelper"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b5 }
            r4.<init>()     // Catch:{ all -> 0x00b5 }
            java.lang.String r5 = "stopGenericRecognition failed: "
            r4.append(r5)     // Catch:{ all -> 0x00b5 }
            r4.append(r1)     // Catch:{ all -> 0x00b5 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00b5 }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x00b5 }
        L_0x0069:
            android.hardware.soundtrigger.SoundTriggerModule r1 = r6.mModule     // Catch:{ all -> 0x00b5 }
            int r3 = r2.getHandle()     // Catch:{ all -> 0x00b5 }
            int r1 = r1.unloadSoundModel(r3)     // Catch:{ all -> 0x00b5 }
            if (r1 == 0) goto L_0x0094
            java.lang.String r3 = "SoundTriggerHelper"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b5 }
            r4.<init>()     // Catch:{ all -> 0x00b5 }
            java.lang.String r5 = "unloadGenericSoundModel() call failed with "
            r4.append(r5)     // Catch:{ all -> 0x00b5 }
            r4.append(r1)     // Catch:{ all -> 0x00b5 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00b5 }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x00b5 }
            java.lang.String r3 = "SoundTriggerHelper"
            java.lang.String r4 = "unloadGenericSoundModel() force-marking model as unloaded."
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x00b5 }
        L_0x0094:
            java.util.HashMap<java.util.UUID, com.android.server.soundtrigger.SoundTriggerHelper$ModelData> r3 = r6.mModelDataMap     // Catch:{ all -> 0x00b5 }
            r3.remove(r7)     // Catch:{ all -> 0x00b5 }
            monitor-exit(r0)     // Catch:{ all -> 0x00b5 }
            return r1
        L_0x009b:
            java.lang.String r3 = "SoundTriggerHelper"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b5 }
            r4.<init>()     // Catch:{ all -> 0x00b5 }
            java.lang.String r5 = "Unload error: Attempting unload invalid generic model with id:"
            r4.append(r5)     // Catch:{ all -> 0x00b5 }
            r4.append(r7)     // Catch:{ all -> 0x00b5 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00b5 }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x00b5 }
            monitor-exit(r0)     // Catch:{ all -> 0x00b5 }
            return r1
        L_0x00b3:
            monitor-exit(r0)     // Catch:{ all -> 0x00b5 }
            return r1
        L_0x00b5:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00b5 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.soundtrigger.SoundTriggerHelper.unloadGenericSoundModel(java.util.UUID):int");
    }

    /* access modifiers changed from: package-private */
    public boolean isRecognitionRequested(UUID modelId) {
        boolean z;
        synchronized (this.mLock) {
            ModelData modelData = this.mModelDataMap.get(modelId);
            z = modelData != null && modelData.isRequested();
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0087, code lost:
        return Integer.MIN_VALUE;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getGenericModelState(java.util.UUID r7) {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mLock
            monitor-enter(r0)
            android.content.Context r1 = r6.mContext     // Catch:{ all -> 0x0088 }
            java.lang.String r2 = "sth_get_generic_model_state"
            r3 = 1
            com.android.internal.logging.MetricsLogger.count(r1, r2, r3)     // Catch:{ all -> 0x0088 }
            r1 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r7 == 0) goto L_0x0086
            android.hardware.soundtrigger.SoundTriggerModule r2 = r6.mModule     // Catch:{ all -> 0x0088 }
            if (r2 != 0) goto L_0x0015
            goto L_0x0086
        L_0x0015:
            java.util.HashMap<java.util.UUID, com.android.server.soundtrigger.SoundTriggerHelper$ModelData> r2 = r6.mModelDataMap     // Catch:{ all -> 0x0088 }
            java.lang.Object r2 = r2.get(r7)     // Catch:{ all -> 0x0088 }
            com.android.server.soundtrigger.SoundTriggerHelper$ModelData r2 = (com.android.server.soundtrigger.SoundTriggerHelper.ModelData) r2     // Catch:{ all -> 0x0088 }
            if (r2 == 0) goto L_0x006e
            boolean r3 = r2.isGenericModel()     // Catch:{ all -> 0x0088 }
            if (r3 != 0) goto L_0x0026
            goto L_0x006e
        L_0x0026:
            boolean r3 = r2.isModelLoaded()     // Catch:{ all -> 0x0088 }
            if (r3 != 0) goto L_0x0044
            java.lang.String r3 = "SoundTriggerHelper"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0088 }
            r4.<init>()     // Catch:{ all -> 0x0088 }
            java.lang.String r5 = "GetGenericModelState: Given generic model is not loaded:"
            r4.append(r5)     // Catch:{ all -> 0x0088 }
            r4.append(r7)     // Catch:{ all -> 0x0088 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0088 }
            android.util.Slog.i(r3, r4)     // Catch:{ all -> 0x0088 }
            monitor-exit(r0)     // Catch:{ all -> 0x0088 }
            return r1
        L_0x0044:
            boolean r3 = r2.isModelStarted()     // Catch:{ all -> 0x0088 }
            if (r3 != 0) goto L_0x0062
            java.lang.String r3 = "SoundTriggerHelper"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0088 }
            r4.<init>()     // Catch:{ all -> 0x0088 }
            java.lang.String r5 = "GetGenericModelState: Given generic model is not started:"
            r4.append(r5)     // Catch:{ all -> 0x0088 }
            r4.append(r7)     // Catch:{ all -> 0x0088 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0088 }
            android.util.Slog.i(r3, r4)     // Catch:{ all -> 0x0088 }
            monitor-exit(r0)     // Catch:{ all -> 0x0088 }
            return r1
        L_0x0062:
            android.hardware.soundtrigger.SoundTriggerModule r1 = r6.mModule     // Catch:{ all -> 0x0088 }
            int r3 = r2.getHandle()     // Catch:{ all -> 0x0088 }
            int r1 = r1.getModelState(r3)     // Catch:{ all -> 0x0088 }
            monitor-exit(r0)     // Catch:{ all -> 0x0088 }
            return r1
        L_0x006e:
            java.lang.String r3 = "SoundTriggerHelper"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0088 }
            r4.<init>()     // Catch:{ all -> 0x0088 }
            java.lang.String r5 = "GetGenericModelState error: Invalid generic model id:"
            r4.append(r5)     // Catch:{ all -> 0x0088 }
            r4.append(r7)     // Catch:{ all -> 0x0088 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0088 }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x0088 }
            monitor-exit(r0)     // Catch:{ all -> 0x0088 }
            return r1
        L_0x0086:
            monitor-exit(r0)     // Catch:{ all -> 0x0088 }
            return r1
        L_0x0088:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0088 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.soundtrigger.SoundTriggerHelper.getGenericModelState(java.util.UUID):int");
    }

    /* access modifiers changed from: package-private */
    public int getKeyphraseModelState(UUID modelId) {
        Slog.w(TAG, "GetKeyphraseModelState error: Not implemented");
        return Integer.MIN_VALUE;
    }

    public void onRecognition(SoundTrigger.RecognitionEvent event) {
        if (event == null) {
            Slog.w(TAG, "Null recognition event!");
        } else if ((event instanceof SoundTrigger.KeyphraseRecognitionEvent) || (event instanceof SoundTrigger.GenericRecognitionEvent)) {
            synchronized (this.mLock) {
                int i = event.status;
                if (i != 0) {
                    if (i == 1) {
                        onRecognitionAbortLocked(event);
                    } else if (i == 2) {
                        onRecognitionFailureLocked();
                    } else if (i != 3) {
                    }
                }
                if (isKeyphraseRecognitionEvent(event)) {
                    onKeyphraseRecognitionSuccessLocked((SoundTrigger.KeyphraseRecognitionEvent) event);
                } else {
                    onGenericRecognitionSuccessLocked((SoundTrigger.GenericRecognitionEvent) event);
                }
            }
        } else {
            Slog.w(TAG, "Invalid recognition event type (not one of generic or keyphrase)!");
        }
    }

    private boolean isKeyphraseRecognitionEvent(SoundTrigger.RecognitionEvent event) {
        return event instanceof SoundTrigger.KeyphraseRecognitionEvent;
    }

    private void onGenericRecognitionSuccessLocked(SoundTrigger.GenericRecognitionEvent event) {
        MetricsLogger.count(this.mContext, "sth_generic_recognition_event", 1);
        if (event.status == 0 || event.status == 3) {
            ModelData model = getModelDataForLocked(event.soundModelHandle);
            if (model == null || !model.isGenericModel()) {
                Slog.w(TAG, "Generic recognition event: Model does not exist for handle: " + event.soundModelHandle);
                return;
            }
            IRecognitionStatusCallback callback = model.getCallback();
            if (callback == null) {
                Slog.w(TAG, "Generic recognition event: Null callback for model handle: " + event.soundModelHandle);
                return;
            }
            model.setStopped();
            try {
                callback.onGenericSoundTriggerDetected(event);
            } catch (DeadObjectException e) {
                forceStopAndUnloadModelLocked(model, e);
                return;
            } catch (RemoteException e2) {
                Slog.w(TAG, "RemoteException in onGenericSoundTriggerDetected", e2);
            }
            SoundTrigger.RecognitionConfig config = model.getRecognitionConfig();
            if (config == null) {
                Slog.w(TAG, "Generic recognition event: Null RecognitionConfig for model handle: " + event.soundModelHandle);
                return;
            }
            model.setRequested(config.allowMultipleTriggers);
            if (model.isRequested()) {
                updateRecognitionLocked(model, isRecognitionAllowed(), true);
            }
        }
    }

    public void onSoundModelUpdate(SoundTrigger.SoundModelEvent event) {
        if (event == null) {
            Slog.w(TAG, "Invalid sound model event!");
            return;
        }
        synchronized (this.mLock) {
            MetricsLogger.count(this.mContext, "sth_sound_model_updated", 1);
            onSoundModelUpdatedLocked(event);
        }
    }

    public void onServiceStateChange(int state) {
        synchronized (this.mLock) {
            boolean z = true;
            if (1 != state) {
                z = false;
            }
            onServiceStateChangedLocked(z);
        }
    }

    public void onServiceDied() {
        Slog.e(TAG, "onServiceDied!!");
        MetricsLogger.count(this.mContext, "sth_service_died", 1);
        synchronized (this.mLock) {
            onServiceDiedLocked();
        }
    }

    /* access modifiers changed from: private */
    public void onCallStateChangedLocked(boolean callActive) {
        if (this.mCallActive != callActive) {
            this.mCallActive = callActive;
            updateAllRecognitionsLocked(true);
        }
    }

    /* access modifiers changed from: private */
    public void onPowerSaveModeChangedLocked(boolean isPowerSaveMode) {
        if (this.mIsPowerSaveMode != isPowerSaveMode) {
            this.mIsPowerSaveMode = isPowerSaveMode;
            updateAllRecognitionsLocked(true);
        }
    }

    private void onSoundModelUpdatedLocked(SoundTrigger.SoundModelEvent event) {
    }

    private void onServiceStateChangedLocked(boolean disabled) {
        if (disabled != this.mServiceDisabled) {
            this.mServiceDisabled = disabled;
            updateAllRecognitionsLocked(true);
        }
    }

    private void onRecognitionAbortLocked(SoundTrigger.RecognitionEvent event) {
        Slog.w(TAG, "Recognition aborted");
        MetricsLogger.count(this.mContext, "sth_recognition_aborted", 1);
        ModelData modelData = getModelDataForLocked(event.soundModelHandle);
        if (modelData != null && modelData.isModelStarted()) {
            modelData.setStopped();
            try {
                modelData.getCallback().onRecognitionPaused();
            } catch (DeadObjectException e) {
                forceStopAndUnloadModelLocked(modelData, e);
            } catch (RemoteException e2) {
                Slog.w(TAG, "RemoteException in onRecognitionPaused", e2);
            }
        }
    }

    private void onRecognitionFailureLocked() {
        Slog.w(TAG, "Recognition failure");
        MetricsLogger.count(this.mContext, "sth_recognition_failure_event", 1);
        try {
            sendErrorCallbacksToAllLocked(Integer.MIN_VALUE);
        } finally {
            internalClearModelStateLocked();
            internalClearGlobalStateLocked();
        }
    }

    private int getKeyphraseIdFromEvent(SoundTrigger.KeyphraseRecognitionEvent event) {
        if (event == null) {
            Slog.w(TAG, "Null RecognitionEvent received.");
            return Integer.MIN_VALUE;
        }
        SoundTrigger.KeyphraseRecognitionExtra[] keyphraseExtras = event.keyphraseExtras;
        if (keyphraseExtras != null && keyphraseExtras.length != 0) {
            return keyphraseExtras[0].id;
        }
        Slog.w(TAG, "Invalid keyphrase recognition event!");
        return Integer.MIN_VALUE;
    }

    private void onKeyphraseRecognitionSuccessLocked(SoundTrigger.KeyphraseRecognitionEvent event) {
        Slog.i(TAG, "Recognition success");
        MetricsLogger.count(this.mContext, "sth_keyphrase_recognition_event", 1);
        int keyphraseId = getKeyphraseIdFromEvent(event);
        ModelData modelData = getKeyphraseModelDataLocked(keyphraseId);
        if (modelData == null || !modelData.isKeyphraseModel()) {
            Slog.e(TAG, "Keyphase model data does not exist for ID:" + keyphraseId);
        } else if (modelData.getCallback() == null) {
            Slog.w(TAG, "Received onRecognition event without callback for keyphrase model.");
        } else {
            modelData.setStopped();
            try {
                modelData.getCallback().onKeyphraseDetected(event);
            } catch (DeadObjectException e) {
                forceStopAndUnloadModelLocked(modelData, e);
                return;
            } catch (RemoteException e2) {
                Slog.w(TAG, "RemoteException in onKeyphraseDetected", e2);
            }
            SoundTrigger.RecognitionConfig config = modelData.getRecognitionConfig();
            if (config != null) {
                modelData.setRequested(config.allowMultipleTriggers);
            }
            if (modelData.isRequested()) {
                updateRecognitionLocked(modelData, isRecognitionAllowed(), true);
            }
        }
    }

    private void updateAllRecognitionsLocked(boolean notify) {
        boolean isAllowed = isRecognitionAllowed();
        Iterator<ModelData> it = new ArrayList<>(this.mModelDataMap.values()).iterator();
        while (it.hasNext()) {
            updateRecognitionLocked(it.next(), isAllowed, notify);
        }
    }

    private int updateRecognitionLocked(ModelData model, boolean isAllowed, boolean notify) {
        boolean start = model.isRequested() && isAllowed;
        if (start == model.isModelStarted()) {
            return 0;
        }
        if (start) {
            return startRecognitionLocked(model, notify);
        }
        return stopRecognitionLocked(model, notify);
    }

    private void onServiceDiedLocked() {
        try {
            MetricsLogger.count(this.mContext, "sth_service_died", 1);
            sendErrorCallbacksToAllLocked(SoundTrigger.STATUS_DEAD_OBJECT);
        } finally {
            internalClearModelStateLocked();
            internalClearGlobalStateLocked();
            SoundTriggerModule soundTriggerModule = this.mModule;
            if (soundTriggerModule != null) {
                soundTriggerModule.detach();
                this.mModule = null;
            }
        }
    }

    /* JADX INFO: finally extract failed */
    private void internalClearGlobalStateLocked() {
        long token = Binder.clearCallingIdentity();
        try {
            this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
            Binder.restoreCallingIdentity(token);
            PowerSaveModeListener powerSaveModeListener = this.mPowerSaveModeListener;
            if (powerSaveModeListener != null) {
                this.mContext.unregisterReceiver(powerSaveModeListener);
                this.mPowerSaveModeListener = null;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(token);
            throw th;
        }
    }

    private void internalClearModelStateLocked() {
        for (ModelData modelData : this.mModelDataMap.values()) {
            modelData.clearState();
        }
    }

    class MyCallStateListener extends PhoneStateListener {
        MyCallStateListener() {
        }

        public void onCallStateChanged(int state, String arg1) {
            synchronized (SoundTriggerHelper.this.mLock) {
                SoundTriggerHelper.this.onCallStateChangedLocked(2 == state);
            }
        }
    }

    class PowerSaveModeListener extends BroadcastReceiver {
        PowerSaveModeListener() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.os.action.POWER_SAVE_MODE_CHANGED".equals(intent.getAction())) {
                boolean active = SoundTriggerHelper.this.mPowerManager.getPowerSaveState(8).batterySaverEnabled;
                synchronized (SoundTriggerHelper.this.mLock) {
                    SoundTriggerHelper.this.onPowerSaveModeChangedLocked(active);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        synchronized (this.mLock) {
            pw.print("  module properties=");
            pw.println(this.mModuleProperties == null ? "null" : this.mModuleProperties);
            pw.print("  call active=");
            pw.println(this.mCallActive);
            pw.print("  power save mode active=");
            pw.println(this.mIsPowerSaveMode);
            pw.print("  service disabled=");
            pw.println(this.mServiceDisabled);
        }
    }

    private void initializeTelephonyAndPowerStateListeners() {
        long token = Binder.clearCallingIdentity();
        try {
            this.mCallActive = this.mTelephonyManager.getCallState() == 2;
            this.mTelephonyManager.listen(this.mPhoneStateListener, 32);
            if (this.mPowerSaveModeListener == null) {
                this.mPowerSaveModeListener = new PowerSaveModeListener();
                this.mContext.registerReceiver(this.mPowerSaveModeListener, new IntentFilter("android.os.action.POWER_SAVE_MODE_CHANGED"));
            }
            this.mIsPowerSaveMode = this.mPowerManager.getPowerSaveState(8).batterySaverEnabled;
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private void sendErrorCallbacksToAllLocked(int errorCode) {
        for (ModelData modelData : this.mModelDataMap.values()) {
            IRecognitionStatusCallback callback = modelData.getCallback();
            if (callback != null) {
                try {
                    callback.onError(errorCode);
                } catch (RemoteException e) {
                    Slog.w(TAG, "RemoteException sendErrorCallbacksToAllLocked for model handle " + modelData.getHandle(), e);
                }
            }
        }
    }

    private void forceStopAndUnloadModelLocked(ModelData modelData, Exception exception) {
        forceStopAndUnloadModelLocked(modelData, exception, (Iterator) null);
    }

    private void forceStopAndUnloadModelLocked(ModelData modelData, Exception exception, Iterator modelDataIterator) {
        if (exception != null) {
            Slog.e(TAG, "forceStopAndUnloadModel", exception);
        }
        if (modelData.isModelStarted()) {
            Slog.d(TAG, "Stopping previously started dangling model " + modelData.getHandle());
            if (this.mModule.stopRecognition(modelData.getHandle()) != 0) {
                modelData.setStopped();
                modelData.setRequested(false);
            } else {
                Slog.e(TAG, "Failed to stop model " + modelData.getHandle());
            }
        }
        if (modelData.isModelLoaded()) {
            Slog.d(TAG, "Unloading previously loaded dangling model " + modelData.getHandle());
            if (this.mModule.unloadSoundModel(modelData.getHandle()) == 0) {
                if (modelDataIterator != null) {
                    modelDataIterator.remove();
                } else {
                    this.mModelDataMap.remove(modelData.getModelId());
                }
                Iterator it = this.mKeyphraseUuidMap.entrySet().iterator();
                while (it.hasNext()) {
                    if (it.next().getValue().equals(modelData.getModelId())) {
                        it.remove();
                    }
                }
                modelData.clearState();
                return;
            }
            Slog.e(TAG, "Failed to unload model " + modelData.getHandle());
        }
    }

    private void stopAndUnloadDeadModelsLocked() {
        Iterator it = this.mModelDataMap.entrySet().iterator();
        while (it.hasNext()) {
            ModelData modelData = (ModelData) it.next().getValue();
            if (modelData.isModelLoaded() && (modelData.getCallback() == null || (modelData.getCallback().asBinder() != null && !modelData.getCallback().asBinder().pingBinder()))) {
                Slog.w(TAG, "Removing model " + modelData.getHandle() + " that has no clients");
                forceStopAndUnloadModelLocked(modelData, (Exception) null, it);
            }
        }
    }

    private ModelData getOrCreateGenericModelDataLocked(UUID modelId) {
        ModelData modelData = this.mModelDataMap.get(modelId);
        if (modelData == null) {
            ModelData modelData2 = ModelData.createGenericModelData(modelId);
            this.mModelDataMap.put(modelId, modelData2);
            return modelData2;
        } else if (modelData.isGenericModel()) {
            return modelData;
        } else {
            Slog.e(TAG, "UUID already used for non-generic model.");
            return null;
        }
    }

    private void removeKeyphraseModelLocked(int keyphraseId) {
        UUID uuid = this.mKeyphraseUuidMap.get(Integer.valueOf(keyphraseId));
        if (uuid != null) {
            this.mModelDataMap.remove(uuid);
            this.mKeyphraseUuidMap.remove(Integer.valueOf(keyphraseId));
        }
    }

    private ModelData getKeyphraseModelDataLocked(int keyphraseId) {
        UUID uuid = this.mKeyphraseUuidMap.get(Integer.valueOf(keyphraseId));
        if (uuid == null) {
            return null;
        }
        return this.mModelDataMap.get(uuid);
    }

    private ModelData createKeyphraseModelDataLocked(UUID modelId, int keyphraseId) {
        this.mKeyphraseUuidMap.remove(Integer.valueOf(keyphraseId));
        this.mModelDataMap.remove(modelId);
        this.mKeyphraseUuidMap.put(Integer.valueOf(keyphraseId), modelId);
        ModelData modelData = ModelData.createKeyphraseModelData(modelId);
        this.mModelDataMap.put(modelId, modelData);
        return modelData;
    }

    private ModelData getModelDataForLocked(int modelHandle) {
        for (ModelData model : this.mModelDataMap.values()) {
            if (model.getHandle() == modelHandle) {
                return model;
            }
        }
        return null;
    }

    private boolean isRecognitionAllowed() {
        return !this.mCallActive && !this.mServiceDisabled && !this.mIsPowerSaveMode;
    }

    private int startRecognitionLocked(ModelData modelData, boolean notify) {
        IRecognitionStatusCallback callback = modelData.getCallback();
        int handle = modelData.getHandle();
        SoundTrigger.RecognitionConfig config = modelData.getRecognitionConfig();
        if (callback == null || handle == Integer.MIN_VALUE || config == null) {
            Slog.w(TAG, "startRecognition: Bad data passed in.");
            MetricsLogger.count(this.mContext, "sth_start_recognition_error", 1);
            return Integer.MIN_VALUE;
        } else if (!isRecognitionAllowed()) {
            Slog.w(TAG, "startRecognition requested but not allowed.");
            MetricsLogger.count(this.mContext, "sth_start_recognition_not_allowed", 1);
            return 0;
        } else {
            int status = this.mModule.startRecognition(handle, config);
            if (status != 0) {
                Slog.w(TAG, "startRecognition failed with " + status);
                MetricsLogger.count(this.mContext, "sth_start_recognition_error", 1);
                if (notify) {
                    try {
                        callback.onError(status);
                    } catch (DeadObjectException e) {
                        forceStopAndUnloadModelLocked(modelData, e);
                    } catch (RemoteException e2) {
                        Slog.w(TAG, "RemoteException in onError", e2);
                    }
                }
            } else {
                Slog.i(TAG, "startRecognition successful.");
                MetricsLogger.count(this.mContext, "sth_start_recognition_success", 1);
                modelData.setStarted();
                if (notify) {
                    try {
                        callback.onRecognitionResumed();
                    } catch (DeadObjectException e3) {
                        forceStopAndUnloadModelLocked(modelData, e3);
                    } catch (RemoteException e4) {
                        Slog.w(TAG, "RemoteException in onRecognitionResumed", e4);
                    }
                }
            }
            return status;
        }
    }

    private int stopRecognitionLocked(ModelData modelData, boolean notify) {
        IRecognitionStatusCallback callback = modelData.getCallback();
        int status = this.mModule.stopRecognition(modelData.getHandle());
        if (status != 0) {
            Slog.w(TAG, "stopRecognition call failed with " + status);
            MetricsLogger.count(this.mContext, "sth_stop_recognition_error", 1);
            if (notify) {
                try {
                    callback.onError(status);
                } catch (DeadObjectException e) {
                    forceStopAndUnloadModelLocked(modelData, e);
                } catch (RemoteException e2) {
                    Slog.w(TAG, "RemoteException in onError", e2);
                }
            }
        } else {
            modelData.setStopped();
            MetricsLogger.count(this.mContext, "sth_stop_recognition_success", 1);
            if (notify) {
                try {
                    callback.onRecognitionPaused();
                } catch (DeadObjectException e3) {
                    forceStopAndUnloadModelLocked(modelData, e3);
                } catch (RemoteException e4) {
                    Slog.w(TAG, "RemoteException in onRecognitionPaused", e4);
                }
            }
        }
        return status;
    }

    private void dumpModelStateLocked() {
        for (UUID modelId : this.mModelDataMap.keySet()) {
            HashMap<UUID, ModelData> hashMap = this.mModelDataMap;
            Slog.i(TAG, "Model :" + hashMap.get(modelId).toString());
        }
    }

    private boolean computeRecognitionRunningLocked() {
        if (this.mModuleProperties == null || this.mModule == null) {
            this.mRecognitionRunning = false;
            return this.mRecognitionRunning;
        }
        for (ModelData modelData : this.mModelDataMap.values()) {
            if (modelData.isModelStarted()) {
                this.mRecognitionRunning = true;
                return this.mRecognitionRunning;
            }
        }
        this.mRecognitionRunning = false;
        return this.mRecognitionRunning;
    }

    private static class ModelData {
        static final int MODEL_LOADED = 1;
        static final int MODEL_NOTLOADED = 0;
        static final int MODEL_STARTED = 2;
        private IRecognitionStatusCallback mCallback = null;
        private int mModelHandle = Integer.MIN_VALUE;
        private UUID mModelId;
        private int mModelState;
        private int mModelType = -1;
        private SoundTrigger.RecognitionConfig mRecognitionConfig = null;
        private boolean mRequested = false;
        private SoundTrigger.SoundModel mSoundModel = null;

        private ModelData(UUID modelId, int modelType) {
            this.mModelId = modelId;
            this.mModelType = modelType;
        }

        static ModelData createKeyphraseModelData(UUID modelId) {
            return new ModelData(modelId, 0);
        }

        static ModelData createGenericModelData(UUID modelId) {
            return new ModelData(modelId, 1);
        }

        static ModelData createModelDataOfUnknownType(UUID modelId) {
            return new ModelData(modelId, -1);
        }

        /* access modifiers changed from: package-private */
        public synchronized void setCallback(IRecognitionStatusCallback callback) {
            this.mCallback = callback;
        }

        /* access modifiers changed from: package-private */
        public synchronized IRecognitionStatusCallback getCallback() {
            return this.mCallback;
        }

        /* access modifiers changed from: package-private */
        public synchronized boolean isModelLoaded() {
            boolean z;
            z = true;
            if (!(this.mModelState == 1 || this.mModelState == 2)) {
                z = false;
            }
            return z;
        }

        /* access modifiers changed from: package-private */
        public synchronized boolean isModelNotLoaded() {
            return this.mModelState == 0;
        }

        /* access modifiers changed from: package-private */
        public synchronized void setStarted() {
            this.mModelState = 2;
        }

        /* access modifiers changed from: package-private */
        public synchronized void setStopped() {
            this.mModelState = 1;
        }

        /* access modifiers changed from: package-private */
        public synchronized void setLoaded() {
            this.mModelState = 1;
        }

        /* access modifiers changed from: package-private */
        public synchronized boolean isModelStarted() {
            return this.mModelState == 2;
        }

        /* access modifiers changed from: package-private */
        public synchronized void clearState() {
            this.mModelState = 0;
            this.mModelHandle = Integer.MIN_VALUE;
            this.mRecognitionConfig = null;
            this.mRequested = false;
            this.mCallback = null;
        }

        /* access modifiers changed from: package-private */
        public synchronized void clearCallback() {
            this.mCallback = null;
        }

        /* access modifiers changed from: package-private */
        public synchronized void setHandle(int handle) {
            this.mModelHandle = handle;
        }

        /* access modifiers changed from: package-private */
        public synchronized void setRecognitionConfig(SoundTrigger.RecognitionConfig config) {
            this.mRecognitionConfig = config;
        }

        /* access modifiers changed from: package-private */
        public synchronized int getHandle() {
            return this.mModelHandle;
        }

        /* access modifiers changed from: package-private */
        public synchronized UUID getModelId() {
            return this.mModelId;
        }

        /* access modifiers changed from: package-private */
        public synchronized SoundTrigger.RecognitionConfig getRecognitionConfig() {
            return this.mRecognitionConfig;
        }

        /* access modifiers changed from: package-private */
        public synchronized boolean isRequested() {
            return this.mRequested;
        }

        /* access modifiers changed from: package-private */
        public synchronized void setRequested(boolean requested) {
            this.mRequested = requested;
        }

        /* access modifiers changed from: package-private */
        public synchronized void setSoundModel(SoundTrigger.SoundModel soundModel) {
            this.mSoundModel = soundModel;
        }

        /* access modifiers changed from: package-private */
        public synchronized SoundTrigger.SoundModel getSoundModel() {
            return this.mSoundModel;
        }

        /* access modifiers changed from: package-private */
        public synchronized int getModelType() {
            return this.mModelType;
        }

        /* access modifiers changed from: package-private */
        public synchronized boolean isKeyphraseModel() {
            return this.mModelType == 0;
        }

        /* access modifiers changed from: package-private */
        public synchronized boolean isGenericModel() {
            boolean z;
            z = true;
            if (this.mModelType != 1) {
                z = false;
            }
            return z;
        }

        /* access modifiers changed from: package-private */
        public synchronized String stateToString() {
            int i = this.mModelState;
            if (i == 0) {
                return "NOT_LOADED";
            }
            if (i == 1) {
                return "LOADED";
            }
            if (i != 2) {
                return "Unknown state";
            }
            return "STARTED";
        }

        /* access modifiers changed from: package-private */
        public synchronized String requestedToString() {
            StringBuilder sb;
            sb = new StringBuilder();
            sb.append("Requested: ");
            sb.append(this.mRequested ? "Yes" : "No");
            return sb.toString();
        }

        /* access modifiers changed from: package-private */
        public synchronized String callbackToString() {
            StringBuilder sb;
            sb = new StringBuilder();
            sb.append("Callback: ");
            sb.append(this.mCallback != null ? this.mCallback.asBinder() : "null");
            return sb.toString();
        }

        /* access modifiers changed from: package-private */
        public synchronized String uuidToString() {
            return "UUID: " + this.mModelId;
        }

        public synchronized String toString() {
            return "Handle: " + this.mModelHandle + "\nModelState: " + stateToString() + "\n" + requestedToString() + "\n" + callbackToString() + "\n" + uuidToString() + "\n" + modelTypeToString();
        }

        /* access modifiers changed from: package-private */
        public synchronized String modelTypeToString() {
            String type;
            type = null;
            int i = this.mModelType;
            if (i == -1) {
                type = ProcessPolicy.REASON_UNKNOWN;
            } else if (i == 0) {
                type = "Keyphrase";
            } else if (i == 1) {
                type = "Generic";
            }
            return "Model type: " + type + "\n";
        }
    }
}
