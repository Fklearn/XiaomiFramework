package com.miui.maml.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.IPackageInstallObserver2;
import android.content.res.Configuration;
import android.content.res.MiuiConfiguration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.MemoryFile;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import java.io.FileDescriptor;
import java.util.Optional;
import miui.os.SystemProperties;

public class HideSdkDependencyUtils {
    private static final String DEBUG_LAYOUT_PROPERTY = "debug.layout";
    private static final String TAG = "MAML_Reflect";
    private static final int TETHERING_WIFI = 0;

    public static int Configuration_getThemeChanged(Configuration configuration) {
        try {
            MiuiConfiguration miuiConfiguration = (MiuiConfiguration) ReflectionHelper.getFieldValue(Configuration.class, configuration, "extraConfig");
            if (miuiConfiguration == null) {
                return 0;
            }
            return miuiConfiguration.themeChanged;
        } catch (Exception e) {
            Log.e(TAG, "Invoke | Configuration_getThemeChanged() occur EXCEPTION: ", e);
            return 0;
        }
    }

    public static int Context_getUserId(Context context) {
        try {
            return ((Integer) ReflectionHelper.invokeObject(Context.class, context, "getUserId", new Class[0], new Object[0])).intValue();
        } catch (Exception e) {
            Log.e(TAG, "Invoke | Context_getUserId() occur EXCEPTION: ", e);
            return 0;
        }
    }

    public static void Context_startActivityAsUser(Context context, Intent intent, Bundle bundle, UserHandle userHandle) {
        try {
            ReflectionHelper.invokeObject(Context.class, context, "startActivityAsUser", new Class[]{Intent.class, Bundle.class, UserHandle.class}, intent, bundle, userHandle);
        } catch (Exception e) {
            Log.e(TAG, "Invoke | Context_startActivityAsUser() occur EXCEPTION: ", e);
        }
    }

    public static ComponentName Context_startServiceAsUser(Context context, Intent intent, UserHandle userHandle) {
        try {
            return (ComponentName) ReflectionHelper.invokeObject(Context.class, context, "startServiceAsUser", new Class[]{Intent.class, UserHandle.class}, intent, userHandle);
        } catch (Exception e) {
            Log.e(TAG, "Invoke | Context_startServiceAsUser() occur EXCEPTION: ", e);
            return null;
        }
    }

    public static FileDescriptor MemoryFile_getFileDescriptor(MemoryFile memoryFile) {
        try {
            return (FileDescriptor) ReflectionHelper.invokeObject(FileDescriptor.class, memoryFile, "getFileDescriptor", new Class[0], new Object[0]);
        } catch (Exception e) {
            Log.e(TAG, "Invoke | MemoryFile_getFileDescriptor() occur EXCEPTION: ", e);
            return null;
        }
    }

