package com.miui.earthquakewarning.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.A;
import com.miui.earthquakewarning.model.WarningModel;
import com.miui.securitycenter.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import miui.animation.Folme;
import miui.animation.base.AnimConfig;

public class EarthquakeWarningListAdapter extends RecyclerView.a<MyViewHolder> {
    /* access modifiers changed from: private */
    public ClickListener listener;
    /* access modifiers changed from: private */
    public Activity mActivity;
    private LayoutInflater mInflater;
    private List<WarningModel> mList = new ArrayList();

    public interface ClickListener {
        void onItemClick(WarningModel warningModel);
    }

    public static class MyViewHolder extends RecyclerView.u {
        TextView mAlertCityText;
        TextView mAlertFeelText;
        TextView mAlertIntensity;
        TextView mAlertLevelText;
        TextView mAlertTime;
        LinearLayout mContainer;
        TextView mDistanceText;

        public MyViewHolder(View view) {
            super(view);
            this.mContainer = (LinearLayout) view.findViewById(R.id.ll_container);
            this.mDistanceText = (TextView) view.findViewById(R.id.alert_distance);
            this.mAlertCityText = (TextView) view.findViewById(R.id.alert_city_text);
            this.mAlertLevelText = (TextView) view.findViewById(R.id.alert_level_text);
            this.mAlertFeelText = (TextView) view.findViewById(R.id.alert_feel_text);
            this.mAlertIntensity = (TextView) view.findViewById(R.id.alert_intensity);
            this.mAlertTime = (TextView) view.findViewById(R.id.alert_time);
        }
    }

    public EarthquakeWarningListAdapter(Activity activity) {
        this.mActivity = activity;
        this.mInflater = activity.getLayoutInflater();
    }

    public int getItemCount() {
        List<WarningModel> list = this.mList;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        LinearLayout linearLayout;
        int i2;
        final WarningModel warningModel = this.mList.get(i);
        String valueOf = String.valueOf(Math.round(warningModel.distance));
        myViewHolder.mDistanceText.setText(this.mActivity.getResources().getString(R.string.ew_list_receive_distance, new Object[]{valueOf}));
        myViewHolder.mAlertCityText.setText(warningModel.epicenter);
        myViewHolder.mAlertLevelText.setText(String.valueOf(warningModel.magnitude));
        myViewHolder.mAlertIntensity.setText(String.format("%.1f", new Object[]{Float.valueOf(warningModel.intensity)}));
        myViewHolder.mAlertCityText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                int i;
                Resources resources;
                TextView textView;
                myViewHolder.mAlertCityText.getViewTreeObserver().removeOnPreDrawListener(this);
                if (myViewHolder.mAlertCityText.getLineCount() > 1) {
                    textView = myViewHolder.mAlertCityText;
                    resources = EarthquakeWarningListAdapter.this.mActivity.getResources();
                    i = R.dimen.text_font_size_36;
                } else {
                    textView = myViewHolder.mAlertCityText;
                    resources = EarthquakeWarningListAdapter.this.mActivity.getResources();
                    i = R.dimen.ew_alert_card_detail_text_size;
                }
                textView.setTextSize(0, (float) resources.getDimensionPixelSize(i));
                return false;
            }
        });
        try {
            if (A.a()) {
                Folme.useAt(new View[]{myViewHolder.itemView}).touch().handleTouchOf(myViewHolder.itemView, new AnimConfig[0]);
            }
        } catch (Throwable unused) {
            Log.e("EwAdapter", "no support folme");
        }
        float f = warningModel.intensity;
        if (f - 0.0f < 0.001f) {
            myViewHolder.mAlertFeelText.setText(this.mActivity.getResources().getString(R.string.ew_alert_earthquake_feel_none));
            linearLayout = myViewHolder.mContainer;
            i2 = R.drawable.ew_list_card_none;
        } else if (f < 2.0f) {
            myViewHolder.mAlertFeelText.setText(this.mActivity.getResources().getString(R.string.ew_alert_earthquake_feel_little));
            linearLayout = myViewHolder.mContainer;
            i2 = R.drawable.ew_list_card_little;
        } else if (f < 3.0f) {
            myViewHolder.mAlertFeelText.setText(this.mActivity.getResources().getString(R.string.ew_alert_earthquake_feel_normal));
            linearLayout = myViewHolder.mContainer;
            i2 = R.drawable.ew_list_card_low;
        } else if (f < 5.0f) {
            myViewHolder.mAlertFeelText.setText(this.mActivity.getResources().getString(R.string.ew_alert_earthquake_feel_middle));
            linearLayout = myViewHolder.mContainer;
            i2 = R.drawable.ew_list_card_strong;
        } else {
            myViewHolder.mAlertFeelText.setText(this.mActivity.getResources().getString(R.string.ew_alert_earthquake_feel_max));
            linearLayout = myViewHolder.mContainer;
            i2 = R.drawable.ew_list_card_max;
        }
        linearLayout.setBackgroundResource(i2);
        myViewHolder.mAlertFeelText.setSelected(true);
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(warningModel.startTime));
        myViewHolder.mAlertTime.setText(this.mActivity.getResources().getString(R.string.ew_list_receive_time, new Object[]{format}));
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (EarthquakeWarningListAdapter.this.listener != null) {
                    EarthquakeWarningListAdapter.this.listener.onItemClick(warningModel);
                }
            }
        });
    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(this.mActivity).inflate(R.layout.earthquake_warning_item_list, viewGroup, false));
    }

    public void setList(List<WarningModel> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void setListener(ClickListener clickListener) {
        this.listener = clickListener;
    }
}
