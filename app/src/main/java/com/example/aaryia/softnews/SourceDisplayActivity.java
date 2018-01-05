package com.example.aaryia.softnews;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.Objects;
import static com.example.aaryia.softnews.ArticleList.ARTICLES_ITEMS;
import static com.example.aaryia.softnews.ArticleList.addArticleItem;
import static com.example.aaryia.softnews.ArticleList.deleteArticles;
import static com.example.aaryia.softnews.SourceList.ITEMS;
import static com.example.aaryia.softnews.SourceList.addItem;

public class SourceDisplayActivity extends AppCompatActivity implements SourceFragment.OnFragmentInteractionListener,ArticleFragment.OnFragmentInteractionListener {

    private JSONObject jsonObject = null;
    String TAG = "SourceDisplayActivity";
    public boolean isDrawerOpen = false, NON_DISPLAYED=true, ERROR_SHOWING=false, LOAD_MORE=true;

    private String source;
    private ProgressBar progressBar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private int batchNumber = 0;
    private Bundle savedInstanceState;
    public final int SOURCE = 0, ARTICLE_LIST_NO_CHANGE = 1, ARTICLE_FULL = 2, ARTICLE_LIST = 3,TIMEOUT=3000;

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
        source = sharedPref.getString(getString(R.string.last_sources),"no source");
        editor = sharedPref.edit();

