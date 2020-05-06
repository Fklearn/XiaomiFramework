package com.android.server.devicepolicy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.ArraySet;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSystemProperty;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.Preconditions;
import com.android.server.inputmethod.InputMethodManagerInternal;
import java.util.List;
import java.util.Set;

public class OverlayPackagesProvider {
    protected static final String TAG = "OverlayPackagesProvider";
    private final Context mContext;
    private final Injector mInjector;
    private final PackageManager mPm;

    @VisibleForTesting
    interface Injector {
        List<InputMethodInfo> getInputMethodListAsUser(int i);

        boolean isPerProfileImeEnabled();
    }

    public OverlayPackagesProvider(Context context) {
        this(context, new DefaultInjector());
    }

    private static final class DefaultInjector implements Injector {
        private DefaultInjector() {
        }

        public boolean isPerProfileImeEnabled() {
            return InputMethodSystemProperty.PER_PROFILE_IME_ENABLED;
        }

        public List<InputMethodInfo> getInputMethodListAsUser(int userId) {
            return InputMethodManagerInternal.get().getInputMethodListAsUser(userId);
        }
    }

    @VisibleForTesting
    OverlayPackagesProvider(Context context, Injector injector) {
        this.mContext = context;
        this.mPm = (PackageManager) Preconditions.checkNotNull(context.getPackageManager());
        this.mInjector = (Injector) Preconditions.checkNotNull(injector);
    }

    public Set<String> getNonRequiredApps(ComponentName admin, int userId, String provisioningAction) {
        Set<String> nonRequiredApps = getLaunchableApps(userId);
        nonRequiredApps.removeAll(getRequiredApps(provisioningAction, admin.getPackageName()));
        if (this.mInjector.isPerProfileImeEnabled()) {
            nonRequiredApps.removeAll(getSystemInputMethods(userId));
        } else if ("android.app.action.PROVISION_MANAGED_DEVICE".equals(provisioningAction) || "android.app.action.PROVISION_MANAGED_USER".equals(provisioningAction)) {
            nonRequiredApps.removeAll(getSystemInputMethods(userId));
        }
        nonRequiredApps.addAll(getDisallowedApps(provisioningAction));
        return nonRequiredApps;
    }

    private Set<String> getLaunchableApps(int userId) {
        Intent launcherIntent = new Intent("android.intent.action.MAIN");
        launcherIntent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> resolveInfos = this.mPm.queryIntentActivitiesAsUser(launcherIntent, 795136, userId);
        Set<String> apps = new ArraySet<>();
        for (ResolveInfo resolveInfo : resolveInfos) {
            apps.add(resolveInfo.activityInfo.packageName);
        }
        return apps;
    }

    private Set<String> getSystemInputMethods(int userId) {
        List<InputMethodInfo> inputMethods = this.mInjector.getInputMethodListAsUser(userId);
        Set<String> systemInputMethods = new ArraySet<>();
        for (InputMethodInfo inputMethodInfo : inputMethods) {
            if (inputMethodInfo.getServiceInfo().applicationInfo.isSystemApp()) {
                systemInputMethods.add(inputMethodInfo.getPackageName());
            }
        }
        return systemInputMethods;
    }

    private Set<String> getRequiredApps(String provisioningAction, String dpcPackageName) {
        Set<String> requiredApps = new ArraySet<>();
        requiredApps.addAll(getRequiredAppsSet(provisioningAction));
        requiredApps.addAll(getVendorRequiredAppsSet(provisioningAction));
        requiredApps.add(dpcPackageName);
        return requiredApps;
    }

