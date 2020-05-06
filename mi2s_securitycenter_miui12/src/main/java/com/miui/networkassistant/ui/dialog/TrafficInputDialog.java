package com.miui.networkassistant.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import b.b.c.c.b.c;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.securitycenter.R;
import java.util.regex.Pattern;
import miui.app.AlertDialog;

public class TrafficInputDialog extends c implements AdapterView.OnItemSelectedListener {
    private int mActionFlag;
    private String mHint;
    /* access modifiers changed from: private */
    public EditText mInpuText;
    private TrafficInputDialogListener mInputDialogListener;
    private long mInputTraffic;
    /* access modifiers changed from: private */
    public TextWatcher mInputWatcher = new TextWatcher() {
        public void afterTextChanged(Editable editable) {
            String obj = editable.toString();
            if (obj.indexOf(".") == 0) {
                editable.delete(0, 1);
            }
            if (TrafficInputDialog.this.mPattern.matcher(obj).find()) {
                TrafficInputDialog.this.mOkButton.setEnabled(true);
                TrafficInputDialog.this.setProperInputValue(obj);
                return;
            }
            TrafficInputDialog.this.mOkButton.setEnabled(false);
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    };
    private long mMaxValue = 0;
    /* access modifiers changed from: private */
    public Button mOkButton;
    /* access modifiers changed from: private */
    public Pattern mPattern = Pattern.compile("^\\d+\\.?\\d*$");
    private double mProperValue;
    private long mTrafficUnits;

    public interface TrafficInputDialogListener {
        void onTrafficUpdated(long j, int i);
    }

    public TrafficInputDialog(Activity activity, TrafficInputDialogListener trafficInputDialogListener) {
        super(activity);
        this.mInputDialogListener = trafficInputDialogListener;
    }

    private void initView(View view) {
        this.mInpuText = (EditText) view.findViewById(R.id.textview_input);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_units);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this.mActivity, 17367048, FormatBytesUtil.trafficUnitArray(this.mActivity));
        arrayAdapter.setDropDownViewResource(miui.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(0);
    }

    /* access modifiers changed from: private */
    public void setProperInputValue(String str) {
        Object[] objArr;
        String str2;
        if (!TextUtils.isEmpty(str)) {
            this.mOkButton.setEnabled(true);
            long parseDouble = (long) (Double.parseDouble(str) * ((double) this.mTrafficUnits));
            long j = this.mMaxValue;
            if (j > 0 && j < parseDouble) {
                double d2 = this.mProperValue;
                if (d2 < 1.0d) {
                    objArr = new Object[]{Double.valueOf((Math.floor(d2 * 100.0d) * 1.0d) / 100.0d)};
                    str2 = "%.02f";
                } else {
                    objArr = new Object[]{Integer.valueOf((int) d2)};
                    str2 = "%d";
                }
                String format = String.format(str2, objArr);
                this.mInpuText.setText(format);
                Selection.setSelection(this.mInpuText.getEditableText(), format.length());
            }
        }
    }

    public void buildInputDialog(String str, String str2) {
        buildInputDialog(str, str2, 0);
    }

    public void buildInputDialog(String str, String str2, int i) {
        this.mActionFlag = i;
        this.mHint = str2;
        this.mMaxValue = 0;
        setTitle(str);
        showDialog();
        refreshHintTxt(str2);
    }

    public void clearInputText() {
        EditText editText = this.mInpuText;
        if (editText != null) {
            editText.setText("");
        }
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
        View inflate = LayoutInflater.from(this.mActivity).inflate(R.layout.view_traffic_input, (ViewGroup) null);
        initView(inflate);
        this.mInpuText.setHint(DeviceUtil.isCNLanguage() ? this.mHint : "");
        this.mInpuText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        alertDialog.setView(inflate);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialogInterface) {
                TrafficInputDialog.this.mInpuText.addTextChangedListener(TrafficInputDialog.this.mInputWatcher);
                new Handler(TrafficInputDialog.this.mActivity.getMainLooper()).post(new Runnable() {
                    public void run() {
                        ((InputMethodManager) TrafficInputDialog.this.mActivity.getSystemService("input_method")).showSoftInput(TrafficInputDialog.this.mInpuText, 0);
                    }
                });
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                TrafficInputDialog.this.mInpuText.removeTextChangedListener(TrafficInputDialog.this.mInputWatcher);
            }
        });
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            String trim = this.mInpuText.getEditableText().toString().trim();
            if (!TextUtils.isEmpty(trim)) {
                this.mInputTraffic = Math.round(Double.parseDouble(trim) * ((double) this.mTrafficUnits));
                dialogInterface.dismiss();
                this.mInputDialogListener.onTrafficUpdated(this.mInputTraffic, this.mActionFlag);
            }
        }
        dialogInterface.dismiss();
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        double d2;
        double d3;
        ((TextView) view).setGravity(1);
        if (i != 0) {
            if (i == 1) {
                this.mTrafficUnits = 1073741824;
                d3 = ((double) this.mMaxValue) * 1.0d;
                d2 = 1.073741824E9d;
            }
            setProperInputValue(this.mInpuText.getEditableText().toString());
        }
        this.mTrafficUnits = 1048576;
        d3 = ((double) this.mMaxValue) * 1.0d;
        d2 = 1048576.0d;
        this.mProperValue = d3 / d2;
        setProperInputValue(this.mInpuText.getEditableText().toString());
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /* access modifiers changed from: protected */
    public void onShow(AlertDialog alertDialog) {
        this.mOkButton = alertDialog.getButton(-1);
        this.mOkButton.setEnabled(false);
    }

    public void refreshHintTxt(String str) {
        EditText editText = this.mInpuText;
        if (editText != null) {
            if (!DeviceUtil.isCNLanguage()) {
                str = "";
            }
            editText.setHint(str);
        }
    }

    public void setMaxValue(long j) {
        this.mMaxValue = j;
    }
}
