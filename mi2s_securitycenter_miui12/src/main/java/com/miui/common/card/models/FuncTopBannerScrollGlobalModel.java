package com.miui.common.card.models;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.functions.FuncTopBannerScrollData;
import com.miui.common.customview.AutoScrollViewPager;
import com.miui.privacyapps.view.ViewPagerIndicator;
import com.miui.securitycenter.R;
import com.miui.securityscan.a.G;
import com.miui.securityscan.cards.n;
import com.miui.securityscan.model.AbsModel;
import java.util.List;

public class FuncTopBannerScrollGlobalModel extends FunctionCardModel {

    public class FuncTopBannerGlobalScrollHolder extends BaseViewHolder implements ViewPager.OnPageChangeListener {
        MyPagerAdapter adapter;
        Context context;
        FuncTopBannerScrollGlobalModel funcTopBannerScrollGlobalModel;
        int indicatorMarginBottom = this.context.getResources().getDimensionPixelSize(R.dimen.security_center_indicator_margin_bottom);
        int indicatorMarginLeft = this.context.getResources().getDimensionPixelSize(R.dimen.main_indicator_marginLeft);
        int indicatorMarginTop = this.context.getResources().getDimensionPixelSize(R.dimen.security_center_indicator_maigin_top);
        int indicatorNormalColor = this.context.getResources().getColor(R.color.main_indicator_normal_color);
        int indicatorSelectedColor = this.context.getResources().getColor(R.color.main_indicator_selected_color);
        int indicatorWidth = this.context.getResources().getDimensionPixelSize(R.dimen.main_indicator_with);
        n menuFuncBinder;
        AutoScrollViewPager viewPager;
        ViewPagerIndicator viewPagerIndicator;

        public FuncTopBannerGlobalScrollHolder(View view) {
            super(view);
            this.context = view.getContext();
            this.viewPager = (AutoScrollViewPager) view.findViewById(R.id.view_pager);
            this.viewPagerIndicator = (ViewPagerIndicator) view.findViewById(R.id.indicator);
            this.viewPagerIndicator.setCycle(true);
            this.adapter = new MyPagerAdapter(this.context);
            this.viewPager.setAdapter(this.adapter);
            this.viewPager.setInterval(4000);
            this.viewPager.setBorderAnimation(false);
            this.viewPager.setPageMargin(this.context.getResources().getDimensionPixelSize(R.dimen.card_layout_line_height));
            ViewPagerIndicator viewPagerIndicator2 = this.viewPagerIndicator;
            int i = this.indicatorMarginLeft;
            viewPagerIndicator2.a(i, this.indicatorMarginTop, i, this.indicatorMarginBottom);
            ViewPagerIndicator viewPagerIndicator3 = this.viewPagerIndicator;
            int i2 = this.indicatorWidth;
            viewPagerIndicator3.a(1, i2, i2, this.indicatorNormalColor, this.indicatorSelectedColor);
        }

        public void bindData(int i, Object obj) {
            if (obj != null && (obj instanceof n)) {
                this.menuFuncBinder = (n) obj;
            }
        }

        public void fillData(View view, BaseCardModel baseCardModel, int i) {
            if (baseCardModel instanceof FuncTopBannerScrollGlobalModel) {
                this.funcTopBannerScrollGlobalModel = (FuncTopBannerScrollGlobalModel) baseCardModel;
                List<FuncTopBannerScrollData> funcTopBannerScrollDataList = this.funcTopBannerScrollGlobalModel.getFuncTopBannerScrollDataList();
                if (funcTopBannerScrollDataList != null && !funcTopBannerScrollDataList.isEmpty()) {
                    int size = funcTopBannerScrollDataList.size();
                    this.adapter.setFuncTopBannerScrollDataList(funcTopBannerScrollDataList);
                    this.adapter.notifyDataSetChanged();
                    int count = this.adapter.getCount();
                    if (count > 1) {
                        count = ((count / 2) / size) * size;
                    }
                    int currentIndex = this.funcTopBannerScrollGlobalModel.getCurrentIndex();
                    if (currentIndex != -1) {
                        count = currentIndex;
                    }
                    this.viewPagerIndicator.a(size, count);
                    this.viewPager.setOnPageChangeListener(this);
                    this.viewPager.setCurrentItem(count);
                    this.viewPager.c();
                    if (this.funcTopBannerScrollGlobalModel.isCanAutoScroll()) {
                        this.viewPager.a(2000);
                    }
                    n nVar = this.menuFuncBinder;
                    if (nVar != null) {
                        nVar.a(this.viewPager);
                    }
                }
            }
        }

        public void onPageScrollStateChanged(int i) {
        }

        public void onPageScrolled(int i, float f, int i2) {
        }

        public void onPageSelected(int i) {
            int size;
            int size2;
            this.viewPagerIndicator.setSelected(i);
            FuncTopBannerScrollGlobalModel funcTopBannerScrollGlobalModel2 = this.funcTopBannerScrollGlobalModel;
            if (funcTopBannerScrollGlobalModel2 != null) {
                funcTopBannerScrollGlobalModel2.setCurrentIndex(i);
                List<FuncTopBannerScrollData> funcTopBannerScrollDataList = this.funcTopBannerScrollGlobalModel.getFuncTopBannerScrollDataList();
                if (funcTopBannerScrollDataList != null && !funcTopBannerScrollDataList.isEmpty() && (size2 = i % (size = funcTopBannerScrollDataList.size())) < size) {
                    FuncTopBannerScrollData funcTopBannerScrollData = funcTopBannerScrollDataList.get(size2);
                    G.a(this.context, funcTopBannerScrollData);
                    Log.d("FuncTopBannerScrollGlobalModel show ", funcTopBannerScrollData.getTitle() + " " + funcTopBannerScrollData.getStatKey());
                }
            }
        }
    }

