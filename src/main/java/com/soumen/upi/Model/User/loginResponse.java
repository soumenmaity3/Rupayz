package com.soumen.upi.Model.User;

public class loginResponse {


    public loginResponse(String token, UPIUser user) {
        this.token = token;
        this.user = user;
    }
    String token;
    UPIUser user;
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public UPIUser getUser() {
        return user;
    }
    public void setUser(UPIUser user) {
        this.user = user;
    }

}