package com.example.aaryia.softnews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aaryia on 22/11/17.
 * SoftNews web APP for lectures
 * Centrale Marseille 2017-2018
 */

class SourceList {


    static List<SourceObject> ITEMS = new ArrayList<>();
    private static Map<String, SourceObject> ITEM_MAP = new HashMap<>();

    static void addItem(SourceObject item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    @Override
    public String toString() {
        return ITEMS.toString();
    }
}

