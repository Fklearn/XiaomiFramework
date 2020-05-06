package com.android.server.am;

import android.app.INotificationManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.util.proto.ProtoUtils;
import com.android.internal.app.procstats.ServiceState;
import com.android.internal.os.BatteryStatsImpl;
import com.android.server.LocalServices;
import com.android.server.notification.NotificationManagerInternal;
import com.android.server.pm.DumpState;
import com.android.server.uri.NeededUriGrants;
import com.android.server.uri.UriPermissionOwner;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class ServiceRecord extends Binder implements ComponentName.WithComponentName {
    static final int MAX_DELIVERY_COUNT = 3;
    static final int MAX_DONE_EXECUTING_COUNT = 6;
    private static final String TAG = "ActivityManager";
    final ActivityManagerService ams;
    ProcessRecord app;
    ApplicationInfo appInfo;
    final ArrayMap<Intent.FilterComparison, IntentBindRecord> bindings = new ArrayMap<>();
    boolean callStart;
    String callerPackage;
    private final ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = new ArrayMap<>();
    int crashCount;
    final long createRealTime;
    boolean createdFromFg;
    final String definingPackageName;
    final int definingUid;
    boolean delayed;
    boolean delayedStop;
    final ArrayList<StartItem> deliveredStarts = new ArrayList<>();
    long destroyTime;
    boolean destroying;
    boolean executeFg;
    int executeNesting;
    long executingStart;
    final boolean exported;
    boolean fgRequired;
    boolean fgWaiting;
    int foregroundId;
    Notification foregroundNoti;
    int foregroundServiceType;
    final ComponentName instanceName;
    final Intent.FilterComparison intent;
    boolean isForeground;
    ProcessRecord isolatedProc;
    long lastActivity;
    private int lastStartId;
    private ProcessRecord mAppForStartedWhitelistingBgActivityStarts;
    private boolean mHasBindingWhitelistingBgActivityStarts;
    private boolean mHasStartedWhitelistingBgActivityStarts;
    private Runnable mStartedWhitelistingBgActivityStartsCleanUp;
    final ComponentName name;
    long nextRestartTime;
    final String packageName;
    int pendingConnectionGroup;
    int pendingConnectionImportance;
    final ArrayList<StartItem> pendingStarts = new ArrayList<>();
    final String permission;
    final String processName;
    int restartCount;
    long restartDelay;
    long restartTime;
    ServiceState restartTracker;
    final Runnable restarter;
    final ServiceInfo serviceInfo;
    final String shortInstanceName;
    boolean startRequested;
    long startingBgTimeout;
    final BatteryStatsImpl.Uid.Pkg.Serv stats;
    boolean stopIfKilled;
    String stringName;
    int totalRestartCount;
    ServiceState tracker;
    final int userId;
    boolean whitelistManager;

    static class StartItem {
        final int callingId;
        long deliveredTime;
        int deliveryCount;
        int doneExecutingCount;
        final int id;
        final Intent intent;
        final NeededUriGrants neededGrants;
        final ServiceRecord sr;
        String stringName;
        final boolean taskRemoved;
        UriPermissionOwner uriPermissions;

        StartItem(ServiceRecord _sr, boolean _taskRemoved, int _id, Intent _intent, NeededUriGrants _neededGrants, int _callingId) {
            this.sr = _sr;
            this.taskRemoved = _taskRemoved;
            this.id = _id;
            this.intent = _intent;
            this.neededGrants = _neededGrants;
            this.callingId = _callingId;
        }

        /* access modifiers changed from: package-private */
        public UriPermissionOwner getUriPermissionsLocked() {
            if (this.uriPermissions == null) {
                this.uriPermissions = new UriPermissionOwner(this.sr.ams.mUgmInternal, this);
            }
            return this.uriPermissions;
        }

        /* access modifiers changed from: package-private */
        public void removeUriPermissionsLocked() {
            UriPermissionOwner uriPermissionOwner = this.uriPermissions;
            if (uriPermissionOwner != null) {
                uriPermissionOwner.removeUriPermissions();
                this.uriPermissions = null;
            }
        }

        public void writeToProto(ProtoOutputStream proto, long fieldId, long now) {
            ProtoOutputStream protoOutputStream = proto;
            long token = proto.start(fieldId);
            proto.write(1120986464257L, this.id);
            ProtoUtils.toDuration(proto, 1146756268034L, this.deliveredTime, now);
            proto.write(1120986464259L, this.deliveryCount);
            proto.write(1120986464260L, this.doneExecutingCount);
            Intent intent2 = this.intent;
            if (intent2 != null) {
                intent2.writeToProto(proto, 1146756268037L, true, true, true, false);
            }
            NeededUriGrants neededUriGrants = this.neededGrants;
            if (neededUriGrants != null) {
                neededUriGrants.writeToProto(proto, 1146756268038L);
            }
            UriPermissionOwner uriPermissionOwner = this.uriPermissions;
            if (uriPermissionOwner != null) {
                uriPermissionOwner.writeToProto(proto, 1146756268039L);
            }
            proto.end(token);
        }

        public String toString() {
            String str = this.stringName;
            if (str != null) {
                return str;
            }
            StringBuilder sb = new StringBuilder(128);
            sb.append("ServiceRecord{");
            sb.append(Integer.toHexString(System.identityHashCode(this.sr)));
            sb.append(' ');
            sb.append(this.sr.shortInstanceName);
            sb.append(" StartItem ");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" id=");
            sb.append(this.id);
            sb.append('}');
            String sb2 = sb.toString();
            this.stringName = sb2;
            return sb2;
        }
    }

    /* access modifiers changed from: package-private */
    public void dumpStartList(PrintWriter pw, String prefix, List<StartItem> list, long now) {
        int N = list.size();
        for (int i = 0; i < N; i++) {
            StartItem si = list.get(i);
            pw.print(prefix);
            pw.print("#");
            pw.print(i);
            pw.print(" id=");
            pw.print(si.id);
            if (now != 0) {
                pw.print(" dur=");
                TimeUtils.formatDuration(si.deliveredTime, now, pw);
            }
            if (si.deliveryCount != 0) {
                pw.print(" dc=");
                pw.print(si.deliveryCount);
            }
            if (si.doneExecutingCount != 0) {
                pw.print(" dxc=");
                pw.print(si.doneExecutingCount);
            }
            pw.println("");
            pw.print(prefix);
            pw.print("  intent=");
            if (si.intent != null) {
                pw.println(si.intent.toString());
            } else {
                pw.println("null");
            }
            if (si.neededGrants != null) {
                pw.print(prefix);
                pw.print("  neededGrants=");
                pw.println(si.neededGrants);
            }
            if (si.uriPermissions != null) {
                si.uriPermissions.dump(pw, prefix);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        ProtoOutputStream protoOutputStream = proto;
        long token = proto.start(fieldId);
        protoOutputStream.write(1138166333441L, this.shortInstanceName);
        protoOutputStream.write(1133871366146L, this.app != null);
        ProcessRecord processRecord = this.app;
        if (processRecord != null) {
            protoOutputStream.write(1120986464259L, processRecord.pid);
        }
        Intent.FilterComparison filterComparison = this.intent;
        if (filterComparison != null) {
            filterComparison.getIntent().writeToProto(proto, 1146756268036L, false, true, false, true);
        }
        protoOutputStream.write(1138166333445L, this.packageName);
        protoOutputStream.write(1138166333446L, this.processName);
        protoOutputStream.write(1138166333447L, this.permission);
        long now = SystemClock.uptimeMillis();
        long nowReal = SystemClock.elapsedRealtime();
        if (this.appInfo != null) {
            long appInfoToken = protoOutputStream.start(1146756268040L);
            protoOutputStream.write(1138166333441L, this.appInfo.sourceDir);
            if (!Objects.equals(this.appInfo.sourceDir, this.appInfo.publicSourceDir)) {
                protoOutputStream.write(1138166333442L, this.appInfo.publicSourceDir);
            }
            protoOutputStream.write(1138166333443L, this.appInfo.dataDir);
            protoOutputStream.end(appInfoToken);
        }
        ProcessRecord processRecord2 = this.app;
        if (processRecord2 != null) {
            processRecord2.writeToProto(protoOutputStream, 1146756268041L);
        }
        ProcessRecord processRecord3 = this.isolatedProc;
        if (processRecord3 != null) {
            processRecord3.writeToProto(protoOutputStream, 1146756268042L);
        }
        protoOutputStream.write(1133871366155L, this.whitelistManager);
        protoOutputStream.write(1133871366156L, this.delayed);
        if (this.isForeground || this.foregroundId != 0) {
            long fgToken = protoOutputStream.start(1146756268045L);
            protoOutputStream.write(1120986464257L, this.foregroundId);
            this.foregroundNoti.writeToProto(protoOutputStream, 1146756268034L);
            protoOutputStream.end(fgToken);
        }
        ProtoOutputStream protoOutputStream2 = proto;
        ProtoUtils.toDuration(protoOutputStream2, 1146756268046L, this.createRealTime, nowReal);
        long j = now;
        ProtoUtils.toDuration(protoOutputStream2, 1146756268047L, this.startingBgTimeout, j);
        ProtoUtils.toDuration(protoOutputStream2, 1146756268048L, this.lastActivity, j);
        ProtoUtils.toDuration(protoOutputStream2, 1146756268049L, this.restartTime, j);
        protoOutputStream.write(1133871366162L, this.createdFromFg);
        if (this.startRequested || this.delayedStop || this.lastStartId != 0) {
            long startToken = protoOutputStream.start(1146756268051L);
            protoOutputStream.write(1133871366145L, this.startRequested);
            protoOutputStream.write(1133871366146L, this.delayedStop);
            protoOutputStream.write(1133871366147L, this.stopIfKilled);
            protoOutputStream.write(1120986464261L, this.lastStartId);
            protoOutputStream.end(startToken);
        }
        if (this.executeNesting != 0) {
            long executNestingToken = protoOutputStream.start(1146756268052L);
            protoOutputStream.write(1120986464257L, this.executeNesting);
            protoOutputStream.write(1133871366146L, this.executeFg);
            ProtoUtils.toDuration(proto, 1146756268035L, this.executingStart, now);
            protoOutputStream.end(executNestingToken);
        }
        if (this.destroying || this.destroyTime != 0) {
            ProtoUtils.toDuration(proto, 1146756268053L, this.destroyTime, now);
        }
        if (!(this.crashCount == 0 && this.restartCount == 0 && this.restartDelay == 0 && this.nextRestartTime == 0)) {
            long crashToken = protoOutputStream.start(1146756268054L);
            protoOutputStream.write(1120986464257L, this.restartCount);
            ProtoOutputStream protoOutputStream3 = proto;
            long j2 = now;
            ProtoUtils.toDuration(protoOutputStream3, 1146756268034L, this.restartDelay, j2);
            ProtoUtils.toDuration(protoOutputStream3, 1146756268035L, this.nextRestartTime, j2);
            protoOutputStream.write(1120986464260L, this.crashCount);
            protoOutputStream.end(crashToken);
        }
        if (this.deliveredStarts.size() > 0) {
            int N = this.deliveredStarts.size();
            for (int i = 0; i < N; i++) {
                this.deliveredStarts.get(i).writeToProto(proto, 2246267895831L, now);
            }
        }
        if (this.pendingStarts.size() > 0) {
            int N2 = this.pendingStarts.size();
            for (int i2 = 0; i2 < N2; i2++) {
                this.pendingStarts.get(i2).writeToProto(proto, 2246267895832L, now);
            }
        }
        if (this.bindings.size() > 0) {
            int N3 = this.bindings.size();
            for (int i3 = 0; i3 < N3; i3++) {
                this.bindings.valueAt(i3).writeToProto(protoOutputStream, 2246267895833L);
            }
        }
        if (this.connections.size() > 0) {
            int N4 = this.connections.size();
            for (int conni = 0; conni < N4; conni++) {
                ArrayList<ConnectionRecord> c = this.connections.valueAt(conni);
                for (int i4 = 0; i4 < c.size(); i4++) {
                    c.get(i4).writeToProto(protoOutputStream, 2246267895834L);
                }
            }
        }
        protoOutputStream.end(token);
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.print("intent={");
        pw.print(this.intent.getIntent().toShortString(false, true, false, true));
        pw.println('}');
        pw.print(prefix);
        pw.print("packageName=");
        pw.println(this.packageName);
        pw.print(prefix);
        pw.print("processName=");
        pw.println(this.processName);
        if (this.permission != null) {
            pw.print(prefix);
            pw.print("permission=");
            pw.println(this.permission);
        }
        long now = SystemClock.uptimeMillis();
        long nowReal = SystemClock.elapsedRealtime();
        if (this.appInfo != null) {
            pw.print(prefix);
            pw.print("baseDir=");
            pw.println(this.appInfo.sourceDir);
            if (!Objects.equals(this.appInfo.sourceDir, this.appInfo.publicSourceDir)) {
                pw.print(prefix);
                pw.print("resDir=");
                pw.println(this.appInfo.publicSourceDir);
            }
            pw.print(prefix);
            pw.print("dataDir=");
            pw.println(this.appInfo.dataDir);
        }
        pw.print(prefix);
        pw.print("app=");
        pw.println(this.app);
        if (this.isolatedProc != null) {
            pw.print(prefix);
            pw.print("isolatedProc=");
            pw.println(this.isolatedProc);
        }
        if (this.whitelistManager) {
            pw.print(prefix);
            pw.print("whitelistManager=");
            pw.println(this.whitelistManager);
        }
        if (this.mHasBindingWhitelistingBgActivityStarts) {
            pw.print(prefix);
            pw.print("hasBindingWhitelistingBgActivityStarts=");
            pw.println(this.mHasBindingWhitelistingBgActivityStarts);
        }
        if (this.mHasStartedWhitelistingBgActivityStarts) {
            pw.print(prefix);
            pw.print("hasStartedWhitelistingBgActivityStarts=");
            pw.println(this.mHasStartedWhitelistingBgActivityStarts);
        }
        if (this.delayed) {
            pw.print(prefix);
            pw.print("delayed=");
            pw.println(this.delayed);
        }
        if (this.isForeground || this.foregroundId != 0) {
            pw.print(prefix);
            pw.print("isForeground=");
            pw.print(this.isForeground);
            pw.print(" foregroundId=");
            pw.print(this.foregroundId);
            pw.print(" foregroundNoti=");
            pw.println(this.foregroundNoti);
        }
        pw.print(prefix);
        pw.print("createTime=");
        TimeUtils.formatDuration(this.createRealTime, nowReal, pw);
        pw.print(" startingBgTimeout=");
        TimeUtils.formatDuration(this.startingBgTimeout, now, pw);
        pw.println();
        pw.print(prefix);
        pw.print("lastActivity=");
        TimeUtils.formatDuration(this.lastActivity, now, pw);
        pw.print(" restartTime=");
        TimeUtils.formatDuration(this.restartTime, now, pw);
        pw.print(" createdFromFg=");
        pw.println(this.createdFromFg);
        if (this.pendingConnectionGroup != 0) {
            pw.print(prefix);
            pw.print(" pendingConnectionGroup=");
            pw.print(this.pendingConnectionGroup);
            pw.print(" Importance=");
            pw.println(this.pendingConnectionImportance);
        }
        if (this.startRequested || this.delayedStop || this.lastStartId != 0) {
            pw.print(prefix);
            pw.print("startRequested=");
            pw.print(this.startRequested);
            pw.print(" delayedStop=");
            pw.print(this.delayedStop);
            pw.print(" stopIfKilled=");
            pw.print(this.stopIfKilled);
            pw.print(" callStart=");
            pw.print(this.callStart);
            pw.print(" lastStartId=");
            pw.println(this.lastStartId);
        }
        if (this.callerPackage != null) {
            pw.print(prefix);
            pw.print("callerPackage=");
            pw.println(this.callerPackage);
        }
        if (this.executeNesting != 0) {
            pw.print(prefix);
            pw.print("executeNesting=");
            pw.print(this.executeNesting);
            pw.print(" executeFg=");
            pw.print(this.executeFg);
            pw.print(" executingStart=");
            TimeUtils.formatDuration(this.executingStart, now, pw);
            pw.println();
        }
        if (this.destroying || this.destroyTime != 0) {
            pw.print(prefix);
            pw.print("destroying=");
            pw.print(this.destroying);
            pw.print(" destroyTime=");
            TimeUtils.formatDuration(this.destroyTime, now, pw);
            pw.println();
        }
        if (!(this.crashCount == 0 && this.restartCount == 0 && this.restartDelay == 0 && this.nextRestartTime == 0)) {
            pw.print(prefix);
            pw.print("restartCount=");
            pw.print(this.restartCount);
            pw.print(" restartDelay=");
            TimeUtils.formatDuration(this.restartDelay, now, pw);
            pw.print(" nextRestartTime=");
            TimeUtils.formatDuration(this.nextRestartTime, now, pw);
            pw.print(" crashCount=");
            pw.println(this.crashCount);
        }
        if (this.deliveredStarts.size() > 0) {
            pw.print(prefix);
            pw.println("Delivered Starts:");
            dumpStartList(pw, prefix, this.deliveredStarts, now);
        }
        if (this.pendingStarts.size() > 0) {
            pw.print(prefix);
            pw.println("Pending Starts:");
            dumpStartList(pw, prefix, this.pendingStarts, 0);
        }
        if (this.bindings.size() > 0) {
            pw.print(prefix);
            pw.println("Bindings:");
            for (int i = 0; i < this.bindings.size(); i++) {
                IntentBindRecord b = this.bindings.valueAt(i);
                pw.print(prefix);
                pw.print("* IntentBindRecord{");
                pw.print(Integer.toHexString(System.identityHashCode(b)));
                if ((b.collectFlags() & 1) != 0) {
                    pw.append(" CREATE");
                }
                pw.println("}:");
                b.dumpInService(pw, prefix + "  ");
            }
        }
        if (this.connections.size() > 0) {
            pw.print(prefix);
            pw.println("All Connections:");
            for (int conni = 0; conni < this.connections.size(); conni++) {
                ArrayList<ConnectionRecord> c = this.connections.valueAt(conni);
                for (int i2 = 0; i2 < c.size(); i2++) {
                    pw.print(prefix);
                    pw.print("  ");
                    pw.println(c.get(i2));
                }
            }
        }
    }

    ServiceRecord(ActivityManagerService ams2, BatteryStatsImpl.Uid.Pkg.Serv servStats, ComponentName name2, ComponentName instanceName2, String definingPackageName2, int definingUid2, Intent.FilterComparison intent2, ServiceInfo sInfo, boolean callerIsFg, Runnable restarter2) {
        this.ams = ams2;
        this.stats = servStats;
        this.name = name2;
        this.instanceName = instanceName2;
        this.shortInstanceName = instanceName2.flattenToShortString();
        this.definingPackageName = definingPackageName2;
        this.definingUid = definingUid2;
        this.intent = intent2;
        this.serviceInfo = sInfo;
        this.appInfo = sInfo.applicationInfo;
        this.packageName = sInfo.applicationInfo.packageName;
        if ((sInfo.flags & 2) != 0) {
            this.processName = sInfo.processName + ":" + instanceName2.getClassName();
        } else {
            this.processName = sInfo.processName;
        }
        this.permission = sInfo.permission;
        this.exported = sInfo.exported;
        this.restarter = restarter2;
        this.createRealTime = SystemClock.elapsedRealtime();
        this.lastActivity = SystemClock.uptimeMillis();
        this.userId = UserHandle.getUserId(this.appInfo.uid);
        this.createdFromFg = callerIsFg;
    }

    public ServiceState getTracker() {
        ServiceState serviceState = this.tracker;
        if (serviceState != null) {
            return serviceState;
        }
        if ((this.serviceInfo.applicationInfo.flags & 8) == 0) {
            this.tracker = this.ams.mProcessStats.getServiceStateLocked(this.serviceInfo.packageName, this.serviceInfo.applicationInfo.uid, this.serviceInfo.applicationInfo.longVersionCode, this.serviceInfo.processName, this.serviceInfo.name);
            this.tracker.applyNewOwner(this);
        }
        return this.tracker;
    }

    public void forceClearTracker() {
        ServiceState serviceState = this.tracker;
        if (serviceState != null) {
            serviceState.clearCurrentOwner(this, true);
            this.tracker = null;
        }
    }

    public void makeRestarting(int memFactor, long now) {
        if (this.restartTracker == null) {
            if ((this.serviceInfo.applicationInfo.flags & 8) == 0) {
                this.restartTracker = this.ams.mProcessStats.getServiceStateLocked(this.serviceInfo.packageName, this.serviceInfo.applicationInfo.uid, this.serviceInfo.applicationInfo.longVersionCode, this.serviceInfo.processName, this.serviceInfo.name);
            }
            if (this.restartTracker == null) {
                return;
            }
        }
        this.restartTracker.setRestarting(true, memFactor, now);
    }

    public void setProcess(ProcessRecord _proc) {
        if (_proc != null) {
            ProcessRecord processRecord = this.mAppForStartedWhitelistingBgActivityStarts;
            if (!(processRecord == null || processRecord == _proc)) {
                processRecord.removeAllowBackgroundActivityStartsToken(this);
                this.ams.mHandler.removeCallbacks(this.mStartedWhitelistingBgActivityStartsCleanUp);
            }
            this.mAppForStartedWhitelistingBgActivityStarts = this.mHasStartedWhitelistingBgActivityStarts ? _proc : null;
            if (this.mHasStartedWhitelistingBgActivityStarts || this.mHasBindingWhitelistingBgActivityStarts) {
                _proc.addAllowBackgroundActivityStartsToken(this);
            } else {
                _proc.removeAllowBackgroundActivityStartsToken(this);
            }
        }
        ProcessRecord processRecord2 = this.app;
        if (!(processRecord2 == null || processRecord2 == _proc)) {
            if (!this.mHasStartedWhitelistingBgActivityStarts) {
                processRecord2.removeAllowBackgroundActivityStartsToken(this);
            }
            this.app.updateBoundClientUids();
        }
        this.app = _proc;
        int i = this.pendingConnectionGroup;
        if (i > 0 && _proc != null) {
            _proc.connectionService = this;
            _proc.connectionGroup = i;
            _proc.connectionImportance = this.pendingConnectionImportance;
            this.pendingConnectionImportance = 0;
            this.pendingConnectionGroup = 0;
        }
        for (int conni = this.connections.size() - 1; conni >= 0; conni--) {
            ArrayList<ConnectionRecord> cr = this.connections.valueAt(conni);
            for (int i2 = 0; i2 < cr.size(); i2++) {
                ConnectionRecord conn = cr.get(i2);
                if (_proc != null) {
                    conn.startAssociationIfNeeded();
                } else {
                    conn.stopAssociation();
                }
            }
        }
        if (_proc != null) {
            _proc.updateBoundClientUids();
        }
    }

    /* access modifiers changed from: package-private */
    public ArrayMap<IBinder, ArrayList<ConnectionRecord>> getConnections() {
        return this.connections;
    }

    /* access modifiers changed from: package-private */
    public void addConnection(IBinder binder, ConnectionRecord c) {
        ArrayList<ConnectionRecord> clist = this.connections.get(binder);
        if (clist == null) {
            clist = new ArrayList<>();
            this.connections.put(binder, clist);
        }
        clist.add(c);
        ProcessRecord processRecord = this.app;
        if (processRecord != null) {
            processRecord.addBoundClientUid(c.clientUid);
        }
    }

    /* access modifiers changed from: package-private */
    public void removeConnection(IBinder binder) {
        this.connections.remove(binder);
        ProcessRecord processRecord = this.app;
        if (processRecord != null) {
            processRecord.updateBoundClientUids();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateHasBindingWhitelistingBgActivityStarts() {
        boolean hasWhitelistingBinding = false;
        for (int conni = this.connections.size() - 1; conni >= 0; conni--) {
            ArrayList<ConnectionRecord> cr = this.connections.valueAt(conni);
            int i = 0;
            while (true) {
                if (i >= cr.size()) {
                    break;
                } else if ((cr.get(i).flags & DumpState.DUMP_DEXOPT) != 0) {
                    hasWhitelistingBinding = true;
                    break;
                } else {
                    i++;
                }
            }
            if (hasWhitelistingBinding) {
                break;
            }
        }
        setHasBindingWhitelistingBgActivityStarts(hasWhitelistingBinding);
    }

    /* access modifiers changed from: package-private */
    public void setHasBindingWhitelistingBgActivityStarts(boolean newValue) {
        if (this.mHasBindingWhitelistingBgActivityStarts != newValue) {
            this.mHasBindingWhitelistingBgActivityStarts = newValue;
            updateParentProcessBgActivityStartsWhitelistingToken();
        }
    }

    /* access modifiers changed from: package-private */
    public void whitelistBgActivityStartsOnServiceStart() {
        setHasStartedWhitelistingBgActivityStarts(true);
        ProcessRecord processRecord = this.app;
        if (processRecord != null) {
            this.mAppForStartedWhitelistingBgActivityStarts = processRecord;
        }
        if (this.mStartedWhitelistingBgActivityStartsCleanUp == null) {
            this.mStartedWhitelistingBgActivityStartsCleanUp = new Runnable() {
                public final void run() {
                    ServiceRecord.this.lambda$whitelistBgActivityStartsOnServiceStart$0$ServiceRecord();
                }
            };
        }
        this.ams.mHandler.removeCallbacks(this.mStartedWhitelistingBgActivityStartsCleanUp);
        this.ams.mHandler.postDelayed(this.mStartedWhitelistingBgActivityStartsCleanUp, this.ams.mConstants.SERVICE_BG_ACTIVITY_START_TIMEOUT);
    }

    public /* synthetic */ void lambda$whitelistBgActivityStartsOnServiceStart$0$ServiceRecord() {
        synchronized (this.ams) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                if (this.app == this.mAppForStartedWhitelistingBgActivityStarts) {
                    setHasStartedWhitelistingBgActivityStarts(false);
                } else if (this.mAppForStartedWhitelistingBgActivityStarts != null) {
                    this.mAppForStartedWhitelistingBgActivityStarts.removeAllowBackgroundActivityStartsToken(this);
                }
                this.mAppForStartedWhitelistingBgActivityStarts = null;
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    private void setHasStartedWhitelistingBgActivityStarts(boolean newValue) {
        if (this.mHasStartedWhitelistingBgActivityStarts != newValue) {
            this.mHasStartedWhitelistingBgActivityStarts = newValue;
            updateParentProcessBgActivityStartsWhitelistingToken();
        }
    }

    private void updateParentProcessBgActivityStartsWhitelistingToken() {
        ProcessRecord processRecord = this.app;
        if (processRecord != null) {
            if (this.mHasStartedWhitelistingBgActivityStarts || this.mHasBindingWhitelistingBgActivityStarts) {
                this.app.addAllowBackgroundActivityStartsToken(this);
            } else {
                processRecord.removeAllowBackgroundActivityStartsToken(this);
            }
        }
    }

    public AppBindRecord retrieveAppBindingLocked(Intent intent2, ProcessRecord app2) {
        Intent.FilterComparison filter = new Intent.FilterComparison(intent2);
        IntentBindRecord i = this.bindings.get(filter);
        if (i == null) {
            i = new IntentBindRecord(this, filter);
            this.bindings.put(filter, i);
        }
        AppBindRecord a = i.apps.get(app2);
        if (a != null) {
            return a;
        }
        AppBindRecord a2 = new AppBindRecord(this, i, app2);
        i.apps.put(app2, a2);
        return a2;
    }

    public boolean hasAutoCreateConnections() {
        for (int conni = this.connections.size() - 1; conni >= 0; conni--) {
            ArrayList<ConnectionRecord> cr = this.connections.valueAt(conni);
            for (int i = 0; i < cr.size(); i++) {
                if ((cr.get(i).flags & 1) != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasOtherAppAutoCreateConnections() {
        for (int conni = this.connections.size() - 1; conni >= 0; conni--) {
            ArrayList<ConnectionRecord> cr = this.connections.valueAt(conni);
            for (int i = 0; i < cr.size(); i++) {
                ConnectionRecord connectionRecord = cr.get(i);
                if ((connectionRecord.flags & 1) != 0 && connectionRecord.binding.client.uid != this.appInfo.uid) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateWhitelistManager() {
        this.whitelistManager = false;
        for (int conni = this.connections.size() - 1; conni >= 0; conni--) {
            ArrayList<ConnectionRecord> cr = this.connections.valueAt(conni);
            for (int i = 0; i < cr.size(); i++) {
                if ((cr.get(i).flags & DumpState.DUMP_SERVICE_PERMISSIONS) != 0) {
                    this.whitelistManager = true;
                    return;
                }
            }
        }
    }

    public void resetRestartCounter() {
        this.restartCount = 0;
        this.restartDelay = 0;
        this.restartTime = 0;
    }

    public StartItem findDeliveredStart(int id, boolean taskRemoved, boolean remove) {
        int N = this.deliveredStarts.size();
        for (int i = 0; i < N; i++) {
            StartItem si = this.deliveredStarts.get(i);
            if (si.id == id && si.taskRemoved == taskRemoved) {
                if (remove) {
                    this.deliveredStarts.remove(i);
                }
                return si;
            }
        }
        return null;
    }

    public int getLastStartId() {
        return this.lastStartId;
    }

    public int makeNextStartId() {
        this.lastStartId++;
        if (this.lastStartId < 1) {
            this.lastStartId = 1;
        }
        return this.lastStartId;
    }

    public void postNotification() {
        int appUid = this.appInfo.uid;
        int appPid = this.app.pid;
        if (this.foregroundId != 0 && this.foregroundNoti != null) {
            String localPackageName = this.packageName;
            int localForegroundId = this.foregroundId;
            final Notification notification = this.foregroundNoti;
            final String str = localPackageName;
            final int i = appUid;
            final int i2 = appPid;
            final int i3 = localForegroundId;
            this.ams.mHandler.post(new Runnable() {
                /* Debug info: failed to restart local var, previous not found, register: 15 */
                /* JADX WARNING: Removed duplicated region for block: B:28:0x0130  */
                /* JADX WARNING: Removed duplicated region for block: B:40:0x0176 A[Catch:{ RuntimeException -> 0x01ab }] */
                /* JADX WARNING: Removed duplicated region for block: B:41:0x018f A[Catch:{ RuntimeException -> 0x01ab }] */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r15 = this;
                        java.lang.String r0 = "ActivityManager"
                        java.lang.Class<com.android.server.notification.NotificationManagerInternal> r1 = com.android.server.notification.NotificationManagerInternal.class
                        java.lang.Object r1 = com.android.server.LocalServices.getService(r1)
                        com.android.server.notification.NotificationManagerInternal r1 = (com.android.server.notification.NotificationManagerInternal) r1
                        if (r1 != 0) goto L_0x000d
                        return
                    L_0x000d:
                        android.app.Notification r2 = r3
                        android.graphics.drawable.Icon r3 = r2.getSmallIcon()     // Catch:{ RuntimeException -> 0x01ad }
                        r4 = 0
                        if (r3 != 0) goto L_0x0121
                        java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x01ad }
                        r3.<init>()     // Catch:{ RuntimeException -> 0x01ad }
                        java.lang.String r5 = "Attempted to start a foreground service ("
                        r3.append(r5)     // Catch:{ RuntimeException -> 0x01ad }
                        com.android.server.am.ServiceRecord r5 = com.android.server.am.ServiceRecord.this     // Catch:{ RuntimeException -> 0x01ad }
                        java.lang.String r5 = r5.shortInstanceName     // Catch:{ RuntimeException -> 0x01ad }
                        r3.append(r5)     // Catch:{ RuntimeException -> 0x01ad }
                        java.lang.String r5 = ") with a broken notification (no icon: "
                        r3.append(r5)     // Catch:{ RuntimeException -> 0x01ad }
                        r3.append(r2)     // Catch:{ RuntimeException -> 0x01ad }
                        java.lang.String r5 = ")"
                        r3.append(r5)     // Catch:{ RuntimeException -> 0x01ad }
                        java.lang.String r3 = r3.toString()     // Catch:{ RuntimeException -> 0x01ad }
                        android.util.Slog.v(r0, r3)     // Catch:{ RuntimeException -> 0x01ad }
                        com.android.server.am.ServiceRecord r3 = com.android.server.am.ServiceRecord.this     // Catch:{ RuntimeException -> 0x01ad }
                        android.content.pm.ApplicationInfo r3 = r3.appInfo     // Catch:{ RuntimeException -> 0x01ad }
                        com.android.server.am.ServiceRecord r5 = com.android.server.am.ServiceRecord.this     // Catch:{ RuntimeException -> 0x01ad }
                        com.android.server.am.ActivityManagerService r5 = r5.ams     // Catch:{ RuntimeException -> 0x01ad }
                        android.content.Context r5 = r5.mContext     // Catch:{ RuntimeException -> 0x01ad }
                        android.content.pm.PackageManager r5 = r5.getPackageManager()     // Catch:{ RuntimeException -> 0x01ad }
                        java.lang.CharSequence r3 = r3.loadLabel(r5)     // Catch:{ RuntimeException -> 0x01ad }
                        if (r3 != 0) goto L_0x0056
                        com.android.server.am.ServiceRecord r5 = com.android.server.am.ServiceRecord.this     // Catch:{ RuntimeException -> 0x01ad }
                        android.content.pm.ApplicationInfo r5 = r5.appInfo     // Catch:{ RuntimeException -> 0x01ad }
                        java.lang.String r5 = r5.packageName     // Catch:{ RuntimeException -> 0x01ad }
                        r3 = r5
                    L_0x0056:
                        r5 = 0
                        com.android.server.am.ServiceRecord r6 = com.android.server.am.ServiceRecord.this     // Catch:{ NameNotFoundException -> 0x0120 }
                        com.android.server.am.ActivityManagerService r6 = r6.ams     // Catch:{ NameNotFoundException -> 0x0120 }
                        android.content.Context r6 = r6.mContext     // Catch:{ NameNotFoundException -> 0x0120 }
                        com.android.server.am.ServiceRecord r7 = com.android.server.am.ServiceRecord.this     // Catch:{ NameNotFoundException -> 0x0120 }
                        android.content.pm.ApplicationInfo r7 = r7.appInfo     // Catch:{ NameNotFoundException -> 0x0120 }
                        java.lang.String r7 = r7.packageName     // Catch:{ NameNotFoundException -> 0x0120 }
                        android.os.UserHandle r8 = new android.os.UserHandle     // Catch:{ NameNotFoundException -> 0x0120 }
                        com.android.server.am.ServiceRecord r9 = com.android.server.am.ServiceRecord.this     // Catch:{ NameNotFoundException -> 0x0120 }
                        int r9 = r9.userId     // Catch:{ NameNotFoundException -> 0x0120 }
                        r8.<init>(r9)     // Catch:{ NameNotFoundException -> 0x0120 }
                        android.content.Context r6 = r6.createPackageContextAsUser(r7, r4, r8)     // Catch:{ NameNotFoundException -> 0x0120 }
                        r5 = r6
                        android.app.Notification$Builder r6 = new android.app.Notification$Builder     // Catch:{ NameNotFoundException -> 0x0120 }
                        java.lang.String r7 = r2.getChannelId()     // Catch:{ NameNotFoundException -> 0x0120 }
                        r6.<init>(r5, r7)     // Catch:{ NameNotFoundException -> 0x0120 }
                        com.android.server.am.ServiceRecord r7 = com.android.server.am.ServiceRecord.this     // Catch:{ NameNotFoundException -> 0x0120 }
                        android.content.pm.ApplicationInfo r7 = r7.appInfo     // Catch:{ NameNotFoundException -> 0x0120 }
                        int r7 = r7.icon     // Catch:{ NameNotFoundException -> 0x0120 }
                        r6.setSmallIcon(r7)     // Catch:{ NameNotFoundException -> 0x0120 }
                        r7 = 64
                        r8 = 1
                        r6.setFlag(r7, r8)     // Catch:{ NameNotFoundException -> 0x0120 }
                        boolean r7 = miui.os.Build.IS_INTERNATIONAL_BUILD     // Catch:{ NameNotFoundException -> 0x0120 }
                        if (r7 == 0) goto L_0x0095
                        android.content.Intent r7 = new android.content.Intent     // Catch:{ NameNotFoundException -> 0x0120 }
                        java.lang.String r9 = "android.settings.APPLICATION_DETAILS_SETTINGS"
                        r7.<init>(r9)     // Catch:{ NameNotFoundException -> 0x0120 }
                        goto L_0x009d
                    L_0x0095:
                        android.content.Intent r7 = new android.content.Intent     // Catch:{ NameNotFoundException -> 0x0120 }
                        java.lang.String r9 = "miui.intent.action.MANAGER_BACKGROUND"
                        r7.<init>(r9)     // Catch:{ NameNotFoundException -> 0x0120 }
                    L_0x009d:
                        java.lang.String r9 = "package"
                        com.android.server.am.ServiceRecord r10 = com.android.server.am.ServiceRecord.this     // Catch:{ NameNotFoundException -> 0x0120 }
                        android.content.pm.ApplicationInfo r10 = r10.appInfo     // Catch:{ NameNotFoundException -> 0x0120 }
                        java.lang.String r10 = r10.packageName     // Catch:{ NameNotFoundException -> 0x0120 }
                        r11 = 0
                        android.net.Uri r9 = android.net.Uri.fromParts(r9, r10, r11)     // Catch:{ NameNotFoundException -> 0x0120 }
                        r7.setData(r9)     // Catch:{ NameNotFoundException -> 0x0120 }
                        com.android.server.am.ServiceRecord r9 = com.android.server.am.ServiceRecord.this     // Catch:{ NameNotFoundException -> 0x0120 }
                        com.android.server.am.ActivityManagerService r9 = r9.ams     // Catch:{ NameNotFoundException -> 0x0120 }
                        android.content.Context r9 = r9.mContext     // Catch:{ NameNotFoundException -> 0x0120 }
                        r10 = 0
                        r12 = 134217728(0x8000000, float:3.85186E-34)
                        r13 = 0
                        com.android.server.am.ServiceRecord r11 = com.android.server.am.ServiceRecord.this     // Catch:{ NameNotFoundException -> 0x0120 }
                        int r11 = r11.userId     // Catch:{ NameNotFoundException -> 0x0120 }
                        android.os.UserHandle r14 = android.os.UserHandle.of(r11)     // Catch:{ NameNotFoundException -> 0x0120 }
                        r11 = r7
                        android.app.PendingIntent r9 = android.app.PendingIntent.getActivityAsUser(r9, r10, r11, r12, r13, r14)     // Catch:{ NameNotFoundException -> 0x0120 }
                        com.android.server.am.ServiceRecord r10 = com.android.server.am.ServiceRecord.this     // Catch:{ NameNotFoundException -> 0x0120 }
                        com.android.server.am.ActivityManagerService r10 = r10.ams     // Catch:{ NameNotFoundException -> 0x0120 }
                        android.content.Context r10 = r10.mContext     // Catch:{ NameNotFoundException -> 0x0120 }
                        r11 = 17170460(0x106001c, float:2.4611991E-38)
                        int r10 = r10.getColor(r11)     // Catch:{ NameNotFoundException -> 0x0120 }
                        r6.setColor(r10)     // Catch:{ NameNotFoundException -> 0x0120 }
                        com.android.server.am.ServiceRecord r10 = com.android.server.am.ServiceRecord.this     // Catch:{ NameNotFoundException -> 0x0120 }
                        com.android.server.am.ActivityManagerService r10 = r10.ams     // Catch:{ NameNotFoundException -> 0x0120 }
                        android.content.Context r10 = r10.mContext     // Catch:{ NameNotFoundException -> 0x0120 }
                        r11 = 17039529(0x10400a9, float:2.4245045E-38)
                        java.lang.Object[] r12 = new java.lang.Object[r8]     // Catch:{ NameNotFoundException -> 0x0120 }
                        r12[r4] = r3     // Catch:{ NameNotFoundException -> 0x0120 }
                        java.lang.String r10 = r10.getString(r11, r12)     // Catch:{ NameNotFoundException -> 0x0120 }
                        r6.setContentTitle(r10)     // Catch:{ NameNotFoundException -> 0x0120 }
                        boolean r10 = miui.os.Build.IS_INTERNATIONAL_BUILD     // Catch:{ NameNotFoundException -> 0x0120 }
                        if (r10 == 0) goto L_0x0102
                        com.android.server.am.ServiceRecord r10 = com.android.server.am.ServiceRecord.this     // Catch:{ NameNotFoundException -> 0x0120 }
                        com.android.server.am.ActivityManagerService r10 = r10.ams     // Catch:{ NameNotFoundException -> 0x0120 }
                        android.content.Context r10 = r10.mContext     // Catch:{ NameNotFoundException -> 0x0120 }
                        r11 = 17039527(0x10400a7, float:2.424504E-38)
                        java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ NameNotFoundException -> 0x0120 }
                        r8[r4] = r3     // Catch:{ NameNotFoundException -> 0x0120 }
                        java.lang.String r8 = r10.getString(r11, r8)     // Catch:{ NameNotFoundException -> 0x0120 }
                        r6.setContentText(r8)     // Catch:{ NameNotFoundException -> 0x0120 }
                        goto L_0x0116
                    L_0x0102:
                        com.android.server.am.ServiceRecord r10 = com.android.server.am.ServiceRecord.this     // Catch:{ NameNotFoundException -> 0x0120 }
                        com.android.server.am.ActivityManagerService r10 = r10.ams     // Catch:{ NameNotFoundException -> 0x0120 }
                        android.content.Context r10 = r10.mContext     // Catch:{ NameNotFoundException -> 0x0120 }
                        r11 = 17039528(0x10400a8, float:2.4245042E-38)
                        java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ NameNotFoundException -> 0x0120 }
                        r8[r4] = r3     // Catch:{ NameNotFoundException -> 0x0120 }
                        java.lang.String r8 = r10.getString(r11, r8)     // Catch:{ NameNotFoundException -> 0x0120 }
                        r6.setContentText(r8)     // Catch:{ NameNotFoundException -> 0x0120 }
                    L_0x0116:
                        r6.setContentIntent(r9)     // Catch:{ NameNotFoundException -> 0x0120 }
                        android.app.Notification r8 = r6.build()     // Catch:{ NameNotFoundException -> 0x0120 }
                        r2 = r8
                        r11 = r2
                        goto L_0x0122
                    L_0x0120:
                        r6 = move-exception
                    L_0x0121:
                        r11 = r2
                    L_0x0122:
                        java.lang.String r2 = r4     // Catch:{ RuntimeException -> 0x01ab }
                        int r3 = r5     // Catch:{ RuntimeException -> 0x01ab }
                        java.lang.String r5 = r11.getChannelId()     // Catch:{ RuntimeException -> 0x01ab }
                        android.app.NotificationChannel r2 = r1.getNotificationChannel(r2, r3, r5)     // Catch:{ RuntimeException -> 0x01ab }
                        if (r2 != 0) goto L_0x0170
                        r2 = 27
                        com.android.server.am.ServiceRecord r3 = com.android.server.am.ServiceRecord.this     // Catch:{ NameNotFoundException -> 0x014e }
                        com.android.server.am.ActivityManagerService r3 = r3.ams     // Catch:{ NameNotFoundException -> 0x014e }
                        android.content.Context r3 = r3.mContext     // Catch:{ NameNotFoundException -> 0x014e }
                        android.content.pm.PackageManager r3 = r3.getPackageManager()     // Catch:{ NameNotFoundException -> 0x014e }
                        com.android.server.am.ServiceRecord r5 = com.android.server.am.ServiceRecord.this     // Catch:{ NameNotFoundException -> 0x014e }
                        android.content.pm.ApplicationInfo r5 = r5.appInfo     // Catch:{ NameNotFoundException -> 0x014e }
                        java.lang.String r5 = r5.packageName     // Catch:{ NameNotFoundException -> 0x014e }
                        com.android.server.am.ServiceRecord r6 = com.android.server.am.ServiceRecord.this     // Catch:{ NameNotFoundException -> 0x014e }
                        int r6 = r6.userId     // Catch:{ NameNotFoundException -> 0x014e }
                        android.content.pm.ApplicationInfo r3 = r3.getApplicationInfoAsUser(r5, r4, r6)     // Catch:{ NameNotFoundException -> 0x014e }
                        int r4 = r3.targetSdkVersion     // Catch:{ NameNotFoundException -> 0x014e }
                        r2 = r4
                        goto L_0x014f
                    L_0x014e:
                        r3 = move-exception
                    L_0x014f:
                        r3 = 27
                        if (r2 >= r3) goto L_0x0154
                        goto L_0x0170
                    L_0x0154:
                        java.lang.RuntimeException r3 = new java.lang.RuntimeException     // Catch:{ RuntimeException -> 0x01ab }
                        java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x01ab }
                        r4.<init>()     // Catch:{ RuntimeException -> 0x01ab }
                        java.lang.String r5 = "invalid channel for service notification: "
                        r4.append(r5)     // Catch:{ RuntimeException -> 0x01ab }
                        com.android.server.am.ServiceRecord r5 = com.android.server.am.ServiceRecord.this     // Catch:{ RuntimeException -> 0x01ab }
                        android.app.Notification r5 = r5.foregroundNoti     // Catch:{ RuntimeException -> 0x01ab }
                        r4.append(r5)     // Catch:{ RuntimeException -> 0x01ab }
                        java.lang.String r4 = r4.toString()     // Catch:{ RuntimeException -> 0x01ab }
                        r3.<init>(r4)     // Catch:{ RuntimeException -> 0x01ab }
                        throw r3     // Catch:{ RuntimeException -> 0x01ab }
                    L_0x0170:
                        android.graphics.drawable.Icon r2 = r11.getSmallIcon()     // Catch:{ RuntimeException -> 0x01ab }
                        if (r2 == 0) goto L_0x018f
                        java.lang.String r3 = r4     // Catch:{ RuntimeException -> 0x01ab }
                        java.lang.String r4 = r4     // Catch:{ RuntimeException -> 0x01ab }
                        int r5 = r5     // Catch:{ RuntimeException -> 0x01ab }
                        int r6 = r6     // Catch:{ RuntimeException -> 0x01ab }
                        r7 = 0
                        int r8 = r7     // Catch:{ RuntimeException -> 0x01ab }
                        com.android.server.am.ServiceRecord r2 = com.android.server.am.ServiceRecord.this     // Catch:{ RuntimeException -> 0x01ab }
                        int r10 = r2.userId     // Catch:{ RuntimeException -> 0x01ab }
                        r2 = r1
                        r9 = r11
                        r2.enqueueNotification(r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ RuntimeException -> 0x01ab }
                        com.android.server.am.ServiceRecord r2 = com.android.server.am.ServiceRecord.this     // Catch:{ RuntimeException -> 0x01ab }
                        r2.foregroundNoti = r11     // Catch:{ RuntimeException -> 0x01ab }
                        goto L_0x01e5
                    L_0x018f:
                        java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ RuntimeException -> 0x01ab }
                        java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x01ab }
                        r3.<init>()     // Catch:{ RuntimeException -> 0x01ab }
                        java.lang.String r4 = "invalid service notification: "
                        r3.append(r4)     // Catch:{ RuntimeException -> 0x01ab }
                        com.android.server.am.ServiceRecord r4 = com.android.server.am.ServiceRecord.this     // Catch:{ RuntimeException -> 0x01ab }
                        android.app.Notification r4 = r4.foregroundNoti     // Catch:{ RuntimeException -> 0x01ab }
                        r3.append(r4)     // Catch:{ RuntimeException -> 0x01ab }
                        java.lang.String r3 = r3.toString()     // Catch:{ RuntimeException -> 0x01ab }
                        r2.<init>(r3)     // Catch:{ RuntimeException -> 0x01ab }
                        throw r2     // Catch:{ RuntimeException -> 0x01ab }
                    L_0x01ab:
                        r3 = move-exception
                        goto L_0x01af
                    L_0x01ad:
                        r3 = move-exception
                        r11 = r2
                    L_0x01af:
                        r2 = r3
                        java.lang.String r3 = "Error showing notification for service"
                        android.util.Slog.w(r0, r3, r2)
                        com.android.server.am.ServiceRecord r0 = com.android.server.am.ServiceRecord.this
                        com.android.server.am.ActivityManagerService r3 = r0.ams
                        com.android.server.am.ServiceRecord r0 = com.android.server.am.ServiceRecord.this
                        android.content.ComponentName r4 = r0.instanceName
                        com.android.server.am.ServiceRecord r5 = com.android.server.am.ServiceRecord.this
                        r6 = 0
                        r7 = 0
                        r8 = 0
                        r9 = 0
                        r3.setServiceForeground(r4, r5, r6, r7, r8, r9)
                        com.android.server.am.ServiceRecord r0 = com.android.server.am.ServiceRecord.this
                        com.android.server.am.ActivityManagerService r3 = r0.ams
                        int r4 = r5
                        int r5 = r6
                        java.lang.String r6 = r4
                        r7 = -1
                        java.lang.StringBuilder r0 = new java.lang.StringBuilder
                        r0.<init>()
                        java.lang.String r8 = "Bad notification for startForeground: "
                        r0.append(r8)
                        r0.append(r2)
                        java.lang.String r8 = r0.toString()
                        r3.crashApplication(r4, r5, r6, r7, r8)
                    L_0x01e5:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ServiceRecord.AnonymousClass1.run():void");
                }
            });
        }
    }

    public void cancelNotification() {
        final String localPackageName = this.packageName;
        final int localForegroundId = this.foregroundId;
        this.ams.mHandler.post(new Runnable() {
            public void run() {
                INotificationManager inm = NotificationManager.getService();
                if (inm != null) {
                    try {
                        inm.cancelNotificationWithTag(localPackageName, (String) null, localForegroundId, ServiceRecord.this.userId);
                    } catch (RuntimeException e) {
                        Slog.w(ServiceRecord.TAG, "Error canceling notification for service", e);
                    } catch (RemoteException e2) {
                    }
                }
            }
        });
    }

    public void stripForegroundServiceFlagFromNotification() {
        if (this.foregroundId != 0) {
            final int localForegroundId = this.foregroundId;
            final int localUserId = this.userId;
            final String localPackageName = this.packageName;
            this.ams.mHandler.post(new Runnable() {
                public void run() {
                    NotificationManagerInternal nmi = (NotificationManagerInternal) LocalServices.getService(NotificationManagerInternal.class);
                    if (nmi != null) {
                        nmi.removeForegroundServiceFlagFromNotification(localPackageName, localForegroundId, localUserId);
                    }
                }
            });
        }
    }

    public void clearDeliveredStartsLocked() {
        for (int i = this.deliveredStarts.size() - 1; i >= 0; i--) {
            this.deliveredStarts.get(i).removeUriPermissionsLocked();
        }
        this.deliveredStarts.clear();
    }

    public String toString() {
        String str = this.stringName;
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("ServiceRecord{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" u");
        sb.append(this.userId);
        sb.append(' ');
        sb.append(this.shortInstanceName);
        sb.append('}');
        String sb2 = sb.toString();
        this.stringName = sb2;
        return sb2;
    }

    public ComponentName getComponentName() {
        return this.name;
    }
}
