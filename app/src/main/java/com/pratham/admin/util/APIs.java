package com.pratham.admin.util;

public class APIs {
    private APIs() {
    }

    public static final String village = "village";
    public static final String CRL = "CRL";
    public static final String Group = "Groups";
    public static final String Student = "Student";
    public static final String HL = "Hybrid Learning";
    public static final String HLpullVillagesURL = "http://www.hlearning.openiscool.org/api/village/get?programId=1&state=";
    public static final String HLpullGroupsURL = "http://www.devtab.openiscool.org/api/Group?programid=1&villageId=";
    public static final String HLpullStudentsURL = "http://www.devtab.openiscool.org/api/student?programid=1&villageId=";
    public static final String HLpullCrlsURL = "http://www.swap.prathamcms.org/api/UserList?statecode=";
                                                 /* http://www.swap.prathamcms.org/api/UserList?statecode=MH&programid=1*/
    public static final String RI = "Read India";
    public static final String RIpullVillagesURL = "http://www.readindia.openiscool.org/api/village/get?programId=2&state=";
    public static final String RIpullGroupsURL = "http://www.devtab.openiscool.org/api/Group?programid=2&villageId=";
    public static final String RIpullStudentsURL = "http://www.devtab.openiscool.org/api/student?programid=2&villageId=";
    public static final String RIpullCrlsURL = "http://www.readindia.openiscool.org/api/crl/get?programId=2";

    public static final String SC = "Second Chance";
    public static final String SCpullVillagesURL = "http://www.hlearning.openiscool.org/api/village/get?programId=3&state=";
    public static final String SCpullGroupsURL = "http://www.devtab.openiscool.org/api/Group?programid=3&villageId=";
    public static final String SCpullStudentsURL = "http://www.devtab.openiscool.org/api/student?programid=3&villageId=";
    public static final String SCpullCrlsURL = "http://www.hlearning.openiscool.org/api/crl/get?programId=3";

    public static final String PI = "Pratham Institute";
    public static final String PIpullVillagesURL = "http://www.tabdata.prathaminstitute.org/api/village/get?programId=4&state=";
    public static final String PIpullGroupsURL = "http://www.devtab.openiscool.org/api/Group?programid=4&villageId=";
    public static final String PIpullStudentsURL = "http://www.devtab.openiscool.org/api/student?programid=4&villageId=";
    public static final String PIpullCrlsURL = "http://www.tabdata.prathaminstitute.org/api/crl/get?programId=4";

    //NewPushURL
  /*  public static final String HLpushToServerURL = "http://www.hlearning.openiscool.org/api/datapush/pushusage";*/
    public static final String HLpushToServerURL = "http://www.swap.prathamcms.org/api/QRSwap/SwapData";
    public static final String RIpushToServerURL = "http://www.readindia.openiscool.org/api/datapush/pushusage";
    public static final String SCpushToServerURL = "http://www.hlearning.openiscool.org/api/datapush/pushusage";
    public static final String PIpushToServerURL = "http://www.tabdata.prathaminstitute.org/api/datapush/pushusage";

   // Device Tracking Push API
   public static final String TabTrackPushAPI ="http://www.swap.prathamcms.org/api/QRPush/QRPushData";
}
