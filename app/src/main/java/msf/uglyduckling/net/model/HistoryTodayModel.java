package msf.uglyduckling.net.model;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import msf.uglyduckling.bean.HistoryTodayBean;
import msf.uglyduckling.net.Api;
import msf.uglyduckling.net.BeanCallback;
import msf.uglyduckling.net.RetrofitUtil;
import msf.uglyduckling.net.service.HistoryTodayService;

/**
 * Created by Administrator on 2018/4/14.
 */

public class HistoryTodayModel {

    private HistoryTodayModel() {
    }

    private static HistoryTodayService historyTodayService = RetrofitUtil.getRetrofit().create(HistoryTodayService.class);

    public static void getHistoryToday(String date, final BeanCallback<HistoryTodayBean> callback) {
        historyTodayService.getHistoryToday(Api.SHOWAPI_APPID, Api.SHOWAPI_SIGN, date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(callback);
    }
}
