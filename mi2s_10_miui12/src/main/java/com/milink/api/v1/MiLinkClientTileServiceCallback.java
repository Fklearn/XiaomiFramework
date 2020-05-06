package com.milink.api.v1;

public interface MiLinkClientTileServiceCallback {
    void onCloseTile();

    void onConnected(String str, String str2);

    void onDisConnected(String str);
}
