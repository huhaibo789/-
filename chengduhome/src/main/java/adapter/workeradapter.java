package adapter;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.example.chengduhome.SupportworkActivity;
import com.zxing.qrcode.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import util.Api;
import util.Constant;

/**
 * Created by Administrator on 2017/8/7.
 */

public class workeradapter extends BaseAdapter {
    //定义hashMap 用来存放之前创建的每一项item
    HashMap<Integer, View> lmap = new HashMap<Integer, View>();
    private SupportworkActivity activity;
    private LayoutInflater inflater;
    private ArrayList<String> data;  //用户姓名
    private ArrayList<String> data1;  //用户性别
    private ArrayList<String> data2;   //用户时间
    private ArrayList<String> data3;   //用户任务
    private ArrayList<String> data4;   //用户id
    private ArrayList<String> data5;   //用户id
    private RequestQueue queue;
    int weizhi;
    public workeradapter (SupportworkActivity context,ArrayList<String> data,ArrayList<String> data1,
                          ArrayList<String> data2,ArrayList<String> data3,ArrayList<String> data4,ArrayList<String> data5){
        super();
        this.activity=context;
        this.data=data;
        this.data1=data1;
        this.data2=data2;
        this.data3=data3;
        this.data4=data4;
        this.data5=data5;
    }
    public  void  setnotify(String olderid){
      /*  if(data5.get(1)!=null){
            Toast.makeText(activity, data5.get(1), Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(activity, "位置为空", Toast.LENGTH_SHORT).show();
        }*/
     if(data5.get(weizhi).equals(olderid)){
            Intent intent=new Intent(activity, FinishActivity.class);
            intent.putExtra("resid",data4.get(weizhi));
            intent.putExtra("nurtime",data2.get(weizhi));
            intent.putExtra("cardid",data5.get(weizhi));
            activity.startActivity(intent);
        }else {
            Toast.makeText(activity, "老人卡信息不符", Toast.LENGTH_SHORT).show();
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
        final workeradapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.activity_listitem, parent, false);
            holder = new workeradapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (workeradapter.ViewHolder) convertView.getTag();
        }
        holder.name_tv.setText(data.get(position));
        holder.user_sex.setText(data1.get(position));
        holder.user_time.setText(data2.get(position));
        if(data3.get(position).equals("0")){
            holder.start_tv.setText("未完成");
            holder.start_tv.setTextColor(Color.parseColor("#0a21ea"));
        }else if(data3.get(position).equals("1")){
            holder.start_tv.setText("已完成");
            holder.start_tv.setTextColor(Color.parseColor("#FFCAC6C6"));
        }else if(data3.get(position).equals("2")){
            holder.start_tv.setText("服务中");
            holder.start_tv.setTextColor(Color.parseColor("#0a21ea"));
        }
        holder.start_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!data3.get(position).equals("1")){
                  /*  Intent intent=new Intent(activity, FinishActivity.class);
                    intent.putExtra("resid",data4.get(position));
                    intent.putExtra("nurtime",data2.get(position));
                    activity.startActivity(intent);
                    Log.i("heiyussd", "onClick: "+data4.toString());
                    Log.i("heiyussd1", "onClick: "+data4.get(position)+""+position);
                    Log.i("heiyussd2", "onClick: "+data4.size());*/
                    activity.scan="false";
                    weizhi=position;
                    activity.startActivityForResult(new Intent(activity, CaptureActivity.class), 0);
                }

            }
        });

        return convertView;
    }
    //初始化控件
    class ViewHolder {
        TextView  name_tv,user_sex,user_time,start_tv;


        public ViewHolder(View itemView) {
            name_tv= (TextView) itemView.findViewById(R.id.name_tv);
            start_tv= (TextView) itemView.findViewById(R.id.start_tv);
            user_sex= (TextView) itemView.findViewById(R.id.user_sex);
            user_time= (TextView) itemView.findViewById(R.id.user_time);
        }

    }


}
