package com.miui.common.card.models;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import b.b.c.j.A;
import com.miui.common.card.BaseViewHolder;
import com.miui.securitycenter.R;
import com.miui.securityscan.a.G;
import com.miui.securityscan.i.f;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.model.GroupModel;
import com.miui.securityscan.scanner.O;
import java.util.ArrayList;
import java.util.List;

public class ListTitleCheckboxCardModel extends BaseCardModel {
    /* access modifiers changed from: private */
    public O.a amoListener;
    /* access modifiers changed from: private */
    public String btnText;
    /* access modifiers changed from: private */
    public List<AbsModel> dangerModelList = new ArrayList();
    private GroupModel group;
    /* access modifiers changed from: private */
    public String groupToast;
    /* access modifiers changed from: private */
    public boolean needRefreshManualItem;
    /* access modifiers changed from: private */
    public int resId;

    public class ListTitleCheckboxViewHolder extends BaseViewHolder {
        /* access modifiers changed from: private */
        public CheckBox[] cbArray;
        private ImageView ivRightOfTitle;
        /* access modifiers changed from: private */
        public Context mContext;
        private TextView[] titleArray;
        private Button tvButtonLocal;
        private View[] viewArray;

        public ListTitleCheckboxViewHolder(View view) {
            super(view);
            this.mContext = view.getContext();
            initView(view);
        }

        private void initView(View view) {
            int i;
            Context context;
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_list_title_checkbox);
            this.ivRightOfTitle = (ImageView) view.findViewById(R.id.iv_right_of_title);
            this.titleArray = new TextView[ListTitleCheckboxCardModel.this.dangerModelList.size()];
            this.cbArray = new CheckBox[ListTitleCheckboxCardModel.this.dangerModelList.size()];
            this.viewArray = new View[ListTitleCheckboxCardModel.this.dangerModelList.size()];
            for (int i2 = 0; i2 < ListTitleCheckboxCardModel.this.dangerModelList.size(); i2++) {
                if (i2 == ListTitleCheckboxCardModel.this.dangerModelList.size() - 1) {
                    context = this.mContext;
                    i = R.layout.card_layout_right_checkbox_no_horizontal_line;
                } else {
                    context = this.mContext;
                    i = R.layout.card_layout_right_checkbox;
                }
                View inflate = View.inflate(context, i, (ViewGroup) null);
                this.titleArray[i2] = (TextView) inflate.findViewById(R.id.title);
                this.cbArray[i2] = (CheckBox) inflate.findViewById(R.id.cb_check);
                this.viewArray[i2] = inflate;
                linearLayout.addView(inflate);
            }
            View inflate2 = View.inflate(this.mContext, R.layout.card_checkbox_layout_bottom_button, (ViewGroup) null);
            this.tvButtonLocal = (Button) inflate2.findViewById(R.id.tv_button);
            linearLayout.addView(inflate2);
        }

        private void updateTvButtonText(ListTitleCheckboxCardModel listTitleCheckboxCardModel) {
            int i = 0;
            for (AbsModel absModel : ListTitleCheckboxCardModel.this.dangerModelList) {
                if (absModel.isChecked()) {
                    i += absModel.getScore();
                }
            }
            if (i > 0 && this.tvButtonLocal != null) {
                String quantityString = this.mContext.getResources().getQuantityString(R.plurals.optimize_result_button_add_score, i, new Object[]{Integer.valueOf(i)});
                this.tvButtonLocal.setText(listTitleCheckboxCardModel.btnText + quantityString);
            }
        }

