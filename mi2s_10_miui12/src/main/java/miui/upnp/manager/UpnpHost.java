package miui.upnp.manager;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import miui.upnp.manager.handler.UpnpActionHandler;
import miui.upnp.manager.handler.UpnpCompletionHandler;
import miui.upnp.service.handler.IActionListener;
import miui.upnp.service.handler.ICompletionHandler;
import miui.upnp.service.host.IUpnpHostService;
import miui.upnp.typedef.device.Device;
import miui.upnp.typedef.device.invocation.ActionInfo;
import miui.upnp.typedef.device.invocation.EventInfo;
import miui.upnp.typedef.error.UpnpError;
import miui.upnp.typedef.exception.UpnpException;
import miui.upnp.utils.Binding;

public class UpnpHost extends Binding {
    private static String SVC_NAME = IUpnpHostService.class.getName();
    private static final String TAG = UpnpHost.class.getSimpleName();
    private static final String UPNP_SERVICE_PACKAGE_NAME = "com.xiaomi.upnp";
    private IUpnpHostService serviceInstance;

    public UpnpHost(Context context) {
        super(context);
    }

    public synchronized boolean bind() {
        return super.bind(UPNP_SERVICE_PACKAGE_NAME, SVC_NAME);
    }

    /* access modifiers changed from: protected */
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.serviceInstance = IUpnpHostService.Stub.asInterface(service);
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

    public void register(Device device, final UpnpCompletionHandler handler, final UpnpActionHandler actionHandler) throws UpnpException {
        Log.d(TAG, "register");
        if (!super.isBound()) {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        } else if (device != null) {
            try {
                this.serviceInstance.register(device, new ICompletionHandler.Stub() {
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
                }, new IActionListener.Stub() {
                    public UpnpError onAction(ActionInfo action) throws RemoteException {
                        try {
                            return actionHandler.onAction(action);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return UpnpError.INTERNAL;
                        }
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
                throw new UpnpException(UpnpError.INTERNAL, "register failed: RemoteException");
            }
        } else {
            throw new UpnpException(UpnpError.INVALID_ARGUMENT);
        }
    }

    public void unregister(Device device, final UpnpCompletionHandler handler) throws UpnpException {
        Log.d(TAG, "unregister");
        if (!super.isBound()) {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        } else if (device != null) {
            try {
                this.serviceInstance.unregister(device, new ICompletionHandler.Stub() {
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
                e.printStackTrace();
                throw new UpnpException(UpnpError.INTERNAL, "unregister failed: RemoteException");
            }
        } else {
            throw new UpnpException(UpnpError.INVALID_ARGUMENT);
        }
    }

    public void sendEvents(EventInfo info) throws UpnpException {
        Log.d(TAG, "sendEvents");
        if (!super.isBound()) {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        } else if (info != null) {
            try {
                this.serviceInstance.sendEvents(info);
            } catch (RemoteException e) {
                e.printStackTrace();
                throw new UpnpException(UpnpError.INTERNAL, "sendEvents failed: RemoteException");
            }
        } else {
            throw new UpnpException(UpnpError.INVALID_ARGUMENT);
        }
    }
}
