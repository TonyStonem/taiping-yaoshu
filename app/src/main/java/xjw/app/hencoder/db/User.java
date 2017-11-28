package xjw.app.hencoder.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xjw on 2017/11/28 21:34
 * Email 1521975316@qq.com
 */

@Entity
public class User {

    @Id
    private long id;
    @NotNull
    private String name;
    private String address;

    @Transient
    private int state;

    @Generated(hash = 864790089)
    public User(long id, @NotNull String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
