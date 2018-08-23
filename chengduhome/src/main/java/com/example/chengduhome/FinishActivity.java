package com.example.chengduhome;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.baidu.location.BDLocation;
import com.githang.statusbar.StatusBarCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import adapter.finishadapter;
import bean.Databean;
import util.Api;
import util.Constant;
import util.FileUtils;
import util.FileUtilsname;
import util.FileUtilspassword;
import util.LocationUtil;
import util.ReceiveBDLocationListener;
import util.Uplocation;

public class FinishActivity extends AppCompatActivity implements  finishadapter.CheckInterface{
    private RelativeLayout list_rl,show_nomessage;
    private ListView list_show;
    private double Longitude;
    private double Latitude;
    private finishadapter adapter;
    public CheckBox sure_iv;
    public Button sure_button;
    private TextView user_tvname,tishi_newsou;
    private ImageView shoptu_iv;
    private ArrayList<String> listitem=new ArrayList<>();//安排任务
    private ArrayList<Databean> listdata=new ArrayList<>();//选中的任务
    private ArrayList<String> charge_id=new ArrayList<>();//列表id
    private RequestQueue queue;
    String reges_id,nur_time,starttime,quickscan,isquick,url,cardno;
    String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        queue = Volley.newRequestQueue(this);
        Intent intent=getIntent();
        quickscan=intent.getStringExtra("quickscan");
        reges_id=intent.getStringExtra("resid"); //护理Id
        nur_time=intent.getStringExtra("nurtime");//护理时间
        cardno=intent.getStringExtra("cardid");//身份id
        initview();
        if(quickscan!=null){
            isquick="1";
        }else {
            isquick="0";
        }
        getvolley();  //网络请求
        setlistener();
        //getlocation();
        StatusBarCompat.setStatusBarColor(FinishActivity.this, Color.parseColor("#0aec68"), false);
    }
    private void getlocation() {
        new LocationUtil().startLocation(new ReceiveBDLocationListener() {
            @Override
            public BDLocation onReceiveBDLocationSuccess(String locationStr, BDLocation location) {
                Longitude = location.getLongitude();
                Latitude = location.getLatitude();
                FileUtilsname file=new FileUtilsname();
                String loginname=file.readDataFromFile(FinishActivity.this);
                FileUtilspassword file_psd=new FileUtilspassword();
                String  loginpassword=file_psd.readDataFromFile(FinishActivity.this);
                FileUtils file_id=new FileUtils();
                String userid=file_id.readDataFromFile(FinishActivity.this);
                if(loginname!=null&&loginpassword!=null&&userid!=null){
                    Uplocation up=new Uplocation();
                    up.scanlocation(FinishActivity.this,userid, loginname,reges_id,nur_time, String.valueOf(Latitude), String.valueOf(Longitude));
                }
                return null;
            }
            @Override
            public BDLocation onReceiveBDLocationFailed(String errorMsg, int errorcode) {
                //  Toast.makeText(FinishActivity.this, "错误码" + errorcode + ":" + errorMsg, Toast.LENGTH_SHORT).show();
                return null;
            }
        });
    }
    private void getvolley() {
        listitem.clear();
        charge_id.clear();
        queue = Volley.newRequestQueue(FinishActivity.this);
        if(isquick.equals("1")){
            url= Api.REGESTIGER;
        }else {
            url=Api.FINISH;
        }
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str=response.toString().trim();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(str);
                            JSONArray jsonary=jsonObject.getJSONArray("flag");
                            String use_name=jsonObject.getString("username");
                            user_tvname.setText(use_name);
                            if(isquick.equals("1")){
                                reges_id=jsonObject.getString("registerid2");
                                nur_time=jsonObject.getString("nursetime");
                                starttime=jsonObject.getString("starttime");
                            }else {
                                JSONObject op_object=jsonObject.getJSONObject("op");
                                starttime=op_object.getString("starttime");
                            }
                            if(jsonary.length()==0){
                                show_nomessage.setVisibility(View.VISIBLE);
                                tishi_newsou.setText("没有该老人的服务");
                            }else{
                                show_nomessage.setVisibility(View.GONE);
                                for (int i = 0; i <jsonary.length() ; i++) {
                                    JSONObject object = jsonary.getJSONObject(i);
                                    String title=object.getString("sftitle");
                                    String title_explain=object.getString("shuiming");
                                    String chid=object.getString("chargeid");
                                    listitem.add(String.valueOf(i+1)+" "+title+":"+title_explain);
                                    charge_id.add(chid);
                                }
                                setadapter();
                                adapter.setCheckInterface(FinishActivity.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                if(isquick.equals("1")&&quickscan!=null){
                    map.put("registerid",quickscan);
                }else {
                    if(reges_id!=null){
                        map.put("registerid",reges_id);  //用户id
                    }else {
                        map.put("registerid","");
                    }
                    if(nur_time!=null){
                        map.put("nursetime",nur_time);  //手机
                    }else {
                        map.put("nursetime","");
                    }
                }
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
        sure_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listdata!=null&&listdata.size()!=0){
                    if(sure_iv.isChecked()){
                        for (int i = 0; i <listitem.size() ; i++) {
                            listdata.get(i).setChoose(true);
                        }
                        adapter.notifyDataSetChanged();
                    }else {
                        for (int i = 0; i <listitem.size() ; i++) {
                            listdata.get(i).setChoose(false);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        list_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void setadapter() {
        initdata();
        adapter=new finishadapter(FinishActivity.this,listitem,listdata,charge_id,reges_id,nur_time,starttime);
        list_show.setAdapter(adapter);
    }
    private void initdata() {
        listdata.clear();
        Databean data=null;
        for (int i = 0; i <listitem.size() ; i++) {
            data=new Databean();
            data.setTitle(listitem.get(i));
            listdata.add(data);
        }
    }
    private void initview() {
        show_nomessage= (RelativeLayout) findViewById(R.id.show_nomessage);
        shoptu_iv= (ImageView) findViewById(R.id.shoptu_iv);
        tishi_newsou= (TextView) findViewById(R.id.tishi_newsou);
        user_tvname= (TextView) findViewById(R.id.user_tvname);
        sure_iv= (CheckBox) findViewById(R.id.sure_iv);
        sure_button= (Button) findViewById(R.id.sure_button);
        list_rl= (RelativeLayout) findViewById(R.id.list_rl);
        list_show= (ListView) findViewById(R.id.list_show);
    }
    @Override
    public void checkGroup(int position, boolean isChecked) {
        listdata.get(position).setChoose(isChecked);
        if (isAllCheck())
            sure_iv.setChecked(true);
        else
            sure_iv.setChecked(false);
        //  adapter.notifyDataSetChanged();
    }
    /**
     * 遍历list集合
     *
     * @return
     */
    private boolean isAllCheck() {
        for (Databean group : listdata) {
            if (!group.isChoose())
                return false;
        }
        return true;
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
            if(isquick.equals("1")&&quickscan!=null){
                if(quickscan.equals(results[1])){
                    finish();
                }
            }else if(cardno!=null&&cardno.equals(results[1])){
                finish();
                Toast.makeText(this, "扫码成功", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "老人卡信息不符合", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
