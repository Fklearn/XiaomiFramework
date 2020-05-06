package com.android.server.display;

import android.content.Context;
import android.hardware.display.IVirtualDisplayCallback;
import android.media.projection.IMediaProjection;
import android.media.projection.IMediaProjectionCallback;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.ArrayMap;
import android.util.Slog;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceControl;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.display.DisplayAdapter;
import com.android.server.display.DisplayManagerService;
import java.io.PrintWriter;

@VisibleForTesting
public class VirtualDisplayAdapter extends DisplayAdapter {
    static final boolean DEBUG = false;
    static final String TAG = "VirtualDisplayAdapter";
    @VisibleForTesting
    static final String UNIQUE_ID_PREFIX = "virtual:";
    private final Handler mHandler;
    private final SurfaceControlDisplayFactory mSurfaceControlDisplayFactory;
    private final ArrayMap<IBinder, VirtualDisplayDevice> mVirtualDisplayDevices;

    @VisibleForTesting
    public interface SurfaceControlDisplayFactory {
        IBinder createDisplay(String str, boolean z);
    }

    public /* bridge */ /* synthetic */ void dumpLocked(PrintWriter printWriter) {
        super.dumpLocked(printWriter);
    }

    public /* bridge */ /* synthetic */ void registerLocked() {
        super.registerLocked();
    }

    public VirtualDisplayAdapter(DisplayManagerService.SyncRoot syncRoot, Context context, Handler handler, DisplayAdapter.Listener listener) {
        this(syncRoot, context, handler, listener, $$Lambda$VirtualDisplayAdapter$PFyqeaYIEBicSVtuy5lL_bT8B0.INSTANCE);
    }

    @VisibleForTesting
    VirtualDisplayAdapter(DisplayManagerService.SyncRoot syncRoot, Context context, Handler handler, DisplayAdapter.Listener listener, SurfaceControlDisplayFactory surfaceControlDisplayFactory) {
        super(syncRoot, context, handler, listener, TAG);
        this.mVirtualDisplayDevices = new ArrayMap<>();
        this.mHandler = handler;
        this.mSurfaceControlDisplayFactory = surfaceControlDisplayFactory;
    }

    public DisplayDevice createVirtualDisplayLocked(IVirtualDisplayCallback callback, IMediaProjection projection, int ownerUid, String ownerPackageName, String name, int width, int height, int densityDpi, Surface surface, int flags, String uniqueId) {
        String uniqueId2;
        boolean z;
        IMediaProjection iMediaProjection = projection;
        String str = ownerPackageName;
        String str2 = name;
        String str3 = uniqueId;
        boolean secure = (flags & 4) != 0;
        IBinder appToken = callback.asBinder();
        IBinder displayToken = this.mSurfaceControlDisplayFactory.createDisplay(str2, secure);
        String baseUniqueId = UNIQUE_ID_PREFIX + str + "," + ownerUid + "," + str2 + ",";
        int uniqueIndex = getNextUniqueIndex(baseUniqueId);
        if (str3 == null) {
            uniqueId2 = baseUniqueId + uniqueIndex;
        } else {
            uniqueId2 = UNIQUE_ID_PREFIX + str + ":" + str3;
        }
        String str4 = baseUniqueId;
        boolean z2 = secure;
        VirtualDisplayDevice device = new VirtualDisplayDevice(this, displayToken, appToken, ownerUid, ownerPackageName, name, width, height, densityDpi, surface, flags, new Callback(callback, this.mHandler), uniqueId2, uniqueIndex);
        IBinder appToken2 = appToken;
        this.mVirtualDisplayDevices.put(appToken2, device);
        IMediaProjection iMediaProjection2 = projection;
        if (iMediaProjection2 != null) {
            try {
                iMediaProjection2.registerCallback(new MediaProjectionCallback(appToken2));
            } catch (RemoteException e) {
                z = false;
            }
        }
        z = false;
        try {
            appToken2.linkToDeath(device, 0);
            return device;
        } catch (RemoteException e2) {
        }
        this.mVirtualDisplayDevices.remove(appToken2);
        device.destroyLocked(z);
        return null;
    }

    public void resizeVirtualDisplayLocked(IBinder appToken, int width, int height, int densityDpi) {
        VirtualDisplayDevice device = this.mVirtualDisplayDevices.get(appToken);
        if (device != null) {
            device.resizeLocked(width, height, densityDpi);
        }
    }

