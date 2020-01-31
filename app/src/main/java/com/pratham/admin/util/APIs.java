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
    public static final String HLpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfov2?programid=1&villageId=";
    //    public static final String HLpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfo?programid=1&villageId=";
    // public static final String HLpullStudentsURL = "http://www.devtab.openiscool.org/api/student?programid=1&villageId=";
    public static final String HLpullCrlsURL = "http://www.swap.prathamcms.org/api/UserList?programId=1&statecode=";
    public static final String HLpullAserURL = "http://devtab.openiscool.org/api/AserVillage?programid=1&villageid=";

    public static final String KGBV = "KGBV";
    public static final String KGBVpullVillagesURL = "http://www.hlearning.openiscool.org/api/village/get?programId=5&state=";
    public static final String KGBVpullGroupsURL = "http://www.devtab.openiscool.org/api/Group?programid=5&villageId=";
    public static final String KGBVpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfov2?programid=5&villageId=";
    //    public static final String KGBVpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfo?programid=5&villageId=";
//    public static final String KGBVpullStudentsURL = "http://www.devtab.openiscool.org/api/student?programid=5&villageId=";
    public static final String KGBVpullCrlsURL = "http://www.swap.prathamcms.org/api/UserList?programId=5&statecode=";
    public static final String KGBpullAserURL = "http://devtab.openiscool.org/api/AserVillage?programid=5&villageid=";


    public static final String ECE = "ECE";
    public static final String ECEpullVillagesURL = "http://www.hlearning.openiscool.org/api/village/get?programId=8&state=";
    public static final String ECEpullGroupsURL = "http://www.devtab.openiscool.org/api/Group?programid=8&villageId=";
    public static final String ECEpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfov2?programid=8&villageId=";
    //    public static final String ECEpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfo?programid=8&villageId=";
//    public static final String ECEpullStudentsURL = "http://www.devtab.openiscool.org/api/student?programid=8&villageId=";
    public static final String ECEpullCrlsURL = "http://www.swap.prathamcms.org/api/UserList?programId=8&statecode=";
    public static final String ECEpullAserURL = "http://devtab.openiscool.org/api/AserVillage?programid=8&villageid=";

    /* http://www.swap.prathamcms.org/api/UserList?statecode=MH&programid=1*/
    public static final String RI = "Read India";
    public static final String RIpullVillagesURL = "http://www.readindia.openiscool.org/api/village/get?programId=2&state=";
    public static final String RIpullGroupsURL = "http://www.devtab.openiscool.org/api/Group?programid=2&villageId=";
    public static final String RIpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfov2?programid=2&villageId=";
    //    public static final String RIpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfo?programid=2&villageId=";
//    public static final String RIpullStudentsURL = "http://www.devtab.openiscool.org/api/student?programid=2&villageId=";
    public static final String RIpullCrlsURL = "http://www.readindia.openiscool.org/api/crl/get?programId=2";
    public static final String RIpullAserURL = "http://devtab.openiscool.org/api/AserVillage?programid=2&villageid=";


    public static final String SC = "Second Chance";
    public static final String SCpullVillagesURL = "http://www.hlearning.openiscool.org/api/village/get?programId=3&state=";
    public static final String SCpullGroupsURL = "http://www.devtab.openiscool.org/api/Group?programid=3&villageId=";
    public static final String SCpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfov2?programid=3&villageId=";
    //    public static final String SCpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfo?programid=3&villageId=";
//    public static final String SCpullStudentsURL = "http://www.devtab.openiscool.org/api/student?programid=3&villageId=";
    // public static final String SCpullCrlsURL = "http://www.hlearning.openiscool.org/api/crl/get?programId=3";
    public static final String SCpullCrlsURL = "http://www.swap.prathamcms.org/api/UserList?programId=3&statecode=";
    public static final String SCpullAserURL = "http://devtab.openiscool.org/api/AserVillage?programid=3&villageid=";


    public static final String PI = "Pratham Institute";
    public static final String PIpullVillagesURL = "http://www.tabdata.prathaminstitute.org/api/village/get?programId=4&state=";
    public static final String PIpullGroupsURL = "http://www.devtab.openiscool.org/api/Group?programid=4&villageId=";
    public static final String PIpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfov2?programid=4&villageId=";
    //    public static final String PIpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfo?programid=4&villageId=";
