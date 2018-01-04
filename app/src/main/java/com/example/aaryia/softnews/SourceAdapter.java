package com.example.aaryia.softnews;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.List;

import static com.example.aaryia.softnews.ArticleList.ARTICLES_ITEMS;
import static com.example.aaryia.softnews.ArticleList.addArticleItem;
import static com.example.aaryia.softnews.ArticleList.deleteArticles;
import static com.example.aaryia.softnews.SourceList.ITEMS;

/**
 * Created by aaryia on 23/11/17.
 */

class SourceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<SourceObject> list;
    Context context;
    Context contextMain;
    private boolean isDrawer=false;
    private final int DRAWER = 1, NOT_DRAWER = 0;

    //ajouter un constructeur prenant en entrée une liste
    public SourceAdapter(List<SourceObject> list, Context context,Context contextMain,boolean isDrawer) {
        this.list = list;
        this.context = context;
        this.contextMain = contextMain;
        this.isDrawer = isDrawer;
    }

    @Override
    public int getItemViewType(int position) {
        if(isDrawer){
            return DRAWER;
        } else {
            return NOT_DRAWER;
        }
    }

    //cette fonction permet de créer les viewHolder
    //et par la même indiquer la vue à inflater (à partir des layout xml)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch(itemType) {
            case DRAWER:
                View v1 = inflater.inflate(R.layout.fragment_item_drawer,viewGroup,false);
                viewHolder =  new SourceDrawerViewHolder(v1);
                break;

            case NOT_DRAWER:
                View v2 = inflater.inflate(R.layout.fragment_item, viewGroup, false);
                viewHolder = new MyViewHolder(v2, context, contextMain);
                break;
            default:
                View v3 = inflater.inflate(R.layout.fragment_item, viewGroup, false);
                viewHolder = new MyViewHolder(v3, context, contextMain);
        }
        return viewHolder;
    }

    //c'est ici que nous allons remplir notre cellule avec le texte/image de chaque MyObjects
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder myViewHolder, int position) {
        SourceObject myObject = list.get(position);
        switch(myViewHolder.getItemViewType()) {
            case DRAWER:
                SourceDrawerViewHolder vh1 = (SourceDrawerViewHolder) myViewHolder;
                vh1.bind(myObject);
                break;
            case NOT_DRAWER:
                MyViewHolder vh2 = (MyViewHolder) myViewHolder;
                vh2.bind(myObject);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class SourceDrawerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static final String TAG = "SourceDrawerViewHolder" ;
        public TextView source;
        public CardView cardView;



        public SourceDrawerViewHolder(View v) {
            super(v);
            source = v.findViewById(R.id.textView_sources);
            v.setOnClickListener(this);
        }

        public void bind(SourceObject myObject) {
            source.setText(myObject.getName());
        }


        public void onClick(View view) {
            SourceDisplayActivity sourceDisplayActivity = (SourceDisplayActivity) contextMain;
            Log.i(TAG, "onClick: Activated view = "+ITEMS.get(getLayoutPosition()).getName());
            int itemPosition = getLayoutPosition();
            String source = ITEMS.get(itemPosition).getId();

            if(sourceDisplayActivity.isDrawerOpen){
                DrawerLayout mDrawerLayout = (DrawerLayout) sourceDisplayActivity.findViewById(R.id.drawer_layout);
                mDrawerLayout.closeDrawers();
            }

            volleyConnectionArticles(source);
        }

        //Fonction permettant d'aller chercher les articles si et uniquement si l'utilisateur a changé de source

        public void volleyConnectionArticles(final String source) {

            final SourceDisplayActivity sourceDisplayActivity = (SourceDisplayActivity) contextMain;
            //Volley request pour obtenir le JSONObject contenant les sources
            RequestQueue queue = Volley.newRequestQueue(sourceDisplayActivity);
            String url = sourceDisplayActivity.getString(R.string.url_articles) + source;
            sourceDisplayActivity.setCanDrawerOpen(true);

            if (!(ARTICLES_ITEMS.isEmpty()) && ARTICLES_ITEMS.get(0).getSource() == source) {
                ArticleFragment articleFragment = new ArticleFragment();

                if(sourceDisplayActivity.getSupportFragmentManager().findFragmentByTag("Article")!=null && sourceDisplayActivity.getSupportFragmentManager().findFragmentByTag("Article").isAdded()){
                    sourceDisplayActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, articleFragment)
                            .commit();
                } else {
                    sourceDisplayActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, articleFragment,"Article")
                            .addToBackStack(null)
                            .commit();
                }
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
                                        .replace(R.id.container, articleFragment,"Article")
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

    }

