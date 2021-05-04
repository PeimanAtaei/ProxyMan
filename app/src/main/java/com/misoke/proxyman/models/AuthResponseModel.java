package com.misoke.proxyman.models;

public class AuthResponseModel {

    private ResponseModel response;
    private UserModel user;

    public ResponseModel getResponse() {
        return response;
    }

    public void setResponse(ResponseModel response) {
        this.response = response;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