        // On lance le téléchargement des données
        initiateDownload();


    }

    //Méthodes appelées depuis les fragments
    @Override
    public void onSourceFragmentInteraction(String id) {
        Log.i(TAG, "onFragmentInteraction: " + id);
    }
    @Override
    public void onArticleFragmentInteraction(String id) {
        //Méthode appelée depuis le fragment
        Log.i(TAG, "onFragmentInteraction: " + id);

    }

    //Gestion du clic sur le bouton de la toolbar s'il est présent.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /* Pour la touche retour arrière : s'il y a un fragment dans le FragmentManager, on regarde quel est le fragment visible.
    * Si c'est un article on revient à la liste des articles.
    * Si c'est la liste des articles on revient à la liste des sources,
    * Et si c'est la liste des sources on ferme l'application.
    */
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


    // Méthodes volley allant charger les sources.
    // La première sert pour le chargement du fragment principal et le second pour le drawerLayout.
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
                public void onErrorResponse(VolleyError volleyError) {volleyError(volleyError);}
            });

            jsObjectRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT,2,1));

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
              public void onErrorResponse(VolleyError volleyError) {volleyError(volleyError);}});

            jsObjectRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT,2,1));

            // Ajoute la requête à la RequestQueue pour obtenir le JSON approprié
            queue.add(jsObjectRequest);
        }
    }


    // Méthodes permettant d'aller chercher les articles si et uniquement si l'utilisateur a changé de source
    public void volleyConnectionArticles(final String source) {

        batchNumber=0;
        //Volley request pour obtenir le JSONObject contenant les sources
        RequestQueue queue = Volley.newRequestQueue(this);

        editor.putString(getString(R.string.last_sources),source);
        editor.commit();

        String url = getString(R.string.url_articles) + source;

        progressBar.setVisibility(View.VISIBLE);
        setCanDrawerOpen(true);

        if (!(ARTICLES_ITEMS.isEmpty()) && Objects.equals(ARTICLES_ITEMS.get(0).getSource(), source)) {
            changeFragment(ARTICLE_LIST_NO_CHANGE);
        } else {

            deleteArticles();
            JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "onResponse: " + String.valueOf(response));

                    if (response != null) {
                        try {
                            for (int i = 0; i < response.getJSONArray("articles").length(); i++) {

                                Log.i(TAG, "onCreateView: " + response.getJSONArray("articles").getJSONObject(i).getString("title"));

                                //On rajoute les articles à la liste des articles
                                addArticleItem(new ArticleObject(response.getJSONArray("articles").getJSONObject(i), source,SourceDisplayActivity.this));
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
                public void onErrorResponse(VolleyError volleyError) {volleyError(volleyError);}
            });

            jsObjectRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT,2,1));

            // Ajoute la requête à la RequestQueue pour obtenir le JSON approprié
            queue.add(jsObjectRequest);

        }
    }

    // Méthodes permettant d'aller chercher les prochaines pages de la source voulue
    public void volleyArticleNextPage(){
        int pageNumber;
        pageNumber = ((ARTICLES_ITEMS.size()+1)/20)+1;

        if(pageNumber>10){

            ERROR_SHOWING=true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Charger plus d'articles ?");

            builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ERROR_SHOWING=false;
                }
            });

            builder.setPositiveButton("J'en veux plus !", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ERROR_SHOWING=false;
                    batchNumber++;
                    source = ARTICLES_ITEMS.get(0).getSource();
                    deleteArticles();
                    volleyArticleNextPage(source);
                }
            });
            AlertDialog dialog = builder.create(); // calling builder.create after adding buttons
            dialog.show();
        } else {
            RequestQueue queue = Volley.newRequestQueue(this);
            final String source = ARTICLES_ITEMS.get(0).getSource();
            String url = getString(R.string.url_articles) + ARTICLES_ITEMS.get(0).getSource() + "&page=" + pageNumber + batchNumber * 10;

            JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "onResponse: " + String.valueOf(response));

                    if (response != null) {
                        try {

                            // On rajoute tous les nouveaux articles dans la liste
                            for (int i = 0; i < response.getJSONArray("articles").length(); i++) {

                                Log.i(TAG, "onCreateView: " + response.getJSONArray("articles").getJSONObject(i).getString("title"));
                                //Puts all the sources in a sourceList.
                                addArticleItem(new ArticleObject(response.getJSONArray("articles").getJSONObject(i), source, SourceDisplayActivity.this));
                            }

                            // Notification du changement et que le chargement est fini.
                            if (getSupportFragmentManager().findFragmentByTag("Article") != null) {
                                ArticleFragment articleFragment = (ArticleFragment) getSupportFragmentManager().findFragmentByTag("Article");
                                if (articleFragment != null) {
                                    articleFragment.mAdapter.notifyItemInserted(ARTICLES_ITEMS.size());
                                    articleFragment.mAdapter.setDone();
                                } else {
                                    Log.d(TAG, "onResponse: No Fragment");
                                }
                            } else {
                                Log.d(TAG, "onResponse: We got a null Fragment");
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
                    volleyError(error, LOAD_MORE);
                }
            });
            // Ajoute la requête à la RequestQueue pour obtenir le JSON approprié
            queue.add(jsObjectRequest);
        }
    }
    public void volleyArticleNextPage(String sourcePassed){
        int pageNumber;
        pageNumber = ((ARTICLES_ITEMS.size()+1)/20)+1;

        if(pageNumber>10){

            ERROR_SHOWING=true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Error");
            builder.setMessage("No more space");

            builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ERROR_SHOWING=false;
                    finish();
                }
            });

            builder.setPositiveButton("J'en veux plus !", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ERROR_SHOWING=false;
                    batchNumber++;
                    source = ARTICLES_ITEMS.get(0).getSource();
                    deleteArticles();
                    volleyArticleNextPage(source);
                }
            });
            AlertDialog dialog = builder.create(); // calling builder.create after adding buttons
            dialog.show();
        } else {
            RequestQueue queue = Volley.newRequestQueue(this);
            final String source = sourcePassed;
            String url = getString(R.string.url_articles) + source + "&page=" + pageNumber + batchNumber * 10;

            JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "onResponse: " + String.valueOf(response));

                    if (response != null) {
                        try {

                            // On rajoute tous les nouveaux articles dans la liste
                            for (int i = 0; i < response.getJSONArray("articles").length(); i++) {

                                Log.i(TAG, "onCreateView: " + response.getJSONArray("articles").getJSONObject(i).getString("title"));
                                //Puts all the sources in a sourceList.
                                addArticleItem(new ArticleObject(response.getJSONArray("articles").getJSONObject(i), source, SourceDisplayActivity.this));
                            }

                            // Notification du changement et que le chargement est fini.
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
                    volleyError(error, LOAD_MORE);
                }
            });
            // Ajoute la requête à la RequestQueue pour obtenir le JSON approprié
            queue.add(jsObjectRequest);
        }
    }


    // Méthode gérant le retour d'erreur des volleys, affichant le popup
    // et lançant les volleys à nouveau si l'utilisateur clique sur retry
    public void volleyError(VolleyError volleyError){
        String message = null;

        if (volleyError instanceof NetworkError) {
            message = "Cannot connect to Internet...Please check your connection!";
            Log.e(TAG, "onErrorResponse: VolleyError"+volleyError.toString(),volleyError );

        } else if (volleyError instanceof ServerError) {
            message = "The server could not be found. Please try again after some time!!";
            Log.e(TAG, "onErrorResponse: VolleyError"+volleyError.toString(),volleyError );

        } else if (volleyError instanceof AuthFailureError) {
            message = "Cannot connect to Internet...Please check your connection!";
            Log.e(TAG, "onErrorResponse: VolleyError"+volleyError.toString(),volleyError );

        } else if (volleyError instanceof ParseError) {
            message = "Parsing error! Please try again after some time!!";
            Log.e(TAG, "onErrorResponse: VolleyError"+volleyError.toString(),volleyError );

        } else if (volleyError instanceof TimeoutError) {
            message = "Connection TimeOut! Please check your internet connection.";
            Log.e(TAG, "onErrorResponse: VolleyError"+volleyError.toString(),volleyError );

        } else {
            Log.e(TAG, "onErrorResponse: VolleyError"+volleyError.toString(),volleyError );
        }

        if(!ERROR_SHOWING) {

            final String finalMessage = message + "\nPlease retry.";

            ERROR_SHOWING=true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Connection Error");
            builder.setMessage(finalMessage);
            builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ERROR_SHOWING = false;
                        finish();
                    }
            });

            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ERROR_SHOWING=false;
                    initiateDownload();
                }
            });
            AlertDialog dialog = builder.create(); // calling builder.create after adding buttons
            dialog.show();
        }
    }
    public void volleyError(VolleyError volleyError, final boolean b){
        String message = null;

        if (volleyError instanceof NetworkError) {
            message = "Cannot connect to Internet...Please check your connection!";
            Log.e(TAG, "onErrorResponse: VolleyError"+volleyError.toString(),volleyError );

        } else if (volleyError instanceof ServerError) {
            message = "The server could not be found. Please try again after some time!!";
            Log.e(TAG, "onErrorResponse: VolleyError"+volleyError.toString(),volleyError );

        } else if (volleyError instanceof AuthFailureError) {
            message = "Cannot connect to Internet...Please check your connection!";
            Log.e(TAG, "onErrorResponse: VolleyError"+volleyError.toString(),volleyError );

        } else if (volleyError instanceof ParseError) {
            message = "Parsing error! Please try again after some time!!";
            Log.e(TAG, "onErrorResponse: VolleyError"+volleyError.toString(),volleyError );

        } else if (volleyError instanceof TimeoutError) {
            message = "Connection TimeOut! Please check your internet connection.";
            Log.e(TAG, "onErrorResponse: VolleyError"+volleyError.toString(),volleyError );

        } else {
            Log.e(TAG, "onErrorResponse: VolleyError"+volleyError.toString(),volleyError );
        }

        if(!ERROR_SHOWING) {

            ERROR_SHOWING=true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Connection Error");
            builder.setMessage(message + "\nPlease retry.");

            builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ERROR_SHOWING=false;
                    finish();
                }
            });

            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ERROR_SHOWING=false;
                    if(b){
                        volleyArticleNextPage();
                    }
                }
            });
            AlertDialog dialog = builder.create(); // On crée le builder après avoir appelé les boutons
            dialog.show();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    //Méthode permettant de changer de fragment affiché dans l'activité
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


    //Méthode lançant les volleys pour récuperer du site NewsAPI les informations
    // en fonction de la dernière source visitée par l'utilisateur
    public void initiateDownload(){
        if (!Objects.equals(source, "no source")) {
            Log.d(TAG, "onCreate: there is an initial source : "+source);
            volleyConnectionArticles(source);
            volleyConnectionSource(NON_DISPLAYED);
        } else {
            Log.d(TAG, "onCreate: There is no  initial source");
            volleyConnectionSource();
        }
    }

}