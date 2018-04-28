package msf.uglyduckling.net.model;

import android.text.TextUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import msf.uglyduckling.bean.PackageBean;
import msf.uglyduckling.net.Api;
import msf.uglyduckling.net.BeanCallback;
import msf.uglyduckling.net.RetrofitUtil;
import msf.uglyduckling.net.service.PackageService;

/**
 * Created by Administrator on 2018/4/18.
 */

public class ExpressModel {

    private ExpressModel() {

    }

    public static void queryPackage(String com, String nu, BeanCallback<PackageBean> callback) {
        RetrofitUtil.getRetrofit().create(PackageService.class)
                .queryPackage(Api.SHOWAPI_APPID, Api.SHOWAPI_SIGN, TextUtils.isEmpty(com) ? "auto" : com, nu)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

}
