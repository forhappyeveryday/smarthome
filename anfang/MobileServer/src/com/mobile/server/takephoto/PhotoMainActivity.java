package com.mobile.server.takephoto;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;

import com.mobile.server.R;
import com.mobile.server.config.Config;
import com.mobile.server.utils.Const;
import com.mobile.server.utils.Log;
import com.mobile.server.utils.ToastShow;

@SuppressLint("NewApi")
public class PhotoMainActivity extends InstrumentedActivity {
    private static final String TAG = "PhotoMainActivity";
    SurfaceView sView;
    SurfaceHolder surfaceHodler;
    int screenWidth, screenHeight;
    // ����ϵͳ���õ������
    Camera camera;
    // �Ƿ����Ԥ����
    boolean isPreview = false;

    String mailto = null;
    boolean mNeedExitAfterTakePhoto = false;

	public static boolean startTakePic(Context context){
		Intent takepicIntent = new Intent(Const.ACTION_SEND_PIC);
		takepicIntent.putExtra("quit", true);
		takepicIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try{
			context.startActivity(takepicIntent);
			return true;
		} catch (Exception e) {
			Log.e(TAG, e.toString(), e);
//			Toast.makeText(context, "send pic service is not installed", Toast.LENGTH_LONG).show();
			ToastShow.show(context, "send pic service is not installed", ToastShow.LENGTH_LONG);
		}
		return false;
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(null != intent){
        	Log.d(TAG, "getIntent is not null, maybe from intent start");
        	String mailtoTmp = intent.getStringExtra("mailto");
        	if(null != mailtoTmp && mailtoTmp.length() > 0){
        		mailto = mailtoTmp;
        	} else {
        		mailto = Config.getInstance().getEmailTo();
        	}

        	mNeedExitAfterTakePhoto = intent.getBooleanExtra("quit", false);
        }

        // ����ȫ��
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.photo_main);


//        SendMailService.sendEmail(this, "/mnt/sdcard/Download/480x762x75x0.jpg");
        initTakePhotoEnv();
    }
    @Override
    protected void onResume() {
    	super.onResume();
		JPushInterface.onResume(this);
    }
    @Override
    protected void onPause() {
    	super.onPause();
    	JPushInterface.onPause(this);
    }
    private void initTakePhotoEnv(){
        // ��ȡ���ڹ�����
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        // ��ȡ��Ļ�Ŀ�͸�
        display.getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        sView = (SurfaceView) findViewById(R.id.sView);
        // ����surface����Ҫ�Լ���ά��������
        sView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // ���SurfaceView��SurfaceHolder
        surfaceHodler = sView.getHolder();
        // ΪsrfaceHolder���һ���ص�������
        surfaceHodler.addCallback(new Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder arg0) {
                // ���camera��Ϊnull���ͷ�����ͷ
                releaseCameraAndPreview();
//                if (camera != null) {
//                    if (isPreview){
//                        camera.stopPreview();
//                    }
//                    camera.release();
//                    camera = null;
//                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder arg0) {
                // ������ͷ
                initCamera();

            }

            @Override
            public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
                    int arg3) {
            }
        });

    	Button bCapture = (Button) findViewById(R.id.take);
        bCapture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				capture(sView);
			}
		});
    }
    void getMsgFromeCaller(){
    	Intent in = getIntent();
    	if(null != in){
    		mNeedExitAfterTakePhoto = true;
    	}
    }

    @SuppressLint("NewApi")
	private void initCamera() {
        if (!isPreview) {
            // �˴�Ĭ�ϴ򿪺�������ͷ
            // ͨ������������Դ�ǰ������ͷ
        	try{
	            camera = Camera.open();
	            camera.setDisplayOrientation(90);
        	} catch (Exception e) {
        		Log.e(TAG, e.toString(), e);
        		finish();
        		return ;
        	}
        }
        if (!isPreview && camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            // ����Ԥ����Ƭ�Ĵ�С
            parameters.setPreviewSize(screenWidth, screenHeight);
            // ����Ԥ����Ƭʱÿ����ʾ����֡����Сֵ�����ֵ
            parameters.setPreviewFpsRange(4, 10);
            // ������Ƭ�ĸ�ʽ
            parameters.setPictureFormat(ImageFormat.JPEG);
            // ����JPG��Ƭ������
            parameters.set("jpeg-quality", 85);
            // ������Ƭ�Ĵ�С
            parameters.setPictureSize(screenWidth, screenHeight);
            // ͨ��SurfaceView��ʾȡ������
            try {
                camera.setPreviewDisplay(surfaceHodler);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // ��ʼԤ��
            camera.startPreview();
            isPreview = true;

            if(mNeedExitAfterTakePhoto){
            	capture(sView);
            }
        }
    }

    public void capture(View source) {
        if (camera != null) {
            // ��������ͷ�Զ��Խ��������
            camera.autoFocus(autoFocusCallback);
        } else {
        	Log.e(TAG, "when call capture, camera is not set!");
        }
    }

    AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
        	  Log.d(TAG, "enter AutoFocusCallback::onAutoFocus");
//            if (arg0) {
                // takePicture()������Ҫ����������������
                // ��һ�������������û����¿���ʱ�����ü�����
                // �ڶ������������������ȡԭʼ��Ƭʱ�����ü�����
                // ���������������������ȡJPG��Ƭʱ�����ü�����
                camera.takePicture(new ShutterCallback() {

                    @Override
                    public void onShutter() {
                        // ���¿���˲���ִ�д˴�����
                    }
                }, new PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] arg0, Camera arg1) {
                        // �˴�������Ծ����Ƿ���Ҫ����ԭʼ��Ƭ��Ϣ
                    }
                }, myJpegCallback);
            }

