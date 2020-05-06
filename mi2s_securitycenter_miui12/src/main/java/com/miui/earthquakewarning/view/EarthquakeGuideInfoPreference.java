package com.miui.earthquakewarning.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import androidx.preference.A;
import androidx.preference.Preference;
import com.miui.earthquakewarning.analytics.AnalyticHelper;
import com.miui.earthquakewarning.ui.EarthquakeWarningGuideSimpleActivity;
import com.miui.securitycenter.R;
import java.util.Locale;
import miui.animation.Folme;
import miui.animation.base.AnimConfig;
import miui.os.Build;

public class EarthquakeGuideInfoPreference extends Preference {
    private boolean darkModel;

    public EarthquakeGuideInfoPreference(Context context) {
        super(context);
    }

    public EarthquakeGuideInfoPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public EarthquakeGuideInfoPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: private */
    public void descInfoView() {
        String locale = Locale.getDefault().toString();
        String region = Build.getRegion();
        boolean z = this.darkModel;
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://sec-cdn.static.xiaomi.net/secStatic/groups/miui-sec/common/quake-warn/dist/index.html?region=" + region + "&lang=" + locale + "&dark=" + (z ? 1 : 0)));
        intent.addFlags(268435456);
        getContext().startActivity(intent);
    }

    /* access modifiers changed from: private */
    public void guideActivity() {
        getContext().startActivity(new Intent(getContext(), EarthquakeWarningGuideSimpleActivity.class));
    }

    public void onBindViewHolder(A a2) {
        super.onBindViewHolder(a2);
        LinearLayout linearLayout = (LinearLayout) a2.itemView.findViewById(R.id.container_desc);
        LinearLayout linearLayout2 = (LinearLayout) a2.itemView.findViewById(R.id.container_guide);
        if (b.b.c.j.A.a()) {
            try {
                Folme.useAt(new View[]{linearLayout2}).touch().handleTouchOf(linearLayout2, new AnimConfig[0]);
                Folme.useAt(new View[]{linearLayout}).touch().handleTouchOf(linearLayout, new AnimConfig[0]);
            } catch (Throwable unused) {
                Log.e("TAG", "no support folme");
            }
        }
        linearLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EarthquakeGuideInfoPreference.this.descInfoView();
            }
        });
        linearLayout2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AnalyticHelper.trackMainResultActionModuleClick(AnalyticHelper.MAIN_GUIDE);
                EarthquakeGuideInfoPreference.this.guideActivity();
            }
        });
    }
}
