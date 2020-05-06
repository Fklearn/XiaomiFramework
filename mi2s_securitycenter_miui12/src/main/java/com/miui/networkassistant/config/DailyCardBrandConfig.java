package com.miui.networkassistant.config;

import android.content.Context;
import android.text.TextUtils;
import com.miui.luckymoney.config.AppConstants;
import com.miui.networkassistant.model.DailyCardBrandInfo;
import java.util.ArrayList;
import java.util.List;

public class DailyCardBrandConfig {
    private static DailyCardBrandConfig sInstance;
    private List<DailyCardBrandInfo> mBrandMap = new ArrayList();
    private Context mContext;

    private DailyCardBrandConfig(Context context) {
        this.mContext = context.getApplicationContext();
        initData();
    }

    private DailyCardBrandInfo buildDefaultConfig() {
        DailyCardBrandInfo dailyCardBrandInfo = new DailyCardBrandInfo();
        dailyCardBrandInfo.brandName = "自定义";
        dailyCardBrandInfo.dailyPackage = 0;
        dailyCardBrandInfo.monthPackage = 0;
        dailyCardBrandInfo.ignoreApps = new ArrayList();
        return dailyCardBrandInfo;
    }

    private DailyCardBrandInfo buildMiDailyCardConfig() {
        DailyCardBrandInfo dailyCardBrandInfo = new DailyCardBrandInfo();
        dailyCardBrandInfo.brandName = "米粉卡";
        dailyCardBrandInfo.dailyPackage = 1073741824;
        dailyCardBrandInfo.monthPackage = 0;
        ArrayList arrayList = new ArrayList();
        arrayList.add("cn.cntv");
        dailyCardBrandInfo.ignoreApps = arrayList;
        return dailyCardBrandInfo;
    }

    private DailyCardBrandInfo buildTencentDailyCardConfig() {
        DailyCardBrandInfo dailyCardBrandInfo = new DailyCardBrandInfo();
        dailyCardBrandInfo.brandName = "腾讯王卡";
        dailyCardBrandInfo.dailyPackage = 838860800;
        dailyCardBrandInfo.monthPackage = 0;
        ArrayList arrayList = new ArrayList();
        arrayList.add(AppConstants.Package.PACKAGE_NAME_MM);
        arrayList.add(AppConstants.Package.PACKAGE_NAME_QQ);
        arrayList.add("com.tencent.qqlite");
        arrayList.add("com.tencent.tim");
        arrayList.add("com.tencent.qqlive");
        arrayList.add("com.tencent.qqmusic");
        arrayList.add("com.tencent.android.qqdownloader");
        arrayList.add("com.tencent.qqpimsecure");
        arrayList.add("com.tencent.mtt");
        arrayList.add("com.qzone");
        arrayList.add("com.tencent.androidqqmail");
        arrayList.add("com.tencent.news");
        arrayList.add("com.qq.reader");
        arrayList.add("com.tencent.weishi");
        arrayList.add("com.tencent.zebra");
        arrayList.add("com.qq.ac.android");
        arrayList.add("com.tencent.qqpicshow");
        arrayList.add("com.tencent.loverzone");
        arrayList.add("com.tencent.qqsports");
        arrayList.add("com.tencent.qqhouse");
        arrayList.add("com.tencent.qqcalendar");
        arrayList.add("com.qq.lottery51buy");
        arrayList.add("com.tencent.meishi");
        arrayList.add("com.tencent.radio");
        arrayList.add("com.tencent.portfolio");
        arrayList.add("com.tencent.qlauncher.lite");
        arrayList.add("com.tencent.qlauncher");
        arrayList.add("com.tencent.reading");
        arrayList.add("com.tencent.map");
        arrayList.add("com.tencent.pb");
        arrayList.add("com.tencent.gamejoy");
        arrayList.add("com.tencent.powermanager");
        arrayList.add("com.tencent.mobileqqi");
        arrayList.add("com.qq.qcloud");
        arrayList.add("com.tencent.gallerymanager");
        arrayList.add("com.tencent.research.drop");
        arrayList.add("com.tencent.QQLottery");
        arrayList.add("com.tencent.now");
        arrayList.add("com.tencent.launcher");
        arrayList.add("com.tencent.unipay");
        arrayList.add("com.tencent.navsns");
        arrayList.add("com.tencent.karaoke");
        arrayList.add("com.tencent.k12");
        arrayList.add("com.tencent.edu");
        arrayList.add("com.tencent.qqlivebroadcast");
        arrayList.add("com.tencent.qqlivekid");
        arrayList.add("com.tencent.igame");
        arrayList.add("com.tencent.qgame");
        arrayList.add("com.tencent.qqpim");
        arrayList.add("com.tencent.wifimanager");
        arrayList.add("com.tencent.wework");
        arrayList.add("com.tencent.weread");
        arrayList.add("com.tencent.tmgp.sgame");
        arrayList.add("com.tencent.tmgp.pubgm");
        arrayList.add("com.tencent.tmgp.pubgmhd");
        arrayList.add("com.tencent.tmgp.cf");
        arrayList.add("com.tencent.tmgp.speedmobile");
        arrayList.add("com.tencent.tmgp.wec");
        arrayList.add("com.tencent.cldts");
        arrayList.add("com.qqgame.hlddz");
        arrayList.add("com.tencent.tmgp.wesix");
        arrayList.add("com.tencent.tmgp.tmsk.qj2");
        arrayList.add("com.tencent.KiHan");
        arrayList.add("com.tencent.pao");
        arrayList.add("com.tencent.qqgame");
        arrayList.add("com.tencent.shootgame");
        arrayList.add("com.tencent.qqxl");
        arrayList.add("com.tencent.qt.qtl");
        arrayList.add("com.qqgame.mic");
        arrayList.add("com.tencent.tmgp.ylm");
        arrayList.add("com.tencent.tmgp.qjnn");
        arrayList.add("com.tencent.tmgp.rxcq");
        arrayList.add("com.tencent.game.rhythmmaster");
        arrayList.add("com.qqgame.happymj");
        arrayList.add("com.tencent.peng");
        arrayList.add("com.tencent.feiji");
        arrayList.add("air.com.tencent.qqpasture");
        arrayList.add("com.tencent.Qfarm");
        arrayList.add("com.tencent.Q108");
        arrayList.add("com.tencent.tmgp.gods");
        arrayList.add("com.tencent.hexkog");
        arrayList.add("com.tencent.qqgame.qqTexaswvga");
        arrayList.add("com.tencent.qqgame.xq");
        arrayList.add("com.tencent.lian");
        arrayList.add("com.tencent.tmgp.vdefense");
        arrayList.add("com.tencent.game.VXDGame");
        arrayList.add("com.tencent.modoomarble");
        arrayList.add("com.tencent.Alice");
        arrayList.add("com.tencent.west");
        arrayList.add("com.tencent.tmgp.vlong");
        arrayList.add("com.tencent.tmgp.jjdzs");
        arrayList.add("com.tencent.clover");
        arrayList.add("com.tencent.tmgp.mxm");
        arrayList.add("com.tencent.tmgp.kof98");
        arrayList.add("com.kingnet.ssl");
        arrayList.add("com.tencent.king.candycrushsaga");
        arrayList.add("com.tencent.tmgp.yxwdzzjy");
        arrayList.add("com.tencent.kof");
        arrayList.add("com.tencent.tmgp.qqx5");
        arrayList.add("com.smile.gifmaker");
        arrayList.add("com.duowan.kiwi");
        arrayList.add("air.tv.douyu.android");
        arrayList.add("com.panda.videoliveplatform");
        arrayList.add("com.huomaotv.mobile");
        arrayList.add("com.gameabc.zhanqiAndroid");
        arrayList.add("com.longzhu.tga");
        arrayList.add("tv.yixia.bobo");
        arrayList.add("com.mogujie");
        arrayList.add("com.kugou.android");
        arrayList.add("com.wuba");
        arrayList.add("com.sankuai.meituan");
        arrayList.add("com.anjuke.android.app");
        arrayList.add("com.sina.weibo");
        arrayList.add("cn.cntv");
        arrayList.add("com.tencent.gwgo");
        arrayList.add("com.zhihu.android");
        dailyCardBrandInfo.ignoreApps = arrayList;
        return dailyCardBrandInfo;
    }

