package com.jaouan.snapandmatch.help;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.jaouan.android.kerandroid.KerSupportFragment;
import com.jaouan.android.kerandroid.annotation.field.viewbyid.FindViewById;
import com.jaouan.android.kerandroid.annotation.type.Layout;
import com.jaouan.snapandmatch.R;
import com.jaouan.snapandmatch.components.utils.AndroidUtils;
import com.jaouan.snapandmatch.components.views.ListenableScrollView;

/**
 * Help drawer fragment.
 *
 * @author Maxence Jaouan
 */
@Layout(R.layout.fragment_help_drawer)
public class HelpDrawerFragment extends KerSupportFragment {

    /**
     * Preferences key - ALready launched.
     */
    private static final String PREFERENCES_KEY_ALREADY_LAUNCHED = "alreadyLaunched";

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    /**
     * Drawer layout.
     */
    private DrawerLayout mDrawerLayout;

    /**
     * Fragment container view.
     */
    private View mFragmentContainerView;

    @FindViewById(R.id.scrollView)
    private ListenableScrollView mScrollView;

    @FindViewById(R.id.versionTextView)
    private TextView mVersionTextView;

    @Override
    public void onStart() {
        super.onStart();

        // - Handle color fading on scroll changed.
        AndroidUtils.handleScrollViewColorFading(getView(), mScrollView, R.color.help_color_top, R.color.help_color_bottom);

        // - Display version name.
        mVersionTextView.setText(getString(R.string.version, AndroidUtils.getVersionName(getActivity())));

        // - If first launch, open drawer.
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (!defaultSharedPreferences.getBoolean(PREFERENCES_KEY_ALREADY_LAUNCHED, false)) {
            defaultSharedPreferences.edit().putBoolean(PREFERENCES_KEY_ALREADY_LAUNCHED, true).commit();
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }

    /**
     * Close drawer.
     */
    public void closeDrawer() {
        mDrawerLayout.closeDrawers();
    }

    /**
     * Get drawer state.
     *
     * @return TRUE if drawer is opened.
     */
    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        );

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

}
