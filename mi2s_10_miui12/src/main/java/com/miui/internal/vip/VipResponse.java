package com.miui.internal.vip;

import miui.telephony.phonenumber.Prefix;

public class VipResponse {
    public static final VipResponse FailRes = new VipResponse(90000);
    public static final int INIT_STATE = -1;
    public static final VipResponse SuccWithoutValueRes = new VipResponse(0);
    public static final VipResponse WaitRes = new VipResponse(1);
    public String errMsg;
    public int state;
    public Object value;

    public VipResponse() {
        this(-1);
    }

    public VipResponse(int state2, Object value2) {
        this(state2, Prefix.EMPTY, value2);
    }

    public VipResponse(int state2) {
        this(state2, Prefix.EMPTY, (Object) null);
    }

    public VipResponse(int state2, String msg) {
        this(state2, msg, (Object) null);
    }

    public VipResponse(int s, String msg, Object v) {
        this.state = s;
        this.errMsg = msg;
        this.value = v;
    }

    public boolean isValid() {
        return this.state != -1;
    }

    public boolean isSuccess() {
        return this.state == 0;
    }

    public boolean isWaiting() {
        return this.state == 1;
    }

    public VipResponse clone() {
        return new VipResponse(this.state, this.errMsg, this.value);
    }

    public String toString() {
        return String.format("{state = %d, errMsg = %s, value = %s}", new Object[]{Integer.valueOf(this.state), this.errMsg, this.value});
    }
}
