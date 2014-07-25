package com.mobile.server.utils;


public class Command {
	private static final String TAG = "Command";


	//������紫��ָ�������ַ���ʹ��,bundle��keyֵ, ����Stringֵ��Ϊ���ݴ���������ģ�鴫���StringֵΪtakepictakepic����ʱ
	public static String CMD_TYPE = "cmdStr";

	public static final int CMD_NONE = 0; //Ĭ����Ч������
	public static final int CMD_51_CALL_SEND_PIC = 101000;
	public static final int CMD_CONNECT_BLUETH = 101001;
	public static final int CMD_DISCONNECT_BLUETH = 101002;
	public static final int CMD_SYSTEM_STOP_SERVICE = 101100; //ֹͣ����
	public static final int CMD_SYSTEM_START_SERVICE = 101101; //ֹͣ����
	public static final int CMD_SYSTEM_GET_STATE = 101102;    //��ȡϵͳ״̬

	public static final int CMD_REGIST_PUSH = 1002001; //Pushע����Ϣ
	public static final int CMD_LOCATION = 1002002; //��λ��Ϣ
	public static final int CMD_UPDATE_NICKNAME = 1002003; //�޸����û��ǳ�


	public static final int CMD_REQUEST_UPDATE_DEVICES = 1002002; //�ⲿ����λ��Ϣ

	public static final String CMD_51_CALL_SEND_PIC_STR = "takepic"; //��Ƭ���ϱ������ְ������ַ���ʱ����Ӧ������
	public static final String CMD_UPDATE_DEVICES_STR = "updateDevices"; //��Ƭ���ϱ������ְ������ַ���ʱ����Ӧ������
//	private static HashMap<String, Integer> strMapAction = new HashMap<String, Integer>();
//	static {
//		strMapAction.put(CMD_51_CALL_SEND_PIC_STR, CMD_51_CALL_SEND_PIC);
//		strMapAction.put(CMD_CONNECT_BLUETH_STR, CMD_CONNECT_BLUETH);
//		strMapAction.put(CMD_DISCONNECT_BLUETH_STR, CMD_DISCONNECT_BLUETH);
//
//	}
//	public static int getCommandFromStr(String src){
//		Log.d(TAG, "enter Command::getCommandFromStr(src:" + src + ')');
//		if(null == src || src.length() == 0){
//			return CMD_NONE;
//		}
//		Set<Entry<String, Integer>> entrSet =  strMapAction.entrySet();
//		for(Entry<String, Integer>entry: entrSet){
//			if(src.contains(entry.getKey())){
//				Log.d(TAG, "getActionFromStr:" + entry.getKey());
//				return entry.getValue();
//			}
//		}
//		return CMD_NONE;
//	}
	/**
	 * ��������Ľ��н��룬�絥Ƭ��ÿ�δ��ݵĶ�����һ����û�������
	 * @param src
	 * @return
	 */
	public static int getCommandFromStr(String src){
		Log.d(TAG, "enter Command::getCommandFromStr(src:" + src + ')');
		if(null == src || src.length() == 0){
			return CMD_NONE;
		}
		if(src.contains(CMD_51_CALL_SEND_PIC_STR)){
			return CMD_51_CALL_SEND_PIC;
		} else if(CMD_UPDATE_DEVICES_STR.equals(src)){
			return CMD_REQUEST_UPDATE_DEVICES;
		}
		return CMD_NONE;
	}

}
