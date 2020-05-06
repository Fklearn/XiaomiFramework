package com.android.server.pm;

import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageParser;
import android.os.Build;
import android.os.FileUtils;
import android.os.RemoteException;
import android.os.SELinux;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Slog;
import com.android.server.pm.PackageManagerService;
import com.android.server.slice.SliceClientPermissions;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import libcore.io.IoUtils;
import miui.os.CustVerifier;
import miui.util.CustomizeUtil;
import miui.util.FeatureParser;
import miui.util.PreinstallAppUtils;
import miui.util.ReflectionUtils;

public class PreinstallApp {
    private static final File CUSTOMIZED_APP_DIR = CustomizeUtil.getMiuiCustomizedAppDir();
    private static final boolean DEBUG = true;
    private static final String ENCRYPTING_STATE = "trigger_restart_min_framework";
    private static final String INSTALL_DIR = "/data/app/";
    private static final int MIUI_APP_PATH_L_LENGTH = 7;
    private static final File NONCUSTOMIZED_APP_DIR = CustomizeUtil.getMiuiNoCustomizedAppDir();
    private static final Set<String> NOT_OTA_PACKAGE_NAMES = new HashSet();
    private static final File OLD_PREINSTALL_APP_DIR = new File("/data/miui/apps");
    private static final String OLD_PREINSTALL_HISTORY_FILE = "/data/system/preinstall_history";
    private static final String OLD_PREINSTALL_PACKAGE_PAI_TRACKING_FILE = "/cust/etc/pre_install.appsflyer";
    private static final File OTA_SKIP_BUSINESS_APP_LIST_FILE = new File("/system/etc/ota_skip_apps");
    private static final String PREINSTALL_HISTORY_FILE = "/data/app/preinstall_history";
    private static final String PREINSTALL_PACKAGE_LIST = "/data/system/preinstall.list";
    private static final String PREINSTALL_PACKAGE_MIUI_TRACKING_DIR = "/data/miui/pai/";
    private static final String PREINSTALL_PACKAGE_MIUI_TRACKING_DIR_CONTEXT = "u:object_r:miui_pai_file:s0";
    private static final String PREINSTALL_PACKAGE_PAI_LIST = "/data/system/preinstallPAI.list";
    private static final String PREINSTALL_PACKAGE_PAI_TRACKING_FILE = "/data/miui/pai/pre_install.appsflyer";
    private static final File PRODUCT_NONCUSTOMIZED_APP_DIR = CustomizeUtil.getMiuiProductNoCustomizedAppDir();
    private static final File RECOMMENDED_APP_DIR = new File("/data/miui/app/recommended");
    private static final String TAG = PreinstallApp.class.getSimpleName();
    private static final String TYPE_TRACKING_APPSFLYER = "appsflyer";
    private static final String TYPE_TRACKING_MIUI = "xiaomi";
    private static final File VENDOR_NONCUSTOMIZED_APP_DIR = CustomizeUtil.getMiuiVendorNoCustomizedAppDir();
    private static List<String> mNewTrackContentList = new ArrayList();
    private static List<String> mPackagePAIList = new ArrayList();
    private static Map<String, Integer> mPackageVersionMap = new HashMap();
    private static List<String> mTraditionalTrackContentList = new ArrayList();
    public static Map<String, String> sAdvanceApps = new HashMap();
    private static final Set<String> sIgnorePreinstallApks = new HashSet();
    private static final Set<Item> sNewUpdatedSystemPreinstallApps = new HashSet();
    private static final Map<String, Item> sPreinstallApps = new HashMap();

    private static class Item {
        static final int TYPE_CLUSTER = 2;
        static final int TYPE_MONOLITHIC = 1;
        File apkFile;
        File app;
        String packageName;
        PackageParser.Package pkg;
        PackageParser.PackageLite pkgLite;
        int type;

        Item(String packageName2, File file, PackageParser.PackageLite pkgLite2, PackageParser.Package pkg2) {
            this.packageName = packageName2;
            this.app = file;
            this.apkFile = PreinstallApp.getApkFile(file);
            this.pkgLite = pkgLite2;
            this.pkg = pkg2;
            this.type = file.isDirectory() ? 2 : 1;
        }

        static boolean betterThan(Item newItem, Item oldItem) {
            if (Build.VERSION.SDK_INT >= 21) {
                int i = newItem.type;
                int i2 = oldItem.type;
                if (i > i2) {
                    return true;
                }
                if (i != i2 || newItem.pkgLite.versionCode <= oldItem.pkgLite.versionCode) {
                    return false;
                }
                return true;
            } else if (newItem.pkgLite.versionCode > oldItem.pkgLite.versionCode) {
                return true;
            }
            return false;
        }

        public String toString() {
            return "" + this.packageName + "[" + this.apkFile.getPath() + "," + this.pkgLite.versionCode + "]";
        }
    }

    static {
        String JDversion = SystemProperties.get("ro.boot.hwversion");
        if (!miui.os.Build.IS_MI2A && !miui.os.Build.IS_MITHREE) {
            ignorePreinstallApks("ota-miui-MiTagApp.apk");
        }
        if (!FeatureParser.getBoolean("support_ir", false)) {
            ignorePreinstallApks("partner-XMRemoteController.apk");
        }
        if (!JDversion.equals("3.9.3") && !JDversion.equals("3.9.5")) {
            ignorePreinstallApks("chinatelecom-picasso-com.jd.jrapp.apk");
            ignorePreinstallApks("chinatelecom-picasso-com.jingdong.app.mall.apk");
            ignorePreinstallApks("chinatelecom-picasso-com.jd.app.reader.apk");
        }
    }

    static void ignorePreinstallApks(String fileName) {
        for (File dir : new File[]{NONCUSTOMIZED_APP_DIR, VENDOR_NONCUSTOMIZED_APP_DIR, PRODUCT_NONCUSTOMIZED_APP_DIR, CUSTOMIZED_APP_DIR, RECOMMENDED_APP_DIR}) {
            File apkFile = new File(dir.getPath() + SliceClientPermissions.SliceAuthority.DELIMITER + fileName);
            if (apkFile.exists()) {
                sIgnorePreinstallApks.add(apkFile.getPath());
            }
            File apkFile2 = new File(dir.getPath() + SliceClientPermissions.SliceAuthority.DELIMITER + fileName.replace(".apk", "") + SliceClientPermissions.SliceAuthority.DELIMITER + fileName);
            if (apkFile2.exists()) {
                sIgnorePreinstallApks.add(apkFile2.getPath());
            }
        }
    }

    private static IPackageManager getPackageManager() {
        return IPackageManager.Stub.asInterface(ServiceManager.getService(Settings.ATTR_PACKAGE));
    }

    private static void readHistory(String filePath, Map<String, Long> preinstallHistoryMap) {
        String[] paths;
        Map<String, Long> map = preinstallHistoryMap;
        BufferedReader bufferReader = null;
        FileReader fileReader = null;
        try {
            try {
                File installHistoryFile = new File(filePath);
                if (!installHistoryFile.exists()) {
                    IoUtils.closeQuietly((AutoCloseable) null);
                    IoUtils.closeQuietly((AutoCloseable) null);
                    return;
                }
                fileReader = new FileReader(installHistoryFile);
                bufferReader = new BufferedReader(fileReader);
                while (true) {
                    String readLine = bufferReader.readLine();
                    String line = readLine;
                    if (readLine == null) {
                        break;
                    }
                    String[] ss = line.split(":");
                    if (ss != null && ss.length == 2) {
                        try {
                            long mtime = Long.valueOf(ss[1]).longValue();
                            if (ss[0].startsWith(OLD_PREINSTALL_APP_DIR.getPath())) {
                                map.put(ss[0].replace(OLD_PREINSTALL_APP_DIR.getPath(), CUSTOMIZED_APP_DIR.getPath()), Long.valueOf(mtime));
                                map.put(ss[0].replace(OLD_PREINSTALL_APP_DIR.getPath(), NONCUSTOMIZED_APP_DIR.getPath()), Long.valueOf(mtime));
                                map.put(ss[0].replace(OLD_PREINSTALL_APP_DIR.getPath(), VENDOR_NONCUSTOMIZED_APP_DIR.getPath()), Long.valueOf(mtime));
                                String possibleNewPath = ss[0].replace(OLD_PREINSTALL_APP_DIR.getPath(), PRODUCT_NONCUSTOMIZED_APP_DIR.getPath());
                                map.put(possibleNewPath, Long.valueOf(mtime));
                                String str = possibleNewPath;
                            } else {
                                if (!(Build.VERSION.SDK_INT < 21 || (paths = ss[0].split(SliceClientPermissions.SliceAuthority.DELIMITER)) == null || paths.length == 7)) {
                                    String fileName = paths[paths.length - 1];
                                    String apkName = fileName.replace(".apk", "");
                                    String str2 = ss[0];
                                    String possibleNewPath2 = str2.replace(fileName, apkName + SliceClientPermissions.SliceAuthority.DELIMITER + fileName);
                                    String str3 = fileName;
                                    File oldAppFile = new File(ss[0]);
                                    File newAppFile = new File(possibleNewPath2);
                                    if (newAppFile.exists() && isSamePackage(oldAppFile, newAppFile)) {
                                        ss[0] = possibleNewPath2;
                                    }
                                }
                                map.put(ss[0], Long.valueOf(mtime));
                            }
                            String str4 = line;
                        } catch (NumberFormatException e) {
                        }
                    }
                    String str5 = line;
                }
                IoUtils.closeQuietly(bufferReader);
                IoUtils.closeQuietly(fileReader);
            } catch (IOException e2) {
            } catch (Throwable th) {
                th = th;
                IoUtils.closeQuietly(bufferReader);
                IoUtils.closeQuietly(fileReader);
                throw th;
            }
        } catch (IOException e3) {
            String str6 = filePath;
        } catch (Throwable th2) {
            th = th2;
            String str7 = filePath;
            IoUtils.closeQuietly(bufferReader);
            IoUtils.closeQuietly(fileReader);
            throw th;
        }
    }

