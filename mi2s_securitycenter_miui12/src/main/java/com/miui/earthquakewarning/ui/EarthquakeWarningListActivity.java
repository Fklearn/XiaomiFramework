package com.miui.earthquakewarning.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import b.b.c.c.a;
import b.b.c.j.e;
import b.b.c.j.i;
import com.miui.earthquakewarning.view.ControlledViewPager;
import com.miui.gamebooster.a.w;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;
import miui.widget.FilterSortView;

public class EarthquakeWarningListActivity extends a {
    private List<Fragment> mFragments;
    /* access modifiers changed from: private */
    public ControlledViewPager mViewPager;
    /* access modifiers changed from: private */
    public Button mWarningAllBtn;
    /* access modifiers changed from: private */
    public Button mWarningReceiveBtn;
    private View.OnClickListener myClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (view.getId() == R.id.btn_warning_all) {
                EarthquakeWarningListActivity.this.mViewPager.setCurrentItem(1);
                EarthquakeWarningListActivity.this.mWarningAllBtn.setSelected(true);
                EarthquakeWarningListActivity.this.mWarningReceiveBtn.setSelected(false);
            } else if (view.getId() == R.id.btn_warning_receive) {
                EarthquakeWarningListActivity.this.mViewPager.setCurrentItem(0);
                EarthquakeWarningListActivity.this.mWarningAllBtn.setSelected(false);
                EarthquakeWarningListActivity.this.mWarningReceiveBtn.setSelected(true);
            }
        }
    };

    class MyPagerAdapter extends w {
        private List<Fragment> mFragments;

        public MyPagerAdapter(FragmentManager fragmentManager, List<Fragment> list) {
            super(fragmentManager);
            this.mFragments = list;
        }

        public int getCount() {
            List<Fragment> list = this.mFragments;
            if (list == null) {
                return 0;
            }
            return list.size();
        }

        public Fragment getItem(int i) {
            return this.mFragments.get(i);
        }
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [b.b.c.c.a, android.content.Context, miui.app.Activity, com.miui.earthquakewarning.ui.EarthquakeWarningListActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.earthquake_warning_activity_list);
        if (Build.IS_INTERNATIONAL_BUILD) {
            finish();
            return;
        }
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ew_list_root);
        LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.ll_split);
        LinearLayout linearLayout3 = (LinearLayout) findViewById(R.id.ll_split_old);
        if (e.b() >= 10) {
            FilterSortView findViewById = findViewById(R.id.filter_sort);
            FilterSortView.TabView addTab = findViewById.addTab(getResources().getString(R.string.ew_list_receive));
            addTab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    EarthquakeWarningListActivity.this.mViewPager.setCurrentItem(0);
                }
            });
            findViewById.addTab(getResources().getString(R.string.ew_list_all)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    EarthquakeWarningListActivity.this.mViewPager.setCurrentItem(1);
                }
            });
            findViewById.setFilteredTab(addTab);
            findViewById.setTabIncatorVisibility(8);
            linearLayout2.setVisibility(0);
            linearLayout3.setVisibility(8);
        } else {
            this.mWarningAllBtn = (Button) findViewById(R.id.btn_warning_all);
            this.mWarningReceiveBtn = (Button) findViewById(R.id.btn_warning_receive);
            this.mWarningAllBtn.setOnClickListener(this.myClickListener);
            this.mWarningReceiveBtn.setOnClickListener(this.myClickListener);
            this.mWarningAllBtn.setSelected(false);
            this.mWarningReceiveBtn.setSelected(true);
            linearLayout2.setVisibility(8);
            linearLayout3.setVisibility(0);
        }
        this.mViewPager = (ControlledViewPager) findViewById(R.id.viewpager);
        this.mFragments = new ArrayList();
        EarthquakeWarningListFragment earthquakeWarningListFragment = new EarthquakeWarningListFragment();
        EarthquakeWarningUnreceiveListFragment earthquakeWarningUnreceiveListFragment = new EarthquakeWarningUnreceiveListFragment();
        this.mFragments.add(earthquakeWarningListFragment);
        this.mFragments.add(earthquakeWarningUnreceiveListFragment);
        this.mViewPager.setAdapter(new MyPagerAdapter(getFragmentManager(), this.mFragments));
        this.mViewPager.setCurrentItem(0);
        if (i.e()) {
            int f = i.f(this) - ((int) getResources().getDimension(R.dimen.pc_status_bar_margin_top));
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) linearLayout.getLayoutParams();
            marginLayoutParams.topMargin += f;
            linearLayout.setLayoutParams(marginLayoutParams);
        }
    }
}
