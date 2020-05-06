package com.android.server.am;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.app.AppGlobals;
import android.app.IActivityController;
import android.app.IActivityManager;
import android.app.IActivityTaskManager;
import android.app.IApplicationThread;
import android.app.IStopUserCallback;
import android.app.IUidObserver;
import android.app.KeyguardManager;
import android.app.ProfilerInfo;
import android.app.WaitResult;
import android.app.usage.AppStandbyInfo;
import android.app.usage.ConfigurationStats;
import android.app.usage.IUsageStatsManager;
import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.SharedLibraryInfo;
import android.content.pm.UserInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.opengl.GLES10;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IProgressListener;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ShellCommand;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.ArrayMap;
import android.util.DebugUtils;
import android.util.DisplayMetrics;
import android.util.proto.ProtoOutputStream;
import android.view.Display;
import com.android.internal.util.HexDump;
import com.android.internal.util.MemInfoReader;
import com.android.internal.util.Preconditions;
import com.android.server.am.ActivityManagerService;
import com.android.server.job.controllers.JobStatus;
import com.android.server.pm.DumpState;
import com.android.server.utils.PriorityDump;
import com.android.server.wm.ActivityTaskManagerInternal;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

final class ActivityManagerShellCommand extends ShellCommand {
    public static final String NO_CLASS_ERROR_CODE = "Error type 3";
    private static final String SHELL_PACKAGE_NAME = "com.android.shell";
    private static final int USER_OPERATION_TIMEOUT_MS = 120000;
    /* access modifiers changed from: private */
    public int mActivityType;
    /* access modifiers changed from: private */
    public String mAgent;
    /* access modifiers changed from: private */
    public boolean mAttachAgentDuringBind;
    /* access modifiers changed from: private */
    public boolean mAutoStop;
    /* access modifiers changed from: private */
    public int mDisplayId;
    final boolean mDumping;
    final IActivityManager mInterface;
    final ActivityManagerService mInternal;
    /* access modifiers changed from: private */
    public boolean mIsLockTask;
    /* access modifiers changed from: private */
    public boolean mIsTaskOverlay;
    final IPackageManager mPm;
    /* access modifiers changed from: private */
    public String mProfileFile;
    /* access modifiers changed from: private */
    public String mReceiverPermission;
    /* access modifiers changed from: private */
    public int mRepeat = 0;
    /* access modifiers changed from: private */
    public int mSamplingInterval;
    private int mStartFlags = 0;
    /* access modifiers changed from: private */
    public boolean mStopOption = false;
    /* access modifiers changed from: private */
    public boolean mStreaming;
    /* access modifiers changed from: private */
    public int mTaskId;
    final IActivityTaskManager mTaskInterface;
    /* access modifiers changed from: private */
    public int mUserId;
    /* access modifiers changed from: private */
    public boolean mWaitOption = false;
    /* access modifiers changed from: private */
    public int mWindowingMode;

    static /* synthetic */ int access$076(ActivityManagerShellCommand x0, int x1) {
        int i = x0.mStartFlags | x1;
        x0.mStartFlags = i;
        return i;
    }

