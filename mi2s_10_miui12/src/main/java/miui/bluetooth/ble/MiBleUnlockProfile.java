package miui.bluetooth.ble;

import android.content.Context;
import android.os.RemoteException;
import miui.bluetooth.ble.MiBleProfile;

public class MiBleUnlockProfile extends MiBleProfile {

    public interface OnUnlockStateChangeListener {
        public static final byte STATE_AUTHORISED = 1;
        public static final byte STATE_LOCKED = 0;
        public static final byte STATE_UNLOCKED = 2;

        void onUnlocked(byte b);
    }

    public MiBleUnlockProfile(Context context, String device, MiBleProfile.IProfileStateChangeCallback callback) {
        super(context, device, callback);
    }

    public boolean setLock(String key) {
        if (!isReady()) {
            return false;
        }
        try {
            return this.mService.authorize(this.mDevice, this.mClientId, key);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setRssiThreshold(int rssi) {
        if (!isReady()) {
            return false;
        }
        try {
            return this.mService.setRssiThreshold(this.mDevice, this.mClientId, rssi);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean unlock() {
        if (!isReady()) {
            return false;
        }
        try {
            return this.mService.authenticate(this.mDevice, this.mClientId);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void registerUnlockListener(final OnUnlockStateChangeListener listener) {
        if (listener != null) {
            registerPropertyNotifyCallback(1, new MiBleProfile.IPropertyNotifyCallback() {
                public void notifyProperty(int property, byte[] data) {
                    if (property == 1) {
                        OnUnlockStateChangeListener onUnlockStateChangeListener = listener;
                        byte b = 0;
                        if (data != null) {
                            b = data[0];
                        }
                        onUnlockStateChangeListener.onUnlocked(b);
                    }
                }
            });
        }
    }

    public void unregisterUnlockListener() {
        unregisterPropertyNotifyCallback(1);
    }
}
