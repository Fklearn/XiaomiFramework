package com.miui.powercenter.autotask;

import android.app.FragmentManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import miui.cloud.CloudPushConstants;
import miui.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;

/* renamed from: com.miui.powercenter.autotask.s  reason: case insensitive filesystem */
public class C0489s {
    public static List<AutoTask> a(Context context) {
        Cursor query = context.getContentResolver().query(AutoTask.CONTENT_URI, AutoTask.QUERY_COLUMNS, (String) null, (String[]) null, (String) null);
        ArrayList arrayList = new ArrayList();
        if (query != null) {
            while (query.moveToNext()) {
                try {
                    arrayList.add(new AutoTask(query));
                } finally {
                    IOUtils.closeQuietly(query);
                }
            }
        }
        return arrayList;
    }

    public static void a(Context context, long j, boolean z) {
        Uri withAppendedId = ContentUris.withAppendedId(AutoTask.CONTENT_URI, j);
        ContentValues contentValues = new ContentValues();
        contentValues.put("enabled", Integer.valueOf(z ? 1 : 0));
        contentValues.put("task_started", false);
        contentValues.put("restore_operation", "{}");
        context.getContentResolver().update(withAppendedId, contentValues, (String) null, (String[]) null);
        c(context, j);
    }

    public static void a(Context context, FragmentManager fragmentManager, Long[] lArr) {
        new r(fragmentManager, lArr, context).setCancelable(false).setIndeterminate(false).setMaxProgress(100).setProgressStyle(1).execute(new Void[0]);
    }

    public static void a(Context context, AutoTask autoTask) {
        StringBuilder sb;
        ContentValues contentValues = new ContentValues();
        contentValues.put(CloudPushConstants.XML_NAME, autoTask.getName());
        contentValues.put("enabled", Boolean.valueOf(autoTask.getEnabled()));
        contentValues.put("condition", autoTask.getConditionString());
        contentValues.put("operation", autoTask.getOperationString());
        contentValues.put("repeat_type", Integer.valueOf(autoTask.getRepeatType()));
        contentValues.put("task_started", Boolean.valueOf(autoTask.getStarted()));
        contentValues.put("restore_operation", autoTask.getRestoreOperationString());
        contentValues.put("restore_level", Integer.valueOf(autoTask.getRestoreLevel()));
        long id = autoTask.getId();
        if (id >= 0) {
            int update = context.getContentResolver().update(ContentUris.withAppendedId(AutoTask.CONTENT_URI, id), contentValues, (String) null, (String[]) null);
            sb = new StringBuilder();
            sb.append("update task id ");
            sb.append(id);
            sb.append(" count ");
            sb.append(update);
        } else {
            Uri insert = context.getContentResolver().insert(AutoTask.CONTENT_URI, contentValues);
            if (insert == null) {
                Log.e("AutoTaskHelper", "insert failed");
                return;
            }
            id = ContentUris.parseId(insert);
            sb = new StringBuilder();
            sb.append("insert new task ");
            sb.append(insert);
        }
        Log.d("AutoTaskHelper", sb.toString());
        c(context, id);
    }

    public static void a(Context context, JSONArray jSONArray) {
        if (jSONArray != null) {
            int i = 0;
            while (i < jSONArray.length()) {
                try {
                    a(context, new AutoTask(jSONArray.getJSONObject(i)));
                    i++;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private static long[] a(Long... lArr) {
        long[] jArr = new long[lArr.length];
        for (int i = 0; i < lArr.length; i++) {
            jArr[i] = lArr[i].longValue();
        }
        return jArr;
    }

    public static AutoTask b(Context context, long j) {
        Cursor query = context.getContentResolver().query(ContentUris.withAppendedId(AutoTask.CONTENT_URI, j), AutoTask.QUERY_COLUMNS, (String) null, (String[]) null, (String) null);
        AutoTask autoTask = null;
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    autoTask = new AutoTask(query);
                }
            } finally {
                IOUtils.closeQuietly(query);
            }
        }
        return autoTask;
    }

    public static JSONArray b(Context context) {
        List<AutoTask> a2 = a(context);
        JSONArray jSONArray = new JSONArray();
        try {
            for (AutoTask json : a2) {
                jSONArray.put(json.toJson());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jSONArray;
    }

    /* access modifiers changed from: private */
    public static void b(Context context, Long[] lArr) {
        long[] a2 = a(lArr);
        Intent intent = new Intent(context, AutoTaskIntentService.class);
        intent.setAction("com.miui.powercenter.action.TASK_DELETE");
        intent.putExtra("ids", a2);
        context.startService(intent);
        Intent intent2 = new Intent();
        intent2.setAction("com.miui.powercenter.action.TASK_DELETE");
        intent2.putExtra("ids", a2);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
    }

    public static Cursor c(Context context) {
        return context.getContentResolver().query(AutoTask.CONTENT_URI, AutoTask.QUERY_COLUMNS, "enabled=1", (String[]) null, (String) null);
    }

    public static void c(Context context, long j) {
        Intent intent = new Intent(context, AutoTaskIntentService.class);
        intent.setAction("com.miui.powercenter.action.TASK_UPDATE");
        intent.putExtra("id", j);
        context.startService(intent);
    }

    public static void d(Context context) {
        Intent intent = new Intent(context, C0477f.class);
        intent.setAction("com.miui.powercenter.action.TASK_RESET");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /* access modifiers changed from: private */
    public static void d(Context context, long j) {
        context.getContentResolver().delete(ContentUris.withAppendedId(AutoTask.CONTENT_URI, j), "", (String[]) null);
    }
}
