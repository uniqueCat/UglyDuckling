package msf.uglyduckling.bean;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Administrator on 2018/4/18.
 */

public class PackageBean extends RealmObject {


    @Override
    public String toString() {
        return "PackageBean{" +
                "update=" + update +
                ", mailNo='" + mailNo + '\'' +
                ", updateStr='" + updateStr + '\'' +
                ", ret_code=" + ret_code +
                ", flag=" + flag +
                ", dataSize=" + dataSize +
                ", status=" + status +
                ", tel='" + tel + '\'' +
                ", expSpellName='" + expSpellName + '\'' +
                ", expTextName='" + expTextName + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    /**
     * update : 1524018006797
     * mailNo : 3357853764525
     * updateStr : 2018-04-18 10:20:06
     * ret_code : 0
     * flag : true
     * dataSize : 9
     * status : 4
     * tel : 400-889-5543
     * expSpellName : shentong
     * data : [{"time":"2018-04-14 21:03:38","context":"深圳市 尹13828750210-已签收，感谢您选择申通快递，期待再次为您服务。"},{"time":"2018-04-14 21:03:31","context":"深圳市 尹13828750210-已签收，感谢您选择申通快递，期待再次为您服务。"},{"time":"2018-04-14 16:23:36","context":"深圳市 广东罗湖东门营业点-尹焕章(13828750210)-派件中"},{"time":"2018-04-14 07:49:57","context":"深圳市 已到达-广东罗湖东门营业点"},{"time":"2018-04-13 23:55:45","context":"深圳市 广东深圳中转部-已发往-广东罗湖东门营业点"},{"time":"2018-04-12 20:58:20","context":"杭州市 浙江杭州航空部-已发往-广东深圳中转部"},{"time":"2018-04-12 19:29:42","context":"杭州市 浙江杭州下沙公司-已发往-浙江杭州航空部"},{"time":"2018-04-12 19:14:49","context":"杭州市 浙江杭州下沙公司-已发往-浙江杭州航空部"},{"time":"2018-04-12 19:11:06","context":"杭州市 浙江杭州下沙公司(0571-88132222)-浙江杭州下沙公司-已收件"}]
     * expTextName : 申通快递
     * msg : 查询成功!
     */

    @Ignore
    public static final String PRIMARY_KEY = "mailNo";
    private boolean isRead; //已读未读？
    @PrimaryKey
    private String mailNo;
    private long update;
    private String name;
    private String updateStr;
    private int ret_code;
    private boolean flag;
    private int dataSize;
    private int status;
    private String tel;
    private String expSpellName;
    private String expTextName;
    private String msg;
    private int colorAvatar;
    private RealmList<PackageStateBean> data;

    public int getColorAvatar() {
        return colorAvatar;
    }

    public void setColorAvatar(int colorAvatar) {
        this.colorAvatar = colorAvatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUpdate() {
        return update;
    }

    public void setUpdate(long update) {
        this.update = update;
    }

    public String getMailNo() {
        return mailNo;
    }

    public void setMailNo(String mailNo) {
        this.mailNo = mailNo;
    }

    public String getUpdateStr() {
        return updateStr;
    }

    public void setUpdateStr(String updateStr) {
        this.updateStr = updateStr;
    }

    public int getRet_code() {
        return ret_code;
    }

    public void setRet_code(int ret_code) {
        this.ret_code = ret_code;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getExpSpellName() {
        return expSpellName;
    }

    public void setExpSpellName(String expSpellName) {
        this.expSpellName = expSpellName;
    }

    public String getExpTextName() {
        return expTextName;
    }

    public void setExpTextName(String expTextName) {
        this.expTextName = expTextName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public RealmList<PackageStateBean> getData() {
        return data;
    }

    public void setData(RealmList<PackageStateBean> data) {
        this.data = data;
    }

    // 实现Cloneable，调用object的clone也得不到一个新的对象，这个对象被数据库那边删除了，clone出去这个对象也没了！？ 所以自己new了个新的，手动复制需要的值
    public PackageBean clone() throws CloneNotSupportedException {
        PackageBean pack = new PackageBean();
        pack.setName(name);
        pack.setColorAvatar(colorAvatar);
        pack.setRead(isRead);
        pack.setExpTextName(expTextName);
        pack.setExpSpellName(expSpellName);
        pack.setMailNo(mailNo);
        pack.setMsg(msg);
        pack.setTel(tel);
        pack.setStatus(status);
        pack.setRet_code(ret_code);
        pack.setUpdateStr(updateStr);
        pack.setUpdate(update);
        if (data == null)
            return pack;
        RealmList<PackageStateBean> list = new RealmList<>();
        for (PackageStateBean datum : data) {
            list.add(new PackageStateBean(datum.getTime(), datum.getContext()));
        }
        pack.setData(list);
        return pack;
    }
}
