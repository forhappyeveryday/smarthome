package com.mobile.server.takephoto.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;

import com.mobile.server.config.Config;
import com.mobile.server.utils.Log;

public class ComFun {
	private static final String TAG = "ComFun";
    private static String genFileNameByCurrentTime(){
        Date date = new Date();
        DateFormat fmt = new SimpleDateFormat("yyyyMMddhhmmssS");
        return fmt.format(date) + ".jpg";
    }
    public static String savePhoto(Bitmap bm){
		Log.d(TAG, "enter savePhoto");
    	// ����һ��λ��SD���ϵ��ļ�
//    	String storeFileName = Environment.getExternalStorageDirectory().  
//                + "/"  
//                + genFileNameByCurrentTime();
		String saveFolder = Config.getInstance().getSavePicPath();
		if(!saveFolder.endsWith("/")){
			saveFolder += "/";
		}
		String storeFileName = saveFolder + genFileNameByCurrentTime();
		Log.d(TAG, "storeFileName:" + storeFileName);
        File file = new File(storeFileName);  
        FileOutputStream  fileOutStream=null;  
        try {  
            fileOutStream=new FileOutputStream(file);  
            //��λͼ�����ָ�����ļ���  
            Bitmap smallBm = small(bm);
            smallBm.compress(CompressFormat.JPEG, 50, fileOutStream);  
//            bm.compress(CompressFormat.PNG, 30, fileOutStream);   
//            bm.compress(CompressFormat.JPEG, 0, fileOutStream);  
            return storeFileName;
        } catch (IOException io) {  
            io.printStackTrace();  
        }  finally {
        	if(fileOutStream != null){
        		try {
					fileOutStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
        return null;
    }
    
    public static Bitmap small(Bitmap bitmap) {
    	  Matrix matrix = new Matrix();
    	  matrix.postScale(0.5f,0.5f); //���Ϳ�Ŵ���С�ı���
    	  Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    	  return resizeBmp;
    	 }
}
