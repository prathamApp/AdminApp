package com.pratham.admin.interfaces;

import com.androidnetworking.error.ANError;

public interface NetworkCallListnerSelectProgram {
    public void onResponce(String response, String header, String type, String program);

    public void onError(ANError anError, String header, String type, String program);
}
