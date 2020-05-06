package miui.bluetooth.ble;

import android.content.Context;
import android.os.RemoteException;
import java.nio.ByteBuffer;
import miui.bluetooth.ble.MiBleProfile;

public class MiBlePayProfile extends MiBleProfile {

    public interface OnRSSIChangedListerner {
        void onRssi(int i);
    }

    public MiBlePayProfile(Context context, String device, MiBleProfile.IProfileStateChangeCallback callback) {
        super(context, device, callback);
    }

    public boolean setEncryptionKey(byte[] key) {
        if (!isReady()) {
            return false;
        }
        try {
            return this.mService.setEncryptionKey(this.mDevice, this.mClientId, key);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public byte[] encrypt(byte[] plain) {
        if (!isReady()) {
            return null;
        }
        try {
            return this.mService.encrypt(this.mDevice, this.mClientId, plain);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void registerRssiChangedListener(final OnRSSIChangedListerner listener) {
        if (listener != null) {
            registerPropertyNotifyCallback(4, new MiBleProfile.IPropertyNotifyCallback() {
                public void notifyProperty(int property, byte[] data) {
                    if (property == 4 && data != null && data.length == 4) {
                        listener.onRssi(ByteBuffer.wrap(data).getInt());
                    }
                }
            });
        }
    }

    public void unregisterRssiChangedListener() {
        unregisterPropertyNotifyCallback(4);
    }
}
