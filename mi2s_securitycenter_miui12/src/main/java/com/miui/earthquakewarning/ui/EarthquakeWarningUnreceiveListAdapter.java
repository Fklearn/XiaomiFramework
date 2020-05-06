package com.miui.earthquakewarning.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.A;
import com.miui.earthquakewarning.model.WarningModel;
import com.miui.securitycenter.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import miui.animation.Folme;
import miui.animation.base.AnimConfig;

public class EarthquakeWarningUnreceiveListAdapter extends RecyclerView.a<MyViewHolder> {
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
        TextView mAlertLevelText;
        TextView mAlertTime;

        public MyViewHolder(View view) {
            super(view);
            this.mAlertCityText = (TextView) view.findViewById(R.id.alert_city_text);
            this.mAlertLevelText = (TextView) view.findViewById(R.id.alert_level_text);
            this.mAlertTime = (TextView) view.findViewById(R.id.alert_time);
        }
    }

    public EarthquakeWarningUnreceiveListAdapter(Activity activity) {
        this.mActivity = activity;
    }

    public static long getStringToDate(String str, String str2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str2);
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
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
        final WarningModel warningModel = this.mList.get(i);
        myViewHolder.mAlertCityText.setText(warningModel.epicenter);
        myViewHolder.mAlertLevelText.setText(String.valueOf(warningModel.magnitude));
        myViewHolder.mAlertCityText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                int i;
                Resources resources;
                TextView textView;
                myViewHolder.mAlertCityText.getViewTreeObserver().removeOnPreDrawListener(this);
                if (myViewHolder.mAlertCityText.getLineCount() > 1) {
                    textView = myViewHolder.mAlertCityText;
                    resources = EarthquakeWarningUnreceiveListAdapter.this.mActivity.getResources();
                    i = R.dimen.text_font_size_36;
                } else {
                    textView = myViewHolder.mAlertCityText;
                    resources = EarthquakeWarningUnreceiveListAdapter.this.mActivity.getResources();
                    i = R.dimen.ew_alert_card_detail_text_size;
                }
                textView.setTextSize(0, (float) resources.getDimensionPixelSize(i));
                return false;
            }
        });
        myViewHolder.mAlertTime.setText(new SimpleDateFormat("yyyy-MM-dd\nHH:mm:ss").format(Long.valueOf(warningModel.startTime)));
        try {
            if (A.a()) {
                Folme.useAt(new View[]{myViewHolder.itemView}).touch().handleTouchOf(myViewHolder.itemView, new AnimConfig[0]);
            }
        } catch (Throwable unused) {
            Log.e("EwAdapter", "no support folme");
        }
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (EarthquakeWarningUnreceiveListAdapter.this.listener != null) {
                    EarthquakeWarningUnreceiveListAdapter.this.listener.onItemClick(warningModel);
                }
            }
        });
    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(this.mActivity).inflate(R.layout.earthquake_warning_item_list_unreceive, viewGroup, false));
    }

    public void setList(List<WarningModel> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void setListener(ClickListener clickListener) {
        this.listener = clickListener;
    }
}
