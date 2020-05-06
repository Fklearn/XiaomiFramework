package com.miui.applicationlock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.r;
import b.b.c.j.v;
import com.miui.applicationlock.c.C0257a;
import com.miui.applicationlock.c.F;
import com.miui.applicationlock.c.o;
import com.miui.gamebooster.view.QRSlidingButton;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;
import miui.security.SecurityManager;

public class E extends RecyclerView.a<b> {

    /* renamed from: a  reason: collision with root package name */
    private Context f3159a;

    /* renamed from: b  reason: collision with root package name */
    private List<C0257a> f3160b = new ArrayList();

    /* renamed from: c  reason: collision with root package name */
    private final boolean f3161c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public boolean f3162d;
    private final Handler e;
    private NotificationManager f;
    private SecurityManager g;
    private boolean h = false;
    /* access modifiers changed from: private */
    public a i;

    public interface a {
        void a(int i, C0257a aVar);
    }

    public static class b extends RecyclerView.u {

        /* renamed from: a  reason: collision with root package name */
        ImageView f3163a;

        /* renamed from: b  reason: collision with root package name */
        TextView f3164b;

        /* renamed from: c  reason: collision with root package name */
        TextView f3165c;

        /* renamed from: d  reason: collision with root package name */
        TextView f3166d;
        QRSlidingButton e;

        public b(View view) {
            super(view);
            this.f3163a = (ImageView) view.findViewById(R.id.app_image_lock);
            this.f3164b = (TextView) view.findViewById(R.id.app_name_lock);
            this.f3165c = (TextView) view.findViewById(R.id.app_type_lock);
            this.f3166d = (TextView) view.findViewById(R.id.app_suggest);
            this.e = (QRSlidingButton) view.findViewById(R.id.switch1);
        }
    }

    public E(Context context, boolean z, Handler handler) {
        this.f3159a = context;
        this.f3161c = z;
        this.e = handler;
        this.f = (NotificationManager) context.getSystemService("notification");
        this.g = (SecurityManager) context.getSystemService("security");
    }

    private void a(int i2, View view) {
        Drawable background = view.getBackground();
        if (!this.f3161c || this.f3162d || i2 != 0) {
            background.clearColorFilter();
            return;
        }
        background.setColorFilter(this.f3159a.getResources().getColor(R.color.btn_lock_item_selected), PorterDuff.Mode.MULTIPLY);
        this.e.postDelayed(new D(this, background), 1000);
    }

    private void a(Context context, int i2, int i3, Intent intent, int i4, int i5, Bitmap bitmap) {
        PendingIntent activity = PendingIntent.getActivity(context, i5, intent, 0);
        Notification.Action action = new Notification.Action(0, context.getResources().getString(R.string.applock_go_to), activity);
        v.a(this.f, "com.miui.securitycenter", context.getResources().getString(R.string.notify_channel_name_security), 5);
        Notification.Builder addAction = v.a(context, "com.miui.securitycenter").setWhen(System.currentTimeMillis()).setContentTitle(context.getResources().getString(i2)).setContentText(context.getResources().getString(i3)).setLargeIcon(bitmap).setSmallIcon(R.drawable.applock_small_icon).setContentIntent(activity).setPriority(2).setSound(Uri.EMPTY, (AudioAttributes) null).addAction(action);
        Bundle bundle = new Bundle();
        bundle.putBoolean("miui.showAction", !Build.IS_INTERNATIONAL_BUILD);
        addAction.setExtras(bundle);
        Notification build = addAction.build();
        build.flags |= 16;
        v.b(build, true);
        v.a(build, true);
        v.a(build, 0);
        this.f.notify(i4, build);
    }

    public void a(int i2, boolean z) {
        this.h = z;
        for (int i3 = 0; i3 < i2; i3++) {
            C0257a aVar = this.f3160b.get(i3);
            aVar.a(z);
            this.g.setApplicationAccessControlEnabledForUser(aVar.e(), z, aVar.d());
        }
        notifyItemRangeChanged(0, i2);
    }

