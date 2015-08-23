package com.jaouan.snapandmatch.components.services;

import android.app.Activity;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.concurrent.Semaphore;

/**
 * Service that checks spell.
 * The service is very tricky. It loads a Google Search page on a WebView, then it tries to extract spell suggest.
 *
 * @author Maxence Jaouan
 */
public class SpellCheckService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SpellCheckService.class);

    /**
     * SINGLETON.
     */
    private static final SpellCheckService SINGLETON = new SpellCheckService();

    public static SpellCheckService getInstance() {
        return SINGLETON;
    }

    /**
     * String holder for callback.
     */
    class StringHolder {
        public String value;
    }

    /**
     * Check text spell.
     *
     * @param activity Activity.
     * @param webView  Webview.
     * @param text     Text to check.
     * @return Correct spell.
     */
    public String checkSpell(final Activity activity, final WebView webView, final String text) {
        final StringHolder wellSpelledText = new StringHolder();
        wellSpelledText.value = text;

        // - Acquire a semaphore (to make asynchronous's callback synchronous)
        final Semaphore sem = new Semaphore(1);
        try {
            sem.acquire();
        } catch (InterruptedException interruptedException) {
            // Ignore.
        }

        activity.runOnUiThread(() -> {
            // - Initialize web view.
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
            webView.setWebViewClient(new WebViewClient());

            // - Initialize an 8 seconds time out.
            final Handler handler = new Handler();
            final Runnable timeoutRunnable = () -> {
                sem.release();
            };
            handler.postDelayed(timeoutRunnable, 8000);


            webView.setWebViewClient(new WebViewClient() {

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);

                    // - Release semaphore on error.
                    sem.release();
                }

                public void onPageFinished(WebView view, String url) {
                    activity.runOnUiThread(() ->
                            // - Evaluate JavaScript to extract Google's correct spell.
                            webView.evaluateJavascript("document.getElementsByClassName(\"spell\")[1].innerText", result -> {
                                // - If no result, then return original string.
                                if (result == null || result.trim().length() == 0) {
                                    result = wellSpelledText.value;
                                }

                                // - Clean result.
                                final String cleanedResult = result.replace("\"", "");

                                // - If cleaned result is empty, then return original string.
                                if (!cleanedResult.isEmpty()) {
                                    wellSpelledText.value = cleanedResult;
                                }

                                // - Release semaphore.
                                sem.release();
                            }));
                }
            });

            try {
                webView.loadUrl("https://www.google.fr/search?hl=en&q=" + URLEncoder.encode(text, Charset.defaultCharset().name()));
            } catch (final UnsupportedEncodingException unsupportedEncodingException) {
                LOGGER.error("Error while loading url.", unsupportedEncodingException);
                // - Release semaphore on error.
                sem.release();
            }
        });

        try {
            sem.acquire();
        } catch (InterruptedException interruptedException) {
            // Ignore.
        }

        // - Return well spelled text.
        return wellSpelledText.value;
    }


}
