package com.example.aaryia.softnews;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import static com.example.aaryia.softnews.ArticleList.ARTICLES_ITEMS;

/**
 * Created by aaryia on 02/01/18.
 */

public class ArticleFullFragment extends android.support.v4.app.Fragment {

    private final String TAG = "ArticleFullFragment";
    private View view = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: Successfully created an ArticleFragment ");
    }


}
