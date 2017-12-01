package xjw.app.hencoder.debug.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by xjw on 2017/12/1 16:17
 * Email 1521975316@qq.com
 */

public class SimpleClassViewer {

    public static void main(String[] args) {

        try {
            String name = String.class.getName();
            System.out.println(name);
            Class c = Class.forName(name);
            //取得套件代表物件
            Package p = c.getPackage();

            System.out.printf(" package %s;%n ", p.getName());

            //取得型态修饰，像是public、final
            int m = c.getModifiers();

            System.out.print(Modifier.toString(m) + "  ");
            //如果是介面
            if (Modifier.isInterface(m)) {
                System.out.print(" interface ");
            } else {
                System.out.print(" class ");
            }

            System.out.println(c.getName() + " { ");

            //取得宣告的资料成员代表物件
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                //显示权限修饰，像是public、protected、private
                System.out.print(" \t " +
                        Modifier.toString(field.getModifiers()));
                //显示型态名称
                System.out.print("  " +
                        field.getType().getName() + "  ");
                //显示资料成员名称
                System.out.println(field.getName() + " ; ");
            }

            //取得宣告的建构方法代表物件
            Constructor[] constructors =
                    c.getDeclaredConstructors();
            for (Constructor constructor : constructors) {
                //显示权限修饰，像是public、protected、private
                System.out.print(" \t " +
                        Modifier.toString(
                                constructor.getModifiers()));
                //显示建构方法名称
                System.out.println("  " +
                        constructor.getName() + " (); ");
            }
            //取得宣告的方法成员代表物件
            Method[] methods = c.getDeclaredMethods();
            for (Method method : methods) {
                //显示权限修饰，像是public、protected、private
                System.out.print(" \t " +
                        Modifier.toString(
                                method.getModifiers()));
                //显示返回值型态名称
                System.out.print("  " +
                        method.getReturnType().getName() + "  ");
                //显示方法名称
                System.out.println(method.getName() + " (); ");
            }
            System.out.println(" } ");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("没有指定类别");
        } catch (ClassNotFoundException e) {
            System.out.println("找不到指定类别");
        }
    }

}
