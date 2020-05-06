package miui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;

public class MiuiKeyBoardView extends FrameLayout implements View.OnClickListener, View.OnTouchListener {
    private static final float FUNC_KEY_RATIO = 1.6f;
    private static final float HORIZONTAL_MARGIN_RATIO = 0.2f;
    private static final float OK_KEY_RATIO = 2.8f;
    private static final int PREVIEW_ANIMATION_DURATION = 100;
    private static final long SHOW_PREVIEW_DURATION = 300;
    private static final float[][] SIZE_GROUP = {new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f}, new float[]{FUNC_KEY_RATIO, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, FUNC_KEY_RATIO}, new float[]{OK_KEY_RATIO, SPACE_KEY_RATIO, OK_KEY_RATIO}};
    private static final float SPACE_KEY_RATIO = 5.8f;
    private static final String SPACE_STR = " ";
    private static final float VERTICAL_MARGIN_RATIO = 0.17f;
    private View mBtnCapsLock;
    private View mBtnLetterDelete;
    private View mBtnLetterOK;
    private View mBtnLetterSpace;
    private View mBtnSymbolDelete;
    private View mBtnSymbolOK;
    private View mBtnSymbolSpace;
    private View mBtnToLetterBoard;
    private View mBtnToSymbolBoard;
    private Context mContext;
    private boolean mIsShowingPreview;
    private boolean mIsUpperMode;
    private ArrayList<OnKeyboardActionListener> mKeyboardListeners;
    private FrameLayout mLetterBoard;
    private int mPopupViewHeight;
    private int mPopupViewWidth;
    private int mPopupViewX;
    private int mPopupViewY;
    private TextView mPreviewText;
    private ValueAnimator mShowPreviewAnimator;
    private long mShowPreviewLastTime;
    private Animation mShrinkToBottonAnimation;
    private Animation mStretchFromBottonAnimation;
    private FrameLayout mSymbolBoard;

    public interface OnKeyboardActionListener {
        void onKeyBoardDelete();

        void onKeyBoardOK();

        void onText(CharSequence charSequence);
    }

    public MiuiKeyBoardView(Context context) {
        this(context, (AttributeSet) null);
    }

    public MiuiKeyBoardView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public MiuiKeyBoardView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsUpperMode = false;
        this.mIsShowingPreview = false;
        this.mShowPreviewLastTime = 0;
        this.mShowPreviewAnimator = new ValueAnimator();
        this.mStretchFromBottonAnimation = null;
        this.mShrinkToBottonAnimation = null;
    }

    public void addKeyboardListener(OnKeyboardActionListener onKeyboardActionListener) {
        Iterator<OnKeyboardActionListener> it = this.mKeyboardListeners.iterator();
        while (it.hasNext()) {
            if (onKeyboardActionListener.equals(it.next())) {
                return;
            }
        }
        this.mKeyboardListeners.add(onKeyboardActionListener);
    }

    /* access modifiers changed from: package-private */
    public void keyboardOnLayout(ViewGroup viewGroup, int i, int i2, int i3, int i4, int i5) {
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        if (getParent() != null) {
            ((ViewGroup) getParent()).setClipChildren(false);
        }
        super.onAttachedToWindow();
    }

    public void onClick(View view) {
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        return true;
    }

    public void removeKeyboardListener(OnKeyboardActionListener onKeyboardActionListener) {
        this.mKeyboardListeners.remove(onKeyboardActionListener);
    }
}
