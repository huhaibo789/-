package util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Administrator on 2017/8/15.
 */

public class FileUtilsname {
    /**
     * 将数据保存到文件
     */
    public static void saveDataToFile(Context context, String data) {
        try {
            // 内置存储
            // 在Android中，每一个应用都有一个自己对应的文件夹，只有该应用本身才能访问
            // 位置：data/data/包名/
            // 1、自己拼接路径（字符串）
            // String path = "data/data/"+context.getPackageName()+"/file.txt";
            // 2、直接通过SDK提供的API获取文件夹的路径
            // context.getFilesDir()获取的路径是：data/data/包名/files/
            // File path = new File(context.getFilesDir(), "file.txt");
            // 3、直接通过SDK提供的API获取文件对应的操作流
            // mode代表的是打开模式
            FileOutputStream fos = context.openFileOutput("test1.txt", Context.MODE_PRIVATE);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            // BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            bw.write(data);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readDataFromFile(Context context) {
        try {
            FileInputStream fis = context.openFileInput("test1.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String data = br.readLine();
            br.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
