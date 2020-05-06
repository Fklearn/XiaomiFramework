package com.miui.earthquakewarning.ui;

import android.app.StatusBarManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import b.b.c.c.a;
import b.b.o.g.e;
import com.google.android.exoplayer2.util.MimeTypes;
import com.miui.common.customview.ScoreTextView;
import com.miui.earthquakewarning.EarthquakeWarningManager;
import com.miui.earthquakewarning.analytics.AnalyticHelper;
import com.miui.earthquakewarning.model.UserQuakeItem;
import com.miui.earthquakewarning.service.EarthquakeWarningService;
import com.miui.earthquakewarning.soundplay.PlaySound;
import com.miui.earthquakewarning.utils.LocationUtils;
import com.miui.earthquakewarning.utils.NotificationUtil;
import com.miui.earthquakewarning.utils.TypefaceHelper;
import com.miui.earthquakewarning.utils.Utils;
import com.miui.earthquakewarning.utils.VibratorUtil;
import com.miui.gamebooster.m.C0384o;
import com.miui.securitycenter.R;
import com.miui.warningcenter.WarningCenterAlertAdapter;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class EarthquakeWarningAlertActivity extends a implements View.OnClickListener {
    public static final String TAG = "EarthquakeWarningAlert";
    private TextView alertTips;
    private boolean isPreviousGestureNav = false;
    private View mAlertCard;
    private TextView mAlertCityText;
    private TextView mAlertFeelText;
    private TextView mAlertFromText;
    private ImageView mAlertIcon;
    private TextView mAlertIntensity;
    private TextView mAlertLevelText;
    private TextView mAlertTitle;
    /* access modifiers changed from: private */
    public TextView mArriveText;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public int mCountDown;
    private TextView mDistanceText;
    /* access modifiers changed from: private */
    public MyHandler mHandler;
    private ImageView mHelpAlarm;
    private View mHelpCard;
    private TextView mHelpCityText;
    private TextView mHelpEarthquakeText;
    private TextView mHelpFeelText;
    private TextView mHelpFromText;
    private TextView mHelpIntensity;
    private TextView mHelpLevelText;
    private Button mHelpPlaceNavi;
    /* access modifiers changed from: private */
    public TextView mHelpSafeText;
    private View mOtherCard;
    private PlaySound mPlaySound;
    private View mSafePlaceCard;
    /* access modifiers changed from: private */
    public ScoreTextView mSecondsText;
    private TimerTask mTask;
    /* access modifiers changed from: private */
    public Timer mTimer;
    /* access modifiers changed from: private */
    public UserQuakeItem mUserQuakeItem;
    private LinearLayout mViewCallPhone;
    private LinearLayout mViewShowEmergency;
    private TextView mWarningClose;

    static class MyHandler extends Handler {
        WeakReference<EarthquakeWarningAlertActivity> mActivityReference;

        MyHandler(EarthquakeWarningAlertActivity earthquakeWarningAlertActivity) {
            this.mActivityReference = new WeakReference<>(earthquakeWarningAlertActivity);
        }

        public void handleMessage(Message message) {
            EarthquakeWarningAlertActivity earthquakeWarningAlertActivity = (EarthquakeWarningAlertActivity) this.mActivityReference.get();
            if (earthquakeWarningAlertActivity != null && message.what == 1) {
                if (earthquakeWarningAlertActivity.mCountDown >= 0) {
                    earthquakeWarningAlertActivity.showWarningCard();
                    earthquakeWarningAlertActivity.mSecondsText.setText(String.valueOf(earthquakeWarningAlertActivity.mCountDown));
                    if (earthquakeWarningAlertActivity.mCountDown > 1) {
                        earthquakeWarningAlertActivity.updateLastCount(earthquakeWarningAlertActivity.mCountDown);
                    } else {
                        earthquakeWarningAlertActivity.updateLastCount(1);
                    }
                } else if (earthquakeWarningAlertActivity.mCountDown > -12) {
                    earthquakeWarningAlertActivity.showHelpCard();
                } else {
                    earthquakeWarningAlertActivity.showArriveCard();
                    earthquakeWarningAlertActivity.mTimer.cancel();
                    return;
                }
                EarthquakeWarningAlertActivity.access$010(earthquakeWarningAlertActivity);
            }
        }
    }

    static /* synthetic */ int access$010(EarthquakeWarningAlertActivity earthquakeWarningAlertActivity) {
        int i = earthquakeWarningAlertActivity.mCountDown;
        earthquakeWarningAlertActivity.mCountDown = i - 1;
        return i;
    }

    private void callPhone(String str) {
        Intent intent = new Intent("android.intent.action.CALL");
        intent.setData(Uri.parse("tel:" + str));
        startActivity(intent);
    }

    private void countDownNumber() {
        if (this.mTimer == null) {
            this.mTimer = new Timer();
            this.mTask = new TimerTask() {
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    EarthquakeWarningAlertActivity.this.mHandler.sendMessage(message);
                }
            };
            this.mTimer.schedule(this.mTask, 0, 1000);
        }
    }

    public static void enableStatusBar(Context context, boolean z) {
        ((StatusBarManager) context.getSystemService("statusbar")).disable(!z ? 18939904 : 0);
    }

    public static boolean handleKeyEvent(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        return keyCode == 24 || keyCode == 25 || keyCode == 27 || keyCode == 80 || keyCode == 164;
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [android.content.Context, miui.app.Activity, com.miui.earthquakewarning.ui.EarthquakeWarningAlertActivity] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x01bc  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x01da  */
    /* JADX WARNING: Removed duplicated region for block: B:33:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initData() {
        /*
            r6 = this;
            android.view.Window r0 = r6.getWindow()
            android.view.View r0 = r0.getDecorView()
            com.miui.earthquakewarning.ui.EarthquakeWarningAlertActivity$2 r1 = new com.miui.earthquakewarning.ui.EarthquakeWarningAlertActivity$2
            r1.<init>()
            r2 = 500(0x1f4, double:2.47E-321)
            r0.postDelayed(r1, r2)
            android.widget.TextView r0 = r6.mDistanceText
            r1 = 1
            java.lang.Object[] r2 = new java.lang.Object[r1]
            java.lang.Object[] r3 = new java.lang.Object[r1]
            com.miui.earthquakewarning.model.UserQuakeItem r4 = r6.mUserQuakeItem
            float r4 = r4.getDistance()
            java.lang.Float r4 = java.lang.Float.valueOf(r4)
            r5 = 0
            r3[r5] = r4
            java.lang.String r4 = "%.0f"
            java.lang.String r3 = java.lang.String.format(r4, r3)
            r2[r5] = r3
            r3 = 2131756022(0x7f1003f6, float:1.914294E38)
            java.lang.String r2 = r6.getString(r3, r2)
            r0.setText(r2)
            android.widget.TextView r0 = r6.mAlertCityText
            com.miui.earthquakewarning.model.UserQuakeItem r2 = r6.mUserQuakeItem
            com.miui.earthquakewarning.model.LocationModel r2 = r2.getEpiLocation()
            java.lang.String r2 = r2.getPlace()
            r0.setText(r2)
            android.widget.TextView r0 = r6.mAlertLevelText
            java.lang.Object[] r2 = new java.lang.Object[r1]
            com.miui.earthquakewarning.model.UserQuakeItem r3 = r6.mUserQuakeItem
            float r3 = r3.getMagnitude()
            java.lang.Float r3 = java.lang.Float.valueOf(r3)
            r2[r5] = r3
            java.lang.String r3 = "%.1f"
            java.lang.String r2 = java.lang.String.format(r3, r2)
            r0.setText(r2)
            android.widget.TextView r0 = r6.mHelpIntensity
            java.lang.Object[] r2 = new java.lang.Object[r1]
            com.miui.earthquakewarning.model.UserQuakeItem r4 = r6.mUserQuakeItem
            float r4 = r4.getIntensity()
            java.lang.Float r4 = java.lang.Float.valueOf(r4)
            r2[r5] = r4
            java.lang.String r2 = java.lang.String.format(r3, r2)
            r0.setText(r2)
            android.widget.TextView r0 = r6.mHelpCityText
            com.miui.earthquakewarning.model.UserQuakeItem r2 = r6.mUserQuakeItem
            com.miui.earthquakewarning.model.LocationModel r2 = r2.getEpiLocation()
            java.lang.String r2 = r2.getPlace()
            r0.setText(r2)
            android.widget.TextView r0 = r6.mHelpLevelText
            java.lang.Object[] r2 = new java.lang.Object[r1]
            com.miui.earthquakewarning.model.UserQuakeItem r4 = r6.mUserQuakeItem
            float r4 = r4.getMagnitude()
            java.lang.Float r4 = java.lang.Float.valueOf(r4)
            r2[r5] = r4
            java.lang.String r2 = java.lang.String.format(r3, r2)
            r0.setText(r2)
            android.widget.TextView r0 = r6.mAlertIntensity
            java.lang.Object[] r2 = new java.lang.Object[r1]
            com.miui.earthquakewarning.model.UserQuakeItem r4 = r6.mUserQuakeItem
            float r4 = r4.getIntensity()
            java.lang.Float r4 = java.lang.Float.valueOf(r4)
            r2[r5] = r4
            java.lang.String r2 = java.lang.String.format(r3, r2)
            r0.setText(r2)
            com.miui.earthquakewarning.ui.EarthquakeWarningAlertActivity$3 r0 = new com.miui.earthquakewarning.ui.EarthquakeWarningAlertActivity$3
            r0.<init>()
            r0.start()
            com.miui.earthquakewarning.model.UserQuakeItem r0 = r6.mUserQuakeItem
            float r0 = r0.getIntensity()
            r2 = 1077936128(0x40400000, float:3.0)
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x00f3
            android.widget.TextView r0 = r6.mAlertFeelText
            r2 = 2131756027(0x7f1003fb, float:1.914295E38)
            java.lang.String r3 = r6.getString(r2)
            r0.setText(r3)
            android.widget.TextView r0 = r6.mHelpFeelText
            java.lang.String r2 = r6.getString(r2)
            r0.setText(r2)
            android.view.View r0 = r6.mAlertCard
            r2 = 2131231118(0x7f08018e, float:1.8078308E38)
            r0.setBackgroundResource(r2)
            android.view.View r0 = r6.mHelpCard
            r0.setBackgroundResource(r2)
            android.widget.ImageView r0 = r6.mAlertIcon
            r2 = 2131231131(0x7f08019b, float:1.8078334E38)
        L_0x00ef:
            r0.setImageResource(r2)
            goto L_0x014f
        L_0x00f3:
            com.miui.earthquakewarning.model.UserQuakeItem r0 = r6.mUserQuakeItem
            float r0 = r0.getIntensity()
            r2 = 1084227584(0x40a00000, float:5.0)
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x0127
            android.widget.TextView r0 = r6.mAlertFeelText
            r2 = 2131756025(0x7f1003f9, float:1.9142946E38)
            java.lang.String r3 = r6.getString(r2)
            r0.setText(r3)
            android.widget.TextView r0 = r6.mHelpFeelText
            java.lang.String r2 = r6.getString(r2)
            r0.setText(r2)
            android.view.View r0 = r6.mAlertCard
            r2 = 2131231117(0x7f08018d, float:1.8078306E38)
            r0.setBackgroundResource(r2)
            android.view.View r0 = r6.mHelpCard
            r0.setBackgroundResource(r2)
            android.widget.ImageView r0 = r6.mAlertIcon
            r2 = 2131231129(0x7f080199, float:1.807833E38)
            goto L_0x00ef
        L_0x0127:
            android.widget.TextView r0 = r6.mAlertFeelText
            r2 = 2131756024(0x7f1003f8, float:1.9142944E38)
            java.lang.String r3 = r6.getString(r2)
            r0.setText(r3)
            android.widget.TextView r0 = r6.mHelpFeelText
            java.lang.String r2 = r6.getString(r2)
            r0.setText(r2)
            android.view.View r0 = r6.mAlertCard
            r2 = 2131231116(0x7f08018c, float:1.8078304E38)
            r0.setBackgroundResource(r2)
            android.view.View r0 = r6.mHelpCard
            r0.setBackgroundResource(r2)
            android.widget.ImageView r0 = r6.mAlertIcon
            r2 = 2131231130(0x7f08019a, float:1.8078332E38)
            goto L_0x00ef
        L_0x014f:
            com.miui.earthquakewarning.model.UserQuakeItem r0 = r6.mUserQuakeItem
            int r0 = r0.getType()
            if (r0 != 0) goto L_0x0164
            android.widget.TextView r0 = r6.mAlertTitle
            r2 = 2131756040(0x7f100408, float:1.9142976E38)
        L_0x015c:
            java.lang.String r2 = r6.getString(r2)
            r0.setText(r2)
            goto L_0x0190
        L_0x0164:
            com.miui.earthquakewarning.model.UserQuakeItem r0 = r6.mUserQuakeItem
            int r0 = r0.getType()
            if (r0 != r1) goto L_0x0172
            android.widget.TextView r0 = r6.mAlertTitle
            r2 = 2131756042(0x7f10040a, float:1.914298E38)
            goto L_0x015c
        L_0x0172:
            com.miui.earthquakewarning.model.UserQuakeItem r0 = r6.mUserQuakeItem
            int r0 = r0.getType()
            r2 = 2
            if (r0 != r2) goto L_0x0181
            android.widget.TextView r0 = r6.mAlertTitle
            r2 = 2131756043(0x7f10040b, float:1.9142982E38)
            goto L_0x015c
        L_0x0181:
            com.miui.earthquakewarning.model.UserQuakeItem r0 = r6.mUserQuakeItem
            int r0 = r0.getType()
            r2 = 3
            if (r0 != r2) goto L_0x0190
            android.widget.TextView r0 = r6.mAlertTitle
            r2 = 2131756041(0x7f100409, float:1.9142978E38)
            goto L_0x015c
        L_0x0190:
            muteAudioFocus(r6, r1)
            com.miui.earthquakewarning.utils.NotificationUtil.setMaxVolume(r6)
            com.miui.earthquakewarning.utils.NotificationUtil.setBrightness(r6)
            com.miui.earthquakewarning.model.UserQuakeItem r0 = r6.mUserQuakeItem
            int r0 = r0.getCountdown()
            r6.mCountDown = r0
            r6.countDownNumber()
            com.miui.earthquakewarning.soundplay.PlaySound r0 = r6.mPlaySound
            com.miui.earthquakewarning.model.UserQuakeItem r1 = r6.mUserQuakeItem
            int r1 = r1.getCountdown()
            com.miui.earthquakewarning.model.UserQuakeItem r2 = r6.mUserQuakeItem
            float r2 = r2.getIntensity()
            r0.play(r1, r2)
            com.miui.earthquakewarning.utils.VibratorUtil.cancel(r6)
            int r0 = r6.mCountDown
            if (r0 <= 0) goto L_0x01ce
            int r0 = r0 + 13
            long[] r0 = new long[r0]
            r1 = r5
        L_0x01c1:
            int r2 = r0.length
            if (r1 >= r2) goto L_0x01cb
            r2 = 1000(0x3e8, double:4.94E-321)
            r0[r1] = r2
            int r1 = r1 + 1
            goto L_0x01c1
        L_0x01cb:
            com.miui.earthquakewarning.utils.VibratorUtil.vibrate(r6, r0, r5)
        L_0x01ce:
            com.miui.earthquakewarning.model.UserQuakeItem r0 = r6.mUserQuakeItem
            java.lang.String r0 = r0.getSignatureText()
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x01e3
            com.miui.earthquakewarning.model.UserQuakeItem r0 = r6.mUserQuakeItem
            java.lang.String r0 = r0.getSignatureText()
            r6.updateFromText(r0)
        L_0x01e3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.earthquakewarning.ui.EarthquakeWarningAlertActivity.initData():void");
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, android.view.View$OnClickListener, miui.app.Activity, com.miui.earthquakewarning.ui.EarthquakeWarningAlertActivity] */
    private void initView() {
        this.mWarningClose = (TextView) findViewById(R.id.warning_close);
        this.mAlertTitle = (TextView) findViewById(R.id.alert_title);
        this.mSecondsText = (ScoreTextView) findViewById(R.id.seconds);
        this.mDistanceText = (TextView) findViewById(R.id.distance_text);
        this.mAlertCityText = (TextView) findViewById(R.id.alert_city_text);
        this.mAlertLevelText = (TextView) findViewById(R.id.alert_level_text);
        this.mAlertFeelText = (TextView) findViewById(R.id.alert_feel_text);
        this.mAlertIntensity = (TextView) findViewById(R.id.alert_intensity);
        this.mHelpIntensity = (TextView) findViewById(R.id.help_intensity);
        this.mHelpCityText = (TextView) findViewById(R.id.help_city_text);
        this.mHelpLevelText = (TextView) findViewById(R.id.help_level_text);
        this.mHelpFeelText = (TextView) findViewById(R.id.help_feel_text);
        this.mViewCallPhone = (LinearLayout) findViewById(R.id.view_call_phone);
        this.mViewShowEmergency = (LinearLayout) findViewById(R.id.view_show_emergency);
        this.mAlertCard = findViewById(R.id.alert_card);
        this.mHelpCard = findViewById(R.id.help_card);
        this.mSafePlaceCard = findViewById(R.id.safe_place_card);
        this.mOtherCard = findViewById(R.id.other_card);
        this.mAlertIcon = (ImageView) findViewById(R.id.alert_icon);
        this.mHelpAlarm = (ImageView) findViewById(R.id.iv_help_alarm);
        this.alertTips = (TextView) findViewById(R.id.alert_tips);
        this.mHelpPlaceNavi = (Button) findViewById(R.id.help_place_navi);
        this.mAlertFromText = (TextView) findViewById(R.id.alert_from_text);
        this.mHelpFromText = (TextView) findViewById(R.id.help_from_text);
        this.mArriveText = (TextView) findViewById(R.id.arrive_text);
        this.mHelpEarthquakeText = (TextView) findViewById(R.id.help_earthquake_text);
        this.mHelpSafeText = (TextView) findViewById(R.id.help_safe_text);
        this.mWarningClose.setOnClickListener(this);
        this.mViewCallPhone.setOnClickListener(this);
        this.mHelpPlaceNavi.setOnClickListener(this);
        this.mViewShowEmergency.setOnClickListener(this);
        this.mPlaySound = new PlaySound(this);
        this.mSecondsText.setTypeface(TypefaceHelper.getMitypeNumber2Typeface(this));
        this.mAlertLevelText.setTypeface(TypefaceHelper.getMitypeNumber1Typeface(this));
        this.mHelpLevelText.setTypeface(TypefaceHelper.getMitypeNumber1Typeface(this));
        this.mAlertIntensity.setTypeface(TypefaceHelper.getMitypeNumber1Typeface(this));
        this.mHelpIntensity.setTypeface(TypefaceHelper.getMitypeNumber1Typeface(this));
    }

    public static boolean isGestureNavBar(Context context) {
        Boolean bool = (Boolean) C0384o.a("android.provider.MiuiSettings$Global", "getBoolean", context.getContentResolver(), (String) C0384o.b("android.provider.MiuiSettings$Global", "FORCE_FSG_NAV_BAR"));
        if (bool != null) {
            return bool.booleanValue();
        }
        return false;
    }

    public static boolean muteAudioFocus(Context context, boolean z) {
        if (context == null) {
            return false;
        }
        AudioManager audioManager = (AudioManager) context.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        if (z) {
            if (audioManager.requestAudioFocus((AudioManager.OnAudioFocusChangeListener) null, 3, 2) != 1) {
                return false;
            }
        } else if (audioManager.abandonAudioFocus((AudioManager.OnAudioFocusChangeListener) null) != 1) {
            return false;
        }
        return true;
    }

    private void resetStatus() {
        PlaySound playSound = this.mPlaySound;
        if (playSound != null) {
            playSound.stop();
        }
        Timer timer = this.mTimer;
        if (timer != null) {
            timer.cancel();
            this.mTimer = null;
        }
        showWarningCard();
    }

    public static void setGestureNavBar(Context context) {
        try {
            e.a(Class.forName("android.provider.MiuiSettings$Global"), Boolean.TYPE, "putBoolean", (Class<?>[]) new Class[]{ContentResolver.class, String.class, Boolean.TYPE}, context.getContentResolver(), "FORCE_FSG_NAV_BAR", false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        } catch (ClassNotFoundException e4) {
            e4.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void showArriveCard() {
        this.mAlertCard.setVisibility(8);
        this.mHelpCard.setVisibility(0);
        this.mSafePlaceCard.setVisibility(0);
        this.mOtherCard.setVisibility(0);
        this.alertTips.setText(Html.fromHtml(getString(R.string.ew_alert_help_tips)));
        this.mHelpAlarm.setVisibility(8);
        String format = new SimpleDateFormat(WarningCenterAlertAdapter.FORMAT_TIME).format(Long.valueOf(this.mUserQuakeItem.getStartTime() + ((long) (this.mUserQuakeItem.getCountTruth() * 1000))));
        this.mHelpEarthquakeText.setText(getString(R.string.ew_arrive_text_tips, new Object[]{format}));
        new Thread() {
            public void run() {
                LocationUtils.getGeoArea(EarthquakeWarningAlertActivity.this.mContext, EarthquakeWarningAlertActivity.this.mUserQuakeItem.getLocation().getLatitude(), EarthquakeWarningAlertActivity.this.mUserQuakeItem.getLocation().getLongitude(), new LocationUtils.AreaResultListener() {
                    public void areaFail() {
                    }

                    public void areaSuccess(final Address address) {
                        EarthquakeWarningAlertActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                TextView access$1200;
                                StringBuilder sb;
                                String subLocality;
                                String subLocality2;
                                if (TextUtils.isEmpty(address.getSubLocality())) {
                                    access$1200 = EarthquakeWarningAlertActivity.this.mHelpSafeText;
                                    sb = new StringBuilder();
                                    sb.append(address.getSubAdminArea());
                                    subLocality = address.getLocality();
                                } else if (TextUtils.isEmpty(address.getLocality())) {
                                    access$1200 = EarthquakeWarningAlertActivity.this.mHelpSafeText;
                                    subLocality2 = address.getSubLocality();
                                    access$1200.setText(subLocality2);
                                } else {
                                    access$1200 = EarthquakeWarningAlertActivity.this.mHelpSafeText;
                                    sb = new StringBuilder();
                                    sb.append(address.getLocality());
                                    subLocality = address.getSubLocality();
                                }
                                sb.append(subLocality);
                                subLocality2 = sb.toString();
                                access$1200.setText(subLocality2);
                            }
                        });
                    }
                });
            }
        }.start();
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, miui.app.Activity, com.miui.earthquakewarning.ui.EarthquakeWarningAlertActivity] */
    private void showEmergencyCard() {
        if (Utils.isEmergencyInfoEmpty()) {
            showToast(getString(R.string.ew_alert_no_emergency));
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, EarthquakeWarningEmergencyActivity.class);
        startActivity(intent);
    }

    /* access modifiers changed from: private */
    public void showHelpCard() {
        this.mAlertCard.setVisibility(8);
        this.mHelpCard.setVisibility(0);
        this.mSafePlaceCard.setVisibility(0);
        this.mOtherCard.setVisibility(0);
        this.alertTips.setText(Html.fromHtml(getString(R.string.ew_alert_help_tips)));
        this.mHelpAlarm.setVisibility(0);
        this.mHelpEarthquakeText.setText(getString(R.string.ew_alert_help_earthquake_title));
        this.mHelpSafeText.setText(getString(R.string.ew_alert_help_safe_title));
    }

    private void showToast(String str) {
        Toast makeText = Toast.makeText(this.mContext, str, 0);
        try {
            ((WindowManager.LayoutParams) makeText.getClass().getDeclaredMethod("getWindowParams", new Class[0]).invoke(makeText, new Object[0])).flags |= 524288;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
        makeText.show();
    }

    /* access modifiers changed from: private */
    public void showWarningCard() {
        this.mAlertCard.setVisibility(0);
        this.mHelpCard.setVisibility(8);
        this.mSafePlaceCard.setVisibility(8);
        this.mOtherCard.setVisibility(8);
        this.alertTips.setText(Html.fromHtml(getString(R.string.ew_alert_help_tips)));
    }

    /* access modifiers changed from: private */
    public void turnOnScreen() {
        PowerManager.WakeLock newWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(268435466, TAG);
        newWakeLock.acquire();
        newWakeLock.release();
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, miui.app.Activity, com.miui.earthquakewarning.ui.EarthquakeWarningAlertActivity] */
    private void updateEWServiceStatus(boolean z) {
        Intent intent = new Intent(this, EarthquakeWarningService.class);
        intent.setAction("updatePlayingStatus");
        intent.putExtra("playing", z);
        startService(intent);
    }

    private void updateFromText(String str) {
        this.mAlertFromText.setText(getString(R.string.ew_alert_text_from, new Object[]{str}));
        this.mHelpFromText.setText(getString(R.string.ew_alert_text_from, new Object[]{str}));
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, miui.app.Activity, com.miui.earthquakewarning.ui.EarthquakeWarningAlertActivity] */
    /* access modifiers changed from: private */
    public void updateLastCount(int i) {
        String quantityString = getResources().getQuantityString(R.plurals.ew_alert_second_after, i, new Object[]{Integer.valueOf(i)});
        SpannableString spannableString = new SpannableString(quantityString);
        int indexOf = quantityString.indexOf(String.valueOf(i));
        spannableString.setSpan(new TextAppearanceSpan(this, R.style.StyleAlertNumberblack), indexOf, String.valueOf(i).length() + indexOf, 33);
        this.mSecondsText.setText(spannableString);
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (handleKeyEvent(keyEvent)) {
            return true;
        }
        return EarthquakeWarningAlertActivity.super.dispatchKeyEvent(keyEvent);
    }

    public void onBackPressed() {
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, miui.app.Activity, com.miui.earthquakewarning.ui.EarthquakeWarningAlertActivity] */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.help_place_navi /*2131296962*/:
                AnalyticHelper.trackAlertResultActionModuleClick(AnalyticHelper.ALERT_SAFE_PLACE);
                EarthquakeWarningManager.getInstance().searchSafePlace(this);
                return;
            case R.id.view_call_phone /*2131298024*/:
                AnalyticHelper.trackAlertResultActionModuleClick(AnalyticHelper.ALERT_CALL);
                String contact = Utils.getContact();
                if (TextUtils.isEmpty(contact)) {
                    showToast(getString(R.string.ew_alert_no_contact));
                    return;
                } else {
                    callPhone(contact);
                    return;
                }
            case R.id.view_show_emergency /*2131298033*/:
                AnalyticHelper.trackAlertResultActionModuleClick(AnalyticHelper.ALERT_EMERGENCY);
                showEmergencyCard();
                return;
            case R.id.warning_close /*2131298044*/:
                AnalyticHelper.trackAlertResultActionModuleClick(AnalyticHelper.ALERT_CLOSE);
                VibratorUtil.cancel(this);
                finish();
                return;
            default:
                return;
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [b.b.c.c.a, android.content.Context, miui.app.Activity, com.miui.earthquakewarning.ui.EarthquakeWarningAlertActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Window window = getWindow();
        getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ew_alert_bg_color)));
        getWindow().setLayout(-1, -1);
        getWindow().addFlags(4);
        window.addFlags(6815873);
        setContentView(R.layout.earthquake_warning_activity_alert);
        if (Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(1);
        }
        if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            finish();
            return;
        }
        this.mContext = this;
        this.mHandler = new MyHandler(this);
        this.mUserQuakeItem = (UserQuakeItem) getIntent().getSerializableExtra("UserQuakeItem");
        if (this.mUserQuakeItem == null) {
            finish();
            return;
        }
        this.isPreviousGestureNav = isGestureNavBar(this);
        initView();
        initData();
        updateEWServiceStatus(true);
        AnalyticHelper.trackAlertResultActionModuleClick(AnalyticHelper.ALERT_SHOW);
        if (this.mUserQuakeItem.getCountdown() >= 0) {
            AnalyticHelper.trackWarningTime(this.mUserQuakeItem.getCountdown());
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, miui.app.Activity, com.miui.earthquakewarning.ui.EarthquakeWarningAlertActivity] */
    /* access modifiers changed from: protected */
    public void onDestroy() {
        EarthquakeWarningAlertActivity.super.onDestroy();
        PlaySound playSound = this.mPlaySound;
        if (playSound != null) {
            playSound.stop();
        }
        Timer timer = this.mTimer;
        if (timer != null) {
            timer.cancel();
            this.mTimer = null;
        }
        NotificationUtil.resetVolume(this);
        NotificationUtil.resetBrightness(this);
        NotificationUtil.resetGPS(this);
        Settings.Secure.putInt(getContentResolver(), "screen_buttons_state", 0);
        enableStatusBar(this, true);
        if (this.isPreviousGestureNav) {
            Settings.Global.putInt(getContentResolver(), "force_fsg_nav_bar", 1);
        }
        this.isPreviousGestureNav = false;
        updateEWServiceStatus(false);
        PlaySound playSound2 = this.mPlaySound;
        if (playSound2 != null) {
            playSound2.release();
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        EarthquakeWarningAlertActivity.super.onNewIntent(intent);
        this.mUserQuakeItem = (UserQuakeItem) intent.getSerializableExtra("UserQuakeItem");
        if (this.mUserQuakeItem == null) {
            finish();
            return;
        }
        resetStatus();
        initData();
        updateEWServiceStatus(true);
        if (this.mUserQuakeItem.getCountdown() >= 0) {
            AnalyticHelper.trackWarningTime(this.mUserQuakeItem.getCountdown());
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [b.b.c.c.a, android.content.Context, miui.app.Activity, com.miui.earthquakewarning.ui.EarthquakeWarningAlertActivity] */
    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        enableStatusBar(this, false);
        if (this.isPreviousGestureNav) {
            Settings.Global.putInt(getContentResolver(), "force_fsg_nav_bar", 0);
        }
        Settings.Secure.putInt(getContentResolver(), "screen_buttons_state", 1);
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, miui.app.Activity, com.miui.earthquakewarning.ui.EarthquakeWarningAlertActivity] */
    /* access modifiers changed from: protected */
    public void onStop() {
        EarthquakeWarningAlertActivity.super.onStop();
        Settings.Secure.putInt(getContentResolver(), "screen_buttons_state", 0);
        enableStatusBar(this, true);
        if (this.isPreviousGestureNav) {
            Settings.Global.putInt(getContentResolver(), "force_fsg_nav_bar", 1);
        }
        muteAudioFocus(this, false);
    }
}
