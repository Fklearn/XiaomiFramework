package com.miui.optimizecenter.storage;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class m {

    /* renamed from: a  reason: collision with root package name */
    private static m f5749a;

    /* renamed from: b  reason: collision with root package name */
    private static final String f5750b = Environment.getExternalStorageDirectory().toString();

    /* renamed from: c  reason: collision with root package name */
    private Context f5751c;

    /* renamed from: d  reason: collision with root package name */
    private List<String> f5752d = Arrays.asList(new String[]{".doc", ".docx", ".pdf", ".txt", ".ppt", ".pptx", ".xls", ".xlsx", ".html", ".xml", ".rar", ".tar", ".gz", ".zip"});

    private m(Context context) {
        this.f5751c = context;
    }

    private long a(Uri uri, String str, String str2, List<String> list) {
        Cursor query = this.f5751c.getContentResolver().query(uri, (String[]) null, (String) null, (String[]) null, (String) null, (CancellationSignal) null);
        int columnIndexOrThrow = query.getColumnIndexOrThrow(str);
        int columnIndexOrThrow2 = query.getColumnIndexOrThrow(str2);
        long j = 0;
        if (query != null) {
            while (query.moveToNext()) {
                try {
                    String string = query.getString(columnIndexOrThrow);
                    if (!TextUtils.isEmpty(string)) {
                        if (string.startsWith(f5750b)) {
                            long j2 = query.getLong(columnIndexOrThrow2);
                            if (list != null && list.size() > 0) {
                                boolean z = false;
                                Iterator<String> it = list.iterator();
                                while (true) {
                                    if (it.hasNext()) {
                                        if (string.startsWith(it.next())) {
                                            z = true;
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                }
                                if (z) {
                                }
                            }
                            j += j2;
                        }
                    }
                } catch (Throwable th) {
                    if (query != null) {
                        query.close();
                    }
                    throw th;
                }
            }
        }
        if (query != null) {
            query.close();
        }
        return j;
    }

    private long a(Uri uri, String[] strArr, String str, List<String> list, List<String> list2) {
        boolean z;
        Cursor query = this.f5751c.getContentResolver().query(uri, strArr, str, (String[]) null, (String) null);
        long j = 0;
        if (query != null) {
            while (query.moveToNext()) {
                boolean z2 = false;
                String string = query.getString(0);
                if (!TextUtils.isEmpty(string) && string.startsWith(f5750b)) {
                    File file = new File(string);
                    if (file.exists()) {
                        if (list2 != null && list2.size() > 0) {
                            Iterator<String> it = list2.iterator();
                            while (true) {
                                if (it.hasNext()) {
                                    if (string.startsWith(it.next())) {
                                        z = true;
                                        break;
                                    }
                                } else {
                                    z = false;
                                    break;
                                }
                            }
                            if (z) {
                            }
                        }
                        if (list != null && list.size() > 0) {
                            Iterator<String> it2 = list.iterator();
                            while (true) {
                                if (it2.hasNext()) {
                                    if (string.endsWith(it2.next())) {
                                        z2 = true;
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                            if (!z2) {
                            }
                        }
                        j += file.length();
                    }
                }
            }
        }
        return j;
    }

    public static m a(Context context) {
        if (f5749a == null) {
            f5749a = new m(context);
        }
        return f5749a;
    }

    public long a(List<String> list) {
        return a(MediaStore.Files.getContentUri("external"), new String[]{"_data"}, "_data LIKE '%.apk'", (List<String>) null, (List<String>) null);
    }

    public long b(List<String> list) {
        return a(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "_data", "_size", list);
    }

    public long c(List<String> list) {
        Uri contentUri = MediaStore.Files.getContentUri("external");
        String[] strArr = {"_data"};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.f5752d.size(); i++) {
            sb.append("_data");
            sb.append(" LIKE ");
            sb.append("'%");
            sb.append(this.f5752d.get(i));
            sb.append("'");
            if (i != this.f5752d.size() - 1) {
                sb.append(" or ");
            }
        }
        return a(contentUri, strArr, sb.toString(), (List<String>) null, list);
    }

    public long d(List<String> list) {
        return a(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "_data", "_size", list);
    }

    public long e(List<String> list) {
        return a(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "_data", "_size", list);
    }
}
