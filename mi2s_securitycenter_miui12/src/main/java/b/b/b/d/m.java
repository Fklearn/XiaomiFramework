package b.b.b.d;

public enum m {
    SAFE(0),
    RISK(1),
    DANGER(2),
    INTERRUPT(3);
    
    private int f;

    private m(int i) {
        this.f = i;
    }

    public static m a() {
        for (m mVar : values()) {
            if (mVar.b() == 0) {
                return mVar;
            }
        }
        return null;
    }

    public static m a(int i) {
        for (m mVar : values()) {
            if (i == mVar.b()) {
                return mVar;
            }
        }
        return a();
    }

    public int b() {
        return this.f;
    }
}
