package miui.keyguard.clock;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.system.internal.R;
import miui.date.DateUtils;

public class MiuiLeftTopLargeClock extends MiuiBaseClock {
    private TextView mCurrentDateLarge;
    private FrameLayout mDateContainer;
    private TextView mTimeText;

    public MiuiLeftTopLargeClock(Context context) {
        this(context, (AttributeSet) null);
    }

    public MiuiLeftTopLargeClock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mTimeText = (TextView) findViewById(R.id.current_time);
        this.mDateContainer = (FrameLayout) findViewById(R.id.left_top_date_container);
        this.mCurrentDateLarge = (TextView) findViewById(R.id.current_date_large);
        updateTime();
    }

    public void setTextColorDark(boolean textDark) {
        int ownerInfoColor = -1;
        int color = textDark ? -16777216 : -1;
        this.mTimeText.setTextColor(color);
        this.mCurrentDateLarge.setTextColor(color);
        setInfoDarkMode(color);
        if (textDark) {
            ownerInfoColor = getContext().getResources().getColor(R.color.miui_owner_info_dark_text_color);
        }
        this.mOwnerInfo.setTextColor(ownerInfoColor);
    }

    public float getTopMargin() {
        return (float) this.mContext.getResources().getDimensionPixelSize(R.dimen.miui_left_top_clock_margin_top);
    }

    public void updateTime() {
        int dateResId;
        super.updateTime();
        this.mTimeText.setText(DateUtils.formatDateTime(System.currentTimeMillis(), (this.m24HourFormat ? 32 : 16) | 12 | 64));
        if (this.m24HourFormat) {
            dateResId = R.string.miui_lock_screen_large_date;
        } else {
            dateResId = R.string.miui_lock_screen_large_date_12;
        }
        this.mCurrentDateLarge.setText(this.mCalendar.format(this.mContext.getString(dateResId)).toUpperCase());
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
        LinearLayout.LayoutParams dateInfoLayoutParams = (LinearLayout.LayoutParams) this.mDateContainer.getLayoutParams();
        dateInfoLayoutParams.topMargin = (int) (this.mScaleRatio * ((float) this.mResources.getDimensionPixelSize(R.dimen.miui_left_top_large_clock_date_info_top_margin)));
        this.mDateContainer.setLayoutParams(dateInfoLayoutParams);
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
        Resources resources = this.mContext.getResources();
        this.mTimeText.setTextSize(0, (float) ((int) (this.mScaleRatio * ((float) resources.getDimensionPixelSize(R.dimen.miui_left_top_clock_time_text_size)))));
        this.mCurrentDateLarge.setTextSize(0, (float) ((int) (this.mScaleRatio * ((float) resources.getDimensionPixelSize(R.dimen.miui_left_top_large_clock_date_text_size)))));
    }
}
