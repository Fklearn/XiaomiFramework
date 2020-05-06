package b.b.a.d.b;

import android.os.AsyncTask;
import android.util.Pair;
import b.b.a.e.n;
import com.miui.securitycenter.R;

class t extends AsyncTask<Void, Void, Pair<Integer, Integer>> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ v f1406a;

    t(v vVar) {
        this.f1406a = vVar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Pair<Integer, Integer> doInBackground(Void... voidArr) {
        return new Pair<>(Integer.valueOf(n.a(this.f1406a.m, 2)), Integer.valueOf(n.c(this.f1406a.m, 2)));
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Pair<Integer, Integer> pair) {
        v vVar = this.f1406a;
        vVar.e.a(vVar.m.getResources().getQuantityString(R.plurals.st_show_num_number, ((Integer) pair.first).intValue(), new Object[]{pair.first}));
        v vVar2 = this.f1406a;
        vVar2.f.a(vVar2.m.getResources().getQuantityString(R.plurals.st_show_num_number, ((Integer) pair.second).intValue(), new Object[]{pair.second}));
    }
}
