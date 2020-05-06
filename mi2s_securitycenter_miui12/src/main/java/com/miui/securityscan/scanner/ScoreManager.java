package com.miui.securityscan.scanner;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import b.b.b.b;
import b.b.b.p;
import b.b.c.j.e;
import com.miui.antivirus.model.k;
import com.miui.luckymoney.config.CommonPerConstants;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.memory.MemoryModel;
import com.miui.securitycenter.memory.d;
import com.miui.securityscan.M;
import com.miui.securityscan.c.a;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.model.GroupModel;
import com.miui.securityscan.model.system.VirusScanModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreManager {

    /* renamed from: a  reason: collision with root package name */
    private static final boolean f7869a = a.f7625a;

    /* renamed from: b  reason: collision with root package name */
    private static ScoreManager f7870b;

    /* renamed from: c  reason: collision with root package name */
    private List<GroupModel> f7871c = Collections.synchronizedList(new ArrayList());

    /* renamed from: d  reason: collision with root package name */
    private List<GroupModel> f7872d = Collections.synchronizedList(new ArrayList());
    private Map<String, k> e = new ConcurrentHashMap();
    private Map<String, d> f = new ConcurrentHashMap();
    private Map<String, ResultModel> g = new ConcurrentHashMap();
    private boolean h = false;
    private int i;
    private long j;
    private long k;
    private long l;
    private List<k> m;
    private List<k> n;

    public static class ResultModel extends MemoryModel {
        private List<String> infoList = new ArrayList();
        private boolean isChecked;

        public ResultModel() {
        }

        public ResultModel(MemoryModel memoryModel) {
            setAppName(memoryModel.getAppName());
            setLockState(memoryModel.getLockState());
            setMemorySize(memoryModel.getMemorySize());
            setPackageName(memoryModel.getPackageName());
        }

        public void addInfo(String str) {
            this.infoList.add(str);
        }

        public List<String> getInfoList() {
            return this.infoList;
        }

        public boolean isChecked() {
            return this.isChecked;
        }

        public void setChecked(boolean z) {
            this.isChecked = z;
        }
    }

    private ScoreManager() {
    }

    private int A() {
        if (f7869a) {
            Log.d("ScoreManager", "getMinusPredictScore------------------------------------------------ ");
        }
        int i2 = 0;
        int i3 = 0;
        for (GroupModel modelList : this.f7871c) {
            for (AbsModel next : modelList.getModelList()) {
                if (next instanceof VirusScanModel) {
                    ((VirusScanModel) next).updateModelState(s() ? AbsModel.State.SAFE : AbsModel.State.DANGER);
                }
                if (next.isSafe() == AbsModel.State.DANGER) {
                    i3 += next.getScore();
                    if (f7869a) {
                        Log.d("ScoreManager", "SystemModel Minus ItemKey ==> " + next.getItemKey());
                    }
                }
            }
        }
        if (f7869a) {
            Log.d("ScoreManager", "SystemModel Minus Score = " + i3);
        }
        int b2 = b(b());
        if (f7869a) {
            Log.d("ScoreManager", "Memory Minus Score = " + b2);
        }
        int i4 = i3 + b2;
        int z = z();
        if (f7869a) {
            Log.d("ScoreManager", "Cache Minus Score = " + z);
        }
        int i5 = i4 + z;
        this.h = i5 == 0;
        for (GroupModel modelList2 : this.f7872d) {
            for (AbsModel next2 : modelList2.getModelList()) {
                if (next2.isSafe() == AbsModel.State.DANGER) {
                    i2 += next2.getScore();
                    if (f7869a) {
                        Log.d("ScoreManager", "ManualModel Minus ItemKey ==> " + next2.getItemKey());
                    }
                }
            }
        }
        if (f7869a) {
            Log.d("ScoreManager", "Manual Minus Score = " + i2);
        }
        int i6 = i5 + i2;
        if (s() || i6 >= 41) {
            return i6;
        }
        return 41;
    }

    private int b(long j2) {
        if (System.currentTimeMillis() - M.b(0) < 300000) {
            return 0;
        }
        try {
            long c2 = e.c();
            Log.d("ScoreManager", "tm = " + c2);
            if (c2 > 0) {
                int i2 = (int) ((j2 * 100) / c2);
                if (i2 >= 50) {
                    return 15;
                }
                if (i2 >= 40) {
                    return 10;
                }
                if (i2 >= 30) {
                    return 8;
                }
                return i2 >= 20 ? 5 : 0;
            }
        } catch (Exception e2) {
            Log.e("ScoreManager", "getMemoryMinusPredictScore", e2);
        }
        return 0;
    }

    public static synchronized ScoreManager e() {
        ScoreManager scoreManager;
        synchronized (ScoreManager.class) {
            if (f7870b == null) {
                f7870b = new ScoreManager();
            }
            scoreManager = f7870b;
        }
        return scoreManager;
    }

    private int z() {
        long currentTimeMillis = System.currentTimeMillis();
        long b2 = M.b(0);
        if (b2 == 0) {
            return 0;
        }
        long j2 = currentTimeMillis - b2;
        if (j2 > 259200000) {
            return 15;
        }
        if (j2 > CommonPerConstants.DEFAULT.DEFAULT_UPDATE_FREQUENCY_DEFAULT) {
            return 10;
        }
        if (j2 > 86400000) {
            return 8;
        }
        if (j2 > 43200000) {
            return 5;
        }
        return j2 > 21600000 ? 3 : 0;
    }

    public long a() {
        return this.l;
    }

    public void a(int i2) {
        List<GroupModel> list = this.f7872d;
        if (list != null && !list.isEmpty()) {
            boolean z = false;
            for (GroupModel modelList : this.f7872d) {
                Iterator<AbsModel> it = modelList.getModelList().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    AbsModel next = it.next();
                    if (next.getIndex() == i2) {
                        next.scan();
                        z = true;
                        continue;
                        break;
                    }
                }
                if (z) {
                    return;
                }
            }
        }
    }

    public void a(long j2) {
        this.l = j2;
    }

    public void a(Context context) {
        if (!this.e.isEmpty()) {
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            for (String next : this.e.keySet()) {
                k kVar = this.e.get(next);
                if (kVar.c() == b.C0024b.INSTALLED_APP) {
                    ApplicationInfo applicationInfo = null;
                    try {
                        applicationInfo = packageManager.getApplicationInfo(kVar.b(), 0);
                    } catch (PackageManager.NameNotFoundException unused) {
                    }
                    if (applicationInfo == null) {
                        b(next);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void a(k kVar) {
        if (!TextUtils.isEmpty(kVar.e())) {
            this.e.put(kVar.e(), kVar);
        }
    }

    /* access modifiers changed from: protected */
    public void a(d dVar) {
        this.f.put(dVar.d(), dVar);
    }

    public void a(AbsModel absModel, AbsModel.State state) {
        List<GroupModel> list = this.f7872d;
        if (list != null && absModel != null) {
            for (GroupModel modelList : list) {
                for (AbsModel next : modelList.getModelList()) {
                    String itemKey = next.getItemKey();
                    if (!TextUtils.isEmpty(itemKey) && itemKey.equals(absModel.getItemKey())) {
                        next.setSafe(state);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void a(ResultModel resultModel) {
        this.g.put(resultModel.getPackageName(), resultModel);
    }

    /* access modifiers changed from: protected */
    public void a(String str) {
        Log.d("removeAppInfo", "size before " + this.f.size());
        this.f.remove(str);
        Log.d("removeAppInfo", "size before " + this.f.size());
    }

    /* access modifiers changed from: protected */
    public void a(List<GroupModel> list) {
        this.f7872d = Collections.synchronizedList(list);
    }

    public long b() {
        long j2 = 0;
        for (d next : this.f.values()) {
            if (next.f()) {
                j2 += next.b();
            }
        }
        return j2;
    }

    public void b(int i2) {
        this.i = i2;
    }

    /* access modifiers changed from: protected */
    public void b(String str) {
        this.e.remove(str);
    }

    public void b(List<k> list) {
        if (list != null) {
            this.n = new ArrayList(list);
        } else {
            this.n = null;
        }
    }

    public long c() {
        return this.j;
    }

    /* access modifiers changed from: protected */
    public void c(List<GroupModel> list) {
        this.f7871c = Collections.synchronizedList(list);
    }

    public int d() {
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        List<k> i2 = i();
        if (i2 != null) {
            for (k next : i2) {
                concurrentHashMap.put(next.e(), next);
            }
        }
        List<k> o = o();
        if (o != null) {
            for (k next2 : o) {
                concurrentHashMap.put(next2.e(), next2);
            }
        }
        return concurrentHashMap.size();
    }

    public int f() {
        int i2 = 0;
        for (GroupModel modelList : this.f7872d) {
            for (AbsModel next : modelList.getModelList()) {
                if (next.isSafe() == AbsModel.State.DANGER) {
                    i2 += next.getScore();
                }
            }
        }
        return i2;
    }

    public List<GroupModel> g() {
        ArrayList arrayList = new ArrayList();
        for (GroupModel next : this.f7872d) {
            Iterator<AbsModel> it = next.getModelList().iterator();
            while (true) {
                if (it.hasNext()) {
                    if (it.next().isSafe() != AbsModel.State.SAFE) {
                        arrayList.add(next);
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        return arrayList;
    }

    public List<d> h() {
        ArrayList arrayList = new ArrayList();
        for (String str : this.f.keySet()) {
            d dVar = this.f.get(str);
            if (dVar.f()) {
                arrayList.add(dVar);
            }
        }
        return arrayList;
    }

    public List<k> i() {
        return this.n;
    }

    public int j() {
        int A = A();
        if (A > 100) {
            A = 100;
        } else if (A < 0) {
            A = 0;
        }
        return 100 - A;
    }

    public List<GroupModel> k() {
        ArrayList arrayList = new ArrayList();
        for (GroupModel next : this.f7871c) {
            Iterator<AbsModel> it = next.getModelList().iterator();
            while (true) {
                if (it.hasNext()) {
                    if (it.next().isSafe() != AbsModel.State.SAFE) {
                        arrayList.add(next);
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        return arrayList;
    }

    public List<GroupModel> l() {
        ArrayList arrayList = new ArrayList();
        for (GroupModel next : this.f7871c) {
            Iterator<AbsModel> it = next.getModelList().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                AbsModel next2 = it.next();
                if (next2.isSafe() == AbsModel.State.SAFE && !next2.isScanHide()) {
                    arrayList.add(next);
                    break;
                }
            }
        }
        return arrayList;
    }

    public int m() {
        return this.i;
    }

    public List<k> n() {
        return new ArrayList(this.e.values());
    }

    public List<k> o() {
        return this.m;
    }

    public int p() {
        return this.e.size();
    }

    public int q() {
        return p.f() + p.e();
    }

    public boolean r() {
        return !this.e.isEmpty();
    }

    public boolean s() {
        return q() <= 0 && this.e.isEmpty();
    }

    public boolean t() {
        long j2 = Settings.Secure.getLong(Application.d().getContentResolver(), "key_latest_virus_scan_date", 0);
        long currentTimeMillis = System.currentTimeMillis() - j2;
        return (j2 <= 0 || currentTimeMillis <= 0) ? q() <= 0 && p() <= 0 : q() <= 0 && p() <= 0 && currentTimeMillis <= 259200000;
    }

    /* access modifiers changed from: protected */
    public void u() {
        this.f7871c.clear();
        this.f7872d.clear();
        this.e.clear();
        this.f.clear();
        this.g.clear();
    }

    public void v() {
        long j2 = 0;
        long j3 = 0;
        for (ResultModel next : this.g.values()) {
            if (next.isChecked()) {
                j3 += next.getMemorySize();
            }
        }
        this.k = j3;
        for (ResultModel memorySize : this.g.values()) {
            j2 += memorySize.getMemorySize();
        }
        this.l = j2;
    }

    public void w() {
        long j2 = 0;
        for (d next : this.f.values()) {
            if (next.f()) {
                j2 += next.b();
            }
        }
        this.j = j2;
    }

    public void x() {
        this.m = new ArrayList(this.e.values());
    }

    public void y() {
        for (GroupModel next : this.f7871c) {
            boolean z = false;
            Iterator<AbsModel> it = next.getModelList().iterator();
            while (true) {
                if (it.hasNext()) {
                    if (it.next() instanceof VirusScanModel) {
                        next.scan();
                        z = true;
                        continue;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (z) {
                return;
            }
        }
    }
}
