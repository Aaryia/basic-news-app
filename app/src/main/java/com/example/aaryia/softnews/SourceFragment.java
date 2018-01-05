package com.example.aaryia.softnews;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import static com.android.volley.VolleyLog.TAG;
import static com.example.aaryia.softnews.SourceList.*;

/**
 * Created by Martin BABEAU on 22/11/17.
 * SoftNews web APP for lectures
 * Centrale Marseille 2017-2018
 */


public class SourceFragment extends android.support.v4.app.Fragment implements AbsListView.OnItemClickListener {

    private View view = null;
    private OnFragmentInteractionListener mListener;
    private boolean isDrawer = false;

     //The fragment's ListView/GridView.
     private RecyclerView mListView;

     //The Adapter which will be used to populate the ListView/GridView withViews.
     private SourceAdapter mAdapter;



    public SourceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: Successfully created a SourceFragment ");
        if(getArguments()!=null){
            if(getArguments().get("isDrawer")!=null&&getArguments().get("isDrawer").equals(1)){
                    isDrawer=true;
                Log.d(TAG, "onCreate: Je suis un drawer");
            }
        }

        mAdapter = new SourceAdapter(ITEMS,getActivity(),isDrawer);
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
            mListener.onSourceFragmentInteraction(ITEMS.get(position).id);
        }
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSourceFragmentInteraction(String id);

    }
}