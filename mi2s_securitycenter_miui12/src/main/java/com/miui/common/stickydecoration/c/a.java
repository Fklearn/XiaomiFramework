package com.miui.common.stickydecoration.c;

import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class a {
    public static List<View> a(View view) {
        ArrayList arrayList = new ArrayList();
        if (view != null && (view instanceof ViewGroup)) {
            LinkedList linkedList = new LinkedList();
            linkedList.add((ViewGroup) view);
            while (!linkedList.isEmpty()) {
                ViewGroup viewGroup = (ViewGroup) linkedList.removeFirst();
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    View childAt = viewGroup.getChildAt(i);
                    if (childAt instanceof ViewGroup) {
                        linkedList.addLast((ViewGroup) viewGroup.getChildAt(i));
                    }
                    if (childAt.getId() != -1) {
                        arrayList.add(childAt);
                    }
                }
            }
        }
        return arrayList;
    }
}
