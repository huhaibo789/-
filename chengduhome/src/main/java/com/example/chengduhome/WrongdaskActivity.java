package com.example.chengduhome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import util.Api;
import util.Constant;

public class WrongdaskActivity extends AppCompatActivity {

    @Bind(R.id.reason_tv)
    TextView reasonTv;
    @Bind(R.id.unable_scan)
    Button unableScan;
    @Bind(R.id.reson_edit)
    EditText resonEdit;
    @Bind(R.id.cancel_btn)
    Button cancelBtn;
    @Bind(R.id.post_btn)
    Button postBtn;
    @Bind(R.id.cancel_rl)
    RelativeLayout cancelRl;
    @Bind(R.id.unable_dask)
    Button unableDask;
    @Bind(R.id.unabledask_edit)
    EditText unabledaskEdit;
    @Bind(R.id.unabledask_btn)
    Button unabledaskBtn;
    @Bind(R.id.unabledask_post)
    Button unabledaskPost;
    @Bind(R.id.unabledask_rl)
    RelativeLayout unabledaskRl;
    @Bind(R.id.reason_rl)
    RelativeLayout reasonRl;
    private RequestQueue queue;
    private String wrongscan,unablefinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrongdask);
        ButterKnife.bind(this);
        setlistener();
    }
    private void getvolley() {
        queue = Volley.newRequestQueue(WrongdaskActivity.this);
        String url = Api.WRONGSCAN;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        try {
                            JSONObject jsonob=new JSONObject(str);
                            String biaozhi=jsonob.getString("flag");
                            if(biaozhi.equals("1")){
                                Toast.makeText(WrongdaskActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(WrongdaskActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WrongdaskActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                wrongscan=resonEdit.getText().toString();
                unablefinish=unabledaskEdit.getText().toString();
                if(unablefinish!=null){
                    map.put("yichang", unablefinish);
                }else {
                    map.put("yichang","");
                }
                if(wrongscan!=null){
                    map.put("saoma",wrongscan);
                }else {
                    map.put("saoma","");
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
        reasonRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        unableScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resonEdit.setVisibility(View.VISIBLE);
                cancelRl.setVisibility(View.VISIBLE);
            }
        });
        unableDask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unabledaskEdit.setVisibility(View.VISIBLE);
                unabledaskRl.setVisibility(View.VISIBLE);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resonEdit.setVisibility(View.GONE);
                cancelRl.setVisibility(View.GONE);
            }
        });
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getvolley();
                resonEdit.setVisibility(View.GONE);
                cancelRl.setVisibility(View.GONE);
            }
        });
        unabledaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unabledaskEdit.setVisibility(View.GONE);
                unabledaskRl.setVisibility(View.GONE);
            }
        });
        unabledaskPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getvolley();
                unabledaskEdit.setVisibility(View.GONE);
                unabledaskRl.setVisibility(View.GONE);
            }
        });
    }


}
