package com.example.aaryia.softnews;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import javax.xml.transform.Source;

import static com.example.aaryia.softnews.ArticleList.ARTICLES_ITEMS;
import static com.example.aaryia.softnews.ArticleList.addArticleItem;
import static com.example.aaryia.softnews.ArticleList.deleteArticles;
import static com.example.aaryia.softnews.SourceList.ITEMS;


/**
 * Created by aaryia on 23/11/17.
 */

public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private static String TAG = "ViewHolder";
    private TextView textView;
    private ImageView imageView;
    Context context;
    SourceDisplayActivity sourceDisplayActivity;

    //itemView est la vue correspondante à 1 cellule
    public MyViewHolder(View itemView,Context context,Context contextMain) {
        super(itemView);
        this.context=context;
        sourceDisplayActivity = (SourceDisplayActivity) contextMain;

        //c'est ici que l'on fait nos findView

        textView = (TextView) itemView.findViewById(R.id.textView_sources);
        imageView = (ImageView) itemView.findViewById(R.id.imageViewSource);
        itemView.setOnClickListener(this);
    }

    //puis ajouter une fonction pour remplir la cellule en fonction d'un SourceObject

    public void bind(SourceObject myObject){
        textView.setText(myObject.getName());
        switch(myObject.getId()) {
            case "google-news-fr":
                imageView.setImageResource(R.drawable.google_logo);
                break;
            case "le-monde":
                imageView.setImageResource(R.drawable.le_monde);
                break;
            case "lequipe":
                imageView.setImageResource(R.drawable.l_equipe);
                break;
            case "les-echos":
                imageView.setImageResource(R.drawable.les_echos);
                break;
            case "liberation":
                imageView.setImageResource(R.drawable.liberation);
                break;
            default:
                imageView.setImageResource(R.drawable.question_mark);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        Log.i(TAG, "onClick: Activated view = "+ITEMS.get(getLayoutPosition()).getName());
        int itemPosition = getLayoutPosition();
        String source = ITEMS.get(itemPosition).getId();
        volleyConnectionArticles(source);
        /*
        Intent myIntent = new Intent(context,ArticleListActivity.class);
        myIntent.putExtra("source",source);
        context.startActivity(myIntent);
        */

    }

    public void volleyConnectionArticles(final String source) {
        //Volley request pour obtenir le JSONObject contenant les sources
        RequestQueue queue = Volley.newRequestQueue(sourceDisplayActivity);
        String url = sourceDisplayActivity.getString(R.string.url_articles) + source;

        if (!(ARTICLES_ITEMS.isEmpty()) && ARTICLES_ITEMS.get(0).getSource() == source) {
            ArticleFragment articleFragment = new ArticleFragment();
            sourceDisplayActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, articleFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            deleteArticles();
            JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject jsonObject = response;
                    Log.i(TAG, "onResponse: " + String.valueOf(jsonObject));

                    if (jsonObject != null) {
                        try {
                            for (int i = 0; i < jsonObject.getJSONArray("articles").length(); i++) {

                                Log.i(TAG, "onCreateView: " + jsonObject.getJSONArray("articles").getJSONObject(i).getString("title"));
                                //Puts all the sources in a sourceList.
                                addArticleItem(new ArticleObject(jsonObject.getJSONArray("articles").getJSONObject(i), source));
                            }

                            ArticleFragment articleFragment = new ArticleFragment();
                            sourceDisplayActivity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, articleFragment)
                                    .addToBackStack(null)
                                    .commit();
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
}