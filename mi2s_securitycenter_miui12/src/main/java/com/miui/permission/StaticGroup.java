package com.miui.permission;

import android.os.Build;
import java.util.ArrayList;
import java.util.List;

public class StaticGroup {
    public static final List<Long> mCallandContactPermissionList = new ArrayList();
    public static final List<Long> mSMSandMMSPermissionList = new ArrayList();

    static {
        mSMSandMMSPermissionList.add(1L);
        mSMSandMMSPermissionList.add(Long.valueOf(PermissionManager.PERM_ID_READSMS));
        mSMSandMMSPermissionList.add(Long.valueOf(PermissionManager.PERM_ID_READ_NOTIFICATION_SMS));
        mSMSandMMSPermissionList.add(Long.valueOf(PermissionManager.PERM_ID_SENDMMS));
        mSMSandMMSPermissionList.add(Long.valueOf(PermissionManager.PERM_ID_READMMS));
        mCallandContactPermissionList.add(2L);
        mCallandContactPermissionList.add(8L);
        mCallandContactPermissionList.add(Long.valueOf(PermissionManager.PERM_ID_READCONTACT));
        mCallandContactPermissionList.add(16L);
        mCallandContactPermissionList.add(1073741824L);
        if (Build.VERSION.SDK_INT >= 23) {
            mCallandContactPermissionList.add(Long.valueOf(PermissionManager.PERM_ID_PROCESS_OUTGOING_CALLS));
        }
    }
}