    public void setVirtualDisplaySurfaceLocked(IBinder appToken, Surface surface) {
        VirtualDisplayDevice device = this.mVirtualDisplayDevices.get(appToken);
        if (device != null) {
            device.setSurfaceLocked(surface);
        }
    }

    public DisplayDevice releaseVirtualDisplayLocked(IBinder appToken) {
        VirtualDisplayDevice device = this.mVirtualDisplayDevices.remove(appToken);
        if (device != null) {
            device.destroyLocked(true);
            appToken.unlinkToDeath(device, 0);
        }
        return device;
    }

    /* access modifiers changed from: package-private */
    public void setVirtualDisplayStateLocked(IBinder appToken, boolean isOn) {
        VirtualDisplayDevice device = this.mVirtualDisplayDevices.get(appToken);
        if (device != null) {
            device.setDisplayState(isOn);
        }
    }

    private int getNextUniqueIndex(String uniqueIdPrefix) {
        if (this.mVirtualDisplayDevices.isEmpty()) {
            return 0;
        }
        int nextUniqueIndex = 0;
        for (VirtualDisplayDevice device : this.mVirtualDisplayDevices.values()) {
            if (device.getUniqueId().startsWith(uniqueIdPrefix) && device.mUniqueIndex >= nextUniqueIndex) {
                nextUniqueIndex = device.mUniqueIndex + 1;
            }
        }
        return nextUniqueIndex;
    }

    /* access modifiers changed from: private */
    public void handleBinderDiedLocked(IBinder appToken) {
        this.mVirtualDisplayDevices.remove(appToken);
    }

    /* access modifiers changed from: private */
    public void handleMediaProjectionStoppedLocked(IBinder appToken) {
        VirtualDisplayDevice device = this.mVirtualDisplayDevices.remove(appToken);
        if (device != null) {
            Slog.i(TAG, "Virtual display device released because media projection stopped: " + device.mName);
            device.stopLocked();
        }
    }

