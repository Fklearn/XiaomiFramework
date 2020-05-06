package b.b.a;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import b.b.a.e.n;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.antispam.policy.KeywordsBlackListPolicy;
import com.miui.antispam.policy.MmsPolicy;
import com.miui.antispam.policy.a;
import com.miui.antispam.policy.a.d;
import com.miui.antispam.policy.a.e;
import com.miui.antispam.policy.a.f;
import com.miui.antispam.policy.a.g;
import com.miui.antispam.policy.b;
import com.miui.maml.elements.AdvancedSlider;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.TreeMap;
import miui.cloud.common.XSimChangeNotification;
import miui.os.Build;
import miui.provider.ExtraTelephony;

public class c implements a.b {

    /* renamed from: a  reason: collision with root package name */
    private static volatile c f1320a;

    /* renamed from: b  reason: collision with root package name */
    private Context f1321b;

    /* renamed from: c  reason: collision with root package name */
    private d f1322c;

    /* renamed from: d  reason: collision with root package name */
    private b f1323d;
    private TreeMap<g, a> e = new TreeMap<>(new b(this));
    private a f;

    private c(Context context) {
        this.f1321b = context.getApplicationContext();
        this.f1322c = new d(this.f1321b);
        for (g gVar : g.values()) {
            try {
                this.e.put(gVar, (a) gVar.w.getConstructor(new Class[]{Context.class, a.b.class, d.class, g.class}).newInstance(new Object[]{this.f1321b, this, this.f1322c, gVar}));
            } catch (InstantiationException e2) {
                e2.printStackTrace();
            } catch (IllegalAccessException e3) {
                e3.printStackTrace();
            } catch (IllegalArgumentException e4) {
                e4.printStackTrace();
            } catch (InvocationTargetException e5) {
                e5.printStackTrace();
            } catch (NoSuchMethodException e6) {
                e6.printStackTrace();
            }
        }
        this.f1323d = new b(this.f1321b);
        this.f1322c.a();
    }

    public static synchronized c a(Context context) {
        c cVar;
        synchronized (c.class) {
            if (f1320a == null) {
                f1320a = new c(context);
            }
            cVar = f1320a;
        }
        return cVar;
    }

