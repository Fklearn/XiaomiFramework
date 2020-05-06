package com.android.server.am;

import android.app.BroadcastOptions;
import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.PrintWriterPrinter;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

final class BroadcastRecord extends Binder {
    static final int APP_RECEIVE = 1;
    static final int CALL_DONE_RECEIVE = 3;
    static final int CALL_IN_RECEIVE = 2;
    static final int DELIVERY_DELIVERED = 1;
    static final int DELIVERY_PENDING = 0;
    static final int DELIVERY_SKIPPED = 2;
    static final int DELIVERY_TIMEOUT = 3;
    static final int IDLE = 0;
    static final int WAITING_SERVICES = 4;
    static AtomicInteger sNextToken = new AtomicInteger(1);
    final boolean allowBackgroundActivityStarts;
    int anrCount;
    final int appOp;
    final ProcessRecord callerApp;
    final boolean callerInstantApp;
    final String callerPackage;
    final int callingPid;
    final int callingUid;
    ProcessRecord curApp;
    ComponentName curComponent;
    BroadcastFilter curFilter;
    ActivityInfo curReceiver;
    boolean deferred;
    final int[] delivery;
    long dispatchClockTime;
    long dispatchTime;
    final long[] duration;
    long enqueueClockTime;
    long finishTime;
    final boolean initialSticky;
    final Intent intent;
    int manifestCount;
    int manifestSkipCount;
    int nextReceiver;
    final BroadcastOptions options;
    final boolean ordered;
    BroadcastQueue queue;
    IBinder receiver;
    long receiverTime;
    final List receivers;
    final String[] requiredPermissions;
    final String resolvedType;
    boolean resultAbort;
    int resultCode;
    String resultData;
    Bundle resultExtras;
    IIntentReceiver resultTo;
    int splitCount;
    int splitToken;
    int state;
    final boolean sticky;
    final ComponentName targetComp;
    boolean timeoutExempt;
    final int userId;

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix, SimpleDateFormat sdf) {
        PrintWriter printWriter = pw;
        SimpleDateFormat simpleDateFormat = sdf;
        long now = SystemClock.uptimeMillis();
        pw.print(prefix);
        printWriter.print(this);
        printWriter.print(" to user ");
        printWriter.println(this.userId);
        pw.print(prefix);
        printWriter.println(this.intent.toInsecureString());
        ComponentName componentName = this.targetComp;
        if (!(componentName == null || componentName == this.intent.getComponent())) {
            pw.print(prefix);
            printWriter.print("  targetComp: ");
            printWriter.println(this.targetComp.toShortString());
        }
        Bundle bundle = this.intent.getExtras();
        if (bundle != null) {
            pw.print(prefix);
            printWriter.print("  extras: ");
            printWriter.println(bundle.toString());
        }
        pw.print(prefix);
        printWriter.print("caller=");
        printWriter.print(this.callerPackage);
        printWriter.print(" ");
        ProcessRecord processRecord = this.callerApp;
        printWriter.print(processRecord != null ? processRecord.toShortString() : "null");
        printWriter.print(" pid=");
        printWriter.print(this.callingPid);
        printWriter.print(" uid=");
        printWriter.println(this.callingUid);
        String[] strArr = this.requiredPermissions;
        if ((strArr != null && strArr.length > 0) || this.appOp != -1) {
            pw.print(prefix);
            printWriter.print("requiredPermissions=");
            printWriter.print(Arrays.toString(this.requiredPermissions));
            printWriter.print("  appOp=");
            printWriter.println(this.appOp);
        }
        if (this.options != null) {
            pw.print(prefix);
            printWriter.print("options=");
            printWriter.println(this.options.toBundle());
        }
        pw.print(prefix);
        printWriter.print("enqueueClockTime=");
        printWriter.print(simpleDateFormat.format(new Date(this.enqueueClockTime)));
        printWriter.print(" dispatchClockTime=");
        printWriter.println(simpleDateFormat.format(new Date(this.dispatchClockTime)));
        pw.print(prefix);
        printWriter.print("dispatchTime=");
        TimeUtils.formatDuration(this.dispatchTime, now, printWriter);
        printWriter.print(" (");
        TimeUtils.formatDuration(this.dispatchClockTime - this.enqueueClockTime, printWriter);
        printWriter.print(" since enq)");
        if (this.finishTime != 0) {
            printWriter.print(" finishTime=");
            TimeUtils.formatDuration(this.finishTime, now, printWriter);
            printWriter.print(" (");
            TimeUtils.formatDuration(this.finishTime - this.dispatchTime, printWriter);
            printWriter.print(" since disp)");
        } else {
            printWriter.print(" receiverTime=");
            TimeUtils.formatDuration(this.receiverTime, now, printWriter);
        }
        printWriter.println("");
        if (this.anrCount != 0) {
            pw.print(prefix);
            printWriter.print("anrCount=");
            printWriter.println(this.anrCount);
        }
        if (!(this.resultTo == null && this.resultCode == -1 && this.resultData == null)) {
            pw.print(prefix);
            printWriter.print("resultTo=");
            printWriter.print(this.resultTo);
            printWriter.print(" resultCode=");
            printWriter.print(this.resultCode);
            printWriter.print(" resultData=");
            printWriter.println(this.resultData);
        }
        if (this.resultExtras != null) {
            pw.print(prefix);
            printWriter.print("resultExtras=");
            printWriter.println(this.resultExtras);
        }
        if (this.resultAbort || this.ordered || this.sticky || this.initialSticky) {
            pw.print(prefix);
            printWriter.print("resultAbort=");
            printWriter.print(this.resultAbort);
            printWriter.print(" ordered=");
            printWriter.print(this.ordered);
            printWriter.print(" sticky=");
            printWriter.print(this.sticky);
            printWriter.print(" initialSticky=");
            printWriter.println(this.initialSticky);
        }
        if (!(this.nextReceiver == 0 && this.receiver == null)) {
            pw.print(prefix);
            printWriter.print("nextReceiver=");
            printWriter.print(this.nextReceiver);
            printWriter.print(" receiver=");
            printWriter.println(this.receiver);
        }
        if (this.curFilter != null) {
            pw.print(prefix);
            printWriter.print("curFilter=");
            printWriter.println(this.curFilter);
        }
        if (this.curReceiver != null) {
            pw.print(prefix);
            printWriter.print("curReceiver=");
            printWriter.println(this.curReceiver);
        }
        if (this.curApp != null) {
            pw.print(prefix);
            printWriter.print("curApp=");
            printWriter.println(this.curApp);
            pw.print(prefix);
            printWriter.print("curComponent=");
            ComponentName componentName2 = this.curComponent;
            printWriter.println(componentName2 != null ? componentName2.toShortString() : "--");
            ActivityInfo activityInfo = this.curReceiver;
            if (!(activityInfo == null || activityInfo.applicationInfo == null)) {
                pw.print(prefix);
                printWriter.print("curSourceDir=");
                printWriter.println(this.curReceiver.applicationInfo.sourceDir);
            }
        }
        int i = this.state;
        int i2 = 2;
        int i3 = 1;
        if (i != 0) {
            String stateStr = " (?)";
            if (i == 1) {
                stateStr = " (APP_RECEIVE)";
            } else if (i == 2) {
                stateStr = " (CALL_IN_RECEIVE)";
            } else if (i == 3) {
                stateStr = " (CALL_DONE_RECEIVE)";
            } else if (i == 4) {
                stateStr = " (WAITING_SERVICES)";
            }
            pw.print(prefix);
            printWriter.print("state=");
            printWriter.print(this.state);
            printWriter.println(stateStr);
        }
        List list = this.receivers;
        int N = list != null ? list.size() : 0;
        String p2 = prefix + "  ";
        PrintWriterPrinter printer = new PrintWriterPrinter(printWriter);
        int i4 = 0;
        while (i4 < N) {
            Object o = this.receivers.get(i4);
            pw.print(prefix);
            int i5 = this.delivery[i4];
            if (i5 == 0) {
                printWriter.print("Pending");
            } else if (i5 == i3) {
                printWriter.print("Deliver");
            } else if (i5 == i2) {
                printWriter.print("Skipped");
            } else if (i5 != 3) {
                printWriter.print("???????");
            } else {
                printWriter.print("Timeout");
            }
            printWriter.print(" ");
            TimeUtils.formatDuration(this.duration[i4], printWriter);
            printWriter.print(" #");
            printWriter.print(i4);
            printWriter.print(": ");
            if (o instanceof BroadcastFilter) {
                printWriter.println(o);
                ((BroadcastFilter) o).dumpBrief(printWriter, p2);
            } else if (o instanceof ResolveInfo) {
                printWriter.println("(manifest)");
                ((ResolveInfo) o).dump(printer, p2, 0);
            } else {
                printWriter.println(o);
            }
            i4++;
            i2 = 2;
            i3 = 1;
        }
    }

    BroadcastRecord(BroadcastQueue _queue, Intent _intent, ProcessRecord _callerApp, String _callerPackage, int _callingPid, int _callingUid, boolean _callerInstantApp, String _resolvedType, String[] _requiredPermissions, int _appOp, BroadcastOptions _options, List _receivers, IIntentReceiver _resultTo, int _resultCode, String _resultData, Bundle _resultExtras, boolean _serialized, boolean _sticky, boolean _initialSticky, int _userId, boolean _allowBackgroundActivityStarts, boolean _timeoutExempt) {
        Intent intent2 = _intent;
        List list = _receivers;
        if (intent2 != null) {
            this.queue = _queue;
            this.intent = intent2;
            this.targetComp = _intent.getComponent();
            this.callerApp = _callerApp;
            this.callerPackage = _callerPackage;
            this.callingPid = _callingPid;
            this.callingUid = _callingUid;
            this.callerInstantApp = _callerInstantApp;
            this.resolvedType = _resolvedType;
            this.requiredPermissions = _requiredPermissions;
            this.appOp = _appOp;
            this.options = _options;
            this.receivers = list;
            this.delivery = new int[(list != null ? _receivers.size() : 0)];
            this.duration = new long[this.delivery.length];
            this.resultTo = _resultTo;
            this.resultCode = _resultCode;
            this.resultData = _resultData;
            this.resultExtras = _resultExtras;
            this.ordered = _serialized;
            this.sticky = _sticky;
            this.initialSticky = _initialSticky;
            this.userId = _userId;
            this.nextReceiver = 0;
            this.state = 0;
            this.allowBackgroundActivityStarts = _allowBackgroundActivityStarts;
            this.timeoutExempt = _timeoutExempt;
            return;
        }
        BroadcastQueue broadcastQueue = _queue;
        ProcessRecord processRecord = _callerApp;
        String str = _callerPackage;
        int i = _callingPid;
        int i2 = _callingUid;
        boolean z = _callerInstantApp;
        String str2 = _resolvedType;
        String[] strArr = _requiredPermissions;
        int i3 = _appOp;
        BroadcastOptions broadcastOptions = _options;
        IIntentReceiver iIntentReceiver = _resultTo;
        int i4 = _resultCode;
        String str3 = _resultData;
        boolean z2 = _timeoutExempt;
        throw new NullPointerException("Can't construct with a null intent");
    }

    private BroadcastRecord(BroadcastRecord from, Intent newIntent) {
        this.intent = newIntent;
        this.targetComp = newIntent.getComponent();
        this.callerApp = from.callerApp;
        this.callerPackage = from.callerPackage;
        this.callingPid = from.callingPid;
        this.callingUid = from.callingUid;
        this.callerInstantApp = from.callerInstantApp;
        this.ordered = from.ordered;
        this.sticky = from.sticky;
        this.initialSticky = from.initialSticky;
        this.userId = from.userId;
        this.resolvedType = from.resolvedType;
        this.requiredPermissions = from.requiredPermissions;
        this.appOp = from.appOp;
        this.options = from.options;
        this.receivers = from.receivers;
        this.delivery = from.delivery;
        this.duration = from.duration;
        this.resultTo = from.resultTo;
        this.enqueueClockTime = from.enqueueClockTime;
        this.dispatchTime = from.dispatchTime;
        this.dispatchClockTime = from.dispatchClockTime;
        this.receiverTime = from.receiverTime;
        this.finishTime = from.finishTime;
        this.resultCode = from.resultCode;
        this.resultData = from.resultData;
        this.resultExtras = from.resultExtras;
        this.resultAbort = from.resultAbort;
        this.nextReceiver = from.nextReceiver;
        this.receiver = from.receiver;
        this.state = from.state;
        this.anrCount = from.anrCount;
        this.manifestCount = from.manifestCount;
        this.manifestSkipCount = from.manifestSkipCount;
        this.queue = from.queue;
        this.allowBackgroundActivityStarts = from.allowBackgroundActivityStarts;
        this.timeoutExempt = from.timeoutExempt;
    }

    /* access modifiers changed from: package-private */
    public BroadcastRecord splitRecipientsLocked(int slowAppUid, int startingAt) {
        ArrayList splitReceivers = null;
        int i = startingAt;
        while (i < this.receivers.size()) {
            Object o = this.receivers.get(i);
            if (getReceiverUid(o) == slowAppUid) {
                if (splitReceivers == null) {
                    splitReceivers = new ArrayList();
                }
                splitReceivers.add(o);
                this.receivers.remove(i);
            } else {
                i++;
            }
        }
        int i2 = slowAppUid;
        if (splitReceivers == null) {
            return null;
        }
        BroadcastRecord broadcastRecord = new BroadcastRecord(this.queue, this.intent, this.callerApp, this.callerPackage, this.callingPid, this.callingUid, this.callerInstantApp, this.resolvedType, this.requiredPermissions, this.appOp, this.options, splitReceivers, this.resultTo, this.resultCode, this.resultData, this.resultExtras, this.ordered, this.sticky, this.initialSticky, this.userId, this.allowBackgroundActivityStarts, this.timeoutExempt);
        broadcastRecord.splitToken = this.splitToken;
        return broadcastRecord;
    }

    /* access modifiers changed from: package-private */
    public int getReceiverUid(Object receiver2) {
        if (receiver2 instanceof BroadcastFilter) {
            return ((BroadcastFilter) receiver2).owningUid;
        }
        return ((ResolveInfo) receiver2).activityInfo.applicationInfo.uid;
    }

    public BroadcastRecord maybeStripForHistory() {
        if (!this.intent.canStripForHistory()) {
            return this;
        }
        return new BroadcastRecord(this, this.intent.maybeStripForHistory());
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean cleanupDisabledPackageReceiversLocked(String packageName, Set<String> filterByClasses, int userId2, boolean doit) {
        if (this.receivers == null) {
            return false;
        }
        boolean cleanupAllUsers = userId2 == -1;
        boolean sendToAllUsers = this.userId == -1;
        if (this.userId != userId2 && !cleanupAllUsers && !sendToAllUsers) {
            return false;
        }
        boolean didSomething = false;
        for (int i = this.receivers.size() - 1; i >= 0; i--) {
            Object o = this.receivers.get(i);
            if (o instanceof ResolveInfo) {
                ActivityInfo info = ((ResolveInfo) o).activityInfo;
                if ((packageName == null || (info.applicationInfo.packageName.equals(packageName) && (filterByClasses == null || filterByClasses.contains(info.name)))) && (cleanupAllUsers || UserHandle.getUserId(info.applicationInfo.uid) == userId2)) {
                    if (!doit) {
                        return true;
                    }
                    didSomething = true;
                    this.receivers.remove(i);
                    int i2 = this.nextReceiver;
                    if (i < i2) {
                        this.nextReceiver = i2 - 1;
                    }
                }
            }
        }
        this.nextReceiver = Math.min(this.nextReceiver, this.receivers.size());
        return didSomething;
    }

    public String toString() {
        return "BroadcastRecord{" + Integer.toHexString(System.identityHashCode(this)) + " u" + this.userId + " " + this.intent.getAction() + "}";
    }

    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1120986464257L, this.userId);
        proto.write(1138166333442L, this.intent.getAction());
        proto.end(token);
    }
}
