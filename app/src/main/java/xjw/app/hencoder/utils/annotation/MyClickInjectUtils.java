package xjw.app.hencoder.utils.annotation;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by xjw on 2017/7/26.
 */

public class MyClickInjectUtils {

    public static void inject(final AppCompatActivity activity) {
        Field[] fields = activity.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
//            field.isAnnotationPresent(MyInject.class);
            MyInject annotation = field.getAnnotation(MyInject.class);
            if (annotation != null) {
                int id = annotation.value();
                View view = activity.findViewById(id);
                try {
                    field.set(activity, view);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        Method[] methods = activity.getClass().getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            method.setAccessible(true);
//            method.isAnnotationPresent(MyClick.class);
            MyClick annotation = method.getAnnotation(MyClick.class);
            if (annotation != null) {
                int[] values = annotation.value();
                for (int j :
                        values) {
                    final View btn = activity.findViewById(j);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                method.invoke(activity, btn);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

        }


    }

}
