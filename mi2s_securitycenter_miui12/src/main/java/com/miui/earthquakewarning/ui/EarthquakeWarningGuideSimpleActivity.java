package com.miui.earthquakewarning.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.c.a;
import com.miui.earthquakewarning.soundplay.GuidePlaySound;
import com.miui.earthquakewarning.view.ControlledViewPager;
import com.miui.privacyapps.view.ViewPagerIndicator;
import com.miui.securitycenter.R;
import miui.os.Build;

public class EarthquakeWarningGuideSimpleActivity extends a implements View.OnClickListener, ViewPager.OnPageChangeListener {
    /* access modifiers changed from: private */
    public static int[] TUTORIAL_ICONS = {R.drawable.ew_guide_page_1, R.drawable.ew_guide_page_2};
    /* access modifiers changed from: private */
    public int mCurrentPosition;
    private ViewPagerIndicator mIndicator;
    /* access modifiers changed from: private */
    public Button mNext;
    /* access modifiers changed from: private */
    public boolean mNextEnable;
    /* access modifiers changed from: private */
    public GuidePlaySound mPlaySound;
    private TextView mTitle;
    /* access modifiers changed from: private */
    public Button mViewAudio;
    private ControlledViewPager mViewPager;
    CountDownTimer timer;

    class TutorialPagerAdapter extends PagerAdapter {
        private Context context;

        public TutorialPagerAdapter(Context context2) {
            this.context = context2;
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            ((ViewPager) viewGroup).removeView((View) obj);
        }

        public int getCount() {
            return 2;
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            ImageView imageView = new ImageView(this.context);
            imageView.setImageResource(EarthquakeWarningGuideSimpleActivity.TUTORIAL_ICONS[i]);
            viewGroup.addView(imageView);
            return imageView;
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }
    }

    private void countDownTimer(int i) {
        this.mViewAudio.setEnabled(false);
        CountDownTimer countDownTimer = this.timer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
            this.timer = null;
            this.mNextEnable = false;
            this.mNext.setEnabled(false);
        }
        this.timer = new CountDownTimer((long) (i * 1000), 1000) {
            public void onFinish() {
                EarthquakeWarningGuideSimpleActivity.this.mViewAudio.setText(EarthquakeWarningGuideSimpleActivity.this.getString(R.string.ew_guide_play_audio));
                EarthquakeWarningGuideSimpleActivity.this.mPlaySound.stop();
                boolean unused = EarthquakeWarningGuideSimpleActivity.this.mNextEnable = true;
                EarthquakeWarningGuideSimpleActivity.this.mNext.setEnabled(true);
                EarthquakeWarningGuideSimpleActivity.this.mViewAudio.setEnabled(true);
            }

            public void onTick(long j) {
                String str;
                Button button;
                long j2 = j / 1000;
                if (j2 < 1) {
                    button = EarthquakeWarningGuideSimpleActivity.this.mViewAudio;
                    str = EarthquakeWarningGuideSimpleActivity.this.getString(R.string.ew_guide_play_audio);
                } else {
                    button = EarthquakeWarningGuideSimpleActivity.this.mViewAudio;
                    str = EarthquakeWarningGuideSimpleActivity.this.getString(R.string.ew_guide_play_audio_last, new Object[]{Long.valueOf(j2)});
                }
                button.setText(str);
                if (EarthquakeWarningGuideSimpleActivity.this.mCurrentPosition == 0 && j2 < 6) {
                    boolean unused = EarthquakeWarningGuideSimpleActivity.this.mNextEnable = true;
                    EarthquakeWarningGuideSimpleActivity.this.mNext.setEnabled(true);
                }
            }
        }.start();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.view_audio) {
            if (this.mCurrentPosition == 0) {
                countDownTimer(11);
                this.mPlaySound.playGuide1();
                return;
            }
            countDownTimer(5);
            this.mPlaySound.playGuide2();
        } else if (view.getId() != R.id.btn_next) {
        } else {
            if (this.mCurrentPosition == 1) {
                finish();
                return;
            }
            this.mViewPager.toggleSlide(true);
            this.mViewPager.setCurrentItem(1);
            this.mViewPager.toggleSlide(false);
            this.mCurrentPosition++;
            GuidePlaySound guidePlaySound = this.mPlaySound;
            if (guidePlaySound != null) {
                guidePlaySound.stop();
            }
            CountDownTimer countDownTimer = this.timer;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                this.timer = null;
            }
            this.mViewAudio.setText(getString(R.string.ew_guide_play_audio));
            this.mViewAudio.setEnabled(true);
            this.mNext.setEnabled(false);
            this.mNext.setText(R.string.auto_task_dialog_button_close);
            this.mTitle.setText(getResources().getString(R.string.ew_guide_tips_2) + getResources().getString(R.string.ew_guide_tips_3));
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [b.b.c.c.a, android.content.Context, android.view.View$OnClickListener, com.miui.earthquakewarning.ui.EarthquakeWarningGuideSimpleActivity, miui.app.Activity, android.support.v4.view.ViewPager$OnPageChangeListener] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.earthquake_warning_activity_guide_simple);
        if (Build.IS_INTERNATIONAL_BUILD) {
            finish();
            return;
        }
        this.mPlaySound = new GuidePlaySound(this);
        this.mTitle = (TextView) findViewById(R.id.tv_title);
        this.mViewPager = (ControlledViewPager) findViewById(R.id.view_pager);
        this.mIndicator = (ViewPagerIndicator) findViewById(R.id.indicator);
        this.mViewAudio = (Button) findViewById(R.id.view_audio);
        this.mNext = (Button) findViewById(R.id.btn_next);
        this.mViewAudio.setOnClickListener(this);
        this.mNext.setOnClickListener(this);
        this.mViewPager.setAdapter(new TutorialPagerAdapter(this));
        this.mViewPager.setOnPageChangeListener(this);
        this.mIndicator.setIndicatorNum(TUTORIAL_ICONS.length);
        this.mTitle.setText(getResources().getString(R.string.ew_guide_tips_1));
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        EarthquakeWarningGuideSimpleActivity.super.onDestroy();
        GuidePlaySound guidePlaySound = this.mPlaySound;
        if (guidePlaySound != null) {
            guidePlaySound.release();
        }
    }

    public void onPageScrollStateChanged(int i) {
    }

    public void onPageScrolled(int i, float f, int i2) {
    }

    public void onPageSelected(int i) {
        this.mIndicator.setSelected(i);
    }

    public void onPause() {
        super.onPause();
        GuidePlaySound guidePlaySound = this.mPlaySound;
        if (guidePlaySound != null) {
            guidePlaySound.stop();
        }
        CountDownTimer countDownTimer = this.timer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
            this.timer = null;
        }
    }
}
