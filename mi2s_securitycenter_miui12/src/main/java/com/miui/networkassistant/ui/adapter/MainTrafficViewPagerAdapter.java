package com.miui.networkassistant.ui.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class MainTrafficViewPagerAdapter extends PagerAdapter {
    private List<View> mListViews = new ArrayList();

    public MainTrafficViewPagerAdapter(List<View> list) {
        this.mListViews = list;
    }

    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        viewGroup.removeView(this.mListViews.get(i));
    }

    public int getCount() {
        return this.mListViews.size();
    }

    public Object instantiateItem(ViewGroup viewGroup, int i) {
        viewGroup.addView(this.mListViews.get(i), 0);
        return this.mListViews.get(i);
    }

    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }
}
