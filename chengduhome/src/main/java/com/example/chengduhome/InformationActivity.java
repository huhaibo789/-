package com.example.chengduhome;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.githang.statusbar.StatusBarCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import util.CustomDatePicker;

public class InformationActivity extends AppCompatActivity {
    private RelativeLayout title_rl,girl_rl,boy_rl;
    private EditText edit_tv,olderperson_edit,born_edit,yuyue_edit;
    private ImageView sex_boyiv,sex_girliv;
    private String gender="no";
    private View v_city;
    private String meettime;
    private ListView list_worker;
    private Button quary_button;
    private CustomDatePicker customDatePicker1, customDatePicker2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        initview();
        initDatePicker();
        setlistener();
        StatusBarCompat.setStatusBarColor(InformationActivity.this, Color.parseColor("#0aec68"), false);
    }
    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                if(meettime.equals("1")){
                    yuyue_edit.setText(time.split(" ")[0]);
                }else if(meettime.equals("2")){
                    born_edit.setText(time.split(" ")[0]);
                }else {
                    olderperson_edit.setText(time.split(" ")[0]);
                }
            }
        }, "1900-01-01 00:00", "2060-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(false); // 不显示时和分
        customDatePicker1.setIsLoop(false); // 不允许循环滚动
    }
    private void setlistener() {
        quary_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                senddata();
            }
        });
        olderperson_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meettime="3";
                showpop();
            }
        });
        yuyue_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meettime="1";
                showpop();
            }
        });
        born_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meettime="2";
                showpop();
            }
        });
        title_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        boy_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gender.equals("no")){
                    sex_boyiv.setImageResource(R.drawable.chose);
                    sex_girliv.setImageResource(R.drawable.nochose);
                    gender="boy";
                }else if(gender.equals("girl")){
                    sex_boyiv.setImageResource(R.drawable.chose);
                    sex_girliv.setImageResource(R.drawable.nochose);
                    gender="boy";
                }
            }
        });
        girl_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gender.equals("no")){
                    sex_boyiv.setImageResource(R.drawable.nochose);
                    sex_girliv.setImageResource(R.drawable.chose);
                    gender="girl";
                }else if(gender.equals("boy")){
                    sex_boyiv.setImageResource(R.drawable.nochose);
                    sex_girliv.setImageResource(R.drawable.chose);
                    gender="girl";
                }
            }
        });
    }
    private void showpop() {
        // 日期格式为yyyy-MM-dd
        customDatePicker1.show(yuyue_edit.getText().toString());
    }
    private void senddata() {
        String editname=edit_tv.getText().toString().trim();//用户姓名
        String appoinment=yuyue_edit.getText().toString().trim();//结束时间
        String borntime=born_edit.getText().toString().trim();//出生时间
        String oldercard=olderperson_edit.getText().toString().trim();//开始时间
        Intent intent=new Intent(InformationActivity.this,SupportworkActivity.class);
        intent.putExtra("editname",editname);  //姓名
        intent.putExtra("appoinment",appoinment);
        intent.putExtra("borntime",borntime);
        intent.putExtra("oldercard",oldercard);
        intent.putExtra("gender",gender);
        startActivity(intent);
    }
    private void initview() {
        boy_rl= (RelativeLayout) findViewById(R.id.boy_rl);
        girl_rl= (RelativeLayout) findViewById(R.id.girl_rl);
        list_worker= (ListView) findViewById(R.id.list_worker);
        yuyue_edit= (EditText) findViewById(R.id.yuyue_edit);
        born_edit= (EditText) findViewById(R.id.born_edit);
        title_rl= (RelativeLayout) findViewById(R.id.title_rl);
        edit_tv= (EditText) findViewById(R.id.edit_tv);
        olderperson_edit= (EditText) findViewById(R.id.olderperson_edit);
        sex_boyiv= (ImageView) findViewById(R.id.sex_boyiv);
        sex_girliv= (ImageView) findViewById(R.id.sex_girliv);
        quary_button= (Button) findViewById(R.id.quary_button);
    }
}
