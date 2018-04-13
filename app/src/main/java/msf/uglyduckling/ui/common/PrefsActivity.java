package msf.uglyduckling.ui.common;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import msf.uglyduckling.R;
import msf.uglyduckling.base.BaseActivity;

/**
 * 装PreferencesFragment的activity；PreferencesFragment不止一种
 */

public class PrefsActivity extends BaseActivity {

    public static final String EXTRA_FLAG = "EXTRA_FLAG";

    public static final int FLAG_SETTINGS = 0, FLAG_ABOUT = 1, FLAG_LICENSES = 2;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_prefs;
    }

    @SuppressLint("CommitTransaction")
    @Override
    protected void init() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Fragment fragment;
        switch (getIntent().getIntExtra(EXTRA_FLAG, -1)) {
            case FLAG_SETTINGS:
                fragment = new SettingsFragment();
                break;
            case FLAG_ABOUT:
                fragment = new SettingsFragment();
                break;
            case FLAG_LICENSES:
                fragment = new SettingsFragment();
                break;
            default:
                throw new IllegalArgumentException("No or unknowns flag");
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.view_pager, fragment)
                .show(fragment)
                .commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }


}
