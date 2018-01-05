package com.example.aaryia.softnews;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aaryia on 23/11/17.
 * SoftNews web APP for lectures
 * Centrale Marseille 2017-2018
 */

class SourceObject {

    protected String id = null;
    protected String name = null;
    private String url = null;


    SourceObject(JSONObject jsonObject){
        try {
            id = jsonObject.getString("id");
            name = jsonObject.getString("name");
            url = jsonObject.getString("url");


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

}
