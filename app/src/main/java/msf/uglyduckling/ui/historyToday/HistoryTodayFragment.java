package msf.uglyduckling.ui.historyToday;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import msf.uglyduckling.R;
import msf.uglyduckling.base.BaseFragment;

/**
 * Created by Administrator on 2018/4/12.
 */

public class HistoryTodayFragment extends BaseFragment {

    @BindView(R.id.rv)
    RecyclerView rv;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_history_today;
    }

    @Override
    protected void viewCreate() {
        setHasOptionsMenu(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        rv.setAdapter(new MyAdapter());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.packages_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            Toast.makeText(getContext(), "搜索", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.action_mark_all_read) {
            Toast.makeText(getContext(), "标记全部", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new MyViewHolder(inflate);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.tv.setText("item" + position);
        }

        @Override
        public int getItemCount() {
            return 68;
        }
    }


    private class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

         MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView;
        }
    }
}
