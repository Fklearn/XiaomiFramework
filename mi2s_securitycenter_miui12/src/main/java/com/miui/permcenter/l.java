package com.miui.permcenter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.b.b;
import b.b.c.b.c;
import b.b.c.j.B;
import b.b.c.j.r;
import b.b.c.j.x;
import com.miui.permcenter.compact.EnterpriseCompat;
import com.miui.permcenter.n;
import com.miui.permcenter.privacymanager.b.e;
import com.miui.permcenter.widget.ChoiceItemView;
import com.miui.permission.PermissionContract;
import com.miui.permission.PermissionManager;
import com.miui.permission.RequiredPermissionsUtil;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import miui.app.AlertDialog;
import miui.util.IOUtils;

public class l {
    public static int a(int i) {
        if (i == 1) {
            return 0;
        }
        if (i == 2) {
            return 1;
        }
        if (i == 3) {
            return 4;
        }
        if (i != 6) {
            return i != 7 ? -1 : 2;
        }
        return 3;
    }

    public static a a(Context context, long j, String str) {
        Cursor cursor;
        String string;
        c cVar;
        String str2 = str;
        String[] strArr = {"pkgName", PermissionContract.Active.SUGGEST_ACCEPT, PermissionContract.Active.SUGGEST_FOREGROUND, PermissionContract.Active.SUGGEST_PROMPT, PermissionContract.Active.SUGGEST_REJECT, PermissionContract.Active.USER_ACCEPT, PermissionContract.Active.USER_FOREGROUND, PermissionContract.Active.USER_PROMPT, PermissionContract.Active.USER_REJECT};
        a aVar = null;
        try {
            String l = Long.toString(j);
            cursor = context.getContentResolver().query(PermissionContract.Active.URI, strArr, "permMask& ? != 0 and +present!= 0 and suggestBlock & ? == 0 and pkgName == ?", new String[]{l, l, str2}, (String) null);
            if (cursor != null) {
                a aVar2 = null;
                while (cursor.moveToNext()) {
                    try {
                        string = cursor.getString(0);
                        cVar = b.a(context).a(str2);
                    } catch (Exception e) {
                        Log.e("PermissionUtils", "fail getAppInfo", e);
                        cVar = null;
                    } catch (Throwable th) {
                        th = th;
                        IOUtils.closeQuietly(cursor);
                        throw th;
                    }
                    if (cVar != null) {
                        int calculatePermissionAction = PermissionManager.calculatePermissionAction(j, cursor.getLong(1), cursor.getLong(2), cursor.getLong(3), cursor.getLong(4), cursor.getLong(5), cursor.getLong(6), cursor.getLong(7), cursor.getLong(8));
                        a aVar3 = new a();
                        aVar3.b(string);
                        aVar3.d(false);
                        aVar3.a(cVar.a());
                        HashMap hashMap = new HashMap();
                        hashMap.put(Long.valueOf(j), Integer.valueOf(calculatePermissionAction));
                        aVar3.a((HashMap<Long, Integer>) hashMap);
                        aVar2 = aVar3;
                    }
                }
                aVar = aVar2;
            }
            IOUtils.closeQuietly(cursor);
            return aVar;
        } catch (Throwable th2) {
            th = th2;
            cursor = null;
            IOUtils.closeQuietly(cursor);
            throw th;
        }
    }