    ActivityManagerShellCommand(ActivityManagerService service, boolean dumping) {
        this.mInterface = service;
        this.mTaskInterface = service.mActivityTaskManager;
        this.mInternal = service;
        this.mPm = AppGlobals.getPackageManager();
        this.mDumping = dumping;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onCommand(java.lang.String r7) {
        /*
            r6 = this;
            if (r7 != 0) goto L_0x0007
            int r0 = r6.handleDefaultCommands(r7)
            return r0
        L_0x0007:
            java.io.PrintWriter r0 = r6.getOutPrintWriter()
            r1 = -1
            int r2 = r7.hashCode()     // Catch:{ RemoteException -> 0x0457 }
            r3 = 2
            r4 = 1
            r5 = 0
            switch(r2) {
                case -2121667104: goto L_0x031f;
                case -1969672196: goto L_0x0313;
                case -1719979774: goto L_0x0308;
                case -1710503333: goto L_0x02fc;
                case -1667670943: goto L_0x02f1;
                case -1619282346: goto L_0x02e5;
                case -1618876223: goto L_0x02da;
                case -1324660647: goto L_0x02ce;
                case -1303445945: goto L_0x02c2;
                case -1131287478: goto L_0x02b6;
                case -1002578147: goto L_0x02aa;
                case -965273485: goto L_0x029d;
                case -930080590: goto L_0x0291;
                case -907667276: goto L_0x0284;
                case -892396682: goto L_0x0278;
                case -870018278: goto L_0x026b;
                case -812219210: goto L_0x025f;
                case -747637291: goto L_0x0252;
                case -699625063: goto L_0x0246;
                case -606123342: goto L_0x0239;
                case -548621938: goto L_0x022c;
                case -387147436: goto L_0x021f;
                case -354890749: goto L_0x0212;
                case -309425751: goto L_0x0205;
                case -170987146: goto L_0x01f8;
                case -146027423: goto L_0x01eb;
                case -100644880: goto L_0x01df;
                case -27715536: goto L_0x01d2;
                case 3194994: goto L_0x01c6;
                case 3291998: goto L_0x01b9;
                case 3552645: goto L_0x01ac;
                case 88586660: goto L_0x01a0;
                case 94921639: goto L_0x0194;
                case 109757064: goto L_0x0187;
                case 109757538: goto L_0x017b;
                case 113399775: goto L_0x016e;
                case 185053203: goto L_0x0162;
                case 237240942: goto L_0x0155;
                case 549617690: goto L_0x0149;
                case 622433197: goto L_0x013c;
                case 667014829: goto L_0x0130;
                case 680834441: goto L_0x0123;
                case 723112852: goto L_0x0116;
                case 764545184: goto L_0x0109;
                case 808179021: goto L_0x00fc;
                case 810242677: goto L_0x00ef;
                case 817137578: goto L_0x00e3;
                case 822490030: goto L_0x00d6;
                case 900455412: goto L_0x00ca;
                case 1024703869: goto L_0x00be;
                case 1078591527: goto L_0x00b2;
                case 1097506319: goto L_0x00a5;
                case 1129261387: goto L_0x0098;
                case 1219773618: goto L_0x008c;
                case 1236319578: goto L_0x007f;
                case 1395483623: goto L_0x0072;
                case 1583986358: goto L_0x0065;
                case 1618908732: goto L_0x0058;
                case 1671764162: goto L_0x004c;
                case 1852789518: goto L_0x003f;
                case 1861559962: goto L_0x0032;
                case 1863290858: goto L_0x0025;
                case 2083239620: goto L_0x0018;
                default: goto L_0x0016;
            }     // Catch:{ RemoteException -> 0x0457 }
        L_0x0016:
            goto L_0x032a
        L_0x0018:
            java.lang.String r2 = "switch-user"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 36
            goto L_0x032b
        L_0x0025:
            java.lang.String r2 = "stop-service"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 9
            goto L_0x032b
        L_0x0032:
            java.lang.String r2 = "idle-maintenance"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 30
            goto L_0x032b
        L_0x003f:
            java.lang.String r2 = "no-home-screen"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 61
            goto L_0x032b
        L_0x004c:
            java.lang.String r2 = "display"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 53
            goto L_0x032b
        L_0x0058:
            java.lang.String r2 = "wait-for-broadcast-idle"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 62
            goto L_0x032b
        L_0x0065:
            java.lang.String r2 = "stop-user"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 40
            goto L_0x032b
        L_0x0072:
            java.lang.String r2 = "instrument"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 11
            goto L_0x032b
        L_0x007f:
            java.lang.String r2 = "monitor"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 26
            goto L_0x032b
        L_0x008c:
            java.lang.String r2 = "get-started-user-state"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 42
            goto L_0x032b
        L_0x0098:
            java.lang.String r2 = "update-appinfo"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 60
            goto L_0x032b
        L_0x00a5:
            java.lang.String r2 = "restart"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 29
            goto L_0x032b
        L_0x00b2:
            java.lang.String r2 = "clear-debug-app"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 17
            goto L_0x032b
        L_0x00be:
            java.lang.String r2 = "attach-agent"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 57
            goto L_0x032b
        L_0x00ca:
            java.lang.String r2 = "start-fg-service"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 7
            goto L_0x032b
        L_0x00d6:
            java.lang.String r2 = "set-agent-app"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 16
            goto L_0x032b
        L_0x00e3:
            java.lang.String r2 = "clear-watch-heap"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 19
            goto L_0x032b
        L_0x00ef:
            java.lang.String r2 = "set-watch-heap"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 18
            goto L_0x032b
        L_0x00fc:
            java.lang.String r2 = "to-intent-uri"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 34
            goto L_0x032b
        L_0x0109:
            java.lang.String r2 = "supports-multiwindow"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 58
            goto L_0x032b
        L_0x0116:
            java.lang.String r2 = "trace-ipc"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 12
            goto L_0x032b
        L_0x0123:
            java.lang.String r2 = "supports-split-screen-multi-window"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 59
            goto L_0x032b
        L_0x0130:
            java.lang.String r2 = "bug-report"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 20
            goto L_0x032b
        L_0x013c:
            java.lang.String r2 = "untrack-associations"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 44
            goto L_0x032b
        L_0x0149:
            java.lang.String r2 = "start-activity"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = r4
            goto L_0x032b
        L_0x0155:
            java.lang.String r2 = "to-app-uri"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 35
            goto L_0x032b
        L_0x0162:
            java.lang.String r2 = "startservice"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = r3
            goto L_0x032b
        L_0x016e:
            java.lang.String r2 = "write"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 56
            goto L_0x032b
        L_0x017b:
            java.lang.String r2 = "start"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = r5
            goto L_0x032b
        L_0x0187:
            java.lang.String r2 = "stack"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 54
            goto L_0x032b
        L_0x0194:
            java.lang.String r2 = "crash"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 22
            goto L_0x032b
        L_0x01a0:
            java.lang.String r2 = "force-stop"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 21
            goto L_0x032b
        L_0x01ac:
            java.lang.String r2 = "task"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 55
            goto L_0x032b
        L_0x01b9:
            java.lang.String r2 = "kill"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 23
            goto L_0x032b
        L_0x01c6:
            java.lang.String r2 = "hang"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 28
            goto L_0x032b
        L_0x01d2:
            java.lang.String r2 = "make-uid-idle"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 25
            goto L_0x032b
        L_0x01df:
            java.lang.String r2 = "startforegroundservice"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 4
            goto L_0x032b
        L_0x01eb:
            java.lang.String r2 = "watch-uids"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 27
            goto L_0x032b
        L_0x01f8:
            java.lang.String r2 = "set-inactive"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 48
            goto L_0x032b
        L_0x0205:
            java.lang.String r2 = "profile"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 13
            goto L_0x032b
        L_0x0212:
            java.lang.String r2 = "screen-compat"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 31
            goto L_0x032b
        L_0x021f:
            java.lang.String r2 = "track-associations"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 43
            goto L_0x032b
        L_0x022c:
            java.lang.String r2 = "is-user-stopped"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 41
            goto L_0x032b
        L_0x0239:
            java.lang.String r2 = "kill-all"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 24
            goto L_0x032b
        L_0x0246:
            java.lang.String r2 = "get-config"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 46
            goto L_0x032b
        L_0x0252:
            java.lang.String r2 = "set-standby-bucket"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 50
            goto L_0x032b
        L_0x025f:
            java.lang.String r2 = "get-current-user"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 37
            goto L_0x032b
        L_0x026b:
            java.lang.String r2 = "to-uri"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 33
            goto L_0x032b
        L_0x0278:
            java.lang.String r2 = "start-foreground-service"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 6
            goto L_0x032b
        L_0x0284:
            java.lang.String r2 = "unlock-user"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 39
            goto L_0x032b
        L_0x0291:
            java.lang.String r2 = "startfgservice"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 5
            goto L_0x032b
        L_0x029d:
            java.lang.String r2 = "stopservice"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 8
            goto L_0x032b
        L_0x02aa:
            java.lang.String r2 = "get-uid-state"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 45
            goto L_0x032b
        L_0x02b6:
            java.lang.String r2 = "start-service"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 3
            goto L_0x032b
        L_0x02c2:
            java.lang.String r2 = "send-trim-memory"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 52
            goto L_0x032b
        L_0x02ce:
            java.lang.String r2 = "suppress-resize-config-changes"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 47
            goto L_0x032b
        L_0x02da:
            java.lang.String r2 = "broadcast"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 10
            goto L_0x032b
        L_0x02e5:
            java.lang.String r2 = "start-user"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 38
            goto L_0x032b
        L_0x02f1:
            java.lang.String r2 = "get-standby-bucket"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 51
            goto L_0x032b
        L_0x02fc:
            java.lang.String r2 = "package-importance"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 32
            goto L_0x032b
        L_0x0308:
            java.lang.String r2 = "get-inactive"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 49
            goto L_0x032b
        L_0x0313:
            java.lang.String r2 = "set-debug-app"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 15
            goto L_0x032b
        L_0x031f:
            java.lang.String r2 = "dumpheap"
            boolean r2 = r7.equals(r2)     // Catch:{ RemoteException -> 0x0457 }
            if (r2 == 0) goto L_0x0016
            r2 = 14
            goto L_0x032b
        L_0x032a:
            r2 = r1
        L_0x032b:
            switch(r2) {
                case 0: goto L_0x0451;
                case 1: goto L_0x0451;
                case 2: goto L_0x044c;
                case 3: goto L_0x044c;
                case 4: goto L_0x0447;
                case 5: goto L_0x0447;
                case 6: goto L_0x0447;
                case 7: goto L_0x0447;
                case 8: goto L_0x0442;
                case 9: goto L_0x0442;
                case 10: goto L_0x043d;
                case 11: goto L_0x0433;
                case 12: goto L_0x042e;
                case 13: goto L_0x0429;
                case 14: goto L_0x0424;
                case 15: goto L_0x041f;
                case 16: goto L_0x041a;
                case 17: goto L_0x0415;
                case 18: goto L_0x0410;
                case 19: goto L_0x040b;
                case 20: goto L_0x0406;
                case 21: goto L_0x0401;
                case 22: goto L_0x03fc;
                case 23: goto L_0x03f7;
                case 24: goto L_0x03f2;
                case 25: goto L_0x03ed;
                case 26: goto L_0x03e8;
                case 27: goto L_0x03e3;
                case 28: goto L_0x03de;
                case 29: goto L_0x03d9;
                case 30: goto L_0x03d4;
                case 31: goto L_0x03cf;
                case 32: goto L_0x03ca;
                case 33: goto L_0x03c5;
                case 34: goto L_0x03c0;
                case 35: goto L_0x03bb;
                case 36: goto L_0x03b6;
                case 37: goto L_0x03b1;
                case 38: goto L_0x03ac;
                case 39: goto L_0x03a7;
                case 40: goto L_0x03a2;
                case 41: goto L_0x039d;
                case 42: goto L_0x0398;
                case 43: goto L_0x0393;
                case 44: goto L_0x038e;
                case 45: goto L_0x0389;
                case 46: goto L_0x0384;
                case 47: goto L_0x037f;
                case 48: goto L_0x037a;
                case 49: goto L_0x0375;
                case 50: goto L_0x0370;
                case 51: goto L_0x036b;
                case 52: goto L_0x0366;
                case 53: goto L_0x0361;
                case 54: goto L_0x035c;
                case 55: goto L_0x0357;
                case 56: goto L_0x0352;
                case 57: goto L_0x034d;
                case 58: goto L_0x0348;
                case 59: goto L_0x0343;
                case 60: goto L_0x033e;
                case 61: goto L_0x0339;
                case 62: goto L_0x0334;
                default: goto L_0x032e;
            }     // Catch:{ RemoteException -> 0x0457 }
        L_0x032e:
            int r1 = r6.handleDefaultCommands(r7)     // Catch:{ RemoteException -> 0x0457 }
            goto L_0x0456
        L_0x0334:
            int r1 = r6.runWaitForBroadcastIdle(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0339:
            int r1 = r6.runNoHomeScreen(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x033e:
            int r1 = r6.runUpdateApplicationInfo(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0343:
            int r1 = r6.runSupportsSplitScreenMultiwindow(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0348:
            int r1 = r6.runSupportsMultiwindow(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x034d:
            int r1 = r6.runAttachAgent(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0352:
            int r1 = r6.runWrite(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0357:
            int r1 = r6.runTask(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x035c:
            int r1 = r6.runStack(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0361:
            int r1 = r6.runDisplay(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0366:
            int r1 = r6.runSendTrimMemory(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x036b:
            int r1 = r6.runGetStandbyBucket(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0370:
            int r1 = r6.runSetStandbyBucket(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0375:
            int r1 = r6.runGetInactive(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x037a:
            int r1 = r6.runSetInactive(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x037f:
            int r1 = r6.runSuppressResizeConfigChanges(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0384:
            int r1 = r6.runGetConfig(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0389:
            int r1 = r6.getUidState(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x038e:
            int r1 = r6.runUntrackAssociations(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0393:
            int r1 = r6.runTrackAssociations(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0398:
            int r1 = r6.runGetStartedUserState(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x039d:
            int r1 = r6.runIsUserStopped(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03a2:
            int r1 = r6.runStopUser(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03a7:
            int r1 = r6.runUnlockUser(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03ac:
            int r1 = r6.runStartUser(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03b1:
            int r1 = r6.runGetCurrentUser(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03b6:
            int r1 = r6.runSwitchUser(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03bb:
            int r1 = r6.runToUri(r0, r3)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03c0:
            int r1 = r6.runToUri(r0, r4)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03c5:
            int r1 = r6.runToUri(r0, r5)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03ca:
            int r1 = r6.runPackageImportance(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03cf:
            int r1 = r6.runScreenCompat(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03d4:
            int r1 = r6.runIdleMaintenance(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03d9:
            int r1 = r6.runRestart(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03de:
            int r1 = r6.runHang(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03e3:
            int r1 = r6.runWatchUids(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03e8:
            int r1 = r6.runMonitor(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03ed:
            int r1 = r6.runMakeIdle(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03f2:
            int r1 = r6.runKillAll(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03f7:
            int r1 = r6.runKill(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x03fc:
            int r1 = r6.runCrash(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0401:
            int r1 = r6.runForceStop(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0406:
            int r1 = r6.runBugReport(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x040b:
            int r1 = r6.runClearWatchHeap(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0410:
            int r1 = r6.runSetWatchHeap(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0415:
            int r1 = r6.runClearDebugApp(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x041a:
            int r1 = r6.runSetAgentApp(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x041f:
            int r1 = r6.runSetDebugApp(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0424:
            int r1 = r6.runDumpHeap(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0429:
            int r1 = r6.runProfile(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x042e:
            int r1 = r6.runTraceIpc(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0433:
            java.io.PrintWriter r2 = r6.getOutPrintWriter()     // Catch:{ RemoteException -> 0x0457 }
            java.lang.String r3 = "Error: must be invoked through 'am instrument'."
            r2.println(r3)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x043d:
            int r1 = r6.runSendBroadcast(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0442:
            int r1 = r6.runStopService(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0447:
            int r1 = r6.runStartService(r0, r4)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x044c:
            int r1 = r6.runStartService(r0, r5)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0451:
            int r1 = r6.runStartActivity(r0)     // Catch:{ RemoteException -> 0x0457 }
            return r1
        L_0x0456:
            return r1
        L_0x0457:
            r2 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Remote exception: "
            r3.append(r4)
            r3.append(r2)
            java.lang.String r3 = r3.toString()
            r0.println(r3)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActivityManagerShellCommand.onCommand(java.lang.String):int");
    }

    private Intent makeIntent(int defUser) throws URISyntaxException {
        this.mStartFlags = 0;
        this.mWaitOption = false;
        this.mStopOption = false;
        this.mRepeat = 0;
        this.mProfileFile = null;
        this.mSamplingInterval = 0;
        this.mAutoStop = false;
        this.mStreaming = false;
        this.mUserId = defUser;
        this.mDisplayId = -1;
        this.mWindowingMode = 0;
        this.mActivityType = 0;
        this.mTaskId = -1;
        this.mIsTaskOverlay = false;
        this.mIsLockTask = false;
        return Intent.parseCommandArgs(this, new Intent.CommandOptionHandler() {
            public boolean handleOption(String opt, ShellCommand cmd) {
                if (opt.equals("-D")) {
                    ActivityManagerShellCommand.access$076(ActivityManagerShellCommand.this, 2);
                } else if (opt.equals("-N")) {
                    ActivityManagerShellCommand.access$076(ActivityManagerShellCommand.this, 8);
                } else if (opt.equals("-W")) {
                    boolean unused = ActivityManagerShellCommand.this.mWaitOption = true;
                } else if (opt.equals("-P")) {
                    ActivityManagerShellCommand activityManagerShellCommand = ActivityManagerShellCommand.this;
                    String unused2 = activityManagerShellCommand.mProfileFile = activityManagerShellCommand.getNextArgRequired();
                    boolean unused3 = ActivityManagerShellCommand.this.mAutoStop = true;
                } else if (opt.equals("--start-profiler")) {
                    ActivityManagerShellCommand activityManagerShellCommand2 = ActivityManagerShellCommand.this;
                    String unused4 = activityManagerShellCommand2.mProfileFile = activityManagerShellCommand2.getNextArgRequired();
                    boolean unused5 = ActivityManagerShellCommand.this.mAutoStop = false;
                } else if (opt.equals("--sampling")) {
                    ActivityManagerShellCommand activityManagerShellCommand3 = ActivityManagerShellCommand.this;
                    int unused6 = activityManagerShellCommand3.mSamplingInterval = Integer.parseInt(activityManagerShellCommand3.getNextArgRequired());
                } else if (opt.equals("--streaming")) {
                    boolean unused7 = ActivityManagerShellCommand.this.mStreaming = true;
                } else if (opt.equals("--attach-agent")) {
                    if (ActivityManagerShellCommand.this.mAgent != null) {
                        cmd.getErrPrintWriter().println("Multiple --attach-agent(-bind) not supported");
                        return false;
                    }
                    ActivityManagerShellCommand activityManagerShellCommand4 = ActivityManagerShellCommand.this;
                    String unused8 = activityManagerShellCommand4.mAgent = activityManagerShellCommand4.getNextArgRequired();
                    boolean unused9 = ActivityManagerShellCommand.this.mAttachAgentDuringBind = false;
                } else if (opt.equals("--attach-agent-bind")) {
                    if (ActivityManagerShellCommand.this.mAgent != null) {
                        cmd.getErrPrintWriter().println("Multiple --attach-agent(-bind) not supported");
                        return false;
                    }
                    ActivityManagerShellCommand activityManagerShellCommand5 = ActivityManagerShellCommand.this;
                    String unused10 = activityManagerShellCommand5.mAgent = activityManagerShellCommand5.getNextArgRequired();
                    boolean unused11 = ActivityManagerShellCommand.this.mAttachAgentDuringBind = true;
                } else if (opt.equals("-R")) {
                    ActivityManagerShellCommand activityManagerShellCommand6 = ActivityManagerShellCommand.this;
                    int unused12 = activityManagerShellCommand6.mRepeat = Integer.parseInt(activityManagerShellCommand6.getNextArgRequired());
                } else if (opt.equals("-S")) {
                    boolean unused13 = ActivityManagerShellCommand.this.mStopOption = true;
                } else if (opt.equals("--track-allocation")) {
                    ActivityManagerShellCommand.access$076(ActivityManagerShellCommand.this, 4);
                } else if (opt.equals("--user")) {
                    ActivityManagerShellCommand activityManagerShellCommand7 = ActivityManagerShellCommand.this;
                    int unused14 = activityManagerShellCommand7.mUserId = UserHandle.parseUserArg(activityManagerShellCommand7.getNextArgRequired());
                } else if (opt.equals("--receiver-permission")) {
                    ActivityManagerShellCommand activityManagerShellCommand8 = ActivityManagerShellCommand.this;
                    String unused15 = activityManagerShellCommand8.mReceiverPermission = activityManagerShellCommand8.getNextArgRequired();
                } else if (opt.equals("--display")) {
                    ActivityManagerShellCommand activityManagerShellCommand9 = ActivityManagerShellCommand.this;
                    int unused16 = activityManagerShellCommand9.mDisplayId = Integer.parseInt(activityManagerShellCommand9.getNextArgRequired());
                } else if (opt.equals("--windowingMode")) {
                    ActivityManagerShellCommand activityManagerShellCommand10 = ActivityManagerShellCommand.this;
                    int unused17 = activityManagerShellCommand10.mWindowingMode = Integer.parseInt(activityManagerShellCommand10.getNextArgRequired());
                } else if (opt.equals("--activityType")) {
                    ActivityManagerShellCommand activityManagerShellCommand11 = ActivityManagerShellCommand.this;
                    int unused18 = activityManagerShellCommand11.mActivityType = Integer.parseInt(activityManagerShellCommand11.getNextArgRequired());
                } else if (opt.equals("--task")) {
                    ActivityManagerShellCommand activityManagerShellCommand12 = ActivityManagerShellCommand.this;
                    int unused19 = activityManagerShellCommand12.mTaskId = Integer.parseInt(activityManagerShellCommand12.getNextArgRequired());
                } else if (opt.equals("--task-overlay")) {
                    boolean unused20 = ActivityManagerShellCommand.this.mIsTaskOverlay = true;
                } else if (!opt.equals("--lock-task")) {
                    return false;
                } else {
                    boolean unused21 = ActivityManagerShellCommand.this.mIsLockTask = true;
                }
                return true;
            }
        });
    }

    private class ProgressWaiter extends IProgressListener.Stub {
        private final CountDownLatch mFinishedLatch;

        private ProgressWaiter() {
            this.mFinishedLatch = new CountDownLatch(1);
        }

        public void onStarted(int id, Bundle extras) {
        }

        public void onProgress(int id, int progress, Bundle extras) {
        }

        public void onFinished(int id, Bundle extras) {
            this.mFinishedLatch.countDown();
        }

        public boolean waitForFinish(long timeoutMillis) {
            try {
                return this.mFinishedLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted unexpectedly.");
                return false;
            }
        }
    }

    /* JADX WARNING: type inference failed for: r13v0 */
    /* JADX WARNING: type inference failed for: r13v1, types: [boolean, int] */
    /* JADX WARNING: type inference failed for: r13v2 */
    /* access modifiers changed from: package-private */
    public int runStartActivity(PrintWriter pw) throws RemoteException {
        String mimeType;
        ActivityOptions options;
        int i;
        String mimeType2;
        int i2;
        int res;
        int i3;
        WaitResult result;
        String packageName;
        PrintWriter printWriter = pw;
        try {
            Intent intent = makeIntent(-2);
            int i4 = -1;
            ? r13 = 1;
            if (this.mUserId == -1) {
                getErrPrintWriter().println("Error: Can't start service with user 'all'");
                return 1;
            }
            String mimeType3 = intent.getType();
            if (mimeType3 != null || intent.getData() == null || !ActivityTaskManagerInternal.ASSIST_KEY_CONTENT.equals(intent.getData().getScheme())) {
                mimeType = mimeType3;
            } else {
                mimeType = this.mInterface.getProviderMimeType(intent.getData(), this.mUserId);
            }
            while (true) {
                if (this.mStopOption) {
                    if (intent.getComponent() != null) {
                        packageName = intent.getComponent().getPackageName();
                    } else {
                        List<ResolveInfo> activities = this.mPm.queryIntentActivities(intent, mimeType, 0, this.mInternal.mUserController.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), this.mUserId, false, 0, "ActivityManagerShellCommand", (String) null)).getList();
                        if (activities == null || activities.size() <= 0) {
                            getErrPrintWriter().println("Error: Intent does not match any activities: " + intent);
                        } else if (activities.size() > r13) {
                            getErrPrintWriter().println("Error: Intent matches multiple activities; can't stop: " + intent);
                            return r13;
                        } else {
                            packageName = activities.get(0).activityInfo.packageName;
                        }
                    }
                    printWriter.println("Stopping: " + packageName);
                    pw.flush();
                    this.mInterface.forceStopPackage(packageName, this.mUserId);
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                    }
                }
                ProfilerInfo profilerInfo = null;
                if (!(this.mProfileFile == null && this.mAgent == null)) {
                    ParcelFileDescriptor fd = null;
                    String str = this.mProfileFile;
                    if (str != null && (fd = openFileForSystem(str, "w")) == null) {
                        return r13;
                    }
                    profilerInfo = new ProfilerInfo(this.mProfileFile, fd, this.mSamplingInterval, this.mAutoStop, this.mStreaming, this.mAgent, this.mAttachAgentDuringBind);
                }
                printWriter.println("Starting: " + intent);
                pw.flush();
                intent.addFlags(268435456);
                WaitResult result2 = null;
                long startTime = SystemClock.uptimeMillis();
                ActivityOptions options2 = null;
                if (this.mDisplayId != i4) {
                    options2 = ActivityOptions.makeBasic();
                    options2.setLaunchDisplayId(this.mDisplayId);
                }
                if (this.mWindowingMode != 0) {
                    if (options2 == null) {
                        options2 = ActivityOptions.makeBasic();
                    }
                    options2.setLaunchWindowingMode(this.mWindowingMode);
                }
                if (this.mActivityType != 0) {
                    if (options2 == null) {
                        options2 = ActivityOptions.makeBasic();
                    }
                    options2.setLaunchActivityType(this.mActivityType);
                }
                if (this.mTaskId != i4) {
                    if (options2 == null) {
                        options2 = ActivityOptions.makeBasic();
                    }
                    options2.setLaunchTaskId(this.mTaskId);
                    if (this.mIsTaskOverlay) {
                        options2.setTaskOverlay(r13, r13);
                    }
                }
                if (this.mIsLockTask) {
                    if (options2 == null) {
                        options2 = ActivityOptions.makeBasic();
                    }
                    options2.setLockTaskEnabled(r13);
                    options = options2;
                } else {
                    options = options2;
                }
                Bundle bundle = null;
                if (this.mWaitOption) {
                    ActivityManagerService activityManagerService = this.mInternal;
                    int i5 = this.mStartFlags;
                    if (options != null) {
                        bundle = options.toBundle();
                    }
                    i = 0;
                    mimeType2 = mimeType;
                    i2 = i4;
                    WaitResult result3 = activityManagerService.startActivityAndWait((IApplicationThread) null, (String) null, intent, mimeType, (IBinder) null, (String) null, 0, i5, profilerInfo, bundle, this.mUserId);
                    res = result3.result;
                    result2 = result3;
                } else {
                    i = 0;
                    mimeType2 = mimeType;
                    i2 = i4;
                    ActivityManagerService activityManagerService2 = this.mInternal;
                    int i6 = this.mStartFlags;
                    if (options != null) {
                        bundle = options.toBundle();
                    }
                    res = activityManagerService2.startActivityAsUser((IApplicationThread) null, (String) null, intent, mimeType2, (IBinder) null, (String) null, 0, i6, profilerInfo, bundle, this.mUserId);
                }
                long endTime = SystemClock.uptimeMillis();
                PrintWriter out = this.mWaitOption ? printWriter : getErrPrintWriter();
                boolean launched = false;
                if (res == -98) {
                    i3 = 1;
                    out.println("Error: Not allowed to start background user activity that shouldn't be displayed for all users.");
                } else if (res == -97) {
                    i3 = 1;
                    out.println("Error: Activity not started, voice control not allowed for: " + intent);
                } else if (res != 0) {
                    i3 = 1;
                    if (res == 1) {
                        launched = true;
                        out.println("Warning: Activity not started because intent should be handled by the caller");
                    } else if (res == 2) {
                        launched = true;
                        out.println("Warning: Activity not started, its current task has been brought to the front");
                    } else if (res == 3) {
                        launched = true;
                        out.println("Warning: Activity not started, intent has been delivered to currently running top-most instance.");
                    } else if (res != 100) {
                        switch (res) {
                            case -94:
                                out.println("Error: Activity not started, you do not have permission to access it.");
                                break;
                            case -93:
                                out.println("Error: Activity not started, you requested to both forward and receive its result");
                                break;
                            case -92:
                                out.println(NO_CLASS_ERROR_CODE);
                                out.println("Error: Activity class " + intent.getComponent().toShortString() + " does not exist.");
                                break;
                            case -91:
                                out.println("Error: Activity not started, unable to resolve " + intent.toString());
                                break;
                            default:
                                out.println("Error: Activity not started, unknown error code " + res);
                                break;
                        }
                    } else {
                        launched = true;
                        out.println("Warning: Activity not started because the  current activity is being kept for the user.");
                    }
                } else {
                    i3 = 1;
                    launched = true;
                }
                out.flush();
                if (this.mWaitOption && launched) {
                    if (result2 == null) {
                        result = new WaitResult();
                        result.who = intent.getComponent();
                    } else {
                        result = result2;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("Status: ");
                    sb.append(result.timeout ? "timeout" : "ok");
                    printWriter.println(sb.toString());
                    printWriter.println("LaunchState: " + WaitResult.launchStateToString(result.launchState));
                    if (result.who != null) {
                        printWriter.println("Activity: " + result.who.flattenToShortString());
                    }
                    if (result.totalTime >= 0) {
                        printWriter.println("TotalTime: " + result.totalTime);
                    }
                    printWriter.println("WaitTime: " + (endTime - startTime));
                    printWriter.println("Complete");
                    pw.flush();
                    WaitResult waitResult = result;
                }
                this.mRepeat -= i3;
                if (this.mRepeat > 0) {
                    this.mTaskInterface.unhandledBack();
                }
                if (this.mRepeat <= 0) {
                    return i;
                }
                r13 = i3;
                i4 = i2;
                mimeType = mimeType2;
            }
            getErrPrintWriter().println("Error: Intent does not match any activities: " + intent);
            return r13;
        } catch (URISyntaxException e2) {
            URISyntaxException e3 = e2;
            throw new RuntimeException(e3.getMessage(), e3);
        }
    }

