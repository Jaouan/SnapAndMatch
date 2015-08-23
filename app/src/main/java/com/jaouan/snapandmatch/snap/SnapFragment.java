package com.jaouan.snapandmatch.snap;


import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.design.widget.FloatingActionButton;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.edmodo.cropper.CropImageView;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.jaouan.android.kerandroid.KerSupportFragment;
import com.jaouan.android.kerandroid.annotation.field.viewbyid.FindViewById;
import com.jaouan.android.kerandroid.annotation.method.click.Click;
import com.jaouan.android.kerandroid.annotation.type.Layout;
import com.jaouan.snapandmatch.R;
import com.jaouan.snapandmatch.SnapAndMatchActivity;
import com.jaouan.snapandmatch.components.models.matches.Match;
import com.jaouan.snapandmatch.components.tasks.AnalyzeAndSearchAsyncTask;
import com.jaouan.snapandmatch.components.utils.AndroidUtils;
import com.jaouan.snapandmatch.results.ErrorFragment;
import com.jaouan.snapandmatch.results.MatchesFragment;

import net.bozho.easycamera.DefaultEasyCamera;
import net.bozho.easycamera.EasyCamera;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Snap fragment that allows user to take a picture from the camera, read text on it, then search matches.
 *
 * @author Maxence Jaouan
 */
