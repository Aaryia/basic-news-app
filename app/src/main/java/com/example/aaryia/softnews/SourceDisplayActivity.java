package com.example.aaryia.softnews;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import static com.example.aaryia.softnews.ArticleList.ARTICLES_ITEMS;
import static com.example.aaryia.softnews.ArticleList.addArticleItem;
import static com.example.aaryia.softnews.ArticleList.deleteArticles;
import static com.example.aaryia.softnews.SourceList.ITEMS;
import static com.example.aaryia.softnews.SourceList.addItem;

public class SourceDisplayActivity extends AppCompatActivity implements SourceFragment.OnFragmentInteractionListener,ArticleFragment.OnFragmentInteractionListener {

    private JSONObject jsonObject = null;
    String TAG = "SourceDisplayActivity";
    public boolean isDrawerOpen = false, NON_DISPLAYED=true;

    private ProgressBar progressBar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private Bundle savedInstanceState;
    public final int SOURCE = 0, ARTICLE_LIST_NO_CHANGE = 1, ARTICLE_FULL = 2, ARTICLE_LIST = 3;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_display);
        this.savedInstanceState = savedInstanceState;

        //On retrouve l'instance du DrawerLayout pour le remplir plus tard

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                isDrawerOpen = true;
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                SourceFragment sourceFragment = new SourceFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("isDrawer", 1);
                sourceFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.navigation, sourceFragment, "Source_drawer")
                        .commit();
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        //Chargement du bon fragment selon la dernière source choisie par l'utilisateur
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        String source = sharedPref.getString(getString(R.string.last_sources),"no source");
        editor = sharedPref.edit();

        if (source!="no source") {
            volleyConnectionArticles(source);
            volleyConnectionSource(NON_DISPLAYED);
        } else {
            volleyConnectionSource();
        }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Pour la touche retour arrière : s'il y a un fragment dans le FragmentManager, on regarde quel est le fragment visible.
    * Si c'est un article on revient à la liste des articles.
    * Si c'est la liste des articles on revient à la liste des sources,
    * Et si c'est la liste des sources on ferme l'application.
    * */

    public void onBackPressed(){

        FragmentManager fm = getSupportFragmentManager();
        
        
        if (fm.getBackStackEntryCount() > 0) {
            
            if(fm.findFragmentByTag("Article")!=null&&fm.findFragmentByTag("Article").isVisible()) {
                
                if (fm.findFragmentByTag("Source") != null) {
                    fm.beginTransaction().replace(R.id.container, fm.findFragmentByTag("Source"), "Source").commit();

                    //De retour sur les sources, on verrouille le drawer.
                    setCanDrawerOpen(false);

                } else {
                    fm.beginTransaction().hide(fm.findFragmentByTag("Article")).commit();
                    progressBar.setVisibility(View.VISIBLE);
                    volleyConnectionSource();
                }
                    // Si l'on revient sur les sources, on enlève le hamburger.
                if(getSupportActionBar()!=null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setHomeButtonEnabled(false);
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

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        if(b){
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }




    public void volleyConnectionSource(){

        //Volley request pour obtenir le JSONObject contenant les sources
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.url_sources);

        if(ITEMS!=null&& ITEMS.isEmpty()) {
            JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    jsonObject = response;

                    changeFragment(SOURCE);

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
        } else {
            changeFragment(SOURCE);
        }
    }
    public void volleyConnectionSource(final boolean b){

        //Volley request pour obtenir le JSONObject contenant les sources
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.url_sources);
        if(ITEMS!=null&& ITEMS.isEmpty()) {
            JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    jsonObject = response;
                    if (b != NON_DISPLAYED) {
                        changeFragment(SOURCE);
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

    //Fonction permettant d'aller chercher les articles si et uniquement si l'utilisateur a changé de source

    public void volleyConnectionArticles(final String source) {
        //Volley request pour obtenir le JSONObject contenant les sources
        RequestQueue queue = Volley.newRequestQueue(this);

        editor.putString(getString(R.string.last_sources),source);
        editor.commit();

        String url = getString(R.string.url_articles) + source;

        progressBar.setVisibility(View.VISIBLE);
        setCanDrawerOpen(true);

        if (!(ARTICLES_ITEMS.isEmpty()) && ARTICLES_ITEMS.get(0).getSource() == source) {
            changeFragment(ARTICLE_LIST_NO_CHANGE);
        } else {

            deleteArticles();
            JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject jsonObject = response;
                    Log.i(TAG, "onResponse: " + String.valueOf(jsonObject));

                    if (jsonObject != null) {
                        try {
                            for (int i = 0; i < jsonObject.getJSONArray("articles").length(); i++) {

                                Log.i(TAG, "onCreateView: " + jsonObject.getJSONArray("articles").getJSONObject(i).getString("title"));
                                //Puts all the sources in a sourceList.
                                addArticleItem(new ArticleObject(jsonObject.getJSONArray("articles").getJSONObject(i), source));
                            }
                            changeFragment(ARTICLE_LIST);

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
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void changeFragment(int fragmentType){

        ArticleFragment articleFragment = new ArticleFragment();

        switch(fragmentType){

            // S'il s'agit d'une source, on crée un fragment source, et on ne le
            // remplit que si l'application n'a jamais rempli la liste.
            case SOURCE:

                SourceFragment sourceFragment = new SourceFragment();

                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                if (this.savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, sourceFragment, "Source")
                        .commit();
                }
                break;

            case ARTICLE_LIST_NO_CHANGE:

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                } else {
                    Snackbar.make(findViewById(R.id.parent),"Hey I have no toolbar",Snackbar.LENGTH_LONG).show();
                }

                if(getSupportFragmentManager().findFragmentByTag("Article")!=null && getSupportFragmentManager().findFragmentByTag("Article").isAdded()){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, articleFragment)
                            .commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, articleFragment,"Article")
                            .addToBackStack(null)
                            .commit();
                }

                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                break;

            case ARTICLE_FULL:

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                } else {
                    Snackbar.make(findViewById(R.id.parent),"Hey I have no toolbar",Snackbar.LENGTH_LONG).show();
                }

                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                break;

            case ARTICLE_LIST:

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                } else {
                    Snackbar.make(findViewById(R.id.parent),"Hey I have no toolbar",Snackbar.LENGTH_LONG).show();
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, articleFragment,"Article")
                        .addToBackStack(null)
                        .commit();
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                break;

        }
        progressBar.setVisibility(View.INVISIBLE);
    }

}