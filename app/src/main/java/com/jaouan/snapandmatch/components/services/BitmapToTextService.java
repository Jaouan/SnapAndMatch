package com.jaouan.snapandmatch.components.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.jaouan.snapandmatch.components.models.exceptions.ServiceTechnicalException;
import com.jaouan.snapandmatch.components.utils.AndroidUtils;

import java.io.File;
import java.io.IOException;

/**
 * Service that converts bitmap to text.
 *
 * @author Maxence Jaouan
 */
public class BitmapToTextService {

    /**
     * Language - French support a lot a caracters.
     */
    public static final String TRAINED_LANGUAGE = "fra";

    /**
     * Trained file's directory.
     */
    private static final String TRAINED_FILE_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tessdata";

    /**
     * Trained file's name.
     */
    private static final String TRAINED_FILENAME = TRAINED_LANGUAGE + ".traineddata";

    /**
     * Trained file's full path.
     */
    private static final String TRAINED_FILE_FULL_PATH = TRAINED_FILE_DIRECTORY + TRAINED_FILENAME;


    /**
     * SINGLETON.
     */
    private static BitmapToTextService SINGLETON = new BitmapToTextService();

    public static BitmapToTextService getInstance() {
        return SINGLETON;
    }

    /**
     * Read bitmap.
     *
     * @param context Context.
     * @param bitmap  Bitmap.
     * @return Text from bitmap.
     * @throws ServiceTechnicalException Thrown when a technical exception occured.
     */
    public String readBitmap(final Context context, final Bitmap bitmap) throws ServiceTechnicalException {
        // - Copy trained file to external storage if necessary.
        final File file = new File(TRAINED_FILE_FULL_PATH);
        if (!file.exists()) {
            new File(TRAINED_FILE_DIRECTORY).mkdirs();
            try {
                AndroidUtils.copyAssetToFile(context, TRAINED_FILENAME, TRAINED_FILE_FULL_PATH);
            } catch (IOException cause) {
                throw new ServiceTechnicalException("Unable to copy trained file.", cause);
            }
        }

        String textFromBitmap;
        try {
            // - Initialize Tess.
            final TessBaseAPI tessBaseApi = new TessBaseAPI();
            // tessBaseApi.setDebug(true);
            tessBaseApi.init(Environment.getExternalStorageDirectory().getAbsolutePath() + "/", TRAINED_LANGUAGE);

            // - Read bitmap.
            tessBaseApi.setImage(bitmap);
            textFromBitmap = tessBaseApi.getUTF8Text();
        } catch (final Throwable cause) {
            throw new ServiceTechnicalException("Unable to read bitmap.", cause);
        }

        // - Clean result (remove unwanted dots).
        String cleanedTextFromBitmap = null;
        if (textFromBitmap != null) {
            cleanedTextFromBitmap = textFromBitmap.replace(".", " ");
        }

        return cleanedTextFromBitmap;
    }


}