    public static boolean MotionEvent_isTouchEvent(MotionEvent motionEvent) {
        try {
            return ((Boolean) ReflectionHelper.invokeObject(MotionEvent.class, motionEvent, "isTouchEvent", new Class[0], new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e(TAG, "Invoke | MotionEvent_isTouchEvent() occur EXCEPTION: ", e);
            return false;
        }
    }

    public static boolean PreloadedAppPolicy_installPreloadedDataApp(final Context context, String str, final Intent intent, final Bundle bundle) {
        try {
            Class<?> cls = ReflectionHelper.getClass("miui.content.pm.PreloadedAppPolicy");
            if (Build.VERSION.SDK_INT >= 28) {
                return ((Boolean) ReflectionHelper.invokeObject(cls, (Object) null, "installPreloadedDataApp", new Class[]{Context.class, String.class, IPackageInstallObserver2.class, Integer.TYPE}, context, str, new IPackageInstallObserver2.Stub() {
                    public void onPackageInstalled(String str, int i, String str2, Bundle bundle) {
                        Utils.startActivity(context, intent, bundle);
                    }

                    public void onUserActionRequired(Intent intent) {
                    }
                }, 1)).booleanValue();
            }
            return ((Boolean) ReflectionHelper.invokeObject(cls, (Object) null, "installPreloadedDataApp", new Class[]{Context.class, String.class, IPackageInstallObserver.class, Integer.TYPE}, context, str, new IPackageInstallObserver.Stub() {
                public void packageInstalled(String str, int i) {
                    Utils.startActivity(context, intent, bundle);
                }
            }, 1)).booleanValue();
        } catch (Exception e) {
            Log.e(TAG, "Invoke | PreloadedAppPolicy_installPreloadedDataApp() occur EXCEPTION: ", e);
            return false;
        }
    }

    public static void StorageManager_disableUsbMassStorage(StorageManager storageManager) {
        try {
            ReflectionHelper.invoke(StorageManager.class, storageManager, "disableUsbMassStorage", new Class[0], new Object[0]);
        } catch (Exception e) {
            Log.e(TAG, "Invoke | StorageManager_disableUsbMassStorage() occur EXCEPTION: ", e);
        }
    }

    public static void StorageManager_enableUsbMassStorage(StorageManager storageManager) {
        try {
            ReflectionHelper.invoke(StorageManager.class, storageManager, "enableUsbMassStorage", new Class[0], new Object[0]);
        } catch (Exception e) {
            Log.e(TAG, "Invoke | StorageManager_enableUsbMassStorage() occur EXCEPTION: ", e);
        }
    }

    public static boolean StorageManager_isUsbMassStorageEnabled(StorageManager storageManager) {
        try {
            return ((Boolean) ReflectionHelper.invokeObject(StorageManager.class, storageManager, "isUsbMassStorageEnabled", new Class[0], new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e(TAG, "Invoke | StorageManager_isUsbMassStorageEnabled() occur EXCEPTION: ", e);
            return false;
        }
    }

    public static Typeface TypefaceUtils_replaceTypeface(Context context, Typeface typeface) {
        try {
            return (Typeface) ReflectionHelper.invokeObject(ReflectionHelper.getClass("miui.util.TypefaceUtils"), (Object) null, "replaceTypeface", new Class[]{Context.class, Typeface.class}, context, typeface);
        } catch (Exception e) {
            Log.e(TAG, "Invoke | TypefaceUtils_replaceTypeface() occur EXCEPTION: ", e);
            return null;
        }
    }

    public static UserHandle UserHandle_CURRENT() {
        try {
            return (UserHandle) ReflectionHelper.getFieldValue(UserHandle.class, (Object) null, "CURRENT");
        } catch (Exception e) {
            Log.e(TAG, "Invoke | UserHandle_CURRENT() occur EXCEPTION: ", e);
            return null;
        }
    }

    public static int UserHandle_getIdentifier(UserHandle userHandle) {
        try {
            return ((Integer) ReflectionHelper.invokeObject(UserHandle.class, userHandle, "getIdentifier", new Class[0], new Object[0])).intValue();
        } catch (Exception e) {
            Log.e(TAG, "Invoke | UserHandle_getIdentifier() occur EXCEPTION: ", e);
            return 0;
        }
    }

    public static UserHandle UserHandle_getInstance_with_int(int i) {
        try {
            return (UserHandle) ReflectionHelper.getConstructorInstance(UserHandle.class, new Class[]{Integer.TYPE}, Integer.valueOf(i));
        } catch (Exception e) {
            Log.e(TAG, "Invoke | UserHandle_getInstance_with_int() occur EXCEPTION: ", e);
            return null;
        }
    }

    public static boolean WifiManager_isWifiApEnabled(WifiManager wifiManager) {
        try {
            return ((Boolean) ReflectionHelper.invokeObject(WifiManager.class, wifiManager, "isWifiApEnabled", new Class[0], new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e(TAG, "Invoke | WifiManager_isWifiApEnabled() occur EXCEPTION: ", e);
            return false;
        }
    }

    public static void WindowManager_LayoutParams_setLayoutParamsBlurRatio(WindowManager.LayoutParams layoutParams, float f) {
        try {
            ReflectionHelper.setFieldValue(WindowManager.LayoutParams.class, layoutParams, "blurRatio", Float.valueOf(f));
        } catch (Exception e) {
            Log.e(TAG, "Invoke | WindowManager_LayoutParams_setLayoutParamsBlurRatio() occur EXCEPTION: ", e);
        }
    }

    public static boolean isShowDebugLayout() {
        try {
            return Build.VERSION.SDK_INT >= 29 ? ((Boolean) ((Optional) ReflectionHelper.invokeObject(ReflectionHelper.getClass("android.sysprop.DisplayProperties"), (Object) null, "debug_layout", new Class[0], new Object[0])).orElse(false)).booleanValue() : SystemProperties.getBoolean(DEBUG_LAYOUT_PROPERTY, false);
        } catch (Exception e) {
            Log.e(TAG, "Invoke | isShowDebugLayout() occur EXCEPTION: ", e);
            return false;
        }
    }

    public static void setWifiApEnabled(Context context, boolean z) {
        try {
            if (Build.VERSION.SDK_INT >= 28) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
                Class<?> cls = ReflectionHelper.getClass("android.net.ConnectivityManager$OnStartTetheringCallback");
                if (z) {
                    ReflectionHelper.invoke(ConnectivityManager.class, connectivityManager, "startTethering", new Class[]{Integer.TYPE, Boolean.TYPE, cls}, 0, true, null);
                    return;
                }
                ReflectionHelper.invoke(ConnectivityManager.class, connectivityManager, "stopTethering", new Class[]{Integer.TYPE}, 0);
                return;
            }
            ReflectionHelper.invokeObject(WifiManager.class, (WifiManager) context.getSystemService("wifi"), "setWifiApEnabled", new Class[]{WifiConfiguration.class, Boolean.TYPE}, null, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e(TAG, "Invoke | setWifiApEnabled() occur EXCEPTION: ", e);
        }
    }
}
