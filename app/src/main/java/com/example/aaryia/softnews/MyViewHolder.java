package com.example.aaryia.softnews;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import static com.example.aaryia.softnews.SourceList.ITEMS;


/**
 * Created by aaryia on 23/11/17.
 * SoftNews web APP for lectures
 * Centrale Marseille 2017-2018
 */

class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private String TAG = "ViewHolder";
    private ImageView imageView;
    private SourceDisplayActivity sourceDisplayActivity;

    //itemView est la vue correspondante à 1 cellule
    MyViewHolder(View itemView, Context contextMain) {
        super(itemView);
        sourceDisplayActivity = (SourceDisplayActivity) contextMain;

        //c'est ici que l'on fait nos findView
        imageView = itemView.findViewById(R.id.imageViewSource);
        itemView.setOnClickListener(this);
    }

    //On remplit ici les images du fragment avec les logos des sources

    void bind(SourceObject myObject){

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


    //Lors du clic sur un item on récupère la View et sa position
    // et on en tire la source qui nous servira pour charger le fragment des articles
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