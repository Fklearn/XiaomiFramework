package miui.telephony.phonenumber;

import android.text.TextUtils;

public class Prefix {
    public static final String EMPTY = "";
    public static final String PREFIX_10193 = "10193";
    public static final String PREFIX_11808 = "11808";
    public static final String PREFIX_12520 = "12520";
    public static final String PREFIX_125831 = "125831";
    public static final String PREFIX_125832 = "125832";
    public static final String PREFIX_125833 = "125833";
    public static final String PREFIX_12593 = "12593";
    public static final String PREFIX_17900 = "17900";
    public static final String PREFIX_17901 = "17901";
    public static final String PREFIX_17908 = "17908";
    public static final String PREFIX_17909 = "17909";
    public static final String PREFIX_17911 = "17911";
    public static final String PREFIX_17950 = "17950";
    public static final String PREFIX_17951 = "17951";
    public static final String PREFIX_17960 = "17960";
    public static final String PREFIX_17961 = "17961";
    public static final String PREFIX_17968 = "17968";
    public static final String PREFIX_17969 = "17969";
    public static final String PREFIX_17990 = "17990";
    public static final String PREFIX_17991 = "17991";
    public static final String PREFIX_17995 = "17995";
    public static final String PREFIX_17996 = "17996";
    public static final String[] SMS_PREFIXES = new String[0];

