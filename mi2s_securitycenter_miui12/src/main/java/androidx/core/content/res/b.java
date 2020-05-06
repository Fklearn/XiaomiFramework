package androidx.core.content.res;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public final class b {

    /* renamed from: a  reason: collision with root package name */
    private final Shader f711a;

    /* renamed from: b  reason: collision with root package name */
    private final ColorStateList f712b;

    /* renamed from: c  reason: collision with root package name */
    private int f713c;

    private b(Shader shader, ColorStateList colorStateList, @ColorInt int i) {
        this.f711a = shader;
        this.f712b = colorStateList;
        this.f713c = i;
    }

    static b a(@ColorInt int i) {
        return new b((Shader) null, (ColorStateList) null, i);
    }

    static b a(@NonNull ColorStateList colorStateList) {
        return new b((Shader) null, colorStateList, colorStateList.getDefaultColor());
    }

    @Nullable
    public static b a(@NonNull Resources resources, @ColorRes int i, @Nullable Resources.Theme theme) {
        try {
            return b(resources, i, theme);
        } catch (Exception e) {
            Log.e("ComplexColorCompat", "Failed to inflate ComplexColor.", e);
            return null;
        }
    }

    static b a(@NonNull Shader shader) {
        return new b(shader, (ColorStateList) null, 0);
    }

    @NonNull
    private static b b(@NonNull Resources resources, @ColorRes int i, @Nullable Resources.Theme theme) {
        int next;
        XmlResourceParser xml = resources.getXml(i);
        AttributeSet asAttributeSet = Xml.asAttributeSet(xml);
        do {
            next = xml.next();
            if (next == 2) {
                break;
            }
        } while (next != 1);
        if (next == 2) {
            String name = xml.getName();
            char c2 = 65535;
            int hashCode = name.hashCode();
            if (hashCode != 89650992) {
                if (hashCode == 1191572447 && name.equals("selector")) {
                    c2 = 0;
                }
            } else if (name.equals("gradient")) {
                c2 = 1;
            }
            if (c2 == 0) {
                return a(a.a(resources, (XmlPullParser) xml, asAttributeSet, theme));
            }
            if (c2 == 1) {
                return a(c.a(resources, xml, asAttributeSet, theme));
            }
            throw new XmlPullParserException(xml.getPositionDescription() + ": unsupported complex color tag " + name);
        }
        throw new XmlPullParserException("No start tag found");
    }

    @ColorInt
    public int a() {
        return this.f713c;
    }

    public boolean a(int[] iArr) {
        if (d()) {
            ColorStateList colorStateList = this.f712b;
            int colorForState = colorStateList.getColorForState(iArr, colorStateList.getDefaultColor());
            if (colorForState != this.f713c) {
                this.f713c = colorForState;
                return true;
            }
        }
        return false;
    }

    @Nullable
    public Shader b() {
        return this.f711a;
    }

    public void b(@ColorInt int i) {
        this.f713c = i;
    }

    public boolean c() {
        return this.f711a != null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r1.f712b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean d() {
        /*
            r1 = this;
            android.graphics.Shader r0 = r1.f711a
            if (r0 != 0) goto L_0x0010
            android.content.res.ColorStateList r0 = r1.f712b
            if (r0 == 0) goto L_0x0010
            boolean r0 = r0.isStateful()
            if (r0 == 0) goto L_0x0010
            r0 = 1
            goto L_0x0011
        L_0x0010:
            r0 = 0
        L_0x0011:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.core.content.res.b.d():boolean");
    }

    public boolean e() {
        return c() || this.f713c != 0;
    }
}
