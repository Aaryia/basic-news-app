package com.example.aaryia.softnews;


import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import static com.example.aaryia.softnews.SourceList.addItem;

public class SourceDisplayActivity extends AppCompatActivity implements SourceFragment.OnFragmentInteractionListener,ArticleFragment.OnFragmentInteractionListener {

    private JSONObject jsonObject = null;
    String TAG = "SourceDisplayActivity";
    public boolean isDrawerOpen = false;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loader);
        volleyConnectionSource(savedInstanceState,this);

    }

    /* Méthode à implementer permettant l’interaction avec le fragment */
    @Override
    public void onSourceFragmentInteraction(String id) {
        //Méthode appelée depuis le fragment
        Log.i(TAG, "onFragmentInteraction: " + id);

    }

    @Override
    public void onArticleFragmentInteraction(String id) {
        //Méthode appelée depuis le fragment
        Log.i(TAG, "onFragmentInteraction: " + id);

    }

    public void onBackPressed(){

        FragmentManager fm = getSupportFragmentManager();
        
        
        if (fm.getBackStackEntryCount() > 0) {
            
            if(fm.findFragmentByTag("Article")!=null&&fm.findFragmentByTag("Article").isVisible()) {
                
                if (fm.findFragmentByTag("Source") != null) {
                    fm.beginTransaction().replace(R.id.container, fm.findFragmentByTag("Source"), "Source").commit();
                    setCanDrawerOpen(false);
                } else {
                    Log.d(TAG, "onBackPressed: aucun fragment nommé source");
                }

            } else if(fm.findFragmentByTag("Article_Full")!=null && fm.findFragmentByTag("Article_Full").isVisible()) {
                fm.beginTransaction().replace(R.id.container, fm.findFragmentByTag("Article"), "Article").commit();
            }

            if(fm.findFragmentByTag("Source")!=null && fm.findFragmentByTag("Source").isVisible()){
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }


    }



    //Méthode permettant de définir si le Drawer peut s'ouvrir ou non
    public void setCanDrawerOpen(boolean b){

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        if(b){
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }




    public void volleyConnectionSource(final Bundle savedInstanceState, final SourceDisplayActivity currentActivity){

        //Volley request pour obtenir le JSONObject contenant les sources
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.url_sources);

        JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonObject = response;

                SourceFragment sourceFragment = new SourceFragment();
                setContentView(R.layout.activity_source_display);

                DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(currentActivity, mDrawerLayout,
                        R.string.drawer_open, R.string.drawer_close) {

                    /** Called when a drawer has settled in a completely closed state. */
                    public void onDrawerClosed(View view) {
                        super.onDrawerClosed(view);
                        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                    }

                    /** Called when a drawer has settled in a completely open state. */
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                        isDrawerOpen = true;
                        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                        SourceFragment sourceFragment = new SourceFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("isDrawer",1);
                        sourceFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.navigation, sourceFragment,"Source_drawer")
                                .commit();
                    }
                };

                // Set the drawer toggle as the DrawerListener
                mDrawerLayout.setDrawerListener(mDrawerToggle);
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, sourceFragment,"Source")
                            .commit();
                }

                Log.i(TAG, "onResponse: " + String.valueOf(jsonObject));

                if (jsonObject != null) {
                    try {
                        for (int i = 0; i < jsonObject.getJSONArray("sources").length(); i++) {

                            Log.i(TAG, "onCreateView: " + jsonObject.getJSONArray("sources").getJSONObject(i).getString("id"));
                            //Puts all the sources in a sourceList.
                            addItem(new SourceObject(jsonObject.getJSONArray("sources").getJSONObject(i)));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onAttach: Error thrown while JSON parsing", e);
                    }
                } else {
                    Log.i(TAG, "onCreateView: JSON Object is null");
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: we got a problem", error);
            }
        });
        // Ajoute la requête à la RequestQueue pour obtenir le JSON approprié
        queue.add(jsObjectRequest);
    }

}