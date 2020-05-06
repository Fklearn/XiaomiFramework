package com.miui.applicationlock;

import android.os.AsyncTask;
import com.miui.applicationlock.c.o;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

class Qa extends AsyncTask<Void, Void, List<Integer>> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ bb f3209a;

    Qa(bb bbVar) {
        this.f3209a = bbVar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public List<Integer> doInBackground(Void... voidArr) {
        ArrayList arrayList = new ArrayList();
        int size = o.a(this.f3209a.z).size();
        int size2 = o.b(this.f3209a.z).size();
        arrayList.add(Integer.valueOf(size));
        arrayList.add(Integer.valueOf(size2));
        return arrayList;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(List<Integer> list) {
        if (list.get(0).intValue() == 0) {
            this.f3209a.m.setEnabled(false);
        }
        this.f3209a.m.a(String.format(this.f3209a.getResources().getQuantityString(R.plurals.number_masked, list.get(1).intValue()), new Object[]{list.get(1)}));
    }
}
