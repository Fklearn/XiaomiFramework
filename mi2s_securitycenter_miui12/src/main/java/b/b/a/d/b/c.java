package b.b.a.d.b;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import b.b.a.d.a.l;
import b.b.a.e.i;
import com.miui.antispam.ui.activity.MainActivity;
import com.miui.antispam.ui.activity.r;
import com.miui.antispam.ui.view.RecyclerViewExt;
import com.miui.maml.component.MamlView;
import com.miui.permission.PermissionContract;
import java.util.ArrayList;
import java.util.List;
import miui.R;
import miui.app.Activity;
import miui.view.EditActionMode;
import miui.widget.DropDownSingleChoiceMenu;
import miuix.nestedheader.widget.NestedHeaderLayout;

public abstract class c extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, i.a, View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    protected RecyclerViewExt f1378a;

    /* renamed from: b  reason: collision with root package name */
    protected NestedHeaderLayout f1379b;

    /* renamed from: c  reason: collision with root package name */
    protected MainActivity f1380c;

    /* renamed from: d  reason: collision with root package name */
    protected l f1381d;
    protected RelativeLayout e;
    protected TextView f;
    protected TextView g;
    protected MamlView h;
    protected LinearLayout i;
    protected LinearLayout j;
    protected a k;
    protected ContentResolver l;
    protected String m = b.b.a.e.c.f;
    protected AlertDialog n;
    private boolean o = false;
    protected ArrayList<Integer> p = new ArrayList<>();
    /* access modifiers changed from: private */
    public List<String> q = new ArrayList();
    public int r = -1;
    /* access modifiers changed from: private */
    public int s = 0;

    public class a implements RecyclerViewExt.d {
        public a() {
        }

        /* JADX WARNING: type inference failed for: r3v2, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity] */
        public void a(ActionMode actionMode, int i, boolean z) {
            int i2;
            EditActionMode editActionMode;
            c cVar = c.this;
            cVar.f1381d.a((Context) cVar.f1380c, actionMode);
            if (!r.f2610a) {
                if (c.this.f1381d.h()) {
                    editActionMode = (EditActionMode) actionMode;
                    i2 = c.this.f1380c.b() ? R.drawable.action_mode_title_button_deselect_all_dark : R.drawable.action_mode_title_button_deselect_all_light;
                } else {
                    editActionMode = (EditActionMode) actionMode;
                    i2 = c.this.f1380c.b() ? R.drawable.action_mode_title_button_select_all_dark : R.drawable.action_mode_title_button_select_all_light;
                }
                editActionMode.setButton(16908314, (CharSequence) null, i2);
            }
        }

        /* JADX WARNING: type inference failed for: r5v5, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity] */
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            boolean z = false;
            switch (menuItem.getItemId()) {
                case 16908313:
                    actionMode.finish();
                    break;
                case 16908314:
                    l lVar = c.this.f1381d;
                    if (!lVar.h()) {
                        z = true;
                    }
                    lVar.a(z);
                    c cVar = c.this;
                    cVar.f1381d.a((Context) cVar.f1380c, actionMode);
                    if (!r.f2610a) {
                        ((EditActionMode) actionMode).setButton(16908314, (CharSequence) null, !c.this.f1381d.h() ? c.this.f1380c.b() ? R.drawable.action_mode_title_button_select_all_dark : R.drawable.action_mode_title_button_select_all_light : c.this.f1380c.b() ? R.drawable.action_mode_title_button_deselect_all_dark : R.drawable.action_mode_title_button_deselect_all_light);
                        break;
                    }
                    break;
                case com.miui.securitycenter.R.id.edit_mode_delete /*2131296738*/:
                    c.this.a(actionMode, false);
                    break;
            }
            return true;
        }

        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            c.this.f1381d.b();
            c.this.g.setEnabled(false);
            c.this.f1380c.getMenuInflater().inflate(com.miui.securitycenter.R.menu.list_view_edit_mode_menu, menu);
            menu.findItem(com.miui.securitycenter.R.id.edit_mode_white).setVisible(false);
            if (r.f2610a) {
                return true;
            }
            EditActionMode editActionMode = (EditActionMode) actionMode;
            editActionMode.setButton(16908313, (CharSequence) null, c.this.f1380c.b() ? R.drawable.action_mode_title_button_cancel_dark : R.drawable.action_mode_title_button_cancel_light);
            editActionMode.setButton(16908314, (CharSequence) null, c.this.f1380c.b() ? R.drawable.action_mode_title_button_select_all_dark : R.drawable.action_mode_title_button_select_all_light);
            return true;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
            c.this.f1381d.c();
            c.this.g.setEnabled(true);
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }
    }

    private int a(int i2) {
        if (this instanceof y) {
            if (i2 == -1) {
                return com.miui.securitycenter.R.string.tab_sort_all;
            }
            if (i2 == 16) {
                return com.miui.securitycenter.R.string.sms_cloud_block;
            }
            if (i2 == 12) {
                return com.miui.securitycenter.R.string.sms_keywords;
            }
            if (i2 == 13) {
                return com.miui.securitycenter.R.string.sms_address;
            }
            switch (i2) {
                case 3:
                case 5:
                    return com.miui.securitycenter.R.string.sms_blacklist;
                case 4:
                    return com.miui.securitycenter.R.string.sms_filter;
                case 6:
                    return com.miui.securitycenter.R.string.sms_prefix;
                case 7:
                    return com.miui.securitycenter.R.string.sms_stranger_block;
                case 8:
                    return com.miui.securitycenter.R.string.sms_malicious_url;
                case 9:
                    return com.miui.securitycenter.R.string.sms_contact_block;
                case 10:
                    return com.miui.securitycenter.R.string.sms_service;
                default:
                    return com.miui.securitycenter.R.string.sms_filter;
            }
        } else if (i2 == -1) {
            return com.miui.securitycenter.R.string.tab_sort_all;
        } else {
            if (i2 == 4) {
                return com.miui.securitycenter.R.string.call_private;
            }
            switch (i2) {
                case 6:
                    return com.miui.securitycenter.R.string.call_prefix;
                case 7:
                    return com.miui.securitycenter.R.string.call_stranger_block;
                case 8:
                    return com.miui.securitycenter.R.string.mark_fraud_block_large;
                case 9:
                    return com.miui.securitycenter.R.string.call_contact_block;
                case 10:
                    return com.miui.securitycenter.R.string.mark_agent_block_large;
                default:
                    switch (i2) {
                        case 12:
                            return com.miui.securitycenter.R.string.mark_sell_block_large;
                        case 13:
                            return com.miui.securitycenter.R.string.call_address;
                        case 14:
                            return com.miui.securitycenter.R.string.mark_harass_block_large;
                        case 15:
                            return com.miui.securitycenter.R.string.call_transfer_block;
                        case 16:
                            return com.miui.securitycenter.R.string.call_cloud_block;
                        case 17:
                            return com.miui.securitycenter.R.string.call_oversea_block;
                        default:
                            return com.miui.securitycenter.R.string.call_blacklist;
                    }
            }
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity] */
    /* access modifiers changed from: private */
    public void a(View view) {
        DropDownSingleChoiceMenu dropDownSingleChoiceMenu = new DropDownSingleChoiceMenu(this.f1380c);
        dropDownSingleChoiceMenu.setItems(this.q);
        dropDownSingleChoiceMenu.setSelectedItem(this.s);
        dropDownSingleChoiceMenu.setAnchorView(view);
        dropDownSingleChoiceMenu.setOnMenuListener(new b(this));
        dropDownSingleChoiceMenu.show();
    }

    public abstract l a(Context context);

    public void a() {
        l lVar = this.f1381d;
        if (lVar != null) {
            lVar.notifyDataSetChanged();
        }
    }

    public void a(Cursor cursor) {
        this.q.clear();
        this.p.clear();
        this.p.add(-1);
        this.q.add(getString(a(-1)));
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            int i2 = cursor.getInt(cursor.getColumnIndex(c()));
            if (!this.p.contains(Integer.valueOf(i2))) {
                this.p.add(Integer.valueOf(i2));
                this.q.add(getString(a(i2)));
                if (this.r == i2) {
                    this.s = this.q.size() - 1;
                }
            }
        }
        if (!this.p.contains(Integer.valueOf(this.r))) {
            this.r = -1;
            this.s = 0;
        }
    }

    public abstract void a(ActionMode actionMode, boolean z);

    public abstract String b();

    public abstract String c();

    public void d() {
        this.f1378a.setVisibility(8);
        this.j.setVisibility(0);
        if (b.b.a.e.c.f.equals(this.m)) {
            this.i.setVisibility(8);
        }
    }

    public abstract void e();

    public void onClick(View view) {
        this.h.sendCommand(this.o ? "deactive" : PermissionContract.Active.TABLE_NAME);
        this.o = !this.o;
    }

    /* JADX WARNING: type inference failed for: r1v5, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity] */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        this.f1380c = (MainActivity) getActivity();
        this.l = this.f1380c.getContentResolver();
        i.a((Context) this.f1380c).a((i.a) this);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity] */
    /* JADX WARNING: type inference failed for: r4v27, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity] */
    /* JADX WARNING: type inference failed for: r5v6, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity] */
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Intent intent = this.f1380c.getIntent();
        if (intent != null && intent.hasExtra(b.b.a.e.c.f1415c)) {
            this.m = intent.getStringExtra(b.b.a.e.c.f1415c);
        }
        this.k = new a();
        View inflate = layoutInflater.inflate(com.miui.securitycenter.R.layout.fw_withouttab_fragment, (ViewGroup) null);
        this.i = (LinearLayout) inflate.findViewById(com.miui.securitycenter.R.id.header);
        this.f1379b = (NestedHeaderLayout) inflate.findViewById(com.miui.securitycenter.R.id.nested_header);
        this.e = (RelativeLayout) inflate.findViewById(16908292);
        this.f = (TextView) inflate.findViewById(com.miui.securitycenter.R.id.emptyText);
        this.g = (TextView) inflate.findViewById(com.miui.securitycenter.R.id.spinner);
        this.j = (LinearLayout) inflate.findViewById(com.miui.securitycenter.R.id.loading_progress);
        this.f1378a = (RecyclerViewExt) inflate.findViewById(16908298);
        this.f1378a.setLayoutManager(new LinearLayoutManager(this.f1380c));
        this.f1381d = a((Context) this.f1380c);
        this.f1378a.setAdapter(this.f1381d);
        this.f1381d.a((Activity) this.f1380c, (RecyclerViewExt.d) this.k);
        this.g.setOnClickListener(new a(this));
        setHasOptionsMenu(true);
        this.h = new MamlView((Context) this.f1380c, b(), 2);
        int dimensionPixelSize = getResources().getDimensionPixelSize(com.miui.securitycenter.R.dimen.view_dimen_480);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dimensionPixelSize, dimensionPixelSize);
        layoutParams.addRule(14);
        layoutParams.addRule(8, this.f.getId());
        this.e.addView(this.h, layoutParams);
        this.e.setOnClickListener(this);
        this.h.setOnClickListener(this);
        return inflate;
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity] */
    public void onDestroy() {
        super.onDestroy();
        i.a((Context) this.f1380c).b((i.a) this);
        MamlView mamlView = this.h;
        if (mamlView != null) {
            mamlView.onDestroy();
            this.h = null;
        }
    }

    public void onDetach() {
        super.onDetach();
        this.f1380c = null;
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        this.f1381d.setData((List<Object>) null);
    }

    public void onPause() {
        super.onPause();
        MamlView mamlView = this.h;
        if (mamlView != null) {
            mamlView.onPause();
        }
    }

    public void onResume() {
        super.onResume();
        MamlView mamlView = this.h;
        if (mamlView != null) {
            mamlView.onResume();
        }
    }
}
