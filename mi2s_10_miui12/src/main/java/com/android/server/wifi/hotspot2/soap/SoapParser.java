package com.android.server.wifi.hotspot2.soap;

public class SoapParser {
    /* JADX WARNING: Removed duplicated region for block: B:12:0x002b  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0034  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.android.server.wifi.hotspot2.soap.SppResponseMessage getResponse(org.ksoap2.serialization.SoapObject r4) {
        /*
            java.lang.String r0 = r4.getName()
            int r1 = r0.hashCode()
            r2 = 1679330383(0x64188c4f, float:1.1256062E22)
            r3 = 1
            if (r1 == r2) goto L_0x001e
            r2 = 1786714445(0x6a7f194d, float:7.709889E25)
            if (r1 == r2) goto L_0x0014
        L_0x0013:
            goto L_0x0028
        L_0x0014:
            java.lang.String r1 = "sppPostDevDataResponse"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0013
            r0 = 0
            goto L_0x0029
        L_0x001e:
            java.lang.String r1 = "sppExchangeComplete"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0013
            r0 = r3
            goto L_0x0029
        L_0x0028:
            r0 = -1
        L_0x0029:
            if (r0 == 0) goto L_0x0034
            if (r0 == r3) goto L_0x002f
            r0 = 0
            goto L_0x0039
        L_0x002f:
            com.android.server.wifi.hotspot2.soap.ExchangeCompleteMessage r0 = com.android.server.wifi.hotspot2.soap.ExchangeCompleteMessage.createInstance(r4)
            goto L_0x0039
        L_0x0034:
            com.android.server.wifi.hotspot2.soap.PostDevDataResponse r0 = com.android.server.wifi.hotspot2.soap.PostDevDataResponse.createInstance(r4)
        L_0x0039:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.hotspot2.soap.SoapParser.getResponse(org.ksoap2.serialization.SoapObject):com.android.server.wifi.hotspot2.soap.SppResponseMessage");
    }
}
