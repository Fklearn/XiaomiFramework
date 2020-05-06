package com.miui.firstaidkit.model.performance;

import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import b.b.c.j.A;
import b.b.c.j.C;
import b.b.c.j.x;
import b.b.o.g.d;
import com.miui.firstaidkit.d.a;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class DeviceManagerModel extends AbsModel {
    private static final String TAG = "DeviceManagerModel";
    private static List<String> whiteList = new ArrayList();
    private DevicePolicyManager devicePolicyManager = ((DevicePolicyManager) getContext().getSystemService("device_policy"));
    private boolean isDanger;
    private UserManager userManager = ((UserManager) getContext().getSystemService("user"));

    static {
        whiteList.add("com.microsoft.office.outlook");
        whiteList.add("com.android.email");
    }

    public DeviceManagerModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("device_manager");
        setTrackIgnoreStr(getTrackStr() + "_ignore");
    }

    private void addActiveAdminsForProfile(List<ComponentName> list, UserHandle userHandle) {
        if (list != null) {
            PackageManager packageManager = getContext().getPackageManager();
            int size = list.size();
            ArrayList arrayList = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                List<ResolveInfo> a2 = a.a(packageManager, new Intent().setComponent(list.get(i)), 32896, userHandle);
                if (a2 != null) {
                    int size2 = a2.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        DeviceAdminInfo createDeviceAdminInfo = createDeviceAdminInfo(a2.get(i2));
                        if (createDeviceAdminInfo != null) {
                            String packageName = createDeviceAdminInfo.getPackageName();
                            boolean i3 = x.i(getContext(), packageName);
                            boolean contains = whiteList.contains(packageName);
                            if (!i3 && !contains) {
                                arrayList.add(createDeviceAdminInfo);
                            }
                        }
                    }
                }
            }
            if (!arrayList.isEmpty()) {
                this.isDanger = true;
            }
        }
    }

    private DeviceAdminInfo createDeviceAdminInfo(ResolveInfo resolveInfo) {
        StringBuilder sb;
        ActivityInfo activityInfo;
        if (resolveInfo != null && (activityInfo = resolveInfo.activityInfo) != null && UserHandle.getUserId(activityInfo.applicationInfo.uid) == 999) {
            return null;
        }
        try {
            return new DeviceAdminInfo(getContext(), resolveInfo);
        } catch (XmlPullParserException e) {
            e = e;
            sb = new StringBuilder();
            sb.append("Skipping ");
            sb.append(resolveInfo.activityInfo);
            Log.w(TAG, sb.toString(), e);
            return null;
        } catch (IOException e2) {
            e = e2;
            sb = new StringBuilder();
            sb.append("Skipping ");
            sb.append(resolveInfo.activityInfo);
            Log.w(TAG, sb.toString(), e);
            return null;
        }
    }

    public String getButtonTitle() {
        return getContext().getString(R.string.first_aid_card_kadun_button2);
    }

    public String getDesc() {
        return "";
    }

    public int getIndex() {
        return 56;
    }

    public String getSummary() {
        return getContext().getString(R.string.first_aid_card_kadun_summary2);
    }

    public String getTitle() {
        return getContext().getString(R.string.first_aid_card_kadun_title);
    }

    public void optimize(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.DeviceAdminSettings"));
        if (!x.a(context, intent, 100)) {
            A.a(context, (int) R.string.app_not_installed_toast);
        }
    }

    public void scan() {
        this.isDanger = false;
        updateList();
        setSafe(this.isDanger ? AbsModel.State.DANGER : AbsModel.State.SAFE);
    }

    /* access modifiers changed from: package-private */
    public void updateList() {
        List<UserHandle> userProfiles = this.userManager.getUserProfiles();
        int size = userProfiles.size();
        for (int i = 0; i < size; i++) {
            UserHandle userHandle = userProfiles.get(i);
            int identifier = userHandle.getIdentifier();
            if (!C.b(identifier)) {
                List list = (List) d.a(TAG, (Object) this.devicePolicyManager, List.class, "getActiveAdminsAsUser", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(identifier));
                StringBuilder sb = new StringBuilder();
                sb.append("activeAdmins = ");
                sb.append(list != null ? list.toString() : "activeAdmins is null");
                Log.d(TAG, sb.toString());
                addActiveAdminsForProfile(list, userHandle);
            }
        }
    }
}
