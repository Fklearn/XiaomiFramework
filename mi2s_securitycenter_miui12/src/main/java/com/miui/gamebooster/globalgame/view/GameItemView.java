package com.miui.gamebooster.globalgame.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.gamebooster.globalgame.module.BannerCardBean;
import com.miui.gamebooster.globalgame.module.GameListItem;
import com.miui.gamebooster.globalgame.present.g;
import com.miui.securitycenter.R;
import com.miui.securitycenter.i;

@Keep
public class GameItemView extends RelativeLayout {
    static final int TYPE_FULL_ROW = 1;
    static final int TYPE_FULL_ROW_NO_DESC_TRANSPARENT_BG = 3;
    static final int TYPE_FULL_ROW_NO_DESC_WHITE_BG = 2;
    static final int TYPE_GRID_CELL = 0;
    private Context context;
    private TextView desc;
    private ImageView icon;
    private View infoOverlay;
    private TextView name;
    private TextView play;
    private BaseRatingBar ratingBar;
    private View ratingContainer;
    private TextView ratingText;
    private View root;
    int type;

    public GameItemView(Context context2) {
        this(context2, (AttributeSet) null);
    }

    public GameItemView(Context context2, AttributeSet attributeSet) {
        this(context2, attributeSet, 0);
    }

    public GameItemView(Context context2, AttributeSet attributeSet, int i) {
        super(context2, attributeSet, i);
        this.context = context2;
        TypedArray obtainStyledAttributes = context2.obtainStyledAttributes(attributeSet, i.GameItemView);
        this.type = obtainStyledAttributes.getInteger(0, 0);
        int i2 = this.type;
        if (i2 == 0) {
            LayoutInflater.from(context2).inflate(R.layout.gbg_game_cell_item, this, true);
            validView();
        } else if (i2 == 1) {
            LayoutInflater.from(context2).inflate(R.layout.gbg_info_overlay_layout, this, true);
            validView();
            adjustTopBottomMargin(context2);
        } else if (i2 == 2) {
            LayoutInflater.from(context2).inflate(R.layout.gbg_info_overlay_layout, this, true);
            validView();
            adjustDescAndMargin();
            setBackgroundResource(R.drawable.background_with_radius_white_fill_bottom_radius);
        } else if (i2 == 3) {
            LayoutInflater.from(context2).inflate(R.layout.gbg_info_overlay_layout, this, true);
            validView();
            adjustDescAndMargin();
            this.name.setTextColor(-1);
        }
        obtainStyledAttributes.recycle();
    }

    private void adjustDescAndMargin() {
        TextView textView = this.desc;
        if (textView != null) {
            textView.setVisibility(8);
        }
        View view = this.ratingContainer;
        if (view != null) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).topMargin *= 5;
        }
        requestLayout();
    }

    private void adjustTopBottomMargin(Context context2) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.icon.getLayoutParams();
        int dimensionPixelOffset = (int) (((float) context2.getResources().getDimensionPixelOffset(R.dimen.gbg_big_post_content_margin)) * 0.67f);
        layoutParams.topMargin = dimensionPixelOffset;
        layoutParams.bottomMargin = dimensionPixelOffset;
        this.icon.getParent().requestLayout();
    }

    private void customFullRowDetail(GameListItem gameListItem, @Nullable String str) {
        TextView textView;
        if (!TextUtils.isEmpty(gameListItem.getDesc()) && this.type == 1 && (textView = this.desc) != null) {
            textView.setVisibility(0);
            this.desc.setText(gameListItem.getDesc());
        }
        float validRating = validRating(gameListItem.getScore());
        BaseRatingBar baseRatingBar = this.ratingBar;
        if (baseRatingBar != null) {
            baseRatingBar.setRating(validRating / 2.0f);
        }
        TextView textView2 = this.ratingText;
        if (textView2 != null) {
            textView2.setVisibility(0);
            this.ratingText.setText(String.format("%.1f", new Object[]{Float.valueOf(validRating)}));
        }
        TextView textView3 = this.play;
        if (textView3 != null) {
            textView3.setVisibility(0);
            this.play.setText(str);
        }
    }

    private int getIconRadius() {
        return this.context.getResources().getDimensionPixelOffset(R.dimen.gbg_card_button_radius);
    }

    private float validRating(String str) {
        try {
            return Float.valueOf(str).floatValue();
        } catch (Exception unused) {
            return 10.0f;
        }
    }

    private void validView() {
        this.icon = (ImageView) findViewById(R.id.icon);
        this.name = (TextView) findViewById(R.id.name);
        if (this.type != 0) {
            this.desc = (TextView) findViewById(R.id.desc);
            this.ratingBar = (BaseRatingBar) findViewById(R.id.rating);
            this.play = (TextView) findViewById(R.id.play);
            this.ratingText = (TextView) findViewById(R.id.ratingText);
            this.infoOverlay = findViewById(R.id.infoOverlay);
            this.root = findViewById(R.id.root);
            this.ratingContainer = findViewById(R.id.ratingContainer);
        }
    }

    @SuppressLint({"DefaultLocale"})
    public void update(BannerCardBean bannerCardBean, GameListItem gameListItem, @Nullable String str) {
        if (this.icon != null) {
            g.a(this.context, gameListItem.getIcon(), this.icon, getIconRadius());
        }
        TextView textView = this.name;
        if (textView != null) {
            if (textView.getVisibility() != 0) {
                this.name.setVisibility(0);
            }
            this.name.setText(gameListItem.getName());
        }
        if (this.type != 0) {
            customFullRowDetail(gameListItem, str);
        }
        Context context2 = this.context;
        View[] viewArr = new View[4];
        View view = this.infoOverlay;
        if (view == null) {
            view = this.icon;
        }
        viewArr[0] = view;
        TextView textView2 = this.name;
        viewArr[1] = textView2;
        TextView textView3 = this.play;
        if (textView3 != null) {
            textView2 = textView3;
        }
        viewArr[2] = textView2;
        View view2 = this.root;
        if (view2 == null) {
            view2 = this.icon;
        }
        viewArr[3] = view2;
        g.a(context2, bannerCardBean, gameListItem, viewArr);
    }

    public void vanishDetail() {
        ImageView imageView = this.icon;
        if (imageView != null) {
            imageView.setImageDrawable((Drawable) null);
        }
        TextView textView = this.name;
        if (textView != null) {
            textView.setVisibility(4);
        }
        if (this.type != 0) {
            TextView textView2 = this.desc;
            if (textView2 != null) {
                textView2.setVisibility(4);
            }
            BaseRatingBar baseRatingBar = this.ratingBar;
            if (baseRatingBar != null) {
                baseRatingBar.setRating(0.0f);
            }
            TextView textView3 = this.play;
            if (textView3 != null) {
                textView3.setVisibility(4);
            }
        }
    }
}
