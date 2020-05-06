package miui.upnp.manager;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.util.List;
import miui.telephony.phonenumber.Prefix;
import miui.upnp.manager.ctrlpoint.AbstractDevice;
import miui.upnp.manager.ctrlpoint.DeviceFactory;
import miui.upnp.manager.handler.UpnpCompletionHandler;
import miui.upnp.manager.handler.UpnpEventListener;
import miui.upnp.manager.handler.UpnpInvokeCompletionHandler;
import miui.upnp.manager.handler.UpnpScanListener;
import miui.upnp.service.controlpoint.IUpnpControlPointService;
import miui.upnp.service.handler.ICompletionHandler;
import miui.upnp.service.handler.IEventListener;
import miui.upnp.service.handler.IInvokeCompletionHandler;
import miui.upnp.service.handler.IScanListener;
import miui.upnp.service.handler.ISubscribeCompletionHandler;
import miui.upnp.typedef.device.Device;
import miui.upnp.typedef.device.PropertyChanged;
import miui.upnp.typedef.device.Service;
import miui.upnp.typedef.device.invocation.ActionInfo;
import miui.upnp.typedef.device.invocation.SubscriptionInfo;
import miui.upnp.typedef.device.invocation.SubscriptionInfoCreator;
import miui.upnp.typedef.device.urn.Urn;
import miui.upnp.typedef.deviceupdate.DeviceUpdate;
import miui.upnp.typedef.error.UpnpError;
import miui.upnp.typedef.exception.UpnpException;
import miui.upnp.utils.Binding;

public class UpnpControlPoint extends Binding {
    private static String SVC_NAME = IUpnpControlPointService.class.getName();
    /* access modifiers changed from: private */
    public static final String TAG = UpnpControlPoint.class.getSimpleName();
    private static final String UPNP_SERVICE_PACKAGE_NAME = "com.xiaomi.upnp";
    private IUpnpControlPointService serviceInstance;

    public UpnpControlPoint(Context context) {
        super(context);
    }

    public synchronized boolean bind() {
        return super.bind(UPNP_SERVICE_PACKAGE_NAME, SVC_NAME);
    }

