package b.b.c.i;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.miui.antispam.db.d;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import miui.util.IOUtils;

public class e {

    /* renamed from: a  reason: collision with root package name */
    public static final String f1748a = "e";
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public static c f1749b = new c();

    private static class a extends AsyncTask<Void, Void, Integer> {

        /* renamed from: a  reason: collision with root package name */
        private int f1750a;

        /* renamed from: b  reason: collision with root package name */
        private Context f1751b;

        public a(Context context, int i) {
            this.f1750a = i;
            this.f1751b = context;
        }

        private int a() {
            String str;
            int i;
            int i2 = this.f1750a;
            String str2 = "";
            if (i2 != 1) {
                str = i2 != 2 ? i2 != 3 ? str2 : "rm -rf /data/data/com.miui.antispam/files" : "rm -rf /data/data/com.miui.antispam/shared_prefs";
            } else {
                str2 = "cp -r /data/data/com.miui.antispam/databases/AntiSpam /data/data/com.miui.securitycenter/databases/";
                str = "rm -rf /data/data/com.miui.antispam/databases/";
            }
            try {
                i = this.f1750a == 2 ? e.c(this.f1751b) : this.f1750a == 3 ? a("cp -r /data/data/com.miui.antispam/files/pattern /data/data/com.miui.securitycenter/files/") + a("cp -r /data/data/com.miui.antispam/files/phish /data/data/com.miui.securitycenter/files/") : a(str2);
                String str3 = e.f1748a;
                Log.d(str3, "the copy result is: " + i + "  " + str);
                if (i == 0 && !TextUtils.isEmpty(str)) {
                    int a2 = a(str);
                    String str4 = e.f1748a;
                    Log.d(str4, "del result: " + a2);
                }
            } catch (Exception e) {
                e.printStackTrace();
                i = -1;
            }
            if (i != 0) {
                String str5 = e.f1748a;
                Log.d(str5, "data transfer failed: " + str2);
            }
            return i;
        }

        private int a(String str) {
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = null;
            try {
                Process exec = Runtime.getRuntime().exec(str);
                BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
                while (true) {
                    try {
                        String readLine = bufferedReader2.readLine();
                        if (readLine != null) {
                            sb.append(readLine);
                        } else {
                            String str2 = e.f1748a;
                            Log.d(str2, "other process result: " + sb.toString());
                            int waitFor = exec.waitFor();
                            IOUtils.closeQuietly(bufferedReader2);
                            return waitFor;
                        }
                    } catch (Exception e) {
                        e = e;
                        bufferedReader = bufferedReader2;
                        try {
                            e.printStackTrace();
                            IOUtils.closeQuietly(bufferedReader);
                            return -1;
                        } catch (Throwable th) {
                            th = th;
                            bufferedReader2 = bufferedReader;
                            IOUtils.closeQuietly(bufferedReader2);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        IOUtils.closeQuietly(bufferedReader2);
                        throw th;
                    }
                }
            } catch (Exception e2) {
                e = e2;
                e.printStackTrace();
                IOUtils.closeQuietly(bufferedReader);
                return -1;
            }
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Integer doInBackground(Void... voidArr) {
            Integer num;
            try {
                num = Integer.valueOf(a());
            } catch (Exception e) {
                Log.d(e.f1748a, "exception");
                e.printStackTrace();
                num = null;
            }
            e.f1749b.b();
            return num;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Integer num) {
            int i = this.f1750a;
            String str = i != 1 ? i != 2 ? i != 3 ? "" : "restore_antispam_files" : "restore_antispam_prefs" : "restore_antispam_db";
            if (num != null && num.intValue() == 0) {
                String str2 = e.f1748a;
                Log.d(str2, "copy success " + str);
            }
        }
    }

    private static void a(Context context, int i) {
        File file;
        File file2 = null;
        String str = "";
        if (i == 1) {
            file2 = new File("/data/data/com.miui.antispam/databases/AntiSpam");
            file = new File("/data/data/com.miui.securitycenter/databases");
            str = "cp -r /data/data/com.miui.antispam/databases/AntiSpam /data/data/com.miui.securitycenter/databases/";
        } else if (i == 2) {
            file2 = new File("/data/data/com.miui.antispam/shared_prefs");
            file = new File("/data/data/com.miui.securitycenter/shared_prefs");
            str = "copy preferences";
        } else if (i != 3) {
            file = null;
        } else {
            try {
                file2 = new File("/data/data/com.miui.antispam/files");
                file = new File("/data/data/com.miui.securitycenter/files");
                str = "copy files";
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(f1748a, "exception " + str);
                return;
            }
        }
        if (file2 != null) {
            if (file != null) {
                if (!file2.exists() || !file.exists()) {
                    Log.d(f1748a, "source or target folder does not exist: " + str + "; " + file2.exists() + "; " + file.exists());
                    return;
                }
                f1749b.a();
                new a(context, i).execute(new Void[0]);
                return;
            }
        }
        Log.d(f1748a, "folder does not exist: " + str);
    }

    public static void b(Context context) {
        if ((Build.VERSION.SDK_INT < 24 || !context.isDeviceProtectedStorage()) && !d.e()) {
            Log.i(f1748a, "restore AntiSpam Data!");
            try {
                File file = new File("/data/data/com.miui.securitycenter");
                if (!file.exists()) {
                    file.mkdir();
                }
                File file2 = new File("/data/data/com.miui.securitycenter/databases");
                if (!file2.exists()) {
                    file2.mkdir();
                }
                File file3 = new File("/data/data/com.miui.securitycenter/shared_prefs");
                if (!file3.exists()) {
                    file3.mkdir();
                }
                File file4 = new File("/data/data/com.miui.securitycenter/files");
                if (!file4.exists()) {
                    file4.mkdir();
                }
                a(context, 1);
                a(context, 2);
                a(context, 3);
                f1749b.c();
                d.b(true);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(f1748a, "create folder failed");
            }
        }
    }

    /* access modifiers changed from: private */
    public static int c(Context context) {
        try {
            SharedPreferences sharedPreferences = context.createPackageContext("com.miui.antispam", 0).getSharedPreferences("remote_provider_preferences", 0);
            d.a(sharedPreferences.getBoolean("dataMigration", false));
            d.a(sharedPreferences.getLong("clouds_data_version", 0));
            d.a(sharedPreferences.getString("update_state", ""));
            d.a(1, sharedPreferences.getBoolean("is_call_transfer_blocked", false));
            d.b(1, sharedPreferences.getBoolean("is_repeated_marked_number_permit", false));
            d.c(sharedPreferences.getBoolean("reported_number_settings_reset", false));
            int i = sharedPreferences.getInt("backsound_index", 0);
            if (i > 0) {
                d.a(1, i);
            }
            d.d(sharedPreferences.getInt("unread_mms_count", 0));
            d.e(sharedPreferences.getInt("unread_phone_count", 0));
            return 0;
        } catch (Exception e) {
            Log.e(f1748a, "create antispam context exception", e);
            return -1;
        }
    }
}