    private Set<String> getDisallowedApps(String provisioningAction) {
        Set<String> disallowedApps = new ArraySet<>();
        disallowedApps.addAll(getDisallowedAppsSet(provisioningAction));
        disallowedApps.addAll(getVendorDisallowedAppsSet(provisioningAction));
        return disallowedApps;
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x005f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Set<java.lang.String> getRequiredAppsSet(java.lang.String r5) {
        /*
            r4 = this;
            int r0 = r5.hashCode()
            r1 = -920528692(0xffffffffc921d8cc, float:-662924.75)
            r2 = 2
            r3 = 1
            if (r0 == r1) goto L_0x002a
            r1 = -514404415(0xffffffffe156cfc1, float:-2.4766084E20)
            if (r0 == r1) goto L_0x0020
            r1 = -340845101(0xffffffffebaf1dd3, float:-4.2340572E26)
            if (r0 == r1) goto L_0x0016
        L_0x0015:
            goto L_0x0034
        L_0x0016:
            java.lang.String r0 = "android.app.action.PROVISION_MANAGED_PROFILE"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0015
            r0 = r3
            goto L_0x0035
        L_0x0020:
            java.lang.String r0 = "android.app.action.PROVISION_MANAGED_USER"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0015
            r0 = 0
            goto L_0x0035
        L_0x002a:
            java.lang.String r0 = "android.app.action.PROVISION_MANAGED_DEVICE"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0015
            r0 = r2
            goto L_0x0035
        L_0x0034:
            r0 = -1
        L_0x0035:
            if (r0 == 0) goto L_0x005f
            if (r0 == r3) goto L_0x005b
            if (r0 != r2) goto L_0x003f
            r0 = 17236112(0x1070090, float:2.4795988E-38)
            goto L_0x0063
        L_0x003f:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Provisioning type "
            r1.append(r2)
            r1.append(r5)
            java.lang.String r2 = " not supported."
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x005b:
            r0 = 17236113(0x1070091, float:2.479599E-38)
            goto L_0x0063
        L_0x005f:
            r0 = 17236114(0x1070092, float:2.4795993E-38)
        L_0x0063:
            android.util.ArraySet r1 = new android.util.ArraySet
            android.content.Context r2 = r4.mContext
            android.content.res.Resources r2 = r2.getResources()
            java.lang.String[] r2 = r2.getStringArray(r0)
            java.util.List r2 = java.util.Arrays.asList(r2)
            r1.<init>(r2)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.OverlayPackagesProvider.getRequiredAppsSet(java.lang.String):java.util.Set");
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x005f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Set<java.lang.String> getDisallowedAppsSet(java.lang.String r5) {
        /*
            r4 = this;
            int r0 = r5.hashCode()
            r1 = -920528692(0xffffffffc921d8cc, float:-662924.75)
            r2 = 2
            r3 = 1
            if (r0 == r1) goto L_0x002a
            r1 = -514404415(0xffffffffe156cfc1, float:-2.4766084E20)
            if (r0 == r1) goto L_0x0020
            r1 = -340845101(0xffffffffebaf1dd3, float:-4.2340572E26)
            if (r0 == r1) goto L_0x0016
        L_0x0015:
            goto L_0x0034
        L_0x0016:
            java.lang.String r0 = "android.app.action.PROVISION_MANAGED_PROFILE"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0015
            r0 = r3
            goto L_0x0035
        L_0x0020:
            java.lang.String r0 = "android.app.action.PROVISION_MANAGED_USER"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0015
            r0 = 0
            goto L_0x0035
        L_0x002a:
            java.lang.String r0 = "android.app.action.PROVISION_MANAGED_DEVICE"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0015
            r0 = r2
            goto L_0x0035
        L_0x0034:
            r0 = -1
        L_0x0035:
            if (r0 == 0) goto L_0x005f
            if (r0 == r3) goto L_0x005b
            if (r0 != r2) goto L_0x003f
            r0 = 17236092(0x107007c, float:2.4795931E-38)
            goto L_0x0063
        L_0x003f:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Provisioning type "
            r1.append(r2)
            r1.append(r5)
            java.lang.String r2 = " not supported."
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x005b:
            r0 = 17236093(0x107007d, float:2.4795934E-38)
            goto L_0x0063
        L_0x005f:
            r0 = 17236094(0x107007e, float:2.4795937E-38)
        L_0x0063:
            android.util.ArraySet r1 = new android.util.ArraySet
            android.content.Context r2 = r4.mContext
            android.content.res.Resources r2 = r2.getResources()
            java.lang.String[] r2 = r2.getStringArray(r0)
            java.util.List r2 = java.util.Arrays.asList(r2)
            r1.<init>(r2)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.OverlayPackagesProvider.getDisallowedAppsSet(java.lang.String):java.util.Set");
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x005f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Set<java.lang.String> getVendorRequiredAppsSet(java.lang.String r5) {
        /*
            r4 = this;
            int r0 = r5.hashCode()
            r1 = -920528692(0xffffffffc921d8cc, float:-662924.75)
            r2 = 2
            r3 = 1
            if (r0 == r1) goto L_0x002a
            r1 = -514404415(0xffffffffe156cfc1, float:-2.4766084E20)
            if (r0 == r1) goto L_0x0020
            r1 = -340845101(0xffffffffebaf1dd3, float:-4.2340572E26)
            if (r0 == r1) goto L_0x0016
        L_0x0015:
            goto L_0x0034
        L_0x0016:
            java.lang.String r0 = "android.app.action.PROVISION_MANAGED_PROFILE"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0015
            r0 = r3
            goto L_0x0035
        L_0x0020:
            java.lang.String r0 = "android.app.action.PROVISION_MANAGED_USER"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0015
            r0 = 0
            goto L_0x0035
        L_0x002a:
            java.lang.String r0 = "android.app.action.PROVISION_MANAGED_DEVICE"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0015
            r0 = r2
            goto L_0x0035
        L_0x0034:
            r0 = -1
        L_0x0035:
            if (r0 == 0) goto L_0x005f
            if (r0 == r3) goto L_0x005b
            if (r0 != r2) goto L_0x003f
            r0 = 17236126(0x107009e, float:2.4796027E-38)
            goto L_0x0063
        L_0x003f:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Provisioning type "
            r1.append(r2)
            r1.append(r5)
            java.lang.String r2 = " not supported."
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x005b:
            r0 = 17236127(0x107009f, float:2.479603E-38)
            goto L_0x0063
        L_0x005f:
            r0 = 17236128(0x10700a0, float:2.4796032E-38)
        L_0x0063:
            android.util.ArraySet r1 = new android.util.ArraySet
            android.content.Context r2 = r4.mContext
            android.content.res.Resources r2 = r2.getResources()
            java.lang.String[] r2 = r2.getStringArray(r0)
            java.util.List r2 = java.util.Arrays.asList(r2)
            r1.<init>(r2)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.OverlayPackagesProvider.getVendorRequiredAppsSet(java.lang.String):java.util.Set");
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x005f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Set<java.lang.String> getVendorDisallowedAppsSet(java.lang.String r5) {
        /*
            r4 = this;
            int r0 = r5.hashCode()
            r1 = -920528692(0xffffffffc921d8cc, float:-662924.75)
            r2 = 2
            r3 = 1
            if (r0 == r1) goto L_0x002a
            r1 = -514404415(0xffffffffe156cfc1, float:-2.4766084E20)
            if (r0 == r1) goto L_0x0020
            r1 = -340845101(0xffffffffebaf1dd3, float:-4.2340572E26)
            if (r0 == r1) goto L_0x0016
        L_0x0015:
            goto L_0x0034
        L_0x0016:
            java.lang.String r0 = "android.app.action.PROVISION_MANAGED_PROFILE"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0015
            r0 = r3
            goto L_0x0035
        L_0x0020:
            java.lang.String r0 = "android.app.action.PROVISION_MANAGED_USER"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0015
            r0 = 0
            goto L_0x0035
        L_0x002a:
            java.lang.String r0 = "android.app.action.PROVISION_MANAGED_DEVICE"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0015
            r0 = r2
            goto L_0x0035
        L_0x0034:
            r0 = -1
        L_0x0035:
            if (r0 == 0) goto L_0x005f
            if (r0 == r3) goto L_0x005b
            if (r0 != r2) goto L_0x003f
            r0 = 17236123(0x107009b, float:2.4796018E-38)
            goto L_0x0063
        L_0x003f:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Provisioning type "
            r1.append(r2)
            r1.append(r5)
            java.lang.String r2 = " not supported."
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x005b:
            r0 = 17236124(0x107009c, float:2.479602E-38)
            goto L_0x0063
        L_0x005f:
            r0 = 17236125(0x107009d, float:2.4796024E-38)
        L_0x0063:
            android.util.ArraySet r1 = new android.util.ArraySet
            android.content.Context r2 = r4.mContext
            android.content.res.Resources r2 = r2.getResources()
            java.lang.String[] r2 = r2.getStringArray(r0)
            java.util.List r2 = java.util.Arrays.asList(r2)
            r1.<init>(r2)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.OverlayPackagesProvider.getVendorDisallowedAppsSet(java.lang.String):java.util.Set");
    }
}
