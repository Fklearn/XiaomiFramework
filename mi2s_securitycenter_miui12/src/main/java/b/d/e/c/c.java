package b.d.e.c;

import b.d.e.b;
import com.miui.maml.elements.TimepanelScreenElement;
import java.util.ArrayList;
import java.util.List;

public class c extends f {

    /* renamed from: b  reason: collision with root package name */
    private static final String[] f2193b = {"0123456789", "０１２３４５６７８９", "零一二三四五六七八九", "零壹贰叁肆伍陆柒捌玖"};

    /* renamed from: c  reason: collision with root package name */
    private static final String[] f2194c = {"①②③④⑤⑥⑦⑧⑨", "⒈⒉⒊⒋⒌⒍⒎⒏⒐", "⓵⓶⓷⓸⓹⓺⓻⓼⓽"};

    /* renamed from: d  reason: collision with root package name */
    private static final String[][] f2195d = {new String[]{"I|loOBbgqzZ", "11100869922"}, new String[]{"｜ｏｑｌｂＯＩＢＺｚ", "1091601822"}};
    protected static final String[] e = {"0123456789", "０１２３４５６７８９", "①②③④⑤⑥⑦⑧⑨", "⒈⒉⒊⒋⒌⒍⒎⒏⒐", "⓵⓶⓷⓸⓹⓺⓻⓼⓽", "零一二三四五六七八九", "零壹贰叁肆伍陆柒捌玖"};
    protected static final int[] f = {15, 16, 18, 19};
    private static final String[] g = {"BankCard", "Phone", "PhoneMobile", "Phone400", "Range", TimepanelScreenElement.TAG_NAME, "Confus"};
    protected ArrayList<b.a> h = new ArrayList<>();
    protected int[] i = new int[a()];

