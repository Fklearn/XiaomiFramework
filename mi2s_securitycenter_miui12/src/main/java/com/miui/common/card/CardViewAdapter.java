package com.miui.common.card;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.miui.common.card.models.BaseCardModel;
import com.miui.common.card.models.FunctionCardModel;
import com.miui.common.card.models.LineCardModel;
import com.miui.securityscan.a.G;
import com.miui.securityscan.cards.n;
import java.util.ArrayList;

public class CardViewAdapter extends ArrayAdapter<BaseCardModel> {
    public static final int PAGE_INDEX_HOMEPAGE = 0;
    public static final int PAGE_INDEX_PHONEMANAGE = 3;
    public static final int PAGE_INDEX_RESULTPAGE = 1;
    public static final int PAGE_INDEX_RESULTPAGE_FIRSTAIDKIT = 2;
    protected boolean canAutoScroll;
    private Context context;
    private boolean defaultStatShow;
    private Handler handler;
    private LayoutInflater inflater;
    private n menuBinder;
    private ArrayList<BaseCardModel> modelList;
    private int pageIndex;

    public CardViewAdapter(Context context2, Handler handler2, int i) {
        this(context2, new ArrayList(), handler2, i);
    }

    public CardViewAdapter(Context context2, ArrayList<BaseCardModel> arrayList, Handler handler2, int i) {
        super(context2, 0, arrayList);
        this.defaultStatShow = true;
        init(context2, i);
        this.context = context2;
        this.modelList = arrayList;
        this.handler = handler2;
        this.pageIndex = i;
    }

    private void init(Context context2, int i) {
        if (i == 0 || 3 == i) {
            this.menuBinder = new n(context2);
        }
        this.inflater = LayoutInflater.from(context2);
    }

    private void statEvent(BaseCardModel baseCardModel, Context context2) {
        if (baseCardModel != null) {
            int i = this.pageIndex;
            if (i != 0) {
                if (i == 1) {
                    G.b(baseCardModel);
                } else if (i == 2) {
                    G.a(baseCardModel);
                }
            } else if (this.defaultStatShow) {
                G.a(context2, baseCardModel);
            }
        }
    }

    public int getCount() {
        return this.modelList.size();
    }

    public int getItemViewType(int i) {
        return this.modelList.get(i).getLayoutIdType();
    }

    public ArrayList<BaseCardModel> getModelList() {
        return this.modelList;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2;
        BaseViewHolder baseViewHolder;
        BaseCardModel baseCardModel = this.modelList.get(i);
        baseCardModel.canRrfreshFunctStatus = false;
        baseCardModel.setDefaultStatShow(this.defaultStatShow);
        statEvent(baseCardModel, this.context);
        if (baseCardModel.noConvertView) {
            view2 = this.inflater.inflate(baseCardModel.getLayoutId(), viewGroup, false);
            BaseViewHolder createViewHolder = baseCardModel.createViewHolder(view2);
            createViewHolder.init(this.handler);
            createViewHolder.fillData(view2, baseCardModel, i);
        } else {
            if (view == null) {
                view = this.inflater.inflate(baseCardModel.getLayoutId(), viewGroup, false);
                baseViewHolder = baseCardModel.createViewHolder(view);
                baseViewHolder.init(this.handler);
                view.setTag(baseViewHolder);
            } else {
                baseViewHolder = (BaseViewHolder) view.getTag();
            }
            baseViewHolder.bindData(i, this.menuBinder);
            int i2 = i + 1;
            if (((i2 < this.modelList.size() && (this.modelList.get(i2) instanceof LineCardModel)) || i == this.modelList.size() - 1) && (baseCardModel instanceof FunctionCardModel)) {
                ((FunctionCardModel) baseCardModel).setNoDivider(true);
            }
            baseCardModel.setCanAutoScroll(this.canAutoScroll);
            baseViewHolder.fillData(view, baseCardModel, i);
            view2 = null;
        }
        return baseCardModel.noConvertView ? view2 : view;
    }

    public int getViewTypeCount() {
        return BaseCardModel.getLayoutTypeCount();
    }

    public boolean isCanAutoScroll() {
        return this.canAutoScroll;
    }

    public void notifyAppManagerMenuChangeListener() {
        for (n.c onAppManagerChange : this.menuBinder.v) {
            n nVar = this.menuBinder;
            nVar.o = true;
            onAppManagerChange.onAppManagerChange(nVar.o);
        }
    }

    public void notifyDataSetChanged() {
        this.defaultStatShow = true;
        super.notifyDataSetChanged();
    }

    public void notifyDataSetChanged(boolean z) {
        this.defaultStatShow = z;
        super.notifyDataSetChanged();
    }

    public void onDestroy() {
        n nVar = this.menuBinder;
        if (nVar != null) {
            nVar.c();
        }
    }

    public void resetViewPager() {
        n nVar = this.menuBinder;
        if (nVar != null) {
            nVar.d();
        }
    }

    public void setCanAutoScroll(boolean z) {
        this.canAutoScroll = z;
    }

    public void setDefaultStatShow(boolean z) {
        this.defaultStatShow = z;
    }

    public void setModelList(ArrayList<BaseCardModel> arrayList) {
        this.modelList = arrayList;
    }
}
