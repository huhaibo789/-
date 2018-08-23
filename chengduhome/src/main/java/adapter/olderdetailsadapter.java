package adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chengduhome.OlderdetailsActivity;
import com.example.chengduhome.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import util.Api;
import util.Constant;

/**
 * Created by Administrator on 2017/11/30.
 */

public class olderdetailsadapter extends BaseAdapter {
    //定义hashMap 用来存放之前创建的每一项item
    HashMap<Integer, View> lmap = new HashMap<Integer, View>();
    private OlderdetailsActivity activity;
    private LayoutInflater inflater;
    private ArrayList<String> data;  //用户姓名
    private ArrayList<String> data1;  //用户性别
    private ArrayList<String> data2;  //用户性别
    String resid;
    String ids;
    int weizhi;//记录Position
    private RequestQueue queue;
    String receivetime;
    String aa="2";
    HashMap<Integer, String> saveMap=new HashMap<Integer, String>();  //这个集合用来存储对应位置上Editext中的文本内容
    private ArrayList<String> arrylist=new ArrayList<>(); //存放输入框的值
    public olderdetailsadapter (OlderdetailsActivity context,ArrayList<String> data,ArrayList<String> data1,ArrayList<String> data2,String resid,String ids,String receivetime){
        super();
        this.activity=context;
        this.data=data;
        this.data1=data1;
        this.data2=data2;
        this.resid=resid;
        this.ids=ids;
        this.receivetime=receivetime;
        for (int i = 0; i < data1.size(); i++) {
            saveMap.put(i,"");     //初始化输入框
        }
    }
    @Override
    public int getCount() {
        return data.size();
    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        olderdetailsadapter.ViewHold vh = null;
        if(lmap.get(position)==null){
            convertView=View.inflate(activity, R.layout.activity_olderdetailsadapter,null);
            vh=new olderdetailsadapter.ViewHold();
            convertView.setTag(vh);
            lmap.put(position,convertView);
            vh.datetime_tv= (TextView) convertView.findViewById(R.id.datetime_tv);
            vh.edit_et= (EditText) convertView.findViewById(R.id.edit_et);
        }else {
            convertView = lmap.get(position);
            vh= (olderdetailsadapter.ViewHold) convertView.getTag();
        }
        final olderdetailsadapter.ViewHold finalVh = vh;
        vh.datetime_tv.setText(data.get(position));
        if(aa.equals("1")){
            vh.edit_et.setEnabled(true);
        }
        vh.edit_et.setText(data1.get(position));
        final ViewHold finalVh1 = vh;
        activity.editor_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.editor_tv.getText().toString().equals("编辑")){
                    activity.editor_tv.setText("保存");
                    notifyDataSetChanged();
                    aa="1";
                }else {
                    postdata();
                }

            }
        });
        return convertView;
    }

    private void postdata() {
        queue = Volley.newRequestQueue(activity);
        String url= Api.savepassemergency;
        final StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str=response.toString().trim();
                        try {
                            JSONObject jsob=new JSONObject(str);
                            String isflag=jsob.getString("flag");
                            Log.i("dsess", "onResponse: "+isflag);
                            if(isflag.equals("1")){
                                Toast.makeText(activity, "确认成功", Toast.LENGTH_SHORT).show();
                                 activity.finish();
                            }else {
                                Toast.makeText(activity, "提交失败", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("zamenjie", "onResponse: "+str);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                for (HashMap.Entry entry : saveMap.entrySet()) {
                    Object value = entry.getValue( );
                    Object key = entry.getKey( );
                    if(String.valueOf(value)!=null&&!String.valueOf(value).equals("")){
                        arrylist.add(String.valueOf(value) );
                    }else {
                        arrylist.add("");
                    }
                }
                JSONArray ClientKey = new JSONArray();
                JSONObject ob=new JSONObject();
                JSONObject json_ob=null;
                for (int i = 0; i <data1.size(); i++) {
                    try {
                        json_ob=new JSONObject();
                        json_ob.put("editid",data2.get(i));
                        json_ob.put("content",arrylist.get(i));
                        ClientKey.put(json_ob);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    ob.put("person",ClientKey);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                map.put("json",String.valueOf(ob));

                Log.i("fdsddds", "getParams: "+map.toString());
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

    public class ViewHold {
        TextView  datetime_tv;
        EditText edit_et;
    }

}
