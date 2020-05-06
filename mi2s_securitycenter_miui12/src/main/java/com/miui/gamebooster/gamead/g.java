package com.miui.gamebooster.gamead;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ArrayAdapter;
import b.b.b.a.c;
import com.miui.gamebooster.viewPointwidget.b;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class g extends ArrayAdapter<e> implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    protected static final HashMap<Integer, Integer> f4302a = new HashMap<>();

    /* renamed from: b  reason: collision with root package name */
    private Resources f4303b;

    /* renamed from: c  reason: collision with root package name */
    private ArrayList<b> f4304c = new ArrayList<>();

    /* renamed from: d  reason: collision with root package name */
    public b.b.c.i.b f4305d;

    static {
        int i;
        HashMap<Integer, Integer> hashMap;
        f4302a.put(Integer.valueOf(R.layout.game_ad_big_image), 0);
        f4302a.put(Integer.valueOf(R.layout.wid_view_point_comment_item), 1);
        f4302a.put(Integer.valueOf(R.layout.wid_view_point_pic_item), 2);
        if (Build.VERSION.SDK_INT >= 26) {
            hashMap = f4302a;
            i = R.layout.wid_view_point_list_video_item_v26;
        } else {
            hashMap = f4302a;
            i = R.layout.wid_view_point_list_video_item;
        }
        hashMap.put(Integer.valueOf(i), 3);
        f4302a.put(Integer.valueOf(R.layout.wid_view_point_muti_buttom_item), 4);
        f4302a.put(Integer.valueOf(R.layout.wid_buttom), 5);
        f4302a.put(Integer.valueOf(R.layout.gbg_card_horizontal_list), 6);
        f4302a.put(Integer.valueOf(R.layout.gbg_card_game_list_vertical), 7);
        f4302a.put(Integer.valueOf(R.layout.gbg_card_post), 8);
        f4302a.put(Integer.valueOf(R.layout.gbg_card_post_small), 9);
        f4302a.put(Integer.valueOf(R.layout.gbg_pure_image), 10);
        f4302a.put(Integer.valueOf(R.layout.gbg_pure_title), 11);
        f4302a.put(Integer.valueOf(R.layout.gbg_user_guide), 12);
        f4302a.put(Integer.valueOf(R.layout.gbg_card_h5_1row_list), 13);
        f4302a.put(Integer.valueOf(R.layout.gbg_card_h5_2row_list), 14);
        f4302a.put(Integer.valueOf(R.layout.gbg_card_h5_torrent_list), 15);
    }

    public g(Context context, List<e> list, b.b.c.i.b bVar) {
        super(context, 0, list);
        this.f4303b = context.getResources();
        this.f4305d = bVar;
    }

    public Drawable b() {
        return new c(this.f4303b.getDrawable(R.drawable.big_backgroud_def));
    }

    public int getItemViewType(int i) {
        return f4302a.get(Integer.valueOf(((e) getItem(i)).getLayoutId())).intValue();
    }

    public int getViewTypeCount() {
        return f4302a.size();
    }

    public boolean isEnabled(int i) {
        return true;
    }

    public void onClick(View view) {
        ((View.OnClickListener) view.getTag()).onClick(view);
    }
}
