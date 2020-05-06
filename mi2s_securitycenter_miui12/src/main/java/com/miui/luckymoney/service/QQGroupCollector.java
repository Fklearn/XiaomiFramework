package com.miui.luckymoney.service;

import android.content.Context;
import android.text.TextUtils;
import com.miui.luckymoney.model.message.Impl.QQMessage;
import java.util.ArrayList;
import java.util.HashMap;

public class QQGroupCollector {
    private static final HashMap<String, QQGroupInfo> idMaps = new HashMap<>();

    public static class QQGroupInfo {
        public String id;
        public String name;
        public int type;
    }

    public static synchronized void collect(Context context, QQMessage qQMessage) {
        synchronized (QQGroupCollector.class) {
            if (qQMessage.isGroupMessage()) {
                QQGroupInfo qQGroupInfo = idMaps.get(qQMessage.conversationId);
                if (qQGroupInfo == null) {
                    qQGroupInfo = new QQGroupInfo();
                    qQGroupInfo.id = qQMessage.conversationId;
                    idMaps.put(qQGroupInfo.id, qQGroupInfo);
                }
                qQGroupInfo.type = qQMessage.type;
                qQGroupInfo.name = qQMessage.conversationName;
            }
        }
    }

    public static synchronized QQGroupInfo findQQGroupByName(String str) {
        synchronized (QQGroupCollector.class) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            ArrayList arrayList = new ArrayList(1);
            for (QQGroupInfo next : idMaps.values()) {
                if (str.equals(next.name)) {
                    arrayList.add(next);
                }
            }
            if (arrayList.size() != 1) {
                return null;
            }
            QQGroupInfo qQGroupInfo = (QQGroupInfo) arrayList.get(0);
            return qQGroupInfo;
        }
    }
}
