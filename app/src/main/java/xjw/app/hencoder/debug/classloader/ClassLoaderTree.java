package xjw.app.hencoder.debug.classloader;

/**
 * Created by xjw on 2017/12/1 10:46
 * Email 1521975316@qq.com
 */

public class ClassLoaderTree {

    public static void main(String[] agrs) {
        classTree();
    }

    /**
     * from IBM
     * 测试不同的类加载器加载出来的class文件 虚拟机是否会认定为同一个类
     * 结果 : 不同的类
     * java.lang.ClassCastException
     * 结论 : 理解类加载器的代理模式的设计动机,代理模式是为了保证 Java 核心库的类型安全。
     *          对于 Java 核心库的类的加载工作由引导类加载器来统一完成，
     *              保证了 Java 应用所使用的都是同一个版本的 Java 核心库的类，是互相兼容的。
     */
    private static void testClassIdentity() {
//        String classDataRootPath = "C:\\workspace\\Classloader\\classData";
//        FileSystemClassLoader fscl1 = new FileSystemClassLoader(classDataRootPath);
//        FileSystemClassLoader fscl2 = new FileSystemClassLoader(classDataRootPath);
//        String className = "xjw.app.hencoder.debug.classloader.Sample";
//        try {
//            Class<?> class1 = fscl1.loadClass(className);
//            Object obj1 = class1.newInstance();
//            Class<?> class2 = fscl2.loadClass(className);
//            Object obj2 = class2.newInstance();
//            Method setSampleMethod = class1.getMethod("setSample", java.lang.Object.class);
//            setSampleMethod.invoke(obj1, obj2);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private static void classTree() {
        //每个 Java 类都维护着一个指向定义它的类加载器的引用，
        //      通过 getClassLoader()方法就可以获取到此引用
        ClassLoader loader = ClassLoaderTree.class.getClassLoader();
        while (loader != null) {
            System.out.println(loader.toString());
            loader = loader.getParent();
        }
    }

}