    public int a(SQLiteDatabase sQLiteDatabase, String str, String str2, String[] strArr) {
        Cursor rawQuery = sQLiteDatabase.rawQuery("select * from " + str + " where " + str2, strArr);
        int i = -1;
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                try {
                    i = rawQuery.getInt(rawQuery.getColumnIndex("_id"));
                    this.f1322c.a(rawQuery.getString(rawQuery.getColumnIndex(DataSchemeDataSource.SCHEME_DATA)), rawQuery.getInt(rawQuery.getColumnIndex("type")), rawQuery.getInt(rawQuery.getColumnIndex(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID)));
                } catch (Exception e2) {
                    e2.printStackTrace();
                } catch (Throwable th) {
                    rawQuery.close();
                    throw th;
                }
            }
            rawQuery.close();
        }
        return i;
    }

    public int a(e eVar) {
        a.C0035a handleData;
        Log.d("PolicyManager", "start call policy judge");
        int i = 0;
        if (b.b.a.e.c.b(this.f1321b, eVar.f2370c)) {
            for (Map.Entry next : this.e.entrySet()) {
                a aVar = (a) next.getValue();
                if ((((g) next.getKey()).x == f.CALL || ((g) next.getKey()).x == f.BOTH) && (handleData = aVar.handleData(eVar)) != null) {
                    i = handleData.f2354b;
                    if (handleData.f2353a) {
                        break;
                    }
                }
            }
        }
        Log.d("PolicyManager", "conventional call policy judge finish" + i);
        return i;
    }

    public int a(String str, String str2) {
        return this.f1323d.a(str, str2);
    }

    public d a() {
        return this.f1322c;
    }

    public a a(g gVar) {
        return this.e.get(gVar);
    }

    public void a(String str, int i, int i2) {
        this.f1322c.b(str, i, i2);
    }

    public void a(String str, int i, int i2, int i3) {
        this.f1322c.d(str, i, i2, i3);
    }

    public int b(e eVar) {
        long currentTimeMillis = System.currentTimeMillis();
        int i = 0;
        String str = "";
        if (!eVar.i || Build.IS_INTERNATIONAL_BUILD) {
            Log.d("PolicyManager", "start sms policy judge");
            if (b.b.a.e.c.b(this.f1321b, eVar.f2370c)) {
                for (Map.Entry next : this.e.entrySet()) {
                    a aVar = (a) next.getValue();
                    if (((g) next.getKey()).x == f.SMS || ((g) next.getKey()).x == f.BOTH) {
                        long currentTimeMillis2 = System.currentTimeMillis();
                        a.C0035a handleData = aVar.handleData(eVar);
                        if (handleData != null) {
                            if (aVar instanceof KeywordsBlackListPolicy) {
                                str = handleData.f2355c;
                            }
                            i = handleData.f2354b;
                            if (handleData.f2353a) {
                                break;
                            }
                        }
                        b.b.a.a.a.a(((g) next.getKey()).v, System.currentTimeMillis() - currentTimeMillis2);
                    }
                }
            }
            Log.d("PolicyManager", "conventional sms policy judge finish");
            b.a a2 = this.f1323d.a(i, eVar);
            if (a2 != null && a2.f2386a) {
                i = a2.f2387b;
            }
        } else {
            Log.d("PolicyManager", "start mms policy judge");
            if (b.b.a.e.c.b(this.f1321b, eVar.f2370c)) {
                if (this.f == null) {
                    this.f = new MmsPolicy(this.f1321b, (a.b) null, (d) null, (g) null);
                }
                a.C0035a handleData2 = this.f.handleData(eVar);
                if (handleData2 != null) {
                    i = handleData2.f2354b;
                }
            }
        }
        int realBlockType = ExtraTelephony.getRealBlockType(i);
        if (realBlockType >= 3) {
            n.a(this.f1321b, eVar.f2369b, realBlockType, str);
        }
        Log.d("PolicyManager", "all sms policy judge finished : " + i);
        b.b.a.a.a.a(System.currentTimeMillis() - currentTimeMillis);
        return i;
    }

    public void b(SQLiteDatabase sQLiteDatabase, String str, String str2, String[] strArr) {
        Cursor rawQuery = sQLiteDatabase.rawQuery("select * from " + str + " where " + str2, strArr);
        if (rawQuery != null) {
            try {
                if (rawQuery.moveToNext()) {
                    this.f1322c.b(rawQuery.getString(rawQuery.getColumnIndex(DataSchemeDataSource.SCHEME_DATA)), rawQuery.getInt(rawQuery.getColumnIndex("type")), rawQuery.getInt(rawQuery.getColumnIndex(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID)));
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            } catch (Throwable th) {
                rawQuery.close();
                throw th;
            }
            rawQuery.close();
        }
    }

    public int c(SQLiteDatabase sQLiteDatabase, String str, String str2, String[] strArr) {
        Cursor rawQuery = sQLiteDatabase.rawQuery("select * from " + str + " where " + str2, strArr);
        int i = -1;
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                try {
                    i = rawQuery.getInt(rawQuery.getColumnIndex("_id"));
                    this.f1322c.c(rawQuery.getString(rawQuery.getColumnIndex("number")), rawQuery.getInt(rawQuery.getColumnIndex("type")), rawQuery.getInt(rawQuery.getColumnIndex(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID)));
                } catch (Exception e2) {
                    e2.printStackTrace();
                } catch (Throwable th) {
                    rawQuery.close();
                    throw th;
                }
            }
            rawQuery.close();
        }
        return i;
    }

    public void d(SQLiteDatabase sQLiteDatabase, String str, String str2, String[] strArr) {
        Cursor rawQuery = sQLiteDatabase.rawQuery("select * from " + str + " where " + str2, strArr);
        if (rawQuery != null) {
            try {
                if (rawQuery.moveToNext()) {
                    String string = rawQuery.getString(rawQuery.getColumnIndex("number"));
                    int i = rawQuery.getInt(rawQuery.getColumnIndex("type"));
                    int i2 = rawQuery.getInt(rawQuery.getColumnIndex(AdvancedSlider.STATE));
                    int i3 = rawQuery.getInt(rawQuery.getColumnIndex(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID));
                    this.f1322c.d(string, i2, i, i3);
                    if (!b.b.a.e.c.b(this.f1321b, i3)) {
                        b.b.a.e.c.a(this.f1321b, i3, true);
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            } catch (Throwable th) {
                rawQuery.close();
                throw th;
            }
            rawQuery.close();
        }
    }
}
