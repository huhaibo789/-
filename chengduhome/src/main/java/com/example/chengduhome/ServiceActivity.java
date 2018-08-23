package com.example.chengduhome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import adapter.serviceadapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import util.Api;
import util.Constant;

public class ServiceActivity extends AppCompatActivity {
    @Bind(R.id.servicelist_info)
    PullToRefreshListView servicelistInfo;
    @Bind(R.id.return_iv)
    ImageView returnIv;
    @Bind(R.id.nomessage_rl)
    RelativeLayout nomessageRl;
    @Bind(R.id.help_tv)
    TextView helpTv;
    private serviceadapter adapter;
    private ArrayList<String> username = new ArrayList<>();//姓名
    private ArrayList<String> usertime = new ArrayList<>();//时间
    private ArrayList<String> usersex = new ArrayList<>();//性别
    private ArrayList<String> userstatus = new ArrayList<>();//任务状态
    private ArrayList<String> resid = new ArrayList<>();//id
    private ArrayList<String> bjid = new ArrayList<>();//报警id
    private ArrayList<String> rwid = new ArrayList<>();//任务id
    private RequestQueue queue;
    int currentpage = 1;//当前页数
    private Handler handler = new Handler();
    JSONArray jsonary;
    private boolean falg;
    String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        ButterKnife.bind(this);
        Intent intent=getIntent();
         user=intent.getStringExtra("user");
        enableLoadMore();
        setPulltoRefreshStyle();
        getvolley();
        setlistener();
    }

    private void setPulltoRefreshStyle() {
        ILoadingLayout il = servicelistInfo.getLoadingLayoutProxy();
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
        servicelistInfo.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
    }

    private void setlistener() {
        helpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ServiceActivity.this,HelpermessageActivity.class);
                startActivity(intent);
            }
        });
        returnIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        servicelistInfo.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                currentpage = 1;
                postshujuDown();
                /*handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        servicelistInfo.onRefreshComplete();
                    }
                }, 500);*/
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                currentpage = currentpage + 1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postshuju1Up();
                        /*if (falg == true) {
                            Toast.makeText(ServiceActivity.this, "无更多数据", Toast.LENGTH_SHORT).show();
                        }
                        servicelistInfo.onRefreshComplete();*/
                    }
                });
            }
        });
    }

    private void postshuju1Up() {
        queue = Volley.newRequestQueue(ServiceActivity.this);
        String url = Api.emergency;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        try {
                            jsonary = new JSONArray(str);
                            if (jsonary.length() == 0 & currentpage == 1) {
                                Toast.makeText(ServiceActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                                servicelistInfo.onRefreshComplete();
                            } else {
                                for (int i = 0; i < jsonary.length(); i++) {
                                    JSONObject jsonob = jsonary.getJSONObject(i);
                                    String name = jsonob.getString("name");
                                    username.add(name);
                                    String sex = jsonob.getString("sex");
                                    usersex.add(sex);
                                    resid.add(jsonob.getString("registerid"));
                                    String status=jsonob.getString("sfwc");
                                    userstatus.add(status);
                                    String servicetime=jsonob.getString("bjsj");
                                    usertime.add(servicetime);
                                   /* JSONObject jsonject = jsonob.getJSONObject("emergency");
                                    String servicetime = jsonject.getString("clsj");
                                    usertime.add(servicetime);
                                    String status = jsonject.getString("sfcl");
                                    userstatus.add(status);*/
                                }
                               if (jsonary.length() == 0) {
                                   Toast.makeText(ServiceActivity.this, "无更多数据", Toast.LENGTH_SHORT).show();
                                   servicelistInfo.onRefreshComplete();
                               }else if(adapter != null) {
                                    adapter.notifyDataSetChanged();
                                   servicelistInfo.onRefreshComplete();
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
                        Toast.makeText(ServiceActivity.this, "服务器出错", Toast.LENGTH_SHORT).show();
                      /*  tishi_newsou.setVisibility(View.VISIBLE);
                        tishi_newsou.setText("服务器出错了。。。");*/
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
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
        usertime.clear();
        usersex.clear();
        userstatus.clear();
        resid.clear();
        queue = Volley.newRequestQueue(ServiceActivity.this);
        String url = Api.emergency;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        Log.i("返回数据", "onResponse: " + str);
                        try {
                            JSONArray jsonary = new JSONArray(str);
                            for (int i = 0; i < jsonary.length(); i++) {
                                JSONObject jsonob = jsonary.getJSONObject(i);
                                String name = jsonob.getString("name");
                                username.add(name);
                                String sex = jsonob.getString("sex");
                                usersex.add(sex);
                                resid.add(jsonob.getString("registerid"));
                                String status=jsonob.getString("sfwc");
                                userstatus.add(status);
                                String servicetime=jsonob.getString("bjsj");
                                usertime.add(servicetime);
                               /* JSONObject jsonject = jsonob.getJSONObject("emergency");
                                String servicetime = jsonject.getString("clsj");
                                usertime.add(servicetime);
                                String status = jsonject.getString("sfcl");
                                userstatus.add(status);*/
                            }
                            setadapter();
                            servicelistInfo.onRefreshComplete();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ServiceActivity.this, "网络连接出错", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("userid",user);
                /*map.put("offset", String.valueOf(currentpage));
                map.put("status", "0"); //完成
                map.put("length", "10");*/
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
        adapter = new serviceadapter(ServiceActivity.this, username, usersex, usertime, userstatus, resid,rwid,bjid,user);
        servicelistInfo.setAdapter(adapter);
    }

    private void getvolley() {
        username.clear();
        rwid.clear();
        bjid.clear();
        usertime.clear();
        usersex.clear();
        userstatus.clear();
        resid.clear();
        queue = Volley.newRequestQueue(ServiceActivity.this);
        String url = Api.emergency;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        Log.i("xianshi", "onResponse: "+str);
                        try {
                            JSONArray jsonary = new JSONArray(str);
                            if (jsonary.length() == 0 && currentpage == 1) {
                                nomessageRl.setVisibility(View.VISIBLE);
                            } else {
                                for (int i = 0; i < jsonary.length(); i++) {
                                    JSONObject jsonob = jsonary.getJSONObject(i);
                                    String name = jsonob.getString("name");
                                    username.add(name);
                                    Log.i("cffgfggr", "setadapter: "+username.size());
                                    String sex = jsonob.getString("sex");
                                    usersex.add(sex);
                                    Log.i("cffgfggr1", "setadapter: "+usersex.size());
                                    resid.add(jsonob.getString("registerid"));
                                    Log.i("cffgfggr7", "setadapter: "+resid.size());
                                    String status=jsonob.getString("sfwc");
                                    userstatus.add(status);
                                    String servicetime=jsonob.getString("bjsj");
                                    usertime.add(servicetime);
                                    String baojin=jsonob.getString("bjid");
                                    bjid.add(baojin);
                                    String reid=jsonob.getString("rwid");
                                    rwid.add(reid);
                                   /* JSONObject jsonject = jsonob.getJSONObject("emergency");
                                    String servicetime = jsonject.getString("clsj");
                                    usertime.add(servicetime);
                                    Log.i("cffgfggr2", "setadapter: "+usertime.size());
                                    String status = jsonject.getString("sfwc");
                                    userstatus.add(status);*/
                                    Log.i("cffgfggr6", "setadapter: "+userstatus.size());
                                  /*  Log.i("cffgfggr", "setadapter: "+username.size());
                                    Log.i("cffgfggr1", "setadapter: "+usersex.size());
                                    Log.i("cffgfggr2", "setadapter: "+usertime.size());
                                    Log.i("cffgfggr3", "setadapter: "+usersex.size());
                                    Log.i("cffgfggr4", "setadapter: "+usertime.size());
                                    Log.i("cffgfggr5", "setadapter: "+username.size());
                                    Log.i("cffgfggr6", "setadapter: "+userstatus.size());
                                    Log.i("cffgfggr7", "setadapter: "+resid);*/
                                }
                                setadapter();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ServiceActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("userid",user);
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
}
