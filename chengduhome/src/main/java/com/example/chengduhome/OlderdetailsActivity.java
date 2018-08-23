package com.example.chengduhome;

import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import adapter.olderdetailsadapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import util.Api;
import util.Constant;

public class OlderdetailsActivity extends AppCompatActivity {
    @Bind(R.id.list_show)
    ListView listShow;
    @Bind(R.id.return_iv)
    ImageView returnIv;
    public TextView editor_tv;
    @Bind(R.id.show_notask)
    RelativeLayout showNotask;
    private ArrayList<String> datetime = new ArrayList<>();
    private ArrayList<String> reason = new ArrayList<>();
    private ArrayList<String> reid = new ArrayList<>();
    private olderdetailsadapter adapter;
    private RequestQueue queue;
    String resid;
    String receivetime;
    String ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olderdetails);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        resid = intent.getStringExtra("resid");
        receivetime = intent.getStringExtra("datetime");
        initview();
        getvolley();
        setlistener();
    }

    private void getvolley() {
        datetime.clear();
        reason.clear();
        queue = Volley.newRequestQueue(OlderdetailsActivity.this);
        String url = Api.lishineirong;
        final StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        try {
                            JSONObject jsonob = new JSONObject(str);
                            JSONArray jsonary = jsonob.getJSONArray("lishirenwu");
                            if (jsonary != null && jsonary.length() != 0) {
                                showNotask.setVisibility(View.GONE);
                                for (int i = 0; i < jsonary.length(); i++) {
                                    JSONObject jsonobject = jsonary.getJSONObject(i);
                                    reason.add(jsonobject.getString("content"));
                                    datetime.add(jsonobject.getString("generationtime"));
                                    reid.add(jsonobject.getString("id"));
                                }
                                setadapter();
                            } else {
                                showNotask.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OlderdetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("registerid", resid);
                map.put("datetime", receivetime);
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
        returnIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setadapter() {
        adapter = new olderdetailsadapter(OlderdetailsActivity.this, datetime, reason, reid, resid, ids, receivetime);
        listShow.setAdapter(adapter);
    }

    private void initview() {
        editor_tv = (TextView) findViewById(R.id.editor_tv);
       /* datetime.add("2017-11-30");
        datetime.add("2017-11-29");
        datetime.add("2017-11-28");
        datetime.add("2017-11-27");
        reason.add("我爱谁的时间都是");
        reason.add("的交互啥都好说");
        reason.add("电话说的就是");
        reason.add("都恢复大方");*/
    }
}
