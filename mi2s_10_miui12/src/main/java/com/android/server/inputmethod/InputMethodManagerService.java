package com.android.server.inputmethod;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityThread;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageStatsManagerInternal;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManagerInternal;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.LocaleList;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.UserManagerInternal;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.style.SuggestionSpan;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.EventLog;
import android.util.LruCache;
import android.util.Pair;
import android.util.PrintWriterPrinter;
import android.util.Printer;
import android.util.Slog;
import android.util.SparseArray;
import android.view.IWindowManager;
import android.view.InputChannel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import android.view.inputmethod.InputMethodSystemProperty;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import com.android.internal.R;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import com.android.internal.inputmethod.IInputContentUriToken;
import com.android.internal.inputmethod.IInputMethodPrivilegedOperations;
import com.android.internal.inputmethod.InputMethodDebug;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.os.HandlerCaller;
import com.android.internal.os.TransferPipe;
import com.android.internal.util.DumpUtils;
import com.android.internal.view.IInputContext;
import com.android.internal.view.IInputMethod;
import com.android.internal.view.IInputMethodClient;
import com.android.internal.view.IInputMethodManager;
import com.android.internal.view.IInputMethodSession;
import com.android.internal.view.IInputSessionCallback;
import com.android.internal.view.InputBindResult;
import com.android.server.EventLogTags;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.inputmethod.InputMethodManagerService;
import com.android.server.inputmethod.InputMethodSubtypeSwitchingController;
import com.android.server.inputmethod.InputMethodUtils;
import com.android.server.statusbar.StatusBarManagerService;
import com.android.server.wm.ActivityTaskManagerInternal;
import com.android.server.wm.WindowManagerInternal;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import miui.app.AlertDialog;

