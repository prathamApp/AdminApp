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
    public static final String HLpullCrlsURL = "http://www.swap.prathamcms.org/api/UserList?programId=1&statecode=";

    public static final String ECE = "ECE";
    public static final String ECEpullVillagesURL = "http://www.hlearning.openiscool.org/api/village/get?programId=8&state=";
    public static final String ECEpullGroupsURL = "http://www.devtab.openiscool.org/api/Group?programid=8&villageId=";
    public static final String ECEpullStudentsURL = "http://www.devtab.openiscool.org/api/student?programid=8&villageId=";
    public static final String ECEpullCrlsURL = "http://www.swap.prathamcms.org/api/UserList?programId=8&statecode=";


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
    public static final String TabTrackPushAPI = "http://www.swap.prathamcms.org/api/QRPush/QRPushData";

    // Pull Courses
    public static final String PullCourses = "http://www.swap.prathamcms.org/api/course/get";

    // Pull HLCourseCommunity
   // public static final String PullHLCourseCommunity = "http://swap.prathamcms.org/api/HLCoach/GetHLCourseCommunity/?villageid=1&programid=1";
    public static final String PullHLCourseCommunity = "http://swap.prathamcms.org/api/HLCoach/GetHLCourseCommunity/?";

    // Pull HLCourseCompletion
   // public static final String PullHLCourseCompletion = "http://swap.prathamcms.org/api/HLCoach/GetHLCourseCompletion/?villageid=1&programid=1";
    public static final String PullHLCourseCompletion = "http://swap.prathamcms.org/api/HLCoach/GetHLCourseCompletion/?";

    // Pull Coaches
   // public static final String PullCoaches = "http://swap.prathamcms.org/api/HLCoach/GetHLCoachInfo/?villageid=1&programid=1";
    public static final String PullCoaches = "http://swap.prathamcms.org/api/HLCoach/GetHLCoachInfo/?";

    // Push API of Forms
    public static final String PushForms = "http://www.swap.prathamcms.org/api/crlvisit/crlvisitpushdata";
}
