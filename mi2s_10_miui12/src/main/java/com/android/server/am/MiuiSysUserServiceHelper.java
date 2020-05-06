package com.android.server.am;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.android.internal.app.IMiuiSysUser;
import com.android.internal.os.BackgroundThread;

public class MiuiSysUserServiceHelper {
    private static final int ADJ_MEM_FACTOR_LOW = 2;
    private static final int ADJ_MEM_FACTOR_NORMAL = 0;
    private static final String EVENT_MEMORY_LEVEL = "EVENT_MEMORY_LEVEL";
    private static final String KEY_MEMORY_LEVELLOW = "KEY_MEMORY_LEVEL_LOW";
    private static long MSG_INPUT_DELAY_TIME = 1000;
    private static final int MSG_INPUT_TIMEOUT = 2;
    private static final int MSG_RESUME_DELAY = 1;
    private static long MSG_RESUME_DELAY_TIME = 3000;
    public static final String TAG = "MIUI_SYS_USER";
    private static boolean mEnable = getDefaultEnable();
    private static Handler sHandler = new UserHandler(BackgroundThread.getHandler().getLooper());
    /* access modifiers changed from: private */
    public static boolean sInputLimit = false;
    /* access modifiers changed from: private */
    public static boolean sIsLimit = false;
    private static int sLastMemoryLevel = 0;
    public static String sTopPackage = null;
    private static IMiuiSysUser sysUser;

    public static boolean getDefaultEnable() {
        return false;
    }

    public static void setEnable(boolean enable) {
    }

    static class UserHandler extends Handler {
        public UserHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                boolean unused = MiuiSysUserServiceHelper.sIsLimit = false;
            } else if (i == 2) {
                boolean unused2 = MiuiSysUserServiceHelper.sInputLimit = false;
            }
        }
    }

    public static void sendInputMessage() {
        if (!mEnable) {
            sInputLimit = false;
            return;
        }
        Handler handler = sHandler;
        if (handler != null) {
            sInputLimit = true;
            handler.removeMessages(2);
            sHandler.sendEmptyMessageDelayed(2, MSG_INPUT_DELAY_TIME);
        }
    }

    private static void sendAllLimitMessage() {
        if (!mEnable) {
            sIsLimit = false;
            return;
        }
        Handler handler = sHandler;
        if (handler != null) {
            sIsLimit = true;
            handler.removeMessages(1);
            sHandler.sendEmptyMessageDelayed(1, MSG_RESUME_DELAY_TIME);
        }
    }

    public static void setMemLevel(int memoryLevel) {
        if (sLastMemoryLevel != memoryLevel) {
            notifyMemoryLevelChange(memoryLevel);
        }
        sLastMemoryLevel = memoryLevel;
    }

    public static void notifyMemoryLevelChange(int memFactor) {
        if (memFactor >= 2) {
            if (sLastMemoryLevel < 2) {
                Bundle data = new Bundle();
                data.putBoolean(KEY_MEMORY_LEVELLOW, true);
                notifyEvent(EVENT_MEMORY_LEVEL, data);
            }
        } else if (sLastMemoryLevel >= 2) {
            Bundle data2 = new Bundle();
            data2.putBoolean(KEY_MEMORY_LEVELLOW, false);
            notifyEvent(EVENT_MEMORY_LEVEL, data2);
        }
    }

    public static boolean isAllLimit() {
        return sIsLimit || sInputLimit;
    }

    public static boolean isLowMemory() {
        return sLastMemoryLevel >= 2;
    }

    public static void setMiuiSysUser(IMiuiSysUser obj) {
        sysUser = obj;
    }

    static void notifyAMProcStart(long startUsedTime, int pid, int uid, String processName, ComponentName name, String reason) {
        try {
            if (sysUser != null) {
                sysUser.notifyAMProcStart(startUsedTime, pid, uid, processName, name, reason);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "call notifyAMProcStart error !!!" + e.getMessage());
        }
    }

    static void notifyAMProcDied(int pid, String packageName) {
        try {
            if (sysUser != null) {
                sysUser.notifyAMProcDied(pid, packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "call notifyAMProcDied error !!!" + e.getMessage());
        }
    }

    static void notifyAMDestroyActivity(int pid, int identify) {
        try {
            if (sysUser != null) {
                sysUser.notifyAMDestroyActivity(pid, identify);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "call notifyAMDestroyActivity error !!!" + e.getMessage());
        }
    }

    static void notifyAMPauseActivity(int pid, int identify) {
        try {
            if (sysUser != null) {
                sysUser.notifyAMPauseActivity(pid, identify);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "call notifyAMPauseActivity error !!!" + e.getMessage());
        }
    }

    static void notifyAMResumeActivity(ComponentName name, int pid, int identify) {
        try {
            sendAllLimitMessage();
            sTopPackage = name.getPackageName();
            if (sysUser != null) {
                sysUser.notifyAMResumeActivity(name, pid, identify);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "call notifyAMResumeActivity error !!!" + e.getMessage());
        }
    }

    static void notifyAMRestartActivity(ComponentName name, int pid, int identify) {
        try {
            sendAllLimitMessage();
            sTopPackage = name.getPackageName();
            if (sysUser != null) {
                sysUser.notifyAMRestartActivity(name, pid, identify);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "call notifyAMRestartActivity error !!!" + e.getMessage());
        }
    }

    static void notifyAMCreateActivity(ComponentName name, int pid, int identify) {
        try {
            sendAllLimitMessage();
            sTopPackage = name.getPackageName();
            if (sysUser != null) {
                sysUser.notifyAMCreateActivity(name, pid, identify);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "call notifyAMCreateActivity error !!!" + e.getMessage());
        }
    }

    static void notifyEvent(String eventTag, Bundle data) {
        try {
            if (sysUser != null) {
                sysUser.notifyEvent(eventTag, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "call notifyEvent error !!!" + e.getMessage());
        }
    }
}
