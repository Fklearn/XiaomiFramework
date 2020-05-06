package b.b.a.d.b;

import android.os.AsyncTask;
import android.util.Pair;
import com.miui.securitycenter.R;

class n extends AsyncTask<Void, Void, Pair<Integer, Integer>> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ s f1401a;

    n(s sVar) {
        this.f1401a = sVar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Pair<Integer, Integer> doInBackground(Void... voidArr) {
        s sVar = this.f1401a;
        int a2 = b.b.a.e.n.a(sVar.m, sVar.r);
        s sVar2 = this.f1401a;
        return new Pair<>(Integer.valueOf(a2), Integer.valueOf(b.b.a.e.n.c(sVar2.m, sVar2.r)));
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Pair<Integer, Integer> pair) {
        s sVar = this.f1401a;
        sVar.e.a(sVar.m.getResources().getQuantityString(R.plurals.st_show_num_number, ((Integer) pair.first).intValue(), new Object[]{pair.first}));
        s sVar2 = this.f1401a;
        sVar2.f.a(sVar2.m.getResources().getQuantityString(R.plurals.st_show_num_number, ((Integer) pair.second).intValue(), new Object[]{pair.second}));
    }
}
