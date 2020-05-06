package com.miui.common.card.models;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import b.b.c.j.C0194a;
import b.b.c.j.l;
import b.b.c.j.o;
import b.b.c.j.r;
import b.c.a.b.d;
import com.miui.common.card.BaseViewHolder;
import com.miui.common.card.FillParentDrawable;
import com.miui.common.card.GridFunctionData;
import com.miui.common.card.functions.BaseFunction;
import com.miui.common.card.functions.CommonFunction;
import com.miui.common.card.functions.FuncTopBannerScrollData;
import com.miui.maml.util.net.SimpleRequest;
import com.miui.networkassistant.provider.ProviderConstant;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.securityscan.a.G;
import com.miui.securityscan.cards.n;
import com.miui.securityscan.i.c;
import com.miui.securityscan.model.AbsModel;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.os.Build;
import org.json.JSONObject;

public class FunctionCardModel extends BaseCardModel {
    public static Map<String, Integer> LOCAL_FUNCTION_ICONS = new HashMap();
    protected static final Resources RESOURCE = Application.c();
    /* access modifiers changed from: protected */
    public static List<String> SHOW_ACTION_WHITE_LIST = new ArrayList();
    private static final String TAG = "FunctionCardModel";
    private static String miuiVersion = getMiuiVersion();
    private String ABtest;
    /* access modifiers changed from: private */
    public AbsModel curModel;
    private List<FuncTopBannerScrollData> funcTopBannerScrollDataList;
    private transient BaseFunction function;
    private int functionId;
    private List<GridFunctionData> gridFunctionDataList;
    private String imgUrl;
    private boolean isHomePageFunc;
    /* access modifiers changed from: private */
    public boolean isNoDivider;
    /* access modifiers changed from: private */
    public boolean needRemove;
    private int score;
    private String statKey;
    private int template;

    public static class FunctionViewHolder extends BaseViewHolder {
        static final String TAG = "FunctionViewHolder";
        Context context;
        View divider;
        d imgOption = r.f1760d;
        ImageView ivBigBanner;
        n menuFuncBinder;
        d option = r.g;

        public FunctionViewHolder(View view) {
            super(view);
            this.context = view.getContext();
            this.divider = view.findViewById(R.id.divider);
            this.ivBigBanner = (ImageView) view.findViewById(R.id.iv_big_banner);
            ImageView imageView = this.ivBigBanner;
            if (imageView != null) {
                imageView.setColorFilter(view.getResources().getColor(R.color.result_banner_icon_bg));
            }
        }

        public void bindData(int i, Object obj) {
            if (obj != null && (obj instanceof n)) {
                this.menuFuncBinder = (n) obj;
            }
        }

