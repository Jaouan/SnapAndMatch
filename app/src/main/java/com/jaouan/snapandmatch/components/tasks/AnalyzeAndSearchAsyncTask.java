package com.jaouan.snapandmatch.components.tasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.webkit.WebView;

import com.jaouan.snapandmatch.components.models.matches.Match;
import com.jaouan.snapandmatch.components.services.BitmapToTextService;
import com.jaouan.snapandmatch.components.services.MatchService;
import com.jaouan.snapandmatch.components.services.SpellCheckService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Task that analyses bitmap and searches for matches.
 *
 * @author Maxence Jaouan
 */
public class AnalyzeAndSearchAsyncTask extends AsyncTask<Bitmap, AnalyzeAndSearchAsyncTask.ProgressState, Set<Match>> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzeAndSearchAsyncTask.class);

    /**
     * Crop rect.
     */
    private final RectF mCropRect;

    /**
     * Web view (for SpellCheckService).
     */
    private final WebView mWebView;

    /**
     * Task's Callback.
     */
    private final CallBack mCallBack;

    /**
     * Cropped bitmap.
     */
    private Bitmap mCroppedBitmap;

    /**
     * Activity (cannot use Context because of SpellCheckService).
     */
    private final Activity mActivity;

    /**
     * Success flash.
     */
    private boolean mSuccess;

    /**
     * Constructor.
     *
     * @param activity Activity.
     * @param cropRect Crop rect.
     * @param webView  Web view.
     * @param callBack Task's Callback.
     */
    public AnalyzeAndSearchAsyncTask(final Activity activity, final RectF cropRect, final WebView webView, final CallBack callBack) {
        this.mActivity = activity;
        this.mCropRect = cropRect;
        this.mWebView = webView;
        this.mCallBack = callBack;
    }

    /**
     * Task's callback interface.
     */
    public interface CallBack {
        /**
         * On cropping bitmap (task beginning).
         */
        void onCropping();

        /**
         * On reading bitmap.
         *
         * @param croppedBitmap Croped bitmap.
         */
        void onReading(Bitmap croppedBitmap);

        /**
         * On checking spell.
         */
        void onChecking();

        /**
         * On searching matches.
         */
        void onSearching();

        /**
         * On success.
         *
         * @param matches Matches.
         */
        void onSuccess(Set<Match> matches);

        /**
         * On error.
         */
        void onError();
    }

    /**
     * Progress state.
     */
    public enum ProgressState {
        CROPPING,
        READING,
        CHECKING,
        SEARCHING,
        ERROR
    }

    @Override
    protected void onProgressUpdate(ProgressState... values) {
        super.onProgressUpdate(values);

        switch (values[0]) {
            case CROPPING:
                mCallBack.onCropping();
                break;

            case READING:
                mCallBack.onReading(mCroppedBitmap);
                break;

            case CHECKING:
                mCallBack.onChecking();
                break;

            case SEARCHING:
                mCallBack.onSearching();
                break;

            case ERROR:
                mSuccess = false;
                break;
        }
    }

    @Override
    protected void onPostExecute(Set<Match> matches) {
        super.onPostExecute(matches);
        if (mSuccess) {
            mCallBack.onSuccess(matches);
        } else {
            mCallBack.onError();
        }
    }

    @Override
    protected Set<Match> doInBackground(Bitmap... snappedBitmaps) {
        final Set<Match> matches = new HashSet<Match>();

        // - For each input bitmap.
        try {
            for (final Bitmap snappedBitmap : snappedBitmaps) {
                // - Set success flag to true, since analyzing hasn't crash yet !
                mSuccess = true;

                // - Crop bitmap.
                publishProgress(ProgressState.CROPPING);
                mCroppedBitmap = Bitmap.createBitmap(snappedBitmap,
                        (int) mCropRect.left,
                        (int) mCropRect.top,
                        (int) mCropRect.width(),
                        (int) mCropRect.height());
                snappedBitmap.recycle();

                // - Read cropped bitmap.
                publishProgress(ProgressState.READING);
                final String resultString = BitmapToTextService.getInstance().readBitmap(mActivity, mCroppedBitmap);

                // - Correct spell.
                publishProgress(ProgressState.CHECKING);
                final SpellCheckService spellCheckService = SpellCheckService.getInstance();
                String[] splittedNeedle = resultString.split("\n");
                for (int i = 0; i < splittedNeedle.length; i++) {
                    splittedNeedle[i] = spellCheckService.checkSpell(mActivity, mWebView, splittedNeedle[i]);
                }

                // - Search.
                publishProgress(ProgressState.SEARCHING);
                matches.addAll(MatchService.getInstance().search(mActivity, splittedNeedle));

            }
        } catch (final Throwable cause) {
            LOGGER.error("Error during AnalyzeAndSearch task.", cause);
            publishProgress(ProgressState.ERROR);
        }

        return matches;
    }

}