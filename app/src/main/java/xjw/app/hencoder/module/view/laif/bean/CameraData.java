package xjw.app.hencoder.module.view.laif.bean;

/**
 * Created by xjw on 2017/11/20 11:24
 * Email 1521975316@qq.com
 */

public class CameraData {

    public static final int FACING_FRONT = 1;
    public static final int FACING_BACK = 2;

    public int cameraID;//camera的id
    public int cameraFacing;//区分前后摄像头
    public int cameraWidth;//camera的采集宽度
    public int cameraHeight;//camera的采集高度
    public boolean hasLight;//是否有闪光灯

    public CameraData() {
    }

    public CameraData(int id, int facing) {
        cameraID = id;
        cameraFacing = facing;
    }

}
