package miui.cloud.log;

import java.util.regex.Pattern;

public class PrivacyFilter {
    private static Pattern sIpv4Pattern;
    private static Pattern sIpv6Pattern;

    static {
        initIpv4PatternString();
        initIpv6PatternString();
    }

    public static String filterPrivacyLog(String str) {
        return replacePrivacyLog(replacePrivacyLog(str, sIpv4Pattern, "@IPV4"), sIpv6Pattern, "@IPV6");
    }

    private static void initIpv4PatternString() {
        sIpv4Pattern = Pattern.compile("(" + "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])" + "\\.){3}" + "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])");
    }

    private static void initIpv6PatternString() {
        sIpv6Pattern = Pattern.compile("(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){2,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){2,7})|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))");
    }

    private static String replacePrivacyLog(String str, Pattern pattern, String str2) {
        return pattern == null ? str : pattern.matcher(str).replaceAll(str2);
    }
}
