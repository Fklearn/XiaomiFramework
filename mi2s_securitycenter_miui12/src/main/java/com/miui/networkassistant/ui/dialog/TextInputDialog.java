package com.miui.networkassistant.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import b.b.c.c.b.c;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.securitycenter.R;
import miui.app.AlertDialog;

public class TextInputDialog extends c {
    private int mActionFlag;
    private String mHint;
    /* access modifiers changed from: private */
    public EditText mInpuText;
    private TextInputDialogListener mInputDialogListener;
    private boolean mIsNumberText;
    /* access modifiers changed from: private */
    public Button mOkButton;
    /* access modifiers changed from: private */
    public TextWatcher mTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable editable) {
            Button button;
            boolean z;
            if (TextUtils.isEmpty(editable.toString())) {
                button = TextInputDialog.this.mOkButton;
                z = false;
            } else {
                button = TextInputDialog.this.mOkButton;
                z = true;
            }
            button.setEnabled(z);
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    };

    public interface TextInputDialogListener {
        void onTextSetted(String str, int i);
    }

    public TextInputDialog(Activity activity, TextInputDialogListener textInputDialogListener) {
        super(activity);
        this.mInputDialogListener = textInputDialogListener;
    }

    public void buildInputDialog(int i, int i2) {
        buildInputDialog(i, i2, 0);
    }

    public void buildInputDialog(int i, int i2, int i3) {
        Resources resources = this.mActivity.getResources();
        buildInputDialog(resources.getString(i), resources.getString(i2), i3);
    }

    public void buildInputDialog(String str, String str2) {
        buildInputDialog(str, str2, 0);
    }

    public void buildInputDialog(String str, String str2, int i) {
        this.mActionFlag = i;
        clearDialog();
        this.mHint = str2;
        setTitle(str);
        showDialog();
    }

    /* access modifiers changed from: protected */
    public int getNegativeButtonText() {
        return R.string.cancel_button;
    }

    /* access modifiers changed from: protected */
    public int getPositiveButtonText() {
        return R.string.ok_button;
    }

    /* access modifiers changed from: protected */
    public void onBuild(AlertDialog alertDialog) {
        EditText editText;
        View inflate = LayoutInflater.from(this.mActivity).inflate(R.layout.dialog_text_input, (ViewGroup) null);
        this.mInpuText = (EditText) inflate.findViewById(R.id.textview_input);
        this.mInpuText.setHint(DeviceUtil.isCNLanguage() ? this.mHint : "");
        int i = 1;
        this.mInpuText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
        if (this.mIsNumberText) {
            editText = this.mInpuText;
            i = 2;
        } else {
            editText = this.mInpuText;
        }
        editText.setInputType(i);
        alertDialog.setView(inflate);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialogInterface) {
                TextInputDialog.this.mInpuText.addTextChangedListener(TextInputDialog.this.mTextWatcher);
                new Handler(TextInputDialog.this.mActivity.getMainLooper()).post(new Runnable() {
                    public void run() {
                        ((InputMethodManager) TextInputDialog.this.mActivity.getSystemService("input_method")).showSoftInput(TextInputDialog.this.mInpuText, 0);
                    }
                });
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                TextInputDialog.this.mInpuText.removeTextChangedListener(TextInputDialog.this.mTextWatcher);
            }
        });
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            this.mInputDialogListener.onTextSetted(this.mInpuText.getEditableText().toString(), this.mActionFlag);
        } else if (i == -2) {
            dialogInterface.dismiss();
        }
    }

    /* access modifiers changed from: protected */
    public void onShow(AlertDialog alertDialog) {
        this.mOkButton = alertDialog.getButton(-1);
        this.mOkButton.setEnabled(false);
    }

    public void setNumberText(boolean z) {
        this.mIsNumberText = z;
    }
}
