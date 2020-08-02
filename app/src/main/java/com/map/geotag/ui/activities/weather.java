package com.map.geotag.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.map.geotag.R;

public class weather extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
       WebView mWebView = (WebView) findViewById(R.id.wview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        mWebView.loadUrl("https://weather.com/en-IN/weather/today/l/ec37a91cce17fa21d4e58e7bd6e6074688f30a50ff3fa2a48141ba6badde3f08");
    }
}