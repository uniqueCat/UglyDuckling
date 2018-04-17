package msf.uglyduckling.net;

import io.reactivex.observers.DisposableObserver;
import msf.uglyduckling.bean.BaseResponse;
import msf.uglyduckling.config.Const;
import msf.uglyduckling.utils.LogUtils;

/**
 * Created by Administrator on 2018/4/14.
 */

public abstract class BeanCallback<T> extends DisposableObserver<BaseResponse<T>> {

    @Override
    public void onNext(BaseResponse<T> tBaseResponse) {
        if (tBaseResponse.showapi_res_code == -1) {
            onFail(Const.ERROR_SYSTEM_CALL);
        } else if (tBaseResponse.showapi_res_code == -2) {
            onFail(Const.ERROR_NO_SURPLUS);
        } else if (tBaseResponse.showapi_res_code != 0) {
            onFail(Const.ERROR_UNKNOWN);
        } else {
            onSuccess(tBaseResponse.showapi_res_body);
        }
    }

    @Override
    public void onError(Throwable e) {
        LogUtils.e("DisposableObserver.onError:" + e.toString());
        onFail(Const.ERROR_UNKNOWN);
    }

    @Override
    public void onComplete() {

    }

    public abstract void onSuccess(T data);

    public abstract void onFail(String msg);
}
