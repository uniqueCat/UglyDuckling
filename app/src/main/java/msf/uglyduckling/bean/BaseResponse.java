package msf.uglyduckling.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/14.
 */

public class BaseResponse<T> implements Serializable {

    @SerializedName("showapi_res_code")
    public int showapi_res_code;
    @SerializedName("showapi_res_error")
    public String showapi_res_error;
    @SerializedName("showapi_res_body")
    public T showapi_res_body;
}
