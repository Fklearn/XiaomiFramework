package b.b.a.e;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import b.b.a.d.b.y;
import b.b.a.e.c;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.util.IOUtils;

public class i {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static AtomicBoolean f1435a = new AtomicBoolean(false);
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public static AtomicBoolean f1436b = new AtomicBoolean(false);

    /* renamed from: c  reason: collision with root package name */
    private static volatile i f1437c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Context f1438d;
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, Pair<String, String>> e = new ConcurrentHashMap<>();
    private ThreadPoolExecutor f = ((ThreadPoolExecutor) Executors.newFixedThreadPool(5));
    private ContentResolver g;
    private b h;
    /* access modifiers changed from: private */
    public ArrayList<a> i = new ArrayList<>();
    /* access modifiers changed from: private */
    public Handler j = new h(this, Looper.getMainLooper());

    public interface a {
        void a();
    }

    public class b extends ContentObserver {
        public b(Handler handler) {
            super(handler);
        }

        public void onChange(boolean z) {
            if (i.this.j.hasMessages(2)) {
                i.this.j.removeMessages(2);
            }
            i.this.j.sendEmptyMessageDelayed(2, 3000);
        }
    }

    private class c implements Runnable {
        private c() {
        }

        /* synthetic */ c(i iVar, h hVar) {
            this();
        }

        public void run() {
            StringBuilder sb;
            ArrayList arrayList = new ArrayList();
            i.this.e.clear();
            try {
                Cursor query = i.this.f1438d.getContentResolver().query(c.a.f1419c, y.t, (String) null, (String[]) null, "date DESC");
                if (query != null) {
                    while (query.moveToNext()) {
                        String string = query.getString(query.getColumnIndex("address"));
                        arrayList.add(string);
                        i.this.e.put(string, n.d(i.this.f1438d, string));
                    }
                }
                i.f1435a.set(true);
                IOUtils.closeQuietly(query);
                i.f1436b.set(false);
                sb = new StringBuilder();
            } catch (Exception e) {
                Log.e("AntiSpamNameLoader", "loadAll Exception!", e);
                IOUtils.closeQuietly((Closeable) null);
                i.f1436b.set(false);
                sb = new StringBuilder();
            } catch (Throwable th) {
                IOUtils.closeQuietly((Closeable) null);
                i.f1436b.set(false);
                Log.i("AntiSpamNameLoader", "load state : " + i.f1435a.get());
                throw th;
            }
            sb.append("load state : ");
            sb.append(i.f1435a.get());
            Log.i("AntiSpamNameLoader", sb.toString());
        }
    }

    public interface d {
        void a(String str, Pair<String, String> pair);
    }

    private class e implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private String f1441a;

        /* renamed from: b  reason: collision with root package name */
        private d f1442b;

        public e(String str, d dVar) {
            this.f1441a = str;
            this.f1442b = dVar;
        }

        public void run() {
            f fVar = new f(this.f1441a, n.d(i.this.f1438d, this.f1441a), this.f1442b);
            Message message = new Message();
            message.what = 1;
            message.obj = fVar;
            i.this.j.sendMessage(message);
        }
    }

    private class f {

        /* renamed from: a  reason: collision with root package name */
        String f1444a;

        /* renamed from: b  reason: collision with root package name */
        Pair<String, String> f1445b;

        /* renamed from: c  reason: collision with root package name */
        d f1446c;

        public f(String str, Pair<String, String> pair, d dVar) {
            this.f1444a = str;
            this.f1445b = pair;
            this.f1446c = dVar;
        }
    }

    private i(Context context) {
        this.f1438d = context.getApplicationContext();
        Uri uri = ContactsContract.PhoneLookup.CONTENT_FILTER_URI;
        this.h = new b(this.j);
        this.g = this.f1438d.getContentResolver();
        this.g.registerContentObserver(uri, false, this.h);
    }

    public static i a(Context context) {
        if (f1437c == null) {
            synchronized (i.class) {
                if (f1437c == null) {
                    f1437c = new i(context);
                }
            }
        }
        return f1437c;
    }

    public Pair<String, String> a(String str, d dVar) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if (this.e.containsKey(str)) {
            return this.e.get(str);
        }
        this.f.execute(new e(str, dVar));
        return null;
    }

    public void a(a aVar) {
        if (!this.i.contains(aVar)) {
            this.i.add(aVar);
        }
    }

    public void b(a aVar) {
        this.i.remove(aVar);
    }

    public boolean c() {
        return f1435a.get();
    }

    public void d() {
        if (f1436b.compareAndSet(false, true)) {
            this.f.execute(new c(this, (h) null));
        }
    }
}
