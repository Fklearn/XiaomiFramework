package miui.yellowpage;

public class AntispamCustomCategory extends AntispamCategory {
    private boolean mIsUserCustom;
    private int mMarkedCount;
    private String mNumber;
    private int mNumberType;

    public AntispamCustomCategory(int id, String names, int type, String icon, int order, String number, int markedCount, boolean isUserCustom) {
        super(id, names, type, icon, order);
        this.mNumber = number;
        this.mMarkedCount = markedCount;
        this.mIsUserCustom = isUserCustom;
    }

    public boolean isNumberCategoryCustom() {
        return this.mIsUserCustom;
    }

    public String getNumber() {
        return this.mNumber;
    }

    public int getMarkedCount() {
        return this.mMarkedCount;
    }

    public int getNumberType() {
        return this.mNumberType;
    }

    public void setNumberType(int numberType) {
        this.mNumberType = numberType;
    }
}
