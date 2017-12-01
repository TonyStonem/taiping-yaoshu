package xjw.app.hencoder.debug.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by xjw on 2017/12/1 16:36
 * Email 1521975316@qq.com
 */

public class Test {

    public static void main(String[] args) {
//        test1();
        try {
            String name = User.class.getName();
            System.out.println(name);

            Class<?> c = Class.forName(name);
            if (c.isInterface()) return;
            Object ins = c.newInstance();
            Method methodN = c.getMethod("setName", String.class);
            Method methodA = c.getMethod("setAge", Integer.TYPE);
            methodN.invoke(ins, "Sid");
            methodA.invoke(ins, 17);
            System.out.println(ins);

            Constructor<?> constructor = c.getConstructor(String.class, Integer.TYPE);
            Object[] objects = new Object[2];
            objects[0] = "Tony";
            objects[1] = 17;
            Object o = constructor.newInstance(objects);
            System.out.println(o);


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void test1() {
        try {
            String name = List.class.getName();
            System.out.println(name);
            Class<?> c = Class.forName(name);
            if (c.isInterface()) {
                System.out.println("isInterface");
                return;
            }
            List list = (List) c.newInstance();
            for (int i = 0; i < 5; i++) {
                list.add("index >> " + i);
            }

            for (Object o :
                    list) {
                System.out.println(o);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
