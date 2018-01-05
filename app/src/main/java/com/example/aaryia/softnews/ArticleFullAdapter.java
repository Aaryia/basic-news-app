package com.example.aaryia.softnews;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.Source;

/**
 * Created by aaryia on 03/01/18.
 */

public class ArticleFullAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "Article Full Adapter :";
    ArticleObject article;
    SourceDisplayActivity sourceDisplayActivity;

    public ArticleFullAdapter(ArticleObject articleObject, Context context){
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

        public TextView title;
        public TextView description;
        public TextView credits;
        public ImageView image;
        public Button button;
        public WebView webView;
        public SourceDisplayActivity sourceDisplayActivity;


        public ArticleFullViewHolder(View v, SourceDisplayActivity sourceDisplayActivity) {
            super(v);
            v.setOverScrollMode(View.OVER_SCROLL_NEVER);
            title = v.findViewById(R.id.article_title);
            description = v.findViewById(R.id.article_description);
            credits = v.findViewById(R.id.article_credits);
            image = v.findViewById(R.id.article_image);
            button = v.findViewById(R.id.button);
            this.sourceDisplayActivity = sourceDisplayActivity;
        }

        public void bind(ArticleObject myObject) {
            title.setText(myObject.getTitle());
            description.setText(myObject.getDescription());
            String creditString = "";
            if(myObject.getAuthor()!="Unknown"){
                creditString = creditString.concat("Auteur : " + myObject.getAuthor());
            }
            if(myObject.getSource()!=""&&myObject.getAuthor()!="Unknown"){
                creditString = creditString.concat(" | Source : " + myObject.getSource());
            }else if(myObject.getSource()!=""){
                creditString = creditString.concat("Source : " + myObject.getSource());
            }
            if(myObject.getDate()!=""){
                creditString = creditString.concat("\nPublié le : "+myObject.getDate());
            }
            credits.setText(creditString);


            if (myObject.getUrlToImage() != null && !myObject.getUrlToImage().isEmpty() && myObject.getUrlToImage() != "null") {
                Log.d(TAG, "bind: " + myObject.getUrlToImage());
                new DownLoadImageTask(image).execute(myObject.getUrlToImage());
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(sourceDisplayActivity,WebViewActivity.class);
                    intent.putExtra("url",article.getUrl());
                    sourceDisplayActivity.startActivity(intent);
                }
            });
        }

    }


    private class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