//        }
    };
    PictureCallback myJpegCallback = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        	Log.d(TAG, "enter PictureCallback::onPictureTaken");
            // �����������õ����ݴ���λͼ
            final Bitmap bm =  BitmapFactory.decodeByteArray(data, 0,
                    data.length);
            if(null == bm){
            	Log.e(TAG, "take a invaild pic");
            	return;
            }
            // ���ز����ļ�
            View saveDialog = getLayoutInflater().inflate(R.layout.photo_save, null);
            final EditText potoName = (EditText) saveDialog
                    .findViewById(R.id.photoNmae);
            // ��ȡsaveDialog�Ի����ϵ�ImageView���
            ImageView show = (ImageView) saveDialog.findViewById(R.id.show);
            // ��ʾ�ո��ĵõ���Ƭ
            show.setImageBitmap(bm);

            if(!mNeedExitAfterTakePhoto){
	            // ʹ��AlertDialog���
	            new AlertDialog.Builder(PhotoMainActivity.this)
	                    .setView(saveDialog)
	                    .setNegativeButton("ȡ��", null)
	                    .setPositiveButton("����",
	                            new DialogInterface.OnClickListener() {
	                                @Override
	                                public void onClick(DialogInterface arg0,
	                                        int arg1) {
//	                                	String saveName =  ComFun.savePhoto(bm);
	                                	if(bm.getByteCount() > 0){
//	                                		BackGroundService.sendEmail(getBaseContext(), mailto, saveName);
	                                		BackGroundService.sendEmail(getBaseContext(), mailto, bm);
	                                	}
	                                }
	                            }).show();
	            //�������
	            camera.stopPreview();
	            camera.startPreview();
	            isPreview=true;
            } else {
//            	String saveName =  ComFun.savePhoto(bm);
            	if(bm.getByteCount() > 0){
//            		BackGroundService.sendEmail(getBaseContext(), mailto, saveName);
            		BackGroundService.sendEmail(getBaseContext(), mailto, bm);
            	}
            	finish();
            }
        }
    };

    private void releaseCameraAndPreview() {
// 	   mPreview.setCamera(null);
 	   if (camera != null) {
 		   camera.stopPreview();
 		   camera.cancelAutoFocus();
 		   camera.release();
 		   camera = null;
 	   }
 	}
    @Override
	protected void onDestroy() {
    	releaseCameraAndPreview();
    	super.onDestroy();
    };
}