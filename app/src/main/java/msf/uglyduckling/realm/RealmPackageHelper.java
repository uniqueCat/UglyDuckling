package msf.uglyduckling.realm;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import msf.uglyduckling.bean.PackageBean;

/**
 * Created by Administrator on 2018/4/18.
 */

public class RealmPackageHelper {

    public static final String DATABASE_NAME = "Espresso.realm";

    private static RealmPackageHelper instance = new RealmPackageHelper();

    private Realm realm;

    private RealmPackageHelper() {
        if (realm == null) {
            realm = Realm.getInstance(new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .name(DATABASE_NAME)
                    .build());
        }
    }

    public static RealmPackageHelper getInstance() {
        return instance;
    }

    //插入或更新一个集合的包裹
    public void increasesOrUpdate(final List<PackageBean> packageBeans) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (PackageBean packageBean : packageBeans) {
                    realm.copyToRealmOrUpdate(packageBean);
                }
            }
        });
    }

    //插入或更新一个包裹
    public void increaseOrUpdate(final PackageBean packageBean) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(packageBean);
            }
        });
    }

    //更新一个包裹的name
    public void updatePackageName(final String primaryKey, final String newName) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                PackageBean first = realm.where(PackageBean.class).equalTo(PackageBean.PRIMARY_KEY, primaryKey).findFirst();
                first.setName(newName);
            }
        });
    }

    //更新包裹 已读还是未读
    public void updatePackageReadState(final String primaryKey, final boolean isRead) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(PackageBean.class).equalTo(PackageBean.PRIMARY_KEY, primaryKey).findFirst().setRead(isRead);
            }
        });
    }

    //查询所有包裹
    public List<PackageBean> queryAll() {
        return realm.where(PackageBean.class).findAll();
    }

    //根据主键查询一个包裹
    public PackageBean query(String primaryKey) {
        return realm.where(PackageBean.class).equalTo(PackageBean.PRIMARY_KEY, primaryKey).findFirst();
    }

    //根据主键删除一个包裹
    public void deletedPackage(final String primaryKey) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(PackageBean.class).equalTo(PackageBean.PRIMARY_KEY, primaryKey).findFirst().deleteFromRealm();
            }
        });
    }

    //删除所有包裹
    public void deletedAllPackage() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(PackageBean.class);
            }
        });
    }

    public void close() {
        if (realm != null)
            realm.close();
    }

}