    private static void readHistory(Map<String, Long> preinstallHistoryMap) {
        readHistory(OLD_PREINSTALL_HISTORY_FILE, preinstallHistoryMap);
        readHistory(PREINSTALL_HISTORY_FILE, preinstallHistoryMap);
    }

    private static void writeHistory(String filePath, Map<String, Long> preinstallHistoryMap) {
        FileWriter fileWriter = null;
        BufferedWriter bufferWriter = null;
        try {
            File installHistoryFile = new File(filePath);
            if (!installHistoryFile.exists()) {
                installHistoryFile.createNewFile();
            }
            fileWriter = new FileWriter(installHistoryFile, false);
            bufferWriter = new BufferedWriter(fileWriter);
            for (Map.Entry<String, Long> r : preinstallHistoryMap.entrySet()) {
                if (new File(r.getKey()).exists()) {
                    bufferWriter.write(r.getKey() + ":" + r.getValue());
                    bufferWriter.write("\n");
                }
            }
        } catch (IOException e) {
        } catch (Throwable th) {
            IoUtils.closeQuietly((AutoCloseable) null);
            IoUtils.closeQuietly((AutoCloseable) null);
            throw th;
        }
        IoUtils.closeQuietly(bufferWriter);
        IoUtils.closeQuietly(fileWriter);
    }

    private static void writeHistory(Map<String, Long> preinstallHistoryMap) {
        File old = new File(OLD_PREINSTALL_HISTORY_FILE);
        if (old.exists()) {
            old.delete();
        }
        writeHistory(PREINSTALL_HISTORY_FILE, preinstallHistoryMap);
    }

    private static void recordToHistory(Map<String, Long> history, File apkFile) {
        history.put(apkFile.getPath(), Long.valueOf(apkFile.lastModified()));
    }

    private static void recordHistory(Map<String, Long> history, Item item) {
        recordToHistory(history, item.apkFile);
    }

    private static boolean existHistory() {
        if (!new File(PREINSTALL_HISTORY_FILE).exists() && !new File(OLD_PREINSTALL_HISTORY_FILE).exists()) {
            return false;
        }
        return true;
    }

