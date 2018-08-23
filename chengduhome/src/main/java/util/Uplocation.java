package util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/21.
 */

public class Uplocation {
    //上传定位
    private RequestQueue queue;
    private Context context;
    public Uplocation(){

    }
    public Uplocation(final Context context, final String userid, final String phone, final String loginName, final String loginPassword, final String Lati, final String Longity ) {
         this.context=context;
        queue = Volley.newRequestQueue(context);
        String  url=Api.UPLOADLOCATOIN ;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str=response.toString().trim();
                        try {
                            JSONObject jsonds=new JSONObject(str);
                             String islocation=jsonds.getString("flag");
                            if(!islocation.equals("1")){
                                Toast.makeText(context, "请开启定位权限", Toast.LENGTH_SHORT).show();
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
                if(userid!=null){
                    map.put("userid",userid);  //用户id
                }else {
                    map.put("userid","");
                }
                if(phone!=null){
                    map.put("phone",phone);
                }else {
                    map.put("phone","");
                }
                if(loginName!=null){
                    try {
                        String str = new String(loginName.getBytes("utf-8"), "ISO-8859-1");
                        map.put("username",str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else {                           //姓名
                    map.put("username","");
                }
                if(Lati!=null){
                    map.put("wei",Lati);
                }
                if(Longity !=null){
                    map.put("jin",Longity); //预约的时间
                }else {
                    map.put("jin","");
                }
                Log.i("heiheisa", "getParams: "+map.toString());
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
    public  void  scanlocation(final Context context, final String userid, final String loginName,final String resid,  final String nursetime, final String Lati, final String Longity ){
        this.context=context;
        queue = Volley.newRequestQueue(context);
        String   url=Api.SCANLOCATOIN;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("hahssdf1", "onResponse: "+response.toString().trim());
                        String str=response.toString().trim();
                        try {
                            JSONObject jsonds=new JSONObject(str);
                            String islocation=jsonds.getString("flag");
                            if(!islocation.equals("1")){
                                Toast.makeText(context, "请开启定位权限", Toast.LENGTH_SHORT).show();
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
                if(userid!=null){
                    map.put("userid",userid);  //用户id
                }else {
                    map.put("userid","");
                }
                if(resid!=null){
                    map.put("regid",resid);
                }else {
                    map.put("regid","");
                }
                if(loginName!=null){
                    try {
                        String str = new String(loginName.getBytes("utf-8"), "ISO-8859-1");
                        map.put("username",str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else {                           //姓名
                    map.put("username","");
                }
                if(nursetime!=null){
                    map.put("nursetime",nursetime);  //手机
                }else {
                    map.put("nursetime","");
                }
                if(Lati!=null){
                    map.put("lan",Lati);
                }else {
                    map.put("lan","");
                }
                if(Longity !=null){
                    map.put("lon",Longity); //预约的时间
                }else {
                    map.put("lon","");
                }
                Log.i("heiheisa1", "getParams: "+map.toString());
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
