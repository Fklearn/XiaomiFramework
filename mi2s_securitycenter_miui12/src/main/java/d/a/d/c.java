package d.a.d;

import d.a.d;

class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d f8687a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ long f8688b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ long f8689c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ g f8690d;

    c(g gVar, d dVar, long j, long j2) {
        this.f8690d = gVar;
        this.f8687a = dVar;
        this.f8688b = j;
        this.f8689c = j2;
    }

    public void run() {
        i animTask = this.f8687a.getAnimTask();
        this.f8690d.a(animTask, this.f8688b, this.f8689c, new long[0]);
        this.f8687a.onFrameEnd(animTask.a());
        this.f8690d.b(this.f8687a);
    }
}
