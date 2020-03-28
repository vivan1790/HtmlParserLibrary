package com.library.htmlparser.iframe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {

    private static String LOAD_DATA = "load_data";

    public static Intent getCallIntent(Context context, String htmlContent) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(LOAD_DATA, htmlContent);
        return intent;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webview = new WebView(this);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setLoadWithOverviewMode(true);
        //webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setDisplayZoomControls(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        setContentView(webview);
        String data = getIntent().getStringExtra(LOAD_DATA);
        if (data != null) {
            webview.loadData(data, "text/html", "utf-8");
        }
    }
}
