package com.miui.networkassistant.ui.base.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.common.stickydecoration.b.c;
import com.miui.common.stickydecoration.f;
import com.miui.networkassistant.model.WhiteListItem;
import com.miui.networkassistant.ui.base.recyclerview.MultiTypeAdapter;
import com.miui.networkassistant.ui.base.recyclerview.impl.WhiteListItemViewType;
import com.miui.networkassistant.ui.fragment.ShowAppDetailFragment;
import com.miui.securitycenter.R;
import java.util.List;

public class MultiRecycleViewHelper {
    /* access modifiers changed from: private */
    public Activity mActivity;
    /* access modifiers changed from: private */
    public MultiTypeAdapter<WhiteListItem> mAdapter;
    /* access modifiers changed from: private */
    public Context mContext;
    private RecyclerView.f mDecorationPage;
    private miuix.recyclerview.widget.RecyclerView mRecyclerView;
    /* access modifiers changed from: private */
    public int mRestrictCount = 0;

    private MultiRecycleViewHelper(Activity activity, miuix.recyclerview.widget.RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        this.mActivity = activity;
        this.mContext = recyclerView.getContext();
    }

    public static MultiRecycleViewHelper getNewInstance(Activity activity, miuix.recyclerview.widget.RecyclerView recyclerView) {
        return new MultiRecycleViewHelper(activity, recyclerView);
    }

    public void addPageDecoration(final int i) {
        final List<WhiteListItem> data = this.mAdapter.getData();
        this.mRecyclerView.b(this.mDecorationPage);
        if (data.size() != 0 && data.get(0).getGroup() != null) {
            this.mRestrictCount = 0;
            for (WhiteListItem isEnabled : data) {
                if (!isEnabled.isEnabled()) {
                    this.mRestrictCount++;
                }
            }
            this.mDecorationPage = f.a.a((c) new c() {
                public String getGroupName(int i) {
                    Resources resources;
                    int size;
                    Object[] objArr;
                    int intValue = ((Integer) ((WhiteListItem) data.get(i)).getGroup()).intValue();
                    if (intValue != i) {
                        resources = MultiRecycleViewHelper.this.mContext.getResources();
                        size = MultiRecycleViewHelper.this.mRestrictCount;
                        objArr = new Object[]{Integer.valueOf(MultiRecycleViewHelper.this.mRestrictCount)};
                    } else {
                        resources = MultiRecycleViewHelper.this.mContext.getResources();
                        size = data.size() - MultiRecycleViewHelper.this.mRestrictCount;
                        objArr = new Object[]{Integer.valueOf(data.size() - MultiRecycleViewHelper.this.mRestrictCount)};
                    }
                    return resources.getQuantityString(intValue, size, objArr);
                }

                public View getGroupView(int i) {
                    View inflate = LayoutInflater.from(MultiRecycleViewHelper.this.mContext).inflate(R.layout.listitem_group_header_view, (ViewGroup) null);
                    ((TextView) inflate.findViewById(R.id.header_title)).setText(getGroupName(i));
                    return inflate;
                }
            }).a();
            this.mRecyclerView.a(this.mDecorationPage);
        }
    }

    public List<WhiteListItem> getData() {
        return this.mAdapter.getData();
    }

    public WhiteListItem getDataItem(int i) {
        return this.mAdapter.getData().get(i);
    }

    public void init(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.mAdapter = new MultiTypeAdapter<>(this.mContext);
        this.mAdapter.addItemViewType(new WhiteListItemViewType(this.mContext, onCheckedChangeListener));
        this.mRecyclerView.setSpringEnabled(false);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mAdapter.setOnItemClickListener(new MultiTypeAdapter.OnItemClickListener() {
            public void onItemClick(int i) {
                ShowAppDetailFragment.startAppDetailFragment(MultiRecycleViewHelper.this.mActivity, ((WhiteListItem) MultiRecycleViewHelper.this.mAdapter.getData().get(i)).getPkgName());
            }

            public boolean onItemLongClick(int i) {
                return false;
            }
        });
    }

    public void notifyDataSetChanged() {
        this.mAdapter.notifyDataSetChanged();
    }

    public void setData(List<WhiteListItem> list) {
        this.mAdapter.setData(list);
    }

    public void setDataItem(WhiteListItem whiteListItem, int i) {
        this.mAdapter.setDataItem(whiteListItem, i);
    }
}
