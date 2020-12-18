package com.pratham.admin.activities.DeviceInformation;

public interface DeviceInfoContract {
    interface DeviceInfoView{
        void showDeviceInfo(int osApiLevel, String osVnum, String osVname, String availStorage,
                            String wifi, String devId, String serId, String manuf, String model,
                            String resol);
    }
    interface DeviceInfoPresenter{
        void populateDeviceInfo(String screenResol);
        void setView(DeviceInfoContract.DeviceInfoView deviceInfoView);
        void addMetaData();
    }
}
