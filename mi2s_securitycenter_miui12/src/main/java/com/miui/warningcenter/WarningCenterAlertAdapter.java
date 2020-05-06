package com.miui.warningcenter;

import android.app.Activity;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.util.ArrayList;
import java.util.List;

public class WarningCenterAlertAdapter extends BaseAdapter {
    public static final String FORMAT_TIME = "HH:mm";
    /* access modifiers changed from: private */
    public Listener listener;
    private Activity mActivity;
    private List<WarningAlertBaseModel> mDatas;

    public interface Listener {
        void onItemClick(int i, MijiaAlertModel mijiaAlertModel);
    }

    public class ViewHolder {
        TextView alertSettings;
        TextView alertSource;
        LinearLayout containerCard;
        LinearLayout containerTitle;
        TextView homeText;
        ImageView ivType;
        ImageView ivTypeTop;
        LinearLayout llTitle;
        LinearLayout llTitleTop;
        RelativeLayout roomCard;
        RelativeLayout roomContainer;
        TextView roomText;
        LinearLayout root;
        TextView timeText;
        TextView tvTitle;
        TextView tvTitleTop;
        Button viewDevice;
        TextView warningTime;

        public ViewHolder() {
        }
    }

    public WarningCenterAlertAdapter(Activity activity) {
        this.mActivity = activity;
    }

    public void addData(WarningAlertBaseModel warningAlertBaseModel) {
        if (this.mDatas == null) {
            this.mDatas = new ArrayList();
        }
        this.mDatas.add(0, warningAlertBaseModel);
        notifyDataSetChanged();
    }

