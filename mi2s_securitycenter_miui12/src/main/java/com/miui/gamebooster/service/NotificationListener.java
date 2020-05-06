package com.miui.gamebooster.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import b.b.o.g.e;
import com.miui.gamebooster.service.ISecurityCenterNotificationListener;

public class NotificationListener extends NotificationListenerService {

    /* renamed from: a  reason: collision with root package name */
    private INotificationListenerBinder f4795a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public RemoteCallbackList<INotificationListenerCallback> f4796b = new RemoteCallbackList<>();

    public class INotificationListenerBinder extends ISecurityCenterNotificationListener.Stub {
        public INotificationListenerBinder() {
        }

        public void a(INotificationListenerCallback iNotificationListenerCallback) {
            if (iNotificationListenerCallback != null) {
                synchronized (NotificationListener.this.f4796b) {
                    NotificationListener.this.f4796b.unregister(iNotificationListenerCallback);
                }
            }
        }

        public void b(INotificationListenerCallback iNotificationListenerCallback) {
            Log.i("GBNotificationListener", "registerCallback");
            if (iNotificationListenerCallback != null) {
                synchronized (NotificationListener.this.f4796b) {
                    NotificationListener.this.f4796b.register(iNotificationListenerCallback);
                }
            }
        }
    }

    private void a(StatusBarNotification statusBarNotification) {
        synchronized (this.f4796b) {
            int beginBroadcast = this.f4796b.beginBroadcast();
            while (beginBroadcast > 0) {
                beginBroadcast--;
                try {
                    this.f4796b.getBroadcastItem(beginBroadcast).onNotificationPostedCallBack(statusBarNotification);
                } catch (RemoteException e) {
                    Log.i("GBNotificationListener", "onQueryCouponsResult. an exception occurred!", e);
                }
            }
            this.f4796b.finishBroadcast();
        }
    }

    private void b(StatusBarNotification statusBarNotification) {
        synchronized (this.f4796b) {
            int beginBroadcast = this.f4796b.beginBroadcast();
            while (beginBroadcast > 0) {
                beginBroadcast--;
                try {
                    this.f4796b.getBroadcastItem(beginBroadcast).onNotificationRemovedCallBack(statusBarNotification);
                } catch (RemoteException e) {
                    Log.i("GBNotificationListener", "onQueryCouponsResult. an exception occurred!", e);
                }
            }
            this.f4796b.finishBroadcast();
        }
    }

    public IBinder onBind(Intent intent) {
        Log.i("GBNotificationListener", "return onBinder");
        return this.f4795a;
    }

    public void onCreate() {
        super.onCreate();
        this.f4795a = new INotificationListenerBinder();
        Class<NotificationListenerService> cls = NotificationListenerService.class;
        try {
            e.a((Class<? extends Object>) cls, (Object) this, "registerAsSystemService", (Class<?>[]) new Class[]{Context.class, ComponentName.class, Integer.TYPE}, this, new ComponentName(getPackageName(), getClass().getCanonicalName()), -1);
        } catch (Exception e) {
            Log.e("GameBoosterReflectUtils", "Unable to register notification listener", e);
        }
    }

    public void onDestroy() {
        try {
            e.a((Class<? extends Object>) NotificationListenerService.class, (Object) this, "unregisterAsSystemService", (Class<?>[]) null, new Object[0]);
        } catch (Exception e) {
            Log.e("GameBoosterReflectUtils", "Unable to register notification listener", e);
        }
        super.onDestroy();
    }

    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        a(statusBarNotification);
    }

    public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
        b(statusBarNotification);
    }
}
