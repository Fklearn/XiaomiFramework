package com.miui.warningcenter.mijia;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.preference.A;
import androidx.preference.Preference;
import com.miui.privacyapps.view.ViewPagerIndicator;
import com.miui.securitycenter.R;

public class MijiaTitleGalleryPreference extends Preference {
    /* access modifiers changed from: private */
    public static int[] PICTURES = {R.drawable.warningcenter_gallery_1, R.drawable.warningcenter_gallery_2, R.drawable.warningcenter_gallery_3, R.drawable.warningcenter_gallery_4};
    private Context mContext;
    /* access modifiers changed from: private */
    public ViewPagerIndicator mIndicator;
    private ViewPager mViewPager;

    class MyAdapter extends PagerAdapter {
        private Context context;

        public MyAdapter(Context context2) {
            this.context = context2;
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            ((ViewPager) viewGroup).removeView((View) obj);
        }

        public int getCount() {
            return MijiaTitleGalleryPreference.PICTURES.length;
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            ImageView imageView = new ImageView(this.context);
            imageView.setImageResource(MijiaTitleGalleryPreference.PICTURES[i]);
            viewGroup.addView(imageView);
            return imageView;
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }
    }

    public MijiaTitleGalleryPreference(Context context) {
        super(context);
        this.mContext = context;
    }

    public MijiaTitleGalleryPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    public void onBindViewHolder(A a2) {
        super.onBindViewHolder(a2);
        this.mViewPager = a2.itemView.findViewById(R.id.view_pager);
        this.mIndicator = (ViewPagerIndicator) a2.itemView.findViewById(R.id.indicator);
        this.mViewPager.setAdapter(new MyAdapter(this.mContext));
        this.mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int i) {
            }

            public void onPageScrolled(int i, float f, int i2) {
            }

            public void onPageSelected(int i) {
                MijiaTitleGalleryPreference.this.mIndicator.setSelected(i);
            }
        });
        this.mIndicator.setIndicatorNum(PICTURES.length);
    }
}
