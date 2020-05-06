package miui.bluetooth.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;
import java.util.List;
import java.util.UUID;
import miui.bluetooth.ble.IMiBleDeviceManager;
import miui.bluetooth.ble.IScanDeviceCallback;

public class MiBleDeviceManager {
    public static final String ACTION_BLE_EVENT = "miui.bluetooth.BLE_EVENT";
    public static final int BLE_EVENT_AWAKE = 5;
    public static final int BLE_EVENT_CLICK = 1;
    public static final int BLE_EVENT_CUSTOM = 256;
    public static final int BLE_EVENT_DOUBLE_CLICK = 2;
    public static final int BLE_EVENT_LINK_LOSS = 6;
    public static final int BLE_EVENT_LONG_CLICK = 3;
    public static final int BLE_EVENT_PHONE_HANGUP = 7;
    public static final int BLE_EVENT_PHONE_IGNORE = 8;
    public static final int BLE_EVENT_SLEEP = 4;
    public static final int BLE_EVENT_UNKNOWN = 0;
    public static final String BLE_IMMEDIATE_ALERT_PERMISSION = "miui.permission.BLE_IMMEDIATE_ALERT";
    private static final boolean DBG = true;
    public static final String EXTRA_DEVICE = "miui.bluetooth.extras.DEVICE";
    public static final String EXTRA_EVENT = "miui.bluetooth.extras.EVENT";
    public static final String EXTRA_EVENT_DATA = "miui.bluetooth.extras.EVENT_DATA";
    public static final int SERVICE_VERSION_UNKNOWN = -1;
    public static final String SETTING_BIND_DEVICE = "device_type";
    public static final String SETTING_IMMEDIATE_ALERT_ALARM_ENABLED = "alert_alarm_enabled";
    public static final String SETTING_IMMEDIATE_ALERT_INCALL_DELAYED = "alert_incall_delayed";
    public static final String SETTING_IMMEDIATE_ALERT_INCALL_ENABLED = "alert_incall_enabled";
    public static final String SETTING_IMMEDIATE_ALERT_INCALL_IN_CONTACTS_ENABLED = "alert_incall_enabled_in_contacts";
    public static final String SETTING_IMMEDIATE_ALERT_INCALL_NO_CONTACTS_ENABLED = "alert_incall_enabled_no_contacts";
    public static final String SETTING_IMMEDIATE_ALERT_SMS_ENABLED = "alert_sms_enabled";
    public static final String SETTING_IMMEDIATE_ALERT_SMS_IN_CONTACTS_ENABLED = "alert_sms_enabled_in_contacts";
    public static final String SETTING_IMMEDIATE_ALERT_SMS_NO_CONTACTS_ENABLED = "alert_sms_enabled_no_contacts";
    private static final String TAG = "MiBleDeviceManager";
    public static final int TYPE_MI_BAND = 1;
    public static final int TYPE_MI_KEY = 69;
    public static final int TYPE_MI_VR_CONTROLLER = 2;
    public static final int TYPE_UNKNOWN = 0;
    private ParcelUuid mClientId;
    private Context mContext;
    /* access modifiers changed from: private */
    public MiBleDeviceManagerListener mListener;
    private IMiBleDeviceManager mService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MiBleDeviceManager.this.setServie(IMiBleDeviceManager.Stub.asInterface(iBinder));
            if (MiBleDeviceManager.this.mListener != null) {
                MiBleDeviceManager.this.mListener.onInit(MiBleDeviceManager.this);
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            MiBleDeviceManager.this.setServie((IMiBleDeviceManager) null);
            if (MiBleDeviceManager.this.mListener != null) {
                MiBleDeviceManager.this.mListener.onDestroy();
            }
        }
    };
    private IBinder mToken;

    public interface MiBleDeviceManagerListener {
        void onDestroy();

        void onInit(MiBleDeviceManager miBleDeviceManager);
    }

    private MiBleDeviceManager(Context context, MiBleDeviceManagerListener listener) {
        if (context == null || context.getApplicationContext() == null) {
            throw new IllegalArgumentException("context not valid");
        }
        this.mClientId = new ParcelUuid(UUID.randomUUID());
        this.mToken = new Binder();
        this.mContext = context.getApplicationContext();
        this.mListener = listener;
    }

    /* access modifiers changed from: private */
    public void setServie(IMiBleDeviceManager servie) {
        this.mService = servie;
    }

    public static MiBleDeviceManager createManager(Context context, MiBleDeviceManagerListener listener) {
        MiBleDeviceManager manager = new MiBleDeviceManager(context, listener);
        manager.init();
        return manager;
    }

    private void init() {
        Intent intent = new Intent("miui.bluetooth.mible.DeviceManagerService");
        intent.setComponent(new ComponentName("com.xiaomi.bluetooth", "com.android.bluetooth.ble.app.MiBleDeviceManagerService"));
        boolean ret = this.mContext.bindService(intent, this.mServiceConnection, 1);
        if (!ret) {
            intent.setComponent(new ComponentName("com.android.bluetooth", "com.android.bluetooth.ble.app.MiBleDeviceManagerService"));
            ret = this.mContext.bindService(intent, this.mServiceConnection, 1);
        }
        if (!ret) {
            Log.e(TAG, "bind manager service error: " + intent);
            if (this.mListener != null) {
                new Handler().post(new Runnable() {
                    public void run() {
                        MiBleDeviceManager.this.mListener.onDestroy();
                    }
                });
            }
        }
    }

    public void close() {
        try {
            this.mContext.unbindService(this.mServiceConnection);
        } catch (Exception e) {
            Log.e(TAG, "Close manager service error", e);
        }
    }

    private boolean checkReady() {
        if (this.mService != null) {
            return true;
        }
        Log.w(TAG, "Manager is not ready");
        return false;
    }

    public int getServiceVersion() {
        if (!checkReady()) {
            return -1;
        }
        try {
            return this.mService.getServiceVersion();
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<String> getBoundDevices() {
        if (!checkReady()) {
            return null;
        }
        try {
            return this.mService.getBoundDevices();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean setSettings(String device, String setting, boolean value) {
        if (checkReady()) {
            try {
                return this.mService.setSettingInteger(device, setting, value ? 1 : 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean setSettings(String device, String setting, int value) {
        if (!checkReady()) {
            return false;
        }
        try {
            return this.mService.setSettingInteger(device, setting, value);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setSettings(String device, String setting, String value) {
        if (!checkReady()) {
            return false;
        }
        try {
            return this.mService.setSettingString(device, setting, value);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean getSettingsBoolean(String device, String setting) {
        if (checkReady()) {
            try {
                if (this.mService.getSettingInteger(device, setting) != 0) {
                    return true;
                }
                return false;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public int getSettingsInteger(String device, String setting) {
        if (!checkReady()) {
            return 0;
        }
        try {
            return this.mService.getSettingInteger(device, setting);
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getSettingsString(String device, String setting) {
        if (!checkReady()) {
            return null;
        }
        try {
            return this.mService.getSettingString(device, setting);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean bindDevice(String device) {
        if (checkReady()) {
            try {
                return this.mService.setSettingInteger(device, SETTING_BIND_DEVICE, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean bindDevice(String device, byte[] token) {
        if (!checkReady()) {
            return false;
        }
        try {
            return this.mService.setToken(device, token);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean unbindDevice(String device) {
        if (!checkReady()) {
            return false;
        }
        try {
            return this.mService.deleteSettings(device);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getDeviceType(String device) {
        if (!checkReady()) {
            return 0;
        }
        try {
            return this.mService.getDeviceType(device);
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public ScanResult getScanResult(String device) {
        if (!checkReady()) {
            return null;
        }
        try {
            return this.mService.getScanResult(device);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean startScanDevice(final int property, final BluetoothAdapter.LeScanCallback callback) {
        if (!checkReady()) {
            return false;
        }
        try {
            this.mService.startScanDevice(this.mToken, this.mClientId, property, new IScanDeviceCallback.Stub() {
                public void onScanDevice(int inProperty, BluetoothDevice device, int rssi, byte[] data) throws RemoteException {
                    if (inProperty == property) {
                        callback.onLeScan(device, rssi, data);
                    }
                }
            });
            return false;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void stopScanDevice() {
        if (checkReady()) {
            try {
                this.mService.stopScanDevice(this.mClientId);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean registerAppForBleEvent(String device, int event) {
        if (!checkReady()) {
            return false;
        }
        try {
            return this.mService.setSettingString(device, getSettingKeyForEvent(event), this.mContext.getPackageName());
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean unregisterAppForBleEvent(String device, int event) {
        if (!checkReady()) {
            return false;
        }
        try {
            return this.mService.setSettingString(device, getSettingKeyForEvent(event), (String) null);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getRegisterAppForBleEvent(String device, int event) {
        if (!checkReady()) {
            return null;
        }
        try {
            return this.mService.getRegisterAppForBleEvent(device, event);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean registerBleEventListener(String device, int event, IBleEventCallback callback) {
        if (callback != null && checkReady()) {
            try {
                return this.mService.registerBleEventListener(device, event, callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean unregisterBleEventListener(String device, int event, IBleEventCallback callback) {
        if (!checkReady()) {
            return false;
        }
        try {
            return this.mService.unregisterBleEventListener(device, event, callback);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getSettingKeyForEvent(int event) {
        return String.format("miui_ble_event_%d", new Object[]{Integer.valueOf(event)});
    }
}
