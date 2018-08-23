package com.example.chengduhome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import adapter.passolderadapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import util.Api;
import util.Constant;

public class PasstaskActivity extends AppCompatActivity {
    @Bind(R.id.title_rl)
    RelativeLayout titleRl;
    @Bind(R.id.img_iv)
    ImageView imgIv;
    @Bind(R.id.name_tv)
    TextView nameTv;
    @Bind(R.id.sex_tv)
    TextView sexTv;
    @Bind(R.id.xian_view)
    View xianView;
    @Bind(R.id.pass_dask)
    RelativeLayout passDask;
    @Bind(R.id.xian_view1)
    View xianView1;
    @Bind(R.id.pass_list)
    ListView passList;
    public TextView editor_tv;
    @Bind(R.id.return_fanhui)
    ImageView returnFanhui;
    @Bind(R.id.editor_tv)
    TextView editorTv;
    @Bind(R.id.notask_tv)
    TextView notaskTv;
    private ImageLoader loader;
    private ArrayList<String> datetime = new ArrayList<>();
    private ArrayList<String> reason = new ArrayList<>();
    private ArrayList<String> editid = new ArrayList<>();
    private passolderadapter adapter;
    private RequestQueue queue;
    String olderid;
    String recetime,bjid;
    String ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passtask);
        loader = ((App) getApplication()).getLoader();
        ButterKnife.bind(this);
        Intent intent = getIntent();
        olderid = intent.getStringExtra("resid");
        recetime = intent.getStringExtra("datetime");
        bjid=intent.getStringExtra("bjid");
        initview();
        getvolley();
        setlistener();
    }

    private void getvolley() {
        datetime.clear();
        reason.clear();
        queue = Volley.newRequestQueue(PasstaskActivity.this);
        String url = Api.lishineirong;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        Log.i("返回数据hf", "onResponse: " + str);
                        try {
                            JSONObject jsonob = new JSONObject(str);
                            nameTv.setText("姓名:" + jsonob.getString("name"));
                            sexTv.setText("性别:" + jsonob.getString("sex"));
                            loader.loadImage(Api.BASEURL+jsonob.getString("imgUrl"),new SimpleImageLoadingListener(){
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    super.onLoadingComplete(imageUri, view, loadedImage);
                                    imgIv.setImageBitmap(loadedImage);
                                }
                            });
                            JSONArray jsonary = jsonob.getJSONArray("lishirenwu");
                            if (jsonary!=null&jsonary.length() != 0) {
                                notaskTv.setVisibility(View.GONE);
                                for (int i = 0; i < jsonary.length(); i++) {
                                    JSONObject jsonobject = jsonary.getJSONObject(i);
                                    reason.add(jsonobject.getString("content"));
                                    datetime.add(jsonobject.getString("generationtime"));
                                    editid.add(jsonobject.getString("id"));
                                }
                                setadapter();
                            }else {
                                notaskTv.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                       /*try {
                            JSONArray  jsonary=new JSONArray(str);
                            for (int i = 0; i <jsonary.length() ; i++) {
                                JSONObject  jsonobject=jsonary.getJSONObject(i);
                                JSONObject jsonject = jsonobject.getJSONObject("emergency");
                                reason.add(jsonject.getString("content"));
                                ids=jsonject.getString("id");
                                datetime.add(jsonject.getString("clwcsj"));
                                nameTv.setText("姓名:"+jsonobject.getString("name"));
                                sexTv.setText("性别："+jsonobject.getString("sex"));
                              loader.loadImage(jsonobject.getString("imgUrl"),new SimpleImageLoadingListener(){
                                  @Override
                                   public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                       super.onLoadingComplete(imageUri, view, loadedImage);
                                       imgIv.setImageBitmap(loadedImage);
                                   }
                               });
                            }
                            setadapter();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PasstaskActivity.this, "网络连接出错", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("registerid", olderid);
                map.put("datetime", recetime);
                map.put("bjid",bjid);
                Log.i("huhuuh", "getParams: " + map.toString());
                //map.put("registerid",olderid);
               /* map.put("offset", String.valueOf(currentpage));
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

    private void setlistener() {
        returnFanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setadapter() {
        Log.i("yuanlai", "setadapter: " + reason);
        adapter = new passolderadapter(PasstaskActivity.this, datetime, reason, editid, olderid, ids, recetime);
        passList.setAdapter(adapter);
    }

    private void initview() {
        editor_tv = (TextView) findViewById(R.id.editor_tv);
       /* datetime.add("2017-11-30");
        datetime.add("2017-11-29");
        datetime.add("2017-11-28");
        datetime.add("2017-11-27");
        reason.add("我爱李子璇");
        reason.add("我爱刘芳芳");
        reason.add("我爱郑立燕");
        reason.add("我爱汪力攀");*/
    }
}
