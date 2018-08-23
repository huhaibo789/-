package adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chengduhome.OldermanActivity;
import com.example.chengduhome.PasstaskActivity;
import com.example.chengduhome.R;
import com.example.chengduhome.ServiceActivity;


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

public class serviceadapter extends BaseAdapter {
    //定义hashMap 用来存放之前创建的每一项item
    HashMap<Integer, View> lmap = new HashMap<Integer, View>();
    private ServiceActivity activity;
    private LayoutInflater inflater;
    private ArrayList<String> data;  //用户姓名
    private ArrayList<String> data1;  //用户性别
    private ArrayList<String> data2;   //用户时间
    private ArrayList<String> data3;   //用户状态
    private ArrayList<String> data4;   //老人id
    private ArrayList<String> data5;   //任务id
    private ArrayList<String> data6;   //报警id
    String userid;
    int weizhi;//记录Position
    private RequestQueue queue;
    public serviceadapter (ServiceActivity context,ArrayList<String> data,ArrayList<String> data1,
                            ArrayList<String> data2,ArrayList<String> data3,ArrayList<String> data4,ArrayList<String> data5,ArrayList<String> data6,String userid){
        super();
        this.activity=context;
        this.data=data;
        this.data1=data1;
        this.data2=data2;
        this.data3=data3;
        this.data4=data4;
        this.data5=data5;
        this.data6=data6;
        this.userid=userid;
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
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.activity_newlistitem, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
       /* serviceadapter.ViewHold vh = null;
        if(lmap.get(position)==null){
            convertView=View.inflate(activity, R.layout.activity_twoservice,null);
            vh=new serviceadapter.ViewHold();
            convertView.setTag(vh);
            lmap.put(position,convertView);
            vh.name_tv= (TextView) convertView.findViewById(R.id.name_tv);
            vh.start_tv= (TextView) convertView.findViewById(R.id.start_tv);
            vh.user_sex= (TextView) convertView.findViewById(R.id.user_sex);
            vh.user_time= (TextView) convertView.findViewById(R.id.user_time);
            vh.showmessage_rl= (RelativeLayout) convertView.findViewById(R.id.showmessage_rl);
        }else {
            convertView = lmap.get(position);
            vh= (serviceadapter.ViewHold) convertView.getTag();
        }*/

        holder.name_tv.setText(data.get(position));
        holder.user_sex.setText(data1.get(position));
        holder.user_time.setText(data2.get(position));
        if(data3.get(position).equals("0")){
            holder.start_tv.setText("未处理");
        }else if(data3.get(position).equals("1")){
           holder.start_tv.setText("已处理");
        }else {
            holder.start_tv.setText("处理中");
        }
        if(data3.get(position).equals("1")||data3.get(position).equals("2")){
            holder.showmessage_rl.setBackgroundColor(Color.parseColor("#00323232"));
            holder.start_tv.setTextColor(Color.parseColor("#FFCAC6C6"));
        }else  {
            holder.showmessage_rl.setBackgroundColor(Color.parseColor("#FFFA0101"));
            holder.start_tv.setTextColor(Color.parseColor("#0a21ea"));
        }
        holder.start_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data3.get(position).equals("0")){
                    // 建造者模式，将对象初始化的步骤抽取出来，让建造者来实现，设置完所有属性之后再创建对象
                    // 这里使用的Context必须是Activity对象
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    // 链式编程
                    builder.setTitle("提示")
                            .setMessage("是否接受任务")
                            .setPositiveButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    weizhi=position;
                                    postdata();
                                }
                            })
                            .setNegativeButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent=new Intent(activity, OldermanActivity.class);
                                    intent.putExtra("userid",userid);
                                    intent.putExtra("sfcl",data3.get(position));
                                    intent.putExtra("datetime",data2.get(position));
                                    intent.putExtra("resid",data4.get(position));
                                    intent.putExtra("bjid",data6.get(position));
                                    intent.putExtra("rwid",data5.get(position));
                                    activity.startActivity(intent);
                                  /*  if(data3.get(position).equals("1")||data3.get(position).equals("2")){
                                        Intent intent=new Intent(activity,PasstaskActivity.class);
                                        intent.putExtra("datetime",data2.get(position));
                                        intent.putExtra("resid",data4.get(position));
                                        activity.startActivity(intent);
                                    }else {
                                        Intent intent=new Intent(activity, OldermanActivity.class);
                                        intent.putExtra("userid",userid);
                                        intent.putExtra("sfcl",data3.get(position));
                                        intent.putExtra("datetime",data2.get(position));
                                        intent.putExtra("resid",data4.get(position));
                                        activity.startActivity(intent);
                                    }*/
                                }
                            })
                            .setCancelable(false); // 能否通过点击对话框以外的区域（包括返回键）关闭对话框
                    // 通过建造者创建Dialog对象
                    // AlertDialog dialog = builder.create();
                    // dialog.show();
                    // 以上两行代码可以简化成以下这一行代码
                    builder.show();
                }else {
                    Intent intent=new Intent(activity,PasstaskActivity.class);
                    intent.putExtra("datetime",data2.get(position));
                    intent.putExtra("resid",data4.get(position));
                    intent.putExtra("bjid",data6.get(position));
                    activity.startActivity(intent);
                }

            }
        });
        return convertView;
    }
    private void postdata() {
        queue = Volley.newRequestQueue(activity);
        String url = Api.refusetask;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        try {
                            JSONObject jsonob=new JSONObject(str);
                            String flag=jsonob.getString("flag");
                            if(flag.equals("1")){
                                Toast.makeText(activity, "取消成功", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity, "服务器出错", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("bjid",data6.get(weizhi));
                map.put("zyzid",userid);
                map.put("rwid",data5.get(weizhi));
                map.put("wczt",data3.get(weizhi));
                map.put("jzbz","急救");  //急救备注
                map.put("sfjhc","0");    //是否叫救护车 0不叫，1叫
                Log.i("showload", "getParams: "+weizhi+map);
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

    //初始化控件
    class ViewHolder {
        TextView  name_tv,user_sex,user_time,start_tv;
        RelativeLayout showmessage_rl;

        public ViewHolder(View itemView) {
            name_tv= (TextView) itemView.findViewById(R.id.name_tv);
            start_tv= (TextView) itemView.findViewById(R.id.start_tv);
            user_sex= (TextView) itemView.findViewById(R.id.user_sex);
            user_time= (TextView) itemView.findViewById(R.id.user_time);
            showmessage_rl= (RelativeLayout) itemView.findViewById(R.id.showmessage_rl);
        }

    }
}
