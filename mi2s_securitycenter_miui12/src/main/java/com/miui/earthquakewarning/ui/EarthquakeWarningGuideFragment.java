package com.miui.earthquakewarning.ui;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.c.c.b.d;
import com.miui.earthquakewarning.Constants;
import com.miui.earthquakewarning.analytics.AnalyticHelper;
import com.miui.earthquakewarning.soundplay.GuidePlaySound;
import com.miui.securitycenter.R;

public class EarthquakeWarningGuideFragment extends d implements View.OnClickListener {
    private Listener listener;
    private Context mContext;
    private TextView mGuideText;
    private ImageView mImageGuide;
    /* access modifiers changed from: private */
    public Button mNext;
    /* access modifiers changed from: private */
    public boolean mNextEnable;
    /* access modifiers changed from: private */
    public GuidePlaySound mPlaySound;
    /* access modifiers changed from: private */
    public int mPosition = -1;
    private RelativeLayout mRoot;
    /* access modifiers changed from: private */
    public Button mViewAudio;
    CountDownTimer timer;

    public interface Listener {
        void onPlayCompleteCallback();
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
                EarthquakeWarningGuideFragment.this.mViewAudio.setText(EarthquakeWarningGuideFragment.this.getString(R.string.ew_guide_play_audio));
                EarthquakeWarningGuideFragment.this.mPlaySound.stop();
                boolean unused = EarthquakeWarningGuideFragment.this.mNextEnable = true;
                EarthquakeWarningGuideFragment.this.mNext.setEnabled(true);
                EarthquakeWarningGuideFragment.this.mViewAudio.setEnabled(true);
            }

            public void onTick(long j) {
                String str;
                Button button;
                long j2 = j / 1000;
                if (j2 < 1) {
                    button = EarthquakeWarningGuideFragment.this.mViewAudio;
                    str = EarthquakeWarningGuideFragment.this.getString(R.string.ew_guide_play_audio);
                } else {
                    button = EarthquakeWarningGuideFragment.this.mViewAudio;
                    str = EarthquakeWarningGuideFragment.this.getString(R.string.ew_guide_play_audio_last, new Object[]{Long.valueOf(j2)});
                }
                button.setText(str);
                if (EarthquakeWarningGuideFragment.this.mPosition == 0 && j2 < 6) {
                    boolean unused = EarthquakeWarningGuideFragment.this.mNextEnable = true;
                    EarthquakeWarningGuideFragment.this.mNext.setEnabled(true);
                }
            }
        }.start();
    }

    /* access modifiers changed from: protected */
    public void initView() {
        ImageView imageView;
        int i;
        this.mRoot = (RelativeLayout) findViewById(R.id.root);
        this.mGuideText = (TextView) findViewById(R.id.guide_text);
        this.mNext = (Button) findViewById(R.id.btn_next);
        this.mImageGuide = (ImageView) findViewById(R.id.image_guide);
        this.mViewAudio = (Button) findViewById(R.id.view_audio);
        this.mViewAudio.setOnClickListener(this);
        this.mNext.setOnClickListener(this);
        this.mRoot.setOnClickListener(this);
        int i2 = this.mPosition;
        if (i2 == 0) {
            this.mPlaySound = new GuidePlaySound(this.mContext);
            this.mGuideText.setText(getString(R.string.ew_guide_tips_1));
            imageView = this.mImageGuide;
            i = R.drawable.ew_guide_icon_1;
        } else if (i2 == 1) {
            this.mPlaySound = new GuidePlaySound(this.mContext);
            this.mGuideText.setText(getString(R.string.ew_guide_tips_2));
            imageView = this.mImageGuide;
            i = R.drawable.ew_guide_icon_2;
        } else if (i2 == 2) {
            this.mGuideText.setText(getString(R.string.ew_guide_tips_3));
            this.mImageGuide.setImageResource(R.drawable.ew_guide_icon_3);
            this.mViewAudio.setVisibility(8);
            this.mNextEnable = true;
            this.mNext.setEnabled(true);
            return;
        } else {
            return;
        }
        imageView.setImageResource(i);
    }

    public void onClick(View view) {
        String str;
        int id = view.getId();
        if (id != R.id.btn_next) {
            if (id == R.id.root) {
                getActivity().finish();
                return;
            } else if (id == R.id.view_audio) {
                int i = this.mPosition;
                str = AnalyticHelper.GUIDE_CLICK_LISTEN;
                if (i == 0) {
                    this.mPlaySound.playGuide1();
                    countDownTimer(11);
                } else if (i == 1) {
                    this.mPlaySound.playGuide2();
                    countDownTimer(5);
                    AnalyticHelper.trackGuide2ActionModuleClick(str);
                    return;
                } else {
                    return;
                }
            } else {
                return;
            }
        } else if (this.listener != null) {
            GuidePlaySound guidePlaySound = this.mPlaySound;
            if (guidePlaySound != null) {
                guidePlaySound.stop();
            }
            this.listener.onPlayCompleteCallback();
            int i2 = this.mPosition;
            str = AnalyticHelper.GUIDE_CLICK_NEXT;
            if (i2 != 0) {
                if (i2 != 1) {
                    if (i2 == 2) {
                        AnalyticHelper.trackGuide3ActionModuleClick(str);
                        return;
                    }
                    return;
                }
                AnalyticHelper.trackGuide2ActionModuleClick(str);
                return;
            }
        } else {
            return;
        }
        AnalyticHelper.trackGuide1ActionModuleClick(str);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getContext();
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mPosition = arguments.getInt(Constants.INTENT_KEY_EARTHQUAKE_WARNING_GUIDE_POSITION);
        }
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.earthquake_warning_fragment_guide;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroyView() {
        super.onDestroyView();
        GuidePlaySound guidePlaySound = this.mPlaySound;
        if (guidePlaySound != null) {
            guidePlaySound.release();
        }
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
        if (this.mPosition <= 1) {
            this.mViewAudio.setText(getString(R.string.ew_guide_play_audio));
            this.mNextEnable = true;
            this.mNext.setEnabled(false);
            this.mViewAudio.setEnabled(true);
        }
    }

    public void setListener(Listener listener2) {
        this.listener = listener2;
    }
}
