package com.android.server.display;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.provider.Settings;
import android.util.Slog;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceControl;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.display.DisplayAdapter;
import com.android.server.display.DisplayManagerService;
import com.android.server.display.OverlayDisplayWindow;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class OverlayDisplayAdapter extends DisplayAdapter {
    static final boolean DEBUG = false;
    private static final Pattern DISPLAY_PATTERN = Pattern.compile("([^,]+)(,[a-z]+)*");
    private static final int MAX_HEIGHT = 4096;
    private static final int MAX_WIDTH = 4096;
    private static final int MIN_HEIGHT = 100;
    private static final int MIN_WIDTH = 100;
    private static final Pattern MODE_PATTERN = Pattern.compile("(\\d+)x(\\d+)/(\\d+)");
    static final String TAG = "OverlayDisplayAdapter";
    private static final String UNIQUE_ID_PREFIX = "overlay:";
    private String mCurrentOverlaySetting = "";
    private final ArrayList<OverlayDisplayHandle> mOverlays = new ArrayList<>();
    /* access modifiers changed from: private */
    public final Handler mUiHandler;

    public OverlayDisplayAdapter(DisplayManagerService.SyncRoot syncRoot, Context context, Handler handler, DisplayAdapter.Listener listener, Handler uiHandler) {
        super(syncRoot, context, handler, listener, TAG);
        this.mUiHandler = uiHandler;
    }

    public void dumpLocked(PrintWriter pw) {
        super.dumpLocked(pw);
        pw.println("mCurrentOverlaySetting=" + this.mCurrentOverlaySetting);
        pw.println("mOverlays: size=" + this.mOverlays.size());
        Iterator<OverlayDisplayHandle> it = this.mOverlays.iterator();
        while (it.hasNext()) {
            it.next().dumpLocked(pw);
        }
    }

    public void registerLocked() {
        super.registerLocked();
        getHandler().post(new Runnable() {
            public void run() {
                OverlayDisplayAdapter.this.getContext().getContentResolver().registerContentObserver(Settings.Global.getUriFor("overlay_display_devices"), true, new ContentObserver(OverlayDisplayAdapter.this.getHandler()) {
                    public void onChange(boolean selfChange) {
                        OverlayDisplayAdapter.this.updateOverlayDisplayDevices();
                    }
                });
                OverlayDisplayAdapter.this.updateOverlayDisplayDevices();
            }
        });
    }

    /* access modifiers changed from: private */
    public void updateOverlayDisplayDevices() {
        synchronized (getSyncRoot()) {
            updateOverlayDisplayDevicesLocked();
        }
    }

    private void updateOverlayDisplayDevicesLocked() {
        String value;
        String[] strArr;
        Matcher displayMatcher;
        String[] strArr2;
        String value2 = Settings.Global.getString(getContext().getContentResolver(), "overlay_display_devices");
        if (value2 == null) {
            value = "";
        } else {
            value = value2;
        }
        if (!value.equals(this.mCurrentOverlaySetting)) {
            this.mCurrentOverlaySetting = value;
            if (!this.mOverlays.isEmpty()) {
                Slog.i(TAG, "Dismissing all overlay display devices.");
                Iterator<OverlayDisplayHandle> it = this.mOverlays.iterator();
                while (it.hasNext()) {
                    it.next().dismissLocked();
                }
                this.mOverlays.clear();
            }
            String[] split = value.split(";");
            int length = split.length;
            int count = 0;
            int i = 0;
            while (i < length) {
                Matcher displayMatcher2 = DISPLAY_PATTERN.matcher(split[i]);
                if (!displayMatcher2.matches()) {
                    strArr = split;
                } else if (count >= 4) {
                    Slog.w(TAG, "Too many overlay display devices specified: " + value);
                    return;
                } else {
                    String modeString = displayMatcher2.group(1);
                    String flagString = displayMatcher2.group(2);
                    ArrayList<OverlayMode> modes = new ArrayList<>();
                    String[] split2 = modeString.split("\\|");
                    int length2 = split2.length;
                    int i2 = 0;
                    while (i2 < length2) {
                        int i3 = length2;
                        String mode = split2[i2];
                        String modeString2 = modeString;
                        Matcher modeMatcher = MODE_PATTERN.matcher(mode);
                        if (modeMatcher.matches()) {
                            displayMatcher = displayMatcher2;
                            try {
                                int width = Integer.parseInt(modeMatcher.group(1), 10);
                                strArr2 = split;
                                try {
                                    int height = Integer.parseInt(modeMatcher.group(2), 10);
                                    Matcher matcher = modeMatcher;
                                    try {
                                        int densityDpi = Integer.parseInt(modeMatcher.group(3), 10);
                                        if (width < 100 || width > 4096 || height < 100 || height > 4096 || densityDpi < 120 || densityDpi > 640) {
                                            StringBuilder sb = new StringBuilder();
                                            int i4 = width;
                                            sb.append("Ignoring out-of-range overlay display mode: ");
                                            sb.append(mode);
                                            Slog.w(TAG, sb.toString());
                                        } else {
                                            modes.add(new OverlayMode(width, height, densityDpi));
                                        }
                                    } catch (NumberFormatException e) {
                                    }
                                } catch (NumberFormatException e2) {
                                    Matcher matcher2 = modeMatcher;
                                }
                            } catch (NumberFormatException e3) {
                                Matcher matcher3 = modeMatcher;
                                strArr2 = split;
                            }
                        } else {
                            displayMatcher = displayMatcher2;
                            strArr2 = split;
                            if (mode.isEmpty()) {
                            }
                        }
                        i2++;
                        split = strArr2;
                        length2 = i3;
                        modeString = modeString2;
                        displayMatcher2 = displayMatcher;
                    }
                    Matcher matcher4 = displayMatcher2;
                    strArr = split;
                    if (!modes.isEmpty()) {
                        int count2 = count + 1;
                        int number = count2;
                        boolean secure = true;
                        String name = getContext().getResources().getString(17039907, new Object[]{Integer.valueOf(number)});
                        int gravity = chooseOverlayGravity(number);
                        if (flagString == null || !flagString.contains(",secure")) {
                            secure = false;
                        }
                        ArrayList<OverlayMode> modes2 = modes;
                        Slog.i(TAG, "Showing overlay display device #" + number + ": name=" + name + ", modes=" + Arrays.toString(modes2.toArray()));
                        OverlayDisplayHandle overlayDisplayHandle = r1;
                        String str = flagString;
                        int count3 = count2;
                        ArrayList<OverlayDisplayHandle> arrayList = this.mOverlays;
                        String str2 = name;
                        OverlayDisplayHandle overlayDisplayHandle2 = new OverlayDisplayHandle(name, modes2, gravity, secure, number);
                        arrayList.add(overlayDisplayHandle);
                        count = count3;
                        i++;
                        split = strArr;
                    } else {
                        ArrayList<OverlayMode> arrayList2 = modes;
                    }
                }
                Slog.w(TAG, "Malformed overlay display devices setting: " + value);
                i++;
                split = strArr;
            }
        }
    }

    private static int chooseOverlayGravity(int overlayNumber) {
        if (overlayNumber == 1) {
            return 51;
        }
        if (overlayNumber == 2) {
            return 85;
        }
        if (overlayNumber != 3) {
            return 83;
        }
        return 53;
    }

    private abstract class OverlayDisplayDevice extends DisplayDevice {
        private int mActiveMode;
        private final int mDefaultMode;
        private final long mDisplayPresentationDeadlineNanos;
        private DisplayDeviceInfo mInfo;
        private final Display.Mode[] mModes;
        private final String mName;
        private final List<OverlayMode> mRawModes;
        private final float mRefreshRate;
        private final boolean mSecure;
        private int mState;
        private Surface mSurface;
        private SurfaceTexture mSurfaceTexture;
        final /* synthetic */ OverlayDisplayAdapter this$0;

        public abstract void onModeChangedLocked(int i);

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public OverlayDisplayDevice(com.android.server.display.OverlayDisplayAdapter r17, android.os.IBinder r18, java.lang.String r19, java.util.List<com.android.server.display.OverlayDisplayAdapter.OverlayMode> r20, int r21, int r22, float r23, long r24, boolean r26, int r27, android.graphics.SurfaceTexture r28, int r29) {
            /*
                r16 = this;
                r0 = r16
                r1 = r17
                r2 = r20
                r3 = r23
                r0.this$0 = r1
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "overlay:"
                r4.append(r5)
                r5 = r29
                r4.append(r5)
                java.lang.String r4 = r4.toString()
                r6 = r18
                r0.<init>(r1, r6, r4)
                r1 = r19
                r0.mName = r1
                r0.mRefreshRate = r3
                r7 = r24
                r0.mDisplayPresentationDeadlineNanos = r7
                r4 = r26
                r0.mSecure = r4
                r9 = r27
                r0.mState = r9
                r10 = r28
                r0.mSurfaceTexture = r10
                r0.mRawModes = r2
                int r11 = r20.size()
                android.view.Display$Mode[] r11 = new android.view.Display.Mode[r11]
                r0.mModes = r11
                r11 = 0
            L_0x0044:
                int r12 = r20.size()
                if (r11 >= r12) goto L_0x005f
                java.lang.Object r12 = r2.get(r11)
                com.android.server.display.OverlayDisplayAdapter$OverlayMode r12 = (com.android.server.display.OverlayDisplayAdapter.OverlayMode) r12
                android.view.Display$Mode[] r13 = r0.mModes
                int r14 = r12.mWidth
                int r15 = r12.mHeight
                android.view.Display$Mode r14 = com.android.server.display.DisplayAdapter.createMode(r14, r15, r3)
                r13[r11] = r14
                int r11 = r11 + 1
                goto L_0x0044
            L_0x005f:
                r11 = r21
                r0.mActiveMode = r11
                r12 = r22
                r0.mDefaultMode = r12
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.OverlayDisplayAdapter.OverlayDisplayDevice.<init>(com.android.server.display.OverlayDisplayAdapter, android.os.IBinder, java.lang.String, java.util.List, int, int, float, long, boolean, int, android.graphics.SurfaceTexture, int):void");
        }

        public void destroyLocked() {
            this.mSurfaceTexture = null;
            Surface surface = this.mSurface;
            if (surface != null) {
                surface.release();
                this.mSurface = null;
            }
            SurfaceControl.destroyDisplay(getDisplayTokenLocked());
        }

        public boolean hasStableUniqueId() {
            return false;
        }

        public void performTraversalLocked(SurfaceControl.Transaction t) {
            SurfaceTexture surfaceTexture = this.mSurfaceTexture;
            if (surfaceTexture != null) {
                if (this.mSurface == null) {
                    this.mSurface = new Surface(surfaceTexture);
                }
                setSurfaceLocked(t, this.mSurface);
            }
        }

        public void setStateLocked(int state) {
            this.mState = state;
            this.mInfo = null;
        }

        public DisplayDeviceInfo getDisplayDeviceInfoLocked() {
            if (this.mInfo == null) {
                Display.Mode[] modeArr = this.mModes;
                int i = this.mActiveMode;
                Display.Mode mode = modeArr[i];
                OverlayMode rawMode = this.mRawModes.get(i);
                this.mInfo = new DisplayDeviceInfo();
                DisplayDeviceInfo displayDeviceInfo = this.mInfo;
                displayDeviceInfo.name = this.mName;
                displayDeviceInfo.uniqueId = getUniqueId();
                this.mInfo.width = mode.getPhysicalWidth();
                this.mInfo.height = mode.getPhysicalHeight();
                this.mInfo.modeId = mode.getModeId();
                this.mInfo.defaultModeId = this.mModes[0].getModeId();
                DisplayDeviceInfo displayDeviceInfo2 = this.mInfo;
                displayDeviceInfo2.supportedModes = this.mModes;
                displayDeviceInfo2.densityDpi = rawMode.mDensityDpi;
                this.mInfo.xDpi = (float) rawMode.mDensityDpi;
                this.mInfo.yDpi = (float) rawMode.mDensityDpi;
                DisplayDeviceInfo displayDeviceInfo3 = this.mInfo;
                displayDeviceInfo3.presentationDeadlineNanos = this.mDisplayPresentationDeadlineNanos + (1000000000 / ((long) ((int) this.mRefreshRate)));
                displayDeviceInfo3.flags = 64;
                if (this.mSecure) {
                    displayDeviceInfo3.flags |= 4;
                }
                DisplayDeviceInfo displayDeviceInfo4 = this.mInfo;
                displayDeviceInfo4.type = 4;
                displayDeviceInfo4.touch = 3;
                displayDeviceInfo4.state = this.mState;
            }
            return this.mInfo;
        }

        public void setAllowedDisplayModesLocked(int[] modes) {
            int id;
            if (modes.length > 0) {
                id = modes[0];
            } else {
                id = 0;
            }
            int index = -1;
            if (id == 0) {
                index = 0;
            } else {
                int i = 0;
                while (true) {
                    Display.Mode[] modeArr = this.mModes;
                    if (i >= modeArr.length) {
                        break;
                    } else if (modeArr[i].getModeId() == id) {
                        index = i;
                        break;
                    } else {
                        i++;
                    }
                }
            }
            if (index == -1) {
                Slog.w(OverlayDisplayAdapter.TAG, "Unable to locate mode " + id + ", reverting to default.");
                index = this.mDefaultMode;
            }
            if (this.mActiveMode != index) {
                this.mActiveMode = index;
                this.mInfo = null;
                this.this$0.sendDisplayDeviceEventLocked(this, 2);
                onModeChangedLocked(index);
            }
        }
    }

    private final class OverlayDisplayHandle implements OverlayDisplayWindow.Listener {
        private static final int DEFAULT_MODE_INDEX = 0;
        /* access modifiers changed from: private */
        public int mActiveMode;
        private OverlayDisplayDevice mDevice;
        private final Runnable mDismissRunnable = new Runnable() {
            public void run() {
                OverlayDisplayWindow window;
                synchronized (OverlayDisplayAdapter.this.getSyncRoot()) {
                    window = OverlayDisplayHandle.this.mWindow;
                    OverlayDisplayWindow unused = OverlayDisplayHandle.this.mWindow = null;
                }
                if (window != null) {
                    window.dismiss();
                }
            }
        };
        /* access modifiers changed from: private */
        public final int mGravity;
        /* access modifiers changed from: private */
        public final List<OverlayMode> mModes;
        /* access modifiers changed from: private */
        public final String mName;
        private final int mNumber;
        private final Runnable mResizeRunnable = new Runnable() {
            public void run() {
                synchronized (OverlayDisplayAdapter.this.getSyncRoot()) {
                    if (OverlayDisplayHandle.this.mWindow != null) {
                        OverlayMode mode = (OverlayMode) OverlayDisplayHandle.this.mModes.get(OverlayDisplayHandle.this.mActiveMode);
                        OverlayDisplayWindow window = OverlayDisplayHandle.this.mWindow;
                        window.resize(mode.mWidth, mode.mHeight, mode.mDensityDpi);
                    }
                }
            }
        };
        /* access modifiers changed from: private */
        public final boolean mSecure;
        private final Runnable mShowRunnable = new Runnable() {
            public void run() {
                OverlayMode mode = (OverlayMode) OverlayDisplayHandle.this.mModes.get(OverlayDisplayHandle.this.mActiveMode);
                OverlayDisplayWindow window = new OverlayDisplayWindow(OverlayDisplayAdapter.this.getContext(), OverlayDisplayHandle.this.mName, mode.mWidth, mode.mHeight, mode.mDensityDpi, OverlayDisplayHandle.this.mGravity, OverlayDisplayHandle.this.mSecure, OverlayDisplayHandle.this);
                window.show();
                synchronized (OverlayDisplayAdapter.this.getSyncRoot()) {
                    OverlayDisplayWindow unused = OverlayDisplayHandle.this.mWindow = window;
                }
            }
        };
        /* access modifiers changed from: private */
        public OverlayDisplayWindow mWindow;

        public OverlayDisplayHandle(String name, List<OverlayMode> modes, int gravity, boolean secure, int number) {
            this.mName = name;
            this.mModes = modes;
            this.mGravity = gravity;
            this.mSecure = secure;
            this.mNumber = number;
            this.mActiveMode = 0;
            showLocked();
        }

        private void showLocked() {
            OverlayDisplayAdapter.this.mUiHandler.post(this.mShowRunnable);
        }

        public void dismissLocked() {
            OverlayDisplayAdapter.this.mUiHandler.removeCallbacks(this.mShowRunnable);
            OverlayDisplayAdapter.this.mUiHandler.post(this.mDismissRunnable);
        }

        /* access modifiers changed from: private */
        public void onActiveModeChangedLocked(int index) {
            OverlayDisplayAdapter.this.mUiHandler.removeCallbacks(this.mResizeRunnable);
            this.mActiveMode = index;
            if (this.mWindow != null) {
                OverlayDisplayAdapter.this.mUiHandler.post(this.mResizeRunnable);
            }
        }

        public void onWindowCreated(SurfaceTexture surfaceTexture, float refreshRate, long presentationDeadlineNanos, int state) {
            synchronized (OverlayDisplayAdapter.this.getSyncRoot()) {
                this.mDevice = new OverlayDisplayDevice(this, SurfaceControl.createDisplay(this.mName, this.mSecure), this.mName, this.mModes, this.mActiveMode, 0, refreshRate, presentationDeadlineNanos, this.mSecure, state, surfaceTexture, this.mNumber) {
                    final /* synthetic */ OverlayDisplayHandle this$1;

                    {
                        OverlayDisplayHandle overlayDisplayHandle = this$1;
                        this.this$1 = overlayDisplayHandle;
                    }

                    public void onModeChangedLocked(int index) {
                        this.this$1.onActiveModeChangedLocked(index);
                    }
                };
                OverlayDisplayAdapter.this.sendDisplayDeviceEventLocked(this.mDevice, 1);
            }
        }

        public void onWindowDestroyed() {
            synchronized (OverlayDisplayAdapter.this.getSyncRoot()) {
                if (this.mDevice != null) {
                    this.mDevice.destroyLocked();
                    OverlayDisplayAdapter.this.sendDisplayDeviceEventLocked(this.mDevice, 3);
                }
            }
        }

        public void onStateChanged(int state) {
            synchronized (OverlayDisplayAdapter.this.getSyncRoot()) {
                if (this.mDevice != null) {
                    this.mDevice.setStateLocked(state);
                    OverlayDisplayAdapter.this.sendDisplayDeviceEventLocked(this.mDevice, 2);
                }
            }
        }

        public void dumpLocked(PrintWriter pw) {
            pw.println("  " + this.mName + ":");
            StringBuilder sb = new StringBuilder();
            sb.append("    mModes=");
            sb.append(Arrays.toString(this.mModes.toArray()));
            pw.println(sb.toString());
            pw.println("    mActiveMode=" + this.mActiveMode);
            pw.println("    mGravity=" + this.mGravity);
            pw.println("    mSecure=" + this.mSecure);
            pw.println("    mNumber=" + this.mNumber);
            if (this.mWindow != null) {
                IndentingPrintWriter ipw = new IndentingPrintWriter(pw, "    ");
                ipw.increaseIndent();
                DumpUtils.dumpAsync(OverlayDisplayAdapter.this.mUiHandler, this.mWindow, ipw, "", 200);
            }
        }
    }

    private static final class OverlayMode {
        final int mDensityDpi;
        final int mHeight;
        final int mWidth;

        OverlayMode(int width, int height, int densityDpi) {
            this.mWidth = width;
            this.mHeight = height;
            this.mDensityDpi = densityDpi;
        }

        public String toString() {
            return "{" + "width=" + this.mWidth + ", height=" + this.mHeight + ", densityDpi=" + this.mDensityDpi + "}";
        }
    }
}