    private DailyCardBrandInfo buildTianShenDailyCardConfig() {
        DailyCardBrandInfo dailyCardBrandInfo = new DailyCardBrandInfo();
        dailyCardBrandInfo.brandName = "小天神卡";
        dailyCardBrandInfo.dailyPackage = 838860800;
        dailyCardBrandInfo.monthPackage = 0;
        ArrayList arrayList = new ArrayList();
        arrayList.add("cn.cntv");
        dailyCardBrandInfo.ignoreApps = arrayList;
        return dailyCardBrandInfo;
    }

    public static synchronized DailyCardBrandConfig getInstance(Context context) {
        DailyCardBrandConfig dailyCardBrandConfig;
        synchronized (DailyCardBrandConfig.class) {
            if (sInstance == null) {
                sInstance = new DailyCardBrandConfig(context);
            }
            dailyCardBrandConfig = sInstance;
        }
        return dailyCardBrandConfig;
    }

    private void initData() {
        this.mBrandMap.add(buildDefaultConfig());
        this.mBrandMap.add(buildMiDailyCardConfig());
        this.mBrandMap.add(buildTianShenDailyCardConfig());
        this.mBrandMap.add(buildTencentDailyCardConfig());
    }

    public DailyCardBrandInfo getBrandInfo(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        for (DailyCardBrandInfo next : this.mBrandMap) {
            if (next.brandName.equals(str)) {
                return next;
            }
        }
        return null;
    }

    public List<DailyCardBrandInfo> getBrandList() {
        return this.mBrandMap;
    }

    public List<String> getBrandNameList() {
        ArrayList arrayList = new ArrayList();
        for (DailyCardBrandInfo dailyCardBrandInfo : this.mBrandMap) {
            arrayList.add(dailyCardBrandInfo.brandName);
        }
        return arrayList;
    }
}
