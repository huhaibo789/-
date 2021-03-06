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
import com.example.chengduhome.Saomaresult;
import com.example.chengduhome.SuosuoActivity;
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
 * Created by Administrator on 2018/1/11.
 */

public class sousuoadapter extends BaseAdapter {
    //定义hashMap 用来存放之前创建的每一项item
    HashMap<Integer, View> lmap = new HashMap<Integer, View>();
    private SuosuoActivity activity;
    private LayoutInflater inflater;
    private ArrayList<String> data;  //用户姓名
    private ArrayList<String> data1;  //用户性别
    private ArrayList<String> data2;   //用户时间
    private ArrayList<String> data3;   //用户任务
    private ArrayList<String> data4;   //用户id
    private ArrayList<String> data5;   //用户id
    private RequestQueue queue;
    int weizhi;
    public sousuoadapter (SuosuoActivity context,ArrayList<String> data,ArrayList<String> data1,
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
        final sousuoadapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.activity_listitem, parent, false);
            holder = new sousuoadapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (sousuoadapter.ViewHolder) convertView.getTag();
        }
        holder.name_tv.setText(data.get(position));
        holder.user_sex.setText(data1.get(position));
        holder.user_time.setText(data2.get(position));
        if(!data3.get(position).equals("null")){
            holder.start_tv.setText("点击急救");
            holder.start_tv.setTextColor(Color.parseColor("#0a21ea"));
        }
        holder.start_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity,Saomaresult.class);
                intent.putExtra("resgesid",data4.get(position));
                activity.startActivity(intent);
            }
        });
        return convertView;
    }
    //初始化控件
    class ViewHolder {
        TextView name_tv,user_sex,user_time,start_tv;
        public ViewHolder(View itemView) {
            name_tv= (TextView) itemView.findViewById(R.id.name_tv);
            start_tv= (TextView) itemView.findViewById(R.id.start_tv);
            user_sex= (TextView) itemView.findViewById(R.id.user_sex);
            user_time= (TextView) itemView.findViewById(R.id.user_time);
        }

    }


}
