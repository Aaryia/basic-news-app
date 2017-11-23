package com.example.aaryia.softnews;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aaryia on 22/11/17.
 */

public class ArticleList {


    public static List<ArticleObject> ARTICLES_ITEMS = new ArrayList<ArticleObject>();
    public static Map<String, ArticleObject> ARTICLES_ITEM_MAP = new HashMap<String, ArticleObject>();

    protected static void addArticleItem(ArticleObject item) {
        ARTICLES_ITEMS.add(item);
        ARTICLES_ITEM_MAP.put(item.getTitle(), item);
    }

    @Override
    public String toString() {
        return ARTICLES_ITEMS.toString();
    }
}

