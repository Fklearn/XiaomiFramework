package com.miui.monthreport;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.miui.analytics.AnalyticsUtil;
import com.miui.securitycenter.h;
import com.xiaomi.stat.MiStat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private static d f5625a;

    /* renamed from: b  reason: collision with root package name */
    private LocationManager f5626b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public C0055d f5627c;

    public enum a {
        ALL("ALL", -1),
        NETWORK_PROVIDER("network", 2),
        GPS_PROVIDER("gps", 1),
        PASSIVE_PROVIDER("passive", 0);
        
        /* access modifiers changed from: private */
        public String f;
        /* access modifiers changed from: private */
        public int g;

        private a(String str, int i) {
            this.f = str;
            this.g = i;
        }
    }

    public interface b {
        void a(boolean z, Location location);
    }

    class c implements LocationListener {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public b f5632a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public boolean f5633b = false;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public boolean f5634c = false;
        /* access modifiers changed from: private */

        /* renamed from: d  reason: collision with root package name */
        public List<a> f5635d;
        /* access modifiers changed from: private */
        public Location e;

        public c(b bVar, List<a> list) {
            this.f5632a = bVar;
            this.f5635d = list;
        }

        public void onLocationChanged(Location location) {
            if (location != null) {
                this.e = location;
            }
            this.f5634c = true;
            d.this.f5627c.removeMessages(1, this);
            d.this.f5627c.sendMessage(d.b(1, this));
        }

        public void onProviderDisabled(String str) {
        }

        public void onProviderEnabled(String str) {
        }

        public void onStatusChanged(String str, int i, Bundle bundle) {
        }
    }

    /* renamed from: com.miui.monthreport.d$d  reason: collision with other inner class name */
    static class C0055d extends Handler {

        /* renamed from: a  reason: collision with root package name */
        private LocationManager f5636a;

        /* renamed from: b  reason: collision with root package name */
        private List<String> f5637b;

        public C0055d(Looper looper, LocationManager locationManager) {
            super(looper);
            this.f5636a = locationManager;
            this.f5637b = locationManager.getAllProviders();
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                Object obj = message.obj;
                if (obj != null && (obj instanceof c)) {
                    c cVar = (c) obj;
                    if (!cVar.f5633b) {
                        this.f5636a.removeUpdates(cVar);
                        if (cVar.f5634c) {
                            if (cVar.f5632a != null) {
                                cVar.f5632a.a(true, cVar.e);
                            }
                            boolean unused = cVar.f5633b = true;
                        } else if (cVar.f5635d == null || cVar.f5635d.size() <= 0) {
                            boolean unused2 = cVar.f5633b = true;
                            cVar.f5632a.a(false, (Location) null);
                        } else {
                            sendMessage(d.b(2, cVar));
                        }
                    }
                }
            } else if (i == 2) {
                try {
                    if (message.obj != null && (message.obj instanceof c)) {
                        c cVar2 = (c) message.obj;
                        if (cVar2.f5635d != null && cVar2.f5635d.size() > 0) {
                            a aVar = (a) cVar2.f5635d.remove(0);
                            if (this.f5637b.contains(aVar.f)) {
                                this.f5636a.requestLocationUpdates(aVar.f, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS, 5.0f, cVar2);
                                sendMessageDelayed(d.b(1, cVar2), 20000);
                            }
                        }
                    }
                } catch (Exception e) {
                    AnalyticsUtil.trackException(e);
                }
            }
        }
    }

    private d(Context context) {
        this.f5626b = (LocationManager) context.getSystemService(MiStat.Param.LOCATION);
        this.f5627c = new C0055d(context.getMainLooper(), this.f5626b);
    }

    public static synchronized d a(Context context) {
        d dVar;
        synchronized (d.class) {
            if (f5625a == null) {
                f5625a = new d(context);
            }
            dVar = f5625a;
        }
        return dVar;
    }

    private List<a> a(a... aVarArr) {
        ArrayList arrayList = new ArrayList();
        if (aVarArr == null || aVarArr.length == 0) {
            a((List<a>) arrayList);
        }
        int length = aVarArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            a aVar = aVarArr[i];
            if (aVar == a.ALL) {
                a((List<a>) arrayList);
                break;
            }
            if (!arrayList.contains(aVar)) {
                arrayList.add(aVar);
            }
            i++;
        }
        Collections.sort(arrayList, new c(this));
        return arrayList;
    }

    private void a(List<a> list) {
        list.clear();
        for (a aVar : a.values()) {
            if (aVar != a.ALL) {
                list.add(aVar);
            }
        }
    }

    /* access modifiers changed from: private */
    public static Message b(int i, c cVar) {
        Message message = new Message();
        message.what = i;
        message.obj = cVar;
        return message;
    }

    public void a(boolean z, b bVar, a... aVarArr) {
        Location location;
        if (h.i()) {
            List<a> a2 = a(aVarArr);
            if (!z) {
                Iterator<a> it = a2.iterator();
                loop0:
                while (true) {
                    location = null;
                    while (true) {
                        if (!it.hasNext()) {
                            break loop0;
                        }
                        try {
                            location = this.f5626b.getLastKnownLocation(it.next().f);
                        } catch (Exception unused) {
                            location = null;
                        }
                        if (location != null) {
                            if (System.currentTimeMillis() - location.getTime() <= 86400000) {
                                if (bVar != null) {
                                    bVar.a(true, location);
                                    break loop0;
                                }
                            }
                        }
                    }
                }
            } else {
                location = null;
            }
            if (location == null) {
                this.f5627c.sendMessage(b(2, new c(bVar, a2)));
            }
        }
    }
}
