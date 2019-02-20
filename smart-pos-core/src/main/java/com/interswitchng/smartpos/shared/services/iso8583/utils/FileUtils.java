package com.interswitchng.smartpos.shared.services.iso8583.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static String[] getFilesArrayFromAssets(Context context, String path) {
        AssetManager assetManager = context.getResources().getAssets();
        String[] files = null;
        try {
            files = assetManager.list(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(files!=null) {
            for (int i = 0; i < files.length; i++) {
                files[i] = path + "/" + files[i];
            }
        }

        return files;

    }

    public static byte[] getFromAssets(Context context) {
        InputStream in = null;
        try {
            in = context.getResources().getAssets().open("jpos.xml");
            //获取文件的字节数
            int lenght = in.available();
            //创建byte数组
            byte[] buffer = new byte[lenght];
            //将文件中的数据读到byte数组中
            in.read(buffer);
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }
}