@Layout(R.layout.fragment_camera)
public class SnapFragment extends KerSupportFragment {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SnapFragment.class);

    /**
     * Create a new instance of {@link SnapFragment}.
     *
     * @return Instance of {@link SnapFragment}.
     */
    public static SnapFragment newInstance() {
        return new SnapFragment();
    }

    /**
     * Camera API.
     */
    private EasyCamera mEasyCamera;

    @FindViewById(R.id.cropImageView)
    private CropImageView mCropper;

    @FindViewById(R.id.croppedImageView)
    private ImageView mCroppedImageView;

    @FindViewById(R.id.snapButton)
    private FloatingActionButton mSnapButton;

    @FindViewById(R.id.webView)
    private WebView webView;

    @FindViewById(R.id.snapCircle)
    private FABProgressCircle mFabProgressCircle;

    @FindViewById(R.id.surface)
    private TextureView mTextureView;

    @FindViewById(R.id.flashView)
    private View mFlashView;

    /**
     * Analyze and search async task call back.
     */
    private final AnalyzeAndSearchAsyncTask.CallBack analyzeAndSearchAsyncTaskCallBack = new AnalyzeAndSearchAsyncTask.CallBack() {
        @Override
        public void onCropping() {
            // - Update snap icon,  display progress circle and stop preview.
            mSnapButton.setImageResource(R.drawable.ic_visibility_white_24dp);
            mFabProgressCircle.show();
            mEasyCamera.stopPreview();
        }

        @Override
        public void onReading(final Bitmap croppedBitmap) {
            // - Flash/Overlay preview and highlight cropped bitmap.
            overlayPreview();
            highlightCroppedBitmap(croppedBitmap);
        }

        @Override
        public void onChecking() {
            // - Update snap icon.
            mSnapButton.setImageResource(R.drawable.ic_spellcheck_white_24dp);
        }

        @Override
        public void onSearching() {
            // - Update snap icon.
            mSnapButton.setImageResource(R.drawable.ic_track_changes_white_24dp);
        }

        @Override
        public void onSuccess(final Set<Match> matches) {
            // - If activity is still alive (asynchronous task).
            if (getActivity() != null) {
                // - Attach success listener to progress circle's final animation, then start it.
                getActivity().runOnUiThread(() -> {
                    mFabProgressCircle.attachListener(() -> {
                        ((SnapAndMatchActivity) getActivity()).pushFragment(MatchesFragment.newInstance(matches));
                    });
                    mFabProgressCircle.beginFinalAnimation();
                });
            }
        }

        @Override
        public void onError() {
            // - If activity is still alive (asynchronous task).
            if (getActivity() != null) {
                // - Attach error listener to progress circle's final animation, then start it.
                getActivity().runOnUiThread(() -> {
                    mFabProgressCircle.attachListener(() -> {
                        ((SnapAndMatchActivity) getActivity()).pushFragment(ErrorFragment.newInstance());
                    });
                    mFabProgressCircle.beginFinalAnimation();
                });
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(null);
    }

    @Click(R.id.snapButton)
    protected void onSnapButtonClicked(final View view) {
        // - Analyze bitmap if internet is enabled.
        if (AndroidUtils.isDataEnabled(getActivity())) {
            mSnapButton.setEnabled(false);
            new AnalyzeAndSearchAsyncTask(getActivity(), mCropper.getActualCropRect(), webView, analyzeAndSearchAsyncTaskCallBack).execute(mTextureView.getBitmap());
        } else {
            AndroidUtils.snackEnableData(getActivity(), getView());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // - Display camera preview.
        displayCameraPreview();

        // - Snack to enable internet if not.
        if (!AndroidUtils.isDataEnabled(getActivity())) {
            AndroidUtils.snackEnableData(getActivity(), getView());
        }
    }

    @Override
    public void onPause() {
        // - Uninitialize camera on pause.
        uninitializeCamera();

        super.onPause();
    }

    /**
     * Display camera error.
     *
     * @param cause CFause.
     */
    private void displayCameraError(final Throwable cause) {
        LOGGER.error("Error while initializing camera.", cause);
        Toast.makeText(getActivity(), "Erreur lors de la communication avec l'appareil photo.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Flash camera preview, then display a white overlay.
     */
    private void overlayPreview() {
        final Animation animation = new AlphaAnimation(1.0f, 0.8f);
        animation.setDuration(750);
        animation.setInterpolator(getActivity(), android.R.interpolator.decelerate_cubic);
        animation.setFillAfter(true);
        animation.setFillEnabled(true);
        mFlashView.setVisibility(View.VISIBLE);
        mFlashView.startAnimation(animation);
    }

    /**
     * Highlight cropped bitmap above overlay.
     *
     * @param croppedBitmap Cropped bitmap.
     */
    private void highlightCroppedBitmap(Bitmap croppedBitmap) {
        // - Hide cropper.
        mCropper.setVisibility(View.GONE);

        // - Update cropped image view's location.
        final RectF rect = mCropper.getActualCropRect();
        mCroppedImageView.setPadding((int) rect.left, (int) rect.top, 0, 0);
        ViewGroup.LayoutParams layoutParams = mCroppedImageView.getLayoutParams();
        layoutParams.width = (int) (croppedBitmap.getWidth() + rect.left);
        layoutParams.height = (int) (croppedBitmap.getHeight() + rect.top);
        mCroppedImageView.setLayoutParams(layoutParams);

        // - Display cropped bitmap.
        mCroppedImageView.setImageBitmap(croppedBitmap);
    }

    /**
     * Initialize and display camera preview with good ratio.
     */
    private void displayCameraPreview() {
        try {
            // - Initialize camera.
            initializeCamera();

            // - Initialize preview when the texture view is available
            AndroidUtils.getSurfaceTexture(mTextureView, (surfaceTexture) -> {
                initializePreview(surfaceTexture);
            });
        } catch (final Throwable cause) {
            // - An exception may be thrown when camera is unavailable.
            displayCameraError(cause);
        }
    }

    /**
     * Initialize camera with all good paramters.
     */
    private void initializeCamera() {
        // - Open camera.
        mEasyCamera = DefaultEasyCamera.open();

        // - Make sure preview is not started.
        mEasyCamera.stopPreview();

        // - Set good parameters.
        Camera.Parameters params = mEasyCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mEasyCamera.setParameters(params);
        mEasyCamera.setDisplayOrientation(90);
    }

    /**
     * Initialize preview with good ratio.
     *
     * @param surfaceTexture
     */
    private void initializePreview(final SurfaceTexture surfaceTexture) {
        try {
            // - Retrieve camera ratio then fit preview and cropper.
            final float ratio = getCameraPreviewRatio();
            updatePreviewRatio(ratio);
            fitCropImageView(ratio);

            // - If snap button is still enable, start preview.
            if (mSnapButton.isEnabled()) {
                mEasyCamera.startPreview(surfaceTexture);
            }
        } catch (final Throwable cause) {
            // - An exception may be thrown when camera is unavailable.
            displayCameraError(cause);
        }
    }

    /**
     * Fit croper to preview size.
     *
     * @param ratio Preview's ratio.
     */
    private void fitCropImageView(float ratio) {
        final Bitmap bitmap = Bitmap.createBitmap(mTextureView.getMeasuredWidth(), (int) (mTextureView.getMeasuredWidth() * ratio), Bitmap.Config.ARGB_8888);
        mCropper.setImageBitmap(bitmap);
    }

    /**
     * Update preview's height with width's ratio.
     *
     * @param ratio Preview's ratio.
     */
    private void updatePreviewRatio(float ratio) {
        final ViewGroup.LayoutParams layoutParams = mTextureView.getLayoutParams();
        layoutParams.height = (int) Math.ceil(mTextureView.getMeasuredWidth() * ratio);
        mTextureView.setLayoutParams(layoutParams);
    }

    /**
     * Get camera preview's ratio.
     *
     * @return Preview's ratio.
     */
    private float getCameraPreviewRatio() {
        final Camera.Size size = mEasyCamera.getParameters().getPreviewSize();
        return (float) size.width / size.height;
    }

    /**
     * Uninitialize camera.
     */
    private void uninitializeCamera() {
        // - If camera is initialized, uninitialied it.
        if (mEasyCamera != null) {
            mEasyCamera.stopPreview();
            mEasyCamera.close();
        }
    }
}
