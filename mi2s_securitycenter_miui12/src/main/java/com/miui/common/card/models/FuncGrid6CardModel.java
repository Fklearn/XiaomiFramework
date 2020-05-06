package com.miui.common.card.models;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import b.b.c.j.A;
import b.b.c.j.l;
import b.b.c.j.x;
import b.c.a.b.d;
import com.miui.appmanager.AppManageUtils;
import com.miui.cleanmaster.g;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.GridFunctionData;
import com.miui.common.persistence.b;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.securityscan.M;
import com.miui.securityscan.a.G;
import com.miui.securityscan.c.e;
import com.miui.securityscan.cards.n;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.ScoreManager;
import com.miui.securityscan.ui.main.FuncGrid6ImageView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FuncGrid6CardModel extends FunctionCardModel {
    private static final long DAY = 86400000;
    private int currentRowIndex;

    private static class DisplayViewHolder {
        /* access modifiers changed from: private */
        public FuncGrid6ImageView icon;
        /* access modifiers changed from: private */
        public String remoteName;
        /* access modifiers changed from: private */
        public String remoteSummary;
        /* access modifiers changed from: private */
        public TextView summaryTextView;
        /* access modifiers changed from: private */
        public TextView titleTextView;

        public DisplayViewHolder(FuncGrid6ImageView funcGrid6ImageView, TextView textView, TextView textView2, GridFunctionData gridFunctionData) {
            this.icon = funcGrid6ImageView;
            this.titleTextView = textView;
            this.summaryTextView = textView2;
            this.remoteName = gridFunctionData.getTitle();
            this.remoteSummary = gridFunctionData.getSummary();
        }
    }

    public static class FuncGrid6ViewHolder extends BaseViewHolder implements View.OnClickListener {
        private static final String TAG = "FuncGrid6ViewHolder";
        private Context context;
        private View functionView1;
        private View functionView2;
        private View[] functionViews;
        private FuncGrid6ImageView iconImageView1;
        private FuncGrid6ImageView iconImageView2;
        private FuncGrid6ImageView[] iconViews;
        private n.c menuChangeListener;
        private n menuFuncBinder;
        private d options;
        private TextView summaryTextView1;
        private TextView summaryTextView2;
        private TextView[] summaryTextViews;
        private TextView titleTextView1;
        private TextView titleTextView2;
        private TextView[] titleTextViews;
        /* access modifiers changed from: private */
        public Map<String, DisplayViewHolder> viewMap = new HashMap();

        public FuncGrid6ViewHolder(View view) {
            super(view);
            d.a aVar = new d.a();
            aVar.c((int) R.drawable.phone_manage_default_selector);
            aVar.b((int) R.drawable.phone_manage_default_selector);
            aVar.a((int) R.drawable.phone_manage_default_selector);
            aVar.a(true);
            aVar.b(true);
            aVar.c(true);
            this.options = aVar.a();
            this.context = view.getContext();
            initView(view);
            this.menuChangeListener = new n.c() {
                public void onAntiSpamChange(boolean z) {
                    DisplayViewHolder displayViewHolder = (DisplayViewHolder) FuncGrid6ViewHolder.this.viewMap.get("#Intent;action=miui.intent.action.SET_FIREWALL;end");
                    if (displayViewHolder != null) {
                        FuncGrid6ViewHolder.this.refreshAntiSpam(z, displayViewHolder);
                    }
                }

                public void onAppManagerChange(boolean z) {
                    DisplayViewHolder displayViewHolder = (DisplayViewHolder) FuncGrid6ViewHolder.this.viewMap.get("#Intent;action=miui.intent.action.APP_MANAGER;end");
                    if (displayViewHolder != null) {
                        FuncGrid6ViewHolder.this.refreshAppManager(z, displayViewHolder);
                    }
                }

                public void onGarbageChange(boolean z, long j) {
                    DisplayViewHolder displayViewHolder = (DisplayViewHolder) FuncGrid6ViewHolder.this.viewMap.get("#Intent;action=miui.intent.action.GARBAGE_CLEANUP;end");
                    if (displayViewHolder != null) {
                        FuncGrid6ViewHolder.this.refreshCleanMaster(z, j, displayViewHolder);
                    }
                }

                public void onNetworkAssistChange(boolean z, boolean z2, long j, boolean z3) {
                    DisplayViewHolder displayViewHolder = (DisplayViewHolder) FuncGrid6ViewHolder.this.viewMap.get("#Intent;action=miui.intent.action.NETWORKASSISTANT_ENTRANCE;end");
                    if (displayViewHolder != null) {
                        FuncGrid6ViewHolder.this.refreshNetworkAssist(z, z2, j, z3, displayViewHolder);
                    }
                }

                public void onPowerCenterChange(boolean z, int i, boolean z2, int i2, String str) {
                    DisplayViewHolder displayViewHolder = (DisplayViewHolder) FuncGrid6ViewHolder.this.viewMap.get("#Intent;action=miui.intent.action.POWER_MANAGER;end");
                    if (displayViewHolder != null) {
                        FuncGrid6ViewHolder.this.refreshPowerCenter(z, i, z2, str, displayViewHolder);
                    }
                }

                public void onSecurityScanChange(boolean z) {
                    DisplayViewHolder displayViewHolder = (DisplayViewHolder) FuncGrid6ViewHolder.this.viewMap.get("#Intent;action=miui.intent.action.ANTI_VIRUS;end");
                    if (displayViewHolder != null) {
                        FuncGrid6ViewHolder.this.refreshSecurityScan(z, displayViewHolder);
                    }
                }
            };
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x002b, code lost:
            if (r0 != null) goto L_0x002d;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0031, code lost:
            r3.setImageResource(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:3:0x000e, code lost:
            if (r0 != null) goto L_0x002d;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void fillIconViews(com.miui.securityscan.ui.main.FuncGrid6ImageView r3, com.miui.common.card.GridFunctionData r4) {
            /*
                r2 = this;
                boolean r0 = r4.isUseLocalPic()
                if (r0 == 0) goto L_0x0011
                int r4 = r4.getLocalPicResoourceId()
                android.graphics.drawable.Drawable r0 = r2.getCacheDrawableByResId(r4)
                if (r0 == 0) goto L_0x0031
                goto L_0x002d
            L_0x0011:
                java.lang.String r0 = r4.getIcon()
                boolean r1 = android.text.TextUtils.isEmpty(r0)
                if (r1 != 0) goto L_0x0021
                b.c.a.b.d r4 = r2.options
                b.b.c.j.r.a((java.lang.String) r0, (android.widget.ImageView) r3, (b.c.a.b.d) r4)
                goto L_0x0034
            L_0x0021:
                int r4 = r4.getIconResourceId()
                if (r4 == 0) goto L_0x0034
                android.graphics.drawable.Drawable r0 = r2.getCacheDrawableByResId(r4)
                if (r0 == 0) goto L_0x0031
            L_0x002d:
                r3.setImageDrawable(r0)
                goto L_0x0034
            L_0x0031:
                r3.setImageResource(r4)
            L_0x0034:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.common.card.models.FuncGrid6CardModel.FuncGrid6ViewHolder.fillIconViews(com.miui.securityscan.ui.main.FuncGrid6ImageView, com.miui.common.card.GridFunctionData):void");
        }

        private Drawable getCacheDrawableByResId(int i) {
            n nVar = this.menuFuncBinder;
            if (nVar != null) {
                return nVar.a(i);
            }
            return null;
        }

        private int getNewAntispamCount() {
            return com.miui.antispam.db.d.c() + com.miui.antispam.db.d.b();
        }

        private void initView(View view) {
            this.functionView1 = view.findViewById(R.id.column1);
            if (!l.a(this.functionView1)) {
                updateFunctionView(this.functionView1);
            }
            this.functionView1.setOnClickListener(this);
            this.iconImageView1 = (FuncGrid6ImageView) view.findViewById(R.id.iv_icon1);
            this.titleTextView1 = (TextView) view.findViewById(R.id.tv_title1);
            this.summaryTextView1 = (TextView) view.findViewById(R.id.tv_summary1);
            this.functionView2 = view.findViewById(R.id.column2);
            if (!l.a(this.functionView2)) {
                updateFunctionView(this.functionView2);
            }
            this.functionView2.setOnClickListener(this);
            this.iconImageView2 = (FuncGrid6ImageView) view.findViewById(R.id.iv_icon2);
            this.titleTextView2 = (TextView) view.findViewById(R.id.tv_title2);
            this.summaryTextView2 = (TextView) view.findViewById(R.id.tv_summary2);
            this.functionViews = new View[]{this.functionView1, this.functionView2};
            this.titleTextViews = new TextView[]{this.titleTextView1, this.titleTextView2};
            this.summaryTextViews = new TextView[]{this.summaryTextView1, this.summaryTextView2};
            FuncGrid6ImageView funcGrid6ImageView = this.iconImageView1;
            this.iconViews = new FuncGrid6ImageView[]{funcGrid6ImageView, this.iconImageView2};
            funcGrid6ImageView.setColorFilter(this.context.getResources().getColor(R.color.result_banner_icon_bg));
            this.iconImageView2.setColorFilter(this.context.getResources().getColor(R.color.result_banner_icon_bg));
        }

        /* access modifiers changed from: private */
        public void refreshAntiSpam(boolean z, DisplayViewHolder displayViewHolder) {
            FuncGrid6ImageView access$700 = displayViewHolder.icon;
            TextView access$800 = displayViewHolder.titleTextView;
            String access$900 = displayViewHolder.remoteName;
            TextView access$1000 = displayViewHolder.summaryTextView;
            String access$1100 = displayViewHolder.remoteSummary;
            if (TextUtils.isEmpty(access$900)) {
                access$800.setText(n.d.ANTI_SPAM.b());
            } else {
                access$800.setText(access$900);
            }
            int newAntispamCount = getNewAntispamCount();
            boolean z2 = true;
            if (z || newAntispamCount <= 0) {
                if (TextUtils.isEmpty(access$1100)) {
                    access$1000.setText(n.d.ANTI_SPAM.c());
                } else {
                    access$1000.setText(access$1100);
                }
                z2 = false;
            } else {
                access$1000.setText(this.context.getResources().getQuantityString(R.plurals.menu_text_antispam_ex, newAntispamCount, new Object[]{Integer.valueOf(newAntispamCount)}));
            }
            int i = z2 ? R.drawable.grid_circular_anti_spam_tips_selector : R.drawable.grid_circular_anti_spam_selector;
            Drawable cacheDrawableByResId = getCacheDrawableByResId(i);
            if (cacheDrawableByResId != null) {
                access$700.setImageDrawable(cacheDrawableByResId);
            } else {
                access$700.setImageResource(i);
            }
            updateTitleAndSummary(access$800, access$1000, z2);
        }

        /* access modifiers changed from: private */
        public void refreshAppManager(boolean z, DisplayViewHolder displayViewHolder) {
            FuncGrid6ImageView access$700 = displayViewHolder.icon;
            TextView access$800 = displayViewHolder.titleTextView;
            String access$900 = displayViewHolder.remoteName;
            TextView access$1000 = displayViewHolder.summaryTextView;
            String access$1100 = displayViewHolder.remoteSummary;
            if (TextUtils.isEmpty(access$900)) {
                access$800.setText(n.d.APP_MANAGER.b());
            } else {
                access$800.setText(access$900);
            }
            int d2 = M.d();
            boolean z2 = true;
            if (z || d2 <= 0) {
                if (TextUtils.isEmpty(access$1100)) {
                    access$1000.setText(n.d.APP_MANAGER.c());
                } else {
                    access$1000.setText(access$1100);
                }
                z2 = false;
            } else {
                access$1000.setText(this.context.getResources().getQuantityString(R.plurals.menu_text_app_manager_ex, d2, new Object[]{Integer.valueOf(d2)}));
            }
            int i = z2 ? R.drawable.menu_icon_appmanager_tips_selector : R.drawable.menu_icon_appmanager_selector;
            Drawable cacheDrawableByResId = getCacheDrawableByResId(i);
            if (cacheDrawableByResId != null) {
                access$700.setImageDrawable(cacheDrawableByResId);
            } else {
                access$700.setImageResource(i);
            }
            updateTitleAndSummary(access$800, access$1000, z2);
        }

        /* access modifiers changed from: private */
        public void refreshCleanMaster(boolean z, long j, DisplayViewHolder displayViewHolder) {
            FuncGrid6ImageView access$700 = displayViewHolder.icon;
            TextView access$800 = displayViewHolder.titleTextView;
            String access$900 = displayViewHolder.remoteName;
            TextView access$1000 = displayViewHolder.summaryTextView;
            String access$1100 = displayViewHolder.remoteSummary;
            if (TextUtils.isEmpty(access$900)) {
                access$900 = FunctionCardModel.RESOURCE.getString(R.string.menu_text_garbage_cleanup);
            }
            access$800.setText(access$900);
            boolean z2 = true;
            if (z || j <= 0) {
                if (TextUtils.isEmpty(access$1100)) {
                    access$1100 = FunctionCardModel.RESOURCE.getString(R.string.menu_summary_garbage_cleanup);
                }
                access$1000.setText(access$1100);
                z2 = false;
            } else {
                access$1000.setText(this.context.getString(R.string.menu_summary_garbage_cleanup_ex, new Object[]{b.b.c.j.n.d(Application.d(), j, 0)}));
            }
            int i = z2 ? R.drawable.menu_icon_garbage_tips_selector : R.drawable.menu_icon_garbage_selector;
            Drawable cacheDrawableByResId = getCacheDrawableByResId(i);
            if (cacheDrawableByResId != null) {
                access$700.setImageDrawable(cacheDrawableByResId);
            } else {
                access$700.setImageResource(i);
            }
            updateTitleAndSummary(access$800, access$1000, z2);
        }

        /* access modifiers changed from: private */
        public void refreshNetworkAssist(boolean z, boolean z2, long j, boolean z3, DisplayViewHolder displayViewHolder) {
            String str;
            FuncGrid6ImageView access$700 = displayViewHolder.icon;
            TextView access$800 = displayViewHolder.titleTextView;
            String access$900 = displayViewHolder.remoteName;
            TextView access$1000 = displayViewHolder.summaryTextView;
            String access$1100 = displayViewHolder.remoteSummary;
            if (TextUtils.isEmpty(access$900)) {
                access$900 = this.context.getString(R.string.menu_text_networkassistants);
            }
            boolean z4 = false;
            if (z2) {
                String b2 = b.b.c.j.n.b(this.context, Math.abs(j), 0);
                if (j > 0) {
                    str = this.context.getString(R.string.menu_text_networkassistants_remain, new Object[]{b2});
                    z4 = !z;
                } else {
                    str = this.context.getString(R.string.menu_text_networkassistants_danger, new Object[]{b2});
                    z4 = true;
                }
            } else {
                if (TextUtils.isEmpty(access$1100)) {
                    access$1100 = this.context.getString(R.string.menu_summary_networkassistants);
                }
                str = access$1100;
            }
            access$800.setText(access$900);
            access$1000.setText(str);
            int i = z4 ? R.drawable.menu_icon_net_safe_tips_selector : R.drawable.menu_icon_net_safe_selector;
            Drawable cacheDrawableByResId = getCacheDrawableByResId(i);
            if (cacheDrawableByResId != null) {
                access$700.setImageDrawable(cacheDrawableByResId);
            } else {
                access$700.setImageResource(i);
            }
            updateTitleAndSummary(access$800, access$1000, z4);
        }

        /* access modifiers changed from: private */
        public void refreshPowerCenter(boolean z, int i, boolean z2, String str, DisplayViewHolder displayViewHolder) {
            FuncGrid6ImageView access$700 = displayViewHolder.icon;
            TextView access$800 = displayViewHolder.titleTextView;
            String access$900 = displayViewHolder.remoteName;
            TextView access$1000 = displayViewHolder.summaryTextView;
            String access$1100 = displayViewHolder.remoteSummary;
            if (TextUtils.isEmpty(access$900)) {
                access$900 = this.context.getString(R.string.menu_text_power_manager);
            }
            access$800.setText(access$900);
            boolean z3 = false;
            if (i != -1) {
                if (z) {
                    if (i == 100) {
                        access$1000.setText(this.context.getString(R.string.menu_summary_power_manager_1));
                    }
                } else if (!z2) {
                    access$1000.setText(this.context.getString(R.string.menu_summary_power_manager_3));
                    z3 = true;
                }
                access$1000.setText(str);
            } else {
                if (TextUtils.isEmpty(access$1100)) {
                    access$1100 = this.context.getString(R.string.menu_summary_power_manager);
                }
                access$1000.setText(access$1100);
            }
            int i2 = z3 ? R.drawable.menu_icon_power_safe_tips_selector : R.drawable.menu_icon_power_safe_selector;
            Drawable cacheDrawableByResId = getCacheDrawableByResId(i2);
            if (cacheDrawableByResId != null) {
                access$700.setImageDrawable(cacheDrawableByResId);
            } else {
                access$700.setImageResource(i2);
            }
            updateTitleAndSummary(access$800, access$1000, z3);
        }

        /* access modifiers changed from: private */
        public void refreshSecurityScan(boolean z, DisplayViewHolder displayViewHolder) {
            Resources resources;
            Object[] objArr;
            FuncGrid6ImageView access$700 = displayViewHolder.icon;
            TextView access$800 = displayViewHolder.titleTextView;
            String access$900 = displayViewHolder.remoteName;
            TextView access$1000 = displayViewHolder.summaryTextView;
            String access$1100 = displayViewHolder.remoteSummary;
            if (TextUtils.isEmpty(access$900)) {
                access$800.setText(n.d.SECURITY_SCAN.b());
            } else {
                access$800.setText(access$900);
            }
            boolean z2 = true;
            if (z) {
                if (TextUtils.isEmpty(access$1100)) {
                    access$1000.setText(n.d.SECURITY_SCAN.c());
                } else {
                    access$1000.setText(access$1100);
                }
                z2 = false;
            } else {
                int q = ScoreManager.e().q();
                int p = ScoreManager.e().p();
                if (q > 0 || p > 0) {
                    String str = null;
                    if (q > 0 && p > 0) {
                        q = Math.max(q, p);
                        resources = this.context.getResources();
                        objArr = new Object[]{Integer.valueOf(q)};
                    } else if (q > 0) {
                        resources = this.context.getResources();
                        objArr = new Object[]{Integer.valueOf(q)};
                    } else {
                        if (p > 0) {
                            str = this.context.getResources().getQuantityString(R.plurals.menu_text_antivirus_virus_ex, p, new Object[]{Integer.valueOf(p)});
                        }
                        access$1000.setText(str);
                    }
                    str = resources.getQuantityString(R.plurals.menu_text_antivirus_virus_ex, q, objArr);
                    access$1000.setText(str);
                } else {
                    int currentTimeMillis = (int) ((System.currentTimeMillis() - Settings.Secure.getLong(Application.d().getContentResolver(), "key_latest_virus_scan_date", 0)) / 86400000);
                    access$1000.setText(this.context.getResources().getQuantityString(R.plurals.menu_text_antivirus_day_ex, currentTimeMillis, new Object[]{Integer.valueOf(currentTimeMillis)}));
                }
            }
            int i = z2 ? R.drawable.menu_icon_virus_safe_tips_selector : R.drawable.menu_icon_virus_safe_selector;
            Drawable cacheDrawableByResId = getCacheDrawableByResId(i);
            if (cacheDrawableByResId != null) {
                access$700.setImageDrawable(cacheDrawableByResId);
            } else {
                access$700.setImageResource(i);
            }
            updateTitleAndSummary(access$800, access$1000, z2);
        }

        private void updateFunctionView(View view) {
            view.setBackgroundResource(R.drawable.hp_card_bg_no_shadow_selector2);
            view.setPaddingRelative(this.context.getResources().getDimensionPixelSize(R.dimen.six_pices_card_item_column_padding_start), this.context.getResources().getDimensionPixelSize(R.dimen.six_pices_card_item_column_padding_top), this.context.getResources().getDimensionPixelSize(R.dimen.six_pices_card_item_column_padding_end), view.getPaddingBottom());
        }

        private void updateTitleAndSummary(TextView textView, TextView textView2, boolean z) {
            int i;
            if (z) {
                textView.setTextColor(this.context.getResources().getColor(R.color.card_menu_button_text_red));
                i = this.context.getResources().getColor(R.color.card_menu_button_text_red);
            } else {
                textView.setTextColor(this.context.getResources().getColor(R.color.six_pices_card_item_title_textcolor));
                i = this.context.getResources().getColor(R.color.six_pices_card_item_summary_textcolor);
            }
            textView2.setTextColor(i);
        }

        public void bindData(int i, Object obj) {
            if (obj != null && (obj instanceof n)) {
                this.menuFuncBinder = (n) obj;
                this.menuFuncBinder.a(this.menuChangeListener);
            }
        }

        public void fillData(View view, BaseCardModel baseCardModel, int i) {
            view.setImportantForAccessibility(2);
            List<GridFunctionData> gridFunctionDataList = ((FuncGrid6CardModel) baseCardModel).getGridFunctionDataList();
            this.viewMap.clear();
            if (gridFunctionDataList != null && !gridFunctionDataList.isEmpty()) {
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < this.functionViews.length; i2++) {
                    if (i2 < gridFunctionDataList.size()) {
                        GridFunctionData gridFunctionData = gridFunctionDataList.get(i2);
                        String action = gridFunctionData.getAction();
                        this.functionViews[i2].setVisibility(0);
                        this.functionViews[i2].setTag(gridFunctionData);
                        this.titleTextViews[i2].setText(gridFunctionData.getTitle());
                        this.titleTextViews[i2].setTextColor(this.context.getResources().getColor(R.color.six_pices_card_item_title_textcolor));
                        this.summaryTextViews[i2].setText(gridFunctionData.getSummary());
                        this.summaryTextViews[i2].setTextColor(this.context.getResources().getColor(R.color.six_pices_card_item_summary_textcolor));
                        this.titleTextViews[i2].setTag(action);
                        this.summaryTextViews[i2].setTag(action);
                        this.iconViews[i2].setAction(action);
                        fillIconViews(this.iconViews[i2], gridFunctionData);
                        this.viewMap.put(action, new DisplayViewHolder(this.iconViews[i2], this.titleTextViews[i2], this.summaryTextViews[i2], gridFunctionData));
                        arrayList.add(gridFunctionData);
                    } else {
                        this.functionViews[i2].setVisibility(4);
                        this.functionViews[i2].setTag((Object) null);
                    }
                }
                if (baseCardModel.isDefaultStatShow()) {
                    G.b(this.context, (List<GridFunctionData>) arrayList);
                }
                n nVar = this.menuFuncBinder;
                if (nVar != null) {
                    nVar.a(this.menuChangeListener, this.viewMap.keySet());
                }
            }
        }

        public void onClick(View view) {
            DisplayViewHolder displayViewHolder;
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
                            b.b("app_manager_click", true);
                            b.b("app_manager_click_time", AppManageUtils.a(0));
                            DisplayViewHolder displayViewHolder2 = this.viewMap.get(action);
                            if (displayViewHolder2 != null) {
                                if (this.menuFuncBinder != null) {
                                    this.menuFuncBinder.o = true;
                                }
                                refreshAppManager(true, displayViewHolder2);
                            }
                            parseUri.putExtra("enter_way", "com.miui.securitycenter");
                        } else if ("#Intent;action=miui.intent.action.SET_FIREWALL;end".equals(action) && (displayViewHolder = this.viewMap.get(action)) != null) {
                            if (this.menuFuncBinder != null) {
                                this.menuFuncBinder.p = true;
                            }
                            refreshAntiSpam(true, displayViewHolder);
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

    public FuncGrid6CardModel() {
        this((AbsModel) null);
    }

    public FuncGrid6CardModel(AbsModel absModel) {
        super(R.layout.card_layout_grid_six_parent, absModel);
    }

    public BaseViewHolder createViewHolder(View view) {
        return new FuncGrid6ViewHolder(view);
    }

    public int getCurrentRowIndex() {
        return this.currentRowIndex;
    }

    public void setCurrentRowIndex(int i) {
        this.currentRowIndex = i;
    }
}
