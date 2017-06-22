package com.jiminycricket.expensesmanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;

public class MainActivity extends Activity {

    private WebView browser;

    /**
     * onCreate method of the Main Activity
     * creates the webview object and navigating to the application's website
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        browser = new WebView(this);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setWebViewClient(new ExpensesWebViewClient());
        setContentView(browser);
        browser.loadUrl("http://104.199.142.109/ExpensesMangerServer/");
    }

    /**
     * pause the execution of the Java Script code in the WebView
     */
    @Override
    public void onPause()
    {
        super.onPause();
        browser.getSettings().setJavaScriptEnabled(false);
    }

    /**
     * resume the execution of the Java Script code in the WebView
     */
    @Override
    public void onResume()
    {
        super.onResume();
        browser.getSettings().setJavaScriptEnabled(true);
    }

    /**
     * handling android's back button press
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if ((keyCode == KeyEvent.KEYCODE_BACK) && browser.canGoBack()){
            browser.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
