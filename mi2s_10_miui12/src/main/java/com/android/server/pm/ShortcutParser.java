package com.android.server.pm;

import android.app.Person;
import android.content.ComponentName;
import android.content.Intent;
import android.content.LocusId;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.res.TypedArray;
import android.graphics.drawable.Icon;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import com.android.internal.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.pm.ShareTargetInfo;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;

public class ShortcutParser {
    private static final boolean DEBUG = false;
    @VisibleForTesting
    static final String METADATA_KEY = "android.app.shortcuts";
    private static final String TAG = "ShortcutService";
    private static final String TAG_CATEGORIES = "categories";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_DATA = "data";
    private static final String TAG_INTENT = "intent";
    private static final String TAG_SHARE_TARGET = "share-target";
    private static final String TAG_SHORTCUT = "shortcut";
    private static final String TAG_SHORTCUTS = "shortcuts";

    public static List<ShortcutInfo> parseShortcuts(ShortcutService service, String packageName, int userId, List<ShareTargetInfo> outShareTargets) throws IOException, XmlPullParserException {
        ShortcutService shortcutService = service;
        List<ResolveInfo> activities = service.injectGetMainActivities(packageName, userId);
        if (activities == null) {
            String str = packageName;
            int i = userId;
        } else if (activities.size() == 0) {
            String str2 = packageName;
            int i2 = userId;
        } else {
            outShareTargets.clear();
            try {
                int size = activities.size();
                List<ShortcutInfo> result = null;
                int i3 = 0;
                while (i3 < size) {
                    try {
                        ActivityInfo activityInfoNoMetadata = activities.get(i3).activityInfo;
                        if (activityInfoNoMetadata == null) {
                            int i4 = userId;
                        } else {
                            try {
                                ActivityInfo activityInfoWithMetadata = service.getActivityInfoWithMetadata(activityInfoNoMetadata.getComponentName(), userId);
                                if (activityInfoWithMetadata != null) {
                                    result = parseShortcutsOneFile(service, activityInfoWithMetadata, packageName, userId, result, outShareTargets);
                                }
                            } catch (RuntimeException e) {
                                e = e;
                                service.wtf("Exception caught while parsing shortcut XML for package=" + packageName, e);
                                return null;
                            }
                        }
                        i3++;
                    } catch (RuntimeException e2) {
                        e = e2;
                        int i5 = userId;
                        service.wtf("Exception caught while parsing shortcut XML for package=" + packageName, e);
                        return null;
                    }
                }
                int i6 = userId;
                return result;
            } catch (RuntimeException e3) {
                e = e3;
                int i7 = userId;
                service.wtf("Exception caught while parsing shortcut XML for package=" + packageName, e);
                return null;
            }
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:203:0x0437  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01f8 A[Catch:{ all -> 0x01e7, all -> 0x0214 }] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0200 A[Catch:{ all -> 0x01e7, all -> 0x0214 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.util.List<android.content.pm.ShortcutInfo> parseShortcutsOneFile(com.android.server.pm.ShortcutService r24, android.content.pm.ActivityInfo r25, java.lang.String r26, int r27, java.util.List<android.content.pm.ShortcutInfo> r28, java.util.List<com.android.server.pm.ShareTargetInfo> r29) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
        /*
            r7 = r24
            r8 = r25
            r1 = 0
            java.lang.String r0 = "android.app.shortcuts"
            android.content.res.XmlResourceParser r0 = r7.injectXmlMetaData(r8, r0)     // Catch:{ all -> 0x042e }
            r9 = r0
            if (r9 != 0) goto L_0x0015
            if (r9 == 0) goto L_0x0014
            r9.close()
        L_0x0014:
            return r28
        L_0x0015:
            android.content.ComponentName r0 = new android.content.ComponentName     // Catch:{ all -> 0x0427 }
            java.lang.String r1 = r8.name     // Catch:{ all -> 0x0427 }
            r10 = r26
            r0.<init>(r10, r1)     // Catch:{ all -> 0x0427 }
            r11 = r0
            android.util.AttributeSet r0 = android.util.Xml.asAttributeSet(r9)     // Catch:{ all -> 0x0427 }
            r12 = r0
            r0 = 0
            int r1 = r24.getMaxActivityShortcuts()     // Catch:{ all -> 0x0427 }
            r13 = r1
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x0427 }
            r5.<init>()     // Catch:{ all -> 0x0427 }
            r14 = r5
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x0427 }
            r5.<init>()     // Catch:{ all -> 0x0427 }
            r15 = r5
            r5 = r28
            r16 = r0
            r6 = r1
            r0 = r2
        L_0x0040:
            int r1 = r9.next()     // Catch:{ all -> 0x0422 }
            r2 = r1
            r8 = 1
            if (r1 == r8) goto L_0x0413
            r1 = 3
            if (r2 != r1) goto L_0x0057
            int r17 = r9.getDepth()     // Catch:{ all -> 0x0422 }
            if (r17 <= 0) goto L_0x0052
            goto L_0x0057
        L_0x0052:
            r13 = r29
            r2 = r7
            goto L_0x041d
        L_0x0057:
            int r17 = r9.getDepth()     // Catch:{ all -> 0x0422 }
            r28 = r17
            java.lang.String r17 = r9.getName()     // Catch:{ all -> 0x0422 }
            r18 = r17
            java.lang.String r8 = "shortcut"
            java.lang.String r10 = "ShortcutService"
            if (r2 != r1) goto L_0x0152
            r1 = r28
            r7 = 2
            if (r1 != r7) goto L_0x014d
            r7 = r18
            boolean r18 = r8.equals(r7)     // Catch:{ all -> 0x0146 }
            if (r18 == 0) goto L_0x0143
            if (r0 != 0) goto L_0x007f
            r28 = r5
            r18 = r11
            goto L_0x016c
        L_0x007f:
            r8 = r0
            r17 = 0
            boolean r0 = r8.isEnabled()     // Catch:{ all -> 0x0146 }
            if (r0 == 0) goto L_0x00b2
            int r0 = r14.size()     // Catch:{ all -> 0x0146 }
            if (r0 != 0) goto L_0x00af
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0146 }
            r0.<init>()     // Catch:{ all -> 0x0146 }
            r18 = r11
            java.lang.String r11 = "Shortcut "
            r0.append(r11)     // Catch:{ all -> 0x0146 }
            java.lang.String r11 = r8.getId()     // Catch:{ all -> 0x0146 }
            r0.append(r11)     // Catch:{ all -> 0x0146 }
            java.lang.String r11 = " has no intent. Skipping it."
            r0.append(r11)     // Catch:{ all -> 0x0146 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0146 }
            android.util.Log.e(r10, r0)     // Catch:{ all -> 0x0146 }
            goto L_0x0137
        L_0x00af:
            r18 = r11
            goto L_0x00c1
        L_0x00b2:
            r18 = r11
            r14.clear()     // Catch:{ all -> 0x0146 }
            android.content.Intent r0 = new android.content.Intent     // Catch:{ all -> 0x0146 }
            java.lang.String r11 = "android.intent.action.VIEW"
            r0.<init>(r11)     // Catch:{ all -> 0x0146 }
            r14.add(r0)     // Catch:{ all -> 0x0146 }
        L_0x00c1:
            if (r6 < r13) goto L_0x00ee
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0146 }
            r0.<init>()     // Catch:{ all -> 0x0146 }
            java.lang.String r11 = "More than "
            r0.append(r11)     // Catch:{ all -> 0x0146 }
            r0.append(r13)     // Catch:{ all -> 0x0146 }
            java.lang.String r11 = " shortcuts found for "
            r0.append(r11)     // Catch:{ all -> 0x0146 }
            android.content.ComponentName r11 = r25.getComponentName()     // Catch:{ all -> 0x0146 }
            r0.append(r11)     // Catch:{ all -> 0x0146 }
            java.lang.String r11 = ". Skipping the rest."
            r0.append(r11)     // Catch:{ all -> 0x0146 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0146 }
            android.util.Log.e(r10, r0)     // Catch:{ all -> 0x0146 }
            r9.close()
            return r5
        L_0x00ee:
            r0 = 0
            java.lang.Object r0 = r14.get(r0)     // Catch:{ all -> 0x0146 }
            android.content.Intent r0 = (android.content.Intent) r0     // Catch:{ all -> 0x0146 }
            r11 = 268484608(0x1000c000, float:2.539146E-29)
            r0.addFlags(r11)     // Catch:{ all -> 0x0146 }
            int r0 = r14.size()     // Catch:{ RuntimeException -> 0x0130 }
            android.content.Intent[] r0 = new android.content.Intent[r0]     // Catch:{ RuntimeException -> 0x0130 }
            java.lang.Object[] r0 = r14.toArray(r0)     // Catch:{ RuntimeException -> 0x0130 }
            android.content.Intent[] r0 = (android.content.Intent[]) r0     // Catch:{ RuntimeException -> 0x0130 }
            r8.setIntents(r0)     // Catch:{ RuntimeException -> 0x0130 }
            r14.clear()     // Catch:{ all -> 0x0146 }
            if (r4 == 0) goto L_0x0115
            r8.setCategories(r4)     // Catch:{ all -> 0x0146 }
            r0 = 0
            r4 = r0
        L_0x0115:
            if (r5 != 0) goto L_0x011d
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x0146 }
            r0.<init>()     // Catch:{ all -> 0x0146 }
            r5 = r0
        L_0x011d:
            r5.add(r8)     // Catch:{ all -> 0x0146 }
            int r6 = r6 + 1
            int r16 = r16 + 1
            r7 = r24
            r8 = r25
            r10 = r26
            r0 = r17
            r11 = r18
            goto L_0x0040
        L_0x0130:
            r0 = move-exception
            java.lang.String r11 = "Shortcut's extras contain un-persistable values. Skipping it."
            android.util.Log.e(r10, r11)     // Catch:{ all -> 0x0146 }
        L_0x0137:
            r7 = r24
            r8 = r25
            r10 = r26
            r0 = r17
            r11 = r18
            goto L_0x0040
        L_0x0143:
            r18 = r11
            goto L_0x0158
        L_0x0146:
            r0 = move-exception
            r2 = r24
            r13 = r29
            goto L_0x0435
        L_0x014d:
            r7 = r18
            r18 = r11
            goto L_0x0158
        L_0x0152:
            r1 = r28
            r7 = r18
            r18 = r11
        L_0x0158:
            java.lang.String r11 = "share-target"
            r28 = r5
            r5 = 3
            if (r2 != r5) goto L_0x01eb
            r5 = 2
            if (r1 != r5) goto L_0x01eb
            boolean r5 = r11.equals(r7)     // Catch:{ all -> 0x01e7 }
            if (r5 == 0) goto L_0x01e0
            if (r3 != 0) goto L_0x0178
        L_0x016c:
            r7 = r24
            r8 = r25
            r10 = r26
            r5 = r28
            r11 = r18
            goto L_0x0040
        L_0x0178:
            r5 = r3
            r3 = 0
            if (r4 == 0) goto L_0x01c4
            boolean r19 = r4.isEmpty()     // Catch:{ all -> 0x01e7 }
            if (r19 != 0) goto L_0x01c4
            boolean r19 = r15.isEmpty()     // Catch:{ all -> 0x01e7 }
            if (r19 == 0) goto L_0x0191
            r19 = r3
            r20 = r6
            r21 = r13
            r13 = r29
            goto L_0x01ce
        L_0x0191:
            r19 = r3
            com.android.server.pm.ShareTargetInfo r3 = new com.android.server.pm.ShareTargetInfo     // Catch:{ all -> 0x01e7 }
            r20 = r6
            int r6 = r15.size()     // Catch:{ all -> 0x01e7 }
            com.android.server.pm.ShareTargetInfo$TargetData[] r6 = new com.android.server.pm.ShareTargetInfo.TargetData[r6]     // Catch:{ all -> 0x01e7 }
            java.lang.Object[] r6 = r15.toArray(r6)     // Catch:{ all -> 0x01e7 }
            com.android.server.pm.ShareTargetInfo$TargetData[] r6 = (com.android.server.pm.ShareTargetInfo.TargetData[]) r6     // Catch:{ all -> 0x01e7 }
            r21 = r13
            java.lang.String r13 = r5.mTargetClass     // Catch:{ all -> 0x01e7 }
            r22 = r5
            int r5 = r4.size()     // Catch:{ all -> 0x01e7 }
            java.lang.String[] r5 = new java.lang.String[r5]     // Catch:{ all -> 0x01e7 }
            java.lang.Object[] r5 = r4.toArray(r5)     // Catch:{ all -> 0x01e7 }
            java.lang.String[] r5 = (java.lang.String[]) r5     // Catch:{ all -> 0x01e7 }
            r3.<init>(r6, r13, r5)     // Catch:{ all -> 0x01e7 }
            r13 = r29
            r13.add(r3)     // Catch:{ all -> 0x0214 }
            r4 = 0
            r15.clear()     // Catch:{ all -> 0x0214 }
            r22 = r4
            goto L_0x01f5
        L_0x01c4:
            r19 = r3
            r22 = r5
            r20 = r6
            r21 = r13
            r13 = r29
        L_0x01ce:
            r7 = r24
            r8 = r25
            r10 = r26
            r5 = r28
            r11 = r18
            r3 = r19
            r6 = r20
            r13 = r21
            goto L_0x0040
        L_0x01e0:
            r20 = r6
            r21 = r13
            r13 = r29
            goto L_0x01f1
        L_0x01e7:
            r0 = move-exception
            r13 = r29
            goto L_0x0215
        L_0x01eb:
            r20 = r6
            r21 = r13
            r13 = r29
        L_0x01f1:
            r19 = r3
            r22 = r4
        L_0x01f5:
            r3 = 2
            if (r2 == r3) goto L_0x0200
            r2 = r24
            r5 = r28
            r6 = r18
            goto L_0x0403
        L_0x0200:
            r3 = 1
            if (r1 != r3) goto L_0x0219
            java.lang.String r3 = "shortcuts"
            boolean r3 = r3.equals(r7)     // Catch:{ all -> 0x0214 }
            if (r3 == 0) goto L_0x0219
            r2 = r24
            r5 = r28
            r6 = r18
            goto L_0x0403
        L_0x0214:
            r0 = move-exception
        L_0x0215:
            r2 = r24
            goto L_0x042b
        L_0x0219:
            r3 = 2
            if (r1 != r3) goto L_0x028c
            boolean r3 = r8.equals(r7)     // Catch:{ all -> 0x0285 }
            if (r3 == 0) goto L_0x028c
            r8 = r1
            r1 = r24
            r23 = r2
            r2 = r12
            r3 = r26
            r4 = r18
            r11 = r28
            r5 = r27
            r6 = r16
            android.content.pm.ShortcutInfo r1 = parseShortcutAttributes(r1, r2, r3, r4, r5, r6)     // Catch:{ all -> 0x027f }
            if (r1 != 0) goto L_0x023f
            r2 = r24
            r5 = r11
            r6 = r18
            goto L_0x0403
        L_0x023f:
            if (r11 == 0) goto L_0x026c
            int r2 = r11.size()     // Catch:{ all -> 0x027f }
            r3 = 1
            int r2 = r2 - r3
        L_0x0247:
            if (r2 < 0) goto L_0x026c
            java.lang.String r3 = r1.getId()     // Catch:{ all -> 0x027f }
            java.lang.Object r4 = r11.get(r2)     // Catch:{ all -> 0x027f }
            android.content.pm.ShortcutInfo r4 = (android.content.pm.ShortcutInfo) r4     // Catch:{ all -> 0x027f }
            java.lang.String r4 = r4.getId()     // Catch:{ all -> 0x027f }
            boolean r3 = r3.equals(r4)     // Catch:{ all -> 0x027f }
            if (r3 == 0) goto L_0x0269
            java.lang.String r3 = "Duplicate shortcut ID detected. Skipping it."
            android.util.Log.e(r10, r3)     // Catch:{ all -> 0x027f }
            r2 = r24
            r5 = r11
            r6 = r18
            goto L_0x0403
        L_0x0269:
            int r2 = r2 + -1
            goto L_0x0247
        L_0x026c:
            r0 = r1
            r4 = 0
            r7 = r24
            r8 = r25
            r10 = r26
            r5 = r11
            r11 = r18
            r3 = r19
            r6 = r20
            r13 = r21
            goto L_0x0040
        L_0x027f:
            r0 = move-exception
            r2 = r24
            r5 = r11
            goto L_0x0435
        L_0x0285:
            r0 = move-exception
            r5 = r28
            r2 = r24
            goto L_0x0435
        L_0x028c:
            r5 = r28
            r8 = r1
            r23 = r2
            r1 = 2
            if (r8 != r1) goto L_0x02bd
            boolean r1 = r11.equals(r7)     // Catch:{ all -> 0x02b8 }
            if (r1 == 0) goto L_0x02bd
            r2 = r24
            com.android.server.pm.ShareTargetInfo r1 = parseShareTargetAttributes(r2, r12)     // Catch:{ all -> 0x0321 }
            if (r1 != 0) goto L_0x02a6
            r6 = r18
            goto L_0x0403
        L_0x02a6:
            r3 = r1
            r4 = 0
            r15.clear()     // Catch:{ all -> 0x0321 }
            r8 = r25
            r10 = r26
            r7 = r2
            r11 = r18
            r6 = r20
            r13 = r21
            goto L_0x0040
        L_0x02b8:
            r0 = move-exception
            r2 = r24
            goto L_0x0435
        L_0x02bd:
            r2 = r24
            r1 = 3
            if (r8 != r1) goto L_0x0324
            java.lang.String r1 = "intent"
            boolean r1 = r1.equals(r7)     // Catch:{ all -> 0x0321 }
            if (r1 == 0) goto L_0x0324
            if (r0 == 0) goto L_0x0318
            boolean r1 = r0.isEnabled()     // Catch:{ all -> 0x0321 }
            if (r1 != 0) goto L_0x02d6
            r6 = r18
            goto L_0x031a
        L_0x02d6:
            android.content.Context r1 = r2.mContext     // Catch:{ all -> 0x0321 }
            android.content.res.Resources r1 = r1.getResources()     // Catch:{ all -> 0x0321 }
            android.content.Intent r1 = android.content.Intent.parseIntent(r1, r9, r12)     // Catch:{ all -> 0x0321 }
            java.lang.String r3 = r1.getAction()     // Catch:{ all -> 0x0321 }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0321 }
            if (r3 == 0) goto L_0x0311
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0321 }
            r3.<init>()     // Catch:{ all -> 0x0321 }
            java.lang.String r4 = "Shortcut intent action must be provided. activity="
            r3.append(r4)     // Catch:{ all -> 0x0321 }
            r6 = r18
            r3.append(r6)     // Catch:{ all -> 0x0321 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0321 }
            android.util.Log.e(r10, r3)     // Catch:{ all -> 0x0321 }
            r0 = 0
            r8 = r25
            r10 = r26
            r7 = r2
            r11 = r6
            r3 = r19
            r6 = r20
            r13 = r21
            r4 = r22
            goto L_0x0040
        L_0x0311:
            r6 = r18
            r14.add(r1)     // Catch:{ all -> 0x0321 }
            goto L_0x0403
        L_0x0318:
            r6 = r18
        L_0x031a:
            java.lang.String r1 = "Ignoring excessive intent tag."
            android.util.Log.e(r10, r1)     // Catch:{ all -> 0x0321 }
            goto L_0x0403
        L_0x0321:
            r0 = move-exception
            goto L_0x0435
        L_0x0324:
            r6 = r18
            java.lang.String r1 = "Empty category found. activity="
            r3 = 3
            if (r8 != r3) goto L_0x0377
            java.lang.String r3 = "categories"
            boolean r3 = r3.equals(r7)     // Catch:{ all -> 0x0321 }
            if (r3 == 0) goto L_0x0377
            if (r0 == 0) goto L_0x0403
            java.util.Set r3 = r0.getCategories()     // Catch:{ all -> 0x0321 }
            if (r3 == 0) goto L_0x033d
            goto L_0x0403
        L_0x033d:
            java.lang.String r3 = parseCategories(r2, r12)     // Catch:{ all -> 0x0321 }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0321 }
            if (r4 == 0) goto L_0x035b
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0321 }
            r4.<init>()     // Catch:{ all -> 0x0321 }
            r4.append(r1)     // Catch:{ all -> 0x0321 }
            r4.append(r6)     // Catch:{ all -> 0x0321 }
            java.lang.String r1 = r4.toString()     // Catch:{ all -> 0x0321 }
            android.util.Log.e(r10, r1)     // Catch:{ all -> 0x0321 }
            goto L_0x0403
        L_0x035b:
            if (r22 != 0) goto L_0x0364
            android.util.ArraySet r1 = new android.util.ArraySet     // Catch:{ all -> 0x0321 }
            r1.<init>()     // Catch:{ all -> 0x0321 }
            r4 = r1
            goto L_0x0366
        L_0x0364:
            r4 = r22
        L_0x0366:
            r4.add(r3)     // Catch:{ all -> 0x0321 }
            r8 = r25
            r10 = r26
            r7 = r2
            r11 = r6
            r3 = r19
            r6 = r20
            r13 = r21
            goto L_0x0040
        L_0x0377:
            r3 = 3
            if (r8 != r3) goto L_0x03bf
            java.lang.String r3 = "category"
            boolean r3 = r3.equals(r7)     // Catch:{ all -> 0x0321 }
            if (r3 == 0) goto L_0x03bf
            if (r19 != 0) goto L_0x0386
            goto L_0x0403
        L_0x0386:
            java.lang.String r3 = parseCategory(r2, r12)     // Catch:{ all -> 0x0321 }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0321 }
            if (r4 == 0) goto L_0x03a3
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0321 }
            r4.<init>()     // Catch:{ all -> 0x0321 }
            r4.append(r1)     // Catch:{ all -> 0x0321 }
            r4.append(r6)     // Catch:{ all -> 0x0321 }
            java.lang.String r1 = r4.toString()     // Catch:{ all -> 0x0321 }
            android.util.Log.e(r10, r1)     // Catch:{ all -> 0x0321 }
            goto L_0x0403
        L_0x03a3:
            if (r22 != 0) goto L_0x03ac
            android.util.ArraySet r1 = new android.util.ArraySet     // Catch:{ all -> 0x0321 }
            r1.<init>()     // Catch:{ all -> 0x0321 }
            r4 = r1
            goto L_0x03ae
        L_0x03ac:
            r4 = r22
        L_0x03ae:
            r4.add(r3)     // Catch:{ all -> 0x0321 }
            r8 = r25
            r10 = r26
            r7 = r2
            r11 = r6
            r3 = r19
            r6 = r20
            r13 = r21
            goto L_0x0040
        L_0x03bf:
            r1 = 3
            if (r8 != r1) goto L_0x03ec
            java.lang.String r1 = "data"
            boolean r1 = r1.equals(r7)     // Catch:{ all -> 0x0321 }
            if (r1 == 0) goto L_0x03ec
            if (r19 != 0) goto L_0x03cd
            goto L_0x0403
        L_0x03cd:
            com.android.server.pm.ShareTargetInfo$TargetData r1 = parseShareTargetData(r2, r12)     // Catch:{ all -> 0x0321 }
            if (r1 != 0) goto L_0x03e8
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0321 }
            r3.<init>()     // Catch:{ all -> 0x0321 }
            java.lang.String r4 = "Invalid data tag found. activity="
            r3.append(r4)     // Catch:{ all -> 0x0321 }
            r3.append(r6)     // Catch:{ all -> 0x0321 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0321 }
            android.util.Log.e(r10, r3)     // Catch:{ all -> 0x0321 }
            goto L_0x0403
        L_0x03e8:
            r15.add(r1)     // Catch:{ all -> 0x0321 }
            goto L_0x0403
        L_0x03ec:
            java.lang.String r1 = "Invalid tag '%s' found at depth %d"
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0321 }
            r4 = 0
            r3[r4] = r7     // Catch:{ all -> 0x0321 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r8)     // Catch:{ all -> 0x0321 }
            r11 = 1
            r3[r11] = r4     // Catch:{ all -> 0x0321 }
            java.lang.String r1 = java.lang.String.format(r1, r3)     // Catch:{ all -> 0x0321 }
            android.util.Log.w(r10, r1)     // Catch:{ all -> 0x0321 }
        L_0x0403:
            r8 = r25
            r10 = r26
            r7 = r2
            r11 = r6
            r3 = r19
            r6 = r20
            r13 = r21
            r4 = r22
            goto L_0x0040
        L_0x0413:
            r23 = r2
            r20 = r6
            r2 = r7
            r6 = r11
            r21 = r13
            r13 = r29
        L_0x041d:
            r9.close()
            return r5
        L_0x0422:
            r0 = move-exception
            r13 = r29
            r2 = r7
            goto L_0x0435
        L_0x0427:
            r0 = move-exception
            r13 = r29
            r2 = r7
        L_0x042b:
            r5 = r28
            goto L_0x0435
        L_0x042e:
            r0 = move-exception
            r13 = r29
            r2 = r7
            r5 = r28
            r9 = r1
        L_0x0435:
            if (r9 == 0) goto L_0x043a
            r9.close()
        L_0x043a:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.ShortcutParser.parseShortcutsOneFile(com.android.server.pm.ShortcutService, android.content.pm.ActivityInfo, java.lang.String, int, java.util.List, java.util.List):java.util.List");
    }

    private static String parseCategories(ShortcutService service, AttributeSet attrs) {
        TypedArray sa = service.mContext.getResources().obtainAttributes(attrs, R.styleable.ShortcutCategories);
        try {
            if (sa.getType(0) == 3) {
                return sa.getNonResourceString(0);
            }
            Log.w(TAG, "android:name for shortcut category must be string literal.");
            sa.recycle();
            return null;
        } finally {
            sa.recycle();
        }
    }

    private static ShortcutInfo parseShortcutAttributes(ShortcutService service, AttributeSet attrs, String packageName, ComponentName activity, int userId, int rank) {
        ComponentName componentName = activity;
        TypedArray sa = service.mContext.getResources().obtainAttributes(attrs, R.styleable.Shortcut);
        try {
            if (sa.getType(2) != 3) {
                Log.w(TAG, "android:shortcutId must be string literal. activity=" + componentName);
                return null;
            }
            String id = sa.getNonResourceString(2);
            boolean enabled = sa.getBoolean(1, true);
            int iconResId = sa.getResourceId(0, 0);
            int titleResId = sa.getResourceId(3, 0);
            int textResId = sa.getResourceId(4, 0);
            int disabledMessageResId = sa.getResourceId(5, 0);
            if (TextUtils.isEmpty(id)) {
                Log.w(TAG, "android:shortcutId must be provided. activity=" + componentName);
                sa.recycle();
                return null;
            } else if (titleResId == 0) {
                Log.w(TAG, "android:shortcutShortLabel must be provided. activity=" + componentName);
                sa.recycle();
                return null;
            } else {
                ShortcutInfo createShortcutFromManifest = createShortcutFromManifest(service, userId, id, packageName, activity, titleResId, textResId, disabledMessageResId, rank, iconResId, enabled);
                sa.recycle();
                return createShortcutFromManifest;
            }
        } finally {
            sa.recycle();
        }
    }

    private static ShortcutInfo createShortcutFromManifest(ShortcutService service, int userId, String id, String packageName, ComponentName activityComponent, int titleResId, int textResId, int disabledMessageResId, int rank, int iconResId, boolean enabled) {
        int disabledReason;
        int flags = (enabled ? 32 : 64) | 256 | (iconResId != 0 ? 4 : 0);
        if (enabled) {
            disabledReason = 0;
        } else {
            disabledReason = 1;
        }
        return new ShortcutInfo(userId, id, packageName, activityComponent, (Icon) null, (CharSequence) null, titleResId, (String) null, (CharSequence) null, textResId, (String) null, (CharSequence) null, disabledMessageResId, (String) null, (Set) null, (Intent[]) null, rank, (PersistableBundle) null, service.injectCurrentTimeMillis(), flags, iconResId, (String) null, (String) null, disabledReason, (Person[]) null, (LocusId) null);
    }

    private static String parseCategory(ShortcutService service, AttributeSet attrs) {
        TypedArray sa = service.mContext.getResources().obtainAttributes(attrs, R.styleable.IntentCategory);
        try {
            if (sa.getType(0) != 3) {
                Log.w(TAG, "android:name must be string literal.");
                return null;
            }
            String string = sa.getString(0);
            sa.recycle();
            return string;
        } finally {
            sa.recycle();
        }
    }

    private static ShareTargetInfo parseShareTargetAttributes(ShortcutService service, AttributeSet attrs) {
        TypedArray sa = service.mContext.getResources().obtainAttributes(attrs, R.styleable.Intent);
        try {
            String targetClass = sa.getString(4);
            if (TextUtils.isEmpty(targetClass)) {
                Log.w(TAG, "android:targetClass must be provided.");
                return null;
            }
            ShareTargetInfo shareTargetInfo = new ShareTargetInfo((ShareTargetInfo.TargetData[]) null, targetClass, (String[]) null);
            sa.recycle();
            return shareTargetInfo;
        } finally {
            sa.recycle();
        }
    }

    private static ShareTargetInfo.TargetData parseShareTargetData(ShortcutService service, AttributeSet attrs) {
        TypedArray sa = service.mContext.getResources().obtainAttributes(attrs, R.styleable.AndroidManifestData);
        try {
            if (sa.getType(0) != 3) {
                Log.w(TAG, "android:mimeType must be string literal.");
                return null;
            }
            ShareTargetInfo.TargetData targetData = new ShareTargetInfo.TargetData(sa.getString(1), sa.getString(2), sa.getString(3), sa.getString(4), sa.getString(6), sa.getString(5), sa.getString(0));
            sa.recycle();
            return targetData;
        } finally {
            sa.recycle();
        }
    }
}
