package miui.keyguard.clock;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.system.internal.R;
import miui.date.DateUtils;

public class MiuiLeftTopClock extends MiuiBaseClock {
    private TextView mTimeText;

    public MiuiLeftTopClock(Context context) {
        this(context, (AttributeSet) null);
    }

    public MiuiLeftTopClock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mTimeText = (TextView) findViewById(R.id.current_time);
        updateTime();
    }

    public void setTextColorDark(boolean textDark) {
        super.setTextColorDark(textDark);
        int color = textDark ? getContext().getResources().getColor(R.color.miui_common_time_dark_text_color) : -1;
        this.mTimeText.setTextColor(color);
        setInfoDarkMode(color);
    }

    public float getTopMargin() {
        return (float) this.mContext.getResources().getDimensionPixelSize(R.dimen.miui_left_top_clock_margin_top);
    }

    public void updateTime() {
        super.updateTime();
        this.mTimeText.setText(DateUtils.formatDateTime(System.currentTimeMillis(), (this.m24HourFormat ? 32 : 16) | 12 | 64));
    }

    /* access modifiers changed from: protected */
    public void updateViewsLayoutParams() {
        int i;
        FrameLayout.LayoutParams clockLayoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        if (this.mHasTopMargin) {
            i = (int) (this.mScaleRatio * ((float) this.mResources.getDimensionPixelSize(R.dimen.miui_left_top_clock_margin_top)));
        } else {
            i = 0;
        }
        clockLayoutParams.topMargin = i;
        clockLayoutParams.setMarginStart((int) (this.mScaleRatio * ((float) this.mResources.getDimensionPixelSize(R.dimen.miui_left_top_clock_margin_left))));
        setLayoutParams(clockLayoutParams);
        LinearLayout.LayoutParams dateInfoLayoutParams = (LinearLayout.LayoutParams) this.mCurrentDate.getLayoutParams();
        dateInfoLayoutParams.topMargin = (int) (this.mScaleRatio * ((float) this.mResources.getDimensionPixelSize(R.dimen.miui_left_top_clock_date_info_top_margin)));
        dateInfoLayoutParams.setMarginStart((int) (this.mScaleRatio * ((float) this.mResources.getDimensionPixelSize(R.dimen.left_top_clock_date_margin_extra))));
        this.mCurrentDate.setLayoutParams(dateInfoLayoutParams);
        LinearLayout.LayoutParams lunarCalendarInfoLayoutParams = (LinearLayout.LayoutParams) this.mLunarCalendarInfo.getLayoutParams();
        lunarCalendarInfoLayoutParams.topMargin = (int) (this.mScaleRatio * ((float) this.mResources.getDimensionPixelSize(R.dimen.miui_clock_lunar_calendar_top_margin)));
        lunarCalendarInfoLayoutParams.setMarginStart((int) (this.mScaleRatio * ((float) this.mResources.getDimensionPixelSize(R.dimen.left_top_clock_date_margin_extra))));
        this.mLunarCalendarInfo.setLayoutParams(lunarCalendarInfoLayoutParams);
        LinearLayout.LayoutParams ownerInfoLayoutParams = (LinearLayout.LayoutParams) this.mOwnerInfo.getLayoutParams();
        ownerInfoLayoutParams.topMargin = (int) (this.mScaleRatio * ((float) this.mResources.getDimensionPixelSize(R.dimen.miui_clock_owner_info_top_margin)));
        ownerInfoLayoutParams.setMarginStart((int) (this.mScaleRatio * ((float) this.mResources.getDimensionPixelSize(R.dimen.left_top_clock_date_margin_extra))));
        this.mOwnerInfo.setLayoutParams(ownerInfoLayoutParams);
    }

    /* access modifiers changed from: protected */
    public void updateViewsTextSize() {
        super.updateViewsTextSize();
        this.mTimeText.setTextSize(0, (float) ((int) (this.mScaleRatio * ((float) this.mContext.getResources().getDimensionPixelSize(R.dimen.miui_left_top_clock_time_text_size)))));
    }
}
