package com.miui.earthquakewarning.ui;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.recyclerview.widget.LinearLayoutManager;
import b.b.c.c.b.d;
import com.miui.earthquakewarning.Constants;
import com.miui.earthquakewarning.model.WarningModel;
import com.miui.earthquakewarning.model.WarningResult;
import com.miui.earthquakewarning.service.RequestWarningListTask;
import com.miui.earthquakewarning.ui.EarthquakeWarningUnreceiveListAdapter;
import com.miui.earthquakewarning.utils.Utils;
import com.miui.earthquakewarning.view.EmptyView;
import com.miui.securitycenter.R;
import java.util.List;
import miuix.recyclerview.widget.RecyclerView;

public class EarthquakeWarningUnreceiveListFragment extends d {
    public static final String TAG = "UnreceiveListFragment";
    private EarthquakeWarningUnreceiveListAdapter adapter;
    /* access modifiers changed from: private */
    public Context mContext;
    private EmptyView mEmptyView;
    private RecyclerView mListView;

    /* access modifiers changed from: private */
    public void queryValue(List<WarningModel> list) {
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
        this.adapter = new EarthquakeWarningUnreceiveListAdapter(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.mContext);
        linearLayoutManager.j(1);
        this.mListView.setLayoutManager(linearLayoutManager);
        this.mListView.setAdapter(this.adapter);
        this.adapter.setListener(new EarthquakeWarningUnreceiveListAdapter.ClickListener() {
            public void onItemClick(WarningModel warningModel) {
                if (Utils.supportMap(EarthquakeWarningUnreceiveListFragment.this.mContext)) {
                    try {
                        Intent intent = new Intent("com.miui.earthquake.detail");
                        Bundle bundle = new Bundle();
                        bundle.putFloat(WarningModel.Columns.MAGNITUDE, warningModel.magnitude);
                        bundle.putDouble(WarningModel.Columns.LONGITUDE, warningModel.longitude);
                        bundle.putDouble(WarningModel.Columns.LATITUDE, warningModel.latitude);
                        bundle.putDouble(WarningModel.Columns.DISTANCE, warningModel.distance);
                        bundle.putFloat(WarningModel.Columns.INTENSITY, warningModel.intensity);
                        bundle.putString(WarningModel.Columns.EPICENTER, warningModel.epicenter);
                        bundle.putLong("startTime", warningModel.startTime);
                        bundle.putBoolean("isAll", true);
                        intent.putExtras(bundle);
                        intent.setPackage(Constants.SECURITY_ADD_PACKAGE);
                        EarthquakeWarningUnreceiveListFragment.this.startActivity(intent);
                    } catch (Exception unused) {
                        Log.e(EarthquakeWarningUnreceiveListFragment.TAG, "can not find detail page");
                    }
                }
            }
        });
        RequestWarningListTask requestWarningListTask = new RequestWarningListTask();
        requestWarningListTask.setListener(new RequestWarningListTask.Listener() {
            public void onPost(WarningResult warningResult) {
                if (warningResult != null && warningResult.getCode() == 0 && warningResult.getData() != null) {
                    EarthquakeWarningUnreceiveListFragment.this.queryValue(warningResult.getData());
                }
            }
        });
        requestWarningListTask.execute(new String[0]);
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
