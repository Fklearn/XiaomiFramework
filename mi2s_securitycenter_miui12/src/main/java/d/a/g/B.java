package d.a.g;

import android.view.View;
import com.miui.maml.folme.AnimatedProperty;
import d.a.m;

public abstract class B extends C0575b<View> {

    /* renamed from: a  reason: collision with root package name */
    public static final B f8754a = new s("translationX");

    /* renamed from: b  reason: collision with root package name */
    public static final B f8755b = new t("translationY");

    /* renamed from: c  reason: collision with root package name */
    public static final B f8756c = new u("translationZ");

    /* renamed from: d  reason: collision with root package name */
    public static final B f8757d = new v(AnimatedProperty.PROPERTY_NAME_SCALE_X);
    public static final B e = new w(AnimatedProperty.PROPERTY_NAME_SCALE_Y);
    public static final B f = new x(AnimatedProperty.PROPERTY_NAME_ROTATION);
    public static final B g = new y(AnimatedProperty.PROPERTY_NAME_ROTATION_X);
    public static final B h = new z(AnimatedProperty.PROPERTY_NAME_ROTATION_Y);
    public static final B i = new A(AnimatedProperty.PROPERTY_NAME_X);
    public static final B j = new i(AnimatedProperty.PROPERTY_NAME_Y);
    public static final B k = new j("z");
    public static final B l = new k("height");
    public static final B m = new l("width");
    public static final B n = new m(AnimatedProperty.PROPERTY_NAME_ALPHA);
    public static final B o = new n("autoAlpha");
    public static final B p = new o("scrollX");
    public static final B q = new p("scrollY");
    public static final B r = new q("deprecated_foreground");
    public static final B s = new r("deprecated_background");

    public B(String str) {
        super(str);
    }

    /* access modifiers changed from: private */
    public static boolean b(View view) {
        return view.getTag(m.miuix_animation_tag_init_layout) != null;
    }

    public String toString() {
        return "ViewProperty{mPropertyName='" + this.mPropertyName + '\'' + '}';
    }
}
