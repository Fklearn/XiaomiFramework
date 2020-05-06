package com.miui.applicationlock.c;

import java.util.ArrayList;
import java.util.Collections;

public class H {
    public static <E> ArrayList<E> a(E... eArr) {
        ArrayList<E> arrayList = new ArrayList<>(((eArr.length * 110) / 100) + 5);
        Collections.addAll(arrayList, eArr);
        return arrayList;
    }
}
