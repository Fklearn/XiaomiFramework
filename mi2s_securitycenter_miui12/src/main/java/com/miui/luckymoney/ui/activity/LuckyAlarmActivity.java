package com.miui.luckymoney.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.c.j.i;
import com.miui.activityutil.o;
import com.miui.common.customview.MovableLayout;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.utils.DateUtil;
import com.miui.luckymoney.utils.ImageUtil;
import com.miui.luckymoney.utils.PackageUtil;
import com.miui.luckymoney.utils.ScreenUtil;
import com.miui.luckymoney.webapi.LuckyAlarmResult;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import com.miui.warningcenter.WarningCenterAlertAdapter;
import com.xiaomi.stat.d;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import miui.app.Activity;

public class LuckyAlarmActivity extends BaseMiuiActivity {
    public static String PACKAGENAME = "packageName";
    private static int REQUESTCODE_SETTINGS = 1;
    public static int RESULTCODE_RETURN = 204;
    private View back;
    /* access modifiers changed from: private */
    public CountDownTimer countDownTimer;
    /* access modifiers changed from: private */
    public ImageView imgLuckyAlarmIcon;
    /* access modifiers changed from: private */
    public Adapter listAdapter;
    /* access modifiers changed from: private */
    public ArrayList<LuckyAlarmResult.AlarmItem> listItem;
    /* access modifiers changed from: private */
    public ListView listView;
    private MovableLayout movableLayout;
    /* access modifiers changed from: private */
    public View.OnClickListener onBackClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            LuckyAlarmActivity.this.finish();
        }
    };
    private MovableLayout.b onScrollListener;
    private View.OnClickListener onSettingClickListener = new View.OnClickListener() {
        /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.luckymoney.ui.activity.LuckyAlarmActivity] */
        /* JADX WARNING: type inference failed for: r0v4, types: [android.content.Context, com.miui.luckymoney.ui.activity.LuckyAlarmActivity] */
        public void onClick(View view) {
            Intent intent = new Intent(LuckyAlarmActivity.this, LuckyAlarmSettingActivity.class);
            HashSet hashSet = new HashSet();
            Iterator it = LuckyAlarmActivity.this.listItem.iterator();
            while (it.hasNext()) {
                hashSet.add(PackageUtil.getPackageNameFromIntent(((LuckyAlarmResult.AlarmItem) it.next()).intent));
            }
            intent.putExtra(LuckyAlarmActivity.PACKAGENAME, (String[]) hashSet.toArray(new String[0]));
            g.b((Context) LuckyAlarmActivity.this, intent, B.b());
        }
    };
    private View setting;
    /* access modifiers changed from: private */
    public TextView txvBack;
    /* access modifiers changed from: private */
    public TextView txvLuckyAlarmApp;
    /* access modifiers changed from: private */
    public TextView txvLuckyAlarmCountDown;

    private class Adapter extends ArrayAdapter<LuckyAlarmResult.AlarmItem> {

        class ViewHolder {
            Button btnEnter;
            View divider;
            ImageView imgIconItem;
            ImageView imgTimeLineItem;
            TextView txvAppName;
            TextView txvClosed;
            TextView txvDayItem;
            TextView txvMonthItem;

            ViewHolder() {
            }
        }

        public Adapter(Context context, ArrayList<LuckyAlarmResult.AlarmItem> arrayList) {
            super(context, 0, arrayList);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            ImageView imageView;
            int i2;
            StringBuilder sb;
            String str;
            final LuckyAlarmResult.AlarmItem alarmItem = (LuckyAlarmResult.AlarmItem) getItem(i);
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_lucky_alarm, viewGroup, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.txvAppName = (TextView) view.findViewById(R.id.txvTimeLineItem);
                viewHolder.txvClosed = (TextView) view.findViewById(R.id.item_txv_has_been_closed);
                viewHolder.divider = view.findViewById(R.id.item_divider_view);
                viewHolder.imgIconItem = (ImageView) view.findViewById(R.id.imgIconItem);
                viewHolder.imgTimeLineItem = (ImageView) view.findViewById(R.id.imgTimeLineItem);
                viewHolder.txvDayItem = (TextView) view.findViewById(R.id.txvDayItem);
                viewHolder.txvMonthItem = (TextView) view.findViewById(R.id.txvMonthItem);
                viewHolder.btnEnter = (Button) view.findViewById(R.id.btnEnter);
                view.setTag(viewHolder);
            }
            ViewHolder viewHolder2 = (ViewHolder) view.getTag();
            String charSequence = DateFormat.format(WarningCenterAlertAdapter.FORMAT_TIME, alarmItem.time).toString();
            TextView textView = viewHolder2.txvAppName;
            textView.setText(charSequence + " " + alarmItem.activityName);
            viewHolder2.imgIconItem.setImageBitmap(alarmItem.appIcon);
            viewHolder2.btnEnter.setVisibility(8);
            viewHolder2.btnEnter.setOnClickListener(new View.OnClickListener() {
                /* JADX WARNING: type inference failed for: r3v2, types: [android.content.Context, com.miui.luckymoney.ui.activity.LuckyAlarmActivity] */
                public void onClick(View view) {
                    g.b((Context) LuckyAlarmActivity.this, alarmItem.intent, B.b());
                }
            });
            long currentTimeMillis = System.currentTimeMillis();
            if (i == 0) {
                if (alarmItem.time > currentTimeMillis) {
                    imageView = viewHolder2.imgTimeLineItem;
                    i2 = R.drawable.express_icon_first_red;
                } else {
                    imageView = viewHolder2.imgTimeLineItem;
                    i2 = R.drawable.express_icon_first;
                }
            } else if (i == getCount() - 1) {
                if (((LuckyAlarmResult.AlarmItem) getItem(i - 1)).time >= currentTimeMillis || alarmItem.time <= currentTimeMillis) {
                    imageView = viewHolder2.imgTimeLineItem;
                    i2 = R.drawable.express_icon_last;
                } else {
                    imageView = viewHolder2.imgTimeLineItem;
                    i2 = R.drawable.express_icon_last_red;
                }
            } else if (alarmItem.time <= currentTimeMillis || ((LuckyAlarmResult.AlarmItem) getItem(i - 1)).time >= currentTimeMillis || ((LuckyAlarmResult.AlarmItem) getItem(i + 1)).time <= currentTimeMillis) {
                imageView = viewHolder2.imgTimeLineItem;
                i2 = R.drawable.express_icon_middle;
            } else {
                imageView = viewHolder2.imgTimeLineItem;
                i2 = R.drawable.express_icon_middle_red;
            }
            imageView.setImageResource(i2);
            if (currentTimeMillis <= alarmItem.time || currentTimeMillis >= alarmItem.endTime) {
                viewHolder2.btnEnter.setVisibility(8);
            } else {
                viewHolder2.btnEnter.setVisibility(0);
            }
            if (alarmItem.endTime < currentTimeMillis) {
                viewHolder2.divider.setVisibility(0);
                viewHolder2.txvClosed.setVisibility(0);
                viewHolder2.txvAppName.setTextColor(LuckyAlarmActivity.this.getResources().getColor(R.color.lucky_alarm_list_finish_txtcolor));
                viewHolder2.imgIconItem.setAlpha(0.3984375f);
                viewHolder2.imgIconItem.setOnClickListener((View.OnClickListener) null);
                viewHolder2.txvDayItem.setOnClickListener((View.OnClickListener) null);
                viewHolder2.txvAppName.setOnClickListener((View.OnClickListener) null);
            } else {
                viewHolder2.divider.setVisibility(8);
                viewHolder2.txvClosed.setVisibility(8);
                viewHolder2.txvAppName.setTextColor(LuckyAlarmActivity.this.getResources().getColor(R.color.lucky_alarm_list_normal_txtcolor));
                viewHolder2.imgIconItem.setAlpha(1.0f);
            }
            if (i == 0) {
                sb = new StringBuilder();
            } else if (DateUtil.isTheSameDay(alarmItem.time, ((LuckyAlarmResult.AlarmItem) getItem(i - 1)).time)) {
                str = "";
                viewHolder2.txvMonthItem.setText(str);
                viewHolder2.txvDayItem.setText(str);
                return view;
            } else {
                sb = new StringBuilder();
            }
            sb.append(DateFormat.format("MM", alarmItem.time).toString());
            sb.append("月");
            String sb2 = sb.toString();
            str = DateFormat.format(d.s, alarmItem.time).toString();
            viewHolder2.txvMonthItem.setText(sb2);
            viewHolder2.txvDayItem.setText(str);
            return view;
        }
    }

    private class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long j) {
            super(j, 1000);
        }

        /* JADX WARNING: type inference failed for: r1v12, types: [android.content.Context, com.miui.luckymoney.ui.activity.LuckyAlarmActivity] */
        /* JADX WARNING: type inference failed for: r1v13, types: [android.content.Context, com.miui.luckymoney.ui.activity.LuckyAlarmActivity] */
        public void onFinish() {
            LuckyAlarmActivity.this.txvLuckyAlarmCountDown.setText(R.string.lucky_alarm_count_down_default);
            LuckyAlarmActivity luckyAlarmActivity = LuckyAlarmActivity.this;
            int access$1100 = luckyAlarmActivity.getCurrentCountDownItemIndex(luckyAlarmActivity.listItem);
            final LuckyAlarmResult.AlarmItem alarmItem = access$1100 == -1 ? null : (LuckyAlarmResult.AlarmItem) LuckyAlarmActivity.this.listItem.get(access$1100);
            if (alarmItem == null) {
                g.b((Context) LuckyAlarmActivity.this, new Intent(LuckyAlarmActivity.this, LuckyAlarmActivity.class), B.b());
                LuckyAlarmActivity.this.finish();
                return;
            }
            LuckyAlarmActivity luckyAlarmActivity2 = LuckyAlarmActivity.this;
            CountDownTimer unused = luckyAlarmActivity2.countDownTimer = new MyCountDownTimer((alarmItem.time - System.currentTimeMillis()) + 10);
            LuckyAlarmActivity.this.countDownTimer.start();
            LuckyAlarmActivity.this.txvLuckyAlarmApp.setText(alarmItem.activityName + LuckyAlarmActivity.this.getString(R.string.lucky_alarm_app_tail));
            LuckyAlarmActivity.this.imgLuckyAlarmIcon.setImageBitmap(alarmItem.appIcon);
            LuckyAlarmActivity.this.imgLuckyAlarmIcon.setOnClickListener(new View.OnClickListener() {
                /* JADX WARNING: type inference failed for: r3v2, types: [android.content.Context, com.miui.luckymoney.ui.activity.LuckyAlarmActivity] */
                public void onClick(View view) {
                    g.b((Context) LuckyAlarmActivity.this, alarmItem.intent, B.b());
                }
            });
            LuckyAlarmActivity.this.listAdapter.notifyDataSetChanged();
        }

        public void onTick(long j) {
            Object obj;
            Object obj2;
            long j2 = j / 3600000;
            long j3 = j - (((j2 * 1000) * 60) * 60);
            long j4 = j3 / 60000;
            long j5 = (j3 - ((j4 * 1000) * 60)) / 1000;
            TextView access$800 = LuckyAlarmActivity.this.txvLuckyAlarmCountDown;
            StringBuilder sb = new StringBuilder();
            sb.append(j2);
            sb.append(":");
            if (j4 > 9) {
                obj = Long.valueOf(j4);
            } else {
                obj = o.f2309a + j4;
            }
            sb.append(obj);
            sb.append(":");
            if (j5 > 9) {
                obj2 = Long.valueOf(j5);
            } else {
                obj2 = o.f2309a + j5;
            }
            sb.append(obj2);
            access$800.setText(sb.toString());
        }
    }

    private class OnScrollListener implements MovableLayout.b {
        private float dpAlarmAppMarginTop;
        /* access modifiers changed from: private */
        public float spAlarmApp;
        /* access modifiers changed from: private */
        public float spAlarmCountDown;
        /* access modifiers changed from: private */
        public float spAlarmCountDownToolbar;

        private OnScrollListener() {
            this.spAlarmApp = LuckyAlarmActivity.this.getResources().getDimension(R.dimen.text_lucky_alarm_app_size);
            this.spAlarmCountDown = LuckyAlarmActivity.this.getResources().getDimension(R.dimen.txv_lucky_alarm_count_down_textsize);
            this.spAlarmCountDownToolbar = LuckyAlarmActivity.this.getResources().getDimension(R.dimen.txv_lucky_alarm_count_down_toolbar_textsize);
            this.dpAlarmAppMarginTop = LuckyAlarmActivity.this.getResources().getDimension(R.dimen.txv_lucky_alarm_app_margin_top);
        }

        private float myInterpolator(float f) {
            return (f * 2.0f) - 1.0f;
        }

        private void transformHeader(final float f) {
            if (f == 0.0f) {
                LuckyAlarmActivity.this.txvLuckyAlarmApp.setOnClickListener(LuckyAlarmActivity.this.onBackClickListener);
            }
            if (f == 1.0f) {
                LuckyAlarmActivity.this.txvLuckyAlarmApp.setOnClickListener((View.OnClickListener) null);
            }
            LuckyAlarmActivity.this.imgLuckyAlarmIcon.setAlpha(myInterpolator(f));
            LuckyAlarmActivity.this.imgLuckyAlarmIcon.setScaleX(f);
            LuckyAlarmActivity.this.imgLuckyAlarmIcon.setScaleY(f);
            LuckyAlarmActivity.this.txvBack.setAlpha(myInterpolator(f));
            float f2 = 1.0f - f;
            LuckyAlarmActivity.this.txvLuckyAlarmApp.setTranslationX(((float) (LuckyAlarmActivity.this.txvBack.getLeft() - LuckyAlarmActivity.this.txvLuckyAlarmApp.getLeft())) * f2);
            LuckyAlarmActivity.this.txvLuckyAlarmApp.setTranslationY(((float) (LuckyAlarmActivity.this.txvBack.getTop() - LuckyAlarmActivity.this.txvLuckyAlarmApp.getTop())) * f2);
            LuckyAlarmActivity.this.txvLuckyAlarmApp.post(new Runnable() {
                public void run() {
                    LuckyAlarmActivity.this.txvLuckyAlarmApp.setTextSize(0, OnScrollListener.this.spAlarmApp + ((LuckyAlarmActivity.this.txvBack.getTextSize() - OnScrollListener.this.spAlarmApp) * (1.0f - f)));
                }
            });
            float top = (((float) (LuckyAlarmActivity.this.txvBack.getTop() + LuckyAlarmActivity.this.txvBack.getHeight())) + this.dpAlarmAppMarginTop) - ((float) LuckyAlarmActivity.this.txvLuckyAlarmCountDown.getTop());
            LuckyAlarmActivity.this.txvLuckyAlarmCountDown.setTranslationX(((float) (LuckyAlarmActivity.this.txvBack.getLeft() - LuckyAlarmActivity.this.txvLuckyAlarmCountDown.getLeft())) * f2);
            LuckyAlarmActivity.this.txvLuckyAlarmCountDown.setTranslationY(top * f2);
            LuckyAlarmActivity.this.txvLuckyAlarmCountDown.post(new Runnable() {
                public void run() {
                    LuckyAlarmActivity.this.txvLuckyAlarmCountDown.setTextSize(0, OnScrollListener.this.spAlarmCountDown + ((OnScrollListener.this.spAlarmCountDownToolbar - OnScrollListener.this.spAlarmCountDown) * (1.0f - f)));
                }
            });
        }

        public boolean onContentScrolled() {
            return LuckyAlarmActivity.this.listView.getScrollY() > 0;
        }

        public void onScroll(int i, float f) {
            transformHeader(f);
        }

        public void onStartScroll() {
        }

        public void onStopScroll() {
        }
    }

    private ArrayList<LuckyAlarmResult.AlarmItem> checkItem(ArrayList<LuckyAlarmResult.AlarmItem> arrayList) {
        ArrayList<LuckyAlarmResult.AlarmItem> arrayList2 = new ArrayList<>();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            LuckyAlarmResult.AlarmItem alarmItem = arrayList.get(i);
            if (alarmItem.intent != null && PackageUtil.isIntentExist(getApplicationContext(), alarmItem.intent, (String) null)) {
                arrayList2.add(alarmItem);
                if (alarmItem.appIcon == null) {
                    try {
                        alarmItem.appIcon = ImageUtil.drawableToBitmap(getPackageManager().getActivityIcon(alarmItem.intent));
                    } catch (PackageManager.NameNotFoundException unused) {
                        arrayList2.remove(alarmItem);
                    }
                }
            }
        }
        return arrayList2;
    }

    /* access modifiers changed from: private */
    public int getCurrentCountDownItemIndex(ArrayList<LuckyAlarmResult.AlarmItem> arrayList) {
        int size = arrayList.size();
        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            if (arrayList.get(i).time > currentTimeMillis) {
                return i;
            }
        }
        return -1;
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, miui.app.Activity, com.miui.luckymoney.ui.activity.LuckyAlarmActivity] */
    private void initAfterView() {
        setContentView(R.layout.activity_lucky_alarm_after);
        this.txvBack = (TextView) findViewById(R.id.txvBack);
        this.back = findViewById(R.id.back);
        this.setting = findViewById(R.id.settings);
        this.back.setOnClickListener(this.onBackClickListener);
        this.txvBack.setOnClickListener(this.onBackClickListener);
        this.setting.setOnClickListener(this.onSettingClickListener);
        View findViewById = findViewById(R.id.actionbar);
        if (i.e()) {
            ScreenUtil.setNotchToolbarMarginTop(this, findViewById);
        } else {
            ScreenUtil.setStatusbarMarginTop(this, findViewById);
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, miui.app.Activity, com.miui.luckymoney.ui.activity.LuckyAlarmActivity] */
    private void initBeforeView() {
        setContentView(R.layout.activity_lucky_alarm_before);
        this.txvBack = (TextView) findViewById(R.id.txvBack);
        this.back = findViewById(R.id.back);
        this.setting = findViewById(R.id.settings);
        this.back.setOnClickListener(this.onBackClickListener);
        this.txvBack.setOnClickListener(this.onBackClickListener);
        this.setting.setOnClickListener(this.onSettingClickListener);
        View findViewById = findViewById(R.id.actionbar);
        if (i.e()) {
            ScreenUtil.setNotchToolbarMarginTop(this, findViewById);
        } else {
            ScreenUtil.setStatusbarMarginTop(this, findViewById);
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, miui.app.Activity, com.miui.luckymoney.ui.activity.LuckyAlarmActivity] */
    private void initCloseView() {
        setContentView(R.layout.activity_lucky_alarm_close);
        this.txvBack = (TextView) findViewById(R.id.txvBack);
        this.back = findViewById(R.id.back);
        this.setting = findViewById(R.id.settings);
        this.back.setOnClickListener(this.onBackClickListener);
        this.txvBack.setOnClickListener(this.onBackClickListener);
        this.setting.setOnClickListener(this.onSettingClickListener);
        findViewById(R.id.btnOpen).setOnClickListener(new View.OnClickListener() {
            /* JADX WARNING: type inference failed for: r3v1, types: [android.content.Context, com.miui.luckymoney.ui.activity.LuckyAlarmActivity] */
            /* JADX WARNING: type inference failed for: r0v1, types: [android.content.Context, com.miui.luckymoney.ui.activity.LuckyAlarmActivity] */
            /* JADX WARNING: type inference failed for: r0v2, types: [android.content.Context, com.miui.luckymoney.ui.activity.LuckyAlarmActivity] */
            public void onClick(View view) {
                CommonConfig.getInstance(LuckyAlarmActivity.this).setLuckyAlarmEnable(true);
                g.b((Context) LuckyAlarmActivity.this, new Intent(LuckyAlarmActivity.this, LuckyAlarmActivity.class), B.b());
                LuckyAlarmActivity.this.finish();
            }
        });
        View findViewById = findViewById(R.id.actionbar);
        if (i.e()) {
            ScreenUtil.setNotchToolbarMarginTop(this, findViewById);
        } else {
            ScreenUtil.setStatusbarMarginTop(this, findViewById);
        }
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [android.content.Context, miui.app.Activity, com.miui.luckymoney.ui.activity.LuckyAlarmActivity] */
    private void initDefaultView() {
        setContentView(R.layout.activity_lucky_alarm);
        this.txvBack = (TextView) findViewById(R.id.txvBack);
        this.back = findViewById(R.id.back);
        this.setting = findViewById(R.id.settings);
        View findViewById = findViewById(R.id.header_container);
        if (i.e()) {
            ScreenUtil.setNotchToolbarMarginTop(this, findViewById);
        } else {
            ScreenUtil.setStatusbarMarginTop(this, findViewById);
        }
        this.listView = (ListView) findViewById(R.id.listLuckyAlarm);
        this.movableLayout = (MovableLayout) findViewById(R.id.activity_lucky_alarm);
        this.txvLuckyAlarmApp = (TextView) findViewById(R.id.txvLuckyAlarmApp);
        this.txvLuckyAlarmCountDown = (TextView) findViewById(R.id.txvLuckyAlarmCountDown);
        this.imgLuckyAlarmIcon = (ImageView) findViewById(R.id.imgLuckyAlarmIcon);
        this.back.setOnClickListener(this.onBackClickListener);
        this.txvBack.setOnClickListener(this.onBackClickListener);
        this.setting.setOnClickListener(this.onSettingClickListener);
        this.movableLayout.setScrollable(true);
        final LuckyAlarmResult.AlarmItem alarmItem = null;
        this.onScrollListener = new OnScrollListener();
        this.movableLayout.setScrollListener(this.onScrollListener);
        this.listAdapter = new Adapter(this, this.listItem);
        this.listView.setAdapter(this.listAdapter);
        this.listView.setDivider((Drawable) null);
        this.listView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.item_lucky_alarm_header, this.listView, false));
        final int currentCountDownItemIndex = getCurrentCountDownItemIndex(this.listItem);
        if (currentCountDownItemIndex != -1) {
            alarmItem = this.listItem.get(currentCountDownItemIndex);
        }
        if (alarmItem != null) {
            this.txvLuckyAlarmApp.setText(alarmItem.activityName + getString(R.string.lucky_alarm_app_tail));
            this.imgLuckyAlarmIcon.setImageBitmap(alarmItem.appIcon);
            this.imgLuckyAlarmIcon.setOnClickListener(new View.OnClickListener() {
                /* JADX WARNING: type inference failed for: r3v1, types: [android.content.Context, com.miui.luckymoney.ui.activity.LuckyAlarmActivity] */
                public void onClick(View view) {
                    g.b((Context) LuckyAlarmActivity.this, alarmItem.intent, B.b());
                }
            });
            this.countDownTimer = new MyCountDownTimer((alarmItem.time - System.currentTimeMillis()) + 10);
            this.countDownTimer.start();
            this.listView.post(new Runnable() {
                public void run() {
                    if (LuckyAlarmActivity.this.listView.getLastVisiblePosition() - 2 < currentCountDownItemIndex) {
                        LuckyAlarmActivity.this.listView.setSelection(currentCountDownItemIndex);
                    }
                }
            });
            return;
        }
        ArrayList<LuckyAlarmResult.AlarmItem> arrayList = this.listItem;
        if (arrayList.get(arrayList.size() - 1).endTime >= System.currentTimeMillis()) {
            ArrayList<LuckyAlarmResult.AlarmItem> arrayList2 = this.listItem;
            LuckyAlarmResult.AlarmItem alarmItem2 = arrayList2.get(arrayList2.size() - 1);
            this.txvLuckyAlarmApp.setText(alarmItem2.activityName + getString(R.string.lucky_alarm_app_tail));
            this.imgLuckyAlarmIcon.setImageBitmap(alarmItem2.appIcon);
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == REQUESTCODE_SETTINGS) {
            int i3 = RESULTCODE_RETURN;
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, miui.app.Activity, com.miui.luckymoney.ui.activity.LuckyAlarmActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        LuckyAlarmActivity.super.onCreate(bundle);
        this.listItem = new LuckyAlarmResult(CommonConfig.getInstance(this).getLuckyAlarmConfig(), true).getItems();
        this.listItem = checkItem(this.listItem);
        if (this.listItem.size() == 0) {
            initBeforeView();
            return;
        }
        ArrayList<LuckyAlarmResult.AlarmItem> arrayList = this.listItem;
        if (arrayList.get(arrayList.size() - 1).endTime < System.currentTimeMillis()) {
            initAfterView();
        } else if (!CommonConfig.getInstance(this).getLuckyAlarmEnable()) {
            initCloseView();
        } else {
            initDefaultView();
            k.a((Activity) this);
        }
    }
}