    private static boolean deleteContents(File dir) {
        File[] files = dir.listFiles();
        boolean success = true;
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    success &= deleteContents(file);
                }
                if (!file.delete()) {
                    Slog.w(TAG, "Failed to delete " + file);
                    success = false;
                }
            }
        }
        return success;
    }

    private static void deleteFileOrDirectory(File f) {
        if (f.isDirectory()) {
            deleteContents(f);
        }
        f.delete();
    }

    private static void createIfNonexist(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
            dir.setReadable(true, false);
            dir.setExecutable(true, false);
            dir.setWritable(true);
        }
    }

    private static boolean underData(PackageSetting ps) {
        return ps.codePathString.startsWith(INSTALL_DIR);
    }

    /* access modifiers changed from: private */
    public static File getApkFile(File app) {
        if (app.isDirectory()) {
            return getBaseApkFile(app);
        }
        return app;
    }

    private static boolean copyPreinstallForVersionUnder21(PackageManagerService pms, Item srcApp, PackageSetting ps) {
        File dstCodePath;
        boolean ret = false;
        if (srcApp.apkFile.isDirectory()) {
            return false;
        }
        if (ps == null || !underData(ps)) {
            dstCodePath = new File(INSTALL_DIR, srcApp.apkFile.getName());
        } else {
            cleanUpResource(pms, ps);
            dstCodePath = ps.codePath;
        }
        if (dstCodePath.exists()) {
            dstCodePath.delete();
        }
        if (FileUtils.copyFile(srcApp.apkFile, dstCodePath) && dstCodePath.setReadable(true, false)) {
            ret = true;
        }
        return ret;
    }

    private static boolean copyPreinstallAppForVersionEqualOrAbove21(PackageManagerService pms, Item srcApp, PackageSetting ps) {
        if (!srcApp.apkFile.exists()) {
            return false;
        }
        File dstCodePath = null;
        if (ps != null && underData(ps)) {
            cleanUpResource(pms, ps);
            dstCodePath = ps.codePath;
        }
        if (dstCodePath == null) {
            if (srcApp.type == 2) {
                dstCodePath = new File(INSTALL_DIR, srcApp.app.getName());
            } else {
                dstCodePath = new File(INSTALL_DIR, srcApp.apkFile.getName().replace(".apk", ""));
            }
        }
        if (dstCodePath.exists()) {
            deleteFileOrDirectory(dstCodePath);
        }
        createIfNonexist(dstCodePath);
        File dstApkFile = new File(dstCodePath, srcApp.apkFile.getName());
        if (!FileUtils.copyFile(srcApp.apkFile, dstApkFile) || !dstApkFile.setReadable(true, false)) {
            return false;
        }
        return true;
    }

    private static boolean copyNewPreinstallApp(PackageManagerService pms, Item item) {
        return copyPreinstallApp(pms, item, (PackageSetting) null);
    }

    private static boolean copyPreinstallApp(PackageManagerService pms, Item item, PackageSetting ps) {
        if (Build.VERSION.SDK_INT < 21) {
            return copyPreinstallForVersionUnder21(pms, item, ps);
        }
        return copyPreinstallAppForVersionEqualOrAbove21(pms, item, ps);
    }

    private static final boolean isApkFile(File apkFile) {
        return apkFile != null && apkFile.getPath().endsWith(".apk");
    }

    private static void readLineToSet(File file, Set<String> set) {
        if (file.exists()) {
            BufferedReader buffer = null;
            try {
                buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                if (!file.getName().equals("vanward_applist")) {
                    while (true) {
                        String readLine = buffer.readLine();
                        String line = readLine;
                        if (readLine == null) {
                            break;
                        }
                        set.add(line.trim());
                    }
                } else {
                    Locale curLocale = ActivityManagerNative.getDefault().getConfiguration().locale;
                    while (true) {
                        String readLine2 = buffer.readLine();
                        String line2 = readLine2;
                        if (readLine2 == null) {
                            break;
                        }
                        String[] ss = line2.trim().split("\\s+");
                        if (ss.length == 2 && isValidIme(ss[1], curLocale)) {
                            set.add(ss[0]);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            } catch (RemoteException e3) {
                e3.printStackTrace();
            } catch (Throwable th) {
                IoUtils.closeQuietly((AutoCloseable) null);
                throw th;
            }
            IoUtils.closeQuietly(buffer);
        }
    }

    private static File getBaseApkFile(File dir) {
        return new File(dir, dir.getName() + ".apk");
    }

    private static void addPreinstallAppToList(List<File> preinstallAppList, File appDir, Set<String> filterSet) {
        File apk;
        File[] apps = appDir.listFiles();
        if (apps != null) {
            for (File app : apps) {
                if (Build.VERSION.SDK_INT < 21) {
                    apk = app;
                    if (apk.isFile()) {
                        if (!isApkFile(apk)) {
                        }
                    }
                } else if (app.isDirectory()) {
                    apk = getBaseApkFile(app);
                    if (!apk.exists()) {
                    }
                } else {
                    apk = app;
                    if (!isApkFile(apk)) {
                    }
                }
                if (!sIgnorePreinstallApks.contains(apk.getPath()) && (filterSet == null || filterSet.contains(apk.getName()))) {
                    preinstallAppList.add(app);
                }
            }
        }
    }

    private static ArrayList<File> getPreinstallApplist(boolean onlyCust) {
        ArrayList<File> preinstallAppList = new ArrayList<>();
        Set<String> customizedAppSet = new HashSet<>();
        Set<String> recommendedAppSet = new HashSet<>();
        File custVariantDir = CustomizeUtil.getMiuiCustVariantDir();
        if (custVariantDir != null) {
            readLineToSet(new File(custVariantDir, "customized_applist"), customizedAppSet);
            readLineToSet(new File(custVariantDir, "ota_customized_applist"), customizedAppSet);
            readLineToSet(new File(custVariantDir, "recommended_applist"), recommendedAppSet);
            readLineToSet(new File(custVariantDir, "ota_recommended_applist"), recommendedAppSet);
        }
        addPreinstallAppToList(preinstallAppList, CUSTOMIZED_APP_DIR, customizedAppSet);
        addPreinstallAppToList(preinstallAppList, RECOMMENDED_APP_DIR, recommendedAppSet);
        if (!onlyCust) {
            addPreinstallAppToList(preinstallAppList, NONCUSTOMIZED_APP_DIR, (Set<String>) null);
            addPreinstallAppToList(preinstallAppList, VENDOR_NONCUSTOMIZED_APP_DIR, (Set<String>) null);
            addPreinstallAppToList(preinstallAppList, PRODUCT_NONCUSTOMIZED_APP_DIR, (Set<String>) null);
        }
        return preinstallAppList;
    }

    private static List<File> getCustomizePreinstallAppList() {
        return getPreinstallApplist(true);
    }

    private static List<File> getAllPreinstallApplist() {
        return getPreinstallApplist(false);
    }

    private static boolean isSystemApp(PackageSetting ps) {
        return (ps.pkgFlags & 1) != 0;
    }

    private static boolean isUpdatedSystemApp(PackageSetting ps) {
        return ps.codePathString != null && ps.codePathString.startsWith(INSTALL_DIR);
    }

    private static boolean isSystemAndNotUpdatedSystemApp(PackageSetting ps) {
        return ps != null && isSystemApp(ps) && !isUpdatedSystemApp(ps);
    }

    private static boolean systemAppDeletedOrDisabled(PackageManagerService pms, String pkgName) {
        return !pms.mPackages.containsKey(pkgName);
    }

    private static boolean dealed(Map<String, Long> history, File apkFile) {
        return history.containsKey(apkFile.getPath()) && apkFile.lastModified() == history.get(apkFile.getPath()).longValue() && !apkFile.getPath().contains("/system/data-app") && !apkFile.getPath().contains("/vendor/data-app") && !apkFile.getPath().contains("/product/data-app");
    }

    private static boolean dealed(Map<String, Long> history, Item item) {
        return dealed(history, item.apkFile);
    }

    private static boolean recorded(Map<String, Long> history, File apkFile) {
        return history.containsKey(apkFile.getPath());
    }

    private static boolean recorded(Map<String, Long> history, Item item) {
        return recorded(history, item.apkFile);
    }

    private static boolean signCheck(File apkFile) {
        if (Build.IS_DEBUGGABLE) {
            return false;
        }
        boolean custAppSupportSign = PreinstallAppUtils.supportSignVerifyInCust() && apkFile.getPath().contains("/cust/app");
        String str = TAG;
        Slog.i(str, "Sign support is " + custAppSupportSign);
        if (custAppSupportSign && CustVerifier.getInstance() == null) {
            String str2 = TAG;
            Slog.i(str2, "CustVerifier init error !" + apkFile.getPath() + " will not be installed.");
            return true;
        } else if (!custAppSupportSign || CustVerifier.getInstance().verifyApkSignatue(apkFile.getPath(), 0)) {
            return false;
        } else {
            String str3 = TAG;
            Slog.i(str3, apkFile.getPath() + " verify failed!");
            return true;
        }
    }

    private static void parseAndDeleteDuplicatePreinstallApps() {
        List<File> preinstallAppFiles = getAllPreinstallApplist();
        long currentTime = System.currentTimeMillis();
        for (File pa : preinstallAppFiles) {
            PackageParser.PackageLite pl = parsePackageLite(pa);
            if (pl == null || pl.packageName == null) {
                String str = TAG;
                Slog.e(str, "Parse " + pa.getPath() + " failed, skip");
            } else {
                String packageName = pl.packageName;
                Item newItem = new Item(packageName, pa, pl, (PackageParser.Package) null);
                if (!sPreinstallApps.containsKey(packageName)) {
                    sPreinstallApps.put(packageName, newItem);
                } else {
                    Item oldItem = sPreinstallApps.get(packageName);
                    if (Item.betterThan(newItem, oldItem)) {
                        sPreinstallApps.put(packageName, newItem);
                    } else {
                        Item tmp = newItem;
                        newItem = oldItem;
                        oldItem = tmp;
                    }
                    String str2 = TAG;
                    Slog.w(str2, newItem.toString() + " is better than " + oldItem.toString() + ", ignore " + oldItem.toString() + " !!!");
                }
            }
        }
        String str3 = TAG;
        Slog.i(str3, "Parse preinstall apps, consume " + (System.currentTimeMillis() - currentTime) + "ms");
    }

    private static void copyPreinstallAppsForFirstBoot(PackageManagerService pms, Settings settings) {
        long currentTime = System.currentTimeMillis();
        Map<String, Long> history = new HashMap<>();
        if (existHistory()) {
            Slog.w(TAG, "Exist preinstall history, skip copying preinstall apps for first boot!");
            return;
        }
        Slog.i(TAG, "Copy preinstall apps start for first boot");
        Map<String, Integer> pkgMap = new HashMap<>();
        for (Item item : sPreinstallApps.values()) {
            if (signCheck(item.apkFile)) {
                Slog.i(TAG, "Skip copying when the sign is false for first boot.");
            } else {
                PackageSetting ps = settings.getPackageLPr(item.pkgLite.packageName);
                if (isSystemAndNotUpdatedSystemApp(ps)) {
                    String str = TAG;
                    Slog.w(str, "Skip copying new system updated preinstall app " + item.toString() + ", update it after system ready");
                    PackageParser.Package pkg = parsePackage(item.app);
                    if (pkg != null) {
                        item.pkg = pkg;
                        sNewUpdatedSystemPreinstallApps.add(item);
                    } else {
                        String str2 = TAG;
                        Slog.e(str2, "Parse " + item.toString() + " failed, skip");
                    }
                } else if (copyPreinstallApp(pms, item, ps)) {
                    String str3 = TAG;
                    Slog.i(str3, "Copy " + item.toString() + " for first boot");
                    if (item.pkgLite != null && !TextUtils.isEmpty(item.pkgLite.packageName)) {
                        pkgMap.put(item.pkgLite.packageName, Integer.valueOf(item.pkgLite.versionCode));
                    }
                    recordHistory(history, item);
                    recordAdvanceAppsHistory(item);
                } else {
                    String str4 = TAG;
                    Slog.e(str4, "Copy " + item.toString() + " failed for first boot");
                }
            }
        }
        writeHistory(history);
        writePreinstallPackage(pkgMap);
        String str5 = TAG;
        Slog.i(str5, "Copy preinstall apps end for first boot, consume " + (System.currentTimeMillis() - currentTime) + "ms");
    }

    private static boolean skipYouPinIfHadPreinstall(Map<String, Long> history, Item item) {
        boolean isNewYoupinPreinstall = "/system/data-app/Youpin/Youpin.apk".equals(item.apkFile.getPath()) || "/vendor/data-app/Youpin/Youpin.apk".equals(item.apkFile.getPath());
        boolean hadPreInstallYouPin = history.containsKey("/system/data-app/com.xiaomi.youpin/com.xiaomi.youpin.apk") || history.containsKey("/cust/app/customized/recommended-3rd-com.xiaomi.youpin/recommended-3rd-com.xiaomi.youpin.apk");
        if (!isNewYoupinPreinstall || !hadPreInstallYouPin) {
            return false;
        }
        return true;
    }

    private static void copyPreinstallAppsForBoot(PackageManagerService pms, Settings settings) {
        long currentTime;
        PackageManagerService packageManagerService = pms;
        long currentTime2 = System.currentTimeMillis();
        Map<String, Long> history = new HashMap<>();
        readLineToSet(OTA_SKIP_BUSINESS_APP_LIST_FILE, NOT_OTA_PACKAGE_NAMES);
        readHistory(history);
        Slog.i(TAG, "copy preinstall apps start");
        for (Item item : sPreinstallApps.values()) {
            if (skipYouPinIfHadPreinstall(history, item)) {
                Slog.i(TAG, "skip YouPin because pre-installed");
            } else if (skipOTA(item)) {
                String str = TAG;
                Slog.i(str, "ota skip copy business preinstall app which under /system/data-app, packageName is :" + item.packageName);
            } else if (!dealed(history, item)) {
                if (signCheck(item.apkFile)) {
                    Slog.i(TAG, "Skip copying when the sign is false.");
                } else {
                    boolean recorded = recorded(history, item);
                    PackageSetting ps = settings.getPackageLPr(item.packageName);
                    if (systemAppDeletedOrDisabled(packageManagerService, item.packageName) || !isSystemAndNotUpdatedSystemApp(ps)) {
                        PackageParser.Package pkg = TAG;
                        Slog.i(pkg, "Copy " + item.toString());
                        if (ps != null) {
                            currentTime = currentTime2;
                            if (((long) item.pkgLite.versionCode) <= ps.versionCode) {
                                String str2 = TAG;
                                Slog.w(str2, item.toString() + " is not newer than " + ps.codePathString + "[" + ps.versionCode + "], skip coping");
                                recordHistory(history, item);
                                currentTime2 = currentTime;
                            } else {
                                PackageParser.Package pkg2 = parsePackage(item.app);
                                if (pkg2 == null || pkg2.packageName == null) {
                                    String str3 = TAG;
                                    Slog.e(str3, "Parse " + item.toString() + " failed, skip");
                                    currentTime2 = currentTime;
                                } else {
                                    boolean sameSignatures = PackageManagerServiceUtils.compareSignatures(pkg2.mSigningDetails.signatures, ps.signatures.mSigningDetails.signatures) == 0;
                                    if (sameSignatures || !isSystemApp(ps)) {
                                        if (!sameSignatures) {
                                            String str4 = TAG;
                                            Slog.e(str4, item.toString() + " mismatch signature with " + ps.codePath + ", delete it's resources and data before coping");
                                            if (!packageManagerService.deleteDataPackage(pkg2.packageName, false)) {
                                                String str5 = TAG;
                                                Slog.e(str5, "Delete mismatch signature app " + item.packageName + " failed, skip coping " + item.toString());
                                                currentTime2 = currentTime;
                                            }
                                        }
                                        if (copyPreinstallApp(packageManagerService, item, ps)) {
                                            recordHistory(history, item);
                                        } else {
                                            String str6 = TAG;
                                            Slog.e(str6, "Copy " + item.toString() + " failed");
                                        }
                                    } else {
                                        String str7 = TAG;
                                        Slog.e(str7, item.toString() + " mismatch signature with system app " + ps.codePath + ", skip coping");
                                        recordHistory(history, item);
                                        currentTime2 = currentTime;
                                    }
                                }
                            }
                        } else if (recorded) {
                            String str8 = TAG;
                            Slog.w(str8, "User had uninstalled " + item.packageName + ", skip coping" + item.toString());
                            recordHistory(history, item);
                            currentTime = currentTime2;
                        } else if (copyNewPreinstallApp(packageManagerService, item)) {
                            recordHistory(history, item);
                            currentTime = currentTime2;
                        } else {
                            String str9 = TAG;
                            Slog.e(str9, "Copy " + item.toString() + " failed");
                            currentTime = currentTime2;
                        }
                        currentTime2 = currentTime;
                    } else {
                        String str10 = TAG;
                        Slog.w(str10, "Skip copying new system updated preinstall app " + item.toString() + ", update it after system ready");
                        PackageParser.Package pkg3 = parsePackage(item.app);
                        if (pkg3 == null || pkg3.packageName == null) {
                            String str11 = TAG;
                            Slog.e(str11, "Parse " + item.toString() + " failed, skip");
                        } else {
                            item.pkg = pkg3;
                            sNewUpdatedSystemPreinstallApps.add(item);
                        }
                    }
                }
            }
        }
        long currentTime3 = currentTime2;
        writeHistory(history);
        String str12 = TAG;
        Slog.i(str12, "copy preinstall apps end, consume " + (System.currentTimeMillis() - currentTime3) + "ms");
    }

    public static void copyPreinstallApps(PackageManagerService pms, Settings settings) {
        if (ENCRYPTING_STATE.equals(SystemProperties.get("vold.decrypt"))) {
            Slog.w(TAG, "Detected encryption in progress - can't copy preinstall apps now!");
            return;
        }
        readPackageVersionMap();
        readPackagePAIList();
        copyTraditionalTrackFileToNewLocationIfNeed();
        if (pms.isFirstBoot() || pms.isDeviceUpgrading()) {
            parseAndDeleteDuplicatePreinstallApps();
        }
        if (pms.isFirstBoot()) {
            copyPreinstallAppsForFirstBoot(pms, settings);
        } else if (pms.isDeviceUpgrading()) {
            copyPreinstallAppsForBoot(pms, settings);
        } else {
            Slog.i(TAG, "Nothing need copy for normal boot.");
        }
        CloudControlPreinstallService.startCloudControlService();
    }

    public static void installNewUpdatedSystemPreinstallApps(Context context) {
        IPackageManager pm = getPackageManager();
        if (sNewUpdatedSystemPreinstallApps.isEmpty()) {
            Slog.i(TAG, "No updated system preinstall apps need to install");
            return;
        }
        long currentTime = System.currentTimeMillis();
        Slog.i(TAG, "Install updated system preinstall apps start");
        Map<String, Long> history = new HashMap<>();
        readHistory(history);
        List<File> needToInstall = new ArrayList<>();
        for (Item item : sNewUpdatedSystemPreinstallApps) {
            if (!dealed(history, item)) {
                PackageParser.Package pkg = item.pkg;
                try {
                    PackageInfo pi = pm.getPackageInfo(pkg.packageName, 64, 0);
                    if (pi == null || pkg.mVersionCode > pi.versionCode) {
                        needToInstall.add(item.apkFile);
                    } else {
                        String str = TAG;
                        Slog.w(str, item.toString() + " is not newer than " + pi.applicationInfo.sourceDir + ", skip");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Map.Entry<File, Integer> entry : PackageManagerProxy.installAppList(context, needToInstall).entrySet()) {
            File apkFile = entry.getKey();
            int result = entry.getValue().intValue();
            if (result == 0) {
                String str2 = TAG;
                Slog.i(str2, "Install new updated system preinstall app " + apkFile.getPath());
                recordToHistory(history, apkFile);
            } else {
                String str3 = TAG;
                Slog.w(str3, "Install new updated system preinstall app " + apkFile.getPath() + " failed, result " + result);
            }
            writeHistory(history);
        }
        String str4 = TAG;
        Slog.i(str4, "Install updated system preinstall apps end, consume " + (System.currentTimeMillis() - currentTime) + "ms");
    }

    public static void installCustApps(Context context) {
        List<File> needToInstall;
        List<File> custAppList = getCustomizePreinstallAppList();
        if (custAppList.isEmpty()) {
            Slog.w(TAG, " No cust app need to install");
            return;
        }
        long currentTime = System.currentTimeMillis();
        Slog.i(TAG, "Install cust apps start");
        Map<String, Long> history = new HashMap<>();
        readHistory(history);
        Map<String, Integer> pkgMap = new HashMap<>();
        List<File> needToInstall2 = new ArrayList<>();
        for (File app : custAppList) {
            File apkFile = getApkFile(app);
            if (!dealed(history, apkFile)) {
                needToInstall2.add(apkFile);
            }
        }
        for (Map.Entry<File, Integer> entry : PackageManagerProxy.installAppList(context, needToInstall2).entrySet()) {
            File apkFile2 = entry.getKey();
            int result = entry.getValue().intValue();
            if (result == 0) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Install cust app [");
                sb.append(apkFile2.getPath());
                sb.append("] mtime[");
                needToInstall = needToInstall2;
                sb.append(apkFile2.lastModified());
                sb.append("]");
                Slog.i(str, sb.toString());
                PackageParser.PackageLite pl = parsePackageLite(apkFile2);
                if (pl != null && !TextUtils.isEmpty(pl.packageName)) {
                    pkgMap.put(pl.packageName, Integer.valueOf(pl.versionCode));
                }
                recordToHistory(history, apkFile2);
            } else {
                needToInstall = needToInstall2;
                String str2 = TAG;
                Slog.w(str2, "Install cust app [" + apkFile2.getPath() + "] fail, result[" + result + "]");
            }
            Context context2 = context;
            needToInstall2 = needToInstall;
        }
        writeHistory(history);
        writePreinstallPackage(pkgMap);
        String str3 = TAG;
        Slog.i(str3, "Install cust apps end, consume " + (System.currentTimeMillis() - currentTime) + "ms");
    }

    public static void installVanwardCustApps(Context context) {
        Set<String> vanwardCustAppSet;
        List<File> custAppList;
        List<File> custAppList2 = getCustomizePreinstallAppList();
        Set<String> vanwardCustAppSet2 = new HashSet<>();
        readLineToSet(new File(CustomizeUtil.getMiuiAppDir(), "vanward_applist"), vanwardCustAppSet2);
        if (custAppList2.isEmpty()) {
            Context context2 = context;
            List<File> list = custAppList2;
            Set<String> set = vanwardCustAppSet2;
        } else if (vanwardCustAppSet2.isEmpty()) {
            Context context3 = context;
            List<File> list2 = custAppList2;
            HashSet hashSet = vanwardCustAppSet2;
        } else {
            long currentTime = System.currentTimeMillis();
            Slog.i(TAG, "Install vanward cust apps start");
            Map<String, Long> history = new HashMap<>();
            readHistory(history);
            Map<String, Integer> pkgMap = new HashMap<>();
            List<File> needToInstall = new ArrayList<>();
            for (File app : custAppList2) {
                File apkFile = getApkFile(app);
                if (vanwardCustAppSet2.contains(apkFile.getName()) && !dealed(history, apkFile)) {
                    needToInstall.add(apkFile);
                }
            }
            for (Map.Entry<File, Integer> entry : PackageManagerProxy.installAppList(context, needToInstall).entrySet()) {
                File apkFile2 = entry.getKey();
                int result = entry.getValue().intValue();
                if (result == 0) {
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    custAppList = custAppList2;
                    sb.append("install vanward cust app [");
                    sb.append(apkFile2.getPath());
                    sb.append("] mtime[");
                    vanwardCustAppSet = vanwardCustAppSet2;
                    sb.append(apkFile2.lastModified());
                    sb.append("]");
                    Slog.i(str, sb.toString());
                    PackageParser.PackageLite pl = parsePackageLite(apkFile2);
                    if (pl != null && !TextUtils.isEmpty(pl.packageName)) {
                        pkgMap.put(pl.packageName, Integer.valueOf(pl.versionCode));
                    }
                    recordToHistory(history, apkFile2);
                } else {
                    custAppList = custAppList2;
                    vanwardCustAppSet = vanwardCustAppSet2;
                    String str2 = TAG;
                    Slog.w(str2, "Install vanward cust app [" + apkFile2.getPath() + "] fail, result[" + result + "]");
                }
                custAppList2 = custAppList;
                vanwardCustAppSet2 = vanwardCustAppSet;
            }
            Set<String> set2 = vanwardCustAppSet2;
            writeHistory(history);
            writePreinstallPackage(pkgMap);
            String str3 = TAG;
            Slog.i(str3, "Install vanward cust apps end, consume " + (System.currentTimeMillis() - currentTime) + "ms");
            return;
        }
        Slog.w(TAG, "No vanward cust app need to install");
    }

    private static boolean isValidIme(String locale, Locale curLocale) {
        String[] locales = locale.split(",");
        for (int i = 0; i < locales.length; i++) {
            if (locales[i].startsWith(curLocale.toString()) || locales[i].equals("*")) {
                return true;
            }
            String str = locales[i];
            if (str.startsWith(curLocale.getLanguage() + "_*")) {
                return true;
            }
        }
        return false;
    }

    private static void writePreinstallPackage(Map<String, Integer> maps) {
        Slog.i(TAG, "Write preinstalled package name into /data/system/preinstall.list");
        if (maps != null && !maps.isEmpty()) {
            BufferedWriter bufferWriter = null;
            try {
                bufferWriter = new BufferedWriter(new FileWriter(PREINSTALL_PACKAGE_LIST, true));
                synchronized (mPackageVersionMap) {
                    for (Map.Entry<String, Integer> r : maps.entrySet()) {
                        mPackageVersionMap.put(r.getKey(), r.getValue());
                        bufferWriter.write(r.getKey() + ":" + r.getValue());
                        bufferWriter.write("\n");
                    }
                }
            } catch (IOException e) {
                try {
                    Slog.e(TAG, "Error occurs when to write preinstalled package name.");
                } catch (Throwable th) {
                    IoUtils.closeQuietly(bufferWriter);
                    throw th;
                }
            }
            IoUtils.closeQuietly(bufferWriter);
        }
    }

    public static void writePreinstallPAIPackage(String pkg) {
        Slog.i(TAG, "Write PAI package name into /data/system/preinstallPAI.list");
        if (!TextUtils.isEmpty(pkg)) {
            BufferedWriter bufferWriter = null;
            try {
                bufferWriter = new BufferedWriter(new FileWriter(PREINSTALL_PACKAGE_PAI_LIST, true));
                synchronized (mPackagePAIList) {
                    mPackagePAIList.add(pkg);
                    bufferWriter.write(pkg);
                    bufferWriter.write("\n");
                }
            } catch (IOException e) {
                try {
                    Slog.e(TAG, "Error occurs when to write PAI package name.");
                } catch (Throwable th) {
                    IoUtils.closeQuietly(bufferWriter);
                    throw th;
                }
            }
            IoUtils.closeQuietly(bufferWriter);
        }
    }

    public static ArrayList<String> getPeinstalledChannelList() {
        ArrayList<String> preinstalledChannelList = new ArrayList<>();
        File custVariantDir = CustomizeUtil.getMiuiCustVariantDir();
        if (custVariantDir != null) {
            String custVariantPath = custVariantDir.getPath();
            File file = CUSTOMIZED_APP_DIR;
            addPreinstallChannelToList(preinstalledChannelList, file, custVariantPath + "/customized_channellist");
            File file2 = CUSTOMIZED_APP_DIR;
            addPreinstallChannelToList(preinstalledChannelList, file2, custVariantPath + "/ota_customized_channellist");
            File file3 = RECOMMENDED_APP_DIR;
            addPreinstallChannelToList(preinstalledChannelList, file3, custVariantPath + "/recommended_channellist");
            File file4 = RECOMMENDED_APP_DIR;
            addPreinstallChannelToList(preinstalledChannelList, file4, custVariantPath + "/ota_recommended_channellist");
        }
        return preinstalledChannelList;
    }

    private static void addPreinstallChannelToList(List<String> preinstallChannelList, File channelDir, String channelListFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(channelListFile));
            while (true) {
                String readLine = reader.readLine();
                String channelName = readLine;
                if (readLine != null) {
                    preinstallChannelList.add(channelDir.getPath() + SliceClientPermissions.SliceAuthority.DELIMITER + channelName);
                } else {
                    reader.close();
                    return;
                }
            }
        } catch (IOException e) {
            String str = TAG;
            Slog.e(str, "Error occurs while read preinstalled channels " + e);
        }
    }

    private static boolean isSamePackage(File appFileA, File appFileB) {
        PackageParser.PackageLite plA = parsePackageLite(appFileA);
        PackageParser.PackageLite plB = parsePackageLite(appFileB);
        if (plA == null || plA.packageName == null) {
            String str = TAG;
            Slog.e(str, "Parse " + appFileA.getPath() + " failed, return false");
            return false;
        } else if (plB == null || plB.packageName == null) {
            String str2 = TAG;
            Slog.e(str2, "Parse " + appFileB.getPath() + " failed, return false");
            return false;
        } else if (plA.packageName.equals(plB.packageName)) {
            return true;
        } else {
            Slog.e(TAG, "isSamePackage return false.");
            return false;
        }
    }

    private static void readPackageVersionMap() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(PREINSTALL_PACKAGE_LIST));
            while (true) {
                String readLine = reader.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                String[] ss = line.split(":");
                if (ss != null && (ss.length == 1 || ss.length == 2)) {
                    int version = 0;
                    try {
                        if (ss.length == 2) {
                            version = Integer.valueOf(ss[1]).intValue();
                        }
                        mPackageVersionMap.put(ss[0], Integer.valueOf(version));
                    } catch (NumberFormatException e) {
                    }
                }
            }
        } catch (IOException e2) {
            String str = TAG;
            Slog.e(str, "Error occurs while read preinstalled packages " + e2);
            if (reader == null) {
                return;
            }
        } catch (Throwable th) {
            if (reader != null) {
                IoUtils.closeQuietly(reader);
            }
            throw th;
        }
        IoUtils.closeQuietly(reader);
    }

    private static void readPackagePAIList() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(PREINSTALL_PACKAGE_PAI_LIST));
            while (true) {
                String readLine = reader.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                mPackagePAIList.add(line);
            }
        } catch (IOException e) {
            String str = TAG;
            Slog.e(str, "Error occurs while read preinstalled PAI packages " + e);
            if (0 == 0) {
                return;
            }
        } catch (Throwable th) {
            if (0 != 0) {
                IoUtils.closeQuietly((AutoCloseable) null);
            }
            throw th;
        }
        IoUtils.closeQuietly(reader);
    }

    private static boolean skipOTA(Item item) {
        try {
            String deviceName = (String) Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class, String.class}).invoke((Object) null, new Object[]{"ro.product.name", ""});
            if ("sagit".equals(deviceName) && "com.xiaomi.youpin".equals(item.packageName)) {
                String str = TAG;
                Slog.i(str, "skipOTA, deviceName is " + deviceName);
                return false;
            }
        } catch (Exception e) {
            Slog.e(TAG, "Get exception", e);
        }
        return NOT_OTA_PACKAGE_NAMES.contains(item.packageName);
    }

    public static boolean isPreinstalledPackage(String pkg) {
        boolean containsKey;
        synchronized (mPackageVersionMap) {
            containsKey = mPackageVersionMap.containsKey(pkg);
        }
        return containsKey;
    }

    public static boolean isPreinstalledPAIPackage(String pkg) {
        boolean contains;
        synchronized (mPackagePAIList) {
            contains = mPackagePAIList.contains(pkg);
        }
        return contains;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r3 = new java.io.FileOutputStream(r1.chooseForWrite());
        r4 = new java.io.BufferedOutputStream(r3);
        android.os.FileUtils.setPermissions(r3.getFD(), 416, 1000, 1032);
        r5 = new java.lang.StringBuilder();
        r6 = mPackageVersionMap;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x004c, code lost:
        monitor-enter(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r7 = mPackageVersionMap.entrySet().iterator();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x005b, code lost:
        if (r7.hasNext() == false) goto L_0x008d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x005d, code lost:
        r8 = r7.next();
        r5.setLength(0);
        r5.append(r8.getKey());
        r5.append(":");
        r5.append(r8.getValue());
        r5.append("\n");
        r4.write(r5.toString().getBytes());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x008d, code lost:
        monitor-exit(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        r4.flush();
        android.os.FileUtils.sync(r3);
        r1.commit();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x009b, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x009d, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
        android.util.Slog.wtf(TAG, "Failed to write preinstall.list + ", r5);
        r1.rollback();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00c8, code lost:
        libcore.io.IoUtils.closeQuietly(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00cb, code lost:
        throw r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0013, code lost:
        r1 = new com.android.internal.util.JournaledFile(new java.io.File(PREINSTALL_PACKAGE_LIST), new java.io.File("/data/system/preinstall.list.tmp"));
        r4 = null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void removeFromPreinstallList(java.lang.String r10) {
        /*
            java.util.Map<java.lang.String, java.lang.Integer> r0 = mPackageVersionMap
            monitor-enter(r0)
            java.util.Map<java.lang.String, java.lang.Integer> r1 = mPackageVersionMap     // Catch:{ all -> 0x00cc }
            boolean r1 = r1.containsKey(r10)     // Catch:{ all -> 0x00cc }
            if (r1 != 0) goto L_0x000d
            monitor-exit(r0)     // Catch:{ all -> 0x00cc }
            return
        L_0x000d:
            java.util.Map<java.lang.String, java.lang.Integer> r1 = mPackageVersionMap     // Catch:{ all -> 0x00cc }
            r1.remove(r10)     // Catch:{ all -> 0x00cc }
            monitor-exit(r0)     // Catch:{ all -> 0x00cc }
            java.io.File r0 = new java.io.File
            java.lang.String r1 = "/data/system/preinstall.list.tmp"
            r0.<init>(r1)
            com.android.internal.util.JournaledFile r1 = new com.android.internal.util.JournaledFile
            java.io.File r2 = new java.io.File
            java.lang.String r3 = "/data/system/preinstall.list"
            r2.<init>(r3)
            r1.<init>(r2, r0)
            java.io.File r2 = r1.chooseForWrite()
            r3 = 0
            r4 = 0
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x009d }
            r5.<init>(r2)     // Catch:{ Exception -> 0x009d }
            r3 = r5
            java.io.BufferedOutputStream r5 = new java.io.BufferedOutputStream     // Catch:{ Exception -> 0x009d }
            r5.<init>(r3)     // Catch:{ Exception -> 0x009d }
            r4 = r5
            java.io.FileDescriptor r5 = r3.getFD()     // Catch:{ Exception -> 0x009d }
            r6 = 416(0x1a0, float:5.83E-43)
            r7 = 1000(0x3e8, float:1.401E-42)
            r8 = 1032(0x408, float:1.446E-42)
            android.os.FileUtils.setPermissions(r5, r6, r7, r8)     // Catch:{ Exception -> 0x009d }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x009d }
            r5.<init>()     // Catch:{ Exception -> 0x009d }
            java.util.Map<java.lang.String, java.lang.Integer> r6 = mPackageVersionMap     // Catch:{ Exception -> 0x009d }
            monitor-enter(r6)     // Catch:{ Exception -> 0x009d }
            java.util.Map<java.lang.String, java.lang.Integer> r7 = mPackageVersionMap     // Catch:{ all -> 0x0098 }
            java.util.Set r7 = r7.entrySet()     // Catch:{ all -> 0x0098 }
            java.util.Iterator r7 = r7.iterator()     // Catch:{ all -> 0x0098 }
        L_0x0057:
            boolean r8 = r7.hasNext()     // Catch:{ all -> 0x0098 }
            if (r8 == 0) goto L_0x008d
            java.lang.Object r8 = r7.next()     // Catch:{ all -> 0x0098 }
            java.util.Map$Entry r8 = (java.util.Map.Entry) r8     // Catch:{ all -> 0x0098 }
            r9 = 0
            r5.setLength(r9)     // Catch:{ all -> 0x0098 }
            java.lang.Object r9 = r8.getKey()     // Catch:{ all -> 0x0098 }
            java.lang.String r9 = (java.lang.String) r9     // Catch:{ all -> 0x0098 }
            r5.append(r9)     // Catch:{ all -> 0x0098 }
            java.lang.String r9 = ":"
            r5.append(r9)     // Catch:{ all -> 0x0098 }
            java.lang.Object r9 = r8.getValue()     // Catch:{ all -> 0x0098 }
            r5.append(r9)     // Catch:{ all -> 0x0098 }
            java.lang.String r9 = "\n"
            r5.append(r9)     // Catch:{ all -> 0x0098 }
            java.lang.String r9 = r5.toString()     // Catch:{ all -> 0x0098 }
            byte[] r9 = r9.getBytes()     // Catch:{ all -> 0x0098 }
            r4.write(r9)     // Catch:{ all -> 0x0098 }
            goto L_0x0057
        L_0x008d:
            monitor-exit(r6)     // Catch:{ all -> 0x0098 }
            r4.flush()     // Catch:{ Exception -> 0x009d }
            android.os.FileUtils.sync(r3)     // Catch:{ Exception -> 0x009d }
            r1.commit()     // Catch:{ Exception -> 0x009d }
            goto L_0x00a8
        L_0x0098:
            r7 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0098 }
            throw r7     // Catch:{ Exception -> 0x009d }
        L_0x009b:
            r5 = move-exception
            goto L_0x00c8
        L_0x009d:
            r5 = move-exception
            java.lang.String r6 = TAG     // Catch:{ all -> 0x009b }
            java.lang.String r7 = "Failed to write preinstall.list + "
            android.util.Slog.wtf(r6, r7, r5)     // Catch:{ all -> 0x009b }
            r1.rollback()     // Catch:{ all -> 0x009b }
        L_0x00a8:
            libcore.io.IoUtils.closeQuietly(r4)
            java.lang.String r5 = TAG
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Delete package:"
            r6.append(r7)
            r6.append(r10)
            java.lang.String r7 = " from preinstall.list"
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            android.util.Slog.d(r5, r6)
            return
        L_0x00c8:
            libcore.io.IoUtils.closeQuietly(r4)
            throw r5
        L_0x00cc:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00cc }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PreinstallApp.removeFromPreinstallList(java.lang.String):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r3 = new java.io.FileOutputStream(r1.chooseForWrite());
        r4 = new java.io.BufferedOutputStream(r3);
        android.os.FileUtils.setPermissions(r3.getFD(), 416, 1000, 1032);
        r5 = new java.lang.StringBuilder();
        r6 = mPackagePAIList;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x004c, code lost:
        monitor-enter(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x004d, code lost:
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0055, code lost:
        if (r8 >= mPackagePAIList.size()) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0057, code lost:
        r5.setLength(0);
        r5.append(mPackagePAIList.get(r8));
        r5.append("\n");
        r4.write(r5.toString().getBytes());
        r8 = r8 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0078, code lost:
        monitor-exit(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        r4.flush();
        android.os.FileUtils.sync(r3);
        r1.commit();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0086, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0088, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
        android.util.Slog.wtf(TAG, "Failed to delete preinstallPAI.list + ", r5);
        r1.rollback();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00b6, code lost:
        libcore.io.IoUtils.closeQuietly(r3);
        libcore.io.IoUtils.closeQuietly(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00bc, code lost:
        throw r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0013, code lost:
        r1 = new com.android.internal.util.JournaledFile(new java.io.File(PREINSTALL_PACKAGE_PAI_LIST), new java.io.File("/data/system/preinstallPAI.list.tmp"));
        r3 = null;
        r4 = null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void removeFromPreinstallPAIList(java.lang.String r10) {
        /*
            java.util.List<java.lang.String> r0 = mPackagePAIList
            monitor-enter(r0)
            java.util.List<java.lang.String> r1 = mPackagePAIList     // Catch:{ all -> 0x00bd }
            boolean r1 = r1.contains(r10)     // Catch:{ all -> 0x00bd }
            if (r1 != 0) goto L_0x000d
            monitor-exit(r0)     // Catch:{ all -> 0x00bd }
            return
        L_0x000d:
            java.util.List<java.lang.String> r1 = mPackagePAIList     // Catch:{ all -> 0x00bd }
            r1.remove(r10)     // Catch:{ all -> 0x00bd }
            monitor-exit(r0)     // Catch:{ all -> 0x00bd }
            java.io.File r0 = new java.io.File
            java.lang.String r1 = "/data/system/preinstallPAI.list.tmp"
            r0.<init>(r1)
            com.android.internal.util.JournaledFile r1 = new com.android.internal.util.JournaledFile
            java.io.File r2 = new java.io.File
            java.lang.String r3 = "/data/system/preinstallPAI.list"
            r2.<init>(r3)
            r1.<init>(r2, r0)
            java.io.File r2 = r1.chooseForWrite()
            r3 = 0
            r4 = 0
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0088 }
            r5.<init>(r2)     // Catch:{ Exception -> 0x0088 }
            r3 = r5
            java.io.BufferedOutputStream r5 = new java.io.BufferedOutputStream     // Catch:{ Exception -> 0x0088 }
            r5.<init>(r3)     // Catch:{ Exception -> 0x0088 }
            r4 = r5
            java.io.FileDescriptor r5 = r3.getFD()     // Catch:{ Exception -> 0x0088 }
            r6 = 416(0x1a0, float:5.83E-43)
            r7 = 1000(0x3e8, float:1.401E-42)
            r8 = 1032(0x408, float:1.446E-42)
            android.os.FileUtils.setPermissions(r5, r6, r7, r8)     // Catch:{ Exception -> 0x0088 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0088 }
            r5.<init>()     // Catch:{ Exception -> 0x0088 }
            java.util.List<java.lang.String> r6 = mPackagePAIList     // Catch:{ Exception -> 0x0088 }
            monitor-enter(r6)     // Catch:{ Exception -> 0x0088 }
            r7 = 0
            r8 = r7
        L_0x004f:
            java.util.List<java.lang.String> r9 = mPackagePAIList     // Catch:{ all -> 0x0083 }
            int r9 = r9.size()     // Catch:{ all -> 0x0083 }
            if (r8 >= r9) goto L_0x0078
            r5.setLength(r7)     // Catch:{ all -> 0x0083 }
            java.util.List<java.lang.String> r9 = mPackagePAIList     // Catch:{ all -> 0x0083 }
            java.lang.Object r9 = r9.get(r8)     // Catch:{ all -> 0x0083 }
            java.lang.String r9 = (java.lang.String) r9     // Catch:{ all -> 0x0083 }
            r5.append(r9)     // Catch:{ all -> 0x0083 }
            java.lang.String r9 = "\n"
            r5.append(r9)     // Catch:{ all -> 0x0083 }
            java.lang.String r9 = r5.toString()     // Catch:{ all -> 0x0083 }
            byte[] r9 = r9.getBytes()     // Catch:{ all -> 0x0083 }
            r4.write(r9)     // Catch:{ all -> 0x0083 }
            int r8 = r8 + 1
            goto L_0x004f
        L_0x0078:
            monitor-exit(r6)     // Catch:{ all -> 0x0083 }
            r4.flush()     // Catch:{ Exception -> 0x0088 }
            android.os.FileUtils.sync(r3)     // Catch:{ Exception -> 0x0088 }
            r1.commit()     // Catch:{ Exception -> 0x0088 }
            goto L_0x0093
        L_0x0083:
            r7 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0083 }
            throw r7     // Catch:{ Exception -> 0x0088 }
        L_0x0086:
            r5 = move-exception
            goto L_0x00b6
        L_0x0088:
            r5 = move-exception
            java.lang.String r6 = TAG     // Catch:{ all -> 0x0086 }
            java.lang.String r7 = "Failed to delete preinstallPAI.list + "
            android.util.Slog.wtf(r6, r7, r5)     // Catch:{ all -> 0x0086 }
            r1.rollback()     // Catch:{ all -> 0x0086 }
        L_0x0093:
            libcore.io.IoUtils.closeQuietly(r3)
            libcore.io.IoUtils.closeQuietly(r4)
            java.lang.String r5 = TAG
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Delete package:"
            r6.append(r7)
            r6.append(r10)
            java.lang.String r7 = " from preinstallPAI.list"
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            android.util.Slog.d(r5, r6)
            return
        L_0x00b6:
            libcore.io.IoUtils.closeQuietly(r3)
            libcore.io.IoUtils.closeQuietly(r4)
            throw r5
        L_0x00bd:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00bd }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PreinstallApp.removeFromPreinstallPAIList(java.lang.String):void");
    }

    public static void copyPreinstallPAITrackingFile(String type, String fileName, String content) {
        String filePath;
        if (!TextUtils.isEmpty(type) && !TextUtils.isEmpty(content)) {
            boolean isAppsflyer = false;
            if (TextUtils.equals(TYPE_TRACKING_APPSFLYER, type)) {
                filePath = PREINSTALL_PACKAGE_PAI_TRACKING_FILE;
                isAppsflyer = true;
            } else if (TextUtils.equals(TYPE_TRACKING_MIUI, type)) {
                filePath = PREINSTALL_PACKAGE_MIUI_TRACKING_DIR + fileName;
            } else {
                Slog.e(TAG, "Used invalid pai tracking type =" + type + "! you can use type:appsflyer or xiaomi");
                return;
            }
            if (!isAppsflyer || !mNewTrackContentList.contains(content)) {
                Slog.i(TAG, "use " + type + " tracking method!");
                try {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        Slog.d(TAG, "create tracking file：" + file.getAbsolutePath());
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdir();
                            FileUtils.setPermissions(file.getParentFile(), 509, -1, -1);
                        }
                        file.createNewFile();
                    }
                    FileUtils.setPermissions(file, 436, -1, -1);
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file, isAppsflyer));
                    if (isAppsflyer) {
                        mNewTrackContentList.add(content);
                    }
                    bw.write(content);
                    bw.write("\n");
                    bw.flush();
                    Slog.i(TAG, "Copy PAI tracking content Success!");
                    IoUtils.closeQuietly(bw);
                    restoreconPreinstallDir();
                } catch (IOException e) {
                    if (TextUtils.equals(TYPE_TRACKING_APPSFLYER, type)) {
                        Slog.e(TAG, "Error occurs when to copy PAI tracking content(" + content + ") into " + filePath + ":" + e);
                    } else if (TextUtils.equals(TYPE_TRACKING_MIUI, type)) {
                        Slog.e(TAG, "Error occurs when to create " + fileName + " PAI tracking file into " + filePath + ":" + e);
                    }
                    IoUtils.closeQuietly((AutoCloseable) null);
                } catch (Throwable th) {
                    IoUtils.closeQuietly((AutoCloseable) null);
                    throw th;
                }
            } else {
                Slog.i(TAG, "Content duplication dose not need to be written again! content is :" + content);
            }
        }
    }

    private static void copyTraditionalTrackFileToNewLocationIfNeed() {
        readNewPAITrackFileIfNeed();
        String appsflyerPath = SystemProperties.get("ro.appsflyer.preinstall.path", "");
        if (TextUtils.isEmpty(appsflyerPath)) {
            Slog.e(TAG, "no system property ro.appsflyer.preinstall.path");
            return;
        }
        File paiTrackingPath = new File(PREINSTALL_PACKAGE_PAI_TRACKING_FILE);
        if (TextUtils.equals(appsflyerPath, PREINSTALL_PACKAGE_PAI_TRACKING_FILE) && !paiTrackingPath.exists()) {
            try {
                String str = TAG;
                Slog.d(str, "create new appsflyer tracking file：" + paiTrackingPath.getAbsolutePath());
                if (!paiTrackingPath.getParentFile().exists()) {
                    paiTrackingPath.getParentFile().mkdir();
                    FileUtils.setPermissions(paiTrackingPath.getParentFile(), 509, -1, -1);
                }
                paiTrackingPath.createNewFile();
                FileUtils.setPermissions(paiTrackingPath, 436, -1, -1);
            } catch (IOException e) {
                String str2 = TAG;
                Slog.e(str2, "Error occurs when to create new appsflyer tracking" + e);
            }
            readTraditionalPAITrackFile();
            writeNewPAITrackFile();
        }
        restoreconPreinstallDir();
    }

    private static void readTraditionalPAITrackFile() {
        Slog.i(TAG, "read traditional track file content from /cust/etc/pre_install.appsflyer");
        File file = new File(OLD_PREINSTALL_PACKAGE_PAI_TRACKING_FILE);
        if (file.exists()) {
            mTraditionalTrackContentList.clear();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                while (true) {
                    String readLine = reader.readLine();
                    String line = readLine;
                    if (readLine == null) {
                        break;
                    }
                    mTraditionalTrackContentList.add(line);
                }
            } catch (IOException e) {
                String str = TAG;
                Slog.e(str, "Error occurs while read traditional track file content" + e);
            } catch (Throwable th) {
                IoUtils.closeQuietly((AutoCloseable) null);
                throw th;
            }
            IoUtils.closeQuietly(reader);
        }
    }

    private static void writeNewPAITrackFile() {
        Slog.i(TAG, "Write old track file content to  /data/miui/pai/pre_install.appsflyer");
        List<String> list = mTraditionalTrackContentList;
        if (list == null || list.isEmpty()) {
            Slog.e(TAG, "no content write to new appsflyer file");
            return;
        }
        BufferedWriter bufferWriter = null;
        try {
            File newFile = new File(PREINSTALL_PACKAGE_PAI_TRACKING_FILE);
            if (!newFile.getParentFile().exists()) {
                newFile.getParentFile().mkdirs();
                FileUtils.setPermissions(newFile.getParentFile(), 509, -1, -1);
            }
            bufferWriter = new BufferedWriter(new FileWriter(newFile, true));
            FileUtils.setPermissions(newFile, 436, -1, -1);
            synchronized (mTraditionalTrackContentList) {
                for (int i = 0; i < mTraditionalTrackContentList.size(); i++) {
                    String content = mTraditionalTrackContentList.get(i);
                    if (!mNewTrackContentList.contains(content)) {
                        mNewTrackContentList.add(content);
                        bufferWriter.write(content);
                        bufferWriter.write("\n");
                    }
                }
            }
        } catch (IOException e) {
            try {
                Slog.e(TAG, "Error occurs when to write track file from /cust/etc/pre_install.appsflyer to /data/miui/pai/pre_install.appsflyer");
            } catch (Throwable th) {
                IoUtils.closeQuietly(bufferWriter);
                throw th;
            }
        }
        IoUtils.closeQuietly(bufferWriter);
    }

    private static void readNewPAITrackFileIfNeed() {
        Slog.i(TAG, "read new track file content from /data/miui/pai/pre_install.appsflyer");
        File file = new File(PREINSTALL_PACKAGE_PAI_TRACKING_FILE);
        if (file.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                while (true) {
                    String readLine = reader.readLine();
                    String line = readLine;
                    if (readLine == null) {
                        break;
                    } else if (!mNewTrackContentList.contains(line)) {
                        mNewTrackContentList.add(line);
                    }
                }
            } catch (IOException e) {
                String str = TAG;
                Slog.e(str, "Error occurs while read new track file content on pkms start" + e);
            } catch (Throwable th) {
                IoUtils.closeQuietly((AutoCloseable) null);
                throw th;
            }
            IoUtils.closeQuietly(reader);
        }
    }

    private static void restoreconPreinstallDir() {
        File file = new File(PREINSTALL_PACKAGE_MIUI_TRACKING_DIR);
        String fileContext = SELinux.getFileContext(PREINSTALL_PACKAGE_MIUI_TRACKING_DIR);
        if (file.exists() && !TextUtils.equals(fileContext, PREINSTALL_PACKAGE_MIUI_TRACKING_DIR_CONTEXT)) {
            SELinux.restoreconRecursive(file);
        }
    }

    public static int getPreinstalledAppVersion(String pkg) {
        int intValue;
        synchronized (mPackageVersionMap) {
            Integer version = mPackageVersionMap.get(pkg);
            intValue = version != null ? version.intValue() : 0;
        }
        return intValue;
    }

    public static boolean isPreinstallApp(String packageName) {
        return sPreinstallApps.containsKey(packageName);
    }

    static PackageParser.Package parsePackage(File apkFile) {
        try {
            PackageParser.Package pkg = new PackageParser().parsePackage(apkFile, 1);
            if (pkg != null) {
                PackageParser.collectCertificates(pkg, false);
            }
            return pkg;
        } catch (PackageParser.PackageParserException e) {
            e.printStackTrace();
            return null;
        }
    }

    static PackageParser.PackageLite parsePackageLite(File apkFile) {
        try {
            return PackageParser.parsePackageLite(apkFile, 0);
        } catch (PackageParser.PackageParserException e) {
            e.printStackTrace();
            return null;
        }
    }

    static boolean cleanUpResource(PackageManagerService pms, PackageSetting ps) {
        try {
            PackageManagerService.InstallArgs args = (PackageManagerService.InstallArgs) ReflectionUtils.callMethod(pms, "createInstallArgsForExisting", PackageManagerService.InstallArgs.class, new Object[]{Integer.valueOf(((Integer) ReflectionUtils.callMethod(pms, "packageFlagsToInstallFlags", Integer.TYPE, new Object[]{ps})).intValue()), ps.codePathString, ps.resourcePathString, (String[]) ReflectionUtils.callStaticMethod(InstructionSets.class, "getAppDexInstructionSets", String[].class, new Object[]{ps})});
            if (args == null) {
                return false;
            }
            synchronized (pms.mInstallLock) {
                args.cleanUpResourcesLI();
            }
            return true;
        } catch (Throwable t) {
            Slog.e(TAG, "failed to call method: cleanUpResource", t);
            return false;
        }
    }

    public static void recordAdvanceAppsHistory(Item item) {
        String path = item.apkFile.getPath();
        if (path != null) {
            if (path.contains(AdvancePreinstallService.ADVANCE_TAG) || path.contains(AdvancePreinstallService.ADVERT_TAG)) {
                sAdvanceApps.put(item.packageName, item.apkFile.getPath());
            }
        }
    }
}