    public void a(a aVar) {
        this.i = aVar;
    }

    /* renamed from: a */
    public void onBindViewHolder(b bVar, int i2) {
        int i3;
        Resources resources;
        TextView textView;
        String str;
        String str2;
        int i4;
        Resources resources2;
        TextView textView2;
        String str3;
        a(i2, bVar.itemView);
        C0257a aVar = this.f3160b.get(i2);
        bVar.f3164b.setText(aVar.a());
        if (aVar.b().intValue() > 0) {
            textView = bVar.f3165c;
            resources = this.f3159a.getResources();
            i3 = R.string.system_application;
        } else {
            textView = bVar.f3165c;
            resources = this.f3159a.getResources();
            i3 = R.string.third_application;
        }
        textView.setText(resources.getString(i3));
        bVar.e.setTag(aVar);
        if (aVar.d() == 999) {
            str2 = aVar.e();
            str = "pkg_icon_xspace://";
        } else {
            str2 = aVar.e();
            str = "pkg_icon://";
        }
        r.a(str.concat(str2), bVar.f3163a, r.f);
        boolean f2 = aVar.f();
        boolean g2 = aVar.g();
        bVar.e.setChecked(f2);
        bVar.f3166d.setVisibility(((!f2 || !g2) && !aVar.c()) ? 8 : 0);
        if (!f2 || !g2) {
            str3 = this.f3159a.getResources().getString(R.string.suggest_app_tolock);
            bVar.f3166d.setBackgroundResource(R.drawable.applock_suggest_border_shape);
            textView2 = bVar.f3166d;
            resources2 = this.f3159a.getResources();
            i4 = R.color.applock_suggest_text_color;
        } else {
            str3 = this.f3159a.getResources().getString(R.string.suggest_app_tolock_masked);
            bVar.f3166d.setBackgroundResource(R.drawable.applock_dismiss_border_shape);
            textView2 = bVar.f3166d;
            resources2 = this.f3159a.getResources();
            i4 = R.color.applock_dismiss_text_color;
        }
        textView2.setTextColor(resources2.getColor(i4));
        bVar.f3166d.setText(str3);
        bVar.itemView.setOnClickListener(new C(this, i2, aVar));
    }

    /* access modifiers changed from: package-private */
    public void a(C0257a aVar, boolean z) {
        aVar.a(z);
        this.g.setApplicationAccessControlEnabledForUser(aVar.e(), z, aVar.d());
        if (z) {
            o.a(this.g, aVar.e(), aVar.d());
        }
        notifyDataSetChanged();
    }

    public void a(List<F> list, boolean z) {
        this.f3160b.clear();
        for (int i2 = 0; i2 < list.size(); i2++) {
            this.f3160b.addAll(list.get(i2).a());
        }
        notifyDataSetChanged();
    }

    /* access modifiers changed from: package-private */
    public void a(boolean z, C0257a aVar, SecurityManager securityManager) {
        if (z && MaskNotificationActivity.f3191a.contains(aVar.e()) && !securityManager.getApplicationMaskNotificationEnabledAsUser(aVar.e(), aVar.d())) {
            Intent intent = new Intent(this.f3159a, MaskNotificationActivity.class);
            intent.putExtra("enter_way", "mask_notification_app_choose");
            Context context = this.f3159a;
            a(context, R.string.notification_masked_item, R.string.notification_masked_subtitle, intent, 101, 5, BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_card_app_lock));
        }
    }

    public boolean b() {
        return this.h;
    }

    public int getItemCount() {
        List<C0257a> list = this.f3160b;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public b onCreateViewHolder(ViewGroup viewGroup, int i2) {
        return new b(LayoutInflater.from(this.f3159a).inflate(R.layout.adapter_list_apps_unlock, viewGroup, false));
    }
}