    /* access modifiers changed from: package-private */
    public int runStartService(PrintWriter pw, boolean asForeground) throws RemoteException {
        PrintWriter err = getErrPrintWriter();
        try {
            Intent intent = makeIntent(-2);
            if (this.mUserId == -1) {
                err.println("Error: Can't start activity with user 'all'");
                return -1;
            }
            pw.println("Starting service: " + intent);
            pw.flush();
            ComponentName cn = this.mInterface.startService((IApplicationThread) null, intent, intent.getType(), asForeground, "com.android.shell", this.mUserId);
            if (cn == null) {
                err.println("Error: Not found; no service started.");
                return -1;
            } else if (cn.getPackageName().equals("!")) {
                err.println("Error: Requires permission " + cn.getClassName());
                return -1;
            } else if (cn.getPackageName().equals("!!")) {
                err.println("Error: " + cn.getClassName());
                return -1;
            } else if (!cn.getPackageName().equals("?")) {
                return 0;
            } else {
                err.println("Error: " + cn.getClassName());
                return -1;
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /* access modifiers changed from: package-private */
    public int runStopService(PrintWriter pw) throws RemoteException {
        PrintWriter err = getErrPrintWriter();
        try {
            Intent intent = makeIntent(-2);
            if (this.mUserId == -1) {
                err.println("Error: Can't stop activity with user 'all'");
                return -1;
            }
            pw.println("Stopping service: " + intent);
            pw.flush();
            int result = this.mInterface.stopService((IApplicationThread) null, intent, intent.getType(), this.mUserId);
            if (result == 0) {
                err.println("Service not stopped: was not running.");
                return -1;
            } else if (result == 1) {
                err.println("Service stopped");
                return -1;
            } else if (result != -1) {
                return 0;
            } else {
                err.println("Error stopping service");
                return -1;
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    static final class IntentReceiver extends IIntentReceiver.Stub {
        private boolean mFinished = false;
        private final PrintWriter mPw;

        IntentReceiver(PrintWriter pw) {
            this.mPw = pw;
        }

        public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) {
            String line = "Broadcast completed: result=" + resultCode;
            if (data != null) {
                line = line + ", data=\"" + data + "\"";
            }
            if (extras != null) {
                line = line + ", extras: " + extras;
            }
            this.mPw.println(line);
            this.mPw.flush();
            synchronized (this) {
                this.mFinished = true;
                notifyAll();
            }
        }

        public synchronized void waitForFinish() {
            while (!this.mFinished) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int runSendBroadcast(PrintWriter pw) throws RemoteException {
        String[] requiredPermissions;
        PrintWriter printWriter = pw;
        try {
            Intent intent = makeIntent(-2);
            intent.addFlags(DumpState.DUMP_CHANGES);
            IntentReceiver receiver = new IntentReceiver(printWriter);
            String str = this.mReceiverPermission;
            if (str == null) {
                requiredPermissions = null;
            } else {
                requiredPermissions = new String[]{str};
            }
            printWriter.println("Broadcasting: " + intent);
            pw.flush();
            this.mInterface.broadcastIntent((IApplicationThread) null, intent, (String) null, receiver, 0, (String) null, (Bundle) null, requiredPermissions, -1, (Bundle) null, true, false, this.mUserId);
            receiver.waitForFinish();
            return 0;
        } catch (URISyntaxException e) {
            URISyntaxException e2 = e;
            throw new RuntimeException(e2.getMessage(), e2);
        }
    }

    /* access modifiers changed from: package-private */
    public int runTraceIpc(PrintWriter pw) throws RemoteException {
        String op = getNextArgRequired();
        if (op.equals("start")) {
            return runTraceIpcStart(pw);
        }
        if (op.equals("stop")) {
            return runTraceIpcStop(pw);
        }
        PrintWriter errPrintWriter = getErrPrintWriter();
        errPrintWriter.println("Error: unknown trace ipc command '" + op + "'");
        return -1;
    }

    /* access modifiers changed from: package-private */
    public int runTraceIpcStart(PrintWriter pw) throws RemoteException {
        pw.println("Starting IPC tracing.");
        pw.flush();
        this.mInterface.startBinderTracking();
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runTraceIpcStop(PrintWriter pw) throws RemoteException {
        PrintWriter err = getErrPrintWriter();
        String filename = null;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption != null) {
                if (opt.equals("--dump-file")) {
                    filename = getNextArgRequired();
                } else {
                    err.println("Error: Unknown option: " + opt);
                    return -1;
                }
            } else if (filename == null) {
                err.println("Error: Specify filename to dump logs to.");
                return -1;
            } else {
                new File(filename).delete();
                ParcelFileDescriptor fd = openFileForSystem(filename, "w");
                if (fd == null) {
                    return -1;
                }
                if (!this.mInterface.stopBinderTrackingAndDump(fd)) {
                    err.println("STOP TRACE FAILED.");
                    return -1;
                }
                pw.println("Stopped IPC tracing. Dumping logs to: " + filename);
                return 0;
            }
        }
    }

    static void removeWallOption() {
        String props = SystemProperties.get("dalvik.vm.extra-opts");
        if (props != null && props.contains("-Xprofile:wallclock")) {
            SystemProperties.set("dalvik.vm.extra-opts", props.replace("-Xprofile:wallclock", "").trim());
        }
    }

    private int runProfile(PrintWriter pw) throws RemoteException {
        boolean wall;
        String process;
        ProfilerInfo profilerInfo;
        PrintWriter err = getErrPrintWriter();
        boolean start = false;
        boolean wall2 = false;
        int userId = -2;
        this.mSamplingInterval = 0;
        this.mStreaming = false;
        String cmd = getNextArgRequired();
        if ("start".equals(cmd)) {
            start = true;
            while (true) {
                String nextOption = getNextOption();
                String opt = nextOption;
                if (nextOption == null) {
                    wall = wall2;
                    process = getNextArgRequired();
                    String str = cmd;
                    break;
                } else if (opt.equals("--user")) {
                    userId = UserHandle.parseUserArg(getNextArgRequired());
                } else if (opt.equals("--wall")) {
                    wall2 = true;
                } else if (opt.equals("--streaming")) {
                    this.mStreaming = true;
                } else if (opt.equals("--sampling")) {
                    this.mSamplingInterval = Integer.parseInt(getNextArgRequired());
                } else {
                    err.println("Error: Unknown option: " + opt);
                    return -1;
                }
            }
        } else if ("stop".equals(cmd)) {
            while (true) {
                String nextOption2 = getNextOption();
                String opt2 = nextOption2;
                if (nextOption2 == null) {
                    wall = false;
                    process = getNextArgRequired();
                    String str2 = cmd;
                    break;
                } else if (opt2.equals("--user")) {
                    userId = UserHandle.parseUserArg(getNextArgRequired());
                } else {
                    err.println("Error: Unknown option: " + opt2);
                    return -1;
                }
            }
        } else {
            String process2 = cmd;
            String cmd2 = getNextArgRequired();
            if ("start".equals(cmd2)) {
                start = true;
                wall = false;
                process = process2;
                String str3 = cmd2;
            } else if ("stop".equals(cmd2)) {
                wall = false;
                process = process2;
                String str4 = cmd2;
            } else {
                throw new IllegalArgumentException("Profile command " + process2 + " not valid");
            }
        }
        if (userId == -1) {
            err.println("Error: Can't profile with user 'all'");
            return -1;
        }
        if (start) {
            String profileFile = getNextArgRequired();
            ParcelFileDescriptor fd = openFileForSystem(profileFile, "w");
            if (fd == null) {
                return -1;
            }
            String str5 = profileFile;
            ParcelFileDescriptor parcelFileDescriptor = fd;
            profilerInfo = new ProfilerInfo(profileFile, fd, this.mSamplingInterval, false, this.mStreaming, (String) null, false);
        } else {
            profilerInfo = null;
        }
        if (wall) {
            try {
                String props = SystemProperties.get("dalvik.vm.extra-opts");
                if (props == null || !props.contains("-Xprofile:wallclock")) {
                    props + " -Xprofile:wallclock";
                }
            } finally {
            }
        }
        if (this.mInterface.profileControl(process, userId, start, profilerInfo, 0)) {
            return 0;
        }
        err.println("PROFILE FAILED on process " + process);
        return -1;
    }

    /* access modifiers changed from: package-private */
    public int runDumpHeap(PrintWriter pw) throws RemoteException {
        String heapFile;
        PrintWriter printWriter = pw;
        PrintWriter err = getErrPrintWriter();
        int userId = -2;
        boolean runGc = false;
        boolean mallocInfo = false;
        boolean managed = true;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                String process = getNextArgRequired();
                String heapFile2 = getNextArg();
                if (heapFile2 == null) {
                    Time t = new Time();
                    t.set(System.currentTimeMillis());
                    heapFile = "/data/local/tmp/heapdump-" + t.format("%Y%m%d-%H%M%S") + ".prof";
                } else {
                    heapFile = heapFile2;
                }
                printWriter.println("File: " + heapFile);
                pw.flush();
                new File(heapFile).delete();
                ParcelFileDescriptor fd = openFileForSystem(heapFile, "w");
                if (fd == null) {
                    return -1;
                }
                final CountDownLatch latch = new CountDownLatch(1);
                RemoteCallback finishCallback = new RemoteCallback(new RemoteCallback.OnResultListener() {
                    public void onResult(Bundle result) {
                        latch.countDown();
                    }
                }, (Handler) null);
                CountDownLatch latch2 = latch;
                String process2 = process;
                String str = opt;
                if (!this.mInterface.dumpHeap(process, userId, managed, mallocInfo, runGc, heapFile, fd, finishCallback)) {
                    err.println("HEAP DUMP FAILED on process " + process2);
                    return -1;
                }
                printWriter.println("Waiting for dump to finish...");
                pw.flush();
                try {
                    latch2.await();
                    return 0;
                } catch (InterruptedException e) {
                    InterruptedException interruptedException = e;
                    err.println("Caught InterruptedException");
                    return 0;
                }
            } else if (opt.equals("--user")) {
                userId = UserHandle.parseUserArg(getNextArgRequired());
                if (userId == -1) {
                    err.println("Error: Can't dump heap with user 'all'");
                    return -1;
                }
            } else if (opt.equals("-n")) {
                managed = false;
            } else if (opt.equals("-g")) {
                runGc = true;
            } else if (opt.equals("-m")) {
                managed = false;
                mallocInfo = true;
            } else {
                err.println("Error: Unknown option: " + opt);
                return -1;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int runSetDebugApp(PrintWriter pw) throws RemoteException {
        boolean wait = false;
        boolean persistent = false;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                this.mInterface.setDebugApp(getNextArgRequired(), wait, persistent);
                return 0;
            } else if (opt.equals("-w")) {
                wait = true;
            } else if (opt.equals("--persistent")) {
                persistent = true;
            } else {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Error: Unknown option: " + opt);
                return -1;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int runSetAgentApp(PrintWriter pw) throws RemoteException {
        this.mInterface.setAgentApp(getNextArgRequired(), getNextArg());
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runClearDebugApp(PrintWriter pw) throws RemoteException {
        this.mInterface.setDebugApp((String) null, false, true);
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runSetWatchHeap(PrintWriter pw) throws RemoteException {
        String proc = getNextArgRequired();
        String str = proc;
        this.mInterface.setDumpHeapDebugLimit(str, 0, Long.parseLong(getNextArgRequired()), (String) null);
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runClearWatchHeap(PrintWriter pw) throws RemoteException {
        this.mInterface.setDumpHeapDebugLimit(getNextArgRequired(), 0, -1, (String) null);
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runBugReport(PrintWriter pw) throws RemoteException {
        int bugreportType = 0;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                this.mInterface.requestBugReport(bugreportType);
                pw.println("Your lovely bug report is being created; please be patient.");
                return 0;
            } else if (opt.equals("--progress")) {
                bugreportType = 1;
            } else if (opt.equals("--telephony")) {
                bugreportType = 4;
            } else {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Error: Unknown option: " + opt);
                return -1;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int runForceStop(PrintWriter pw) throws RemoteException {
        int userId = -1;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                this.mInterface.forceStopPackage(getNextArgRequired(), userId);
                return 0;
            } else if (opt.equals("--user")) {
                userId = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Error: Unknown option: " + opt);
                return -1;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int runCrash(PrintWriter pw) throws RemoteException {
        String packageName;
        int pid;
        int userId = -1;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                String arg = getNextArgRequired();
                try {
                    pid = Integer.parseInt(arg);
                    packageName = null;
                } catch (NumberFormatException e) {
                    pid = -1;
                    packageName = arg;
                }
                this.mInterface.crashApplication(-1, pid, packageName, userId, "shell-induced crash");
                return 0;
            } else if (opt.equals("--user")) {
                userId = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                getErrPrintWriter().println("Error: Unknown option: " + opt);
                return -1;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int runKill(PrintWriter pw) throws RemoteException {
        int userId = -1;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                this.mInterface.killBackgroundProcesses(getNextArgRequired(), userId);
                return 0;
            } else if (opt.equals("--user")) {
                userId = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Error: Unknown option: " + opt);
                return -1;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int runKillAll(PrintWriter pw) throws RemoteException {
        this.mInterface.killAllBackgroundProcesses();
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runMakeIdle(PrintWriter pw) throws RemoteException {
        int userId = -1;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                this.mInterface.makePackageIdle(getNextArgRequired(), userId);
                return 0;
            } else if (opt.equals("--user")) {
                userId = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Error: Unknown option: " + opt);
                return -1;
            }
        }
    }

    static final class MyActivityController extends IActivityController.Stub {
        static final int RESULT_ANR_DIALOG = 0;
        static final int RESULT_ANR_KILL = 1;
        static final int RESULT_ANR_WAIT = 1;
        static final int RESULT_CRASH_DIALOG = 0;
        static final int RESULT_CRASH_KILL = 1;
        static final int RESULT_DEFAULT = 0;
        static final int RESULT_EARLY_ANR_CONTINUE = 0;
        static final int RESULT_EARLY_ANR_KILL = 1;
        static final int STATE_ANR = 3;
        static final int STATE_CRASHED = 1;
        static final int STATE_EARLY_ANR = 2;
        static final int STATE_NORMAL = 0;
        final String mGdbPort;
        Process mGdbProcess;
        Thread mGdbThread;
        boolean mGotGdbPrint;
        final InputStream mInput;
        final IActivityManager mInterface;
        final boolean mMonkey;
        final PrintWriter mPw;
        int mResult;
        int mState;

        MyActivityController(IActivityManager iam, PrintWriter pw, InputStream input, String gdbPort, boolean monkey) {
            this.mInterface = iam;
            this.mPw = pw;
            this.mInput = input;
            this.mGdbPort = gdbPort;
            this.mMonkey = monkey;
        }

        public boolean activityResuming(String pkg) {
            synchronized (this) {
                PrintWriter printWriter = this.mPw;
                printWriter.println("** Activity resuming: " + pkg);
                this.mPw.flush();
            }
            return true;
        }

        public boolean activityStarting(Intent intent, String pkg) {
            synchronized (this) {
                PrintWriter printWriter = this.mPw;
                printWriter.println("** Activity starting: " + pkg);
                this.mPw.flush();
            }
            return true;
        }

        public boolean appCrashed(String processName, int pid, String shortMsg, String longMsg, long timeMillis, String stackTrace) {
            boolean z;
            synchronized (this) {
                this.mPw.println("** ERROR: PROCESS CRASHED");
                PrintWriter printWriter = this.mPw;
                printWriter.println("processName: " + processName);
                PrintWriter printWriter2 = this.mPw;
                printWriter2.println("processPid: " + pid);
                PrintWriter printWriter3 = this.mPw;
                printWriter3.println("shortMsg: " + shortMsg);
                PrintWriter printWriter4 = this.mPw;
                printWriter4.println("longMsg: " + longMsg);
                PrintWriter printWriter5 = this.mPw;
                printWriter5.println("timeMillis: " + timeMillis);
                this.mPw.println("stack:");
                this.mPw.print(stackTrace);
                this.mPw.println("#");
                this.mPw.flush();
                z = true;
                if (waitControllerLocked(pid, 1) == 1) {
                    z = false;
                }
            }
            return z;
        }

        public int appEarlyNotResponding(String processName, int pid, String annotation) {
            synchronized (this) {
                this.mPw.println("** ERROR: EARLY PROCESS NOT RESPONDING");
                PrintWriter printWriter = this.mPw;
                printWriter.println("processName: " + processName);
                PrintWriter printWriter2 = this.mPw;
                printWriter2.println("processPid: " + pid);
                PrintWriter printWriter3 = this.mPw;
                printWriter3.println("annotation: " + annotation);
                this.mPw.flush();
                if (waitControllerLocked(pid, 2) == 1) {
                    return -1;
                }
                return 0;
            }
        }

        public int appNotResponding(String processName, int pid, String processStats) {
            synchronized (this) {
                this.mPw.println("** ERROR: PROCESS NOT RESPONDING");
                PrintWriter printWriter = this.mPw;
                printWriter.println("processName: " + processName);
                PrintWriter printWriter2 = this.mPw;
                printWriter2.println("processPid: " + pid);
                this.mPw.println("processStats:");
                this.mPw.print(processStats);
                this.mPw.println("#");
                this.mPw.flush();
                int result = waitControllerLocked(pid, 3);
                if (result == 1) {
                    return -1;
                }
                if (result == 1) {
                    return 1;
                }
                return 0;
            }
        }

        public int systemNotResponding(String message) {
            synchronized (this) {
                this.mPw.println("** ERROR: PROCESS NOT RESPONDING");
                PrintWriter printWriter = this.mPw;
                printWriter.println("message: " + message);
                this.mPw.println("#");
                this.mPw.println("Allowing system to die.");
                this.mPw.flush();
            }
            return -1;
        }

        /* access modifiers changed from: package-private */
        public void killGdbLocked() {
            this.mGotGdbPrint = false;
            if (this.mGdbProcess != null) {
                this.mPw.println("Stopping gdbserver");
                this.mPw.flush();
                this.mGdbProcess.destroy();
                this.mGdbProcess = null;
            }
            Thread thread = this.mGdbThread;
            if (thread != null) {
                thread.interrupt();
                this.mGdbThread = null;
            }
        }

        /* access modifiers changed from: package-private */
        public int waitControllerLocked(int pid, int state) {
            if (this.mGdbPort != null) {
                killGdbLocked();
                try {
                    PrintWriter printWriter = this.mPw;
                    printWriter.println("Starting gdbserver on port " + this.mGdbPort);
                    this.mPw.println("Do the following:");
                    PrintWriter printWriter2 = this.mPw;
                    printWriter2.println("  adb forward tcp:" + this.mGdbPort + " tcp:" + this.mGdbPort);
                    PrintWriter printWriter3 = this.mPw;
                    StringBuilder sb = new StringBuilder();
                    sb.append("  gdbclient app_process :");
                    sb.append(this.mGdbPort);
                    printWriter3.println(sb.toString());
                    this.mPw.flush();
                    Runtime runtime = Runtime.getRuntime();
                    this.mGdbProcess = runtime.exec(new String[]{"gdbserver", ":" + this.mGdbPort, "--attach", Integer.toString(pid)});
                    final InputStreamReader converter = new InputStreamReader(this.mGdbProcess.getInputStream());
                    this.mGdbThread = new Thread() {
                        /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
                            r2 = r0.readLine();
                         */
                        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0025, code lost:
                            if (r2 != null) goto L_0x0028;
                         */
                        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0027, code lost:
                            return;
                         */
                        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0028, code lost:
                            r3 = r6.this$0.mPw;
                            r3.println("GDB: " + r2);
                            r6.this$0.mPw.flush();
                         */
                        /* JADX WARNING: Code restructure failed: missing block: B:19:0x004b, code lost:
                            return;
                         */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public void run() {
                            /*
                                r6 = this;
                                java.io.BufferedReader r0 = new java.io.BufferedReader
                                java.io.InputStreamReader r1 = r0
                                r0.<init>(r1)
                                r1 = 0
                            L_0x0008:
                                com.android.server.am.ActivityManagerShellCommand$MyActivityController r2 = com.android.server.am.ActivityManagerShellCommand.MyActivityController.this
                                monitor-enter(r2)
                                com.android.server.am.ActivityManagerShellCommand$MyActivityController r3 = com.android.server.am.ActivityManagerShellCommand.MyActivityController.this     // Catch:{ all -> 0x004c }
                                java.lang.Thread r3 = r3.mGdbThread     // Catch:{ all -> 0x004c }
                                if (r3 != 0) goto L_0x0013
                                monitor-exit(r2)     // Catch:{ all -> 0x004c }
                                return
                            L_0x0013:
                                r3 = 2
                                if (r1 != r3) goto L_0x0020
                                com.android.server.am.ActivityManagerShellCommand$MyActivityController r3 = com.android.server.am.ActivityManagerShellCommand.MyActivityController.this     // Catch:{ all -> 0x004c }
                                r4 = 1
                                r3.mGotGdbPrint = r4     // Catch:{ all -> 0x004c }
                                com.android.server.am.ActivityManagerShellCommand$MyActivityController r3 = com.android.server.am.ActivityManagerShellCommand.MyActivityController.this     // Catch:{ all -> 0x004c }
                                r3.notifyAll()     // Catch:{ all -> 0x004c }
                            L_0x0020:
                                monitor-exit(r2)     // Catch:{ all -> 0x004c }
                                java.lang.String r2 = r0.readLine()     // Catch:{ IOException -> 0x004a }
                                if (r2 != 0) goto L_0x0028
                                return
                            L_0x0028:
                                com.android.server.am.ActivityManagerShellCommand$MyActivityController r3 = com.android.server.am.ActivityManagerShellCommand.MyActivityController.this     // Catch:{ IOException -> 0x004a }
                                java.io.PrintWriter r3 = r3.mPw     // Catch:{ IOException -> 0x004a }
                                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x004a }
                                r4.<init>()     // Catch:{ IOException -> 0x004a }
                                java.lang.String r5 = "GDB: "
                                r4.append(r5)     // Catch:{ IOException -> 0x004a }
                                r4.append(r2)     // Catch:{ IOException -> 0x004a }
                                java.lang.String r4 = r4.toString()     // Catch:{ IOException -> 0x004a }
                                r3.println(r4)     // Catch:{ IOException -> 0x004a }
                                com.android.server.am.ActivityManagerShellCommand$MyActivityController r3 = com.android.server.am.ActivityManagerShellCommand.MyActivityController.this     // Catch:{ IOException -> 0x004a }
                                java.io.PrintWriter r3 = r3.mPw     // Catch:{ IOException -> 0x004a }
                                r3.flush()     // Catch:{ IOException -> 0x004a }
                                int r1 = r1 + 1
                                goto L_0x0008
                            L_0x004a:
                                r2 = move-exception
                                return
                            L_0x004c:
                                r3 = move-exception
                                monitor-exit(r2)     // Catch:{ all -> 0x004c }
                                throw r3
                            */
                            throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActivityManagerShellCommand.MyActivityController.AnonymousClass1.run():void");
                        }
                    };
                    this.mGdbThread.start();
                    try {
                        wait(500);
                    } catch (InterruptedException e) {
                    }
                } catch (IOException e2) {
                    PrintWriter printWriter4 = this.mPw;
                    printWriter4.println("Failure starting gdbserver: " + e2);
                    this.mPw.flush();
                    killGdbLocked();
                }
            }
            this.mState = state;
            this.mPw.println("");
            printMessageForState();
            this.mPw.flush();
            while (this.mState != 0) {
                try {
                    wait();
                } catch (InterruptedException e3) {
                }
            }
            killGdbLocked();
            return this.mResult;
        }

        /* access modifiers changed from: package-private */
        public void resumeController(int result) {
            synchronized (this) {
                this.mState = 0;
                this.mResult = result;
                notifyAll();
            }
        }

        /* access modifiers changed from: package-private */
        public void printMessageForState() {
            int i = this.mState;
            if (i == 0) {
                this.mPw.println("Monitoring activity manager...  available commands:");
            } else if (i == 1) {
                this.mPw.println("Waiting after crash...  available commands:");
                this.mPw.println("(c)ontinue: show crash dialog");
                this.mPw.println("(k)ill: immediately kill app");
            } else if (i == 2) {
                this.mPw.println("Waiting after early ANR...  available commands:");
                this.mPw.println("(c)ontinue: standard ANR processing");
                this.mPw.println("(k)ill: immediately kill app");
            } else if (i == 3) {
                this.mPw.println("Waiting after ANR...  available commands:");
                this.mPw.println("(c)ontinue: show ANR dialog");
                this.mPw.println("(k)ill: immediately kill app");
                this.mPw.println("(w)ait: wait some more");
            }
            this.mPw.println("(q)uit: finish monitoring");
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        /* access modifiers changed from: package-private */
        public void run() throws RemoteException {
            try {
                printMessageForState();
                this.mPw.flush();
                this.mInterface.setActivityController(this, this.mMonkey);
                this.mState = 0;
                BufferedReader in = new BufferedReader(new InputStreamReader(this.mInput));
                while (true) {
                    String readLine = in.readLine();
                    String line = readLine;
                    if (readLine == null) {
                        break;
                    }
                    boolean addNewline = true;
                    if (line.length() <= 0) {
                        addNewline = false;
                    } else if ("q".equals(line)) {
                        break;
                    } else if ("quit".equals(line)) {
                        break;
                    } else if (this.mState == 1) {
                        if (!"c".equals(line)) {
                            if (!"continue".equals(line)) {
                                if (!"k".equals(line)) {
                                    if (!"kill".equals(line)) {
                                        PrintWriter printWriter = this.mPw;
                                        printWriter.println("Invalid command: " + line);
                                    }
                                }
                                resumeController(1);
                            }
                        }
                        resumeController(0);
                    } else if (this.mState == 3) {
                        if (!"c".equals(line)) {
                            if (!"continue".equals(line)) {
                                if (!"k".equals(line)) {
                                    if (!"kill".equals(line)) {
                                        if (!"w".equals(line)) {
                                            if (!"wait".equals(line)) {
                                                PrintWriter printWriter2 = this.mPw;
                                                printWriter2.println("Invalid command: " + line);
                                            }
                                        }
                                        resumeController(1);
                                    }
                                }
                                resumeController(1);
                            }
                        }
                        resumeController(0);
                    } else if (this.mState == 2) {
                        if (!"c".equals(line)) {
                            if (!"continue".equals(line)) {
                                if (!"k".equals(line)) {
                                    if (!"kill".equals(line)) {
                                        PrintWriter printWriter3 = this.mPw;
                                        printWriter3.println("Invalid command: " + line);
                                    }
                                }
                                resumeController(1);
                            }
                        }
                        resumeController(0);
                    } else {
                        PrintWriter printWriter4 = this.mPw;
                        printWriter4.println("Invalid command: " + line);
                    }
                    synchronized (this) {
                        if (addNewline) {
                            this.mPw.println("");
                        }
                        printMessageForState();
                        this.mPw.flush();
                    }
                }
                resumeController(0);
            } catch (IOException e) {
                try {
                    e.printStackTrace(this.mPw);
                    this.mPw.flush();
                } catch (Throwable th) {
                    this.mInterface.setActivityController((IActivityController) null, this.mMonkey);
                    throw th;
                }
            }
            this.mInterface.setActivityController((IActivityController) null, this.mMonkey);
        }
    }

    /* access modifiers changed from: package-private */
    public int runMonitor(PrintWriter pw) throws RemoteException {
        String gdbPort = null;
        boolean monkey = false;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                new MyActivityController(this.mInterface, pw, getRawInputStream(), gdbPort, monkey).run();
                return 0;
            } else if (opt.equals("--gdb")) {
                gdbPort = getNextArgRequired();
            } else if (opt.equals("-m")) {
                monkey = true;
            } else {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Error: Unknown option: " + opt);
                return -1;
            }
        }
    }

    static final class MyUidObserver extends IUidObserver.Stub implements ActivityManagerService.OomAdjObserver {
        static final int STATE_NORMAL = 0;
        final InputStream mInput;
        final IActivityManager mInterface;
        final ActivityManagerService mInternal;
        final PrintWriter mPw;
        int mState;
        final int mUid;

        MyUidObserver(ActivityManagerService service, PrintWriter pw, InputStream input, int uid) {
            this.mInterface = service;
            this.mInternal = service;
            this.mPw = pw;
            this.mInput = input;
            this.mUid = uid;
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public void onUidStateChanged(int uid, int procState, long procStateSeq) throws RemoteException {
            synchronized (this) {
                StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
                try {
                    this.mPw.print(uid);
                    this.mPw.print(" procstate ");
                    this.mPw.print(ProcessList.makeProcStateString(procState));
                    this.mPw.print(" seq ");
                    this.mPw.println(procStateSeq);
                    this.mPw.flush();
                } finally {
                    StrictMode.setThreadPolicy(oldPolicy);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public void onUidGone(int uid, boolean disabled) throws RemoteException {
            synchronized (this) {
                StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
                try {
                    this.mPw.print(uid);
                    this.mPw.print(" gone");
                    if (disabled) {
                        this.mPw.print(" disabled");
                    }
                    this.mPw.println();
                    this.mPw.flush();
                } finally {
                    StrictMode.setThreadPolicy(oldPolicy);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public void onUidActive(int uid) throws RemoteException {
            synchronized (this) {
                StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
                try {
                    this.mPw.print(uid);
                    this.mPw.println(" active");
                    this.mPw.flush();
                } finally {
                    StrictMode.setThreadPolicy(oldPolicy);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public void onUidIdle(int uid, boolean disabled) throws RemoteException {
            synchronized (this) {
                StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
                try {
                    this.mPw.print(uid);
                    this.mPw.print(" idle");
                    if (disabled) {
                        this.mPw.print(" disabled");
                    }
                    this.mPw.println();
                    this.mPw.flush();
                } finally {
                    StrictMode.setThreadPolicy(oldPolicy);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public void onUidCachedChanged(int uid, boolean cached) throws RemoteException {
            synchronized (this) {
                StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
                try {
                    this.mPw.print(uid);
                    this.mPw.println(cached ? " cached" : " uncached");
                    this.mPw.flush();
                } finally {
                    StrictMode.setThreadPolicy(oldPolicy);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public void onOomAdjMessage(String msg) {
            synchronized (this) {
                StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
                try {
                    this.mPw.print("# ");
                    this.mPw.println(msg);
                    this.mPw.flush();
                } finally {
                    StrictMode.setThreadPolicy(oldPolicy);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void printMessageForState() {
            if (this.mState == 0) {
                this.mPw.println("Watching uid states...  available commands:");
            }
            this.mPw.println("(q)uit: finish watching");
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x007d, code lost:
            if (r7.mUid >= 0) goto L_0x0091;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:37:0x009c, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() throws android.os.RemoteException {
            /*
                r7 = this;
                r7.printMessageForState()     // Catch:{ IOException -> 0x0082 }
                java.io.PrintWriter r0 = r7.mPw     // Catch:{ IOException -> 0x0082 }
                r0.flush()     // Catch:{ IOException -> 0x0082 }
                android.app.IActivityManager r0 = r7.mInterface     // Catch:{ IOException -> 0x0082 }
                r1 = 31
                r2 = -1
                r3 = 0
                r0.registerUidObserver(r7, r1, r2, r3)     // Catch:{ IOException -> 0x0082 }
                int r0 = r7.mUid     // Catch:{ IOException -> 0x0082 }
                if (r0 < 0) goto L_0x001c
                com.android.server.am.ActivityManagerService r0 = r7.mInternal     // Catch:{ IOException -> 0x0082 }
                int r1 = r7.mUid     // Catch:{ IOException -> 0x0082 }
                r0.setOomAdjObserver(r1, r7)     // Catch:{ IOException -> 0x0082 }
            L_0x001c:
                r0 = 0
                r7.mState = r0     // Catch:{ IOException -> 0x0082 }
                java.io.InputStreamReader r0 = new java.io.InputStreamReader     // Catch:{ IOException -> 0x0082 }
                java.io.InputStream r1 = r7.mInput     // Catch:{ IOException -> 0x0082 }
                r0.<init>(r1)     // Catch:{ IOException -> 0x0082 }
                java.io.BufferedReader r1 = new java.io.BufferedReader     // Catch:{ IOException -> 0x0082 }
                r1.<init>(r0)     // Catch:{ IOException -> 0x0082 }
            L_0x002b:
                java.lang.String r2 = r1.readLine()     // Catch:{ IOException -> 0x0082 }
                r3 = r2
                if (r2 == 0) goto L_0x007b
                r2 = 1
                int r4 = r3.length()     // Catch:{ IOException -> 0x0082 }
                if (r4 > 0) goto L_0x003b
                r2 = 0
                goto L_0x0064
            L_0x003b:
                java.lang.String r4 = "q"
                boolean r4 = r4.equals(r3)     // Catch:{ IOException -> 0x0082 }
                if (r4 != 0) goto L_0x007b
                java.lang.String r4 = "quit"
                boolean r4 = r4.equals(r3)     // Catch:{ IOException -> 0x0082 }
                if (r4 == 0) goto L_0x004e
                goto L_0x007b
            L_0x004e:
                java.io.PrintWriter r4 = r7.mPw     // Catch:{ IOException -> 0x0082 }
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0082 }
                r5.<init>()     // Catch:{ IOException -> 0x0082 }
                java.lang.String r6 = "Invalid command: "
                r5.append(r6)     // Catch:{ IOException -> 0x0082 }
                r5.append(r3)     // Catch:{ IOException -> 0x0082 }
                java.lang.String r5 = r5.toString()     // Catch:{ IOException -> 0x0082 }
                r4.println(r5)     // Catch:{ IOException -> 0x0082 }
            L_0x0064:
                monitor-enter(r7)     // Catch:{ IOException -> 0x0082 }
                if (r2 == 0) goto L_0x006e
                java.io.PrintWriter r4 = r7.mPw     // Catch:{ all -> 0x0078 }
                java.lang.String r5 = ""
                r4.println(r5)     // Catch:{ all -> 0x0078 }
            L_0x006e:
                r7.printMessageForState()     // Catch:{ all -> 0x0078 }
                java.io.PrintWriter r4 = r7.mPw     // Catch:{ all -> 0x0078 }
                r4.flush()     // Catch:{ all -> 0x0078 }
                monitor-exit(r7)     // Catch:{ all -> 0x0078 }
                goto L_0x002b
            L_0x0078:
                r4 = move-exception
                monitor-exit(r7)     // Catch:{ all -> 0x0078 }
                throw r4     // Catch:{ IOException -> 0x0082 }
            L_0x007b:
                int r0 = r7.mUid
                if (r0 < 0) goto L_0x0096
                goto L_0x0091
            L_0x0080:
                r0 = move-exception
                goto L_0x009d
            L_0x0082:
                r0 = move-exception
                java.io.PrintWriter r1 = r7.mPw     // Catch:{ all -> 0x0080 }
                r0.printStackTrace(r1)     // Catch:{ all -> 0x0080 }
                java.io.PrintWriter r1 = r7.mPw     // Catch:{ all -> 0x0080 }
                r1.flush()     // Catch:{ all -> 0x0080 }
                int r0 = r7.mUid
                if (r0 < 0) goto L_0x0096
            L_0x0091:
                com.android.server.am.ActivityManagerService r0 = r7.mInternal
                r0.clearOomAdjObserver()
            L_0x0096:
                android.app.IActivityManager r0 = r7.mInterface
                r0.unregisterUidObserver(r7)
                return
            L_0x009d:
                int r1 = r7.mUid
                if (r1 < 0) goto L_0x00a6
                com.android.server.am.ActivityManagerService r1 = r7.mInternal
                r1.clearOomAdjObserver()
            L_0x00a6:
                android.app.IActivityManager r1 = r7.mInterface
                r1.unregisterUidObserver(r7)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActivityManagerShellCommand.MyUidObserver.run():void");
        }
    }

    /* access modifiers changed from: package-private */
    public int runWatchUids(PrintWriter pw) throws RemoteException {
        int uid = -1;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                new MyUidObserver(this.mInternal, pw, getRawInputStream(), uid).run();
                return 0;
            } else if (opt.equals("--oom")) {
                uid = Integer.parseInt(getNextArgRequired());
            } else {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Error: Unknown option: " + opt);
                return -1;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int runHang(PrintWriter pw) throws RemoteException {
        boolean allowRestart = false;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                pw.println("Hanging the system...");
                pw.flush();
                this.mInterface.hang(new Binder(), allowRestart);
                return 0;
            } else if (opt.equals("--allow-restart")) {
                allowRestart = true;
            } else {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Error: Unknown option: " + opt);
                return -1;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int runRestart(PrintWriter pw) throws RemoteException {
        String nextOption = getNextOption();
        String opt = nextOption;
        if (nextOption != null) {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: Unknown option: " + opt);
            return -1;
        }
        pw.println("Restart the system...");
        pw.flush();
        this.mInterface.restart();
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runIdleMaintenance(PrintWriter pw) throws RemoteException {
        String nextOption = getNextOption();
        String opt = nextOption;
        if (nextOption != null) {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: Unknown option: " + opt);
            return -1;
        }
        pw.println("Performing idle maintenance...");
        this.mInterface.sendIdleJobTrigger();
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runScreenCompat(PrintWriter pw) throws RemoteException {
        boolean enabled;
        int i;
        String mode = getNextArgRequired();
        if ("on".equals(mode)) {
            enabled = true;
        } else if ("off".equals(mode)) {
            enabled = false;
        } else {
            getErrPrintWriter().println("Error: enabled mode must be 'on' or 'off' at " + mode);
            return -1;
        }
        String packageName = getNextArgRequired();
        do {
            try {
                IActivityManager iActivityManager = this.mInterface;
                if (enabled) {
                    i = 1;
                } else {
                    i = 0;
                }
                iActivityManager.setPackageScreenCompatMode(packageName, i);
            } catch (RemoteException e) {
            }
            packageName = getNextArg();
        } while (packageName != null);
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runPackageImportance(PrintWriter pw) throws RemoteException {
        pw.println(ActivityManager.RunningAppProcessInfo.procStateToImportance(this.mInterface.getPackageProcessState(getNextArgRequired(), "com.android.shell")));
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runToUri(PrintWriter pw, int flags) throws RemoteException {
        try {
            pw.println(makeIntent(-2).toUri(flags));
            return 0;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /* access modifiers changed from: package-private */
    public int runSwitchUser(PrintWriter pw) throws RemoteException {
        int userSwitchable = ((UserManager) this.mInternal.mContext.getSystemService(UserManager.class)).getUserSwitchability();
        if (userSwitchable != 0) {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: " + userSwitchable);
            return -1;
        }
        this.mInterface.switchUser(Integer.parseInt(getNextArgRequired()));
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runGetCurrentUser(PrintWriter pw) throws RemoteException {
        pw.println(((UserInfo) Preconditions.checkNotNull(this.mInterface.getCurrentUser(), "Current user not set")).id);
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runStartUser(PrintWriter pw) throws RemoteException {
        boolean wait = false;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                int userId = Integer.parseInt(getNextArgRequired());
                ProgressWaiter waiter = null;
                if (wait) {
                    waiter = new ProgressWaiter();
                }
                boolean success = this.mInterface.startUserInBackgroundWithListener(userId, waiter);
                if (wait && success) {
                    success = waiter.waitForFinish(JobStatus.DEFAULT_TRIGGER_MAX_DELAY);
                }
                if (success) {
                    pw.println("Success: user started");
                    return 0;
                }
                getErrPrintWriter().println("Error: could not start user");
                return 0;
            } else if ("-w".equals(opt)) {
                wait = true;
            } else {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Error: unknown option: " + opt);
                return -1;
            }
        }
    }

    private static byte[] argToBytes(String arg) {
        if (arg.equals("!")) {
            return null;
        }
        return HexDump.hexStringToByteArray(arg);
    }

    /* access modifiers changed from: package-private */
    public int runUnlockUser(PrintWriter pw) throws RemoteException {
        if (this.mInterface.unlockUser(Integer.parseInt(getNextArgRequired()), argToBytes(getNextArgRequired()), argToBytes(getNextArgRequired()), (IProgressListener) null)) {
            pw.println("Success: user unlocked");
            return 0;
        }
        getErrPrintWriter().println("Error: could not unlock user");
        return 0;
    }

    static final class StopUserCallback extends IStopUserCallback.Stub {
        private boolean mFinished = false;

        StopUserCallback() {
        }

        public synchronized void waitForFinish() {
            while (!this.mFinished) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        public synchronized void userStopped(int userId) {
            this.mFinished = true;
            notifyAll();
        }

        public synchronized void userStopAborted(int userId) {
            this.mFinished = true;
            notifyAll();
        }
    }

    /* access modifiers changed from: package-private */
    public int runStopUser(PrintWriter pw) throws RemoteException {
        boolean wait = false;
        boolean force = false;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                int user = Integer.parseInt(getNextArgRequired());
                StopUserCallback callback = wait ? new StopUserCallback() : null;
                int res = this.mInterface.stopUser(user, force, callback);
                if (res != 0) {
                    String txt = "";
                    if (res == -4) {
                        txt = " (Can't stop user " + user + " - one of its related users can't be stopped)";
                    } else if (res == -3) {
                        txt = " (System user cannot be stopped)";
                    } else if (res == -2) {
                        txt = " (Can't stop current user)";
                    } else if (res == -1) {
                        txt = " (Unknown user " + user + ")";
                    }
                    getErrPrintWriter().println("Switch failed: " + res + txt);
                    return -1;
                } else if (callback == null) {
                    return 0;
                } else {
                    callback.waitForFinish();
                    return 0;
                }
            } else if ("-w".equals(opt)) {
                wait = true;
            } else if ("-f".equals(opt)) {
                force = true;
            } else {
                getErrPrintWriter().println("Error: unknown option: " + opt);
                return -1;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int runIsUserStopped(PrintWriter pw) {
        pw.println(this.mInternal.isUserStopped(UserHandle.parseUserArg(getNextArgRequired())));
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runGetStartedUserState(PrintWriter pw) throws RemoteException {
        this.mInternal.enforceCallingPermission("android.permission.DUMP", "runGetStartedUserState()");
        int userId = Integer.parseInt(getNextArgRequired());
        try {
            pw.println(this.mInternal.getStartedUserState(userId));
            return 0;
        } catch (NullPointerException e) {
            pw.println("User is not started: " + userId);
            return 0;
        }
    }

    /* access modifiers changed from: package-private */
    public int runTrackAssociations(PrintWriter pw) {
        this.mInternal.enforceCallingPermission("android.permission.SET_ACTIVITY_WATCHER", "runTrackAssociations()");
        synchronized (this.mInternal) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                if (!this.mInternal.mTrackingAssociations) {
                    this.mInternal.mTrackingAssociations = true;
                    pw.println("Association tracking started.");
                } else {
                    pw.println("Association tracking already enabled.");
                }
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runUntrackAssociations(PrintWriter pw) {
        this.mInternal.enforceCallingPermission("android.permission.SET_ACTIVITY_WATCHER", "runUntrackAssociations()");
        synchronized (this.mInternal) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                if (this.mInternal.mTrackingAssociations) {
                    this.mInternal.mTrackingAssociations = false;
                    this.mInternal.mAssociations.clear();
                    pw.println("Association tracking stopped.");
                } else {
                    pw.println("Association tracking not running.");
                }
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int getUidState(PrintWriter pw) throws RemoteException {
        this.mInternal.enforceCallingPermission("android.permission.DUMP", "getUidState()");
        int state = this.mInternal.getUidState(Integer.parseInt(getNextArgRequired()));
        pw.print(state);
        pw.print(" (");
        pw.printf(DebugUtils.valueToString(ActivityManager.class, "PROCESS_STATE_", state), new Object[0]);
        pw.println(")");
        return 0;
    }

    private List<Configuration> getRecentConfigurations(int days) {
        IUsageStatsManager usm = IUsageStatsManager.Stub.asInterface(ServiceManager.getService("usagestats"));
        long now = System.currentTimeMillis();
        try {
            ParceledListSlice<ConfigurationStats> configStatsSlice = usm.queryConfigurationStats(4, now - ((long) ((((days * 24) * 60) * 60) * 1000)), now, "com.android.shell");
            if (configStatsSlice == null) {
                return Collections.emptyList();
            }
            final ArrayMap<Configuration, Integer> recentConfigs = new ArrayMap<>();
            List<ConfigurationStats> configStatsList = configStatsSlice.getList();
            int configStatsListSize = configStatsList.size();
            for (int i = 0; i < configStatsListSize; i++) {
                ConfigurationStats stats = configStatsList.get(i);
                int indexOfKey = recentConfigs.indexOfKey(stats.getConfiguration());
                if (indexOfKey < 0) {
                    recentConfigs.put(stats.getConfiguration(), Integer.valueOf(stats.getActivationCount()));
                } else {
                    recentConfigs.setValueAt(indexOfKey, Integer.valueOf(recentConfigs.valueAt(indexOfKey).intValue() + stats.getActivationCount()));
                }
            }
            Comparator<Configuration> comparator = new Comparator<Configuration>() {
                public int compare(Configuration a, Configuration b) {
                    return ((Integer) recentConfigs.get(b)).compareTo((Integer) recentConfigs.get(a));
                }
            };
            ArrayList<Configuration> configs = new ArrayList<>(recentConfigs.size());
            configs.addAll(recentConfigs.keySet());
            Collections.sort(configs, comparator);
            return configs;
        } catch (RemoteException e) {
            return Collections.emptyList();
        }
    }

    private static void addExtensionsForConfig(EGL10 egl, EGLDisplay display, EGLConfig config, int[] surfaceSize, int[] contextAttribs, Set<String> glExtensions) {
        EGLContext context = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, contextAttribs);
        if (context != EGL10.EGL_NO_CONTEXT) {
            EGLSurface surface = egl.eglCreatePbufferSurface(display, config, surfaceSize);
            if (surface == EGL10.EGL_NO_SURFACE) {
                egl.eglDestroyContext(display, context);
                return;
            }
            egl.eglMakeCurrent(display, surface, surface, context);
            String extensionList = GLES10.glGetString(7939);
            if (!TextUtils.isEmpty(extensionList)) {
                for (String extension : extensionList.split(" ")) {
                    glExtensions.add(extension);
                }
            }
            egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            egl.eglDestroySurface(display, surface);
            egl.eglDestroyContext(display, context);
        }
    }

    /* access modifiers changed from: package-private */
    public Set<String> getGlExtensionsFromDriver() {
        int i;
        int i2;
        char c;
        EGLConfig[] configs;
        int[] attrib;
        int[] numConfigs;
        HashSet hashSet = new HashSet();
        EGL10 egl = (EGL10) EGLContext.getEGL();
        if (egl == null) {
            getErrPrintWriter().println("Warning: couldn't get EGL");
            return hashSet;
        }
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        egl.eglInitialize(display, new int[2]);
        int i3 = 1;
        int[] numConfigs2 = new int[1];
        char c2 = 0;
        if (!egl.eglGetConfigs(display, (EGLConfig[]) null, 0, numConfigs2)) {
            getErrPrintWriter().println("Warning: couldn't get EGL config count");
            return hashSet;
        }
        EGLConfig[] configs2 = new EGLConfig[numConfigs2[0]];
        if (!egl.eglGetConfigs(display, configs2, numConfigs2[0], numConfigs2)) {
            getErrPrintWriter().println("Warning: couldn't get EGL configs");
            return hashSet;
        }
        int[] surfaceSize = {12375, 1, 12374, 1, 12344};
        int[] gles2 = {12440, 2, 12344};
        int[] attrib2 = new int[1];
        int i4 = 0;
        while (i4 < numConfigs2[c2]) {
            egl.eglGetConfigAttrib(display, configs2[i4], 12327, attrib2);
            if (attrib2[c2] == 12368) {
                i = i4;
                attrib = attrib2;
                configs = configs2;
                c = c2;
                numConfigs = numConfigs2;
                i2 = i3;
            } else {
                egl.eglGetConfigAttrib(display, configs2[i4], 12339, attrib2);
                if ((attrib2[c2] & i3) == 0) {
                    i = i4;
                    attrib = attrib2;
                    configs = configs2;
                    c = c2;
                    numConfigs = numConfigs2;
                    i2 = i3;
                } else {
                    egl.eglGetConfigAttrib(display, configs2[i4], 12352, attrib2);
                    if ((attrib2[c2] & i3) != 0) {
                        i = i4;
                        addExtensionsForConfig(egl, display, configs2[i4], surfaceSize, (int[]) null, hashSet);
                    } else {
                        i = i4;
                    }
                    if ((attrib2[c2] & 4) != 0) {
                        attrib = attrib2;
                        configs = configs2;
                        c = c2;
                        numConfigs = numConfigs2;
                        i2 = i3;
                        addExtensionsForConfig(egl, display, configs2[i], surfaceSize, gles2, hashSet);
                    } else {
                        attrib = attrib2;
                        configs = configs2;
                        c = c2;
                        numConfigs = numConfigs2;
                        i2 = i3;
                    }
                }
            }
            numConfigs2 = numConfigs;
            configs2 = configs;
            c2 = c;
            i3 = i2;
            i4 = i + 1;
            attrib2 = attrib;
        }
        egl.eglTerminate(display);
        return hashSet;
    }

    private void writeDeviceConfig(ProtoOutputStream protoOutputStream, long fieldId, PrintWriter pw, Configuration config, DisplayMetrics displayMetrics) {
        ConfigurationInfo configInfo;
        ProtoOutputStream protoOutputStream2 = protoOutputStream;
        PrintWriter printWriter = pw;
        DisplayMetrics displayMetrics2 = displayMetrics;
        long token = -1;
        if (protoOutputStream2 != null) {
            token = protoOutputStream.start(fieldId);
            protoOutputStream2.write(1155346202625L, displayMetrics2.widthPixels);
            protoOutputStream2.write(1155346202626L, displayMetrics2.heightPixels);
            protoOutputStream2.write(1155346202627L, DisplayMetrics.DENSITY_DEVICE_STABLE);
        }
        if (printWriter != null) {
            printWriter.print("stable-width-px: ");
            printWriter.println(displayMetrics2.widthPixels);
            printWriter.print("stable-height-px: ");
            printWriter.println(displayMetrics2.heightPixels);
            printWriter.print("stable-density-dpi: ");
            printWriter.println(DisplayMetrics.DENSITY_DEVICE_STABLE);
        }
        MemInfoReader memreader = new MemInfoReader();
        memreader.readMemInfo();
        KeyguardManager kgm = (KeyguardManager) this.mInternal.mContext.getSystemService(KeyguardManager.class);
        if (protoOutputStream2 != null) {
            protoOutputStream2.write(1116691496964L, memreader.getTotalSize());
            protoOutputStream2.write(1133871366149L, ActivityManager.isLowRamDeviceStatic());
            protoOutputStream2.write(1155346202630L, Runtime.getRuntime().availableProcessors());
            protoOutputStream2.write(1133871366151L, kgm.isDeviceSecure());
        }
        if (printWriter != null) {
            printWriter.print("total-ram: ");
            printWriter.println(memreader.getTotalSize());
            printWriter.print("low-ram: ");
            printWriter.println(ActivityManager.isLowRamDeviceStatic());
            printWriter.print("max-cores: ");
            printWriter.println(Runtime.getRuntime().availableProcessors());
            printWriter.print("has-secure-screen-lock: ");
            printWriter.println(kgm.isDeviceSecure());
        }
        try {
            ConfigurationInfo configInfo2 = this.mTaskInterface.getDeviceConfigurationInfo();
            if (configInfo2.reqGlEsVersion != 0) {
                if (protoOutputStream2 != null) {
                    protoOutputStream2.write(1155346202632L, configInfo2.reqGlEsVersion);
                }
                if (printWriter != null) {
                    printWriter.print("opengl-version: 0x");
                    printWriter.println(Integer.toHexString(configInfo2.reqGlEsVersion));
                }
            }
            Set<String> glExtensionsSet = getGlExtensionsFromDriver();
            String[] glExtensions = (String[]) glExtensionsSet.toArray(new String[glExtensionsSet.size()]);
            Arrays.sort(glExtensions);
            for (int i = 0; i < glExtensions.length; i++) {
                if (protoOutputStream2 != null) {
                    protoOutputStream2.write(2237677961225L, glExtensions[i]);
                }
                if (printWriter != null) {
                    printWriter.print("opengl-extensions: ");
                    printWriter.println(glExtensions[i]);
                }
            }
            PackageManager pm = this.mInternal.mContext.getPackageManager();
            List<SharedLibraryInfo> slibs = pm.getSharedLibraries(0);
            Collections.sort(slibs, Comparator.comparing($$Lambda$jVSWDZTj55yxOQmZSLdNsbUkMb4.INSTANCE));
            int i2 = 0;
            while (i2 < slibs.size()) {
                if (protoOutputStream2 != null) {
                    configInfo = configInfo2;
                    protoOutputStream2.write(2237677961226L, slibs.get(i2).getName());
                } else {
                    configInfo = configInfo2;
                }
                if (printWriter != null) {
                    printWriter.print("shared-libraries: ");
                    printWriter.println(slibs.get(i2).getName());
                }
                i2++;
                configInfo2 = configInfo;
            }
            FeatureInfo[] features = pm.getSystemAvailableFeatures();
            Arrays.sort(features, $$Lambda$ActivityManagerShellCommand$yu115wjRB5hvRTjVM9oePAy5cM0.INSTANCE);
            int i3 = 0;
            while (i3 < features.length) {
                if (features[i3].name != null) {
                    if (protoOutputStream2 != null) {
                        protoOutputStream2.write(2237677961227L, features[i3].name);
                    }
                    if (printWriter != null) {
                        printWriter.print("features: ");
                        printWriter.println(features[i3].name);
                    }
                }
                i3++;
            }
            if (protoOutputStream2 != null) {
                protoOutputStream2.end(token);
            }
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    static /* synthetic */ int lambda$writeDeviceConfig$0(FeatureInfo o1, FeatureInfo o2) {
        if (o1.name == o2.name) {
            return 0;
        }
        if (o1.name == null) {
            return -1;
        }
        if (o2.name == null) {
            return 1;
        }
        return o1.name.compareTo(o2.name);
    }

    /* access modifiers changed from: package-private */
    public int runGetConfig(PrintWriter pw) throws RemoteException {
        List<Configuration> recentConfigs;
        int recentConfigSize;
        ProtoOutputStream proto;
        PrintWriter printWriter = pw;
        int days = -1;
        int displayId = 0;
        boolean asProto = false;
        boolean inclDevice = false;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                Configuration config = this.mInterface.getConfiguration();
                if (config == null) {
                    getErrPrintWriter().println("Activity manager has no configuration");
                    return -1;
                }
                Display display = ((DisplayManager) this.mInternal.mContext.getSystemService(DisplayManager.class)).getDisplay(displayId);
                if (display == null) {
                    getErrPrintWriter().println("Error: Display does not exist: " + displayId);
                    return -1;
                }
                DisplayMetrics metrics = new DisplayMetrics();
                display.getMetrics(metrics);
                if (asProto) {
                    ProtoOutputStream proto2 = new ProtoOutputStream(getOutFileDescriptor());
                    config.writeResConfigToProto(proto2, 1146756268033L, metrics);
                    if (inclDevice) {
                        proto = proto2;
                        Display display2 = display;
                        writeDeviceConfig(proto2, 1146756268034L, (PrintWriter) null, config, metrics);
                    } else {
                        proto = proto2;
                        DisplayMetrics displayMetrics = metrics;
                        Display display3 = display;
                    }
                    proto.flush();
                    return 0;
                }
                Display display4 = display;
                StringBuilder sb = new StringBuilder();
                sb.append("config: ");
                DisplayMetrics metrics2 = metrics;
                sb.append(Configuration.resourceQualifierString(config, metrics2));
                printWriter.println(sb.toString());
                printWriter.println("abi: " + TextUtils.join(",", Build.SUPPORTED_ABIS));
                if (inclDevice) {
                    DisplayMetrics displayMetrics2 = metrics2;
                    writeDeviceConfig((ProtoOutputStream) null, -1, pw, config, metrics2);
                }
                if (days < 0 || (recentConfigSize = (recentConfigs = getRecentConfigurations(days)).size()) <= 0) {
                    return 0;
                }
                printWriter.println("recentConfigs:");
                for (int i = 0; i < recentConfigSize; i++) {
                    printWriter.println("  config: " + Configuration.resourceQualifierString(recentConfigs.get(i)));
                }
                return 0;
            } else if (opt.equals("--days")) {
                days = Integer.parseInt(getNextArgRequired());
                if (days <= 0) {
                    throw new IllegalArgumentException("--days must be a positive integer");
                }
            } else if (opt.equals(PriorityDump.PROTO_ARG)) {
                asProto = true;
            } else if (opt.equals("--device")) {
                inclDevice = true;
            } else if (opt.equals("--display")) {
                displayId = Integer.parseInt(getNextArgRequired());
                if (displayId < 0) {
                    throw new IllegalArgumentException("--display must be a non-negative integer");
                }
            } else {
                getErrPrintWriter().println("Error: Unknown option: " + opt);
                return -1;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int runSuppressResizeConfigChanges(PrintWriter pw) throws RemoteException {
        this.mTaskInterface.suppressResizeConfigChanges(Boolean.valueOf(getNextArgRequired()).booleanValue());
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runSetInactive(PrintWriter pw) throws RemoteException {
        int userId = -2;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                IUsageStatsManager.Stub.asInterface(ServiceManager.getService("usagestats")).setAppInactive(getNextArgRequired(), Boolean.parseBoolean(getNextArgRequired()), userId);
                return 0;
            } else if (opt.equals("--user")) {
                userId = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Error: Unknown option: " + opt);
                return -1;
            }
        }
    }

    private int bucketNameToBucketValue(String name) {
        String lower = name.toLowerCase();
        if (lower.startsWith("ac")) {
            return 10;
        }
        if (lower.startsWith("wo")) {
            return 20;
        }
        if (lower.startsWith("fr")) {
            return 30;
        }
        if (lower.startsWith("ra")) {
            return 40;
        }
        if (lower.startsWith("ne")) {
            return 50;
        }
        try {
            return Integer.parseInt(lower);
        } catch (NumberFormatException e) {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: Unknown bucket: " + name);
            return -1;
        }
    }

    /* access modifiers changed from: package-private */
    public int runSetStandbyBucket(PrintWriter pw) throws RemoteException {
        int userId = -2;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                String packageName = getNextArgRequired();
                String value = getNextArgRequired();
                int bucket = bucketNameToBucketValue(value);
                if (bucket < 0) {
                    return -1;
                }
                boolean multiple = peekNextArg() != null;
                IUsageStatsManager usm = IUsageStatsManager.Stub.asInterface(ServiceManager.getService("usagestats"));
                if (!multiple) {
                    usm.setAppStandbyBucket(packageName, bucketNameToBucketValue(value), userId);
                } else {
                    ArrayList<AppStandbyInfo> bucketInfoList = new ArrayList<>();
                    bucketInfoList.add(new AppStandbyInfo(packageName, bucket));
                    while (true) {
                        String nextArg = getNextArg();
                        String packageName2 = nextArg;
                        if (nextArg == null) {
                            break;
                        }
                        int bucket2 = bucketNameToBucketValue(getNextArgRequired());
                        if (bucket2 >= 0) {
                            bucketInfoList.add(new AppStandbyInfo(packageName2, bucket2));
                        }
                    }
                    usm.setAppStandbyBuckets(new ParceledListSlice<>(bucketInfoList), userId);
                }
                return 0;
            } else if (opt.equals("--user")) {
                userId = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Error: Unknown option: " + opt);
                return -1;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int runGetStandbyBucket(PrintWriter pw) throws RemoteException {
        int userId = -2;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                String packageName = getNextArg();
                IUsageStatsManager usm = IUsageStatsManager.Stub.asInterface(ServiceManager.getService("usagestats"));
                if (packageName != null) {
                    pw.println(usm.getAppStandbyBucket(packageName, (String) null, userId));
                    return 0;
                }
                for (AppStandbyInfo bucketInfo : usm.getAppStandbyBuckets("com.android.shell", userId).getList()) {
                    pw.print(bucketInfo.mPackageName);
                    pw.print(": ");
                    pw.println(bucketInfo.mStandbyBucket);
                }
                return 0;
            } else if (opt.equals("--user")) {
                userId = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Error: Unknown option: " + opt);
                return -1;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int runGetInactive(PrintWriter pw) throws RemoteException {
        int userId = -2;
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                boolean isIdle = IUsageStatsManager.Stub.asInterface(ServiceManager.getService("usagestats")).isAppInactive(getNextArgRequired(), userId);
                pw.println("Idle=" + isIdle);
                return 0;
            } else if (opt.equals("--user")) {
                userId = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Error: Unknown option: " + opt);
                return -1;
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int runSendTrimMemory(java.io.PrintWriter r10) throws android.os.RemoteException {
        /*
            r9 = this;
            r0 = -2
        L_0x0001:
            java.lang.String r1 = r9.getNextOption()
            r2 = r1
            r3 = -1
            if (r1 == 0) goto L_0x003e
            java.lang.String r1 = "--user"
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L_0x0025
            java.lang.String r1 = r9.getNextArgRequired()
            int r0 = android.os.UserHandle.parseUserArg(r1)
            if (r0 != r3) goto L_0x0001
            java.io.PrintWriter r1 = r9.getErrPrintWriter()
            java.lang.String r4 = "Error: Can't use user 'all'"
            r1.println(r4)
            return r3
        L_0x0025:
            java.io.PrintWriter r1 = r9.getErrPrintWriter()
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Error: Unknown option: "
            r4.append(r5)
            r4.append(r2)
            java.lang.String r4 = r4.toString()
            r1.println(r4)
            return r3
        L_0x003e:
            java.lang.String r1 = r9.getNextArgRequired()
            java.lang.String r4 = r9.getNextArgRequired()
            int r5 = r4.hashCode()
            r6 = 0
            switch(r5) {
                case -1943119297: goto L_0x008b;
                case -847101650: goto L_0x0081;
                case -219160669: goto L_0x0077;
                case 163769603: goto L_0x006d;
                case 183181625: goto L_0x0063;
                case 1072631956: goto L_0x0059;
                case 2130809258: goto L_0x004f;
                default: goto L_0x004e;
            }
        L_0x004e:
            goto L_0x0095
        L_0x004f:
            java.lang.String r5 = "HIDDEN"
            boolean r5 = r4.equals(r5)
            if (r5 == 0) goto L_0x004e
            r5 = r6
            goto L_0x0096
        L_0x0059:
            java.lang.String r5 = "RUNNING_LOW"
            boolean r5 = r4.equals(r5)
            if (r5 == 0) goto L_0x004e
            r5 = 3
            goto L_0x0096
        L_0x0063:
            java.lang.String r5 = "COMPLETE"
            boolean r5 = r4.equals(r5)
            if (r5 == 0) goto L_0x004e
            r5 = 6
            goto L_0x0096
        L_0x006d:
            java.lang.String r5 = "MODERATE"
            boolean r5 = r4.equals(r5)
            if (r5 == 0) goto L_0x004e
            r5 = 4
            goto L_0x0096
        L_0x0077:
            java.lang.String r5 = "RUNNING_MODERATE"
            boolean r5 = r4.equals(r5)
            if (r5 == 0) goto L_0x004e
            r5 = 1
            goto L_0x0096
        L_0x0081:
            java.lang.String r5 = "BACKGROUND"
            boolean r5 = r4.equals(r5)
            if (r5 == 0) goto L_0x004e
            r5 = 2
            goto L_0x0096
        L_0x008b:
            java.lang.String r5 = "RUNNING_CRITICAL"
            boolean r5 = r4.equals(r5)
            if (r5 == 0) goto L_0x004e
            r5 = 5
            goto L_0x0096
        L_0x0095:
            r5 = r3
        L_0x0096:
            switch(r5) {
                case 0: goto L_0x00af;
                case 1: goto L_0x00ad;
                case 2: goto L_0x00aa;
                case 3: goto L_0x00a7;
                case 4: goto L_0x00a4;
                case 5: goto L_0x00a1;
                case 6: goto L_0x009e;
                default: goto L_0x0099;
            }
        L_0x0099:
            int r5 = java.lang.Integer.parseInt(r4)     // Catch:{ NumberFormatException -> 0x00c6 }
            goto L_0x00b2
        L_0x009e:
            r5 = 80
            goto L_0x00b3
        L_0x00a1:
            r5 = 15
            goto L_0x00b3
        L_0x00a4:
            r5 = 60
            goto L_0x00b3
        L_0x00a7:
            r5 = 10
            goto L_0x00b3
        L_0x00aa:
            r5 = 40
            goto L_0x00b3
        L_0x00ad:
            r5 = 5
            goto L_0x00b3
        L_0x00af:
            r5 = 20
            goto L_0x00b3
        L_0x00b2:
        L_0x00b3:
            android.app.IActivityManager r7 = r9.mInterface
            boolean r7 = r7.setProcessMemoryTrimLevel(r1, r0, r5)
            if (r7 != 0) goto L_0x00c5
            java.io.PrintWriter r6 = r9.getErrPrintWriter()
            java.lang.String r7 = "Unknown error: failed to set trim level"
            r6.println(r7)
            return r3
        L_0x00c5:
            return r6
        L_0x00c6:
            r5 = move-exception
            java.io.PrintWriter r6 = r9.getErrPrintWriter()
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Error: Unknown level option: "
            r7.append(r8)
            r7.append(r4)
            java.lang.String r7 = r7.toString()
            r6.println(r7)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActivityManagerShellCommand.runSendTrimMemory(java.io.PrintWriter):int");
    }

    /* access modifiers changed from: package-private */
    public int runDisplay(PrintWriter pw) throws RemoteException {
        String op = getNextArgRequired();
        if (!((op.hashCode() == 1625698700 && op.equals("move-stack")) ? false : true)) {
            return runDisplayMoveStack(pw);
        }
        PrintWriter errPrintWriter = getErrPrintWriter();
        errPrintWriter.println("Error: unknown command '" + op + "'");
        return -1;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int runStack(java.io.PrintWriter r6) throws android.os.RemoteException {
        /*
            r5 = this;
            java.lang.String r0 = r5.getNextArgRequired()
            int r1 = r0.hashCode()
            r2 = -1
            switch(r1) {
                case -1551426916: goto L_0x0066;
                case -1152140410: goto L_0x005b;
                case -1082080869: goto L_0x0050;
                case -934610812: goto L_0x0044;
                case -934437708: goto L_0x0039;
                case 3237038: goto L_0x002e;
                case 3322014: goto L_0x0023;
                case 35000878: goto L_0x0018;
                case 1022285313: goto L_0x000d;
                default: goto L_0x000c;
            }
        L_0x000c:
            goto L_0x0071
        L_0x000d:
            java.lang.String r1 = "move-task"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 0
            goto L_0x0072
        L_0x0018:
            java.lang.String r1 = "positiontask"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 4
            goto L_0x0072
        L_0x0023:
            java.lang.String r1 = "list"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 5
            goto L_0x0072
        L_0x002e:
            java.lang.String r1 = "info"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 6
            goto L_0x0072
        L_0x0039:
            java.lang.String r1 = "resize"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 1
            goto L_0x0072
        L_0x0044:
            java.lang.String r1 = "remove"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 8
            goto L_0x0072
        L_0x0050:
            java.lang.String r1 = "move-top-activity-to-pinned-stack"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 7
            goto L_0x0072
        L_0x005b:
            java.lang.String r1 = "resize-docked-stack"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 3
            goto L_0x0072
        L_0x0066:
            java.lang.String r1 = "resize-animated"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000c
            r1 = 2
            goto L_0x0072
        L_0x0071:
            r1 = r2
        L_0x0072:
            switch(r1) {
                case 0: goto L_0x00bb;
                case 1: goto L_0x00b6;
                case 2: goto L_0x00b1;
                case 3: goto L_0x00ac;
                case 4: goto L_0x00a7;
                case 5: goto L_0x00a2;
                case 6: goto L_0x009d;
                case 7: goto L_0x0098;
                case 8: goto L_0x0093;
                default: goto L_0x0075;
            }
        L_0x0075:
            java.io.PrintWriter r1 = r5.getErrPrintWriter()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Error: unknown command '"
            r3.append(r4)
            r3.append(r0)
            java.lang.String r4 = "'"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            return r2
        L_0x0093:
            int r1 = r5.runStackRemove(r6)
            return r1
        L_0x0098:
            int r1 = r5.runMoveTopActivityToPinnedStack(r6)
            return r1
        L_0x009d:
            int r1 = r5.runStackInfo(r6)
            return r1
        L_0x00a2:
            int r1 = r5.runStackList(r6)
            return r1
        L_0x00a7:
            int r1 = r5.runStackPositionTask(r6)
            return r1
        L_0x00ac:
            int r1 = r5.runStackResizeDocked(r6)
            return r1
        L_0x00b1:
            int r1 = r5.runStackResizeAnimated(r6)
            return r1
        L_0x00b6:
            int r1 = r5.runStackResize(r6)
            return r1
        L_0x00bb:
            int r1 = r5.runStackMoveTask(r6)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActivityManagerShellCommand.runStack(java.io.PrintWriter):int");
    }

    private Rect getBounds() {
        String leftStr = getNextArgRequired();
        int left = Integer.parseInt(leftStr);
        String topStr = getNextArgRequired();
        int top = Integer.parseInt(topStr);
        String rightStr = getNextArgRequired();
        int right = Integer.parseInt(rightStr);
        String bottomStr = getNextArgRequired();
        int bottom = Integer.parseInt(bottomStr);
        if (left < 0) {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: bad left arg: " + leftStr);
            return null;
        } else if (top < 0) {
            PrintWriter errPrintWriter2 = getErrPrintWriter();
            errPrintWriter2.println("Error: bad top arg: " + topStr);
            return null;
        } else if (right <= 0) {
            PrintWriter errPrintWriter3 = getErrPrintWriter();
            errPrintWriter3.println("Error: bad right arg: " + rightStr);
            return null;
        } else if (bottom > 0) {
            return new Rect(left, top, right, bottom);
        } else {
            PrintWriter errPrintWriter4 = getErrPrintWriter();
            errPrintWriter4.println("Error: bad bottom arg: " + bottomStr);
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public int runDisplayMoveStack(PrintWriter pw) throws RemoteException {
        this.mTaskInterface.moveStackToDisplay(Integer.parseInt(getNextArgRequired()), Integer.parseInt(getNextArgRequired()));
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runStackMoveTask(PrintWriter pw) throws RemoteException {
        boolean toTop;
        int taskId = Integer.parseInt(getNextArgRequired());
        int stackId = Integer.parseInt(getNextArgRequired());
        String toTopStr = getNextArgRequired();
        if ("true".equals(toTopStr)) {
            toTop = true;
        } else if ("false".equals(toTopStr)) {
            toTop = false;
        } else {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: bad toTop arg: " + toTopStr);
            return -1;
        }
        this.mTaskInterface.moveTaskToStack(taskId, stackId, toTop);
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runStackResize(PrintWriter pw) throws RemoteException {
        int stackId = Integer.parseInt(getNextArgRequired());
        Rect bounds = getBounds();
        if (bounds != null) {
            return resizeStack(stackId, bounds, 0);
        }
        getErrPrintWriter().println("Error: invalid input bounds");
        return -1;
    }

    /* access modifiers changed from: package-private */
    public int runStackResizeAnimated(PrintWriter pw) throws RemoteException {
        Rect bounds;
        int stackId = Integer.parseInt(getNextArgRequired());
        if ("null".equals(peekNextArg())) {
            bounds = null;
        } else {
            bounds = getBounds();
            if (bounds == null) {
                getErrPrintWriter().println("Error: invalid input bounds");
                return -1;
            }
        }
        return resizeStackUnchecked(stackId, bounds, 0, true);
    }

    /* access modifiers changed from: package-private */
    public int resizeStackUnchecked(int stackId, Rect bounds, int delayMs, boolean animate) throws RemoteException {
        try {
            this.mTaskInterface.resizeStack(stackId, bounds, false, false, animate, -1);
            Thread.sleep((long) delayMs);
            return 0;
        } catch (InterruptedException e) {
            return 0;
        }
    }

    /* access modifiers changed from: package-private */
    public int runStackResizeDocked(PrintWriter pw) throws RemoteException {
        Rect bounds = getBounds();
        Rect taskBounds = getBounds();
        if (bounds == null || taskBounds == null) {
            getErrPrintWriter().println("Error: invalid input bounds");
            return -1;
        }
        this.mTaskInterface.resizeDockedStack(bounds, taskBounds, (Rect) null, (Rect) null, (Rect) null);
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int resizeStack(int stackId, Rect bounds, int delayMs) throws RemoteException {
        if (bounds != null) {
            return resizeStackUnchecked(stackId, bounds, delayMs, false);
        }
        getErrPrintWriter().println("Error: invalid input bounds");
        return -1;
    }

    /* access modifiers changed from: package-private */
    public int runStackPositionTask(PrintWriter pw) throws RemoteException {
        this.mTaskInterface.positionTaskInStack(Integer.parseInt(getNextArgRequired()), Integer.parseInt(getNextArgRequired()), Integer.parseInt(getNextArgRequired()));
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runStackList(PrintWriter pw) throws RemoteException {
        for (ActivityManager.StackInfo info : this.mTaskInterface.getAllStackInfos()) {
            pw.println(info);
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runStackInfo(PrintWriter pw) throws RemoteException {
        pw.println(this.mTaskInterface.getStackInfo(Integer.parseInt(getNextArgRequired()), Integer.parseInt(getNextArgRequired())));
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runStackRemove(PrintWriter pw) throws RemoteException {
        this.mTaskInterface.removeStack(Integer.parseInt(getNextArgRequired()));
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runMoveTopActivityToPinnedStack(PrintWriter pw) throws RemoteException {
        int stackId = Integer.parseInt(getNextArgRequired());
        Rect bounds = getBounds();
        if (bounds == null) {
            getErrPrintWriter().println("Error: invalid input bounds");
            return -1;
        } else if (this.mTaskInterface.moveTopActivityToPinnedStack(stackId, bounds)) {
            return 0;
        } else {
            getErrPrintWriter().println("Didn't move top activity to pinned stack.");
            return -1;
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0046  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x006e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setBoundsSide(android.graphics.Rect r6, java.lang.String r7, int r8) {
        /*
            r5 = this;
            int r0 = r7.hashCode()
            r1 = 98
            r2 = 3
            r3 = 2
            r4 = 1
            if (r0 == r1) goto L_0x0039
            r1 = 108(0x6c, float:1.51E-43)
            if (r0 == r1) goto L_0x002e
            r1 = 114(0x72, float:1.6E-43)
            if (r0 == r1) goto L_0x0023
            r1 = 116(0x74, float:1.63E-43)
            if (r0 == r1) goto L_0x0018
        L_0x0017:
            goto L_0x0043
        L_0x0018:
            java.lang.String r0 = "t"
            boolean r0 = r7.equals(r0)
            if (r0 == 0) goto L_0x0017
            r0 = r3
            goto L_0x0044
        L_0x0023:
            java.lang.String r0 = "r"
            boolean r0 = r7.equals(r0)
            if (r0 == 0) goto L_0x0017
            r0 = r4
            goto L_0x0044
        L_0x002e:
            java.lang.String r0 = "l"
            boolean r0 = r7.equals(r0)
            if (r0 == 0) goto L_0x0017
            r0 = 0
            goto L_0x0044
        L_0x0039:
            java.lang.String r0 = "b"
            boolean r0 = r7.equals(r0)
            if (r0 == 0) goto L_0x0017
            r0 = r2
            goto L_0x0044
        L_0x0043:
            r0 = -1
        L_0x0044:
            if (r0 == 0) goto L_0x006e
            if (r0 == r4) goto L_0x006b
            if (r0 == r3) goto L_0x0068
            if (r0 == r2) goto L_0x0065
            java.io.PrintWriter r0 = r5.getErrPrintWriter()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Unknown set side: "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r1 = r1.toString()
            r0.println(r1)
            goto L_0x0071
        L_0x0065:
            r6.bottom = r8
            goto L_0x0071
        L_0x0068:
            r6.top = r8
            goto L_0x0071
        L_0x006b:
            r6.right = r8
            goto L_0x0071
        L_0x006e:
            r6.left = r8
        L_0x0071:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActivityManagerShellCommand.setBoundsSide(android.graphics.Rect, java.lang.String, int):void");
    }

    /* access modifiers changed from: package-private */
    public int runTask(PrintWriter pw) throws RemoteException {
        String op = getNextArgRequired();
        if (op.equals("lock")) {
            return runTaskLock(pw);
        }
        if (op.equals("resizeable")) {
            return runTaskResizeable(pw);
        }
        if (op.equals("resize")) {
            return runTaskResize(pw);
        }
        if (op.equals("focus")) {
            return runTaskFocus(pw);
        }
        PrintWriter errPrintWriter = getErrPrintWriter();
        errPrintWriter.println("Error: unknown command '" + op + "'");
        return -1;
    }

    /* access modifiers changed from: package-private */
    public int runTaskLock(PrintWriter pw) throws RemoteException {
        String taskIdStr = getNextArgRequired();
        if (taskIdStr.equals("stop")) {
            this.mTaskInterface.stopSystemLockTaskMode();
        } else {
            this.mTaskInterface.startSystemLockTaskMode(Integer.parseInt(taskIdStr));
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Activity manager is ");
        sb.append(this.mTaskInterface.isInLockTaskMode() ? "" : "not ");
        sb.append("in lockTaskMode");
        pw.println(sb.toString());
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runTaskResizeable(PrintWriter pw) throws RemoteException {
        this.mTaskInterface.setTaskResizeable(Integer.parseInt(getNextArgRequired()), Integer.parseInt(getNextArgRequired()));
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runTaskResize(PrintWriter pw) throws RemoteException {
        int taskId = Integer.parseInt(getNextArgRequired());
        Rect bounds = getBounds();
        if (bounds == null) {
            getErrPrintWriter().println("Error: invalid input bounds");
            return -1;
        }
        taskResize(taskId, bounds, 0, false);
        return 0;
    }

    /* access modifiers changed from: package-private */
    public void taskResize(int taskId, Rect bounds, int delay_ms, boolean pretendUserResize) throws RemoteException {
        this.mTaskInterface.resizeTask(taskId, bounds, (int) pretendUserResize);
        try {
            Thread.sleep((long) delay_ms);
        } catch (InterruptedException e) {
        }
    }

    /* access modifiers changed from: package-private */
    public int moveTask(int taskId, Rect taskRect, Rect stackRect, int stepSize, int maxToTravel, boolean movingForward, boolean horizontal, int delay_ms) throws RemoteException {
        if (movingForward) {
            while (maxToTravel > 0 && ((horizontal && taskRect.right < stackRect.right) || (!horizontal && taskRect.bottom < stackRect.bottom))) {
                if (horizontal) {
                    int maxMove = Math.min(stepSize, stackRect.right - taskRect.right);
                    maxToTravel -= maxMove;
                    taskRect.right += maxMove;
                    taskRect.left += maxMove;
                } else {
                    int maxMove2 = Math.min(stepSize, stackRect.bottom - taskRect.bottom);
                    maxToTravel -= maxMove2;
                    taskRect.top += maxMove2;
                    taskRect.bottom += maxMove2;
                }
                taskResize(taskId, taskRect, delay_ms, false);
            }
        } else {
            while (maxToTravel < 0 && ((horizontal && taskRect.left > stackRect.left) || (!horizontal && taskRect.top > stackRect.top))) {
                if (horizontal) {
                    int maxMove3 = Math.min(stepSize, taskRect.left - stackRect.left);
                    maxToTravel = maxToTravel - maxMove3;
                    taskRect.right -= maxMove3;
                    taskRect.left -= maxMove3;
                } else {
                    int maxMove4 = Math.min(stepSize, taskRect.top - stackRect.top);
                    maxToTravel = maxToTravel - maxMove4;
                    taskRect.top -= maxMove4;
                    taskRect.bottom -= maxMove4;
                }
                taskResize(taskId, taskRect, delay_ms, false);
            }
        }
        return maxToTravel;
    }

    /* access modifiers changed from: package-private */
    public int getStepSize(int current, int target, int inStepSize, boolean greaterThanTarget) {
        int stepSize = 0;
        if (greaterThanTarget && target < current) {
            current -= inStepSize;
            stepSize = inStepSize;
            if (target > current) {
                stepSize -= target - current;
            }
        }
        if (greaterThanTarget || target <= current) {
            return stepSize;
        }
        int current2 = current + inStepSize;
        int stepSize2 = inStepSize;
        if (target < current2) {
            return stepSize2 + (current2 - target);
        }
        return stepSize2;
    }

    /* access modifiers changed from: package-private */
    public int runTaskFocus(PrintWriter pw) throws RemoteException {
        int taskId = Integer.parseInt(getNextArgRequired());
        pw.println("Setting focus to task " + taskId);
        this.mTaskInterface.setFocusedTask(taskId);
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runWrite(PrintWriter pw) {
        this.mInternal.enforceCallingPermission("android.permission.SET_ACTIVITY_WATCHER", "registerUidObserver()");
        this.mInternal.mAtmInternal.flushRecentTasks();
        pw.println("All tasks persisted.");
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runAttachAgent(PrintWriter pw) {
        this.mInternal.enforceCallingPermission("android.permission.SET_ACTIVITY_WATCHER", "attach-agent");
        String process = getNextArgRequired();
        String agent = getNextArgRequired();
        String nextArg = getNextArg();
        String opt = nextArg;
        if (nextArg != null) {
            pw.println("Error: Unknown option: " + opt);
            return -1;
        }
        this.mInternal.attachAgent(process, agent);
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runSupportsMultiwindow(PrintWriter pw) throws RemoteException {
        if (getResources(pw) == null) {
            return -1;
        }
        pw.println(ActivityTaskManager.supportsMultiWindow(this.mInternal.mContext));
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runSupportsSplitScreenMultiwindow(PrintWriter pw) throws RemoteException {
        if (getResources(pw) == null) {
            return -1;
        }
        pw.println(ActivityTaskManager.supportsSplitScreenMultiWindow(this.mInternal.mContext));
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runUpdateApplicationInfo(PrintWriter pw) throws RemoteException {
        int userid = UserHandle.parseUserArg(getNextArgRequired());
        ArrayList<String> packages = new ArrayList<>();
        packages.add(getNextArgRequired());
        while (true) {
            String nextArg = getNextArg();
            String packageName = nextArg;
            if (nextArg != null) {
                packages.add(packageName);
            } else {
                this.mInternal.scheduleApplicationInfoChanged(packages, userid);
                pw.println("Packages updated with most recent ApplicationInfos.");
                return 0;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int runNoHomeScreen(PrintWriter pw) throws RemoteException {
        Resources res = getResources(pw);
        if (res == null) {
            return -1;
        }
        pw.println(res.getBoolean(17891490));
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int runWaitForBroadcastIdle(PrintWriter pw) throws RemoteException {
        this.mInternal.waitForBroadcastIdle(pw);
        return 0;
    }

    private Resources getResources(PrintWriter pw) throws RemoteException {
        Configuration config = this.mInterface.getConfiguration();
        if (config == null) {
            pw.println("Error: Activity manager has no configuration");
            return null;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        metrics.setToDefaults();
        return new Resources(AssetManager.getSystem(), metrics, config);
    }

    public void onHelp() {
        dumpHelp(getOutPrintWriter(), this.mDumping);
    }

    static void dumpHelp(PrintWriter pw, boolean dumping) {
        if (dumping) {
            pw.println("Activity manager dump options:");
            pw.println("  [-a] [-c] [-p PACKAGE] [-h] [WHAT] ...");
            pw.println("  WHAT may be one of:");
            pw.println("    a[ctivities]: activity stack state");
            pw.println("    r[recents]: recent activities state");
            pw.println("    b[roadcasts] [PACKAGE_NAME] [history [-s]]: broadcast state");
            pw.println("    broadcast-stats [PACKAGE_NAME]: aggregated broadcast statistics");
            pw.println("    i[ntents] [PACKAGE_NAME]: pending intent state");
            pw.println("    p[rocesses] [PACKAGE_NAME]: process state");
            pw.println("    o[om]: out of memory management");
            pw.println("    perm[issions]: URI permission grant state");
            pw.println("    prov[iders] [COMP_SPEC ...]: content provider state");
            pw.println("    provider [COMP_SPEC]: provider client-side state");
            pw.println("    s[ervices] [COMP_SPEC ...]: service state");
            pw.println("    allowed-associations: current package association restrictions");
            pw.println("    as[sociations]: tracked app associations");
            pw.println("    lmk: stats on low memory killer");
            pw.println("    lru: raw LRU process list");
            pw.println("    binder-proxies: stats on binder objects and IPCs");
            pw.println("    settings: currently applied config settings");
            pw.println("    service [COMP_SPEC]: service client-side state");
            pw.println("    package [PACKAGE_NAME]: all state related to given package");
            pw.println("    all: dump all activities");
            pw.println("    top: dump the top activity");
            pw.println("  WHAT may also be a COMP_SPEC to dump activities.");
            pw.println("  COMP_SPEC may be a component name (com.foo/.myApp),");
            pw.println("    a partial substring in a component name, a");
            pw.println("    hex object identifier.");
            pw.println("  -a: include all available server state.");
            pw.println("  -c: include client state.");
            pw.println("  -p: limit output to given package.");
            pw.println("  --checkin: output checkin format, resetting data.");
            pw.println("  --C: output checkin format, not resetting data.");
            pw.println("  --proto: output dump in protocol buffer format.");
            pw.println("  --autofill: dump just the autofill-related state of an activity");
            return;
        }
        pw.println("Activity manager (activity) commands:");
        pw.println("  help");
        pw.println("      Print this help text.");
        pw.println("  start-activity [-D] [-N] [-W] [-P <FILE>] [--start-profiler <FILE>]");
        pw.println("          [--sampling INTERVAL] [--streaming] [-R COUNT] [-S]");
        pw.println("          [--track-allocation] [--user <USER_ID> | current] <INTENT>");
        pw.println("      Start an Activity.  Options are:");
        pw.println("      -D: enable debugging");
        pw.println("      -N: enable native debugging");
        pw.println("      -W: wait for launch to complete");
        pw.println("      --start-profiler <FILE>: start profiler and send results to <FILE>");
        pw.println("      --sampling INTERVAL: use sample profiling with INTERVAL microseconds");
        pw.println("          between samples (use with --start-profiler)");
        pw.println("      --streaming: stream the profiling output to the specified file");
        pw.println("          (use with --start-profiler)");
        pw.println("      -P <FILE>: like above, but profiling stops when app goes idle");
        pw.println("      --attach-agent <agent>: attach the given agent before binding");
        pw.println("      --attach-agent-bind <agent>: attach the given agent during binding");
        pw.println("      -R: repeat the activity launch <COUNT> times.  Prior to each repeat,");
        pw.println("          the top activity will be finished.");
        pw.println("      -S: force stop the target app before starting the activity");
        pw.println("      --track-allocation: enable tracking of object allocations");
        pw.println("      --user <USER_ID> | current: Specify which user to run as; if not");
        pw.println("          specified then run as the current user.");
        pw.println("      --windowingMode <WINDOWING_MODE>: The windowing mode to launch the activity into.");
        pw.println("      --activityType <ACTIVITY_TYPE>: The activity type to launch the activity as.");
        pw.println("      --display <DISPLAY_ID>: The display to launch the activity into.");
        pw.println("  start-service [--user <USER_ID> | current] <INTENT>");
        pw.println("      Start a Service.  Options are:");
        pw.println("      --user <USER_ID> | current: Specify which user to run as; if not");
        pw.println("          specified then run as the current user.");
        pw.println("  start-foreground-service [--user <USER_ID> | current] <INTENT>");
        pw.println("      Start a foreground Service.  Options are:");
        pw.println("      --user <USER_ID> | current: Specify which user to run as; if not");
        pw.println("          specified then run as the current user.");
        pw.println("  stop-service [--user <USER_ID> | current] <INTENT>");
        pw.println("      Stop a Service.  Options are:");
        pw.println("      --user <USER_ID> | current: Specify which user to run as; if not");
        pw.println("          specified then run as the current user.");
        pw.println("  broadcast [--user <USER_ID> | all | current] <INTENT>");
        pw.println("      Send a broadcast Intent.  Options are:");
        pw.println("      --user <USER_ID> | all | current: Specify which user to send to; if not");
        pw.println("          specified then send to all users.");
        pw.println("      --receiver-permission <PERMISSION>: Require receiver to hold permission.");
        pw.println("  instrument [-r] [-e <NAME> <VALUE>] [-p <FILE>] [-w]");
        pw.println("          [--user <USER_ID> | current] [--no-hidden-api-checks]");
        pw.println("          [--no-isolated-storage]");
        pw.println("          [--no-window-animation] [--abi <ABI>] <COMPONENT>");
        pw.println("      Start an Instrumentation.  Typically this target <COMPONENT> is in the");
        pw.println("      form <TEST_PACKAGE>/<RUNNER_CLASS> or only <TEST_PACKAGE> if there");
        pw.println("      is only one instrumentation.  Options are:");
        pw.println("      -r: print raw results (otherwise decode REPORT_KEY_STREAMRESULT).  Use with");
        pw.println("          [-e perf true] to generate raw output for performance measurements.");
        pw.println("      -e <NAME> <VALUE>: set argument <NAME> to <VALUE>.  For test runners a");
        pw.println("          common form is [-e <testrunner_flag> <value>[,<value>...]].");
        pw.println("      -p <FILE>: write profiling data to <FILE>");
        pw.println("      -m: Write output as protobuf to stdout (machine readable)");
        pw.println("      -f <Optional PATH/TO/FILE>: Write output as protobuf to a file (machine");
        pw.println("          readable). If path is not specified, default directory and file name will");
        pw.println("          be used: /sdcard/instrument-logs/log-yyyyMMdd-hhmmss-SSS.instrumentation_data_proto");
        pw.println("      -w: wait for instrumentation to finish before returning.  Required for");
        pw.println("          test runners.");
        pw.println("      --user <USER_ID> | current: Specify user instrumentation runs in;");
        pw.println("          current user if not specified.");
        pw.println("      --no-hidden-api-checks: disable restrictions on use of hidden API.");
        pw.println("      --no-isolated-storage: don't use isolated storage sandbox and ");
        pw.println("          mount full external storage");
        pw.println("      --no-window-animation: turn off window animations while running.");
        pw.println("      --abi <ABI>: Launch the instrumented process with the selected ABI.");
        pw.println("          This assumes that the process supports the selected ABI.");
        pw.println("  trace-ipc [start|stop] [--dump-file <FILE>]");
        pw.println("      Trace IPC transactions.");
        pw.println("      start: start tracing IPC transactions.");
        pw.println("      stop: stop tracing IPC transactions and dump the results to file.");
        pw.println("      --dump-file <FILE>: Specify the file the trace should be dumped to.");
        pw.println("  profile start [--user <USER_ID> current]");
        pw.println("          [--sampling INTERVAL | --streaming] <PROCESS> <FILE>");
        pw.println("      Start profiler on a process.  The given <PROCESS> argument");
        pw.println("        may be either a process name or pid.  Options are:");
        pw.println("      --user <USER_ID> | current: When supplying a process name,");
        pw.println("          specify user of process to profile; uses current user if not");
        pw.println("          specified.");
        pw.println("      --sampling INTERVAL: use sample profiling with INTERVAL microseconds");
        pw.println("          between samples.");
        pw.println("      --streaming: stream the profiling output to the specified file.");
        pw.println("  profile stop [--user <USER_ID> current] <PROCESS>");
        pw.println("      Stop profiler on a process.  The given <PROCESS> argument");
        pw.println("        may be either a process name or pid.  Options are:");
        pw.println("      --user <USER_ID> | current: When supplying a process name,");
        pw.println("          specify user of process to profile; uses current user if not");
        pw.println("          specified.");
        pw.println("  dumpheap [--user <USER_ID> current] [-n] [-g] <PROCESS> <FILE>");
        pw.println("      Dump the heap of a process.  The given <PROCESS> argument may");
        pw.println("        be either a process name or pid.  Options are:");
        pw.println("      -n: dump native heap instead of managed heap");
        pw.println("      -g: force GC before dumping the heap");
        pw.println("      --user <USER_ID> | current: When supplying a process name,");
        pw.println("          specify user of process to dump; uses current user if not specified.");
        pw.println("  set-debug-app [-w] [--persistent] <PACKAGE>");
        pw.println("      Set application <PACKAGE> to debug.  Options are:");
        pw.println("      -w: wait for debugger when application starts");
        pw.println("      --persistent: retain this value");
        pw.println("  clear-debug-app");
        pw.println("      Clear the previously set-debug-app.");
        pw.println("  set-watch-heap <PROCESS> <MEM-LIMIT>");
        pw.println("      Start monitoring pss size of <PROCESS>, if it is at or");
        pw.println("      above <HEAP-LIMIT> then a heap dump is collected for the user to report.");
        pw.println("  clear-watch-heap");
        pw.println("      Clear the previously set-watch-heap.");
        pw.println("  bug-report [--progress | --telephony]");
        pw.println("      Request bug report generation; will launch a notification");
        pw.println("        when done to select where it should be delivered. Options are:");
        pw.println("     --progress: will launch a notification right away to show its progress.");
        pw.println("     --telephony: will dump only telephony sections.");
        pw.println("  force-stop [--user <USER_ID> | all | current] <PACKAGE>");
        pw.println("      Completely stop the given application package.");
        pw.println("  crash [--user <USER_ID>] <PACKAGE|PID>");
        pw.println("      Induce a VM crash in the specified package or process");
        pw.println("  kill [--user <USER_ID> | all | current] <PACKAGE>");
        pw.println("      Kill all background processes associated with the given application.");
        pw.println("  kill-all");
        pw.println("      Kill all processes that are safe to kill (cached, etc).");
        pw.println("  make-uid-idle [--user <USER_ID> | all | current] <PACKAGE>");
        pw.println("      If the given application's uid is in the background and waiting to");
        pw.println("      become idle (not allowing background services), do that now.");
        pw.println("  monitor [--gdb <port>]");
        pw.println("      Start monitoring for crashes or ANRs.");
        pw.println("      --gdb: start gdbserv on the given port at crash/ANR");
        pw.println("  watch-uids [--oom <uid>]");
        pw.println("      Start watching for and reporting uid state changes.");
        pw.println("      --oom: specify a uid for which to report detailed change messages.");
        pw.println("  hang [--allow-restart]");
        pw.println("      Hang the system.");
        pw.println("      --allow-restart: allow watchdog to perform normal system restart");
        pw.println("  restart");
        pw.println("      Restart the user-space system.");
        pw.println("  idle-maintenance");
        pw.println("      Perform idle maintenance now.");
        pw.println("  screen-compat [on|off] <PACKAGE>");
        pw.println("      Control screen compatibility mode of <PACKAGE>.");
        pw.println("  package-importance <PACKAGE>");
        pw.println("      Print current importance of <PACKAGE>.");
        pw.println("  to-uri [INTENT]");
        pw.println("      Print the given Intent specification as a URI.");
        pw.println("  to-intent-uri [INTENT]");
        pw.println("      Print the given Intent specification as an intent: URI.");
        pw.println("  to-app-uri [INTENT]");
        pw.println("      Print the given Intent specification as an android-app: URI.");
        pw.println("  switch-user <USER_ID>");
        pw.println("      Switch to put USER_ID in the foreground, starting");
        pw.println("      execution of that user if it is currently stopped.");
        pw.println("  get-current-user");
        pw.println("      Returns id of the current foreground user.");
        pw.println("  start-user [-w] <USER_ID>");
        pw.println("      Start USER_ID in background if it is currently stopped;");
        pw.println("      use switch-user if you want to start the user in foreground.");
        pw.println("      -w: wait for start-user to complete and the user to be unlocked.");
        pw.println("  unlock-user <USER_ID> [TOKEN_HEX]");
        pw.println("      Attempt to unlock the given user using the given authorization token.");
        pw.println("  stop-user [-w] [-f] <USER_ID>");
        pw.println("      Stop execution of USER_ID, not allowing it to run any");
        pw.println("      code until a later explicit start or switch to it.");
        pw.println("      -w: wait for stop-user to complete.");
        pw.println("      -f: force stop even if there are related users that cannot be stopped.");
        pw.println("  is-user-stopped <USER_ID>");
        pw.println("      Returns whether <USER_ID> has been stopped or not.");
        pw.println("  get-started-user-state <USER_ID>");
        pw.println("      Gets the current state of the given started user.");
        pw.println("  track-associations");
        pw.println("      Enable association tracking.");
        pw.println("  untrack-associations");
        pw.println("      Disable and clear association tracking.");
        pw.println("  get-uid-state <UID>");
        pw.println("      Gets the process state of an app given its <UID>.");
        pw.println("  attach-agent <PROCESS> <FILE>");
        pw.println("    Attach an agent to the specified <PROCESS>, which may be either a process name or a PID.");
        pw.println("  get-config [--days N] [--device] [--proto] [--display <DISPLAY_ID>]");
        pw.println("      Retrieve the configuration and any recent configurations of the device.");
        pw.println("      --days: also return last N days of configurations that have been seen.");
        pw.println("      --device: also output global device configuration info.");
        pw.println("      --proto: return result as a proto; does not include --days info.");
        pw.println("      --display: Specify for which display to run the command; if not ");
        pw.println("          specified then run for the default display.");
        pw.println("  supports-multiwindow");
        pw.println("      Returns true if the device supports multiwindow.");
        pw.println("  supports-split-screen-multi-window");
        pw.println("      Returns true if the device supports split screen multiwindow.");
        pw.println("  suppress-resize-config-changes <true|false>");
        pw.println("      Suppresses configuration changes due to user resizing an activity/task.");
        pw.println("  set-inactive [--user <USER_ID>] <PACKAGE> true|false");
        pw.println("      Sets the inactive state of an app.");
        pw.println("  get-inactive [--user <USER_ID>] <PACKAGE>");
        pw.println("      Returns the inactive state of an app.");
        pw.println("  set-standby-bucket [--user <USER_ID>] <PACKAGE> active|working_set|frequent|rare");
        pw.println("      Puts an app in the standby bucket.");
        pw.println("  get-standby-bucket [--user <USER_ID>] <PACKAGE>");
        pw.println("      Returns the standby bucket of an app.");
        pw.println("  send-trim-memory [--user <USER_ID>] <PROCESS>");
        pw.println("          [HIDDEN|RUNNING_MODERATE|BACKGROUND|RUNNING_LOW|MODERATE|RUNNING_CRITICAL|COMPLETE]");
        pw.println("      Send a memory trim event to a <PROCESS>.  May also supply a raw trim int level.");
        pw.println("  display [COMMAND] [...]: sub-commands for operating on displays.");
        pw.println("       move-stack <STACK_ID> <DISPLAY_ID>");
        pw.println("           Move <STACK_ID> from its current display to <DISPLAY_ID>.");
        pw.println("  stack [COMMAND] [...]: sub-commands for operating on activity stacks.");
        pw.println("       move-task <TASK_ID> <STACK_ID> [true|false]");
        pw.println("           Move <TASK_ID> from its current stack to the top (true) or");
        pw.println("           bottom (false) of <STACK_ID>.");
        pw.println("       resize <STACK_ID> <LEFT,TOP,RIGHT,BOTTOM>");
        pw.println("           Change <STACK_ID> size and position to <LEFT,TOP,RIGHT,BOTTOM>.");
        pw.println("       resize-animated <STACK_ID> <LEFT,TOP,RIGHT,BOTTOM>");
        pw.println("           Same as resize, but allow animation.");
        pw.println("       resize-docked-stack <LEFT,TOP,RIGHT,BOTTOM> [<TASK_LEFT,TASK_TOP,TASK_RIGHT,TASK_BOTTOM>]");
        pw.println("           Change docked stack to <LEFT,TOP,RIGHT,BOTTOM>");
        pw.println("           and supplying temporary different task bounds indicated by");
        pw.println("           <TASK_LEFT,TOP,RIGHT,BOTTOM>");
        pw.println("       move-top-activity-to-pinned-stack: <STACK_ID> <LEFT,TOP,RIGHT,BOTTOM>");
        pw.println("           Moves the top activity from");
        pw.println("           <STACK_ID> to the pinned stack using <LEFT,TOP,RIGHT,BOTTOM> for the");
        pw.println("           bounds of the pinned stack.");
        pw.println("       positiontask <TASK_ID> <STACK_ID> <POSITION>");
        pw.println("           Place <TASK_ID> in <STACK_ID> at <POSITION>");
        pw.println("       list");
        pw.println("           List all of the activity stacks and their sizes.");
        pw.println("       info <WINDOWING_MODE> <ACTIVITY_TYPE>");
        pw.println("           Display the information about activity stack in <WINDOWING_MODE> and <ACTIVITY_TYPE>.");
        pw.println("       remove <STACK_ID>");
        pw.println("           Remove stack <STACK_ID>.");
        pw.println("  task [COMMAND] [...]: sub-commands for operating on activity tasks.");
        pw.println("       lock <TASK_ID>");
        pw.println("           Bring <TASK_ID> to the front and don't allow other tasks to run.");
        pw.println("       lock stop");
        pw.println("           End the current task lock.");
        pw.println("       resizeable <TASK_ID> [0|1|2|3]");
        pw.println("           Change resizeable mode of <TASK_ID> to one of the following:");
        pw.println("           0: unresizeable");
        pw.println("           1: crop_windows");
        pw.println("           2: resizeable");
        pw.println("           3: resizeable_and_pipable");
        pw.println("       resize <TASK_ID> <LEFT,TOP,RIGHT,BOTTOM>");
        pw.println("           Makes sure <TASK_ID> is in a stack with the specified bounds.");
        pw.println("           Forces the task to be resizeable and creates a stack if no existing stack");
        pw.println("           has the specified bounds.");
        pw.println("  update-appinfo <USER_ID> <PACKAGE_NAME> [<PACKAGE_NAME>...]");
        pw.println("      Update the ApplicationInfo objects of the listed packages for <USER_ID>");
        pw.println("      without restarting any processes.");
        pw.println("  write");
        pw.println("      Write all pending state to storage.");
        pw.println();
        Intent.printIntentArgsHelp(pw, "");
    }
}
