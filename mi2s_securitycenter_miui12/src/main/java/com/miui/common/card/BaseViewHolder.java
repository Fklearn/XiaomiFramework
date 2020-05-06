package com.miui.common.card;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.miui.common.card.models.BaseCardModel;
import com.miui.common.card.models.ListTitleCheckboxCardModel;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.m;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.ScoreManager;
import miui.app.AlertDialog;

public class BaseViewHolder {
    protected Button actionButton;
    /* access modifiers changed from: protected */
    public Handler handler;
    protected ImageView imageView;
    protected AlertDialog mAlertDialog;
    protected TextView summaryView;
    protected TextView titleView;
    protected Button tvButton;

    public BaseViewHolder(View view) {
        if (view != null) {
            this.titleView = (TextView) view.findViewById(R.id.title);
            this.summaryView = (TextView) view.findViewById(R.id.summary);
            this.actionButton = (Button) view.findViewById(R.id.button);
            this.tvButton = (Button) view.findViewById(R.id.tv_button);
            this.imageView = (ImageView) view.findViewById(R.id.icon);
            ImageView imageView2 = this.imageView;
            if (imageView2 != null) {
                imageView2.setColorFilter(view.getResources().getColor(R.color.result_banner_icon_bg));
            }
        }
    }

    /* access modifiers changed from: private */
    public void showAlertInfoDialog(final BaseCardModel baseCardModel, final AbsModel absModel, Context context) {
        this.mAlertDialog = new AlertDialog.Builder(context).setTitle(R.string.button_text_ignore_alert_title).setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                m.a(absModel.getItemKey());
                if (BaseViewHolder.this.handler != null) {
                    BaseCardModel baseCardModel = baseCardModel;
                    if (baseCardModel instanceof ListTitleCheckboxCardModel) {
                        ((ListTitleCheckboxCardModel) baseCardModel).ignoreAbsModel(absModel);
                    }
                    ScoreManager.e().a(absModel, AbsModel.State.SAFE);
                    Message obtain = Message.obtain();
                    obtain.what = 109;
                    obtain.obj = baseCardModel;
                    BaseViewHolder.this.handler.sendMessage(obtain);
                }
            }
        }).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    /* access modifiers changed from: private */
    public void showFirstAidAlertInfoDialog(BaseCardModel baseCardModel, final AbsModel absModel, Context context) {
        this.mAlertDialog = new AlertDialog.Builder(context).setTitle(R.string.button_text_ignore_alert_title).setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                m.a(absModel.getItemKey());
                Handler firstAidEventHandler = absModel.getFirstAidEventHandler();
                if (firstAidEventHandler != null) {
                    firstAidEventHandler.sendEmptyMessage(201);
                }
            }
        }).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    public void bindData(int i, Object obj) {
    }

    public void fillData(View view, BaseCardModel baseCardModel, int i) {
        fillDefault(baseCardModel);
    }

    /* access modifiers changed from: protected */
    public void fillDefault(BaseCardModel baseCardModel) {
        String title = baseCardModel.getTitle();
        TextView textView = this.titleView;
        if (textView != null) {
            if (TextUtils.isEmpty(title)) {
                title = "";
            }
            textView.setText(title);
        }
        String summary = baseCardModel.getSummary();
        TextView textView2 = this.summaryView;
        if (textView2 != null) {
            if (TextUtils.isEmpty(summary)) {
                summary = "";
            }
            textView2.setText(summary);
        }
        String button = baseCardModel.getButton();
        Button button2 = this.tvButton;
        if (button2 != null) {
            button2.setText(TextUtils.isEmpty(button) ? "" : button);
        }
        Button button3 = this.actionButton;
        if (button3 != null) {
            if (TextUtils.isEmpty(button)) {
                button = "";
            }
            button3.setText(button);
        }
    }

    public void init(Handler handler2) {
        this.handler = handler2;
    }

    public void showFirstAidItemLongClickDialog(final BaseCardModel baseCardModel, final AbsModel absModel, final Context context) {
        this.mAlertDialog = new AlertDialog.Builder(context).setTitle(absModel.getTitle()).setSingleChoiceItems(new String[]{context.getString(R.string.button_text_ignore)}, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    dialogInterface.dismiss();
                    BaseViewHolder.this.showFirstAidAlertInfoDialog(baseCardModel, absModel, context);
                }
            }
        }).show();
    }

    public void showManualItemLongClickDialog(final BaseCardModel baseCardModel, final AbsModel absModel, final Context context) {
        this.mAlertDialog = new AlertDialog.Builder(context).setTitle(absModel.getTitle()).setSingleChoiceItems(new String[]{context.getString(R.string.button_text_ignore)}, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    dialogInterface.dismiss();
                    BaseViewHolder.this.showAlertInfoDialog(baseCardModel, absModel, context);
                }
            }
        }).show();
    }
}
