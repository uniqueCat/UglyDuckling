package msf.uglyduckling.ui.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import msf.uglyduckling.R;
import msf.uglyduckling.base.BaseActivity;
import msf.uglyduckling.ui.express.ExpressFragment;
import msf.uglyduckling.ui.historyToday.HistoryTodayFragment;
import msf.uglyduckling.ui.weather.WeatherFragment;

//fragment模块管理，toolBar关联NavigationView，NavigationView事件处理； toolbar menu设置及事件在fragment模块中处理
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView naView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private Fragment fragments[] = new Fragment[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();

        if (savedInstanceState != null) {
            lastIndex = savedInstanceState.getInt("lastIndex");
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            //activity重建时fragment会自己保存下来，给fragment设置个tag，冲创建时拿出来用；既不会重叠，又能保存fragment状态
            fragments[0] = supportFragmentManager.findFragmentByTag("fragment_0");
            fragments[1] = supportFragmentManager.findFragmentByTag("fragment_1");
            fragments[2] = supportFragmentManager.findFragmentByTag("fragment_2");
        }
        if (lastIndex == -1)
            checkFragmentIndex(0);
        else
            checkFragmentIndex(lastIndex);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {

    }

    private void initViews() {
        naView.setNavigationItemSelectedListener(this);
        //设置上去，title默认app_name
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //DrawerLayout关联上ToolBar    toolBar左上角出现个图标，点击打卡开抽屉
        drawerLayout.addDrawerListener(toggle);
        //同步生效
        toggle.syncState();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_history_today:
                checkFragmentIndex(0);
                break;
            case R.id.nav_express:
                checkFragmentIndex(1);
                break;
            case R.id.nav_weather:
                checkFragmentIndex(2);
                break;
            case R.id.nav_switch_theme:
                drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                    }

                    @Override
                    public void onDrawerOpened(@NonNull View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(@NonNull View drawerView) {
                        //日夜间切换，改变用values还是values-night的属性
                        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                                == Configuration.UI_MODE_NIGHT_NO) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        }
                        //日夜间切换过渡动画
                        getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
                        //activity重建，不然没效果
                        recreate();

                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {

                    }
                });
                break;
            case R.id.nav_settings:
                startActivity(new Intent(MainActivity.this, PrefsActivity.class).putExtra(PrefsActivity.EXTRA_FLAG, PrefsActivity.FLAG_SETTINGS));
                break;
            case R.id.nav_about:
                startActivity(new Intent(MainActivity.this, PrefsActivity.class).putExtra(PrefsActivity.EXTRA_FLAG, PrefsActivity.FLAG_ABOUT));
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private int lastIndex = -1;

    public void checkFragmentIndex(int index) {
        if (fragments[index] == null) {
            createFragment(index);
        }
        @SuppressLint("CommitTransaction") FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!fragments[index].isAdded()) {
            transaction.add(R.id.content_main, fragments[index], "fragment_" + index);
        }
        if (lastIndex != -1) {
            transaction.hide(fragments[lastIndex]);
        }
        lastIndex = index;
        transaction.show(fragments[index]);
        transaction.commit();
        switch (index) {
            case 0:
                naView.setCheckedItem(R.id.nav_history_today);
                toolbar.setTitle(R.string.historyToday);
                break;
            case 1:
                naView.setCheckedItem(R.id.nav_express);
                toolbar.setTitle(R.string.express);
                break;
            case 2:
                naView.setCheckedItem(R.id.nav_weather);
                toolbar.setTitle(R.string.weather);
                break;
        }
    }

    private void createFragment(int index) {
        switch (index) {
            case 0:
                fragments[0] = new HistoryTodayFragment();
                break;
            case 1:
                fragments[1] = new ExpressFragment();
                break;
            case 2:
                fragments[2] = new WeatherFragment();
                break;
        }
    }

    //recreate时会调用，保存状态及fragment
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("lastIndex", lastIndex);    //保存当前选中页
    }

}
