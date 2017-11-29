package xjw.app.hencoder.debug;

/**
 * Created by xjw on 2017/11/27 20:46
 * Email 1521975316@qq.com
 */ public class Test {

    public static void main(String[] args) {
        String s1 = "Programming";
        String s2 = new String("Programming");
        String s3 = "Program" + "ming";
        System.out.println(s1 == s2);//false
        System.out.println(s1 == s3);//true
        System.out.println(s1 == s1.intern());//true
//        test4b();
//        test4b2();
    }

    private static void test4b2() {
        for (int i = 0; i < 5; i++) {
//            if (i == 3) break;
            if (i == 3) continue;
            System.out.println("i >> " + i);
        }
    }

    private static void test4b() {
        int i = 0;
        while (true) {
//            if (i == 3) break;
            if (i == 3) return;
            System.out.println("i > " + i);
            i++;
        }
    }

}
