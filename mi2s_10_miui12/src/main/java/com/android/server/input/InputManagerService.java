package com.android.server.input;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayViewport;
import android.hardware.input.IInputDevicesChangedListener;
import android.hardware.input.IInputManager;
import android.hardware.input.ITabletModeChangedListener;
import android.hardware.input.InputDeviceIdentifier;
import android.hardware.input.InputManagerInternal;
import android.hardware.input.KeyboardLayout;
import android.hardware.input.TouchCalibration;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.LocaleList;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.view.IInputFilter;
import android.view.IInputFilterHost;
import android.view.IInputMonitorHost;
import android.view.IWindow;
import android.view.InputApplicationHandle;
import android.view.InputChannel;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.InputMonitor;
import android.view.InputWindowHandle;
import android.view.KeyEvent;
import android.view.PointerIcon;
import android.view.ViewConfiguration;
import android.widget.Toast;
import com.android.internal.R;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.server.DisplayThread;
import com.android.server.HandyMode;
import com.android.server.LocalServices;
import com.android.server.Watchdog;
import com.android.server.am.BroadcastQueueInjector;
import com.android.server.pm.Settings;
import com.android.server.slice.SliceClientPermissions;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import libcore.io.IoUtils;
import libcore.io.Streams;

public class InputManagerService extends IInputManager.Stub implements Watchdog.Monitor {
    public static final int BTN_MOUSE = 272;
    private static final int COVER_OFF = 6;
    private static final int COVER_ON = 7;
    static final boolean DEBUG = false;
    private static final String EXCLUDED_DEVICES_PATH = "etc/excluded-input-devices.xml";
    private static final int GLOVE_OFF = 0;
    private static final int GLOVE_ON = 1;
    private static final int INJECTION_TIMEOUT_MILLIS = 30000;
    private static final int INPUT_EVENT_INJECTION_FAILED = 2;
    private static final int INPUT_EVENT_INJECTION_PERMISSION_DENIED = 1;
    private static final int INPUT_EVENT_INJECTION_SUCCEEDED = 0;
    private static final int INPUT_EVENT_INJECTION_TIMED_OUT = 3;
    public static final int KEY_STATE_DOWN = 1;
    public static final int KEY_STATE_UNKNOWN = -1;
    public static final int KEY_STATE_UP = 0;
    public static final int KEY_STATE_VIRTUAL = 2;
    private static final int MSG_DELIVER_INPUT_DEVICES_CHANGED = 1;
    private static final int MSG_DELIVER_TABLET_MODE_CHANGED = 6;
    private static final int MSG_RELOAD_DEVICE_ALIASES = 5;
    private static final int MSG_RELOAD_KEYBOARD_LAYOUTS = 3;
    private static final int MSG_SWITCH_KEYBOARD_LAYOUT = 2;
    private static final int MSG_UPDATE_KEYBOARD_LAYOUTS = 4;
    private static final String PORT_ASSOCIATIONS_PATH = "etc/input-port-associations.xml";
    private static final int STYLUS_OFF = 2;
    private static final int STYLUS_ON = 3;
    public static final int SW_CAMERA_LENS_COVER = 9;
    public static final int SW_CAMERA_LENS_COVER_BIT = 512;
    public static final int SW_HEADPHONE_INSERT = 2;
    public static final int SW_HEADPHONE_INSERT_BIT = 4;
    public static final int SW_JACK_BITS = 212;
    public static final int SW_JACK_PHYSICAL_INSERT = 7;
    public static final int SW_JACK_PHYSICAL_INSERT_BIT = 128;
    public static final int SW_KEYPAD_SLIDE = 10;
    public static final int SW_KEYPAD_SLIDE_BIT = 1024;
    public static final int SW_LID = 0;
    public static final int SW_LID_BIT = 1;
    public static final int SW_LINEOUT_INSERT = 6;
    public static final int SW_LINEOUT_INSERT_BIT = 64;
    public static final int SW_MICROPHONE_INSERT = 4;
    public static final int SW_MICROPHONE_INSERT_BIT = 16;
    public static final int SW_TABLET_MODE = 1;
    public static final int SW_TABLET_MODE_BIT = 2;
    static final String TAG = "InputManager";
    private static final int WAKEUP_OFF = 4;
    private static final int WAKEUP_ON = 5;
    IInputFilter mBsInputFilter;
    private final Context mContext;
    private final PersistentDataStore mDataStore = new PersistentDataStore();
    private Context mDisplayContext;
    /* access modifiers changed from: private */
    public final File mDoubleTouchGestureEnableFile;
    private IWindow mFocusedWindow;
    private boolean mFocusedWindowHasCapture;
    private final InputManagerHandler mHandler;
    private int mInputDebugLevel = 0;
    private InputDevice[] mInputDevices = new InputDevice[0];
    private final SparseArray<InputDevicesChangedListenerRecord> mInputDevicesChangedListeners = new SparseArray<>();
    private boolean mInputDevicesChangedPending;
    private Object mInputDevicesLock = new Object();
    IInputFilter mInputFilter;
    InputFilterHost mInputFilterHost;
    final Object mInputFilterLock = new Object();
    private PendingIntent mKeyboardLayoutIntent;
    private boolean mKeyboardLayoutNotificationShown;
    private int mNextVibratorTokenValue;
    private NotificationManager mNotificationManager;
    IInputFilter mPrevInputFilter;
    /* access modifiers changed from: private */
    public final long mPtr;
    private Toast mSwitchedKeyboardLayoutToast;
    private boolean mSystemReady;
    private final SparseArray<TabletModeChangedListenerRecord> mTabletModeChangedListeners = new SparseArray<>();
    private final Object mTabletModeLock = new Object();
    private final ArrayList<InputDevice> mTempFullKeyboards = new ArrayList<>();
    private final ArrayList<InputDevicesChangedListenerRecord> mTempInputDevicesChangedListenersToNotify = new ArrayList<>();
    private final List<TabletModeChangedListenerRecord> mTempTabletModeChangedListenersToNotify = new ArrayList();
    final boolean mUseDevInputEventForAudioJack;
    private Object mVibratorLock = new Object();
    private HashMap<IBinder, VibratorToken> mVibratorTokens = new HashMap<>();
    private WindowManagerCallbacks mWindowManagerCallbacks;
    private WiredAccessoryCallbacks mWiredAccessoryCallbacks;

    private interface KeyboardLayoutVisitor {
        void visitKeyboardLayout(Resources resources, int i, KeyboardLayout keyboardLayout);
    }

    public interface WindowManagerCallbacks {
        KeyEvent dispatchUnhandledKey(IBinder iBinder, KeyEvent keyEvent, int i);

        int getPointerDisplayId();

        int getPointerLayer();

        long interceptKeyBeforeDispatching(IBinder iBinder, KeyEvent keyEvent, int i);

        int interceptKeyBeforeQueueing(KeyEvent keyEvent, int i);

        int interceptMotionBeforeQueueingNonInteractive(int i, long j, int i2);

        long notifyANR(IBinder iBinder, String str);

        void notifyCameraLensCoverSwitchChanged(long j, boolean z);

        void notifyConfigurationChanged();

        void notifyInputChannelBroken(IBinder iBinder);

        void notifyLidSwitchChanged(long j, boolean z);

        void onPointerDownOutsideFocus(IBinder iBinder);
    }

    public interface WiredAccessoryCallbacks {
        void notifyWiredAccessoryChanged(long j, int i, int i2);

        void systemReady();
    }

    private static native boolean nativeCanDispatchToDisplay(long j, int i, int i2);

    private static native void nativeCancelVibrate(long j, int i, int i2);

    private static native void nativeDisableInputDevice(long j, int i);

    private static native String nativeDump(long j);

    private static native void nativeEnableInputDevice(long j, int i);

    private static native int nativeGetKeyCodeState(long j, int i, int i2, int i3);

    private static native int nativeGetScanCodeState(long j, int i, int i2, int i3);

    private static native int nativeGetSwitchState(long j, int i, int i2, int i3);

    private static native boolean nativeHasKeys(long j, int i, int i2, int[] iArr, boolean[] zArr);

    private static native long nativeInit(InputManagerService inputManagerService, Context context, MessageQueue messageQueue);

    /* access modifiers changed from: private */
    public static native int nativeInjectInputEvent(long j, InputEvent inputEvent, int i, int i2, int i3, int i4, int i5);

    private static native boolean nativeIsInputDeviceEnabled(long j, int i);

    private static native void nativeMonitor(long j);

    /* access modifiers changed from: private */
    public static native void nativePilferPointers(long j, IBinder iBinder);

    private static native void nativeRegisterInputChannel(long j, InputChannel inputChannel, int i);

    private static native void nativeRegisterInputMonitor(long j, InputChannel inputChannel, int i, boolean z);

    private static native void nativeReloadCalibration(long j);

    private static native void nativeReloadDeviceAliases(long j);

    private static native void nativeReloadKeyboardLayouts(long j);

    private static native void nativeReloadPointerIcons(long j);

    private static native void nativeSetCustomPointerIcon(long j, PointerIcon pointerIcon);

    private static native void nativeSetDebugInput(long j, int i);

    private static native void nativeSetDisplayViewports(long j, DisplayViewport[] displayViewportArr);

    private static native void nativeSetFocusedApplication(long j, int i, InputApplicationHandle inputApplicationHandle);

    private static native void nativeSetFocusedDisplay(long j, int i);

    private static native void nativeSetInputDispatchMode(long j, boolean z, boolean z2);

    private static native void nativeSetInputFilterEnabled(long j, boolean z);

    private static native void nativeSetInputWindows(long j, InputWindowHandle[] inputWindowHandleArr, int i);

    /* access modifiers changed from: private */
    public static native void nativeSetInteractive(long j, boolean z);

    private static native void nativeSetPointerCapture(long j, boolean z);

    private static native void nativeSetPointerIconType(long j, int i);

    private static native void nativeSetPointerSpeed(long j, int i);

    private static native void nativeSetShowTouches(long j, boolean z);

    private static native void nativeSetSystemUiVisibility(long j, int i);

    private static native void nativeStart(long j);

    private static native void nativeSwitchTouchWorkMode(long j, int i);

    /* access modifiers changed from: private */
    public static native void nativeToggleCapsLock(long j, int i);

    /* access modifiers changed from: private */
    public static native void nativeUnregisterInputChannel(long j, InputChannel inputChannel);

    private static native void nativeVibrate(long j, int i, long[] jArr, int i2, int i3);