    private final class VirtualDisplayDevice extends DisplayDevice implements IBinder.DeathRecipient {
        private static final int PENDING_RESIZE = 2;
        private static final int PENDING_SURFACE_CHANGE = 1;
        private static final float REFRESH_RATE = 60.0f;
        private final IBinder mAppToken;
        private final Callback mCallback;
        private int mDensityDpi;
        private int mDisplayState = 0;
        private final int mFlags;
        private int mHeight;
        private DisplayDeviceInfo mInfo;
        private boolean mIsDisplayOn;
        private Display.Mode mMode;
        final String mName;
        final String mOwnerPackageName;
        private final int mOwnerUid;
        private int mPendingChanges;
        private boolean mStopped;
        private Surface mSurface;
        /* access modifiers changed from: private */
        public int mUniqueIndex;
        private int mWidth;
        final /* synthetic */ VirtualDisplayAdapter this$0;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public VirtualDisplayDevice(com.android.server.display.VirtualDisplayAdapter r17, android.os.IBinder r18, android.os.IBinder r19, int r20, java.lang.String r21, java.lang.String r22, int r23, int r24, int r25, android.view.Surface r26, int r27, com.android.server.display.VirtualDisplayAdapter.Callback r28, java.lang.String r29, int r30) {
            /*
                r16 = this;
                r0 = r16
                r1 = r17
                r2 = r23
                r3 = r24
                r4 = r26
                r0.this$0 = r1
                r5 = r18
                r6 = r29
                r0.<init>(r1, r5, r6)
                r1 = r19
                r0.mAppToken = r1
                r7 = r20
                r0.mOwnerUid = r7
                r8 = r21
                r0.mOwnerPackageName = r8
                r9 = r22
                r0.mName = r9
                r0.mWidth = r2
                r0.mHeight = r3
                r10 = 1114636288(0x42700000, float:60.0)
                android.view.Display$Mode r10 = com.android.server.display.DisplayAdapter.createMode(r2, r3, r10)
                r0.mMode = r10
                r10 = r25
                r0.mDensityDpi = r10
                r0.mSurface = r4
                r11 = r27
                r0.mFlags = r11
                r12 = r28
                r0.mCallback = r12
                r13 = 0
                r0.mDisplayState = r13
                int r14 = r0.mPendingChanges
                r15 = 1
                r14 = r14 | r15
                r0.mPendingChanges = r14
                r14 = r30
                r0.mUniqueIndex = r14
                if (r4 == 0) goto L_0x004d
                r13 = r15
            L_0x004d:
                r0.mIsDisplayOn = r13
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.VirtualDisplayAdapter.VirtualDisplayDevice.<init>(com.android.server.display.VirtualDisplayAdapter, android.os.IBinder, android.os.IBinder, int, java.lang.String, java.lang.String, int, int, int, android.view.Surface, int, com.android.server.display.VirtualDisplayAdapter$Callback, java.lang.String, int):void");
        }

        public void binderDied() {
            synchronized (this.this$0.getSyncRoot()) {
                this.this$0.handleBinderDiedLocked(this.mAppToken);
                Slog.i(VirtualDisplayAdapter.TAG, "Virtual display device released because application token died: " + this.mOwnerPackageName);
                destroyLocked(false);
                this.this$0.sendDisplayDeviceEventLocked(this, 3);
            }
        }

        public void destroyLocked(boolean binderAlive) {
            Surface surface = this.mSurface;
            if (surface != null) {
                surface.release();
                this.mSurface = null;
            }
            SurfaceControl.destroyDisplay(getDisplayTokenLocked());
            if (binderAlive) {
                this.mCallback.dispatchDisplayStopped();
            }
        }

        public boolean hasStableUniqueId() {
            return false;
        }

        public Runnable requestDisplayStateLocked(int state, int brightness) {
            if (state == this.mDisplayState) {
                return null;
            }
            this.mDisplayState = state;
            if (state == 1) {
                this.mCallback.dispatchDisplayPaused();
                return null;
            }
            this.mCallback.dispatchDisplayResumed();
            return null;
        }

        public void performTraversalLocked(SurfaceControl.Transaction t) {
            if ((this.mPendingChanges & 2) != 0) {
                t.setDisplaySize(getDisplayTokenLocked(), this.mWidth, this.mHeight);
            }
            if ((this.mPendingChanges & 1) != 0) {
                setSurfaceLocked(t, this.mSurface);
                if ((this.mFlags & Integer.MIN_VALUE) != 0) {
                    setDiffScreenProjection(t, true);
                }
            }
            this.mPendingChanges = 0;
        }

        public void setSurfaceLocked(Surface surface) {
            Surface surface2;
            if (!this.mStopped && (surface2 = this.mSurface) != surface) {
                boolean z = false;
                boolean z2 = surface2 != null;
                if (surface != null) {
                    z = true;
                }
                if (z2 != z) {
                    this.this$0.sendDisplayDeviceEventLocked(this, 2);
                }
                this.this$0.sendTraversalRequestLocked();
                this.mSurface = surface;
                this.mInfo = null;
                this.mPendingChanges |= 1;
            }
        }

        public void resizeLocked(int width, int height, int densityDpi) {
            if (this.mWidth != width || this.mHeight != height || this.mDensityDpi != densityDpi) {
                this.this$0.sendDisplayDeviceEventLocked(this, 2);
                this.this$0.sendTraversalRequestLocked();
                this.mWidth = width;
                this.mHeight = height;
                this.mMode = DisplayAdapter.createMode(width, height, REFRESH_RATE);
                this.mDensityDpi = densityDpi;
                this.mInfo = null;
                this.mPendingChanges |= 2;
            }
        }

        /* access modifiers changed from: package-private */
        public void setDisplayState(boolean isOn) {
            if (this.mIsDisplayOn != isOn) {
                this.mIsDisplayOn = isOn;
                this.mInfo = null;
                this.this$0.sendDisplayDeviceEventLocked(this, 2);
            }
        }

        public void stopLocked() {
            setSurfaceLocked((Surface) null);
            this.mStopped = true;
        }

        public void dumpLocked(PrintWriter pw) {
            super.dumpLocked(pw);
            pw.println("mFlags=" + this.mFlags);
            pw.println("mDisplayState=" + Display.stateToString(this.mDisplayState));
            pw.println("mStopped=" + this.mStopped);
        }

        public DisplayDeviceInfo getDisplayDeviceInfoLocked() {
            if (this.mInfo == null) {
                this.mInfo = new DisplayDeviceInfo();
                DisplayDeviceInfo displayDeviceInfo = this.mInfo;
                displayDeviceInfo.name = this.mName;
                displayDeviceInfo.uniqueId = getUniqueId();
                DisplayDeviceInfo displayDeviceInfo2 = this.mInfo;
                displayDeviceInfo2.width = this.mWidth;
                displayDeviceInfo2.height = this.mHeight;
                displayDeviceInfo2.modeId = this.mMode.getModeId();
                this.mInfo.defaultModeId = this.mMode.getModeId();
                DisplayDeviceInfo displayDeviceInfo3 = this.mInfo;
                int i = 1;
                displayDeviceInfo3.supportedModes = new Display.Mode[]{this.mMode};
                int i2 = this.mDensityDpi;
                displayDeviceInfo3.densityDpi = i2;
                displayDeviceInfo3.xDpi = (float) i2;
                displayDeviceInfo3.yDpi = (float) i2;
                displayDeviceInfo3.presentationDeadlineNanos = 16666666;
                displayDeviceInfo3.flags = 0;
                if ((this.mFlags & 1) == 0) {
                    displayDeviceInfo3.flags |= 48;
                }
                if ((this.mFlags & 16) != 0) {
                    this.mInfo.flags &= -33;
                } else {
                    this.mInfo.flags |= 128;
                }
                if ((this.mFlags & 4) != 0) {
                    this.mInfo.flags |= 4;
                }
                int i3 = 3;
                if ((this.mFlags & 2) != 0) {
                    this.mInfo.flags |= 64;
                    if ((this.mFlags & 1) != 0 && "portrait".equals(SystemProperties.get("persist.demo.remoterotation"))) {
                        this.mInfo.rotation = 3;
                    }
                }
                if ((this.mFlags & 32) != 0) {
                    this.mInfo.flags |= 512;
                }
                if ((this.mFlags & 128) != 0) {
                    this.mInfo.flags |= 2;
                }
                if ((this.mFlags & 256) != 0) {
                    this.mInfo.flags |= 1024;
                }
                if ((this.mFlags & 512) != 0) {
                    this.mInfo.flags |= 4096;
                }
                DisplayDeviceInfo displayDeviceInfo4 = this.mInfo;
                displayDeviceInfo4.type = 5;
                if ((this.mFlags & 64) == 0) {
                    i3 = 0;
                }
                displayDeviceInfo4.touch = i3;
                DisplayDeviceInfo displayDeviceInfo5 = this.mInfo;
                if (this.mIsDisplayOn) {
                    i = 2;
                }
                displayDeviceInfo5.state = i;
                DisplayDeviceInfo displayDeviceInfo6 = this.mInfo;
                displayDeviceInfo6.ownerUid = this.mOwnerUid;
                displayDeviceInfo6.ownerPackageName = this.mOwnerPackageName;
            }
            return this.mInfo;
        }
    }

