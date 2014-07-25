package com.mobile.server.arm.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.mobile.server.utils.Log;

/**
 * ����ͨ�����������Ѿ���Ե������ӻ���ֱ������OK������OK�����ISocketEvent �ص���ȥ��
 * ���÷�ʽ��
 * 		���ɸö��󼴿ɣ�new Bluetoothclient(new SocketCallBack());
 * 
 * ע��㣺
 * 1. ������������ͨ���ֻ��е����ø㶨�ģ����ʵ�����С��û�ж�����塣
 * 2. һ��ƥ��������ȴ�����ģ�飬�����������ͻ��˶�����ν�������Զ�ƥ���ϡ�
 * 3. ��λ��ͨ��onConnect�ص��е�BluetoothSocket ���й�������ͨ�ţ���ֱ��д���ݡ�
 * 4. ��λ��ͨ��onReceived ��֪���������ϴ��ϵ���Ϣ
 *
 * @author Administrator
 *
 */
@SuppressLint("NewApi")
public class Bluetoothclient {
	public static enum State {WaitStart, Connecting, Connected}; 
	
	private static final String TAG = "bluetoothclient";
	private BluetoothSocket mmSocket = null;
	BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	ISocketEvent eventCallback = null;
	boolean needRun = true;
	ReadThread readThread = null;
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if(null == readThread){
			sb.append(" is not running!");
		} else {
			sb.append(" readThread state:" + readThread.getState());
		}
		
		if(null == mmSocket){
			sb.append("not connect!!");
			return sb.toString();
		} 
		sb.append("isConnected:").append(mmSocket.isConnected());
		if(!mmSocket.isConnected()){
			return sb.toString();
		}
		sb.append("device:").append(mmSocket.getRemoteDevice().getName());
		return sb.toString();
	}
	/**
	 * ��ȡ��ʱ��״̬
	 * 0 ���ӳɹ� 1: δ����  2:������
	 * @return
	 */
	public State getState(){
		//���̶߳�δ����
		if(!readThread.isAlive()){
			return State.WaitStart;
		}
		//���������У�����socketδ���ӳɹ�
		if(null == mmSocket ||!mmSocket.isConnected()){
			return State.Connecting;
		}
		return State.Connected; //connected
	}
	public Bluetoothclient(ISocketEvent eventCallback) {
		this.eventCallback = eventCallback;
		connect();
	}

	public boolean prepare() throws InterruptedException {
		if (!adapter.isEnabled()) {
			Log.d(TAG, "bluetooth is not open, open it!!");
			adapter.enable();
		}
		if (adapter.getBondedDevices().size() == 0) {
			Log.d(TAG, "no devices is bound, just wait");
			return false;
		}
		return true;
	}

	public boolean exit() {
		needRun = false;
		close();
		if(null != readThread){
			readThread.exit();
			readThread = null;
		}
		return true;
	}

	public synchronized void connect() {
		needRun = true;
		if (null != readThread && readThread.isAlive()) {
			return;
		}
		readThread = new ReadThread();
		readThread.start();
	}

	public boolean write(byte[] buf) {
		if (null == mmSocket || !mmSocket.isConnected()) {
			return false;
		}
		try {
			OutputStream out = mmSocket.getOutputStream();
			out.write(buf);
		} catch (IOException e) {
			Log.e(TAG, e.toString(), e);
			return false;
		}
		return true;
	}


	/** Will cancel an in-progress connection, and close the socket */
	private synchronized void close() {
		try {
			eventCallback.onDisConnect(mmSocket);
			if (null != mmSocket && mmSocket.isConnected()) {
				mmSocket.close();
				mmSocket = null;
			}
		} catch (IOException e) {
		}
	}
	
	class ReadThread extends Thread {
		boolean isRun = true;

		public void exit() {
			isRun = false;
			interrupt();
		}

		@Override
		public void run() {
			Log.i(TAG, "begin to connect To srv");
			InputStream in = null;
			while (isRun && !isInterrupted()) {
				try {
					sleep(500);
					if (!prepare()) {
						continue;
					}

					// Use a temporary object that is later assigned to
					// mmSocket,
					// because mmSocket is final
					mmSocket = connectToServer();
					if(null == mmSocket){
						Log.i(TAG, "call connectToServer return null");
						continue;
					}

					deposeSocket(mmSocket);
				} catch (InterruptedException e) {
					Log.e(TAG, e.toString(), e);
				} finally {
					close();
					if (null != in) {
						try {
							in.close();
						} catch (Exception e) {
						}
					}
				}
			}
			Log.i(TAG, "exit bluetootch Client read Thread");
		}
		
		private BluetoothSocket connectToServer(){
			// Use a temporary object that is later assigned to
			// mmSocket,
			// because mmSocket is final
			BluetoothSocket connectSocket = null;
			BluetoothDevice device = adapter.getBondedDevices().toArray(new BluetoothDevice[] {})[0];
			try {
				Log.d(TAG, "begin to creat socket from " + device.getName());
				eventCallback.onConnecting(connectSocket);
				// MY_UUID is the app's UUID string, also used by the
				// server code
				connectSocket = device.createRfcommSocketToServiceRecord(UUID
						.fromString("00001101-0000-1000-8000-00805F9B34FB"));
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				connectSocket.connect();
				
				mmSocket = connectSocket;
				if(null != mmSocket && mmSocket.getRemoteDevice() != null){
					Log.i(TAG, "connect to " + mmSocket.getRemoteDevice().getName() + " success");
				} else {
					Log.i(TAG, "connect to server success!!");
				}
				eventCallback.onConnected(mmSocket);
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				return null;
			}
			return connectSocket;
		}
		
		private boolean deposeSocket(BluetoothSocket socket){
			InputStream in;
			try {
				in = socket.getInputStream();
				byte[] buf = new byte[1024];
				int iRead = -1;
				while (-1 != (iRead = in.read(buf))) {
					Log.i(TAG, "received: " + new String(buf, 0, iRead));
					eventCallback.onReceived(socket, buf, 0, iRead);
				}
			} catch (IOException e) {
				Log.e(TAG, e.toString(), e);
			}
			return false;
		}
	}
}