        public void fillData(View view, final BaseCardModel baseCardModel, int i) {
            super.fillData(view, baseCardModel, i);
            final FunctionCardModel functionCardModel = (FunctionCardModel) baseCardModel;
            final AbsModel access$000 = functionCardModel.curModel;
            if (!(access$000 == null || access$000.getOnAbsModelDisplayListener() == null)) {
                access$000.getOnAbsModelDisplayListener().onAbsModelDisplay();
            }
            final BaseFunction function = functionCardModel.getFunction();
            if (this.divider != null) {
                if (functionCardModel.isNoDivider) {
                    this.divider.setVisibility(8);
                } else {
                    this.divider.setVisibility(0);
                }
            }
            if (function != null) {
                AnonymousClass1 r2 = new View.OnClickListener() {
                    private void statEvent(FunctionCardModel functionCardModel, Context context) {
                        if (functionCardModel.isHomePageFunc()) {
                            String statKey = functionCardModel.getStatKey();
                            if (!TextUtils.isEmpty(statKey)) {
                                G.w(statKey);
                            }
                            if ("#Intent;action=com.miui.gamebooster.action.ACCESS_MAINACTIVITY;S.jump_target=gamebox;end".equals(function.getAction())) {
                                G.b(context);
                                return;
                            }
                            return;
                        }
                        AbsModel absModel = access$000;
                        if (absModel != null && !TextUtils.isEmpty(absModel.getTrackStr())) {
                            G.r(access$000.getTrackStr());
                        }
                    }

                    public void onClick(View view) {
                        statEvent(functionCardModel, FunctionViewHolder.this.context);
                        function.onClick(view);
                        if (functionCardModel.needRemove && FunctionViewHolder.this.handler != null) {
                            Message obtain = Message.obtain();
                            obtain.what = 109;
                            obtain.obj = functionCardModel;
                            FunctionViewHolder.this.handler.sendMessage(obtain);
                        }
                    }
                };
                l.a(view);
                view.setOnClickListener(r2);
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View view) {
                        AbsModel absModel = access$000;
                        if (absModel == null) {
                            return true;
                        }
                        FunctionViewHolder functionViewHolder = FunctionViewHolder.this;
                        functionViewHolder.showManualItemLongClickDialog(baseCardModel, absModel, functionViewHolder.context);
                        return true;
                    }
                });
                Button button = this.actionButton;
                if (button != null) {
                    button.setOnClickListener(r2);
                }
                Button button2 = this.tvButton;
                if (button2 != null) {
                    button2.setOnClickListener(r2);
                    if (functionCardModel.getScore() > 0) {
                        String charSequence = this.tvButton.getText().toString();
                        String quantityString = this.context.getResources().getQuantityString(R.plurals.optimize_result_button_add_score, functionCardModel.getScore(), new Object[]{Integer.valueOf(functionCardModel.getScore())});
                        Button button3 = this.tvButton;
                        button3.setText(charSequence + quantityString);
                    }
                }
            }
            Drawable drawable = null;
            if (this.imageView != null) {
                n nVar = this.menuFuncBinder;
                Drawable a2 = nVar != null ? nVar.a((int) R.drawable.card_icon_default) : null;
                String icon = functionCardModel.getIcon();
                if (!TextUtils.isEmpty(icon)) {
                    if (a2 != null) {
                        r.a(icon, this.imageView, this.option, a2);
                    } else {
                        r.a(icon, this.imageView, this.option, (int) R.drawable.card_icon_default);
                    }
                } else if (a2 != null) {
                    this.imageView.setImageDrawable(a2);
                } else {
                    this.imageView.setImageResource(R.drawable.card_icon_default);
                }
            }
            if (this.ivBigBanner != null) {
                n nVar2 = this.menuFuncBinder;
                if (nVar2 != null) {
                    drawable = nVar2.a((int) R.drawable.big_banner_background_default);
                }
                String imgUrl = functionCardModel.getImgUrl();
                if (TextUtils.isEmpty(imgUrl)) {
                    ImageView imageView = this.ivBigBanner;
                    if (drawable == null) {
                        drawable = new FillParentDrawable(this.context.getResources().getDrawable(R.drawable.big_banner_background_default));
                    }
                    imageView.setImageDrawable(drawable);
                } else if (!imgUrl.startsWith("drawable://") || (!(functionCardModel instanceof FuncTopBannerCardModel) && !(functionCardModel instanceof FuncTopBannerNewCardModel) && !(functionCardModel instanceof FuncTopBannerNew2CardModel))) {
                    ImageView imageView2 = this.ivBigBanner;
                    d dVar = this.imgOption;
                    if (drawable == null) {
                        drawable = new FillParentDrawable(this.context.getResources().getDrawable(R.drawable.big_banner_background_default));
                    }
                    r.a(imgUrl, imageView2, dVar, drawable);
                } else {
                    try {
                        this.ivBigBanner.setImageResource(Integer.valueOf(imgUrl.replace("drawable://", "")).intValue());
                    } catch (Exception e) {
                        Log.e(TAG, "the big banner set a image resource failed: ", e);
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void setIconDisplayOption(d dVar) {
            this.option = dVar;
        }

        /* access modifiers changed from: protected */
        public void setImgDisplayOption(d dVar) {
            this.imgOption = dVar;
        }
    }

    static {
        SHOW_ACTION_WHITE_LIST.add("#Intent;action=miui.intent.action.GARBAGE_CLEANUP;end");
        SHOW_ACTION_WHITE_LIST.add("#Intent;action=miui.intent.action.GARBAGE_DEEPCLEAN;end");
        SHOW_ACTION_WHITE_LIST.add("#Intent;action=miui.intent.action.GARBAGE_DEEPCLEAN_WECHAT;end");
        SHOW_ACTION_WHITE_LIST.add("#Intent;action=miui.intent.action.GARBAGE_DEEPCLEAN_QQ;end");
        SHOW_ACTION_WHITE_LIST.add("miui.intent.action.GARBAGE_UNINSTALL_APPS");
        SHOW_ACTION_WHITE_LIST.add("#Intent;component=com.miui.cleanmaster/com.miui.optimizecenter.deepclean.largefile.LargeFilesActivity;end");
        SHOW_ACTION_WHITE_LIST.add("#Intent;component=com.miui.cleanmaster/com.miui.optimizecenter.deepclean.video.VideoListActivity;end");
        SHOW_ACTION_WHITE_LIST.add("#Intent;component=com.miui.cleanmaster/com.miui.optimizecenter.similarimage.ImageCategoryListActivity;end");
        SHOW_ACTION_WHITE_LIST.add("#Intent;component=com.miui.cleanmaster/com.miui.optimizecenter.deepclean.apk.ApkListActivity;end");
        SHOW_ACTION_WHITE_LIST.add("#Intent;component=com.miui.cleanmaster/com.miui.optimizecenter.deepclean.appdata.AppDataActivity;end");
        if (Build.IS_INTERNATIONAL_BUILD) {
            LOCAL_FUNCTION_ICONS.put("2401-001", Integer.valueOf(R.drawable.menu_icon_garbage_selector));
            LOCAL_FUNCTION_ICONS.put("2401-002", Integer.valueOf(R.drawable.menu_icon_virus_safe_selector));
            LOCAL_FUNCTION_ICONS.put("2401-003", Integer.valueOf(R.drawable.menu_icon_power_safe_selector));
            LOCAL_FUNCTION_ICONS.put("2401-004", Integer.valueOf(R.drawable.menu_icon_net_safe_selector));
            LOCAL_FUNCTION_ICONS.put("2401-005", Integer.valueOf(R.drawable.grid_circular_game_boost_selector));
            LOCAL_FUNCTION_ICONS.put("2401-006", Integer.valueOf(R.drawable.menu_icon_appmanager_selector));
            LOCAL_FUNCTION_ICONS.put("2402-001", Integer.valueOf(R.drawable.phone_manage_deep_clean));
            LOCAL_FUNCTION_ICONS.put("2402-002", Integer.valueOf(R.drawable.phone_manage_dual_app));
            LOCAL_FUNCTION_ICONS.put("2402-003", Integer.valueOf(R.drawable.phone_manage_second_space));
            LOCAL_FUNCTION_ICONS.put("2402-004", Integer.valueOf(R.drawable.phone_manage_antispam));
            LOCAL_FUNCTION_ICONS.put("2402-005", Integer.valueOf(R.drawable.phone_manage_wechat_clean));
            LOCAL_FUNCTION_ICONS.put("2402-006", Integer.valueOf(R.drawable.phone_manage_qq_clean));
            LOCAL_FUNCTION_ICONS.put("2402-007", Integer.valueOf(R.drawable.phone_manage_lucky_money));
            LOCAL_FUNCTION_ICONS.put("2402-008", Integer.valueOf(R.drawable.phone_manage_network_detection));
            LOCAL_FUNCTION_ICONS.put("2402-009", Integer.valueOf(R.drawable.phone_manage_app_lock));
        }
    }

    public FunctionCardModel(int i, AbsModel absModel) {
        super(i);
        this.curModel = absModel;
    }

    private static int compareVersion(String str, String str2) {
        int i = 0;
        try {
            String[] split = str.split("\\.");
            String[] split2 = str2.split("\\.");
            int min = Math.min(split.length, split2.length);
            int i2 = 0;
            while (true) {
                if (i2 < min) {
                    int parseInt = Integer.parseInt(split[i2]);
                    int parseInt2 = Integer.parseInt(split2[i2]);
                    if (parseInt <= parseInt2) {
                        if (parseInt != parseInt2) {
                            i = -1;
                            break;
                        }
                        i2++;
                    } else {
                        i = 1;
                        break;
                    }
                } else {
                    break;
                }
            }
            return (i != 0 || split.length == split2.length) ? i : split.length - split2.length;
        } catch (Exception unused) {
            Log.e(TAG, "parse error version1 " + str + " version2 " + str2);
            return 0;
        }
    }

    private static String getMiuiVersion() {
        String str = Build.VERSION.INCREMENTAL;
        if (!TextUtils.isEmpty(str) && str.contains(".")) {
            try {
                if (miui.os.Build.IS_STABLE_VERSION) {
                    int indexOf = str.indexOf(46, str.indexOf(46, 0) + 1);
                    return indexOf != -1 ? str.substring(1, indexOf) : str.substring(1, str.length());
                }
                String[] split = str.split("\\.");
                String str2 = split[split.length - 1];
                int i = 0;
                for (int i2 = 0; i2 < str2.length(); i2++) {
                    Character valueOf = Character.valueOf(str2.charAt(i2));
                    if (valueOf.charValue() < '0' || valueOf.charValue() > '9') {
                        break;
                    }
                    i++;
                }
                return str.substring(0, str.length() - (str2.length() - i));
            } catch (Exception e) {
                Log.e(TAG, "getMiuiVersion error", e);
            }
        }
        return "";
    }

    private static String getReplaceImei() {
        try {
            return URLEncoder.encode(C0194a.a("RuPJ0BCJNiaPpPV9", c.b()), SimpleRequest.UTF8).replace("+", "%2B");
        } catch (Exception e) {
            Log.e(TAG, "getReplaceImei error:", e);
            return "";
        }
    }

    private static String handleDuplicateAction(String str) {
        return str == null ? "" : "#Intent;action=android.intent.action.SET_FIREWALL;end".equalsIgnoreCase(str) ? "#Intent;action=miui.intent.action.SET_FIREWALL;end" : "#Intent;action=android.intent.action.VIEW_DATA_USAGE_SUMMARY;end".equalsIgnoreCase(str) ? "#Intent;action=miui.intent.action.NETWORKASSISTANT_ENTRANCE;end" : str.trim();
    }

    private static void handleFuncTopBannerScrollDataList(JSONObject jSONObject, FunctionCardModel functionCardModel) {
        if (jSONObject != null && functionCardModel != null) {
            try {
                List funcTopBannerScrollDataList2 = functionCardModel.getFuncTopBannerScrollDataList();
                if (funcTopBannerScrollDataList2 == null) {
                    funcTopBannerScrollDataList2 = new ArrayList();
                }
                FuncTopBannerScrollData funcTopBannerScrollData = new FuncTopBannerScrollData();
                funcTopBannerScrollData.setButton(jSONObject.optString("button"));
                funcTopBannerScrollData.setIcon(jSONObject.optString(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON));
                funcTopBannerScrollData.setImgUrl(jSONObject.optString("imgUrl"));
                funcTopBannerScrollData.setStatKey(jSONObject.optString("statKey"));
                funcTopBannerScrollData.setSummary(jSONObject.optString("summary"));
                funcTopBannerScrollData.setTitle(jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME));
                funcTopBannerScrollData.setTemplate(jSONObject.optInt("template"));
                funcTopBannerScrollData.setAction(jSONObject.optString("action"));
                int optInt = jSONObject.optInt("functionId");
                String handleDuplicateAction = handleDuplicateAction(funcTopBannerScrollData.getAction());
                if (optInt == 10020001) {
                    Intent parseUri = Intent.parseUri(handleDuplicateAction, 0);
                    funcTopBannerScrollData.setCommonFunction(new CommonFunction(parseUri));
                    if (com.miui.securityscan.cards.d.a(parseUri) && isShowLocalFunction(handleDuplicateAction) && isCloudShowFunction(jSONObject)) {
                        funcTopBannerScrollDataList2.add(funcTopBannerScrollData);
                    }
                }
                functionCardModel.setFuncTopBannerScrollDataList(funcTopBannerScrollDataList2);
            } catch (Exception e) {
                Log.e(TAG, "handleFuncTopBannerScrollDataList error:", e);
            }
        }
    }

    private static void handleFunctionGrid(int i, JSONObject jSONObject, TitleCardModel titleCardModel) {
        List<GridFunctionData> list;
        GridFunctionData parseGridFunctionData;
        if (titleCardModel != null) {
            int subCardModelTemplate = titleCardModel.getSubCardModelTemplate();
            if (subCardModelTemplate == -1 || subCardModelTemplate == i) {
                try {
                    String handleDuplicateAction = handleDuplicateAction(jSONObject.optString("action"));
                    if (SHOW_ACTION_WHITE_LIST.contains(handleDuplicateAction)) {
                        if (titleCardModel.gridFunctionDataList == null) {
                            titleCardModel.gridFunctionDataList = new ArrayList();
                        }
                        list = titleCardModel.gridFunctionDataList;
                        parseGridFunctionData = parseGridFunctionData(jSONObject, handleDuplicateAction);
                    } else {
                        int optInt = jSONObject.optInt("functionId");
                        if (optInt == 10020001) {
                            if (com.miui.securityscan.cards.d.a(Intent.parseUri(handleDuplicateAction, 0))) {
                                if (titleCardModel.gridFunctionDataList == null) {
                                    titleCardModel.gridFunctionDataList = new ArrayList();
                                }
                                if (isShowLocalFunction(handleDuplicateAction) && isCloudShowFunction(jSONObject)) {
                                    list = titleCardModel.gridFunctionDataList;
                                    parseGridFunctionData = parseGridFunctionData(jSONObject, handleDuplicateAction);
                                }
                            }
                            titleCardModel.setSubCardModelTemplate(i);
                        }
                        if (optInt == 10020002 && "https://api.jr.mi.com/activity/security/?from=insads_security_property_entry&partnerId=128".equals(handleDuplicateAction)) {
                            if (titleCardModel.gridFunctionDataList == null) {
                                titleCardModel.gridFunctionDataList = new ArrayList();
                            }
                            list = titleCardModel.gridFunctionDataList;
                            parseGridFunctionData = parseGridFunctionData(jSONObject, handleDuplicateAction + "&encryptImei=" + getReplaceImei());
                        }
                        titleCardModel.setSubCardModelTemplate(i);
                    }
                    list.add(parseGridFunctionData);
                } catch (Exception e) {
                    Log.e(TAG, "parse function card model , module " + i + " error:", e);
                }
                titleCardModel.setSubCardModelTemplate(i);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00cd, code lost:
        if (r8 <= r10) goto L_0x00cf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0027, code lost:
        if (r6 == false) goto L_0x0029;
     */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x009c A[Catch:{ Exception -> 0x00a1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00b4 A[Catch:{ Exception -> 0x00b9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00c4 A[Catch:{ Exception -> 0x012c }] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00d2 A[Catch:{ Exception -> 0x012c }] */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00d3 A[Catch:{ Exception -> 0x012c }] */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00f6 A[Catch:{ Exception -> 0x012c }] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x011a  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x011e  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0125 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean isCloudShowFunction(org.json.JSONObject r11) {
        /*
            java.lang.String r0 = ","
            java.lang.String r1 = "unknown"
            java.lang.String r2 = "FunctionCardModel"
            r3 = 1
            boolean r4 = miui.os.Build.IS_ALPHA_BUILD     // Catch:{ Exception -> 0x012c }
            r5 = 0
            if (r4 == 0) goto L_0x002d
            java.lang.String r4 = "isAlphaSupport"
            boolean r4 = r11.optBoolean(r4, r3)     // Catch:{ Exception -> 0x012c }
            java.lang.String r6 = "alphaMiuiVersionStart"
            java.lang.String r6 = r11.optString(r6)     // Catch:{ Exception -> 0x012c }
            java.lang.String r7 = "alphaMiuiVersionEnd"
            java.lang.String r7 = r11.optString(r7)     // Catch:{ Exception -> 0x012c }
            java.lang.String r8 = miuiVersion     // Catch:{ Exception -> 0x012c }
            boolean r6 = isVersionContain(r6, r7, r8)     // Catch:{ Exception -> 0x012c }
            if (r4 == 0) goto L_0x0027
            goto L_0x0089
        L_0x0027:
            if (r6 != 0) goto L_0x002b
        L_0x0029:
            r6 = r3
            goto L_0x0089
        L_0x002b:
            r6 = r5
            goto L_0x0089
        L_0x002d:
            boolean r4 = miui.os.Build.IS_DEVELOPMENT_VERSION     // Catch:{ Exception -> 0x012c }
            if (r4 == 0) goto L_0x004f
            java.lang.String r4 = "isDevSupport"
            boolean r4 = r11.optBoolean(r4, r3)     // Catch:{ Exception -> 0x012c }
            java.lang.String r6 = "devMiuiVersionStart"
            java.lang.String r6 = r11.optString(r6)     // Catch:{ Exception -> 0x012c }
            java.lang.String r7 = "devMiuiVersionEnd"
            java.lang.String r7 = r11.optString(r7)     // Catch:{ Exception -> 0x012c }
            java.lang.String r8 = miuiVersion     // Catch:{ Exception -> 0x012c }
            boolean r6 = isVersionContain(r6, r7, r8)     // Catch:{ Exception -> 0x012c }
            if (r4 == 0) goto L_0x004c
            goto L_0x0089
        L_0x004c:
            if (r6 != 0) goto L_0x002b
            goto L_0x0029
        L_0x004f:
            java.lang.String r4 = "isStableSupport"
            boolean r4 = r11.optBoolean(r4, r3)     // Catch:{ Exception -> 0x012c }
            java.lang.String r6 = "stableMiuiVersionStart"
            java.lang.String r6 = r11.optString(r6)     // Catch:{ Exception -> 0x012c }
            java.lang.String r7 = "stableMiuiVersionEnd"
            java.lang.String r7 = r11.optString(r7)     // Catch:{ Exception -> 0x012c }
            int r8 = r6.length()     // Catch:{ Exception -> 0x012c }
            if (r8 <= r3) goto L_0x006f
            int r8 = r6.length()     // Catch:{ Exception -> 0x012c }
            java.lang.String r6 = r6.substring(r3, r8)     // Catch:{ Exception -> 0x012c }
        L_0x006f:
            int r8 = r7.length()     // Catch:{ Exception -> 0x012c }
            if (r8 <= r3) goto L_0x007d
            int r8 = r7.length()     // Catch:{ Exception -> 0x012c }
            java.lang.String r7 = r7.substring(r3, r8)     // Catch:{ Exception -> 0x012c }
        L_0x007d:
            java.lang.String r8 = miuiVersion     // Catch:{ Exception -> 0x012c }
            boolean r6 = isVersionContain(r6, r7, r8)     // Catch:{ Exception -> 0x012c }
            if (r4 == 0) goto L_0x0086
            goto L_0x0089
        L_0x0086:
            if (r6 != 0) goto L_0x002b
            goto L_0x0029
        L_0x0089:
            java.lang.String r4 = "isAndroidApiSupport"
            boolean r4 = r11.optBoolean(r4, r3)     // Catch:{ Exception -> 0x012c }
            r7 = -1
            java.lang.String r8 = "androidApiLevelStart"
            java.lang.String r8 = r11.optString(r8)     // Catch:{ Exception -> 0x00a1 }
            boolean r9 = android.text.TextUtils.isEmpty(r8)     // Catch:{ Exception -> 0x00a1 }
            if (r9 != 0) goto L_0x00a7
            int r8 = java.lang.Integer.parseInt(r8)     // Catch:{ Exception -> 0x00a1 }
            goto L_0x00a8
        L_0x00a1:
            r8 = move-exception
            java.lang.String r9 = "parseInt apiLevel start error"
            android.util.Log.e(r2, r9, r8)     // Catch:{ Exception -> 0x012c }
        L_0x00a7:
            r8 = r7
        L_0x00a8:
            java.lang.String r9 = "androidApiLevelEnd"
            java.lang.String r9 = r11.optString(r9)     // Catch:{ Exception -> 0x00b9 }
            boolean r10 = android.text.TextUtils.isEmpty(r9)     // Catch:{ Exception -> 0x00b9 }
            if (r10 != 0) goto L_0x00bf
            int r9 = java.lang.Integer.parseInt(r9)     // Catch:{ Exception -> 0x00b9 }
            goto L_0x00c0
        L_0x00b9:
            r9 = move-exception
            java.lang.String r10 = "parseInt apiLevel end error"
            android.util.Log.e(r2, r10, r9)     // Catch:{ Exception -> 0x012c }
        L_0x00bf:
            r9 = r7
        L_0x00c0:
            int r10 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x012c }
            if (r8 == r7) goto L_0x00cf
            if (r9 == r7) goto L_0x00cd
            if (r8 > r10) goto L_0x00cb
            if (r9 < r10) goto L_0x00cb
            goto L_0x00cf
        L_0x00cb:
            r7 = r5
            goto L_0x00d0
        L_0x00cd:
            if (r8 > r10) goto L_0x00cb
        L_0x00cf:
            r7 = r3
        L_0x00d0:
            if (r4 == 0) goto L_0x00d3
            goto L_0x00d8
        L_0x00d3:
            if (r7 != 0) goto L_0x00d7
            r7 = r3
            goto L_0x00d8
        L_0x00d7:
            r7 = r5
        L_0x00d8:
            java.lang.String r4 = "isDeviceSupport"
            boolean r4 = r11.optBoolean(r4, r3)     // Catch:{ Exception -> 0x012c }
            java.lang.String r8 = "devices"
            java.lang.String r11 = r11.optString(r8)     // Catch:{ Exception -> 0x012c }
            java.lang.String r8 = "ro.product.device"
            java.lang.String r8 = b.b.c.j.y.a((java.lang.String) r8, (java.lang.String) r1)     // Catch:{ Exception -> 0x012c }
            boolean r1 = r1.equals(r8)     // Catch:{ Exception -> 0x012c }
            if (r1 != 0) goto L_0x011a
            boolean r1 = android.text.TextUtils.isEmpty(r11)     // Catch:{ Exception -> 0x012c }
            if (r1 != 0) goto L_0x011a
            boolean r1 = r11.contains(r0)     // Catch:{ Exception -> 0x012c }
            if (r1 == 0) goto L_0x0115
            java.lang.String[] r11 = r11.split(r0)     // Catch:{ Exception -> 0x012c }
            r1 = r3
            r0 = r5
        L_0x0102:
            int r9 = r11.length     // Catch:{ Exception -> 0x012c }
            if (r0 >= r9) goto L_0x0113
            r1 = r11[r0]     // Catch:{ Exception -> 0x012c }
            boolean r1 = r8.equalsIgnoreCase(r1)     // Catch:{ Exception -> 0x012c }
            if (r1 == 0) goto L_0x010f
            r1 = r3
            goto L_0x0113
        L_0x010f:
            int r0 = r0 + 1
            r1 = r5
            goto L_0x0102
        L_0x0113:
            r11 = r1
            goto L_0x011b
        L_0x0115:
            boolean r11 = r8.equalsIgnoreCase(r11)     // Catch:{ Exception -> 0x012c }
            goto L_0x011b
        L_0x011a:
            r11 = r3
        L_0x011b:
            if (r4 == 0) goto L_0x011e
            goto L_0x0123
        L_0x011e:
            if (r11 != 0) goto L_0x0122
            r11 = r3
            goto L_0x0123
        L_0x0122:
            r11 = r5
        L_0x0123:
            if (r6 == 0) goto L_0x012a
            if (r7 == 0) goto L_0x012a
            if (r11 == 0) goto L_0x012a
            goto L_0x012b
        L_0x012a:
            r3 = r5
        L_0x012b:
            return r3
        L_0x012c:
            r11 = move-exception
            java.lang.String r0 = "cloud show function opt data error"
            android.util.Log.e(r2, r0, r11)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.common.card.models.FunctionCardModel.isCloudShowFunction(org.json.JSONObject):boolean");
    }

    private static boolean isShowLocalFunction(String str) {
        return o.a(str);
    }

    private static boolean isVersionContain(String str, String str2, String str3) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str3)) {
            return true;
        }
        if (TextUtils.isEmpty(str2)) {
            if (compareVersion(str3, str) >= 0) {
                return true;
            }
        } else if (compareVersion(str3, str) >= 0 && compareVersion(str3, str2) <= 0) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0043, code lost:
        r4.setHomePageFunc(true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0046, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0047, code lost:
        if (r4 == null) goto L_0x00fb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0049, code lost:
        if (r3 == null) goto L_0x00fb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x004b, code lost:
        r4.setTemplate(r2);
        r4.setTitle(r3.optString(com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity.TITLE_KEY_NAME));
        r4.setSummary(r3.optString("summary"));
        r4.setButton(r3.optString("button"));
        r4.setIcon(r3.optString(com.miui.networkassistant.provider.ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON));
        r4.setImgUrl(r3.optString("imgUrl"));
        r4.setStatKey(r3.optString("statKey"));
        r4.setABtest(r3.optString("ABtest"));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r2 = r3.optInt("functionId");
        r6 = handleDuplicateAction(r3.optString("action"));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x00a0, code lost:
        if (r2 != 10020001) goto L_0x00c4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00a2, code lost:
        r2 = android.content.Intent.parseUri(r6, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00aa, code lost:
        if (com.miui.securityscan.cards.d.a(r2) == false) goto L_0x00c1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00b0, code lost:
        if (isShowLocalFunction(r6) == false) goto L_0x00c1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00b6, code lost:
        if (isCloudShowFunction(r3) == false) goto L_0x00c1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00b8, code lost:
        r3 = new com.miui.common.card.functions.CommonFunction(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00bd, code lost:
        r4.setFunction(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00c1, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00c2, code lost:
        r5 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00c7, code lost:
        if (r2 != 10020002) goto L_0x00fb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00cf, code lost:
        if ("https://api.jr.mi.com/activity/security/?from=insads_security_property_entry&partnerId=128".equals(r6) == false) goto L_0x00fb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00d1, code lost:
        r3 = new com.miui.common.card.functions.CommonFunction(android.content.Intent.parseUri(r6 + "&encryptImei=" + getReplaceImei(), 0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00f3, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00f4, code lost:
        android.util.Log.e(TAG, "parseData error:", r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0012, code lost:
        r4 = null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.miui.common.card.models.FunctionCardModel parse(int r2, org.json.JSONObject r3, com.miui.common.card.models.TitleCardModel r4, com.miui.common.card.models.FuncTopBannerScrollCnModel r5, com.miui.common.card.models.FuncTopBannerScrollGlobalModel r6, b.b.j.b.b r7) {
        /*
            r0 = 0
            r1 = 1
            switch(r2) {
                case 4: goto L_0x003e;
                case 5: goto L_0x0038;
                case 6: goto L_0x0032;
                case 7: goto L_0x002c;
                case 8: goto L_0x0026;
                case 9: goto L_0x001d;
                case 10: goto L_0x0014;
                default: goto L_0x0005;
            }
        L_0x0005:
            switch(r2) {
                case 1401: goto L_0x000f;
                case 1402: goto L_0x000f;
                case 1403: goto L_0x000f;
                case 1404: goto L_0x000f;
                case 1405: goto L_0x0009;
                default: goto L_0x0008;
            }
        L_0x0008:
            goto L_0x0012
        L_0x0009:
            if (r7 == 0) goto L_0x0012
            handleFuncTopBannerScrollDataList(r3, r7)
            goto L_0x0012
        L_0x000f:
            handleFunctionGrid(r2, r3, r4)
        L_0x0012:
            r4 = r0
            goto L_0x0046
        L_0x0014:
            if (r6 == 0) goto L_0x0012
            r6.setHomePageFunc(r1)
            handleFuncTopBannerScrollDataList(r3, r6)
            goto L_0x0012
        L_0x001d:
            if (r5 == 0) goto L_0x0012
            r5.setHomePageFunc(r1)
            handleFuncTopBannerScrollDataList(r3, r5)
            goto L_0x0012
        L_0x0026:
            com.miui.common.card.models.FuncTopBannerNew2CardModel r4 = new com.miui.common.card.models.FuncTopBannerNew2CardModel
            r4.<init>()
            goto L_0x0043
        L_0x002c:
            com.miui.common.card.models.FuncTopBannerNewCardModel r4 = new com.miui.common.card.models.FuncTopBannerNewCardModel
            r4.<init>()
            goto L_0x0043
        L_0x0032:
            com.miui.common.card.models.FuncTopBannerCardModel r4 = new com.miui.common.card.models.FuncTopBannerCardModel
            r4.<init>()
            goto L_0x0043
        L_0x0038:
            com.miui.common.card.models.FuncListBannerCardModel r4 = new com.miui.common.card.models.FuncListBannerCardModel
            r4.<init>()
            goto L_0x0043
        L_0x003e:
            com.miui.common.card.models.FuncLeftBannerCardModel r4 = new com.miui.common.card.models.FuncLeftBannerCardModel
            r4.<init>()
        L_0x0043:
            r4.setHomePageFunc(r1)
        L_0x0046:
            r5 = 0
            if (r4 == 0) goto L_0x00fb
            if (r3 == 0) goto L_0x00fb
            r4.setTemplate(r2)
            java.lang.String r2 = "title"
            java.lang.String r2 = r3.optString(r2)
            r4.setTitle(r2)
            java.lang.String r2 = "summary"
            java.lang.String r2 = r3.optString(r2)
            r4.setSummary(r2)
            java.lang.String r2 = "button"
            java.lang.String r2 = r3.optString(r2)
            r4.setButton(r2)
            java.lang.String r2 = "icon"
            java.lang.String r2 = r3.optString(r2)
            r4.setIcon(r2)
            java.lang.String r2 = "imgUrl"
            java.lang.String r2 = r3.optString(r2)
            r4.setImgUrl(r2)
            java.lang.String r2 = "statKey"
            java.lang.String r2 = r3.optString(r2)
            r4.setStatKey(r2)
            java.lang.String r2 = "ABtest"
            java.lang.String r2 = r3.optString(r2)
            r4.setABtest(r2)
            java.lang.String r2 = "functionId"
            int r2 = r3.optInt(r2)     // Catch:{ Exception -> 0x00f3 }
            java.lang.String r6 = "action"
            java.lang.String r6 = r3.optString(r6)     // Catch:{ Exception -> 0x00f3 }
            java.lang.String r6 = handleDuplicateAction(r6)     // Catch:{ Exception -> 0x00f3 }
            r7 = 10020001(0x98e4a1, float:1.4041012E-38)
            if (r2 != r7) goto L_0x00c4
            android.content.Intent r2 = android.content.Intent.parseUri(r6, r5)     // Catch:{ Exception -> 0x00f3 }
            boolean r7 = com.miui.securityscan.cards.d.a((android.content.Intent) r2)     // Catch:{ Exception -> 0x00f3 }
            if (r7 == 0) goto L_0x00c1
            boolean r6 = isShowLocalFunction(r6)     // Catch:{ Exception -> 0x00f3 }
            if (r6 == 0) goto L_0x00c1
            boolean r3 = isCloudShowFunction(r3)     // Catch:{ Exception -> 0x00f3 }
            if (r3 == 0) goto L_0x00c1
            com.miui.common.card.functions.CommonFunction r3 = new com.miui.common.card.functions.CommonFunction     // Catch:{ Exception -> 0x00f3 }
            r3.<init>(r2)     // Catch:{ Exception -> 0x00f3 }
        L_0x00bd:
            r4.setFunction(r3)     // Catch:{ Exception -> 0x00f3 }
            goto L_0x00c2
        L_0x00c1:
            r1 = r5
        L_0x00c2:
            r5 = r1
            goto L_0x00fb
        L_0x00c4:
            r3 = 10020002(0x98e4a2, float:1.4041013E-38)
            if (r2 != r3) goto L_0x00fb
            java.lang.String r2 = "https://api.jr.mi.com/activity/security/?from=insads_security_property_entry&partnerId=128"
            boolean r2 = r2.equals(r6)     // Catch:{ Exception -> 0x00f3 }
            if (r2 == 0) goto L_0x00fb
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00f3 }
            r2.<init>()     // Catch:{ Exception -> 0x00f3 }
            r2.append(r6)     // Catch:{ Exception -> 0x00f3 }
            java.lang.String r3 = "&encryptImei="
            r2.append(r3)     // Catch:{ Exception -> 0x00f3 }
            java.lang.String r3 = getReplaceImei()     // Catch:{ Exception -> 0x00f3 }
            r2.append(r3)     // Catch:{ Exception -> 0x00f3 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00f3 }
            android.content.Intent r2 = android.content.Intent.parseUri(r2, r5)     // Catch:{ Exception -> 0x00f3 }
            com.miui.common.card.functions.CommonFunction r3 = new com.miui.common.card.functions.CommonFunction     // Catch:{ Exception -> 0x00f3 }
            r3.<init>(r2)     // Catch:{ Exception -> 0x00f3 }
            goto L_0x00bd
        L_0x00f3:
            r2 = move-exception
            java.lang.String r3 = "FunctionCardModel"
            java.lang.String r6 = "parseData error:"
            android.util.Log.e(r3, r6, r2)
        L_0x00fb:
            if (r5 == 0) goto L_0x00fe
            goto L_0x00ff
        L_0x00fe:
            r4 = r0
        L_0x00ff:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.common.card.models.FunctionCardModel.parse(int, org.json.JSONObject, com.miui.common.card.models.TitleCardModel, com.miui.common.card.models.FuncTopBannerScrollCnModel, com.miui.common.card.models.FuncTopBannerScrollGlobalModel, b.b.j.b.b):com.miui.common.card.models.FunctionCardModel");
    }

    private static GridFunctionData parseGridFunctionData(JSONObject jSONObject, String str) {
        String optString = jSONObject.optString("iconId");
        GridFunctionData gridFunctionData = new GridFunctionData();
        gridFunctionData.setTitle(jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME));
        gridFunctionData.setSummary(jSONObject.optString("summary"));
        gridFunctionData.setFunctionId(jSONObject.optString("functionId"));
        gridFunctionData.setIconId(optString);
        gridFunctionData.setIcon(jSONObject.optString(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON));
        gridFunctionData.setTemplate(jSONObject.optInt("template"));
        gridFunctionData.setDataId(jSONObject.optString("dataId"));
        gridFunctionData.setStatKey(jSONObject.optString("statKey"));
        gridFunctionData.setABtest(jSONObject.optString("ABtest"));
        gridFunctionData.setAction(str);
        if (!TextUtils.isEmpty(optString) && LOCAL_FUNCTION_ICONS.get(optString) != null) {
            gridFunctionData.setIconResourceId(LOCAL_FUNCTION_ICONS.get(optString).intValue());
        }
        return gridFunctionData;
    }

    public BaseViewHolder createViewHolder(View view) {
        return new FunctionViewHolder(view);
    }

    public String getABtest() {
        return this.ABtest;
    }

    public AbsModel getCurModel() {
        return this.curModel;
    }

    public List<FuncTopBannerScrollData> getFuncTopBannerScrollDataList() {
        return this.funcTopBannerScrollDataList;
    }

    public BaseFunction getFunction() {
        return this.function;
    }

    public int getFunctionId() {
        return this.functionId;
    }

    public List<GridFunctionData> getGridFunctionDataList() {
        return this.gridFunctionDataList;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public int getScore() {
        return this.score;
    }

    public String getStatKey() {
        return this.statKey;
    }

    public int getTemplate() {
        return this.template;
    }

    public boolean isHomePageFunc() {
        return this.isHomePageFunc;
    }

    public boolean isNeedRemove() {
        return this.needRemove;
    }

    public boolean isNoDivider() {
        return this.isNoDivider;
    }

    public void setABtest(String str) {
        this.ABtest = str;
    }

    public void setFuncTopBannerScrollDataList(List<FuncTopBannerScrollData> list) {
        this.funcTopBannerScrollDataList = list;
    }

    public void setFunction(BaseFunction baseFunction) {
        this.function = baseFunction;
    }

    public void setFunctionId(int i) {
        this.functionId = i;
    }

    public void setGridFunctionDataList(List<GridFunctionData> list) {
        this.gridFunctionDataList = list;
    }

    public void setHomePageFunc(boolean z) {
        this.isHomePageFunc = z;
    }

    public void setImgUrl(String str) {
        this.imgUrl = str;
    }

    public void setNeedRemove(boolean z) {
        this.needRemove = z;
    }

    public void setNoDivider(boolean z) {
        this.isNoDivider = z;
    }

    public void setScore(int i) {
        this.score = i;
    }

    public void setStatKey(String str) {
        this.statKey = str;
    }

    public void setTemplate(int i) {
        this.template = i;
    }

    public boolean validate() {
        return true;
    }
}