    class MyPagerAdapter extends PagerAdapter {
        private Context context;
        private List<FuncTopBannerScrollData> funcTopBannerScrollDataList;
        private LayoutInflater mInflater;

        public MyPagerAdapter(Context context2) {
            this.mInflater = LayoutInflater.from(context2);
            this.context = context2;
        }

        /* access modifiers changed from: private */
        public void statClick(FuncTopBannerScrollData funcTopBannerScrollData) {
            String statKey = funcTopBannerScrollData.getStatKey();
            if (!TextUtils.isEmpty(statKey)) {
                G.w(statKey);
                Log.d("FuncTopBannerScrollGlobalModel click", funcTopBannerScrollData.getTitle() + " " + statKey);
            }
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        public int getCount() {
            List<FuncTopBannerScrollData> list = this.funcTopBannerScrollDataList;
            if (list == null) {
                return 0;
            }
            int size = list.size();
            if (size > 1) {
                return 1000;
            }
            return size;
        }

        public List<FuncTopBannerScrollData> getFuncTopBannerScrollDataList() {
            return this.funcTopBannerScrollDataList;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:4:0x0039, code lost:
            r12 = r10.funcTopBannerScrollDataList.get(r12);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Object instantiateItem(android.view.ViewGroup r11, int r12) {
            /*
                r10 = this;
                android.view.LayoutInflater r0 = r10.mInflater
                r1 = 2131493006(0x7f0c008e, float:1.860948E38)
                r2 = 0
                android.view.View r0 = r0.inflate(r1, r2)
                r1 = 2131296975(0x7f0902cf, float:1.8211882E38)
                android.view.View r1 = r0.findViewById(r1)
                android.widget.ImageView r1 = (android.widget.ImageView) r1
                r2 = 2131297844(0x7f090634, float:1.8213644E38)
                android.view.View r2 = r0.findViewById(r2)
                android.widget.TextView r2 = (android.widget.TextView) r2
                r3 = 2131297740(0x7f0905cc, float:1.8213433E38)
                android.view.View r3 = r0.findViewById(r3)
                android.widget.TextView r3 = (android.widget.TextView) r3
                r4 = 2131297108(0x7f090354, float:1.8212152E38)
                android.view.View r4 = r0.findViewById(r4)
                android.widget.ImageView r4 = (android.widget.ImageView) r4
                java.util.List<com.miui.common.card.functions.FuncTopBannerScrollData> r5 = r10.funcTopBannerScrollDataList
                if (r5 == 0) goto L_0x0095
                int r5 = r5.size()
                int r12 = r12 % r5
                if (r12 >= r5) goto L_0x0095
                java.util.List<com.miui.common.card.functions.FuncTopBannerScrollData> r5 = r10.funcTopBannerScrollDataList
                java.lang.Object r12 = r5.get(r12)
                com.miui.common.card.functions.FuncTopBannerScrollData r12 = (com.miui.common.card.functions.FuncTopBannerScrollData) r12
                if (r12 == 0) goto L_0x0095
                com.miui.common.card.functions.BaseFunction r5 = r12.getCommonFunction()
                android.content.Context r6 = r10.context
                android.content.res.Resources r6 = r6.getResources()
                r7 = 2131100544(0x7f060380, float:1.7813472E38)
                int r6 = r6.getColor(r7)
                r1.setColorFilter(r6)
                java.lang.String r6 = r12.getIcon()
                b.c.a.b.d r8 = b.b.c.j.r.g
                r9 = 2131231069(0x7f08015d, float:1.8078209E38)
                b.b.c.j.r.a((java.lang.String) r6, (android.widget.ImageView) r1, (b.c.a.b.d) r8, (int) r9)
                android.content.Context r1 = r10.context
                android.content.res.Resources r1 = r1.getResources()
                int r1 = r1.getColor(r7)
                r4.setColorFilter(r1)
                java.lang.String r1 = r12.getImgUrl()
                b.c.a.b.d r6 = b.b.c.j.r.f1760d
                r7 = 2131231022(0x7f08012e, float:1.8078113E38)
                b.b.c.j.r.a((java.lang.String) r1, (android.widget.ImageView) r4, (b.c.a.b.d) r6, (int) r7)
                java.lang.String r1 = r12.getTitle()
                r2.setText(r1)
                java.lang.String r1 = r12.getSummary()
                r3.setText(r1)
                b.b.c.j.l.a(r0)
                com.miui.common.card.models.FuncTopBannerScrollGlobalModel$MyPagerAdapter$1 r1 = new com.miui.common.card.models.FuncTopBannerScrollGlobalModel$MyPagerAdapter$1
                r1.<init>(r5, r12)
                r0.setOnClickListener(r1)
            L_0x0095:
                r12 = 0
                r11.addView(r0, r12)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.common.card.models.FuncTopBannerScrollGlobalModel.MyPagerAdapter.instantiateItem(android.view.ViewGroup, int):java.lang.Object");
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        public void setFuncTopBannerScrollDataList(List<FuncTopBannerScrollData> list) {
            this.funcTopBannerScrollDataList = list;
        }
    }

    public FuncTopBannerScrollGlobalModel() {
        this((AbsModel) null);
    }

    public FuncTopBannerScrollGlobalModel(AbsModel absModel) {
        super(R.layout.card_layout_top_banner_scroll, absModel);
    }

    public BaseViewHolder createViewHolder(View view) {
        return new FuncTopBannerGlobalScrollHolder(view);
    }
}
