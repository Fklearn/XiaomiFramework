package com.miui.common.card.models;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import b.b.c.d.C0184d;
import b.b.c.d.s;
import b.b.c.j.l;
import b.b.c.j.r;
import b.b.g.a;
import b.c.a.b.d;
import com.miui.applicationlock.c.y;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.FillParentDrawable;
import com.miui.common.customview.AdImageView;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.R;
import com.miui.securitycenter.p;
import com.miui.securityscan.C0534a;
import com.miui.securityscan.a.G;
import com.miui.securityscan.cards.g;
import com.miui.securityscan.cards.h;
import com.miui.securityscan.cards.k;
import com.miui.securityscan.i.f;
import com.xiaomi.ad.feedback.IAdFeedbackListener;
import java.lang.ref.WeakReference;
import org.json.JSONArray;
import org.json.JSONObject;

public class AdvCardModel extends BaseCardModel implements a.C0028a {
    private static final int MAX_IMG_SIZE = 3;
    private static final String TAG = "AdvCardModel";
    private static final String TAG_CLOSE_NEW_STYLE = "closeNormalAdNewStyle";
    private String actionUrl;
    /* access modifiers changed from: private */
    public long allDownloadNum;
    private String appChannel;
    private String appClientId;
    private String appRef;
    private String appSignature;
    private boolean autoOpen;
    private String btnBgColorDownloading2;
    private String btnBgColorNormal2;
    private String btnBgColorOpenN2;
    private String btnBgColorOpenP2;
    private String btnBgColorPressed2;
    private String buttonColor2;
    private String buttonOpen;
    private String buttonOpenColor2;
    private String[] clickMonitorUrls;
    private String cta;
    private String dataId;
    private String deepLink;
    /* access modifiers changed from: private */
    public String donwloadCountStr;
    private String ex;
    private String floatCardData;
    private int id;
    private String landingPageUrl;
    private boolean local;
    private transient View mCloseView;
    protected String[] multiImgUrls = new String[3];
    private String nonce;
    /* access modifiers changed from: private */
    public transient Object object;
    private String packageName;
    private int position = -1;
    private String positionId;
    private String source;
    private int targetType;
    /* access modifiers changed from: private */
    public int template;
    private String testId;
    /* access modifiers changed from: private */
    public int usePosition;
    private long validTime;
    private String[] viewMonitorUrls;

    private static class AdFeedbackListener extends IAdFeedbackListener.Stub {
        /* access modifiers changed from: private */
        public C0534a context;
        private final WeakReference<AdvCardModel> weakReferenceModel;

        public AdFeedbackListener(AdvCardModel advCardModel, C0534a aVar) {
            this.weakReferenceModel = new WeakReference<>(advCardModel);
            this.context = aVar;
        }

