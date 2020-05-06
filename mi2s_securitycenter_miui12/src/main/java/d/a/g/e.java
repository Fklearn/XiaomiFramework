package d.a.g;

public class e extends f implements C0576c {
    public e(String str) {
        super(str);
    }

    public int getIntValue(Object obj) {
        Integer num;
        if (!(obj instanceof h) || (num = (Integer) ((h) obj).a(getName(), Integer.TYPE)) == null) {
            return 0;
        }
        return num.intValue();
    }

    public void setIntValue(Object obj, int i) {
        if (obj instanceof h) {
            ((h) obj).a(getName(), Integer.TYPE, Integer.valueOf(i));
        }
    }

    public String toString() {
        return "IntValueProperty{name=" + getName() + '}';
    }
}
