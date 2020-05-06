package com.miui.networkassistant.config;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import com.miui.networkassistant.service.ISharedPreBinder;
import com.miui.networkassistant.service.ISharedPreBinderListener;
import com.miui.networkassistant.service.ITrafficManageBinder;
import com.miui.networkassistant.service.wrapper.TmBinderCacher;
import java.util.HashMap;
import java.util.Map;

public class SharedPreferenceHelper {
    private static HashMap<String, SharedPreferenceHelper> sInstanceMap;
    static ServiceConnection sTmConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ITrafficManageBinder unused = SharedPreferenceHelper.sTrafficManageBinder = ITrafficManageBinder.Stub.asInterface(iBinder);
            SharedPreferenceHelper.onBinderAttach();
        }

        public void onServiceDisconnected(ComponentName componentName) {
        }
    };
    /* access modifiers changed from: private */
    public static ITrafficManageBinder sTrafficManageBinder;
    private final int MODE = 0;
    private Object mBinderPreLock = new Object();
    private BinderPreferences mBinderPreferences = null;
    private ISharedPreBinderListener mClientBinderListener = null;
    private String mFileName = null;
    private NaSharedPreferences mPreferences = null;

    private class BinderPreferences implements IPreferences {
        ISharedPreBinder mSharedPreBinder;

        public BinderPreferences(ISharedPreBinder iSharedPreBinder) {
            this.mSharedPreBinder = iSharedPreBinder;
        }

        public float load(String str, float f) {
            ISharedPreBinder iSharedPreBinder = this.mSharedPreBinder;
            if (iSharedPreBinder != null) {
                try {
                    return iSharedPreBinder.getFloat(str, f);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return f;
        }

        public int load(String str, int i) {
            ISharedPreBinder iSharedPreBinder = this.mSharedPreBinder;
            if (iSharedPreBinder != null) {
                try {
                    return iSharedPreBinder.getInt(str, i);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return i;
        }

        public long load(String str, long j) {
            ISharedPreBinder iSharedPreBinder = this.mSharedPreBinder;
            if (iSharedPreBinder != null) {
                try {
                    return iSharedPreBinder.getLong(str, j);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return j;
        }

        public String load(String str, String str2) {
            ISharedPreBinder iSharedPreBinder = this.mSharedPreBinder;
            if (iSharedPreBinder != null) {
                try {
                    return iSharedPreBinder.getString(str, str2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return str2;
        }

        public boolean load(String str, boolean z) {
            ISharedPreBinder iSharedPreBinder = this.mSharedPreBinder;
            if (iSharedPreBinder != null) {
                try {
                    return iSharedPreBinder.getBoolean(str, z);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return z;
        }

        public boolean save(String str, float f) {
            ISharedPreBinder iSharedPreBinder = this.mSharedPreBinder;
            if (iSharedPreBinder == null) {
                return false;
            }
            try {
                return iSharedPreBinder.putFloat(str, f);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean save(String str, int i) {
            ISharedPreBinder iSharedPreBinder = this.mSharedPreBinder;
            if (iSharedPreBinder == null) {
                return false;
            }
            try {
                return iSharedPreBinder.putInt(str, i);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean save(String str, long j) {
            ISharedPreBinder iSharedPreBinder = this.mSharedPreBinder;
            if (iSharedPreBinder == null) {
                return false;
            }
            try {
                return iSharedPreBinder.putLong(str, j);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean save(String str, String str2) {
            ISharedPreBinder iSharedPreBinder = this.mSharedPreBinder;
            if (iSharedPreBinder == null) {
                return false;
            }
            try {
                return iSharedPreBinder.putString(str, str2);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean save(String str, boolean z) {
            ISharedPreBinder iSharedPreBinder = this.mSharedPreBinder;
            if (iSharedPreBinder == null) {
                return false;
            }
            try {
                return iSharedPreBinder.putBoolean(str, z);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private interface IPreferences {
        float load(String str, float f);

        int load(String str, int i);

        long load(String str, long j);

        String load(String str, String str2);

        boolean load(String str, boolean z);

        boolean save(String str, float f);

        boolean save(String str, int i);

        boolean save(String str, long j);

        boolean save(String str, String str2);

        boolean save(String str, boolean z);
    }

    private class NaSharedPreferences implements IPreferences {
        private final ISharedPreBinderListener mBinderListener = new ISharedPreBinderListener.Stub() {
            public boolean onPutBoolean(String str, boolean z) {
                return NaSharedPreferences.this.save(str, z);
            }

            public boolean onPutFloat(String str, float f) {
                return NaSharedPreferences.this.save(str, f);
            }

            public boolean onPutInt(String str, int i) {
                return NaSharedPreferences.this.save(str, i);
            }

            public boolean onPutLong(String str, long j) {
                return NaSharedPreferences.this.save(str, j);
            }

            public boolean onPutString(String str, String str2) {
                return NaSharedPreferences.this.save(str, str2);
            }
        };
        private final SharedPreferences mSharedPreferences;

        public NaSharedPreferences(Context context, String str) {
            this.mSharedPreferences = context.getSharedPreferences(str, 0);
        }

        public ISharedPreBinderListener getBinderListener() {
            return this.mBinderListener;
        }

        public float load(String str, float f) {
            return this.mSharedPreferences.getFloat(str, f);
        }

        public int load(String str, int i) {
            return this.mSharedPreferences.getInt(str, i);
        }

        public long load(String str, long j) {
            return this.mSharedPreferences.getLong(str, j);
        }

        public String load(String str, String str2) {
            return this.mSharedPreferences.getString(str, str2);
        }

        public boolean load(String str, boolean z) {
            return this.mSharedPreferences.getBoolean(str, z);
        }

        public boolean save(String str, float f) {
            SharedPreferences.Editor edit = this.mSharedPreferences.edit();
            edit.putFloat(str, f);
            edit.apply();
            return true;
        }

        public boolean save(String str, int i) {
            SharedPreferences.Editor edit = this.mSharedPreferences.edit();
            edit.putInt(str, i);
            edit.apply();
            return true;
        }

        public boolean save(String str, long j) {
            SharedPreferences.Editor edit = this.mSharedPreferences.edit();
            edit.putLong(str, j);
            edit.apply();
            return true;
        }

        public boolean save(String str, String str2) {
            SharedPreferences.Editor edit = this.mSharedPreferences.edit();
            try {
                edit.putString(str, str2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            edit.apply();
            return true;
        }

        public boolean save(String str, boolean z) {
            SharedPreferences.Editor edit = this.mSharedPreferences.edit();
            edit.putBoolean(str, z);
            edit.apply();
            return true;
        }
    }

    private SharedPreferenceHelper(Context context, String str) {
        this.mFileName = str;
        this.mPreferences = new NaSharedPreferences(context, str);
        ITrafficManageBinder iTrafficManageBinder = sTrafficManageBinder;
        if (iTrafficManageBinder != null) {
            try {
                this.mBinderPreferences = new BinderPreferences(iTrafficManageBinder.getSharedPreBinder(this.mFileName, this.mPreferences.getBinderListener()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void attachBinder(ITrafficManageBinder iTrafficManageBinder) {
        synchronized (this.mBinderPreLock) {
            if (sTrafficManageBinder != null) {
                try {
                    this.mBinderPreferences = new BinderPreferences(sTrafficManageBinder.getSharedPreBinder(this.mFileName, this.mPreferences.getBinderListener()));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static synchronized SharedPreferenceHelper getInstance(Context context, String str) {
        SharedPreferenceHelper sharedPreferenceHelper;
        synchronized (SharedPreferenceHelper.class) {
            if (sInstanceMap == null) {
                sInstanceMap = new HashMap<>();
            }
            sharedPreferenceHelper = sInstanceMap.get(str);
            if (sharedPreferenceHelper == null) {
                sharedPreferenceHelper = new SharedPreferenceHelper(context, str);
                sInstanceMap.put(str, sharedPreferenceHelper);
            }
        }
        return sharedPreferenceHelper;
    }

    public static void initForUIProcess() {
        TmBinderCacher.getInstance().bindTmService(sTmConnection);
    }

    /* access modifiers changed from: private */
    public static synchronized void onBinderAttach() {
        synchronized (SharedPreferenceHelper.class) {
            if (sInstanceMap != null && sInstanceMap.size() > 0) {
                for (Map.Entry<String, SharedPreferenceHelper> value : sInstanceMap.entrySet()) {
                    ((SharedPreferenceHelper) value.getValue()).attachBinder(sTrafficManageBinder);
                }
            }
        }
    }

    public void attachBinderListener(ISharedPreBinderListener iSharedPreBinderListener) {
        synchronized (this.mBinderPreLock) {
            this.mClientBinderListener = iSharedPreBinderListener;
        }
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        super.finalize();
        TmBinderCacher.getInstance().unbindTmService(sTmConnection);
    }

    public float load(String str, float f) {
        float load = this.mPreferences.load(str, f);
        synchronized (this.mBinderPreLock) {
            if (this.mBinderPreferences != null) {
                float load2 = this.mBinderPreferences.load(str, load);
                if (load != load2) {
                    this.mPreferences.save(str, load2);
                    load = load2;
                }
            }
        }
        return load;
    }

    public int load(String str, int i) {
        int load;
        int load2 = this.mPreferences.load(str, i);
        synchronized (this.mBinderPreLock) {
            if (!(this.mBinderPreferences == null || load2 == (load = this.mBinderPreferences.load(str, load2)))) {
                this.mPreferences.save(str, load);
                load2 = load;
            }
        }
        return load2;
    }

    public long load(String str, long j) {
        long load = this.mPreferences.load(str, j);
        synchronized (this.mBinderPreLock) {
            if (this.mBinderPreferences != null) {
                long load2 = this.mBinderPreferences.load(str, load);
                if (load != load2) {
                    this.mPreferences.save(str, load2);
                    load = load2;
                }
            }
        }
        return load;
    }

    public String load(String str, String str2) {
        String load = this.mPreferences.load(str, str2);
        synchronized (this.mBinderPreLock) {
            if (this.mBinderPreferences != null) {
                String load2 = this.mBinderPreferences.load(str, load);
                if (!TextUtils.equals(load, load2)) {
                    this.mPreferences.save(str, load2);
                    load = load2;
                }
            }
        }
        return load;
    }

    public boolean load(String str, boolean z) {
        boolean load;
        boolean load2 = this.mPreferences.load(str, z);
        synchronized (this.mBinderPreLock) {
            if (!(this.mBinderPreferences == null || load2 == (load = this.mBinderPreferences.load(str, load2)))) {
                this.mPreferences.save(str, load);
                load2 = load;
            }
        }
        return load2;
    }

    public boolean save(String str, float f) {
        synchronized (this.mBinderPreLock) {
            if (this.mBinderPreferences != null) {
                this.mBinderPreferences.save(str, f);
            }
            if (this.mClientBinderListener != null) {
                try {
                    this.mClientBinderListener.onPutFloat(str, f);
                } catch (DeadObjectException unused) {
                    this.mClientBinderListener = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        return this.mPreferences.save(str, f);
    }

    public boolean save(String str, int i) {
        synchronized (this.mBinderPreLock) {
            if (this.mBinderPreferences != null) {
                this.mBinderPreferences.save(str, i);
            }
            if (this.mClientBinderListener != null) {
                try {
                    this.mClientBinderListener.onPutInt(str, i);
                } catch (DeadObjectException unused) {
                    this.mClientBinderListener = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        return this.mPreferences.save(str, i);
    }

    public boolean save(String str, long j) {
        synchronized (this.mBinderPreLock) {
            if (this.mBinderPreferences != null) {
                this.mBinderPreferences.save(str, j);
            }
            if (this.mClientBinderListener != null) {
                try {
                    this.mClientBinderListener.onPutLong(str, j);
                } catch (DeadObjectException unused) {
                    this.mClientBinderListener = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        return this.mPreferences.save(str, j);
    }

    public boolean save(String str, String str2) {
        synchronized (this.mBinderPreLock) {
            if (this.mBinderPreferences != null) {
                this.mBinderPreferences.save(str, str2);
            }
            if (this.mClientBinderListener != null) {
                try {
                    this.mClientBinderListener.onPutString(str, str2);
                } catch (DeadObjectException unused) {
                    this.mClientBinderListener = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        return this.mPreferences.save(str, str2);
    }

    public boolean save(String str, boolean z) {
        synchronized (this.mBinderPreLock) {
            if (this.mBinderPreferences != null) {
                this.mBinderPreferences.save(str, z);
            }
            if (this.mClientBinderListener != null) {
                try {
                    this.mClientBinderListener.onPutBoolean(str, z);
                } catch (DeadObjectException unused) {
                    this.mClientBinderListener = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        return this.mPreferences.save(str, z);
    }
}
