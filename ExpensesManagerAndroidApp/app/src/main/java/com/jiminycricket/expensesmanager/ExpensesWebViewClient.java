package com.jiminycricket.expensesmanager;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Jiminy Cricket on 6/25/2016.
 */
public class ExpensesWebViewClient extends WebViewClient {
    /**
     * will override loading URLs that contain ExpensesMangerServer
     * so this application will be viewed through the webview
     * @param view
     * @param url
     * @return
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        if (url.contains("ExpensesMangerServer"))
        {
            view.loadUrl(url);
            return true;
        }
        return false;
    }
}
