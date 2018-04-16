package msf.uglyduckling.ui.express;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.BindView;
import msf.uglyduckling.R;
import msf.uglyduckling.base.BaseFragment;

/**
 * Created by Administrator on 2018/4/13.
 */

public class ExpressFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.emptyView)
    LinearLayout emptyView;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.bottomNavigationView)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.fragment_packages)
    RelativeLayout fragmentPackages;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_express;
    }

    @Override
    protected void viewCreate() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.nav_all:

                        break;

                    case R.id.nav_on_the_way:

                        break;

                    case R.id.nav_delivered:

                        break;

                }

                return true;
            }
        });
    }
}
