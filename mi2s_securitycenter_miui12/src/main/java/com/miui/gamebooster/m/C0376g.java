package com.miui.gamebooster.m;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import com.miui.gamebooster.model.t;
import java.io.File;

/* renamed from: com.miui.gamebooster.m.g  reason: case insensitive filesystem */
public class C0376g {

    /* renamed from: com.miui.gamebooster.m.g$a */
    public enum a {
        TABLE_GAME_INFO,
        TABLE_GAME_INFO_VIDEO,
        TABLE_OP_TRACE
    }

    public static int a(Context context, a aVar, int i) {
        return context.getContentResolver().delete(a(aVar), "id=?", new String[]{String.valueOf(i)});
    }

    public static int a(Context context, a aVar, long j) {
        return context.getContentResolver().delete(a(aVar), "store_path is null and op_date<=?", new String[]{String.valueOf(j)});
    }

    public static int a(Context context, t tVar) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("store_path", tVar.g());
        return context.getContentResolver().update(a(a.TABLE_GAME_INFO_VIDEO), contentValues, "file_path=?", new String[]{tVar.c()});
    }

    public static Cursor a(Context context, a aVar, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return context.getContentResolver().query(a(aVar), (String[]) null, "game_type=?", new String[]{str}, " op_date desc");
    }

    public static Cursor a(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return context.getContentResolver().query(a(a.TABLE_GAME_INFO_VIDEO), (String[]) null, "match_md5=?", new String[]{str}, (String) null);
    }

    private static Uri a(a aVar) {
        StringBuilder sb = new StringBuilder();
        sb.append("content://com.xiaomi.migameservice.provider");
        sb.append(File.separator);
        sb.append(a.TABLE_GAME_INFO_VIDEO == aVar ? "game_video" : "game_info");
        return Uri.parse(sb.toString());
    }

    public static Cursor b(Context context, a aVar, long j) {
        return context.getContentResolver().query(a(aVar), (String[]) null, "op_date<=?", new String[]{String.valueOf(j)}, (String) null);
    }

    public static Cursor b(Context context, a aVar, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return context.getContentResolver().query(a(aVar), (String[]) null, "match_md5=?", new String[]{str}, " op_date desc");
    }
}
