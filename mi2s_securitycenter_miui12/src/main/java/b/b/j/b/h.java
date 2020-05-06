package b.b.j.b;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.A;
import b.b.c.j.x;
import b.b.j.d.b;
import b.c.a.b.d;
import com.miui.applicationlock.c.K;
import com.miui.appmanager.AppManageUtils;
import com.miui.appmanager.i;
import com.miui.cleanmaster.g;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.GridFunctionData;
import com.miui.common.card.models.FunctionCardModel;
import com.miui.securitycenter.R;
import com.miui.securityscan.MainActivity;
import com.miui.securityscan.a.G;
import com.miui.securityscan.cards.n;
import com.miui.securityscan.model.AbsModel;
import d.a.f;
import d.a.j;
import miui.cloud.Constants;
import miui.cloud.finddevice.FindDeviceStatusManagerProvider;

public class h extends FunctionCardModel {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public boolean f1806a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public boolean f1807b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public boolean f1808c;

    public static class a extends BaseViewHolder implements View.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        protected View f1809a;

        /* renamed from: b  reason: collision with root package name */
        private ImageView f1810b;

        /* renamed from: c  reason: collision with root package name */
        private TextView f1811c;

        /* renamed from: d  reason: collision with root package name */
        private TextView f1812d;
        protected View e;
        private ImageView f;
        private TextView g;
        private TextView h;
        protected View i;
        private ImageView j;
        private TextView k;
        private TextView l;
        protected View m;
        private ImageView n;
        private TextView o;
        private TextView p;
        private View[] q;
        private ImageView[] r;
        protected TextView[] s;
        private TextView[] t;
        private View u;
        private n v;
        private Context w;
        private i x = new i(this.w);
        private int y = this.w.getResources().getDimensionPixelSize(R.dimen.phone_manage_item_card_margin_bm);
        public d z;

        public a(View view) {
            super(view);
            d.a aVar = new d.a();
            aVar.a(true);
            aVar.b(true);
            aVar.c(true);
            this.z = aVar.a();
            this.w = view.getContext();
            initView(view);
        }