    private static class Callback extends Handler {
        private static final int MSG_ON_DISPLAY_PAUSED = 0;
        private static final int MSG_ON_DISPLAY_RESUMED = 1;
        private static final int MSG_ON_DISPLAY_STOPPED = 2;
        private final IVirtualDisplayCallback mCallback;

        public Callback(IVirtualDisplayCallback callback, Handler handler) {
            super(handler.getLooper());
            this.mCallback = callback;
        }

        public void handleMessage(Message msg) {
            try {
                int i = msg.what;
                if (i == 0) {
                    this.mCallback.onPaused();
                } else if (i == 1) {
                    this.mCallback.onResumed();
                } else if (i == 2) {
                    this.mCallback.onStopped();
                }
            } catch (RemoteException e) {
                Slog.w(VirtualDisplayAdapter.TAG, "Failed to notify listener of virtual display event.", e);
            }
        }

        public void dispatchDisplayPaused() {
            sendEmptyMessage(0);
        }

        public void dispatchDisplayResumed() {
            sendEmptyMessage(1);
        }

        public void dispatchDisplayStopped() {
            sendEmptyMessage(2);
        }
    }

    private final class MediaProjectionCallback extends IMediaProjectionCallback.Stub {
        private IBinder mAppToken;

        public MediaProjectionCallback(IBinder appToken) {
            this.mAppToken = appToken;
        }

        public void onStop() {
            synchronized (VirtualDisplayAdapter.this.getSyncRoot()) {
                VirtualDisplayAdapter.this.handleMediaProjectionStoppedLocked(this.mAppToken);
            }
        }
    }
}
