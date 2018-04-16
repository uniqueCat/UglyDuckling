package msf.uglyduckling.net.service;


import io.reactivex.Observable;
import msf.uglyduckling.bean.BaseResponse;
import msf.uglyduckling.bean.HistoryTodayBean;
import msf.uglyduckling.net.Api;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2018/4/14.
 */

public interface HistoryTodayService {

    @POST(Api.HISTORY_TODAY_FIELD)
    @FormUrlEncoded
    Observable<BaseResponse<HistoryTodayBean>> getHistoryToday(@Field("showapi_appid") String showapiAppid,
                                                               @Field("showapi_sign") String showapiSign,
                                                               @Field("date") String date);

}
