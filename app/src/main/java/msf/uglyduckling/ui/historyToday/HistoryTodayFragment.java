package msf.uglyduckling.ui.historyToday;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import msf.uglyduckling.R;
import msf.uglyduckling.base.BaseFragment;
import msf.uglyduckling.bean.HistoryTodayBean;
import msf.uglyduckling.config.Const;
import msf.uglyduckling.net.BeanCallback;
import msf.uglyduckling.net.model.HistoryTodayModel;
import msf.uglyduckling.utils.CustomTabsHelper;
import msf.uglyduckling.utils.PermissionsChecker;

/**
 * Created by Administrator on 2018/4/12.
 */

public class HistoryTodayFragment extends BaseFragment {

    @BindView(R.id.rv)
    RecyclerView rv;

    private List<HistoryTodayBean.ListBean> list;

    private int currentMonth = 0, currentDay = 1;

    private String saveImgUrl;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_history_today;
    }

    @Override
    protected void viewCreate() {
        setHasOptionsMenu(true);
        refreshData(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_someday, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search_someday) {
            if (getContext() == null)
                return true;
            DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Panel, new DatePickerDialog.OnDateSetListener() {
                @Override   //月是从零开始，选择一月month是零
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    String sMonth = (month + 1) + "";
                    String sDay = dayOfMonth + "";
                    if (sMonth.length() == 1)
                        sMonth = "0" + sMonth;
                    if (sDay.length() == 1)
                        sDay = "0" + sDay;
                    refreshData(sMonth + sDay);
                }
            }, 2016, currentMonth, currentDay);
            dialog.setMessage("\n选择查看历史上的某一天!");
            dialog.show();
            //得到dialog的日期控件
            DatePicker datePicker = dialog.getDatePicker();
            try {
                //限制只在16年选择日期，16年是闰年
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");
                datePicker.setMinDate(format.parse("2016-1-1").getTime());
                datePicker.setMaxDate(format.parse("2016-12-31").getTime());
            } catch (ParseException e) {
            }
            //隐藏掉年
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(0).setVisibility(View.GONE);
        }
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    private void refreshData(String date) {
        HistoryTodayModel.getHistoryToday(date, new BeanCallback<HistoryTodayBean>() {
            @Override
            public void onSuccess(HistoryTodayBean data) {
                if (data.getRet_code() != 0) {
                    showSnack(getView(), "获取数据失败");
                    return;
                }
                list = data.getList();
                currentMonth = list.get(0).getMonth() - 1;
                currentDay = list.get(0).getDay();
                //需要item高度自适应，导入方式要正确，如果item的宽高以item中的image决定，imageView要在代码里设置setAdjustViewBounds 固定宽高比，否自会自动缩放； 百度说要自适应需要设置manager.setAutoMeasureEnabled(true);但好像不需要？
                LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                rv.setLayoutManager(manager);   //item动画，也可以在xml中设置到RecyclerView上，anim要跟manager对应
                rv.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.rv_load_anim));
                rv.setAdapter(new MyAdapter());
            }

            @Override
            public void onFail(String msg) {
                showSnack(getView(), msg);
            }
        });
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> implements View.OnClickListener, View.OnLongClickListener {


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //以这种方式导入布局   RecyclerView才能拿到item的layoutParams参数，以自适应每个item的宽高(否则根据item根布局类型要么是全屏，要么是包裹内容)！ 第三个参数代表添加入parent，始终为false，列表中item跟parent不是绑定的，是动态的
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_today, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(inflate);
            //Image固定宽高比，否则item不能宽度占满，高度自适应
            myViewHolder.ivSrc.setAdjustViewBounds(true);
            return myViewHolder;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            HistoryTodayBean.ListBean listBean = list.get(position);
            holder.tvTitle.setText(listBean.getTitle());
            holder.tvYear.setText(listBean.getYear() + "年" + listBean.getMonth() + "月" + listBean.getDay() + "日");
            if (getContext() == null)
                return;
            holder.tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.mdtp_white));
            holder.tvYear.setTextColor(ContextCompat.getColor(getContext(), R.color.mdtp_white));
            Glide.with(getContext()).load(listBean.getImg()).apply(RequestOptions.errorOf(R.drawable.photo_frame)).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.mdtp_transparent_black));
                    holder.tvYear.setTextColor(ContextCompat.getColor(getContext(), R.color.mdtp_transparent_black));
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(holder.ivSrc);
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(this);
            holder.itemView.setOnLongClickListener(this);
        }


        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override   //使用百度搜索此事件
        public void onClick(View v) {
            CustomTabsHelper.openUrl(getContext(), "https://www.baidu.com/s?wd=" + list.get((Integer) v.getTag()).getTitle());
        }

        @Override   //保存图片
        public boolean onLongClick(View v) {
            saveImgUrl = list.get((Integer) v.getTag()).getImg();
            if (saveImgUrl == null) {
                showSnack(getView(), "暂无图片");
            } else {
                if (PermissionsChecker.lacksPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    saveBitmap();
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Const.REQUEST_PERMISSION_CODE);
                }
            }
            return true;
        }
    }

    private void saveBitmap() {
        if (getContext() == null)
            return;
        Glide.with(getContext()).asBitmap().load(saveImgUrl).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                FileOutputStream fos = null;
                try {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + getString(R.string.app_name) + "/img/" + System.currentTimeMillis() + ".jpg");
                    if (!file.getParentFile().exists())
                        file.getParentFile().mkdirs();
                    fos = new FileOutputStream(file);
                    boolean compress = resource.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    if (compress) {
                        getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                        showSnack(getView(), "保存成功");
                    }
                } catch (FileNotFoundException e) {

                } finally {
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Const.REQUEST_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveBitmap();
            } else {
                showSnack(getView(), "没有写入sd卡权限，保存失败");
            }
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.iv_src)
        ImageView ivSrc;
        @BindView(R.id.tv_year)
        TextView tvYear;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