    protected static char a(char c2) {
        for (String indexOf : f2193b) {
            int indexOf2 = indexOf.indexOf(c2);
            if (indexOf2 != -1) {
                return "0123456789".charAt(indexOf2);
            }
        }
        for (String indexOf3 : f2194c) {
            int indexOf4 = indexOf3.indexOf(c2);
            if (indexOf4 != -1) {
                return "0123456789".charAt(indexOf4 + 1);
            }
        }
        int i2 = 0;
        while (true) {
            String[][] strArr = f2195d;
            if (i2 >= strArr.length) {
                return c2;
            }
            int indexOf5 = strArr[i2][0].indexOf(c2);
            if (indexOf5 != -1) {
                return f2195d[i2][1].charAt(indexOf5);
            }
            i2++;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0161, code lost:
        if (b.d.e.c.a.a(b.d.e.c.a.a(r1)) != false) goto L_0x0163;
     */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x01c4  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x011c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int a(java.lang.String r20, int r21, boolean r22, char r23, boolean r24, char r25) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r2 = r23
            r3 = r25
            int r4 = r20.length()
            r5 = 2
            r6 = -1
            if (r4 > r5) goto L_0x0011
            return r6
        L_0x0011:
            boolean r4 = c((java.lang.String) r20)
            r7 = 1
            r4 = r4 ^ r7
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            java.lang.String r9 = "400"
            r11 = 4
            java.lang.String r12 = "0123456789"
            r14 = 3
            r15 = 0
            if (r22 == 0) goto L_0x0112
            r10 = r15
            r13 = r10
            r16 = r13
        L_0x0029:
            int r7 = r20.length()
            if (r10 < r7) goto L_0x00f7
            java.lang.String r1 = r1.substring(r13)
            r8.add(r1)
            r1 = r15
        L_0x0037:
            int r7 = r8.size()
            if (r1 < r7) goto L_0x00e6
            java.lang.String r1 = "-—一~"
            int r1 = r1.indexOf(r2)
            if (r1 == r6) goto L_0x0119
            int r1 = r8.size()
            if (r1 != r5) goto L_0x0119
            java.lang.Object r1 = r8.get(r15)
            java.lang.String r1 = (java.lang.String) r1
            char r1 = r1.charAt(r15)
            char r2 = r12.charAt(r15)
            if (r1 != r2) goto L_0x0094
            if (r24 != 0) goto L_0x0094
            java.lang.Object r1 = r8.get(r15)
            java.lang.String r1 = (java.lang.String) r1
            int r1 = r1.length()
            if (r1 == r14) goto L_0x0075
            java.lang.Object r1 = r8.get(r15)
            java.lang.String r1 = (java.lang.String) r1
            int r1 = r1.length()
            if (r1 != r11) goto L_0x0094
        L_0x0075:
            r1 = 1
            java.lang.Object r2 = r8.get(r1)
            java.lang.String r2 = (java.lang.String) r2
            int r2 = r2.length()
            r7 = 7
            if (r2 == r7) goto L_0x0091
            java.lang.Object r2 = r8.get(r1)
            java.lang.String r2 = (java.lang.String) r2
            int r1 = r2.length()
            r2 = 8
            if (r1 != r2) goto L_0x0094
        L_0x0091:
            r11 = 1
            goto L_0x011a
        L_0x0094:
            java.lang.Object r1 = r8.get(r15)
            java.lang.String r1 = (java.lang.String) r1
            r2 = 1
            java.lang.Object r7 = r8.get(r2)
            java.lang.String r7 = (java.lang.String) r7
            if (r24 == 0) goto L_0x00af
            r2 = 46
            if (r3 == r2) goto L_0x00af
            java.lang.String r1 = r1.replace(r3, r2)
            java.lang.String r7 = r7.replace(r3, r2)
        L_0x00af:
            boolean r2 = e((java.lang.String) r1)
            if (r2 == 0) goto L_0x0119
            boolean r2 = e((java.lang.String) r7)
            if (r2 == 0) goto L_0x0119
            java.lang.Double r1 = java.lang.Double.valueOf(r1)
            double r1 = r1.doubleValue()
            java.lang.Double r10 = java.lang.Double.valueOf(r7)
            double r17 = r10.doubleValue()
            int r1 = (r1 > r17 ? 1 : (r1 == r17 ? 0 : -1))
            if (r1 >= 0) goto L_0x0119
            java.lang.Object r1 = r8.get(r15)
            java.lang.String r1 = (java.lang.String) r1
            boolean r1 = r1.equals(r9)
            if (r1 == 0) goto L_0x011a
            int r1 = r7.length()
            r2 = 7
            if (r1 != r2) goto L_0x011a
            if (r24 != 0) goto L_0x011a
            r11 = r14
            goto L_0x011a
        L_0x00e6:
            java.lang.Object r7 = r8.get(r1)
            java.lang.String r7 = (java.lang.String) r7
            java.lang.String r7 = b((java.lang.String) r7)
            r8.set(r1, r7)
            int r1 = r1 + 1
            goto L_0x0037
        L_0x00f7:
            char r7 = r1.charAt(r10)
            if (r7 != r2) goto L_0x010c
            if (r16 != 0) goto L_0x0108
            java.lang.String r7 = r1.substring(r13, r10)
            r8.add(r7)
            r16 = 1
        L_0x0108:
            int r7 = r10 + 1
            r13 = r7
            goto L_0x010e
        L_0x010c:
            r16 = r15
        L_0x010e:
            int r10 = r10 + 1
            goto L_0x0029
        L_0x0112:
            java.lang.String r1 = b((java.lang.String) r20)
            r8.add(r1)
        L_0x0119:
            r11 = r6
        L_0x011a:
            if (r11 >= 0) goto L_0x01c1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.util.Iterator r2 = r8.iterator()
        L_0x0125:
            boolean r7 = r2.hasNext()
            if (r7 != 0) goto L_0x01a9
            java.lang.String r1 = r1.toString()
            int r2 = r1.length()
            r7 = 19
            if (r2 <= r7) goto L_0x0139
            goto L_0x01c2
        L_0x0139:
            r10 = 15
            if (r2 == r10) goto L_0x019d
            r10 = 16
            if (r2 == r10) goto L_0x019d
            if (r2 != r7) goto L_0x0144
            goto L_0x019d
        L_0x0144:
            r7 = 12
            if (r2 <= r7) goto L_0x014a
            goto L_0x01c2
        L_0x014a:
            r7 = 11
            if (r2 < r7) goto L_0x0165
            char r2 = r1.charAt(r15)
            char r3 = r12.charAt(r15)
            if (r2 != r3) goto L_0x0159
            r11 = 1
        L_0x0159:
            int r1 = b.d.e.c.a.a((java.lang.String) r1)
            boolean r1 = b.d.e.c.a.a((int) r1)
            if (r1 == 0) goto L_0x01c1
        L_0x0163:
            r6 = r5
            goto L_0x01c2
        L_0x0165:
            r5 = 10
            if (r2 != r5) goto L_0x0175
            java.lang.String r1 = r1.substring(r15, r14)
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x01c1
            r6 = r14
            goto L_0x01c2
        L_0x0175:
            int r5 = r8.size()
            r7 = 1
            if (r5 != r7) goto L_0x0188
            if (r24 == 0) goto L_0x0188
            java.lang.String r5 = ":："
            int r3 = r5.indexOf(r3)
            if (r3 == r6) goto L_0x0188
            r5 = 5
            goto L_0x0163
        L_0x0188:
            if (r24 != 0) goto L_0x01c2
            if (r22 != 0) goto L_0x01c2
            char r1 = r1.charAt(r15)
            r3 = 49
            if (r1 <= r3) goto L_0x01c2
            r7 = 7
            if (r2 == r7) goto L_0x019b
            r10 = 8
            if (r2 != r10) goto L_0x01c2
        L_0x019b:
            r6 = 1
            goto L_0x01c2
        L_0x019d:
            boolean r1 = d((java.lang.String) r1)
            if (r1 == 0) goto L_0x01c2
            if (r24 == 0) goto L_0x01a7
            if (r22 != 0) goto L_0x01c2
        L_0x01a7:
            r6 = r15
            goto L_0x01c2
        L_0x01a9:
            r7 = 7
            r10 = 8
            java.lang.Object r13 = r2.next()
            java.lang.String r13 = (java.lang.String) r13
            java.lang.String r5 = "\\."
            java.lang.String r6 = ""
            java.lang.String r5 = r13.replaceAll(r5, r6)
            r1.append(r5)
            r5 = 2
            r6 = -1
            goto L_0x0125
        L_0x01c1:
            r6 = r11
        L_0x01c2:
            if (r6 < 0) goto L_0x01d8
            int[] r1 = r0.i
            r2 = r1[r6]
            r3 = 1
            int r2 = r2 + r3
            r1[r6] = r2
            if (r4 != 0) goto L_0x01d0
            if (r21 == 0) goto L_0x01d8
        L_0x01d0:
            int[] r1 = r0.i
            r2 = 6
            r4 = r1[r2]
            int r4 = r4 + r3
            r1[r2] = r4
        L_0x01d8:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: b.d.e.c.c.a(java.lang.String, int, boolean, char, boolean, char):int");
    }

    protected static int b(char c2) {
        int i2 = 0;
        while (true) {
            String[] strArr = e;
            if (i2 >= strArr.length) {
                return -1;
            }
            if (strArr[i2].indexOf(c2) != -1) {
                return i2;
            }
            i2++;
        }
    }

    protected static String b(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < str.length(); i2++) {
            char charAt = str.charAt(i2);
            if (!d(charAt)) {
                sb.append(a(charAt));
            }
        }
        return sb.toString();
    }