        public void onFinished(int i) {
            final AdvCardModel advCardModel = (AdvCardModel) this.weakReferenceModel.get();
            if (advCardModel != null) {
                if (i > 0) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            C0534a access$400 = AdFeedbackListener.this.context;
                            AdvCardModel advCardModel = advCardModel;
                            access$400.a((BaseCardModel) advCardModel, advCardModel.usePosition);
                            if (advCardModel.template == 10001 || advCardModel.template == 30001 || advCardModel.template == 30002) {
                                h.a(advCardModel.getPositionId(), advCardModel.object);
                            }
                        }
                    });
                }
                y.b().b(this.context.getApplicationContext());
            }
        }
    }

    public class AdvViewHolder extends BaseViewHolder {
        private TextView downloadCount;
        private ImageView ivBanner1;
        private ImageView ivBanner2;
        private ImageView ivBanner3;
        private ImageView ivBigBanner;
        d option = r.g;
        private View viewClose;

        public AdvViewHolder(View view) {
            super(view);
            this.viewClose = view.findViewById(R.id.close);
            this.ivBigBanner = (ImageView) view.findViewById(R.id.big_image);
            this.ivBanner1 = (ImageView) view.findViewById(R.id.image1);
            this.ivBanner2 = (ImageView) view.findViewById(R.id.image2);
            this.ivBanner3 = (ImageView) view.findViewById(R.id.image3);
            this.downloadCount = (TextView) view.findViewById(R.id.download_count);
            int color = view.getResources().getColor(R.color.result_banner_icon_bg);
            ImageView imageView = this.ivBigBanner;
            if (imageView != null) {
                imageView.setColorFilter(color);
            }
            ImageView imageView2 = this.ivBanner1;
            if (imageView2 != null) {
                imageView2.setColorFilter(color);
            }
            ImageView imageView3 = this.ivBanner2;
            if (imageView3 != null) {
                imageView3.setColorFilter(color);
            }
            ImageView imageView4 = this.ivBanner3;
            if (imageView4 != null) {
                imageView4.setColorFilter(color);
            }
            l.a(view);
        }

        private void fillNormalData(View view, BaseCardModel baseCardModel, int i, AdvCardModel advCardModel, View.OnClickListener onClickListener) {
            if (this.actionButton != null) {
                AdvCardModel.this.setDonwloadButtonStatus(view.getContext(), this.actionButton, advCardModel);
                this.actionButton.setOnClickListener(onClickListener);
            }
            View view2 = this.viewClose;
            if (view2 != null) {
                view2.setOnClickListener(onClickListener);
            }
            if (this.imageView != null && !TextUtils.isEmpty(baseCardModel.getIcon())) {
                r.a(baseCardModel.getIcon(), this.imageView, this.option, (int) R.drawable.card_icon_default);
                if (this.imageView instanceof AdImageView) {
                    ((C0534a) view.getContext()).a((AdImageView) this.imageView, i, advCardModel);
                }
            }
            if (this.ivBanner1 != null && !TextUtils.isEmpty(advCardModel.multiImgUrls[0])) {
                r.a(advCardModel.multiImgUrls[0], this.ivBanner1, this.option, (int) R.drawable.card_icon_default);
                if (this.ivBanner1 instanceof AdImageView) {
                    ((C0534a) view.getContext()).a((AdImageView) this.ivBanner1, i, advCardModel);
                }
            }
            if (this.ivBanner2 != null && !TextUtils.isEmpty(advCardModel.multiImgUrls[1])) {
                r.a(advCardModel.multiImgUrls[1], this.ivBanner2, this.option, (int) R.drawable.card_icon_default);
                if (this.ivBanner2 instanceof AdImageView) {
                    ((C0534a) view.getContext()).a((AdImageView) this.ivBanner2, i, advCardModel);
                }
            }
            if (this.ivBanner3 != null && !TextUtils.isEmpty(advCardModel.multiImgUrls[2])) {
                r.a(advCardModel.multiImgUrls[2], this.ivBanner3, this.option, (int) R.drawable.card_icon_default);
                if (this.ivBanner3 instanceof AdImageView) {
                    ((C0534a) view.getContext()).a((AdImageView) this.ivBanner3, i, advCardModel);
                }
            }
            ImageView imageView = this.ivBigBanner;
            if (imageView != null) {
                if ((advCardModel instanceof AdvNormalWebsiteBigPicCardModel) && (imageView instanceof AdImageView)) {
                    ((C0534a) view.getContext()).a((AdImageView) this.ivBigBanner, i, advCardModel);
                }
                if (!TextUtils.isEmpty(advCardModel.multiImgUrls[0])) {
                    r.a(advCardModel.multiImgUrls[0], this.ivBigBanner, r.f1760d, (Drawable) new FillParentDrawable(view.getContext().getResources().getDrawable(R.drawable.big_backgroud_def)));
                }
            }
        }

        public void fillData(View view, BaseCardModel baseCardModel, int i) {
            super.fillData(view, baseCardModel, i);
            final AdvCardModel advCardModel = (AdvCardModel) baseCardModel;
            AnonymousClass1 r5 = new View.OnClickListener() {
                public void onClick(View view) {
                    advCardModel.onClick(view);
                }
            };
            view.setOnClickListener(r5);
            Log.d(AdvCardModel.TAG, "Chinese Ads");
            fillNormalData(view, baseCardModel, i, advCardModel, r5);
            if (advCardModel.getTemplate() == 40 && this.downloadCount != null) {
                if (AdvCardModel.this.allDownloadNum != -1) {
                    this.downloadCount.setVisibility(0);
                    this.downloadCount.setText(advCardModel.donwloadCountStr);
                    s.a(this.titleView, advCardModel.title, advCardModel.donwloadCountStr);
                    return;
                }
                this.downloadCount.setVisibility(8);
            }
        }

        /* access modifiers changed from: protected */
        public void setIconDisplayOption(d dVar) {
            this.option = dVar;
        }
    }

    public AdvCardModel(int i, JSONObject jSONObject, int i2) {
        super(i);
        init(jSONObject);
        this.usePosition = i2;
    }

    private void closeNormalAd(final C0534a aVar, View view) {
        View inflate = aVar.getLayoutInflater().inflate(R.layout.result_unlike_pop_window, (ViewGroup) null);
        Resources resources = aVar.getResources();
        int i = resources.getDisplayMetrics().widthPixels;
        final PopupWindow popupWindow = new PopupWindow(inflate, -2, -2);
        inflate.measure(View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(10, Integer.MIN_VALUE));
        inflate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                popupWindow.dismiss();
                C0534a aVar = aVar;
                AdvCardModel advCardModel = AdvCardModel.this;
                aVar.a((BaseCardModel) advCardModel, advCardModel.usePosition);
            }
        });
        int measuredWidth = inflate.getMeasuredWidth();
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        popupWindow.showAtLocation(view, 0, iArr[0] - measuredWidth, iArr[1] - resources.getDimensionPixelOffset(R.dimen.result_popwindow_offset));
    }

    private void closeNormalAdNewStyle(C0534a aVar) {
        String str;
        y b2 = y.b();
        AdFeedbackListener adFeedbackListener = new AdFeedbackListener(this, aVar);
        if (b2.a(aVar.getApplicationContext())) {
            try {
                b2.a(aVar.getApplicationContext(), (IAdFeedbackListener) adFeedbackListener, aVar.getPackageName(), "com.miui.securitycenter_scanresult", getEx());
            } catch (Exception unused) {
                str = "showDislikeWindow failed,maybe method showDislikeWindow() does not match or exits ";
            }
        } else {
            str = "connect failed,maybe not support dislike window";
            Log.e(TAG_CLOSE_NEW_STYLE, str);
        }
    }

    /* JADX WARNING: type inference failed for: r11v0, types: [android.content.Context, miui.app.Activity, com.miui.securityscan.a] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onAdvButtonClick(com.miui.securityscan.C0534a r11) {
        /*
            r10 = this;
            java.lang.String r0 = r10.deepLink
            boolean r0 = com.miui.securityscan.i.i.b(r11, r0)
            if (r0 == 0) goto L_0x0009
            return
        L_0x0009:
            java.lang.String r0 = r10.packageName
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0017
            java.lang.String r0 = r10.landingPageUrl
            com.miui.securityscan.i.i.b(r11, r0)
            return
        L_0x0017:
            com.miui.securityscan.cards.k r0 = com.miui.securityscan.cards.k.a((android.content.Context) r11)
            java.lang.String r1 = r10.packageName
            boolean r0 = r0.a((java.lang.String) r1)
            if (r0 == 0) goto L_0x0033
            android.content.pm.PackageManager r0 = r11.getPackageManager()
            java.lang.String r1 = r10.packageName
            android.content.Intent r0 = r0.getLaunchIntentForPackage(r1)
            if (r0 == 0) goto L_0x0066
            r11.startActivity(r0)
            goto L_0x0066
        L_0x0033:
            boolean r0 = com.miui.securityscan.i.c.f(r11)
            if (r0 != 0) goto L_0x0040
            r0 = 2131758379(0x7f100d2b, float:1.914772E38)
            com.miui.securityscan.i.c.a((android.content.Context) r11, (int) r0)
            return
        L_0x0040:
            java.lang.String r2 = r10.packageName
            java.lang.String r3 = r10.appRef
            java.lang.String r4 = r10.ex
            java.lang.String r5 = r10.appClientId
            java.lang.String r6 = r10.appSignature
            java.lang.String r7 = r10.nonce
            java.lang.String r8 = r10.appChannel
            java.lang.String r9 = r10.floatCardData
            r1 = r11
            b.b.c.j.x.a(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            com.miui.securityscan.cards.g r11 = com.miui.securityscan.cards.g.a((android.content.Context) r11)
            java.lang.String r0 = r10.packageName
            boolean r1 = r10.autoOpen
            r11.a((java.lang.String) r0, (boolean) r1)
            java.lang.String r0 = r10.packageName
            r1 = 10
            r11.a((java.lang.String) r0, (int) r1)
        L_0x0066:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.common.card.models.AdvCardModel.onAdvButtonClick(com.miui.securityscan.a):void");
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.securityscan.a] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onAdvContentClick(com.miui.securityscan.C0534a r3) {
        /*
            r2 = this;
            java.lang.String r0 = "AdvCardModel"
            java.lang.String r1 = r2.deepLink
            boolean r1 = com.miui.securityscan.i.i.b(r3, r1)
            if (r1 == 0) goto L_0x000b
            return
        L_0x000b:
            java.lang.String r1 = r2.packageName
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x0019
            java.lang.String r0 = r2.landingPageUrl
            com.miui.securityscan.i.i.b(r3, r0)
            return
        L_0x0019:
            java.lang.String r1 = r2.landingPageUrl     // Catch:{ Exception -> 0x002d }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x002d }
            if (r1 == 0) goto L_0x0027
            java.lang.String r3 = "landingPageUrl is empty"
            android.util.Log.d(r0, r3)     // Catch:{ Exception -> 0x002d }
            return
        L_0x0027:
            java.lang.String r1 = r2.landingPageUrl     // Catch:{ Exception -> 0x002d }
            com.miui.securityscan.i.i.c(r3, r1)     // Catch:{ Exception -> 0x002d }
            goto L_0x0033
        L_0x002d:
            r3 = move-exception
            java.lang.String r1 = "onAdvContentClick"
            android.util.Log.e(r0, r1, r3)
        L_0x0033:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.common.card.models.AdvCardModel.onAdvContentClick(com.miui.securityscan.a):void");
    }

    public static AdvCardModel parse(com.miui.securityscan.cards.d dVar, int i, int i2, JSONObject jSONObject) {
        if (i2 == 3) {
            return new AdvNormalWebsiteBigPicCardModel(jSONObject, i);
        }
        if (i2 == 4) {
            return new AdvNormalWebsiteSmallPicCardModel(jSONObject, i);
        }
        if (i2 == 5) {
            return new AdvBigButtonCardModel(R.layout.result_ad_template_5, jSONObject, i);
        }
        if (i2 == 25) {
            return new AdvBigButtonCardModel(R.layout.result_ad_template_25, jSONObject, i);
        }
        if (i2 == 31) {
            return new AdvThreePicCardModel(R.layout.result_ad_template_31, jSONObject, i);
        }
        if (i2 == 40) {
            return new AdvNormalWebsiteGroupPicCardModel(jSONObject, i);
        }
        if (i2 != 10001 && i2 != 30001 && i2 != 30002) {
            return null;
        }
        dVar.a(dVar.d() + 1);
        int d2 = dVar.d();
        Log.d(TAG, "internationalAdvIndex = " + d2);
        Log.d(TAG, "placeid = " + "" + " ;  advUsePosition = " + i);
        AdvInternationalCardModel a2 = h.a(i, "", i2);
        JSONObject optJSONObject = jSONObject.optJSONObject("extra");
        if (a2 == null || optJSONObject == null) {
            return a2;
        }
        a2.position = optJSONObject.optInt("position", -1);
        return a2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:33:0x0048 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0064  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x006d  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0071  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x007d  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0081  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setButtonBGandTextColor(android.content.Context r10, android.widget.Button r11, boolean r12, boolean r13, com.miui.common.card.models.AdvCardModel r14) {
        /*
            r9 = this;
            if (r11 != 0) goto L_0x0003
            return
        L_0x0003:
            android.content.res.Resources r10 = r10.getResources()
            r0 = 2131165574(0x7f070186, float:1.7945369E38)
            float r0 = r10.getDimension(r0)
            r1 = 0
            java.lang.String r2 = r14.buttonOpenColor2     // Catch:{ Exception -> 0x0016 }
            int r2 = android.graphics.Color.parseColor(r2)     // Catch:{ Exception -> 0x0016 }
            goto L_0x0017
        L_0x0016:
            r2 = r1
        L_0x0017:
            java.lang.String r3 = r14.buttonColor2     // Catch:{ Exception -> 0x001e }
            int r3 = android.graphics.Color.parseColor(r3)     // Catch:{ Exception -> 0x001e }
            goto L_0x001f
        L_0x001e:
            r3 = r1
        L_0x001f:
            java.lang.String r4 = r14.btnBgColorNormal2     // Catch:{ Exception -> 0x002c }
            int r4 = android.graphics.Color.parseColor(r4)     // Catch:{ Exception -> 0x002c }
            java.lang.String r5 = r14.btnBgColorPressed2     // Catch:{ Exception -> 0x002d }
            int r5 = android.graphics.Color.parseColor(r5)     // Catch:{ Exception -> 0x002d }
            goto L_0x002e
        L_0x002c:
            r4 = r1
        L_0x002d:
            r5 = r1
        L_0x002e:
            java.lang.String r6 = r14.btnBgColorOpenN2     // Catch:{ Exception -> 0x003b }
            int r6 = android.graphics.Color.parseColor(r6)     // Catch:{ Exception -> 0x003b }
            java.lang.String r7 = r14.btnBgColorOpenP2     // Catch:{ Exception -> 0x003c }
            int r7 = android.graphics.Color.parseColor(r7)     // Catch:{ Exception -> 0x003c }
            goto L_0x003d
        L_0x003b:
            r6 = r1
        L_0x003c:
            r7 = r1
        L_0x003d:
            java.lang.String r14 = r14.btnBgColorDownloading2     // Catch:{ Exception -> 0x0044 }
            int r14 = android.graphics.Color.parseColor(r14)     // Catch:{ Exception -> 0x0044 }
            goto L_0x0045
        L_0x0044:
            r14 = r1
        L_0x0045:
            r8 = 0
            if (r12 == 0) goto L_0x0051
            if (r6 == 0) goto L_0x0062
            if (r7 == 0) goto L_0x0062
            android.graphics.drawable.Drawable r8 = com.miui.securitycenter.utils.b.a(r0, r6, r7)
            goto L_0x0062
        L_0x0051:
            if (r13 == 0) goto L_0x005a
            if (r14 == 0) goto L_0x0062
            android.graphics.drawable.Drawable r8 = com.miui.securitycenter.utils.b.a(r0, r14, r14)
            goto L_0x0062
        L_0x005a:
            if (r4 == 0) goto L_0x0062
            if (r5 == 0) goto L_0x0062
            android.graphics.drawable.Drawable r8 = com.miui.securitycenter.utils.b.a(r0, r4, r5)
        L_0x0062:
            if (r12 == 0) goto L_0x0068
            if (r2 == 0) goto L_0x006b
            r1 = r2
            goto L_0x006b
        L_0x0068:
            if (r3 == 0) goto L_0x006b
            r1 = r3
        L_0x006b:
            if (r1 == 0) goto L_0x0071
            r11.setTextColor(r1)
            goto L_0x007b
        L_0x0071:
            r12 = 2131100559(0x7f06038f, float:1.7813503E38)
            int r10 = r10.getColor(r12)
            r11.setTextColor(r10)
        L_0x007b:
            if (r8 == 0) goto L_0x0081
            r11.setBackground(r8)
            goto L_0x0087
        L_0x0081:
            r10 = 2131232292(0x7f080624, float:1.808069E38)
            r11.setBackgroundResource(r10)
        L_0x0087:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.common.card.models.AdvCardModel.setButtonBGandTextColor(android.content.Context, android.widget.Button, boolean, boolean, com.miui.common.card.models.AdvCardModel):void");
    }

    /* access modifiers changed from: private */
    public void setDonwloadButtonStatus(Context context, Button button, AdvCardModel advCardModel) {
        boolean z;
        int i;
        String str;
        boolean a2 = k.a(context).a(advCardModel.packageName);
        boolean z2 = false;
        if (a2) {
            str = advCardModel.buttonOpen;
        } else {
            int b2 = g.a(context).b(advCardModel.packageName);
            if (b2 != -1) {
                if (b2 != 5) {
                    if (b2 == 10) {
                        i = R.string.connecting;
                    } else if (b2 != 1) {
                        if (b2 != 2) {
                            if (b2 != 3) {
                                str = advCardModel.button;
                            } else {
                                i = R.string.installing;
                            }
                        }
                    }
                    button.setText(i);
                    z = true;
                    setButtonBGandTextColor(context, button, a2, z, advCardModel);
                    button.setEnabled(z2);
                }
                int a3 = g.a(context).a(advCardModel.packageName);
                if (a3 != -1) {
                    button.setText(a3 + "%");
                    z = true;
                    setButtonBGandTextColor(context, button, a2, z, advCardModel);
                    button.setEnabled(z2);
                }
            }
            button.setText(R.string.downloading);
            z = true;
            setButtonBGandTextColor(context, button, a2, z, advCardModel);
            button.setEnabled(z2);
        }
        button.setText(str);
        z = false;
        z2 = true;
        setButtonBGandTextColor(context, button, a2, z, advCardModel);
        button.setEnabled(z2);
    }

    public BaseViewHolder createViewHolder(View view) {
        return new AdvViewHolder(view);
    }

    public String getActionUrl() {
        return this.actionUrl;
    }

    public long getAllDownloadNum() {
        return this.allDownloadNum;
    }

    public String[] getClickMonitorUrls() {
        return this.clickMonitorUrls;
    }

    public String getCta() {
        return this.cta;
    }

    public String getDataId() {
        return this.dataId;
    }

    public String getEx() {
        return this.ex;
    }

    public int getId() {
        return this.id;
    }

    public String getLandingPageUrl() {
        return this.landingPageUrl;
    }

    public String[] getMultiImgUrls() {
        return this.multiImgUrls;
    }

    public Object getObject() {
        return this.object;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public int getPosition() {
        return this.position;
    }

    public String getPositionId() {
        return this.positionId;
    }

    public String getSource() {
        return this.source;
    }

    public int getTargetType() {
        return this.targetType;
    }

    public int getTemplate() {
        return this.template;
    }

    public String getTestId() {
        return this.testId;
    }

    public String getTitle() {
        int i = this.template;
        return (i == 3 || i == 4 || i == 40) ? getSource() : super.getTitle();
    }

    public int getUsePosition() {
        return this.usePosition;
    }

    public Long getValidTime() {
        return Long.valueOf(this.validTime);
    }

    public String[] getViewMonitorUrls() {
        return this.viewMonitorUrls;
    }

    public void init(JSONObject jSONObject) {
        if (jSONObject != null) {
            this.id = jSONObject.optInt("id");
            this.dataId = jSONObject.optString("dataId");
            this.title = jSONObject.optString("appName");
            if (TextUtils.isEmpty(this.title)) {
                this.title = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
            }
            this.summary = jSONObject.optString("summary");
            this.source = jSONObject.optString("source");
            this.landingPageUrl = jSONObject.optString("landingPageUrl");
            this.template = jSONObject.optInt("template");
            this.cta = jSONObject.optString("cta");
            this.allDownloadNum = jSONObject.optLong("allDownloadNum");
            this.donwloadCountStr = C0184d.a(this.allDownloadNum);
            this.icon = jSONObject.optString("iconUrl");
            this.actionUrl = jSONObject.optString("actionUrl");
            this.deepLink = jSONObject.optString("deeplink");
            this.packageName = jSONObject.optString("packageName");
            this.ex = jSONObject.optString("ex");
            this.appRef = jSONObject.optString("appRef");
            this.appClientId = jSONObject.optString("appClientId");
            this.appSignature = jSONObject.optString("appSignature");
            this.nonce = jSONObject.optString("nonce");
            this.appChannel = jSONObject.optString("appChannel");
            this.local = jSONObject.optBoolean("local");
            this.floatCardData = jSONObject.optString("floatCardData");
            JSONObject optJSONObject = jSONObject.optJSONObject("extra");
            if (optJSONObject != null) {
                this.validTime = optJSONObject.optLong("validTime");
                this.position = optJSONObject.optInt("position", -1);
                this.autoOpen = optJSONObject.optBoolean("autoOpen");
                this.button = optJSONObject.optString("button");
                this.buttonOpen = optJSONObject.optString("buttonOpen");
                this.buttonColor2 = optJSONObject.optString("buttonColor2");
                this.buttonOpenColor2 = optJSONObject.optString("buttonOpenColor2");
                this.btnBgColorNormal2 = optJSONObject.optString("btnBgColorNormal2");
                this.btnBgColorPressed2 = optJSONObject.optString("btnBgColorPressed2");
                this.btnBgColorOpenN2 = optJSONObject.optString("btnBgColorOpenN2");
                this.btnBgColorOpenP2 = optJSONObject.optString("btnBgColorOpenP2");
                this.btnBgColorDownloading2 = optJSONObject.optString("btnBgColorDownloading2");
            }
            JSONArray optJSONArray = jSONObject.optJSONArray("imgUrls");
            if (optJSONArray != null) {
                int length = optJSONArray.length();
                int i = 0;
                while (i < 3 && i < length) {
                    this.multiImgUrls[i] = optJSONArray.optString(i);
                    i++;
                }
            }
            this.targetType = jSONObject.optInt("targetType");
            JSONArray optJSONArray2 = jSONObject.optJSONArray("viewMonitorUrls");
            if (optJSONArray2 != null && optJSONArray2.length() > 0) {
                this.viewMonitorUrls = new String[optJSONArray2.length()];
                for (int i2 = 0; i2 < optJSONArray2.length(); i2++) {
                    this.viewMonitorUrls[i2] = optJSONArray2.optString(i2);
                }
            }
            JSONArray optJSONArray3 = jSONObject.optJSONArray("clickMonitorUrls");
            if (optJSONArray3 != null && optJSONArray3.length() > 0) {
                this.clickMonitorUrls = new String[optJSONArray3.length()];
                for (int i3 = 0; i3 < optJSONArray3.length(); i3++) {
                    this.clickMonitorUrls[i3] = optJSONArray3.optString(i3);
                }
            }
        }
    }

    public boolean isAutoOpen() {
        return this.autoOpen;
    }

    public boolean isLocal() {
        return this.local;
    }

    public void onAdDisliked(Object obj, int i) {
        View view = this.mCloseView;
        if (view != null) {
            ((C0534a) view.getContext()).a((BaseCardModel) this, this.usePosition);
            int i2 = this.template;
            if (i2 == 10001 || i2 == 30001 || i2 == 30002) {
                h.a(getPositionId(), this.object);
            }
        }
    }

    public void onClick(View view) {
        if (!f.a()) {
            C0534a aVar = (C0534a) view.getContext();
            int i = this.template;
            if (i != 10001 && i != 30001 && i != 30002) {
                int id2 = view.getId();
                if (id2 == R.id.button) {
                    onAdvButtonClick(aVar);
                } else if (id2 != R.id.close) {
                    onAdvContentClick(aVar);
                } else if (p.a() < 5 || isLocal()) {
                    closeNormalAd(aVar, view);
                } else {
                    closeNormalAdNewStyle(aVar);
                }
                if (view.getId() != R.id.close) {
                    C0534a.a("CLICK", this);
                    int i2 = this.usePosition;
                    if (i2 == 1) {
                        G.v(isLocal() ? this.dataId : String.valueOf(this.id));
                    } else if (i2 == 2) {
                        G.q(isLocal() ? this.dataId : String.valueOf(this.id));
                    } else if (i2 == 3) {
                        G.i(isLocal() ? this.dataId : String.valueOf(this.id));
                    }
                }
            } else if (h.a(view)) {
                showXOutAdFeedBackDialog(view);
            }
        }
    }

    public void setActionUrl(String str) {
        this.actionUrl = str;
    }

    public void setAllDownloadNum(long j) {
        this.allDownloadNum = j;
    }

    public void setClickMonitorUrls(String[] strArr) {
        this.clickMonitorUrls = strArr;
    }

    public void setCta(String str) {
        this.cta = str;
    }

    public void setEx(String str) {
        this.ex = str;
    }

    public void setId(int i) {
        this.id = i;
    }

    public void setLandingPageUrl(String str) {
        this.landingPageUrl = str;
    }

    public void setMultiImgUrls(String[] strArr) {
        this.multiImgUrls = strArr;
    }

    public void setObject(Object obj) {
        this.object = obj;
    }

    public void setPackageName(String str) {
        this.packageName = str;
    }

    public void setPositionId(String str) {
        this.positionId = str;
    }

    public void setSource(String str) {
        this.source = str;
    }

    public void setTargetType(int i) {
        this.targetType = i;
    }

    public void setTemplate(int i) {
        this.template = i;
    }

    public void setTestId(String str) {
        this.testId = str;
    }

    public void setViewMonitorUrls(String[] strArr) {
        this.viewMonitorUrls = strArr;
    }

    public void showXOutAdFeedBackDialog(View view) {
        this.mCloseView = view;
        h.a(view.getContext(), this.object);
    }

    public boolean validate() {
        return true;
    }
}
