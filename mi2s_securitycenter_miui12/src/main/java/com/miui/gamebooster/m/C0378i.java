package com.miui.gamebooster.m;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import com.miui.gamebooster.m.C0376g;
import com.miui.gamebooster.model.C;
import com.miui.gamebooster.model.C0399e;
import com.miui.gamebooster.model.s;
import com.miui.gamebooster.model.t;
import com.miui.securitycenter.R;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/* renamed from: com.miui.gamebooster.m.i  reason: case insensitive filesystem */
public class C0378i {

    /* renamed from: a  reason: collision with root package name */
    private static final String f4494a = "i";

    public static int a(Context context, long j) {
        return C0376g.a(context, C0376g.a.TABLE_GAME_INFO_VIDEO, j);
    }

    public static File a() {
        File file = new File(b(), "mitime/storage");
        file.mkdirs();
        return file;
    }

    public static List<C0399e> a(Context context, String str) {
        Context context2 = context;
        if (TextUtils.isEmpty(str)) {
            return Collections.emptyList();
        }
        Cursor a2 = C0376g.a(context2, C0376g.a.TABLE_GAME_INFO_VIDEO, str);
        if (a2 == null || a2.getCount() <= 0) {
            return Collections.emptyList();
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context2.getString(R.string.gb_wonderful_video_date_format));
            int columnIndexOrThrow = a2.getColumnIndexOrThrow("duration");
            int columnIndexOrThrow2 = a2.getColumnIndexOrThrow("id");
            int columnIndexOrThrow3 = a2.getColumnIndexOrThrow("game_type");
            int columnIndexOrThrow4 = a2.getColumnIndexOrThrow("record_type");
            int columnIndexOrThrow5 = a2.getColumnIndexOrThrow("op_date");
            int columnIndexOrThrow6 = a2.getColumnIndexOrThrow("file_path");
            int columnIndexOrThrow7 = a2.getColumnIndexOrThrow("file_name");
            int columnIndexOrThrow8 = a2.getColumnIndexOrThrow("store_path");
            int columnIndexOrThrow9 = a2.getColumnIndexOrThrow("match_md5");
            ArrayList arrayList = new ArrayList();
            while (a2.moveToNext()) {
                int i = a2.getInt(columnIndexOrThrow);
                String string = a2.getString(columnIndexOrThrow4);
                SimpleDateFormat simpleDateFormat2 = simpleDateFormat;
                int i2 = columnIndexOrThrow;
                long j = a2.getLong(columnIndexOrThrow5);
                int i3 = columnIndexOrThrow4;
                int i4 = a2.getInt(columnIndexOrThrow2);
                int i5 = columnIndexOrThrow2;
                String string2 = a2.getString(columnIndexOrThrow8);
                int i6 = columnIndexOrThrow5;
                String string3 = a2.getString(columnIndexOrThrow7);
                int i7 = columnIndexOrThrow7;
                String string4 = a2.getString(columnIndexOrThrow6);
                int i8 = columnIndexOrThrow6;
                String string5 = a2.getString(columnIndexOrThrow3);
                int i9 = columnIndexOrThrow3;
                String string6 = a2.getString(columnIndexOrThrow9);
                int i10 = columnIndexOrThrow8;
                t tVar = new t();
                tVar.a(i4);
                tVar.d(string6);
                tVar.a(string3);
                tVar.a((long) i);
                tVar.b(string4);
                tVar.f(string2);
                tVar.c(string5);
                tVar.g(string);
                long a3 = C0382m.a(string4);
                long a4 = C0382m.a(string2);
                int i11 = (a3 > 0 ? 1 : (a3 == 0 ? 0 : -1));
                if (i11 > 0) {
                    tVar.b(a3);
                } else if (a4 > 0) {
                    tVar.b(a4);
                }
                Date date = new Date(j);
                simpleDateFormat = simpleDateFormat2;
                tVar.e(simpleDateFormat.format(date));
                if (TextUtils.equals(tVar.f(), simpleDateFormat.format(new Date()))) {
                    tVar.e(context2.getString(R.string.gb_wonderful_video_header_title));
                }
                if (i11 > 0 || a4 > 0) {
                    arrayList.add(tVar);
                }
                columnIndexOrThrow = i2;
                columnIndexOrThrow4 = i3;
                columnIndexOrThrow2 = i5;
                columnIndexOrThrow5 = i6;
                columnIndexOrThrow7 = i7;
                columnIndexOrThrow6 = i8;
                columnIndexOrThrow3 = i9;
                columnIndexOrThrow8 = i10;
            }
            return a((List<t>) arrayList);
        } catch (SQLiteException | IllegalArgumentException e) {
            Log.e(f4494a, "get data error", e);
            return Collections.emptyList();
        } finally {
            a2.close();
        }
    }

    public static List<C0399e> a(List<t> list) {
        C c2;
        if (list == null || list.size() == 0) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        for (t next : list) {
            if (arrayList.size() > 0) {
                Object obj = arrayList.get(arrayList.size() - 1);
                if (obj instanceof C) {
                    C c3 = (C) obj;
                    t e = c3.e();
                    if (e == null || !TextUtils.equals(next.f(), e.f())) {
                        s sVar = new s();
                        sVar.a(next.f());
                        arrayList.add(sVar);
                        c2 = new C();
                    } else if (c3.f() < 3) {
                        c3.a(next);
                    } else {
                        c2 = new C();
                    }
                }
            } else {
                s sVar2 = new s();
                sVar2.a(next.f());
                arrayList.add(sVar2);
                c2 = new C();
            }
            c2.a(next);
            arrayList.add(c2);
        }
        return arrayList;
    }

    public static void a(Context context, t tVar) {
        C0376g.a(context, C0376g.a.TABLE_GAME_INFO_VIDEO, tVar.e());
    }

    public static void a(t tVar) {
        a(tVar.c());
        a(tVar.g());
    }

    public static void a(String str) {
        if (!TextUtils.isEmpty(str)) {
            File file = new File(str);
            file.delete();
            File parentFile = file.getParentFile();
            if (parentFile != null) {
                String[] list = parentFile.list();
                if (list == null || list.length == 0) {
                    parentFile.delete();
                }
            }
        }
    }

    private static File b() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    }

    public static String b(Context context, String str) {
        Cursor b2 = C0376g.b(context, C0376g.a.TABLE_GAME_INFO_VIDEO, str);
        if (b2 == null || b2.getCount() <= 0) {
            return null;
        }
        int columnIndexOrThrow = b2.getColumnIndexOrThrow("game_type");
        b2.moveToFirst();
        String string = b2.getString(columnIndexOrThrow);
        b2.close();
        return string;
    }

    public static List<String> b(Context context, long j) {
        ArrayList arrayList = new ArrayList();
        Cursor b2 = C0376g.b(context, C0376g.a.TABLE_GAME_INFO_VIDEO, j);
        if (b2 != null) {
            if (b2.getCount() > 0) {
                try {
                    int columnIndexOrThrow = b2.getColumnIndexOrThrow("file_path");
                    while (b2.moveToNext()) {
                        arrayList.add(b2.getString(columnIndexOrThrow));
                    }
                    return arrayList;
                } catch (SQLiteException | IllegalArgumentException e) {
                    Log.e(f4494a, "get delete data error", e);
                    return arrayList;
                } finally {
                    b2.close();
                }
            } else {
                b2.close();
            }
        }
        return arrayList;
    }

    public static boolean b(Context context, t tVar) {
        return C0376g.a(context, tVar) > 0;
    }

    public static List<t> c(Context context, String str) {
        ArrayList arrayList;
        Context context2;
        Context context3 = context;
        ArrayList arrayList2 = new ArrayList();
        if (TextUtils.isEmpty(str)) {
            return arrayList2;
        }
        Cursor b2 = C0376g.b(context3, C0376g.a.TABLE_GAME_INFO_VIDEO, str);
        if (b2 == null || b2.getCount() <= 0) {
            return arrayList2;
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context3.getString(R.string.gb_wonderful_video_date_format));
            int columnIndexOrThrow = b2.getColumnIndexOrThrow("duration");
            int columnIndexOrThrow2 = b2.getColumnIndexOrThrow("id");
            int columnIndexOrThrow3 = b2.getColumnIndexOrThrow("game_type");
            int columnIndexOrThrow4 = b2.getColumnIndexOrThrow("record_type");
            int columnIndexOrThrow5 = b2.getColumnIndexOrThrow("op_date");
            int columnIndexOrThrow6 = b2.getColumnIndexOrThrow("file_path");
            int columnIndexOrThrow7 = b2.getColumnIndexOrThrow("file_name");
            int columnIndexOrThrow8 = b2.getColumnIndexOrThrow("store_path");
            int columnIndexOrThrow9 = b2.getColumnIndexOrThrow("match_md5");
            while (b2.moveToNext()) {
                int i = b2.getInt(columnIndexOrThrow);
                String string = b2.getString(columnIndexOrThrow4);
                ArrayList arrayList3 = arrayList2;
                try {
                    long j = b2.getLong(columnIndexOrThrow5);
                    int i2 = columnIndexOrThrow;
                    int i3 = b2.getInt(columnIndexOrThrow2);
                    int i4 = columnIndexOrThrow2;
                    String string2 = b2.getString(columnIndexOrThrow8);
                    int i5 = columnIndexOrThrow4;
                    String string3 = b2.getString(columnIndexOrThrow7);
                    int i6 = columnIndexOrThrow5;
                    String string4 = b2.getString(columnIndexOrThrow6);
                    int i7 = columnIndexOrThrow6;
                    String string5 = b2.getString(columnIndexOrThrow3);
                    int i8 = columnIndexOrThrow3;
                    String string6 = b2.getString(columnIndexOrThrow9);
                    int i9 = columnIndexOrThrow7;
                    t tVar = new t();
                    tVar.a(i3);
                    tVar.d(string6);
                    tVar.a(string3);
                    tVar.a((long) i);
                    tVar.b(string4);
                    tVar.f(string2);
                    tVar.c(string5);
                    tVar.g(string);
                    tVar.e(simpleDateFormat.format(new Date(j)));
                    if (TextUtils.equals(tVar.f(), simpleDateFormat.format(new Date()))) {
                        context2 = context;
                        tVar.e(context2.getString(R.string.gb_wonderful_video_header_title));
                    } else {
                        context2 = context;
                    }
                    long a2 = C0382m.a(string4);
                    long a3 = C0382m.a(string2);
                    if (a2 > 0 || a3 > 0) {
                        arrayList = arrayList3;
                        try {
                            arrayList.add(tVar);
                        } catch (SQLiteException | IllegalArgumentException e) {
                            e = e;
                        }
                    } else {
                        arrayList = arrayList3;
                    }
                    Context context4 = context2;
                    arrayList2 = arrayList;
                    columnIndexOrThrow2 = i4;
                    columnIndexOrThrow4 = i5;
                    columnIndexOrThrow5 = i6;
                    columnIndexOrThrow6 = i7;
                    columnIndexOrThrow3 = i8;
                    columnIndexOrThrow7 = i9;
                    columnIndexOrThrow = i2;
                } catch (SQLiteException | IllegalArgumentException e2) {
                    e = e2;
                    arrayList = arrayList3;
                    try {
                        Log.e(f4494a, "get match error", e);
                        return arrayList;
                    } finally {
                        b2.close();
                    }
                }
            }
            ArrayList arrayList4 = arrayList2;
            b2.close();
            return arrayList4;
        } catch (SQLiteException | IllegalArgumentException e3) {
            e = e3;
            arrayList = arrayList2;
            Log.e(f4494a, "get match error", e);
            return arrayList;
        }
    }

    public static int d(Context context, String str) {
        Cursor a2 = C0376g.a(context, str);
        if (a2 == null) {
            return 0;
        }
        int count = a2.getCount();
        a2.close();
        return count;
    }
}
