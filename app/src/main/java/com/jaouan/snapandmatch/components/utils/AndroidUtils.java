package com.jaouan.snapandmatch.components.utils;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.annotation.ColorRes;
import android.support.design.widget.Snackbar;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;

import com.jaouan.snapandmatch.R;
import com.jaouan.snapandmatch.components.views.ListenableScrollView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Android utils.
 *
 * @author Maxence Jaouan
 */
public final class AndroidUtils {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AndroidUtils.class);

    /**
     * Private constructor.
     */
    private AndroidUtils() {
    }

    /**
     * Copy asset file to external file.
     *
     * @param context          Context.
     * @param assetFileName    Asset file name.
     * @param externalFileName External file name.
     * @throws IOException
     */
    public static void copyAssetToFile(Context context, String assetFileName, String externalFileName) throws IOException {
        final AssetManager assetManager = context.getAssets();

        InputStream in = assetManager.open(assetFileName);
        OutputStream out = new FileOutputStream(externalFileName);

        final byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }

        in.close();
        out.flush();
        out.close();
    }


    /**
     * Get surface texture from TextureView.
     *
     * @param textureView Texture view.
     * @param callback    Surface texture callback.
     */
    public static void getSurfaceTexture(TextureView textureView, Procedure1<SurfaceTexture> callback) {
        if (textureView.getSurfaceTexture() == null) {
            textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                    callback.proceed(surfaceTexture);
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                }
            });
        } else {
            callback.proceed(textureView.getSurfaceTexture());
        }
    }

    /**
     * Display a snack to enable data connection.
     *
     * @param activity Activity.
     * @param view     Root view.
     */
    public static void snackEnableData(final Activity activity, final View view) {
        Snackbar.make(view, R.string.snack_nointernet, Snackbar.LENGTH_LONG).setAction(R.string.snack_enableinternet, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        }).show();
    }

    /**
     * Check data state.
     *
     * @param context Context.
     * @return TRUE if data enabled.
     */
    public static boolean isDataEnabled(final Context context) {
        boolean isDataEnabled = false;

        try {
            final ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity.getActiveNetworkInfo() != null) {
                if (connectivity.getActiveNetworkInfo().isConnected()) {
                    isDataEnabled = true;
                }
            }
        } catch (final Throwable cause) {
            // - On error, assume that data is enabled.
            isDataEnabled = true;
        }

        return isDataEnabled;
    }

    /**
     * Handle color fading on scroll changed.
     *
     * @param rootView              Root view.
     * @param listenableScrollView  ScrollView.
     * @param topColorResourceId    Top color.
     * @param bottomColorResourceId Bottom color.
     */
    public static void handleScrollViewColorFading(final View rootView, final ListenableScrollView listenableScrollView, @ColorRes final int topColorResourceId, @ColorRes final int bottomColorResourceId) {
        final View contentView = listenableScrollView.getChildAt(0);
        contentView
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        contentView.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);

                        // - Get content height.
                        int height = contentView.getMeasuredHeight();

                        // - Create an color animation.
                        final Resources resources = rootView.getContext().getResources();
                        final ObjectAnimator colorFade = ObjectAnimator.ofObject(rootView, "backgroundColor", new ArgbEvaluator(), resources.getColor(topColorResourceId), resources.getColor(bottomColorResourceId));
                        colorFade.setDuration(height);

                        // - Update animation play time on scroll changed.
                        listenableScrollView.setOnScrollListener((newPosition) -> {
                            colorFade.setCurrentPlayTime(newPosition);
                        });
                    }
                });
    }

    /**
     * Get application's version name.
     *
     * @param context Context.
     * @return Version name.
     */
    public static String getVersionName(final Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (final PackageManager.NameNotFoundException exception) {
            LOGGER.warn("Unable to retrieve version name.", exception);
            return null;
        }
    }
}
