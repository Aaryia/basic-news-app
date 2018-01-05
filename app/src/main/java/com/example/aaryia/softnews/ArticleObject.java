package com.example.aaryia.softnews;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by aaryia on 23/11/17.
 */

class ArticleObject {

    private String source = "";
    private String author = "Unknown";
    private String title = "";
    private String url = "";
    private String date = "";
    private String description = "Pas de description pour cet article, désolé";
    private String urlToImage = "";


    ArticleObject(JSONObject jsonObject, String source){
        try{
            if(jsonObject.getString("author")!=null&& !Objects.equals(jsonObject.getString("author"), "null")){author = jsonObject.getString("author");}
            if(jsonObject.getString("title")!=null&& !Objects.equals(jsonObject.getString("title"), "null")){title = jsonObject.getString("title");}
            if(jsonObject.getString("url")!=null&& !Objects.equals(jsonObject.getString("url"), "null")){url = jsonObject.getString("url");}
            if(jsonObject.getString("urlToImage")!=null&& !Objects.equals(jsonObject.getString("urlToImage"), "null")){urlToImage = jsonObject.getString("urlToImage");}
            if(jsonObject.getString("description")!=null&& !Objects.equals(jsonObject.getString("description"), "null")){description = jsonObject.getString("description");}
            if(jsonObject.getString("publishedAt")!=null&& !Objects.equals(jsonObject.getString("publishedAt"), "null")){date = jsonObject.getString("publishedAt").substring(0,10);}
            this.source = source;
        } catch (JSONException e){
            Log.e("Article Object", "ArticleObject: Error Parsing JSON", e);
        }
    }



    public String getDate(){
        return date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return "Titre de l'article : "+title;
    }
}