public class InputMethodManagerService extends IInputMethodManager.Stub implements ServiceConnection, Handler.Callback {
    private static final String ACTION_SHOW_INPUT_METHOD_PICKER = "com.android.server.inputmethod.InputMethodManagerService.SHOW_INPUT_METHOD_PICKER";
    static final boolean DEBUG = false;
    private static final int FALLBACK_DISPLAY_ID = 0;
    private static final int IME_CONNECTION_BIND_FLAGS = 1082130437;
    private static final int IME_VISIBLE_BIND_FLAGS = 738725889;
    static final int MSG_APPLY_IME_VISIBILITY = 3070;
    static final int MSG_BIND_CLIENT = 3010;
    static final int MSG_BIND_INPUT = 1010;
    static final int MSG_CREATE_SESSION = 1050;
    static final int MSG_HARD_KEYBOARD_SWITCH_CHANGED = 4000;
    static final int MSG_HIDE_CURRENT_INPUT_METHOD = 1035;
    static final int MSG_HIDE_SOFT_INPUT = 1030;
    static final int MSG_INITIALIZE_IME = 1040;
    static final int MSG_REPORT_FULLSCREEN_MODE = 3045;
    static final int MSG_REPORT_PRE_RENDERED = 3060;
    static final int MSG_SET_ACTIVE = 3020;
    static final int MSG_SET_INTERACTIVE = 3030;
    static final int MSG_SHOW_IM_CONFIG = 3;
    static final int MSG_SHOW_IM_SUBTYPE_ENABLER = 2;
    static final int MSG_SHOW_IM_SUBTYPE_PICKER = 1;
    static final int MSG_SHOW_SOFT_INPUT = 1020;
    static final int MSG_START_INPUT = 2000;
    static final int MSG_SYSTEM_UNLOCK_USER = 5000;
    static final int MSG_UNBIND_CLIENT = 3000;
    static final int MSG_UNBIND_INPUT = 1000;
    private static final int NOT_A_SUBTYPE_ID = -1;
    static final int SECURE_SUGGESTION_SPANS_MAX_SIZE = 20;
    static final String TAG = "InputMethodManagerService";
    private static final String TAG_TRY_SUPPRESSING_IME_SWITCHER = "TrySuppressingImeSwitcher";
    static final long TIME_TO_RECONNECT = 3000;
    /* access modifiers changed from: private */
    public boolean mAccessibilityRequestingNoSoftKeyboard;
    private SparseArray<ActivityViewInfo> mActivityViewDisplayIdToParentMap = new SparseArray<>();
    /* access modifiers changed from: private */
    public final ArrayMap<String, List<InputMethodSubtype>> mAdditionalSubtypeMap = new ArrayMap<>();
    private final AppOpsManager mAppOpsManager;
    int mBackDisposition = 0;
    boolean mBoundToMethod;
    final HandlerCaller mCaller;
    final ArrayMap<IBinder, ClientState> mClients = new ArrayMap<>();
    final Context mContext;
    private Matrix mCurActivityViewToScreenMatrix = null;
    EditorInfo mCurAttribute;
    ClientState mCurClient;
    private boolean mCurClientInKeyguard;
    IBinder mCurFocusedWindow;
    ClientState mCurFocusedWindowClient;
    int mCurFocusedWindowSoftInputMode;
    String mCurId;
    IInputContext mCurInputContext;
    int mCurInputContextMissingMethods;
    Intent mCurIntent;
    IInputMethod mCurMethod;
    String mCurMethodId;
    int mCurSeq;
    IBinder mCurToken;
    int mCurTokenDisplayId = -1;
    private InputMethodSubtype mCurrentSubtype;
    private AlertDialog.Builder mDialogBuilder;
    private final DisplayManagerInternal mDisplayManagerInternal;
    SessionState mEnabledSession;
    final Handler mHandler;
    private final int mHardKeyboardBehavior;
    private final HardKeyboardListener mHardKeyboardListener;
    final boolean mHasFeature;
    boolean mHaveConnection;
    /* access modifiers changed from: private */
    public final IPackageManager mIPackageManager = AppGlobals.getPackageManager();
    final IWindowManager mIWindowManager;
    final ImeDisplayValidator mImeDisplayValidator;
    private PendingIntent mImeSwitchPendingIntent;
    private Notification.Builder mImeSwitcherNotification;
    @GuardedBy({"mMethodMap"})
    private final WeakHashMap<IBinder, IBinder> mImeTargetWindowMap = new WeakHashMap<>();
    int mImeWindowVis;
    /* access modifiers changed from: private */
    public InputMethodInfo[] mIms;
    boolean mInFullscreenMode;
    boolean mInputShown;
    boolean mIsInteractive = true;
    private final boolean mIsLowRam;
    private KeyguardManager mKeyguardManager;
    long mLastBindTime;
    IBinder mLastImeTargetWindow;
    private int mLastSwitchUserId;
    private LocaleList mLastSystemLocales;
    final ArrayList<InputMethodInfo> mMethodList = new ArrayList<>();
    final ArrayMap<String, InputMethodInfo> mMethodMap = new ArrayMap<>();
    @GuardedBy({"mMethodMap"})
    private int mMethodMapUpdateCount = 0;
    MiuiSecurityInputMethodHelper mMiuiSecurityInputMethodHelper;
    private final MyPackageMonitor mMyPackageMonitor = new MyPackageMonitor();
    private NotificationManager mNotificationManager;
    private boolean mNotificationShown;
    private String mPackageName;
    final Resources mRes;
    private final LruCache<SuggestionSpan, InputMethodInfo> mSecureSuggestionSpans = new LruCache<>(20);
    final InputMethodUtils.InputMethodSettings mSettings;
    final SettingsObserver mSettingsObserver;
    boolean mShowExplicitlyRequested;
    boolean mShowForced;
    private boolean mShowImeWithHardKeyboard;
    private boolean mShowOngoingImeSwitcherForPhones;
    boolean mShowRequested;
    private final String mSlotIme;
    @GuardedBy({"mMethodMap"})
    private final StartInputHistory mStartInputHistory = new StartInputHistory();
    private StatusBarManagerService mStatusBar;
    /* access modifiers changed from: private */
    public int[] mSubtypeIds;
    private final InputMethodSubtypeSwitchingController mSwitchingController;
    /* access modifiers changed from: private */
    public AlertDialog mSwitchingDialog;
    /* access modifiers changed from: private */
    public View mSwitchingDialogTitleView;
    private IBinder mSwitchingDialogToken = new Binder();
    boolean mSystemReady;
    private final UserManager mUserManager;
    /* access modifiers changed from: private */
    public final UserManagerInternal mUserManagerInternal;
    boolean mVisibleBound = false;
    final ServiceConnection mVisibleConnection = new ServiceConnection() {
        public void onBindingDied(ComponentName name) {
            synchronized (InputMethodManagerService.this.mMethodMap) {
                if (InputMethodManagerService.this.mVisibleBound) {
                    InputMethodManagerService.this.mContext.unbindService(InputMethodManagerService.this.mVisibleConnection);
                    InputMethodManagerService.this.mVisibleBound = false;
                }
            }
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        public void onServiceDisconnected(ComponentName name) {
        }
    };
    final WindowManagerInternal mWindowManagerInternal;

    @Retention(RetentionPolicy.SOURCE)
    private @interface HardKeyboardBehavior {
        public static final int WIRED_AFFORDANCE = 1;
        public static final int WIRELESS_AFFORDANCE = 0;
    }

    @FunctionalInterface
    interface ImeDisplayValidator {
        boolean displayCanShowIme(int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface ShellCommandResult {
        public static final int FAILURE = -1;
        public static final int SUCCESS = 0;
    }

    private static final class DebugFlag {
        private static final Object LOCK = new Object();
        private final boolean mDefaultValue;
        private final String mKey;
        @GuardedBy({"LOCK"})
        private boolean mValue;

        public DebugFlag(String key, boolean defaultValue) {
            this.mKey = key;
            this.mDefaultValue = defaultValue;
            this.mValue = SystemProperties.getBoolean(key, defaultValue);
        }

        /* access modifiers changed from: package-private */
        public void refresh() {
            synchronized (LOCK) {
                this.mValue = SystemProperties.getBoolean(this.mKey, this.mDefaultValue);
            }
        }

        /* access modifiers changed from: package-private */
        public boolean value() {
            boolean z;
            synchronized (LOCK) {
                z = this.mValue;
            }
            return z;
        }
    }

    private static final class DebugFlags {
        static final DebugFlag FLAG_OPTIMIZE_START_INPUT = new DebugFlag("debug.optimize_startinput", false);
        static final DebugFlag FLAG_PRE_RENDER_IME_VIEWS = new DebugFlag("persist.pre_render_ime_views", false);

        private DebugFlags() {
        }
    }

    static class SessionState {
        InputChannel channel;
        final ClientState client;
        final IInputMethod method;
        IInputMethodSession session;

        public String toString() {
            return "SessionState{uid " + this.client.uid + " pid " + this.client.pid + " method " + Integer.toHexString(System.identityHashCode(this.method)) + " session " + Integer.toHexString(System.identityHashCode(this.session)) + " channel " + this.channel + "}";
        }

        SessionState(ClientState _client, IInputMethod _method, IInputMethodSession _session, InputChannel _channel) {
            this.client = _client;
            this.method = _method;
            this.session = _session;
            this.channel = _channel;
        }
    }

    private static final class ClientDeathRecipient implements IBinder.DeathRecipient {
        private final IInputMethodClient mClient;
        private final InputMethodManagerService mImms;

        ClientDeathRecipient(InputMethodManagerService imms, IInputMethodClient client) {
            this.mImms = imms;
            this.mClient = client;
        }

        public void binderDied() {
            this.mImms.removeClient(this.mClient);
        }
    }

    static final class ClientState {
        final InputBinding binding = new InputBinding((InputConnection) null, this.inputContext.asBinder(), this.uid, this.pid);
        final IInputMethodClient client;
        final ClientDeathRecipient clientDeathRecipient;
        SessionState curSession;
        final IInputContext inputContext;
        final int pid;
        final int selfReportedDisplayId;
        boolean sessionRequested;
        boolean shouldPreRenderIme;
        final int uid;

        public String toString() {
            return "ClientState{" + Integer.toHexString(System.identityHashCode(this)) + " uid=" + this.uid + " pid=" + this.pid + " displayId=" + this.selfReportedDisplayId + "}";
        }

        ClientState(IInputMethodClient _client, IInputContext _inputContext, int _uid, int _pid, int _selfReportedDisplayId, ClientDeathRecipient _clientDeathRecipient) {
            this.client = _client;
            this.inputContext = _inputContext;
            this.uid = _uid;
            this.pid = _pid;
            this.selfReportedDisplayId = _selfReportedDisplayId;
            this.clientDeathRecipient = _clientDeathRecipient;
        }
    }

    private static final class ActivityViewInfo {
        /* access modifiers changed from: private */
        public final Matrix mMatrix;
        /* access modifiers changed from: private */
        public final ClientState mParentClient;

        ActivityViewInfo(ClientState parentClient, Matrix matrix) {
            this.mParentClient = parentClient;
            this.mMatrix = matrix;
        }
    }

    private static class StartInputInfo {
        private static final AtomicInteger sSequenceNumber = new AtomicInteger(0);
        final int mClientBindSequenceNumber;
        final EditorInfo mEditorInfo;
        final int mImeDisplayId;
        final String mImeId;
        final IBinder mImeToken;
        final int mImeUserId;
        final boolean mRestarting;
        final int mSequenceNumber = sSequenceNumber.getAndIncrement();
        final int mStartInputReason;
        final int mTargetDisplayId;
        final int mTargetUserId;
        final IBinder mTargetWindow;
        final int mTargetWindowSoftInputMode;
        final long mTimestamp = SystemClock.uptimeMillis();
        final long mWallTime = System.currentTimeMillis();

        StartInputInfo(int imeUserId, IBinder imeToken, int imeDisplayId, String imeId, int startInputReason, boolean restarting, int targetUserId, int targetDisplayId, IBinder targetWindow, EditorInfo editorInfo, int targetWindowSoftInputMode, int clientBindSequenceNumber) {
            this.mImeUserId = imeUserId;
            this.mImeToken = imeToken;
            this.mImeDisplayId = imeDisplayId;
            this.mImeId = imeId;
            this.mStartInputReason = startInputReason;
            this.mRestarting = restarting;
            this.mTargetUserId = targetUserId;
            this.mTargetDisplayId = targetDisplayId;
            this.mTargetWindow = targetWindow;
            this.mEditorInfo = editorInfo;
            this.mTargetWindowSoftInputMode = targetWindowSoftInputMode;
            this.mClientBindSequenceNumber = clientBindSequenceNumber;
        }
    }

    private static final class StartInputHistory {
        private static final int ENTRY_SIZE_FOR_HIGH_RAM_DEVICE = 16;
        private static final int ENTRY_SIZE_FOR_LOW_RAM_DEVICE = 5;
        private final Entry[] mEntries;
        private int mNextIndex;

        private StartInputHistory() {
            this.mEntries = new Entry[getEntrySize()];
            this.mNextIndex = 0;
        }

        private static int getEntrySize() {
            if (ActivityManager.isLowRamDeviceStatic()) {
                return 5;
            }
            return 16;
        }

        private static final class Entry {
            int mClientBindSequenceNumber;
            EditorInfo mEditorInfo;
            int mImeDisplayId;
            String mImeId;
            String mImeTokenString;
            int mImeUserId;
            boolean mRestarting;
            int mSequenceNumber;
            int mStartInputReason;
            int mTargetDisplayId;
            int mTargetUserId;
            int mTargetWindowSoftInputMode;
            String mTargetWindowString;
            long mTimestamp;
            long mWallTime;

            Entry(StartInputInfo original) {
                set(original);
            }

            /* access modifiers changed from: package-private */
            public void set(StartInputInfo original) {
                this.mSequenceNumber = original.mSequenceNumber;
                this.mTimestamp = original.mTimestamp;
                this.mWallTime = original.mWallTime;
                this.mImeUserId = original.mImeUserId;
                this.mImeTokenString = String.valueOf(original.mImeToken);
                this.mImeDisplayId = original.mImeDisplayId;
                this.mImeId = original.mImeId;
                this.mStartInputReason = original.mStartInputReason;
                this.mRestarting = original.mRestarting;
                this.mTargetUserId = original.mTargetUserId;
                this.mTargetDisplayId = original.mTargetDisplayId;
                this.mTargetWindowString = String.valueOf(original.mTargetWindow);
                this.mEditorInfo = original.mEditorInfo;
                this.mTargetWindowSoftInputMode = original.mTargetWindowSoftInputMode;
                this.mClientBindSequenceNumber = original.mClientBindSequenceNumber;
            }
        }

        /* access modifiers changed from: package-private */
        public void addEntry(StartInputInfo info) {
            int index = this.mNextIndex;
            Entry[] entryArr = this.mEntries;
            if (entryArr[index] == null) {
                entryArr[index] = new Entry(info);
            } else {
                entryArr[index].set(info);
            }
            this.mNextIndex = (this.mNextIndex + 1) % this.mEntries.length;
        }

        /* access modifiers changed from: package-private */
        public void dump(PrintWriter pw, String prefix) {
            SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
            int i = 0;
            while (true) {
                Entry[] entryArr = this.mEntries;
                if (i < entryArr.length) {
                    Entry entry = entryArr[(this.mNextIndex + i) % entryArr.length];
                    if (entry != null) {
                        pw.print(prefix);
                        pw.println("StartInput #" + entry.mSequenceNumber + ":");
                        pw.print(prefix);
                        pw.println(" time=" + dataFormat.format(new Date(entry.mWallTime)) + " (timestamp=" + entry.mTimestamp + ") reason=" + InputMethodDebug.startInputReasonToString(entry.mStartInputReason) + " restarting=" + entry.mRestarting);
                        pw.print(prefix);
                        StringBuilder sb = new StringBuilder();
                        sb.append(" imeToken=");
                        sb.append(entry.mImeTokenString);
                        sb.append(" [");
                        sb.append(entry.mImeId);
                        sb.append("]");
                        pw.print(sb.toString());
                        pw.print(" imeUserId=" + entry.mImeUserId);
                        pw.println(" imeDisplayId=" + entry.mImeDisplayId);
                        pw.print(prefix);
                        pw.println(" targetWin=" + entry.mTargetWindowString + " [" + entry.mEditorInfo.packageName + "] targetUserId=" + entry.mTargetUserId + " targetDisplayId=" + entry.mTargetDisplayId + " clientBindSeq=" + entry.mClientBindSequenceNumber);
                        pw.print(prefix);
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(" softInputMode=");
                        sb2.append(InputMethodDebug.softInputModeToString(entry.mTargetWindowSoftInputMode));
                        pw.println(sb2.toString());
                        pw.print(prefix);
                        pw.println(" inputType=0x" + Integer.toHexString(entry.mEditorInfo.inputType) + " imeOptions=0x" + Integer.toHexString(entry.mEditorInfo.imeOptions) + " fieldId=0x" + Integer.toHexString(entry.mEditorInfo.fieldId) + " fieldName=" + entry.mEditorInfo.fieldName + " actionId=" + entry.mEditorInfo.actionId + " actionLabel=" + entry.mEditorInfo.actionLabel);
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    class SettingsObserver extends ContentObserver {
        String mLastEnabled = "";
        boolean mRegistered = false;
        int mUserId;

        SettingsObserver(Handler handler) {
            super(handler);
        }

        public void registerContentObserverLocked(int userId) {
            if (!this.mRegistered || this.mUserId != userId) {
                ContentResolver resolver = InputMethodManagerService.this.mContext.getContentResolver();
                if (this.mRegistered) {
                    InputMethodManagerService.this.mContext.getContentResolver().unregisterContentObserver(this);
                    this.mRegistered = false;
                }
                if (this.mUserId != userId) {
                    this.mLastEnabled = "";
                    this.mUserId = userId;
                }
                resolver.registerContentObserver(Settings.Secure.getUriFor("default_input_method"), false, this, userId);
                resolver.registerContentObserver(Settings.Secure.getUriFor("enabled_input_methods"), false, this, userId);
                resolver.registerContentObserver(Settings.Secure.getUriFor("selected_input_method_subtype"), false, this, userId);
                resolver.registerContentObserver(Settings.Secure.getUriFor("show_ime_with_hard_keyboard"), false, this, userId);
                resolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_soft_keyboard_mode"), false, this, userId);
                this.mRegistered = true;
            }
        }

        public void onChange(boolean selfChange, Uri uri) {
            Uri showImeUri = Settings.Secure.getUriFor("show_ime_with_hard_keyboard");
            Uri accessibilityRequestingNoImeUri = Settings.Secure.getUriFor("accessibility_soft_keyboard_mode");
            synchronized (InputMethodManagerService.this.mMethodMap) {
                if (showImeUri.equals(uri)) {
                    InputMethodManagerService.this.updateKeyboardFromSettingsLocked();
                } else if (accessibilityRequestingNoImeUri.equals(uri)) {
                    boolean unused = InputMethodManagerService.this.mAccessibilityRequestingNoSoftKeyboard = (Settings.Secure.getIntForUser(InputMethodManagerService.this.mContext.getContentResolver(), "accessibility_soft_keyboard_mode", 0, this.mUserId) & 3) == 1;
                    if (InputMethodManagerService.this.mAccessibilityRequestingNoSoftKeyboard) {
                        boolean showRequested = InputMethodManagerService.this.mShowRequested;
                        InputMethodManagerService.this.hideCurrentInputLocked(0, (ResultReceiver) null);
                        InputMethodManagerService.this.mShowRequested = showRequested;
                    } else if (InputMethodManagerService.this.mShowRequested) {
                        InputMethodManagerService.this.showCurrentInputLocked(1, (ResultReceiver) null);
                    }
                } else {
                    boolean enabledChanged = false;
                    String newEnabled = InputMethodManagerService.this.mSettings.getEnabledInputMethodsStr();
                    if (!this.mLastEnabled.equals(newEnabled)) {
                        this.mLastEnabled = newEnabled;
                        enabledChanged = true;
                    }
                    InputMethodManagerService.this.updateInputMethodsFromSettingsLocked(enabledChanged);
                }
            }
        }

        public String toString() {
            return "SettingsObserver{mUserId=" + this.mUserId + " mRegistered=" + this.mRegistered + " mLastEnabled=" + this.mLastEnabled + "}";
        }
    }

    private final class ImmsBroadcastReceiverForSystemUser extends BroadcastReceiver {
        private ImmsBroadcastReceiverForSystemUser() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.USER_ADDED".equals(action) || "android.intent.action.USER_REMOVED".equals(action)) {
                InputMethodManagerService.this.updateCurrentProfileIds();
            } else if ("android.intent.action.LOCALE_CHANGED".equals(action)) {
                InputMethodManagerService.this.onActionLocaleChanged();
            } else if (InputMethodManagerService.ACTION_SHOW_INPUT_METHOD_PICKER.equals(action)) {
                InputMethodManagerService.this.mHandler.obtainMessage(1, 1, 0).sendToTarget();
            } else {
                Slog.w(InputMethodManagerService.TAG, "Unexpected intent " + intent);
            }
        }
    }

    private final class ImmsBroadcastReceiverForAllUsers extends BroadcastReceiver {
        private ImmsBroadcastReceiverForAllUsers() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction())) {
                BroadcastReceiver.PendingResult pendingResult = getPendingResult();
                if (pendingResult != null) {
                    int senderUserId = pendingResult.getSendingUserId();
                    if (senderUserId != -1) {
                        if ((InputMethodSystemProperty.PER_PROFILE_IME_ENABLED ? senderUserId : InputMethodManagerService.this.mUserManagerInternal.getProfileParentId(senderUserId)) != InputMethodManagerService.this.mSettings.getCurrentUserId()) {
                            return;
                        }
                    }
                    InputMethodManagerService.this.hideInputMethodMenu();
                    return;
                }
                return;
            }
            Slog.w(InputMethodManagerService.TAG, "Unexpected intent " + intent);
        }
    }

    /* access modifiers changed from: package-private */
    public void onActionLocaleChanged() {
        synchronized (this.mMethodMap) {
            LocaleList possibleNewLocale = this.mRes.getConfiguration().getLocales();
            if (possibleNewLocale == null || !possibleNewLocale.equals(this.mLastSystemLocales)) {
                buildInputMethodListLocked(true);
                resetDefaultImeLocked(this.mContext);
                updateFromSettingsLocked(true);
                this.mLastSystemLocales = possibleNewLocale;
            }
        }
    }

    final class MyPackageMonitor extends PackageMonitor {
        private final ArrayList<String> mChangedPackages = new ArrayList<>();
        private boolean mImePackageAppeared = false;
        @GuardedBy({"mMethodMap"})
        private final ArraySet<String> mKnownImePackageNames = new ArraySet<>();

        MyPackageMonitor() {
        }

        /* access modifiers changed from: package-private */
        @GuardedBy({"mMethodMap"})
        public void clearKnownImePackageNamesLocked() {
            this.mKnownImePackageNames.clear();
        }

        /* access modifiers changed from: package-private */
        @GuardedBy({"mMethodMap"})
        public final void addKnownImePackageNameLocked(String packageName) {
            this.mKnownImePackageNames.add(packageName);
        }

        @GuardedBy({"mMethodMap"})
        private boolean isChangingPackagesOfCurrentUserLocked() {
            return getChangingUserId() == InputMethodManagerService.this.mSettings.getCurrentUserId();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:31:0x0069, code lost:
            return false;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onHandleForceStop(android.content.Intent r11, java.lang.String[] r12, int r13, boolean r14) {
            /*
                r10 = this;
                com.android.server.inputmethod.InputMethodManagerService r0 = com.android.server.inputmethod.InputMethodManagerService.this
                android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r0 = r0.mMethodMap
                monitor-enter(r0)
                boolean r1 = r10.isChangingPackagesOfCurrentUserLocked()     // Catch:{ all -> 0x006a }
                r2 = 0
                if (r1 != 0) goto L_0x000e
                monitor-exit(r0)     // Catch:{ all -> 0x006a }
                return r2
            L_0x000e:
                com.android.server.inputmethod.InputMethodManagerService r1 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x006a }
                com.android.server.inputmethod.InputMethodUtils$InputMethodSettings r1 = r1.mSettings     // Catch:{ all -> 0x006a }
                java.lang.String r1 = r1.getSelectedInputMethod()     // Catch:{ all -> 0x006a }
                com.android.server.inputmethod.InputMethodManagerService r3 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x006a }
                java.util.ArrayList<android.view.inputmethod.InputMethodInfo> r3 = r3.mMethodList     // Catch:{ all -> 0x006a }
                int r3 = r3.size()     // Catch:{ all -> 0x006a }
                if (r1 == 0) goto L_0x0068
                r4 = 0
            L_0x0021:
                if (r4 >= r3) goto L_0x0068
                com.android.server.inputmethod.InputMethodManagerService r5 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x006a }
                java.util.ArrayList<android.view.inputmethod.InputMethodInfo> r5 = r5.mMethodList     // Catch:{ all -> 0x006a }
                java.lang.Object r5 = r5.get(r4)     // Catch:{ all -> 0x006a }
                android.view.inputmethod.InputMethodInfo r5 = (android.view.inputmethod.InputMethodInfo) r5     // Catch:{ all -> 0x006a }
                java.lang.String r6 = r5.getId()     // Catch:{ all -> 0x006a }
                boolean r6 = r6.equals(r1)     // Catch:{ all -> 0x006a }
                if (r6 == 0) goto L_0x0065
                int r6 = r12.length     // Catch:{ all -> 0x006a }
                r7 = r2
            L_0x0039:
                if (r7 >= r6) goto L_0x0065
                r8 = r12[r7]     // Catch:{ all -> 0x006a }
                java.lang.String r9 = r5.getPackageName()     // Catch:{ all -> 0x006a }
                boolean r9 = r9.equals(r8)     // Catch:{ all -> 0x006a }
                if (r9 == 0) goto L_0x0062
                r2 = 1
                if (r14 != 0) goto L_0x004c
                monitor-exit(r0)     // Catch:{ all -> 0x006a }
                return r2
            L_0x004c:
                boolean r6 = com.android.server.inputmethod.InputMethodManagerServiceInjector.shouldResetIME(r11, r12, r13, r14)     // Catch:{ all -> 0x006a }
                if (r6 != 0) goto L_0x0054
                monitor-exit(r0)     // Catch:{ all -> 0x006a }
                return r2
            L_0x0054:
                com.android.server.inputmethod.InputMethodManagerService r6 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x006a }
                java.lang.String r7 = ""
                r6.resetSelectedInputMethodAndSubtypeLocked(r7)     // Catch:{ all -> 0x006a }
                com.android.server.inputmethod.InputMethodManagerService r6 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x006a }
                boolean unused = r6.chooseNewDefaultIMELocked()     // Catch:{ all -> 0x006a }
                monitor-exit(r0)     // Catch:{ all -> 0x006a }
                return r2
            L_0x0062:
                int r7 = r7 + 1
                goto L_0x0039
            L_0x0065:
                int r4 = r4 + 1
                goto L_0x0021
            L_0x0068:
                monitor-exit(r0)     // Catch:{ all -> 0x006a }
                return r2
            L_0x006a:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x006a }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.MyPackageMonitor.onHandleForceStop(android.content.Intent, java.lang.String[], int, boolean):boolean");
        }

        public void onBeginPackageChanges() {
            clearPackageChangeState();
        }

        public void onPackageAppeared(String packageName, int reason) {
            if (!this.mImePackageAppeared && !InputMethodManagerService.this.mContext.getPackageManager().queryIntentServicesAsUser(new Intent("android.view.InputMethod").setPackage(packageName), 512, getChangingUserId()).isEmpty()) {
                this.mImePackageAppeared = true;
            }
            this.mChangedPackages.add(packageName);
        }

        public void onPackageDisappeared(String packageName, int reason) {
            this.mChangedPackages.add(packageName);
        }

        public void onPackageModified(String packageName) {
            this.mChangedPackages.add(packageName);
        }

        public void onPackagesSuspended(String[] packages) {
            for (String packageName : packages) {
                this.mChangedPackages.add(packageName);
            }
        }

        public void onPackagesUnsuspended(String[] packages) {
            for (String packageName : packages) {
                this.mChangedPackages.add(packageName);
            }
        }

        public void onFinishPackageChanges() {
            onFinishPackageChangesInternal();
            clearPackageChangeState();
        }

        private void clearPackageChangeState() {
            this.mChangedPackages.clear();
            this.mImePackageAppeared = false;
        }

        @GuardedBy({"mMethodMap"})
        private boolean shouldRebuildInputMethodListLocked() {
            if (this.mImePackageAppeared) {
                return true;
            }
            int N = this.mChangedPackages.size();
            for (int i = 0; i < N; i++) {
                if (this.mKnownImePackageNames.contains(this.mChangedPackages.get(i))) {
                    return true;
                }
            }
            return false;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:50:0x0127, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void onFinishPackageChangesInternal() {
            /*
                r14 = this;
                com.android.server.inputmethod.InputMethodManagerService r0 = com.android.server.inputmethod.InputMethodManagerService.this
                android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r0 = r0.mMethodMap
                monitor-enter(r0)
                boolean r1 = r14.isChangingPackagesOfCurrentUserLocked()     // Catch:{ all -> 0x0128 }
                if (r1 != 0) goto L_0x000d
                monitor-exit(r0)     // Catch:{ all -> 0x0128 }
                return
            L_0x000d:
                boolean r1 = r14.shouldRebuildInputMethodListLocked()     // Catch:{ all -> 0x0128 }
                if (r1 != 0) goto L_0x0015
                monitor-exit(r0)     // Catch:{ all -> 0x0128 }
                return
            L_0x0015:
                r1 = 0
                com.android.server.inputmethod.InputMethodManagerService r2 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0128 }
                com.android.server.inputmethod.InputMethodUtils$InputMethodSettings r2 = r2.mSettings     // Catch:{ all -> 0x0128 }
                java.lang.String r2 = r2.getSelectedInputMethod()     // Catch:{ all -> 0x0128 }
                com.android.server.inputmethod.InputMethodManagerService r3 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0128 }
                java.util.ArrayList<android.view.inputmethod.InputMethodInfo> r3 = r3.mMethodList     // Catch:{ all -> 0x0128 }
                int r3 = r3.size()     // Catch:{ all -> 0x0128 }
                r4 = 3
                r5 = 2
                r6 = 0
                if (r2 == 0) goto L_0x00a1
                r7 = 0
            L_0x002c:
                if (r7 >= r3) goto L_0x00a1
                com.android.server.inputmethod.InputMethodManagerService r8 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0128 }
                java.util.ArrayList<android.view.inputmethod.InputMethodInfo> r8 = r8.mMethodList     // Catch:{ all -> 0x0128 }
                java.lang.Object r8 = r8.get(r7)     // Catch:{ all -> 0x0128 }
                android.view.inputmethod.InputMethodInfo r8 = (android.view.inputmethod.InputMethodInfo) r8     // Catch:{ all -> 0x0128 }
                java.lang.String r9 = r8.getId()     // Catch:{ all -> 0x0128 }
                boolean r10 = r9.equals(r2)     // Catch:{ all -> 0x0128 }
                if (r10 == 0) goto L_0x0043
                r1 = r8
            L_0x0043:
                java.lang.String r10 = r8.getPackageName()     // Catch:{ all -> 0x0128 }
                int r10 = r14.isPackageDisappearing(r10)     // Catch:{ all -> 0x0128 }
                java.lang.String r11 = r8.getPackageName()     // Catch:{ all -> 0x0128 }
                boolean r11 = r14.isPackageModified(r11)     // Catch:{ all -> 0x0128 }
                if (r11 == 0) goto L_0x0077
                com.android.server.inputmethod.InputMethodManagerService r11 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0128 }
                android.util.ArrayMap r11 = r11.mAdditionalSubtypeMap     // Catch:{ all -> 0x0128 }
                java.lang.String r12 = r8.getId()     // Catch:{ all -> 0x0128 }
                r11.remove(r12)     // Catch:{ all -> 0x0128 }
                com.android.server.inputmethod.InputMethodManagerService r11 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0128 }
                android.util.ArrayMap r11 = r11.mAdditionalSubtypeMap     // Catch:{ all -> 0x0128 }
                com.android.server.inputmethod.InputMethodManagerService r12 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0128 }
                android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r12 = r12.mMethodMap     // Catch:{ all -> 0x0128 }
                com.android.server.inputmethod.InputMethodManagerService r13 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0128 }
                com.android.server.inputmethod.InputMethodUtils$InputMethodSettings r13 = r13.mSettings     // Catch:{ all -> 0x0128 }
                int r13 = r13.getCurrentUserId()     // Catch:{ all -> 0x0128 }
                com.android.server.inputmethod.AdditionalSubtypeUtils.save(r11, r12, r13)     // Catch:{ all -> 0x0128 }
            L_0x0077:
                if (r10 == r5) goto L_0x007b
                if (r10 != r4) goto L_0x009e
            L_0x007b:
                java.lang.String r11 = "InputMethodManagerService"
                java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x0128 }
                r12.<init>()     // Catch:{ all -> 0x0128 }
                java.lang.String r13 = "Input method uninstalled, disabling: "
                r12.append(r13)     // Catch:{ all -> 0x0128 }
                android.content.ComponentName r13 = r8.getComponent()     // Catch:{ all -> 0x0128 }
                r12.append(r13)     // Catch:{ all -> 0x0128 }
                java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x0128 }
                android.util.Slog.i(r11, r12)     // Catch:{ all -> 0x0128 }
                com.android.server.inputmethod.InputMethodManagerService r11 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0128 }
                java.lang.String r12 = r8.getId()     // Catch:{ all -> 0x0128 }
                boolean unused = r11.setInputMethodEnabledLocked(r12, r6)     // Catch:{ all -> 0x0128 }
            L_0x009e:
                int r7 = r7 + 1
                goto L_0x002c
            L_0x00a1:
                com.android.server.inputmethod.InputMethodManagerService r7 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0128 }
                r7.buildInputMethodListLocked(r6)     // Catch:{ all -> 0x0128 }
                r7 = 0
                if (r1 == 0) goto L_0x0108
                java.lang.String r8 = r1.getPackageName()     // Catch:{ all -> 0x0128 }
                int r8 = r14.isPackageDisappearing(r8)     // Catch:{ all -> 0x0128 }
                if (r8 == r5) goto L_0x00b5
                if (r8 != r4) goto L_0x0108
            L_0x00b5:
                r4 = 0
                com.android.server.inputmethod.InputMethodManagerService r5 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ RemoteException -> 0x00ce }
                android.content.pm.IPackageManager r5 = r5.mIPackageManager     // Catch:{ RemoteException -> 0x00ce }
                android.content.ComponentName r9 = r1.getComponent()     // Catch:{ RemoteException -> 0x00ce }
                com.android.server.inputmethod.InputMethodManagerService r10 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ RemoteException -> 0x00ce }
                com.android.server.inputmethod.InputMethodUtils$InputMethodSettings r10 = r10.mSettings     // Catch:{ RemoteException -> 0x00ce }
                int r10 = r10.getCurrentUserId()     // Catch:{ RemoteException -> 0x00ce }
                android.content.pm.ServiceInfo r5 = r5.getServiceInfo(r9, r6, r10)     // Catch:{ RemoteException -> 0x00ce }
                r4 = r5
                goto L_0x00cf
            L_0x00ce:
                r5 = move-exception
            L_0x00cf:
                if (r4 != 0) goto L_0x0108
                java.lang.String r5 = "InputMethodManagerService"
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x0128 }
                r9.<init>()     // Catch:{ all -> 0x0128 }
                java.lang.String r10 = "Current input method removed: "
                r9.append(r10)     // Catch:{ all -> 0x0128 }
                r9.append(r2)     // Catch:{ all -> 0x0128 }
                java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x0128 }
                android.util.Slog.i(r5, r9)     // Catch:{ all -> 0x0128 }
                com.android.server.inputmethod.InputMethodManagerService r5 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0128 }
                com.android.server.inputmethod.InputMethodManagerService r9 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0128 }
                int r9 = r9.mBackDisposition     // Catch:{ all -> 0x0128 }
                r5.updateSystemUiLocked(r6, r9)     // Catch:{ all -> 0x0128 }
                com.android.server.inputmethod.InputMethodManagerService r5 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0128 }
                boolean r5 = r5.chooseNewDefaultIMELocked()     // Catch:{ all -> 0x0128 }
                if (r5 != 0) goto L_0x0108
                r7 = 1
                r1 = 0
                java.lang.String r5 = "InputMethodManagerService"
                java.lang.String r9 = "Unsetting current input method"
                android.util.Slog.i(r5, r9)     // Catch:{ all -> 0x0128 }
                com.android.server.inputmethod.InputMethodManagerService r5 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0128 }
                java.lang.String r9 = ""
                r5.resetSelectedInputMethodAndSubtypeLocked(r9)     // Catch:{ all -> 0x0128 }
            L_0x0108:
                if (r1 != 0) goto L_0x0112
                com.android.server.inputmethod.InputMethodManagerService r4 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0128 }
                boolean r4 = r4.chooseNewDefaultIMELocked()     // Catch:{ all -> 0x0128 }
                r7 = r4
                goto L_0x011f
            L_0x0112:
                if (r7 != 0) goto L_0x011f
                java.lang.String r4 = r1.getPackageName()     // Catch:{ all -> 0x0128 }
                boolean r4 = r14.isPackageModified(r4)     // Catch:{ all -> 0x0128 }
                if (r4 == 0) goto L_0x011f
                r7 = 1
            L_0x011f:
                if (r7 == 0) goto L_0x0126
                com.android.server.inputmethod.InputMethodManagerService r4 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0128 }
                r4.updateFromSettingsLocked(r6)     // Catch:{ all -> 0x0128 }
            L_0x0126:
                monitor-exit(r0)     // Catch:{ all -> 0x0128 }
                return
            L_0x0128:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0128 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.MyPackageMonitor.onFinishPackageChangesInternal():void");
        }
    }

    private static final class MethodCallback extends IInputSessionCallback.Stub {
        private final InputChannel mChannel;
        private final IInputMethod mMethod;
        private final InputMethodManagerService mParentIMMS;

        MethodCallback(InputMethodManagerService imms, IInputMethod method, InputChannel channel) {
            this.mParentIMMS = imms;
            this.mMethod = method;
            this.mChannel = channel;
        }

        public void sessionCreated(IInputMethodSession session) {
            long ident = Binder.clearCallingIdentity();
            try {
                this.mParentIMMS.onSessionCreated(this.mMethod, session, this.mChannel);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    private class HardKeyboardListener implements WindowManagerInternal.OnHardKeyboardStatusChangeListener {
        private HardKeyboardListener() {
        }

        public void onHardKeyboardStatusChange(boolean available) {
            InputMethodManagerService.this.mHandler.sendMessage(InputMethodManagerService.this.mHandler.obtainMessage(InputMethodManagerService.MSG_HARD_KEYBOARD_SWITCH_CHANGED, Integer.valueOf(available)));
        }

        public void handleHardKeyboardStatusChange(boolean available) {
            synchronized (InputMethodManagerService.this.mMethodMap) {
                if (!(InputMethodManagerService.this.mSwitchingDialog == null || InputMethodManagerService.this.mSwitchingDialogTitleView == null || !InputMethodManagerService.this.mSwitchingDialog.isShowing())) {
                    InputMethodManagerService.this.mSwitchingDialogTitleView.findViewById(16908983).setVisibility(available ? 0 : 8);
                }
            }
        }
    }

    public static final class Lifecycle extends SystemService {
        private InputMethodManagerService mService;

        public Lifecycle(Context context) {
            super(context);
            this.mService = new InputMethodManagerService(context);
        }

        /* JADX WARNING: type inference failed for: r0v1, types: [com.android.server.inputmethod.InputMethodManagerService, android.os.IBinder] */
        public void onStart() {
            LocalServices.addService(InputMethodManagerInternal.class, new LocalServiceImpl(this.mService));
            publishBinderService("input_method", this.mService);
        }

        public void onSwitchUser(int userHandle) {
            this.mService.onSwitchUser(userHandle);
        }

        public void onBootPhase(int phase) {
            if (phase == 550) {
                this.mService.systemRunning((StatusBarManagerService) ServiceManager.getService("statusbar"));
            }
        }

        public void onUnlockUser(int userHandle) {
            this.mService.mHandler.sendMessage(this.mService.mHandler.obtainMessage(InputMethodManagerService.MSG_SYSTEM_UNLOCK_USER, userHandle, 0));
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0026, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onUnlockUser(int r7) {
        /*
            r6 = this;
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r0 = r6.mMethodMap
            monitor-enter(r0)
            com.android.server.inputmethod.InputMethodUtils$InputMethodSettings r1 = r6.mSettings     // Catch:{ all -> 0x0027 }
            int r1 = r1.getCurrentUserId()     // Catch:{ all -> 0x0027 }
            if (r7 == r1) goto L_0x000d
            monitor-exit(r0)     // Catch:{ all -> 0x0027 }
            return
        L_0x000d:
            com.android.server.inputmethod.InputMethodUtils$InputMethodSettings r2 = r6.mSettings     // Catch:{ all -> 0x0027 }
            boolean r3 = r6.mSystemReady     // Catch:{ all -> 0x0027 }
            r4 = 1
            r5 = 0
            if (r3 != 0) goto L_0x0017
            r3 = r4
            goto L_0x0018
        L_0x0017:
            r3 = r5
        L_0x0018:
            r2.switchCurrentUser(r1, r3)     // Catch:{ all -> 0x0027 }
            boolean r2 = r6.mSystemReady     // Catch:{ all -> 0x0027 }
            if (r2 == 0) goto L_0x0025
            r6.buildInputMethodListLocked(r5)     // Catch:{ all -> 0x0027 }
            r6.updateInputMethodsFromSettingsLocked(r4)     // Catch:{ all -> 0x0027 }
        L_0x0025:
            monitor-exit(r0)     // Catch:{ all -> 0x0027 }
            return
        L_0x0027:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0027 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.onUnlockUser(int):void");
    }

    /* access modifiers changed from: package-private */
    public void onSwitchUser(int userId) {
        synchronized (this.mMethodMap) {
            switchUserLocked(userId);
        }
    }

    public InputMethodManagerService(Context context) {
        this.mContext = context;
        this.mRes = context.getResources();
        this.mHandler = new Handler(this);
        this.mSettingsObserver = new SettingsObserver(this.mHandler);
        this.mIWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        this.mWindowManagerInternal = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);
        this.mDisplayManagerInternal = (DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class);
        this.mImeDisplayValidator = new ImeDisplayValidator() {
            public final boolean displayCanShowIme(int i) {
                return InputMethodManagerService.this.lambda$new$0$InputMethodManagerService(i);
            }
        };
        this.mCaller = new HandlerCaller(context, (Looper) null, new HandlerCaller.Callback() {
            public void executeMessage(Message msg) {
                InputMethodManagerService.this.handleMessage(msg);
            }
        }, true);
        this.mAppOpsManager = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        this.mUserManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        this.mUserManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        this.mHardKeyboardListener = new HardKeyboardListener();
        this.mHasFeature = context.getPackageManager().hasSystemFeature("android.software.input_methods");
        this.mSlotIme = this.mContext.getString(17041172);
        this.mHardKeyboardBehavior = this.mContext.getResources().getInteger(17694807);
        this.mIsLowRam = ActivityManager.isLowRamDeviceStatic();
        Bundle extras = new Bundle();
        extras.putBoolean("android.allowDuringSetup", true);
        this.mImeSwitcherNotification = new Notification.Builder(this.mContext, SystemNotificationChannels.VIRTUAL_KEYBOARD_MIUI).setSmallIcon(17302752).setWhen(0).setOngoing(true).addExtras(extras).setCategory("sys").setColor(this.mContext.getColor(17170460));
        this.mImeSwitchPendingIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_SHOW_INPUT_METHOD_PICKER).setPackage(this.mContext.getPackageName()), 0);
        this.mShowOngoingImeSwitcherForPhones = false;
        this.mNotificationShown = false;
        int userId = 0;
        try {
            userId = ActivityManager.getService().getCurrentUser().id;
        } catch (RemoteException e) {
            Slog.w(TAG, "Couldn't get current user ID; guessing it's 0", e);
        }
        this.mLastSwitchUserId = userId;
        this.mSettings = new InputMethodUtils.InputMethodSettings(this.mRes, context.getContentResolver(), this.mMethodMap, userId, !this.mSystemReady);
        updateCurrentProfileIds();
        AdditionalSubtypeUtils.load(this.mAdditionalSubtypeMap, userId);
        this.mSwitchingController = InputMethodSubtypeSwitchingController.createInstanceLocked(this.mSettings, context);
        InputMethodManagerServiceInjector.enableSystemIMEsIfThereIsNoEnabledIME(this.mMethodList, this.mSettings);
        this.mMiuiSecurityInputMethodHelper = new MiuiSecurityInputMethodHelper(this);
    }

    public /* synthetic */ boolean lambda$new$0$InputMethodManagerService(int displayId) {
        return this.mWindowManagerInternal.shouldShowIme(displayId);
    }

    private void resetDefaultImeLocked(Context context) {
        String str = this.mCurMethodId;
        if (str == null || this.mMethodMap.get(str).isSystem()) {
            List<InputMethodInfo> suitableImes = InputMethodUtils.getDefaultEnabledImes(context, this.mSettings.getEnabledInputMethodListLocked());
            if (suitableImes.isEmpty()) {
                Slog.i(TAG, "No default found");
            } else {
                setSelectedInputMethodAndSubtypeLocked(suitableImes.get(0), -1, false);
            }
        }
    }

    @GuardedBy({"mMethodMap"})
    private void switchUserLocked(int newUserId) {
        this.mSettingsObserver.registerContentObserverLocked(newUserId);
        this.mSettings.switchCurrentUser(newUserId, !this.mSystemReady || !this.mUserManagerInternal.isUserUnlockingOrUnlocked(newUserId));
        updateCurrentProfileIds();
        AdditionalSubtypeUtils.load(this.mAdditionalSubtypeMap, newUserId);
        boolean initialUserSwitch = TextUtils.isEmpty(this.mSettings.getSelectedInputMethod());
        this.mLastSystemLocales = this.mRes.getConfiguration().getLocales();
        this.mMiuiSecurityInputMethodHelper.onSwitchUserLocked(newUserId);
        if (this.mSystemReady) {
            hideCurrentInputLocked(0, (ResultReceiver) null);
            resetCurrentMethodAndClient(6);
            buildInputMethodListLocked(initialUserSwitch);
            if (TextUtils.isEmpty(this.mSettings.getSelectedInputMethod())) {
                resetDefaultImeLocked(this.mContext);
            }
            updateFromSettingsLocked(true);
        }
        if (initialUserSwitch) {
            InputMethodUtils.setNonSelectedSystemImesDisabledUntilUsed(this.mIPackageManager, this.mSettings.getEnabledInputMethodListLocked(), newUserId, this.mContext.getBasePackageName());
        }
        this.mLastSwitchUserId = newUserId;
    }

    /* access modifiers changed from: package-private */
    public void updateCurrentProfileIds() {
        InputMethodUtils.InputMethodSettings inputMethodSettings = this.mSettings;
        inputMethodSettings.setCurrentProfileIds(this.mUserManager.getProfileIdsWithDisabled(inputMethodSettings.getCurrentUserId()));
    }

    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        try {
            return InputMethodManagerService.super.onTransact(code, data, reply, flags);
        } catch (RuntimeException e) {
            if (!(e instanceof SecurityException)) {
                Slog.wtf(TAG, "Input Method Manager Crash", e);
            }
            throw e;
        }
    }

    public void systemRunning(StatusBarManagerService statusBar) {
        synchronized (this.mMethodMap) {
            if (!this.mSystemReady) {
                this.mSystemReady = true;
                this.mLastSystemLocales = this.mRes.getConfiguration().getLocales();
                int currentUserId = this.mSettings.getCurrentUserId();
                boolean z = false;
                this.mSettings.switchCurrentUser(currentUserId, !this.mUserManagerInternal.isUserUnlockingOrUnlocked(currentUserId));
                this.mKeyguardManager = (KeyguardManager) this.mContext.getSystemService(KeyguardManager.class);
                this.mNotificationManager = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
                this.mStatusBar = statusBar;
                if (this.mStatusBar != null) {
                    this.mStatusBar.setIconVisibility(this.mSlotIme, false);
                }
                updateSystemUiLocked(this.mImeWindowVis, this.mBackDisposition);
                this.mShowOngoingImeSwitcherForPhones = this.mRes.getBoolean(17891625);
                if (this.mShowOngoingImeSwitcherForPhones) {
                    this.mWindowManagerInternal.setOnHardKeyboardStatusChangeListener(this.mHardKeyboardListener);
                }
                this.mMyPackageMonitor.register(this.mContext, (Looper) null, UserHandle.ALL, true);
                this.mSettingsObserver.registerContentObserverLocked(currentUserId);
                IntentFilter broadcastFilterForSystemUser = new IntentFilter();
                broadcastFilterForSystemUser.addAction("android.intent.action.USER_ADDED");
                broadcastFilterForSystemUser.addAction("android.intent.action.USER_REMOVED");
                broadcastFilterForSystemUser.addAction("android.intent.action.LOCALE_CHANGED");
                broadcastFilterForSystemUser.addAction(ACTION_SHOW_INPUT_METHOD_PICKER);
                this.mContext.registerReceiver(new ImmsBroadcastReceiverForSystemUser(), broadcastFilterForSystemUser);
                IntentFilter broadcastFilterForAllUsers = new IntentFilter();
                broadcastFilterForAllUsers.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
                this.mContext.registerReceiverAsUser(new ImmsBroadcastReceiverForAllUsers(), UserHandle.ALL, broadcastFilterForAllUsers, (String) null, (Handler) null);
                if (!(!TextUtils.isEmpty(this.mSettings.getSelectedInputMethod()))) {
                    z = true;
                }
                buildInputMethodListLocked(z);
                updateFromSettingsLocked(true);
                InputMethodUtils.setNonSelectedSystemImesDisabledUntilUsed(this.mIPackageManager, this.mSettings.getEnabledInputMethodListLocked(), currentUserId, this.mContext.getBasePackageName());
                this.mMiuiSecurityInputMethodHelper.onSystemRunningLocked();
            }
        }
    }

    @GuardedBy({"mMethodMap"})
    private boolean calledFromValidUserLocked() {
        int uid = Binder.getCallingUid();
        int userId = UserHandle.getUserId(uid);
        if (uid == 1000 || userId == this.mSettings.getCurrentUserId() || userId == 999) {
            return true;
        }
        if ((!InputMethodSystemProperty.PER_PROFILE_IME_ENABLED && this.mSettings.isCurrentProfile(userId)) || this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") == 0) {
            return true;
        }
        Slog.w(TAG, "--- IPC called from background users. Ignore. callers=" + Debug.getCallers(10));
        return false;
    }

    @GuardedBy({"mMethodMap"})
    private boolean calledWithValidTokenLocked(IBinder token) {
        if (token == null) {
            throw new InvalidParameterException("token must not be null.");
        } else if (token == this.mCurToken) {
            return true;
        } else {
            Slog.e(TAG, "Ignoring " + Debug.getCaller() + " due to an invalid token. uid:" + Binder.getCallingUid() + " token:" + token);
            return false;
        }
    }

    @GuardedBy({"mMethodMap"})
    private boolean bindCurrentInputMethodServiceLocked(Intent service, ServiceConnection conn, int flags) {
        if (service != null && conn != null) {
            return this.mContext.bindServiceAsUser(service, conn, flags, new UserHandle(this.mSettings.getCurrentUserId()));
        }
        Slog.e(TAG, "--- bind failed: service = " + service + ", conn = " + conn);
        return false;
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public List<InputMethodInfo> getInputMethodList(int userId) {
        if (UserHandle.getCallingUserId() != userId) {
            this.mContext.enforceCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL", (String) null);
        }
        synchronized (this.mMethodMap) {
            int[] resolvedUserIds = InputMethodUtils.resolveUserId(userId, this.mSettings.getCurrentUserId(), (PrintWriter) null);
            if (resolvedUserIds.length != 1) {
                List<InputMethodInfo> emptyList = Collections.emptyList();
                return emptyList;
            }
            long ident = Binder.clearCallingIdentity();
            try {
                List<InputMethodInfo> inputMethodListLocked = getInputMethodListLocked(resolvedUserIds[0]);
                return inputMethodListLocked;
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public List<InputMethodInfo> getEnabledInputMethodList(int userId) {
        if (UserHandle.getCallingUserId() != userId) {
            this.mContext.enforceCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL", (String) null);
        }
        synchronized (this.mMethodMap) {
            int[] resolvedUserIds = InputMethodUtils.resolveUserId(userId, this.mSettings.getCurrentUserId(), (PrintWriter) null);
            if (resolvedUserIds.length != 1) {
                List<InputMethodInfo> emptyList = Collections.emptyList();
                return emptyList;
            }
            long ident = Binder.clearCallingIdentity();
            try {
                List<InputMethodInfo> enabledInputMethodListLocked = getEnabledInputMethodListLocked(resolvedUserIds[0]);
                return enabledInputMethodListLocked;
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    @GuardedBy({"mMethodMap"})
    private List<InputMethodInfo> getInputMethodListLocked(int userId) {
        ArrayList<InputMethodInfo> methodList;
        if (userId == this.mSettings.getCurrentUserId()) {
            methodList = new ArrayList<>(this.mMethodList);
        } else {
            ArrayMap<String, InputMethodInfo> methodMap = new ArrayMap<>();
            ArrayList<InputMethodInfo> methodList2 = new ArrayList<>();
            ArrayMap<String, List<InputMethodSubtype>> additionalSubtypeMap = new ArrayMap<>();
            AdditionalSubtypeUtils.load(additionalSubtypeMap, userId);
            queryInputMethodServicesInternal(this.mContext, userId, additionalSubtypeMap, methodMap, methodList2);
            methodList = methodList2;
        }
        return this.mMiuiSecurityInputMethodHelper.filterSecMethodLocked(new ArrayList(methodList));
    }

    @GuardedBy({"mMethodMap"})
    private List<InputMethodInfo> getEnabledInputMethodListLocked(int userId) {
        if (userId == this.mSettings.getCurrentUserId()) {
            return this.mMiuiSecurityInputMethodHelper.filterSecMethodLocked(this.mSettings.getEnabledInputMethodListLocked());
        }
        ArrayMap<String, InputMethodInfo> methodMap = new ArrayMap<>();
        ArrayList arrayList = new ArrayList();
        ArrayMap arrayMap = new ArrayMap();
        AdditionalSubtypeUtils.load(arrayMap, userId);
        queryInputMethodServicesInternal(this.mContext, userId, arrayMap, methodMap, arrayList);
        return new InputMethodUtils.InputMethodSettings(this.mContext.getResources(), this.mContext.getContentResolver(), methodMap, userId, true).getEnabledInputMethodListLocked();
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public List<InputMethodSubtype> getEnabledInputMethodSubtypeList(String imiId, boolean allowsImplicitlySelectedSubtypes) {
        int callingUserId = UserHandle.getCallingUserId();
        synchronized (this.mMethodMap) {
            int[] resolvedUserIds = InputMethodUtils.resolveUserId(callingUserId, this.mSettings.getCurrentUserId(), (PrintWriter) null);
            if (resolvedUserIds.length != 1) {
                List<InputMethodSubtype> emptyList = Collections.emptyList();
                return emptyList;
            }
            long ident = Binder.clearCallingIdentity();
            try {
                List<InputMethodSubtype> enabledInputMethodSubtypeListLocked = getEnabledInputMethodSubtypeListLocked(imiId, allowsImplicitlySelectedSubtypes, resolvedUserIds[0]);
                return enabledInputMethodSubtypeListLocked;
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    @GuardedBy({"mMethodMap"})
    private List<InputMethodSubtype> getEnabledInputMethodSubtypeListLocked(String imiId, boolean allowsImplicitlySelectedSubtypes, int userId) {
        InputMethodInfo imi;
        String str;
        if (userId == this.mSettings.getCurrentUserId()) {
            if (imiId != null || (str = this.mCurMethodId) == null) {
                imi = this.mMethodMap.get(imiId);
            } else {
                imi = this.mMethodMap.get(str);
            }
            if (imi == null) {
                return Collections.emptyList();
            }
            return this.mSettings.getEnabledInputMethodSubtypeListLocked(this.mContext, imi, allowsImplicitlySelectedSubtypes);
        }
        ArrayMap<String, InputMethodInfo> methodMap = new ArrayMap<>();
        ArrayList arrayList = new ArrayList();
        ArrayMap arrayMap = new ArrayMap();
        AdditionalSubtypeUtils.load(arrayMap, userId);
        queryInputMethodServicesInternal(this.mContext, userId, arrayMap, methodMap, arrayList);
        InputMethodInfo imi2 = methodMap.get(imiId);
        if (imi2 == null) {
            return Collections.emptyList();
        }
        return new InputMethodUtils.InputMethodSettings(this.mContext.getResources(), this.mContext.getContentResolver(), methodMap, userId, true).getEnabledInputMethodSubtypeListLocked(this.mContext, imi2, allowsImplicitlySelectedSubtypes);
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    public void addClient(IInputMethodClient client, IInputContext inputContext, int selfReportedDisplayId) {
        int i = selfReportedDisplayId;
        int callerUid = Binder.getCallingUid();
        int callerPid = Binder.getCallingPid();
        synchronized (this.mMethodMap) {
            try {
                int numClients = this.mClients.size();
                for (int i2 = 0; i2 < numClients; i2++) {
                    ClientState state = this.mClients.valueAt(i2);
                    if (state.uid == callerUid && state.pid == callerPid) {
                        if (state.selfReportedDisplayId == i) {
                            throw new SecurityException("uid=" + callerUid + "/pid=" + callerPid + "/displayId=" + i + " is already registered.");
                        }
                    }
                }
                try {
                    ClientDeathRecipient deathRecipient = new ClientDeathRecipient(this, client);
                    client.asBinder().linkToDeath(deathRecipient, 0);
                    ArrayMap<IBinder, ClientState> arrayMap = this.mClients;
                    ClientState clientState = r2;
                    IBinder asBinder = client.asBinder();
                    ClientState clientState2 = new ClientState(client, inputContext, callerUid, callerPid, selfReportedDisplayId, deathRecipient);
                    arrayMap.put(asBinder, clientState);
                } catch (RemoteException e) {
                    throw new IllegalStateException(e);
                } catch (Throwable th) {
                    e = th;
                    throw e;
                }
            } catch (Throwable th2) {
                e = th2;
                IInputMethodClient iInputMethodClient = client;
                throw e;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void removeClient(IInputMethodClient client) {
        synchronized (this.mMethodMap) {
            ClientState cs = this.mClients.remove(client.asBinder());
            if (cs != null) {
                client.asBinder().unlinkToDeath(cs.clientDeathRecipient, 0);
                clearClientSessionLocked(cs);
                for (int i = this.mActivityViewDisplayIdToParentMap.size() - 1; i >= 0; i--) {
                    if (this.mActivityViewDisplayIdToParentMap.valueAt(i).mParentClient == cs) {
                        this.mActivityViewDisplayIdToParentMap.removeAt(i);
                    }
                }
                if (this.mCurClient == cs) {
                    if (this.mBoundToMethod) {
                        this.mBoundToMethod = false;
                        if (this.mCurMethod != null) {
                            executeOrSendMessage(this.mCurMethod, this.mCaller.obtainMessageO(1000, this.mCurMethod));
                        }
                    }
                    this.mCurClient = null;
                    this.mCurActivityViewToScreenMatrix = null;
                }
                if (this.mCurFocusedWindowClient == cs) {
                    this.mCurFocusedWindowClient = null;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void executeOrSendMessage(IInterface target, Message msg) {
        if (target.asBinder() instanceof Binder) {
            this.mCaller.sendMessage(msg);
            return;
        }
        handleMessage(msg);
        msg.recycle();
    }

    /* access modifiers changed from: package-private */
    public void unbindCurrentClientLocked(int unbindClientReason) {
        if (this.mCurClient != null) {
            if (this.mBoundToMethod) {
                this.mBoundToMethod = false;
                IInputMethod iInputMethod = this.mCurMethod;
                if (iInputMethod != null) {
                    executeOrSendMessage(iInputMethod, this.mCaller.obtainMessageO(1000, iInputMethod));
                }
            }
            executeOrSendMessage(this.mCurClient.client, this.mCaller.obtainMessageIIO(MSG_SET_ACTIVE, 0, 0, this.mCurClient));
            executeOrSendMessage(this.mCurClient.client, this.mCaller.obtainMessageIIO(MSG_UNBIND_CLIENT, this.mCurSeq, unbindClientReason, this.mCurClient.client));
            this.mCurClient.sessionRequested = false;
            this.mCurClient = null;
            this.mCurActivityViewToScreenMatrix = null;
            hideInputMethodMenuLocked();
        }
    }

    private int getImeShowFlags() {
        if (this.mShowForced) {
            return 0 | 3;
        }
        if (this.mShowExplicitlyRequested) {
            return 0 | 1;
        }
        return 0;
    }

    private int getAppShowFlags() {
        if (this.mShowForced) {
            return 0 | 2;
        }
        if (!this.mShowExplicitlyRequested) {
            return 0 | 1;
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mMethodMap"})
    public InputBindResult attachNewInputLocked(int startInputReason, boolean initial) {
        if (!this.mBoundToMethod) {
            IInputMethod iInputMethod = this.mCurMethod;
            executeOrSendMessage(iInputMethod, this.mCaller.obtainMessageOO(MSG_BIND_INPUT, iInputMethod, this.mCurClient.binding));
            this.mBoundToMethod = true;
        }
        Binder startInputToken = new Binder();
        StartInputInfo info = new StartInputInfo(this.mSettings.getCurrentUserId(), this.mCurToken, this.mCurTokenDisplayId, this.mCurId, startInputReason, !initial, UserHandle.getUserId(this.mCurClient.uid), this.mCurClient.selfReportedDisplayId, this.mCurFocusedWindow, this.mCurAttribute, this.mCurFocusedWindowSoftInputMode, this.mCurSeq);
        this.mImeTargetWindowMap.put(startInputToken, this.mCurFocusedWindow);
        this.mStartInputHistory.addEntry(info);
        SessionState session = this.mCurClient.curSession;
        IInputMethod iInputMethod2 = session.method;
        HandlerCaller handlerCaller = this.mCaller;
        int i = this.mCurInputContextMissingMethods;
        IInputContext iInputContext = this.mCurInputContext;
        EditorInfo editorInfo = this.mCurAttribute;
        executeOrSendMessage(iInputMethod2, handlerCaller.obtainMessageIIOOOO(MSG_START_INPUT, i, initial ^ true ? 1 : 0, startInputToken, session, iInputContext, editorInfo));
        InputChannel inputChannel = null;
        if (this.mShowRequested) {
            showCurrentInputLocked(getAppShowFlags(), (ResultReceiver) null);
        }
        IInputMethodSession iInputMethodSession = session.session;
        if (session.channel != null) {
            inputChannel = session.channel.dup();
        }
        return new InputBindResult(0, iInputMethodSession, inputChannel, this.mCurId, this.mCurSeq, this.mCurActivityViewToScreenMatrix);
    }

    private Matrix getActivityViewToScreenMatrixLocked(int clientDisplayId, int imeDisplayId) {
        if (clientDisplayId == imeDisplayId) {
            return null;
        }
        int displayId = clientDisplayId;
        Matrix matrix = null;
        while (true) {
            ActivityViewInfo info = this.mActivityViewDisplayIdToParentMap.get(displayId);
            if (info == null) {
                return null;
            }
            if (matrix == null) {
                matrix = new Matrix(info.mMatrix);
            } else {
                matrix.postConcat(info.mMatrix);
            }
            if (info.mParentClient.selfReportedDisplayId == imeDisplayId) {
                return matrix;
            }
            displayId = info.mParentClient.selfReportedDisplayId;
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mMethodMap"})
    public InputBindResult startInputUncheckedLocked(ClientState cs, IInputContext inputContext, int missingMethods, EditorInfo attribute, int startInputFlags, int startInputReason) {
        int missingMethods2;
        ClientState clientState = cs;
        EditorInfo editorInfo = attribute;
        String str = this.mCurMethodId;
        if (str == null) {
            return InputBindResult.NO_IME;
        }
        if (!this.mSystemReady) {
            return new InputBindResult(7, (IInputMethodSession) null, (InputChannel) null, str, this.mCurSeq, (Matrix) null);
        }
        if (!InputMethodUtils.checkIfPackageBelongsToUid(this.mAppOpsManager, clientState.uid, editorInfo.packageName)) {
            Slog.e(TAG, "Rejecting this client as it reported an invalid package name. uid=" + clientState.uid + " package=" + editorInfo.packageName);
            return InputBindResult.INVALID_PACKAGE_NAME;
        }
        this.mMiuiSecurityInputMethodHelper.mayChangeInputMethodLocked(editorInfo);
        if (!this.mWindowManagerInternal.isUidAllowedOnDisplay(clientState.selfReportedDisplayId, clientState.uid)) {
            return InputBindResult.INVALID_DISPLAY_ID;
        }
        int displayIdToShowIme = computeImeDisplayIdForTarget(clientState.selfReportedDisplayId, this.mImeDisplayValidator);
        if (this.mCurClient != clientState) {
            this.mCurClientInKeyguard = isKeyguardLocked();
            unbindCurrentClientLocked(1);
            if (this.mIsInteractive) {
                executeOrSendMessage(clientState.client, this.mCaller.obtainMessageIO(MSG_SET_ACTIVE, 1, clientState));
            }
        }
        this.mCurSeq++;
        if (this.mCurSeq <= 0) {
            this.mCurSeq = 1;
        }
        this.mCurClient = clientState;
        this.mCurInputContext = inputContext;
        this.mCurActivityViewToScreenMatrix = getActivityViewToScreenMatrixLocked(clientState.selfReportedDisplayId, displayIdToShowIme);
        if (clientState.selfReportedDisplayId == displayIdToShowIme || this.mCurActivityViewToScreenMatrix != null) {
            missingMethods2 = missingMethods;
        } else {
            missingMethods2 = missingMethods | 8;
        }
        this.mCurInputContextMissingMethods = missingMethods2;
        this.mCurAttribute = editorInfo;
        String str2 = this.mCurId;
        boolean z = false;
        if (str2 == null || !str2.equals(this.mCurMethodId) || displayIdToShowIme != this.mCurTokenDisplayId) {
            int i = startInputReason;
        } else if (clientState.curSession != null) {
            if ((startInputFlags & 8) != 0) {
                z = true;
            }
            return attachNewInputLocked(startInputReason, z);
        } else {
            int i2 = startInputReason;
            if (this.mHaveConnection) {
                if (this.mCurMethod != null) {
                    requestClientSessionLocked(cs);
                    return new InputBindResult(1, (IInputMethodSession) null, (InputChannel) null, this.mCurId, this.mCurSeq, (Matrix) null);
                } else if (SystemClock.uptimeMillis() < this.mLastBindTime + 3000) {
                    InputMethodInfo inputMethodInfo = this.mMethodMap.get(this.mCurMethodId);
                    if (this.mIsInteractive && inputMethodInfo != null && !InputMethodManagerServiceInjector.checkProcessRunning(inputMethodInfo.getPackageName())) {
                        executeOrSendMessage(clientState.client, this.mCaller.obtainMessageIO(MSG_SET_ACTIVE, 1, clientState));
                    }
                    return new InputBindResult(2, (IInputMethodSession) null, (InputChannel) null, this.mCurId, this.mCurSeq, (Matrix) null);
                } else {
                    EventLog.writeEvent(EventLogTags.IMF_FORCE_RECONNECT_IME, new Object[]{this.mCurMethodId, Long.valueOf(SystemClock.uptimeMillis() - this.mLastBindTime), 0});
                }
            }
        }
        InputMethodInfo info = this.mMethodMap.get(this.mCurMethodId);
        if (info != null) {
            unbindCurrentMethodLocked();
            this.mCurIntent = new Intent("android.view.InputMethod");
            this.mCurIntent.setComponent(info.getComponent());
            this.mCurIntent.putExtra("android.intent.extra.client_label", 17040189);
            this.mCurIntent.putExtra("android.intent.extra.client_intent", PendingIntent.getActivity(this.mContext, 0, new Intent("android.settings.INPUT_METHOD_SETTINGS"), 0));
            if (bindCurrentInputMethodServiceLocked(this.mCurIntent, this, IME_CONNECTION_BIND_FLAGS)) {
                this.mLastBindTime = SystemClock.uptimeMillis();
                this.mHaveConnection = true;
                this.mCurId = info.getId();
                this.mCurToken = new Binder();
                this.mCurTokenDisplayId = displayIdToShowIme;
                try {
                    this.mIWindowManager.addWindowToken(this.mCurToken, 2011, this.mCurTokenDisplayId);
                    if (!this.mVisibleBound) {
                        bindCurrentInputMethodServiceLocked(this.mCurIntent, this.mVisibleConnection, IME_VISIBLE_BIND_FLAGS);
                        this.mVisibleBound = true;
                    }
                } catch (RemoteException e) {
                }
                return new InputBindResult(2, (IInputMethodSession) null, (InputChannel) null, this.mCurId, this.mCurSeq, (Matrix) null);
            }
            this.mCurIntent = null;
            Slog.w(TAG, "Failure connecting to input method service: " + this.mCurIntent);
            return InputBindResult.IME_NOT_CONNECTED;
        }
        throw new IllegalArgumentException("Unknown id: " + this.mCurMethodId);
    }

    static int computeImeDisplayIdForTarget(int displayId, ImeDisplayValidator checker) {
        if (displayId == 0 || displayId == -1 || !checker.displayCanShowIme(displayId)) {
            return 0;
        }
        return displayId;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0051, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onServiceConnected(android.content.ComponentName r8, android.os.IBinder r9) {
        /*
            r7 = this;
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r0 = r7.mMethodMap
            monitor-enter(r0)
            android.content.Intent r1 = r7.mCurIntent     // Catch:{ all -> 0x0052 }
            if (r1 == 0) goto L_0x0050
            android.content.Intent r1 = r7.mCurIntent     // Catch:{ all -> 0x0052 }
            android.content.ComponentName r1 = r1.getComponent()     // Catch:{ all -> 0x0052 }
            boolean r1 = r8.equals(r1)     // Catch:{ all -> 0x0052 }
            if (r1 == 0) goto L_0x0050
            com.android.internal.view.IInputMethod r1 = com.android.internal.view.IInputMethod.Stub.asInterface(r9)     // Catch:{ all -> 0x0052 }
            r7.mCurMethod = r1     // Catch:{ all -> 0x0052 }
            java.lang.String r1 = r8.getPackageName()     // Catch:{ all -> 0x0052 }
            r7.mPackageName = r1     // Catch:{ all -> 0x0052 }
            android.os.IBinder r1 = r7.mCurToken     // Catch:{ all -> 0x0052 }
            if (r1 != 0) goto L_0x002f
            java.lang.String r1 = "InputMethodManagerService"
            java.lang.String r2 = "Service connected without a token!"
            android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x0052 }
            r7.unbindCurrentMethodLocked()     // Catch:{ all -> 0x0052 }
            monitor-exit(r0)     // Catch:{ all -> 0x0052 }
            return
        L_0x002f:
            com.android.internal.view.IInputMethod r1 = r7.mCurMethod     // Catch:{ all -> 0x0052 }
            com.android.internal.os.HandlerCaller r2 = r7.mCaller     // Catch:{ all -> 0x0052 }
            r3 = 1040(0x410, float:1.457E-42)
            int r4 = r7.mCurTokenDisplayId     // Catch:{ all -> 0x0052 }
            com.android.internal.view.IInputMethod r5 = r7.mCurMethod     // Catch:{ all -> 0x0052 }
            android.os.IBinder r6 = r7.mCurToken     // Catch:{ all -> 0x0052 }
            android.os.Message r2 = r2.obtainMessageIOO(r3, r4, r5, r6)     // Catch:{ all -> 0x0052 }
            r7.executeOrSendMessage(r1, r2)     // Catch:{ all -> 0x0052 }
            com.android.server.inputmethod.InputMethodManagerService$ClientState r1 = r7.mCurClient     // Catch:{ all -> 0x0052 }
            if (r1 == 0) goto L_0x0050
            com.android.server.inputmethod.InputMethodManagerService$ClientState r1 = r7.mCurClient     // Catch:{ all -> 0x0052 }
            r7.clearClientSessionLocked(r1)     // Catch:{ all -> 0x0052 }
            com.android.server.inputmethod.InputMethodManagerService$ClientState r1 = r7.mCurClient     // Catch:{ all -> 0x0052 }
            r7.requestClientSessionLocked(r1)     // Catch:{ all -> 0x0052 }
        L_0x0050:
            monitor-exit(r0)     // Catch:{ all -> 0x0052 }
            return
        L_0x0052:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0052 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.onServiceConnected(android.content.ComponentName, android.os.IBinder):void");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0048, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004a, code lost:
        r9.dispose();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onSessionCreated(com.android.internal.view.IInputMethod r7, com.android.internal.view.IInputMethodSession r8, android.view.InputChannel r9) {
        /*
            r6 = this;
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r0 = r6.mMethodMap
            monitor-enter(r0)
            com.android.internal.view.IInputMethod r1 = r6.mCurMethod     // Catch:{ all -> 0x004e }
            if (r1 == 0) goto L_0x0049
            if (r7 == 0) goto L_0x0049
            com.android.internal.view.IInputMethod r1 = r6.mCurMethod     // Catch:{ all -> 0x004e }
            android.os.IBinder r1 = r1.asBinder()     // Catch:{ all -> 0x004e }
            android.os.IBinder r2 = r7.asBinder()     // Catch:{ all -> 0x004e }
            if (r1 != r2) goto L_0x0049
            com.android.server.inputmethod.InputMethodManagerService$ClientState r1 = r6.mCurClient     // Catch:{ all -> 0x004e }
            if (r1 == 0) goto L_0x0049
            com.android.server.inputmethod.InputMethodManagerService$ClientState r1 = r6.mCurClient     // Catch:{ all -> 0x004e }
            r6.clearClientSessionLocked(r1)     // Catch:{ all -> 0x004e }
            com.android.server.inputmethod.InputMethodManagerService$ClientState r1 = r6.mCurClient     // Catch:{ all -> 0x004e }
            com.android.server.inputmethod.InputMethodManagerService$SessionState r2 = new com.android.server.inputmethod.InputMethodManagerService$SessionState     // Catch:{ all -> 0x004e }
            com.android.server.inputmethod.InputMethodManagerService$ClientState r3 = r6.mCurClient     // Catch:{ all -> 0x004e }
            r2.<init>(r3, r7, r8, r9)     // Catch:{ all -> 0x004e }
            r1.curSession = r2     // Catch:{ all -> 0x004e }
            r1 = 9
            r2 = 1
            com.android.internal.view.InputBindResult r1 = r6.attachNewInputLocked(r1, r2)     // Catch:{ all -> 0x004e }
            com.android.internal.view.IInputMethodSession r2 = r1.method     // Catch:{ all -> 0x004e }
            if (r2 == 0) goto L_0x0047
            com.android.server.inputmethod.InputMethodManagerService$ClientState r2 = r6.mCurClient     // Catch:{ all -> 0x004e }
            com.android.internal.view.IInputMethodClient r2 = r2.client     // Catch:{ all -> 0x004e }
            com.android.internal.os.HandlerCaller r3 = r6.mCaller     // Catch:{ all -> 0x004e }
            r4 = 3010(0xbc2, float:4.218E-42)
            com.android.server.inputmethod.InputMethodManagerService$ClientState r5 = r6.mCurClient     // Catch:{ all -> 0x004e }
            com.android.internal.view.IInputMethodClient r5 = r5.client     // Catch:{ all -> 0x004e }
            android.os.Message r3 = r3.obtainMessageOO(r4, r5, r1)     // Catch:{ all -> 0x004e }
            r6.executeOrSendMessage(r2, r3)     // Catch:{ all -> 0x004e }
        L_0x0047:
            monitor-exit(r0)     // Catch:{ all -> 0x004e }
            return
        L_0x0049:
            monitor-exit(r0)     // Catch:{ all -> 0x004e }
            r9.dispose()
            return
        L_0x004e:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004e }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.onSessionCreated(com.android.internal.view.IInputMethod, com.android.internal.view.IInputMethodSession, android.view.InputChannel):void");
    }

    /* access modifiers changed from: package-private */
    public void unbindCurrentMethodLocked() {
        if (this.mVisibleBound) {
            this.mContext.unbindService(this.mVisibleConnection);
            this.mVisibleBound = false;
        }
        if (this.mHaveConnection) {
            this.mContext.unbindService(this);
            this.mHaveConnection = false;
        }
        IBinder iBinder = this.mCurToken;
        if (iBinder != null) {
            try {
                this.mIWindowManager.removeWindowToken(iBinder, this.mCurTokenDisplayId);
            } catch (RemoteException e) {
            }
            this.mImeWindowVis = 0;
            this.mBackDisposition = 0;
            updateSystemUiLocked(this.mImeWindowVis, this.mBackDisposition);
            this.mCurToken = null;
            this.mCurTokenDisplayId = -1;
        }
        this.mCurId = null;
        clearCurMethodLocked();
    }

    /* access modifiers changed from: package-private */
    public void resetCurrentMethodAndClient(int unbindClientReason) {
        this.mCurMethodId = null;
        unbindCurrentMethodLocked();
        unbindCurrentClientLocked(unbindClientReason);
    }

    /* access modifiers changed from: package-private */
    public void requestClientSessionLocked(ClientState cs) {
        if (!cs.sessionRequested) {
            InputChannel[] channels = InputChannel.openInputChannelPair(cs.toString());
            cs.sessionRequested = true;
            IInputMethod iInputMethod = this.mCurMethod;
            executeOrSendMessage(iInputMethod, this.mCaller.obtainMessageOOO(MSG_CREATE_SESSION, iInputMethod, channels[1], new MethodCallback(this, iInputMethod, channels[0])));
        }
    }

    /* access modifiers changed from: package-private */
    public void clearClientSessionLocked(ClientState cs) {
        finishSessionLocked(cs.curSession);
        cs.curSession = null;
        cs.sessionRequested = false;
    }

    private void finishSessionLocked(SessionState sessionState) {
        if (sessionState != null) {
            if (sessionState.session != null) {
                try {
                    sessionState.session.finishSession();
                } catch (RemoteException e) {
                    Slog.w(TAG, "Session failed to close due to remote exception", e);
                    updateSystemUiLocked(0, this.mBackDisposition);
                }
                sessionState.session = null;
            }
            if (sessionState.channel != null) {
                sessionState.channel.dispose();
                sessionState.channel = null;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void clearCurMethodLocked() {
        if (this.mCurMethod != null) {
            int numClients = this.mClients.size();
            for (int i = 0; i < numClients; i++) {
                clearClientSessionLocked(this.mClients.valueAt(i));
            }
            finishSessionLocked(this.mEnabledSession);
            this.mEnabledSession = null;
            this.mCurMethod = null;
        }
        StatusBarManagerService statusBarManagerService = this.mStatusBar;
        if (statusBarManagerService != null) {
            statusBarManagerService.setIconVisibility(this.mSlotIme, false);
        }
        this.mInFullscreenMode = false;
    }

    public void onServiceDisconnected(ComponentName name) {
        synchronized (this.mMethodMap) {
            if (!(this.mCurMethod == null || this.mCurIntent == null || !name.equals(this.mCurIntent.getComponent()))) {
                clearCurMethodLocked();
                this.mLastBindTime = SystemClock.uptimeMillis();
                this.mShowRequested = this.mInputShown;
                this.mInputShown = false;
                unbindCurrentClientLocked(3);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* access modifiers changed from: private */
    public void updateStatusIcon(IBinder token, String packageName, int iconId) {
        synchronized (this.mMethodMap) {
            if (calledWithValidTokenLocked(token)) {
                long ident = Binder.clearCallingIdentity();
                if (iconId == 0) {
                    try {
                        if (this.mStatusBar != null) {
                            this.mStatusBar.setIconVisibility(this.mSlotIme, false);
                        }
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(ident);
                        throw th;
                    }
                } else if (packageName != null) {
                    CharSequence contentDescription = null;
                    try {
                        contentDescription = this.mContext.getPackageManager().getApplicationLabel(this.mIPackageManager.getApplicationInfo(packageName, 0, this.mSettings.getCurrentUserId()));
                    } catch (RemoteException e) {
                    }
                    if (this.mStatusBar != null) {
                        this.mStatusBar.setIcon(this.mSlotIme, packageName, iconId, 0, contentDescription != null ? contentDescription.toString() : null);
                        this.mStatusBar.setIconVisibility(this.mSlotIme, true);
                    }
                }
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    private boolean shouldShowImeSwitcherLocked(int visibility) {
        KeyguardManager keyguardManager;
        if (this.mMiuiSecurityInputMethodHelper.shouldHideImeSwitcherLocked() || !this.mShowOngoingImeSwitcherForPhones || this.mSwitchingDialog != null) {
            return false;
        }
        if ((this.mWindowManagerInternal.isKeyguardShowingAndNotOccluded() && (keyguardManager = this.mKeyguardManager) != null && keyguardManager.isKeyguardSecure()) || (visibility & 1) == 0 || (visibility & 4) != 0) {
            return false;
        }
        if (this.mWindowManagerInternal.isHardKeyboardAvailable()) {
            if (this.mHardKeyboardBehavior == 0) {
                return true;
            }
        } else if ((visibility & 2) == 0) {
            return false;
        }
        List<InputMethodInfo> imis = this.mSettings.getEnabledInputMethodListLocked();
        int N = imis.size();
        if (N > 2) {
            return true;
        }
        if (N < 1) {
            return false;
        }
        int nonAuxCount = 0;
        int auxCount = 0;
        InputMethodSubtype nonAuxSubtype = null;
        InputMethodSubtype auxSubtype = null;
        for (int i = 0; i < N; i++) {
            List<InputMethodSubtype> subtypes = this.mSettings.getEnabledInputMethodSubtypeListLocked(this.mContext, imis.get(i), true);
            int subtypeCount = subtypes.size();
            if (subtypeCount == 0) {
                nonAuxCount++;
            } else {
                for (int j = 0; j < subtypeCount; j++) {
                    InputMethodSubtype subtype = subtypes.get(j);
                    if (!subtype.isAuxiliary()) {
                        nonAuxCount++;
                        nonAuxSubtype = subtype;
                    } else {
                        auxCount++;
                        auxSubtype = subtype;
                    }
                }
            }
        }
        if (nonAuxCount > 1 || auxCount > 1) {
            return true;
        }
        if (nonAuxCount != 1 || auxCount != 1) {
            return false;
        }
        if (nonAuxSubtype == null || auxSubtype == null || ((!nonAuxSubtype.getLocale().equals(auxSubtype.getLocale()) && !auxSubtype.overridesImplicitlyEnabledSubtype() && !nonAuxSubtype.overridesImplicitlyEnabledSubtype()) || !nonAuxSubtype.containsExtraValueKey(TAG_TRY_SUPPRESSING_IME_SWITCHER))) {
            return true;
        }
        return false;
    }

    private boolean isKeyguardLocked() {
        KeyguardManager keyguardManager = this.mKeyguardManager;
        return keyguardManager != null && keyguardManager.isKeyguardLocked();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0023, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0025, code lost:
        if (r9 == 1) goto L_0x0034;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0028, code lost:
        if (r9 == 2) goto L_0x0032;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x002c, code lost:
        if ((r8 & 2) == 0) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x002e, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0030, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0032, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0034, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0036, code lost:
        r4 = r6.mWindowManagerInternal;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x003a, code lost:
        if ((r8 & 2) == 0) goto L_0x003d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x003c, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x003d, code lost:
        r4.updateInputMethodWindowStatus(r7, r1, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0040, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setImeWindowStatus(android.os.IBinder r7, int r8, int r9) {
        /*
            r6 = this;
            com.android.server.wm.WindowManagerInternal r0 = r6.mWindowManagerInternal
            int r0 = r0.getTopFocusedDisplayId()
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r1 = r6.mMethodMap
            monitor-enter(r1)
            boolean r2 = r6.calledWithValidTokenLocked(r7)     // Catch:{ all -> 0x0041 }
            if (r2 != 0) goto L_0x0011
            monitor-exit(r1)     // Catch:{ all -> 0x0041 }
            return
        L_0x0011:
            int r2 = r6.mCurTokenDisplayId     // Catch:{ all -> 0x0041 }
            if (r2 == r0) goto L_0x001b
            int r2 = r6.mCurTokenDisplayId     // Catch:{ all -> 0x0041 }
            if (r2 == 0) goto L_0x001b
            monitor-exit(r1)     // Catch:{ all -> 0x0041 }
            return
        L_0x001b:
            r6.mImeWindowVis = r8     // Catch:{ all -> 0x0041 }
            r6.mBackDisposition = r9     // Catch:{ all -> 0x0041 }
            r6.updateSystemUiLocked(r8, r9)     // Catch:{ all -> 0x0041 }
            monitor-exit(r1)     // Catch:{ all -> 0x0041 }
            r1 = 0
            r2 = 1
            if (r9 == r2) goto L_0x0034
            r3 = 2
            if (r9 == r3) goto L_0x0032
            r3 = r8 & 2
            if (r3 == 0) goto L_0x0030
            r3 = r2
            goto L_0x0031
        L_0x0030:
            r3 = r1
        L_0x0031:
            goto L_0x0036
        L_0x0032:
            r3 = 1
            goto L_0x0036
        L_0x0034:
            r3 = 0
        L_0x0036:
            com.android.server.wm.WindowManagerInternal r4 = r6.mWindowManagerInternal
            r5 = r8 & 2
            if (r5 == 0) goto L_0x003d
            r1 = r2
        L_0x003d:
            r4.updateInputMethodWindowStatus(r7, r1, r3)
            return
        L_0x0041:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0041 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.setImeWindowStatus(android.os.IBinder, int, int):void");
    }

    /* access modifiers changed from: private */
    public void reportStartInput(IBinder token, IBinder startInputToken) {
        synchronized (this.mMethodMap) {
            if (calledWithValidTokenLocked(token)) {
                IBinder targetWindow = this.mImeTargetWindowMap.get(startInputToken);
                if (!(targetWindow == null || this.mLastImeTargetWindow == targetWindow)) {
                    this.mWindowManagerInternal.updateInputMethodTargetWindow(token, targetWindow);
                }
                this.mLastImeTargetWindow = targetWindow;
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateSystemUiLocked(int vis, int backDisposition) {
        if (this.mCurToken != null) {
            long ident = Binder.clearCallingIdentity();
            if (vis != 0) {
                try {
                    if (isKeyguardLocked() && !this.mCurClientInKeyguard) {
                        vis = 0;
                    }
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(ident);
                    throw th;
                }
            }
            boolean needsToShowImeSwitcher = shouldShowImeSwitcherLocked(vis);
            if (this.mStatusBar != null) {
                this.mStatusBar.setImeWindowStatus(this.mCurTokenDisplayId, this.mCurToken, vis, backDisposition, needsToShowImeSwitcher);
            }
            InputMethodInfo imi = this.mMethodMap.get(this.mCurMethodId);
            if (imi != null && needsToShowImeSwitcher) {
                CharSequence title = this.mRes.getText(17041077);
                this.mImeSwitcherNotification.setContentTitle(title).setContentText(InputMethodUtils.getImeAndSubtypeDisplayName(this.mContext, imi, this.mCurrentSubtype)).setContentIntent(this.mImeSwitchPendingIntent);
                if (this.mNotificationManager != null) {
                    Notification notification = this.mImeSwitcherNotification.build();
                    notification.extras.putBoolean("miui.enableFloat", false);
                    this.mNotificationManager.notifyAsUser((String) null, 8, notification, UserHandle.ALL);
                    this.mNotificationShown = true;
                }
            } else if (this.mNotificationShown && this.mNotificationManager != null) {
                this.mNotificationManager.cancelAsUser((String) null, 8, UserHandle.ALL);
                this.mNotificationShown = false;
            }
            Binder.restoreCallingIdentity(ident);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateFromSettingsLocked(boolean enabledMayChange) {
        updateInputMethodsFromSettingsLocked(enabledMayChange);
        updateKeyboardFromSettingsLocked();
    }

    /* access modifiers changed from: package-private */
    public void updateInputMethodsFromSettingsLocked(boolean enabledMayChange) {
        if (enabledMayChange) {
            List<InputMethodInfo> enabled = this.mSettings.getEnabledInputMethodListLocked();
            for (int i = 0; i < enabled.size(); i++) {
                InputMethodInfo imm = enabled.get(i);
                try {
                    ApplicationInfo ai = this.mIPackageManager.getApplicationInfo(imm.getPackageName(), 32768, this.mSettings.getCurrentUserId());
                    if (ai != null && ai.enabledSetting == 4) {
                        this.mIPackageManager.setApplicationEnabledSetting(imm.getPackageName(), 0, 1, this.mSettings.getCurrentUserId(), this.mContext.getBasePackageName());
                    }
                } catch (RemoteException e) {
                }
            }
        }
        String id = this.mSettings.getSelectedInputMethod();
        if (TextUtils.isEmpty(id) && chooseNewDefaultIMELocked()) {
            id = this.mSettings.getSelectedInputMethod();
        }
        if (!TextUtils.isEmpty(id)) {
            try {
                setInputMethodLocked(id, this.mSettings.getSelectedInputMethodSubtypeId(id));
            } catch (IllegalArgumentException e2) {
                Slog.w(TAG, "Unknown input method from prefs: " + id, e2);
                resetCurrentMethodAndClient(5);
            }
        } else {
            resetCurrentMethodAndClient(4);
        }
        this.mSwitchingController.resetCircularListLocked(this.mContext);
    }

    public void updateKeyboardFromSettingsLocked() {
        this.mShowImeWithHardKeyboard = this.mSettings.isShowImeWithHardKeyboardEnabled();
        AlertDialog alertDialog = this.mSwitchingDialog;
        if (alertDialog != null && this.mSwitchingDialogTitleView != null && alertDialog.isShowing()) {
            ((Switch) this.mSwitchingDialogTitleView.findViewById(16908984)).setChecked(this.mShowImeWithHardKeyboard);
        }
    }

    /* access modifiers changed from: package-private */
    public void setInputMethodLocked(String id, int subtypeId) {
        InputMethodSubtype newSubtype;
        InputMethodInfo info = this.mMethodMap.get(id);
        if (info == null) {
            throw new IllegalArgumentException("Unknown id: " + id);
        } else if (id.equals(this.mCurMethodId)) {
            int subtypeCount = info.getSubtypeCount();
            if (subtypeCount > 0) {
                InputMethodSubtype oldSubtype = this.mCurrentSubtype;
                if (subtypeId < 0 || subtypeId >= subtypeCount) {
                    newSubtype = getCurrentInputMethodSubtypeLocked();
                } else {
                    newSubtype = info.getSubtypeAt(subtypeId);
                }
                if (newSubtype == null || oldSubtype == null) {
                    Slog.w(TAG, "Illegal subtype state: old subtype = " + oldSubtype + ", new subtype = " + newSubtype);
                } else if (newSubtype != oldSubtype) {
                    setSelectedInputMethodAndSubtypeLocked(info, subtypeId, true);
                    if (this.mCurMethod != null) {
                        try {
                            updateSystemUiLocked(this.mImeWindowVis, this.mBackDisposition);
                            this.mCurMethod.changeInputMethodSubtype(newSubtype);
                        } catch (RemoteException e) {
                            Slog.w(TAG, "Failed to call changeInputMethodSubtype");
                        }
                    }
                }
            }
        } else {
            long ident = Binder.clearCallingIdentity();
            try {
                setSelectedInputMethodAndSubtypeLocked(info, subtypeId, false);
                this.mCurMethodId = id;
                if (((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).isSystemReady()) {
                    Intent intent = new Intent("android.intent.action.INPUT_METHOD_CHANGED");
                    intent.addFlags(536870912);
                    intent.putExtra("input_method_id", id);
                    this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
                }
                unbindCurrentClientLocked(2);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    public boolean showSoftInput(IInputMethodClient client, int flags, ResultReceiver resultReceiver) {
        int uid = Binder.getCallingUid();
        synchronized (this.mMethodMap) {
            if (!calledFromValidUserLocked()) {
                return false;
            }
            long ident = Binder.clearCallingIdentity();
            try {
                if (this.mCurClient == null || client == null || this.mCurClient.client.asBinder() != client.asBinder()) {
                    ClientState cs = this.mClients.get(client.asBinder());
                    if (cs == null) {
                        throw new IllegalArgumentException("unknown client " + client.asBinder());
                    } else if (!this.mWindowManagerInternal.isInputMethodClientFocus(cs.uid, cs.pid, cs.selfReportedDisplayId)) {
                        Slog.w(TAG, "Ignoring showSoftInput of uid " + uid + ": " + client);
                        return false;
                    }
                }
                boolean showCurrentInputLocked = showCurrentInputLocked(flags, resultReceiver);
                Binder.restoreCallingIdentity(ident);
                return showCurrentInputLocked;
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mMethodMap"})
    public boolean showCurrentInputLocked(int flags, ResultReceiver resultReceiver) {
        this.mShowRequested = true;
        if (this.mAccessibilityRequestingNoSoftKeyboard) {
            return false;
        }
        if ((flags & 2) != 0) {
            this.mShowExplicitlyRequested = true;
            this.mShowForced = true;
        } else if ((flags & 1) == 0) {
            this.mShowExplicitlyRequested = true;
        }
        if (!this.mSystemReady) {
            return false;
        }
        IInputMethod iInputMethod = this.mCurMethod;
        if (iInputMethod != null) {
            executeOrSendMessage(iInputMethod, this.mCaller.obtainMessageIOO(MSG_SHOW_SOFT_INPUT, getImeShowFlags(), this.mCurMethod, resultReceiver));
            if (!this.mInputShown) {
                reportMethodEvent(10001);
            }
            this.mInputShown = true;
            return true;
        } else if (!this.mHaveConnection || SystemClock.uptimeMillis() < this.mLastBindTime + 3000) {
            return false;
        } else {
            EventLog.writeEvent(EventLogTags.IMF_FORCE_RECONNECT_IME, new Object[]{this.mCurMethodId, Long.valueOf(SystemClock.uptimeMillis() - this.mLastBindTime), 1});
            Slog.w(TAG, "Force disconnect/connect to the IME in showCurrentInputLocked()");
            this.mContext.unbindService(this);
            bindCurrentInputMethodServiceLocked(this.mCurIntent, this, IME_CONNECTION_BIND_FLAGS);
            return false;
        }
    }

    private void reportMethodEvent(int eventType) {
        int userid = UserHandle.getUserId(Binder.getCallingUid());
        UsageStatsManagerInternal localUsageStatsManager = (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class);
        if (localUsageStatsManager != null) {
            localUsageStatsManager.reportEvent(this.mPackageName, userid, eventType);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    public boolean hideSoftInput(IInputMethodClient client, int flags, ResultReceiver resultReceiver) {
        int callingUid = Binder.getCallingUid();
        synchronized (this.mMethodMap) {
            if (!calledFromValidUserLocked()) {
                return false;
            }
            long ident = Binder.clearCallingIdentity();
            try {
                if (this.mCurClient == null || client == null || this.mCurClient.client.asBinder() != client.asBinder()) {
                    ClientState cs = this.mClients.get(client.asBinder());
                    if (cs == null) {
                        throw new IllegalArgumentException("unknown client " + client.asBinder());
                    } else if (!this.mWindowManagerInternal.isInputMethodClientFocus(cs.uid, cs.pid, cs.selfReportedDisplayId)) {
                        Binder.restoreCallingIdentity(ident);
                        return false;
                    }
                }
                boolean hideCurrentInputLocked = hideCurrentInputLocked(flags, resultReceiver);
                return hideCurrentInputLocked;
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hideCurrentInputLocked(int flags, ResultReceiver resultReceiver) {
        boolean res;
        if ((flags & 1) != 0 && (this.mShowExplicitlyRequested || this.mShowForced)) {
            return false;
        }
        if (this.mShowForced && (flags & 2) != 0) {
            return false;
        }
        boolean shouldHideSoftInput = true;
        if (this.mCurMethod == null || (!this.mInputShown && (this.mImeWindowVis & 1) == 0)) {
            shouldHideSoftInput = false;
        }
        if (shouldHideSoftInput) {
            IInputMethod iInputMethod = this.mCurMethod;
            executeOrSendMessage(iInputMethod, this.mCaller.obtainMessageOO(MSG_HIDE_SOFT_INPUT, iInputMethod, resultReceiver));
            if (this.mInputShown) {
                reportMethodEvent(10002);
            }
            res = true;
        } else {
            res = false;
        }
        this.mInputShown = false;
        this.mShowRequested = false;
        this.mShowExplicitlyRequested = false;
        this.mShowForced = false;
        return res;
    }

    /* Debug info: failed to restart local var, previous not found, register: 20 */
    public InputBindResult startInputOrWindowGainedFocus(int startInputReason, IInputMethodClient client, IBinder windowToken, int startInputFlags, int softInputMode, int windowFlags, EditorInfo attribute, IInputContext inputContext, int missingMethods, int unverifiedTargetSdkVersion) {
        int userId;
        ArrayMap<String, InputMethodInfo> arrayMap;
        EditorInfo editorInfo = attribute;
        if (windowToken == null) {
            Slog.e(TAG, "windowToken cannot be null.");
            return InputBindResult.NULL;
        }
        int callingUserId = UserHandle.getCallingUserId();
        if (editorInfo == null || editorInfo.targetInputMethodUser == null || editorInfo.targetInputMethodUser.getIdentifier() == callingUserId) {
            userId = callingUserId;
        } else {
            this.mContext.enforceCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "Using EditorInfo.targetInputMethodUser requires INTERACT_ACROSS_USERS_FULL.");
            int userId2 = editorInfo.targetInputMethodUser.getIdentifier();
            if (!this.mUserManagerInternal.isUserRunning(userId2)) {
                Slog.e(TAG, "User #" + userId2 + " is not running.");
                return InputBindResult.INVALID_USER;
            }
            userId = userId2;
        }
        ArrayMap<String, InputMethodInfo> arrayMap2 = this.mMethodMap;
        synchronized (arrayMap2) {
            try {
                long ident = Binder.clearCallingIdentity();
                arrayMap = arrayMap2;
                try {
                    InputBindResult result = startInputOrWindowGainedFocusInternalLocked(startInputReason, client, windowToken, startInputFlags, softInputMode, windowFlags, attribute, inputContext, missingMethods, unverifiedTargetSdkVersion, userId);
                    Binder.restoreCallingIdentity(ident);
                    if (result != null) {
                        return result;
                    }
                    Slog.wtf(TAG, "InputBindResult is @NonNull. startInputReason=" + InputMethodDebug.startInputReasonToString(startInputReason) + " windowFlags=#" + Integer.toHexString(windowFlags) + " editorInfo=" + editorInfo);
                    return InputBindResult.NULL;
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                arrayMap = arrayMap2;
                throw th;
            }
        }
    }

    private InputBindResult startInputOrWindowGainedFocusInternalLocked(int startInputReason, IInputMethodClient client, IBinder windowToken, int startInputFlags, int softInputMode, int windowFlags, EditorInfo attribute, IInputContext inputContext, int missingMethods, int unverifiedTargetSdkVersion, int userId) {
        int i;
        IBinder iBinder = windowToken;
        int i2 = startInputFlags;
        int i3 = softInputMode;
        int i4 = unverifiedTargetSdkVersion;
        int i5 = userId;
        int windowDisplayId = this.mWindowManagerInternal.getDisplayIdForWindow(iBinder);
        ClientState cs = this.mClients.get(client.asBinder());
        if (cs == null) {
            throw new IllegalArgumentException("unknown client " + client.asBinder());
        } else if (cs.selfReportedDisplayId != windowDisplayId) {
            Slog.e(TAG, "startInputOrWindowGainedFocusInternal: display ID mismatch. from client:" + cs.selfReportedDisplayId + " from window:" + windowDisplayId);
            return InputBindResult.DISPLAY_ID_MISMATCH;
        } else if (!this.mWindowManagerInternal.isInputMethodClientFocus(cs.uid, cs.pid, cs.selfReportedDisplayId)) {
            return InputBindResult.NOT_IME_TARGET_WINDOW;
        } else {
            if (!this.mSettings.isCurrentProfile(i5)) {
                Slog.w(TAG, "A background user is requesting window. Hiding IME.");
                Slog.w(TAG, "If you need to impersonate a foreground user/profile from a background user, use EditorInfo.targetInputMethodUser with INTERACT_ACROSS_USERS_FULL permission.");
                hideCurrentInputLocked(0, (ResultReceiver) null);
                return InputBindResult.INVALID_USER;
            }
            if (!(!InputMethodSystemProperty.PER_PROFILE_IME_ENABLED || i5 == this.mSettings.getCurrentUserId() || i5 == 999)) {
                switchUserLocked(i5);
            }
            cs.shouldPreRenderIme = DebugFlags.FLAG_PRE_RENDER_IME_VIEWS.value() && !this.mIsLowRam;
            if (this.mCurFocusedWindow != iBinder) {
                this.mCurFocusedWindow = iBinder;
                this.mCurFocusedWindowSoftInputMode = i3;
                this.mCurFocusedWindowClient = cs;
                boolean doAutoShow = (i3 & 240) == 16 || this.mRes.getConfiguration().isLayoutSizeAtLeast(3);
                boolean isTextEditor = (i2 & 2) != 0;
                boolean didStart = false;
                InputBindResult res = null;
                int i6 = i3 & 15;
                if (i6 != 0) {
                    if (i6 != 1) {
                        if (i6 != 2) {
                            if (i6 == 3) {
                                hideCurrentInputLocked(0, (ResultReceiver) null);
                            } else if (i6 != 4) {
                                if (i6 == 5) {
                                    if (InputMethodUtils.isSoftInputModeStateVisibleAllowed(i4, i2)) {
                                        if (attribute != null) {
                                            i = 1;
                                            res = startInputUncheckedLocked(cs, inputContext, missingMethods, attribute, startInputFlags, startInputReason);
                                            didStart = true;
                                        } else {
                                            i = 1;
                                        }
                                        showCurrentInputLocked(i, (ResultReceiver) null);
                                    } else {
                                        Slog.e(TAG, "SOFT_INPUT_STATE_ALWAYS_VISIBLE is ignored because there is no focused view that also returns true from View#onCheckIsTextEditor()");
                                    }
                                }
                            } else if ((i3 & 256) != 0) {
                                if (InputMethodUtils.isSoftInputModeStateVisibleAllowed(i4, i2)) {
                                    if (attribute != null) {
                                        res = startInputUncheckedLocked(cs, inputContext, missingMethods, attribute, startInputFlags, startInputReason);
                                        didStart = true;
                                    }
                                    showCurrentInputLocked(1, (ResultReceiver) null);
                                } else {
                                    Slog.e(TAG, "SOFT_INPUT_STATE_VISIBLE is ignored because there is no focused view that also returns true from View#onCheckIsTextEditor()");
                                }
                            }
                        } else if ((i3 & 256) != 0) {
                            hideCurrentInputLocked(0, (ResultReceiver) null);
                        }
                    }
                } else if (!isTextEditor || !doAutoShow) {
                    if (WindowManager.LayoutParams.mayUseInputMethod(windowFlags)) {
                        hideCurrentInputLocked(2, (ResultReceiver) null);
                        if (cs.selfReportedDisplayId != this.mCurTokenDisplayId) {
                            unbindCurrentMethodLocked();
                        }
                    }
                } else if (isTextEditor && doAutoShow && (i3 & 256) != 0) {
                    if (attribute != null) {
                        res = startInputUncheckedLocked(cs, inputContext, missingMethods, attribute, startInputFlags, startInputReason);
                        didStart = true;
                    }
                    showCurrentInputLocked(1, (ResultReceiver) null);
                }
                if (didStart) {
                    return res;
                }
                if (attribute == null) {
                    return InputBindResult.NULL_EDITOR_INFO;
                }
                if (!DebugFlags.FLAG_OPTIMIZE_START_INPUT.value() || (i2 & 2) != 0) {
                    return startInputUncheckedLocked(cs, inputContext, missingMethods, attribute, startInputFlags, startInputReason);
                }
                return InputBindResult.NO_EDITOR;
            } else if (attribute != null) {
                return startInputUncheckedLocked(cs, inputContext, missingMethods, attribute, startInputFlags, startInputReason);
            } else {
                return new InputBindResult(3, (IInputMethodSession) null, (InputChannel) null, (String) null, -1, (Matrix) null);
            }
        }
    }

    private boolean canShowInputMethodPickerLocked(IInputMethodClient client) {
        int uid = Binder.getCallingUid();
        ClientState clientState = this.mCurFocusedWindowClient;
        if (clientState != null && client != null && clientState.client.asBinder() == client.asBinder()) {
            return true;
        }
        Intent intent = this.mCurIntent;
        if (intent == null || !InputMethodUtils.checkIfPackageBelongsToUid(this.mAppOpsManager, uid, intent.getComponent().getPackageName())) {
            return false;
        }
        return true;
    }

    public void showInputMethodPickerFromClient(IInputMethodClient client, int auxiliarySubtypeMode) {
        synchronized (this.mMethodMap) {
            if (calledFromValidUserLocked()) {
                if (!canShowInputMethodPickerLocked(client)) {
                    Slog.w(TAG, "Ignoring showInputMethodPickerFromClient of uid " + Binder.getCallingUid() + ": " + client);
                    return;
                }
                this.mHandler.sendMessage(this.mCaller.obtainMessageII(1, auxiliarySubtypeMode, this.mCurClient != null ? this.mCurClient.selfReportedDisplayId : 0));
            }
        }
    }

    public void showInputMethodPickerFromSystem(IInputMethodClient client, int auxiliarySubtypeMode, int displayId) {
        if (this.mContext.checkCallingPermission("android.permission.WRITE_SECURE_SETTINGS") == 0) {
            this.mHandler.sendMessage(this.mCaller.obtainMessageII(1, auxiliarySubtypeMode, displayId));
            return;
        }
        throw new SecurityException("showInputMethodPickerFromSystem requires WRITE_SECURE_SETTINGS permission");
    }

    public boolean isInputMethodPickerShownForTest() {
        synchronized (this.mMethodMap) {
            if (this.mSwitchingDialog == null) {
                return false;
            }
            boolean isShowing = this.mSwitchingDialog.isShowing();
            return isShowing;
        }
    }

    /* access modifiers changed from: private */
    public void setInputMethod(IBinder token, String id) {
        synchronized (this.mMethodMap) {
            if (calledWithValidTokenLocked(token)) {
                setInputMethodWithSubtypeIdLocked(token, id, -1);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0025, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setInputMethodAndSubtype(android.os.IBinder r4, java.lang.String r5, android.view.inputmethod.InputMethodSubtype r6) {
        /*
            r3 = this;
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r0 = r3.mMethodMap
            monitor-enter(r0)
            boolean r1 = r3.calledWithValidTokenLocked(r4)     // Catch:{ all -> 0x0026 }
            if (r1 != 0) goto L_0x000b
            monitor-exit(r0)     // Catch:{ all -> 0x0026 }
            return
        L_0x000b:
            if (r6 == 0) goto L_0x0021
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r1 = r3.mMethodMap     // Catch:{ all -> 0x0026 }
            java.lang.Object r1 = r1.get(r5)     // Catch:{ all -> 0x0026 }
            android.view.inputmethod.InputMethodInfo r1 = (android.view.inputmethod.InputMethodInfo) r1     // Catch:{ all -> 0x0026 }
            int r2 = r6.hashCode()     // Catch:{ all -> 0x0026 }
            int r1 = com.android.server.inputmethod.InputMethodUtils.getSubtypeIdFromHashCode(r1, r2)     // Catch:{ all -> 0x0026 }
            r3.setInputMethodWithSubtypeIdLocked(r4, r5, r1)     // Catch:{ all -> 0x0026 }
            goto L_0x0024
        L_0x0021:
            r3.setInputMethod(r4, r5)     // Catch:{ all -> 0x0026 }
        L_0x0024:
            monitor-exit(r0)     // Catch:{ all -> 0x0026 }
            return
        L_0x0026:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0026 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.setInputMethodAndSubtype(android.os.IBinder, java.lang.String, android.view.inputmethod.InputMethodSubtype):void");
    }

    public void showInputMethodAndSubtypeEnablerFromClient(IInputMethodClient client, String inputMethodId) {
        synchronized (this.mMethodMap) {
            if (calledFromValidUserLocked()) {
                executeOrSendMessage(this.mCurMethod, this.mCaller.obtainMessageO(2, inputMethodId));
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean switchToPreviousInputMethod(IBinder token) {
        InputMethodInfo lastImi;
        List<InputMethodInfo> enabled;
        String locale;
        InputMethodSubtype keyboardSubtype;
        int currentSubtypeHash;
        synchronized (this.mMethodMap) {
            try {
                if (!calledWithValidTokenLocked(token)) {
                    return false;
                }
                Pair<String, String> lastIme = this.mSettings.getLastInputMethodAndSubtypeLocked();
                if (lastIme != null) {
                    lastImi = this.mMethodMap.get(lastIme.first);
                } else {
                    lastImi = null;
                }
                String targetLastImiId = null;
                int subtypeId = -1;
                if (!(lastIme == null || lastImi == null)) {
                    boolean imiIdIsSame = lastImi.getId().equals(this.mCurMethodId);
                    int lastSubtypeHash = Integer.parseInt((String) lastIme.second);
                    if (this.mCurrentSubtype == null) {
                        currentSubtypeHash = -1;
                    } else {
                        currentSubtypeHash = this.mCurrentSubtype.hashCode();
                    }
                    if (!imiIdIsSame || lastSubtypeHash != currentSubtypeHash) {
                        targetLastImiId = (String) lastIme.first;
                        subtypeId = InputMethodUtils.getSubtypeIdFromHashCode(lastImi, lastSubtypeHash);
                    }
                }
                if (TextUtils.isEmpty(targetLastImiId) && !InputMethodUtils.canAddToLastInputMethod(this.mCurrentSubtype) && (enabled = this.mSettings.getEnabledInputMethodListLocked()) != null) {
                    int N = enabled.size();
                    if (this.mCurrentSubtype == null) {
                        locale = this.mRes.getConfiguration().locale.toString();
                    } else {
                        locale = this.mCurrentSubtype.getLocale();
                    }
                    int i = 0;
                    while (true) {
                        if (i >= N) {
                            break;
                        }
                        InputMethodInfo imi = enabled.get(i);
                        if (imi.getSubtypeCount() > 0 && imi.isSystem() && (keyboardSubtype = InputMethodUtils.findLastResortApplicableSubtypeLocked(this.mRes, InputMethodUtils.getSubtypes(imi), "keyboard", locale, true)) != null) {
                            targetLastImiId = imi.getId();
                            subtypeId = InputMethodUtils.getSubtypeIdFromHashCode(imi, keyboardSubtype.hashCode());
                            if (keyboardSubtype.getLocale().equals(locale)) {
                                break;
                            }
                        }
                        i++;
                    }
                }
                if (!TextUtils.isEmpty(targetLastImiId)) {
                    setInputMethodWithSubtypeIdLocked(token, targetLastImiId, subtypeId);
                    return true;
                }
                IBinder iBinder = token;
                return false;
            } catch (Throwable th) {
                th = th;
                throw th;
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean switchToNextInputMethod(IBinder token, boolean onlyCurrentIme) {
        synchronized (this.mMethodMap) {
            if (!calledWithValidTokenLocked(token)) {
                return false;
            }
            InputMethodSubtypeSwitchingController.ImeSubtypeListItem nextSubtype = this.mSwitchingController.getNextInputMethodLocked(onlyCurrentIme, this.mMethodMap.get(this.mCurMethodId), this.mCurrentSubtype);
            if (nextSubtype == null) {
                return false;
            }
            setInputMethodWithSubtypeIdLocked(token, nextSubtype.mImi.getId(), nextSubtype.mSubtypeId);
            return true;
        }
    }

    /* access modifiers changed from: private */
    public boolean shouldOfferSwitchingToNextInputMethod(IBinder token) {
        synchronized (this.mMethodMap) {
            if (!calledWithValidTokenLocked(token)) {
                return false;
            }
            if (this.mSwitchingController.getNextInputMethodLocked(false, this.mMethodMap.get(this.mCurMethodId), this.mCurrentSubtype) == null) {
                return false;
            }
            return true;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0054, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0059, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.inputmethod.InputMethodSubtype getLastInputMethodSubtype() {
        /*
            r7 = this;
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r0 = r7.mMethodMap
            monitor-enter(r0)
            boolean r1 = r7.calledFromValidUserLocked()     // Catch:{ all -> 0x005a }
            r2 = 0
            if (r1 != 0) goto L_0x000c
            monitor-exit(r0)     // Catch:{ all -> 0x005a }
            return r2
        L_0x000c:
            com.android.server.inputmethod.InputMethodUtils$InputMethodSettings r1 = r7.mSettings     // Catch:{ all -> 0x005a }
            android.util.Pair r1 = r1.getLastInputMethodAndSubtypeLocked()     // Catch:{ all -> 0x005a }
            if (r1 == 0) goto L_0x0058
            java.lang.Object r3 = r1.first     // Catch:{ all -> 0x005a }
            java.lang.CharSequence r3 = (java.lang.CharSequence) r3     // Catch:{ all -> 0x005a }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x005a }
            if (r3 != 0) goto L_0x0058
            java.lang.Object r3 = r1.second     // Catch:{ all -> 0x005a }
            java.lang.CharSequence r3 = (java.lang.CharSequence) r3     // Catch:{ all -> 0x005a }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x005a }
            if (r3 == 0) goto L_0x0029
            goto L_0x0058
        L_0x0029:
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r3 = r7.mMethodMap     // Catch:{ all -> 0x005a }
            java.lang.Object r4 = r1.first     // Catch:{ all -> 0x005a }
            java.lang.Object r3 = r3.get(r4)     // Catch:{ all -> 0x005a }
            android.view.inputmethod.InputMethodInfo r3 = (android.view.inputmethod.InputMethodInfo) r3     // Catch:{ all -> 0x005a }
            if (r3 != 0) goto L_0x0037
            monitor-exit(r0)     // Catch:{ all -> 0x005a }
            return r2
        L_0x0037:
            java.lang.Object r4 = r1.second     // Catch:{ NumberFormatException -> 0x0055 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ NumberFormatException -> 0x0055 }
            int r4 = java.lang.Integer.parseInt(r4)     // Catch:{ NumberFormatException -> 0x0055 }
            int r5 = com.android.server.inputmethod.InputMethodUtils.getSubtypeIdFromHashCode(r3, r4)     // Catch:{ NumberFormatException -> 0x0055 }
            if (r5 < 0) goto L_0x0053
            int r6 = r3.getSubtypeCount()     // Catch:{ NumberFormatException -> 0x0055 }
            if (r5 < r6) goto L_0x004d
            goto L_0x0053
        L_0x004d:
            android.view.inputmethod.InputMethodSubtype r2 = r3.getSubtypeAt(r5)     // Catch:{ NumberFormatException -> 0x0055 }
            monitor-exit(r0)     // Catch:{ all -> 0x005a }
            return r2
        L_0x0053:
            monitor-exit(r0)     // Catch:{ all -> 0x005a }
            return r2
        L_0x0055:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x005a }
            return r2
        L_0x0058:
            monitor-exit(r0)     // Catch:{ all -> 0x005a }
            return r2
        L_0x005a:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x005a }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.getLastInputMethodSubtype():android.view.inputmethod.InputMethodSubtype");
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00bc, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setAdditionalInputMethodSubtypes(java.lang.String r11, android.view.inputmethod.InputMethodSubtype[] r12) {
        /*
            r10 = this;
            boolean r0 = android.text.TextUtils.isEmpty(r11)
            if (r0 != 0) goto L_0x00ca
            if (r12 != 0) goto L_0x000a
            goto L_0x00ca
        L_0x000a:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            int r1 = r12.length
            r2 = 0
            r3 = r2
        L_0x0012:
            if (r3 >= r1) goto L_0x0049
            r4 = r12[r3]
            boolean r5 = r0.contains(r4)
            if (r5 != 0) goto L_0x0020
            r0.add(r4)
            goto L_0x0046
        L_0x0020:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Duplicated subtype definition found: "
            r5.append(r6)
            java.lang.String r6 = r4.getLocale()
            r5.append(r6)
            java.lang.String r6 = ", "
            r5.append(r6)
            java.lang.String r6 = r4.getMode()
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            java.lang.String r6 = "InputMethodManagerService"
            android.util.Slog.w(r6, r5)
        L_0x0046:
            int r3 = r3 + 1
            goto L_0x0012
        L_0x0049:
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r1 = r10.mMethodMap
            monitor-enter(r1)
            boolean r3 = r10.calledFromValidUserLocked()     // Catch:{ all -> 0x00c7 }
            if (r3 != 0) goto L_0x0054
            monitor-exit(r1)     // Catch:{ all -> 0x00c7 }
            return
        L_0x0054:
            boolean r3 = r10.mSystemReady     // Catch:{ all -> 0x00c7 }
            if (r3 != 0) goto L_0x005a
            monitor-exit(r1)     // Catch:{ all -> 0x00c7 }
            return
        L_0x005a:
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r3 = r10.mMethodMap     // Catch:{ all -> 0x00c7 }
            java.lang.Object r3 = r3.get(r11)     // Catch:{ all -> 0x00c7 }
            android.view.inputmethod.InputMethodInfo r3 = (android.view.inputmethod.InputMethodInfo) r3     // Catch:{ all -> 0x00c7 }
            if (r3 != 0) goto L_0x0066
            monitor-exit(r1)     // Catch:{ all -> 0x00c7 }
            return
        L_0x0066:
            android.content.pm.IPackageManager r4 = r10.mIPackageManager     // Catch:{ RemoteException -> 0x00bd }
            int r5 = android.os.Binder.getCallingUid()     // Catch:{ RemoteException -> 0x00bd }
            java.lang.String[] r4 = r4.getPackagesForUid(r5)     // Catch:{ RemoteException -> 0x00bd }
            if (r4 == 0) goto L_0x00bb
            int r5 = r4.length     // Catch:{ all -> 0x00c7 }
            r6 = 0
        L_0x0075:
            if (r6 >= r5) goto L_0x00bb
            r7 = r4[r6]     // Catch:{ all -> 0x00c7 }
            java.lang.String r8 = r3.getPackageName()     // Catch:{ all -> 0x00c7 }
            boolean r7 = r7.equals(r8)     // Catch:{ all -> 0x00c7 }
            if (r7 == 0) goto L_0x00b8
            int r7 = r12.length     // Catch:{ all -> 0x00c7 }
            if (r7 <= 0) goto L_0x0090
            android.util.ArrayMap<java.lang.String, java.util.List<android.view.inputmethod.InputMethodSubtype>> r7 = r10.mAdditionalSubtypeMap     // Catch:{ all -> 0x00c7 }
            java.lang.String r8 = r3.getId()     // Catch:{ all -> 0x00c7 }
            r7.put(r8, r0)     // Catch:{ all -> 0x00c7 }
            goto L_0x0099
        L_0x0090:
            android.util.ArrayMap<java.lang.String, java.util.List<android.view.inputmethod.InputMethodSubtype>> r7 = r10.mAdditionalSubtypeMap     // Catch:{ all -> 0x00c7 }
            java.lang.String r8 = r3.getId()     // Catch:{ all -> 0x00c7 }
            r7.remove(r8)     // Catch:{ all -> 0x00c7 }
        L_0x0099:
            android.util.ArrayMap<java.lang.String, java.util.List<android.view.inputmethod.InputMethodSubtype>> r7 = r10.mAdditionalSubtypeMap     // Catch:{ all -> 0x00c7 }
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r8 = r10.mMethodMap     // Catch:{ all -> 0x00c7 }
            com.android.server.inputmethod.InputMethodUtils$InputMethodSettings r9 = r10.mSettings     // Catch:{ all -> 0x00c7 }
            int r9 = r9.getCurrentUserId()     // Catch:{ all -> 0x00c7 }
            com.android.server.inputmethod.AdditionalSubtypeUtils.save(r7, r8, r9)     // Catch:{ all -> 0x00c7 }
            long r7 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x00c7 }
            r10.buildInputMethodListLocked(r2)     // Catch:{ all -> 0x00b3 }
            android.os.Binder.restoreCallingIdentity(r7)     // Catch:{ all -> 0x00c7 }
            monitor-exit(r1)     // Catch:{ all -> 0x00c7 }
            return
        L_0x00b3:
            r2 = move-exception
            android.os.Binder.restoreCallingIdentity(r7)     // Catch:{ all -> 0x00c7 }
            throw r2     // Catch:{ all -> 0x00c7 }
        L_0x00b8:
            int r6 = r6 + 1
            goto L_0x0075
        L_0x00bb:
            monitor-exit(r1)     // Catch:{ all -> 0x00c7 }
            return
        L_0x00bd:
            r2 = move-exception
            java.lang.String r4 = "InputMethodManagerService"
            java.lang.String r5 = "Failed to get package infos"
            android.util.Slog.e(r4, r5)     // Catch:{ all -> 0x00c7 }
            monitor-exit(r1)     // Catch:{ all -> 0x00c7 }
            return
        L_0x00c7:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x00c7 }
            throw r2
        L_0x00ca:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.setAdditionalInputMethodSubtypes(java.lang.String, android.view.inputmethod.InputMethodSubtype[]):void");
    }

    public int getInputMethodWindowVisibleHeight() {
        return this.mWindowManagerInternal.getInputMethodWindowVisibleHeight(this.mCurTokenDisplayId);
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0126, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void reportActivityView(com.android.internal.view.IInputMethodClient r13, int r14, float[] r15) {
        /*
            r12 = this;
            android.hardware.display.DisplayManagerInternal r0 = r12.mDisplayManagerInternal
            android.view.DisplayInfo r0 = r0.getDisplayInfo(r14)
            if (r0 == 0) goto L_0x0132
            int r1 = android.os.Binder.getCallingUid()
            int r2 = r0.ownerUid
            if (r1 != r2) goto L_0x012a
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r2 = r12.mMethodMap
            monitor-enter(r2)
            android.util.ArrayMap<android.os.IBinder, com.android.server.inputmethod.InputMethodManagerService$ClientState> r3 = r12.mClients     // Catch:{ all -> 0x0127 }
            android.os.IBinder r4 = r13.asBinder()     // Catch:{ all -> 0x0127 }
            java.lang.Object r3 = r3.get(r4)     // Catch:{ all -> 0x0127 }
            com.android.server.inputmethod.InputMethodManagerService$ClientState r3 = (com.android.server.inputmethod.InputMethodManagerService.ClientState) r3     // Catch:{ all -> 0x0127 }
            if (r3 != 0) goto L_0x0023
            monitor-exit(r2)     // Catch:{ all -> 0x0127 }
            return
        L_0x0023:
            if (r15 != 0) goto L_0x0055
            android.util.SparseArray<com.android.server.inputmethod.InputMethodManagerService$ActivityViewInfo> r4 = r12.mActivityViewDisplayIdToParentMap     // Catch:{ all -> 0x0127 }
            java.lang.Object r4 = r4.get(r14)     // Catch:{ all -> 0x0127 }
            com.android.server.inputmethod.InputMethodManagerService$ActivityViewInfo r4 = (com.android.server.inputmethod.InputMethodManagerService.ActivityViewInfo) r4     // Catch:{ all -> 0x0127 }
            if (r4 != 0) goto L_0x0031
            monitor-exit(r2)     // Catch:{ all -> 0x0127 }
            return
        L_0x0031:
            com.android.server.inputmethod.InputMethodManagerService$ClientState r5 = r4.mParentClient     // Catch:{ all -> 0x0127 }
            if (r5 != r3) goto L_0x003e
            android.util.SparseArray<com.android.server.inputmethod.InputMethodManagerService$ActivityViewInfo> r5 = r12.mActivityViewDisplayIdToParentMap     // Catch:{ all -> 0x0127 }
            r5.remove(r14)     // Catch:{ all -> 0x0127 }
            monitor-exit(r2)     // Catch:{ all -> 0x0127 }
            return
        L_0x003e:
            java.lang.SecurityException r5 = new java.lang.SecurityException     // Catch:{ all -> 0x0127 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0127 }
            r6.<init>()     // Catch:{ all -> 0x0127 }
            java.lang.String r7 = "Only the owner client can clear ActivityViewGeometry for display #"
            r6.append(r7)     // Catch:{ all -> 0x0127 }
            r6.append(r14)     // Catch:{ all -> 0x0127 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0127 }
            r5.<init>(r6)     // Catch:{ all -> 0x0127 }
            throw r5     // Catch:{ all -> 0x0127 }
        L_0x0055:
            android.util.SparseArray<com.android.server.inputmethod.InputMethodManagerService$ActivityViewInfo> r4 = r12.mActivityViewDisplayIdToParentMap     // Catch:{ all -> 0x0127 }
            java.lang.Object r4 = r4.get(r14)     // Catch:{ all -> 0x0127 }
            com.android.server.inputmethod.InputMethodManagerService$ActivityViewInfo r4 = (com.android.server.inputmethod.InputMethodManagerService.ActivityViewInfo) r4     // Catch:{ all -> 0x0127 }
            if (r4 == 0) goto L_0x0089
            com.android.server.inputmethod.InputMethodManagerService$ClientState r5 = r4.mParentClient     // Catch:{ all -> 0x0127 }
            if (r5 != r3) goto L_0x0066
            goto L_0x0089
        L_0x0066:
            java.security.InvalidParameterException r5 = new java.security.InvalidParameterException     // Catch:{ all -> 0x0127 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0127 }
            r6.<init>()     // Catch:{ all -> 0x0127 }
            java.lang.String r7 = "Display #"
            r6.append(r7)     // Catch:{ all -> 0x0127 }
            r6.append(r14)     // Catch:{ all -> 0x0127 }
            java.lang.String r7 = " is already registered by "
            r6.append(r7)     // Catch:{ all -> 0x0127 }
            com.android.server.inputmethod.InputMethodManagerService$ClientState r7 = r4.mParentClient     // Catch:{ all -> 0x0127 }
            r6.append(r7)     // Catch:{ all -> 0x0127 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0127 }
            r5.<init>(r6)     // Catch:{ all -> 0x0127 }
            throw r5     // Catch:{ all -> 0x0127 }
        L_0x0089:
            if (r4 != 0) goto L_0x00c0
            com.android.server.wm.WindowManagerInternal r5 = r12.mWindowManagerInternal     // Catch:{ all -> 0x0127 }
            int r6 = r3.uid     // Catch:{ all -> 0x0127 }
            boolean r5 = r5.isUidAllowedOnDisplay(r14, r6)     // Catch:{ all -> 0x0127 }
            if (r5 == 0) goto L_0x00a6
            com.android.server.inputmethod.InputMethodManagerService$ActivityViewInfo r5 = new com.android.server.inputmethod.InputMethodManagerService$ActivityViewInfo     // Catch:{ all -> 0x0127 }
            android.graphics.Matrix r6 = new android.graphics.Matrix     // Catch:{ all -> 0x0127 }
            r6.<init>()     // Catch:{ all -> 0x0127 }
            r5.<init>(r3, r6)     // Catch:{ all -> 0x0127 }
            r4 = r5
            android.util.SparseArray<com.android.server.inputmethod.InputMethodManagerService$ActivityViewInfo> r5 = r12.mActivityViewDisplayIdToParentMap     // Catch:{ all -> 0x0127 }
            r5.put(r14, r4)     // Catch:{ all -> 0x0127 }
            goto L_0x00c0
        L_0x00a6:
            java.lang.SecurityException r5 = new java.lang.SecurityException     // Catch:{ all -> 0x0127 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0127 }
            r6.<init>()     // Catch:{ all -> 0x0127 }
            r6.append(r3)     // Catch:{ all -> 0x0127 }
            java.lang.String r7 = " cannot access to display #"
            r6.append(r7)     // Catch:{ all -> 0x0127 }
            r6.append(r14)     // Catch:{ all -> 0x0127 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0127 }
            r5.<init>(r6)     // Catch:{ all -> 0x0127 }
            throw r5     // Catch:{ all -> 0x0127 }
        L_0x00c0:
            android.graphics.Matrix r5 = r4.mMatrix     // Catch:{ all -> 0x0127 }
            r5.setValues(r15)     // Catch:{ all -> 0x0127 }
            com.android.server.inputmethod.InputMethodManagerService$ClientState r5 = r12.mCurClient     // Catch:{ all -> 0x0127 }
            if (r5 == 0) goto L_0x0125
            com.android.server.inputmethod.InputMethodManagerService$ClientState r5 = r12.mCurClient     // Catch:{ all -> 0x0127 }
            com.android.server.inputmethod.InputMethodManagerService$SessionState r5 = r5.curSession     // Catch:{ all -> 0x0127 }
            if (r5 != 0) goto L_0x00d2
            goto L_0x0125
        L_0x00d2:
            r5 = 0
            com.android.server.inputmethod.InputMethodManagerService$ClientState r6 = r12.mCurClient     // Catch:{ all -> 0x0127 }
            int r6 = r6.selfReportedDisplayId     // Catch:{ all -> 0x0127 }
            r7 = 0
        L_0x00d8:
            if (r6 != r14) goto L_0x00dc
            r8 = 1
            goto L_0x00dd
        L_0x00dc:
            r8 = 0
        L_0x00dd:
            r7 = r7 | r8
            android.util.SparseArray<com.android.server.inputmethod.InputMethodManagerService$ActivityViewInfo> r8 = r12.mActivityViewDisplayIdToParentMap     // Catch:{ all -> 0x0127 }
            java.lang.Object r8 = r8.get(r6)     // Catch:{ all -> 0x0127 }
            com.android.server.inputmethod.InputMethodManagerService$ActivityViewInfo r8 = (com.android.server.inputmethod.InputMethodManagerService.ActivityViewInfo) r8     // Catch:{ all -> 0x0127 }
            if (r8 != 0) goto L_0x00e9
            goto L_0x011b
        L_0x00e9:
            if (r5 != 0) goto L_0x00f6
            android.graphics.Matrix r9 = new android.graphics.Matrix     // Catch:{ all -> 0x0127 }
            android.graphics.Matrix r10 = r8.mMatrix     // Catch:{ all -> 0x0127 }
            r9.<init>(r10)     // Catch:{ all -> 0x0127 }
            r5 = r9
            goto L_0x00fd
        L_0x00f6:
            android.graphics.Matrix r9 = r8.mMatrix     // Catch:{ all -> 0x0127 }
            r5.postConcat(r9)     // Catch:{ all -> 0x0127 }
        L_0x00fd:
            com.android.server.inputmethod.InputMethodManagerService$ClientState r9 = r8.mParentClient     // Catch:{ all -> 0x0127 }
            int r9 = r9.selfReportedDisplayId     // Catch:{ all -> 0x0127 }
            int r10 = r12.mCurTokenDisplayId     // Catch:{ all -> 0x0127 }
            if (r9 != r10) goto L_0x011d
            if (r7 == 0) goto L_0x011b
            r9 = 9
            float[] r9 = new float[r9]     // Catch:{ all -> 0x0127 }
            r5.getValues(r9)     // Catch:{ all -> 0x0127 }
            com.android.server.inputmethod.InputMethodManagerService$ClientState r10 = r12.mCurClient     // Catch:{ RemoteException -> 0x011a }
            com.android.internal.view.IInputMethodClient r10 = r10.client     // Catch:{ RemoteException -> 0x011a }
            int r11 = r12.mCurSeq     // Catch:{ RemoteException -> 0x011a }
            r10.updateActivityViewToScreenMatrix(r11, r9)     // Catch:{ RemoteException -> 0x011a }
            goto L_0x011b
        L_0x011a:
            r10 = move-exception
        L_0x011b:
            monitor-exit(r2)     // Catch:{ all -> 0x0127 }
            return
        L_0x011d:
            com.android.server.inputmethod.InputMethodManagerService$ClientState r9 = r4.mParentClient     // Catch:{ all -> 0x0127 }
            int r9 = r9.selfReportedDisplayId     // Catch:{ all -> 0x0127 }
            r6 = r9
            goto L_0x00d8
        L_0x0125:
            monitor-exit(r2)     // Catch:{ all -> 0x0127 }
            return
        L_0x0127:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0127 }
            throw r3
        L_0x012a:
            java.lang.SecurityException r2 = new java.lang.SecurityException
            java.lang.String r3 = "The caller doesn't own the display."
            r2.<init>(r3)
            throw r2
        L_0x0132:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Cannot find display for non-existent displayId: "
            r2.append(r3)
            r2.append(r14)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.reportActivityView(com.android.internal.view.IInputMethodClient, int, float[]):void");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void notifyUserAction(android.os.IBinder r5) {
        /*
            r4 = this;
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r0 = r4.mMethodMap
            monitor-enter(r0)
            android.os.IBinder r1 = r4.mCurToken     // Catch:{ all -> 0x001e }
            if (r1 == r5) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x001e }
            return
        L_0x0009:
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r1 = r4.mMethodMap     // Catch:{ all -> 0x001e }
            java.lang.String r2 = r4.mCurMethodId     // Catch:{ all -> 0x001e }
            java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x001e }
            android.view.inputmethod.InputMethodInfo r1 = (android.view.inputmethod.InputMethodInfo) r1     // Catch:{ all -> 0x001e }
            if (r1 == 0) goto L_0x001c
            com.android.server.inputmethod.InputMethodSubtypeSwitchingController r2 = r4.mSwitchingController     // Catch:{ all -> 0x001e }
            android.view.inputmethod.InputMethodSubtype r3 = r4.mCurrentSubtype     // Catch:{ all -> 0x001e }
            r2.onUserActionLocked(r1, r3)     // Catch:{ all -> 0x001e }
        L_0x001c:
            monitor-exit(r0)     // Catch:{ all -> 0x001e }
            return
        L_0x001e:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x001e }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.notifyUserAction(android.os.IBinder):void");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0027, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void reportPreRendered(android.os.IBinder r6, android.view.inputmethod.EditorInfo r7) {
        /*
            r5 = this;
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r0 = r5.mMethodMap
            monitor-enter(r0)
            boolean r1 = r5.calledWithValidTokenLocked(r6)     // Catch:{ all -> 0x0028 }
            if (r1 != 0) goto L_0x000b
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            return
        L_0x000b:
            com.android.server.inputmethod.InputMethodManagerService$ClientState r1 = r5.mCurClient     // Catch:{ all -> 0x0028 }
            if (r1 == 0) goto L_0x0026
            com.android.server.inputmethod.InputMethodManagerService$ClientState r1 = r5.mCurClient     // Catch:{ all -> 0x0028 }
            com.android.internal.view.IInputMethodClient r1 = r1.client     // Catch:{ all -> 0x0028 }
            if (r1 == 0) goto L_0x0026
            com.android.server.inputmethod.InputMethodManagerService$ClientState r1 = r5.mCurClient     // Catch:{ all -> 0x0028 }
            com.android.internal.view.IInputMethodClient r1 = r1.client     // Catch:{ all -> 0x0028 }
            com.android.internal.os.HandlerCaller r2 = r5.mCaller     // Catch:{ all -> 0x0028 }
            r3 = 3060(0xbf4, float:4.288E-42)
            com.android.server.inputmethod.InputMethodManagerService$ClientState r4 = r5.mCurClient     // Catch:{ all -> 0x0028 }
            android.os.Message r2 = r2.obtainMessageOO(r3, r7, r4)     // Catch:{ all -> 0x0028 }
            r5.executeOrSendMessage(r1, r2)     // Catch:{ all -> 0x0028 }
        L_0x0026:
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            return
        L_0x0028:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.reportPreRendered(android.os.IBinder, android.view.inputmethod.EditorInfo):void");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x002c, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void applyImeVisibility(android.os.IBinder r7, boolean r8) {
        /*
            r6 = this;
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r0 = r6.mMethodMap
            monitor-enter(r0)
            boolean r1 = r6.calledWithValidTokenLocked(r7)     // Catch:{ all -> 0x002d }
            if (r1 != 0) goto L_0x000b
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            return
        L_0x000b:
            com.android.server.inputmethod.InputMethodManagerService$ClientState r1 = r6.mCurClient     // Catch:{ all -> 0x002d }
            if (r1 == 0) goto L_0x002b
            com.android.server.inputmethod.InputMethodManagerService$ClientState r1 = r6.mCurClient     // Catch:{ all -> 0x002d }
            com.android.internal.view.IInputMethodClient r1 = r1.client     // Catch:{ all -> 0x002d }
            if (r1 == 0) goto L_0x002b
            com.android.server.inputmethod.InputMethodManagerService$ClientState r1 = r6.mCurClient     // Catch:{ all -> 0x002d }
            com.android.internal.view.IInputMethodClient r1 = r1.client     // Catch:{ all -> 0x002d }
            com.android.internal.os.HandlerCaller r2 = r6.mCaller     // Catch:{ all -> 0x002d }
            r3 = 3070(0xbfe, float:4.302E-42)
            if (r8 == 0) goto L_0x0021
            r4 = 1
            goto L_0x0022
        L_0x0021:
            r4 = 0
        L_0x0022:
            com.android.server.inputmethod.InputMethodManagerService$ClientState r5 = r6.mCurClient     // Catch:{ all -> 0x002d }
            android.os.Message r2 = r2.obtainMessageIO(r3, r4, r5)     // Catch:{ all -> 0x002d }
            r6.executeOrSendMessage(r1, r2)     // Catch:{ all -> 0x002d }
        L_0x002b:
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            return
        L_0x002d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.applyImeVisibility(android.os.IBinder, boolean):void");
    }

    private void setInputMethodWithSubtypeIdLocked(IBinder token, String id, int subtypeId) {
        if (token == null) {
            if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != 0) {
                throw new SecurityException("Using null token requires permission android.permission.WRITE_SECURE_SETTINGS");
            }
        } else if (this.mCurToken != token) {
            Slog.w(TAG, "Ignoring setInputMethod of uid " + Binder.getCallingUid() + " token: " + token);
            return;
        }
        long ident = Binder.clearCallingIdentity();
        try {
            setInputMethodLocked(id, subtypeId);
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: private */
    public void hideMySoftInput(IBinder token, int flags) {
        synchronized (this.mMethodMap) {
            if (calledWithValidTokenLocked(token)) {
                long ident = Binder.clearCallingIdentity();
                try {
                    hideCurrentInputLocked(flags, (ResultReceiver) null);
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: private */
    public void showMySoftInput(IBinder token, int flags) {
        synchronized (this.mMethodMap) {
            if (calledWithValidTokenLocked(token)) {
                long ident = Binder.clearCallingIdentity();
                try {
                    showCurrentInputLocked(flags, (ResultReceiver) null);
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setEnabledSessionInMainThread(SessionState session) {
        SessionState sessionState = this.mEnabledSession;
        if (sessionState != session) {
            if (!(sessionState == null || sessionState.session == null)) {
                try {
                    this.mEnabledSession.method.setSessionEnabled(this.mEnabledSession.session, false);
                } catch (RemoteException e) {
                }
            }
            this.mEnabledSession = session;
            SessionState sessionState2 = this.mEnabledSession;
            if (sessionState2 != null && sessionState2.session != null) {
                try {
                    this.mEnabledSession.method.setSessionEnabled(this.mEnabledSession.session, true);
                } catch (RemoteException e2) {
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:100:0x01dc, code lost:
        if (android.os.Binder.isProxy(r1) != false) goto L_0x01f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x01f3, code lost:
        if (android.os.Binder.isProxy(r1) != false) goto L_0x01f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:111:0x01f5, code lost:
        r2.dispose();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x013c, code lost:
        if (android.os.Binder.isProxy(r1) != false) goto L_0x013e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x013e, code lost:
        r2.channel.dispose();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0168, code lost:
        if (android.os.Binder.isProxy(r1) != false) goto L_0x013e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean handleMessage(android.os.Message r15) {
        /*
            r14 = this;
            int r0 = r15.what
            r1 = 2
            r2 = 0
            r3 = 1
            if (r0 == r3) goto L_0x027b
            if (r0 == r1) goto L_0x0273
            r1 = 3
            if (r0 == r1) goto L_0x026f
            switch(r0) {
                case 1000: goto L_0x0265;
                case 1010: goto L_0x0250;
                case 1020: goto L_0x0239;
                case 1030: goto L_0x0224;
                case 1035: goto L_0x0218;
                case 1040: goto L_0x01fc;
                case 1050: goto L_0x01c3;
                case 2000: goto L_0x018d;
                case 3000: goto L_0x017f;
                case 3010: goto L_0x0125;
                case 3020: goto L_0x00e3;
                case 3030: goto L_0x00da;
                case 3045: goto L_0x009e;
                case 3060: goto L_0x005d;
                case 3070: goto L_0x0021;
                case 4000: goto L_0x0016;
                case 5000: goto L_0x0010;
                default: goto L_0x000f;
            }
        L_0x000f:
            return r2
        L_0x0010:
            int r0 = r15.arg1
            r14.onUnlockUser(r0)
            return r3
        L_0x0016:
            com.android.server.inputmethod.InputMethodManagerService$HardKeyboardListener r0 = r14.mHardKeyboardListener
            int r1 = r15.arg1
            if (r1 != r3) goto L_0x001d
            r2 = r3
        L_0x001d:
            r0.handleHardKeyboardStatusChange(r2)
            return r3
        L_0x0021:
            int r0 = r15.arg1
            if (r0 == 0) goto L_0x0026
            r2 = r3
        L_0x0026:
            r0 = r2
            java.lang.Object r1 = r15.obj
            com.android.server.inputmethod.InputMethodManagerService$ClientState r1 = (com.android.server.inputmethod.InputMethodManagerService.ClientState) r1
            com.android.internal.view.IInputMethodClient r2 = r1.client     // Catch:{ RemoteException -> 0x0031 }
            r2.applyImeVisibility(r0)     // Catch:{ RemoteException -> 0x0031 }
            goto L_0x005c
        L_0x0031:
            r2 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Got RemoteException sending applyImeVisibility("
            r4.append(r5)
            r4.append(r0)
            java.lang.String r5 = ") notification to pid="
            r4.append(r5)
            int r5 = r1.pid
            r4.append(r5)
            java.lang.String r5 = " uid="
            r4.append(r5)
            int r5 = r1.uid
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            java.lang.String r5 = "InputMethodManagerService"
            android.util.Slog.w(r5, r4)
        L_0x005c:
            return r3
        L_0x005d:
            java.lang.Object r0 = r15.obj
            com.android.internal.os.SomeArgs r0 = (com.android.internal.os.SomeArgs) r0
            java.lang.Object r1 = r0.arg1
            android.view.inputmethod.EditorInfo r1 = (android.view.inputmethod.EditorInfo) r1
            java.lang.Object r2 = r0.arg2
            com.android.server.inputmethod.InputMethodManagerService$ClientState r2 = (com.android.server.inputmethod.InputMethodManagerService.ClientState) r2
            com.android.internal.view.IInputMethodClient r4 = r2.client     // Catch:{ RemoteException -> 0x006f }
            r4.reportPreRendered(r1)     // Catch:{ RemoteException -> 0x006f }
            goto L_0x009a
        L_0x006f:
            r4 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Got RemoteException sending reportPreRendered("
            r5.append(r6)
            r5.append(r1)
            java.lang.String r6 = ") notification to pid="
            r5.append(r6)
            int r6 = r2.pid
            r5.append(r6)
            java.lang.String r6 = " uid="
            r5.append(r6)
            int r6 = r2.uid
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            java.lang.String r6 = "InputMethodManagerService"
            android.util.Slog.w(r6, r5)
        L_0x009a:
            r0.recycle()
            return r3
        L_0x009e:
            int r0 = r15.arg1
            if (r0 == 0) goto L_0x00a3
            r2 = r3
        L_0x00a3:
            r0 = r2
            java.lang.Object r1 = r15.obj
            com.android.server.inputmethod.InputMethodManagerService$ClientState r1 = (com.android.server.inputmethod.InputMethodManagerService.ClientState) r1
            com.android.internal.view.IInputMethodClient r2 = r1.client     // Catch:{ RemoteException -> 0x00ae }
            r2.reportFullscreenMode(r0)     // Catch:{ RemoteException -> 0x00ae }
            goto L_0x00d9
        L_0x00ae:
            r2 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Got RemoteException sending reportFullscreen("
            r4.append(r5)
            r4.append(r0)
            java.lang.String r5 = ") notification to pid="
            r4.append(r5)
            int r5 = r1.pid
            r4.append(r5)
            java.lang.String r5 = " uid="
            r4.append(r5)
            int r5 = r1.uid
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            java.lang.String r5 = "InputMethodManagerService"
            android.util.Slog.w(r5, r4)
        L_0x00d9:
            return r3
        L_0x00da:
            int r0 = r15.arg1
            if (r0 == 0) goto L_0x00df
            r2 = r3
        L_0x00df:
            r14.handleSetInteractive(r2)
            return r3
        L_0x00e3:
            java.lang.Object r0 = r15.obj     // Catch:{ RemoteException -> 0x00f9 }
            com.android.server.inputmethod.InputMethodManagerService$ClientState r0 = (com.android.server.inputmethod.InputMethodManagerService.ClientState) r0     // Catch:{ RemoteException -> 0x00f9 }
            com.android.internal.view.IInputMethodClient r0 = r0.client     // Catch:{ RemoteException -> 0x00f9 }
            int r1 = r15.arg1     // Catch:{ RemoteException -> 0x00f9 }
            if (r1 == 0) goto L_0x00ef
            r1 = r3
            goto L_0x00f0
        L_0x00ef:
            r1 = r2
        L_0x00f0:
            int r4 = r15.arg2     // Catch:{ RemoteException -> 0x00f9 }
            if (r4 == 0) goto L_0x00f5
            r2 = r3
        L_0x00f5:
            r0.setActive(r1, r2)     // Catch:{ RemoteException -> 0x00f9 }
            goto L_0x0124
        L_0x00f9:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Got RemoteException sending setActive(false) notification to pid "
            r1.append(r2)
            java.lang.Object r2 = r15.obj
            com.android.server.inputmethod.InputMethodManagerService$ClientState r2 = (com.android.server.inputmethod.InputMethodManagerService.ClientState) r2
            int r2 = r2.pid
            r1.append(r2)
            java.lang.String r2 = " uid "
            r1.append(r2)
            java.lang.Object r2 = r15.obj
            com.android.server.inputmethod.InputMethodManagerService$ClientState r2 = (com.android.server.inputmethod.InputMethodManagerService.ClientState) r2
            int r2 = r2.uid
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "InputMethodManagerService"
            android.util.Slog.w(r2, r1)
        L_0x0124:
            return r3
        L_0x0125:
            java.lang.Object r0 = r15.obj
            com.android.internal.os.SomeArgs r0 = (com.android.internal.os.SomeArgs) r0
            java.lang.Object r1 = r0.arg1
            com.android.internal.view.IInputMethodClient r1 = (com.android.internal.view.IInputMethodClient) r1
            java.lang.Object r2 = r0.arg2
            com.android.internal.view.InputBindResult r2 = (com.android.internal.view.InputBindResult) r2
            r1.onBindMethod(r2)     // Catch:{ RemoteException -> 0x0146 }
            android.view.InputChannel r4 = r2.channel
            if (r4 == 0) goto L_0x016b
            boolean r4 = android.os.Binder.isProxy(r1)
            if (r4 == 0) goto L_0x016b
        L_0x013e:
            android.view.InputChannel r4 = r2.channel
            r4.dispose()
            goto L_0x016b
        L_0x0144:
            r3 = move-exception
            goto L_0x016f
        L_0x0146:
            r4 = move-exception
            java.lang.String r5 = "InputMethodManagerService"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0144 }
            r6.<init>()     // Catch:{ all -> 0x0144 }
            java.lang.String r7 = "Client died receiving input method "
            r6.append(r7)     // Catch:{ all -> 0x0144 }
            java.lang.Object r7 = r0.arg2     // Catch:{ all -> 0x0144 }
            r6.append(r7)     // Catch:{ all -> 0x0144 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0144 }
            android.util.Slog.w(r5, r6)     // Catch:{ all -> 0x0144 }
            android.view.InputChannel r4 = r2.channel
            if (r4 == 0) goto L_0x016b
            boolean r4 = android.os.Binder.isProxy(r1)
            if (r4 == 0) goto L_0x016b
            goto L_0x013e
        L_0x016b:
            r0.recycle()
            return r3
        L_0x016f:
            android.view.InputChannel r4 = r2.channel
            if (r4 == 0) goto L_0x017e
            boolean r4 = android.os.Binder.isProxy(r1)
            if (r4 == 0) goto L_0x017e
            android.view.InputChannel r4 = r2.channel
            r4.dispose()
        L_0x017e:
            throw r3
        L_0x017f:
            java.lang.Object r0 = r15.obj     // Catch:{ RemoteException -> 0x018b }
            com.android.internal.view.IInputMethodClient r0 = (com.android.internal.view.IInputMethodClient) r0     // Catch:{ RemoteException -> 0x018b }
            int r1 = r15.arg1     // Catch:{ RemoteException -> 0x018b }
            int r2 = r15.arg2     // Catch:{ RemoteException -> 0x018b }
            r0.onUnbindMethod(r1, r2)     // Catch:{ RemoteException -> 0x018b }
            goto L_0x018c
        L_0x018b:
            r0 = move-exception
        L_0x018c:
            return r3
        L_0x018d:
            int r0 = r15.arg1
            int r1 = r15.arg2
            if (r1 == 0) goto L_0x0195
            r9 = r3
            goto L_0x0196
        L_0x0195:
            r9 = r2
        L_0x0196:
            java.lang.Object r1 = r15.obj
            com.android.internal.os.SomeArgs r1 = (com.android.internal.os.SomeArgs) r1
            java.lang.Object r2 = r1.arg1
            android.os.IBinder r2 = (android.os.IBinder) r2
            java.lang.Object r4 = r1.arg2
            r11 = r4
            com.android.server.inputmethod.InputMethodManagerService$SessionState r11 = (com.android.server.inputmethod.InputMethodManagerService.SessionState) r11
            java.lang.Object r4 = r1.arg3
            r12 = r4
            com.android.internal.view.IInputContext r12 = (com.android.internal.view.IInputContext) r12
            java.lang.Object r4 = r1.arg4
            r13 = r4
            android.view.inputmethod.EditorInfo r13 = (android.view.inputmethod.EditorInfo) r13
            r14.setEnabledSessionInMainThread(r11)     // Catch:{ RemoteException -> 0x01be }
            com.android.internal.view.IInputMethod r4 = r11.method     // Catch:{ RemoteException -> 0x01be }
            com.android.server.inputmethod.InputMethodManagerService$ClientState r5 = r11.client     // Catch:{ RemoteException -> 0x01be }
            boolean r10 = r5.shouldPreRenderIme     // Catch:{ RemoteException -> 0x01be }
            r5 = r2
            r6 = r12
            r7 = r0
            r8 = r13
            r4.startInput(r5, r6, r7, r8, r9, r10)     // Catch:{ RemoteException -> 0x01be }
            goto L_0x01bf
        L_0x01be:
            r4 = move-exception
        L_0x01bf:
            r1.recycle()
            return r3
        L_0x01c3:
            java.lang.Object r0 = r15.obj
            com.android.internal.os.SomeArgs r0 = (com.android.internal.os.SomeArgs) r0
            java.lang.Object r1 = r0.arg1
            com.android.internal.view.IInputMethod r1 = (com.android.internal.view.IInputMethod) r1
            java.lang.Object r2 = r0.arg2
            android.view.InputChannel r2 = (android.view.InputChannel) r2
            java.lang.Object r4 = r0.arg3     // Catch:{ RemoteException -> 0x01ec, all -> 0x01df }
            com.android.internal.view.IInputSessionCallback r4 = (com.android.internal.view.IInputSessionCallback) r4     // Catch:{ RemoteException -> 0x01ec, all -> 0x01df }
            r1.createSession(r2, r4)     // Catch:{ RemoteException -> 0x01ec, all -> 0x01df }
            if (r2 == 0) goto L_0x01f8
            boolean r4 = android.os.Binder.isProxy(r1)
            if (r4 == 0) goto L_0x01f8
            goto L_0x01f5
        L_0x01df:
            r3 = move-exception
            if (r2 == 0) goto L_0x01eb
            boolean r4 = android.os.Binder.isProxy(r1)
            if (r4 == 0) goto L_0x01eb
            r2.dispose()
        L_0x01eb:
            throw r3
        L_0x01ec:
            r4 = move-exception
            if (r2 == 0) goto L_0x01f8
            boolean r4 = android.os.Binder.isProxy(r1)
            if (r4 == 0) goto L_0x01f8
        L_0x01f5:
            r2.dispose()
        L_0x01f8:
            r0.recycle()
            return r3
        L_0x01fc:
            java.lang.Object r0 = r15.obj
            com.android.internal.os.SomeArgs r0 = (com.android.internal.os.SomeArgs) r0
            java.lang.Object r1 = r0.arg2     // Catch:{ RemoteException -> 0x0213 }
            android.os.IBinder r1 = (android.os.IBinder) r1     // Catch:{ RemoteException -> 0x0213 }
            java.lang.Object r2 = r0.arg1     // Catch:{ RemoteException -> 0x0213 }
            com.android.internal.view.IInputMethod r2 = (com.android.internal.view.IInputMethod) r2     // Catch:{ RemoteException -> 0x0213 }
            int r4 = r15.arg1     // Catch:{ RemoteException -> 0x0213 }
            com.android.server.inputmethod.InputMethodManagerService$InputMethodPrivilegedOperationsImpl r5 = new com.android.server.inputmethod.InputMethodManagerService$InputMethodPrivilegedOperationsImpl     // Catch:{ RemoteException -> 0x0213 }
            r5.<init>(r14, r1)     // Catch:{ RemoteException -> 0x0213 }
            r2.initializeInternal(r1, r4, r5)     // Catch:{ RemoteException -> 0x0213 }
            goto L_0x0214
        L_0x0213:
            r1 = move-exception
        L_0x0214:
            r0.recycle()
            return r3
        L_0x0218:
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r0 = r14.mMethodMap
            monitor-enter(r0)
            r1 = 0
            r14.hideCurrentInputLocked(r2, r1)     // Catch:{ all -> 0x0221 }
            monitor-exit(r0)     // Catch:{ all -> 0x0221 }
            return r3
        L_0x0221:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0221 }
            throw r1
        L_0x0224:
            java.lang.Object r0 = r15.obj
            com.android.internal.os.SomeArgs r0 = (com.android.internal.os.SomeArgs) r0
            java.lang.Object r1 = r0.arg1     // Catch:{ RemoteException -> 0x0234 }
            com.android.internal.view.IInputMethod r1 = (com.android.internal.view.IInputMethod) r1     // Catch:{ RemoteException -> 0x0234 }
            java.lang.Object r4 = r0.arg2     // Catch:{ RemoteException -> 0x0234 }
            android.os.ResultReceiver r4 = (android.os.ResultReceiver) r4     // Catch:{ RemoteException -> 0x0234 }
            r1.hideSoftInput(r2, r4)     // Catch:{ RemoteException -> 0x0234 }
            goto L_0x0235
        L_0x0234:
            r1 = move-exception
        L_0x0235:
            r0.recycle()
            return r3
        L_0x0239:
            java.lang.Object r0 = r15.obj
            com.android.internal.os.SomeArgs r0 = (com.android.internal.os.SomeArgs) r0
            java.lang.Object r1 = r0.arg1     // Catch:{ RemoteException -> 0x024b }
            com.android.internal.view.IInputMethod r1 = (com.android.internal.view.IInputMethod) r1     // Catch:{ RemoteException -> 0x024b }
            int r2 = r15.arg1     // Catch:{ RemoteException -> 0x024b }
            java.lang.Object r4 = r0.arg2     // Catch:{ RemoteException -> 0x024b }
            android.os.ResultReceiver r4 = (android.os.ResultReceiver) r4     // Catch:{ RemoteException -> 0x024b }
            r1.showSoftInput(r2, r4)     // Catch:{ RemoteException -> 0x024b }
            goto L_0x024c
        L_0x024b:
            r1 = move-exception
        L_0x024c:
            r0.recycle()
            return r3
        L_0x0250:
            java.lang.Object r0 = r15.obj
            com.android.internal.os.SomeArgs r0 = (com.android.internal.os.SomeArgs) r0
            java.lang.Object r1 = r0.arg1     // Catch:{ RemoteException -> 0x0260 }
            com.android.internal.view.IInputMethod r1 = (com.android.internal.view.IInputMethod) r1     // Catch:{ RemoteException -> 0x0260 }
            java.lang.Object r2 = r0.arg2     // Catch:{ RemoteException -> 0x0260 }
            android.view.inputmethod.InputBinding r2 = (android.view.inputmethod.InputBinding) r2     // Catch:{ RemoteException -> 0x0260 }
            r1.bindInput(r2)     // Catch:{ RemoteException -> 0x0260 }
            goto L_0x0261
        L_0x0260:
            r1 = move-exception
        L_0x0261:
            r0.recycle()
            return r3
        L_0x0265:
            java.lang.Object r0 = r15.obj     // Catch:{ RemoteException -> 0x026d }
            com.android.internal.view.IInputMethod r0 = (com.android.internal.view.IInputMethod) r0     // Catch:{ RemoteException -> 0x026d }
            r0.unbindInput()     // Catch:{ RemoteException -> 0x026d }
            goto L_0x026e
        L_0x026d:
            r0 = move-exception
        L_0x026e:
            return r3
        L_0x026f:
            r14.showConfigureInputMethods()
            return r3
        L_0x0273:
            java.lang.Object r0 = r15.obj
            java.lang.String r0 = (java.lang.String) r0
            r14.showInputMethodAndSubtypeEnabler(r0)
            return r3
        L_0x027b:
            int r0 = r15.arg2
            int r4 = r15.arg1
            if (r4 == 0) goto L_0x02a2
            if (r4 == r3) goto L_0x02a0
            if (r4 == r1) goto L_0x029e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "Unknown subtype picker mode = "
            r1.append(r3)
            int r3 = r15.arg1
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            java.lang.String r3 = "InputMethodManagerService"
            android.util.Slog.e(r3, r1)
            return r2
        L_0x029e:
            r1 = 0
            goto L_0x02a5
        L_0x02a0:
            r1 = 1
            goto L_0x02a5
        L_0x02a2:
            boolean r1 = r14.mInputShown
        L_0x02a5:
            r14.showInputMethodMenu(r1, r0)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.handleMessage(android.os.Message):boolean");
    }

    private void handleSetInteractive(boolean interactive) {
        synchronized (this.mMethodMap) {
            this.mIsInteractive = interactive;
            int i = 0;
            updateSystemUiLocked(interactive ? this.mImeWindowVis : 0, this.mBackDisposition);
            if (!(this.mCurClient == null || this.mCurClient.client == null)) {
                IInputMethodClient iInputMethodClient = this.mCurClient.client;
                HandlerCaller handlerCaller = this.mCaller;
                int i2 = this.mIsInteractive ? 1 : 0;
                if (this.mInFullscreenMode) {
                    i = 1;
                }
                executeOrSendMessage(iInputMethodClient, handlerCaller.obtainMessageIIO(MSG_SET_ACTIVE, i2, i, this.mCurClient));
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean chooseNewDefaultIMELocked() {
        InputMethodInfo imi = InputMethodUtils.getMostApplicableDefaultIME(this.mSettings.getEnabledInputMethodListLocked());
        if (imi == null) {
            return false;
        }
        resetSelectedInputMethodAndSubtypeLocked(imi.getId());
        return true;
    }

    static void queryInputMethodServicesInternal(Context context, int userId, ArrayMap<String, List<InputMethodSubtype>> additionalSubtypeMap, ArrayMap<String, InputMethodInfo> methodMap, ArrayList<InputMethodInfo> methodList) {
        methodList.clear();
        methodMap.clear();
        List<ResolveInfo> services2 = context.getPackageManager().queryIntentServicesAsUser(new Intent("android.view.InputMethod"), 32896, userId);
        methodList.ensureCapacity(services2.size());
        methodMap.ensureCapacity(services2.size());
        for (int i = 0; i < services2.size(); i++) {
            ResolveInfo ri = services2.get(i);
            ServiceInfo si = ri.serviceInfo;
            String imeId = InputMethodInfo.computeId(ri);
            if (!"android.permission.BIND_INPUT_METHOD".equals(si.permission)) {
                Slog.w(TAG, "Skipping input method " + imeId + ": it does not require the permission " + "android.permission.BIND_INPUT_METHOD");
            } else {
                try {
                    InputMethodInfo imi = new InputMethodInfo(context, ri, additionalSubtypeMap.get(imeId));
                    if (!imi.isVrOnly()) {
                        methodList.add(imi);
                        methodMap.put(imi.getId(), imi);
                    }
                } catch (Exception e) {
                    Slog.wtf(TAG, "Unable to load input method " + imeId, e);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mMethodMap"})
    public void buildInputMethodListLocked(boolean resetDefaultEnabledIme) {
        if (!this.mSystemReady) {
            Slog.e(TAG, "buildInputMethodListLocked is not allowed until system is ready");
            return;
        }
        this.mMethodMapUpdateCount++;
        this.mMyPackageMonitor.clearKnownImePackageNamesLocked();
        queryInputMethodServicesInternal(this.mContext, this.mSettings.getCurrentUserId(), this.mAdditionalSubtypeMap, this.mMethodMap, this.mMethodList);
        List<ResolveInfo> allInputMethodServices = this.mContext.getPackageManager().queryIntentServicesAsUser(new Intent("android.view.InputMethod"), 512, this.mSettings.getCurrentUserId());
        int N = allInputMethodServices.size();
        for (int i = 0; i < N; i++) {
            ServiceInfo si = allInputMethodServices.get(i).serviceInfo;
            if ("android.permission.BIND_INPUT_METHOD".equals(si.permission)) {
                this.mMyPackageMonitor.addKnownImePackageNameLocked(si.packageName);
            }
        }
        boolean reenableMinimumNonAuxSystemImes = false;
        if (!resetDefaultEnabledIme) {
            boolean enabledImeFound = false;
            boolean enabledNonAuxImeFound = false;
            List<InputMethodInfo> enabledImes = this.mSettings.getEnabledInputMethodListLocked();
            int N2 = enabledImes.size();
            int i2 = 0;
            while (true) {
                if (i2 >= N2) {
                    break;
                }
                InputMethodInfo imi = enabledImes.get(i2);
                if (this.mMethodList.contains(imi)) {
                    enabledImeFound = true;
                    if (!imi.isAuxiliaryIme()) {
                        enabledNonAuxImeFound = true;
                        break;
                    }
                }
                i2++;
            }
            if (!enabledImeFound) {
                resetDefaultEnabledIme = true;
                resetSelectedInputMethodAndSubtypeLocked("");
            } else if (!enabledNonAuxImeFound) {
                reenableMinimumNonAuxSystemImes = true;
            }
        }
        if (resetDefaultEnabledIme || reenableMinimumNonAuxSystemImes) {
            ArrayList<InputMethodInfo> defaultEnabledIme = InputMethodUtils.getDefaultEnabledImes(this.mContext, this.mMethodList, reenableMinimumNonAuxSystemImes);
            int N3 = defaultEnabledIme.size();
            for (int i3 = 0; i3 < N3; i3++) {
                setInputMethodEnabledLocked(defaultEnabledIme.get(i3).getId(), true);
            }
        }
        String defaultImiId = this.mSettings.getSelectedInputMethod();
        if (!TextUtils.isEmpty(defaultImiId)) {
            if (!this.mMethodMap.containsKey(defaultImiId)) {
                Slog.w(TAG, "Default IME is uninstalled. Choose new default IME.");
                if (chooseNewDefaultIMELocked()) {
                    updateInputMethodsFromSettingsLocked(true);
                }
            } else {
                setInputMethodEnabledLocked(defaultImiId, true);
            }
        }
        this.mSwitchingController.resetCircularListLocked(this.mContext);
    }

    private void showInputMethodAndSubtypeEnabler(String inputMethodId) {
        int userId;
        Intent intent = new Intent("android.settings.INPUT_METHOD_SUBTYPE_SETTINGS");
        intent.setFlags(337641472);
        if (!TextUtils.isEmpty(inputMethodId)) {
            intent.putExtra("input_method_id", inputMethodId);
        }
        synchronized (this.mMethodMap) {
            userId = this.mSettings.getCurrentUserId();
        }
        this.mContext.startActivityAsUser(intent, (Bundle) null, UserHandle.of(userId));
    }

    /* access modifiers changed from: private */
    public void showConfigureInputMethods() {
        Intent intent = new Intent("android.settings.INPUT_METHOD_SETTINGS");
        intent.setFlags(337641472);
        this.mContext.startActivityAsUser(intent, (Bundle) null, UserHandle.CURRENT);
    }

    private boolean isScreenLocked() {
        KeyguardManager keyguardManager = this.mKeyguardManager;
        return keyguardManager != null && keyguardManager.isKeyguardLocked() && this.mKeyguardManager.isKeyguardSecure();
    }

    private void showInputMethodMenu(boolean showAuxSubtypes, int displayId) {
        int checkedItem;
        Context dialogContext;
        int lastInputMethodSubtypeId;
        int subtypeId;
        boolean isScreenLocked = isScreenLocked();
        final String lastInputMethodId = this.mSettings.getSelectedInputMethod();
        int lastInputMethodSubtypeId2 = this.mSettings.getSelectedInputMethodSubtypeId(lastInputMethodId);
        synchronized (this.mMethodMap) {
            try {
                try {
                    final List<InputMethodSubtypeSwitchingController.ImeSubtypeListItem> imList = this.mSwitchingController.getSortedInputMethodAndSubtypeListLocked(showAuxSubtypes, isScreenLocked);
                    if (imList.isEmpty()) {
                        try {
                        } catch (Throwable th) {
                            th = th;
                            boolean z = isScreenLocked;
                            String str = lastInputMethodId;
                            while (true) {
                                try {
                                    break;
                                } catch (Throwable th2) {
                                    th = th2;
                                }
                            }
                            throw th;
                        }
                    } else {
                        hideInputMethodMenuLocked();
                        if (lastInputMethodSubtypeId2 == -1) {
                            InputMethodSubtype currentSubtype = getCurrentInputMethodSubtypeLocked();
                            if (currentSubtype != null) {
                                lastInputMethodSubtypeId2 = InputMethodUtils.getSubtypeIdFromHashCode(this.mMethodMap.get(this.mCurMethodId), currentSubtype.hashCode());
                            }
                        }
                        try {
                            this.mMiuiSecurityInputMethodHelper.removeSecMethod(imList);
                            int N = imList.size();
                            this.mIms = new InputMethodInfo[N];
                            this.mSubtypeIds = new int[N];
                            checkedItem = 0;
                            for (int i = 0; i < N; i++) {
                                InputMethodSubtypeSwitchingController.ImeSubtypeListItem item = imList.get(i);
                                this.mIms[i] = item.mImi;
                                this.mSubtypeIds[i] = item.mSubtypeId;
                                if (this.mIms[i].getId().equals(lastInputMethodId) && ((subtypeId = this.mSubtypeIds[i]) == -1 || ((lastInputMethodSubtypeId2 == -1 && subtypeId == 0) || subtypeId == lastInputMethodSubtypeId2))) {
                                    checkedItem = i;
                                }
                            }
                            ActivityThread currentActivityThread = ActivityThread.currentActivityThread();
                            this.mDialogBuilder = new AlertDialog.Builder(this.mContext);
                            this.mDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                public void onCancel(DialogInterface dialog) {
                                    InputMethodManagerService.this.hideInputMethodMenu();
                                }
                            });
                            dialogContext = this.mDialogBuilder.getContext();
                            TypedArray a = dialogContext.obtainStyledAttributes((AttributeSet) null, R.styleable.DialogPreference, 16842845, 0);
                            Drawable dialogIcon = a.getDrawable(2);
                            a.recycle();
                            this.mDialogBuilder.setIcon(dialogIcon);
                            lastInputMethodSubtypeId = lastInputMethodSubtypeId2;
                        } catch (Throwable th3) {
                            th = th3;
                            boolean z2 = isScreenLocked;
                            String str2 = lastInputMethodId;
                            int i2 = lastInputMethodSubtypeId2;
                            while (true) {
                                break;
                            }
                            throw th;
                        }
                        try {
                            View tv = ((LayoutInflater) dialogContext.getSystemService(LayoutInflater.class)).inflate(17367168, (ViewGroup) null);
                            this.mDialogBuilder.setCustomTitle(tv);
                            this.mSwitchingDialogTitleView = tv;
                            View view = tv;
                            this.mSwitchingDialogTitleView.findViewById(16908983).setVisibility(this.mWindowManagerInternal.isHardKeyboardAvailable() ? 0 : 8);
                            Switch hardKeySwitch = (Switch) this.mSwitchingDialogTitleView.findViewById(16908984);
                            hardKeySwitch.setChecked(this.mShowImeWithHardKeyboard);
                            hardKeySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    InputMethodManagerService.this.mSettings.setShowImeWithHardKeyboard(isChecked);
                                    InputMethodManagerService.this.hideInputMethodMenu();
                                }
                            });
                            removeCustomTitle();
                            Switch switchR = hardKeySwitch;
                            final ImeSubtypeListAdapter adapter = new ImeSubtypeListAdapter(dialogContext, 17367169, imList, checkedItem);
                            List<InputMethodSubtypeSwitchingController.ImeSubtypeListItem> list = imList;
                            this.mDialogBuilder.setSingleChoiceItems(adapter, checkedItem, new DialogInterface.OnClickListener() {
                                /* JADX WARNING: Code restructure failed: missing block: B:22:0x0073, code lost:
                                    return;
                                 */
                                /* JADX WARNING: Code restructure failed: missing block: B:24:0x0075, code lost:
                                    return;
                                 */
                                /* Code decompiled incorrectly, please refer to instructions dump. */
                                public void onClick(android.content.DialogInterface r10, int r11) {
                                    /*
                                        r9 = this;
                                        com.android.server.inputmethod.InputMethodManagerService r0 = com.android.server.inputmethod.InputMethodManagerService.this
                                        android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r0 = r0.mMethodMap
                                        monitor-enter(r0)
                                        com.android.server.inputmethod.InputMethodManagerService r1 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0076 }
                                        android.view.inputmethod.InputMethodInfo[] r1 = r1.mIms     // Catch:{ all -> 0x0076 }
                                        if (r1 == 0) goto L_0x0074
                                        com.android.server.inputmethod.InputMethodManagerService r1 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0076 }
                                        android.view.inputmethod.InputMethodInfo[] r1 = r1.mIms     // Catch:{ all -> 0x0076 }
                                        int r1 = r1.length     // Catch:{ all -> 0x0076 }
                                        if (r1 <= r11) goto L_0x0074
                                        com.android.server.inputmethod.InputMethodManagerService r1 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0076 }
                                        int[] r1 = r1.mSubtypeIds     // Catch:{ all -> 0x0076 }
                                        if (r1 == 0) goto L_0x0074
                                        com.android.server.inputmethod.InputMethodManagerService r1 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0076 }
                                        int[] r1 = r1.mSubtypeIds     // Catch:{ all -> 0x0076 }
                                        int r1 = r1.length     // Catch:{ all -> 0x0076 }
                                        if (r1 > r11) goto L_0x0028
                                        goto L_0x0074
                                    L_0x0028:
                                        com.android.server.inputmethod.InputMethodManagerService r1 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0076 }
                                        android.view.inputmethod.InputMethodInfo[] r1 = r1.mIms     // Catch:{ all -> 0x0076 }
                                        r1 = r1[r11]     // Catch:{ all -> 0x0076 }
                                        com.android.server.inputmethod.InputMethodManagerService r2 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0076 }
                                        int[] r2 = r2.mSubtypeIds     // Catch:{ all -> 0x0076 }
                                        r2 = r2[r11]     // Catch:{ all -> 0x0076 }
                                        com.android.server.inputmethod.InputMethodManagerService$ImeSubtypeListAdapter r3 = r4     // Catch:{ all -> 0x0076 }
                                        r3.mCheckedItem = r11     // Catch:{ all -> 0x0076 }
                                        com.android.server.inputmethod.InputMethodManagerService$ImeSubtypeListAdapter r3 = r4     // Catch:{ all -> 0x0076 }
                                        r3.notifyDataSetChanged()     // Catch:{ all -> 0x0076 }
                                        com.android.server.inputmethod.InputMethodManagerService r3 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0076 }
                                        r3.hideInputMethodMenu()     // Catch:{ all -> 0x0076 }
                                        if (r1 == 0) goto L_0x0072
                                        if (r2 < 0) goto L_0x0053
                                        int r3 = r1.getSubtypeCount()     // Catch:{ all -> 0x0076 }
                                        if (r2 < r3) goto L_0x0051
                                        goto L_0x0053
                                    L_0x0051:
                                        r8 = r2
                                        goto L_0x0055
                                    L_0x0053:
                                        r2 = -1
                                        r8 = r2
                                    L_0x0055:
                                        com.android.server.inputmethod.InputMethodManagerService r2 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0076 }
                                        java.lang.String r3 = r1.getId()     // Catch:{ all -> 0x0076 }
                                        r2.setInputMethodLocked(r3, r8)     // Catch:{ all -> 0x0076 }
                                        com.android.server.inputmethod.InputMethodManagerService r2 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0076 }
                                        android.content.Context r2 = r2.mContext     // Catch:{ all -> 0x0076 }
                                        java.lang.String r4 = r3     // Catch:{ all -> 0x0076 }
                                        java.util.List r5 = r0     // Catch:{ all -> 0x0076 }
                                        com.android.server.inputmethod.InputMethodManagerService r3 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0076 }
                                        com.android.server.inputmethod.InputMethodUtils$InputMethodSettings r6 = r3.mSettings     // Catch:{ all -> 0x0076 }
                                        com.android.server.inputmethod.InputMethodManagerService r3 = com.android.server.inputmethod.InputMethodManagerService.this     // Catch:{ all -> 0x0076 }
                                        android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r7 = r3.mMethodMap     // Catch:{ all -> 0x0076 }
                                        r3 = r1
                                        com.android.server.inputmethod.InputMethodManagerServiceInjector.onSwitchIME(r2, r3, r4, r5, r6, r7)     // Catch:{ all -> 0x0076 }
                                    L_0x0072:
                                        monitor-exit(r0)     // Catch:{ all -> 0x0076 }
                                        return
                                    L_0x0074:
                                        monitor-exit(r0)     // Catch:{ all -> 0x0076 }
                                        return
                                    L_0x0076:
                                        r1 = move-exception
                                        monitor-exit(r0)     // Catch:{ all -> 0x0076 }
                                        throw r1
                                    */
                                    throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.AnonymousClass5.onClick(android.content.DialogInterface, int):void");
                                }
                            });
                            if (!isScreenLocked) {
                                boolean z3 = isScreenLocked;
                                try {
                                    String str3 = lastInputMethodId;
                                    this.mDialogBuilder.setPositiveButton(android.miui.R.string.configure_input_methods, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            InputMethodManagerService.this.showConfigureInputMethods();
                                        }
                                    });
                                } catch (Throwable th4) {
                                    th = th4;
                                    int i3 = lastInputMethodSubtypeId;
                                    while (true) {
                                        break;
                                    }
                                    throw th;
                                }
                            } else {
                                String str4 = lastInputMethodId;
                            }
                            this.mSwitchingDialog = this.mDialogBuilder.create();
                            this.mSwitchingDialog.setCanceledOnTouchOutside(true);
                            Window w = this.mSwitchingDialog.getWindow();
                            WindowManager.LayoutParams attrs = w.getAttributes();
                            w.setType(2012);
                            attrs.token = this.mSwitchingDialogToken;
                            attrs.privateFlags |= 16;
                            attrs.setTitle("Select input method");
                            w.setAttributes(attrs);
                            Window window = w;
                            updateSystemUiLocked(this.mImeWindowVis, this.mBackDisposition);
                            this.mSwitchingDialog.show();
                        } catch (Throwable th5) {
                            th = th5;
                            boolean z4 = isScreenLocked;
                            String str5 = lastInputMethodId;
                            int i4 = lastInputMethodSubtypeId;
                            while (true) {
                                break;
                            }
                            throw th;
                        }
                    }
                } catch (Throwable th6) {
                    th = th6;
                    boolean z5 = isScreenLocked;
                    String str6 = lastInputMethodId;
                    while (true) {
                        break;
                    }
                    throw th;
                }
            } catch (Throwable th7) {
                th = th7;
                boolean z6 = showAuxSubtypes;
                boolean z7 = isScreenLocked;
                String str7 = lastInputMethodId;
                while (true) {
                    break;
                }
                throw th;
            }
        }
    }

    private static class ImeSubtypeListAdapter extends ArrayAdapter<InputMethodSubtypeSwitchingController.ImeSubtypeListItem> {
        public int mCheckedItem;
        private final LayoutInflater mInflater;
        private final List<InputMethodSubtypeSwitchingController.ImeSubtypeListItem> mItemsList;
        private final int mTextViewResourceId;

        public ImeSubtypeListAdapter(Context context, int textViewResourceId, List<InputMethodSubtypeSwitchingController.ImeSubtypeListItem> itemsList, int checkedItem) {
            super(context, textViewResourceId, itemsList);
            this.mTextViewResourceId = textViewResourceId;
            this.mItemsList = itemsList;
            this.mCheckedItem = checkedItem;
            this.mInflater = (LayoutInflater) context.getSystemService(LayoutInflater.class);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = this.mInflater.inflate(this.mTextViewResourceId, (ViewGroup) null);
            }
            if (position < 0 || position >= this.mItemsList.size()) {
                return view;
            }
            InputMethodSubtypeSwitchingController.ImeSubtypeListItem item = this.mItemsList.get(position);
            CharSequence imeName = item.mImeName;
            CharSequence subtypeName = item.mSubtypeName;
            TextView firstTextView = (TextView) view.findViewById(16908308);
            TextView secondTextView = (TextView) view.findViewById(16908309);
            boolean z = false;
            if (TextUtils.isEmpty(subtypeName)) {
                firstTextView.setText(imeName);
                secondTextView.setVisibility(8);
            } else {
                firstTextView.setText(subtypeName);
                secondTextView.setText(imeName);
                secondTextView.setVisibility(0);
            }
            RadioButton radioButton = (RadioButton) view.findViewById(16909307);
            if (position == this.mCheckedItem) {
                z = true;
            }
            radioButton.setChecked(z);
            return view;
        }
    }

    /* access modifiers changed from: package-private */
    public void hideInputMethodMenu() {
        synchronized (this.mMethodMap) {
            hideInputMethodMenuLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public void hideInputMethodMenuLocked() {
        AlertDialog alertDialog = this.mSwitchingDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mSwitchingDialog = null;
            this.mSwitchingDialogTitleView = null;
        }
        updateSystemUiLocked(this.mImeWindowVis, this.mBackDisposition);
        this.mDialogBuilder = null;
        this.mIms = null;
    }

    /* access modifiers changed from: private */
    public boolean setInputMethodEnabledLocked(String id, boolean enabled) {
        List<Pair<String, ArrayList<String>>> enabledInputMethodsList = this.mSettings.getEnabledInputMethodsAndSubtypeListLocked();
        if (enabled) {
            for (Pair<String, ArrayList<String>> pair : enabledInputMethodsList) {
                if (((String) pair.first).equals(id)) {
                    return true;
                }
            }
            this.mSettings.appendAndPutEnabledInputMethodLocked(id, false);
            return false;
        }
        if (!this.mSettings.buildAndPutEnabledInputMethodsStrRemovingIdLocked(new StringBuilder(), enabledInputMethodsList, id)) {
            return false;
        }
        if (id.equals(this.mSettings.getSelectedInputMethod()) && !chooseNewDefaultIMELocked()) {
            Slog.i(TAG, "Can't find new IME, unsetting the current input method.");
            resetSelectedInputMethodAndSubtypeLocked("");
        }
        return true;
    }

    private void setSelectedInputMethodAndSubtypeLocked(InputMethodInfo imi, int subtypeId, boolean setSubtypeOnly) {
        this.mSettings.saveCurrentInputMethodAndSubtypeToHistory(this.mCurMethodId, this.mCurrentSubtype);
        if (imi == null || subtypeId < 0) {
            this.mSettings.putSelectedSubtype(-1);
            this.mCurrentSubtype = null;
        } else if (subtypeId < imi.getSubtypeCount()) {
            InputMethodSubtype subtype = imi.getSubtypeAt(subtypeId);
            this.mSettings.putSelectedSubtype(subtype.hashCode());
            this.mCurrentSubtype = subtype;
        } else {
            this.mSettings.putSelectedSubtype(-1);
            this.mCurrentSubtype = getCurrentInputMethodSubtypeLocked();
        }
        if (!setSubtypeOnly) {
            this.mSettings.putSelectedInputMethod(imi != null ? imi.getId() : "");
        }
    }

    /* access modifiers changed from: private */
    public void resetSelectedInputMethodAndSubtypeLocked(String newDefaultIme) {
        String subtypeHashCode;
        InputMethodInfo imi = this.mMethodMap.get(newDefaultIme);
        int lastSubtypeId = -1;
        if (!(imi == null || TextUtils.isEmpty(newDefaultIme) || (subtypeHashCode = this.mSettings.getLastSubtypeForInputMethodLocked(newDefaultIme)) == null)) {
            try {
                lastSubtypeId = InputMethodUtils.getSubtypeIdFromHashCode(imi, Integer.parseInt(subtypeHashCode));
            } catch (NumberFormatException e) {
                Slog.w(TAG, "HashCode for subtype looks broken: " + subtypeHashCode, e);
            }
        }
        setSelectedInputMethodAndSubtypeLocked(imi, lastSubtypeId, false);
    }

    public InputMethodSubtype getCurrentInputMethodSubtype() {
        synchronized (this.mMethodMap) {
            if (!calledFromValidUserLocked()) {
                return null;
            }
            InputMethodSubtype currentInputMethodSubtypeLocked = getCurrentInputMethodSubtypeLocked();
            return currentInputMethodSubtypeLocked;
        }
    }

    private InputMethodSubtype getCurrentInputMethodSubtypeLocked() {
        InputMethodSubtype inputMethodSubtype;
        if (this.mCurMethodId == null) {
            return null;
        }
        boolean subtypeIsSelected = this.mSettings.isSubtypeSelected();
        InputMethodInfo imi = this.mMethodMap.get(this.mCurMethodId);
        if (imi == null || imi.getSubtypeCount() == 0) {
            return null;
        }
        if (!subtypeIsSelected || (inputMethodSubtype = this.mCurrentSubtype) == null || !InputMethodUtils.isValidSubtypeId(imi, inputMethodSubtype.hashCode())) {
            int subtypeId = this.mSettings.getSelectedInputMethodSubtypeId(this.mCurMethodId);
            if (subtypeId == -1) {
                List<InputMethodSubtype> explicitlyOrImplicitlyEnabledSubtypes = this.mSettings.getEnabledInputMethodSubtypeListLocked(this.mContext, imi, true);
                if (explicitlyOrImplicitlyEnabledSubtypes.size() == 1) {
                    this.mCurrentSubtype = explicitlyOrImplicitlyEnabledSubtypes.get(0);
                } else if (explicitlyOrImplicitlyEnabledSubtypes.size() > 1) {
                    this.mCurrentSubtype = InputMethodUtils.findLastResortApplicableSubtypeLocked(this.mRes, explicitlyOrImplicitlyEnabledSubtypes, "keyboard", (String) null, true);
                    if (this.mCurrentSubtype == null) {
                        this.mCurrentSubtype = InputMethodUtils.findLastResortApplicableSubtypeLocked(this.mRes, explicitlyOrImplicitlyEnabledSubtypes, (String) null, (String) null, true);
                    }
                }
            } else {
                this.mCurrentSubtype = InputMethodUtils.getSubtypes(imi).get(subtypeId);
            }
        }
        return this.mCurrentSubtype;
    }

    /* access modifiers changed from: private */
    public List<InputMethodInfo> getInputMethodListAsUser(int userId) {
        List<InputMethodInfo> inputMethodListLocked;
        synchronized (this.mMethodMap) {
            inputMethodListLocked = getInputMethodListLocked(userId);
        }
        return inputMethodListLocked;
    }

    /* access modifiers changed from: private */
    public List<InputMethodInfo> getEnabledInputMethodListAsUser(int userId) {
        List<InputMethodInfo> enabledInputMethodListLocked;
        synchronized (this.mMethodMap) {
            enabledInputMethodListLocked = getEnabledInputMethodListLocked(userId);
        }
        return enabledInputMethodListLocked;
    }

    private static final class LocalServiceImpl extends InputMethodManagerInternal {
        private final InputMethodManagerService mService;

        LocalServiceImpl(InputMethodManagerService service) {
            this.mService = service;
        }

        public void setInteractive(boolean interactive) {
            this.mService.mHandler.obtainMessage(InputMethodManagerService.MSG_SET_INTERACTIVE, interactive, 0).sendToTarget();
        }

        public void hideCurrentInputMethod() {
            this.mService.mHandler.removeMessages(InputMethodManagerService.MSG_HIDE_CURRENT_INPUT_METHOD);
            this.mService.mHandler.sendEmptyMessage(InputMethodManagerService.MSG_HIDE_CURRENT_INPUT_METHOD);
        }

        public List<InputMethodInfo> getInputMethodListAsUser(int userId) {
            return this.mService.getInputMethodListAsUser(userId);
        }

        public List<InputMethodInfo> getEnabledInputMethodListAsUser(int userId) {
            return this.mService.getEnabledInputMethodListAsUser(userId);
        }
    }

    /* access modifiers changed from: private */
    public IInputContentUriToken createInputContentUriToken(IBinder token, Uri contentUri, String packageName) {
        if (token == null) {
            throw new NullPointerException("token");
        } else if (packageName == null) {
            throw new NullPointerException("packageName");
        } else if (contentUri == null) {
            throw new NullPointerException("contentUri");
        } else if (ActivityTaskManagerInternal.ASSIST_KEY_CONTENT.equals(contentUri.getScheme())) {
            synchronized (this.mMethodMap) {
                int uid = Binder.getCallingUid();
                if (this.mCurMethodId == null) {
                    return null;
                }
                if (this.mCurToken != token) {
                    Slog.e(TAG, "Ignoring createInputContentUriToken mCurToken=" + this.mCurToken + " token=" + token);
                    return null;
                } else if (!TextUtils.equals(this.mCurAttribute.packageName, packageName)) {
                    Slog.e(TAG, "Ignoring createInputContentUriToken mCurAttribute.packageName=" + this.mCurAttribute.packageName + " packageName=" + packageName);
                    return null;
                } else {
                    int imeUserId = UserHandle.getUserId(uid);
                    int appUserId = UserHandle.getUserId(this.mCurClient.uid);
                    InputContentUriTokenHandler inputContentUriTokenHandler = new InputContentUriTokenHandler(ContentProvider.getUriWithoutUserId(contentUri), uid, packageName, ContentProvider.getUserIdFromUri(contentUri, imeUserId), appUserId);
                    return inputContentUriTokenHandler;
                }
            }
        } else {
            throw new InvalidParameterException("contentUri must have content scheme");
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x002e, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void reportFullscreenMode(android.os.IBinder r7, boolean r8) {
        /*
            r6 = this;
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r0 = r6.mMethodMap
            monitor-enter(r0)
            boolean r1 = r6.calledWithValidTokenLocked(r7)     // Catch:{ all -> 0x002f }
            if (r1 != 0) goto L_0x000b
            monitor-exit(r0)     // Catch:{ all -> 0x002f }
            return
        L_0x000b:
            com.android.server.inputmethod.InputMethodManagerService$ClientState r1 = r6.mCurClient     // Catch:{ all -> 0x002f }
            if (r1 == 0) goto L_0x002d
            com.android.server.inputmethod.InputMethodManagerService$ClientState r1 = r6.mCurClient     // Catch:{ all -> 0x002f }
            com.android.internal.view.IInputMethodClient r1 = r1.client     // Catch:{ all -> 0x002f }
            if (r1 == 0) goto L_0x002d
            r6.mInFullscreenMode = r8     // Catch:{ all -> 0x002f }
            com.android.server.inputmethod.InputMethodManagerService$ClientState r1 = r6.mCurClient     // Catch:{ all -> 0x002f }
            com.android.internal.view.IInputMethodClient r1 = r1.client     // Catch:{ all -> 0x002f }
            com.android.internal.os.HandlerCaller r2 = r6.mCaller     // Catch:{ all -> 0x002f }
            r3 = 3045(0xbe5, float:4.267E-42)
            if (r8 == 0) goto L_0x0023
            r4 = 1
            goto L_0x0024
        L_0x0023:
            r4 = 0
        L_0x0024:
            com.android.server.inputmethod.InputMethodManagerService$ClientState r5 = r6.mCurClient     // Catch:{ all -> 0x002f }
            android.os.Message r2 = r2.obtainMessageIO(r3, r4, r5)     // Catch:{ all -> 0x002f }
            r6.executeOrSendMessage(r1, r2)     // Catch:{ all -> 0x002f }
        L_0x002d:
            monitor-exit(r0)     // Catch:{ all -> 0x002f }
            return
        L_0x002f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.reportFullscreenMode(android.os.IBinder, boolean):void");
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        ClientState client;
        ClientState focusedWindowClient;
        IInputMethod method;
        if (DumpUtils.checkDumpPermission(this.mContext, TAG, pw)) {
            Printer p = new PrintWriterPrinter(pw);
            synchronized (this.mMethodMap) {
                p.println("Current Input Method Manager state:");
                int N = this.mMethodList.size();
                p.println("  Input Methods: mMethodMapUpdateCount=" + this.mMethodMapUpdateCount);
                for (int i = 0; i < N; i++) {
                    p.println("  InputMethod #" + i + ":");
                    this.mMethodList.get(i).dump(p, "    ");
                }
                p.println("  Clients:");
                int numClients = this.mClients.size();
                for (int i2 = 0; i2 < numClients; i2++) {
                    ClientState ci = this.mClients.valueAt(i2);
                    p.println("  Client " + ci + ":");
                    StringBuilder sb = new StringBuilder();
                    sb.append("    client=");
                    sb.append(ci.client);
                    p.println(sb.toString());
                    p.println("    inputContext=" + ci.inputContext);
                    p.println("    sessionRequested=" + ci.sessionRequested);
                    p.println("    curSession=" + ci.curSession);
                }
                p.println("  mCurMethodId=" + this.mCurMethodId);
                client = this.mCurClient;
                p.println("  mCurClient=" + client + " mCurSeq=" + this.mCurSeq);
                p.println("  mCurFocusedWindow=" + this.mCurFocusedWindow + " softInputMode=" + InputMethodDebug.softInputModeToString(this.mCurFocusedWindowSoftInputMode) + " client=" + this.mCurFocusedWindowClient);
                focusedWindowClient = this.mCurFocusedWindowClient;
                p.println("  mCurId=" + this.mCurId + " mHaveConnection=" + this.mHaveConnection + " mBoundToMethod=" + this.mBoundToMethod + " mVisibleBound=" + this.mVisibleBound);
                StringBuilder sb2 = new StringBuilder();
                sb2.append("  mCurToken=");
                sb2.append(this.mCurToken);
                p.println(sb2.toString());
                StringBuilder sb3 = new StringBuilder();
                sb3.append("  mCurTokenDisplayId=");
                sb3.append(this.mCurTokenDisplayId);
                p.println(sb3.toString());
                p.println("  mCurIntent=" + this.mCurIntent);
                method = this.mCurMethod;
                p.println("  mCurMethod=" + this.mCurMethod);
                p.println("  mEnabledSession=" + this.mEnabledSession);
                p.println("  mShowRequested=" + this.mShowRequested + " mShowExplicitlyRequested=" + this.mShowExplicitlyRequested + " mShowForced=" + this.mShowForced + " mInputShown=" + this.mInputShown);
                StringBuilder sb4 = new StringBuilder();
                sb4.append("  mInFullscreenMode=");
                sb4.append(this.mInFullscreenMode);
                p.println(sb4.toString());
                p.println("  mSystemReady=" + this.mSystemReady + " mInteractive=" + this.mIsInteractive);
                StringBuilder sb5 = new StringBuilder();
                sb5.append("  mSettingsObserver=");
                sb5.append(this.mSettingsObserver);
                p.println(sb5.toString());
                p.println("  mSwitchingController:");
                this.mSwitchingController.dump(p);
                p.println("  mSettings:");
                this.mSettings.dumpLocked(p, "    ");
                p.println("  mStartInputHistory:");
                this.mStartInputHistory.dump(pw, "   ");
            }
            p.println(" ");
            if (client != null) {
                pw.flush();
                try {
                    TransferPipe.dumpAsync(client.client.asBinder(), fd, args);
                } catch (RemoteException | IOException e) {
                    p.println("Failed to dump input method client: " + e);
                }
            } else {
                p.println("No input method client.");
            }
            if (!(focusedWindowClient == null || client == focusedWindowClient)) {
                p.println(" ");
                p.println("Warning: Current input method client doesn't match the last focused. window.");
                p.println("Dumping input method client in the last focused window just in case.");
                p.println(" ");
                pw.flush();
                try {
                    TransferPipe.dumpAsync(focusedWindowClient.client.asBinder(), fd, args);
                } catch (RemoteException | IOException e2) {
                    p.println("Failed to dump input method client in focused window: " + e2);
                }
            }
            p.println(" ");
            if (method != null) {
                pw.flush();
                try {
                    TransferPipe.dumpAsync(method.asBinder(), fd, args);
                } catch (RemoteException | IOException e3) {
                    p.println("Failed to dump input method service: " + e3);
                }
            } else {
                p.println("No input method service.");
            }
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.os.Binder] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onShellCommand(java.io.FileDescriptor r12, java.io.FileDescriptor r13, java.io.FileDescriptor r14, java.lang.String[] r15, android.os.ShellCallback r16, android.os.ResultReceiver r17) throws android.os.RemoteException {
        /*
            r11 = this;
            r8 = r17
            int r9 = android.os.Binder.getCallingUid()
            if (r9 == 0) goto L_0x0042
            r0 = 2000(0x7d0, float:2.803E-42)
            if (r9 == r0) goto L_0x0042
            if (r8 == 0) goto L_0x0013
            r0 = -1
            r1 = 0
            r8.send(r0, r1)
        L_0x0013:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "InputMethodManagerService does not support shell commands from non-shell users. callingUid="
            r0.append(r1)
            r0.append(r9)
            java.lang.String r1 = " args="
            r0.append(r1)
            java.lang.String r1 = java.util.Arrays.toString(r15)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            boolean r1 = android.os.Process.isCoreUid(r9)
            if (r1 == 0) goto L_0x003c
            java.lang.String r1 = "InputMethodManagerService"
            android.util.Slog.e(r1, r0)
            return
        L_0x003c:
            java.lang.SecurityException r1 = new java.lang.SecurityException
            r1.<init>(r0)
            throw r1
        L_0x0042:
            com.android.server.inputmethod.InputMethodManagerService$ShellCommandImpl r0 = new com.android.server.inputmethod.InputMethodManagerService$ShellCommandImpl
            r10 = r11
            r0.<init>(r11)
            r1 = r11
            r2 = r12
            r3 = r13
            r4 = r14
            r5 = r15
            r6 = r16
            r7 = r17
            r0.exec(r1, r2, r3, r4, r5, r6, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
    }

    private static final class ShellCommandImpl extends ShellCommand {
        final InputMethodManagerService mService;

        ShellCommandImpl(InputMethodManagerService service) {
            this.mService = service;
        }

        public int onCommand(String cmd) {
            Arrays.asList(new String[]{"android.permission.DUMP", "android.permission.INTERACT_ACROSS_USERS_FULL", "android.permission.WRITE_SECURE_SETTINGS"}).forEach(new Consumer() {
                public final void accept(Object obj) {
                    InputMethodManagerService.ShellCommandImpl.this.lambda$onCommand$0$InputMethodManagerService$ShellCommandImpl((String) obj);
                }
            });
            long identity = Binder.clearCallingIdentity();
            try {
                return onCommandWithSystemIdentity(cmd);
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        public /* synthetic */ void lambda$onCommand$0$InputMethodManagerService$ShellCommandImpl(String permission) {
            this.mService.mContext.enforceCallingPermission(permission, (String) null);
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private int onCommandWithSystemIdentity(java.lang.String r9) {
            /*
                r8 = this;
                java.lang.String r0 = "refresh_debug_properties"
                boolean r0 = r0.equals(r9)
                if (r0 == 0) goto L_0x000e
                int r0 = r8.refreshDebugProperties()
                return r0
            L_0x000e:
                java.lang.String r0 = "get-last-switch-user-id"
                boolean r0 = r0.equals(r9)
                if (r0 == 0) goto L_0x001d
                com.android.server.inputmethod.InputMethodManagerService r0 = r8.mService
                int r0 = r0.getLastSwitchUserId(r8)
                return r0
            L_0x001d:
                java.lang.String r0 = "ime"
                boolean r0 = r0.equals(r9)
                if (r0 == 0) goto L_0x00cd
                java.lang.String r0 = r8.getNextArg()
                r1 = 0
                if (r0 == 0) goto L_0x00c9
                java.lang.String r2 = "help"
                boolean r2 = r2.equals(r0)
                if (r2 != 0) goto L_0x00c9
                java.lang.String r2 = "-h"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x0040
                goto L_0x00c9
            L_0x0040:
                int r2 = r0.hashCode()
                r3 = 4
                r4 = 3
                r5 = 2
                r6 = -1
                r7 = 1
                switch(r2) {
                    case -1298848381: goto L_0x0078;
                    case 113762: goto L_0x006d;
                    case 3322014: goto L_0x0062;
                    case 108404047: goto L_0x0057;
                    case 1671308008: goto L_0x004d;
                    default: goto L_0x004c;
                }
            L_0x004c:
                goto L_0x0082
            L_0x004d:
                java.lang.String r2 = "disable"
                boolean r2 = r0.equals(r2)
                if (r2 == 0) goto L_0x004c
                r2 = r5
                goto L_0x0083
            L_0x0057:
                java.lang.String r2 = "reset"
                boolean r2 = r0.equals(r2)
                if (r2 == 0) goto L_0x004c
                r2 = r3
                goto L_0x0083
            L_0x0062:
                java.lang.String r2 = "list"
                boolean r2 = r0.equals(r2)
                if (r2 == 0) goto L_0x004c
                r2 = r1
                goto L_0x0083
            L_0x006d:
                java.lang.String r2 = "set"
                boolean r2 = r0.equals(r2)
                if (r2 == 0) goto L_0x004c
                r2 = r4
                goto L_0x0083
            L_0x0078:
                java.lang.String r2 = "enable"
                boolean r2 = r0.equals(r2)
                if (r2 == 0) goto L_0x004c
                r2 = r7
                goto L_0x0083
            L_0x0082:
                r2 = r6
            L_0x0083:
                if (r2 == 0) goto L_0x00c2
                if (r2 == r7) goto L_0x00bb
                if (r2 == r5) goto L_0x00b4
                if (r2 == r4) goto L_0x00ad
                if (r2 == r3) goto L_0x00a6
                java.io.PrintWriter r1 = r8.getOutPrintWriter()
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "Unknown command: "
                r2.append(r3)
                r2.append(r0)
                java.lang.String r2 = r2.toString()
                r1.println(r2)
                return r6
            L_0x00a6:
                com.android.server.inputmethod.InputMethodManagerService r1 = r8.mService
                int r1 = r1.handleShellCommandResetInputMethod(r8)
                return r1
            L_0x00ad:
                com.android.server.inputmethod.InputMethodManagerService r1 = r8.mService
                int r1 = r1.handleShellCommandSetInputMethod(r8)
                return r1
            L_0x00b4:
                com.android.server.inputmethod.InputMethodManagerService r2 = r8.mService
                int r1 = r2.handleShellCommandEnableDisableInputMethod(r8, r1)
                return r1
            L_0x00bb:
                com.android.server.inputmethod.InputMethodManagerService r1 = r8.mService
                int r1 = r1.handleShellCommandEnableDisableInputMethod(r8, r7)
                return r1
            L_0x00c2:
                com.android.server.inputmethod.InputMethodManagerService r1 = r8.mService
                int r1 = r1.handleShellCommandListInputMethods(r8)
                return r1
            L_0x00c9:
                r8.onImeCommandHelp()
                return r1
            L_0x00cd:
                int r0 = r8.handleDefaultCommands(r9)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.ShellCommandImpl.onCommandWithSystemIdentity(java.lang.String):int");
        }

        private int refreshDebugProperties() {
            DebugFlags.FLAG_OPTIMIZE_START_INPUT.refresh();
            DebugFlags.FLAG_PRE_RENDER_IME_VIEWS.refresh();
            return 0;
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0031, code lost:
            $closeResource(r1, r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0034, code lost:
            throw r2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x002e, code lost:
            r2 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x002f, code lost:
            if (r0 != null) goto L_0x0031;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onHelp() {
            /*
                r3 = this;
                java.io.PrintWriter r0 = r3.getOutPrintWriter()
                java.lang.String r1 = "InputMethodManagerService commands:"
                r0.println(r1)     // Catch:{ all -> 0x002c }
                java.lang.String r1 = "  help"
                r0.println(r1)     // Catch:{ all -> 0x002c }
                java.lang.String r1 = "    Prints this help text."
                r0.println(r1)     // Catch:{ all -> 0x002c }
                java.lang.String r1 = "  dump [options]"
                r0.println(r1)     // Catch:{ all -> 0x002c }
                java.lang.String r1 = "    Synonym of dumpsys."
                r0.println(r1)     // Catch:{ all -> 0x002c }
                java.lang.String r1 = "  ime <command> [options]"
                r0.println(r1)     // Catch:{ all -> 0x002c }
                java.lang.String r1 = "    Manipulate IMEs.  Run \"ime help\" for details."
                r0.println(r1)     // Catch:{ all -> 0x002c }
                r1 = 0
                $closeResource(r1, r0)
                return
            L_0x002c:
                r1 = move-exception
                throw r1     // Catch:{ all -> 0x002e }
            L_0x002e:
                r2 = move-exception
                if (r0 == 0) goto L_0x0034
                $closeResource(r1, r0)
            L_0x0034:
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.ShellCommandImpl.onHelp():void");
        }

        private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
            if (x0 != null) {
                try {
                    x1.close();
                } catch (Throwable th) {
                    x0.addSuppressed(th);
                }
            } else {
                x1.close();
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        /* JADX WARNING: Code restructure failed: missing block: B:10:0x00c3, code lost:
            throw r1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x00bf, code lost:
            r1 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x00c0, code lost:
            $closeResource(r0, r2);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void onImeCommandHelp() {
            /*
                r6 = this;
                java.lang.String r0 = "--user <USER_ID>: Specify which user to enable."
                java.lang.String r1 = " Assumes the current user if not specified."
                com.android.internal.util.IndentingPrintWriter r2 = new com.android.internal.util.IndentingPrintWriter
                java.io.PrintWriter r3 = r6.getOutPrintWriter()
                java.lang.String r4 = "  "
                r5 = 100
                r2.<init>(r3, r4, r5)
                java.lang.String r3 = "ime <command>:"
                r2.println(r3)     // Catch:{ all -> 0x00bd }
                r2.increaseIndent()     // Catch:{ all -> 0x00bd }
                java.lang.String r3 = "list [-a] [-s]"
                r2.println(r3)     // Catch:{ all -> 0x00bd }
                r2.increaseIndent()     // Catch:{ all -> 0x00bd }
                java.lang.String r3 = "prints all enabled input methods."
                r2.println(r3)     // Catch:{ all -> 0x00bd }
                r2.increaseIndent()     // Catch:{ all -> 0x00bd }
                java.lang.String r3 = "-a: see all input methods"
                r2.println(r3)     // Catch:{ all -> 0x00bd }
                java.lang.String r3 = "-s: only a single summary line of each"
                r2.println(r3)     // Catch:{ all -> 0x00bd }
                r2.decreaseIndent()     // Catch:{ all -> 0x00bd }
                r2.decreaseIndent()     // Catch:{ all -> 0x00bd }
                java.lang.String r3 = "enable [--user <USER_ID>] <ID>"
                r2.println(r3)     // Catch:{ all -> 0x00bd }
                r2.increaseIndent()     // Catch:{ all -> 0x00bd }
                java.lang.String r3 = "allows the given input method ID to be used."
                r2.println(r3)     // Catch:{ all -> 0x00bd }
                r2.increaseIndent()     // Catch:{ all -> 0x00bd }
                r2.print(r0)     // Catch:{ all -> 0x00bd }
                r2.println(r1)     // Catch:{ all -> 0x00bd }
                r2.decreaseIndent()     // Catch:{ all -> 0x00bd }
                r2.decreaseIndent()     // Catch:{ all -> 0x00bd }
                java.lang.String r3 = "disable [--user <USER_ID>] <ID>"
                r2.println(r3)     // Catch:{ all -> 0x00bd }
                r2.increaseIndent()     // Catch:{ all -> 0x00bd }
                java.lang.String r3 = "disallows the given input method ID to be used."
                r2.println(r3)     // Catch:{ all -> 0x00bd }
                r2.increaseIndent()     // Catch:{ all -> 0x00bd }
                java.lang.String r3 = "--user <USER_ID>: Specify which user to disable."
                r2.print(r3)     // Catch:{ all -> 0x00bd }
                r2.println(r1)     // Catch:{ all -> 0x00bd }
                r2.decreaseIndent()     // Catch:{ all -> 0x00bd }
                r2.decreaseIndent()     // Catch:{ all -> 0x00bd }
                java.lang.String r3 = "set [--user <USER_ID>] <ID>"
                r2.println(r3)     // Catch:{ all -> 0x00bd }
                r2.increaseIndent()     // Catch:{ all -> 0x00bd }
                java.lang.String r3 = "switches to the given input method ID."
                r2.println(r3)     // Catch:{ all -> 0x00bd }
                r2.increaseIndent()     // Catch:{ all -> 0x00bd }
                r2.print(r0)     // Catch:{ all -> 0x00bd }
                r2.println(r1)     // Catch:{ all -> 0x00bd }
                r2.decreaseIndent()     // Catch:{ all -> 0x00bd }
                r2.decreaseIndent()     // Catch:{ all -> 0x00bd }
                java.lang.String r0 = "reset [--user <USER_ID>]"
                r2.println(r0)     // Catch:{ all -> 0x00bd }
                r2.increaseIndent()     // Catch:{ all -> 0x00bd }
                java.lang.String r0 = "reset currently selected/enabled IMEs to the default ones as if the device is initially booted with the current locale."
                r2.println(r0)     // Catch:{ all -> 0x00bd }
                r2.increaseIndent()     // Catch:{ all -> 0x00bd }
                java.lang.String r0 = "--user <USER_ID>: Specify which user to reset."
                r2.print(r0)     // Catch:{ all -> 0x00bd }
                r2.println(r1)     // Catch:{ all -> 0x00bd }
                r2.decreaseIndent()     // Catch:{ all -> 0x00bd }
                r2.decreaseIndent()     // Catch:{ all -> 0x00bd }
                r2.decreaseIndent()     // Catch:{ all -> 0x00bd }
                r0 = 0
                $closeResource(r0, r2)
                return
            L_0x00bd:
                r0 = move-exception
                throw r0     // Catch:{ all -> 0x00bf }
            L_0x00bf:
                r1 = move-exception
                $closeResource(r0, r2)
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.ShellCommandImpl.onImeCommandHelp():void");
        }
    }

    /* access modifiers changed from: private */
    public int getLastSwitchUserId(ShellCommand shellCommand) {
        synchronized (this.mMethodMap) {
            shellCommand.getOutPrintWriter().println(this.mLastSwitchUserId);
        }
        return 0;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00c3, code lost:
        if (r0.equals("-a") != false) goto L_0x00c7;
     */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00c9  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00dc  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int handleShellCommandListInputMethods(android.os.ShellCommand r17) {
        /*
            r16 = this;
            r1 = r16
            r0 = 0
            r2 = 0
            r3 = -2
            r4 = r3
            r3 = r2
            r2 = r0
        L_0x0008:
            java.lang.String r0 = r17.getNextOption()
            r5 = 0
            r6 = 1
            if (r0 != 0) goto L_0x0086
            android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r7 = r1.mMethodMap
            monitor-enter(r7)
            java.io.PrintWriter r0 = r17.getOutPrintWriter()     // Catch:{ all -> 0x0083 }
            com.android.server.inputmethod.InputMethodUtils$InputMethodSettings r8 = r1.mSettings     // Catch:{ all -> 0x0083 }
            int r8 = r8.getCurrentUserId()     // Catch:{ all -> 0x0083 }
            java.io.PrintWriter r9 = r17.getErrPrintWriter()     // Catch:{ all -> 0x0083 }
            int[] r8 = com.android.server.inputmethod.InputMethodUtils.resolveUserId(r4, r8, r9)     // Catch:{ all -> 0x0083 }
            int r9 = r8.length     // Catch:{ all -> 0x0083 }
            r10 = r5
        L_0x0028:
            if (r10 >= r9) goto L_0x0081
            r11 = r8[r10]     // Catch:{ all -> 0x0083 }
            if (r2 == 0) goto L_0x0033
            java.util.List r12 = r1.getInputMethodListLocked(r11)     // Catch:{ all -> 0x0083 }
            goto L_0x0037
        L_0x0033:
            java.util.List r12 = r1.getEnabledInputMethodListLocked(r11)     // Catch:{ all -> 0x0083 }
        L_0x0037:
            int r13 = r8.length     // Catch:{ all -> 0x0083 }
            if (r13 <= r6) goto L_0x0048
            java.lang.String r13 = "User #"
            r0.print(r13)     // Catch:{ all -> 0x0083 }
            r0.print(r11)     // Catch:{ all -> 0x0083 }
            java.lang.String r13 = ":"
            r0.println(r13)     // Catch:{ all -> 0x0083 }
        L_0x0048:
            java.util.Iterator r13 = r12.iterator()     // Catch:{ all -> 0x0083 }
        L_0x004c:
            boolean r14 = r13.hasNext()     // Catch:{ all -> 0x0083 }
            if (r14 == 0) goto L_0x007d
            java.lang.Object r14 = r13.next()     // Catch:{ all -> 0x0083 }
            android.view.inputmethod.InputMethodInfo r14 = (android.view.inputmethod.InputMethodInfo) r14     // Catch:{ all -> 0x0083 }
            if (r3 == 0) goto L_0x0062
            java.lang.String r15 = r14.getId()     // Catch:{ all -> 0x0083 }
            r0.println(r15)     // Catch:{ all -> 0x0083 }
            goto L_0x007b
        L_0x0062:
            java.lang.String r15 = r14.getId()     // Catch:{ all -> 0x0083 }
            r0.print(r15)     // Catch:{ all -> 0x0083 }
            java.lang.String r15 = ":"
            r0.println(r15)     // Catch:{ all -> 0x0083 }
            java.util.Objects.requireNonNull(r0)     // Catch:{ all -> 0x0083 }
            com.android.server.inputmethod.-$$Lambda$Z2NtIIfW6UZqUgiVBM1fNETGPS8 r15 = new com.android.server.inputmethod.-$$Lambda$Z2NtIIfW6UZqUgiVBM1fNETGPS8     // Catch:{ all -> 0x0083 }
            r15.<init>(r0)     // Catch:{ all -> 0x0083 }
            java.lang.String r6 = "  "
            r14.dump(r15, r6)     // Catch:{ all -> 0x0083 }
        L_0x007b:
            r6 = 1
            goto L_0x004c
        L_0x007d:
            int r10 = r10 + 1
            r6 = 1
            goto L_0x0028
        L_0x0081:
            monitor-exit(r7)     // Catch:{ all -> 0x0083 }
            return r5
        L_0x0083:
            r0 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x0083 }
            throw r0
        L_0x0086:
            r6 = -1
            int r7 = r0.hashCode()
            r8 = 1492(0x5d4, float:2.091E-42)
            r9 = 3
            r10 = 2
            if (r7 == r8) goto L_0x00bd
            r5 = 1510(0x5e6, float:2.116E-42)
            if (r7 == r5) goto L_0x00b3
            r5 = 1512(0x5e8, float:2.119E-42)
            if (r7 == r5) goto L_0x00a9
            r5 = 1333469547(0x4f7b216b, float:4.2132713E9)
            if (r7 == r5) goto L_0x009f
        L_0x009e:
            goto L_0x00c6
        L_0x009f:
            java.lang.String r5 = "--user"
            boolean r5 = r0.equals(r5)
            if (r5 == 0) goto L_0x009e
            r5 = r9
            goto L_0x00c7
        L_0x00a9:
            java.lang.String r5 = "-u"
            boolean r5 = r0.equals(r5)
            if (r5 == 0) goto L_0x009e
            r5 = r10
            goto L_0x00c7
        L_0x00b3:
            java.lang.String r5 = "-s"
            boolean r5 = r0.equals(r5)
            if (r5 == 0) goto L_0x009e
            r5 = 1
            goto L_0x00c7
        L_0x00bd:
            java.lang.String r7 = "-a"
            boolean r7 = r0.equals(r7)
            if (r7 == 0) goto L_0x009e
            goto L_0x00c7
        L_0x00c6:
            r5 = r6
        L_0x00c7:
            if (r5 == 0) goto L_0x00dc
            r6 = 1
            if (r5 == r6) goto L_0x00da
            if (r5 == r10) goto L_0x00d1
            if (r5 == r9) goto L_0x00d1
            goto L_0x00de
        L_0x00d1:
            java.lang.String r5 = r17.getNextArgRequired()
            int r4 = android.os.UserHandle.parseUserArg(r5)
            goto L_0x00de
        L_0x00da:
            r3 = 1
            goto L_0x00de
        L_0x00dc:
            r2 = 1
        L_0x00de:
            goto L_0x0008
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.InputMethodManagerService.handleShellCommandListInputMethods(android.os.ShellCommand):int");
    }

    /* access modifiers changed from: private */
    public int handleShellCommandEnableDisableInputMethod(ShellCommand shellCommand, boolean enabled) {
        int userIdToBeResolved = handleOptionsForCommandsThatOnlyHaveUserOption(shellCommand);
        String imeId = shellCommand.getNextArgRequired();
        PrintWriter out = shellCommand.getOutPrintWriter();
        PrintWriter error = shellCommand.getErrPrintWriter();
        synchronized (this.mMethodMap) {
            for (int userId : InputMethodUtils.resolveUserId(userIdToBeResolved, this.mSettings.getCurrentUserId(), shellCommand.getErrPrintWriter())) {
                if (userHasDebugPriv(userId, shellCommand)) {
                    int i = userId;
                    handleShellCommandEnableDisableInputMethodInternalLocked(userId, imeId, enabled, out, error);
                }
            }
        }
        return 0;
    }

    private static int handleOptionsForCommandsThatOnlyHaveUserOption(ShellCommand shellCommand) {
        char c;
        do {
            String nextOption = shellCommand.getNextOption();
            if (nextOption != null) {
                c = 65535;
                int hashCode = nextOption.hashCode();
                if (hashCode != 1512) {
                    if (hashCode == 1333469547 && nextOption.equals("--user")) {
                        c = 1;
                    }
                } else if (nextOption.equals("-u")) {
                    c = 0;
                }
                if (c == 0) {
                    break;
                }
            } else {
                return -2;
            }
        } while (c != 1);
        return UserHandle.parseUserArg(shellCommand.getNextArgRequired());
    }

    private void handleShellCommandEnableDisableInputMethodInternalLocked(int userId, String imeId, boolean enabled, PrintWriter out, PrintWriter error) {
        int i = userId;
        String str = imeId;
        boolean z = enabled;
        PrintWriter printWriter = out;
        PrintWriter printWriter2 = error;
        boolean failedToEnableUnknownIme = false;
        boolean previouslyEnabled = false;
        if (i != this.mSettings.getCurrentUserId()) {
            ArrayMap<String, InputMethodInfo> methodMap = new ArrayMap<>();
            ArrayList arrayList = new ArrayList();
            ArrayMap arrayMap = new ArrayMap();
            AdditionalSubtypeUtils.load(arrayMap, i);
            queryInputMethodServicesInternal(this.mContext, i, arrayMap, methodMap, arrayList);
            ArrayMap arrayMap2 = arrayMap;
            InputMethodUtils.InputMethodSettings settings = new InputMethodUtils.InputMethodSettings(this.mContext.getResources(), this.mContext.getContentResolver(), methodMap, userId, false);
            if (!z) {
                previouslyEnabled = settings.buildAndPutEnabledInputMethodsStrRemovingIdLocked(new StringBuilder(), settings.getEnabledInputMethodsAndSubtypeListLocked(), str);
            } else if (!methodMap.containsKey(str)) {
                failedToEnableUnknownIme = true;
            } else {
                Iterator<InputMethodInfo> it = settings.getEnabledInputMethodListLocked().iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (TextUtils.equals(it.next().getId(), str)) {
                            previouslyEnabled = true;
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (!previouslyEnabled) {
                    settings.appendAndPutEnabledInputMethodLocked(str, false);
                }
            }
        } else if (!z || this.mMethodMap.containsKey(str)) {
            previouslyEnabled = setInputMethodEnabledLocked(str, z);
        } else {
            failedToEnableUnknownIme = true;
        }
        if (failedToEnableUnknownIme) {
            printWriter2.print("Unknown input method ");
            printWriter2.print(str);
            printWriter2.println(" cannot be enabled for user #" + i);
            return;
        }
        printWriter.print("Input method ");
        printWriter.print(str);
        printWriter.print(": ");
        printWriter.print(z == previouslyEnabled ? "already " : "now ");
        printWriter.print(z ? "enabled" : "disabled");
        printWriter.print(" for user #");
        printWriter.println(i);
    }

    /* access modifiers changed from: private */
    public int handleShellCommandSetInputMethod(ShellCommand shellCommand) {
        int[] userIds;
        int userIdToBeResolved = handleOptionsForCommandsThatOnlyHaveUserOption(shellCommand);
        String imeId = shellCommand.getNextArgRequired();
        PrintWriter out = shellCommand.getOutPrintWriter();
        PrintWriter error = shellCommand.getErrPrintWriter();
        synchronized (this.mMethodMap) {
            int[] userIds2 = InputMethodUtils.resolveUserId(userIdToBeResolved, this.mSettings.getCurrentUserId(), shellCommand.getErrPrintWriter());
            int length = userIds2.length;
            int i = 0;
            while (i < length) {
                int userId = userIds2[i];
                if (!userHasDebugPriv(userId, shellCommand)) {
                    userIds = userIds2;
                } else {
                    boolean failedToSelectUnknownIme = false;
                    if (userId != this.mSettings.getCurrentUserId()) {
                        ArrayMap<String, InputMethodInfo> methodMap = new ArrayMap<>();
                        ArrayList arrayList = new ArrayList();
                        ArrayMap<String, List<InputMethodSubtype>> additionalSubtypeMap = new ArrayMap<>();
                        AdditionalSubtypeUtils.load(additionalSubtypeMap, userId);
                        queryInputMethodServicesInternal(this.mContext, userId, additionalSubtypeMap, methodMap, arrayList);
                        ArrayMap<String, List<InputMethodSubtype>> arrayMap = additionalSubtypeMap;
                        ArrayList arrayList2 = arrayList;
                        Resources resources = this.mContext.getResources();
                        ArrayMap<String, InputMethodInfo> methodMap2 = methodMap;
                        userIds = userIds2;
                        InputMethodUtils.InputMethodSettings settings = new InputMethodUtils.InputMethodSettings(resources, this.mContext.getContentResolver(), methodMap2, userId, false);
                        if (methodMap2.containsKey(imeId)) {
                            settings.putSelectedInputMethod(imeId);
                            settings.putSelectedSubtype(-1);
                        } else {
                            failedToSelectUnknownIme = true;
                        }
                    } else if (this.mMethodMap.containsKey(imeId)) {
                        setInputMethodLocked(imeId, -1);
                        userIds = userIds2;
                    } else {
                        failedToSelectUnknownIme = true;
                        userIds = userIds2;
                    }
                    if (failedToSelectUnknownIme) {
                        error.print("Unknown input method ");
                        error.print(imeId);
                        error.print(" cannot be selected for user #");
                        error.println(userId);
                    } else {
                        out.print("Input method ");
                        out.print(imeId);
                        out.print(" selected for user #");
                        out.println(userId);
                    }
                }
                i++;
                userIds2 = userIds;
            }
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public void removeCustomTitle() {
        this.mDialogBuilder.setCustomTitle((View) null);
        this.mDialogBuilder.setTitle(17041077);
        this.mSwitchingDialogTitleView = null;
    }

    /* access modifiers changed from: private */
    public int handleShellCommandResetInputMethod(ShellCommand shellCommand) {
        List<InputMethodInfo> nextEnabledImes;
        String nextIme;
        PrintWriter out = shellCommand.getOutPrintWriter();
        int userIdToBeResolved = handleOptionsForCommandsThatOnlyHaveUserOption(shellCommand);
        synchronized (this.mMethodMap) {
            try {
                int[] userIds = InputMethodUtils.resolveUserId(userIdToBeResolved, this.mSettings.getCurrentUserId(), shellCommand.getErrPrintWriter());
                int length = userIds.length;
                int i = 0;
                int i2 = 0;
                while (i2 < length) {
                    int userId = userIds[i2];
                    if (userHasDebugPriv(userId, shellCommand)) {
                        if (userId == this.mSettings.getCurrentUserId()) {
                            hideCurrentInputLocked(i, (ResultReceiver) null);
                            unbindCurrentMethodLocked();
                            resetSelectedInputMethodAndSubtypeLocked((String) null);
                            this.mSettings.putSelectedInputMethod((String) null);
                            this.mSettings.getEnabledInputMethodListLocked().forEach(new Consumer() {
                                public final void accept(Object obj) {
                                    InputMethodManagerService.this.lambda$handleShellCommandResetInputMethod$1$InputMethodManagerService((InputMethodInfo) obj);
                                }
                            });
                            InputMethodUtils.getDefaultEnabledImes(this.mContext, this.mMethodList).forEach(new Consumer() {
                                public final void accept(Object obj) {
                                    InputMethodManagerService.this.lambda$handleShellCommandResetInputMethod$2$InputMethodManagerService((InputMethodInfo) obj);
                                }
                            });
                            updateInputMethodsFromSettingsLocked(true);
                            InputMethodUtils.setNonSelectedSystemImesDisabledUntilUsed(this.mIPackageManager, this.mSettings.getEnabledInputMethodListLocked(), this.mSettings.getCurrentUserId(), this.mContext.getBasePackageName());
                            nextIme = this.mSettings.getSelectedInputMethod();
                            nextEnabledImes = this.mSettings.getEnabledInputMethodListLocked();
                        } else {
                            ArrayMap<String, List<InputMethodSubtype>> methodMap = new ArrayMap<>();
                            ArrayList<InputMethodInfo> methodList = new ArrayList<>();
                            ArrayMap arrayMap = new ArrayMap();
                            AdditionalSubtypeUtils.load(arrayMap, userId);
                            queryInputMethodServicesInternal(this.mContext, userId, arrayMap, methodMap, methodList);
                            ArrayMap arrayMap2 = arrayMap;
                            ArrayMap<String, List<InputMethodSubtype>> arrayMap3 = methodMap;
                            InputMethodUtils.InputMethodSettings settings = new InputMethodUtils.InputMethodSettings(this.mContext.getResources(), this.mContext.getContentResolver(), methodMap, userId, false);
                            nextEnabledImes = InputMethodUtils.getDefaultEnabledImes(this.mContext, methodList);
                            String nextIme2 = InputMethodUtils.getMostApplicableDefaultIME(nextEnabledImes).getId();
                            settings.putEnabledInputMethodsStr("");
                            nextEnabledImes.forEach(new Consumer() {
                                public final void accept(Object obj) {
                                    InputMethodUtils.InputMethodSettings.this.appendAndPutEnabledInputMethodLocked(((InputMethodInfo) obj).getId(), false);
                                }
                            });
                            settings.putSelectedInputMethod(nextIme2);
                            settings.putSelectedSubtype(-1);
                            nextIme = nextIme2;
                        }
                        out.println("Reset current and enabled IMEs for user #" + userId);
                        out.println("  Selected: " + nextIme);
                        nextEnabledImes.forEach(new Consumer(out) {
                            private final /* synthetic */ PrintWriter f$0;

                            {
                                this.f$0 = r1;
                            }

                            public final void accept(Object obj) {
                                this.f$0.println("   Enabled: " + ((InputMethodInfo) obj).getId());
                            }
                        });
                    }
                    i2++;
                    i = 0;
                }
                ShellCommand shellCommand2 = shellCommand;
                return 0;
            } catch (Throwable th) {
                th = th;
                throw th;
            }
        }
    }

    public /* synthetic */ void lambda$handleShellCommandResetInputMethod$1$InputMethodManagerService(InputMethodInfo imi) {
        setInputMethodEnabledLocked(imi.getId(), false);
    }

    public /* synthetic */ void lambda$handleShellCommandResetInputMethod$2$InputMethodManagerService(InputMethodInfo imi) {
        setInputMethodEnabledLocked(imi.getId(), true);
    }

    private boolean userHasDebugPriv(int userId, ShellCommand shellCommand) {
        if (!this.mUserManager.hasUserRestriction("no_debugging_features", UserHandle.of(userId))) {
            return true;
        }
        PrintWriter errPrintWriter = shellCommand.getErrPrintWriter();
        errPrintWriter.println("User #" + userId + " is restricted with DISALLOW_DEBUGGING_FEATURES.");
        return false;
    }

    private static final class InputMethodPrivilegedOperationsImpl extends IInputMethodPrivilegedOperations.Stub {
        private final InputMethodManagerService mImms;
        private final IBinder mToken;

        InputMethodPrivilegedOperationsImpl(InputMethodManagerService imms, IBinder token) {
            this.mImms = imms;
            this.mToken = token;
        }

        public void setImeWindowStatus(int vis, int backDisposition) {
            this.mImms.setImeWindowStatus(this.mToken, vis, backDisposition);
        }

        public void reportStartInput(IBinder startInputToken) {
            this.mImms.reportStartInput(this.mToken, startInputToken);
        }

        public IInputContentUriToken createInputContentUriToken(Uri contentUri, String packageName) {
            return this.mImms.createInputContentUriToken(this.mToken, contentUri, packageName);
        }

        public void reportFullscreenMode(boolean fullscreen) {
            this.mImms.reportFullscreenMode(this.mToken, fullscreen);
        }

        public void setInputMethod(String id) {
            this.mImms.setInputMethod(this.mToken, id);
        }

        public void setInputMethodAndSubtype(String id, InputMethodSubtype subtype) {
            this.mImms.setInputMethodAndSubtype(this.mToken, id, subtype);
        }

        public void hideMySoftInput(int flags) {
            this.mImms.hideMySoftInput(this.mToken, flags);
        }

        public void showMySoftInput(int flags) {
            this.mImms.showMySoftInput(this.mToken, flags);
        }

        public void updateStatusIcon(String packageName, int iconId) {
            this.mImms.updateStatusIcon(this.mToken, packageName, iconId);
        }

        public boolean switchToPreviousInputMethod() {
            return this.mImms.switchToPreviousInputMethod(this.mToken);
        }

        public boolean switchToNextInputMethod(boolean onlyCurrentIme) {
            return this.mImms.switchToNextInputMethod(this.mToken, onlyCurrentIme);
        }

        public boolean shouldOfferSwitchingToNextInputMethod() {
            return this.mImms.shouldOfferSwitchingToNextInputMethod(this.mToken);
        }

        public void notifyUserAction() {
            this.mImms.notifyUserAction(this.mToken);
        }

        public void reportPreRendered(EditorInfo info) {
            this.mImms.reportPreRendered(this.mToken, info);
        }

        public void applyImeVisibility(boolean setVisible) {
            this.mImms.applyImeVisibility(this.mToken, setVisible);
        }
    }

    public boolean isTokenValid(IBinder token) {
        boolean calledWithValidTokenLocked;
        synchronized (this.mMethodMap) {
            calledWithValidTokenLocked = calledWithValidTokenLocked(token);
        }
        return calledWithValidTokenLocked;
    }
}