    public static ArrayList<a> a(Context context, long j) {
        int i;
        Cursor cursor;
        Cursor cursor2;
        ArrayList<a> arrayList;
        HashMap hashMap;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        ArrayList<a> arrayList2;
        List<PackageInfo> a2 = b.a(context).a();
        HashMap hashMap2 = new HashMap();
        Iterator<PackageInfo> it = a2.iterator();
        while (true) {
            i = 1;
            if (!it.hasNext()) {
                break;
            }
            PackageInfo next = it.next();
            ApplicationInfo applicationInfo = next.applicationInfo;
            if ((applicationInfo.flags & 1) == 0) {
                hashMap2.put(next.packageName, applicationInfo);
            }
        }
        int i9 = 3;
        int i10 = 4;
        int i11 = 5;
        int i12 = 6;
        int i13 = 7;
        String[] strArr = {"pkgName", PermissionContract.Active.SUGGEST_ACCEPT, PermissionContract.Active.SUGGEST_FOREGROUND, PermissionContract.Active.SUGGEST_PROMPT, PermissionContract.Active.SUGGEST_REJECT, PermissionContract.Active.USER_ACCEPT, PermissionContract.Active.USER_FOREGROUND, PermissionContract.Active.USER_PROMPT, PermissionContract.Active.USER_REJECT};
        ArrayList<a> arrayList3 = new ArrayList<>();
        try {
            String l = Long.toString(j);
            int i14 = 0;
            int i15 = 2;
            Cursor query = context.getContentResolver().query(PermissionContract.Active.URI, strArr, "permMask& ? != 0 and +present!= 0 and suggestBlock & ? == 0 ", new String[]{l, l}, (String) null);
            if (query != null) {
                while (query.moveToNext()) {
                    try {
                        String string = query.getString(i14);
                        ApplicationInfo applicationInfo2 = (ApplicationInfo) hashMap2.get(string);
                        if (applicationInfo2 != null) {
                            long j2 = query.getLong(i);
                            long j3 = query.getLong(i15);
                            long j4 = query.getLong(i9);
                            long j5 = query.getLong(i10);
                            long j6 = query.getLong(i11);
                            long j7 = query.getLong(i12);
                            long j8 = query.getLong(i13);
                            long j9 = query.getLong(8);
                            if ((j6 & j) == 0 && (j8 & j) == 0 && (j9 & j) == 0) {
                                Context context2 = context;
                                cursor = query;
                                arrayList2 = arrayList3;
                                i8 = i15;
                                i7 = i13;
                                i6 = i14;
                                i5 = i11;
                                i4 = i12;
                                i2 = i;
                                i3 = i10;
                                hashMap = hashMap2;
                            } else {
                                ApplicationInfo applicationInfo3 = applicationInfo2;
                                cursor = query;
                                String str = string;
                                long j10 = j3;
                                ArrayList<a> arrayList4 = arrayList3;
                                i8 = i15;
                                i7 = i13;
                                i6 = i14;
                                long j11 = j5;
                                i5 = i11;
                                i4 = i12;
                                long j12 = j6;
                                i2 = i;
                                i3 = i10;
                                long j13 = j7;
                                hashMap = hashMap2;
                                try {
                                    int calculatePermissionAction = PermissionManager.calculatePermissionAction(j, j2, j10, j4, j11, j12, j13, j8, j9);
                                    a aVar = new a();
                                    aVar.b(str);
                                    aVar.a(x.a(context, applicationInfo3));
                                    HashMap hashMap3 = new HashMap();
                                    hashMap3.put(Long.valueOf(j), Integer.valueOf(calculatePermissionAction));
                                    aVar.a((HashMap<Long, Integer>) hashMap3);
                                    arrayList2 = arrayList4;
                                    arrayList2.add(aVar);
                                } catch (Throwable th) {
                                    th = th;
                                    IOUtils.closeQuietly(cursor);
                                    throw th;
                                }
                            }
                            arrayList3 = arrayList2;
                            query = cursor;
                            i15 = i8;
                            i13 = i7;
                            i14 = i6;
                            i11 = i5;
                            i12 = i4;
                            i10 = i3;
                            i = i2;
                            hashMap2 = hashMap;
                            i9 = 3;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        cursor = query;
                        IOUtils.closeQuietly(cursor);
                        throw th;
                    }
                }
                cursor2 = query;
                arrayList = arrayList3;
                Collections.sort(arrayList, new b());
            } else {
                cursor2 = query;
                arrayList = arrayList3;
            }
            IOUtils.closeQuietly(cursor2);
            return arrayList;
        } catch (Throwable th3) {
            th = th3;
            cursor = null;
            IOUtils.closeQuietly(cursor);
            throw th;
        }
    }

    public static ArrayList<a> a(Context context, long j, boolean z) {
        Context context2 = context;
        List<PackageInfo> a2 = b.a(context).a();
        HashMap hashMap = new HashMap();
        for (PackageInfo next : a2) {
            if (EnterpriseCompat.shouldGrantPermission(context2, next.packageName)) {
                Log.d("Enterprise", "Permission edit for package " + next.packageName + " is restricted");
            } else {
                hashMap.put(next.packageName, next);
            }
        }
        int i = 3;
        String[] strArr = {"pkgName", PermissionContract.Active.SUGGEST_ACCEPT, PermissionContract.Active.SUGGEST_FOREGROUND, PermissionContract.Active.SUGGEST_PROMPT, PermissionContract.Active.SUGGEST_REJECT, PermissionContract.Active.USER_ACCEPT, PermissionContract.Active.USER_FOREGROUND, PermissionContract.Active.USER_PROMPT, PermissionContract.Active.USER_REJECT};
        ArrayList<a> arrayList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String l = Long.toString(j);
            boolean z2 = false;
            cursor = context.getContentResolver().query(PermissionContract.Active.URI, strArr, "permMask& ? != 0 and +present!= 0 and suggestBlock & ? == 0", new String[]{l, l}, (String) null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String string = cursor.getString(z2 ? 1 : 0);
                    PackageInfo packageInfo = (PackageInfo) hashMap.get(string);
                    if (packageInfo != null) {
                        boolean z3 = (packageInfo.applicationInfo.flags & 1) != 0 ? true : z2;
                        boolean z4 = B.a(packageInfo.applicationInfo.uid) <= 10000;
                        boolean isAdaptedRequiredPermissionsOnData = RequiredPermissionsUtil.isAdaptedRequiredPermissionsOnData(packageInfo);
                        if ((!z3 && !isAdaptedRequiredPermissionsOnData && !z4) || z) {
                            int calculatePermissionAction = PermissionManager.calculatePermissionAction(j, cursor.getLong(1), cursor.getLong(2), cursor.getLong(i), cursor.getLong(4), cursor.getLong(5), cursor.getLong(6), cursor.getLong(7), cursor.getLong(8));
                            a aVar = new a();
                            aVar.b(string);
                            aVar.d(z3);
                            aVar.a(x.a(context2, packageInfo.applicationInfo));
                            HashMap hashMap2 = new HashMap();
                            hashMap2.put(Long.valueOf(j), Integer.valueOf(calculatePermissionAction));
                            aVar.a((HashMap<Long, Integer>) hashMap2);
                            arrayList.add(aVar);
                            i = 3;
                        }
                        z2 = false;
                    }
                }
                Collections.sort(arrayList, new b());
            }
            return arrayList;
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    public static HashMap<Long, Integer> a(Context context, String str) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            String[] strArr = {PermissionContract.Active.PERMISSION_MASK, PermissionContract.Active.SUGGEST_ACCEPT, PermissionContract.Active.SUGGEST_FOREGROUND, PermissionContract.Active.SUGGEST_PROMPT, PermissionContract.Active.SUGGEST_REJECT, PermissionContract.Active.USER_ACCEPT, PermissionContract.Active.USER_FOREGROUND, PermissionContract.Active.USER_PROMPT, PermissionContract.Active.USER_REJECT};
            Cursor query = contentResolver.query(PermissionContract.Active.URI, strArr, "pkgName =? ", new String[]{str}, (String) null);
            if (query != null) {
                try {
                    if (query.getCount() >= 0) {
                        PermissionManager instance = PermissionManager.getInstance(context);
                        if (query.moveToNext()) {
                            HashMap<Long, Integer> calculatePermissionAction = instance.calculatePermissionAction(query.getLong(0), query.getLong(1), query.getLong(2), query.getLong(3), query.getLong(4), 0, query.getLong(5), query.getLong(6), query.getLong(7), query.getLong(8));
                            IOUtils.closeQuietly(query);
                            return calculatePermissionAction;
                        }
                        IOUtils.closeQuietly(query);
                        return null;
                    }
                } catch (Throwable th) {
                    th = th;
                    cursor = query;
                    IOUtils.closeQuietly(cursor);
                    throw th;
                }
            }
            IOUtils.closeQuietly(query);
            return null;
        } catch (Throwable th2) {
            th = th2;
            IOUtils.closeQuietly(cursor);
            throw th;
        }
    }

    public static void a(Activity activity, String str, int i, long j, String str2, int i2, n.c cVar, boolean z, boolean z2, String str3, String str4, boolean z3) {
        Activity activity2 = activity;
        String str5 = str;
        if (com.miui.permcenter.privacymanager.b.c.a(activity)) {
            e.a(activity, str, j, str2, i2, cVar, z, z2, str3, str4, z3);
            return;
        }
        String[] strArr = new String[5];
        String string = activity2.getString(R.string.permission_action_always);
        String string2 = activity2.getString(R.string.permission_action_reject);
        if (Build.VERSION.SDK_INT < 28 || !z3) {
            strArr[4] = string;
        } else {
            if (j != PermissionManager.PERM_ID_VIDEO_RECORDER) {
                strArr[4] = string;
            }
            strArr[3] = activity2.getString(R.string.permission_action_foreground);
        }
        if (PermissionManager.virtualMap.containsKey(Long.valueOf(j))) {
            strArr[2] = activity2.getString(R.string.permission_action_virtual);
        }
        if (!z) {
            strArr[1] = activity2.getString(R.string.permission_action_prompt);
        }
        if (!z2) {
            strArr[0] = string2;
        }
        int a2 = a(i2);
        View inflate = LayoutInflater.from(activity).inflate(R.layout.dialog_permission_manager, (ViewGroup) null);
        ImageView imageView = (ImageView) inflate.findViewById(R.id.dialog_package_icon);
        TextView textView = (TextView) inflate.findViewById(R.id.dialog_package_name);
        TextView textView2 = (TextView) inflate.findViewById(R.id.dialog_permission_desc_content);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
        builder.setView(inflate);
        AlertDialog show = builder.setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).show();
        FrameLayout frameLayout = (FrameLayout) inflate.getParent().getParent();
        int paddingLeft = frameLayout.getPaddingLeft();
        int paddingRight = frameLayout.getPaddingRight();
        frameLayout.setPadding(0, frameLayout.getPaddingTop(), 0, frameLayout.getPaddingBottom());
        ((TextView) inflate.findViewById(R.id.dialog_permission_name)).setText(str2);
        r.a((i == 999 ? "pkg_icon_xspace://" : "pkg_icon://").concat(str5), imageView, r.f);
        textView.setText(str3);
        if (!TextUtils.isEmpty(str4)) {
            textView2.setVisibility(0);
            textView2.setText(str4);
        }
        View view = inflate;
        int i3 = a2;
        k kVar = new k(show, i2, activity, str, j, cVar);
        ChoiceItemView[] choiceItemViewArr = new ChoiceItemView[5];
        choiceItemViewArr[4] = (ChoiceItemView) view.findViewById(R.id.select_allow);
        choiceItemViewArr[3] = (ChoiceItemView) view.findViewById(R.id.select_foreground);
        choiceItemViewArr[2] = (ChoiceItemView) view.findViewById(R.id.select_virtual);
        choiceItemViewArr[1] = (ChoiceItemView) view.findViewById(R.id.select_ask);
        choiceItemViewArr[0] = (ChoiceItemView) view.findViewById(R.id.select_deny);
        for (int i4 = 0; i4 <= 4; i4++) {
            if (TextUtils.isEmpty(strArr[i4])) {
                choiceItemViewArr[i4].setVisibility(8);
            } else {
                choiceItemViewArr[i4].a(0, paddingLeft, paddingRight);
                choiceItemViewArr[i4].setOnClickListener(kVar);
                choiceItemViewArr[i4].setTitle(strArr[i4]);
                if (i4 == 2) {
                    choiceItemViewArr[i4].setSummary(activity.getResources().getString(R.string.permission_action_desc));
                    choiceItemViewArr[i4].setTips(activity.getResources().getString(R.string.permission_action_tag));
                }
            }
        }
        if (i3 != -1) {
            choiceItemViewArr[i3].setSelectedVisible(true);
        }
    }

    public static HashMap<Long, Integer> b(Context context, String str) {
        Cursor cursor;
        ContentResolver contentResolver = context.getContentResolver();
        try {
            String[] strArr = {PermissionContract.Active.PERMISSION_MASK, PermissionContract.Active.SUGGEST_ACCEPT, PermissionContract.Active.SUGGEST_FOREGROUND, PermissionContract.Active.SUGGEST_PROMPT, PermissionContract.Active.SUGGEST_REJECT, PermissionContract.Active.SUGGEST_BLOCK, PermissionContract.Active.USER_ACCEPT, PermissionContract.Active.USER_FOREGROUND, PermissionContract.Active.USER_PROMPT, PermissionContract.Active.USER_REJECT};
            Cursor query = contentResolver.query(PermissionContract.Active.URI, strArr, "pkgName =? ", new String[]{str}, (String) null);
            if (query != null) {
                try {
                    if (query.getCount() >= 0) {
                        PermissionManager instance = PermissionManager.getInstance(context);
                        if (query.moveToNext()) {
                            HashMap<Long, Integer> calculatePermissionAction = instance.calculatePermissionAction(query.getLong(0), query.getLong(1), query.getLong(2), query.getLong(3), query.getLong(4), query.getLong(5), query.getLong(6), query.getLong(7), query.getLong(8), query.getLong(9));
                            IOUtils.closeQuietly(query);
                            return calculatePermissionAction;
                        }
                        IOUtils.closeQuietly(query);
                        return null;
                    }
                } catch (Throwable th) {
                    th = th;
                    cursor = query;
                    IOUtils.closeQuietly(cursor);
                    throw th;
                }
            }
            IOUtils.closeQuietly(query);
            return null;
        } catch (Throwable th2) {
            th = th2;
            cursor = null;
            IOUtils.closeQuietly(cursor);
            throw th;
        }
    }
}
