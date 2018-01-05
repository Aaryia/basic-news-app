package com.example.aaryia.softnews;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import static com.example.aaryia.softnews.SourceList.ITEMS;

/**
 * Created by aaryia on 23/11/17.
 * SoftNews web APP for lectures
 * Centrale Marseille 2017-2018
 */

class SourceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SourceObject> list;
    private Context contextMain;
    private boolean isDrawer = false;
    private final int DRAWER = 1, NOT_DRAWER = 0;

    //ajouter un constructeur prenant en entrée une liste
    SourceAdapter(List<SourceObject> list, Context contextMain, boolean isDrawer) {
        this.list = list;
        this.contextMain = contextMain;
        this.isDrawer = isDrawer;
    }

    //On vérifie ici si l'on doit afficher les sources pour le drawer ou pour le fragment principal
    @Override
    public int getItemViewType(int position) {
        if (isDrawer) {
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

        switch (itemType) {
            case DRAWER:
                View v1 = inflater.inflate(R.layout.fragment_item_drawer, viewGroup, false);
                viewHolder = new SourceDrawerViewHolder(v1);
                break;

            case NOT_DRAWER:
                View v2 = inflater.inflate(R.layout.fragment_item, viewGroup, false);
                viewHolder = new MyViewHolder(v2, contextMain);
                break;
            default:
                View v3 = inflater.inflate(R.layout.fragment_item, viewGroup, false);
                viewHolder = new MyViewHolder(v3, contextMain);
        }
        return viewHolder;
    }

    //c'est ici que nous allons remplir notre cellule avec le texte/image de chaque MyObjects
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder myViewHolder, int position) {
        SourceObject myObject = list.get(position);
        switch (myViewHolder.getItemViewType()) {
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

        private static final String TAG = "SourceDrawerViewHolder";
        TextView source;

        SourceDrawerViewHolder(View v) {
            super(v);
            source = v.findViewById(R.id.textView_sources);
            v.setOnClickListener(this);
        }

        void bind(SourceObject myObject) {
            source.setText(myObject.getName());
        }


        public void onClick(View view) {
            SourceDisplayActivity sourceDisplayActivity = (SourceDisplayActivity) contextMain;
            Log.i(TAG, "onClick: Activated view = " + ITEMS.get(getLayoutPosition()).getName());
            int itemPosition = getLayoutPosition();
            String source = ITEMS.get(itemPosition).getId();

            if (sourceDisplayActivity.isDrawerOpen) {
                DrawerLayout mDrawerLayout = (DrawerLayout) sourceDisplayActivity.findViewById(R.id.drawer_layout);
                mDrawerLayout.closeDrawers();
            }

            sourceDisplayActivity.volleyConnectionArticles(source);
        }

    }

}