package com.miui.warningcenter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidxc.recyclerview.widget.RecyclerView;
import com.miui.securitycenter.R;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.util.ArrayList;
import java.util.List;

public class WarningCenterAlertRecyclerAdapter extends RecyclerView.a<MyViewHolder> {
    /* access modifiers changed from: private */
    public Listener listener;
    private Activity mActivity;
    private List<WarningAlertBaseModel> mDatas;

    public interface Listener {
        void onItemClick(int i, MijiaAlertModel mijiaAlertModel);
    }

    public static class MyViewHolder extends RecyclerView.a {
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

        public MyViewHolder(View view) {
            super(view);
            this.tvTitle = (TextView) view.findViewById(R.id.tv_title);
            this.tvTitleTop = (TextView) view.findViewById(R.id.tv_title_top);
            this.ivType = (ImageView) view.findViewById(R.id.iv_type);
            this.ivTypeTop = (ImageView) view.findViewById(R.id.iv_type_top);
            this.llTitleTop = (LinearLayout) view.findViewById(R.id.ll_title_top);
            this.llTitle = (LinearLayout) view.findViewById(R.id.ll_title);
            this.warningTime = (TextView) view.findViewById(R.id.warning_time);
            this.homeText = (TextView) view.findViewById(R.id.home_text);
            this.roomText = (TextView) view.findViewById(R.id.room_text);
            this.timeText = (TextView) view.findViewById(R.id.time_text);
            this.alertSource = (TextView) view.findViewById(R.id.alert_source);
            this.alertSettings = (TextView) view.findViewById(R.id.alert_settings);
            this.viewDevice = (Button) view.findViewById(R.id.view_device);
            this.containerTitle = (LinearLayout) view.findViewById(R.id.container_title);
            this.containerCard = (LinearLayout) view.findViewById(R.id.container_card);
            this.roomContainer = (RelativeLayout) view.findViewById(R.id.room_container);
            this.roomCard = (RelativeLayout) view.findViewById(R.id.room_card);
            this.root = (LinearLayout) view.findViewById(R.id.root);
        }
    }

    public WarningCenterAlertRecyclerAdapter(Activity activity) {
        this.mActivity = activity;
    }

    public void addData(WarningAlertBaseModel warningAlertBaseModel) {
        if (this.mDatas == null) {
            this.mDatas = new ArrayList();
        }
        this.mDatas.add(0, warningAlertBaseModel);
        notifyItemInserted(0);
        if (this.mDatas.size() == 2) {
            notifyItemChanged(1);
        }
    }

