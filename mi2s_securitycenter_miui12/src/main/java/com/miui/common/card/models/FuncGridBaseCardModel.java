package com.miui.common.card.models;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.A;
import b.b.c.j.x;
import b.c.a.b.d;
import com.miui.cleanmaster.g;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.GridFunctionData;
import com.miui.securitycenter.R;
import com.miui.securityscan.a.G;
import com.miui.securityscan.c.e;
import com.miui.securityscan.cards.n;
import com.miui.securityscan.model.AbsModel;
import d.a.a.a;
import d.a.b;
import d.a.f;
import d.a.j;

public class FuncGridBaseCardModel extends FunctionCardModel {
    private static final int COLUMS = 3;
    /* access modifiers changed from: private */
    public boolean isBottomRow;
    /* access modifiers changed from: private */
    public boolean isTopRow;
    /* access modifiers changed from: private */
    public boolean previousCardModelIsBlankLine;

    public static class FuncGridBaseViewHolder extends BaseViewHolder implements View.OnClickListener {
        private static final String TAG = "FuncGrid4ViewHolder";
        private int cardColorfulPaddingBottom;
        private int cardColorfulPaddingTop;
        private Context context;
        private View functionView1;
        private View functionView2;
        private View functionView3;
        private View[] functionViews;
        private ImageView iconImageView1;
        private ImageView iconImageView2;
        private ImageView iconImageView3;
        private ImageView[] iconViews;
        private n menuFuncBinder;
        private int middleItemPaddingBottom;
        public d options;
        private TextView titleTextView1;
        private TextView titleTextView2;
        private TextView titleTextView3;
        private TextView[] titleViews;

        public FuncGridBaseViewHolder(View view) {
            super(view);
            d.a aVar = new d.a();
            aVar.a(true);
            aVar.b(true);
            aVar.c(true);
            this.options = aVar.a();
            this.context = view.getContext();
            Resources resources = this.context.getResources();
            this.cardColorfulPaddingBottom = resources.getDimensionPixelSize(R.dimen.nine_pices_card_colorful_padding_bottom);
            this.cardColorfulPaddingTop = resources.getDimensionPixelSize(R.dimen.nine_pices_card_colorful_padding_top);
            this.middleItemPaddingBottom = resources.getDimensionPixelSize(R.dimen.nine_pices_card_middle_item_padding_bottom);
            initView(view);
        }

        private void fillIconViews(ImageView imageView, int i) {
            n nVar = this.menuFuncBinder;
            Drawable a2 = nVar != null ? nVar.a(i) : null;
            if (a2 != null) {
                imageView.setImageDrawable(a2);
            } else {
                imageView.setImageResource(i);
            }
        }

