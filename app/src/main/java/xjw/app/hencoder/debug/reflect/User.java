package xjw.app.hencoder.debug.reflect;

/**
 * Created by xjw on 2017/12/1 16:45
 * Email 1521975316@qq.com
 */

public class User {

    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public User() {

    }

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "name >> " + name + "; age >> " + age;
    }
}