    public InputManagerService(Context context) {
        File file;
        this.mContext = context;
        this.mHandler = new InputManagerHandler(DisplayThread.get().getLooper());
        this.mUseDevInputEventForAudioJack = context.getResources().getBoolean(17891563);
        Slog.i(TAG, "Initializing input manager, mUseDevInputEventForAudioJack=" + this.mUseDevInputEventForAudioJack);
        this.mPtr = nativeInit(this, this.mContext, this.mHandler.getLooper().getQueue());
        String doubleTouchGestureEnablePath = context.getResources().getString(17039746);
        if (TextUtils.isEmpty(doubleTouchGestureEnablePath)) {
            file = null;
        } else {
            file = new File(doubleTouchGestureEnablePath);
        }
        this.mDoubleTouchGestureEnableFile = file;
        LocalServices.addService(InputManagerInternal.class, new LocalService());
    }

    public void setWindowManagerCallbacks(WindowManagerCallbacks callbacks) {
        this.mWindowManagerCallbacks = callbacks;
    }

    public void setWiredAccessoryCallbacks(WiredAccessoryCallbacks callbacks) {
        this.mWiredAccessoryCallbacks = callbacks;
    }

    public void start() {
        Slog.i(TAG, "Starting input manager");
        nativeStart(this.mPtr);
        Watchdog.getInstance().addMonitor(this);
        registerPointerSpeedSettingObserver();
        registerShowTouchesSettingObserver();
        registerAccessibilityLargePointerSettingObserver();
        registerTouchWakeupModeSettingObserver();
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                InputManagerService.this.updatePointerSpeedFromSettings();
                InputManagerService.this.updateShowTouchesFromSettings();
                InputManagerService.this.updateAccessibilityLargePointerFromSettings();
            }
        }, new IntentFilter("android.intent.action.USER_SWITCHED"), (String) null, this.mHandler);
        updatePointerSpeedFromSettings();
        updateShowTouchesFromSettings();
        updateAccessibilityLargePointerFromSettings();
        HandyMode.initialize(this.mContext, this);
    }

    public void systemRunning() {
        this.mNotificationManager = (NotificationManager) this.mContext.getSystemService("notification");
        this.mSystemReady = true;
        IntentFilter filter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addAction("android.intent.action.PACKAGE_CHANGED");
        filter.addAction("android.intent.action.PACKAGE_REPLACED");
        filter.addDataScheme(Settings.ATTR_PACKAGE);
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                InputManagerService.this.updateKeyboardLayouts();
            }
        }, filter, (String) null, this.mHandler);
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                InputManagerService.this.reloadDeviceAliases();
            }
        }, new IntentFilter("android.bluetooth.device.action.ALIAS_CHANGED"), (String) null, this.mHandler);
        this.mHandler.sendEmptyMessage(5);
        this.mHandler.sendEmptyMessage(4);
        WiredAccessoryCallbacks wiredAccessoryCallbacks = this.mWiredAccessoryCallbacks;
        if (wiredAccessoryCallbacks != null) {
            wiredAccessoryCallbacks.systemReady();
        }
        updateTouchWakeupModeFromSettings();
    }

    /* access modifiers changed from: private */
    public void reloadKeyboardLayouts() {
        nativeReloadKeyboardLayouts(this.mPtr);
    }

    /* access modifiers changed from: private */
    public void reloadDeviceAliases() {
        nativeReloadDeviceAliases(this.mPtr);
    }

    /* access modifiers changed from: private */
    public void setDisplayViewportsInternal(List<DisplayViewport> viewports) {
        nativeSetDisplayViewports(this.mPtr, (DisplayViewport[]) viewports.toArray(new DisplayViewport[0]));
    }

    public int getKeyCodeState(int deviceId, int sourceMask, int keyCode) {
        return nativeGetKeyCodeState(this.mPtr, deviceId, sourceMask, keyCode);
    }

    public int getScanCodeState(int deviceId, int sourceMask, int scanCode) {
        return nativeGetScanCodeState(this.mPtr, deviceId, sourceMask, scanCode);
    }

    public int getSwitchState(int deviceId, int sourceMask, int switchCode) {
        return nativeGetSwitchState(this.mPtr, deviceId, sourceMask, switchCode);
    }

    public boolean hasKeys(int deviceId, int sourceMask, int[] keyCodes, boolean[] keyExists) {
        if (keyCodes == null) {
            throw new IllegalArgumentException("keyCodes must not be null.");
        } else if (keyExists != null && keyExists.length >= keyCodes.length) {
            return nativeHasKeys(this.mPtr, deviceId, sourceMask, keyCodes, keyExists);
        } else {
            throw new IllegalArgumentException("keyExists must not be null and must be at least as large as keyCodes.");
        }
    }

    public InputChannel monitorInput(String inputChannelName, int displayId) {
        if (inputChannelName == null) {
            throw new IllegalArgumentException("inputChannelName must not be null.");
        } else if (displayId >= 0) {
            InputChannel[] inputChannels = InputChannel.openInputChannelPair(inputChannelName);
            inputChannels[0].setToken(new Binder());
            nativeRegisterInputMonitor(this.mPtr, inputChannels[0], displayId, false);
            inputChannels[0].dispose();
            return inputChannels[1];
        } else {
            throw new IllegalArgumentException("displayId must >= 0.");
        }
    }

    public InputMonitor monitorGestureInput(String inputChannelName, int displayId) {
        if (checkCallingPermission("android.permission.MONITOR_INPUT", "monitorInputRegion()")) {
            Objects.requireNonNull(inputChannelName, "inputChannelName must not be null.");
            if (displayId >= 0) {
                long ident = Binder.clearCallingIdentity();
                try {
                    InputChannel[] inputChannels = InputChannel.openInputChannelPair(inputChannelName);
                    InputMonitorHost host = new InputMonitorHost(inputChannels[0]);
                    inputChannels[0].setToken(host.asBinder());
                    nativeRegisterInputMonitor(this.mPtr, inputChannels[0], displayId, true);
                    return new InputMonitor(inputChannelName, inputChannels[1], host);
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            } else {
                throw new IllegalArgumentException("displayId must >= 0.");
            }
        } else {
            throw new SecurityException("Requires MONITOR_INPUT permission");
        }
    }

    public void registerInputChannel(InputChannel inputChannel, IBinder token) {
        if (inputChannel != null) {
            if (token == null) {
                token = new Binder();
            }
            inputChannel.setToken(token);
            nativeRegisterInputChannel(this.mPtr, inputChannel, -1);
            return;
        }
        throw new IllegalArgumentException("inputChannel must not be null.");
    }

    public void unregisterInputChannel(InputChannel inputChannel) {
        if (inputChannel != null) {
            nativeUnregisterInputChannel(this.mPtr, inputChannel);
            return;
        }
        throw new IllegalArgumentException("inputChannel must not be null.");
    }

    public void setBsInputFilter(IInputFilter filter) {
        synchronized (this.mInputFilterLock) {
            IInputFilter oldFilter = this.mInputFilter;
            if (oldFilter != filter) {
                if (this.mBsInputFilter != null || filter != null) {
                    if (oldFilter != null) {
                        this.mInputFilter = null;
                        this.mInputFilterHost.disconnectLocked();
                        this.mInputFilterHost = null;
                        try {
                            oldFilter.uninstall();
                        } catch (RemoteException e) {
                        }
                    }
                    if (filter != null) {
                        this.mPrevInputFilter = oldFilter;
                        this.mInputFilter = filter;
                        this.mBsInputFilter = filter;
                        this.mInputFilterHost = new InputFilterHost();
                        try {
                            filter.install(this.mInputFilterHost);
                        } catch (RemoteException e2) {
                        }
                    } else {
                        filter = this.mPrevInputFilter;
                        this.mPrevInputFilter = null;
                        this.mBsInputFilter = null;
                        if (filter != null) {
                            this.mInputFilter = filter;
                            this.mInputFilterHost = new InputFilterHost();
                            try {
                                filter.install(this.mInputFilterHost);
                            } catch (RemoteException e3) {
                            }
                        }
                    }
                    nativeSetInputFilterEnabled(this.mPtr, filter != null);
                }
            }
        }
    }

    public void setInputFilter(IInputFilter filter) {
        synchronized (this.mInputFilterLock) {
            IInputFilter oldFilter = this.mInputFilter;
            if (oldFilter != filter) {
                if (filter != null || this.mBsInputFilter == null) {
                    if (oldFilter != null) {
                        this.mInputFilter = null;
                        this.mInputFilterHost.disconnectLocked();
                        this.mInputFilterHost = null;
                        try {
                            oldFilter.uninstall();
                        } catch (RemoteException e) {
                        }
                    }
                    if (filter != null) {
                        this.mInputFilter = filter;
                        this.mInputFilterHost = new InputFilterHost();
                        try {
                            filter.install(this.mInputFilterHost);
                        } catch (RemoteException e2) {
                        }
                    }
                    nativeSetInputFilterEnabled(this.mPtr, filter != null);
                    return;
                }
                this.mPrevInputFilter = null;
            }
        }
    }

    public boolean injectInputEvent(InputEvent event, int mode) {
        return injectInputEventInternal(event, mode);
    }

    /* access modifiers changed from: private */
    public boolean injectInputEventInternal(InputEvent event, int mode) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        } else if (mode == 0 || mode == 2 || mode == 1) {
            int pid = Binder.getCallingPid();
            int uid = Binder.getCallingUid();
            long ident = Binder.clearCallingIdentity();
            try {
                int result = nativeInjectInputEvent(this.mPtr, event, pid, uid, mode, INJECTION_TIMEOUT_MILLIS, 134217728);
                if (result == 0) {
                    return true;
                }
                if (result == 1) {
                    Slog.w(TAG, "Input event injection from pid " + pid + " permission denied.");
                    throw new SecurityException("Injecting to another application requires INJECT_EVENTS permission");
                } else if (result != 3) {
                    Slog.w(TAG, "Input event injection from pid " + pid + " failed.");
                    return false;
                } else {
                    Slog.w(TAG, "Input event injection from pid " + pid + " timed out.");
                    return false;
                }
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        } else {
            throw new IllegalArgumentException("mode is invalid");
        }
    }

    public InputDevice getInputDevice(int deviceId) {
        synchronized (this.mInputDevicesLock) {
            for (InputDevice inputDevice : this.mInputDevices) {
                if (inputDevice.getId() == deviceId) {
                    return inputDevice;
                }
            }
            return null;
        }
    }

    public boolean isInputDeviceEnabled(int deviceId) {
        return nativeIsInputDeviceEnabled(this.mPtr, deviceId);
    }

    public void enableInputDevice(int deviceId) {
        if (checkCallingPermission("android.permission.DISABLE_INPUT_DEVICE", "enableInputDevice()")) {
            nativeEnableInputDevice(this.mPtr, deviceId);
            return;
        }
        throw new SecurityException("Requires DISABLE_INPUT_DEVICE permission");
    }

    public void disableInputDevice(int deviceId) {
        if (checkCallingPermission("android.permission.DISABLE_INPUT_DEVICE", "disableInputDevice()")) {
            nativeDisableInputDevice(this.mPtr, deviceId);
            return;
        }
        throw new SecurityException("Requires DISABLE_INPUT_DEVICE permission");
    }

    public int[] getInputDeviceIds() {
        int[] ids;
        synchronized (this.mInputDevicesLock) {
            int count = this.mInputDevices.length;
            ids = new int[count];
            for (int i = 0; i < count; i++) {
                ids[i] = this.mInputDevices[i].getId();
            }
        }
        return ids;
    }

    public InputDevice[] getInputDevices() {
        InputDevice[] inputDeviceArr;
        synchronized (this.mInputDevicesLock) {
            inputDeviceArr = this.mInputDevices;
        }
        return inputDeviceArr;
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void registerInputDevicesChangedListener(IInputDevicesChangedListener listener) {
        if (listener != null) {
            synchronized (this.mInputDevicesLock) {
                int callingPid = Binder.getCallingPid();
                if (this.mInputDevicesChangedListeners.get(callingPid) == null) {
                    InputDevicesChangedListenerRecord record = new InputDevicesChangedListenerRecord(callingPid, listener);
                    try {
                        listener.asBinder().linkToDeath(record, 0);
                        this.mInputDevicesChangedListeners.put(callingPid, record);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    throw new SecurityException("The calling process has already registered an InputDevicesChangedListener.");
                }
            }
            return;
        }
        throw new IllegalArgumentException("listener must not be null");
    }

    /* access modifiers changed from: private */
    public void onInputDevicesChangedListenerDied(int pid) {
        synchronized (this.mInputDevicesLock) {
            this.mInputDevicesChangedListeners.remove(pid);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x007d, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x007e, code lost:
        if (r1 >= r3) goto L_0x008e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0080, code lost:
        r12.mTempInputDevicesChangedListenersToNotify.get(r1).notifyInputDevicesChanged(r5);
        r1 = r1 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x008e, code lost:
        r12.mTempInputDevicesChangedListenersToNotify.clear();
        r4 = new java.util.ArrayList<>();
        r6 = r12.mTempFullKeyboards.size();
        r8 = r12.mDataStore;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00a1, code lost:
        monitor-enter(r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00a2, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00a3, code lost:
        if (r1 >= r6) goto L_0x00cf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
        r9 = r12.mTempFullKeyboards.get(r1);
        r10 = getCurrentKeyboardLayoutForInputDevice(r9.getIdentifier());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00b6, code lost:
        if (r10 != null) goto L_0x00c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00b8, code lost:
        r10 = getDefaultKeyboardLayout(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00bd, code lost:
        if (r10 == null) goto L_0x00c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00bf, code lost:
        setCurrentKeyboardLayoutForInputDevice(r9.getIdentifier(), r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00c7, code lost:
        if (r10 != null) goto L_0x00cc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00c9, code lost:
        r4.add(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00cc, code lost:
        r1 = r1 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00cf, code lost:
        monitor-exit(r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00d2, code lost:
        if (r12.mNotificationManager == null) goto L_0x00f6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00d8, code lost:
        if (r4.isEmpty() != false) goto L_0x00ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00de, code lost:
        if (r4.size() <= 1) goto L_0x00e5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00e0, code lost:
        showMissingKeyboardLayoutNotification((android.view.InputDevice) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00e5, code lost:
        showMissingKeyboardLayoutNotification(r4.get(0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00f1, code lost:
        if (r12.mKeyboardLayoutNotificationShown == false) goto L_0x00f6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00f3, code lost:
        hideMissingKeyboardLayoutNotification();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00f6, code lost:
        r12.mTempFullKeyboards.clear();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00fb, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void deliverInputDevicesChanged(android.view.InputDevice[] r13) {
        /*
            r12 = this;
            r0 = 0
            java.util.ArrayList<com.android.server.input.InputManagerService$InputDevicesChangedListenerRecord> r1 = r12.mTempInputDevicesChangedListenersToNotify
            r1.clear()
            java.util.ArrayList<android.view.InputDevice> r1 = r12.mTempFullKeyboards
            r1.clear()
            java.lang.Object r1 = r12.mInputDevicesLock
            monitor-enter(r1)
            boolean r2 = r12.mInputDevicesChangedPending     // Catch:{ all -> 0x00ff }
            if (r2 != 0) goto L_0x0014
            monitor-exit(r1)     // Catch:{ all -> 0x00ff }
            return
        L_0x0014:
            r2 = 0
            r12.mInputDevicesChangedPending = r2     // Catch:{ all -> 0x00ff }
            android.util.SparseArray<com.android.server.input.InputManagerService$InputDevicesChangedListenerRecord> r3 = r12.mInputDevicesChangedListeners     // Catch:{ all -> 0x00ff }
            int r3 = r3.size()     // Catch:{ all -> 0x00ff }
            r4 = 0
        L_0x001e:
            if (r4 >= r3) goto L_0x0030
            java.util.ArrayList<com.android.server.input.InputManagerService$InputDevicesChangedListenerRecord> r5 = r12.mTempInputDevicesChangedListenersToNotify     // Catch:{ all -> 0x00ff }
            android.util.SparseArray<com.android.server.input.InputManagerService$InputDevicesChangedListenerRecord> r6 = r12.mInputDevicesChangedListeners     // Catch:{ all -> 0x00ff }
            java.lang.Object r6 = r6.valueAt(r4)     // Catch:{ all -> 0x00ff }
            com.android.server.input.InputManagerService$InputDevicesChangedListenerRecord r6 = (com.android.server.input.InputManagerService.InputDevicesChangedListenerRecord) r6     // Catch:{ all -> 0x00ff }
            r5.add(r6)     // Catch:{ all -> 0x00ff }
            int r4 = r4 + 1
            goto L_0x001e
        L_0x0030:
            android.view.InputDevice[] r4 = r12.mInputDevices     // Catch:{ all -> 0x00ff }
            int r4 = r4.length     // Catch:{ all -> 0x00ff }
            int r5 = r4 * 2
            int[] r5 = new int[r5]     // Catch:{ all -> 0x00ff }
            r6 = 0
        L_0x0038:
            r7 = 1
            if (r6 >= r4) goto L_0x007c
            android.view.InputDevice[] r8 = r12.mInputDevices     // Catch:{ all -> 0x00ff }
            r8 = r8[r6]     // Catch:{ all -> 0x00ff }
            int r9 = r6 * 2
            int r10 = r8.getId()     // Catch:{ all -> 0x00ff }
            r5[r9] = r10     // Catch:{ all -> 0x00ff }
            int r9 = r6 * 2
            int r9 = r9 + r7
            int r7 = r8.getGeneration()     // Catch:{ all -> 0x00ff }
            r5[r9] = r7     // Catch:{ all -> 0x00ff }
            boolean r7 = r8.isVirtual()     // Catch:{ all -> 0x00ff }
            if (r7 != 0) goto L_0x0079
            boolean r7 = r8.isFullKeyboard()     // Catch:{ all -> 0x00ff }
            if (r7 == 0) goto L_0x0079
            java.lang.String r7 = r8.getDescriptor()     // Catch:{ all -> 0x00ff }
            boolean r7 = containsInputDeviceWithDescriptor(r13, r7)     // Catch:{ all -> 0x00ff }
            if (r7 != 0) goto L_0x0074
            java.util.ArrayList<android.view.InputDevice> r7 = r12.mTempFullKeyboards     // Catch:{ all -> 0x00ff }
            int r9 = r0 + 1
            r7.add(r0, r8)     // Catch:{ all -> 0x0070 }
            r0 = r9
            goto L_0x0079
        L_0x0070:
            r2 = move-exception
            r0 = r9
            goto L_0x0100
        L_0x0074:
            java.util.ArrayList<android.view.InputDevice> r7 = r12.mTempFullKeyboards     // Catch:{ all -> 0x00ff }
            r7.add(r8)     // Catch:{ all -> 0x00ff }
        L_0x0079:
            int r6 = r6 + 1
            goto L_0x0038
        L_0x007c:
            monitor-exit(r1)     // Catch:{ all -> 0x00ff }
            r1 = 0
        L_0x007e:
            if (r1 >= r3) goto L_0x008e
            java.util.ArrayList<com.android.server.input.InputManagerService$InputDevicesChangedListenerRecord> r4 = r12.mTempInputDevicesChangedListenersToNotify
            java.lang.Object r4 = r4.get(r1)
            com.android.server.input.InputManagerService$InputDevicesChangedListenerRecord r4 = (com.android.server.input.InputManagerService.InputDevicesChangedListenerRecord) r4
            r4.notifyInputDevicesChanged(r5)
            int r1 = r1 + 1
            goto L_0x007e
        L_0x008e:
            java.util.ArrayList<com.android.server.input.InputManagerService$InputDevicesChangedListenerRecord> r1 = r12.mTempInputDevicesChangedListenersToNotify
            r1.clear()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r4 = r1
            java.util.ArrayList<android.view.InputDevice> r1 = r12.mTempFullKeyboards
            int r6 = r1.size()
            com.android.server.input.PersistentDataStore r8 = r12.mDataStore
            monitor-enter(r8)
            r1 = 0
        L_0x00a3:
            if (r1 >= r6) goto L_0x00cf
            java.util.ArrayList<android.view.InputDevice> r9 = r12.mTempFullKeyboards     // Catch:{ all -> 0x00fc }
            java.lang.Object r9 = r9.get(r1)     // Catch:{ all -> 0x00fc }
            android.view.InputDevice r9 = (android.view.InputDevice) r9     // Catch:{ all -> 0x00fc }
            android.hardware.input.InputDeviceIdentifier r10 = r9.getIdentifier()     // Catch:{ all -> 0x00fc }
            java.lang.String r10 = r12.getCurrentKeyboardLayoutForInputDevice(r10)     // Catch:{ all -> 0x00fc }
            if (r10 != 0) goto L_0x00c7
            java.lang.String r11 = r12.getDefaultKeyboardLayout(r9)     // Catch:{ all -> 0x00fc }
            r10 = r11
            if (r10 == 0) goto L_0x00c7
            android.hardware.input.InputDeviceIdentifier r11 = r9.getIdentifier()     // Catch:{ all -> 0x00fc }
            r12.setCurrentKeyboardLayoutForInputDevice(r11, r10)     // Catch:{ all -> 0x00fc }
        L_0x00c7:
            if (r10 != 0) goto L_0x00cc
            r4.add(r9)     // Catch:{ all -> 0x00fc }
        L_0x00cc:
            int r1 = r1 + 1
            goto L_0x00a3
        L_0x00cf:
            monitor-exit(r8)     // Catch:{ all -> 0x00fc }
            android.app.NotificationManager r1 = r12.mNotificationManager
            if (r1 == 0) goto L_0x00f6
            boolean r1 = r4.isEmpty()
            if (r1 != 0) goto L_0x00ef
            int r1 = r4.size()
            if (r1 <= r7) goto L_0x00e5
            r1 = 0
            r12.showMissingKeyboardLayoutNotification(r1)
            goto L_0x00f6
        L_0x00e5:
            java.lang.Object r1 = r4.get(r2)
            android.view.InputDevice r1 = (android.view.InputDevice) r1
            r12.showMissingKeyboardLayoutNotification(r1)
            goto L_0x00f6
        L_0x00ef:
            boolean r1 = r12.mKeyboardLayoutNotificationShown
            if (r1 == 0) goto L_0x00f6
            r12.hideMissingKeyboardLayoutNotification()
        L_0x00f6:
            java.util.ArrayList<android.view.InputDevice> r1 = r12.mTempFullKeyboards
            r1.clear()
            return
        L_0x00fc:
            r1 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x00fc }
            throw r1
        L_0x00ff:
            r2 = move-exception
        L_0x0100:
            monitor-exit(r1)     // Catch:{ all -> 0x00ff }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.input.InputManagerService.deliverInputDevicesChanged(android.view.InputDevice[]):void");
    }

    private String getDefaultKeyboardLayout(final InputDevice d) {
        final Locale systemLocale = this.mContext.getResources().getConfiguration().locale;
        if (TextUtils.isEmpty(systemLocale.getLanguage())) {
            return null;
        }
        final List<KeyboardLayout> layouts = new ArrayList<>();
        visitAllKeyboardLayouts(new KeyboardLayoutVisitor() {
            public void visitKeyboardLayout(Resources resources, int keyboardLayoutResId, KeyboardLayout layout) {
                if (layout.getVendorId() == d.getVendorId() && layout.getProductId() == d.getProductId()) {
                    LocaleList locales = layout.getLocales();
                    int numLocales = locales.size();
                    for (int localeIndex = 0; localeIndex < numLocales; localeIndex++) {
                        if (InputManagerService.isCompatibleLocale(systemLocale, locales.get(localeIndex))) {
                            layouts.add(layout);
                            return;
                        }
                    }
                }
            }
        });
        if (layouts.isEmpty()) {
            return null;
        }
        Collections.sort(layouts);
        int N = layouts.size();
        for (int i = 0; i < N; i++) {
            KeyboardLayout layout = layouts.get(i);
            LocaleList locales = layout.getLocales();
            int numLocales = locales.size();
            for (int localeIndex = 0; localeIndex < numLocales; localeIndex++) {
                Locale locale = locales.get(localeIndex);
                if (locale.getCountry().equals(systemLocale.getCountry()) && locale.getVariant().equals(systemLocale.getVariant())) {
                    return layout.getDescriptor();
                }
            }
        }
        for (int i2 = 0; i2 < N; i2++) {
            KeyboardLayout layout2 = layouts.get(i2);
            LocaleList locales2 = layout2.getLocales();
            int numLocales2 = locales2.size();
            for (int localeIndex2 = 0; localeIndex2 < numLocales2; localeIndex2++) {
                if (locales2.get(localeIndex2).getCountry().equals(systemLocale.getCountry())) {
                    return layout2.getDescriptor();
                }
            }
        }
        return layouts.get(0).getDescriptor();
    }

    /* access modifiers changed from: private */
    public static boolean isCompatibleLocale(Locale systemLocale, Locale keyboardLocale) {
        if (!systemLocale.getLanguage().equals(keyboardLocale.getLanguage())) {
            return false;
        }
        if (TextUtils.isEmpty(systemLocale.getCountry()) || TextUtils.isEmpty(keyboardLocale.getCountry()) || systemLocale.getCountry().equals(keyboardLocale.getCountry())) {
            return true;
        }
        return false;
    }

    public TouchCalibration getTouchCalibrationForInputDevice(String inputDeviceDescriptor, int surfaceRotation) {
        TouchCalibration touchCalibration;
        if (inputDeviceDescriptor != null) {
            synchronized (this.mDataStore) {
                touchCalibration = this.mDataStore.getTouchCalibration(inputDeviceDescriptor, surfaceRotation);
            }
            return touchCalibration;
        }
        throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public void setTouchCalibrationForInputDevice(String inputDeviceDescriptor, int surfaceRotation, TouchCalibration calibration) {
        if (!checkCallingPermission("android.permission.SET_INPUT_CALIBRATION", "setTouchCalibrationForInputDevice()")) {
            throw new SecurityException("Requires SET_INPUT_CALIBRATION permission");
        } else if (inputDeviceDescriptor == null) {
            throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
        } else if (calibration == null) {
            throw new IllegalArgumentException("calibration must not be null");
        } else if (surfaceRotation < 0 || surfaceRotation > 3) {
            throw new IllegalArgumentException("surfaceRotation value out of bounds");
        } else {
            synchronized (this.mDataStore) {
                try {
                    if (this.mDataStore.setTouchCalibration(inputDeviceDescriptor, surfaceRotation, calibration)) {
                        nativeReloadCalibration(this.mPtr);
                    }
                    this.mDataStore.saveIfNeeded();
                } catch (Throwable th) {
                    this.mDataStore.saveIfNeeded();
                    throw th;
                }
            }
        }
    }

    public int isInTabletMode() {
        if (checkCallingPermission("android.permission.TABLET_MODE", "isInTabletMode()")) {
            return getSwitchState(-1, -256, 1);
        }
        throw new SecurityException("Requires TABLET_MODE permission");
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void registerTabletModeChangedListener(ITabletModeChangedListener listener) {
        if (!checkCallingPermission("android.permission.TABLET_MODE", "registerTabletModeChangedListener()")) {
            throw new SecurityException("Requires TABLET_MODE_LISTENER permission");
        } else if (listener != null) {
            synchronized (this.mTabletModeLock) {
                int callingPid = Binder.getCallingPid();
                if (this.mTabletModeChangedListeners.get(callingPid) == null) {
                    TabletModeChangedListenerRecord record = new TabletModeChangedListenerRecord(callingPid, listener);
                    try {
                        listener.asBinder().linkToDeath(record, 0);
                        this.mTabletModeChangedListeners.put(callingPid, record);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    throw new IllegalStateException("The calling process has already registered a TabletModeChangedListener.");
                }
            }
        } else {
            throw new IllegalArgumentException("listener must not be null");
        }
    }

    /* access modifiers changed from: private */
    public void onTabletModeChangedListenerDied(int pid) {
        synchronized (this.mTabletModeLock) {
            this.mTabletModeChangedListeners.remove(pid);
        }
    }

    /* access modifiers changed from: private */
    public void deliverTabletModeChanged(long whenNanos, boolean inTabletMode) {
        int numListeners;
        this.mTempTabletModeChangedListenersToNotify.clear();
        synchronized (this.mTabletModeLock) {
            numListeners = this.mTabletModeChangedListeners.size();
            for (int i = 0; i < numListeners; i++) {
                this.mTempTabletModeChangedListenersToNotify.add(this.mTabletModeChangedListeners.valueAt(i));
            }
        }
        for (int i2 = 0; i2 < numListeners; i2++) {
            this.mTempTabletModeChangedListenersToNotify.get(i2).notifyTabletModeChanged(whenNanos, inTabletMode);
        }
    }

    private void showMissingKeyboardLayoutNotification(InputDevice device) {
        if (!this.mKeyboardLayoutNotificationShown) {
            Intent intent = new Intent("android.settings.HARD_KEYBOARD_SETTINGS");
            if (device != null) {
                intent.putExtra("input_device_identifier", device.getIdentifier());
            }
            intent.setFlags(337641472);
            PendingIntent keyboardLayoutIntent = PendingIntent.getActivityAsUser(this.mContext, 0, intent, 0, (Bundle) null, UserHandle.CURRENT);
            Resources r = this.mContext.getResources();
            this.mNotificationManager.notifyAsUser((String) null, 19, new Notification.Builder(this.mContext, SystemNotificationChannels.PHYSICAL_KEYBOARD).setContentTitle(r.getString(17041079)).setContentText(r.getString(17041078)).setContentIntent(keyboardLayoutIntent).setSmallIcon(17302812).setColor(this.mContext.getColor(17170460)).build(), UserHandle.ALL);
            this.mKeyboardLayoutNotificationShown = true;
        }
    }

    private void hideMissingKeyboardLayoutNotification() {
        if (this.mKeyboardLayoutNotificationShown) {
            this.mKeyboardLayoutNotificationShown = false;
            this.mNotificationManager.cancelAsUser((String) null, 19, UserHandle.ALL);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: private */
    public void updateKeyboardLayouts() {
        final HashSet<String> availableKeyboardLayouts = new HashSet<>();
        visitAllKeyboardLayouts(new KeyboardLayoutVisitor() {
            public void visitKeyboardLayout(Resources resources, int keyboardLayoutResId, KeyboardLayout layout) {
                availableKeyboardLayouts.add(layout.getDescriptor());
            }
        });
        synchronized (this.mDataStore) {
            try {
                this.mDataStore.removeUninstalledKeyboardLayouts(availableKeyboardLayouts);
                this.mDataStore.saveIfNeeded();
            } catch (Throwable th) {
                this.mDataStore.saveIfNeeded();
                throw th;
            }
        }
        reloadKeyboardLayouts();
    }

    private static boolean containsInputDeviceWithDescriptor(InputDevice[] inputDevices, String descriptor) {
        for (InputDevice inputDevice : inputDevices) {
            if (inputDevice.getDescriptor().equals(descriptor)) {
                return true;
            }
        }
        return false;
    }

    public KeyboardLayout[] getKeyboardLayouts() {
        final ArrayList<KeyboardLayout> list = new ArrayList<>();
        visitAllKeyboardLayouts(new KeyboardLayoutVisitor() {
            public void visitKeyboardLayout(Resources resources, int keyboardLayoutResId, KeyboardLayout layout) {
                list.add(layout);
            }
        });
        return (KeyboardLayout[]) list.toArray(new KeyboardLayout[list.size()]);
    }

    public KeyboardLayout[] getKeyboardLayoutsForInputDevice(InputDeviceIdentifier identifier) {
        String[] enabledLayoutDescriptors = getEnabledKeyboardLayoutsForInputDevice(identifier);
        ArrayList<KeyboardLayout> enabledLayouts = new ArrayList<>(enabledLayoutDescriptors.length);
        ArrayList<KeyboardLayout> potentialLayouts = new ArrayList<>();
        final String[] strArr = enabledLayoutDescriptors;
        final ArrayList<KeyboardLayout> arrayList = enabledLayouts;
        final InputDeviceIdentifier inputDeviceIdentifier = identifier;
        final ArrayList<KeyboardLayout> arrayList2 = potentialLayouts;
        visitAllKeyboardLayouts(new KeyboardLayoutVisitor() {
            boolean mHasSeenDeviceSpecificLayout;

            public void visitKeyboardLayout(Resources resources, int keyboardLayoutResId, KeyboardLayout layout) {
                String[] strArr = strArr;
                int length = strArr.length;
                int i = 0;
                while (i < length) {
                    String s = strArr[i];
                    if (s == null || !s.equals(layout.getDescriptor())) {
                        i++;
                    } else {
                        arrayList.add(layout);
                        return;
                    }
                }
                if (layout.getVendorId() == inputDeviceIdentifier.getVendorId() && layout.getProductId() == inputDeviceIdentifier.getProductId()) {
                    if (!this.mHasSeenDeviceSpecificLayout) {
                        this.mHasSeenDeviceSpecificLayout = true;
                        arrayList2.clear();
                    }
                    arrayList2.add(layout);
                } else if (layout.getVendorId() == -1 && layout.getProductId() == -1 && !this.mHasSeenDeviceSpecificLayout) {
                    arrayList2.add(layout);
                }
            }
        });
        int enabledLayoutSize = enabledLayouts.size();
        int potentialLayoutSize = potentialLayouts.size();
        KeyboardLayout[] layouts = new KeyboardLayout[(enabledLayoutSize + potentialLayoutSize)];
        enabledLayouts.toArray(layouts);
        for (int i = 0; i < potentialLayoutSize; i++) {
            layouts[enabledLayoutSize + i] = potentialLayouts.get(i);
        }
        return layouts;
    }

    public KeyboardLayout getKeyboardLayout(String keyboardLayoutDescriptor) {
        if (keyboardLayoutDescriptor != null) {
            final KeyboardLayout[] result = new KeyboardLayout[1];
            visitKeyboardLayout(keyboardLayoutDescriptor, new KeyboardLayoutVisitor() {
                public void visitKeyboardLayout(Resources resources, int keyboardLayoutResId, KeyboardLayout layout) {
                    result[0] = layout;
                }
            });
            if (result[0] == null) {
                Slog.w(TAG, "Could not get keyboard layout with descriptor '" + keyboardLayoutDescriptor + "'.");
            }
            return result[0];
        }
        throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
    }

    private void visitAllKeyboardLayouts(KeyboardLayoutVisitor visitor) {
        PackageManager pm = this.mContext.getPackageManager();
        for (ResolveInfo resolveInfo : pm.queryBroadcastReceivers(new Intent("android.hardware.input.action.QUERY_KEYBOARD_LAYOUTS"), 786560)) {
            visitKeyboardLayoutsInPackage(pm, resolveInfo.activityInfo, (String) null, resolveInfo.priority, visitor);
        }
    }

    private void visitKeyboardLayout(String keyboardLayoutDescriptor, KeyboardLayoutVisitor visitor) {
        KeyboardLayoutDescriptor d = KeyboardLayoutDescriptor.parse(keyboardLayoutDescriptor);
        if (d != null) {
            PackageManager pm = this.mContext.getPackageManager();
            try {
                visitKeyboardLayoutsInPackage(pm, pm.getReceiverInfo(new ComponentName(d.packageName, d.receiverName), 786560), d.keyboardLayoutName, 0, visitor);
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 25 */
    private void visitKeyboardLayoutsInPackage(PackageManager pm, ActivityInfo receiver, String keyboardName, int requestedPriority, KeyboardLayoutVisitor visitor) {
        int priority;
        XmlResourceParser parser;
        int configResId;
        Bundle metaData;
        int i;
        Resources resources;
        TypedArray a;
        KeyboardLayout keyboardLayout;
        int keyboardLayoutResId;
        PackageManager packageManager = pm;
        ActivityInfo activityInfo = receiver;
        String str = keyboardName;
        Bundle metaData2 = activityInfo.metaData;
        if (metaData2 != null) {
            int configResId2 = metaData2.getInt("android.hardware.input.metadata.KEYBOARD_LAYOUTS");
            if (configResId2 == 0) {
                Slog.w(TAG, "Missing meta-data 'android.hardware.input.metadata.KEYBOARD_LAYOUTS' on receiver " + activityInfo.packageName + SliceClientPermissions.SliceAuthority.DELIMITER + activityInfo.name);
                return;
            }
            CharSequence receiverLabel = activityInfo.loadLabel(packageManager);
            String collection = receiverLabel != null ? receiverLabel.toString() : "";
            int i2 = 1;
            if ((activityInfo.applicationInfo.flags & 1) != 0) {
                priority = requestedPriority;
            } else {
                priority = 0;
            }
            try {
                Resources resources2 = packageManager.getResourcesForApplication(activityInfo.applicationInfo);
                XmlResourceParser parser2 = resources2.getXml(configResId2);
                try {
                    XmlUtils.beginDocument(parser2, "keyboard-layouts");
                    while (true) {
                        XmlUtils.nextElement(parser2);
                        String element = parser2.getName();
                        if (element == null) {
                            try {
                                parser2.close();
                                KeyboardLayoutVisitor keyboardLayoutVisitor = visitor;
                                Bundle bundle = metaData2;
                                int i3 = configResId2;
                                return;
                            } catch (Exception e) {
                                ex = e;
                                KeyboardLayoutVisitor keyboardLayoutVisitor2 = visitor;
                                Bundle bundle2 = metaData2;
                                int i4 = configResId2;
                                Slog.w(TAG, "Could not parse keyboard layout resource from receiver " + activityInfo.packageName + SliceClientPermissions.SliceAuthority.DELIMITER + activityInfo.name, ex);
                            }
                        } else {
                            if (element.equals("keyboard-layout")) {
                                TypedArray a2 = resources2.obtainAttributes(parser2, R.styleable.KeyboardLayout);
                                try {
                                    String name = a2.getString(i2);
                                    String label = a2.getString(0);
                                    int keyboardLayoutResId2 = a2.getResourceId(2, 0);
                                    LocaleList locales = getLocalesFromLanguageTags(a2.getString(3));
                                    metaData = metaData2;
                                    try {
                                        a = a2;
                                        int vid = a2.getInt(5, -1);
                                        configResId = configResId2;
                                        try {
                                            int pid = a.getInt(4, -1);
                                            if (name == null || label == null) {
                                                String str2 = name;
                                                parser = parser2;
                                                String str3 = element;
                                                resources = resources2;
                                                int i5 = keyboardLayoutResId2;
                                                i = 1;
                                                KeyboardLayoutVisitor keyboardLayoutVisitor3 = visitor;
                                            } else if (keyboardLayoutResId2 == 0) {
                                                String str4 = name;
                                                parser = parser2;
                                                String str5 = element;
                                                resources = resources2;
                                                int i6 = keyboardLayoutResId2;
                                                i = 1;
                                                KeyboardLayoutVisitor keyboardLayoutVisitor4 = visitor;
                                            } else {
                                                String str6 = element;
                                                String element2 = KeyboardLayoutDescriptor.format(activityInfo.packageName, activityInfo.name, name);
                                                if (str != null) {
                                                    try {
                                                        if (!name.equals(str)) {
                                                            parser = parser2;
                                                            resources = resources2;
                                                            i = 1;
                                                            KeyboardLayoutVisitor keyboardLayoutVisitor5 = visitor;
                                                            a.recycle();
                                                        }
                                                    } catch (Throwable th) {
                                                        th = th;
                                                        XmlResourceParser xmlResourceParser = parser2;
                                                        Resources resources3 = resources2;
                                                        KeyboardLayoutVisitor keyboardLayoutVisitor6 = visitor;
                                                        a.recycle();
                                                        throw th;
                                                    }
                                                }
                                                try {
                                                    parser = parser2;
                                                    resources = resources2;
                                                    String str7 = name;
                                                    keyboardLayoutResId = keyboardLayoutResId2;
                                                    i = 1;
                                                } catch (Throwable th2) {
                                                    th = th2;
                                                    XmlResourceParser xmlResourceParser2 = parser2;
                                                    Resources resources4 = resources2;
                                                    KeyboardLayoutVisitor keyboardLayoutVisitor7 = visitor;
                                                    a.recycle();
                                                    throw th;
                                                }
                                                try {
                                                    keyboardLayout = new KeyboardLayout(element2, label, collection, priority, locales, vid, pid);
                                                    visitor.visitKeyboardLayout(resources, keyboardLayoutResId, keyboardLayout);
                                                    a.recycle();
                                                } catch (Throwable th3) {
                                                    th = th3;
                                                    a.recycle();
                                                    throw th;
                                                }
                                            }
                                            Slog.w(TAG, "Missing required 'name', 'label' or 'keyboardLayout' attributes in keyboard layout resource from receiver " + activityInfo.packageName + SliceClientPermissions.SliceAuthority.DELIMITER + activityInfo.name);
                                            a.recycle();
                                        } catch (Throwable th4) {
                                            th = th4;
                                            try {
                                                parser.close();
                                                throw th;
                                            } catch (Exception e2) {
                                                ex = e2;
                                                Slog.w(TAG, "Could not parse keyboard layout resource from receiver " + activityInfo.packageName + SliceClientPermissions.SliceAuthority.DELIMITER + activityInfo.name, ex);
                                            }
                                        }
                                    } catch (Throwable th5) {
                                        th = th5;
                                        int i7 = configResId2;
                                        XmlResourceParser xmlResourceParser3 = parser2;
                                        String str8 = element;
                                        Resources resources5 = resources2;
                                        a = a2;
                                        KeyboardLayoutVisitor keyboardLayoutVisitor8 = visitor;
                                        a.recycle();
                                        throw th;
                                    }
                                } catch (Throwable th6) {
                                    th = th6;
                                    Bundle bundle3 = metaData2;
                                    int i8 = configResId2;
                                    XmlResourceParser xmlResourceParser4 = parser2;
                                    String str9 = element;
                                    Resources resources6 = resources2;
                                    a = a2;
                                    KeyboardLayoutVisitor keyboardLayoutVisitor9 = visitor;
                                    a.recycle();
                                    throw th;
                                }
                            } else {
                                metaData = metaData2;
                                configResId = configResId2;
                                parser = parser2;
                                resources = resources2;
                                i = i2;
                                KeyboardLayoutVisitor keyboardLayoutVisitor10 = visitor;
                                Slog.w(TAG, "Skipping unrecognized element '" + element + "' in keyboard layout resource from receiver " + activityInfo.packageName + SliceClientPermissions.SliceAuthority.DELIMITER + activityInfo.name);
                            }
                            PackageManager packageManager2 = pm;
                            resources2 = resources;
                            i2 = i;
                            metaData2 = metaData;
                            configResId2 = configResId;
                            parser2 = parser;
                            str = keyboardName;
                        }
                    }
                } catch (Throwable th7) {
                    th = th7;
                    Bundle bundle4 = metaData2;
                    int i9 = configResId2;
                    parser = parser2;
                    Resources resources7 = resources2;
                    KeyboardLayoutVisitor keyboardLayoutVisitor11 = visitor;
                    parser.close();
                    throw th;
                }
            } catch (Exception e3) {
                ex = e3;
                KeyboardLayoutVisitor keyboardLayoutVisitor12 = visitor;
                Bundle bundle5 = metaData2;
                int i10 = configResId2;
                Slog.w(TAG, "Could not parse keyboard layout resource from receiver " + activityInfo.packageName + SliceClientPermissions.SliceAuthority.DELIMITER + activityInfo.name, ex);
            }
        }
    }

    private static LocaleList getLocalesFromLanguageTags(String languageTags) {
        if (TextUtils.isEmpty(languageTags)) {
            return LocaleList.getEmptyLocaleList();
        }
        return LocaleList.forLanguageTags(languageTags.replace('|', ','));
    }

    private String getLayoutDescriptor(InputDeviceIdentifier identifier) {
        if (identifier == null || identifier.getDescriptor() == null) {
            throw new IllegalArgumentException("identifier and descriptor must not be null");
        } else if (identifier.getVendorId() == 0 && identifier.getProductId() == 0) {
            return identifier.getDescriptor();
        } else {
            return "vendor:" + identifier.getVendorId() + ",product:" + identifier.getProductId();
        }
    }

    public String getCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier) {
        String layout;
        String key = getLayoutDescriptor(identifier);
        synchronized (this.mDataStore) {
            layout = this.mDataStore.getCurrentKeyboardLayout(key);
            if (layout == null && !key.equals(identifier.getDescriptor())) {
                layout = this.mDataStore.getCurrentKeyboardLayout(identifier.getDescriptor());
            }
        }
        return layout;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void setCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, String keyboardLayoutDescriptor) {
        if (!checkCallingPermission("android.permission.SET_KEYBOARD_LAYOUT", "setCurrentKeyboardLayoutForInputDevice()")) {
            throw new SecurityException("Requires SET_KEYBOARD_LAYOUT permission");
        } else if (keyboardLayoutDescriptor != null) {
            String key = getLayoutDescriptor(identifier);
            synchronized (this.mDataStore) {
                try {
                    if (this.mDataStore.setCurrentKeyboardLayout(key, keyboardLayoutDescriptor)) {
                        this.mHandler.sendEmptyMessage(3);
                    }
                    this.mDataStore.saveIfNeeded();
                } catch (Throwable th) {
                    this.mDataStore.saveIfNeeded();
                    throw th;
                }
            }
        } else {
            throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
        }
    }

    public String[] getEnabledKeyboardLayoutsForInputDevice(InputDeviceIdentifier identifier) {
        String[] layouts;
        String key = getLayoutDescriptor(identifier);
        synchronized (this.mDataStore) {
            layouts = this.mDataStore.getKeyboardLayouts(key);
            if ((layouts == null || layouts.length == 0) && !key.equals(identifier.getDescriptor())) {
                layouts = this.mDataStore.getKeyboardLayouts(identifier.getDescriptor());
            }
        }
        return layouts;
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void addKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, String keyboardLayoutDescriptor) {
        if (!checkCallingPermission("android.permission.SET_KEYBOARD_LAYOUT", "addKeyboardLayoutForInputDevice()")) {
            throw new SecurityException("Requires SET_KEYBOARD_LAYOUT permission");
        } else if (keyboardLayoutDescriptor != null) {
            String key = getLayoutDescriptor(identifier);
            synchronized (this.mDataStore) {
                try {
                    String oldLayout = this.mDataStore.getCurrentKeyboardLayout(key);
                    if (oldLayout == null && !key.equals(identifier.getDescriptor())) {
                        oldLayout = this.mDataStore.getCurrentKeyboardLayout(identifier.getDescriptor());
                    }
                    if (this.mDataStore.addKeyboardLayout(key, keyboardLayoutDescriptor) && !Objects.equals(oldLayout, this.mDataStore.getCurrentKeyboardLayout(key))) {
                        this.mHandler.sendEmptyMessage(3);
                    }
                    this.mDataStore.saveIfNeeded();
                } catch (Throwable th) {
                    this.mDataStore.saveIfNeeded();
                    throw th;
                }
            }
        } else {
            throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void removeKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, String keyboardLayoutDescriptor) {
        if (!checkCallingPermission("android.permission.SET_KEYBOARD_LAYOUT", "removeKeyboardLayoutForInputDevice()")) {
            throw new SecurityException("Requires SET_KEYBOARD_LAYOUT permission");
        } else if (keyboardLayoutDescriptor != null) {
            String key = getLayoutDescriptor(identifier);
            synchronized (this.mDataStore) {
                try {
                    String oldLayout = this.mDataStore.getCurrentKeyboardLayout(key);
                    if (oldLayout == null && !key.equals(identifier.getDescriptor())) {
                        oldLayout = this.mDataStore.getCurrentKeyboardLayout(identifier.getDescriptor());
                    }
                    boolean removed = this.mDataStore.removeKeyboardLayout(key, keyboardLayoutDescriptor);
                    if (!key.equals(identifier.getDescriptor())) {
                        removed |= this.mDataStore.removeKeyboardLayout(identifier.getDescriptor(), keyboardLayoutDescriptor);
                    }
                    if (removed && !Objects.equals(oldLayout, this.mDataStore.getCurrentKeyboardLayout(key))) {
                        this.mHandler.sendEmptyMessage(3);
                    }
                    this.mDataStore.saveIfNeeded();
                } catch (Throwable th) {
                    this.mDataStore.saveIfNeeded();
                    throw th;
                }
            }
        } else {
            throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
        }
    }

    public void switchKeyboardLayout(int deviceId, int direction) {
        this.mHandler.obtainMessage(2, deviceId, direction).sendToTarget();
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* access modifiers changed from: private */
    public void handleSwitchKeyboardLayout(int deviceId, int direction) {
        boolean changed;
        String keyboardLayoutDescriptor;
        KeyboardLayout keyboardLayout;
        InputDevice device = getInputDevice(deviceId);
        if (device != null) {
            String key = getLayoutDescriptor(device.getIdentifier());
            synchronized (this.mDataStore) {
                try {
                    changed = this.mDataStore.switchKeyboardLayout(key, direction);
                    keyboardLayoutDescriptor = this.mDataStore.getCurrentKeyboardLayout(key);
                    this.mDataStore.saveIfNeeded();
                } catch (Throwable th) {
                    this.mDataStore.saveIfNeeded();
                    throw th;
                }
            }
            if (changed) {
                Toast toast = this.mSwitchedKeyboardLayoutToast;
                if (toast != null) {
                    toast.cancel();
                    this.mSwitchedKeyboardLayoutToast = null;
                }
                if (!(keyboardLayoutDescriptor == null || (keyboardLayout = getKeyboardLayout(keyboardLayoutDescriptor)) == null)) {
                    this.mSwitchedKeyboardLayoutToast = Toast.makeText(this.mContext, keyboardLayout.getLabel(), 0);
                    this.mSwitchedKeyboardLayoutToast.show();
                }
                reloadKeyboardLayouts();
            }
        }
    }

    public void setFocusedApplication(int displayId, InputApplicationHandle application) {
        nativeSetFocusedApplication(this.mPtr, displayId, application);
    }

    public void setFocusedDisplay(int displayId) {
        nativeSetFocusedDisplay(this.mPtr, displayId);
    }

    public void onDisplayRemoved(int displayId) {
        nativeSetInputWindows(this.mPtr, (InputWindowHandle[]) null, displayId);
    }

    public void requestPointerCapture(IBinder windowToken, boolean enabled) {
        IWindow iWindow = this.mFocusedWindow;
        if (iWindow == null || iWindow.asBinder() != windowToken) {
            Slog.e(TAG, "requestPointerCapture called for a window that has no focus: " + windowToken);
        } else if (this.mFocusedWindowHasCapture == enabled) {
            StringBuilder sb = new StringBuilder();
            sb.append("requestPointerCapture: already ");
            sb.append(enabled ? "enabled" : "disabled");
            Slog.i(TAG, sb.toString());
        } else {
            setPointerCapture(enabled);
        }
    }

    private void setPointerCapture(boolean enabled) {
        if (this.mFocusedWindowHasCapture != enabled) {
            this.mFocusedWindowHasCapture = enabled;
            try {
                this.mFocusedWindow.dispatchPointerCaptureChanged(enabled);
            } catch (RemoteException e) {
            }
            nativeSetPointerCapture(this.mPtr, enabled);
        }
    }

    public void setInputDispatchMode(boolean enabled, boolean frozen) {
        nativeSetInputDispatchMode(this.mPtr, enabled, frozen);
    }

    public void setSystemUiVisibility(int visibility) {
        nativeSetSystemUiVisibility(this.mPtr, visibility);
    }

    public void tryPointerSpeed(int speed) {
        if (!checkCallingPermission("android.permission.SET_POINTER_SPEED", "tryPointerSpeed()")) {
            throw new SecurityException("Requires SET_POINTER_SPEED permission");
        } else if (speed < -7 || speed > 7) {
            throw new IllegalArgumentException("speed out of range");
        } else {
            setPointerSpeedUnchecked(speed);
        }
    }

    public void updatePointerSpeedFromSettings() {
        setPointerSpeedUnchecked(getPointerSpeedSetting());
    }

    private void setPointerSpeedUnchecked(int speed) {
        nativeSetPointerSpeed(this.mPtr, Math.min(Math.max(speed, -7), 7));
    }

    private void registerPointerSpeedSettingObserver() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("pointer_speed"), true, new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                InputManagerService.this.updatePointerSpeedFromSettings();
            }
        }, -1);
    }

    private int getPointerSpeedSetting() {
        try {
            return Settings.System.getIntForUser(this.mContext.getContentResolver(), "pointer_speed", -2);
        } catch (Settings.SettingNotFoundException e) {
            return 0;
        }
    }

    public void updateShowTouchesFromSettings() {
        int i = 0;
        int setting = getShowTouchesSetting(0);
        nativeSetShowTouches(this.mPtr, setting != 0);
        if (setting != 0) {
            i = 1;
        }
        this.mInputDebugLevel = i;
        nativeSetDebugInput(this.mPtr, this.mInputDebugLevel);
    }

    private void registerShowTouchesSettingObserver() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("show_touches"), true, new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                InputManagerService.this.updateShowTouchesFromSettings();
            }
        }, -1);
    }

    public void updateAccessibilityLargePointerFromSettings() {
        boolean z = false;
        if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_large_pointer_icon", 0, -2) == 1) {
            z = true;
        }
        PointerIcon.setUseLargeIcons(z);
        nativeReloadPointerIcons(this.mPtr);
    }

    public void updateTouchWakeupModeFromSettings() {
        switchTouchWakeupMode(MiuiSettings.System.getBoolean(this.mContext.getContentResolver(), "gesture_wakeup", false));
    }

    private void registerTouchWakeupModeSettingObserver() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("gesture_wakeup"), false, new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                InputManagerService.this.updateTouchWakeupModeFromSettings();
            }
        }, -1);
    }

    private void registerAccessibilityLargePointerSettingObserver() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("accessibility_large_pointer_icon"), true, new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                InputManagerService.this.updateAccessibilityLargePointerFromSettings();
            }
        }, -1);
    }

    private int getShowTouchesSetting(int defaultValue) {
        try {
            return Settings.System.getIntForUser(this.mContext.getContentResolver(), "show_touches", -2);
        } catch (Settings.SettingNotFoundException e) {
            return defaultValue;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public void vibrate(int deviceId, long[] pattern, int repeat, IBinder token) {
        VibratorToken v;
        if (repeat < pattern.length) {
            synchronized (this.mVibratorLock) {
                v = this.mVibratorTokens.get(token);
                if (v == null) {
                    int i = this.mNextVibratorTokenValue;
                    this.mNextVibratorTokenValue = i + 1;
                    v = new VibratorToken(deviceId, token, i);
                    try {
                        token.linkToDeath(v, 0);
                        this.mVibratorTokens.put(token, v);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            synchronized (v) {
                v.mVibrating = true;
                nativeVibrate(this.mPtr, deviceId, pattern, repeat, v.mTokenValue);
            }
            return;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public void cancelVibrate(int deviceId, IBinder token) {
        synchronized (this.mVibratorLock) {
            VibratorToken v = this.mVibratorTokens.get(token);
            if (v != null) {
                if (v.mDeviceId == deviceId) {
                    cancelVibrateIfNeeded(v);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onVibratorTokenDied(VibratorToken v) {
        synchronized (this.mVibratorLock) {
            this.mVibratorTokens.remove(v.mToken);
        }
        cancelVibrateIfNeeded(v);
    }

    private void cancelVibrateIfNeeded(VibratorToken v) {
        synchronized (v) {
            if (v.mVibrating) {
                nativeCancelVibrate(this.mPtr, v.mDeviceId, v.mTokenValue);
                v.mVibrating = false;
            }
        }
    }

    public void setPointerIconType(int iconId) {
        nativeSetPointerIconType(this.mPtr, iconId);
    }

    public void setCustomPointerIcon(PointerIcon icon) {
        Preconditions.checkNotNull(icon);
        nativeSetCustomPointerIcon(this.mPtr, icon);
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (DumpUtils.checkDumpPermission(this.mContext, TAG, pw)) {
            pw.println("INPUT MANAGER (dumpsys input)\n");
            if (args.length == 2 && "debuglog".equals(args[0])) {
                try {
                    pw.println("open Reader and Dispatcher debuglog, " + args[0] + " " + args[1]);
                    this.mInputDebugLevel = Integer.parseInt(args[1]);
                    nativeSetDebugInput(this.mPtr, this.mInputDebugLevel);
                    return;
                } catch (ClassCastException e) {
                    pw.println("open Reader and Dispatcher debuglog, ClassCastException!!!");
                }
            }
            String dumpStr = nativeDump(this.mPtr);
            if (dumpStr != null) {
                pw.println(dumpStr);
            }
        }
    }

    private boolean checkCallingPermission(String permission, String func) {
        if (Binder.getCallingPid() == Process.myPid() || this.mContext.checkCallingPermission(permission) == 0) {
            return true;
        }
        Slog.w(TAG, "Permission Denial: " + func + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + permission);
        return false;
    }

    public void monitor() {
        synchronized (this.mInputFilterLock) {
        }
        nativeMonitor(this.mPtr);
    }

    public void switchTouchSensitiveMode(boolean modeOn) {
        Slog.d(TAG, "glove modeOn = " + modeOn);
        if (modeOn) {
            nativeSwitchTouchWorkMode(this.mPtr, 1);
        } else {
            nativeSwitchTouchWorkMode(this.mPtr, 0);
        }
    }

    public void switchTouchStylusMode(boolean modeOn) {
        Slog.d(TAG, "stylus modeOn = " + modeOn);
        if (modeOn) {
            nativeSwitchTouchWorkMode(this.mPtr, 3);
        } else {
            nativeSwitchTouchWorkMode(this.mPtr, 2);
        }
    }

    public void switchTouchWakeupMode(boolean modeOn) {
        Slog.d(TAG, "wakeup modeOn = " + modeOn);
        if (modeOn) {
            nativeSwitchTouchWorkMode(this.mPtr, 5);
        } else {
            nativeSwitchTouchWorkMode(this.mPtr, 4);
        }
    }

    public void switchTouchCoverMode(boolean modeOn) {
        Slog.d(TAG, "cover modeOn = " + modeOn);
        if (modeOn) {
            nativeSwitchTouchWorkMode(this.mPtr, 7);
        } else {
            nativeSwitchTouchWorkMode(this.mPtr, 6);
        }
    }

    private void notifyConfigurationChanged(long whenNanos) {
        this.mWindowManagerCallbacks.notifyConfigurationChanged();
    }

    private void notifyInputDevicesChanged(InputDevice[] inputDevices) {
        synchronized (this.mInputDevicesLock) {
            if (!this.mInputDevicesChangedPending) {
                this.mInputDevicesChangedPending = true;
                this.mHandler.obtainMessage(1, this.mInputDevices).sendToTarget();
            }
            this.mInputDevices = inputDevices;
        }
    }

    private void notifySwitch(long whenNanos, int switchValues, int switchMask) {
        boolean z = false;
        if ((switchMask & 1) != 0) {
            this.mWindowManagerCallbacks.notifyLidSwitchChanged(whenNanos, (switchValues & 1) == 0);
        }
        if (switchMask != false && true) {
            this.mWindowManagerCallbacks.notifyCameraLensCoverSwitchChanged(whenNanos, (switchValues & 512) != 0);
        }
        if (this.mUseDevInputEventForAudioJack && (switchMask & SW_JACK_BITS) != 0) {
            this.mWiredAccessoryCallbacks.notifyWiredAccessoryChanged(whenNanos, switchValues, switchMask);
        }
        if ((switchMask & 2) != 0) {
            SomeArgs args = SomeArgs.obtain();
            args.argi1 = (int) (-1 & whenNanos);
            args.argi2 = (int) (whenNanos >> 32);
            if ((switchValues & 2) != 0) {
                z = true;
            }
            args.arg1 = Boolean.valueOf(z);
            this.mHandler.obtainMessage(6, args).sendToTarget();
        }
    }

    private void notifyInputChannelBroken(IBinder token) {
        this.mWindowManagerCallbacks.notifyInputChannelBroken(token);
    }

    private void notifyFocusChanged(IBinder oldToken, IBinder newToken) {
        IWindow iWindow = this.mFocusedWindow;
        if (iWindow != null) {
            if (iWindow.asBinder() == newToken) {
                Slog.w(TAG, "notifyFocusChanged called with unchanged mFocusedWindow=" + this.mFocusedWindow);
                return;
            }
            setPointerCapture(false);
        }
        this.mFocusedWindow = IWindow.Stub.asInterface(newToken);
    }

    private long notifyANR(IBinder token, String reason) {
        return this.mWindowManagerCallbacks.notifyANR(token, reason);
    }

    /* access modifiers changed from: package-private */
    public final boolean filterInputEvent(InputEvent event, int policyFlags) {
        synchronized (this.mInputFilterLock) {
            if (this.mInputFilter != null) {
                try {
                    this.mInputFilter.filterInputEvent(event, policyFlags);
                } catch (RemoteException e) {
                }
                return false;
            }
            event.recycle();
            return true;
        }
    }

    private int interceptKeyBeforeQueueing(KeyEvent event, int policyFlags) {
        return this.mWindowManagerCallbacks.interceptKeyBeforeQueueing(event, policyFlags);
    }

    private int interceptMotionBeforeQueueingNonInteractive(int displayId, long whenNanos, int policyFlags) {
        return this.mWindowManagerCallbacks.interceptMotionBeforeQueueingNonInteractive(displayId, whenNanos, policyFlags);
    }

    private long interceptKeyBeforeDispatching(IBinder focus, KeyEvent event, int policyFlags) {
        return this.mWindowManagerCallbacks.interceptKeyBeforeDispatching(focus, event, policyFlags);
    }

    private KeyEvent dispatchUnhandledKey(IBinder focus, KeyEvent event, int policyFlags) {
        return this.mWindowManagerCallbacks.dispatchUnhandledKey(focus, event, policyFlags);
    }

    private boolean checkInjectEventsPermission(int injectorPid, int injectorUid) {
        return this.mContext.checkPermission("android.permission.INJECT_EVENTS", injectorPid, injectorUid) == 0;
    }

    private void onPointerDownOutsideFocus(IBinder touchedToken) {
        this.mWindowManagerCallbacks.onPointerDownOutsideFocus(touchedToken);
    }

    private int getVirtualKeyQuietTimeMillis() {
        return this.mContext.getResources().getInteger(17694911);
    }

    private static String[] getExcludedDeviceNames() {
        List<String> names = new ArrayList<>();
        for (File baseDir : new File[]{Environment.getRootDirectory(), Environment.getVendorDirectory()}) {
            File confFile = new File(baseDir, EXCLUDED_DEVICES_PATH);
            try {
                names.addAll(ConfigurationProcessor.processExcludedDeviceNames(new FileInputStream(confFile)));
            } catch (FileNotFoundException e) {
            } catch (Exception e2) {
                Slog.e(TAG, "Could not parse '" + confFile.getAbsolutePath() + "'", e2);
            }
        }
        return (String[]) names.toArray(new String[0]);
    }

    private static <T> List<T> flatten(List<Pair<T, T>> pairs) {
        List<T> list = new ArrayList<>(pairs.size() * 2);
        for (Pair<T, T> pair : pairs) {
            list.add(pair.first);
            list.add(pair.second);
        }
        return list;
    }

    private static String[] getInputPortAssociations() {
        File confFile = new File(Environment.getVendorDirectory(), PORT_ASSOCIATIONS_PATH);
        try {
            return (String[]) flatten(ConfigurationProcessor.processInputPortAssociations(new FileInputStream(confFile))).toArray(new String[0]);
        } catch (FileNotFoundException e) {
            return new String[0];
        } catch (Exception e2) {
            Slog.e(TAG, "Could not parse '" + confFile.getAbsolutePath() + "'", e2);
            return new String[0];
        }
    }

    public boolean canDispatchToDisplay(int deviceId, int displayId) {
        return nativeCanDispatchToDisplay(this.mPtr, deviceId, displayId);
    }

    private int getKeyRepeatTimeout() {
        return ViewConfiguration.getKeyRepeatTimeout();
    }

    private int getKeyRepeatDelay() {
        return ViewConfiguration.getKeyRepeatDelay();
    }

    private int getHoverTapTimeout() {
        return ViewConfiguration.getHoverTapTimeout();
    }

    private int getHoverTapSlop() {
        return ViewConfiguration.getHoverTapSlop();
    }

    private int getDoubleTapTimeout() {
        return ViewConfiguration.getDoubleTapTimeout();
    }

    private int getLongPressTimeout() {
        return ViewConfiguration.getLongPressTimeout();
    }

    private int getPointerLayer() {
        return this.mWindowManagerCallbacks.getPointerLayer();
    }

    private PointerIcon getPointerIcon(int displayId) {
        return PointerIcon.getDefaultIcon(getContextForDisplay(displayId));
    }

    private Context getContextForDisplay(int displayId) {
        Context context = this.mDisplayContext;
        if (context != null && context.getDisplay().getDisplayId() == displayId) {
            return this.mDisplayContext;
        }
        if (this.mContext.getDisplay().getDisplayId() == displayId) {
            this.mDisplayContext = this.mContext;
            return this.mDisplayContext;
        }
        this.mDisplayContext = this.mContext.createDisplayContext(((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).getDisplay(displayId));
        return this.mDisplayContext;
    }

    private int getPointerDisplayId() {
        return this.mWindowManagerCallbacks.getPointerDisplayId();
    }

    private String[] getKeyboardLayoutOverlay(InputDeviceIdentifier identifier) {
        String keyboardLayoutDescriptor;
        if (!this.mSystemReady || (keyboardLayoutDescriptor = getCurrentKeyboardLayoutForInputDevice(identifier)) == null) {
            return null;
        }
        final String[] result = new String[2];
        visitKeyboardLayout(keyboardLayoutDescriptor, new KeyboardLayoutVisitor() {
            public void visitKeyboardLayout(Resources resources, int keyboardLayoutResId, KeyboardLayout layout) {
                try {
                    result[0] = layout.getDescriptor();
                    result[1] = Streams.readFully(new InputStreamReader(resources.openRawResource(keyboardLayoutResId)));
                } catch (Resources.NotFoundException | IOException e) {
                }
            }
        });
        if (result[0] != null) {
            return result;
        }
        Slog.w(TAG, "Could not get keyboard layout with descriptor '" + keyboardLayoutDescriptor + "'.");
        return null;
    }

    private String getDeviceAlias(String uniqueId) {
        return BluetoothAdapter.checkBluetoothAddress(uniqueId) ? null : null;
    }

    private final class InputManagerHandler extends Handler {
        public InputManagerHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    InputManagerService.this.deliverInputDevicesChanged((InputDevice[]) msg.obj);
                    return;
                case 2:
                    InputManagerService.this.handleSwitchKeyboardLayout(msg.arg1, msg.arg2);
                    return;
                case 3:
                    InputManagerService.this.reloadKeyboardLayouts();
                    return;
                case 4:
                    InputManagerService.this.updateKeyboardLayouts();
                    return;
                case 5:
                    InputManagerService.this.reloadDeviceAliases();
                    return;
                case 6:
                    SomeArgs args = (SomeArgs) msg.obj;
                    InputManagerService.this.deliverTabletModeChanged((((long) args.argi1) & 4294967295L) | (((long) args.argi2) << 32), ((Boolean) args.arg1).booleanValue());
                    return;
                default:
                    return;
            }
        }
    }

    private final class InputFilterHost extends IInputFilterHost.Stub {
        private boolean mDisconnected;

        private InputFilterHost() {
        }

        public void disconnectLocked() {
            this.mDisconnected = true;
        }

        public void sendInputEvent(InputEvent event, int policyFlags) {
            if (event != null) {
                synchronized (InputManagerService.this.mInputFilterLock) {
                    if (!this.mDisconnected) {
                        int unused = InputManagerService.nativeInjectInputEvent(InputManagerService.this.mPtr, event, 0, 0, 0, 0, policyFlags | BroadcastQueueInjector.FLAG_IMMUTABLE);
                    }
                }
                return;
            }
            throw new IllegalArgumentException("event must not be null");
        }
    }

    private final class InputMonitorHost extends IInputMonitorHost.Stub {
        private final InputChannel mInputChannel;

        InputMonitorHost(InputChannel channel) {
            this.mInputChannel = channel;
        }

        public void pilferPointers() {
            InputManagerService.nativePilferPointers(InputManagerService.this.mPtr, asBinder());
        }

        public void dispose() {
            InputManagerService.nativeUnregisterInputChannel(InputManagerService.this.mPtr, this.mInputChannel);
            this.mInputChannel.dispose();
        }
    }

    private static final class KeyboardLayoutDescriptor {
        public String keyboardLayoutName;
        public String packageName;
        public String receiverName;

        private KeyboardLayoutDescriptor() {
        }

        public static String format(String packageName2, String receiverName2, String keyboardName) {
            return packageName2 + SliceClientPermissions.SliceAuthority.DELIMITER + receiverName2 + SliceClientPermissions.SliceAuthority.DELIMITER + keyboardName;
        }

        public static KeyboardLayoutDescriptor parse(String descriptor) {
            int pos2;
            int pos = descriptor.indexOf(47);
            if (pos < 0 || pos + 1 == descriptor.length() || (pos2 = descriptor.indexOf(47, pos + 1)) < pos + 2 || pos2 + 1 == descriptor.length()) {
                return null;
            }
            KeyboardLayoutDescriptor result = new KeyboardLayoutDescriptor();
            result.packageName = descriptor.substring(0, pos);
            result.receiverName = descriptor.substring(pos + 1, pos2);
            result.keyboardLayoutName = descriptor.substring(pos2 + 1);
            return result;
        }
    }

    private final class InputDevicesChangedListenerRecord implements IBinder.DeathRecipient {
        private final IInputDevicesChangedListener mListener;
        private final int mPid;

        public InputDevicesChangedListenerRecord(int pid, IInputDevicesChangedListener listener) {
            this.mPid = pid;
            this.mListener = listener;
        }

        public void binderDied() {
            InputManagerService.this.onInputDevicesChangedListenerDied(this.mPid);
        }

        public void notifyInputDevicesChanged(int[] info) {
            try {
                this.mListener.onInputDevicesChanged(info);
            } catch (RemoteException ex) {
                Slog.w(InputManagerService.TAG, "Failed to notify process " + this.mPid + " that input devices changed, assuming it died.", ex);
                binderDied();
            }
        }
    }

    private final class TabletModeChangedListenerRecord implements IBinder.DeathRecipient {
        private final ITabletModeChangedListener mListener;
        private final int mPid;

        public TabletModeChangedListenerRecord(int pid, ITabletModeChangedListener listener) {
            this.mPid = pid;
            this.mListener = listener;
        }

        public void binderDied() {
            InputManagerService.this.onTabletModeChangedListenerDied(this.mPid);
        }

        public void notifyTabletModeChanged(long whenNanos, boolean inTabletMode) {
            try {
                this.mListener.onTabletModeChanged(whenNanos, inTabletMode);
            } catch (RemoteException ex) {
                Slog.w(InputManagerService.TAG, "Failed to notify process " + this.mPid + " that tablet mode changed, assuming it died.", ex);
                binderDied();
            }
        }
    }

    private final class VibratorToken implements IBinder.DeathRecipient {
        public final int mDeviceId;
        public final IBinder mToken;
        public final int mTokenValue;
        public boolean mVibrating;

        public VibratorToken(int deviceId, IBinder token, int tokenValue) {
            this.mDeviceId = deviceId;
            this.mToken = token;
            this.mTokenValue = tokenValue;
        }

        public void binderDied() {
            InputManagerService.this.onVibratorTokenDied(this);
        }
    }

    private final class LocalService extends InputManagerInternal {
        private LocalService() {
        }

        public void setDisplayViewports(List<DisplayViewport> viewports) {
            InputManagerService.this.setDisplayViewportsInternal(viewports);
        }

        public boolean injectInputEvent(InputEvent event, int mode) {
            return InputManagerService.this.injectInputEventInternal(event, mode);
        }

        public void setInteractive(boolean interactive) {
            InputManagerService.nativeSetInteractive(InputManagerService.this.mPtr, interactive);
        }

        public void toggleCapsLock(int deviceId) {
            InputManagerService.nativeToggleCapsLock(InputManagerService.this.mPtr, deviceId);
        }

        public void setPulseGestureEnabled(boolean enabled) {
            if (InputManagerService.this.mDoubleTouchGestureEnableFile != null) {
                FileWriter writer = null;
                try {
                    writer = new FileWriter(InputManagerService.this.mDoubleTouchGestureEnableFile);
                    writer.write(enabled ? SplitScreenReporter.ACTION_ENTER_SPLIT : "0");
                } catch (IOException e) {
                    Log.wtf(InputManagerService.TAG, "Unable to setPulseGestureEnabled", e);
                } catch (Throwable th) {
                    IoUtils.closeQuietly((AutoCloseable) null);
                    throw th;
                }
                IoUtils.closeQuietly(writer);
            }
        }
    }
}
