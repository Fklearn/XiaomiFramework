package com.miui.securityscan.scanner;

import android.util.ArrayMap;
import android.util.Log;
import com.miui.securitycenter.memory.a;
import com.miui.securitycenter.memory.d;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.miui.securityscan.scanner.j  reason: case insensitive filesystem */
class C0563j implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ a f7900a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ List f7901b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ C0564k f7902c;

    C0563j(C0564k kVar, a aVar, List list) {
        this.f7902c = kVar;
        this.f7900a = aVar;
        this.f7901b = list;
    }

    public void run() {
        a aVar;
        try {
            if (this.f7900a != null) {
                this.f7900a.f();
            }
            ArrayMap arrayMap = new ArrayMap();
            arrayMap.put(0, new ArrayList());
            arrayMap.put(10, new ArrayList());
            arrayMap.put(999, new ArrayList());
            for (d dVar : this.f7901b) {
                List list = (List) arrayMap.get(Integer.valueOf(dVar.e()));
                if (list != null) {
                    list.add(dVar.c());
                }
            }
            for (Integer num : arrayMap.keySet()) {
                ArrayMap arrayMap2 = new ArrayMap();
                List list2 = (List) arrayMap.get(num);
                Integer.valueOf(103);
                Class<?> cls = Class.forName("miui.process.ProcessConfig");
                arrayMap2.put((Integer) b.b.o.g.d.a("MemoryCheckManager", cls, "KILL_LEVEL_UNKNOWN", Integer.TYPE), list2);
                Integer.valueOf(6);
                Object newInstance = cls.getConstructor(new Class[]{Integer.TYPE, Integer.TYPE, ArrayMap.class}).newInstance(new Object[]{(Integer) b.b.o.g.d.a("MemoryCheckManager", cls, "POLICY_GARBAGE_CLEAN", Integer.TYPE), num, arrayMap2});
                b.b.o.g.d.a("MemoryCheckManager", (Object) newInstance, "setRemoveTaskNeeded", (Class<?>[]) new Class[]{Boolean.TYPE}, true);
                b.b.o.g.d.a("MemoryCheckManager", Class.forName("miui.process.ProcessManager"), "kill", (Class<?>[]) new Class[]{cls}, newInstance);
                Log.d("MemoryCheckManager", "startCleanup:userId = " + num + " ; " + list2.toString());
            }
            aVar = this.f7900a;
            if (aVar == null) {
                return;
            }
        } catch (Exception e) {
            Log.e("MemoryCheckManager", "startCleanup:", e);
            aVar = this.f7900a;
            if (aVar == null) {
                return;
            }
        } catch (Throwable th) {
            a aVar2 = this.f7900a;
            if (aVar2 != null) {
                aVar2.d();
            }
            throw th;
        }
        aVar.d();
    }
}
