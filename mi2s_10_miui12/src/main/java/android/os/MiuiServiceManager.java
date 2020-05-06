package android.os;

import android.os.IMiuiServiceManager;
import android.util.Log;
import java.util.HashMap;

@Deprecated
public class MiuiServiceManager {
    private static final String DEPRECATED_MSG = "MiuiServiceManager has been deprecated";
    private static final String TAG = "MiuiServiceManager";
    private static HashMap<String, IBinder> sCache = new HashMap<>();
    private static IMiuiServiceManager sMiuiServiceManager;

    private static IMiuiServiceManager getIMiuiServiceManager() {
        IMiuiServiceManager iMiuiServiceManager = sMiuiServiceManager;
        if (iMiuiServiceManager != null) {
            return iMiuiServiceManager;
        }
        sMiuiServiceManager = IMiuiServiceManager.Stub.asInterface(ServiceManager.getService(MiuiServiceManagerInternal.SERVICE_NAME));
        return sMiuiServiceManager;
    }

    @Deprecated
    public static IBinder getService(String name) {
        try {
            IBinder service = sCache.get(name);
            if (service != null) {
                return service;
            }
            return getIMiuiServiceManager().getService(name);
        } catch (NullPointerException e) {
            Log.w(TAG, DEPRECATED_MSG, e);
            return null;
        } catch (RemoteException e2) {
            Log.e(TAG, "error in miui getService", e2);
            return null;
        }
    }

    @Deprecated
    public static void addService(String name, IBinder service) {
        try {
            getIMiuiServiceManager().addService(name, service);
        } catch (NullPointerException e) {
            Log.w(TAG, DEPRECATED_MSG, e);
        } catch (RemoteException e2) {
            Log.e(TAG, "error in miui addService", e2);
        }
    }

    @Deprecated
    public static String[] listServices() {
        try {
            return getIMiuiServiceManager().listServices();
        } catch (NullPointerException e) {
            Log.w(TAG, DEPRECATED_MSG, e);
            return new String[0];
        } catch (RemoteException e2) {
            Log.e(TAG, "error in miui listServices", e2);
            return new String[0];
        }
    }
}
