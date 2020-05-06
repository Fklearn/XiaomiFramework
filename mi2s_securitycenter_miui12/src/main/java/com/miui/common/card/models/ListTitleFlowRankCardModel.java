package com.miui.common.card.models;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import b.b.c.j.l;
import com.miui.common.card.BaseViewHolder;
import com.miui.securitycenter.R;
import com.miui.securityscan.a.G;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.model.manualitem.FlowRankModel;
import com.miui.securityscan.scanner.O;
import java.util.List;

public class ListTitleFlowRankCardModel extends BaseCardModel {
    /* access modifiers changed from: private */
    public O.a amoListener;
    /* access modifiers changed from: private */
    public AbsModel curModel;
    /* access modifiers changed from: private */
    public List<FlowRankModel.RankDataModel> flowRankDataModels;
    private int score;

    public class ListTitleFlowRankViewHolder extends BaseViewHolder {
        /* access modifiers changed from: private */
        public Context mContext;
        private ViewHolder[] mViewHolderArray;

        public ListTitleFlowRankViewHolder(View view) {
            super(view);
            this.mContext = view.getContext();
            initView(view);
            l.a(view);
        }

        private void initView(View view) {
            ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.item1);
            ViewGroup viewGroup2 = (ViewGroup) view.findViewById(R.id.item2);
            ViewGroup viewGroup3 = (ViewGroup) view.findViewById(R.id.item3);
            ViewGroup viewGroup4 = (ViewGroup) view.findViewById(R.id.item4);
            ViewGroup viewGroup5 = (ViewGroup) view.findViewById(R.id.item5);
            this.mViewHolderArray = new ViewHolder[5];
            this.mViewHolderArray[0] = new ViewHolder();
            ViewHolder[] viewHolderArr = this.mViewHolderArray;
            viewHolderArr[0].parent = viewGroup;
            viewHolderArr[0].title = (TextView) viewGroup.findViewById(R.id.title);
            this.mViewHolderArray[0].bar = (TextView) viewGroup.findViewById(R.id.bar);
            this.mViewHolderArray[0].value = (TextView) viewGroup.findViewById(R.id.value);
            this.mViewHolderArray[1] = new ViewHolder();
            ViewHolder[] viewHolderArr2 = this.mViewHolderArray;
            viewHolderArr2[1].parent = viewGroup2;
            viewHolderArr2[1].title = (TextView) viewGroup2.findViewById(R.id.title);
            this.mViewHolderArray[1].bar = (TextView) viewGroup2.findViewById(R.id.bar);
            this.mViewHolderArray[1].value = (TextView) viewGroup2.findViewById(R.id.value);
            this.mViewHolderArray[2] = new ViewHolder();
            ViewHolder[] viewHolderArr3 = this.mViewHolderArray;
            viewHolderArr3[2].parent = viewGroup3;
            viewHolderArr3[2].title = (TextView) viewGroup3.findViewById(R.id.title);
            this.mViewHolderArray[2].bar = (TextView) viewGroup3.findViewById(R.id.bar);
            this.mViewHolderArray[2].value = (TextView) viewGroup3.findViewById(R.id.value);
            this.mViewHolderArray[3] = new ViewHolder();
            ViewHolder[] viewHolderArr4 = this.mViewHolderArray;
            viewHolderArr4[3].parent = viewGroup4;
            viewHolderArr4[3].title = (TextView) viewGroup4.findViewById(R.id.title);
            this.mViewHolderArray[3].bar = (TextView) viewGroup4.findViewById(R.id.bar);
            this.mViewHolderArray[3].value = (TextView) viewGroup4.findViewById(R.id.value);
            this.mViewHolderArray[4] = new ViewHolder();
            ViewHolder[] viewHolderArr5 = this.mViewHolderArray;
            viewHolderArr5[4].parent = viewGroup5;
            viewHolderArr5[4].title = (TextView) viewGroup5.findViewById(R.id.title);
            this.mViewHolderArray[4].bar = (TextView) viewGroup5.findViewById(R.id.bar);
            this.mViewHolderArray[4].value = (TextView) viewGroup5.findViewById(R.id.value);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x00c5, code lost:
            if (r12 < r4) goto L_0x00c7;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void updateData(java.util.List<com.miui.securityscan.model.manualitem.FlowRankModel.RankDataModel> r18) {
            /*
                r17 = this;
                r1 = r17
                r0 = r18
                android.content.Context r2 = r1.mContext
                android.content.pm.PackageManager r2 = r2.getPackageManager()
                android.content.Context r3 = r1.mContext
                android.content.res.Resources r3 = r3.getResources()
                r4 = 2131166820(0x7f070664, float:1.7947896E38)
                int r3 = r3.getDimensionPixelSize(r4)
                android.content.Context r4 = r1.mContext
                android.content.res.Resources r4 = r4.getResources()
                r5 = 2131166821(0x7f070665, float:1.7947898E38)
                int r4 = r4.getDimensionPixelOffset(r5)
                com.miui.common.card.models.ListTitleFlowRankCardModel$ViewHolder[] r5 = r1.mViewHolderArray
                int r6 = r5.length
                r7 = 0
                r8 = r7
            L_0x0029:
                if (r8 >= r6) goto L_0x0037
                r9 = r5[r8]
                android.view.ViewGroup r9 = r9.parent
                r10 = 8
                r9.setVisibility(r10)
                int r8 = r8 + 1
                goto L_0x0029
            L_0x0037:
                r8 = r7
                r11 = r8
                r9 = 0
            L_0x003b:
                int r12 = r18.size()
                if (r8 >= r12) goto L_0x00d9
                java.lang.Object r12 = r0.get(r8)
                com.miui.securityscan.model.manualitem.FlowRankModel$RankDataModel r12 = (com.miui.securityscan.model.manualitem.FlowRankModel.RankDataModel) r12
                java.lang.String r12 = r12.getTitle()
                if (r12 == 0) goto L_0x00d3
                com.miui.common.card.models.ListTitleFlowRankCardModel$ViewHolder[] r12 = r1.mViewHolderArray
                r12 = r12[r8]
                android.view.ViewGroup r12 = r12.parent
                r12.setVisibility(r7)
                com.miui.common.card.models.ListTitleFlowRankCardModel$ViewHolder[] r12 = r1.mViewHolderArray
                r12 = r12[r8]
                android.widget.TextView r12 = r12.title
                java.lang.Object r13 = r0.get(r8)
                com.miui.securityscan.model.manualitem.FlowRankModel$RankDataModel r13 = (com.miui.securityscan.model.manualitem.FlowRankModel.RankDataModel) r13
                java.lang.String r13 = r13.getTitle()
                r12.setText(r13)
                java.lang.Object r12 = r0.get(r8)
                com.miui.securityscan.model.manualitem.FlowRankModel$RankDataModel r12 = (com.miui.securityscan.model.manualitem.FlowRankModel.RankDataModel) r12
                java.lang.String r12 = r12.getValue()
                long r12 = java.lang.Long.parseLong(r12)
                com.miui.common.card.models.ListTitleFlowRankCardModel$ViewHolder[] r14 = r1.mViewHolderArray
                r14 = r14[r8]
                android.widget.TextView r14 = r14.value
                android.content.Context r15 = r1.mContext
                r5 = 1
                java.lang.String r5 = b.b.c.j.n.a((android.content.Context) r15, (long) r12, (int) r5)
                r14.setText(r5)
                java.lang.Object r5 = r0.get(r8)     // Catch:{ Exception -> 0x00ce }
                com.miui.securityscan.model.manualitem.FlowRankModel$RankDataModel r5 = (com.miui.securityscan.model.manualitem.FlowRankModel.RankDataModel) r5     // Catch:{ Exception -> 0x00ce }
                java.lang.String r5 = r5.getTitle()     // Catch:{ Exception -> 0x00ce }
                android.content.pm.ApplicationInfo r5 = r2.getApplicationInfo(r5, r7)     // Catch:{ Exception -> 0x00ce }
                com.miui.common.card.models.ListTitleFlowRankCardModel$ViewHolder[] r6 = r1.mViewHolderArray     // Catch:{ Exception -> 0x00ce }
                r6 = r6[r8]     // Catch:{ Exception -> 0x00ce }
                android.widget.TextView r6 = r6.title     // Catch:{ Exception -> 0x00ce }
                java.lang.CharSequence r5 = r5.loadLabel(r2)     // Catch:{ Exception -> 0x00ce }
                java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x00ce }
                r6.setText(r5)     // Catch:{ Exception -> 0x00ce }
                com.miui.common.card.models.ListTitleFlowRankCardModel$ViewHolder[] r5 = r1.mViewHolderArray     // Catch:{ Exception -> 0x00ce }
                r5 = r5[r8]     // Catch:{ Exception -> 0x00ce }
                android.widget.TextView r5 = r5.bar     // Catch:{ Exception -> 0x00ce }
                android.view.ViewGroup$LayoutParams r6 = r5.getLayoutParams()     // Catch:{ Exception -> 0x00ce }
                if (r8 != 0) goto L_0x00b9
                r6.height = r3     // Catch:{ Exception -> 0x00ce }
                r11 = r3
                r9 = r12
                r14 = 0
                goto L_0x00ca
            L_0x00b9:
                r14 = 0
                int r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
                if (r16 == 0) goto L_0x00c7
                float r12 = (float) r12     // Catch:{ Exception -> 0x00ce }
                float r13 = (float) r9     // Catch:{ Exception -> 0x00ce }
                float r12 = r12 / r13
                float r13 = (float) r11     // Catch:{ Exception -> 0x00ce }
                float r13 = r13 * r12
                int r12 = (int) r13     // Catch:{ Exception -> 0x00ce }
                if (r12 >= r4) goto L_0x00c8
            L_0x00c7:
                r12 = r4
            L_0x00c8:
                r6.height = r12     // Catch:{ Exception -> 0x00ce }
            L_0x00ca:
                r5.setLayoutParams(r6)     // Catch:{ Exception -> 0x00ce }
                goto L_0x00d5
            L_0x00ce:
                r0 = move-exception
                r0.printStackTrace()
                return
            L_0x00d3:
                r14 = 0
            L_0x00d5:
                int r8 = r8 + 1
                goto L_0x003b
            L_0x00d9:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.common.card.models.ListTitleFlowRankCardModel.ListTitleFlowRankViewHolder.updateData(java.util.List):void");
        }

        public void fillData(View view, final BaseCardModel baseCardModel, int i) {
            super.fillData(view, baseCardModel, i);
            final ListTitleFlowRankCardModel listTitleFlowRankCardModel = (ListTitleFlowRankCardModel) baseCardModel;
            if (listTitleFlowRankCardModel.flowRankDataModels != null) {
                updateData(listTitleFlowRankCardModel.flowRankDataModels);
                if (this.tvButton != null) {
                    AnonymousClass1 r0 = new View.OnClickListener() {
                        public void onClick(View view) {
                            if (ListTitleFlowRankCardModel.this.amoListener != null) {
                                ListTitleFlowRankCardModel.this.amoListener.b(ListTitleFlowRankCardModel.this.curModel);
                                ListTitleFlowRankCardModel.this.amoListener.a(ListTitleFlowRankCardModel.this.curModel.getScore(), ListTitleFlowRankCardModel.this.curModel.isDelayOptimized());
                            }
                            G.r(ListTitleFlowRankCardModel.this.curModel.getTrackStr());
                        }
                    };
                    this.tvButton.setOnClickListener(r0);
                    view.setOnClickListener(r0);
                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        public boolean onLongClick(View view) {
                            if (listTitleFlowRankCardModel.curModel == null) {
                                return true;
                            }
                            ListTitleFlowRankViewHolder.this.showManualItemLongClickDialog(baseCardModel, listTitleFlowRankCardModel.curModel, ListTitleFlowRankViewHolder.this.mContext);
                            return true;
                        }
                    });
                    if (listTitleFlowRankCardModel.getScore() > 0) {
                        String charSequence = this.tvButton.getText().toString();
                        String quantityString = this.mContext.getResources().getQuantityString(R.plurals.optimize_result_button_add_score, listTitleFlowRankCardModel.getScore(), new Object[]{Integer.valueOf(listTitleFlowRankCardModel.getScore())});
                        Button button = this.tvButton;
                        button.setText(charSequence + quantityString);
                    }
                }
            }
        }
    }

    static class ViewHolder {
        TextView bar;
        ViewGroup parent;
        TextView title;
        TextView value;

        ViewHolder() {
        }
    }

    public ListTitleFlowRankCardModel(O.a aVar, AbsModel absModel) {
        super(R.layout.card_layout_flow_rank);
        this.amoListener = aVar;
        this.curModel = absModel;
    }

    public BaseViewHolder createViewHolder(View view) {
        return new ListTitleFlowRankViewHolder(view);
    }

    public AbsModel getCurModel() {
        return this.curModel;
    }

    public List<FlowRankModel.RankDataModel> getFlowRankDataModels() {
        return this.flowRankDataModels;
    }

    public int getScore() {
        return this.score;
    }

    public void setFlowRankDataModels(List<FlowRankModel.RankDataModel> list) {
        this.flowRankDataModels = list;
    }

    public void setScore(int i) {
        this.score = i;
    }

    public boolean validate() {
        return true;
    }
}
