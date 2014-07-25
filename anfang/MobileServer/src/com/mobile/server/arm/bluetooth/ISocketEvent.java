package com.mobile.server.arm.bluetooth;

import android.bluetooth.BluetoothSocket;

public interface ISocketEvent {
	void onConnecting(BluetoothSocket socket);
	void onConnected(BluetoothSocket socket);  //֪ͨ���÷��Ѿ�������
	void onReceived(BluetoothSocket socket, byte[] buf, int start, int end); //֪ͨ�Ѿ��յ������ݣ�����startΪbuf��㣬endΪ�յ�idx
	void onDisConnect(BluetoothSocket socket); //֪ͨ�Ѿ�����
	
	enum NotifyType{Recevied, DisConnect, Connect};
	
}
