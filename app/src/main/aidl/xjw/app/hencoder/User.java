package xjw.app.hencoder;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xjw on 2017/11/29 21:03
 * Email 1521975316@qq.com
 */

public class User implements Parcelable{

    private String name;
    private String address;

    public User() {
    }

    protected User(Parcel in) {
        name = in.readString();
        address = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
    }

    /**
     * 参数是一个Parcel,用它来存储与传输数据
     * 使aidl 支持out inout的tag
     * @param dest
     */
    public void readFromParcel(Parcel dest) {
        //注意，此处的读值顺序应当是和writeToParcel()方法中一致的
        name = dest.readString();
        address = dest.readString();
    }

    @Override
    public String toString() {
        return "name >> " + name + " ; address >> " + address;
    }
}
