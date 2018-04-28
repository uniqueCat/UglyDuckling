package msf.uglyduckling.net.service;

import io.reactivex.Observable;
import msf.uglyduckling.bean.BaseResponse;
import msf.uglyduckling.bean.PackageBean;
import msf.uglyduckling.net.Api;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2018/4/18.
 */

public interface PackageService {

    @POST(Api.EXPRESS_FIELD)
    @FormUrlEncoded
    Observable<BaseResponse<PackageBean>> queryPackage(@Field("showapi_appid") String appid,
                                                       @Field("showapi_sign") String sign,
                                                       @Field("com") String com,
                                                       @Field("nu") String nu);
}
