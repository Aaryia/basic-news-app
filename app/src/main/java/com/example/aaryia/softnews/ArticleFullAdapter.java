package com.example.aaryia.softnews;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by aaryia on 03/01/18.
 */

public class ArticleFullAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "Article Full Adapter :";
    ArticleObject article;

    public ArticleFullAdapter(ArticleObject articleObject){
        this.article = articleObject;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position){
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View v = inflater.inflate(R.layout.article_view_full,viewGroup,false);
        viewHolder = new ArticleFullViewHolder(v);
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


        public ArticleFullViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.article_title);
            description = v.findViewById(R.id.article_description);
            credits = v.findViewById(R.id.article_credits);
            image = v.findViewById(R.id.article_image);
            button = v.findViewById(R.id.button);
        }

        public void bind(ArticleObject myObject) {
            title.setText(myObject.getTitle());
            description.setText(myObject.getDescription());
            credits.setText("Auteur : " + myObject.getAuthor() + " | Source : " + myObject.getSource());
            if (myObject.getUrlToImage() != null && !myObject.getUrlToImage().isEmpty() && myObject.getUrlToImage() != "null") {
                Log.d(TAG, "bind: " + myObject.getUrlToImage());
                new DownLoadImageTask(image).execute(myObject.getUrlToImage());
            }
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
