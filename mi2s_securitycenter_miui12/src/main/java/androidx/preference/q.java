package androidx.preference;

import androidx.preference.PreferenceGroup;
import androidx.preference.r;
import androidx.recyclerview.widget.RecyclerView;

class q implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Preference f1045a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f1046b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ r f1047c;

    q(r rVar, Preference preference, String str) {
        this.f1047c = rVar;
        this.f1045a = preference;
        this.f1046b = str;
    }

    public void run() {
        RecyclerView.a adapter = this.f1047c.mList.getAdapter();
        if (adapter instanceof PreferenceGroup.b) {
            Preference preference = this.f1045a;
            int b2 = preference != null ? ((PreferenceGroup.b) adapter).b(preference) : ((PreferenceGroup.b) adapter).a(this.f1046b);
            if (b2 != -1) {
                this.f1047c.mList.f(b2);
            } else {
                adapter.registerAdapterDataObserver(new r.e(adapter, this.f1047c.mList, this.f1045a, this.f1046b));
            }
        } else if (adapter != null) {
            throw new IllegalStateException("Adapter must implement PreferencePositionCallback");
        }
    }
}
