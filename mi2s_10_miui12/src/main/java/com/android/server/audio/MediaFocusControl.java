package com.android.server.audio;

import android.app.AppOpsManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusInfo;
import android.media.IAudioFocusDispatcher;
import android.media.audiopolicy.IAudioPolicyCallback;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.server.audio.AudioEventLogger;
import com.android.server.slice.SliceClientPermissions;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class MediaFocusControl implements PlayerFocusEnforcer {
    static final boolean DEBUG = false;
    static final int DUCKING_IN_APP_SDK_LEVEL = 25;
    static final boolean ENFORCE_DUCKING = true;
    static final boolean ENFORCE_DUCKING_FOR_NEW = true;
    static final boolean ENFORCE_MUTING_FOR_RING_OR_CALL = true;
    private static final int MAX_STACK_SIZE = 100;
    private static final String[] NAVI_APP_PKG_NAME = {"com.autonavi.minimap", "com.baidu.BaiduMap", "com.tencent.map"};
    private static final int RING_CALL_MUTING_ENFORCEMENT_DELAY_MS = 100;
    private static final String TAG = "MediaFocusControl";
    /* access modifiers changed from: private */
    public static final int[] USAGES_TO_MUTE_IN_RING_OR_CALL = {1, 14};
    private static final String VOICE_ASSIST_PKG_NAME = "com.miui.voiceassist";
    /* access modifiers changed from: private */
    public static final Object mAudioFocusLock = new Object();
    private static final AudioEventLogger mEventLogger = new AudioEventLogger(50, "focus commands as seen by MediaFocusControl");
    private final AppOpsManager mAppOps;
    private final Context mContext;
    @GuardedBy({"mExtFocusChangeLock"})
    private long mExtFocusChangeCounter;
    private final Object mExtFocusChangeLock = new Object();
    /* access modifiers changed from: private */
    public PlayerFocusEnforcer mFocusEnforcer;
    private ArrayList<IAudioPolicyCallback> mFocusFollowers = new ArrayList<>();
    private HashMap<String, FocusRequester> mFocusOwnersForFocusPolicy = new HashMap<>();
    /* access modifiers changed from: private */
    @GuardedBy({"mAudioFocusLock"})
    public IAudioPolicyCallback mFocusPolicy = null;
    /* access modifiers changed from: private */
    public final Stack<FocusRequester> mFocusStack = new Stack<>();
    private boolean mNotifyFocusOwnerOnDuck = true;
    @GuardedBy({"mAudioFocusLock"})
    private IAudioPolicyCallback mPreviousFocusPolicy = null;
    /* access modifiers changed from: private */
    public boolean mRingOrCallActive = false;

    protected MediaFocusControl(Context cntxt, PlayerFocusEnforcer pfe) {
        this.mContext = cntxt;
        this.mAppOps = (AppOpsManager) this.mContext.getSystemService("appops");
        this.mFocusEnforcer = pfe;
    }

    /* access modifiers changed from: protected */
    public void dump(PrintWriter pw) {
        pw.println("\nMediaFocusControl dump time: " + DateFormat.getTimeInstance().format(new Date()));
        dumpFocusStack(pw);
        pw.println("\n");
        mEventLogger.dump(pw);
    }

    public boolean duckPlayers(FocusRequester winner, FocusRequester loser, boolean forceDuck) {
        return this.mFocusEnforcer.duckPlayers(winner, loser, forceDuck);
    }

    public void unduckPlayers(FocusRequester winner) {
        this.mFocusEnforcer.unduckPlayers(winner);
    }

    public void mutePlayersForCall(int[] usagesToMute) {
        this.mFocusEnforcer.mutePlayersForCall(usagesToMute);
    }

    public void unmutePlayersForCall() {
        this.mFocusEnforcer.unmutePlayersForCall();
    }

    /* access modifiers changed from: protected */
    public void dump(PrintWriter pw, String[] args) {
        for (String arg : args) {
            if (arg.equalsIgnoreCase("focusStack")) {
                dumpFocusStack(pw);
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void noFocusForSuspendedApp(String packageName, int uid) {
        synchronized (mAudioFocusLock) {
            Iterator<FocusRequester> stackIterator = this.mFocusStack.iterator();
            List<String> clientsToRemove = new ArrayList<>();
            while (stackIterator.hasNext()) {
                FocusRequester focusOwner = stackIterator.next();
                if (focusOwner.hasSameUid(uid) && focusOwner.hasSamePackage(packageName)) {
                    clientsToRemove.add(focusOwner.getClientId());
                    AudioEventLogger audioEventLogger = mEventLogger;
                    audioEventLogger.log(new AudioEventLogger.StringEvent("focus owner:" + focusOwner.getClientId() + " in uid:" + uid + " pack: " + packageName + " getting AUDIOFOCUS_LOSS due to app suspension").printLog(TAG));
                    focusOwner.dispatchFocusChange(-1);
                }
            }
            for (String clientToRemove : clientsToRemove) {
                removeFocusStackEntry(clientToRemove, false, true);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasAudioFocusUsers() {
        boolean z;
        synchronized (mAudioFocusLock) {
            z = !this.mFocusStack.empty();
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public void discardAudioFocusOwner() {
        synchronized (mAudioFocusLock) {
            if (!this.mFocusStack.empty()) {
                FocusRequester exFocusOwner = this.mFocusStack.pop();
                exFocusOwner.handleFocusLoss(-1, (FocusRequester) null, false);
                exFocusOwner.release();
            }
        }
    }

    @GuardedBy({"mAudioFocusLock"})
    private void notifyTopOfAudioFocusStack() {
        if (!this.mFocusStack.empty() && canReassignAudioFocus()) {
            this.mFocusStack.peek().handleFocusGain(1);
        }
    }

    @GuardedBy({"mAudioFocusLock"})
    private void propagateFocusLossFromGain_syncAf(int focusGain, FocusRequester fr, boolean forceDuck) {
        List<String> clientsToRemove = new LinkedList<>();
        Iterator it = this.mFocusStack.iterator();
        while (it.hasNext()) {
            FocusRequester focusLoser = (FocusRequester) it.next();
            if (focusLoser.handleFocusLossFromGain(focusGain, fr, forceDuck)) {
                clientsToRemove.add(focusLoser.getClientId());
            }
        }
        for (String clientToRemove : clientsToRemove) {
            removeFocusStackEntry(clientToRemove, false, true);
        }
    }

    private void dumpFocusStack(PrintWriter pw) {
        pw.println("\nAudio Focus stack entries (last is top of stack):");
        synchronized (mAudioFocusLock) {
            Iterator<FocusRequester> stackIterator = this.mFocusStack.iterator();
            while (stackIterator.hasNext()) {
                stackIterator.next().dump(pw);
            }
            pw.println("\n");
            if (this.mFocusPolicy == null) {
                pw.println("No external focus policy\n");
            } else {
                pw.println("External focus policy: " + this.mFocusPolicy + ", focus owners:\n");
                dumpExtFocusPolicyFocusOwners(pw);
            }
        }
        pw.println("\n");
        pw.println(" Notify on duck:  " + this.mNotifyFocusOwnerOnDuck + "\n");
        pw.println(" In ring or call: " + this.mRingOrCallActive + "\n");
    }

    @GuardedBy({"mAudioFocusLock"})
    private void removeFocusStackEntry(String clientToRemove, boolean signal, boolean notifyFocusFollowers) {
        if (this.mFocusStack.empty() || !this.mFocusStack.peek().hasSameClient(clientToRemove)) {
            Iterator<FocusRequester> stackIterator = this.mFocusStack.iterator();
            while (stackIterator.hasNext()) {
                FocusRequester fr = stackIterator.next();
                if (fr.hasSameClient(clientToRemove)) {
                    Log.i(TAG, "AudioFocus  removeFocusStackEntry(): removing entry for " + clientToRemove);
                    stackIterator.remove();
                    fr.release();
                }
            }
            return;
        }
        FocusRequester fr2 = this.mFocusStack.pop();
        fr2.release();
        if (notifyFocusFollowers) {
            AudioFocusInfo afi = fr2.toAudioFocusInfo();
            afi.clearLossReceived();
            notifyExtPolicyFocusLoss_syncAf(afi, false);
        }
        if (signal) {
            notifyTopOfAudioFocusStack();
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mAudioFocusLock"})
    public void removeFocusStackEntryOnDeath(IBinder cb) {
        boolean isTopOfStackForClientToRemove = !this.mFocusStack.isEmpty() && this.mFocusStack.peek().hasSameBinder(cb);
        Iterator<FocusRequester> stackIterator = this.mFocusStack.iterator();
        while (stackIterator.hasNext()) {
            FocusRequester fr = stackIterator.next();
            if (fr.hasSameBinder(cb)) {
                Log.i(TAG, "AudioFocus  removeFocusStackEntryOnDeath(): removing entry for " + cb);
                stackIterator.remove();
                fr.release();
            }
        }
        if (isTopOfStackForClientToRemove) {
            notifyTopOfAudioFocusStack();
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mAudioFocusLock"})
    public void removeFocusEntryForExtPolicy(IBinder cb) {
        if (!this.mFocusOwnersForFocusPolicy.isEmpty()) {
            Iterator<Map.Entry<String, FocusRequester>> ownerIterator = this.mFocusOwnersForFocusPolicy.entrySet().iterator();
            while (ownerIterator.hasNext()) {
                FocusRequester fr = ownerIterator.next().getValue();
                if (fr.hasSameBinder(cb)) {
                    ownerIterator.remove();
                    fr.release();
                    notifyExtFocusPolicyFocusAbandon_syncAf(fr.toAudioFocusInfo());
                    return;
                }
            }
        }
    }

    private boolean canReassignAudioFocus() {
        if (this.mFocusStack.isEmpty() || !isLockedFocusOwner(this.mFocusStack.peek())) {
            return true;
        }
        return false;
    }

    private boolean isLockedFocusOwner(FocusRequester fr) {
        return fr.hasSameClient("AudioFocus_For_Phone_Ring_And_Calls") || fr.isLockedFocusOwner();
    }

    private boolean isNaviFocusOwner(FocusRequester fr) {
        return (fr.getGrantFlags() & 16) != 0;
    }

    private boolean shouldLockForNavigation(String callingPackageName) {
        String lowerPackageName = callingPackageName.toLowerCase();
        int index = 0;
        while (true) {
            String[] strArr = NAVI_APP_PKG_NAME;
            if (index >= strArr.length) {
                return false;
            }
            if (lowerPackageName.startsWith(strArr[index].toLowerCase())) {
                return true;
            }
            index++;
        }
    }

    @GuardedBy({"mAudioFocusLock"})
    private int pushBelowLockedFocusOwners(FocusRequester nfr) {
        int lastLockedFocusOwnerIndex = this.mFocusStack.size();
        for (int index = this.mFocusStack.size() - 1; index >= 0; index--) {
            if (isLockedFocusOwner((FocusRequester) this.mFocusStack.elementAt(index))) {
                lastLockedFocusOwnerIndex = index;
            }
        }
        if (lastLockedFocusOwnerIndex == this.mFocusStack.size()) {
            Log.e(TAG, "No exclusive focus owner found in propagateFocusLossFromGain_syncAf()", new Exception());
            propagateFocusLossFromGain_syncAf(nfr.getGainRequest(), nfr, false);
            this.mFocusStack.push(nfr);
            return 1;
        }
        this.mFocusStack.insertElementAt(nfr, lastLockedFocusOwnerIndex);
        return 2;
    }

    private int pushBelowNaviFocusOwners(FocusRequester nfr) {
        int lastLockedFocusOwnerIndex = this.mFocusStack.size();
        for (int index = this.mFocusStack.size() - 1; index >= 0; index--) {
            if (isNaviFocusOwner((FocusRequester) this.mFocusStack.elementAt(index))) {
                lastLockedFocusOwnerIndex = index;
            }
        }
        if (lastLockedFocusOwnerIndex == this.mFocusStack.size()) {
            Log.e(TAG, "No navigation focus owner found in propagateFocusLossFromGain_syncAf()", new Exception());
            propagateFocusLossFromGain_syncAf(nfr.getGainRequest(), nfr, false);
            this.mFocusStack.push(nfr);
            return 1;
        }
        Log.i(TAG, "insert Focus below:" + ((FocusRequester) this.mFocusStack.elementAt(lastLockedFocusOwnerIndex)).getClientId() + " for " + nfr.getClientId());
        this.mFocusStack.insertElementAt(nfr, lastLockedFocusOwnerIndex);
        return 2;
    }

    protected class AudioFocusDeathHandler implements IBinder.DeathRecipient {
        private IBinder mCb;

        AudioFocusDeathHandler(IBinder cb) {
            this.mCb = cb;
        }

        public void binderDied() {
            synchronized (MediaFocusControl.mAudioFocusLock) {
                if (MediaFocusControl.this.mFocusPolicy != null) {
                    MediaFocusControl.this.removeFocusEntryForExtPolicy(this.mCb);
                } else {
                    MediaFocusControl.this.removeFocusStackEntryOnDeath(this.mCb);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setDuckingInExtPolicyAvailable(boolean available) {
        this.mNotifyFocusOwnerOnDuck = !available;
    }

    /* access modifiers changed from: package-private */
    public boolean mustNotifyFocusOwnerOnDuck() {
        return this.mNotifyFocusOwnerOnDuck;
    }

    /* access modifiers changed from: package-private */
    public void addFocusFollower(IAudioPolicyCallback ff) {
        if (ff != null) {
            synchronized (mAudioFocusLock) {
                boolean found = false;
                Iterator<IAudioPolicyCallback> it = this.mFocusFollowers.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    } else if (it.next().asBinder().equals(ff.asBinder())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    this.mFocusFollowers.add(ff);
                    notifyExtPolicyCurrentFocusAsync(ff);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void removeFocusFollower(IAudioPolicyCallback ff) {
        if (ff != null) {
            synchronized (mAudioFocusLock) {
                Iterator<IAudioPolicyCallback> it = this.mFocusFollowers.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    IAudioPolicyCallback pcb = it.next();
                    if (pcb.asBinder().equals(ff.asBinder())) {
                        this.mFocusFollowers.remove(pcb);
                        break;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setFocusPolicy(IAudioPolicyCallback policy, boolean isTestFocusPolicy) {
        if (policy != null) {
            synchronized (mAudioFocusLock) {
                if (isTestFocusPolicy) {
                    this.mPreviousFocusPolicy = this.mFocusPolicy;
                }
                this.mFocusPolicy = policy;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void unsetFocusPolicy(IAudioPolicyCallback policy, boolean isTestFocusPolicy) {
        if (policy != null) {
            synchronized (mAudioFocusLock) {
                if (this.mFocusPolicy == policy) {
                    if (isTestFocusPolicy) {
                        this.mFocusPolicy = this.mPreviousFocusPolicy;
                    } else {
                        this.mFocusPolicy = null;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyExtPolicyCurrentFocusAsync(IAudioPolicyCallback pcb) {
        final IAudioPolicyCallback pcb2 = pcb;
        new Thread() {
            public void run() {
                synchronized (MediaFocusControl.mAudioFocusLock) {
                    if (!MediaFocusControl.this.mFocusStack.isEmpty()) {
                        try {
                            pcb2.notifyAudioFocusGrant(((FocusRequester) MediaFocusControl.this.mFocusStack.peek()).toAudioFocusInfo(), 1);
                        } catch (RemoteException e) {
                            Log.e(MediaFocusControl.TAG, "Can't call notifyAudioFocusGrant() on IAudioPolicyCallback " + pcb2.asBinder(), e);
                        }
                    }
                }
            }
        }.start();
    }

    /* access modifiers changed from: package-private */
    public void notifyExtPolicyFocusGrant_syncAf(AudioFocusInfo afi, int requestResult) {
        Iterator<IAudioPolicyCallback> it = this.mFocusFollowers.iterator();
        while (it.hasNext()) {
            IAudioPolicyCallback pcb = it.next();
            try {
                pcb.notifyAudioFocusGrant(afi, requestResult);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call notifyAudioFocusGrant() on IAudioPolicyCallback " + pcb.asBinder(), e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyExtPolicyFocusLoss_syncAf(AudioFocusInfo afi, boolean wasDispatched) {
        Iterator<IAudioPolicyCallback> it = this.mFocusFollowers.iterator();
        while (it.hasNext()) {
            IAudioPolicyCallback pcb = it.next();
            try {
                pcb.notifyAudioFocusLoss(afi, wasDispatched);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call notifyAudioFocusLoss() on IAudioPolicyCallback " + pcb.asBinder(), e);
            }
        }
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x008f, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean notifyExtFocusPolicyFocusRequest_syncAf(android.media.AudioFocusInfo r17, android.media.IAudioFocusDispatcher r18, android.os.IBinder r19) {
        /*
            r16 = this;
            r7 = r16
            r8 = r17
            r9 = r19
            java.lang.Object r1 = r7.mExtFocusChangeLock
            monitor-enter(r1)
            long r2 = r7.mExtFocusChangeCounter     // Catch:{ all -> 0x008a }
            r4 = 1
            long r4 = r4 + r2
            r7.mExtFocusChangeCounter = r4     // Catch:{ all -> 0x008a }
            r8.setGen(r2)     // Catch:{ all -> 0x008a }
            monitor-exit(r1)     // Catch:{ all -> 0x008a }
            java.util.HashMap<java.lang.String, com.android.server.audio.FocusRequester> r0 = r7.mFocusOwnersForFocusPolicy
            java.lang.String r1 = r17.getClientId()
            java.lang.Object r0 = r0.get(r1)
            r10 = r0
            com.android.server.audio.FocusRequester r10 = (com.android.server.audio.FocusRequester) r10
            r0 = 0
            if (r10 == 0) goto L_0x0034
            r11 = r18
            boolean r1 = r10.hasSameDispatcher(r11)
            if (r1 != 0) goto L_0x0032
            r10.release()
            r0 = 1
            r12 = r0
            goto L_0x0038
        L_0x0032:
            r12 = r0
            goto L_0x0038
        L_0x0034:
            r11 = r18
            r0 = 1
            r12 = r0
        L_0x0038:
            r13 = 0
            if (r12 == 0) goto L_0x0064
            com.android.server.audio.MediaFocusControl$AudioFocusDeathHandler r0 = new com.android.server.audio.MediaFocusControl$AudioFocusDeathHandler
            r0.<init>(r9)
            r14 = r0
            r9.linkToDeath(r14, r13)     // Catch:{ RemoteException -> 0x005f }
            java.util.HashMap<java.lang.String, com.android.server.audio.FocusRequester> r0 = r7.mFocusOwnersForFocusPolicy
            java.lang.String r15 = r17.getClientId()
            com.android.server.audio.FocusRequester r6 = new com.android.server.audio.FocusRequester
            r1 = r6
            r2 = r17
            r3 = r18
            r4 = r19
            r5 = r14
            r13 = r6
            r6 = r16
            r1.<init>(r2, r3, r4, r5, r6)
            r0.put(r15, r13)
            goto L_0x0064
        L_0x005f:
            r0 = move-exception
            r1 = r0
            r0 = r1
            r1 = 0
            return r1
        L_0x0064:
            android.media.audiopolicy.IAudioPolicyCallback r0 = r7.mFocusPolicy     // Catch:{ RemoteException -> 0x006b }
            r1 = 1
            r0.notifyAudioFocusRequest(r8, r1)     // Catch:{ RemoteException -> 0x006b }
            return r1
        L_0x006b:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Can't call notifyAudioFocusRequest() on IAudioPolicyCallback "
            r1.append(r2)
            android.media.audiopolicy.IAudioPolicyCallback r2 = r7.mFocusPolicy
            android.os.IBinder r2 = r2.asBinder()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "MediaFocusControl"
            android.util.Log.e(r2, r1, r0)
            r1 = 0
            return r1
        L_0x008a:
            r0 = move-exception
            r11 = r18
        L_0x008d:
            monitor-exit(r1)     // Catch:{ all -> 0x008f }
            throw r0
        L_0x008f:
            r0 = move-exception
            goto L_0x008d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.MediaFocusControl.notifyExtFocusPolicyFocusRequest_syncAf(android.media.AudioFocusInfo, android.media.IAudioFocusDispatcher, android.os.IBinder):boolean");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001e, code lost:
        r0.dispatchFocusResultFromExtPolicy(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0010, code lost:
        r0 = r5.mFocusOwnersForFocusPolicy.get(r6.getClientId());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001c, code lost:
        if (r0 == null) goto L_?;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setFocusRequestResultFromExtPolicy(android.media.AudioFocusInfo r6, int r7) {
        /*
            r5 = this;
            java.lang.Object r0 = r5.mExtFocusChangeLock
            monitor-enter(r0)
            long r1 = r6.getGen()     // Catch:{ all -> 0x0022 }
            long r3 = r5.mExtFocusChangeCounter     // Catch:{ all -> 0x0022 }
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 <= 0) goto L_0x000f
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            return
        L_0x000f:
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            java.util.HashMap<java.lang.String, com.android.server.audio.FocusRequester> r0 = r5.mFocusOwnersForFocusPolicy
            java.lang.String r1 = r6.getClientId()
            java.lang.Object r0 = r0.get(r1)
            com.android.server.audio.FocusRequester r0 = (com.android.server.audio.FocusRequester) r0
            if (r0 == 0) goto L_0x0021
            r0.dispatchFocusResultFromExtPolicy(r7)
        L_0x0021:
            return
        L_0x0022:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.MediaFocusControl.setFocusRequestResultFromExtPolicy(android.media.AudioFocusInfo, int):void");
    }

    /* access modifiers changed from: package-private */
    public boolean notifyExtFocusPolicyFocusAbandon_syncAf(AudioFocusInfo afi) {
        if (this.mFocusPolicy == null) {
            return false;
        }
        FocusRequester fr = this.mFocusOwnersForFocusPolicy.remove(afi.getClientId());
        if (fr != null) {
            fr.release();
        }
        try {
            this.mFocusPolicy.notifyAudioFocusAbandon(afi);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "Can't call notifyAudioFocusAbandon() on IAudioPolicyCallback " + this.mFocusPolicy.asBinder(), e);
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public int dispatchFocusChange(AudioFocusInfo afi, int focusChange) {
        FocusRequester fr;
        synchronized (mAudioFocusLock) {
            if (this.mFocusPolicy == null) {
                return 0;
            }
            if (focusChange == -1) {
                fr = this.mFocusOwnersForFocusPolicy.remove(afi.getClientId());
            } else {
                fr = this.mFocusOwnersForFocusPolicy.get(afi.getClientId());
            }
            if (fr == null) {
                return 0;
            }
            int dispatchFocusChange = fr.dispatchFocusChange(focusChange);
            return dispatchFocusChange;
        }
    }

    private void dumpExtFocusPolicyFocusOwners(PrintWriter pw) {
        for (Map.Entry<String, FocusRequester> owner : this.mFocusOwnersForFocusPolicy.entrySet()) {
            owner.getValue().dump(pw);
        }
    }

    /* access modifiers changed from: protected */
    public int getCurrentAudioFocus() {
        synchronized (mAudioFocusLock) {
            if (this.mFocusStack.empty()) {
                return 0;
            }
            int gainRequest = this.mFocusStack.peek().getGainRequest();
            return gainRequest;
        }
    }

    protected static int getFocusRampTimeMs(int focusGain, AudioAttributes attr) {
        switch (attr.getUsage()) {
            case 1:
            case 14:
                return 1000;
            case 2:
            case 3:
            case 5:
            case 7:
            case 8:
            case 9:
            case 10:
            case 13:
                return 500;
            case 4:
            case 6:
            case 11:
            case 12:
            case 16:
                return 700;
            default:
                return 0;
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x0213, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x022b, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x0253, code lost:
        return r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x0260, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:0x0261, code lost:
        r1 = r33;
        r19 = r6;
        r22 = r8;
        r23 = r9;
        r0 = r0;
        r3 = new java.lang.StringBuilder();
        r3.append("AudioFocus  requestAudioFocus() could not link to ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:?, code lost:
        r3.append(r27);
        r3.append(" binder death");
        android.util.Log.w(TAG, r3.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x0289, code lost:
        return r10 ? 1 : 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:167:0x02ab, code lost:
        r0 = th;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x0205 A[SYNTHETIC, Splitter:B:117:0x0205] */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x021b A[Catch:{ all -> 0x0214 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int requestAudioFocus(android.media.AudioAttributes r25, int r26, android.os.IBinder r27, android.media.IAudioFocusDispatcher r28, java.lang.String r29, java.lang.String r30, int r31, int r32, boolean r33) {
        /*
            r24 = this;
            r13 = r24
            r14 = r26
            r15 = r27
            r12 = r29
            r11 = r30
            com.android.server.audio.AudioEventLogger r0 = mEventLogger
            com.android.server.audio.AudioEventLogger$StringEvent r1 = new com.android.server.audio.AudioEventLogger$StringEvent
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "requestAudioFocus() from uid/pid "
            r2.append(r3)
            int r3 = android.os.Binder.getCallingUid()
            r2.append(r3)
            java.lang.String r3 = "/"
            r2.append(r3)
            int r3 = android.os.Binder.getCallingPid()
            r2.append(r3)
            java.lang.String r3 = " clientId="
            r2.append(r3)
            r2.append(r12)
            java.lang.String r3 = " callingPack="
            r2.append(r3)
            r2.append(r11)
            java.lang.String r3 = " req="
            r2.append(r3)
            r2.append(r14)
            java.lang.String r3 = " flags=0x"
            r2.append(r3)
            java.lang.String r3 = java.lang.Integer.toHexString(r31)
            r2.append(r3)
            java.lang.String r3 = " sdk="
            r2.append(r3)
            r10 = r32
            r2.append(r10)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            java.lang.String r2 = "MediaFocusControl"
            com.android.server.audio.AudioEventLogger$Event r1 = r1.printLog(r2)
            r0.log(r1)
            boolean r0 = r27.pingBinder()
            r9 = 0
            if (r0 != 0) goto L_0x0079
            java.lang.String r0 = "MediaFocusControl"
            java.lang.String r1 = " AudioFocus DOA client for requestAudioFocus(), aborting."
            android.util.Log.e(r0, r1)
            return r9
        L_0x0079:
            android.app.AppOpsManager r0 = r13.mAppOps
            r1 = 32
            int r2 = android.os.Binder.getCallingUid()
            int r0 = r0.noteOp(r1, r2, r11)
            if (r0 == 0) goto L_0x0088
            return r9
        L_0x0088:
            java.lang.Object r16 = mAudioFocusLock
            monitor-enter(r16)
            java.util.Stack<com.android.server.audio.FocusRequester> r0 = r13.mFocusStack     // Catch:{ all -> 0x02a3 }
            int r0 = r0.size()     // Catch:{ all -> 0x02a3 }
            r8 = 100
            if (r0 <= r8) goto L_0x00a6
            java.lang.String r0 = "MediaFocusControl"
            java.lang.String r1 = "Max AudioFocus stack size reached, failing requestAudioFocus()"
            android.util.Log.e(r0, r1)     // Catch:{ all -> 0x009e }
            monitor-exit(r16)     // Catch:{ all -> 0x009e }
            return r9
        L_0x009e:
            r0 = move-exception
            r22 = r31
            r1 = r33
            r4 = r15
            goto L_0x02a9
        L_0x00a6:
            boolean r0 = r13.mRingOrCallActive     // Catch:{ all -> 0x02a3 }
            r7 = 1
            if (r0 != 0) goto L_0x00ad
            r0 = r7
            goto L_0x00ae
        L_0x00ad:
            r0 = r9
        L_0x00ae:
            java.lang.String r1 = "AudioFocus_For_Phone_Ring_And_Calls"
            int r1 = r1.compareTo(r12)     // Catch:{ all -> 0x02a3 }
            if (r1 != 0) goto L_0x00b8
            r1 = r7
            goto L_0x00b9
        L_0x00b8:
            r1 = r9
        L_0x00b9:
            r17 = r0 & r1
            if (r17 == 0) goto L_0x00bf
            r13.mRingOrCallActive = r7     // Catch:{ all -> 0x009e }
        L_0x00bf:
            boolean r0 = r13.shouldLockForNavigation(r11)     // Catch:{ all -> 0x02a3 }
            if (r0 == 0) goto L_0x00e0
            java.lang.String r0 = "MediaFocusControl"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x009e }
            r1.<init>()     // Catch:{ all -> 0x009e }
            java.lang.String r2 = "set NAVI_LOCK for "
            r1.append(r2)     // Catch:{ all -> 0x009e }
            r1.append(r11)     // Catch:{ all -> 0x009e }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x009e }
            android.util.Log.i(r0, r1)     // Catch:{ all -> 0x009e }
            r0 = r31 | 16
            r6 = r0
            goto L_0x00e2
        L_0x00e0:
            r6 = r31
        L_0x00e2:
            android.media.audiopolicy.IAudioPolicyCallback r0 = r13.mFocusPolicy     // Catch:{ all -> 0x029c }
            if (r0 == 0) goto L_0x0112
            android.media.AudioFocusInfo r0 = new android.media.AudioFocusInfo     // Catch:{ all -> 0x0108 }
            int r3 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x0108 }
            r18 = 0
            r1 = r0
            r2 = r25
            r4 = r29
            r5 = r30
            r31 = r6
            r6 = r26
            r10 = r7
            r7 = r18
            r18 = r8
            r8 = r31
            r10 = r9
            r9 = r32
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x009e }
            r9 = r0
            goto L_0x0119
        L_0x0108:
            r0 = move-exception
            r31 = r6
            r22 = r31
            r1 = r33
            r4 = r15
            goto L_0x02a9
        L_0x0112:
            r31 = r6
            r18 = r8
            r10 = r9
            r0 = 0
            r9 = r0
        L_0x0119:
            r0 = 0
            boolean r1 = r24.canReassignAudioFocus()     // Catch:{ all -> 0x0295 }
            if (r1 != 0) goto L_0x012c
            r8 = r31
            r1 = r8 & 1
            if (r1 != 0) goto L_0x0128
            monitor-exit(r16)     // Catch:{ all -> 0x016f }
            return r10
        L_0x0128:
            r0 = 1
            r20 = r0
            goto L_0x0130
        L_0x012c:
            r8 = r31
            r20 = r0
        L_0x0130:
            r0 = 0
            if (r20 != 0) goto L_0x0177
            java.util.Stack<com.android.server.audio.FocusRequester> r1 = r13.mFocusStack     // Catch:{ all -> 0x016f }
            boolean r1 = r1.empty()     // Catch:{ all -> 0x016f }
            if (r1 != 0) goto L_0x0177
            java.lang.String r1 = r30.toLowerCase()     // Catch:{ all -> 0x016f }
            java.lang.String r2 = "com.miui.voiceassist"
            boolean r1 = r1.equals(r2)     // Catch:{ all -> 0x016f }
            if (r1 == 0) goto L_0x0177
            java.util.Stack<com.android.server.audio.FocusRequester> r1 = r13.mFocusStack     // Catch:{ all -> 0x016f }
            java.lang.Object r1 = r1.peek()     // Catch:{ all -> 0x016f }
            com.android.server.audio.FocusRequester r1 = (com.android.server.audio.FocusRequester) r1     // Catch:{ all -> 0x016f }
            boolean r1 = r13.isNaviFocusOwner(r1)     // Catch:{ all -> 0x016f }
            if (r1 == 0) goto L_0x0177
            java.lang.String r1 = "MediaFocusControl"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x016f }
            r2.<init>()     // Catch:{ all -> 0x016f }
            java.lang.String r3 = "delay focus grant for "
            r2.append(r3)     // Catch:{ all -> 0x016f }
            r2.append(r11)     // Catch:{ all -> 0x016f }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x016f }
            android.util.Log.d(r1, r2)     // Catch:{ all -> 0x016f }
            r0 = 1
            r21 = r0
            goto L_0x0179
        L_0x016f:
            r0 = move-exception
            r1 = r33
            r22 = r8
            r4 = r15
            goto L_0x02a9
        L_0x0177:
            r21 = r0
        L_0x0179:
            android.media.audiopolicy.IAudioPolicyCallback r0 = r13.mFocusPolicy     // Catch:{ all -> 0x028e }
            if (r0 == 0) goto L_0x0189
            r7 = r28
            boolean r0 = r13.notifyExtFocusPolicyFocusRequest_syncAf(r9, r7, r15)     // Catch:{ all -> 0x016f }
            if (r0 == 0) goto L_0x0187
            monitor-exit(r16)     // Catch:{ all -> 0x016f }
            return r18
        L_0x0187:
            monitor-exit(r16)     // Catch:{ all -> 0x016f }
            return r10
        L_0x0189:
            r7 = r28
            com.android.server.audio.MediaFocusControl$AudioFocusDeathHandler r0 = new com.android.server.audio.MediaFocusControl$AudioFocusDeathHandler     // Catch:{ all -> 0x028e }
            r0.<init>(r15)     // Catch:{ all -> 0x028e }
            r6 = r0
            r15.linkToDeath(r6, r10)     // Catch:{ RemoteException -> 0x0260 }
            java.util.Stack<com.android.server.audio.FocusRequester> r0 = r13.mFocusStack     // Catch:{ all -> 0x0258 }
            boolean r0 = r0.empty()     // Catch:{ all -> 0x0258 }
            if (r0 != 0) goto L_0x01d8
            java.util.Stack<com.android.server.audio.FocusRequester> r0 = r13.mFocusStack     // Catch:{ all -> 0x016f }
            java.lang.Object r0 = r0.peek()     // Catch:{ all -> 0x016f }
            com.android.server.audio.FocusRequester r0 = (com.android.server.audio.FocusRequester) r0     // Catch:{ all -> 0x016f }
            boolean r0 = r0.hasSameClient(r12)     // Catch:{ all -> 0x016f }
            if (r0 == 0) goto L_0x01d8
            java.util.Stack<com.android.server.audio.FocusRequester> r0 = r13.mFocusStack     // Catch:{ all -> 0x016f }
            java.lang.Object r0 = r0.peek()     // Catch:{ all -> 0x016f }
            com.android.server.audio.FocusRequester r0 = (com.android.server.audio.FocusRequester) r0     // Catch:{ all -> 0x016f }
            int r1 = r0.getGainRequest()     // Catch:{ all -> 0x016f }
            if (r1 != r14) goto L_0x01cc
            int r1 = r0.getGrantFlags()     // Catch:{ all -> 0x016f }
            if (r1 != r8) goto L_0x01cc
            r15.unlinkToDeath(r6, r10)     // Catch:{ all -> 0x016f }
            android.media.AudioFocusInfo r1 = r0.toAudioFocusInfo()     // Catch:{ all -> 0x016f }
            r5 = 1
            r13.notifyExtPolicyFocusGrant_syncAf(r1, r5)     // Catch:{ all -> 0x016f }
            monitor-exit(r16)     // Catch:{ all -> 0x016f }
            return r5
        L_0x01cc:
            r5 = 1
            if (r20 != 0) goto L_0x01d9
            java.util.Stack<com.android.server.audio.FocusRequester> r1 = r13.mFocusStack     // Catch:{ all -> 0x016f }
            r1.pop()     // Catch:{ all -> 0x016f }
            r0.release()     // Catch:{ all -> 0x016f }
            goto L_0x01d9
        L_0x01d8:
            r5 = 1
        L_0x01d9:
            r13.removeFocusStackEntry(r12, r10, r10)     // Catch:{ all -> 0x0258 }
            com.android.server.audio.FocusRequester r0 = new com.android.server.audio.FocusRequester     // Catch:{ all -> 0x0258 }
            int r10 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x0258 }
            r1 = r0
            r2 = r25
            r3 = r26
            r4 = r8
            r18 = r5
            r5 = r28
            r19 = r6
            r6 = r27
            r7 = r29
            r22 = r8
            r8 = r19
            r23 = r9
            r9 = r30
            r15 = r18
            r11 = r24
            r12 = r32
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)     // Catch:{ all -> 0x0254 }
            if (r20 == 0) goto L_0x021b
            int r1 = r13.pushBelowLockedFocusOwners(r0)     // Catch:{ all -> 0x0214 }
            if (r1 == 0) goto L_0x0212
            android.media.AudioFocusInfo r2 = r0.toAudioFocusInfo()     // Catch:{ all -> 0x0214 }
            r13.notifyExtPolicyFocusGrant_syncAf(r2, r1)     // Catch:{ all -> 0x0214 }
        L_0x0212:
            monitor-exit(r16)     // Catch:{ all -> 0x0214 }
            return r1
        L_0x0214:
            r0 = move-exception
            r4 = r27
            r1 = r33
            goto L_0x02a9
        L_0x021b:
            if (r21 == 0) goto L_0x022c
            int r1 = r13.pushBelowNaviFocusOwners(r0)     // Catch:{ all -> 0x0214 }
            if (r1 == 0) goto L_0x022a
            android.media.AudioFocusInfo r2 = r0.toAudioFocusInfo()     // Catch:{ all -> 0x0214 }
            r13.notifyExtPolicyFocusGrant_syncAf(r2, r1)     // Catch:{ all -> 0x0214 }
        L_0x022a:
            monitor-exit(r16)     // Catch:{ all -> 0x0214 }
            return r1
        L_0x022c:
            java.util.Stack<com.android.server.audio.FocusRequester> r1 = r13.mFocusStack     // Catch:{ all -> 0x0254 }
            boolean r1 = r1.empty()     // Catch:{ all -> 0x0254 }
            if (r1 != 0) goto L_0x023a
            r1 = r33
            r13.propagateFocusLossFromGain_syncAf(r14, r0, r1)     // Catch:{ all -> 0x028a }
            goto L_0x023c
        L_0x023a:
            r1 = r33
        L_0x023c:
            java.util.Stack<com.android.server.audio.FocusRequester> r2 = r13.mFocusStack     // Catch:{ all -> 0x028a }
            r2.push(r0)     // Catch:{ all -> 0x028a }
            r0.handleFocusGainFromRequest(r15)     // Catch:{ all -> 0x028a }
            android.media.AudioFocusInfo r2 = r0.toAudioFocusInfo()     // Catch:{ all -> 0x028a }
            r13.notifyExtPolicyFocusGrant_syncAf(r2, r15)     // Catch:{ all -> 0x028a }
            r2 = r17 & 1
            if (r2 == 0) goto L_0x0252
            r13.runAudioCheckerForRingOrCallAsync(r15)     // Catch:{ all -> 0x028a }
        L_0x0252:
            monitor-exit(r16)     // Catch:{ all -> 0x028a }
            return r15
        L_0x0254:
            r0 = move-exception
            r1 = r33
            goto L_0x028b
        L_0x0258:
            r0 = move-exception
            r1 = r33
            r22 = r8
            r4 = r27
            goto L_0x0294
        L_0x0260:
            r0 = move-exception
            r1 = r33
            r19 = r6
            r22 = r8
            r23 = r9
            r2 = r0
            r0 = r2
            java.lang.String r2 = "MediaFocusControl"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x028a }
            r3.<init>()     // Catch:{ all -> 0x028a }
            java.lang.String r4 = "AudioFocus  requestAudioFocus() could not link to "
            r3.append(r4)     // Catch:{ all -> 0x028a }
            r4 = r27
            r3.append(r4)     // Catch:{ all -> 0x02ab }
            java.lang.String r5 = " binder death"
            r3.append(r5)     // Catch:{ all -> 0x02ab }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x02ab }
            android.util.Log.w(r2, r3)     // Catch:{ all -> 0x02ab }
            monitor-exit(r16)     // Catch:{ all -> 0x02ab }
            return r10
        L_0x028a:
            r0 = move-exception
        L_0x028b:
            r4 = r27
            goto L_0x02a9
        L_0x028e:
            r0 = move-exception
            r1 = r33
            r22 = r8
            r4 = r15
        L_0x0294:
            goto L_0x02a9
        L_0x0295:
            r0 = move-exception
            r22 = r31
            r1 = r33
            r4 = r15
            goto L_0x02a9
        L_0x029c:
            r0 = move-exception
            r1 = r33
            r22 = r6
            r4 = r15
            goto L_0x02a9
        L_0x02a3:
            r0 = move-exception
            r1 = r33
            r4 = r15
            r22 = r31
        L_0x02a9:
            monitor-exit(r16)     // Catch:{ all -> 0x02ab }
            throw r0
        L_0x02ab:
            r0 = move-exception
            goto L_0x02a9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.MediaFocusControl.requestAudioFocus(android.media.AudioAttributes, int, android.os.IBinder, android.media.IAudioFocusDispatcher, java.lang.String, java.lang.String, int, int, boolean):int");
    }

    /* Debug info: failed to restart local var, previous not found, register: 14 */
    /* access modifiers changed from: protected */
    public int abandonAudioFocus(IAudioFocusDispatcher fl, String clientId, AudioAttributes aa, String callingPackageName) {
        String str = clientId;
        AudioEventLogger audioEventLogger = mEventLogger;
        audioEventLogger.log(new AudioEventLogger.StringEvent("abandonAudioFocus() from uid/pid " + Binder.getCallingUid() + SliceClientPermissions.SliceAuthority.DELIMITER + Binder.getCallingPid() + " clientId=" + str).printLog(TAG));
        try {
            synchronized (mAudioFocusLock) {
                if (this.mFocusPolicy != null) {
                    if (notifyExtFocusPolicyFocusAbandon_syncAf(new AudioFocusInfo(aa, Binder.getCallingUid(), clientId, callingPackageName, 0, 0, 0, 0))) {
                        return 1;
                    }
                }
                boolean exitingRingOrCall = this.mRingOrCallActive & ("AudioFocus_For_Phone_Ring_And_Calls".compareTo(str) == 0);
                if (exitingRingOrCall) {
                    this.mRingOrCallActive = false;
                }
                removeFocusStackEntry(str, true, true);
                if (exitingRingOrCall && true) {
                    runAudioCheckerForRingOrCallAsync(false);
                }
            }
        } catch (ConcurrentModificationException cme) {
            Log.e(TAG, "FATAL EXCEPTION AudioFocus  abandonAudioFocus() caused " + cme);
            cme.printStackTrace();
        }
        return 1;
    }

    /* access modifiers changed from: protected */
    public void unregisterAudioFocusClient(String clientId) {
        synchronized (mAudioFocusLock) {
            removeFocusStackEntry(clientId, false, true);
        }
    }

    private void runAudioCheckerForRingOrCallAsync(final boolean enteringRingOrCall) {
        new Thread() {
            public void run() {
                if (enteringRingOrCall) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                synchronized (MediaFocusControl.mAudioFocusLock) {
                    if (MediaFocusControl.this.mRingOrCallActive) {
                        MediaFocusControl.this.mFocusEnforcer.mutePlayersForCall(MediaFocusControl.USAGES_TO_MUTE_IN_RING_OR_CALL);
                    } else {
                        MediaFocusControl.this.mFocusEnforcer.unmutePlayersForCall();
                    }
                }
            }
        }.start();
    }
}
