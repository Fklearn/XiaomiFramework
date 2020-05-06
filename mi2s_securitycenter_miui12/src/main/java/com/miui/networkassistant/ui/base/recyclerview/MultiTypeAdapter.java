package com.miui.networkassistant.ui.base.recyclerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.networkassistant.ui.base.recyclerview.BaseEntity;
import java.util.ArrayList;
import java.util.List;

public class MultiTypeAdapter<T extends BaseEntity> extends RecyclerView.a<ViewHolder> {
    protected final Context mContext;
    private final List<T> mDataList;
    /* access modifiers changed from: private */
    public OnItemClickListener mOnItemClickListener;
    private List<ItemViewType<T>> mTypeList;

    public interface OnItemClickListener {
        void onItemClick(int i);

        boolean onItemLongClick(int i);
    }

    public static class ViewHolder extends RecyclerView.u {
        public int type;

        public ViewHolder(@NonNull View view) {
            super(view);
        }
    }

    public MultiTypeAdapter(Context context) {
        this(context, (List) null);
    }

    public MultiTypeAdapter(Context context, List<T> list) {
        this.mDataList = new ArrayList();
        this.mTypeList = new ArrayList();
        this.mContext = context;
        setData(list);
    }

    private void setClickListener(final ViewHolder viewHolder) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (MultiTypeAdapter.this.mOnItemClickListener != null) {
                    MultiTypeAdapter.this.mOnItemClickListener.onItemClick(viewHolder.getAdapterPosition());
                }
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                if (MultiTypeAdapter.this.mOnItemClickListener == null) {
                    return false;
                }
                boolean onItemLongClick = MultiTypeAdapter.this.mOnItemClickListener.onItemLongClick(viewHolder.getAdapterPosition());
                if (onItemLongClick) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                }
                return onItemLongClick;
            }
        });
    }

    public void addItemViewType(ItemViewType<T> itemViewType) {
        this.mTypeList.add(itemViewType);
    }

    public List<T> getData() {
        return this.mDataList;
    }

    public final int getItemCount() {
        return getData().size();
    }

    public int getItemViewType(int i) {
        for (int i2 = 0; i2 < this.mTypeList.size(); i2++) {
            if (this.mTypeList.get(i2).checkType((BaseEntity) this.mDataList.get(i), i)) {
                return i2;
            }
        }
        return super.getItemViewType(i);
    }

    public final void onBindViewHolder(ViewHolder viewHolder, int i) {
        this.mTypeList.get(viewHolder.type).onBindViewHolder(viewHolder, (BaseEntity) this.mDataList.get(i), i);
    }

    public final ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ViewHolder onCreateViewHolder = this.mTypeList.get(i).onCreateViewHolder(viewGroup);
        onCreateViewHolder.type = i;
        setClickListener(onCreateViewHolder);
        return onCreateViewHolder;
    }

    public void setData(List<T> list) {
        this.mDataList.clear();
        if (list != null) {
            this.mDataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void setDataItem(T t, int i) {
        if (t != null && i < this.mDataList.size()) {
            this.mDataList.set(i, t);
        }
        notifyItemChanged(i);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
