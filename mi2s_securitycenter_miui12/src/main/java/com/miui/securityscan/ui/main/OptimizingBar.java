package com.miui.securityscan.ui.main;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.c.i.b;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.securitycenter.R;
import com.miui.securityscan.scanner.C0568o;
import java.util.ArrayList;
import java.util.List;

public class OptimizingBar extends RelativeLayout implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private Button f8005a;

    /* renamed from: b  reason: collision with root package name */
    private Handler f8006b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f8007c;

    /* renamed from: d  reason: collision with root package name */
    private TextView f8008d;
    private TextView e;
    private TextView f;
    private TextView g;
    private TextView h;
    private ProgressBar i;
    private ProgressBar j;
    private ProgressBar k;
    private ImageView l;
    private ImageView m;
    private ImageView n;
    private Context o;
    private ImageView p;
    private ImageView q;
    private ImageView r;
    private View s;
    private View t;
    private View u;
    private List<ValueAnimator> v;

    public OptimizingBar(Context context) {
        this(context, (AttributeSet) null);
    }

    public OptimizingBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public OptimizingBar(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.v = new ArrayList();
        this.o = context;
    }

    public void a() {
        setTranslationY(0.0f);
        this.p.setImageResource(R.drawable.card_icon_system_before);
        this.q.setImageResource(R.drawable.card_icon_memory_before);
        this.r.setImageResource(R.drawable.card_icon_cache_before);
        this.f8008d.setText(this.o.getString(R.string.optmizingbar_ready));
        this.f.setText(this.o.getString(R.string.optmizingbar_ready));
        this.h.setText(this.o.getString(R.string.optmizingbar_ready));
        this.i.setVisibility(0);
        this.l.setVisibility(8);
        this.k.setVisibility(0);
        this.n.setVisibility(8);
        this.j.setVisibility(0);
        this.m.setVisibility(8);
    }

    public void a(Handler handler) {
        this.f8006b = handler;
    }

    public void a(C0568o oVar) {
        ImageView imageView;
        a(oVar, this.o.getString(R.string.optmizingbar_optimize_done));
        if (!this.v.isEmpty()) {
            for (int i2 = 0; i2 < this.v.size(); i2++) {
                ValueAnimator valueAnimator = this.v.get(i2);
                if (valueAnimator != null && valueAnimator.isRunning()) {
                    valueAnimator.cancel();
                }
            }
        }
        int i3 = b.f8011a[oVar.ordinal()];
        if (i3 == 1) {
            this.p.setImageResource(R.drawable.card_icon_system);
            ObjectAnimator.ofFloat(this.p, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{0.6f, 1.0f}).setDuration(200).start();
            imageView = this.p;
        } else if (i3 == 2) {
            this.q.setImageResource(R.drawable.card_icon_memory);
            ObjectAnimator.ofFloat(this.q, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{0.6f, 1.0f}).setDuration(200).start();
            imageView = this.q;
        } else if (i3 == 3) {
            this.r.setImageResource(R.drawable.card_icon_cache);
            ObjectAnimator.ofFloat(this.r, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{0.6f, 1.0f}).setDuration(200).start();
            imageView = this.r;
        } else {
            return;
        }
        ObjectAnimator.ofFloat(imageView, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{0.6f, 1.0f}).setDuration(200).start();
    }

    public void a(C0568o oVar, int i2) {
        ImageView imageView;
        ImageView imageView2;
        int i3 = b.f8011a[oVar.ordinal()];
        if (i3 != 1) {
            if (i3 != 2) {
                if (i3 == 3) {
                    if (i2 == 100) {
                        this.j.setVisibility(8);
                        imageView2 = this.m;
                    } else {
                        this.j.setVisibility(0);
                        imageView = this.m;
                        imageView.setVisibility(8);
                        return;
                    }
                } else {
                    return;
                }
            } else if (i2 == 100) {
                this.k.setVisibility(8);
                imageView2 = this.n;
            } else {
                this.k.setVisibility(0);
                imageView = this.n;
                imageView.setVisibility(8);
                return;
            }
        } else if (i2 == 100) {
            this.i.setVisibility(8);
            imageView2 = this.l;
        } else {
            this.i.setVisibility(0);
            imageView = this.l;
            imageView.setVisibility(8);
            return;
        }
        imageView2.setVisibility(0);
    }

    public void a(C0568o oVar, Animator.AnimatorListener animatorListener) {
        LinearInterpolator linearInterpolator;
        ValueAnimator valueAnimator;
        int i2 = b.f8011a[oVar.ordinal()];
        if (i2 == 1) {
            valueAnimator = ValueAnimator.ofInt(new int[]{1, 100});
            valueAnimator.setDuration(AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            linearInterpolator = new LinearInterpolator();
        } else if (i2 == 2) {
            valueAnimator = ValueAnimator.ofInt(new int[]{1, 100});
            valueAnimator.setDuration(AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            valueAnimator.addListener(animatorListener);
            linearInterpolator = new LinearInterpolator();
        } else if (i2 == 3) {
            valueAnimator = ValueAnimator.ofInt(new int[]{1, 100});
            valueAnimator.setDuration(AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            if (animatorListener != null) {
                valueAnimator.addListener(animatorListener);
            }
            linearInterpolator = new LinearInterpolator();
        } else {
            return;
        }
        valueAnimator.setInterpolator(linearInterpolator);
        valueAnimator.start();
        this.v.add(valueAnimator);
    }

    public void a(C0568o oVar, String str) {
        TextView textView;
        int i2 = b.f8011a[oVar.ordinal()];
        if (i2 == 1) {
            textView = this.f8008d;
        } else if (i2 == 2) {
            textView = this.f;
        } else if (i2 == 3) {
            textView = this.h;
        } else {
            return;
        }
        textView.setText(str);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.button_stop_optmize) {
            this.f8006b.sendEmptyMessage(106);
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f8005a = (Button) findViewById(R.id.button_stop_optmize);
        this.f8005a.setOnClickListener(this);
        this.s = findViewById(R.id.layout_system_item);
        this.t = findViewById(R.id.layout_cache_item);
        this.u = findViewById(R.id.layout_memory_item);
        this.p = (ImageView) this.s.findViewById(R.id.iv_bg);
        this.p.setImageResource(R.drawable.card_icon_system_before);
        this.q = (ImageView) this.t.findViewById(R.id.iv_bg);
        this.q.setImageResource(R.drawable.card_icon_cache_before);
        this.r = (ImageView) this.u.findViewById(R.id.iv_bg);
        this.r.setImageResource(R.drawable.card_icon_memory_before);
        this.f8007c = (TextView) this.s.findViewById(R.id.tv_title);
        this.f8007c.setText(R.string.optmizingbar_title_system);
        this.e = (TextView) this.t.findViewById(R.id.tv_title);
        this.e.setText(R.string.optmizingbar_title_clear);
        this.g = (TextView) this.u.findViewById(R.id.tv_title);
        this.g.setText(R.string.optmizingbar_title_security);
        this.f8008d = (TextView) this.s.findViewById(R.id.tv_summary);
        this.f = (TextView) this.t.findViewById(R.id.tv_summary);
        this.f.setText(R.string.optmizingbar_ready);
        this.h = (TextView) this.u.findViewById(R.id.tv_summary);
        this.h.setText(R.string.optmizingbar_ready);
        this.i = (ProgressBar) this.s.findViewById(R.id.progressbar_status);
        this.k = (ProgressBar) this.t.findViewById(R.id.progressbar_status);
        this.j = (ProgressBar) this.u.findViewById(R.id.progressbar_status);
        this.l = (ImageView) this.s.findViewById(R.id.iv_progress);
        this.n = (ImageView) this.t.findViewById(R.id.iv_progress);
        this.m = (ImageView) this.u.findViewById(R.id.iv_progress);
    }

    public void setButtonClickable(boolean z) {
        this.f8005a.setClickable(z);
    }

    public void setEventHandler(b bVar) {
        this.f8006b = bVar;
    }
}
