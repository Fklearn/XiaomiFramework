package com.android.server;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.InputEvent;
import android.view.InputFilter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.android.server.usb.descriptors.UsbACInterface;
import com.android.server.usb.descriptors.UsbTerminalTypes;
import java.util.ArrayList;
import java.util.List;
import miui.util.FeatureParser;
import miui.util.HandyModeUtils;

public class MiuiInputFilter extends InputFilter {
    static int[][] ENTERED_LISTEN_COMBINATION_KEYS = null;
    private static int MIDDLE_KEYCODE = (isDpadDevice ? 23 : 3);
    static int[][] NOT_ENTERED_LISTEN_COMBINATION_KEYS = null;
    private static final String PERSIST_SYS_BACKTOUCH_PROPERTY = "persist.sys.backtouch";
    private static final String PERSIST_SYS_HANDSWAP_PROPERTY = "persist.sys.handswap";
    private static boolean isDpadDevice = FeatureParser.getBoolean("middle_keycode_is_dpad_center", false);
    private static float sEdgeDistance;
    private final double MAX_COS = Math.cos(0.3490658503988659d);
    private boolean mCitTestEnabled;
    private ClickableRect mClickingRect;
    private Context mContext;
    private H mHandler;
    private boolean mInstalled;
    private List<ClickableRect> mOutsideClickableRects = new ArrayList();
    private List<KeyData> mPendingKeys = new ArrayList();
    private ArrayList<PointF> mPoints = new ArrayList<>();
    private int mSampleDura;
    private MotionEvent.PointerCoords[] mTempPointerCoords;
    private MotionEvent.PointerProperties[] mTempPointerProperties;
    private boolean mWasInside;

    static {
        int i = MIDDLE_KEYCODE;
        NOT_ENTERED_LISTEN_COMBINATION_KEYS = new int[][]{new int[]{i, 4}, new int[]{i, 82}};
        ENTERED_LISTEN_COMBINATION_KEYS = new int[][]{new int[]{i, 4}, new int[]{i, 82}, new int[]{4, i}, new int[]{82, i}};
    }

    public boolean isInstalled() {
        return this.mInstalled;
    }

    static class KeyData {
        boolean isSended;
        KeyEvent keyEvent;
        int policyFlags;

        KeyData() {
        }
    }

    private class H extends Handler {
        public static final int MSG_DOUBLE_CLICK_DELAY = 1;

