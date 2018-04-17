package msf.uglyduckling.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/13.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        init();
    }

    protected abstract int getLayoutId();

    protected abstract void init();

    public void showSnack(View view, String msg) {
        try {
            Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
        }
    }

}
