package com.miui.antispam.policy.a;

import com.miui.antispam.policy.BlackAddressPolicy;
import com.miui.antispam.policy.BlackListPolicy;
import com.miui.antispam.policy.BlackPrefixPolicy;
import com.miui.antispam.policy.CallTransferPolicy;
import com.miui.antispam.policy.CloudBlackKeywordsPolicy;
import com.miui.antispam.policy.CloudBlackListPolicy;
import com.miui.antispam.policy.CloudWhiteKeywordsPolicy;
import com.miui.antispam.policy.CloudWhiteListPolicy;
import com.miui.antispam.policy.ContactsPolicy;
import com.miui.antispam.policy.EmptyNumberPolicy;
import com.miui.antispam.policy.KeywordsBlackListPolicy;
import com.miui.antispam.policy.KeywordsWhiteListPolicy;
import com.miui.antispam.policy.OverseaPolicy;
import com.miui.antispam.policy.ReportedNumberPolicy;
import com.miui.antispam.policy.ServiceSmsPolicy;
import com.miui.antispam.policy.SmartSmsFilterPolicy;
import com.miui.antispam.policy.StrangerPolicy;
import com.miui.antispam.policy.StrongCloudBlackListPolicy;
import com.miui.antispam.policy.StrongCloudWhiteListPolicy;
import com.miui.antispam.policy.WhiteListPolicy;

public enum g {
    EMPTY_NUMBER_POLICY(0, EmptyNumberPolicy.class, f.CALL),
    STRONG_CLOUD_WHITELIST_POLICY(1, StrongCloudWhiteListPolicy.class, f.BOTH),
    STRONG_CLOUD_BLACKLIST_POLICY(2, StrongCloudBlackListPolicy.class, f.BOTH),
    WHITELIST_POLICY(3, WhiteListPolicy.class, f.BOTH),
    BLACKLIST_POLICY(4, BlackListPolicy.class, f.BOTH),
    BLACK_PREFIX_POLICY(6, BlackPrefixPolicy.class, f.BOTH),
    CONTACTS_POLICY(7, ContactsPolicy.class, f.BOTH),
    BLACK_ADDRESS_POLICY(8, BlackAddressPolicy.class, f.BOTH),
    SERVICE_SMS_POLICY(9, ServiceSmsPolicy.class, f.SMS),
    STRANGER_POLICY(10, StrangerPolicy.class, f.BOTH),
    OVERSEA_POLICY(11, OverseaPolicy.class, f.CALL),
    KEYWORDS_WHITELIST_POLICY(12, KeywordsWhiteListPolicy.class, f.SMS),
    KEYWORDS_BLACKLIST_POLICY(13, KeywordsBlackListPolicy.class, f.SMS),
    CLOUD_WHITELIST_POLICY(14, CloudWhiteListPolicy.class, f.BOTH),
    CLOUD_BLACKLIST_POLICY(15, CloudBlackListPolicy.class, f.BOTH),
    CLOUD_WHITE_KEYWORDS_POLICY(16, CloudWhiteKeywordsPolicy.class, f.SMS),
    CLOUD_BLACK_KEYWORDS_POLICY(17, CloudBlackKeywordsPolicy.class, f.SMS),
    SMART_SMS_FILTER_POLICY(18, SmartSmsFilterPolicy.class, f.SMS),
    CALL_TRANSFER_POLICY(19, CallTransferPolicy.class, f.CALL),
    REPORTED_NUMBER_POLICY(20, ReportedNumberPolicy.class, f.CALL);
    
    public int v;
    public Class w;
    public f x;

    private g(int i, Class cls, f fVar) {
        this.v = i;
        this.w = cls;
        this.x = fVar;
    }
}
