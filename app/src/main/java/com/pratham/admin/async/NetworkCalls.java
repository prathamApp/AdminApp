package com.pratham.admin.async;

import android.app.ProgressDialog;
import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.pratham.admin.interfaces.NetworkCallListener;
import com.pratham.admin.interfaces.NetworkCallListnerSelectProgram;

import org.json.JSONArray;

public class NetworkCalls {
    private static NetworkCalls networkCalls;
    private ProgressDialog dialog;
    //  private NetworkCallListener networkCallListner;
    static Context mContext;

    private NetworkCalls() {
    }

    public static NetworkCalls getNetworkCallsInstance(Context context) {
        mContext = context;
        if (networkCalls == null) {
            networkCalls = new NetworkCalls();
        }
        return networkCalls;
    }


    //push data(json) to url      used in Activity_QRScan,AssignTabletMD
    public void postRequest(final NetworkCallListener networkCallListener, String url, String msg, String json, final String header) {
/*        dialog = new ProgressDialog(mContext);
        dialog.setTitle(msg);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();*/
        AndroidNetworking.post(url).setContentType("application/json").addStringBody(json).build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                networkCallListener.onResponse(response, header);
                //dialog.dismiss();
            }

            @Override
            public void onError(ANError anError) {
                networkCallListener.onError(anError, header);
                //dialog.dismiss();
            }
        });
    }

    public void getRequest(final NetworkCallListener networkCallListener, String url, String msg, final String header) {
        dialog = new ProgressDialog(mContext);
        dialog.setTitle(msg);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        AndroidNetworking.get(url).setPriority(Priority.MEDIUM).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                networkCallListener.onResponse(response.toString(), header);
                dialog.dismiss();
            }

            @Override
            public void onError(ANError anError) {
                networkCallListener.onError(anError, header);
                dialog.dismiss();
            }
        });
    }

    /*SELECT PROGRAM Calls*/
    public void getRequestWithProgram(final NetworkCallListnerSelectProgram networkCallListnerSelectProgram, String url, final String header, final String type, final String program) {
        AndroidNetworking.get(url).setPriority(Priority.MEDIUM).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                networkCallListnerSelectProgram.onResponce(response.toString(), header, type, program);

            }

            @Override
            public void onError(ANError anError) {
                networkCallListnerSelectProgram.onError(anError, header, type, program);
            }
        });
    }
    public void getRequestWithautLoader(final NetworkCallListener networkCallListener, String url, final String header) {
        AndroidNetworking.get(url).setPriority(Priority.MEDIUM).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                networkCallListener.onResponse(response.toString(), header);

            }

            @Override
            public void onError(ANError anError) {
                networkCallListener.onError(anError, header);
            }
        });
    }
}
