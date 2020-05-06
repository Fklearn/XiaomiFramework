package com.miui.server;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.miui.R;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManagerPolicyConstants;
import android.widget.Toast;
import com.android.server.UiThread;
import com.android.server.policy.PhoneWindowManager;

public class MiuiPointerEventListener implements WindowManagerPolicyConstants.PointerEventListener {
    private static final int GESTURE_FINGER_COUNT = 3;
    private static final String TAG = "MiuiPointerEventListener";
    private static final int THREE_GESTURE_STATE_DETECTED_FALSE = 2;
    private static final int THREE_GESTURE_STATE_DETECTED_TRUE = 3;
    private static final int THREE_GESTURE_STATE_DETECTING = 1;
    private static final int THREE_GESTURE_STATE_NONE = 0;
    private static final int THREE_GESTURE_STATE_NO_DETECT = 4;
    private boolean mBootCompleted;
    /* access modifiers changed from: private */
    public Context mContext;
    private boolean mDeviceProvisioned = false;
    private Intent mDumpLogIntent;
    /* access modifiers changed from: private */
    public boolean mEnable;
    private float[] mInitMotionY;
    private int[] mPointerIds;
    private String mThreeGestureAction;
    private int mThreeGestureState;
    private int mThreeGestureThreshold;
    private int mThreshold;

