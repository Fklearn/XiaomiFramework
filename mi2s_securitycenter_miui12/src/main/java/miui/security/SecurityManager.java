package miui.security;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import java.util.List;

public class SecurityManager {
    public static final int FLAG_AC_ENABLED = 1;
    public static final int FLAG_AC_PACKAGE_CANCELED = 8;
    public static final int FLAG_AC_PACKAGE_ENABLED = 2;
    public static final int FLAG_AC_PACKAGE_PASSED = 4;
    public static final int MODE_EACH = 0;
    public static final int MODE_LOCK_SCREEN = 1;
    public static final int MODE_TIME_OUT = 2;
    public static final String SKIP_INTERCEPT = "skip_interception";
    public static final String SKIP_INTERCEPT_ACTIVITY = "com.miui.gallery.activity.ExternalPhotoPageActivity";
    public static final String SKIP_INTERCEPT_PACKAGE = "com.miui.gallery";
    private static final String TAG = "SecurityManager";

    public void addAccessControlPass(String str) {
    }

    public void addAccessControlPassForUser(String str, int i) {
    }

    public boolean addMiuiFirewallSharedUid(int i) {
        return false;
    }

    public void checkAccessControl(Activity activity, int i) {
    }

    public boolean checkAccessControlPass(String str) {
        return false;
    }

    public boolean checkAccessControlPass(String str, Intent intent) {
        return false;
    }

    public boolean checkAccessControlPassAsUser(String str, int i) {
        return false;
    }

    public boolean checkAccessControlPassAsUser(String str, Intent intent, int i) {
        return false;
    }

    public boolean checkAccessControlPassword(String str, String str2) {
        return false;
    }

    public void finishAccessControl(String str) {
    }

    public void finishAccessControl(String str, int i) {
    }

    public String getAccessControlPasswordType() {
        return null;
    }

    public List<String> getAllPrivacyApps(int i) {
        return null;
    }

    public boolean getApplicationAccessControlEnabled(String str) {
        return false;
    }

    public boolean getApplicationAccessControlEnabledAsUser(String str, int i) {
        return false;
    }

    public boolean getApplicationMaskNotificationEnabledAsUser(String str, int i) {
        return false;
    }

    public List<String> getIncompatibleAppList() {
        return null;
    }

    public String getPackageNameByPid(int i) {
        return null;
    }

    public IBinder getTopActivity() {
        return null;
    }

    public boolean isAppHide() {
        return false;
    }

    public boolean isFunctionOpen() {
        return false;
    }

    public boolean isGameBoosterActived(int i) {
        return false;
    }

    public boolean isInputMethodOpen() {
        return false;
    }

    public boolean isPrivacyApp(String str, int i) {
        return false;
    }

    public boolean isValidDevice() {
        return false;
    }

    public boolean needFinishAccessControl(IBinder iBinder) {
        return false;
    }

    public boolean putSystemDataStringFile(String str, String str2) {
        return false;
    }

    public String readSystemDataStringFile(String str) {
        return null;
    }

    public void removeAccessControlPass(String str) {
    }

    public void removeAccessControlPassAsUser(String str, int i) {
    }

    public void setAccessControlPassword(String str, String str2) {
    }

    public boolean setAppHide(boolean z) {
        return false;
    }

    public void setAppPermissionControlOpen(int i) {
    }

    public void setApplicationAccessControlEnabled(String str, boolean z) {
    }

    public void setApplicationAccessControlEnabledForUser(String str, boolean z, int i) {
    }

    public void setApplicationMaskNotificationEnabledForUser(String str, boolean z, int i) {
    }

    public boolean setCurrentNetworkState(int i) {
        return false;
    }

    public void setGameBoosterIBinder(IBinder iBinder, int i, boolean z) {
    }

    public void setIncompatibleAppList(List<String> list) {
    }

    public boolean setMiuiFirewallRule(String str, int i, int i2, int i3) {
        return false;
    }

    public void setPrivacyApp(String str, int i, boolean z) {
    }

    public void setWakeUpTime(String str, long j) {
    }

    public boolean startInterceptSmsBySender(Context context, String str, int i) {
        return false;
    }

    public boolean stopInterceptSmsBySender() {
        return false;
    }

    public void updateLauncherPackageNames() {
    }
}
