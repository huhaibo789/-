package com.example.chengduhome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import adapter.helpmessageadapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import util.Api;
import util.Constant;

public class HelpermessageActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.help_rl)
    RelativeLayout helpRl;
    @Bind(R.id.older_edit)
    EditText olderEdit;
    @Bind(R.id.search_rl)
    RelativeLayout searchRl;
    @Bind(R.id.list_help)
    PullToRefreshListView listHelp;
    @Bind(R.id.search_btn)
    Button searchBtn;
    helpmessageadapter helpadapter;
    @Bind(R.id.unfinish_tv)
    TextView unfinishTv;
    @Bind(R.id.finish_tv)
    TextView finishTv;
    @Bind(R.id.fanhui)
    ImageView fanhui;
    @Bind(R.id.quick_iv)
    ImageView quickIv;
    String  input;
    String unwork="1";
    JSONArray jsonarry;
    int currentpage = 1;//当前页数
    private Handler handler = new Handler();
    private RequestQueue queue;
    private boolean falg;
    String result;
    private ArrayList<String> name = new ArrayList<>(); //姓名
    private ArrayList<String> time = new ArrayList<>(); //时间
    private ArrayList<String> sex = new ArrayList<>(); //性别
    private ArrayList<String> status = new ArrayList<>(); //状态
    private ArrayList<String> resid = new ArrayList<>(); //老人id
    private ProgressDialog progressDialog;
    String upload="1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpermessage);
        progressDialog=new ProgressDialog(HelpermessageActivity.this);
        progressDialog.setMessage("获取数据中...");
        ButterKnife.bind(this);
        unfinishTv.setOnClickListener(this);
        finishTv.setOnClickListener(this);
        fanhui.setOnClickListener(this);
        quickIv.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        enableLoadMore();
        setPulltoRefreshStyle();
        getvolley();
        setlistener();
    }
    private void getvolley() {
        queue = Volley.newRequestQueue(HelpermessageActivity.this);
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
                                        Toast.makeText(HelpermessageActivity.this, "没有任务信息", Toast.LENGTH_SHORT).show();
                                    } else {
                                        for (int i = 0; i < jsonarry.length(); i++) {
                                            JSONObject object = jsonarry.getJSONObject(i);
                                            String usename = object.getString("name");
                                            name.add(usename);
                                            /*String cardid=object.getString("cardno");
                                            sex.add(cardid);*/
                                            String userres = object.getString("registerid");
                                            resid.add(userres);
                                            String user_sex = object.getString("sex");
                                            sex.add(user_sex);
                                            String user_time = object.getString("nursetime");
                                            time.add(user_time);
                                            String user_work = object.getString("status");
                                            status.add(user_work);
                                        }
                                        if (jsonarry.length() != 0) {
                                            setadapter();
                                        }
                                    }
                                } else {
                                    Toast.makeText(HelpermessageActivity.this, "未登录", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(HelpermessageActivity.this, "服务器出错", Toast.LENGTH_SHORT).show();
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

    private void setlistener() {
        listHelp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(HelpermessageActivity.this,Saomaresult.class);
                intent.putExtra("resgesid",resid.get(position-1));
                startActivity(intent);
            }
        });
        quickIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(HelpermessageActivity.this, CaptureActivity.class), 0);
            }
        });
        listHelp.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
           //下拉
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                upload="1";
                currentpage = 1;
                postmessage();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                currentpage = currentpage + 1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         upload="2";
                         postmessage();
                    }
                });
            }
        });
    }


    private void setPulltoRefreshStyle() {
        listHelp.setMode(PullToRefreshBase.Mode.BOTH);
    }
    private void enableLoadMore() {
        ILoadingLayout il = listHelp.getLoadingLayoutProxy();
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

    private void setadapter() {
        helpadapter = new helpmessageadapter(HelpermessageActivity.this, name, sex, time, status);
        listHelp.setAdapter(helpadapter);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.unfinish_tv:
                currentpage=1;
                unfinishTv.setBackgroundColor(Color.parseColor("#0fdc3b"));
                finishTv.setBackgroundColor(Color.parseColor("#dcdada"));
                unwork="1";
               postmessage();
                progressDialog.dismiss();
                break;
            case R.id.finish_tv:
                currentpage=1;
                unfinishTv.setBackgroundColor(Color.parseColor("#dcdada"));
                finishTv.setBackgroundColor(Color.parseColor("#0fdc3b"));
                unwork="2";
                postmessage();
                progressDialog.dismiss();
                break;
            case R.id.fanhui:
                finish();
                break;
            case R.id.quick_iv:
                //startActivityForResult(new Intent(HelpermessageActivity.this, CaptureActivity.class), 0);
                break;
            case R.id.search_btn:
                 input=olderEdit.getText().toString();
                if(TextUtils.isEmpty(input)){
                    Toast toast = Toast.makeText(HelpermessageActivity.this, "请输入搜索条件", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else {
                    Intent intent=new Intent(HelpermessageActivity.this,SuosuoActivity.class);
                    intent.putExtra("name",input);
                    startActivity(intent);
            }
                break;
        }
    }
    private void postmessage() {
        if(upload.equals("1")){
            name.clear();
            sex.clear();
            time.clear();
            status.clear();
            resid.clear();
        }
        queue = Volley.newRequestQueue(HelpermessageActivity.this);
        String url = Api.itemScan;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        Log.i("khfrr", "onResponse: "+str);
                        if(str!=null){
                            try {
                                JSONObject jsonObject = new JSONObject(str);
                                String login = jsonObject.getString("flag");
                                if (login.equals("1")) {
                                    jsonarry = jsonObject.getJSONArray("op");
                                    if (jsonarry.length() == 0 && currentpage == 1) {
                                        Toast.makeText(HelpermessageActivity.this, "没有任务信息", Toast.LENGTH_SHORT).show();
                                        listHelp.onRefreshComplete();
                                    } else if(jsonarry.length()==0&&currentpage!=1){
                                        Toast.makeText(HelpermessageActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                                       listHelp.onRefreshComplete();
                                    }else {
                                        for (int i = 0; i < jsonarry.length(); i++) {
                                            JSONObject object = jsonarry.getJSONObject(i);
                                            String usename = object.getString("name");
                                            name.add(usename);
                                            String userres = object.getString("registerid");
                                            resid.add(userres);
                                            String user_sex = object.getString("sex");
                                            sex.add(user_sex);
                                            String user_time = object.getString("nursetime");
                                            time.add(user_time);
                                            String user_work = object.getString("status");
                                             status.add(user_work);
                                        }
                                        if(helpadapter!=null){
                                            helpadapter.notifyDataSetChanged();
                                            listHelp.onRefreshComplete();
                                        }else {
                                            setadapter();
                                            listHelp.onRefreshComplete();
                                        }
                                        if(upload.equals("1")){
                                            progressDialog.dismiss();//去掉加载框
                                        }
                                        upload="1";
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
                        Toast.makeText(HelpermessageActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 8 ) {
            result = data.getStringExtra("result");
            if (result == null) return;
            String[] results = result.split(",");
            if (results.length != 2) {
                Toast.makeText(this, "老人卡信息有误", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i("ydfbfhbff", "onActivityResult: "+results[1]);
                //快速扫码
                Intent inint=new Intent(HelpermessageActivity.this,Saomaresult.class);
                inint.putExtra("resgesid",results[1]);
                startActivity(inint);
            }
        }
    }