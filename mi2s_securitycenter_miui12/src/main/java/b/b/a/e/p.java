package b.b.a.e;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.miui.permcenter.compact.MiuiSettingsCompat;
import com.xiaomi.stat.MiStat;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import miui.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class p {

    /* renamed from: a  reason: collision with root package name */
    private static final String f1462a = "p";

    /* renamed from: b  reason: collision with root package name */
    private static String f1463b = "((https?|ftp|file)://)?(?<![@|[A-Za-z0-9_]])([[A-Za-z0-9_]-_]+[.])+([a-zA-Z]+)(:[1-9]\\d*)?([/][[A-Za-z0-9_]+&#%?=.~_|!]*)*";

    /* renamed from: c  reason: collision with root package name */
    private static String f1464c = "(((http(s?)|ftp|file):)?//)?((25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})(:[1-9]\\d*)?([/][[A-Za-z0-9_]+&#%?=.~_|!]*)*";

    /* renamed from: d  reason: collision with root package name */
    private static String f1465d = "\\d{7,15}";
    private static final String[] e = {"cn", "hk", "mo", "tw"};
    private static final String[] f = {"com", "net", "gov", "org", "edu"};

    public static String a(String str) {
        boolean z;
        boolean z2;
        if (str == null || str == "") {
            return str;
        }
        int indexOf = str.indexOf("//");
        int i = indexOf == -1 ? 0 : indexOf + 2;
        int indexOf2 = str.indexOf("/", i);
        if (indexOf2 == -1) {
            indexOf2 = str.length();
        }
        String substring = str.substring(i, indexOf2);
        if (substring.indexOf(":") != -1 && d(substring.substring(substring.indexOf(":") + 1, substring.length()))) {
            substring = substring.substring(0, substring.indexOf(":"));
        }
        String[] split = substring.split("\\.");
        if (split.length > 2) {
            String[] strArr = e;
            int length = strArr.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    z2 = false;
                    break;
                } else if (strArr[i2].equals(split[split.length - 1])) {
                    z2 = true;
                    break;
                } else {
                    i2++;
                }
            }
            if (z2) {
                for (String equals : f) {
                    if (equals.equals(split[split.length - 2])) {
                        return a(split, '.', split.length - 3, split.length - 1);
                    }
                }
            }
        }
        if (split.length < 2) {
            return substring;
        }
        if (split.length == 4) {
            int length2 = split.length;
            int i3 = 0;
            while (true) {
                if (i3 >= length2) {
                    z = true;
                    break;
                } else if (!d(split[i3])) {
                    z = false;
                    break;
                } else {
                    i3++;
                }
            }
            if (z) {
                return a(split, '.', 0, split.length - 1);
            }
        }
        return a(split, '.', split.length - 2, split.length - 1);
    }

    public static String a(Object[] objArr, char c2, int i, int i2) {
        int i3;
        if (objArr == null || (i3 = i2 - i) <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder(i3 * 16);
        for (int i4 = i; i4 <= i2; i4++) {
            if (i4 > i) {
                sb.append(c2);
            }
            if (objArr[i4] != null) {
                sb.append(objArr[i4]);
            }
        }
        return sb.toString();
    }

    public static ArrayList<String> a(Context context) {
        String str;
        String str2;
        JSONArray jSONArray;
        ArrayList<String> arrayList = new ArrayList<>();
        InputStream inputStream = null;
        try {
            String cloudDataString = MiuiSettingsCompat.getCloudDataString(context.getContentResolver(), "antispam_white_url", MiStat.Param.CONTENT, (String) null);
            if (!TextUtils.isEmpty(cloudDataString)) {
                jSONArray = new JSONArray(cloudDataString);
            } else {
                inputStream = context.getAssets().open("white_url.json");
                jSONArray = new JSONObject(IOUtils.toString(inputStream)).getJSONArray(MiStat.Param.CONTENT);
            }
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(jSONArray.getString(i));
            }
        } catch (JSONException e2) {
            e = e2;
            str = f1462a;
            str2 = "JSONException when get white urls :";
        } catch (Exception e3) {
            e = e3;
            str = f1462a;
            str2 = "Exception when get white urls :";
        } catch (Throwable th) {
            IOUtils.closeQuietly((InputStream) null);
            throw th;
        }
        IOUtils.closeQuietly(inputStream);
        return arrayList;
        Log.e(str, str2, e);
        IOUtils.closeQuietly(inputStream);
        return arrayList;
    }

    public static boolean a(Context context, String str) {
        String str2;
        String str3;
        JSONArray jSONArray;
        InputStream inputStream = null;
        try {
            String cloudDataString = MiuiSettingsCompat.getCloudDataString(context.getContentResolver(), "antispam_service_number", MiStat.Param.CONTENT, (String) null);
            if (!TextUtils.isEmpty(cloudDataString)) {
                jSONArray = new JSONArray(cloudDataString);
            } else {
                inputStream = context.getAssets().open("service_num.json");
                jSONArray = new JSONObject(IOUtils.toString(inputStream)).getJSONArray(MiStat.Param.CONTENT);
            }
            for (int i = 0; i < jSONArray.length(); i++) {
                if (jSONArray.getString(i).equals(str)) {
                    IOUtils.closeQuietly(inputStream);
                    return true;
                }
            }
        } catch (JSONException e2) {
            e = e2;
            str2 = f1462a;
            str3 = "JSONException when judge is target service number :";
        } catch (Exception e3) {
            e = e3;
            str2 = f1462a;
            str3 = "Exception when judge is target service number :";
        } catch (Throwable th) {
            IOUtils.closeQuietly((InputStream) null);
            throw th;
        }
        IOUtils.closeQuietly(inputStream);
        return false;
        Log.e(str2, str3, e);
        IOUtils.closeQuietly(inputStream);
        return false;
    }

    public static ArrayList<String> b(String str) {
        ArrayList<String> arrayList = new ArrayList<>();
        Matcher matcher = Pattern.compile(f1463b, 2).matcher(str);
        ArrayList<String> arrayList2 = new ArrayList<>();
        int i = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (start > i) {
                arrayList2.add(str.substring(i, start));
            }
            arrayList.add(str.substring(start, end));
            i = end;
        }
        if (i < str.length() - 1) {
            arrayList2.add(str.substring(i));
        }
        ArrayList arrayList3 = new ArrayList();
        for (String str2 : arrayList2) {
            Matcher matcher2 = Pattern.compile(f1464c, 2).matcher(str2);
            int i2 = 0;
            while (matcher2.find()) {
                int start2 = matcher2.start();
                int end2 = matcher2.end();
                if (start2 > i2) {
                    arrayList3.add(str2.substring(i2, start2));
                }
                arrayList.add(str2.substring(start2, end2));
                i2 = end2;
            }
            if (i2 < str2.length() - 1) {
                arrayList3.add(str2.substring(i2));
            }
        }
        return arrayList;
    }

    public static boolean c(String str) {
        return Pattern.compile(f1465d, 2).matcher(str).find();
    }

    private static boolean d(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) < '0' || str.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }
}
