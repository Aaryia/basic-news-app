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

class ArticleList {


    static List<ArticleObject> ARTICLES_ITEMS = new ArrayList<>();
    private static Map<String, ArticleObject> ARTICLES_ITEM_MAP = new HashMap<>();

    static void addArticleItem(ArticleObject item) {
        ARTICLES_ITEMS.add(item);
        ARTICLES_ITEM_MAP.put(item.getTitle(), item);
    }

    @Override
    public String toString() {
        return ARTICLES_ITEMS.toString();
    }

    static void deleteArticles() {
        ARTICLES_ITEMS.clear();
        ARTICLES_ITEM_MAP.clear();
    }
}

