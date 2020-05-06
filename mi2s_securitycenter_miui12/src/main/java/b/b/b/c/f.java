package b.b.b.c;

class f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ long f1496a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ g f1497b;

    f(g gVar, long j) {
        this.f1497b = gVar;
        this.f1496a = j;
    }

    public void run() {
        this.f1497b.f1500c.getSharedPreferences("av_sidekick_settings", 0).edit().putLong("side_kick_last_time", this.f1496a).commit();
    }
}
