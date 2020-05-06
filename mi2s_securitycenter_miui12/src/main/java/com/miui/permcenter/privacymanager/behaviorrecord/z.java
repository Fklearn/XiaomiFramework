package com.miui.permcenter.privacymanager.behaviorrecord;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.permcenter.b.c;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class z extends RecyclerView.a<a> implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private Context f6470a;

    /* renamed from: b  reason: collision with root package name */
    private Resources f6471b;

    /* renamed from: c  reason: collision with root package name */
    private String f6472c;

    /* renamed from: d  reason: collision with root package name */
    private List<Long> f6473d = new ArrayList();
    private c e;

    public static class a extends RecyclerView.u {

        /* renamed from: a  reason: collision with root package name */
        ImageView f6474a;

        /* renamed from: b  reason: collision with root package name */
        TextView f6475b;

        /* renamed from: c  reason: collision with root package name */
        TextView f6476c;

        public a(@NonNull View view) {
            super(view);
            this.f6474a = (ImageView) view.findViewById(R.id.app_behavior_icon);
            this.f6475b = (TextView) view.findViewById(R.id.app_behavior_perm_name);
            this.f6476c = (TextView) view.findViewById(R.id.app_behavior_event_name);
        }
    }

    public z(Context context, String str, long j) {
        this.f6470a = context;
        this.f6471b = context.getResources();
        this.f6472c = str;
        b(j);
    }

    private void b(long j) {
        if ((j & 32) != 0) {
            this.f6473d.add(32L);
        }
        if ((j & PermissionManager.PERM_ID_AUDIO_RECORDER) != 0) {
            this.f6473d.add(Long.valueOf(PermissionManager.PERM_ID_AUDIO_RECORDER));
        }
    }

    public void a(long j) {
        this.f6473d.clear();
        b(j);
        notifyDataSetChanged();
    }

    public void a(c cVar) {
        this.e = cVar;
    }

    /* renamed from: a */
    public void onBindViewHolder(@NonNull a aVar, int i) {
        String str;
        View view;
        long valueOf;
        GradientDrawable gradientDrawable = (GradientDrawable) aVar.itemView.getBackground();
        int i2 = (this.f6473d.get(i).longValue() > 32 ? 1 : (this.f6473d.get(i).longValue() == 32 ? 0 : -1));
        int i3 = R.drawable.icon_app_behavior_audio_background;
        if (i2 == 0) {
            i3 = R.drawable.icon_app_behavior_location_background;
            str = this.f6471b.getString(R.string.app_behavior_warning_location_title, new Object[]{this.f6472c});
            gradientDrawable.setColor(this.f6471b.getColor(R.color.app_behavior_privacy_location_using));
            view = aVar.itemView;
            valueOf = 32L;
        } else if (this.f6473d.get(i).longValue() == PermissionManager.PERM_ID_AUDIO_RECORDER) {
            str = this.f6471b.getString(R.string.app_behavior_warning_audio_title, new Object[]{this.f6472c});
            gradientDrawable.setColor(this.f6471b.getColor(R.color.app_behavior_privacy_audio_using));
            view = aVar.itemView;
            valueOf = Long.valueOf(PermissionManager.PERM_ID_AUDIO_RECORDER);
        } else {
            str = "";
            aVar.f6474a.setImageResource(i3);
            aVar.f6475b.setText(str);
            aVar.f6476c.setText(this.f6471b.getString(R.string.app_behavior_warning_content));
            aVar.f6475b.setTag(this.f6473d.get(i));
        }
        view.setTag(valueOf);
        aVar.f6474a.setImageResource(i3);
        aVar.f6475b.setText(str);
        aVar.f6476c.setText(this.f6471b.getString(R.string.app_behavior_warning_content));
        aVar.f6475b.setTag(this.f6473d.get(i));
    }

    public int getItemCount() {
        return this.f6473d.size();
    }

    public void onClick(View view) {
        c cVar = this.e;
        if (cVar != null) {
            cVar.a(view);
        }
    }

    @NonNull
    public a onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(this.f6470a).inflate(R.layout.listitem_app_behavior_withicon, viewGroup, false);
        inflate.setOnClickListener(this);
        return new a(inflate);
    }
}
