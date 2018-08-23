package util;

/**
 * Created by Administrator on 2017/8/21.
 */

public class Api {
    public static String BASEURL = "http://123.56.168.104:1999";//基础url
    public static String HOMEURL = BASEURL + "/WXYL/homeLogin.jsp";//主页
    public static String UPLOADLOCATOIN = "http://123.56.168.104:1999/JJXXGLXT/android.do?op=realtime";//上传定位
    public static String LOGINURL ="http://123.56.168.104:1999/JJXXGLXT/androidTest/androidlogin.do";//主页登陆
    //public static String LOGINURL ="http://123.56.168.104:1999/WXYL/android.do?op=androidlogin";//主页登陆
    public static String WORKER ="http://123.56.168.104:1999/JJXXGLXT/androidTest/selectRegisterByHomeregisteruser.do";//人员列表
    public static String SCANLOCATOIN ="http://123.56.168.104:1999/WXYL/android.do?op=mapApi";//护工上传定位
    public static String REGESTIGER="http://123.56.168.104:1999/JJXXGLXT/androidTest/RegisterRewu.do";//快速扫码任务
    public static String  FINISH  ="http://123.56.168.104:1999/JJXXGLXT/androidTest/homePhoneUserDealNurse.do";//列表扫码
    public static String itemScan="http://123.56.168.104:1999/JJXXGLXT/androidTest/Wstart.do";//完成未完成
    public static String WRONGSCAN="http://123.56.168.104:1999/JJXXGLXT/androidTest/savegsaoma.do";//异常任务
    public static String saveFinish="http://123.56.168.104:1999/JJXXGLXT/androidTest/saveHomeRegnurse.do" ;//保存任务
    public static String  emergency ="http://123.56.168.104:1999/JJXXGLXT/androidTest/jijiurenwu.do";//急救任务
    public static String  passemergency="http://123.56.168.104:1999/JJXXGLXT/androidTest/fuwuneirong.do";//历史任务
    public static String  savepassemergency="http://123.56.168.104:1999/JJXXGLXT/androidTest/savefuwuneirong.do";//保存单个输入框内容.
    public static String  miszhangemergency="http://123.56.168.104:1999/JJXXGLXT/androidTest/zhiyuanzhesaverenwupictures.do";//保存图片内容
    public static String  uploadedit ="http://123.56.168.104:1999/JJXXGLXT/Pictures/save.do";//上传评价
    public static String callphone="http://123.56.168.104:1999/JJXXGLXT/androidTest/hugongjijiudianhua.do";//打电话报警
    //public static String LOGINURL ="http://192.168.0.122:8080/WXYL/android.do?op=androidlogin";
    public static String loadname="http://123.56.168.104:1999/JJXXGLXT/androidTest/registerWstart.do";//搜索
    public static String  hugonghelp ="http://123.56.168.104:1999/JJXXGLXT/androidTest/jijiutupian.do";//护工上传评价
    public static String  refusetask ="http://123.56.168.104:1999/JJXXGLXT/androidJjbj/zyzWcjj.do";//护工拒绝任务
    public static String  lishineirong ="http://123.56.168.104:1999/JJXXGLXT/androidTest/lishirenwu.do";//查看历史任务
}
