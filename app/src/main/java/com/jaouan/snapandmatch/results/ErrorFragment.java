package com.jaouan.snapandmatch.results;


import android.support.v4.app.Fragment;

import com.jaouan.android.kerandroid.KerSupportFragment;
import com.jaouan.android.kerandroid.annotation.type.Layout;
import com.jaouan.snapandmatch.R;


/**
 * Camera fragment that displays nothing but an error.
 *
 * @author Maxence Jaouan
 */
@Layout(R.layout.fragment_error)
public class ErrorFragment extends KerSupportFragment {

    /**
     * Create a new instance of {@link ErrorFragment}.
     *
     * @return Instance of {@link ErrorFragment}.
     */
    public static Fragment newInstance() {
        return new ErrorFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.error_title));
    }

}
