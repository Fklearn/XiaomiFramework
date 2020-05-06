package androidx.lifecycle;

class n implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ LiveData f998a;

    n(LiveData liveData) {
        this.f998a = liveData;
    }

    public void run() {
        Object obj;
        synchronized (this.f998a.f966b) {
            obj = this.f998a.f;
            this.f998a.f = LiveData.f965a;
        }
        this.f998a.a(obj);
    }
}
