package msf.uglyduckling.bean;

import io.realm.RealmObject;

/**
 * Created by Administrator on 2018/5/8.
 */

public class HistoryTodayListBean extends RealmObject {
    /**
     * title : 隋炀帝下令开凿大运河
     * month : 4
     * year : 605
     * day : 14
     * img : http://img.lssdjt.com/201705/02221100530.jpg
     */

    private String title;
    private int month;
    private String year;
    private int day;
    private String img;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
