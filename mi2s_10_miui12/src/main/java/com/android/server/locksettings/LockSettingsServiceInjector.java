package com.android.server.locksettings;

import android.content.pm.UserInfo;
import android.os.UserManager;
import com.android.internal.widget.LockPatternUtils;

public class LockSettingsServiceInjector {
    public static boolean tiedManagedProfileReadyToUnlock(UserInfo userInfo, LockPatternUtils mLockPatternUtils, LockSettingsStorage mStorage, UserManager mUserManager) {
        return true;
    }

    public static boolean shouldTieManagedProfileLock(LockSettingsStorage mStorage, UserManager mUserManager, int managedUserId) {
        return true;
    }
}
