package xjw.app.hencoder.base;

/**
 * Created by xjw on 2017/8/15.
 */

public interface BaseContract {

    interface View {

        void showLoadDias();

        void cancelLoadDialogs();

        void showSnacks(String msg, int actionResId, Runnable action);

        void closes();

    }

    interface Presenter<V extends BaseContract.View> {

    }

}
