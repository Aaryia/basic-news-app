package com.example.aaryia.softnews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aaryia on 23/11/17.
 */

class SourceObject {

    protected String id = null;
    protected String name = null;
    protected String description = null;
    protected String url = null;
    protected String category = null;
    protected String country = null;

    public SourceObject(JSONObject jsonObject){
        try {
            id = jsonObject.getString("id");
            name = jsonObject.getString("name");
            description = jsonObject.getString("description");
            url = jsonObject.getString("url");
            category = jsonObject.getString("category");
            country = jsonObject.getString("country");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "ID : "+id+"\n"+" is found at "+url;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
