package androidx.preference;

class s implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PreferenceGroup f1056a;

    s(PreferenceGroup preferenceGroup) {
        this.f1056a = preferenceGroup;
    }

    public void run() {
        synchronized (this) {
            this.f1056a.f1019a.clear();
        }
    }
}
