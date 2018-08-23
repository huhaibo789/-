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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import util.Api;
import util.Constant;
import util.FileUtils;
import util.UploadFileTask;

public class Saomaresult extends AppCompatActivity implements View.OnClickListener {
    private final int IMAGE_OPEN = 1;        //打开图片标记
    @Bind(R.id.return_show)
    ImageView returnShow;
    @Bind(R.id.saveimg_tv)
    TextView saveimgTv;
    @Bind(R.id.edit_text)
    EditText editText;
    @Bind(R.id.gridView1)
    GridView gridView1;
    private String pathImage;                       //选择图片路径
    private Bitmap bmp;                               //导入临时图片
    private ArrayList<HashMap<String, Object>> imageItem;
    private SimpleAdapter simpleAdapter;     //适配器
    String uploadFile = "";
    String request;
    private List<String> list;
    private RequestQueue queue;
    String resid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saomaresult);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        resid=intent.getStringExtra("resgesid");
        returnShow.setOnClickListener(this);
        saveimgTv.setOnClickListener(this);
        list = new ArrayList<>();
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gridview_addpic); //加号
        imageItem = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemImage", bmp);
        imageItem.add(map);
        simpleAdapter = new SimpleAdapter(Saomaresult.this,
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
                    Toast.makeText(Saomaresult.this, "图片数3张已满", Toast.LENGTH_SHORT).show();
                } else if (position == 0 && imageItem.size() != 4) { //点击图片位置为+ 0对应0张图片
                    Toast.makeText(Saomaresult.this, "添加图片", Toast.LENGTH_SHORT).show();
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

    /*
     * Dialog对话框提示用户删除操作
     * position为删除图片位置
     */
    protected void dialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Saomaresult.this);
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
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_show:
                finish();
                break;
            case R.id.saveimg_tv:
                if(list.size()==0){
                    postshuju();
                }else {
                    if(editText.getText().toString()!=null){
                        FileUtils files=new FileUtils();
                        String file_userid=files.readDataFromFile(Saomaresult.this);
                        if(resid!=null){
                            request=Api.hugonghelp+"?content="+editText.getText().toString().trim()+"&nurseid="+file_userid+"&registerid="+resid;
                        }else {
                            request=Api.hugonghelp+"?content="+editText.getText().toString().trim()+"&nurseid="+file_userid+"&registerid=";
                        }
                        for (int i = 0; i < list.size(); i++) {
                            uploadFile = list.get(i);
                            System.out.println(uploadFile);
                            if (uploadFile != null && uploadFile.length() > 0) {
                                UploadFileTask uploadFileTask = new UploadFileTask(this,editText.getText().toString(),request);
                                uploadFileTask.execute(uploadFile);
                            }

                        }
                    }

                }
                break;
        }
    }
    private void postshuju() {
        queue = Volley.newRequestQueue(Saomaresult.this);
        String url = Api.hugonghelp;
        StringRequest post = new StringRequest(
                StringRequest.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.toString().trim();
                        Log.i("khfrr", "onResponse: "+str);
                        try {
                            JSONObject json=new JSONObject(str);
                            String flag=json.getString("flag");
                            if(flag.equals("true")){
                                Toast.makeText(Saomaresult.this, "上传成功", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(Saomaresult.this, "上传失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Saomaresult.this, "请检查网络", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            //通过重写此对象的getParams方法设置请求条件
            //将所有的请求条件存入返回值的map对象中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                String advice = editText.getText().toString();
                FileUtils files=new FileUtils();
                String file_userid=files.readDataFromFile(Saomaresult.this);
                if(file_userid!=null){
                    map.put("nurseid",file_userid);  //用户id
                }else {
                    map.put("nurseid","");
                }
                map.put("content", advice); //服务时间
                if(resid!=null){
                    map.put("registerid",resid);
                }else {
                    map.put("registerid","");
                }
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
