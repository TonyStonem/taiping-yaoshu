package xjw.app.hencoder.debug.proxy;

/**
 * Created by xjw on 2017/12/1 17:21
 * Email 1521975316@qq.com
 *
 * 静态代理
 */

public class ProxyTest {

    public static void main(String[] args) {

    }

    public interface Subject{
        void request();
    }

    public class RealSubject implements Subject{

        @Override
        public void request() {
            System.out.println("RealSubject >> request");
        }
    }

    public class Proxy implements Subject{

        private final RealSubject subject;

        public Proxy() {
            subject = new RealSubject();
        }

        @Override
        public void request() {
            subject.request();
        }
    }

}
