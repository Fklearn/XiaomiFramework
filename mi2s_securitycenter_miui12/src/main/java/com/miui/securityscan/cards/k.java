package com.miui.securityscan.cards;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.text.TextUtils;
import com.miui.networkassistant.config.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class k {

    /* renamed from: a  reason: collision with root package name */
    private static k f7657a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public HashMap<String, String> f7658b = new HashMap<>();

    /* renamed from: c  reason: collision with root package name */
    private ArrayList<a> f7659c = new ArrayList<>();

    public interface a {
        void a(String str);
    }

    private class b extends BroadcastReceiver {
        private b() {
        }

        public void onReceive(Context context, Intent intent) {
            Uri data;
            if (intent != null && (data = intent.getData()) != null) {
                String action = intent.getAction();
                String schemeSpecificPart = data.getSchemeSpecificPart();
                if (!TextUtils.isEmpty(schemeSpecificPart)) {
                    if (Constants.System.ACTION_PACKAGE_ADDED.equals(action)) {
                        k.this.f7658b.put(schemeSpecificPart, (Object) null);
                    } else if (Constants.System.ACTION_PACKAGE_REMOVED.equals(action)) {
                        k.this.f7658b.remove(schemeSpecificPart);
                        g.a(context).c(schemeSpecificPart);
                    }
                    k.this.b(schemeSpecificPart);
                }
            }
        }
    }

    private k(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Constants.System.ACTION_PACKAGE_ADDED);
        intentFilter.addDataScheme("package");
        context.registerReceiver(new b(), intentFilter);
        for (PackageInfo packageInfo : b.b.c.b.b.a(context).a()) {
            this.f7658b.put(packageInfo.packageName, (Object) null);
        }
    }

    public static synchronized k a(Context context) {
        k kVar;
        synchronized (k.class) {
            if (f7657a == null) {
                f7657a = new k(context.getApplicationContext());
            }
            kVar = f7657a;
        }
        return kVar;
    }

    /* access modifiers changed from: private */
    public void b(String str) {
        Iterator<a> it = this.f7659c.iterator();
        while (it.hasNext()) {
            it.next().a(str);
        }
    }

    public void a(a aVar) {
        if (aVar != null) {
            this.f7659c.add(aVar);
            return;
        }
        throw new NullPointerException(" listener is null");
    }

    public boolean a(String str) {
        return this.f7658b.containsKey(str);
    }

    public void b(a aVar) {
        if (aVar != null) {
            this.f7659c.remove(aVar);
            return;
        }
        throw new NullPointerException(" listener is null");
    }
}
