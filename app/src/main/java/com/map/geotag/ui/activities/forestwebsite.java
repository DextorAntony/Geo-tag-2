package com.map.geotag.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

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
    }
}