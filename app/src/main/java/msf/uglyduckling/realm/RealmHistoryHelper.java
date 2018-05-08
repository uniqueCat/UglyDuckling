package msf.uglyduckling.realm;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import msf.uglyduckling.bean.HistoryTodayBean;

/**
 * Created by Administrator on 2018/5/8.
 */

public class RealmHistoryHelper {

    private static final String DATABASE_NAME = "HistoryToday.realm";

    private static RealmHistoryHelper instance = new RealmHistoryHelper();

    private Realm realm;

    private RealmHistoryHelper() {
        if (realm == null) {
            realm = Realm.getInstance(new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .name(DATABASE_NAME)
                    .build());
        }
    }

    public static RealmHistoryHelper getInstance() {
        return instance;
    }

    //添加或更新历史上的某天
    public void increasesHistoryToday(final HistoryTodayBean bean) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (bean.getList() != null && bean.getList().size() > 1) {
                    String month = bean.getList().get(0).getMonth() + "";
                    String day = bean.getList().get(0).getDay() + "";
                    if (month.length() == 1)
                        month = "0" + month;
                    if (day.length() == 1)
                        day = "0" + day;
                    bean.setMonthDay(month + day);
                    bean.setUpdateTime(System.currentTimeMillis());
                }
                realm.copyToRealmOrUpdate(bean);
            }
        });
    }

    //清除历史上的某天
    public void removeHistoryToday(final String monthDay) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(HistoryTodayBean.class).equalTo("monthDay", monthDay).findAll().deleteAllFromRealm();
            }
        });
    }

    //清除全部
    public void removeAll() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(HistoryTodayBean.class);
            }
        });
    }

    //查询历史上的某天
    public HistoryTodayBean queryHistoryBean(String monthDay) {
        return realm.where(HistoryTodayBean.class).equalTo("monthDay", monthDay).findFirst();
    }

    public List<HistoryTodayBean> queryAllHistory() {
        return realm.where(HistoryTodayBean.class).findAll();
    }

    public void close() {
        realm.close();
    }
}
