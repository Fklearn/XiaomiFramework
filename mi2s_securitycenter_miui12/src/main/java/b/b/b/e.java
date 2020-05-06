package b.b.b;

import android.os.AsyncTask;
import org.json.JSONObject;

class e extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ JSONObject f1543a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ g f1544b;

    e(g gVar, JSONObject jSONObject) {
        this.f1544b = gVar;
        this.f1543a = jSONObject;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        this.f1544b.b(this.f1543a);
        return null;
    }
}
