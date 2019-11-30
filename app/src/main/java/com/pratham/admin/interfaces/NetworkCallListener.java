package com.pratham.admin.interfaces;

import com.androidnetworking.error.ANError;

public interface NetworkCallListener {

    public void onResponse(String response, String header);

    public void onError(ANError anError, String header);

}
