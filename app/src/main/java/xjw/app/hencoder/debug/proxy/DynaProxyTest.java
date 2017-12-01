package xjw.app.hencoder.debug.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by xjw on 2017/12/1 17:26
 * Email 1521975316@qq.com
 *
 * 动态代理
 */

public class DynaProxyTest {

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

    public class ProxyHandler implements InvocationHandler {

        private final RealSubject subject;

        public ProxyHandler() {
            subject = new RealSubject();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = method.invoke(subject, args);
            System.out.println("====after====");
            return result;
        }
    }

}
