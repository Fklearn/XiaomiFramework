package com.miui.networkassistant.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class ScanningBar extends LinearLayout implements View.OnClickListener {
    private static final int SCANNING_RET_FAILED = 2;
    private static final int SCANNING_RET_SUCCESSED = 1;
    private static final int SCANNING_RET_UNKNOWN = 0;
    private ScanningBarAdapter mListAdapter;
    private ListView mScanningListView;
    /* access modifiers changed from: private */
    public List<Integer> mScanningRetList;
    private List<String> mScanningTextList;

    class ScanningBarAdapter extends BaseAdapter {
        private Context mContext;
        protected LayoutInflater mInflater = LayoutInflater.from(this.mContext);
        private List<String> mItems;

        public ScanningBarAdapter(Context context, List<String> list) {
            this.mContext = context;
            this.mItems = list;
        }

        public void bindView(View view, Context context, int i) {
            List<String> list;
            View view2;
            if (view != null && context != null && (list = this.mItems) != null && list.size() > i) {
                ((TextView) view.findViewById(R.id.scanning_item_name)).setText(this.mItems.get(i));
                int intValue = ((Integer) ScanningBar.this.mScanningRetList.get(i)).intValue();
                if (intValue == 0) {
                    view.findViewById(R.id.scanning_item_waitting).setVisibility(0);
                    view2 = view.findViewById(R.id.scanning_item_success);
                } else if (intValue == 1) {
                    view.findViewById(R.id.scanning_item_waitting).setVisibility(8);
                    view.findViewById(R.id.scanning_item_success).setVisibility(0);
                    return;
                } else if (intValue == 2) {
                    view2 = view.findViewById(R.id.scanning_item_waitting);
                } else {
                    return;
                }
                view2.setVisibility(8);
            }
        }

        public int getCount() {
            return this.mItems.size();
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = newView(this.mContext, viewGroup);
            }
            bindView(view, this.mContext, i);
            return view;
        }

        public View newView(Context context, ViewGroup viewGroup) {
            if (this.mContext != null) {
                return this.mInflater.inflate(R.layout.listitem_network_diagnostics_scanning, (ViewGroup) null);
            }
            return null;
        }
    }

    public ScanningBar(Context context) {
        this(context, (AttributeSet) null);
    }

    public ScanningBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ScanningBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void onClick(View view) {
        int id = view.getId();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mScanningTextList = new ArrayList();
        this.mScanningRetList = new ArrayList();
        this.mScanningListView = (ListView) findViewById(R.id.scanning_system_text);
        this.mListAdapter = new ScanningBarAdapter(getContext(), this.mScanningTextList);
        this.mScanningListView.setAdapter(this.mListAdapter);
    }

    public void resetScanningBar() {
        this.mScanningTextList.clear();
        this.mScanningRetList.clear();
        this.mScanningListView.invalidate();
        this.mListAdapter.notifyDataSetChanged();
    }

    public void setScanningItems(List<String> list) {
        this.mScanningTextList.clear();
        this.mScanningTextList.addAll(list);
        this.mScanningRetList.clear();
        for (int i = 0; i < this.mScanningTextList.size(); i++) {
            this.mScanningRetList.add(0);
        }
        this.mScanningListView.smoothScrollToPosition(0);
        this.mListAdapter.notifyDataSetChanged();
    }

    public void setScanningRet(int i, boolean z) {
        ListView listView;
        List<Integer> list;
        int i2;
        if (i < this.mScanningRetList.size()) {
            if (z) {
                list = this.mScanningRetList;
                i2 = 1;
            } else {
                list = this.mScanningRetList;
                i2 = 2;
            }
            list.set(i, i2);
        }
        if (i < this.mScanningTextList.size() - 1) {
            listView = this.mScanningListView;
            i++;
        } else {
            listView = this.mScanningListView;
        }
        listView.smoothScrollToPosition(i);
        this.mListAdapter.notifyDataSetChanged();
    }
}