    protected static boolean c(char c2) {
        return ("I|loOBbgqzZ".indexOf(c2) == -1 && "｜ｏｑｌｂＯＩＢＺｚ".indexOf(c2) == -1) ? false : true;
    }

    protected static boolean c(String str) {
        int b2;
        if (str == null || str.length() <= 0 || (b2 = b(str.charAt(0))) < 0) {
            return false;
        }
        for (int i2 = 1; i2 < str.length(); i2++) {
            char charAt = str.charAt(i2);
            if (b(charAt) != b2 && !d(charAt) && !h(charAt)) {
                return false;
            }
        }
        return true;
    }

    protected static boolean d(char c2) {
        return ",-—一~ 　".indexOf(c2) != -1 || c2 == 10 || c2 == 9 || c2 == 13;
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x002d  */
    /* JADX WARNING: Removed duplicated region for block: B:34:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean d(java.lang.String r8) {
        /*
            r0 = 0
            char r1 = r8.charAt(r0)
            r2 = 54
            r3 = 1
            r4 = 52
            if (r1 == r4) goto L_0x001d
            char r1 = r8.charAt(r0)
            r4 = 53
            if (r1 == r4) goto L_0x001d
            char r1 = r8.charAt(r0)
            if (r1 != r2) goto L_0x001b
            goto L_0x001d
        L_0x001b:
            r1 = r0
            goto L_0x002b
        L_0x001d:
            int r1 = r8.length()
            int r1 = r1 - r3
            r4 = r0
            r5 = r4
        L_0x0024:
            if (r1 >= 0) goto L_0x004f
            int r5 = r5 % 10
            if (r5 != 0) goto L_0x001b
            r1 = r3
        L_0x002b:
            if (r1 != 0) goto L_0x004e
            int r4 = r8.length()
            r5 = 19
            if (r4 == r5) goto L_0x003d
            int r4 = r8.length()
            r5 = 16
            if (r4 != r5) goto L_0x004e
        L_0x003d:
            char r1 = r8.charAt(r0)
            if (r1 != r2) goto L_0x004d
            char r8 = r8.charAt(r3)
            r1 = 50
            if (r8 != r1) goto L_0x004d
            r1 = r3
            goto L_0x004e
        L_0x004d:
            r1 = r0
        L_0x004e:
            return r1
        L_0x004f:
            char r6 = r8.charAt(r1)
            int r6 = r6 + -48
            if (r4 == 0) goto L_0x005f
            int r6 = r6 * 2
            r7 = 9
            if (r6 <= r7) goto L_0x005f
            int r6 = r6 + -9
        L_0x005f:
            int r5 = r5 + r6
            r4 = r4 ^ 1
            int r1 = r1 + -1
            goto L_0x0024
        */
        throw new UnsupportedOperationException("Method not decompiled: b.d.e.c.c.d(java.lang.String):boolean");
    }

