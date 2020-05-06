package com.android.server.display;

import android.content.Context;
import android.hardware.display.WifiDisplay;
import android.media.RemoteDisplay;
import android.os.Handler;
import android.util.Slog;
import android.view.Surface;
import miui.util.ObjectReference;
import miui.util.ReflectionUtils;

final class MiuiProjectionManager {
    private static final String TAG = "MIUI_PROJECTION";
    private Context mContext;
    /* access modifiers changed from: private */
    public WifiDisplayController mDisplayController;
    private Handler mHandler;
    private String mIface;
    /* access modifiers changed from: private */
    public State mState = State.STATE_DISCONNECTED;

    enum State {
        STATE_LISTENING,
        STATE_CONNECTED,
        STATE_DISCONNECTED
    }

    public MiuiProjectionManager(Context context, Handler handler, WifiDisplayController controller) {
        this.mContext = context;
        this.mHandler = handler;
        this.mDisplayController = controller;
    }

    private Class<?> tryFindClass(String name) {
        try {
            return Class.forName(name);
        } catch (Exception e) {
            Slog.e(TAG, "class not found: " + name);
            return null;
        }
    }

    /* access modifiers changed from: private */
    public void startProjectionInternal() {
        ObjectReference<Boolean> isAvailable;
        ObjectReference<Object> remoteDisplay;
        ObjectReference<Boolean> connected = ReflectionUtils.tryGetObjectField(this.mDisplayController, "mRemoteDisplayConnected", Boolean.class);
        if (connected == null || !((Boolean) connected.get()).booleanValue()) {
            Class<?> displayControllerClazz = this.mDisplayController.getClass();
            Class<?> extDisplayHelperClazz = tryFindClass("com.android.server.display.ExtendedRemoteDisplayHelper");
            if (extDisplayHelperClazz == null || (isAvailable = ReflectionUtils.tryCallStaticMethod(extDisplayHelperClazz, "isAvailable", Boolean.class, new Object[0])) == null || !((Boolean) isAvailable.get()).booleanValue() || (remoteDisplay = ReflectionUtils.tryCallStaticMethod(extDisplayHelperClazz, "listen", Object.class, new Object[]{this.mIface, new RemoteDisplayListener(), this.mHandler, this.mContext})) == null) {
                ReflectionUtils.trySetObjectField(this.mDisplayController, "mRemoteDisplay", RemoteDisplay.listen(this.mIface, new RemoteDisplayListener(), this.mHandler, (String) null));
                this.mState = State.STATE_LISTENING;
                return;
            }
            ReflectionUtils.trySetObjectField(this.mDisplayController, "mExtRemoteDisplay", remoteDisplay.get());
            this.mState = State.STATE_LISTENING;
        }
    }

    /* access modifiers changed from: private */
    public void stopProjectionInternal() {
        ObjectReference<Object> reference = ReflectionUtils.tryGetObjectField(this.mDisplayController, "mExtRemoteDisplay", Object.class);
        if (reference != null) {
            Object extRemoteDisplay = reference.get();
            Class<?> extDisplayHelperClazz = tryFindClass("com.android.server.display.ExtendedRemoteDisplayHelper");
            if (extDisplayHelperClazz != null) {
                ReflectionUtils.tryCallStaticMethod(extDisplayHelperClazz, "dispose", Void.class, new Object[]{extRemoteDisplay});
                ReflectionUtils.trySetObjectField(this.mDisplayController, "mExtRemoteDisplay", (Object) null);
            }
        } else {
            ((RemoteDisplay) ReflectionUtils.tryGetObjectField(this.mDisplayController, "mRemoteDisplay", RemoteDisplay.class).get()).dispose();
            ReflectionUtils.trySetObjectField(this.mDisplayController, "mRemoteDisplay", (Object) null);
        }
        this.mState = State.STATE_DISCONNECTED;
    }

    public void startProjection(String iface) {
        if (this.mState == State.STATE_DISCONNECTED) {
            Slog.d(TAG, "start projection: " + iface);
            this.mIface = new String(iface);
            this.mHandler.post(new Runnable() {
                public void run() {
                    MiuiProjectionManager.this.startProjectionInternal();
                }
            });
        }
    }

    public void stopProjection() {
        if (this.mState != State.STATE_DISCONNECTED) {
            Slog.d(TAG, "stop projection");
            this.mHandler.post(new Runnable() {
                public void run() {
                    MiuiProjectionManager.this.stopProjectionInternal();
                }
            });
        }
    }

    private class RemoteDisplayListener implements RemoteDisplay.Listener {
        private RemoteDisplayListener() {
        }

        public void onDisplayConnected(Surface surface, int width, int height, int flags, int session) {
            if (surface != null) {
                Slog.d(MiuiProjectionManager.TAG, "remote display connected");
                State unused = MiuiProjectionManager.this.mState = State.STATE_CONNECTED;
                ReflectionUtils.trySetObjectField(MiuiProjectionManager.this.mDisplayController, "mRemoteDisplayConnected", true);
                ReflectionUtils.tryCallMethod(MiuiProjectionManager.this.mDisplayController, "advertiseDisplay", Void.class, new Object[]{new WifiDisplay("02:0e:55:53:62:34", "miui-projection", (String) null, true, true, false), surface, Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(flags)});
                Slog.d(MiuiProjectionManager.TAG, String.format("virtual display metrics:  wxh = %d x %d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)}));
            }
        }

        public void onDisplayDisconnected() {
            Slog.d(MiuiProjectionManager.TAG, "remote display disconnected");
            ReflectionUtils.trySetObjectField(MiuiProjectionManager.this.mDisplayController, "mRemoteDisplayConnected", false);
            MiuiProjectionManager.this.stopProjection();
        }

        public void onDisplayError(int error) {
            MiuiProjectionManager.this.stopProjection();
        }

        public void onDisplayGenericMsgEvent(int event) {
            Slog.d(MiuiProjectionManager.TAG, "onDisplayGenericMsgEvent: " + event);
        }

        public void onDisplayKeyEvent(int uniCode, int flags) {
            Slog.d(MiuiProjectionManager.TAG, "onDisplayKeyEvent: " + uniCode);
        }
    }
}
