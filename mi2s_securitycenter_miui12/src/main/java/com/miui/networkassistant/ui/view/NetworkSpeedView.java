package com.miui.networkassistant.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.networkassistant.utils.TypefaceHelper;
import com.miui.securitycenter.R;

public class NetworkSpeedView extends RelativeLayout {
    private static final String TAG = "NetworkSpeedView";
    private Context mContext;
    private TextView mTvNumber;
    private TextView mTvUnit;

    public NetworkSpeedView(Context context) {
        this(context, (AttributeSet) null);
    }

    public NetworkSpeedView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NetworkSpeedView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
        View.inflate(context, R.layout.network_speed_view, this);
        this.mTvNumber = (TextView) findViewById(R.id.number);
        this.mTvNumber.setTypeface(TypefaceHelper.getMiuiTypefaceForNA(this.mContext));
        this.mTvUnit = (TextView) findViewById(R.id.unit);
        this.mTvNumber.setTypeface(TypefaceHelper.getMiuiTypefaceForNA(this.mContext));
        updateNetworkSpeed(0);
    }

    public void updateNetworkSpeed(long j) {
        String[] formatSpeed = FormatBytesUtil.formatSpeed(this.mContext, j);
        if (formatSpeed != null) {
            this.mTvNumber.setText(formatSpeed[0]);
            this.mTvUnit.setText(formatSpeed[1]);
        }
        this.mTvNumber.setVisibility(0);
    }
}