        public void fillData(View view, final BaseCardModel baseCardModel, int i) {
            super.fillData(view, baseCardModel, i);
            final ListTitleCheckboxCardModel listTitleCheckboxCardModel = (ListTitleCheckboxCardModel) baseCardModel;
            this.tvButtonLocal.setText(listTitleCheckboxCardModel.btnText);
            if (listTitleCheckboxCardModel.resId != 0) {
                this.ivRightOfTitle.setImageResource(listTitleCheckboxCardModel.resId);
            }
            int i2 = 0;
            while (true) {
                CheckBox[] checkBoxArr = this.cbArray;
                if (i2 >= checkBoxArr.length) {
                    break;
                }
                checkBoxArr[i2].setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
                i2++;
            }
            for (int i3 = 0; i3 < this.cbArray.length; i3++) {
                this.titleArray[i3].setText(((AbsModel) listTitleCheckboxCardModel.dangerModelList.get(i3)).getTitle());
                this.cbArray[i3].setChecked(((AbsModel) listTitleCheckboxCardModel.dangerModelList.get(i3)).isChecked());
            }
            updateTvButtonText(listTitleCheckboxCardModel);
            for (int i4 = 0; i4 < this.cbArray.length; i4++) {
                final AbsModel absModel = (AbsModel) ListTitleCheckboxCardModel.this.dangerModelList.get(i4);
                this.cbArray[i4].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        CheckBox checkBox = (CheckBox) compoundButton;
                        checkBox.setChecked(z);
                        absModel.setChecked(checkBox.isChecked());
                        if (ListTitleCheckboxViewHolder.this.handler != null) {
                            ListTitleCheckboxViewHolder.this.handler.sendEmptyMessage(107);
                        }
                    }
                });
            }
            for (int i5 = 0; i5 < this.viewArray.length; i5++) {
                final CheckBox checkBox = this.cbArray[i5];
                final AbsModel absModel2 = (AbsModel) ListTitleCheckboxCardModel.this.dangerModelList.get(i5);
                this.viewArray[i5].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        CheckBox checkBox = checkBox;
                        checkBox.setChecked(!checkBox.isChecked());
                        absModel2.setChecked(checkBox.isChecked());
                        if (ListTitleCheckboxViewHolder.this.handler != null) {
                            ListTitleCheckboxViewHolder.this.handler.sendEmptyMessage(107);
                        }
                    }
                });
                this.viewArray[i5].setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View view) {
                        AbsModel absModel = absModel2;
                        if (absModel == null) {
                            return true;
                        }
                        ListTitleCheckboxViewHolder listTitleCheckboxViewHolder = ListTitleCheckboxViewHolder.this;
                        listTitleCheckboxViewHolder.showManualItemLongClickDialog(baseCardModel, absModel, listTitleCheckboxViewHolder.mContext);
                        return true;
                    }
                });
            }
            this.tvButtonLocal.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Context context;
                    String str;
                    if (!f.a()) {
                        ListTitleCheckboxViewHolder listTitleCheckboxViewHolder = ListTitleCheckboxViewHolder.this;
                        if (ListTitleCheckboxCardModel.this.isChecked(listTitleCheckboxViewHolder.cbArray)) {
                            ListTitleCheckboxViewHolder listTitleCheckboxViewHolder2 = ListTitleCheckboxViewHolder.this;
                            if (ListTitleCheckboxCardModel.this.isAllChecked(listTitleCheckboxViewHolder2.cbArray)) {
                                int i = 0;
                                for (AbsModel absModel : ListTitleCheckboxCardModel.this.dangerModelList) {
                                    i += absModel.getScore();
                                    if (ListTitleCheckboxCardModel.this.amoListener != null) {
                                        ListTitleCheckboxCardModel.this.amoListener.b(absModel);
                                    }
                                    G.r(absModel.getTrackStr());
                                }
                                if (ListTitleCheckboxCardModel.this.amoListener != null) {
                                    ListTitleCheckboxCardModel.this.amoListener.a(i, false);
                                }
                                if (listTitleCheckboxCardModel.needRefreshManualItem && ListTitleCheckboxViewHolder.this.handler != null) {
                                    ListTitleCheckboxViewHolder.this.handler.sendEmptyMessage(102);
                                }
                                if (ListTitleCheckboxCardModel.this.groupToast != null && !TextUtils.isEmpty(ListTitleCheckboxCardModel.this.groupToast)) {
                                    context = ListTitleCheckboxViewHolder.this.mContext;
                                    str = ListTitleCheckboxCardModel.this.groupToast;
                                } else {
                                    return;
                                }
                            } else {
                                ArrayList arrayList = new ArrayList();
                                int i2 = 0;
                                for (AbsModel absModel2 : ListTitleCheckboxCardModel.this.dangerModelList) {
                                    if (absModel2.isChecked()) {
                                        i2 += absModel2.getScore();
                                        if (ListTitleCheckboxCardModel.this.amoListener != null) {
                                            ListTitleCheckboxCardModel.this.amoListener.b(absModel2);
                                        }
                                        G.r(absModel2.getTrackStr());
                                        arrayList.add(absModel2);
                                    }
                                }
                                if (ListTitleCheckboxCardModel.this.amoListener != null) {
                                    ListTitleCheckboxCardModel.this.amoListener.a(i2, false);
                                }
                                ListTitleCheckboxCardModel.this.dangerModelList.removeAll(arrayList);
                                if (ListTitleCheckboxViewHolder.this.handler != null) {
                                    ListTitleCheckboxViewHolder.this.handler.sendEmptyMessage(107);
                                    return;
                                }
                                return;
                            }
                        } else {
                            context = ListTitleCheckboxViewHolder.this.mContext;
                            str = ListTitleCheckboxViewHolder.this.mContext.getString(R.string.optimize_result_toast_system_no_item_selected);
                        }
                        A.a(context, str);
                    }
                }
            });
        }
    }

    public ListTitleCheckboxCardModel(O.a aVar, GroupModel groupModel) {
        super(R.layout.card_layout_list_title_checkbox);
        this.group = groupModel;
        this.amoListener = aVar;
        for (AbsModel next : this.group.getModelList()) {
            if (next.isSafe() != AbsModel.State.SAFE) {
                next.setChecked(true);
                this.dangerModelList.add(next);
            }
        }
        this.noConvertView = true;
    }

    /* access modifiers changed from: private */
    public boolean isAllChecked(CheckBox[] checkBoxArr) {
        if (checkBoxArr == null) {
            return false;
        }
        int i = 0;
        for (CheckBox isChecked : checkBoxArr) {
            if (isChecked.isChecked()) {
                i++;
            }
        }
        return i == checkBoxArr.length;
    }

    /* access modifiers changed from: private */
    public boolean isChecked(CheckBox[] checkBoxArr) {
        if (checkBoxArr == null) {
            return false;
        }
        int i = 0;
        for (CheckBox isChecked : checkBoxArr) {
            if (isChecked.isChecked()) {
                i++;
            }
        }
        return i > 0;
    }

    public BaseViewHolder createViewHolder(View view) {
        return new ListTitleCheckboxViewHolder(view);
    }

    public String getBtnText() {
        return this.btnText;
    }

    public GroupModel getGroup() {
        return this.group;
    }

    public String getGroupToast() {
        return this.groupToast;
    }

    public int getResId() {
        return this.resId;
    }

    public int getScore() {
        int i = 0;
        for (AbsModel next : this.dangerModelList) {
            if (next.isChecked()) {
                i += next.getScore();
            }
        }
        return i;
    }

    public void ignoreAbsModel(AbsModel absModel) {
        List<AbsModel> modelList;
        GroupModel groupModel = this.group;
        if (groupModel != null && (modelList = groupModel.getModelList()) != null && modelList.contains(absModel)) {
            modelList.remove(absModel);
            this.dangerModelList = new ArrayList();
            for (AbsModel next : modelList) {
                if (next.isSafe() != AbsModel.State.SAFE) {
                    this.dangerModelList.add(next);
                }
            }
        }
    }

    public boolean isNeedRefreshManualItem() {
        return this.needRefreshManualItem;
    }

    public boolean isSafe() {
        List<AbsModel> list = this.dangerModelList;
        return list != null && list.isEmpty();
    }

    public void setBtnText(String str) {
        this.btnText = str;
    }

    public void setGroupToast(String str) {
        this.groupToast = str;
    }

    public void setNeedRefreshManualItem(boolean z) {
        this.needRefreshManualItem = z;
    }

    public void setResId(int i) {
        this.resId = i;
    }

    public boolean validate() {
        return true;
    }
}
