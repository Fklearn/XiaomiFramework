package com.miui.luckymoney.ui.view;

import com.miui.luckymoney.model.config.BaseConfiguration;
import com.miui.luckymoney.ui.view.messageview.MessageView;
import com.miui.luckymoney.ui.view.messageview.MessageViewCreator;

public class GeneralMessageViewCreator implements MessageViewCreator {
    private final BaseConfiguration mMessageConfig;

    public GeneralMessageViewCreator(BaseConfiguration baseConfiguration) {
        this.mMessageConfig = baseConfiguration;
    }

    public MessageView createHeadsUpMessageView() {
        return new HandsUpMessageView(this.mMessageConfig);
    }

    public MessageView createLockScreenMessageView() {
        return new LockScreenMessageView(this.mMessageConfig);
    }
}
