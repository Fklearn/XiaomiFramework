package com.milink.api.v1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.milink.api.v1.aidl.IMcs;
import com.milink.api.v1.type.MediaType;
import com.milink.api.v1.type.MilinkConfig;
import com.milink.api.v1.type.ReturnCode;
import com.milink.api.v1.type.SlideMode;

public class MilinkClientManager implements IMilinkClientManager {
    /* access modifiers changed from: private */
    public static final String TAG = MilinkClientManager.class.getSimpleName();
    private Context mContext = null;
    /* access modifiers changed from: private */
    public MilinkClientManagerDelegate mDelegate = null;
    /* access modifiers changed from: private */
    public String mDeviceName = null;
    private boolean mIsbound = false;
    /* access modifiers changed from: private */
    public McsDataSource mMcsDataSource = null;
    /* access modifiers changed from: private */
    public McsDelegate mMcsDelegate = null;
    /* access modifiers changed from: private */
    public McsDeviceListener mMcsDeviceListener = null;
    private McsMiracastConnectCallback mMcsMiracastConnectCallback;
    private McsOpenMiracastListener mMcsOpenMiracastListener;
    private McsScanListCallback mMcsScanListCallback;
    /* access modifiers changed from: private */
    public IMcs mService = null;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(MilinkClientManager.TAG, "onServiceConnected");
            new Handler().post(new Runnable() {
                public void run() {
                    if (MilinkClientManager.this.mDelegate != null) {
                        MilinkClientManager.this.mDelegate.onOpen();
                    }
                }
            });
            IMcs unused = MilinkClientManager.this.mService = IMcs.Stub.asInterface(service);
            try {
                MilinkClientManager.this.mService.setDeviceName(MilinkClientManager.this.mDeviceName);
                MilinkClientManager.this.mService.setDelegate(MilinkClientManager.this.mMcsDelegate);
                MilinkClientManager.this.mService.setDataSource(MilinkClientManager.this.mMcsDataSource);
                MilinkClientManager.this.mService.setDeviceListener(MilinkClientManager.this.mMcsDeviceListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.d(MilinkClientManager.TAG, "onServiceDisconnected");
            new Handler().post(new Runnable() {
                public void run() {
                    if (MilinkClientManager.this.mDelegate != null) {
                        MilinkClientManager.this.mDelegate.onClose();
                    }
                }
            });
            try {
                MilinkClientManager.this.mService.unsetDeviceListener(MilinkClientManager.this.mMcsDeviceListener);
                MilinkClientManager.this.mService.unsetDataSource(MilinkClientManager.this.mMcsDataSource);
                MilinkClientManager.this.mService.unsetDelegate(MilinkClientManager.this.mMcsDelegate);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            IMcs unused = MilinkClientManager.this.mService = null;
        }
    };

    public MilinkClientManager(Context context) {
        this.mContext = context;
        this.mMcsDelegate = new McsDelegate();
        this.mMcsDataSource = new McsDataSource();
        this.mMcsDeviceListener = new McsDeviceListener();
        this.mMcsOpenMiracastListener = new McsOpenMiracastListener();
        this.mMcsMiracastConnectCallback = new McsMiracastConnectCallback();
        this.mMcsScanListCallback = new McsScanListCallback();
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public void setDeviceName(String selfName) {
        this.mDeviceName = selfName;
    }

    public void setDataSource(MilinkClientManagerDataSource dataSource) {
        this.mMcsDataSource.setDataSource(dataSource);
    }

    public void setDelegate(MilinkClientManagerDelegate delegate) {
        this.mDelegate = delegate;
        this.mMcsDelegate.setDelegate(delegate);
        this.mMcsDeviceListener.setDelegate(delegate);
    }

    public void setDeviceListener(MiLinkClientDeviceListener listener) {
        this.mMcsDeviceListener.setDeviceListener(listener);
    }

    public void open() {
        bindMilinkClientService();
    }

    public void close() {
        unbindMilinkClientService();
    }

    public ReturnCode showScanList(MiLinkClientScanListCallback callback, int displayFlag) {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode code = ReturnCode.OK;
        try {
            this.mMcsScanListCallback.setCallback(callback);
            this.mService.showScanList(this.mMcsScanListCallback, displayFlag);
            return code;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    public ReturnCode dismissScanList() {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode code = ReturnCode.OK;
        try {
            this.mService.dismissScanList();
            return code;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    public ReturnCode selectDevice(String deviceId, String deviceName, String deviceType) {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode code = ReturnCode.OK;
        try {
            this.mService.selectDevice(deviceId, deviceName, deviceType);
            return code;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    public ReturnCode startWifiDisplayScan() {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode code = ReturnCode.OK;
        try {
            this.mService.startWifiDisplayScan();
            return code;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    public ReturnCode stopWifiDisplayScan() {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode code = ReturnCode.OK;
        try {
            this.mService.stopWifiDisplayScan();
            return code;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    public ReturnCode connectWifiDisplay(String deviceName, String p2pMac, String wifiMac, MiLinkClientMiracastConnectCallback connectCallback) {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode code = ReturnCode.OK;
        try {
            this.mMcsMiracastConnectCallback.setCallback(connectCallback);
            this.mService.connectWifiDisplay(deviceName, p2pMac, wifiMac, this.mMcsMiracastConnectCallback);
            return code;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    public ReturnCode disconnectWifiDisplay() {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode code = ReturnCode.OK;
        try {
            this.mService.disconnectWifiDisplay();
            return code;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    public ReturnCode startTvMiracast(String deviceName, String ip, String p2pMac, String wifiMac, String deviceType, MiLinkClientOpenMiracastListener listener) {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        this.mMcsOpenMiracastListener.setOpenMiracastListener(listener);
        ReturnCode code = ReturnCode.OK;
        try {
            this.mService.startTvMiracast(deviceName, ip, p2pMac, wifiMac, deviceType, this.mMcsOpenMiracastListener);
            return code;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    private void bindMilinkClientService() {
        if (!this.mIsbound) {
            Intent intent = new Intent(IMcs.class.getName());
            intent.setPackage(MilinkConfig.PACKAGE_NAME);
            this.mIsbound = this.mContext.bindService(intent, this.mServiceConnection, 1);
        }
    }

    private void unbindMilinkClientService() {
        if (this.mIsbound) {
            this.mContext.unbindService(this.mServiceConnection);
            this.mIsbound = false;
        }
    }

    public ReturnCode connect(String deviceId, int timeout) {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(this.mService.connect(deviceId, timeout));
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    public ReturnCode disconnect() {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(this.mService.disconnect());
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    public ReturnCode startShow() {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(this.mService.startShow());
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    public ReturnCode show(String photoUri) {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(this.mService.show(photoUri));
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            return ReturnCode.InvalidParams;
        }
    }

    public ReturnCode zoomPhoto(String photoUri, int x, int y, int screenWidth, int screenHeight, int orgPhotoWidth, int orgPhotoHeight, float scale) {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(this.mService.zoomPhoto(photoUri, x, y, screenWidth, screenHeight, orgPhotoWidth, orgPhotoHeight, scale));
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    public ReturnCode stopShow() {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(this.mService.stopShow());
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    public ReturnCode startSlideshow(int duration, SlideMode type) {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(this.mService.startSlideshow(duration, type == SlideMode.Recyle));
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    public ReturnCode stopSlideshow() {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(this.mService.stopSlideshow());
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    public ReturnCode startPlay(String url, String title, int iPosition, double dPosition, MediaType type) {
        return startPlay(url, title, (String) null, iPosition, dPosition, type);
    }

    public ReturnCode startPlay(String url, String title, String extra, int iPosition, double dPosition, MediaType type) {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            int i = AnonymousClass2.$SwitchMap$com$milink$api$v1$type$MediaType[type.ordinal()];
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        ReturnCode code = ReturnCode.InvalidParams;
                    }
                    return ReturnCode.InvalidParams;
                } else if (extra == null) {
                    return getReturnCode(this.mService.startPlayVideo(url, title, iPosition, dPosition));
                } else {
                    return getReturnCode(this.mService.startPlayVideoEx(url, title, extra, iPosition, dPosition));
                }
            } else if (extra == null) {
                return getReturnCode(this.mService.startPlayAudio(url, title, iPosition, dPosition));
            } else {
                return getReturnCode(this.mService.startPlayAudioEx(url, title, extra, iPosition, dPosition));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    /* renamed from: com.milink.api.v1.MilinkClientManager$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$milink$api$v1$type$MediaType = new int[MediaType.values().length];

        static {
            try {
                $SwitchMap$com$milink$api$v1$type$MediaType[MediaType.Audio.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$milink$api$v1$type$MediaType[MediaType.Video.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$milink$api$v1$type$MediaType[MediaType.Photo.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public ReturnCode stopPlay() {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(this.mService.stopPlay());
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    public ReturnCode setPlaybackRate(int rate) {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(this.mService.setPlaybackRate(rate));
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    public int getPlaybackRate() {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return 0;
        }
        try {
            return iMcs.getPlaybackRate();
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public ReturnCode setPlaybackProgress(int position) {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(this.mService.setPlaybackProgress(position));
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    public int getPlaybackProgress() {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return 0;
        }
        try {
            return iMcs.getPlaybackProgress();
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getPlaybackDuration() {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return 0;
        }
        try {
            return iMcs.getPlaybackDuration();
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public ReturnCode setVolume(int volume) {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(this.mService.setVolume(volume));
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    public int getVolume() {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return 0;
        }
        try {
            return iMcs.getVolume();
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public ReturnCode rotatePhoto(String photoUri, boolean flag, float degree) {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(this.mService.rotatePhoto(photoUri, flag, degree));
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    private ReturnCode getReturnCode(int returnValue) {
        ReturnCode returnCode = ReturnCode.OK;
        if (returnValue == -5) {
            return ReturnCode.NotSupport;
        }
        if (returnValue == -4) {
            return ReturnCode.NotConnected;
        }
        if (returnValue == -3) {
            return ReturnCode.InvalidUrl;
        }
        if (returnValue == -2) {
            return ReturnCode.InvalidParams;
        }
        if (returnValue == -1) {
            return ReturnCode.Error;
        }
        if (returnValue != 0) {
            return ReturnCode.Error;
        }
        return ReturnCode.OK;
    }
}
