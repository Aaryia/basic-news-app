package com.example.aaryia.softnews;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.example.aaryia.softnews.ArticleList.ARTICLES_ITEMS;

/**
 * Created by aaryia on 02/01/18.
 * SoftNews web APP for lectures
 * Centrale Marseille 2017-2018
 */

public class ArticleFullFragment extends android.support.v4.app.Fragment {

    private final String TAG = "ArticleFullFragment";
    private View view = null;
    private int articlePosition;

    //The fragment's ListView/GridView.
    private RecyclerView recyclerView;

    //The Adapter which will be used to populate the ListView/GridView with Views.
    private ArticleFullAdapter mAdapter;



    public ArticleFullFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: Successfully created an ArticleFragment ");
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            articlePosition  = bundle.getInt("position");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment,container,false);
        view.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        mAdapter = new ArticleFullAdapter(ARTICLES_ITEMS.get(articlePosition),getContext());

        recyclerView.setAdapter(mAdapter);
        return view;

    }


}
