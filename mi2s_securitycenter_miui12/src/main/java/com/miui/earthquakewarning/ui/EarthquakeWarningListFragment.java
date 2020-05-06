package com.miui.earthquakewarning.ui;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.recyclerview.widget.LinearLayoutManager;
import b.b.c.c.b.d;
import com.miui.earthquakewarning.Constants;
import com.miui.earthquakewarning.model.WarningModel;
import com.miui.earthquakewarning.ui.EarthquakeWarningListAdapter;
import com.miui.earthquakewarning.utils.Utils;
import com.miui.earthquakewarning.view.EmptyView;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miuix.recyclerview.widget.RecyclerView;

public class EarthquakeWarningListFragment extends d {
    private static final String AUTHORITY = "com.miui.earthquakewarning.EarthquakeContentProvider";
    private static final Uri EARTHQUAKE_URI = Uri.parse("content://com.miui.earthquakewarning.EarthquakeContentProvider/earthquake");
    public static final String TAG = "EarthquakeWarningListFragment";
    EarthquakeWarningListAdapter adapter;
    /* access modifiers changed from: private */
    public Context mContext;
    private EmptyView mEmptyView;
    private RecyclerView mListView;

    private void queryValue(Context context) {
        ArrayList arrayList = new ArrayList();
        Cursor query = context.getContentResolver().query(EARTHQUAKE_URI, new String[]{"_id", WarningModel.Columns.EVENTID, WarningModel.Columns.INDEX_EW, WarningModel.Columns.MAGNITUDE, WarningModel.Columns.LONGITUDE, WarningModel.Columns.LATITUDE, WarningModel.Columns.MYLONGITUDE, WarningModel.Columns.MYLATITUDE, WarningModel.Columns.EPICENTER, "startTime", WarningModel.Columns.SIGNATURE, WarningModel.Columns.DISTANCE, WarningModel.Columns.INTENSITY, WarningModel.Columns.WARNTIME}, (String) null, (String[]) null, "startTime desc");
        while (query.moveToNext()) {
            WarningModel warningModel = new WarningModel();
            warningModel.eventID = query.getInt(query.getColumnIndex(WarningModel.Columns.EVENTID));
            warningModel.index_ew = query.getInt(query.getColumnIndex(WarningModel.Columns.INDEX_EW));
            warningModel.magnitude = query.getFloat(query.getColumnIndex(WarningModel.Columns.MAGNITUDE));
            warningModel.longitude = query.getDouble(query.getColumnIndex(WarningModel.Columns.LONGITUDE));
            warningModel.latitude = query.getDouble(query.getColumnIndex(WarningModel.Columns.LATITUDE));
            warningModel.myLongitude = query.getDouble(query.getColumnIndex(WarningModel.Columns.MYLONGITUDE));
            warningModel.myLatitude = query.getDouble(query.getColumnIndex(WarningModel.Columns.MYLATITUDE));
            warningModel.epicenter = query.getString(query.getColumnIndex(WarningModel.Columns.EPICENTER));
            warningModel.startTime = query.getLong(query.getColumnIndex("startTime"));
            warningModel.signature = query.getString(query.getColumnIndex(WarningModel.Columns.SIGNATURE));
            warningModel.distance = query.getDouble(query.getColumnIndex(WarningModel.Columns.DISTANCE));
            warningModel.intensity = query.getFloat(query.getColumnIndex(WarningModel.Columns.INTENSITY));
            warningModel.warnTime = query.getInt(query.getColumnIndex(WarningModel.Columns.WARNTIME));
            arrayList.add(warningModel);
        }
        this.adapter.setList(arrayList);
        setEmptyView(arrayList);
    }

    private void setEmptyView(List<WarningModel> list) {
        int i;
        EmptyView emptyView;
        if (list == null || list.size() == 0) {
            emptyView = this.mEmptyView;
            i = 0;
        } else {
            emptyView = this.mEmptyView;
            i = 8;
        }
        emptyView.setVisibility(i);
        this.adapter.setList(list);
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.mListView = (RecyclerView) findViewById(R.id.listview);
        this.mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        this.adapter = new EarthquakeWarningListAdapter(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.mContext);
        linearLayoutManager.j(1);
        this.mListView.setLayoutManager(linearLayoutManager);
        this.mListView.setAdapter(this.adapter);
        this.adapter.setListener(new EarthquakeWarningListAdapter.ClickListener() {
            public void onItemClick(WarningModel warningModel) {
                if (Utils.supportMap(EarthquakeWarningListFragment.this.mContext)) {
                    try {
                        Intent intent = new Intent("com.miui.earthquake.detail");
                        Bundle bundle = new Bundle();
                        bundle.putFloat(WarningModel.Columns.MAGNITUDE, warningModel.magnitude);
                        bundle.putDouble(WarningModel.Columns.LONGITUDE, warningModel.longitude);
                        bundle.putDouble(WarningModel.Columns.LATITUDE, warningModel.latitude);
                        bundle.putDouble(WarningModel.Columns.DISTANCE, warningModel.distance);
                        bundle.putDouble(WarningModel.Columns.MYLONGITUDE, warningModel.myLongitude);
                        bundle.putDouble(WarningModel.Columns.MYLATITUDE, warningModel.myLatitude);
                        bundle.putFloat(WarningModel.Columns.INTENSITY, warningModel.intensity);
                        bundle.putString(WarningModel.Columns.EPICENTER, warningModel.epicenter);
                        bundle.putLong("startTime", warningModel.startTime);
                        bundle.putInt("warnTime", warningModel.warnTime);
                        bundle.putBoolean("isAll", false);
                        if (!TextUtils.isEmpty(warningModel.signature)) {
                            bundle.putString(WarningModel.Columns.SIGNATURE, EarthquakeWarningListFragment.this.getResources().getString(R.string.ew_alert_text_from, new Object[]{warningModel.signature}));
                        }
                        intent.putExtras(bundle);
                        intent.setPackage(Constants.SECURITY_ADD_PACKAGE);
                        EarthquakeWarningListFragment.this.startActivity(intent);
                    } catch (Exception unused) {
                        Log.e(EarthquakeWarningListFragment.TAG, "can not find detail page");
                    }
                }
            }
        });
        queryValue(getContext());
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getContext();
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.earthquake_warning_fragment_list;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroyView() {
        super.onDestroyView();
        EmptyView emptyView = this.mEmptyView;
        if (emptyView != null) {
            emptyView.onDestroy();
        }
    }

    public void onPause() {
        super.onPause();
        EmptyView emptyView = this.mEmptyView;
        if (emptyView != null) {
            emptyView.onPause();
        }
    }

    public void onResume() {
        super.onResume();
        EmptyView emptyView = this.mEmptyView;
        if (emptyView != null) {
            emptyView.onResume();
        }
    }
}
