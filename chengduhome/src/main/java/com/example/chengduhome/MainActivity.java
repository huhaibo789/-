package com.example.chengduhome;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.githang.statusbar.StatusBarCompat;
import com.zxing.qrcode.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import util.Api;
import util.Constant;
import util.FileUtils;
import util.FileUtilsname;
import util.FileUtilspassword;
import util.LocationUtil;
import util.PermissionsChecker;
import util.ReceiveBDLocationListener;
import util.Uplocation;
public class MainActivity extends AppCompatActivity {
    private double Longitude;
    private double Latitude;
    private WebView contentWebView = null;
    private boolean startLoopUploadLocation = false;//是否开启轮训上传位置
    private String userid;
    private String phone;
    private String username;
    public static boolean isInited = false;
    private EditText user_editname, user_password;
    private Button post_login;
    private RequestQueue queue;
    String loginname;
    String loginpassword;
    private long exitTime = 0;//在按一次退出app
    private long[] mHits = new long[2];
    Timer timer = new Timer(true);
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private static final int REQUEST_PERMISSION = 4;  //权限请求
    static final String[] PERMISSIONSLocation = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!isInited) {
            isInited = true;
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(MainActivity.this, SplashActivity.class);
            startActivity(intent);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
        handler.sendEmptyMessage(2);
        StatusBarCompat.setStatusBarColor(MainActivity.this, Color.parseColor("#0fdc3b"), false); //改变状态栏颜色
    }
    private void startPermissionsActivity1() {
        PermissionsActivity.startActivityForResult(this, REQUEST_PERMISSION,
                PERMISSIONSLocation);
    }
    private void postdata() {
        queue = Volley.newRequestQueue(MainActivity.this);
        String url = Api.LOGINURL;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("1")) {
                                userid = jsonObject.getString("userid");
                                phone = jsonObject.getString("phone");
                                FileUtils files = new FileUtils();
                                files.saveDataToFile(MainActivity.this, userid);
                                Intent intent = new Intent(MainActivity.this, UnfinishActivity.class);
                               /* intent.putExtra("isflag","1");*/
                                startActivity(intent);
                                startLoopUploadLocation = true;
                                getLocation();
                            } else if(flag.equals("2")){
                                userid = jsonObject.getString("userid");
                                Intent intent = new Intent(MainActivity.this, ServiceActivity.class);  //志愿者界面
                                intent.putExtra("user",userid);
                                startActivity(intent);
                            }else if(flag.equals("3")){
                                userid = jsonObject.getString("userid");
                                phone = jsonObject.getString("phone");
                                FileUtils files = new FileUtils();
                                files.saveDataToFile(MainActivity.this, userid);
                                Intent intent = new Intent(MainActivity.this, UnfinishActivity.class);
                                startActivity(intent);
                                startLoopUploadLocation = true;
                                getLocation();
                            }else {
                                Toast.makeText(MainActivity.this, "密码或账号不对", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if (loginname != null) {
                    map.put("username", loginname);
                }
                if (loginpassword != null) {
                    map.put("password", loginpassword);
                }
                return map;
            }
            //重写parseNetworkResponse方法，返回的数据中 Set-Cookie:***************;
            //其中**************为session id
            @Override
            protected Response<String> parseNetworkResponse(
                    NetworkResponse response) {
                Response<String> superResponse = super
                        .parseNetworkResponse(response);
                Map<String, String> responseHeaders = response.headers;
                String rawCookies = responseHeaders.get("Set-Cookie");
                if(rawCookies!=null){
                    //Constant是一个自建的类，存储常用的全局变量
                    Constant.localCookie = rawCookies.substring(0, rawCookies.indexOf(";"));
                }

                return superResponse;
            }
        };
        queue.add(post);
    }
    private void initview() {
        user_editname = (EditText) findViewById(R.id.user_editname);
        user_password = (EditText) findViewById(R.id.user_password);
        post_login = (Button)findViewById(R.id.post_login);
        FileUtilsname file = new FileUtilsname();
        String name = file.readDataFromFile(MainActivity.this);
        FileUtilspassword file_psd = new FileUtilspassword();
        String psd = file_psd.readDataFromFile(MainActivity.this);
        if (name != null && psd != null) {
            user_editname.setText(name);
            user_password.setText(psd);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    getLocation();
                    break;
                case 2:
                    checkpermission();
                    break;
            }
        }
    };
    private void checkpermission() {
        mPermissionsChecker = new PermissionsChecker(MainActivity.this);
        //检查权限(6.0以上做权限判断)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mPermissionsChecker.lacksPermissions(PERMISSIONSLocation)) {
                startPermissionsActivity1();  //检查权限
            }
        }
    }
    private void getLocation() {
        new LocationUtil().startLocation(new ReceiveBDLocationListener() {
            @Override
            public BDLocation onReceiveBDLocationSuccess(String locationStr, BDLocation location) {
                Longitude = location.getLongitude();
                Latitude = location.getLatitude();
                if (startLoopUploadLocation) {
                    Uplocation up = new Uplocation(MainActivity.this, userid, phone, loginname, loginpassword, String.valueOf(Latitude), String.valueOf(Longitude));
                    //启动定时器
                    timer.schedule(new RequestTimerTask(), 5 * 60 * 1000);//5分钟执行一次
                }
                return null;
            }
            @Override
            public BDLocation onReceiveBDLocationFailed(String errorMsg, int errorcode) {
                Toast.makeText(MainActivity.this, "错误码" + errorcode + ":" + errorMsg, Toast.LENGTH_SHORT).show();
                return null;
            }
        });
    }
    public void PostClick(View view) {
        loginname = user_editname.getText().toString().trim();    //用户姓名
        loginpassword = user_password.getText().toString().trim(); //登录密码
        if (loginname == null || loginpassword == null || TextUtils.isEmpty(loginname) || TextUtils.isEmpty(loginpassword)) {
            Toast toast = Toast.makeText(MainActivity.this, "账号和密码不能为空", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        } else {
            FileUtilsname file = new FileUtilsname();
            file.saveDataToFile(MainActivity.this, loginname);
            FileUtilspassword file_psw = new FileUtilspassword();
            file_psw.saveDataToFile(MainActivity.this, loginpassword);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    postdata();
                }
            });
        }
    }
    class RequestTimerTask extends TimerTask {
        public void run() {
            handler.sendEmptyMessage(1);  //发送定位
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            if (result == null) return;
            String[] results = result.split(",");
            if (results.length != 2) {
                Toast.makeText(this, "不是有效的数据:" + result, Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
}
