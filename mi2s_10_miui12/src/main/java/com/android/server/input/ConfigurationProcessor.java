package com.android.server.input;

class ConfigurationProcessor {
    private static final String TAG = "ConfigurationProcessor";

    ConfigurationProcessor() {
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003a, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x003b, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003e, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.util.List<java.lang.String> processExcludedDeviceNames(java.io.InputStream r5) throws java.lang.Exception {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.io.InputStreamReader r1 = new java.io.InputStreamReader
            r1.<init>(r5)
            org.xmlpull.v1.XmlPullParser r2 = android.util.Xml.newPullParser()     // Catch:{ all -> 0x0038 }
            r2.setInput(r1)     // Catch:{ all -> 0x0038 }
            java.lang.String r3 = "devices"
            com.android.internal.util.XmlUtils.beginDocument(r2, r3)     // Catch:{ all -> 0x0038 }
        L_0x0016:
            com.android.internal.util.XmlUtils.nextElement(r2)     // Catch:{ all -> 0x0038 }
            java.lang.String r3 = "device"
            java.lang.String r4 = r2.getName()     // Catch:{ all -> 0x0038 }
            boolean r3 = r3.equals(r4)     // Catch:{ all -> 0x0038 }
            r4 = 0
            if (r3 != 0) goto L_0x002b
            $closeResource(r4, r1)
            return r0
        L_0x002b:
            java.lang.String r3 = "name"
            java.lang.String r3 = r2.getAttributeValue(r4, r3)     // Catch:{ all -> 0x0038 }
            if (r3 == 0) goto L_0x0037
            r0.add(r3)     // Catch:{ all -> 0x0038 }
        L_0x0037:
            goto L_0x0016
        L_0x0038:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x003a }
        L_0x003a:
            r3 = move-exception
            $closeResource(r2, r1)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.input.ConfigurationProcessor.processExcludedDeviceNames(java.io.InputStream):java.util.List");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        } else {
            x1.close();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0066, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0067, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x006a, code lost:
        throw r3;
     */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.util.List<android.util.Pair<java.lang.String, java.lang.String>> processInputPortAssociations(java.io.InputStream r9) throws java.lang.Exception {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.io.InputStreamReader r1 = new java.io.InputStreamReader
            r1.<init>(r9)
            org.xmlpull.v1.XmlPullParser r2 = android.util.Xml.newPullParser()     // Catch:{ all -> 0x0064 }
            r2.setInput(r1)     // Catch:{ all -> 0x0064 }
            java.lang.String r3 = "ports"
            com.android.internal.util.XmlUtils.beginDocument(r2, r3)     // Catch:{ all -> 0x0064 }
        L_0x0017:
            com.android.internal.util.XmlUtils.nextElement(r2)     // Catch:{ all -> 0x0064 }
            java.lang.String r3 = r2.getName()     // Catch:{ all -> 0x0064 }
            java.lang.String r4 = "port"
            boolean r4 = r4.equals(r3)     // Catch:{ all -> 0x0064 }
            r5 = 0
            if (r4 != 0) goto L_0x002d
            $closeResource(r5, r1)
            return r0
        L_0x002d:
            java.lang.String r4 = "input"
            java.lang.String r4 = r2.getAttributeValue(r5, r4)     // Catch:{ all -> 0x0064 }
            java.lang.String r6 = "display"
            java.lang.String r5 = r2.getAttributeValue(r5, r6)     // Catch:{ all -> 0x0064 }
            boolean r6 = android.text.TextUtils.isEmpty(r4)     // Catch:{ all -> 0x0064 }
            java.lang.String r7 = "ConfigurationProcessor"
            if (r6 != 0) goto L_0x005e
            boolean r6 = android.text.TextUtils.isEmpty(r5)     // Catch:{ all -> 0x0064 }
            if (r6 == 0) goto L_0x0049
            goto L_0x005e
        L_0x0049:
            java.lang.Integer.parseUnsignedInt(r5)     // Catch:{ NumberFormatException -> 0x0057 }
            android.util.Pair r6 = new android.util.Pair     // Catch:{ all -> 0x0064 }
            r6.<init>(r4, r5)     // Catch:{ all -> 0x0064 }
            r0.add(r6)     // Catch:{ all -> 0x0064 }
            goto L_0x0017
        L_0x0057:
            r6 = move-exception
            java.lang.String r8 = "Display port should be an integer"
            android.util.Slog.wtf(r7, r8)     // Catch:{ all -> 0x0064 }
            goto L_0x0017
        L_0x005e:
            java.lang.String r6 = "Ignoring incomplete entry"
            android.util.Slog.wtf(r7, r6)     // Catch:{ all -> 0x0064 }
            goto L_0x0017
        L_0x0064:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0066 }
        L_0x0066:
            r3 = move-exception
            $closeResource(r2, r1)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.input.ConfigurationProcessor.processInputPortAssociations(java.io.InputStream):java.util.List");
    }
}
