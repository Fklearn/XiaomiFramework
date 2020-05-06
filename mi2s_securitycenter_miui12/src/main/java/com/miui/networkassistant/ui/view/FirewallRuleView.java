package com.miui.networkassistant.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.miui.networkassistant.model.FirewallRule;
import com.miui.securitycenter.R;
import java.util.HashMap;

public class FirewallRuleView extends ImageView implements View.OnClickListener {
    private static HashMap<FirewallRule, Integer> sRuleImageMap = new HashMap<>();
    private FirewallRule mRule;
    private OnRuleChangedListener mRuleChangedListener;

    public interface OnRuleChangedListener {
        void onRuleChanged(FirewallRuleView firewallRuleView, FirewallRule firewallRule);

        boolean onRuleChanging(FirewallRuleView firewallRuleView, FirewallRule firewallRule);
    }

    static {
        sRuleImageMap.put(FirewallRule.Allow, Integer.valueOf(R.drawable.firewall_enable));
        sRuleImageMap.put(FirewallRule.Restrict, Integer.valueOf(R.drawable.firewall_disable));
    }

    public FirewallRuleView(Context context) {
        super(context);
        initView();
    }

    public FirewallRuleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    public FirewallRuleView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView();
    }

    private void initView() {
        this.mRule = FirewallRule.Init;
        setOnClickListener(this);
        setImageResource(R.drawable.firewall_enable);
        setScaleType(ImageView.ScaleType.CENTER);
    }

    public FirewallRule getRule() {
        return this.mRule;
    }

    public void onClick(View view) {
        if (this.mRuleChangedListener != null) {
            FirewallRule firewallRule = this.mRule;
            FirewallRule firewallRule2 = FirewallRule.Allow;
            if (firewallRule == firewallRule2) {
                firewallRule2 = FirewallRule.Restrict;
            }
            if (this.mRuleChangedListener.onRuleChanging(this, this.mRule)) {
                setRule(firewallRule2);
                this.mRuleChangedListener.onRuleChanged(this, this.mRule);
            }
        }
    }

    public void setRule(FirewallRule firewallRule) {
        if (firewallRule != this.mRule) {
            this.mRule = firewallRule;
            int intValue = sRuleImageMap.get(firewallRule).intValue();
            if (intValue > 0) {
                setImageResource(intValue);
            }
        }
    }

    public void setRuleChangedListener(OnRuleChangedListener onRuleChangedListener) {
        this.mRuleChangedListener = onRuleChangedListener;
    }
}