    public int getCount() {
        List<WarningAlertBaseModel> list = this.mDatas;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public Object getItem(int i) {
        return null;
    }

    public long getItemId(int i) {
        return (long) i;
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x01c5  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x01eb  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x026e  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0285  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View getView(final int r11, android.view.View r12, android.view.ViewGroup r13) {
        /*
            r10 = this;
            if (r12 != 0) goto L_0x00df
            android.app.Activity r12 = r10.mActivity
            android.view.LayoutInflater r12 = android.view.LayoutInflater.from(r12)
            r13 = 2131493574(0x7f0c02c6, float:1.8610632E38)
            r0 = 0
            android.view.View r12 = r12.inflate(r13, r0)
            com.miui.warningcenter.WarningCenterAlertAdapter$ViewHolder r13 = new com.miui.warningcenter.WarningCenterAlertAdapter$ViewHolder
            r13.<init>()
            r0 = 2131297942(0x7f090696, float:1.8213843E38)
            android.view.View r0 = r12.findViewById(r0)
            android.widget.TextView r0 = (android.widget.TextView) r0
            r13.tvTitle = r0
            r0 = 2131297946(0x7f09069a, float:1.8213851E38)
            android.view.View r0 = r12.findViewById(r0)
            android.widget.TextView r0 = (android.widget.TextView) r0
            r13.tvTitleTop = r0
            r0 = 2131297152(0x7f090380, float:1.821224E38)
            android.view.View r0 = r12.findViewById(r0)
            android.widget.ImageView r0 = (android.widget.ImageView) r0
            r13.ivType = r0
            r0 = 2131297153(0x7f090381, float:1.8212243E38)
            android.view.View r0 = r12.findViewById(r0)
            android.widget.ImageView r0 = (android.widget.ImageView) r0
            r13.ivTypeTop = r0
            r0 = 2131297265(0x7f0903f1, float:1.821247E38)
            android.view.View r0 = r12.findViewById(r0)
            android.widget.LinearLayout r0 = (android.widget.LinearLayout) r0
            r13.llTitleTop = r0
            r0 = 2131297264(0x7f0903f0, float:1.8212468E38)
            android.view.View r0 = r12.findViewById(r0)
            android.widget.LinearLayout r0 = (android.widget.LinearLayout) r0
            r13.llTitle = r0
            r0 = 2131298047(0x7f0906ff, float:1.8214056E38)
            android.view.View r0 = r12.findViewById(r0)
            android.widget.TextView r0 = (android.widget.TextView) r0
            r13.warningTime = r0
            r0 = 2131296970(0x7f0902ca, float:1.8211872E38)
            android.view.View r0 = r12.findViewById(r0)
            android.widget.TextView r0 = (android.widget.TextView) r0
            r13.homeText = r0
            r0 = 2131297569(0x7f090521, float:1.8213087E38)
            android.view.View r0 = r12.findViewById(r0)
            android.widget.TextView r0 = (android.widget.TextView) r0
            r13.roomText = r0
            r0 = 2131297841(0x7f090631, float:1.8213638E38)
            android.view.View r0 = r12.findViewById(r0)
            android.widget.TextView r0 = (android.widget.TextView) r0
            r13.timeText = r0
            r0 = 2131296364(0x7f09006c, float:1.8210643E38)
            android.view.View r0 = r12.findViewById(r0)
            android.widget.TextView r0 = (android.widget.TextView) r0
            r13.alertSource = r0
            r0 = 2131296363(0x7f09006b, float:1.821064E38)
            android.view.View r0 = r12.findViewById(r0)
            android.widget.TextView r0 = (android.widget.TextView) r0
            r13.alertSettings = r0
            r0 = 2131298026(0x7f0906ea, float:1.8214014E38)
            android.view.View r0 = r12.findViewById(r0)
            android.widget.Button r0 = (android.widget.Button) r0
            r13.viewDevice = r0
            r0 = 2131296666(0x7f09019a, float:1.8211255E38)
            android.view.View r0 = r12.findViewById(r0)
            android.widget.LinearLayout r0 = (android.widget.LinearLayout) r0
            r13.containerTitle = r0
            r0 = 2131296651(0x7f09018b, float:1.8211225E38)
            android.view.View r0 = r12.findViewById(r0)
            android.widget.LinearLayout r0 = (android.widget.LinearLayout) r0
            r13.containerCard = r0
            r0 = 2131297568(0x7f090520, float:1.8213085E38)
            android.view.View r0 = r12.findViewById(r0)
            android.widget.RelativeLayout r0 = (android.widget.RelativeLayout) r0
            r13.roomContainer = r0
            r0 = 2131297567(0x7f09051f, float:1.8213083E38)
            android.view.View r0 = r12.findViewById(r0)
            android.widget.RelativeLayout r0 = (android.widget.RelativeLayout) r0
            r13.roomCard = r0
            r0 = 2131297570(0x7f090522, float:1.8213089E38)
            android.view.View r0 = r12.findViewById(r0)
            android.widget.LinearLayout r0 = (android.widget.LinearLayout) r0
            r13.root = r0
            r12.setTag(r13)
            goto L_0x00e5
        L_0x00df:
            java.lang.Object r13 = r12.getTag()
            com.miui.warningcenter.WarningCenterAlertAdapter$ViewHolder r13 = (com.miui.warningcenter.WarningCenterAlertAdapter.ViewHolder) r13
        L_0x00e5:
            java.util.List<com.miui.warningcenter.WarningAlertBaseModel> r0 = r10.mDatas
            java.lang.Object r0 = r0.get(r11)
            com.miui.warningcenter.mijia.MijiaAlertModel r0 = (com.miui.warningcenter.mijia.MijiaAlertModel) r0
            java.lang.String r1 = r0.getAlertType()
            r2 = -1
            int r3 = r1.hashCode()
            r4 = 3
            r5 = 2
            r6 = 1
            r7 = 0
            switch(r3) {
                case -1602213623: goto L_0x011c;
                case 109562223: goto L_0x0112;
                case 1303243883: goto L_0x0108;
                case 1961175435: goto L_0x00fe;
                default: goto L_0x00fd;
            }
        L_0x00fd:
            goto L_0x0125
        L_0x00fe:
            java.lang.String r3 = "water_leak"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x0125
            r2 = r5
            goto L_0x0125
        L_0x0108:
            java.lang.String r3 = "break_lock"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x0125
            r2 = r4
            goto L_0x0125
        L_0x0112:
            java.lang.String r3 = "smoke"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x0125
            r2 = r6
            goto L_0x0125
        L_0x011c:
            java.lang.String r3 = "gas_leak"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x0125
            r2 = r7
        L_0x0125:
            if (r2 == 0) goto L_0x017f
            if (r2 == r6) goto L_0x0164
            if (r2 == r5) goto L_0x0149
            if (r2 == r4) goto L_0x012e
            goto L_0x019c
        L_0x012e:
            android.widget.TextView r1 = r13.tvTitle
            r2 = 2131758573(0x7f100ded, float:1.9148114E38)
            r1.setText(r2)
            android.widget.TextView r1 = r13.tvTitleTop
            r1.setText(r2)
            android.widget.ImageView r1 = r13.ivType
            r2 = 2131232607(0x7f08075f, float:1.8081328E38)
            r1.setImageResource(r2)
            android.widget.ImageView r1 = r13.ivTypeTop
            r2 = 2131232608(0x7f080760, float:1.808133E38)
            goto L_0x0199
        L_0x0149:
            android.widget.TextView r1 = r13.tvTitle
            r2 = 2131758572(0x7f100dec, float:1.9148112E38)
            r1.setText(r2)
            android.widget.TextView r1 = r13.tvTitleTop
            r1.setText(r2)
            android.widget.ImageView r1 = r13.ivType
            r2 = 2131232612(0x7f080764, float:1.8081338E38)
            r1.setImageResource(r2)
            android.widget.ImageView r1 = r13.ivTypeTop
            r2 = 2131232613(0x7f080765, float:1.808134E38)
            goto L_0x0199
        L_0x0164:
            android.widget.TextView r1 = r13.tvTitle
            r2 = 2131758571(0x7f100deb, float:1.914811E38)
            r1.setText(r2)
            android.widget.TextView r1 = r13.tvTitleTop
            r1.setText(r2)
            android.widget.ImageView r1 = r13.ivType
            r2 = 2131232610(0x7f080762, float:1.8081334E38)
            r1.setImageResource(r2)
            android.widget.ImageView r1 = r13.ivTypeTop
            r2 = 2131232611(0x7f080763, float:1.8081336E38)
            goto L_0x0199
        L_0x017f:
            android.widget.TextView r1 = r13.tvTitle
            r2 = 2131758570(0x7f100dea, float:1.9148108E38)
            r1.setText(r2)
            android.widget.TextView r1 = r13.tvTitleTop
            r1.setText(r2)
            android.widget.ImageView r1 = r13.ivType
            r2 = 2131232605(0x7f08075d, float:1.8081324E38)
            r1.setImageResource(r2)
            android.widget.ImageView r1 = r13.ivTypeTop
            r2 = 2131232606(0x7f08075e, float:1.8081326E38)
        L_0x0199:
            r1.setImageResource(r2)
        L_0x019c:
            java.text.SimpleDateFormat r1 = new java.text.SimpleDateFormat
            java.lang.String r2 = "HH:mm"
            r1.<init>(r2)
            long r2 = r0.getCtime()
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            java.lang.String r1 = r1.format(r2)
            java.lang.String r2 = r0.getHomeName()
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            r3 = 8
            if (r2 == 0) goto L_0x01eb
            java.lang.String r2 = r0.getRoomName()
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x01eb
            android.widget.TextView r2 = r13.warningTime
            android.app.Activity r4 = r10.mActivity
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2131758566(0x7f100de6, float:1.91481E38)
            java.lang.Object[] r8 = new java.lang.Object[r6]
            r8[r7] = r1
            java.lang.String r1 = r4.getString(r5, r8)
            r2.setText(r1)
            android.widget.TextView r1 = r13.warningTime
            r1.setVisibility(r7)
            android.widget.LinearLayout r1 = r13.containerTitle
            r1.setVisibility(r3)
            android.widget.LinearLayout r1 = r13.containerCard
            r1.setVisibility(r3)
            goto L_0x0225
        L_0x01eb:
            java.lang.String r2 = r0.getRoomName()
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x020e
            android.widget.TextView r2 = r13.homeText
            java.lang.String r4 = r0.getHomeName()
            r2.setText(r4)
            android.widget.TextView r2 = r13.timeText
            r2.setText(r1)
            android.widget.RelativeLayout r1 = r13.roomContainer
            r1.setVisibility(r3)
            android.widget.RelativeLayout r1 = r13.roomCard
            r1.setVisibility(r3)
            goto L_0x0225
        L_0x020e:
            android.widget.TextView r2 = r13.homeText
            java.lang.String r4 = r0.getHomeName()
            r2.setText(r4)
            android.widget.TextView r2 = r13.roomText
            java.lang.String r4 = r0.getRoomName()
            r2.setText(r4)
            android.widget.TextView r2 = r13.timeText
            r2.setText(r1)
        L_0x0225:
            android.widget.TextView r1 = r13.alertSource
            android.app.Activity r2 = r10.mActivity
            android.content.res.Resources r2 = r2.getResources()
            r4 = 2131758564(0x7f100de4, float:1.9148096E38)
            java.lang.Object[] r5 = new java.lang.Object[r6]
            android.app.Activity r8 = r10.mActivity
            android.content.res.Resources r8 = r8.getResources()
            r9 = 2131758588(0x7f100dfc, float:1.9148144E38)
            java.lang.String r8 = r8.getString(r9)
            r5[r7] = r8
            java.lang.String r2 = r2.getString(r4, r5)
            r1.setText(r2)
            android.widget.TextView r1 = r13.alertSettings
            r1.setVisibility(r7)
            android.widget.Button r1 = r13.viewDevice
            r1.setVisibility(r7)
            android.widget.Button r1 = r13.viewDevice
            com.miui.warningcenter.WarningCenterAlertAdapter$1 r2 = new com.miui.warningcenter.WarningCenterAlertAdapter$1
            r2.<init>(r11, r0)
            r1.setOnClickListener(r2)
            android.widget.LinearLayout r1 = r13.root
            com.miui.warningcenter.WarningCenterAlertAdapter$2 r2 = new com.miui.warningcenter.WarningCenterAlertAdapter$2
            r2.<init>(r11, r0)
            r1.setOnClickListener(r2)
            java.util.List<com.miui.warningcenter.WarningAlertBaseModel> r11 = r10.mDatas
            int r11 = r11.size()
            if (r11 <= r6) goto L_0x0285
            android.widget.Button r11 = r13.viewDevice
            r11.setVisibility(r3)
            android.widget.TextView r11 = r13.alertSettings
            r11.setVisibility(r3)
            android.widget.TextView r11 = r13.alertSource
            r11.setVisibility(r3)
            android.widget.LinearLayout r11 = r13.llTitleTop
            r11.setVisibility(r3)
            android.widget.LinearLayout r11 = r13.llTitle
            goto L_0x028c
        L_0x0285:
            android.widget.LinearLayout r11 = r13.llTitle
            r11.setVisibility(r3)
            android.widget.LinearLayout r11 = r13.llTitleTop
        L_0x028c:
            r11.setVisibility(r7)
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.warningcenter.WarningCenterAlertAdapter.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }

    public void setDatas(List<WarningAlertBaseModel> list) {
        this.mDatas = list;
        notifyDataSetChanged();
    }

    public void setListener(Listener listener2) {
        this.listener = listener2;
    }
}
