package com.miui.firstaidkit;

import com.miui.securityscan.model.AbsModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class o {

    /* renamed from: a  reason: collision with root package name */
    private static o f3974a;

    /* renamed from: b  reason: collision with root package name */
    private List<AbsModel> f3975b = Collections.synchronizedList(new ArrayList());

    /* renamed from: c  reason: collision with root package name */
    private List<AbsModel> f3976c = Collections.synchronizedList(new ArrayList());

    /* renamed from: d  reason: collision with root package name */
    private List<AbsModel> f3977d = Collections.synchronizedList(new ArrayList());
    private List<AbsModel> e = Collections.synchronizedList(new ArrayList());
    private List<AbsModel> f = Collections.synchronizedList(new ArrayList());

    public static synchronized o f() {
        o oVar;
        synchronized (o.class) {
            if (f3974a == null) {
                f3974a = new o();
            }
            oVar = f3974a;
        }
        return oVar;
    }

    public List<AbsModel> a() {
        ArrayList arrayList = new ArrayList();
        for (AbsModel next : this.e) {
            if (next.isSafe() != AbsModel.State.SAFE) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public void a(List<AbsModel> list) {
        this.e = Collections.synchronizedList(list);
    }

    public List<AbsModel> b() {
        ArrayList arrayList = new ArrayList();
        for (AbsModel next : this.f3977d) {
            if (next.isSafe() != AbsModel.State.SAFE) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public void b(List<AbsModel> list) {
        this.f3976c = Collections.synchronizedList(list);
    }

    public List<AbsModel> c() {
        ArrayList arrayList = new ArrayList();
        for (AbsModel next : this.f) {
            if (next.isSafe() != AbsModel.State.SAFE) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public void c(List<AbsModel> list) {
        this.f3977d = Collections.synchronizedList(list);
    }

    public List<AbsModel> d() {
        ArrayList arrayList = new ArrayList();
        for (AbsModel next : this.f3975b) {
            if (next.isSafe() != AbsModel.State.SAFE) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public void d(List<AbsModel> list) {
        this.f = Collections.synchronizedList(list);
    }

    public List<AbsModel> e() {
        ArrayList arrayList = new ArrayList();
        for (AbsModel next : this.f3976c) {
            if (next.isSafe() != AbsModel.State.SAFE) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public void e(List<AbsModel> list) {
        this.f3975b = Collections.synchronizedList(list);
    }
}
