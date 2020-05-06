package com.miui.common.card.models;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import b.b.c.j.l;
import b.b.c.j.r;
import b.c.a.b.d;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.FillParentDrawable;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.R;
import com.miui.securitycenter.utils.b;
import com.miui.securityscan.i.i;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import org.json.JSONObject;

public class ActivityCardModel extends BaseCardModel {
    private static final String HTTP = "http";
    private static final String TAG = "ActivityCardModel";
    private String appUrl;
    private boolean browserOpen;
    /* access modifiers changed from: private */
    public int btnBgColorOpenN2;
    /* access modifiers changed from: private */
    public int btnBgColorOpenP2;
    /* access modifiers changed from: private */
    public int buttonColor2;
    private String cornerTip;
    /* access modifiers changed from: private */
    public String img;
    /* access modifiers changed from: private */
    public int template;
    private String url;
    private int usePosition;

    public class ActivityViewHolder extends BaseViewHolder {
        private static final String TAG = "ActivityViewHolder";
        d option = r.g;

        public ActivityViewHolder(View view) {
            super(view);
            l.a(view);
        }

        public void fillData(View view, BaseCardModel baseCardModel, int i) {
            Button button;
            int i2;
            super.fillData(view, baseCardModel, i);
            Resources resources = view.getContext().getResources();
            final ActivityCardModel activityCardModel = (ActivityCardModel) baseCardModel;
            AnonymousClass1 r0 = new View.OnClickListener() {
                public void onClick(View view) {
                    activityCardModel.onClick(view);
                }
            };
            view.setOnClickListener(r0);
            if (this.imageView != null) {
                if (activityCardModel.template == 4 || activityCardModel.template == 6) {
                    r.a(activityCardModel.img, this.imageView, this.option, (int) R.drawable.card_icon_default);
                } else {
                    r.a(activityCardModel.img, this.imageView, this.option, (Drawable) new FillParentDrawable(resources.getDrawable(R.drawable.big_backgroud_def)));
                }
            }
            Button button2 = this.actionButton;
            if (button2 != null) {
                button2.setOnClickListener(r0);
                if (activityCardModel.buttonColor2 != 0) {
                    button = this.actionButton;
                    i2 = activityCardModel.buttonColor2;
                } else {
                    button = this.actionButton;
                    i2 = resources.getColor(R.color.result_small_button_text_color);
                }
                button.setTextColor(i2);
                float dimension = resources.getDimension(R.dimen.big_result_blue_button_corner_radius);
                Drawable drawable = null;
                if (!(activityCardModel.btnBgColorOpenN2 == 0 || activityCardModel.btnBgColorOpenP2 == 0)) {
                    drawable = b.a(dimension, activityCardModel.btnBgColorOpenN2, activityCardModel.btnBgColorOpenP2);
                }
                if (drawable != null) {
                    this.actionButton.setBackground(drawable);
                } else {
                    this.actionButton.setBackgroundResource(R.drawable.scanresult_button_blue);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void setIconDisplayOption(d dVar) {
            this.option = dVar;
        }
    }

    public ActivityCardModel(int i, JSONObject jSONObject, int i2) {
        super(i);
        init(jSONObject);
        this.usePosition = i2;
    }

    private void init(JSONObject jSONObject) {
        if (jSONObject != null) {
            this.img = jSONObject.optString("img");
            this.title = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
            this.summary = jSONObject.optString("summary");
            this.cornerTip = jSONObject.optString("cornerTip");
            this.url = jSONObject.optString(MijiaAlertModel.KEY_URL);
            this.appUrl = jSONObject.optString("appUrl");
            this.template = jSONObject.optInt("template");
            this.button = jSONObject.optString("button");
            this.dataId = jSONObject.optString("dataId");
            this.browserOpen = jSONObject.optBoolean("browserOpen", true);
            String optString = jSONObject.optString("buttonColor2");
            String optString2 = jSONObject.optString("btnBgColorOpenN2");
            String optString3 = jSONObject.optString("btnBgColorOpenP2");
            if (!TextUtils.isEmpty(optString)) {
                try {
                    this.buttonColor2 = Color.parseColor(optString);
                } catch (Exception e) {
                    Log.e(TAG, "buttonColor2", e);
                }
            }
            if (!TextUtils.isEmpty(optString2) && !TextUtils.isEmpty(optString3)) {
                try {
                    this.btnBgColorOpenN2 = Color.parseColor(optString2);
                    this.btnBgColorOpenP2 = Color.parseColor(optString3);
                } catch (Exception e2) {
                    Log.e(TAG, "btnBgColorOpenN2,btnBgColorOpenP2", e2);
                }
            }
        }
    }

    private static boolean isUrlAvailable(String str) {
        try {
            return !TextUtils.isEmpty(str) && com.miui.securityscan.cards.d.a(Intent.parseUri(str, 0));
        } catch (Exception e) {
            Log.e(TAG, "Intent parse url error :", e);
            return false;
        }
    }

    public static ActivityCardModel parse(int i, int i2, JSONObject jSONObject) {
        ActivityCardModel activitySmallButtonCardModel = (i2 == 4 || i2 == 6) ? new ActivitySmallButtonCardModel(jSONObject, i) : i2 != 7 ? null : new ActivityBigBannerCardModel(jSONObject, i);
        if (activitySmallButtonCardModel != null && (isUrlAvailable(activitySmallButtonCardModel.appUrl) || isUrlAvailable(activitySmallButtonCardModel.url))) {
            return activitySmallButtonCardModel;
        }
        return null;
    }

    private void startNewActivity(String str, Context context, String str2) {
        try {
            if (!str2.startsWith(HTTP) || this.browserOpen) {
                context.startActivity(Intent.parseUri(str2, 0));
            } else {
                i.c(context, str2, str);
            }
        } catch (Exception e) {
            Log.e(TAG, "handle click error : ", e);
        }
    }

    public BaseViewHolder createViewHolder(View view) {
        return new ActivityViewHolder(view);
    }

    public String getAppUrl() {
        return this.appUrl;
    }

    public String getCornerTip() {
        return this.cornerTip;
    }

    public String getImg() {
        return this.img;
    }

    public String getUrl() {
        return this.url;
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x002d  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0033  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onClick(android.view.View r3) {
        /*
            r2 = this;
            boolean r0 = com.miui.securityscan.i.f.a()
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            android.content.Context r3 = r3.getContext()
            java.lang.String r0 = r2.getTitle()
            java.lang.String r1 = r2.appUrl
            boolean r1 = isUrlAvailable(r1)
            if (r1 == 0) goto L_0x001d
            java.lang.String r1 = r2.appUrl
        L_0x0019:
            r2.startNewActivity(r0, r3, r1)
            goto L_0x0028
        L_0x001d:
            java.lang.String r1 = r2.url
            boolean r1 = isUrlAvailable(r1)
            if (r1 == 0) goto L_0x0028
            java.lang.String r1 = r2.url
            goto L_0x0019
        L_0x0028:
            int r3 = r2.usePosition
            r0 = 1
            if (r3 != r0) goto L_0x0033
            java.lang.String r3 = r2.dataId
            com.miui.securityscan.a.G.u(r3)
            goto L_0x0044
        L_0x0033:
            r0 = 2
            if (r3 != r0) goto L_0x003c
            java.lang.String r3 = r2.dataId
            com.miui.securityscan.a.G.p(r3)
            goto L_0x0044
        L_0x003c:
            r0 = 3
            if (r3 != r0) goto L_0x0044
            java.lang.String r3 = r2.dataId
            com.miui.securityscan.a.G.j((java.lang.String) r3)
        L_0x0044:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.common.card.models.ActivityCardModel.onClick(android.view.View):void");
    }

    public void setAppUrl(String str) {
        this.appUrl = str;
    }

    public void setCornerTip(String str) {
        this.cornerTip = str;
    }

    public void setImg(String str) {
        this.img = str;
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public boolean validate() {
        return true;
    }
}
