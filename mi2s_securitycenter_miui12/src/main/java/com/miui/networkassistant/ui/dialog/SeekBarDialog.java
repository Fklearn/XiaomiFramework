package com.miui.networkassistant.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import b.b.c.c.b.c;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.securitycenter.R;
import java.text.NumberFormat;
import miui.app.AlertDialog;

public class SeekBarDialog extends c {
    private static final int SEEK_BAR_RANGE_MAX = 100;
    private static final int SEEK_BAR_RANGE_MIN = 60;
    private Activity mActivity;
    private long mMonthTotal;
    private float mPercent;
    private TextView mPercentTextView;
    /* access modifiers changed from: private */
    public int mRealValue;
    private SeekBar mSeekBar;
    /* access modifiers changed from: private */
    public SeekBarChangeListener mSeekBarChangeListener = null;
    private TextView mWarnTraffic;

    public interface SeekBarChangeListener {
        void onSeekBarChanged(float f);
    }

    public SeekBarDialog(Activity activity, SeekBarChangeListener seekBarChangeListener) {
        super(activity);
        this.mActivity = activity;
        this.mSeekBarChangeListener = seekBarChangeListener;
    }

    /* access modifiers changed from: private */
    public int getReallySeekBarValue(int i) {
        return ((i * 40) / 100) + 60;
    }

    private void initView(View view) {
        this.mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        this.mPercentTextView = (TextView) view.findViewById(R.id.textview_precent);
        this.mWarnTraffic = (TextView) view.findViewById(R.id.textview_warn_traffic);
        ((TextView) view.findViewById(R.id.left_precent)).setText(NumberFormat.getPercentInstance().format(0.6d));
        ((TextView) view.findViewById(R.id.right_precent)).setText(NumberFormat.getPercentInstance().format(1));
        SeekBar seekBar = this.mSeekBar;
        if (seekBar != null) {
            seekBar.setMax(100);
            this.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    if (z) {
                        SeekBarDialog seekBarDialog = SeekBarDialog.this;
                        seekBarDialog.updateData(seekBarDialog.getReallySeekBarValue(i));
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (SeekBarDialog.this.mSeekBarChangeListener != null) {
                        SeekBarDialog seekBarDialog = SeekBarDialog.this;
                        int unused = seekBarDialog.mRealValue = seekBarDialog.getReallySeekBarValue(seekBar.getProgress());
                    }
                }
            });
        }
    }

    private void loadData() {
        this.mRealValue = (int) (this.mPercent * 100.0f);
        updateData(this.mRealValue);
        SeekBar seekBar = this.mSeekBar;
        if (seekBar != null) {
            seekBar.setProgress((int) ((((this.mPercent * 100.0f) - 60.0f) / 40.0f) * 100.0f));
        }
    }

    /* access modifiers changed from: private */
    public void updateData(int i) {
        if (this.mPercentTextView != null && this.mWarnTraffic != null) {
            this.mPercentTextView.setText(NumberFormat.getPercentInstance().format((double) (((float) i) / 100.0f)));
            this.mWarnTraffic.setText(FormatBytesUtil.formatBytes(this.mActivity, (this.mMonthTotal * ((long) i)) / 100));
        }
    }

    public void buildDateDialog(String str) {
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
        View inflate = LayoutInflater.from(this.mActivity).inflate(R.layout.pc_seekbar_preference_ms, (ViewGroup) null);
        alertDialog.setView(inflate);
        initView(inflate);
    }

    /* access modifiers changed from: protected */
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            this.mSeekBarChangeListener.onSeekBarChanged((float) (((double) this.mRealValue) / 100.0d));
        }
        dialogInterface.dismiss();
    }

    /* access modifiers changed from: protected */
    public void onShow(AlertDialog alertDialog) {
    }

    public void setData(long j, float f) {
        this.mMonthTotal = j;
        this.mPercent = f;
        loadData();
    }
}
