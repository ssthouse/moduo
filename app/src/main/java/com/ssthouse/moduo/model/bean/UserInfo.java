package com.ssthouse.moduo.model.bean;

/**
 * 用户信息---对应云端table
 * Created by ssthouse on 2016/1/22.
 */
public class UserInfo {

    private String username;
    private String password;
    private String gesturePassword;

    public UserInfo(String username, String password, String gesturePassword) {
        this.username = username;
        this.password = password;
        this.gesturePassword = gesturePassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGesturePassword() {
        return gesturePassword;
    }

    public void setGesturePassword(String gesturePassword) {
        this.gesturePassword = gesturePassword;
    }

    @Override
    public String toString() {
        return username + "    " + password + "    " + gesturePassword + "-----";
    }

    /**
     * 数据是否完成:
     * 是否可被上传至Cloud
     *
     * @return
     */
    public boolean isComplete() {
        if (username != null && username.length() != 0
                && password != null && password.length() != 0
                && gesturePassword != null && gesturePassword.length() != 0){
            return true;
        }else{
            return false;
        }
    }
}
