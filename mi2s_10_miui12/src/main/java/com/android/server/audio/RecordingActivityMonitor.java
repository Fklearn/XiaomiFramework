package com.android.server.audio;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecordingConfiguration;
import android.media.AudioSystem;
import android.media.IRecordingConfigDispatcher;
import android.media.MediaRecorder;
import android.media.audiofx.AudioEffect;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.android.server.audio.AudioEventLogger;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public final class RecordingActivityMonitor implements AudioSystem.AudioRecordingCallback {
    public static final String TAG = "AudioService.RecordingActivityMonitor";
    private static final AudioEventLogger sEventLogger = new AudioEventLogger(50, "recording activity received by AudioService");
    private ArrayList<RecMonitorClient> mClients = new ArrayList<>();
    private boolean mHasPublicClients = false;
    private final PackageManager mPackMan;
    private List<RecordingState> mRecordStates = new ArrayList();

    static final class RecordingState {
        private AudioRecordingConfiguration mConfig;
        private final RecorderDeathHandler mDeathHandler;
        private boolean mIsActive;
        private final int mRiid;

        RecordingState(int riid, RecorderDeathHandler handler) {
            this.mRiid = riid;
            this.mDeathHandler = handler;
        }

        RecordingState(AudioRecordingConfiguration config) {
            this.mRiid = -1;
            this.mDeathHandler = null;
            this.mConfig = config;
        }

        /* access modifiers changed from: package-private */
        public int getRiid() {
            return this.mRiid;
        }

        /* access modifiers changed from: package-private */
        public int getPortId() {
            AudioRecordingConfiguration audioRecordingConfiguration = this.mConfig;
            if (audioRecordingConfiguration != null) {
                return audioRecordingConfiguration.getClientPortId();
            }
            return -1;
        }

        /* access modifiers changed from: package-private */
        public AudioRecordingConfiguration getConfig() {
            return this.mConfig;
        }

        /* access modifiers changed from: package-private */
        public boolean hasDeathHandler() {
            return this.mDeathHandler != null;
        }

        /* access modifiers changed from: package-private */
        public boolean isActiveConfiguration() {
            return this.mIsActive && this.mConfig != null;
        }

        /* access modifiers changed from: package-private */
        public boolean setActive(boolean active) {
            if (this.mIsActive == active) {
                return false;
            }
            this.mIsActive = active;
            if (this.mConfig != null) {
                return true;
            }
            return false;
        }

        /* access modifiers changed from: package-private */
        public boolean setConfig(AudioRecordingConfiguration config) {
            if (config.equals(this.mConfig)) {
                return false;
            }
            this.mConfig = config;
            return this.mIsActive;
        }

        /* access modifiers changed from: package-private */
        public void dump(PrintWriter pw) {
            pw.println("riid " + this.mRiid + "; active? " + this.mIsActive);
            AudioRecordingConfiguration audioRecordingConfiguration = this.mConfig;
            if (audioRecordingConfiguration != null) {
                audioRecordingConfiguration.dump(pw);
            } else {
                pw.println("  no config");
            }
        }
    }

    RecordingActivityMonitor(Context ctxt) {
        RecMonitorClient.sMonitor = this;
        RecorderDeathHandler.sMonitor = this;
        this.mPackMan = ctxt.getPackageManager();
    }

    public void onRecordingConfigurationChanged(int event, int riid, int uid, int session, int source, int portId, boolean silenced, int[] recordingInfo, AudioEffect.Descriptor[] clientEffects, AudioEffect.Descriptor[] effects, int activeSource, String packName) {
        int i = event;
        int i2 = riid;
        AudioRecordingConfiguration config = createRecordingConfiguration(uid, session, source, recordingInfo, portId, silenced, activeSource, clientEffects, effects);
        if (MediaRecorder.isSystemOnlyAudioSource(source)) {
            sEventLogger.log(new RecordingEvent(event, riid, config).printLog(TAG));
        } else {
            dispatchCallbacks(updateSnapshot(event, riid, config));
        }
    }

    public int trackRecorder(IBinder recorder) {
        if (recorder == null) {
            Log.e(TAG, "trackRecorder called with null token");
            return -1;
        }
        int newRiid = AudioSystem.newAudioRecorderId();
        RecorderDeathHandler handler = new RecorderDeathHandler(newRiid, recorder);
        if (!handler.init()) {
            return -1;
        }
        synchronized (this.mRecordStates) {
            this.mRecordStates.add(new RecordingState(newRiid, handler));
        }
        return newRiid;
    }

    public void recorderEvent(int riid, int event) {
        int configEvent = 1;
        if (event == 0) {
            configEvent = 0;
        } else if (event != 1) {
            configEvent = -1;
        }
        if (riid == -1 || configEvent == -1) {
            sEventLogger.log(new RecordingEvent(event, riid, (AudioRecordingConfiguration) null).printLog(TAG));
        } else {
            dispatchCallbacks(updateSnapshot(configEvent, riid, (AudioRecordingConfiguration) null));
        }
    }

    public void releaseRecorder(int riid) {
        dispatchCallbacks(updateSnapshot(3, riid, (AudioRecordingConfiguration) null));
    }

    private void dispatchCallbacks(List<AudioRecordingConfiguration> configs) {
        List<AudioRecordingConfiguration> configsPublic;
        if (configs != null) {
            synchronized (this.mClients) {
                if (this.mHasPublicClients) {
                    configsPublic = anonymizeForPublicConsumption(configs);
                } else {
                    configsPublic = new ArrayList<>();
                }
                Iterator<RecMonitorClient> it = this.mClients.iterator();
                while (it.hasNext()) {
                    RecMonitorClient rmc = it.next();
                    try {
                        if (rmc.mIsPrivileged) {
                            rmc.mDispatcherCb.dispatchRecordingConfigChange(configs);
                        } else {
                            rmc.mDispatcherCb.dispatchRecordingConfigChange(configsPublic);
                        }
                    } catch (RemoteException e) {
                        Log.w(TAG, "Could not call dispatchRecordingConfigChange() on client", e);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dump(PrintWriter pw) {
        pw.println("\nRecordActivityMonitor dump time: " + DateFormat.getTimeInstance().format(new Date()));
        synchronized (this.mRecordStates) {
            for (RecordingState state : this.mRecordStates) {
                state.dump(pw);
            }
        }
        pw.println("\n");
        sEventLogger.dump(pw);
    }

    private static ArrayList<AudioRecordingConfiguration> anonymizeForPublicConsumption(List<AudioRecordingConfiguration> sysConfigs) {
        ArrayList<AudioRecordingConfiguration> publicConfigs = new ArrayList<>();
        for (AudioRecordingConfiguration config : sysConfigs) {
            publicConfigs.add(AudioRecordingConfiguration.anonymizedCopy(config));
        }
        return publicConfigs;
    }

    /* access modifiers changed from: package-private */
    public void initMonitor() {
        AudioSystem.setRecordingCallback(this);
    }

    /* access modifiers changed from: package-private */
    public void onAudioServerDied() {
        List<AudioRecordingConfiguration> configs = null;
        synchronized (this.mRecordStates) {
            boolean configChanged = false;
            Iterator<RecordingState> it = this.mRecordStates.iterator();
            while (it.hasNext()) {
                RecordingState state = it.next();
                if (!state.hasDeathHandler()) {
                    if (state.isActiveConfiguration()) {
                        configChanged = true;
                        sEventLogger.log(new RecordingEvent(3, state.getRiid(), state.getConfig()));
                    }
                    it.remove();
                }
            }
            if (configChanged) {
                configs = getActiveRecordingConfigurations(true);
            }
        }
        dispatchCallbacks(configs);
    }

    /* access modifiers changed from: package-private */
    public void registerRecordingCallback(IRecordingConfigDispatcher rcdb, boolean isPrivileged) {
        if (rcdb != null) {
            synchronized (this.mClients) {
                RecMonitorClient rmc = new RecMonitorClient(rcdb, isPrivileged);
                if (rmc.init()) {
                    if (!isPrivileged) {
                        this.mHasPublicClients = true;
                    }
                    this.mClients.add(rmc);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void unregisterRecordingCallback(IRecordingConfigDispatcher rcdb) {
        if (rcdb != null) {
            synchronized (this.mClients) {
                Iterator<RecMonitorClient> clientIterator = this.mClients.iterator();
                boolean hasPublicClients = false;
                while (clientIterator.hasNext()) {
                    RecMonitorClient rmc = clientIterator.next();
                    if (rcdb.equals(rmc.mDispatcherCb)) {
                        rmc.release();
                        clientIterator.remove();
                    } else if (!rmc.mIsPrivileged) {
                        hasPublicClients = true;
                    }
                }
                this.mHasPublicClients = hasPublicClients;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public List<AudioRecordingConfiguration> getActiveRecordingConfigurations(boolean isPrivileged) {
        List<AudioRecordingConfiguration> configs = new ArrayList<>();
        synchronized (this.mRecordStates) {
            for (RecordingState state : this.mRecordStates) {
                if (state.isActiveConfiguration()) {
                    configs.add(state.getConfig());
                }
            }
        }
        if (!isPrivileged) {
            return anonymizeForPublicConsumption(configs);
        }
        return configs;
    }

    private AudioRecordingConfiguration createRecordingConfiguration(int uid, int session, int source, int[] recordingInfo, int portId, boolean silenced, int activeSource, AudioEffect.Descriptor[] clientEffects, AudioEffect.Descriptor[] effects) {
        String packageName;
        AudioFormat clientFormat = new AudioFormat.Builder().setEncoding(recordingInfo[0]).setChannelMask(recordingInfo[1]).setSampleRate(recordingInfo[2]).build();
        AudioFormat deviceFormat = new AudioFormat.Builder().setEncoding(recordingInfo[3]).setChannelMask(recordingInfo[4]).setSampleRate(recordingInfo[5]).build();
        int patchHandle = recordingInfo[6];
        String[] packages = this.mPackMan.getPackagesForUid(uid);
        if (packages == null || packages.length <= 0) {
            packageName = "";
        } else {
            packageName = packages[0];
        }
        String[] strArr = packages;
        return new AudioRecordingConfiguration(uid, session, source, clientFormat, deviceFormat, patchHandle, packageName, portId, silenced, activeSource, clientEffects, effects);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0054, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00d0, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.List<android.media.AudioRecordingConfiguration> updateSnapshot(int r12, int r13, android.media.AudioRecordingConfiguration r14) {
        /*
            r11 = this;
            r0 = 0
            java.util.List<com.android.server.audio.RecordingActivityMonitor$RecordingState> r1 = r11.mRecordStates
            monitor-enter(r1)
            r2 = -1
            r3 = -1
            if (r13 == r3) goto L_0x0011
            int r4 = r11.findStateByRiid(r13)     // Catch:{ all -> 0x000e }
            r2 = r4
            goto L_0x001c
        L_0x000e:
            r2 = move-exception
            goto L_0x00d1
        L_0x0011:
            if (r14 == 0) goto L_0x001c
            int r4 = r14.getClientPortId()     // Catch:{ all -> 0x000e }
            int r4 = r11.findStateByPortId(r4)     // Catch:{ all -> 0x000e }
            r2 = r4
        L_0x001c:
            r4 = 2
            r5 = 0
            r6 = 1
            if (r2 != r3) goto L_0x0055
            if (r12 != 0) goto L_0x0038
            if (r14 == 0) goto L_0x0038
            java.util.List<com.android.server.audio.RecordingActivityMonitor$RecordingState> r3 = r11.mRecordStates     // Catch:{ all -> 0x000e }
            com.android.server.audio.RecordingActivityMonitor$RecordingState r7 = new com.android.server.audio.RecordingActivityMonitor$RecordingState     // Catch:{ all -> 0x000e }
            r7.<init>(r14)     // Catch:{ all -> 0x000e }
            r3.add(r7)     // Catch:{ all -> 0x000e }
            java.util.List<com.android.server.audio.RecordingActivityMonitor$RecordingState> r3 = r11.mRecordStates     // Catch:{ all -> 0x000e }
            int r3 = r3.size()     // Catch:{ all -> 0x000e }
            int r2 = r3 + -1
            goto L_0x0055
        L_0x0038:
            if (r14 != 0) goto L_0x0053
            java.lang.String r3 = "AudioService.RecordingActivityMonitor"
            java.lang.String r7 = "Unexpected event %d for riid %d"
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x000e }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r12)     // Catch:{ all -> 0x000e }
            r4[r5] = r8     // Catch:{ all -> 0x000e }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r13)     // Catch:{ all -> 0x000e }
            r4[r6] = r5     // Catch:{ all -> 0x000e }
            java.lang.String r4 = java.lang.String.format(r7, r4)     // Catch:{ all -> 0x000e }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x000e }
        L_0x0053:
            monitor-exit(r1)     // Catch:{ all -> 0x000e }
            return r0
        L_0x0055:
            java.util.List<com.android.server.audio.RecordingActivityMonitor$RecordingState> r3 = r11.mRecordStates     // Catch:{ all -> 0x000e }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ all -> 0x000e }
            com.android.server.audio.RecordingActivityMonitor$RecordingState r3 = (com.android.server.audio.RecordingActivityMonitor.RecordingState) r3     // Catch:{ all -> 0x000e }
            if (r12 == 0) goto L_0x00aa
            if (r12 == r6) goto L_0x009a
            if (r12 == r4) goto L_0x0095
            r7 = 3
            if (r12 == r7) goto L_0x008b
            java.lang.String r8 = "AudioService.RecordingActivityMonitor"
            java.lang.String r9 = "Unknown event %d for riid %d / portid %d"
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x000e }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r12)     // Catch:{ all -> 0x000e }
            r7[r5] = r10     // Catch:{ all -> 0x000e }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r13)     // Catch:{ all -> 0x000e }
            r7[r6] = r5     // Catch:{ all -> 0x000e }
            int r5 = r3.getPortId()     // Catch:{ all -> 0x000e }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x000e }
            r7[r4] = r5     // Catch:{ all -> 0x000e }
            java.lang.String r4 = java.lang.String.format(r9, r7)     // Catch:{ all -> 0x000e }
            android.util.Log.e(r8, r4)     // Catch:{ all -> 0x000e }
            r4 = 0
            goto L_0x00ba
        L_0x008b:
            boolean r4 = r3.isActiveConfiguration()     // Catch:{ all -> 0x000e }
            java.util.List<com.android.server.audio.RecordingActivityMonitor$RecordingState> r5 = r11.mRecordStates     // Catch:{ all -> 0x000e }
            r5.remove(r2)     // Catch:{ all -> 0x000e }
            goto L_0x00ba
        L_0x0095:
            boolean r4 = r3.setConfig(r14)     // Catch:{ all -> 0x000e }
            goto L_0x00ba
        L_0x009a:
            boolean r4 = r3.setActive(r5)     // Catch:{ all -> 0x000e }
            boolean r5 = r3.hasDeathHandler()     // Catch:{ all -> 0x000e }
            if (r5 != 0) goto L_0x00ba
            java.util.List<com.android.server.audio.RecordingActivityMonitor$RecordingState> r5 = r11.mRecordStates     // Catch:{ all -> 0x000e }
            r5.remove(r2)     // Catch:{ all -> 0x000e }
            goto L_0x00ba
        L_0x00aa:
            boolean r4 = r3.setActive(r6)     // Catch:{ all -> 0x000e }
            if (r14 == 0) goto L_0x00ba
            boolean r7 = r3.setConfig(r14)     // Catch:{ all -> 0x000e }
            if (r7 != 0) goto L_0x00b8
            if (r4 == 0) goto L_0x00b9
        L_0x00b8:
            r5 = r6
        L_0x00b9:
            r4 = r5
        L_0x00ba:
            if (r4 == 0) goto L_0x00cf
            com.android.server.audio.AudioEventLogger r5 = sEventLogger     // Catch:{ all -> 0x000e }
            com.android.server.audio.RecordingActivityMonitor$RecordingEvent r7 = new com.android.server.audio.RecordingActivityMonitor$RecordingEvent     // Catch:{ all -> 0x000e }
            android.media.AudioRecordingConfiguration r8 = r3.getConfig()     // Catch:{ all -> 0x000e }
            r7.<init>(r12, r13, r8)     // Catch:{ all -> 0x000e }
            r5.log(r7)     // Catch:{ all -> 0x000e }
            java.util.List r5 = r11.getActiveRecordingConfigurations(r6)     // Catch:{ all -> 0x000e }
            r0 = r5
        L_0x00cf:
            monitor-exit(r1)     // Catch:{ all -> 0x000e }
            return r0
        L_0x00d1:
            monitor-exit(r1)     // Catch:{ all -> 0x000e }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.RecordingActivityMonitor.updateSnapshot(int, int, android.media.AudioRecordingConfiguration):java.util.List");
    }

    private int findStateByRiid(int riid) {
        synchronized (this.mRecordStates) {
            for (int i = 0; i < this.mRecordStates.size(); i++) {
                if (this.mRecordStates.get(i).getRiid() == riid) {
                    return i;
                }
            }
            return -1;
        }
    }

    private int findStateByPortId(int portId) {
        synchronized (this.mRecordStates) {
            for (int i = 0; i < this.mRecordStates.size(); i++) {
                if (!this.mRecordStates.get(i).hasDeathHandler() && this.mRecordStates.get(i).getPortId() == portId) {
                    return i;
                }
            }
            return -1;
        }
    }

    private static final class RecMonitorClient implements IBinder.DeathRecipient {
        static RecordingActivityMonitor sMonitor;
        final IRecordingConfigDispatcher mDispatcherCb;
        final boolean mIsPrivileged;

        RecMonitorClient(IRecordingConfigDispatcher rcdb, boolean isPrivileged) {
            this.mDispatcherCb = rcdb;
            this.mIsPrivileged = isPrivileged;
        }

        public void binderDied() {
            Log.w(RecordingActivityMonitor.TAG, "client died");
            sMonitor.unregisterRecordingCallback(this.mDispatcherCb);
        }

        /* access modifiers changed from: package-private */
        public boolean init() {
            try {
                this.mDispatcherCb.asBinder().linkToDeath(this, 0);
                return true;
            } catch (RemoteException e) {
                Log.w(RecordingActivityMonitor.TAG, "Could not link to client death", e);
                return false;
            }
        }

        /* access modifiers changed from: package-private */
        public void release() {
            this.mDispatcherCb.asBinder().unlinkToDeath(this, 0);
        }
    }

    private static final class RecorderDeathHandler implements IBinder.DeathRecipient {
        static RecordingActivityMonitor sMonitor;
        private final IBinder mRecorderToken;
        final int mRiid;

        RecorderDeathHandler(int riid, IBinder recorderToken) {
            this.mRiid = riid;
            this.mRecorderToken = recorderToken;
        }

        public void binderDied() {
            sMonitor.releaseRecorder(this.mRiid);
        }

        /* access modifiers changed from: package-private */
        public boolean init() {
            try {
                this.mRecorderToken.linkToDeath(this, 0);
                return true;
            } catch (RemoteException e) {
                Log.w(RecordingActivityMonitor.TAG, "Could not link to recorder death", e);
                return false;
            }
        }
    }

    private static final class RecordingEvent extends AudioEventLogger.Event {
        private final int mClientUid;
        private final String mPackName;
        private final int mRIId;
        private final int mRecEvent;
        private final int mSession;
        private final int mSource;

        RecordingEvent(int event, int riid, AudioRecordingConfiguration config) {
            this.mRecEvent = event;
            this.mRIId = riid;
            if (config != null) {
                this.mClientUid = config.getClientUid();
                this.mSession = config.getClientAudioSessionId();
                this.mSource = config.getClientAudioSource();
                this.mPackName = config.getClientPackageName();
                return;
            }
            this.mClientUid = -1;
            this.mSession = -1;
            this.mSource = -1;
            this.mPackName = null;
        }

        private static String recordEventToString(int recEvent) {
            if (recEvent == 0) {
                return "start";
            }
            if (recEvent == 1) {
                return "stop";
            }
            if (recEvent == 2) {
                return "update";
            }
            if (recEvent == 3) {
                return "release";
            }
            return "unknown (" + recEvent + ")";
        }

        public String eventToString() {
            String str;
            StringBuilder sb = new StringBuilder("rec ");
            sb.append(recordEventToString(this.mRecEvent));
            sb.append(" riid:");
            sb.append(this.mRIId);
            sb.append(" uid:");
            sb.append(this.mClientUid);
            sb.append(" session:");
            sb.append(this.mSession);
            sb.append(" src:");
            sb.append(MediaRecorder.toLogFriendlyAudioSource(this.mSource));
            if (this.mPackName == null) {
                str = "";
            } else {
                str = " pack:" + this.mPackName;
            }
            sb.append(str);
            return sb.toString();
        }
    }
}
