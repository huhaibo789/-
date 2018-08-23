package com.example.chengduhome;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zxing.qrcode.CaptureActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import adapter.unfinishadapter;
import presenter.MainPresenter;
import presenter.MainPresenterImpl;
import util.Api;
import util.Constant;
import view.Mainview;

public class UnfinishActivity extends AppCompatActivity implements Mainview {
    private CoordinatorLayout right;
    private NavigationView left;
    private boolean isDrawer = false;
    private ImageView showiv;
    private Toolbar toolbar;
    private PullToRefreshListView list_info;
    private TextView hometv;
    private DrawerLayout drawer;
    int currentpage = 1;//当前页数
    private MainPresenter mMainPresenter;
    private Button wanxia_tv, wanxia_button;
    private RequestQueue queue;
    JSONArray jsonarry;
    private unfinishadapter unfinish;
    private boolean falg;
    private Handler handler = new Handler();
    String unwork="1";
    public  String capture;
    private ProgressDialog progressDialog;
    private ArrayList<String> username = new ArrayList<>();//姓名
    private ArrayList<String> usersex = new ArrayList<>();//性别
    private ArrayList<String> usertime = new ArrayList<>();//开始时间
    private ArrayList<String> userwork = new ArrayList<>(); //任务的状态
    private ArrayList<String> userresgiter = new ArrayList<>(); //注册id
    private ArrayList<String>  card = new ArrayList<>(); //老人身份
    String result;
    String isflag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }*/
        setContentView(R.layout.activity_unfinish);
        progressDialog=new ProgressDialog(UnfinishActivity.this);
        progressDialog.setMessage("获取数据中...");
        initview();
        getVolley();
        inittoolbar();
        enableLoadMore();
        setPulltoRefreshStyle();
        setlistener();
        //StatusBarCompat.setStatusBarColor(UnfinishActivity.this, Color.parseColor("#000000"), false); //改变状态栏颜色
    }

    private void setlistener() {
        wanxia_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wanxia_button.setBackgroundColor(Color.parseColor("#e6bd0b"));
                wanxia_tv.setBackgroundDrawable(null);
                currentpage = 1;
                unwork = "2";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postshujuDown();
                        progressDialog.show();
                        //progressDialog = ProgressDialog.show(UnfinishActivity.this, "请稍等...", "获取数据中...", true);//显示加载框
                    }
                });
            }
        });
        wanxia_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wanxia_tv.setBackgroundColor(Color.parseColor("#e6bd0b"));
                wanxia_button.setBackgroundDrawable(null);
                currentpage = 1;
                unwork = "1";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postshujuDown();
                        progressDialog=new ProgressDialog(UnfinishActivity.this);
                        progressDialog.setMessage("获取数据中...");
                        progressDialog.show();
                        //progressDialog = ProgressDialog.show(UnfinishActivity.this, "请稍等...", "获取数据中...", true);//显示加载框
                    }
                });
            }
        });
        showiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capture = "true";
               startActivityForResult(new Intent(UnfinishActivity.this, CaptureActivity.class), 0);
            }
        });
        list_info.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            //下拉
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                currentpage = 1;
                postshujuDown();
            }
            //上拉
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                currentpage = currentpage + 1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postshuju1Up();
                    }
                });
            }
        });
    }
    private void postshuju1Up() {
        queue = Volley.newRequestQueue(UnfinishActivity.this);
        String url = Api.itemScan;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            String login = jsonObject.getString("flag");
                            if (login.equals("1")) {
                                jsonarry = jsonObject.getJSONArray("op");
                                if (jsonarry.length() == 0 && currentpage == 1) {
                                    Toast.makeText(UnfinishActivity.this, "没有任务信息", Toast.LENGTH_SHORT).show();
                                } else if(jsonarry.length()==0&&currentpage!=1){
                                    Toast.makeText(UnfinishActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                                    list_info.onRefreshComplete();
                                }else {
                                    for (int i = 0; i < jsonarry.length(); i++) {
                                        JSONObject object = jsonarry.getJSONObject(i);
                                        String usename = object.getString("name");
                                        username.add(usename);
                                        String user_sex = object.getString("sex");
                                        usersex.add(user_sex);
                                        String userres = object.getString("registerid");
                                        userresgiter.add(userres);
                                        String user_time = object.getString("nursetime");
                                        usertime.add(user_time);
                                        String user_work = object.getString("status");
                                        userwork.add(user_work);
                                    }
                                    if (unfinish != null) {
                                        unfinish.notifyDataSetChanged();
                                        list_info.onRefreshComplete();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UnfinishActivity.this, "服务器出错了", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if(unwork.equals("2")){
                    map.put("status","1");
                }else {
                    map.put("status","0");
                }
                map.put("offset", String.valueOf(currentpage));
                map.put("length", "10");
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
        username.clear();
        usersex.clear();
        userwork.clear();
        usertime.clear();
        userresgiter.clear();
        queue = Volley.newRequestQueue(UnfinishActivity.this);
        String url = Api.itemScan;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        if(str!=null){
                            try {
                                JSONObject jsonObject = new JSONObject(str);
                                String login = jsonObject.getString("flag");
                                if (login.equals("1")) {
                                    jsonarry = jsonObject.getJSONArray("op");
                                    if (jsonarry.length() == 0 && currentpage == 1) {
                                        Toast.makeText(UnfinishActivity.this, "没有任务信息", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();//去掉加载框
                                        hometv.setVisibility(View.VISIBLE);
                                        list_info.onRefreshComplete();
                                    } else if(jsonarry.length()==0&&currentpage!=1){
                                        hometv.setVisibility(View.GONE);
                                        Toast.makeText(UnfinishActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                                        list_info.onRefreshComplete();
                                    } else {
                                        hometv.setVisibility(View.GONE);
                                        for (int i = 0; i < jsonarry.length(); i++) {
                                            JSONObject object = jsonarry.getJSONObject(i);
                                            String usename = object.getString("name");
                                            username.add(usename);
                                            String userres = object.getString("registerid");
                                            userresgiter.add(userres);
                                            String user_sex = object.getString("sex");
                                            usersex.add(user_sex);
                                            String user_time = object.getString("nursetime");
                                            usertime.add(user_time);
                                            String user_work = object.getString("status");
                                            userwork.add(user_work);
                                        }
                                        if(unfinish!=null){
                                            unfinish.notifyDataSetChanged();
                                            list_info.onRefreshComplete();
                                        }else {
                                            setadapter();
                                            list_info.onRefreshComplete();
                                        }
                                        progressDialog.dismiss();//去掉加载框

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
                        Toast.makeText(UnfinishActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                        Log.i("dsda", "onErrorResponse: " + error.getMessage());
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if(unwork.equals("2")){
                    map.put("status","1");  //完成
                }else {
                    map.put("status","0"); //未完成
                }
                map.put("offset", String.valueOf(currentpage));
                map.put("length", "10");
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
    private void getVolley() {
        queue = Volley.newRequestQueue(UnfinishActivity.this);
        String url = Api.itemScan;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        Log.i("igggeorro", "onResponse: "+str);
                        if(str!=null){
                            try {
                                JSONObject jsonObject = new JSONObject(str);
                                String login = jsonObject.getString("flag");
                                if (login.equals("1")) {
                                    jsonarry = jsonObject.getJSONArray("op");
                                    if (jsonarry.length() == 0 && currentpage == 1){
                                        Toast.makeText(UnfinishActivity.this, "没有任务信息", Toast.LENGTH_SHORT).show();
                                  /*  Log.i("zheshi", "onResponse: "+"1");
                                    shoptu_iv.setVisibility(View.VISIBLE);
                                    tishi_newsou.setVisibility(View.VISIBLE);
                                    tishi_newsou.setText("换个条件查询试试");
                                    list_worker.setMode(PullToRefreshBase.Mode.DISABLED);*/
                                    }else {
                                        list_info.setMode(PullToRefreshBase.Mode.BOTH);
                                        for (int i = 0; i < jsonarry.length(); i++) {
                                            JSONObject object = jsonarry.getJSONObject(i);
                                            String usename = object.getString("name");
                                            username.add(usename);
                                            String cardid=object.getString("cardno");
                                            card.add(cardid);
                                            String userres = object.getString("registerid");
                                            userresgiter.add(userres);
                                            String user_sex = object.getString("sex");
                                            usersex.add(user_sex);
                                            String user_time = object.getString("nursetime");
                                            usertime.add(user_time);
                                            String user_work = object.getString("status");
                                            userwork.add(user_work);
                                        }

                                        if (jsonarry.length() != 0) {
                                            setadapter();
                                        }
                                    }
                                } else {
                                    Toast.makeText(UnfinishActivity.this, "未登录", Toast.LENGTH_SHORT).show();
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
                        Log.i("dsda", "onErrorResponse: " + error.getMessage());
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("offset",String.valueOf(currentpage));
                map.put("status", "0"); //未完成
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
    private void setadapter() {
        unfinish = new unfinishadapter(UnfinishActivity.this, username, usersex, usertime, userwork, userresgiter,card);
        list_info.setAdapter(unfinish);
    }
    private void setPulltoRefreshStyle() {
        ILoadingLayout il = list_info.getLoadingLayoutProxy();
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

    private void enableLoadMore() {
        list_info.setMode(PullToRefreshBase.Mode.BOTH);
    }

    private void inittoolbar() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        /*Intent intent=getIntent();
        isflag=intent.getStringExtra("isflag");
        if(isflag!=null&&isflag.equals("1")){
            MenuItem menuItem1 = navigationView.getMenu().findItem(R.id.nav_help);
            menuItem1.setVisible(false);    // true 为显示，false 为隐藏
        }*/
        setupDrawerContent(navigationView);
        mMainPresenter = new MainPresenterImpl(UnfinishActivity.this);
    }

    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // menuItem.setVisible(true);
                        Log.i("fkvfffg", "onNavigationItemSelected: "+menuItem);
                        Log.i("fkvfffg1", "onNavigationItemSelected: "+menuItem.getItemId());

                        mMainPresenter.switchNavigation(menuItem.getItemId());
                        menuItem.setChecked(true);
                        drawer.closeDrawers();
                        return true;
                    }
                });
    }
    private void initview() {
        hometv= (TextView) findViewById(R.id.home_tv);
        list_info = (PullToRefreshListView) findViewById(R.id.list_info);
        wanxia_tv = (Button) findViewById(R.id.wanxia_tv);
        wanxia_button = (Button) findViewById(R.id.wanxia_button);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        showiv = (ImageView) findViewById(R.id.show_iv);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        right = (CoordinatorLayout) findViewById(R.id.right);
        left = (NavigationView) findViewById(R.id.nav_view);
        //   setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);//去掉toolbar标题
    }

    @Override
    public void switch2News() {
        Intent intent = new Intent(UnfinishActivity.this, InformationActivity.class);
        startActivity(intent);

    }

    @Override
    public void switch2Images() {
        // 建造者模式，将对象初始化的步骤抽取出来，让建造者来实现，设置完所有属性之后再创建对象
        // 这里使用的Context必须是Activity对象
        AlertDialog.Builder builder = new AlertDialog.Builder(UnfinishActivity.this);
        // 链式编程
        builder.setTitle("提示")
                .setMessage("拨打电话急救")
                .setPositiveButton("立即拨打", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gettelphone();
                    }
                })
                .setNegativeButton("查找老人急救", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(UnfinishActivity.this,HelpermessageActivity.class);
                        startActivity(intent);
                    }
                })
                .setCancelable(false); // 能否通过点击对话框以外的区域（包括返回键）关闭对话框
        // 通过建造者创建Dialog对象
        // AlertDialog dialog = builder.create();
        // dialog.show();
        // 以上两行代码可以简化成以下这一行代码
        builder.show();
    }
    private void gettelphone() {
        queue = Volley.newRequestQueue(UnfinishActivity.this);
        String url = Api.callphone;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        try {
                            JSONObject json=new JSONObject(str);
                            String ss=json.getString("tel");
                            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ss));
                            if (ActivityCompat.checkSelfPermission(UnfinishActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            startActivity(intent1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UnfinishActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
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
    public void switch2Weather() {
        finish();
    }

    @Override
    public void switch2About() {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 8) {
            result = data.getStringExtra("result");
            if (result == null) return;
            String[] results = result.split(",");
            if (results.length != 2) {
                Toast.makeText(this, "老人卡信息有误", Toast.LENGTH_SHORT).show();
                return;
            }
            if(capture.equals("true")){
                //快速扫码
                Intent inint=new Intent(UnfinishActivity.this,FinishActivity.class);
                inint.putExtra("quickscan",results[1]);
                startActivity(inint);
            }else {
                //列表扫码
                unfinish.setnotify(results[1]);
            }

        }
    }
}
