package b.b.j.b;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import b.b.c.j.A;
import b.b.c.j.r;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.functions.FuncTopBannerScrollData;
import com.miui.common.card.models.BaseCardModel;
import com.miui.common.card.models.FunctionCardModel;
import com.miui.common.customview.AutoScrollViewPager;
import com.miui.privacyapps.view.ViewPagerIndicator;
import com.miui.securitycenter.R;
import com.miui.securityscan.a.G;
import com.miui.securityscan.cards.n;
import com.miui.securityscan.model.AbsModel;
import d.a.f;
import java.util.List;

public class b extends FunctionCardModel {

    /* renamed from: a  reason: collision with root package name */
    private C0029b f1790a;

    public class a extends PagerAdapter {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public Context f1791a;

        /* renamed from: b  reason: collision with root package name */
        private LayoutInflater f1792b;

        /* renamed from: c  reason: collision with root package name */
        private List<FuncTopBannerScrollData> f1793c;

        public a(Context context) {
            this.f1791a = context;
            this.f1792b = LayoutInflater.from(context);
        }

        public void a(List<FuncTopBannerScrollData> list) {
            this.f1793c = list;
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        public int getCount() {
            List<FuncTopBannerScrollData> list = this.f1793c;
            if (list == null) {
                return 0;
            }
            int size = list.size();
            if (size > 1) {
                return 1000;
            }
            return size;
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            int size;
            int size2;
            FuncTopBannerScrollData funcTopBannerScrollData;
            View inflate = this.f1792b.inflate(R.layout.phone_manage_banner_item_layout, (ViewGroup) null);
            ImageView imageView = (ImageView) inflate.findViewById(R.id.banner_img);
            List<FuncTopBannerScrollData> list = this.f1793c;
            if (!(list == null || (size2 = i % (size = list.size())) >= size || (funcTopBannerScrollData = this.f1793c.get(size2)) == null)) {
                imageView.setColorFilter(this.f1791a.getResources().getColor(R.color.result_banner_icon_bg));
                r.a(funcTopBannerScrollData.getImgUrl(), imageView, r.f1760d, (int) R.drawable.big_banner_background_default);
                imageView.setContentDescription(funcTopBannerScrollData.getTitle());
                if (A.a()) {
                    try {
                        f a2 = d.a.b.a(inflate);
                        a2.touch().setTint(0.0f, 0.0f, 0.0f, 0.0f);
                        a2.touch().a(inflate, new d.a.a.a[0]);
                    } catch (Throwable unused) {
                        Log.e("PhoneManageBannerModel", "no support folme");
                    }
                }
                inflate.setOnClickListener(new a(this, funcTopBannerScrollData));
            }
            viewGroup.addView(inflate, 0);
            return inflate;
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }
    }

    /* renamed from: b.b.j.b.b$b  reason: collision with other inner class name */
    public class C0029b extends BaseViewHolder implements ViewPager.OnPageChangeListener {

        /* renamed from: a  reason: collision with root package name */
        Context f1795a;

        /* renamed from: b  reason: collision with root package name */
        AutoScrollViewPager f1796b;

        /* renamed from: c  reason: collision with root package name */
        ViewPagerIndicator f1797c;

        /* renamed from: d  reason: collision with root package name */
        a f1798d;
        int e = this.f1795a.getResources().getDimensionPixelSize(R.dimen.main_indicator_with);
        int f = this.f1795a.getResources().getColor(R.color.phone_manage_indicator_normal_color);
        int g = this.f1795a.getResources().getColor(R.color.phone_manage_indicator_selected_color);
        int h = this.f1795a.getResources().getDimensionPixelSize(R.dimen.main_indicator_marginLeft);
        int i;
        int j = this.f1795a.getResources().getDimensionPixelSize(R.dimen.phone_manage_indicator_mrg_bottom);
        b k;
        n l;

