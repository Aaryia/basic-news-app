package com.example.aaryia.softnews;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Objects;


/**
 * Created by aaryia on 03/01/18.
 * SoftNews web APP for lectures
 * Centrale Marseille 2017-2018
 */

class ArticleFullAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "Article Full Adapter :";
    private ArticleObject article;
    private SourceDisplayActivity sourceDisplayActivity;

    ArticleFullAdapter(ArticleObject articleObject, Context context){
        this.article = articleObject;
        sourceDisplayActivity = (SourceDisplayActivity) context;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position){
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View v = inflater.inflate(R.layout.article_view_full,viewGroup,false);
        viewHolder = new ArticleFullViewHolder(v, sourceDisplayActivity);
        return viewHolder;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position){
        ArticleFullViewHolder vh = (ArticleFullViewHolder) viewHolder;
        vh.bind(article);
    }

    public int getItemCount() {
        return 1;
    }



    private class ArticleFullViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView description;
        TextView credits;
        ImageView image;
        Button button;
        SourceDisplayActivity sourceDisplayActivity;


        ArticleFullViewHolder(View v, SourceDisplayActivity sourceDisplayActivity) {
            super(v);
            v.setOverScrollMode(View.OVER_SCROLL_NEVER);
            title = v.findViewById(R.id.article_title);
            description = v.findViewById(R.id.article_description);
            credits = v.findViewById(R.id.article_credits);
            image = v.findViewById(R.id.article_image);
            button = v.findViewById(R.id.button);
            this.sourceDisplayActivity = sourceDisplayActivity;
        }

        void bind(ArticleObject myObject) {
            title.setText(myObject.getTitle());
            description.setText(myObject.getDescription());
            String creditString = "";
            if(!Objects.equals(myObject.getAuthor(), "Unknown")){
                creditString = creditString.concat("Auteur : " + myObject.getAuthor());
            }
            if(!Objects.equals(myObject.getSource(), "") && !Objects.equals(myObject.getAuthor(), "Unknown")){
                creditString = creditString.concat(" | Source : " + myObject.getSource());
            }else if(!Objects.equals(myObject.getSource(), "")){
                creditString = creditString.concat("Source : " + myObject.getSource());
            }
            if(!Objects.equals(myObject.getDate(), "")){
                creditString = creditString.concat("\nPublié le : "+myObject.getDate());
            }
            credits.setText(creditString);

            if(article.getUrl()==null|| Objects.equals(article.getUrl(), "")){
                button.setText(R.string.button_no_article);
                button.setClickable(false);
            } else {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: url is : " + article.getUrl());
                        Intent intent = new Intent(sourceDisplayActivity, WebViewActivity.class);
                        intent.putExtra("url", article.getUrl());
                        sourceDisplayActivity.startActivity(intent);
                    }
                });
            }


            if (myObject.getDrawable()!=null){
                image.setImageDrawable(myObject.getDrawable().getDrawable());
            } else {
                Log.d(TAG, "bind: " + myObject.getSource());
                switch (myObject.getSource()) {
                    case "google-news-fr":
                        image.setImageResource(R.drawable.google_logo);
                        break;
                    case "le-monde":
                        image.setImageResource(R.drawable.le_monde);
                        break;
                    case "lequipe":
                        image.setImageResource(R.drawable.l_equipe);
                        break;
                    case "les-echos":
                        image.setImageResource(R.drawable.les_echos);
                        break;
                    case "liberation":
                        image.setImageResource(R.drawable.liberation);
                        break;
                    default:
                        image.setImageResource(R.drawable.question_mark);
                        break;
                }
            }
        }
    }
}
