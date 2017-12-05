package com.example.aaryia.softnews;

/**
 * Created by aaryia on 23/11/17.
 */

import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final String TAG = "Article Adapter :";
    List<ArticleObject> list;
    private final int LEFT = 0, RIGHT = 1;

    @Override
    public int getItemViewType(int position){
        if (position%2!=1){
            return RIGHT;
        }
        else return LEFT;
    }

    //ajouter un constructeur prenant en entrée une liste
    public ArticleAdapter(List<ArticleObject> list) {
        this.list = list;
    }

    //cette fonction permet de créer les viewHolder
    //et par la même indiquer la vue à inflate (à partir des layout xml)

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        if (itemType%2==1) {
            View v1 = inflater.inflate(R.layout.article_item_right, viewGroup, false);
            viewHolder = new ArticleViewHolder(v1);
        } else {
            View v2 = inflater.inflate(R.layout.article_item_left, viewGroup, false);
            viewHolder = new ArticleViewHolderLeft(v2);
        }
        return viewHolder;
    }


    //c'est ici que nous allons remplir notre cellule avec le texte/image de chaque MyObjects
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder myViewHolder, int position) {
        ArticleObject myObject = list.get(position);
        switch (myViewHolder.getItemViewType()) {
            case RIGHT:
                ArticleViewHolder vh1 = (ArticleViewHolder) myViewHolder;
                vh1.bind(myObject);
                break;
            case LEFT:
                ArticleViewHolderLeft vh2 = (ArticleViewHolderLeft) myViewHolder;
                vh2.bind(myObject);
                break;
            default:
                Log.d(TAG, "onBindViewHolder: weird Type viewholder encountered");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}