package com.mobile.server.location;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.mobile.server.utils.Tools;

public class PositionInfo {
	private static final String TAG = "PositionInfo";
	String time; //��λʱ��
//    61 �� GPS��λ���
//    62 �� ɨ�����϶�λ����ʧ�ܡ���ʱ��λ�����Ч��
//    63 �� �����쳣��û�гɹ���������������󡣴�ʱ��λ�����Ч��
//    65 �� ��λ����Ľ����
//    66 �� ���߶�λ�����ͨ��requestOfflineLocaiton����ʱ��Ӧ�ķ��ؽ��
//    67 �� ���߶�λʧ�ܡ�ͨ��requestOfflineLocaiton����ʱ��Ӧ�ķ��ؽ��
//    68 �� ��������ʧ��ʱ�����ұ������߶�λʱ��Ӧ�ķ��ؽ��
//    161�� ��ʾ���綨λ���
//    162~167�� ����˶�λʧ��
//    502��key��������
//    505��key�����ڻ��߷Ƿ�
//    601��key���񱻿������Լ�����
//    602��key mcode��ƥ��
//    501��700��key��֤ʧ��
	int locType;

	double latitude; //γ��
	double longitude; //����

	float radius; //��λ�ľ�ȷ�ȣ��뾶��Χ
	String addr; //λ��
	public PositionInfo(){}
	public PositionInfo(BDLocation dbLocation){
		this.time = dbLocation.getTime();
		this.locType = dbLocation.getLocType();
		this.latitude = dbLocation.getLatitude();
		this.longitude = dbLocation.getLongitude();
		this.radius = dbLocation.getRadius();
		this.addr = dbLocation.getAddrStr();
	}
	String toJsonStr(){
		try{
			JSONObject jsonObj =  toJson();
			return jsonObj.toString();
		} catch (Exception e) {
			Log.e(TAG, e.toString(), e);
		}
		return "";
	}
	JSONObject toJson() throws JSONException{
		JSONObject json = new JSONObject();
		json.put("time", time);
		json.put("locType", locType);
		json.put("latitude", latitude);
		json.put("longitude", longitude);
		json.put("radius", radius);
		json.put("addr", Base64.encodeToString(addr.getBytes(), Base64.URL_SAFE));
		return json;
	}
	public static PositionInfo getFromJSONStr(String jsonstr) throws JSONException{
		System.out.println("in LocationInfo jsonStr:" + jsonstr);
		JSONObject json = new JSONObject(jsonstr);
		PositionInfo location = new PositionInfo();
		location.time = json.getString("time");
		location.locType = json.getInt("locType");
		location.latitude = json.getDouble("latitude");
		location.longitude = json.getDouble("longitude");
		location.radius = (float) json.getDouble("radius");

		byte[] addrDatas = Base64.decode(json.getString("addr").getBytes(), Base64.URL_SAFE);
		location.addr = new String(addrDatas);
		return location;
	}

}
