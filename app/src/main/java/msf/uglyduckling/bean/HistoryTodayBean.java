package msf.uglyduckling.bean;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Administrator on 2018/4/14.
 */

public class HistoryTodayBean extends RealmObject {

    /**
     * ret_code : 0
     * list : [{"title":"隋炀帝下令开凿大运河","month":4,"year":"605","day":14},{"title":"《我的奋斗》\u201c进入日本教科书\u201d事件始末","img":"http://img.lssdjt.com/201705/02221100530.jpg","month":4,"year":"2017","day":14},{"title":"聊城辱母案","img":"http://img.lssdjt.com/201703/27092304658.jpg","month":4,"year":"2016","day":14},{"title":"重庆市司法局原局长文强一审被判死刑","img":"http://img.lssdjt.com/201004/14/32184020908.jpg","month":4,"year":"2010","day":14},{"title":"\u201c长征三号甲\u201d运载火箭将一颗北斗导航卫星送入太空","img":"http://img.lssdjt.com/201004/11/B723750682.jpg","month":4,"year":"2007","day":14},{"title":"中国首条大容量欧亚陆地光缆网络开通","month":4,"year":"2004","day":14},{"title":"国务院对三起\u201c特大事故\u201d作出处理决定","month":4,"year":"2004","day":14},{"title":"联合国第七次否决反华人权提案","img":"http://img.lssdjt.com/200905/17/5A151027426.jpg","month":4,"year":"1997","day":14},{"title":"国务院批准销毁邱氏鼠药","img":"http://img.lssdjt.com/201004/11/C3182235751.jpg","month":4,"year":"1995","day":14},{"title":"上海发行中国第一张联名信用卡\u2014\u2014牡丹上航联名卡","month":4,"year":"1995","day":14},{"title":"波兰举办\u201c酷似名人\u201d大赛","img":"http://img.lssdjt.com/200905/17/F1151223975.jpg","month":4,"year":"1993","day":14},{"title":"北京游乐园举行首次激光水幕电影晚会","img":"http://img.lssdjt.com/200905/17/6F151342724.jpg","month":4,"year":"1991","day":14},{"title":"政治解决阿富汗问题协议在日内瓦签署","month":4,"year":"1988","day":14},{"title":"辽宁出现全国第一个租赁企业集团","img":"http://img.lssdjt.com/200905/17/79151549998.jpg","month":4,"year":"1984","day":14},{"title":"李师科抢银行案","img":"http://img.lssdjt.com/201607/07002654814.jpg","month":4,"year":"1982","day":14},{"title":"中国获三十六届世乒赛全部冠军","month":4,"year":"1981","day":14},{"title":"鉴真大师像回故乡\u2014\u2014扬州展出","month":4,"year":"1980","day":14},{"title":"古巴审判\u201c入侵者\u201d并索要赎金","img":"http://img.lssdjt.com/200905/17/9C152019123.jpg","month":4,"year":"1962","day":14},{"title":"第26届世界乒乓球锦标赛在北京举行","img":"http://img.lssdjt.com/200404/14/52132012488.jpg","month":4,"year":"1961","day":14},{"title":"邱钟惠成为中国首位女子世界冠军","img":"http://img.lssdjt.com/201704/16212100855.jpg","month":4,"year":"1961","day":14},{"title":"中央军委海军领导机构成立","month":4,"year":"1950","day":14},{"title":"羊马河战役成为西北战局转折点","img":"http://img.lssdjt.com/200905/17/4F152249278.jpg","month":4,"year":"1947","day":14},{"title":"陈独秀案开始审理","month":4,"year":"1933","day":14},{"title":"西班牙第二共和国建立","img":"http://img.lssdjt.com/200905/17/BB15278127.jpg","month":4,"year":"1931","day":14},{"title":"揭露日本侵华野心第一人","img":"http://img.lssdjt.com/200905/17/E4152727777.jpg","month":4,"year":"1928","day":14},{"title":"全国教育统计普查结束","month":4,"year":"1923","day":14},{"title":"毛泽东等发起长沙新民学会","img":"http://img.lssdjt.com/200905/17/6F152855638.jpg","month":4,"year":"1918","day":14},{"title":"美国铺设第一条海底电缆","img":"http://img.lssdjt.com/200905/17/9A152941620.jpg","month":4,"year":"1910","day":14},{"title":"世界博览会在巴黎开幕","img":"http://img.lssdjt.com/200905/17/66153028763.jpg","month":4,"year":"1900","day":14},{"title":"清政府与美国签订《粤汉铁路借款合同》","month":4,"year":"1898","day":14},{"title":"英国议会通过世界上第一部版权法《安娜法令》","month":4,"year":"1710","day":14},{"title":"萨尔浒之战爆发","img":"http://img.lssdjt.com/201404/9/1729455277.jpg","month":4,"year":"1619","day":14}]
     */

    private int ret_code;
    private RealmList<HistoryTodayListBean> list;
    @PrimaryKey
    private String monthDay;
    private long updateTime;

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getRet_code() {
        return ret_code;
    }

    public void setRet_code(int ret_code) {
        this.ret_code = ret_code;
    }

    public RealmList<HistoryTodayListBean> getList() {
        return list;
    }

    public void setList(RealmList<HistoryTodayListBean> list) {
        this.list = list;
    }

    public String getMonthDay() {
        return monthDay;
    }

    public void setMonthDay(String monthDay) {
        this.monthDay = monthDay;
    }

}
