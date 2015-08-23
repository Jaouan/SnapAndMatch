package com.jaouan.snapandmatch;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.jaouan.android.kerandroid.KerActionBarActivity;
import com.jaouan.android.kerandroid.annotation.type.Layout;
import com.jaouan.snapandmatch.help.HelpDrawerFragment;
import com.jaouan.snapandmatch.snap.SnapFragment;

/**
 * Activity that allows user to take a picture, then search matches on web.
 *
 * @author Maxence Jaouan
 */
@Layout(R.layout.activity_snapandmatch)
public class SnapAndMatchActivity extends KerActionBarActivity {

    /**
     * Help drawer.
     */
    private HelpDrawerFragment mHelpDrawerFragment;

    /**
     * Action bar's background.
     */
    private ColorDrawable mColorDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // - Initialize activity.
        initializeActionBar();
        initializeNavigationDrawer();

        // - Handle fragment poping.
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(() -> {
            if (fragmentManager.getBackStackEntryCount() == 0) {
                finish();
            }
        });

        // - Initialize entry fragment if necessary.
        if (savedInstanceState == null) {
            pushFragment(SnapFragment.newInstance());
        }
    }

    /**
     * Push fragment.
     *
     * @param fragment Fragment.
     */
    public void pushFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Initialize navigation drawer.
     */
    private void initializeNavigationDrawer() {
        mHelpDrawerFragment = (HelpDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mHelpDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    /**
     * Initialize action bar.
     */
    private void initializeActionBar() {
        // - Set alpha-able actionbar.
        mColorDrawable = new ColorDrawable(getResources().getColor(R.color.actionbar_color));
        getSupportActionBar().setBackgroundDrawable(mColorDrawable);

        // - Remove title by default.
        setTitle(null);
    }

    @Override
    public void onBackPressed() {
        // - Close drawer before poping fragment.
        if(mHelpDrawerFragment.isDrawerOpen()){
            mHelpDrawerFragment.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void setTitle(final CharSequence title) {
        super.setTitle(title);

        // - Hide action bar if no title.
        if (title == null || title.length() == 0) {
            mColorDrawable.setAlpha(0);
        } else {
            mColorDrawable.setAlpha(255);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mHelpDrawerFragment.getDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
