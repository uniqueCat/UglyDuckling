package msf.uglyduckling.ui.express;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.OnClick;
import msf.uglyduckling.R;
import msf.uglyduckling.base.BaseActivity;
import msf.uglyduckling.bean.PackageBean;
import msf.uglyduckling.net.BeanCallback;
import msf.uglyduckling.net.model.ExpressModel;
import msf.uglyduckling.realm.RealmPackageHelper;
import msf.uglyduckling.utils.LogUtils;
import msf.uglyduckling.utils.NetworkUtil;
import msf.uglyduckling.widget.NotifyDialog;

/**
 * Created by Administrator on 2018/4/25.
 */

public class AddPackageActivity extends BaseActivity {
    @BindView(R.id.editTextNumber)
    TextInputEditText editTextNumber;
    @BindView(R.id.editTextName)
    TextInputEditText editTextName;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    public final static int SCANNING_REQUEST_CODE = 1;
    public final static int REQUEST_CAMERA_PERMISSION_CODE = 0;

    private int[] colorRes;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_package;
    }

    @Override
    protected void init() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        colorRes = new int[]{
                R.color.cyan_500, R.color.amber_500,
                R.color.pink_500, R.color.orange_500,
                R.color.light_blue_500, R.color.lime_500,
                R.color.green_500};
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick({R.id.textViewScanCode, R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.textViewScanCode:
                checkPermissionOrToScan();
                break;
            case R.id.fab:
                hideImm();
                checkInput();
                break;
        }
    }

    private void checkInput() {
        String name = editTextName.getText().toString();    //去除所有 空白字符 包括空格、制表符、回车等
        String number = editTextNumber.getText().toString().replaceAll("\\s*", "");
        if (number.isEmpty()) {
            showNumberError();
            return;
        }
        for (char c : number.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {    //判断字符是否是字母或数字
                showNumberError();
                return;
            }
        }
        if (RealmPackageHelper.getInstance().query(number) != null) { //从数据库中通过此单号查询到包裹，此单号已存在！
            showNumberExistError();
            return;
        }
        if (name.isEmpty())
            name = getString(R.string.package_name_default_pre) + number.substring(0, 4);
        querySavePackage(name, number);
    }

    private void querySavePackage(final String name, String number) {
        try {
            setProgressIndicator(true);
            ExpressModel.queryPackage(null, number, new BeanCallback<PackageBean>() {
                @Override
                public void onSuccess(PackageBean data) {
                    data.setName(name);
                    LogUtils.e("onSuccess  物流:" + data.getData());
                    data.setColorAvatar(colorRes[(int) (Math.random() * colorRes.length)]);
                    RealmPackageHelper.getInstance().increaseOrUpdate(data);
                    setProgressIndicator(false);
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onFail(String msg) {
                    setProgressIndicator(false);
                    if (!NetworkUtil.netIsAvailable(AddPackageActivity.this))
                        showNetworkError();
                    else
                        showSnack(fab, msg);
                }
            });
        } catch (Exception e) {
            LogUtils.e("请求出错: " + e.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //监听视图变化 输入名称时scrollView抬高不挡住名称
        editTextName.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                scrollView.getWindowVisibleDisplayFrame(rect);
                int mainInvisibleHeight = scrollView.getRootView().getHeight() - rect.bottom;
                if (mainInvisibleHeight > 150) {
                    int[] location = new int[2];
                    scrollView.getLocationInWindow(location);
                    int scrollHeight = (location[1] + scrollView.getHeight()) - rect.bottom;
                    scrollView.scrollTo(0, scrollHeight);
                } else {
                    scrollView.scrollTo(0, 0);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //扫描结果，得到快递单号
            case SCANNING_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        editTextNumber.setText(bundle.getString("result"));
                    }
                }
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScanningActivity();
                } else {
                    hideImm();
                    //权限请求不同意，提示用户自己去设置
                    new NotifyDialog()
                            .setTitle(getString(R.string.permission_denied))
                            .setPositiveButton(getString(R.string.go_to_settings))
                            .setNegativeButton(getString(android.R.string.cancel))
                            .setOnCheckListener(new NotifyDialog.OnCheckListener() {
                                @Override
                                public void isCheck(boolean check) {
                                    if (check) {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }
                                }
                            }).show(this, getString(R.string.require_permission));
                }
                break;
        }
    }

    //检测相机权限 有跳转二维码扫描，没有请求
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissionOrToScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
        } else {
            startScanningActivity();
        }
    }

    //跳转二维码扫描
    private void startScanningActivity() {
        try {
            Intent intent = new Intent(this, CaptureActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, SCANNING_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //进度条显示隐藏
    private void setProgressIndicator(boolean loading) {
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    //订单号已添加
    private void showNumberExistError() {
        showSnack(fab, getString(R.string.package_exist));
    }

    //订单号输入不合法或无效
    private void showNumberError() {
        showSnack(fab, getString(R.string.wrong_number_and_check));
    }

    //网络没开启，提示并给去设置的按钮
    private void showNetworkError() {
        Snackbar.make(fab, R.string.network_error, Snackbar.LENGTH_SHORT)
                .setAction(R.string.go_to_settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent().setAction(Settings.ACTION_SETTINGS));
                    }
                }).show();
    }

    //软键盘隐藏
    private void hideImm() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        assert imm != null;
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(fab.getWindowToken(), 0);
        }
    }
}
