package com.miui.earthquakewarning.ui;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.c.c.b.d;
import com.miui.earthquakewarning.analytics.AnalyticHelper;
import com.miui.earthquakewarning.utils.UserNoticeUtil;
import com.miui.securitycenter.R;
import java.util.Locale;
import miui.os.Build;

public class EarthquakeWarningGuideLawFragment extends d implements View.OnClickListener {
    /* access modifiers changed from: private */
    public boolean isDark;
    private Listener listener;
    private Button mDone;
    private Button mExit;
    private boolean mFromGuide;
    private ListView mListView;
    private RelativeLayout mRoot;

    public interface Listener {
        void onCancelCallback();

        void onCompleteCallback();
    }

    class MyAdapter extends ArrayAdapter {
        private int res;

        public MyAdapter(Context context, int i) {
            super(context, i);
            this.res = i;
        }

        public int getCount() {
            return 1;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View inflate = EarthquakeWarningGuideLawFragment.this.mActivity.getLayoutInflater().inflate(this.res, (ViewGroup) null);
            TextView textView = (TextView) inflate.findViewById(R.id.statement_text);
            textView.setText(EarthquakeWarningGuideLawFragment.this.getClickableHtml(Html.fromHtml(UserNoticeUtil.getStatementMessage(getContext()))));
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            return inflate;
        }
    }

    /* access modifiers changed from: private */
    public CharSequence getClickableHtml(Spanned spanned) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spanned);
        for (URLSpan linkClickable : (URLSpan[]) spannableStringBuilder.getSpans(0, spanned.length(), URLSpan.class)) {
            setLinkClickable(spannableStringBuilder, linkClickable);
        }
        return spannableStringBuilder;
    }

    private void setLinkClickable(SpannableStringBuilder spannableStringBuilder, URLSpan uRLSpan) {
        spannableStringBuilder.setSpan(new ClickableSpan() {
            public void onClick(View view) {
                boolean access$200 = EarthquakeWarningGuideLawFragment.this.isDark;
                EarthquakeWarningGuideLawFragment.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://cdn.cnbj1.fds.api.mi-img.com/quake-warn/index.html?region=" + Build.getRegion() + "&lang=" + Locale.getDefault().toString() + "&dark=" + (access$200 ? 1 : 0))));
            }
        }, spannableStringBuilder.getSpanStart(uRLSpan), spannableStringBuilder.getSpanEnd(uRLSpan), spannableStringBuilder.getSpanFlags(uRLSpan));
    }

    /* access modifiers changed from: protected */
    public void initView() {
        int i;
        Button button;
        this.mRoot = (RelativeLayout) findViewById(R.id.root);
        this.mDone = (Button) findViewById(R.id.done);
        this.mExit = (Button) findViewById(R.id.exit);
        this.mListView = (ListView) findViewById(R.id.listview);
        if (this.mFromGuide) {
            this.mDone.setText(getString(R.string.ew_guide_law_done));
            this.mDone.setTextColor(getResources().getColor(R.color.ew_guide_text_title_color));
            button = this.mExit;
            i = 8;
        } else {
            this.mDone.setText(getString(R.string.ew_guide_law_agree));
            button = this.mExit;
            i = 0;
        }
        button.setVisibility(i);
        this.mDone.setOnClickListener(this);
        this.mExit.setOnClickListener(this);
        this.mRoot.setOnClickListener(this);
        this.mListView.setAdapter(new MyAdapter(this.mActivity, R.layout.earthquake_warning_item_guide_law));
    }

    public void onClick(View view) {
        String str;
        int id = view.getId();
        if (id == R.id.done) {
            Listener listener2 = this.listener;
            if (listener2 != null) {
                listener2.onCompleteCallback();
                str = AnalyticHelper.GUIDE_CLICK_NEXT;
            } else {
                return;
            }
        } else if (id == R.id.exit) {
            Listener listener3 = this.listener;
            if (listener3 != null) {
                listener3.onCancelCallback();
                str = AnalyticHelper.GUIDE_CLICK_DISAGREE;
            } else {
                return;
            }
        } else if (id == R.id.root) {
            getActivity().finish();
            return;
        } else {
            return;
        }
        AnalyticHelper.trackGuide4ActionModuleClick(str);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mFromGuide = arguments.getBoolean("mFromGuide");
            this.isDark = arguments.getBoolean("isDark");
        }
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.earthquake_warning_fragment_guide_law;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void setListener(Listener listener2) {
        this.listener = listener2;
    }
}
