package com.miui.common.card.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import b.b.c.j.l;
import b.c.a.b.d;
import b.c.a.b.g.a;
import com.miui.common.card.BaseViewHolder;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.securityscan.a.G;
import com.miui.securityscan.i.f;
import com.miui.securityscan.i.i;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import miui.content.res.IconCustomizer;
import org.json.JSONArray;
import org.json.JSONObject;

public class NewsCardModel extends BaseCardModel {
    private static final int MAX_IMG_SIZE = 3;
    private static final String TAG = "NewsCardModel";
    private String cardId;
    private String cornerTip;
    private String detailTitle;
    /* access modifiers changed from: private */
    public String[] images = new String[3];
    /* access modifiers changed from: private */
    public boolean isBottomRow;
    /* access modifiers changed from: private */
    public boolean isTopRow;
    private long newsDate;
    private String newsId;
    /* access modifiers changed from: private */
    public boolean previousCardModelIsBlankLine;
    private String source;
    private int template;
    private String url;
    private int usePosition;
    private String views;

    public class NewsViewHolder extends BaseViewHolder {
        private Context context;
        /* access modifiers changed from: private */
        public d newsCardModelOptions;

        public NewsViewHolder(View view) {
            super(view);
            d.a aVar = new d.a();
            aVar.a(true);
            aVar.b(true);
            aVar.b((int) R.drawable.card_icon_default);
            aVar.a((int) R.drawable.card_icon_default);
            aVar.a((a) new a() {
                public Bitmap process(Bitmap bitmap) {
                    synchronized (NewsViewHolder.this.newsCardModelOptions) {
                        Bitmap bitmap2 = IconCustomizer.generateIconStyleDrawable(new BitmapDrawable(Application.d().getResources(), Bitmap.createBitmap(bitmap))).getBitmap();
                        return bitmap2 != null ? bitmap2 : bitmap;
                    }
                }
            });
            aVar.c(true);
            this.newsCardModelOptions = aVar.a();
            this.context = view.getContext();
            l.a(view, 1.0f);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0083, code lost:
            if (com.miui.common.card.models.NewsCardModel.access$200(r7) != false) goto L_0x0092;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0090, code lost:
            if (com.miui.common.card.models.NewsCardModel.access$300(r7) != false) goto L_0x0092;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x00c5, code lost:
            if (com.miui.common.card.models.NewsCardModel.access$200(r7) != false) goto L_0x0092;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void fillData(android.view.View r6, com.miui.common.card.models.BaseCardModel r7, int r8) {
            /*
                r5 = this;
                super.fillData(r6, r7, r8)
                com.miui.common.card.models.NewsCardModel r7 = (com.miui.common.card.models.NewsCardModel) r7
                boolean r8 = r7 instanceof com.miui.common.card.models.NewsListBannerCardModel
                r0 = 0
                if (r8 == 0) goto L_0x00c8
                boolean r8 = r7.previousCardModelIsBlankLine
                r1 = 2131231055(0x7f08014f, float:1.807818E38)
                r2 = 2131231056(0x7f080150, float:1.8078182E38)
                r3 = 2131166859(0x7f07068b, float:1.7947975E38)
                r4 = 2131166833(0x7f070671, float:1.7947923E38)
                if (r8 == 0) goto L_0x0086
                boolean r8 = r7.isBottomRow
                if (r8 == 0) goto L_0x0051
                boolean r8 = r7.isTopRow
                if (r8 == 0) goto L_0x0051
                r8 = 2131231061(0x7f080155, float:1.8078192E38)
                r6.setBackgroundResource(r8)
                android.content.Context r8 = r5.context
                android.content.res.Resources r8 = r8.getResources()
                int r8 = r8.getDimensionPixelSize(r3)
                android.content.Context r1 = r5.context
                android.content.res.Resources r1 = r1.getResources()
                int r1 = r1.getDimensionPixelSize(r4)
                android.content.Context r2 = r5.context
                android.content.res.Resources r2 = r2.getResources()
                int r2 = r2.getDimensionPixelSize(r4)
                r6.setPaddingRelative(r1, r8, r2, r8)
                goto L_0x00ce
            L_0x0051:
                boolean r8 = r7.isTopRow
                if (r8 == 0) goto L_0x007f
                r8 = 2131231063(0x7f080157, float:1.8078196E38)
                r6.setBackgroundResource(r8)
                android.content.Context r8 = r5.context
                android.content.res.Resources r8 = r8.getResources()
                int r8 = r8.getDimensionPixelSize(r3)
                android.content.Context r1 = r5.context
                android.content.res.Resources r1 = r1.getResources()
                int r1 = r1.getDimensionPixelSize(r4)
                android.content.Context r2 = r5.context
                android.content.res.Resources r2 = r2.getResources()
                int r2 = r2.getDimensionPixelSize(r4)
                r6.setPaddingRelative(r1, r8, r2, r0)
                goto L_0x00ce
            L_0x007f:
                boolean r8 = r7.isBottomRow
                if (r8 == 0) goto L_0x00bd
                goto L_0x0092
            L_0x0086:
                boolean r8 = r7.isBottomRow
                if (r8 == 0) goto L_0x00b7
                boolean r8 = r7.isTopRow
                if (r8 == 0) goto L_0x00b7
            L_0x0092:
                r6.setBackgroundResource(r1)
                android.content.Context r8 = r5.context
                android.content.res.Resources r8 = r8.getResources()
                int r8 = r8.getDimensionPixelSize(r3)
                android.content.Context r1 = r5.context
                android.content.res.Resources r1 = r1.getResources()
                int r1 = r1.getDimensionPixelSize(r4)
                android.content.Context r2 = r5.context
                android.content.res.Resources r2 = r2.getResources()
                int r2 = r2.getDimensionPixelSize(r4)
                r6.setPaddingRelative(r1, r0, r2, r8)
                goto L_0x00ce
            L_0x00b7:
                boolean r8 = r7.isTopRow
                if (r8 == 0) goto L_0x00c1
            L_0x00bd:
                r6.setBackgroundResource(r2)
                goto L_0x00ce
            L_0x00c1:
                boolean r8 = r7.isBottomRow
                if (r8 == 0) goto L_0x00bd
                goto L_0x0092
            L_0x00c8:
                r8 = 2131231054(0x7f08014e, float:1.8078178E38)
                r6.setBackgroundResource(r8)
            L_0x00ce:
                com.miui.common.card.models.NewsCardModel$NewsViewHolder$2 r8 = new com.miui.common.card.models.NewsCardModel$NewsViewHolder$2
                r8.<init>(r7)
                r6.setOnClickListener(r8)
                android.widget.ImageView r6 = r5.imageView
                if (r6 == 0) goto L_0x00e7
                java.lang.String[] r6 = r7.images
                r6 = r6[r0]
                android.widget.ImageView r7 = r5.imageView
                b.c.a.b.d r8 = r5.newsCardModelOptions
                b.b.c.j.r.a((java.lang.String) r6, (android.widget.ImageView) r7, (b.c.a.b.d) r8)
            L_0x00e7:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.common.card.models.NewsCardModel.NewsViewHolder.fillData(android.view.View, com.miui.common.card.models.BaseCardModel, int):void");
        }

        /* access modifiers changed from: protected */
        public void setIconDisplayOption(d dVar) {
            this.newsCardModelOptions = dVar;
        }
    }

    public NewsCardModel(int i, JSONObject jSONObject, int i2) {
        super(i);
        init(jSONObject);
        this.usePosition = i2;
    }

    private void init(JSONObject jSONObject) {
        if (jSONObject != null) {
            this.newsId = jSONObject.optString("newsId");
            this.title = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
            this.url = jSONObject.optString(MijiaAlertModel.KEY_URL);
            this.summary = jSONObject.optString("summary");
            this.source = jSONObject.optString("source");
            this.newsDate = jSONObject.optLong("newsDate");
            this.template = jSONObject.optInt("template");
            this.cornerTip = jSONObject.optString("cornerTip");
            this.views = jSONObject.optString("views");
            this.dataId = jSONObject.optString("dataId");
            JSONArray optJSONArray = jSONObject.optJSONArray("images");
            if (optJSONArray != null) {
                int length = optJSONArray.length();
                int i = 0;
                while (i < 3 && i < length) {
                    this.images[i] = optJSONArray.optString(i);
                    i++;
                }
            }
        }
    }

    public static NewsCardModel parse(int i, int i2, JSONObject jSONObject, TitleCardModel titleCardModel) {
        if (i2 != 7) {
            return null;
        }
        NewsListBannerCardModel newsListBannerCardModel = new NewsListBannerCardModel(jSONObject, i);
        if (titleCardModel != null) {
            titleCardModel.setSubCardModelTemplate(i2);
        }
        return newsListBannerCardModel;
    }

    public BaseViewHolder createViewHolder(View view) {
        return new NewsViewHolder(view);
    }

    public String getCardId() {
        return this.cardId;
    }

    public String getCornerTip() {
        return this.cornerTip;
    }

    public String getDetailTitle() {
        return this.detailTitle;
    }

    public String[] getImages() {
        return this.images;
    }

    public long getNewsDate() {
        return this.newsDate;
    }

    public String getNewsId() {
        return this.newsId;
    }

    public String getSource() {
        return this.source;
    }

    public int getTemplate() {
        return this.template;
    }

    public String getUrl() {
        return this.url;
    }

    public String getViews() {
        return this.views;
    }

    public void onClick(View view) {
        if (!f.a()) {
            i.c(view.getContext(), this.url, this.detailTitle);
            int i = this.usePosition;
            if (i == 1) {
                G.x(this.dataId);
            } else if (i == 2) {
                G.s(this.dataId);
            } else if (i == 3) {
                G.l(this.dataId);
            }
        }
    }

    public void setBottomRow(boolean z) {
        this.isBottomRow = z;
    }

    public void setCardId(String str) {
        this.cardId = str;
    }

    public void setCornerTip(String str) {
        this.cornerTip = str;
    }

    public void setDetailTitle(String str) {
        this.detailTitle = str;
    }

    public void setImages(String[] strArr) {
        this.images = strArr;
    }

    public void setNewsDate(long j) {
        this.newsDate = j;
    }

    public void setNewsId(String str) {
        this.newsId = str;
    }

    public void setPreviousLine(boolean z) {
        this.previousCardModelIsBlankLine = z;
    }

    public void setSource(String str) {
        this.source = str;
    }

    public void setTemplate(int i) {
        this.template = i;
    }

    public void setTopRow(boolean z) {
        this.isTopRow = z;
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public void setViews(String str) {
        this.views = str;
    }

    public boolean validate() {
        return true;
    }
}
