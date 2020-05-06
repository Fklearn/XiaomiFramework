package com.miui.gamebooster.n.d;

import android.content.Intent;
import android.view.View;
import com.miui.gamebooster.n.c.b;
import com.miui.gamebooster.n.d.b;
import com.miui.gamebooster.videobox.adapter.h;
import com.miui.securitycenter.R;

public class l extends j {
    public l(String str, b bVar) {
        super(str, bVar);
    }

    public void a(int i, View view, b.a aVar) {
        View view2;
        if (view != null && view.getTag() != null && (view2 = ((h.a) view.getTag()).f5169a) != null) {
            view2.setOnClickListener(new k(this, aVar));
        }
    }

    public void a(View view) {
        Intent intent = new Intent("com.miui.gamebooster.action.VIDEOBOX_SETTINGS");
        intent.addFlags(67108864);
        intent.addFlags(268435456);
        view.getContext().startActivity(intent);
    }

    public int d() {
        return R.layout.video_box_list_item_settings;
    }
}
