package adapter;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chengduhome.FinishActivity;
import com.example.chengduhome.R;
import com.zxing.qrcode.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import bean.Databean;
import util.Api;
import util.Constant;

/**
 * Created by Administrator on 2017/8/8.
 */

public class finishadapter extends BaseAdapter {
    //定义hashMap 用来存放之前创建的每一项item
    HashMap<Integer, View> lmap = new HashMap<Integer, View>();
    private FinishActivity activity;
    private LayoutInflater inflater;
    private ArrayList<String> data;  //用户姓名
    private ArrayList<Databean> listdata;
    private CheckInterface checkInterface; //定义checkbox的接口
    private ArrayList<String> arryposition=new ArrayList<>();//存放位置
    private ArrayList<String> arrylist=new ArrayList<>(); //存放输入框的值
    private ArrayList<String> chargeid=new ArrayList<>();//item的id;
    private ArrayList<String> status=new ArrayList<>();//每一项是否完成;
    private RequestQueue queue;
    String str,regster_id,nurse_id,starttime;
    HashMap<Integer, String> saveMap=new HashMap<Integer, String>();  //这个集合用来存储对应位置上Editext中的文本内容
    //private boolean[] checks; //用于保存checkBox的选择状态
    public finishadapter (FinishActivity context,ArrayList<String> data,ArrayList<Databean> listdata,ArrayList<String> chargeid
                ,String regster_id,String nurse_id,String starttime){
        super();
        this.activity=context;
        this.data=data;
        this.listdata=listdata;
        this.starttime=starttime;
        this.chargeid=chargeid;
        this.regster_id=regster_id;
        this.nurse_id=nurse_id;
        queue = Volley.newRequestQueue(activity);
        for (int i = 0; i < listdata.size(); i++) {
            saveMap.put(i,"");     //初始化输入框
        }
    }
    @Override
    public int getCount() {
        return listdata== null ? 0 : listdata.size();
    }

