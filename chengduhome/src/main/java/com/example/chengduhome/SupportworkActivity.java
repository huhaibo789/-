package com.example.chengduhome;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.githang.statusbar.StatusBarCompat;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zxing.qrcode.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import adapter.workeradapter;
import util.Api;
import util.Constant;
import util.FileUtils;

public class SupportworkActivity extends AppCompatActivity implements PullToRefreshBase.OnRefreshListener2, View.OnClickListener{
    private double Longitude;
    private double Latitude;
    private RequestQueue queue;
    private PullToRefreshListView list_worker;
    private workeradapter worker;
    private RelativeLayout ok_rl,return_back;
    private ImageView shoptu_iv,qiuck_scan;
    private TextView tishi_newsou;
    private Handler handler = new Handler();
    int currentpage=1;//当前页数
    private ArrayList<String> username=new ArrayList<>();//姓名
    private ArrayList<String> usersex=new ArrayList<>();//性别
    private ArrayList<String> usertime=new ArrayList<>();//开始时间
    private ArrayList<String> userwork=new ArrayList<>(); //任务的状态
    private ArrayList<String> userresgiter=new ArrayList<>(); //注册id
    private ArrayList<String> usercardno=new ArrayList<>(); //身份信息
    JSONArray jsonarry;
    private  String singlesign="no";
    public  String scan;
    String user_name,connectphone,appoinment,personinformation,borntime,oldercard,gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supportwork);
        queue = Volley.newRequestQueue(this);
        getdata();
        initview();
        getVolley();
        enableLoadMore();
        setPulltoRefreshStyle();
        //setlistener();
        StatusBarCompat.setStatusBarColor(SupportworkActivity.this, Color.parseColor("#05e65b"), false);
    }
    private void getdata() {
        Intent intent=getIntent();
        user_name=intent.getStringExtra("editname"); //用户姓名
        appoinment=intent.getStringExtra("appoinment");//预约时间
        borntime=intent.getStringExtra("borntime"); //出生时间
        oldercard=intent.getStringExtra("oldercard");//老人卡
        gender=intent.getStringExtra("gender");//性别
    }
    private void setadapter() {
        worker = new workeradapter(SupportworkActivity.this, username,usersex, usertime, userwork,userresgiter,usercardno);
        list_worker.setAdapter(worker);
    }
  /*  private void setlistener() {
        list_worker.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            //下拉
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                currentpage = 1;
                postshujuDown();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setadapter();
                        list_worker.onRefreshComplete();
                    }
                }, 500);
            }
            //上拉
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                currentpage = currentpage + 1;
                postshuju1Up();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(worker!=null){
                            worker.notifyDataSetChanged();
                        }
                        if (falg == true) {
                            Toast.makeText(SupportworkActivity.this, "无更多数据", Toast.LENGTH_SHORT).show();
                        }
                        list_worker.onRefreshComplete();
                    }
                }, 500);
            }
        });
    }*/

    private void postshuju1Up() {
        tishi_newsou.setVisibility(View.GONE);
        shoptu_iv.setVisibility(View.GONE);
        queue = Volley.newRequestQueue(SupportworkActivity.this);
        String url= Api.WORKER;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str=response.toString().trim();
                        if(str!=null){
                            try {
                                JSONObject jsonObject = new JSONObject(str);
                                String login=jsonObject.getString("flag");
                                if(login.equals("1")){
                                    jsonarry=jsonObject.getJSONArray("op");
                                    if(jsonarry.length()==0&&currentpage==1){
                                        shoptu_iv.setVisibility(View.VISIBLE);
                                        tishi_newsou.setVisibility(View.VISIBLE);
                                        tishi_newsou.setText("换个条件查询试试");
                                    }else if(jsonarry.length()==0&&currentpage!=1){
                                        Toast.makeText(SupportworkActivity.this, "无更多数据", Toast.LENGTH_SHORT).show();
                                        list_worker.onRefreshComplete();
                                    }else{
                                        for (int i = 0; i <jsonarry.length() ; i++) {
                                            JSONObject object = jsonarry.getJSONObject(i);
                                            String usename=object.getString("name");
                                            username.add(usename);
                                            String user_sex=object.getString("sex");
                                            usersex.add(user_sex);
                                            String userres=object.getString("registerid");
                                            userresgiter.add(userres);
                                            String user_time=object.getString("nursetime");
                                            usertime.add(user_time);
                                            String user_card=object.getString("cardno");
                                            usercardno.add(user_card);
                                            String user_work=object.getString("status");
                                            userwork.add(user_work);
                                        }
                                        if(worker!=null){
                                            worker.notifyDataSetChanged();
                                            list_worker.onRefreshComplete();
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tishi_newsou.setVisibility(View.VISIBLE);
                        tishi_newsou.setText("服务器出错了。。。");
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                FileUtils files=new FileUtils();
                String file_userid=files.readDataFromFile(SupportworkActivity.this);
                if(file_userid!=null){
                    map.put("userid",file_userid);  //用户id
                }else {
                    map.put("userid","");
                }
                if(user_name!=null){
                    try {
                        String str = new String(user_name.getBytes("utf-8"), "ISO-8859-1");
                        map.put("name",str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }else {                           //姓名
                    map.put("name","");
                }
                if(connectphone!=null){
                    map.put("phone",connectphone);  //手机
                }else {
                    map.put("phone","");
                }
                if(gender!=null&&gender.equals("no")){
                    map.put("sex","");
                }else if(gender!=null&&gender.equals("boy")){    //性别
                    String sex="男";
                    try {
                        String str = new String(sex.getBytes("utf-8"), "ISO-8859-1");
                        map.put("sex",str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else if(gender!=null&&gender.equals("girl")){
                    String sex="女";
                    try {
                        String str = new String(sex.getBytes("utf-8"), "ISO-8859-1");
                        map.put("sex",str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
                if(appoinment!=null){
                    map.put("Yytime",appoinment); //预约的时间
                }else {
                    map.put("Yytime","");
                }
                if(personinformation!=null){
                    map.put("cardno",personinformation); //省份证
                }else {
                    map.put("cardno","");
                }
                if(oldercard!=null){
                    map.put("lnzh",oldercard);  //老年证号
                }else {
                    map.put("lnzh","");
                }
                if(borntime!=null){
                    map.put("cstime",borntime);
                }else {
                    map.put("cstime","");   //出生日期
                }
                map.put("offset",String.valueOf(currentpage));
                map.put("length","10");
                return map;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (Constant.localCookie != null && Constant.localCookie.length() > 0) {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("cookie", Constant.localCookie);
                    Log.d("调试88", "headers----------------" + headers);
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };
        queue.add(post);
    }
    private void postshujuDown() {
        shoptu_iv.setVisibility(View.GONE);
        tishi_newsou.setVisibility(View.GONE);
        username.clear();
        usersex.clear();
        userwork.clear();
        usertime.clear();
        userresgiter.clear();
        usercardno.clear();
        queue = Volley.newRequestQueue(SupportworkActivity.this);
        String url=Api.WORKER;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str=response.toString().trim();
                        if(str!=null){
                            try {
                                JSONObject jsonObject = new JSONObject(str);
                                String login=jsonObject.getString("flag");
                                if(login.equals("1")){
                                    jsonarry=jsonObject.getJSONArray("op");
                                    if(jsonarry.length()==0&&currentpage==1){
                                        shoptu_iv.setVisibility(View.VISIBLE);
                                        tishi_newsou.setVisibility(View.VISIBLE);
                                        tishi_newsou.setText("换个条件查询试试");
                                    }else {
                                        for (int i = 0; i <jsonarry.length() ; i++) {
                                            JSONObject object = jsonarry.getJSONObject(i);
                                            String usename=object.getString("name");
                                            username.add(usename);
                                            String userres = object.getString("registerid");
                                            userresgiter.add(userres);
                                            String user_sex=object.getString("sex");
                                            usersex.add(user_sex);
                                            String user_time=object.getString("nursetime");
                                            usertime.add(user_time);
                                            String user_card=object.getString("cardno");
                                            usercardno.add(user_card);
                                            String user_work=object.getString("status");
                                            userwork.add(user_work);
                                        }
                                        setadapter();
                                        list_worker.onRefreshComplete();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tishi_newsou.setVisibility(View.VISIBLE);
                        tishi_newsou.setText("服务器出错了。。。");
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                FileUtils files=new FileUtils();
                String file_userid=files.readDataFromFile(SupportworkActivity.this);
                if(file_userid!=null){
                    map.put("userid",file_userid);  //用户id
                }else {
                    map.put("userid","");
                }
                if(user_name!=null){
                    try {
                        String str = new String(user_name.getBytes("utf-8"), "ISO-8859-1");
                        map.put("name",str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else {                           //姓名
                    map.put("name","");
                }
                if(connectphone!=null){
                    map.put("phone",connectphone);  //手机
                }else {
                    map.put("phone","");
                }
                if(gender!=null&&gender.equals("no")){
                    map.put("sex","");
                }else if(gender!=null&&gender.equals("boy")){    //性别
                    String sex="男";
                    try {
                        String str = new String(sex.getBytes("utf-8"), "ISO-8859-1");
                        map.put("sex",str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else if(gender!=null&&gender.equals("girl")){
                    String sex="女";
                    try {
                        String str = new String(sex.getBytes("utf-8"), "ISO-8859-1");
                        map.put("sex",str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                if(appoinment!=null){
                    map.put("Yytime",appoinment); //预约的时间
                }else {
                    map.put("Yytime","");
                }
                if(personinformation!=null){
                    map.put("cardno",personinformation); //省份证
                }else {
                    map.put("cardno","");
                }
                if(oldercard!=null){
                    map.put("lnzh",oldercard);  //老年证号
                }else {
                    map.put("lnzh","");
                }
                if(borntime!=null){
                    map.put("cstime",borntime);
                }else {
                    map.put("cstime","");   //出生日期
                }
                map.put("offset",String.valueOf(currentpage));
                map.put("length","10");
                Log.i("fvnffr", "getParams: "+map);
                return map;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (Constant.localCookie != null && Constant.localCookie.length() > 0) {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("cookie", Constant.localCookie);
                    Log.d("调试88", "headers----------------" + headers);
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };
        queue.add(post);
    }
    private void setPulltoRefreshStyle() {
        ILoadingLayout il =list_worker.getLoadingLayoutProxy();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = format1.format(curDate);
        il.setLastUpdatedLabel("最近更新：" + str);
        //  il.setLoadingDrawable(getResources().getDrawable(R.mipmap.ic_pulltorefresh_arrow));
        //设置下拉状态时的提示文字
        il.setPullLabel("下拉刷新");
        //设置正在刷新过程中的提示文字
        il.setRefreshingLabel("正在刷新");
        //设置松手提示文字
        il.setReleaseLabel("松开刷新");
    }
    private void initview() {
        return_back= (RelativeLayout) findViewById(R.id.return_back);
        qiuck_scan= (ImageView) findViewById(R.id.qiuck_scan);
        ok_rl= (RelativeLayout) findViewById(R.id.ok_rl);
        list_worker= (PullToRefreshListView) findViewById(R.id.list_worker);
        shoptu_iv= (ImageView) findViewById(R.id.shoptu_iv);
        tishi_newsou= (TextView) findViewById(R.id.tishi_newsou);
        usertime.clear();
        username.clear();
        usersex.clear();
        userwork.clear();
        userresgiter.clear();
        usercardno.clear();
        return_back.setOnClickListener(this);
        qiuck_scan.setOnClickListener(this);
        list_worker.setOnRefreshListener(this);
    }
    private void enableLoadMore() {
        list_worker.setMode(PullToRefreshBase.Mode.BOTH);
    }
    private void getVolley() {
        queue = Volley.newRequestQueue(SupportworkActivity.this);
        String url= Api.WORKER;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str=response.toString().trim();
                        Log.i("您们", "onResponse: "+str);
                        if(str!=null){
                            try {
                                JSONObject jsonObject = new JSONObject(str);
                                String login=jsonObject.getString("flag");
                                if(login.equals("1")){
                                    jsonarry=jsonObject.getJSONArray("op");
                                    if(jsonarry.length()==0&&currentpage==1){
                                        Log.i("zheshi", "onResponse: "+"1");
                                        shoptu_iv.setVisibility(View.VISIBLE);
                                        tishi_newsou.setVisibility(View.VISIBLE);
                                        tishi_newsou.setText("换个条件查询试试");
                                        list_worker.setMode(PullToRefreshBase.Mode.DISABLED);
                                    }else {
                                        list_worker.setMode(PullToRefreshBase.Mode.BOTH);
                                        for (int i = 0; i <jsonarry.length() ; i++) {
                                            JSONObject object = jsonarry.getJSONObject(i);
                                            String usename=object.getString("name");
                                            username.add(usename);
                                            String userres=object.getString("registerid");
                                            userresgiter.add(userres);
                                            String user_sex=object.getString("sex");
                                            usersex.add(user_sex);
                                            String user_time=object.getString("nursetime");
                                            usertime.add(user_time);
                                            String user_card=object.getString("cardno");
                                            usercardno.add(user_card);
                                            String user_work=object.getString("status");
                                            userwork.add(user_work);
                                        }
                                        if (jsonarry.length() != 0) {
                                            setadapter();
                                        }
                                    }
                                }else {
                                    Toast.makeText(SupportworkActivity.this, "未登录", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tishi_newsou.setVisibility(View.VISIBLE);
                        tishi_newsou.setText("服务器出错了。。。");
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                FileUtils files=new FileUtils();
                String file_userid=files.readDataFromFile(SupportworkActivity.this);
               if(file_userid!=null){
                    map.put("userid",file_userid);  //用户id
                }else {
                    map.put("userid","");
                }
              //  map.put("userid","8720171020041032");
                if(user_name!=null){
                    try {
                        String str = new String(user_name.getBytes("utf-8"), "ISO-8859-1");
                        map.put("name",str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else {                           //姓名
                    map.put("name","");
                }
                map.put("phone","");
                if(gender!=null&&gender.equals("no")){
                    map.put("sex","");
                }else if(gender!=null&&gender.equals("boy")){    //性别
                    String sex="男";
                    try {
                        String str = new String(sex.getBytes("utf-8"), "ISO-8859-1");
                        map.put("sex",str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }else if(gender!=null&&gender.equals("girl")){
                    String sex="女";
                    try {
                        String str = new String(sex.getBytes("utf-8"), "ISO-8859-1");
                        map.put("sex",str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                if(appoinment!=null){
                    map.put("Yytime",appoinment); //预约的时间
                }else {
                    map.put("Yytime","");
                }

                map.put("cardno","");

                if(oldercard!=null){
                    map.put("lnzh",oldercard);  //老年证号
                }else {
                    map.put("lnzh","");
                }
                if(borntime!=null){
                    map.put("cstime",borntime);
                }else {
                    map.put("cstime","");   //出生日期
                }
                map.put("offset",String.valueOf(currentpage));
                map.put("length","10");
                Log.i("hahsasdd", "getParams: "+map);
                return map;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (Constant.localCookie != null && Constant.localCookie.length() > 0) {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("cookie", Constant.localCookie);
                    Log.d("调试88", "headers----------------" + headers);
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };
        queue.add(post);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 8) {
            //String result = data.getStringExtra("result");
            String result = data.getExtras().getString("result");
            Log.i("dfnrfrr", "onActivityResult: "+result);
            if (result == null) return;
            String[] results = result.split(",");
            if (results.length != 2) {
                Toast.makeText(this, "老人卡信息有误", Toast.LENGTH_SHORT).show();
                return;
            }
            if(scan.equals("true")){
                //快速扫码任务
                Intent inint=new Intent(SupportworkActivity.this,FinishActivity.class);
                inint.putExtra("quickscan",results[1]);
                startActivity(inint);
            }else {
                //列表扫码任务
               worker.setnotify(results[1]);
            }

         /*   String jsFunction = MessageFormat.format("javascript: showMsg({0},{1})", '"' + results[0] + '"', '"' + results[1] + '"');
            contentWebView.loadUrl(jsFunction);*/
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.return_back:
                Log.i("jimo", "onClick: "+"dss");
                finish();
                break;
            case R.id. qiuck_scan:
                //快速扫码任务

                scan="true";
                startActivityForResult(new Intent(this, CaptureActivity.class), 0);
                break;
            default:
                break;
        }
    }
    //下拉
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        currentpage = 1;
        postshujuDown();

    }
    //上拉
    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        currentpage = currentpage + 1;
        postshuju1Up();

    }

}
