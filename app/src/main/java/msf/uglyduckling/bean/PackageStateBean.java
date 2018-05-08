package msf.uglyduckling.bean;

import io.realm.RealmObject;

/**
 * Created by Administrator on 2018/4/25.
 */

public class PackageStateBean extends RealmObject implements Cloneable {
    @Override
    public String toString() {
        return " {" +
                "time:'" + time + '\'' +
                ", context:'" + context + '\'' +
                '}';
    }

    /**
     * time : 2018-04-14 21:03:38
     * context : 深圳市 尹13828750210-已签收，感谢您选择申通快递，期待再次为您服务。
     */

    private String time;
    private String context;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public PackageStateBean(String time, String context) {
        this.time = time;
        this.context = context;
    }

    public PackageStateBean() {
    }
}
