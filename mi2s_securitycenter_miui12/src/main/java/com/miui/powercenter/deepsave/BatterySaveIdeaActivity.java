package com.miui.powercenter.deepsave;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import b.b.c.j.x;
import com.miui.securitycenter.R;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.util.ArrayList;
import java.util.Iterator;
import miui.app.Activity;

public class BatterySaveIdeaActivity extends Activity {

    /* renamed from: a  reason: collision with root package name */
    private AdapterView.OnItemClickListener f6991a = new a(this);

    /* renamed from: b  reason: collision with root package name */
    private ListView f6992b;

    /* renamed from: c  reason: collision with root package name */
    private a f6993c = new a(this);
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public ArrayList<IdeaModel> f6994d = new ArrayList<>();

    private class a extends BaseAdapter {

        /* renamed from: a  reason: collision with root package name */
        Context f6995a;

        a(Context context) {
            this.f6995a = context;
        }

        public int getCount() {
            return BatterySaveIdeaActivity.this.f6994d.size();
        }

        public Object getItem(int i) {
            return BatterySaveIdeaActivity.this.f6994d.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(this.f6995a).inflate(R.layout.pc_list_item_battery_save_idea_item, viewGroup, false);
                b bVar = new b((a) null);
                bVar.f6997a = (ImageView) view.findViewById(16908294);
                bVar.f6998b = (TextView) view.findViewById(16908310);
                view.setTag(bVar);
            }
            IdeaModel ideaModel = (IdeaModel) BatterySaveIdeaActivity.this.f6994d.get(i);
            b bVar2 = (b) view.getTag();
            com.miui.powercenter.utils.b.a(bVar2.f6997a, ideaModel.packageName);
            bVar2.f6998b.setText(ideaModel.title);
            bVar2.f6999c = ideaModel.url;
            return view;
        }
    }

    private static class b {

        /* renamed from: a  reason: collision with root package name */
        ImageView f6997a;

        /* renamed from: b  reason: collision with root package name */
        TextView f6998b;

        /* renamed from: c  reason: collision with root package name */
        String f6999c;

        private b() {
        }

        /* synthetic */ b(a aVar) {
            this();
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.powercenter.deepsave.BatterySaveIdeaActivity] */
    /* access modifiers changed from: private */
    public void a(String str) {
        Intent intent = new Intent("miui.intent.action.POWER_CENTER_WEBVIEW");
        intent.putExtra(MijiaAlertModel.KEY_URL, str);
        x.c((Context) this, intent);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        BatterySaveIdeaActivity.super.onCreate(bundle);
        setContentView(R.layout.pc_activity_battery_save_idea);
        this.f6992b = (ListView) findViewById(R.id.list);
        this.f6992b.setOnItemClickListener(this.f6991a);
        this.f6992b.setAdapter(this.f6993c);
        Iterator it = getIntent().getParcelableArrayListExtra("idea_list").iterator();
        while (it.hasNext()) {
            this.f6994d.add((IdeaModel) it.next());
        }
    }
}
