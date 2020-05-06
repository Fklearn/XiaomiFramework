package com.android.server.firewall;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class PortFilter implements Filter {
    private static final String ATTR_EQUALS = "equals";
    private static final String ATTR_MAX = "max";
    private static final String ATTR_MIN = "min";
    public static final FilterFactory FACTORY = new FilterFactory("port") {
        public Filter newFilter(XmlPullParser parser) throws IOException, XmlPullParserException {
            int lowerBound = -1;
            int upperBound = -1;
            String equalsValue = parser.getAttributeValue((String) null, PortFilter.ATTR_EQUALS);
            if (equalsValue != null) {
                try {
                    int value = Integer.parseInt(equalsValue);
                    lowerBound = value;
                    upperBound = value;
                } catch (NumberFormatException e) {
                    throw new XmlPullParserException("Invalid port value: " + equalsValue, parser, (Throwable) null);
                }
            }
            String lowerBoundString = parser.getAttributeValue((String) null, PortFilter.ATTR_MIN);
            String upperBoundString = parser.getAttributeValue((String) null, PortFilter.ATTR_MAX);
            if (!(lowerBoundString == null && upperBoundString == null)) {
                if (equalsValue == null) {
                    if (lowerBoundString != null) {
                        try {
                            lowerBound = Integer.parseInt(lowerBoundString);
                        } catch (NumberFormatException e2) {
                            throw new XmlPullParserException("Invalid minimum port value: " + lowerBoundString, parser, (Throwable) null);
                        }
                    }
                    if (upperBoundString != null) {
                        try {
                            upperBound = Integer.parseInt(upperBoundString);
                        } catch (NumberFormatException e3) {
                            throw new XmlPullParserException("Invalid maximum port value: " + upperBoundString, parser, (Throwable) null);
                        }
                    }
                } else {
                    throw new XmlPullParserException("Port filter cannot use both equals and range filtering", parser, (Throwable) null);
                }
            }
            return new PortFilter(lowerBound, upperBound);
        }
    };
    private static final int NO_BOUND = -1;
    private final int mLowerBound;
    private final int mUpperBound;

    private PortFilter(int lowerBound, int upperBound) {
        this.mLowerBound = lowerBound;
        this.mUpperBound = upperBound;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000e, code lost:
        r3 = r4.mLowerBound;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0014, code lost:
        r3 = r4.mUpperBound;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean matches(com.android.server.firewall.IntentFirewall r5, android.content.ComponentName r6, android.content.Intent r7, int r8, int r9, java.lang.String r10, int r11) {
        /*
            r4 = this;
            r0 = -1
            android.net.Uri r1 = r7.getData()
            if (r1 == 0) goto L_0x000b
            int r0 = r1.getPort()
        L_0x000b:
            r2 = -1
            if (r0 == r2) goto L_0x001c
            int r3 = r4.mLowerBound
            if (r3 == r2) goto L_0x0014
            if (r3 > r0) goto L_0x001c
        L_0x0014:
            int r3 = r4.mUpperBound
            if (r3 == r2) goto L_0x001a
            if (r3 < r0) goto L_0x001c
        L_0x001a:
            r2 = 1
            goto L_0x001d
        L_0x001c:
            r2 = 0
        L_0x001d:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.firewall.PortFilter.matches(com.android.server.firewall.IntentFirewall, android.content.ComponentName, android.content.Intent, int, int, java.lang.String, int):boolean");
    }
}
