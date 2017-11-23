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

import static com.android.volley.VolleyLog.TAG;
import static com.example.aaryia.softnews.SourceList.*;

/**
 * Created by aaryia on 22/11/17.
 */


public class SourceFragment extends android.support.v4.app.Fragment implements AbsListView.OnItemClickListener {

    private View view = null;
    private OnFragmentInteractionListener mListener;

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
        mAdapter = new SourceAdapter(ITEMS,getContext());
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
            mListener.onFragmentInteraction(ITEMS.get(position).id);
        }
    }


    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.

     * This interface must be implemented by activities that contain this* fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }
}