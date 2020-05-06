package com.miui.permcenter.autostart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.securitycenter.R;
import miui.widget.SlidingButton;

public class b extends RecyclerView.a<C0057b> {

    /* renamed from: a  reason: collision with root package name */
    private LayoutInflater f6061a;

    /* renamed from: b  reason: collision with root package name */
    private boolean f6062b;

    /* renamed from: c  reason: collision with root package name */
    private int f6063c;

    /* renamed from: d  reason: collision with root package name */
    private CompoundButton.OnCheckedChangeListener f6064d;
    /* access modifiers changed from: private */
    public a e;

    public interface a {
        void a(int i, C0057b bVar);
    }

    /* renamed from: com.miui.permcenter.autostart.b$b  reason: collision with other inner class name */
    static class C0057b extends RecyclerView.u {

        /* renamed from: a  reason: collision with root package name */
        int f6065a;

        /* renamed from: b  reason: collision with root package name */
        SlidingButton f6066b;

        /* renamed from: c  reason: collision with root package name */
        TextView f6067c;

        public C0057b(@NonNull View view, CompoundButton.OnCheckedChangeListener onCheckedChangeListener, int i) {
            super(view);
            this.f6065a = i;
            this.f6067c = (TextView) view.findViewById(R.id.auto_start_type);
            this.f6066b = view.findViewById(R.id.auto_start_sliding_button);
            this.f6066b.setOnCheckedChangeListener(onCheckedChangeListener);
        }
    }

    public b(Context context) {
        this.f6061a = LayoutInflater.from(context);
    }

    public void a(int i, boolean z) {
        this.f6063c = i;
        this.f6062b = z;
        notifyDataSetChanged();
    }

    public void a(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.f6064d = onCheckedChangeListener;
    }

    public void a(a aVar) {
        this.e = aVar;
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x002f  */
    /* renamed from: a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBindViewHolder(@androidx.annotation.NonNull com.miui.permcenter.autostart.b.C0057b r5, int r6) {
        /*
            r4 = this;
            int r0 = r5.f6065a
            r1 = 1
            if (r0 == r1) goto L_0x0019
            r1 = 2
            if (r0 == r1) goto L_0x0009
            goto L_0x002b
        L_0x0009:
            android.widget.TextView r0 = r5.f6067c
            r1 = 2131757246(0x7f1008be, float:1.9145422E38)
            r0.setText(r1)
            miui.widget.SlidingButton r0 = r5.f6066b
            boolean r1 = r4.f6062b
        L_0x0015:
            r0.setChecked(r1)
            goto L_0x002b
        L_0x0019:
            android.widget.TextView r0 = r5.f6067c
            r2 = 2131757248(0x7f1008c0, float:1.9145426E38)
            r0.setText(r2)
            miui.widget.SlidingButton r0 = r5.f6066b
            int r2 = r4.f6063c
            r3 = 3
            if (r2 != r3) goto L_0x0029
            goto L_0x0015
        L_0x0029:
            r1 = 0
            goto L_0x0015
        L_0x002b:
            com.miui.permcenter.autostart.b$a r0 = r4.e
            if (r0 == 0) goto L_0x0039
            android.view.View r0 = r5.itemView
            com.miui.permcenter.autostart.a r1 = new com.miui.permcenter.autostart.a
            r1.<init>(r4, r6, r5)
            r0.setOnClickListener(r1)
        L_0x0039:
            miui.widget.SlidingButton r6 = r5.f6066b
            r6.setTag(r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.permcenter.autostart.b.onBindViewHolder(com.miui.permcenter.autostart.b$b, int):void");
    }

    public int getItemCount() {
        return 2;
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getItemViewType(int i) {
        if (i != 0) {
            return i != 1 ? 0 : 2;
        }
        return 1;
    }

    @NonNull
    public C0057b onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new C0057b(this.f6061a.inflate(R.layout.pm_auto_start_detail_view, viewGroup, false), this.f6064d, i);
    }
}
