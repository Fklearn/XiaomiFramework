package com.android.server.pm;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.AuxiliaryResolveInfo;
import android.content.pm.InstantAppIntentFilter;
import android.content.pm.InstantAppRequest;
import android.content.pm.InstantAppResolveInfo;
import android.metrics.LogMaker;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.Slog;
import com.android.internal.logging.MetricsLogger;
import com.android.server.pm.ComponentResolver;
import com.android.server.pm.InstantAppResolverConnection;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class InstantAppResolver {
    private static final boolean DEBUG_INSTANT = Build.IS_DEBUGGABLE;
    private static final int RESOLUTION_BIND_TIMEOUT = 2;
    private static final int RESOLUTION_CALL_TIMEOUT = 3;
    private static final int RESOLUTION_FAILURE = 1;
    private static final int RESOLUTION_SUCCESS = 0;
    private static final String TAG = "PackageManager";
    private static MetricsLogger sMetricsLogger;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ResolutionStatus {
    }

    private static MetricsLogger getLogger() {
        if (sMetricsLogger == null) {
            sMetricsLogger = new MetricsLogger();
        }
        return sMetricsLogger;
    }

    public static Intent sanitizeIntent(Intent origIntent) {
        Uri sanitizedUri;
        Intent sanitizedIntent = new Intent(origIntent.getAction());
        Set<String> categories = origIntent.getCategories();
        if (categories != null) {
            for (String category : categories) {
                sanitizedIntent.addCategory(category);
            }
        }
        if (origIntent.getData() == null) {
            sanitizedUri = null;
        } else {
            sanitizedUri = Uri.fromParts(origIntent.getScheme(), "", "");
        }
        sanitizedIntent.setDataAndType(sanitizedUri, origIntent.getType());
        sanitizedIntent.addFlags(origIntent.getFlags());
        sanitizedIntent.setPackage(origIntent.getPackage());
        return sanitizedIntent;
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x007a  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x007f  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00b7  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0119 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.content.pm.AuxiliaryResolveInfo doInstantAppResolutionPhaseOne(com.android.server.pm.InstantAppResolverConnection r19, android.content.pm.InstantAppRequest r20) {
        /*
            r1 = r20
            long r2 = java.lang.System.currentTimeMillis()
            java.util.UUID r0 = java.util.UUID.randomUUID()
            java.lang.String r11 = r0.toString()
            boolean r0 = DEBUG_INSTANT
            java.lang.String r12 = "["
            java.lang.String r13 = "PackageManager"
            if (r0 == 0) goto L_0x002d
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r12)
            r0.append(r11)
            java.lang.String r4 = "] Phase1; resolving"
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            android.util.Log.d(r13, r0)
        L_0x002d:
            android.content.Intent r14 = r1.origIntent
            android.content.Intent r15 = sanitizeIntent(r14)
            r16 = 0
            r17 = 0
            r10 = 2
            android.content.pm.InstantAppResolveInfo$InstantAppDigest r0 = r1.digest     // Catch:{ ConnectionException -> 0x0071 }
            int[] r0 = r0.getDigestPrefixSecure()     // Catch:{ ConnectionException -> 0x0071 }
            int r4 = r1.userId     // Catch:{ ConnectionException -> 0x0071 }
            r9 = r19
            java.util.List r0 = r9.getInstantAppResolveInfoList(r15, r0, r4, r11)     // Catch:{ ConnectionException -> 0x0071 }
            if (r0 == 0) goto L_0x006b
            int r4 = r0.size()     // Catch:{ ConnectionException -> 0x0071 }
            if (r4 <= 0) goto L_0x006b
            java.lang.String r6 = r1.resolvedType     // Catch:{ ConnectionException -> 0x0071 }
            int r7 = r1.userId     // Catch:{ ConnectionException -> 0x0071 }
            java.lang.String r8 = r14.getPackage()     // Catch:{ ConnectionException -> 0x0071 }
            android.content.pm.InstantAppResolveInfo$InstantAppDigest r5 = r1.digest     // Catch:{ ConnectionException -> 0x0071 }
            r4 = r0
            r18 = r5
            r5 = r14
            r9 = r18
            r18 = r15
            r15 = r10
            r10 = r11
            android.content.pm.AuxiliaryResolveInfo r4 = filterInstantAppIntent(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ ConnectionException -> 0x0069 }
            r16 = r4
            goto L_0x006e
        L_0x0069:
            r0 = move-exception
            goto L_0x0075
        L_0x006b:
            r18 = r15
            r15 = r10
        L_0x006e:
            r0 = r17
            goto L_0x008c
        L_0x0071:
            r0 = move-exception
            r18 = r15
            r15 = r10
        L_0x0075:
            int r4 = r0.failure
            r5 = 1
            if (r4 != r5) goto L_0x007f
            r17 = 2
            r0 = r17
            goto L_0x008c
        L_0x007f:
            int r4 = r0.failure
            if (r4 != r15) goto L_0x0088
            r17 = 3
            r0 = r17
            goto L_0x008c
        L_0x0088:
            r17 = 1
            r0 = r17
        L_0x008c:
            boolean r4 = r1.resolveForStart
            if (r4 == 0) goto L_0x0097
            if (r0 != 0) goto L_0x0097
            r4 = 899(0x383, float:1.26E-42)
            logMetrics(r4, r2, r11, r0)
        L_0x0097:
            boolean r4 = DEBUG_INSTANT
            if (r4 == 0) goto L_0x0103
            if (r16 != 0) goto L_0x0103
            if (r0 != r15) goto L_0x00b7
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r12)
            r4.append(r11)
            java.lang.String r5 = "] Phase1; bind timed out"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            android.util.Log.d(r13, r4)
            goto L_0x0103
        L_0x00b7:
            r4 = 3
            if (r0 != r4) goto L_0x00d2
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r12)
            r4.append(r11)
            java.lang.String r5 = "] Phase1; call timed out"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            android.util.Log.d(r13, r4)
            goto L_0x0103
        L_0x00d2:
            if (r0 == 0) goto L_0x00ec
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r12)
            r4.append(r11)
            java.lang.String r5 = "] Phase1; service connection error"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            android.util.Log.d(r13, r4)
            goto L_0x0103
        L_0x00ec:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r12)
            r4.append(r11)
            java.lang.String r5 = "] Phase1; No results matched"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            android.util.Log.d(r13, r4)
        L_0x0103:
            if (r16 != 0) goto L_0x0119
            int r4 = r14.getFlags()
            r4 = r4 & 2048(0x800, float:2.87E-42)
            if (r4 == 0) goto L_0x0119
            android.content.pm.AuxiliaryResolveInfo r4 = new android.content.pm.AuxiliaryResolveInfo
            r5 = 0
            android.content.Intent r6 = createFailureIntent(r14, r11)
            r7 = 0
            r4.<init>(r11, r5, r6, r7)
            return r4
        L_0x0119:
            return r16
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.InstantAppResolver.doInstantAppResolutionPhaseOne(com.android.server.pm.InstantAppResolverConnection, android.content.pm.InstantAppRequest):android.content.pm.AuxiliaryResolveInfo");
    }

    public static void doInstantAppResolutionPhaseTwo(Context context, InstantAppResolverConnection connection, InstantAppRequest requestObj, ActivityInfo instantAppInstaller, Handler callbackHandler) {
        long startTime;
        String str;
        String str2;
        String token;
        InstantAppRequest instantAppRequest = requestObj;
        long startTime2 = System.currentTimeMillis();
        String token2 = instantAppRequest.responseObj.token;
        if (DEBUG_INSTANT) {
            Log.d(TAG, "[" + token2 + "] Phase2; resolving");
        }
        Intent origIntent = instantAppRequest.origIntent;
        Intent sanitizedIntent = sanitizeIntent(origIntent);
        final Intent intent = origIntent;
        final InstantAppRequest instantAppRequest2 = requestObj;
        final String str3 = token2;
        final Intent intent2 = sanitizedIntent;
        final ActivityInfo activityInfo = instantAppInstaller;
        final Context context2 = context;
        AnonymousClass1 r1 = new InstantAppResolverConnection.PhaseTwoCallback() {
            /* access modifiers changed from: package-private */
            public void onPhaseTwoResolved(List<InstantAppResolveInfo> instantAppResolveInfoList, long startTime) {
                Intent failureIntent;
                if (instantAppResolveInfoList == null || instantAppResolveInfoList.size() <= 0) {
                    failureIntent = null;
                } else {
                    Intent intent = intent;
                    AuxiliaryResolveInfo instantAppIntentInfo = InstantAppResolver.filterInstantAppIntent(instantAppResolveInfoList, intent, (String) null, 0, intent.getPackage(), instantAppRequest2.digest, str3);
                    if (instantAppIntentInfo != null) {
                        failureIntent = instantAppIntentInfo.failureIntent;
                    } else {
                        failureIntent = null;
                    }
                }
                Intent installerIntent = InstantAppResolver.buildEphemeralInstallerIntent(instantAppRequest2.origIntent, intent2, failureIntent, instantAppRequest2.callingPackage, instantAppRequest2.verificationBundle, instantAppRequest2.resolvedType, instantAppRequest2.userId, instantAppRequest2.responseObj.installFailureActivity, str3, false, instantAppRequest2.responseObj.filters);
                installerIntent.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));
                InstantAppResolver.logMetrics(900, startTime, str3, instantAppRequest2.responseObj.filters != null ? 0 : 1);
                context2.startActivity(installerIntent);
            }
        };
        try {
            int[] digestPrefixSecure = instantAppRequest.digest.getDigestPrefixSecure();
            int i = instantAppRequest.userId;
            Intent intent3 = origIntent;
            InstantAppResolverConnection instantAppResolverConnection = connection;
            str2 = TAG;
            str = "[";
            startTime = startTime2;
            int i2 = i;
            token = token2;
            try {
                instantAppResolverConnection.getInstantAppIntentFilterList(sanitizedIntent, digestPrefixSecure, i2, token2, r1, callbackHandler, startTime);
            } catch (InstantAppResolverConnection.ConnectionException e) {
                e = e;
            }
        } catch (InstantAppResolverConnection.ConnectionException e2) {
            e = e2;
            Intent intent4 = origIntent;
            str2 = TAG;
            str = "[";
            startTime = startTime2;
            token = token2;
            int resolutionStatus = 1;
            if (e.failure == 1) {
                resolutionStatus = 2;
            }
            logMetrics(900, startTime, token, resolutionStatus);
            if (!DEBUG_INSTANT) {
                return;
            }
            if (resolutionStatus == 2) {
                Log.d(str2, str + token + "] Phase2; bind timed out");
                return;
            }
            Log.d(str2, str + token + "] Phase2; service connection error");
        }
    }

    public static Intent buildEphemeralInstallerIntent(Intent origIntent, Intent sanitizedIntent, Intent failureIntent, String callingPackage, Bundle verificationBundle, String resolvedType, int userId, ComponentName installFailureActivity, String token, boolean needsPhaseTwo, List<AuxiliaryResolveInfo.AuxiliaryFilter> filters) {
        Intent onFailureIntent;
        Intent intent = origIntent;
        Bundle bundle = verificationBundle;
        ComponentName componentName = installFailureActivity;
        String str = token;
        List<AuxiliaryResolveInfo.AuxiliaryFilter> list = filters;
        int flags = origIntent.getFlags();
        Intent intent2 = new Intent();
        intent2.setFlags(1073741824 | flags | DumpState.DUMP_VOLUMES);
        if (str != null) {
            intent2.putExtra("android.intent.extra.INSTANT_APP_TOKEN", str);
        }
        if (origIntent.getData() != null) {
            intent2.putExtra("android.intent.extra.INSTANT_APP_HOSTNAME", origIntent.getData().getHost());
        }
        intent2.putExtra("android.intent.extra.INSTANT_APP_ACTION", origIntent.getAction());
        intent2.putExtra("android.intent.extra.INTENT", sanitizedIntent);
        if (needsPhaseTwo) {
            intent2.setAction("android.intent.action.RESOLVE_INSTANT_APP_PACKAGE");
            String str2 = callingPackage;
        } else {
            if (!(failureIntent == null && componentName == null)) {
                if (componentName != null) {
                    try {
                        onFailureIntent = new Intent();
                        onFailureIntent.setComponent(componentName);
                        if (list != null && filters.size() == 1) {
                            onFailureIntent.putExtra("android.intent.extra.SPLIT_NAME", list.get(0).splitName);
                        }
                        onFailureIntent.putExtra("android.intent.extra.INTENT", intent);
                    } catch (RemoteException e) {
                    }
                } else {
                    onFailureIntent = failureIntent;
                }
                intent2.putExtra("android.intent.extra.INSTANT_APP_FAILURE", new IntentSender(ActivityManager.getService().getIntentSender(2, callingPackage, (IBinder) null, (String) null, 1, new Intent[]{onFailureIntent}, new String[]{resolvedType}, 1409286144, (Bundle) null, userId)));
            }
            Intent successIntent = new Intent(intent);
            successIntent.setLaunchToken(str);
            try {
                intent2.putExtra("android.intent.extra.INSTANT_APP_SUCCESS", new IntentSender(ActivityManager.getService().getIntentSender(2, callingPackage, (IBinder) null, (String) null, 0, new Intent[]{successIntent}, new String[]{resolvedType}, 1409286144, (Bundle) null, userId)));
            } catch (RemoteException e2) {
            }
            if (bundle != null) {
                intent2.putExtra("android.intent.extra.VERIFICATION_BUNDLE", bundle);
            }
            intent2.putExtra("android.intent.extra.CALLING_PACKAGE", callingPackage);
            if (list != null) {
                Bundle[] resolvableFilters = new Bundle[filters.size()];
                int i = 0;
                int max = filters.size();
                while (i < max) {
                    Bundle resolvableFilter = new Bundle();
                    AuxiliaryResolveInfo.AuxiliaryFilter filter = list.get(i);
                    Bundle resolvableFilter2 = resolvableFilter;
                    resolvableFilter2.putBoolean("android.intent.extra.UNKNOWN_INSTANT_APP", filter.resolveInfo != null && filter.resolveInfo.shouldLetInstallerDecide());
                    resolvableFilter2.putString("android.intent.extra.PACKAGE_NAME", filter.packageName);
                    resolvableFilter2.putString("android.intent.extra.SPLIT_NAME", filter.splitName);
                    resolvableFilter2.putLong("android.intent.extra.LONG_VERSION_CODE", filter.versionCode);
                    resolvableFilter2.putBundle("android.intent.extra.INSTANT_APP_EXTRAS", filter.extras);
                    resolvableFilters[i] = resolvableFilter2;
                    if (i == 0) {
                        intent2.putExtras(resolvableFilter2);
                        intent2.putExtra("android.intent.extra.VERSION_CODE", (int) filter.versionCode);
                    }
                    i++;
                    Intent intent3 = origIntent;
                    Bundle bundle2 = verificationBundle;
                    ComponentName componentName2 = installFailureActivity;
                    String str3 = token;
                }
                intent2.putExtra("android.intent.extra.INSTANT_APP_BUNDLES", resolvableFilters);
            }
            intent2.setAction("android.intent.action.INSTALL_INSTANT_APP_PACKAGE");
        }
        return intent2;
    }

    /* access modifiers changed from: private */
    public static AuxiliaryResolveInfo filterInstantAppIntent(List<InstantAppResolveInfo> instantAppResolveInfoList, Intent origIntent, String resolvedType, int userId, String packageName, InstantAppResolveInfo.InstantAppDigest digest, String token) {
        String str = token;
        int[] shaPrefix = digest.getDigestPrefix();
        byte[][] digestBytes = digest.getDigestBytes();
        boolean requiresPrefixMatch = origIntent.isWebIntent() || (shaPrefix.length > 0 && (origIntent.getFlags() & 2048) == 0);
        boolean requiresSecondPhase = false;
        ArrayList<AuxiliaryResolveInfo.AuxiliaryFilter> filters = null;
        for (InstantAppResolveInfo instantAppResolveInfo : instantAppResolveInfoList) {
            if (!requiresPrefixMatch || !instantAppResolveInfo.shouldLetInstallerDecide()) {
                byte[] filterDigestBytes = instantAppResolveInfo.getDigestBytes();
                if (shaPrefix.length > 0 && (requiresPrefixMatch || filterDigestBytes.length > 0)) {
                    boolean matchFound = false;
                    int i = shaPrefix.length - 1;
                    while (true) {
                        if (i < 0) {
                            break;
                        } else if (Arrays.equals(digestBytes[i], filterDigestBytes)) {
                            matchFound = true;
                            break;
                        } else {
                            i--;
                        }
                    }
                    if (!matchFound) {
                    }
                }
                List<AuxiliaryResolveInfo.AuxiliaryFilter> matchFilters = computeResolveFilters(origIntent, resolvedType, userId, packageName, token, instantAppResolveInfo);
                if (matchFilters != null) {
                    if (matchFilters.isEmpty()) {
                        requiresSecondPhase = true;
                    }
                    if (filters == null) {
                        filters = new ArrayList<>(matchFilters);
                    } else {
                        filters.addAll(matchFilters);
                    }
                }
            } else {
                Slog.d(TAG, "InstantAppResolveInfo with mShouldLetInstallerDecide=true when digest required; ignoring");
            }
        }
        if (filters != null && !filters.isEmpty()) {
            return new AuxiliaryResolveInfo(str, requiresSecondPhase, createFailureIntent(origIntent, str), filters);
        }
        Intent intent = origIntent;
        return null;
    }

    private static Intent createFailureIntent(Intent origIntent, String token) {
        Intent failureIntent = new Intent(origIntent);
        failureIntent.setFlags(failureIntent.getFlags() | 512);
        failureIntent.setFlags(failureIntent.getFlags() & -2049);
        failureIntent.setLaunchToken(token);
        return failureIntent;
    }

    private static List<AuxiliaryResolveInfo.AuxiliaryFilter> computeResolveFilters(Intent origIntent, String resolvedType, int userId, String packageName, String token, InstantAppResolveInfo instantAppInfo) {
        String str = packageName;
        String str2 = token;
        InstantAppResolveInfo instantAppResolveInfo = instantAppInfo;
        if (instantAppInfo.shouldLetInstallerDecide()) {
            return Collections.singletonList(new AuxiliaryResolveInfo.AuxiliaryFilter(instantAppResolveInfo, (String) null, instantAppInfo.getExtras()));
        }
        if (str != null && !str.equals(instantAppInfo.getPackageName())) {
            return null;
        }
        List<InstantAppIntentFilter> instantAppFilters = instantAppInfo.getIntentFilters();
        if (instantAppFilters == null) {
            Intent intent = origIntent;
            String str3 = resolvedType;
            int i = userId;
        } else if (instantAppFilters.isEmpty()) {
            Intent intent2 = origIntent;
            String str4 = resolvedType;
            int i2 = userId;
        } else {
            ComponentResolver.InstantAppIntentResolver instantAppResolver = new ComponentResolver.InstantAppIntentResolver();
            for (int j = instantAppFilters.size() - 1; j >= 0; j--) {
                InstantAppIntentFilter instantAppFilter = instantAppFilters.get(j);
                List<IntentFilter> splitFilters = instantAppFilter.getFilters();
                if (splitFilters != null && !splitFilters.isEmpty()) {
                    for (int k = splitFilters.size() - 1; k >= 0; k--) {
                        IntentFilter filter = splitFilters.get(k);
                        Iterator<IntentFilter.AuthorityEntry> authorities = filter.authoritiesIterator();
                        if ((authorities != null && authorities.hasNext()) || ((!filter.hasDataScheme("http") && !filter.hasDataScheme("https")) || !filter.hasAction("android.intent.action.VIEW") || !filter.hasCategory("android.intent.category.BROWSABLE"))) {
                            instantAppResolver.addFilter(new AuxiliaryResolveInfo.AuxiliaryFilter(filter, instantAppResolveInfo, instantAppFilter.getSplitName(), instantAppInfo.getExtras()));
                        }
                    }
                }
            }
            List<AuxiliaryResolveInfo.AuxiliaryFilter> matchedResolveInfoList = instantAppResolver.queryIntent(origIntent, resolvedType, false, userId);
            if (!matchedResolveInfoList.isEmpty()) {
                if (DEBUG_INSTANT) {
                    Log.d(TAG, "[" + str2 + "] Found match(es); " + matchedResolveInfoList);
                }
                return matchedResolveInfoList;
            }
            if (DEBUG_INSTANT) {
                Log.d(TAG, "[" + str2 + "] No matches found package: " + instantAppInfo.getPackageName() + ", versionCode: " + instantAppInfo.getVersionCode());
            }
            return null;
        }
        if (origIntent.isWebIntent()) {
            return null;
        }
        if (DEBUG_INSTANT) {
            Log.d(TAG, "No app filters; go to phase 2");
        }
        return Collections.emptyList();
    }

    /* access modifiers changed from: private */
    public static void logMetrics(int action, long startTime, String token, int status) {
        getLogger().write(new LogMaker(action).setType(4).addTaggedData(901, new Long(System.currentTimeMillis() - startTime)).addTaggedData(903, token).addTaggedData(902, new Integer(status)));
    }
}
