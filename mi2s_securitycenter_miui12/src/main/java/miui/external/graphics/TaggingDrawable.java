package miui.external.graphics;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import java.util.Arrays;

public class TaggingDrawable extends DrawableWrapperAdapter {
    private int[] mRawState;
    private int[] mTaggingState;

    public TaggingDrawable(Drawable drawable) {
        this(drawable, drawable.getState());
    }

    public TaggingDrawable(Drawable drawable, int[] iArr) {
        super(drawable);
        this.mTaggingState = new int[0];
        this.mRawState = new int[0];
        setTaggingState(iArr);
    }

    public static boolean containsTagState(StateListDrawable stateListDrawable, int[] iArr) {
        int stateCount = StateListDrawableReflect.getStateCount(stateListDrawable);
        for (int i = 0; i < stateCount; i++) {
            for (int binarySearch : StateListDrawableReflect.getStateSet(stateListDrawable, i)) {
                if (Arrays.binarySearch(iArr, binarySearch) >= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int[] mergeSortTaggingState(int[] iArr, int[] iArr2) {
        int[] iArr3 = new int[(iArr2.length + iArr.length)];
        System.arraycopy(iArr, 0, iArr3, 0, iArr.length);
        System.arraycopy(iArr2, 0, iArr3, iArr.length, iArr2.length);
        Arrays.sort(iArr3);
        return iArr3;
    }

    private static int[] mergeTaggingState(int[] iArr, int[] iArr2) {
        int[] iArr3 = new int[(iArr2.length + iArr.length)];
        System.arraycopy(iArr, 0, iArr3, 0, iArr.length);
        System.arraycopy(iArr2, 0, iArr3, iArr.length, iArr2.length);
        return iArr3;
    }

    public boolean setState(int[] iArr) {
        if (Arrays.equals(iArr, this.mRawState)) {
            return false;
        }
        this.mRawState = iArr;
        return super.setState(mergeTaggingState(this.mTaggingState, iArr));
    }

    public boolean setTaggingState(int[] iArr) {
        if (Arrays.equals(iArr, this.mTaggingState)) {
            return false;
        }
        this.mTaggingState = iArr;
        return super.setState(mergeTaggingState(this.mTaggingState, this.mRawState));
    }
}
