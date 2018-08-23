package com.example.chengduhome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import adapter.sousuoadapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import util.Api;
import util.Constant;

public class SuosuoActivity extends AppCompatActivity implements PullToRefreshListView.OnRefreshListener2 {
    @Bind(R.id.name_tv)
    TextView nameTv;
    @Bind(R.id.work_time)
    TextView workTime;
    @Bind(R.id.showmessage_rl)
    RelativeLayout showmessageRl;
    @Bind(R.id.list_search)
    PullToRefreshListView listSearch;
    @Bind(R.id.no_serarch)
    RelativeLayout noSerarch;
    @Bind(R.id.iv_arrow)
    ImageView ivArrow;
    private sousuoadapter worker;
    private RequestQueue queue;
    JSONArray jsonarry;
    int currentpage = 1;//当前页数
    private ArrayList<String> username = new ArrayList<>();//姓名
    private ArrayList<String> usersex = new ArrayList<>();//性别
    private ArrayList<String> usertime = new ArrayList<>();//开始时间
    private ArrayList<String> userwork = new ArrayList<>(); //任务的状态
    private ArrayList<String> userresgiter = new ArrayList<>(); //注册id
    private ArrayList<String> usercardno = new ArrayList<>(); //身份信息
    String name;
    private boolean falg;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suosuo);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        listSearch.setOnRefreshListener(this);
        enableload();
        getvolley();
        setlistener();
    }

    private void setlistener() {
    ivArrow.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    });
    }

    private void enableload() {
        listSearch.setMode(PullToRefreshBase.Mode.BOTH);
    }

    private void getvolley() {
        queue = Volley.newRequestQueue(SuosuoActivity.this);
        String url = Api.loadname;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        if (str != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(str);
                                String login = jsonObject.getString("flag");
                                if (login.equals("1")) {
                                    jsonarry = jsonObject.getJSONArray("op");
                                    if (jsonarry.length() == 0 && currentpage == 1) {
                                        noSerarch.setVisibility(View.VISIBLE);
                                    } else {
                                        noSerarch.setVisibility(View.GONE);
                                        listSearch.setMode(PullToRefreshBase.Mode.BOTH);
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
                                            String user_card = object.getString("cardno");
                                            usercardno.add(user_card);
                                            String user_work = object.getString("status");
                                            userwork.add(user_work);
                                        }
                                        if (jsonarry.length() != 0) {
                                            setadapter();
                                        }
                                    }
                                } else {
                                    Toast.makeText(SuosuoActivity.this, "未登录", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SuosuoActivity.this, "服务器出错了", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                try {
                    String str = new String(name.getBytes("utf-8"), "ISO-8859-1");
                    map.put("name", str);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                map.put("offset", String.valueOf(currentpage));
                map.put("length", "10");
                Log.i("hahsasdd", "getParams: " + map);
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
        worker = new sousuoadapter(SuosuoActivity.this, username, usersex, usertime, userwork, userresgiter, usercardno);
        listSearch.setAdapter(worker);
    }

    //下拉
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        currentpage = 1;
        postshujuDown();
    }

    private void postshujuDown() {
        username.clear();
        usersex.clear();
        userwork.clear();
        usertime.clear();
        userresgiter.clear();
        usercardno.clear();
        queue = Volley.newRequestQueue(SuosuoActivity.this);
        String url = Api.loadname;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        if (str != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(str);
                                String login = jsonObject.getString("flag");
                                if (login.equals("1")) {
                                    jsonarry = jsonObject.getJSONArray("op");
                                    if (jsonarry.length() == 0 && currentpage == 1) {
                                        noSerarch.setVisibility(View.VISIBLE);
                                    } else {
                                        noSerarch.setVisibility(View.GONE);
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
                                            String user_card = object.getString("cardno");
                                            usercardno.add(user_card);
                                            String user_work = object.getString("status");
                                            userwork.add(user_work);
                                        }
                                        setadapter();
                                        listSearch.onRefreshComplete();
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
                        Toast.makeText(SuosuoActivity.this, "服务器出错啦", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                try {
                    String str = new String(name.getBytes("utf-8"), "ISO-8859-1");
                    map.put("name", str);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
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

    //上拉
    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        currentpage = currentpage + 1;
        postshuju1Up();

    }

    private void postshuju1Up() {
        queue = Volley.newRequestQueue(SuosuoActivity.this);
        String url = Api.loadname;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        if (str != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(str);
                                String login = jsonObject.getString("flag");
                                if (login.equals("1")) {
                                    jsonarry = jsonObject.getJSONArray("op");
                                    if (jsonarry.length() == 0 && currentpage == 1) {
                                        noSerarch.setVisibility(View.VISIBLE);
                                    } else if(jsonarry.length()==0&&currentpage!=1){
                                        Toast.makeText(SuosuoActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                                        listSearch.onRefreshComplete();
                                    } else {
                                        noSerarch.setVisibility(View.GONE);
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
                                            String user_card = object.getString("cardno");
                                            usercardno.add(user_card);
                                            String user_work = object.getString("status");
                                            userwork.add(user_work);
                                        }
                                        if (worker != null){
                                            worker.notifyDataSetChanged();
                                            listSearch.onRefreshComplete();
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
                        Toast.makeText(SuosuoActivity.this, "服务器出错了", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                try {
                    String str = new String(name.getBytes("utf-8"), "ISO-8859-1");
                    map.put("name", str);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
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
}