    /* access modifiers changed from: protected */
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.serviceInstance = IUpnpControlPointService.Stub.asInterface(service);
    }

    /* access modifiers changed from: protected */
    public void onServiceDisconnected(ComponentName name) {
        this.serviceInstance = null;
    }

    public void start() throws UpnpException {
        Log.d(TAG, "start");
        if (super.isBound()) {
            try {
                this.serviceInstance.start();
            } catch (RemoteException e) {
                e.printStackTrace();
                throw new UpnpException(UpnpError.INTERNAL, "start failed: RemoteException");
            }
        } else {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        }
    }

    public void stop() throws UpnpException {
        Log.d(TAG, "stop");
        if (super.isBound()) {
            try {
                this.serviceInstance.stop();
            } catch (RemoteException e) {
                e.printStackTrace();
                throw new UpnpException(UpnpError.INTERNAL, "stop failed: RemoteException");
            }
        } else {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        }
    }

    public void startScan(List<Urn> types, final UpnpCompletionHandler handler, final UpnpScanListener listener) throws UpnpException {
        Log.d(TAG, "startScan");
        if (super.isBound()) {
            try {
                this.serviceInstance.startScan(types, new ICompletionHandler.Stub() {
                    public void onSucceed() throws RemoteException {
                        try {
                            handler.onSucceed();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    public void onFailed(UpnpError error) throws RemoteException {
                        try {
                            handler.onFailed(error);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new IScanListener.Stub() {
                    public void onDeviceFound(Device device) throws RemoteException {
                        Log.d(UpnpControlPoint.TAG, "onDeviceFound");
                        AbstractDevice d = DeviceFactory.createDevice(device);
                        if (d == null) {
                            Log.d(UpnpControlPoint.TAG, "DeviceFactory createDevice failed");
                            return;
                        }
                        try {
                            listener.onDeviceFound(d);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    public void onDeviceLost(Device device) throws RemoteException {
                        Log.d(UpnpControlPoint.TAG, "onDeviceLost");
                        AbstractDevice d = DeviceFactory.createDevice(device);
                        if (d == null) {
                            Log.d(UpnpControlPoint.TAG, "DeviceFactory createDevice failed");
                            return;
                        }
                        try {
                            listener.onDeviceLost(d);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    public void onDeviceUpdate(DeviceUpdate update) throws RemoteException {
                        Log.d(UpnpControlPoint.TAG, "onDeviceUpdate");
                        try {
                            listener.onDeviceUpdate(update);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
                throw new UpnpException(UpnpError.INTERNAL, "startScan failed: RemoteException");
            }
        } else {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        }
    }

    public void stopScan(final UpnpCompletionHandler handler) throws UpnpException {
        Log.d(TAG, "stopScan");
        if (super.isBound()) {
            try {
                this.serviceInstance.stopScan(new ICompletionHandler.Stub() {
                    public void onSucceed() throws RemoteException {
                        try {
                            handler.onSucceed();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    public void onFailed(UpnpError error) throws RemoteException {
                        try {
                            handler.onFailed(error);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (RemoteException e) {
                throw new UpnpException(UpnpError.INTERNAL, "stopScan failed: RemoteException");
            }
        } else {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        }
    }

    public void invoke(ActionInfo action, final UpnpInvokeCompletionHandler handler) throws UpnpException {
        Log.d(TAG, "invoke");
        if (!super.isBound()) {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        } else if (action != null) {
            try {
                this.serviceInstance.invoke(action, new IInvokeCompletionHandler.Stub() {
                    public void onSucceed(ActionInfo invocation) throws RemoteException {
                        try {
                            handler.onSucceed(invocation);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    public void onFailed(UpnpError error) throws RemoteException {
                        try {
                            handler.onFailed(error);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (RemoteException e) {
                throw new UpnpException(UpnpError.INTERNAL, "invoke failed: RemoteException");
            }
        } else {
            throw new UpnpException(UpnpError.INVALID_ARGUMENT);
        }
    }

    public void subscribe(final Service service, final UpnpCompletionHandler handler, final UpnpEventListener listener) throws UpnpException {
        Log.d(TAG, "subscribe");
        if (!super.isBound()) {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        } else if (!service.isSubscribed()) {
            try {
                this.serviceInstance.subscribe(SubscriptionInfoCreator.create(service), new ISubscribeCompletionHandler.Stub() {
                    public void onSucceed(SubscriptionInfo invocation) throws RemoteException {
                        service.setSubscriptionId(invocation.getSubscriptionId());
                        service.setSubscribed(true);
                        try {
                            handler.onSucceed();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    public void onFailed(UpnpError error) throws RemoteException {
                        try {
                            handler.onFailed(error);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new IEventListener.Stub() {
                    public void onSubscriptionExpired(String serviceId) throws RemoteException {
                        service.setSubscribed(false);
                        try {
                            listener.onSubscriptionExpired(serviceId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    public void onEvent(String serviceId, List<PropertyChanged> list) throws RemoteException {
                        try {
                            listener.onEvent(serviceId, list);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (RemoteException e) {
                throw new UpnpException(UpnpError.INTERNAL, "invoke failed: RemoteException");
            }
        } else {
            throw new UpnpException(UpnpError.SERVICE_SUBSCRIBED);
        }
    }

    public void unsubscribe(final Service service, final UpnpCompletionHandler handler) throws UpnpException {
        Log.d(TAG, "unsubscribe");
        if (!super.isBound()) {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        } else if (service.isSubscribed()) {
            try {
                this.serviceInstance.unsubscribe(SubscriptionInfoCreator.create(service), new ICompletionHandler.Stub() {
                    public void onSucceed() throws RemoteException {
                        service.setSubscribed(false);
                        service.setSubscriptionId(Prefix.EMPTY);
                        try {
                            handler.onSucceed();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    public void onFailed(UpnpError error) throws RemoteException {
                        try {
                            handler.onFailed(error);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (RemoteException e) {
                throw new UpnpException(UpnpError.INTERNAL, "invoke failed: RemoteException");
            }
        } else {
            throw new UpnpException(UpnpError.SERVICE_SUBSCRIBED);
        }
    }
}