    @Override
    public Object getItem(int position) {
        return listdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHold vh = null;
        if(lmap.get(position) == null){
            convertView=View.inflate(activity, R.layout.activity_finishitem,null);
            vh=new ViewHold();
            convertView.setTag(vh);
            vh.clean_tv= (TextView) convertView.findViewById(R.id.clean_tv);
            vh.finish_tv= (TextView) convertView.findViewById(R.id.finish_tv);
            vh.unfinish= (TextView) convertView.findViewById(R.id.unfinish);
            vh.finish_edit= (EditText) convertView.findViewById(R.id.finish_edit);
            vh.finish_iv= (CheckBox) convertView.findViewById(R.id.finish_iv);
            vh.unfinish_iv= (ImageView) convertView.findViewById(R.id.unfinish_iv);
            vh.unfinish_rl= (RelativeLayout) convertView.findViewById(R.id.unfinish_rl);
        }else {
            convertView = lmap.get(position);
            vh= (ViewHold) convertView.getTag();
        }
        final Databean datashuju = listdata.get(position);
        vh.clean_tv.setText(data.get(position));
        final ViewHold finalVh1 = vh;
        final ViewHold finalVh5 = vh;
        final ViewHold finalVh6 = vh;
        vh.finish_edit.clearFocus();//清除焦点  不清除的话因为item复用的原因   多个Editext同时改变
        vh.finish_edit.setText(saveMap.get(position));//将对应保存在集合中的文本内容取出来  并显示上去
        if(saveMap.get(position)!=null&&!saveMap.get(position).equals("")){  //控制上下滑动时导致未完成按钮状态改变
            vh.finish_edit.setVisibility(View.VISIBLE);
            vh.unfinish_iv.setImageResource(R.drawable.ck_select);
        }
        vh.unfinish_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalVh5.unfinish_iv.setImageResource(R.drawable.ck_select);
                listdata.get(position).setChoose(false);
                finalVh6.finish_iv.setChecked(false);
                finalVh1.finish_edit.setVisibility(View.VISIBLE);
                activity.sure_iv.setChecked(false);
            }
        });
        vh.finish_iv.setChecked(datashuju.isChoose());
        if(datashuju.isChoose()){
             vh.finish_edit.setVisibility(View.GONE);
            finalVh5.unfinish_iv.setImageResource(R.drawable.ck_normal);

        }
        final ViewHold finalVh3 = vh;
        final ViewHold finalVh4 = vh;
        final ViewHold finalVh = vh;
        vh.finish_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalVh3.finish_iv.setChecked(true);
                finalVh4.unfinish_iv.setImageResource(R.drawable.ck_normal);
                finalVh4.finish_edit.setVisibility(View.GONE);
                finalVh.finish_edit.setText("");
               checkInterface.checkGroup(position, ((CheckBox) view).isChecked());//向外暴露接口
            }
        });
        vh.finish_edit.addTextChangedListener(new TextWatcher() {    //监听输入框的改变
             @Override
             public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

             }
             @Override
             public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
             }
             @Override
             public void afterTextChanged(Editable editable) {
                 saveMap.put(position, editable.toString());//在这里根据position去保存文本内容
             }
         });
        activity.sure_button.setOnClickListener(new View.OnClickListener() {    //点击确定后请求数据
            @Override
            public void onClick(View view) {
                arrylist.clear();
                arryposition.clear();
                status.clear();
             /*   for (HashMap.Entry entry : saveMap.entrySet()) {
                    Object value = entry.getValue( );
                    Object key = entry.getKey( );
                    if(String.valueOf(value)!=null&&!String.valueOf(value).equals("")){
                        arrylist.add(String.valueOf(value) );
                    }else {
                        arrylist.add("");
                    }
                }
                Log.i("ftyrijr", "afterTextChanged: "+arrylist.size());
                SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss     ");
                Date curDate=new Date(System.currentTimeMillis());//获取当前时间
                 str=formatter.format(curDate);
                JSONArray ClientKey = new JSONArray();
                JSONObject ob=new JSONObject();
                JSONObject json_ob=null;
                for (int i = 0; i <listdata.size(); i++) {
                    if(listdata!=null&&listdata.get(i).isChoose()){
                        Log.i("aisays", "onClick: "+i);
                        status.add("0");
                    }else {
                        status.add("1");
                    }
                    try {
                            json_ob=new JSONObject();
                            json_ob.put("status", status.get(i));
                            json_ob.put("nurseid",chargeid.get(i) );
                            json_ob.put("remark",arrylist.get(i));
                            ClientKey.put(json_ob);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    ob.put("person",ClientKey);
                    ob.put("endtime",str);
                    ob.put("registerid",regster_id);
                    ob.put("nursetime",nurse_id);
                    ob.put("commentflag","0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                postadata();
            }
        });
        return convertView;
    }
    private void postadata() {
        queue = Volley.newRequestQueue(activity);
        String url= Api.saveFinish;
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
                                activity.startActivityForResult(new Intent(activity, CaptureActivity.class), 0);

                            }else {
                                Toast.makeText(activity, "提交失败", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss     ");
                Date curDate=new Date(System.currentTimeMillis());//获取当前时间
                str=formatter.format(curDate);
                JSONArray ClientKey = new JSONArray();
                JSONObject ob=new JSONObject();
                JSONObject json_ob=null;
                for (int i = 0; i <listdata.size(); i++) {
                    if(listdata!=null&&listdata.get(i).isChoose()){
                        status.add("0");
                    }else {
                        status.add("1");
                    }
                    try {
                        json_ob=new JSONObject();
                        json_ob.put("status", status.get(i));
                        json_ob.put("nurseid",chargeid.get(i) );
                        json_ob.put("remark",arrylist.get(i));
                        ClientKey.put(json_ob);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    ob.put("person",ClientKey);
                    ob.put("endtime",str);
                    ob.put("registerid",regster_id);
                    ob.put("starttime",starttime);
                    ob.put("nursetime",nurse_id);
                    ob.put("commentflag","0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                map.put("json",String.valueOf(ob));
                Log.i("ddfrrrrfg", "getParams: "+map);
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
    /**
     * 单选接口
     *
     * @param checkInterface
     */
    public void setCheckInterface(CheckInterface checkInterface) {
        this.checkInterface = checkInterface;  //初始化接口

    }
    public interface CheckInterface {
        void checkGroup(int position, boolean isChecked);
    }
    public class ViewHold {
        TextView  clean_tv,finish_tv,unfinish;
        EditText finish_edit;
        CheckBox finish_iv;
        ImageView unfinish_iv;
        RelativeLayout unfinish_rl;
    }
}