    /* JADX WARNING: Removed duplicated region for block: B:144:0x0223 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String parse(java.lang.StringBuffer r8, int r9, int r10) {
        /*
            java.lang.String r0 = ""
            if (r10 > 0) goto L_0x0005
            return r0
        L_0x0005:
            char r1 = r8.charAt(r9)
            r2 = 49
            r3 = 57
            r4 = 48
            switch(r1) {
                case 49: goto L_0x0039;
                case 50: goto L_0x0014;
                case 51: goto L_0x0014;
                case 52: goto L_0x0014;
                case 53: goto L_0x0014;
                case 54: goto L_0x0014;
                case 55: goto L_0x0014;
                case 56: goto L_0x0014;
                default: goto L_0x0012;
            }
        L_0x0012:
            goto L_0x0223
        L_0x0014:
            r1 = 10
            if (r10 < r1) goto L_0x0223
            int r1 = r9 + 1
            char r1 = r8.charAt(r1)
            if (r1 == r4) goto L_0x0022
            goto L_0x0223
        L_0x0022:
            int r1 = r9 + 2
            char r1 = r8.charAt(r1)
            if (r1 < r2) goto L_0x0223
            int r1 = r9 + 2
            char r1 = r8.charAt(r1)
            if (r1 > r3) goto L_0x0223
            int r0 = r9 + 3
            java.lang.String r0 = r8.substring(r9, r0)
            return r0
        L_0x0039:
            r1 = 4
            if (r10 <= r1) goto L_0x0223
            int r1 = r9 + 1
            char r1 = r8.charAt(r1)
            r5 = 55
            r6 = 53
            r7 = 56
            if (r1 == r5) goto L_0x00fc
            r5 = 51
            switch(r1) {
                case 48: goto L_0x00e1;
                case 49: goto L_0x00c6;
                case 50: goto L_0x0051;
                default: goto L_0x004f;
            }
        L_0x004f:
            goto L_0x0223
        L_0x0051:
            int r1 = r9 + 2
            char r1 = r8.charAt(r1)
            if (r1 != r6) goto L_0x0223
            r1 = 5
            if (r10 <= r1) goto L_0x0077
            int r4 = r9 + 3
            char r4 = r8.charAt(r4)
            if (r4 != r7) goto L_0x0077
            int r4 = r9 + 4
            char r4 = r8.charAt(r4)
            if (r4 != r5) goto L_0x0077
            int r4 = r9 + 5
            char r4 = r8.charAt(r4)
            if (r4 != r2) goto L_0x0077
            java.lang.String r0 = "125831"
            return r0
        L_0x0077:
            if (r10 <= r1) goto L_0x0096
            int r2 = r9 + 3
            char r2 = r8.charAt(r2)
            if (r2 != r7) goto L_0x0096
            int r2 = r9 + 4
            char r2 = r8.charAt(r2)
            if (r2 != r5) goto L_0x0096
            int r2 = r9 + 5
            char r2 = r8.charAt(r2)
            r4 = 50
            if (r2 != r4) goto L_0x0096
            java.lang.String r0 = "125832"
            return r0
        L_0x0096:
            if (r10 <= r1) goto L_0x00b3
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            if (r1 != r7) goto L_0x00b3
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r5) goto L_0x00b3
            int r1 = r9 + 5
            char r1 = r8.charAt(r1)
            if (r1 != r5) goto L_0x00b3
            java.lang.String r0 = "125833"
            return r0
        L_0x00b3:
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            if (r1 != r3) goto L_0x0223
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r5) goto L_0x0223
            java.lang.String r0 = "12593"
            return r0
        L_0x00c6:
            int r1 = r9 + 2
            char r1 = r8.charAt(r1)
            if (r1 != r7) goto L_0x0223
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            if (r1 != r4) goto L_0x0223
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r7) goto L_0x0223
            java.lang.String r0 = "11808"
            return r0
        L_0x00e1:
            int r1 = r9 + 2
            char r1 = r8.charAt(r1)
            if (r1 != r2) goto L_0x0223
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            if (r1 != r3) goto L_0x0223
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r5) goto L_0x0223
            java.lang.String r0 = "10193"
            return r0
        L_0x00fc:
            int r1 = r9 + 2
            char r1 = r8.charAt(r1)
            if (r1 != r3) goto L_0x0223
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            if (r1 != r4) goto L_0x0117
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r4) goto L_0x0117
            java.lang.String r0 = "17900"
            return r0
        L_0x0117:
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            if (r1 != r4) goto L_0x012a
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r2) goto L_0x012a
            java.lang.String r0 = "17901"
            return r0
        L_0x012a:
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            if (r1 != r4) goto L_0x013d
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r7) goto L_0x013d
            java.lang.String r0 = "17908"
            return r0
        L_0x013d:
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            if (r1 != r4) goto L_0x0150
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r3) goto L_0x0150
            java.lang.String r0 = "17909"
            return r0
        L_0x0150:
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            if (r1 != r2) goto L_0x0163
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r2) goto L_0x0163
            java.lang.String r0 = "17911"
            return r0
        L_0x0163:
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            if (r1 != r6) goto L_0x0176
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r4) goto L_0x0176
            java.lang.String r0 = "17950"
            return r0
        L_0x0176:
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            if (r1 != r6) goto L_0x0189
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r2) goto L_0x0189
            java.lang.String r0 = "17951"
            return r0
        L_0x0189:
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            r5 = 54
            if (r1 != r5) goto L_0x019e
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r4) goto L_0x019e
            java.lang.String r0 = "17960"
            return r0
        L_0x019e:
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            if (r1 != r5) goto L_0x01b1
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r2) goto L_0x01b1
            java.lang.String r0 = "17961"
            return r0
        L_0x01b1:
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            if (r1 != r5) goto L_0x01c4
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r7) goto L_0x01c4
            java.lang.String r0 = "17968"
            return r0
        L_0x01c4:
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            if (r1 != r5) goto L_0x01d7
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r3) goto L_0x01d7
            java.lang.String r0 = "17969"
            return r0
        L_0x01d7:
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            if (r1 != r3) goto L_0x01ea
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r4) goto L_0x01ea
            java.lang.String r0 = "17990"
            return r0
        L_0x01ea:
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            if (r1 != r3) goto L_0x01fd
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r2) goto L_0x01fd
            java.lang.String r0 = "17991"
            return r0
        L_0x01fd:
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            if (r1 != r3) goto L_0x0210
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r6) goto L_0x0210
            java.lang.String r0 = "17995"
            return r0
        L_0x0210:
            int r1 = r9 + 3
            char r1 = r8.charAt(r1)
            if (r1 != r3) goto L_0x0223
            int r1 = r9 + 4
            char r1 = r8.charAt(r1)
            if (r1 != r5) goto L_0x0223
            java.lang.String r0 = "17996"
            return r0
        L_0x0223:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.telephony.phonenumber.Prefix.parse(java.lang.StringBuffer, int, int):java.lang.String");
    }

    public static boolean isSmsPrefix(String prefix) {
        if (!TextUtils.isEmpty(prefix)) {
            for (String sms : SMS_PREFIXES) {
                if (sms.equals(prefix)) {
                    return true;
                }
            }
        }
        return false;
    }
}
