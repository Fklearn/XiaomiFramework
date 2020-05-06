package com.miui.earthquakewarning.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import b.b.c.c.a;
import com.miui.earthquakewarning.Constants;
import com.miui.earthquakewarning.analytics.AnalyticHelper;
import com.miui.earthquakewarning.ui.EarthquakeWarningGuideFragment;
import com.miui.earthquakewarning.ui.EarthquakeWarningGuideLawFragment;
import com.miui.earthquakewarning.view.ControlledViewPager;
import com.miui.gamebooster.a.w;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeWarningGuideActivity extends a {
    private int mCurrentPosition;
    private List<Fragment> mFragments;
    private boolean mFromGuide;
    /* access modifiers changed from: private */
    public ControlledViewPager mViewPager;
    private LinearLayout root;

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

    static /* synthetic */ int access$104(EarthquakeWarningGuideActivity earthquakeWarningGuideActivity) {
        int i = earthquakeWarningGuideActivity.mCurrentPosition + 1;
        earthquakeWarningGuideActivity.mCurrentPosition = i;
        return i;
    }

    public void onBackPressed() {
        EarthquakeWarningGuideActivity.super.onBackPressed();
        setResult(1004);
        int i = this.mCurrentPosition;
        if (i == 0) {
            AnalyticHelper.trackGuide1ActionModuleClick(AnalyticHelper.GUIDE_CLICK_BACK);
        } else if (i == 1) {
            AnalyticHelper.trackGuide2ActionModuleClick(AnalyticHelper.GUIDE_CLICK_BACK);
        } else if (i == 2) {
            AnalyticHelper.trackGuide3ActionModuleClick(AnalyticHelper.GUIDE_CLICK_BACK);
        } else if (i == 3) {
            AnalyticHelper.trackGuide4ActionModuleClick(AnalyticHelper.GUIDE_CLICK_BACK);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        EarthquakeWarningGuideActivity.super.onConfigurationChanged(configuration);
        finish();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        overridePendingTransition(R.anim.applock_confirm_open_anim, 0);
        setContentView(R.layout.earthquake_warning_activity_guide);
        this.mFromGuide = getIntent().getBooleanExtra("mFromGuide", false);
        if (Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(1);
        }
        if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            finish();
            return;
        }
        this.mViewPager = (ControlledViewPager) findViewById(R.id.view_pager);
        this.root = (LinearLayout) findViewById(R.id.root);
        this.mFragments = new ArrayList();
        for (int i = 0; i < 3; i++) {
            EarthquakeWarningGuideFragment earthquakeWarningGuideFragment = new EarthquakeWarningGuideFragment();
            Bundle bundle2 = new Bundle();
            bundle2.putInt(Constants.INTENT_KEY_EARTHQUAKE_WARNING_GUIDE_POSITION, i);
            earthquakeWarningGuideFragment.setArguments(bundle2);
            earthquakeWarningGuideFragment.setListener(new EarthquakeWarningGuideFragment.Listener() {
                public void onPlayCompleteCallback() {
                    EarthquakeWarningGuideActivity.this.mViewPager.toggleSlide(true);
                    EarthquakeWarningGuideActivity.this.mViewPager.setCurrentItem(EarthquakeWarningGuideActivity.access$104(EarthquakeWarningGuideActivity.this));
                    EarthquakeWarningGuideActivity.this.mViewPager.toggleSlide(false);
                }
            });
            this.mFragments.add(earthquakeWarningGuideFragment);
        }
        EarthquakeWarningGuideLawFragment earthquakeWarningGuideLawFragment = new EarthquakeWarningGuideLawFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putBoolean("mFromGuide", this.mFromGuide);
        earthquakeWarningGuideLawFragment.setArguments(bundle3);
        earthquakeWarningGuideLawFragment.setListener(new EarthquakeWarningGuideLawFragment.Listener() {
            public void onCancelCallback() {
                EarthquakeWarningGuideActivity.this.setResult(1004);
                EarthquakeWarningGuideActivity.this.finish();
            }

            public void onCompleteCallback() {
                EarthquakeWarningGuideActivity.this.setResult(1003);
                EarthquakeWarningGuideActivity.this.finish();
            }
        });
        this.mFragments.add(earthquakeWarningGuideLawFragment);
        this.mViewPager.setAdapter(new MyPagerAdapter(getFragmentManager(), this.mFragments));
    }
}
