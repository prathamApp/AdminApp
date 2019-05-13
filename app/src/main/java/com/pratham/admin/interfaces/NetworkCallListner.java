package com.pratham.admin.interfaces;

import com.androidnetworking.error.ANError;

public interface NetworkCallListner {

    public void onResponce(String response, String header);

    public void onError(ANError anError, String header);

}