        public void bindData(int i, Object obj) {
            if (obj != null && (obj instanceof n)) {
                this.menuFuncBinder = (n) obj;
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:14:0x004b, code lost:
            if (com.miui.common.card.models.FuncGridBaseCardModel.access$100(r9) != false) goto L_0x005a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0058, code lost:
            if (com.miui.common.card.models.FuncGridBaseCardModel.access$200(r9) != false) goto L_0x005a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:0x0079, code lost:
            if (com.miui.common.card.models.FuncGridBaseCardModel.access$100(r9) != false) goto L_0x005a;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void fillData(android.view.View r7, com.miui.common.card.models.BaseCardModel r8, int r9) {
            /*
                r6 = this;
                r9 = 2
                r7.setImportantForAccessibility(r9)
                r9 = r8
                com.miui.common.card.models.FuncGridBaseCardModel r9 = (com.miui.common.card.models.FuncGridBaseCardModel) r9
                java.util.List r0 = r9.getGridFunctionDataList()
                boolean r1 = r9 instanceof com.miui.common.card.models.FuncGrid9ColorfulCardModel
                r2 = 2131231056(0x7f080150, float:1.8078182E38)
                r3 = 0
                if (r1 == 0) goto L_0x007c
                boolean r1 = r9.previousCardModelIsBlankLine
                r4 = 2131231620(0x7f080384, float:1.8079326E38)
                if (r1 == 0) goto L_0x004e
                boolean r1 = r9.isBottomRow
                if (r1 == 0) goto L_0x0033
                boolean r1 = r9.isTopRow
                if (r1 == 0) goto L_0x0033
                r9 = 2131231623(0x7f080387, float:1.8079332E38)
                r7.setBackgroundResource(r9)
                int r9 = r6.cardColorfulPaddingTop
                int r1 = r6.cardColorfulPaddingBottom
                goto L_0x0043
            L_0x0033:
                boolean r1 = r9.isTopRow
                if (r1 == 0) goto L_0x0047
                r9 = 2131231625(0x7f080389, float:1.8079336E38)
                r7.setBackgroundResource(r9)
                int r9 = r6.cardColorfulPaddingTop
                int r1 = r6.middleItemPaddingBottom
            L_0x0043:
                r7.setPaddingRelative(r3, r9, r3, r1)
                goto L_0x0082
            L_0x0047:
                boolean r9 = r9.isBottomRow
                if (r9 == 0) goto L_0x006c
                goto L_0x005a
            L_0x004e:
                boolean r1 = r9.isBottomRow
                if (r1 == 0) goto L_0x0066
                boolean r1 = r9.isTopRow
                if (r1 == 0) goto L_0x0066
            L_0x005a:
                r7.setBackgroundResource(r4)
                int r9 = r6.cardColorfulPaddingBottom
                r7.setPaddingRelative(r3, r3, r3, r9)
                r7.invalidate()
                goto L_0x0082
            L_0x0066:
                boolean r1 = r9.isTopRow
                if (r1 == 0) goto L_0x0075
            L_0x006c:
                r7.setBackgroundResource(r2)
                int r9 = r6.middleItemPaddingBottom
                r7.setPaddingRelative(r3, r3, r3, r9)
                goto L_0x0082
            L_0x0075:
                boolean r9 = r9.isBottomRow
                if (r9 == 0) goto L_0x006c
                goto L_0x005a
            L_0x007c:
                r7.setBackgroundResource(r2)
                r7.setPadding(r3, r3, r3, r3)
            L_0x0082:
                if (r0 == 0) goto L_0x0120
                boolean r7 = r0.isEmpty()
                if (r7 != 0) goto L_0x0120
                java.util.ArrayList r7 = new java.util.ArrayList
                r7.<init>()
                r9 = r3
            L_0x0090:
                android.view.View[] r1 = r6.functionViews
                int r1 = r1.length
                if (r9 >= r1) goto L_0x0115
                int r1 = r0.size()
                if (r9 >= r1) goto L_0x0101
                java.lang.Object r1 = r0.get(r9)
                com.miui.common.card.GridFunctionData r1 = (com.miui.common.card.GridFunctionData) r1
                android.view.View[] r2 = r6.functionViews
                r2 = r2[r9]
                r2.setVisibility(r3)
                android.view.View[] r2 = r6.functionViews
                r2 = r2[r9]
                r2.setTag(r1)
                android.widget.TextView[] r2 = r6.titleViews
                r2 = r2[r9]
                java.lang.String r4 = r1.getTitle()
                r2.setText(r4)
                android.widget.TextView[] r2 = r6.titleViews
                r2 = r2[r9]
                boolean r4 = r1.isMarquee()
                r5 = 1
                r4 = r4 ^ r5
                r2.setSelected(r4)
                r1.setMarquee(r5)
                boolean r2 = r1.isUseLocalPic()
                if (r2 == 0) goto L_0x00dc
                android.widget.ImageView[] r2 = r6.iconViews
                r2 = r2[r9]
                int r4 = r1.getLocalPicResoourceId()
                r6.fillIconViews(r2, r4)
                goto L_0x00fd
            L_0x00dc:
                java.lang.String r2 = r1.getIcon()
                boolean r4 = android.text.TextUtils.isEmpty(r2)
                if (r4 != 0) goto L_0x00f0
                android.widget.ImageView[] r4 = r6.iconViews
                r4 = r4[r9]
                b.c.a.b.d r5 = r6.options
                b.b.c.j.r.a((java.lang.String) r2, (android.widget.ImageView) r4, (b.c.a.b.d) r5)
                goto L_0x00fd
            L_0x00f0:
                int r2 = r1.getIconResourceId()
                if (r2 == 0) goto L_0x00fd
                android.widget.ImageView[] r4 = r6.iconViews
                r4 = r4[r9]
                r6.fillIconViews(r4, r2)
            L_0x00fd:
                r7.add(r1)
                goto L_0x0111
            L_0x0101:
                android.view.View[] r1 = r6.functionViews
                r1 = r1[r9]
                r2 = 4
                r1.setVisibility(r2)
                android.view.View[] r1 = r6.functionViews
                r1 = r1[r9]
                r2 = 0
                r1.setTag(r2)
            L_0x0111:
                int r9 = r9 + 1
                goto L_0x0090
            L_0x0115:
                boolean r8 = r8.isDefaultStatShow()
                if (r8 == 0) goto L_0x0120
                android.content.Context r8 = r6.context
                com.miui.securityscan.a.G.b((android.content.Context) r8, (java.util.List<com.miui.common.card.GridFunctionData>) r7)
            L_0x0120:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.common.card.models.FuncGridBaseCardModel.FuncGridBaseViewHolder.fillData(android.view.View, com.miui.common.card.models.BaseCardModel, int):void");
        }

        public void initView(View view) {
            this.functionView1 = view.findViewById(R.id.column1);
            this.iconImageView1 = (ImageView) view.findViewById(R.id.iv_icon1);
            this.titleTextView1 = (TextView) view.findViewById(R.id.tv_title1);
            if (A.a()) {
                try {
                    f a2 = b.a(this.functionView1);
                    a2.touch().setTint(0.2f, 0.0f, 0.0f, 0.0f);
                    a2.touch().a(1.0f, j.a.DOWN);
                    a2.touch().a(this.functionView1, new a[0]);
                } catch (Throwable unused) {
                    Log.e(TAG, "no support folme");
                }
            }
            this.functionView2 = view.findViewById(R.id.column2);
            this.iconImageView2 = (ImageView) view.findViewById(R.id.iv_icon2);
            this.titleTextView2 = (TextView) view.findViewById(R.id.tv_title2);
            if (A.a()) {
                try {
                    f a3 = b.a(this.functionView2);
                    a3.touch().setTint(0.2f, 0.0f, 0.0f, 0.0f);
                    a3.touch().a(1.0f, j.a.DOWN);
                    a3.touch().a(this.functionView2, new a[0]);
                } catch (Throwable unused2) {
                    Log.e(TAG, "no support folme");
                }
            }
            this.functionView3 = view.findViewById(R.id.column3);
            this.iconImageView3 = (ImageView) view.findViewById(R.id.iv_icon3);
            this.titleTextView3 = (TextView) view.findViewById(R.id.tv_title3);
            if (A.a()) {
                try {
                    f a4 = b.a(this.functionView3);
                    a4.touch().setTint(0.2f, 0.0f, 0.0f, 0.0f);
                    a4.touch().a(1.0f, j.a.DOWN);
                    a4.touch().a(this.functionView3, new a[0]);
                } catch (Throwable unused3) {
                    Log.e(TAG, "no support folme");
                }
            }
            this.functionViews = new View[]{this.functionView1, this.functionView2, this.functionView3};
            int i = 0;
            while (true) {
                View[] viewArr = this.functionViews;
                if (i < viewArr.length) {
                    viewArr[i].setOnClickListener(this);
                    i++;
                } else {
                    this.titleViews = new TextView[]{this.titleTextView1, this.titleTextView2, this.titleTextView3};
                    this.iconViews = new ImageView[]{this.iconImageView1, this.iconImageView2, this.iconImageView3};
                    return;
                }
            }
        }

        public void onClick(View view) {
            Object tag = view.getTag();
            if (tag != null && (tag instanceof GridFunctionData)) {
                GridFunctionData gridFunctionData = (GridFunctionData) tag;
                String action = gridFunctionData.getAction();
                if (!TextUtils.isEmpty(action)) {
                    try {
                        Intent parseUri = Intent.parseUri(action, 0);
                        parseUri.putExtra("enter_homepage_way", "00001");
                        parseUri.putExtra("track_gamebooster_enter_way", "00001");
                        if ("#Intent;action=miui.intent.action.APP_MANAGER;end".equals(action)) {
                            parseUri.putExtra("enter_way", "com.miui.securitycenter");
                        }
                        if (FunctionCardModel.SHOW_ACTION_WHITE_LIST.contains(action)) {
                            g.b(this.context, parseUri);
                        } else if (!x.c(this.context, parseUri)) {
                            A.a(this.context, (int) R.string.app_not_installed_toast);
                        }
                        String statKey = gridFunctionData.getStatKey();
                        if (!TextUtils.isEmpty(statKey)) {
                            G.w(statKey);
                        }
                        if ("#Intent;action=com.miui.gamebooster.action.ACCESS_MAINACTIVITY;S.jump_target=gamebox;end".equals(action)) {
                            G.b(this.context);
                        }
                        e.a(this.context, "data_config").c("is_homepage_operated", true);
                    } catch (Exception e) {
                        Log.e(TAG, "onClick error:", e);
                    }
                }
            }
        }
    }

    public FuncGridBaseCardModel(int i, AbsModel absModel) {
        super(i, absModel);
    }

    public BaseViewHolder createViewHolder(View view) {
        return new FuncGridBaseViewHolder(view);
    }

    public void setBottomRow(boolean z) {
        this.isBottomRow = z;
    }

    public void setPreviousLine(boolean z) {
        this.previousCardModelIsBlankLine = z;
    }

    public void setTopRow(boolean z) {
        this.isTopRow = z;
    }
}
