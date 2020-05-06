package com.android.server.gamepad;

import android.content.Context;
import android.gamepad.BsGameKeyMap;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.Log;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.android.server.usb.descriptors.UsbACInterface;
import com.android.server.usb.descriptors.UsbTerminalTypes;
import java.util.ArrayList;
import java.util.Map;

public class BsGamePadWorker {
    private static final String BSGAMEPAD_MOUSE_SUPPORT_SETTING = "bsgamepad_mouse_support";
    private static final boolean DEBUG = true;
    /* access modifiers changed from: private */
    public static boolean DEBUG_FOR_CHOOSE_MOVE = true;
    private static final boolean DEBUG_FOR_DELAY_MOVE = false;
    private static final boolean DEBUG_FOR_MULTIKEY_CONFLICT = false;
    private static final boolean DEBUG_FOR_SPLIT_MOTIONEVENT = false;
    public static final int DIRECTIONPAD_COUNT = 3;
    public static final int DIRECTION_PAD_HAT = 12;
    public static final int DIRECTION_PAD_L = 10;
    public static final int DIRECTION_PAD_R = 11;
    public static final int GAMEBUTTON_BUTTON_A = 2;
    public static final int GAMEBUTTON_BUTTON_B = 3;
    public static final int GAMEBUTTON_BUTTON_L1 = 6;
    public static final int GAMEBUTTON_BUTTON_L2 = 8;
    public static final int GAMEBUTTON_BUTTON_R1 = 7;
    public static final int GAMEBUTTON_BUTTON_R2 = 9;
    public static final int GAMEBUTTON_BUTTON_X = 4;
    public static final int GAMEBUTTON_BUTTON_Y = 5;
    public static final int GAMEBUTTON_COUNT = 10;
    public static final int GAMEBUTTON_MASK = 1023;
    public static final int GAMEBUTTON_SELECT = 1;
    public static final int GAMEBUTTON_START = 0;
    public static final int GAMEPAD_CONTROL_COUNT = 13;
    public static final int INVALID_POINTER_ID = -1;
    public static final int MAX_POINTER_COUNT = 16;
    public static final int MAX_POINTER_ID_COUNT = 32;
    public static final int MOUSE_BUTTON_LEFT = 1;
    public static final int MOUSE_BUTTON_MIDDLE = 128;
    public static final int MOUSE_BUTTON_RIGHT = 8;
    public static final int MOUSE_TRACKER = 13;
    private static final int MOVE_TIMEOUT = 45;
    private static final int MOVE_UP_TIMEOUT = 60;
    private static final String TAG = "BsGamePadWorker";
    public static final int TP_ACTION_BUTTON = 0;
    private static final int TYPE_ENABLE_SEND_MOVE = 2;
    private static final int TYPE_MOUSE_MOVE_UP = 3;
    private static final int TYPE_SEND_MOVE = 1;
    private static final int TYPE_SEND_MOVE_UP = 0;
    private Context mContext;
    private boolean mEnableKeyMap;
    private long mEnableSendMoveBitMap = 0;
    private long mGameButtonStatesBitMap = 0;
    /* access modifiers changed from: private */
    public final ArrayMap<Integer, BsGameKeyMap> mGamePadMapper = new ArrayMap<>();
    private int mGamePadRotation;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int touchid;
            int i = msg.what;
            if (i == 0) {
                BsGamePadWorker.this.fire(BsGamePadWorker.this.buildMotionEvent(BsGamePadWorker.this.insertTouch((Pos) msg.obj), 1));
                BsGamePadWorker.access$374(BsGamePadWorker.this, (long) (~(1 << msg.arg1)));
                if (BsGamePadWorker.DEBUG_FOR_CHOOSE_MOVE) {
                    BsGamePadWorker.access$574(BsGamePadWorker.this, (long) (~(1 << (msg.arg1 - 10))));
                }
            } else if (i == 1) {
                BsGamePadWorker.this.fire(BsGamePadWorker.this.buildMotionEvent(msg.arg1, 2));
            } else if (i == 2) {
                BsGamePadWorker.access$578(BsGamePadWorker.this, (long) (1 << msg.arg1));
                if (BsGamePadWorker.this.mJoystickLastPos[msg.arg1] != null) {
                    BsGamePadWorker bsGamePadWorker = BsGamePadWorker.this;
                    BsGamePadWorker.this.fire(BsGamePadWorker.this.buildMotionEvent(bsGamePadWorker.insertTouch(bsGamePadWorker.mJoystickLastPos[msg.arg1]), 2));
                }
            } else if (i == 3 && (touchid = msg.arg1) >= 0 && touchid < BsGamePadWorker.this.mGamePadMapper.size()) {
                BsGamePadWorker bsGamePadWorker2 = BsGamePadWorker.this;
                Pos mouseUpPos = new Pos(((BsGameKeyMap) bsGamePadWorker2.mGamePadMapper.valueAt(touchid)).getCenterX(), ((BsGameKeyMap) BsGamePadWorker.this.mGamePadMapper.valueAt(touchid)).getCenterY());
                mouseUpPos.setTouchId(touchid);
                BsGamePadWorker.this.fire(BsGamePadWorker.this.buildMotionEvent(BsGamePadWorker.this.insertTouch(mouseUpPos), 1));
                BsGamePadWorker.access$374(BsGamePadWorker.this, -8193);
            }
        }
    };
    /* access modifiers changed from: private */
    public Pos[] mJoystickLastPos;
    private float mJoystickLastX;
    private float mJoystickLastY;
    private int mMainDirectionPad;
    private boolean mMousePointerVisibility;
    private boolean mMouseSupport;
    private float mMouseTrackerLastX;
    private float mMouseTrackerLastY;
    private Pos[] mTouchList;

    static /* synthetic */ long access$374(BsGamePadWorker x0, long x1) {
        long j = x0.mGameButtonStatesBitMap & x1;
        x0.mGameButtonStatesBitMap = j;
        return j;
    }

    static /* synthetic */ long access$574(BsGamePadWorker x0, long x1) {
        long j = x0.mEnableSendMoveBitMap & x1;
        x0.mEnableSendMoveBitMap = j;
        return j;
    }

    static /* synthetic */ long access$578(BsGamePadWorker x0, long x1) {
        long j = x0.mEnableSendMoveBitMap | x1;
        x0.mEnableSendMoveBitMap = j;
        return j;
    }

    protected static float getCenteredAxis(MotionEvent event, InputDevice device, int axis, int historyPos) {
        InputDevice.MotionRange range;
        if (!(device == null || event == null || (range = device.getMotionRange(axis, event.getSource())) == null)) {
            float flat = range.getFlat();
            float value = historyPos < 0 ? event.getAxisValue(axis) : event.getHistoricalAxisValue(axis, historyPos);
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0.0f;
    }

    private class Pos {
        private boolean fromTouch;
        private MotionEvent.PointerCoords pointerCoords;
        private int touchId;

        public Pos() {
            this.pointerCoords = new MotionEvent.PointerCoords();
            reset();
        }

        public Pos(float x, float y) {
            this.pointerCoords = new MotionEvent.PointerCoords();
            this.fromTouch = false;
            this.touchId = -1;
            MotionEvent.PointerCoords pointerCoords2 = this.pointerCoords;
            pointerCoords2.x = x;
            pointerCoords2.y = y;
        }

        public void reset() {
            this.fromTouch = false;
            this.touchId = -1;
            this.pointerCoords.clear();
        }

        public boolean isValid() {
            return this.touchId != -1;
        }

        public void setX(float x) {
            this.pointerCoords.x = x;
        }

        public void setY(float y) {
            this.pointerCoords.y = y;
        }

        public void setTouchId(int touchId2) {
            this.touchId = touchId2;
        }

        public void setFromTouch(boolean touch) {
            this.fromTouch = touch;
        }

        public boolean getFromTouch() {
            return this.fromTouch;
        }

        public void setPointerCoords(MotionEvent.PointerCoords pointerCoords2) {
            this.pointerCoords = pointerCoords2;
        }

        public MotionEvent.PointerCoords getPointerCoords() {
            return this.pointerCoords;
        }

        public void setPos(Pos pos) {
            this.fromTouch = pos.getFromTouch();
            this.touchId = pos.getTouchId();
            this.pointerCoords = pos.getPointerCoords();
        }

        public float getX() {
            return this.pointerCoords.x;
        }

        public float getY() {
            return this.pointerCoords.y;
        }

        public int getTouchId() {
            return this.touchId;
        }

        public String toString() {
            return String.format("Pos {pointerCoords = %s, touchId = %d}", new Object[]{this.pointerCoords, Integer.valueOf(this.touchId)});
        }
    }

    public BsGamePadWorker(Context context) {
        this.mContext = context;
        this.mTouchList = new Pos[16];
        for (int i = 0; i < 16; i++) {
            this.mTouchList[i] = new Pos();
        }
        this.mJoystickLastPos = new Pos[3];
        this.mMainDirectionPad = 10;
        this.mGamePadRotation = 0;
        boolean z = true;
        this.mMousePointerVisibility = true;
        this.mMouseSupport = Settings.System.getInt(context.getContentResolver(), BSGAMEPAD_MOUSE_SUPPORT_SETTING, 0) == 0 ? false : z;
        this.mEnableKeyMap = false;
    }

    public void loadKeyMap(Map map, boolean isChooseMove, int rotation) {
        DEBUG_FOR_CHOOSE_MOVE = isChooseMove;
        this.mGamePadMapper.putAll(map);
        this.mMousePointerVisibility = true;
        this.mGamePadRotation = rotation;
        this.mEnableKeyMap = true;
    }

    public void unloadKeyMap() {
        for (int i = 0; i < 16; i++) {
            if (this.mTouchList[i].isValid()) {
                fire(buildMotionEvent(i, 1));
            }
        }
        this.mGameButtonStatesBitMap = 0;
        this.mGamePadMapper.clear();
        this.mMousePointerVisibility = true;
        this.mEnableKeyMap = false;
    }

    public void enableKeyMap(boolean enable) {
        if (this.mEnableKeyMap != enable) {
            if (!enable) {
                for (int i = 0; i < 16; i++) {
                    if (this.mTouchList[i].isValid()) {
                        fire(buildMotionEvent(i, 1));
                    }
                }
                this.mGameButtonStatesBitMap = 0;
                this.mMousePointerVisibility = true;
            }
            this.mEnableKeyMap = enable;
        }
    }

    public boolean onInputEvent(InputEvent event) {
        Log.d(TAG, "onInputEvent = " + event.toString());
        if (!this.mEnableKeyMap) {
            return false;
        }
        if ((event.getSource() & 16) == 16 || (event.getSource() & UsbTerminalTypes.TERMINAL_BIDIR_HANDSET) == 1025 || (event.getSource() & UsbACInterface.FORMAT_III_IEC1937_MPEG1_Layer1) == 8194 || (event.getSource() & UsbACInterface.FORMAT_II_AC3) == 4098 || (event.getSource() & UsbTerminalTypes.TERMINAL_USB_STREAMING) == 257) {
            return processInputEvent(event);
        }
        return false;
    }

    private boolean processInputEvent(InputEvent event) {
        if (event instanceof KeyEvent) {
            if (event.getSource() == 1281) {
                return processGameButtonEvent((KeyEvent) event);
            }
            if (!this.mMouseSupport || event.getSource() != 8194) {
                return false;
            }
            return true;
        } else if (!(event instanceof MotionEvent)) {
            return false;
        } else {
            if ((event.getSource() & UsbACInterface.FORMAT_II_AC3) == 4098) {
                return processTouchScreenEvent((MotionEvent) event);
            }
            if ((event.getSource() & UsbACInterface.FORMAT_III_IEC1937_MPEG1_Layer1) != 8194) {
                return processJoystickMotionEvent((MotionEvent) event);
            }
            if (this.mMouseSupport) {
                return processMouseEvent((MotionEvent) event);
            }
            return false;
        }
    }

    private void updateTouchId(int index, int touchid) {
        this.mTouchList[index].setTouchId(touchid);
    }

    private void adjustTouchId(int gap) {
        for (int i = 0; i < 16; i++) {
            if (this.mTouchList[i].getFromTouch()) {
                updateTouchId(i, this.mTouchList[i].getTouchId() + gap);
            }
        }
    }

    /* access modifiers changed from: private */
    public int insertTouch(Pos pos) {
        int index = -1;
        for (int i = 0; i < 16; i++) {
            if (this.mTouchList[i].isValid()) {
                index++;
                if (this.mTouchList[i].getTouchId() == pos.getTouchId()) {
                    this.mTouchList[i].setPos(pos);
                    return index;
                }
            }
        }
        for (int i2 = 0; i2 < 16; i2++) {
            if (!this.mTouchList[i2].isValid()) {
                this.mTouchList[i2].setPos(pos);
                return i2;
            }
        }
        return -1;
    }

    private boolean removeTouch(int actionIndex) {
        if (actionIndex < 0 || actionIndex >= 16) {
            return false;
        }
        for (int i = 0; i < 16; i++) {
            if (this.mTouchList[i].isValid()) {
                if (actionIndex == 0) {
                    this.mTouchList[i].reset();
                    return true;
                }
                actionIndex--;
            }
        }
        return true;
    }

    private boolean processTouchScreenEvent(MotionEvent event) {
        if (this.mGamePadMapper.size() == 0) {
            return false;
        }
        int count = event.getPointerCount();
        int actionIndex = -1;
        for (int i = 0; i < count; i++) {
            Pos pos = new Pos(event.getX(i), event.getY(i));
            MotionEvent.PointerCoords pointerCoords = new MotionEvent.PointerCoords();
            event.getPointerCoords(i, pointerCoords);
            pos.setPointerCoords(pointerCoords);
            pos.setFromTouch(true);
            pos.setTouchId(this.mGamePadMapper.size() + event.getPointerId(i));
            if (i == event.getActionIndex()) {
                actionIndex = insertTouch(pos);
            } else {
                insertTouch(pos);
            }
        }
        fire(buildMotionEvent(actionIndex, event.getAction() & 255));
        return true;
    }

    private boolean processGameButtonEvent(KeyEvent event) {
        int index;
        if (event.getAction() != 0 && event.getAction() != 1) {
            return false;
        }
        int keyCode = event.getKeyCode();
        if (this.mGamePadRotation == 270) {
            switch (keyCode) {
                case 96:
                    keyCode = 97;
                    break;
                case HdmiCecKeycode.CEC_KEYCODE_PAUSE_PLAY_FUNCTION /*97*/:
                    keyCode = 100;
                    break;
                case 99:
                    keyCode = 96;
                    break;
                case 100:
                    keyCode = 99;
                    break;
            }
        }
        switch (keyCode) {
            case 96:
                index = 2;
                break;
            case HdmiCecKeycode.CEC_KEYCODE_PAUSE_PLAY_FUNCTION /*97*/:
                index = 3;
                break;
            case 99:
                index = 4;
                break;
            case 100:
                index = 5;
                break;
            case 102:
                index = 6;
                break;
            case 103:
                index = 7;
                break;
            case HdmiCecKeycode.CEC_KEYCODE_SELECT_MEDIA_FUNCTION /*104*/:
                index = 8;
                break;
            case HdmiCecKeycode.CEC_KEYCODE_SELECT_AV_INPUT_FUNCTION /*105*/:
                index = 9;
                break;
            case HdmiCecKeycode.CEC_KEYCODE_POWER_OFF_FUNCTION /*108*/:
                index = 0;
                break;
            case HdmiCecKeycode.CEC_KEYCODE_POWER_ON_FUNCTION /*109*/:
                index = 1;
                break;
            default:
                return false;
        }
        if (event.getAction() == 0) {
            this.mGameButtonStatesBitMap |= (long) (1 << index);
        }
        Log.d(TAG, "mGameButtonStatesBitMap = " + this.mGameButtonStatesBitMap);
        for (int i = 0; i < this.mGamePadMapper.size(); i++) {
            Log.d(TAG, "touchMask = " + this.mGamePadMapper.keyAt(i) + " buttonMap = " + this.mGamePadMapper.valueAt(i).toString());
        }
        int touchid = this.mGamePadMapper.indexOfKey(Integer.valueOf((int) this.mGameButtonStatesBitMap));
        if ((touchid < 0 || touchid >= this.mGamePadMapper.size()) && (((touchid = this.mGamePadMapper.indexOfKey(Integer.valueOf((int) (this.mGameButtonStatesBitMap & 1023)))) < 0 || touchid >= this.mGamePadMapper.size()) && ((touchid = this.mGamePadMapper.indexOfKey(Integer.valueOf(1 << index))) < 0 || touchid >= this.mGamePadMapper.size()))) {
            touchid = -1;
        }
        if (touchid != -1) {
            Pos pos = new Pos(this.mGamePadMapper.valueAt(touchid).getCenterX(), this.mGamePadMapper.valueAt(touchid).getCenterY());
            pos.setTouchId(touchid);
            fire(buildMotionEvent(insertTouch(pos), event.getAction()));
        }
        if (event.getAction() == 1) {
            this.mGameButtonStatesBitMap &= (long) (~(1 << index));
        }
        if (touchid != -1) {
            return true;
        }
        return false;
    }

    private boolean processMouseTouchEvent(MotionEvent event) {
        if (!this.mMousePointerVisibility) {
            return true;
        }
        if (this.mGamePadMapper.size() == 0) {
            return false;
        }
        Pos pos = new Pos(event.getX(), event.getY());
        pos.setFromTouch(true);
        pos.setTouchId(15);
        fire(buildMotionEvent(insertTouch(pos), event.getAction() & 255));
        return true;
    }

    private boolean processMouseTrackerEvent(MotionEvent event) {
        if (this.mHandler.hasMessages(3)) {
            this.mHandler.removeMessages(3);
        }
        int touchid = -1;
        long j = this.mGameButtonStatesBitMap;
        if ((j & 8192) == 0) {
            this.mGameButtonStatesBitMap = j | 8192;
            touchid = this.mGamePadMapper.indexOfKey(Integer.valueOf((int) this.mGameButtonStatesBitMap));
            if ((touchid < 0 || touchid >= this.mGamePadMapper.size()) && ((touchid = this.mGamePadMapper.indexOfKey(8192)) < 0 || touchid >= this.mGamePadMapper.size())) {
                touchid = -1;
            }
            if (touchid == -1) {
                return true;
            }
            this.mMouseTrackerLastX = this.mGamePadMapper.valueAt(touchid).getCenterX();
            this.mMouseTrackerLastY = this.mGamePadMapper.valueAt(touchid).getCenterY();
            Pos downPos = new Pos(this.mMouseTrackerLastX, this.mMouseTrackerLastY);
            downPos.setTouchId(touchid);
            fire(buildMotionEvent(insertTouch(downPos), 0));
        }
        if (touchid == -1 && (((touchid = this.mGamePadMapper.indexOfKey(Integer.valueOf((int) this.mGameButtonStatesBitMap))) < 0 || touchid >= this.mGamePadMapper.size()) && ((touchid = this.mGamePadMapper.indexOfKey(8192)) < 0 || touchid >= this.mGamePadMapper.size()))) {
            touchid = -1;
        }
        if (touchid == -1) {
            return true;
        }
        this.mMouseTrackerLastX += event.getAxisValue(27) / 2.0f;
        this.mMouseTrackerLastY += event.getAxisValue(28) / 2.0f;
        Pos movePos = new Pos(this.mMouseTrackerLastX, this.mMouseTrackerLastY);
        movePos.setTouchId(touchid);
        fire(buildMotionEvent(insertTouch(movePos), 2));
        Message msg = this.mHandler.obtainMessage(3);
        msg.arg1 = touchid;
        this.mHandler.sendMessageDelayed(msg, 60);
        return true;
    }

    private boolean processMouseEvent(MotionEvent event) {
        int key;
        if (event.getAction() != 0 && event.getAction() != 1 && event.getAction() != 2 && event.getAction() != 7 && event.getAction() != 11 && event.getAction() != 12) {
            return !this.mMousePointerVisibility;
        }
        if (event.getAction() != 7) {
            int action = event.getAction();
            int actionButton = event.getActionButton();
            if (actionButton != 0) {
                if (actionButton != 1) {
                    if (actionButton != 8) {
                        if (actionButton == 128 && action == 12) {
                            if (this.mHandler.hasMessages(3)) {
                                this.mHandler.removeMessages(3);
                            }
                            int touchid = -1;
                            long j = this.mGameButtonStatesBitMap;
                            if ((8192 & j) != 0 && (((touchid = this.mGamePadMapper.indexOfKey(Integer.valueOf((int) j))) < 0 || touchid >= this.mGamePadMapper.size()) && ((touchid = this.mGamePadMapper.indexOfKey(8192)) < 0 || touchid >= this.mGamePadMapper.size()))) {
                                touchid = -1;
                            }
                            if (touchid != -1) {
                                Pos upPos = new Pos(this.mGamePadMapper.valueAt(touchid).getCenterX(), this.mGamePadMapper.valueAt(touchid).getCenterY());
                                upPos.setTouchId(touchid);
                                fire(buildMotionEvent(insertTouch(upPos), 1));
                                this.mGameButtonStatesBitMap &= -8193;
                            }
                            this.mMousePointerVisibility = !this.mMousePointerVisibility;
                            if (this.mMousePointerVisibility) {
                                this.mGamePadMapper.remove(8192);
                                adjustTouchId(-1);
                            } else {
                                this.mGamePadMapper.put(8192, new BsGameKeyMap(1080.0f, 540.0f, 0.0f));
                                adjustTouchId(1);
                            }
                        }
                        return true;
                    } else if (this.mMousePointerVisibility) {
                        return true;
                    } else {
                        key = 97;
                    }
                } else if (this.mMousePointerVisibility) {
                    return true;
                } else {
                    key = 96;
                }
                return processGameButtonEvent(new KeyEvent(action - 11, key));
            } else if (!this.mMousePointerVisibility) {
                return processMouseTrackerEvent(event);
            } else {
                if (action == 0 || action == 1 || action == 2) {
                    return processMouseTouchEvent(event);
                }
                return true;
            }
        } else if (this.mMousePointerVisibility != 0) {
            return false;
        } else {
            return processMouseTrackerEvent(event);
        }
    }

    private boolean processJoystickMotionEvent(MotionEvent event) {
        int touchid = this.mGamePadMapper.indexOfKey(Integer.valueOf(1 << this.mMainDirectionPad));
        if (touchid < 0 || touchid >= this.mGamePadMapper.size()) {
            return false;
        }
        if (event.getAction() == 2) {
            int historySize = event.getHistorySize();
            for (int i = 0; i < historySize; i++) {
                processJoystickDirectPadEvent(event, i);
            }
            processJoystickDirectPadEvent(event, -1);
        }
        return true;
    }

    private Pos getPosFromMotionEvent(MotionEvent event, int historyPos, int index) {
        if (index < 10 && index > 12) {
            return null;
        }
        Log.d(TAG, "index = " + index);
        int axis_X = 0;
        int axis_Y = 1;
        if (index == 11) {
            axis_X = 11;
            axis_Y = 14;
        } else if (index == 12) {
            axis_X = 15;
            axis_Y = 16;
        }
        float x = getCenteredAxis(event, event.getDevice(), axis_X, historyPos);
        float y = getCenteredAxis(event, event.getDevice(), axis_Y, historyPos);
        if (this.mGamePadRotation == 270) {
            float tmp = x;
            x = y;
            y = -tmp;
        }
        return new Pos(x, y);
    }

    private void processJoystickPos(Pos pos, int radius) {
        double length = Math.sqrt(Math.pow((double) pos.getX(), 2.0d) + Math.pow((double) pos.getY(), 2.0d));
        if (Math.abs(length) > 0.7d) {
            pos.setX((float) (((double) (((float) radius) * pos.getX())) / length));
            pos.setY((float) (((double) (((float) radius) * pos.getY())) / length));
        }
    }

    private int prevProcessJoystickPos(Pos pos, int radius) {
        double length = Math.sqrt(Math.pow((double) pos.getX(), 2.0d) + Math.pow((double) pos.getY(), 2.0d));
        if (Math.abs(length) <= 0.7d) {
            return (int) (10.0d * length);
        }
        pos.setX((float) (((double) (((float) radius) * pos.getX())) / length));
        pos.setY((float) (((double) (((float) radius) * pos.getY())) / length));
        return 10;
    }

    private void processJoystickDirectPadEvent(MotionEvent event, int historyPos) {
        boolean z;
        int touchid;
        int touchid2 = -1;
        for (int i = 0; i < 3; i++) {
            Pos pos = getPosFromMotionEvent(event, historyPos, i + 10);
            if (pos != null && this.mMainDirectionPad == i + 10) {
                if (!((Float.compare(Math.abs(pos.getX()), 0.0f) == 0 && Float.compare(Math.abs(pos.getY()), 0.0f) == 0) ? false : true)) {
                    long j = this.mGameButtonStatesBitMap;
                    if ((((long) (1 << (i + 10))) & j) != 0) {
                        touchid2 = this.mGamePadMapper.indexOfKey(Integer.valueOf((int) (((long) (1 << (i + 10))) | (1023 & j))));
                        if ((touchid2 < 0 || touchid2 >= this.mGamePadMapper.size()) && ((touchid2 = this.mGamePadMapper.indexOfKey(Integer.valueOf(1 << (i + 10)))) < 0 || touchid2 >= this.mGamePadMapper.size())) {
                            touchid2 = -1;
                        }
                        if (touchid2 != -1) {
                            Pos upPos = new Pos(this.mGamePadMapper.valueAt(touchid2).getCenterX(), this.mGamePadMapper.valueAt(touchid2).getCenterY());
                            upPos.setTouchId(touchid2);
                            if (this.mHandler.hasMessages(0)) {
                                this.mHandler.removeMessages(0);
                            }
                            Message msg = this.mHandler.obtainMessage(0);
                            msg.arg1 = i + 10;
                            msg.obj = upPos;
                            this.mHandler.sendMessageDelayed(msg, 60);
                        }
                    }
                } else {
                    if (this.mHandler.hasMessages(0)) {
                        this.mHandler.removeMessages(0);
                    }
                    int i2 = 2;
                    if (this.mMainDirectionPad == i + 10) {
                        long j2 = this.mGameButtonStatesBitMap;
                        if ((((long) (1 << (i + 10))) & j2) == 0) {
                            this.mGameButtonStatesBitMap = j2 | ((long) (1 << (i + 10)));
                            int touchid3 = this.mGamePadMapper.indexOfKey(Integer.valueOf((int) this.mGameButtonStatesBitMap));
                            if ((touchid3 < 0 || touchid3 >= this.mGamePadMapper.size()) && ((touchid3 = this.mGamePadMapper.indexOfKey(Integer.valueOf(1 << (i + 10)))) < 0 || touchid3 >= this.mGamePadMapper.size())) {
                                touchid3 = -1;
                            }
                            if (touchid2 == -1) {
                                Log.e(TAG, "something is wrong in BsGamePadService");
                                Log.e(TAG, "mGameButtonStatesBitMap = " + Integer.toBinaryString((int) this.mGameButtonStatesBitMap));
                                for (int j3 = 0; j3 < 16; j3++) {
                                    Log.e(TAG, "mTouchList[" + j3 + "] = " + this.mTouchList[j3].toString());
                                }
                            } else {
                                Pos downPos = new Pos(this.mGamePadMapper.valueAt(touchid2).getCenterX(), this.mGamePadMapper.valueAt(touchid2).getCenterY());
                                downPos.setTouchId(touchid2);
                                int actionIndex = insertTouch(downPos);
                                fire(buildMotionEvent(actionIndex, 0));
                                if (DEBUG_FOR_CHOOSE_MOVE) {
                                    fire(buildMotionEvent(actionIndex, 2));
                                    Message msg2 = this.mHandler.obtainMessage(2);
                                    msg2.arg1 = i;
                                    this.mHandler.sendMessageDelayed(msg2, 33);
                                }
                            }
                        }
                    }
                    if (touchid2 == -1 && ((touchid2 = this.mGamePadMapper.indexOfKey(Integer.valueOf((int) this.mGameButtonStatesBitMap))) < 0 || touchid2 >= this.mGamePadMapper.size())) {
                        if (this.mMainDirectionPad == i + 10) {
                            touchid = this.mGamePadMapper.indexOfKey(Integer.valueOf(1 << (i + 10)));
                        } else {
                            touchid = this.mGamePadMapper.indexOfKey(Integer.valueOf((int) (this.mGameButtonStatesBitMap & 1023)));
                        }
                        if (touchid2 < 0 || touchid2 >= this.mGamePadMapper.size()) {
                            touchid2 = -1;
                        }
                    }
                    if (touchid2 != -1) {
                        processJoystickPos(pos, 1);
                        if (Math.abs(pos.getX()) == 1.0f || Math.abs(pos.getY()) == 1.0f) {
                            z = false;
                            if (SystemProperties.getBoolean("persist.bsgamepad.topup", false)) {
                                z = true;
                            }
                        } else {
                            z = false;
                        }
                        boolean isTopUp = z;
                        pos.setX((pos.getX() * ((float) ((int) this.mGamePadMapper.valueAt(touchid2).getRadius()))) + this.mGamePadMapper.valueAt(touchid2).getCenterX());
                        pos.setY((pos.getY() * ((float) ((int) this.mGamePadMapper.valueAt(touchid2).getRadius()))) + this.mGamePadMapper.valueAt(touchid2).getCenterY());
                        pos.setTouchId(touchid2);
                        if (!DEBUG_FOR_CHOOSE_MOVE || (this.mEnableSendMoveBitMap & ((long) (1 << i))) != 0) {
                            int actionIndex2 = insertTouch(pos);
                            if (isTopUp) {
                                i2 = 1;
                            }
                            fire(buildMotionEvent(actionIndex2, i2));
                        } else {
                            this.mJoystickLastPos[i] = pos;
                        }
                    }
                }
            }
        }
        MotionEvent motionEvent = event;
        int i3 = historyPos;
    }

    private void splitMotionEvent(int touchid, float firstX, float firstY, float secondX, float secondY, int splitCount) {
        if (splitCount != 0) {
            float offsetX = (secondX - firstX) / ((float) splitCount);
            float offsetY = (secondY - firstY) / ((float) splitCount);
            for (int i = 1; i < splitCount; i++) {
                Pos pos = new Pos((((float) i) * offsetX) + firstX, (((float) i) * offsetY) + firstY);
                pos.setTouchId(touchid);
                fire(buildMotionEvent(insertTouch(pos), 2));
            }
        }
    }

    /* access modifiers changed from: private */
    public MotionEvent buildMotionEvent(int actionIndex, int action) {
        int i = actionIndex;
        int action2 = action;
        Log.d(TAG, "buildMotionEvent " + i);
        if (i >= 0 || i < 16) {
            int nCount = 0;
            ArrayList<MotionEvent.PointerCoords> pointerCoordsList = new ArrayList<>();
            ArrayList<MotionEvent.PointerProperties> pointerPropsList = new ArrayList<>();
            int i2 = 0;
            for (int i3 = 16; i2 < i3; i3 = 16) {
                if (this.mTouchList[i2].isValid()) {
                    MotionEvent.PointerProperties pointerProps = new MotionEvent.PointerProperties();
                    pointerProps.id = i2;
                    pointerProps.toolType = 1;
                    pointerPropsList.add(pointerProps);
                    MotionEvent.PointerCoords pointerCoords = new MotionEvent.PointerCoords();
                    pointerCoords.copyFrom(this.mTouchList[i2].getPointerCoords());
                    pointerCoordsList.add(pointerCoords);
                    nCount++;
                }
                i2++;
            }
            if (action2 == 1 || action2 == 6 || action2 == 10 || action2 == 3) {
                removeTouch(actionIndex);
            }
            if (nCount == 0) {
                Log.e(TAG, "buildMotionEvent " + i);
                Log.e(TAG, "something is wrong in BsGamePadService");
                Log.e(TAG, "mGameButtonStatesBitMap = " + Integer.toBinaryString((int) this.mGameButtonStatesBitMap));
                for (int i4 = 0; i4 < 16; i4++) {
                    Log.e(TAG, "mTouchList[" + i4 + "] = " + this.mTouchList[i4].toString());
                }
                return null;
            }
            if (nCount > 1 && action2 != 2) {
                if (action2 == 0) {
                    action2 = 5;
                } else if (action2 == 1) {
                    action2 = 6;
                }
                action2 |= i << 8;
            }
            return MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), action2, nCount, (MotionEvent.PointerProperties[]) pointerPropsList.toArray(new MotionEvent.PointerProperties[pointerPropsList.size()]), (MotionEvent.PointerCoords[]) pointerCoordsList.toArray(new MotionEvent.PointerCoords[pointerCoordsList.size()]), 0, 0, 1.0f, 1.0f, 0, 0, UsbACInterface.FORMAT_II_AC3, 0);
        }
        Log.e(TAG, "something is wrong in BsGamePadService");
        Log.e(TAG, "mGameButtonStatesBitMap = " + Integer.toBinaryString((int) this.mGameButtonStatesBitMap));
        for (int i5 = 0; i5 < 16; i5++) {
            Log.e(TAG, "mTouchList[" + i5 + "] = " + this.mTouchList[i5].toString());
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void fire(MotionEvent event) {
        if (event != null) {
            Log.d(TAG, "fire event = " + event.toString());
            int mode = 0;
            int action = event.getAction() & 255;
            if (action == 0 || action == 5) {
                mode = 2;
            }
            if (!InputManager.getInstance().injectInputEvent(event, mode)) {
                Log.e(TAG, "fire FAILED ! event = " + event.toString());
            }
        }
    }
}
