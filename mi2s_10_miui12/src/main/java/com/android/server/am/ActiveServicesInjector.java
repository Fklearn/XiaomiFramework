package com.android.server.am;

import android.content.ComponentName;
import android.os.UserHandle;
import android.util.SparseArray;
import com.android.internal.app.MiuiServicePriority;
import com.android.internal.os.BackgroundThread;
import com.android.server.am.ActiveServices;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.mqsas.sdk.MQSEventManagerDelegate;
import miui.os.Build;
import miui.util.ObjectReference;
import miui.util.ReflectionUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class ActiveServicesInjector {
    private static final boolean DEBUG_SERVICE = false;
    private static final int PROCESS_STATE_FOREGROUND_SERVICE = 3;
    private static final int SERVICE_RESTART_BUFFER_SIZE = 20;
    private static final String TAG = "ActiveServicesInjector";
    private static Field sFieldIsLowMem = ReflectionUtils.tryFindField(ActivityManagerService.class, "mIsLowMem");
    static final ArrayList<String> sMayRestartProcessList = new ArrayList<>();
    private static final Map<ComponentKey, Integer> sRestartServiceMap = new HashMap();

    static {
        sMayRestartProcessList.add("com.android.incallui");
    }

    public static boolean canRestartServiceLocked(ServiceRecord record, ActivityManagerService service) {
        if (!AutoStartManagerService.canRestartServiceLocked(record.packageName, record.appInfo.uid, service)) {
            return false;
        }
        checkReportRestartService(record, service);
        return true;
    }

    public static boolean isServiceForeground(ActivityManagerService service, ComponentName className, int callingUid) {
        if (Build.IS_CTS_BUILD || className == null || service.mAppOpsService.checkOperation(10023, callingUid, className.getPackageName()) == 0) {
            return true;
        }
        return false;
    }

    public static void setServicePriority(ActivityManagerService service, List<MiuiServicePriority> servicePrioritys) {
        LowPriorityServiceHelper.mInstance.setServicePriority(servicePrioritys);
    }

    public static void setServicePriority(ActivityManagerService service, List<MiuiServicePriority> servicePrioritys, long noProcDelayTime) {
        LowPriorityServiceHelper.mInstance.setServicePriority(servicePrioritys);
        LowPriorityServiceHelper.mInstance.setNoProcDelayTime(noProcDelayTime);
    }

    public static void removeServicePriority(ActivityManagerService service, MiuiServicePriority servicePriority, boolean inBlacklist) {
        LowPriorityServiceHelper.mInstance.removeServicePriority(servicePriority, inBlacklist);
    }

    public static void closeCheckPriority(ActivityManagerService service) {
        LowPriorityServiceHelper.mInstance.closeCheckPriority();
    }

    public static boolean willRestartNow(ServiceRecord record) {
        return sMayRestartProcessList.contains(record.packageName);
    }

    public static boolean willRestartNow(ProcessRecord app) {
        if (app == null || app.getCurProcState() < 0 || app.getCurProcState() > 3 || !sMayRestartProcessList.contains(app.processName)) {
            return false;
        }
        return true;
    }

    public static void removeServiceLocked(ActiveServices.ServiceMap smap, ArrayList<ServiceRecord> mTmpCollection) {
        if (mTmpCollection != null) {
            for (int i = mTmpCollection.size() - 1; i >= 0; i--) {
                ServiceRecord service = mTmpCollection.get(i);
                smap.mDelayedStartList.remove(service);
                LowPriorityServiceHelper.mInstance.forceRemoveServiceLocked(service);
            }
        }
    }

    public static void removeServiceLocked(int userId, SparseArray<ActiveServices.ServiceMap> serviceMap, ArrayList<ServiceRecord> mTmpCollection) {
        if (userId == -1) {
            for (int i = serviceMap.size() - 1; i >= 0; i--) {
                removeServiceLocked(serviceMap.valueAt(i), mTmpCollection);
            }
            return;
        }
        ActiveServices.ServiceMap smap = serviceMap.get(userId);
        if (smap != null) {
            removeServiceLocked(smap, mTmpCollection);
        }
    }

    static void checkReportRestartService(ServiceRecord record, ActivityManagerService service) {
        try {
            if (sFieldIsLowMem != null && sFieldIsLowMem.getBoolean(service)) {
                ComponentKey s = new ComponentKey(record.instanceName, UserHandle.of(record.userId));
                if (sRestartServiceMap.containsKey(s)) {
                    Integer count = sRestartServiceMap.get(s);
                    boolean isPersistent = (record.serviceInfo.applicationInfo.flags & 8) != 0;
                    ObjectReference<Boolean> serviceReschedule = ReflectionUtils.tryGetStaticObjectField(ActiveServices.class, "SERVICE_RESCHEDULE", Boolean.class);
                    if (count != null && !isPersistent && (serviceReschedule == null || !((Boolean) serviceReschedule.get()).booleanValue())) {
                        Map<ComponentKey, Integer> map = sRestartServiceMap;
                        Integer valueOf = Integer.valueOf(count.intValue() + 1);
                        Integer count2 = valueOf;
                        map.put(s, valueOf);
                    }
                } else {
                    s.processName = record.processName;
                    sRestartServiceMap.put(s, 1);
                }
                if (sRestartServiceMap.size() >= 20) {
                    List<ComponentKey> services2 = new ArrayList<>();
                    for (ComponentKey key : sRestartServiceMap.keySet()) {
                        key.restartCount = sRestartServiceMap.get(key).intValue();
                        services2.add(key);
                    }
                    sRestartServiceMap.clear();
                    reportRestartService(services2);
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException e) {
        }
    }

    static void reportRestartService(final List<ComponentKey> services2) {
        BackgroundThread.getHandler().post(new Runnable() {
            public void run() {
                List<String> jsons = new ArrayList<>();
                for (ComponentKey key : services2) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("processName", key.processName);
                        object.put("instanceName", key.componentName);
                        object.put("model", Build.MODEL);
                        object.put("userId", key.user.getIdentifier());
                        object.put(AssistDataRequester.KEY_RECEIVER_EXTRA_COUNT, key.restartCount);
                        jsons.add(object.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (jsons.size() > 0) {
                    MQSEventManagerDelegate.getInstance().reportEventsV2("service_restart", jsons, "mqs_lowmem_service_restart_80191000", false);
                }
            }
        });
    }

    public static class ComponentKey {
        static final String COUNT = "count";
        static final String INSTANCE_NAME = "instanceName";
        static final String MODEL = "model";
        static final String PROCESS_NAME = "processName";
        static final String USER_ID = "userId";
        public final ComponentName componentName;
        private final int mHashCode;
        public String processName;
        public int restartCount;
        public final UserHandle user;

        public ComponentKey(ComponentName componentName2, UserHandle user2) {
            if (componentName2 == null || user2 == null) {
                throw new NullPointerException();
            }
            this.componentName = componentName2;
            this.user = user2;
            this.mHashCode = Arrays.hashCode(new Object[]{componentName2, user2});
        }

        public int hashCode() {
            return this.mHashCode;
        }

        public boolean equals(Object o) {
            ComponentKey other = (ComponentKey) o;
            return other != null && other.componentName.equals(this.componentName) && other.user.equals(this.user);
        }

        public String toString() {
            return this.componentName.flattenToString() + "#" + this.user;
        }
    }
}
