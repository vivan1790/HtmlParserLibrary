package com.library.htmlparser.iframe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.library.htmlparser.R;

import org.jsoup.nodes.Element;

public class HtmlIFrameLayout extends LinearLayout implements View.OnClickListener {

    private WebView webView;
    private View fullScreenButton;
    private Element iFrameElement;
    private boolean dataLoadingRequested = false;

    public HtmlIFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOrientation(LinearLayout.VERTICAL);
        addWebView(context, attrs, defStyleAttr);
        addFullScreenButton(context, attrs, defStyleAttr);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void addWebView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        webView = new WebView(context, attrs, defStyleAttr);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.getLayoutParams().height = view.getMeasuredHeight();
            }
        });
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(webView, layoutParams);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
    }

    private void addFullScreenButton(Context context,
                                     @Nullable AttributeSet attrs, int defStyleAttr) {
        fullScreenButton = new TextView(new ContextThemeWrapper(
                context, R.style.iframe_full_screen_button), attrs, defStyleAttr);
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.END;
        addView(fullScreenButton, layoutParams);
        fullScreenButton.setVisibility(View.GONE);
    }

    public void loadData(Element iFrameElement) {
        if (iFrameElement != null && !iFrameElement.toString().isEmpty()) {
            this.iFrameElement = iFrameElement;
            dataLoadingRequested = true;
        }
    }

    @Override
    public void onClick(View view) {
        if (iFrameElement != null && iFrameElement.attr("src").contains("youtube")) {
            getContext().startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse(iFrameElement.attr("src"))));
        } else {
            getContext().startActivity(
                    WebViewActivity.getCallIntent(getContext(), iFrameElement.toString()));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (dataLoadingRequested) {
            iFrameElement.attr("height", getCalculatedHeight());
            iFrameElement.attr("width", "100%");
            webView.loadData(iFrameElement.toString(), "text/html", "utf-8");
            fullScreenButton.setVisibility(View.VISIBLE);
            fullScreenButton.setOnClickListener(this);
            dataLoadingRequested = false;
        }
    }

    private String getCalculatedHeight() {
        String iFrameWidth = iFrameElement.attr("width"),
                iFrameHeight = iFrameElement.attr("height");
        if (iFrameWidth.isEmpty() || iFrameWidth.contains("%")) return iFrameHeight;
        else {
            int frameWidth = Integer.parseInt(iFrameWidth);
            int frameHeight = Integer.parseInt(iFrameHeight);
            return String.valueOf((frameHeight* getMeasuredWidth()) / frameWidth);
        }
    }
}