    public MiuiPointerEventListener(Context context) {
        int i = 3;
        this.mPointerIds = new int[3];
        this.mInitMotionY = new float[3];
        this.mEnable = true;
        this.mContext = context;
        this.mThreshold = (int) (context.getResources().getDisplayMetrics().density * 50.0f);
        this.mThreeGestureThreshold = this.mThreshold * 3;
        this.mThreeGestureState = !SystemProperties.getBoolean("sys.miui.screenshot", false) ? 0 : i;
        Settings.System.putInt(this.mContext.getContentResolver(), "enable_three_gesture", 1);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("enable_three_gesture"), false, new ContentObserver(UiThread.getHandler()) {
            public void onChange(boolean selfChange) {
                MiuiPointerEventListener miuiPointerEventListener = MiuiPointerEventListener.this;
                boolean z = true;
                if (Settings.System.getInt(miuiPointerEventListener.mContext.getContentResolver(), "enable_three_gesture", 1) != 1) {
                    z = false;
                }
                boolean unused = miuiPointerEventListener.mEnable = z;
            }
        }, -1);
    }

    public void onPointerEvent(MotionEvent event) {
        processMotionEventForThreeGestureDetect(event);
    }

    private void processMotionEventForThreeGestureDetect(MotionEvent event) {
        if (!this.mBootCompleted) {
            this.mBootCompleted = SystemProperties.getBoolean("sys.boot_completed", false);
            if (!this.mBootCompleted) {
                return;
            }
        }
        if (!this.mDeviceProvisioned) {
            this.mDeviceProvisioned = Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) != 0;
            if (!this.mDeviceProvisioned) {
                return;
            }
        }
        if (this.mEnable) {
            if (event.getAction() == 0) {
                changeThreeGestureState(0);
            } else if (this.mThreeGestureState == 0 && event.getPointerCount() == 3) {
                if (!checkIsStartThreeGesture(event)) {
                    changeThreeGestureState(4);
                } else {
                    changeThreeGestureState(1);
                    for (int i = 0; i < 3; i++) {
                        this.mPointerIds[i] = event.getPointerId(i);
                        this.mInitMotionY[i] = event.getY(i);
                    }
                }
            }
            int i2 = this.mThreeGestureState;
            if (i2 != 4 && i2 != 0 && i2 == 1) {
                if (event.getPointerCount() != 3) {
                    changeThreeGestureState(2);
                } else if (event.getActionMasked() == 2) {
                    float distance = 0.0f;
                    int i3 = 0;
                    while (i3 < 3) {
                        int index = event.findPointerIndex(this.mPointerIds[i3]);
                        if (index < 0 || index >= 3) {
                            changeThreeGestureState(2);
                            return;
                        } else {
                            distance += event.getY(index) - this.mInitMotionY[i3];
                            i3++;
                        }
                    }
                    if (distance >= ((float) this.mThreeGestureThreshold)) {
                        changeThreeGestureState(3);
                        takeThreeGestureAction();
                    }
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0013, code lost:
        if (r4 != 4) goto L_0x0022;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void changeThreeGestureState(int r4) {
        /*
            r3 = this;
            int r0 = r3.mThreeGestureState
            if (r0 != r4) goto L_0x0005
            return
        L_0x0005:
            java.lang.String r0 = "sys.miui.screenshot"
            if (r4 == 0) goto L_0x001c
            r1 = 1
            if (r4 == r1) goto L_0x0016
            r1 = 2
            if (r4 == r1) goto L_0x001c
            r1 = 3
            if (r4 == r1) goto L_0x0016
            r1 = 4
            if (r4 == r1) goto L_0x001c
            goto L_0x0022
        L_0x0016:
            java.lang.String r1 = "true"
            android.os.SystemProperties.set(r0, r1)     // Catch:{ RuntimeException -> 0x0025 }
            goto L_0x0022
        L_0x001c:
            java.lang.String r1 = "false"
            android.os.SystemProperties.set(r0, r1)     // Catch:{ RuntimeException -> 0x0025 }
        L_0x0022:
            r3.mThreeGestureState = r4     // Catch:{ RuntimeException -> 0x0025 }
            goto L_0x002d
        L_0x0025:
            r0 = move-exception
            java.lang.String r1 = "MiuiPointerEventListener"
            java.lang.String r2 = "RuntimeException when setprop"
            android.util.Slog.w(r1, r2, r0)
        L_0x002d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.MiuiPointerEventListener.changeThreeGestureState(int):void");
    }

    private boolean checkIsStartThreeGesture(MotionEvent event) {
        if (event.getEventTime() - event.getDownTime() > 500) {
            return false;
        }
        float maxY = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float minX = Float.MAX_VALUE;
        DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
        int hight = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        for (int i = 0; i < event.getPointerCount(); i++) {
            float x = event.getX(i);
            float y = event.getY(i);
            if (y > ((float) (hight - this.mThreshold))) {
                return false;
            }
            maxX = Math.max(maxX, x);
            minX = Math.min(minX, x);
            maxY = Math.max(maxY, y);
            minY = Math.min(minY, y);
        }
        if (maxY - minY <= displayMetrics.density * 150.0f) {
            if (maxX - minX > ((float) (width < hight ? width : hight))) {
                return false;
            }
            return true;
        }
        return false;
    }

    private void takeThreeGestureAction() {
        if ("screen_shot".equals(this.mThreeGestureAction)) {
            takeScreenshot();
        } else if ("dump_log".equals(this.mThreeGestureAction)) {
            this.mContext.sendBroadcastAsUser(getDumpLogIntent(), UserHandle.CURRENT);
            Context context = this.mContext;
            Toast toast = Toast.makeText(context, context.getString(R.string.start_dump_log), 0);
            toast.getWindowParams().privateFlags |= 16;
            toast.show();
        }
    }

    private void takeScreenshot() {
        this.mContext.sendBroadcastAsUser(new Intent("android.intent.action.CAPTURE_SCREENSHOT"), UserHandle.CURRENT);
        sendRecordCountEvent(this.mContext, PhoneWindowManager.SYSTEM_DIALOG_REASON_SCREENSHOT, "threefingers");
        changeThreeGestureState(0);
    }

    private static void sendRecordCountEvent(Context context, String category, String event) {
        Intent intent = new Intent("com.miui.gallery.intent.action.SEND_STAT");
        intent.setPackage(AccessController.PACKAGE_GALLERY);
        intent.putExtra("stat_type", "count_event");
        intent.putExtra("category", category);
        intent.putExtra("event", event);
        context.sendBroadcast(intent);
    }

    public void setThreeGestureAction(String action) {
        this.mThreeGestureAction = action;
    }

    private Intent getDumpLogIntent() {
        if (this.mDumpLogIntent == null) {
            this.mDumpLogIntent = new Intent();
            this.mDumpLogIntent.setPackage("com.miui.bugreport");
            this.mDumpLogIntent.setAction("com.miui.bugreport.service.action.DUMPLOG");
        }
        return this.mDumpLogIntent;
    }
}
