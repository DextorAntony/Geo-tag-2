package com.map.geotag.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.map.geotag.R;

public class weather extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        WebView mWebView1 = (WebView) findViewById(R.id.wview1);
        WebSettings webSettings = mWebView1.getSettings();
        webSettings.setJavaScriptEnabled(true);


        mWebView1.loadUrl("https://weather.com/en-IN/weather/today/l/ec37a91cce17fa21d4e58e7bd6e6074688f30a50ff3fa2a48141ba6badde3f08");
        mWebView1.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                view.loadUrl(url);
                return false; // then it is not handled by default action
            }
        });
    }}