        private void a(Context context, Intent intent) {
            if (!(context instanceof Activity)) {
                return;
            }
            if (K.c(context)) {
                intent.putExtra("state_open", FindDeviceStatusManagerProvider.isOpen(context.getApplicationContext()));
                if (!x.c(context, intent)) {
                    A.a(context, (int) R.string.app_not_installed_toast);
                    return;
                }
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putBoolean("show_sync_settings", true);
            AccountManager.get(context).addAccount(Constants.XIAOMI_ACCOUNT_TYPE, (String) null, (String[]) null, bundle, (Activity) context, (AccountManagerCallback) null, (Handler) null);
        }

        private void a(View view, String str) {
            if ("#Intent;action=miui.intent.action.GARBAGE_DEEPCLEAN_WECHAT;end".equals(str)) {
                b.b(false);
            } else if ("#Intent;action=miui.intent.action.GARBAGE_DEEPCLEAN_QQ;end".equals(str)) {
                b.a(false);
            } else if ("#Intent;action=com.xiaomi.market.UPDATE_APP_LIST;end".equals(str)) {
                AppManageUtils.e(AppManageUtils.a(0));
                AppManageUtils.a(true);
                ((MainActivity) view.getContext()).n();
            }
            ((TextView) view.findViewById(R.id.subscript_text)).setVisibility(4);
            ((MainActivity) view.getContext()).p();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0050, code lost:
            if (r5 > 500000000) goto L_0x0052;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0071, code lost:
            if (r5 > 500000000) goto L_0x0052;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void a(java.lang.String r8, android.widget.TextView r9) {
            /*
                r7 = this;
                if (r9 != 0) goto L_0x0003
                return
            L_0x0003:
                java.lang.String r0 = "#Intent;action=com.xiaomi.market.UPDATE_APP_LIST;end"
                boolean r0 = r0.equals(r8)
                r1 = 4
                r2 = 0
                if (r0 == 0) goto L_0x0037
                int r8 = com.miui.securityscan.M.d()
                com.miui.appmanager.i r0 = r7.x
                boolean r0 = r0.a()
                if (r8 <= 0) goto L_0x0074
                if (r0 == 0) goto L_0x0074
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                int r0 = com.miui.securityscan.M.d()
                r8.append(r0)
                java.lang.String r0 = ""
                r8.append(r0)
                java.lang.String r8 = r8.toString()
            L_0x0030:
                r9.setText(r8)
                r9.setVisibility(r2)
                goto L_0x0077
            L_0x0037:
                java.lang.String r0 = "#Intent;action=miui.intent.action.GARBAGE_DEEPCLEAN_WECHAT;end"
                boolean r0 = r0.equals(r8)
                r3 = 500000000(0x1dcd6500, double:2.47032823E-315)
                if (r0 == 0) goto L_0x005b
                android.content.Context r8 = r7.w
                long r5 = b.b.j.d.b.b((android.content.Context) r8)
                boolean r8 = b.b.j.d.b.b()
                if (r8 == 0) goto L_0x0074
                int r8 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
                if (r8 <= 0) goto L_0x0074
            L_0x0052:
                com.miui.securitycenter.Application r8 = com.miui.securitycenter.Application.d()
                java.lang.String r8 = b.b.c.j.n.d(r8, r5, r2)
                goto L_0x0030
            L_0x005b:
                java.lang.String r0 = "#Intent;action=miui.intent.action.GARBAGE_DEEPCLEAN_QQ;end"
                boolean r8 = r0.equals(r8)
                if (r8 == 0) goto L_0x0074
                android.content.Context r8 = r7.w
                long r5 = b.b.j.d.b.a((android.content.Context) r8)
                boolean r8 = b.b.j.d.b.a()
                if (r8 == 0) goto L_0x0074
                int r8 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
                if (r8 <= 0) goto L_0x0074
                goto L_0x0052
            L_0x0074:
                r9.setVisibility(r1)
            L_0x0077:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: b.b.j.b.h.a.a(java.lang.String, android.widget.TextView):void");
        }

        private void fillIconViews(ImageView imageView, int i2) {
            n nVar = this.v;
            Drawable a2 = nVar != null ? nVar.a(i2) : null;
            if (a2 != null) {
                imageView.setImageDrawable(a2);
            } else {
                imageView.setImageResource(i2);
            }
        }

        private void initView(View view) {
            this.f1809a = view.findViewById(R.id.column1);
            this.f1810b = (ImageView) this.f1809a.findViewById(R.id.icon);
            this.f1811c = (TextView) this.f1809a.findViewById(R.id.title);
            this.f1812d = (TextView) this.f1809a.findViewById(R.id.subscript_text);
            if (A.a()) {
                try {
                    f a2 = d.a.b.a(this.f1809a);
                    a2.touch().setTint(0.2f, 0.0f, 0.0f, 0.0f);
                    a2.touch().a(1.0f, j.a.DOWN);
                    a2.touch().a(this.f1809a, new d.a.a.a[0]);
                } catch (Throwable unused) {
                    Log.e("PhoneManagerItemViewHolder", "no support folme");
                }
            }
            this.e = view.findViewById(R.id.column2);
            this.f = (ImageView) this.e.findViewById(R.id.icon);
            this.g = (TextView) this.e.findViewById(R.id.title);
            this.h = (TextView) this.e.findViewById(R.id.subscript_text);
            if (A.a()) {
                try {
                    f a3 = d.a.b.a(this.e);
                    a3.touch().setTint(0.2f, 0.0f, 0.0f, 0.0f);
                    a3.touch().a(1.0f, j.a.DOWN);
                    a3.touch().a(this.e, new d.a.a.a[0]);
                } catch (Throwable unused2) {
                    Log.e("PhoneManagerItemViewHolder", "no support folme");
                }
            }
            this.i = view.findViewById(R.id.column3);
            this.j = (ImageView) this.i.findViewById(R.id.icon);
            this.k = (TextView) this.i.findViewById(R.id.title);
            this.l = (TextView) this.i.findViewById(R.id.subscript_text);
            if (A.a()) {
                try {
                    f a4 = d.a.b.a(this.i);
                    a4.touch().setTint(0.2f, 0.0f, 0.0f, 0.0f);
                    a4.touch().a(1.0f, j.a.DOWN);
                    a4.touch().a(this.i, new d.a.a.a[0]);
                } catch (Throwable unused3) {
                    Log.e("PhoneManagerItemViewHolder", "no support folme");
                }
            }
            this.m = view.findViewById(R.id.column4);
            this.n = (ImageView) this.m.findViewById(R.id.icon);
            this.o = (TextView) this.m.findViewById(R.id.title);
            this.p = (TextView) this.m.findViewById(R.id.subscript_text);
            if (A.a()) {
                try {
                    f a5 = d.a.b.a(this.m);
                    a5.touch().setTint(0.2f, 0.0f, 0.0f, 0.0f);
                    a5.touch().a(1.0f, j.a.DOWN);
                    a5.touch().a(this.m, new d.a.a.a[0]);
                } catch (Throwable unused4) {
                    Log.e("PhoneManagerItemViewHolder", "no support folme");
                }
            }
            this.u = view.findViewById(R.id.item_container);
            this.q = new View[]{this.f1809a, this.e, this.i, this.m};
            int i2 = 0;
            while (true) {
                View[] viewArr = this.q;
                if (i2 < viewArr.length) {
                    viewArr[i2].setOnClickListener(this);
                    i2++;
                } else {
                    this.s = new TextView[]{this.f1811c, this.g, this.k, this.o};
                    ImageView imageView = this.f1810b;
                    this.r = new ImageView[]{imageView, this.f, this.j, this.n};
                    imageView.setColorFilter(this.w.getResources().getColor(R.color.result_banner_icon_bg));
                    this.f.setColorFilter(this.w.getResources().getColor(R.color.result_banner_icon_bg));
                    this.j.setColorFilter(this.w.getResources().getColor(R.color.result_banner_icon_bg));
                    this.n.setColorFilter(this.w.getResources().getColor(R.color.result_banner_icon_bg));
                    this.t = new TextView[]{this.f1812d, this.h, this.l, this.p};
                    return;
                }
            }
        }

        public void bindData(int i2, Object obj) {
            if (obj != null && (obj instanceof n)) {
                this.v = (n) obj;
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:12:0x003c, code lost:
            if (b.b.j.b.h.b(r8) != false) goto L_0x004b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0049, code lost:
            if (b.b.j.b.h.c(r8) != false) goto L_0x004b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0072, code lost:
            if (b.b.j.b.h.b(r8) != false) goto L_0x004b;
         */
        /* JADX WARNING: Removed duplicated region for block: B:27:0x007d  */
        /* JADX WARNING: Removed duplicated region for block: B:50:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void fillData(android.view.View r7, com.miui.common.card.models.BaseCardModel r8, int r9) {
            /*
                r6 = this;
                r9 = 2
                r7.setImportantForAccessibility(r9)
                b.b.j.b.h r8 = (b.b.j.b.h) r8
                java.util.List r7 = r8.getGridFunctionDataList()
                boolean r9 = r8.f1808c
                r0 = 2131231620(0x7f080384, float:1.8079326E38)
                r1 = 2131231056(0x7f080150, float:1.8078182E38)
                r2 = 0
                if (r9 == 0) goto L_0x003f
                boolean r9 = r8.f1807b
                if (r9 == 0) goto L_0x0029
                boolean r9 = r8.f1806a
                if (r9 == 0) goto L_0x0029
                android.view.View r9 = r6.u
                r0 = 2131231623(0x7f080387, float:1.8079332E38)
                goto L_0x0034
            L_0x0029:
                boolean r9 = r8.f1806a
                if (r9 == 0) goto L_0x0038
                android.view.View r9 = r6.u
                r0 = 2131231625(0x7f080389, float:1.8079336E38)
            L_0x0034:
                r9.setBackgroundResource(r0)
                goto L_0x0068
            L_0x0038:
                boolean r9 = r8.f1807b
                if (r9 == 0) goto L_0x0063
                goto L_0x004b
            L_0x003f:
                boolean r9 = r8.f1807b
                if (r9 == 0) goto L_0x005d
                boolean r9 = r8.f1806a
                if (r9 == 0) goto L_0x005d
            L_0x004b:
                android.view.View r9 = r6.u
                r9.setBackgroundResource(r0)
                android.view.View r9 = r6.u
                int r0 = r6.y
                r9.setPadding(r2, r2, r2, r0)
                android.view.View r9 = r6.u
                r9.invalidate()
                goto L_0x0075
            L_0x005d:
                boolean r9 = r8.f1806a
                if (r9 == 0) goto L_0x006e
            L_0x0063:
                android.view.View r9 = r6.u
                r9.setBackgroundResource(r1)
            L_0x0068:
                android.view.View r9 = r6.u
                r9.setPadding(r2, r2, r2, r2)
                goto L_0x0075
            L_0x006e:
                boolean r9 = r8.f1807b
                if (r9 == 0) goto L_0x0063
                goto L_0x004b
            L_0x0075:
                if (r7 == 0) goto L_0x0118
                boolean r9 = r7.isEmpty()
                if (r9 != 0) goto L_0x0118
                java.util.ArrayList r9 = new java.util.ArrayList
                r9.<init>()
                r0 = r2
            L_0x0083:
                android.view.View[] r1 = r6.q
                int r1 = r1.length
                if (r0 >= r1) goto L_0x010d
                int r1 = r7.size()
                if (r0 >= r1) goto L_0x00f9
                java.lang.Object r1 = r7.get(r0)
                com.miui.common.card.GridFunctionData r1 = (com.miui.common.card.GridFunctionData) r1
                android.view.View[] r3 = r6.q
                r3 = r3[r0]
                r3.setVisibility(r2)
                android.view.View[] r3 = r6.q
                r3 = r3[r0]
                r3.setTag(r1)
                android.widget.TextView[] r3 = r6.s
                r3 = r3[r0]
                java.lang.String r4 = r1.getTitle()
                r3.setText(r4)
                android.widget.TextView[] r3 = r6.s
                r3 = r3[r0]
                boolean r4 = r1.isMarquee()
                r5 = 1
                r4 = r4 ^ r5
                r3.setSelected(r4)
                r1.setMarquee(r5)
                java.lang.String r3 = r1.getAction()
                android.widget.TextView[] r4 = r6.t
                r4 = r4[r0]
                r6.a((java.lang.String) r3, (android.widget.TextView) r4)
                boolean r3 = r1.isUseLocalPic()
                if (r3 == 0) goto L_0x00da
                android.widget.ImageView[] r3 = r6.r
                r3 = r3[r0]
                int r4 = r1.getLocalPicResoourceId()
                r6.fillIconViews(r3, r4)
                goto L_0x00f5
            L_0x00da:
                int r3 = r1.getIconResourceId()
                if (r3 == 0) goto L_0x00e8
                android.widget.ImageView[] r4 = r6.r
                r4 = r4[r0]
                r6.fillIconViews(r4, r3)
                goto L_0x00f5
            L_0x00e8:
                java.lang.String r3 = r1.getIcon()
                android.widget.ImageView[] r4 = r6.r
                r4 = r4[r0]
                b.c.a.b.d r5 = r6.z
                b.b.c.j.r.a((java.lang.String) r3, (android.widget.ImageView) r4, (b.c.a.b.d) r5)
            L_0x00f5:
                r9.add(r1)
                goto L_0x0109
            L_0x00f9:
                android.view.View[] r1 = r6.q
                r1 = r1[r0]
                r3 = 4
                r1.setVisibility(r3)
                android.view.View[] r1 = r6.q
                r1 = r1[r0]
                r3 = 0
                r1.setTag(r3)
            L_0x0109:
                int r0 = r0 + 1
                goto L_0x0083
            L_0x010d:
                boolean r7 = r8.isDefaultStatShow()
                if (r7 == 0) goto L_0x0118
                android.content.Context r7 = r6.w
                com.miui.securityscan.a.G.a((android.content.Context) r7, (java.util.List<com.miui.common.card.GridFunctionData>) r9)
            L_0x0118:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: b.b.j.b.h.a.fillData(android.view.View, com.miui.common.card.models.BaseCardModel, int):void");
        }

        public void onClick(View view) {
            Object tag = view.getTag();
            if (tag != null && (tag instanceof GridFunctionData)) {
                GridFunctionData gridFunctionData = (GridFunctionData) tag;
                String action = gridFunctionData.getAction();
                if (!TextUtils.isEmpty(action)) {
                    try {
                        Intent parseUri = Intent.parseUri(action, 0);
                        parseUri.putExtra("enter_homepage_way", "phone_manage");
                        if ("#Intent;action=com.xiaomi.market.UPDATE_APP_LIST;end".equals(action)) {
                            parseUri.putExtra("back", true);
                        }
                        if ("#Intent;action=miui.intent.action.APP_MANAGER;end".equals(action)) {
                            parseUri.putExtra("enter_way", "com.miui.securitycenter");
                        }
                        if ("#Intent;action=miui.intent.action.KIDMODE_ENTRANCE;end".equals(action)) {
                            parseUri.putExtra("enter_kid_space_channel", "phonemanage_page");
                        }
                        a(view, action);
                        if (FunctionCardModel.SHOW_ACTION_WHITE_LIST.contains(action)) {
                            g.b(this.w, parseUri);
                        } else if ("#Intent;component=com.miui.cloudservice/com.miui.cloudservice.ui.MiCloudFindDeviceStatusActivity;end".equals(action)) {
                            a(this.w, parseUri);
                        } else if (!x.c(this.w, parseUri)) {
                            A.a(this.w, (int) R.string.app_not_installed_toast);
                        }
                        com.miui.securitycenter.n.a().b(new g(this, action));
                    } catch (Exception e2) {
                        Log.e("PhoneManagerItemViewHolder", "onClick error:", e2);
                    }
                }
                String dataId = gridFunctionData.getDataId();
                if (TextUtils.isEmpty(dataId)) {
                    dataId = gridFunctionData.getStatKey();
                    if (TextUtils.isEmpty(dataId)) {
                        dataId = gridFunctionData.getAction();
                    }
                }
                G.t(dataId);
            }
        }
    }

    public h() {
        this((AbsModel) null);
    }

    public h(int i, AbsModel absModel) {
        super(i, absModel);
    }

    public h(AbsModel absModel) {
        super(R.layout.phone_manage_list_item_card, absModel);
    }

    public BaseViewHolder createViewHolder(View view) {
        a aVar = new a(view);
        d.a aVar2 = new d.a();
        aVar2.c((int) R.drawable.phone_manage_default_selector);
        aVar2.b((int) R.drawable.phone_manage_default_selector);
        aVar2.a((int) R.drawable.phone_manage_default_selector);
        aVar2.a(true);
        aVar2.b(true);
        aVar2.c(true);
        aVar.z = aVar2.a();
        return aVar;
    }

    public void setBottomRow(boolean z) {
        this.f1807b = z;
    }

    public void setTopRow(boolean z) {
        this.f1806a = z;
    }

    public boolean validate() {
        return true;
    }
}
