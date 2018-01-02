package com.example.aaryia.softnews;

import android.app.Activity;
import android.os.Bundle;
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
import static com.example.aaryia.softnews.ArticleList.deleteArticles;
import static com.example.aaryia.softnews.SourceList.*;

    /**
     * Created by aaryia on 22/11/17.
     */
    public class ArticleFragment extends android.support.v4.app.Fragment implements AbsListView.OnItemClickListener {

        private final String TAG = "ArticleFragment";
        private View view = null;
        private OnFragmentInteractionListener mListener;

        //The fragment's ListView/GridView.
        private RecyclerView mListView;

        //The Adapter which will be used to populate the ListView/GridView withViews.
        private ArticleAdapter mAdapter;


        public ArticleFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.i(TAG, "onCreate: Successfully created an ArticleFragment ");
            mAdapter = new ArticleAdapter(ARTICLES_ITEMS);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            view = inflater.inflate(R.layout.fragment, container, false);

            // Set the adapter
            mListView = view.findViewById(R.id.recycler);
            mListView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            mListView.setAdapter(mAdapter);

            return view;
        }

        public void onBackPressed() {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.popBackStack();
            return;
        }

        /*
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            try {
                mListener = (OnFragmentInteractionListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnFragmentInteractionListener");
            }


        }
        */

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