    protected static boolean e(char c2) {
        return f(c2) && "零一二三四五六七八九".indexOf(c2) == -1 && "零壹贰叁肆伍陆柒捌玖".indexOf(c2) == -1;
    }

    private static boolean e(String str) {
        return str.indexOf(46) == str.lastIndexOf(46);
    }

    protected static boolean f(char c2) {
        return b(c2) >= 0 || c(c2);
    }

    protected static int g(char c2) {
        return "０１２３４５６７８９".indexOf(c2) != -1 ? 1 : 0;
    }

    private static boolean h(char c2) {
        return ":：".indexOf(c2) != -1 || c2 == '.';
    }

    /* access modifiers changed from: protected */
    public int a() {
        return g.length;
    }

    /* access modifiers changed from: protected */
    public List<String> a(String str) {
        String str2 = str;
        if (str2 == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        char c2 = '0';
        char c3 = '.';
        int i2 = -1;
        int i3 = 0;
        int i4 = 0;
        boolean z = false;
        int i5 = -1;
        boolean z2 = false;
        int i6 = 0;
        int i7 = -1;
        while (true) {
            boolean z3 = true;
            if (i6 >= str.length()) {
                break;
            }
            char charAt = str2.charAt(i6);
            if (f(charAt) && charAt != 19968) {
                if (i2 < 0) {
                    if (e(charAt)) {
                        i4 = g(charAt);
                        i2 = i6;
                    }
                }
                if (e(charAt)) {
                    i7 = i6;
                }
            } else if (i2 >= 0) {
                if (d(charAt) && !z) {
                    c2 = charAt;
                    i5 = i6;
                    z = true;
                } else if (h(charAt) && !z2) {
                    c3 = charAt;
                    z2 = true;
                } else if (!(charAt == c2 || charAt == c3)) {
                    if (i2 > i3) {
                        arrayList.add(str2.substring(i3, i2));
                    }
                    int i8 = i7 + 1;
                    String substring = str2.substring(i2, i8);
                    if (z) {
                        if (i5 >= i7) {
                            z3 = false;
                        }
                        z = z3;
                    }
                    String str3 = substring;
                    int a2 = a(substring, i4, z, c2, z2, c3);
                    if (a2 >= 0) {
                        this.h.add(new b.a(str3, a2));
                    }
                    i3 = i8;
                    i2 = -1;
                    i4 = 0;
                    z = false;
                    i5 = -1;
                    z2 = false;
                }
            }
            i6++;
        }
        if (i2 >= 0) {
            if (i2 > i3) {
                arrayList.add(str2.substring(i3, i2));
            }
            int i9 = i7 + 1;
            if (i9 < str.length()) {
                arrayList.add(str2.substring(i9));
            }
            String substring2 = str2.substring(i2, i9);
            int a3 = a(substring2, i4, z, c2, z2, c3);
            if (a3 >= 0) {
                this.h.add(new b.a(substring2, a3));
            }
        } else {
            arrayList.add(str2.substring(i3, str.length()));
        }
        return arrayList;
    }

    public boolean a(b bVar, int[] iArr, int i2) {
        int i3 = 0;
        boolean z = false;
        while (true) {
            int[] iArr2 = this.i;
            if (i3 >= iArr2.length) {
                break;
            }
            if (iArr2[i3] > 0) {
                z = true;
            }
            iArr[i2 + i3] = this.i[i3];
            i3++;
        }
        if (this.h.size() > 0) {
            bVar.n = new ArrayList();
            bVar.n.addAll(this.h);
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public void b() {
        super.b();
        this.h.clear();
        int i2 = 0;
        while (true) {
            int[] iArr = this.i;
            if (i2 < iArr.length) {
                iArr[i2] = 0;
                i2++;
            } else {
                return;
            }
        }
    }
}
