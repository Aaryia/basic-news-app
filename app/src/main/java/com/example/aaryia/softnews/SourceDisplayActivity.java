package com.example.aaryia.softnews;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import static com.example.aaryia.softnews.SourceList.addItem;

public class SourceDisplayActivity extends AppCompatActivity implements SourceFragment.OnFragmentInteractionListener {

    private JSONObject jsonObject = null;
    String TAG = "SourceDisplayActivity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loader);
        volleyConnectionSource(savedInstanceState);
        setContentView(R.layout.activity_source_display);

    }

    /* Méthode à implementer permettant l’interaction avec le fragment */
    @Override
    public void onFragmentInteraction(String id) {
        //Méthode appelée depuis le fragment
        Log.i(TAG, "onFragmentInteraction: " + id);

    }

    public void volleyConnectionSource(final Bundle savedInstanceState){

        //Volley request pour obtenir le JSONObject contenant les sources
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.url_sources);

        JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonObject = response;
                SourceFragment sourceFragment = new SourceFragment();
                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, sourceFragment)
                            .commit();
                }
                Log.i(TAG, "onResponse: " + String.valueOf(jsonObject));

                if (jsonObject != null) {
                    try {
                        for (int i = 0; i < jsonObject.getJSONArray("sources").length(); i++) {

                            Log.i(TAG, "onCreateView: " + jsonObject.getJSONArray("sources").getJSONObject(i).getString("id"));
                            //Puts all the sources in a sourceList.
                            addItem(new SourceObject(jsonObject.getJSONArray("sources").getJSONObject(i)));
                        }
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


    public void volleyConnectionArticles(final Bundle savedInstanceState,String source){
        //Volley request pour obtenir le JSONObject contenant les sources
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.url_articles)+source;

        JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonObject = response;
                ArticleFragment articleFragment = new ArticleFragment();
                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, articleFragment)
                            .commit();
                }
                Log.i(TAG, "onResponse: " + String.valueOf(jsonObject));

                if (jsonObject != null) {
                    try {
                        for (int i = 0; i < jsonObject.getJSONArray("sources").length(); i++) {

                            Log.i(TAG, "onCreateView: " + jsonObject.getJSONArray("sources").getJSONObject(i).getString("id"));
                            //Puts all the sources in a sourceList.
                            addItem(new SourceObject(jsonObject.getJSONArray("sources").getJSONObject(i)));
                        }
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

}