        public C0029b(View view) {
            super(view);
            this.f1795a = view.getContext();
            this.f1796b = (AutoScrollViewPager) view.findViewById(R.id.view_pager);
            this.f1797c = (ViewPagerIndicator) view.findViewById(R.id.indicator);
            this.f1797c.setCycle(true);
            this.f1798d = new a(this.f1795a);
            this.f1796b.setAdapter(this.f1798d);
            this.f1796b.setInterval(4000);
            this.f1796b.setBorderAnimation(false);
            ViewPagerIndicator viewPagerIndicator = this.f1797c;
            int i2 = this.h;
            viewPagerIndicator.a(i2, this.i, i2, this.j);
            ViewPagerIndicator viewPagerIndicator2 = this.f1797c;
            int i3 = this.e;
            viewPagerIndicator2.a(1, i3, i3, this.f, this.g);
        }

        public void bindData(int i2, Object obj) {
            if (obj != null && (obj instanceof n)) {
                this.l = (n) obj;
            }
        }

        public void fillData(View view, BaseCardModel baseCardModel, int i2) {
            if (baseCardModel instanceof b) {
                this.k = (b) baseCardModel;
                List<FuncTopBannerScrollData> funcTopBannerScrollDataList = this.k.getFuncTopBannerScrollDataList();
                if (funcTopBannerScrollDataList != null && !funcTopBannerScrollDataList.isEmpty()) {
                    int size = funcTopBannerScrollDataList.size();
                    this.f1798d.a(funcTopBannerScrollDataList);
                    this.f1798d.notifyDataSetChanged();
                    int count = this.f1798d.getCount();
                    int i3 = count > 1 ? ((count / 2) / size) * size : count;
                    int currentIndex = this.k.getCurrentIndex();
                    if (currentIndex != -1) {
                        i3 = currentIndex;
                    }
                    this.f1797c.a(size, i3);
                    if (count < 2) {
                        this.f1797c.setVisibility(8);
                        if (this.k.isDefaultStatShow()) {
                            G.a(funcTopBannerScrollDataList.get(0));
                        }
                    } else {
                        this.f1797c.setVisibility(0);
                    }
                    this.f1796b.setOnPageChangeListener(this);
                    this.f1796b.setCurrentItem(i3);
                    this.f1796b.c();
                    if (this.k.isCanAutoScroll()) {
                        this.f1796b.a(2000);
                    }
                    n nVar = this.l;
                    if (nVar != null) {
                        nVar.a(this.f1796b);
                    }
                }
            }
        }

        public void onPageScrollStateChanged(int i2) {
        }

        public void onPageScrolled(int i2, float f2, int i3) {
        }

        public void onPageSelected(int i2) {
            int size;
            int size2;
            this.f1797c.setSelected(i2);
            b bVar = this.k;
            if (bVar != null) {
                bVar.setCurrentIndex(i2);
                List<FuncTopBannerScrollData> funcTopBannerScrollDataList = this.k.getFuncTopBannerScrollDataList();
                if (funcTopBannerScrollDataList != null && !funcTopBannerScrollDataList.isEmpty() && (size2 = i2 % (size = funcTopBannerScrollDataList.size())) < size && this.k.isDefaultStatShow()) {
                    G.a(funcTopBannerScrollDataList.get(size2));
                }
            }
        }
    }

    public b() {
        this((AbsModel) null);
    }

    public b(AbsModel absModel) {
        super(R.layout.phone_manage_card_banner_layout, absModel);
    }

    public BaseViewHolder createViewHolder(View view) {
        this.f1790a = new C0029b(view);
        return this.f1790a;
    }

    public void startAutoScroll() {
        AutoScrollViewPager autoScrollViewPager;
        C0029b bVar = this.f1790a;
        if (bVar != null && (autoScrollViewPager = bVar.f1796b) != null) {
            autoScrollViewPager.a(2000);
        }
    }

    public void stopAutoScroll() {
        AutoScrollViewPager autoScrollViewPager;
        C0029b bVar = this.f1790a;
        if (bVar != null && (autoScrollViewPager = bVar.f1796b) != null) {
            autoScrollViewPager.c();
        }
    }
}
