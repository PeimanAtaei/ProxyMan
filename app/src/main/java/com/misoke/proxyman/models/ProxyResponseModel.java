package com.misoke.proxyman.models;

import java.util.List;

public class ProxyResponseModel {

    private ResponseModel response;
    private List<ProxyModel> proxies;


    public ResponseModel getResponse() {
        return response;
    }

    public void setResponse(ResponseModel response) {
        this.response = response;
    }

    public List<ProxyModel> getProxies() {
        return proxies;
    }

    public void setProxies(List<ProxyModel> proxies) {
        this.proxies = proxies;
    }
}
