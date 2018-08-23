package com.example.chengduhome;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import util.Api;
import util.Constant;
import util.UploadFileTask;

public class OldermanActivity extends AppCompatActivity {
    @Bind(R.id.title_rl)
    RelativeLayout titleRl;
    @Bind(R.id.img_iv)
    ImageView imgIv;
    @Bind(R.id.name_tv)
    TextView nameTv;
    @Bind(R.id.sex_tv)
    TextView sexTv;
    @Bind(R.id.xian_view)
    View xianView;
    @Bind(R.id.pass_dask)
    RelativeLayout passDask;
    @Bind(R.id.xian_view1)
    View xianView1;
    @Bind(R.id.save_tv)
    TextView saveTv;
    @Bind(R.id.today_task)
    TextView todayTask;
    @Bind(R.id.reason_edit)
    EditText reasonEdit;
    @Bind(R.id.fanhui_iv)
    ImageView fanhuiIv;
    @Bind(R.id.evluate_tv)
    TextView evluateTv;
    @Bind(R.id.gridView1)
    GridView gridView1;
    private RequestQueue queue;
    String resid;
    String ids;
    String datetime;
    private ImageLoader loader;
    private String pathImage;                       //选择图片路径
    private Bitmap bmp;                               //导入临时图片
    private ArrayList<HashMap<String, Object>> imageItem;
    private SimpleAdapter simpleAdapter;     //适配器
    private final int IMAGE_OPEN = 1;        //打开图片标记
    private List<String> list;
    String uploadFile = "";
    String request,sfcl,userid,bjid,rwid;
    String str,newtime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olderman);
        Intent intent = getIntent();
        userid=intent.getStringExtra("userid");
        resid = intent.getStringExtra("resid");
        datetime = intent.getStringExtra("datetime");
        sfcl=intent.getStringExtra("sfcl");
        bjid=intent.getStringExtra("bjid");
        rwid=intent.getStringExtra("rwid");
        ButterKnife.bind(this);
        init();
        getvolley();
        loader = ((App) getApplication()).getLoader();
        setlistener();
    }

    private void init() {
        list = new ArrayList<>();
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gridview_addpic); //加号
        imageItem = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemImage", bmp);
        imageItem.add(map);
        simpleAdapter = new SimpleAdapter(OldermanActivity.this,
                imageItem, R.layout.nav_header_main,
                new String[]{"itemImage"}, new int[]{R.id.imageView});
        /*
         * HashMap载入bmp图片在GridView中不显示,但是如果载入资源ID能显示 如
         * map.put("itemImage", R.drawable.img);
         * 解决方法:
         *              1.自定义继承BaseAdapter实现
         *              2.ViewBinder()接口实现
         *  参考 http://blog.csdn.net/admin_/article/details/7257901
         */
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView i = (ImageView) view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        gridView1.setAdapter(simpleAdapter);
        /*
         * 监听GridView点击事件
         * 报错:该函数必须抽象方法 故需要手动导入import android.view.View;
         */
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == 0 && imageItem.size() == 4) { //第一张为默认图片
                    Toast.makeText(OldermanActivity.this, "图片数3张已满", Toast.LENGTH_SHORT).show();
                } else if (position == 0 && imageItem.size() != 4) { //点击图片位置为+ 0对应0张图片
                    Toast.makeText(OldermanActivity.this, "添加图片", Toast.LENGTH_SHORT).show();
                    //选择图片
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_OPEN);
                    //通过onResume()刷新数据
                } else {
                    dialog(position);
                    //Toast.makeText(MainActivity.this, "点击第" + (position + 1) + " 号图片",
                    //		Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    //刷新图片
    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(pathImage)) {
            Bitmap addbmp = BitmapFactory.decodeFile(pathImage);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", addbmp);
            imageItem.add(map);
            simpleAdapter = new SimpleAdapter(this,
                    imageItem, R.layout.nav_header_main,
                    new String[]{"itemImage"}, new int[]{R.id.imageView});
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    // TODO Auto-generated method stub
                    if (view instanceof ImageView && data instanceof Bitmap) {
                        ImageView i = (ImageView) view;
                        i.setImageBitmap((Bitmap) data);
                        return true;
                    }
                    return false;
                }
            });
            gridView1.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
            //刷新后释放防止手机休眠后自动添加
            pathImage = null;
        }
    }
    //获取图片路径 响应startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //打开图片
        if (resultCode == RESULT_OK && requestCode == IMAGE_OPEN) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                //查询选择图片
                Cursor cursor = getContentResolver().query(
                        uri,
                        new String[]{MediaStore.Images.Media.DATA},
                        null,
                        null,
                        null);
                //返回 没找到选择图片
                if (null == cursor) {
                    return;
                }
                //光标移动至开头 获取图片路径
                cursor.moveToFirst();
                pathImage = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));
                list.add(pathImage);
            }
        }  //end if 打开图片
    }
    /*
    * Dialog对话框提示用户删除操作
    * position为删除图片位置
    */
    protected void dialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(OldermanActivity.this);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                imageItem.remove(position);
                simpleAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void getvolley() {
        queue = Volley.newRequestQueue(OldermanActivity.this);
        String url = Api.passemergency;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        Log.i("返回数据9", "onResponse: " + str);
                        try {
                            JSONArray jsonary=new JSONArray(str);
                            for (int i = 0; i <jsonary.length() ; i++) {
                                JSONObject jsonb=jsonary.getJSONObject(i);
                                nameTv.setText("姓名:" + jsonb.getString("name"));
                                sexTv.setText("性别:" + jsonb.getString("sex"));
                                loader.loadImage(Api.BASEURL+jsonb.getString("imgUrl"),new SimpleImageLoadingListener(){
                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        super.onLoadingComplete(imageUri, view, loadedImage);
                                        imgIv.setImageBitmap(loadedImage);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                       /* try {
                            JSONObject jsonob = new JSONObject(str);
                            nameTv.setText("姓名:" + jsonob.getString("name"));
                            sexTv.setText("性别:" + jsonob.getString("sex"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                      /*  try {
                            JSONArray jsonary = new JSONArray(str);
                            for (int i = 0; i < jsonary.length(); i++) {
                                JSONObject jsonobject = jsonary.getJSONObject(i);
                                JSONObject jsonject = jsonobject.getJSONObject("emergency");
                                ids = jsonject.getString("id");
                                nameTv.setText("姓名:" + jsonobject.getString("name"));
                                sexTv.setText("性别：" + jsonobject.getString("sex"));
                               *//* loader.loadImage(jsonobject.getString("imgUrl"),new SimpleImageLoadingListener(){
                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        super.onLoadingComplete(imageUri, view, loadedImage);
                                        imgIv.setImageBitmap(loadedImage);
                                    }
                                });*//*
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OldermanActivity.this, "网络连接出错", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("registerid", resid);
                map.put("sfcl", sfcl);//是否处理
                map.put("bjid",bjid);//报警id
                //map.put("registerid",olderid);
               /* map.put("offset", String.valueOf(currentpage));
                map.put("status", "0"); //完成
                map.put("length", "10");*/
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
        saveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reasonEdit.getText().toString()!=null){
                    try {
                        str = new String(reasonEdit.getText().toString().trim().getBytes("utf-8"), "ISO-8859-1");
                        changetime();
                        request=Api.miszhangemergency+"?content="+str+"&registerid="+resid+"&datetime="+newtime+"&userid="+userid+"&rwid="+rwid+"&bjid="+bjid;
                        postimg();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
        fanhuiIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        passDask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OldermanActivity.this, OlderdetailsActivity.class);
                intent.putExtra("resid", resid);
                intent.putExtra("datetime", datetime);
                startActivity(intent);
            }
        });
    }

    private void changetime() {
        String[] strArray = datetime.split(" ");
        String time = strArray[strArray.length - 1];  //时间
        String  isdatae = strArray[strArray.length - 2];  //日期
        String[] strArray3 = time.split(":");
        String[] strArray1 = isdatae.split("-");
        String day = strArray1[strArray.length];  //日
        String month = strArray1[strArray.length - 1]; //月
        String year = strArray1[strArray.length - 2]; //年
        String  second = strArray3[strArray.length];//秒
        String  minute= strArray3[strArray.length - 1];//分
        String  hour = strArray3[strArray.length - 2]; //时
        newtime=year+","+month+","+day+"/"+hour+","+minute+","+second;  //拼接出来新时间
    }

    private void postimg() {
        if(list.size()==0){
          postdata();
        }else {
            for (int i = 0; i < list.size(); i++) {
                uploadFile = list.get(i);
                System.out.println(uploadFile);
                if (uploadFile != null && uploadFile.length() > 0) {
                    UploadFileTask uploadFileTask = new UploadFileTask(this,reasonEdit.getText().toString(),request);
                    uploadFileTask.execute(uploadFile);
                }
            }
        }
    }

    private void postdata() {
        queue = Volley.newRequestQueue(OldermanActivity.this);
        String url = Api.miszhangemergency;
        final StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        try {
                            JSONObject jsob = new JSONObject(str);
                            String isflag = jsob.getString("flag");
                            if (isflag.equals("true")) {
                                Toast.makeText(OldermanActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OldermanActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(OldermanActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                /*    map.put("nurseid","9220171023102815");  //用户id
                    map.put("content","qq");
                    map.put("registerid","1509088314936");
                    map.put("bz","1");*/

                map.put("registerid", resid);
                map.put("content", reasonEdit.getText().toString());
                map.put("datetime", datetime);
                map.put("userid",userid);
                map.put("rwid",rwid);
                map.put("bjid",bjid);
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
