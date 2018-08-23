package adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.example.chengduhome.HelpermessageActivity;
import com.example.chengduhome.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/12/21.
 */

public class helpmessageadapter extends BaseAdapter {
    //定义hashMap 用来存放之前创建的每一项item
    HashMap<Integer, View> lmap = new HashMap<Integer, View>();
    private HelpermessageActivity activity;
    private LayoutInflater inflater;
    private ArrayList<String> data;  //用户姓名
    private ArrayList<String> data1;  //用户性别
    private ArrayList<String> data2;   //用户时间
    private ArrayList<String> data3;   //用户状态
    String unfin;
    int weizhi;//记录Position
    private RequestQueue queue;
    public helpmessageadapter (HelpermessageActivity context,ArrayList<String> data,ArrayList<String> data1,
                            ArrayList<String> data2,ArrayList<String> data3){
        super();
        this.activity=context;
        this.data=data;
        this.data1=data1;
        this.data2=data2;
        this.data3=data3;
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
        final helpmessageadapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.activity_listitem, parent, false);
            holder = new helpmessageadapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (helpmessageadapter.ViewHolder) convertView.getTag();
        }
        holder.name_tv.setText(data.get(position));
        holder.user_sex.setText(data1.get(position));
        holder.user_time.setText(data2.get(position));
        if(data3.get(position).equals("0")){
            holder.start_tv.setText("未处理");
            holder.start_tv.setTextColor(Color.parseColor("#0a21ea"));
        }else if(data3.get(position).equals("1")){
            holder.start_tv.setText("已处理");
            holder.start_tv.setTextColor(Color.parseColor("#FFCAC6C6"));
        }else if(data3.get(position).equals("2")){
            holder.start_tv.setText("处理中");
            holder.start_tv.setTextColor(Color.parseColor("#0a21ea"));
        }
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
