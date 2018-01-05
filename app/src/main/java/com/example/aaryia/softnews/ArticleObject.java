package com.example.aaryia.softnews;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;

/**
 * Created by aaryia on 23/11/17.
 * SoftNews web APP for lectures
 * Centrale Marseille 2017-2018
 */

class ArticleObject {

    private String source = "";
    private String author = "Unknown";
    private String title = "";
    private String url = "";
    private String date = "";
    private String description = "Pas de description pour cet article, désolé";
    private ImageView drawable;


    ArticleObject(JSONObject jsonObject, String source, Context context){
        try{
            if(jsonObject.getString("author")!=null&& !Objects.equals(jsonObject.getString("author"), "null")){author = jsonObject.getString("author");}
            if(jsonObject.getString("title")!=null&& !Objects.equals(jsonObject.getString("title"), "null")){title = jsonObject.getString("title");}
            if(jsonObject.getString("url")!=null&& !Objects.equals(jsonObject.getString("url"), "null")){url = jsonObject.getString("url");}
            if(jsonObject.getString("description")!=null&& !Objects.equals(jsonObject.getString("description"), "null")){description = jsonObject.getString("description");}
            if(jsonObject.getString("publishedAt")!=null&& !Objects.equals(jsonObject.getString("publishedAt"), "null")){date = jsonObject.getString("publishedAt").substring(0,10);}
            this.source = source;

            drawable = new ImageView(context);
            if(jsonObject.getString("urlToImage")!=null&&!Objects.equals(jsonObject.getString("urlToImage"), "null")&&(jsonObject.getString("urlToImage").contains(".jpg")||jsonObject.getString("urlToImage").contains(".png"))){
                    new DownloadImageTask(drawable).execute(jsonObject.getString("urlToImage"));
            }else{drawable = null;}
        } catch (JSONException e){
            Log.e("Article Object", "ArticleObject: Error Parsing JSON", e);
        }
    }


    public ImageView getDrawable(){return drawable;}

    String getDate(){
        return date;
    }

    String getSource() {
        return source;
    }

    String getAuthor() {
        return author;
    }

    String getTitle() {
        return title;
    }

    String getUrl() {
        return url;
    }

    String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Titre de l'article : "+title;
    }
}
