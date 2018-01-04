package com.example.aaryia.softnews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.DrawerLayout;
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

    private String TAG = "ViewHolder";
    private ImageView imageView;
    private Context context;
    private SourceDisplayActivity sourceDisplayActivity;

    //itemView est la vue correspondante à 1 cellule
    public MyViewHolder(View itemView,Context context,Context contextMain) {
        super(itemView);
        this.context=context;
        sourceDisplayActivity = (SourceDisplayActivity) contextMain;

        //c'est ici que l'on fait nos findView
        imageView = itemView.findViewById(R.id.imageViewSource);
        itemView.setOnClickListener(this);
    }

    //puis ajouter une fonction pour remplir la cellule en fonction d'un SourceObject

    public void bind(SourceObject myObject){

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

        if(sourceDisplayActivity.isDrawerOpen){
            DrawerLayout mDrawerLayout = (DrawerLayout) sourceDisplayActivity.findViewById(R.id.drawer_layout);
            mDrawerLayout.closeDrawers();
        }

        sourceDisplayActivity.volleyConnectionArticles(source);
    }
}