        public H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                MiuiInputFilter.this.flushPending();
            }
            super.handleMessage(msg);
        }
    }

    private static class ClickableRect {
        public Runnable mClickListener;
        public Rect mRect;

        public ClickableRect(Rect rect, Runnable listener) {
            this.mRect = rect;
            this.mClickListener = listener;
        }
    }

    public MiuiInputFilter(Context context) {
        super(DisplayThread.get().getLooper());
        this.mContext = context;
        this.mHandler = new H(DisplayThread.get().getLooper());
        sEdgeDistance = context.getResources().getDisplayMetrics().density * 20.0f;
    }

    public void addOutsideClickableRect(Rect rect, Runnable listener) {
        this.mOutsideClickableRects.add(new ClickableRect(rect, listener));
    }

    public void removeOutsideClickableRect(Runnable listener) {
        for (int i = this.mOutsideClickableRects.size() - 1; i >= 0; i--) {
            if (this.mOutsideClickableRects.get(i).mClickListener == listener) {
                this.mOutsideClickableRects.remove(i);
            }
        }
    }

    public void updateOutsideClickableRect(Rect rect, Runnable listener) {
        boolean containListener = false;
        for (int i = this.mOutsideClickableRects.size() - 1; i >= 0; i--) {
            if (this.mOutsideClickableRects.get(i).mClickListener == listener) {
                this.mOutsideClickableRects.remove(i);
                containListener = true;
            }
        }
        if (containListener) {
            this.mOutsideClickableRects.add(new ClickableRect(rect, listener));
        }
    }

    public void setCitTestEnabled(boolean enabled) {
        this.mCitTestEnabled = enabled;
    }

    private void processMotionEventForBackTouch(MotionEvent event, int policyFlags) {
        int action = event.getAction();
        if (action == 1) {
            this.mSampleDura = 0;
            this.mPoints.clear();
        } else if (action == 2) {
            PointF curPointF = new PointF(event.getRawX(), event.getRawY());
            int i = this.mSampleDura + 1;
            this.mSampleDura = i;
            if (i >= 5) {
                this.mPoints.add(curPointF);
                this.mSampleDura = 0;
            }
            if (this.mPoints.size() >= 3) {
                changeVolumeForBackTouch(policyFlags);
                this.mPoints.clear();
            }
        }
    }

    private void changeVolumeForBackTouch(int policyFlags) {
        int i = policyFlags;
        PointF firstP = this.mPoints.get(0);
        PointF secondP = this.mPoints.get(1);
        PointF thirdP = this.mPoints.get(2);
        float volumeChange = 0.0f;
        if (Math.abs(((double) (((secondP.x - firstP.x) * (thirdP.x - secondP.x)) + ((secondP.y - firstP.y) * (thirdP.y - secondP.y)))) / (Math.hypot((double) (secondP.x - firstP.x), (double) (secondP.y - firstP.y)) * Math.hypot((double) (thirdP.x - secondP.x), (double) (thirdP.y - secondP.y)))) < this.MAX_COS) {
            volumeChange = ((secondP.x - firstP.x) * (thirdP.y - secondP.y)) - ((thirdP.x - secondP.x) * (secondP.y - firstP.y));
        }
        if (volumeChange != 0.0f) {
            long time = SystemClock.uptimeMillis();
            sendInputEvent(new KeyEvent(time, time, 0, volumeChange > 0.0f ? 24 : 25, 0), i);
            long time2 = SystemClock.uptimeMillis();
            sendInputEvent(new KeyEvent(time2, time2, 1, volumeChange > 0.0f ? 24 : 25, 0), i);
        }
    }

    public void onInputEvent(InputEvent event, int policyFlags) {
        if (!(event instanceof MotionEvent) || !event.isFromSource(UsbACInterface.FORMAT_II_AC3)) {
            if (!(event instanceof KeyEvent) || !event.isFromSource(UsbTerminalTypes.TERMINAL_USB_STREAMING) || !HandyModeUtils.isFeatureVisible()) {
                MiuiInputFilter.super.onInputEvent(event, policyFlags);
            } else {
                onKeyEvent((KeyEvent) event, policyFlags);
            }
        } else if (event.getDevice() == null || !"backtouch".equals(event.getDevice().getName()) || this.mCitTestEnabled) {
            onMotionEvent((MotionEvent) event, policyFlags);
        } else {
            processMotionEventForBackTouch((MotionEvent) event, policyFlags);
        }
    }

    public void onInstalled() {
        MiuiInputFilter.super.onInstalled();
        this.mInstalled = true;
    }

    public void onUninstalled() {
        MiuiInputFilter.super.onUninstalled();
        this.mInstalled = false;
        clearPendingList();
    }

    static float processCoordinate(float coordValue, float offset, float scale, float scalePivot) {
        return (scalePivot - ((scalePivot - coordValue) * scale)) - offset;
    }

    private void onMotionEvent(MotionEvent event, int policyFlags) {
        if (HandyMode.sEnable) {
            processMotionEventForHandyMode(event, policyFlags);
        } else {
            MiuiInputFilter.super.onInputEvent(event, policyFlags);
        }
    }

    private void processMotionEventForHandyMode(MotionEvent event, int policyFlags) {
        MotionEvent motionEvent = event;
        int i = policyFlags;
        int mode = HandyMode.getMode();
        if (mode != 0) {
            float scaleInverse = 1.0f / HandyMode.sScale;
            int pointerCount = event.getPointerCount();
            MotionEvent.PointerCoords[] coords = getTempPointerCoordsWithMinSize(pointerCount);
            MotionEvent.PointerProperties[] properties = getTempPointerPropertiesWithMinSize(pointerCount);
            for (int i2 = 0; i2 < pointerCount; i2++) {
                motionEvent.getPointerCoords(i2, coords[i2]);
                coords[i2].x = processCoordinate(coords[i2].x, 0.0f, scaleInverse, mode == 2 ? (float) HandyMode.sScreenWidth : 0.0f);
                coords[i2].y = processCoordinate(coords[i2].y, 0.0f, scaleInverse, (float) HandyMode.sScreenHeight);
                motionEvent.getPointerProperties(i2, properties[i2]);
            }
            int pointerCount2 = pointerCount;
            MotionEvent newEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), pointerCount, properties, coords, event.getMetaState(), event.getButtonState(), event.getXPrecision(), event.getYPrecision(), event.getDeviceId(), event.getEdgeFlags(), event.getSource(), event.getFlags());
            if (pointerCount2 == 1) {
                if (newEvent.getAction() == 0) {
                    this.mWasInside = false;
                    this.mClickingRect = findClickableRect(event.getX(), event.getY());
                }
                if (this.mWasInside || isTouchInside(newEvent.getX(), newEvent.getY())) {
                    if (!this.mWasInside && newEvent.getAction() == 2) {
                        newEvent.setAction(0);
                    }
                    sendInputEvent(newEvent, i);
                    this.mWasInside = true;
                    this.mClickingRect = null;
                } else {
                    ClickableRect clickableRect = this.mClickingRect;
                    if (clickableRect != null && !clickableRect.mRect.contains((int) event.getX(), (int) event.getY())) {
                        this.mClickingRect = null;
                    }
                    if (newEvent.getAction() == 1) {
                        ClickableRect clickableRect2 = this.mClickingRect;
                        if (clickableRect2 != null) {
                            clickableRect2.mClickListener.run();
                        } else if (!isTouchInsideOrEdge(newEvent.getX(), newEvent.getY())) {
                            HandyMode.changeMode(0);
                        }
                    }
                }
            } else {
                sendInputEvent(newEvent, i);
                this.mWasInside = true;
                this.mClickingRect = null;
            }
            newEvent.recycle();
            return;
        }
        MiuiInputFilter.super.onInputEvent(event, policyFlags);
    }

    private ClickableRect findClickableRect(float x, float y) {
        for (ClickableRect c : this.mOutsideClickableRects) {
            if (c.mRect.contains((int) x, (int) y)) {
                return c;
            }
        }
        return null;
    }

    private boolean isTouchInside(float x, float y) {
        return x > 0.0f && x < ((float) HandyMode.sScreenWidth) && y > 0.0f && y < ((float) HandyMode.sScreenHeight);
    }

    private boolean isTouchInsideOrEdge(float x, float y) {
        if (x > (-sEdgeDistance)) {
            float f = sEdgeDistance;
            return x < ((float) HandyMode.sScreenWidth) + f && y > (-f) && y < ((float) HandyMode.sScreenHeight);
        }
    }

    private MotionEvent.PointerCoords[] getTempPointerCoordsWithMinSize(int size) {
        MotionEvent.PointerCoords[] pointerCoordsArr = this.mTempPointerCoords;
        int oldSize = pointerCoordsArr != null ? pointerCoordsArr.length : 0;
        if (oldSize < size) {
            MotionEvent.PointerCoords[] oldTempPointerCoords = this.mTempPointerCoords;
            this.mTempPointerCoords = new MotionEvent.PointerCoords[size];
            if (oldTempPointerCoords != null) {
                System.arraycopy(oldTempPointerCoords, 0, this.mTempPointerCoords, 0, oldSize);
            }
        }
        for (int i = oldSize; i < size; i++) {
            this.mTempPointerCoords[i] = new MotionEvent.PointerCoords();
        }
        return this.mTempPointerCoords;
    }

    private MotionEvent.PointerProperties[] getTempPointerPropertiesWithMinSize(int size) {
        MotionEvent.PointerProperties[] pointerPropertiesArr = this.mTempPointerProperties;
        int oldSize = pointerPropertiesArr != null ? pointerPropertiesArr.length : 0;
        if (oldSize < size) {
            MotionEvent.PointerProperties[] oldTempPointerProperties = this.mTempPointerProperties;
            this.mTempPointerProperties = new MotionEvent.PointerProperties[size];
            if (oldTempPointerProperties != null) {
                System.arraycopy(oldTempPointerProperties, 0, this.mTempPointerProperties, 0, oldSize);
            }
        }
        for (int i = oldSize; i < size; i++) {
            this.mTempPointerProperties[i] = new MotionEvent.PointerProperties();
        }
        return this.mTempPointerProperties;
    }

    private boolean needShowDialog() {
        return HandyMode.getMode() == 0 && !HandyMode.sHandyModeUtils.isEnterDirect();
    }

    private boolean needDelayKey(boolean isSecondKey) {
        return !needShowDialog() || isSecondKey;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0080, code lost:
        if (r7 == false) goto L_0x0082;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00e6, code lost:
        if (r3 != false) goto L_0x00e8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0135, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void onKeyEvent(android.view.KeyEvent r14, int r15) {
        /*
            r13 = this;
            monitor-enter(r13)
            boolean r0 = com.android.server.HandyMode.sEnable     // Catch:{ all -> 0x013b }
            if (r0 == 0) goto L_0x0136
            int r0 = r14.getKeyCode()     // Catch:{ all -> 0x013b }
            if (r0 != 0) goto L_0x000d
            goto L_0x0136
        L_0x000d:
            r0 = 0
            r1 = 0
            r2 = 0
            java.util.List<com.android.server.MiuiInputFilter$KeyData> r3 = r13.mPendingKeys     // Catch:{ all -> 0x013b }
            int r3 = r3.size()     // Catch:{ all -> 0x013b }
            r4 = 0
            r5 = 1
            if (r3 == 0) goto L_0x0102
            if (r3 == r5) goto L_0x00bd
            r6 = 2
            if (r3 == r6) goto L_0x0057
            r7 = 3
            if (r3 == r7) goto L_0x0024
            goto L_0x0127
        L_0x0024:
            java.util.List<com.android.server.MiuiInputFilter$KeyData> r3 = r13.mPendingKeys     // Catch:{ all -> 0x013b }
            java.lang.Object r3 = r3.get(r6)     // Catch:{ all -> 0x013b }
            com.android.server.MiuiInputFilter$KeyData r3 = (com.android.server.MiuiInputFilter.KeyData) r3     // Catch:{ all -> 0x013b }
            android.view.KeyEvent r3 = r3.keyEvent     // Catch:{ all -> 0x013b }
            int r6 = r14.getAction()     // Catch:{ all -> 0x013b }
            if (r6 != r5) goto L_0x0052
            int r6 = r14.getKeyCode()     // Catch:{ all -> 0x013b }
            int r7 = r3.getKeyCode()     // Catch:{ all -> 0x013b }
            if (r6 != r7) goto L_0x0052
            r1 = 1
            r0 = 1
            android.view.KeyEvent r7 = r14.copy()     // Catch:{ all -> 0x013b }
            r9 = -1
            r10 = 1
            if (r0 != 0) goto L_0x004a
            r11 = r5
            goto L_0x004b
        L_0x004a:
            r11 = r4
        L_0x004b:
            r6 = r13
            r8 = r15
            r6.addPendingData(r7, r8, r9, r10, r11)     // Catch:{ all -> 0x013b }
            goto L_0x0127
        L_0x0052:
            r13.flushPending()     // Catch:{ all -> 0x013b }
            goto L_0x0127
        L_0x0057:
            java.util.List<com.android.server.MiuiInputFilter$KeyData> r3 = r13.mPendingKeys     // Catch:{ all -> 0x013b }
            java.lang.Object r3 = r3.get(r4)     // Catch:{ all -> 0x013b }
            com.android.server.MiuiInputFilter$KeyData r3 = (com.android.server.MiuiInputFilter.KeyData) r3     // Catch:{ all -> 0x013b }
            android.view.KeyEvent r3 = r3.keyEvent     // Catch:{ all -> 0x013b }
            java.util.List<com.android.server.MiuiInputFilter$KeyData> r6 = r13.mPendingKeys     // Catch:{ all -> 0x013b }
            java.lang.Object r6 = r6.get(r5)     // Catch:{ all -> 0x013b }
            com.android.server.MiuiInputFilter$KeyData r6 = (com.android.server.MiuiInputFilter.KeyData) r6     // Catch:{ all -> 0x013b }
            android.view.KeyEvent r6 = r6.keyEvent     // Catch:{ all -> 0x013b }
            int r7 = r6.getAction()     // Catch:{ all -> 0x013b }
            if (r7 != r5) goto L_0x0082
            int r7 = r14.getAction()     // Catch:{ all -> 0x013b }
            if (r7 != 0) goto L_0x0082
            int r7 = r14.getKeyCode()     // Catch:{ all -> 0x013b }
            boolean r7 = r13.checkSecondKey(r7)     // Catch:{ all -> 0x013b }
            r2 = r7
            if (r7 != 0) goto L_0x0098
        L_0x0082:
            int r7 = r6.getAction()     // Catch:{ all -> 0x013b }
            if (r7 != 0) goto L_0x00b8
            int r7 = r14.getAction()     // Catch:{ all -> 0x013b }
            if (r7 != r5) goto L_0x00b8
            int r7 = r14.getKeyCode()     // Catch:{ all -> 0x013b }
            int r8 = r3.getKeyCode()     // Catch:{ all -> 0x013b }
            if (r7 != r8) goto L_0x00b8
        L_0x0098:
            boolean r7 = r13.needDelayKey(r2)     // Catch:{ all -> 0x013b }
            r0 = r7
            android.view.KeyEvent r8 = r14.copy()     // Catch:{ all -> 0x013b }
            int r7 = r14.getAction()     // Catch:{ all -> 0x013b }
            if (r7 != 0) goto L_0x00aa
            r7 = -1
            r10 = r7
            goto L_0x00ab
        L_0x00aa:
            r10 = r5
        L_0x00ab:
            r11 = 1
            if (r0 != 0) goto L_0x00b0
            r12 = r5
            goto L_0x00b1
        L_0x00b0:
            r12 = r4
        L_0x00b1:
            r7 = r13
            r9 = r15
            r7.addPendingData(r8, r9, r10, r11, r12)     // Catch:{ all -> 0x013b }
            goto L_0x0127
        L_0x00b8:
            r13.flushPending()     // Catch:{ all -> 0x013b }
            goto L_0x0127
        L_0x00bd:
            int r3 = r14.getAction()     // Catch:{ all -> 0x013b }
            if (r3 != r5) goto L_0x00d7
            int r3 = r14.getKeyCode()     // Catch:{ all -> 0x013b }
            java.util.List<com.android.server.MiuiInputFilter$KeyData> r6 = r13.mPendingKeys     // Catch:{ all -> 0x013b }
            java.lang.Object r6 = r6.get(r4)     // Catch:{ all -> 0x013b }
            com.android.server.MiuiInputFilter$KeyData r6 = (com.android.server.MiuiInputFilter.KeyData) r6     // Catch:{ all -> 0x013b }
            android.view.KeyEvent r6 = r6.keyEvent     // Catch:{ all -> 0x013b }
            int r6 = r6.getKeyCode()     // Catch:{ all -> 0x013b }
            if (r3 == r6) goto L_0x00e8
        L_0x00d7:
            int r3 = r14.getAction()     // Catch:{ all -> 0x013b }
            if (r3 != 0) goto L_0x00fe
            int r3 = r14.getKeyCode()     // Catch:{ all -> 0x013b }
            boolean r3 = r13.checkSecondKey(r3)     // Catch:{ all -> 0x013b }
            r2 = r3
            if (r3 == 0) goto L_0x00fe
        L_0x00e8:
            boolean r3 = r13.needDelayKey(r2)     // Catch:{ all -> 0x013b }
            r0 = r3
            android.view.KeyEvent r7 = r14.copy()     // Catch:{ all -> 0x013b }
            r9 = -1
            r10 = 0
            if (r0 != 0) goto L_0x00f7
            r11 = r5
            goto L_0x00f8
        L_0x00f7:
            r11 = r4
        L_0x00f8:
            r6 = r13
            r8 = r15
            r6.addPendingData(r7, r8, r9, r10, r11)     // Catch:{ all -> 0x013b }
            goto L_0x0127
        L_0x00fe:
            r13.flushPending()     // Catch:{ all -> 0x013b }
            goto L_0x0127
        L_0x0102:
            int r3 = r14.getAction()     // Catch:{ all -> 0x013b }
            if (r3 != 0) goto L_0x0127
            int r3 = r14.getKeyCode()     // Catch:{ all -> 0x013b }
            boolean r3 = r13.checkKeyNeedListen(r3)     // Catch:{ all -> 0x013b }
            if (r3 == 0) goto L_0x0127
            boolean r3 = r13.needDelayKey(r4)     // Catch:{ all -> 0x013b }
            r0 = r3
            android.view.KeyEvent r7 = r14.copy()     // Catch:{ all -> 0x013b }
            r9 = -1
            r10 = 1
            if (r0 != 0) goto L_0x0121
            r11 = r5
            goto L_0x0122
        L_0x0121:
            r11 = r4
        L_0x0122:
            r6 = r13
            r8 = r15
            r6.addPendingData(r7, r8, r9, r10, r11)     // Catch:{ all -> 0x013b }
        L_0x0127:
            if (r0 != 0) goto L_0x012c
            com.android.server.MiuiInputFilter.super.onInputEvent(r14, r15)     // Catch:{ all -> 0x013b }
        L_0x012c:
            if (r1 == 0) goto L_0x0134
            r13.triggerCombinationClick()     // Catch:{ all -> 0x013b }
            r13.clearPendingList()     // Catch:{ all -> 0x013b }
        L_0x0134:
            monitor-exit(r13)
            return
        L_0x0136:
            com.android.server.MiuiInputFilter.super.onInputEvent(r14, r15)     // Catch:{ all -> 0x013b }
            monitor-exit(r13)
            return
        L_0x013b:
            r14 = move-exception
            monitor-exit(r13)
            throw r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.MiuiInputFilter.onKeyEvent(android.view.KeyEvent, int):void");
    }

    /* access modifiers changed from: package-private */
    public boolean checkKeyNeedListen(int keyCode) {
        for (int[] iArr : getListenCombinationKeys()) {
            if (iArr[0] == keyCode) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public int[][] getListenCombinationKeys() {
        if (HandyMode.getMode() == 0) {
            return NOT_ENTERED_LISTEN_COMBINATION_KEYS;
        }
        return ENTERED_LISTEN_COMBINATION_KEYS;
    }

    /* access modifiers changed from: package-private */
    public boolean checkSecondKey(int secondKeyCode) {
        int[][] listenCombinationKeys = getListenCombinationKeys();
        int firstKeyCode = this.mPendingKeys.get(0).keyEvent.getKeyCode();
        for (int[] keySequence : listenCombinationKeys) {
            if (keySequence[0] == firstKeyCode && keySequence[1] == secondKeyCode) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0060, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void triggerCombinationClick() {
        /*
            r7 = this;
            monitor-enter(r7)
            android.content.Context r0 = com.android.server.HandyMode.sContext     // Catch:{ all -> 0x0061 }
            android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x0061 }
            java.lang.String r1 = "screen_buttons_state"
            r2 = 0
            int r0 = android.provider.Settings.Secure.getInt(r0, r1, r2)     // Catch:{ all -> 0x0061 }
            if (r0 == 0) goto L_0x0013
            monitor-exit(r7)
            return
        L_0x0013:
            java.lang.String r0 = "persist.sys.handswap"
            java.lang.String r1 = "0"
            java.lang.String r0 = android.os.SystemProperties.get(r0, r1)     // Catch:{ all -> 0x0061 }
            java.lang.String r1 = "1"
            boolean r1 = r1.equals(r0)     // Catch:{ all -> 0x0061 }
            r2 = 0
            java.util.List<com.android.server.MiuiInputFilter$KeyData> r3 = r7.mPendingKeys     // Catch:{ all -> 0x0061 }
            r4 = 2
            java.lang.Object r3 = r3.get(r4)     // Catch:{ all -> 0x0061 }
            com.android.server.MiuiInputFilter$KeyData r3 = (com.android.server.MiuiInputFilter.KeyData) r3     // Catch:{ all -> 0x0061 }
            android.view.KeyEvent r3 = r3.keyEvent     // Catch:{ all -> 0x0061 }
            int r3 = r3.getKeyCode()     // Catch:{ all -> 0x0061 }
            r5 = 4
            r6 = 1
            if (r3 != r5) goto L_0x003b
            if (r1 == 0) goto L_0x0039
            r4 = r6
        L_0x0039:
            r2 = r4
            goto L_0x0052
        L_0x003b:
            java.util.List<com.android.server.MiuiInputFilter$KeyData> r3 = r7.mPendingKeys     // Catch:{ all -> 0x0061 }
            java.lang.Object r3 = r3.get(r4)     // Catch:{ all -> 0x0061 }
            com.android.server.MiuiInputFilter$KeyData r3 = (com.android.server.MiuiInputFilter.KeyData) r3     // Catch:{ all -> 0x0061 }
            android.view.KeyEvent r3 = r3.keyEvent     // Catch:{ all -> 0x0061 }
            int r3 = r3.getKeyCode()     // Catch:{ all -> 0x0061 }
            r5 = 82
            if (r3 != r5) goto L_0x0052
            if (r1 == 0) goto L_0x0050
            goto L_0x0051
        L_0x0050:
            r4 = r6
        L_0x0051:
            r2 = r4
        L_0x0052:
            boolean r3 = r7.needShowDialog()     // Catch:{ all -> 0x0061 }
            if (r3 == 0) goto L_0x005c
            com.android.server.HandyMode.alertToEnter(r2)     // Catch:{ all -> 0x0061 }
            goto L_0x005f
        L_0x005c:
            com.android.server.HandyMode.changeMode(r2)     // Catch:{ all -> 0x0061 }
        L_0x005f:
            monitor-exit(r7)
            return
        L_0x0061:
            r0 = move-exception
            monitor-exit(r7)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.MiuiInputFilter.triggerCombinationClick():void");
    }

    /* access modifiers changed from: package-private */
    public synchronized void flushPending() {
        for (int i = 0; i < this.mPendingKeys.size(); i++) {
            KeyData keyData = this.mPendingKeys.get(i);
            if (!keyData.isSended) {
                sendInputEvent(keyData.keyEvent, this.mPendingKeys.get(i).policyFlags);
            }
        }
        clearPendingList();
    }

    /* access modifiers changed from: package-private */
    public synchronized void addPendingData(KeyEvent keyEvent, int policyFlags, int index, boolean delayEnhance, boolean isSended) {
        this.mHandler.removeMessages(1);
        KeyData keyData = new KeyData();
        keyData.keyEvent = keyEvent;
        keyData.policyFlags = policyFlags;
        keyData.isSended = isSended;
        if (index < 0) {
            this.mPendingKeys.add(keyData);
        } else {
            this.mPendingKeys.add(index, keyData);
        }
        this.mHandler.sendEmptyMessageDelayed(1, (long) (HandyMode.COMBINATION_CLICK_TIMEOUT * (delayEnhance ? 2 : 1)));
    }

    /* access modifiers changed from: package-private */
    public synchronized void clearPendingList() {
        this.mHandler.removeMessages(1);
        this.mPendingKeys.clear();
    }
}
