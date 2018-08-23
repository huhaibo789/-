package util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Administrator on 2018/1/3.
 */
public class UploadFileTask extends AsyncTask<String, Void, String> {
    String requestURL;
    /**
     * 可变长的输入参数，与AsyncTask.exucute()对应
     */
    private String shuru;
    private String grade;
    private ProgressDialog pdialog;
    private Activity context = null;

    public UploadFileTask(Activity ctx,String shuru,String requestURL) {
        this.context = ctx;
        this.shuru=shuru;
        this.requestURL=requestURL;
        pdialog = ProgressDialog.show(context, "正在加载...", "系统正在处理您的请求");
    }
    @Override
    protected void onPostExecute(String result) {
        // 返回HTML页面的内容
        pdialog.dismiss();
        if (UploadUtils.SUCCESS.equalsIgnoreCase(result)) {
            Toast.makeText(context, "上传成功!", Toast.LENGTH_SHORT).show();
            context.finish();
        }else{
            Toast.makeText(context, "上传失败!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onPreExecute() {
    }
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
    @Override
    protected String doInBackground(String... params) {
        File file = new File(params[0]);
        return UploadUtils.uploadFile(file,requestURL,shuru);
    }
    @Override
    protected void onProgressUpdate(Void... values) {
    }

}
