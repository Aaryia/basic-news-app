package com.example.aaryia.softnews;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;


/**
 * Created by aaryia on 23/11/17.
 */

public class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

    //puis ajouter une fonction pour remplir la cellule en fonction d'un ArticleObject

    public void bind(ArticleObject myObject) {
        textView_title.setText(myObject.getTitle());
        textView_description.setText(myObject.getDescription());
        if (myObject.getUrlToImage() != null && !myObject.getUrlToImage().isEmpty() && myObject.getUrlToImage() != "null"){
            Log.d(TAG, "bind: " + myObject.getUrlToImage());
        new DownLoadImageTask(imageView).execute(myObject.getUrlToImage());
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
            Log.i(TAG, "onClick: Activated view");
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