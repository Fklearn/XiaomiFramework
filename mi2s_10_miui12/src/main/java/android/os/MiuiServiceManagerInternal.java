package android.os;

import android.os.IMiuiServiceManager;
import android.util.Log;
import java.util.HashMap;

public class MiuiServiceManagerInternal extends IMiuiServiceManager.Stub {
    public static final String SERVICE_NAME = "miui.os.servicemanager";
    private static final String TAG = "MiuiServiceManagerInternal";
    private HashMap<String, IBinder> map = new HashMap<>();

    public IBinder getService(String name) {
        if (name != null) {
            return this.map.get(name);
        }
        Log.e(TAG, "get service name is null");
        return null;
    }

    public void addService(String name, IBinder service) {
        if (name == null || service == null) {
            Log.e(TAG, "add service parameter error, service name: " + name);
            return;
        }
        this.map.put(name, service);
    }

    public String[] listServices() {
        return (String[]) this.map.keySet().toArray();
    }
}
