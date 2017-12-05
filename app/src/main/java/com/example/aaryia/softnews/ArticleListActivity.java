package com.example.aaryia.softnews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import static com.example.aaryia.softnews.ArticleList.addArticleItem;
import static com.example.aaryia.softnews.SourceList.addItem;

/**
 * Created by aaryia on 23/11/17.
 */

public class ArticleListActivity extends AppCompatActivity implements ArticleFragment.OnFragmentInteractionListener{

    private static String TAG = "ArticleListActivity";
    private JSONObject jsonObject = null;
    private String news_source = "";

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loader);
        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            news_source = extras.getString("source");
        }
        volleyConnectionArticles(savedInstanceState,news_source);
    }


    @Override
    public void onFragmentInteraction(String id) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void volleyConnectionArticles(final Bundle savedInstanceState, String source){
        //Volley request pour obtenir le JSONObject contenant les sources
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.url_articles)+source;

        JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                setContentView(R.layout.activity_articles_display);
                jsonObject = response;
                ArticleFragment articleFragment = new ArticleFragment();
                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container_article, articleFragment)
                            .commit();
                }
                Log.i(TAG, "onResponse: " + String.valueOf(jsonObject));

                if (jsonObject != null) {
                    try {
                        for (int i = 0; i < jsonObject.getJSONArray("articles").length(); i++) {

                            Log.i(TAG, "onCreateView: " + jsonObject.getJSONArray("articles").getJSONObject(i).getString("title"));
                            //Puts all the sources in a sourceList.
                            addArticleItem(new ArticleObject(jsonObject.getJSONArray("articles").getJSONObject(i)), news_source);
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

