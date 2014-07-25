package com.mobile.server.location;

import android.content.Context;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.mobile.server.utils.Log;

public class LocationService {
	private static final String TAG = "LocationService";
	public static LocationClient mLocationClient = null;
	public static BDLocationListener myListener = null;
	public static void init(Context context){
		//ÿ��������ʱ���ϱ��µ���λ�ð�
		startWork(context);
	}
	public static void startWork(Context context){
		Log.d(TAG, "enter LocationService::init()");
		if(null != mLocationClient && mLocationClient.isStarted()){
			stopWork();
		}
		myListener = new MyLocationListener();
		mLocationClient = new LocationClient(context.getApplicationContext()); //����LocationClient��
		mLocationClient.registerLocationListener( myListener ); //ע���������

	    LocationClientOption option = new LocationClientOption();
	    option.setLocationMode(LocationMode.Battery_Saving);//���ö�λģʽ
	    option.setCoorType("bd09ll");//���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
	    option.setScanSpan(10000);
//	    option.setScanType(5000);//���÷���λ����ļ��ʱ��Ϊ5000ms
	    option.setIsNeedAddress(true);//���صĶ�λ���������ַ��Ϣ
	    option.setNeedDeviceDirect(true);//���صĶ�λ��������ֻ���ͷ�ķ���
	    mLocationClient.setLocOption(option);

	    mLocationClient.start();
	    startLocation();
	}

	public static void stopWork(){
		Log.d(TAG, "enter LocationService::stopWork");
		if(null != mLocationClient){
			mLocationClient.unRegisterLocationListener(myListener);
			myListener = null;
			mLocationClient = null;
		}
	}

	private static void startLocation(){
		Log.d(TAG, "enter LocationService::startLocation()");
	    if (mLocationClient != null && mLocationClient.isStarted()){
	    	Log.d(TAG, "begin to request location");
	    	mLocationClient.requestLocation();
	    	mLocationClient.requestOfflineLocation();
	    }else {
	        Log.d(TAG, "locClient is null or not started");
	    }
	}
}
