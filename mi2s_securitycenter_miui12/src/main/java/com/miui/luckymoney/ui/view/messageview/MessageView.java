package com.miui.luckymoney.ui.view.messageview;

import com.miui.luckymoney.model.message.AppMessage;

public interface MessageView {
    void hide();

    boolean isAlive();

    void show(AppMessage appMessage);
}
