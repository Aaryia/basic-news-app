package com.example.aaryia.softnews;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

public class WebViewActivity extends AppCompatActivity {

        private String url = "";
        private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_web_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.webview_toolbar);
        setSupportActionBar(toolbar);

        webView = (WebView)findViewById(R.id.webview);

        if(getIntent().hasExtra("url")){
            url = getIntent().getStringExtra("url");
            webView = (WebView)findViewById(R.id.webview);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setSupportZoom(true);
            webView.loadUrl(url);
        } else {
            this.finish();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } else {
            Snackbar.make(findViewById(R.id.webview),"Hey I have no toolbar",Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
