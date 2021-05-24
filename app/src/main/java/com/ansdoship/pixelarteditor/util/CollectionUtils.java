package com.ansdoship.pixelarteditor.util;

import java.util.Collections;
import java.util.List;

public final class CollectionUtils {

    public static <T> List <T> removeNullElements (List <T> list) {
        list.removeAll(Collections.singleton(null));
        return list;
    }

    public static <T> int getNonNullElementsCount (List <T> list) {
        return removeNullElements(list).size();
    }

}
