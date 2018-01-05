package com.example.aaryia.softnews;


import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import static com.example.aaryia.softnews.ArticleList.ARTICLES_ITEMS;
import static com.example.aaryia.softnews.SourceList.*;

    /**
     * Created by aaryia on 22/11/17.
     */
    public class ArticleFragment extends android.support.v4.app.Fragment implements AbsListView.OnItemClickListener {

        private final String TAG = "ArticleFragment";
        private View view = null;
        private OnFragmentInteractionListener mListener;
        protected Handler handler = new Handler();

        //The fragment's ListView/GridView.
        private RecyclerView mListView;

        //The Adapter which will be used to populate the ListView/GridView with Views.
        ArticleAdapter mAdapter;
        private SourceDisplayActivity sourceDisplayActivity;



        public ArticleFragment() {}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.i(TAG, "onCreate: Successfully created an ArticleFragment ");
            sourceDisplayActivity = (SourceDisplayActivity) getActivity();

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            view = inflater.inflate(R.layout.fragment, container, false);

            // Set the adapter
            mListView = view.findViewById(R.id.recycler);
            mListView.setLayoutManager(new LinearLayoutManager(this.getContext()));


            mAdapter = new ArticleAdapter(ARTICLES_ITEMS,mListView, this.getActivity());

            mAdapter.setOnLoadMoreListener(new ArticleAdapter.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    //add null , so the adapter will check view_type and show progress bar at bottom
                    ARTICLES_ITEMS.add(null);
                    mAdapter.notifyItemInserted(ARTICLES_ITEMS.size() - 1);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //On enlève la barre de progression et on retourne le résultat de la volley
                            ARTICLES_ITEMS.remove(ARTICLES_ITEMS.size() - 1);
                            mAdapter.notifyItemRemoved(ARTICLES_ITEMS.size());
                            sourceDisplayActivity.volleyArticleNextPage();
                        }
                    }, 2000);
                }
            });

            mListView.setAdapter(mAdapter);
            return view;
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mListener = null;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (null != mListener) {
// Notify the active callbacks interface (the activity, if the
// fragment is attached to one) that an item has been selected.
                mListener.onArticleFragmentInteraction(ITEMS.get(position).id);
            }
        }

        interface OnFragmentInteractionListener {
            void onArticleFragmentInteraction(String id);

        }
    }

