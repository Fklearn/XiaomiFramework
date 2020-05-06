package b.b.c.c.b;

class i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ k f1626a;

    i(k kVar) {
        this.f1626a = kVar;
    }

    public void run() {
        if (this.f1626a.f1628a != null && this.f1626a.f1628a.isShowing() && this.f1626a.f1629b != null && !this.f1626a.f1629b.isFinishing()) {
            try {
                this.f1626a.f1628a.dismiss();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
}
