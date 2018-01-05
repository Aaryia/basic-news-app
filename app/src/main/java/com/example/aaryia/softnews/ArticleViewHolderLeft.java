package com.example.aaryia.softnews;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import static com.example.aaryia.softnews.ArticleList.ARTICLES_ITEMS;


/**
 * Created by aaryia on 23/11/17.
 * SoftNews web APP for lectures
 * Centrale Marseille 2017-2018
 */

class ArticleViewHolderLeft extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static String TAG = "ViewHolder";
    private TextView textView_title;
    private ImageView imageView;
    private SourceDisplayActivity sourceDisplayActivity;

    //itemView est la vue correspondante à 1 cellule
    ArticleViewHolderLeft(View itemView, Context contextMain) {
        super(itemView);

        //c'est ici que l'on fait nos findView

        textView_title = itemView.findViewById(R.id.article_title_l);
        imageView = itemView.findViewById(R.id.article_image_l);
        this.sourceDisplayActivity = (SourceDisplayActivity) contextMain;
        itemView.setOnClickListener(this);
    }

    //puis ajouter une fonction pour remplir la cellule en fonction d'un SourceObject

    void bind(ArticleObject myObject) {
        textView_title.setText(myObject.getTitle());

        if (myObject.getDrawable()!=null&&myObject.getDrawable().getDrawable()!=null){
            imageView.setImageDrawable(myObject.getDrawable().getDrawable());
        } else {
            Log.d(TAG, "bind: " + myObject.getSource());
            switch (myObject.getSource()) {
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
    }

    @Override
    public void onClick(View view) {
        Log.i(TAG, "onClick: Activated view = "+ARTICLES_ITEMS.get(getLayoutPosition()).getTitle());
        int itemPosition = getLayoutPosition();
        Bundle bundle = new Bundle();
        bundle.putInt("position",itemPosition);
        ArticleFullFragment articleFullFragment = new ArticleFullFragment();
        articleFullFragment.setArguments(bundle);
        sourceDisplayActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, articleFullFragment,"Article_Full")
                .addToBackStack(null)
                .commit();

    }
}