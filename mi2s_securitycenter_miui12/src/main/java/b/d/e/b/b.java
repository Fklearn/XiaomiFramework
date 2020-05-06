package b.d.e.b;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class b extends e {
    private static final String h = e.class.getName();
    List<int[]> i;
    int[] j;
    int[] k;

    b(JSONObject jSONObject) {
        super(jSONObject);
        try {
            if (jSONObject.has("body_len")) {
                JSONArray jSONArray = jSONObject.getJSONArray("body_len");
                for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                    String[] split = jSONArray.getString(i2).split("-");
                    if (split.length == 2) {
                        int parseInt = Integer.parseInt(split[0]);
                        int parseInt2 = Integer.parseInt(split[1]);
                        if (this.i == null) {
                            this.i = new ArrayList();
                        }
                        this.i.add(new int[]{parseInt, parseInt2});
                    }
                }
            }
            if (jSONObject.has("num_types")) {
                JSONArray jSONArray2 = jSONObject.getJSONArray("num_types");
                this.j = new int[jSONArray2.length()];
                for (int i3 = 0; i3 < jSONArray2.length(); i3++) {
                    this.j[i3] = jSONArray2.getInt(i3);
                }
            }
            if (jSONObject.has("struct_types")) {
                JSONArray jSONArray3 = jSONObject.getJSONArray("struct_types");
                this.k = new int[jSONArray3.length()];
                for (int i4 = 0; i4 < jSONArray3.length(); i4++) {
                    this.k[i4] = jSONArray3.getInt(i4);
                }
            }
        } catch (JSONException unused) {
            Log.e(h, "JSONException when decode KEY_TYPE features.");
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0073, code lost:
        if (r1 == false) goto L_0x0075;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean a(b.d.e.b r11) {
        /*
            r10 = this;
            java.lang.String r0 = r11.c()
            int r0 = r0.length()
            java.util.List<int[]> r1 = r10.i
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0038
            int r1 = r1.size()
            if (r1 <= 0) goto L_0x0038
            java.util.List<int[]> r1 = r10.i
            java.util.Iterator r1 = r1.iterator()
        L_0x001a:
            boolean r4 = r1.hasNext()
            if (r4 != 0) goto L_0x0022
            r0 = r3
            goto L_0x0035
        L_0x0022:
            java.lang.Object r4 = r1.next()
            int[] r4 = (int[]) r4
            int r5 = r4.length
            r6 = 2
            if (r5 != r6) goto L_0x001a
            r5 = r4[r3]
            if (r5 > r0) goto L_0x001a
            r4 = r4[r2]
            if (r0 > r4) goto L_0x001a
            r0 = r2
        L_0x0035:
            if (r0 != 0) goto L_0x0038
            return r3
        L_0x0038:
            int[] r0 = r10.j
            if (r0 == 0) goto L_0x0076
            int r0 = r0.length
            if (r0 <= 0) goto L_0x0076
            java.util.List<b.d.e.b$a> r0 = r11.n
            if (r0 == 0) goto L_0x0075
            int r0 = r0.size()
            if (r0 != 0) goto L_0x004a
            goto L_0x0075
        L_0x004a:
            r0 = r3
            r1 = r0
        L_0x004c:
            java.util.List<b.d.e.b$a> r4 = r11.n
            int r4 = r4.size()
            if (r0 >= r4) goto L_0x0073
            if (r1 == 0) goto L_0x0057
            goto L_0x0073
        L_0x0057:
            java.util.List<b.d.e.b$a> r4 = r11.n
            java.lang.Object r4 = r4.get(r0)
            b.d.e.b$a r4 = (b.d.e.b.a) r4
            int[] r5 = r10.j
            int r6 = r5.length
            r7 = r3
        L_0x0063:
            if (r7 < r6) goto L_0x0066
            goto L_0x006d
        L_0x0066:
            r8 = r5[r7]
            int r9 = r4.f2172a
            if (r9 != r8) goto L_0x0070
            r1 = r2
        L_0x006d:
            int r0 = r0 + 1
            goto L_0x004c
        L_0x0070:
            int r7 = r7 + 1
            goto L_0x0063
        L_0x0073:
            if (r1 != 0) goto L_0x0076
        L_0x0075:
            return r3
        L_0x0076:
            int[] r0 = r10.k
            if (r0 == 0) goto L_0x0094
            int r0 = r0.length
            if (r0 <= 0) goto L_0x0094
            int r0 = r11.i()
            int[] r1 = r10.k
            int r4 = r1.length
            r5 = r3
        L_0x0085:
            if (r5 < r4) goto L_0x0089
            r2 = r3
            goto L_0x008e
        L_0x0089:
            r6 = r1[r5]
            r6 = r6 & r0
            if (r6 == 0) goto L_0x0091
        L_0x008e:
            if (r2 != 0) goto L_0x0094
            return r3
        L_0x0091:
            int r5 = r5 + 1
            goto L_0x0085
        L_0x0094:
            java.lang.String r11 = r11.c()
            boolean r11 = super.a(r11)
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: b.d.e.b.b.a(b.d.e.b):boolean");
    }
}
