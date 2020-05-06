package com.miui.common.card.models;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.C;
import b.b.c.j.l;
import com.miui.common.card.BaseViewHolder;
import com.miui.powercenter.f.a;
import com.miui.powercenter.utils.b;
import com.miui.powercenter.utils.j;
import com.miui.securitycenter.R;
import com.miui.securityscan.a.G;
import com.miui.securityscan.model.manualitem.ConsumePowerRankModel;
import com.miui.securityscan.scanner.O;
import java.util.List;

public class ListTitleConsumePowerRankCardModel extends BaseCardModel {
    /* access modifiers changed from: private */
    public O.a amoListener;
    /* access modifiers changed from: private */
    public List<a> appConsumeInfoList;
    /* access modifiers changed from: private */
    public ConsumePowerRankModel curModel;
    /* access modifiers changed from: private */
    public int score;

    public class ListTitleConsumePowerRankViewHolder extends BaseViewHolder {
        /* access modifiers changed from: private */
        public Context mContext;
        private ViewHolder[] mViewHolderArray;

        public ListTitleConsumePowerRankViewHolder(View view) {
            super(view);
            this.mContext = view.getContext();
            initView(view);
            l.a(view);
        }

        private void initView(View view) {
            ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.item1);
            ViewGroup viewGroup2 = (ViewGroup) view.findViewById(R.id.item2);
            ViewGroup viewGroup3 = (ViewGroup) view.findViewById(R.id.item3);
            this.mViewHolderArray = new ViewHolder[3];
            this.mViewHolderArray[0] = new ViewHolder();
            ViewHolder[] viewHolderArr = this.mViewHolderArray;
            viewHolderArr[0].parent = viewGroup;
            viewHolderArr[0].icon = (ImageView) viewGroup.findViewById(R.id.icon);
            this.mViewHolderArray[0].name = (TextView) viewGroup.findViewById(R.id.title);
            this.mViewHolderArray[0].percent = (TextView) viewGroup.findViewById(R.id.percent);
            this.mViewHolderArray[1] = new ViewHolder();
            ViewHolder[] viewHolderArr2 = this.mViewHolderArray;
            viewHolderArr2[1].parent = viewGroup2;
            viewHolderArr2[1].icon = (ImageView) viewGroup2.findViewById(R.id.icon);
            this.mViewHolderArray[1].name = (TextView) viewGroup2.findViewById(R.id.title);
            this.mViewHolderArray[1].percent = (TextView) viewGroup2.findViewById(R.id.percent);
            this.mViewHolderArray[2] = new ViewHolder();
            ViewHolder[] viewHolderArr3 = this.mViewHolderArray;
            viewHolderArr3[2].parent = viewGroup3;
            viewHolderArr3[2].icon = (ImageView) viewGroup3.findViewById(R.id.icon);
            this.mViewHolderArray[2].name = (TextView) viewGroup3.findViewById(R.id.title);
            this.mViewHolderArray[2].percent = (TextView) viewGroup3.findViewById(R.id.percent);
        }

        private void updateData(List<a> list) {
            ImageView imageView;
            Drawable drawable;
            if (list != null) {
                for (ViewHolder viewHolder : this.mViewHolderArray) {
                    viewHolder.parent.setVisibility(8);
                }
                for (int i = 0; i < list.size(); i++) {
                    this.mViewHolderArray[i].parent.setVisibility(0);
                    this.mViewHolderArray[i].name.setText(list.get(i).f7063b);
                    this.mViewHolderArray[i].percent.setText(String.format("%.1f%%", new Object[]{Double.valueOf(list.get(i).f7064c)}));
                    if (list.get(i).f7065d > 0) {
                        b.a(this.mViewHolderArray[i].icon, list.get(i).f7065d);
                    } else {
                        if (TextUtils.isEmpty(list.get(i).f7062a)) {
                            PackageManager packageManager = this.mContext.getPackageManager();
                            imageView = this.mViewHolderArray[i].icon;
                            drawable = packageManager.getDefaultActivityIcon();
                        } else if (C.b(j.a(list.get(i).e))) {
                            Context context = this.mViewHolderArray[i].icon.getContext();
                            drawable = C.a(context, new BitmapDrawable(context.getResources(), b.a(list.get(i).f7062a)), list.get(i).e);
                            imageView = this.mViewHolderArray[i].icon;
                        } else {
                            b.a(this.mViewHolderArray[i].icon, list.get(i).f7062a);
                        }
                        imageView.setImageDrawable(drawable);
                    }
                }
            }
        }

        public void fillData(View view, final BaseCardModel baseCardModel, int i) {
            super.fillData(view, baseCardModel, i);
            final ListTitleConsumePowerRankCardModel listTitleConsumePowerRankCardModel = (ListTitleConsumePowerRankCardModel) baseCardModel;
            updateData(listTitleConsumePowerRankCardModel.appConsumeInfoList);
            if (this.tvButton != null) {
                AnonymousClass1 r0 = new View.OnClickListener() {
                    public void onClick(View view) {
                        if (ListTitleConsumePowerRankCardModel.this.amoListener != null) {
                            ListTitleConsumePowerRankCardModel.this.amoListener.b(ListTitleConsumePowerRankCardModel.this.curModel);
                            ListTitleConsumePowerRankCardModel.this.amoListener.a(ListTitleConsumePowerRankCardModel.this.score, ListTitleConsumePowerRankCardModel.this.curModel.isDelayOptimized());
                        }
                        G.r(ListTitleConsumePowerRankCardModel.this.curModel.getTrackStr());
                    }
                };
                this.tvButton.setOnClickListener(r0);
                view.setOnClickListener(r0);
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View view) {
                        if (listTitleConsumePowerRankCardModel.curModel == null) {
                            return true;
                        }
                        ListTitleConsumePowerRankViewHolder.this.showManualItemLongClickDialog(baseCardModel, listTitleConsumePowerRankCardModel.curModel, ListTitleConsumePowerRankViewHolder.this.mContext);
                        return true;
                    }
                });
                if (listTitleConsumePowerRankCardModel.getScore() > 0) {
                    String charSequence = this.tvButton.getText().toString();
                    String quantityString = this.mContext.getResources().getQuantityString(R.plurals.optimize_result_button_add_score, listTitleConsumePowerRankCardModel.getScore(), new Object[]{Integer.valueOf(listTitleConsumePowerRankCardModel.getScore())});
                    Button button = this.tvButton;
                    button.setText(charSequence + quantityString);
                }
            }
        }
    }

    static class ViewHolder {
        ImageView icon;
        TextView name;
        ViewGroup parent;
        TextView percent;

        ViewHolder() {
        }
    }

    public ListTitleConsumePowerRankCardModel(O.a aVar, ConsumePowerRankModel consumePowerRankModel) {
        super(R.layout.card_layout_consume_power_rank);
        this.amoListener = aVar;
        this.curModel = consumePowerRankModel;
    }

    public BaseViewHolder createViewHolder(View view) {
        return new ListTitleConsumePowerRankViewHolder(view);
    }

    public List<a> getAppConsumeInfoList() {
        return this.appConsumeInfoList;
    }

    public ConsumePowerRankModel getCurModel() {
        return this.curModel;
    }

    public int getScore() {
        return this.score;
    }

    public void setAppConsumeInfoList(List<a> list) {
        this.appConsumeInfoList = list;
    }

    public void setScore(int i) {
        this.score = i;
    }

    public boolean validate() {
        return true;
    }
}