//    public static final String PIpullStudentsURL = "http://www.devtab.openiscool.org/api/student?programid=4&villageId=";
    public static final String PIpullCrlsURL = "http://www.tabdata.prathaminstitute.org/api/crl/get?programId=4";
    public static final String PIpullAserURL = "http://devtab.openiscool.org/api/AserVillage?programid=4&villageid=";

    public static final String UP = "Urban programme";
    public static final String UPpullVillagesURL = "http://www.hlearning.openiscool.org/api/village/get?programId=6&state=";
    public static final String UPpullGroupsURL = "http://www.devtab.openiscool.org/api/Group?programid=6&villageId=";
    public static final String UPpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfov2?programid=6&villageId=";
    //    public static final String UPpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfo?programid=6&villageId=";
//    public static final String UPpullStudentsURL = "http://www.devtab.openiscool.org/api/student?programid=6&villageId=";
    public static final String UPpullCrlsURL = "http://www.swap.prathamcms.org/api/UserList?programId=6&statecode=";
    public static final String UPpullAserURL = "http://devtab.openiscool.org/api/AserVillage?programid=6&villageid=";

    public static final String HG = "Hamara Gaon";
    public static final String HGpullVillagesURL = "http://www.hlearning.openiscool.org/api/village/get?programId=13&state=";
    public static final String HGpullGroupsURL = "http://www.devtab.openiscool.org/api/Group?programid=13&villageId=";
    public static final String HGpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfov2?programid=13&villageId=";
    //    public static final String HGpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfo?programid=13&villageId=";
//    public static final String HGpullStudentsURL = "http://www.devtab.openiscool.org/api/student?programid=13&villageId=";
    public static final String HGpullCrlsURL = "http://www.swap.prathamcms.org/api/UserList?programId=13&statecode=";
    public static final String HGpullAserURL = "http://devtab.openiscool.org/api/AserVillage?programid=13&villageid=";


    public static final String GP = "Government Partnership";
    public static final String GPpullVillagesURL = "http://www.hlearning.openiscool.org/api/village/get?programId=14&state=";
    public static final String GPpullGroupsURL = "http://www.devtab.openiscool.org/api/Group?programid=14&villageId=";
    public static final String GPpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfov2?programid=14&villageId=";
    //    public static final String GPpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfo?programid=14&villageId=";
//    public static final String GPpullStudentsURL = "http://www.devtab.openiscool.org/api/student?programid=14&villageId=";
    public static final String GPpullCrlsURL = "http://www.swap.prathamcms.org/api/UserList?programId=14&statecode=";
    public static final String GPpullAserURL = "http://devtab.openiscool.org/api/AserVillage?programid=14&villageid=";


    //DigitalSchoolProgram
    public static final String DSP = "Digital School Program";
    public static final String DSPpullVillagesURL = "http://www.hlearning.openiscool.org/api/village/get?programId=22&state=";
    public static final String DSPpullGroupsURL = "http://www.devtab.openiscool.org/api/Group?programid=22&villageId=";
    public static final String DSPpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfov2?programid=22&villageId=";
    public static final String DSPpullCrlsURL = "http://www.swap.prathamcms.org/api/UserList?programId=22&statecode=";
    public static final String DSPpullAserURL = "http://devtab.openiscool.org/api/AserVillage?programid=22&villageid=";

    //Read India- Michelin
    public static final String RIM = "Read India- Michelin";
    public static final String RIMpullVillagesURL = "http://www.hlearning.openiscool.org/api/village/get?programId=11&state=";
    public static final String RIMpullGroupsURL = "http://www.devtab.openiscool.org/api/Group?programid=11&villageId=";
    public static final String RIMpullStudentsURL = "http://www.devtab.openiscool.org/api/studentinfov2?programid=11&villageId=";
    public static final String RIMpullCrlsURL = "http://www.swap.prathamcms.org/api/UserList?programId=11&statecode=";
    public static final String RIMpullAserURL = "http://devtab.openiscool.org/api/AserVillage?programid=11&villageid=";

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


    // Assign/ Return Admin App API
//    public static final String AssignReturn = "http://swap.prathamcms.org/api/AssignReturn/pushdata";
    public static final String AssignReturn = "http://www.swap.prathamcms.org/api/AssignReturnV2/PushData";

    // Replace Admin App API
//    public static final String ReplaceTab = "http://swap.prathamcms.org/api/ReplaceCollect/PushReplaceCollect";
    public static final String ReplaceTab = "http://www.swap.prathamcms.org/api/CollectReplaceV2/PushData";


    // Replace Admin App API
    public static final String GetCollectedTabList = "http://swap.prathamcms.org/api/ReplaceCollect/get?userid=";

    // Device List API
    public static final String DeviceList = "http://swap.prathamcms.org/api/tablist?userid=";

    //Store person AOI
    public static final String storePersonAPI = "http://www.swap.prathamcms.org/api/Vendor?programId=17&statecode=";

    //Notification API
    public static final String notificationAPI = " http://swap.prathamcms.org/api/tabletnotification/get?userid=";
}
