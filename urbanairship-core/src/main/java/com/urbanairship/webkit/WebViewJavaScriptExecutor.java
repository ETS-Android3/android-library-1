/* Copyright Airship and Contributors */

package com.urbanairship.webkit;

import android.os.Build;
import android.webkit.WebView;

import com.urbanairship.javascript.JavaScriptExecutor;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

/**
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class WebViewJavaScriptExecutor implements JavaScriptExecutor {

    private final WeakReference<WebView> weakReference;

    public WebViewJavaScriptExecutor(@NonNull WebView webView) {
        this.weakReference = new WeakReference<>(webView);
    }

    @Override
    public void executeJavaScript(@NonNull String javaScript) {
        WebView webView = weakReference.get();
        if (webView != null) {
            webView.evaluateJavascript(javaScript, null);
        }
    }
}