    public int getItemCount() {
        List<WarningAlertBaseModel> list = this.mDatas;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00e1  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x01a1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBindViewHolder(com.miui.warningcenter.WarningCenterAlertRecyclerAdapter.MyViewHolder r11, final int r12) {
        /*
            r10 = this;
            java.util.List<com.miui.warningcenter.WarningAlertBaseModel> r0 = r10.mDatas
            java.lang.Object r0 = r0.get(r12)
            com.miui.warningcenter.mijia.MijiaAlertModel r0 = (com.miui.warningcenter.mijia.MijiaAlertModel) r0
            java.lang.String r1 = r0.getAlertType()
            int r2 = r1.hashCode()
            r3 = 3
            r4 = 2
            r5 = 1
            r6 = 0
            switch(r2) {
                case -1602213623: goto L_0x0036;
                case 109562223: goto L_0x002c;
                case 1303243883: goto L_0x0022;
                case 1961175435: goto L_0x0018;
                default: goto L_0x0017;
            }
        L_0x0017:
            goto L_0x0040
        L_0x0018:
            java.lang.String r2 = "water_leak"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0040
            r1 = r4
            goto L_0x0041
        L_0x0022:
            java.lang.String r2 = "break_lock"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0040
            r1 = r3
            goto L_0x0041
        L_0x002c:
            java.lang.String r2 = "smoke"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0040
            r1 = r5
            goto L_0x0041
        L_0x0036:
            java.lang.String r2 = "gas_leak"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0040
            r1 = r6
            goto L_0x0041
        L_0x0040:
            r1 = -1
        L_0x0041:
            if (r1 == 0) goto L_0x009b
            if (r1 == r5) goto L_0x0080
            if (r1 == r4) goto L_0x0065
            if (r1 == r3) goto L_0x004a
            goto L_0x00b8
        L_0x004a:
            android.widget.TextView r1 = r11.tvTitle
            r2 = 2131758573(0x7f100ded, float:1.9148114E38)
            r1.setText(r2)
            android.widget.TextView r1 = r11.tvTitleTop
            r1.setText(r2)
            android.widget.ImageView r1 = r11.ivType
            r2 = 2131232607(0x7f08075f, float:1.8081328E38)
            r1.setImageResource(r2)
            android.widget.ImageView r1 = r11.ivTypeTop
            r2 = 2131232608(0x7f080760, float:1.808133E38)
            goto L_0x00b5
        L_0x0065:
            android.widget.TextView r1 = r11.tvTitle
            r2 = 2131758572(0x7f100dec, float:1.9148112E38)
            r1.setText(r2)
            android.widget.TextView r1 = r11.tvTitleTop
            r1.setText(r2)
            android.widget.ImageView r1 = r11.ivType
            r2 = 2131232612(0x7f080764, float:1.8081338E38)
            r1.setImageResource(r2)
            android.widget.ImageView r1 = r11.ivTypeTop
            r2 = 2131232613(0x7f080765, float:1.808134E38)
            goto L_0x00b5
        L_0x0080:
            android.widget.TextView r1 = r11.tvTitle
            r2 = 2131758571(0x7f100deb, float:1.914811E38)
            r1.setText(r2)
            android.widget.TextView r1 = r11.tvTitleTop
            r1.setText(r2)
            android.widget.ImageView r1 = r11.ivType
            r2 = 2131232610(0x7f080762, float:1.8081334E38)
            r1.setImageResource(r2)
            android.widget.ImageView r1 = r11.ivTypeTop
            r2 = 2131232611(0x7f080763, float:1.8081336E38)
            goto L_0x00b5
        L_0x009b:
            android.widget.TextView r1 = r11.tvTitle
            r2 = 2131758570(0x7f100dea, float:1.9148108E38)
            r1.setText(r2)
            android.widget.TextView r1 = r11.tvTitleTop
            r1.setText(r2)
            android.widget.ImageView r1 = r11.ivType
            r2 = 2131232605(0x7f08075d, float:1.8081324E38)
            r1.setImageResource(r2)
            android.widget.ImageView r1 = r11.ivTypeTop
            r2 = 2131232606(0x7f08075e, float:1.8081326E38)
        L_0x00b5:
            r1.setImageResource(r2)
        L_0x00b8:
            java.text.SimpleDateFormat r1 = new java.text.SimpleDateFormat
            java.lang.String r2 = "HH:mm"
            r1.<init>(r2)
            long r2 = r0.getCtime()
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            java.lang.String r1 = r1.format(r2)
            java.lang.String r2 = r0.getHomeName()
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            r3 = 8
            if (r2 == 0) goto L_0x0107
            java.lang.String r2 = r0.getRoomName()
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x0107
            android.widget.TextView r2 = r11.warningTime
            android.app.Activity r4 = r10.mActivity
            android.content.res.Resources r4 = r4.getResources()
            r7 = 2131758566(0x7f100de6, float:1.91481E38)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            r8[r6] = r1
            java.lang.String r1 = r4.getString(r7, r8)
            r2.setText(r1)
            android.widget.TextView r1 = r11.warningTime
            r1.setVisibility(r6)
            android.widget.LinearLayout r1 = r11.containerTitle
            r1.setVisibility(r3)
            android.widget.LinearLayout r1 = r11.containerCard
            r1.setVisibility(r3)
            goto L_0x0141
        L_0x0107:
            java.lang.String r2 = r0.getRoomName()
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x012a
            android.widget.TextView r2 = r11.homeText
            java.lang.String r4 = r0.getHomeName()
            r2.setText(r4)
            android.widget.TextView r2 = r11.timeText
            r2.setText(r1)
            android.widget.RelativeLayout r1 = r11.roomContainer
            r1.setVisibility(r3)
            android.widget.RelativeLayout r1 = r11.roomCard
            r1.setVisibility(r3)
            goto L_0x0141
        L_0x012a:
            android.widget.TextView r2 = r11.homeText
            java.lang.String r4 = r0.getHomeName()
            r2.setText(r4)
            android.widget.TextView r2 = r11.roomText
            java.lang.String r4 = r0.getRoomName()
            r2.setText(r4)
            android.widget.TextView r2 = r11.timeText
            r2.setText(r1)
        L_0x0141:
            android.widget.TextView r1 = r11.alertSource
            android.app.Activity r2 = r10.mActivity
            android.content.res.Resources r2 = r2.getResources()
            r4 = 2131758564(0x7f100de4, float:1.9148096E38)
            java.lang.Object[] r7 = new java.lang.Object[r5]
            android.app.Activity r8 = r10.mActivity
            android.content.res.Resources r8 = r8.getResources()
            r9 = 2131758588(0x7f100dfc, float:1.9148144E38)
            java.lang.String r8 = r8.getString(r9)
            r7[r6] = r8
            java.lang.String r2 = r2.getString(r4, r7)
            r1.setText(r2)
            android.widget.TextView r1 = r11.alertSettings
            r1.setVisibility(r6)
            android.widget.Button r1 = r11.viewDevice
            r1.setVisibility(r6)
            android.widget.Button r1 = r11.viewDevice
            com.miui.warningcenter.WarningCenterAlertRecyclerAdapter$1 r2 = new com.miui.warningcenter.WarningCenterAlertRecyclerAdapter$1
            r2.<init>(r12, r0)
            r1.setOnClickListener(r2)
            android.widget.LinearLayout r1 = r11.root
            com.miui.warningcenter.WarningCenterAlertRecyclerAdapter$2 r2 = new com.miui.warningcenter.WarningCenterAlertRecyclerAdapter$2
            r2.<init>(r12, r0)
            r1.setOnClickListener(r2)
            java.util.List<com.miui.warningcenter.WarningAlertBaseModel> r12 = r10.mDatas
            int r12 = r12.size()
            if (r12 <= r5) goto L_0x01a1
            android.widget.Button r12 = r11.viewDevice
            r12.setVisibility(r3)
            android.widget.TextView r12 = r11.alertSettings
            r12.setVisibility(r3)
            android.widget.TextView r12 = r11.alertSource
            r12.setVisibility(r3)
            android.widget.LinearLayout r12 = r11.llTitleTop
            r12.setVisibility(r3)
            android.widget.LinearLayout r11 = r11.llTitle
            goto L_0x01a8
        L_0x01a1:
            android.widget.LinearLayout r12 = r11.llTitle
            r12.setVisibility(r3)
            android.widget.LinearLayout r11 = r11.llTitleTop
        L_0x01a8:
            r11.setVisibility(r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.warningcenter.WarningCenterAlertRecyclerAdapter.onBindViewHolder(com.miui.warningcenter.WarningCenterAlertRecyclerAdapter$MyViewHolder, int):void");
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(this.mActivity).inflate(R.layout.warning_center_alert_card, viewGroup, false));
    }

    public void setDatas(List<WarningAlertBaseModel> list) {
        this.mDatas = list;
        notifyDataSetChanged();
    }

    public void setListener(Listener listener2) {
        this.listener = listener2;
    }
}
