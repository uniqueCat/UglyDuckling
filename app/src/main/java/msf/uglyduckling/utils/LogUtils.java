package msf.uglyduckling.utils;

import android.util.Log;

/**
 * Created by Administrator on 2017/12/28.
 */

public class LogUtils {

    public static final String TAG = "test";

    public static final boolean isLog = true;

    public static void e(String msg) {
        if (isLog)
            Log.e(TAG, "**********:"+msg);
    }
}
