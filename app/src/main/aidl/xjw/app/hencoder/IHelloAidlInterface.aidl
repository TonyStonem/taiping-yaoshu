// IHelloAidlInterface.aidl
package xjw.app.hencoder;

import xjw.app.hencoder.User;
// Declare any non-default types here with import statements

interface IHelloAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

    void setUser(in User user);

    List<User> getUser();

}
