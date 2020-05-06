package com.miui.powercenter.deepsave.a;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.view.View;
import android.widget.TextView;
import b.b.c.d.C0185e;
import b.b.c.d.C0191k;
import b.b.c.j.l;
import com.miui.securitycenter.R;

public class d extends C0185e {

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        TextView f7006a;

        /* renamed from: b  reason: collision with root package name */
        TextView f7007b;

        /* renamed from: c  reason: collision with root package name */
        TextView f7008c;

        /* renamed from: d  reason: collision with root package name */
        TextView f7009d;
        TextView e;
        TextView f;
        TextView g;
        TextView h;

        private a() {
        }
    }

    private int a(Context context) {
        return context.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED")).getIntExtra("health", 0);
    }

    private void a(Context context, a aVar, boolean z) {
        aVar.f7006a.setTextColor(context.getResources().getColor(R.color.pc_textview_color));
        aVar.e.setTextColor(context.getResources().getColor(R.color.pc_main_power_card_summary_text_color));
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0074  */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0076  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(com.miui.powercenter.deepsave.a.d.a r8, android.content.Context r9) {
        /*
            r7 = this;
            int r0 = r7.a(r9)
            r1 = 2
            r2 = 1
            r3 = 0
            if (r0 == r1) goto L_0x0024
            r1 = 3
            if (r0 == r1) goto L_0x0018
            android.widget.TextView r0 = r8.e
            r1 = 2131755656(0x7f100288, float:1.9142197E38)
        L_0x0011:
            r0.setText(r1)
            r7.a(r9, r8, r3)
            goto L_0x002a
        L_0x0018:
            android.widget.TextView r0 = r8.e
            r1 = 2131755655(0x7f100287, float:1.9142195E38)
            r0.setText(r1)
            r7.a(r9, r8, r2)
            goto L_0x002a
        L_0x0024:
            android.widget.TextView r0 = r8.e
            r1 = 2131755654(0x7f100286, float:1.9142193E38)
            goto L_0x0011
        L_0x002a:
            float r0 = r7.c(r9)
            r1 = 2131757276(0x7f1008dc, float:1.9145483E38)
            java.lang.Object[] r4 = new java.lang.Object[r2]
            java.util.Locale r5 = java.util.Locale.getDefault()
            java.lang.Object[] r6 = new java.lang.Object[r2]
            java.lang.Float r0 = java.lang.Float.valueOf(r0)
            r6[r3] = r0
            java.lang.String r0 = "%.1f"
            java.lang.String r0 = java.lang.String.format(r5, r0, r6)
            r4[r3] = r0
            java.lang.String r0 = r9.getString(r1, r4)
            android.widget.TextView r1 = r8.f
            r1.setText(r0)
            r0 = 2131757275(0x7f1008db, float:1.9145481E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            int r4 = r7.b(r9)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r1[r3] = r4
            java.lang.String r0 = r9.getString(r0, r1)
            android.widget.TextView r1 = r8.g
            r1.setText(r0)
            int r0 = com.miui.powercenter.utils.o.c(r9)
            int r1 = com.miui.powercenter.utils.o.e(r9)
            r4 = 100
            if (r1 != r4) goto L_0x0076
            r1 = r0
            goto L_0x007d
        L_0x0076:
            float r4 = (float) r0
            r5 = 1120403456(0x42c80000, float:100.0)
            float r4 = r4 / r5
            float r1 = (float) r1
            float r4 = r4 * r1
            int r1 = (int) r4
        L_0x007d:
            android.widget.TextView r8 = r8.h
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.Object[] r5 = new java.lang.Object[r2]
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r5[r3] = r1
            r1 = 2131757277(0x7f1008dd, float:1.9145485E38)
            java.lang.String r5 = r9.getString(r1, r5)
            r4.append(r5)
            java.lang.String r5 = " / "
            r4.append(r5)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r2[r3] = r0
            java.lang.String r9 = r9.getString(r1, r2)
            r4.append(r9)
            java.lang.String r9 = r4.toString()
            r8.setText(r9)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.deepsave.a.d.a(com.miui.powercenter.deepsave.a.d$a, android.content.Context):void");
    }

    private int b(Context context) {
        return context.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED")).getIntExtra("temperature", 0) / 10;
    }

    private float c(Context context) {
        return ((float) context.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED")).getIntExtra("voltage", 0)) / 1000.0f;
    }

    public int a() {
        return R.layout.pc_list_item_battery_health;
    }

    public void a(int i, View view, Context context, C0191k kVar) {
        a aVar;
        super.a(i, view, context, kVar);
        if (view.getTag() == null) {
            aVar = new a();
            aVar.f7006a = (TextView) view.findViewById(R.id.battery_status);
            aVar.f7007b = (TextView) view.findViewById(R.id.battery_pressure);
            aVar.f7008c = (TextView) view.findViewById(R.id.battery_temp);
            aVar.f7009d = (TextView) view.findViewById(R.id.battery_left);
            aVar.e = (TextView) view.findViewById(R.id.status_value);
            aVar.f = (TextView) view.findViewById(R.id.voltage_value);
            aVar.g = (TextView) view.findViewById(R.id.temperature_value);
            aVar.h = (TextView) view.findViewById(R.id.capacity_value);
            view.setTag(aVar);
        } else {
            aVar = (a) view.getTag();
        }
        a(aVar, context);
        l.a(view);
    }
}
