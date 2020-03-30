package com.pratham.admin.activities.DeviceInformation;

public interface DeviceInfoContract {
    interface DeviceInfoView{
        void showDeviceInfo(int osApiLevel, String availStorage);
    }
    interface DeviceInfoPresenter{
        void populateDeviceInfo();
        void setView(DeviceInfoContract.DeviceInfoView deviceInfoView);
        //void addMetaData();
    }
}
