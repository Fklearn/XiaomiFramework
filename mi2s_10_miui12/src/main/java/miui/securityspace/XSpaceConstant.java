package miui.securityspace;

import android.os.Build.VERSION;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import miui.os.Build;

public class XSpaceConstant
{
  public static final ArrayList<String> GMS_RELATED_APPS;
  public static final ArrayList<String> REQUIRED_APPS = new ArrayList();
  public static final Map<String, ArrayList<String>> SPECIAL_APPS = new HashMap();
  public static final ArrayList<String> XSPACE_DEFAULT_BLACK_LIST;
  public static final ArrayList<String> XSPACE_WHITELIST;
  
  static
  {
    GMS_RELATED_APPS = new ArrayList();
    XSPACE_WHITELIST = new ArrayList();
    XSPACE_DEFAULT_BLACK_LIST = new ArrayList();
    XSPACE_DEFAULT_BLACK_LIST.add("com.carrot.iceworld");
    XSPACE_DEFAULT_BLACK_LIST.add("com.szgd.UltraManRunawayWar.mi");
    XSPACE_DEFAULT_BLACK_LIST.add("com.kiloo.subwaysurf");
    XSPACE_DEFAULT_BLACK_LIST.add("com.duowan.groundhog.mctools");
    XSPACE_DEFAULT_BLACK_LIST.add("com.DBGame.DiabloLOL.mi");
    XSPACE_DEFAULT_BLACK_LIST.add("com.pingan.pinganwifi");
    XSPACE_DEFAULT_BLACK_LIST.add("com.sitech.ac");
    XSPACE_DEFAULT_BLACK_LIST.add("cmb.pb");
    XSPACE_DEFAULT_BLACK_LIST.add("com.cib.qdzg");
    XSPACE_DEFAULT_BLACK_LIST.add("com.tencent.tmgp.rxcq");
    XSPACE_DEFAULT_BLACK_LIST.add("com.sfpay.mobile");
    XSPACE_DEFAULT_BLACK_LIST.add("com.tongbanjie.android");
    XSPACE_DEFAULT_BLACK_LIST.add("com.webank.wemoney");
    XSPACE_DEFAULT_BLACK_LIST.add("com.lcwx.xm");
    XSPACE_DEFAULT_BLACK_LIST.add("com.cmbchina.ccd.pluto.cmbActivity");
    XSPACE_DEFAULT_BLACK_LIST.add("com.tencent.tmgp.carrot3");
    XSPACE_DEFAULT_BLACK_LIST.add("org.cocos2d.fishingjoy3.mi");
    XSPACE_DEFAULT_BLACK_LIST.add("com.yodo1.skisafari2.mi");
    XSPACE_DEFAULT_BLACK_LIST.add("com.tencent.game.rhythmmaster");
    XSPACE_DEFAULT_BLACK_LIST.add("com.tencent.wequiz");
    XSPACE_DEFAULT_BLACK_LIST.add("com.tencent.gamehelper.nz");
    XSPACE_DEFAULT_BLACK_LIST.add("com.soulgame.bubble");
    XSPACE_DEFAULT_BLACK_LIST.add("com.tencent.tmgp.qjnn");
    XSPACE_DEFAULT_BLACK_LIST.add("com.tencent.WeFire");
    XSPACE_DEFAULT_BLACK_LIST.add("com.tencent.Mtown");
    XSPACE_DEFAULT_BLACK_LIST.add("com.imangi.templerun2");
    XSPACE_DEFAULT_BLACK_LIST.add("com.halfbrick.fruitninja");
    XSPACE_DEFAULT_BLACK_LIST.add("com.tencent.map");
    XSPACE_DEFAULT_BLACK_LIST.add("com.tencent.peng");
    XSPACE_DEFAULT_BLACK_LIST.add("com.tencent.game.SSGame");
    XSPACE_DEFAULT_BLACK_LIST.add("com.tencent.Alice");
    XSPACE_DEFAULT_BLACK_LIST.add("com.tencent.tmgp.RunGame");
    XSPACE_DEFAULT_BLACK_LIST.add("com.ttx5.mi");
    XSPACE_DEFAULT_BLACK_LIST.add("com.brianbaek.popstar.mi");
    XSPACE_DEFAULT_BLACK_LIST.add("com.google.android.apps.inputmethod.hindi");
    XSPACE_DEFAULT_BLACK_LIST.add("com.google.android.inputmethod.pinyin");
    XSPACE_DEFAULT_BLACK_LIST.add("com.android.browser");
    XSPACE_DEFAULT_BLACK_LIST.add("com.mi.globalbrowser");
    XSPACE_DEFAULT_BLACK_LIST.add("com.mi.global.shop");
    XSPACE_DEFAULT_BLACK_LIST.add("com.duokan.phone.remotecontroller");
    XSPACE_DEFAULT_BLACK_LIST.add("com.miui.player");
    XSPACE_DEFAULT_BLACK_LIST.add("cn.wps.moffice_eng");
    XSPACE_DEFAULT_BLACK_LIST.add("com.google.android.apps.inputmethod.zhuyin");
    XSPACE_DEFAULT_BLACK_LIST.add("com.touchtype.swiftkey");
    XSPACE_DEFAULT_BLACK_LIST.add("com.cleanmaster.mguard_cn");
    XSPACE_DEFAULT_BLACK_LIST.add("com.mi.misupport");
    XSPACE_DEFAULT_BLACK_LIST.add("com.google.android.apps.tachyon");
    XSPACE_DEFAULT_BLACK_LIST.add("com.xiaomi.scanner");
    XSPACE_DEFAULT_BLACK_LIST.add("com.cmcm.indianews_for_oem");
    XSPACE_DEFAULT_BLACK_LIST.add("com.xiaomi.gamecenter");
    XSPACE_DEFAULT_BLACK_LIST.add("com.miui.notes");
    XSPACE_DEFAULT_BLACK_LIST.add("com.miui.scanner");
    XSPACE_DEFAULT_BLACK_LIST.add("com.android.email");
    XSPACE_DEFAULT_BLACK_LIST.add("com.miui.compass");
    XSPACE_DEFAULT_BLACK_LIST.add("com.miui.calculator");
    XSPACE_DEFAULT_BLACK_LIST.add("com.xiaomi.gamecenter");
    XSPACE_DEFAULT_BLACK_LIST.add("com.miui.weather2");
    XSPACE_DEFAULT_BLACK_LIST.add("com.xiaomi.pass");
    XSPACE_DEFAULT_BLACK_LIST.add("com.miui.cleanmaster");
    XSPACE_DEFAULT_BLACK_LIST.add("com.miui.screenrecorder");
    XSPACE_DEFAULT_BLACK_LIST.add("com.yidian.xiaomi");
    XSPACE_DEFAULT_BLACK_LIST.add("com.autonavi.minimap");
    XSPACE_DEFAULT_BLACK_LIST.add("com.duokan.reader");
    XSPACE_DEFAULT_BLACK_LIST.add("com.google.android.youtube");
    XSPACE_DEFAULT_BLACK_LIST.add("com.google.android.googlequicksearchbox");
    XSPACE_DEFAULT_BLACK_LIST.add("com.android.vending");
    XSPACE_DEFAULT_BLACK_LIST.add("com.google.android.marvin.talkback");
    XSPACE_DEFAULT_BLACK_LIST.add("com.google.android.apps.inputmethod.hindi");
    XSPACE_DEFAULT_BLACK_LIST.add("com.google.android.music");
    XSPACE_DEFAULT_BLACK_LIST.add("com.google.android.apps.docs");
    XSPACE_DEFAULT_BLACK_LIST.add("com.google.android.apps.maps");
    XSPACE_DEFAULT_BLACK_LIST.add("com.android.chrome");
    XSPACE_DEFAULT_BLACK_LIST.add("com.google.android.videos");
    XSPACE_DEFAULT_BLACK_LIST.add("com.google.android.apps.photos");
    XSPACE_DEFAULT_BLACK_LIST.add("com.google.android.talk");
    XSPACE_DEFAULT_BLACK_LIST.add("com.google.android.gm");
    XSPACE_DEFAULT_BLACK_LIST.add("com.miui.virtualsim");
    REQUIRED_APPS.add("android");
    REQUIRED_APPS.add("com.android.keychain");
    REQUIRED_APPS.add("com.google.android.webview");
    REQUIRED_APPS.add("com.android.webview");
    REQUIRED_APPS.add("com.google.android.packageinstaller");
    REQUIRED_APPS.add("com.xiaomi.gamecenter.sdk.service");
    REQUIRED_APPS.add("com.miui.securitycore");
    REQUIRED_APPS.add("com.miui.analytics");
    REQUIRED_APPS.add("com.miui.contentcatcher");
    REQUIRED_APPS.add("com.miui.rom");
    REQUIRED_APPS.add("com.tencent.soter.soterserver");
    if (Build.IS_INTERNATIONAL_BUILD)
    {
      REQUIRED_APPS.add("com.android.chrome");
      REQUIRED_APPS.add("com.google.android.permissioncontroller");
    }
    else
    {
      REQUIRED_APPS.add("com.android.permissioncontroller");
    }
    ArrayList localArrayList = new ArrayList();
    localArrayList.add("com.android.packageinstaller.permission.ui.GrantPermissionsActivity");
    localArrayList.add("com.android.packageinstaller.permission.ui.ManagePermissionsActivity");
    SPECIAL_APPS.put("com.google.android.packageinstaller", localArrayList);
    localArrayList = new ArrayList();
    localArrayList.add("com.miui.xspace.receiver.MediaScannerReceiver");
    localArrayList.add("com.miui.xspace.receiver.InstallShortcutReceiver");
    SPECIAL_APPS.put("com.miui.securitycore", localArrayList);
    GMS_RELATED_APPS.add("com.google.android.gsf");
    GMS_RELATED_APPS.add("com.google.android.gms");
    if (Build.VERSION.SDK_INT < 26) {
      GMS_RELATED_APPS.add("com.google.android.gsf.login");
    }
    XSPACE_WHITELIST.add("com.whatsapp");
    XSPACE_WHITELIST.add("net.one97.paytm");
    XSPACE_WHITELIST.add("com.facebook.katana");
    XSPACE_WHITELIST.add("com.instagram.android");
    XSPACE_WHITELIST.add("com.facebook.orca");
    XSPACE_WHITELIST.add("com.phonepe.app");
    XSPACE_WHITELIST.add("com.gbwhatsapp");
    XSPACE_WHITELIST.add("com.mobile.legends");
    XSPACE_WHITELIST.add("com.vkontakte.android");
    XSPACE_WHITELIST.add("com.tencent.ig");
    XSPACE_WHITELIST.add("com.lenovo.anyshare.gps");
    XSPACE_WHITELIST.add("com.zhiliaoapp.musically");
    XSPACE_WHITELIST.add("com.nemo.vidmate");
    XSPACE_WHITELIST.add("com.UCMobile.intl");
    XSPACE_WHITELIST.add("com.mxtech.videoplayer.ad");
    XSPACE_WHITELIST.add("com.facebook.lite");
    XSPACE_WHITELIST.add("in.startv.hotstar");
    XSPACE_WHITELIST.add("com.jio.media.jiobeats");
    XSPACE_WHITELIST.add("video.like");
    XSPACE_WHITELIST.add("com.dts.freefireth");
    XSPACE_WHITELIST.add("com.truecaller");
    XSPACE_WHITELIST.add("com.tencent.mm");
    XSPACE_WHITELIST.add("com.tencent.mobileqq");
    XSPACE_WHITELIST.add("com.eg.android.AlipayGphone");
    XSPACE_WHITELIST.add("com.smile.gifmaker");
    XSPACE_WHITELIST.add("com.sina.weibo");
    XSPACE_WHITELIST.add("com.ss.android.ugc.aweme");
    XSPACE_WHITELIST.add("com.tencent.tmgp.sgame");
    XSPACE_WHITELIST.add("com.tencent.karaoke");
    XSPACE_WHITELIST.add("com.immomo.momo");
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securityspace/XSpaceConstant.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */