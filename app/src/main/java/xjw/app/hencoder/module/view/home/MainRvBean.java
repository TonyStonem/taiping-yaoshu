package xjw.app.hencoder.module.view.home;

/**
 * Created by xjw on 2017/8/15.
 */

public class MainRvBean {

    public int imgResID;
    public String title;
    public String content;
    public String time;
    public String num;

    public MainRvBean() {
    }

    public MainRvBean(String title, String content, String time, String num,int imgResID) {
        this.title = title;
        this.content = content;
        this.time = time;
        this.num = num;
        this.imgResID = imgResID;
    }
}
