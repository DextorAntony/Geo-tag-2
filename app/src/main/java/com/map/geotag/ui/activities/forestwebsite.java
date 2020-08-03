package com.map.geotag.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.map.geotag.R;

public class forestwebsite extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forestwebsite);



        WebView mWebView = (WebView) findViewById(R.id.wview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        mWebView.loadUrl("http://ls1.and.nic.in/doef/");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onLoadResource(WebView view, String url) {
                // Notice Here.
                view.clearHistory();
                super.onLoadResource(view, url);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                // And Here.
                view.clearHistory();
                super.onPageFinished(view,url);
            }
        });

    }

}