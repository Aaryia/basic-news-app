package com.example.aaryia.softnews;

/**
 * Created by aaryia on 23/11/17.
 */

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;


class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final String TAG = "Article Adapter :";
    List<ArticleObject> list;
    private final int LEFT = 0, RIGHT = 1, PROGRESS = 2;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    //Les entiers nécessaires à la mise en place de la progress bar
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) != null) {
            if (position % 2 != 1) {
                return RIGHT;

            } else return LEFT;

        } else return PROGRESS;
    }

    //ajouter un constructeur prenant en entrée une liste
    public ArticleAdapter(List<ArticleObject> list, RecyclerView recyclerView) {
        this.list = list;

        Log.d(TAG, "ArticleAdapter: "+recyclerView.getLayoutManager().toString());

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                Log.d(TAG, "onScrolled: tu as scrollé "+dx+" en x et "+dy+" en y.");
                                // Chargement lancé lorsqu'on atteint la fin de la liste
                                recyclerView.post(new Runnable() {
                                    public void run() {
                                        if (onLoadMoreListener != null) {
                                        onLoadMoreListener.onLoadMore();
                                        }
                                    }
                                });
                                loading = true;
                            }
                        }
                    });
        }


    }

    //cette fonction permet de créer les viewHolder
    //et par la même indiquer la vue à inflate (à partir des layout xml)

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        if (itemType != PROGRESS) {
            if (itemType % 2 == RIGHT) {
                View v1 = inflater.inflate(R.layout.article_item_right, viewGroup, false);
                viewHolder = new ArticleViewHolder(v1);
            } else {
                View v2 = inflater.inflate(R.layout.article_item_left, viewGroup, false);
                viewHolder = new ArticleViewHolderLeft(v2);
            }
        } else {
            View v3 = inflater.inflate(R.layout.progress_bar,viewGroup,false);
            viewHolder = new ProgressViewHolder(v3);
        }
            return viewHolder;
    }

    //Indique à l'adapteur s'il faut rajouter une progress bar
    public void setDone() {
        loading = false;
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
                case PROGRESS:
                    ((ProgressViewHolder) myViewHolder).progressBar.setIndeterminate(true);
                    break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v){
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar_item);
        }
    }



    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}


