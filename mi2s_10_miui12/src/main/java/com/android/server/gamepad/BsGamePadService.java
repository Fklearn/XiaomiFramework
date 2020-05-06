package com.android.server.gamepad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.gamepad.IBsGamePadService;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.server.am.SplitScreenReporter;
import android.util.Log;
import com.android.server.LocalServices;
import com.android.server.pm.Settings;
import com.android.server.wm.WindowManagerInternal;
import java.util.Arrays;
import java.util.Map;
import miui.process.ForegroundInfo;
import miui.process.IForegroundInfoListener;
import miui.process.ProcessManager;

public class BsGamePadService extends IBsGamePadService.Stub {
    private static final boolean DEBUG = true;
    private static final String TAG = "BsGamePadService";
    private static final int TYPE_LOAD_KEYMAP = 0;
    private static final int TYPE_UNLOAD_KEYMAP = 1;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public boolean mEnableAppSwitch;
    /* access modifiers changed from: private */
    public int mGameControllerUid;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int i = msg.what;
            boolean isChooseMove = false;
            if (i == 0) {
                Map mapper = (Map) msg.obj;
                if (msg.arg1 > 0) {
                    isChooseMove = true;
                }
                BsGamePadService.this.updateGamePadFilter(true);
                if (BsGamePadService.this.mInputFilter != null) {
                    BsGamePadService.this.mInputFilter.loadKeyMap(mapper, isChooseMove, msg.arg2);
                }
            } else if (i == 1) {
                if (BsGamePadService.this.mInputFilter != null) {
                    BsGamePadService.this.mInputFilter.unloadKeyMap();
                }
                BsGamePadService.this.updateGamePadFilter(false);
            }
        }
    };
    /* access modifiers changed from: private */
    public BsGamePadInputFilter mInputFilter;
    private PackageManager mPackageManager;
    private BroadcastReceiver mPackageReceiver;
    private BroadcastReceiver mScreenReceiver;
    private WindowManagerInternal mWindowManagerService;

    public BsGamePadService(Context context) {
        this.mContext = context;
        this.mEnableAppSwitch = false;
    }

    public void systemReady() {
        this.mWindowManagerService = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);
        this.mPackageManager = this.mContext.getPackageManager();
        ProcessManager.registerForegroundInfoListener(new IForegroundInfoListener.Stub() {
            public void onForegroundInfoChanged(ForegroundInfo foregroundInfo) {
                if (BsGamePadService.this.mEnableAppSwitch) {
                    Intent intent = new Intent("com.blackshark.gamecontroller.APP_SWITCH");
                    intent.setPackage("com.blackshark.gamecontroller");
                    intent.putExtra(SplitScreenReporter.STR_PKG, foregroundInfo.mForegroundPackageName);
                    BsGamePadService.this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
                }
            }
        });
        this.mPackageReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int uid = intent.getIntExtra("android.intent.extra.UID", -1);
                if ("com.blackshark.gamecontroller".equals(intent.getData() != null ? intent.getData().getSchemeSpecificPart() : null) && BsGamePadService.this.mGameControllerUid == uid) {
                    boolean unused = BsGamePadService.this.mEnableAppSwitch = false;
                    if (BsGamePadService.this.mInputFilter != null) {
                        BsGamePadService.this.mInputFilter.unloadKeyMap();
                    }
                    BsGamePadService.this.updateGamePadFilter(false);
                }
            }
        };
        IntentFilter packageFilter = new IntentFilter();
        packageFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        packageFilter.addDataScheme(Settings.ATTR_PACKAGE);
        this.mContext.registerReceiverAsUser(this.mPackageReceiver, UserHandle.ALL, packageFilter, (String) null, (Handler) null);
        this.mScreenReceiver = new BroadcastReceiver() {
            /* JADX WARNING: Removed duplicated region for block: B:12:0x002b A[ADDED_TO_REGION] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onReceive(android.content.Context r6, android.content.Intent r7) {
                /*
                    r5 = this;
                    java.lang.String r0 = r7.getAction()
                    int r1 = r0.hashCode()
                    r2 = -2128145023(0xffffffff81271581, float:-3.0688484E-38)
                    java.lang.String r3 = "android.intent.action.SCREEN_ON"
                    r4 = 1
                    if (r1 == r2) goto L_0x001e
                    r2 = -1454123155(0xffffffffa953d76d, float:-4.7038264E-14)
                    if (r1 == r2) goto L_0x0016
                L_0x0015:
                    goto L_0x0028
                L_0x0016:
                    boolean r0 = r0.equals(r3)
                    if (r0 == 0) goto L_0x0015
                    r0 = r4
                    goto L_0x0029
                L_0x001e:
                    java.lang.String r1 = "android.intent.action.SCREEN_OFF"
                    boolean r0 = r0.equals(r1)
                    if (r0 == 0) goto L_0x0015
                    r0 = 0
                    goto L_0x0029
                L_0x0028:
                    r0 = -1
                L_0x0029:
                    if (r0 == 0) goto L_0x002e
                    if (r0 == r4) goto L_0x002e
                    goto L_0x0047
                L_0x002e:
                    com.android.server.gamepad.BsGamePadService r0 = com.android.server.gamepad.BsGamePadService.this
                    com.android.server.gamepad.BsGamePadInputFilter r0 = r0.mInputFilter
                    if (r0 == 0) goto L_0x0047
                    com.android.server.gamepad.BsGamePadService r0 = com.android.server.gamepad.BsGamePadService.this
                    com.android.server.gamepad.BsGamePadInputFilter r0 = r0.mInputFilter
                    java.lang.String r1 = r7.getAction()
                    boolean r1 = r3.equals(r1)
                    r0.enableKeyMap(r1)
                L_0x0047:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.server.gamepad.BsGamePadService.AnonymousClass4.onReceive(android.content.Context, android.content.Intent):void");
            }
        };
        IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction("android.intent.action.SCREEN_OFF");
        screenFilter.addAction("android.intent.action.SCREEN_ON");
        this.mContext.registerReceiverAsUser(this.mScreenReceiver, UserHandle.ALL, screenFilter, (String) null, (Handler) null);
        Log.d(TAG, "BsGamePadService is ready!");
    }

    /* access modifiers changed from: private */
    public void updateGamePadFilter(boolean enable) {
        if (!enable) {
            this.mInputFilter = null;
        } else if (this.mInputFilter == null) {
            this.mInputFilter = new BsGamePadInputFilter(this.mContext);
        }
        this.mWindowManagerService.setBsInputFilter(this.mInputFilter);
    }

    private boolean checkPackagePermission(int uid) {
        String packageName = this.mPackageManager.getNameForUid(uid);
        if (!"com.blackshark.gamecontroller".equals(packageName)) {
            return false;
        }
        try {
            byte[] signByteArray = this.mPackageManager.getPackageInfo(packageName, 64).signatures[0].toByteArray();
            if (signByteArray.length == 1023 && Arrays.hashCode(signByteArray) == 195760992) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void setAppSwitch(boolean enable) {
        Log.d(TAG, "enable = " + enable);
        if (checkPackagePermission(Binder.getCallingUid())) {
            this.mEnableAppSwitch = enable;
            this.mGameControllerUid = Binder.getCallingUid();
        }
    }

    public void loadKeyMap(Map mapper, boolean isChooseMove, int rotation) {
        Log.d(TAG, "mapper.size() = " + mapper.size());
        Log.d(TAG, "isChooseMove = " + isChooseMove);
        if (checkPackagePermission(Binder.getCallingUid())) {
            Message msg = this.mHandler.obtainMessage(0);
            msg.obj = mapper;
            msg.arg1 = isChooseMove;
            msg.arg2 = rotation;
            this.mHandler.sendMessage(msg);
        }
    }

    public void unloadKeyMap() {
        if (checkPackagePermission(Binder.getCallingUid())) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1));
        }
    }
}
