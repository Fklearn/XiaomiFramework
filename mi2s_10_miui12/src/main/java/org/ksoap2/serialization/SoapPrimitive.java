package org.ksoap2.serialization;

public class SoapPrimitive extends AttributeContainer {
    public static final Object NullNilElement = new Object();
    public static final Object NullSkip = new Object();
    protected String name;
    protected String namespace;
    protected Object value;

    public SoapPrimitive(String namespace2, String name2, Object value2) {
        this.namespace = namespace2;
        this.name = name2;
        this.value = value2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0025, code lost:
        r2 = r5.value;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0014, code lost:
        r2 = r5.namespace;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r6) {
        /*
            r5 = this;
            boolean r0 = r6 instanceof org.ksoap2.serialization.SoapPrimitive
            r1 = 0
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            r0 = r6
            org.ksoap2.serialization.SoapPrimitive r0 = (org.ksoap2.serialization.SoapPrimitive) r0
            java.lang.String r2 = r5.name
            java.lang.String r3 = r0.name
            boolean r2 = r2.equals(r3)
            r3 = 1
            if (r2 == 0) goto L_0x0038
            java.lang.String r2 = r5.namespace
            if (r2 != 0) goto L_0x001d
            java.lang.String r2 = r0.namespace
            if (r2 != 0) goto L_0x0038
            goto L_0x0025
        L_0x001d:
            java.lang.String r4 = r0.namespace
            boolean r2 = r2.equals(r4)
            if (r2 == 0) goto L_0x0038
        L_0x0025:
            java.lang.Object r2 = r5.value
            if (r2 != 0) goto L_0x002e
            java.lang.Object r2 = r0.value
            if (r2 != 0) goto L_0x0038
            goto L_0x0036
        L_0x002e:
            java.lang.Object r4 = r0.value
            boolean r2 = r2.equals(r4)
            if (r2 == 0) goto L_0x0038
        L_0x0036:
            r2 = r3
            goto L_0x0039
        L_0x0038:
            r2 = r1
        L_0x0039:
            if (r2 == 0) goto L_0x0042
            boolean r4 = r5.attributesAreEqual(r0)
            if (r4 == 0) goto L_0x0042
            r1 = r3
        L_0x0042:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.ksoap2.serialization.SoapPrimitive.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        int hashCode = this.name.hashCode();
        String str = this.namespace;
        return hashCode ^ (str == null ? 0 : str.hashCode());
    }

    public String toString() {
        Object obj = this.value;
        if (obj != null) {
            return obj.toString();
        }
        return null;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }
}
