package d.a.b;

import android.util.Log;
import d.a.a.g;
import d.a.d;
import d.a.d.i;
import d.a.d.j;
import d.a.g.C0575b;
import d.a.g.C0576c;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Object f8652a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ g f8653b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ f f8654c;

    e(f fVar, Object obj, g gVar) {
        this.f8654c = fVar;
        this.f8652a = obj;
        this.f8653b = gVar;
    }

    public void run() {
        a state = this.f8654c.getState(this.f8652a);
        d target = this.f8654c.getTarget();
        i animTask = target.getAnimTask();
        if (animTask.b()) {
            animTask.a((C0575b[]) state.d().toArray(new C0575b[0]));
        }
        Log.d("miuix_anim", "FolmeState.setTo, state = " + state);
        j.a(this.f8654c.f8658d, state, new long[0]);
        for (C0575b next : state.d()) {
            target.trackVelocity(next, (double) (next instanceof C0576c ? target.getIntValue((C0576c) next) : target.getValue(next)));
        }
        animTask.a(state, this.f8653b);
    }
}
