package com.miui.appmanager.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import b.b.c.j.e;
import com.miui.securitycenter.R;
import java.util.List;
import miui.widget.DropDownPopupWindow;

public class d implements DropDownPopupWindow.Controller {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public Context f3722a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public List<a> f3723b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f3724c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public b f3725d;
    private View e;
    private DropDownPopupWindow f;
    /* access modifiers changed from: private */
    public int g = e.b();

    class a extends ArrayAdapter<a> {

        /* renamed from: a  reason: collision with root package name */
        List<a> f3726a;

        a(Context context, List<a> list) {
            super(context, 0, list);
            this.f3726a = list;
        }

        public a getItem(int i) {
            List<a> list = this.f3726a;
            if (list != null) {
                return list.get(i);
            }
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            c cVar;
            ImageView imageView;
            int i2;
            Resources resources;
            if (view == null) {
                view = LayoutInflater.from(d.this.f3722a).inflate(R.layout.app_manager_drop_down_item, (ViewGroup) null);
                cVar = new c();
                cVar.f3728a = (ImageView) view.findViewById(R.id.am_drop_arrow);
                cVar.f3729b = (ImageView) view.findViewById(R.id.am_drop_icon);
                cVar.f3730c = (TextView) view.findViewById(R.id.am_drop_text);
                view.setTag(cVar);
            } else {
                cVar = (c) view.getTag();
            }
            if (d.this.g <= 8) {
                view.setBackgroundResource(R.drawable.list_item_bg_dropdown_popup_light);
            } else if (i == 0) {
                view.setBackgroundResource(R.drawable.am_drop_choice_item_bg_top);
                view.setPaddingRelative(0, d.this.f3722a.getResources().getDimensionPixelSize(R.dimen.am_drop_tb_item_height), 0, 0);
            } else if (i == d.this.f3723b.size() - 1) {
                view.setBackgroundResource(R.drawable.am_drop_choice_item_bg_bottom);
                view.setPaddingRelative(0, 0, 0, d.this.f3722a.getResources().getDimensionPixelSize(R.dimen.am_drop_tb_item_height));
            } else {
                view.setBackgroundResource(R.drawable.am_drop_choice_item_bg);
                view.setPaddingRelative(0, 0, 0, 0);
            }
            a item = getItem(i);
            if (item != null) {
                if (d.this.f3724c == i) {
                    cVar.f3728a.setVisibility(0);
                    cVar.f3730c.setTextColor(d.this.f3722a.getResources().getColor(R.color.app_manager_drop_pop_text_selected));
                    imageView = cVar.f3729b;
                    if (imageView != null) {
                        resources = d.this.f3722a.getResources();
                        i2 = item.f3718b;
                    }
                    cVar.f3730c.setText(item.f3719c);
                } else {
                    cVar.f3728a.setVisibility(4);
                    cVar.f3730c.setTextColor(d.this.f3722a.getResources().getColor(R.color.app_manager_list_title_color));
                    imageView = cVar.f3729b;
                    if (imageView != null) {
                        resources = d.this.f3722a.getResources();
                        i2 = item.f3717a;
                    }
                    cVar.f3730c.setText(item.f3719c);
                }
                imageView.setImageDrawable(resources.getDrawable(i2));
                cVar.f3730c.setText(item.f3719c);
            }
            return view;
        }
    }

    public interface b {
        void a(d dVar, int i);

        void onDismiss();

        void onShow();
    }

    class c {

        /* renamed from: a  reason: collision with root package name */
        ImageView f3728a;

        /* renamed from: b  reason: collision with root package name */
        ImageView f3729b;

        /* renamed from: c  reason: collision with root package name */
        TextView f3730c;

        c() {
        }
    }

    public d(Context context) {
        this.f3722a = context;
    }

    /* access modifiers changed from: private */
    public void c() {
        this.f = null;
    }

    public void a() {
        DropDownPopupWindow dropDownPopupWindow = this.f;
        if (dropDownPopupWindow != null) {
            dropDownPopupWindow.dismiss();
        }
    }

    public void a(int i) {
        this.f3724c = i;
    }

    public void a(View view) {
        this.e = view;
    }

    public void a(b bVar) {
        this.f3725d = bVar;
    }

    public void a(List<a> list) {
        this.f3723b = list;
    }

    public void b() {
        if (this.f3723b != null && this.e != null) {
            DropDownPopupWindow dropDownPopupWindow = this.f;
            if (dropDownPopupWindow == null) {
                this.f = new DropDownPopupWindow(this.f3722a, (AttributeSet) null, 0);
                this.f.setContainerController(new b(this));
                this.f.setDropDownController(this);
                ListView listView = new DropDownPopupWindow.ListController(this.f).getListView();
                listView.setAdapter(new a(this.f3722a, this.f3723b));
                listView.setOnItemClickListener(new c(this));
                listView.setChoiceMode(1);
                listView.setItemChecked(this.f3724c, true);
                this.f.setAnchor(this.e);
                dropDownPopupWindow = this.f;
            }
            dropDownPopupWindow.show();
        }
    }

    public void onAniamtionUpdate(View view, float f2) {
    }

    public void onDismiss() {
        b bVar = this.f3725d;
        if (bVar != null) {
            bVar.onDismiss();
        }
    }

    public void onShow() {
    }
}
