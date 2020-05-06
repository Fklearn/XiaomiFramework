package miui.upnp.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public abstract class Binding {
    /* access modifiers changed from: private */
    public static final String TAG = Binding.class.getSimpleName();
    private ServiceConnection connection;
    private Context context;
    /* access modifiers changed from: private */
    public final ServiceState serviceState = new ServiceState();

    private enum State {
        UNBIND,
        BINDING,
        BOUND
    }

    /* access modifiers changed from: protected */
    public abstract void onServiceConnected(ComponentName componentName, IBinder iBinder);

    /* access modifiers changed from: protected */
    public abstract void onServiceDisconnected(ComponentName componentName);

    private class ServiceState {
        State value;
        boolean waiting;

        private ServiceState() {
            this.waiting = false;
            this.value = State.UNBIND;
        }
    }

    public Binding(Context context2) {
        this.context = context2.getApplicationContext();
    }

    public Context getContext() {
        return this.context;
    }

    /* access modifiers changed from: protected */
    public synchronized boolean bind(String packageName, String serviceName) {
        boolean z;
        z = false;
        if (this.context == null) {
            Log.d(TAG, "context is null");
        } else if (this.serviceState.value != State.UNBIND) {
            Log.e(TAG, String.format("bind, but serviceState is: %s", new Object[]{this.serviceState.value.toString()}));
        } else {
            this.serviceState.value = State.BINDING;
            ServiceConnection conn = new MyServiceConnection();
            Intent intent = new Intent(serviceName);
            intent.setPackage(packageName);
            if (this.context.bindService(intent, conn, 1)) {
                this.connection = conn;
                synchronized (this.serviceState) {
                    if (this.serviceState.value == State.BINDING) {
                        Log.d(TAG, String.format("(%s) waiting...", new Object[]{serviceName}));
                        try {
                            this.serviceState.waiting = true;
                            this.serviceState.wait();
                            this.serviceState.waiting = false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                Log.d(TAG, "bindService failed");
                this.serviceState.value = State.UNBIND;
            }
        }
        if (this.serviceState.value == State.BOUND) {
            z = true;
        }
        return z;
    }

    public synchronized boolean unbind() {
        boolean z;
        Log.d(TAG, "unbind");
        z = false;
        if (this.serviceState.value == State.UNBIND) {
            Log.e(TAG, String.format("unbind, but serviceState is: %s", new Object[]{this.serviceState.value.toString()}));
        } else {
            if (this.connection != null) {
                this.context.unbindService(this.connection);
                this.connection = null;
                onServiceDisconnected((ComponentName) null);
            }
            this.serviceState.value = State.UNBIND;
        }
        if (this.serviceState.value == State.UNBIND) {
            z = true;
        }
        return z;
    }

    public boolean isBound() {
        return this.serviceState.value == State.BOUND;
    }

    private class MyServiceConnection implements ServiceConnection {
        private MyServiceConnection() {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(Binding.TAG, String.format("onServiceConnected: %s", new Object[]{name.getShortClassName()}));
            Binding.this.onServiceConnected(name, service);
            synchronized (Binding.this.serviceState) {
                Binding.this.serviceState.value = State.BOUND;
                if (Binding.this.serviceState.waiting) {
                    Binding.this.serviceState.notify();
                }
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.d(Binding.TAG, String.format("onServiceDisconnected: %s", new Object[]{name.getShortClassName()}));
            Binding.this.onServiceDisconnected(name);
            synchronized (Binding.this.serviceState) {
                Binding.this.serviceState.value = State.UNBIND;
                if (Binding.this.serviceState.waiting) {
                    Binding.this.serviceState.notify();
                }
            }
        }
    }
}
