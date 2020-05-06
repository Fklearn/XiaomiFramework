package b.b.a.e;

import java.util.Comparator;
import miui.telephony.SubscriptionInfo;

class m implements Comparator<SubscriptionInfo> {
    m() {
    }

    /* renamed from: a */
    public int compare(SubscriptionInfo subscriptionInfo, SubscriptionInfo subscriptionInfo2) {
        return subscriptionInfo.getSlotId() - subscriptionInfo2.getSlotId();
    }
}
