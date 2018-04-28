package msf.uglyduckling.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/4/13.
 */

public abstract class BaseFragment extends Fragment {

    private Unbinder bind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(getLayoutId(), container, false);
        bind = ButterKnife.bind(this, inflate);
        viewCreate();
        return inflate;
    }

    public abstract int getLayoutId();

    protected abstract void viewCreate();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();
    }

    public void jumpActivity(Class<? extends AppCompatActivity> c){
        startActivity(new Intent(getContext(),c));
    }

    public void showSnack(View view, String msg) {
        try {
            Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
        }
    }
}
