package com.example.aaryia.softnews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aaryia on 22/11/17.
 */

public class SourceList {


    public static List<SourceObject> ITEMS = new ArrayList<SourceObject>();
    public static Map<String, SourceObject> ITEM_MAP = new HashMap<String, SourceObject>();

    protected static void addItem(SourceObject item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    @Override
    public String toString() {
        return ITEMS.toString();
    }
}

