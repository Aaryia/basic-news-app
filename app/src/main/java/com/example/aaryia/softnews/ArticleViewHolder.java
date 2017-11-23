package com.example.aaryia.softnews;

import android.content.res.Resources;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by aaryia on 23/11/17.
 */

public class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private static String TAG = "ViewHolder";
    private TextView textView_title;
    private TextView textView_description;
    private ImageView imageView;

    //itemView est la vue correspondante à 1 cellule
    public ArticleViewHolder(View itemView) {
        super(itemView);

        //c'est ici que l'on fait nos findView

        textView_title = itemView.findViewById(R.id.article_title);
        textView_description = itemView.findViewById(R.id.article_description);
        imageView = itemView.findViewById(R.id.article_image);
        itemView.setOnClickListener(this);
    }

    //puis ajouter une fonction pour remplir la cellule en fonction d'un SourceObject

    public void bind(ArticleObject myObject){
        textView_title.setText(myObject.getTitle());
        textView_description.setText(myObject.getDescription());
        if(myObject.getUrlToImage()!=null){

        }

    }

    @Override
    public void onClick(View view) {
        Log.i(TAG, "onClick: Activated view");

    }
}