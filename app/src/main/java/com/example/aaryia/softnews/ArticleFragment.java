package com.example.aaryia.softnews;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import static com.example.aaryia.softnews.ArticleList.ARTICLES_ITEMS;
import static com.example.aaryia.softnews.ArticleList.SOURCE;
import static com.example.aaryia.softnews.ArticleList.addArticleItem;
import static com.example.aaryia.softnews.ArticleList.deleteArticles;
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

        //The Adapter which will be used to populate the ListView/GridView withViews.
        private ArticleAdapter mAdapter;
        private SourceDisplayActivity sourceDisplayActivity;

        public ArticleFragment() {
        }

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


            mAdapter = new ArticleAdapter(ARTICLES_ITEMS,mListView);

            mAdapter.setOnLoadMoreListener(new ArticleAdapter.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    //add null , so the adapter will check view_type and show progress bar at bottom
                    ARTICLES_ITEMS.add(null);
                    mAdapter.notifyItemInserted(ARTICLES_ITEMS.size() - 1);

                    //TODO: volley à implémenter

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //On enlève la barre de progression et on retourne le résultat de la volley
                            ARTICLES_ITEMS.remove(ARTICLES_ITEMS.size() - 1);
                            mAdapter.notifyItemRemoved(ARTICLES_ITEMS.size());
                            volleyArticleNextPage();
                        }
                    }, 2000);
                }
            });

            mListView.setAdapter(mAdapter);
            return view;
        }

        public void onBackPressed() {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.popBackStack();
        }

        public void volleyArticleNextPage(){
            int pageNumber;
            pageNumber = ((ARTICLES_ITEMS.size()+1)/20)+1;
            RequestQueue queue = Volley.newRequestQueue(sourceDisplayActivity);
            String url = sourceDisplayActivity.getString(R.string.url_articles) + SOURCE + "&page="+pageNumber;

            JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject jsonObject = response;
                    Log.i(TAG, "onResponse: " + String.valueOf(jsonObject));

                    if (jsonObject != null) {
                        try {

                            // On rajoute tous les nouveaux articles dans la liste
                            for (int i = 0; i < jsonObject.getJSONArray("articles").length(); i++) {

                                Log.i(TAG, "onCreateView: " + jsonObject.getJSONArray("articles").getJSONObject(i).getString("title"));
                                //Puts all the sources in a sourceList.
                                addArticleItem(new ArticleObject(jsonObject.getJSONArray("articles").getJSONObject(i), SOURCE));
                            }

                            // Notification du changement et que le chargement est fini.
                            mAdapter.notifyItemInserted(ARTICLES_ITEMS.size());
                            mAdapter.setDone();

                        } catch (Exception e) {
                            Log.e(TAG, "onAttach: Error thrown while JSON parsing", e);
                        }
                    } else {
                        Log.i(TAG, "onCreateView: JSON Object is null");
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: we got a problem", error);
                }
            });
            // Ajoute la requête à la RequestQueue pour obtenir le JSON approprié
            queue.add(jsObjectRequest);
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

        public interface OnFragmentInteractionListener {
            void onArticleFragmentInteraction(String id);

        }
    }

